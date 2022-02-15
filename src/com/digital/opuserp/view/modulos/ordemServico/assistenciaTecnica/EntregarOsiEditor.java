package com.digital.opuserp.view.modulos.ordemServico.assistenciaTecnica;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class EntregarOsiEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;	
	
	ComboBox cbProblema; 
	
	public EntregarOsiEditor(Item item, String title, boolean modal){
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

	
	
	public String getNextId() {
		CredenciaisAcessoDAO caDao = new CredenciaisAcessoDAO();
		return caDao.getNextID();
	}
	
	
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField txtCodOs = new TextField("OS Número");		
					txtCodOs.setStyleName("caption-align");
					txtCodOs.setNullRepresentation("");				
					txtCodOs.setRequired(true);
					
									
					addComponent(txtCodOs);
					fieldGroup.bind(txtCodOs, "id");
					txtCodOs.setReadOnly(true);
									
				}
		});	
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField txtValorOS = new TextField("Valor Boleto");
					txtValorOS.setStyleName("caption-align");
					txtValorOS.setNullRepresentation("");				
					txtValorOS.setRequired(true);
					txtValorOS.setId("txtValorOs");
					txtValorOS.addStyleName("align-currency");
					JavaScript.getCurrent().execute("$('#txtValorOs').maskMoney({decimal:',',thousands:'.'})");
									
					addComponent(txtValorOS);
					fieldGroup.bind(txtValorOS, "valor");
					txtValorOS.setReadOnly(true);
					
									
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
						
						fieldGroup.commit();		
						item.getItemProperty("status").setValue("FECHADO");						
												
						fireEvent(new EntregarOsiEvent(getUI(), item, true));
														
						

					} catch (Exception e) {									
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
					}
				}else{
					
					    for (Field<?> field: fieldGroup.getFields()) {						
							if(!field.isValid()){
								field.addStyleName("invalid-txt");
							}else{
								field.removeStyleName("invalid-txt");
							}
					    }
					    
					    if(!cbProblema.isValid()){
					    	cbProblema.addStyleName("invalid-txt");
					    }else{
					    	cbProblema.removeStyleName("invalid-txt");  	
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
		//btSalvar.setEnabled(false);
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new EntregarOsiEvent(getUI(), item, false));
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
									
									
									
									if(fieldGroup.isValid()){							
										fieldGroup.commit();				
										fireEvent(new EntregarOsiEvent(getUI(), item, true));
																			
									}
								
								
								} catch (Exception e) {									
									e.printStackTrace();
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
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
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
	
	
	public void addListerner(EntregarOsiListerner target){
		try {
			Method method = EntregarOsiListerner.class.getDeclaredMethod("onClose", EntregarOsiEvent.class);
			addListener(EntregarOsiEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(EntregarOsiListerner target){
		removeListener(EntregarOsiEvent.class, target);
	}
	public static class EntregarOsiEvent extends Event{
		
		private Item item;
		private boolean confirm;		
		
		public EntregarOsiEvent(Component source, Item item, boolean confirm) {
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
	public interface EntregarOsiListerner extends Serializable{
		public void onClose(EntregarOsiEvent event);
	}
	
	
	




}

