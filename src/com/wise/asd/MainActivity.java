package com.wise.asd;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import com.wise.BaseClass.GetSystem;
import com.wise.BaseClass.NetThread;
import com.wise.Data.CallPhoneAdapter;
import com.wise.Data.CarData;
import com.wise.Data.CarItemizedOverlay;
import com.wise.Data.PhoneData;
import com.wise.Parameter.Config;
import com.wise.bluetoothUtil.BluetoothServerService;
import com.wise.sql.DBExcute;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
/**
 * ������
 * @author honesty
 */
public class MainActivity extends MapActivity{
	private static String TAG = "MainActivity";
	
	private final String CMD_STOPENGINE = "16421";    //Ϩ��
	private final String CMD_STARTENGINE = "16406";  //����
	private final String CMD_UNLOCKDOOR = "16417";  //����
	private final String CMD_DISARMING = "16394";  //����
	private final String CMD_LOCKDOOR = "16416";  //����
	private final String CMD_ARMING = "16393";   //���
	private final String CMD_FINDVEHICLE = "16420"; //Ѱ��
	private final String CMD_SLIENT = "16408";     	//����
	private final String CMD_SOUND = "16409";     	//����
	private final String CMD_P20STATUS = "16425";	//��ȡP20״̬
	private final String CMD_OPENTRAIL = "16436"; 	//��β��
	private static final String cmd_open = "KS";
	private static final String cmd_lock = "GS";
	private static final String cmd_start = "QD";
	private static final String cmd_stop = "XH";
	
	private final int GET_CARINFO = 1;	 //��ȡ�û������г����б�
	private final int REFRESH_CARINFO = 2;	//�õ���ǰ��������
	private final int GET_MESSAGE_COUNT = 3;  //��ȡδ����Ϣ��Ŀ
	private final int SEND_CMD = 4;  //����ָ��
	private final int GET_PARMS = 5;  //��ȡ��Ӧ����������״̬
	private final int GET_BDLOCATION = 6; //��ȡ��ƫ��İٶȵ�ַ
	private final int SEND_OPEN = 7;   //����
	private final int SEND_CLOSE = 9;   //�� ��
	private final int SEND_START = 10;   //����,Ϩ��
	private final int SEND_SOUND = 11;   //����,����
	private final int SEND_SOUND_OK = 12;   //����,����״̬���³ɹ�
	private final int SEND_START_OK = 13;   //����,Ϩ��״̬���³ɹ�
	private final int GET_AUTH_CODE = 15; //��ȡҵ�������code
	private final int OPEN_BOX = 16;		//�򿪺���
	
    int notification_id=19172439;
    NotificationManager nm ;
	
	ViewFlipper flipper;
	LinearLayout rl_car_top;
	RelativeLayout rl_pref_sms,rl_pref_terminal,rl_perf_person,rl_perf_about,rl_perf_feedback;
	Spinner s_carid,s_car;
	Button bt_ZoomDown,bt_ZoomUp,bt_contor_location,bt_contor_Diagnosis,iv_open,bt_control_call,iv_lock,iv_contor,iv_un_contor;
	ImageView iv_aTraffic,iv_map,iv_control,iv_pref,iv_Me,iv_button_start,iv_UnReadMsg,iv_perfrenece_UnReadMsg,iv_car;
	TextView tv_control_isOnLine;
	ProgressDialog Dialog = null;    //progress
	View popView;     // ���ݴ���
	boolean IsTraffic = false; //ʵʱ��ͨ
	int index = 0;    //��ǰ��λ���Ǹ���
	boolean isGetData = true;
	String address;
	boolean is_lockdoor = false;  //true����,false����
	boolean is_sound = false;    //true�Ƿ��ľ���ָ�false����������
	boolean is_start_now = false; //startָ���Ƿ���,�ǲ��ܵ��
	boolean is_lockdoor_arming = true; //true�Ƿ��Ľ���ָ�false�����ǳ���ָ��
	boolean is_lock = true ;    //true�Ƿ�������ָ�false���������ָ��
	String is_start = "0";	//0:Ϩ��1:������2:Զ������
	String accessory = "0";
	int PicSize = 16;
    boolean isAppPause = false; //������ͣ
    boolean isControl = true; //���ƽ��棬60s����ָ��
    boolean isBlueServer = false;
    SendMessageBroadCastReceiver sendMessageBroadCastReceiver;
	CarData carData;    
    List<PhoneData> phoneDatas;
    String sim;

	BMapManager mBMapMan = null;
	MapView mMapView;
	MapController mMapController;
	private List<Overlay> mapOverLays; // ͼ���б�
	GeoPoint myLocation;  //��ǰ����
	MyOverlay myOverlay;  //��ǰλ��	
	String lat = null;	//һ����λlat
	String lon = null;	//һ����λlon
	CarItemizedOverlay itemOverLay;
	MKSearch mMKSearch;
	LocationListener mLocationListener = null;//onResumeʱע���listener��onPauseʱ��ҪRemove	

