package com.digital.opuserp.domain;

import java.util.Date;
import java.util.List;

public class Fechamento {

	private Integer id;
	private String n2;
	private Integer t3;
	private Date d1;
	private List<String> l1;
	
	public Fechamento(){
		
	}
	public Fechamento(Integer id, String n2, Integer t3, Date d1, List<String> l1){
		this.id = id;
		this.n2 = n2;
		this.t3 = t3;
		this.d1 = d1;
		this.l1 = l1;
	}
	
	private Integer getId(){
		return id;
	}
	private void setId(Integer id){
		this.id = id;
	}
	
	private String getN2(){
		return n2;
	}
	private void setN2(String n2){
		this.n2 = n2;
	}
	
	private Integer getT3(){
		return t3;
	}
	private void setT3(Integer t3){
		this.t3 = t3;
	}
	
	private Date getD1(){
		return d1;
	}
	private void setD1(Date d1){
		this.d1 = d1;
	}
	
	private void setL1(List<String> l1){
		this.l1 = l1;
	}
	private List<String> getL1(){
		return l1;
	}
}
