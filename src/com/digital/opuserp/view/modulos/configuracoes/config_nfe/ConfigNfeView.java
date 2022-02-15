package com.digital.opuserp.view.modulos.configuracoes.config_nfe;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.view.modulos.configuracoes.config_nfe.nfe_mod_21.ConfigMod21View;
import com.digital.opuserp.view.modulos.configuracoes.config_nfe.nfe_mod_55.ConfigMod55View;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class ConfigNfeView extends VerticalLayout {

 
	
	GerenciarModuloDAO gmDAO;
	
	 
	public ConfigNfeView(boolean act){		
		
		if(act){
			
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			TabSheet ts_principal = new TabSheet();
			 ts_principal.setSizeFull();
			 
			 TabSheet.Tab tNfeModelo21 = ts_principal.addTab(buildLayout(new ConfigMod21View()), "Nota Fiscal Modelo 21");
			 TabSheet.Tab tNfeModelo55 = ts_principal.addTab(buildLayout(new ConfigMod55View()), "Nota Fiscal Modelo 55");
		 
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