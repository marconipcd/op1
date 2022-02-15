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

@Entity
@Table(name="alteracoes_materiais")
public class AlteracoesMateriais {
	
	@Id
	@Column(name="ID", nullable=false, unique=true)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="ACESSO_CLIENTE_ID", nullable=false)
	private AcessoCliente acesso_cliente;
	
	@Column(name="REGIME")
	private String regime;
	
	@Column(name="MAC")
	private String mac;
	
	@Column(name="DESCRICAO", nullable=false)
	private String descricao;
	
	@Column(name="DATA", nullable=false)
	private Date data;
	
	public AlteracoesMateriais(){
		
	}

	public AlteracoesMateriais(Integer id, AcessoCliente acesso_cliente,String regime,
			String mac, String descricao, Date data) {
		super();
		this.id = id;
		this.acesso_cliente = acesso_cliente;
		this.regime = regime;
		this.mac = mac;
		this.descricao = descricao;
		this.data = data;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public AcessoCliente getAcesso_cliente() {
		return acesso_cliente;
	}

	public void setAcesso_cliente(AcessoCliente acesso_cliente) {
		this.acesso_cliente = acesso_cliente;
	}

	
	public String getRegime() {
		return regime;
	}
//
	public void setRegime(String regime) {
		this.regime = regime;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	

}
