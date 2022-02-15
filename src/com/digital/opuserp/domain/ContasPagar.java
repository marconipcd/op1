package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.digital.opuserp.OpusERP4UI;

@Entity
@Table(name="contas_pagar")
@Cacheable(value=false)
public class ContasPagar {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID",nullable=false, unique=true)
	private Integer id;
	@Column(name="VALOR_TITULO",nullable=true, unique=false)
	private Double valor_titulo;
	@Column(name="VALOR_PAGAMENTO",nullable=true, unique=false)
	private Double valor_pagamento;	
	@Column(name="DATA_CADASTRO",nullable=true, unique=false)
	@Temporal(TemporalType.DATE)
	private Date  data_cadastro;	
	@Column(name="DATA_VENCIMENTO",nullable=true, unique=false)
	@Temporal(TemporalType.DATE)
	private Date data_vencimento;
	@Column(name="DATA_PAGO",nullable=true, unique=false)
	@Temporal(TemporalType.DATE)
	private Date data_pago;
	@Column(name="STATUS",nullable=true, unique=false)
	private String status;
	@Column(name="TIPO",nullable=true, unique=false)
	private String tipo;
	@Column(name="TERMO",nullable=true, unique=false)
	private String termo;
	
	@Column(name="DESCRICAO",nullable=true, unique=false)
	private String descricao;	
	@Column(name="PARCELA",nullable=true, unique=false)
	private String parcela;	

	@Column(name="INTERVALO",nullable=true, unique=false)
	private String intervalo;
	
	@Column(name="N_DOC",nullable=true, unique=false)
	private String n_doc;

	@Column(name="empresa_id",nullable=true, unique=false)
	private Integer empresa_id;
	
	@ManyToOne
	@JoinColumn(nullable=false, referencedColumnName="id", name="CONTA")
	private Contas conta;
	
	@ManyToOne
	@JoinColumn(nullable=true, referencedColumnName="id",name="FORNECEDOR")
	private Fornecedor fornecedor;	
	
//	@ManyToOne
//	@JoinColumn(nullable=true, referencedColumnName="id", name="FORMA_PGTO")
//	private Contas forma_pgto;	


	@Column(nullable=true, name="FORMA_PGTO")
	private String forma_pgto;	
	
	@Transient
	private String coluna;
	@Transient
	private Date coluna_date;
	@Transient
	private Integer coluna_Inter;
	@Transient
	private Long qtd;
	@Transient
	private Double coluna_Double;
	
	public ContasPagar(String coluna, Long qtd) {		
		this.coluna = coluna;
		this.qtd = qtd;
	}
	
	public ContasPagar(Date coluna_date, Long qtd) {		
		this.coluna_date = coluna_date;
		this.qtd = qtd;
	}

	public ContasPagar(Integer coluna_Inter, Long qtd) {		
		this.coluna_Inter = coluna_Inter;
		this.qtd = qtd;
	}
	
	public ContasPagar(Double coluna_Double, Long qtd) {		
		this.coluna_Double = coluna_Double;
		this.qtd = qtd;
	}

	public Double getColuna_Double() {
		return coluna_Double;
	}

	public void setColuna_Double(Double coluna_Double) {
		this.coluna_Double = coluna_Double;
	}
	
	public String getColuna() {
		return coluna;
	}

	public void setColuna(String coluna) {
		this.coluna = coluna;
	}
	
	public Long getQtd() {
		return qtd;
	}

	public void setQtd(Long qtd) {
		this.qtd = qtd;
	}
	
	public ContasPagar(){
		
	}
	
	public ContasPagar(Integer id){
		this.id = id;
	}

	
	public ContasPagar(Contas conta, Double valor_titulo, Date data_cadastro,
			Date data_vencimento, String status, String tipo, String termo,
			Fornecedor fornecedor, String descricao,String intervalo,
			Integer empresa_id, String n_doc, String parcela) {
		super();
		
		this.conta = conta;
		this.valor_titulo = valor_titulo;
		this.data_cadastro = data_cadastro;
		this.data_vencimento = data_vencimento;
		this.status = status;
		this.tipo = tipo;
		this.termo = termo;
		this.fornecedor = fornecedor;
		this.descricao = descricao;
		this.intervalo = intervalo;
		this.empresa_id = empresa_id;
		this.n_doc = n_doc;
		this.parcela = parcela;
	}

