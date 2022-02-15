package com.digital.opuserp.view.modulos.cadastro.transportadoras;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Query;

import org.vaadin.suggestfield.SuggestField;
import org.vaadin.suggestfield.SuggestField.SuggestionHandler;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.domain.Cnae;
import com.digital.opuserp.domain.Transportadoras;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.CnpjUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.ValidarCPF;
import com.digital.opuserp.view.util.CepUtil;
import com.digital.opuserp.view.util.MunicipiosUtil;
import com.digital.opuserp.view.util.MunicipiosUtil.MunicipioEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
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
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class TransportadoraEditor2 extends Window implements GenericEditor {


	
	Item itemFornecedor;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	ComboBox cbTransportadora;
	
	private Float width_endereco;
	private Float width_numero;
	private Float width_bairro;
	private Float width_cidade;
	private Float width_uf;
	private Float width_pais;
	private String width_rasão_social;
	private String tipo_frete;
	
	Integer codTransportadoraDefault;
	boolean cnpjValid = false;
	boolean cnpjCadast= false;
	String cnpjAtual;
	FormLayout frmTransportadoraPadrao;
	HorizontalLayout hlFreteTransportadora;
	boolean pode = false;
	
	Cnae cnaeSelecionado;
	
	
	private TextField txtCidade;
	private TextField txtUf;
	private TextField txtPais;
		
	public TransportadoraEditor2(Item item, String title, boolean modal){
		getEnderecos();
		
		Styles styles = Page.getCurrent().getStyles();					        
        styles.add(".dashboard input.v-textfield-readonly { background-color: #FFF; }");
        
		this.itemFornecedor = item;
		
		setStyleName("fornecedor_editor");
		setLocale(new Locale("pt", "BR"));
		
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
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		if(item.getItemProperty("cnpj").getValue() != null){
			cnpjAtual  = item.getItemProperty("cnpj").getValue().toString();
			cnpjValid = true;
			cnpjCadast = true;
		}else{
			cnpjValid = false;
			cnpjCadast = false;
			
		}
		
		
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
				setWidth("950px");
				setHeight("650px");
			
				width_numero = 0.1f;
				width_endereco = 0.53f;				
				width_bairro = 0.2f;
				width_cidade = 0.23f;
				width_uf = 0.1f;
				width_pais = 0.2f;
				width_rasão_social = "693px";
			
		//}
	}
	public void updateCep(Ceps cep){
		((TextField) fieldGroup.getField("cep")).setValue(cep.getCep().toUpperCase());
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
		
		fieldGroup = new FieldGroup(itemFornecedor);
		
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
										
										
						TextField tfCod = (TextField)fieldGroup.buildAndBind("Código", "id");				
						tfCod.setWidth("88px");				
						tfCod.setNullRepresentation("");
						tfCod.setStyleName("caption-align-fornecedores-top");						
						tfCod.setReadOnly(true);
						tfCod.setMaxLength(10);
					//	tfCod.focus();
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
											
										
						TextField txtDataCadastro = new TextField("Cadastro");			
						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");				
						if(fieldGroup.getItemDataSource().getItemProperty("data_cadastro").getValue() != null){
							txtDataCadastro.setValue(sdf.format(fieldGroup.getItemDataSource().getItemProperty("data_cadastro").getValue()).toString());
						}else{
							txtDataCadastro.setValue(sdf.format(new Date()));
						}
						
						txtDataCadastro.setWidth("130px");				
						txtDataCadastro.setNullRepresentation("");						
						txtDataCadastro.setReadOnly(true);
						txtDataCadastro.setMaxLength(10);
						addComponent(txtDataCadastro);
						setExpandRatio(txtDataCadastro, 2);
					}
				});
								
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
										
										
						TextField txtDataAlteracao =new TextField("Alteração");	
											
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");				
						if(fieldGroup.getItemDataSource().getItemProperty("data_alteracao").getValue() != null){
							txtDataAlteracao.setValue(sdf.format(fieldGroup.getItemDataSource().getItemProperty("data_alteracao").getValue()).toString());
						}else{
							txtDataAlteracao.setValue(sdf.format(new Date()));
						}
						
						txtDataAlteracao.setWidth("130px");				
						txtDataAlteracao.setNullRepresentation("");						
						txtDataAlteracao.setReadOnly(true);
						txtDataAlteracao.setMaxLength(10);
						addComponent(txtDataAlteracao);
						setExpandRatio(txtDataAlteracao, 2);
					}
				});
				
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						ComboBox tfCod = new ComboBox("Status");
						tfCod.setNullSelectionAllowed(false);
						tfCod.addItem("ATIVO");						
						tfCod.addItem("INATIVO");
						tfCod.setWidth("115px");			
						tfCod.focus();			
						tfCod.setStyleName("caption-align-status");
						tfCod.setRequired(true);
												
						addComponent(tfCod);
						
						fieldGroup.bind(tfCod, "status");
					}
				});
					
				
			}	
		});

		vlRoot.addComponent(
				new HorizontalLayout(){
					{
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
												
												
								TextField tfCod = (TextField)fieldGroup.buildAndBind("CPF/CNPJ", "cnpj");				
								tfCod.setWidth("150px");				
								tfCod.setStyleName("caption-align-fornecedores-top");
								tfCod.setNullRepresentation("");
								tfCod.setRequired(true);
								tfCod.setMaxLength(14);
								tfCod.setTextChangeEventMode(TextChangeEventMode.LAZY);
								tfCod.addListener(new FieldEvents.TextChangeListener() {
									
									@Override
									public void textChange(TextChangeEvent event) {
										
											  if(!event.getText().isEmpty() && !event.getText().toString().equals(cnpjAtual)){
													
													//Validar CNPJ
													if(event.getText().length() == 14){
														if(CnpjUtil.validar(event.getText())){
															cnpjValid = true;
															event.getComponent().removeStyleName("textfield-invalid");
															event.getComponent().setStyleName("textfield-valid");
														}else{
															cnpjValid = false;
															event.getComponent().removeStyleName("textfield-valid");
															event.getComponent().setStyleName("textfield-invalid");
														}
													}else if(event.getText().length() == 11){
//														cnpjValid = false;
//														event.getComponent().removeStyleName("textfield-valid");
//														event.getComponent().setStyleName("textfield-invalid");
//													}
													
//													if(event.getText().length() == 11){
														if(ValidarCPF.cpfValido(event.getText())){
															cnpjValid = true;
															event.getComponent().removeStyleName("textfield-invalid");
															event.getComponent().setStyleName("textfield-valid");
														}else{
															cnpjValid = false;
															event.getComponent().removeStyleName("textfield-valid");
															event.getComponent().setStyleName("textfield-invalid");
														}
													}else{
														cnpjValid = false;
														event.getComponent().removeStyleName("textfield-valid");
														event.getComponent().setStyleName("textfield-invalid");
													}
													
													
													
													if(cnpjValid){
														//Verificar Existencia
														FornecedorDAO fDAO = new FornecedorDAO();
														if(fDAO.cpfCnpjCadastrado(OpusERP4UI.getEmpresa(), event.getText())){
															cnpjCadast = true;
															event.getComponent().removeStyleName("textfield-invalid");
															event.getComponent().setStyleName("textfield-valid");
														}else{
															cnpjCadast = false;
															event.getComponent().removeStyleName("textfield-valid");
															event.getComponent().setStyleName("textfield-invalid");
														}
													}
													
												}else{
													
													if(!event.getText().isEmpty())
													{
														cnpjValid = true;
														cnpjCadast = true;											
													}else{
														cnpjValid = false;
														cnpjCadast = false;
													}
													
													event.getComponent().removeStyleName("textfield-invalid");
													event.getComponent().removeStyleName("textfield-valid");
													event.getComponent().setStyleName("caption-align-fornecedores-top");
												}
											
										}
								});
								
								
								tfCod.addListener(new FieldEvents.BlurListener() {
									
									@Override
									public void blur(BlurEvent event) {
										if(cnpjValid == false){
											Notification.show("CPF/CNPJ Inválido!");
										}else{											
											if(cnpjCadast == false){
												Notification.show("CPF/CNPJ Já Cadastrado!");
											}
										}
										
									}
								});
								
								tfCod.addBlurListener(new FieldEvents.BlurListener() {
									
									@Override
									public void blur(BlurEvent event) {
										((TextField) fieldGroup.getField("insc_estadual")).focus();
									}
								});
									
								addComponent(tfCod);
//								if(item.getItemProperty("id").getValue() != null){
//									cnpjValid = true;
//									tfCod.setStyleName("textfield-valid");
//									//tfCod.setReadOnly(true);
//								}
							}	
						});
						
						final Button btCpf = new Button();
						btCpf.setStyleName(BaseTheme.BUTTON_LINK);
						btCpf.setIcon(new ThemeResource("icons/browser-16.png"));
						btCpf.setDescription("Pesquisar CPF na Receita Federal");
						
						BrowserWindowOpener openCpf = new BrowserWindowOpener("http://www.receita.fazenda.gov.br/aplicacoes/atcta/cpf/consultapublica.asp");
						openCpf.setFeatures("height=600,width=800");
						openCpf.extend(btCpf);
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");	
								addComponent(btCpf);
							}
						});
						
						
						final Button btCnpj = new Button();
						btCnpj.setStyleName(BaseTheme.BUTTON_LINK);
						btCnpj.setIcon(new ThemeResource("icons/browser-16.png"));
						btCnpj.setDescription("Pesquisar CNPJ na Receita Federal");
						
						BrowserWindowOpener openCnpj = new BrowserWindowOpener("http://www.receita.fazenda.gov.br/pessoajuridica/cnpj/cnpjreva/cnpjreva_solicitacao.asp");
						openCnpj.setFeatures("height=600,width=800");
						openCnpj.extend(btCnpj);
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new_hide_error_cell");	
								addComponent(btCnpj);
							}
						});
						
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
													
