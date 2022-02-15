package com.digital.opuserp.view.modulos.configuracoes.config_estoque.icms;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.CstFormTributoDAO;
import com.digital.opuserp.dao.SimplesNacionalDAO;
import com.digital.opuserp.domain.CstFormaTributo;
import com.digital.opuserp.domain.SimplesNacional;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.CstFormTributoUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.SimplesNacionalUtil;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;
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

public class IcmsEditor extends Window implements GenericEditor {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TabSheet tbDados;
	
	TextField tfTotalValor;
	
	boolean valid_desconto = false;
	
	private CstFormaTributo tributoSelecionado;
	private TextField tfDescricaoTributo;
	private Button btSearchCst;
	private TextField tfCodTributo;	
	Button btSearchCSOSN;
	
	private SimplesNacional simplesNSelecionado;
	private TextField tfCodSimplesN;
	private TextField tfDescricaoSimplesN;

	public IcmsEditor(Item item, String title, boolean modal){
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

	
	}
	
	
	
	private void configLayout(){
		//1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("800px");
				setHeight("530px");		
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
						setHeight("515px");
						setWidth("459px");
						
						
						addTab(new VerticalLayout() {
							{
									{
										
										addStyleName("more-space");
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												
												TextField txtDescricao = (TextField)fieldGroup.buildAndBind("Descrição", "descricao");				
												txtDescricao.setWidth("263px");				
												txtDescricao.setStyleName("caption-align-icms");
												txtDescricao.setNullRepresentation("");
												txtDescricao.focus();
												txtDescricao.setRequired(true);
												txtDescricao.setMaxLength(200);
												
												addComponent(txtDescricao);
												setExpandRatio(txtDescricao, 2);		
												
											}
										});
										
//										addComponent(new FormLayout() {
//											{
//												
//												setMargin(true);
//												setSpacing(true);
//												setStyleName("form-cutom");
//												
//												TextField txtDescricao = (TextField)fieldGroup.buildAndBind("CST", "cst");				
//												txtDescricao.setWidth("263px");				
//												txtDescricao.setStyleName("caption-align-icms");
//												txtDescricao.setNullRepresentation("");
//												txtDescricao.focus();
//												txtDescricao.setRequired(true);
//												txtDescricao.setMaxLength(200);
//												
//												addComponent(txtDescricao);
//												setExpandRatio(txtDescricao, 2);		
//												
//											}
//										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												
												
												HorizontalLayout hl6 = new HorizontalLayout();
												hl6.setWidth("100%");

												hl6.addComponent(new HorizontalLayout(){
													{
																				
														tfCodTributo = new TextField("CST");				
														tfCodTributo.setWidth("60px");				
														tfCodTributo.setNullRepresentation("");
														tfCodTributo.setStyleName("caption-align-cst-icms");
														tfCodTributo.setId("tributo");
														if(tributoSelecionado != null){
															tfCodTributo.setValue(tributoSelecionado.getCst_tributo_cod());
														}
																																										
														JavaScript.getCurrent().execute("$('#tributo').mask('0000')");
														tfCodTributo.setImmediate(true);		
														
														tfCodTributo.addListener(new TextChangeListener() {
															
															@Override
															public void textChange(TextChangeEvent event) {
																
																tributoSelecionado = new CstFormaTributo();
																
																if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
																	String codTributo = event.getText();
																	tributoSelecionado = CstFormTributoDAO.findCod(codTributo);		
																	
																	if(tributoSelecionado != null){
																		tfDescricaoTributo.setReadOnly(false);
																		tfDescricaoTributo.setValue(tributoSelecionado.getReferencia());
																		tfDescricaoTributo.setReadOnly(true);
																		
																	}else{
																		tfDescricaoTributo.setReadOnly(false);
																		tfDescricaoTributo.setValue("");
																		tfDescricaoTributo.setReadOnly(true);
																		
																	}
																}else{
																	tfDescricaoTributo.setReadOnly(false);
																	tfDescricaoTributo.setValue("");
																	tfDescricaoTributo.setReadOnly(true);							
																}
															}
														});
														
//														tfCodTributo.setRequired(true);		
														tfDescricaoTributo = new TextField();
														tfDescricaoTributo.setTabIndex(2000);
														tfDescricaoTributo.setReadOnly(true);
														tfDescricaoTributo.setWidth("100px");
														
														if(item.getItemProperty("cst_forma_tributo") != null && item.getItemProperty("cst_forma_tributo").getValue() != null){
															tributoSelecionado = (CstFormaTributo)item.getItemProperty("cst_forma_tributo").getValue();				
														}
														
														btSearchCst = new Button();
														btSearchCst.setStyleName(BaseTheme.BUTTON_LINK);
														btSearchCst.setIcon(new ThemeResource("icons/search-16.png"));
														btSearchCst.setTabIndex(300000);
														
														btSearchCst.addClickListener(new Button.ClickListener() {
															
															@Override
															public void buttonClick(ClickEvent event) {
																CstFormTributoUtil cUtil = new CstFormTributoUtil(true, true);
																cUtil.addListerner(new CstFormTributoUtil.CstListerner() {
																	
																	@Override
																	public void onSelected(
																			CstFormTributoUtil.CstEvent event) {
																		if(event.getCstTributo() != null){
																			tfCodTributo.setValue(event.getCstTributo().getCst_tributo_cod());
																			tfDescricaoTributo.setReadOnly(false);
																			tfDescricaoTributo.setValue(event.getCstTributo().getReferencia());
																			tfDescricaoTributo.setReadOnly(true);
																			tributoSelecionado = event.getCstTributo();
																			
																		}
																	}							
																});
																
																getUI().addWindow(cUtil);
															}
														});
														
														
														addComponent(new FormLayout(){
															{
																
																setMargin(true);
																setSpacing(true);
																setStyleName("form-cutom");		
																
																addComponent(tfCodTributo);							
															}
														});

														
														addComponent(new FormLayout(){
															{
																
																setMargin(true);
																setSpacing(true);
																setStyleName("form-cutom_hide_error_cell");										
																addComponent(btSearchCst);							
															}
														}); 
	
//													if(tributoSelecionado != null && tfCodTributo != null){
//															tfCodTributo.setValue(tributoSelecionado.getCst_tributo_cod());
//													}

													}
												});
											
												
												tfDescricaoTributo = new TextField();
																							
												addComponent(hl6); 
											}
										});
										
										
																			
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												
												ComboBox cbModCalculo = new ComboBox("Mod. Cálculo");
												cbModCalculo.setTextInputAllowed(false);
												cbModCalculo.setNullSelectionAllowed(false);												
												cbModCalculo.setRequired(true);
												cbModCalculo.setWidth("130px");				
												cbModCalculo.setStyleName("caption-align-icms");
												
												cbModCalculo.addItem("Margem Valor Agregado");
												cbModCalculo.addItem("Pauta");
												cbModCalculo.addItem("Pauta");
												cbModCalculo.addItem("Preço Tabelado Máximo");
												cbModCalculo.addItem("Valor da Operação");
																								
												addComponent(cbModCalculo);
												fieldGroup.bind(cbModCalculo, "modalidade_base_calculo_icms");
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												
												TextField txtBaseCalculoICMS = (TextField)fieldGroup.buildAndBind("Base Cál. ICMS", "base_calculo_icms");				
												txtBaseCalculoICMS.setWidth("130px");				
												txtBaseCalculoICMS.setStyleName("caption-align-icms");
												txtBaseCalculoICMS.setNullRepresentation("");
												txtBaseCalculoICMS.setMaxLength(200);
												
												addComponent(txtBaseCalculoICMS);
												setExpandRatio(txtBaseCalculoICMS, 2);		
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												
												TextField txtAliquotaICMS = (TextField)fieldGroup.buildAndBind("Aliquota ICMS", "aliquota_icms");				
												txtAliquotaICMS.setWidth("130px");				
												txtAliquotaICMS.setStyleName("caption-align-icms");
												txtAliquotaICMS.setNullRepresentation("");
												txtAliquotaICMS.setMaxLength(200);
												
												addComponent(txtAliquotaICMS);
												setExpandRatio(txtAliquotaICMS, 2);		
												
											}
										});
										

										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																											
														ComboBox cbModCalculoIcmsSt = new ComboBox("Mod. Cálculo Icms ST");
														cbModCalculoIcmsSt.setTextInputAllowed(false);
														cbModCalculoIcmsSt.setNullSelectionAllowed(false);												
														cbModCalculoIcmsSt.setRequired(true);
														cbModCalculoIcmsSt.setWidth("130px");				
														cbModCalculoIcmsSt.setStyleName("caption-align-icms");
														
														cbModCalculoIcmsSt.addItem("Preço tabelado ou máximo sugerido");
														cbModCalculoIcmsSt.addItem("Lista Negativa");
														cbModCalculoIcmsSt.addItem("Lista Positiva");
														cbModCalculoIcmsSt.addItem("Lista Neutra");
														cbModCalculoIcmsSt.addItem("Margem Valor Agregado");
														cbModCalculoIcmsSt.addItem("Pauta");
																										
														addComponent(cbModCalculoIcmsSt);
														fieldGroup.bind(cbModCalculoIcmsSt, "modalidade_base_calculo_icms_st");
														
														
													}
												});											
												
											}
										});
										
										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																		
														TextField txtPercentual = (TextField)fieldGroup.buildAndBind("Percentual", "percentual_iva");				
														txtPercentual.setWidth("49px");				
														txtPercentual.setStyleName("caption-align-icms");
														txtPercentual.setNullRepresentation("");														
														txtPercentual.setMaxLength(200);
														
														addComponent(txtPercentual);
														setExpandRatio(txtPercentual, 2);
														
													}
												});											
												
											}
										});
										
										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																		
														TextField txtBaseCalculoIcmsSt = (TextField)fieldGroup.buildAndBind("Base Cál. Icms ST", "base_calculo_icms_st");				
														txtBaseCalculoIcmsSt.setWidth("130px");				
														txtBaseCalculoIcmsSt.setStyleName("caption-align-icms");
														txtBaseCalculoIcmsSt.setNullRepresentation("");														
														txtBaseCalculoIcmsSt.setMaxLength(200);
														
														addComponent(txtBaseCalculoIcmsSt);
														setExpandRatio(txtBaseCalculoIcmsSt, 2);
														
													}
												});											
												
											}
										});
										
										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																		
														TextField txtAliquotaIcms = (TextField)fieldGroup.buildAndBind("Aliquota Icms", "aliquota_icms_st");				
														txtAliquotaIcms.setWidth("130px");				
														txtAliquotaIcms.setStyleName("caption-align-icms");
														txtAliquotaIcms.setNullRepresentation("");														
														txtAliquotaIcms.setMaxLength(200);
														
														addComponent(txtAliquotaIcms);
														setExpandRatio(txtAliquotaIcms, 2);
														
													}
												});											
												
											}
										});
										
										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																																												
														ComboBox cbMotivoDesonIcms = new ComboBox("Motivo Deson. Icms");
														cbMotivoDesonIcms.setTextInputAllowed(false);
														cbMotivoDesonIcms.setNullSelectionAllowed(false);												
														cbMotivoDesonIcms.setRequired(true);
														cbMotivoDesonIcms.setWidth("130px");				
														cbMotivoDesonIcms.setStyleName("caption-align-icms");
														
														cbMotivoDesonIcms.addItem("Não desonerado");
														cbMotivoDesonIcms.addItem("Táxi");
														cbMotivoDesonIcms.addItem("Deficiente Físico");
														cbMotivoDesonIcms.addItem("Produtor Agropecuário");
														cbMotivoDesonIcms.addItem("Frotista/Locadora");
														cbMotivoDesonIcms.addItem("Diplomático/Consular");
														cbMotivoDesonIcms.addItem("Utilit./Motos da Amazônia/Áreas de Livre Comércio");
														cbMotivoDesonIcms.addItem("SUFRAMA");
														cbMotivoDesonIcms.addItem("Outros");
																										
														addComponent(cbMotivoDesonIcms);
														fieldGroup.bind(cbMotivoDesonIcms, "motivo_desoneracao_icms");
														
													}
												});											
												
											}
										});
										
										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																		
														TextField txtBaseCalculoPropria = (TextField)fieldGroup.buildAndBind("Base Cál. Própria", "base_calculo_propria");				
														txtBaseCalculoPropria.setWidth("130px");				
														txtBaseCalculoPropria.setStyleName("caption-align-icms");
														txtBaseCalculoPropria.setNullRepresentation("");														
														txtBaseCalculoPropria.setMaxLength(200);
														
														addComponent(txtBaseCalculoPropria);
														setExpandRatio(txtBaseCalculoPropria, 2);
														
													}
												});											
												
											}
										});
										
										
										HorizontalLayout hl7 = new HorizontalLayout();
										hl7.setWidth("100%");
										
										hl7.addComponent(new HorizontalLayout(){
												{

												tfCodSimplesN = new TextField("CSOSN");				
												tfCodSimplesN.setWidth("60px");				
												tfCodSimplesN.setNullRepresentation("");
												tfCodSimplesN.setStyleName("caption-align-csosn-icms");
												tfCodSimplesN.setId("simples");
												
												
												JavaScript.getCurrent().execute("$('#simples').mask('00000000000000')");
												tfCodSimplesN.setImmediate(true);		
												
												tfCodSimplesN.addListener(new TextChangeListener() {
													
													@Override
													public void textChange(TextChangeEvent event) {
														
														simplesNSelecionado = new SimplesNacional();
														
														if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
															Integer codSimples = Integer.parseInt(event.getText());
															simplesNSelecionado = SimplesNacionalDAO.findCod(codSimples);		
															
															if(simplesNSelecionado != null){
																tfDescricaoSimplesN.setReadOnly(false);
																tfDescricaoSimplesN.setValue(simplesNSelecionado.getReferencia());
																tfDescricaoSimplesN.setReadOnly(true);
																
															}else{
																tfDescricaoSimplesN.setReadOnly(false);
																tfDescricaoSimplesN.setValue("");
																tfDescricaoSimplesN.setReadOnly(true);
																
															}
														}else{
															tfDescricaoSimplesN.setReadOnly(false);
															tfDescricaoSimplesN.setValue("");
															tfDescricaoSimplesN.setReadOnly(true);							
														}
													}
												});
														
												tfDescricaoSimplesN = new TextField();
												tfDescricaoSimplesN.setTabIndex(2000);
												tfDescricaoSimplesN.setReadOnly(true);
												tfDescricaoSimplesN.setWidth("100px");
												
												if(item.getItemProperty("simples_nacional") != null && item.getItemProperty("simples_nacional").getValue() != null){
													simplesNSelecionado = (SimplesNacional)item.getItemProperty("simples_nacional").getValue();				
												}
												
												btSearchCSOSN = new Button();
												btSearchCSOSN.setStyleName(BaseTheme.BUTTON_LINK);
												btSearchCSOSN.setIcon(new ThemeResource("icons/search-16.png"));
												btSearchCSOSN.setTabIndex(300000);
												
												btSearchCSOSN.addClickListener(new Button.ClickListener() {
													
													@Override
													public void buttonClick(ClickEvent event) {
														SimplesNacionalUtil cUtil = new SimplesNacionalUtil(true, true);
														cUtil.addListerner(new SimplesNacionalUtil.CstListerner() {
															
															@Override
															public void onSelected(
																	SimplesNacionalUtil.CstEvent event) {
																if(event.getCstSimples() != null){
																	tfCodSimplesN.setValue(event.getCstSimples().getSimples_nacional_cod().toString());
																	tfDescricaoSimplesN.setReadOnly(false);
																	tfDescricaoSimplesN.setValue(event.getCstSimples().getReferencia());
																	tfDescricaoSimplesN.setReadOnly(true);
																	simplesNSelecionado = event.getCstSimples();
																	
																}
															}							
														});
														
														getUI().addWindow(cUtil);
													}
												});
												
												addComponent(new FormLayout(){
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");		
														
														addComponent(tfCodSimplesN);							
													}
												});

												addComponent(new FormLayout(){
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom_hide_error_cell");										
														addComponent(btSearchCSOSN);							
													}
												}); 
												
												if(simplesNSelecionado != null && tfCodSimplesN != null){
													tfCodSimplesN.setValue(simplesNSelecionado.getSimples_nacional_cod().toString());
												}

											}
										});	
										
										addComponent(hl7); 
									
										
										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																		
														TextField txtAliquotaSimplesNacional = (TextField)fieldGroup.buildAndBind("Aliq. Simp. Nacional", "aliquota_simples_nacional");				
														txtAliquotaSimplesNacional.setWidth("80px");				
														txtAliquotaSimplesNacional.setStyleName("caption-align-icms");
														txtAliquotaSimplesNacional.setNullRepresentation("");														
														txtAliquotaSimplesNacional.setMaxLength(200);
														
														addComponent(txtAliquotaSimplesNacional);
														setExpandRatio(txtAliquotaSimplesNacional, 2);
														
													}
												});											
												
											}
										});
										
										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																		
														TextField txtBaseCalculoIcmsRetido = (TextField)fieldGroup.buildAndBind("Base Cál. Icms Retido", "base_calculo_icms_retido");				
														txtBaseCalculoIcmsRetido.setWidth("80px");				
														txtBaseCalculoIcmsRetido.setStyleName("caption-align-icms");
														txtBaseCalculoIcmsRetido.setNullRepresentation("");														
														txtBaseCalculoIcmsRetido.setMaxLength(200);
														
														addComponent(txtBaseCalculoIcmsRetido);
														setExpandRatio(txtBaseCalculoIcmsRetido, 2);
														
													}
												});											
												
											}
										});
										
										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																		
														TextField txtAliquotaIcmsRetido = (TextField)fieldGroup.buildAndBind("Aliq. Icms Retido", "aliquota_icms_retido");				
														txtAliquotaIcmsRetido.setWidth("80px");				
														txtAliquotaIcmsRetido.setStyleName("caption-align-icms");
														txtAliquotaIcmsRetido.setNullRepresentation("");														
														txtAliquotaIcmsRetido.setMaxLength(200);
														
														addComponent(txtAliquotaIcmsRetido);
														setExpandRatio(txtAliquotaIcmsRetido, 2);
														
													}
												});											
												
											}
										});
										
										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																		
														TextField txtBaseCalculoIcmsDestino = (TextField)fieldGroup.buildAndBind("Base Cál. Icms Destino", "base_calculo_icms_destino");				
														txtBaseCalculoIcmsDestino.setWidth("80px");				
														txtBaseCalculoIcmsDestino.setStyleName("caption-align-icms");
														txtBaseCalculoIcmsDestino.setNullRepresentation("");														
														txtBaseCalculoIcmsDestino.setMaxLength(200);
														
														addComponent(txtBaseCalculoIcmsDestino);
														setExpandRatio(txtBaseCalculoIcmsDestino, 2);
														
													}
												});											
												
											}
										});
										
										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																		
														TextField txtAliquotaIcmsDestino = (TextField)fieldGroup.buildAndBind("Aliq. Icms Destino", "aliquota_icms_destino");				
														txtAliquotaIcmsDestino.setWidth("80px");				
														txtAliquotaIcmsDestino.setStyleName("caption-align-icms");
														txtAliquotaIcmsDestino.setNullRepresentation("");														
														txtAliquotaIcmsDestino.setMaxLength(200);
														
														addComponent(txtAliquotaIcmsDestino);
														setExpandRatio(txtAliquotaIcmsDestino, 2);
														
													}
												});											
												
											}
										});
																	
									}
								}														
							},"Principal");

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
				if(fieldGroup.isValid() && tributoSelecionado != null){
					try {
						fieldGroup.commit();			
						item.getItemProperty("cst").setValue(tributoSelecionado);
						item.getItemProperty("csosn").setValue(simplesNSelecionado);
						fireEvent(new IcmsEvent(getUI(), item, true));
						
					} catch (CommitException e) {
						Notify.Show("Não foi Possivel Salvar o Cofins!", Notify.TYPE_ERROR);
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
					fireEvent(new IcmsEvent(getUI(), item, false));
					
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
											fireEvent(new IcmsEvent(getUI(), item, true));
											
											Notify.Show("Cofins Salvo com Sucesso!", Notify.TYPE_SUCCESS);
											
											close();
										} catch (Exception e) {
											e.printStackTrace();
											Notify.Show("Não foi Possivel Salvar o Cofins!", Notify.TYPE_ERROR);
										}
									}else{
										Notify.Show_Invalid_Submit_Form();
									}
								}else{							
									fieldGroup.discard();				
									fireEvent(new IcmsEvent(getUI(), item, false));
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
	
	
	public void addListerner(IcmsListerner target){
		try {
			Method method = IcmsListerner.class.getDeclaredMethod("onClose", IcmsEvent.class);
			addListener(IcmsEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(IcmsListerner target){
		removeListener(IcmsEvent.class, target);
	}
	public static class IcmsEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public IcmsEvent(Component source, Item item, boolean confirm) {
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
	public interface IcmsListerner extends Serializable{
		public void onClose(IcmsEvent event);
	}
	
}
