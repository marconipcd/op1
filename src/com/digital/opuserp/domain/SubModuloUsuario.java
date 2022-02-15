package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;


@Entity
@Table(name="submodulo_usuario")
@Cacheable(value=false)
public class SubModuloUsuario {
	
	@Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false,unique=true)
    private Integer id;
    
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name="SUBMODULO_ID")    
    SubModulo submodulo;
    
    @ManyToOne
    @JoinColumn(name="USUARIO_ID")
    Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name="EMPRESA_ID")
    Empresa empresa;
    
    public SubModuloUsuario(Integer id,Empresa empresa, SubModulo submodulo, Usuario usuario) {
		this.id = id;
    	this.empresa = empresa;
    	this.submodulo = submodulo;
    	this.usuario = usuario;
	}

	public SubModuloUsuario() {
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
    
    

}
