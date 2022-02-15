package com.digital.opuserp.view.modulos.relatorio.Pedido;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.RelatorioDAO;
import com.digital.opuserp.domain.RelatorioPre;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SalvarRelatorioPedidoEditor extends Window implements GenericEditor {

	
	Button btSalvar;
	Button btCancelar;
	
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	boolean validName = false;
	
	Integer codSubModulo;
	
	
	public SalvarRelatorioPedidoEditor(Integer codSubModulo, String title, boolean modal){
		
		this.codSubModulo = codSubModulo;
		
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
	
	TextField txtNome;
	
	public void buildLayout(){
		
					
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtNome = new TextField ("Nome");				
					txtNome.setWidth("273px");				
					txtNome.setNullRepresentation("");
					txtNome.setRequired(true);
					txtNome.setMaxLength(200);
					txtNome.focus();
					txtNome.setImmediate(true);
					txtNome.setTextChangeEventMode(TextChangeEventMode.LAZY);
					txtNome.addListener(new FieldEvents.TextChangeListener() {
						
						public void textChange(TextChangeEvent event) {
							
							RelatorioDAO rDAO = new RelatorioDAO();
							 							
							if(rDAO.validarNome(event.getText(), codSubModulo)==true){
								validName = true;
								event.getComponent().setStyleName("valid");
							}else{
								event.getComponent().setStyleName("invalid");
								validName = false;								
							}
						}
					});
							
					addComponent(txtNome);
					setExpandRatio(txtNome, 2);	
				
					
				}
			});
	
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(txtNome.isValid()&& validName){
							
					fireEvent(new SalvarRelatorioPedidoEvent(getUI(),true, txtNome.getValue().toUpperCase()));
					close();
										
				}else if(validName==false){
					Notification.show("Não é Possivel Salvar,O Nome do relatório já existe!");					
				}else{
					
											
					if(!txtNome.isValid()){
						txtNome.addStyleName("invalid-txt");
					}else{
						txtNome.removeStyleName("invalid-txt");
					}
					Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
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
				
				fireEvent(new SalvarRelatorioPedidoEvent(getUI(),false, ""));
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
	
	
	public void addListerner(SalvarRelatorioPedidoListerner target){
		try {
			Method method = SalvarRelatorioPedidoListerner.class.getDeclaredMethod("onClose", SalvarRelatorioPedidoEvent.class);
			addListener(SalvarRelatorioPedidoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(SalvarRelatorioPedidoListerner target){
		removeListener(SalvarRelatorioPedidoEvent.class, target);
	}
	public static class SalvarRelatorioPedidoEvent extends Event{
		
		
		private boolean confirm;
		private String nome;
		
		public SalvarRelatorioPedidoEvent(Component source, boolean confirm, String nome) {
			super(source);
			this.confirm = confirm;			
			this.nome = nome;
		}

		public String getNome(){
			return nome;
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface SalvarRelatorioPedidoListerner extends Serializable{
		public void onClose(SalvarRelatorioPedidoEvent event);
	}

	
}

