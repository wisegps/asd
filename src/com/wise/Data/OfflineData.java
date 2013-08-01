package com.wise.Data;

public class OfflineData {
	public String serial;
	public String obj_name;
	public String sim;
	public String accessory;
	public String is_start;
	public String phone;
	public boolean is_sound;
	public boolean is_lockdoor;	
	
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getObj_name() {
		return obj_name;
	}
	public void setObj_name(String obj_name) {
		this.obj_name = obj_name;
	}
	public String getSim() {
		return sim;
	}
	public void setSim(String sim) {
		this.sim = sim;
	}
	public String getAccessory() {
		return accessory;
	}
	public void setAccessory(String accessory) {
		this.accessory = accessory;
	}
	public String getIs_start() {
		return is_start;
	}
	public void setIs_start(String is_start) {
		this.is_start = is_start;
	}
	public boolean isIs_sound() {
		return is_sound;
	}
	public void setIs_sound(boolean is_sound) {
		this.is_sound = is_sound;
	}
	public boolean isIs_lockdoor() {
		return is_lockdoor;
	}	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setIs_lockdoor(boolean is_lockdoor) {
		this.is_lockdoor = is_lockdoor;
	}
	@Override
	public String toString() {
		return "OfflineData [serial=" + serial + ", obj_name=" + obj_name
				+ ", sim=" + sim + ", accessory=" + accessory + ", is_start="
				+ is_start + ", phone=" + phone + ", is_sound=" + is_sound
				+ ", is_lockdoor=" + is_lockdoor + "]";
	}		
}