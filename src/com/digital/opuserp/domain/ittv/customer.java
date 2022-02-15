package com.digital.opuserp.domain.ittv;

import com.fasterxml.jackson.annotation.JsonGetter;

public class customer {
	
	private String _id;
	private String firstName;
	private String status;
	private String email;
	private String username;
	private String serviceProvide;
	private String reseller;
	private String created;
	private String updated;
	private String activated;
	
	@JsonGetter("_id")
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	
	@JsonGetter("firstName")
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@JsonGetter("status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonGetter("email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@JsonGetter("username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@JsonGetter("serviceProvide")
	public String getServiceProvide() {
		return serviceProvide;
	}
	public void setServiceProvide(String serviceProvide) {
		this.serviceProvide = serviceProvide;
	}
	
	@JsonGetter("reseller")
	public String getReseller() {
		return reseller;
	}
	public void setReseller(String reseller) {
		this.reseller = reseller;
	}
	
	@JsonGetter("created")
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	
	@JsonGetter("updated")
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	
	@JsonGetter("activated")
	public String getActivated() {
		return activated;
	}
	public void setActivated(String activated) {
		this.activated = activated;
	}
	
	

}
