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
@Table(name="icms")
public class Icms {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="PRODUTO_ID")
	private Produto produto;
	@Column(name="ORIGEM")
	private String origem;
	@Column(name="CST")
	private Integer cst;
	@Column(name="CSOSN")
	private Integer csosn;
	@Column(name="CST_ECF")
	private Integer cst_ecf;	
	@Column(name="ALIQUOTA")
	private double aliquota;	
	@Column(name="MOD_BC_ICMS")
	private String mod_bc_icms;
	@Column(name="BASE_CALC_ICMS")
	private double base_calc_icms;
	@Column(name="ALIQUOTA_ICMS")
	private double aliquota_icms;
	@Column(name="MOD_BC_ICMS_ST")
	private String mod_bc_icms_st;
	@Column(name="MVA")
	private double mva;
	@Column(name="BASE_CALC_ICMS_ST")
	private double base_calc_icms_st;
	@Column(name="ALIQUOTA_ICMS_ST")
	private double aliquota_icms_st;
	
	public Icms(){
		
	}
	
	

	public Icms(Integer id, Produto produto, String origem, Integer cst,
			Integer csosn, Integer cst_ecf, double aliquota,
			String mod_bc_icms, double base_calc_icms, double aliquota_icms,
			String mod_bc_icms_st, double mva, double base_calc_icms_st,
			double aliquota_icms_st) {
		super();
		this.id = id;
		this.produto = produto;
		this.origem = origem;
		this.cst = cst;
		this.csosn = csosn;
		this.cst_ecf = cst_ecf;
		this.aliquota = aliquota;
		this.mod_bc_icms = mod_bc_icms;
		this.base_calc_icms = base_calc_icms;
		this.aliquota_icms = aliquota_icms;
		this.mod_bc_icms_st = mod_bc_icms_st;
		this.mva = mva;
		this.base_calc_icms_st = base_calc_icms_st;
		this.aliquota_icms_st = aliquota_icms_st;
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



	public String getOrigem() {
		return origem;
	}



	public void setOrigem(String origem) {
		this.origem = origem;
	}



	public Integer getCst() {
		return cst;
	}



	public void setCst(Integer cst) {
		this.cst = cst;
	}



	public Integer getCsosn() {
		return csosn;
	}



	public void setCsosn(Integer csosn) {
		this.csosn = csosn;
	}



	public Integer getCst_ecf() {
		return cst_ecf;
	}



	public void setCst_ecf(Integer cst_ecf) {
		this.cst_ecf = cst_ecf;
	}



	public double getAliquota() {
		return aliquota;
	}



	public void setAliquota(double aliquota) {
		this.aliquota = aliquota;
	}



	public String getMod_bc_icms() {
		return mod_bc_icms;
	}



	public void setMod_bc_icms(String mod_bc_icms) {
		this.mod_bc_icms = mod_bc_icms;
	}



	public double getBase_calc_icms() {
		return base_calc_icms;
	}



	public void setBase_calc_icms(double base_calc_icms) {
		this.base_calc_icms = base_calc_icms;
	}



	public double getAliquota_icms() {
		return aliquota_icms;
	}



	public void setAliquota_icms(double aliquota_icms) {
		this.aliquota_icms = aliquota_icms;
	}



	public String getMod_bc_icms_st() {
		return mod_bc_icms_st;
	}



	public void setMod_bc_icms_st(String mod_bc_icms_st) {
		this.mod_bc_icms_st = mod_bc_icms_st;
	}



	public double getMva() {
		return mva;
	}



	public void setMva(double mva) {
		this.mva = mva;
	}



	public double getBase_calc_icms_st() {
		return base_calc_icms_st;
	}



	public void setBase_calc_icms_st(double base_calc_icms_st) {
		this.base_calc_icms_st = base_calc_icms_st;
	}



	public double getAliquota_icms_st() {
		return aliquota_icms_st;
	}



	public void setAliquota_icms_st(double aliquota_icms_st) {
		this.aliquota_icms_st = aliquota_icms_st;
	}




		
}
