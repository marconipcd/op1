package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="servicos_itens_osi")
@Entity
public class ServicosItensOsi {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="OSI_ID", nullable=false)
	private Osi osi;
	
	@OneToOne
	@JoinColumn(name="SERVICO_ID", nullable=false)
	private Servico servico;
	
	public ServicosItensOsi(){
		
	}

	public ServicosItensOsi(Integer id, Osi osi, Servico servico) {
		super();
		this.id = id;
		this.osi = osi;
		this.servico = servico;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Osi getOsi() {
		return osi;
	}

	public void setOsi(Osi osi) {
		this.osi = osi;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}
	
	
}
