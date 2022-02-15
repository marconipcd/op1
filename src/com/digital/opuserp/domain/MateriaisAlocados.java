package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="materiais_alocados")
public class MateriaisAlocados {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="TECNICO")
	private Usuario tecnico;
	
	@OneToOne
	@JoinColumn(name="MATERIAL")
	private Produto material;
	
	@Column(name="QTD")
	private double qtd;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALOCACAO")
	private Date data_alocacao;
	
	public MateriaisAlocados(){
		
	}
	
	public MateriaisAlocados(Integer id, Usuario tecnico, Produto material, double qtd, Date data_alocacao){
		this.id = id;
		this.tecnico = tecnico;
		this.material = material;
		this.qtd = qtd;
		this.data_alocacao = data_alocacao;
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Usuario getTecnico() {
		return tecnico;
	}

	public void setTecnico(Usuario tecnico) {
		this.tecnico = tecnico;
	}

	public Produto getMaterial() {
		return material;
	}

	public void setMaterial(Produto material) {
		this.material = material;
	}

	public double getQtd() {
		return qtd;
	}

	public void setQtd(double qtd) {
		this.qtd = qtd;
	}

	public Date getData_alocacao() {
		return data_alocacao;
	}

	public void setData_alocacao(Date data_alocacao) {
		this.data_alocacao = data_alocacao;
	}
	
	
	
	
}
