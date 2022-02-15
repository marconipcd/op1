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
@Table(name="totais_pedido")
public class TotaisPedido {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@OneToOne    
	@JoinColumn(name="ECF_PREVENDA_CABECALHO_ID", nullable=false)
	private EcfPreVendaCabecalho pedido;
	@OneToOne
	@JoinColumn(name="FORMA_PGTO_ID", nullable=true)
	private FormasPgto forma_pgto;
	@OneToOne
	@JoinColumn(name="HAVER_ID", nullable=true)
	private HaverCab haver;
	@Column(name="VALOR")
	private double valor;
	@Column(name="PARCELAS")
	private Integer parcelas;
	
	public TotaisPedido(){
		
	}

	public TotaisPedido(Integer id, EcfPreVendaCabecalho pedido,
			FormasPgto forma_pgto, double valor, Integer parcelas) {
		super();
		this.id = id;
		this.pedido = pedido;
		this.forma_pgto = forma_pgto;
		this.valor = valor;
		this.parcelas = parcelas;
	}
	public TotaisPedido(Integer id, EcfPreVendaCabecalho pedido,
			HaverCab haver, double valor, Integer parcelas) {
		super();
		this.id = id;
		this.pedido = pedido;
		this.haver = haver;
		this.valor = valor;
		this.parcelas = parcelas;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EcfPreVendaCabecalho getPedido() {
		return pedido;
	}

	public void setPedido(EcfPreVendaCabecalho pedido) {
		this.pedido = pedido;
	}

	public FormasPgto getForma_pgto() {
		return forma_pgto;
	}

	public void setForma_pgto(FormasPgto forma_pgto) {
		this.forma_pgto = forma_pgto;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public Integer getParcelas() {
		return parcelas;
	}

	public void setParcelas(Integer parcelas) {
		this.parcelas = parcelas;
	}

	public HaverCab getHaver() {
		return haver;
	}

	public void setHaver(HaverCab haver) {
		this.haver = haver;
	}
	
}