	List<String> list = new ArrayList<String>();
	ArrayAdapter<String> carAdapter;
	ArrayAdapter<String> carAdapter1;
	SmsContent content;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    
    OnClickListener onClickListener = new OnClickListener() {		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_Me: //��ǰλ��
				try {
					if(myLocation != null){
						mMapController.animateTo(myLocation);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}				
				break;	
			case R.id.iv_car: //��ǰ����λ��
				try {
					GeoPoint point = new GeoPoint(GetSystem.StringToInt(carData.getRev_lat()), GetSystem.StringToInt(carData.getRev_lon()));
					mMapController.animateTo(point); 
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.bt_ZoomDown:  //��С
				mMapController.zoomOut();
				break;			
			case R.id.bt_ZoomUp:  //�Ŵ�
				mMapController.zoomIn();
				break;
			case R.id.iv_aTraffic: //ʵʱ��ͨ����ͼ�л�
				ChangeTraffic();
				break;			
			case R.id.iv_button_start://������Ϩ��
				onClickStart();
				break;			
			case R.id.iv_open://����
				onClickOpen();
				break;			
			case R.id.iv_lock://����
				onClickClose();
				break;			
			case R.id.iv_contor://Ѱ��
				onClickFindCar();
				break;			
			case R.id.iv_un_contor://����,����
				onClickSound();
				break;
			case R.id.bt_contor_Diagnosis:
				onOpenBox();
				break;
			case R.id.bt_control_call://һ������
				onClickCall();
				break;
			case R.id.bt_contor_location://һ����λ
				onClickLocation();
				break;
			case R.id.iv_map://��ͼҳ��
				isControl = false;
				flipper.setDisplayedChild(1);
				bg_set(1);
				break;
			case R.id.iv_control:// ָ��ҳ��
				isControl = true;
				Toast.makeText(getApplicationContext(), "ˢ�³���״̬", Toast.LENGTH_SHORT).show();
				SendCmd(CMD_P20STATUS,SEND_CMD);//����ˢ��ָ��
				flipper.setDisplayedChild(0);
				bg_set(0);				
				break;
			case R.id.iv_pref://����ҳ��
				isControl = false;
				flipper.setDisplayedChild(2);
				bg_set(2);
				break;
			case R.id.rl_pref_sms:
				Intent intent = new Intent(getApplicationContext(), SmsActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_pref_terminal:
				Intent intent1 = new Intent(getApplicationContext(), CarActivity.class);
				startActivityForResult(intent1, 1);
				break;
			case R.id.rl_perf_person:
				Intent intent2 = new Intent(getApplicationContext(), PersonActivity.class);
				startActivity(intent2);
				break;
			case R.id.rl_perf_about:
				Intent intent3 = new Intent(getApplicationContext(), AboutActivity.class);
				startActivity(intent3);
				break;
			case R.id.rl_perf_feedback:
				Intent intent4 = new Intent(getApplicationContext(), FeedbcakActivity.class);
				startActivity(intent4);
				break;
			}
		}
	};    
    Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {			
			case GET_CARINFO://��ȡ�û��³����б�
				getAllCar(msg);
				break;			
			case REFRESH_CARINFO://��ȡ����λ��
				getOneCarLocation(msg);
				break;
			case GET_BDLOCATION://�ĵ���ƫ��ַ����ʾ����
				ShowCar();
				break;
			case GET_MESSAGE_COUNT://δ����Ϣ
				doUnReadMsg(msg.obj.toString());
				break;			
			case GET_PARMS://�õ�������״̬
				if(Dialog != null){
					Dialog.dismiss();
				}
				Refresh(msg.obj.toString());				
				break;
			case SEND_OPEN://ѭ���жϽ��
				new Thread(new getOpenThread(index,is_lockdoor_arming,Config.Business_auth_code)).start();
				break;
			case SEND_CLOSE:
				new Thread(new getCloseThread(index,is_lock,Config.Business_auth_code)).start();
				break;
			case SEND_START:
				new Thread(new getStartThread(index,is_start,Config.Business_auth_code)).start();
				break;
			case SEND_SOUND:
				new Thread(new getSoundThread(index,is_sound,Config.Business_auth_code)).start();
				break;
			case SEND_SOUND_OK://����״̬�ı�����ͼƬ
				if(msg.obj.toString().equals("true")){//��ǰ״̬����
					iv_un_contor.setBackgroundResource(R.drawable.iv_un_contor);
					iv_un_contor.setText(R.string.SOUND);
					is_sound = true;
				}else{//��ǰ״̬����
					iv_un_contor.setBackgroundResource(R.drawable.iv_sound_false);
					iv_un_contor.setText(R.string.SOUND_false);
					is_sound = false;
				}
				break;
			case SEND_START_OK:
				is_start_now = false;
				System.out.println("//����״̬�ı�����ͼƬ");
				if(msg.obj.toString().equals("0")){//��ǰ״̬Ϩ��
					iv_button_start.setBackgroundResource(R.drawable.iv_button_start);
					is_start = "0";
				}else{//��ǰ״̬����
					iv_button_start.setBackgroundResource(R.drawable.iv_stop);
					is_start = "2";
				}
				break;
			case GET_AUTH_CODE:
				Log.d(TAG, msg.toString());
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					Config.Business_auth_code = jsonObject.getString("auth_code");
					Config.obj_id = jsonObject.getString("obj_id");
					GetCmdInfo();
					GetCarInfo();
					GetUnReadMessageNumber();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case OPEN_BOX:
				System.out.println(msg.obj.toString());
				if(msg.obj.toString().indexOf("0")>-1){
					Toast.makeText(getApplicationContext(), "β�俪���ɹ�", Toast.LENGTH_SHORT).show();
				}
				break;
			case SEND_CMD:
				break;
			}
		}    	
    };
    /**
     * ��β��
     */
    private void onOpenBox(){
    	new AlertDialog.Builder(MainActivity.this)
			.setTitle("��ʾ")
			.setMessage("�Ƿ���β��")
			.setNegativeButton("ȡ��", null)
			.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					SendCmd(CMD_OPENTRAIL, OPEN_BOX);
				}
			}).show();
    }
    /**
     * ������Ϩ��
     */
    private void onClickStart(){
    	if(carData == null){
    		return;
    	}
    	if(isOffine()){
    		SendSms(R.string.car_offline_send_sms, cmd_start);
    	}else{
    		if(is_start_now){//�Ƿ�ָ���ڷ�����
    			return;
    		}
    		String message = "";
    		if(is_start.equals("2")){//Ϩ��
    			message = getString(R.string.stop_engine);
    		}else if(is_start.equals("0")){//����				
    			message = getString(R.string.start_engine);
    		}else{//����״̬���� is_start = 1
    			ToastCar(is_start);
    			return;
    		}			
    		if(!is_lockdoor){//�Ƿ�����
    			Toast.makeText(getApplicationContext(), "�������ڿ���״̬, ���ܽ��д˲���", Toast.LENGTH_SHORT).show();
    			return;
    		}
    		//�Ƿ�p20;
    		if(isP20()){
    			AlertDialog.Builder Builder = new AlertDialog.Builder(MainActivity.this);
    			Builder.setTitle(R.string.Note);
    			Builder.setMessage(message);
    			Builder.setPositiveButton(R.string.Sure, new DialogInterface.OnClickListener() {							
    				public void onClick(DialogInterface dialog, int which) {						
    					if(GetSystem.checkNetWorkStatus(getApplicationContext())){//����
    						if(isOffine()){//���߷��Ͷ���
    							if(is_start.equals("2")){
    								SendSms(R.string.car_offline_send_sms, cmd_stop);
    							}else if(is_start.equals("0")){
    								SendSms(R.string.car_offline_send_sms, cmd_start);
    							}
    						}else{
    							String cmd_type = CMD_STARTENGINE;
    							String message = getString(R.string.STARTENGINE);
    							if(is_start.equals("2")){
    								//Ϩ��
    								cmd_type = CMD_STOPENGINE;
    								message = getString(R.string.STARTENGINE_false);
    								iv_button_start.setBackgroundResource(R.drawable.button_stop_press);
    							}else if(is_start.equals("0")){
    								iv_button_start.setBackgroundResource(R.drawable.button_start_press);
    							}
    							SendCmd(cmd_type,SEND_START);
    							Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    							is_start_now = true;
    						}
    					}else{//����,���Ͷ���
    						if(is_start.equals("2")){
    							SendSms(R.string.net_wrong_send_sms, cmd_stop);
    						}else if(is_start.equals("0")){
    							SendSms(R.string.net_wrong_send_sms, cmd_start);
    						}
    					}
    				}
    			});
    			Builder.setNegativeButton(R.string.cancle, null);
    			Builder.show();
    		}else{
    			Toast.makeText(getApplicationContext(), R.string.add_p20, Toast.LENGTH_SHORT).show();
    		}
    	}		
    }
    /**
     * ����
     */
    private void onClickOpen(){
    	if(carData == null){
    		return;
    	}
		if(isP20()){
			is_lockdoor_arming = true;
			if(isOffine()){
				SendSms(R.string.car_offline_send_sms, cmd_open);
			}else{
				if(is_lockdoor){
					if(is_start.equals("0")){
						if(GetSystem.checkNetWorkStatus(getApplicationContext())){//����
							if(isOffine()){//���߷��Ͷ���
								SendSms(R.string.car_offline_send_sms, cmd_open);
							}else{
								SendCmd(CMD_UNLOCKDOOR,SEND_OPEN);
								Toast.makeText(getApplicationContext(), R.string.UNLOCK, Toast.LENGTH_LONG).show();
							}
						}else{
							SendSms(R.string.net_wrong_send_sms, cmd_open);
						}						
						
					}else{
						ToastCar(is_start);
					}
				}else{//����״̬������Ϊ����
					Toast.makeText(getApplicationContext(), "�������ڿ���״̬, ���ܽ��д˲���", Toast.LENGTH_SHORT).show();
				}
			}			
		}else{
			is_lockdoor_arming = false;
			SendCmd(CMD_DISARMING,SEND_OPEN);
			Toast.makeText(getApplicationContext(), R.string.DISARMING, Toast.LENGTH_LONG).show();
		}
    }
    /**
     * ����
     */
    private void onClickClose(){
    	if(carData == null){
    		return;
    	}
		if(isP20()){
			is_lock = true;
			if(isOffine()){
				SendSms(R.string.car_offline_send_sms, cmd_lock);
			}else{
				if(is_lockdoor){//����״̬������
					Toast.makeText(getApplicationContext(), "������������״̬, ���ܽ��д˲���", Toast.LENGTH_SHORT).show();
				}else{
					if(is_start.equals("0")){
						if(GetSystem.checkNetWorkStatus(getApplicationContext())){//����
							if(isOffine()){//���߷��Ͷ���
								SendSms(R.string.car_offline_send_sms, cmd_lock);
							}else{
								SendCmd(CMD_LOCKDOOR,SEND_CLOSE);
								Toast.makeText(getApplicationContext(), R.string.LOCK, Toast.LENGTH_LONG).show();
							}
						}else{
							SendSms(R.string.net_wrong_send_sms, cmd_lock);
						}						
					}else{
						ToastCar(is_start);
					}
				}
			}			
		}else{
			is_lock = false;
			SendCmd(CMD_ARMING,SEND_CLOSE);
			Toast.makeText(getApplicationContext(), R.string.ARMING, Toast.LENGTH_LONG).show();
		}
    }
    /**
     * Ѱ��
     */
    private void onClickFindCar(){
    	if(carData == null){
    		return;
    	}
		if(is_lockdoor == true){
			if(!isOffine()){
				if(is_start.equals("0")){
					SendCmd(CMD_FINDVEHICLE,SEND_CMD);
					Toast.makeText(getApplicationContext(), R.string.FINDVEHICLE, Toast.LENGTH_LONG).show();
				}else{
					ToastCar(is_start);
				}
			}
		}else{
			Toast.makeText(getApplicationContext(), "�������ڿ���״̬, ���ܽ��д˲���", Toast.LENGTH_SHORT).show();
		}
    }
    /**
     * ����
     */
    private void onClickSound(){
    	if(carData == null){
    		return;
    	}
		if(is_lockdoor == true){
			Toast.makeText(getApplicationContext(), "������������״̬, ���ܽ��д˲���", Toast.LENGTH_SHORT).show();
		}else{
			if(!isOffine()){
				if(is_start.equals("0")){
					String message = getString(R.string.SEND_SOUND);
					if(is_sound){
						message = getString(R.string.SEND_SOUND_false);
					}
					AlertDialog.Builder Builder = new AlertDialog.Builder(MainActivity.this);
					Builder.setTitle(R.string.Note);
					Builder.setMessage(message);
					Builder.setPositiveButton(R.string.Sure, new DialogInterface.OnClickListener() {							
						public void onClick(DialogInterface dialog, int which) {
							String cmd_type = CMD_SOUND;
							String message = getString(R.string.SOUND);
							if(is_sound){
								cmd_type = CMD_SLIENT;
								message = getString(R.string.SOUND_false);
							}
							SendCmd(cmd_type,SEND_SOUND);
							Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
						}
					});
					Builder.setNegativeButton(R.string.cancle, null);
					Builder.show();
				}else{
						ToastCar(is_start);
					}
				}
			}
    }
    /**
     * һ������
     */
    private void onClickCall(){
    	if(carData == null){
    		return;
    	}
    	//�ж��Ƿ�������һ�����к���
		if(phoneDatas != null && phoneDatas.size()>0){					
			//���ͳ���λ�õ���Ӧ���ֻ�
			View view_Phone = LayoutInflater.from(MainActivity.this).inflate(R.layout.phone_list, null);
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
			AlertDialog.Builder addPhoneBuilder = new AlertDialog.Builder(MainActivity.this);
			addPhoneBuilder.setTitle(R.string.choose_phone);
			addPhoneBuilder.setView(view_Phone);
			addPhoneBuilder.setNegativeButton(R.string.cancle, null);
			addPhoneBuilder.show();
		}else{
			Toast.makeText(getApplicationContext(), R.string.no_call_phone, Toast.LENGTH_SHORT).show();
		}
    }
    /**
     * һ����λ
     */
    private void onClickLocation(){
    	if(carData == null){
    		return;
    	}
    	if(lat == null){
			Toast.makeText(MainActivity.this, R.string.no_car_location, Toast.LENGTH_SHORT).show();
		}else{
			//���ͳ���λ�õ���Ӧ���ֻ�
			View view_Phone = LayoutInflater.from(MainActivity.this).inflate(R.layout.location_phone, null);
			final EditText et_location_phone = (EditText)view_Phone.findViewById(R.id.et_location_phone);
			AlertDialog.Builder addPhoneBuilder = new AlertDialog.Builder(MainActivity.this);
			addPhoneBuilder.setTitle(R.string.enter_phone);
			addPhoneBuilder.setView(view_Phone);
			addPhoneBuilder.setPositiveButton(R.string.Sure, new DialogInterface.OnClickListener() {					
				public void onClick(DialogInterface dialog, int which) {
					String phone = et_location_phone.getText().toString();
					String url = "http://api.map.baidu.com/geocoder?location=" + lat + "," + lon +"&coord_type=bd09ll&output=html";
					if(phone.equals("")){
						Toast.makeText(MainActivity.this, R.string.not_complete_not, Toast.LENGTH_SHORT).show();
					}else{
						SmsManager sms = SmsManager.getDefault();
						List<String> texts = sms.divideMessage(url);
						for(String text : texts){
							sms.sendTextMessage(phone, null, text, null, null);
						}
						Toast.makeText(MainActivity.this, R.string.send_sms, Toast.LENGTH_SHORT).show();
					}
				}
			});
			addPhoneBuilder.setNegativeButton(R.string.cancle, null);
			addPhoneBuilder.show();
		}
    }
    /**
     * �������г�����Ϣ
     * @param msg
     */
    private void getAllCar(Message msg){
    	try {
    		Log.d(TAG, msg.obj.toString());
        	if(Dialog != null){
    			Dialog.dismiss();
    		}
    		Config.carDatas = GetSystem.JsonCarData(MainActivity.this,msg.obj.toString());
    		if(Config.carDatas.size()>1){
    			rl_car_top.setVisibility(View.VISIBLE);
    			s_car.setVisibility(View.VISIBLE);
    		}else{
    			rl_car_top.setVisibility(View.GONE);
    			s_car.setVisibility(View.INVISIBLE);
    		}
    		if(Config.carDatas.size() != 0){
    			tv_control_isOnLine.setVisibility(View.VISIBLE);
    		}
    		bindSpinner();
    		//��ȡ������auth_code��obj_id
    		String url = Config.carDatas.get(index).getUrl() + "app_login?username=" + Config.account + "&password=" + GetSystem.getM5DEndo(Config.pwd) +"&mac=" + GetSystem.getMacAddress(getApplicationContext()) + "&serial=" + Config.carDatas.get(index).getSerial();
    		new Thread(new NetThread.GetDataThread(handler, url, GET_AUTH_CODE)).start();
    		//���Ҷ�Ӧ������λ��
    		new Thread(new GetAllDataThread()).start();
		} catch (Exception e) {
			e.printStackTrace();
		}    	
    }
    /**
     * ��ȡ��ǰ����λ��
     * @param msg
     */
    private void getOneCarLocation(Message msg){
    	try {
			if(!msg.obj.toString().equals("")){
				carData = new CarData();
				JSONObject jsonObject = new JSONObject(msg.obj.toString());
				JSONObject jsonData = jsonObject.getJSONObject("active_gps_data");
				carData.setObj_id(jsonObject.getString("obj_id"));
				carData.setObj_name(jsonObject.getString("obj_name"));
				new Thread(new GetBDLocation(jsonData.getString("lat"), jsonData.getString("lon"))).start();
				carData.setRcv_time(GetSystem.ChangeTime(jsonData.getString("rcv_time"),0));   
        		try {
					int direct = Integer.valueOf(jsonData.getString("direct"));
					carData.setDirect(direct);
				} catch (Exception e) {
					carData.setDirect(0);
				}        		
        		try {
					int gps_flag = Integer.valueOf(jsonData.getString("gps_flag"));
					carData.setGps_flag(gps_flag);
				} catch (Exception e) {
					carData.setGps_flag(0);
				}
        		try {
					carData.setMileage(jsonData.getString("mileage"));
				} catch (Exception e) {
					carData.setMileage("0");
				}
        		try{
        			double dSpeed = Double.valueOf(jsonData.getString("speed"));
        			int speed = (int)dSpeed;
        			carData.setSpeed(speed);
				} catch (Exception e) {
					e.printStackTrace();
					carData.setSpeed(0);
				}
        		JSONArray jsonArray = jsonData.getJSONArray("uni_status");
        		JSONArray jsonArray1 = jsonData.getJSONArray("uni_alerts");
        		String status = getStatusDesc(carData.getRcv_time(), carData.getGps_flag(), carData.getSpeed(), getUniStatusDesc(jsonArray), getUniAlertsDesc(jsonArray1));
        		carData.setStatus(status);
        		//if(isOffine()){//������ʾ
        			//Toast.makeText(getApplicationContext(), R.string.car_offine, Toast.LENGTH_SHORT).show();
        		//}
			}
		} catch (Exception e) {
			Log.e(TAG, "��ȡ����λ���쳣");
		}
    }  
    //TODO ��ʾ״̬
    /**
     * ���ݳ���״̬��ʾͼƬ
     * @param str
     */
    public void Refresh(String str){
    	try {
    		if(isOffine()){
    			iv_open.setBackgroundResource(R.drawable.iv_lock);
    			iv_lock.setBackgroundResource(R.drawable.iv_open);
    			tv_control_isOnLine.setText("�ն�״̬������");
    			JSONObject jsonObject1 = new JSONObject(str);
    			try {
    				accessory = jsonObject1.getString("accessory");
    			} catch (Exception e) {
    				accessory = "0";
    			}
    			JSONArray jsonArray = jsonObject1.getJSONArray("call_phones");
    			for(int i = 0 ; i < jsonArray.length() ; i++){
    				JSONObject jsonObject = jsonArray.getJSONObject(i);
    				PhoneData phoneData = new PhoneData();
    				phoneData.setName(jsonObject.getString("name"));
    				phoneData.setPhone(jsonObject.getString("phone"));
    				phoneDatas.add(phoneData);
    			}
    			sim = jsonObject1.getString("sim");
    			Log.d(TAG, "���⣺" + is_sound + ",������" + is_start + ",������" +is_lockdoor +",P20:" + accessory);
    			//���±��������
    			String UpdateDB = "update wise_unicom_zwc set accessory = '" + accessory +"',is_sound = '" + is_sound + "',is_start ='" + is_start
    					+ "',is_lockdoor ='" + is_lockdoor + "',sim = '" + sim + "',phone = '" + jsonArray.toString() + "' where serial =" + jsonObject1.getString("serial");
    			DBExcute dbExcute = new DBExcute();
    			dbExcute.UpdateDB(MainActivity.this, UpdateDB);
    		}else{
    			tv_control_isOnLine.setText("�ն�״̬������");
    			JSONObject jsonObject1 = new JSONObject(str);
    			try {
    				accessory = jsonObject1.getString("accessory");
    			} catch (Exception e) {
    				accessory = "0";
    			}
    			JSONArray jsonArray = jsonObject1.getJSONArray("call_phones");
    			for(int i = 0 ; i < jsonArray.length() ; i++){
    				JSONObject jsonObject = jsonArray.getJSONObject(i);
    				PhoneData phoneData = new PhoneData();
    				phoneData.setName(jsonObject.getString("name"));
    				phoneData.setPhone(jsonObject.getString("phone"));
    				phoneDatas.add(phoneData);
    			}
    			JSONObject jsonObject = jsonObject1.getJSONObject("params");
    			try {//�Ƿ�����
    				if(jsonObject.getString("is_sound").equals("true")){//��ʾͼ��Ϊ����
    					is_sound = true;					
    					iv_un_contor.setBackgroundResource(R.drawable.iv_un_contor);
    					iv_un_contor.setText(R.string.SOUND);
    				}else{//��ʾͼ��Ϊ����
    					is_sound = false;					
    					iv_un_contor.setBackgroundResource(R.drawable.iv_sound_false);
    					iv_un_contor.setText(R.string.SOUND_false);
    				}						
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			is_start = jsonObject.getString("is_start");
    			if(!is_start_now){//�Ƿ����ڷ�������ָ��				
    				try {
    					if(is_start.equals("0")){
    						iv_button_start.setBackgroundResource(R.drawable.iv_button_start);
    					}else if(is_start.equals("2")||is_start.equals("1")){
    						iv_button_start.setBackgroundResource(R.drawable.iv_stop);
    					}
    				} catch (Exception e) {
    					Log.e(TAG, "û������");
    					iv_button_start.setBackgroundResource(R.drawable.iv_button_start);
    				}
    			}
    			if(jsonObject.getString("is_lockdoor").equals("true")){ //����״̬
    				is_lockdoor = true;
    				iv_lock.setBackgroundResource(R.drawable.button_open_press);
    				iv_lock.setTextColor(Color.RED);
    				iv_open.setBackgroundResource(R.drawable.iv_lock);
    				iv_open.setTextColor(Color.BLACK);
    			}else{	//����״̬
    				is_lockdoor = false;
    				iv_open.setBackgroundResource(R.drawable.button_lock_press);
    				iv_open.setTextColor(Color.RED);
    				iv_lock.setBackgroundResource(R.drawable.iv_open);
    				iv_lock.setTextColor(Color.BLACK);
    			}
    			sim = jsonObject1.getString("sim");
    			Log.d(TAG, "���⣺" + is_sound + ",������" + is_start + ",������" +is_lockdoor +",P20:" + accessory);
    			//���±��������
    			String UpdateDB = "update wise_unicom_zwc set accessory = '" + accessory +"',is_sound = '" + is_sound + "',is_start ='" + is_start
    					+ "',is_lockdoor ='" + is_lockdoor + "',sim = '" + sim + "',phone = '" + jsonArray.toString() + "' where serial =" + jsonObject1.getString("serial");
    			DBExcute dbExcute = new DBExcute();
    			dbExcute.UpdateDB(MainActivity.this, UpdateDB);
    			if(!isBlueServer){
    				//�Ƿ�����������
    				if(accessory != null&&accessory.equals("20487")){
    					Intent startService = new Intent(MainActivity.this, BluetoothServerService.class);
    					startService(startService);
    				}
    				isBlueServer = true;
    			}
    		}						
    	} catch (Exception e) {
			Log.e(TAG, "��ȡ����״̬�쳣");
		}
    }
	/**
	 * ����ָ��
	 * @param cmd_type  ָ�����
	 * @param cmd ���ͳɹ���handler����
	 */
	private void SendCmd(String cmd_type,int cmd){	
		try {
			String url = Config.carDatas.get(index).url + "command?auth_code=" + Config.Business_auth_code;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("obj_id", Config.obj_id));
			params.add(new BasicNameValuePair("cmd_type", cmd_type));
			params.add(new BasicNameValuePair("params", "{}"));
			new Thread(new NetThread.postDataThread(handler, url, params, cmd)).start();
		} catch (Exception e) {
			Log.d(TAG, "����ָ���쳣");
		}
	}	
	/**
	 * ��spinner�ؼ�
	 */
	private void bindSpinner(){
		for(int i = 0 ; i < Config.carDatas.size(); i++){
			list.add(Config.carDatas.get(i).getObj_name());
		}
		//��ͼҳ��ķ��
		carAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		carAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // ���������б�ķ��
		s_carid.setAdapter(carAdapter);
		s_carid.setOnItemSelectedListener(onItemSelectedListener);
		//����ҳ��ķ��
		carAdapter1 = new ArrayAdapter<String>(this, R.layout.myspinner, list);
		carAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // ���������б�ķ��			
		s_car.setAdapter(carAdapter1);
		s_car.setOnItemSelectedListener(onItemSelectedListener);
	}	
	OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			System.out.println("onItemSelectedListener");
			//���ͼ��
    		mapOverLays.remove(itemOverLay);
    		popView.setVisibility(View.GONE);
    		mMapController.animateTo(mMapView.getMapCenter());    		
			carData = new CarData();
			index = arg2;
			Config.index = arg2;
			s_car.setSelection(index);
			s_carid.setSelection(index);
			Reset();			
			//��ȡ������auth_code��obj_id
			String url = Config.carDatas.get(index).getUrl() + "app_login?username=" + Config.account + "&password=" + GetSystem.getM5DEndo(Config.pwd) +"&mac=" + GetSystem.getMacAddress(getApplicationContext()) + "&serial=" + Config.carDatas.get(index).getSerial();
			Log.d(TAG, "Get_Auth_code=" + url);
			new Thread(new NetThread.GetDataThread(handler, url, GET_AUTH_CODE)).start();
		}
		public void onNothingSelected(AdapterView<?> arg0) {
			Log.d(TAG, "onNothingSelected");
		}
	};
	
	/**
	 * �л�����������������
	 */
	private void Reset(){
		lat = null;
		lon = null;
		is_sound = false;
		is_start = "0";
		is_start_now = false; //��ǰ��start��ť����
		address = "";
		accessory = "0";
		sim = "";
	}
	/**
	 * ��ȡ��������״̬
	 */
	private void GetCmdInfo(){
		phoneDatas = new ArrayList<PhoneData>();		
		String url = Config.carDatas.get(index).url + "vehicle/" + Config.obj_id + "?auth_code=" + Config.Business_auth_code;
		new Thread(new NetThread.GetDataThread(handler, url, GET_PARMS)).start();
	}
	/**
	 * ��ȡ����������Ϣ
	 */
	private void GetCarInfo(){
		String url = Config.carDatas.get(index).url + "vehicle/" + Config.obj_id + "/active_gps_data?auth_code=" + Config.Business_auth_code;
		new Thread(new NetThread.GetDataThread(handler, url, REFRESH_CARINFO)).start();
	}
	/**
	 * ��ȡδ����Ϣ��Ŀ
	 */
	private void GetUnReadMessageNumber(){
		try {
			String unReadUrl = Config.carDatas.get(index).url + "vehicle/" + Config.obj_id + "/noti_new?auth_code=" + Config.Business_auth_code + "&update_time=" + URLEncoder.encode(Config.GetMessageTime,"UTF-8");
			Log.d(TAG, unReadUrl);
			new Thread(new NetThread.GetDataThread(handler, unReadUrl, GET_MESSAGE_COUNT)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
    /**
     * ��ʾָ������ͼ��
     * @param index
     */
    private void ShowCar(){
    	try {
    		mapOverLays.remove(itemOverLay);
    		popView.setVisibility(View.GONE);
    		mMapController.animateTo(mMapView.getMapCenter());
    		
        	GeoPoint point = new GeoPoint(GetSystem.StringToInt(carData.getRev_lat()), GetSystem.StringToInt(carData.getRev_lon()));
        	mMKSearch.reverseGeocode(point); 
        	Drawable drawable = GetSystem.GetDrawable(getApplicationContext(),carData.getDirect(),R.drawable.car);
        	drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    		itemOverLay= new CarItemizedOverlay(drawable,point,carData.getObj_name(),PicSize);
    		OverlayItem overLayItem = new OverlayItem(point, "", ""); // �󶨵���¼�
    		itemOverLay.addOverLay(overLayItem);
    		mapOverLays.add(itemOverLay); // ͼ����ӵ�map��ʾ
    		itemOverLay.setOnFocusChangeListener(onFocusChangeListener); // ����ʾ�¼�
    		
    		mMapController.animateTo(point);  
		} catch (Exception e) {
			e.printStackTrace();
			mMapController.animateTo(mMapView.getMapCenter());
		}    	
    } 
    /**
     * ��ַ��ƫ
     * @author honesty
     */
    class GetBDLocation extends Thread{
    	String tLat;
    	String tLon;
    	public GetBDLocation(String Lat,String Lon){
    		this.tLat = Lat;
    		this.tLon = Lon;
    	}
    	@Override
    	public void run() {
    		super.run();
    		String Location = GetSystem.chanGeoPoint(tLat, tLon);
    		String bdLon;
    		String bdLat;
    		try {
    			String[] array_Location = Location.split(",");
        		bdLon = GetSystem.basetoString(array_Location[0]);
        		bdLat = GetSystem.basetoString(array_Location[1]);
			} catch (Exception e) {
				bdLon = lon;
				bdLat = lat;
			}
			//��ַ������
			SharedPreferences preferences = getSharedPreferences(Config.sharedPreferencesName, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString("Rev_lat", bdLat);
			editor.putString("Rev_lon", bdLon);				
			editor.commit();
			carData.setRev_lat(bdLat);
			carData.setRev_lon(bdLon);  
			lat = bdLat;
			lon = bdLon;
			Message message = new Message();
			message.what = GET_BDLOCATION;
			handler.sendMessage(message);
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
            	Cursor cursor = managedQuery(Uri.parse("content://sms/inbox"),new String[] { "_id", "address", "body" },"read=?",new String[] {"0" }, "date desc");
            	if(cursor != null){
            		Log.d(TAG, "������");
            		cursor.moveToFirst();
					if(sim.equals(cursor.getString(1))){//��Ҫ���صĺ���
						String body = cursor.getString(2);
	                    Toast.makeText(MainActivity.this, body, Toast.LENGTH_SHORT).show();
	                    if(body.indexOf("�����ɹ�") >= 0 || body.indexOf("����ɹ�") >= 0){
	                    	Log.d(TAG, "�����ɹ�");
	                    	is_lockdoor = true;
            				iv_lock.setTextColor(Color.RED);
            				iv_open.setTextColor(Color.BLACK);
	                    }else if(body.indexOf("�����ɹ�") >= 0 || body.indexOf("�����ɹ�") >= 0){
	                    	Log.d(TAG, "�����ɹ�");
	                    	is_lockdoor = false;
            				iv_open.setTextColor(Color.RED);
            				iv_lock.setTextColor(Color.BLACK);
	                    }else if(body.indexOf("�����ɹ�") >= 0){
	                    	Log.d(TAG, "�����ɹ�");
	                    	is_start = "0";
	                    	iv_button_start.setBackgroundResource(R.drawable.iv_button_start);
	                    }else if(body.indexOf("Ϩ��ɹ�") >= 0){
	                    	Log.d(TAG, "Ϩ��ɹ�");
	                    	is_start = "2";
	                    	iv_button_start.setBackgroundResource(R.drawable.iv_stop);
	                    }
					}
            	}
			} catch (Exception e) {
				e.printStackTrace();
			}                                          
        }
    }
    /**
     * �����ж�
     * @author honesty
     */
    class getOpenThread extends Thread{
    	int item;
    	boolean is_lockdoor_arming; //����or���
    	String auth_code;
    	public getOpenThread(int index, boolean is_lockdoor_arming,String auth_code){
    		this.item = index;
    		this.is_lockdoor_arming = is_lockdoor_arming;
    		this.auth_code = auth_code;
    	}
    	@Override
    	public void run() {
    		super.run();
    		for (int i = 0; i < 2; i++) {
				try {
					Log.d(TAG, "��" + i + "��ѭ��");
					Thread.sleep(5000);
					String data = GetSystem.GetData(item,auth_code,Config.obj_id);					
					//�ж�
					JSONObject jsonObject1 = new JSONObject(data);
	    			JSONObject jsonObject = jsonObject1.getJSONObject("params");
	    			if(is_lockdoor_arming){ //�ж��������Ƿ�Ϊtrue	    				
	    				try {
							if(jsonObject.getString("is_lockdoor").equals("false")){
								is_lockdoor = false;
								showNotification(R.drawable.send_ok,"ƽ̨�����ɹ���","ָ����ʾ","ƽ̨�����ɹ���");
								if(item == index){
									//����ָ��ͼƬ
									Message message = new Message();
									message.what = GET_PARMS;
									message.obj = data;
									handler.sendMessage(message);
								}
								break;
							}						
						} catch (Exception e) {
							e.printStackTrace();
						}
	    			}else{
	    				//�ж�����Ƿ�Ϊtrue
	    				try {
							if(jsonObject.getString("is_arming").equals("false")){
								showNotification(R.drawable.send_ok,"ƽ̨�����ɹ���","ָ����ʾ","ƽ̨�����ɹ���");
								if(item == index){
									//����ָ��ͼƬ
									Message message = new Message();
									message.what = GET_PARMS;
									message.obj = data;
									handler.sendMessage(message);
								}
								break;
							}						
						} catch (Exception e) {
							e.printStackTrace();
						}
	    			}
	    			//ѭ��
	    			if(i == 1){
	    				Log.d(TAG, "open=error");
	    				if(is_lockdoor_arming){
	    					showNotification(R.drawable.send_error,"ƽ̨����ʧ�ܣ�","ָ����ʾ","ƽ̨����ʧ�ܣ�");
	    				}else{
	    					showNotification(R.drawable.send_error,"ƽ̨����ʧ�ܣ�","ָ����ʾ","ƽ̨����ʧ�ܣ�");
	    				}
    				}
	    			if(item == index){
						//����ָ��ͼƬ
						Message message = new Message();
						message.what = GET_PARMS;
						message.obj = data;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
    	}
    }
    /**
     * �����ж�
     * @author honesty
     *
     */
    class getCloseThread extends Thread{
    	int item;
    	boolean is_lock;
    	String auth_code;
    	public getCloseThread(int index, boolean is_lock,String auth_code){
    		this.item = index;
    		this.is_lock = is_lock;
    		this.auth_code = auth_code;
    	}
    	@Override
    	public void run() {
    		super.run();
    		for (int i = 0; i < 2; i++) {
				try {
					Log.d(TAG, "��" + i + "��ѭ��");
					Thread.sleep(5000);
					String data = GetSystem.GetData(item,auth_code,Config.obj_id);
					//�ж�
					JSONObject jsonObject1 = new JSONObject(data);
	    			JSONObject jsonObject = jsonObject1.getJSONObject("params");
	    			Log.d(TAG, "is_lockdoor=" + jsonObject.getString("is_lockdoor") + "is_arming=" + jsonObject.getString("is_arming"));
	    			if(is_lock){
	    				//�ж��������Ƿ�Ϊtrue
	    				try {
							if(jsonObject.getString("is_lockdoor").equals("true")){
								is_lockdoor = true;
								Log.d(TAG, "is_lockdoor = true");
								showNotification(R.drawable.send_ok,"ƽ̨�����ɹ�!","ָ����ʾ","ƽ̨�����ɹ�!");
								if(item == index){
									//����ָ��ͼƬ
									Message message = new Message();
									message.what = GET_PARMS;
									message.obj = data;
									handler.sendMessage(message);
								}
								break;
							}						
						} catch (Exception e) {
							e.printStackTrace();
						}
	    			}else{
	    				//�ж�����Ƿ�Ϊtrue
	    				try {
							if(jsonObject.getString("is_arming").equals("true")){
								Log.d(TAG, "is_arming = false");
								showNotification(R.drawable.send_ok,"ƽ̨����ɹ�!","ָ����ʾ","ƽ̨����ɹ�!");
								if(item == index){
									//����ָ��ͼƬ
									Message message = new Message();
									message.what = GET_PARMS;
									message.obj = data;
									handler.sendMessage(message);
								}
								break;
							}						
						} catch (Exception e) {
							e.printStackTrace();
						}
	    			}
	    			if(i == 1){
	    				Log.d(TAG, "open=error");
	    				if(is_lock){
	    					showNotification(R.drawable.send_error,"ƽ̨����ʧ��!","ָ����ʾ","ƽ̨����ʧ��!");
	    				}else{
	    					showNotification(R.drawable.send_error,"ƽ̨���ʧ��!","ָ����ʾ","ƽ̨���ʧ��!");
	    				}
    				}	
	    			if(item == index){
						//����ָ��ͼƬ
						Message message = new Message();
						message.what = GET_PARMS;
						message.obj = data;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();					
				}
			}
    	}
    }
    /**
     * ����Ϩ���ж�
     */
    class getStartThread extends Thread{
    	int item;
    	String is_start;
    	String auth_code;    	
    	String is_start_in = "3";
    	public getStartThread(int index,String is_start,String auth_code){
    		this.item = index;
    		this.is_start = is_start;
    		this.auth_code = auth_code;
    	}
    	@Override
    	public void run() {
    		super.run();
    		for (int i = 0; i < 10; i++) {
				try {
					Log.d(TAG, "��" + i + "��ѭ��");
					Thread.sleep(10000);
					String data = GetSystem.GetData(item,auth_code,Config.obj_id);
	    			JSONObject jsonObject = new JSONObject(data).getJSONObject("params");
    				//TODO �ж��������Ƿ�Ϊtrue
	    			try {
    					String now_is_start = jsonObject.getString("is_start"); 
        				System.out.println("��ǰ״̬="+ now_is_start);
    					if(is_start_in.equals(now_is_start)){//����������
    						
    					}else{
    						if(is_start.equals(now_is_start)){//����ʧ��
    							if(is_start.equals("0")){
    								showNotification(R.drawable.send_error,"ƽ̨����ʧ��!","ָ����ʾ","ƽ̨����ʧ��!");
    							}else{
    								showNotification(R.drawable.send_error,"ƽ̨Ϩ��ʧ��!","ָ����ʾ","ƽ̨Ϩ��ʧ��!");
    							}
    						}else{//�����ɹ�
    							if(now_is_start.equals("2")){
        							showNotification(R.drawable.send_ok,"ƽ̨�����ɹ�!","ָ����ʾ","ƽ̨�����ɹ�!");
        						}else if(now_is_start.equals("0")){
        							showNotification(R.drawable.send_ok,"ƽ̨Ϩ��ɹ�!","ָ����ʾ","ƽ̨Ϩ��ɹ�!");
        						}
    						}
							if(item == index){
								//����ָ��ͼƬ
								Message message = new Message();
								message.what = SEND_START_OK;
								message.obj = now_is_start;
								handler.sendMessage(message);
							}
							break;
    					}
					} catch (Exception e) {
						e.printStackTrace();
					}
	    			if(i == 9){
	    				Log.d(TAG, "open=error");
	    				if(is_start.equals("0")){
							showNotification(R.drawable.send_error,"ƽ̨����ʧ��!","ָ����ʾ","ƽ̨����ʧ��!");
						}else{
							showNotification(R.drawable.send_error,"ƽ̨Ϩ��ʧ��!","ָ����ʾ","ƽ̨Ϩ��ʧ��!");
						}
	    				if(item == index){
							Message message = new Message();
							message.what = SEND_START_OK;
							message.obj = is_start;
							handler.sendMessage(message);
						}
	    				break;
    				}
	    			if(item == index){
						Message message = new Message();
						message.what = GET_PARMS;
						message.obj = data;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();					
				}
			}
    	}
    }
    /**
     * ���⣬����
     * @author honesty
     */
    class getSoundThread extends Thread{
    	int item;
    	boolean is_sound;
    	String auth_code;
    	public getSoundThread(int index,boolean is_sound,String auth_code){
    		this.item = index;
    		this.is_sound = is_sound;
    		this.auth_code = auth_code;
    	}
    	@Override
    	public void run() {
    		super.run();
    		for (int i = 0; i < 3; i++) {
				try {
					Log.d(TAG, "��" + i + "��ѭ��");
					Thread.sleep(10000);
					String data = GetSystem.GetData(item,auth_code,Config.obj_id);
					//�ж�
					JSONObject jsonObject1 = new JSONObject(data);
	    			JSONObject jsonObject = jsonObject1.getJSONObject("params");
	    			Log.d(TAG, "is_sound=" + jsonObject.getString("is_sound"));
    				try {
    					boolean is_sound_close = false;
						if(jsonObject.getString("is_sound").equals("true")){
							is_sound_close = true;
						}
						if(!is_sound_close == is_sound){
							if(is_sound){
								showNotification(R.drawable.send_ok,"���þ���ģʽ�ɹ�!","ָ����ʾ","���þ���ģʽ�ɹ�!");
							}else{
								showNotification(R.drawable.send_ok,"��������ģʽ�ɹ�!","ָ����ʾ","��������ģʽ�ɹ�!");
							}
							if(item == index){
								Message message = new Message();
								message.what = SEND_SOUND_OK;
								message.obj = is_sound_close;
								handler.sendMessage(message);
							}
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
	    			if(i == 2){
	    				if(is_sound){
							showNotification(R.drawable.send_error,"���þ���ģʽʧ��!","ָ����ʾ","���þ���ģʽʧ��!");
						}else{
							showNotification(R.drawable.send_error,"��������ģʽʧ��!","ָ����ʾ","��������ģʽʧ��!");
						}
    				}	
	    			if(item == index){
						Message message = new Message();
						message.what = GET_PARMS;
						message.obj = data;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();					
				}
			}
    	}
    }
    
    int intervalTime = 5000;
    /**
     * ��ʱˢ������
     * @author honesty
     */
    class GetAllDataThread extends Thread{
    	int i = 0; //��״̬5sһ�Σ�����30sһ��
    	@Override
    	public void run() {
    		super.run();
    		while(isGetData){
    			if(!isAppPause){
    				try {
    					if (Config.carDatas.size()>0) {
    						i++;
    						if(isControl){
    							Log.d(TAG, "5sˢ��");
    							GetCmdInfo();//��ȡ����״̬
    							if(i == 1){
            						SendCmd(CMD_P20STATUS,SEND_CMD);//����ˢ��ָ��
    							}
    						}					
    						if(i == 1 || i == 6){
    							GetCarInfo();
        						GetUnReadMessageNumber();
    						}
    						if(i == 12){
    							i = 0;
    						}
    					}else{
    						Log.d(TAG, "û�г���");
    					}
					} catch (Exception e) {
						e.printStackTrace();
					}
    			}
    			try {
					Thread.sleep(intervalTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    private final ItemizedOverlay.OnFocusChangeListener onFocusChangeListener = new ItemizedOverlay.OnFocusChangeListener() {
		@SuppressWarnings("rawtypes")
		public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
			if (popView != null) {
				popView.setVisibility(View.GONE);
			}
			if (newFocus != null) {
				MapView.LayoutParams geoLP = (MapView.LayoutParams) popView.getLayoutParams();
				geoLP.point = newFocus.getPoint();
				TextView tv_car_id = (TextView) popView.findViewById(R.id.pop_car_id);
				TextView tv_car_MSTStatus = (TextView) popView.findViewById(R.id.pop_car_MSTStatus);
				TextView tv_car_Speed = (TextView) popView.findViewById(R.id.pop_car_Speed);
				TextView tv_car_GpsTime = (TextView) popView.findViewById(R.id.pop_car_GpsTime);
							
				tv_car_id.setText(carData.getObj_name());
				tv_car_GpsTime.setText(carData.getRcv_time());				
				tv_car_MSTStatus.setText(address);
				tv_car_Speed.setText(carData.getStatus());
				mMapView.updateViewLayout(popView, geoLP);
				popView.setVisibility(View.VISIBLE);
			}
		}
	};
	class MyOverlay extends Overlay{
		GeoPoint geoPoint;
		public MyOverlay(GeoPoint geoPoint){
			this.geoPoint = geoPoint;
		}
		@Override
		public boolean draw(Canvas arg0, MapView mapView, boolean arg2, long arg3) {
			super.draw(arg0, mapView, arg2);
			Point point = new Point();
			Projection projection = mapView.getProjection();
			projection.toPixels(geoPoint, point);
			Paint paint = new Paint();
            // ����γ��ת����ʵ����Ļ����        
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_locr);
            arg0.drawBitmap(bmp, point.x - bmp.getWidth()/2, point.y - bmp.getHeight()/2, paint);
			return true;
		}
	}
	
	public class MySearhListener implements MKSearchListener{
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			address = arg0.strAddr;
		}
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {}
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {}
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {}
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {}
		public void onGetRGCShareUrlResult(String arg0, int arg1) {}
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {}
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {}
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {}
	}
    @Override
    protected void onResume() {
    	super.onResume();
    	if (mBMapMan != null) {
			mBMapMan.start();
			mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		}
    	isAppPause = false;
    }   
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if (mBMapMan != null) {
			mBMapMan.stop();
			mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		}
    	Log.d(TAG, "onPause()");
    	isAppPause = true;
    }
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	isGetData = false;
    	this.unregisterReceiver(sendMessageBroadCastReceiver);
    	this.getContentResolver().unregisterContentObserver(content);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == 1){
    		Log.d(TAG, "ˢ��spinner");
    		list.clear();
    		for(int i = 0 ; i < Config.carDatas.size(); i++){
    			Log.d(TAG, Config.carDatas.get(i).getObj_name());
    			list.add(Config.carDatas.get(i).getObj_name());
    		}
    		carAdapter.notifyDataSetChanged();
    		carAdapter1.notifyDataSetChanged();
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	/**
	 * ���ݷֱ��ʣ����������С
	 */
	private void getDM(){
		 DisplayMetrics dm = new DisplayMetrics();  
		 getWindowManager().getDefaultDisplay().getMetrics(dm);
	     int with = dm.widthPixels;
		 if(with >= 480){
			 PicSize = 20;
	     }else if(with >=240){
	    	 PicSize = 16;
	     }else{
	    	 PicSize = 6;
	     }
	}
	/**
	 * ��ʼ������
	 */
	private void init(){
		setContentView(R.layout.main);        
        flipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
        LayoutInflater mLayoutInflater = LayoutInflater.from(MainActivity.this);
        iv_map = (ImageView)findViewById(R.id.iv_map);
        iv_map.setOnClickListener(onClickListener);
        iv_control = (ImageView)findViewById(R.id.iv_control);
        iv_control.setOnClickListener(onClickListener);
        iv_pref = (ImageView)findViewById(R.id.iv_pref);
        iv_pref.setOnClickListener(onClickListener);
        iv_UnReadMsg = (ImageView)findViewById(R.id.iv_UnReadMsg);
		//����ҳ��
		View controlView = mLayoutInflater.inflate(R.layout.control, null);
		flipper.addView(controlView);
		iv_button_start = (ImageView)controlView.findViewById(R.id.iv_button_start);
		iv_button_start.setOnClickListener(onClickListener);
		s_car = (Spinner)controlView.findViewById(R.id.s_car);
		tv_control_isOnLine = (TextView)controlView.findViewById(R.id.tv_control_isOnLine);
		iv_open = (Button)controlView.findViewById(R.id.iv_open);
		iv_open.setOnClickListener(onClickListener);
		iv_lock = (Button)controlView.findViewById(R.id.iv_lock);
		iv_lock.setOnClickListener(onClickListener);
		iv_contor = (Button)controlView.findViewById(R.id.iv_contor);
		iv_contor.setOnClickListener(onClickListener);
		iv_un_contor = (Button)controlView.findViewById(R.id.iv_un_contor);
		iv_un_contor.setOnClickListener(onClickListener);
		bt_contor_location = (Button)controlView.findViewById(R.id.bt_contor_location);
		bt_contor_location.setOnClickListener(onClickListener);
		bt_contor_Diagnosis = (Button)controlView.findViewById(R.id.bt_contor_Diagnosis);
		bt_contor_Diagnosis.setOnClickListener(onClickListener);
		bt_control_call = (Button)controlView.findViewById(R.id.bt_control_call);
		bt_control_call.setOnClickListener(onClickListener);
        //��ͼҳ��
        View mapView = mLayoutInflater.inflate(R.layout.map, null);
		flipper.addView(mapView);
		bt_ZoomDown = (Button)mapView.findViewById(R.id.bt_ZoomDown);
		bt_ZoomDown.setOnClickListener(onClickListener);
		bt_ZoomUp = (Button)mapView.findViewById(R.id.bt_ZoomUp);
		bt_ZoomUp.setOnClickListener(onClickListener);
		iv_aTraffic = (ImageView)mapView.findViewById(R.id.iv_aTraffic);
		iv_aTraffic.setOnClickListener(onClickListener);
		iv_Me = (ImageView)mapView.findViewById(R.id.iv_Me);
		iv_Me.setOnClickListener(onClickListener);
		iv_car = (ImageView)mapView.findViewById(R.id.iv_car);
		iv_car.setOnClickListener(onClickListener);
		rl_car_top = (LinearLayout)mapView.findViewById(R.id.rl_car_top);
		s_carid = (Spinner)mapView.findViewById(R.id.s_carid);
		//����ҳ��
		View prefreView = mLayoutInflater.inflate(R.layout.prefrenece, null);
		flipper.addView(prefreView);
		rl_pref_sms = (RelativeLayout)prefreView.findViewById(R.id.rl_pref_sms);
		rl_pref_sms.setOnClickListener(onClickListener);
		rl_pref_terminal = (RelativeLayout)prefreView.findViewById(R.id.rl_pref_terminal);
		rl_pref_terminal.setOnClickListener(onClickListener);
		rl_perf_person = (RelativeLayout)prefreView.findViewById(R.id.rl_perf_person);
		rl_perf_person.setOnClickListener(onClickListener);
		rl_perf_about = (RelativeLayout)prefreView.findViewById(R.id.rl_perf_about);
		rl_perf_about.setOnClickListener(onClickListener);
		rl_perf_feedback = (RelativeLayout)prefreView.findViewById(R.id.rl_perf_feedback);
		rl_perf_feedback.setOnClickListener(onClickListener);
		iv_perfrenece_UnReadMsg = (ImageView)prefreView.findViewById(R.id.iv_perfrenece_UnReadMsg);
		
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("934B1BF7DD618B5E356CA715D473D1BD864048AC", null);
		super.initMapActivity(mBMapMan);
		mMapView = (MapView) findViewById(R.id.MapView);
		mMapView.setBuiltInZoomControls(false); // �����������õ����ſؼ�
		//���������Ŷ���������Ҳ��ʾoverlay,Ĭ��Ϊ������
        mMapView.setDrawOverlayWhenZooming(true);
		mMapController = mMapView.getController(); // �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		//������ʼ����
		SharedPreferences preferences = getSharedPreferences(Config.sharedPreferencesName, Context.MODE_PRIVATE);
		String Rev_lat = preferences.getString("Rev_lat", "");
		if(!Rev_lat.equals("")){
			System.out.println("Rev_lat="+Rev_lat);
			String Rev_lon = preferences.getString("Rev_lon", "");
			GeoPoint point = new GeoPoint(GetSystem.StringToInt(Rev_lat), GetSystem.StringToInt(Rev_lon));
			mMapController.setCenter(point);
		}
		mMapController.setZoom(15);
		mapOverLays = mMapView.getOverlays();
        
		// ��ʼ�������ע��ʾ��ʽ
		popView = super.getLayoutInflater().inflate(R.layout.pop, null);
		mMapView.addView(popView, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,0,0,
				MapView.LayoutParams.BOTTOM_CENTER));
		popView.setVisibility(View.GONE);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Dialog = ProgressDialog.show(MainActivity.this,getString(R.string.Note),getString(R.string.Get_car_data),true);
		//��ȡ�����б�
		String url = Config.URL + "customer/mobile/" + Config.account + "/vehicle?auth_code=" + Config.auth_code + "&business_type=1";
		new Thread(new NetThread.GetDataThread(handler, url, GET_CARINFO)).start();
        Log.d(TAG, url);
		//ע�ᶨλ�¼�
        mLocationListener = new LocationListener(){
			public void onLocationChanged(Location location) {
				if (location != null){
					myLocation = new GeoPoint((int)(location.getLatitude()*1e6),(int)(location.getLongitude()*1e6));
		    		mapOverLays.remove(myOverlay); //�Ƴ�
	    			myOverlay = new MyOverlay(myLocation);
	        		mapOverLays.add(myOverlay);	//���
				}else{
					System.out.println("location null");
				}
			}
        };
        //���ҵ�ַ
        mMKSearch = new MKSearch();  
        mMKSearch.init(mBMapMan, new MySearhListener());  
        getDM();
        //�ж϶����Ƿ��ͳɹ�
        sendMessageBroadCastReceiver = new SendMessageBroadCastReceiver();        
        this.registerReceiver(sendMessageBroadCastReceiver, new IntentFilter("SENT_SMS_ACTION"));
        
        content = new SmsContent(new Handler());
        // ע����ű仯����
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(MainActivity.this)
				.setTitle("��ʾ")
				.setMessage("���Ƿ�Ҫ�˳��ͻ��ˣ�")
				.setNegativeButton("ȡ��", null)
				.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						finish();
					}
				}).show();
		}
		return super.onKeyDown(keyCode, event);
	}	
	/**
     * ʵʱ��ͨ����ͼ�л�
     */
    private void ChangeTraffic(){
    	if(IsTraffic){
			IsTraffic = false;
			iv_aTraffic.setBackgroundResource(R.drawable.traffic_control);
		}else{
			IsTraffic = true;
			iv_aTraffic.setBackgroundResource(R.drawable.traffic_control_enable);
		}
		mMapView.setTraffic(IsTraffic);
    }
	/**
	 * ��ǰ����״̬
	 * @param is_start
	 */
	private void ToastCar(String is_start){
		if(is_start.equals("0")){
			Toast.makeText(getApplicationContext(), "��������Ϩ��״̬, ���ܽ��д˲���", Toast.LENGTH_SHORT).show();
		}else if(is_start.equals("1")){
			Toast.makeText(getApplicationContext(), "������������״̬, ���ܽ��д˲���", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(getApplicationContext(), "��������Զ������״̬, ���ܽ��д˲���", Toast.LENGTH_SHORT).show();
		}
	}
	//TODO �����ж�
	/**
	 * �豸�Ƿ�����
	 * @return
	 */
	private boolean isOffine(){
		if(carData.getRcv_time() == null || carData.getRcv_time().equals("")){
			return true;
		}
		long time = GetSystem.GetTimeDiff(carData.getRcv_time());
		System.out.println("time="+time);
		if(time > 11){	
			return true;
		}else{
			return false;
		}
	}
	/**
	 * TODO asd ����Ҫ�жϸ���
	 * @return
	 */
	private boolean isP20(){
		if(accessory.equals("0")){
			return true;
		}else{
			return true;
		}
	}
	private void SendSms(int message, final String cmd){
		new AlertDialog.Builder(MainActivity.this)
		.setTitle("��ʾ")
		.setMessage(message)
		.setNegativeButton("ȡ��", null)
		.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				Log.d(TAG, sim + "," + cmd);
				SmsManager sms = SmsManager.getDefault();
				Intent sendIntent = new Intent("SENT_SMS_ACTION");
				PendingIntent sendPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, sendIntent, 0);
				sms.sendTextMessage(sim, null, cmd, sendPendingIntent, null);
			}
		}).show();
	}
	class SendMessageBroadCastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive=" + intent.getAction());
			if(intent.getAction().equals("SENT_SMS_ACTION")){
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(MainActivity.this, R.string.send_sms, Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(MainActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}		
	}
	/**
     * �жϵ�ǰ״̬
     * @param Gps_time gpsʱ��
     * @param gps_flag flag
     * @param speed �ٶ�
     * @param UniStatusDesc ״̬
     * @param UniAlertsDesc ״̬
     * @return
     */
    private String getStatusDesc(String Rec_time, int gps_flag,int speed,String UniStatusDesc,String UniAlertsDesc){
    	String desc = "";
    	long time = GetSystem.GetTimeDiff(Rec_time);
    	if(time<10){//�Ƿ�����
    		if(gps_flag%2==0){
        		if(speed > 10){//�ٶ��ж�
        			desc = "��ʻ," + UniStatusDesc + UniAlertsDesc + " " + speed + "����/Сʱ";
        		}else{
        			desc = "��ֹ," + UniStatusDesc + UniAlertsDesc;
        		}
        	}else{
        		if(speed > 10){
        			desc = "ä��," + UniStatusDesc + UniAlertsDesc;
        		}else{
        			desc = "��ֹ," + UniStatusDesc + UniAlertsDesc;
        		}
        	}
    	}else{
    		desc = "����" + GetSystem.ShowOfflineTime(time);
    	}
    	if(desc.endsWith(",")){//��ʽ�����
    		desc = desc.substring(0, desc.length()-1);
    	}
    	return desc;
    }    
    String STATUS_FORTIFY = "8193";
    String STATUS_LOCK = "8194";
    String STATUS_NETLOC = "8195";
    String STATUS_SLEEP = "8197";
    public String getUniStatusDesc(JSONArray jsonArray){
    	String str = "";
    	for(int i = 0 ; i < jsonArray.length() ; i++){
    		try {
    			String jsonString = jsonArray.getString(i);
    			if(jsonString.equals(STATUS_FORTIFY)){
    				str += "���,";
    			}else if(jsonString.equals(STATUS_LOCK)){
    				str += "����,";
    			}else if(jsonString.equals(STATUS_FORTIFY)){
    				str += "��վ��λ,";
    			}else if(jsonString.equals(STATUS_FORTIFY)){
    				str += "ʡ��״̬,";
    			}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return str;
    }
    
    String ALERT_SOS = "12289";
    String ALERT_OVERSPEED = "12290";
    String ALERT_VIRBRATE = "12291";
    String ALERT_MOVE = "12292";
    String ALERT_ALARM = "12293";
    String ALERT_INVALIDRUN = "12294";
    String ALERT_ENTERGEO = "12295";
    String ALERT_EXITGEO = "12296";
    String ALERT_CUTPOWER = "12297";
    String ALERT_LOWPOWER = "12298";
    String ALERT_GPSCUT = "12299";
    String ALERT_OVERDRIVE = "12300";
    String ALERT_INVALIDACC = "12301";
    String ALERT_INVALIDDOOR = "12302";    
    public String getUniAlertsDesc(JSONArray jsonArray){
    	String str = "";
    	for(int i = 0 ; i < jsonArray.length() ; i++){
    		try {
    			String jsonString = jsonArray.getString(i);
    			if(jsonString.equals(ALERT_SOS)){
    				str += "��������,";
    			}else if(jsonString.equals(ALERT_OVERSPEED)){
    				str += "���ٱ���,";
    			}else if(jsonString.equals(ALERT_VIRBRATE)){
    				str += "�𶯱���,";
    			}else if(jsonString.equals(ALERT_MOVE)){
    				str += "λ�Ʊ���,";
    			}else if(jsonString.equals(ALERT_ALARM)){
    				str += "����������,";
    			}else if(jsonString.equals(ALERT_INVALIDRUN)){
    				str += "�Ƿ���ʻ����,";
    			}else if(jsonString.equals(ALERT_ENTERGEO)){
    				str += "��Χ������,";
    			}else if(jsonString.equals(ALERT_EXITGEO)){
    				str += "��Χ������,";
    			}else if(jsonString.equals(ALERT_CUTPOWER)){
    				str += "���߱���,";
    			}else if(jsonString.equals(ALERT_LOWPOWER)){
    				str += "�͵�ѹ����,";
    			}else if(jsonString.equals(ALERT_GPSCUT)){
    				str += "GPS���߱���,";
    			}else if(jsonString.equals(ALERT_OVERDRIVE)){
    				str += "ƣ�ͼ�ʻ����,";
    			}else if(jsonString.equals(ALERT_INVALIDACC)){
    				str += "�Ƿ���𱨾�,";
    			}else if(jsonString.equals(ALERT_INVALIDDOOR)){
    				str += "�Ƿ����ű���,";
    			}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return str;
    }
    /**
     * ��ȡδ����Ϣ��Ŀ
     * @param str
     */
    public void doUnReadMsg(String str){
    	if(str.equals("")){
    		iv_UnReadMsg.setVisibility(View.GONE);
			iv_perfrenece_UnReadMsg.setVisibility(View.GONE);
    	}else{
    		try {
    			JSONObject jsonObject = new JSONObject(str);
    			int count = Integer.valueOf(jsonObject.getString("count"));
    			if (count > 0) {
    				iv_UnReadMsg.setVisibility(View.VISIBLE);
    				iv_perfrenece_UnReadMsg.setVisibility(View.VISIBLE);
    			}else{
    				iv_UnReadMsg.setVisibility(View.GONE);
    				iv_perfrenece_UnReadMsg.setVisibility(View.GONE);
    			}
    		} catch (JSONException e) {
    			iv_UnReadMsg.setVisibility(View.GONE);
				iv_perfrenece_UnReadMsg.setVisibility(View.GONE);
				Log.e(TAG, "��ȡδ����Ϣ�����쳣");
    		}
    	}
    }
    /**
     * Notifacation ����
     * @param icon
     * @param tickertext
     * @param title
     * @param content
     */
    public void showNotification(int icon,String tickertext,String title,String content){
    	nm.cancel(notification_id);
    	Notification notification = new Notification();
    	notification.icon = icon;
    	notification.tickerText = tickertext;
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	notification.defaults |= Notification.DEFAULT_SOUND;
    	Intent notificationIntent =new Intent(MainActivity.this, AaActivity.class); // �����֪ͨ��Ҫ��ת��Activity
    	PendingIntent contentItent = PendingIntent.getActivity(this, 0, notificationIntent, 0);    	
    	notification.setLatestEventInfo(MainActivity.this, title, content, contentItent);
    	nm.notify(notification_id, notification);
    }
	/**
	 * �ײ�״̬�����
	 * @param �������Ŀ
	 */
	private void bg_set(int i){
		iv_map.setImageDrawable(getResources().getDrawable(R.drawable.car_search));
		iv_control.setImageDrawable(getResources().getDrawable(R.drawable.sms));
		iv_pref.setImageDrawable(getResources().getDrawable(R.drawable.more));
		switch (i) {
		case 0:
			iv_control.setImageDrawable(getResources().getDrawable(R.drawable.sms_focuse));
			break;
		case 1:
			iv_map.setImageDrawable(getResources().getDrawable(R.drawable.car_search_focuse));
			break;
		case 2:
			iv_pref.setImageDrawable(getResources().getDrawable(R.drawable.more_focuse));
			break;
		}
	}
}