package com.digital.opuserp.view.modulos.configuracoes.config_sistema;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.contratos.ContratosView;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.materiais.ConfigMaterialAcessoView;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.cnae.CnaeView;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.empresa.EmpresaView;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario.UsuarioView;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class TabSheetConfigSistemaView extends VerticalLayout {

	
	GerenciarModuloDAO gmDAO;
	
	 
	public TabSheetConfigSistemaView(boolean act){		
		
		if(act){
			
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			TabSheet ts_principal = new TabSheet();
			 ts_principal.setSizeFull();
			 
			 TabSheet.Tab tEmpresa = ts_principal.addTab(buildLayout(new EmpresaView(true)), "Empresa");
			 TabSheet.Tab tUsuario = ts_principal.addTab(buildLayout(new UsuarioView(true)), "Usuario");
			 TabSheet.Tab tCnae = ts_principal.addTab(buildLayout(new CnaeView()), "Cnae");

			 			 
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