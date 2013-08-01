package com.wise.asd;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.wise.Data.PhoneAdapter;
import com.wise.Data.PhoneData;
import com.wise.asd.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
/**
 * 查看，设置一键呼叫页面
 * @author honesty
 *
 */
public class PhoneActivity extends Activity{
	private final static String TAG = "PhoneActivity";
	ListView lv_phone;
	Button bt_phone_back,bt_phone_save;
	
	List<PhoneData> phoneDatas = new ArrayList<PhoneData>();
	String jsonString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone);
		bt_phone_back = (Button)findViewById(R.id.bt_phone_back);
		bt_phone_back.setOnClickListener(onClickListener);
		bt_phone_save = (Button)findViewById(R.id.bt_phone_save);
		bt_phone_save.setOnClickListener(onClickListener);
		lv_phone = (ListView)findViewById(R.id.lv_phone);
		lv_phone.setOnItemClickListener(onItemClickListener);
		//解析数据
		try {
			Intent intent =getIntent();
			jsonString = intent.getStringExtra("call");
			JSONArray jsonArray = new JSONArray(jsonString);
			for(int i = 0 ; i < jsonArray.length() ; i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				PhoneData phoneData = new PhoneData();
				phoneData.setName(jsonObject.getString("name"));
				phoneData.setPhone(jsonObject.getString("phone"));
				phoneDatas.add(phoneData);
			}
			PhoneAdapter phoneAdapter = new PhoneAdapter(getApplicationContext(), phoneDatas);
			lv_phone.setAdapter(phoneAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	OnClickListener onClickListener = new OnClickListener() {		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_phone_back:
				Back();
				break;				
			case R.id.bt_phone_save:
				ShowPhone();
				break;
			}
		}
	};
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,long arg3) {
			new AlertDialog.Builder(PhoneActivity.this).setItems(R.array.phone_list, new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						//修改
						View view_addfriend = LayoutInflater.from(PhoneActivity.this).inflate(R.layout.add_phone, null);
						final EditText et_phone_name = (EditText) view_addfriend.findViewById(R.id.et_add_phone_name);
						final EditText et_phone = (EditText) view_addfriend.findViewById(R.id.et_add_phone);
						AlertDialog.Builder addPhoneBuilder = new AlertDialog.Builder(PhoneActivity.this);
						addPhoneBuilder.setTitle(R.string.Note);
						addPhoneBuilder.setView(view_addfriend);
						et_phone_name.setText(phoneDatas.get(arg2).getName());
						et_phone.setText(phoneDatas.get(arg2).getPhone());
						addPhoneBuilder.setPositiveButton(R.string.Sure, new DialogInterface.OnClickListener() {			
							public void onClick(DialogInterface dialog, int which) {
								String name = et_phone_name.getText().toString();
								String phone = et_phone.getText().toString();
								if(name.equals("")||phone.equals("")){
									Toast.makeText(getApplicationContext(), R.string.not_complete_not, Toast.LENGTH_SHORT).show();
								}else{
									phoneDatas.get(arg2).setName(name);
									phoneDatas.get(arg2).setPhone(phone);
									PhoneAdapter phoneAdapter = new PhoneAdapter(getApplicationContext(), phoneDatas);
									lv_phone.setAdapter(phoneAdapter);
								}
							}
						});
						addPhoneBuilder.setNegativeButton(R.string.cancle, null);
						addPhoneBuilder.show();
						break;

					case 1:
						//删除
						AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(PhoneActivity.this);
						deleteBuilder.setTitle(R.string.Note);
						deleteBuilder.setMessage(R.string.delete_phone);
						deleteBuilder.setPositiveButton(R.string.Sure, new DialogInterface.OnClickListener() {							
							public void onClick(DialogInterface dialog, int which) {
								phoneDatas.remove(arg2);
								PhoneAdapter phoneAdapter = new PhoneAdapter(getApplicationContext(), phoneDatas);
								lv_phone.setAdapter(phoneAdapter);
							}
						});
						deleteBuilder.setNegativeButton(R.string.cancle, null);
						deleteBuilder.show();
						break;
					}
				}
			}).setNegativeButton(R.string.cancle, null).show();
		}
	};
	/**
	 * 添加号码
	 */
	private void ShowPhone(){
		View view_addfriend = LayoutInflater.from(PhoneActivity.this).inflate(R.layout.add_phone, null);
		final EditText et_phone_name = (EditText) view_addfriend.findViewById(R.id.et_add_phone_name);
		final EditText et_phone = (EditText) view_addfriend.findViewById(R.id.et_add_phone);
		AlertDialog.Builder addPhoneBuilder = new AlertDialog.Builder(PhoneActivity.this);
		addPhoneBuilder.setTitle(R.string.Note);
		addPhoneBuilder.setView(view_addfriend);
		addPhoneBuilder.setPositiveButton(R.string.Sure, new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				String name = et_phone_name.getText().toString();
				String phone = et_phone.getText().toString();
				if(name.equals("")||phone.equals("")){
					Toast.makeText(getApplicationContext(), R.string.not_complete_not, Toast.LENGTH_SHORT).show();
				}else{
					PhoneData phoneData = new PhoneData();
					phoneData.setName(name);
					phoneData.setPhone(phone);
					phoneDatas.add(phoneData);
					PhoneAdapter phoneAdapter = new PhoneAdapter(getApplicationContext(), phoneDatas);
					lv_phone.setAdapter(phoneAdapter);
				}
			}
		});
		addPhoneBuilder.setNegativeButton(R.string.cancle, null);
		addPhoneBuilder.show();
	}
	private void Back(){
		try {
			JSONArray jsonArray = new JSONArray();
			for(int i = 0 ; i < phoneDatas.size() ; i++){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", phoneDatas.get(i).getName());
				jsonObject.put("phone", phoneDatas.get(i).getPhone());
				jsonArray.put(jsonObject);
			}
			jsonString = jsonArray.toString().replaceAll("\"name\":", "name:").replaceAll("\"phone\":", "phone:");
			Log.d(TAG, jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(getApplicationContext(), AddCarActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("jsonPhone", jsonString);
		intent.putExtras(bundle);
		setResult(1, intent);
		finish();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Back();
		}
		return super.onKeyDown(keyCode, event);
	}
}
