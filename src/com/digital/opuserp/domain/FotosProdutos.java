package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="fotos_produtos")
public class FotosProdutos {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="PRODUTO_ID")
	private Integer produto_id;
	
	
	@Column(name="ENDERECO_FOTO")
    private String endereco_foto;
	
	@Column(name="DATA_CADASTRO")
	private Date data_cadastro;

	
	public FotosProdutos(){
		
	}
	
	


	public FotosProdutos(Integer id, Integer produto_id, String  endereco_foto,
			Date data_cadastro) {
		super();
		this.id = id;
		this.produto_id = produto_id;
		this.endereco_foto = endereco_foto;
		this.data_cadastro = data_cadastro;
	}




	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getProduto_id() {
		return produto_id;
	}


	public void setProduto_id(Integer produto_id) {
		this.produto_id = produto_id;
	}


	public String getEnderecoFoto() {
		return endereco_foto;
	}


	public void setEnderecoFoto(String endereco_foto) {
		this.endereco_foto = endereco_foto;
	}


	public Date getData_cadastro() {
		return data_cadastro;
	}


	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}



	
	
}
