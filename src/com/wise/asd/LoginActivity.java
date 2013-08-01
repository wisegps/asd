package com.wise.asd;

import org.json.JSONObject;
import com.wise.BaseClass.GetSystem;
import com.wise.BaseClass.NetThread;
import com.wise.BaseClass.UpdateManager;
import com.wise.Parameter.Config;
import com.wise.asd.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity{
	private static String TAG = "LoginActivity";
	private final int LOGIN = 1;
	private final int Update = 2;
	
	TextView tv_update;
	EditText et_account,et_password;
	Button bt_login,bt_register;
	CheckBox cb_password;
	
	ProgressDialog Dialog = null;    //progress
	boolean IsRememberPassword = true; //�Ƿ��ס����
	String account;   //�˺�
	String password;  //����
	double Verson; 	  //�汾�ţ��û��жϸ���
	String VersonUrl; //����·��
	String MACAdress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(); //��ʼ���ؼ�
		getSharedPreferences();//��ȡ������Ϣ		
		getMacAddress(); //��ȡMAC��ַ
		isUpdate();  //�Ƿ��ȡ������Ϣ
		isOffline(); //�ж�����
	}
	OnClickListener onClickListener = new OnClickListener() {		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_login:
				Login(); //��¼
				break;
			case R.id.bt_register:
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivityForResult(intent, 1);
				break;
			case R.id.tv_update:
				UpdateManager mUpdateManager = new UpdateManager(LoginActivity.this,VersonUrl);
			    mUpdateManager.checkUpdateInfo();
				break;
			}
		}
	};
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOGIN:
				LoginData(msg);
				break;
			case Update:
				UpdateData(msg);
				break;
			}
		}		
	};
	/**
	 * �ж�sd���Ƿ����
	 * @return
	 */
	private boolean isSdCardExist(){
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	/**
	 * �Ƿ��ȡ������Ϣ,�����sd�������
	 */
	private void isUpdate(){
		if(isSdCardExist()){
			try {
				Log.d(TAG, "Verson:" + Verson + ",VersonUrl:" + VersonUrl);
				//�õ�ϵͳ�İ汾
				if(Verson>Double.valueOf(GetSystem.GetVersion(getApplicationContext(), Config.PackString))){
					tv_update.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String url = Config.URL + Config.UpdateUrl;
			new Thread(new NetThread.GetDataThread(handler, url, Update)).start();
		}else{
			Toast.makeText(LoginActivity.this, R.string.SD_NOTFIND, Toast.LENGTH_LONG).show();
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 1){
			Bundle bundle = data.getExtras();
			et_account.setText(bundle.getString("account"));
			et_password.setText(bundle.getString("password"));
		}
	};
	/*
     * �������������
     * */
    public void setNetworkMethod(){
    	new AlertDialog.Builder(LoginActivity.this).setTitle("����������ʾ")
    	.setMessage("�������Ӳ�����,������������,Ҳ�ɽ�����Ų���ģʽ.")
    	.setPositiveButton("��������", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				startActivity(intent);
			}
		}).setNeutralButton("���Ų���", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(getApplicationContext(), OfflineActivity.class);
				startActivity(intent);
			}
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).show();
    }
    /**
     * ��ȡMAC��ַ
     */
    public void getMacAddress(){
    	MACAdress = GetSystem.getMacAddress(getApplicationContext());
    }
    public void init(){
    	setContentView(R.layout.login);
		tv_update = (TextView)findViewById(R.id.tv_update);
		tv_update.setOnClickListener(onClickListener);
		et_account = (EditText)findViewById(R.id.et_account);
		et_password = (EditText)findViewById(R.id.et_password);
		bt_login = (Button)findViewById(R.id.bt_login);
		bt_login.setOnClickListener(onClickListener);
		bt_register = (Button)findViewById(R.id.bt_register);
		bt_register.setOnClickListener(onClickListener);
		cb_password = (CheckBox)findViewById(R.id.cb_password);
		cb_password.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox)v;
				IsRememberPassword = cb.isChecked();
			}
		});	
    }
    /**
     * ��ȡ������Ϣ
     */
    private void getSharedPreferences(){
    	SharedPreferences preferences = getSharedPreferences(Config.sharedPreferencesName, Context.MODE_PRIVATE);
		account = preferences.getString("account", "");
		password = preferences.getString("password", "");
		Verson = Double.valueOf(preferences.getString("Verson", "0"));
		VersonUrl = preferences.getString("VersonUrl", "");
		IsRememberPassword = preferences.getBoolean("IsRememberPassword", true);
		if(IsRememberPassword){
			et_account.setText(account);
			et_password.setText(password);
			cb_password.setChecked(IsRememberPassword);
		}
    }
    /**
     * �ж���������״����û������ʾ������
     */
    private void isOffline(){
    	if(!GetSystem.checkNetWorkStatus(getApplicationContext())){
			setNetworkMethod();
		}
    }
    /**
     * �����̵߳�¼�ж�
     */
    private void Login(){
    	account = et_account.getText().toString();
		password = et_password.getText().toString();
		IsRememberPassword = cb_password.isChecked();
		if(account.equals("")||password.equals("")){
			Toast.makeText(getApplicationContext(), R.string.not_complete_not, Toast.LENGTH_LONG).show();
		}else{
			Dialog = ProgressDialog.show(LoginActivity.this,getString(R.string.Note),getString(R.string.login_note),true);
			String url = Config.URL + "customer/login?username=" + account + "&password=" + GetSystem.getM5DEndo(password) +"&mac=" + MACAdress;
			new Thread(new NetThread.GetDataThread(handler, url, LOGIN)).start();
		}
    }
    /**
     * ��¼������Ϣ��֤
     * @param msg
     */
    private void LoginData(Message msg){
    	if(Dialog != null){
			Dialog.dismiss();
		}
		if(msg.obj.toString().equals("SocketTimeoutException")){
			setNetworkMethod();
		}else{
			try {
				Log.d(TAG, msg.obj.toString());
				JSONObject jsonObject = new JSONObject(msg.obj.toString());
				if(jsonObject.opt("auth_code") == null){
					String status_code = jsonObject.getString("status_code");
					if(status_code.equals("5")){
						Toast.makeText(getApplicationContext(), "���˺��Ѱ������ֻ�", Toast.LENGTH_LONG).show();
					}else if(status_code.equals("2") || status_code.equals("1")){
						Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
					}else{
						setNetworkMethod();
					}
				}else{
					Config.auth_code = jsonObject.getString("auth_code");
					Config.cust_id = jsonObject.getString("cust_id");
					Config.number_type = jsonObject.getString("number_type");
					//����������Ϣ
					SharedPreferences preferences = getSharedPreferences(Config.sharedPreferencesName, Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
					editor.putString("account", account);
					editor.putString("password", password);
					editor.putBoolean("IsRememberPassword", IsRememberPassword);					
					editor.commit();
					Config.account = account;
					Config.pwd = password;
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    /**
     * ��ȡ���½ӿ���Ϣ
     * @param msg
     */
    private void UpdateData(Message msg){
    	try {
			JSONObject jsonObject = new JSONObject(msg.obj.toString());
			SharedPreferences preferences = getSharedPreferences(Config.sharedPreferencesName, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString("Verson", jsonObject.getString("version"));
			editor.putString("VersonUrl", jsonObject.getString("app_path"));
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}