package com.wise.asd;

import java.util.ArrayList;
import java.util.List;
import com.wise.Data.BluetoothData;
import com.wise.Data.BluetoothPhoneAdapter;
import com.wise.bluetoothUtil.BluetoothServerService;
import com.wise.bluetoothUtil.BluetoothTools;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends Activity{
	private static String TAG = "BluetoothActivity";
	private final String TYPE2 = "AT+TYPE2";
	private final String GET_PHONE_MARK = "OK+ADDX:";
	
	private final int NUMBERPHONE = 3;
	
	TextView serverStateTextView;
	EditText et_cmd;
	Button bt_send,bt_bluetooth_back,bt_add_bluetooth;
	ListView lv_bluetooth;
	
	//蓝牙适配器
	private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();	
	List<BluetoothData> bluetoothDatas;
	BluetoothPhoneAdapter bluetoothPhoneAdapter;
	String BluetoothAdress = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	OnClickListener onClickListener = new OnClickListener() {		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_send:
				String cmd = et_cmd.getText().toString();
				Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
				sendDataIntent.putExtra("value", cmd);
				sendBroadcast(sendDataIntent);
				break;
			case R.id.bt_bluetooth_back:
				finish();
				break;
			case R.id.bt_add_bluetooth:
				Intent sendAddDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
				sendAddDataIntent.putExtra("value", TYPE2);
				sendBroadcast(sendAddDataIntent);
				Intent startService = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
				sendBroadcast(startService);
				Toast.makeText(getApplicationContext(), R.string.p_m, Toast.LENGTH_LONG).show();
				try {
					bluetoothAdapter.disable();
				} catch (Exception e) {
					e.printStackTrace();
				}				
				break;
			}		
		}
	};
	int number = 0;
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();			
			if (BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action)) {//连接成功	
				BluetoothAdress = bluetoothAdapter.getAddress().replace(":", "");
				Log.d(TAG, "连接成功");
				serverStateTextView.setText(R.string.connection_seccess);
				bluetoothDatas = new ArrayList<BluetoothData>();
			} else if(BluetoothTools.ACTION_CONNECT_ERROR.equals(action)){
				serverStateTextView.setText(R.string.Connection_broken);
				Log.d(TAG, "连接断开");
			} else if(BluetoothTools.ACTION_GET_DATA.equals(action)){
				String data  = intent.getStringExtra("data");
				Log.d(TAG, data);
				serverStateTextView.setText(data);
				
				number++;
				if(number == NUMBERPHONE + 1){//数据查询完毕，绑定listview
					Log.d(TAG, "NUMBERPHONE="+NUMBERPHONE);
					bluetoothPhoneAdapter = new BluetoothPhoneAdapter(BluetoothActivity.this, bluetoothDatas);
					lv_bluetooth.setAdapter(bluetoothPhoneAdapter);
				}
				if(number <= NUMBERPHONE + 1){
					Log.d(TAG, "number="+number);
					if(data.indexOf(GET_PHONE_MARK)>=0){
						String address = data.substring(8, data.length());
						if(address.equals("000000000000") || address.endsWith(BluetoothAdress)){
							Log.d(TAG, "不显示的地址：" + address);
						}else{
							BluetoothData bluetoothData = new BluetoothData();
							bluetoothData.id = number - 1;
							bluetoothData.setAddress(address);
							bluetoothDatas.add(bluetoothData);
						}
					}
				}
				//连接设置工作模式后，查询模块内的3个蓝牙地址
				if(number <= NUMBERPHONE){	
					Intent GetAddIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
					GetAddIntent.putExtra("value", "AT+ADD" + number + "?");
					sendBroadcast(GetAddIntent);
				}			
			}
		}		
	};
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,long arg3) {
			new AlertDialog.Builder(BluetoothActivity.this)
			.setTitle(R.string.Note)
			.setMessage(R.string.sure_delete)
			.setNegativeButton(R.string.cancle, null)
			.setPositiveButton(R.string.Sure,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					String cmd = "AT+ADD" + bluetoothDatas.get(arg2).getId() + "0";
					Intent DeleteDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
					DeleteDataIntent.putExtra("value", cmd);
					sendBroadcast(DeleteDataIntent);
					bluetoothDatas.remove(arg2);
					bluetoothPhoneAdapter.notifyDataSetChanged();
				}
			}).show();
		}
	};
	
	private void init(){
		setContentView(R.layout.bluetooth);
		Button bt_add_bluetooth = (Button)findViewById(R.id.bt_add_bluetooth);
		bt_add_bluetooth.setOnClickListener(onClickListener);
		bt_send = (Button)findViewById(R.id.bt_send);
		bt_send.setOnClickListener(onClickListener);
		bt_bluetooth_back = (Button)findViewById(R.id.bt_bluetooth_back);
		bt_bluetooth_back.setOnClickListener(onClickListener);
		bt_add_bluetooth = (Button)findViewById(R.id.bt_add_bluetooth);
		bt_add_bluetooth.setOnClickListener(onClickListener);
		serverStateTextView = (TextView)findViewById(R.id.serverStateText);
		et_cmd = (EditText)findViewById(R.id.et_cmd);
		lv_bluetooth = (ListView)findViewById(R.id.lv_bluetooth);
		lv_bluetooth.setOnItemClickListener(onItemClickListener);
		serverStateTextView.setText(R.string.wait_connection);
		if(!bluetoothAdapter.isEnabled()){
			bluetoothAdapter.enable();
		}		
	}
	@Override
	protected void onStart() {
		super.onStart();
		//开启后台service
		Intent startService = new Intent(BluetoothActivity.this, BluetoothServerService.class);
		startService(startService);		
		//注册BoradcasrReceiver
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_ERROR);		
		intentFilter.addAction(BluetoothTools.ACTION_GET_DATA);				
		registerReceiver(broadcastReceiver, intentFilter);
		//判断连接断开情况
	}
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(broadcastReceiver);
	}
}