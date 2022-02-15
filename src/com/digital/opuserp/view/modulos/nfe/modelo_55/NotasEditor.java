package com.digital.opuserp.view.modulos.nfe.modelo_55;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.NfeDAO;
import com.digital.opuserp.dao.NfeDAO55;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.TotaisPedidoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ConfigNfeMod55;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.TotaisPedido;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.PedidoUtil2;
import com.digital.opuserp.view.util.PedidoUtil2.PedidoEvent;
import com.digital.opuserp.view.util.PedidoUtil2.PedidoListerner;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class NotasEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String  cepAtual;
	TextField txtCodPedido;
	TextField txtNomeCliente;
	TextField txtUfDestinatario;
	TextField txtMunicipioDestinatario;
	TextField txtFormaPgto;
	TextField txtNaturezaOperacao;
	EcfPreVendaCabecalho pedidoSelecionado;
	Table tb;
	ComboBox cbMunicipio;
	
	private TextField txtCodNatureza;
	private TextField txtDescNatureza;
	
	
	
	Integer cod_submodulo;
	ConfigNfeMod55 default_value;
		
	public NotasEditor(Item item, String title, boolean modal, Integer cod_submodulo){
	
		this.item = item;
		this.cod_submodulo = cod_submodulo;
		
		default_value = NfeDAO55.getDefaultValue();
		
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
				
				HorizontalLayout hl_footer = new HorizontalLayout();
				hl_footer.setWidth("100%");
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				hl_footer.addComponent(hlButtons);
				hl_footer.setComponentAlignment(hlButtons, Alignment.MIDDLE_RIGHT);
				
				addComponent(hl_footer);
			}
		});
			
		buildLayout();
	}
		
	private void configLayout(){
				setWidth("950px");
				setHeight("620px");					
	}
	
	public void buildLayout(){	
		fieldGroup = new FieldGroup(item);
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");
						
						TextField txtModelo = new TextField("Modelo");
						txtModelo.setRequired(true);
						txtModelo.setNullRepresentation("");
						txtModelo.setValue("55");
						txtModelo.setReadOnly(true);
						txtModelo.setWidth("60px");
						
						txtModelo.setStyleName("caption-align-nfe");
						
						addComponent(txtModelo);
						
						fieldGroup.bind(txtModelo, "modelo");
						txtModelo.setValue("55");
					}
				});
				
				addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");
						
						TextField txtSerie = new TextField("Série");
						txtSerie.setRequired(true);
						txtSerie.setNullRepresentation("");
						txtSerie.setValue("001");
						txtSerie.setWidth("60px");

						addComponent(txtSerie);
						fieldGroup.bind(txtSerie, "serie");
						
						txtSerie.setValue("001");
						
					}
				});
				addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");
						
						TextField txtNumero = new TextField("Número");
						txtNumero.setRequired(true);
						txtNumero.setNullRepresentation("");
						txtNumero.setValue("000000002");
						
						addComponent(txtNumero);
						fieldGroup.bind(txtNumero, "numero");
						
						txtNumero.setValue(NfeDAO.getNextID());
					}
				});
				
				addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");
						
						DateField txtDataHoraEmissao = new DateField("Data e hora de emissão");
						txtDataHoraEmissao.setWidth("141px");
						txtDataHoraEmissao.setRequired(true);
						txtDataHoraEmissao.setDateFormat("dd/MM/yyyy");
						String dh = DataUtil.formatDateBra(new Date())+" "+DataUtil.formatHoraBra(new Date());			
						
						addComponent(txtDataHoraEmissao);
						fieldGroup.bind(txtDataHoraEmissao, "data_emissao");
						txtDataHoraEmissao.setValue(new Date());
						txtDataHoraEmissao.setEnabled(false); 
					}
				});
			}
			
		});
		
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				ComboBox cbTipoDoc = new ComboBox("Tipo do documento");
				cbTipoDoc.setRequired(true);
				cbTipoDoc.setNullSelectionAllowed(false);
				cbTipoDoc.setTextInputAllowed(false);
				cbTipoDoc.addItem("0 - Entrada");
				cbTipoDoc.addItem("1 - Saída");
				cbTipoDoc.setStyleName("caption-align-nfe");
				
				cbTipoDoc.select("1 - Saída");
				
				addComponent(cbTipoDoc); 
				fieldGroup.bind(cbTipoDoc, "tipo_documento");
				cbTipoDoc.select(default_value.getTipoDocumento());
			}
		});
		vlRoot.addComponent(new FormLayout(){
			{
			setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				ComboBox cbFormaEmissao = new ComboBox("Forma de emissão");
				cbFormaEmissao.setRequired(true);
				cbFormaEmissao.setNullSelectionAllowed(false);
				cbFormaEmissao.setTextInputAllowed(false);
				cbFormaEmissao.addItem("Normal");
				cbFormaEmissao.addItem("Contingência FS-IA");
				cbFormaEmissao.addItem("Contingência via EPEC");
				cbFormaEmissao.addItem("Contingência via DPEC");
				cbFormaEmissao.addItem("Contingência FS-DA");
				cbFormaEmissao.addItem("Contingência SVC-AN");
				cbFormaEmissao.addItem("Contingência SVC-RS");
				cbFormaEmissao.setStyleName("caption-align-nfe");
				
				cbFormaEmissao.select("Normal");
				
				addComponent(cbFormaEmissao); 
				fieldGroup.bind(cbFormaEmissao, "forma_emissao");
				cbFormaEmissao.select(default_value.getFormaEmissao());
			}
		});
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				ComboBox cbConsumidorFinal = new ComboBox("Consumidor final");
				cbConsumidorFinal.setRequired(true);
				cbConsumidorFinal.setNullSelectionAllowed(false);
				cbConsumidorFinal.setTextInputAllowed(false);
				cbConsumidorFinal.addItem("0 - Não");
				cbConsumidorFinal.addItem("1 - Sim");
				cbConsumidorFinal.setStyleName("caption-align-nfe");
				
				cbConsumidorFinal.select("1 - Sim");
				
				addComponent(cbConsumidorFinal); 
				fieldGroup.bind(cbConsumidorFinal, "consumidor_final");
				
				cbConsumidorFinal.select(default_value.getConsumidorFinal());
			}
		});
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				ComboBox cbDestinoOperacao = new ComboBox("Destino da Operação");
				cbDestinoOperacao.setRequired(true);
				cbDestinoOperacao.setNullSelectionAllowed(false);
				cbDestinoOperacao.setTextInputAllowed(false);
				cbDestinoOperacao.addItem("1 - Operacao Interna");
				cbDestinoOperacao.addItem("2 - Operacao Interestadual");
				cbDestinoOperacao.addItem("3 - Operação com Exterior");
				cbDestinoOperacao.setStyleName("caption-align-nfe");
				
				cbDestinoOperacao.select("1 - Operacao Interna");
				
				addComponent(cbDestinoOperacao); 
				fieldGroup.bind(cbDestinoOperacao, "destino_operacao");
				
				cbDestinoOperacao.select(default_value.getDestinoOperacao());
			}
		});
		vlRoot.addComponent(new FormLayout(){
			{
			setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				ComboBox cbTipoAtendi = new ComboBox("Tipo de atendimento");
				cbTipoAtendi.setRequired(true);
				cbTipoAtendi.setNullSelectionAllowed(false);
				cbTipoAtendi.setTextInputAllowed(false);
				cbTipoAtendi.addItem("Não se aplica");
				cbTipoAtendi.addItem("1 - Operação Presencial");
				cbTipoAtendi.addItem("2 - Operação NÃO Presencial, pela INTERNET");
				cbTipoAtendi.addItem("3 - Operação NÃO Presencial, pela TELEATENDIMENTO");
				cbTipoAtendi.addItem("9 - Operação NÃO Presencial, OUTROS");
				cbTipoAtendi.setStyleName("caption-align-nfe");
				
				cbTipoAtendi.select("Não se aplica");
				
				addComponent(cbTipoAtendi); 
				fieldGroup.bind(cbTipoAtendi, "tipo_atendimento");
				
				cbTipoAtendi.select(default_value.getTipoAtendimento());
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
				
				txtCodPedido = new TextField("Pedido");				
				txtCodPedido.setWidth("60px");				
				txtCodPedido.setNullRepresentation("");
				txtCodPedido.setStyleName("caption-align-nfe");
				txtCodPedido.setId("txtCodPedido");
				
				JavaScript.getCurrent().execute("$('#txtCodPedido').mask('0000000000')");
				txtCodPedido.setImmediate(true);
						
				
				
				txtCodPedido.addListener(new TextChangeListener() {
					
					@Override 
					public void textChange(TextChangeEvent event) {
						PedidoDAO cDAO = new PedidoDAO();
						pedidoSelecionado = new EcfPreVendaCabecalho();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							pedidoSelecionado = cDAO.find(Integer.parseInt(event.getText()));		
							
							if(pedidoSelecionado != null){
								txtNomeCliente.setReadOnly(false);
								txtFormaPgto.setReadOnly(false);
								txtNaturezaOperacao.setReadOnly(false);		
								txtUfDestinatario.setReadOnly(false);
								txtMunicipioDestinatario.setReadOnly(false); 
								
								txtNomeCliente.setValue(pedidoSelecionado.getCliente() != null ? pedidoSelecionado.getCliente().getNome_razao() : "");
								txtUfDestinatario.setValue(pedidoSelecionado.getCliente() != null && pedidoSelecionado.getCliente().getEndereco_principal() != null ? pedidoSelecionado.getCliente().getEndereco_principal().getUf() : "");
								txtMunicipioDestinatario.setValue(pedidoSelecionado.getCliente() != null && pedidoSelecionado.getCliente().getEndereco_principal() != null ? pedidoSelecionado.getCliente().getEndereco_principal().getCidade() : "");
																								
								List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedidoSelecionado);
								
								for (TotaisPedido totaisPedido : totais) {
									//String vlr = totaisPedido.getForma_pgto().getNome()+" R$ "+Real.formatDbToString(String.valueOf(totaisPedido.getValor()));
									txtFormaPgto.setValue(totaisPedido.getForma_pgto().getNfe());						
								}
									
								txtNaturezaOperacao.setValue(pedidoSelecionado.getNaturezaOperacao().getDescricao());
								txtFormaPgto.setReadOnly(true);
								txtNomeCliente.setReadOnly(true);
								txtNaturezaOperacao.setReadOnly(true); 								
								
								List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(pedidoSelecionado.getId());
								
								Integer i = 0;
								for (EcfPreVendaDetalhe item : itens) {
									Produto p = ProdutoDAO.find(item.getProdutoId());								
									tb.addItem(new Object[]{String.valueOf( i+1),p.getId().toString(), p.getNome(),p.getNcm(),p.getSimples_nacional().getSimples_nacional_cod().toString(),p.getCfop() != null ? p.getCfop().getId().toString()+" - "+p.getCfop().getDescricao() : "0",p.getUnidadeProduto().getNome(),String.valueOf(item.getQuantidade()),Real.formatDbToString(String.valueOf(item.getValorUnitario())),Real.formatDbToString(String.valueOf(item.getValorTotal()))}, i);
									i++;
								}
								
							}else {
								txtNomeCliente.setReadOnly(false);
								txtFormaPgto.setReadOnly(false);
								txtNaturezaOperacao.setReadOnly(false);
								txtUfDestinatario.setReadOnly(false);
								txtMunicipioDestinatario.setReadOnly(false);
								txtNomeCliente.setValue("");
								txtFormaPgto.setValue("");
								txtUfDestinatario.setValue("");
								txtNaturezaOperacao.setValue("");
								txtMunicipioDestinatario.setValue("");
								txtMunicipioDestinatario.setReadOnly(true); 
								txtUfDestinatario.setReadOnly(true);
								txtNaturezaOperacao.setReadOnly(true);
								txtFormaPgto.setReadOnly(true);
								txtNomeCliente.setReadOnly(true);
								
								tb.removeAllItems();
							}
						}else{
							txtNomeCliente.setReadOnly(false);
							txtFormaPgto.setReadOnly(false);
							txtNaturezaOperacao.setReadOnly(false);
							txtUfDestinatario.setReadOnly(false);
							txtMunicipioDestinatario.setReadOnly(false);
							txtNomeCliente.setValue("");
							txtFormaPgto.setValue("");
							txtNaturezaOperacao.setValue("");
							txtUfDestinatario.setValue("");
							txtMunicipioDestinatario.setValue("");
							txtUfDestinatario.setReadOnly(true);
							txtMunicipioDestinatario.setReadOnly(true);
							txtNaturezaOperacao.setReadOnly(true);
							txtFormaPgto.setReadOnly(true);
							txtNomeCliente.setReadOnly(true);	
							
							
							tb.removeAllItems();
							
						}
					}
				});

				txtCodPedido.setRequired(true);		
				txtNomeCliente = new TextField("Cliente");
				//txtNomeCliente.setTabIndex(2000);
				txtNomeCliente.setReadOnly(true);
				txtNomeCliente.setWidth("722px");
				txtNomeCliente.addStyleName("caption-align-nfe");
								
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setTabIndex(300000);
				//btSearchCliente.setEnabled(false);
				btSearchCliente.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						PedidoUtil2 cUtil = new PedidoUtil2(true, true);
						cUtil.addListerner(new PedidoListerner() {
							
							@Override
							public void onSelected(PedidoEvent event) {
									if(event.getPedido() != null){
										txtCodPedido.setValue(event.getPedido().getId().toString());
										
										txtNomeCliente.setReadOnly(false);
										txtFormaPgto.setReadOnly(false);
										txtNaturezaOperacao.setReadOnly(false);
										txtUfDestinatario.setReadOnly(false);
										txtMunicipioDestinatario.setReadOnly(false);
										
										List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(event.getPedido());
										
										for (TotaisPedido totaisPedido : totais) {
											String vlr = totaisPedido.getForma_pgto().getNome()+" R$ "+Real.formatDbToString(String.valueOf(totaisPedido.getValor()));
											txtFormaPgto.setValue(totaisPedido.getForma_pgto().getNfe());						
										}
										
										txtNaturezaOperacao.setValue(event.getPedido().getNaturezaOperacao().getDescricao());
										txtNomeCliente.setValue(event.getPedido().getCliente() != null ? event.getPedido().getCliente().getNome_razao() : "");
										txtUfDestinatario.setValue(event.getPedido().getCliente() != null && event.getPedido().getCliente().getEndereco_principal() != null ? event.getPedido().getCliente().getEndereco_principal().getUf() : "");
										txtMunicipioDestinatario.setValue(event.getPedido().getCliente() != null && event.getPedido().getCliente().getEndereco_principal() != null ? event.getPedido().getCliente().getEndereco_principal().getCidade() : "");
										
										txtUfDestinatario.setReadOnly(true);
										txtMunicipioDestinatario.setReadOnly(true); 
										txtFormaPgto.setReadOnly(true);
										txtNaturezaOperacao.setReadOnly(true); 
										txtNomeCliente.setReadOnly(true);
										pedidoSelecionado = event.getPedido();						
										
										List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(pedidoSelecionado.getId());
										
										Integer i = 0;
										for (EcfPreVendaDetalhe item : itens) {
											Produto p = ProdutoDAO.find(item.getProdutoId());		
											String cfop = p.getCfop() != null ? p.getCfop().getId().toString()+" - "+p.getCfop().getDescricao() : "0";
											tb.addItem(new Object[]{String.valueOf( i+1),p.getId().toString(), p.getNome(),p.getNcm(),p.getSimples_nacional().getSimples_nacional_cod().toString(),cfop,p.getUnidadeProduto().getNome(),String.valueOf(item.getQuantidade()),Real.formatDbToString(String.valueOf(item.getValorUnitario())),Real.formatDbToString(String.valueOf(item.getValorTotal()))}, i);
											i++;
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
												
						addComponent(txtCodPedido);							
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
						
						//addComponent(txtDescricaoCliente);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);		
				
				
				if(txtCodPedido != null && pedidoSelecionado != null){
					txtCodPedido.setValue(pedidoSelecionado.getId().toString());
				}
				
				if(txtNomeCliente != null && pedidoSelecionado != null){
					txtNomeCliente.setReadOnly(false);
					txtFormaPgto.setReadOnly(false);
					txtNaturezaOperacao.setReadOnly(false);			
					txtUfDestinatario.setReadOnly(false);
					txtMunicipioDestinatario.setReadOnly(false);					
					txtNomeCliente.setValue(pedidoSelecionado.getCliente() != null ? pedidoSelecionado.getCliente().getNome_razao() : "");
					txtUfDestinatario.setValue(pedidoSelecionado.getCliente() != null && pedidoSelecionado.getCliente().getEndereco_principal() != null ? pedidoSelecionado.getCliente().getEndereco_principal().getUf() : "");
					txtMunicipioDestinatario.setValue(pedidoSelecionado.getCliente() != null && pedidoSelecionado.getCliente().getEndereco_principal() != null ? pedidoSelecionado.getCliente().getEndereco_principal().getCidade() : "");
					
					List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedidoSelecionado);
					
					for (TotaisPedido totaisPedido : totais) {
						String vlr = totaisPedido.getForma_pgto().getNome()+" R$ "+Real.formatDbToString(String.valueOf(totaisPedido.getValor()));
						txtFormaPgto.setValue(totaisPedido.getForma_pgto().getNfe());						
					}
					
					txtNaturezaOperacao.setValue(pedidoSelecionado.getNaturezaOperacao().getDescricao());
					txtNaturezaOperacao.setReadOnly(true);
					txtFormaPgto.setReadOnly(true);
					txtNomeCliente.setReadOnly(true);
					txtUfDestinatario.setReadOnly(true);
					txtMunicipioDestinatario.setReadOnly(true); 
					
					List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(pedidoSelecionado.getId());
					
					Integer i = 0;
					for (EcfPreVendaDetalhe item : itens) {
						Produto p = ProdutoDAO.find(item.getProdutoId());
						
						tb.addItem(new Object[]{String.valueOf( i+1),p.getId().toString(),p.getNome(),p.getNcm(),p.getCst_forma_tributo().getCst_tributo_cod(),p.getCfop() != null ? p.getCfop().getId().toString()+" - "+p.getCfop().getDescricao() : "0",
								p.getUnidadeProduto().getNome(),String.valueOf(item.getQuantidade()),Real.formatDbToString(String.valueOf(item.getValorUnitario())),Real.formatDbToString(String.valueOf(item.getValorTotal()))}, i);						
						i++;
					}
				}

			}	
		});
		
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
								
				txtNaturezaOperacao = new TextField("Natureza da operação");				
				txtNaturezaOperacao.setWidth("350px");
				txtNaturezaOperacao.setReadOnly(true); 
				txtNaturezaOperacao.setStyleName("caption-align-nfe");
				addComponent(txtNaturezaOperacao); 
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
								
				txtFormaPgto = new TextField("Forma Pagamento");				
				txtFormaPgto.setNullRepresentation("");
				txtFormaPgto.setWidth("350px");
				txtFormaPgto.setStyleName("caption-align-nfe");
				addComponent(txtFormaPgto); 
				
				fieldGroup.bind(txtFormaPgto, "forma_pagamento");
				txtFormaPgto.setReadOnly(true); 
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				addComponent(txtNomeCliente); 
			}
		});
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtUfDestinatario = new TextField("UF Destinatário");
						txtUfDestinatario.setNullRepresentation("");
						txtUfDestinatario.addStyleName("caption-align-nfe");
						
						addComponent(txtUfDestinatario);
						fieldGroup.bind(txtUfDestinatario, "uf");
						txtUfDestinatario.setReadOnly(true);
					}
				});

				addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtMunicipioDestinatario = new TextField("Município");
						txtMunicipioDestinatario.setNullRepresentation("");
						txtMunicipioDestinatario.addStyleName("caption-align-nfe");
						
						addComponent(txtMunicipioDestinatario); 						
						fieldGroup.bind(txtMunicipioDestinatario, "municipio");
						txtMunicipioDestinatario.setReadOnly(true);
					}
				});
				
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				tb = new Table();
				tb.addContainerProperty("Item", String.class, "");
				tb.addContainerProperty("Código", String.class, "");
				tb.addContainerProperty("Descrição", String.class, "");
				tb.addContainerProperty("NCM", String.class, "");
				tb.addContainerProperty("CSOSN", String.class, "");				
				tb.addContainerProperty("CFOP", String.class, "");
				tb.addContainerProperty("Unid", String.class, "");
				tb.addContainerProperty("Qtde", String.class, "");
				tb.addContainerProperty("Valor Unit", String.class, "");
				tb.addContainerProperty("Valor Total", String.class, "");
				
				tb.setWidth("860px");
				tb.setHeight("145px");
												
				addComponent(tb); 
			}
		});
	}
	
	
	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					
					if(fieldGroup.isValid() && pedidoSelecionado != null){
					
						fieldGroup.commit();
						item.getItemProperty("pedido").setValue(pedidoSelecionado);
						item.getItemProperty("naturezaOperacao").setValue(pedidoSelecionado.getNaturezaOperacao());
						
						fireEvent(new NotasEvent(getUI(), item, true));
						close();	
					}else{
						for (Field<?> field: fieldGroup.getFields()) {
							
							if(!field.isValid()){
								field.addStyleName("invalid-txt");
							}else{
								field.removeStyleName("invalid-txt");
							}	
						}
												
						if(pedidoSelecionado == null){							
							txtCodPedido.addStyleName("invalid-txt");
						}else{
							txtCodPedido.removeStyleName("invalid-txt");
						}
						
						Notify.Show_Invalid_Submit_Form();
					}
				}catch(Exception e){
					e.printStackTrace();
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
				close();			
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
	
	
	
	
	public void addListerner(NotasListerner target){
		try {
			Method method = NotasListerner.class.getDeclaredMethod("onClose", NotasEvent.class);
			addListener(NotasEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(NotasEvent.class, target);
	}
	public static class NotasEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public NotasEvent(Component source, Item item, boolean confirm) {
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
	public interface NotasListerner extends Serializable{
		public void onClose(NotasEvent event);
	}
	
	
}
