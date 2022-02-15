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
@Table(name="pis")
public class Pis {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)	
	private Integer id;
	@OneToOne
	@JoinColumn(name="PRODUTO_ID")
	private Produto produto;
	@OneToOne
	@JoinColumn(name="CST_PIS")
	private CstPis cst_pis;	
	@Column(name="TIPO_CALCULO")
	private String tipo_calculo;	
	@Column(name="BASE_CALCULO_PIS")
	private double base_calculo_pis;
	@Column(name="ALIQUOTA")
	private double aliquota;
	@Column(name="VALOR_PIS")
	private double valor_pis;	
	@Column(name="TIPO_CALCULO_PIS_ST")
	private String tipo_calculo_pis_st;
	@Column(name="BASE_CALCULO_PIS_ST")
	private double base_calculo_pis_st;	
	@Column(name="ALIQUOTA_PIS_ST")
	private double aliquota_pis_st;
	@Column(name="VALOR_PIS_ST")
	private double valor_pis_st;
	
	public Pis(){
		
	}

		

	



	






	public Pis(Integer id, Produto produto, CstPis cst_pis,
			String tipo_calculo, double base_calculo_pis, double aliquota,
			double valor_pis, String tipo_calculo_pis_st,
			double base_calculo_pis_st, double aliquota_pis_st,
			double valor_pis_st) {
		super();
		this.id = id;
		this.produto = produto;
		this.cst_pis = cst_pis;
		this.tipo_calculo = tipo_calculo;
		this.base_calculo_pis = base_calculo_pis;
		this.aliquota = aliquota;
		this.valor_pis = valor_pis;
		this.tipo_calculo_pis_st = tipo_calculo_pis_st;
		this.base_calculo_pis_st = base_calculo_pis_st;
		this.aliquota_pis_st = aliquota_pis_st;
		this.valor_pis_st = valor_pis_st;
	}














	public Produto getProduto() {
		return produto;
	}














	public void setProduto(Produto produto) {
		this.produto = produto;
	}














	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}



	


	public CstPis getCst_pis() {
		return cst_pis;
	}







	public void setCst_pis(CstPis cst_pis) {
		this.cst_pis = cst_pis;
	}







	public String getTipo_calculo() {
		return tipo_calculo;
	}



	public void setTipo_calculo(String tipo_calculo) {
		this.tipo_calculo = tipo_calculo;
	}



	public double getBase_calculo_pis() {
		return base_calculo_pis;
	}



	public void setBase_calculo_pis(double base_calculo_pis) {
		this.base_calculo_pis = base_calculo_pis;
	}



	public double getAliquota() {
		return aliquota;
	}



	public void setAliquota(double aliquota) {
		this.aliquota = aliquota;
	}



	public double getValor_pis() {
		return valor_pis;
	}



	public void setValor_pis(double valor_pis) {
		this.valor_pis = valor_pis;
	}



	public String getTipo_calculo_pis_st() {
		return tipo_calculo_pis_st;
	}



	public void setTipo_calculo_pis_st(String tipo_calculo_pis_st) {
		this.tipo_calculo_pis_st = tipo_calculo_pis_st;
	}



	public double getBase_calculo_pis_st() {
		return base_calculo_pis_st;
	}



	public void setBase_calculo_pis_st(double base_calculo_pis_st) {
		this.base_calculo_pis_st = base_calculo_pis_st;
	}



	public double getAliquota_pis_st() {
		return aliquota_pis_st;
	}



	public void setAliquota_pis_st(double aliquota_pis_st) {
		this.aliquota_pis_st = aliquota_pis_st;
	}



	public double getValor_pis_st() {
		return valor_pis_st;
	}



	public void setValor_pis_st(double valor_pis_st) {
		this.valor_pis_st = valor_pis_st;
	}

	
	
}
