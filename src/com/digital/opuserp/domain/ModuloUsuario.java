package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;


@Entity
@Table(name="modulo_usuario")
@Cacheable(value=false)
public class ModuloUsuario {
 
	@Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false,unique=true)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name="MODULO_ID")
    @OrderBy("ordem ASC")
    Modulo modulo;
    
    @ManyToOne
    @JoinColumn(name="USUARIO_ID")
    Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name="EMPRESA_ID")
    Empresa empresa;
    
	public ModuloUsuario(Integer id,Empresa empresa, Modulo modulo, Usuario usuario) {
		this.id = id;
    	this.empresa = empresa;
    	this.modulo = modulo;
    	this.usuario = usuario;
    
	}

	public ModuloUsuario() {
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
