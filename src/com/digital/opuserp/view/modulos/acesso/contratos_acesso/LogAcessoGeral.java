package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.domain.AlteracoesMateriais;
import com.digital.opuserp.domain.SystemEvents;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class LogAcessoGeral extends Window {

	JPAContainer<SystemEvents> container;
	
	TextField tfBusca;
	Table tbAlteracoes;
	Button btSalvar;
	Button btCancelar;
	
	
	
	public LogAcessoGeral(boolean modal, boolean center){
		
		super("Log de Acesso Geral");
	
		setWidth("1030px");
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
				
				addComponent(buildTxtBusca());
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
	
	private TextField txtBusca;
	private TextField buildTxtBusca(){
		txtBusca = new TextField();
		txtBusca.setWidth("100%");
		txtBusca.setInputPrompt("buscar...");
		
		txtBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
					addFilter(event.getText());
			}
		});
		
		return txtBusca;
	}
	
	public void addFilter(String s) {		
		container.removeAllContainerFilters();
		container.setApplyFiltersImmediately(false);
				
		if(s != null && !s.equals("") && !s.isEmpty()){


			container.addContainerFilter(new Like("Message", "%"+s+"%", false));			

		}
		
		container.sort(new Object[]{"DeviceReportedTime"}, new boolean[]{false});
		container.applyFilters();
			
	}
	
	
	public JPAContainer<SystemEvents> buildJpaContainer(){
		container = JPAContainerFactory.make(SystemEvents.class, ConnUtil.getEntity());
	
		container.sort(new Object[]{"DeviceReportedTime"}, new boolean[]{false});
		
		return container;
	}
	
	public Table buildTbChamados(){
		tbAlteracoes = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
//				if(colId.equals("id") && tbAlteracoes.getItem(rowId).getItemProperty("id").getValue() != null){
//										
//					return ((Integer)tbAlteracoes.getItem(rowId).getItemProperty("id").getValue()).toString();										
//				}
//				
//				if(colId.equals("acesso_cliente.id") && tbAlteracoes.getItem(rowId).getItemProperty("acesso_cliente.id").getValue() != null){
//					
//					return ((Integer)tbAlteracoes.getItem(rowId).getItemProperty("acesso_cliente.id").getValue()).toString();										
//				}
				
//				if(colId.equals("acesso_cliente.cliente")){
//					
//					EntityItem<T>
//				}
//								
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};
		
		tbAlteracoes.setSizeFull();
		tbAlteracoes.setSelectable(true);

		tbAlteracoes.setVisibleColumns(new Object[]{"DeviceReportedTime"});
		
		tbAlteracoes.setColumnHeader("DeviceReportedTime", "Data");
		//tbAlteracoes.setColumnHeader("Message", "Log");
		
		
		tbAlteracoes.addGeneratedColumn("Ip Cliente", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {

				String link[] = source.getItem(itemId).getItemProperty("Message").getValue().toString().split("\\|");
				return link[0];		
			}
		});
		
		tbAlteracoes.addGeneratedColumn("Ip Servidor", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {

				String link[] = source.getItem(itemId).getItemProperty("Message").getValue().toString().split("\\|");
				return link[1];		
			}
		});
		
		tbAlteracoes.addGeneratedColumn("Url", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				
					try{
							String link[] = source.getItem(itemId).getItemProperty("Message").getValue().toString().split("\\|");
							URL aURL = new URL(link[2]);
					
									
									
									return new Label("<a target='_blank' href='http://"+aURL.getHost()+aURL.getPath()+"'>"+aURL.getHost()+aURL.getPath()+"</a>",ContentMode.HTML);
					}catch(Exception e){
						e.printStackTrace();
						return null;
					}
				
				
			}
		});

				
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
