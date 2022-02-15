package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="base")
public class Base {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="EMPRESA_ID")
	private Integer empresa_id;
	
	@Column(name="NOME")
	private String nome;
	@Column(name="ALTURA")
	private String altura;
	
	@Column(name="ALTITUDE")
	private String altitude;
	
	@Column(name="COORDENADAS_SUL")
	private String coordenadas_sul;
	
	@Column(name="COORDENADAS_OESTE")
	private String coordenadas_oeste;
	
	@Column(name="TIPO_TORRE")
	private String tipo_torre;
	
	@Column(name="N_CONTRATO_ENERGIA")
	private String n_contrato_energia;
	
	@Column(name="TITULAR_ENERGIA")
	private String titular_energia;
	
	@Column(name="TENSAO") 
	private String tensao;

	@Column(name="TIPO_TENSAO")
	private String tipo_tensao;
	
	@Column(name="BATERIAL")
	private String bateria;
	
	@Column(name="CEP",nullable=false, unique=false)
	private String cep;
	
	@Column(name="ENDERECO",nullable=false, unique=false)
	private String endereco;
	
	@Column(name="NUMERO",nullable=false, unique=false)
	private String numero;
	
	@Column(name="CIDADE",nullable=false, unique=false)
	private String cidade;
	
	@Column(name="BAIRRO",nullable=false, unique=false)
	private String bairro;
	
	@Column(name="UF",nullable=false, unique=false)
	private String uf;
	
	@Column(name="PAIS", nullable=false, unique=false)
	private String pais;
	
	@Column(name="COMPLEMENTO",nullable=true, unique=false)
	private String complemento;
	
	@Column(name="REFERENCIA",nullable=false, unique=false)
	private String referencia;	
	
	
	@Column(name="CONTATO")
	private String contato;	
	
	@Column(name="DDD1")	
	private String ddd1;
	
	@Column(name="DDD2")
	private String ddd2;
	
	@Column(name="TELEFONE1")
	private String telefone1;
	
	@Column(name="TELEFONE2")
	private String telefone2;
	
	@Column(name="OBS")
	private String observacao;
	
	@Column(name="PROX_TROCA", columnDefinition="DATE")
	private Date prox_troca;
	
	@Column(name="SITUACAO")
	private String situacao;
	
	
	public Base(){
		
	}
	
	

	


	public Base(Integer id, Integer empresa_id, String nome, String altura,
			String altitude, String coordenadas_sul, String coordenadas_oeste,
			String tipo_torre, String tensao, String tipo_tensao,
			String bateria, String cep, String endereco, String numero,
			String cidade, String bairro, String uf, String pais,
			String complemento, String referencia, String contato, String ddd1,
			String ddd2, String telefone1, String telefone2, String observacao,
			Date prox_troca) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.nome = nome;
		this.altura = altura;
		this.altitude = altitude;
		this.coordenadas_sul = coordenadas_sul;
		this.coordenadas_oeste = coordenadas_oeste;
		this.tipo_torre = tipo_torre;
		this.tensao = tensao;
		this.tipo_tensao = tipo_tensao;
		this.bateria = bateria;
		this.cep = cep;
		this.endereco = endereco;
		this.numero = numero;
		this.cidade = cidade;
		this.bairro = bairro;
		this.uf = uf;
		this.pais = pais;
		this.complemento = complemento;
		this.referencia = referencia;
		this.contato = contato;
		this.ddd1 = ddd1;
		this.ddd2 = ddd2;
		this.telefone1 = telefone1;
		this.telefone2 = telefone2;
		this.observacao = observacao;
		this.prox_troca = prox_troca;
	}






	public Base(Integer id){
		this.id = id;
	}
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getAltura() {
		return altura;
	}

	public void setAltura(String altura) {
		this.altura = altura;
	}

	public String getCoordenadas() {
		return coordenadas_sul;
	}

	public void setCoordenadas(String coordenadas) {
		this.coordenadas_sul = coordenadas;
	}

	public String getAltitude() {
		return altitude;
	}

	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	

	public String getTipo_torre() {
		return tipo_torre;
	}

	public void setTipo_torre(String tipo_torre) {
		this.tipo_torre = tipo_torre;
	}

	public String getTipo_alimentacao() {
		return tipo_tensao;
	}

	public void setTipo_alimentacao(String tipo_alimentacao) {
		this.tipo_tensao = tipo_alimentacao;
	}

	public String getTensao() {
		return tensao;
	}

	public void setTensao(String tensao) {
		this.tensao = tensao;
	}

	
	public Integer getEmpresa_id() {
		return empresa_id;
	}

	public void setEmpresa_id(Integer empresa_id) {
		this.empresa_id = empresa_id;
	}



	public String getCoordenadas_sul() {
		return coordenadas_sul;
	}



	public void setCoordenadas_sul(String coordenadas_sul) {
		this.coordenadas_sul = coordenadas_sul;
	}



	public String getCoordenadas_oeste() {
		return coordenadas_oeste;
	}



	public void setCoordenadas_oeste(String coordenadas_oeste) {
		this.coordenadas_oeste = coordenadas_oeste;
	}



	public String getTipo_tensao() {
		return tipo_tensao;
	}



	public void setTipo_tensao(String tipo_tensao) {
		this.tipo_tensao = tipo_tensao;
	}



	public String getBateria() {
		return bateria;
	}



	public void setBateria(String bateria) {
		this.bateria = bateria;
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



	public String getContato() {
		return contato;
	}



	public void setContato(String contato) {
		this.contato = contato;
	}



	public String getDdd1() {
		return ddd1;
	}



	public void setDdd1(String ddd1) {
		this.ddd1 = ddd1;
	}



	public String getDdd2() {
		return ddd2;
	}



	public void setDdd2(String ddd2) {
		this.ddd2 = ddd2;
	}



	public String getTelefone1() {
		return telefone1;
	}



	public void setTelefone1(String telefone1) {
		this.telefone1 = telefone1;
	}



	public String getTelefone2() {
		return telefone2;
	}



	public void setTelefone2(String telefone2) {
		this.telefone2 = telefone2;
	}



	public String getObservacao() {
		return observacao;
	}



	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}






	public Date getProx_troca() {
		return prox_troca;
	}






	public void setProx_troca(Date prox_troca) {
		this.prox_troca = prox_troca;
	}
	public String getN_contrato_energia() {
		return n_contrato_energia;
	}






	public void setN_contrato_energia(String n_contrato_energia) {
		this.n_contrato_energia = n_contrato_energia;
	}






	public String getTitular_energia() {
		return titular_energia;
	}






	public void setTitular_energia(String titular_energia) {
		this.titular_energia = titular_energia;
	}






	public String getSituacao() {
		return situacao;
	}


	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	
	
	
}
