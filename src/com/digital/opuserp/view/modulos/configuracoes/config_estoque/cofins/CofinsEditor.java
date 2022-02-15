package com.digital.opuserp.view.modulos.configuracoes.config_estoque.cofins;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.CstFormTributoDAO;
import com.digital.opuserp.domain.CstFormaTributo;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.CstFormTributoUtil;
import com.digital.opuserp.view.util.Notify;
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

public class CofinsEditor extends Window implements GenericEditor {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TabSheet tbDados;
	
	TextField tfTotalValor;
	
	private CstFormaTributo tributoSelecionado;
	private TextField tfDescricaoTributo;
	private Button btSearchCst;
	private TextField tfCodTributo;	
	
	boolean valid_desconto = false;

	public CofinsEditor(Item item, String title, boolean modal){
		this.item = item;
		
		if(item.getItemProperty("cst").getValue() != null){
			tributoSelecionado = ((CstFormaTributo)item.getItemProperty("cst").getValue());			
		}
		
		
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
						setHeight("244px");
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
												txtDescricao.setStyleName("caption-align");
												txtDescricao.setNullRepresentation("");
												txtDescricao.focus();
												txtDescricao.setRequired(true);
												txtDescricao.setMaxLength(200);
												
												addComponent(txtDescricao);
												setExpandRatio(txtDescricao, 2);		
												
											}
										});
										
										
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
														tfCodTributo.setStyleName("caption-align-cst");
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
												
												ComboBox cbTipo = new ComboBox("Tipo");				
												cbTipo.setWidth("130px");				
												cbTipo.setStyleName("caption-align");
												cbTipo.setNullSelectionAllowed(false);
												cbTipo.setTextInputAllowed(false);
												cbTipo.addItem("Percentual");
												cbTipo.addItem("Valor");
												cbTipo.setRequired(true);
												
												addComponent(cbTipo);
												fieldGroup.bind(cbTipo, "tipo");
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												
												TextField txtBaseCalculo = (TextField)fieldGroup.buildAndBind("Base Cálculo", "base_calculo");				
												txtBaseCalculo.setWidth("60px");				
												txtBaseCalculo.setStyleName("caption-align");
												txtBaseCalculo.setNullRepresentation("");
												txtBaseCalculo.setMaxLength(200);
												
												addComponent(txtBaseCalculo);
												setExpandRatio(txtBaseCalculo, 2);		
												
											}
										});
										
										addComponent(new FormLayout() {
											{
												
												setMargin(true);
												setSpacing(true);
												setStyleName("form-cutom");
												
												TextField txtAliquota = (TextField)fieldGroup.buildAndBind("Aliquota", "aliquota");				
												txtAliquota.setWidth("60px");				
												txtAliquota.setStyleName("caption-align");
												txtAliquota.setNullRepresentation("");
												txtAliquota.setMaxLength(200);
												
												addComponent(txtAliquota);
												setExpandRatio(txtAliquota, 2);		
												
											}
										});
										

										addComponent(new HorizontalLayout(){
											{
												addComponent(new FormLayout() {
													{
														
														setMargin(true);
														setSpacing(true);
														setStyleName("form-cutom");						
																		
														TextField txtValorCofins = (TextField)fieldGroup.buildAndBind("Valor Cofins", "valor_cofins");				
														txtValorCofins.setWidth("60px");				
														txtValorCofins.setStyleName("caption-align");
														txtValorCofins.setNullRepresentation("");
														txtValorCofins.setRequired(true);
														txtValorCofins.setMaxLength(200);
														
														addComponent(txtValorCofins);
														setExpandRatio(txtValorCofins, 2);
														
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
						fireEvent(new CofinsEvent(getUI(), item, true));
						
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
					fireEvent(new CofinsEvent(getUI(), item, false));
					
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
											fireEvent(new CofinsEvent(getUI(), item, true));
											
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
									fireEvent(new CofinsEvent(getUI(), item, false));
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
	
	
	public void addListerner(CofinsListerner target){
		try {
			Method method = CofinsListerner.class.getDeclaredMethod("onClose", CofinsEvent.class);
			addListener(CofinsEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CofinsListerner target){
		removeListener(CofinsEvent.class, target);
	}
	public static class CofinsEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public CofinsEvent(Component source, Item item, boolean confirm) {
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
	public interface CofinsListerner extends Serializable{
		public void onClose(CofinsEvent event);
	}
	
}
