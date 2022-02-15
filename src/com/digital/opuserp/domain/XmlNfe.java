package com.digital.opuserp.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("nfe")
public class XmlNfe {

	private String infNFe;
	private XmlIde ide;
	
	
	public XmlNfe(String infNFe, XmlIde ide) {
		super();
		this.infNFe = infNFe;
		this.ide = ide;
	}


	public String getInfNFe() {
		return infNFe;
	}


	public void setInfNFe(String infNFe) {
		this.infNFe = infNFe;
	}


	public XmlIde getIde() {
		return ide;
	}


	public void setIde(XmlIde ide) {
		this.ide = ide;
	}
	
	
}
