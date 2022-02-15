package com.digital.opuserp.domain;

public class XmlEmit {

	private String CNPJ;
	private String xNome;	
	private String IE;
	private String CRT;
	private XmlEnderEmit enderEmit;
	
	public XmlEmit(){
		
	}
	public XmlEmit(String CNPJ, String xNome, String IE, String CRT, XmlEnderEmit enderEmit){
		this.CNPJ = CNPJ;
		this.xNome = xNome;
		this.IE = IE;
		this.CRT = CRT;
		this.enderEmit = enderEmit;
	}
	public String getCNPJ() {
		return CNPJ;
	}
	public void setCNPJ(String cNPJ) {
		CNPJ = cNPJ;
	}
	public String getxNome() {
		return xNome;
	}
	public void setxNome(String xNome) {
		this.xNome = xNome;
	}
	public String getIE() {
		return IE;
	}
	public void setIE(String iE) {
		IE = iE;
	}
	public String getCRT() {
		return CRT;
	}
	public void setCRT(String cRT) {
		CRT = cRT;
	}
	public XmlEnderEmit getEnderEmit() {
		return enderEmit;
	}
	public void setEnderEmit(XmlEnderEmit enderEmit) {
		this.enderEmit = enderEmit;
	}
	
	
}
