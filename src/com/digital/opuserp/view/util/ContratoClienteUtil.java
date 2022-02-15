package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
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
import com.vaadin.ui.themes.Reindeer;

public class ContratoClienteUtil extends Window {

	JPAContainer<AcessoCliente> container;
	
	TextField tfBusca;
	Table tbContratos;
	Button btSalvar;
	Button btCancelar;

	private String status;
	
	private Label lbRegistros;
	HorizontalLayout hlFloat;
	
	
	public ContratoClienteUtil(boolean modal, boolean center){
		
		super("Selecione um Contrato");
		
		
		addStyleName("teste");
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
				setExpandRatio(tbContratos, 2);

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
		
	public JPAContainer<AcessoCliente> buildJpaContainer(){
		container = JPAContainerFactory.make(AcessoCliente.class, ConnUtil.getEntity());
		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.not(Filters.eq("status_2", "ENCERRADO")));
		container.addNestedContainerProperty("cliente.nome_razao");
		
//		container.sort(new String[]{"cliente.nome_razao"},new boolean[]{true});
		return container;
	}
	
	
	
	public Table buildTb(){
		tbContratos = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id")){
						
						if(tbContratos.getItem(rowId).getItemProperty("id").getValue() != null){						
							return ((Integer)tbContratos.getItem(rowId).getItemProperty("id").getValue()).toString();				
						}					
				} else{
						return super.formatPropertyValue(rowId, colId, property);
				}
				return ""; 
				
			}
		};
		tbContratos.setWidth("100%");
		tbContratos.setHeight("230px");
		tbContratos.setSelectable(true);
		tbContratos.setColumnHeader("id", "Código");
		tbContratos.setColumnHeader("cliente.nome_razao", "Nome");
		
		tbContratos.setVisibleColumns(new Object[]{"id","cliente.nome_razao"});
		tbContratos.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				
				
				if(event.isDoubleClick()){
					if(tbContratos.getValue() != null){
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)tbContratos.getItem(tbContratos.getValue());						
						fireEvent(new ContratoClienteUtilEvent(getUI(), entityItem.getEntity()));
						close();					
					}
					
				}
			}
		});
		return tbContratos;
	}
	
	
	public TextField buildTextField() {
		tfBusca = new TextField();
		tfBusca.setInputPrompt("Buscar...");
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setImmediate(true);
		tfBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
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
		container.addContainerFilter(Filters.not(Filters.eq("status_2", "ENCERRADO")));
		
		Object[] collums = tbContratos.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
				for(Object c:collums){		 			
				if(!tbContratos.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				if(!tbContratos.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}
				}
		} catch (Exception e) {
			
			for(Object c:collums){
				if(!tbContratos.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
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
					
				
					if(tbContratos.getValue() != null){
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)tbContratos.getItem(tbContratos.getValue());						
						fireEvent(new ContratoClienteUtilEvent(getUI(), entityItem.getEntity()));
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
		btSalvar.addStyleName(Reindeer.BUTTON_SMALL);
		
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
		btCancelar.addStyleName(Reindeer.BUTTON_SMALL);
		
		return btCancelar;
	}

	
	
	public void addListerner(ContratoClienteUtilListerner target){
		try {
			Method method = ContratoClienteUtilListerner.class.getDeclaredMethod("onSelected",ContratoClienteUtilEvent.class);
			addListener(ContratoClienteUtilEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ContratoClienteUtilListerner target){
		removeListener(ContratoClienteUtilEvent.class, target);
	}
	public static class ContratoClienteUtilEvent extends Event{

		private AcessoCliente contrato;
		
		public ContratoClienteUtilEvent(Component source, AcessoCliente contrato) {
			super(source);		
			this.contrato = contrato;
		}

		public AcessoCliente getContrato() {			
			return contrato;
		}		
	}
	public interface ContratoClienteUtilListerner{
		public void onSelected(ContratoClienteUtilEvent event);
	}
}
