package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


public class DomainTeste {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@Column(name="NAME")
	private String name;
	@Column(name="ADDRESS")
	private String address;
	@Column(name="COUNTRY")
	private String country;
	@Column(name="FONE")
	private String fone;
	@Column(name="EMAIL")
	private String email;
	@Column(name="SINUP_DATE")
	private Date sinup_date;
	
	public DomainTeste(){
		
	}
	public DomainTeste(Integer id, String name, String address, String country, String fone, String email, Date sinup_date){
		this.id = id;
		this.name = name;
		this.address = address;
		this.country = country;
		this.fone = fone;
		this.email = email;
		this.sinup_date = sinup_date;
	}
	
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getAddress(){
		return address;
	}
	public void setAddress(String address){
		this.address = address;
	}
	public String getCountry(){
		return country;
	}
	public void setCountry(String country){
		this.country = country;
	}
	public String getFone(){
		return fone;
	}
	public void setFone(String fone){
		this.fone = fone;
	}
	public String getEmail(){
		return email;
	}
	public void setEmail(String email){
		this.email = email;
	}
	public Date getSinupDate(){
		return sinup_date;
	}
	public void setSinupDate(Date sinup_date){
		this.sinup_date = sinup_date;
	}
		
}
