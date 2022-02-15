package com.digital.opuserp.view.modulos.configuracoes.log;

import org.vaadin.addons.idle.Idle;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

public class LogView extends VerticalLayout {
	
	//
	TextField tfBusca;
	Table tb;
	
	HorizontalLayout hlFloat;	
	
	JPAContainer<LogAcoes> containerSetores;
	
	private Label lbRegistros;	
	private Integer codSubModulo;	
	GerenciarModuloDAO gmDAO;
	
	public LogView(boolean act){
		
		if(act){
			setSizeFull();
			
			
			gmDAO = new GerenciarModuloDAO();
					
			//addComponent(buildTfbusca());		
			addComponent(buildtb());
					
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildLbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
			lbLegend.setWidth("250px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
					
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
		
		
			Idle.track(OpusERP4UI.getCurrent(), 300000, new Idle.Listener() {
				
	            @Override
	            public void userInactive() {
	            	if(OpusERP4UI.getCurrent().getUI().getWindows().size() == 0){
						refresh();
					}	            	
	            }
	
	            @Override
	            public void userActive() {
	            	
	            }
	        });
		}
						
	}
	
	public void refresh(){
		replaceComponent(tb, buildtb());
		setExpandRatio(tb, 1);		
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(tb.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	JPAContainer<LogAcoes> container;
	private JPAContainer<LogAcoes> buildContainer(){
		container = JPAContainerFactory.make(LogAcoes.class, ConnUtil.getEntity());
		container.sort(new Object[]{"id"} ,new boolean[]{false});
				
		return container;
	}
	
	public Table buildtb() {
		
		tb = new TreeTable();
		tb.setContainerDataSource(buildContainer());
		tb.setSizeFull();		
		tb.setColumnCollapsingAllowed(true);		
		tb.setImmediate(true);
		
		tb.setVisibleColumns(new Object[]{"operador","acao","data_cadastro"});
		tb.setColumnHeader("operador", "Operador");
		tb.setColumnHeader("acao", "Ação");
		tb.setColumnHeader("data_cadastro", "Data Cadastro");
			
		return tb;
	}

	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setImmediate(true);
		tfBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				if(event.getText() != null)
				addFilter(event.getText());
			}
		});
				
		return tfBusca;
	}

	public void addFilter(Integer s) {
		// TODO Auto-generated method stub
		
	}	
	public void addFilter(String s) {
		refresh();
	}

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
	
	
}
