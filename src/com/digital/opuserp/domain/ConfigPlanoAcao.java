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
@Table(name="config_plano_acao")
public class ConfigPlanoAcao {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="NOME_PLANO")
	private String nome_plano;
	
	@Column(name="NOME_GESTOR")
	private String nome_gestor;
	
	@Column(name="DATA_CRIACAO")
	private String data_criacao;
	
	@OneToOne
	@JoinColumn(name="OPERADOR_ID")
	private Usuario operador;
	
	public ConfigPlanoAcao(){
		
	}

	public ConfigPlanoAcao(Integer id, String nome_plano, String nome_gestor,
			String data_criacao, Usuario operador) {
		super();
		this.id = id;
		this.nome_plano = nome_plano;
		this.nome_gestor = nome_gestor;
		this.data_criacao = data_criacao;
		this.operador = operador;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome_plano() {
		return nome_plano;
	}

	public void setNome_plano(String nome_plano) {
		this.nome_plano = nome_plano;
	}

	public String getNome_gestor() {
		return nome_gestor;
	}

	public void setNome_gestor(String nome_gestor) {
		this.nome_gestor = nome_gestor;
	}

	public String getData_criacao() {
		return data_criacao;
	}

	public void setData_criacao(String data_criacao) {
		this.data_criacao = data_criacao;
	}

	public Usuario getOperador() {
		return operador;
	}

	public void setOperador(Usuario operador) {
		this.operador = operador;
	}
	
	
}
