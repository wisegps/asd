package com.wise.Data;

import java.util.List;

import com.wise.asd.R;

import android.R.raw;
import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	Context mContext;
	List<SmsData> smsDatas;
	public NewAdapter(Context context,List<SmsData> smsDatas){
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.smsDatas = smsDatas;
	}

	public int getCount() {
		return smsDatas.size();
	}

	public Object getItem(int position) {
		return smsDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.new_row, null);
			holder = new ViewHolder();
			holder.tv_new_content = (TextView) convertView.findViewById(R.id.tv_new_content);
			holder.tv_new_time = (TextView)convertView.findViewById(R.id.tv_new_time);
			holder.tv_new_Regnum = (TextView)convertView.findViewById(R.id.tv_new_Regnum);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(mContext.getString(R.string.zh).equals("0")){
			holder.tv_new_content.setText(smsDatas.get(position).getContent());
		}else{
			holder.tv_new_content.setText(smsDatas.get(position).getContent_en());
		}
		holder.tv_new_time.setText(smsDatas.get(position).getRcv_time());
		
		TextPaint tp = holder.tv_new_content.getPaint();
		tp.setStrokeWidth(0);
		tp.setFakeBoldText(false);
		
		TextPaint tt = holder.tv_new_time.getPaint();
		tt.setStrokeWidth(0);
		tt.setFakeBoldText(false);
		
		TextPaint tr = holder.tv_new_Regnum.getPaint();
		tr.setStrokeWidth(0);
		tr.setFakeBoldText(false);
		
		String Type = smsDatas.get(position).getMsg_type();
		if(Type.equals("1")){
			holder.tv_new_Regnum.setText(R.string.sms_10010);
		}else if (Type.equals("2")) {
			holder.tv_new_Regnum.setText(R.string.sms_device);
		}else{
			holder.tv_new_Regnum.setText(R.string.sms_app);
		}
		return convertView;
	}
	private class ViewHolder {
		TextView tv_new_Regnum,tv_new_content,tv_new_time;
	}
}