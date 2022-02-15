package com.digital.opuserp.view.modulos.cadastro.transportadoras;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.TransportadoraDAO;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.BlurListernerUtil;
import com.digital.opuserp.util.CnpjUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.ValidarCPF;
import com.digital.opuserp.view.util.CepUtil;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class TransportadoraEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;

	ShortcutListener sl_esc;

	private Float width_tipoPessoa;
	private Float width_cpfcnpj;
	private Float width_insc_estadual;
	private Float width_endereco;
	private Float width_numero;
	private Float width_bairro;
	private Float width_cidade;
	private Float width_uf;
	private Float width_pais;

	boolean cnpjValid = false;
	boolean cnpjCadast= false;
	String cnpjAtual; 
	String limiteCredito; 

	public TransportadoraEditor(Item item, String title, boolean modal) {
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

		setContent(new VerticalLayout() {
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

		sl_esc = buildShortCurListerner();
		
		
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

	private ShortcutListener buildShortCurListerner() {
		sl_esc = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ENTER,
				null) {
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};

		return sl_esc;
	}

	private void configLayout() {
		// 1366x768
		//if (OpusERP4UI.browser.getScreenWidth() >= 1024	&& OpusERP4UI.browser.getScreenHeight() >= 768) {
			setWidth("960px");
			setHeight("660px");

			width_tipoPessoa = 0.18f;
			width_cpfcnpj = 0.13f;
			width_insc_estadual = 0.19f;
			width_numero = 0.1f;
			width_endereco = 0.53f;

			width_bairro = 0.35f;
			width_cidade = 0.23f;
			width_uf = 0.1f;
			width_pais = 0.2f;

		//}
	}

	public void updateCep(Ceps cep) {
		((TextField) fieldGroup.getField("cep")).setValue(cep.getCep());
		((TextField) fieldGroup.getField("endereco")).setValue(cep
				.getEndereco());
		((TextField) fieldGroup.getField("numero")).setValue("");
		((TextField) fieldGroup.getField("bairro")).setValue(cep.getBairro());
		((TextField) fieldGroup.getField("cidade")).setValue(cep.getCidade());
		((TextField) fieldGroup.getField("uf")).setValue(cep.getUf());
		((TextField) fieldGroup.getField("pais")).setValue(cep.getPais());
		((TextField) fieldGroup.getField("numero")).focus();
	}

	public void buildLayout() {

		fieldGroup = new FieldGroup(item);

		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);

						TextField tfCod = (TextField) fieldGroup.buildAndBind("Código", "id");
						tfCod.setWidth("88px");
						tfCod.setNullRepresentation("");
						tfCod.setStyleName("caption-align-transportadora");
						tfCod.setReadOnly(true);
						tfCod.setMaxLength(10);
						//tfCod.focus();
						
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				});


				
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);

						TextField tfCod = (TextField) fieldGroup.buildAndBind("Cadastro", "data_cadastro");
						tfCod.setWidth("142px");
						tfCod.setNullRepresentation("");
						tfCod.setReadOnly(true);