	public ContasPagar(Integer id, Double valor_titulo, Double valor_pagamento,
			Date data_cadastro, Date data_vencimento, Date data_pago,
			String status, String tipo, String termo, String descricao,
			String parcela, String intervalo, String n_doc, Integer empresa_id,
			Contas conta, Fornecedor fornecedor, String forma_pgto,
			String coluna, Date coluna_date, Integer coluna_Inter, Long qtd,
			Double coluna_Double) {
		super();
		this.id = id;
		this.valor_titulo = valor_titulo;
		this.valor_pagamento = valor_pagamento;
		this.data_cadastro = data_cadastro;
		this.data_vencimento = data_vencimento;
		this.data_pago = data_pago;
		this.status = status;
		this.tipo = tipo;
		this.termo = termo;
		this.descricao = descricao;
		this.parcela = parcela;
		this.intervalo = intervalo;
		this.n_doc = n_doc;
		this.empresa_id = empresa_id;
		this.conta = conta;
		this.fornecedor = fornecedor;
		this.forma_pgto = forma_pgto;
		this.coluna = coluna;
		this.coluna_date = coluna_date;
		this.coluna_Inter = coluna_Inter;
		this.qtd = qtd;
		this.coluna_Double = coluna_Double;
	}

	public ContasPagar(Integer id, Double valor_titulo, Double valor_pagamento,
			Date data_cadastro, Date data_vencimento, Date data_pago,
			String status, String tipo, String termo, Fornecedor fornecedor,
			String descricao, String parcela, Contas conta, String intervalo,
			Integer empresa_id, String coluna, Date coluna_date,
			Integer coluna_Inter, Long qtd, String n_doc) {
		super();
		this.id = id;
		this.valor_titulo = valor_titulo;
		this.valor_pagamento = valor_pagamento;
		this.data_cadastro = data_cadastro;
		this.data_vencimento = data_vencimento;
		this.data_pago = data_pago;
		this.status = status;
		this.tipo = tipo;
		this.termo = termo;
		this.fornecedor = fornecedor;
		this.descricao = descricao;
		this.parcela = parcela;
		this.conta = conta;
		this.intervalo = intervalo;
		this.empresa_id = empresa_id;
		this.coluna = coluna;
		this.coluna_date = coluna_date;
		this.coluna_Inter = coluna_Inter;
		this.qtd = qtd;
		this.n_doc = n_doc;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getValor_titulo() {
		return valor_titulo;
	}

	public void setValor_titulo(Double valor_titulo) {
		this.valor_titulo = valor_titulo;
	}

	public Double getValor_pagamento() {
		return valor_pagamento;
	}

	public void setValor_pagamento(Double valor_pagamento) {
		this.valor_pagamento = valor_pagamento;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public Date getData_vencimento() {
		return data_vencimento;
	}

	public void setData_vencimento(Date data_vencimento) {
		this.data_vencimento = data_vencimento;
	}

	public Date getData_pago() {
		return data_pago;
	}

	public void setData_pago(Date data_pago) {
		this.data_pago = data_pago;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTermo() {
		return termo;
	}

	public void setTermo(String termo) {
		this.termo = termo;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getParcela() {
		return parcela;
	}

	public void setParcela(String parcela) {
		this.parcela = parcela;
	}

	public Contas getConta() {
		return conta;
	}

	public void setConta(Contas conta) {
		this.conta = conta;
	}

	public String getintervalo() {
		return intervalo;
	}

	public void setintervalo(String intervalo) {
		this.intervalo = intervalo;
	}

	public Integer getEmpresa_id() {
		return empresa_id;
	}

	public void setEmpresa_id(Integer empresa_id) {
		this.empresa_id = empresa_id;
	}

	public Date getColuna_date() {
		return coluna_date;
	}

	public void setColuna_date(Date coluna_date) {
		this.coluna_date = coluna_date;
	}

	public Integer getColuna_Inter() {
		return coluna_Inter;
	}

	public void setColuna_Inter(Integer coluna_Inter) {
		this.coluna_Inter = coluna_Inter;
	}

	public String getN_doc() {
		return n_doc;
	}

	public void setN_doc(String n_doc) {
		this.n_doc = n_doc;
	}

	public String getForma_pgto() {
		return forma_pgto;
	}

	public void setForma_pgto(String forma_pgto) {
		this.forma_pgto = forma_pgto;
	}

	
	

}

