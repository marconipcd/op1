package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="notas")
@Cacheable(value=false)
public class Notas {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private Integer id;
	@OneToOne	
	private Usuario usuario;
	@OneToOne
	private Empresa empresa;
	@Column(columnDefinition="TEXT", nullable=true)
	private String nota;
	@Temporal(TemporalType.TIMESTAMP)
	private Date data_alteracao;
	
	@PrePersist
	private void onUpdate(){
		data_alteracao = new Date();
	}
	
	public Notas(){
		
	}

	public Notas(Integer id, Usuario usuario, Empresa empresa, String nota,
			Date data_alteracao) {
		super();
		this.id = id;
		this.usuario = usuario;
		this.empresa = empresa;
		this.nota = nota;
		this.data_alteracao = data_alteracao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public Date getData_alteracao() {
		return data_alteracao;
	}

	public void setData_alteracao(Date data_alteracao) {
		this.data_alteracao = data_alteracao;
	}
	
	
}
