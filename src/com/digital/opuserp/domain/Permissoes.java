package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="permissoes")
@Cacheable(value=false)
public class Permissoes {

	@Id
	@Column(nullable=false, unique=true, name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(nullable=false, unique=true, name="SUBMODULO_ID")
	private Integer submoduloid;
	@Column(nullable=false, unique=true, name="USUARIO_ID")
	private Integer usuarioid;
	@Column(nullable=false, unique=true, name="EMPRESA_ID")
	private Integer empresaid;
	@Column(nullable=false, unique=true, name="PERMISSAO")
	private String permissao;
	
	public Permissoes(){
		
	}
	
	public Permissoes(Integer id, Integer submoduloid, Integer usuarioid,
			Integer empresaid, String permissao) {
		super();
		this.id = id;
		this.submoduloid = submoduloid;
		this.usuarioid = usuarioid;
		this.empresaid = empresaid;
		this.permissao = permissao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSubmoduloid() {
		return submoduloid;
	}

	public void setSubmoduloid(Integer submoduloid) {
		this.submoduloid = submoduloid;
	}

	public Integer getUsuarioid() {
		return usuarioid;
	}

	public void setUsuarioid(Integer usuarioid) {
		this.usuarioid = usuarioid;
	}

	public Integer getEmpresaid() {
		return empresaid;
	}

	public void setEmpresaid(Integer empresaid) {
		this.empresaid = empresaid;
	}

	public String getPermissao() {
		return permissao;
	}

	public void setPermissao(String permissao) {
		this.permissao = permissao;
	}
	
	
	
}
