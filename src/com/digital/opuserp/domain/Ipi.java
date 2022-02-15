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
@Table(name="ipi")
public class Ipi {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="PRODUTO_ID")
	private Produto produto;
	
	@OneToOne
	@JoinColumn(name="CST_IPI")
	private CstIpi cst_ipi;
	@Column(name="CNPJ_PRODUTOR")
	private String cnpj_produtor;
	@Column(name="TIPO_CALCULO")
	private String tipo_calculo;
	@Column(name="VALOR_BASE_CALCULO")
	private double valor_base_calculo;	
	@Column(name="ALIQUOTA_IPI")
	private double aliquota;
	@Column(name="VALOR_IPI")
	private double valor_ipi;
	
	
		
	public Ipi(){
		
	}

	
	

	





	public Ipi(Integer id, Produto produto, CstIpi cst_ipi,
			String cnpj_produtor, String tipo_calculo,
			double valor_base_calculo, double aliquota, double valor_ipi) {
		super();
		this.id = id;
		this.produto = produto;
		this.cst_ipi = cst_ipi;
		this.cnpj_produtor = cnpj_produtor;
		this.tipo_calculo = tipo_calculo;
		this.valor_base_calculo = valor_base_calculo;
		this.aliquota = aliquota;
		this.valor_ipi = valor_ipi;
	}










	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	

	public CstIpi getCst_ipi() {
		return cst_ipi;
	}










	public void setCst_ipi(CstIpi cst_ipi) {
		this.cst_ipi = cst_ipi;
	}










	public String getCnpj_produtor() {
		return cnpj_produtor;
	}


	public void setCnpj_produtor(String cnpj_produtor) {
		this.cnpj_produtor = cnpj_produtor;
	}


	public String getTipo_calculo() {
		return tipo_calculo;
	}


	public void setTipo_calculo(String tipo_calculo) {
		this.tipo_calculo = tipo_calculo;
	}


	public double getValor_base_calculo() {
		return valor_base_calculo;
	}


	public void setValor_base_calculo(double valor_base_calculo) {
		this.valor_base_calculo = valor_base_calculo;
	}


	public double getAliquota() {
		return aliquota;
	}


	public void setAliquota(double aliquota) {
		this.aliquota = aliquota;
	}


	public double getValor_ipi() {
		return valor_ipi;
	}


	public void setValor_ipi(double valor_ipi) {
		this.valor_ipi = valor_ipi;
	}





	public Produto getProduto() {
		return produto;
	}





	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	
		
}
