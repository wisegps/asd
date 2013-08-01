package com.wise.bluetoothUtil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

/**
 * �����������߳�
 * @author GuoDong
 *
 */
public class BluetoothServerConnThread extends Thread {
	
	private Handler serviceHandler;		//����ͬServiceͨ�ŵ�Handler
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;		//����ͨ�ŵ�Socket
	private BluetoothServerSocket serverSocket;
	
	/**
	 * ���캯��
	 * @param handler
	 */
	public BluetoothServerConnThread(Handler handler) {
		this.serviceHandler = handler;
		adapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public void run() {
		try {
			System.out.println("�������ȴ�����");
			serverSocket = adapter.listenUsingRfcommWithServiceRecord("Server", BluetoothTools.PRIVATE_UUID);
			socket = serverSocket.accept();
		} catch (Exception e) {
			//��������ʧ����Ϣ
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_OPENSOCKET).sendToTarget();
			e.printStackTrace();
			return;
		} finally {
			try {
				System.out.println("serverSocket.close()");
				serverSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (socket != null) {
			//�������ӳɹ���Ϣ����Ϣ��obj�ֶ�Ϊ���ӵ�socket
			Message msg = serviceHandler.obtainMessage();
			msg.what = BluetoothTools.MESSAGE_CONNECT_SUCCESS;
			msg.obj = socket;
			msg.sendToTarget();
		} else {
			//��������ʧ����Ϣ
			//serviceHandler.obtainMessage(BluetoothTools.MESSAGE_OPENSOCKET).sendToTarget();
			return;
		}
	}
	
	
}
