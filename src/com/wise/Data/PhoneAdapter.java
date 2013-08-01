package com.wise.Data;

import java.util.List;

import com.wise.asd.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PhoneAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	Context mContext;
	List<PhoneData> phoneDatas;
	public PhoneAdapter(Context context,List<PhoneData> phoneDatas){
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.phoneDatas = phoneDatas;
	}
	public int getCount() {
		return phoneDatas.size();
	}
	public Object getItem(int position) {
		return phoneDatas.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.phone_row, null);
			holder = new ViewHolder();
			holder.tv_phone_row_name = (TextView)convertView.findViewById(R.id.tv_phone_row_name);
			holder.tv_phone_row_phone = (TextView)convertView.findViewById(R.id.tv_phone_row_phone);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_phone_row_name.setText(phoneDatas.get(position).getName());
		holder.tv_phone_row_phone.setText(phoneDatas.get(position).getPhone());		
		return convertView;
	}
	private class ViewHolder {
		TextView tv_phone_row_name,tv_phone_row_phone;
	}
}
