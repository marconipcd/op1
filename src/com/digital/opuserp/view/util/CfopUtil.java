package com.digital.opuserp.view.util;

import java.lang.reflect.Method;

import com.digital.opuserp.domain.Cfop;
import com.digital.opuserp.domain.Pis;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
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

public class CfopUtil extends Window {

	JPAContainer<Cfop> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;
	
	Integer codEmpresa;
	boolean default_mod_21;
	
	public CfopUtil(boolean modal, boolean center, Integer codEmpresa, boolean default_mod_21){
						
		super("Selecione um CFOP");
		
		this.codEmpresa = codEmpresa;
		this.default_mod_21 = default_mod_21;
		
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
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		});
	}
	
	public JPAContainer<Cfop> buildJpaContainer(){
		container = JPAContainerFactory.make(Cfop.class, ConnUtil.getEntity());

		if(default_mod_21){
			container.addContainerFilter(Filters.eq("default_nfe_mod21", true));
		}
		
		return container;
	}
	
	public void procurar(String s){
		container.removeAllContainerFilters();
		
		try{
			Integer s1 = Integer.parseInt(s);
			container.addContainerFilter(Filters.eq("id", s1));
		}catch(Exception e){
			container.addContainerFilter(Filters.like("descricao", "%"+s+"%", false));			
		}
		
		if(default_mod_21){
			container.addContainerFilter(Filters.eq("default_nfe_mod21", true));
		}
		
		container.applyFilters();
	}
	
	public Table buildTb(){
		tb = new Table(null, buildJpaContainer());
		tb.setWidth("100%");
		tb.setHeight("230px");
		tb.setSelectable(true);
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("descricao", "Descricao");
		tb.setColumnCollapsingAllowed(false);		
		tb.setVisibleColumns(new Object[]{"id","descricao"});
		tb.addListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					
					EntityItem<Cfop> entityItem = (EntityItem<Cfop>)tb.getItem(tb.getValue());
					Cfop cfop = entityItem.getEntity();
															
					fireEvent(new CfopUtilEvent(getUI(), cfop));
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
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}
	

	public void addFilter(String s) {
		container.removeAllContainerFilters();
		
		if(s != null && !s.equals("")){
			try{
				Integer s1 = Integer.parseInt(s);
				container.addContainerFilter(Filters.eq("id", s1));
			}catch(Exception e){
				container.addContainerFilter(new Like("descricao", "%"+s+"%", false));			
			}
		}
		
		if(default_mod_21){
			container.addContainerFilter(Filters.eq("default_nfe_mod21", true));
		}
		container.applyFilters();
	}
	
	
	private Button buildBtSalvar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tb.getValue()!= null){
					EntityItem<Cfop> entityItem = (EntityItem<Cfop>)tb.getItem(tb.getValue());
					Cfop cfop = entityItem.getEntity();
						
					fireEvent(new CfopUtilEvent(getUI(), cfop));
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

	
	
	public void addListerner(CfopUtilListerner target){
		try {
			Method method = CfopUtilListerner.class.getDeclaredMethod("onSelected",CfopUtilEvent.class);
			addListener(CfopUtilEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CfopUtilListerner target){
		removeListener(CfopUtilEvent.class, target);
	}
	public static class CfopUtilEvent extends Event{

		private Cfop cfop;
		
		public CfopUtilEvent(Component source, Cfop cfop) {
			super(source);		
			this.cfop  = cfop;
		}

		public Cfop getCfop() {
			return cfop;
		}		
	}
	public interface CfopUtilListerner{
		public void onSelected(CfopUtilEvent event);
	}
}
