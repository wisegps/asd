package com.wise.asd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import com.wise.BaseClass.GetSystem;
import com.wise.BaseClass.NetThread;
import com.wise.Parameter.Config;
import com.wise.asd.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class AddCarActivity extends Activity{
	private static final String TAG = "AddCarActivity";
	public static final int requestCode = 1;
	public static final int verification_serial = 2;
	public static final int save_car_info = 3;
	Button bt_car_add_phone,bt_car_save,bt_car_add_back,bt_car_add_sim;
	EditText et_add_Brand,et_add_car_id,et_car_add_yearcheck_time,et_car_add_insurance_time,et_car_add_serial,
				et_add_SIM,et_add_reset,et_add_next_maintenance;
	CheckBox cb_car_add_yearcheck,cb_car_add_insurance,cb_car_add_gps,cb_car_add_mileage;
	ImageView iv_car_add_yearcheck_time,iv_car_add_insurance_time;
	TableRow tr_car_add_yearcheck,tr_car_add_insurance,tr_car_add_serial,tr_car_add_sim,tr_car_add_reset,tr_car_add_mileage,tr_car_add_maintenance;
	String call_phones = "[]";
	String serial;
	String device_id ;
	ProgressDialog Dialog = null;    //等待框    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.car_add);
		bt_car_add_phone = (Button)findViewById(R.id.bt_car_add_phone);
		bt_car_add_phone.setOnClickListener(onClickListener);
		bt_car_save = (Button)findViewById(R.id.bt_car_save);
		bt_car_save.setOnClickListener(onClickListener);
		bt_car_add_back = (Button)findViewById(R.id.bt_car_add_back);
		bt_car_add_back.setOnClickListener(onClickListener);
		bt_car_add_sim = (Button)findViewById(R.id.bt_car_add_sim);
		bt_car_add_sim.setOnClickListener(onClickListener);
		iv_car_add_yearcheck_time = (ImageView)findViewById(R.id.iv_car_add_yearcheck_time);
		iv_car_add_yearcheck_time.setOnClickListener(onClickListener);
		iv_car_add_insurance_time = (ImageView)findViewById(R.id.iv_car_add_insurance_time);
		iv_car_add_insurance_time.setOnClickListener(onClickListener);
		
		et_add_Brand = (EditText)findViewById(R.id.et_add_Brand);
		et_add_car_id = (EditText)findViewById(R.id.et_add_car_id);
		et_car_add_yearcheck_time = (EditText)findViewById(R.id.et_car_add_yearcheck_time);
		et_car_add_insurance_time = (EditText)findViewById(R.id.et_car_add_insurance_time);
		et_car_add_serial = (EditText)findViewById(R.id.et_car_add_serial);
		et_add_SIM = (EditText)findViewById(R.id.et_add_SIM);
		et_add_reset = (EditText)findViewById(R.id.et_add_reset);
		et_add_next_maintenance = (EditText)findViewById(R.id.et_add_next_maintenance);
		
		cb_car_add_yearcheck = (CheckBox)findViewById(R.id.cb_car_add_yearcheck);
		cb_car_add_yearcheck.setOnCheckedChangeListener(onCheckedChangeListener);
		cb_car_add_insurance = (CheckBox)findViewById(R.id.cb_car_add_insurance);
		cb_car_add_insurance.setOnCheckedChangeListener(onCheckedChangeListener);
		cb_car_add_gps = (CheckBox)findViewById(R.id.cb_car_add_gps);
		cb_car_add_gps.setOnCheckedChangeListener(onCheckedChangeListener);
		cb_car_add_mileage = (CheckBox)findViewById(R.id.cb_car_add_mileage);	
		cb_car_add_mileage.setOnCheckedChangeListener(onCheckedChangeListener);
		
		tr_car_add_serial = (TableRow)findViewById(R.id.tr_car_add_serial);
		tr_car_add_sim = (TableRow)findViewById(R.id.tr_car_add_sim);
		tr_car_add_reset = (TableRow)findViewById(R.id.tr_car_add_reset);
		tr_car_add_mileage = (TableRow)findViewById(R.id.tr_car_add_mileage);
		tr_car_add_maintenance = (TableRow)findViewById(R.id.tr_car_add_maintenance);
		tr_car_add_yearcheck = (TableRow)findViewById(R.id.tr_car_add_yearcheck);
		tr_car_add_insurance = (TableRow)findViewById(R.id.tr_car_add_insurance);
		
		tr_car_add_serial.setVisibility(View.GONE);
		tr_car_add_sim.setVisibility(View.GONE);
		tr_car_add_mileage.setVisibility(View.GONE);
		tr_car_add_reset.setVisibility(View.GONE);
		tr_car_add_maintenance.setVisibility(View.GONE);
		tr_car_add_yearcheck.setVisibility(View.GONE);
		tr_car_add_insurance.setVisibility(View.GONE);
	}
	OnClickListener onClickListener = new OnClickListener() {		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_car_add_back:
				finish();
				break;
			case R.id.iv_car_add_yearcheck_time:
				showDateDialog(1);
				break;
			case R.id.iv_car_add_insurance_time:
				showDateDialog(2);
				break;
			case R.id.bt_car_add_phone:
				Intent intent = new Intent(getApplicationContext(),PhoneActivity.class);
				intent.putExtra("call", call_phones);
				startActivityForResult(intent, requestCode);
				break;
			case R.id.bt_car_add_sim:
				//判断账户是否存在
				String serial1 = et_car_add_serial.getText().toString();
				if(serial1.equals("")){
					Log.d(TAG, "未输入账号");
				}else{
					String url = Config.URL + "pre_reg/search?auth_code=" + Config.auth_code + "&serial=" + serial1;
					new Thread(new NetThread.GetDataThread(handler, url, verification_serial)).start();
				}
				break; 
			case R.id.bt_car_save:
				String obj_name = et_add_car_id.getText().toString();
				String brand = et_add_Brand.getText().toString();
				//年检
				String annual_inspect_alert = cb_car_add_yearcheck.isChecked()?"1":"0";
				String annual_inspect_date = et_car_add_yearcheck_time.getText().toString();
				//保险
				String insurance_alert = cb_car_add_insurance.isChecked()?"1":"0";
				String insurance_date = et_car_add_insurance_time.getText().toString();
				//保养
				String maintain_alert = cb_car_add_mileage.isChecked()?"1":"0";
				String maintain_mileage = et_add_next_maintenance.getText().toString();
				String sim = et_add_SIM.getText().toString();
				if(obj_name.equals("")){
					Toast.makeText(getApplicationContext(), R.string.not_car_id, Toast.LENGTH_SHORT).show();
					return;
				}
				if(annual_inspect_alert.equals("true")&&annual_inspect_date.equals("")){
					Toast.makeText(getApplicationContext(), R.string.not_annual_inspect, Toast.LENGTH_SHORT).show();
					return;
				}
				if(insurance_alert.equals("true")&&insurance_date.equals("")){
					Toast.makeText(getApplicationContext(), R.string.not_insurance, Toast.LENGTH_SHORT).show();
					return;
				}
				if(cb_car_add_gps.isChecked()){
					if(device_id == null || serial == null){
						Toast.makeText(getApplicationContext(), R.string.not_gpsinfo, Toast.LENGTH_SHORT).show();
						return;
					}
					if(maintain_alert.equals("true")&&maintain_mileage.equals("")){
						Toast.makeText(getApplicationContext(), R.string.not_gpsinfo, Toast.LENGTH_SHORT).show();
						return;
					}
				}
				try {
					String url = Config.URL + "vehicle?auth_code=" + Config.auth_code;
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("cust_id", Config.cust_id));
					params.add(new BasicNameValuePair("obj_name", obj_name));
					params.add(new BasicNameValuePair("annual_inspect_alert", annual_inspect_alert));
					params.add(new BasicNameValuePair("annual_inspect_date", GetSystem.CreateTime(annual_inspect_date)));
					params.add(new BasicNameValuePair("insurance_alert", insurance_alert));
					params.add(new BasicNameValuePair("insurance_date", GetSystem.CreateTime(insurance_date)));
					params.add(new BasicNameValuePair("maintain_alert", maintain_alert));
					params.add(new BasicNameValuePair("maintain_mileage", maintain_mileage));
					params.add(new BasicNameValuePair("reg_rule", "{}"));
					params.add(new BasicNameValuePair("service_end_date", ""));
					params.add(new BasicNameValuePair("obj_type", "1"));
					params.add(new BasicNameValuePair("device_id", device_id));
					params.add(new BasicNameValuePair("serial", serial));
					params.add(new BasicNameValuePair("sim", sim));
					params.add(new BasicNameValuePair("sim_type", "1"));
					params.add(new BasicNameValuePair("mobile_operator", ""));
					params.add(new BasicNameValuePair("op_mobile", ""));
					params.add(new BasicNameValuePair("op_mobile2", ""));
					params.add(new BasicNameValuePair("brand", "1"));
					params.add(new BasicNameValuePair("mdt_type", "0"));
					params.add(new BasicNameValuePair("call_phones", call_phones));
					params.add(new BasicNameValuePair("sms_phones", "[]"));
					new Thread(new NetThread.postDataThread(handler, url, params, save_car_info)).start();
					Dialog = ProgressDialog.show(AddCarActivity.this,getString(R.string.Note),getString(R.string.save_data),true);			
				} catch (Exception e) {
					e.printStackTrace();
				}
				String testString = "查看数据:cust_id=" + Config.cust_id + ",obj_name=" + obj_name + ",brand=1"
									+ ",annual_inspect_alert=" + annual_inspect_alert + ",annual_inspect_date=" + GetSystem.CreateTime(annual_inspect_date)
									+ ",insurance_alert = "  +insurance_alert + ",insurance_date=" + GetSystem.CreateTime(insurance_date)
									+ ",maintain_alert=" + maintain_alert + ",maintain_milage=" + maintain_mileage
									+ ",reg_rule={}" + ",service_end_date = " + ",obj_type=1"
									+ ",device_id =" + device_id + ",serial =" + serial + ",sim=" + sim
									+ ",call_phones =" + call_phones;
				Log.d(TAG, testString);
				break;
			}
		}
	};
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {		
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.cb_car_add_yearcheck:
				if(isChecked){
					tr_car_add_yearcheck.setVisibility(View.VISIBLE);
				}else{
					tr_car_add_yearcheck.setVisibility(View.GONE);
				}
				break;
			case R.id.cb_car_add_insurance:
				if(isChecked){
					tr_car_add_insurance.setVisibility(View.VISIBLE);
				}else{
					tr_car_add_insurance.setVisibility(View.GONE);
				}		
				break;
			case R.id.cb_car_add_gps:
				if(isChecked){
					tr_car_add_serial.setVisibility(View.VISIBLE);
				}else{
					tr_car_add_serial.setVisibility(View.GONE);
					tr_car_add_sim.setVisibility(View.GONE);
					tr_car_add_mileage.setVisibility(View.GONE);
					tr_car_add_reset.setVisibility(View.GONE);
					tr_car_add_maintenance.setVisibility(View.GONE);
				}
				break;
			case R.id.cb_car_add_mileage:
				if(isChecked){
					tr_car_add_maintenance.setVisibility(View.VISIBLE);
				}else{
					tr_car_add_maintenance.setVisibility(View.GONE);
				}
				break;
			}
		}
	};
	Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case verification_serial:
				String str = msg.obj.toString();
				Log.d(TAG, "return=" + str);
				if(str.equals("")){
					Toast.makeText(getApplicationContext(), R.string.not_Barcode, Toast.LENGTH_SHORT).show();
				}else{
					//解析数据
					try {
						JSONObject jsonObject = new JSONObject(str);
						JSONObject json = jsonObject.getJSONObject("pre_device");
						String registered = json.getString("registered");
						if(registered.equals("0")){
							//未注册终端
							device_id = json.getString("imei");
							serial = json.getString("serial");
							//得到imei号后显示其余选项
							tr_car_add_sim.setVisibility(View.VISIBLE);
							tr_car_add_mileage.setVisibility(View.VISIBLE);
							tr_car_add_reset.setVisibility(View.VISIBLE);
						}else{
							Toast.makeText(getApplicationContext(), R.string.gps_registered, Toast.LENGTH_SHORT).show();
						}						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case save_car_info:
				if(Dialog != null){
					Dialog.dismiss();
				}				
				Log.d(TAG, msg.obj.toString());
				Intent intent = new Intent(getApplicationContext(), AddCarActivity.class);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				setResult(2, intent);
				finish();
				break;
			}
		}		
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == requestCode){
			Bundle bundle = data.getExtras();
			call_phones = bundle.getString("jsonPhone");
		}
	};
	private void showDateDialog(final int where){
		View v = LayoutInflater.from(AddCarActivity.this).inflate(R.layout.data_wheel, null);
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
		
		AlertDialog.Builder addHoldBuilder = new AlertDialog.Builder(AddCarActivity.this);
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
			et_car_add_yearcheck_time.setText(Time);
			break;
		case 2:
			et_car_add_insurance_time.setText(Time);
			break;
		}
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
