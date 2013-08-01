package com.wise.bluetoothUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
	
/**
 * ����ͨѶ�߳�
 * @author GuoDong
 *
 */
public class BluetoothCommunThread extends Thread {

	private Handler serviceHandler;		//��Serviceͨ�ŵ�Handler
	private BluetoothSocket socket;
	private InputStream inStream;		//����������
	private OutputStream outStream;	//���������
	public volatile boolean isRun = true;	//���б�־λ
	
	/**
	 * ���캯��
	 * @param handler ���ڽ�����Ϣ
	 * @param socket
	 */
	public BluetoothCommunThread(Handler handler, BluetoothSocket socket) {
		this.serviceHandler = handler;
		this.socket = socket;
		try {
			outStream = socket.getOutputStream();
			inStream = socket.getInputStream();
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//��������ʧ����Ϣ
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		byte[] buffer = new byte[1024];
        int bytes;
		while (true) {
			if (!isRun) {	
				System.out.println("ѭ���ر�");
				break;
			}
			try {
				bytes = inStream.read(buffer);
				serviceHandler.obtainMessage(BluetoothTools.MESSAGE_READ_OBJECT, bytes, -1, buffer).sendToTarget();
			} catch (Exception ex) {
				//��������ʧ����Ϣ
				serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
				ex.printStackTrace();
				return;
			}
		}
		
		//�ر���
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (outStream != null) {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * д��һ�������л��Ķ���
	 * @param obj
	 */
	public void writeObject(String obj) {
		try {
			System.out.println("����ָ�" + obj);
			outStream.write(obj.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
