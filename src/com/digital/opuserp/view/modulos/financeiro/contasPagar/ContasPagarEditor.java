package com.digital.opuserp.view.modulos.financeiro.contasPagar;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContasDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.CreditoClienteDAO;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Contas;
import com.digital.opuserp.domain.FormaPagamento;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.comp.RealTextField.RealTextField;
import com.digital.opuserp.view.modulos.cadastro.ceps.CepEditor.CepEvent;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.modulos.financeiro.contasPagar.ConfirmarLancamento.ConfirmEvent;
import com.digital.opuserp.view.modulos.financeiro.contasPagar.ConfirmarLancamento.ConfirmListerner;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.NovoTituloEditor.NovoTituloEvent;
import com.digital.opuserp.view.modulos.financeiro.contasReceber.NovoTituloEditor.NovoTituloListerner;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.ContasUtil;
import com.digital.opuserp.view.util.FornecedorUtil;
import com.digital.opuserp.view.util.ContasUtil.ContasEvent;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ContasPagarEditor extends Window implements GenericEditor {

	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	
	
	Item item;
	RealTextField tfValorTitulo; //
	RealTextField tfValorPagamento;
	TextField tfNdoc; //
	DateField dfVencimento; //
	DateField dfPagamento; //
	TextField tfDescricaoConta;
	TextField tfCodConta;
	ComboBox cbTermo;
	ComboBox cbTipo;
	TextField tfDesc;
	TextField tfParcela;
	TextField tfCod;
		
	ComboBox cbIntervalo;
	
	TextField tfQAtd;
	
	private TextField tfDescricaoFornecedor;
	private TextField tfCodFornecedor;	
	private Fornecedor FornecedorSelecionado;	
	private String FormaPgtoSelecionado;	
	private Contas contaSelecionada;
	
	
	String nomeRazao;
	String valorTitulo;
	String valorPagamento;
	String nDoc; 
	Date dataVencimento;
	Date dataPagamento;
	Date datacadastro;
		
	boolean data_venc_valid = false;
	boolean valor_valid = true;
	
	boolean nDoc_valid = true;
	
	String controle;
	Integer codCliente;
	
	
	public ContasPagarEditor(Item item, String title, boolean modal){
		this.item = item;		
		
		setWidth("870px");
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
		
		
		if(item !=null && item.getItemProperty("id") != null && item.getItemProperty("id").getValue() != null){
			contaSelecionada = (Contas) item.getItemProperty("conta").getValue();
			datacadastro = (Date) item.getItemProperty("data_cadastro").getValue();
			FornecedorSelecionado = (Fornecedor) item.getItemProperty("fornecedor").getValue();
			FormaPgtoSelecionado = (String) item.getItemProperty("forma_pgto").getValue();
			
			setHeight("470px");
			
		}

		buildLayout();
	}
	
	
	public String getNextId() {
		ContasDAO cDao = new ContasDAO();
		return cDao.getNextID();
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		HorizontalLayout hl1 = new HorizontalLayout();
		hl1.setWidth("100%");
		
		 hl1.addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						tfCod = new TextField("Código");
						tfCod.setWidth("100px");
						
						if (item.getItemProperty("id").getValue() != null) {
							fieldGroup.bind(tfCod, "id");
						} else {
							tfCod.setValue(getNextId());
						}
						tfCod.addStyleName("caption-align-editar-titulo");
										
						tfCod.setReadOnly(true);
						addComponent(tfCod);
					}
			});
			
			hl1.addComponent(new FormLayout(){
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					
					DateField dfdataCadastro = new DateField("Data Cadastro");
					dfdataCadastro.setStyleName("caption-align-editar-titulo-cp");
					dfdataCadastro.setImmediate(true);
					dfdataCadastro.setDateFormat("dd/MM/yyyy");
					
					if(item.getItemProperty("data_cadastro").getValue()!=null){
						fieldGroup.bind(dfdataCadastro, "data_cadastro");
					}else{
						dfdataCadastro.setValue(new Date());						
					}
							
					dfdataCadastro.setReadOnly(true);
					addComponent(dfdataCadastro);
				}
			});
	
			vlRoot.addComponent(hl1);
			
			if(item.getItemProperty("id").getValue()!=null){	
				
				vlRoot.addComponent(new FormLayout(){
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);
							
							tfNdoc = new TextField("Nº Documento");
							tfNdoc.setWidth("165px");
							tfNdoc.addStyleName("caption-align-editar-titulo");

							fieldGroup.bind(tfNdoc, "n_doc");							
							tfNdoc.setReadOnly(true);
					
							addComponent(tfNdoc);
	
						}
				});
			}	 
			
		vlRoot.addComponent(
				new FormLayout(){
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					cbTermo = new ComboBox("Termo");
					cbTermo.addStyleName("caption-align-editar-titulo");
					cbTermo.addItem("DESPESA");
					cbTermo.addItem("CUSTO");
					cbTermo.focus();
					cbTermo.setNullSelectionAllowed(false);
					cbTermo.setTextInputAllowed(false);
					
					if(item.getItemProperty("termo").getValue()!=null){
						fieldGroup.bind(cbTermo, "termo");							
						cbTermo.setRequired(false);
//						cbTermo.setReadOnly(true);
					}else{
						cbTermo.setRequired(true);
//						cbTermo.setReadOnly(false);
					}
					
					addComponent(cbTermo);

				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						cbTipo = new ComboBox("Tipo");
						cbTipo.addStyleName("caption-align-editar-titulo");
						cbTipo.addItem("FIXA");
						cbTipo.addItem("VARIAVEL");
						cbTipo.setRequired(true);
						cbTipo.setNullSelectionAllowed(false);
						cbTipo.setTextInputAllowed(false);
						
						if(item.getItemProperty("tipo").getValue()!=null){
							fieldGroup.bind(cbTipo, "tipo");							
							cbTipo.setRequired(false);
//							cbTipo.setReadOnly(true);
						}else{
							cbTipo.select("VARIAVEL");
							cbTipo.setRequired(true);
//							cbTipo.setReadOnly(false);
						}
						
						addComponent(cbTipo);
				
					}
				});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				tfCodConta = new TextField("Conta");				
				tfCodConta.setWidth("120px");				
				tfCodConta.setNullRepresentation("");
				tfCodConta.setStyleName("caption-align-editar-titulo");
