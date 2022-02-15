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
@Table(name="config_central_assinante")
public class ConfigCentralAssinante {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false)
	private Integer id;
	@OneToOne
	@JoinColumn(name="SETOR_ID")
	private Setores setor;
	@OneToOne
	@JoinColumn(name="ASSUNTO_ID")
	private CrmAssunto assunto;
	
	public ConfigCentralAssinante(){
		
	}

	public ConfigCentralAssinante(Integer id, Setores setor, CrmAssunto assunto) {
		super();
		this.id = id;
		this.setor = setor;
		this.assunto = assunto;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Setores getSetor() {
		return setor;
	}

	public void setSetor(Setores setor) {
		this.setor = setor;
	}

	public CrmAssunto getAssunto() {
		return assunto;
	}

	public void setAssunto(CrmAssunto assunto) {
		this.assunto = assunto;
	}
	
	
	
	
}
