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
@Table(name="chat")
public class Chat {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;	
	@OneToOne
	@JoinColumn(name="USUARIO_REMETENTE")
	private Usuario usuario_remetente;
	@OneToOne
	@JoinColumn(name="USUARIO_DESTINATARIO")	
	private Usuario usuario_destinatario;
	@Column(name="MENSAGEM")
	private String mensagem;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ENVIO")
	private Date data_envio;
	@Column(name="STATUS")
	private String status;
	
	public Chat(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Usuario getUsuario_remetente() {
		return usuario_remetente;
	}

	public void setUsuario_remetente(Usuario usuario_remetente) {
		this.usuario_remetente = usuario_remetente;
	}

	public Usuario getUsuario_destinatario() {
		return usuario_destinatario;
	}

	public void setUsuario_destinatario(Usuario usuario_destinatario) {
		this.usuario_destinatario = usuario_destinatario;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Date getData_envio() {
		return data_envio;
	}

	public void setData_envio(Date data_envio) {
		this.data_envio = data_envio;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
