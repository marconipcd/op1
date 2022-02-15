package com.digital.opuserp.view.modulos.cadastro.fornecedores;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.vaadin.suggestfield.SuggestField;
import org.vaadin.suggestfield.SuggestField.SuggestionHandler;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.domain.Cnae;
import com.digital.opuserp.domain.EmpresaFornecedor;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.CnpjUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.ValidarCPF;
import com.digital.opuserp.view.modulos.cadastro.fornecedores.AddEmpEditor.AddEmpresaEvent;
import com.digital.opuserp.view.util.CepUtil;
import com.digital.opuserp.view.util.CnaeUtil;
import com.digital.opuserp.view.util.MunicipiosUtil;
import com.digital.opuserp.view.util.MunicipiosUtil.MunicipioEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class FornecedorEditor extends Window implements GenericEditor {


	//JPAContainer<EmpresaFornecedor> containerEmpFornecedor;
	
	//Item itemFornecedor;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	//FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	
	private Float width_endereco;
	private Float width_numero;
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
	
		
	String bairro;
	Fornecedor fornecedor;

	ComboBox cbTransportadora;
	ComboBox cbStatus;
	TextField txtCpfCnpj;
	TextField txtInscEstadual;
	TextField txtRazaoSocial;
	TextField txtFantasia;	
	TextField txtDDDFone1;
	TextField txtFone1;
	TextField txtDDDFone2;
	TextField txtFone2;
	TextField txtDDDFone3;
	TextField txtFone3;
	TextField txtFone0800;
	TextField txtEmail;
	TextField txtSite;
	TextField txtCnaeFiscal;
	TextArea txtPrincipaisProdutos;
	TextArea txtObs;
	TextField txtLimiteCredito;
	TextField txtDpt;
	
	TextField txtContato1;
	TextField txtDDDFone1Contato;
	TextField txtFone1Contato;
	TextField txtDDDFone2Contato1;
	TextField txtFone2Contato1;
	TextField txtEmailContato1;
	TextField txtSkypeContato1;
	TextField txtDptContato1;
	
	TextField txtContato2;
	TextField txtDDDFone1Contato2;
	TextField txtFone1Contato2;
	TextField txtDDDFone2Contato2;
	TextField txtFone2Contato2;
	TextField txtEmailContato2;
	TextField txtSkypeContato2;
	TextField txtDptContato2;
	
	TextField txtRepresentante;
	TextField txtDDDFoneRepresentante;
	TextField txtFoneRepresentate;
	TextField txtDDDFoneRepresentante2;
	TextField txtFoneRepresentate2;	
	TextField txtDDDFoneRepresentante3;
	TextField txtFoneRepresentate3;
	TextField txtDDDFoneRepresentante4;
	TextField txtFoneRepresentante4;
	
	TextField txtEmailRepresentante;
	TextField txtSiteRepresentante;
	TextField txtSkypeRepresentante;
	TextArea txtObsRepresentante;	
	
	
	TextField txtCep;
	TextField txtEndereco;
	SuggestField txtBairro;
	TextField txtNumero;
	TextField txtCidade;
	TextField txtUf;
	TextField txtPais;
	TextField txtComplemento;
	TextField txtReferencia;
	
	ComboBox cbTipoFrete;
	
	public FornecedorEditor(Fornecedor fornecedor,String title, boolean modal){		
		getEnderecos();
		Styles styles = Page.getCurrent().getStyles();					        
        styles.add(".dashboard input.v-textfield-readonly { background-color: #FFF; }");
	
       this.fornecedor = fornecedor;
       
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
		
		if(fornecedor != null && fornecedor.getCnpj() != null){
			cnpjAtual  = fornecedor.getCnpj();
			cnpjValid = true;
			cnpjCadast = true;
		}else{
			cnpjValid = false;
			cnpjCadast = false;
			
		}
		
		if(fornecedor != null && fornecedor.getCnae_fiscal() != null){
			cnaeSelecionado = fornecedor.getCnae_fiscal();
		}else{
			cnaeSelecionado = null;
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

				setWidth("950px");
				setHeight("682px");
			
				width_numero = 0.1f;
				width_endereco = 0.53f;		
				width_rasão_social = "693px";
			
		
	}
	public void updateCep(Ceps cep){
		
		if(cep.getCep() != null){
			txtCep.setValue(cep.getCep().toUpperCase());	
		}
		
		if(cep.getEndereco() != null){
			txtEndereco.setValue(cep.getEndereco().toUpperCase());			
		}
		
		if(cep.getBairro() != null){
			txtBairro.setValue(cep.getBairro().toUpperCase());
			
			if(txtBairro.getValue() != null){
				bairro = txtBairro.getValue().toString();
			}
		}
		
		if(cep.getCidade() != null){
			txtCidade.setReadOnly(false);
			txtCidade.setValue(cep.getCidade().toUpperCase());
			txtCidade.setReadOnly(true);			
		}
		
		if(cep.getUf() != null){
			txtUf.setReadOnly(false);
			txtUf.setValue(cep.getUf().toUpperCase());
			txtUf.setReadOnly(true);
		}
		
		if(cep.getPais() != null){
			txtPais.setReadOnly(false);
			txtPais.setValue(cep.getPais().toUpperCase());
			txtPais.setReadOnly(true);
		}		
	}
	
	TextField tfCod;
	TextField txtDataCadastro;
	TextField txtDataAlteracao;
	
	
	public void buildLayout(){
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
									
						tfCod = new TextField("Código");				
						tfCod.setWidth("88px");				
						tfCod.setNullRepresentation("");
						tfCod.setStyleName("caption-align-fornecedores-top");						
						tfCod.setReadOnly(true);
						tfCod.setMaxLength(10);
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
						
						tfCod.setReadOnly(false);
						tfCod.setValue(fornecedor.getId() != null ? fornecedor.getId().toString() : "");
						tfCod.setReadOnly(true);
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
									
						txtDataCadastro = new TextField ("Cadastro");
						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");				
						if(fornecedor.getData_cadastro() != null){
							txtDataCadastro.setValue(sdf.format(fornecedor.getData_cadastro()));
						}else{
							txtDataCadastro.setValue(sdf.format(new Date()));
						}
						
						txtDataCadastro.setWidth("130px");				
						txtDataCadastro.setNullRepresentation("");						
						txtDataCadastro.setReadOnly(true);
						txtDataCadastro.setMaxLength(10);
						addComponent(txtDataCadastro);
						setExpandRatio(txtDataCadastro, 2);
						
						txtDataCadastro.setReadOnly(false);
						txtDataCadastro.setValue(DataUtil.formatDateBra(fornecedor.getData_cadastro()));
						txtDataCadastro.setReadOnly(true);
					}
				});
					
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
										
						txtDataAlteracao = new TextField ("Alteração");	
						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");				
						if(fornecedor.getData_alteracao() != null){
							txtDataAlteracao.setValue(sdf.format(fornecedor.getData_alteracao()));
						}else{
							txtDataAlteracao.setValue(sdf.format(new Date()));
						}
						
						txtDataAlteracao.setWidth("130px");				
						txtDataAlteracao.setNullRepresentation("");						
						txtDataAlteracao.setReadOnly(true);
						txtDataAlteracao.setMaxLength(10);
						addComponent(txtDataAlteracao);
						setExpandRatio(txtDataAlteracao, 2);
						
						txtDataAlteracao.setReadOnly(false);
						txtDataAlteracao.setValue(DataUtil.formatDateBra(fornecedor.getData_alteracao()));
						txtDataAlteracao.setReadOnly(true);
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						cbStatus = new ComboBox("Status");
						cbStatus.setNullSelectionAllowed(false);
						cbStatus.addItem("ATIVO");						
						cbStatus.addItem("INATIVO");
						cbStatus.setWidth("115px");
						cbStatus.select("ATIVO");
						
						cbStatus.setStyleName("caption-align-status");
						cbStatus.setRequired(true);
						
						addComponent(cbStatus);

						if(fornecedor.getStatus().equals("EM DIGITACAO")){
							cbStatus.select("ATIVO");
						}else{
							cbStatus.select(fornecedor.getStatus());
						}
						
					}
				});
					
			}	
		});

		vlRoot.addComponent(new HorizontalLayout(){
					{
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
												
								txtCpfCnpj = new TextField("CPF/CNPJ");				
								txtCpfCnpj.setWidth("150px");				
								txtCpfCnpj.setStyleName("caption-align-fornecedores-top");
								txtCpfCnpj.setNullRepresentation("");
								txtCpfCnpj.setRequired(true);
								txtCpfCnpj.setMaxLength(14);
								txtCpfCnpj.focus();		
								txtCpfCnpj.setTextChangeEventMode(TextChangeEventMode.LAZY);
								txtCpfCnpj.addListener(new FieldEvents.TextChangeListener() {
									
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
								
								txtCpfCnpj.addListener(new FieldEvents.BlurListener() {
									
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
								
								
									
								addComponent(txtCpfCnpj);
								
								txtCpfCnpj.setValue(fornecedor.getCnpj());

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
										
								txtInscEstadual = new TextField("Inscrição Estadual/RG");
								txtInscEstadual.setWidth("338px");
								txtInscEstadual.setNullRepresentation("");
								txtInscEstadual.setMaxLength(45);

								addComponent(txtInscEstadual);

								txtInscEstadual.setValue(fornecedor.getInsc_estadual());
							}
						});
						
					}
				}
		);	
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					txtRazaoSocial = new TextField("Razão Social");				
					txtRazaoSocial.setWidth(width_rasão_social);				
					txtRazaoSocial.setStyleName("caption-align-fornecedores-top");
					txtRazaoSocial.setNullRepresentation("");
					txtRazaoSocial.setMaxLength(200);
					
					txtRazaoSocial.setRequired(true);
					
					addComponent(txtRazaoSocial);
					setExpandRatio(txtRazaoSocial, 2);				
					
					txtRazaoSocial.setValue(fornecedor.getRazao_social());
				}
			});	
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);				
									
					txtFantasia = new TextField("Fantasia");				
					txtFantasia.setWidth("340px");				
					txtFantasia.setStyleName("caption-align-fornecedores-top");
					txtFantasia.setNullRepresentation("");
					txtFantasia.setMaxLength(200);
					
					txtFantasia.setRequired(true);
					
					addComponent(txtFantasia);
					setExpandRatio(txtFantasia, 2);						
					
					txtFantasia.setValue(fornecedor.getFantasia()); 
				}
			});	
		
		VerticalLayout vlPrincipal = new VerticalLayout(){
			{
				setMargin(true);
				setStyleName("horizontal-tabsheet");
				addComponent(
						new HorizontalLayout(){
							{	
								addComponent(new FormLayout(){					
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");						
														
										txtCep = new TextField("CEP");				
										txtCep.setWidth("80px");				
										txtCep.setStyleName("caption-align-fornecedores");
										txtCep.setNullRepresentation("");
										txtCep.setMaxLength(8);
										txtCep.setRequired(true);
										txtCep.setTextChangeEventMode(TextChangeEventMode.LAZY);
										txtCep.addListener(new FieldEvents.TextChangeListener() {
											
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
																														
										addComponent(txtCep);
										setExpandRatio(txtCep, 2);		
										
										txtCep.setValue(fornecedor.getCep());
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
													//((TextField) fieldGroup.getField("numero")).focus();												
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

								txtEndereco = new TextField("Endereço");
								txtEndereco.setWidth("100%");
								txtEndereco.setNullRepresentation("");
								txtEndereco.setStyleName("caption-align-fornecedores");
								txtEndereco.setRequired(true);
								txtEndereco.setMaxLength(100);
								txtEndereco.addBlurListener(new FieldEvents.BlurListener() {
									
									@Override
									public void blur(BlurEvent event) {
										if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
											
											((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
										}
									}
								});
								
								addComponent(txtEndereco);
								
								txtEndereco.setValue(fornecedor.getEndereco());

							}
						};
						
						addComponent(frmEndereco);
						setExpandRatio(frmEndereco, width_endereco);
					
						FormLayout frmNumero = new FormLayout(){
							{
								
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");						
												
								txtNumero = new TextField("Número");				
								txtNumero.setWidth("54px");				
								txtNumero.setNullRepresentation("");
								txtNumero.setRequired(true);
								txtNumero.setMaxLength(10);
								addComponent(txtNumero);
								setExpandRatio(txtNumero, 2);
								
								txtNumero.setValue(fornecedor.getNumero());
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
								
								txtBairro = new SuggestField();
								txtBairro.setCaption("Bairro");
								txtBairro.setSuggestionHandler(new SuggestionHandler() {
									@Override
									public List<Object> searchItems(String query) {
										
										bairro = query.toString();
										return handleSearchQuery(query);
									}
								});					
						
								if(txtBairro.getValue() != null){
									bairro = txtBairro.getValue().toString();
								}
								
								txtBairro.setWidth("200px");				
								
								txtBairro.setStyleName("caption-align-fornecedores");
								txtBairro.setRequired(true);

									
																
								addComponent(txtBairro);
								setExpandRatio(txtBairro, 2);
								
								txtBairro.setValue(fornecedor.getBairro());
							}
						});


						addComponent(new HorizontalLayout(){
							{
											
								txtCidade = new TextField("Cidade");				
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
								
								txtCidade.setReadOnly(false);
								txtCidade.setValue(fornecedor.getCidade());
								txtCidade.setReadOnly(true);
								
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
												
								txtUf = new TextField("UF");				
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
								
								txtUf.setReadOnly(false);
								txtUf.setValue(fornecedor.getUf());
								txtUf.setReadOnly(true);
							}
						});
						
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");						
												
								txtPais = new TextField("Pais");				
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
								
								txtPais.setReadOnly(false); 
								txtPais.setValue(fornecedor.getPais());
								txtPais.setReadOnly(true); 
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
												
								txtComplemento = new TextField("Complemento");				
								txtComplemento.setWidth("100%");				
								txtComplemento.setNullRepresentation("");
								txtComplemento.setStyleName("caption-align-fornecedores");
								txtComplemento.setRequired(false);
								txtComplemento.setMaxLength(100);
								txtComplemento.addBlurListener(new FieldEvents.BlurListener() {
									
									@Override
									public void blur(BlurEvent event) {
										if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
											
											((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
										}
									}
								});
								addComponent(txtComplemento);
								setExpandRatio(txtComplemento, 2);
								
								txtComplemento.setValue(fornecedor.getComplemento());
							}
						});
						
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");						
												
								txtReferencia = new TextField("Referência");				
								txtReferencia.setWidth("351px");				
								txtReferencia.setNullRepresentation("");
								txtReferencia.setRequired(false);
								txtReferencia.setMaxLength(100);
								txtReferencia.addBlurListener(new FieldEvents.BlurListener() {
									
									@Override
									public void blur(BlurEvent event) {
										if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
											
											((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
										}
									}
								});
								
								addComponent(txtReferencia);
								setExpandRatio(txtReferencia, 2);
								
								txtReferencia.setValue(fornecedor.getPonto_referencia());
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
								
								txtDDDFone1 = new TextField("Telefone Principal");
								txtDDDFone1.setWidth("40px");
								txtDDDFone1.setRequired(true);
								txtDDDFone1.setStyleName("caption-align-fornecedores");
								txtDDDFone1.setNullRepresentation("");
								txtDDDFone1.setMaxLength(2);
								
								addComponent(txtDDDFone1);			
								
								txtDDDFone1.setValue(fornecedor.getDdd_fone1());
							}
						};
						addComponent(frmDDD_Fone1);
						
						
						FormLayout frmFone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								txtFone1 = new TextField();
								txtFone1.setWidth("105px");
								txtFone1.setRequired(true);
								txtFone1.setNullRepresentation("");
								txtFone1.setMaxLength(10);
								
								
								addComponent(txtFone1);		
								
								txtFone1.setValue(fornecedor.getFone1());
							}
						};
						addComponent(frmFone1);
						
						
						
						FormLayout frmDDD_Fone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								txtDDDFone2 = new TextField("Telefone Alternativo");
								txtDDDFone2.setWidth("40px");							
								txtDDDFone2.setStyleName("caption-align-empresa");
								txtDDDFone2.setNullRepresentation("");
								txtDDDFone2.setMaxLength(2);
								
								addComponent(txtDDDFone2);				
								
								txtDDDFone2.setValue(fornecedor.getDdd_fone2());
							}
						};
						addComponent(frmDDD_Fone2);
						
						
						FormLayout frmFone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								txtFone2 = new TextField();
								txtFone2.setWidth("105px");							
								txtFone2.setNullRepresentation("");
								txtFone2.setMaxLength(10);
								
								addComponent(txtFone2);				
								
								txtFone2.setValue(fornecedor.getFone2());
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
								
								txtDDDFone3 = new TextField("Telefone Alternativo");
								txtDDDFone3.setWidth("40px");							
								txtDDDFone3.setStyleName("caption-align-fornecedores");
								txtDDDFone3.setNullRepresentation("");
								txtDDDFone3.setMaxLength(2);
								
								addComponent(txtDDDFone3);				
								
								txtDDDFone3.setValue(fornecedor.getDdd_fone3());
							}
						};
						addComponent(frmDDD_Fone1);
						
						
						FormLayout frmFone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								txtFone3 = new TextField();
								txtFone3.setWidth("105px");							
								txtFone3.setNullRepresentation("");
								txtFone3.setMaxLength(10);
								
								addComponent(txtFone3);					
								
								txtFone3.setValue(fornecedor.getFone3());
							}
						};
						addComponent(frmFone1);
						
									
						
						FormLayout frmFone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								txtFone0800 = new TextField("0800");
								txtFone0800.setWidth("105px");							
								txtFone0800.setNullRepresentation("");
								txtFone0800.setStyleName("caption-align-empresa");
								txtFone0800.setMaxLength(11);
								
								addComponent(txtFone0800);			
								
								txtFone0800.setValue(fornecedor.getFone0800());
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
													
												
								txtEmail = new TextField("E-mail");				
								txtEmail.setWidth("100%");				
								txtEmail.setStyleName("caption-align-fornecedores");
								txtEmail.setNullRepresentation("");
								
								txtEmail.setRequired(false);
								txtEmail.setMaxLength(45);
								
								addComponent(txtEmail);				
								
								txtEmail.setValue(fornecedor.getEmail());
							}
						});
						
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
												
												
								txtSite = new TextField("Site");				
												
								txtSite.setWidth("100%");
								txtSite.setNullRepresentation("");
								
								txtSite.setMaxLength(45);
								
								addComponent(txtSite);		
								
								txtSite.setValue(fornecedor.getHome_page());
							}
						});
						
						
					}
				});
				
				
				addComponent(new HorizontalLayout(){
					{						
						boolean preencher = false;
						if(fornecedor.getId()!= null && fornecedor.getCnae_fiscal() != null){
							preencher = true;
						}
						
						txtCnaeFiscal = new TextField("CNAE Fiscal");				
						txtCnaeFiscal.setWidth("75px");				
						txtCnaeFiscal.setNullRepresentation("");
						txtCnaeFiscal.setStyleName("caption-align-fornecedores");
						txtCnaeFiscal.setRequired(true);		
						
						final TextField tfDescricaoCnae = new TextField();
						tfDescricaoCnae.setReadOnly(true);
						tfDescricaoCnae.setWidth("450px");
						
						
						
						if(cnaeSelecionado != null){							
							
								txtCnaeFiscal.setValue(cnaeSelecionado.getId().toString());
								tfDescricaoCnae.setReadOnly(false);
								tfDescricaoCnae.setValue(cnaeSelecionado.getDescricao());
								tfDescricaoCnae.setReadOnly(true);
							
						}
						
						
						final Button btSearchCnae = new Button();
						btSearchCnae.setStyleName(BaseTheme.BUTTON_LINK);
						btSearchCnae.setIcon(new ThemeResource("icons/search-16.png"));
						btSearchCnae.setDescription("Pesquisar CNAE");
						btSearchCnae.addListener(new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								
								CnaeUtil cUtil = new CnaeUtil(true, true);
								cUtil.addListerner(new CnaeUtil.CnaeListerner() {
									
									@Override
									public void onSelected(
											com.digital.opuserp.view.util.CnaeUtil.CnaeEvent event) {
										if(event.getCnae() != null){
											txtCnaeFiscal.setValue(event.getCnae().getCodigo());
											tfDescricaoCnae.setReadOnly(false);
											tfDescricaoCnae.setValue(event.getCnae().getDescricao());
											tfDescricaoCnae.setReadOnly(true);
											cnaeSelecionado = event.getCnae();
										}
										
									}
								});
								
								getUI().addWindow(cUtil);
								
							}
						});
									
						
						FormLayout frmCodigoCnae = new FormLayout(){
							{
								
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");		
														
								addComponent(txtCnaeFiscal);							
							}
						};
						addComponent(frmCodigoCnae);
						
						
						FormLayout frmButtonSearchCnae =new FormLayout(){
							{
								
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_error_cell");										
								addComponent(btSearchCnae);							
							}
						}; 
						
						
						FormLayout frmDescCnae = new FormLayout(){
							{
								
								setMargin(true);
								setSpacing(true);						
								setStyleName("form-cutom");							
								addComponent(tfDescricaoCnae);							
							}
						}; 
						addComponent(frmButtonSearchCnae);
						addComponent(frmDescCnae);
						setExpandRatio(frmDescCnae, 1);
						
						
						
					}	
				});
				
				
					
				hlFreteTransportadora = new HorizontalLayout();
				
				
			
				FormLayout frmTipoFrete = new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);											
								
						
						cbTipoFrete = new ComboBox("Tipo de Frete");										
						cbTipoFrete.setWidth("100%");
						cbTipoFrete.setNullSelectionAllowed(false);
						cbTipoFrete.addItem("POR CONTA DO EMITENTE");
						cbTipoFrete.addItem("POR CONTA DO DESTINATÁRIO");
						cbTipoFrete.setStyleName("caption-align-fornecedores");
						cbTipoFrete.setRequired(true);
						cbTipoFrete.setImmediate(true);
						cbTipoFrete.addListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(event.getProperty().toString() != "" && pode){
									tipo_frete = cbTipoFrete.getValue().toString();
									
									if(tipo_frete.equals("POR CONTA DO EMITENTE")){
										hlFreteTransportadora.replaceComponent(frmTransportadoraPadrao, buildFormTransportadora(false));										
									}else{										
										hlFreteTransportadora.replaceComponent(frmTransportadoraPadrao, buildFormTransportadora(true));										
									}
									
								}
							}
						});
						

						addComponent(cbTipoFrete);
						
						cbTipoFrete.select(fornecedor.getTipo_de_frete());
						
						
					}
				};
				
				hlFreteTransportadora.setWidth("100%");
				hlFreteTransportadora.addComponent(frmTipoFrete);
				
				if(fornecedor.getTransportadora_padrao_id() != null){
					hlFreteTransportadora.addComponent(buildFormTransportadora(true));
				}else{
					hlFreteTransportadora.addComponent(buildFormTransportadora(false));
				}
				pode = true;
				addComponent(hlFreteTransportadora);
				
				

			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					
					addComponent(
							new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
												
												
								txtPrincipaisProdutos = new TextArea("Principais Produtos");										
								txtPrincipaisProdutos.setWidth("100%");	
								txtPrincipaisProdutos.setHeight("60px");
								txtPrincipaisProdutos.setNullRepresentation("");
								txtPrincipaisProdutos.setStyleName("caption-align-fornecedores");
								txtPrincipaisProdutos.addStyleName("text-area-border");
								txtPrincipaisProdutos.setRequired(false);
								addComponent(txtPrincipaisProdutos);	
								
								txtPrincipaisProdutos.setValue(fornecedor.getPrincipais_produtos());
								
							}
						});	
					
					
					addComponent(
							new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
														
												
								txtObs = new TextArea("Obs.:");										
								txtObs.setWidth("100%");	
								txtObs.setHeight("60px");								
								txtObs.addStyleName("text-area-border");
								txtObs.setRequired(false);
								txtObs.setNullRepresentation("");
								addComponent(txtObs);	
								
								txtObs.setValue(fornecedor.getObservacao());
								
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
											
							txtLimiteCredito = new TextField("Limite de Crédito($)");														
							txtLimiteCredito.setWidth("150px");				
							txtLimiteCredito.setNullRepresentation("");
							txtLimiteCredito.setStyleName("textfield-fornecedor-align");
							txtLimiteCredito.setRequired(false);							
							txtLimiteCredito.setImmediate(true);
							txtLimiteCredito.setId("limiteCreditoFornecedor");
								
							JavaScript.getCurrent().execute("$('#limiteCreditoFornecedor').maskMoney({decimal:',',thousands:'.'})");
							
							addComponent(txtLimiteCredito);
							setExpandRatio(txtLimiteCredito, 2);
							
							txtLimiteCredito.setValue(fornecedor.getLimite_de_credito());
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
						
						txtDpt = new TextField("DPTº");
						txtDpt.setStyleName("caption-align-fornecedores");
						txtDpt.setWidth("400px");
						txtDpt.setNullRepresentation("");
						
						addComponent(txtDpt);		
						
						
						txtDpt.setValue(fornecedor.getDtpvendas());
						
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);										
						
						txtContato1 = new TextField("Contato");
						txtContato1.setStyleName("caption-align-fornecedores");
						txtContato1.setWidth("400px");
						txtContato1.setNullRepresentation("");
						
						addComponent(txtContato1);
						
						txtContato1.setValue(fornecedor.getContato_dpt1());
						
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
								
								txtDDDFone1Contato = new TextField("Telefone Principal");
								txtDDDFone1Contato.setWidth("40px");
								txtDDDFone1Contato.setStyleName("caption-align-fornecedores");
								txtDDDFone1Contato.setNullRepresentation("");
																
								addComponent(txtDDDFone1Contato);	
								
								txtDDDFone1Contato.setValue(fornecedor.getDdd_fone1());
							}
						};
						addComponent(frmDDD_Fone1);
						
						
						FormLayout frmFone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								txtFone1Contato = new TextField();
								txtFone1Contato.setWidth("105px");
								txtFone1Contato.setStyleName("caption-align-empresa");
								txtFone1Contato.setNullRepresentation("");
												
								addComponent(txtFone1Contato);				
								
								txtFone1Contato.setValue(fornecedor.getFone1());
							}
						};
						addComponent(frmFone1);
						
						
						
						FormLayout frmDDD_Fone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								txtDDDFone2Contato1 = new TextField("Telefone Alternativo");
								txtDDDFone2Contato1.setWidth("40px");							
								txtDDDFone2Contato1.setStyleName("caption-align-empresa");	
								txtDDDFone2Contato1.setNullRepresentation("");
								
								addComponent(txtDDDFone2Contato1);				
								
								txtDDDFone2Contato1.setValue(fornecedor.getDdd_fone2());
							}
						};
						addComponent(frmDDD_Fone2);
						
						
						FormLayout frmFone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								txtFone2Contato1 = new TextField();
								txtFone2Contato1.setWidth("105px");							
								txtFone2Contato1.setNullRepresentation("");
								txtFone2Contato1.setMaxLength(10);
								
								addComponent(txtFone2Contato1);			
								
								txtFone2Contato1.setValue(fornecedor.getFone2());
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
						
						txtEmailContato1 = new TextField("E-mail");
						txtEmailContato1.setStyleName("caption-align-fornecedores");
						txtEmailContato1.setWidth("350px");
						txtEmailContato1.setNullRepresentation("");
						
						addComponent(txtEmailContato1);	
						
						
						txtEmailContato1.setValue(fornecedor.getEmail_dpt1());
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);										
						
						txtSkypeContato1 = new TextField("Skype");
						txtSkypeContato1.setStyleName("caption-align-fornecedores");
						txtSkypeContato1.setWidth("350px");
						txtSkypeContato1.setNullRepresentation("");
						
						addComponent(txtSkypeContato1);		
						
						txtSkypeContato1.setValue(fornecedor.getSkype_dpt1());
						
					}
				});
				
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						addStyleName("margin-top");
						setMargin(true);
						setSpacing(true);										
						
						txtDptContato1 = new TextField("DPTº");
						txtDptContato1.setStyleName("caption-align-fornecedores");
						txtDptContato1.setWidth("400px");
						txtDptContato1.setNullRepresentation("");
						
						addComponent(txtDptContato1);	
						
						txtDptContato1.setValue(fornecedor.getContato_dpt1());
						
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);										
						
						txtContato2 = new TextField("Contato");
						txtContato2.setStyleName("caption-align-fornecedores");
						txtContato2.setWidth("400px");
						txtContato2.setNullRepresentation("");
						
						addComponent(txtContato2);	
						
						txtContato2.setValue(fornecedor.getContato_dpt2());
						
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
								
								txtDDDFone1Contato2 = new TextField("Telefone Principal");
								txtDDDFone1Contato2.setWidth("40px");
								txtDDDFone1Contato2.setStyleName("caption-align-fornecedores");
								txtDDDFone1Contato2.setNullRepresentation("");
																
								addComponent(txtDDDFone1Contato2);		
								
								txtDDDFone1Contato2.setValue(fornecedor.getFone2());
								
							}
						};
						addComponent(frmDDD_Fone1);
						
						
						FormLayout frmFone1 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								txtFone2Contato2 = new TextField();
								txtFone2Contato2.setWidth("105px");
								txtFone2Contato2.setStyleName("caption-align-empresa");
								txtFone2Contato2.setNullRepresentation("");
															
								addComponent(txtFone2Contato2);	
								
								txtFone2Contato2.setValue(fornecedor.getFone2());
								
							}
						};
						addComponent(frmFone1);
						
						
						
						FormLayout frmDDD_Fone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								txtDDDFone2Contato2 = new TextField("Telefone Alternativo");
								txtDDDFone2Contato2.setWidth("40px");							
								txtDDDFone2Contato2.setStyleName("caption-align-empresa");	
								txtDDDFone2Contato2.setNullRepresentation("");
								
								addComponent(txtDDDFone2Contato2);	
								
								txtDDDFone2Contato2.setValue(fornecedor.getDdd_fone2());
								
							}
						};
						addComponent(frmDDD_Fone2);
						
						
						FormLayout frmFone2 = new FormLayout(){
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom_hide_require");
								
								txtFone2Contato2 = new TextField();
								txtFone2Contato2.setWidth("105px");							
								txtFone2Contato2.setNullRepresentation("");
								txtFone2Contato2.setMaxLength(10);
								
								addComponent(txtFone2Contato2);	
								
								txtFone2Contato2.setValue(fornecedor.getFone4());
								
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
						
						txtEmailContato2= new TextField("E-mail");
						txtEmailContato2.setStyleName("caption-align-fornecedores");
						txtEmailContato2.setWidth("350px");
						txtEmailContato2.setNullRepresentation("");
						
						addComponent(txtEmailContato2);		
						
						txtEmailContato2.setValue(fornecedor.getEmail_dpt2());
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);										
						
						txtSkypeContato2 = new TextField("Skype");
						txtSkypeContato2.setStyleName("caption-align-fornecedores");
						txtSkypeContato2.setWidth("350px");
						txtSkypeContato2.setNullRepresentation("");
						
						addComponent(txtSkypeContato2);
						
						txtSkypeContato2.setValue(fornecedor.getSkype_dpt2());
					}
				});
			}				
		};
		
		
		VerticalLayout vlRepresentante = new VerticalLayout(){
			{
				setMargin(true);
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
										
										
						txtRepresentante = new TextField("Representante");						
						txtRepresentante.setWidth("100%");				
						txtRepresentante.setNullRepresentation("");
						txtRepresentante.setStyleName("caption-align-fornecedores");
						txtRepresentante.setRequired(false);
						txtRepresentante.setMaxLength(200);
						addComponent(txtRepresentante);
						setExpandRatio(txtRepresentante, 2);
						
						txtRepresentante.setValue(fornecedor.getRepresentante());
					}
				});
			
	
				addComponent(new HorizontalLayout(){
					{
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
								
								txtDDDFoneRepresentante = new TextField("Telefone Principal");
								txtDDDFoneRepresentante.setNullRepresentation("");
								txtDDDFoneRepresentante.setStyleName("caption-align-fornecedores");
								txtDDDFoneRepresentante.setRequired(false);
								txtDDDFoneRepresentante.setMaxLength(2);
								txtDDDFoneRepresentante.setWidth("40px");
								
								addComponent(txtDDDFoneRepresentante);
								
								txtDDDFoneRepresentante.setValue(fornecedor.getDdd_fone1_representante());
							}
						});
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");

												
												
								txtFoneRepresentate = new TextField();			
								txtFoneRepresentate.setWidth("100px");				
								txtFoneRepresentate.setNullRepresentation("");

								txtFoneRepresentate.setMaxLength(10);
								addComponent(txtFoneRepresentate);
								setExpandRatio(txtFoneRepresentate, 2);
								
								txtFoneRepresentate.setValue(fornecedor.getFone1_representante());
							}
						});
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
								
								txtDDDFoneRepresentante2 = new TextField("Telefone Alternativo");
								txtDDDFoneRepresentante2.setWidth("40px");
								txtDDDFoneRepresentante2.setNullRepresentation("");
								txtDDDFoneRepresentante2.setMaxLength(2);
								
								addComponent(txtDDDFoneRepresentante2);
								
								txtDDDFoneRepresentante2.setValue(fornecedor.getDdd_fone2_representante());
							}
						});
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
											
								txtFoneRepresentate2 = new TextField();				
								txtFoneRepresentate2.setWidth("100px");		
								txtFoneRepresentate2.setNullRepresentation("");
								txtFoneRepresentate2.setRequired(false);
								txtFoneRepresentate2.setMaxLength(10);
								addComponent(txtFoneRepresentate2);
								setExpandRatio(txtFoneRepresentate2, 2);
								
								txtFoneRepresentate2.setValue(fornecedor.getFone2_representante());
							}
						});	
	
					}	
				});
				
				addComponent(new HorizontalLayout(){
					{
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								txtDDDFoneRepresentante3 = new TextField("Telefone Alternativo");
								txtDDDFoneRepresentante3.setWidth("40px");
								txtDDDFoneRepresentante3.setNullRepresentation("");
								txtDDDFoneRepresentante3.setStyleName("caption-align-fornecedores");
								txtDDDFoneRepresentante3.setMaxLength(2);
								
								addComponent(txtDDDFoneRepresentante3);
								
								txtDDDFoneRepresentante3.setValue(fornecedor.getDdd_fone3_representante());
							}
						});

						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
												
								txtFoneRepresentate3 = new TextField();				
								txtFoneRepresentate3.setWidth("100px");			
								txtFoneRepresentate3.setNullRepresentation("");
								txtFoneRepresentate3.setRequired(false);
								txtFoneRepresentate3.setMaxLength(10);
								addComponent(txtFoneRepresentate3);
								setExpandRatio(txtFoneRepresentate3, 2);
								
								txtFoneRepresentate3.setValue(fornecedor.getFone3_representante());
							}
						});	
	
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								txtDDDFoneRepresentante4 = new TextField("Telefone Alternativo");
								txtDDDFoneRepresentante4.setNullRepresentation("");
								txtDDDFoneRepresentante4.setMaxLength(2);
								txtDDDFoneRepresentante4.setWidth("40px");
								
								addComponent(txtDDDFoneRepresentante4);
								
								txtDDDFoneRepresentante4.setValue(fornecedor.getDdd_fone4_representante());
							}
						});
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");

								txtFoneRepresentante4 = new TextField();				
								txtFoneRepresentante4.setWidth("100px");			
								txtFoneRepresentante4.setNullRepresentation("");								
								txtFoneRepresentante4.setRequired(false);
								txtFoneRepresentante4.setMaxLength(10);
								addComponent(txtFoneRepresentante4);
								setExpandRatio(txtFoneRepresentante4, 2);
								
								txtFoneRepresentante4.setValue(fornecedor.getFone4_representante());
							}
						});
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
													
												
								txtEmailRepresentante = new TextField("Email");				
								txtEmailRepresentante.setWidth("100%");				
								txtEmailRepresentante.setNullRepresentation("");
								txtEmailRepresentante.setRequired(false);
								txtEmailRepresentante.setMaxLength(50);
								txtEmailRepresentante.setStyleName("caption-align-fornecedores");
								addComponent(txtEmailRepresentante);
								setExpandRatio(txtEmailRepresentante, 2);
								
								txtEmailRepresentante.setValue(fornecedor.getEmail_representante());
							}
						});	
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
								
								txtSiteRepresentante = new TextField("Site");
								txtSiteRepresentante.setWidth("100%");
								txtSiteRepresentante.setNullRepresentation("");
								txtSiteRepresentante.setMaxLength(200);
								
								addComponent(txtSiteRepresentante);
								
								txtSiteRepresentante.setValue(fornecedor.getSite_representante());
								
							}
						});
					}
				});		
					
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);													
												
								txtSkypeRepresentante = new TextField("Skype");				
								txtSkypeRepresentante.setWidth("37.2%");				
								txtSkypeRepresentante.setNullRepresentation("");
								txtSkypeRepresentante.setRequired(false);
								txtSkypeRepresentante.setMaxLength(50);
								txtSkypeRepresentante.setStyleName("caption-align-fornecedores");
								addComponent(txtSkypeRepresentante);
								setExpandRatio(txtSkypeRepresentante, 2);
								
								txtSkypeRepresentante.setValue(fornecedor.getMsn_representante());
							}
						});	
						
						
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);													
											
								txtObsRepresentante = new TextArea("Obs");
										
								txtObsRepresentante.setWidth("100%");
								txtObsRepresentante.setHeight("50px");
								txtObsRepresentante.setNullRepresentation("");
								txtObsRepresentante.setRequired(false);								
								txtObsRepresentante.setStyleName("caption-align-fornecedores");
								txtObsRepresentante.addStyleName("text-area-border");
								addComponent(txtObsRepresentante);
								
								txtObsRepresentante.setValue(fornecedor.getObservacao_representante());
								
							}
						});	
			}
		};
		
		final VerticalLayout vlEmpresas = new VerticalLayout(){
			{
				setMargin(true);
				
				
				if(fornecedor.getStatus().equals("EM DIGITACAO")){
					EntityManager em = ConnUtil.getEntity();
					
					em.getTransaction().begin();
					em.persist(new EmpresaFornecedor(null, OpusERP4UI.getEmpresa(), fornecedor));
					em.getTransaction().commit();
				}
				
				
				Button btAddEmpresa = new Button("Adicionar Empresa", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						//final Fornecedor fornecedor = FornecedorDAO.find(Integer.parseInt(itemFornecedor.getItemProperty("id").toString()));
						AddEmpEditor winAddEmpresa = new AddEmpEditor(true, true, fornecedor);
						winAddEmpresa.addListerner(new AddEmpEditor.AddEmpresaListerner() {
							
							@Override
							public void onSelected(AddEmpresaEvent event) {
								 if(event.getEmpresa() != null){
									 EmpresaFornecedor empFornecedor = new EmpresaFornecedor(null, event.getEmpresa(), fornecedor);
									 
									 EntityManager em = ConnUtil.getEntity();
									 em.getTransaction().begin();
									 em.persist(empFornecedor);
									 em.getTransaction().commit();
									  
									 atualizarListaEmpresas();
									 
								 }
							}
						});
						
						getUI().addWindow(winAddEmpresa);
					}
				});
				
				addComponent(btAddEmpresa); 
				
				addComponent(buildTbEmpresas()); 
				
			}
		};
		
		final TabSheet tbRoot = new TabSheet();
		tbRoot.setHeight("422px");
		tbRoot.setWidth("902px");
		
		tbRoot.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if(event.getTabSheet().getSelectedTab() != null){
									
					if(txtBairro.getValue()!=null && bairro != null){
						txtBairro.setValue(bairro.toUpperCase());
					}
				}				
			}
		});
		tbRoot.addTab(vlPrincipal,"Principal");
		tbRoot.addTab(vlContatos,"Contatos");
		tbRoot.addTab(vlRepresentante,"Representante");
		tbRoot.addTab(vlEmpresas,"Empresas");
		tbRoot.setStyleName("caption-align-fornecedores-bottom");
		
    	vlRoot.addComponent(tbRoot);
	
	}
	
	Table tbEmpresas;
	private Table buildTbEmpresas(){
		
		tbEmpresas = new Table();
		tbEmpresas.setSizeFull();
		tbEmpresas.addContainerProperty("Cod", Integer.class, 0);
		tbEmpresas.addContainerProperty("Empresa", String.class, "");
				
		tbEmpresas.addGeneratedColumn("r", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source,final Object itemId, Object columnId) {

				Button btRemove = new Button("Remover", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						if(tbEmpresas.getItemIds().size() > 1){						
							EntityManager em = ConnUtil.getEntity();
							em.getTransaction().begin();
							em.remove(em.find(EmpresaFornecedor.class, Integer.parseInt(source.getItem(itemId).getItemProperty("Cod").getValue().toString())));
							em.getTransaction().commit();							
							
							atualizarListaEmpresas();
						}else{
							Notify.Show("Não é possível Remover todas as Empresas!", Notify.TYPE_WARNING);
						}
					}
				});
				btRemove.setStyleName(Reindeer.BUTTON_LINK);
				
				return btRemove;
			}
		});
		
		if(fornecedor.getId() != null){
		atualizarListaEmpresas();
		}
		
		return tbEmpresas;
	}
	
	private void atualizarListaEmpresas(){
		if(tbEmpresas != null && fornecedor != null){
			
			tbEmpresas.removeAllItems();
			
			EntityManager em = ConnUtil.getEntity();
			Query qEmpresas = em.createQuery("select ef from EmpresaFornecedor ef where ef.fornecedor=:fornecedor", EmpresaFornecedor.class);
			qEmpresas.setParameter("fornecedor", fornecedor);
			
			for (EmpresaFornecedor ef : (List<EmpresaFornecedor>)qEmpresas.getResultList()) {
				tbEmpresas.addItem(new Object[]{ef.getId(),ef.getEmpresa().getNome_fantasia()}, tbEmpresas.getItemIds().size() + 1);
			}
		}
	}
	

	
	public JPAContainer<Fornecedor> buildTransportadoraDefault(){
		JPAContainer<Fornecedor> container = JPAContainerFactory.make(Fornecedor.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
		container.addContainerFilter(Filters.eq("transportadora",true));
		return container;
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {			
				
				preencherEntity();
					
				if(fornecedor.getLimite_de_credito() != null){
					String valorAtual = fornecedor.getLimite_de_credito();
					String valorSemCifrao = valorAtual.replace("R$", "");
					fornecedor.setLimite_de_credito(valorSemCifrao);					
				}
				
				if(fornecedor.getCnae_fiscal() != null && cnaeSelecionado == null){
					cnaeSelecionado =  fornecedor.getCnae_fiscal();
				}
				
				if(cnaeSelecionado != null && cnpjValid == true && cnpjCadast == true){
					
						
							
							fornecedor.setStatus(cbStatus.getValue().toString());
							fornecedor.setCnae_fiscal(cnaeSelecionado);
							fireEvent(new FornecedorEvent(getUI(), fornecedor, true));
							Notification.show("Informações com Sucesso!");
						
						
						close();						
				
				}else{
					
					if(txtCidade.getValue() == null || txtUf.getValue() == null || txtPais.getValue() == null){
						Styles styles = Page.getCurrent().getStyles();					        
						styles.add(".dashboard input.v-textfield-readonly { background-color: #E97953; }");						
					}
										
					if(!txtCnaeFiscal.isValid()){
						txtCnaeFiscal.addStyleName("invalid-txt");
					}else{
						txtCnaeFiscal.removeStyleName("invalid-txt");
					}
					
					if(!txtBairro.isValid()){
						txtBairro.addStyleName("invalid-txt");
					}else{
						txtBairro.removeStyleName("invalid-txt");
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
	
	private void preencherEntity(){
		
		
		if(cbTransportadora != null && cbTransportadora.isEnabled() && cbTransportadora.getValue() != null){
			Integer codTransportadora = Integer.parseInt(cbTransportadora.getItem(cbTransportadora.getValue()).getItemProperty("id").getValue().toString());
			fornecedor.setTransportadora_padrao_id(codTransportadora);
		}
		
//		fornecedor.setTransportadora_padrao_id(Integer.parseInt(cbTransportadora.getItem(cbTransportadora).getItemProperty("id").getValue().toString()));
		
		if(cbStatus != null && cbStatus.getValue() != null){
			fornecedor.setStatus(cbStatus.getValue().toString());
		}
		
		if(txtInscEstadual != null && txtInscEstadual.getValue() != null){
			fornecedor.setInsc_estadual(txtInscEstadual.getValue());
		}
		
		if(txtRazaoSocial != null && txtRazaoSocial.getValue() != null){
		fornecedor.setRazao_social(txtRazaoSocial.getValue());
		}
		
		if(txtFantasia != null && txtFantasia.getValue() != null){
		fornecedor.setFantasia(txtFantasia.getValue());
		}
		
		if(txtDDDFone1 != null && txtDDDFone1.getValue() != null){
			fornecedor.setDdd_fone1(txtDDDFone1.getValue());
		}
		
		if(txtFone1 != null && txtFone1.getValue() != null){
			fornecedor.setFone1(txtFone1.getValue());
		}
		
		if(txtDDDFone2 != null && txtDDDFone2.getValue()  != null){
			fornecedor.setDdd_fone2(txtDDDFone2.getValue());
		}
		
		if(txtFone2 != null && txtFone2.getValue() != null){
		fornecedor.setFone2(txtFone2.getValue());
		}
		
		if(txtDDDFone3 != null && txtDDDFone3.getValidators() != null){
			fornecedor.setDdd_fone3(txtDDDFone3.getValue());
		}
		
		if(txtFone3 != null && txtFone3.getValue() != null){
		fornecedor.setFone3(txtFone3.getValue());
		}
		
		if(txtFone0800 != null && txtFone0800.getValue() != null){
			fornecedor.setFone0800(txtFone0800.getValue());
		}
		
		if(txtEmail != null && txtEmail.getValue() != null){
			fornecedor.setEmail(txtEmail.getValue());
		}
		
		if(txtSite != null && txtSite.getValue() != null){
			fornecedor.setSite_representante(txtSite.getValue());
		}
		
		if(cnaeSelecionado != null){
			fornecedor.setCnae_fiscal(cnaeSelecionado);
		}
		
		if(txtPrincipaisProdutos != null && txtPrincipaisProdutos.getValue()  != null){
			fornecedor.setPrincipais_produtos(txtPrincipaisProdutos.getValue());
		}
		
		if(txtObs != null && txtObs.getValue() != null){
		fornecedor.setObservacao(txtObs.getValue());
		}
		
		if(txtLimiteCredito != null && txtLimiteCredito.getValue() != null){
		fornecedor.setLimite_de_credito(txtLimiteCredito.getValue());
		}
		
		if(txtDpt != null && txtDpt.getValue() != null){
		fornecedor.setDtpvendas(txtDpt.getValue());
		}
		
		if(txtContato1 != null && txtContato1.getValue() != null){
		fornecedor.setContato_dpt1(txtContato1.getValue());
		}
		
		if(txtDDDFone1Contato != null && txtDDDFone1Contato.getValue() != null){
			fornecedor.setDdd_fone1(txtDDDFone1Contato.getValue());
		}
		
		if(txtEmailContato1 != null && txtEmailContato1.getValue() != null){
		fornecedor.setEmail_dpt1(txtEmailContato1.getValue());
		}
		
		if(txtSkypeContato1 != null && txtSkypeContato1.getValue() != null){
		fornecedor.setSkype_dpt1(txtSkypeContato1.getValue());
		}
		
		if(txtDptContato1 != null && txtDptContato1.getValue() != null){
		fornecedor.setDtpvendas(txtDptContato1.getValue());
		}
		
		if(txtContato2 != null && txtContato2.getValue() != null){
		fornecedor.setContato_dpt2(txtContato2.getValue());
		}
		
		if(txtDDDFone2Contato2 != null && txtDDDFone2Contato2.getValue() != null){
		fornecedor.setDdd_fone2(txtDDDFone2Contato2.getValue());
		}
		
		if(txtEmailContato2 != null && txtEmailContato2.getValue() != null){
			fornecedor.setEmail_dpt2(txtEmailContato2.getValue());
		}
		
		if(txtSkypeContato2 != null && txtSkypeContato2.getValue()  != null){
		fornecedor.setSkype_dpt2(txtSkypeContato2.getValue());
		}
		
		if(txtDptContato2 != null && txtDptContato2.getValue() != null){
		fornecedor.setDtpvendas2(txtDptContato2.getValue());
		}
		
		if(txtRepresentante != null && txtRepresentante.getValue() != null){
		fornecedor.setRepresentante(txtRepresentante.getValue());
		}
		
		if(txtDDDFoneRepresentante != null && txtDDDFoneRepresentante.getValue()  != null){
		fornecedor.setDdd_fone1_representante(txtDDDFoneRepresentante.getValue());
		}
		
		if(txtFoneRepresentate != null && txtFoneRepresentate.getValue() != null){
			fornecedor.setFone1_representante(txtFoneRepresentate.getValue());
		}
		
		if(txtDDDFoneRepresentante2 != null && txtDDDFoneRepresentante2.getValue() != null){
			fornecedor.setDdd_fone2_representante(txtDDDFoneRepresentante2.getValue());
		}
		
		if(txtFoneRepresentate2 != null && txtFoneRepresentate2.getValue() != null){
		fornecedor.setFone2_representante(txtFoneRepresentate2.getValue());
		}
		
		if(txtDDDFoneRepresentante3 != null && txtDDDFoneRepresentante3.getValue() != null){
			fornecedor.setDdd_fone3_representante(txtDDDFoneRepresentante3.getValue());
		}
		
		if(txtFoneRepresentate3 != null && txtFoneRepresentate3.getValue() != null){
		fornecedor.setFone3_representante(txtFoneRepresentate3.getValue());
		}
		
		if(txtDDDFoneRepresentante4 != null && txtDDDFoneRepresentante4.getValue() != null){
		fornecedor.setDdd_fone4_representante(txtDDDFoneRepresentante4.getValue());
		}
		
		if(txtFoneRepresentante4 != null && txtFoneRepresentante4.getValue() != null){
		fornecedor.setFone4_representante(txtFoneRepresentante4.getValue());
		}
		
		if(txtEmailRepresentante != null && txtEmailRepresentante.getValue() != null){
			fornecedor.setEmail_representante(txtEmailRepresentante.getValue());
		}
		
		if(txtSiteRepresentante != null && txtSiteRepresentante.getValue() != null){
		fornecedor.setSite_representante(txtSiteRepresentante.getValue());
		}
		
		if(txtSkypeRepresentante != null && txtSkypeRepresentante.getValue() != null){
		fornecedor.setSkype_dpt1(txtSkypeRepresentante.getValue());
		}
		
		if(txtObsRepresentante != null && txtObsRepresentante.getValue() != null){
		fornecedor.setObservacao(txtObsRepresentante.getValue());
		}
		
		if(txtCep != null && txtCep.getValue() != null){
		fornecedor.setCep(txtCep.getValue());
		}
		
		if(txtEndereco != null && txtEndereco.getValue() != null){
		fornecedor.setEndereco(txtEndereco.getValue());
		}
		
		if(txtBairro != null && txtBairro.getValue() != null){
		fornecedor.setBairro(txtBairro.getValue().toString());
		}
		
		if(txtNumero != null && txtNumero.getValue() != null){
		fornecedor.setNumero(txtNumero.getValue());
		}
		
		if(txtCidade != null && txtCidade.getValue() != null){
		fornecedor.setCidade(txtCidade.getValue());
		}
		
		if(txtUf != null && txtUf.getValue() != null){
		fornecedor.setUf(txtUf.getValue());
		}
		
		if(txtPais != null && txtPais.getValue() != null){
		fornecedor.setPais(txtPais.getValue());
		}
		
		if(txtComplemento != null && txtComplemento.getValue() != null){
		fornecedor.setComplemento(txtComplemento.getValue());
		}
		
		if(txtReferencia != null && txtReferencia.getValue() != null){
			fornecedor.setPonto_referencia(txtReferencia.getValue());
		}
		
		if(txtRepresentante  != null && txtRepresentante.getValue() != null){
		fornecedor.setRepresentante(txtRepresentante.getValue());
		}
		
		if(txtCpfCnpj != null && txtCpfCnpj.getValue() != null){
		fornecedor.setCnpj(txtCpfCnpj.getValue());
		}
		
		if(txtSite != null && txtSite.getValue() != null){
			fornecedor.setHome_page(txtSite.getValue());
		}
		
		if(cbTipoFrete != null && cbTipoFrete.getValue() != null){
			fornecedor.setTipo_de_frete(cbTipoFrete.getValue().toString());
		}
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			
					final GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
															
								if(cnpjValid == true && cnpjCadast == true){
									
										preencherEntity();
										
										fornecedor.setStatus(cbStatus.getValue().toString());
										fornecedor.setCnae_fiscal(cnaeSelecionado);
										fireEvent(new FornecedorEvent(getUI(), fornecedor, true));

										
										if(fornecedor.getId() == null){
											Notification.show("Fornecedor Cadastrado com Sucesso!");
										}else{
											Notification.show("Fornecedor Alterado com Sucesso!");
										}
										
										close();
									
								}else{
									Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
								}
							}else{							
							
								if(fornecedor.getStatus().equals("EM DIGITACAO")){
									FornecedorDAO.remove(fornecedor);
								}
								
								fireEvent(new FornecedorEvent(getUI(), null, false));
								close();
														
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
						
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
	
	private FormLayout buildFormTransportadora(boolean enabled){
		
		final boolean enable = enabled;
		frmTransportadoraPadrao = new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				cbTransportadora = new ComboBox("Transportadora Padrão", buildTransportadoraDefault());
				cbTransportadora.setItemCaptionPropertyId("razao_social");
				cbTransportadora.setWidth("100%");	
				cbTransportadora.setImmediate(true);
				cbTransportadora.setNullSelectionAllowed(false);
				cbTransportadora.setConverter(new SingleSelectConverter(cbTransportadora));
				cbTransportadora.setEnabled(enable);
				cbTransportadora.setRequired(enable);
				
				if(!enable){
					fornecedor.setTransportadora_padrao_id(null);
				}
				
				addComponent(cbTransportadora);
				
				cbTransportadora.select(fornecedor.getTransportadora_padrao_id());
				
			}				 
		};
		
		return frmTransportadoraPadrao;
	}
	public void addListerner(FornecedorListerner target){
		try {
			Method method = FornecedorListerner.class.getDeclaredMethod("onClose", FornecedorEvent.class);
			addListener(FornecedorEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(FornecedorListerner target){
		removeListener(FornecedorEvent.class, target);
	}
	public static class FornecedorEvent extends Event{
		
		private Fornecedor fornecedor;
		private boolean confirm;
		
		public FornecedorEvent(Component source, Fornecedor fornecedor, boolean confirm) {
			super(source);
			this.fornecedor = fornecedor;
			this.confirm = confirm;			
		}

		public Fornecedor getFornecedor() {
			return fornecedor;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface FornecedorListerner extends Serializable{
		public void onClose(FornecedorEvent event);
	}
	
	
}