//												
//								TextField tfCod = (TextField)fieldGroup.buildAndBind("Inscrição Estadual/RG", "insc_estadual");				
//								tfCod.setWidth("250px");				
//								tfCod.setNullRepresentation("");
//								tfCod.setRequired(false);								
//								tfCod.setMaxLength(45);
								addComponent(((TextField) fieldGroup.buildAndBind("Inscrição Estadual/RG", "insc_estadual")));
								((TextField) fieldGroup.getField("insc_estadual")).setWidth("338px");
								((TextField) fieldGroup.getField("insc_estadual")).setNullRepresentation("");
								((TextField) fieldGroup.getField("insc_estadual")).setMaxLength(45);
//								setExpandRatio(tfCod, 2);
							}
						});
						
						
					}
				}
		);	
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Razão Social", "razao_social");				
					tfCod.setWidth(width_rasão_social);				
					tfCod.setStyleName("caption-align-fornecedores-top");
					tfCod.setNullRepresentation("");
					tfCod.setMaxLength(200);
					
					tfCod.setRequired(true);
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});	
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Fantasia", "fantasia");				
					tfCod.setWidth("340px");				
					tfCod.setStyleName("caption-align-fornecedores-top");
					tfCod.setNullRepresentation("");
					tfCod.setMaxLength(200);
					
					tfCod.setRequired(true);
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});	
		

		
		VerticalLayout vlPrincipal = new VerticalLayout(){
			{
				setMargin(true);
				setStyleName("horizontal-tabsheet");
				addComponent(new HorizontalLayout(){
							{
								setStyleName("custom-form_tabsheet");		
								addComponent(new FormLayout(){					
									{
										
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");						
														
										TextField tfCep = (TextField)fieldGroup.buildAndBind("CEP", "cep");				
										tfCep.setWidth("80px");				
										tfCep.setStyleName("caption-align-fornecedores");
										tfCep.setNullRepresentation("");
										tfCep.setMaxLength(8);
										tfCep.setRequired(true);
										tfCep.setTextChangeEventMode(TextChangeEventMode.LAZY);
										tfCep.addListener(new FieldEvents.TextChangeListener() {
											
											@Override
											public void textChange(TextChangeEvent event) {
												if(event.getText().length() == 8){
													String cepPassado = event.getText();
													com.digital.opuserp.util.CepUtil cepUtil = new com.digital.opuserp.util.CepUtil();		
													if(cepUtil.pegarCep(event.getText()) != null){
														updateCep(cepUtil.pegarCep(event.getText()));
													}else{
														com.digital.opuserp.view.util.CepUtil cep = new com.digital.opuserp.view.util.CepUtil(true, true);
														cep.addListerner(new com.digital.opuserp.view.util.CepUtil.CepListerner() {

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
										
										tfCep.addListener(new FieldEvents.BlurListener() {
											
											@Override
											public void blur(BlurEvent event) {
												((TextField) fieldGroup.getField("numero")).focus();
											}
										});
										
										addComponent(tfCep);
										setExpandRatio(tfCep, 2);						
									}
								});
								
								
								final Button btSearch = new Button(null, new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
											CepUtil cep = new CepUtil(true, true);
											cep.addListerner(new CepUtil.CepListerner() {
												
												@Override
												public void onSelected(com.digital.opuserp.view.util.CepUtil.CepEvent event) {												
													updateCep(event.getCep());
													((TextField) fieldGroup.getField("numero")).focus();												
												}
											});
											
											getUI().addWindow(cep);
									}
								});		
								btSearch.setStyleName(BaseTheme.BUTTON_LINK);
								btSearch.setIcon(new ThemeResource("icons/search-16.png"));
								btSearch.setDescription("Procurar por Cep");
								
								addComponent(new FormLayout(){
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
								
								addComponent(new FormLayout(){
									{
										setStyleName("form-cutom-new_hide_error_cell");	
										addComponent(btEnvelope);
									}
								});
							}
						});
				
				
				
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						
						FormLayout frmEndereco = new FormLayout(){
							{
								
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");						
												
//								TextField tfCod = (TextField)fieldGroup.buildAndBind("Endereço", "endereco");				
//								tfCod.setWidth("100%");				
//								tfCod.setNullRepresentation("");
//								tfCod.setStyleName("caption-align-fornecedores");
//								tfCod.setRequired(true);
//								tfCod.setMaxLength(100);
								addComponent(fieldGroup.buildAndBind("Endereço", "endereco"));
								((TextField) fieldGroup.getField("endereco")).setWidth("100%");
								((TextField) fieldGroup.getField("endereco")).setNullRepresentation("");
								((TextField) fieldGroup.getField("endereco")).setStyleName("caption-align-fornecedores");
								((TextField) fieldGroup.getField("endereco")).setRequired(true);
								((TextField) fieldGroup.getField("endereco")).setMaxLength(100);
								((TextField) fieldGroup.getField("endereco")).addBlurListener(new FieldEvents.BlurListener() {
									
									@Override
									public void blur(BlurEvent event) {
										if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
											
											((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
										}
									}
								});
								//setExpandRatio(tfCod, 2);
							}
						};
						
						addComponent(frmEndereco);
						
						
						setExpandRatio(frmEndereco, width_endereco);
					
		
						
						FormLayout frmNumero = new FormLayout(){
							{
								
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");						
												
								TextField tfCod = (TextField)fieldGroup.buildAndBind("Número", "numero");				
								tfCod.setWidth("54px");				
								tfCod.setNullRepresentation("");
								tfCod.setRequired(true);
								tfCod.setMaxLength(10);
							//	tfCod.focus();
								addComponent(tfCod);
								setExpandRatio(tfCod, 2);
							}
						};
						addComponent(frmNumero);
						
						
						setExpandRatio(frmNumero, width_numero);
						
					}	
				});
				
				addComponent(new HorizontalLayout() {
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
								
								search.setStyleName("caption-align-fornecedores");
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
				
				
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");						
												
								TextField tfCod = (TextField)fieldGroup.buildAndBind("Complemento", "complemento");				
								tfCod.setWidth("100%");				
								tfCod.setNullRepresentation("");
								tfCod.setStyleName("caption-align-fornecedores");
								tfCod.setRequired(false);
								tfCod.setMaxLength(100);
								tfCod.addBlurListener(new FieldEvents.BlurListener() {
									
									@Override
									public void blur(BlurEvent event) {
										if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
											
											((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
										}
									}
								});
								addComponent(tfCod);
								setExpandRatio(tfCod, 2);
							}
						});
						
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");						
												
								TextField tfCod = (TextField)fieldGroup.buildAndBind("Referência", "ponto_referencia");				
								tfCod.setWidth("351px");				
								tfCod.setNullRepresentation("");
								tfCod.setRequired(false);
								tfCod.setMaxLength(100);
								tfCod.addBlurListener(new FieldEvents.BlurListener() {
									
									@Override
									public void blur(BlurEvent event) {
										if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
											
											((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
										}
									}
								});
								
								addComponent(tfCod);
								setExpandRatio(tfCod, 2);
							}
						});
					}	
				});
				
				
				
				
				addComponent(new HorizontalLayout(){
					{
						setSizeUndefined();		
						
						
						FormLayout frmDDD_Fone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								TextField tfCod = (TextField)fieldGroup.buildAndBind("Telefone Principal", "ddd_fone1");
								tfCod.setWidth("40px");
								tfCod.setRequired(true);
								tfCod.setStyleName("caption-align-fornecedores");
								tfCod.setNullRepresentation("");
								tfCod.setMaxLength(2);
								
								addComponent(tfCod);						
							}
						};
						addComponent(frmDDD_Fone1);
						
						
						FormLayout frmFone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								TextField tfCod = (TextField)fieldGroup.buildAndBind(null, "fone1");
								tfCod.setWidth("105px");
								tfCod.setRequired(true);
								tfCod.setNullRepresentation("");
								tfCod.setMaxLength(10);
								
								
								addComponent(tfCod);						
							}
						};
						addComponent(frmFone1);
						
						
						
						FormLayout frmDDD_Fone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								TextField tfCod = (TextField)fieldGroup.buildAndBind("Telefone Alternativo", "ddd_fone2");
								tfCod.setWidth("40px");							
								tfCod.setStyleName("caption-align-empresa");
								tfCod.setNullRepresentation("");
								tfCod.setMaxLength(2);
								
								addComponent(tfCod);						
							}
						};
						addComponent(frmDDD_Fone2);
						
						
						FormLayout frmFone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								TextField tfCod = (TextField)fieldGroup.buildAndBind(null, "fone2");
								tfCod.setWidth("105px");							
								tfCod.setNullRepresentation("");
								tfCod.setMaxLength(10);
								
								addComponent(tfCod);						
							}
						};
						addComponent(frmFone2);
						
					
						
					}
				});
				
				addComponent(new HorizontalLayout(){
					{
						setSizeUndefined();		
						
						
						FormLayout frmDDD_Fone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								TextField tfCod = (TextField)fieldGroup.buildAndBind("Telefone Alternativo", "ddd_fone3");
								tfCod.setWidth("40px");							
								tfCod.setStyleName("caption-align-fornecedores");
								tfCod.setNullRepresentation("");
								tfCod.setMaxLength(2);
								
								addComponent(tfCod);						
							}
						};
						addComponent(frmDDD_Fone1);
						
						
						FormLayout frmFone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								TextField tfCod = (TextField)fieldGroup.buildAndBind(null, "fone3");
								tfCod.setWidth("105px");							
								tfCod.setNullRepresentation("");
								tfCod.setMaxLength(10);
								
								addComponent(tfCod);						
							}
						};
						addComponent(frmFone1);
						
									
						
						FormLayout frmFone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								TextField tfCod = (TextField)fieldGroup.buildAndBind("0800", "fone0800");
								tfCod.setWidth("105px");							
								tfCod.setNullRepresentation("");
								tfCod.setStyleName("caption-align-empresa");
								tfCod.setMaxLength(11);
								
								addComponent(tfCod);						
							}
						};
						addComponent(frmFone2);
						
					
						
					}
				});
				
				
				
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
													
												
								TextField tfCod = (TextField)fieldGroup.buildAndBind("E-mail", "email");				
								tfCod.setWidth("100%");				
								tfCod.setStyleName("caption-align-fornecedores");
								tfCod.setNullRepresentation("");
								
								tfCod.setRequired(false);
								tfCod.setMaxLength(45);
								
								addComponent(tfCod);
