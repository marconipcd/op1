package com.digital.opuserp.interfaces;

import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public interface GenericView {
	
		
	
	
	public Table buildTbGeneric();
	
	public TextField buildTfbusca();
	
	public void addFilter(String s);
	
	public Button BuildbtTransferencia();
	
	public Button BuildbtAceitar();
	
	public Button BuildbtCancelar();
}
