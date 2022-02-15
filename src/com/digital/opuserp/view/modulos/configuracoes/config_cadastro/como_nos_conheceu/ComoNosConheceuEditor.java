package com.digital.opuserp.view.modulos.configuracoes.config_cadastro.como_nos_conheceu;



import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ComoNosConheceuEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	public ComoNosConheceuEditor(Item item, String title, boolean modal){
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
														
					TextField tfCod = new TextField ("Nome");				
					tfCod.setWidth("100%");				
					//tfCod.setStyleName("caption-align");
					tfCod.setNullRepresentation("");
					tfCod.setRequired(true);
					tfCod.setMaxLength(200);
					tfCod.focus();
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);	
					tfCod.setTextChangeEventMode(TextChangeEventMode.LAZY);
					tfCod.addBlurListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
							((TextField) (event.getComponent())).setValue(((TextField) (event.getComponent())).getValue().toUpperCase());
								}
						});
					
					fieldGroup.bind(tfCod,"nome");
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
							fireEvent(new ComoNosConheceuEvent(getUI(), item, true));					
							Notify.Show("Como Nos Conheceu Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
						}else{
							if(fieldGroup.isModified()){
								fieldGroup.commit();				
								fireEvent(new ComoNosConheceuEvent(getUI(), item, true));					
								Notify.Show("Como Nos Conheceu Alterado com Sucesso!", Notify.TYPE_SUCCESS);
							}
						}
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
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new ComoNosConheceuEvent(getUI(), item, false));
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
									fireEvent(new ComoNosConheceuEvent(getUI(), item, true));									
									if(item.getItemProperty("id").getValue() == null){
										
										Notify.Show("Categoria Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
									}else{								
										Notify.Show("Categoria Alterado com Sucesso!", Notify.TYPE_SUCCESS);
									}
									close();
								} catch (Exception e) {
									e.printStackTrace();
									Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new ComoNosConheceuEvent(getUI(), item, false));
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
	
	
	public void addListerner(ComoNosConheceuListerner target){
		try {
			Method method = ComoNosConheceuListerner.class.getDeclaredMethod("onClose", ComoNosConheceuEvent.class);
			addListener(ComoNosConheceuEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ComoNosConheceuListerner target){
		removeListener(ComoNosConheceuEvent.class, target);
	}
	public static class ComoNosConheceuEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public ComoNosConheceuEvent(Component source, Item item, boolean confirm) {
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
	public interface ComoNosConheceuListerner extends Serializable{
		public void onClose(ComoNosConheceuEvent event);
	}

	
}