//				tfCodConta.setId("tfCodFornecedor1");
								
//				JavaScript.getCurrent().execute("$('#tfCodFornecedor1').mask('0000000000')");
				tfCodConta.setImmediate(true);
				
				if(item.getItemProperty("conta").getValue()==null){
					tfCodConta.setValue("2.01.01.01.00");			
					contaSelecionada = ContasDAO.findCod_Ref(("2.01.01.01.00"));	
				}
				
				tfCodConta.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						
						contaSelecionada = new Contas();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							contaSelecionada = ContasDAO.findCod_Ref((event.getText()));		
							
							if(contaSelecionada != null){
								tfDescricaoConta.setReadOnly(false);
								tfDescricaoConta.setValue(contaSelecionada.getDescricao());
								tfDescricaoConta.setReadOnly(true);
								
							}else {
								tfDescricaoConta.setReadOnly(false);
								tfDescricaoConta.setValue("");
								tfDescricaoConta.setReadOnly(true);
															
							}
						}else{
							tfDescricaoConta.setReadOnly(false);
							tfDescricaoConta.setValue("");
							tfDescricaoConta.setReadOnly(true);							
						}
					}
				});

				tfCodConta.setRequired(true);		
				tfDescricaoConta = new TextField();
				tfDescricaoConta.setTabIndex(2000);
				tfDescricaoConta.setValue(contaSelecionada.getDescricao());
				tfDescricaoConta.setReadOnly(true);
				tfDescricaoConta.setWidth("456px");
						
				final Button btSearchConta = new Button();
				btSearchConta.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchConta.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchConta.setTabIndex(300000);
				
				if(item.getItemProperty("conta").getValue() != null){
					//btSearchConta.setEnabled(false);
				}
				btSearchConta.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						ContasUtil cUtil = new ContasUtil(true, true, "não");
						cUtil.addListerner(new ContasUtil.ContasListerner() {
							
							@Override
							public void onSelected(ContasEvent event) {
								if(event.getContas() != null){
									tfCodConta.setValue(event.getContas().getcod_cta_ref().toString());
									tfDescricaoConta.setReadOnly(false);
									tfDescricaoConta.setValue(event.getContas().getDescricao());
									tfDescricaoConta.setReadOnly(true);
									contaSelecionada = event.getContas();
								}
								
							}
						});
					
						getUI().addWindow(cUtil);        
				}
				});
				
				
				FormLayout frmCodigoContas = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(tfCodConta);							
					}
				};
				addComponent(frmCodigoContas);
		
				FormLayout frmButtonSearchConta =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom-new_hide_error_cell");										
						addComponent(btSearchConta);							
					}
				}; 
							
				FormLayout frmDescContas = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoConta);							
					}
				}; 
				addComponent(frmButtonSearchConta);
				addComponent(frmDescContas);
				setExpandRatio(frmDescContas, 1);		
				
				
				if(item.getItemProperty("conta").getValue()!=null){
					tfCodConta.setValue(contaSelecionada.getcod_cta_ref().toString());
					//tfCodConta.setReadOnly(true);
				}
				
				if(tfDescricaoConta != null && contaSelecionada != null){
					tfDescricaoConta.setReadOnly(false);
					tfDescricaoConta.setValue(contaSelecionada.getDescricao());
					tfDescricaoConta.setReadOnly(true);
				}
			}	
		});

		vlRoot.addComponent(new HorizontalLayout(){
			{
				
				tfCodFornecedor = new TextField("Fornecedor");				
				tfCodFornecedor.setWidth("120px");				
				tfCodFornecedor.setNullRepresentation("");
				tfCodFornecedor.setStyleName("caption-align-editar-titulo");
				tfCodFornecedor.setId("tfCodFornecedor2");
				
				
				JavaScript.getCurrent().execute("$('#tfCodFornecedor2').mask('00000000000000')");
				tfCodFornecedor.setImmediate(true);		
				
				tfCodFornecedor.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						FornecedorSelecionado = new Fornecedor();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
//							if(FornecedorSelecionado == null){
//								Integer codEmpresa = OpusERP4UI.getEmpresa().getId();
//								FornecedorSelecionado = FornecedorDAO.findbyCnpj(event.getText(),codEmpresa);
//							}
							
							if(FornecedorSelecionado != null){
								tfDescricaoFornecedor.setReadOnly(false);
								tfDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
								tfDescricaoFornecedor.setReadOnly(true);
								
							}else{
								tfDescricaoFornecedor.setReadOnly(false);
								tfDescricaoFornecedor.setValue("");
								tfDescricaoFornecedor.setReadOnly(true);
															
							}
						}else{
							tfDescricaoFornecedor.setReadOnly(false);
							tfDescricaoFornecedor.setValue("");
							tfDescricaoFornecedor.setReadOnly(true);							
						}
					}
				});

				tfCodFornecedor.setRequired(true);		
				tfDescricaoFornecedor = new TextField();
				tfDescricaoFornecedor.setTabIndex(2000);
				tfDescricaoFornecedor.setReadOnly(true);
				tfDescricaoFornecedor.setWidth("456px");
						
				final Button btSearchFornecedor = new Button();
				btSearchFornecedor.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchFornecedor.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchFornecedor.setTabIndex(300000);

				btSearchFornecedor.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						FornecedorUtil cUtil = new FornecedorUtil(true, true, false);
						cUtil.addListerner(new FornecedorUtil.FornecedorListerner() {
							
							@Override
							public void onSelected(
									FornecedorUtil.FornecedorEvent event) {
									if(event.getFornecedor() != null){
										
										FornecedorSelecionado = event.getFornecedor();

										tfCodFornecedor.setValue(event.getFornecedor().getCnpj().toString());
										tfDescricaoFornecedor.setReadOnly(false);
										tfDescricaoFornecedor.setValue(event.getFornecedor().getRazao_social());
										tfDescricaoFornecedor.setReadOnly(true);
										
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});
				
				
				FormLayout frmCodigoFornecedor = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
												
						addComponent(tfCodFornecedor);							
					}
				};
				addComponent(frmCodigoFornecedor);
		
				FormLayout frmButtonSearchFornecedor =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchFornecedor);							
					}
				}; 
							
				FormLayout frmDescFornecedor = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoFornecedor);							
					}
				}; 
				addComponent(frmButtonSearchFornecedor);
				addComponent(frmDescFornecedor);
				setExpandRatio(frmDescFornecedor, 1);		
				
				
				if(FornecedorSelecionado != null && tfCodFornecedor != null){
					tfCodFornecedor.setValue(FornecedorSelecionado.getCnpj().toString());
//					tfCodFornecedor.setReadOnly(true);
				}
				
				if(FornecedorSelecionado != null && tfDescricaoFornecedor != null){
					tfDescricaoFornecedor.setReadOnly(false);
					tfDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
					tfDescricaoFornecedor.setReadOnly(true);
				}
			}			
		});
		
		
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);						
						
						tfDesc = new TextField("Descrição");
						tfDesc.setStyleName("caption-align-editar-titulo");
						tfDesc.setImmediate(true);
						tfDesc.setNullRepresentation("");
						tfDesc.setWidth("608px");
						tfDesc.setRequired(true);						
						
						addComponent(tfDesc);		
						fieldGroup.bind(tfDesc, "descricao");
					}
			});

		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.setWidth("100%");
		
		if(item.getItemProperty("id").getValue()==null){	
			hl2.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);						
						
						dfVencimento = new DateField("Data Vencimento");
						dfVencimento.setStyleName("caption-align-editar-titulo");
						dfVencimento.setImmediate(true);
						dfVencimento.setDateFormat("dd/MM/yyyy");
						dfVencimento.setRequired(true);
												
						addComponent(dfVencimento);
						fieldGroup.bind(dfVencimento, "data_vencimento");
						
					}
			});
		}	
			
		if(item.getItemProperty("id").getValue()!=null){	
			hl2.addComponent(
				new FormLayout(){
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);						
					
					dfPagamento = new DateField("Data Pagamento");
					dfPagamento.setStyleName("caption-align-editar-titulo");
					dfPagamento.setImmediate(true);
					dfPagamento.setDateFormat("dd/MM/yyyy");
							
					if(item.getItemProperty("data_pago").getValue()!=null){
						fieldGroup.bind(dfPagamento, "data_pago");					
					}
					dfPagamento.setReadOnly(true);
					addComponent(dfPagamento);
					
				}
			});
		}		
		vlRoot.addComponent(hl2);
		
		HorizontalLayout hl3 = new HorizontalLayout();
		hl3.setWidth("100%");
		
			hl3.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);						
						
						tfValorTitulo = new RealTextField("Valor(R$)");
						tfValorTitulo.setStyleName("caption-align-editar-titulo");
						tfValorTitulo.setImmediate(true);
						tfValorTitulo.setWidth("80px");
						tfValorTitulo.setRequired(true);
						tfValorTitulo.setNullRepresentation("");
						tfValorTitulo.setId("txtValorTituloedit1");
						tfValorTitulo.addStyleName("align-currency");

						addComponent(tfValorTitulo);			
						
