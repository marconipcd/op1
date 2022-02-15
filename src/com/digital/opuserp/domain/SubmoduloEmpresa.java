package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Marconi
 */

@Entity
@Table(name="submodulo_empresa")
@Cacheable(value=false)
public class SubmoduloEmpresa {
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="COD",nullable=false, unique=true)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name="SUBMODULO_ID")
    SubModulo submodulo;
    
    @ManyToOne
    @JoinColumn(name="EMPRESA_ID")
    Empresa empresa;

    
    public SubmoduloEmpresa(Integer id, SubModulo submodulo, Empresa empresa) {
    	this.id = id;
    	this.submodulo = submodulo;
    	this.empresa = empresa;   	
    }
  
	public SubmoduloEmpresa() {
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SubModulo getSubmodulo() {
		return submodulo;
	}

	public void setSubmodulo(SubModulo submodulo) {
		this.submodulo = submodulo;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

    
    
    
}
