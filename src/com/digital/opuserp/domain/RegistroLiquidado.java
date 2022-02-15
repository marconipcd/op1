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
@Table(name="registro_liquidado")
public class RegistroLiquidado {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="NOSSO_NUMERO")
	private String nosso_numero;
	 
	@Column(name="VALOR_PAGO")
	private String valor_pago;
	
	@Column(name="VALOR_CREDITADO")
	private String valor_creditado;
	
	@Column(name="VALOR_TARIFA")
	private String valor_tarifa;
	
	@Column(name="DATA_PAGO")
	private String data_pago;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="COMANDO")
	private String comando;
	
	@Column(name="DESC_COMANDO")
	private String desc_comando;
	
	@Column(name="QTD_ENTRADA")
	private String qtdEntrada;
	@Column(name="QTD_LIQUIDADO")
	private String qtdLiquidado;
	@Column(name="QTD_RECUSADO")
	private String qtdRecusado;
	
	
	@Column(name="EMPRESA")
	private String empresa;
	
//	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO")
	private Date data_cadastro;
	
	@Column(name="NOME_ARQUIVO")
	private String nome_arquivo;
	
	public RegistroLiquidado(){
		
	}

	public RegistroLiquidado(Integer id, String nosso_numero,
			String valor_pago, String data_pago, String status, String empresa,
			Date data_cadastro) {
		super();
		this.id = id;
		this.nosso_numero = nosso_numero;
		this.valor_pago = valor_pago;
		this.data_pago = data_pago;
		this.status = status;
		this.empresa = empresa;
		this.data_cadastro = data_cadastro;
	}
	
	

	public RegistroLiquidado(Integer id, String nosso_numero,
			String valor_pago, String data_pago, String status, String comando,
			String desc_comando, String empresa, Date data_cadastro) {
		super();
		this.id = id;
		this.nosso_numero = nosso_numero;
		this.valor_pago = valor_pago;
		this.data_pago = data_pago;
		this.status = status;
		this.comando = comando;
		this.desc_comando = desc_comando;
		this.empresa = empresa;
		this.data_cadastro = data_cadastro;
	}
	
	public RegistroLiquidado(Integer id, String nosso_numero,
			String valor_pago, String valor_creditado, String valor_tarifa, String data_pago, String status, String comando,
			String desc_comando, String empresa, Date data_cadastro) {
		super();
		this.id = id;
		this.nosso_numero = nosso_numero;
		this.valor_pago = valor_pago;
		this.valor_creditado = valor_creditado;
		this.valor_tarifa = valor_tarifa;
		this.data_pago = data_pago;
		this.status = status;
		this.comando = comando;
		this.desc_comando = desc_comando;
		this.empresa = empresa;
		this.data_cadastro = data_cadastro;
	}
	
	public RegistroLiquidado(Integer id, String nosso_numero,
			String valor_pago, String valor_creditado, String valor_tarifa, String data_pago, String status, String comando,
			String desc_comando, String empresa, Date data_cadastro, String nome_arquivo) {
		super();
		this.id = id;
		this.nosso_numero = nosso_numero;
		this.valor_pago = valor_pago;
		this.valor_creditado = valor_creditado;
		this.valor_tarifa = valor_tarifa;
		this.data_pago = data_pago;
		this.status = status;
		this.comando = comando;
		this.desc_comando = desc_comando;
		this.empresa = empresa;
		this.data_cadastro = data_cadastro;
		this.nome_arquivo = nome_arquivo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNosso_numero() {
		return nosso_numero;
	}

	public void setNosso_numero(String nosso_numero) {
		this.nosso_numero = nosso_numero;
	}

	public String getValor_pago() {
		return valor_pago;
	}

	public void setValor_pago(String valor_pago) {
		this.valor_pago = valor_pago;
	}

	public String getData_pago() {
		return data_pago;
	}

	public void setData_pago(String data_pago) {
		this.data_pago = data_pago;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public String getComando() {
		return comando;
	}

	public void setComando(String comando) {
		this.comando = comando;
	}

	public String getDesc_comando() {
		return desc_comando;
	}

	public void setDesc_comando(String desc_comando) {
		this.desc_comando = desc_comando;
	}

	public String getValor_creditado() {
		return valor_creditado;
	}

	public void setValor_creditado(String valor_creditado) {
		this.valor_creditado = valor_creditado;
	}

	public String getValor_tarifa() {
		return valor_tarifa;
	}

	public void setValor_tarifa(String valor_tarifa) {
		this.valor_tarifa = valor_tarifa;
	}

	public String getQtdEntrada() {
		return qtdEntrada;
	}

	public void setQtdEntrada(String qtdEntrada) {
		this.qtdEntrada = qtdEntrada;
	}

	public String getQtdLiquidado() {
		return qtdLiquidado;
	}

	public void setQtdLiquidado(String qtdLiquidado) {
		this.qtdLiquidado = qtdLiquidado;
	}

	public String getQtdRecusado() {
		return qtdRecusado;
	}

	public void setQtdRecusado(String qtdRecusado) {
		this.qtdRecusado = qtdRecusado;
	}

	public String getNome_arquivo() {
		return nome_arquivo;
	}

	public void setNome_arquivo(String nome_arquivo) {
		this.nome_arquivo = nome_arquivo;
	}
	  
	
	
	  
	  
	  
	 
}
