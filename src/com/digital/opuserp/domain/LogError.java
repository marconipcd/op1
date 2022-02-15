package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="log_error_four")
@Cacheable(value=false)
public class LogError {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;	
	private String classe;
	private String funcao;
	private String detalhes;
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	@OneToOne
	@JoinColumn(nullable=false)
	private Usuario usuario;
	
	@OneToOne
	@JoinColumn(nullable=true)
	private Empresa empresa;
	
	@PrePersist
	private void onCreate(){
		data = new Date();
	}
	
	public LogError(){
		
	}
	
	public LogError(Integer id){
		super();
		this.id = id;
	}

	public LogError(Integer id, String classe, String funcao, String detalhes,
			 Empresa empresa, Usuario user) {
		super();
		this.id = id;
		this.classe = classe;
		this.funcao = funcao;
		this.detalhes = detalhes;		
		this.empresa = empresa;
		this.usuario = user;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getFuncao() {
		return funcao;
	}

	public void setFuncao(String funcao) {
		this.funcao = funcao;
	}

	public String getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(String detalhes) {
		this.detalhes = detalhes;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	
}
