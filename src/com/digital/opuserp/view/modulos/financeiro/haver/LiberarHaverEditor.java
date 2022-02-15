package com.digital.opuserp.view.modulos.financeiro.haver;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.HaverEmpresaDAO;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class LiberarHaverEditor extends Window  {

	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private Haver haver;
	public LiberarHaverEditor(Haver haver, String title, boolean modal){
		this.haver = haver;
		
		setHeight("330px");
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");
		
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				addComponent(vlRoot);
				
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				
			
				hlButtons.addComponent(buildBtCancelar("Fechar"));
//				hlButtons.addComponent(buildBtSalvar());
			
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		buildLayout();
		
	}
	
	public void buildLayout(){
		
		
					
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					addComponent(buildTbEmpresa());					
				}
			});
				
	}
	
	private Table tbEmpresa;
	private Table buildTbEmpresa(){
		tbEmpresa = new Table(null, buildContainer());
		tbEmpresa.setHeight("180px");
		tbEmpresa.setVisibleColumns(new Object[]{"cnpj","razao_social","nome_fantasia"});
		tbEmpresa.setColumnHeader("cnpj", "CNPJ");
		tbEmpresa.setColumnHeader("razao_social", "Razão Social");
		tbEmpresa.setColumnHeader("nome_fantasia", "Fantasia");
		
		tbEmpresa.addGeneratedColumn("c", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				
				final CheckBox check = new CheckBox();
				check.addStyleName("no-padding");
				check.setImmediate(true);
				
				
				EntityItem<Empresa> itemEmpresa= (EntityItem<Empresa>)source.getItem(itemId);
				
				boolean ck = HaverEmpresaDAO.check(haver, itemEmpresa.getEntity());
				
				check.setValue(ck);
				
				
				
				
				
				check.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						
						EntityItem<Empresa> itemEmpresa= (EntityItem<Empresa>)source.getItem(itemId);
						if(check.getValue()){
							HaverEmpresaDAO.add(haver, itemEmpresa.getEntity());
						}else{
							HaverEmpresaDAO.remove(haver, itemEmpresa.getEntity());
						}
					}
				});
				
				
				return check;
			}
		});
		
		tbEmpresa.setColumnWidth("c", 40);
		
		return tbEmpresa;
	}
	
	private JPAContainer<Empresa> buildContainer(){
		JPAContainer<Empresa> container = JPAContainerFactory.make(Empresa.class,ConnUtil.getEntity());
		
		return container;
	}
	
	

	
	public Button buildBtCancelar(String lb) {
		btCancelar = new Button(lb, new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
							
//					fireEvent(new HaverEmpresaEvent(getUI(), false));
					close();
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		
		
		return btCancelar;
	}
	
	
	public void addListerner(HaverEmpresaListerner target){
		try {
			Method method = HaverEmpresaListerner.class.getDeclaredMethod("onClose", HaverEmpresaEvent.class);
			addListener(HaverEmpresaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(HaverEmpresaListerner target){
		removeListener(HaverEmpresaEvent.class, target);
	}
	public static class HaverEmpresaEvent extends Event{
		
		private boolean confirm;
		
		public HaverEmpresaEvent(Component source,boolean confirm) {
			super(source);			
			this.confirm = confirm;			
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface HaverEmpresaListerner extends Serializable{
		public void onClose(HaverEmpresaEvent event);
	}

	
}
