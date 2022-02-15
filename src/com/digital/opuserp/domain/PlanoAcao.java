package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="plano_acao")
public class PlanoAcao {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="ACAO")
	private String acao;
	
	@Column(name="ORIGEM_ACAO")
	private String origem_acao;
	
	@Column(name="OBJETIVO")
	private String objetivo;
	
	@Column(name="COMO")
	private String como;
	
	@Column(name="RESPONSAVEL")
	private String responsavel;
	
	@Column(name="ONDE")
	private String onde;
	
	@Temporal(TemporalType.DATE)
	@Column(name="DATA_ABERTURA")
	private Date data_abertura;
	
	@Temporal(TemporalType.DATE)
	@Column(name="DATA_PRAZO")
	private Date data_prazo;
	
	@Temporal(TemporalType.DATE)
	@Column(name="DATA_CONCLUSAO")
	private Date data_conclusao;
	
	@Column(name="STATUS")
	private String status;
	
	@OneToOne
	@JoinColumn(name="OPERADOR_ID")
	private Usuario operador;
	
	
	public PlanoAcao(){
		
	}
	
	public PlanoAcao(Integer id, String acao, String origem_acao,
			String objetivo, String como, String responsavel, String onde,
			Date data_abertura, Date data_prazo, Date data_conclusao,
			String status, Usuario operador) {
		super();
		this.id = id;
		this.acao = acao;
		this.origem_acao = origem_acao;
		this.objetivo = objetivo;
		this.como = como;
		this.responsavel = responsavel;
		this.onde = onde;
		this.data_abertura = data_abertura;
		this.data_prazo = data_prazo;
		this.data_conclusao = data_conclusao;
		this.status = status;
		this.operador = operador;
	}

	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getAcao() {
		return acao;
	}


	public void setAcao(String acao) {
		this.acao = acao;
	}


	public String getOrigem_acao() {
		return origem_acao;
	}


	public void setOrigem_acao(String origem_acao) {
		this.origem_acao = origem_acao;
	}


	public String getObjetivo() {
		return objetivo;
	}


	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}


	public String getComo() {
		return como;
	}


	public void setComo(String como) {
		this.como = como;
	}


	public String getResponsavel() {
		return responsavel;
	}


	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}


	public String getOnde() {
		return onde;
	}


	public void setOnde(String onde) {
		this.onde = onde;
	}


	public Date getData_abertura() {
		return data_abertura;
	}


	public void setData_abertura(Date data_abertura) {
		this.data_abertura = data_abertura;
	}


	public Date getData_prazo() {
		return data_prazo;
	}


	public void setData_prazo(Date data_prazo) {
		this.data_prazo = data_prazo;
	}


	public Date getData_conclusao() {
		return data_conclusao;
	}


	public void setData_conclusao(Date data_conclusao) {
		this.data_conclusao = data_conclusao;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Usuario getOperador() {
		return operador;
	}


	public void setOperador(Usuario operador) {
		this.operador = operador;
	}
	
	
}
