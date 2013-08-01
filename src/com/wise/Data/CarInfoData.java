package com.wise.Data;


/**
 * 车辆参数信息
 * @author honesty
 *
 */
public class CarInfoData {
	public String cust_name;
	public String op_mobile;
	public String service_end_date;
	public String sim;
	public String sim_type;
	public String annual_inspect_alert;
	public String annual_inspect_date;
	public String insurance_alert;
	public String insurance_date;
	public String maintain_alert;
	public String maintain_milage;
	public String device_id;
	public String serial;
	public String call_phones;
	public int sensitivity;
	public String is_autolock;
	public boolean is_repair;
	
	public boolean isIs_repair() {
		return is_repair;
	}
	public void setIs_repair(boolean is_repair) {
		this.is_repair = is_repair;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getOp_mobile() {
		return op_mobile;
	}
	public void setOp_mobile(String op_mobile) {
		this.op_mobile = op_mobile;
	}
	public String getService_end_date() {
		return service_end_date;
	}
	public void setService_end_date(String service_end_date) {
		this.service_end_date = service_end_date;
	}
	public String getSim() {
		return sim;
	}
	public void setSim(String sim) {
		this.sim = sim;
	}
	public String getAnnual_inspect_alert() {
		return annual_inspect_alert;
	}
	public void setAnnual_inspect_alert(String annual_inspect_alert) {
		this.annual_inspect_alert = annual_inspect_alert;
	}
	public String getAnnual_inspect_date() {
		return annual_inspect_date;
	}
	public void setAnnual_inspect_date(String annual_inspect_date) {
		this.annual_inspect_date = annual_inspect_date;
	}
	public String getInsurance_alert() {
		return insurance_alert;
	}
	public void setInsurance_alert(String insurance_alert) {
		this.insurance_alert = insurance_alert;
	}
	public String getInsurance_date() {
		return insurance_date;
	}
	public void setInsurance_date(String insurance_date) {
		this.insurance_date = insurance_date;
	}
	public String getMaintain_alert() {
		return maintain_alert;
	}
	public void setMaintain_alert(String maintain_alert) {
		this.maintain_alert = maintain_alert;
	}
	public String getMaintain_milage() {
		return maintain_milage;
	}
	public void setMaintain_milage(String maintain_milage) {
		this.maintain_milage = maintain_milage;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getCall_phones() {
		return call_phones;
	}
	public void setCall_phones(String call_phones) {
		this.call_phones = call_phones;
	}
	public String getSim_type() {
		return sim_type;
	}
	public void setSim_type(String sim_type) {
		this.sim_type = sim_type;
	}
	public int getSensitivity() {
		return sensitivity;
	}
	public void setSensitivity(int sensitivity) {
		this.sensitivity = sensitivity;
	}
	public String getIs_autolock() {
		return is_autolock;
	}
	public void setIs_autolock(String is_autolock) {
		this.is_autolock = is_autolock;
	}	
}
