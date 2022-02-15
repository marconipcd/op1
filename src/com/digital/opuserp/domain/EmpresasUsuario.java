package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Marconi
 */

@Entity
@Table(name="empresas_usuario")
@Cacheable(value=false)
public class EmpresasUsuario {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    
    @OneToOne
    @JoinColumn(name="USUARIO_ID")
    private Usuario usuario;
    
    @OneToOne
    @JoinColumn(name="EMPRESA_ID")
    private Empresa empresa;
    
    
    public EmpresasUsuario() {
    }


	public EmpresasUsuario(Integer id, Usuario usuario, Empresa empresa) {
		super();
		this.id = id;
		this.usuario = usuario;
		this.empresa = empresa;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
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