//						fieldGroup.bind(tfValorTitulo, "valor_titulo");
						
						if(item.getItemProperty("valor_titulo").getValue()!=null){
							tfValorTitulo.setValue(String.valueOf(Real.formatDbToString(item.getItemProperty("valor_titulo").getValue().toString())));							
						}
						
						JavaScript.getCurrent().execute("$('#txtValorTituloedit1').maskMoney({decimal:',',thousands:'.'})");
						
					}
			});
			
			if(item.getItemProperty("id").getValue()!=null){
				
				hl3.addComponent(
					new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);						
						
						tfValorPagamento = new RealTextField("Valor Pagamento(R$)");
						tfValorPagamento.setStyleName("caption-align-editar-titulo");
						tfValorPagamento.setImmediate(true);
						tfValorPagamento.setWidth("80px");
						tfValorPagamento.setRequired(true);
						tfValorPagamento.setNullRepresentation("");
						tfValorPagamento.setId("txtValorTituloedit2");
						tfValorPagamento.addStyleName("align-currency");

//						if(item.getItemProperty("valor_pagamento").getValue()!=null){
//							fieldGroup.bind(tfValorPagamento, "valor_pagamento");							
//						}
						
						if(item.getItemProperty("valor_pagamento").getValue()!=null){
							tfValorPagamento.setValue(String.valueOf(Real.formatDbToString(item.getItemProperty("valor_pagamento").getValue().toString())));							

						}
						
						tfValorPagamento.setReadOnly(true);
						addComponent(tfValorPagamento);			
						JavaScript.getCurrent().execute("$('#txtValorTituloedit2').maskMoney({decimal:',',thousands:'.'})");
						
					}
				});			
			}
		vlRoot.addComponent(hl3);
		
		
		HorizontalLayout hl4 = new HorizontalLayout();
		hl4.setWidth("100%");
		
		hl4.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);						
											
					tfParcela = new TextField("Parcela");
					tfParcela.setStyleName("caption-align-editar-titulo");
					tfParcela.setImmediate(true);
					tfParcela.setWidth("40px");		
					tfParcela.setRequired(true);
					
					fieldGroup.bind(tfParcela, "parcela");
					addComponent(tfParcela);						
					
				}
		});
		
		hl4.addComponent(
				new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);						
						
						TextField tfFormPgto = new TextField("Conta Movimento");
						tfFormPgto.setStyleName("caption-align-editar-titulo");
						tfFormPgto.setImmediate(true);
