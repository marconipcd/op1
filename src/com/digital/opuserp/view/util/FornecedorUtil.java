package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
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
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FornecedorUtil  extends Window {
	
	
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;

	boolean fornecedor= false;
	
	public FornecedorUtil(boolean modal, boolean center, boolean fornecedor){
		
		super("Selecione um Fornecedor");
		this.fornecedor = fornecedor;
		
		
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
				
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						addComponent(buildTextField());				
						addComponent(buildBtBuscar());
						setExpandRatio(tfBusca, 1.0f);
					}
				});
				
				
				
				
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
	
	public Button buildBtBuscar(){
		Button bt = new Button("Buscar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				atualizarTabela();
			}
		});
		bt.setStyleName(Reindeer.BUTTON_SMALL);
		return bt;
	}
	
	public FornecedorUtil(boolean modal, boolean center){
		
		
		super("Selecione um Fornecedor");
		
		
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
				
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");						
						addComponent(buildTextField());
						addComponent(buildBtBuscar());
						
						setExpandRatio(tfBusca, 2f);
					}
				});
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
		

	
	
	
	public Table buildTb(){
		tb = new Table(){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id")){
						
						if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
							return tb.getItem(rowId).getItemProperty("id").getValue().toString();				
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
		//tb.setColumnHeader("empresa.id", "Empresa");
		tb.setColumnHeader("razao_social", "Razão Social");
		tb.setColumnHeader("fantasia", "Fantasia");
		
		//tb.setConverter("id", null);
		
		//tb.setVisibleColumns(new Object[]{"id","razao_social","fantasia"});
		
		tb.addContainerProperty("id", String.class, "");
		tb.addContainerProperty("razao_social", String.class, "");
		tb.addContainerProperty("fantasia", String.class, "");
		
		
		tb.addListener(new ItemClickListener() {
			
			Fornecedor fornecedor;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick() && tb.getValue() != null){
					Integer codFornecedor= Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());			
					Fornecedor fornecedor = FornecedorDAO.find(codFornecedor);				
				
					fireEvent(new FornecedorEvent(getUI(), fornecedor));
					close();
					
				}
			}
		});
		
		atualizarTabela();
		
		return tb;
	}
	
	private void atualizarTabela(){
		List<Fornecedor> fornecedores = FornecedorDAO.getFornecedores("ATIVO", tfBusca.getValue());
		
		tb.removeAllItems();
		for (Fornecedor f: fornecedores) {
			tb.addItem(new Object[]{
					f.getId().toString() != null ? f.getId().toString() : "", 
					f.getRazao_social() != null ? f.getRazao_social() : "",
					f.getFantasia() != null ? f.getFantasia() : ""}, tb.getItemIds().size()+1);
		}
	
	}
	

	public TextField buildTextField() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				
			
			}
		});
		return tfBusca;
	}
	


	
	
	
	
	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tb.getValue()!= null){
					Integer codFornecedor= Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());			
					Fornecedor fornecedor = FornecedorDAO.find(codFornecedor);				
				
					fireEvent(new FornecedorEvent(getUI(), fornecedor));
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

	
	
	public void addListerner(FornecedorListerner target){
		try {
			Method method = FornecedorListerner.class.getDeclaredMethod("onSelected",FornecedorEvent.class);
			addListener(FornecedorEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(FornecedorListerner target){
		removeListener(FornecedorEvent.class, target);
	}
	public static class FornecedorEvent extends Event{

		private Fornecedor fornecedor;
		
		public FornecedorEvent(Component source, Fornecedor fornecedor) {
			super(source);		
			this.fornecedor  = fornecedor;
		}

		public Fornecedor getFornecedor() {
			
			return fornecedor;
		}		
	}
	public interface FornecedorListerner{
		public void onSelected(FornecedorEvent event);
	}
}

