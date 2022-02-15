package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="bases_analitico")
public class BasesAnalitico {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false,unique=true)
	private Integer id;
	@Column(name="NOME")
	private String nome;
	@Column(name="UPTIME")
	private String uptime;
	@Column(name="ROUTEROS")
	private String routeros;
	@Column(name="CPU")
	private Integer cpu;
	@Column(name="FIRMWARE")
	private String firmware;
	
	@Column(name="ARCHITETURE_NAME")
	private String  architecture_name;
	@Column(name="BOARD_NAME")
	private String board_name;
	@Column(name="BAD_BLOCKS")
	private Integer  bad_blocks;
	
	@Column(name="DATA")
	private Date data;
	
	
	public BasesAnalitico(){
		
	}


	public BasesAnalitico(Integer id, String nome, String uptime,
			String routeros, Integer cpu, String firmware, Date data) {
		super();
		this.id = id;
		this.nome = nome;
		this.uptime = uptime;
		this.routeros = routeros;
		this.cpu = cpu;
		this.firmware = firmware;
		this.data = data;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getUptime() {
		return uptime;
	}


	public void setUptime(String uptime) {
		this.uptime = uptime;
	}


	public String getRouteros() {
		return routeros;
	}


	public void setRouteros(String routeros) {
		this.routeros = routeros;
	}


	public Integer getCpu() {
		return cpu;
	}


	public void setCpu(Integer cpu) {
		this.cpu = cpu;
	}


	public String getFirmware() {
		return firmware;
	}


	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}


	public Date getData() {
		return data;
	}


	public void setData(Date data) {
		this.data = data;
	}


	public String getArchitecture_name() {
		return architecture_name;
	}


	public void setArchitecture_name(String architecture_name) {
		this.architecture_name = architecture_name;
	}


	public String getBoard_name() {
		return board_name;
	}


	public void setBoard_name(String board_name) {
		this.board_name = board_name;
	}


	public Integer getBad_blocks() {
		return bad_blocks;
	}


	public void setBad_blocks(Integer bad_blocks) {
		this.bad_blocks = bad_blocks;
	}

	
	
	
}
