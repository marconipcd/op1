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
@Table(name="crm_perguntas_respostas")
public class Respostas {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@OneToOne
	@JoinColumn(name="PERGUNTA_ID",nullable=false)
	private Perguntas pergunta;
	@Column(name="RESPOSTA", nullable=false)
	private String resposta;
	
	public Respostas(){
		
	}
	public Respostas(Integer id, Perguntas pergunta, String resposta){
		this.id = id;
		this.pergunta = pergunta;
		this.resposta = resposta;				
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Perguntas getPergunta() {
		return pergunta;
	}

	public void setPergunta(Perguntas pergunta) {
		this.pergunta = pergunta;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
	
}
