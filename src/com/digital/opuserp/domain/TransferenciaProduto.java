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
@Table(name="tranferencia_produto")
public class TransferenciaProduto {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false)
	private Integer id;
	
	@Column(name="TIPO")
	private String tipo;
	
	@Column(name="TIPO_TRANSFERENCIA")
	private String tipo_transferencia;
	
	@OneToOne
    @JoinColumn(name="PRODUTO_ID", nullable=false)
	private Produto produto;
	
	@Column(name="QTD", nullable=false)
	private Float qtd;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_INICIO_TRANSFERENCIA", nullable=false)
	private Date data_inicio_transferencia;
	
	@Column(name="USUARIO_TRANSFERENCIA", nullable=false)
	private String usuario_transferencia;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CONCLUSAO_TRANSFERENCIA")
	private Date data_conclusao_transferencia;
	
	@Column(name="USUARIO_ACEITOU_TRANSFERENCIA")
	private String usuario_aceitou_transferencia;
	
	//PENDENTE, CONCLUIDO, CANCELADO
	@Column(name="STATUS")
	private String status;
	
	public TransferenciaProduto(){
		
	}

	public TransferenciaProduto(Integer id, Produto produto, Float qtd,
			Date data_inicio_transferencia, String usuario_transferencia,
			Date data_conclusao_transferencia,
			String usuario_aceitou_transferencia, String status) {
		
		this.id = id;
		this.produto = produto;
		this.qtd = qtd;
		this.data_inicio_transferencia = data_inicio_transferencia;
		this.usuario_transferencia = usuario_transferencia;
		this.data_conclusao_transferencia = data_conclusao_transferencia;
		this.usuario_aceitou_transferencia = usuario_aceitou_transferencia;
		this.status = status;
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

	public Float getQtd() {
		return qtd;
	}

	public void setQtd(Float qtd) {
		this.qtd = qtd;
	}

	public Date getData_inicio_transferencia() {
		return data_inicio_transferencia;
	}

	public void setData_inicio_transferencia(Date data_inicio_transferencia) {
		this.data_inicio_transferencia = data_inicio_transferencia;
	}

	public String getUsuario_transferencia() {
		return usuario_transferencia;
	}

	public void setUsuario_transferencia(String usuario_transferencia) {
		this.usuario_transferencia = usuario_transferencia;
	}

	public Date getData_conclusao_transferencia() {
		return data_conclusao_transferencia;
	}

	public void setData_conclusao_transferencia(Date data_conclusao_transferencia) {
		this.data_conclusao_transferencia = data_conclusao_transferencia;
	}

	public String getUsuario_aceitou_transferencia() {
		return usuario_aceitou_transferencia;
	}

	public void setUsuario_aceitou_transferencia(
			String usuario_aceitou_transferencia) {
		this.usuario_aceitou_transferencia = usuario_aceitou_transferencia;
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

	public String getTipo_transferencia() {
		return tipo_transferencia;
	}

	public void setTipo_transferencia(String tipo_transferencia) {
		this.tipo_transferencia = tipo_transferencia;
	}
	
	
	
    
}
