package com.digital.opuserp.view.modulos.nfe.modelo_21.notas;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.interfaces.GenericEditor;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class NotasEditorBoleto extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TextField txtBoleto;
	TextField txtCliente;
	
	public NotasEditorBoleto(Item item, String title, boolean modal){
		this.item = item;
		
		
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
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		buildLayout();
		

	}


	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
					
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
													
				txtBoleto = new TextField ("Boleto");				
				txtBoleto.setWidth("100%");
				txtBoleto.setNullRepresentation("");
				txtBoleto.setRequired(true);
				txtBoleto.setMaxLength(200);
				txtBoleto.setImmediate(true); 
				txtBoleto.focus();
				addComponent(txtBoleto);				
				
				EntityItem<NfeMestre> entityItem = (EntityItem<NfeMestre>)item;				
				ContasReceber cr = entityItem.getEntity().getContas_receber();
				
				txtBoleto.setValue(cr != null ? cr.getId().toString() : "");
				

			}
		});
		
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid()){
				
					//fieldGroup.commit();				
					fireEvent(new NotasEditorBoletoEvent(getUI(), Integer.parseInt(txtBoleto.getId()), true));					
						
				}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
					fireEvent(new NotasEditorBoletoEvent(getUI(), Integer.parseInt(txtBoleto.getId()), false));
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
	
	
	public void addListerner(NotasEditorBoletoListerner target){
		try {
			Method method = NotasEditorBoletoListerner.class.getDeclaredMethod("onClose", NotasEditorBoletoEvent.class);
			addListener(NotasEditorBoletoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(NotasEditorBoletoListerner target){
		removeListener(NotasEditorBoletoEvent.class, target);
	}
	public static class NotasEditorBoletoEvent extends Event{
		
		private Integer boleto;
		private boolean confirm;
		
		public NotasEditorBoletoEvent(Component source, Integer boleto, boolean confirm) {
			super(source);
			this.boleto = boleto;
			this.confirm = confirm;			
		}

		public Integer getBoleto() {
			return boleto;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface NotasEditorBoletoListerner extends Serializable{
		public void onClose(NotasEditorBoletoEvent event);
	}

	
}
