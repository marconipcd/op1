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
@Table(name="log_pedidos")
public class LogPedidos {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="PEDIDO_ID")
	private EcfPreVendaCabecalho pedido;
	
	@Column(name="USUARIO")
	private String usuario;
	
	@Column(name="ACAO")
	private String acao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA")
	private Date data;
	
	public LogPedidos(){
		
	}
	
	public LogPedidos(Integer id, EcfPreVendaCabecalho pedido, String usuario, String acao, Date data){
		
		this.id = id;
		this.pedido = pedido;
		this.usuario = usuario;
		this.acao = acao;
		this.data = data;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EcfPreVendaCabecalho getPedido() {
		return pedido;
	}

	public void setPedido(EcfPreVendaCabecalho pedido) {
		this.pedido = pedido;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	
}
