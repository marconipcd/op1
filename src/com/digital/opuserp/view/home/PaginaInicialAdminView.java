package com.digital.opuserp.view.home;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.util.ConnUtil;
//import com.github.wolfie.refresher.Refresher;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class PaginaInicialAdminView extends VerticalLayout {

	JPAContainer<LogAcoes> logs;
	Table tbLogs;
	TextField tfBusca;
	
	//Refresher refresher;
	
	public PaginaInicialAdminView(){
		
		setSizeFull();
		
		addComponent(buildTextFieldBusca());	
		addComponent(buildTbLogs());
		setExpandRatio(tbLogs, 1);
		
		//addExtension(buildRefresher());		
	}
//	public Refresher buildRefresher(){
//		refresher = new Refresher();
//		refresher.setRefreshInterval(10000);
//		refresher.addListener(new Refresher.RefreshListener() {
//			
//			@Override
//			public void refresh(Refresher source) {
//				replaceComponent(tbLogs, buildTbLogs());
//				setExpandRatio(tbLogs, 1);
//			}
//		});
//		
//		return refresher;
//	}
	
	private JPAContainer<LogAcoes> buildJpaContainer(){
		
		logs = JPAContainerFactory.make(LogAcoes.class, ConnUtil.getEntity());
		
		if(tfBusca.getValue() != null || !tfBusca.getValue().isEmpty()){
			addFilter(tfBusca.getValue());
		}
		
		logs.sort(new Object[]{"id"}, new boolean[]{false});
		
		
		
		return logs;
	}
	
	private Table buildTbLogs(){
		tbLogs = new Table(null,buildJpaContainer());
		tbLogs.setSizeFull();
		tbLogs.setVisibleColumns(new Object[]{"operador","acao","data_cadastro"});

		tbLogs.setColumnHeader("id", "COD");
		tbLogs.setColumnHeader("operador", "Operador");
		tbLogs.setColumnHeader("acao", "Ação");
		tbLogs.setColumnHeader("data_cadastro", "Data");
				
		
		return tbLogs;
	}
	
	private TextField buildTextFieldBusca(){
		tfBusca = new TextField();
		tfBusca.setWidth("100%");		
		tfBusca.setTextChangeEventMode(TextChangeEventMode.EAGER);
		tfBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				
				if(!event.getText().isEmpty() && event.getText() != null){
					addFilter(event.getText());
				}
			}
		});
		
		
		tfBusca.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				replaceComponent(tbLogs, buildTbLogs());
				setExpandRatio(tbLogs, 1);
			}
		});
		
		return tfBusca;
	}
	
	private void addFilter(String s){
		logs.removeAllContainerFilters();
		
		if(tbLogs != null){
			Object[] collums = tbLogs.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			for(Object c:collums){		 
				
				if(!tbLogs.isColumnCollapsed(c.toString()) && logs.getType(c.toString()) == String.class){					   	
					filtros.add(Filters.like(c.toString(), "%"+s+"%", false));
				}			
			}
		
			logs.addContainerFilter(Filters.or(filtros));
		}
		
	}
}
