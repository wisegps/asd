package com.wise.Data;

public class CarData {
	public String obj_id;
	public String obj_name;
	public String rev_lat;
	public String rev_lon;
	public String rcv_time;
	public String mileage;
	public int speed;
	public String status;
	public int direct;
	public int gps_flag;
	public String getObj_id() {
		return obj_id;
	}
	public void setObj_id(String obj_id) {
		this.obj_id = obj_id;
	}
	public String getObj_name() {
		return obj_name;
	}
	public void setObj_name(String obj_name) {
		this.obj_name = obj_name;
	}
	public String getRev_lat() {
		return rev_lat;
	}
	public void setRev_lat(String rev_lat) {
		this.rev_lat = rev_lat;
	}
	public String getRev_lon() {
		return rev_lon;
	}
	public void setRev_lon(String rev_lon) {
		this.rev_lon = rev_lon;
	}
	public int getDirect() {
		return direct;
	}
	public void setDirect(int direct) {
		this.direct = direct;
	}
	public int getGps_flag() {
		return gps_flag;
	}
	public void setGps_flag(int gps_flag) {
		this.gps_flag = gps_flag;
	}	
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public String getMileage() {
		return mileage;
	}
	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRcv_time() {
		return rcv_time;
	}
	public void setRcv_time(String rcv_time) {
		this.rcv_time = rcv_time;
	}
	@Override
	public String toString() {
		return "CarData [obj_id=" + obj_id + ", obj_name=" + obj_name
				+ ", rev_lat=" + rev_lat + ", rev_lon=" + rev_lon
				+ ", rcv_time=" + rcv_time + ", mileage=" + mileage
				+ ", speed=" + speed + ", status=" + status + ", direct="
				+ direct + ", gps_flag=" + gps_flag + "]";
	}		
}
