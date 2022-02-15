package com.digital.opuserp.view.modulos.acesso.central_assinantes;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ListasEditor  extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TextField txtNome;
	
	public ListasEditor(Item item, String title, boolean modal){
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
														
					txtNome = new TextField ("Nome");				
					txtNome.setWidth("100%");				
					//tfCod.setStyleName("caption-align");
					txtNome.setNullRepresentation("");
					txtNome.setRequired(true);
					txtNome.setMaxLength(200);
					txtNome.focus();
					addComponent(txtNome);
					setExpandRatio(txtNome, 2);	
					txtNome.setTextChangeEventMode(TextChangeEventMode.LAZY);
					txtNome.addBlurListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
							
							if(txtNome.getValue()!=null && txtNome.getValue().equals("")){
								((TextField) (event.getComponent())).setValue(event.getComponent().toString().toUpperCase());								
							}
								}
						});
					
					fieldGroup.bind(txtNome,"nome");
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					ComboBox cbStatus = new ComboBox("Status");
					cbStatus.setNullSelectionAllowed(false); 
					cbStatus.addItem("ATIVO");
					cbStatus.addItem("INATIVO");
					cbStatus.setRequired(true);				
					
					addComponent(cbStatus);
								
					fieldGroup.bind(cbStatus,"status");
					
					if(item.getItemProperty("status") == null){
						cbStatus.select("ATIVO");
					}
					
				}
			});
		
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid()){
					try {				
						if(item.getItemProperty("id").getValue() == null){
							fieldGroup.commit();				
							fireEvent(new ListaEvent(getUI(), item, true));					
							Notification.show("Lista Cadastrada com Sucesso!");
						}else{
							if(fieldGroup.isModified()){
								fieldGroup.commit();				
								fireEvent(new ListaEvent(getUI(), item, true));					
								Notification.show("Lista Alterada com Sucesso!");
							}
						}
						close();
					} catch (Exception e) {					
						//LogErrorDAO.add(new LogError(null, "CategoriaEditor.java", "buildBtSalvar()", e.toString(),null,OpusERP4UI.EMPRESA_SELECIONADA, OpusERP4UI.USUARIO_LOGADO));					
						Notification.show("Não foi Possivel Salvar as Alterações!");
					}
				}else{
					
					for (Field<?> field: fieldGroup.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
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
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new ListaEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								try {
									fieldGroup.commit();				
									fireEvent(new ListaEvent(getUI(), item, true));									
									if(item.getItemProperty("id").getValue() == null){
										
										Notification.show("Categoria Cadastrado com Sucesso!");
									}else{								
										Notification.show("Categoria Alterado com Sucesso!");
									}
									close();
								} catch (Exception e) {
									System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
									Notification.show("Não foi Possivel Salvar as Alterações!");
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new TransportadoraEvent(getUI(), item, false));
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
	
	
	public void addListerner(ListaListerner target){
		try {
			Method method = ListaListerner.class.getDeclaredMethod("onClose", ListaEvent.class);
			addListener(ListaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ListaListerner target){
		removeListener(ListaEvent.class, target);
	}
	public static class ListaEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public ListaEvent(Component source, Item item, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;			
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ListaListerner extends Serializable{
		public void onClose(ListaEvent event);
	}

	
}
