package com.digital.opuserp.view.util;

import java.lang.reflect.Method;

import com.digital.opuserp.domain.Ceps;
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

public class CepUtil extends Window {

	JPAContainer<Ceps> container;
	
	TextField tfBusca;
	Table tbCeps;
	Button btSalvar;
	Button btCancelar;
	
	
	public CepUtil(boolean modal, boolean center){
		
		super("Selecione um Cep");
		setStyleName("disable_scroolbar");
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
				addComponent(buildTbCeps());
				setExpandRatio(tbCeps, 2);
				
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
	
	public JPAContainer<Ceps> buildJpaContainer(){
		container = JPAContainerFactory.make(Ceps.class, ConnUtil.getEntity());
		return container;
	}
	
	public void procurarCep(String cep){
		container.removeAllContainerFilters();
		container.addContainerFilter("cep", "%"+cep+"%", true, false);
		container.applyFilters();
	}
	
	public Table buildTbCeps(){
		tbCeps = new Table(null, buildJpaContainer());
		tbCeps.setWidth("100%");
		tbCeps.setHeight("230px");
		tbCeps.setSelectable(true);
		tbCeps.setColumnHeader("cep", "Cep");
		tbCeps.setColumnHeader("endereco", "Endereço");
		tbCeps.setColumnHeader("bairro", "Bairro");
		tbCeps.setColumnHeader("uf", "UF");
		tbCeps.setColumnHeader("cidade", "Cidade");
		tbCeps.setColumnHeader("pais", "País");
		
		tbCeps.setVisibleColumns(new Object[]{"cep","endereco","bairro","cidade","uf","pais"});
		tbCeps.addListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Item item = tbCeps.getItem(tbCeps.getValue());
					
					if(item.getItemProperty("cep").getValue() != null && item.getItemProperty("endereco").getValue() != null && 
						item.getItemProperty("bairro").getValue() != null && item.getItemProperty("cidade").getValue() != null &&
						item.getItemProperty("uf").getValue() != null && item.getItemProperty("pais").getValue() != null){
						
						Ceps cep = new Ceps(
								item.getItemProperty("cep").toString(), 
								item.getItemProperty("endereco").toString(), 
								item.getItemProperty("bairro").toString(), 
								item.getItemProperty("cidade").toString(), 
								item.getItemProperty("uf").toString(), 
								item.getItemProperty("pais").toString());
						
						fireEvent(new CepEvent(getUI(), cep));
						close();
					}
				}
			}
		});
		return tbCeps;
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
						new Like("cep", "%"+s+"%", false),
						new Like("endereco", "%"+s+"%", false),
						new Like("bairro", "%"+s+"%", false),
						new Like("cidade", "%"+s+"%", false),
						new Like("uf", "%"+s+"%", false),
						new Like("pais", "%"+s+"%", false)));
		container.applyFilters();
	}
	
	
	private Button buildBtSalvar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tbCeps.getValue()!= null){
					Item item = tbCeps.getItem(tbCeps.getValue());			
					Ceps cep = new Ceps(
							item.getItemProperty("cep").toString(), 
							item.getItemProperty("endereco").toString(), 
							item.getItemProperty("bairro").toString(), 
							item.getItemProperty("cidade").toString(), 
							item.getItemProperty("uf").toString(), 
							item.getItemProperty("pais").toString());
					
					fireEvent(new CepEvent(getUI(), cep));
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

	
	
	public void addListerner(CepListerner target){
		try {
			Method method = CepListerner.class.getDeclaredMethod("onSelected",CepEvent.class);
			addListener(CepEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CepListerner target){
		removeListener(CepEvent.class, target);
	}
	public static class CepEvent extends Event{

		private Ceps ceps;
		
		public CepEvent(Component source, Ceps cep) {
			super(source);		
			this.ceps  = cep;
		}

		public Ceps getCep() {
			return ceps;
		}		
	}
	public interface CepListerner{
		public void onSelected(CepEvent event);
	}
}
