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
@Table(name="seriais_compra")
public class SeriaisCompra {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@OneToOne
	@JoinColumn(name="SERIAL", nullable=false)
	private SerialProduto serial;
	@OneToOne
	@JoinColumn(name="ITEM_PEDIDO",nullable=false)
	private MovimentoEntDetalhe itemPedido;
	
	public SeriaisCompra() {
		
	}
	

	public SeriaisCompra(Integer id, SerialProduto serial,
			MovimentoEntDetalhe itemPedido) {
		super();
		this.id = id;
		this.serial = serial;
		this.itemPedido = itemPedido;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SerialProduto getSerial() {
		return serial;
	}

	public void setSerial(SerialProduto serial) {
		this.serial = serial;
	}

	public MovimentoEntDetalhe getItemPedido() {
		return itemPedido;
	}

	public void setItemPedido(MovimentoEntDetalhe itemPedido) {
		this.itemPedido = itemPedido;
	}
	
	
	
}