//								setExpandRatio(tfCod, 2);						
							}
						});
						
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
												
												
								TextField tfCod = (TextField)fieldGroup.buildAndBind("Site", "home_page");				
												
								tfCod.setWidth("100%");
								tfCod.setNullRepresentation("");
								
								tfCod.setMaxLength(45);
								
								addComponent(tfCod);
//								setExpandRatio(tfCod, 2);						
							}
						});
						
						
					}
				});			

			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");					
					
					addComponent(
							new FormLayout(){					
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
														
												
								TextArea txtObs = new TextArea("Obs.:");										
								txtObs.setWidth("100%");	
								txtObs.setHeight("100px");								
								txtObs.setStyleName("caption-align-fornecedores");
								txtObs.setRequired(false);
								txtObs.setNullRepresentation("");
								addComponent(txtObs);	
								
								fieldGroup.bind(txtObs, "observacao");
							}
						});	
					
				}
			});	
					
			}
		};
		
		
		VerticalLayout vlContatos = new VerticalLayout(){
			{
				setMargin(true);
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);										
						
						TextField tfDept = new TextField("DPTº");
						tfDept.setStyleName("caption-align-fornecedores");
						tfDept.setWidth("400px");
						tfDept.setNullRepresentation("");
						
						addComponent(tfDept);			
						fieldGroup.bind(tfDept, "contato_dpt1");
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);										
						
						TextField tfDept = new TextField("Contato");
						tfDept.setStyleName("caption-align-fornecedores");
						tfDept.setWidth("400px");
						tfDept.setNullRepresentation("");
						
						addComponent(tfDept);
						fieldGroup.bind(tfDept, "dtpvendas");
					}
				});
				
				
				addComponent(new HorizontalLayout(){
					{
						setSizeUndefined();		
						
						
						FormLayout frmDDD_Fone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								TextField tfCod = new TextField("Telefone Principal");
								tfCod.setWidth("40px");
								tfCod.setStyleName("caption-align-fornecedores");
								tfCod.setNullRepresentation("");
																
								addComponent(tfCod);	
								fieldGroup.bind(tfCod, "ddd_tel_principal_dpt1");
							}
						};
						addComponent(frmDDD_Fone1);
						
						
						FormLayout frmFone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								TextField tfCod = new TextField();
								tfCod.setWidth("105px");
								tfCod.setStyleName("caption-align-empresa");
								tfCod.setNullRepresentation("");
								
								fieldGroup.bind(tfCod, "tel_principal_dpt1");
															
								addComponent(tfCod);						
							}
						};
						addComponent(frmFone1);
						
						
						
						FormLayout frmDDD_Fone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								TextField tfCod = new TextField("Telefone Alternativo");
								tfCod.setWidth("40px");							
								tfCod.setStyleName("caption-align-empresa");	
								tfCod.setNullRepresentation("");
								
								fieldGroup.bind(tfCod, "ddd_tel_alternativo_dpt1");
								
								addComponent(tfCod);						
							}
						};
						addComponent(frmDDD_Fone2);
						
						
						FormLayout frmFone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								TextField tfCod = new TextField();
								tfCod.setWidth("105px");							
								tfCod.setNullRepresentation("");
								tfCod.setMaxLength(10);
								fieldGroup.bind(tfCod, "tel_alternativo_dpt1");
								
								addComponent(tfCod);						
							}
						};
						addComponent(frmFone2);
						
					
						
					}
				});
				
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);										
						
						TextField tfDept = new TextField("E-mail");
						tfDept.setStyleName("caption-align-fornecedores");
						tfDept.setWidth("350px");
						tfDept.setNullRepresentation("");
						
						addComponent(tfDept);	
						fieldGroup.bind(tfDept, "email_dpt1");
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);										
						
						TextField tfDept = new TextField("Skype");
						tfDept.setStyleName("caption-align-fornecedores");
						tfDept.setWidth("350px");
						tfDept.setNullRepresentation("");
						
						addComponent(tfDept);		
						fieldGroup.bind(tfDept, "skype_dpt1");
					}
				});
				
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						addStyleName("margin-top");
						setMargin(true);
						setSpacing(true);										
						
						TextField tfDept = new TextField("DPTº");
						tfDept.setStyleName("caption-align-fornecedores");
						tfDept.setWidth("400px");
						tfDept.setNullRepresentation("");
						
						addComponent(tfDept);	
						fieldGroup.bind(tfDept, "contato_dpt2");
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);										
						
						TextField tfDept = new TextField("Contato");
						tfDept.setStyleName("caption-align-fornecedores");
						tfDept.setWidth("400px");
						tfDept.setNullRepresentation("");
						
						addComponent(tfDept);	
						fieldGroup.bind(tfDept, "dtpvendas2");
					}
				});
				
				
				addComponent(new HorizontalLayout(){
					{
						setSizeUndefined();		
						
						
						FormLayout frmDDD_Fone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								TextField tfCod = new TextField("Telefone Principal");
								tfCod.setWidth("40px");
								tfCod.setStyleName("caption-align-fornecedores");
								tfCod.setNullRepresentation("");
																
								addComponent(tfCod);		
								fieldGroup.bind(tfCod, "ddd_tel_principal_dpt2");
							}
						};
						addComponent(frmDDD_Fone1);
						
						
						FormLayout frmFone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								TextField tfCod = new TextField();
								tfCod.setWidth("105px");
								tfCod.setStyleName("caption-align-empresa");
								tfCod.setNullRepresentation("");
															
								addComponent(tfCod);	
								fieldGroup.bind(tfCod, "tel_principal_dpt2");
							}
						};
						addComponent(frmFone1);
						
						
						
						FormLayout frmDDD_Fone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								TextField tfCod = new TextField("Telefone Alternativo");
								tfCod.setWidth("40px");							
								tfCod.setStyleName("caption-align-empresa");	
								tfCod.setNullRepresentation("");
								
								addComponent(tfCod);	
								fieldGroup.bind(tfCod, "ddd_tel_alternativo_dpt2");
							}
						};
						addComponent(frmDDD_Fone2);
						
						
						FormLayout frmFone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								TextField tfCod = new TextField();
								tfCod.setWidth("105px");							
								tfCod.setNullRepresentation("");
								tfCod.setMaxLength(10);
								
								addComponent(tfCod);	
								fieldGroup.bind(tfCod, "tel_alternativo_dpt2");
							}
						};
						addComponent(frmFone2);
						
					
						
					}
				});
				
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);										
						
						TextField tfDept = new TextField("E-mail");
						tfDept.setStyleName("caption-align-fornecedores");
						tfDept.setWidth("350px");
						tfDept.setNullRepresentation("");
						
						addComponent(tfDept);		
						fieldGroup.bind(tfDept, "email_dpt2");
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);										
						
						TextField tfDept = new TextField("Skype");
						tfDept.setStyleName("caption-align-fornecedores");
						tfDept.setWidth("350px");
						tfDept.setNullRepresentation("");
						
						addComponent(tfDept);
						fieldGroup.bind(tfDept, "skype_dpt2");
					}
				});
			}				
		};
		
		
		TabSheet tbRoot = new TabSheet();
		tbRoot.setHeight("380px");
		tbRoot.setWidth("902px");
		tbRoot.addTab(vlPrincipal,"Principal");
		tbRoot.addTab(vlContatos,"Contatos");
		tbRoot.setStyleName("caption-align-fornecedores-bottom");
		
		vlRoot.addComponent(tbRoot);
	
	}
	
	public JPAContainer<Transportadoras> buildTransportadoraDefault(){
		JPAContainer<Transportadoras> containerTransportara = JPAContainerFactory.make(Transportadoras.class, ConnUtil.getEntity());
		containerTransportara.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		containerTransportara.addContainerFilter(Filters.eq("status", "ATIVO"));
		return containerTransportara;
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {			
					
				
				if(fieldGroup.isValid() && cnpjValid == true && cnpjCadast == true){
					try {					
						
						if(!fieldGroup.isModified()){
							close();
						}else{
							fieldGroup.commit();		
							fireEvent(new TransportadoraEvent(getUI(), itemFornecedor, true));
							if(itemFornecedor.getItemProperty("id").getValue() == null){
								Notification.show("Transportadora Cadastrada com Sucesso!");
							}else{
								Notification.show("Transportadora Alterada com Sucesso!");
							}
							close();						
						 }	
					} catch (CommitException e) {					
						e.printStackTrace();
						System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
						Notification.show("Não foi Possivel Salvar as Alterações!");
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
					close();
				}else{
					final GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
															
								if(fieldGroup.isValid() && cnpjValid == true && cnpjCadast == true){
									try {
										
										fieldGroup.commit();	
										fireEvent(new TransportadoraEvent(getUI(), itemFornecedor, true));
	
										if(itemFornecedor.getItemProperty("id").getValue() == null){
											Notification.show("Transportadora Cadastrada com Sucesso!");
										}else{
											Notification.show("Transportadora Alterada com Sucesso!");
										}
										
										close();
									} catch (CommitException e) {
										e.printStackTrace();
										System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause()); 
										Notification.show("Não foi Possivel Salvar as Alterações!");
									}
								}else{
									Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new TransportadoraEvent(getUI(), itemFornecedor, false));
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
	
	public void addListerner(TransportadoraListerner target){
		try {
			Method method = TransportadoraListerner.class.getDeclaredMethod("onClose", TransportadoraEvent.class);
			addListener(TransportadoraEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(TransportadoraListerner target){
		removeListener(TransportadoraEvent.class, target);
	}
	public static class TransportadoraEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public TransportadoraEvent(Component source, Item item, boolean confirm) {
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
	public interface TransportadoraListerner extends Serializable{
		public void onClose(TransportadoraEvent event);
	}
	
	
}
