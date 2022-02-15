package com.digital.opuserp.view.modulos.cadastro.clientes;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.EmailUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
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
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ClienteEdicaoRapida extends Window implements GenericEditor{
	
	Item ClienteItem;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	ComboBox boxMarca;
	TextField tfModelo;
	
	VerticalLayout hlTeste;
	
	Integer codFornecedorDefault = 0;
	boolean cnpjCpfValid;
	
	String tipoVeiculo = "";
	
	ShortcutListener sl_esc;

	Cliente c;
	TextField txtUrlLocalizacao;
	
	
	public ClienteEdicaoRapida(Item ClienteItem, String title, boolean modal, Integer codSubModulo){
		this.ClienteItem = ClienteItem;
		BeanItem<Cliente> eiCliente = (BeanItem<Cliente>) ClienteItem;
		c = eiCliente.getBean();
		
		if(this.ClienteItem.getItemProperty("email").getValue() != null){
			emailInicial = this.ClienteItem.getItemProperty("email").getValue().toString();
		}
		
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
		
		sl_esc = buildShortCurListerner();
		
		buildLayout();
	
	}
	
	private ShortcutListener buildShortCurListerner(){
		 sl_esc = new ShortcutListener("Cancelar",
	                ShortcutAction.KeyCode.ENTER, null) {
	            @Override
	            public void handleAction(Object sender, Object target) {
	                btCancelar.click();
	            }
	        };
	        
	     return sl_esc;
	}

	boolean emailValid = false;
	String emailInicial;
	public void buildLayout(){	
		
		
		fieldGroup = new FieldGroup(ClienteItem);	
		vlRoot.addComponent(new VerticalLayout() {
			{
				
				// Telefone 1,2
				addComponent(new HorizontalLayout() {
					{
						addComponent(new FormLayout() {
							{
								setStyleName("form-cutom");
								TextField tfDDD_fone1 = (TextField) fieldGroup.buildAndBind("Telefone Principal","ddd_fone1");
								tfDDD_fone1.setNullRepresentation("");
								tfDDD_fone1.setStyleName("telefone");
								tfDDD_fone1.setRequired(true);
								tfDDD_fone1.setMaxLength(2);
								tfDDD_fone1.setWidth("40px");

								addComponent(tfDDD_fone1);
							}
						});

						addComponent(new FormLayout() {
							{
								// setMargin(true);
								// setSpacing(true);
								setStyleName("form-cutom_hide_require");
								addComponent(fieldGroup.buildAndBind("","telefone1"));
								((TextField) fieldGroup.getField("telefone1")).setNullRepresentation("");											
								((TextField) fieldGroup.getField("telefone1")).setWidth("105px");
								((TextField) fieldGroup.getField("telefone1")).setMaxLength(11);
								((TextField) fieldGroup.getField("telefone1")).setRequired(true);
								
								((TextField) fieldGroup.getField("telefone1")).setId("txtTelefone1");												
								JavaScript.getCurrent().execute("$('#txtTelefone1').mask('000000000')");
							}
						});

						addComponent(new FormLayout() {
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");

								TextField tfDDD_fone2 = (TextField) fieldGroup.buildAndBind("Telefone Alternativo","ddd_fone2");
								tfDDD_fone2.setNullRepresentation("");
								tfDDD_fone2.setWidth("40px");
								tfDDD_fone2.setMaxLength(2);

								addComponent(tfDDD_fone2);
							}
						});

						addComponent(new FormLayout() {
							{
								// setMargin(true);
								// setSpacing(true);
								setStyleName("form-cutom");
								addComponent(fieldGroup.buildAndBind("","telefone2"));
								((TextField) fieldGroup.getField("telefone2")).setNullRepresentation("");
								((TextField) fieldGroup.getField("telefone2")).setWidth("105px");
								((TextField) fieldGroup.getField("telefone2")).setMaxLength(11);
								
								((TextField) fieldGroup.getField("telefone2")).setId("txtTelefone2");												
								JavaScript.getCurrent().execute("$('#txtTelefone2').mask('000000000')");

							}
						});

					}
				});

				// //Telefone 3,4

				addComponent(new HorizontalLayout() {
					{
						addComponent(new FormLayout() {
							{
								setStyleName("form-cutom");
								TextField tfDDD_cel1 = (TextField) fieldGroup.buildAndBind("Telefone Alternativo","ddd_cel1");
								tfDDD_cel1.setNullRepresentation("");
								tfDDD_cel1.setStyleName("telefone2");
								tfDDD_cel1.setWidth("40px");
								tfDDD_cel1.setMaxLength(2);

								addComponent(tfDDD_cel1);
							}
						});

						addComponent(new FormLayout() {
							{
								// setMargin(true);
								// setSpacing(true);
								setStyleName("form-cutom");
								addComponent(fieldGroup.buildAndBind("","celular1"));
								((TextField) fieldGroup.getField("celular1")).setNullRepresentation("");
								((TextField) fieldGroup.getField("celular1")).setWidth("105px");
								((TextField) fieldGroup.getField("celular1")).setMaxLength(11);
								
								((TextField) fieldGroup.getField("celular1")).setId("txtCelular1");												
								JavaScript.getCurrent().execute("$('#txtCelular1').mask('000000000')");
							}
						});

						addComponent(new FormLayout() {
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								TextField tfDDD_cel2 = (TextField) fieldGroup.buildAndBind("Telefone Alternativo","ddd_cel2");
								tfDDD_cel2.setNullRepresentation("");
								tfDDD_cel2.setMaxLength(2);
								tfDDD_cel2.setWidth("40px");

								addComponent(tfDDD_cel2);
							}
						});

						addComponent(new FormLayout() {
							{
								// setMargin(true);
								// setSpacing(true);
								setStyleName("form-cutom");
								addComponent(fieldGroup.buildAndBind("","celular2"));
								((TextField) fieldGroup.getField("celular2")).setNullRepresentation("");
								((TextField) fieldGroup.getField("celular2")).setWidth("105px");
								((TextField) fieldGroup.getField("celular2")).setMaxLength(11);
								
								((TextField) fieldGroup.getField("celular2")).setId("txtCelular2");												
								JavaScript.getCurrent().execute("$('#txtCelular2').mask('000000000')");
							}
						});
					}
				});

				// Email 1,2
				addComponent(new HorizontalLayout() {
					{
						addComponent(new FormLayout() {
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom-new");
								addComponent(fieldGroup.buildAndBind("Email Principal","email"));
								((TextField) fieldGroup.getField("email")).setNullRepresentation("");
								((TextField) fieldGroup.getField("email")).setStyleName("caption-align");
								((TextField) fieldGroup.getField("email")).addStyleName("lowercase");
								((TextField) fieldGroup.getField("email")).setWidth("200px");
								((TextField) fieldGroup.getField("email")).setMaxLength(50);
								((TextField) fieldGroup.getField("email")).setImmediate(true);
								((TextField) fieldGroup.getField("email")).setRequired(true); 
								((TextField) fieldGroup.getField("email")).setTextChangeEventMode(TextChangeEventMode.LAZY);
								((TextField) fieldGroup.getField("email")).addTextChangeListener(new FieldEvents.TextChangeListener() {
									
									@Override
									public void textChange(TextChangeEvent event) {
										if(event.getText() != null && !event.getText().isEmpty() && EmailUtil.validate(event.getText()) && !event.getText().equals(emailInicial)){
											
											if(!ClienteDAO.checkEmailExists(event.getText()) || event.getText().equals(emailInicial)){																
												((TextField) fieldGroup.getField("email")).removeStyleName("invalid-txt");
												emailValid = true;																
											}else{
												((TextField) fieldGroup.getField("email")).addStyleName("invalid-txt");
												emailValid = false;
											}
										}else{
											if(event.getText() != null && !event.getText().isEmpty() && !event.getText().equals(emailInicial)){
												emailValid = false;
												((TextField) fieldGroup.getField("email")).addStyleName("invalid-txt");
											}else{
												emailValid = true;
												((TextField) fieldGroup.getField("email")).removeStyleName("invalid-txt");
											}
										}
										
									}
								});
								
								
							
							}
						});										
						
						addComponent(new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom-new");
								
								CheckBox chb = new CheckBox("Não possui e-mail");
								chb.addListener(new  Property.ValueChangeListener() {
									
									@Override
									public void valueChange(ValueChangeEvent event) {
																								
										((TextField) fieldGroup.getField("email")).setRequired(!((TextField) fieldGroup.getField("email")).isRequired());
										if(!((TextField) fieldGroup.getField("email")).isRequired()){
											((TextField) fieldGroup.getField("email")).setValue("");
										}
										
										if(((TextField) fieldGroup.getField("email")).getValue() == null ||  ((TextField) fieldGroup.getField("email")).getValue().isEmpty()){											
											if(((TextField) fieldGroup.getField("email")).isRequired()){
												emailValid = false;
											}else{
												emailValid = true;
												((TextField) fieldGroup.getField("email")).removeStyleName("invalid-txt");
											}										
										}
									}
								});
								
																					
								
								addComponent(chb); 
							}
						});

						addComponent(new FormLayout() {
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom-new");
								addComponent(fieldGroup.buildAndBind("Site","site"));
								((TextField) fieldGroup.getField("site")).setNullRepresentation("");
								((TextField) fieldGroup.getField("site")).setWidth("200px");
								((TextField) fieldGroup.getField("site")).setMaxLength(100);
								((TextField) fieldGroup.getField("site")).addStyleName("lowercase");
							}
						});
					}
				});
						
				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");

						addComponent(fieldGroup.buildAndBind("Email Alternativo","emailAlternativo"));
						
						((TextField) fieldGroup.getField("emailAlternativo")).setNullRepresentation("");
						((TextField) fieldGroup.getField("emailAlternativo")).setStyleName("caption-align");
						((TextField) fieldGroup.getField("emailAlternativo")).setWidth("200px");
						((TextField) fieldGroup.getField("emailAlternativo")).setMaxLength(100);
						((TextField) fieldGroup.getField("emailAlternativo")).addStyleName("lowercase");
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");
						
						txtUrlLocalizacao = new TextField("Url Localização");
						txtUrlLocalizacao.setNullRepresentation("");
						txtUrlLocalizacao.setStyleName("caption-align");
						txtUrlLocalizacao.setWidth("530px");						
						txtUrlLocalizacao.addStyleName("lowercase");

						if(c.getEndereco_principal().getLocalizacao() != null && !c.getEndereco_principal().getLocalizacao().equals("")){
							txtUrlLocalizacao.setValue(c.getEndereco_principal().getLocalizacao());
						}
						
						addComponent(txtUrlLocalizacao);					
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
					if(fieldGroup.isValid()){
						try {

							if(txtUrlLocalizacao != null && txtUrlLocalizacao.getValue() !=null && !txtUrlLocalizacao.getValue().equals("")){
								Endereco endereco = c.getEndereco_principal();
								endereco.setLocalizacao(txtUrlLocalizacao.getValue());
								EnderecoDAO.save(endereco);
							}
																		
							fieldGroup.commit();
							fireEvent(new ClienteEdicaoRapidaEvent(getUI(), ClienteItem, true));							
						} catch (Exception e) {
							e.printStackTrace();
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
						fireEvent(new ClienteEdicaoRapidaEvent(getUI(), null, false));
						close();
					}else{
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
						gDialog.setCaptionCANCEL("Sair sem Salvar!");
						gDialog.setCaptionOK("Salvar");
						
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								
							}
						});					
						btCancelar.addShortcutListener(sl_esc);
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
		
		
		public void addListerner(ClienteEdicaoRapidaListerner target){
			try {
				Method method = ClienteEdicaoRapidaListerner.class.getDeclaredMethod("onClose", ClienteEdicaoRapidaEvent.class);
				addListener(ClienteEdicaoRapidaEvent.class, target, method);
			} catch (Exception e) {
				System.out.println("Método não Encontrado!");
			}
		}
		public void removeListerner(ClienteEdicaoRapidaListerner target){
			removeListener(ClienteEdicaoRapidaEvent.class, target);
		}
		public static class ClienteEdicaoRapidaEvent extends Event{
			
			private Item item;
			private boolean confirm;
			
			public ClienteEdicaoRapidaEvent(Component source, Item item, boolean confirm) {
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
		public interface ClienteEdicaoRapidaListerner extends Serializable{
			public void onClose(ClienteEdicaoRapidaEvent event);
		}
	

}
