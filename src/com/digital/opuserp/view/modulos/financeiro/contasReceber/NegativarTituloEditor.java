package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class NegativarTituloEditor extends Window {
	
	private Button btSalvar; 
	private Button btCancelar;	
	private VerticalLayout vlRoot;
	
		
	private ComboBox cbStatusNegativacao;
	
	
	public NegativarTituloEditor(String title, boolean modal){
						
		setWidth("455px");
		setHeight("181px");
		
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
		
		
			vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					cbStatusNegativacao = new ComboBox("Tipo");
					cbStatusNegativacao.setStyleName("caption-align");
					cbStatusNegativacao.setNullSelectionAllowed(false);
					cbStatusNegativacao.setRequired(true);
					cbStatusNegativacao.addItem("NEGATIVADO");
					cbStatusNegativacao.addItem("NEGATIVADO|AVALISTA");				
										
					addComponent(cbStatusNegativacao);				
				}
			});
			
			
			
	}	
	
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
						
					if(cbStatusNegativacao.isValid()){
						
						fireEvent(new NegativarTituloEvent(event.getComponent().getParent(), true, cbStatusNegativacao.getValue().toString()));						
						Notification.show("Título(s) Baixado Com Sucesso!");
						close();
					}else{
						Notification.show("Não é Possivel Baixar o Título, Verifique se todos os Campos estão Preenchidos!");
					}					
		
				
			}
		});
		btSalvar.addStyleName("default");
		ShortcutListener clTb = new ShortcutListener("OK", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);
		
		
		return btSalvar;
	}

	
	public Button buildBtCancelar() {
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
	
	public void addListerner(NegativarTituloListerner target){
		try {
			Method method = NegativarTituloListerner.class.getDeclaredMethod("onClose", NegativarTituloEvent.class);
			addListener(NegativarTituloEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(NegativarTituloEvent.class, target);
	}
	public static class NegativarTituloEvent extends Event{
		
		
		private String statusNegativado;

		private boolean confirm;
		
		public NegativarTituloEvent(Component source, boolean confirm, String statusNegativado) {
			super(source);
			
			this.confirm = confirm;
			this.statusNegativado = statusNegativado;			
			
		}
		
		public boolean isConfirm() {
			return confirm;
		}
		
		public String getStatusNegativado(){
			return statusNegativado;
		}
		
	}
	public interface NegativarTituloListerner extends Serializable{
		public void onClose(NegativarTituloEvent event);
	}
	

}
