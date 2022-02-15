package com.digital.opuserp.view.modulos.ordemServico.assistenciaTecnica;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesOsiDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.EmpresaDAO;
import com.digital.opuserp.dao.OsiDAO;
import com.digital.opuserp.domain.AlteracoesAssistencia;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.ServicosItensOsi;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.EmailUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class AssistenciaTecnicaEditor extends Window implements GenericEditor {

	Item item;
	Button btFechar;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;	
		
	
	TextField tfCodCliente;
	TextField tfDescricaoCliente;
	Cliente ClienteSelecionado;
	
	TextField txtDDD1;
	TextField txtTelefone1;
	TextField txtDDD2;
	TextField txtTelefone2;
	TextField txtDDD3;
	TextField txtTelefone3;
	TextField txtDDD4;
	TextField txtTelefone4;
	
	private ComboBox cbEndereco;
	private TextField txtComplemento;
	private TextField txtReferencia;
	
	private TextField txtContato;
	private TextField txtEmail;
	
	private String ValorTotalItens;
	private Table tbItens;
	
	private boolean valid_email;
	private String emailInicial;

			
	
	public AssistenciaTecnicaEditor(final Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("864px");
		//setHeight("420px");
		
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
				
				if(item.getItemProperty("id").getValue() == null){
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtSalvar());
				}else{
					hlButtons.addComponent(buildBtOcorrencia());
					hlButtons.addComponent(buildBtFechar());
				}
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		if(item.getItemProperty("id").getValue() != null){
			buildLayoutReadlyOnly();
		}else{
			buildLayout();
		}
	}

	
	
	public String getNextId() {
		return OsiDAO.getNextID();
	}
	
	private JPAContainer<Endereco> getEnderecos(){
		JPAContainer<Endereco> container = JPAContainerFactory.make(Endereco.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("clientes", ClienteSelecionado));
		
		return container;
	}
	
	public void buildLayoutReadlyOnly(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						
						setMargin(true);
						setSpacing(true);
						
						TextField txtCod = new TextField("OS Número");
						txtCod.setStyleName("caption-align");
						
						
						addComponent(txtCod);
						
						fieldGroup.bind(txtCod, "id");
						txtCod.setReadOnly(true);
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						
						setMargin(true);
						setSpacing(true);
						
						TextField txtDataEntrada = new TextField("Entrada");
						txtDataEntrada.setStyleName("caption-align-assistencia-tecnica");
						
						addComponent(txtDataEntrada);
						
						java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						txtDataEntrada.setValue(sdf.format(item.getItemProperty("data_entrada").getValue()));
						txtDataEntrada.setReadOnly(true);
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
				tfCodCliente.focus();
				tfCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				tfCodCliente.setImmediate(true);
						
				
				
				tfCodCliente.addListener(new TextChangeListener() {
					
					@Override
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
								
								
								txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
								txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
								txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
								txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
								
								txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
								txtTelefone3.setValue(ClienteSelecionado.getCelular1());
								txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
								txtTelefone4.setValue(ClienteSelecionado.getCelular2());
								
								txtEmail.setValue(ClienteSelecionado.getEmail());
							
								
								txtContato.setValue(ClienteSelecionado.getContato().toUpperCase());
							}else {
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue("");
								tfDescricaoCliente.setReadOnly(true);
								
								
								txtDDD1.setValue("");
								txtTelefone1.setValue("");
								txtDDD2.setValue("");
								txtTelefone2.setValue("");
								
								txtDDD3.setValue("");
								txtTelefone3.setValue("");
								txtDDD4.setValue("");
								txtTelefone4.setValue("");
								
								txtEmail.setValue("");
								
								txtContato.setValue("");
								
							}
						}else{
							tfDescricaoCliente.setReadOnly(false);
							tfDescricaoCliente.setValue("");
							tfDescricaoCliente.setReadOnly(true);	
							
							txtDDD1.setValue("");
							txtTelefone1.setValue("");
							txtDDD2.setValue("");
							txtTelefone2.setValue("");
							
							txtDDD3.setValue("");
							txtTelefone3.setValue("");
							txtDDD4.setValue("");
							txtTelefone4.setValue("");
							
							txtEmail.setValue("");
							txtContato.setValue("");


							
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
					ClienteSelecionado = eDAO.getCliente(((Cliente)item.getItemProperty("cliente").getValue()).getId());
					
					if(ClienteSelecionado != null){
						tfCodCliente.setValue(ClienteSelecionado.getId().toString());
						tfCodCliente.setReadOnly(true);
						
						tfDescricaoCliente.setReadOnly(false);
						tfDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
						tfDescricaoCliente.setReadOnly(true);
						
						
					}
				}
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setTabIndex(300000);
				btSearchCliente.setEnabled(false);
				btSearchCliente.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						ClienteUtil cUtil = new ClienteUtil(true, true,"INATIVO");
						cUtil.addListerner(new ClienteUtil.ClienteListerner() {
							
							@Override
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
									if(event.getCliente() != null){
										tfCodCliente.setValue(event.getCliente().getId().toString());
										tfDescricaoCliente.setReadOnly(false);
										tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
										tfDescricaoCliente.setReadOnly(true);
										ClienteSelecionado = event.getCliente();
										
										
										txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
										txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
										txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
										txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
										
										txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
										txtTelefone3.setValue(ClienteSelecionado.getCelular1());
										txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
										txtTelefone4.setValue(ClienteSelecionado.getCelular2());
										
										if(ClienteSelecionado.getEmail() != null && !ClienteSelecionado.getEmail().equals("")){
											if(EmailUtil.validate(ClienteSelecionado.getEmail())){
												valid_email = true;
											}else{
												valid_email = false;
											}
											
											txtEmail.setValue(ClienteSelecionado.getEmail());	
										}									
																				
										txtContato.setValue(ClienteSelecionado.getContato().toUpperCase());										
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
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtContato = new TextField ("Contato");								
					txtContato.setStyleName("caption-align");
					txtContato.setId("txtBoleto");
					txtContato.setNullRepresentation("");					
					txtContato.setWidth("457px");
									
					addComponent(txtContato);
					fieldGroup.bind(txtContato, "contato");
					txtContato.setReadOnly(true);
				
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtEndereco = new TextField ("Endereço");								
					txtEndereco.setStyleName("caption-align");					
					txtEndereco.setNullRepresentation("");					
					txtEndereco.setWidth("457px");
									
					addComponent(txtEndereco);
					
					Endereco end = (Endereco)item.getItemProperty("end").getValue();
					if(end != null){
						txtEndereco.setValue(end.getCep()+" "+end.getEndereco()+", "+end.getNumero()+" "+end.getBairro()+", "+end.getCidade());
					}
					txtEndereco.setReadOnly(true);
				
				}
		});
		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtComplemento = new TextField("Complemento");
						txtComplemento.setNullRepresentation("");
						txtComplemento.setStyleName("caption-align");
						txtComplemento.setWidth("284px");
						txtComplemento.setImmediate(true);
						txtComplemento.setTextChangeEventMode(TextChangeEventMode.LAZY);
						txtComplemento.setRequired(true);
						txtComplemento.setDescription("Casa, Apto, Galpão, Anexo, etc");						
						
						addComponent(txtComplemento);	
						
						Endereco end = (Endereco)item.getItemProperty("end").getValue();
						if(end != null){
							txtComplemento.setValue(end.getComplemento());
						}
						txtComplemento.setReadOnly(true);
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtReferencia = new TextField("Referência");
						txtReferencia.setNullRepresentation("");
						txtReferencia.setWidth("284px");
						txtReferencia.setImmediate(true);
						txtReferencia.setTextChangeEventMode(TextChangeEventMode.LAZY);
						
						addComponent(txtReferencia);
						
						Endereco end = (Endereco)item.getItemProperty("end").getValue();
						if(end != null){
							txtReferencia.setValue(end.getReferencia());
						}
						txtReferencia.setReadOnly(true);
					}
				});
			}
		});
			
		// Telefone 1,2
		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						txtDDD1 = new TextField("Telefone Principal");
						txtDDD1.setNullRepresentation("");
						txtDDD1.setStyleName("telefone");
						txtDDD1.addStyleName("caption-align");
						txtDDD1.setRequired(true);
						txtDDD1.setMaxLength(2);
						txtDDD1.setWidth("40px");
						
						if(ClienteSelecionado != null){
							txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
							txtDDD1.setReadOnly(true);
						}

						addComponent(txtDDD1);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom-new_hide_require");
						
						txtTelefone1 = new TextField();
						addComponent(txtTelefone1);
						txtTelefone1.setNullRepresentation("");
						txtTelefone1.setWidth("105px");
						txtTelefone1.setMaxLength(11);
						txtTelefone1.setRequired(true);
						
						if(ClienteSelecionado != null){
							txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
							txtTelefone1.setReadOnly(true);
						}
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");

						txtDDD2 = new TextField("Telefone Alternativo");
						txtDDD2.setNullRepresentation("");
						txtDDD2.setWidth("40px");
						txtDDD2.setMaxLength(2);
						
						if(ClienteSelecionado != null){
							txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
							txtDDD2.setReadOnly(true);
						}

						addComponent(txtDDD2);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom-new");
						
						txtTelefone2 = new TextField();
						addComponent(txtTelefone2);
						txtTelefone2.setNullRepresentation("");
						txtTelefone2.setWidth("105px");
						txtTelefone2.setMaxLength(11);
						
						if(ClienteSelecionado != null){
							txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
							txtTelefone2.setReadOnly(true);
						}

					}
				});

			}
		});
		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom");
						txtDDD3 = new TextField("Telefone Alternativo");
						txtDDD3.setNullRepresentation("");
						txtDDD3.setStyleName("telefone");
						txtDDD3.addStyleName("caption-align");
						txtDDD3.setRequired(false);
						txtDDD3.setMaxLength(2);
						txtDDD3.setWidth("40px");
						
						if(ClienteSelecionado != null){
							txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
							txtDDD3.setReadOnly(true);
						}

						addComponent(txtDDD3);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom_hide_require");
						
						txtTelefone3 = new TextField();
						addComponent(txtTelefone3);
						txtTelefone3.setNullRepresentation("");
						txtTelefone3.setWidth("105px");
						txtTelefone3.setMaxLength(11);
						txtTelefone3.setRequired(true);
						
						if(ClienteSelecionado != null){
							txtTelefone3.setValue(ClienteSelecionado.getCelular1());
							txtTelefone3.setReadOnly(true);
						}
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");

						txtDDD4 = new TextField("Telefone Alternativo");
						txtDDD4.setNullRepresentation("");
						txtDDD4.setWidth("40px");
						txtDDD4.setMaxLength(2);

						if(ClienteSelecionado != null){
							txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
							txtDDD4.setReadOnly(true);
						}
						
						addComponent(txtDDD4);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom");
						
						txtTelefone4 = new TextField();
						addComponent(txtTelefone4);
						txtTelefone4.setNullRepresentation("");
						txtTelefone4.setWidth("105px");
						txtTelefone4.setMaxLength(11);

						if(ClienteSelecionado != null){
							txtTelefone4.setValue(ClienteSelecionado.getCelular2());
							txtTelefone4.setReadOnly(true);
						}
					}
				});

			}
		});

		vlRoot.addComponent(
				new HorizontalLayout(){
					{
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																	
								txtEmail = new TextField ("E-mail");								
								txtEmail.setStyleName("caption-align");
								txtEmail.setId("txtBoleto");
								txtEmail.setNullRepresentation("");					
								txtEmail.setWidth("420px");
								
								txtEmail.addTextChangeListener(new FieldEvents.TextChangeListener() {
									
									@Override
									public void textChange(TextChangeEvent event) {
										
										if(event.getText() != null && !event.getText().equals("") && !event.getText().equals(emailInicial)){
											if(EmailUtil.validate(event.getText()) && !event.getText().equals(emailInicial)){
																		
												if(ClienteDAO.checkEmailExists(event.getText()) && !event.getText().equals(emailInicial)){
													event.getComponent().addStyleName("invalid-cpf");
													valid_email = false;
												}else{
													event.getComponent().removeStyleName("invalid-cpf");
													valid_email = true;	
												}
											}else{
												
												if(!EmailUtil.validate(event.getText())){
													event.getComponent().addStyleName("invalid-cpf");
													valid_email = false;
												}else{
													event.getComponent().removeStyleName("invalid-cpf");
													valid_email = true;
												}
											}
										}
										
									}
								});
								
												
								addComponent(txtEmail);
								if(ClienteSelecionado != null ){
									txtEmail.setValue(ClienteSelecionado.getEmail());						
								}
								txtEmail.setReadOnly(true);
							}
					});
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																	
								CheckBox chk = new CheckBox("Não possui e-mail");
								
								chk.addValueChangeListener(new Property.ValueChangeListener() {
									
									@Override
									public void valueChange(ValueChangeEvent event) {
										((TextField) fieldGroup.getField("email")).setRequired(!((TextField) fieldGroup.getField("email")).isRequired());
										if(!((TextField) fieldGroup.getField("email")).isRequired()){
											((TextField) fieldGroup.getField("email")).setValue("");
										}
										
										if(((TextField) fieldGroup.getField("email")).getValue() == null || 
												((TextField) fieldGroup.getField("email")).getValue().isEmpty()){
											
//											if(((TextField) fieldGroup.getField("email")).isRequired()){
//												emailValid = false;
//											}else{
//												emailValid = true;
//												((TextField) fieldGroup.getField("email")).removeStyleName("invalid-txt");
//											}
										
										}
									}
								});
								
								addComponent(chk); 
							}
						});
					}
				});
				
				
			
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextField txtEquipamento = new TextField ("Equipamento");								
					txtEquipamento.setStyleName("caption-align");
					txtEquipamento.setId("txtBoleto");
					txtEquipamento.setNullRepresentation("");
					txtEquipamento.setWidth("654px");
					txtEquipamento.setRequired(true);
					txtEquipamento.addStyleName("uppercase");
					
					addComponent(txtEquipamento);
					fieldGroup.bind(txtEquipamento, "equipamento");
					txtEquipamento.setReadOnly(true);									
				
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtAcessorios = new TextField ("Acessórios");								
					txtAcessorios.setStyleName("caption-align");
					txtAcessorios.setId("txtBoleto");
					txtAcessorios.setNullRepresentation("");
					txtAcessorios.setWidth("654px");
					txtAcessorios.addStyleName("uppercase");
										
					addComponent(txtAcessorios);
					fieldGroup.bind(txtAcessorios, "acessorios");
					txtAcessorios.setReadOnly(true);
				
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextArea txtObservacao = new TextArea ("Observação:");								
					txtObservacao.setStyleName("caption-align");
					txtObservacao.setId("txtBoleto");
					txtObservacao.setNullRepresentation("");
					txtObservacao.setHeight("40px");
					txtObservacao.setWidth("654px");
					txtObservacao.addStyleName("uppercase");
					
					addComponent(txtObservacao);
					fieldGroup.bind(txtObservacao, "observacao");
					txtObservacao.setReadOnly(true);
				
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtOcorrencia = new TextField ("Ultima Ocorrência:");								
					txtOcorrencia.setStyleName("caption-align");
					txtOcorrencia.setId("txtBoleto");
					txtOcorrencia.setNullRepresentation("");					
					txtOcorrencia.setWidth("654px");
					txtOcorrencia.addStyleName("uppercase");
					
					List<AlteracoesAssistencia> alteracoes = AlteracoesOsiDAO.getAltercoesAssistencia((Integer)item.getItemProperty("id").getValue());
					if(alteracoes.size() > 0){
						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						String data = sdf.format(((AlteracoesAssistencia)alteracoes.get(alteracoes.size()-1)).getData_alteracao());
						txtOcorrencia.setValue(data+" - "+
								((AlteracoesAssistencia)alteracoes.get(alteracoes.size()-1)).getOperador().getUsername()+" - "+((AlteracoesAssistencia)alteracoes.get(alteracoes.size()-1)).getTipo());
					}
					
					addComponent(txtOcorrencia);					
					txtOcorrencia.setReadOnly(true);
				
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextArea txtConclusao = new TextArea ("Laudo:");								
					txtConclusao.setStyleName("caption-align");
					txtConclusao.setId("txtBoleto");
					txtConclusao.setNullRepresentation("");
					txtConclusao.setHeight("40px");
					txtConclusao.setWidth("654px");
					txtConclusao.addStyleName("uppercase");
					
					addComponent(txtConclusao);
					fieldGroup.bind(txtConclusao, "conclusao");
					txtConclusao.setReadOnly(true);
				
				}
		});
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextArea txtPecasSubstituidas = new TextArea ("Peças Substituídas:");								
					txtPecasSubstituidas.setStyleName("caption-align");
					txtPecasSubstituidas.setId("txtBoleto");
					txtPecasSubstituidas.setNullRepresentation("");
					txtPecasSubstituidas.setHeight("20px");
					txtPecasSubstituidas.setWidth("654px");
					txtPecasSubstituidas.addStyleName("uppercase");
					
					addComponent(txtPecasSubstituidas);
					fieldGroup.bind(txtPecasSubstituidas, "pecas_subs");
					txtPecasSubstituidas.setReadOnly(true);
				
				}
		});
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					tbItens = new Table("Serviços"){
						@Override
						protected String formatPropertyValue(Object rowId, Object colId,
								Property<?> property) {
							
							if(colId.equals("VALOR") && tbItens.getItem(rowId).getItemProperty(colId).getValue() != null){
								
								
								return "R$ "+Real.formatDbToString(tbItens.getItem(rowId).getItemProperty(colId).getValue().toString());
							}				
							
						
							
							return super.formatPropertyValue(rowId, colId, property);
						
							
						}
					};
					tbItens.addContainerProperty("COD", Integer.class, null);
					tbItens.addContainerProperty("NOME", String.class, null);
					tbItens.addContainerProperty("VALOR", String.class, null);
					tbItens.setWidth("650px");
					tbItens.setHeight("80px");
					tbItens.addStyleName("tb-itens");
					tbItens.addStyleName("caption-align");
					
					tbItens.setColumnAlignment("VALOR", Align.RIGHT);

					
					List<ServicosItensOsi> itens = OsiDAO.getServicos((Integer)item.getItemProperty("id").getValue());
					double valorTotal = 0;
					for (ServicosItensOsi s : itens) {
						tbItens.addItem(new Object[]{s.getServico().getId(), s.getServico().getNome(), s.getServico().getValor_venda()}, tbItens.getItemIds().size()+1);
						
						valorTotal = valorTotal+Double.parseDouble(s.getServico().getValor_venda());
					}
					
					ValorTotalItens = Real.formatDbToString(String.valueOf(valorTotal));
					addComponent(tbItens);
					
				
				}
		});		
		
	
		vlRoot.addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
															
						TextField txtValorTotal = new TextField ("Total (R$)");								
						txtValorTotal.setStyleName("caption-align");
						txtValorTotal.setId("txtValorTotal");
						txtValorTotal.setValue(ValorTotalItens);
						
						JavaScript.getCurrent().execute("$('#txtValorTotal').maskMoney({decimal:',',thousands:'.'})");
						
						addComponent(txtValorTotal);
						
						
						
						
						
					}
				});			
				
			}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
															
						DateField dfDataAgendamento = new DateField ("Data Previsão");								
						dfDataAgendamento.setStyleName("caption-align");
						dfDataAgendamento.setDateFormat("dd/MM/yyyy");					
						dfDataAgendamento.setRequired(true);
						dfDataAgendamento.setWidth("120px");
						
						addComponent(dfDataAgendamento);
						fieldGroup.bind(dfDataAgendamento, "data_agendamento");
						dfDataAgendamento.setReadOnly(true);
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
															
						DateField dfDataConclusao = new DateField ("Data Conclusão");							
						dfDataConclusao.setDateFormat("dd/MM/yyyy HH:mm:ss");				
						dfDataConclusao.setResolution(DateField.RESOLUTION_HOUR);
						dfDataConclusao.setResolution(DateField.RESOLUTION_MIN);
						dfDataConclusao.setResolution(DateField.RESOLUTION_SEC);
						dfDataConclusao.setRequired(true);
						dfDataConclusao.setWidth("120px");
						dfDataConclusao.setStyleName("caption-align");
						
						addComponent(dfDataConclusao);
						fieldGroup.bind(dfDataConclusao, "data_conclusao");
						dfDataConclusao.setReadOnly(true);
					}
				});
				
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
															
						DateField dfDataEntrega = new DateField ("Data Entrega");						
						dfDataEntrega.setDateFormat("dd/MM/yyyy HH:mm:ss");			
						dfDataEntrega.setResolution(DateField.RESOLUTION_HOUR);
						dfDataEntrega.setResolution(DateField.RESOLUTION_MIN);
						dfDataEntrega.setResolution(DateField.RESOLUTION_SEC);
						dfDataEntrega.setRequired(true);
						dfDataEntrega.setWidth("120px");
						dfDataEntrega.setStyleName("caption-align-assistencia-tecnica-2");
						
						addComponent(dfDataEntrega);
						fieldGroup.bind(dfDataEntrega, "data_entrega");
						dfDataEntrega.setReadOnly(true);
					}
				});
				
			}
		});
	
	
	
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
				

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
				tfCodCliente.focus();
				tfCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				tfCodCliente.setImmediate(true);
						
				
				
				tfCodCliente.addListener(new TextChangeListener() {
					
					@Override
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
								
								
								txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
								txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
								txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
								txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
								
								txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
								txtTelefone3.setValue(ClienteSelecionado.getCelular1());
								txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
								txtTelefone4.setValue(ClienteSelecionado.getCelular2());
								
								txtEmail.setValue(ClienteSelecionado.getEmail());
								valid_email =true;
								emailInicial = ClienteSelecionado.getEmail();
								txtContato.setValue(ClienteSelecionado.getContato().toUpperCase());
								
								cbEndereco.setContainerDataSource(getEnderecos());
								fieldGroup.bind(cbEndereco, "end");
							}else {
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue("");
								tfDescricaoCliente.setReadOnly(true);
								
								
								txtDDD1.setValue("");
								txtTelefone1.setValue("");
								txtDDD2.setValue("");
								txtTelefone2.setValue("");
								
								txtDDD3.setValue("");
								txtTelefone3.setValue("");
								txtDDD4.setValue("");
								txtTelefone4.setValue("");
								
								txtEmail.setValue("");
								valid_email =true;
								txtContato.setValue("");
								emailInicial = "";
								
								cbEndereco.setContainerDataSource(null);
								
								if(fieldGroup.getField("end") != null){
									fieldGroup.unbind(cbEndereco);
								}
								
							}
						}else{
							tfDescricaoCliente.setReadOnly(false);
							tfDescricaoCliente.setValue("");
							tfDescricaoCliente.setReadOnly(true);	
							
							txtDDD1.setValue("");
							txtTelefone1.setValue("");
							txtDDD2.setValue("");
							txtTelefone2.setValue("");
							
							txtDDD3.setValue("");
							txtTelefone3.setValue("");
							txtDDD4.setValue("");
							txtTelefone4.setValue("");
							txtEmail.setValue("");
							valid_email =true;
							txtContato.setValue("");
							emailInicial = "";


							if(fieldGroup.getField("end") != null){
								fieldGroup.unbind(cbEndereco);
							}
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
					
					@Override
					public void buttonClick(ClickEvent event) {
						ClienteUtil cUtil = new ClienteUtil(true, true,"INATIVO");
						cUtil.addListerner(new ClienteUtil.ClienteListerner() {
							
							@Override
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
									if(event.getCliente() != null){
										tfCodCliente.setValue(event.getCliente().getId().toString());
										tfDescricaoCliente.setReadOnly(false);
										tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
										tfDescricaoCliente.setReadOnly(true);
										ClienteSelecionado = event.getCliente();
										
										
										txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
										txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
										txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
										txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
										
										txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
										txtTelefone3.setValue(ClienteSelecionado.getCelular1());
										txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
										txtTelefone4.setValue(ClienteSelecionado.getCelular2());
										
										emailInicial = ClienteSelecionado.getEmail();

										if(ClienteSelecionado.getContato() != null){
											txtContato.setValue(ClienteSelecionado.getContato().toUpperCase());
										}else{
											txtContato.setValue("");
										}
										
										if(ClienteSelecionado.getEmail() != null && !ClienteSelecionado.getEmail().equals("")){
											if(EmailUtil.validate(ClienteSelecionado.getEmail())){
												valid_email = true;
											}else{
												valid_email = false;
											}
											
											txtEmail.setValue(ClienteSelecionado.getEmail());	
										}	
										
										cbEndereco.setContainerDataSource(getEnderecos());
										fieldGroup.bind(cbEndereco, "end");
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
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtContato = new TextField ("Contato");								
					txtContato.setStyleName("caption-align");
					txtContato.setId("txtBoleto");
					txtContato.setNullRepresentation("");					
					txtContato.setWidth("420px");
									
					addComponent(txtContato);
					fieldGroup.bind(txtContato, "contato");
				
				}
		});
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
																											
					cbEndereco = new ComboBox("Endereços"){
						@Override
						public String getItemCaption(Object itemId) {
						   Item item = getItem(itemId);
						
						   if (item == null) {
						      return "";
						   }
						     
						   return String.valueOf(item.getItemProperty("cep")) + ", " + 
					   		  String.valueOf(item.getItemProperty("endereco")) + ", " +
					   		  String.valueOf(item.getItemProperty("numero")) + ", " +
					   		  String.valueOf(item.getItemProperty("bairro")) + ", " +
					   		  String.valueOf(item.getItemProperty("cidade"))+ " - " +
					   		  String.valueOf(item.getItemProperty("uf"));
						}
					};
					
					cbEndereco.setWidth("654px");
					cbEndereco.setRequired(true);
					cbEndereco.setNullSelectionAllowed(false);
					cbEndereco.setStyleName("caption-align");	
					cbEndereco.setImmediate(true);
					cbEndereco.setTextInputAllowed(false);
					
					cbEndereco.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(cbEndereco.getValue() != null && txtComplemento != null && txtReferencia != null){
								
								if(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue() != null){
									txtComplemento.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
								}else{
									txtComplemento.setValue("");
								}
																
								if(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue() != null){
									txtReferencia.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue().toString());
								}else{
									txtReferencia.setValue("");
								}
								
								
							}else{
								if(txtComplemento != null && txtReferencia != null){
									txtReferencia.setValue("");
									txtComplemento.setValue("");
								}
							}
						}
					});
									
					addComponent(cbEndereco);
					
					if(ClienteSelecionado != null && item.getItemProperty("end").getValue() != null){
						cbEndereco.setContainerDataSource(getEnderecos());
						cbEndereco.setConverter(new SingleSelectConverter(cbEndereco));
						fieldGroup.bind(cbEndereco, "end"); 
					}
					
					
									
				}
		});
		
		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtComplemento = new TextField("Complemento");
						txtComplemento.setNullRepresentation("");
						txtComplemento.setStyleName("caption-align");
						txtComplemento.setWidth("285px");
						txtComplemento.setImmediate(true);
						txtComplemento.setTextChangeEventMode(TextChangeEventMode.LAZY);
						txtComplemento.setRequired(true);
						txtComplemento.setDescription("Casa, Apto, Galpão, Anexo, etc");						
						
						addComponent(txtComplemento);	
						
						if(cbEndereco.getValue() != null){
							txtComplemento.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
						}
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtReferencia = new TextField("Referência");
						txtReferencia.setNullRepresentation("");
						txtReferencia.setWidth("284px");
						txtReferencia.setImmediate(true);
						txtReferencia.setTextChangeEventMode(TextChangeEventMode.LAZY);
						
						addComponent(txtReferencia);
						
						if(cbEndereco.getValue() != null){
							txtReferencia.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
						}
					}
				});
			}
		});
			
		// Telefone 1,2
		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						txtDDD1 = new TextField("Telefone Principal");
						txtDDD1.setNullRepresentation("");
						txtDDD1.setStyleName("telefone");
						txtDDD1.addStyleName("caption-align");
						txtDDD1.setRequired(true);
						txtDDD1.setMaxLength(2);
						txtDDD1.setWidth("40px");

						addComponent(txtDDD1);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom-new_hide_require");
						
						txtTelefone1 = new TextField();
						addComponent(txtTelefone1);
						txtTelefone1.setNullRepresentation("");
						txtTelefone1.setWidth("105px");
						txtTelefone1.setMaxLength(11);
						txtTelefone1.setRequired(true);
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");

						txtDDD2 = new TextField("Telefone Alternativo");
						txtDDD2.setNullRepresentation("");
						txtDDD2.setWidth("40px");
						txtDDD2.setMaxLength(2);

						addComponent(txtDDD2);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom-new");
						
						txtTelefone2 = new TextField();
						addComponent(txtTelefone2);
						txtTelefone2.setNullRepresentation("");
						txtTelefone2.setWidth("105px");
						txtTelefone2.setMaxLength(11);

					}
				});

			}
		});
		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom");
						txtDDD3 = new TextField("Telefone Alternativo");
						txtDDD3.setNullRepresentation("");
						txtDDD3.setStyleName("telefone");
						txtDDD3.addStyleName("caption-align");
						txtDDD3.setRequired(false);
						txtDDD3.setMaxLength(2);
						txtDDD3.setWidth("40px");

						addComponent(txtDDD3);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom_hide_require");
						
						txtTelefone3 = new TextField();
						addComponent(txtTelefone3);
						txtTelefone3.setNullRepresentation("");
						txtTelefone3.setWidth("105px");
						txtTelefone3.setMaxLength(11);
						txtTelefone3.setRequired(true);
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");

						txtDDD4 = new TextField("Telefone Alternativo");
						txtDDD4.setNullRepresentation("");
						txtDDD4.setWidth("40px");
						txtDDD4.setMaxLength(2);

						addComponent(txtDDD4);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom");
						
						txtTelefone4 = new TextField();
						addComponent(txtTelefone4);
						txtTelefone4.setNullRepresentation("");
						txtTelefone4.setWidth("105px");
						txtTelefone4.setMaxLength(11);

					}
				});

			}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtEmail = new TextField ("E-mail");								
					txtEmail.setStyleName("caption-align");
					txtEmail.setId("txtBoleto");
					txtEmail.setNullRepresentation("");					
					txtEmail.setWidth("420px");
					
					txtEmail.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
							if(event.getText() != null && !event.getText().equals("") && !event.getText().equals(emailInicial)){
								if(EmailUtil.validate(event.getText()) && !event.getText().equals(emailInicial)){
															
									if(ClienteDAO.checkEmailExists(event.getText()) && !event.getText().equals(emailInicial)){
										event.getComponent().addStyleName("invalid-cpf");
										valid_email = false;
									}else{
										event.getComponent().removeStyleName("invalid-cpf");
										valid_email = true;	
									}
								}else{
									
									if(!EmailUtil.validate(event.getText())){
										event.getComponent().addStyleName("invalid-cpf");
										valid_email = false;
									}else{
										event.getComponent().removeStyleName("invalid-cpf");
										valid_email = true;
									}
								}
							}else{
								if(EmailUtil.validate(event.getText())){
									event.getComponent().removeStyleName("invalid-cpf");
									valid_email = true;
								}
							}
							
						}
					});
					
					txtEmail.addBlurListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
							if(!valid_email){
								Notify.Show("E-mail Inválido!", Notify.TYPE_ERROR);
							}
						}
					});
					
									
					addComponent(txtEmail);					
				}
		});

		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextField txtEquipamento = new TextField ("Equipamento");								
					txtEquipamento.setStyleName("caption-align");
					txtEquipamento.setId("txtBoleto");
					txtEquipamento.setNullRepresentation("");
					txtEquipamento.setWidth("654px");
					txtEquipamento.setRequired(true);
					txtEquipamento.addStyleName("uppercase");
														
					addComponent(txtEquipamento);
					fieldGroup.bind(txtEquipamento, "equipamento");
				
				}
		});
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtAcessorios = new TextField ("Acessórios");								
					txtAcessorios.setStyleName("caption-align");
					txtAcessorios.setId("txtBoleto");
					txtAcessorios.setNullRepresentation("");
					txtAcessorios.setWidth("654px");
					txtAcessorios.addStyleName("uppercase");
										
					addComponent(txtAcessorios);
					fieldGroup.bind(txtAcessorios, "acessorios");
				
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextArea txtObservacao = new TextArea ("Obs.:");								
					txtObservacao.setStyleName("caption-align");
					txtObservacao.setId("txtBoleto");
					txtObservacao.setNullRepresentation("");
					txtObservacao.setHeight("80px");
					txtObservacao.setWidth("654px");
					txtObservacao.addStyleName("uppercase");
					
					addComponent(txtObservacao);
					fieldGroup.bind(txtObservacao, "observacao");
				
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					DateField dfDataAgendamento = new DateField ("Data Agendamento");								
					dfDataAgendamento.setStyleName("caption-align");
					dfDataAgendamento.setDateFormat("dd/MM/yyyy");					
					dfDataAgendamento.setRequired(true);
					
					
					addComponent(dfDataAgendamento);
					fieldGroup.bind(dfDataAgendamento, "data_agendamento");
				
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					ComboBox cbEntregar = new ComboBox ("Entregar");								
					cbEntregar.setStyleName("caption-align");
					cbEntregar.setNullSelectionAllowed(false);
					cbEntregar.setTextInputAllowed(false); 
					cbEntregar.setRequired(true);
					cbEntregar.addItem("SIM");
					cbEntregar.addItem("NÃO");
										
					addComponent(cbEntregar);
					fieldGroup.bind(cbEntregar, "entregar");
				
				}
		});
		
	
	
	
	}
	private Button btOcorrencias;
	public Button buildBtOcorrencia(){
		btOcorrencias = new Button("Ocorrências", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				HistoricoAlteracoes historicoAlteracoes = new HistoricoAlteracoes(true, true, (Integer)item.getItemProperty("id").getValue());
				getUI().addWindow(historicoAlteracoes);				
			}
		});
		
		return btOcorrencias;
	}

	public Button buildBtFechar(){
		btFechar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		return btFechar;
	}
	
	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

			
				if(txtEmail.getValue() == null || txtEmail.getValue().isEmpty() || txtEmail.getValue() == ""){
					valid_email = true;
				}else{
					if(EmailUtil.validate(txtEmail.getValue())){
						valid_email = true;
					}else{
						valid_email = false;
					}
				}
									
				if(fieldGroup.isValid() && valid_email && ClienteSelecionado != null && txtDDD1.isValid() && txtTelefone1.isValid()){
					try {		
						
						ClienteSelecionado.setDdd_fone1(txtDDD1.getValue());
						ClienteSelecionado.setDdd_fone2(txtDDD2.getValue());
						ClienteSelecionado.setTelefone1(txtTelefone1.getValue());
						ClienteSelecionado.setTelefone2(txtTelefone2.getValue());
						ClienteSelecionado.setDdd_cel1(txtDDD3.getValue());
						ClienteSelecionado.setDdd_cel2(txtDDD4.getValue());
						ClienteSelecionado.setCelular1(txtTelefone3.getValue());
						ClienteSelecionado.setCelular2(txtTelefone4.getValue());
						ClienteSelecionado.setContato(txtContato.getValue());
						ClienteSelecionado.setEmail(txtEmail.getValue());
						ClienteSelecionado.setContato(txtContato.getValue());
						
						EntityItem<Endereco> eiEnd = (EntityItem<Endereco>)((ComboBox)fieldGroup.getField("end")).getItem(((ComboBox)fieldGroup.getField("end")).getValue());
						fieldGroup.unbind(((ComboBox)fieldGroup.getField("end")));
						fieldGroup.commit();	
						
						item.getItemProperty("end").setValue(eiEnd.getEntity());
						
						Endereco end = (Endereco)item.getItemProperty("end").getValue();
						end.setComplemento(txtComplemento.getValue());
						end.setReferencia(txtReferencia.getValue());
						
						item.getItemProperty("end").setValue(end);						
						item.getItemProperty("cliente").setValue(ClienteSelecionado);
						fireEvent(new OsiEvent(getUI(), item, true));
						

					} catch (Exception e) {											
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
					}
				}else{
					
					    for (Field<?> field: fieldGroup.getFields()) {						
							if(!field.isValid()){
								field.addStyleName("invalid-txt");
							}else{
								field.removeStyleName("invalid-txt");
							}
					    }
					    
					    if(!valid_email){
							txtEmail.addStyleName("invalid-txt");
						}else{
							txtEmail.removeStyleName("invalid-txt");
						}
					    
					    if(!tfCodCliente.isValid()){
					    	tfCodCliente.addStyleName("invalid-txt");
					    }else{
					    	tfCodCliente.removeStyleName("invalid-txt");  	
					    }	
					    
					    if(!txtDDD1.isValid()){
					    	txtDDD1.addStyleName("invalid-txt");
					    }else{
					    	txtDDD1.removeStyleName("invalid-txt");  	
					    }	
					    
					    if(!txtTelefone1.isValid()){
					    	txtTelefone1.addStyleName("invalid-txt");
					    }else{
					    	txtTelefone1.removeStyleName("invalid-txt");  	
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
		btSalvar.setStyleName("default");
		//btSalvar.setEnabled(false);
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new OsiEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								try {
									
									if(txtEmail.getValue() == null || txtEmail.getValue().isEmpty() || txtEmail.getValue() == ""){
										valid_email = true;
									}
									
									if(fieldGroup.isValid() && valid_email && ClienteSelecionado != null && txtDDD1.isValid() && txtTelefone1.isValid()){							
										ClienteSelecionado.setDdd_fone1(txtDDD1.getValue());
										ClienteSelecionado.setDdd_fone2(txtDDD2.getValue());
										ClienteSelecionado.setTelefone1(txtTelefone1.getValue());
										ClienteSelecionado.setTelefone2(txtTelefone2.getValue());
										ClienteSelecionado.setDdd_cel1(txtDDD3.getValue());
										ClienteSelecionado.setDdd_cel2(txtDDD4.getValue());
										ClienteSelecionado.setCelular1(txtTelefone3.getValue());
										ClienteSelecionado.setCelular2(txtTelefone4.getValue());
										ClienteSelecionado.setContato(txtContato.getValue());
										ClienteSelecionado.setEmail(txtEmail.getValue());
										ClienteSelecionado.setContato(txtContato.getValue());
										
										fieldGroup.commit();		
										
										Endereco end = (Endereco)item.getItemProperty("end").getValue();
										end.setComplemento(txtComplemento.getValue());
										end.setReferencia(txtReferencia.getValue());
										
										item.getItemProperty("end").setValue(end);						
										item.getItemProperty("cliente").setValue(ClienteSelecionado);
										fireEvent(new OsiEvent(getUI(), item, true));
										
									}else{
										for (Field<?> field: fieldGroup.getFields()) {						
											if(!field.isValid()){
												field.addStyleName("invalid-txt");
											}else{
												field.removeStyleName("invalid-txt");
											}
									    }
										
										 if(!valid_email){
												txtEmail.addStyleName("invalid-txt");
											}else{
												txtEmail.removeStyleName("invalid-txt");
											}
									    
									    if(!tfCodCliente.isValid()){
									    	tfCodCliente.addStyleName("invalid-txt");
									    }else{
									    	tfCodCliente.removeStyleName("invalid-txt");  	
									    }	
									    
									    if(!txtDDD1.isValid()){
									    	txtDDD1.addStyleName("invalid-txt");
									    }else{
									    	txtDDD1.removeStyleName("invalid-txt");  	
									    }	
									    
									    if(!txtTelefone1.isValid()){
									    	txtTelefone1.addStyleName("invalid-txt");
									    }else{
									    	txtTelefone1.removeStyleName("invalid-txt");  	
									    }	

										Notify.Show_Invalid_Submit_Form();
									}
								
								
								} catch (Exception e) {				
									e.printStackTrace();
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new TransportadoraEvent(getUI(), item, false));
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
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
	
	
	public void addListerner(OsiListerner target){
		try {
			Method method = OsiListerner.class.getDeclaredMethod("onClose", OsiEvent.class);
			addListener(OsiEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(OsiListerner target){
		removeListener(OsiEvent.class, target);
	}
	public static class OsiEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private Date dataPrimeiroBoleto;
		private String valorPrimeiroBoleto;
		
		public OsiEvent(Component source, Item item, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;
			this.dataPrimeiroBoleto = dataPrimeiroBoleto;
			this.valorPrimeiroBoleto = valorPrimeiroBoleto;
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}	
		
	}
	public interface OsiListerner extends Serializable{
		public void onClose(OsiEvent event);
	}
	
	
	




}
