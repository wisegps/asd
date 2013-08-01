package com.wise.asd;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.wise.BaseClass.GetSystem;
import com.wise.BaseClass.NetThread;
import com.wise.Parameter.Config;
import com.wise.asd.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 注册账号界面
 * @author honesty
 *
 */
public class RegisterActivity extends Activity{
	String TAG = "RegisterActivity";
	private final int IS_ACCOUNT_OK = 1; 
	private final int REGISTER = 2;
	private final int REGISTER_ACCOUNT = 3;
	Button bt_register_back,bt_register_save;
	EditText et_register_account,et_register_password,et_register_password_again,et_register_person,et_register_mail,et_register_phone;
	TextView tv_isaccount_use;
	
	ProgressDialog Dialog = null;    //progress
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		bt_register_back = (Button)findViewById(R.id.bt_register_back);
		bt_register_back.setOnClickListener(onClickListener);
		bt_register_save = (Button)findViewById(R.id.bt_register_save);
		bt_register_save.setOnClickListener(onClickListener);
		et_register_account = (EditText)findViewById(R.id.et_register_account);
		et_register_account.setOnFocusChangeListener(onFocusChangeListener);
		et_register_password = (EditText)findViewById(R.id.et_register_password);
		et_register_password_again = (EditText)findViewById(R.id.et_register_password_again);
		et_register_person = (EditText)findViewById(R.id.et_register_person);
		et_register_mail = (EditText)findViewById(R.id.et_register_mail);
		et_register_phone = (EditText)findViewById(R.id.et_register_phone);
		tv_isaccount_use = (TextView)findViewById(R.id.tv_isaccount_use);
	}
	String url;
	String cust_name;
	String email;
	String cust_type;
	String parent_cust_id;
	String contacter;
	String contacter_tel;
	String province;
	String city;
	String reg_rule;
	String send_type;
	String parent_tree_path;
	String parent_level;
	String roles;
	String password;
	String users;
	OnClickListener onClickListener = new OnClickListener() {		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_register_back:
				finish();
				break;
			case R.id.bt_register_save:
					url = Config.URL + "customer?auth_code=" + Config.reg_auth_code;
					cust_name = et_register_account.getText().toString();
					email = et_register_mail.getText().toString();
					cust_type = "1";
					parent_cust_id = "1";
					contacter = et_register_person.getText().toString();
					contacter_tel = et_register_phone.getText().toString();
					province = "1";
					city = "10";
					reg_rule = "{}";
					send_type = "0";
					parent_tree_path = ",1,";
					parent_level = "0";
					roles = "[1]";
					password = GetSystem.getM5DEndo(et_register_password.getText().toString());
					String update_time = GetSystem.GetNowTime();				
					users = "[{\"user_name\": \"" + cust_name + "\",\"password\": \"" + password
									+ "\",\"mobile\": \"" + contacter_tel + "\",\"email\": \"" + email
									+ "\",\"update_time\": \"" + update_time + "\"}]";
					Log.d(TAG, users);
					//判断内容是否为空
					if(cust_name.equals("")||contacter.equals("")||contacter_tel.equals("")||password.equals("")){
						Toast.makeText(getApplicationContext(), R.string.not_complete_not, Toast.LENGTH_SHORT).show();
					}else if(!et_register_password.getText().toString().equals(et_register_password_again.getText().toString())){
						Toast.makeText(getApplicationContext(), R.string.password_again_error, Toast.LENGTH_SHORT).show();
					}else{
						Dialog = ProgressDialog.show(RegisterActivity.this,getString(R.string.Note),getString(R.string.registering),true);
						//验证账号
						String url = Config.URL + "customer/search?auth_code=" + Config.reg_auth_code + "&username=" + cust_name;
						new Thread(new NetThread.GetDataThread(handler, url, REGISTER_ACCOUNT)).start();
						
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
			case IS_ACCOUNT_OK:
				Log.d(TAG, "返回结果=" + msg.obj.toString());
				if(msg.obj.toString().equals("")){
					tv_isaccount_use.setVisibility(View.GONE);
				}else{
					tv_isaccount_use.setVisibility(View.VISIBLE);
				}
				break;
			case REGISTER_ACCOUNT:
				if(msg.obj.toString().equals("")){
					tv_isaccount_use.setVisibility(View.GONE);
					//注册
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("cust_name", cust_name));
					params.add(new BasicNameValuePair("cust_type", cust_type));
					params.add(new BasicNameValuePair("parent_cust_id", parent_cust_id));
					params.add(new BasicNameValuePair("contacter", contacter));
					params.add(new BasicNameValuePair("contacter_tel", contacter_tel));
					params.add(new BasicNameValuePair("province", province));
					params.add(new BasicNameValuePair("city", city));
					params.add(new BasicNameValuePair("reg_rule", reg_rule));
					params.add(new BasicNameValuePair("send_type", send_type));
					params.add(new BasicNameValuePair("tree_path", parent_tree_path));
					params.add(new BasicNameValuePair("level", parent_level));
					params.add(new BasicNameValuePair("roles", roles));
					params.add(new BasicNameValuePair("users", users));
					new Thread(new NetThread.postDataThread(handler, url, params, REGISTER)).start();
				}else{
					//提示账号已存在
					if(Dialog != null){
						Dialog.dismiss();					
					}
					tv_isaccount_use.setVisibility(View.VISIBLE);
					Toast.makeText(getApplicationContext(), R.string.account_used, Toast.LENGTH_SHORT).show();
				}
				break;

			case REGISTER:
				if(Dialog != null){
					Dialog.dismiss();					
				}
				if(msg.obj.toString().equals("")){
					Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), R.string.register_ok, Toast.LENGTH_SHORT).show();
					
					Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("account", cust_name);
					bundle.putString("password", et_register_password.getText().toString());
					intent.putExtras(bundle);
					setResult(1, intent);
					finish();
				}
				break;
			}
		}		
	};
	
	OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {		
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				tv_isaccount_use.setVisibility(View.GONE);
			}else{
				//判断账户是否存在
				String account = et_register_account.getText().toString();
				if(account.equals("")){
				}else{
					String url = Config.URL + "customer/search?auth_code=" + Config.reg_auth_code + "&username=" + account;
					new Thread(new NetThread.GetDataThread(handler, url, IS_ACCOUNT_OK)).start();
				}
			}
		}
	};
}
