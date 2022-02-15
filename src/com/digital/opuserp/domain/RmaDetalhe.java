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
@Table(name="rma_detalhe")
public class RmaDetalhe {

	@Id
	@Column(name="ID", nullable=false)	
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="RMA_MESTRE_ID")
	private Integer rma_mestre_id;
	
	@Column(name="EMPRESA_ID", nullable=false)
	private Integer empresa_id;
	
	@OneToOne
	@JoinColumn(name="PRODUTO_ID", nullable=false)
	private Produto produto;
	@Column(name="NF_COMPRA", nullable=false)
	private String nf_compra;
	@Column(name="DEFEITO", nullable=false)
	private String defeito;
	@Column(name="VENC_GARANTIA", nullable=false)
	private Date venc;
	@OneToOne
	@JoinColumn(name="SERIAL", nullable=true)
	private SerialProduto serial;
	@Column(name="STATUS", nullable=false)
	private String status;
	@Column(name="DATA_MUDANCA_STATUS", nullable=false)
	private Date data_mudanca_status;
	

	public RmaDetalhe(){
		
	}
	public RmaDetalhe(Integer id, Integer empresa_id,  Produto produto,String nf_compra,String defeito, Date venc,String status,SerialProduto serial){
		this.empresa_id = empresa_id;
		this.id = id;				
		this.nf_compra = nf_compra;
		this.defeito = defeito;
		this.venc = venc;
		this.status = status;
		this.serial = serial;
		this.produto = produto;		
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}	
	public Produto getProduto() {
		return produto;
	}
	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	public String getNf_compra() {
		return nf_compra;
	}
	public void setNf_compra(String nf_compra) {
		this.nf_compra = nf_compra;
	}
	public String getDefeito() {
		return defeito;
	}
	public void setDefeito(String defeito) {
		this.defeito = defeito;
	}
	
	public Date getVenc() {
		return venc;
	}
	public void setVenc(Date venc) {
		this.venc = venc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public SerialProduto getSerial() {
		return serial;
	}
	public void setSerial(SerialProduto serial) {
		this.serial = serial;
	}
	public Integer getEmpresa_id() {
		return empresa_id;
	}
	public void setEmpresa_id(Integer empresa_id) {
		this.empresa_id = empresa_id;
	}
	public Date getData_mudanca_status() {
		return data_mudanca_status;
	}
	public void setData_mudanca_status(Date data_mudanca_status) {
		this.data_mudanca_status = data_mudanca_status;
	}
	public Integer getRma_mestre_id() {
		return rma_mestre_id;
	}
	public void setRma_mestre_id(Integer rma_mestre_id) {
		this.rma_mestre_id = rma_mestre_id;
	}
	
	
	
}
