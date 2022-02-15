package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.domain.Cliente;
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
import com.vaadin.ui.themes.Reindeer;

public class ClienteUtil extends Window {

	JPAContainer<Cliente> container;
	
	TextField tfBusca;
	Table tbCliente;
	Button btSalvar;
	Button btCancelar;

	private String status;
	
	private Label lbRegistros;
	HorizontalLayout hlFloat;
	
	
	public ClienteUtil(boolean modal, boolean center, String status){
		
		
		super("Selecione um Cliente");
		this.status = status;
		
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
				setExpandRatio(tbCliente, 2);

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
		
	public JPAContainer<Cliente> buildJpaContainer(){
		container = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
		
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
		if(status != null){
			container.addContainerFilter(Filters.eq("status", status));				
		}
		
		container.sort(new String[]{"nome_razao"},new boolean[]{true});
		return container;
	}
	
	
	
	public Table buildTb(){
		tbCliente = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id")){
						
						if(tbCliente.getItem(rowId).getItemProperty("id").getValue() != null){						
							return ((Integer)tbCliente.getItem(rowId).getItemProperty("id").getValue()).toString();				
						}					
				} else{
						return super.formatPropertyValue(rowId, colId, property);
				}
				return ""; 
				
			}
		};
		tbCliente.setWidth("100%");
		tbCliente.setHeight("230px");
		tbCliente.setSelectable(true);
		tbCliente.setColumnHeader("id", "Código");
		tbCliente.setColumnHeader("nome_razao", "Nome");
		tbCliente.setColumnHeader("nome_fantasia", "Fantasia");
		tbCliente.setColumnHeader("doc_cpf_cnpj", "CPF");
		//tbCliente.setConverter("id", null);
		
		tbCliente.setVisibleColumns(new Object[]{"id","nome_razao","nome_fantasia","doc_cpf_cnpj"});
//		tbCliente.setColumnCollapsed("nome_fantasia", true);
		tbCliente.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				Cliente c = null;
				
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					//TODO FAZER VERIFICAÇÃO DOS ITENS
					if(item != null && item.getItemProperty("id") !=null){
						c = ClienteDAO.find(Integer.parseInt(item.getItemProperty("id").toString()));										
					}
					fireEvent(new ClienteEvent(getUI(), c));
					close();
					
				}
			}
		});
		return tbCliente;
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
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
			
		
		Object[] collums = tbCliente.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
				for(Object c:collums){		 			
				if(!tbCliente.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				if(!tbCliente.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}
				}
		} catch (Exception e) {
			
			for(Object c:collums){
				if(!tbCliente.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"nome_razao"},new boolean[]{true});
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
					
				if(tbCliente.getValue()!= null){
					Item item = tbCliente.getItem(tbCliente.getValue());			
					Cliente c = ClienteDAO.find(Integer.parseInt(item.getItemProperty("id").toString()));				
					fireEvent(new ClienteEvent(getUI(), c));
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

	
	
	public void addListerner(ClienteListerner target){
		try {
			Method method = ClienteListerner.class.getDeclaredMethod("onSelected",ClienteEvent.class);
			addListener(ClienteEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClienteListerner target){
		removeListener(ClienteEvent.class, target);
	}
	public static class ClienteEvent extends Event{

		private Cliente Cliente;
		
		public ClienteEvent(Component source, Cliente Cliente) {
			super(source);		
			this.Cliente  = Cliente;
		}

		public Cliente getCliente() {
			
			return Cliente;
		}		
	}
	public interface ClienteListerner{
		public void onSelected(ClienteEvent event);
	}
}
