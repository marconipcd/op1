package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="cat_loja_produto")
public class CatLojaProduto {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="PRODUTO_ID")
	private Produto produto;
	
	@OneToOne
	@JoinColumn(name="CAT_LOJA_INT_ID")
	private CategoriaLojaIntegrada categoria;
	
	public CatLojaProduto(){
		
	}

	public CatLojaProduto(Integer id, Produto produto,
			CategoriaLojaIntegrada categoria) {
		super();
		this.id = id;
		this.produto = produto;
		this.categoria = categoria;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public CategoriaLojaIntegrada getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaLojaIntegrada categoria) {
		this.categoria = categoria;
	}
	
	
}
