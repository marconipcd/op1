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

@Entity
@Table(name="contrato_manutencao_cliente")
public class ContratoManutencao {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@Column(name="EMPRESA_ID")
	private Integer empresa_id;
	@OneToOne
	@JoinColumn(name="CLIENTE_ID")
	private Cliente cliente;
	@OneToOne
	@JoinColumn(name="CONTRATO_MANUTENCAO_ID")
	private PlanosManutencao plano_manutencao;
	@Column(name="DATA_VENC_CONTRATO")
	private Date data_venc_contrato;
	@Column(name="DATA_ALTERACAO")
	private Date data_alteracao;
	@Column(name="DATA_CADASTRO")
	private Date data_cadastro;
	
	public ContratoManutencao(){
		
	}
	
	public ContratoManutencao(Integer id,Integer empresa_id, Cliente cliente, PlanosManutencao plano_manutencao, Date data_venc_contrato, Date data_alteracao, Date data_cadastro){
		this.id = id;
		this.empresa_id = empresa_id;
		this.cliente = cliente;
		this.plano_manutencao = plano_manutencao;
		this.data_venc_contrato = data_venc_contrato;
		this.data_alteracao = data_alteracao;
		this.data_cadastro = data_cadastro;
	}
	
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
	
		
	public Integer getEmpresa_id() {
		return empresa_id;
	}

	public void setEmpresa_id(Integer empresa_id) {
		this.empresa_id = empresa_id;
	}

	public Cliente getCliente(){
		return cliente;
	}
	public void setCliente(Cliente cliente){
		this.cliente = cliente;
	}
	
	public PlanosManutencao getPlano_manutencao(){
		return plano_manutencao;
	}
	public void setPlano_manutencao(PlanosManutencao plano_manutencao){
		this.plano_manutencao = plano_manutencao;
	}
	
	
	
	public Date getData_venc_contrato() {
		return data_venc_contrato;
	}

	public void setData_venc_contrato(Date data_venc_contrato) {
		this.data_venc_contrato = data_venc_contrato;
	}

	public Date getData_alteracao(){
		return data_alteracao;
	}
	
	public void setData_alteracao(Date data_alteracao){
		this.data_alteracao= data_alteracao;
	}
	
	public Date getData_cadastro(){
		return data_cadastro;
	}
	public void setData_cadastro(Date data_cadastro){
		this.data_cadastro = data_cadastro;
	}
	
}
