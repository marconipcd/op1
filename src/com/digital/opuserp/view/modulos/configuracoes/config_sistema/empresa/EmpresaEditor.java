	package com.digital.opuserp.view.modulos.configuracoes.config_sistema.empresa;
	
	import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.vaadin.suggestfield.SuggestField;
import org.vaadin.suggestfield.SuggestField.SuggestionHandler;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.EmpresaDAO;
import com.digital.opuserp.dao.TransportadoraDAO;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.domain.Cnae;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.CnpjUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.ValidarCPF;
import com.digital.opuserp.view.util.CepUtil;
import com.digital.opuserp.view.util.CnaeUtil;
import com.digital.opuserp.view.util.MunicipiosUtil;
import com.digital.opuserp.view.util.MunicipiosUtil.MunicipioEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
	
	public class EmpresaEditor extends Window implements GenericEditor {
	
		Item item;
		Button btSalvar;
		Button btCancelar;
		FormLayout flPrincipal;
		FieldGroup fieldGroup;
		VerticalLayout vlRoot;
		VerticalLayout vlRoot1;
		
		ShortcutListener sl_esc;
		
		private Float width_codigo;
		private Float width_alteracao;
		private Float width_cadastro;
		private Float width_status;
		
		private Float width_tipoPessoa;
		private Float width_cpfcnpj;
		private Float width_insc_estadual;
		private Float width_endereco;
		private Float width_numero;
		private Float width_bairro;
		private Float width_cidade;
		private Float width_uf;
		private Float width_pais;
		
		private String nameImagemUploaded;
		private Image logo;	
		
		boolean cnpjValid = false;
		boolean cnpjCadast= false;
		
		public File file;
		
		Cnae cnaeSelecionado;
		
		private TextField txtCidade;
		private TextField txtUf;
		private TextField txtPais;
		
		public EmpresaEditor(Item item, String title, boolean modal){
			
			getEnderecos();
			this.item = item;
			
			Styles styles = Page.getCurrent().getStyles();					        
	        styles.add(".dashboard input.v-textfield-readonly { background-color: #FFF; }");
			
			if(item.getItemProperty("logo_empresa").getValue() != null){
				String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
				file = new File(basepath + "/WEB-INF/uploads/logo.jpeg");
				try{
				    writeFile(file, (byte[]) item.getItemProperty("logo_empresa").getValue());
				}catch(Exception e){
					e.printStackTrace();
					Notify.Show("Logo Não Encontrado!", Notify.TYPE_WARNING);
				}
			}
			
			
			configLayout();	
			
			setCaption(title);
			setModal(modal);
			setResizable(false);
			setClosable(false);
			center();
			
			vlRoot = new VerticalLayout();	
			vlRoot.setWidth("100%");
			vlRoot.setMargin(true);
			//vlRoot.setStyleName("border-form");
			
			
			vlRoot1 = new VerticalLayout();	
			vlRoot1.setWidth("100%");
			vlRoot1.setMargin(true);
			//vlRoot1.setStyleName("border-form");
			
			
			final TabSheet tbSh = new TabSheet();
			tbSh.addTab(vlRoot, "Principal");
			tbSh.addTab(vlRoot1, "Outras Informações");
			tbSh.setSizeFull();
			tbSh.setHeight("594px");
			tbSh.setStyleName("fornecedor_editor");
			
			setContent(new VerticalLayout(){
				{
					setWidth("100%");
					setMargin(true);
					addComponent(tbSh);
					
					HorizontalLayout hlButtons = new HorizontalLayout();
					hlButtons.setSpacing(true);
					hlButtons.setMargin(true);
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtSalvar());
					
					addComponent(hlButtons);
					setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				}
			});
			
			//sl_esc = buildShortCurListerner();
	
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
					setHeight("707px");
				
					width_numero = 0.1f;
					width_endereco = 0.53f;				
					width_bairro = 0.35f;
					width_cidade = 0.23f;
					width_uf = 0.1f;
					width_pais = 0.2f;
				
			//}
		}
		public void updateCep(Ceps cep){
			((TextField) fieldGroup.getField("cep")).setValue(cep.getCep());
			((TextField) fieldGroup.getField("endereco")).setValue(cep.getEndereco());
			((TextField) fieldGroup.getField("numero")).setValue("");
			((SuggestField) fieldGroup.getField("bairro")).setValue(cep.getBairro());
			((TextField) fieldGroup.getField("cidade")).setReadOnly(false);
			((TextField) fieldGroup.getField("cidade")).setValue(cep.getCidade());
			((TextField) fieldGroup.getField("cidade")).setReadOnly(true);
			
			((TextField) fieldGroup.getField("uf")).setReadOnly(false);
			((TextField) fieldGroup.getField("uf")).setValue(cep.getUf());
			((TextField) fieldGroup.getField("uf")).setReadOnly(true);
			
			((TextField) fieldGroup.getField("pais")).setReadOnly(false);
			((TextField) fieldGroup.getField("pais")).setValue(cep.getPais());
			((TextField) fieldGroup.getField("pais")).setReadOnly(true);
			((TextField) fieldGroup.getField("numero")).focus();
		}
		public void buildLayout(){
			
			fieldGroup = new FieldGroup(item);
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
					setWidth("100%");					
					
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");
							
							TextField tfCod = (TextField)fieldGroup.buildAndBind("Código", "id");
							tfCod.setWidth("100%");
							tfCod.setReadOnly(true);
							tfCod.setStyleName("caption-align-empresa");
							tfCod.setNullRepresentation("");
							tfCod.focus();
							
							addComponent(tfCod);						
						}
					});
					
					
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");
							
							TextField txtDataCadastro = new TextField ("Cadastro");
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");				
							if(fieldGroup.getItemDataSource().getItemProperty("data_cadastro").getValue() != null){
								txtDataCadastro.setValue(sdf.format(fieldGroup.getItemDataSource().getItemProperty("data_cadastro").getValue()).toString());
							}else{
								txtDataCadastro.setValue(sdf.format(new Date()));
							}
							
							txtDataCadastro.setWidth("100%");
							txtDataCadastro.setReadOnly(true);						
							txtDataCadastro.setNullRepresentation("");
							
							addComponent(txtDataCadastro);
						}
					});
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");
							
							TextField txtDataAlteracao = new TextField ("Alteração");	
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");				
							if(fieldGroup.getItemDataSource().getItemProperty("data_alteracao").getValue() != null){
								txtDataAlteracao.setValue(sdf.format(fieldGroup.getItemDataSource().getItemProperty("data_alteracao").getValue()).toString());
							}else{
								txtDataAlteracao.setValue(sdf.format(new Date()));
							}
							txtDataAlteracao.setWidth("100%");
							txtDataAlteracao.setReadOnly(true);						
							txtDataAlteracao.setNullRepresentation("");
							
							addComponent(txtDataAlteracao);
						}
					});
					
					
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");
							
							ComboBox cbStatus = new ComboBox("Status");							
							cbStatus.setWidth("165px");
							cbStatus.setNullSelectionAllowed(false);
							cbStatus.addItem("ATIVO");
							cbStatus.addItem("INATIVO");	
							cbStatus.focus();
							
							addComponent(cbStatus);
							fieldGroup.bind(cbStatus, "status");
						}
					});
					
				}
			});
			
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
					//setWidth("100%");			
					
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");
							
							TextField tfCod = (TextField)fieldGroup.buildAndBind("CPF/CNPJ", "cnpj");
							tfCod.setWidth("100%");
							tfCod.setRequired(true);
							tfCod.setStyleName("caption-align-empresa");
							tfCod.setNullRepresentation("");
							tfCod.setMaxLength(14);											
							//teste
							tfCod.setTextChangeEventMode(TextChangeEventMode.LAZY);
							tfCod.addListener(new FieldEvents.TextChangeListener() {

								@Override
								public void textChange(TextChangeEvent event) {

									if (!event.getText().isEmpty()) {

										if (event.getText().length() == 14) {
											if (!CnpjUtil.validar(event.getText())) {
												// CNPJ Inválido
												cnpjValid = false;
												event.getComponent().setStyleName("textfield-empresa-invalid");
											} else {
												// CNPJ Válido
												cnpjValid = true;
												event.getComponent().setStyleName("textfield-empresa-valid");
											}
										} else if (event.getText().length() == 11) {

											if (!ValidarCPF.cpfValido(event
													.getText())) {
												// CPF Inválido
												cnpjValid = false;
												event.getComponent().setStyleName("textfield-empresa-invalid");
											} else {
												// CPF Válido
												cnpjValid = true;
												event.getComponent().setStyleName("textfield-empresa-valid");
											}
										} else {
											// CPF/CNPJ Inválido
											cnpjValid = false;
											event.getComponent().setStyleName("textfield-empresa-invalid");
										}
									} else {
										event.getComponent().setStyleName("textfield-empresa-invalid");
									}
									
									int empresa_id = OpusERP4UI.getEmpresa().getId();
									String cnpj = event.getText();
									
									TransportadoraDAO transDAO = new TransportadoraDAO();								
									if(transDAO.cpfCnpjCadastrado(empresa_id, cnpj) == true ){
										cnpjCadast = true;		
										if(cnpjCadast == true && cnpjValid == true){
											event.getComponent().setStyleName("textfield-empresa-valid");
										}
									}else{
										cnpjCadast = false;									
										event.getComponent().setStyleName("textfield-empresa-invalid");
									}
									
								}
							});

							 tfCod.addListener(new FieldEvents.BlurListener() {
									
									@Override
									public void blur(BlurEvent event) {
										if(cnpjValid == false){
											Notify.Show("CPF/CNPJ Inválido!", Notify.TYPE_ERROR);
										}else if(cnpjCadast == false){
											Notify.Show("CPF/CNPJ Já Cadastrado!", Notify.TYPE_ERROR);
										}						
									}
								});

							addComponent(tfCod);
							if (item.getItemProperty("id").getValue() != null) {
								cnpjValid = true;
								tfCod.setStyleName("textfield-empresa-valid");
								tfCod.setReadOnly(true);
							}
						}
					});
							//teste
					
					
					
					
					
					
					
					
					
					
					
					
					final Button btEnvelope = new Button();
					btEnvelope.setStyleName(BaseTheme.BUTTON_LINK);
					btEnvelope.setIcon(new ThemeResource("icons/browser-16.png"));
					btEnvelope.setDescription("Pesquisar na Receita Federal");
					
					BrowserWindowOpener opener = new BrowserWindowOpener("http://www.receita.fazenda.gov.br/pessoajuridica/cnpj/cnpjreva/cnpjreva_solicitacao.asp");
					opener.setFeatures("height=600,width=800");
					opener.extend(btEnvelope);
					
					addComponent(new FormLayout(){
						{
							setStyleName("form-cutom-new");	
							addComponent(btEnvelope);
						}
					});
				}
			});
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
					setWidth("100%");

					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");
							
							TextField tfCod = (TextField)fieldGroup.buildAndBind("Inscrição Estadual/RG", "inscricao_estadual");
							tfCod.setWidth("100%");
							tfCod.setRequired(false);
							tfCod.setStyleName("caption-align-empresa");
							tfCod.setNullRepresentation("");
							tfCod.setMaxLength(100);
							
							addComponent(tfCod);
						}
					});
					
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");
							
							TextField tfCod = (TextField)fieldGroup.buildAndBind("Insc. Municipal", "inscricao_municipal");
							tfCod.setWidth("140px");
							tfCod.setRequired(false);					
							tfCod.setNullRepresentation("");
							tfCod.setMaxLength(100);
							
							addComponent(tfCod);
						}
					});
				}
			});
			
			
			vlRoot.addComponent(new FormLayout(){
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom-new");
					
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Razão Social", "razao_social");
					tfCod.setWidth("100%");
					tfCod.setRequired(true);					
					tfCod.setNullRepresentation("");
					tfCod.setStyleName("caption-align-empresa");
					tfCod.setMaxLength(200);
					
					addComponent(tfCod);
				}
			});
			
			vlRoot.addComponent(new FormLayout(){
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");
					
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Fantasia", "nome_fantasia");
					tfCod.setWidth("340px");
					tfCod.setRequired(true);					
					tfCod.setNullRepresentation("");
					tfCod.setStyleName("caption-align-empresa");
					tfCod.setMaxLength(200);
					
					addComponent(tfCod);
				}
			});
			
			vlRoot.addComponent(new FormLayout(){
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom-new");
					
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Principais Sócios", "socio1");
					tfCod.setWidth("100%");
					tfCod.setRequired(false);					
					tfCod.setNullRepresentation("");
					tfCod.setStyleName("caption-align-empresa");
					tfCod.setMaxLength(100);
					
					addComponent(tfCod);
				}
			});
			
			vlRoot.addComponent(new FormLayout(){
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");
					
					TextField tfCod = (TextField)fieldGroup.buildAndBind("", "socio2");
					tfCod.setWidth("100%");
					tfCod.setRequired(false);					
					tfCod.setNullRepresentation("");
					tfCod.setStyleName("caption-align-empresa");
					tfCod.setMaxLength(100);
					
					addComponent(tfCod);
				}
			});
			
			vlRoot.addComponent(new FormLayout(){
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");
					
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Responsável", "responsavel");
					tfCod.setWidth("100%");
					tfCod.setRequired(true);					
					tfCod.setNullRepresentation("");
					tfCod.setStyleName("caption-align-empresa");
					tfCod.setMaxLength(70);
					
					addComponent(tfCod);
				}
			});
			
			
			vlRoot.addComponent(
					new HorizontalLayout(){
						{
							addComponent(new FormLayout(){					
								{
									
									setMargin(true);
									setSpacing(true);
									setStyleName("form-cutom-new");						
													
									TextField tfCod = (TextField)fieldGroup.buildAndBind("CEP", "cep");				
									tfCod.setWidth("80px");				
									tfCod.setStyleName("caption-align-empresa");
									tfCod.setNullRepresentation("");
									tfCod.setMaxLength(8);
									tfCod.setRequired(true);
									tfCod.setTextChangeEventMode(TextChangeEventMode.LAZY);
									tfCod.addListener(new FieldEvents.TextChangeListener() {
										
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
									
									tfCod.addListener(new FieldEvents.BlurListener() {
										
										@Override
										public void blur(BlurEvent event) {
											((TextField) fieldGroup.getField("numero")).focus();
										}
									});
									
									addComponent(tfCod);
									setExpandRatio(tfCod, 2);						
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
			
			
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					
					FormLayout frmEndereco = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");						
											
//							TextField tfCod = (TextField)fieldGroup.buildAndBind("Endereço", "endereco");				
//							tfCod.setWidth("100%");				
//							tfCod.setNullRepresentation("");
//							tfCod.setStyleName("caption-align-empresa");
//							tfCod.setRequired(true);
//							tfCod.setMaxLength(200);
							addComponent(fieldGroup.buildAndBind("Endereço", "endereco"));
							((TextField) fieldGroup.getField("endereco")).setWidth("100%");
							((TextField) fieldGroup.getField("endereco")).setNullRepresentation("");
							((TextField) fieldGroup.getField("endereco")).setStyleName("caption-align-empresa");
							((TextField) fieldGroup.getField("endereco")).setRequired(true);
							((TextField) fieldGroup.getField("endereco")).setMaxLength(200);
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
							tfCod.setWidth("100%");				
							tfCod.setNullRepresentation("");
							tfCod.setRequired(true);
							tfCod.setMaxLength(10);
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
							search.setValue(item.getItemProperty("bairro").getValue().toString());
							search.setWidth("200px");				
							
							search.setStyleName("caption-align-empresa");
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
			
			
//			vlRoot.addComponent(new HorizontalLayout(){
//				{
//					setWidth("100%");
//					FormLayout frmBairro = new FormLayout(){
//						{
//							setStyleName("form-cutom");
//							setMargin(true);
//							setSpacing(true);
//							setStyleName("form-cutom");						
//											
//							TextField tfCod = (TextField)fieldGroup.buildAndBind("Bairro", "bairro");				
//							tfCod.setWidth("200px");				
//							tfCod.setNullRepresentation("");
//							tfCod.setStyleName("caption-align-empresa");
//							tfCod.setRequired(true);
//							tfCod.setMaxLength(50);
//							addComponent(tfCod);
//							setExpandRatio(tfCod, 2);
//						}
//					};
//					
//					addComponent(frmBairro);
//					setExpandRatio(frmBairro, width_bairro);
//					
//					FormLayout frmCidade = new FormLayout(){
//						{
//							setStyleName("form-cutom");
//							setMargin(true);
//							setSpacing(true);
//							setStyleName("form-cutom");						
//											
//							TextField tfCod = (TextField)fieldGroup.buildAndBind("Cidade", "cidade");				
////							tfCod.setWidth("100%");				
//							tfCod.setNullRepresentation("");
//							tfCod.setRequired(true);
//							tfCod.setMaxLength(50);
//							addComponent(tfCod);
//							setExpandRatio(tfCod, 2);
//						}
//					};
//					addComponent(frmCidade);
//					setExpandRatio(frmCidade, width_cidade);
//					
//					
//					FormLayout frmUf = new FormLayout(){
//						{
//							setStyleName("form-cutom");
//							setMargin(true);
//							setSpacing(true);
//							setStyleName("form-cutom");						
//											
//							TextField tfCod = (TextField)fieldGroup.buildAndBind("UF", "uf");				
//							tfCod.setWidth("40px");				
//							tfCod.setNullRepresentation("");
//							tfCod.setRequired(true);
//							tfCod.setMaxLength(2);
//							addComponent(tfCod);
//							setExpandRatio(tfCod, 2);
//						}
//					};
//					addComponent(frmUf);
//					setExpandRatio(frmUf, width_uf);
//					
//					FormLayout frmPais = new FormLayout(){
//						{
//							setStyleName("form-cutom");
//							setMargin(true);
//							setSpacing(true);
//							setStyleName("form-cutom");						
//											
//							TextField tfCod = (TextField)fieldGroup.buildAndBind("Pais", "pais");				
//							tfCod.setWidth("148px");				
//							tfCod.setNullRepresentation("");
//							tfCod.setRequired(true);
//							tfCod.setMaxLength(50);
//							addComponent(tfCod);
//							setExpandRatio(tfCod, 2);
//						}
//					};
//					addComponent(frmPais);
//					
//					
//						setExpandRatio(frmPais, width_pais);
//				
//					
//					
//				}	
//			});
			
			vlRoot.addComponent(new HorizontalLayout(){
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
							tfCod.setStyleName("caption-align-empresa");
							tfCod.setRequired(false);
							tfCod.setMaxLength(100);
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
											
							TextField tfCod = (TextField)fieldGroup.buildAndBind("Referência", "referencia");				
							tfCod.setWidth("100%");				
							tfCod.setNullRepresentation("");
							tfCod.setRequired(false);
							tfCod.setMaxLength(100);
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
							tfCod.setStyleName("caption-align-empresa");
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
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
					setSizeUndefined();		
					
					
					FormLayout frmDDD_Fone1 = new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");
							
							TextField tfCod = (TextField)fieldGroup.buildAndBind("Telefone Alternativo", "ddd_fone3");
							tfCod.setWidth("40px");							
							tfCod.setStyleName("caption-align-empresa");
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
							
							TextField tfCod = (TextField)fieldGroup.buildAndBind("0800", "_0800");
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
			
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
									
					addComponent(new FormLayout(){
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);
											
											
							TextField tfCod = (TextField)fieldGroup.buildAndBind("E-mail", "email");				
							tfCod.setWidth("100%");				
							tfCod.setNullRepresentation("");
							tfCod.setStyleName("caption-align-empresa");
							tfCod.setRequired(true);
							tfCod.setMaxLength(100);
							addComponent(tfCod);
							setExpandRatio(tfCod, 2);
						}
					});
					
					addComponent(new FormLayout(){
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);											
											
							TextField tfCod = (TextField)fieldGroup.buildAndBind("Site", "site");				
							tfCod.setWidth("100%");				
							tfCod.setNullRepresentation("");						
							tfCod.setRequired(true);
							tfCod.setMaxLength(100);
							addComponent(tfCod);
							setExpandRatio(tfCod, 2);
						}
					});
					
				}	
			});
			
			
			
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
					//setWidth("100%");
					
					boolean preencher = false;
					if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cnae_fiscal").getValue() != null){
						preencher = true;
					}
					
					final TextField tfCodigoCnae = new TextField("CNAE Fiscal");				
					tfCodigoCnae.setWidth("75px");				
					tfCodigoCnae.setNullRepresentation("");
					tfCodigoCnae.setStyleName("caption-align-empresa");
					tfCodigoCnae.setRequired(true);		
					
					final TextField tfDescricaoCnae = new TextField();
					tfDescricaoCnae.setReadOnly(true);
					tfDescricaoCnae.setWidth("580px");
					
					
					EmpresaDAO eDAO = new EmpresaDAO();
					Cnae c = eDAO.getCnae(((Cnae)item.getItemProperty("cnae_fiscal").getValue()).getId());
					if(c != null){
						tfCodigoCnae.setValue(c.getCodigo());
						tfDescricaoCnae.setReadOnly(false);
						tfDescricaoCnae.setValue(c.getDescricao());
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
										tfCodigoCnae.setValue(event.getCnae().getCodigo());
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
							setStyleName("form-cutom-new");		
													
							addComponent(tfCodigoCnae);							
						}
					};
					addComponent(frmCodigoCnae);
					
					
					FormLayout frmButtonSearchCnae =new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new_hide_error_cell");										
							addComponent(btSearchCnae);							
						}
					}; 
					
					
					FormLayout frmDescCnae = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);						
													
							addComponent(tfDescricaoCnae);							
						}
					}; 
					addComponent(frmButtonSearchCnae);
					addComponent(frmDescCnae);
					setExpandRatio(frmDescCnae, 1);
					
					
					
				}	
			});
			
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
					//setWidth("100%");
					
					addComponent(new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");
							
							ComboBox cbRamo = new ComboBox("Optante do Simples");
							cbRamo.setStyleName("caption-align-empresa");
							cbRamo.setNullSelectionAllowed(false);					
							cbRamo.addItem("SIM");
							cbRamo.addItem("NAO");
							
							
							cbRamo.setRequired(true);
							
							fieldGroup.bind(cbRamo, "opta_pelo_simples");
							
							addComponent(cbRamo);
						}
					});
									
					addComponent(new FormLayout(){
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
											
							DateField dfDataImplantacaoERP = new DateField("Implantação do ERP");											
											
							dfDataImplantacaoERP.setStyleName("caption-align-empresa");
							dfDataImplantacaoERP.setRequired(false);
							dfDataImplantacaoERP.setDateFormat("dd/MM/yyyy");
							
							addComponent(dfDataImplantacaoERP);
							fieldGroup.bind(dfDataImplantacaoERP, "data_implantacao");
						}
					});
					
					
					
				}	
			});
			
			vlRoot.addComponent(new FormLayout(){
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");						
											
							TextField txtAliquotaInterna = (TextField)fieldGroup.buildAndBind("Aliquota Interna", "aliquota_interna");				
							txtAliquotaInterna.setWidth("60px");				
							txtAliquotaInterna.setNullRepresentation("");
							txtAliquotaInterna.addStyleName("caption-align-empresa");
							txtAliquotaInterna.setRequired(false);
							txtAliquotaInterna.addStyleName("align-currency");
							txtAliquotaInterna.setId("txtAliquotaInterna");
							
							addComponent(txtAliquotaInterna);
							
							JavaScript.getCurrent().execute("$('#txtAliquotaInterna').maskMoney({decimal:',',thousands:'.'})");
							
						}
			});
			
			
			vlRoot1.addComponent(new HorizontalLayout(){
				{
					setSizeFull();
							addComponent(new FormLayout(){
							{
								
								setWidth("100%");
								setStyleName("form-cutom");
								setMargin(false);
								setSpacing(false);							
																
								final Upload imageUploaded = new Upload(null,null);
								imageUploaded.setButtonCaption("Upload");								
								
								ImageUploader uploader = new ImageUploader();
								imageUploaded.setReceiver(uploader);
								imageUploaded.addSucceededListener(uploader);
								
								if(item.getItemProperty("logo_empresa").getValue() != null){
									//File file = new File(basepath + "/WEB-INF/uploads/" + item.getItemProperty("logo").getValue());
									logo = new Image(null, new FileResource(file));
								}else{
									logo = new Image(null, new ThemeResource("img/default_logo.png"));
								}								
								
								//logo.setWidth("300px");
								//logo.setHeight("70px");
								
								Panel pnLogo = new Panel("Logo");
								pnLogo.setStyleName("pn-logo-align-empresa");
								pnLogo.setSizeUndefined();	
								pnLogo.setContent(new VerticalLayout(){
									{
										addComponent(logo);
										addComponent(imageUploaded);
										
										setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
										setComponentAlignment(imageUploaded, Alignment.MIDDLE_CENTER);
									}
								});
								
								addComponent(pnLogo);
							}
						});							
				}
			});
			
			
			
			vlRoot1.addComponent(new HorizontalLayout(){
				{
					addComponent(new FormLayout(){
						{
							Label px = new Label("Tamanho Recomendado 300x87px");
							px.setStyleName("label-pn-logo-align-empresa-px");
							
							addComponent(px);
						}
					});
					
				}
			});
			
		}
		
		
		
	
		@Override
		public Button buildBtSalvar() {
			btSalvar = new Button("Ok", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(item.getItemProperty("id").getValue() != null){
						cnpjCadast = true;
					}
					
					if(item.getItemProperty("cnae_fiscal").getValue() != null && cnaeSelecionado == null){
						cnaeSelecionado = ((Cnae) item.getItemProperty("cnae_fiscal").getValue());
					}
					
					if(fieldGroup.isValid() && cnaeSelecionado != null && cnpjValid == true && cnpjCadast == true){				
						try {										
									fieldGroup.commit();

									item.getItemProperty("logo_empresa").setValue(readImageOldWay(file));
									item.getItemProperty("data_ateracao").setValue(new Date());
									item.getItemProperty("cnae_fiscal").setValue(cnaeSelecionado);
									
									fireEvent(new EmpresaSavedEvent(getUI(), item,true, cnaeSelecionado));
									Notify.Show("Empresa Alterada com Sucesso!", Notify.TYPE_ERROR);
									close();
											
						} catch (Exception e) {					
							e.printStackTrace();					
						}
					}else{
						
						if(txtCidade.getValue() == null || txtUf.getValue() == null || txtPais.getValue() == null){
							Styles styles = Page.getCurrent().getStyles();					        
							styles.add(".dashboard input.v-textfield-readonly { background-color: #E97953; }");						
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
			
			btSalvar.addStyleName("default");
			return btSalvar;
		}
	
		@Override
		public Button buildBtCancelar() {
			btCancelar = new Button("Cancelar", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					if(item.getItemProperty("id").getValue() != null){
						cnpjCadast = true;
					}
					
					if(item.getItemProperty("cnae_fiscal").getValue() != null && cnaeSelecionado == null){						
						
						cnaeSelecionado = ((Cnae) item.getItemProperty("cnae_fiscal").getValue());
					}
					
					if(fieldGroup.isModified() && cnaeSelecionado != null && cnpjValid == true && cnpjCadast == true){
						
						final GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
						gDialog.setCaptionCANCEL("Sair sem Salvar!");
						gDialog.setCaptionOK("Salvar");
						
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									if(fieldGroup.isValid()){
										try {
											fieldGroup.commit();
											//item.getItemProperty("logo").setValue(nameImagemUploaded);
											item.getItemProperty("logo_empresa").setValue(readImageOldWay(file));
											item.getItemProperty("data_ateracao").setValue(new Date());
											item.getItemProperty("cnae_fiscal").setValue(cnaeSelecionado);
											fireEvent(new EmpresaSavedEvent(getUI(), item,true,cnaeSelecionado));
											gDialog.close();
											close();	
											Notify.Show("Empresa Alterada com Sucesso!", Notify.TYPE_SUCCESS);
										} catch (Exception e) {											
											e.printStackTrace();
											Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
										}
									}else{
										Notify.Show_Invalid_Submit_Form();
									}
								}else{
									fieldGroup.discard();
									item.getItemProperty("logo").setValue("");
									gDialog.close();
									close();
									fireEvent(new EmpresaSavedEvent(getUI(), item,false, cnaeSelecionado));
								}
							}
						});
						getUI().addWindow(gDialog);
						
					}else{
						close();
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
			
		public void addListener(EmpresaSavedListener listener){
			try {
				Method metodo = EmpresaSavedListener.class.getDeclaredMethod("empresaSaved", 
						new Class[]{EmpresaSavedEvent.class});
				addListener(EmpresaSavedEvent.class, listener, metodo);
			} catch (Exception e) {
				System.out.println("Erro: Método não encontrado! "+e);
			}
			
		}
		
		public void removeListener(EmpresaSavedListener listener){
			removeListener(EmpresaSavedEvent.class, listener);
		}
		
		public static class EmpresaSavedEvent extends Event{
	
			private static final long serialVersionUID = 1L;
	
			private Item eventoItem;
			private boolean confirm; 
			
			private Cnae cnae;
			
			public EmpresaSavedEvent(Component arg0, Item eventoItem, boolean confirm, Cnae cane) {
				super(arg0);
				this.eventoItem = eventoItem;
				this.confirm = confirm;
				this.cnae = cane;
				
			}
			public Item getEventoItem(){
				return eventoItem;
			}
			public boolean isConfirm(){
				return confirm;
			}
			public Cnae getCnae(){
				return cnae;
			}
		}
		
		public interface EmpresaSavedListener extends Serializable{
			public void empresaSaved(EmpresaSavedEvent event);
		}
		
		
		public class ImageUploader implements Receiver, SucceededListener{
			
			
			
			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				
				FileOutputStream fos = null;
				String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();	
				try {
					
					file = new File(basepath + "/WEB-INF/uploads/" + filename);
					fos = new FileOutputStream(file);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Notify.Show("O Arquivo não pode ser Aberto", Notify.TYPE_ERROR);
					return null;
				}
				
				return fos;
			}

			@Override
			public void uploadSucceeded(SucceededEvent event) {
				logo.setSource(new FileResource(file));
				nameImagemUploaded = event.getFilename();
			}

		}
		
		public byte[] readImageOldWay(File file) throws IOException
		{
		  //Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
		  if(file != null){
			  
			  InputStream is = new FileInputStream(file);
			  // Get the size of the file
			  long length = file.length();
			  // You cannot create an array using a long type.
			  // It needs to be an int type.
			  // Before converting to an int type, check
			  // to ensure that file is not larger than Integer.MAX_VALUE.
			  if (length > Integer.MAX_VALUE)
			  {
				  // File is too large
			  }
			  // Create the byte array to hold the data
			  byte[] bytes = new byte[(int) length];
			  // Read in the bytes
			  int offset = 0;
			  int numRead = 0;
			  while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
			  {
				  offset += numRead;
			  }
			  // Ensure all the bytes have been read in
			  if (offset < bytes.length)
			  {
				  throw new IOException("Could not completely read file " + file.getName());
			  }
			  // Close the input stream and return bytes
			  is.close();
			  return bytes;
		  }
		return null;
		}
		public void writeFile(File file, byte[] data) throws IOException
		 {
		   OutputStream fo = new FileOutputStream(file);
		   // Write the data
		   fo.write(data);
		   // flush the file (down the toilet)
		   fo.flush();
		   // Close the door to keep the smell in.
		   fo.close();
		 }
			
	}
