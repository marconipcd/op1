package com.digital.opuserp.view.modulos.configuracoes.config_crm;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.view.modulos.configuracoes.config_crm.assunto.AssuntosView;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class ConfigCRMView extends VerticalLayout {
 
	
	GerenciarModuloDAO gmDAO;	
	 
	public ConfigCRMView(boolean act){		
		
		if(act){
			
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			TabSheet ts_principal = new TabSheet();
			 ts_principal.setSizeFull();
			 
			 TabSheet.Tab tContratos = ts_principal.addTab(buildLayout(new AssuntosView(true)), "Config. de Assuntos");
			 TabSheet.Tab tMateriais = ts_principal.addTab(buildLayout(new VerticalLayout()), "Config. de Formas de Contato");
			 tMateriais.setEnabled(false); 
		 			 
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
