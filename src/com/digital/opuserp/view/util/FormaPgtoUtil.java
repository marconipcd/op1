package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FormaPgtoDAO;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.util.ConnUtil;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FormaPgtoUtil extends Window {

	JPAContainer<FormasPgto> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;

	private String status;
	
	private Label lbRegistros;
	HorizontalLayout hlFloat;
	
	
	public FormaPgtoUtil(boolean modal, boolean center){
		
		
		super("Selecione um Veículo");
		this.status = status;
		
		
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
				
				hlFloat = new HorizontalLayout();
				hlFloat.setStyleName("horizontal-hlFloatUtil");
				hlFloat.addComponent(buildLbRegistros());
				hlFloat.setComponentAlignment(lbRegistros, Alignment.MIDDLE_LEFT);

				HorizontalLayout hlRoor = new HorizontalLayout();
				hlRoor.setWidth("100%");
				hlRoor.setStyleName("horizontal-hlRootUtil");
				hlRoor.addComponent(hlFloat);
				hlRoor.setComponentAlignment(hlFloat, Alignment.TOP_LEFT);
				hlRoor.addComponent(hlButtons);
				hlRoor.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			
				addComponent(hlRoor);
			}
		});
	}
		
	public JPAContainer<FormasPgto> buildJpaContainer(){
		container = JPAContainerFactory.make(FormasPgto.class, ConnUtil.getEntity());
		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		
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
		tb.setColumnHeader("nome", "Nome");
				
		//tb.setConverter("id", null);
		
		tb.setVisibleColumns(new Object[]{"id","nome"});
		tb.addListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				
				FormasPgto c = null;
				
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					//TODO FAZER VERIFICAÇÃO DOS ITENS
					if(item.getItemProperty("id")!=null){
						c = FormaPgtoDAO.find(Integer.parseInt(item.getItemProperty("id").toString()));										
					}
					fireEvent(new FormaPgtoEvent(getUI(), c));
					close();
					
				}
			}
		});
		return tb;
	}
	
	
	public TextField buildTextField() {
		tfBusca = new TextField();
		tfBusca.setInputPrompt("Buscar...");
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}
	

	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
			
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
				for(Object c:collums){		 			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}
				}
		} catch (Exception e) {
			
			for(Object c:collums){
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	
	
	
	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tb.getValue()!= null){
					Item item = tb.getItem(tb.getValue());			
					FormasPgto c = FormaPgtoDAO.find(Integer.parseInt(item.getItemProperty("id").toString()));				
					fireEvent(new FormaPgtoEvent(getUI(), c));
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

	
	
	public void addListerner(FormaPgtoListerner target){
		try {
			Method method = FormaPgtoListerner.class.getDeclaredMethod("onSelected",FormaPgtoEvent.class);
			addListener(FormaPgtoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(FormaPgtoListerner target){
		removeListener(FormaPgtoEvent.class, target);
	}
	public static class FormaPgtoEvent extends Event{

		private FormasPgto formaPgto;
		
		public FormaPgtoEvent(Component source, FormasPgto formaPgto) {
			super(source);		
			this.formaPgto  = formaPgto;
		}

		public FormasPgto getFormaPgto() {
			
			return formaPgto;
		}		
	}
	public interface FormaPgtoListerner{
		public void onSelected(FormaPgtoEvent event);
	}
}
