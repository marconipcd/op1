package com.digital.opuserp.view.modulos.configuracoes.config_cadastro.cnae;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CnaeEditor extends Window implements GenericEditor {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	public CnaeEditor(Item item, String title, boolean modal){
		this.item = item;
		
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-fRamoAtivEditororm");
		
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
														
					TextField tfCod = new TextField ("Código");				
					tfCod.setWidth("100%");				
					tfCod.setNullRepresentation("");
					tfCod.setRequired(true);
					tfCod.setMaxLength(200);
					tfCod.focus();
					tfCod.setStyleName("caption-align-cep");
					addComponent(tfCod);					
					
					fieldGroup.bind(tfCod,"codigo");
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField tfDescricao = new TextField ("Descrição");				
					tfDescricao.setWidth("350px");				
					tfDescricao.setNullRepresentation("");
					tfDescricao.setRequired(true);
					tfDescricao.setMaxLength(200);					
					tfDescricao.setStyleName("caption-align-cep");
					addComponent(tfDescricao);					
					
					fieldGroup.bind(tfDescricao,"descricao");
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
							fireEvent(new CnaeEvent(getUI(), item, true));					
							Notify.Show("Cnae Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
						}else{
							if(fieldGroup.isModified()){
								fieldGroup.commit();				
								fireEvent(new CnaeEvent(getUI(), item, true));					
							
								Notify.Show("Cnae Alterado com Sucesso!", Notify.TYPE_SUCCESS);
							}
						}
						close();
					} catch (Exception e) {					
						e.printStackTrace();						
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_SUCCESS);
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
					fireEvent(new CnaeEvent(getUI(), item, false));
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
									fireEvent(new CnaeEvent(getUI(), item, true));									
									if(item.getItemProperty("id").getValue() == null){										
										Notify.Show("Cnae Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
									}else{								
										Notify.Show("Cnae Alterado com Sucesso!", Notify.TYPE_SUCCESS);
									}
									close();
								} catch (Exception e) {
									e.printStackTrace();
									Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new CnaeEvent(getUI(), item, false));
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
	
	
	public void addListerner(CnaeListerner target){
		try {
			Method method = CnaeListerner.class.getDeclaredMethod("onClose", CnaeEvent.class);
			addListener(CnaeEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CnaeListerner target){
		removeListener(CnaeEvent.class, target);
	}
	public static class CnaeEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public CnaeEvent(Component source, Item item, boolean confirm) {
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
	public interface CnaeListerner extends Serializable{
		public void onClose(CnaeEvent event);
	}


}
