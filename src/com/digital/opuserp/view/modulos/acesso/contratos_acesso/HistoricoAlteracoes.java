package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesContrato;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HistoricoAlteracoes extends Window {

	JPAContainer<AlteracoesContrato> container;
	
	TextField tfBusca;
	Table tbAlteracoes;
	Button btSalvar;
	Button btCancelar;
	
	Integer codContrato;
	
	public HistoricoAlteracoes(boolean modal, boolean center, Integer codContrato){
		
		super("Histórico de Alteracoes");
		
		this.codContrato = codContrato;
		setWidth("830px");
		setHeight("360px");
		
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
						
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				addComponent(buildTbChamados());
				setExpandRatio(tbAlteracoes, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				btCancelar.focus();
				//hlButtons.addComponent(buildBtSalvar());
								
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}

		});
	}
	
	public JPAContainer<AlteracoesContrato> buildJpaContainer(){
		container = JPAContainerFactory.make(AlteracoesContrato.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("contrato", new AcessoCliente(codContrato)));
		container.addNestedContainerProperty("operador.username");
		
		container.sort(new String []{"id"}, new boolean[] {false});
		return container;
	}
	
	public Table buildTbChamados(){
		tbAlteracoes = new Table(null, buildJpaContainer());
		//tbAlteracoes.setWidth("100%");
		//tbAlteracoes.setHeight("230px");
		tbAlteracoes.setSizeFull();
		tbAlteracoes.setSelectable(true);

		tbAlteracoes.setVisibleColumns(new Object[]{"data_alteracao","tipo","operador.username"});
		
		tbAlteracoes.setColumnHeader("tipo", "Tipo");
		tbAlteracoes.setColumnHeader("data_alteracao", "Data Alteração");		
		tbAlteracoes.setColumnHeader("operador.username", "Operador");
		
		
		return tbAlteracoes;
	}
	
	private Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}	
		});
		
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}

}
