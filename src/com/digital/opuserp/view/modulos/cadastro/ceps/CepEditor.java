package com.digital.opuserp.view.modulos.cadastro.ceps;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.vaadin.suggestfield.SuggestField;
import org.vaadin.suggestfield.SuggestField.SuggestionHandler;

import com.digital.opuserp.dao.CepDAO;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.MunicipiosUtil;
import com.digital.opuserp.view.util.MunicipiosUtil.MunicipioEvent;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class CepEditor extends Window implements GenericEditor {

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
	
	private boolean cepCadastrado;
	private boolean cepValido;
	String  cepAtual;
	
	private TextField txtUf;
	private TextField txtPais;
	private TextField txtCidade;
	
	
	public CepEditor(Item item, String title, boolean modal){
		getEnderecos();
		this.item = item;
		
		Styles styles = Page.getCurrent().getStyles();					        
        styles.add(".dashboard input.v-textfield-readonly { background-color: #FFF; }");
		
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
		
		
		if(item.getItemProperty("id") != null && item.getItemProperty("id").getValue() != null){
			cepAtual = item.getItemProperty("cep").getValue().toString();

			cepCadastrado = true;
			cepValido = true;
		}else{
			cepCadastrado = false;
			cepValido = false;
			
		}

		
		buildLayout();
	}
	
	
	
	private void configLayout(){
		//1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("950px");
				setHeight("207px");

				
				width_cep = "80px";
				width_uf = "40px";	
				width_pais = "147px";	
				
				width_bairro = "200px";
				width_cidade = "147px";	

		//}	
	}
	
	private List<String> enderecos;
	private void getEnderecos(){
		Query q = ConnUtil.getEntity().createQuery("select distinct e.bairro from Ceps e ", String.class);
		enderecos = q.getResultList();
	}

	private List<Object> handleSearchQuery(String query) {
		if ("".equals(query) || query == null) {
			return Collections.emptyList();
		}
		List<String> result = new ArrayList<String>();
		
		for (String country : enderecos) {
			if (country.toLowerCase().startsWith(query.toLowerCase())) {
				result.add(country);
			}
		}
		
		if(result.size() ==0){
			result.add(query.toUpperCase());
		}
		
		return new ArrayList<Object>(result);
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
			new HorizontalLayout(){					
			{							
								
				final TextField txtCep = (TextField)fieldGroup.buildAndBind("CEP", "cep");				
				txtCep.setWidth(width_cep);				
				txtCep.setStyleName("caption-align-cep");
				txtCep.setNullRepresentation("");
				txtCep.setMaxLength(8);
				txtCep.focus();
				txtCep.setRequired(true);
				txtCep.setTextChangeEventMode(TextChangeEventMode.LAZY);
				
				txtCep.addListener(new FieldEvents.TextChangeListener(){
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						if(!event.getText().isEmpty() && !event.getText().toString().equals(cepAtual)){
							
							if(event.getText().length() == 8){							
								CepDAO cepDAO = new CepDAO();
								if(cepDAO.getCep(event.getText()) != null){
									event.getComponent().setStyleName("textfield-cep-invalid");
									cepCadastrado = false;
									cepValido = true;
								}else{
									event.getComponent().setStyleName("textfield-cep-valid");
									cepCadastrado = true;
									cepValido = true;
									 }						
							}else{					
									cepCadastrado = false;
									cepValido = false;
									event.getComponent().setStyleName("textfield-cep-invalid");
								}
						}else if(event.getText().toString().equals(cepAtual)){
							cepCadastrado = true;
							cepValido = true;
							event.getComponent().setStyleName("textfield-cep-valid");
						}
					}
				});
				
				txtCep.addBlurListener(new FieldEvents.BlurListener() {
					
					@Override
					public void blur(BlurEvent event) {
						if(cepCadastrado == false && cepValido == true){
							Notification.show("Cep já Cadastrado");
						}else{
							if(cepValido == false && ((TextField)event.getComponent()).getValue() != null && !((TextField)event.getComponent()).getValue().equals(""))
							Notification.show("Cep Inválido!");
						}				
					}
				});
				
				
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");			
						
						addComponent(txtCep);
					}
				});
						
				final Button btEnvelope = new Button();
				btEnvelope.setStyleName(BaseTheme.BUTTON_LINK);
				btEnvelope.setIcon(new ThemeResource("icons/browser-16.png"));
				btEnvelope.setDescription("Pesquisar nos Correios");

				BrowserWindowOpener opener = new BrowserWindowOpener("https://buscacepinter.correios.com.br/app/endereco/index.php?t");
				opener.setFeatures("height=600,width=800");
				opener.extend(btEnvelope);

				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom_hide_error_cell");
						addComponent(btEnvelope);
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
					setStyleName("form-cutom");						
									
					TextField txtEndereco = (TextField)fieldGroup.buildAndBind("Endereço", "endereco");				
					txtEndereco.setWidth("100%");				
					txtEndereco.setStyleName("caption-align-cep");
					txtEndereco.setNullRepresentation("");
					txtEndereco.setRequired(true);
					txtEndereco.addBlurListener(new FieldEvents.BlurListener() {
						
				@Override
				public void blur(BlurEvent event) {		
						if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
						
						((TextField)(event.getComponent())).setValue(((TextField)(event.getComponent())).getValue().toUpperCase());
					}
				}
			});
					txtEndereco.setMaxLength(200);

					addComponent(txtEndereco);
					setExpandRatio(txtEndereco, 2);						
				}
			});
		
		
		
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
												
						SuggestField search = new SuggestField();
						search.setCaption("Bairro");
						search.setSuggestionHandler(new SuggestionHandler() {
							@Override
							public List<Object> searchItems(String query) {
								
								return handleSearchQuery(query);
							}
						});					
		
						fieldGroup.bind(search, "bairro");				
						search.setWidth(width_bairro);				
						
						search.setStyleName("caption-align-cep");
						search.setRequired(true);

						addComponent(search);
						setExpandRatio(search, 2);
					}
				});
				
				
				addComponent(new HorizontalLayout(){
					{
											
										
						txtCidade = (TextField)fieldGroup.buildAndBind("Cidade", "cidade");				
						txtCidade.setWidth(width_cidade);				
						txtCidade.setNullRepresentation("");
						txtCidade.setMaxLength(50);
						txtCidade.setRequired(true);
						txtCidade.setReadOnly(true);
						txtCidade.addBlurListener(new FieldEvents.BlurListener() {
							
					@Override
					public void blur(BlurEvent event) {		
							if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
								((TextField)(event.getComponent())).setValue(((TextField)(event.getComponent())).getValue().toUpperCase());
						}
					}
				});
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");	
								
								addComponent(txtCidade); 
							}
						});
						
						
						
						final Button btSearch = new Button(null, new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								MunicipiosUtil cep = new MunicipiosUtil(true, true);
								cep.addListerner(new MunicipiosUtil.MunicipioListerner() {
									
									@Override
									public void onSelected(MunicipioEvent event) {
											txtCidade.setReadOnly(false);
											txtCidade.setValue(event.getMunicipio().getMunicipio());
											txtCidade.setReadOnly(true);
											
											txtUf.setReadOnly(false);
											txtUf.setValue(event.getMunicipio().getUf());
											txtUf.setReadOnly(true);
											
											txtPais.setReadOnly(false);
											txtPais.setValue(event.getMunicipio().getPais());
											txtPais.setReadOnly(true);
									}
								});

								getUI().addWindow(cep);
							}
						});
				
						btSearch.setStyleName(BaseTheme.BUTTON_LINK);
						btSearch.setIcon(new ThemeResource("icons/search-16.png"));
						btSearch.setDescription("Procurar por Cep");
		
						addComponent(new FormLayout() {
							{
								setStyleName("form-cutom_hide_error_cell");
								addComponent(btSearch);
							}
						});				
						
						
						
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");						
										
						txtUf = (TextField)fieldGroup.buildAndBind("UF", "uf");				
						txtUf.setWidth(width_uf);						
						txtUf.setNullRepresentation("");
						txtUf.setRequired(true);
						txtUf.setReadOnly(true);
						txtUf.addBlurListener(new FieldEvents.BlurListener() {
							
					@Override
					public void blur(BlurEvent event) {		
							if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
							((TextField)(event.getComponent())).setValue(((TextField)(event.getComponent())).getValue().toUpperCase());
						}
					}
				});
						txtUf.setMaxLength(2);
						addComponent(txtUf);
						setExpandRatio(txtUf, 2);
					}
				});
				
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");						
										
						txtPais = (TextField)fieldGroup.buildAndBind("Pais", "pais");				
						txtPais.setWidth(width_pais);						
						txtPais.setNullRepresentation("");
						txtPais.setRequired(true);
						txtPais.setMaxLength(10);
						txtPais.setReadOnly(true); 
						txtPais.addBlurListener(new FieldEvents.BlurListener() {
							
					@Override
					public void blur(BlurEvent event) {		
							if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
							((TextField)(event.getComponent())).setValue(((TextField)(event.getComponent())).getValue().toUpperCase());
						}
					}
				});
						addComponent(txtPais);
						setExpandRatio(txtPais, 2);
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
				if(fieldGroup.isValid() && cepCadastrado == true && cepValido == true){
					try {
						if(item.getItemProperty("id").getValue()== null){
							fieldGroup.commit();				
							fireEvent(new CepEvent(getUI(), item, true));							
							Notification.show("Cep Cadastrado com Sucesso!");
						}else{
							if(fieldGroup.isModified()){
								fieldGroup.commit();				
								fireEvent(new CepEvent(getUI(), item, true));	
								Notification.show("Cep Alterado com Sucesso!");
								}
							}	
		
						close();
					} catch (CommitException e) {
						e.printStackTrace();
						System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
						Notification.show("Não foi Possivel Salvar as Alterações!");
					}
				}else{
					boolean fieldreadonly = false;
					for (Field<?> field: fieldGroup.getFields()) {

						if(!field.isValid()){						
							field.addStyleName("invalid-txt");
							
							Styles styles = Page.getCurrent().getStyles();					        
					        styles.add(".dashboard input.v-textfield-readonly { background-color: #E97953; }");
					        
							 
						}else{
							field.removeStyleName("invalid-txt");
							Styles styles = Page.getCurrent().getStyles();					        
					        styles.add(".dashboard input.v-textfield-readonly { background-color: #FFF; }");
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
					fireEvent(new CepEvent(getUI(), item, false));
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
									fireEvent(new CepEvent(getUI(), item, true));
									
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
								fireEvent(new CepEvent(getUI(), item, false));
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
	
	public void addListerner(CepListerner target){
		try {
			Method method = CepListerner.class.getDeclaredMethod("onClose", CepEvent.class);
			addListener(CepEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(CepEvent.class, target);
	}
	public static class CepEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public CepEvent(Component source, Item item, boolean confirm) {
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
	public interface CepListerner extends Serializable{
		public void onClose(CepEvent event);
	}
	
	
}
