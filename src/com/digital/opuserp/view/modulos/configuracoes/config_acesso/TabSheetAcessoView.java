package com.digital.opuserp.view.modulos.configuracoes.config_acesso;


import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.central.ConfigCentralAssinanteView;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.contratos.ContratosView;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.materiais.ConfigMaterialAcessoView;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.pool.PoolEditor;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.pool.PoolView;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class TabSheetAcessoView extends VerticalLayout {

 
	
	GerenciarModuloDAO gmDAO;
	
	 
	public TabSheetAcessoView(boolean act){		
		
		if(act){
			
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			TabSheet ts_principal = new TabSheet();
			 ts_principal.setSizeFull();
			 
			 TabSheet.Tab tContratos = ts_principal.addTab(buildLayout(new ContratosView(true)), "Contratos");
			 TabSheet.Tab tMateriais = ts_principal.addTab(buildLayout(new ConfigMaterialAcessoView()), "Material");
			 TabSheet.Tab tAssinante = ts_principal.addTab(buildLayout(new ConfigCentralAssinanteView()), "Central de Assinante");
//			 TabSheet.Tab tPool = ts_principal.addTab(buildLayout(new PoolView()), "Pool");
			 
			 			 
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