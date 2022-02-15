package com.digital.opuserp.domain;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="print_log")
public class PrintLog {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	private Integer id;
	
	@Lob
	@Column(name="SCREEN")
	private byte[] screen;
	
	@OneToOne	
	@JoinColumn(name="LOG")
	private LogError log;
	
	public PrintLog()
	{
		
	}


	public PrintLog(Integer id, byte[] screen, LogError log) {
		super();
		this.id = id;
		this.screen = screen;
		this.log = log;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public byte[] getScreen() {
		return screen;
	}


	public void setScreen(byte[] screen) {
		this.screen = screen;
	}


	public LogError getLog() {
		return log;
	}


	public void setLog(LogError log) {
		this.log = log;
	}
	
	
	
}
