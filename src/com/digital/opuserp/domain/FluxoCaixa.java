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

import com.jcabi.aspects.Timeable;

@Entity
@Table(name="fluxoCaixa")
public class FluxoCaixa {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="EMPRESA_ID") 
	private Integer empresa;
	
	@Column(name="TIPO")
	private String tipo;
	
	@Column(name="REF")
	private String ref;
	
	@Column(name="VALOR")
	private double valor;
	
	@OneToOne
	@JoinColumn(name="USUARIO_ID")
	private Usuario usuario;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	@Column(name="DATA")
	private Date data;
	
	public FluxoCaixa(){
		
	}

	public FluxoCaixa(Integer id, Integer empresa, String tipo, String ref,
			double valor, Usuario usuario, Date data) {
		super();
		this.id = id;
		this.empresa = empresa;
		this.tipo = tipo;
		this.ref = ref;
		this.valor = valor;
		this.usuario = usuario;
		this.data = data;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Integer empresa) {
		this.empresa = empresa;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	
}
