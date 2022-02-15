package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
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

public class SeriaisUtil  extends Window {
	
	JPAContainer<SerialProduto> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;

	Produto produto;
	
	boolean ativos = false;
	
	public SeriaisUtil(boolean modal, boolean center, Produto produto, boolean ativos){
		
		super("Selecione um Fornecedor");
		this.produto = produto;
		this.ativos = ativos;
	
		setWidth("900px");
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
	

		
	public JPAContainer<SerialProduto> buildJpaContainer(){
		container = JPAContainerFactory.make(SerialProduto.class, ConnUtil.getEntity());		
		container.addContainerFilter(Filters.eq("produto", produto));
		
		if(ativos){
			container.addContainerFilter(Filters.eq("status", "ATIVO"));
		}
		
		//container.sort(new String[]{"razao_social"},new boolean[]{true});
		
		return container;
	}
	
	
	
	public Table buildTb(){
		tb = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id")){
						
						if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
							return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
						}					
				} else{
						return super.formatPropertyValue(rowId, colId, property);
				}
				return ""; 
				
			}
		};
		tb.setWidth("100%");
		tb.setHeight("230px");
		tb.setSelectable(true);
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("serial", "Serial");
		tb.setColumnHeader("status", "Status");
		
		//tb.setConverter("id", null);
		
		tb.setVisibleColumns(new Object[]{"id","serial","status"});
		tb.addListener(new ItemClickListener() {
			
			SerialProduto serial;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					
					EntityItem<SerialProduto> entityItem = (EntityItem<SerialProduto>)item;
					serial = entityItem.getEntity();
					
					fireEvent(new SerialEvent(getUI(), serial));
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
		container.addContainerFilter(Filters.eq("produto", produto));
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		//container.sort(new String[]{"razao_social"},new boolean[]{true});
	}
	
	public void addFilter(Integer s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("grupoId", 21));	
			
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"nome"},new boolean[]{true});
	}
	
	
	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tb.getValue()!= null){
					Item item = tb.getItem(tb.getValue());
					EntityItem<SerialProduto> entityItem = (EntityItem<SerialProduto>) item;
					SerialProduto serial = entityItem.getEntity();				
				
					fireEvent(new SerialEvent(getUI(), serial));
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

	
	
	public void addListerner(SerialListerner target){
		try {
			Method method = SerialListerner.class.getDeclaredMethod("onSelected",SerialEvent.class);
			addListener(SerialEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(SerialListerner target){
		removeListener(SerialEvent.class, target);
	}
	public static class SerialEvent extends Event{

		private SerialProduto serial;
		
		public SerialEvent(Component source, SerialProduto serial) {
			super(source);		
			this.serial  = serial;
		}

		public SerialProduto getSerial() {
			
			return serial;
		}		
	}
	public interface SerialListerner{
		public void onSelected(SerialEvent event);
	}
}

