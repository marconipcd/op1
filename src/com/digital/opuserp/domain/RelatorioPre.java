package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;


@Entity
@Table(name="relatorio_pre")
public class RelatorioPre {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@Column(name="COD_EMPRESA", nullable=false)
	private Integer codEmpresa;
	
	@OneToOne
	@JoinColumn(name="COD_USUARIO", nullable=false)
	private Usuario codUsuario;
	
	@Column(name="COD_SUBMODULO", nullable=false)
	private Integer codSubModulo;
	
	@Column(name="NOME_RELATORIO", nullable=false)
	private String nome_relatorio;
	
	@Column(name="TIPO_RELATORIO", nullable=true)
	private String tipo_relatorio;
	
	@Column(name="ORDENACAO_RELATORIO", nullable=true)
	private String ordenacao_relatorio;
	
	@Column(name="ORIENTACAO", nullable=true)
	private String orientacao;
	
	@Column(name="RESUMO", nullable=true)
	private String resumo;
			
	@Column(name="DATA_CADASTRO", nullable=false)
	private Date data_cadastro;
	
	
	@PrePersist
	public void onInsert(){
		data_cadastro = new Date();
	}
	
	public RelatorioPre(){
		
	}
	
	public RelatorioPre(Integer id, Integer codEmpresa, Usuario codUsuario,
			Integer codSubModulo, String nome_relatorio,String tipo_relatorio, String ordenacao,String orientacao,String resumo, Date data_cadastro) {
		super();
		this.id = id;
		this.codEmpresa = codEmpresa;
		this.codUsuario = codUsuario;
		this.codSubModulo = codSubModulo;
		this.nome_relatorio = nome_relatorio;
		this.tipo_relatorio = tipo_relatorio;
		this.ordenacao_relatorio = ordenacao;
		this.orientacao = orientacao;
		this.data_cadastro = data_cadastro;
		this.resumo =resumo;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getCodEmpresa() {
		return codEmpresa;
	}


	public void setCodEmpresa(Integer codEmpresa) {
		this.codEmpresa = codEmpresa;
	}


	public Usuario getCodUsuario() {
		return codUsuario;
	}


	public void setCodUsuario(Usuario codUsuario) {
		this.codUsuario = codUsuario;
	}


	public Integer getCodSubModulo() {
		return codSubModulo;
	}


	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}


	public String getNome_relatorio() {
		return nome_relatorio;
	}


	public void setNome_relatorio(String nome_relatorio) {
		this.nome_relatorio = nome_relatorio;
	}

	
	public Date getData_cadastro() {
		return data_cadastro;
	}


	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public String getTipo_relatorio() {
		return tipo_relatorio;
	}

	public void setTipo_relatorio(String tipo_relatorio) {
		this.tipo_relatorio = tipo_relatorio;
	}

	public String getOrdenacao_relatorio() {
		return ordenacao_relatorio;
	}

	public void setOrdenacao_relatorio(String ordenacao_relatorio) {
		this.ordenacao_relatorio = ordenacao_relatorio;
	}

	public String getOrientacao() {
		return orientacao;
	}

	public void setOrientacao(String orientacao) {
		this.orientacao = orientacao;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	
	
	
	
	
	
}
