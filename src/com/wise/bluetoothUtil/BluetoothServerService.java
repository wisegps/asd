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
 * ����ģ���������������Service
 * @author GuoDong
 *
 */
public class BluetoothServerService extends Service {
	private final String TAG = "BluetoothServerService";
	private final String TYPE1 = "AT+TYPE1";
	//����ͨѶ�߳�
	private BluetoothCommunThread communThread;
	boolean isBlueToothOn = true; //�����Ƿ��
	boolean isConn = false;   //�Ƿ�����
	boolean isOnCreate = true; //�Ƿ��ǵ�һ������
	//������Ϣ�㲥������
	private BroadcastReceiver controlReceiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();			
			if (BluetoothTools.ACTION_STOP_SERVICE.equals(action)) {
				//ֹͣ��̨����
				if (communThread != null) {
					communThread.isRun = false;
				}
				stopSelf();				
			} else if (BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)) {
				//��������
				String data = intent.getStringExtra("value");
				if (communThread != null) {
					communThread.writeObject(data);
				}				
			}else if(BluetoothTools.ACTION_CHANGE.equals(action)){//����״̬�л�
				 String stateExtra = BluetoothAdapter.EXTRA_STATE;
				 int state = intent.getIntExtra(stateExtra, -1);
				 switch(state) {
				    case BluetoothAdapter.STATE_ON://������
				    	isBlueToothOn = true;
				    	//��������������̨�����߳�
				    	Intent openIntent = new Intent(BluetoothTools.ACTION_GET_DATA);
				    	openIntent.putExtra("data", getString(R.string.blue_open));
						sendBroadcast(openIntent);
						new BluetoothServerConnThread(serviceHandler).start();	
				        break;
				    case BluetoothAdapter.STATE_TURNING_OFF: //�������ڹر�
				    	isBlueToothOn = false;
				        break;
				    case BluetoothAdapter.STATE_OFF://�����ر�
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
	
	//���������߳���Ϣ��Handler
	private Handler serviceHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {			
			switch (msg.what) {
			case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
				//���ӳɹ�,����ͨѶ�߳�
				communThread = new BluetoothCommunThread(serviceHandler, (BluetoothSocket)msg.obj);
				communThread.start();
				isConn = true;
				//�������ӳɹ���Ϣ
				Intent connSuccIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
				sendBroadcast(connSuccIntent);
				//���ӳɹ����ù���ģʽ
				if (communThread != null) {
					communThread.writeObject(TYPE1);
				}
				break;				
			case BluetoothTools.MESSAGE_CONNECT_ERROR:
				//���Ӵ���,�������Ӵ���㲥
				Intent errorIntent = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
				sendBroadcast(errorIntent);
				Log.d(TAG, "���Ӵ���");
				isConn = false;
				//���¿�ʼ��������,������̨�����߳�(�ж������Ƿ��ڹر�)
				if(isBlueToothOn){//����û�йرգ�˵���Ǿ����Զ�Ͽ����ӣ����¿�ʼ����
					new BluetoothServerConnThread(serviceHandler).start();
					Intent openIntent = new Intent(BluetoothTools.ACTION_GET_DATA);
					openIntent.putExtra("data", getString(R.string.blue_connect_borken));
					sendBroadcast(openIntent);
				}
				break;				
			case BluetoothTools.MESSAGE_READ_OBJECT://��ȡģ�鷵������
				byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);
				//��ȡ������
                Intent getIntent = new Intent(BluetoothTools.ACTION_GET_DATA);
                getIntent.putExtra("data", readMessage);
				sendBroadcast(getIntent);
				break;
			case BluetoothTools.MESSAGE_OPENSOCKET://socket���쳣
				Log.d(TAG, "socket���쳣");
				break;
			}			
			super.handleMessage(msg);
		}		
	};
	
	@Override
	public void onCreate() {
		isOnCreate = true;
		Log.d(TAG, "����service");
		//ע��BroadcastReceiver
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(BluetoothTools.ACTION_START_SERVER);
		controlFilter.addAction(BluetoothTools.ACTION_STOP_SERVICE);
		controlFilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);		
		controlFilter.addAction(BluetoothTools.ACTION_CHANGE);
		registerReceiver(controlReceiver, controlFilter);
		//������̨�����߳�
		new BluetoothServerConnThread(serviceHandler).start();		
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "service start");
		if(!isOnCreate){
			if(isConn){ //����
				Log.d(TAG, "isConn = true");
				Intent connIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
				sendBroadcast(connIntent);
				//���ӳɹ����ù���ģʽ
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