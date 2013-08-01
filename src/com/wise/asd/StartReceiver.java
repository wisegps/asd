package com.wise.asd;

import com.wise.bluetoothUtil.BluetoothServerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartReceiver extends BroadcastReceiver{
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION)){
			Intent startService = new Intent(context, BluetoothServerService.class);
			context.startService(startService);	
		}
	}
}
