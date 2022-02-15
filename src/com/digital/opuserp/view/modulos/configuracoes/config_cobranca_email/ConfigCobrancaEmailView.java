package com.digital.opuserp.view.modulos.configuracoes.config_cobranca_email;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.view.modulos.configuracoes.config_cobranca_email.boleto.ConfigBoletoView;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class ConfigCobrancaEmailView extends VerticalLayout {

	GerenciarModuloDAO gmDAO;
	
	public ConfigCobrancaEmailView(boolean act){		
		
		if(act){
			
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			TabSheet ts_principal = new TabSheet();
			 ts_principal.setSizeFull();
			 
			 TabSheet.Tab tNfeModelo21 = ts_principal.addTab(buildLayout(new ConfigBoletoView()), "Boleto");
			 
			 addComponent(ts_principal);    
		}
	}
	
	private VerticalLayout buildLayout(Component c){
		VerticalLayout vl1 = new VerticalLayout();		
		vl1.addComponent(c);
		vl1.setSizeFull();
		return vl1;
	}
	
}