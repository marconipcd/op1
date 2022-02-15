package com.digital.opuserp.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

public class contelePoi {

	private String createdAt;
    private String updatedAt;
    private String id;
    private String customId;
    private String name;
    private String corporateName;
    private String cnpj_cpf;
    private String lat;
    private String lng;
    private String status;
    private String categoryId;
    private String obs;
    
   
    private contelePoiAddress address;  
    private List<contelePoiContact> contacts;
    
    
    @JsonGetter("createdAt")
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	@JsonGetter("updatedAt")
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	@JsonGetter("id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@JsonGetter("customId")
	public String getCustomId() {
		return customId;
	}
	public void setCustomId(String customId) {
		this.customId = customId;
	}
	
	@JsonGetter("name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonGetter("corporateName")
	public String getCorporateName() {
		return corporateName;
	}
	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}
	
	@JsonGetter("cnpj_cpf")
	public String getCnpj_cpf() {
		return cnpj_cpf;
	}
	public void setCnpj_cpf(String cnpj_cpf) {
		this.cnpj_cpf = cnpj_cpf;
	}
	
	@JsonGetter("lat")
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	
	@JsonGetter("lng")
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	
	@JsonGetter("status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonGetter("categoryId")
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	
	@JsonGetter("obs")
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	
	@JsonGetter("address")
	public contelePoiAddress getAddress() {
		return address;
	}
	public void setAddress(contelePoiAddress address) {
		this.address = address;
	}
	
	@JsonGetter("contacts")
	public List<contelePoiContact> getContacts() {
		return contacts;
	}
	public void setContacts(List<contelePoiContact> contacts) {
		this.contacts = contacts;
	}
   

}
