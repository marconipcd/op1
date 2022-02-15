package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="config_nfe55")
public class ConfigNfeMod55 {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@Column(name="TIPO_DOCUMENTO")
	private String tipo_documento;
	@Column(name="FORMA_PGTO")
	private String forma_pgto;
	@Column(name="FORMA_EMISSAO")
	private String forma_emissao;
	@Column(name="CONSUMIDOR_FINAL")
	private String consumidor_final;
	@Column(name="DESTINO_OPERACAO")
	private String destino_operacao;
	@Column(name="TIPO_ATENDIMENTO")
	private String tipo_atendimento;
	@Column(name="OBSERVACAO")
	private String observacao;
	
	public ConfigNfeMod55(){
		
	}
	public ConfigNfeMod55(Integer id, String tipo_documento, String forma_pgto, String forma_emissao, String consumidor_final, String destino_operacao, String tipo_atendimento, String observacao){
		this.id = id;
		this.tipo_documento = tipo_documento;
		this.forma_pgto = forma_pgto;
		this.forma_emissao = forma_emissao;
		this.consumidor_final = consumidor_final;
		this.destino_operacao = destino_operacao;
		this.tipo_atendimento = tipo_atendimento;
		this.observacao = observacao;
	}
	
	public void setId(Integer id){
		this.id = id;
	}
	public Integer getId(){
		return id;
	}
	public void setTipoDocumento(String tipo_documento){
		this.tipo_documento = tipo_documento;
	}
	public String getTipoDocumento(){
		return tipo_documento;
	}
	public void setFormaPgto(String forma_pgto){
		this.forma_pgto = forma_pgto;
	}
	public String getFormaPgto(){
		return forma_pgto;
	}
	public void setFormaEmissao(String forma_emissao){
		this.forma_emissao = forma_emissao;
	}
	public String getFormaEmissao(){
		return forma_emissao;
	}
	public void setConsumidorFinal(String consumidor_final){
		this.consumidor_final = consumidor_final;
	}
	public String getConsumidorFinal(){
		return consumidor_final;
	}
	public void setDestinoOperacao(String destino_operacao){
		this.destino_operacao = destino_operacao;
	}
	public String getDestinoOperacao(){
		return destino_operacao;
	}
	public void setTipoAtendimento(String tipo_atendimento){
		this.tipo_atendimento = tipo_atendimento;
	}
	public String getTipoAtendimento(){
		return tipo_atendimento;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
}
