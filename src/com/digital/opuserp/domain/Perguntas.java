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
@Table(name="crm_perguntas")
public class Perguntas {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@OneToOne
	@JoinColumn(name="ASSUNTO_ID",nullable=false)
	private CrmAssunto assunto;
	@Column(name="PERGUNTA", nullable=false)
	private String pergunta;
	@Column(name="PRE_RESPOSTAS", nullable=true)
	private boolean pre_respostas;
	@Column(name="STATUS", nullable=false)
	private String status;
	
	public Perguntas(){
		
	}

	public Perguntas(Integer id, CrmAssunto assunto, String pergunta,boolean pre_respostas, String status) {
		super();
		this.id = id;
		this.assunto = assunto;
		this.pergunta = pergunta;
		this.pre_respostas = pre_respostas;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CrmAssunto getAssunto() {
		return assunto;
	}

	public void setAssunto(CrmAssunto assunto) {
		this.assunto = assunto;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}
	
	public boolean isPre_respostas() {
		return pre_respostas;
	}

	public void setPre_respostas(boolean pre_respostas) {
		this.pre_respostas = pre_respostas;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
