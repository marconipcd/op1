package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Marconi
 */
@Entity
@Table(name="cep")
@Cacheable(value=false)
public class Ceps {
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, unique=true)
    private Integer id;
    @Column(name="CEP", nullable=false, unique=false, length=20)
    private String cep;
    @Column(name="ENDERECO", nullable=false, unique=false, length=200)
    private String endereco;
    @Column(name="BAIRRO", nullable=false, unique=false, length=30)
    private String bairro;
    @Column(name="CIDADE", nullable=false, unique=false, length=50)
    private String cidade;
    @Column(name="UF", nullable=false, unique=false, length=2)
    private String uf;
    @Column(name="PAIS", nullable=false, unique=false, length=10)
    private String pais;
    @Column(name="COD_IBGE")
    private String cod_ibge;

    public Ceps() {
    }

    public Ceps(String cep, String endereco, String bairro, String cidade, String uf, String pais) {
        this.cep = cep;
        this.endereco = endereco;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.pais = pais;
    }
    

    public Ceps(Integer id, String cep, String endereco, String bairro,
			String cidade, String uf, String pais, String cod_ibge) {
		super();
		this.id = id;
		this.cep = cep;
		this.endereco = endereco;
		this.bairro = bairro;
		this.cidade = cidade;
		this.uf = uf;
		this.pais = pais;
		this.cod_ibge = cod_ibge;
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

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
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

	public String getCod_ibge() {
		return cod_ibge;
	}

	public void setCod_ibge(String cod_ibge) {
		this.cod_ibge = cod_ibge;
	}
    
    
}
