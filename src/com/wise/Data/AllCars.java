package com.wise.Data;
/**
 * 用户所属车辆列表
 * @author honesty
 *
 */
public class AllCars {
	public String serial;
	public String obj_name;
	public String url;
	public String obj_id;
	public String getObj_name() {
		return obj_name;
	}
	public void setObj_name(String obj_name) {
		this.obj_name = obj_name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}	
	public String getObj_id() {
		return obj_id;
	}
	public void setObj_id(String obj_id) {
		this.obj_id = obj_id;
	}
	@Override
	public String toString() {
		return "AllCars [serial=" + serial + ", obj_name=" + obj_name
				+ ", url=" + url + "]";
	}		
}