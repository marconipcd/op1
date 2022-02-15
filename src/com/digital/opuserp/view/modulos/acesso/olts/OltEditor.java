package com.digital.opuserp.view.modulos.acesso.olts;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class OltEditor extends Window {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
		
	public OltEditor(Item item, String title, boolean modal){
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
	
	private void configLayout(){		
		setWidth("862px");
		setHeight("460px");			
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField txtIdentificacao = (TextField)fieldGroup.buildAndBind("Identificação", "identificacao");				
					txtIdentificacao.setWidth("200px");				
					txtIdentificacao.setStyleName("caption-align-concentrador");
					txtIdentificacao.setNullRepresentation("");
					txtIdentificacao.focus();
					txtIdentificacao.setMaxLength(100);
					txtIdentificacao.setRequired(true);
					
					addComponent(txtIdentificacao);
					setExpandRatio(txtIdentificacao, 2);						
				}
		});
		
		vlRoot.addComponent(	new FormLayout(){					
			{
				
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");						
								
				TextField txtEnderecoIp = (TextField)fieldGroup.buildAndBind("Endereço IP", "endereco_ip");				
				txtEnderecoIp.setWidth("200px");				
				txtEnderecoIp.setStyleName("caption-align-concentrador");
				txtEnderecoIp.setNullRepresentation("");				
				txtEnderecoIp.setMaxLength(100);
				txtEnderecoIp.setRequired(true);
				
				addComponent(txtEnderecoIp);
				setExpandRatio(txtEnderecoIp, 2);						
			}
		});
		
		vlRoot.addComponent(	new FormLayout(){
			{			
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				ComboBox cbSituacao = new ComboBox("Situação");
				cbSituacao.setRequired(true); 
				cbSituacao.addItem("NORMAL");
				cbSituacao.addItem("MANUTENCAO");
				cbSituacao.addItem("PROBLEMA");
				cbSituacao.setNullSelectionAllowed(false);
				cbSituacao.setStyleName("caption-align-concentrador");
				cbSituacao.setTextInputAllowed(false);
				
				addComponent(cbSituacao);
				fieldGroup.bind(cbSituacao, "situacao");
						
			}
		});
		
		vlRoot.addComponent(	new FormLayout(){
			{
			
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				ComboBox cbStatus = new ComboBox("Status");
				cbStatus.setRequired(true);
				cbStatus.addItem("ATIVO");
				cbStatus.addItem("INATIVO");
				cbStatus.setNullSelectionAllowed(false);
				cbStatus.setStyleName("caption-align-concentrador");
				cbStatus.setTextInputAllowed(false);
				
				addComponent(cbStatus);
				fieldGroup.bind(cbStatus, "status");
						
			}
		});		
	}

	
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid()){
					try {
						fieldGroup.commit();				
						fireEvent(new OltEvent(getUI(), item, true));
						
						if (item.getItemProperty("id").getValue() != null) {
							Notify.Show("Concentrador Alterado com Sucesso!", Notify.TYPE_SUCCESS);
						}
						
					} catch (CommitException e) {						
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
					}
					close();
				}else{					
					Notify.Show_Invalid_Submit_Form();
				}
				
			}
		});
		btSalvar.setStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("Salvar", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);
		return btSalvar;
	}

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new OltEvent(getUI(), item, false));
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
											fireEvent(new OltEvent(getUI(), item, true));
											
											if (item.getItemProperty("id").getValue() != null) {
												Notify.Show("Concentrador Alterado com Sucesso!", Notify.TYPE_SUCCESS);
											}else{
												Notify.Show("Concentrador Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
											}
											
											close();
										} catch (Exception e) {
											e.printStackTrace();
											Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
										}
									}else{										
										Notify.Show_Invalid_Submit_Form();
									}
								}else{							
									fieldGroup.discard();				
									fireEvent(new OltEvent(getUI(), item, false));
									close();													
								}
							
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
			}
		});
		
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}
	
	
	public void addListerner(OltListerner target){
		try {
			Method method = OltListerner.class.getDeclaredMethod("onClose", OltEvent.class);
			addListener(OltEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(OltListerner target){
		removeListener(OltEvent.class, target);
	}
	public static class OltEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public OltEvent(Component source, Item item, boolean confirm) {
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
	public interface OltListerner extends Serializable{
		public void onClose(OltEvent event);
	}
	
}
