package com.digital.opuserp.domain;

import com.fasterxml.jackson.annotation.JsonGetter;

public class contelePoiContact {
	
	private String name;
	private String phone;
	private String email;
	
	@JsonGetter("name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonGetter("phone")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@JsonGetter("email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
