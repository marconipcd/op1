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
@Table(name="categorias")
@Cacheable(value=false)
public class Categorias {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, unique=true)
    private Integer id;
    @Column(name="EMPRESA_ID", nullable=false, unique=false)
    private Integer empresa_id;
    @Column(name="NOME", nullable=true, unique=false, length=200)
    private String nome;
    @Column(name="STATUS")     
    private String status;
    @Column(name="USER_CHANGE")     
    private String user_change;

    public Categorias() {
    }

    public Categorias(Integer id) {
    	super();
    	this.id = id;
    }
    
    
    public Categorias(Integer empresa_id, String nome, String status, String user_change) {
        this.empresa_id = empresa_id;
        this.nome = nome;
        this.status = status;
        this.user_change = user_change;
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


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	public String getUser_change() {
		return user_change;
	}

	public void setUser_change(String user_change) {
		this.user_change = user_change;
	}

	

	
    
    
}
