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
@Table(name="haver")
public class Haver {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;	
	@OneToOne
	@JoinColumn(name="HAVER_CAB_ID")
	private HaverCab haver_cab;
	@OneToOne
	@JoinColumn(name="CLIENTE_ID")
	private Cliente cliente;	
	@Column(name="DOC")
	private String doc;
	@Column(name="N_DOC")
	private String nDoc;
	@Column(name="REFERENTE")
	private String referente;
	@Column(name="OPERADOR")
	private String usuario;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_EMISSAO")
	private Date data_emissao;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALTERACAO")
	private Date data_alteracao;
	@Column(name="VALOR")
	private double valor;
	@Column(name="VALOR_DISPONIVEL")
	private double valor_disponivel;
	@Column(name="STATUS")
	private String status;
	
	public Haver(){
		
	}
	
	

	public Haver(Integer id) {
		super();
		this.id = id;
	}

	public Haver(Integer id, Cliente cliente, String doc,
			String nDoc, String referente, String usuario, Date data_emissao,
			double valor, String status) {
		super();
		this.id = id;
		
		this.cliente = cliente;
		this.doc = doc;
		this.nDoc = nDoc;
		this.referente = referente;
		this.usuario = usuario;
		this.data_emissao = data_emissao;
		this.valor = valor;
		this.status = status;
	}
	
	public Haver(Integer id, Cliente cliente, String doc,
			String nDoc, String referente, String usuario, Date data_emissao,
			double valor,double valor_disponivel, String status) {
		super();
		this.id = id;
		
		this.cliente = cliente;
		this.doc = doc;
		this.nDoc = nDoc;
		this.referente = referente;
		this.usuario = usuario;
		this.data_emissao = data_emissao;
		this.valor = valor;
		this.valor_disponivel = valor_disponivel;
		this.status = status;
	}



	public Haver(Integer id, HaverCab haver_cab, Cliente cliente, String doc,
			String nDoc, String referente, String usuario, Date data_emissao,
			Date data_alteracao, double valor, double valor_disponivel,
			String status) {
		super();
		this.id = id;
		this.haver_cab = haver_cab;
		this.cliente = cliente;
		this.doc = doc;
		this.nDoc = nDoc;
		this.referente = referente;
		this.usuario = usuario;
		this.data_emissao = data_emissao;
		this.data_alteracao = data_alteracao;
		this.valor = valor;
		this.valor_disponivel = valor_disponivel;
		this.status = status;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getnDoc() {
		return nDoc;
	}

	public void setnDoc(String nDoc) {
		this.nDoc = nDoc;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getData_emissao() {
		return data_emissao;
	}

	public void setData_emissao(Date data_emissao) {
		this.data_emissao = data_emissao;
	}

	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}



	public String getReferente() {
		return referente;
	}



	public void setReferente(String referente) {
		this.referente = referente;
	}



	public double getValor_disponivel() {
		return valor_disponivel;
	}



	public void setValor_disponivel(double valor_disponivel) {
		this.valor_disponivel = valor_disponivel;
	}



	public Date getData_alteracao() {
		return data_alteracao;
	}



	public void setData_alteracao(Date data_alteracao) {
		this.data_alteracao = data_alteracao;
	}



	public HaverCab getHaver_cab() {
		return haver_cab;
	}



	public void setHaver_cab(HaverCab haver_cab) {
		this.haver_cab = haver_cab;
	}

	

	
	
	
	
}
