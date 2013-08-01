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
import com.wise.Data.PersonData;
import com.wise.Parameter.Config;
import com.wise.asd.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class PersonActivity extends Activity{
	private final String TAG = "PersonActivity";
	ProgressDialog Dialog = null;    //等待框    
	
	private final static int GET_PERSON = 0;
	private final static int GET_parent = 1;
	private final static int update_person = 2;
	private final static int update_pwd = 3;
	private final static int update_mac = 4;//修改mac地址
	
	EditText et_person_info_sup,et_user_name,et_person_name,et_pwd,et_person_yearcheck,et_pwd_agian;
	CheckBox cb_person_yearcheck,cb_person_binding;
	Button bt_person_save,bt_person_back;
	ImageView iv_rep_person_time;
	TableRow tr_person_info_time, tr_user_name,tr_user_phone,tr_user_yearcheck;
	
	PersonData personData = new PersonData();
	boolean is_person = false;  //是否修改个人信息
	boolean is_pwd = false;		//是否修改密码
	boolean isBangDingMac = false; //是否绑定手机
	boolean isMac = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();	
		if(Config.number_type.equals("1")){//主号
			Dialog = ProgressDialog.show(PersonActivity.this,getString(R.string.Note),getString(R.string.get_data),true);			
			String url = Config.URL+ "customer/" + Config.cust_id + "?auth_code=" + Config.auth_code;
			new Thread(new NetThread.GetDataThread(handler, url, GET_PERSON)).start();
		}else{
			tr_user_name.setVisibility(View.GONE);
			tr_user_phone.setVisibility(View.GONE);
			tr_user_yearcheck.setVisibility(View.GONE);
		}
	}
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_PERSON:
				getPerson(msg);
				break;
			case GET_parent:
				if(Dialog != null){
					Dialog.dismiss();
				}
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					personData.setParent_name(jsonObject.getString("cust_name"));
					personData.setParent_tree_path(jsonObject.getString("tree_path"));						
				} catch (Exception e) {
					e.printStackTrace();
					personData.setParent_name("");
					personData.setParent_tree_path("");
				}
				break;
			case update_person:
				Log.d(TAG, "is_pwd=" + is_pwd);
				is_person = false;
				if(is_pwd == false){
					if(Dialog != null){
						Dialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), R.string.save_ok, Toast.LENGTH_SHORT).show();
				}
				break;
			case update_pwd:
				is_pwd = false;
				if(is_person == false){
					if(Dialog != null){
						Dialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), R.string.save_ok, Toast.LENGTH_SHORT).show();
				}
			case update_mac:
				Log.d(TAG, "MAC="+msg.toString());
				if(isBangDingMac){
					isMac = true;
					Toast.makeText(getApplicationContext(), "绑定手机成功", Toast.LENGTH_SHORT).show();
				}else{
					isMac = false;
					Toast.makeText(getApplicationContext(), "取消绑定成功", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}		
	};
	
	OnClickListener onClickListener = new OnClickListener() {		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_person_back:
				finish();
				break;
			case R.id.iv_rep_person_time:
				showDateDialog();
				break;
			case R.id.bt_person_save:
				save();
				break;
			}
		}
	};
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {		
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.cb_person_yearcheck:
				if(isChecked){
					tr_person_info_time.setVisibility(View.VISIBLE);
				}else{
					tr_person_info_time.setVisibility(View.GONE);
				}
				break;
			case R.id.cb_person_binding:
				if(isChecked){
					isBangDingMac = true;
				}else{
					isBangDingMac = false;
				}
				Log.d(TAG, "isBangDingMac="+isBangDingMac);
				break;
			}
			
		}
	};
	
	private void save(){
		//密码修改判断
		String password = et_pwd.getText().toString().trim();
		String againPassword = et_pwd_agian.getText().toString().trim();
		if(password.equals("") || againPassword.equals("")){
			Log.d(TAG, "不用修改密码");
		}else{
			if(!password.equals(againPassword)){
				Toast.makeText(getApplicationContext(), R.string.password_again_error, Toast.LENGTH_SHORT).show();
			}else{
				is_pwd = true;
				Log.d(TAG, "修改密码");
				//修改密码
				String urlString = Config.URL + "customer/user/password?auth_code=" + Config.auth_code + "&number_type="+Config.number_type;;
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("user_name",Config.account));
				params1.add(new BasicNameValuePair("old_password",GetSystem.getM5DEndo(Config.pwd)));
				params1.add(new BasicNameValuePair("new_password",GetSystem.getM5DEndo(password)));
				new Thread(new NetThread.GetDataThread(handler, urlString, update_pwd)).start();
			}
		}
		//个人信息修改判断
		if(Config.number_type.equals("1")){//主号
			String annual_inspect_alert;
			if(cb_person_yearcheck.isChecked()){
				annual_inspect_alert = "1";
			}else{
				annual_inspect_alert = "0";
			}
			String annual_inspect_date = et_person_yearcheck.getText().toString();
			String cust_name = et_user_name.getText().toString();
			String Contacter = et_person_name.getText().toString();
			if(annual_inspect_alert.equals(personData.annual_inspect_alert) && annual_inspect_date.equals(personData.annual_inspect_date) 
					&& cust_name.equals(personData.cust_name) && Contacter.equals(personData.contacter)){
				Log.d(TAG, "没有修改用户信息");
			}else{
				is_person = true;
				Log.d(TAG, "修改用户信息");				
				String teString = "cust_id=" + personData.getCust_id()
									+ ",cust_name=" + cust_name
									+ ",cust_type=" + personData.getCust_type()
									+ ",parent_cust_id=" + personData.getParent_cust_id()
									+ ",parent_tree_path=" + personData.getParent_tree_path()
									+ ",contacter=" + Contacter
									+ ",annual_inspect_alert =" + annual_inspect_alert
									+ ",annual_inspect_date =" + annual_inspect_date;
				Log.d(TAG, teString);					
				String url = Config.URL + "customer/" + Config.cust_id + "?auth_code=" + Config.auth_code + "&mode=simple";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("cust_name",cust_name));
				params.add(new BasicNameValuePair("cust_type",personData.getCust_type()));
				params.add(new BasicNameValuePair("parent_cust_id",personData.getParent_cust_id()));
				params.add(new BasicNameValuePair("contacter",Contacter));
				params.add(new BasicNameValuePair("annual_inspect_alert",annual_inspect_alert));
				params.add(new BasicNameValuePair("annual_inspect_date",annual_inspect_date));
				new Thread(new NetThread.putDataThread(handler, url, params, update_person)).start();
			}
		}		
		//如果有一个修改则显示
		if(is_person == true || is_pwd == true){
			Dialog = ProgressDialog.show(PersonActivity.this,getString(R.string.Note),getString(R.string.save_data),true);			
		}
		if(isMac != isBangDingMac){ //绑定状态变化
			//更新MAC
			String url = Config.URL + "customer/user/mac?auth_code=" + Config.auth_code + "&number_type="+Config.number_type;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_name",Config.account));
			if(isBangDingMac){
				params.add(new BasicNameValuePair("mac",GetSystem.getMacAddress(getApplicationContext())));
			}else{
				params.add(new BasicNameValuePair("mac",""));
			}			
			new Thread(new NetThread.putDataThread(handler, url, params, update_mac)).start();
		}		
	}
	/**
	 * 得到个人信息
	 * @param msg
	 */
	private void getPerson(Message msg){
		Log.d(TAG, msg.obj.toString());
		try {
			JSONObject jsonObject = new JSONObject(msg.obj.toString());
			if(jsonObject.opt("annual_inspect_alert") == null){
				personData.setAnnual_inspect_alert("0");
			}else{
				if(jsonObject.getString("annual_inspect_alert").equals("true")){
					personData.setAnnual_inspect_alert("1");
				}else{
					personData.setAnnual_inspect_alert("0");
				}
			}
			if(personData.getAnnual_inspect_alert().equals("0")){
				cb_person_yearcheck.setChecked(false);
				tr_person_info_time.setVisibility(View.GONE);
			}else{
				cb_person_yearcheck.setChecked(true);
				tr_person_info_time.setVisibility(View.VISIBLE);
			}
			if(jsonObject.opt("annual_inspect_date") == null){
				personData.setAnnual_inspect_date("");
			}else{
				personData.setAnnual_inspect_date(GetSystem.ChangeTime(jsonObject.getString("annual_inspect_date"),1));
				et_person_yearcheck.setText(GetSystem.ChangeTime(jsonObject.getString("annual_inspect_date"),1));
			}
			
			personData.setCust_name(jsonObject.getString("cust_name"));
			personData.setContacter(jsonObject.getString("contacter_tel"));
			et_user_name.setText(personData.getCust_name());
			et_person_name.setText(personData.getContacter());	
			personData.setCust_id(Config.cust_id);
			personData.setCust_type(jsonObject.getString("cust_type"));
			personData.setParent_cust_id(jsonObject.getString("parent_cust_id"));
			String url = Config.URL + "customer/" + jsonObject.getString("parent_cust_id") + "?auth_code=" + Config.auth_code;
			new Thread(new NetThread.GetDataThread(handler, url, GET_parent)).start();
			if(jsonObject.getJSONArray("users").getJSONObject(0).opt("mac") == null){
				Log.d(TAG, "没有绑定mac地址");
				isMac = false;
			}else{
				String mac = jsonObject.getJSONArray("users").getJSONObject(0).getString("mac");
				if(mac.equals("")){
					isMac = false;
				}
			}			
			if(isMac){
				cb_person_binding.setChecked(true);
			}else{
				cb_person_binding.setChecked(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(Dialog != null){
				Dialog.dismiss();
			}
		}
	}
	
	private void init(){
		setContentView(R.layout.person_info);
		bt_person_save = (Button)findViewById(R.id.bt_person_save);
		bt_person_save.setOnClickListener(onClickListener);
		bt_person_back = (Button)findViewById(R.id.bt_person_back);
		bt_person_back.setOnClickListener(onClickListener);
		iv_rep_person_time = (ImageView)findViewById(R.id.iv_rep_person_time);
		iv_rep_person_time.setOnClickListener(onClickListener);
		
		et_person_info_sup = (EditText)findViewById(R.id.et_person_info_sup);
		et_user_name = (EditText)findViewById(R.id.et_user_name);
		et_person_name = (EditText)findViewById(R.id.et_person_name);
		et_person_yearcheck = (EditText)findViewById(R.id.et_person_yearcheck);
		et_pwd = (EditText)findViewById(R.id.et_pwd);
		et_pwd_agian = (EditText)findViewById(R.id.et_pwd_agian);
		cb_person_yearcheck = (CheckBox)findViewById(R.id.cb_person_yearcheck);
		cb_person_yearcheck.setOnCheckedChangeListener(onCheckedChangeListener);
		cb_person_binding = (CheckBox)findViewById(R.id.cb_person_binding);
		cb_person_binding.setOnCheckedChangeListener(onCheckedChangeListener);
		
		tr_person_info_time = (TableRow)findViewById(R.id.tr_person_info_time);
		tr_user_name = (TableRow)findViewById(R.id.tr_user_name);
		tr_user_phone = (TableRow)findViewById(R.id.tr_user_phone);
		tr_user_yearcheck = (TableRow)findViewById(R.id.tr_user_yearcheck);
	}
	
	private void showDateDialog(){
		View v = LayoutInflater.from(PersonActivity.this).inflate(R.layout.data_wheel, null);
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
		
		AlertDialog.Builder addHoldBuilder = new AlertDialog.Builder(PersonActivity.this);
		addHoldBuilder.setTitle("请输入日期");
		addHoldBuilder.setView(v);
		addHoldBuilder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String trueTime = (curYear +year.getCurrentItem()) + "-" + GetSystem.ChangeTime(month.getCurrentItem()+1) + "-" + GetSystem.ChangeTime(day.getCurrentItem()+1);
				et_person_yearcheck.setText(trueTime);
			}
		});
		addHoldBuilder.setNegativeButton("取消",null);
		addHoldBuilder.show();
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
