package com.wise.asd;

import com.wise.asd.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 意见反馈
 * @author honesty
 *
 */
public class FeedbcakActivity extends Activity{
	EditText et_feedback_content,et_feedback_phone;
	String content,phone;
	ProgressDialog Dialog = null;    //progress
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	private void init(){
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉标题栏
		setContentView(R.layout.feedback);
		et_feedback_content = (EditText)findViewById(R.id.et_feedback_content);
		et_feedback_phone = (EditText)findViewById(R.id.et_feedback_phone);
		Button bt_feedback_back = (Button)findViewById(R.id.bt_feedback_back);
		bt_feedback_back.setOnClickListener(Ocl);
		Button bt_feedback_save = (Button)findViewById(R.id.bt_feedback_save);
		bt_feedback_save.setOnClickListener(Ocl);
	}
	private OnClickListener Ocl = new OnClickListener() {		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_feedback_back:
				finish();
				break;
			case R.id.bt_feedback_save:
				content = et_feedback_content.getText().toString();
				phone = et_feedback_phone.getText().toString();
				if(content.equals("")||phone.equals("")){
					Toast.makeText(FeedbcakActivity.this, "请填写完整", Toast.LENGTH_LONG).show();
				}else{
					//new Thread(new myThread()).start();
					//Dialog = ProgressDialog.show(FeedbcakActivity.this,"提示","意见反馈中...",true);
				}
				break;
			}
		}
	};
	class myThread extends Thread{
		@Override
		public void run() {
			super.run();
			try {
				//String myBackString = WebService.SoapAddFeedback(Config.url, Config.nameSpace, Config.methodAddFeedback, Config.userid,phone ,content, 30000);
				//System.out.println(myBackString);
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			} catch (Exception e) {
				Message message = new Message();
				message.what = 0;
				handler.sendMessage(message);
			}
		}
	}
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(Dialog != null){
				Dialog.dismiss();
			}
			switch (msg.what) {
			case 0:
				Toast.makeText(FeedbcakActivity.this, "意见反馈失败", Toast.LENGTH_LONG).show();
				break;

			case 1:
				Toast.makeText(FeedbcakActivity.this, "意见反馈成功", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
}
