package com.digital.opuserp.view.modulos.acesso.base;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.vaadin.suggestfield.SuggestField;
import org.vaadin.suggestfield.SuggestField.SuggestionHandler;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.LogErrorDAO;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.CepUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.MunicipiosUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.MunicipiosUtil.MunicipioEvent;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.Page.Styles;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class BaseEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	String  cepAtual;
	
	
	private TextField txtCidade;
	private TextField txtUf;
	private TextField txtPais;
	
	public BaseEditor(Item item, String title, boolean modal){
		getEnderecos();
		Styles styles = Page.getCurrent().getStyles();					        
        styles.add(".dashboard input.v-textfield-readonly { background-color: #FFF; }");
        
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
	
	
	private void configLayout(){
		//1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("938px");
				setHeight("656px");
		//}	
	}
	
	public void updateCep(Ceps cep) {
		((TextField) fieldGroup.getField("cep")).setValue(cep.getCep());
		((TextField) fieldGroup.getField("endereco")).setValue(cep.getEndereco().toUpperCase());
		((TextField) fieldGroup.getField("numero")).setValue("");
		((SuggestField) fieldGroup.getField("bairro")).setValue(cep.getBairro().toUpperCase());
		
		((TextField) fieldGroup.getField("cidade")).setReadOnly(false);
		((TextField) fieldGroup.getField("cidade")).setValue(cep.getCidade().toUpperCase());
		((TextField) fieldGroup.getField("cidade")).setReadOnly(true);
		
		((TextField) fieldGroup.getField("uf")).setReadOnly(false);
		((TextField) fieldGroup.getField("uf")).setValue(cep.getUf().toUpperCase());
		((TextField) fieldGroup.getField("uf")).setReadOnly(true);
		
		((TextField) fieldGroup.getField("pais")).setReadOnly(false);
		((TextField) fieldGroup.getField("pais")).setValue(cep.getPais().toUpperCase());
		((TextField) fieldGroup.getField("pais")).setReadOnly(true);
		
		
		((TextField) fieldGroup.getField("numero")).focus();
		
	}
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
			
				
		vlRoot.addComponent(	new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField tfNomeBase = (TextField)fieldGroup.buildAndBind("Nome", "nome");				
					tfNomeBase.setWidth("250px");				
					tfNomeBase.setStyleName("caption-align-base");
					tfNomeBase.setNullRepresentation("");
					tfNomeBase.setRequired(true);
					tfNomeBase.setMaxLength(200);
					tfNomeBase.focus();
					addComponent(tfNomeBase);					
				}
		});
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextField tfNomeBase = (TextField)fieldGroup.buildAndBind("Altura (m)", "altura");				
					tfNomeBase.setWidth("250px");				
					tfNomeBase.setStyleName("caption-align-base");
					tfNomeBase.setNullRepresentation("");
					tfNomeBase.setRequired(false);
					tfNomeBase.setMaxLength(200);
					
					addComponent(tfNomeBase);					
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField tfNomeBase = (TextField)fieldGroup.buildAndBind("Altitude (m)", "altitude");				
					tfNomeBase.setWidth("250px");				
					tfNomeBase.setStyleName("caption-align-base");
					tfNomeBase.setNullRepresentation("");
					tfNomeBase.setRequired(false);
					tfNomeBase.setMaxLength(200);
					
					addComponent(tfNomeBase);					
				}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
															
						TextField tfNomeBase = (TextField)fieldGroup.buildAndBind("Coordenadas", "coordenadas_sul");				
						tfNomeBase.setWidth("70px");				
						tfNomeBase.setStyleName("caption-align-base");
						tfNomeBase.setNullRepresentation("");
						tfNomeBase.setRequired(false);
						tfNomeBase.setMaxLength(70);
					
						addComponent(tfNomeBase);
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
															
						TextField tfNomeBase = (TextField)fieldGroup.buildAndBind("SUL", "coordenadas_oeste");				
						tfNomeBase.setWidth("70px");				
						//tfNomeBase.setStyleName("caption-align-base");
						tfNomeBase.setNullRepresentation("");
						tfNomeBase.setRequired(false);
						tfNomeBase.setMaxLength(70);
					
						addComponent(tfNomeBase);
					}
				});
				
				
				Label lbOeste = new Label("OESTE");
				lbOeste.addStyleName("lb_coordenadas");
				addComponent(lbOeste);
				
			}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField tfNomeBase = (TextField)fieldGroup.buildAndBind("Tipo Torre", "tipo_torre");				
					tfNomeBase.setWidth("250px");				
					tfNomeBase.setStyleName("caption-align-base");
					tfNomeBase.setNullRepresentation("");
					tfNomeBase.setRequired(false);
					tfNomeBase.setMaxLength(200);
					
					addComponent(tfNomeBase);					
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField tfNContratoEnergia = (TextField)fieldGroup.buildAndBind("Nº Contrato Energia", "n_contrato_energia");				
					tfNContratoEnergia.setWidth("250px");				
					tfNContratoEnergia.setStyleName("caption-align-base");
					tfNContratoEnergia.setNullRepresentation("");
					tfNContratoEnergia.setRequired(false);
					tfNContratoEnergia.setMaxLength(200);
					
					addComponent(tfNContratoEnergia);					
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField tfTitularEnergia = (TextField)fieldGroup.buildAndBind("Titular Energia", "titular_energia");				
					tfTitularEnergia.setWidth("250px");				
					tfTitularEnergia.setStyleName("caption-align-base");
					tfTitularEnergia.setNullRepresentation("");
					tfTitularEnergia.setRequired(false);
					tfTitularEnergia.setMaxLength(200);
					
					addComponent(tfTitularEnergia);					
				}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
															
						TextField tfNomeBase = (TextField)fieldGroup.buildAndBind("Tensão (V)", "tensao");				
						tfNomeBase.setWidth("70px");				
						tfNomeBase.setStyleName("caption-align-base");
						tfNomeBase.setNullRepresentation("");
						tfNomeBase.setRequired(false);
						tfNomeBase.setMaxLength(70);
						
						addComponent(tfNomeBase);
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
															
						TextField tfNomeBase = (TextField)fieldGroup.buildAndBind("Tipo Tensão", "tipo_tensao");				
						tfNomeBase.setWidth("70px");				
						//tfNomeBase.setStyleName("caption-align-base");
						tfNomeBase.setNullRepresentation("");
						tfNomeBase.setRequired(false);
						tfNomeBase.setMaxLength(70);
						
						addComponent(tfNomeBase);
					}
				});
			}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
		
					addComponent(
							new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																	
								TextField tfNomeBase = (TextField)fieldGroup.buildAndBind("Bateria", "bateria");				
								tfNomeBase.setWidth("250px");				
								tfNomeBase.setStyleName("caption-align-base");
								tfNomeBase.setNullRepresentation("");
								tfNomeBase.setRequired(false);
								tfNomeBase.setMaxLength(200);
								
								addComponent(tfNomeBase);					
							}
					});
					
					addComponent(
							new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																	
								DateField dfProxTroca = new DateField("Vencimento Bateria");	
								dfProxTroca.setRequired(true);
								dfProxTroca.setDateFormat("dd/MM/yyyy");
								
								addComponent(dfProxTroca);
								fieldGroup.bind(dfProxTroca, "prox_troca");
							}
					});
		
			}
		});
		
		vlRoot.addComponent(new HorizontalLayout() {
			{
				// Cep
				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");
						addComponent(fieldGroup.buildAndBind("CEP","cep"));
						((TextField) fieldGroup.getField("cep")).setWidth("100px");
						((TextField) fieldGroup.getField("cep")).setNullRepresentation("");
						((TextField) fieldGroup.getField("cep")).setStyleName("caption-align-base");
						((TextField) fieldGroup.getField("cep")).setMaxLength(8);
						((TextField) fieldGroup.getField("cep")).setRequired(true);
						((TextField) fieldGroup.getField("cep")).setMaxLength(8);
						((TextField) fieldGroup.getField("cep")).setTextChangeEventMode(TextChangeEventMode.LAZY);
						
						((TextField) fieldGroup.getField("cep")).addBlurListener(new FieldEvents.BlurListener() {
							
							@Override
							public void blur(BlurEvent event) {
								((TextField) fieldGroup.getField("endereco")).focus();
							}
						});
						
						
						((TextField) fieldGroup.getField("cep")).addListener(new FieldEvents.TextChangeListener() {
									
									
									@Override
									public void textChange(TextChangeEvent event) {
										
										final String cepEndereco = ((TextField) fieldGroup.getField("cep")).getValue();																			
										
																				
										
										if (event.getText().length() == 8) {
											String cepPassado = event.getText();
											CepUtil utilCep = new CepUtil();
											
											if(utilCep.pegarCep(event.getText()) != null){
												
												updateCep(utilCep.pegarCep(event.getText()));					
												
											}else{
												com.digital.opuserp.view.util.CepUtil cep = new com.digital.opuserp.view.util.CepUtil(true, true);
												
												cep.addListerner(new com.digital.opuserp.view.util.CepUtil.CepListerner() {

													final String cepEndereco = ((TextField) fieldGroup.getField("cep")).getValue();
													
													@Override
													public void onSelected(com.digital.opuserp.view.util.CepUtil.CepEvent event) {
														updateCep(event.getCep());																							
													}
												});
												cep.procurarCep(cepPassado);
												getUI().addWindow(cep);
											}
										}	
											
									}
								});

						((TextField) fieldGroup.getField("cep")).addListener(new FieldEvents.BlurListener() {

									@Override
									public void blur(BlurEvent event) {
										if(((TextField) fieldGroup.getField("cep")).getValue() != null)
										((TextField) fieldGroup.getField("numero")).focus();																
									}
						});

					}
				});
				

				final Button btSearch = new Button(null, new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								com.digital.opuserp.view.util.CepUtil cep = new com.digital.opuserp.view.util.CepUtil(true, true);
								cep.addListerner(new com.digital.opuserp.view.util.CepUtil.CepListerner() {

									final String cepEndereco = ((TextField) fieldGroup.getField("cep")).getValue();									
									@Override
									public void onSelected(com.digital.opuserp.view.util.CepUtil.CepEvent event) {
										updateCep(event.getCep());																					
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
						setStyleName("form-cutom-new");
						addComponent(btSearch);
					}
				});

				final Button btEnvelope = new Button();
				btEnvelope.setStyleName(BaseTheme.BUTTON_LINK);
				btEnvelope.setIcon(new ThemeResource("icons/browser-16.png"));
				btEnvelope.setDescription("Pesquisar nos Correios");

				BrowserWindowOpener opener = new BrowserWindowOpener("http://www.buscacep.correios.com.br/servicos/dnec/index.do");
				opener.setFeatures("height=600,width=800");
				opener.extend(btEnvelope);

				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new_hide_error_cell");
						addComponent(btEnvelope);
					}
				});
			}
		});

		// Endereco e Número
		vlRoot.addComponent(new HorizontalLayout() {
			{
				setWidth("90%");
				FormLayout flEndereco = new FormLayout();
				flEndereco.setMargin(true);
				flEndereco.setSpacing(true);
				flEndereco.setWidth("100%");
				flEndereco.setStyleName("form-cutom");
				flEndereco.addComponent(fieldGroup.buildAndBind("Endereço","endereco"));
				((TextField) fieldGroup.getField("endereco")).setWidth("100%");
				((TextField) fieldGroup.getField("endereco")).setNullRepresentation("");
				((TextField) fieldGroup.getField("endereco")).setStyleName("caption-align-base");
				((TextField) fieldGroup.getField("endereco")).setRequired(true);
				((TextField) fieldGroup.getField("endereco")).setImmediate(true);
				((TextField) fieldGroup.getField("endereco")).setTextChangeEventMode(TextChangeEventMode.LAZY);
				((TextField) fieldGroup.getField("endereco")).addBlurListener(new FieldEvents.BlurListener() {

					@Override
					public void blur(BlurEvent event) {												
						if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
							((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
						}
					}
				});
								
				addComponent(flEndereco);
				setExpandRatio(flEndereco, 1);

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setWidth("50px");
						setStyleName("form-cutom");
						addComponent(fieldGroup.buildAndBind("Nº","numero"));
						((TextField) fieldGroup.getField("numero")).setWidth("64px");
						((TextField) fieldGroup.getField("numero")).setNullRepresentation("");
						((TextField) fieldGroup.getField("numero")).setRequired(true);
						((TextField) fieldGroup.getField("numero")).setMaxLength(10);
						((TextField) fieldGroup.getField("numero")).setTextChangeEventMode(TextChangeEventMode.LAZY);

					}
				});
			}
		});

		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						SuggestField search = new SuggestField();
						search.setCaption("Bairro");
						search.setSuggestionHandler(new SuggestionHandler() {
							@Override
							public List<Object> searchItems(String query) {
								
								return handleSearchQuery(query);
							}
						});					
		
						fieldGroup.bind(search, "bairro");				
						search.setWidth("200px");				
						
						search.setStyleName("caption-align");
						search.setRequired(true);

						addComponent(search);
						setExpandRatio(search, 2);
					}
				});


				addComponent(new HorizontalLayout(){
					{
											
										
						txtCidade = (TextField)fieldGroup.buildAndBind("Cidade", "cidade");				
						txtCidade.setWidth("140px");				
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
						txtUf.setWidth("40px");						
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
						txtPais.setWidth("147px");						
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
		
		

				

		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						addComponent(fieldGroup.buildAndBind("Complemento","complemento"));
						((TextField) fieldGroup.getField("complemento")).setNullRepresentation("");
						((TextField) fieldGroup.getField("complemento")).setStyleName("caption-align-base");
						((TextField) fieldGroup.getField("complemento")).setWidth("304px");
						((TextField) fieldGroup.getField("complemento")).setImmediate(true);
						((TextField) fieldGroup.getField("complemento")).setTextChangeEventMode(TextChangeEventMode.LAZY);
						((TextField) fieldGroup.getField("complemento")).setRequired(true);
						((TextField) fieldGroup.getField("complemento")).setDescription("Casa, Apto, Galpão, Anexo, etc");
						((TextField) fieldGroup.getField("complemento")).addBlurListener(new FieldEvents.BlurListener() {
							
							@Override
							public void blur(BlurEvent event) {
								
								if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
									
									((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
								}

							}
						});
						
						
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						addComponent(fieldGroup.buildAndBind("Referência","referencia"));
						((TextField) fieldGroup.getField("referencia")).setNullRepresentation("");
						((TextField) fieldGroup.getField("referencia")).setWidth("314px");
						((TextField) fieldGroup.getField("referencia")).setImmediate(true);
						((TextField) fieldGroup.getField("referencia")).setTextChangeEventMode(TextChangeEventMode.LAZY);
						((TextField) fieldGroup.getField("referencia")).addBlurListener(new FieldEvents.BlurListener() {
							
							@Override
							public void blur(BlurEvent event) {
								if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
									
									((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
								}
							}
						});
						
						
						
					}
				});
			}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextField tfNomeBase = (TextField)fieldGroup.buildAndBind("Contato", "contato");				
					tfNomeBase.setWidth("250px");				
					tfNomeBase.setStyleName("caption-align-base");
					tfNomeBase.setNullRepresentation("");
					tfNomeBase.setRequired(false);
					tfNomeBase.setMaxLength(200);
					
					addComponent(tfNomeBase);					
				}
		});
		
		
		// Telefone 1,2
		vlRoot.addComponent(new HorizontalLayout() {
			{
				//setWidth("100%");
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						TextField tfDDD_fone1 = (TextField) fieldGroup.buildAndBind("Telefone Principal","ddd1");
						tfDDD_fone1.setNullRepresentation("");
						tfDDD_fone1.setStyleName("caption-align-base-ddd");
						tfDDD_fone1.setRequired(true);
						tfDDD_fone1.setMaxLength(2);
						tfDDD_fone1.setWidth("40px");

						addComponent(tfDDD_fone1);
					}
				});

				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new_hide_require");
						addComponent(fieldGroup.buildAndBind("","telefone1"));
						((TextField) fieldGroup.getField("telefone1")).setNullRepresentation("");						
						((TextField) fieldGroup.getField("telefone1")).setWidth("105px");
						((TextField) fieldGroup.getField("telefone1")).setMaxLength(11);
						((TextField) fieldGroup.getField("telefone1")).setRequired(true);
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");

						TextField tfDDD_fone2 = (TextField) fieldGroup.buildAndBind("Telefone Alternativo","ddd2");
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
						setStyleName("form-cutom-new");
						addComponent(fieldGroup.buildAndBind("","telefone2"));
						((TextField) fieldGroup.getField("telefone2")).setNullRepresentation("");
						((TextField) fieldGroup.getField("telefone2")).setWidth("105px");
						((TextField) fieldGroup.getField("telefone2")).setMaxLength(11);

					}
				});

			}
		});
		

		vlRoot.addComponent(	new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextArea taObservacao = new TextArea("Observação");									
					taObservacao.setStyleName("caption-align-base");
					taObservacao.setNullRepresentation("");
					taObservacao.setWidth("100%");
					taObservacao.setHeight("50px");
					
					addComponent(taObservacao);					
					
					fieldGroup.bind(taObservacao, "observacao");
				}
		});
		
		vlRoot.addComponent(	new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
													
				ComboBox cbSituacao = new ComboBox("Situação");
				cbSituacao.setRequired(true); 
				cbSituacao.addItem("NORMAL");
				cbSituacao.addItem("MANUTENCAO");
				cbSituacao.addItem("PROBLEMA");
				cbSituacao.setNullSelectionAllowed(false);
				cbSituacao.setStyleName("caption-align-base");
				cbSituacao.setTextInputAllowed(false);
				
				addComponent(cbSituacao);
				fieldGroup.bind(cbSituacao, "situacao");
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
						fireEvent(new BaseEvent(getUI(), item, true));
						
						close();
					} catch (Exception e) {
						e.printStackTrace();
						
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
						LogErrorDAO.add(new LogError(null, e.getClass().getName(), e.getClass().getSimpleName(),e.getLocalizedMessage(),
								OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
					}
				}else{
					if(txtCidade.getValue() == null || txtUf.getValue() == null || txtPais.getValue() == null){
						Styles styles = Page.getCurrent().getStyles();					        
						styles.add(".dashboard input.v-textfield-readonly { background-color: #E97953; }");						
					}
					
					
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
					fireEvent(new BaseEvent(getUI(), item, false));
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
									fireEvent(new BaseEvent(getUI(), item, true));
									
									if(item.getItemProperty("id").getValue() == null){
										Notify.Show("Base Cadastrada Com Sucesso!", Notify.TYPE_SUCCESS);
									}else{								
										Notify.Show("Base Alterada Com Sucesso!", Notify.TYPE_SUCCESS);
									}
									
									close();
								} catch (Exception e) {
									e.printStackTrace();
									
									Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
									LogErrorDAO.add(new LogError(null, e.getClass().getName(), e.getClass().getSimpleName(),e.getLocalizedMessage(),
											OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
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
								fireEvent(new BaseEvent(getUI(), item, false));
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
	
	public void addListerner(BaseListerner target){
		try {
			Method method = BaseListerner.class.getDeclaredMethod("onClose", BaseEvent.class);
			addListener(BaseEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(BaseEvent.class, target);
	}
	public static class BaseEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public BaseEvent(Component source, Item item, boolean confirm) {
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
	public interface BaseListerner extends Serializable{
		public void onClose(BaseEvent event);
	}
	
	
}
