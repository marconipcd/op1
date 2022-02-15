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
@Table(name="pre_contrato")
public class PreContrato {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="PLANO_NOVO")
	private PlanoAcesso plano;
	
	@Column(name="TIPO_PLANO")
	private String tipo_plano;
	
	@Column(name="VELOCIDADE")
	private String velocidade;
	
	@Column(name="VALOR")
	private Float valor;
	
	@Column(name="DIA_VENCIMENTO")
	private String dia_vencimento;
	
	@Column(name="ISENCAO_INSTALACAO")
	private boolean isencao_instalacao;
	
	@OneToOne
	@JoinColumn(name="CONTRATO")
	private AcessoCliente contrato;
	
	@Column(name="USUARIO")
	private String usuario;
	
	@Column(name="DATA")
	private Date data;
	
	public PreContrato(){
		
	}
	
	public PreContrato(Integer id, PlanoAcesso plano, String tipo_plano,
			String velocidade, Float valor, String dia_vencimento,
			boolean isencao_instalacao, AcessoCliente contrato, String usuario,
			Date data) {
		super();
		this.id = id;
		this.plano = plano;
		this.tipo_plano = tipo_plano;
		this.velocidade = velocidade;
		this.valor = valor;
		this.dia_vencimento = dia_vencimento;
		this.isencao_instalacao = isencao_instalacao;
		this.contrato = contrato;
		this.usuario = usuario;
		this.data = data;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PlanoAcesso getPlano() {
		return plano;
	}

	public void setPlano(PlanoAcesso plano) {
		this.plano = plano;
	}

	public String getTipo_plano() {
		return tipo_plano;
	}

	public void setTipo_plano(String tipo_plano) {
		this.tipo_plano = tipo_plano;
	}

	public String getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(String velocidade) {
		this.velocidade = velocidade;
	}

	public Float getValor() {
		return valor;
	}

	public void setValor(Float valor) {
		this.valor = valor;
	}

	public String getDia_vencimento() {
		return dia_vencimento;
	}

	public void setDia_vencimento(String dia_vencimento) {
		this.dia_vencimento = dia_vencimento;
	}

	public boolean isIsencao_instalacao() {
		return isencao_instalacao;
	}

	public void setIsencao_instalacao(boolean isencao_instalacao) {
		this.isencao_instalacao = isencao_instalacao;
	}

	public AcessoCliente getContrato() {
		return contrato;
	}

	public void setContrato(AcessoCliente contrato) {
		this.contrato = contrato;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	
}
