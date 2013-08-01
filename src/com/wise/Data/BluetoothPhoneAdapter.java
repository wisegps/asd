package com.wise.Data;

import java.util.List;

import com.wise.asd.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BluetoothPhoneAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	Context mContext;
	List<BluetoothData> bluetoothDatas;
	public BluetoothPhoneAdapter(Context context,List<BluetoothData> bluetoothDatas){
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.bluetoothDatas = bluetoothDatas;
	}
	public int getCount() {
		return bluetoothDatas.size();
	}
	public Object getItem(int position) {
		return bluetoothDatas.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.bluetooth_row, null);
			holder = new ViewHolder();
			holder.tv_bluetooth_row_name = (TextView)convertView.findViewById(R.id.tv_bluetooth_row_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}	
		holder.tv_bluetooth_row_name.setText(bluetoothDatas.get(position).getAddress());	
		return convertView;
	}
	private class ViewHolder {
		TextView tv_bluetooth_row_name;
	}
}
