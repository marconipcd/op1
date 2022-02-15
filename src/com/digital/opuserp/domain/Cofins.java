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
@Table(name="cofins")
public class Cofins {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)	
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="PRODUTO_ID")
	private Produto produto;
	
	@OneToOne
	@JoinColumn(name="CST_COFINS")
	private CstCofins cst_cofins;
	@Column(name="TIPO_CALCULO")
	private String tipo_calculo;
	@Column(name="BASE_CALCULO_COFINS")
	private double base_calculo_cofins;
	@Column(name="ALIQUOTA_COFINS")
	private double aliquota_cofins;
	
	@Column(name="VALOR_COFINS")
	private double valor_cofins;
	@Column(name="TIPO_CALCULO_ST")
	private String tipo_calculo_st;
	@Column(name="BASE_CALCULO_COFINS_ST")
	private double base_calculo_cst;
	@Column(name="ALIQUOTA_COFINS_ST")
	private double aliquota_cofins_st;
	@Column(name="VALOR_COFINS_ST")
	private double valor_cofins_st;
	
	public Cofins(){
		
	}
	
	
	

	

	public Cofins(Integer id, Produto produto, CstCofins cst_cofins,
			String tipo_calculo, double base_calculo_cofins,
			double aliquota_cofins, double valor_cofins,
			String tipo_calculo_st, double base_calculo_cst,
			double aliquota_cofins_st, double valor_cofins_st) {
		super();
		this.id = id;
		this.produto = produto;
		this.cst_cofins = cst_cofins;
		this.tipo_calculo = tipo_calculo;
		this.base_calculo_cofins = base_calculo_cofins;
		this.aliquota_cofins = aliquota_cofins;
		this.valor_cofins = valor_cofins;
		this.tipo_calculo_st = tipo_calculo_st;
		this.base_calculo_cst = base_calculo_cst;
		this.aliquota_cofins_st = aliquota_cofins_st;
		this.valor_cofins_st = valor_cofins_st;
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




	public CstCofins getCst_cofins() {
		return cst_cofins;
	}




	public void setCst_cofins(CstCofins cst_cofins) {
		this.cst_cofins = cst_cofins;
	}




	public String getTipo_calculo() {
		return tipo_calculo;
	}




	public void setTipo_calculo(String tipo_calculo) {
		this.tipo_calculo = tipo_calculo;
	}




	public double getBase_calculo_cofins() {
		return base_calculo_cofins;
	}




	public void setBase_calculo_cofins(double base_calculo_cofins) {
		this.base_calculo_cofins = base_calculo_cofins;
	}




	public double getAliquota_cofins() {
		return aliquota_cofins;
	}




	public void setAliquota_cofins(double aliquota_cofins) {
		this.aliquota_cofins = aliquota_cofins;
	}




	public double getValor_cofins() {
		return valor_cofins;
	}




	public void setValor_cofins(double valor_cofins) {
		this.valor_cofins = valor_cofins;
	}




	public String getTipo_calculo_st() {
		return tipo_calculo_st;
	}




	public void setTipo_calculo_st(String tipo_calculo_st) {
		this.tipo_calculo_st = tipo_calculo_st;
	}




	public double getBase_calculo_cst() {
		return base_calculo_cst;
	}




	public void setBase_calculo_cst(double base_calculo_cst) {
		this.base_calculo_cst = base_calculo_cst;
	}




	public double getAliquota_cofins_st() {
		return aliquota_cofins_st;
	}




	public void setAliquota_cofins_st(double aliquota_cofins_st) {
		this.aliquota_cofins_st = aliquota_cofins_st;
	}




	public double getValor_cofins_st() {
		return valor_cofins_st;
	}




	public void setValor_cofins_st(double valor_cofins_st) {
		this.valor_cofins_st = valor_cofins_st;
	}
	
	



	
	
}
