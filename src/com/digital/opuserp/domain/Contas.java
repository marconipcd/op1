package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="contas")
@Cacheable(value=false)
public class Contas {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@Column(name="DESCRICAO", nullable=true, unique=false)
	private String descricao;
	@Column(name="COD_CTA_REF", nullable=true, unique=false)
	private String cod_cta_ref;
	
	@OneToOne
	@JoinColumn(name="CONTAS_ROOT_ID", nullable=true)
	private Contas contas_root;
	
	
	public Contas(Integer id){
		super();
		this.id = id;
	}
	
	public Contas(){
		
	}


	public Contas(Integer id, String descricao, String cod_cta_ref,
			Contas contas_root) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.cod_cta_ref = cod_cta_ref;
		this.contas_root = contas_root;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public String getcod_cta_ref() {
		return cod_cta_ref;
	}


	public void setcod_cta_ref(String cod_cta_ref) {
		this.cod_cta_ref = cod_cta_ref;
	}


	public Contas getContas_root() {
		return contas_root;
	}


	public void setContas_root(Contas contas_root) {
		this.contas_root = contas_root;
	}

}

