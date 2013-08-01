package com.wise.asd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.BaseClass.GetSystem;
import com.wise.BaseClass.NetThread;
import com.wise.Data.CarInfoData;
import com.wise.Parameter.Config;
import com.wise.bluetoothUtil.BluetoothServerService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
/**
 * 车辆设置,终端参数页面
 * @author honesty
 *
 */
public class CarActivity extends Activity{	
	private final String TAG = "CarActivity";
	private final static int GET_CARINFO = 1;	//获取车辆信息
	private final static int SAVE_CARINFO = 2; //保存
	private final static int SEND_CMD = 3;     //发送指令
	private final static int REFRESH = 4 ;     //刷新车辆列表
	private final static int DELETE = 5 ;     //删除对应车辆
	private final static int verification_serial = 6; //验证serial
	private final static int REFRESH_CARINFO = 7; //刷新车辆信息
	private final static int CMD_AUTOLOCK_ = 8 ;     //发送行车落锁指令
	private final static int SEND_COMMAND = 9;
	private final static int DELETE_COMMAND = 10 ;
	private final static int GET_AUTH_CODE = 11;
	private final static int update_main = 12; //更新主服务器信息
	private final static int cmd_repair_open = 13;
	private final static int cmd_repair_close = 14;
	
	public static final String ACCESSORY_FUEL = "20481"; //油耗传感器
	public static final String ACCESSORY_ODB = "20483"; //ODB附件
	public static final String ACCESSORY_ENGINE = "20486"; //远程启动附件
	public static final String ACCESSORY_PKE = "20487"; //远程启动附件,PKE
	public static final String ACCESSORY_LOCKBOX = "20488"; //防拆盒
	
	public static final String COMMAND_AUTOLOCKON = "16418"; //行车自动落锁开
	public static final String COMMAND_AUTOLOCKOFF = "16419"; //行车自动落锁关
	public static final String vibration = "16391"; //震动报警灵敏度
	public static final String COMMAND_SLAVE = "16435"; //副号设置
	public static final String REPAIR_OPEN = "16437";
	public static final String REPAIR_CLOSE = "16438";
	
	
	EditText et_car_info_maintenance,et_car_info_yearcheck_time,et_car_info_insurance_time,et_service_end,
			et_car_info_mileage,et_car_name,et_car_info_serial,et_car_info_SIM,et_car_info_deputy;
	CheckBox cb_car_info_yearcheck,cb_car_info_insurance,cb_car_info_mileage,cb_car_info_autolock,cb_car_info_Repair;
	TableRow tr_car_info_yearcheck,tr_car_info_insurance,tr_car_info_maintenance,
				tr_terminal_info,tr_terminal_other,tr_terminal_sensitivity,tr_deputy,tr_bluethooth;
	ImageView iv_car_info_yearcheck_time,iv_car_info_insurance_time;
	TextView tv_terminal_info,tv_terminal_other;
	Button bt_car_info_deputy;
	ScrollView sv_car_info;
	ProgressDialog Dialog = null;    //等待框    
	
	CarInfoData carInfoData;
	int index;  
	NotificationManager nm ;
	int notification_id=19172439;
	boolean isChangeCarName = false;    //是否添加/删除了车辆，添加返回刷新主界面的spinner
	String Device_id = "";
	String serial = "";
	String rcv_time = "";
	String accessory;
	String Business_auth_code,obj_id;
	
