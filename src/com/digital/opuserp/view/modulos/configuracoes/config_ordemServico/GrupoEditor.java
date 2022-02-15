package com.digital.opuserp.view.modulos.configuracoes.config_ordemServico;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.CrmFormasContato;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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

public class GrupoEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	public GrupoEditor(Item item, String title, boolean modal){
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
					
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtNomeGrupo = new TextField ("Nome");	
					txtNomeGrupo.setStyleName("caption-align");
					txtNomeGrupo.setWidth("100%");				
					txtNomeGrupo.setNullRepresentation("");
					txtNomeGrupo.setRequired(true);
					txtNomeGrupo.setMaxLength(200);
					txtNomeGrupo.focus();
					
					addComponent(txtNomeGrupo);					
					fieldGroup.bind(txtNomeGrupo,"nome");
				}
			});
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					ComboBox cbStatus = new ComboBox("Status");
					cbStatus.setNullSelectionAllowed(false);
					cbStatus.setRequired(true);
					cbStatus.addItem("ATIVO");
					cbStatus.addItem("INATIVO");
					cbStatus.setStyleName("caption-align");
					
					
					addComponent(cbStatus);					
					fieldGroup.bind(cbStatus,"status");
				}
			});
		
		

		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					ComboBox cbTipoBaixa = new ComboBox("Tipo Baixa");	
					cbTipoBaixa.setId("txtSetor");
					cbTipoBaixa.addStyleName("txtSetor");
					cbTipoBaixa.setStyleName("caption-align");
					cbTipoBaixa.setNullSelectionAllowed(false);
					cbTipoBaixa.setTextInputAllowed(false);									
					
					cbTipoBaixa.addItem("NORMAL");
					cbTipoBaixa.addItem("ESTORNO");
					
					
					addComponent(cbTipoBaixa);	
					fieldGroup.bind(cbTipoBaixa, "baixa_material");
					
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
						fireEvent(new GrupoEvent(getUI(), item, true));					
												
						close();
					} catch (Exception e) {					
						e.printStackTrace();
						Notify.Show(e.getLocalizedMessage(), Notify.TYPE_ERROR);
					}
				}else{
					
					for (Field<?> field: fieldGroup.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
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

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new GrupoEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(fieldGroup.isValid()){
									try {				
										fieldGroup.commit();				
										fireEvent(new GrupoEvent(getUI(), item, true));					
										Notify.Show("Categoria Cadastrada com Sucesso!", Notify.TYPE_SUCCESS);
										
										close();
									} catch (Exception e) {					
										e.printStackTrace();
										Notify.Show(e.getLocalizedMessage(), Notify.TYPE_ERROR);
									}
								}else{
									for (Field<?> field: fieldGroup.getFields()) {
										
										if(!field.isValid()){
											field.addStyleName("invalid-txt");
										}else{
											field.removeStyleName("invalid-txt");
										}
									}
									
									Notify.Show_Invalid_Submit_Form();	
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new GrupoEvent(getUI(), item, false));
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
	
	
	public void addListerner(GrupoListerner target){
		try {
			Method method = GrupoListerner.class.getDeclaredMethod("onClose", GrupoEvent.class);
			addListener(GrupoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(GrupoListerner target){
		removeListener(GrupoEvent.class, target);
	}
	public static class GrupoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public GrupoEvent(Component source, Item item, boolean confirm) {
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
	public interface GrupoListerner extends Serializable{
		public void onClose(GrupoEvent event);
	}

	
}