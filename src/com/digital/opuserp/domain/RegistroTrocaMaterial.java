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
@Table(name="registro_troca_material")
public class RegistroTrocaMaterial {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=false)
	private Integer id;	
	
	@OneToOne
	@JoinColumn(name="MATERIAL_ACESSO_ANTIGO_ID", nullable=false)
	private Produto material_antigo;
	
	@OneToOne
	@JoinColumn(name="MATERIAL_ACESSO_NOVO_ID", nullable=false)
	private Produto material_novo;
	
	@Column(name="MOTIVO", nullable=false)
	private String motivo;
	
	@OneToOne
	@JoinColumn(name="ACESSO_ID", nullable=false)
	private AcessoCliente acesso_id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date data_troca;


	public RegistroTrocaMaterial(Integer id, Produto material_antigo, Produto material_novo, 
								 String motivo, AcessoCliente acesso_id, Date data_troca){
		
		this.id = id;
		this.material_antigo = material_antigo;
		this.material_novo = material_novo;
		this.motivo = motivo;
		this.acesso_id = acesso_id;
		this.data_troca = data_troca;
	}
	
	public RegistroTrocaMaterial(Produto material_antigo, Produto material_novo, 
			String motivo, AcessoCliente acesso_id, Date data_troca){
	
		this.material_antigo = material_antigo;
		this.material_novo = material_novo;
		this.motivo = motivo;
		this.acesso_id = acesso_id;
		this.data_troca = data_troca;
	}

	public RegistroTrocaMaterial(){
		
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Produto getMaterial_antigo() {
		return material_antigo;
	}


	public void setMaterial_antigo(Produto material_antigo) {
		this.material_antigo = material_antigo;
	}


	public Produto getMaterial_novo() {
		return material_novo;
	}


	public void setMaterial_novo(Produto material_novo) {
		this.material_novo = material_novo;
	}


	public String getMotivo() {
		return motivo;
	}


	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}


	public AcessoCliente getAcesso_id() {
		return acesso_id;
	}


	public void setAcesso_id(AcessoCliente acesso_id) {
		this.acesso_id = acesso_id;
	}


	public Date getData_troca() {
		return data_troca;
	}


	public void setData_troca(Date data_troca) {
		this.data_troca = data_troca;
	}

	

}
