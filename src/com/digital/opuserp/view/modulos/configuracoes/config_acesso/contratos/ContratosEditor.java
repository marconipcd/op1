package com.digital.opuserp.view.modulos.configuracoes.config_acesso.contratos;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.vaadin.openesignforms.ckeditor.CKEditorConfig;
import org.vaadin.openesignforms.ckeditor.CKEditorTextField;

import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


	
public class ContratosEditor extends Window implements GenericEditor {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TabSheet tbDados;
	
	TextField tfTotalValor;
	
	boolean valid_desconto = false;

	public ContratosEditor(Item item, String title, boolean modal){
		this.item = item;
		
		
		//configLayout();	
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		//vlRoot.setMargin(true);
		//vlRoot.setStyleName("border-form");
		
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				addComponent(vlRoot);
				
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		buildTabSheet();
//		buildLayout();	

		if (item.getItemProperty("valor_desconto").getValue() != null && !item.getItemProperty("valor_desconto").getValue().equals("")) {
			valid_desconto = true;
		} 
	}
	
	
	
	private void configLayout(){
		//1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("800px");
				setHeight("630px");		
		//}
	}
	
	
	public void buildTabSheet(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");
				setStyleName("horizontal-tabsheet");
				addComponent(new TabSheet() {
					{
						setStyleName("custom-form_tabsheet");						
						setHeight("617px");
						setWidth("800px");
						
						
						addTab(new VerticalLayout() {
							{
									{
										
										addStyleName("more-space");
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												
												TextField tfCod = (TextField)fieldGroup.buildAndBind("Nome", "nome");				
												tfCod.setWidth("263px");				
												tfCod.setStyleName("caption-align-contrato-plano");
												tfCod.setNullRepresentation("");
												tfCod.focus();
												tfCod.setRequired(true);
												tfCod.setMaxLength(200);
												
												addComponent(tfCod);
												setExpandRatio(tfCod, 2);		
												
											}
										});
										
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");
												
												TextField tfCod = (TextField)fieldGroup.buildAndBind("Ato de Autorização ANATEL Nº", "ato_autorizacao");				
												tfCod.setWidth("130px");				
												tfCod.setStyleName("caption-align-contrato-plano");
												tfCod.setNullRepresentation("");
												tfCod.setMaxLength(200);
												
												addComponent(tfCod);
												setExpandRatio(tfCod, 2);		
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												
												TextField tfCod = (TextField)fieldGroup.buildAndBind("Termo de Autorização ANATEL Nº", "termo_autorizacao");				
												tfCod.setWidth("130px");				
												tfCod.setStyleName("caption-align-contrato-plano");
												tfCod.setNullRepresentation("");
												tfCod.setMaxLength(200);
												
												addComponent(tfCod);
												setExpandRatio(tfCod, 2);		
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");
												
												TextField tfCod = (TextField)fieldGroup.buildAndBind("Registro Cartório Contrato SCM Nº", "reg_cartorio_scm");				
												tfCod.setWidth("130px");				
												tfCod.setStyleName("caption-align-contrato-plano");
												tfCod.setNullRepresentation("");
												tfCod.setMaxLength(200);
												
												addComponent(tfCod);
												setExpandRatio(tfCod, 2);		
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												
												TextField tfCod = (TextField)fieldGroup.buildAndBind("Registro Cartório Contrato Comodato Nº", "reg_cartorio");				
												tfCod.setWidth("130px");				
												tfCod.setStyleName("caption-align-contrato-plano");
												tfCod.setNullRepresentation("");
												tfCod.setMaxLength(200);
												
												addComponent(tfCod);
												setExpandRatio(tfCod, 2);		
												
											}
										});
										

										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom-new");						
																		
														TextField tfCod = (TextField)fieldGroup.buildAndBind("Vigência (meses)", "vigencia");				
														tfCod.setWidth("49px");				
														tfCod.setStyleName("caption-align-contrato-plano");
														tfCod.setNullRepresentation("");
														tfCod.setRequired(true);
														tfCod.setMaxLength(200);
														
														addComponent(tfCod);
														setExpandRatio(tfCod, 2);
														
													}
												});
												
												
												addComponent(new FormLayout() {
													{
													
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom-new");													
														ComboBox cb = new ComboBox("Carência");
														cb.setNullSelectionAllowed(false);
//														cb.setWidth("80px");
														cb.addItem("SIM");
														cb.addItem("NAO");																
														//cb.setStyleName("caption-align-contrato");
														cb.setRequired(true);
														
														addComponent(cb);
														fieldGroup.bind(cb, "carencia");												
														
													}
												});			
												
												
											}
										});
										
																														
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");						
																
												ComboBox cb = new ComboBox("Sistema de Cobrança");					
												cb.setNullSelectionAllowed(false);
												cb.addItem("POS-PAGO");
												cb.addItem("PRE-PAGO");
												cb.addItem("GRATIS");
												
												cb.setStyleName("caption-align-contrato-plano");
												cb.setRequired(true);										
												addComponent(cb);
												fieldGroup.bind(cb, "tipo_contrato");
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");						
																
												TextField tfCod = (TextField)fieldGroup.buildAndBind("Tolerância Bloqueio Parcial (dias)", "bloqueio");				
												tfCod.setWidth("40px");				
												tfCod.setStyleName("caption-align-contrato-plano");
												tfCod.setNullRepresentation("");
												tfCod.setRequired(true);
												tfCod.setMaxLength(200);
												
												addComponent(tfCod);
												
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");						
																
												TextField txtBloqueioTotal = (TextField)fieldGroup.buildAndBind("Tolerância Bloqueio Total (dias)", "bloqueio_total");				
												txtBloqueioTotal.setWidth("40px");				
												txtBloqueioTotal.setStyleName("caption-align-contrato-plano");
												txtBloqueioTotal.setNullRepresentation("");
												txtBloqueioTotal.setRequired(true);
												txtBloqueioTotal.setMaxLength(200);
												
												addComponent(txtBloqueioTotal);
												
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");						
																
												ComboBox cbClasseConsumo = new ComboBox("Classe de Consumo");				
												cbClasseConsumo.setStyleName("caption-align-contrato-plano");
												cbClasseConsumo.setRequired(true);
												cbClasseConsumo.setNullSelectionAllowed(false);
												cbClasseConsumo.addItem("Comercial");
												cbClasseConsumo.addItem("Residencial");
												cbClasseConsumo.addItem("Poder Público");
												cbClasseConsumo.addItem("Grande Cliente");
												
												fieldGroup.bind(cbClasseConsumo, "classe_consumo");
												
												addComponent(cbClasseConsumo);												
												
											}
										});
										
										
										
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");						
																
												TextField tfDesconto = new TextField("Desconto (R$)");	
												tfDesconto.setWidth("80px");				
												tfDesconto.setStyleName("caption-align-contrato-plano");
												//tfCod.setNullRepresentation("");
												tfDesconto.setRequired(true);
												tfDesconto.setMaxLength(200);
												tfDesconto.setId("txtDesconto");
												tfDesconto.setNullRepresentation("");												
												tfDesconto.setTextChangeEventMode(TextChangeEventMode.EAGER);
												tfDesconto.addStyleName("align-currency");
												tfDesconto.addTextChangeListener(new FieldEvents.TextChangeListener() {
													
													@Override
													public void textChange(TextChangeEvent event) {
														if(event.getText() != null && !event.getText().isEmpty() && !event.getText().equals("") && 
																fieldGroup.getField("valor_adesao") != null && !((TextField) (fieldGroup.getField("valor_adesao"))).getValue().isEmpty() &&
																!((TextField) (fieldGroup.getField("valor_adesao"))).getValue().equals("")){													
															
															
															
															Double valorAdesao = Double.parseDouble(Real.formatStringToDB(((TextField) (fieldGroup.getField("valor_adesao"))).getValue()));
															Double valorDesconto = Double.parseDouble(Real.formatStringToDB(event.getText()));
															
															if(valorDesconto > valorAdesao){
																
																
																event.getComponent().addStyleName("invalid-txt");
																valid_desconto = false;
															}else{
																
																Double total = valorAdesao-Double.parseDouble(Real.formatStringToDB(event.getText()));
																tfTotalValor.setReadOnly(false);															
																tfTotalValor.setValue(Real.formatDbToString(total.toString()));															
																tfTotalValor.setReadOnly(true);	
																
																event.getComponent().removeStyleName("invalid-txt");
																valid_desconto = true;
															}
															
														}
													}
												});
												
												addComponent(tfDesconto);												
												fieldGroup.bind(tfDesconto, "valor_desconto");
												JavaScript.getCurrent().execute("$('#txtDesconto').maskMoney({decimal:',',thousands:'.'})");
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");						
																
												TextField txtBeneficioAdesão = new TextField("Benefício de Adesão R$");	
												txtBeneficioAdesão.setWidth("80px");				
												txtBeneficioAdesão.setStyleName("caption-align-contrato-plano");
												txtBeneficioAdesão.setNullRepresentation("");
												txtBeneficioAdesão.setRequired(true);
												txtBeneficioAdesão.setMaxLength(200);
												//tfCod.addStyleName("moneyMaskBR");
												txtBeneficioAdesão.setId("txtAdesao");
												txtBeneficioAdesão.addStyleName("align-currency");
												txtBeneficioAdesão.addTextChangeListener(new FieldEvents.TextChangeListener() {
													
													@Override
													public void textChange(TextChangeEvent event) {
														if(event.getText() != null && !event.getText().isEmpty()){
															//1.500,00
															
															Float total = null;																	
															if(fieldGroup.getField("valor_desconto").getValue() != null){
																
																	total =	Float.parseFloat(event.getText().replace(".", "").replace(",", "."))-
																		Float.parseFloat(fieldGroup.getField("valor_desconto").getValue().toString().replace(".", "").replace(",", "."));
																
																	tfTotalValor.setReadOnly(false);																		
																	tfTotalValor.setValue(Real.formatDbToString(total.toString()));
																	
																	tfTotalValor.setReadOnly(true);
															}
														}
													}
												});
												
												addComponent(txtBeneficioAdesão);												
												fieldGroup.bind(txtBeneficioAdesão, "valor_adesao");
												
												JavaScript.getCurrent().execute("$('#txtAdesao').maskMoney({decimal:',',thousands:'.'})");
												
												
											}
										});
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");						
																
												TextField txtBeneficioComodato = new TextField("Benefício de Comodato (R$)");	
												txtBeneficioComodato.setWidth("80px");				
												txtBeneficioComodato.setStyleName("caption-align-contrato-plano");
												//tfCod.setNullRepresentation("");
												txtBeneficioComodato.setRequired(true);
												txtBeneficioComodato.setMaxLength(200);
												txtBeneficioComodato.setId("txtBeneficioComodato");
												txtBeneficioComodato.setNullRepresentation("");												
												txtBeneficioComodato.setTextChangeEventMode(TextChangeEventMode.EAGER);
												txtBeneficioComodato.addStyleName("align-currency");
																							
												addComponent(txtBeneficioComodato);												
												fieldGroup.bind(txtBeneficioComodato, "valor_equipamento");
												JavaScript.getCurrent().execute("$('#txtBeneficioComodato').maskMoney({decimal:',',thousands:'.'})");
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");						
																
												TextField txtIsencaoTaxaInstalacao = new TextField("Isenção Taxa Instalação (R$)");	
												txtIsencaoTaxaInstalacao.setWidth("80px");				
												txtIsencaoTaxaInstalacao.setStyleName("caption-align-contrato-plano");
												//tfCod.setNullRepresentation("");
												txtIsencaoTaxaInstalacao.setRequired(true);
												txtIsencaoTaxaInstalacao.setMaxLength(200);
												txtIsencaoTaxaInstalacao.setId("txtIsencaoTaxaInstalacao");
												txtIsencaoTaxaInstalacao.setNullRepresentation("");												
												txtIsencaoTaxaInstalacao.setTextChangeEventMode(TextChangeEventMode.EAGER);
												txtIsencaoTaxaInstalacao.addStyleName("align-currency");
																							
												addComponent(txtIsencaoTaxaInstalacao);												
												fieldGroup.bind(txtIsencaoTaxaInstalacao, "isencao_taxa_instalacao");
												JavaScript.getCurrent().execute("$('#txtIsencaoTaxaInstalacao').maskMoney({decimal:',',thousands:'.'})");
												
											}
										});
										
										addComponent(new FormLayout() {
											{												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");						
																
												TextField txtIsencaoPrestServManuten = new TextField("Isenção Manutenção de Serviço (R$)");	
												txtIsencaoPrestServManuten.setWidth("80px");				
												txtIsencaoPrestServManuten.setStyleName("caption-align-contrato-plano");
												txtIsencaoPrestServManuten.setRequired(true);
												txtIsencaoPrestServManuten.setMaxLength(200);
												txtIsencaoPrestServManuten.setId("txtIsencaoPrestServManuten");
												txtIsencaoPrestServManuten.setNullRepresentation("");												
												txtIsencaoPrestServManuten.setTextChangeEventMode(TextChangeEventMode.EAGER);
												txtIsencaoPrestServManuten.addStyleName("align-currency");
																							
												addComponent(txtIsencaoPrestServManuten);												
												fieldGroup.bind(txtIsencaoPrestServManuten, "isencao_prest_serv_manutencao");
												JavaScript.getCurrent().execute("$('#txtIsencaoPrestServManuten').maskMoney({decimal:',',thousands:'.'})");
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");						
																
												tfTotalValor = new TextField("Total (R$)");	
												tfTotalValor.setWidth("80px");				
												tfTotalValor.setStyleName("caption-align-contrato-total");
												tfTotalValor.setNullRepresentation("");
												tfTotalValor.setRequired(true);
												tfTotalValor.setMaxLength(200);
												tfTotalValor.setReadOnly(true);
												tfTotalValor.setId("txtTotal");
												tfTotalValor.addStyleName("align-currency");
												
												if(fieldGroup.getField("valor_adesao").getValue() != null && fieldGroup.getField("valor_desconto").getValue() != null){
													Float total = Float.parseFloat(fieldGroup.getField("valor_adesao").getValue().toString().replace(".", "").replace(",", "."))-
															Float.parseFloat(fieldGroup.getField("valor_desconto").getValue().toString().replace(".", "").replace(",", "."));
													
													tfTotalValor.setReadOnly(false);		
//													
//													Double ValorTotal = Double.parseDouble(total.toString());															  
//													DecimalFormat df = new DecimalFormat();  
//													df.applyPattern(" #,##0.00");  											
//													String valorFormatado = df.format(ValorTotal);
													tfTotalValor.setValue(Real.formatDbToString(total.toString()));
													
													tfTotalValor.setReadOnly(true);
								
												}
												
												addComponent(tfTotalValor);
												
												JavaScript.getCurrent().execute("$('#txtTotal').maskMoney({decimal:',',thousands:'.'})");
												
											}
										});
										
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");						
												
												ComboBox cbCategoriaContrato = new ComboBox("Categoria Contrato");				
												cbCategoriaContrato.setStyleName("caption-align-contrato-plano");
												cbCategoriaContrato.setRequired(true);
												cbCategoriaContrato.setNullSelectionAllowed(false);
												cbCategoriaContrato.addItem("DOMESTICO");
												cbCategoriaContrato.addItem("COMERCIAL");
												cbCategoriaContrato.addItem("CORPORATIVO");
																								
												fieldGroup.bind(cbCategoriaContrato, "categoria");
												
												addComponent(cbCategoriaContrato);												
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom-new");						
																
												ComboBox cb = new ComboBox("Status");					
												cb.setNullSelectionAllowed(false);
												cb.addItem("ATIVO");
												cb.addItem("INATIVO");
