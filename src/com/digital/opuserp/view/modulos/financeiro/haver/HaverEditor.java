package com.digital.opuserp.view.modulos.financeiro.haver;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.EmpresaDAO;
import com.digital.opuserp.dao.HaverDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class HaverEditor extends Window  {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private TextField tfCodCliente;
	private TextField tfDescricaoCliente;
	private Cliente ClienteSelecionado;
	
	private TextField tfCodClienteInd;
	private TextField tfDescricaoClienteInd;
	private Cliente ClienteSelecionadoInd;
	
	TextField txtReferente;
	ComboBox cbDoc;
	String referencia;
	TextField txtNdoc;
	
	private TextField txtValor;
	
	boolean indicacao;
	
	
	public HaverEditor(final Item item, String title, boolean modal,boolean indicacao){
		this.item = item;
		this.indicacao = indicacao;
		
		
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
				
				if(item.getItemProperty("id").getValue() != null){				
					hlButtons.addComponent(buildBtCancelar("Fechar"));					
				}else{
					hlButtons.addComponent(buildBtCancelar("Cancelar"));
					hlButtons.addComponent(buildBtSalvar());
				}
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		if(item.getItemProperty("id").getValue() != null){
			buildLayoutReadOnly();
		}else{
			buildLayout();
		}
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
					
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtCodigo = new TextField ("Código");				
					txtCodigo.setWidth("80px");				
					txtCodigo.setStyleName("caption-align");
					txtCodigo.setNullRepresentation("");										
					
					addComponent(txtCodigo);					
					txtCodigo.setValue(HaverDAO.getNextID());
				}
			});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				//setWidth("100%");
				
				JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
				containerClientes.addContainerFilter(Filters.eq("status", "INATIVO"));
				
				
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
					preencher = true;
				}
				
				tfCodCliente = new TextField("Cliente");				
				tfCodCliente.setWidth("60px");				
				tfCodCliente.setNullRepresentation("");
				tfCodCliente.setStyleName("caption-align");				
				tfCodCliente.setId("txtCodCliente");
				tfCodCliente.focus();
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				tfCodCliente.setImmediate(true);
						
				
				
				tfCodCliente.addListener(new TextChangeListener() {
					
					
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						ClienteSelecionado = new Cliente();
						
						//somente numeros contaas receber
						
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							ClienteSelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));		
							
							if(ClienteSelecionado != null){
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
								tfDescricaoCliente.setReadOnly(true);
								
								
							}else {
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue("");
								tfDescricaoCliente.setReadOnly(true);
								
							
								
							}
						}else{
							tfDescricaoCliente.setReadOnly(false);
							tfDescricaoCliente.setValue("");
							tfDescricaoCliente.setReadOnly(true);	
							
						

						}
					}
				});

				tfCodCliente.setRequired(true);		
				tfDescricaoCliente = new TextField();
				tfDescricaoCliente.setTabIndex(2000);
				tfDescricaoCliente.setReadOnly(true);
				tfDescricaoCliente.setWidth("562px");
				
				
									
				if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
					EmpresaDAO eDAO = new EmpresaDAO();
					Cliente c = eDAO.getCliente(((Cliente)item.getItemProperty("cliente").getValue()).getId());
					
					if(c != null){
						tfCodCliente.setValue(c.getId().toString());
						tfCodCliente.setReadOnly(false);
						
						tfDescricaoCliente.setReadOnly(false);
						tfDescricaoCliente.setValue(c.getNome_razao());
						tfDescricaoCliente.setReadOnly(true);
					}
				}
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setTabIndex(300000);
				btSearchCliente.addClickListener(new Button.ClickListener() {
					
					
					public void buttonClick(ClickEvent event) {
						ClienteUtil cUtil = new ClienteUtil(true, true,"INATIVO");
						cUtil.addListerner(new ClienteUtil.ClienteListerner() {
							
							
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
									if(event.getCliente() != null){
										tfCodCliente.setValue(event.getCliente().getId().toString());
										tfDescricaoCliente.setReadOnly(false);
										tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
										tfDescricaoCliente.setReadOnly(true);
										ClienteSelecionado = event.getCliente();

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
												
						addComponent(tfCodCliente);							
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
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoCliente);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);	
				
//				fieldGroup.bind(tfCodCliente,Clientelecionado);

			}	
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					cbDoc = new ComboBox("Documento");
					cbDoc.setNullSelectionAllowed(false);
					cbDoc.setTextInputAllowed(false);
					cbDoc.setWidth("250px");				
					cbDoc.setStyleName("caption-align");
					cbDoc.addItem("CHEQUE");
					cbDoc.addItem("DINHEIRO");
					cbDoc.addItem("DEPOSITO");
					cbDoc.addItem("VALE COMPRA");
					cbDoc.addItem("CREDITO/DEBITO");
					
					cbDoc.setRequired(true);
					
					if(indicacao){
						cbDoc.addItem("INDICACAO");
						cbDoc.select("INDICACAO");;
						cbDoc.setReadOnly(true);
						addComponent(cbDoc);
					}else{
						addComponent(cbDoc);					
						fieldGroup.bind(cbDoc,"doc");						
					}
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtNdoc = new TextField ("Nº Documento");				
					txtNdoc.setWidth("250px");				
					txtNdoc.setStyleName("caption-align");
					txtNdoc.setNullRepresentation("");			
					txtNdoc.setRequired(true);
					
					if(indicacao){
						txtNdoc.setValue("NENHUM");			
						txtNdoc.setReadOnly(true);
						addComponent(txtNdoc);					
					}else{
						addComponent(txtNdoc);
						fieldGroup.bind(txtNdoc,"n_doc");
					}

				}
			});
		
		
		if(indicacao){
			vlRoot.addComponent(new HorizontalLayout(){
				{
					//setWidth("100%");
					
					JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
					containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
					containerClientes.addContainerFilter(Filters.eq("status", "INATIVO"));
					
					
					boolean preencher = false;
					if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
						preencher = true;
					}
					
					tfCodClienteInd = new TextField("Cliente Indicado");				
					tfCodClienteInd.setWidth("60px");				
					tfCodClienteInd.setNullRepresentation("");
					tfCodClienteInd.setStyleName("caption-align");				
					tfCodClienteInd.setId("txtCodClienteInd");
					tfCodClienteInd.focus();
					
					JavaScript.getCurrent().execute("$('#txtCodClienteInd').mask('0000000000')");
					tfCodClienteInd.setImmediate(true);
							
					
					
					tfCodClienteInd.addListener(new TextChangeListener() {
						
						
						public void textChange(TextChangeEvent event) {
							ClienteDAO cDAO = new ClienteDAO();
							ClienteSelecionadoInd = new Cliente();
							
							//somente numeros contaas receber
							
							
							if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
								
															
								ClienteSelecionadoInd = cDAO.getCliente(Integer.parseInt(event.getText()));		
								
								if(ClienteSelecionadoInd != null){
									tfDescricaoClienteInd.setReadOnly(false);
									tfDescricaoClienteInd.setValue(ClienteSelecionadoInd.getNome_razao());
									tfDescricaoClienteInd.setReadOnly(true);
									
									
								}else {
									tfDescricaoClienteInd.setReadOnly(false);
									tfDescricaoClienteInd.setValue("");
									tfDescricaoClienteInd.setReadOnly(true);
									
								
									
								}
							}else{
								tfDescricaoClienteInd.setReadOnly(false);
								tfDescricaoClienteInd.setValue("");
								tfDescricaoClienteInd.setReadOnly(true);	
								
							

							}
						}
					});

					tfCodClienteInd.setRequired(true);		
					tfDescricaoClienteInd = new TextField();
					tfDescricaoClienteInd.setTabIndex(2000);
					tfDescricaoClienteInd.setReadOnly(true);
					tfDescricaoClienteInd.setWidth("562px");
					
					
										
					if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
						EmpresaDAO eDAO = new EmpresaDAO();
						Cliente c = eDAO.getCliente(((Cliente)item.getItemProperty("cliente").getValue()).getId());
						
						if(c != null){
							tfCodClienteInd.setValue(c.getId().toString());
							tfCodClienteInd.setReadOnly(false);
							
							tfDescricaoClienteInd.setReadOnly(false);
							tfDescricaoClienteInd.setValue(c.getNome_razao());
							tfDescricaoClienteInd.setReadOnly(true);
						}
					}
								
					final Button btSearchCliente = new Button();
					btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
					btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
					btSearchCliente.setTabIndex(300000);
					btSearchCliente.addClickListener(new Button.ClickListener() {
						
						
						public void buttonClick(ClickEvent event) {
							ClienteUtil cUtil = new ClienteUtil(true, true,"INATIVO");
							cUtil.addListerner(new ClienteUtil.ClienteListerner() {
								
								
								public void onSelected(
										com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
										if(event.getCliente() != null){
											tfCodClienteInd.setValue(event.getCliente().getId().toString());
											tfDescricaoClienteInd.setReadOnly(false);
											tfDescricaoClienteInd.setValue(event.getCliente().getNome_razao());
											tfDescricaoClienteInd.setReadOnly(true);
											ClienteSelecionadoInd = event.getCliente();

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
													
							addComponent(tfCodClienteInd);							
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
							setStyleName("form-cutom-new");		
							addStyleName("form-cutom_hide_require");
							
							addComponent(tfDescricaoClienteInd);							
						}
					}; 
					addComponent(frmButtonSearchCliente);
					addComponent(frmDescCliente);
					setExpandRatio(frmDescCliente, 1);	
					
//					fieldGroup.bind(tfCodCliente,Clientelecionado);

					if(tfCodClienteInd!= null && !tfCodClienteInd.getValue().equals("")){
						referencia = "INDICACAO: "+tfCodClienteInd.getValue()+"-"+tfDescricaoClienteInd.getValue();						
					}
				}	
			});
		}else{		
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
															
						txtReferente = new TextField ("Referente a");				
						txtReferente.setWidth("250px");				
						txtReferente.setStyleName("caption-align");
						txtReferente.setNullRepresentation("");			
						txtReferente.setRequired(true);
						
						addComponent(txtReferente);					
						
						
						fieldGroup.bind(txtReferente,"referente");
						
					}
				});	
		}
		

		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					txtValor = new TextField ("Valor do Haver");				
					txtValor.setWidth("80px");				
					txtValor.setStyleName("caption-align");
					txtValor.setNullRepresentation("");			
					txtValor.setRequired(true);
					txtValor.setId("txtValor");
					txtValor.addStyleName("align-currency");
					
					addComponent(txtValor);					
										
					JavaScript.getCurrent().execute("$('#txtValor').maskMoney({decimal:',',thousands:'.'})");
				}
			});

		
		
	}
	
	
	public void buildLayoutReadOnly(){
		
		fieldGroup = new FieldGroup(item);
					
		vlRoot.addComponent(
				new HorizontalLayout(){
					{
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																	
								TextField txtCodigo = new TextField ("Código");				
								txtCodigo.setWidth("80px");				
								txtCodigo.setStyleName("caption-align");
								txtCodigo.setNullRepresentation("");										
								
								addComponent(txtCodigo);					
								
								fieldGroup.bind(txtCodigo,"id");
								txtCodigo.setReadOnly(true);					
							}
						});
						
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																	
								TextField txtDataEmissao = new TextField ("Data de Emissão");				
								txtDataEmissao.setWidth("130px");				
								txtDataEmissao.setStyleName("caption-align");
								txtDataEmissao.setNullRepresentation("");			
								txtDataEmissao.addStyleName("align-404-data-emissao");
								
								
								addComponent(txtDataEmissao);					
								
								if(item.getItemProperty("data_emissao").getValue() != null){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
									txtDataEmissao.setValue(sdf.format(item.getItemProperty("data_emissao").getValue()));
								}
								
								txtDataEmissao.setReadOnly(true);					
							}
						});
					}
				});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				//setWidth("100%");
				
				JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
				containerClientes.addContainerFilter(Filters.eq("status", "INATIVO"));
				
				
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
					preencher = true;
				}
				
				tfCodCliente = new TextField("Cliente");				
				tfCodCliente.setWidth("60px");				
				tfCodCliente.setNullRepresentation("");
				tfCodCliente.setStyleName("caption-align");				
				tfCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				tfCodCliente.setImmediate(true);
						
				
				
				tfCodCliente.addListener(new TextChangeListener() {
					
					
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						ClienteSelecionado = new Cliente();
						
						//somente numeros contaas receber
						
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							ClienteSelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));		
							
							if(ClienteSelecionado != null){
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
								tfDescricaoCliente.setReadOnly(true);
								
								
							}else {
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue("");
								tfDescricaoCliente.setReadOnly(true);
								
							
								
							}
						}else{
							tfDescricaoCliente.setReadOnly(false);
							tfDescricaoCliente.setValue("");
							tfDescricaoCliente.setReadOnly(true);	
							
						

						}
					}
				});

				tfCodCliente.setRequired(true);		
				tfDescricaoCliente = new TextField();
				tfDescricaoCliente.setTabIndex(2000);
				tfDescricaoCliente.setReadOnly(true);
				tfDescricaoCliente.setWidth("562px");
				
				
									
				if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
					EmpresaDAO eDAO = new EmpresaDAO();
					Cliente c = eDAO.getCliente(((Cliente)item.getItemProperty("cliente").getValue()).getId());
					
					if(c != null){
						tfCodCliente.setValue(c.getId().toString());
						tfCodCliente.setReadOnly(false);
						
						tfDescricaoCliente.setReadOnly(false);
						tfDescricaoCliente.setValue(c.getNome_razao());
						tfDescricaoCliente.setReadOnly(true);
					}
				}
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setTabIndex(300000);
				btSearchCliente.setEnabled(false);
				btSearchCliente.addClickListener(new Button.ClickListener() {
					
					
					public void buttonClick(ClickEvent event) {
						ClienteUtil cUtil = new ClienteUtil(true, true,"INATIVO");
						cUtil.addListerner(new ClienteUtil.ClienteListerner() {
							
							
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
									if(event.getCliente() != null){
										tfCodCliente.setValue(event.getCliente().getId().toString());
										tfDescricaoCliente.setReadOnly(false);
										tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
										tfDescricaoCliente.setReadOnly(true);
										ClienteSelecionado = event.getCliente();

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
												
						addComponent(tfCodCliente);							
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
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoCliente);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);	
				
//				fieldGroup.bind(tfCodCliente,Clientelecionado);
				
				
				if(item.getItemProperty("cliente").getValue() != null){
					Cliente c = (Cliente)item.getItemProperty("cliente").getValue();
					tfCodCliente.setValue(c.getId().toString());
					tfCodCliente.setReadOnly(true);
					
					tfDescricaoCliente.setReadOnly(false);
					tfDescricaoCliente.setValue(c.getNome_razao());
					tfDescricaoCliente.setReadOnly(true);
				}

			}	
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					ComboBox cbNdoc = new ComboBox("Documento");
					cbNdoc.setNullSelectionAllowed(false);
					cbNdoc.setWidth("250px");				
					cbNdoc.setStyleName("caption-align");
					cbNdoc.addItem("CHEQUE");
					cbNdoc.addItem("DINHEIRO");
					cbNdoc.addItem("PEDIDO DE VENDA");
					cbNdoc.addItem("VALE COMPRA");
					cbNdoc.addItem("CREDITO/DEBITO");
					cbNdoc.setRequired(true);
										
					if(item.getItemProperty("doc").getValue().toString().equals("INDICACAO")){
						cbNdoc.addItem("INDICACAO");
						cbNdoc.select("INDICACAO");
						cbNdoc.setReadOnly(true);
						addComponent(cbNdoc);
					}else{
						addComponent(cbNdoc);					
						fieldGroup.bind(cbNdoc,"doc");						
						cbNdoc.setReadOnly(true);
					}
				}
			});
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtDoc = new TextField ("Nº Documento");				
					txtDoc.setWidth("250px");				
					txtDoc.setStyleName("caption-align");
					txtDoc.setNullRepresentation("");			
					txtDoc.setRequired(true);
					
					addComponent(txtDoc);					
					
					
					fieldGroup.bind(txtDoc,"nDoc");
					txtDoc.setReadOnly(true);
					
				}
			});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtReferente = new TextField ("Referente a");				
					txtReferente.setWidth("250px");				
					txtReferente.setStyleName("caption-align");
					txtReferente.setNullRepresentation("");			
					txtReferente.setRequired(true);
					
					addComponent(txtReferente);					
					
					
					fieldGroup.bind(txtReferente,"referente");
					txtReferente.setReadOnly(true);
					
				}
			});
		
		
		
		
		
		
		

		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					txtValor = new TextField ("Valor do Haver");				
					txtValor.setWidth("80px");				
					txtValor.setStyleName("caption-align");
					txtValor.setNullRepresentation("");			
					txtValor.setRequired(true);
					txtValor.setId("txtValor");
					txtValor.addStyleName("align-currency");
					
					addComponent(txtValor);	
					
					if(item.getItemProperty("valor").getValue() != null){
						txtValor.setValue(Real.formatDbToString(item.getItemProperty("valor").getValue().toString()));
					}
					
					txtValor.setReadOnly(true);					
					JavaScript.getCurrent().execute("$('#txtValor').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextField txtValorUtilizado = new TextField ("Valor Utilizado");				
					txtValorUtilizado.setWidth("80px");				
					txtValorUtilizado.setStyleName("caption-align");
					txtValorUtilizado.setNullRepresentation("");			
					txtValorUtilizado.setRequired(true);
					txtValorUtilizado.setId("txtValorUtilizado");
					txtValorUtilizado.addStyleName("align-currency");
					
					addComponent(txtValorUtilizado);	
					
					if(item.getItemProperty("valor_disponivel").getValue() != null && item.getItemProperty("valor").getValue() != null){
						double valorUtilizado = new Double(item.getItemProperty("valor").getValue().toString()) - new Double(item.getItemProperty("valor_disponivel").getValue().toString());
						txtValorUtilizado.setValue(Real.formatDbToString(String.valueOf(valorUtilizado)));
					}
					
					txtValorUtilizado.setReadOnly(true);					
					JavaScript.getCurrent().execute("$('#txtValorUtilizado').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextField txtValorDisponivel = new TextField ("Valor Disponível");				
					txtValorDisponivel.setWidth("80px");				
					txtValorDisponivel.setStyleName("caption-align");
					txtValorDisponivel.setNullRepresentation("");			
					txtValorDisponivel.setRequired(true);
					txtValorDisponivel.setId("txtValorDisponivel");
					txtValorDisponivel.addStyleName("align-currency");
					
					addComponent(txtValorDisponivel);	
					
									
					if(item.getItemProperty("valor_disponivel").getValue() != null){
						txtValorDisponivel.setValue(Real.formatDbToString(item.getItemProperty("valor_disponivel").getValue().toString()));
					}
					
					txtValorDisponivel.setReadOnly(true);					
					JavaScript.getCurrent().execute("$('#txtValorUtilizado').maskMoney({decimal:',',thousands:'.'})");
				}
			});

		
		
	}
	

	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && ClienteSelecionado != null && txtValor.isValid()){
					try {				
						
						//item.getItemProperty("cliente").setValue(ClienteSelecionado);	
						item.getItemProperty("data_emissao").setValue(new Date());
						//item.getItemProperty("data_alteracao").setValue(new Date());
						item.getItemProperty("valor").setValue(Real.formatStringToDBDouble(txtValor.getValue()));
						//item.getItemProperty("valor_disponivel").setValue(Real.formatStringToDBDouble(txtValor.getValue()));
						item.getItemProperty("tipo").setValue("ENTRADA");
						item.getItemProperty("operador").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
						
						if(indicacao){						
							referencia = "INDICACAO: "+ClienteSelecionadoInd.getId()+"-"+ClienteSelecionadoInd.getNome_razao();													
							item.getItemProperty("referente").setValue(referencia);
							item.getItemProperty("n_doc").setValue(txtNdoc.getValue());
							item.getItemProperty("doc").setValue(cbDoc.getValue().toString());	
							//item.getItemProperty("data_alteracao").setValue(new Date());
						}
						
						fieldGroup.commit();				
						fireEvent(new HaverEvent(getUI(), item, ClienteSelecionado,true));					
						
					} catch (Exception e) {					
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(),Notify.TYPE_ERROR);
					}
				}else{
					
					for (Field<?> field: fieldGroup.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
					}
					
					if(txtValor.getValue().equals("") || txtValor.getValue().isEmpty() || txtValor.getValue().equals("")){
						txtValor.addStyleName("invalid-txt");
					}else{
						txtValor.removeStyleName("invalid-txt");
					}
					
					if(ClienteSelecionado == null){
						tfCodCliente.addStyleName("invalid-txt");
						tfDescricaoCliente.addStyleName("invalid-txt");
					}else{
						tfCodCliente.removeStyleName("invalid-txt");
						tfDescricaoCliente.removeStyleName("invalid-txt");
					}
					
					Notify.Show_Invalid_Submit_Form();
				}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}

	
	public Button buildBtCancelar(String lb) {
		btCancelar = new Button(lb, new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new HaverEvent(getUI(), item,ClienteSelecionado, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(fieldGroup.isValid() && ClienteSelecionado != null &&  txtValor.isValid()){
									try {	
										
										item.getItemProperty("cliente").setValue(ClienteSelecionado);	
										item.getItemProperty("data_emissao").setValue(new Date());
										item.getItemProperty("valor").setValue(Real.formatDbToString(txtValor.getValue()));
										item.getItemProperty("status").setValue("ABERTO");
										item.getItemProperty("data_alteracao").setValue(new Date());

										
										if(indicacao){
											referencia = "INDICACAO: "+ClienteSelecionadoInd.getId()+"-"+ClienteSelecionadoInd.getNome_razao();	
											item.getItemProperty("referente").setValue(referencia);
											item.getItemProperty("nDoc").setValue(txtNdoc.getValue());
											item.getItemProperty("doc").setValue(cbDoc.getValue().toString());	
											item.getItemProperty("data_alteracao").setValue(new Date());

											
										}
										
										fieldGroup.commit();				
										fireEvent(new HaverEvent(getUI(), item,ClienteSelecionado, true));					
										
									} catch (Exception e) {					
										e.printStackTrace();
										Notify.Show("ERRO: "+e.getLocalizedMessage(),Notify.TYPE_ERROR);
									}
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
							}else{							
								fieldGroup.discard();				
								fireEvent(new HaverEvent(getUI(), item,ClienteSelecionado, false));
								close();						
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		
		
		return btCancelar;
	}
	
	
	public void addListerner(HaverListerner target){
		try {
			Method method = HaverListerner.class.getDeclaredMethod("onClose", HaverEvent.class);
			addListener(HaverEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(HaverListerner target){
		removeListener(HaverEvent.class, target);
	}
	public static class HaverEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private Cliente cliente;
		
		public HaverEvent(Component source, Item item,Cliente cliente, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;
			this.cliente = cliente;
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
		public Cliente getCliente(){
			return cliente;
		}
	}
	public interface HaverListerner extends Serializable{
		public void onClose(HaverEvent event);
	}

	
}
