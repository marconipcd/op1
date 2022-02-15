	package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Marconi
 */
@Entity
@Table(name="transportadoras")
@Cacheable(value=false)
public class Transportadoras {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, unique=true)
    private Integer id;
    @Column(name="EMPRESA_ID", nullable=false, unique=false)
    private Integer empresa_id;
    @Column(name="NOME", nullable=false, unique=false, length=200)
    private String nome;
    @Column(name="FANTASIA", nullable=false, unique=false, length=200)
    private String fantasia;
    @Column(name="CONTATO", nullable=false, unique=false, length=200)
    private String contato;
    @Column(name="TIPO_PESSOA", nullable=false, unique=false, length=15)
    private String tipo_pessoa;
    @Column(name="CNPJ", nullable=false, unique=false, length=20)
    private String cnpj;
    @Column(name="IE", nullable=false, unique=false, length=30)
    private String ie;
    @Column(name="CEP", nullable=false, unique=false, length=10)
    private String cep;
    @Column(name="ENDERECO", nullable=false, unique=false, length=200)
    private String endereco;
    @Column(name="NUMERO", nullable=false, unique=false, length=10)
    private String numero;
    @Column(name="COMPLEMENTO", nullable=false, unique=false, length=100)
    private String complemento;
    @Column(name="BAIRRO", nullable=false, unique=false, length=50)
    private String bairro;
    @Column(name="CIDADE", nullable=false, unique=false, length=50)
    private String cidade;
    @Column(name="UF", nullable=false, unique=false, length=2)
    private String uf;
    @Column(name="PAIS", nullable=false, unique=false, length=50)
    private String pais;
    @Column(name="REFERENCIA", nullable=false, unique=false, length=200)
    private String referencia;
    @Column(name="DDD_FONE1", nullable=true, unique=false, length=50)
    private String ddd_fone1;
    @Column(name="FONE", nullable=false, unique=false, length=50)
    private String fone;
    @Column(name="DDD_FONE3", nullable=true, unique=false, length=50)
    private String ddd_fone3;
    @Column(name="FONE3", nullable=false, unique=false, length=50)
    private String fone3;
    @Column(name="FONE0800", nullable=false, unique=false, length=50)
    private String fone0800;
    @Column(name="DDD_FONE2", nullable=true, unique=false, length=50)
    private String ddd_fone2;
    @Column(name="FAX", nullable=false, unique=false, length=50)
    private String fax;
    @Column(name="SITE", nullable=false, unique=false, length=50)
    private String site;
    @Column(name="MSN", nullable=false, unique=false, length=50)
    private String msn;
    @Column(name="EMAIL", nullable=false, unique=false, length=50)
    private String email;
    @Column(name="OBSERVACAO", nullable=false, unique=false, length=200)
    private String observacao;
    @Column(nullable=true, unique=false, name="LIMITE_DE_CREDITO")
	private Float limite_de_credito;
    @Column(name="STATUS")
    private String status;    
    
    @Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="DATA_CADASTRO", nullable = false)
    private Date data_cadastro;   
    @Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="DATA_ALTERACAO", nullable = false)
    private Date data_alteracao;
    
    @PrePersist
    protected void onCreate(){
    	data_alteracao = data_cadastro = new Date();
    }   
    @PreUpdate
    protected void onUpdate(){
    	data_alteracao = new Date();
    }

     
    public Transportadoras(Integer id) {
    	this.id = id;
    }
    
    public Transportadoras() {
    }

    public Transportadoras(Integer empresa_id, String nome, String fantasia, String tipo_pessoa, String cnpj, String ie, String cep, String endereco, String numero, String complemento, String bairro, String cidade, String uf, String pais, String referencia, String fone, String fax, String site, String email, String observacao) {
        this.empresa_id = empresa_id;
        this.nome = nome;
        this.fantasia = fantasia;
        this.tipo_pessoa = tipo_pessoa;
        this.cnpj = cnpj;
        this.ie = ie;
        this.cep = cep;
        this.endereco = endereco;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.pais = pais;
        this.referencia = referencia;
        this.fone = fone;
        this.fax = fax;
        this.site = site;
        this.email = email;
        this.observacao = observacao;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getTipo_pessoa() {
        return tipo_pessoa;
    }

    public void setTipo_pessoa(String tipo_pessoa) {
        this.tipo_pessoa = tipo_pessoa;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getIe() {
        return ie;
    }

    public void setIe(String ie) {
        this.ie = ie;
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

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
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

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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

	public String getFone3() {
		return fone3;
	}

	public void setFone3(String fone3) {
		this.fone3 = fone3;
	}

	public String getFone0800() {
		return fone0800;
	}

	public void setFone0800(String fone0800) {
		this.fone0800 = fone0800;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public Float getLimite_de_credito() {
		return limite_de_credito;
	}

	public void setLimite_de_credito(Float limite_de_credito) {
		this.limite_de_credito = limite_de_credito;
	}

	public String getContato() {
		return contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public String getDdd_fone1() {
		return ddd_fone1;
	}

	public void setDdd_fone1(String ddd_fone1) {
		this.ddd_fone1 = ddd_fone1;
	}

	public String getDdd_fone3() {
		return ddd_fone3;
	}

	public void setDdd_fone3(String ddd_fone3) {
		this.ddd_fone3 = ddd_fone3;
	}

	public String getDdd_fone2() {
		return ddd_fone2;
	}

	public void setDdd_fone2(String ddd_fone2) {
		this.ddd_fone2 = ddd_fone2;
	}
    
    
    
}
