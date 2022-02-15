package com.digital.opuserp.view.modulos.configuracoes.config_cadastro.ramo_atv;



import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.LogErrorDAO;
import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraListerner;
import com.digital.opuserp.view.util.Notify;
import com.itextpdf.text.pdf.codec.GifImage;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.ShortCutConstants;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;

public class RamoAtivEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	public RamoAtivEditor(Item item, String title, boolean modal){
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
							fireEvent(new RamoAtivEvent(getUI(), item, true));					
							Notify.Show("Ramo de Atividade Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
						}else{
							if(fieldGroup.isModified()){
								fieldGroup.commit();				
								fireEvent(new RamoAtivEvent(getUI(), item, true));					
								Notify.Show("Ramo de Atividade Alterado com Sucesso!", Notify.TYPE_SUCCESS);
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
					fireEvent(new RamoAtivEvent(getUI(), item, false));
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
									fireEvent(new RamoAtivEvent(getUI(), item, true));									
									if(item.getItemProperty("id").getValue() == null){
										

										Notify.Show("Ramo de Atividade Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
									}else{								
										Notify.Show("Ramo de Atividade Alterado com Sucesso!", Notify.TYPE_SUCCESS);
									}
									close();
								} catch (Exception e) {
									System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
									
									Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new RamoAtivEvent(getUI(), item, false));
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
	
	
	public void addListerner(RamoAtivListerner target){
		try {
			Method method = RamoAtivListerner.class.getDeclaredMethod("onClose", RamoAtivEvent.class);
			addListener(RamoAtivEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(RamoAtivListerner target){
		removeListener(RamoAtivEvent.class, target);
	}
	public static class RamoAtivEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public RamoAtivEvent(Component source, Item item, boolean confirm) {
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
	public interface RamoAtivListerner extends Serializable{
		public void onClose(RamoAtivEvent event);
	}

	
}
