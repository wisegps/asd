package com.wise.bluetoothUtil;

import com.wise.asd.R;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * 蓝牙模块服务器端主控制Service
 * @author GuoDong
 *
 */
public class BluetoothServerService extends Service {
	private final String TAG = "BluetoothServerService";
	private final String TYPE1 = "AT+TYPE1";
	//蓝牙通讯线程
	private BluetoothCommunThread communThread;
	boolean isBlueToothOn = true; //蓝牙是否打开
	boolean isConn = false;   //是否连接
	boolean isOnCreate = true; //是否是第一次启动
	//控制信息广播接收器
	private BroadcastReceiver controlReceiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();			
			if (BluetoothTools.ACTION_STOP_SERVICE.equals(action)) {
				//停止后台服务
				if (communThread != null) {
					communThread.isRun = false;
				}
				stopSelf();				
			} else if (BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)) {
				//发送数据
				String data = intent.getStringExtra("value");
				if (communThread != null) {
					communThread.writeObject(data);
				}				
			}else if(BluetoothTools.ACTION_CHANGE.equals(action)){//蓝牙状态切换
				 String stateExtra = BluetoothAdapter.EXTRA_STATE;
				 int state = intent.getIntExtra(stateExtra, -1);
				 switch(state) {
				    case BluetoothAdapter.STATE_ON://蓝牙打开
				    	isBlueToothOn = true;
				    	//打开蓝牙，开启后台连接线程
				    	Intent openIntent = new Intent(BluetoothTools.ACTION_GET_DATA);
				    	openIntent.putExtra("data", getString(R.string.blue_open));
						sendBroadcast(openIntent);
						new BluetoothServerConnThread(serviceHandler).start();	
				        break;
				    case BluetoothAdapter.STATE_TURNING_OFF: //蓝牙正在关闭
				    	isBlueToothOn = false;
				        break;
				    case BluetoothAdapter.STATE_OFF://蓝牙关闭
				    	Intent closeIntent = new Intent(BluetoothTools.ACTION_GET_DATA);
				    	closeIntent.putExtra("data", getString(R.string.blue_closed));
						sendBroadcast(closeIntent);
						if (communThread != null) {
							communThread.isRun = false;
						}
				        break;
				 }
			}
		}
	};
	
	//接收其他线程消息的Handler
	private Handler serviceHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {			
			switch (msg.what) {
			case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
				//连接成功,开启通讯线程
				communThread = new BluetoothCommunThread(serviceHandler, (BluetoothSocket)msg.obj);
				communThread.start();
				isConn = true;
				//发送连接成功消息
				Intent connSuccIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
				sendBroadcast(connSuccIntent);
				//连接成功设置工作模式
				if (communThread != null) {
					communThread.writeObject(TYPE1);
				}
				break;				
			case BluetoothTools.MESSAGE_CONNECT_ERROR:
				//连接错误,发送连接错误广播
				Intent errorIntent = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
				sendBroadcast(errorIntent);
				Log.d(TAG, "连接错误");
				isConn = false;
				//重新开始启动监听,开启后台连接线程(判断蓝牙是否在关闭)
				if(isBlueToothOn){//蓝牙没有关闭，说明是距离过远断开连接，重新开始侦听
					new BluetoothServerConnThread(serviceHandler).start();
					Intent openIntent = new Intent(BluetoothTools.ACTION_GET_DATA);
					openIntent.putExtra("data", getString(R.string.blue_connect_borken));
					sendBroadcast(openIntent);
				}
				break;				
			case BluetoothTools.MESSAGE_READ_OBJECT://读取模块返回数据
				byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);
				//读取到数据
                Intent getIntent = new Intent(BluetoothTools.ACTION_GET_DATA);
                getIntent.putExtra("data", readMessage);
				sendBroadcast(getIntent);
				break;
			case BluetoothTools.MESSAGE_OPENSOCKET://socket打开异常
				Log.d(TAG, "socket打开异常");
				break;
			}			
			super.handleMessage(msg);
		}		
	};
	
	@Override
	public void onCreate() {
		isOnCreate = true;
		Log.d(TAG, "开启service");
		//注册BroadcastReceiver
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(BluetoothTools.ACTION_START_SERVER);
		controlFilter.addAction(BluetoothTools.ACTION_STOP_SERVICE);
		controlFilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);		
		controlFilter.addAction(BluetoothTools.ACTION_CHANGE);
		registerReceiver(controlReceiver, controlFilter);
		//开启后台连接线程
		new BluetoothServerConnThread(serviceHandler).start();		
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "service start");
		if(!isOnCreate){
			if(isConn){ //连接
				Log.d(TAG, "isConn = true");
				Intent connIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
				sendBroadcast(connIntent);
				//连接成功设置工作模式
				if (communThread != null) {
					communThread.writeObject(TYPE1);
				}
			}else{
				Log.d(TAG, "isConn = false");
				Intent getIntent = new Intent(BluetoothTools.ACTION_GET_DATA);
	            getIntent.putExtra("data", getString(R.string.Connection_broken));
				sendBroadcast(getIntent);
			}
		}
		isOnCreate = false;
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		System.out.println("service destroy");
		try {
			BluetoothAdapter.getDefaultAdapter().disable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (communThread != null) {
			communThread.isRun = false;
		}
		unregisterReceiver(controlReceiver);
		stopSelf();
		super.onDestroy();
	}	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}