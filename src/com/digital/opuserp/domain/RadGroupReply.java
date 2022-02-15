package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="radgroupreply")
public class RadGroupReply {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, unique=true)
	private Integer id;
	@Column(name="groupname", nullable=false)
	private String groupname;
	@Column(name="attribute", nullable=false)
	private String attribute;
	@Column(name="op", nullable=false)
	private String op;
	@Column(name="value", nullable=false)
	private String value;
	
	public RadGroupReply(){
		
	}

	public RadGroupReply(Integer id, String groupname, String attribute,
			String op, String value) {
		super();
		this.id = id;
		this.groupname = groupname;
		this.attribute = attribute;
		this.op = op;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	

}
