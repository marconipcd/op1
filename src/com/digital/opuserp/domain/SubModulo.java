package com.digital.opuserp.domain;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Marconi
 */
@Entity
@Table(name="submodulo")
@Cacheable(value=false)
public class SubModulo {

       @Id
       @GeneratedValue(strategy= GenerationType.AUTO)
       @Column(name="ID", nullable=false,unique=true)
       private Integer id;
       @Column(name="MODULO_ID", nullable=false,unique=false)
       private Integer modulo_id;
       @Column(name="NOME", nullable=false,unique=false,length=50)
       private String nome;
       @Column(name="DESCRICAO", nullable=false,unique=false,length=100)
       private String descricao;
       @Column(name="ICONE", nullable=false,unique=false)
       private String icon;
       @Column(name="ORDEM", nullable=false,unique=false)
       private Integer ordem;
       
       @OneToMany(mappedBy="submodulo")
       private List<SubmoduloEmpresa> empresas;
       
       @OneToMany(mappedBy="submodulo")
       private List<SubModuloUsuario> usuario;
       

    public SubModulo(Integer id) {
    	this.id = id;
    }

    public SubModulo() {
	}
    
    public SubModulo(Integer modulo_id, String nome, String descricao, Integer ordem) {
        this.modulo_id = modulo_id;
        this.nome = nome;
        this.descricao = descricao;
        this.ordem = ordem;
    }
  
	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getModulo_id() {
        return modulo_id;
    }

    public void setModulo_id(Integer modulo_id) {
        this.modulo_id = modulo_id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

	public List<SubmoduloEmpresa> getEmpresas() {
		return empresas;
	}

	public void setEmpresas(List<SubmoduloEmpresa> empresas) {
		this.empresas = empresas;
	}

	public List<SubModuloUsuario> getUsuario() {
		return usuario;
	}

	public void setUsuario(List<SubModuloUsuario> usuario) {
		this.usuario = usuario;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}


       
    
       
}
