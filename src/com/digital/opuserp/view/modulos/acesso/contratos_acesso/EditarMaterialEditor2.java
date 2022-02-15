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
import com.digital.opuserp.util.Validator;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.EditarMaterialEditor.EditarProdutoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.EditarMaterialEditor.EditarProdutoListerner;
import com.digital.opuserp.view.util.MaterialUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.MaterialUtil.MaterialEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.themes.BaseTheme;

public class EditarMaterialEditor2 extends Window implements GenericEditor{
	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String motivo;
	ComboBox cbProduto;
	VerticalLayout vlRootSubMaterial;
	CheckBox ckb;
	Produto material_selecionado_comodato;
	TextField tfDescricaoMaterialComodato;
	ComboBox cbSerialMaterialComodato;
	TextField txtGpon;
	boolean valid_serial_material_comodato = false;
	
	Produto material_selecionado;
	private TextField tfDescricaoMaterial;
	private ComboBox cbSerial;
	
	private JPAContainer<SerialProduto> containerSeriais;
		
	boolean valid_mac = false;
	
	private TextField txtSerial;
	private ContratosAcesso contratoAcesso;
	
	boolean proprio_parcial = false;

	public EditarMaterialEditor2(Item item, String title, boolean modal){
		this.item = item;

		contratoAcesso = (ContratosAcesso) item.getItemProperty("contrato").getValue();
		
		setWidth("751px");
		setHeight("440px");
		
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
		
		proprio_parcial = item.getItemProperty("regime").getValue().toString().equals("PROPRIO (PARCIAL)") ? true : false;
		
		if(proprio_parcial){
			setHeight("570px");
		}
		
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
					
					
					TextField tfMaterialAtual = new TextField("Material Atual");
					if(proprio_parcial){
						tfMaterialAtual.setCaption("Material PROPRIO Atual");
					}
					 
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
		
		
		if(proprio_parcial){
			
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						
						TextField tfMaterialAtual = new TextField("Material COMODATO Atual");
						
						 
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
						
						TextField tfEnderecoMac = new TextField("Endereço MAC atual");
						tfEnderecoMac.setValue(enderecoMac);
						tfEnderecoMac.setReadOnly(true);
						tfEnderecoMac.setWidth("147px");
						tfEnderecoMac.setStyleName("caption-align-editar-material");
						
						addComponent(tfEnderecoMac);
					}
			});	
			
		}
		
		
		
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
		
			
		vlRoot.addComponent(new HorizontalLayout(){
			{
											
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
					preencher = true;
				}
				
				final TextField tfCodMaterial = new TextField("Material");	
				if(proprio_parcial){
					tfCodMaterial.setCaption("Material PROPRIO Novo");
				}
				
				tfCodMaterial.setWidth("60px");				
				tfCodMaterial.setNullRepresentation("");
				tfCodMaterial.setStyleName("caption-align-editar-material");
				tfCodMaterial.setImmediate(true);
				tfCodMaterial.setId("txtCodMaterial");
				tfCodMaterial.focus();
				
				JavaScript.getCurrent().execute("$('#txtCodMaterial').mask('00000000')");						
								
				tfCodMaterial.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ProdutoDAO cDAO = new ProdutoDAO();
						material_selecionado = new Produto();
																	
						if(event.getText()!=null && 
								!event.getText().isEmpty() && 
								!event.getText().equals("") 
							){
							
														
							material_selecionado = cDAO.find(Integer.parseInt(event.getText()));		
							
							if(material_selecionado != null){
								tfDescricaoMaterial.setReadOnly(false);
								tfDescricaoMaterial.setValue(material_selecionado.getNome());
								tfDescricaoMaterial.setReadOnly(true);
								
								if(cbSerial != null){
									cbSerial.setEnabled(true);
									cbSerial.setContainerDataSource(buildContainerSeriais(material_selecionado.getId()));		
									cbSerial.setItemCaptionPropertyId("serial");cbSerial.setItemCaptionPropertyId("serial");
								}
							}else {
								tfDescricaoMaterial.setReadOnly(false);
								tfDescricaoMaterial.setValue("");
								tfDescricaoMaterial.setReadOnly(true);		
								
								if(cbSerial != null){
									cbSerial.setEnabled(false);
									cbSerial.setContainerDataSource(null);
								}
								
								if(txtSerial != null){
									txtSerial.setEnabled(false);
								}
							}
						}
						
					}
				});

				tfCodMaterial.setRequired(true);		
				tfDescricaoMaterial = new TextField();
				tfDescricaoMaterial.setReadOnly(true);
				tfDescricaoMaterial.setWidth("416px");			
									
				
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						MaterialUtil cUtil = new MaterialUtil(true, true);
						cUtil.addListerner(new MaterialUtil.MaterialListerner() {
							
							@Override
							public void onSelected(MaterialEvent event) {
									
									if(event.getMaterial() != null ){
										
										tfCodMaterial.setValue(event.getMaterial().getId().toString());
										tfDescricaoMaterial.setReadOnly(false);
										tfDescricaoMaterial.setValue(event.getMaterial().getNome());
										tfDescricaoMaterial.setReadOnly(true);
										material_selecionado = event.getMaterial();		
										
										if(cbSerial != null){
											cbSerial.setEnabled(true);
											cbSerial.setContainerDataSource(buildContainerSeriais(event.getMaterial().getId()));
											cbSerial.setItemCaptionPropertyId("serial");
										}
									}else{
										
										if(event.getMaterial() != null && materialAntigo != null && materialAntigo.getId().equals(event.getMaterial().getId())){											
											tfCodMaterial.setValue("");											
											Notify.Show("Não é possivel selecionar o  mesmo material!", Notify.TYPE_ERROR);
										}
										
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});			
				
				FormLayout frmCodigoCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(tfCodMaterial);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");							
						addComponent(btSearchCliente);							
					}
				}; 
							
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom-new_hide_require");
						
						addComponent(tfDescricaoMaterial);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);	

			}	
		});
		
					
			
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					
					
					if(item.getItemProperty("regime").getValue().equals("COMODATO")){					
						cbSerial = new ComboBox("Endereço MAC");
						cbSerial.setEnabled(false);
						cbSerial.setNullSelectionAllowed(false);
						cbSerial.setStyleName("caption-align-editar-material");
						cbSerial.setRequired(true);
						
						cbSerial.setImmediate(true);
						
						cbSerial.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(cbSerial.isValid()){
									valid_mac = true;
								}else{
									valid_mac = false;
								}
							}
						});
						
						addComponent(cbSerial);
					}else{
						txtSerial = new TextField("Endereço MAC");
						txtSerial.setStyleName("caption-align-editar-material");
						txtSerial.setRequired(true);
						txtSerial.setId("txtSerial");						
						//01:01:01:01:01:01
						JavaScript.getCurrent().execute("$('#txtSerial').mask('AA:AA:AA:AA:AA:AA')");
						
						txtSerial.setImmediate(true);
						txtSerial.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								if(event.getText() != null && !event.getText().equals("") && !event.getText().isEmpty() && Validator.MacAddress(event.getText())){
									CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
									boolean permite = caDAO.checkSerialProprio(event.getText());
									
									if(!permite){
										txtSerial.addStyleName("invalid-cpf");
										valid_mac = false;										
										Notify.Show("Necessário Comprar Material", Notify.TYPE_ERROR);
									}else{
										txtSerial.removeStyleName("invalid-cpf");
										valid_mac = true;
									}
								}else{
									txtSerial.addStyleName("invalid-cpf");
									valid_mac = false;
								}
								
							}
						});
						
						txtSerial.addBlurListener(new FieldEvents.BlurListener() {
							
							@Override
							public void blur(BlurEvent event) {
								if(txtSerial.getValue() !=null && !txtSerial.getValue().isEmpty() && !txtSerial.getValue().equals("") && 
										!Validator.MacAddress(txtSerial.getValue())){
									
									Notify.Show("Formato de MAC Inválido", Notify.TYPE_ERROR);
								}
							}
						});
						
						
						addComponent(txtSerial);
					}					
					
				}
			});
		

		
		
		vlRoot.addComponent(
				new FormLayout(){
				{
					
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
											
					ckb = new CheckBox("Não Alterar Material COMODATO");
					ckb.setImmediate(true); 
					ckb.addStyleName("caption-align-editar-material");
										
					ckb.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
					
							if(ckb.getValue()){
								fieldGroup.unbind(txtGpon); 
								vlRootSubMaterial.removeAllComponents();
							}else{
								buildFormMaterialComodato(item.getItemProperty("regime").getValue().toString());
							}
						}
					});
					
					
					addComponent(ckb); 
					
				}				
		});
		
		
		
		vlRootSubMaterial = new VerticalLayout();
		vlRootSubMaterial.setWidth("100%");
		vlRootSubMaterial.setMargin(false);
		vlRootSubMaterial.setStyleName("form-cutom-new");	
		vlRootSubMaterial.setSpacing(false); 
		vlRoot.addComponent(vlRootSubMaterial);
		
		
		
		buildFormMaterialComodato(item.getItemProperty("regime").getValue().toString());
		
		
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
	
	private void buildFormMaterialComodato(String regime){
		
		//this.setHeight("393px");
		if(vlRootSubMaterial != null){
		
				vlRootSubMaterial.removeAllComponents();
		
				if(regime.equals("PROPRIO (PARCIAL)")){			
					//this.setHeight("479px");
							
					vlRootSubMaterial.addComponent(new HorizontalLayout(){
						{
							
							final TextField tfCodMaterialComodato = new TextField("Material COMODATO Novo");				
							tfCodMaterialComodato.setWidth("60px");				
							tfCodMaterialComodato.setNullRepresentation("");
							tfCodMaterialComodato.setStyleName("caption-align-editar-material");
							tfCodMaterialComodato.setImmediate(true);
									
											
							tfCodMaterialComodato.addListener(new TextChangeListener() {
								
								@Override
								public void textChange(TextChangeEvent event) {
									ProdutoDAO cDAO = new ProdutoDAO();
									material_selecionado_comodato = new Produto();
									
									if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
										
																	
										material_selecionado_comodato = cDAO.find(Integer.parseInt(event.getText()));		
										
										if(material_selecionado_comodato != null){
											tfDescricaoMaterialComodato.setReadOnly(false);
											tfDescricaoMaterialComodato.setValue(material_selecionado_comodato.getNome());
									 		tfDescricaoMaterialComodato.setReadOnly(true);
											
											if(cbSerialMaterialComodato != null){
												cbSerialMaterialComodato.setEnabled(true);
												cbSerialMaterialComodato.setContainerDataSource(buildContainerSeriais(material_selecionado_comodato.getId()));		
												cbSerialMaterialComodato.setItemCaptionPropertyId("serial");
												cbSerialMaterialComodato.setItemCaptionPropertyId("serial");
											}
										}else {
											tfDescricaoMaterialComodato.setReadOnly(false);
											tfDescricaoMaterialComodato.setValue("");
											tfDescricaoMaterialComodato.setReadOnly(true);		
											
											cbSerialMaterialComodato.setEnabled(false);
											cbSerialMaterialComodato.setContainerDataSource(null);
										}
									}else{
										tfDescricaoMaterialComodato.setReadOnly(false);
										tfDescricaoMaterialComodato.setValue("");
										tfDescricaoMaterialComodato.setReadOnly(true);
										
										cbSerialMaterialComodato.setEnabled(false);
										cbSerialMaterialComodato.setContainerDataSource(null);
									}
								}
							});
		
							tfCodMaterialComodato.setRequired(true);		
							tfDescricaoMaterialComodato = new TextField();
							tfDescricaoMaterialComodato.setReadOnly(true);
							tfDescricaoMaterialComodato.setWidth("416px");			
												
							
										
							final Button btSearchMaterialComodato = new Button();
							btSearchMaterialComodato.setStyleName(BaseTheme.BUTTON_LINK);
							btSearchMaterialComodato.setIcon(new ThemeResource("icons/search-16.png"));
							btSearchMaterialComodato.addListener(new Button.ClickListener() {
								
								@Override
								public void buttonClick(ClickEvent event) {
									
									MaterialUtil cUtil = new MaterialUtil(true, true);
									cUtil.addListerner(new MaterialUtil.MaterialListerner() {
										
										@Override
										public void onSelected(MaterialEvent event) {
												
												if(event.getMaterial() != null){
													tfCodMaterialComodato.setValue(event.getMaterial().getId().toString());
													tfDescricaoMaterialComodato.setReadOnly(false);
													tfDescricaoMaterialComodato.setValue(event.getMaterial().getNome());
													tfDescricaoMaterialComodato.setReadOnly(true);
													material_selecionado_comodato = event.getMaterial();		
													
													if(cbSerialMaterialComodato != null){
														cbSerialMaterialComodato.setEnabled(true);
														cbSerialMaterialComodato.setContainerDataSource(buildContainerSeriais(event.getMaterial().getId()));
														cbSerialMaterialComodato.setItemCaptionPropertyId("serial");
													}
												}
											}							
									});
									
									getUI().addWindow(cUtil);
								}
							});			
							
							FormLayout frmCodigoCliente = new FormLayout(){
								{
									
									setMargin(true);
									setSpacing(true);
									setStyleName("form-cutom");		
															
									addComponent(tfCodMaterialComodato);							
								}
							};
							addComponent(frmCodigoCliente);
					
							FormLayout frmButtonSearchMaterialComodato =new FormLayout(){
								{
									
									setMargin(true);
									setSpacing(true);
									setStyleName("form-cutom_hide_error_cell");							
									addComponent(btSearchMaterialComodato);							
								}
							}; 
										
							FormLayout frmDescMaterialComodato = new FormLayout(){
								{
									
									setMargin(true);
									setSpacing(true);						
									setStyleName("form-cutom");		
									addStyleName("form-cutom-new_hide_require");
									
									addComponent(tfDescricaoMaterialComodato);							
								}
							}; 
							addComponent(frmButtonSearchMaterialComodato);
							addComponent(frmDescMaterialComodato);
							setExpandRatio(frmDescMaterialComodato, 1);	
		
						}	
					});
					
					vlRootSubMaterial.addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
												
							
								
							cbSerialMaterialComodato = new ComboBox("Serial ONU");
							cbSerialMaterialComodato.setEnabled(false);
							cbSerialMaterialComodato.setNullSelectionAllowed(false);
							cbSerialMaterialComodato.setStyleName("caption-align-editar-material");
							cbSerialMaterialComodato.setRequired(true);
							cbSerialMaterialComodato.setImmediate(true);
							
							cbSerialMaterialComodato.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									if(cbSerialMaterialComodato.isValid()){
										valid_serial_material_comodato = true;
									}else{
										valid_serial_material_comodato = false;
									}
								}
							});
							
							addComponent(cbSerialMaterialComodato);
											
							
						}
					});
					
					vlRootSubMaterial.addComponent(new FormLayout(){					
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
					
				}else{
					if(txtGpon != null){
						fieldGroup.unbind(txtGpon); 
					}
				}
		
		}
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
			boolean allow = true;
			if(item.getItemProperty("regime").getValue().equals("PROPRIO") && txtSerial.getValue().equals(item.getItemProperty("endereco_mac").getValue().toString())){
				allow = false;
			}
			
			if(fieldGroup.isValid() && valid_mac && allow){
				try {	
					item.getItemProperty("material").setValue(material_selecionado);
					
					Integer codSerial = null;
					if(item.getItemProperty("regime").getValue().equals("COMODATO")){	
						item.getItemProperty("endereco_mac").setValue(cbSerial.getItem(cbSerial.getValue()).getItemProperty("serial").getValue().toString());
						codSerial = (Integer)cbSerial.getItem(cbSerial.getValue()).getItemProperty("id").getValue();
					}else{
						item.getItemProperty("endereco_mac").setValue(txtSerial.getValue());
					}
					
					
					if(item.getItemProperty("regime").getValue().equals("PROPRIO (PARCIAL)")){
						if(!ckb.getValue()){
							codSerial = (Integer)cbSerialMaterialComodato.getItem(cbSerialMaterialComodato.getValue()).getItemProperty("id").getValue();
							item.getItemProperty("onu_serial").setValue(cbSerialMaterialComodato.getItem(cbSerialMaterialComodato.getValue()).getItemProperty("serial").getValue().toString());
							item.getItemProperty("onu").setValue(material_selecionado_comodato);
						}else{
							codSerial = null;
						}						
					}
					
					
					
					fieldGroup.commit();				
					fireEvent(new EditarProdutoEvent(getUI(), item, motivo, codSerial, true));		
						
					
				} catch (Exception e) {											
					e.printStackTrace();
					Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);	
				}
			}else{
				Notify.Show_Invalid_Submit_Form();
				
				if(!allow){
					Notify.Show("Não é possivel selecionar o mesmo Material!", Notify.TYPE_ERROR);
				}
			}
		}
	@Override
	public Button buildBtCancelar() {
			btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new EditarProdutoEvent(getUI(), item,motivo,null, false));
					
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
								fireEvent(new EditarProdutoEvent(getUI(), item,motivo,null, false));											
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
	
	
	
	public void addListerner(EditarProdutoListerner target){
		try {
			Method method = EditarProdutoListerner.class.getDeclaredMethod("onClose", EditarProdutoEvent.class);
			addListener(EditarProdutoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(EditarProdutoListerner target){
		removeListener(EditarProdutoEvent.class, target);
	}
	public static class EditarProdutoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private String motivo;
		private Integer codSerial;
		
		public EditarProdutoEvent(Component source, Item item,String motivo, Integer codSerial, boolean confirm) {
			super(source);
			this.item = item;
			this.motivo = motivo;
			this.confirm = confirm;
			this.codSerial = codSerial;
			
		}
		
		public Integer getCodSerial(){
			return codSerial;
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
	public interface EditarProdutoListerner extends Serializable{
		public void onClose(EditarProdutoEvent event);
	}

}
