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
@Table(name="modulos_empresa")
@Cacheable(value=false)
public class ModulosEmpresa {
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false,unique=true)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name="MODULO_ID")
    Modulo modulo;
    
    @ManyToOne
    @JoinColumn(name="EMPRESA_ID")
    Empresa empresa;

    
    public ModulosEmpresa(Integer id,Empresa empresa, Modulo modulo) {
    	this.id = id;
    	this.empresa = empresa;
    	this.modulo = modulo;
    }
    
    public ModulosEmpresa() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
    

	

    
    
}
