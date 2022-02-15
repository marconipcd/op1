package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.Produto;
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

public class MaterialUtil extends Window {
	
	JPAContainer<Produto> container;
	
	TextField tfBusca;
	Table tbProdutos;
	Button btSalvar;
	Button btCancelar;

	
	
	public MaterialUtil(boolean modal, boolean center){
		
		
		super("Selecione um Material");
		
		
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
				setExpandRatio(tbProdutos, 2);
				
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
		
	public JPAContainer<Produto> buildJpaContainer(){
		container = JPAContainerFactory.make(Produto.class, ConnUtil.getEntity());	
		container.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
		
		if(OpusERP4UI.getEmpresa().getId() == 1){
			container.addContainerFilter(Filters.eq("grupoId", new GrupoProduto(21)));
		}
		
		container.sort(new String[]{"nome"},new boolean[]{true});
		return container;
	}
	
	
	
	public Table buildTb(){
		tbProdutos = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id")){
						
						if(tbProdutos.getItem(rowId).getItemProperty("id").getValue() != null){						
							return ((Integer)tbProdutos.getItem(rowId).getItemProperty("id").getValue()).toString();				
						}					
				} else{
						return super.formatPropertyValue(rowId, colId, property);
				}
				return ""; 
				
			}
		};
		tbProdutos.setWidth("100%");
		tbProdutos.setHeight("230px");
		tbProdutos.setSelectable(true);
		tbProdutos.setColumnHeader("id", "Código");
		tbProdutos.setColumnHeader("nome", "Nome");
		tbProdutos.setColumnHeader("qtdEstoque", "Saldo");
		tbProdutos.setColumnHeader("qtdEstoqueDeposito", "Saldo Deposito");
		//tbProdutos.setConverter("id", null);
		
		tbProdutos.setVisibleColumns(new Object[]{"id","nome","qtdEstoque","qtdEstoqueDeposito"});
		tbProdutos.addListener(new ItemClickListener() {
			
			Produto material;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					
					if(item != null && item.getItemProperty("id") != null && item.getItemProperty("id").toString()!=null && item.getItemProperty("nome") != null && item.getItemProperty("nome").toString()!=null && item.getItemProperty("qtdEstoque") != null && item.getItemProperty("qtdEstoque").toString()!=null){
													
						EntityItem<Produto> entityItem = (EntityItem<Produto>)item;
						material = entityItem.getEntity();									
					}
					fireEvent(new MaterialEvent(getUI(), material));
					close();
					
				}
			}
		});
		return tbProdutos;
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
		container.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("grupoId", 21));	
			
		
		Object[] collums = tbProdutos.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tbProdutos.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"nome"},new boolean[]{true});
	}
	
	public void addFilter(Integer s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("grupoId", 21));	
			
		
		Object[] collums = tbProdutos.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tbProdutos.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
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
					
				if(tbProdutos.getValue()!= null){
					Item item = tbProdutos.getItem(tbProdutos.getValue());			
					
					EntityItem<Produto> entityItem =(EntityItem<Produto>)item; 
					Produto material = entityItem.getEntity();
					
					fireEvent(new MaterialEvent(getUI(), material));
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

	
	
	public void addListerner(MaterialListerner target){
		try {
			Method method = MaterialListerner.class.getDeclaredMethod("onSelected",MaterialEvent.class);
			addListener(MaterialEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(MaterialListerner target){
		removeListener(MaterialEvent.class, target);
	}
	public static class MaterialEvent extends Event{

		private Produto material;
		
		public MaterialEvent(Component source, Produto material) {
			super(source);		
			this.material  = material;
		}

		public Produto getMaterial() {
			
			return material;
		}		
	}
	public interface MaterialListerner{
		public void onSelected(MaterialEvent event);
	}
}
