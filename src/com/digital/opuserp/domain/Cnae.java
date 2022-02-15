package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="cadastro_cnaes")
public class Cnae {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique= true)
	private Integer id;
	@Column(name="CODIGO", nullable=false)
	private String codigo;
	@Column(name="DESCRICAO", nullable=false)
	private String descricao;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALTERACAO", nullable=true)
	private Date data_alteracao;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO", nullable=false)
	private Date data_cadastro;
	
	@PrePersist
	private void onInsert(){
		data_cadastro = new Date();
	}
	
	@PreUpdate
	private void onUpdate(){
		data_alteracao = new Date();
	}
	
	public Cnae(){
		
	}
	
	public Cnae(Integer id){
		this.id = id;
	}

	public Cnae(Integer id, String codigo, String descricao,
			Date data_alteracao, Date data_cadastro) {
		super();
		this.id = id;
		this.codigo = codigo;
		this.descricao = descricao;
		this.data_alteracao = data_alteracao;
		this.data_cadastro = data_cadastro;
	}

	public Cnae(Integer id,String codigo, String descricao) {
		this.id = id;
		this.codigo = codigo;
		this.descricao = descricao;
	}

	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getData_alteracao() {
		return data_alteracao;
	}

	public void setData_alteracao(Date data_alteracao) {
		this.data_alteracao = data_alteracao;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}
	
	
}
