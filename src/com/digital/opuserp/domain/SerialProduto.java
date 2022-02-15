package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="seriais_produto")
public class SerialProduto {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true)
	private Integer id;	
	
	@ManyToOne
	@JoinColumn(name="PRODUTO_ID")
	private Produto produto;
	
	@Column(name="SERIAL")
	private String serial;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="DATA_CADASTRO")
	private Date data_cadastro;
	
	@Column(name="DATA_ALTERACAO")
	private Date data_alteracao;
	
	@Column(name="TIPO_SERIAL")
	private String tipo_serial;
	
	public SerialProduto(){
		
	}

	public SerialProduto(Integer id, Produto produto, String serial,
			String status, Date data_cadastro,String tipo_serial) {
		super();
		this.id = id;
		this.produto = produto;
		this.serial = serial;
		this.status = status;
		this.data_cadastro = data_cadastro;
		this.tipo_serial = tipo_serial;
	}
	
	public SerialProduto(Integer id) {
		super();
		this.id = id;

	}
	
	public SerialProduto(Integer id, Produto produto, String serial,
			String status, Date data_cadastro) {
		super();
		this.id = id;
		this.produto = produto;
		this.serial = serial;
		this.status = status;
		this.data_cadastro = data_cadastro;
	}

	public SerialProduto(Integer id, Produto produto, String serial,
			String status, Date data_cadastro,Date data_alteracao) {
		super();
		this.id = id;
		this.produto = produto;
		this.serial = serial;
		this.status = status;
		this.data_cadastro = data_cadastro;
		this.data_alteracao = data_alteracao;
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

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public Date getData_alteracao() {
		return data_alteracao;
	}

	public void setData_alteracao(Date data_alteracao) {
		this.data_alteracao = data_alteracao;
	}

	public String getTipo_serial() {
		return tipo_serial;
	}

	public void setTipo_serial(String tipo_serial) {
		this.tipo_serial = tipo_serial;
	}
	
	
	
}
