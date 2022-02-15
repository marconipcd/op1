package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

@Entity
public class ArquivosOsp {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="OSP_ID", nullable=false)
	private Osp osp;
	
	@Column(name="NOME")
	private String nome;
	
	@Lob
	@Column(name="ARQUIVO")
    private byte[] file;
	
	public ArquivosOsp(){
		
	}
	
	public ArquivosOsp(Integer id, Osp osp, String nome, byte[] file) {
		super();
		this.id = id;
		this.osp = osp;
		this.nome = nome;
		this.file = file;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Osp getOsp() {
		return osp;
	}

	public void setOsp(Osp osp) {
		this.osp = osp;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}
	
	
	
}
