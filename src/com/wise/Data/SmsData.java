package com.wise.Data;

public class SmsData {
	public String lat;
	public String lon;
	public String rcv_time;
	public String msg_type;
	public String content;
	public String noti_id;
	
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getRcv_time() {
		return rcv_time;
	}
	public void setRcv_time(String rcv_time) {
		this.rcv_time = rcv_time;
	}
	public String getMsg_type() {
		return msg_type;
	}
	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getNoti_id() {
		return noti_id;
	}
	public void setNoti_id(String noti_id) {
		this.noti_id = noti_id;
	}
	@Override
	public String toString() {
		return "SmsData [rcv_time=" + rcv_time + ", msg_type=" + msg_type + ", content=" + content
				+ ", noti_id=" + noti_id + "]";
	}	
}