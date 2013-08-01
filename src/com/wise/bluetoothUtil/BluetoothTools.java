package com.wise.bluetoothUtil;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * ����������
 * @author GuoDong
 *
 */
public class BluetoothTools {

	private static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	
	/**
	 * ��������ʹ�õ�UUID
	 */
	public static final UUID PRIVATE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	/**
	 * �ַ��������������Intent�е��豸����
	 */
	public static final String DEVICE = "DEVICE";
	
	/**
	 * �ַ��������������������豸�б��е�λ��
	 */
	public static final String SERVER_INDEX = "SERVER_INDEX";
	
	/**
	 * �ַ���������Intent�е�����
	 */
	public static final String DATA = "DATA";
	
	/**
	 * Action���ͱ�ʶ����Action���� Ϊ��������
	 */
	public static final String ACTION_READ_DATA = "ACTION_READ_DATA";
	
	/**
	 * Action���ͱ�ʶ����Action����Ϊ δ�����豸
	 */
	public static final String ACTION_NOT_FOUND_SERVER = "ACTION_NOT_FOUND_DEVICE";
	
	/**
	 * Action���ͱ�ʶ����Action����Ϊ ��ʼ�����豸
	 */
	public static final String ACTION_START_DISCOVERY = "ACTION_START_DISCOVERY";
	
	/**
	 * Action���豸�б�
	 */
	public static final String ACTION_FOUND_DEVICE = "ACTION_FOUND_DEVICE";
	
	/**
	 * Action��ѡ����������ӵ��豸
	 */
	public static final String ACTION_SELECTED_DEVICE = "ACTION_SELECTED_DEVICE";
	
	/**
	 * Action������������
	 */
	public static final String ACTION_START_SERVER = "ACTION_STARRT_SERVER";
	
	/**
	 * Action���رպ�̨Service
	 */
	public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
	
	/**
	 * Action����Service������
	 */
	public static final String ACTION_DATA_TO_SERVICE = "ACTION_DATA_TO_SERVICE";
	
	/**
	 * Action�����ӳɹ�
	 */
	public static final String ACTION_CONNECT_SUCCESS = "ACTION_CONNECT_SUCCESS";
	
	/**
	 * Action�����Ӵ���
	 */
	public static final String ACTION_CONNECT_ERROR = "ACTION_CONNECT_ERROR";
	/**
	 * ����������ʾ
	 */
	public static final String ACTION_GET_DATA = "ACTION_GET_DATA";
	
	public static final String ACTION_CHANGE = "android.bluetooth.adapter.action.STATE_CHANGED";
	
	/**
	 * Message���ͱ�ʶ�������ӳɹ�
	 */
	public static final int MESSAGE_CONNECT_SUCCESS = 0x00000002;
	
	/**
	 * Message������ʧ��
	 */
	public static final int MESSAGE_CONNECT_ERROR = 0x00000003;
	
	/**
	 * Message����ȡ��һ������
	 */
	public static final int MESSAGE_READ_OBJECT = 0x00000004;
	/**
	 * socket�����쳣
	 */
	public static final int MESSAGE_OPENSOCKET = 0x00000005;
	/**
	 * ����������
	 */
	public static void openBluetooth() {
		adapter.enable();
	}
	
	/**
	 * �ر���������
	 */
	public static void closeBluetooth() {
		adapter.disable();
	}
	
	/**
	 * �����������ֹ���
	 * @param duration �����������ֹ��ܴ򿪳���������ֵΪ0��300֮���������
	 */
	public static void openDiscovery(int duration) {
		if (duration <= 0 || duration > 300) {
			duration = 200;
		}
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
	}
	
	/**
	 * ֹͣ��������
	 */
	public static void stopDiscovery() {
		adapter.cancelDiscovery();
	}
	
}
