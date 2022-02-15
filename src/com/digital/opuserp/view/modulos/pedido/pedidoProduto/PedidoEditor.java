package com.digital.opuserp.view.modulos.pedido.pedidoProduto;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.EcfPreVendaCabecalhoDAO;
import com.digital.opuserp.dao.EmpresaUsuarioDAO;
import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.dao.NaturezaOperacaoDAO;
import com.digital.opuserp.dao.ParametizacaoDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.dao.TotaisPedidoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.EmpresasUsuario;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.NaturezaOperacao;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.domain.TipoItemProduto;
import com.digital.opuserp.domain.TotaisPedido;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.NaturezaOperacaoUtil;
import com.digital.opuserp.view.util.NaturezaOperacaoUtil.NaturezaUtilEvent;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.PedidoUtil2;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class PedidoEditor extends Window implements GenericEditor {

	Item item;
	
	private Button btSalvar;
	private Button btItens;
	private Button btCancelar;
	private Button btFechar;
	Label lbTotaisItens;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private TextField txtCodNatureza;
	private TextField txtDescNatureza;
	private Button btSearchNatureza;
	double total_itens = 0;
	HorizontalLayout hlTotal;

	
	private TextField txtDescricaoCliente;
	private TextField txtCodPreVendaDevolucao;
	private Button btSearchCliente;
	private Button btSearchPreVenda;
	private TextField txtCodCliente;
	private TextField txtDDD1;
	private TextField txtTelefone1;
	private TextField txtDDD2;
	private TextField txtTelefone2;
	private TextField txtDDD3;
	private TextField txtTelefone3;
	private TextField txtDDD4;
	private TextField txtTelefone4;	
	private TextField txtEmail;
	private Cliente ClienteSelecionado;
	private EcfPreVendaCabecalho PreVendaDevolucaoSelecionado;
	private NaturezaOperacao NaturezaSelecionada;
	
	private ComboBox cbEndereco;
	private ComboBox cbTrasportadora;
	private TextField txtComplemento;
	private TextField txtReferencia;
	
	private ComboBox cbEntrega;
	
	private Table tbItens;
	
	private  ComboBox cbUsuario;
	
	boolean visualizar;
	boolean alterarPropriedade = false;
	
	private ComboBox cbTipo;
	
	Integer cod_submodulo;
	
	public PedidoEditor(final Item item, String title, boolean modal, final boolean visualizar, Integer cod_submodulo){
		this.item = item;
		this.visualizar = visualizar;
		this.cod_submodulo = cod_submodulo;
		
		//setWidth("935px");
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("985px");
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
				
				if(!visualizar){
					hlButtons.addComponent(buildBtSave());
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtSalvar());
				}else{
					hlButtons.addComponent(buildBtFechar());
				}
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		if(item.getItemProperty("id").getValue() == null){
			buildLayout();			
		}else{
			if(visualizar){
				buildLayoutVisualizar();
			}else{
				buildLayoutEdit();
			}
		}
		
	}
	
	public PedidoEditor(final Item item, String title, boolean modal, final boolean visualizar, boolean alterarPropriedade){
		this.item = item;
		this.visualizar = visualizar;
		this.alterarPropriedade = alterarPropriedade;
		
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
				
				
				if(!visualizar){
					hlButtons.addComponent(buildBtSave());
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtSalvar());
				}else{
					hlButtons.addComponent(buildBtFechar());
				}
				
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		if(item.getItemProperty("id").getValue() == null){
			buildLayout();			
		}else{
			if(visualizar){
				buildLayoutVisualizar();
			}else{
				buildLayoutEdit();
			}
		}
		
	}
		
	boolean novo = false;
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		
		vlRoot.addComponent(new HorizontalLayout(){
				{
					addComponent(
						new FormLayout(){					
					    {
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
					
							TextField txtOs = new TextField("Orçamento | Pedido Número");
							txtOs.setStyleName("caption-align-pedido");
							addComponent(txtOs);
							
							if(item.getItemProperty("id").getValue() != null){
								txtOs.setValue(item.getItemProperty("id").getValue().toString());
								txtOs.setReadOnly(true);
							}else{
								EcfPreVendaCabecalho pedido = PedidoDAO.getNextID();
								txtOs.setValue(pedido.getId().toString());
								item.getItemProperty("id").setValue(pedido.getId());
								item.getItemProperty("data").setValue(pedido.getData());
								item.getItemProperty("hora_pv").setValue(pedido.getHora_pv());
								txtOs.setReadOnly(true);
								novo = true;
							}
					    }
					});
					
					addComponent(
							new FormLayout(){					
						    {
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
						
								TextField txtAbertura = new TextField("Abertura");
								txtAbertura.setStyleName("caption-align-pedido-abertura");
								addComponent(txtAbertura);
								
								SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
								
								if(item.getItemProperty("id").getValue() != null){
									Date dataPv = (Date)item.getItemProperty("data").getValue();
									Date horaPv = (Date)item.getItemProperty("hora_pv").getValue();
									
									txtAbertura.setValue(sdf.format(dataPv)+" "+sdf2.format(horaPv));
									txtAbertura.setReadOnly(true);
								}else{
									txtAbertura.setValue(sdf1.format(new Date()));
									txtAbertura.setReadOnly(true);
								}
						    }
						});
				}
		});
			
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(	new FormLayout(){					
					{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);
								
							cbTipo = new ComboBox("Tipo");
							cbTipo.addItem("ORCAMENTO");
							cbTipo.addItem("PEDIDO");
							cbTipo.addItem("DEVOLUCAO");
							cbTipo.setStyleName("caption-align-pedido");						
							cbTipo.setNullSelectionAllowed(false);
							cbTipo.setTextInputAllowed(false);
							cbTipo.setRequired(true);							
							cbTipo.focus();
							
							
							cbTipo.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									if(txtCodNatureza != null && txtDescNatureza != null){
										NaturezaSelecionada = null;
										if(txtCodNatureza.isReadOnly()){
											txtCodNatureza.setReadOnly(false);										
										}
										txtCodNatureza.setValue("");
										
										if(txtDescNatureza.isReadOnly()){
											txtDescNatureza.setReadOnly(false); 
										}
										txtDescNatureza.setValue("");
										txtDescNatureza.setReadOnly(true);
									}
									
									if(cbTipo.getValue() != null && cbTipo.getValue().toString().equals("DEVOLUCAO")){									
										
										if(txtCodPreVendaDevolucao != null && txtCodCliente != null){
											txtCodPreVendaDevolucao.setRequired(true); 
											txtCodCliente.setRequired(true);
										}
										
										
										if(txtCodPreVendaDevolucao != null && btSearchPreVenda != null){
											txtCodPreVendaDevolucao.setReadOnly(false);
											btSearchPreVenda.setEnabled(true);
										}
									}else{
										if(txtCodPreVendaDevolucao != null && txtCodCliente != null){
											txtCodPreVendaDevolucao.setRequired(false); 
											txtCodCliente.setRequired(false);
										}
										
										if(txtCodPreVendaDevolucao != null && btSearchPreVenda != null){
											txtCodPreVendaDevolucao.setReadOnly(true);
											btSearchPreVenda.setEnabled(false);
										}
									}
								
								}
							});
							
							addComponent(cbTipo);	
							fieldGroup.bind(cbTipo, "tipo");
							
							cbTipo.select("PEDIDO");
	
					}
				});
				
			}
		});
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
				txtCodNatureza = new TextField("Natureza Operação");				
				txtCodNatureza.setWidth("60px");				
				txtCodNatureza.setNullRepresentation("");
				txtCodNatureza.setStyleName("caption-align-pedido");
				txtCodNatureza.setId("txtCodNatureza");
				txtCodNatureza.setReadOnly(false);
				
				JavaScript.getCurrent().execute("$('#txtCodNatureza').mask('0000000000')");
				txtCodNatureza.setImmediate(true);
						
				
				
				txtCodNatureza.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						
						NaturezaSelecionada = new NaturezaOperacao();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){							
														
							NaturezaSelecionada = NaturezaOperacaoDAO.find(Integer.parseInt(event.getText()));		
							
							if(NaturezaSelecionada != null){
								
								boolean valid_natureza = false;
								if(cbTipo.getValue().toString().equals("PEDIDO") && NaturezaSelecionada.getTipo().equals("SAIDA")){
									txtDescNatureza.setReadOnly(false);
									txtDescNatureza.setValue(NaturezaSelecionada.getDescricao());
									txtDescNatureza.setReadOnly(true);			
									
									if(NaturezaSelecionada.getTipo().equals("ENTRADA")){
										txtCodPreVendaDevolucao.setReadOnly(false);
										btSearchPreVenda.setEnabled(true);											
									}else{
										txtCodPreVendaDevolucao.setReadOnly(true);
										btSearchPreVenda.setEnabled(false);
									}
									
									valid_natureza = true;
								}
								
								if(cbTipo.getValue().toString().equals("DEVOLUCAO") && NaturezaSelecionada.getTipo().equals("ENTRADA")){
									txtDescNatureza.setReadOnly(false);
									txtDescNatureza.setValue(NaturezaSelecionada.getDescricao());
									txtDescNatureza.setReadOnly(true);			
									
									if(NaturezaSelecionada.getTipo().equals("ENTRADA")){
										txtCodPreVendaDevolucao.setReadOnly(false);
										btSearchPreVenda.setEnabled(true);											
									}else{
										txtCodPreVendaDevolucao.setReadOnly(true);
										btSearchPreVenda.setEnabled(false);
									}
									
									valid_natureza = true;
								}
								
								if(!valid_natureza){
									Notify.Show("Natureza de Operação Inválida !", Notify.TYPE_WARNING);
								}
								
								
							}else {
								txtDescNatureza.setReadOnly(false);
								txtDescNatureza.setValue("");
								txtDescNatureza.setReadOnly(true);
								
							}
						}else{
							txtDescNatureza.setReadOnly(false);
							txtDescNatureza.setValue("");
							txtDescNatureza.setReadOnly(true);							
						}
					}
				});

				txtCodNatureza.setRequired(true);		
				txtDescNatureza = new TextField();
				txtDescNatureza.setTabIndex(2000);
				txtDescNatureza.setReadOnly(true);
				txtDescNatureza.setWidth("308px");
									
				if(item.getItemProperty("naturezaOperacao") != null && item.getItemProperty("naturezaOperacao").getValue() != null){
					NaturezaSelecionada = NaturezaOperacaoDAO.find(((NaturezaOperacao)item.getItemProperty("naturezaOperacao").getValue()).getId());				
				}else{
//					String valorDefault = ParametizacaoDAO.getValueDefault(cod_submodulo, "NATUREZA_OPERACAO");
//					if(valorDefault != null){
//						NaturezaSelecionada = NaturezaOperacaoDAO.find(Integer.parseInt(valorDefault));
//					}
				}
						
				btSearchNatureza = new Button();
				btSearchNatureza.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchNatureza.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchNatureza.setTabIndex(300000);
				
				
				btSearchNatureza.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						String tipo = "";
						if(cbTipo.getValue().toString().equals("PEDIDO") || cbTipo.getValue().toString().equals("ORCAMENTO")){
							tipo = "SAIDA";
						}
						if(cbTipo.getValue().toString().equals("DEVOLUCAO")){
							tipo = "ENTRADA";
						}
						NaturezaOperacaoUtil cUtil = new NaturezaOperacaoUtil(true, true, tipo);
						cUtil.addListerner(new NaturezaOperacaoUtil.NaturezaUtilListerner() {
							
							@Override
							public void onSelected(NaturezaUtilEvent event) {
									if(event.getNaturezaOperacao() != null){
										txtCodNatureza.setValue(event.getNaturezaOperacao().getId().toString());
										txtDescNatureza.setReadOnly(false);
										txtDescNatureza.setValue(event.getNaturezaOperacao().getDescricao());
										txtDescNatureza.setReadOnly(true);
										NaturezaSelecionada = event.getNaturezaOperacao();
										
										if(NaturezaSelecionada.getTipo().equals("ENTRADA")){
											txtCodPreVendaDevolucao.setReadOnly(false);
											btSearchPreVenda.setEnabled(true);											
										}else{
											txtCodPreVendaDevolucao.setReadOnly(true);
											btSearchPreVenda.setEnabled(false);
										}
									}else{
										txtCodPreVendaDevolucao.setReadOnly(true);
										btSearchPreVenda.setEnabled(false);
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});
				
				
				FormLayout frmCodigoNatureza = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(txtCodNatureza);							
					}
				};
				addComponent(frmCodigoNatureza);
		
				FormLayout frmButtonSearchNatureza =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");										
						addComponent(btSearchNatureza);							
					}
				}; 
							
				FormLayout frmDescNatureza = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(txtDescNatureza);							
					}
				}; 
				addComponent(frmButtonSearchNatureza);
				addComponent(frmDescNatureza);
				setExpandRatio(frmDescNatureza, 1);		
				
				
				if(txtCodNatureza != null && NaturezaSelecionada != null){
					txtCodNatureza.setReadOnly(false);
					txtCodNatureza.setValue(NaturezaSelecionada.getId().toString());
					txtCodNatureza.setReadOnly(true);
				}
				
				if(txtDescNatureza != null && NaturezaSelecionada != null){
					txtDescNatureza.setReadOnly(false);
					txtDescNatureza.setValue(NaturezaSelecionada.getDescricao());
					txtDescNatureza.setReadOnly(true);
				}
				
				
				addComponent(new FormLayout(){
					{												
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						//
						
						txtCodPreVendaDevolucao= new TextField("Pré-venda Devolução Nº");				
						txtCodPreVendaDevolucao.setWidth("60px");				
						txtCodPreVendaDevolucao.setNullRepresentation("");				
						txtCodPreVendaDevolucao.setId("txtCodCliente");				
						JavaScript.getCurrent().execute("$('#txtCodPreVendaDevolucao').mask('0000000000')");
						txtCodPreVendaDevolucao.setImmediate(true);
						txtCodPreVendaDevolucao.setReadOnly(true); 
							
						txtCodPreVendaDevolucao.addListener(new TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								PreVendaDevolucaoSelecionado = new EcfPreVendaCabecalho();
								
								if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){															
									PreVendaDevolucaoSelecionado = EcfPreVendaCabecalhoDAO.find(Integer.parseInt(event.getText()));		
									
									if(PreVendaDevolucaoSelecionado != null && PreVendaDevolucaoSelecionado.getSituacao().equals("C")){
										Notify.Show("Este Pedido esta Cancelado!", Notify.TYPE_WARNING);
										PreVendaDevolucaoSelecionado = null;
									}
									
									txtCodPreVendaDevolucao.setValue(PreVendaDevolucaoSelecionado != null && PreVendaDevolucaoSelecionado.getId() != null ? PreVendaDevolucaoSelecionado.getId().toString() : "");
													
									
									if(PreVendaDevolucaoSelecionado.getCliente() != null && PreVendaDevolucaoSelecionado.getCliente().getNome_razao() != null){
										ClienteSelecionado = PreVendaDevolucaoSelecionado.getCliente();
										txtCodCliente.setReadOnly(false);
										txtCodCliente.setValue(PreVendaDevolucaoSelecionado.getCliente().getId().toString());
										txtCodCliente.setReadOnly(true);
										
										txtDescricaoCliente.setReadOnly(false);
										txtDescricaoCliente.setValue(PreVendaDevolucaoSelecionado.getCliente().getNome_razao().toString());
										txtDescricaoCliente.setReadOnly(true);
										
										txtCodCliente.setReadOnly(true);
										btSearchCliente.setEnabled(false);
										
										txtEmail.setValue(ClienteSelecionado.getEmail());
										txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
										txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
										txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
										txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
										txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
										txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
										txtTelefone3.setValue(ClienteSelecionado.getCelular1());
										txtTelefone4.setValue(ClienteSelecionado.getCelular2());
									}else{
										ClienteSelecionado = null;
										
										txtCodCliente.setReadOnly(false);
										txtCodCliente.setValue("");
										
										txtDescricaoCliente.setReadOnly(false);
										txtDescricaoCliente.setValue("");
										txtDescricaoCliente.setReadOnly(true);
										
										btSearchCliente.setEnabled(true); 
										
										txtEmail.setValue("");
										txtDDD1.setValue("");
										txtDDD2.setValue("");
										txtDDD3.setValue("");
										txtDDD4.setValue("");
										txtTelefone1.setValue("");
										txtTelefone2.setValue("");
										txtTelefone3.setValue("");
										txtTelefone4.setValue("");
									}
									
								}else{
									txtCodCliente.setValue("");
									txtDescricaoCliente.setValue("");
								}
							}
						});
						
						addComponent(txtCodPreVendaDevolucao);							
					}
				});
				addComponent(new FormLayout(){
					{												
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom-new_hide_require");
						
						btSearchPreVenda = new Button("");
						btSearchPreVenda.setStyleName(BaseTheme.BUTTON_LINK);
						btSearchPreVenda.setIcon(new ThemeResource("icons/search-16.png"));
						btSearchPreVenda.setTabIndex(300000);
						btSearchPreVenda.setEnabled(false);
						btSearchPreVenda.addClickListener(new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								   PedidoUtil2 cUtil = new PedidoUtil2(true, true);
								   cUtil.addListerner(new PedidoUtil2.PedidoListerner() {
									
											@Override
											public void onSelected(PedidoUtil2.PedidoEvent event) {
													if(event.getPedido() != null){
														txtCodPreVendaDevolucao.setValue(event.getPedido().getId().toString());
														PreVendaDevolucaoSelecionado = event.getPedido();						
														
														if(event.getPedido().getCliente() != null && event.getPedido().getCliente().getNome_razao() != null){
															ClienteSelecionado = event.getPedido().getCliente();
															txtCodCliente.setReadOnly(false);
															txtCodCliente.setValue(event.getPedido().getCliente().getId().toString());
															txtCodCliente.setReadOnly(true);
															
															txtDescricaoCliente.setReadOnly(false);
															txtDescricaoCliente.setValue(event.getPedido().getCliente().getNome_razao().toString());
															txtDescricaoCliente.setReadOnly(true);
															
															txtCodCliente.setReadOnly(true);
															btSearchCliente.setEnabled(false);
															
															txtEmail.setValue(ClienteSelecionado.getEmail());
															txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
															txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
															txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
															txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
															txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
															txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
															txtTelefone3.setValue(ClienteSelecionado.getCelular1());
															txtTelefone4.setValue(ClienteSelecionado.getCelular2());
														}else{
															ClienteSelecionado = null;
															
															txtCodCliente.setReadOnly(false);
															txtCodCliente.setValue("");
															
															txtDescricaoCliente.setReadOnly(false);
															txtDescricaoCliente.setValue("");
															txtDescricaoCliente.setReadOnly(true);
															
															btSearchCliente.setEnabled(true); 
															
															txtEmail.setValue("");
															txtDDD1.setValue("");
															txtDDD2.setValue("");
															txtDDD3.setValue("");
															txtDDD4.setValue("");
															txtTelefone1.setValue("");
															txtTelefone2.setValue("");
															txtTelefone3.setValue("");
															txtTelefone4.setValue("");
														}
													}
											}							
								   });
								
								   getUI().addWindow(cUtil);
							}
						});
						
						addComponent(btSearchPreVenda);							
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
				
				txtCodCliente = new TextField("Cliente");				
				txtCodCliente.setWidth("60px");				
				txtCodCliente.setNullRepresentation("");
				txtCodCliente.setStyleName("caption-align-pedido");
				txtCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				txtCodCliente.setImmediate(true);
						
				
				
				txtCodCliente.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						ClienteSelecionado = new Cliente();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							ClienteSelecionado = cDAO.find(Integer.parseInt(event.getText()));		
							
							if(ClienteSelecionado != null){
								txtDescricaoCliente.setReadOnly(false);
								txtDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
								txtDescricaoCliente.setReadOnly(true);
								
								txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
								txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
								txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
								txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
								
								txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
								txtTelefone3.setValue(ClienteSelecionado.getCelular1());
								txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
								txtTelefone4.setValue(ClienteSelecionado.getCelular2());
																
								txtEmail.setValue(ClienteSelecionado.getEmail());
								
								if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("SIM")){
									cbEndereco.setContainerDataSource(getEnderecos());
									//fieldGroup.bind(cbEndereco, "end"); 
								}else{
									if(fieldGroup.getField("end") != null){
										fieldGroup.unbind(cbEndereco);
									}
								}
								
								
							}else {
								txtDescricaoCliente.setReadOnly(false);
								txtDescricaoCliente.setValue("");
								txtDescricaoCliente.setReadOnly(true);
								
								
								txtDDD1.setValue("");
								txtTelefone1.setValue("");
								txtDDD2.setValue("");
								txtTelefone2.setValue("");
								
								txtDDD3.setValue("");
								txtTelefone3.setValue("");
								txtDDD4.setValue("");
								txtTelefone4.setValue("");
								txtEmail.setValue("");
																
								cbEndereco.setContainerDataSource(null);
								
								if(fieldGroup.getField("end") != null){
									fieldGroup.unbind(cbEndereco);
								}
								
							}
						}else{
							txtDescricaoCliente.setReadOnly(false);
							txtDescricaoCliente.setValue("");
							txtDescricaoCliente.setReadOnly(true);	
							
							txtDDD1.setValue("");
							txtTelefone1.setValue("");
							txtDDD2.setValue("");
							txtTelefone2.setValue("");
							
							txtDDD3.setValue("");
							txtTelefone3.setValue("");
							txtDDD4.setValue("");
							txtTelefone4.setValue("");
							
							
							txtEmail.setValue("");
							
							cbEndereco.setContainerDataSource(null);
							if(fieldGroup.getField("end") != null){
								fieldGroup.unbind(cbEndereco);
							}
						}
					}
				});

				//txtCodCliente.setRequired(true);		
				txtDescricaoCliente = new TextField();
				txtDescricaoCliente.setTabIndex(2000);
				txtDescricaoCliente.setReadOnly(true);
				txtDescricaoCliente.setWidth("592px");
				
				
									
				if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
					ClienteSelecionado = ClienteDAO.find(((Cliente)item.getItemProperty("cliente").getValue()).getId());				
				}
				
				
							
				btSearchCliente = new Button();
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
										txtCodCliente.setValue(event.getCliente().getId().toString());
										txtDescricaoCliente.setReadOnly(false);
										txtDescricaoCliente.setValue(event.getCliente().getNome_razao());
										txtDescricaoCliente.setReadOnly(true);
										ClienteSelecionado = event.getCliente();
										
										txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
										txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
										txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
										txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
										
										txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
										txtTelefone3.setValue(ClienteSelecionado.getCelular1());
										txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
										txtTelefone4.setValue(ClienteSelecionado.getCelular2());
										
										txtEmail.setValue(ClienteSelecionado.getEmail());
										
										if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("SIM")){
											cbEndereco.setContainerDataSource(getEnderecos());
											//fieldGroup.bind(cbEndereco, "end"); 
										}else{
											if(fieldGroup.getField("end") != null){
												fieldGroup.unbind(cbEndereco);
											}
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
												
						addComponent(txtCodCliente);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchCliente);							
					}
				}; 
							
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(txtDescricaoCliente);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);		
				
				
				if(txtCodCliente != null && ClienteSelecionado != null){
					txtCodCliente.setValue(ClienteSelecionado.getId().toString());
				}
				
				if(txtDescricaoCliente != null && ClienteSelecionado != null){
					txtDescricaoCliente.setReadOnly(false);
					txtDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
					txtDescricaoCliente.setReadOnly(true);
				}

			}	
		});
		vlRoot.addComponent(new FormLayout(){					
				{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						TextField txtComprador = new TextField("Comprador");
						txtComprador.setNullRepresentation("");
						txtComprador.setStyleName("caption-align-pedido");
						txtComprador.setWidth("400px");
						
						addComponent(txtComprador);		
						fieldGroup.bind(txtComprador, "comprador");
				}
		});
		
		
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						ComboBox cbTipoVenda = new ComboBox("Natureza Itens");
						cbTipoVenda.setNullSelectionAllowed(false);
						cbTipoVenda.setTextInputAllowed(false);
						cbTipoVenda.setRequired(true);
						cbTipoVenda.addItem("PRODUTO");
						cbTipoVenda.addItem("SERVICO");
						
						
						cbTipoVenda.setStyleName("caption-align-pedido");
						addComponent(cbTipoVenda);	
						fieldGroup.bind(cbTipoVenda, "tipoVenda");
						
						if(cbTipoVenda.getValue() == null){
							String valorDefault = ParametizacaoDAO.getValueDefault(cod_submodulo, "TIPO_VENDA");
							if(valorDefault != null){
								cbTipoVenda.select(valorDefault); 
							}
						}
				}
		});
		
		vlRoot.addComponent(new FormLayout(){					
			{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					JPAContainer<TipoItemProduto> container = JPAContainerFactory.make(TipoItemProduto.class, ConnUtil.getEntity());
						
					ComboBox cbTipoProduto = new ComboBox("Tipo Produto", container);
					cbTipoProduto.setWidth("190px");
					cbTipoProduto.setNullSelectionAllowed(false);
					cbTipoProduto.setTextInputAllowed(false);
					cbTipoProduto.setRequired(true);
					cbTipoProduto.setItemCaptionPropertyId("nome");
					cbTipoProduto.setConverter(new SingleSelectConverter(cbTipoProduto));
					
					cbTipoProduto.setStyleName("caption-align-pedido");
					addComponent(cbTipoProduto);	
					fieldGroup.bind(cbTipoProduto, "tipo_produto");
					
					
					
					if(cbTipoProduto.getValue() == null){
						String valorDefault = ParametizacaoDAO.getValueDefault(cod_submodulo, "TIPO_PRODUTO");
						if(valorDefault != null){
							
							for (Object item : container.getItemIds()) {
								if((Integer)container.getItem(item).getItemProperty("id").getValue() == Integer.parseInt(valorDefault)){									
									cbTipoProduto.select(item);
									break;
								}
							}
							
						}
					}
					
			}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
							
						cbEntrega = new ComboBox("Entrega");
						cbEntrega.setNullSelectionAllowed(false);
						cbEntrega.setTextInputAllowed(false);
						cbEntrega.setRequired(true);
						cbEntrega.addItem("SIM");
						cbEntrega.addItem("NAO");
						
						cbEntrega.setImmediate(true);
						cbEntrega.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("SIM")){
									
									cbEndereco.setReadOnly(false);
									txtComplemento.setReadOnly(false);
									txtReferencia.setReadOnly(false);
									cbTrasportadora.setReadOnly(false);
									
									
									
									cbEndereco.setContainerDataSource(getEnderecos());
									//fieldGroup.bind(cbEndereco, "end"); 
									
								}
								
								if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("NAO")){
									
									if(cbEndereco != null){
										cbEndereco.setReadOnly(true);									
									}
									
									if(txtComplemento != null){
										txtComplemento.setReadOnly(true);
									}
									
									if(txtReferencia != null){
										txtReferencia.setReadOnly(true);
									}
									
									if(cbTrasportadora != null){
										cbTrasportadora.setReadOnly(true);
									}
									
									
									if(fieldGroup.getField("end") != null){
										fieldGroup.unbind(cbEndereco);
									}
									
								}
							}
						});
						
						cbEntrega.setStyleName("caption-align-pedido");
						addComponent(cbEntrega);		
						fieldGroup.bind(cbEntrega, "entregar");
						
						cbEntrega.select("NAO");
				}
		});
	
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
																											
					cbEndereco = new ComboBox("Endereços"){
						@Override
						public String getItemCaption(Object itemId) {
						   Item item = getItem(itemId);
						
						   if (item == null) {
						      return "";
						   }
						     
						   return String.valueOf(item.getItemProperty("cep").getValue()) + ", " + 
					   		  String.valueOf(item.getItemProperty("endereco").getValue()) + ", " +
					   		  String.valueOf(item.getItemProperty("numero").getValue()) + ", " +
					   		  String.valueOf(item.getItemProperty("bairro").getValue()) + ", " +
					   		  String.valueOf(item.getItemProperty("cidade").getValue())+ " - " +
					   		  String.valueOf(item.getItemProperty("uf").getValue());
						}
					};
					
					cbEndereco.setWidth("685px");
					cbEndereco.setRequired(true);
					cbEndereco.setNullSelectionAllowed(false);
					cbEndereco.setStyleName("caption-align-pedido");	
					cbEndereco.setImmediate(true);
					cbEndereco.setTextInputAllowed(false);
					//cbEndereco.setConverter(new SingleSelectConverter(cbEndereco));
					
					
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
						//fieldGroup.bind(cbEndereco, "end"); 
					}
					
					cbEndereco.setReadOnly(true);
									
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
						txtComplemento.setStyleName("caption-align-pedido");
						txtComplemento.setWidth("300px");
						txtComplemento.setImmediate(true);
						txtComplemento.setTextChangeEventMode(TextChangeEventMode.LAZY);
						txtComplemento.setRequired(true);
						txtComplemento.setDescription("Casa, Apto, Galpão, Anexo, etc");						
						
						addComponent(txtComplemento);	
						
						if(cbEndereco.getValue() != null){
							txtComplemento.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
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
						txtReferencia.setWidth("300px");
						txtReferencia.setImmediate(true);
						txtReferencia.setTextChangeEventMode(TextChangeEventMode.LAZY);
						txtReferencia.setRequired(false);
						
						addComponent(txtReferencia);
						
						if(cbEndereco.getValue() != null){
							txtReferencia.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue().toString());
						}
						
						txtReferencia.setReadOnly(true);
					}
				});
			}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
																											
					cbTrasportadora = new ComboBox("Transportadora", getTransportadoras());
					cbTrasportadora.setItemCaptionPropertyId("fantasia");
					cbTrasportadora.setWidth("685px");
					cbTrasportadora.setRequired(true);
					cbTrasportadora.setNullSelectionAllowed(false);
					cbTrasportadora.setStyleName("caption-align-pedido");	
					cbTrasportadora.setImmediate(true);
					cbTrasportadora.setTextInputAllowed(false);
					cbTrasportadora.setConverter(new SingleSelectConverter(cbTrasportadora));
					cbTrasportadora.setItemCaptionPropertyId("fantasia");
					
					cbTrasportadora.setReadOnly(true);
								
					addComponent(cbTrasportadora);
					//fieldGroup.bind(cbTrasportadora, "transportadora");
					
									
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					txtEmail = new TextField ("E-mail");								
					txtEmail.setStyleName("caption-align-pedido");						
					txtEmail.setNullRepresentation("");					
					txtEmail.setWidth("420px");
									
					addComponent(txtEmail);		
					
					if(ClienteSelecionado != null){
						txtEmail.setValue(ClienteSelecionado.getEmail());
					}
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
									txtDDD1.setStyleName("telefone3");
									txtDDD1.addStyleName("caption-align-pedido");									
									txtDDD1.setMaxLength(2);
									txtDDD1.setWidth("40px");

									addComponent(txtDDD1);
									
									if(ClienteSelecionado != null){
										txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
									}
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

									addComponent(txtDDD2);
									
									if(ClienteSelecionado != null){
										txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
									}
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
									txtDDD3.setStyleName("telefone3");
									txtDDD3.addStyleName("caption-align-pedido");
									txtDDD3.setRequired(false);
									txtDDD3.setMaxLength(2);
									txtDDD3.setWidth("40px");

									addComponent(txtDDD3);
									
									if(ClienteSelecionado != null){
										txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
									}
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

									addComponent(txtDDD4);
									
									if(ClienteSelecionado != null){
										txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
									}
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
									}

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
																	
								TextArea txtObs = new TextArea ("Obs.");								
								txtObs.setStyleName("caption-align-pedido");						
								txtObs.setNullRepresentation("");					
								txtObs.setWidth("620px");
								txtObs.setHeight("40px");
												
								addComponent(txtObs);		
								fieldGroup.bind(txtObs, "obs");
								
							}
					});
		
			
	}
	
	public void buildLayoutEdit(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
			new HorizontalLayout(){
				{
					addComponent(
						new FormLayout(){					
					    {
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
					
							TextField txtOs = new TextField("Orçamento | Pedido Número");
							txtOs.setStyleName("caption-align-pedido");
							addComponent(txtOs);
							
							if(item.getItemProperty("id").getValue() != null){
								txtOs.setValue(item.getItemProperty("id").getValue().toString());
								txtOs.setReadOnly(true);
							}else{
								EcfPreVendaCabecalho pedido = PedidoDAO.getNextID();
								txtOs.setValue(pedido.getId().toString());
								item.getItemProperty("id").setValue(pedido.getId());
								item.getItemProperty("data").setValue(pedido.getData());
								item.getItemProperty("hora_pv").setValue(pedido.getHora_pv());
								txtOs.setReadOnly(true);
							}
					    }
					});
					
					addComponent(
							new FormLayout(){					
						    {
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
						
								TextField txtAbertura = new TextField("Abertura");
								txtAbertura.setStyleName("caption-align-pedido-abertura");
								addComponent(txtAbertura);
								
								SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
								if(item.getItemProperty("id").getValue() != null){
									Date dataPv = (Date)item.getItemProperty("data").getValue();
									Date horaPv = (Date)item.getItemProperty("hora_pv").getValue();
									txtAbertura.setValue(sdf.format(dataPv)+" "+sdf2.format(horaPv));
									txtAbertura.setReadOnly(true);
								}else{
									txtAbertura.setValue(sdf1.format(new Date()));
									txtAbertura.setReadOnly(true);
								}
						    }
						});
				}
		});
			
		
		if(alterarPropriedade){
		
			vlRoot.addComponent(
					new FormLayout(){					
					{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);
								
						
							
							
							cbUsuario = new ComboBox("Propriedade");
							cbUsuario.setNullSelectionAllowed(false);
							cbUsuario.setTextInputAllowed(false);
							cbUsuario.setRequired(true);
							cbUsuario.setStyleName("caption-align-pedido");							
							cbUsuario.setImmediate(true);

							List<EmpresasUsuario> usuarios = EmpresaUsuarioDAO.getUsuarios();
							for (EmpresasUsuario empresasUsuario : usuarios) {
								cbUsuario.addItem(empresasUsuario.getUsuario().getUsername());
							}
							
							addComponent(cbUsuario);	
							
							if(item.getItemProperty("vendedor").getValue() != null){
								cbUsuario.select(item.getItemProperty("vendedor").getValue().toString());
							}
							
					}
			});
		
		}
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
							
						ComboBox cbTipo = new ComboBox("Tipo");
						cbTipo.setNullSelectionAllowed(false);
						cbTipo.setTextInputAllowed(false);
						cbTipo.setRequired(true);
						cbTipo.addItem("PEDIDO");
						cbTipo.addItem("ORCAMENTO");	
						
						
						
						cbTipo.setStyleName("caption-align-pedido");
						addComponent(cbTipo);	
						fieldGroup.bind(cbTipo, "tipo");
						
						cbTipo.setReadOnly(true);
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
				
				txtCodCliente = new TextField("Cliente");				
				txtCodCliente.setWidth("60px");				
				txtCodCliente.setNullRepresentation("");
				txtCodCliente.setStyleName("caption-align-pedido");
				txtCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				txtCodCliente.setImmediate(true);
						
				
				
				txtCodCliente.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						ClienteSelecionado = new Cliente();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							ClienteSelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));		
							
							if(ClienteSelecionado != null){
								txtDescricaoCliente.setReadOnly(false);
								txtDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
								txtDescricaoCliente.setReadOnly(true);
								
								txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
								txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
								txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
								txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
								
								txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
								txtTelefone3.setValue(ClienteSelecionado.getCelular1());
								txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
								txtTelefone4.setValue(ClienteSelecionado.getCelular2());
																
								txtEmail.setValue(ClienteSelecionado.getEmail());
								
								if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("SIM")){
									cbEndereco.setContainerDataSource(getEnderecos());
									//fieldGroup.bind(cbEndereco, "end"); 
								}else{
									if(fieldGroup.getField("end") != null){
										fieldGroup.unbind(cbEndereco);
									}
								}
								
								
							}else {
								txtDescricaoCliente.setReadOnly(false);
								txtDescricaoCliente.setValue("");
								txtDescricaoCliente.setReadOnly(true);
								
								
								txtDDD1.setValue("");
								txtTelefone1.setValue("");
								txtDDD2.setValue("");
								txtTelefone2.setValue("");
								
								txtDDD3.setValue("");
								txtTelefone3.setValue("");
								txtDDD4.setValue("");
								txtTelefone4.setValue("");
								txtEmail.setValue("");
																
								cbEndereco.setContainerDataSource(null);
								
								if(fieldGroup.getField("end") != null){
									fieldGroup.unbind(cbEndereco);
								}
								
							}
						}else{
							txtDescricaoCliente.setReadOnly(false);
							txtDescricaoCliente.setValue("");
							txtDescricaoCliente.setReadOnly(true);	
							
							txtDDD1.setValue("");
							txtTelefone1.setValue("");
							txtDDD2.setValue("");
							txtTelefone2.setValue("");
							
							txtDDD3.setValue("");
							txtTelefone3.setValue("");
							txtDDD4.setValue("");
							txtTelefone4.setValue("");
							
							
							txtEmail.setValue("");
							
							cbEndereco.setContainerDataSource(null);
							if(fieldGroup.getField("end") != null){
								fieldGroup.unbind(cbEndereco);
							}
						}
					}
				});

				txtCodCliente.setRequired(true);		
				txtDescricaoCliente = new TextField();
				txtDescricaoCliente.setTabIndex(2000);
				txtDescricaoCliente.setReadOnly(true);
				txtDescricaoCliente.setWidth("592px");
				
				
									
				if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
					ClienteSelecionado = ClienteDAO.find(((Cliente)item.getItemProperty("cliente").getValue()).getId());				
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
										txtCodCliente.setValue(event.getCliente().getId().toString());
										txtDescricaoCliente.setReadOnly(false);
										txtDescricaoCliente.setValue(event.getCliente().getNome_razao());
										txtDescricaoCliente.setReadOnly(true);
										ClienteSelecionado = event.getCliente();
										
										txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
										txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
										txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
										txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
										
										txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
										txtTelefone3.setValue(ClienteSelecionado.getCelular1());
										txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
										txtTelefone4.setValue(ClienteSelecionado.getCelular2());
										
										txtEmail.setValue(ClienteSelecionado.getEmail());
										
										if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("SIM")){
											cbEndereco.setContainerDataSource(getEnderecos());
											//fieldGroup.bind(cbEndereco, "end"); 
										}else{
											if(fieldGroup.getField("end") != null){
												fieldGroup.unbind(cbEndereco);
											}
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
												
						addComponent(txtCodCliente);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchCliente);							
					}
				}; 
							
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(txtDescricaoCliente);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);		
				
				
				if(txtCodCliente != null && ClienteSelecionado != null){
					txtCodCliente.setValue(ClienteSelecionado.getId().toString());
				}
				
				if(txtDescricaoCliente != null && ClienteSelecionado != null){
					txtDescricaoCliente.setReadOnly(false);
					txtDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
					txtDescricaoCliente.setReadOnly(true);
				}

			}	
		});
				
		vlRoot.addComponent(
				new FormLayout(){					
				{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						TextField txtComprador = new TextField("Comprador");
						txtComprador.setNullRepresentation("");
						txtComprador.setStyleName("caption-align-pedido");
						txtComprador.setWidth("400px");
						
						addComponent(txtComprador);		
						fieldGroup.bind(txtComprador, "comprador");
				}
		});
		

		
		
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
				txtCodNatureza = new TextField("Natureza Operação");				
				txtCodNatureza.setWidth("60px");				
				txtCodNatureza.setNullRepresentation("");
				txtCodNatureza.setStyleName("caption-align-pedido");
				txtCodNatureza.setId("txtCodNatureza");
				
				JavaScript.getCurrent().execute("$('#txtCodNatureza').mask('0000000000')");
				txtCodNatureza.setImmediate(true);
						
				
				
				txtCodNatureza.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						
						NaturezaSelecionada = new NaturezaOperacao();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							NaturezaSelecionada = NaturezaOperacaoDAO.find(Integer.parseInt(event.getText()));		
							
							if(NaturezaSelecionada != null){
								txtDescNatureza.setReadOnly(false);
								txtDescNatureza.setValue(NaturezaSelecionada.getDescricao());
								txtDescNatureza.setReadOnly(true);
																
								
							}else {
								txtDescNatureza.setReadOnly(false);
								txtDescNatureza.setValue("");
								txtDescNatureza.setReadOnly(true);
								
							}
						}else{
							txtDescNatureza.setReadOnly(false);
							txtDescNatureza.setValue("");
							txtDescNatureza.setReadOnly(true);							
						}
					}
				});

				txtCodNatureza.setRequired(true);		
				txtDescNatureza = new TextField();
				txtDescNatureza.setTabIndex(2000);
				txtDescNatureza.setReadOnly(true);
				txtDescNatureza.setWidth("592px");
				
				
									
				if(item.getItemProperty("naturezaOperacao") != null && item.getItemProperty("naturezaOperacao").getValue() != null){
					NaturezaSelecionada = NaturezaOperacaoDAO.find(((NaturezaOperacao)item.getItemProperty("naturezaOperacao").getValue()).getId());				
				}else{
					String valorDefault = ParametizacaoDAO.getValueDefault(cod_submodulo, "NATUREZA_OPERACAO");
					if(valorDefault != null){
						NaturezaSelecionada = NaturezaOperacaoDAO.find(Integer.parseInt(valorDefault));
					}
				}
				
				
							
				final Button btSearchNatureza = new Button();
				btSearchNatureza.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchNatureza.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchNatureza.setTabIndex(300000);
				btSearchNatureza.setEnabled(false);
				btSearchNatureza.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						NaturezaOperacaoUtil cUtil = new NaturezaOperacaoUtil(true, true);
						cUtil.addListerner(new NaturezaOperacaoUtil.NaturezaUtilListerner() {
							
							@Override
							public void onSelected(
									NaturezaUtilEvent event) {
									if(event.getNaturezaOperacao() != null){
										txtCodNatureza.setValue(event.getNaturezaOperacao().getId().toString());
										txtDescNatureza.setReadOnly(false);
										txtDescNatureza.setValue(event.getNaturezaOperacao().getDescricao());
										txtDescNatureza.setReadOnly(true);
										NaturezaSelecionada = event.getNaturezaOperacao();						
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});
				
				
				FormLayout frmCodigoNatureza = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(txtCodNatureza);							
					}
				};
				addComponent(frmCodigoNatureza);
		
				FormLayout frmButtonSearchNatureza =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");										
						addComponent(btSearchNatureza);							
					}
				}; 
							
				FormLayout frmDescNatureza = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(txtDescNatureza);							
					}
				}; 
				addComponent(frmButtonSearchNatureza);
				addComponent(frmDescNatureza);
				setExpandRatio(frmDescNatureza, 1);		
				
				
				if(txtCodNatureza != null && NaturezaSelecionada != null){
					txtCodNatureza.setValue(NaturezaSelecionada.getId().toString());
				}
				
				if(txtDescNatureza != null && NaturezaSelecionada != null){
					txtDescNatureza.setReadOnly(false);
					txtDescNatureza.setValue(NaturezaSelecionada.getDescricao());
					txtDescNatureza.setReadOnly(true);
				}

			}	
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						ComboBox cbTipoVenda = new ComboBox("Natureza Itens");
						cbTipoVenda.setNullSelectionAllowed(false);
						cbTipoVenda.setTextInputAllowed(false);
						cbTipoVenda.setRequired(true);
						cbTipoVenda.addItem("PRODUTO");
						cbTipoVenda.addItem("SERVICO");
						cbTipoVenda.focus();
						
						cbTipoVenda.setStyleName("caption-align-pedido");
						addComponent(cbTipoVenda);	
						fieldGroup.bind(cbTipoVenda, "tipoVenda");
						
						cbTipoVenda.setReadOnly(true);
				}
		});
		
		
		vlRoot.addComponent(new FormLayout(){					
			{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					JPAContainer<TipoItemProduto> container = JPAContainerFactory.make(TipoItemProduto.class, ConnUtil.getEntity());
						
					ComboBox cbTipoProduto = new ComboBox("Tipo Produto", container);
					cbTipoProduto.setWidth("190px");
					cbTipoProduto.setNullSelectionAllowed(false);
					cbTipoProduto.setTextInputAllowed(false);
					cbTipoProduto.setRequired(true);
					cbTipoProduto.setItemCaptionPropertyId("nome");
					cbTipoProduto.setConverter(new SingleSelectConverter(cbTipoProduto));
					
					cbTipoProduto.setStyleName("caption-align-pedido");
					addComponent(cbTipoProduto);	
					fieldGroup.bind(cbTipoProduto, "tipo_produto");
					
					
					
					if(cbTipoProduto.getValue() == null){
						String valorDefault = ParametizacaoDAO.getValueDefault(cod_submodulo, "TIPO_PRODUTO");
						if(valorDefault != null){
							
							for (Object item : container.getItemIds()) {
								if((Integer)container.getItem(item).getItemProperty("id").getValue() == Integer.parseInt(valorDefault)){									
									cbTipoProduto.select(item);
									break;
								}
							}
							
						}
					}
					
			}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
							
						cbEntrega = new ComboBox("Entrega");
						cbEntrega.setNullSelectionAllowed(false);
						cbEntrega.setTextInputAllowed(false);
						cbEntrega.setRequired(true);
						cbEntrega.addItem("SIM");
						cbEntrega.addItem("NAO");
						
						
						cbEntrega.setImmediate(true);
						cbEntrega.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("SIM")){
									
										if(cbEndereco != null){
											cbEndereco.setReadOnly(false);
										}
										
										if(txtComplemento != null ){
											txtComplemento.setReadOnly(false);
										}
										
										if(txtReferencia != null){
											txtReferencia.setReadOnly(false);
										}
										
										if(cbTrasportadora != null){
											cbTrasportadora.setReadOnly(false);
										}
									
									
										if(cbEndereco != null){
									cbEndereco.setContainerDataSource(getEnderecos());
									//fieldGroup.bind(cbEndereco, "end");
										}
									
								}
								
								if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("NAO")){
									
									if(cbEndereco != null){
										cbEndereco.setReadOnly(true);									
									}
									
									if(txtComplemento != null){
										txtComplemento.setReadOnly(true);
									}
									
									if(txtReferencia != null){
										txtReferencia.setReadOnly(true);
									}
									
									if(cbTrasportadora != null){
										cbTrasportadora.setReadOnly(true);
									}
									
									
									if(fieldGroup.getField("end") != null){
										fieldGroup.unbind(cbEndereco);
									}
									
								}
							}
						});
						
						cbEntrega.setStyleName("caption-align-pedido");
						addComponent(cbEntrega);		
						fieldGroup.bind(cbEntrega, "entregar");
				}
		});
	
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
																											
					cbEndereco = new ComboBox("Endereços"){
						@Override
						public String getItemCaption(Object itemId) {
						   Item item = getItem(itemId);
						
						   if (item == null) {
						      return "";
						   }
						     
						   return String.valueOf(item.getItemProperty("cep").getValue()) + ", " + 
					   		  String.valueOf(item.getItemProperty("endereco").getValue()) + ", " +
					   		  String.valueOf(item.getItemProperty("numero").getValue()) + ", " +
					   		  String.valueOf(item.getItemProperty("bairro").getValue()) + ", " +
					   		  String.valueOf(item.getItemProperty("cidade").getValue())+ " - " +
					   		  String.valueOf(item.getItemProperty("uf").getValue());
						}
					};
					
					cbEndereco.setWidth("685px");
					cbEndereco.setRequired(true);
					cbEndereco.setNullSelectionAllowed(false);
					cbEndereco.setStyleName("caption-align-pedido");	
					cbEndereco.setImmediate(true);
					cbEndereco.setTextInputAllowed(false);
					//cbEndereco.setConverter(new SingleSelectConverter(cbEndereco));
					
					
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
						//fieldGroup.bind(cbEndereco, "end"); 
					}
					
					if(cbEntrega.getValue().equals("SIM")){
						cbEndereco.setReadOnly(false);
					}else{
						cbEndereco.setReadOnly(true);
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
						txtComplemento.setStyleName("caption-align-pedido");
						txtComplemento.setWidth("300px");
						txtComplemento.setImmediate(true);
						txtComplemento.setTextChangeEventMode(TextChangeEventMode.LAZY);
						txtComplemento.setRequired(true);
						txtComplemento.setDescription("Casa, Apto, Galpão, Anexo, etc");						
						
						addComponent(txtComplemento);	
						
						if(cbEndereco.getValue() != null && cbEndereco.getValue() != null && cbEndereco.getItem(cbEndereco.getValue()) != null && cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue() != null){
							txtComplemento.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
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
						txtReferencia.setWidth("300px");
						txtReferencia.setImmediate(true);
						txtReferencia.setTextChangeEventMode(TextChangeEventMode.LAZY);
						txtReferencia.setRequired(false);
						
						addComponent(txtReferencia);
						
						if(cbEndereco.getValue() != null && cbEndereco.getValue() != null && cbEndereco.getItem(cbEndereco.getValue()) != null && cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue() != null){
							txtReferencia.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue().toString());
						}
						
						txtReferencia.setReadOnly(true);
					}
				});
			}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
																											
					cbTrasportadora = new ComboBox("Transportadora", getTransportadoras());
					cbTrasportadora.setItemCaptionPropertyId("fantasia");
					cbTrasportadora.setWidth("685px");
					cbTrasportadora.setRequired(true);
					cbTrasportadora.setNullSelectionAllowed(false);
					cbTrasportadora.setStyleName("caption-align-pedido");	
					cbTrasportadora.setImmediate(true);
					cbTrasportadora.setTextInputAllowed(false);
					cbTrasportadora.setConverter(new SingleSelectConverter(cbTrasportadora));
					cbTrasportadora.setItemCaptionPropertyId("fantasia");
					
								
					addComponent(cbTrasportadora);
					
					if(ClienteSelecionado != null && item.getItemProperty("transportadora").getValue() != null){						
						fieldGroup.bind(cbTrasportadora, "transportadora"); 
					}
					
					
							
					if(cbEntrega.getValue().equals("SIM")){
						cbTrasportadora.setReadOnly(false);
					}else{
						cbTrasportadora.setReadOnly(true);
					}
					
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					txtEmail = new TextField ("E-mail");								
					txtEmail.setStyleName("caption-align-pedido");						
					txtEmail.setNullRepresentation("");					
					txtEmail.setWidth("420px");
									
					addComponent(txtEmail);		
					
					if(ClienteSelecionado != null){
						txtEmail.setValue(ClienteSelecionado.getEmail());
					}
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
									txtDDD1.setStyleName("telefone3");
									txtDDD1.addStyleName("caption-align-pedido");
									txtDDD1.setRequired(true);
									txtDDD1.setMaxLength(2);
									txtDDD1.setWidth("40px");

									addComponent(txtDDD1);
									
									if(ClienteSelecionado != null){
										txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
									}
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

									addComponent(txtDDD2);
									
									if(ClienteSelecionado != null){
										txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
									}
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
									txtDDD3.setStyleName("telefone3");
									txtDDD3.addStyleName("caption-align-pedido");
									txtDDD3.setRequired(false);
									txtDDD3.setMaxLength(2);
									txtDDD3.setWidth("40px");

									addComponent(txtDDD3);
									
									if(ClienteSelecionado != null){
										txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
									}
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

									addComponent(txtDDD4);
									
									if(ClienteSelecionado != null){
										txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
									}
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
									}

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
																	
								TextArea txtObs = new TextArea ("Obs.");								
								txtObs.setStyleName("caption-align-pedido");						
								txtObs.setNullRepresentation("");					
								txtObs.setWidth("620px");
								txtObs.setHeight("40px");
												
								addComponent(txtObs);		
								fieldGroup.bind(txtObs, "obs");
								
							}
					});
		
			
	}
	
	
	public void buildLayoutVisualizar(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new HorizontalLayout(){
				{
					addComponent(
						new FormLayout(){					
					    {
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
					
							TextField txtOs = new TextField("Orçamento | Pedido Nº");
							txtOs.setStyleName("caption-align-pedido");
							addComponent(txtOs);
							
							if(item.getItemProperty("id").getValue() != null){
								txtOs.setValue(item.getItemProperty("id").getValue().toString());
								txtOs.setReadOnly(true);
							}else{
								EcfPreVendaCabecalho pedido = PedidoDAO.getNextID();
								txtOs.setValue(pedido.getId().toString());
								item.getItemProperty("id").setValue(pedido.getId());
								item.getItemProperty("data").setValue(pedido.getData());
								item.getItemProperty("hora_pv").setValue(pedido.getHora_pv());
								txtOs.setReadOnly(true);
							}
					    }
					});
					
					addComponent(
							new FormLayout(){					
						    {
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
						
								TextField txtAbertura = new TextField("Data Emissão");
								txtAbertura.setStyleName("caption-align-pedido-abertura");
								addComponent(txtAbertura);
								
								SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
								
								if(item.getItemProperty("id").getValue() != null){
									Date dataPv = (Date)item.getItemProperty("data").getValue();
									Date horaPv = (Date)item.getItemProperty("hora_pv").getValue();
									
									txtAbertura.setValue(sdf.format(dataPv)+" "+sdf2.format(horaPv));
									txtAbertura.setReadOnly(true);
								}else{
									txtAbertura.setValue(sdf1.format(new Date()));
									txtAbertura.setReadOnly(true);
								}
						    }
						});
				}
		});
		
		
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(
					new FormLayout(){					
				    {
						
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
							
						ComboBox cbTipo = new ComboBox("Tipo");
						cbTipo.setNullSelectionAllowed(false);
						cbTipo.setTextInputAllowed(false);
						cbTipo.setRequired(true);
						cbTipo.addItem("ORCAMENTO");
						cbTipo.addItem("PEDIDO");
						cbTipo.addItem("DEVOLUCAO");
						
						cbTipo.setStyleName("caption-align-pedido");
						addComponent(cbTipo);	
						fieldGroup.bind(cbTipo, "tipo");
						
						cbTipo.setReadOnly(true);
						
						
				    }
				});
				
				if(item.getItemProperty("n_nf_devolucao").getValue() != null){
				addComponent(
						new FormLayout(){					
					    {
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
					
							TextField txtNfDevolucao = new TextField("Pedido Devolução");
							txtNfDevolucao.setStyleName("caption-align-pedido-devolucao");
							
							String cod = ((EcfPreVendaCabecalho)item.getItemProperty("n_nf_devolucao").getValue()).getId().toString();							
							txtNfDevolucao.setValue(cod);
							addComponent(txtNfDevolucao);
							//fieldGroup.bind(txtNfDevolucao, "n_nf_devolucao");
							
							txtNfDevolucao.setReadOnly(true);
							
					    }
					});
				}
			}
	});
		
