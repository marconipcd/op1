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
import javax.persistence.Transient;

@Entity
@Table(name="crm_perguntas_rel")
public class CrmPesquisaRel {

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="COD_CRM", nullable=false)
	private Crm cod_crm;
	
	@Column(name="COD_PERGUNTA", nullable=false)
	private Integer cod_pergunta;
	@Column(name="PERGUNTA", nullable=false)
	private String pergunta;	
	@Column(name="RESPOSTA", nullable=false)
	private String resposta;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO", nullable=false)
	private Date data_cadastro;
	
	
	
	@Transient
	private String coluna;
	@Transient
	private Date coluna_date;
	@Transient
	private Integer coluna_Inter;
	@Transient
	private Long qtd;
	
	public CrmPesquisaRel(String coluna, Long qtd) {		
		this.coluna = coluna;
		this.qtd = qtd;
	}
	
	public CrmPesquisaRel(Date coluna_date, Long qtd) {		
		this.coluna_date = coluna_date;
		this.qtd = qtd;
	}

	public CrmPesquisaRel(Integer coluna_Inter, Long qtd) {		
		this.coluna_Inter = coluna_Inter;
		this.qtd = qtd;
	}

	public String getColuna() {
		return coluna;
	}

	public void setColuna(String coluna) {
		this.coluna = coluna;
	}
	
	public Long getQtd() {
		return qtd;
	}

	public CrmPesquisaRel(){
		
	}

	public CrmPesquisaRel(Integer id, Crm cod_crm, Integer cod_pergunta,
			String pergunta, String resposta,
			Date data_cadastro) {
		super();
		this.id = id;
		this.cod_crm = cod_crm;
		this.cod_pergunta = cod_pergunta;
		this.pergunta = pergunta;		
		this.resposta = resposta;
		this.data_cadastro = data_cadastro;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Crm getCod_crm() {
		return cod_crm;
	}

	public void setCod_crm(Crm cod_crm) {
		this.cod_crm = cod_crm;
	}

	public Integer getCod_pergunta() {
		return cod_pergunta;
	}

	public void setCod_pergunta(Integer cod_pergunta) {
		this.cod_pergunta = cod_pergunta;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public Date getColuna_date() {
		return coluna_date;
	}

	public void setColuna_date(Date coluna_date) {
		this.coluna_date = coluna_date;
	}

	public Integer getColuna_Inter() {
		return coluna_Inter;
	}

	public void setColuna_Inter(Integer coluna_Inter) {
		this.coluna_Inter = coluna_Inter;
	}

	public void setQtd(Long qtd) {
		this.qtd = qtd;
	}
	
	

}
