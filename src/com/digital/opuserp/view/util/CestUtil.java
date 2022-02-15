package com.digital.opuserp.view.util;

import java.lang.reflect.Method;

import com.digital.opuserp.domain.Cest;
import com.digital.opuserp.domain.Ncm;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.NcmUtil.NcmEvent;
import com.digital.opuserp.view.util.NcmUtil.NcmListerner;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;

public class CestUtil extends Window {

	JPAContainer<Cest> container;
	
	TextField tfBusca;
	Table tbCest;
	Button btSalvar;
	Button btCancelar;
		
	public CestUtil(boolean modal, boolean center){
		
		super("Selecione um Cest");
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
				setExpandRatio(tbCest, 2);
				
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
	
	public JPAContainer<Cest> buildJpaContainer(){
		container = JPAContainerFactory.make(Cest.class, ConnUtil.getEntity());
		return container;
	}
	public Table buildTb(){
		tbCest = new Table(null, buildJpaContainer());
		tbCest.setWidth("100%");
		tbCest.setHeight("230px");
		tbCest.setSelectable(true);
		tbCest.setColumnHeader("cest", "CEST");
		tbCest.setColumnHeader("ncm", "NCM");
		tbCest.setColumnHeader("descricao", "Descrição");		
		
		tbCest.setVisibleColumns(new Object[]{"cest","ncm","descricao"});
		tbCest.addListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					EntityItem<Cest> entityItem = (EntityItem<Cest>)tbCest.getItem(tbCest.getValue());
					
					fireEvent(new CestEvent(getUI(), entityItem.getEntity()));
					close();
					
				}
			}
		});
		return tbCest;
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
		container.addContainerFilter(
				Filters.or(
						new Like("cest", "%"+s+"%", false),
						new Like("descricao", "%"+s+"%", false)
						));
		container.applyFilters();
	}
	
	
	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tbCest.getValue()!= null){
				Item item = tbCest.getItem(tbCest.getValue());			
				
				EntityItem<Cest> entityItem = (EntityItem<Cest>)tbCest.getItem(tbCest.getValue());
				
				fireEvent(new CestEvent(getUI(), entityItem.getEntity()));
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

	public void addListerner(CestListerner target){
		try {
			Method method = CestListerner.class.getDeclaredMethod("onSelected",CestEvent.class);
			addListener(CestEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CestListerner target){
		removeListener(CestEvent.class, target);
	}
	public static class CestEvent extends Event{

		private Cest cest;
		
		public CestEvent(Component source, Cest cest) {
			super(source);		
			this.cest  = cest;
		}

		public Cest getCest() {			
			return cest;
		}		
	}
	public interface CestListerner{
		public void onSelected(CestEvent event);
	}
}
