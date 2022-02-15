package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="produto_entrada")
public class ProdutoEntrada {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false)
	private Integer id;	
	@Column(name="ID_PRODUTO")
	private Integer id_produto;
	@Column(name="LOCAL_WORK")
	private String local_work;
	
	public ProdutoEntrada(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	
	public Integer getId_produto() {
		return id_produto;
	}

	public void setId_produto(Integer id_produto) {
		this.id_produto = id_produto;
	}

	public String getLocal_work() {
		return local_work;
	}

	public void setLocal_work(String local_work) {
		this.local_work = local_work;
	}
	
	
}
