package com.digital.opuserp.view.modulos.pedido.pedidoProduto;

import java.text.SimpleDateFormat;

import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.LogPedidos;
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

public class HistoricoLogsProdutoEditor extends Window {

	JPAContainer<LogPedidos> container;
	
	TextField tfBusca;
	Table tbLogs;
	Button btFechar;
	
	EcfPreVendaCabecalho pedido;
	
	public HistoricoLogsProdutoEditor(boolean modal, boolean center, EcfPreVendaCabecalho pedido){
		
		super("Histórico de Logs Pedido");
		
		this.pedido = pedido;
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
				setExpandRatio(tbLogs, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
							
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}

		});
	}
	
	public JPAContainer<LogPedidos> buildJpaContainer(){
		container = JPAContainerFactory.make(LogPedidos.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("pedido",pedido));
				
		container.sort(new String []{"id"}, new boolean[] {true});
		return container;
	}
	
	public Table buildTbChamados(){
		tbLogs = new Table(null, buildJpaContainer());
		tbLogs.setWidth("100%");
		tbLogs.setHeight("230px");
		tbLogs.setSelectable(true);

		
		tbLogs.addGeneratedColumn("Logs", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String data = sdf.format(source.getItem(itemId).getItemProperty("data").getValue());
				
				String s = data+" - "+
						source.getItem(itemId).getItemProperty("usuario").getValue().toString()+" - "+source.getItem(itemId).getItemProperty("acao").getValue().toString();
				
				return s;
			}
		});
		
		tbLogs.setVisibleColumns(new Object[]{"Logs"});
			
		
		return tbLogs;
	}
	
	private Button buildBtCancelar() {
		btFechar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}	
		});
		
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btFechar.click();
			}
		};
		btFechar.addShortcutListener(clTb);
		
		return btFechar;
	}

}
