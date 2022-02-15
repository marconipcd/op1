package com.digital.opuserp.view.modulos.configuracoes.config_financeiro;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.view.modulos.configuracoes.config_crm.assunto.AssuntosView;
import com.digital.opuserp.view.modulos.configuracoes.config_financeiro.contrato_cobranca.ContratoCobrancaView;
import com.digital.opuserp.view.modulos.configuracoes.config_financeiro.controle.ControleView;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class ConfigFinanceiroView extends VerticalLayout {
	 
	
		GerenciarModuloDAO gmDAO;	
		 
		public ConfigFinanceiroView(boolean act){		
			
			if(act){
				
				setSizeFull();
				gmDAO = new GerenciarModuloDAO();
				
				TabSheet ts_principal = new TabSheet();
				 ts_principal.setSizeFull();
				 
				 TabSheet.Tab tMateriais = ts_principal.addTab(buildLayout(new ContratoCobrancaView(true)), "Contratos de Cobrança");
				 TabSheet.Tab tContratos = ts_principal.addTab(buildLayout(new ControleView(true)), "Controle de Títulos");
				// tMateriais.setEnabled(false); 
			 			 
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
