package com.digital.opuserp.view.modulos.cadastro.clientes;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.vaadin.suggestfield.SuggestField;
import org.vaadin.suggestfield.SuggestField.SuggestionHandler;
import org.vaadin.suggestfield.client.SuggestFieldSuggestion;

import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.CepUtil;
import com.digital.opuserp.util.ChecarCidadeUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.MunicipiosUtil;
import com.digital.opuserp.view.util.MunicipiosUtil.MunicipioEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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

public class EnderecoEditor extends Window implements GenericEditor {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fgEndereco;
	VerticalLayout vlRoot;
	private String tipo;
	
	private String width_pais;
	private String width_referenc;
	private String width_cidade;
	private String width_categoria;
	private String width_numero;
	private String width_apelido;
	private String width_tratamento;
	private String width_complemento;
	private String width_localizacao;

	
	private boolean cepCadastrado;
	private boolean cepValido;
	String  cepAtual;
	
	
	private TextField txtCidade;
	private TextField txtUf;
	private TextField txtPais;
	
	private boolean askPrincipal;
	
	public EnderecoEditor(Item item, String title, boolean modal, boolean askPrincipal){
		
		String[] cidades_validas = new String[]{"ABREU E LIMA","AFOGADOS DA INGAZEIRA","AFRANIO","AGRESTINA","AGUA PRETA","AGUAS BELAS","ALAGOINHA","ALIANCA","ALTINHO","AMARAJI","ANGELIM","ARACOIABA","ARARIPINA","ARCOVERDE","BARRA DE GUABIRABA", "BARREIROS","BELEM DE MARIA","BELEM DE SAO FRANCISCO","BELO JARDIM","BETANIA","BEZERROS","BODOCO", "BOM CONSELHO","BOM JARDIM","BONITO","BREJAO","BREJINHO","BREJO DA MADRE DE DEUS","BUENOS AIRES", "BUIQUE","CABO DE SANTO AGOSTINHO","CABROBO","CACHOEIRINHA","CAETES","CALCADO","CALUMBI","CAMARAGIBE","CAMOCIM DE SAO FELIX","CAMUTANGA","CANHOTINHO","CAPOEIRAS","CARNAUBA","CARNAUBEIRA DA PENHA","CARPINA","CARUARU","CASINHAS","CATENDE","CEDRO","CHA DE ALEGRIA","CHA GRANDE","CONDADO","CORRENTES","CORTES","CUMARU","CUPIRA","CUSTODIA","DORMENTES","ESCADA","EXU","FEIRA NOVA","FERNANDO DE NORONHA","FERREIROS","FLORES","FLORESTA","FREI MIGUELINHO","GAMELEIRA","GARANHUNS","GLORIA DO GOITA","GOIANA","GRANITO", "GRAVATA","IATI","IBIMIRIM","IBIRAJUBA","IGARASSU","IGUARACI","INAJA","INGAZEIRA","IPOJUCA","IPUBI", "ITACURUBA","ITAIBA","ILHA DE ITAMARACA","ITAMBE","ITAPETIM","ITAPISSUMA","ITAQUITINGA", "JABOATAO DOS GUARARAPES","JAQUEIRA","JATAOBA","JATOBA","JOAO ALFREDO","JOAQUIM NABUCO", "JUCATI","JUPI","JUREMA","LAGOA DO CARRO","LAGOA DO ITAENGA","LAGOA DO OURO","LAGOA DOS GATOS", "LAGOA GRANDE","LAJEDO","LIMOEIRO","MACAPARANA","MACHADOS","MANARI","MARAIAL","MIRANDIBA","MORENO","NAZARE DA MATA","OLINDA","OROBO","OROCO","OURICURI","PALMARES","PALMEIRINA","PANELAS","PARANATAMA", "PARNAMIRIM","PASSIRA","PAUDALHO","PAULISTA","PEDRA","PESQUEIRA","PETROLANDIA","PETROLINA","POCAO", "POMBOS","PRIMAVERA","QUIPAPA","QUIXABA","RECIFE","RIACHO DAS ALMAS","RIBEIRAO","RIO FORMOSO", "SAIRE","SALGADINHO","SALGUEIRO","SALOE","SANHARO","SANTA CRUZ","SANTA CRUZ DA BAIXA VERDE","SANTA CRUZ DO CAPIBARIBE","SANTA FILOMENA","SANTA MARIA DA BOA VISTA","SANTA MARIA DO CAMBUCA",	"SANTA TEREZINHA","SAO BENEDITO DO SUL","SAO BENTO DO UNA","SAO CAITANO","SAO JOAO",	 "SAO JOAQUIM DO MONTE","SAO JOSE DA COROA GRANDE","SAO JOSE DO BELMONTE","SAO JOSE DO EGITO","SAO LOURENCO DA MATA","SAO VICENTE FERRER","SERRA TALHADA","SERRITA","SERTANIA","SIRINHAEM",	"MOREILANDIA","SOLIDAO","SURUBIM","TABIRA","TACAIMBO","TACARATU","TAMANDARE","TAQUARITINGA DO NORTE","TEREZINHA","TERRA NOVA","TIMBAUBA","TORITAMA","TRACUNHAEM","TRINDADE","TRIUNFO","TUPANATINGA",	"TUPARETAMA","VENTUROSA","VERDEJANTE","VERTENTE DO LORIO","VERTENTES","VICENCIA","VITORIA DE SANTO ANTAO","XEXEU"};
			
		if(cidades_validas.toString().contains("XEXEU")){
			System.out.println("Existe");
		}else{
			System.out.println("Não Existe");
		}
		
		getEnderecos();
		this.item = item;
		this.askPrincipal = askPrincipal;
		
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
		
		
		if(item.getItemProperty("id") != null && item.getItemProperty("id").getValue() != null && item.getItemProperty("cep") != null && item.getItemProperty("cep").getValue() != null){
			cepAtual = item.getItemProperty("cep").getValue().toString();

			cepCadastrado = true;
			cepValido = true;
		}else{
			cepCadastrado = false;
			cepValido = false;
			
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
	
	private void configLayout() {
		// 1366x768
		//if (OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768) {
			setWidth("950px");
			setHeight("275px");

			width_pais = "141px";
			width_complemento = "304px";
			width_localizacao = "600px";
			width_referenc = "314px";
			width_cidade = "140px";
			width_categoria = "200px";
			width_numero = "64px";
			width_apelido = "191px";
			width_tratamento = "100px";

		//}
	}
	
	public void buildLayout(){
		
		fgEndereco = new FieldGroup(item);
		
		vlRoot.addComponent(new HorizontalLayout() {
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
						((TextField) fgEndereco.getField("cep")).focus();
						((TextField) fgEndereco.getField("cep")).addListener(new FieldEvents.TextChangeListener() {
									
									
									@Override
									public void textChange(TextChangeEvent event) {
										
										final String cepEndereco = ((TextField) fgEndereco.getField("cep")).getValue();
										
										
										if (event.getText().length() == 8) {
											String cepPassado = event.getText();
											CepUtil utilCep = new CepUtil();
											if(utilCep.pegarCep(event.getText()) != null){
												updateCep(utilCep.pegarCep(event.getText()));											
											}else{
												com.digital.opuserp.view.util.CepUtil cep = new com.digital.opuserp.view.util.CepUtil(true, true);
												cep.addListerner(new com.digital.opuserp.view.util.CepUtil.CepListerner() {

													final String cepEndereco = ((TextField) fgEndereco.getField("cep")).getValue();
													
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
							
							((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
						}
					}
				});
				
				
				
				((TextField) fgEndereco.getField("endereco")).addListener(new FieldEvents.TextChangeListener() {

					@Override
					public void textChange(TextChangeEvent event) {
						
						final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
						

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

									}
								});
					}
				});
			}
		});

		// Complemento, Bairro,Cidade, UF, Pais

		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						final SuggestField search = new SuggestField();
						search.setImmediate(true);		
						search.setCaption("Bairro");
						search.setSuggestionHandler(new SuggestionHandler() {
							@Override
							public List<Object> searchItems(String query) {
								
								return handleSearchQuery(query);
							}
						});				
						

						
		
						fgEndereco.bind(search, "bairro");				
						search.setWidth("200px");				
		
						if(item.getItemProperty("bairro").getValue() != null){
							search.setValue(item.getItemProperty("bairro").getValue().toString());
						}
						
						search.setStyleName("caption-align");
						search.setRequired(true);

						addComponent(search);
						setExpandRatio(search, 2);
					}
				});


				addComponent(new HorizontalLayout(){
					{
											
										
						txtCidade = (TextField)fgEndereco.buildAndBind("Cidade", "cidade");				
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
										
						txtUf = (TextField)fgEndereco.buildAndBind("UF", "uf");				
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
										
						txtPais = (TextField)fgEndereco.buildAndBind("Pais", "pais");				
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
						addComponent(fgEndereco.buildAndBind("Complemento","complemento"));
						((TextField) fgEndereco.getField("complemento")).setNullRepresentation("");
						((TextField) fgEndereco.getField("complemento")).setStyleName("caption-align");
						((TextField) fgEndereco.getField("complemento")).setWidth(width_complemento);
						((TextField) fgEndereco.getField("complemento")).setImmediate(true);
						((TextField) fgEndereco.getField("complemento")).setTextChangeEventMode(TextChangeEventMode.LAZY);
						((TextField) fgEndereco.getField("complemento")).setRequired(true);
						((TextField) fgEndereco.getField("complemento")).setDescription("Casa, Apto, Galpão, Anexo, etc");
						((TextField) fgEndereco.getField("complemento")).addBlurListener(new FieldEvents.BlurListener() {
							
							@Override
							public void blur(BlurEvent event) {
								
								if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
									
									((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
								}

							}
						});
						
						((TextField) fgEndereco.getField("complemento")).addListener(new FieldEvents.TextChangeListener() {

							@Override
							public void textChange(TextChangeEvent event) {
								
								final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
								
							}
						});
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						addComponent(fgEndereco.buildAndBind("Referência","referencia"));
						((TextField) fgEndereco.getField("referencia")).setNullRepresentation("");
						((TextField) fgEndereco.getField("referencia")).setWidth(width_referenc);
						((TextField) fgEndereco.getField("referencia")).setImmediate(true);
						((TextField) fgEndereco.getField("referencia")).setTextChangeEventMode(TextChangeEventMode.LAZY);
						((TextField) fgEndereco.getField("referencia")).addBlurListener(new FieldEvents.BlurListener() {
							
							@Override
							public void blur(BlurEvent event) {
								if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
									
									((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
								}
							}
						});
						
						((TextField) fgEndereco.getField("referencia")).addListener(new FieldEvents.TextChangeListener() {

							@Override
							public void textChange(TextChangeEvent event) {
								
								final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
								
							}
						});
						
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
						addComponent(fgEndereco.buildAndBind("Localização","localizacao"));
						((TextField) fgEndereco.getField("localizacao")).setNullRepresentation("");
						((TextField) fgEndereco.getField("localizacao")).setStyleName("caption-align");
						((TextField) fgEndereco.getField("localizacao")).setWidth(width_localizacao);
						((TextField) fgEndereco.getField("localizacao")).setImmediate(true);
						((TextField) fgEndereco.getField("localizacao")).setTextChangeEventMode(TextChangeEventMode.LAZY);
						((TextField) fgEndereco.getField("localizacao")).setRequired(true);
						((TextField) fgEndereco.getField("localizacao")).setDescription("Link de localização");
//						((TextField) fgEndereco.getField("localizacao")).addBlurListener(new FieldEvents.BlurListener() {
//							
//							@Override
//							public void blur(BlurEvent event) {
//								
//								if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
//									
//									((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
//								}
//
//							}
//						});
						
//						BrowserWindowOpener openIpPool = new BrowserWindowOpener(((TextField) fgEndereco.getField("localizacao")).getValue());
//						openIpPool.setFeatures("height=600,width=800");
//						openIpPool.extend(((TextField) fgEndereco.getField("localizacao")));
						
//						((TextField) fgEndereco.getField("localizacao")).addListener(new FieldEvents.TextChangeListener() {
//
//							@Override
//							public void textChange(TextChangeEvent event) {
//								
//								final String endereco = ((TextField) fgEndereco.getField("cep")).getValue();
//								
//							}
//						});
					}
				});				
			}
		});

		
		
		
		
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
			((SuggestField) fgEndereco.getField("bairro")).setValue(cep.getBairro().toUpperCase());
			((TextField) fgEndereco.getField("cidade")).setReadOnly(false);
			((TextField) fgEndereco.getField("cidade")).setValue(cep.getCidade().toUpperCase());
			((TextField) fgEndereco.getField("cidade")).setReadOnly(true);
			((TextField) fgEndereco.getField("uf")).setReadOnly(false);
			((TextField) fgEndereco.getField("uf")).setValue(cep.getUf().toUpperCase());
			((TextField) fgEndereco.getField("uf")).setReadOnly(true);
			((TextField) fgEndereco.getField("pais")).setReadOnly(false);
			((TextField) fgEndereco.getField("pais")).setValue(cep.getPais().toUpperCase());
			((TextField) fgEndereco.getField("pais")).setReadOnly(true);
			((TextField) fgEndereco.getField("numero")).focus();
		}
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				//boolean cidade_valida = ChecarCidadeUtil.validar(item.getItemProperty("cidade").getValue().toString());
				
				if(fgEndereco.isValid() ){
					
					
					if(item.getItemProperty("id").getValue() == null){
						
						if(askPrincipal){
								GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja Definir o Endereço Como Principal ?", true, true);
								gd.addListerner(new GenericDialog.DialogListerner() {
									
									@Override
									public void onClose(DialogEvent event) {
										if(event.isConfirm()){
											item.getItemProperty("principal").setValue(true);
										}else{
											item.getItemProperty("principal").setValue(false);
										}
										
										try {
											fgEndereco.commit();				
											fireEvent(new CepEvent(getUI(), item, true));							
											Notify.Show("Endereço adicionado com Sucesso!", Notify.TYPE_SUCCESS);
										
										} catch (CommitException e) {
											e.printStackTrace();											
											Notify.Show("Problema ao adicionar Endereço", Notify.TYPE_ERROR);
										}
										
										close();
									}
								});
								getUI().addWindow(gd);
						}else{
							try{
								item.getItemProperty("principal").setValue(true);
								fgEndereco.commit();				
								fireEvent(new CepEvent(getUI(), item, true));							
								Notify.Show("Endereço adicionado com Sucesso!", Notify.TYPE_SUCCESS);
								
								close();
							}catch(Exception e){
								e.printStackTrace();
								Notify.Show("Problema ao adicionar Endereço", Notify.TYPE_ERROR);
							}
						}
						
						
						
					}else{
						
						try {
							fgEndereco.commit();				
							fireEvent(new CepEvent(getUI(), item, true));						
							Notify.Show("Endereço Gravado com Sucesso!", Notify.TYPE_SUCCESS);
							close();
						}catch(CommitException e){
							e.printStackTrace();
							Notify.Show("Problema ao adicionar Endereço", Notify.TYPE_ERROR);
						}
					}
						
				}else{

//					if(!cidade_valida){
//						Notify.Show("A Cidade informada não é válida!", Notify.TYPE_ERROR);
//					}

					if(txtCidade.getValue() == null || txtUf.getValue() == null || txtPais.getValue() == null){
						Styles styles = Page.getCurrent().getStyles();					        
						styles.add(".dashboard input.v-textfield-readonly { background-color: #E97953; }");						
					}
					
					
					for (Field<?> field: fgEndereco.getFields()) {
						
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
				
				if(!fgEndereco.isModified()){
					fgEndereco.discard();				
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
								
							  if(fgEndereco.isValid()){	
								try {
									fgEndereco.commit();				
									fireEvent(new CepEvent(getUI(), item, true));									
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
								fgEndereco.discard();				
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
