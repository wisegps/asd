package com.wise.asd;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.wise.BaseClass.GetSystem;
import com.wise.BaseClass.NetThread;
import com.wise.Data.NewAdapter;
import com.wise.Data.SmsData;
import com.wise.Parameter.Config;
import com.wise.asd.R;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * 未读消息页面
 * @author honesty
 *
 */
public class SmsActivity extends Activity implements IXListViewListener{
	private String TAG = "SmsActivity";
	private final int GET_SMS = 1;	
	private final int GET_NEXT_SMS = 2;	
	private final int PULL = 3;
	
	XListView lv_sms;
	Button bt_sms_back;
	ProgressDialog Dialog = null;    //等待框    
	NewAdapter newAdapter;
	List<SmsData> smsDataList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		lv_sms = (XListView)findViewById(R.id.lv_sms);
		lv_sms.setPullLoadEnable(true);
		lv_sms.setXListViewListener(this);
		
		bt_sms_back = (Button)findViewById(R.id.bt_sms_back);
		bt_sms_back.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				finish();
			}
		});
		//读取所有未读消息数据
		try {
			String unReadUrl = Config.carDatas.get(Config.index).getUrl() + "vehicle/" + Config.obj_id + "/notification?auth_code=" + Config.Business_auth_code + "&mode=unread&update_time=" + URLEncoder.encode("2013-01-04","UTF-8");
			Config.GetMessageTime = GetSystem.GetNowTime();
			new Thread(new NetThread.GetDataThread(handler, unReadUrl, GET_SMS)).start();
			Dialog = ProgressDialog.show(SmsActivity.this,getString(R.string.Note),getString(R.string.get_data),true);		
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case GET_SMS:
				if(Dialog!=null){
					Dialog.dismiss();
				}
				try {
					smsDataList = new ArrayList<SmsData>();
					JSONObject jsonObject1 = new JSONObject(msg.obj.toString());
					JSONArray jsonArray = jsonObject1.getJSONArray("data");
		        	for(int i = 0 ; i < jsonArray.length() ; i++){
		        		SmsData smsData = new SmsData();
		        		JSONObject jsonObject = jsonArray.getJSONObject(i);
		        		smsData.setContent(jsonObject.getString("content"));
		        		smsData.setLat(jsonObject.getString("lat"));
		        		smsData.setLon(jsonObject.getString("lon"));
		        		smsData.setMsg_type(jsonObject.getString("msg_type"));
		        		smsData.setNoti_id(jsonObject.getString("noti_id"));
		        		smsData.setRcv_time(GetSystem.ChangeTime(jsonObject.getString("rcv_time"),0));
		        		smsDataList.add(smsData);
		        	}
		        	newAdapter = new NewAdapter(SmsActivity.this, smsDataList);
					lv_sms.setAdapter(newAdapter);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case GET_NEXT_SMS:
				try {
					JSONObject jsonObject1 = new JSONObject(msg.obj.toString());
					JSONArray jsonArray = jsonObject1.getJSONArray("data");
					for(int i = 0 ; i < jsonArray.length() ; i++){
		        		SmsData smsData = new SmsData();
		        		JSONObject jsonObject = jsonArray.getJSONObject(i);
		        		smsData.setContent(jsonObject.getString("content"));
		        		smsData.setLat(jsonObject.getString("lat"));
		        		smsData.setLon(jsonObject.getString("lon"));
		        		smsData.setMsg_type(jsonObject.getString("msg_type"));
		        		smsData.setNoti_id(jsonObject.getString("noti_id"));
		        		smsData.setRcv_time(GetSystem.ChangeTime(jsonObject.getString("rcv_time"),0));
		        		smsDataList.add(smsData);
		        	}
					newAdapter.notifyDataSetChanged();
					onLoad();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case PULL:
				try {
					JSONArray jsonArray = new JSONArray(msg.obj.toString());
					for(int i = 0 ; i < jsonArray.length() ; i++){
		        		SmsData smsData = new SmsData();
		        		JSONObject jsonObject = jsonArray.getJSONObject(i);
		        		smsData.setContent(jsonObject.getString("content"));
		        		smsData.setLat(jsonObject.getString("lat"));
		        		smsData.setLon(jsonObject.getString("lon"));
		        		smsData.setMsg_type(jsonObject.getString("msg_type"));
		        		smsData.setNoti_id(jsonObject.getString("noti_id"));
		        		smsData.setRcv_time(GetSystem.ChangeTime(jsonObject.getString("rcv_time"),0));
		        		smsDataList.add(i, smsData);
		        		Log.d(TAG, smsData.toString());
		        	}
					newAdapter.notifyDataSetChanged();
					onLoad();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 999:
				onLoad();
				break;
			}
		}		
	};
	private void onLoad() {
		lv_sms.stopRefresh();
		lv_sms.stopLoadMore();
		lv_sms.setRefreshTime(GetSystem.GetNowTime());
	}
	public void onRefresh() {
		Log.d(TAG, "上拉刷新");
		try {
			String unReadUrl = Config.carDatas.get(Config.index).getUrl() + "vehicle/" + Config.obj_id + "/notification?auth_code=" + Config.Business_auth_code + "&mode=unread&update_time=" + URLEncoder.encode(smsDataList.get(0).getRcv_time(),"UTF-8")+"&page_no=1&page_count=10&max_id=" + smsDataList.get(0).getNoti_id();
			Log.d(TAG, unReadUrl);
			new Thread(new NetThread.GetDataThread(handler, unReadUrl, PULL)).start();
		} catch (Exception e) {
			e.printStackTrace();
			new Thread(new myThread()).start();
		}
	}
	public void onLoadMore() {
		Log.d(TAG, "下拉刷新");
		try {
			int z = Integer.valueOf(smsDataList.get(smsDataList.size()-1).getNoti_id()) - 1;
			String unReadUrl = Config.carDatas.get(Config.index).getUrl() + "vehicle/" + Config.obj_id + "/notification?auth_code=" + Config.Business_auth_code + "&mode=unread&update_time=" + URLEncoder.encode("2013-01-04","UTF-8")+"&page_no=1&page_count=10&min_id="+ z;
			Log.d(TAG, unReadUrl);
			new Thread(new NetThread.GetDataThread(handler, unReadUrl, GET_NEXT_SMS)).start();		
		} catch (Exception e) {
			e.printStackTrace();
			new Thread(new myThread()).start();
		}
	}
	
	class myThread extends Thread{
		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(1000);
				Message message = new Message();
				message.what = 999;
				handler.sendMessage(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