//						tfCod.setMaxLength(10);
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				});

				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						
						TextField tfCod = (TextField)fieldGroup.buildAndBind("Alteração", "data_alteracao");
						tfCod.setWidth("142px");
						tfCod.setNullRepresentation("");
						tfCod.setReadOnly(true);
						tfCod.setMaxLength(10);
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				});
				
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);

						ComboBox tfCod = new ComboBox("Status");
						tfCod.setNullSelectionAllowed(false);
						tfCod.addItem("ATIVO");
						tfCod.addItem("INATIVO");
						tfCod.focus();
						tfCod.setWidth("140px");
						tfCod.setStyleName("caption-align-status");
						tfCod.setRequired(true);

						addComponent(tfCod);

						fieldGroup.bind(tfCod, "status");
					}
				});
			}
		});

		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);

						
						TextField tfCod = (TextField) fieldGroup.buildAndBind("CPF/CNPJ", "cnpj");
						tfCod.setWidth("150px");
						tfCod.setStyleName("caption-align-transportadora");
						tfCod.setNullRepresentation("");
						tfCod.setRequired(true);
						tfCod.setMaxLength(14);
						tfCod.setTextChangeEventMode(TextChangeEventMode.LAZY);
						tfCod.addListener(new FieldEvents.TextChangeListener() {

							@Override
							public void textChange(TextChangeEvent event) {

								 if(!event.getText().isEmpty() && 
											!event.getText().toString().equals(cnpjAtual)){
										
										//Validar CNPJ
										if(event.getText().length() == 14){
											if(CnpjUtil.validar(event.getText())){
												cnpjValid = true;
												event.getComponent().removeStyleName("textfield-transportadora-invalid");
												event.getComponent().setStyleName("textfield-transportadora-valid");
											}else{
												cnpjValid = false;
												event.getComponent().removeStyleName("textfield-transportadora-valid");
												event.getComponent().setStyleName("textfield-transportadora-invalid");
											}
										}else if(event.getText().length() == 11){
//											cnpjValid = false;
//											event.getComponent().removeStyleName("textfield-transportadora-valid");
//											event.getComponent().setStyleName("textfield-transportadora-invalid");
//										}
										
//										if(event.getText().length() == 11){
											if(ValidarCPF.cpfValido(event.getText())){
												cnpjValid = true;
												event.getComponent().removeStyleName("textfield-transportadora-invalid");
												event.getComponent().setStyleName("textfield-transportadora-valid");
											}else{
												cnpjValid = false;
												event.getComponent().removeStyleName("textfield-transportadora-valid");
												event.getComponent().setStyleName("textfield-transportadora-invalid");
											}
										}else{
											cnpjValid = false;
											event.getComponent().removeStyleName("textfield-transportadora-valid");
											event.getComponent().setStyleName("textfield-transportadora-invalid");
										}
										
										
										
										if(cnpjValid){
											//Verificar Existencia
											TransportadoraDAO tDAO = new TransportadoraDAO();
											if(tDAO.cpfCnpjCadastrado(OpusERP4UI.getEmpresa().getId(), event.getText())){
												cnpjCadast = true;
												event.getComponent().removeStyleName("textfield-transportadora-invalid");
												event.getComponent().setStyleName("textfield-transportadora-valid");
											}else{
												cnpjCadast = false;
												event.getComponent().removeStyleName("textfield-transportadora-valid");
												event.getComponent().setStyleName("textfield-transportadora-invalid");
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
										
										event.getComponent().removeStyleName("textfield-transportadora-invalid");
										event.getComponent().removeStyleName("textfield-transportadora-valid");
										event.getComponent().setStyleName("caption-align-transportadora");
									}
								
							}
						});

						 tfCod.addListener(new FieldEvents.BlurListener() {
								
								@Override
								public void blur(BlurEvent event) {
									if(cnpjValid == false){
											Notification.show("CPF/CNPJ Inválido!");
											((TextField) event.getComponent()).focus();
									}else{
										if(cnpjCadast == false){
											Notification.show("CPF/CNPJ Já Cadastrado!");
											
											if(fieldGroup.getField("ie") != null){
												fieldGroup.getField("ie").focus();
											}
										}else{
											if(fieldGroup.getField("ie") != null){
												fieldGroup.getField("ie").focus();
											}
										}
									}	
									
								}
							});

						addComponent(tfCod);
//						if (item.getItemProperty("id").getValue() != null) {
//							cnpjValid = true;
//							tfCod.setStyleName("textfield-transportadora-valid");
//							tfCod.setReadOnly(true);
//						}
						
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
				
				
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);

						TextField tfCod = (TextField) fieldGroup.buildAndBind("Inscrição Estadual/RG", "ie");
						tfCod.setWidth("250px");
						tfCod.setNullRepresentation("");
						tfCod.setRequired(false);
						tfCod.setMaxLength(30);
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				});

			}
		});

		vlRoot.addComponent(new FormLayout() {
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");

				TextField tfCod = (TextField) fieldGroup.buildAndBind("Razão Social", "nome");
				tfCod.setWidth("100%");
				tfCod.setStyleName("caption-align-transportadora");
				tfCod.setNullRepresentation("");
				tfCod.setMaxLength(200);
				tfCod.addListener((BlurListener) new BlurListernerUtil());

				tfCod.setRequired(true);
				addComponent(tfCod);
				setExpandRatio(tfCod, 2);
			}
		});

		vlRoot.addComponent(new FormLayout() {
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");

				TextField tfCod = (TextField) fieldGroup.buildAndBind("Fantasia", "fantasia");
				tfCod.setWidth("340px");
				tfCod.setStyleName("caption-align-transportadora");
				tfCod.setNullRepresentation("");
				tfCod.setMaxLength(200);
				tfCod.addListener((BlurListener) new BlurListernerUtil());

				tfCod.setRequired(true);
				addComponent(tfCod);
				setExpandRatio(tfCod, 2);
			}
		});
		
		
		
		
		
		
		
		
		vlRoot.addComponent(new FormLayout() {
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
			

				TextField tfCod = (TextField) fieldGroup.buildAndBind(
						"Contato", "contato");
				tfCod.setWidth("400px");
				tfCod.setStyleName("caption-align-transportadora");
				tfCod.setNullRepresentation("");
				tfCod.setMaxLength(200);
				tfCod.addListener((BlurListener) new BlurListernerUtil());

				tfCod.setRequired(true);

				addComponent(tfCod);
				setExpandRatio(tfCod, 2);
			}
		});

		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{

						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");

						TextField tfCod = (TextField) fieldGroup.buildAndBind(
								"CEP", "cep");
						tfCod.setWidth("80px");
						tfCod.setStyleName("caption-align-transportadora");
						tfCod.setNullRepresentation("");
						tfCod.setMaxLength(8);
						tfCod.setRequired(true);
						tfCod.setTextChangeEventMode(TextChangeEventMode.LAZY);
						tfCod.addListener((BlurListener) new BlurListernerUtil());
						tfCod.addListener(new FieldEvents.TextChangeListener() {

							@Override
							public void textChange(TextChangeEvent event) {
								if (event.getText().length() == 8) {
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
						
						tfCod.addListener(new FieldEvents.BlurListener() {
							
							@Override
							public void blur(BlurEvent event) {
								if(((TextField) fieldGroup.getField("endereco")).getValue().equals("") || ((TextField) fieldGroup.getField("endereco")).getValue().equals(null)){
									((TextField) fieldGroup.getField("endereco")).focus();
								}else{
									((TextField) fieldGroup.getField("numero")).focus();
								}
							}
						});
						
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				});

				final Button btSearch = new Button(null,
						new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								CepUtil cep = new CepUtil(true, true);
								cep.addListerner(new CepUtil.CepListerner() {

									@Override
									public void onSelected(
											com.digital.opuserp.view.util.CepUtil.CepEvent event) {
										updateCep(event.getCep());
										((TextField) fieldGroup
												.getField("numero")).focus();
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

				BrowserWindowOpener opener = new BrowserWindowOpener(
						"http://www.buscacep.correios.com.br/servicos/dnec/index.do");
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

		
		vlRoot.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");

				FormLayout frmEndereco = new FormLayout() {
					{

						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
//
//						TextField tfCod = (TextField) fieldGroup.buildAndBind(
//								"Endereço", "endereco");
//						tfCod.setWidth("100%");
//						tfCod.setNullRepresentation("");
//						tfCod.setStyleName("caption-align-transportadora");
//						tfCod.setRequired(true);
//						tfCod.setMaxLength(200);
						addComponent(fieldGroup.buildAndBind("Endereço", "endereco"));
						((TextField) fieldGroup.getField("endereco")).setWidth("100%");
						((TextField) fieldGroup.getField("endereco")).setNullRepresentation("");
						((TextField) fieldGroup.getField("endereco")).setStyleName("caption-align-transportadora");
						((TextField) fieldGroup.getField("endereco")).setRequired(true);
						((TextField) fieldGroup.getField("endereco")).setMaxLength(200);
						((TextField) fieldGroup.getField("endereco")).addListener((BlurListener) new BlurListernerUtil());
						// setExpandRatio(tfCod, 2);
					}
				};

				addComponent(frmEndereco);

				setExpandRatio(frmEndereco, width_endereco);

				FormLayout frmNumero = new FormLayout() {
					{

						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");

						TextField tfCod = (TextField) fieldGroup.buildAndBind(
								"Número", "numero");
						tfCod.setWidth("100%");
						tfCod.setNullRepresentation("");
						tfCod.setRequired(true);
					//	tfCod.focus();
						tfCod.setMaxLength(10);
						tfCod.addListener((BlurListener) new BlurListernerUtil());
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				};
				addComponent(frmNumero);

				setExpandRatio(frmNumero, width_numero);

			}
		});

		
		
		
		
		
		vlRoot.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");
				FormLayout frmBairro = new FormLayout() {
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");

						TextField tfCod = (TextField) fieldGroup.buildAndBind(
								"Bairro", "bairro");
						tfCod.setWidth("200px");
						tfCod.setNullRepresentation("");
						tfCod.setStyleName("caption-align-transportadora");
						tfCod.setRequired(true);
						tfCod.setMaxLength(50);
						tfCod.addListener((BlurListener) new BlurListernerUtil());
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				};

				addComponent(frmBairro);
				setExpandRatio(frmBairro, width_bairro);

				FormLayout frmCidade = new FormLayout() {
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");

						TextField tfCod = (TextField) fieldGroup.buildAndBind(
								"Cidade", "cidade");
					//	tfCod.setWidth("100%");
						tfCod.setNullRepresentation("");
						tfCod.setRequired(true);
						tfCod.setMaxLength(50);
						tfCod.addListener((BlurListener) new BlurListernerUtil());
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				};
				addComponent(frmCidade);
				setExpandRatio(frmCidade, width_cidade);

				FormLayout frmUf = new FormLayout() {
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");

						TextField tfCod = (TextField) fieldGroup.buildAndBind(
								"UF", "uf");
						tfCod.setWidth("40px");
						tfCod.setNullRepresentation("");
						tfCod.setRequired(true);
						tfCod.setMaxLength(2);
						tfCod.addListener((BlurListener) new BlurListernerUtil());
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				};
				addComponent(frmUf);
				setExpandRatio(frmUf, width_uf);

				FormLayout frmPais = new FormLayout() {
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");

						TextField tfCod = (TextField) fieldGroup.buildAndBind(
								"Pais", "pais");
						tfCod.setWidth("148px");
						tfCod.setNullRepresentation("");
						tfCod.setRequired(true);
						tfCod.setMaxLength(50);
						tfCod.addListener((BlurListener) new BlurListernerUtil());
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				};
				addComponent(frmPais);

				setExpandRatio(frmPais, width_pais);

			}
		});

		
		
		
		
		vlRoot.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");

						TextField tfCod = (TextField) fieldGroup.buildAndBind(
								"Complemento", "complemento");
						tfCod.setWidth("100%");
						tfCod.setNullRepresentation("");
						tfCod.setStyleName("caption-align-transportadora");
						tfCod.setRequired(true);
						tfCod.setMaxLength(100);
						tfCod.addListener((BlurListener) new BlurListernerUtil());
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				});

				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");

						TextField tfCod = (TextField) fieldGroup.buildAndBind(
								"Referência", "referencia");
						tfCod.setWidth("100%");
						tfCod.setNullRepresentation("");
						tfCod.setRequired(false);
						tfCod.setMaxLength(200);
						tfCod.addListener((BlurListener) new BlurListernerUtil());
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				});
			}
		});

		
		
		
		vlRoot.addComponent(new HorizontalLayout(){
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
						tfCod.setStyleName("caption-align-transportadora");
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
						
						TextField tfCod = (TextField)fieldGroup.buildAndBind(null, "fone");
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
						
						TextField tfCod = (TextField)fieldGroup.buildAndBind(null, "fax");
						tfCod.setWidth("105px");							
						tfCod.setNullRepresentation("");
						tfCod.setMaxLength(10);
						
						addComponent(tfCod);						
					}
				};
				addComponent(frmFone2);
				
			
				
			}
		});
		
		
		
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				setSizeUndefined();		
				
				
				FormLayout frmDDD_Fone3 = new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						TextField tfCod = (TextField)fieldGroup.buildAndBind("Telefone Alternativo", "ddd_fone3");
						tfCod.setWidth("40px");							
						tfCod.setStyleName("caption-align-transportadora");
						tfCod.setNullRepresentation("");
						tfCod.setMaxLength(2);
						
						addComponent(tfCod);						
					}
				};
				addComponent(frmDDD_Fone3);
				
				
				FormLayout frmFone3 = new FormLayout(){
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
				addComponent(frmFone3);
				
							
				
				FormLayout frmFone0800 = new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						TextField tfCod = (TextField)fieldGroup.buildAndBind("0800", "fone0800");
						tfCod.setWidth("105px");							
						tfCod.setNullRepresentation("");
						tfCod.setStyleName("caption-align-transportadora");
						tfCod.setMaxLength(11);
						
						addComponent(tfCod);						
					}
				};
				addComponent(frmFone0800);
				
			
				
			}
		});
		
		
		
		

		vlRoot.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");

				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						

						TextField tfCod = (TextField) fieldGroup.buildAndBind("E-mail", "email");
						tfCod.setWidth("100%");
						tfCod.setNullRepresentation("");
						tfCod.setRequired(true);
						tfCod.setMaxLength(50);
						tfCod.setStyleName("caption-align-transportadora");
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				});

				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
				

						TextField tfCod = (TextField) fieldGroup.buildAndBind("Site", "site");
						tfCod.setWidth("100%");
						tfCod.setNullRepresentation("");
						tfCod.setStyleName("caption-align-transportadora");
						tfCod.setRequired(false);
						tfCod.setMaxLength(50);
						addComponent(tfCod);
						setExpandRatio(tfCod, 2);
					}
				});

			}
		});
		
		
		
		

		vlRoot.addComponent(new FormLayout() {
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");

				TextField tfCod = (TextField) fieldGroup.buildAndBind("MSN","msn");
				tfCod.setWidth("295px");
				tfCod.setStyleName("caption-align-transportadora");
				tfCod.setNullRepresentation("");
				tfCod.setRequired(false);
				tfCod.setMaxLength(200);

				addComponent(tfCod);
				setExpandRatio(tfCod, 2);
			}
		});

		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);													
							
				TextArea tfCod = new TextArea("Obs");
						
				tfCod.setWidth("100%");
				tfCod.setHeight("50px");
				tfCod.setNullRepresentation("");
				tfCod.setRequired(false);								
				tfCod.setStyleName("caption-align-transportadora");
				tfCod.addStyleName("text-area-border");
				addComponent(tfCod);
				fieldGroup.bind(tfCod, "observacao");
			}
		});
		
		
		
