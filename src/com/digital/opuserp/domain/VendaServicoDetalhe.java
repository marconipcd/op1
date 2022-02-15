package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="venda_servico_detalhe")
public class VendaServicoDetalhe {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer ID;
	
	@Column(name="VENDA_SERVICO_CABECALHO_ID")
	private Integer VENDA_SERVICO_CABECALHO_ID;
	
	@Column(name="ID_PRODUTO")
	private Integer ID_PRODUTO;
	
	@Column(name="ORDEM")
	private Integer ORDEM;
	
	@Column(name="QUANTIDADE")
	private Float QUANTIDADE;
	
	@Column(name="VALOR_UNITARIO")
	private Float VALOR_UNITARIO;
	
	@Column(name="VALOR_TOTAL")
	private Float VALOR_TOTAL;
	
	@Column(name="CANCELADO")
	private String CANCELADO;
	
	public VendaServicoDetalhe(){
		
	}

	public VendaServicoDetalhe(Integer iD, Integer vENDA_SERVICO_CABECALHO_ID,
			Integer iD_PRODUTO, Integer oRDEM, Float qUANTIDADE,
			Float vALOR_UNITARIO, Float vALOR_TOTAL, String cANCELADO) {
		super();
		ID = iD;
		VENDA_SERVICO_CABECALHO_ID = vENDA_SERVICO_CABECALHO_ID;
		ID_PRODUTO = iD_PRODUTO;
		ORDEM = oRDEM;
		QUANTIDADE = qUANTIDADE;
		VALOR_UNITARIO = vALOR_UNITARIO;
		VALOR_TOTAL = vALOR_TOTAL;
		CANCELADO = cANCELADO;
	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public Integer getVENDA_SERVICO_CABECALHO_ID() {
		return VENDA_SERVICO_CABECALHO_ID;
	}

	public void setVENDA_SERVICO_CABECALHO_ID(Integer vENDA_SERVICO_CABECALHO_ID) {
		VENDA_SERVICO_CABECALHO_ID = vENDA_SERVICO_CABECALHO_ID;
	}

	public Integer getID_PRODUTO() {
		return ID_PRODUTO;
	}

	public void setID_PRODUTO(Integer iD_PRODUTO) {
		ID_PRODUTO = iD_PRODUTO;
	}

	public Integer getORDEM() {
		return ORDEM;
	}

	public void setORDEM(Integer oRDEM) {
		ORDEM = oRDEM;
	}

	public Float getQUANTIDADE() {
		return QUANTIDADE;
	}

	public void setQUANTIDADE(Float qUANTIDADE) {
		QUANTIDADE = qUANTIDADE;
	}

	public Float getVALOR_UNITARIO() {
		return VALOR_UNITARIO;
	}

	public void setVALOR_UNITARIO(Float vALOR_UNITARIO) {
		VALOR_UNITARIO = vALOR_UNITARIO;
	}

	public Float getVALOR_TOTAL() {
		return VALOR_TOTAL;
	}

	public void setVALOR_TOTAL(Float vALOR_TOTAL) {
		VALOR_TOTAL = vALOR_TOTAL;
	}

	public String getCANCELADO() {
		return CANCELADO;
	}

	public void setCANCELADO(String cANCELADO) {
		CANCELADO = cANCELADO;
	}
	
	
}
