package com.digital.opuserp.view.util;

import java.lang.reflect.Method;

import com.digital.opuserp.domain.Cnae;
import com.digital.opuserp.domain.Ncm;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.CnaeUtil.CnaeEvent;
import com.digital.opuserp.view.util.CnaeUtil.CnaeListerner;
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

public class NcmUtil extends Window {

	JPAContainer<Ncm> container;
	
	TextField tfBusca;
	Table tbNcm;
	Button btSalvar;
	Button btCancelar;
		
	public NcmUtil(boolean modal, boolean center){
		
		super("Selecione um Ncm");
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
				setExpandRatio(tbNcm, 2);
				
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
	
	public JPAContainer<Ncm> buildJpaContainer(){
		container = JPAContainerFactory.make(Ncm.class, ConnUtil.getEntity());
		return container;
	}
	public Table buildTb(){
		tbNcm = new Table(null, buildJpaContainer());
		tbNcm.setWidth("100%");
		tbNcm.setHeight("230px");
		tbNcm.setSelectable(true);
		tbNcm.setColumnHeader("codigo", "Código");
		tbNcm.setColumnHeader("descricao", "Descrição");		
		
		tbNcm.setVisibleColumns(new Object[]{"codigo","descricao"});
		tbNcm.addListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					EntityItem<Ncm> entityItem = (EntityItem<Ncm>)tbNcm.getItem(tbNcm.getValue());
					
					fireEvent(new NcmEvent(getUI(), entityItem.getEntity()));
					close();
					
				}
			}
		});
		return tbNcm;
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
						new Like("codigo", "%"+s+"%", false),
						new Like("descricao", "%"+s+"%", false)
						));
		container.applyFilters();
	}
	
	
	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tbNcm.getValue()!= null){
				Item item = tbNcm.getItem(tbNcm.getValue());			
				
				EntityItem<Ncm> entityItem = (EntityItem<Ncm>)tbNcm.getItem(tbNcm.getValue());
				
				fireEvent(new NcmEvent(getUI(), entityItem.getEntity()));
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

	
	
	public void addListerner(NcmListerner target){
		try {
			Method method = NcmListerner.class.getDeclaredMethod("onSelected",NcmEvent.class);
			addListener(NcmEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(NcmListerner target){
		removeListener(NcmEvent.class, target);
	}
	public static class NcmEvent extends Event{

		private Ncm ncm;
		
		public NcmEvent(Component source, Ncm ncm) {
			super(source);		
			this.ncm  = ncm;
		}

		public Ncm getNcm() {
			
			return ncm;
		}		
	}
	public interface NcmListerner{
		public void onSelected(NcmEvent event);
	}
}
