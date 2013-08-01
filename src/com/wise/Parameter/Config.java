package com.wise.Parameter;

import java.util.List;
import com.wise.Data.AllCars;

public class Config {
	/**
	 * 包名,更新得到版本号
	 */
	public static String PackString = "com.wise.asd";
	/**
	 * 1,掌沃车
	 * 2,沃管车
	 */
	public static String send_type = "1";
	/**
	 * 更新地址
	 */
	public static String UpdateUrl = "upgrade/asd?auth_code=bba2204bcd4c1f87a19ef792f1f68404";
	/**
	 * 配置文件名称
	 */
	public static String sharedPreferencesName = "Vehicle_Net";
	
	/**
	 * 中心服务器
	 */
	public static String URL = "http://42.121.254.182/";	
	/**
	 * 中心服务器验证码
	 */
	public static String auth_code;
	/**
	 * 与车辆有关的,读取车辆消息
	 */
	public static String Business_auth_code;
	/**
	 * 注册用户用到
	 */
	public static String reg_auth_code = "bba2204bcd4c1f87a19ef792f1f68404";
	/**
	 * 客户id
	 */
	public static String cust_id;
	/**
	 * 用户名
	 */
	public static String account;
	/**
	 * 密码
	 */
	public static String pwd;
	/**
	 * 树路径
	 */
	public static String tree_path;
	/**
	 *  1 主号 2 副号
	 */
	public static String number_type;
	/**
	 * 用户下车辆列表
	 */
	public static List<AllCars> carDatas;
	/**
	 * 当前车辆
	 */
	public static int index = 0;
	/**
	 * 读取车辆信息用到
	 */
	public static String obj_id;
	/**
	 * 读取消息数目时间
	 */
	public static String GetMessageTime = "2013-01-04";
}