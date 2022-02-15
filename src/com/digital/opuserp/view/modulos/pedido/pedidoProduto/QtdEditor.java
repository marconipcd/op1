package com.digital.opuserp.view.modulos.pedido.pedidoProduto;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class QtdEditor extends Window implements GenericEditor {

	
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;

	VerticalLayout vlRoot;
	
	boolean fracionar;
	Float qtd;
	boolean allow = true;
	boolean bloquear_qtd = true;
	
	public QtdEditor(String title, boolean modal, boolean fracionar, Float qtd,boolean bloquear_qtd){
		
		this.fracionar = fracionar;
		this.qtd = qtd;
		this.bloquear_qtd = bloquear_qtd;
		
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
	
	
	private TextField txtQtd;
	public void buildLayout(){
		
		vlRoot.addComponent(new FormLayout(){
			{
				txtQtd = new TextField();
				txtQtd.setRequired(true);
				txtQtd.setWidth("175px");
				txtQtd.setHeight("150px");
				txtQtd.addStyleName("font-24");
				txtQtd.setId("txtQtd");
				txtQtd.setImmediate(true);
				
				txtQtd.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						if(qtd != null && !event.getText().equals("") && bloquear_qtd){
							if(new Float(event.getText().replace(",", ".")) > qtd){
								Notify.Show("Saldo Superior ao Disponível no Estoque", Notify.TYPE_ERROR);
								event.getComponent().addStyleName("invalid-txt");
								allow = false;
							}else{
								event.getComponent().removeStyleName("invalid-txt");
								allow = true;
							}
						}else{
							allow = true;
						}
					}
				});
				txtQtd.focus();
				
				addComponent(txtQtd);			
				
				if(!fracionar){
					JavaScript.getCurrent().execute("$('#txtQtd').mask('0000000000')");
				}else{
					JavaScript.getCurrent().execute("$('#txtQtd').maskMoney({decimal:',',thousands:''})");
				}

				//txtQtd.setValue("1");
				txtQtd.selectAll();
			}
		});
		
				
		
				
	}
	
	
	
	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("Confirmar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				Float qtd = new Float(0);
				
				try{
					if(txtQtd.getValue() != null && !txtQtd.getValue().equals("") && Real.formatStringToDBFloat(txtQtd.getValue()) > 0){
						qtd = Real.formatStringToDBFloat(txtQtd.getValue());
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				if(qtd > 0 && txtQtd.isValid() && allow){
					try {										
						
						fireEvent(new QtdEvent(getUI(),new Float(txtQtd.getValue().replace(",", ".")),true));						
						close();
					} catch (Exception e) {					
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
					}
				}else{
					
					
					
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

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!txtQtd.isModified()){
					fireEvent(new QtdEvent(getUI(),null, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(txtQtd.getValue() != null && txtQtd.isValid()){
									try {				
										
										
										fireEvent(new QtdEvent(getUI(), new Float(txtQtd.getValue()), true));						
										close();
									} catch (Exception e) {					
										e.printStackTrace();					
										Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
									}
								}else{
									
									
									
									Notify.Show_Invalid_Submit_Form();
									
								}
							}else{							
								fireEvent(new QtdEvent(getUI(), null, true));
								close();						
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
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
	
	
	public void addListerner(QtdListerner target){
		try {
			Method method = QtdListerner.class.getDeclaredMethod("onClose", QtdEvent.class);
			addListener(QtdEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(QtdListerner target){
		removeListener(QtdEvent.class, target);
	}
	public static class QtdEvent extends Event{
				
		private boolean confirm;
		
		Float qtd;
		public QtdEvent(Component source, Float qtd,  boolean confirm) {
			super(source);
		
			this.qtd= qtd;
			this.confirm = confirm;			
		}
		
		
		public Float getQtd(){
			return qtd;
		}
		
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface QtdListerner extends Serializable{
		public void onClose(QtdEvent event);
	}

	
}
