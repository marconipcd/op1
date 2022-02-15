package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.dao.ContasDAO;
import com.digital.opuserp.domain.Contas;
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

public class ContasUtil extends Window {

	JPAContainer<Contas> container;
	
	TextField tfBusca;
	Table tbContas;
	Button btSalvar;
	Button btCancelar;

	private String status;
	
	private Label lbRegistros;
	HorizontalLayout hlFloat;
	
	
	public ContasUtil(boolean modal, boolean center, String status){
		
		
		super("Selecione uma Conta");
		this.status = status;
		
		if(status.equals("sim")){
			super.setCaption("Selecione uma Conta Movimento");
		}
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
				setExpandRatio(tbContas, 2);

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
		
	public JPAContainer<Contas> buildJpaContainer(){
		container = JPAContainerFactory.make(Contas.class, ConnUtil.getEntity());
		
		container.sort(new String[]{"cod_cta_ref"},new boolean[]{true});
		return container;
	}
	
	
	
	public Table buildTb(){
		tbContas = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id")){
						
						if(tbContas.getItem(rowId).getItemProperty("id").getValue() != null){						
							return ((Integer)tbContas.getItem(rowId).getItemProperty("id").getValue()).toString();				
						}					
				} else{
						return super.formatPropertyValue(rowId, colId, property);
				}
				return ""; 
				
			}
		};
		tbContas.setWidth("100%");
		tbContas.setHeight("230px");
		tbContas.setSelectable(true);
		tbContas.setColumnHeader("id", "Código");
		tbContas.setColumnHeader("cod_cta_ref", "COD_CTA_REF");
		tbContas.setColumnHeader("descricao", "Descricao");
		tbContas.setColumnCollapsingAllowed(true);
		//tbContas.setConverter("id", null);
		tbContas.setColumnCollapsed("id", true);
		
		tbContas.setVisibleColumns(new Object[]{"id","cod_cta_ref","descricao"});
//		tbCliente.setColumnCollapsed("nome_fantasia", true);
		tbContas.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				Contas c = null;
				
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					//TODO FAZER VERIFICAÇÃO DOS ITENS
					if(item != null && item.getItemProperty("id") !=null){
						c = ContasDAO.find(Integer.parseInt(item.getItemProperty("id").toString()));										
					}
					fireEvent(new ContasEvent(getUI(), c));
					close();
					
				}
			}
		});
		return tbContas;
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
//		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
			
		
		Object[] collums = tbContas.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
				for(Object c:collums){		 			
				if(!tbContas.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				if(!tbContas.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}
				}
		} catch (Exception e) {
			
			for(Object c:collums){
				if(!tbContas.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"cod_cta_ref"},new boolean[]{true});
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
					
				if(tbContas.getValue()!= null){
					Item item = tbContas.getItem(tbContas.getValue());			
					Contas c = ContasDAO.find(Integer.parseInt(item.getItemProperty("id").toString()));				
					fireEvent(new ContasEvent(getUI(), c));
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

	
	
	public void addListerner(ContasListerner target){
		try {
			Method method = ContasListerner.class.getDeclaredMethod("onSelected",ContasEvent.class);
			addListener(ContasEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ContasListerner target){
		removeListener(ContasEvent.class, target);
	}
	public static class ContasEvent extends Event{

		private Contas Contas;
		
		public ContasEvent(Component source, Contas Contas) {
			super(source);		
			this.Contas  = Contas;
		}

		public Contas getContas() {
			
			return Contas;
		}		
	}
	public interface ContasListerner{
		public void onSelected(ContasEvent event);
	}
}

