package com.digital.opuserp.view.modulos.acesso.central_assinantes;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import com.digital.opuserp.dao.CentralAssinantesDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.domain.CentralAssinante;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.util.EmailUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
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

public class DefinirCredenciaisEditor  extends Window{
	
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
	
	EntityManager em;
	Integer codCliente;
	
	CentralAssinante c;
	Cliente cliente;
	
	private TextField txtEmail;
	private TextField txtSenha;
	String emailInicial;
	
	boolean emailValid = false;
	
	public DefinirCredenciaisEditor(Integer codCliente, String title, boolean modal){
				
		this.codCliente = codCliente;
		this.c = CentralAssinantesDAO.findByCodCliente(codCliente);
		this.cliente = ClienteDAO.find(codCliente);
		
		if(cliente.getEmail()!=null && !cliente.getEmail().equals("")){
			emailInicial = cliente.getEmail();
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
		
		if(c == null){
			c = new CentralAssinante();
			c.setCliente_id(codCliente);			
			c.setSenha("");
		}
					
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtEmail = new TextField ("E-Mail");				
					txtEmail.setWidth("280px");					
					txtEmail.setRequired(true);		
//					txtEmail.setStyleName("caption-align");
					txtEmail.focus();
					txtEmail.setValue(cliente.getEmail());
					txtEmail.addStyleName("lowercase");
					txtEmail.setImmediate(true);
					txtEmail.setTextChangeEventMode(TextChangeEventMode.LAZY);
					txtEmail.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							if(event.getText() != null && !event.getText().isEmpty() && EmailUtil.validate(event.getText()) && !event.getText().equals(emailInicial)){
								
								if(!ClienteDAO.checkEmailExists(event.getText()) || event.getText().equals(emailInicial)){																
									txtEmail.removeStyleName("invalid-txt");
									emailValid = true;																
								}else{
									txtEmail.addStyleName("invalid-txt");
									emailValid = false;
								}
							}else{
								if(event.getText() != null && !event.getText().isEmpty() && !event.getText().equals(emailInicial)){
									emailValid = false;
									txtEmail.addStyleName("invalid-txt");
								}else{
									emailValid = true;
									txtEmail.removeStyleName("invalid-txt");
								}
							}
						}
					});
					
					addComponent(txtEmail);
									
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtSenha = new TextField ("Senha");				
					txtSenha.setWidth("280px");					
					txtSenha.setRequired(true);					
//					txtSenha.setStyleName("caption-align");
					txtSenha.setValue(c.getSenha());
					addComponent(txtSenha);			
					
				}
			});
		
		
	}

	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				String email = txtEmail.getValue();
				if(email != null && !email.isEmpty() && EmailUtil.validate(email) && !email.equals(emailInicial)){
					
					if(!ClienteDAO.checkEmailExists(email)){																
						txtEmail.removeStyleName("invalid-txt");
						emailValid = true;																
					}else{
						txtEmail.addStyleName("invalid-txt");
						emailValid = false;
					}
				}else{
					if(email != null && !email.isEmpty() && !email.equals(emailInicial)){
						emailValid = false;
						txtEmail.addStyleName("invalid-txt");
					}else{
						emailValid = true;
						txtEmail.removeStyleName("invalid-txt");
					}
				}
				
				if(!txtEmail.getValue().isEmpty() && !txtSenha.getValue().isEmpty() && emailValid){
					
					try {										
							
						cliente.setEmail(txtEmail.getValue());
						c.setSenha(txtSenha.getValue());
						CentralAssinantesDAO.save(c);
						ClienteDAO.saveCliente(cliente);
												
						fireEvent(new DefinirCredenciaisEvent(getUI(),true));					
												
						close();
					} catch (Exception e) {					
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
					}
				}else{
					
					if(!emailValid){
						txtEmail.addStyleName("invalid-txt");
					}else{
						txtEmail.removeStyleName("invalid-txt");
					}
					
					Notify.Show_Invalid_Submit_Form();
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

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
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
	
	
	public void addListerner(DefinirCredenciaisListerner target){
		try {
			Method method = DefinirCredenciaisListerner.class.getDeclaredMethod("onClose", DefinirCredenciaisEvent.class);
			addListener(DefinirCredenciaisEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(DefinirCredenciaisListerner target){
		removeListener(DefinirCredenciaisEvent.class, target);
	}
	public static class DefinirCredenciaisEvent extends Event{

		private boolean confirm;
		
		public DefinirCredenciaisEvent(Component source, boolean confirm) {
			super(source);			
			this.confirm = confirm;			
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface DefinirCredenciaisListerner extends Serializable{
		public void onClose(DefinirCredenciaisEvent event);
	}

	
}
