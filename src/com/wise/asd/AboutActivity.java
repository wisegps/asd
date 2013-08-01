package com.wise.asd;

import com.wise.BaseClass.GetSystem;
import com.wise.Parameter.Config;
import com.wise.asd.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	private void init(){
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉标题栏
		setContentView(R.layout.about);
		Button bt_about_back = (Button)findViewById(R.id.bt_about_back);
		bt_about_back.setOnClickListener(Ocl);
		TextView tv_banben = (TextView)findViewById(R.id.tv_banben);
		tv_banben.setText("版本：" + GetSystem.GetVersion(getApplicationContext(), Config.PackString));
	}
	private OnClickListener Ocl = new OnClickListener() {
		
		public void onClick(View v) {
			finish();
		}
	};
}
