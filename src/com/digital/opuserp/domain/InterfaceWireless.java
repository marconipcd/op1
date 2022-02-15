package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="interfacewireless")
public class InterfaceWireless {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@Column(name="NOME")
	private String nome;
	@Column(name="UPTIME")
	private String uptime;	
	
	@Column(name="ROUTEROS")
	private String routeros;
	@Column(name="FIRMEWARE")
	private String firmeware;
	@Column(name="CPU")
	private String cpu;
	@Column(name="INTERFACE")
	private String interfaces;
	
	@Column(name="SSID")
	private String ssid;
	@Column(name="MAC")
	private String mac;
	@Column(name="DISTACIA")
	private Integer distancia;
	@Column(name="CHANNEL")	
	private Integer channel;
	@Column(name="CCQ")
	private double ccq;
	@Column(name="AUTHENTICATED_CLIENTS")
	private Integer authenticated_clientes;
	@Column(name="DATA")
	private Date data;
	
	
	public InterfaceWireless(){
		
	}


	

	public InterfaceWireless(Integer id, String nome, String uptime,
			String routeros, String firmeware, String cpu, String interfaces,
			String ssid, String mac, Integer distancia, Integer channel,
			double ccq, Integer authenticated_clientes, Date data) {
		super();
		this.id = id;
		this.nome = nome;
		this.uptime = uptime;
		this.routeros = routeros;
		this.firmeware = firmeware;
		this.cpu = cpu;
		this.interfaces = interfaces;
		this.ssid = ssid;
		this.mac = mac;
		this.distancia = distancia;
		this.channel = channel;
		this.ccq = ccq;
		this.authenticated_clientes = authenticated_clientes;
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


	public void setCcq(double ccq) {
		this.ccq = ccq;
	}


	public String getSsid() {
		return ssid;
	}


	public void setSsid(String ssid) {
		this.ssid = ssid;
	}


	public String getMac() {
		return mac;
	}


	public void setMac(String mac) {
		this.mac = mac;
	}


	public Integer getDistancia() {
		return distancia;
	}


	public void setDistancia(Integer distancia) {
		this.distancia = distancia;
	}


	public Integer getChannel() {
		return channel;
	}


	public void setChannel(Integer channel) {
		this.channel = channel;
	}


	public double getCcq() {
		return ccq;
	}


	public void setCcq(Integer ccq) {
		this.ccq = ccq;
	}


	public Integer getAuthenticated_clientes() {
		return authenticated_clientes;
	}


	public void setAuthenticated_clientes(Integer authenticated_clientes) {
		this.authenticated_clientes = authenticated_clientes;
	}


	public Date getData() {
		return data;
	}


	public void setData(Date data) {
		this.data = data;
	}


	public String getRouteros() {
		return routeros;
	}


	public void setRouteros(String routeros) {
		this.routeros = routeros;
	}


	public String getFirmeware() {
		return firmeware;
	}


	public void setFirmeware(String firmeware) {
		this.firmeware = firmeware;
	}


	public String getCpu() {
		return cpu;
	}


	public void setCpu(String cpu) {
		this.cpu = cpu;
	}


	public String getInterfaces() {
		return interfaces;
	}


	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}
	
	

}
