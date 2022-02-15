package com.digital.opuserp.view.modulos.configuracoes.config_estoque;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.view.modulos.configuracoes.config_estoque.cofins.CofinsView;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class ConfigEstoqueView extends VerticalLayout {
	
	GerenciarModuloDAO gmDAO;
		 
	public ConfigEstoqueView(boolean act){		
		
		if(act){
			
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			TabSheet ts_principal = new TabSheet();
			 ts_principal.setSizeFull();
			 
			 TabSheet.Tab tCofins = ts_principal.addTab(buildLayout(new CofinsView(true)), "Cofins");
			 //TabSheet.Tab tCofinsst = ts_principal.addTab(buildLayout(new CofinsStView(true)), "Cofins ST");
			 //TabSheet.Tab tIcms = ts_principal.addTab(buildLayout(new IcmsView(true)), "ICMS");
			 //TabSheet.Tab tIi = ts_principal.addTab(buildLayout(new IiView(true)), "Imposto de Importação");
			 //TabSheet.Tab tIpi = ts_principal.addTab(buildLayout(new IpiView(true)), "IPI");
			 //TabSheet.Tab tIssqn = ts_principal.addTab(buildLayout(new IssqnView(true)), "Issqn");
			 //TabSheet.Tab tPis = ts_principal.addTab(buildLayout(new PisView(true)), "Pis");
			 //TabSheet.Tab tPisSt = ts_principal.addTab(buildLayout(new PisStView(true)), "Pis ST");
	 
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