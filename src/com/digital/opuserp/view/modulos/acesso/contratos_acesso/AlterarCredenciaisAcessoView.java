	package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.SwithDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.RadAcct;
import com.digital.opuserp.domain.Swith;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.util.SignalStrengthUtil;
import com.digital.opuserp.util.Validator;
import com.digital.opuserp.util.mk_bean.WirelessInterface;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AlterarCredenciaisAcessoView extends Window implements GenericEditor{

	Item item;
	Button btSalvar; 
	Button btCancelar;
	Button btAtualizar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
		
	boolean valid_mac = false;
	boolean valid_ip = false;
	boolean valid_usuario = false;
	boolean valid_senha = false;
	boolean Validar_signal = false;
	boolean conectado = false;
	

	TextField tfConexao; 
	TextField tfComunicacao; 
	TextField tfEnderecoIPPool; 
	EntityManager em = ConnUtil.getEntity();
	Integer codAcesso;
	AcessoCliente ac;			
	Concentrador base; 
	
	Concentrador concentrador;
	ComboBox cbInterface;
	ComboBox cbConcentradores;
	Integer vez = 0;
	String ipAtual;
	
	String loginAtual;
	
	private ComboBox cbSwitch;
	
	public AlterarCredenciaisAcessoView(Item item, String title, boolean modal){
		this.item = item;
		
		if(item.getItemProperty("id").getValue()!=null){
			codAcesso = (Integer) item.getItemProperty("id").getValue();
			ac = em.find(AcessoCliente.class, codAcesso);				
			base = (Concentrador) item.getItemProperty("base").getValue();	
		}
		
		if (item.getItemProperty("endereco_ip").getValue() != null) {
			ipAtual = item.getItemProperty("endereco_ip").getValue().toString();
		}
				
		if(item.getItemProperty("login") != null && item.getItemProperty("login").getValue() != null){
			valid_usuario = true;
		}
		
		setWidth("683px");
		setHeight("242px");

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
		
		
		if (item.getItemProperty("login").getValue() != null) {
			loginAtual = item.getItemProperty("login").getValue().toString();
		}
		
		buildLayout();
	}
	
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		final Cliente cliente = (Cliente) item.getItemProperty("cliente").getValue();
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField tfCodContrato = new TextField("Contrato");
					tfCodContrato.setValue(item.getItemProperty("id").getValue().toString());
					tfCodContrato.setReadOnly(true);
					tfCodContrato.setWidth("62px");
					tfCodContrato.setStyleName("caption-align-contrato");
					
					addComponent(tfCodContrato);
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);

					TextField tfNomeCliente = new TextField("Cliente");
					tfNomeCliente.setValue(cliente.getNome_razao());
					tfNomeCliente.setReadOnly(true);
					tfNomeCliente.setWidth("360px");
					tfNomeCliente.setStyleName("caption-align-contrato");
					
					addComponent(tfNomeCliente);
				}
		});
		
					
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
						
					addComponent(fieldGroup.buildAndBind("Usuário", "login"));		
					fieldGroup.getField("login").setRequired(true);
					((TextField)fieldGroup.getField("login")).setNullRepresentation("");
					((TextField)fieldGroup.getField("login")).setStyleName("caption-align-contrato");
					((TextField)fieldGroup.getField("login")).setMaxLength(100);
					((TextField)fieldGroup.getField("login")).setDescription("Apenas letras minúscula");
					((TextField)fieldGroup.getField("login")).focus();
					((TextField)fieldGroup.getField("login")).setTextChangeEventMode(TextChangeEventMode.LAZY);
					((TextField)fieldGroup.getField("login")).setImmediate(true);
					((TextField)fieldGroup.getField("login")).addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							if(event.getText() != null && !event.getText().equals("")){
																
									if(!event.getText().equals(loginAtual)){
										
										CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();				
										
										Pattern pattern = Pattern.compile("[a-z-_0-9]{3,}");
										Matcher matcher = pattern.matcher(event.getText());
										if(caDAO.usernameDisponibility(event.getText()) && matcher.matches()){
											event.getComponent().removeStyleName("invalid-cpf");
											event.getComponent().addStyleName("valid-cpf");
											valid_usuario = true;
										}else{
											event.getComponent().removeStyleName("valid-cpf");
											event.getComponent().addStyleName("invalid-cpf");
											valid_usuario = false;
										}
									}else{									
										event.getComponent().removeStyleName("invalid-cpf");
										event.getComponent().addStyleName("valid-cpf");
										valid_usuario = true;
									}	
							}
						}
					});
				}
			});
			
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				addComponent(fieldGroup.buildAndBind("Senha", "senha"));
				fieldGroup.getField("senha").setRequired(true);
				((TextField)fieldGroup.getField("senha")).setNullRepresentation("");
				((TextField)fieldGroup.getField("senha")).setStyleName("caption-align-contrato");
				((TextField)fieldGroup.getField("senha")).setMaxLength(100);
				((TextField)fieldGroup.getField("senha")).setImmediate(true);
				((TextField)fieldGroup.getField("senha")).setTextChangeEventMode(TextChangeEventMode.LAZY);
				((TextField)fieldGroup.getField("senha")).addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						if(event.getText() != null && !event.getText().equals("")){

							 Pattern pattern = Pattern.compile("[A-Za-z0-9]{3,}");
						     Matcher matcher = pattern.matcher(event.getText());
							
							if(matcher.matches()){
								event.getComponent().removeStyleName("invalid-cpf");
								event.getComponent().addStyleName("valid-cpf");
								valid_senha = true;
							}else{
								event.getComponent().removeStyleName("valid-cpf");
								event.getComponent().addStyleName("invalid-cpf");
								valid_senha = false;
							}
						}						
					}
				});	
			}
		});
		
			
	}
	
	
	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				

				if(fieldGroup.isValid() && valid_usuario){
					try {												
						
						fieldGroup.commit();						
						fireEvent(new AlterarCredenciaisAcessoEvent(getUI(), item, true));	
						
					} catch (Exception e) {											
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);		
						e.printStackTrace();
					}				
				}else{
					
					for (Field<?> field: fieldGroup.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
						
						if(!cbInterface.isValid()){
							cbInterface.addStyleName("invalid-txt");
						}else{
							cbInterface.removeStyleName("invalid-txt");
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
					fireEvent(new AlterarCredenciaisAcessoEvent(getUI(), item, false));					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){						
								
								if(fieldGroup.isValid() && valid_usuario){
									try {											
										fieldGroup.commit();				
										fireEvent(new AlterarCredenciaisAcessoEvent(getUI(), item, true));		
										
									} catch (Exception e) {											
										Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);	
										e.printStackTrace();
									}
								}else{
									Notify.Show_Invalid_Submit_Form();
								}
								
							}else{							
								fieldGroup.discard();				
								fireEvent(new TransportadoraEvent(getUI(), item, false));								
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
	
	
	public void addListerner(AlterarCredenciaisAcessoListerner target){
		try {
			Method method = AlterarCredenciaisAcessoListerner.class.getDeclaredMethod("onClose", AlterarCredenciaisAcessoEvent.class);
			addListener(AlterarCredenciaisAcessoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AlterarCredenciaisAcessoListerner target){
		removeListener(AlterarCredenciaisAcessoEvent.class, target);
	}
	public static class AlterarCredenciaisAcessoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private Date dataPrimeiroBoleto;
		
		public AlterarCredenciaisAcessoEvent(Component source, Item item, boolean confirm) {
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
	public interface AlterarCredenciaisAcessoListerner extends Serializable{
		public void onClose(AlterarCredenciaisAcessoEvent event);
	}
}
