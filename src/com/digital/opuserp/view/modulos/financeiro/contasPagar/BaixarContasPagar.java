package com.digital.opuserp.view.modulos.financeiro.contasPagar;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasDAO;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.domain.Contas;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.comp.RealTextField.RealTextField;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.modulos.financeiro.contasPagar.ConfirmarLancamento.ConfirmEvent;
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
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
import com.vaadin.ui.themes.BaseTheme;

public class BaixarContasPagar extends Window implements GenericEditor {

	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	
	
	Item item;
	DateField dfDataPagamento;
	RealTextField tfValorTitulo; //
	RealTextField tfValordesconto;
	RealTextField tfValorAcrescimo;
	RealTextField tfValorPagamento;
	TextField tfNdoc; //
	DateField dfVencimento; //
	DateField dfPagamento; //
	TextField tfDescricaoConta;
	TextField tfCodFpgto;
	TextField tfDescricaoFpgto;
	TextField tfCodConta;
	ComboBox cbTermo;
	ComboBox cbTipo;
	TextField tfDesc;
	TextField tfParcela;
	TextField tfCod;
		
	String vlpago;
	ComboBox cbIntervalo;
	
	TextField tfQAtd;
	
	private TextField tfDescricaoFornecedor;
	private TextField tfCodFornecedor;	
	private Fornecedor FornecedorSelecionado;	
	private Contas FormaPgtoSelecionado;	
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
	
	
	public BaixarContasPagar(Item item, String title, boolean modal){
		this.item = item;		
		
		setWidth("870px");
		setHeight("535px");
		
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
			vlpago = Real.formatDbToString(String.valueOf(item.getItemProperty("valor_titulo").getValue()));
			
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
						tfCod.setWidth("80px");
						
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
					
					
					DateField dfdataCadastro = new DateField("Data Vencimento");
					dfdataCadastro.setStyleName("caption-align-editar-titulo-cp");
					dfdataCadastro.setImmediate(true);
					dfdataCadastro.setDateFormat("dd/MM/yyyy");
					
					if(item.getItemProperty("data_vencimento").getValue()!=null){
						fieldGroup.bind(dfdataCadastro, "data_vencimento");
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
					cbTermo.setNullSelectionAllowed(false);
					
					if(item.getItemProperty("termo").getValue()!=null){
						fieldGroup.bind(cbTermo, "termo");							
						cbTermo.setRequired(false);
						cbTermo.setReadOnly(true);
					}else{
						cbTermo.setRequired(true);
						cbTermo.setReadOnly(false);
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
						
						if(item.getItemProperty("termo").getValue()!=null){
							fieldGroup.bind(cbTipo, "tipo");							
							cbTipo.setRequired(false);
							cbTipo.setReadOnly(true);
						}else{
							cbTipo.setRequired(true);
							cbTipo.setReadOnly(false);
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
				tfCodConta.setId("tfCodFornecedor3");
				tfCodConta.focus();
				
				JavaScript.getCurrent().execute("$('#tfCodFornecedor3').mask('00000000000000000000000000')");
				tfCodConta.setImmediate(true);
	
				
				tfCodConta.setRequired(true);		
				tfDescricaoConta = new TextField();
				tfDescricaoConta.setTabIndex(2000);
				tfDescricaoConta.setReadOnly(true);
				tfDescricaoConta.setWidth("456px");
						
				final Button btSearchConta = new Button();
				btSearchConta.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchConta.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchConta.setTabIndex(300000);
				
				if(item.getItemProperty("conta").getValue() != null){
					btSearchConta.setEnabled(false);
				}
				btSearchConta.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						ContasUtil cUtil = new ContasUtil(true, true, null);
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
						setStyleName("form-cutom-new_hide_error_cell");										
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
				
				
				if(tfCodConta != null && contaSelecionada != null){
					tfCodConta.setValue(contaSelecionada.getcod_cta_ref().toString());
					tfCodConta.setReadOnly(true);
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
				tfCodFornecedor.focus();
				
				JavaScript.getCurrent().execute("$('#tfCodFornecedor2').mask('000000000000000000000000000')");
				tfCodFornecedor.setImmediate(true);
						
				
				
				tfCodFornecedor.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						FornecedorSelecionado = new Fornecedor();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							FornecedorSelecionado = FornecedorDAO.findbyCnpj(event.getText(), OpusERP4UI.getEmpresa().getId());		
							
							if(FornecedorSelecionado != null){
								tfDescricaoFornecedor.setReadOnly(false);
								tfDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
								tfDescricaoFornecedor.setReadOnly(true);
								
							}else {
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
				
				if(item.getItemProperty("fornecedor").getValue() != null){
					btSearchFornecedor.setEnabled(false);
				}
				btSearchFornecedor.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						FornecedorUtil cUtil = new FornecedorUtil(true, true, true);
						cUtil.addListerner(new FornecedorUtil.FornecedorListerner() {
							
							@Override
							public void onSelected(
									FornecedorUtil.FornecedorEvent event) {
									if(event.getFornecedor() != null){
										tfCodFornecedor.setValue(event.getFornecedor().getCnpj().toString());
										tfDescricaoFornecedor.setReadOnly(false);
										tfDescricaoFornecedor.setValue(event.getFornecedor().getRazao_social());
										tfDescricaoFornecedor.setReadOnly(true);
										FornecedorSelecionado = event.getFornecedor();
										
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
				
				
				if(tfCodFornecedor != null && FornecedorSelecionado != null){
					tfCodFornecedor.setValue(FornecedorSelecionado.getCnpj().toString());
					tfCodFornecedor.setReadOnly(true);
				}
				
				if(tfDescricaoFornecedor != null && FornecedorSelecionado != null){
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
						tfDesc.setWidth("607px");
						
						fieldGroup.bind(tfDesc, "descricao");
						tfDesc.setReadOnly(true);						
						
						addComponent(tfDesc);		
					}
			});

				

		
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);						
						
						tfValorTitulo = new RealTextField("Valor(R$)");
						tfValorTitulo.setStyleName("caption-align-editar-titulo");
						tfValorTitulo.setImmediate(true);
						tfValorTitulo.focus();
						tfValorTitulo.setWidth("80px");
						tfValorTitulo.setRequired(true);
						tfValorTitulo.setNullRepresentation("");
						tfValorTitulo.setId("txtValorTitulovl");
						tfValorTitulo.addStyleName("align-currency");						
						tfValorTitulo.setValue(vlpago);
						
						tfValorTitulo.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								if(event.getText()!=null && !event.getText().equals("") && tfValordesconto.getValue()!=null && 
										!tfValordesconto.getValue().equals("") && tfValorAcrescimo.getValue()!=null && 
										!tfValorAcrescimo.getValue().equals("")){
									
									vlpago = Real.formatDbToString(String.valueOf(Real.formatStringToDBDouble(event.getText())-
											  Real.formatStringToDBDouble(tfValordesconto.getValue())+
											  Real.formatStringToDBDouble(tfValorAcrescimo.getValue())));
									
								}else if(event.getText()!=null && !event.getText().equals("") && tfValordesconto.getValue()!=null && 
										!tfValordesconto.getValue().equals("")){
									
									vlpago = Real.formatDbToString(String.valueOf(Real.formatStringToDBDouble(event.getText())-
											  Real.formatStringToDBDouble(tfValordesconto.getValue())));
									
								}else if(event.getText()!=null && !event.getText().equals("") && tfValorAcrescimo.getValue()!=null && 
										!tfValorAcrescimo.getValue().equals("")){
									
									vlpago = Real.formatDbToString(String.valueOf(Real.formatStringToDBDouble(event.getText())+
											  Real.formatStringToDBDouble(tfValorAcrescimo.getValue())));
									
								}else if(event.getText()!=null && !event.getText().equals("")){
									
									vlpago = Real.formatDbToString(event.getText());											 
								}
						

								tfValorPagamento.setReadOnly(false);							
								tfValorPagamento.setValue(vlpago);
								tfValorPagamento.setReadOnly(true);							
							}		
						});

						addComponent(tfValorTitulo);			
						JavaScript.getCurrent().execute("$('#txtValorTitulovl').maskMoney({decimal:',',thousands:'.'})");
						
						
					}
			});
						
			vlRoot.addComponent(
				new FormLayout(){
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);						
					
					tfValordesconto = new RealTextField("Desconto(-)");
					tfValordesconto.setStyleName("caption-align-editar-titulo");
					tfValordesconto.setImmediate(true);
					tfValordesconto.setWidth("80px");
					tfValordesconto.setNullRepresentation("");
					tfValordesconto.setId("txtValorTitulodes");
					tfValordesconto.addStyleName("align-currency");
					
					
					tfValordesconto.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
						
							if(event.getText()!=null && !event.getText().equals("") && tfValorTitulo.getValue()!=null && 
									!tfValorTitulo.getValue().equals("") && tfValorAcrescimo.getValue()!=null && 
									!tfValorAcrescimo.getValue().equals("")){
								
								vlpago = Real.formatDbToString(String.valueOf(Real.formatStringToDBDouble(tfValorAcrescimo.getValue())-
										 Real.formatStringToDBDouble(event.getText())+ Real.formatStringToDBDouble(tfValorTitulo.getValue())));
								
							}else if(event.getText()!=null && !event.getText().equals("") && tfValorTitulo.getValue()!=null && 
									!tfValorTitulo.getValue().equals("")){
								
								vlpago = Real.formatDbToString(String.valueOf(Real.formatStringToDBDouble(tfValorTitulo.getValue())-
										 Real.formatStringToDBDouble(event.getText())));
														
							}else if(tfValorTitulo.getValue()!=null && tfValorTitulo.getValue().equals("")){
								
								vlpago = Real.formatDbToString(tfValorTitulo.getValue());											 
							}
							
							tfValorPagamento.setReadOnly(false);							
							tfValorPagamento.setValue(vlpago);
							tfValorPagamento.setReadOnly(true);	
							
						}		
					});

					addComponent(tfValordesconto);			
					JavaScript.getCurrent().execute("$('#txtValorTitulodes').maskMoney({decimal:',',thousands:'.'})");
					
				}
			});	
			
			vlRoot.addComponent(
					new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);						
							
							tfValorAcrescimo = new RealTextField("Acréscimo(+)");
							tfValorAcrescimo.setStyleName("caption-align-editar-titulo");
							tfValorAcrescimo.setImmediate(true);
							tfValorAcrescimo.setWidth("80px");
							tfValorAcrescimo.setNullRepresentation("");
							tfValorAcrescimo.setId("txtValorTituloacre");
							tfValorAcrescimo.addStyleName("align-currency");
							
							tfValorAcrescimo.addTextChangeListener(new FieldEvents.TextChangeListener() {
								
								@Override
								public void textChange(TextChangeEvent event) {
									
									if(event.getText()!=null && !event.getText().equals("") && tfValordesconto.getValue()!=null && 
											!tfValordesconto.getValue().equals("") && tfValorTitulo.getValue()!=null && 
											!tfValorTitulo.getValue().equals("")){
										
										vlpago = Real.formatDbToString(String.valueOf(Real.formatStringToDBDouble(tfValorTitulo.getValue())-
												 Real.formatStringToDBDouble(tfValordesconto.getValue())+Real.formatStringToDBDouble(event.getText())));
										
									}else if(event.getText()!=null && !event.getText().equals("") && tfValorTitulo.getValue()!=null && 
											!tfValorTitulo.getValue().equals("")){
										
										vlpago = Real.formatDbToString(String.valueOf(Real.formatStringToDBDouble(event.getText())+
												 Real.formatStringToDBDouble(tfValorTitulo.getValue())));
																	
									}else if(tfValorTitulo.getValue()!=null && tfValorTitulo.getValue().equals("")){
										
										vlpago = Real.formatDbToString(tfValorTitulo.getValue());											 
									}
								
									tfValorPagamento.setReadOnly(false);							
									tfValorPagamento.setValue(vlpago);
									tfValorPagamento.setReadOnly(true);	
									
								}		
							});
							
							addComponent(tfValorAcrescimo);			
							JavaScript.getCurrent().execute("$('#txtValorTituloacre').maskMoney({decimal:',',thousands:'.'})");
							
						}
					});
			
			vlRoot.addComponent(
					new FormLayout(){
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);						
							
							tfValorPagamento = new RealTextField("Valor Pago(R$)");
							tfValorPagamento.setStyleName("caption-align-editar-titulo");
							tfValorPagamento.setImmediate(true);
							tfValorPagamento.setWidth("80px");
							tfValorPagamento.setRequired(true);
							tfValorPagamento.setNullRepresentation("");
							tfValorPagamento.setId("txtValorTitulopg");
							tfValorPagamento.addStyleName("align-currency");

							tfValorPagamento.setReadOnly(false);												
							tfValorPagamento.setValue(vlpago);
							tfValorPagamento.setReadOnly(true);							
									
							addComponent(tfValorPagamento);			
							JavaScript.getCurrent().execute("$('#txtValorTitulopg').maskMoney({decimal:',',thousands:'.'})");
							
						}
					});	
			
			
			vlRoot.addComponent(new HorizontalLayout(){
				{
					tfCodFpgto = new TextField("Conta Movimento");				
					tfCodFpgto.setWidth("120px");				
					tfCodFpgto.setNullRepresentation("");
					tfCodFpgto.setStyleName("caption-align-editar-titulo");
//					tfCodFpgto.focus();
					tfCodFpgto.setImmediate(true);

					tfCodFpgto.setValue("1.01.01.02.00");			
					FormaPgtoSelecionado = ContasDAO.findCod_Ref(("1.01.01.02.00"));	
					

					tfCodFpgto.addListener(new TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
							FormaPgtoSelecionado = new Contas();
							
							if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
								
															
								FormaPgtoSelecionado = ContasDAO.findCod_Ref(event.getText());		
								
								if(FormaPgtoSelecionado != null){
									tfDescricaoFpgto.setReadOnly(false);
									tfDescricaoFpgto.setValue(FormaPgtoSelecionado.getDescricao());
									tfDescricaoFpgto.setReadOnly(true);
									
								}else {
									tfDescricaoFpgto.setReadOnly(false);
									tfDescricaoFpgto.setValue("");
									tfDescricaoFpgto.setReadOnly(true);
																
								}
							}else{
								tfDescricaoFpgto.setReadOnly(false);
								tfDescricaoFpgto.setValue("");
								tfDescricaoFpgto.setReadOnly(true);							
							}
						}
					});

					tfCodFpgto.setRequired(true);		
					tfDescricaoFpgto = new TextField();
					tfDescricaoFpgto.setTabIndex(2000);
					tfDescricaoFpgto.setReadOnly(true);
					tfDescricaoFpgto.setWidth("456px");
							
					final Button btSearchConta = new Button();
					btSearchConta.setStyleName(BaseTheme.BUTTON_LINK);
					btSearchConta.setIcon(new ThemeResource("icons/search-16.png"));
					btSearchConta.setTabIndex(300000);
				
					btSearchConta.addClickListener(new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							
							ContasUtil cUtil = new ContasUtil(true, true, "sim");
							cUtil.addListerner(new ContasUtil.ContasListerner() {
								
								@Override
								public void onSelected(ContasEvent event) {
									if(event.getContas() != null){
										tfCodFpgto.setValue(event.getContas().getcod_cta_ref().toString());
										tfDescricaoFpgto.setReadOnly(false);
										tfDescricaoFpgto.setValue(event.getContas().getDescricao());
										tfDescricaoFpgto.setReadOnly(true);
										FormaPgtoSelecionado = event.getContas();
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
													
							addComponent(tfCodFpgto);							
						}
					};
					addComponent(frmCodigoContas);
			
					FormLayout frmButtonSearchConta =new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new_hide_error_cell");										
							addComponent(btSearchConta);							
						}
					}; 
								
					FormLayout frmDescContas = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);						
							setStyleName("form-cutom-new");		
							addStyleName("form-cutom_hide_require");
							
							addComponent(tfDescricaoFpgto);							
						}
					}; 
					addComponent(frmButtonSearchConta);
					addComponent(frmDescContas);
					setExpandRatio(frmDescContas, 1);		
					
					
					if(FormaPgtoSelecionado!=null){
						tfCodFpgto.setValue(FormaPgtoSelecionado.getcod_cta_ref().toString());
						tfDescricaoFpgto.setReadOnly(false);
						tfDescricaoFpgto.setValue(FormaPgtoSelecionado.getDescricao());		
						tfDescricaoFpgto.setReadOnly(true);
					}
				

				}	
			});
			
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);						
						
						dfDataPagamento = new DateField("Data Ptgo.");
						dfDataPagamento.setStyleName("caption-align-editar-titulo");
						dfDataPagamento.setImmediate(true);						
						dfDataPagamento.setRequired(true);											
						dfDataPagamento.setValue(new Date());
					
						addComponent(dfDataPagamento);						
					}
			});
			
			
		}

		
		

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("BAIXAR", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

					if(tfValorTitulo.isValid() && tfCodFpgto.isValid() && dfDataPagamento.isValid()){
						try {
							item.getItemProperty("valor_titulo").setValue(Real.formatStringToDB(tfValorTitulo.getValue()));
							item.getItemProperty("valor_pagamento").setValue(Real.formatStringToDB(vlpago));
							item.getItemProperty("forma_pgto").setValue(FormaPgtoSelecionado.getcod_cta_ref());
							item.getItemProperty("status").setValue("PAGO");
							item.getItemProperty("data_pago").setValue(dfDataPagamento.getValue());
							
							fieldGroup.commit();
							fireEvent(new BaixarTituloEvent(event.getComponent().getParent(),true,item));
							
							Notification.show("Título(s) Baixados Com Sucesso!");
							close();
						} catch (CommitException e) {			
							e.printStackTrace();
							Notification.show("Erro!");
						}
					}else{
										
						if(!tfValorTitulo.isValid()){
							tfValorTitulo.addStyleName("invalid-txt");
						}else{
							tfValorTitulo.removeStyleName("invalid-txt");
						}	
						if(!tfCodFpgto.isValid()){
							tfCodFpgto.addStyleName("invalid-txt");
						}else{
							tfCodFpgto.removeStyleName("invalid-txt");
						}	
											
						Notification.show("Não é Possivel Baixar Tituilo(s), Verifique se todos os Campos estão Preenchidos!");
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
	
	public void addListerner(BaixarTituloListerner target){
		try {
			Method method = BaixarTituloListerner.class.getDeclaredMethod("onClose", BaixarTituloEvent.class);
			addListener(BaixarTituloEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(BaixarTituloEvent.class, target);
	}
	public static class BaixarTituloEvent extends Event{
		
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

		public BaixarTituloEvent(Component souce, boolean confirm, Item item) {
			super(souce);
			this.confirm = confirm;
			this.item = item;
		}

		public BaixarTituloEvent(Component souce, String termo, String tipo,
				Contas conta, Fornecedor fornecedor, String descricao,
				Date dataVenciemento, String valor, String qtd,
				String intervalo, boolean confirm,String n_cod) {
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
	
	}
	public interface BaixarTituloListerner extends Serializable{
		public void onClose(BaixarTituloEvent event);
	}

}

