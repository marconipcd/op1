package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Not;
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
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PedidoUtil2 extends Window {
	
	JPAContainer<EcfPreVendaCabecalho> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;

	
	
	public PedidoUtil2(boolean modal, boolean center){
		
		
		super("Selecione um Pedido");
		
		
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
		
	public JPAContainer<EcfPreVendaCabecalho> buildJpaContainer(){
		container = JPAContainerFactory.make(EcfPreVendaCabecalho.class, ConnUtil.getEntity());	
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("situacao", "F"));
		container.addContainerFilter(new Not(Filters.eq("tipo", "ORCAMENTO")));

		//container.addContainerFilter(Filters.eq("tipoVenda", "SERVICO"));
				
		container.sort(new String[]{"id"},new boolean[]{false});
		container.addNestedContainerProperty("cliente.nome_razao");
		
		return container;
	}
	
	
	
	public Table buildTb(){
		tb = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("valor") && tb.getItem(rowId) != null && tb.getItem(rowId).getItemProperty(colId) != null && tb.getItem(rowId).getItemProperty(colId).getValue() != null){				
					return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
				}
				if(colId.equals("id")){
						
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}
				
				if(colId.equals("data")){
					
					return DataUtil.formatDateBra((Date)tb.getItem(rowId).getItemProperty("data").getValue());						
					
										
				}
				return super.formatPropertyValue(rowId, colId, property);			
			}
			
			
		};
		tb.setWidth("100%");
		tb.setHeight("230px");
		tb.setSelectable(true);

		tb.setVisibleColumns(new Object[]{"id","tipo","cliente.nome_razao","valor","vendedor","data"});
		
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("tipo", "Tipo");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("valor", "Valor");
		tb.setColumnHeader("vendedor", "Vendedor");
		tb.setColumnHeader("data", "Data");
		
		tb.setColumnAlignment("valor", Align.RIGHT);
		
		//tb.setConverter("id", null);
		
		tb.setImmediate(true);
		tb.addItemClickListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					
					EcfPreVendaCabecalho pedido;
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					
					if(item != null && item.getItemProperty("id") != null && item.getItemProperty("id").getValue()!=null){
						pedido = PedidoDAO.find((Integer)item.getItemProperty("id").getValue());																	
						fireEvent(new PedidoEvent(getUI(), pedido));
						close();
					}
					
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
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("situacao", "F"));
		container.addContainerFilter(Filters.eq("tipo", "PEDIDO"));
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){				
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}		
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
				try{										
					Integer valor = Integer.parseInt(s);
					filtros.add(Filters.eq(c.toString(), valor));
				}catch(Exception e){
					//e.printStackTrace();
				}
			}
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Float.class){					   	
				try{										
					 double valor = Real.formatStringToDBDouble(s);
					filtros.add(Filters.eq(c.toString(), valor));
				}catch(Exception e){
					//e.printStackTrace();
				}
			}
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
				try{										
					 Date valor = DataUtil.stringToDate(s);
					filtros.add(Filters.eq(c.toString(), valor));
				}catch(Exception e){
					//e.printStackTrace();
				}
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"id"},new boolean[]{false});
	}
	
	public void addFilter(Integer s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("situacao", "F"));
		container.addContainerFilter(Filters.eq("tipo", "PEDIDO"));
			
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
					EcfPreVendaCabecalho pedido = PedidoDAO.find((Integer)item.getItemProperty("id").getValue());
				
					fireEvent(new PedidoEvent(getUI(), pedido));
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

	
	
	public void addListerner(PedidoListerner target){
		try {
			Method method = PedidoListerner.class.getDeclaredMethod("onSelected",PedidoEvent.class);
			addListener(PedidoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(PedidoListerner target){
		removeListener(PedidoEvent.class, target);
	}
	public static class PedidoEvent extends Event{

		private EcfPreVendaCabecalho pedido;
		
		public PedidoEvent(Component source, EcfPreVendaCabecalho pedido) {
			super(source);		
			this.pedido  = pedido;
		}

		public EcfPreVendaCabecalho getPedido() {
			
			return pedido;
		}		
	}
	public interface PedidoListerner{
		public void onSelected(PedidoEvent event);
	}
}
