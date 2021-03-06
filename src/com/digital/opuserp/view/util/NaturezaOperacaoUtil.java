package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.NaturezaOperacaoDAO;
import com.digital.opuserp.domain.NaturezaOperacao;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class NaturezaOperacaoUtil  extends Window {
	
	JPAContainer<NaturezaOperacao> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;

	
	
	public NaturezaOperacaoUtil(boolean modal, boolean center){
		
		
		super("Selecione");
		
		
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
	
	private String tipo = null;
	public NaturezaOperacaoUtil(boolean modal, boolean center, String tipo){
		
		super("Selecione");
		this.tipo=  tipo;
		
		
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
		
	public JPAContainer<NaturezaOperacao> buildJpaContainer(){
		container = JPAContainerFactory.make(NaturezaOperacao.class, ConnUtil.getEntity());	
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		container.addContainerFilter(Filters.eq("visualizar", true));
		
		if(tipo != null){
			container.addContainerFilter(Filters.eq("tipo", tipo));
		}

		
		container.sort(new String[]{"descricao"},new boolean[]{true});
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
		tb.setColumnHeader("id", "C??digo");
		tb.setColumnHeader("descricao", "Descricao");
		tb.setColumnHeader("aplicacao", "Aplica????o");
		
		//tb.setConverter("id", null);
		
		tb.setVisibleColumns(new Object[]{"id","descricao","aplicacao"});
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {

			NaturezaOperacao natureza;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				
				Item item = event.getItem();
				if(event.isDoubleClick()){					
					tb.select(event.getItemId());
					if(item != null && item.getItemProperty("id").toString()!=null){
						natureza = NaturezaOperacaoDAO.find((Integer)item.getItemProperty("id").getValue());										
						fireEvent(new NaturezaUtilEvent(getUI(), natureza));
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
	
	
	public void addFilter(Integer s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		container.addContainerFilter(Filters.eq("visualizar", true));
		
		if(tipo != null){
			container.addContainerFilter(Filters.eq("tipo", tipo));
		}

		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"descricao"},new boolean[]{true});
	}

	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		container.addContainerFilter(Filters.eq("visualizar", true));
		
		if(tipo != null){
			container.addContainerFilter(Filters.eq("tipo", tipo));
		}

		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"descricao"},new boolean[]{true});
	}
	
	
	
	
	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tb.getValue()!= null){
					Item item = tb.getItem(tb.getValue());			
					NaturezaOperacao natureza = NaturezaOperacaoDAO.find((Integer)item.getItemProperty("id").getValue());				
				
					fireEvent(new NaturezaUtilEvent(getUI(), natureza));
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

	
	
	public void addListerner(NaturezaUtilListerner target){
		try {
			Method method = NaturezaUtilListerner.class.getDeclaredMethod("onSelected",NaturezaUtilEvent.class);
			addListener(NaturezaUtilEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("M??todo n??o Encontrado!");
		}
	}
	public void removeListerner(NaturezaUtilListerner target){
		removeListener(NaturezaUtilEvent.class, target);
	}
	public static class NaturezaUtilEvent extends Event{

		private NaturezaOperacao naturezaOperacao;
		
		public NaturezaUtilEvent(Component source, NaturezaOperacao naturezaOperacao) {
			super(source);		
			this.naturezaOperacao  = naturezaOperacao;
		}

		public NaturezaOperacao getNaturezaOperacao() {
			
			return naturezaOperacao;
		}		
	}
	public interface NaturezaUtilListerner{
		public void onSelected(NaturezaUtilEvent event);
	}
}

