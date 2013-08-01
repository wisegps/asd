package com.wise.Data;

public class PersonData {
	public String cust_id;
	public String cust_name;
	public String cust_type;
	public String parent_cust_id;
	public String parent_tree_path;
	public String parent_name;
	public String contacter;
	public String annual_inspect_alert;
	public String annual_inspect_date;
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getCust_type() {
		return cust_type;
	}
	public void setCust_type(String cust_type) {
		this.cust_type = cust_type;
	}
	public String getParent_cust_id() {
		return parent_cust_id;
	}
	public void setParent_cust_id(String parent_cust_id) {
		this.parent_cust_id = parent_cust_id;
	}
	public String getParent_tree_path() {
		return parent_tree_path;
	}
	public void setParent_tree_path(String parent_tree_path) {
		this.parent_tree_path = parent_tree_path;
	}
	public String getContacter() {
		return contacter;
	}
	public void setContacter(String contacter) {
		this.contacter = contacter;
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
	public String getParent_name() {
		return parent_name;
	}
	public void setParent_name(String parent_name) {
		this.parent_name = parent_name;
	}
	@Override
	public String toString() {
		return "PersonData [cust_id=" + cust_id + ", cust_name=" + cust_name
				+ ", cust_type=" + cust_type + ", parent_cust_id="
				+ parent_cust_id + ", parent_tree_path=" + parent_tree_path
				+ ", parent_name=" + parent_name + ", contacter=" + contacter
				+ ", annual_inspect_alert=" + annual_inspect_alert
				+ ", annual_inspect_date=" + annual_inspect_date + "]";
	}		
}