//		vlRoot.addComponent(new FormLayout() {
//			{
//				setStyleName("form-cutom-new");
//				setMargin(true);
//				setSpacing(true);				
//
//				TextField tfCod = (TextField) fieldGroup.buildAndBind(
//						"Observação", "observacao");
//				tfCod.setWidth("100%");
//				tfCod.setStyleName("caption-align-transportadora");
//				tfCod.setNullRepresentation("");
//				tfCod.setRequired(false);
//				tfCod.setMaxLength(200);
//
//				addComponent(tfCod);
//				setExpandRatio(tfCod, 2);
//			}
//		});

//		vlRoot.addComponent(new FormLayout() {
//			{
//				setStyleName("form-cutom-new");
//				setMargin(true);
//				setSpacing(true);
//				
//				TextField tfCod = (TextField) fieldGroup.buildAndBind("Limite de Crédito", "limite_de_credito");
//				tfCod.setWidth("150px");
//				tfCod.setStyleName("textfield-transportdora-align");
//				tfCod.setNullRepresentation("");
//				tfCod.setRequired(false);
////				tfCod.setMaxLength(200);
//
//				
//				if (item.getItemProperty("limite_de_credito").getValue() != null) {			
//					Double limiteTrans = Double.parseDouble(item.getItemProperty("limite_de_credito").getValue().toString());
//										
//					Locale.setDefault(new Locale("pt", "BR"));  // mudança global  				  
//					DecimalFormat df = new DecimalFormat();  
//					df.applyPattern("R$ #,##0.00");  
//					df.format(limiteTrans);
//
//				}
//							
//				addComponent(tfCod);
//				setExpandRatio(tfCod, 2);
//			}
//		});

	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				if(fieldGroup.isValid() && cnpjValid == true && cnpjCadast == true){
					try {
						if(item.getItemProperty("id").getValue() == null){			
							fieldGroup.commit();
							fireEvent(new TransportadoraEvent(getUI(), item, true));
							Notification.show("Transportadora Salva com Sucesso!");
						}else{
							if(fieldGroup.isModified()){
								fieldGroup.commit();
								fireEvent(new TransportadoraEvent(getUI(), item, true));
								Notification.show("Transportadora Alterada com Sucesso!");
							}	
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

				if (!fieldGroup.isModified()) {
//					fieldGroup.discard();
//					fireEvent(new TransportadoraEvent(getUI(), item, false));
					close();
				} else {
					GenericDialog gDialog = new GenericDialog(
							"Confirme para Continuar!","Deseja Salvar as Informações Alteradas?", true,true);
					  		 gDialog.setCaptionCANCEL("Sair sem Salvar!");
							 gDialog.setCaptionOK("Salvar");

					gDialog.addListerner(new GenericDialog.DialogListerner() {

						@Override
						public void onClose(DialogEvent event) {
							if (event.isConfirm()) {
								if(item.getItemProperty("id").getValue() != null){
									cnpjCadast = true;
								}
								if (fieldGroup.isValid() && cnpjValid == true && cnpjCadast == true) {
									try {
										fieldGroup.commit();
										fireEvent(new TransportadoraEvent(getUI(), item, true));

										if(item.getItemProperty("id").getValue() == null){							
											Notification.show("Transportadora Salva com Sucesso!");
										}else{
											Notification.show("Transportadora Alterada com Sucesso!");
											}	
										
										close();
									} catch (Exception e) {
										e.printStackTrace();
										System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
										Notification.show("Não foi Possivel Salvar as Alterações!");
									}
								} else {
									Notification.show("Não é Possivel Salvar os Dados Verifique os Campos Inválidos/Obrigatórios!");
								}
							} else {
								fieldGroup.discard();
								fireEvent(new TransportadoraEvent(getUI(),item, false));
								close();
							}

						}
					});
					//btCancelar.addShortcutListener(sl_esc);
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

	public void addListerner(TransportadoraListerner target) {
		try {
			Method method = TransportadoraListerner.class.getDeclaredMethod(
					"onClose", TransportadoraEvent.class);
			addListener(TransportadoraEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}

	public void removeListerner(TransportadoraListerner target) {
		removeListener(TransportadoraEvent.class, target);
	}

	public static class TransportadoraEvent extends Event {

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

	public interface TransportadoraListerner extends Serializable {
		public void onClose(TransportadoraEvent event);
	}


	

}