	List<String> list;
	ArrayAdapter<String> adapter_carRegnum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();		
	}	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_CARINFO:
				if(Dialog != null){
					Dialog.dismiss();
				}
				jsonCarInfo(msg.obj.toString());
				break;
			case SAVE_CARINFO:
				if(Dialog != null){
					Dialog.dismiss();
				}
				Log.d(TAG, "业务" + msg.obj.toString());
				Toast.makeText(getApplicationContext(), R.string.save_ok, Toast.LENGTH_SHORT).show();
				break;
			case SEND_CMD:
				//nm.cancel(notification_id);
				//new Thread(new getSensitivityThread(index,sensitivity,Business_auth_code)).start();
				break;
			case CMD_AUTOLOCK_:
				new Thread(new getAUTOLOCKThread(index, cb_car_info_autolock.isChecked(),Business_auth_code)).start();
				break;
			case REFRESH:
				if(Dialog != null){
					Dialog.dismiss();
				}
				Config.carDatas = GetSystem.JsonCarData(CarActivity.this,msg.obj.toString());
				BindSpinner();
				break;
			case DELETE:
				if(Dialog != null){
					Dialog.dismiss();
				}
				isChangeCarName = true;
				//删除对应的车辆
				Config.carDatas.remove(index);
				BindSpinner();
				break;
			case verification_serial:
				VerificationSerial(msg.obj.toString());
				break;
			case REFRESH_CARINFO:  //获取里程
				getMileage(msg);
				break;
			case SEND_COMMAND:
				Log.d(TAG, msg.obj.toString());
				break;
			case DELETE_COMMAND:
				Log.d(TAG, msg.obj.toString());
				break;
			case GET_AUTH_CODE:
				//获取车辆信息
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					Business_auth_code = jsonObject.getString("auth_code");
					obj_id = jsonObject.getString("obj_id");
					String url = Config.carDatas.get(index).getUrl() + "vehicle/" + obj_id + "?auth_code=" + Business_auth_code;
					new Thread(new NetThread.GetDataThread(handler, url, GET_CARINFO)).start();
				} catch (JSONException e) {
					e.printStackTrace();
				}				
				break;
			case update_main:
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					if(jsonObject.getString("status_code").equals("0")){
						//TODO adapter_carRegnum
						String obj_nameString = et_car_name.getText().toString();
						if(!Config.carDatas.get(index).getObj_name().equals(obj_nameString)){
							Config.carDatas.get(index).setObj_name(obj_nameString);
							list.set(index, obj_nameString);
							adapter_carRegnum.notifyDataSetChanged();
							isChangeCarName = true;
						}
						Toast.makeText(getApplicationContext(), R.string.save_ok, Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "数据保存失败", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "数据保存失败", Toast.LENGTH_SHORT).show();
				}
				break;
			case cmd_repair_close:
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					if(jsonObject.getString("status_code").equals("0")){
						Toast.makeText(getApplicationContext(), "修车模式关", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case cmd_repair_open:
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					if(jsonObject.getString("status_code").equals("0")){
						Toast.makeText(getApplicationContext(), "修车模式开", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}		
	};
	private void VerificationSerial(String json){
		if(json.equals("")){
			Toast.makeText(getApplicationContext(), R.string.not_Barcode, Toast.LENGTH_SHORT).show();
		}else{
			//解析数据
			try {
				JSONObject jsonObject = new JSONObject(json);
				JSONObject json1 = jsonObject.getJSONObject("pre_device");
				String registered = json1.getString("registered");
				if(registered.equals("0")){
					//未注册终端
					Device_id = json1.getString("imei");
					serial = json1.getString("serial");
				}else{
					Toast.makeText(getApplicationContext(), R.string.gps_registered, Toast.LENGTH_SHORT).show();
				}						
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 解析里程
	 * @param msg
	 */
	private void getMileage(Message msg){
		try {
			//Log.d(TAG, "车辆GPS信息=" + msg.obj.toString());
			if(msg.obj.toString().equals("")){
				
			}else{
				JSONObject jsonObject = new JSONObject(msg.obj.toString());
				JSONObject jsonData = jsonObject.getJSONObject("active_gps_data");
        		try {
					String mileage = jsonData.getString("mileage");
					rcv_time = GetSystem.ChangeTime(jsonData.getString("rcv_time"),0);
					et_car_info_mileage.setText(mileage);
				} catch (Exception e) {
					et_car_info_mileage.setText("");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 绑定spinner控件
	 */
	private void BindSpinner(){
		list = new ArrayList<String>();
		for(int i = 0 ; i < Config.carDatas.size() ; i++){
			list.add(Config.carDatas.get(i).getObj_name());
		}
		if(list.size() > 0){
			Spinner s_car_info_car = (Spinner)findViewById(R.id.s_car_info_car);
			adapter_carRegnum = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
			adapter_carRegnum.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 设置下拉列表的风格
			s_car_info_car.setAdapter(adapter_carRegnum);
			s_car_info_car.setOnItemSelectedListener(onItemSelectedListener);
			sv_car_info.setVisibility(View.VISIBLE);
		}else{
			sv_car_info.setVisibility(View.GONE);
		}
	}
	OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			ClearText();
			carInfoData = new CarInfoData();
			index = arg2;
			Dialog = ProgressDialog.show(CarActivity.this,getString(R.string.Note),getString(R.string.get_data),true);	
			
			//获取车辆里程
			//String url1 = Config.GetUrl + "vehicle/" + Config.carDatas.get(index).obj_id + "/active_gps_data?auth_code=" + Config.auth_code;
			//Log.d(TAG, url1);
			//new Thread(new NetThread.GetDataThread(handler, url1, REFRESH_CARINFO)).start();
			//读取车辆的auth_code和obj_id
			String url = Config.carDatas.get(index).getUrl() + "app_login?username=" + Config.account + "&password=" + GetSystem.getM5DEndo(Config.pwd) +"&mac=" + GetSystem.getMacAddress(getApplicationContext()) + "&serial=" + Config.carDatas.get(index).getSerial();
			Log.d(TAG, url);
			new Thread(new NetThread.GetDataThread(handler, url, GET_AUTH_CODE)).start();
		}
		public void onNothingSelected(AdapterView<?> arg0) {}
	};
	/**
	 * 切换车辆时先清空数据
	 */
	private void ClearText(){
		et_car_info_maintenance.setText("");
		et_car_info_yearcheck_time.setText("");
		et_car_info_insurance_time.setText("");
		et_car_info_mileage.setText("");
		et_car_name.setText("");
		et_car_info_SIM.setText("");
		et_service_end.setText("");
		
		cb_car_info_yearcheck.setChecked(false);
		cb_car_info_insurance.setChecked(false);
		cb_car_info_mileage.setChecked(false);
		
		tr_car_info_yearcheck.setVisibility(View.GONE);
		tr_car_info_insurance.setVisibility(View.GONE);
		tr_car_info_maintenance.setVisibility(View.GONE);
		tr_terminal_info.setVisibility(View.GONE);
		tr_terminal_other.setVisibility(View.GONE);
		tr_terminal_sensitivity.setVisibility(View.GONE);
		tv_terminal_other.setText("");
		tv_terminal_info.setText("");
		
		Device_id = "";
		serial = "";
		rcv_time = "";
	}
	
	OnClickListener onClickListener = new OnClickListener() {		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_car_back:
				finish();
				break;
			case R.id.bt_add_phones:
				Intent intent2 = new Intent(getApplicationContext(), PhoneActivity.class);
				intent2.putExtra("call", carInfoData.getCall_phones());
				startActivityForResult(intent2, 1);
				break;
			case R.id.bt_car_info_sim:
				//判断serial是否存在
				String serial1 = et_car_info_serial.getText().toString();
				if(serial1.equals("")){
					
				}else{
					String url = Config.carDatas.get(index).getUrl() + "pre_reg/search?auth_code=" + Business_auth_code + "&serial=" + serial1;
					new Thread(new NetThread.GetDataThread(handler, url, verification_serial)).start();
				}
				break;
			case R.id.bt_car_info_deputy://设置副号
				setDeputy();
				break;
			case R.id.bt_car_save:
				if(Config.carDatas.size() == 0){
					return;
				}
				String obj_name = et_car_name.getText().toString();
				//年检
				String annual_inspect_alert = cb_car_info_yearcheck.isChecked()?"1":"0";
				String annual_inspect_date = et_car_info_yearcheck_time.getText().toString();
				//保险
				String insurance_alert = cb_car_info_insurance.isChecked()?"1":"0";
				String insurance_date = et_car_info_insurance_time.getText().toString();
				//保养
				String maintain_alert = cb_car_info_mileage.isChecked()?"1":"0";
				String maintain_mileage = et_car_info_maintenance.getText().toString();
				if(obj_name.equals("")){
					Toast.makeText(getApplicationContext(), R.string.not_car_id, Toast.LENGTH_SHORT).show();
					return;
				}
				if(annual_inspect_alert.equals("1")&&annual_inspect_date.equals("")){
					Toast.makeText(getApplicationContext(), R.string.not_annual_inspect, Toast.LENGTH_SHORT).show();
					return;
				}
				if(insurance_alert.equals("1")&&insurance_date.equals("")){
					Toast.makeText(getApplicationContext(), R.string.not_insurance, Toast.LENGTH_SHORT).show();
					return;
				}
				if(maintain_alert.equals("1")&&maintain_mileage.equals("")){
					Toast.makeText(getApplicationContext(), R.string.not_mileage, Toast.LENGTH_SHORT).show();
					return;
				}
				String DeviceId;
				String serialString;
				if(Device_id == null){
					DeviceId = carInfoData.getDevice_id();
					serialString = carInfoData.getSerial();
				}else{
					DeviceId = Device_id;
					serialString = serial;
				}
				try {
					Dialog = ProgressDialog.show(CarActivity.this,getString(R.string.Note),getString(R.string.save_data),true);	
					String url = Config.carDatas.get(index).getUrl() + "vehicle/" + obj_id + "?auth_code=" + Business_auth_code;
					Log.d(TAG, "url=" + url);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("obj_id", obj_id));
					params.add(new BasicNameValuePair("obj_name", obj_name));
					params.add(new BasicNameValuePair("annual_inspect_alert", annual_inspect_alert));
					params.add(new BasicNameValuePair("annual_inspect_date", GetSystem.CreateTime(annual_inspect_date)));
					params.add(new BasicNameValuePair("insurance_alert", insurance_alert));
					params.add(new BasicNameValuePair("insurance_date", GetSystem.CreateTime(insurance_date)));
					params.add(new BasicNameValuePair("maintain_alert", maintain_alert));
					params.add(new BasicNameValuePair("maintain_mileage", maintain_mileage));
					params.add(new BasicNameValuePair("reg_rule", "{}"));
					params.add(new BasicNameValuePair("service_end_date", GetSystem.CreateTime(carInfoData.getService_end_date())));
					params.add(new BasicNameValuePair("obj_type", "1"));
					params.add(new BasicNameValuePair("device_id", DeviceId));
					params.add(new BasicNameValuePair("serial", serialString));
					params.add(new BasicNameValuePair("sim", et_car_info_SIM.getText().toString()));
					params.add(new BasicNameValuePair("sim_type", carInfoData.getSim_type()));
					params.add(new BasicNameValuePair("mobile_operator", ""));
					params.add(new BasicNameValuePair("op_mobile", ""));
					params.add(new BasicNameValuePair("op_mobile2", ""));
					params.add(new BasicNameValuePair("brand", "1"));
					params.add(new BasicNameValuePair("mdt_type", "0"));
					params.add(new BasicNameValuePair("call_phones", carInfoData.getCall_phones()));
					params.add(new BasicNameValuePair("sms_phones", "[]"));
					
					String teString = "obj_id=" + obj_id + ",obj_name=" + obj_name
										+ ",annual_inspect_alert = " + annual_inspect_alert + ",annual_inspect_date =" + GetSystem.CreateTime(annual_inspect_date)
										+ ",insurance_alert = " + insurance_alert + ",insurance_date = " + GetSystem.CreateTime(insurance_date)
										+ ",maintain_alert = " + maintain_alert + ",maintain_mileage = " + maintain_mileage + ",reg_rule={}"
										+ ",service_end_date=" + GetSystem.CreateTime(carInfoData.getService_end_date()) + ",obj_type =1"
										+ ",device_id =" + carInfoData.getDevice_id() + ",serial =" + carInfoData.getSerial()
										+ ",sim=" + carInfoData.getSim() + ",sim_type = " + carInfoData.getSim_type() + ",brand=1"
										+ ",call_phones = " + carInfoData.getCall_phones().replaceAll("\"phone\":", "phone:").replaceAll("\"name\":", "name:");
					Log.d(TAG, teString);
					//TODO 修改中心服务器
					new Thread(new NetThread.putDataThread(handler, url, params, SAVE_CARINFO)).start();
					String mainUrl = Config.URL + "vehicle/" + Config.carDatas.get(index).getObj_id() + "?auth_code=" + Config.auth_code;
					//String mainUrl = "http://121.34.147.38:4000/" + "vehicle/" + Config.carDatas.get(index).getObj_id() + "?auth_code=" + Config.auth_code;
					Log.d(TAG, "mainUrl=" + mainUrl);
					List<NameValuePair> params1 = new ArrayList<NameValuePair>();
					params1.add(new BasicNameValuePair("obj_id", Config.carDatas.get(index).getObj_id()));
					params1.add(new BasicNameValuePair("obj_name", obj_name));
					params1.add(new BasicNameValuePair("annual_inspect_alert", annual_inspect_alert));
					params1.add(new BasicNameValuePair("annual_inspect_date", GetSystem.CreateTime(annual_inspect_date)));
					params1.add(new BasicNameValuePair("insurance_alert", insurance_alert));
					params1.add(new BasicNameValuePair("insurance_date", GetSystem.CreateTime(insurance_date)));
					params1.add(new BasicNameValuePair("maintain_alert", maintain_alert));
					params1.add(new BasicNameValuePair("maintain_mileage", maintain_mileage));
					params1.add(new BasicNameValuePair("reg_rule", "{}"));
					params1.add(new BasicNameValuePair("service_end_date", GetSystem.CreateTime(carInfoData.getService_end_date())));
					params1.add(new BasicNameValuePair("obj_type", "1"));
					params1.add(new BasicNameValuePair("device_id", DeviceId));
					params1.add(new BasicNameValuePair("serial", serialString));
					params1.add(new BasicNameValuePair("sim", et_car_info_SIM.getText().toString()));
					params1.add(new BasicNameValuePair("sim_type", carInfoData.getSim_type()));
					params1.add(new BasicNameValuePair("mobile_operator", ""));
					params1.add(new BasicNameValuePair("op_mobile", ""));
					params1.add(new BasicNameValuePair("op_mobile2", ""));
					params1.add(new BasicNameValuePair("brand", "1"));
					params1.add(new BasicNameValuePair("mdt_type", "0"));
					params1.add(new BasicNameValuePair("call_phones", carInfoData.getCall_phones()));
					params1.add(new BasicNameValuePair("sms_phones", "[]"));
					new Thread(new NetThread.putDataThread(handler, mainUrl, params1, update_main)).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					//rcv_time
					if(!isOffine()){
						boolean is_repair = cb_car_info_Repair.isChecked();
						System.out.println("is_repair="+is_repair+","+carInfoData.isIs_repair());
						if(is_repair != carInfoData.isIs_repair()){
							if(is_repair){
								System.out.println("修车模式");
								SendCmd(REPAIR_OPEN,cmd_repair_open,"{}");
							}else{
								SendCmd(REPAIR_CLOSE,cmd_repair_close,"{}");
							}
						}
					}					
				} catch (Exception e) {
					e.printStackTrace();
				}
				//行车自动落锁判断 
				String autolock = "false";
				if(cb_car_info_autolock.isChecked()){
					autolock = "true";
				}
				if(!carInfoData.getIs_autolock().equals(autolock)){
					//发送指令
					if(cb_car_info_autolock.isChecked()){
						SendCmd(COMMAND_AUTOLOCKON,CMD_AUTOLOCK_,"{}");
					}else{
						SendCmd(COMMAND_AUTOLOCKOFF,CMD_AUTOLOCK_,"{}");
					}
				}
				break;
			case R.id.iv_car_info_yearcheck_time:
				showDateDialog(1);
				break;
			case R.id.iv_car_info_insurance_time:
				showDateDialog(2);
				break;
			case R.id.bt_carinfo_reset:
				String mileage = et_car_info_mileage.getText().toString();
				break;
			case R.id.bt_set_bluetooth:
				Intent intent3 = new Intent(getApplicationContext(), BluetoothActivity.class);
				startActivity(intent3);
				break;
			}
		}
	};
	
	/**
	 * 发送指令
	 * @param cmd_type  指令编码
	 * @param cmd 发送成功后handler接受
	 */
	private void SendCmd(String cmd_type,int cmd,String parms){	
		try {//发送指令
			String url = Config.carDatas.get(index).getUrl() + "command?auth_code=" + Business_auth_code;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("obj_id", obj_id));
			params.add(new BasicNameValuePair("cmd_type", cmd_type));
			params.add(new BasicNameValuePair("params", parms));
			new Thread(new NetThread.postDataThread(handler, url, params, cmd)).start();
			Log.d(TAG, url);
			Log.d(TAG, obj_id);
			Log.d(TAG, cmd_type);
			Log.d(TAG, parms);
		} catch (Exception e) {
			Log.d(TAG, "发送指令异常");
		}
	}
	
	/**
	 * 设备是否离线
	 * @return
	 */
	private boolean isOffine(){
		return false;
//		Log.d(TAG, "rcv_time="+rcv_time);
//		if(rcv_time == null || rcv_time.equals("")){
//			Toast.makeText(getApplicationContext(), "设备离线", Toast.LENGTH_SHORT).show();
//			return true;
//		}
//		long time = GetSystem.GetTimeDiff(rcv_time);
//		if(time>=10){
//			Toast.makeText(getApplicationContext(), R.string.car_offine, Toast.LENGTH_SHORT).show();
//			return true;
//		}else{
//			return false;
//		}
	}
	
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {		
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.cb_car_info_yearcheck:
				if(isChecked){					
					tr_car_info_yearcheck.setVisibility(View.VISIBLE);
				}else{
					tr_car_info_yearcheck.setVisibility(View.GONE);
				}
				break;
			case R.id.cb_car_info_insurance:
				if(isChecked){
					tr_car_info_insurance.setVisibility(View.VISIBLE);
				}else{
					tr_car_info_insurance.setVisibility(View.GONE);
				}
				break;
			case R.id.cb_car_info_mileage:
				if(isChecked){
					tr_car_info_maintenance.setVisibility(View.VISIBLE);
				}else{
					tr_car_info_maintenance.setVisibility(View.GONE);
				}
				break;
			}
		}
	};	
	/**
	 * 解析车辆信息
	 * @param str
	 */
	private void jsonCarInfo(String str){
		Log.d(TAG, str);
		try {
			JSONObject jsonObject = new JSONObject(str);
			if(jsonObject.opt("accessory") == null){
				tv_terminal_other.setText("");
			}else{
				accessory = jsonObject.getString("accessory");
				if (accessory.equals(ACCESSORY_FUEL)) {
					tv_terminal_other.setText("油耗传感器");
				}else if (accessory.equals(ACCESSORY_ODB)) {
					tv_terminal_other.setText("ODB附件");
				}else if (accessory.equals(ACCESSORY_ENGINE)) {
					tv_terminal_other.setText("远程启动附件");
				}else if(accessory.equals(ACCESSORY_PKE)){
					tv_terminal_other.setText("带PKE的远程启动附件");
				}else if (accessory.equals(ACCESSORY_LOCKBOX)) {
					tv_terminal_other.setText("防拆盒");
				}
			}
			//服务到期日期
			et_service_end.setText(GetSystem.ChangeTime(jsonObject.getString("service_end_date"),1));
			//是否年检提醒
			if(jsonObject.opt("annual_inspect_alert") == null){
				cb_car_info_yearcheck.setChecked(false);
				tr_car_info_yearcheck.setVisibility(View.GONE);
				et_car_info_yearcheck_time.setText("");
			}else{
				String Annual_inspect_alert = jsonObject.getString("annual_inspect_alert");
				carInfoData.setAnnual_inspect_alert(Annual_inspect_alert);
				if(Annual_inspect_alert.equals("true")){
					cb_car_info_yearcheck.setChecked(true);
					tr_car_info_yearcheck.setVisibility(View.VISIBLE);
					//年检时间
					if(jsonObject.opt("annual_inspect_date") == null){
						et_car_info_yearcheck_time.setText("");
					}else{
						String Annual_inspect_date = GetSystem.ChangeTime(jsonObject.getString("annual_inspect_date"),1);
						carInfoData.setAnnual_inspect_date(Annual_inspect_date);
						et_car_info_yearcheck_time.setText(Annual_inspect_date);
					}
				}else{
					cb_car_info_yearcheck.setChecked(false);
					tr_car_info_yearcheck.setVisibility(View.GONE);
					et_car_info_yearcheck_time.setText("");
				}
			}			
			
			//是否保险提醒
			if(jsonObject.opt("insurance_alert") == null){
				cb_car_info_insurance.setChecked(false);
				tr_car_info_insurance.setVisibility(View.GONE);
				et_car_info_insurance_time.setText("");
			}else{
				String Insurance_alert = jsonObject.getString("insurance_alert");
				carInfoData.setInsurance_alert(Insurance_alert);
				if(Insurance_alert.equals("true")){
					cb_car_info_insurance.setChecked(true);
					tr_car_info_insurance.setVisibility(View.VISIBLE);
					//保险日期
					if(jsonObject.opt("insurance_date") == null){
						et_car_info_insurance_time.setText("");
					}else{
						String Insurance_date = GetSystem.ChangeTime(jsonObject.getString("insurance_date"),1);
						carInfoData.setInsurance_date(Insurance_date);
						et_car_info_insurance_time.setText(Insurance_date);
					}
				}else{
					cb_car_info_insurance.setChecked(false);
					tr_car_info_insurance.setVisibility(View.GONE);
					et_car_info_insurance_time.setText("");
				}
			}			
			//保养提醒
			if(jsonObject.opt("maintain_alert") == null){
				cb_car_info_mileage.setChecked(false);
				tr_car_info_maintenance.setVisibility(View.GONE);
				et_car_info_maintenance.setText("");
			}else{
				String Maintain_alert = jsonObject.getString("maintain_alert");
				carInfoData.setMaintain_alert(Maintain_alert);
				if(Maintain_alert.equals("true")){
					cb_car_info_mileage.setChecked(true);
					tr_car_info_maintenance.setVisibility(View.VISIBLE);
					//保养里程
					if(jsonObject.opt("maintain_mileage") == null){
						carInfoData.setMaintain_milage("0");		
						et_car_info_maintenance.setText("0");	
					}else{
						if(jsonObject.getString("maintain_mileage").equals("null")){
							carInfoData.setMaintain_milage("0");		
							et_car_info_maintenance.setText("0");
						}else{
							carInfoData.setMaintain_milage(jsonObject.getString("maintain_mileage"));		
							et_car_info_maintenance.setText(jsonObject.getString("maintain_mileage"));
						}					
					}
				}else{
					cb_car_info_mileage.setChecked(false);
					tr_car_info_maintenance.setVisibility(View.GONE);
					et_car_info_maintenance.setText("");
				}
			}				
			try {
				carInfoData.setService_end_date(GetSystem.ChangeTime(jsonObject.getString("service_end_date"),1));
			} catch (Exception e) {
				carInfoData.setService_end_date("");
			}
			try {
				carInfoData.setSerial(jsonObject.getString("serial"));
				et_car_info_serial.setText(jsonObject.getString("serial"));
			} catch (Exception e) {
				carInfoData.setSerial("");
				et_car_info_serial.setText("");
			}
			JSONObject jsonObjectParams = jsonObject.getJSONObject("params");
			//维修模式
			if(jsonObjectParams.opt("is_repair") == null){
				carInfoData.setIs_repair(false);
			}else{
				if(jsonObjectParams.getString("is_repair").equals("true")){
					carInfoData.setIs_repair(true);
				}else{
					carInfoData.setIs_repair(false);
				}
			}
			try {
				JSONObject jsonObject2 = jsonObject.getJSONObject("params");
				int sensitivity = Integer.valueOf(jsonObject2.getString("sensitivity"));
				carInfoData.setSensitivity(sensitivity);
				
			} catch (Exception e) {
				carInfoData.setSerial("");
				carInfoData.setIs_autolock("false");
				cb_car_info_autolock.setChecked(false);
			}
			try {
				JSONObject jsonObject2 = jsonObject.getJSONObject("params");
				carInfoData.setIs_autolock(jsonObject2.getString("is_autolock"));
				if(carInfoData.getIs_autolock().equals("true")){
					cb_car_info_autolock.setChecked(true);
				}else{
					cb_car_info_autolock.setChecked(false);
				}
			} catch (Exception e) {
				carInfoData.setIs_autolock("true");
				cb_car_info_autolock.setChecked(true);
			}
			if(!jsonObject.getString("device_id").equals("")){
				tr_terminal_info.setVisibility(View.VISIBLE);
				tr_terminal_other.setVisibility(View.VISIBLE);
				tr_terminal_sensitivity.setVisibility(View.VISIBLE);
			}
			carInfoData.setCust_name(jsonObject.getString("cust_name"));
			carInfoData.setOp_mobile(jsonObject.getString("op_mobile"));			
			carInfoData.setSim(jsonObject.getString("sim"));
			et_car_info_SIM.setText(jsonObject.getString("sim"));
			carInfoData.setDevice_id(jsonObject.getString("device_id"));			
			carInfoData.setSim_type(jsonObject.getString("sim_type"));
			et_car_name.setText(jsonObject.getString("obj_name"));
			carInfoData.setCall_phones(jsonObject.getString("call_phones"));
			tv_terminal_info.setText(jsonObject.getJSONObject("reg_rule").getJSONObject("mdt_type").getString("mdt_name"));
			
			//判断accessory
			String op_mobile = jsonObject.getString("op_mobile");
			String op_mobile2 = jsonObject.getString("op_mobile2");
			if(accessory != null&&accessory.equals(ACCESSORY_PKE) && op_mobile.endsWith(Config.account)){
				tr_bluethooth.setVisibility(View.VISIBLE);
			}else{
				tr_bluethooth.setVisibility(View.GONE);
			}
			if(op_mobile2.equals(Config.account)){//副号
				tr_deputy.setVisibility(View.GONE);
			}else{//主号
				tr_deputy.setVisibility(View.VISIBLE);
				if(op_mobile2.equals("")){
					bt_car_info_deputy.setText("设置");
				}else{//清除副号
					bt_car_info_deputy.setText("清空");
					et_car_info_deputy.setText(op_mobile2);
				}
			}
			//是否开启蓝牙服务
			if(accessory != null&&accessory.equals(ACCESSORY_PKE)){
				Intent startService = new Intent(CarActivity.this, BluetoothServerService.class);
				startService(startService);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 设置副号
	 */
	private void setDeputy(){
		String deputy = et_car_info_deputy.getText().toString();
		if(deputy.equals("")){
			View view_Phone = LayoutInflater.from(CarActivity.this).inflate(R.layout.deputy_phone, null);
			final EditText et_deputy_phone = (EditText)view_Phone.findViewById(R.id.et_deputy_phone);
			AlertDialog.Builder addPhoneBuilder = new AlertDialog.Builder(CarActivity.this);
			addPhoneBuilder.setTitle(R.string.enter_deputy_phone);
			addPhoneBuilder.setView(view_Phone);
			addPhoneBuilder.setPositiveButton(R.string.Sure, new DialogInterface.OnClickListener() {					
				public void onClick(DialogInterface dialog, int which) {
					String phone = et_deputy_phone.getText().toString();
					if(phone.equals("")){
						Toast.makeText(CarActivity.this, R.string.not_complete_not, Toast.LENGTH_SHORT).show();
					}else{
						//发送设置副号指令 13316560478
						SendCmd(COMMAND_SLAVE, SEND_COMMAND,"{sim: \" " + phone + "\"}");
					}
				}
			});
			addPhoneBuilder.setNegativeButton(R.string.cancle, null);
			addPhoneBuilder.show();
		}else{
			new AlertDialog.Builder(CarActivity.this)
			.setTitle("提示")
			.setMessage("是否清空副号")
			.setNegativeButton("取消", null)
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					//发送清空副号指令
					SendCmd(COMMAND_SLAVE, DELETE_COMMAND,"{sim: \"\"}");
				}
			}).show();
		}
	}
	
	/**
     * 震动判断
     * @author honesty
     */
    class getSensitivityThread extends Thread{
    	int item;
    	int mySensitivity;
    	String auth_code;
    	public getSensitivityThread(int index, int sensitivity,String auth_code){
    		this.item = index;
    		this.mySensitivity = sensitivity;
    		this.auth_code = auth_code;
    	}
    	@Override
    	public void run() {
    		super.run();
    		for (int i = 0; i < 3; i++) {
				try {
					Log.d(TAG, "第" + i + "次循环");
					Thread.sleep(10000);
					String data = GetSystem.GetData(item,auth_code,obj_id);					
					JSONObject jsonObject = new JSONObject(data);
					try {
						JSONObject jsonObject2 = jsonObject.getJSONObject("params");
						int sensitivity = Integer.valueOf(jsonObject2.getString("sensitivity"));
						if(sensitivity == mySensitivity){
							showNotification(R.drawable.send_ok,"设置震动灵敏度成功","指令提示","设置震动灵敏度成功");
							break;
						}
					} catch (Exception e) {}
	    			//循环
	    			if(i == 2){
	    				Log.d(TAG, "open=error");
	    				showNotification(R.drawable.send_error,"设置震动灵敏度失败","指令提示","设置震动灵敏度失败");
    				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
    	}
    }
    
    class getAUTOLOCKThread extends Thread{
    	int item;
    	boolean is_AUTOLOCK;
    	String auth_code;
    	/**
    	 * @param index
    	 * @param is_AUTOLOCK true开，false关
    	 */
    	public getAUTOLOCKThread(int index,boolean is_AUTOLOCK,String auth_code){
    		this.item = index;
    		this.is_AUTOLOCK = is_AUTOLOCK;
    		this.auth_code = auth_code;
    	}
    	@Override
    	public void run() {
    		super.run();
    		for (int i = 0; i < 3; i++) {
				try {
					Log.d(TAG, "第" + i + "次循环");
					Thread.sleep(10000);
					String data = GetSystem.GetData(item,auth_code,obj_id);
					//判断
					JSONObject jsonObject1 = new JSONObject(data);
	    			JSONObject jsonObject = jsonObject1.getJSONObject("params");
    				try {
    					boolean is_sound_close = false;
						if(jsonObject.getString("is_autolock").equals("true")){
							is_sound_close = true;
						}
						if(is_sound_close == is_AUTOLOCK){
							if(is_AUTOLOCK){
								showNotification(R.drawable.send_ok,"打开行车自动落锁成功!","指令提示","打开行车自动落锁成功!");
							}else{
								showNotification(R.drawable.send_ok,"关闭行车自动落锁成功!","指令提示","关闭行车自动落锁成功!");
							}
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
	    			if(i == 2){
	    				if(is_AUTOLOCK){
							showNotification(R.drawable.send_ok,"打开行车自动落锁失败!","指令提示","打开行车自动落锁失败!");
						}else{
							showNotification(R.drawable.send_ok,"关闭行车自动落锁失败!","指令提示","关闭行车自动落锁失败!");
						}
    				}
				} catch (Exception e) {
					e.printStackTrace();					
				}
			}
    	}
    }
	
	public void showNotification(int icon,String tickertext,String title,String content){
		nm.cancel(notification_id);
    	Notification notification = new Notification();
    	notification.icon = icon;
    	notification.tickerText = tickertext;
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	notification.defaults |= Notification.DEFAULT_SOUND;
    	Intent notificationIntent =new Intent(CarActivity.this, AaActivity.class); // 点击该通知后要跳转的Activity
    	PendingIntent contentItent = PendingIntent.getActivity(this, 0, notificationIntent, 0);    	
    	notification.setLatestEventInfo(CarActivity.this, title, content, contentItent);
    	nm.notify(notification_id, notification);
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 1){
			Bundle bundle = data.getExtras();
			carInfoData.setCall_phones(bundle.getString("jsonPhone"));
		}else if(resultCode == 2){
			//读取车辆列表
			String url = Config.carDatas.get(index).getUrl() + "customer/" + Config.cust_id + "/vehicle?auth_code=" + Business_auth_code + "&tree_path=" + Config.tree_path + "&mode=all";
			new Thread(new NetThread.GetDataThread(handler, url, REFRESH)).start();
			isChangeCarName = true;
		}
	}	
	private void Back(){
		if(isChangeCarName){
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			Bundle bundle = new Bundle();
			intent.putExtras(bundle);
			setResult(1, intent);
		}
		finish();
	}
	private void init(){
		setContentView(R.layout.car_info);		
		sv_car_info = (ScrollView)findViewById(R.id.sv_car_info);
		
		iv_car_info_yearcheck_time = (ImageView)findViewById(R.id.iv_car_info_yearcheck_time);
		iv_car_info_yearcheck_time.setOnClickListener(onClickListener);
		iv_car_info_insurance_time = (ImageView)findViewById(R.id.iv_car_info_insurance_time);
		iv_car_info_insurance_time.setOnClickListener(onClickListener);
		
		et_car_info_yearcheck_time = (EditText)findViewById(R.id.et_car_info_yearcheck_time);
		et_car_info_yearcheck_time.setInputType(InputType.TYPE_NULL);
		et_car_info_insurance_time = (EditText)findViewById(R.id.et_car_info_insurance_time);
		et_car_info_insurance_time.setInputType(InputType.TYPE_NULL);
		et_car_info_mileage = (EditText)findViewById(R.id.et_car_info_mileage);
		et_car_name = (EditText)findViewById(R.id.et_car_name);
		et_car_info_SIM = (EditText)findViewById(R.id.et_car_info_SIM);
		et_car_info_serial = (EditText)findViewById(R.id.et_car_info_serial);
		et_car_info_maintenance = (EditText)findViewById(R.id.et_car_info_maintenance);
		et_car_info_deputy = (EditText)findViewById(R.id.et_car_info_deputy);
		et_service_end = (EditText)findViewById(R.id.et_service_end);
		
		cb_car_info_yearcheck = (CheckBox)findViewById(R.id.cb_car_info_yearcheck);
		cb_car_info_yearcheck.setOnCheckedChangeListener(onCheckedChangeListener);
		cb_car_info_insurance = (CheckBox)findViewById(R.id.cb_car_info_insurance);
		cb_car_info_insurance.setOnCheckedChangeListener(onCheckedChangeListener);
		cb_car_info_mileage = (CheckBox)findViewById(R.id.cb_car_info_mileage);
		cb_car_info_mileage.setOnCheckedChangeListener(onCheckedChangeListener);
		cb_car_info_autolock = (CheckBox)findViewById(R.id.cb_car_info_autolock);
		cb_car_info_Repair = (CheckBox)findViewById(R.id.cb_car_info_Repair);
		
		tr_car_info_yearcheck = (TableRow)findViewById(R.id.tr_car_info_yearcheck);
		tr_car_info_insurance = (TableRow)findViewById(R.id.tr_car_info_insurance);
		tr_car_info_maintenance = (TableRow)findViewById(R.id.tr_car_info_maintenance);
		tr_terminal_info = (TableRow)findViewById(R.id.tr_terminal_info);
		tr_terminal_other = (TableRow)findViewById(R.id.tr_terminal_other);
		tr_terminal_sensitivity = (TableRow)findViewById(R.id.tr_terminal_sensitivity);
		tr_deputy = (TableRow)findViewById(R.id.tr_deputy);
		tr_bluethooth = (TableRow)findViewById(R.id.tr_bluethooth);
		
		Button bt_car_back = (Button)findViewById(R.id.bt_car_back);
		bt_car_back.setOnClickListener(onClickListener);
		Button bt_car_save = (Button)findViewById(R.id.bt_car_save);
		bt_car_save.setOnClickListener(onClickListener);
		Button bt_carinfo_reset = (Button)findViewById(R.id.bt_carinfo_reset);
		bt_carinfo_reset.setOnClickListener(onClickListener);
		Button bt_add_phones = (Button)findViewById(R.id.bt_add_phones);
		bt_add_phones.setOnClickListener(onClickListener);
		Button bt_car_info_sim = (Button)findViewById(R.id.bt_car_info_sim);
		bt_car_info_sim.setOnClickListener(onClickListener);
		Button bt_set_bluetooth = (Button)findViewById(R.id.bt_set_bluetooth);
		bt_set_bluetooth.setOnClickListener(onClickListener);
		bt_car_info_deputy = (Button)findViewById(R.id.bt_car_info_deputy);
		bt_car_info_deputy.setOnClickListener(onClickListener);
		
		tv_terminal_info = (TextView)findViewById(R.id.tv_terminal_info);
		tv_terminal_other = (TextView)findViewById(R.id.tv_terminal_other);
		
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);		
		BindSpinner();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Back();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
     * Updates day wheel. Sets max days according to selected month and year
     */
    void updateDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(calendar.get(Calendar.YEAR) + year.getCurrentItem(), month.getCurrentItem(), 1);
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
        
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2012, 1, 3);
    }
    
    private void showDateDialog(final int where){
		View v = LayoutInflater.from(CarActivity.this).inflate(R.layout.data_wheel, null);
		Calendar calendar = Calendar.getInstance();
    	final int curYear = calendar.get(Calendar.YEAR);
        final WheelView month = (WheelView) v.findViewById(R.id.data_month);
        final WheelView year = (WheelView) v.findViewById(R.id.data_year);
        final WheelView day = (WheelView) v.findViewById(R.id.data_day);
        // month
        int curMonth = calendar.get(Calendar.MONTH);
        String months[] = new String[] {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        month.setViewAdapter(new DateArrayAdapter(this, months, curMonth));
        month.setCurrentItem(curMonth);    
        // year
        year.setViewAdapter(new DateNumericAdapter(this, curYear, curYear + 10, 0));
        year.setCurrentItem(curYear);        
        //day
        updateDays(year, month, day);
        day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
		
		AlertDialog.Builder addHoldBuilder = new AlertDialog.Builder(CarActivity.this);
		addHoldBuilder.setTitle("请输入日期");
		addHoldBuilder.setView(v);
		addHoldBuilder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String trueTime = (curYear +year.getCurrentItem()) + "-" + GetSystem.ChangeTime(month.getCurrentItem()+1) + "-" + GetSystem.ChangeTime(day.getCurrentItem()+1);
				GetDateTime(where,trueTime);
			}
		});
		addHoldBuilder.setNegativeButton("取消",null);
		addHoldBuilder.show();
	}
    private void GetDateTime(int which,String Time){
		switch (which) {
		case 1:
			et_car_info_yearcheck_time.setText(Time);
			break;
		case 2:
			et_car_info_insurance_time.setText(Time);
			break;
		}
	}    
    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;        
        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(16);
        }        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }    
    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;        
        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(16);
        }        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
}