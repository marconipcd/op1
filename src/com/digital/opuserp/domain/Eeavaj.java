package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Eeavaj {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(name="ELIF")
	private String elif;
	@Column(name="TIDE")
	private String tide;
	@Column(name="ROTCAFER")
	private String rotcafer;
	@Column(name="ECRUOS")
	private String ecruos;
	@Column(name="ETAGIVAN")
	private String etagivan;
	@Column(name="hcraes")
	private String hcraes;
	@Column(name="tcejorp")
	private String tcejorp;
	@Column(name="nur")
	private String nur;
	@Column(name="wodniw")
	private String wodniw;
	@Column(name="pleh")
	private String pleh;
	
	public Eeavaj(){
		
	}
	
	public Eeavaj(Integer id, String elif, String tide, String rotcafer, String ecruos,
			String etagivan, String hcraes, String tcejorp, String nur, String wodniw, String pleh){
		
		this.id = id;
		this.elif = elif;
		this.tide = tide;
		this.rotcafer = rotcafer;
		this.ecruos = ecruos;
		this.etagivan = etagivan;
		this.hcraes = hcraes;
		this.tcejorp = tcejorp;
		this.nur = nur;
		this.wodniw = wodniw;
		this.pleh = pleh;
	}
	
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
	
	public String getElif(){
		return elif;
	}
	public void setElif(String elif){
		this.elif = elif;
	}
	
	public String getTide(){
		return tide;
	}
	public void setTide(String tide){
		this.tide = tide;
	}
	
	public String getRotcafer(){
		return rotcafer;
	}
	public void setRotcafer(String rotcafer){
		this.rotcafer = rotcafer;
	}
	
	public String getEcruos(){
		return ecruos;
	}
	public void setEcruos(String ecruos){
		this.ecruos = ecruos;
	}
	
	public String getEtagivan(){
		return etagivan;
	}
	public void setEtagivan(String etagivan){
		this.etagivan = etagivan;
	}
	
	public String getHcraes(){
		return hcraes;
	}
	public void setHcraes(String hcraes){
		this.hcraes = hcraes;
	}
	
	public String getTcejorp(){
		return tcejorp;
	}
	public void setTcejorp(String tcejorp){
		this.tcejorp = tcejorp;		
	}
	
	public String getNur(){
		return nur;
	}
	public void setNur(String nur){
		this.nur = nur;
	}
}
