package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.util.ConnUtil;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class EditarRegimeProprioParcialTeste extends VerticalLayout {

	public EditarRegimeProprioParcialTeste(){
		
		buildLeiaute();
	}
	
	private void buildLeiaute(){
		
		
		VerticalLayout vlRoot = new VerticalLayout();
		
		TextField txtForm1 = new TextField();
		
		
		txtForm1.setId("txtForm1");
		txtForm1.setNullRepresentation("");
		txtForm1.setStyleName("customField");
		txtForm1.setInputPrompt("Form...");
		txtForm1.setMaxLength(250);
		txtForm1.setWidth("100%");
		txtForm1.setTabIndex(0);
		txtForm1.setRequired(true);
		txtForm1.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {

				EntityManager em = ConnUtil.getEntity();
				String s = event.getText();
				Query q = em.createNamedQuery("SELECT * FROM clientes c WHERE c.nome_razao LIKE '%"+s+"%'");
						
			}
		});
		
		
				
	}
}
