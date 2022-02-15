package com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.LoginDAO;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario.UsuarioEditor.UsuarioCancelEvent.UsuarioCancelListener;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario.UsuarioEditor.UsuarioSalveEvent.UsuarioSalvedListener;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class UsuarioEditor extends Window{

	
	private Item usuarioItem;
//	private Form usuarioForm;
	private FieldGroup fgUsuario;
	private Button btSalvar;
	private Button btCancelar;
	private String tipo;
	private String usuarioCadastrado;
	private Boolean validarUser;
	private VerticalLayout vlRoot;
	
	private String width_user;
	private String width_senha;
	private String width_funcao;

	
	private String novoUsuario;

	public UsuarioEditor(Item item, String titulo) {
		
		configLayout();
		
		
		configLayout();
		setResizable(false);
		setClosable(false);
		center();
		
		usuarioItem = item;
		tipo = titulo;
		setCaption(titulo);	
		
		btSalvar = buildBtSalvar();
		btCancelar = buildBtCancelar();
		vlRoot = buildVlRoot();
		
		setContent(vlRoot);	
		
		
		setClosable(false);
		
		if(tipo.equals("Editar Usuario")){
			validarUser = true;
		}else{
			validarUser = false;
		}
		
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
			
	}
		
	private void configLayout(){
		//1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("310px");
				setHeight("200px");
			
				 width_user = "150px";
				 width_senha = "150px";
				 width_funcao = "150px";
			
		//}
	}
	
	
	private VerticalLayout buildVlRoot(){
		
		
		vlRoot = new VerticalLayout();
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");	 
		fgUsuario = new FieldGroup(usuarioItem);
		
//		vlRoot.addComponent(new FormLayout(){
//			{
//				addComponent(fgUsuario.buildAndBind("Usuario", "username"));
//				((TextField)fgUsuario.getField("username")).setStyleName("user");
//				((TextField)fgUsuario.getField("username")).setEnabled(true);
//				((TextField)fgUsuario.getField("username")).setRequired(true);
//			}	
//		});

		final TextField tfUser = new TextField();
		fgUsuario.bind(tfUser, "username");
		tfUser.setCaption("Usuario");
		tfUser.setRequired(true);
		tfUser.setNullRepresentation("");
		tfUser.focus();
		tfUser.setWidth(width_user);
		
		tfUser.setStyleName("caption-geral");
	tfUser.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				usuarioCadastrado = event.getText();
				usuarioCadastrado.replaceAll(" ",""); 
								
				LoginDAO log = new LoginDAO();
				
				 if(!event.getText().isEmpty()){
						if(log.validarUsuario(event.getText()) == true){
							event.getComponent().setStyleName("textfield-valid-usuario");
							validarUser = true;					
							tfUser.setValue(event.getText());		
							
						}else{
							event.getComponent().setStyleName("textfield-invalid-usuario");
							validarUser = false;					
							tfUser.setValue(event.getText());				
						}	
				}else{
//					lbInfo.setStyleName("lbInicial");				
				}
			}
		});
	
		tfUser.addListener(new FieldEvents.BlurListener() {
			
					@Override
					public void blur(BlurEvent event) {
						if(validarUser == false){
							Notify.Show("Usuario Já Cadastrado!", Notify.TYPE_ERROR);
						}						
					}
				});
		
//		tfUser.addListener(new FieldEvents.TextChangeListener() {
//			
//			@Override
//			public void textChange(TextChangeEvent event) {
//				usuarioCadastrado = event.getText();
//				usuarioCadastrado.replaceAll(" ",""); 
//								
//				Login log = new Login();
//				if(log.validarUsuario(event.getText()) == true && !usuarioCadastrado.isEmpty()){							
//					lbInfo.setValue("Disponivel!");
//					lbInfo.setStyleName("verde");
//					validarUser = true;	
//					tfUser.setValue(event.getText());		
//				}else {
//					lbInfo.setValue("Indisponivel!");
//					validarUser = false;
//					lbInfo.setStyleName("vermelho");
//					tfUser.setValue(event.getText());
//				}				
//			}
//		});
		
		HorizontalLayout hlValidarUser = new HorizontalLayout();
		hlValidarUser.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				addComponent(tfUser);
			}
		});