//						tfFormPgto.setWidth("40px");		
						tfFormPgto.setNullRepresentation("");
						
						if(item.getItemProperty("forma_pgto").getValue()!=null){
							tfFormPgto.setValue(FormaPgtoSelecionado);
						}
						tfFormPgto.setReadOnly(true);
						addComponent(tfFormPgto);						
						
					}
				});

		if(item.getItemProperty("id").getValue()!=null){
			vlRoot.addComponent(hl4);
			
			vlRoot.addComponent(
					new FormLayout(){					
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);						
							
							TextField tfStatus = new TextField("Status");
							tfStatus.setStyleName("caption-align-editar-titulo");
							tfStatus.setImmediate(true);
							tfStatus.setWidth("80px");	
							
							fieldGroup.bind(tfStatus, "status");
							tfStatus.setReadOnly(true);
							addComponent(tfStatus);						
							
						}
					});
			
		}else{
			
			vlRoot.addComponent(
					new FormLayout(){					
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);						
							
							tfQAtd = new TextField("Quantidade");
							tfQAtd.setStyleName("caption-align-editar-titulo");
							tfQAtd.setImmediate(true);
							tfQAtd.setWidth("40px");		
							tfQAtd.setRequired(true);

							tfQAtd.setValue("1");
							
							addComponent(tfQAtd);						
							
						}
					});
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);						
												
						cbIntervalo = new ComboBox("Intervalo");
						cbIntervalo.setStyleName("caption-align-editar-titulo");
						cbIntervalo.setImmediate(true);
						cbIntervalo.setTextInputAllowed(false);
						cbIntervalo.setRequired(true);
						cbIntervalo.addItem("SEMANAL");
						cbIntervalo.addItem("QUINZENAL");
						cbIntervalo.addItem("MENSAL");
						cbIntervalo.addItem("ANUAL");
						
						if(item.getItemProperty("intervalo").getValue()==null){
							cbIntervalo.select("MENSAL");
						}
						cbIntervalo.setNullSelectionAllowed(false);
						
						addComponent(cbIntervalo);						
					}
			});
		}
		
		
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(item.getItemProperty("id").getValue()==null){
					
				if(tfCodFornecedor.isValid() && tfCodConta.isValid() && tfValorTitulo.isValid() &&
				   tfDesc.isValid() && tfQAtd.isValid() && cbIntervalo.isValid() && cbTermo.isValid() && 
				   cbTipo.isValid() && dfVencimento.isValid()){
				

					ConfirmarLancamento confLanc = new ConfirmarLancamento(true, true, Integer.parseInt(tfQAtd.getValue().toString()),
						dfVencimento.getValue(), tfValorTitulo.getValue(),cbIntervalo.getValue().toString());
				
				confLanc.addListerner(new ConfirmarLancamento.ConfirmListerner() {
					
					@Override
					public void onClose(ConfirmEvent event) {						
						if(event.isConfirm()){
							fireEvent(new NovoTituloEvent(event.getComponent().getParent(), cbTermo.getValue().toString(), 
							cbTipo.getValue().toString(),contaSelecionada,FornecedorSelecionado, tfDesc.getValue(),
							dfVencimento.getValue(),tfValorTitulo.getValue(),tfQAtd.getValue(),cbIntervalo.getValue().toString(),true,tfCod.getValue(),tfCod.getValue()));
							
							Notification.show("Título(s) Gerado(s) Com Sucesso!");
							close();
						}						
					}
				});
				getUI().addWindow(confLanc);
				
				
				
				}else{						
					
					if(!tfCodFornecedor.isValid()){
						tfCodFornecedor.addStyleName("invalid-txt");
					}else{
						tfCodFornecedor.removeStyleName("invalid-txt");
					}
					if(!tfCodConta.isValid()){
						tfCodConta.addStyleName("invalid-txt");
					}else{
						tfCodConta.removeStyleName("invalid-txt");
					}
					if(!tfValorTitulo.isValid()){
						tfValorTitulo.addStyleName("invalid-txt");
					}else{
						tfValorTitulo.removeStyleName("invalid-txt");
					}
					if(!tfValorTitulo.isValid()){
						tfValorTitulo.addStyleName("invalid-txt");
					}else{
						tfValorTitulo.removeStyleName("invalid-txt");
					}
					if(!tfDesc.isValid()){
						tfDesc.addStyleName("invalid-txt");
					}else{
						tfDesc.removeStyleName("invalid-txt");
					}
					if(!tfQAtd.isValid()){
						tfQAtd.addStyleName("invalid-txt");
					}else{
						tfQAtd.removeStyleName("invalid-txt");
					}
					if(!cbIntervalo.isValid()){
						cbIntervalo.addStyleName("invalid-txt");
					}else{
						cbIntervalo.removeStyleName("invalid-txt");
					}
					if(!cbTipo.isValid()){
						cbTipo.addStyleName("invalid-txt");
					}else{
						cbTipo.removeStyleName("invalid-txt");
					}
					if(!dfVencimento.isValid()){
						dfVencimento.addStyleName("invalid-txt");
					}else{
						dfVencimento.removeStyleName("invalid-txt");
					}
					if(!cbTermo.isValid()){
						cbTermo.addStyleName("invalid-txt");
					}else{
						cbTermo.removeStyleName("invalid-txt");
					}		
					
					Notification.show("Não é Possivel Gerar Novo(s) Tituilo(s), Verifique se todos os Campos estão Preenchidos!");
					}		
				
				}else{
					
					if(tfDesc.isValid() && tfValorTitulo.isValid() && tfParcela.isValid()){
						try {
							fieldGroup.commit();
							item.getItemProperty("valor_titulo").setValue(Real.formatStringToDB(tfValorTitulo.getValue().toString()));
							item.getItemProperty("fornecedor").setValue(FornecedorSelecionado);
							item.getItemProperty("conta").setValue(contaSelecionada);
							fireEvent(new NovoTituloEvent(event.getComponent().getParent(),true,item));
							
							Notification.show("Título Alterardos Com Sucesso!");
							close();
						} catch (CommitException e) {			
							e.printStackTrace();
							Notification.show("Erro!");
						}
					}else{
						
						if(!tfDesc.isValid()){
							tfDesc.addStyleName("invalid-txt");
						}else{
							tfDesc.removeStyleName("invalid-txt");
						}	
						if(!tfValorTitulo.isValid()){
							tfValorTitulo.addStyleName("invalid-txt");
						}else{
							tfValorTitulo.removeStyleName("invalid-txt");
						}	
						if(!tfParcela.isValid()){
							tfParcela.addStyleName("invalid-txt");
						}else{
							tfParcela.removeStyleName("invalid-txt");
						}	
						
						Notification.show("Não é Possivel Gerar Novo(s) Tituilo(s), Verifique se todos os Campos estão Preenchidos!");
					}
				}
			}
		});	
		btSalvar.addStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("OK", ShortcutAction.KeyCode.ENTER, null) {
			
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
				close();				
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
	
	public void addListerner(NovoTituloListerner target){
		try {
			Method method = NovoTituloListerner.class.getDeclaredMethod("onClose", NovoTituloEvent.class);
			addListener(NovoTituloEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(NovoTituloEvent.class, target);
	}
	public static class NovoTituloEvent extends Event{
		
		private String id;
		private String n_cod;
		private String termo;
		private String tipo;
		private Contas conta;
		private Fornecedor fornecedor;
		private String descricao;
		private Date dataVenciemento;
		private String valor;
		private String qtd;
		private String intervalo;	
		private boolean confirm;
		
		private Item item;	

		public NovoTituloEvent(Component souce, boolean confirm, Item item) {
			super(souce);
			this.confirm = confirm;
			this.item = item;
		}

		public NovoTituloEvent(Component souce, String termo, String tipo,
				Contas conta, Fornecedor fornecedor, String descricao,
				Date dataVenciemento, String valor, String qtd,
				String intervalo, boolean confirm,String n_cod,String id) {
			super(souce);
			
			this.termo = termo;
			this.tipo = tipo;
			this.conta = conta;
			this.fornecedor = fornecedor;
			this.descricao = descricao;
			this.dataVenciemento = dataVenciemento;
			this.valor = valor;
			this.qtd = qtd;
			this.intervalo = intervalo;
			this.confirm = confirm;
			this.n_cod = n_cod;
			this.id = id;
		}

		public String getTermo() {
			return termo;
		}

		public void setTermo(String termo) {
			this.termo = termo;
		}

		public String getTipo() {
			return tipo;
		}

		public void setTipo(String tipo) {
			this.tipo = tipo;
		}
		

		public Contas getConta() {
			return conta;
		}

		public Fornecedor getFornecedor() {
			return fornecedor;
		}

		public void setFornecedor(Fornecedor fornecedor) {
			this.fornecedor = fornecedor;
		}

		public String getDescricao() {
			return descricao;
		}

		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}

		public Date getDataVenciemento() {
			return dataVenciemento;
		}

		public void setDataVenciemento(Date dataVenciemento) {
			this.dataVenciemento = dataVenciemento;
		}

		public String getValor() {
			return valor;
		}

		public void setValor(String valor) {
			this.valor = valor;
		}

		public String getQtd() {
			return qtd;
		}

		public void setQtd(String qtd) {
			this.qtd = qtd;
		}

		public String getIntervalo() {
			return intervalo;
		}

		public void setIntervalo(String intervalo) {
			this.intervalo = intervalo;
		}

		public boolean isConfirm() {
			return confirm;
		}

		public void setConfirm(boolean confirm) {
			this.confirm = confirm;
		}

		public String getN_cod() {
			return n_cod;
		}

		public void setN_cod(String n_cod) {
			this.n_cod = n_cod;
		}

		public void setConta(Contas conta) {
			this.conta = conta;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
	
	}
	public interface NovoTituloListerner extends Serializable{
		public void onClose(NovoTituloEvent event);
	}

}
