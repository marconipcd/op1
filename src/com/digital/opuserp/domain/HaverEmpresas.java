package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="haver_empresas")
public class HaverEmpresas {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@OneToOne
	@JoinColumn(name="EMPRESA_ID")
	private Empresa empresa;
	@OneToOne
	@JoinColumn(name="HAVER_ID")
	private Haver haver;
	
	public HaverEmpresas(){
		
	}
	
	

	public HaverEmpresas(Integer id, Empresa empresa, Haver haver) {
		super();
		this.id = id;
		this.empresa = empresa;
		this.haver = haver;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Haver getHaver() {
		return haver;
	}

	public void setHaver(Haver haver) {
		this.haver = haver;
	}
	
	
}