//		hlValidarUser.addComponent(new FormLayout(){
//			{
//				setStyleName("lbInfo");
//				addComponent(lbInfo);
//			}
//		});
		
		vlRoot.addComponent(hlValidarUser);
				
		final PasswordField  senha = new PasswordField();
		fgUsuario.bind(senha, "password");
		senha.setCaption("Senha");
		senha.setRequired(true);
		senha.setWidth(width_senha);
		
		senha.setNullRepresentation("");
		senha.setStyleName("caption-geral");
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				addComponent(senha);			
			}	
			
		});
		
		final ComboBox boxFuncao = new ComboBox();
		fgUsuario.bind(boxFuncao, "funcao");
		boxFuncao.setRequired(true);
		boxFuncao.setNullSelectionItemId(false);
		boxFuncao.addItem("Operador");
		boxFuncao.addItem("Gerente");
		boxFuncao.addItem("tecnico");
		boxFuncao.setCaption("Função");
		boxFuncao.setStyleName("caption-geral");
		boxFuncao.setWidth(width_funcao);
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				addComponent(boxFuncao);
			}
		});	
		
		return vlRoot;	
	}
	

	private Button buildBtSalvar(){
		btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
	 
				 if(tipo.equals("Cadastro de usuario")){					 
					  if(fgUsuario.isValid() && validarUser == true ){
						  try {
							fgUsuario.commit();
							usuarioItem.getItemProperty("password").setValue(StringUtil.md5(fgUsuario.getField("password").getValue().toString()));
							
							fireEvent(new UsuarioSalveEvent(getUI(), usuarioItem));
							  Notify.Show("Usuario Cadastrado com sucesso!", Notify.TYPE_ERROR);
							  close();
						} catch (CommitException e) {
							e.printStackTrace();
						}
						  
					  }else{
						  Notify.Show_Invalid_Submit_Form();
					  }
	
					 }else if(tipo.equals("Editar Usuario")){	
						 								 	
							 if(fgUsuario.isValid() && validarUser == true ){						  							  
								 try {
						
									 fgUsuario.commit();
									 usuarioItem.getItemProperty("password").setValue(StringUtil.md5(fgUsuario.getField("password").getValue().toString()));									 
									 
									 fireEvent(new UsuarioSalveEvent(getUI(), usuarioItem));

									   Notify.Show("Usuario Alterado com sucesso!", Notify.TYPE_ERROR);
									   
									   close();
								} catch (CommitException e) {
									e.printStackTrace();
									System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
									Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
								}
								  
							 }else{
								 Notify.Show_Invalid_Submit_Form();
								 fgUsuario.discard();	 
							}
					 }		 
			}	 
		});
		btSalvar.addStyleName("default");
		return btSalvar;		
	}
		
	private Button buildBtCancelar(){
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
					
			@Override
			public void buttonClick(ClickEvent event) {	
				if(fgUsuario.isModified() ){
					
					final GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {									
							if(event.isConfirm()){
								
								if(fgUsuario.isValid() && validarUser == true){
									try {
										fgUsuario.commit();
										fireEvent(new UsuarioSalveEvent(getUI(), usuarioItem));
										
										if(usuarioItem.getItemProperty("id").getValue() == null){
											Notify.Show("Usuario Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
										}else{
											Notify.Show("Usuario Alterado com Sucesso!", Notify.TYPE_SUCCESS);
										}
										
										close();
									} catch (Exception e) {
										e.printStackTrace();
										System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
										Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
									}								
								}else{
									Notify.Show_Invalid_Submit_Form();
								}
						
							}else{
								fgUsuario.discard();	
								fireEvent(new UsuarioCancelEvent(getUI(), usuarioItem));
								gDialog.close();
								close();

							}
						}
					});
					getUI().addWindow(gDialog);
					
				}else{
					close();
					fgUsuario.discard();
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
	
//	private Form buildForm(){
//				
//		
//		usuarioForm = new Form();		
//		usuarioForm.setImmediate(true);
//		usuarioForm.getFooter().addComponent(btSalvar);
//		usuarioForm.getFooter().addComponent(btCancelar);
//		usuarioForm.setSizeUndefined();
//		usuarioForm.setFormFieldFactory(new FormFieldFactory() {
//			
//			
//			
//			@Override
//			public Field createField(Item item, Object propertyId,
//					Component uiContext) {
//				if("password".equals(propertyId)){
//					PasswordField passField = new PasswordField();
//					passField.setRequired(true);
//					passField.setNullRepresentation("");
//					passField.setCaption("Senha");	
//					
//					return passField;
//				}else if("funcao".equals(propertyId)){
//					ComboBox boxFuncao = new ComboBox();
//					boxFuncao.setRequired(true);
//					boxFuncao.setNullSelectionItemId(false);
//					boxFuncao.addItem("admin");
//					boxFuncao.addItem("gerente");
//					boxFuncao.addItem("tecnico");
//					boxFuncao.addItem("vendedor");
//					boxFuncao.addItem("supervisor");
//					boxFuncao.addItem("caixa");
//					boxFuncao.setCaption("Função");
//
//					
//					return boxFuncao;							
//				}else{
//					return buildUsername();
//				}
//				
//			}
//		});
//		usuarioForm.setItemDataSource(usuarioItem, Arrays.asList("username","password","funcao"));//tambem binda os item da tabela com formulario de edi��o
//		return usuarioForm;		
//	}	
	
	
	
//	private CustomField<String> buildUsername(){
//		 CustomField<String> username = new CustomField<String>() {
//
//			@Override
//			protected Component initContent() {
//				HorizontalLayout hlUsername = new HorizontalLayout();
//				final Label lbInfo = new Label();
//				final Label requerido = new Label("*");
//				requerido.setStyleName("requerido");
//				final Label usuario = new Label("Usuario");	
//				final TextField tfUsername = new TextField();
//				tfUsername.setStyleName("ftUser");
//				tfUsername.setValue(usuarioItem.getItemProperty("username").toString());
//				tfUsername.setNullRepresentation("");
//				setCaption("Usuario");
//				
//				tfUsername.addListener(new FieldEvents.TextChangeListener() {
//					
//					@Override
//					public void textChange(TextChangeEvent event) {
//						usuarioCadastrado = event.getText();
//						usuarioCadastrado.replaceAll(" ",""); 
//						
//												
//						Login log = new Login();
//						if(log.validarUsuario(event.getText()) == true && !usuarioCadastrado.isEmpty()){							
//							lbInfo.setValue("Disponivel!");
//							lbInfo.setStyleName("verde");
//							validarUser = true;	
//							setValue(event.getText());			
//						}else {
//							lbInfo.setValue("Indisponivel!");
//							validarUser = false;
//							lbInfo.setStyleName("vermelho");
//							setValue(event.getText());
//						}
//						
//						
//					}
//				});		
//			//	hlUsername.addComponent(usuario);
//				hlUsername.addComponent(requerido);
//				hlUsername.addComponent(tfUsername);
//				hlUsername.addComponent(lbInfo);			
//				return hlUsername;
//			}
//
//			@Override
//			public Class getType() {
//				return String.class;
//			}			
//		};	
//		return username;		
//	}

	
	
	public void addListerner(UsuarioSalvedListener listerner) {
		try {
			Method method = UsuarioSalvedListener.class.getDeclaredMethod("usuarioSalvo",
					new Class[]{UsuarioSalveEvent.class});
			addListener(UsuarioSalveEvent.class, listerner, method);	
		} catch (Exception e) {
			System.out.println("ERRO METHOD NAO ENCONTRADO!");
		}
	}
	
	public void removerListerner() {
		
	}
	
	public static class UsuarioSalveEvent extends Event{
		
		private Item usuarioItem;

		public UsuarioSalveEvent(Component arg0,Item usuarioItem) {
			super(arg0);
			this.usuarioItem = usuarioItem;			
		}
		
		public Item getUsuarioItem() {
			return usuarioItem;			
		}
			
		
	public interface UsuarioSalvedListener extends Serializable{
		public void usuarioSalvo(UsuarioSalveEvent event);
	}	
}
	
	public void addListener(UsuarioCancelListener listener){
		try {
			Method method = UsuarioCancelListener.class.getDeclaredMethod("cancelarAlteracao",
				new Class[]{UsuarioCancelEvent.class});
			addListener(UsuarioCancelEvent.class, listener, method);
		} catch (Exception e) {
			System.out.println("ERRO METHOD NAO ENCONTRADO!");
		}		
	}
	
	public static class UsuarioCancelEvent extends Event{
		
		private Item usuarioItem;
		
		public UsuarioCancelEvent(Component arg0,Item usuarioItem) {
			super(arg0);
			this.usuarioItem = usuarioItem;			
		}

		public Item getUsuarioItem() {
			return usuarioItem;
		}
	
	public interface UsuarioCancelListener extends Serializable{
		public void cancelarAlteracao(UsuarioCancelEvent event);
	}
  }
	
	
	
	
	
}
