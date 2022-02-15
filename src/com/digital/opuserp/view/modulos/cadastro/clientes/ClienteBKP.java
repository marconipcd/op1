package com.digital.opuserp.view.modulos.cadastro.clientes;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.CreditoClienteDAO;
import com.digital.opuserp.dao.EcfPreVendaCabecalhoDAO;
import com.digital.opuserp.dao.LogErrorDAO;
import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ComoNosConheceu;
import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.CepUtil;
import com.digital.opuserp.util.CnpjUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.ValidarCPF;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class ClienteBKP extends Window implements GenericEditor {

	Item ClienteItem;
	Item EndItem;
	Item EndEntreItem;
	Item EndCobrancaItem;
		
	Button btSalvar;
	Button btCancelar;

	VerticalLayout vlRoot;

	TabSheet tbDados;
	FormLayout flPrincipal;
	FormLayout flOutrosEnd;
	FormLayout flFiador;
	FormLayout flOutrasInfo;

	FieldGroup fieldGroup;
	FieldGroup fgEndereco;
	FieldGroup fgEnderecoCobranca;
	FieldGroup fgEnderecoEntrega;
	
	CheckBox cbEnderecoCobranca;
	CheckBox cbEnderecoEntrega;	
	
	String tipo;

	boolean cnpjValid = false;
	boolean cnpjCadast = false;
	String cnpjAtual;
	String categoria;
	
	private String width_pais;
	private String width_referenc;
	private String width_cidade;
	private String width_categoria;
	private String width_numero;
	private String width_apelido;
	private String width_tratamento;
	private String width_complemento;

	JPAContainer<Categorias> containerCategorias;

	public ClienteBKP(Item ClienteItem, Item EndItem, Item EndCobItem,Item EndEntItem, String title, boolean modal) {

		configLayout();
		setStyleName("cliente_editor");

		this.ClienteItem = ClienteItem;
		this.EndItem = EndItem;
		this.EndEntreItem = EndEntItem;
		this.EndCobrancaItem = EndCobItem;
		
		tipo = title;
		
		setCaption(title);
		setModal(modal);		

		setResizable(false);
		center();
		setClosable(false);

		containerCategorias = buildJPACategoria();

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

		if (ClienteItem.getItemProperty("doc_cpf_cnpj").getValue() != null) {
			cnpjAtual = ClienteItem.getItemProperty("doc_cpf_cnpj").getValue().toString();
			cnpjValid = true;
			cnpjCadast = true;
		} else {
			cnpjValid = false;
			cnpjCadast = false;
		}

		buildLayout();

	}

	private void configLayout() {
		// 1366x768
		//if (OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768) {
			setWidth("950px");
			setHeight("672px");

			width_pais = "141px";
			width_complemento = "304px";
			width_referenc = "314px";
			width_cidade = "140px";
			width_categoria = "200px";
			width_numero = "64px";
			width_apelido = "191px";
			width_tratamento = "100px";

		//}
	}

	public JPAContainer<Categorias> buildJPACategoria() {
		containerCategorias = new JPAContainerFactory().make(Categorias.class,ConnUtil.getEntity());
		containerCategorias.removeAllContainerFilters();
		containerCategorias.addContainerFilter("empresa_id",OpusERP4UI.getEmpresa().getId().toString(), true, false);
		containerCategorias.applyFilters();
		return containerCategorias;
	}

	public String getNextId() {
		ClienteDAO cDao = new ClienteDAO();
		return cDao.getNextID();
	}

	public void buildLayout() {

		// Item Cliente
		fieldGroup = new FieldGroup(ClienteItem);

		HorizontalLayout hl1 = new HorizontalLayout();
		hl1.setWidth("100%");
		
		// Cod
		hl1.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom-new");

				TextField tfCod = new TextField("Código");

				if (ClienteItem.getItemProperty("id").getValue() != null) {
					fieldGroup.bind(tfCod, "id");
				} else {
					tfCod.setValue(getNextId());
				}

				
				
				tfCod.setWidth("60px");
				tfCod.setReadOnly(true);
				tfCod.setStyleName("caption-align");
				tfCod.setNullRepresentation("");
				//tfCod.setConverter(new StringToIntegerConverter());
				
				addComponent(tfCod);
				setExpandRatio(tfCod, 2);

			}
		});

		// Data de Cadastro
		hl1.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom-new");

				TextField txtdataCadastro = (TextField) fieldGroup.buildAndBind("Cadastro", "data_cadastro");
				txtdataCadastro.setReadOnly(true);
				txtdataCadastro.setNullRepresentation("");
				txtdataCadastro.setWidth("137px");

				addComponent(txtdataCadastro);
				setComponentAlignment(txtdataCadastro, Alignment.TOP_RIGHT);
			}
		});

		// Ultima Alteração
		hl1.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom-new");

				TextField txtAlteracao = (TextField) fieldGroup.buildAndBind("Alteração", "data_alteracao");
				txtAlteracao.setReadOnly(true);
				txtAlteracao.setNullRepresentation("");
				txtAlteracao.setWidth("137px");

				addComponent(txtAlteracao);
				setComponentAlignment(txtAlteracao, Alignment.TOP_RIGHT);
			}
		});
		
		// Status
		hl1.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom-new");

				TextField txtStatus = (TextField) fieldGroup.buildAndBind("Status", "status");
				((TextField) fieldGroup.getField("status")).setWidth("137px");
				((TextField) fieldGroup.getField("status")).setReadOnly(true);
				
				
				addComponent(txtStatus);
			}
		});
		
		vlRoot.addComponent(hl1);

		HorizontalLayout hl2 = new HorizontalLayout();
		
		hl2.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom-new");
		
				addComponent(fieldGroup.buildAndBind("CPF/CNPJ", "doc_cpf_cnpj"));
				
				if(ClienteItem.getItemProperty("id").getValue() != null){
					((TextField) fieldGroup.getField("doc_cpf_cnpj")).setReadOnly(true);
				}
				
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setRequired(true);
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setNullRepresentation("");
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setStyleName("caption-align");
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setWidth("153px");
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setMaxLength(14);
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).focus();
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setTextChangeEventMode(TextChangeEventMode.LAZY);
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).addListener(new FieldEvents.TextChangeListener() {

							@Override
							public void textChange(TextChangeEvent event) {
								if (!event.getText().isEmpty() && !event.getText().toString().equals(cnpjAtual)) {

									// Validar CNPJ
									if (event.getText().length() == 14) {
										if (CnpjUtil.validar(event.getText())) {
											cnpjValid = true;
											event.getComponent().removeStyleName("invalid-cpf");
											event.getComponent().setStyleName("valid-cpf");
										} else {
											cnpjValid = false;
											event.getComponent().removeStyleName("valid-cpf");
											event.getComponent().setStyleName("invalid-cpf");
										}
									} else if (event.getText().length() == 11) {
																				
										if (ValidarCPF.cpfValido(event.getText())) {
											cnpjValid = true;
											event.getComponent().removeStyleName("invalid-cpf");
											event.getComponent().setStyleName("valid-cpf");
										} else {
											cnpjValid = false;
											event.getComponent().removeStyleName("valid-cpf");
											event.getComponent().setStyleName("invalid-cpf");
										}
									} else {
										cnpjValid = false;
										event.getComponent().removeStyleName("valid-cpf");
										event.getComponent().setStyleName("invalid-cpf");
									}

									if (cnpjValid) {
										// Verificar Existencia
										ClienteDAO cDAO = new ClienteDAO();									
										Cliente c = cDAO.cpfCnpjCadastrado(OpusERP4UI.getEmpresa(),event.getText());

										if (c != null && !c.getId().equals(ClienteItem.getItemProperty("id").getValue())) {
											cnpjCadast = false;
											event.getComponent().removeStyleName("valid-cpf");
											event.getComponent().setStyleName("invalid-cpf");
										} else {
											cnpjCadast = true;
											event.getComponent().removeStyleName("invalid-cpf");
											event.getComponent().setStyleName("valid-cpf");										
										}
									}

								} else {

									if (!event.getText().isEmpty()) {
										cnpjValid = true;
										cnpjCadast = true;
									} else {
										cnpjValid = false;
										cnpjCadast = false;
									}

									event.getComponent().removeStyleName("invalid-cpf");
									event.getComponent().removeStyleName("valid-cpf");
									event.getComponent().setStyleName("caption-align");
								}
							}
						});

				
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).addListener(new FieldEvents.BlurListener() {

							@Override
							public void blur(BlurEvent event) {
																	
								if (((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().isEmpty()){
									// Validar CNPJ
										if (((TextField)(event.getComponent())).getValue().length() == 14) {
											if (CnpjUtil.validar(((TextField)(event.getComponent())).getValue())) {
												cnpjValid = true;
												((TextField)(event.getComponent())).removeStyleName("invalid-cpf");
												((TextField)(event.getComponent())).setStyleName("valid-cpf");
											} else {
												cnpjValid = false;												
												((TextField)(event.getComponent())).removeStyleName("valid-cpf");
												((TextField)(event.getComponent())).setStyleName("invalid-cpf");
											}
										} else if (((TextField)(event.getComponent())).getValue().length() == 11) {
																					
											if (ValidarCPF.cpfValido(((TextField)(event.getComponent())).getValue())) {
												cnpjValid = true;
												((TextField)(event.getComponent())).removeStyleName("invalid-cpf");
												((TextField)(event.getComponent())).setStyleName("valid-cpf");
											} else {
												cnpjValid = false;												
												((TextField)(event.getComponent())).removeStyleName("valid-cpf");
												((TextField)(event.getComponent())).setStyleName("invalid-cpf");
											}
										} else {
											cnpjValid = false;										
											((TextField)(event.getComponent())).removeStyleName("valid-cpf");
											((TextField)(event.getComponent())).setStyleName("invalid-cpf");
										}
										
										if (cnpjValid) {
											// Verificar Existencia
											ClienteDAO cDAO = new ClienteDAO();									
											Cliente c = cDAO.cpfCnpjCadastrado(OpusERP4UI.getEmpresa(),((TextField)(event.getComponent())).getValue());

											if (c != null && !c.getId().equals(ClienteItem.getItemProperty("id").getValue())) {
												cnpjCadast = false;
												event.getComponent().removeStyleName("valid-cpf");
												event.getComponent().setStyleName("invalid-cpf");
											} else {
												cnpjCadast = true;
												event.getComponent().removeStyleName("invalid-cpf");
												event.getComponent().setStyleName("valid-cpf");										
											}
										}		
								}
										
									
									if (cnpjValid == false || ((TextField)(event.getComponent())).getValue().isEmpty()) {
										Notification.show("CPF/CNPJ Inválido!");	
										
									}else if(cnpjCadast == false){
										Notification.show("CPF/CNPJ Já Cadrastrado!");
									}
							}
						});

				
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).addBlurListener(new FieldEvents.BlurListener() {
					
					@Override
					public void blur(BlurEvent event) {
						((TextField) fieldGroup.getField("doc_rg_insc_estadual")).focus();
					}
				});
			}
		});


		final Button btCpf = new Button();
		btCpf.setStyleName(BaseTheme.BUTTON_LINK);
		btCpf.setIcon(new ThemeResource("icons/browser-16.png"));
		btCpf.setDescription("Pesquisar CPF na Receita Federal");
		
		BrowserWindowOpener openCpf = new BrowserWindowOpener("http://www.receita.fazenda.gov.br/aplicacoes/atcta/cpf/consultapublica.asp");
		openCpf.setFeatures("height=600,width=800");
		openCpf.extend(btCpf);
		
		hl2.addComponent(new FormLayout(){
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
		
		hl2.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom-new_hide_error_cell");	
				addComponent(btCnpj);
			}
		});
		
		
		// RgInsc.Estadual
		hl2.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom-new");
				
				addComponent(fieldGroup.buildAndBind("Insc. Estadual/RG","doc_rg_insc_estadual"));
				((TextField) fieldGroup.getField("doc_rg_insc_estadual")).setRequired(false);
				((TextField) fieldGroup.getField("doc_rg_insc_estadual")).setNullRepresentation("");
				((TextField) fieldGroup.getField("doc_rg_insc_estadual")).setWidth("398px");
				((TextField) fieldGroup.getField("doc_rg_insc_estadual")).setMaxLength(50);
				
				((TextField) fieldGroup.getField("doc_rg_insc_estadual")).addListener(new FieldEvents.BlurListener() {
					
					@Override
					public void blur(BlurEvent event) {
						if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
							((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
						}
						
					}
				});
				
				
			
				
				
			
			}
		});

		vlRoot.addComponent(hl2);

		HorizontalLayout hl3 = new HorizontalLayout();

		// Nome Razão
		hl3.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");

				TextField tfNomeRazao = (TextField) fieldGroup.buildAndBind("",	"nome_razao");
				addComponent(tfNomeRazao);
				tfNomeRazao.setNullRepresentation("");
				tfNomeRazao.setWidth("400px");
				tfNomeRazao.setMaxLength(60);
				tfNomeRazao.setCaption("Nome/Razão Social");
				tfNomeRazao.setStyleName("caption-align");
				tfNomeRazao.setImmediate(true);
				tfNomeRazao.setRequired(true);
				tfNomeRazao.setTextChangeEventMode(TextChangeEventMode.LAZY);
				tfNomeRazao.addBlurListener(new FieldEvents.BlurListener() {
					
					@Override
					public void blur(BlurEvent event) {						
							if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
							((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
						}
	
					}
				});

				setExpandRatio(tfNomeRazao, 2);
			}
		});

		hl3.addComponent(new FormLayout() {
			{

				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				ComboBox cbTratamento = new ComboBox();
				cbTratamento.setWidth(width_tratamento);
				cbTratamento.setNullSelectionAllowed(false);
				cbTratamento.addItem("Sr(a)");
				cbTratamento.addItem("Dr(a)");
				addComponent(cbTratamento);
				fieldGroup.bind(cbTratamento, "tratamento");
			}
		});

		hl3.addComponent(new FormLayout() {
			{
				
				setStyleName("form-cutom");

				TextField tfQserChamado = (TextField) fieldGroup.buildAndBind("", "como_quer_ser_chamado");
				addComponent(tfQserChamado);
				setStyleName("form-cutom");
				tfQserChamado.setWidth(width_apelido);
				tfQserChamado.setMaxLength(100);
				tfQserChamado.setDescription("Como quer ser chamado");			
				tfQserChamado.setNullRepresentation("");
				tfQserChamado.setImmediate(true);
				tfQserChamado.setTextChangeEventMode(TextChangeEventMode.LAZY);
				tfQserChamado.addBlurListener(new FieldEvents.BlurListener() {
							
					@Override
					public void blur(BlurEvent event) {		
							if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
							((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
						}
					}
				});
			}
		});

		vlRoot.addComponent(hl3);

		// Fantasia
		vlRoot.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");

				addComponent(fieldGroup.buildAndBind("Fantasia","nome_fantasia"));
				((TextField) fieldGroup.getField("nome_fantasia")).setWidth("730px");
				((TextField) fieldGroup.getField("nome_fantasia")).setMaxLength(100);
				((TextField) fieldGroup.getField("nome_fantasia")).setNullRepresentation("");
				((TextField) fieldGroup.getField("nome_fantasia")).setStyleName("caption-align");
				((TextField) fieldGroup.getField("nome_fantasia")).setImmediate(true);
				((TextField) fieldGroup.getField("nome_fantasia")).setTextChangeEventMode(TextChangeEventMode.LAZY);
				((TextField) fieldGroup.getField("nome_fantasia")).addBlurListener(new FieldEvents.BlurListener() {					
					@Override
					public void blur(BlurEvent event) {				
							if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
							((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
						}
					}
				});
			}
		});

		// Contato
		vlRoot.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(fieldGroup.buildAndBind("Contato", "contato"));
				((TextField) fieldGroup.getField("contato")).setWidth("730px");
				((TextField) fieldGroup.getField("contato")).setMaxLength(200);
				((TextField) fieldGroup.getField("contato")).setNullRepresentation("");
				((TextField) fieldGroup.getField("contato")).setStyleName("caption-align");
				((TextField) fieldGroup.getField("contato")).setRequired(true);
				((TextField) fieldGroup.getField("contato")).setImmediate(true);
				((TextField) fieldGroup.getField("contato")).setTextChangeEventMode(TextChangeEventMode.LAZY);
				((TextField) fieldGroup.getField("contato")).addBlurListener(new FieldEvents.BlurListener() {
					
					@Override
					public void blur(BlurEvent event) {			
							if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
							((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
						}	
					}
				});
			}
		});

		buildTabSheet();

	}

	private void buildButtons() {
		VerticalLayout vlButtons = new VerticalLayout();
		vlButtons.addComponent(new HorizontalLayout() {
			{
				final HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setMargin(true);
				hlButtons.setSpacing(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());

				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});

	}

	private void buildTabSheet() {

		vlRoot.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");
				setStyleName("horizontal-tabsheet");
				addComponent(new TabSheet() {
					{
						setStyleName("custom-form_tabsheet");						
						setHeight("380px");
						addTab(new VerticalLayout() {
							{

								addComponent(new HorizontalLayout() {
									{
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");
												ComboBox cbSexo = new ComboBox("Sexo");
												fieldGroup.bind(cbSexo, "sexo");
												cbSexo.setWidth("150px");
												cbSexo.setNullSelectionAllowed(false);
												cbSexo.setStyleName("caption-align");
												cbSexo.addItem("MASCULINO");
												cbSexo.addItem("FEMININO");
												addComponent(cbSexo);
											}
										});

										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");
												DateField dfDataNascimento = new DateField("Data de Nascimento");
												dfDataNascimento.setWidth("80px");
												dfDataNascimento.setDateFormat("dd/MM/yyyy");
												dfDataNascimento.setData(new Date());
												
												addComponent(dfDataNascimento);
												fieldGroup.bind(dfDataNascimento,"data_nascimento");

											}
										});
									}
								});

								// Init FieldGroup Endereco
								fgEndereco = new FieldGroup(EndItem);

								addComponent(new HorizontalLayout() {
									{
										// Cep
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");
												addComponent(fgEndereco.buildAndBind("CEP","cep"));
												((TextField) fgEndereco.getField("cep")).setWidth("100px");
												((TextField) fgEndereco.getField("cep")).setNullRepresentation("");
												((TextField) fgEndereco.getField("cep")).setStyleName("caption-align");
												((TextField) fgEndereco.getField("cep")).setMaxLength(8);
												((TextField) fgEndereco.getField("cep")).setRequired(true);
												((TextField) fgEndereco.getField("cep")).setMaxLength(8);
												((TextField) fgEndereco.getField("cep")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEndereco.getField("cep")).addListener(new FieldEvents.TextChangeListener() {
															
															
															@Override
															public void textChange(TextChangeEvent event) {
																
																final String cepEndereco = ((TextField) fgEndereco.getField("cep")).getValue();
																final String cepEndEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
																final String cepEndCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
															
																
																try {
																		if(cbEnderecoEntrega != null && !cbEnderecoEntrega.getValue()){											
																			removeReadOnlyEndEntrega();
																			((TextField) fgEnderecoEntrega.getField("cep")).setValue(event.getText());
																			readOnlyEndEntrega();
																		}
																		if(cbEnderecoCobranca != null && !cbEnderecoCobranca.getValue()){		
																			removeReadOnlyEndCobranca();
																			((TextField) fgEnderecoCobranca.getField("cep")).setValue(event.getText());
																			readOnlyEndCobranca();
																		}	
																    }
																	
																 catch (Exception e) {
																	e.printStackTrace();
																	System.out.println("Erro : "+e.getMessage()+".\n Causado por: "+e.getCause());
																}
																
																
																if (event.getText().length() == 8) {
																	String cepPassado = event.getText();
																	CepUtil utilCep = new CepUtil();
																	if(utilCep.pegarCep(event.getText()) != null){
																		updateCep(utilCep.pegarCep(event.getText()));																	
																		
																		if(cbEnderecoEntrega != null && !cbEnderecoEntrega.getValue()){
																			removeReadOnlyEndEntrega();
																			setEnderecoEntrega();
																			readOnlyEndEntrega();
																		}
																		if(cbEnderecoCobranca != null && !cbEnderecoCobranca.getValue()){
																			removeReadOnlyEndCobranca();
																			setEnderecoCobranca();
																			readOnlyEndCobranca();
																		}
																	}else{
																		com.digital.opuserp.view.util.CepUtil cep = new com.digital.opuserp.view.util.CepUtil(true, true);
																		cep.addListerner(new com.digital.opuserp.view.util.CepUtil.CepListerner() {

																			final String cepEndereco = ((TextField) fgEndereco.getField("cep")).getValue();
																			final String cepEndEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
																			final String cepEndCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
																			@Override
																			public void onSelected(com.digital.opuserp.view.util.CepUtil.CepEvent event) {
																				updateCep(event.getCep());
																					
																				if(cbEnderecoEntrega != null && !cbEnderecoEntrega.getValue()){
																					removeReadOnlyEndEntrega();
																					setEnderecoEntrega();
																					readOnlyEndEntrega();
																				}
																				if(cbEnderecoCobranca != null && !cbEnderecoCobranca.getValue()){
																					removeReadOnlyEndCobranca();
																					setEnderecoCobranca();
																					readOnlyEndCobranca();
																				}										
																			}
																		});
																		cep.procurarCep(cepPassado);
																		getUI().addWindow(cep);
																	}
																}	
																	
															}
														});

												((TextField) fgEndereco.getField("cep")).addListener(new FieldEvents.BlurListener() {

															@Override
															public void blur(BlurEvent event) {
																if(((TextField) fgEndereco.getField("cep")).getValue() != null)
																((TextField) fgEndereco.getField("numero")).focus();																
															}
												});

											}
										});
										

										final Button btSearch = new Button(null, new Button.ClickListener() {

													@Override
													public void buttonClick(ClickEvent event) {
														com.digital.opuserp.view.util.CepUtil cep = new com.digital.opuserp.view.util.CepUtil(true, true);
														cep.addListerner(new com.digital.opuserp.view.util.CepUtil.CepListerner() {

															final String cepEndereco = ((TextField) fgEndereco.getField("cep")).getValue();
															final String cepEndEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
															final String cepEndCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
															@Override
															public void onSelected(com.digital.opuserp.view.util.CepUtil.CepEvent event) {
																updateCep(event.getCep());
																	
																if(cbEnderecoEntrega != null && !cbEnderecoEntrega.getValue()){
																	removeReadOnlyEndEntrega();
																	setEnderecoEntrega();
																	readOnlyEndEntrega();
																}
																if(cbEnderecoCobranca != null && !cbEnderecoCobranca.getValue()){
																	removeReadOnlyEndCobranca();
																	setEnderecoCobranca();
																	readOnlyEndCobranca();
																}										
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

								addComponent(new HorizontalLayout() {
									{
										setWidth("90%");
										FormLayout flEndereco = new FormLayout();
										flEndereco.setMargin(true);
										flEndereco.setSpacing(true);
										flEndereco.setWidth("100%");
										flEndereco.setStyleName("form-cutom");
										flEndereco.addComponent(fgEndereco.buildAndBind("Endereço","endereco"));
										((TextField) fgEndereco.getField("endereco")).setWidth("100%");
										((TextField) fgEndereco.getField("endereco")).setNullRepresentation("");
										((TextField) fgEndereco.getField("endereco")).setStyleName("caption-align");
										((TextField) fgEndereco.getField("endereco")).setRequired(true);
										((TextField) fgEndereco.getField("endereco")).setImmediate(true);
										((TextField) fgEndereco.getField("endereco")).setTextChangeEventMode(TextChangeEventMode.LAZY);
										((TextField) fgEndereco.getField("endereco")).addBlurListener(new FieldEvents.BlurListener() {

											@Override
											public void blur(BlurEvent event) {												
												if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
													
													((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
												}
											}
										});
										
										
										
										((TextField) fgEndereco.getField("endereco")).addListener(new FieldEvents.TextChangeListener() {

											@Override
											public void textChange(TextChangeEvent event) {
												
												final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
												final String endEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
												final String endCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
												
//												if (ClienteItem.getItemProperty("id").getValue() == null) {
//													removeReadOnlyEndEntrega();
//													removeReadOnlyEndCobranca();																	
//													((TextField) fgEnderecoEntrega.getField("endereco")).setValue(event.getText());
//													((TextField) fgEnderecoCobranca.getField("endereco")).setValue(event.getText());
//													readOnlyEndEntrega();
//													readOnlyEndCobranca();
//										    	}else{
												try {
													if(endereco.equals(endEntrega)){											
														removeReadOnlyEndEntrega();
														((TextField) fgEnderecoEntrega.getField("endereco")).setValue(event.getText());
														readOnlyEndEntrega();
													}
													if(endereco.equals(endCobranca)){		
														removeReadOnlyEndCobranca();
														((TextField) fgEnderecoCobranca.getField("endereco")).setValue(event.getText());
														readOnlyEndCobranca();
													}	
													
												} catch (Exception e) {
													e.printStackTrace();
													System.out.println("Erro : "+e.getMessage()+".\n Causado por: "+e.getCause());
												}
//													}	
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
												addComponent(fgEndereco.buildAndBind("Nº","numero"));
												((TextField) fgEndereco.getField("numero")).setWidth(width_numero);
												((TextField) fgEndereco.getField("numero")).setNullRepresentation("");
												((TextField) fgEndereco.getField("numero")).setRequired(true);
												((TextField) fgEndereco.getField("numero")).setMaxLength(10);
												((TextField) fgEndereco.getField("numero")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEndereco.getField("numero")).addListener(new FieldEvents.TextChangeListener() {

															@Override
															public void textChange(TextChangeEvent event) {
																
																final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
																final String endEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
																final String endCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
																
//																if (ClienteItem.getItemProperty("id").getValue() == null) {	
//																	removeReadOnlyEndEntrega();
//																	removeReadOnlyEndCobranca();																	
//																	((TextField) fgEnderecoEntrega.getField("numero")).setValue(event.getText());
//																	((TextField) fgEnderecoCobranca.getField("numero")).setValue(event.getText());
//																	readOnlyEndEntrega();
//																	readOnlyEndCobranca();
//																	
//														    	}else{
																try {
																	if(endereco.equals(endEntrega)){											
																		removeReadOnlyEndEntrega();
																		((TextField) fgEnderecoEntrega.getField("numero")).setValue(event.getText());
																		readOnlyEndEntrega();
																	}
																	if(endereco.equals(endCobranca)){		
																		removeReadOnlyEndCobranca();
																		((TextField) fgEnderecoCobranca.getField("numero")).setValue(event.getText());
																		readOnlyEndCobranca();
																	}	
																	
																} catch (Exception e) {
																	e.printStackTrace();
																	System.out.println("Erro : "+e.getMessage()+".\n Causado por: "+e.getCause());
																}
//														    	}
															}
														});
											}
										});
									}
								});

								// Complemento, Bairro,Cidade, UF, Pais

								addComponent(new HorizontalLayout() {
									{
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												addComponent(fgEndereco
														.buildAndBind("Bairro",
																"bairro"));
												((TextField) fgEndereco
														.getField("bairro"))
														.setNullRepresentation("");
												((TextField) fgEndereco
														.getField("bairro"))
														.setStyleName("caption-align");
												((TextField) fgEndereco
														.getField("bairro"))
														.setRequired(true);
												((TextField) fgEndereco
														.getField("bairro"))
														.setWidth("200px");
												((TextField) fgEndereco.getField("bairro")).setImmediate(true);
												((TextField) fgEndereco.getField("bairro")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEndereco.getField("bairro")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
														}

													}
												});
												
												((TextField) fgEndereco.getField("bairro")).addListener(new FieldEvents.TextChangeListener() {

													@Override
													public void textChange(TextChangeEvent event) {
														
														final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
														final String endEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
														final String endCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
														
//														if (ClienteItem.getItemProperty("id").getValue() == null) {
//															removeReadOnlyEndEntrega();
//															removeReadOnlyEndCobranca();																	
//															((TextField) fgEnderecoEntrega.getField("bairro")).setValue(event.getText());
//															((TextField) fgEnderecoCobranca.getField("bairro")).setValue(event.getText());
//															readOnlyEndEntrega();
//															readOnlyEndCobranca();
//												    	}else{
														try {
															if(endereco.equals(endEntrega)){											
																removeReadOnlyEndEntrega();
																((TextField) fgEnderecoEntrega.getField("bairro")).setValue(event.getText());
																readOnlyEndEntrega();
															}
															if(endereco.equals(endCobranca)){		
																removeReadOnlyEndCobranca();
																((TextField) fgEnderecoCobranca.getField("bairro")).setValue(event.getText());
																readOnlyEndCobranca();
															}	
															
														} catch (Exception e) {
															e.printStackTrace();
															System.out.println("Erro : "+e.getMessage()+".\n Causado por: "+e.getCause());
														}
//												    	}
													}
												});
											}
										});

										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												addComponent(fgEndereco
														.buildAndBind("Cidade",
																"cidade"));
												((TextField) fgEndereco
														.getField("cidade"))
														.setNullRepresentation("");
												((TextField) fgEndereco
														.getField("cidade"))
														.setRequired(true);
												((TextField) fgEndereco
														.getField("cidade"))
														.setWidth(width_cidade);
												((TextField) fgEndereco.getField("cidade")).setImmediate(true);
												((TextField) fgEndereco.getField("cidade")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEndereco.getField("cidade")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
														}

													}
												});
												
												((TextField) fgEndereco.getField("cidade")).addListener(new FieldEvents.TextChangeListener() {

													@Override
													public void textChange(TextChangeEvent event) {

														final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
														final String endEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
														final String endCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
														
//														if (ClienteItem.getItemProperty("id").getValue() == null) {
//															removeReadOnlyEndEntrega();
//															removeReadOnlyEndCobranca();																	
//															((TextField) fgEnderecoEntrega.getField("cidade")).setValue(event.getText());
//															((TextField) fgEnderecoCobranca.getField("cidade")).setValue(event.getText());
//															readOnlyEndEntrega();
//															readOnlyEndCobranca();
//												    	}else{
														try {
															if(endereco.equals(endEntrega)){											
																removeReadOnlyEndEntrega();
																((TextField) fgEnderecoEntrega.getField("cidade")).setValue(event.getText());
																readOnlyEndEntrega();
															}
															if(endereco.equals(endCobranca)){		
																removeReadOnlyEndCobranca();
																((TextField) fgEnderecoCobranca.getField("cidade")).setValue(event.getText());
																readOnlyEndCobranca();
															}	
															
														} catch (Exception e) {
															e.printStackTrace();
															System.out.println("Erro : "+e.getMessage()+".\n Causado por: "+e.getCause());
														}	
//												    	}
													}
												});
												
											}
										});

										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												addComponent(fgEndereco
														.buildAndBind("UF",
																"uf"));
												((TextField) fgEndereco
														.getField("uf"))
														.setWidth("42px");
												((TextField) fgEndereco
														.getField("uf"))
														.setNullRepresentation("");
												((TextField) fgEndereco
														.getField("uf"))
														.setRequired(true);
												((TextField) fgEndereco.getField("uf")).setImmediate(true);
												((TextField) fgEndereco.getField("uf")).setTextChangeEventMode(TextChangeEventMode.LAZY);												
												((TextField) fgEndereco.getField("uf")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
															if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
														}

													}
												});
												
												((TextField) fgEndereco.getField("uf")).addListener(new FieldEvents.TextChangeListener() {

													@Override
													public void textChange(TextChangeEvent event) {
														
														final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
														final String endEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
														final String endCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
														
//														if (ClienteItem.getItemProperty("id").getValue() == null) {
//															removeReadOnlyEndEntrega();
//															removeReadOnlyEndCobranca();																	
//															((TextField) fgEnderecoEntrega.getField("uf")).setValue(event.getText());
//															((TextField) fgEnderecoCobranca.getField("uf")).setValue(event.getText());
//															readOnlyEndEntrega();
//															readOnlyEndCobranca();
//												    	}else{
														try {
															if(endereco.equals(endEntrega)){											
																removeReadOnlyEndEntrega();
																((TextField) fgEnderecoEntrega.getField("uf")).setValue(event.getText());
																readOnlyEndEntrega();
															}
															if(endereco.equals(endCobranca)){		
																removeReadOnlyEndCobranca();
																((TextField) fgEnderecoCobranca.getField("uf")).setValue(event.getText());
																readOnlyEndCobranca();
															}	
															
														} catch (Exception e) {
															e.printStackTrace();
															System.out.println("Erro : "+e.getMessage()+".\n Causado por: "+e.getCause());
														}	
//												    	}
													}
												});
											}
										});

										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												addComponent(fgEndereco
														.buildAndBind("País",
																"pais"));
												((TextField) fgEndereco
														.getField("pais"))
														.setNullRepresentation("");
												((TextField) fgEndereco
														.getField("pais"))
														.setWidth(width_pais);
												((TextField) fgEndereco
														.getField("pais"))
														.setRequired(true);
												((TextField) fgEndereco.getField("pais")).setImmediate(true);
												((TextField) fgEndereco.getField("pais")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEndereco.getField("pais")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
														}
														
														
													}
												});
												
												((TextField) fgEndereco.getField("pais")).addListener(new FieldEvents.TextChangeListener() {

													@Override
													public void textChange(TextChangeEvent event) {
														
														final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
														final String endEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
														final String endCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
														
//														if (ClienteItem.getItemProperty("id").getValue() == null) {
//															removeReadOnlyEndEntrega();
//															removeReadOnlyEndCobranca();																	
//															((TextField) fgEnderecoEntrega.getField("pais")).setValue(event.getText());
//															((TextField) fgEnderecoCobranca.getField("pais")).setValue(event.getText());
//															readOnlyEndEntrega();
//															readOnlyEndCobranca();
//												    	}else{
														try {
															if(endereco.equals(endEntrega)){											
																removeReadOnlyEndEntrega();
																((TextField) fgEnderecoEntrega.getField("pais")).setValue(event.getText());
																readOnlyEndEntrega();
															}
															if(endereco.equals(endCobranca)){		
																removeReadOnlyEndCobranca();
																((TextField) fgEnderecoCobranca.getField("pais")).setValue(event.getText());
																readOnlyEndCobranca();
															}	
															
														} catch (Exception e) {
															e.printStackTrace();
															System.out.println("Erro : "+e.getMessage()+".\n Causado por: "+e.getCause());
														}
//												    	}
													}
												});
											}
										});

									}
								});

								addComponent(new HorizontalLayout() {
									{
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												addComponent(fgEndereco.buildAndBind(
																"Complemento",
																"complemento"));
												((TextField) fgEndereco
														.getField("complemento"))
														.setNullRepresentation("");
												((TextField) fgEndereco
														.getField("complemento"))
														.setStyleName("caption-align");
												((TextField) fgEndereco
														.getField("complemento"))
														.setWidth(width_complemento);
												((TextField) fgEndereco.getField("complemento")).setImmediate(true);
												((TextField) fgEndereco.getField("complemento")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEndereco.getField("complemento")).setRequired(true);
												((TextField) fgEndereco.getField("complemento")).setDescription("Casa, Apto, Galpão, Anexo, etc");
												((TextField) fgEndereco.getField("complemento")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
														}

													}
												});
												
												((TextField) fgEndereco.getField("complemento")).addListener(new FieldEvents.TextChangeListener() {

													@Override
													public void textChange(TextChangeEvent event) {
														
														final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
														final String endEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
														final String endCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
														
//														if (ClienteItem.getItemProperty("id").getValue() == null) {
//															removeReadOnlyEndEntrega();
//															removeReadOnlyEndCobranca();																	
//															((TextField) fgEnderecoEntrega.getField("complemento")).setValue(event.getText());
//															((TextField) fgEnderecoCobranca.getField("complemento")).setValue(event.getText());
//															readOnlyEndEntrega();
//															readOnlyEndCobranca();
//												    	}else{
														try {
															if(endereco.equals(endEntrega)){											
																removeReadOnlyEndEntrega();
																((TextField) fgEnderecoEntrega.getField("complemento")).setValue(event.getText());
																readOnlyEndEntrega();
															}
															if(endereco.equals(endCobranca)){		
																removeReadOnlyEndCobranca();
																((TextField) fgEnderecoCobranca.getField("complemento")).setValue(event.getText());
																readOnlyEndCobranca();
															}	
															
														} catch (Exception e) {
															e.printStackTrace();
															System.out.println("Erro : "+e.getMessage()+".\n Causado por: "+e.getCause());
														}
//												    	}
													}
												});
											}
										});

										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												addComponent(fgEndereco
														.buildAndBind(
																"Referência",
																"referencia"));
												((TextField) fgEndereco
														.getField("referencia"))
														.setNullRepresentation("");
												((TextField) fgEndereco
														.getField("referencia"))
														.setWidth(width_referenc);
												((TextField) fgEndereco.getField("referencia")).setImmediate(true);
												((TextField) fgEndereco.getField("referencia")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEndereco.getField("referencia")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
														}
													}
												});
												
												((TextField) fgEndereco.getField("referencia")).addListener(new FieldEvents.TextChangeListener() {

													@Override
													public void textChange(TextChangeEvent event) {
														
														final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
														final String endEntrega = ((TextField) fgEnderecoEntrega.getField("cep")).getValue();
														final String endCobranca = ((TextField) fgEnderecoCobranca.getField("cep")).getValue();
														
//														if (ClienteItem.getItemProperty("id").getValue() == null) {
//															removeReadOnlyEndEntrega();
//															removeReadOnlyEndCobranca();																	
//															((TextField) fgEnderecoEntrega.getField("referencia")).setValue(event.getText());
//															((TextField) fgEnderecoCobranca.getField("referencia")).setValue(event.getText());
//															readOnlyEndEntrega();
//															readOnlyEndCobranca();
//												    	}else{
														try {
															if(endereco.equals(endEntrega)){											
																removeReadOnlyEndEntrega();
																((TextField) fgEnderecoEntrega.getField("referencia")).setValue(event.getText());
																readOnlyEndEntrega();
															}
															if(endereco.equals(endCobranca)){		
																removeReadOnlyEndCobranca();
																((TextField) fgEnderecoCobranca.getField("referencia")).setValue(event.getText());
																readOnlyEndCobranca();
															}	
															
														} catch (Exception e) {
															e.printStackTrace();
															System.out.println("Erro : "+e.getMessage()+".\n Causado por: "+e.getCause());
														}	
//												    	}
													}
												});
												
											}
										});
									}
								});

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
												// ((TextField)
												// fieldGroup.getField("telefone1")).setStyleName("caption-align");
												// ((TextField)
												// fieldGroup.getField("telefone1")).setRequired(true);
												((TextField) fieldGroup.getField("telefone1")).setWidth("105px");
												((TextField) fieldGroup.getField("telefone1")).setMaxLength(11);
												((TextField) fieldGroup.getField("telefone1")).setRequired(true);
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
												((TextField) fieldGroup.getField("email")).setWidth("200px");
												((TextField) fieldGroup.getField("email")).setMaxLength(50);
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
											}
										});
									}
								});
													

								addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");

										addComponent(fieldGroup.buildAndBind(
												"Email Alternativo",
												"emailAlternativo"));
										((TextField) fieldGroup
												.getField("emailAlternativo"))
												.setNullRepresentation("");
										((TextField) fieldGroup
												.getField("emailAlternativo"))
												.setStyleName("caption-align");
										((TextField) fieldGroup
												.getField("emailAlternativo"))
												.setWidth("200px");
										((TextField) fieldGroup.getField("emailAlternativo")).setMaxLength(100);
									}
								});

								// Categoria, Como Nos Conheceu?
								addComponent(new HorizontalLayout() {
									{
										addComponent(new FormLayout() {
											{
												
												try {
													setMargin(true);
													setSpacing(true);
													setStyleName("form-cutom-new");

													final ComboBox cbCategoria = new ComboBox("Categoria", containerCategorias);
													cbCategoria.setNullSelectionAllowed(false);
													cbCategoria.setItemCaptionPropertyId("nome");
													cbCategoria.setRequired(true);
													cbCategoria.setConverter(new SingleSelectConverter(cbCategoria));
													fieldGroup.bind(cbCategoria, "categoria");
													cbCategoria.setWidth(width_categoria);
													addComponent(cbCategoria);
													cbCategoria.setStyleName("caption-align");
													
													if(ClienteItem.getItemProperty("id").getValue() != null && cbCategoria.getValue() == null){
														LogErrorDAO.add(new LogError(null, "ClienteEditor", "Teste categoria", "Não Foi Possivel Selecionar Categoria", 
																OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
													}
													
												} catch (Exception e) {													
													LogErrorDAO.add(new LogError(null, "ClienteEditor", "Teste categoria", e.getMessage(), 
															OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
													e.printStackTrace();
												}
												
											}
										});

										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");

												
												JPAContainer<ComoNosConheceu> containersComoNosConheceu = JPAContainerFactory.make(ComoNosConheceu.class, ConnUtil.getEntity());
												ComboBox cbComoNosConheceu = new ComboBox("Como nos Conheceu?", containersComoNosConheceu);
												cbComoNosConheceu.setNullSelectionAllowed(true);												
												cbComoNosConheceu.setRequired(false);
												cbComoNosConheceu.setConverter(new SingleSelectConverter(cbComoNosConheceu));
												cbComoNosConheceu.setItemCaptionPropertyId("nome");

												addComponent(cbComoNosConheceu);
												fieldGroup.bind(cbComoNosConheceu,"como_nos_conheceu");

											}
										});
									}
								});

								addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");

										final TextArea taObs = new TextArea("Observação");
										fieldGroup.bind(taObs, "obs");
										taObs.setNullRepresentation("");
										taObs.setStyleName("caption-align");
										taObs.addStyleName("text-area-border");
										taObs.setWidth("95%");
										taObs.setHeight("45px");
										taObs.setMaxLength(200);
										taObs.setImmediate(true);
										taObs.setTextChangeEventMode(TextChangeEventMode.LAZY);
										taObs.addBlurListener(new FieldEvents.BlurListener() {
											
											@Override
											public void blur(BlurEvent event) {
												if(((TextArea)(event.getComponent())) != null && ((TextArea)(event.getComponent())).getValue() != null && !((TextArea)(event.getComponent())).getValue().toString().isEmpty()){
													
													((TextArea)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
												}
											}
										});
										addComponent(taObs);

									}
								});

							}
						}, "Principal");

						fgEnderecoEntrega = new FieldGroup(EndEntreItem);
						fgEnderecoCobranca = new FieldGroup(EndCobrancaItem);

						// Tab de Outros Endereços
						addTab(new VerticalLayout() {
							{

								Label lbTituloEntre = new Label("<h1 class='title_outros_end'>Endereço de Entrega</h1>",ContentMode.HTML);
								lbTituloEntre.addStyleName("title_outros_end");
								addComponent(lbTituloEntre);

								cbEnderecoEntrega = new CheckBox("Utilizar Endereço Diferente do Endereço Principal");
								
								if(ClienteItem.getItemProperty("id").getValue() != null){
									
									if(fgEndereco.getItemDataSource().getItemProperty("cep").getValue() != null && fgEnderecoEntrega.getItemDataSource().getItemProperty("cep").getValue() != null){
										if(fgEndereco.getItemDataSource().getItemProperty("cep").getValue().equals(fgEnderecoEntrega.getItemDataSource().getItemProperty("cep").getValue()
												 ) || fgEnderecoEntrega.getItemDataSource().getItemProperty("cep").getValue() == null){
											cbEnderecoEntrega.setValue(false);
										}else{
											cbEnderecoEntrega.setValue(true);
										}
									}else{
										cbEnderecoEntrega.setValue(false);
									}
								
								}
								
								cbEnderecoEntrega.addListener(new Property.ValueChangeListener() {
									
											@Override
											public void valueChange(ValueChangeEvent event) {									
												
												if (cbEnderecoEntrega.getValue() == true) {													
													((TextField) fgEnderecoEntrega.getField("cep")).setReadOnly(false);
													((TextField) fgEnderecoEntrega.getField("endereco")).setReadOnly(false);
													((TextField) fgEnderecoEntrega.getField("numero")).setReadOnly(false);
													((TextField) fgEnderecoEntrega.getField("cidade")).setReadOnly(false);
													((TextField) fgEnderecoEntrega.getField("bairro")).setReadOnly(false);
													((TextField) fgEnderecoEntrega.getField("uf")).setReadOnly(false);
													((TextField) fgEnderecoEntrega.getField("pais")).setReadOnly(false);
													((TextField) fgEnderecoEntrega.getField("complemento")).setReadOnly(false);
													((TextField) fgEnderecoEntrega.getField("referencia")).setReadOnly(false);
													
													clearEnderecoEntrega();
													
												} else {
													setEnderecoEntrega();
													
													((TextField) fgEnderecoEntrega.getField("cep")).setReadOnly(true);
													((TextField) fgEnderecoEntrega.getField("endereco")).setReadOnly(true);
													((TextField) fgEnderecoEntrega.getField("numero")).setReadOnly(true);
													((TextField) fgEnderecoEntrega.getField("cidade")).setReadOnly(true);
													((TextField) fgEnderecoEntrega.getField("bairro")).setReadOnly(true);
													((TextField) fgEnderecoEntrega.getField("uf")).setReadOnly(true);
													((TextField) fgEnderecoEntrega.getField("pais")).setReadOnly(true);
													((TextField) fgEnderecoEntrega.getField("complemento")).setReadOnly(true);
													((TextField) fgEnderecoEntrega.getField("referencia")).setReadOnly(true);
												}
											}
								});

								addComponent(cbEnderecoEntrega);

								// if(EndEntreItem.getItemProperty("cep").getValue()
								// !=
								// EndEntreItem.getItemProperty("cep").getValue()){
								// checkEndEntrega.setValue(true);
								// }

								// Cep
								addComponent(new HorizontalLayout() {
									{

										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");
												addComponent(fgEnderecoEntrega.buildAndBind("CEP","cep"));
												((TextField) fgEnderecoEntrega.getField("cep")).setWidth("150px");
												((TextField) fgEnderecoEntrega.getField("cep")).setNullRepresentation("");
												((TextField) fgEnderecoEntrega.getField("cep")).setStyleName("caption-align");
												((TextField) fgEnderecoEntrega.getField("cep")).setRequired(true);
												((TextField) fgEnderecoEntrega.getField("cep")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoEntrega.getField("cep")).addListener(new FieldEvents.TextChangeListener() {

															@Override
															public void textChange(TextChangeEvent event) {
																if (event.getText().length() == 8) {
																	CepUtil utilCep = new CepUtil();
																	try {
																		updateCepEntrega(utilCep.pegarCep(event.getText()));
																	} catch (Exception e) {
																		e.printStackTrace();
																		System.out.println("Erro ao pegar CEP: "
																						+ e.getMessage()
																						+ " |Causado por: "
																						+ e.getCause());

																	}
																}
															}
														});

											}

										});

										final Button btSearch = new Button(null,new Button.ClickListener() {
											  
													@Override
													public void buttonClick(ClickEvent event) {
														if(!cbEnderecoEntrega.getValue()){
															Notification.show("Escolhar por Utilizar Endereço Diferente Antes de Fazer a Busca!");
														}else{
															
															com.digital.opuserp.view.util.CepUtil cep = new com.digital.opuserp.view.util.CepUtil(true, true);
															cep.addListerner(new com.digital.opuserp.view.util.CepUtil.CepListerner() {
																
																@Override
																public void onSelected(
																		com.digital.opuserp.view.util.CepUtil.CepEvent event) {
																	updateCepEntrega(event.getCep());
																}
															});
															
															getUI().addWindow(cep);
														}
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

								addComponent(new HorizontalLayout() {
									{
										setWidth("90%");
										FormLayout flEndereco = new FormLayout();
										flEndereco.setMargin(true);
										flEndereco.setSpacing(true);
										flEndereco.setWidth("100%");
										flEndereco.setStyleName("form-cutom");
										flEndereco.addComponent(fgEnderecoEntrega.buildAndBind("Endereço","endereco"));
																			
										((TextField) fgEnderecoEntrega.getField("endereco")).setWidth("100%");
										((TextField) fgEnderecoEntrega.getField("endereco")).setNullRepresentation("");
										((TextField) fgEnderecoEntrega.getField("endereco")).setStyleName("caption-align");
										((TextField) fgEnderecoEntrega.getField("endereco")).setRequired(true);
										((TextField) fgEnderecoEntrega.getField("endereco")).setImmediate(true);
										((TextField) fgEnderecoEntrega.getField("endereco")).setTextChangeEventMode(TextChangeEventMode.LAZY);
										((TextField) fgEnderecoEntrega.getField("endereco")).addBlurListener(new FieldEvents.BlurListener() {
											
											@Override
											public void blur(BlurEvent event) {
												if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
													
													((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
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
												addComponent(fgEnderecoEntrega
														.buildAndBind("Nº",
																"numero"));
												((TextField) fgEnderecoEntrega
														.getField("numero"))
														.setWidth(width_numero);
												((TextField) fgEnderecoEntrega
														.getField("numero"))
														.setNullRepresentation("");
												((TextField) fgEnderecoEntrega
														.getField("numero"))
														.setRequired(true);
												((TextField) fgEnderecoEntrega.getField("numero")).setMaxLength(10);
											}
										});
									}
								});

								// Complemento, Bairro,Cidade, UF, Pais

								addComponent(new HorizontalLayout() {
									{
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												addComponent(fgEnderecoEntrega
														.buildAndBind("Bairro",
																"bairro"));
												((TextField) fgEnderecoEntrega
														.getField("bairro"))
														.setNullRepresentation("");
												((TextField) fgEnderecoEntrega
														.getField("bairro"))
														.setStyleName("caption-align");
												((TextField) fgEnderecoEntrega
														.getField("bairro"))
														.setRequired(true);
												((TextField) fgEnderecoEntrega
														.getField("bairro"))
														.setWidth("200px");
												((TextField) fgEnderecoEntrega.getField("bairro")).setImmediate(true);
												((TextField) fgEnderecoEntrega.getField("bairro")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoEntrega.getField("bairro")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
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
												addComponent(fgEnderecoEntrega
														.buildAndBind("Cidade",
																"cidade"));
												((TextField) fgEnderecoEntrega
														.getField("cidade"))
														.setNullRepresentation("");
												((TextField) fgEnderecoEntrega
														.getField("cidade"))
														.setRequired(true);
												((TextField) fgEnderecoEntrega
														.getField("cidade"))
														.setWidth(width_cidade);
												((TextField) fgEnderecoEntrega.getField("cidade")).setImmediate(true);
												((TextField) fgEnderecoEntrega.getField("cidade")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoEntrega.getField("cidade")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
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
												addComponent(fgEnderecoEntrega
														.buildAndBind("UF",
																"uf"));
												((TextField) fgEnderecoEntrega
														.getField("uf"))
														.setWidth("42px");
												((TextField) fgEnderecoEntrega
														.getField("uf"))
														.setNullRepresentation("");
												((TextField) fgEnderecoEntrega
														.getField("uf"))
														.setRequired(true);
												((TextField) fgEnderecoEntrega.getField("uf")).setImmediate(true);
												((TextField) fgEnderecoEntrega.getField("uf")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoEntrega.getField("uf")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
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
												addComponent(fgEnderecoEntrega
														.buildAndBind("Pais",
																"pais"));
												((TextField) fgEnderecoEntrega
														.getField("pais"))
														.setNullRepresentation("");
												((TextField) fgEnderecoEntrega
														.getField("pais"))
														.setWidth(width_pais);
												((TextField) fgEnderecoEntrega
														.getField("pais"))
														.setRequired(true);
												((TextField) fgEnderecoEntrega.getField("pais")).setImmediate(true);
												((TextField) fgEnderecoEntrega.getField("pais")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoEntrega.getField("pais")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
														}	
														}
												});
											}
										});

									}
								});
								addComponent(new HorizontalLayout() {
									{
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												addComponent(fgEnderecoEntrega
														.buildAndBind(
																"Complemento",
																"complemento"));
												((TextField) fgEnderecoEntrega
														.getField("complemento"))
														.setNullRepresentation("");
												((TextField) fgEnderecoEntrega
														.getField("complemento"))
														.setStyleName("caption-align");
												((TextField) fgEnderecoEntrega
														.getField("complemento"))
														.setWidth(width_complemento);
												((TextField) fgEnderecoEntrega.getField("complemento")).setImmediate(true);
												((TextField) fgEnderecoEntrega.getField("complemento")).setRequired(true);
												((TextField) fgEnderecoEntrega.getField("complemento")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoEntrega.getField("complemento")).setDescription("Casa, Apto, Galpão, Anexo, etc");	
												((TextField) fgEnderecoEntrega.getField("complemento")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
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
												addComponent(fgEnderecoEntrega
														.buildAndBind(
																"Referência",
																"referencia"));
												((TextField) fgEnderecoEntrega
														.getField("referencia"))
														.setNullRepresentation("");
												((TextField) fgEnderecoEntrega
														.getField("referencia"))
														.setWidth(width_referenc);
												((TextField) fgEnderecoEntrega.getField("referencia")).setImmediate(true);
												((TextField) fgEnderecoEntrega.getField("referencia")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoEntrega.getField("referencia")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
														}
													}
												});
											}
										});
									}
								});

								if(fgEnderecoEntrega.getItemDataSource().getItemProperty("cep").getValue() == null){
									setEnderecoEntrega();
								}									
									readOnlyEndEntrega();
									
									
								
								
								Label lbTituloCobran = new Label(
										"<h1 class='title_outros_end'>Endereço de Cobrança</h1>",
										ContentMode.HTML);
								lbTituloCobran.addStyleName("title_outros_end");
								addComponent(lbTituloCobran);

								cbEnderecoCobranca = new CheckBox("Utilizar Endereço Diferente do Endereço Principal");
								
								if(ClienteItem.getItemProperty("id").getValue() != null){	
									
									if(fgEndereco.getItemDataSource().getItemProperty("cep").getValue() != null && 
											fgEndereco.getItemDataSource().getItemProperty("cep").getValue() != null){
										if(fgEndereco.getItemDataSource().getItemProperty("cep").getValue().equals(fgEnderecoCobranca.getItemDataSource().getItemProperty("cep").getValue()
												 ) || fgEnderecoCobranca.getItemDataSource().getItemProperty("cep").getValue() == null){
											cbEnderecoCobranca.setValue(false);
										}else{
											cbEnderecoCobranca.setValue(true);
										}
									
									}else{
										cbEnderecoCobranca.setValue(false);
									}
								}
							
								cbEnderecoCobranca.addListener(new Property.ValueChangeListener() {

											@Override
											public void valueChange(ValueChangeEvent event) {
												
												if (cbEnderecoCobranca.getValue() == true) {													
													((TextField) fgEnderecoCobranca.getField("cep")).setReadOnly(false);
													((TextField) fgEnderecoCobranca.getField("endereco")).setReadOnly(false);
													((TextField) fgEnderecoCobranca.getField("numero")).setReadOnly(false);
													((TextField) fgEnderecoCobranca.getField("cidade")).setReadOnly(false);
													((TextField) fgEnderecoCobranca.getField("bairro")).setReadOnly(false);
													((TextField) fgEnderecoCobranca.getField("uf")).setReadOnly(false);
													((TextField) fgEnderecoCobranca.getField("pais")).setReadOnly(false);
													((TextField) fgEnderecoCobranca.getField("complemento")).setReadOnly(false);
													((TextField) fgEnderecoCobranca.getField("referencia")).setReadOnly(false);
													
													clearEnderecoCobranca();
													
												} else {
													setEnderecoCobranca();
													
													((TextField) fgEnderecoCobranca.getField("cep")).setReadOnly(true);
													((TextField) fgEnderecoCobranca.getField("endereco")).setReadOnly(true);
													((TextField) fgEnderecoCobranca.getField("numero")).setReadOnly(true);
													((TextField) fgEnderecoCobranca.getField("cidade")).setReadOnly(true);
													((TextField) fgEnderecoCobranca.getField("bairro")).setReadOnly(true);
													((TextField) fgEnderecoCobranca.getField("uf")).setReadOnly(true);
													((TextField) fgEnderecoCobranca.getField("pais")).setReadOnly(true);
													((TextField) fgEnderecoCobranca.getField("complemento")).setReadOnly(true);
													((TextField) fgEnderecoCobranca.getField("referencia")).setReadOnly(true);
												}
											}
										});

								// if(EndItem.getItemProperty("cep").getValue()
								// !=
								// EndEntreItem.getItemProperty("cep").getValue()){
								// checkEndCobranca.setValue(true);
								// }

								addComponent(cbEnderecoCobranca);

								addComponent(new HorizontalLayout() {
									{
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");
												addComponent(fgEnderecoCobranca
														.buildAndBind("CEP",
																"cep"));
												((TextField) fgEnderecoCobranca
														.getField("cep"))
														.setWidth("150px");
												((TextField) fgEnderecoCobranca
														.getField("cep"))
														.setNullRepresentation("");
												((TextField) fgEnderecoCobranca
														.getField("cep"))
														.setStyleName("caption-align");
												((TextField) fgEnderecoCobranca
														.getField("cep"))
														.setRequired(true);
												((TextField) fgEnderecoCobranca
														.getField("cep"))
														.setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoCobranca
														.getField("cep"))
														.addListener(new FieldEvents.TextChangeListener() {

															@Override
															public void textChange(
																	TextChangeEvent event) {
																if (event
																		.getText()
																		.length() == 8) {
																	CepUtil util = new CepUtil();
																	updateCepCobranca(util
																			.pegarCep(event
																					.getText()));
																}

															}
														});
											}
										});

										final Button btSearch = new Button(null,new Button.ClickListener() {

													@Override
													public void buttonClick(ClickEvent event) {
														
														if(!cbEnderecoCobranca.getValue()){
															Notification.show("Escolhar por Utilizar Endereço Diferente Antes de Fazer a Busca!");
														}else{
															
															com.digital.opuserp.view.util.CepUtil cep = new com.digital.opuserp.view.util.CepUtil(true, true);
															cep.addListerner(new com.digital.opuserp.view.util.CepUtil.CepListerner() {
																
																@Override
																public void onSelected(
																		com.digital.opuserp.view.util.CepUtil.CepEvent event) {
																	updateCepCobranca(event.getCep());
																}
															});
															
															getUI().addWindow(cep);
														}
														
													}
												});
										btSearch.setStyleName(BaseTheme.BUTTON_LINK);
										btSearch.setIcon(new ThemeResource(
												"icons/search-16.png"));
										btSearch.setDescription("Procurar por Cep");

										addComponent(new FormLayout() {
											{
												setStyleName("form-cutom-new");
												addComponent(btSearch);
											}
										});

										final Button btEnvelope = new Button();
										btEnvelope
												.setStyleName(BaseTheme.BUTTON_LINK);
										btEnvelope.setIcon(new ThemeResource(
												"icons/browser-16.png"));
										btEnvelope
												.setDescription("Pesquisar nos Correios");

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

								// Endereco e Número

								addComponent(new HorizontalLayout() {
									{
										setWidth("90%");
										FormLayout flEndereco = new FormLayout();
										flEndereco.setMargin(true);
										flEndereco.setSpacing(true);
										flEndereco.setWidth("100%");
										flEndereco.setStyleName("form-cutom");
										flEndereco
												.addComponent(fgEnderecoCobranca
														.buildAndBind(
																"Endereço",
																"endereco"));
										((TextField) fgEnderecoCobranca
												.getField("endereco"))
												.setWidth("100%");
										((TextField) fgEnderecoCobranca
												.getField("endereco"))
												.setNullRepresentation("");
										((TextField) fgEnderecoCobranca
												.getField("endereco"))
												.setStyleName("caption-align");
										((TextField) fgEnderecoCobranca
												.getField("endereco"))
												.setRequired(true);
										((TextField) fgEnderecoCobranca.getField("endereco")).setImmediate(true);
										((TextField) fgEnderecoCobranca.getField("endereco")).setTextChangeEventMode(TextChangeEventMode.LAZY);
										((TextField) fgEnderecoCobranca.getField("endereco")).addBlurListener(new FieldEvents.BlurListener() {
											
											@Override
											public void blur(BlurEvent event) {
												if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
													
													((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
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
												addComponent(fgEnderecoCobranca
														.buildAndBind("Nº",
																"numero"));
												((TextField) fgEnderecoCobranca
														.getField("numero"))
														.setWidth(width_numero);
												((TextField) fgEnderecoCobranca
														.getField("numero"))
														.setNullRepresentation("");
												((TextField) fgEnderecoCobranca
														.getField("numero"))
														.setRequired(true);
												((TextField) fgEnderecoCobranca.getField("numero")).setMaxLength(10);
												
											}
										});
									}
								});

								// Complemento, Bairro,Cidade, UF, Pais

								addComponent(new HorizontalLayout() {
									{
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												addComponent(fgEnderecoCobranca
														.buildAndBind("Bairro",
																"bairro"));
												((TextField) fgEnderecoCobranca
														.getField("bairro"))
														.setNullRepresentation("");
												((TextField) fgEnderecoCobranca
														.getField("bairro"))
														.setStyleName("caption-align");
												((TextField) fgEnderecoCobranca
														.getField("bairro"))
														.setRequired(true);
												((TextField) fgEnderecoCobranca
														.getField("bairro"))
														.setWidth("200px");
												((TextField) fgEnderecoCobranca.getField("bairro")).setImmediate(true);
												((TextField) fgEnderecoCobranca.getField("bairro")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoCobranca.getField("bairro")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
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
												addComponent(fgEnderecoCobranca
														.buildAndBind("Cidade",
																"cidade"));
												((TextField) fgEnderecoCobranca
														.getField("cidade"))
														.setNullRepresentation("");
												((TextField) fgEnderecoCobranca
														.getField("cidade"))
														.setRequired(true);
												((TextField) fgEnderecoCobranca
														.getField("cidade"))
														.setWidth(width_cidade);
												((TextField) fgEnderecoCobranca.getField("cidade")).setImmediate(true);
												((TextField) fgEnderecoCobranca.getField("cidade")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoCobranca.getField("cidade")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
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
												addComponent(fgEnderecoCobranca
														.buildAndBind("UF",
																"uf"));
												((TextField) fgEnderecoCobranca
														.getField("uf"))
														.setWidth("42px");
												((TextField) fgEnderecoCobranca
														.getField("uf"))
														.setNullRepresentation("");
												((TextField) fgEnderecoCobranca
														.getField("uf"))
														.setRequired(true);
												((TextField) fgEnderecoCobranca.getField("uf")).setImmediate(true);
												((TextField) fgEnderecoCobranca.getField("uf")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoCobranca.getField("uf")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
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
												addComponent(fgEnderecoCobranca
														.buildAndBind("Pais",
																"pais"));
												((TextField) fgEnderecoCobranca
														.getField("pais"))
														.setNullRepresentation("");
												((TextField) fgEnderecoCobranca
														.getField("pais"))
														.setWidth(width_pais);
												((TextField) fgEnderecoCobranca
														.getField("pais"))
														.setRequired(true);
												((TextField) fgEnderecoCobranca.getField("pais")).setImmediate(true);
												((TextField) fgEnderecoCobranca.getField("pais")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoCobranca.getField("pais")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
														}	
														}
												});
											}
										});

									}
								});

								addComponent(new HorizontalLayout() {
									{
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												addComponent(fgEnderecoCobranca
														.buildAndBind(
																"Complemento",
																"complemento"));
												((TextField) fgEnderecoCobranca
														.getField("complemento"))
														.setNullRepresentation("");
												((TextField) fgEnderecoCobranca
														.getField("complemento"))
														.setStyleName("caption-align");
												// ((TextField)
												// fgEnderecoCobranca.getField("complemento")).setStyleName("caption-align-complemento");
												((TextField) fgEnderecoCobranca
														.getField("complemento"))
														.setWidth(width_complemento);
												((TextField) fgEnderecoCobranca.getField("complemento")).setImmediate(true);
												((TextField) fgEnderecoCobranca.getField("complemento")).setRequired(true);
												((TextField) fgEnderecoCobranca.getField("complemento")).setDescription("Casa, Apto, Galpão, Anexo, etc");
												((TextField) fgEnderecoCobranca.getField("complemento")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoCobranca.getField("complemento")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
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
												addComponent(fgEnderecoCobranca
														.buildAndBind(
																"Referência",
																"referencia"));
												((TextField) fgEnderecoCobranca
														.getField("referencia"))
														.setNullRepresentation("");
												((TextField) fgEnderecoCobranca
														.getField("referencia"))
														.setWidth(width_referenc);
												// ((TextField)
												// fgEnderecoCobranca.getField("referencia")).setStyleName("caption-align-referencia");
												((TextField) fgEnderecoCobranca.getField("referencia")).setImmediate(true);
												((TextField) fgEnderecoCobranca.getField("referencia")).setTextChangeEventMode(TextChangeEventMode.LAZY);
												((TextField) fgEnderecoCobranca.getField("referencia")).addBlurListener(new FieldEvents.BlurListener() {
													
													@Override
													public void blur(BlurEvent event) {
														if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
															
															((TextField)(event.getComponent())).setValue(event.getComponent().toString().toUpperCase());
														}
													}
												});
											}
										});
									}
								});

							}
						}, "Outros Endereços");

						if(fgEnderecoCobranca.getItemDataSource().getItemProperty("cep").getValue() == null){
							setEnderecoCobranca();
						}
						readOnlyEndCobranca();
						
						
						addTab(new VerticalLayout() {
							{

								addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");
										ComboBox cbVendedor = new ComboBox(
												"Vendedor");
										cbVendedor
												.setStyleName("caption-align-tabOutros");
										cbVendedor.setReadOnly(true);
										cbVendedor.setWidth("200px");
										addComponent(cbVendedor);
									}
								});

								addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");
										ComboBox cbTransportadora = new ComboBox(
												"Transportadora Padrão");
										cbTransportadora
												.setStyleName("caption-align-tabOutros");
										cbTransportadora.setReadOnly(true);
										cbTransportadora.setWidth("400px");
										addComponent(cbTransportadora);
									}
								});

								addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");
										TextField tfTabelaPreco = new TextField(
												"Tabela de Preço Padrão");
										// tfTabelaPreco.setValue(item.getItemProperty("tabelas_preco.nome").getValue().toString());
										tfTabelaPreco
												.setStyleName("caption-align-tabOutros");
										tfTabelaPreco.setReadOnly(true);
										tfTabelaPreco.setWidth("200px");
										addComponent(tfTabelaPreco);
									}
								});

								addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");
										TextField dtPrimeiraCompra = new TextField(
												"Primeira Compra");
										dtPrimeiraCompra
												.setStyleName("caption-align-tabOutros");

										if (ClienteItem.getItemProperty("id")
												.getValue() != null) {

											Integer codCliente = Integer
													.parseInt(ClienteItem
															.getItemProperty(
																	"id")
															.getValue()
															.toString());

											final EcfPreVendaCabecalhoDAO ecfDAO = new EcfPreVendaCabecalhoDAO();
											String primeiraData = ecfDAO
													.getPrimeDate(codCliente);
											dtPrimeiraCompra
													.setValue(primeiraData);
										}
										dtPrimeiraCompra.setReadOnly(true);
										addComponent(dtPrimeiraCompra);
									}
								});

								addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");
										TextField dtUltimaCompra = new TextField(
												"Ultima Compra");
										dtUltimaCompra
												.setStyleName("caption-align-tabOutros");

										if (ClienteItem.getItemProperty("id")
												.getValue() != null) {

											Integer codCliente = Integer
													.parseInt(ClienteItem
															.getItemProperty(
																	"id")
															.getValue()
															.toString());

											final EcfPreVendaCabecalhoDAO ecfDao = new EcfPreVendaCabecalhoDAO();
											String ultimaData = ecfDao
													.getLastDate(codCliente);
											dtUltimaCompra.setValue(ultimaData);

										}
										dtUltimaCompra.setReadOnly(true);
										addComponent(dtUltimaCompra);
									}
								});

								addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");
										TextField tfLimite = new TextField("Limite de Crédito R$");
										tfLimite.setStyleName("textfield-cliente-align");

										if (ClienteItem.getItemProperty("id").getValue() != null) {
											Integer codCliente = Integer.parseInt(ClienteItem.getItemProperty("id").getValue().toString());
											CreditoClienteDAO ccDAO = new CreditoClienteDAO();
											
												try {
													String limite = ccDAO.getLimiteCredito(codCliente);

													Double limiteCliente = Double.parseDouble(limite);												
													Locale.setDefault(new Locale("pt", "BR"));  // mudança global  				  
													DecimalFormat df = new DecimalFormat();  
													df.applyPattern(" #,##0.00");  											
													String valorFormatado = df.format(limiteCliente);
											    
													tfLimite.setValue(valorFormatado);
												} catch (Exception e) {
													tfLimite.setValue("0,00");
												}
												

									}

										tfLimite.setReadOnly(true);
										addComponent(tfLimite);
									}
								});

								addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");
										TextField tfSaldo = new TextField("Saldo R$");
										tfSaldo.setStyleName("textfield-cliente-align");
										// tfSaldo.setStyleName("caption-align-saldo");

										if (ClienteItem.getItemProperty("id").getValue() != null) {
											Integer codCliente = Integer.parseInt(ClienteItem.getItemProperty("id").getValue().toString());
											CreditoClienteDAO ccDAO = new CreditoClienteDAO();
											String saldo = ccDAO.getSaldo(codCliente);
												
											try {
												String limite = ccDAO.getSaldo(codCliente);
											 
												Double saldoCliente = Double.parseDouble(limite);												
												Locale.setDefault(new Locale("pt", "BR"));  // mudança global  				  
												DecimalFormat df = new DecimalFormat();  
												df.applyPattern(" #,##0.00");  											
												String valorFormatado = df.format(saldoCliente);
											    
												tfSaldo.setValue(valorFormatado);
											} catch (Exception e) {
												tfSaldo.setValue("0,00");												
											}
												
					
										}

										tfSaldo.setReadOnly(true);
										addComponent(tfSaldo);
									}
								});

							}
						}, "Outras Informações");

					}
				});
			}
		});

	}

	public void updateNumeroEndEntregaCob() {
		String numero = ((TextField) fgEndereco.getField("numero")).getValue();

		((TextField) fgEnderecoEntrega.getField("numero")).setValue(numero);
		((TextField) fgEnderecoCobranca.getField("numero")).setValue(numero);
	}
	
	public void updateEndEntrega() {
		String cep = ((TextField) fgEndereco.getField("cep")).getValue();
		String endereco = ((TextField) fgEndereco.getField("endereco")).getValue();
		String numero = ((TextField) fgEndereco.getField("numero")).getValue();
		String bairro = ((TextField) fgEndereco.getField("bairro")).getValue();
		String cidade = ((TextField) fgEndereco.getField("cidade")).getValue();
		String uf = ((TextField) fgEndereco.getField("uf")).getValue();
		String pais = ((TextField) fgEndereco.getField("pais")).getValue();
		String complemento = ((TextField) fgEndereco.getField("complemento")).getValue();
		String referencia = ((TextField) fgEndereco.getField("referencia")).getValue();
		
		
	}
	

	public void clearEnderecoEntrega() {
		((TextField) fgEnderecoEntrega.getField("cep")).setValue("");
		((TextField) fgEnderecoEntrega.getField("endereco")).setValue("");
		((TextField) fgEnderecoEntrega.getField("numero")).setValue("");
		((TextField) fgEnderecoEntrega.getField("bairro")).setValue("");
		((TextField) fgEnderecoEntrega.getField("cidade")).setValue("");
		((TextField) fgEnderecoEntrega.getField("uf")).setValue("");
		((TextField) fgEnderecoEntrega.getField("pais")).setValue("");
		((TextField) fgEnderecoEntrega.getField("complemento")).setValue("");
		((TextField) fgEnderecoEntrega.getField("referencia")).setValue("");
		((TextField) fgEnderecoEntrega.getField("cep")).focus();
	}

	public void clearEnderecoCobranca() {
		((TextField) fgEnderecoCobranca.getField("cep")).setValue("");
		((TextField) fgEnderecoCobranca.getField("endereco")).setValue("");
		((TextField) fgEnderecoCobranca.getField("numero")).setValue("");
		((TextField) fgEnderecoCobranca.getField("bairro")).setValue("");
		((TextField) fgEnderecoCobranca.getField("cidade")).setValue("");
		((TextField) fgEnderecoCobranca.getField("uf")).setValue("");
		((TextField) fgEnderecoCobranca.getField("pais")).setValue("");
		((TextField) fgEnderecoCobranca.getField("complemento")).setValue("");
		((TextField) fgEnderecoCobranca.getField("referencia")).setValue("");
		((TextField) fgEnderecoCobranca.getField("cep")).focus();
	}

	public void setEnderecoEntrega() {
		String cep = ((TextField) fgEndereco.getField("cep")).getValue();
		String endereco = ((TextField) fgEndereco.getField("endereco")).getValue();
		String numero = ((TextField) fgEndereco.getField("numero")).getValue();
		String bairro = ((TextField) fgEndereco.getField("bairro")).getValue();
		String cidade = ((TextField) fgEndereco.getField("cidade")).getValue();
		String uf = ((TextField) fgEndereco.getField("uf")).getValue();
		String pais = ((TextField) fgEndereco.getField("pais")).getValue();
		String complemento = ((TextField) fgEndereco.getField("complemento")).getValue();
		String referencia = ((TextField) fgEndereco.getField("referencia")).getValue();

		((TextField) fgEnderecoEntrega.getField("cep")).setValue(cep);
		((TextField) fgEnderecoEntrega.getField("endereco")).setValue(endereco);
		((TextField) fgEnderecoEntrega.getField("numero")).setValue(numero);
		((TextField) fgEnderecoEntrega.getField("bairro")).setValue(bairro);
		((TextField) fgEnderecoEntrega.getField("cidade")).setValue(cidade);
		((TextField) fgEnderecoEntrega.getField("uf")).setValue(uf);
		((TextField) fgEnderecoEntrega.getField("pais")).setValue(pais);
		((TextField) fgEnderecoEntrega.getField("complemento")).setValue(complemento);
		((TextField) fgEnderecoEntrega.getField("referencia")).setValue(referencia);
	}

	public void setEnderecoCobranca() {
		String cep = ((TextField) fgEndereco.getField("cep")).getValue();
		String endereco = ((TextField) fgEndereco.getField("endereco")).getValue();
		String numero = ((TextField) fgEndereco.getField("numero")).getValue();
		String bairro = ((TextField) fgEndereco.getField("bairro")).getValue();
		String cidade = ((TextField) fgEndereco.getField("cidade")).getValue();
		String uf = ((TextField) fgEndereco.getField("uf")).getValue();
		String pais = ((TextField) fgEndereco.getField("pais")).getValue();
		String complemento = ((TextField) fgEndereco.getField("complemento")).getValue();
		String referencia = ((TextField) fgEndereco.getField("referencia")).getValue();

		((TextField) fgEnderecoCobranca.getField("cep")).setValue(cep);
		((TextField) fgEnderecoCobranca.getField("endereco")).setValue(endereco);
		((TextField) fgEnderecoCobranca.getField("numero")).setValue(numero);
		((TextField) fgEnderecoCobranca.getField("bairro")).setValue(bairro);
		((TextField) fgEnderecoCobranca.getField("cidade")).setValue(cidade);
		((TextField) fgEnderecoCobranca.getField("uf")).setValue(uf);
		((TextField) fgEnderecoCobranca.getField("pais")).setValue(pais);
		((TextField) fgEnderecoCobranca.getField("complemento")).setValue(complemento);
		((TextField) fgEnderecoCobranca.getField("referencia")).setValue(referencia);
	}

	public void updateCepCobranca(Ceps cep) {
		((TextField) fgEnderecoCobranca.getField("cep")).setValue(cep.getCep());
		((TextField) fgEnderecoCobranca.getField("endereco")).setValue(cep.getEndereco().toUpperCase());
		((TextField) fgEnderecoCobranca.getField("numero")).setValue("");
		((TextField) fgEnderecoCobranca.getField("bairro")).setValue(cep.getBairro().toUpperCase());
		((TextField) fgEnderecoCobranca.getField("cidade")).setValue(cep.getCidade().toUpperCase());
		((TextField) fgEnderecoCobranca.getField("uf")).setValue(cep.getUf().toUpperCase());
		((TextField) fgEnderecoCobranca.getField("pais")).setValue(cep.getPais().toUpperCase());
		((TextField) fgEnderecoCobranca.getField("numero")).focus();
	}

	public void updateCepEntrega(Ceps cep) {
		((TextField) fgEnderecoEntrega.getField("cep")).setValue(cep.getCep());
		((TextField) fgEnderecoEntrega.getField("endereco")).setValue(cep.getEndereco().toUpperCase());
		((TextField) fgEnderecoEntrega.getField("numero")).setValue("");
		((TextField) fgEnderecoEntrega.getField("bairro")).setValue(cep.getBairro().toUpperCase());
		((TextField) fgEnderecoEntrega.getField("cidade")).setValue(cep.getCidade().toUpperCase());
		((TextField) fgEnderecoEntrega.getField("uf")).setValue(cep.getUf().toUpperCase());
		((TextField) fgEnderecoEntrega.getField("pais")).setValue(cep.getPais().toUpperCase());
		((TextField) fgEnderecoEntrega.getField("numero")).focus();
	}

	public void updateCep(Ceps cep) {
		
		if(cep != null && 
		   cep.getCep() != null && 
		   cep.getEndereco() != null && 
		   cep.getBairro() != null && 
		   cep.getCidade() != null && 
		   cep.getPais() != null && 
		   cep.getUf() != null){
		
			((TextField) fgEndereco.getField("cep")).setValue(cep.getCep());
			((TextField) fgEndereco.getField("endereco")).setValue(cep.getEndereco().toUpperCase());
			((TextField) fgEndereco.getField("numero")).setValue("");
			((TextField) fgEndereco.getField("bairro")).setValue(cep.getBairro().toUpperCase());
			((TextField) fgEndereco.getField("cidade")).setValue(cep.getCidade().toUpperCase());
			((TextField) fgEndereco.getField("uf")).setValue(cep.getUf().toUpperCase());
			((TextField) fgEndereco.getField("pais")).setValue(cep.getPais().toUpperCase());
			((TextField) fgEndereco.getField("numero")).focus();
		}
		
	}

	public void readOnlyEndEntrega(){
		
		try {
			if(cbEnderecoEntrega.getValue().equals(false)){
				
				((TextField) fgEnderecoEntrega.getField("cep")).setReadOnly(true);
				((TextField) fgEnderecoEntrega.getField("endereco")).setReadOnly(true);
				((TextField) fgEnderecoEntrega.getField("numero")).setReadOnly(true);
				((TextField) fgEnderecoEntrega.getField("cidade")).setReadOnly(true);
				((TextField) fgEnderecoEntrega.getField("bairro")).setReadOnly(true);
				((TextField) fgEnderecoEntrega.getField("uf")).setReadOnly(true);
				((TextField) fgEnderecoEntrega.getField("pais")).setReadOnly(true);
				((TextField) fgEnderecoEntrega.getField("complemento")).setReadOnly(true);
				((TextField) fgEnderecoEntrega.getField("referencia")).setReadOnly(true);
				
			}		
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro: "+e.getMessage()+".\n Causado por: "+e.getCause());
		}
	}
	
	public void readOnlyEndCobranca(){

		try {
			if(cbEnderecoCobranca.getValue().equals(false)){
				
				((TextField) fgEnderecoCobranca.getField("cep")).setReadOnly(true);
				((TextField) fgEnderecoCobranca.getField("endereco")).setReadOnly(true);
				((TextField) fgEnderecoCobranca.getField("numero")).setReadOnly(true);
				((TextField) fgEnderecoCobranca.getField("cidade")).setReadOnly(true);
				((TextField) fgEnderecoCobranca.getField("bairro")).setReadOnly(true);
				((TextField) fgEnderecoCobranca.getField("uf")).setReadOnly(true);
				((TextField) fgEnderecoCobranca.getField("pais")).setReadOnly(true);
				((TextField) fgEnderecoCobranca.getField("complemento")).setReadOnly(true);
				((TextField) fgEnderecoCobranca.getField("referencia")).setReadOnly(true);				
			}			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro : "+e.getMessage()+".\n Causado por: "+e.getCause());
		}
	}
	
	public void removeReadOnlyEndEntrega(){
				
				((TextField) fgEnderecoEntrega.getField("cep")).setReadOnly(false);
				((TextField) fgEnderecoEntrega.getField("endereco")).setReadOnly(false);
				((TextField) fgEnderecoEntrega.getField("numero")).setReadOnly(false);
				((TextField) fgEnderecoEntrega.getField("cidade")).setReadOnly(false);
				((TextField) fgEnderecoEntrega.getField("bairro")).setReadOnly(false);
				((TextField) fgEnderecoEntrega.getField("uf")).setReadOnly(false);
				((TextField) fgEnderecoEntrega.getField("pais")).setReadOnly(false);
				((TextField) fgEnderecoEntrega.getField("complemento")).setReadOnly(false);
				((TextField) fgEnderecoEntrega.getField("referencia")).setReadOnly(false);
	
	}
	
	public void removeReadOnlyEndCobranca(){
											
				((TextField) fgEnderecoCobranca.getField("cep")).setReadOnly(false);
				((TextField) fgEnderecoCobranca.getField("endereco")).setReadOnly(false);
				((TextField) fgEnderecoCobranca.getField("numero")).setReadOnly(false);
				((TextField) fgEnderecoCobranca.getField("cidade")).setReadOnly(false);
				((TextField) fgEnderecoCobranca.getField("bairro")).setReadOnly(false);
				((TextField) fgEnderecoCobranca.getField("uf")).setReadOnly(false);
				((TextField) fgEnderecoCobranca.getField("pais")).setReadOnly(false);
				((TextField) fgEnderecoCobranca.getField("complemento")).setReadOnly(false);
				((TextField) fgEnderecoCobranca.getField("referencia")).setReadOnly(false);		
			
	}
	

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				if (fieldGroup.isValid() && fgEndereco.isValid() && fgEnderecoEntrega.isValid() && fgEnderecoCobranca.isValid() && cnpjCadast == true && cnpjValid == true) {
					try {		
						if (ClienteItem.getItemProperty("id").getValue() == null) {								
							// CLIENTES
							fieldGroup.commit();
							fireEvent(new ClienteEvent(getUI(), ClienteItem, true));
							
							// ENDEREÇO PRINCIPAL
							fgEndereco.commit();
							fireEvent(new EnderecoEvent(getUI(), EndItem, true));
							
							// ENDEREÇOS ENTREGA
							fgEnderecoEntrega.commit();
							fireEvent(new EnderecoEntregaEvent(getUI(),EndEntreItem, true));
							
							// ENDEREÇO COBRANÇA
							fgEnderecoCobranca.commit();
							fireEvent(new EnderecoCobEvent(getUI(),EndCobrancaItem, true));
						
							Notification.show("Cliente Cadastrado com Sucesso!");
						} else {
							if(fieldGroup.isModified()){
								// CLIENTES
								fieldGroup.commit();
								fireEvent(new ClienteEvent(getUI(), ClienteItem, true));
							}			
							if(fgEndereco.isModified()){
								// ENDEREÇO PRINCIPAL
								fgEndereco.commit();
								fireEvent(new EnderecoEvent(getUI(), EndItem, true));
							}
								// ENDEREÇOS ENTREGA
							if (fgEnderecoEntrega.isModified()) {
								fgEnderecoEntrega.commit();
								fireEvent(new EnderecoEntregaEvent(getUI(), EndEntreItem, true));
							}

							// ENDEREÇO COBRANÇA
							if (fgEnderecoCobranca.isModified()) {
								fgEnderecoCobranca.commit();
								fireEvent(new EnderecoCobEvent(getUI(), EndCobrancaItem, true));
							}
							
							Notification.show("Cliente Alerado com Sucesso!");															
						}

						close();
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
						Notification.show("Não foi Possivel Salvar as Alterações!");

					}
			
				} else {
					
					for (Field<?> field: fieldGroup.getFields()) {
					
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
					}
					
					for (Field<?> field: fgEndereco.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
					}
					
					for (Field<?> field: fgEnderecoCobranca.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
					}
					
					for (Field<?> field: fgEnderecoEntrega.getFields()) {
						
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

		ShortcutListener slbtOK = new ShortcutListener("Ok",
				ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};

		btSalvar.addShortcutListener(slbtOK);

		btSalvar.addStyleName("default");
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				if (!fieldGroup.isModified() && !fgEndereco.isModified() && !fgEnderecoEntrega.isModified()	&& !fgEnderecoCobranca.isModified()) {
					close();
				} else {
					final GenericDialog gDialog = new GenericDialog("Confirme para Continuar!","Deseja Salvar as Informações Alteradas?", true,true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if (event.isConfirm()) {
								
								if (fieldGroup.isValid() && fgEndereco.isValid() && fgEnderecoEntrega.isValid() && fgEnderecoCobranca.isValid() && cnpjCadast == true && cnpjValid == true) {
									try {
										// CLIENTES
										fieldGroup.commit();
										fireEvent(new ClienteEvent(getUI(),ClienteItem, true));
										
										// ENDEREÇO PRINCIPAL
										fgEndereco.commit();
										fireEvent(new EnderecoEvent(getUI(),EndItem, true));
										
										// ENDEREÇO ENTREGA
										fgEnderecoEntrega.commit();
										fireEvent(new EnderecoEntregaEvent(getUI(), EndEntreItem, true));
										
										// ENDEREÇO COBRANÇA
										fgEnderecoCobranca.commit();
										fireEvent(new EnderecoCobEvent(getUI(),EndCobrancaItem, true));
										
										if (ClienteItem.getItemProperty("id").getValue() == null) {
											Notification.show("Cliente Cadastrado com Sucesso!");
										} else {											
											Notification.show("Cliente Alterado com Sucesso!");								
										}
										
										close();
									} catch (Exception e) {
										e.printStackTrace();
										System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
										Notification.show("Não foi Possivel Salvar as Alterações!");
									}
									gDialog.close();
									close();
									
								} else {
									Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
								}
							} else {
								fieldGroup.discard();
								fgEndereco.discard();
								fgEnderecoCobranca.discard();
								fgEnderecoEntrega.discard();
								gDialog.close();
								close();
								//	Notification.show("As alteraçẽos foram descartadas!");
							}
							
						}
					});
					getUI().addWindow(gDialog);
				
				}
			}
		});

		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar",
				ShortcutAction.KeyCode.ESCAPE, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};

		btCancelar.addShortcutListener(slbtCancelar);

		return btCancelar;
	}

	public void addListerner(ClientesListerner target) {
		try {
			Method method = ClientesListerner.class.getDeclaredMethod(
					"onClose", ClienteEvent.class);
			addListener(ClienteEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado: " + "AQUI 1"
					+ e.getMessage());
		}
	}

	public void removeListerner(ClientesListerner target) {
		removeListener(ClienteEvent.class, target);
	}

	public static class ClienteEvent extends Event {

		private Item item;
		private boolean confirm;

		public ClienteEvent(Component source, Item itemCliente, boolean confirm) {
			super(source);
			this.item = itemCliente;
			this.confirm = confirm;
		}

		public Item getItem() {
			return item;
		}

		public boolean isConfirm() {
			return confirm;
		}
	}

	public interface ClientesListerner extends Serializable {
		public void onClose(ClienteEvent event);
	}

	// ------------------------------------------------------------ENDEREÇOS
	// PRINCIPAIS--------//
	public void addListerner(EnderecoListerner target) {
		try {
			Method method = EnderecoListerner.class.getDeclaredMethod(
					"onCloseEndereco", EnderecoEvent.class);
			addListener(EnderecoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado: " + "AQUI 2"
					+ e.getMessage());
		}
	}

	public void removeListerner(EnderecoListerner target) {
		removeListener(EnderecoEvent.class, target);
	}

	public static class EnderecoEvent extends Event {

		private Item endPrinItem;
		private boolean confirm;

		public EnderecoEvent(Component source, Item endPrinItem, boolean confirm) {
			super(source);
			this.endPrinItem = endPrinItem;
			this.confirm = confirm;
		}

		public boolean getConfirm() {
			return confirm;
		}

		public Item getPrinItem() {
			return endPrinItem;
		}
	}

	public interface EnderecoListerner extends Serializable {
		public void onCloseEndereco(EnderecoEvent event);
	}

	// ------------------------------------------------------------ENDEREÇOS
	// ENTREGA--------------------//

	public void addListerner(EnderecoEntregaListerner target) {
		try {
			Method method = EnderecoEntregaListerner.class.getDeclaredMethod(
					"onCloseEndEntrega", EnderecoEntregaEvent.class);
			addListener(EnderecoEntregaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado: " + "AQUI 3"
					+ e.getMessage());
		}
	}

	public void removeListerner(EnderecoEntregaListerner target) {
		removeListener(EnderecoEntregaEvent.class, target);
	}

	public static class EnderecoEntregaEvent extends Event {

		private Item endEntItem;
		private boolean confirm;

		public EnderecoEntregaEvent(Component source, Item endEntItem,
				boolean confirm) {
			super(source);
			this.endEntItem = endEntItem;
			this.confirm = confirm;
		}

		public boolean getConfirm() {
			return confirm;
		}

		public Item getEndEnt() {
			return endEntItem;
		}
	}

	public interface EnderecoEntregaListerner extends Serializable {
		public void onCloseEndEntrega(EnderecoEntregaEvent event);
	}

	// -----------------------------------------------------------ENDEREÇO DE
	// COBRANÇA--------------------------------------//

	public void addListerner(EnderecoCobListerner target) {
		try {
			Method method = EnderecoCobListerner.class.getDeclaredMethod(
					"onCloseEndCob", EnderecoCobEvent.class);
			addListener(EnderecoCobEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado: " + "AQUI 4 "
					+ e.getMessage());
		}
	}

	public void removeListerner(EnderecoCobListerner target) {
		removeListener(EnderecoCobEvent.class, target);
	}

	public static class EnderecoCobEvent extends Event {

		private Item item;
		private boolean confirm;

		public EnderecoCobEvent(Component source, Item itemEndCob,
				boolean confirm) {
			super(source);
			this.item = itemEndCob;
			this.confirm = confirm;
		}

		public Item getItem() {
			return item;
		}

		public boolean isConfirm() {
			return confirm;
		}
	}

	public interface EnderecoCobListerner extends Serializable {
		public void onCloseEndCob(EnderecoCobEvent event);
	}

}
