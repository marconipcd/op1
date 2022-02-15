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
@Table(name="lista_sites_filtro_d")
public class ListaSitesFiltroD {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, unique=true)
	private Integer id;
	@OneToOne
	@JoinColumn(name="lista_sites_filtro_c", nullable=false)
	private ListaSitesFiltroC lista_sites_filtro_c;
	@Column(name="site", nullable=false)
	private String site;
	@Column(name="data_cadastro", nullable=false)
	private Date data_cadastro;
	
	public ListaSitesFiltroD(){
		
	}

	public ListaSitesFiltroD(Integer id,
			ListaSitesFiltroC lista_sites_filtro_c, String site,
			Date data_cadastro) {
		super();
		this.id = id;
		this.lista_sites_filtro_c = lista_sites_filtro_c;
		this.site = site;
		this.data_cadastro = data_cadastro;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ListaSitesFiltroC getLista_sites_filtro_c() {
		return lista_sites_filtro_c;
	}

	public void setLista_sites_filtro_c(ListaSitesFiltroC lista_sites_filtro_c) {
		this.lista_sites_filtro_c = lista_sites_filtro_c;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}
	
}
