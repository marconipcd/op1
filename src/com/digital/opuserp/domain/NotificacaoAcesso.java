package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.omg.PortableInterceptor.INACTIVE;

public class NotificacaoAcesso {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(name="EMPRESA_ID")
	private Integer empresa_id;
	@Column(name="ACESSO_CLIENTE")
	private Integer acesso_cliente;
	@Column(name="DESCRICAO")
	private String descricao;
	@Column(name="DATA_NOTIFICACAO")
	private Date data_notificacao;
	
	public NotificacaoAcesso(){
		
	}
	
	public NotificacaoAcesso(Integer id, Integer empresa_id, Integer acesso_cliente, String  descricao, Date data_notificacao){
		this.id= id;
		this.empresa_id = empresa_id;
		this.acesso_cliente = acesso_cliente;
		this.descricao = descricao;
		this.data_notificacao = data_notificacao;
	}
	
	public void setId(Integer id){
		this.id = id;
	}
	public Integer getId(){
		return id;
	}
	
	public void setEmpresaId(Integer empresa_id){
		this.empresa_id = empresa_id;
	}
	
	public void setAcessoCliente(Integer acesso_cliente){
		this.acesso_cliente = acesso_cliente;		
	}
	
	public Integer getAcessoCliente(){
		return acesso_cliente;
	}
}
