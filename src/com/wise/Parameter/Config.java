package com.wise.Parameter;

import java.util.List;
import com.wise.Data.AllCars;

public class Config {
	/**
	 * ����,���µõ��汾��
	 */
	public static String PackString = "com.wise.asd";
	/**
	 * 1,���ֳ�
	 * 2,�ֹܳ�
	 */
	public static String send_type = "1";
	/**
	 * ���µ�ַ
	 */
	public static String UpdateUrl = "upgrade/asd?auth_code=bba2204bcd4c1f87a19ef792f1f68404";
	/**
	 * �����ļ�����
	 */
	public static String sharedPreferencesName = "Vehicle_Net";
	
	/**
	 * ���ķ�����
	 */
	public static String URL = "http://42.121.254.182/";	
	/**
	 * ���ķ�������֤��
	 */
	public static String auth_code;
	/**
	 * �복���йص�,��ȡ������Ϣ
	 */
	public static String Business_auth_code;
	/**
	 * ע���û��õ�
	 */
	public static String reg_auth_code = "bba2204bcd4c1f87a19ef792f1f68404";
	/**
	 * �ͻ�id
	 */
	public static String cust_id;
	/**
	 * �û���
	 */
	public static String account;
	/**
	 * ����
	 */
	public static String pwd;
	/**
	 * ��·��
	 */
	public static String tree_path;
	/**
	 *  1 ���� 2 ����
	 */
	public static String number_type;
	/**
	 * �û��³����б�
	 */
	public static List<AllCars> carDatas;
	/**
	 * ��ǰ����
	 */
	public static int index = 0;
	/**
	 * ��ȡ������Ϣ�õ�
	 */
	public static String obj_id;
	/**
	 * ��ȡ��Ϣ��Ŀʱ��
	 */
	public static String GetMessageTime = "2013-01-04";
}