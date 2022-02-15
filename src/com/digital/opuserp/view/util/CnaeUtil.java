package com.digital.opuserp.view.util;

import java.lang.reflect.Method;

import com.digital.opuserp.domain.Cnae;
import com.digital.opuserp.util.ConnUtil;
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

public class CnaeUtil extends Window {

	JPAContainer<Cnae> container;
	
	TextField tfBusca;
	Table tbCnae;
	Button btSalvar;
	Button btCancelar;
	
	
	public CnaeUtil(boolean modal, boolean center){
		
		super("Selecione um CNAE");
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
				setExpandRatio(tbCnae, 2);
				
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
	
	public JPAContainer<Cnae> buildJpaContainer(){
		container = JPAContainerFactory.make(Cnae.class, ConnUtil.getEntity());
		return container;
	}
	public Table buildTb(){
		tbCnae = new Table(null, buildJpaContainer());
		tbCnae.setWidth("100%");
		tbCnae.setHeight("230px");
		tbCnae.setSelectable(true);
		tbCnae.setColumnHeader("codigo", "Código");
		tbCnae.setColumnHeader("descricao", "Descrição");		
		
		tbCnae.setVisibleColumns(new Object[]{"codigo","descricao"});
		tbCnae.addListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					Cnae cep = new Cnae(
							Integer.parseInt(item.getItemProperty("id").toString()),
							item.getItemProperty("codigo").toString(), 
							item.getItemProperty("descricao").toString());												
					
					fireEvent(new CnaeEvent(getUI(), cep));
					close();
					
				}
			}
		});
		return tbCnae;
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
					
				if(tbCnae.getValue()!= null){
				Item item = tbCnae.getItem(tbCnae.getValue());			
				Cnae cep = new Cnae(
						Integer.parseInt(item.getItemProperty("id").toString()),
						item.getItemProperty("codigo").toString(), 
						item.getItemProperty("descricao").toString()
						);
				
				fireEvent(new CnaeEvent(getUI(), cep));
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

	
	
	public void addListerner(CnaeListerner target){
		try {
			Method method = CnaeListerner.class.getDeclaredMethod("onSelected",CnaeEvent.class);
			addListener(CnaeEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CnaeListerner target){
		removeListener(CnaeEvent.class, target);
	}
	public static class CnaeEvent extends Event{

		private Cnae cnae;
		
		public CnaeEvent(Component source, Cnae cnae) {
			super(source);		
			this.cnae  = cnae;
		}

		public Cnae getCnae() {
			
			return cnae;
		}		
	}
	public interface CnaeListerner{
		public void onSelected(CnaeEvent event);
	}
}
