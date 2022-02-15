package com.digital.opuserp.view.modulos.cadastro.clientes;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.CreditoClienteDAO;
import com.digital.opuserp.dao.EcfPreVendaCabecalhoDAO;
import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogErrorDAO;
import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ComoNosConheceu;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.CnpjUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.EmailUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.ValidarCPF;
import com.digital.opuserp.view.modulos.cadastro.clientes.EnderecoEditor.CepEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
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
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class ClienteEditor extends Window implements GenericEditor {

	Item ClienteItem;
	Object itemId;
	
	Button btSalvar;
	Button btCancelar;
	Button btAdicionar;

	VerticalLayout vlRoot;

	TabSheet tbDados;
	FormLayout flPrincipal;
	FormLayout flOutrosEnd;
	FormLayout flFiador;
	FormLayout flOutrasInfo;

	FieldGroup fieldGroup;

	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	
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
	
	private JPAContainer<Endereco> containerEnderecos;
	List<Endereco> enderecosItem;
		
	VerticalLayout vlEndereco;
	

	Button btEditar;
	Button btInativar;
	Button btPrincipal;
	
	Integer codClienteImportacao;
	
	String cepSelecionado= null;
	
	boolean emailValid = false;
	String emailInicial;
	
	public ClienteEditor(Item ClienteItem, String title, boolean modal, Integer codClienteImportacao, Integer codSubModulo) {
		
		gmDAO = new GerenciarModuloDAO();
		this.codSubModulo = codSubModulo;
		
		Styles styles = Page.getCurrent().getStyles();					        
        styles.add(".dashboard input.v-textfield-readonly { background-color: #FFF; }");
		
		configLayout();
		setStyleName("cliente_editor");
		this.codClienteImportacao = codClienteImportacao;

		this.ClienteItem = ClienteItem;
	
		if(this.ClienteItem.getItemProperty("email").getValue() != null){
			emailInicial = this.ClienteItem.getItemProperty("email").getValue().toString();
		}
			
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
								
				tfCod.setWidth("60px");
				tfCod.setReadOnly(true);
				tfCod.setStyleName("caption-align");
				tfCod.setNullRepresentation("");
				//tfCod.setConverter(new com.vaadin.data.util.converter.);
				
				fieldGroup.bind(tfCod, "id");
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

				TextField txtdataCadastro = new TextField("Cadastro");
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");				
				if(fieldGroup.getItemDataSource().getItemProperty("data_cadastro").getValue() != null){
					txtdataCadastro.setValue(sdf.format(fieldGroup.getItemDataSource().getItemProperty("data_cadastro").getValue()).toString());
				}else{
					txtdataCadastro.setValue(sdf.format(new Date()));
				}
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

				TextField txtAlteracao = new TextField ("Alteração");
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");			
				
				if(fieldGroup.getItemDataSource().getItemProperty("data_cadastro").getValue() != null){
					txtAlteracao.setValue(sdf.format(fieldGroup.getItemDataSource().getItemProperty("data_alteracao").getValue()).toString());
				}else{
					txtAlteracao.setValue(sdf.format(new Date()));
				}
				
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
				
				if(ClienteItem.getItemProperty("id").getValue() != null && ClienteItem.getItemProperty("status").getValue() != "WORKING"){
					((TextField) fieldGroup.getField("doc_cpf_cnpj")).setReadOnly(true);
				}
				
				if(((TextField) fieldGroup.getField("doc_cpf_cnpj")) != null && ((TextField) fieldGroup.getField("doc_cpf_cnpj")).getValue() != null){
					if (((TextField) fieldGroup.getField("doc_cpf_cnpj")).getValue().length() == 14) {
						if (!CnpjUtil.validar(((TextField) fieldGroup.getField("doc_cpf_cnpj")).getValue())) {
							((TextField) fieldGroup.getField("doc_cpf_cnpj")).setReadOnly(false);
							
							cnpjValid = false;
							((TextField) fieldGroup.getField("doc_cpf_cnpj")).removeStyleName("valid-cpf");
							((TextField) fieldGroup.getField("doc_cpf_cnpj")).addStyleName("invalid-cpf");
						}
					} else if (((TextField) fieldGroup.getField("doc_cpf_cnpj")).getValue().length() == 11) {
																
						if (!ValidarCPF.cpfValido(((TextField) fieldGroup.getField("doc_cpf_cnpj")).getValue())) {
							((TextField) fieldGroup.getField("doc_cpf_cnpj")).setReadOnly(false);
							
							cnpjValid = false;
							((TextField) fieldGroup.getField("doc_cpf_cnpj")).removeStyleName("valid-cpf");
							((TextField) fieldGroup.getField("doc_cpf_cnpj")).addStyleName("invalid-cpf");
						}
					}else{
						((TextField) fieldGroup.getField("doc_cpf_cnpj")).setReadOnly(false);
						
						cnpjValid = false;
						((TextField) fieldGroup.getField("doc_cpf_cnpj")).removeStyleName("valid-cpf");
						((TextField) fieldGroup.getField("doc_cpf_cnpj")).addStyleName("invalid-cpf");
					}
				}
				
				
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setRequired(true);
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setNullRepresentation("");
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setStyleName("caption-align");
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setWidth("153px");
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setMaxLength(14);
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).focus();
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).setTextChangeEventMode(TextChangeEventMode.LAZY);
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						if (!event.getText().isEmpty() && !event.getText().toString().equals(cnpjAtual)) {

							// Validar CNPJ
							if (event.getText().length() == 14) {
								if (CnpjUtil.validar(event.getText())) {
									cnpjValid = true;
									event.getComponent().removeStyleName("invalid-cpf");
									event.getComponent().addStyleName("valid-cpf");
								} else {
									cnpjValid = false;
									event.getComponent().removeStyleName("valid-cpf");
									event.getComponent().addStyleName("invalid-cpf");
								}
							} else if (event.getText().length() == 11) {
																		
								if (ValidarCPF.cpfValido(event.getText())) {
									cnpjValid = true;
									event.getComponent().removeStyleName("invalid-cpf");
									event.getComponent().addStyleName("valid-cpf");
								} else {
									cnpjValid = false;
									event.getComponent().removeStyleName("valid-cpf");
									event.getComponent().addStyleName("invalid-cpf");
								}
							} else {
								cnpjValid = false;
								event.getComponent().removeStyleName("valid-cpf");
								event.getComponent().addStyleName("invalid-cpf");
							}

							if (cnpjValid) {
								// Verificar Existencia
								ClienteDAO cDAO = new ClienteDAO();									
								Cliente c = cDAO.cpfCnpjCadastrado(OpusERP4UI.getEmpresa(),event.getText());

								if (c != null && !c.getId().equals(ClienteItem.getItemProperty("id").getValue())) {
									cnpjCadast = false;
									event.getComponent().removeStyleName("valid-cpf");
									event.getComponent().addStyleName("invalid-cpf");
								} else {
									cnpjCadast = true;
									event.getComponent().removeStyleName("invalid-cpf");
									event.getComponent().addStyleName("valid-cpf");										
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
																	
								if (((TextField)(event.getComponent())).getValue() != null && 
										!((TextField)(event.getComponent())).getValue().equals("") && !((TextField)(event.getComponent())).getValue().isEmpty()){
									// Validar CNPJ
										if (((TextField)(event.getComponent())).getValue().length() == 14) {
											if (CnpjUtil.validar(((TextField)(event.getComponent())).getValue())) {
												cnpjValid = true;
												((TextField)(event.getComponent())).removeStyleName("invalid-cpf");
												((TextField)(event.getComponent())).addStyleName("valid-cpf");
											} else {
												cnpjValid = false;												
												((TextField)(event.getComponent())).removeStyleName("valid-cpf");
												((TextField)(event.getComponent())).addStyleName("invalid-cpf");
											}
										} else if (((TextField)(event.getComponent())).getValue().length() == 11) {
																					
											if (ValidarCPF.cpfValido(((TextField)(event.getComponent())).getValue())) {
												cnpjValid = true;
												((TextField)(event.getComponent())).removeStyleName("invalid-cpf");
												((TextField)(event.getComponent())).addStyleName("valid-cpf");
											} else {
												cnpjValid = false;												
												((TextField)(event.getComponent())).removeStyleName("valid-cpf");
												((TextField)(event.getComponent())).addStyleName("invalid-cpf");
											}
										} else {
											cnpjValid = false;										
											((TextField)(event.getComponent())).removeStyleName("valid-cpf");
											((TextField)(event.getComponent())).addStyleName("invalid-cpf");
										}
										
										if (cnpjValid) {
											// Verificar Existencia
											ClienteDAO cDAO = new ClienteDAO();									
											Cliente c = cDAO.cpfCnpjCadastrado(OpusERP4UI.getEmpresa(),((TextField)(event.getComponent())).getValue());

											if (c != null && !c.getId().equals(ClienteItem.getItemProperty("id").getValue())) {
												cnpjCadast = false;
												event.getComponent().removeStyleName("valid-cpf");
												event.getComponent().addStyleName("invalid-cpf");
											} else {
												cnpjCadast = true;
												event.getComponent().removeStyleName("invalid-cpf");
												event.getComponent().addStyleName("valid-cpf");										
											}
										}		
								}
										
									
									if (cnpjValid == false && ((TextField)(event.getComponent())).getValue() != null && 
											!((TextField)(event.getComponent())).getValue().equals("") && !((TextField)(event.getComponent())).getValue().isEmpty()) {
										Notify.Show("CPF/CNPJ Inválido!", Notify.TYPE_ERROR);	
										
									}else if(cnpjCadast == false && ((TextField)(event.getComponent())).getValue() != null && 
											!((TextField)(event.getComponent())).getValue().equals("") && !((TextField)(event.getComponent())).getValue().isEmpty()){										
										Notify.Show("CPF/CNPJ Já Cadastrado!", Notify.TYPE_WARNING);
									}
							}
						});

				
				((TextField) fieldGroup.getField("doc_cpf_cnpj")).addBlurListener(new FieldEvents.BlurListener() {
					
					@Override
					public void blur(BlurEvent event) {
						//((TextField) fieldGroup.getField("doc_rg_insc_estadual")).focus();
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
		hl2.addComponent(new HorizontalLayout(){
			{
				FormLayout fmr1 =  new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");
						
						addComponent(fieldGroup.buildAndBind("Insc. Estadual/RG","doc_rg_insc_estadual"));				
						((TextField) fieldGroup.getField("doc_rg_insc_estadual")).setNullRepresentation("");
						((TextField) fieldGroup.getField("doc_rg_insc_estadual")).setWidth("177px");
						((TextField) fieldGroup.getField("doc_rg_insc_estadual")).setMaxLength(50);
						
						((TextField) fieldGroup.getField("doc_rg_insc_estadual")).setId("txtRg");
						
						JavaScript.getCurrent().execute("$('#txtRg').mask('00000000000000')");
						
						((TextField) fieldGroup.getField("doc_rg_insc_estadual")).addListener(new FieldEvents.BlurListener() {
							
							@Override
							public void blur(BlurEvent event) {
								if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){									
									((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
								}
								
							}
						});			
					}
				};
				
				addComponent(fmr1); 
				
				
				final Button btConsultaInscricao = new Button();	
				btConsultaInscricao.setStyleName(BaseTheme.BUTTON_LINK);
				btConsultaInscricao.setIcon(new ThemeResource("icons/browser-16.png"));
				btConsultaInscricao.setDescription("Sefaz PE");
				
				BrowserWindowOpener openCpf = new BrowserWindowOpener("http://www.sefaz.pe.gov.br/sintegra/");
				openCpf.setFeatures("height=600,width=800");
				openCpf.extend(btConsultaInscricao);
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");	
						addComponent(btConsultaInscricao);
					}
				});
				
			}
		});
		hl2.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom-new");
				
				addComponent(fieldGroup.buildAndBind("Orgão Emissor","orgao_emissor_rg"));				
				((TextField) fieldGroup.getField("orgao_emissor_rg")).setNullRepresentation("");
				((TextField) fieldGroup.getField("orgao_emissor_rg")).setWidth("76px");
				((TextField) fieldGroup.getField("orgao_emissor_rg")).setMaxLength(50);
				
				
				((TextField) fieldGroup.getField("orgao_emissor_rg")).addListener(new FieldEvents.BlurListener() {
					
					@Override
					public void blur(BlurEvent event) {
						if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
							((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
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
							
							((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
						}
	
					}
				});
				
				
				tfNomeRazao.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						if(event.getText() != null && !event.getText().equals("") && !event.getText().isEmpty()){
							
							((TextField)fieldGroup.getField("como_quer_ser_chamado")).setValue(event.getText().split(" ")[0].toUpperCase());	
							((TextField)fieldGroup.getField("contato")).setValue(event.getText().split(" ")[0].toUpperCase());
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
				setStyleName("form-cutom_hide_require");
				ComboBox cbTratamento = new ComboBox();
				cbTratamento.setWidth(width_tratamento);
				cbTratamento.setNullSelectionAllowed(false);
				cbTratamento.addItem("Sr(a)");
				cbTratamento.addItem("Dr(a)");
				cbTratamento.setRequired(true);
				addComponent(cbTratamento);
				fieldGroup.bind(cbTratamento, "tratamento");
				
				
				if(ClienteItem.getItemProperty("tratamento").getValue() == null){
					cbTratamento.select("Sr(a)");
				}
			}
		});

		hl3.addComponent(new FormLayout() {
			{
				
				setStyleName("form-cutom");

				TextField tfQserChamado = (TextField) fieldGroup.buildAndBind("", "como_quer_ser_chamado");
				addComponent(tfQserChamado);
				setStyleName("form-cutom_hide_require");
				tfQserChamado.setWidth("205px");
				tfQserChamado.setMaxLength(100);
				tfQserChamado.setDescription("Como quer ser chamado");			
				tfQserChamado.setNullRepresentation("");
				tfQserChamado.setImmediate(true);
				tfQserChamado.setTextChangeEventMode(TextChangeEventMode.LAZY);
				tfQserChamado.setRequired(true);
				tfQserChamado.addBlurListener(new FieldEvents.BlurListener() {
							
					@Override
					public void blur(BlurEvent event) {		
							if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
							
							((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
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
							
							((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
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
							
							((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
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
	TabSheet tab;
	private void buildTabSheet() {

		vlRoot.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");
				setStyleName("horizontal-tabsheet");
				
				tab = new TabSheet() {
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
												dfDataNascimento.setRequired(true); 
												
												addComponent(dfDataNascimento);
												fieldGroup.bind(dfDataNascimento,"data_nascimento");

											}
										});
										
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");
												ComboBox cbProfissao = new ComboBox("Profissão");
												fieldGroup.bind(cbProfissao, "profissao");
												cbProfissao.setWidth("180px");
												cbProfissao.setNullSelectionAllowed(false);
												cbProfissao.setStyleName("caption-align");
												cbProfissao.addItem("AGRICULTOR");
												cbProfissao.addItem("ADVOGADO");
												cbProfissao.addItem("APOSENTADO");
												cbProfissao.addItem("AUTONOMO");
												cbProfissao.addItem("AUX. ADMINISTRATIVO");
												cbProfissao.addItem("AUX. DE PRODUÇÃO");
												cbProfissao.addItem("ASSISTENTE SOCIAL");
												cbProfissao.addItem("BOMBEIRO");
												cbProfissao.addItem("BANCARIO");
												cbProfissao.addItem("COSTUREIRA");
												cbProfissao.addItem("DOMESTICA");
												cbProfissao.addItem("DESIGNER");
												cbProfissao.addItem("ELETRICISTA");
												cbProfissao.addItem("EMPILHADOR");
												cbProfissao.addItem("EMPRESARIO");
												cbProfissao.addItem("ENGENHEIRO");
												cbProfissao.addItem("ESTUDANTE");
												cbProfissao.addItem("MECANICO");
												cbProfissao.addItem("METALURGICO");
												cbProfissao.addItem("MEDICO");
												cbProfissao.addItem("MILITAR");
												cbProfissao.addItem("MOTORISTA");
												cbProfissao.addItem("MONITORA");
												cbProfissao.addItem("NAO INFORMADO");
												cbProfissao.addItem("PADRE");
												cbProfissao.addItem("PASTOR");
												cbProfissao.addItem("PADEIRO");
												cbProfissao.addItem("PEDREIRO");
												cbProfissao.addItem("POLICIAL");
												cbProfissao.addItem("PROFESSOR");
												cbProfissao.addItem("PSICOLOGO");
												cbProfissao.addItem("SERVIDOR PUBLICO");
												cbProfissao.addItem("TAXISTA");
												cbProfissao.addItem("TECNICO EM INFORMATICA");
												cbProfissao.addItem("TECNICO EM ENFERMAGEM");
												cbProfissao.addItem("VETERINARIO");
												cbProfissao.addItem("VENDEDOR");
												cbProfissao.addItem("VIGILANTE");
												addComponent(cbProfissao);
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
								
								//Whatsapp
								addComponent(new FormLayout() {
									{
											setMargin(true);
											setSpacing(true);
											setStyleName("form-cutom");
											addComponent(fieldGroup.buildAndBind("Instagram","whatsapp"));
											((TextField) fieldGroup.getField("whatsapp")).setNullRepresentation("");
											((TextField) fieldGroup.getField("whatsapp")).setStyleName("caption-align");
											((TextField) fieldGroup.getField("whatsapp")).addStyleName("lowercase");
											((TextField) fieldGroup.getField("whatsapp")).setWidth("200px");
											((TextField) fieldGroup.getField("whatsapp")).setMaxLength(50);
											((TextField) fieldGroup.getField("whatsapp")).setImmediate(true);
											//((TextField) fieldGroup.getField("whatsapp")).setRequired(true); 
											((TextField) fieldGroup.getField("whatsapp")).setTextChangeEventMode(TextChangeEventMode.LAZY);	
											
											if(((TextField) fieldGroup.getField("whatsapp")).getValue() != null && !((TextField) fieldGroup.getField("whatsapp")).equals("")){
												BrowserWindowOpener janelaZap = new BrowserWindowOpener("https://api.whatsapp.com/send?phone="+((TextField) fieldGroup.getField("whatsapp")).getValue()+"&text=Oi");
												janelaZap.setFeatures("height=600,width=800");
												janelaZap.extend(((TextField) fieldGroup.getField("whatsapp")));
											}
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
														
														if(((TextField) fieldGroup.getField("email")).getValue() == null || 
																((TextField) fieldGroup.getField("email")).getValue().isEmpty()){
															
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

								//Agendar CRM
								addComponent(new FormLayout(){
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");
										
										
										ComboBox cbAgendarCrm = new ComboBox("CRM Aniversariante");
										cbAgendarCrm.setWidth("60px");
										cbAgendarCrm.setNullSelectionAllowed(false);
										cbAgendarCrm.setTextInputAllowed(false);
										cbAgendarCrm.setRequired(true);
										cbAgendarCrm.setStyleName("caption-align");
										
										cbAgendarCrm.addItem("SIM");
										cbAgendarCrm.addItem("NAO");
										
										fieldGroup.bind(cbAgendarCrm, "agendar_crm");
//										if(ClienteItem.getItemProperty("agendar_crm").getValue() == null){
//											cbAgendarCrm.select("SIM");											
//										}
										
										addComponent(cbAgendarCrm);
										
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
													setStyleName("form-cutom");

													final ComboBox cbCategoria = new ComboBox("Categoria", containerCategorias);
													cbCategoria.setNullSelectionAllowed(false);
													cbCategoria.setItemCaptionPropertyId("nome");
													cbCategoria.setRequired(true);
													cbCategoria.setConverter(new SingleSelectConverter(cbCategoria));
													fieldGroup.bind(cbCategoria, "categoria");
													cbCategoria.setWidth(width_categoria);
													addComponent(cbCategoria);
													cbCategoria.setStyleName("caption-align");
													
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
										taObs.setHeight("120px");
										//taObs.setMaxLength(200);
										taObs.setImmediate(true);
										taObs.setTextChangeEventMode(TextChangeEventMode.LAZY);
										taObs.addBlurListener(new FieldEvents.BlurListener() {
											
											@Override
											public void blur(BlurEvent event) {
												if(((TextArea)(event.getComponent())) != null && ((TextArea)(event.getComponent())).getValue() != null && !((TextArea)(event.getComponent())).getValue().toString().isEmpty()){
													
													((TextArea)(event.getComponent())).setValue(((TextArea)event.getComponent()).getValue().toUpperCase());
												}
											}
										});
										addComponent(taObs);

									}
								});

							}
						}, "Principal");

				

						// Tab de Outros Endereços
						vlEndereco = new VerticalLayout() {
							{
								addComponent(buildLayoutEndereco());
							}
						};
						
						addTab(vlEndereco, "Endereços");

						
						
						addTab(new VerticalLayout() {
							{

									if(ClienteItem != null && ClienteItem.getItemProperty("indicado_por").getValue() != null){
										addComponent(new FormLayout() {
											{
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");
												
												TextField txtIndicacao = new TextField("Indicado Por");
												txtIndicacao.setWidth("520px");
												txtIndicacao.setStyleName("caption-align-tabOutros");
												txtIndicacao.setValue(((Cliente)ClienteItem.getItemProperty("indicado_por").getValue()).getId().toString()+" - "+((Cliente)ClienteItem.getItemProperty("indicado_por").getValue()).getNome_razao());
												txtIndicacao.setReadOnly(true); 
		
												addComponent(txtIndicacao);
											}
										});
								
									}
								
								addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom");
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
										
										ComboBox cbCredito = new ComboBox("Crédito SPC/SERASA");		
										cbCredito.setStyleName("textfield-cliente-align");
										cbCredito.setNullSelectionAllowed(false);
										cbCredito.setRequired(false); 
										cbCredito.addItem("ADIMPLENTE");
										cbCredito.addItem("INADIMPLENTE");
										cbCredito.addItem("NAO INFORMADO");
										cbCredito.setTextInputAllowed(false);
										cbCredito.setRequired(true);
										
										fieldGroup.bind(cbCredito, "credito_cliente");

										
										addComponent(cbCredito);
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
								
								
								if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Black List"))				
								{
									addComponent(new FormLayout() {
										{
											setMargin(true);
											setSpacing(true);
											setStyleName("form-cutom-new");
											
											ComboBox cbCredito = new ComboBox("Black List");		
											cbCredito.setStyleName("textfield-cliente-align");
											cbCredito.setNullSelectionAllowed(false);
											cbCredito.setRequired(false); 
											cbCredito.addItem("SIM");
											cbCredito.addItem("NAO");											
											cbCredito.setTextInputAllowed(false);
											cbCredito.setRequired(true);
											
											fieldGroup.bind(cbCredito, "black_list");
											
											addComponent(cbCredito); 
										}
									});
									
									addComponent(new FormLayout() {
										{
											setMargin(true);
											setSpacing(true);
											setStyleName("form-cutom");
											
											final TextArea taObs = new TextArea("Observação");
											
											taObs.setNullRepresentation("");
											taObs.setStyleName("textfield-cliente-align");
											taObs.addStyleName("text-area-border");
											taObs.setWidth("95%");
											taObs.setHeight("80px");
											taObs.setMaxLength(200);
											taObs.setImmediate(true);
											
											fieldGroup.bind(taObs, "black_list_obs");
											
											addComponent(taObs); 
										}
									});
								}

							}
						}, "Outras Informações");

					}
				};
				addComponent(tab);
			}
		});

	}
	
	private VerticalLayout buildLayoutEndereco(){
		VerticalLayout vl = new VerticalLayout(){
			{
				final Table tbEnderecos = buildTbEndereco();
				
				if(codClienteImportacao != null){
					enderecosItem = new ArrayList<>();
					for (Object itemId: containerEnderecos.getItemIds()) {
																										
						enderecosItem.add(containerEnderecos.getItem(itemId).getEntity());
						
					}
				}
				
				//Botões
				btAdicionar = new Button("Adicionar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(final ClickEvent eventClick) {
						
						if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Adicionar Endereço"))				
						{
						
						final BeanItem<Endereco> endItem = new BeanItem<Endereco>(new Endereco());
						EnderecoEditor endEditor = new EnderecoEditor(endItem, "Adicionar Endereço", true, ClienteItem.getItemProperty("id").getValue() != null ? true: false);
						
						endEditor.addListerner(new EnderecoEditor.CepListerner() {
							
							@Override
							public void onClose(CepEvent event) {
								if(event.isConfirm()){													
									final Endereco end = endItem.getBean();
									
									if(ClienteItem.getItemProperty("id").getValue() != null){
										
											if(end.isPrincipal()){
												for (Object itemId: containerEnderecos.getItemIds()) {
													containerEnderecos.getItem(itemId).getItemProperty("principal").setValue(false);
												}
											}
											
											end.setClientes(new Cliente((Integer)ClienteItem.getItemProperty("id").getValue()));
											end.setStatus("ATIVO");
											containerEnderecos.addEntity(end);
											containerEnderecos.commit();
											//end.setId((Integer)containerEnderecos.getItem(containerEnderecos.getIdByIndex(containerEnderecos.size()-1)).getItemProperty("id").getValue());
											ClienteItem.getItemProperty("endereco_principal").setValue(end);											
												
																										 
//											Endereco endExclu =(Endereco) ClienteItem.getItemProperty("endereco_principal").getValue();
//											endExclu = EnderecoDAO.find(endExclu.getId());
//											
//											if(endExclu.getClientes() == null){
//												EnderecoDAO.remove(endExclu); 
//											}
										
											eventClick.getButton().setEnabled(true);
									}else{
										
										if(containerEnderecos == null){
											tbEnderecos.setContainerDataSource(buildContainerEnderecos());
											tbEnderecos.setVisibleColumns(new Object[]{"cep","endereco","numero","bairro","cidade","uf"});
											
											tbEnderecos.setColumnHeader("cep", "CEP");
											tbEnderecos.setColumnHeader("endereco", "Endereço");
											tbEnderecos.setColumnHeader("numero", "Número");
											tbEnderecos.setColumnHeader("bairro", "Bairro");
											tbEnderecos.setColumnHeader("cidade", "Cidade");
											tbEnderecos.setColumnHeader("uf", "UF");//
											
											
											
										}
										
										if(enderecosItem == null){
											enderecosItem = new ArrayList<>();														
										}
										
										if(end.isPrincipal()){
											enderecosItem = new ArrayList<>();	
											for (Object itemId: containerEnderecos.getItemIds()) {
												containerEnderecos.getItem(itemId).getItemProperty("principal").setValue(false);
												enderecosItem.add(containerEnderecos.getItem(itemId).getEntity());
											}
											end.setStatus("ATIVO");
											containerEnderecos.addEntity(end);	
											enderecosItem.add(end);
										}else{									
											end.setStatus("ATIVO");
											containerEnderecos.addEntity(end);																															
											enderecosItem.add(end);
										}
										
										
										eventClick.getButton().setEnabled(false);
										
									}
								}
							}
						});
						
						getUI().addWindow(endEditor);
						
						}else{
							Notify.Show("Você não tem Permissão para Adicionar Endereço", Notify.TYPE_ERROR);
						}
					}
				});
				if(ClienteItem.getItemProperty("id").getValue() == null){
					if(containerEnderecos.size() == 0){
						btAdicionar.setEnabled(true);
					}else{
						btAdicionar.setEnabled(false);
					}
				}else{
					btAdicionar.setEnabled(true);
				}
				
				btEditar = new Button("Editar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						if(tbEnderecos.getItem(itemId).getItemProperty("cep").getValue() != null){
						
							if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Editar Endereço"))				
							{				
								if(tbEnderecos.getValue() != null){										
									EnderecoEditor endEditor = new EnderecoEditor(tbEnderecos.getItem(itemId), "Editar Endereço", true,ClienteItem.getItemProperty("id").getValue() != null ? true: false);
									endEditor.addListerner(new EnderecoEditor.CepListerner() {
										
										@Override
										public void onClose(CepEvent event) {
											if(event.isConfirm()){													
												containerEnderecos.commit();
											}
										}
									});
									
									getUI().addWindow(endEditor);											
								}										
							}else{
								Notify.Show("Você não tem Permissão para Editar Endereço", Notify.TYPE_ERROR);
							}
						}else{
							if(tbEnderecos.getValue() != null){										
								EnderecoEditor endEditor = new EnderecoEditor(tbEnderecos.getItem(itemId), "Editar Endereço", true,ClienteItem.getItemProperty("id").getValue() != null ? true: false);
								endEditor.addListerner(new EnderecoEditor.CepListerner() {
									
									@Override
									public void onClose(CepEvent event) {
										if(event.isConfirm()){													
											containerEnderecos.commit();
										}
									}
								});
								
								getUI().addWindow(endEditor);											
							}
						}
						
					}
				});
				btEditar.setEnabled(false);
				
				btInativar = new Button("Ativar/Inativar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir Endereço"))				
						{											
							int codEnd;

							try {
								if(tbEnderecos != null && itemId != null && tbEnderecos.getItem(itemId) != null && tbEnderecos.getItem(itemId).getItemProperty("id") != null && tbEnderecos.getItem(itemId).getItemProperty("id").getValue() != null){
									codEnd = Integer.parseInt(tbEnderecos.getItem(itemId).getItemProperty("id").getValue().toString()); 
								}else{
									codEnd = 0;
								}
							} catch(NumberFormatException ex) {
								codEnd = 0; 
								ex.printStackTrace(); 
							}

							if(codEnd != 0){
								if(itemId != null &&  !EnderecoDAO.checkUtilizacao(codEnd)){	
											
									if(tbEnderecos.getItem(itemId).getItemProperty("status").getValue().equals("ATIVO")){
										tbEnderecos.getItem(itemId).getItemProperty("status").setValue("INATIVO");
									}else{
										tbEnderecos.getItem(itemId).getItemProperty("status").setValue("ATIVO");
									}
									
									tbEnderecos.commit();
									containerEnderecos.commit();
									
									Notify.Show("Endereço Ativado/Inativado com Sucesso!", Notify.TYPE_SUCCESS);
									vlEndereco.removeAllComponents();
									vlEndereco.addComponent(buildLayoutEndereco());
								}else{
									Notification.show("ERRO", "Endereço em Uso Não Pode Ser Inativado!", Type.ERROR_MESSAGE);
								}
							}
						}else{
							Notify.Show("Você não tem Permissão para Ativar/Inativar Endereço", Notify.TYPE_ERROR);
						}
					}
				});
				btInativar.setEnabled(false);
				
				
				btPrincipal = new Button("Principal/Cobrança", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						if(tbEnderecos.getValue() != null && !((boolean)tbEnderecos.getItem(itemId).getItemProperty("principal").getValue())){
							Integer codCliente = (Integer)ClienteItem.getItemProperty("id").getValue();
							Integer codEndPrincipalNovo = (Integer)tbEnderecos.getItem(itemId).getItemProperty("id").getValue();
							EnderecoDAO.changeEnderecoPrincipal(codCliente, codEndPrincipalNovo);
							ClienteItem.getItemProperty("endereco_principal").setValue(EnderecoDAO.find(codEndPrincipalNovo));
							
							vlEndereco.removeAllComponents();
							vlEndereco.addComponent(buildLayoutEndereco());
							Notify.Show("Endereço Principal Alterado com Sucesso!",Notify.TYPE_SUCCESS);
						}
					}
				});
				btPrincipal.setEnabled(false);
				btPrincipal.setDisableOnClick(true); 
				
				HorizontalLayout hlButtonsEndereco = new HorizontalLayout(){
					{										
						addComponent(btAdicionar);									
						addComponent(btEditar);									
						addComponent(btInativar);
						addComponent(btPrincipal);
						
					}
				};
				
				addComponent(hlButtonsEndereco);
				setComponentAlignment(hlButtonsEndereco, Alignment.MIDDLE_RIGHT);
				
				addComponent(tbEnderecos);
				addComponent(new Label("(<strong>Negrito</strong>) Endereço Principal / Cobrança",ContentMode.HTML));
				
			}
		};
		
		return vl;
	}

	private Table buildTbEndereco(){
		
		final Table tbEnderecos = new Table();
		tbEnderecos.setWidth("100%");
		tbEnderecos.setHeight("300px");
		tbEnderecos.setSelectable(true);

		tbEnderecos.setContainerDataSource(buildContainerEnderecos());
		tbEnderecos.setVisibleColumns(new Object[]{"id","cep","endereco","numero","bairro","cidade","uf"});
					
		tbEnderecos.setColumnHeader("id", "ID");
		tbEnderecos.setColumnHeader("cep", "CEP");
		tbEnderecos.setColumnHeader("endereco", "Endereço");
		tbEnderecos.setColumnHeader("numero", "Número");
		tbEnderecos.setColumnHeader("bairro", "Bairro");
		tbEnderecos.setColumnHeader("cidade", "Cidade");
		tbEnderecos.setColumnHeader("uf", "UF");				
		
		tbEnderecos.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				if((boolean)source.getItem(itemId).getItemProperty("principal").getValue()){
					return "row-header-end-principal";
				}else{					
					
					if(source.getItem(itemId).getItemProperty("status").getValue().toString().equals("INATIVO")){
						return "row-header-pedido-cancelado";
					}else{						
						return "row-header-default";
					}
					
				}
			}
		});

		if(ClienteItem.getItemProperty("id").getValue() == null){
			tbEnderecos.addGeneratedColumn("", new Table.ColumnGenerator() {
				
				@Override
				public Object generateCell(final Table source,final  Object itemId, Object columnId) {
					
					Button btDeletar = new Button(null, new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Excluir este Endereço?", true,true);
							gd.addListerner(new GenericDialog.DialogListerner() {
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){		
										Integer codRelatorio  = (Integer)source.getItem(itemId).getItemProperty("id").getValue();
										containerEnderecos.removeItem(itemId);
										
										btAdicionar.setEnabled(true);
									}
								}
							});
							
							getUI().addWindow(gd);
						}
					});
					btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
					btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
					btDeletar.setDescription("Deletar Endereço");
					
					return btDeletar;
				}
			});
		}
		tbEnderecos.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(event.getProperty().getValue() != null){
					itemId = event.getProperty().getValue();
					if(ClienteItem.getItemProperty("id").getValue() != null){
						if(tbEnderecos.getItem(itemId).getItemProperty("status").getValue().toString().equals("ATIVO")){
							btEditar.setEnabled(true);
						}else{
							btEditar.setEnabled(false);
						}
					}else{
						btEditar.setEnabled(false);
					}
					
							
					
					if(((boolean)tbEnderecos.getItem(event.getProperty().getValue()).getItemProperty("principal").getValue())){
						btPrincipal.setEnabled(false); 
						btInativar.setEnabled(false);		
					}else{
						if(tbEnderecos.getItem(itemId).getItemProperty("status").getValue().toString().equals("ATIVO")){
							btPrincipal.setEnabled(true); 
						}else{
							btPrincipal.setEnabled(false);
						}
						btInativar.setEnabled(true);		
					}
					
				}else{
					btEditar.setEnabled(false);
					btInativar.setEnabled(false);
					btPrincipal.setEnabled(false); 
				}
			}
		});
		
		return tbEnderecos;
	}
	
	private JPAContainer<Endereco> buildContainerEnderecos(){
		
		containerEnderecos = JPAContainerFactory.makeBatchable(Endereco.class, ConnUtil.getEntity());
		containerEnderecos.setAutoCommit(false);
		
		Integer codCliente = null;
		if(ClienteItem.getItemProperty("id").getValue() != null || codClienteImportacao != null){
			
			if(codClienteImportacao != null){
				codCliente = codClienteImportacao;
			}
			
			if(ClienteItem.getItemProperty("id").getValue() != null){
				codCliente = Integer.parseInt(ClienteItem.getItemProperty("id").getValue().toString());
			}
		}else{
			codCliente = Integer.parseInt(getNextId());
		}
		containerEnderecos.addContainerFilter(Filters.eq("clientes", new Cliente(codCliente)));
		containerEnderecos.applyFilters();
		
		
		
		return containerEnderecos;			
	}

	
	

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				String email = ((TextField) fieldGroup.getField("email")).getValue();
				if(email != null && !email.isEmpty() && EmailUtil.validate(email) && !email.equals(emailInicial)){
					
					if(!ClienteDAO.checkEmailExists(email)){																
						((TextField) fieldGroup.getField("email")).removeStyleName("invalid-txt");
						emailValid = true;																
					}else{
						((TextField) fieldGroup.getField("email")).addStyleName("invalid-txt");
						emailValid = false;
					}
				}else{
					
					if(email != null && !email.isEmpty() && !email.equals(emailInicial)){
						emailValid = false;
						((TextField) fieldGroup.getField("email")).addStyleName("invalid-txt");
					}else{
						if(!((TextField) fieldGroup.getField("email")).isRequired()){
							emailValid = true;
							((TextField) fieldGroup.getField("email")).removeStyleName("invalid-txt");
						}else{
							if(((TextField) fieldGroup.getField("email")).getValue() == null || ((TextField) fieldGroup.getField("email")).getValue().equals("")){
								emailValid = false;
								((TextField) fieldGroup.getField("email")).addStyleName("invalid-txt");
							}else{
								emailValid = true;
								((TextField) fieldGroup.getField("email")).removeStyleName("invalid-txt");
							}
						}
					}
				}
				
				
				
				
				boolean endValid = false;
				enderecosItem = new ArrayList<>();
				if(containerEnderecos.size() >0){
					for (Object itemId: containerEnderecos.getItemIds()) {
						
						if((boolean)containerEnderecos.getItem(itemId).getItemProperty("principal").getValue()){
							endValid = true;							
						}						
						enderecosItem.add(containerEnderecos.getItem(itemId).getEntity());
					}
				}else{
					endValid = false;
				}
				
				if (fieldGroup.isValid() && cnpjCadast && cnpjValid && 
						containerEnderecos.size() >0 && endValid && emailValid) {
					try {		
						
						fieldGroup.commit();
						
						if(ClienteItem.getItemProperty("doc_cpf_cnpj").getValue().toString().length() == 11){
							ClienteItem.getItemProperty("tipo_pessoa").setValue("Pessoa Física");
						}else{
							ClienteItem.getItemProperty("tipo_pessoa").setValue("Pessoa Jurídica");
						}
						
						
						
						fireEvent(new ClienteEvent(getUI(), ClienteItem, true, enderecosItem));
						
													
						Notification.show("Cliente Gravado com Sucesso!");						

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
							
							if(field.getCaption() != null && field.getCaption().equals("Crédito SPC/SERASA")){
								tab.getTab(2).setStyleName("tab-invalid");
							}
						}else{
							field.removeStyleName("invalid-txt");
							
							if(field.getCaption() != null && field.getCaption().equals("Crédito SPC/SERASA")){
								tab.getTab(2).setStyleName("tab-valid");
							}
						}
					}
					
					if(!emailValid){
						((TextField) fieldGroup.getField("email")).addStyleName("invalid-txt");
					}else{
						((TextField) fieldGroup.getField("email")).removeStyleName("invalid-txt");
					}
					
					if(containerEnderecos.size() == 0){
						Notification.show("Informe ao Menos um Endereço!");
					}else{
						
						if(!endValid){
							Notification.show("Informe ao Menos um Endereço como Principal!");
						}else{						
							Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
						}
					}
				}
			}
		});

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER, null) {

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
			
				boolean endValid = false;			
				if(containerEnderecos.size() >0){
					for (Object itemId: containerEnderecos.getItemIds()) {
						
						if((boolean)containerEnderecos.getItem(itemId).getItemProperty("principal").getValue()){
							endValid = true;
							break;
						}
					}
				}else{
					endValid = false;
				}
				
				
				if (!fieldGroup.isModified() && endValid) {
					close();
				} else {
					final GenericDialog gDialog = new GenericDialog("Confirme para Continuar!","Deseja Salvar as Informações Alteradas?", true,true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if (event.isConfirm()) {
																
								boolean endValid = false;			
								enderecosItem = new ArrayList<>();
								if(containerEnderecos.size() >0){
									for (Object itemId: containerEnderecos.getItemIds()) {
										
										if((boolean)containerEnderecos.getItem(itemId).getItemProperty("principal").getValue()){
											endValid = true;											
										}
										enderecosItem.add(containerEnderecos.getItem(itemId).getEntity());
									}
								}else{
									endValid = false;
								}
								
								if (fieldGroup.isValid() && cnpjCadast == true && cnpjValid == true && endValid && containerEnderecos.size() >0 && endValid && emailValid) {
									try {
										// CLIENTES
										fieldGroup.commit();
										
										
											fireEvent(new ClienteEvent(getUI(), ClienteItem, true, enderecosItem));
									
																			
										Notification.show("Cliente Gravado com Sucesso!");									
										
										close();
									} catch (Exception e) {
										e.printStackTrace();
										System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
										Notification.show("Não foi Possivel Salvar as Alterações!");
									}
									gDialog.close();
									close();
																
								}else if(!endValid){
									Notification.show("Informe ao Menos um Endereço como Principal!");
								}else{
									Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
								}
								
							} else {
								fieldGroup.discard();							
								gDialog.close();
								fireEvent(new ClienteEvent(getUI(), ClienteItem, false, null));
								close();
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
		private List<Endereco> enderecos;

		public ClienteEvent(Component source, Item itemCliente, boolean confirm, List<Endereco> enderecos) {
			super(source);
			this.item = itemCliente;
			this.confirm = confirm;
			this.enderecos = enderecos;
		}

		public Item getItem() {
			return item;
		}

		public boolean isConfirm() {
			return confirm;
		}
		
		public List<Endereco> getEnderecos() {
			return enderecos;
		}
	}

	public interface ClientesListerner extends Serializable {
		public void onClose(ClienteEvent event);
	}
	

}
