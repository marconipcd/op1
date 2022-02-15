package com.digital.opuserp.domain;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Marconi
 */
@Entity
@Table(name="modulo")
@Cacheable(value=false)
public class Modulo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, unique=true)
    private Integer id;
    @Column(name="NOME_MODULO", nullable=false, unique=false)
    private String nome_modulo;
    @Column(name="DESCRICAO", nullable=false, unique=false)
    private String descricao;
    @Column(name="ICONE_MODULO", nullable=false, unique=false)
    private String icone_modulo;
    @Column(name="ORDEM", nullable=false, unique=false)
    private Integer ordem;
    
    @OneToMany(mappedBy="modulo")
    private List<ModulosEmpresa> empresas;
    
    @OneToMany(mappedBy="modulo")
    private List<ModuloUsuario> usuario;
    

    public Modulo(Integer id) {
    	this.id = id;
    }

    public Modulo() {
	}

	public Modulo(String nome_modulo, String descricao, String icone_modulo, Integer ordem) {
        this.nome_modulo = nome_modulo;
        this.descricao = descricao;
        this.icone_modulo = icone_modulo;
        this.ordem = ordem;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome_modulo() {
        return nome_modulo;
    }

    public void setNome_modulo(String nome_modulo) {
        this.nome_modulo = nome_modulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIcone_modulo() {
        return icone_modulo;
    }

    public void setIcone_modulo(String icone_modulo) {
        this.icone_modulo = icone_modulo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public List<ModulosEmpresa> getEmpresas() {
		return empresas;
	}


	public void setEmpresas(List<ModulosEmpresa> empresas) {
		this.empresas = empresas;
	}

	public List<ModuloUsuario> getUsuario() {
		return usuario;
	}

	public void setUsuario(List<ModuloUsuario> usuario) {
		this.usuario = usuario;
	}
	
    
}
