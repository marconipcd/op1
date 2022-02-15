package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="ocorrencia_plano_acao")
public class OcorrenciaPlanoAcao {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="PLANO_ACAO_ID")
	private PlanoAcao plano;
	
	@Column(name="DETALHE")
	private String detalhe;
	
	@Column(name="USUARIO")
	private String usuario;
	
	@Column(name="DATA")
	private Date data;
	
	public OcorrenciaPlanoAcao(){
		
	}

	public OcorrenciaPlanoAcao(Integer id, PlanoAcao plano, String detalhe,
			String usuario, Date data) {
		super();
		this.id = id;
		this.plano = plano;
		this.detalhe = detalhe;
		this.usuario = usuario;
		this.data = data;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PlanoAcao getPlano() {
		return plano;
	}

	public void setPlano(PlanoAcao plano) {
		this.plano = plano;
	}

	public String getDetalhe() {
		return detalhe;
	}

	public void setDetalhe(String detalhe) {
		this.detalhe = detalhe;
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
