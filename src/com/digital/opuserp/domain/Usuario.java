package com.digital.opuserp.domain;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Marconi
 */

@Entity
@Table(name="usuario")
@Cacheable(value=false)
public class Usuario {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false,unique=true)
    private Integer id;
    @Column(name="USERNAME", nullable=false,unique=false, length=50)
    private String username;
    @Column(name="PASSWORD_2", nullable=false,unique=false, length=32)
    private String password;
    @Column(name="FUNCAO", nullable=false,unique=false, length=20)
    private String funcao;
    
    @Column(name="VISUALIZAR_TODOS_CRMS", nullable=true)
    private Integer visualizar_todos_crm;
    
    @OneToMany(mappedBy="usuario")   
    private List<EmpresasUsuario> empresas;
    
    @OneToMany(mappedBy="usuario")    
    private List<ModuloUsuario> modulos;
    
    @OneToMany(mappedBy="usuario")   
    private List<SubModuloUsuario> submodulos;
    

    
    public Usuario() {
    }
    
    public Usuario(Integer id) {
    	this.id = id;
    }


    public Usuario(String username, String password, String funcao) {
        this.username = username;
        this.password = password;
        this.funcao = funcao;
    }
    public Usuario(Integer id,String username, String password, String funcao) {
        
    	this.id = id;
    	this.username = username;
        this.password = password;
        this.funcao = funcao;
    }

 
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

        public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

        public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

        public String getFuncao() {
        return funcao;
    }

	public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

	public Integer getVisualizar_todos_crm() {
		return visualizar_todos_crm;
	}

	public void setVisualizar_todos_crm(Integer visualizar_todos_crm) {
		this.visualizar_todos_crm = visualizar_todos_crm;
	}

	
	

//	public List<EmpresasUsuario> getEmpresas() {
//		return empresas;
//	}


//	public void setEmpresas(List<EmpresasUsuario> empresas) {
//		this.empresas = empresas;
//	}
    
	
//	public List<ModuloUsuario> getModulos() {
//		return modulos;
//	}
//
//	public void setModulos(List<ModuloUsuario> modulos) {
//		this.modulos = modulos;
//	}
//
//	public List<SubModuloUsuario> getSubmodulos() {
//		return submodulos;
//	}
//
//	public void setSubmodulos(List<SubModuloUsuario> submodulos) {
//		this.submodulos = submodulos;
//	}

   
    
    
    
}
