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

@Entity
@Table(name="usuario_concentradores")
public class UsuarioConcentradores {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="USUARIO")
	private String usuario;
	
	@Column(name="SENHA")
	private String senha;
	
	@Column(name="GRUPO")
	private String grupo;
	
	@Column(name="OPERADOR_ID")
	private Integer operador_id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALTERACAO")
	private Date data_alteracao;
	
	public UsuarioConcentradores(){
		
	}

	public UsuarioConcentradores(Integer id, String usuario, String senha,
			Integer operador_id, Date data_alteracao, String grupo) {
		super();
		this.id = id;
		this.usuario = usuario;
		this.senha = senha;
		this.operador_id = operador_id;
		this.data_alteracao = data_alteracao;
		this.grupo = grupo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Integer getOperador_id() {
		return operador_id;
	}

	public void setOperador_id(Integer operador_id) {
		this.operador_id = operador_id;
	}

	public Date getData_alteracao() {
		return data_alteracao;
	}

	public void setData_alteracao(Date data_alteracao) {
		this.data_alteracao = data_alteracao;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getUsuario() {
		return usuario;
	}
	
	
	
}