//												cb.setWidth("80px");
												cb.setStyleName("caption-align-contrato-plano");
												cb.setRequired(true);										
												addComponent(cb);
												fieldGroup.bind(cb, "status");
												
											}
										});
										

																			
									}
								}														
							},"Principal");
						
						
						
						
//						addTab(new VerticalLayout() {
//							{
								
//								addComponent(new FormLayout() {
//									{
//										Label clausulas = new Label("Título:");
//										setStyleName("caption-align-contrato-label");	
//										addComponent(clausulas);
//									}
//								});
								
								
//								setMargin(false);
//								setSpacing(false);
//								setStyleName("form-cutom");						
//											
//								
//								CKEditorConfig config = new CKEditorConfig();
//				                config.useCompactTags();
//				                config.disableElementsPath();
//				                config.setResizeDir(CKEditorConfig.RESIZE_DIR.HORIZONTAL);
//				                config.disableSpellChecker();
//				                config.setToolbarCanCollapse(false);
//				                config.setWidth("740px");
//				                config.setHeight("100%");
//				                config.addToRemovePlugins("insert");
//				                config.addToRemovePlugins("forms");
//				                config.addToRemovePlugins("links");
//				                
//				                
//				                CKEditorTextField ckEditorTitulo = new CKEditorTextField(config);
//				                ckEditorTitulo.setStyleName("caption-align-contrato-new");
//				                ckEditorTitulo.setHeight("400px");
//								
//								
//								addComponent(ckEditorTitulo);	
//										
//								try {						
//									fieldGroup.bind(ckEditorTitulo, "titulo");
//								} catch (Exception e) {
//									e.printStackTrace();
//								}									
//										
//							}
//						},"Título");
//						
//						
//						addTab(new VerticalLayout() {
//							{														
//								
//								
//								
//								setMargin(false);
//								setSpacing(false);
//								setStyleName("form-cutom");						
//													
//								CKEditorConfig config = new CKEditorConfig();
//						        config.useCompactTags();
//						        config.disableElementsPath();
//						        config.setResizeDir(CKEditorConfig.RESIZE_DIR.HORIZONTAL);
//						        config.disableSpellChecker();
//						        config.setToolbarCanCollapse(false);
//						        config.setWidth("740px");
//						        config.setHeight("100%");
//
//						                
//						        CKEditorTextField ckEditorClausulas = new CKEditorTextField(config);
//						        ckEditorClausulas.setStyleName("caption-align-contrato-new");
//						        ckEditorClausulas.setHeight("400px");
//						               
//						                										
//								addComponent(ckEditorClausulas);	
//								
//								try {						
//									fieldGroup.bind(ckEditorClausulas, "clausulas");
//								} catch (Exception e) {
//									e.printStackTrace();
//								}									
//								
//							}
//						},"Cláusulas");
//
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
				if(fieldGroup.isValid() && valid_desconto){
					try {
						fieldGroup.commit();				
						fireEvent(new ContratoEvent(getUI(), item, true));
						
					} catch (CommitException e) {
						Notify.Show("Não foi Possivel Salvar o Contrato!", Notify.TYPE_ERROR);
						e.printStackTrace();
					}
					close();
				}else{
					
					for (Field<?> field: fieldGroup.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
					}
					
					//Verifica se Desconto é Válido
					if(!valid_desconto){
						fieldGroup.getField("valor_desconto").addStyleName("invalid-txt");
					}else{
						fieldGroup.getField("valor_desconto").removeStyleName("invalid-txt");
					}
					
					Notify.Show_Invalid_Submit_Form();
				}
				
			}
		});
		
		btSalvar.setStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("Salvar", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new ContratoEvent(getUI(), item, false));
					
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
											fireEvent(new ContratoEvent(getUI(), item, true));
											
											Notify.Show("Contrato Salvo com Sucesso!", Notify.TYPE_SUCCESS);
											
											close();
										} catch (Exception e) {
											e.printStackTrace();
											Notify.Show("Não foi Possivel Salvar o Contrato!", Notify.TYPE_ERROR);
										}
									}else{
										Notify.Show_Invalid_Submit_Form();
									}
								}else{							
									fieldGroup.discard();				
									fireEvent(new ContratoEvent(getUI(), item, false));
									close();													
								}
							
						}
					});					
					
					getUI().addWindow(gDialog);
					
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
	
	
	public void addListerner(ContratoListerner target){
		try {
			Method method = ContratoListerner.class.getDeclaredMethod("onClose", ContratoEvent.class);
			addListener(ContratoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ContratoListerner target){
		removeListener(ContratoEvent.class, target);
	}
	public static class ContratoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public ContratoEvent(Component source, Item item, boolean confirm) {
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
	public interface ContratoListerner extends Serializable{
		public void onClose(ContratoEvent event);
	}
	
}
