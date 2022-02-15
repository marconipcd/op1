package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="materiais_alocados_detalhe")
public class MateriaisAlocadosDetalhe {

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="MATERIAIS_ALOCADOS_CAB")
	private Integer materiais_alocados_cab;
	
	@Column(name="MATERIAL")
	private Integer material;
	
	@Column(name="QTD")
	private Integer qtd;
	
	public MateriaisAlocadosDetalhe(){
		
	}
	
	public MateriaisAlocadosDetalhe(Integer id, Integer materiais_alocados_cab, Integer material, Integer qtd){
		this.id = id;
		this.materiais_alocados_cab = materiais_alocados_cab;
		this.material = material;
		this.qtd = qtd;
	}
	
	
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
	public Integer getMateriaisAlocadosCab(){
		return materiais_alocados_cab;
	}
	public void setMateriaisAlocadosCab(Integer materiais_alocados_cab){
		this.materiais_alocados_cab = materiais_alocados_cab;
	}
	public Integer getMaterial(){
		return material;
	}
	public void setMaterial(Integer material){
		this.material = material;
	}
	public Integer getQtd(){
		return qtd;
	}
	public void setQtd(Integer qtd){
		this.qtd = qtd;
	}
}
