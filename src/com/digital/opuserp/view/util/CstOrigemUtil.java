package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CstDAO;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.domain.CstOrigem;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CstOrigemUtil  extends Window {
	
	JPAContainer<CstOrigem> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;

	public CstOrigemUtil(boolean modal, boolean center){
		
		
		super("Selecione uma Origem");
		
		
		setWidth("830px");
		setHeight("360px");
		setIcon(new ThemeResource("icons/search-32.png"));
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
				
				
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				addComponent(buildTextField());
				addComponent(buildTb());
				setExpandRatio(tb, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSelecionar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		});
	}
		
	public JPAContainer<CstOrigem> buildJpaContainer(){
		container = JPAContainerFactory.make(CstOrigem.class, ConnUtil.getEntity());	
//		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
		
//		if(fornecedor){
//			container.addContainerFilter(Filters.eq("fornecedor", true));
//		}
//				
		container.sort(new String[]{"cst_origem_cod"},new boolean[]{true});
		return container;
	}
	
	
	
	public Table buildTb(){
		tb = new Table(null, buildJpaContainer());
		tb.setWidth("100%");
		tb.setHeight("230px");
		tb.setSelectable(true);
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("cst_origem_cod", "CST Código");
		tb.setColumnHeader("referencia", "Referência");
		
		
		tb.setVisibleColumns(new Object[]{"cst_origem_cod","referencia"});
		tb.addListener(new ItemClickListener() {
			
			CstOrigem origem;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					
					if(item.getItemProperty("id").toString()!=null){
						origem = CstDAO.find((Integer)item.getItemProperty("id").getValue());										
					}
					fireEvent(new CstEvent(getUI(), origem));
					close();
					
				}
			}
		});
		return tb;
	}
	
	
	public TextField buildTextField() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				
				try{
					addFilter(Integer.parseInt(event.getText()));
				}catch(Exception e){
					addFilter(event.getText());				
				}
			}
		});
		return tfBusca;
	}
	

	public void addFilter(String s) {
		container.removeAllContainerFilters();
//		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"cst_origem_cod"},new boolean[]{true});
	}
	
	public void addFilter(Integer s) {
		container.removeAllContainerFilters();
//		container.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
//		container.addContainerFilter(Filters.eq("grupoId", 21));	
			
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"cst_origem_cod"},new boolean[]{true});
	}
	
	
	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tb.getValue()!= null){
					Item item = tb.getItem(tb.getValue());			
					CstOrigem origem = CstDAO.find((Integer)item.getItemProperty("id").getValue());				
				
					fireEvent(new CstEvent(getUI(), origem));
					close();
				}	
			}	
		});
		
		ShortcutListener slbtOK = new ShortcutListener("Ok",ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};

		btSalvar.addShortcutListener(slbtOK);

		btSalvar.setStyleName("default");
		return btSalvar;
	}

	private Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
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

	
	
	public void addListerner(CstListerner target){
		try {
			Method method = CstListerner.class.getDeclaredMethod("onSelected",CstEvent.class);
			addListener(CstEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CstListerner target){
		removeListener(CstEvent.class, target);
	}
	public static class CstEvent extends Event{

		private CstOrigem origem;
		
		public CstEvent(Component source, CstOrigem origem) {
			super(source);		
			this.origem  = origem;
		}

		public CstOrigem getCstOrigem() {
			
			return origem;
		}		
	}
	public interface CstListerner{
		public void onSelected(CstEvent event);
	}
}

