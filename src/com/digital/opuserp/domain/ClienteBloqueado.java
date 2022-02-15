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
@Table(name="clientes_bloqueado")
public class ClienteBloqueado {
		
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@OneToOne
	@JoinColumn(name="CLIENTES_ID")	
	private Cliente cliente;
	@OneToOne
	@JoinColumn(name="PLANOS_ACESSO_ID")	
	private PlanoAcesso plano;
	@Column(name="MOTIVO")
	private String motivo;
	@Column(name="DATA_INCLUSAO")
	private Date data_inclusao;
	
	@PrePersist
	private void onCreate(){
		data_inclusao = new Date();
	}
	
	public ClienteBloqueado(){
		
	}
	public ClienteBloqueado(Integer id, Cliente cliente, PlanoAcesso plano,
			String motivo, Date data_inclusao) {
		super();
		this.id = id;
		this.cliente = cliente;
		this.plano = plano;
		this.motivo = motivo;
		this.data_inclusao = data_inclusao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public PlanoAcesso getPlano() {
		return plano;
	}

	public void setPlano(PlanoAcesso plano) {
		this.plano = plano;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Date getData_inclusao() {
		return data_inclusao;
	}

	public void setData_inclusao(Date data_inclusao) {
		this.data_inclusao = data_inclusao;
	}
	
	
}
