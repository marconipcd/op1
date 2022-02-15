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
@Table(name="config_ose")
public class ConfigOse {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="TIPO_ITEM_DEFAULT")
	private TipoItemProduto tipo_item_default;
	
	
	
	public ConfigOse(){
		
	}



	public ConfigOse(Integer id, TipoItemProduto tipo_item_default) {
		super();
		this.id = id;
		this.tipo_item_default = tipo_item_default;
	}



	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public TipoItemProduto getTipo_item_default() {
		return tipo_item_default;
	}



	public void setTipo_item_default(TipoItemProduto tipo_item_default) {
		this.tipo_item_default = tipo_item_default;
	}
	
	
}
