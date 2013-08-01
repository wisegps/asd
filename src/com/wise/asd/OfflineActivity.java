package com.wise.asd;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wise.Data.CallPhoneAdapter;
import com.wise.Data.OfflineData;
import com.wise.Data.PhoneData;
import com.wise.asd.R;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class OfflineActivity extends Activity{
	private static final String TAG = "OfflineActivity";
	private static final String cmd_open = "KS";
	private static final String cmd_lock = "GS";
	private static final String cmd_start = "QD";
	private static final String cmd_stop = "XH";
	Spinner s_car;
	ImageView iv_button_start;
	Button iv_open,iv_lock,iv_un_contor,bt_control_call,bt_contor_location;
	TextView tv_refresh_control;
	SmsContent content;
	List<OfflineData> list;
	OfflineData offlineData;
	List<PhoneData> phoneDatas;
	SendMessageBroadCastReceiver sendMessageBroadCastReceiver;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListenner();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control);
		init();
		
		mLocationClient = new LocationClient(this); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������
		setLocationOption(30000);
		mLocationClient.start();
	}
	private void init(){
		s_car = (Spinner)findViewById(R.id.s_car);
		iv_button_start = (ImageView)findViewById(R.id.iv_button_start);
		iv_button_start.setOnClickListener(onClickListener);		
		iv_un_contor = (Button)findViewById(R.id.iv_un_contor);	
		iv_open = (Button)findViewById(R.id.iv_open);
		iv_open.setOnClickListener(onClickListener);
		iv_lock = (Button)findViewById(R.id.iv_lock);
		iv_lock.setOnClickListener(onClickListener);
		bt_control_call = (Button)findViewById(R.id.bt_control_call);
		bt_control_call.setOnClickListener(onClickListener);
		bt_contor_location = (Button)findViewById(R.id.bt_contor_location);
		bt_contor_location.setOnClickListener(onClickListener);
		tv_refresh_control = (TextView)findViewById(R.id.tv_refresh_control);
		tv_refresh_control.setVisibility(View.GONE);
		GetUserLocation();
		bindSpinner();
		
		content = new SmsContent(new Handler());
        // ע����ű仯����
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);
        sendMessageBroadCastReceiver = new SendMessageBroadCastReceiver();        
        this.registerReceiver(sendMessageBroadCastReceiver, new IntentFilter("SENT_SMS_ACTION"));
	}
	/**
	 * ��ȡ��������
	 */
	private void GetUserLocation(){
		list = new ArrayList<OfflineData>();
		DBHelper dbHelper = new DBHelper(OfflineActivity.this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("wise_unicom_zwc", null, null, null, null, null,null);
		while (cursor.moveToNext()) {
			String obj_name = cursor.getString(cursor.getColumnIndex("obj_name"));
			String sim = cursor.getString(cursor.getColumnIndex("sim"));
			String accessory = cursor.getString(cursor.getColumnIndex("accessory"));
			String is_sound = cursor.getString(cursor.getColumnIndex("is_sound"));
			String is_start = cursor.getString(cursor.getColumnIndex("is_start"));
			String is_lockdoor = cursor.getString(cursor.getColumnIndex("is_lockdoor"));
			String phone = cursor.getString(cursor.getColumnIndex("phone"));
			Log.d(TAG, obj_name + "," + accessory + "," +is_sound + "," +is_start + "," +is_lockdoor + "," +phone);
			OfflineData offlineData = new OfflineData();
			offlineData.setSerial(cursor.getString(cursor.getColumnIndex("serial")));
			offlineData.setPhone(phone);
			offlineData.setObj_name(obj_name);
			offlineData.setSim(sim);
			offlineData.setAccessory(accessory);
			offlineData.setIs_start(is_start);
			if(is_sound == null || is_sound.equals("true")){
				offlineData.is_sound = true;
			}else{
				offlineData.is_sound = false;
			}
			if(is_lockdoor == null || is_lockdoor.equals("true")){
				offlineData.is_lockdoor = true;
			}else{
				offlineData.is_lockdoor = false;
				iv_open.setBackgroundResource(R.drawable.button_lock_press);
			}
			list.add(offlineData);
		}
		cursor.close();
		db.close();
		if(list.size()>1){
			s_car.setVisibility(View.VISIBLE);
		}else{
			s_car.setVisibility(View.INVISIBLE);
		}
	}
	List<String> nameList;
	private void bindSpinner(){
		nameList = new ArrayList<String>();
		for(int i = 0 ; i < list.size(); i++){
			nameList.add(list.get(i).getObj_name());
		}
		//����ҳ��ķ��
		ArrayAdapter<String> carAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, nameList);
		carAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // ���������б�ķ��			
		s_car.setAdapter(carAdapter);
		s_car.setOnItemSelectedListener(onItemSelectedListener);
	}
	int index;
	OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			index = arg2;
			offlineData = list.get(arg2);
			if(offlineData.is_sound){
				//��ʾͼ��Ϊ����
				iv_un_contor.setBackgroundResource(R.drawable.iv_un_contor);
				iv_un_contor.setText(R.string.SOUND);
			}else{
				//��ʾͼ��Ϊ����
				iv_un_contor.setBackgroundResource(R.drawable.iv_sound_false);
				iv_un_contor.setText(R.string.SOUND_false);
			}
			if(offlineData.is_lockdoor){
				iv_lock.setBackgroundResource(R.drawable.button_open_press);
				iv_lock.setTextColor(Color.RED);
				iv_open.setBackgroundResource(R.drawable.iv_lock);
				iv_open.setTextColor(Color.BLACK);
			}else{
				iv_open.setBackgroundResource(R.drawable.button_lock_press);
				iv_open.setTextColor(Color.RED);
				iv_lock.setBackgroundResource(R.drawable.iv_open);
				iv_lock.setTextColor(Color.BLACK);
			}
			if(offlineData.getIs_start() == null || offlineData.getIs_start().equals("0")){
				iv_button_start.setBackgroundResource(R.drawable.iv_button_start);
			}else{
				iv_button_start.setBackgroundResource(R.drawable.iv_stop);
			}
			phoneDatas = new ArrayList<PhoneData>();
			try {
				JSONArray jsonArray = new JSONArray(offlineData.getPhone());
				for(int i = 0 ; i < jsonArray.length() ; i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					PhoneData phoneData = new PhoneData();
					phoneData.setName(jsonObject.getString("name"));
					phoneData.setPhone(jsonObject.getString("phone"));
					phoneDatas.add(phoneData);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		public void onNothingSelected(AdapterView<?> arg0) {}
	};
	OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_open:
				if(offlineData != null){
					if(offlineData.is_lockdoor){
						sendMessage(cmd_open);
					}
				}				
				break;
			case R.id.iv_lock:
				if(offlineData != null){
					if(!offlineData.is_lockdoor){
						sendMessage(cmd_lock);
					}
				}				
				break;
			case R.id.iv_button_start:
				if(offlineData != null){
					if(offlineData.getIs_start().equals("0")){
						sendMessage(cmd_start);
					}else{
						sendMessage(cmd_stop);
					}
				}
				break;
			case R.id.bt_control_call:
				//�ж��Ƿ�������һ�����к���
				if(phoneDatas != null && phoneDatas.size()>0){					
					//���ͳ���λ�õ���Ӧ���ֻ�
					View view_Phone = LayoutInflater.from(OfflineActivity.this).inflate(R.layout.phone_list, null);
					final ListView lv_phone = (ListView)view_Phone.findViewById(R.id.lv_call);
					CallPhoneAdapter callPhoneAdapter = new CallPhoneAdapter(getApplicationContext(), phoneDatas);
					lv_phone.setAdapter(callPhoneAdapter);
					lv_phone.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
							//��ת���绰ҳ��
							String phone = "tel:" + phoneDatas.get(arg2).getPhone();
							Uri uri = Uri.parse(phone);   
							Intent intent = new Intent(Intent.ACTION_DIAL, uri);     
							startActivity(intent);  
						}
					});
					AlertDialog.Builder addPhoneBuilder = new AlertDialog.Builder(OfflineActivity.this);
					addPhoneBuilder.setTitle(R.string.choose_phone);
					addPhoneBuilder.setView(view_Phone);
					addPhoneBuilder.setNegativeButton(R.string.cancle, null);
					addPhoneBuilder.show();
				}else{
					Toast.makeText(getApplicationContext(), R.string.no_call_phone, Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.bt_contor_location:
				if(lat == null){
					Toast.makeText(OfflineActivity.this, R.string.no_car_location, Toast.LENGTH_SHORT).show();
				}else{
					//���ͳ���λ�õ���Ӧ���ֻ�
					View view_Phone = LayoutInflater.from(OfflineActivity.this).inflate(R.layout.location_phone, null);
					final EditText et_location_phone = (EditText)view_Phone.findViewById(R.id.et_location_phone);
					AlertDialog.Builder addPhoneBuilder = new AlertDialog.Builder(OfflineActivity.this);
					addPhoneBuilder.setTitle(R.string.enter_phone);
					addPhoneBuilder.setView(view_Phone);
					addPhoneBuilder.setPositiveButton(R.string.Sure, new DialogInterface.OnClickListener() {					
						public void onClick(DialogInterface dialog, int which) {
							String phone = et_location_phone.getText().toString();
							String url = "http://api.map.baidu.com/geocoder?location=" + lat + "," + lon + "&coord_type=wgs84&output=html";
							if(phone.equals("")){
								Toast.makeText(OfflineActivity.this, R.string.not_complete_not, Toast.LENGTH_SHORT).show();
							}else{
								SmsManager sms = SmsManager.getDefault();
								List<String> texts = sms.divideMessage(url);
								for(String text : texts){
									sms.sendTextMessage(phone, null, text, null, null);
								}
								Toast.makeText(OfflineActivity.this, R.string.send_sms, Toast.LENGTH_SHORT).show();
							}
						}
					});
					addPhoneBuilder.setNegativeButton(R.string.cancle, null);
					addPhoneBuilder.show();
				}
				break;
			}
		}
	};
	/**
	 * ���Ͷ���
	 */
	private void sendMessage(String cmd){
		Log.d(TAG, offlineData.getSim() + "," + cmd);
		SmsManager sms = SmsManager.getDefault();
		Intent sendIntent = new Intent("SENT_SMS_ACTION");
		PendingIntent sendPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, sendIntent, 0);
		sms.sendTextMessage(offlineData.getSim(), null, cmd, sendPendingIntent, null);
	}
	
	class SendMessageBroadCastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive=" + intent.getAction());
			if(intent.getAction().equals("SENT_SMS_ACTION")){
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(OfflineActivity.this, R.string.send_sms, Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(OfflineActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}		
	}
	
	/**
	 * ����ʱ�����µĶ���
	 * @author honesty
	 *
	 */
	class SmsContent extends ContentObserver {
        public SmsContent(Handler handler) {
            super(handler);
        } 
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // ��ȡ�ռ�����ָ������Ķ���
            try {
            	Log.d(TAG, "����ı�");
            	//Cursor cursor = managedQuery(Uri.parse("content://sms/inbox"),new String[] { "_id", "address", "body" }," address=? and read=?",new String[] { "10010", "0" }, "date desc");
            	Cursor cursor = managedQuery(Uri.parse("content://sms/inbox"),new String[] { "_id", "address", "body" },"read=?",new String[] {"0" }, "date desc");
            	if(cursor != null){//nameList
            		Log.d(TAG, "������");
            		cursor.moveToFirst();
            		for (int i = 0; i < nameList.size(); i++) {
            			Log.d(TAG,list.get(i).getSim() + "-" + cursor.getString(1));
						if(list.get(i).getSim().equals(cursor.getString(1))){//��Ҫ���صĺ���
							Log.d(TAG, "�绰="+ list.get(i).getSerial());
							String body = cursor.getString(2);
		                    Toast.makeText(OfflineActivity.this, body, Toast.LENGTH_SHORT).show();
		                    String UpdateDB = "";	
		                    System.out.println("body="+body);
		                    if(body.indexOf("�����ɹ�") >= 0){
		                    	Log.d(TAG, "�����ɹ�");
		                    	list.get(i).setIs_lockdoor(true);
		                    	UpdateDB = "update wise_unicom_zwc set is_lockdoor = 'true' where  serial =" + list.get(i).getSerial();
		                    	if(index == i){ //��Ҫ�޸�ҳ��״̬
		                    		iv_lock.setBackgroundResource(R.drawable.button_open_press);
		            				iv_lock.setTextColor(Color.RED);
		            				iv_open.setBackgroundResource(R.drawable.iv_lock);
		            				iv_open.setTextColor(Color.BLACK);
			                    }
		                    }else if(body.indexOf("�����ɹ�") >= 0){
		                    	Log.d(TAG, "�����ɹ�");
		                    	list.get(i).setIs_lockdoor(false);
		                    	UpdateDB = "update wise_unicom_zwc set is_lockdoor = 'false' where serial =" + list.get(i).getSerial();
		                    	if(index == i){ //��Ҫ�޸�ҳ��״̬
		                    		iv_open.setBackgroundResource(R.drawable.button_lock_press);
		            				iv_open.setTextColor(Color.RED);
		            				iv_lock.setBackgroundResource(R.drawable.iv_open);
		            				iv_lock.setTextColor(Color.BLACK);
			                    }
		                    }else if(body.indexOf("�����ɹ�") >= 0){
		                    	list.get(i).setIs_start("0");
		                    	UpdateDB = "update wise_unicom_zwc set is_start = '0' where serial =" + list.get(i).getSerial();
		                    	if(index == i){ //��Ҫ�޸�ҳ��״̬
		                    		iv_button_start.setBackgroundResource(R.drawable.iv_button_start);
			                    }
		                    }else if(body.indexOf("Ϩ��ɹ�") >= 0){
		                    	list.get(i).setIs_start("2");
		                    	UpdateDB = "update wise_unicom_zwc set is_start = '2' where serial =" + list.get(i).getSerial();
		                    	if(index == i){ //��Ҫ�޸�ҳ��״̬
		                    		iv_button_start.setBackgroundResource(R.drawable.iv_stop);
			                    }
		                    }
		                    if(!UpdateDB.equals("")){ //�������ݿ�
		                    	DBExcute dbExcute = new DBExcute();
			        			dbExcute.UpdateDB(OfflineActivity.this, UpdateDB);
		                    }
							break;
						}
					}
            	}
			} catch (Exception e) {
				e.printStackTrace();
			}                                          
        }
    }
	
	private void setLocationOption(int spnTime) {
		if (mLocationClient == null)
			return;
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("detail");
		option.setCoorType("gcj02");
		option.setScanSpan(spnTime);
		mLocationClient.setLocOption(option);
	}
	String lon,lat;
	class MyLocationListenner implements BDLocationListener{
		public void onReceiveLocation(BDLocation location) {
			if(location.getLocType() == 61){
				lat = ""+ location.getLatitude();
				lon = ""+ location.getLongitude();	
			}					
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			System.out.println(sb.toString());		
		}
		public void onReceivePoi(BDLocation arg0) {}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(sendMessageBroadCastReceiver);
		mLocationClient.stop();
		this.getContentResolver().unregisterContentObserver(content);
	}
}
