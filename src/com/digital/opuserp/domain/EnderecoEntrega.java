package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="enderecos_entrega")
@Cacheable(value=false)
public class EnderecoEntrega {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@Column(name="CEP",nullable=false, unique=false)
	private String cep;
	@Column(name="ENDERECO",nullable=false, unique=false)
	private String endereco;
	@Column(name="NUMERO",nullable=false, unique=false)
	private String numero;
	@Column(name="CIDADE", nullable=false, unique=false)
	private String cidade;
	@Column(name="BAIRRO", nullable=false, unique=false)
	private String bairro;
	@Column(name="UF", nullable=false, unique=false)
	private String uf;
	@Column(name="PAIS", nullable=false, unique=false)
	private String pais;
	@Column(name="COMPLEMENTO", nullable=true, unique=false)
	private String complemento;
	@Column(name="REFERENCIA", nullable=false, unique=false)
	private String referencia;	
	
	@OneToOne
	@JoinColumn(name="CLIENTES_ID")
	private Cliente cliente;
	

	
	public EnderecoEntrega(){
		
	}

	public EnderecoEntrega(Integer id, String cep, String endereco, String numero,
			String cidade, String bairro, String uf, String pais,
			String complemento, String referencia) {
		super();
		this.id = id;
		this.cep = cep;
		this.endereco = endereco;
		this.numero = numero;
		this.cidade = cidade;
		this.bairro = bairro;
		this.uf = uf;
		this.pais = pais;
		this.complemento = complemento;
		this.referencia = referencia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	
}
