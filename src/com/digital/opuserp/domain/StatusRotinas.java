package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.jcabi.aspects.Timeable;

@Entity
@Table(name="status_rotinas")
public class StatusRotinas {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@Column(name="EMPRESA_ID", nullable=false)
	private Integer empresa_id;
	@Column(name="DESC", nullable=false)
	private String desc;
	
	@Temporal(value=TemporalType.DATE)
	@Column(name="DATA", nullable=false)
	private Date data;
	
	public StatusRotinas(){
		
	}
	
	public StatusRotinas(Integer id, Integer empresa_id, String desc, Date data) {
		
		this.id = id;
		this.empresa_id = empresa_id;
		this.desc = desc;
		this.data = data;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmpresa_id() {
		return empresa_id;
	}

	public void setEmpresa_id(Integer empresa_id) {
		this.empresa_id = empresa_id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	
	
}
