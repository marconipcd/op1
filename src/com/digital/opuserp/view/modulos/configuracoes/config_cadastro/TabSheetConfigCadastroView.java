package com.digital.opuserp.view.modulos.configuracoes.config_cadastro;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaView;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.cnae.CnaeView;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.como_nos_conheceu.ComoNosConheceuView;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.empresa.EmpresaView;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario.UsuarioView;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class TabSheetConfigCadastroView extends VerticalLayout {

	
	GerenciarModuloDAO gmDAO;
	
	 
	public TabSheetConfigCadastroView(boolean act){		
		
		if(act){
			
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			TabSheet ts_principal = new TabSheet();
			 ts_principal.setSizeFull();
			 
			 TabSheet.Tab tCategoria = ts_principal.addTab(buildLayout(new CategoriaView(true)), "Categoria");
			 TabSheet.Tab tComoNosConheceu = ts_principal.addTab(buildLayout(new ComoNosConheceuView()), "Como nos conheceu");
			 

			 			 
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