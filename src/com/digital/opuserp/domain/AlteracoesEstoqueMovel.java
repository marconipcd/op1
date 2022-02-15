package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="alteracoes_estoque_movel")
public class AlteracoesEstoqueMovel {

	
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="COD_ESTOQUE_MOV")
	private EstoqueMovel estoqueMovel;
	
	@OneToOne
	@JoinColumn(name="USUARIO_ID")	
	private Usuario usuario;
	
	@Column(name="DESCRICAO")
	private String descricao;
	
	@Column(name="DATA_ALTERACAO")
	private Date data_alteracao;
	
	private Float qtd;
	
	public AlteracoesEstoqueMovel(){
		
	}


	public AlteracoesEstoqueMovel(Integer id, EstoqueMovel estoqueMovel,
			Usuario usuario, String descricao, Date data_alteracao, Float qtd) {
		super();
		this.id = id;
		this.estoqueMovel = estoqueMovel;
		this.usuario = usuario;
		this.descricao = descricao;
		this.data_alteracao = data_alteracao;
		this.qtd = qtd;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public EstoqueMovel getEstoqueMovel() {
		return estoqueMovel;
	}


	public void setEstoqueMovel(EstoqueMovel estoqueMovel) {
		this.estoqueMovel = estoqueMovel;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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


	public Float getQtd() {
		return qtd;
	}


	public void setQtd(Float qtd) {
		this.qtd = qtd;
	}
	
	
	
	
}
