package com.digital.opuserp.view.modulos.configuracoes.config_financeiro.controle;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CepDAO;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.ceps.CepEditor.CepEvent;
import com.digital.opuserp.view.modulos.cadastro.ceps.CepEditor.CepListerner;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;

public class ControleEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	private String tipo;
	
	private String width_cep;
	private String width_uf;
	private String width_pais;

	private String width_bairro;
	private String width_cidade;
		
	public ControleEditor(Item item, String title, boolean modal){
		this.item = item;
		
		configLayout();
		
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
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		
		
		buildLayout();
	}
	
	
	
	private void configLayout(){
		setWidth("440px");
		//setHeight("134px");

		
		width_cep = "80px";
		width_uf = "40px";	
		width_pais = "147px";	
		
		width_bairro = "200px";
		width_cidade = "147px";	
	
	}	
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");						
								
				TextField txtNome = (TextField)fieldGroup.buildAndBind("Nome", "nome");				
						
				txtNome.setStyleName("caption-align");
				txtNome.setNullRepresentation("");
				txtNome.setMaxLength(40);
				txtNome.focus();
				txtNome.setRequired(true);
				txtNome.setTextChangeEventMode(TextChangeEventMode.LAZY);
				
				
				addComponent(txtNome);
								
			}
		});
		
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");						
								
				JPAContainer<ContaBancaria> containerConta = JPAContainerFactory.makeReadOnly(ContaBancaria.class, ConnUtil.getEntity());
				containerConta.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
				
				ComboBox cbContaBancaria = new ComboBox("Contrato Cobrança", containerConta);					
				cbContaBancaria.setStyleName("caption-align");
				cbContaBancaria.setNullSelectionAllowed(false);
				cbContaBancaria.setTextInputAllowed(false);
				cbContaBancaria.setRequired(true);
				cbContaBancaria.setItemCaptionPropertyId("nome");
				cbContaBancaria.setConverter(new SingleSelectConverter(cbContaBancaria));
				
				fieldGroup.bind(cbContaBancaria, "conta_bancaria");
				
				addComponent(cbContaBancaria);								
			}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					ComboBox cbRegistro = new ComboBox("Registro");					
					cbRegistro.setStyleName("caption-align");
					cbRegistro.setNullSelectionAllowed(false);
					cbRegistro.setTextInputAllowed(false);
					cbRegistro.setRequired(true);
					cbRegistro.addItem("SIM");
					cbRegistro.addItem("NAO");
										
					fieldGroup.bind(cbRegistro, "registro");					
					addComponent(cbRegistro);								
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
						if(item.getItemProperty("id").getValue()== null){
							fieldGroup.commit();				
							fireEvent(new ControleEvent(getUI(), item, true));							
							Notification.show("Controle Cadastrado com Sucesso!");
						}else{
							if(fieldGroup.isModified()){
								fieldGroup.commit();				
								fireEvent(new ControleEvent(getUI(), item, true));	
								Notification.show("Controle Alterado com Sucesso!");
							}
						}	
		
						close();
					} catch (CommitException e) {
						e.printStackTrace();
						System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
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
					fireEvent(new ControleEvent(getUI(), item, false));
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
									fireEvent(new ControleEvent(getUI(), item, true));
									
									if(item.getItemProperty("id").getValue() == null){
										Notification.show("Cep Cadastrado com Sucesso!");
									}else{								
										Notification.show("Cep Alterado com Sucesso!");
									}
									
									close();
								} catch (Exception e) {
									e.printStackTrace();
									System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
									Notification.show("Não foi Possivel Salvar as Alterações!");
								}
							  }else{
								  Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
							  }
							}else{							
								fieldGroup.discard();				
								fireEvent(new ControleEvent(getUI(), item, false));
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
	
	public void addListerner(ControleListerner target){
		try {
			Method method = ControleListerner.class.getDeclaredMethod("onClose", ControleEvent.class);
			addListener(ControleEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(ControleEvent.class, target);
	}
	public static class ControleEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public ControleEvent(Component source, Item item, boolean confirm) {
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
	public interface ControleListerner extends Serializable{
		public void onClose(ControleEvent event);
	}
	
	
}