//		vlRoot.addComponent(new FormLayout(){					
//				{
//						setStyleName("form-cutom-new");
//						setMargin(true);
//						setSpacing(true);
//							
//						ComboBox cbTipo = new ComboBox("Tipo");
//						cbTipo.setNullSelectionAllowed(false);
//						cbTipo.setTextInputAllowed(false);
//						cbTipo.setRequired(true);
//						cbTipo.addItem("ORCAMENTO");
//						cbTipo.addItem("PEDIDO");
//						cbTipo.addItem("DEVOLUCAO");
//						
//						
//						
//						cbTipo.setStyleName("caption-align-pedido");
//						addComponent(cbTipo);	
//						fieldGroup.bind(cbTipo, "tipo");
//						
//						cbTipo.setReadOnly(true);
//				}
//		});
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
				
				txtCodCliente = new TextField("Cliente");				
				txtCodCliente.setWidth("60px");				
				txtCodCliente.setNullRepresentation("");
				txtCodCliente.setStyleName("caption-align-pedido");
				txtCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				txtCodCliente.setImmediate(true);
						
				
				
				txtCodCliente.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						ClienteSelecionado = new Cliente();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							ClienteSelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));		
							
							if(ClienteSelecionado != null){
								txtDescricaoCliente.setReadOnly(false);
								txtDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
								txtDescricaoCliente.setReadOnly(true);
								
								txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
								txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
								txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
								txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
								
								txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
								txtTelefone3.setValue(ClienteSelecionado.getCelular1());
								txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
								txtTelefone4.setValue(ClienteSelecionado.getCelular2());
																
								txtEmail.setValue(ClienteSelecionado.getEmail());
								
								if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("SIM")){
									cbEndereco.setContainerDataSource(getEnderecos());
									//fieldGroup.bind(cbEndereco, "end"); 
								}else{
									if(fieldGroup.getField("end") != null){
										fieldGroup.unbind(cbEndereco);
									}
								}
								
								
							}else {
								txtDescricaoCliente.setReadOnly(false);
								txtDescricaoCliente.setValue("");
								txtDescricaoCliente.setReadOnly(true);
								
								
								txtDDD1.setValue("");
								txtTelefone1.setValue("");
								txtDDD2.setValue("");
								txtTelefone2.setValue("");
								
								txtDDD3.setValue("");
								txtTelefone3.setValue("");
								txtDDD4.setValue("");
								txtTelefone4.setValue("");
								txtEmail.setValue("");
																
								cbEndereco.setContainerDataSource(null);
								
								if(fieldGroup.getField("end") != null){
									fieldGroup.unbind(cbEndereco);
								}
								
							}
						}else{
							txtDescricaoCliente.setReadOnly(false);
							txtDescricaoCliente.setValue("");
							txtDescricaoCliente.setReadOnly(true);	
							
							txtDDD1.setValue("");
							txtTelefone1.setValue("");
							txtDDD2.setValue("");
							txtTelefone2.setValue("");
							
							txtDDD3.setValue("");
							txtTelefone3.setValue("");
							txtDDD4.setValue("");
							txtTelefone4.setValue("");
							
							
							txtEmail.setValue("");
							
							cbEndereco.setContainerDataSource(null);
							if(fieldGroup.getField("end") != null){
								fieldGroup.unbind(cbEndereco);
							}
						}
					}
				});

				txtCodCliente.setRequired(true);		
				txtDescricaoCliente = new TextField();
				txtDescricaoCliente.setTabIndex(2000);
				txtDescricaoCliente.setReadOnly(true);
				txtDescricaoCliente.setWidth("592px");
				
				
									
				if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
					ClienteSelecionado = ClienteDAO.find(((Cliente)item.getItemProperty("cliente").getValue()).getId());				
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
										txtCodCliente.setValue(event.getCliente().getId().toString());
										txtDescricaoCliente.setReadOnly(false);
										txtDescricaoCliente.setValue(event.getCliente().getNome_razao());
										txtDescricaoCliente.setReadOnly(true);
										ClienteSelecionado = event.getCliente();
										
										txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
										txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
										txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
										txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
										
										txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
										txtTelefone3.setValue(ClienteSelecionado.getCelular1());
										txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
										txtTelefone4.setValue(ClienteSelecionado.getCelular2());
										
										txtEmail.setValue(ClienteSelecionado.getEmail());
										
										if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("SIM")){
											cbEndereco.setContainerDataSource(getEnderecos());
											//fieldGroup.bind(cbEndereco, "end"); 
										}else{
											if(fieldGroup.getField("end") != null){
												fieldGroup.unbind(cbEndereco);
											}
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
												
						addComponent(txtCodCliente);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchCliente);							
					}
				}; 
							
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(txtDescricaoCliente);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);		
				
				
				if(txtCodCliente != null && ClienteSelecionado != null){
					txtCodCliente.setValue(ClienteSelecionado.getId().toString());
					txtCodCliente.setReadOnly(true);
				}
				
				if(txtDescricaoCliente != null && ClienteSelecionado != null){
					txtDescricaoCliente.setReadOnly(false);
					txtDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
					txtDescricaoCliente.setReadOnly(true);
				}

			}	
		});
		vlRoot.addComponent(new FormLayout(){					
				{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						TextField txtComprador = new TextField("Comprador");
						txtComprador.setNullRepresentation("");
						txtComprador.setStyleName("caption-align-pedido");
						txtComprador.setWidth("400px");
						
						addComponent(txtComprador);		
						fieldGroup.bind(txtComprador, "comprador");
						
						txtComprador.setReadOnly(true);
				}
		});
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
				txtCodNatureza = new TextField("Natureza Operação");				
				txtCodNatureza.setWidth("60px");				
				txtCodNatureza.setNullRepresentation("");
				txtCodNatureza.setStyleName("caption-align-pedido");
				txtCodNatureza.setId("txtCodNatureza");
				
				JavaScript.getCurrent().execute("$('#txtCodNatureza').mask('0000000000')");
				txtCodNatureza.setImmediate(true);
						
				
				
				txtCodNatureza.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						
						NaturezaSelecionada = new NaturezaOperacao();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							NaturezaSelecionada = NaturezaOperacaoDAO.find(Integer.parseInt(event.getText()));		
							
							if(NaturezaSelecionada != null){
								txtDescNatureza.setReadOnly(false);
								txtDescNatureza.setValue(NaturezaSelecionada.getDescricao());
								txtDescNatureza.setReadOnly(true);
																
								
							}else {
								txtDescNatureza.setReadOnly(false);
								txtDescNatureza.setValue("");
								txtDescNatureza.setReadOnly(true);
								
							}
						}else{
							txtDescNatureza.setReadOnly(false);
							txtDescNatureza.setValue("");
							txtDescNatureza.setReadOnly(true);							
						}
					}
				});

				txtCodNatureza.setRequired(true);		
				txtDescNatureza = new TextField();
				txtDescNatureza.setTabIndex(2000);
				txtDescNatureza.setReadOnly(true);
				txtDescNatureza.setWidth("592px");
				
				
									
				if(item.getItemProperty("naturezaOperacao") != null && item.getItemProperty("naturezaOperacao").getValue() != null){
					NaturezaSelecionada = NaturezaOperacaoDAO.find(((NaturezaOperacao)item.getItemProperty("naturezaOperacao").getValue()).getId());				
				}else{
					String valorDefault = ParametizacaoDAO.getValueDefault(cod_submodulo, "NATUREZA_OPERACAO");
					if(valorDefault != null){
						NaturezaSelecionada = NaturezaOperacaoDAO.find(Integer.parseInt(valorDefault));
					}
				}
				
				
							
				final Button btSearchNatureza = new Button();
				btSearchNatureza.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchNatureza.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchNatureza.setTabIndex(300000);
				btSearchNatureza.setEnabled(false);
				btSearchNatureza.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						NaturezaOperacaoUtil cUtil = new NaturezaOperacaoUtil(true, true);
						cUtil.addListerner(new NaturezaOperacaoUtil.NaturezaUtilListerner() {
							
							@Override
							public void onSelected(
									NaturezaUtilEvent event) {
									if(event.getNaturezaOperacao() != null){
										txtCodNatureza.setValue(event.getNaturezaOperacao().getId().toString());
										txtDescNatureza.setReadOnly(false);
										txtDescNatureza.setValue(event.getNaturezaOperacao().getDescricao());
										txtDescNatureza.setReadOnly(true);
										NaturezaSelecionada = event.getNaturezaOperacao();						
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});
				
				
				FormLayout frmCodigoNatureza = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(txtCodNatureza);							
					}
				};
				addComponent(frmCodigoNatureza);
		
				FormLayout frmButtonSearchNatureza =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");										
						addComponent(btSearchNatureza);							
					}
				}; 
							
				FormLayout frmDescNatureza = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(txtDescNatureza);							
					}
				}; 
				addComponent(frmButtonSearchNatureza);
				addComponent(frmDescNatureza);
				setExpandRatio(frmDescNatureza, 1);		
				
				
				if(txtCodNatureza != null && NaturezaSelecionada != null){
					txtCodNatureza.setValue(NaturezaSelecionada.getId().toString());
					txtCodNatureza.setReadOnly(true);
				}else{
					txtCodNatureza.setReadOnly(true);
				}
				
				if(txtDescNatureza != null && NaturezaSelecionada != null){
					txtDescNatureza.setReadOnly(false);
					txtDescNatureza.setValue(NaturezaSelecionada.getDescricao());
					txtDescNatureza.setReadOnly(true);
				}

			}	
		});
		
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
					addComponent(new FormLayout(){					
							{
									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
										
									ComboBox cbTipoVenda = new ComboBox("Natureza Itens");
									cbTipoVenda.setNullSelectionAllowed(false);
									cbTipoVenda.setTextInputAllowed(false);
									cbTipoVenda.setRequired(true);
									cbTipoVenda.addItem("PRODUTO");
									cbTipoVenda.addItem("SERVICO");
									cbTipoVenda.focus();
									
									cbTipoVenda.setStyleName("caption-align-pedido");
									addComponent(cbTipoVenda);	
									fieldGroup.bind(cbTipoVenda, "tipoVenda");
									
									cbTipoVenda.setReadOnly(true);
							}
					});
					
					addComponent(new FormLayout(){					
						{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
									
								cbEntrega = new ComboBox("Entrega");
								cbEntrega.setNullSelectionAllowed(false);
								cbEntrega.setTextInputAllowed(false);
								cbEntrega.setRequired(true);
								cbEntrega.addItem("SIM");
								cbEntrega.addItem("NAO");
								
								cbEntrega.setImmediate(true);
								cbEntrega.addValueChangeListener(new Property.ValueChangeListener() {
									
									@Override
									public void valueChange(ValueChangeEvent event) {
										if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("SIM")){
											
											if(cbEndereco != null){
												cbEndereco.setReadOnly(false);
											}
											
											if(txtComplemento != null){
												txtComplemento.setReadOnly(false);
											}
											
											if(txtReferencia != null){
												txtReferencia.setReadOnly(false);
											}
											
											if(cbTrasportadora != null){
												cbTrasportadora.setReadOnly(false);
											}
											
											
											
		
											cbEndereco.setContainerDataSource(getEnderecos());
											//fieldGroup.bind(cbEndereco, "end"); 
											
										}
										
										if(cbEntrega.getValue() != null && cbEntrega.getValue().equals("NAO")){
											
											if(cbEndereco != null){
												cbEndereco.setReadOnly(true);									
											}
											
											if(txtComplemento != null){
												txtComplemento.setReadOnly(true);
											}
											
											if(txtReferencia != null){
												txtReferencia.setReadOnly(true);
											}
											
											if(cbTrasportadora != null){
												cbTrasportadora.setReadOnly(true);
											}
											
											
											if(fieldGroup.getField("end") != null){
												fieldGroup.unbind(cbEndereco);
											}
											
										}
									}
								});
								
								cbEntrega.setStyleName("caption-align-pedido");
								addComponent(cbEntrega);		
								fieldGroup.bind(cbEntrega, "entregar");
								
								cbEntrega.setReadOnly(true);
						}
				});
			}
		});
		
		
		vlRoot.addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
																	
								tbItens = new Table("Itens"){
									@Override
									protected String formatPropertyValue(Object rowId, Object colId,
											Property<?> property) {
										
										if(colId.equals("VALOR (R$)") && tbItens.getItem(rowId).getItemProperty(colId).getValue() != null){
											
											
											double valor = Real.formatStringToDBDouble(tbItens.getItem(rowId).getItemProperty(colId).getValue().toString());
											total_itens = total_itens + valor;
											
											lbTotaisItens = new Label("<span style='text-align: right;display: block;'>R$ "+Real.formatDbToString(String.valueOf(total_itens))+"</span>", ContentMode.HTML);
											
											hlTotal.removeAllComponents();
											hlTotal.addComponent(lbTotaisItens); 
											hlTotal.setComponentAlignment(lbTotaisItens, Alignment.BOTTOM_RIGHT);				
											
											
											return "R$ "+tbItens.getItem(rowId).getItemProperty(colId).getValue().toString();
										}				
										
									
										
										return super.formatPropertyValue(rowId, colId, property);
									
										
									}
								};
								tbItens.addContainerProperty("COD", Integer.class, null);
								tbItens.addContainerProperty("NOME", String.class,"");
								tbItens.addContainerProperty("QTD", String.class, "");
								tbItens.addContainerProperty("VALOR (R$)", String.class, "");
								tbItens.addContainerProperty("TOTAL (R$)", String.class, "");
								tbItens.addContainerProperty("C", String.class, "");
								
								tbItens.setColumnAlignment("VALOR (R$)", Align.RIGHT);
								tbItens.setColumnAlignment("TOTAL (R$)", Align.RIGHT);

								tbItens.setWidth("690px");
								tbItens.setHeight("141px");
								tbItens.addStyleName("tb-itens");
								tbItens.addStyleName("caption-align-pedido");
								
								tbItens.setCellStyleGenerator(new Table.CellStyleGenerator() {
									
									@Override
									public String getStyle(Table source, Object itemId, Object propertyId) {
										 if (propertyId == null)
							                    return "row-header-default"; // Will not actually be visible
							               
							                Item item = source.getItem(itemId); 
							                if(item != null){
								                
								                if(item.getItemProperty("C").getValue() != null){
									                String cancelado = item.getItemProperty("C").getValue().toString();
									                
									                if(cancelado.equals("S")){
									                    return "row-header-pedido-cancelado"; 
									                }else {									                
									                    return "row-header-default"; 
									                }
								                
								                }else{
								                	 return "row-header-default";
								                }

							                }else{
							                	 return "row-header-default";
							                }
									}
								});
								
								tbItens.setColumnAlignment("VALOR", Align.RIGHT);
								//tbItens.set

								
								List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido((Integer)item.getItemProperty("id").getValue());
								double valorTotal = 0;
								for (EcfPreVendaDetalhe s : itens) {
									
									if(item.getItemProperty("tipoVenda").getValue().equals("PRODUTO")){
										Produto p = ProdutoDAO.find(s.getProdutoId());	
										if(p !=null){
											tbItens.addItem(new Object[]{p.getId(), p.getNome(), Real.formatDbToString(s.getQuantidade().toString()),Real.formatDbToString(s.getValorUnitario().toString()),
													Real.formatDbToString(s.getValorTotal().toString()), s.getCancelado() }, tbItens.getItemIds().size()+1);																			
										}
									}
									
									if(item.getItemProperty("tipoVenda").getValue().equals("SERVICO")){
										Servico p = ServicoDAO.find(s.getProdutoId());			
										if(p != null){
											tbItens.addItem(new Object[]{p.getId(), p.getNome(), Real.formatDbToString(s.getQuantidade().toString()),Real.formatDbToString(s.getValorUnitario().toString()),
													Real.formatDbToString(s.getValorTotal().toString()), s.getCancelado() }, tbItens.getItemIds().size()+1);																			
										}
									}
								}								
								
								addComponent(tbItens);
								hlTotal  = new HorizontalLayout(){
									{
										setWidth("689px");
										double valorTotal = total_itens;
										lbTotaisItens = new Label("<span style='text-align: right;display: block;'>R$ "+Real.formatDbToString(String.valueOf(valorTotal))+"</span>", ContentMode.HTML);
										//lbTotaisItens.setWidth("162px");
										
										addComponent(lbTotaisItens); 
										setComponentAlignment(lbTotaisItens, Alignment.BOTTOM_RIGHT);										
									}
								};
								addComponent(hlTotal);
								
							}
					});			
		
		
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
				
				addComponent(new VerticalLayout(){
					{
						addComponent(new FormLayout(){					
							{
									setStyleName("form-cutom-new");
									setMargin(true);
									setSpacing(true);
										
									TextField txtFormaPgto = new TextField("Forma Pagamento");
									txtFormaPgto.setWidth("400px");
									txtFormaPgto.setRequired(true);

									
									EntityItem<EcfPreVendaCabecalho> prevenda =(EntityItem<EcfPreVendaCabecalho>) item;
									List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(prevenda.getEntity());
									
									for (TotaisPedido totaisPedido : totais) {
										if(totaisPedido.getForma_pgto() != null){
											String vlr = totaisPedido.getForma_pgto().getNome()+" R$ "+Real.formatDbToString(String.valueOf(totaisPedido.getValor()));
											txtFormaPgto.setValue(txtFormaPgto.getValue() != null && !txtFormaPgto.getValue().equals("") && !txtFormaPgto.getValue().isEmpty()? txtFormaPgto.getValue()+" | "+vlr : ""+vlr);						
										}
										if(totaisPedido.getHaver() != null){
											String vlr = "HAVER R$ "+Real.formatDbToString(String.valueOf(totaisPedido.getValor()));
											txtFormaPgto.setValue(txtFormaPgto.getValue() != null && !txtFormaPgto.getValue().equals("") && !txtFormaPgto.getValue().isEmpty()? txtFormaPgto.getValue()+" | "+vlr : ""+vlr);						
										}
									}
									
									txtFormaPgto.setReadOnly(true);
								
									
									txtFormaPgto.setStyleName("caption-align-pedido");
									addComponent(txtFormaPgto);	
									
							}
						});
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								TextArea  txtObservacao = new TextArea("Obs");
								txtObservacao.setStyleName("caption-align-pedido");
								txtObservacao.setWidth("400px");
								txtObservacao.setHeight("80px");
								txtObservacao.setValue(item.getItemProperty("obs").getValue() != null ? item.getItemProperty("obs").getValue().toString() : "");				
								txtObservacao.setReadOnly(true); 
								
								addComponent(txtObservacao);
								
							}
						});
						
						
					}
				});
				
				addComponent(new VerticalLayout(){
					{
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
								
								TextField txtAcrescimo = new TextField ("Acréscimo (R$)");								
								txtAcrescimo.setStyleName("caption-align-pedido2");	
								txtAcrescimo.addStyleName("align-currency");
								txtAcrescimo.addStyleName("bold");
								txtAcrescimo.setWidth("168px");	
								
								txtAcrescimo.setValue(item.getItemProperty("total_acres").getValue() != null ? Real.formatDbToString(String.valueOf(item.getItemProperty("total_acres").getValue()))  : "");		
								txtAcrescimo.setReadOnly(true);
								
								addComponent(txtAcrescimo); 
							}
						});
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
								
								TextField txtSubTotal = new TextField ("SubTotal (R$)");								
								txtSubTotal.setStyleName("caption-align-pedido2");	
								txtSubTotal.addStyleName("align-currency");
								txtSubTotal.addStyleName("bold");
								txtSubTotal.setWidth("168px");	
								
								//String subtotal = Real.formatDbToString(String.valueOf((Float)item.getItemProperty("subTotal").getValue()+(Float)item.getItemProperty("total_acres").getValue()));
								txtSubTotal.setValue(item.getItemProperty("total_acres").getValue() != null ? Real.formatDbToString(String.valueOf((Float)item.getItemProperty("subTotal").getValue()+(Float)item.getItemProperty("total_acres").getValue())) :  Real.formatDbToString(String.valueOf(item.getItemProperty("subTotal").getValue())));		
								txtSubTotal.setReadOnly(true);
								
								addComponent(txtSubTotal);
								
							}
						});
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								TextField txtDesconto = new TextField ("Desconto (%)");								
								txtDesconto.setStyleName("caption-align-pedido2");	
								txtDesconto.addStyleName("align-currency");
								txtDesconto.addStyleName("bold");
								txtDesconto.setWidth("168px");	
								
								txtDesconto.setValue(item.getItemProperty("total_desc").getValue() != null ?Real.formatDbToString(String.valueOf(item.getItemProperty("total_desc").getValue()))  : "");		
								txtDesconto.setReadOnly(true);
								
								addComponent(txtDesconto); 
							}
						});
						
						
						addComponent(
								new FormLayout(){					
								{
									setStyleName("form-cutom-new");
									setMargin(true);
									setSpacing(true);
																		
									TextField txtValor = new TextField ("Total (R$)");								
									txtValor.setStyleName("caption-align-pedido2");	
									txtValor.addStyleName("align-currency");
									txtValor.addStyleName("bold");
									
									txtValor.setNullRepresentation("");					
									txtValor.setWidth("168px");								
													
									addComponent(txtValor);		
									
									if(item.getItemProperty("valor").getValue() != null){
										txtValor.setValue("R$ "+Real.formatDbToString(item.getItemProperty("valor").getValue().toString()));
									}else{
										txtValor.setValue("R$ 0,00");
									}
									txtValor.setReadOnly(true);
								}
						});
					}
				});
			}
		});
		
		
		
		
		
					
			
	}
	
	private JPAContainer<Fornecedor> getTransportadoras(){
		JPAContainer<Fornecedor> transportadoras = JPAContainerFactory.make(Fornecedor.class, ConnUtil.getEntity());
		transportadoras.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
		transportadoras.addContainerFilter(Filters.eq("transportadora", true));
		
		return transportadoras;
	}
	private JPAContainer<Endereco> getEnderecos(){
		JPAContainer<Endereco> container = JPAContainerFactory.make(Endereco.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("clientes", ClienteSelecionado));
		
		return container;
	}
	
	
	@Override
	public Button buildBtSalvar() {
		
		btItens = new Button("(F2) Itens", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				boolean allow = true;
				
				if(cbTipo != null && cbTipo.getValue().toString().equals("DEVOLUCAO") && ClienteSelecionado == null){
					allow = false;
				}
				
				
				if(fieldGroup.isValid() && NaturezaSelecionada != null && allow){
					try {
						
						if(NaturezaSelecionada.getTipo().equals("ENTRADA")){
							if(PreVendaDevolucaoSelecionado != null){
								if(ClienteSelecionado != null){
									ClienteSelecionado.setDdd_fone1(txtDDD1.getValue());
									ClienteSelecionado.setDdd_fone2(txtDDD2.getValue());
									ClienteSelecionado.setTelefone1(txtTelefone1.getValue());
									ClienteSelecionado.setTelefone2(txtTelefone2.getValue());
									ClienteSelecionado.setDdd_cel1(txtDDD3.getValue());
									ClienteSelecionado.setDdd_cel2(txtDDD4.getValue());
									ClienteSelecionado.setCelular1(txtTelefone3.getValue());
									ClienteSelecionado.setCelular2(txtTelefone4.getValue());						
									ClienteSelecionado.setEmail(txtEmail.getValue());
									
									item.getItemProperty("cliente").setValue(ClienteSelecionado);
								}
								
								item.getItemProperty("n_nf_devolucao").setValue(PreVendaDevolucaoSelecionado);								
								item.getItemProperty("naturezaOperacao").setValue(NaturezaSelecionada);
								item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
		
								if(cbUsuario != null && cbUsuario.getValue() != null){
									item.getItemProperty("vendedor").setValue(cbUsuario.getValue().toString());
								}
								
								if(cbEndereco.getValue() != null){
									Endereco end = EnderecoDAO.find(Integer.parseInt(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("id").getValue().toString()));
									end.setComplemento(txtComplemento.getValue());
									end.setReferencia(txtReferencia.getValue());
									item.getItemProperty("end").setValue(end);
								}											
								
								
								fieldGroup.commit();
								fireEvent(new PedidoEvent(getUI(), item, true));	
							}else{
								Notify.Show("Necessita informar uma PreVenda para Devolução", Notify.TYPE_ERROR);
							}
						}else{
						
								if(ClienteSelecionado != null){
									ClienteSelecionado.setDdd_fone1(txtDDD1.getValue());
									ClienteSelecionado.setDdd_fone2(txtDDD2.getValue());
									ClienteSelecionado.setTelefone1(txtTelefone1.getValue());
									ClienteSelecionado.setTelefone2(txtTelefone2.getValue());
									ClienteSelecionado.setDdd_cel1(txtDDD3.getValue());
									ClienteSelecionado.setDdd_cel2(txtDDD4.getValue());
									ClienteSelecionado.setCelular1(txtTelefone3.getValue());
									ClienteSelecionado.setCelular2(txtTelefone4.getValue());						
									ClienteSelecionado.setEmail(txtEmail.getValue());
									
									item.getItemProperty("cliente").setValue(ClienteSelecionado);
								}
								
								item.getItemProperty("naturezaOperacao").setValue(NaturezaSelecionada);
								item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
		
								if(cbUsuario != null && cbUsuario.getValue() != null){
									item.getItemProperty("vendedor").setValue(cbUsuario.getValue().toString());
								}
								
								if(cbEndereco.getValue() != null){
									Endereco end = EnderecoDAO.find(Integer.parseInt(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("id").getValue().toString()));
									end.setComplemento(txtComplemento.getValue());
									end.setReferencia(txtReferencia.getValue());
									item.getItemProperty("end").setValue(end);
								}											
								
								
								fieldGroup.commit();
								fireEvent(new PedidoEvent(getUI(), item, true));						
						}
					} catch (Exception e) {					
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
					}
				}else{
					
					for (Field<?> field: fieldGroup.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
						
						if(NaturezaSelecionada == null){
							txtCodNatureza.addStyleName("invalid-txt");
						}else{
							txtCodNatureza.removeStyleName("invalid-txt");
						}		
						
						if(cbTipo.getValue() != null && cbTipo.getValue().toString().equals("DEVOLUCAO")){
							if(!txtCodPreVendaDevolucao.isValid()){
								txtCodPreVendaDevolucao.addStyleName("invalid-txt");
							}else{
								txtCodPreVendaDevolucao.removeStyleName("invalid-txt");
							}
							 
							if(!txtCodCliente.isValid()){
								txtCodCliente.addStyleName("invalid-txt");
							}else{
								txtCodCliente.removeStyleName("invalid-txt");
							}
						}else{
							txtCodPreVendaDevolucao.removeStyleName("invalid-txt");
							txtCodCliente.removeStyleName("invalid-txt");
						}
					
					}
					
					Notify.Show_Invalid_Submit_Form();
					
				}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btItens.click();
			}
		};
		
		btItens.addShortcutListener(slbtOK);
		
		btItens.setStyleName("default");
		return btItens;
	}
	
	public Button buildBtSave(){
		btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && ClienteSelecionado != null && NaturezaSelecionada != null){
					try{
						ClienteSelecionado.setDdd_fone1(txtDDD1.getValue());
						ClienteSelecionado.setDdd_fone2(txtDDD2.getValue());
						ClienteSelecionado.setTelefone1(txtTelefone1.getValue());
						ClienteSelecionado.setTelefone2(txtTelefone2.getValue());
						ClienteSelecionado.setDdd_cel1(txtDDD3.getValue());
						ClienteSelecionado.setDdd_cel2(txtDDD4.getValue());
						ClienteSelecionado.setCelular1(txtTelefone3.getValue());
						ClienteSelecionado.setCelular2(txtTelefone4.getValue());						
						ClienteSelecionado.setEmail(txtEmail.getValue());
						
						item.getItemProperty("naturezaOperacao").setValue(NaturezaSelecionada);
						item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
						item.getItemProperty("cliente").setValue(ClienteSelecionado);
						
						if(cbUsuario != null && cbUsuario.getValue() != null){
							item.getItemProperty("vendedor").setValue(cbUsuario.getValue().toString());
						}
						
						if(cbEndereco.getValue() != null){
							Endereco end = EnderecoDAO.find(Integer.parseInt(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("id").getValue().toString()));
							end.setComplemento(txtComplemento.getValue());
							end.setReferencia(txtReferencia.getValue());
							item.getItemProperty("end").setValue(end);
						}											
						
						fieldGroup.commit();
						
						
						fireEvent(new PedidoEvent(getUI(), item, false));			
						close();
					}catch(Exception e){								
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
					}
				}else{
					Notify.Show_Invalid_Submit_Form();
				}
			}
		});
		
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
								
				GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja Cancelar este Pedido ?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						
						if(event.isConfirm()){
							boolean check = PedidoDAO.CancelarPedido(PedidoDAO.find((Integer)item.getItemProperty("id").getValue()));
							
							
							
							if(check){
								close();
								Notify.Show("Pedido Cancelado com Sucesso",Notify.TYPE_SUCCESS);
							}
						}else{							
							close();							
						}
					}
				});
				
				
				getUI().addWindow(gd);						
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
	
	
	public Button buildBtFechar() {
		btFechar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {								
				close();			
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btFechar.click();
			}
		};
		
		btFechar.addShortcutListener(slbtCancelar);
		
		
		return btFechar;
	}
	
	
	public void addListerner(PedidoListerner target){
		try {
			Method method = PedidoListerner.class.getDeclaredMethod("onClose", PedidoEvent.class);
			addListener(PedidoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(PedidoListerner target){
		removeListener(PedidoEvent.class, target);
	}
	public static class PedidoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public PedidoEvent(Component source, Item item, boolean confirm) {
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
	public interface PedidoListerner extends Serializable{
		public void onClose(PedidoEvent event);
	}

	
}
