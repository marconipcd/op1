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
@Table(name="estoque_movel")
public class EstoqueMovel {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="EMPRESA_ID")
	private Integer empresa_id;
	
	@JoinColumn(name="PRODUTO_ID")
	@OneToOne
	private Produto produto;
	
	@JoinColumn(name="VEICULO_ID")
	@OneToOne
	private Veiculos veiculo;
	
	@Column(name="QTD")
	private double qtd;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO")
	private Date data_cadastro;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ULTIMA_ALTERACAO")
	private Date data_ultima_alteracao;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="INFO_ADICIONAIS")
	private String info_adicionais;
	
	public EstoqueMovel(){
		
	}
	
	public EstoqueMovel(Integer id){
		this.id = id;
	}

	public EstoqueMovel(Integer id, Integer empresa_id, Produto produto,
			Veiculos veiculo, double qtd, Date data_cadastro,
			Date data_ultima_alteracao) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.produto = produto;
		this.veiculo = veiculo;
		this.qtd = qtd;
		this.data_cadastro = data_cadastro;
		this.data_ultima_alteracao = data_ultima_alteracao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmpresa_id() {
		return empresa_id;
	}

	public void setEmpresa_id(Integer empresa_id) {
		this.empresa_id = empresa_id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Veiculos getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculos veiculo) {
		this.veiculo = veiculo;
	}

	public double getQtd() {
		return qtd;
	}

	public void setQtd(double qtd) {
		this.qtd = qtd;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public Date getData_ultima_alteracao() {
		return data_ultima_alteracao;
	}

	public void setData_ultima_alteracao(Date data_ultima_alteracao) {
		this.data_ultima_alteracao = data_ultima_alteracao;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInfo_adicionais() {
		return info_adicionais;
	}

	public void setInfo_adicionais(String info_adicionais) {
		this.info_adicionais = info_adicionais;
	}
	
	
	
	
}
