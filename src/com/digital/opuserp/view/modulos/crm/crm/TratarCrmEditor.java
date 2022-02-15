package com.digital.opuserp.view.modulos.crm.crm;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TratarCrmEditor extends Window {
		
	EntityManager em = ConnUtil.getEntity();
	
	Item item;
	Button btSalvar;
	Button btCancelar;
		
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	Crm crm;
	
	TextArea taOcorrencia;
	
	public TratarCrmEditor(final Item item, final String title, boolean modal){
		
		setHeight("690px");
		this.item = item;
		setCaption(title);
		
		if(item.getItemProperty("id").getValue() != null){
			EntityItem<Crm> entityICrm = (EntityItem<Crm>) item;		
			this.crm = entityICrm.getEntity();
		}
		
				
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
				
				HorizontalLayout hlLog = new HorizontalLayout();
				hlLog.setStyleName("hl_buttons_bottom");
				hlLog.setSpacing(true);
				hlLog.setMargin(true);	
								
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);		
				
				hlButtons.addComponent(buildBtCancelar("Cancelar"));
				hlButtons.addComponent(buildBtSalvar("Gravar"));
				
				HorizontalLayout hlBtRoot = new HorizontalLayout();
				hlBtRoot.setWidth("100%");
								
				hlBtRoot.addComponent(hlButtons);
				hlBtRoot.setExpandRatio(hlButtons, 1f);
				hlBtRoot.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
				addComponent(hlBtRoot);
			}
		});
		
			
		buildLayout();			
			
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
					
		vlRoot.addComponent(	new VerticalLayout(){
				{
					setWidth("100%");
										
					addComponent(new FormLayout(){
						{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																										
								taOcorrencia = new TextArea("Ocorrência");	
								taOcorrencia.setStyleName("caption-align");
								taOcorrencia.setWidth("220px");								
								
								addComponent(taOcorrencia);								
						}
					});							
				}
		});
		
		
	}
	
	
	public Button buildBtSalvar(String s) {
			
		btSalvar = new Button(s,new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				fireEvent(new TratarCrmEditorEvent(getUI(), item, taOcorrencia.getValue(),true));									
				
				close();
			}
		});
		
		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				if(!((Component)target).getStateType().getName().equals("com.vaadin.shared.ui.textarea.TextAreaState")){
					btSalvar.click(); 
				}			
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);		
		btSalvar.setStyleName("default");
		return btSalvar;
	}

	public Button buildBtCancelar(String s) {
		btCancelar = new Button(s, new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {			
				
					fieldGroup.discard();				
					fireEvent(new TratarCrmEditorEvent(getUI(), item,taOcorrencia.getValue(), false));
					
					close();			
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		return btCancelar;
	}

	public void addListerner(TratarCrmEditorListerner target){
		try {
			Method method = TratarCrmEditorListerner.class.getDeclaredMethod("onClose", TratarCrmEditorEvent.class);
			addListener(TratarCrmEditorEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(TratarCrmEditorListerner target){
		removeListener(TratarCrmEditorEvent.class, target);
	}
	public static class TratarCrmEditorEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private String ocorrencia;
		
		public TratarCrmEditorEvent(Component source, Item item,String ocorrencia, boolean confirm) {
			super(source);
			this.item = item;
			this.ocorrencia = ocorrencia;
			this.confirm = confirm;			
		}

		public Item getItem() {
			return item;
		}	
		
		public String getOcorrencia(){
			return ocorrencia;
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface TratarCrmEditorListerner extends Serializable{
		public void onClose(TratarCrmEditorEvent event);
	}
	
		
}
