package com.digital.opuserp.view.modulos.configuracoes.config_crm.assunto;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AssuntosEditor extends Window {

	Button btCancelar;
	Button btSalvar;
	Item item;
	String tipo;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	String setorCadastrado;
	String setorAtual;
	
	public AssuntosEditor(Item item, String titulo){
		tipo = titulo;
		this.item = item;
		center();
		setModal(true);
		setCaption(titulo);
		setClosable(false);
		setResizable(false);
		
		btSalvar = buildBtSalvar();
		btCancelar = buildBtCancelar();
		
		vlRoot = new VerticalLayout();
		vlRoot.setWidth("100%");
		vlRoot.addStyleName("border-form");
		vlRoot.setMargin(true);
		
		
		
		
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
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				final TextField tfNome = (TextField)fieldGroup.buildAndBind("Nome", "nome");
				tfNome.setRequired(true);
				tfNome.setNullRepresentation("");
				tfNome.setWidth("300px");
				tfNome.focus();
				tfNome.setImmediate(true);
				tfNome.setTextChangeEventMode(TextChangeEventMode.LAZY);
				tfNome.setStyleName("caption-align");
				
								
				addComponent(tfNome);
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				JPAContainer<Setores> containerSetor = JPAContainerFactory.makeReadOnly(Setores.class, ConnUtil.getEntity());
				containerSetor.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
				
				ComboBox cbSetor = new ComboBox("Setor", containerSetor);	
				cbSetor.setRequired(true); 
				cbSetor.setId("txtSetor");
				cbSetor.addStyleName("txtSetor");
				cbSetor.setStyleName("caption-align");
				cbSetor.setNullSelectionAllowed(false);
				cbSetor.setTextInputAllowed(false); 
				cbSetor.setItemCaptionPropertyId("nome");
				cbSetor.setConverter(new SingleSelectConverter(cbSetor));
				cbSetor.setWidth("173px");
				
				
				addComponent(cbSetor);	
				fieldGroup.bind(cbSetor, "setor");				
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				final TextArea tfConteudo = new TextArea("Conteudo");;
				tfConteudo.setRequired(true);	
				tfConteudo.setNullRepresentation("");
				tfConteudo.setWidth("538");
				tfConteudo.setHeight("111px");
				tfConteudo.addStyleName("uppercase");
				tfConteudo.addStyleName("font-courier-new");
//				tfConteudo.setImmediate(true);
//				tfConteudo.setTextChangeEventMode(TextChangeEventMode.LAZY);
				tfConteudo.setStyleName("caption-align");
						
				addComponent(tfConteudo);
				fieldGroup.bind(tfConteudo,"conteudo");
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				CheckBox ckbMostrarNaCentral = new CheckBox("Mostrar na Central ?");
				ckbMostrarNaCentral.setStyleName("caption-align");
								
				addComponent(ckbMostrarNaCentral);
				fieldGroup.bind(ckbMostrarNaCentral, "mostrar_central");
			}
		});
		
		
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				ComboBox cbPrioridade = new ComboBox("Prioridade");	
				cbPrioridade.setRequired(true); 
				cbPrioridade.setStyleName("caption-align");
				cbPrioridade.setNullSelectionAllowed(false);
				cbPrioridade.setTextInputAllowed(false); 
				cbPrioridade.addItem("ALTA");
				cbPrioridade.addItem("MEDIA");
				cbPrioridade.addItem("BAIXA");
											
				addComponent(cbPrioridade);
				fieldGroup.bind(cbPrioridade, "prioridade");
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				ComboBox cbStatus = new ComboBox("Status");	
				cbStatus.setRequired(true); 
				cbStatus.setStyleName("caption-align");
				cbStatus.setNullSelectionAllowed(false);
				cbStatus.setTextInputAllowed(false); 
				cbStatus.addItem("ATIVO");
				cbStatus.addItem("INATIVO");
				
				
								
				addComponent(cbStatus);
				fieldGroup.bind(cbStatus, "status");
			}
		});
	}
	
	private Button buildBtSalvar(){
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(fieldGroup.isValid()){
					try {

							fieldGroup.commit();
							fireEvent(new CRmAssuntoSavedEvent(getUI(), item, true));
							Notify.Show("CRM Assunto Gravado com Sucesso!", Notify.TYPE_SUCCESS);
						
		
						close();
					} catch (CommitException e) {
						e.printStackTrace();
						System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_SUCCESS);
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
		
		
		btSalvar.addStyleName("default");
		return btSalvar;
	}
	
	private Button buildBtCancelar(){
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isModified()){
					
					final GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							try {
								if(event.isConfirm()){
									
									if(fieldGroup.isValid()){
																														
										fieldGroup.commit();
										fireEvent(new CRmAssuntoSavedEvent(getUI(), item, true));							
										

										Notify.Show("CRM Assunto Gravado com Sucesso!!", Notify.TYPE_SUCCESS);
										
										gDialog.close();
										close();
										
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
									gDialog.close();
									close();
								}
							} catch (Exception e) {
								e.printStackTrace();
								Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
							}
							
						}
					});
					getUI().addWindow(gDialog);
				}else{
					close();
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
	
	
	public void addListener(CRmAssuntoSavedListener listener){
		try {
			Method method = CRmAssuntoSavedListener.class.getDeclaredMethod("setoresSaved", 
					new Class[]{CRmAssuntoSavedEvent.class});
			addListener(CRmAssuntoSavedEvent.class, listener, method);
		} catch (Exception e) {
			System.out.println("O método não foi encontrado: "+e.getMessage());
		}
	}
	
	public void removeListener(CRmAssuntoSavedListener listener){
		removeListener(CRmAssuntoSavedEvent.class, listener);
	}
	
	public static class CRmAssuntoSavedEvent extends Event{
		
		private Item eventoItem;
		private boolean confirm;
		
		public CRmAssuntoSavedEvent(Component source, Item eventoItem, boolean confirm) {
			super(source);
			this.eventoItem = eventoItem;
			this.confirm = confirm;
		}
		
		public Item getEventItem(){
			return eventoItem;
		}
		public boolean isConfirm() {
			return confirm;
		}
		
	}
	
	public interface CRmAssuntoSavedListener extends Serializable{
		public void setoresSaved(CRmAssuntoSavedEvent event);
	}	

}
