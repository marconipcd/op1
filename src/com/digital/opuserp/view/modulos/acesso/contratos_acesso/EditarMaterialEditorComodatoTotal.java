package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Validator;
import com.digital.opuserp.view.util.MaterialUtil;
import com.digital.opuserp.view.util.MaterialUtil.MaterialEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class EditarMaterialEditorComodatoTotal extends Window implements GenericEditor{
	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String motivo;
	ComboBox cbProduto;
	VerticalLayout vlRootSubMaterialOnu;
	VerticalLayout vlRootSubMaterialRoteador;
	CheckBox ckbNotAllowRoteador;
	CheckBox ckbNotAllowOnu;
	Produto onu_selecionada;
	TextField tfDescricaoOnu;
	ComboBox cbSerialOnu;
	TextField txtGpon;
	boolean valid_onu = false;
	
	Produto roteador_selecionado;
	private TextField tfDescricaoRoteador;
	private ComboBox cbMacRoteador;
	
	private JPAContainer<SerialProduto> containerSeriais;
		
	boolean valid_roteador = false;
	
	private TextField txtSerial;
	private ContratosAcesso contratoAcesso;
	
	//boolean proprio_parcial = false;

	public EditarMaterialEditorComodatoTotal(Item item, String title, boolean modal){
		this.item = item;

		contratoAcesso = (ContratosAcesso) item.getItemProperty("contrato").getValue();
		
		setWidth("751px");
		//setHeight("440px");
		
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
		buildLayout();
		
	}
	

	private void buildLayout() {
		
		//proprio_parcial = item.getItemProperty("regime").getValue().toString().equals("PROPRIO (PARCIAL)") ? true : false;
		
//		if(proprio_parcial){
//			setHeight("570px");
//		}
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField tfCodContrato = new TextField("Contrato");
					tfCodContrato.setValue(item.getItemProperty("id").getValue().toString());
					tfCodContrato.setReadOnly(true);
					tfCodContrato.setWidth("62px");
					tfCodContrato.setStyleName("caption-align-editar-material");
					
					addComponent(tfCodContrato);
				}
		});
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					Cliente cliente = (Cliente) item.getItemProperty("cliente").getValue();
					
					TextField tfNomeCliente = new TextField("Titular Atual");
					tfNomeCliente.setValue(cliente.getNome_razao());
					tfNomeCliente.setReadOnly(true);
					tfNomeCliente.setWidth("360px");
					tfNomeCliente.setStyleName("caption-align-editar-material");
					
					addComponent(tfNomeCliente);
				}
		});	
		
		final Produto materialAntigo = item.getItemProperty("material").getValue() != null ? (Produto) item.getItemProperty("material").getValue() : null;
		final Produto materialNovo = item.getItemProperty("onu").getValue() != null ? (Produto) item.getItemProperty("onu").getValue() : null;
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					
					TextField tfMaterialAtual = new TextField("Roteador Atual");
										 
					tfMaterialAtual.setValue(materialAntigo != null ? materialAntigo.getNome() : "NENHUM");
					tfMaterialAtual.setReadOnly(true);
					tfMaterialAtual.setWidth("360px");
					tfMaterialAtual.setStyleName("caption-align-editar-material");
					
					addComponent(tfMaterialAtual);
				}
		});	
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					String enderecoMac = item.getItemProperty("endereco_mac").getValue().toString();
					
					TextField tfEnderecoMac = new TextField("Endereço MAC atual");
					tfEnderecoMac.setValue(enderecoMac);
					tfEnderecoMac.setReadOnly(true);
					tfEnderecoMac.setWidth("147px");
					tfEnderecoMac.setStyleName("caption-align-editar-material");
					
					addComponent(tfEnderecoMac);
				}
		});	
			
		vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						
						TextField tfMaterialAtual = new TextField("ONU Atual");
						
						 
						tfMaterialAtual.setValue(materialNovo != null ? materialNovo.getNome() : "NENHUM");
						tfMaterialAtual.setReadOnly(true);
						tfMaterialAtual.setWidth("360px");
						tfMaterialAtual.setStyleName("caption-align-editar-material");
						
						addComponent(tfMaterialAtual);
					}
			});	
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						String enderecoMac = item.getItemProperty("onu_serial").getValue().toString();
						
						TextField tfEnderecoMac = new TextField("Serial atual");
						tfEnderecoMac.setValue(enderecoMac);
						tfEnderecoMac.setReadOnly(true);
						tfEnderecoMac.setWidth("147px");
						tfEnderecoMac.setStyleName("caption-align-editar-material");
						
						addComponent(tfEnderecoMac);
					}
			});	
			
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					
					
					TextField tfRegime = new TextField("Regime");
					tfRegime.setValue(item.getItemProperty("regime").getValue().toString());
					tfRegime.setReadOnly(true);
					tfRegime.setWidth("119px");
					tfRegime.setStyleName("caption-align-editar-material");
					
					addComponent(tfRegime);
				}
		});	
		
			
		
		//Campos -----------------
		//-----------------------------------------
		
		//Check Roteador
		vlRoot.addComponent(new FormLayout(){
			{
				
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
										
				ckbNotAllowRoteador = new CheckBox("Não Alterar Roteador");
				ckbNotAllowRoteador.setImmediate(true); 
				ckbNotAllowRoteador.addStyleName("caption-align-editar-material");
									
				ckbNotAllowRoteador.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
				
						if(ckbNotAllowRoteador.getValue()){
							vlRootSubMaterialRoteador.removeAllComponents();
						}else{
							buildFormRoteador();
						}
					}
				});
				
				
				addComponent(ckbNotAllowRoteador); 
				
			}				
		});
		
		vlRootSubMaterialRoteador = new VerticalLayout();
		vlRootSubMaterialRoteador.setWidth("100%");
		vlRootSubMaterialRoteador.setMargin(false);
		vlRootSubMaterialRoteador.setStyleName("form-cutom-new");	
		vlRootSubMaterialRoteador.setSpacing(false); 
		vlRoot.addComponent(vlRootSubMaterialRoteador);
		
		buildFormRoteador();
		
		//Check Onu
		vlRoot.addComponent(new FormLayout(){
				{
					
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
											
					ckbNotAllowOnu = new CheckBox("Não Alterar ONU");
					ckbNotAllowOnu.setImmediate(true); 
					ckbNotAllowOnu.addStyleName("caption-align-editar-material");
										
					ckbNotAllowOnu.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
					
							if(ckbNotAllowOnu.getValue()){
								fieldGroup.unbind(txtGpon); 
								vlRootSubMaterialOnu.removeAllComponents();
							}else{
								buildFormOnu();
							}
						}
					});
					
					
					addComponent(ckbNotAllowOnu); 
					
				}				
		});
		
		vlRootSubMaterialOnu = new VerticalLayout();
		vlRootSubMaterialOnu.setWidth("100%");
		vlRootSubMaterialOnu.setMargin(false);
		vlRootSubMaterialOnu.setStyleName("form-cutom-new");	
		vlRootSubMaterialOnu.setSpacing(false); 
		vlRoot.addComponent(vlRootSubMaterialOnu);
						
		buildFormOnu();
		
		//Motivo
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);

				
				final ComboBox cbMotivo = new ComboBox("Motivo");
				cbMotivo.setNullSelectionAllowed(false);
				cbMotivo.setRequired(true);
				cbMotivo.setWidth("200px");
				cbMotivo.addItem("DEFEITO");
				cbMotivo.addItem("INVIABILIDADE");
				cbMotivo.addItem("NOVA CONECTIVIDADE");
				cbMotivo.addItem("NOVO HOST");

				cbMotivo.setStyleName("caption-align-editar-material");
				cbMotivo.addListener(new Listener() {
					
					@Override
					public void componentEvent(Event event) {
						motivo = (String) cbMotivo.getValue();
						
					}
				});
				
				addComponent(cbMotivo);		

			}
		});
		
		
	}
	
	private void buildFormRoteador() {
		
		vlRootSubMaterialRoteador.removeAllComponents();
		
		vlRootSubMaterialRoteador.addComponent(new HorizontalLayout(){
			{								
				final TextField tfCodRoteador = new TextField("Roteador Novo");	
			
				tfCodRoteador.setWidth("60px");				
				tfCodRoteador.setNullRepresentation("");
				tfCodRoteador.setStyleName("caption-align-editar-material");
				tfCodRoteador.setImmediate(true);
				tfCodRoteador.setId("tfCodRoteador");
				tfCodRoteador.focus();
				
				JavaScript.getCurrent().execute("$('#tfCodRoteador').mask('00000000')");						
								
				tfCodRoteador.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ProdutoDAO cDAO = new ProdutoDAO();
						roteador_selecionado = new Produto();
																	
						if(event.getText()!=null && 	!event.getText().isEmpty() && !event.getText().equals("")){
																					
							roteador_selecionado = cDAO.find(Integer.parseInt(event.getText()));		
							
							if(roteador_selecionado != null){
								tfDescricaoRoteador.setReadOnly(false);
								tfDescricaoRoteador.setValue(roteador_selecionado.getNome());
								tfDescricaoRoteador.setReadOnly(true);
								
								if(cbMacRoteador != null){
									cbMacRoteador.setEnabled(true);
									cbMacRoteador.setContainerDataSource(buildContainerSeriais(roteador_selecionado.getId()));		
									cbMacRoteador.setItemCaptionPropertyId("serial");cbMacRoteador.setItemCaptionPropertyId("serial");
								}
							}else {
								tfDescricaoRoteador.setReadOnly(false);
								tfDescricaoRoteador.setValue("");
								tfDescricaoRoteador.setReadOnly(true);		
								
								if(cbMacRoteador != null){
									cbMacRoteador.setEnabled(false);
									cbMacRoteador.setContainerDataSource(null);
								}
								
								if(txtSerial != null){
									txtSerial.setEnabled(false);
								}
							}
						}
						
					}
				});

				tfCodRoteador.setRequired(true);		
				tfDescricaoRoteador = new TextField();
				tfDescricaoRoteador.setReadOnly(true);
				tfDescricaoRoteador.setWidth("416px");			
				
				final Button btSearchRoteador = new Button();
				btSearchRoteador.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchRoteador.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchRoteador.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						MaterialUtil cUtil = new MaterialUtil(true, true);
						cUtil.addListerner(new MaterialUtil.MaterialListerner() {
							
							@Override
							public void onSelected(MaterialEvent event) {
									
									if(event.getMaterial() != null ){
										
										tfCodRoteador.setValue(event.getMaterial().getId().toString());
										tfDescricaoRoteador.setReadOnly(false);
										tfDescricaoRoteador.setValue(event.getMaterial().getNome());
										tfDescricaoRoteador.setReadOnly(true);
										roteador_selecionado = event.getMaterial();		
										
										if(cbMacRoteador != null){
											cbMacRoteador.setEnabled(true);
											cbMacRoteador.setContainerDataSource(buildContainerSeriais(event.getMaterial().getId()));
											cbMacRoteador.setItemCaptionPropertyId("serial");
										}
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});			
				
				FormLayout frmCodRoteador = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(tfCodRoteador);							
					}
				};
				addComponent(frmCodRoteador);
		
				FormLayout frmButtonSearchRoteador =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");							
						addComponent(btSearchRoteador);							
					}
				}; 
							
				FormLayout frmDescRoteador = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom-new_hide_require");
						
						addComponent(tfDescricaoRoteador);							
					}
				}; 
				addComponent(frmButtonSearchRoteador);
				addComponent(frmDescRoteador);
				setExpandRatio(frmDescRoteador, 1);	

			}	
		});
			
		vlRootSubMaterialRoteador.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					cbMacRoteador = new ComboBox("Endereço MAC");
					cbMacRoteador.setEnabled(false);
					cbMacRoteador.setNullSelectionAllowed(false);
					cbMacRoteador.setStyleName("caption-align-editar-material");
					cbMacRoteador.setRequired(true);
					
					cbMacRoteador.setImmediate(true);
					
					cbMacRoteador.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(cbMacRoteador.isValid()){
								valid_roteador = true;
							}else{
								valid_roteador = false;
							}
						}
					});
					
					addComponent(cbMacRoteador);					
				}
			});
	}


	private void buildFormOnu(){		
		
					vlRootSubMaterialOnu.removeAllComponents();
						
					vlRootSubMaterialOnu.addComponent(new HorizontalLayout(){
						{
							
							final TextField txtCodSerial = new TextField("ONU Nova");				
							txtCodSerial.setWidth("60px");				
							txtCodSerial.setNullRepresentation("");
							txtCodSerial.setStyleName("caption-align-editar-material");
							txtCodSerial.setImmediate(true);
									
											
							txtCodSerial.addTextChangeListener(new TextChangeListener() {
								
								@Override
								public void textChange(TextChangeEvent event) {
									ProdutoDAO cDAO = new ProdutoDAO();
									onu_selecionada = new Produto();
									
									if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
																									
										onu_selecionada = cDAO.find(Integer.parseInt(event.getText()));		
										
										if(onu_selecionada != null){
											tfDescricaoOnu.setReadOnly(false);
											tfDescricaoOnu.setValue(onu_selecionada.getNome());
									 		tfDescricaoOnu.setReadOnly(true);
											
											if(cbSerialOnu != null){
												cbSerialOnu.setEnabled(true);
												cbSerialOnu.setContainerDataSource(buildContainerSeriais(onu_selecionada.getId()));		
												cbSerialOnu.setItemCaptionPropertyId("serial");
												cbSerialOnu.setItemCaptionPropertyId("serial");
											}
										}else {
											tfDescricaoOnu.setReadOnly(false);
											tfDescricaoOnu.setValue("");
											tfDescricaoOnu.setReadOnly(true);		
											
											cbSerialOnu.setEnabled(false);
											cbSerialOnu.setContainerDataSource(null);
										}
									}else{
										tfDescricaoOnu.setReadOnly(false);
										tfDescricaoOnu.setValue("");
										tfDescricaoOnu.setReadOnly(true);
										
										cbSerialOnu.setEnabled(false);
										cbSerialOnu.setContainerDataSource(null);
									}
								}
							});
		
							txtCodSerial.setRequired(true);		
							tfDescricaoOnu = new TextField();
							tfDescricaoOnu.setReadOnly(true);
							tfDescricaoOnu.setWidth("416px");			
										
							final Button btSearchOnu = new Button();
							btSearchOnu.setStyleName(BaseTheme.BUTTON_LINK);
							btSearchOnu.setIcon(new ThemeResource("icons/search-16.png"));
							btSearchOnu.addListener(new Button.ClickListener() {
								
								@Override
								public void buttonClick(ClickEvent event) {
									
									MaterialUtil cUtil = new MaterialUtil(true, true);
									cUtil.addListerner(new MaterialUtil.MaterialListerner() {
										
										@Override
										public void onSelected(MaterialEvent event) {
												
												if(event.getMaterial() != null){
													txtCodSerial.setValue(event.getMaterial().getId().toString());
													tfDescricaoOnu.setReadOnly(false);
													tfDescricaoOnu.setValue(event.getMaterial().getNome());
													tfDescricaoOnu.setReadOnly(true);
													onu_selecionada = event.getMaterial();		
													
													if(cbSerialOnu != null){
														cbSerialOnu.setEnabled(true);
														cbSerialOnu.setContainerDataSource(buildContainerSeriais(event.getMaterial().getId()));
														cbSerialOnu.setItemCaptionPropertyId("serial");
													}
												}
											}							
									});
									
									getUI().addWindow(cUtil);
								}
							});			
							
							FormLayout frmCodigoSerial = new FormLayout(){
								{
									
									setMargin(true);
									setSpacing(true);
									setStyleName("form-cutom");		
															
									addComponent(txtCodSerial);							
								}
							};
							addComponent(frmCodigoSerial);
					
							FormLayout frmButtonSearchOnu =new FormLayout(){
								{
									
									setMargin(true);
									setSpacing(true);
									setStyleName("form-cutom_hide_error_cell");							
									addComponent(btSearchOnu);							
								}
							}; 
										
							FormLayout frmDescOnu = new FormLayout(){
								{
									
									setMargin(true);
									setSpacing(true);						
									setStyleName("form-cutom");		
									addStyleName("form-cutom-new_hide_require");
									
									addComponent(tfDescricaoOnu);							
								}
							}; 
							addComponent(frmButtonSearchOnu);
							addComponent(frmDescOnu);
							setExpandRatio(frmDescOnu, 1);	
		
						}	
					});
					
					vlRootSubMaterialOnu.addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
								
							cbSerialOnu = new ComboBox("Serial ONU");
							cbSerialOnu.setEnabled(false);
							cbSerialOnu.setNullSelectionAllowed(false);
							cbSerialOnu.setStyleName("caption-align-editar-material");
							cbSerialOnu.setRequired(true);
							cbSerialOnu.setImmediate(true);
							
							cbSerialOnu.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									if(cbSerialOnu.isValid()){
										valid_onu = true;
									}else{
										valid_onu = false;
									}
								}
							});
							
							addComponent(cbSerialOnu);
							
						}
					});
					
					vlRootSubMaterialOnu.addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
											
							txtGpon = new TextField("GPON");
							txtGpon.setNullRepresentation("");
							txtGpon.setStyleName("caption-align-editar-material");					
							txtGpon.setRequired(true);
							txtGpon.setWidth("138px");				
							txtGpon.setId("txtGpon");
							JavaScript.getCurrent().execute("$('#txtGpon').mask('99')");
																						
							addComponent(txtGpon);	
							
							fieldGroup.bind(txtGpon, "gpon");
						}
					});
					
	}


	private JPAContainer<SerialProduto> buildContainerSeriais(Integer codProduto){
		containerSeriais = JPAContainerFactory.make(SerialProduto.class, ConnUtil.getEntity());
		containerSeriais.addContainerFilter(Filters.eq("produto", new Produto(codProduto)));
		containerSeriais.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		return containerSeriais;
	}
	
	@Override
	public Button buildBtSalvar() {
		
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				concluirEdicaoMaterial();
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
		//btSalvar.setEnabled(false);
		return btSalvar;
	}


		private void concluirEdicaoMaterial(){
						
			if(ckbNotAllowOnu.getValue()){
				valid_onu = true;
			}
			
			if(ckbNotAllowRoteador.getValue()){
				valid_roteador = true;
			}
			
			if(fieldGroup.isValid() && valid_roteador && valid_onu){
				try {	
					
					Integer codMacRoteador = null;
					if(roteador_selecionado != null){
						item.getItemProperty("material").setValue(roteador_selecionado);
						item.getItemProperty("endereco_mac").setValue(cbMacRoteador.getItem(cbMacRoteador.getValue()).getItemProperty("serial").getValue().toString());
						
						codMacRoteador = (Integer)cbMacRoteador.getItem(cbMacRoteador.getValue()).getItemProperty("id").getValue();						
					}
					
					Integer codSerialOnu = null;
					if(onu_selecionada != null){
						item.getItemProperty("onu").setValue(onu_selecionada);
						item.getItemProperty("onu_serial").setValue(cbSerialOnu.getItem(cbSerialOnu.getValue()).getItemProperty("serial").getValue().toString());
						
						codSerialOnu = (Integer)cbSerialOnu.getItem(cbSerialOnu.getValue()).getItemProperty("id").getValue();
					}
					
					fieldGroup.commit();				
					fireEvent(new EditarMaterialComodatoEvent(getUI(), item, motivo, codMacRoteador,codSerialOnu, true));		
					
				} catch (Exception e) {											
					e.printStackTrace();
					Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);	
				}
			}else{
				Notify.Show_Invalid_Submit_Form();			
			}
		}
	@Override
	public Button buildBtCancelar() {
			btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new EditarMaterialComodatoEvent(getUI(), item,motivo,null,null, false));
					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								concluirEdicaoMaterial();
							}else{							
								fieldGroup.discard();				
								fireEvent(new EditarMaterialComodatoEvent(getUI(), item,motivo,null,null, false));											
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
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
	
	
	
	public void addListerner(EditarMaterialComodatoListerner target){
		try {
			Method method = EditarMaterialComodatoListerner.class.getDeclaredMethod("onClose", EditarMaterialComodatoEvent.class);
			addListener(EditarMaterialComodatoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(EditarMaterialComodatoListerner target){
		removeListener(EditarMaterialComodatoEvent.class, target);
	}
	public static class EditarMaterialComodatoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private String motivo;
		private Integer codMacRoteador;
		private Integer codSerialOnu;
		
		public EditarMaterialComodatoEvent(Component source, Item item,String motivo, Integer codMacRoteador,Integer codSerialOnu, boolean confirm) {
			super(source);
			this.item = item;
			this.motivo = motivo;
			this.confirm = confirm;
			this.codMacRoteador = codMacRoteador;
			this.codSerialOnu = codSerialOnu;
			
		}
		
		public Integer getCodMacRoteador(){
			return codMacRoteador;
		}
		
		public Integer getCodSerialOnu(){
			return codSerialOnu;
		}

		public Item getItem() {
			return item;
		}	
		
		public String getMotivo() {
			return motivo;
		}	

		public boolean isConfirm() {
			return confirm;
		}	
		
		
	}
	public interface EditarMaterialComodatoListerner extends Serializable{
		public void onClose(EditarMaterialComodatoEvent event);
	}

}
