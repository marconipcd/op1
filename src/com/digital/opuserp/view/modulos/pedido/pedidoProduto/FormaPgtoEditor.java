package com.digital.opuserp.view.modulos.pedido.pedidoProduto;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.CreditoClienteDAO;
import com.digital.opuserp.dao.FormaPgtoDAO;
import com.digital.opuserp.dao.HaverDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class FormaPgtoEditor extends Window{

	private EcfPreVendaCabecalho pedido;
		
	//private VerticalLayout vl2;
	private VerticalLayout vl1;
	private VerticalLayout vlFormasPgto;	
	//private VerticalLayout vlFormasPgtoTotal;
	private VerticalLayout vlFormasPgtoTotalRestante;
	private VerticalLayout vlFormasPgtoTotalGeral;
	private VerticalLayout vlRoot;
	private TextField txtSubTotalPedido;
	
	private Button btSalvar;
	private Button btCancelar;
	
	private Label lbAcrescimo;
	private Label lbDesconto;
	private Label lbSubTotal;
	private Label lbTotal;
	
	private TextField txtAcrescPorc2;	
	private TextField txtValorFormaPgto;
	private TextField txtValorFormaPgtoHaver;
	private TextField txtValorDisponivel;
	private TextField txtValorRestante; 
	private Button btAdicionarFormPgto;
	private Button btAdicionarFormPgtoHaver;
	private TextField txtParcelamento;
	private DateField dfPrimeiroVenc;
	private TextField txtDesc;
	private TextField txtValorPrimeiraParc;
	private JPAContainer<Haver> containerHaver;
	
	
	
	//private HashMap<Integer, TotaisHash> totais_forma_pgto_haver = new HashMap<>();
	private HashMap<Integer, TotaisHash> totais_forma_pgto = new HashMap<>();
	private HashMap<Integer, Integer> parcelas_forma_pgto= new HashMap<>();
	
	private FormasPgto formaPgtoSelecionada;
	private HaverCab haverSelecionado;
	private double haver_valor;
	double subTotalPedido;	
	double total;
	double total_pagando;
	Integer qtd_parc;	
	private ComboBox cbFormaPgto;
	
	boolean allowParc;
	
	public FormaPgtoEditor(EcfPreVendaCabecalho pedido, String title, boolean modal){
		this.pedido = pedido;
		total = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(pedido.getSubTotal()));
		subTotalPedido  = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(pedido.getSubTotal()));
		
		setWidth("471px");
		
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
	double vlr_forma_default = 0;
	public void buildLayout(){
		
		vlRoot.addComponent(new FormLayout(){
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField txtNumero = new TextField("Nº");
					txtNumero.addStyleName("caption-align-form-pgto");
					txtNumero.setValue(pedido.getId().toString());
					
					addComponent(txtNumero);
					txtNumero.setReadOnly(true);
				}
		});
		vlRoot.addComponent(new FormLayout(){
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField txtCliente = new TextField("Cliente");
					txtCliente.addStyleName("caption-align-form-pgto");
					txtCliente.setWidth("220px");
					txtCliente.setValue(pedido.getCliente() != null ? pedido.getCliente().getNome_razao() : "NENHUM CLIENTE VINCULADO");
					
					addComponent(txtCliente);
					txtCliente.setReadOnly(true);
				}
		});
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
				
				TextField txtTotalPedido = new TextField("Total Pedido (R$)");
				txtTotalPedido.setWidth("80px");
				txtTotalPedido.addStyleName("caption-align-form-pgto");				
				txtTotalPedido.setValue(Real.formatDbToString(String.valueOf(subTotalPedido)));
				
				addComponent(txtTotalPedido);
				txtTotalPedido.setReadOnly(true);
			}
	   });
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
							
							txtAcrescPorc2 = new TextField("Acresc (R$)");
							txtAcrescPorc2.addStyleName("caption-align-form-pgto");
							txtAcrescPorc2.setWidth("80px");
							txtAcrescPorc2.setId("txtAcresc2");
							txtAcrescPorc2.addStyleName("align-currency");
							txtAcrescPorc2.setImmediate(true);
							//txtAcrescPorc2.focus();
							
							txtAcrescPorc2.addTextChangeListener(new FieldEvents.TextChangeListener() {
								
								@Override
								public void textChange(TextChangeEvent event) {
									 
									if(event.getText() != null && !event.getText().equals("0,00") && !event.getText().equals("")){
										
										for(Entry<Integer,TotaisHash> entry : totais_forma_pgto.entrySet()) {
											formaPgtoSelecionada = FormaPgtoDAO.find(entry.getKey());
											break;
										}
									  									  
									  subTotalPedido = pedido.getSubTotal()+Real.formatStringToDBDouble(event.getText());
									  txtSubTotalPedido.setReadOnly(false);
									  txtSubTotalPedido.setValue(Real.formatDbToString(String.valueOf(subTotalPedido)));
									  txtSubTotalPedido.setReadOnly(true);
									  
									  vlFormasPgtoTotalGeral.removeAllComponents();
								      vlFormasPgtoTotalGeral.addComponent(new Label("<h2 style='padding-top: 14px;text-align: right;margin: 0px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+"</h2>", ContentMode.HTML));
									  
									  vl1.removeAllComponents();
									  vl1.addComponent(new Label("<h2 style='text-align: right;margin: 35px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL A PAGAR (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+ "</h2>", ContentMode.HTML));
									  
									  if(total_pagando >= subTotalPedido){
										  btAdicionarFormPgto.setEnabled(false);										  
										  cbFormaPgto.setEnabled(false);
										  txtValorFormaPgto.setEnabled(false);
										  
										 // vlFormasPgtoTotal.removeAllComponents();				
										 // vlFormasPgtoTotal.addComponent(	new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));
										  
										  vlFormasPgtoTotalRestante.removeAllComponents();										 
										  vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) 0,00</h2>", ContentMode.HTML));
									  }else{
										  btAdicionarFormPgto.setEnabled(true);										  
										  cbFormaPgto.setEnabled(true);
										  txtValorFormaPgto.setEnabled(true);

										 // vlFormasPgtoTotal.removeAllComponents();
										 // vlFormasPgtoTotal.addComponent(	new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));
										  
										  vlFormasPgtoTotalRestante.removeAllComponents();
										  double rest = subTotalPedido-total_pagando;
										  vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) "+Real.formatDbToString(String.valueOf(rest))+"</h2>", ContentMode.HTML));
									  }
									  
										
									  
									 
									  
									 
									}else{
										
										

										 subTotalPedido = pedido.getSubTotal();
										 
										 txtSubTotalPedido.setReadOnly(false);
										  txtSubTotalPedido.setValue(Real.formatDbToString(String.valueOf(subTotalPedido)));
										  txtSubTotalPedido.setReadOnly(true);
										 
										 vlFormasPgtoTotalGeral.removeAllComponents();
									     vlFormasPgtoTotalGeral.addComponent(new Label("<h2 style='padding-top: 14px;text-align: right;margin: 0px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+"</h2>", ContentMode.HTML));
									     
									     vlFormasPgtoTotalRestante.removeAllComponents();
										  double rest = subTotalPedido-total_pagando;
										  vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+"</h2>", ContentMode.HTML));
									      
										 vl1.removeAllComponents();
										 vl1.addComponent(new Label("<h2 style='text-align: right;margin: 35px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL A PAGAR (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+ "</h2>", ContentMode.HTML));
										 
										
										
									
									}
								}
							});
							txtAcrescPorc2.addBlurListener(new FieldEvents.BlurListener() {
								
								@Override
								public void blur(BlurEvent event) {
									
									double valor_desconto = 0;
									double desconto = txtDesc != null && !txtDesc.isReadOnly() && txtDesc.getValue() != null && !txtDesc.getValue().equals("")   && !txtDesc.getValue().isEmpty()  ? Real.formatStringToDBDouble(txtDesc.getValue()) : 0;
									
									double percentual = desconto / 100.0; 
									valor_desconto = (percentual * pedido.getSubTotal());
																			
									double subt = subTotalPedido+valor_desconto; 
									double totap = total_pagando;
									 
									if(txtAcrescPorc2.getValue() == null || txtAcrescPorc2.getValue().equals("0,00") && total_pagando > Real.formatStringToDBDouble(new DecimalFormat("0.00").format(subt))){
										
										totais_forma_pgto.clear();
										total_pagando = 0;
										reloadFormasPgto();
										
										btAdicionarFormPgto.setEnabled(true);										  
										cbFormaPgto.setEnabled(true);
										txtValorFormaPgto.setEnabled(true);

										//vlFormasPgtoTotal.removeAllComponents();
										//vlFormasPgtoTotal.addComponent(	new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));
									}
								}
							});
							
							
							addComponent(txtAcrescPorc2);
							JavaScript.getCurrent().execute("$('#txtAcresc2').maskMoney({decimal:',',thousands:'.'})");

						}
				});
				
				
			}
		});
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				txtSubTotalPedido = new TextField("Sub Total Pedido (R$)");
				txtSubTotalPedido.addStyleName("caption-align-form-pgto");
				txtSubTotalPedido.setWidth("80px");				
				txtSubTotalPedido.setValue(Real.formatDbToString(String.valueOf(subTotalPedido)));
				
				addComponent(txtSubTotalPedido);
				txtSubTotalPedido.setReadOnly(true);
			}
	   });
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				txtSubTotalPedido = new TextField("Sub Total Pedido (R$)");
				txtSubTotalPedido.addStyleName("caption-align-form-pgto");
				txtSubTotalPedido.setWidth("80px");				
				txtSubTotalPedido.setValue(Real.formatDbToString(String.valueOf(subTotalPedido)));
				
				addComponent(txtSubTotalPedido);
				txtSubTotalPedido.setReadOnly(true);
			}
	   });
		
		
		final ShortcutListener sl1 = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btAdicionarFormPgto.click();
			}
		};
		
		final ShortcutListener sl2 = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btAdicionarFormPgtoHaver.click();
			}
		};
		
		 HaverCab haver = HaverDAO.buscarHaver(pedido.getCliente());
		 
		//Inicio tab Pagamentos		
		final TabSheet ts = new TabSheet();
		ts.addStyleName("top-margin-forma_pgto");
		ts.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				JavaScript.getCurrent().execute("$('#txtValorFormaPgtoHaver').maskMoney({decimal:',',thousands:'.'})");
				JavaScript.getCurrent().execute("$('#txtValorFormaPgto').maskMoney({decimal:',',thousands:'.'})");
				
				txtValorFormaPgto.setValue("");
				txtValorFormaPgto.setEnabled(false);
				
				if(txtValorFormaPgtoHaver != null){
					txtValorFormaPgtoHaver.setValue("");
				}
				
				if(event.getTabSheet() != null && event.getTabSheet().getSelectedTab() != null && event.getTabSheet().getSelectedTab().getCaption() != null && event.getTabSheet().getSelectedTab().getCaption().equals("haver")){
					btAdicionarFormPgto.removeShortcutListener(sl1);
					
					if(btAdicionarFormPgtoHaver != null){
						btAdicionarFormPgtoHaver.addShortcutListener(sl2);
					}
				}else{
					btAdicionarFormPgto.addShortcutListener(sl1);
					
					if(btAdicionarFormPgtoHaver != null){
						btAdicionarFormPgtoHaver.removeShortcutListener(sl2);
					}
				}
			}
		});
		 
		 VerticalLayout vlPrincipal = new VerticalLayout();		 
		 VerticalLayout vlHaver = new VerticalLayout();
		 
		 if(haver != null){
			 
			 containerHaver = JPAContainerFactory.make(Haver.class, ConnUtil.getEntity());
			 containerHaver.addContainerFilter(Filters.eq("cliente", pedido.getCliente()));
			 containerHaver.addContainerFilter(Filters.eq("status", "DISPONIVEL"));
			 		 		 
			 vlHaver.addComponent(new FormLayout(){
					{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);
							
							txtValorDisponivel = new TextField("Valor Disponível (R$)");
							txtValorDisponivel.setRequired(true); 
							txtValorDisponivel.setId("txtValorDisponivel");
							txtValorDisponivel.addStyleName("caption-align-form-pgto");
							txtValorDisponivel.setImmediate(true);
							txtValorDisponivel.setWidth("80px");
							txtValorDisponivel.addStyleName("align-currency");
							
							addComponent(txtValorDisponivel);
							JavaScript.getCurrent().execute("$('#txtValorDisponivel').maskMoney({decimal:',',thousands:'.'})");
														
						}
							
				});
			 vlHaver.addComponent(new FormLayout(){
					{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
							
							txtValorRestante = new TextField("Valor Restante (R$)");
							txtValorRestante.setRequired(true); 
							txtValorRestante.setId("txtValorRestante");
							txtValorRestante.addStyleName("caption-align-form-pgto");
							txtValorRestante.setImmediate(true);
							txtValorRestante.setWidth("80px");
							txtValorRestante.addStyleName("align-currency");
							txtValorRestante.setReadOnly(true);
							
							addComponent(txtValorRestante);
							JavaScript.getCurrent().execute("$('#txtValorRestante').maskMoney({decimal:',',thousands:'.'})");
						}
							
				});
			 vlHaver.addComponent(new FormLayout(){
				{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						txtValorFormaPgtoHaver = new TextField("Valor Haver (R$)");
						txtValorFormaPgtoHaver.setRequired(true); 
						txtValorFormaPgtoHaver.setId("txtValorFormaPgtoHaver");
						txtValorFormaPgtoHaver.addStyleName("caption-align-form-pgto");
						txtValorFormaPgtoHaver.setImmediate(true);
						txtValorFormaPgtoHaver.setWidth("80px");
						txtValorFormaPgtoHaver.addStyleName("align-currency");
																			
						txtValorFormaPgtoHaver.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								if(event.getText() != null && !event.getText().isEmpty() && !event.getText().equals("")){
									
									double valor = Real.formatStringToDBDouble(event.getText());
									double valor_disponivel = Real.formatStringToDBDouble(txtValorDisponivel.getValue());
									double valor_restante = valor_disponivel - valor;
									if(valor <= valor_disponivel){								
										txtValorRestante.setReadOnly(false);
										txtValorRestante.setValue(Real.formatDbToString(String.valueOf(valor_restante)));
										txtValorRestante.setReadOnly(true);
										
										txtValorFormaPgtoHaver.removeStyleName("invalid");
										haver_valor = Real.formatStringToDBDouble(event.getText());
									}else{
										txtValorFormaPgtoHaver.addStyleName("invalid");
									}
									
								}
							}
						});
							
						addComponent(txtValorFormaPgtoHaver);
						JavaScript.getCurrent().execute("$('#txtValorFormaPgtoHaver').maskMoney({decimal:',',thousands:'.'})");
					}
						
			});
			 vlHaver.addComponent(new FormLayout(){
				{
					setStyleName("form-cutom");
					addStyleName("button-margem-forma-pgto");
					setMargin(true);
					setSpacing(true);
					
					btAdicionarFormPgtoHaver = new Button("Adicionar", new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							addFormaPgtoHaver(event);							
						}
					});
					btAdicionarFormPgtoHaver.setWidth("100px");
					btAdicionarFormPgtoHaver.addStyleName(Reindeer.BUTTON_SMALL);
					
					//btAdicionarFormPgtoHaver.setEnabled(false); 
					btAdicionarFormPgtoHaver.addShortcutListener(sl2);
					
					addComponent(btAdicionarFormPgtoHaver);
				}
			});
				
			haverSelecionado = haver;
			txtValorDisponivel.setReadOnly(false); 
			txtValorDisponivel.setValue(Real.formatDbToString(String.valueOf(haver.getValor_total())));
			txtValorDisponivel.setReadOnly(true);
			
			txtValorRestante.setReadOnly(false);
			txtValorRestante.setValue(Real.formatDbToString(String.valueOf(haver.getValor_total())));
			txtValorRestante.setReadOnly(true);
		}
		 
		vlPrincipal.addComponent(new FormLayout(){
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					cbFormaPgto = new ComboBox("Forma de Pagamento", getFormasPgto());
					cbFormaPgto.addStyleName("caption-align-form-pgto");
					cbFormaPgto.setNullSelectionAllowed(false);
					cbFormaPgto.setItemCaptionPropertyId("nome");
					cbFormaPgto.setTextInputAllowed(false);
					cbFormaPgto.setRequired(true);
					cbFormaPgto.setImmediate(true);
					cbFormaPgto.focus();
					
					cbFormaPgto.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(cbFormaPgto.getValue() != null){
								
								txtValorFormaPgto.setEnabled(true); 
								txtValorPrimeiraParc.setReadOnly(true);
								txtValorPrimeiraParc.setRequired(false);
								
								
								EntityItem<FormasPgto> formaPgto = (EntityItem<FormasPgto>) cbFormaPgto.getItem(cbFormaPgto.getValue());								
								formaPgtoSelecionada = formaPgto.getEntity();
								
								if(formaPgtoSelecionada != null && formaPgtoSelecionada.isAprovacao_credito()){
									txtValorFormaPgto.focus();
									
									
									if(!ContasReceberDAO.existeAlgumBoletoAbertoeVencido(pedido.getCliente())){
									
										if(formaPgtoSelecionada.getN_parcelas() > 0){
											txtParcelamento.setReadOnly(false);
											txtParcelamento.setRequired(true);
											txtParcelamento.setValue("");
										}else{
											txtParcelamento.setReadOnly(true);
											txtParcelamento.setRequired(false);										
										}
										
										
										if(formaPgtoSelecionada.getTipo_titulo().equals("BOLETO")){
											dfPrimeiroVenc.setReadOnly(false);
											dfPrimeiroVenc.setRequired(true);
											dfPrimeiroVenc.setValue(new Date());										
										}else{
											dfPrimeiroVenc.setRequired(false);
											dfPrimeiroVenc.setReadOnly(true);											
										}
										
										
									}else{
										Notify.Show("Cliente Possui Boletos Vencidos!", Notify.TYPE_ERROR);
										formaPgtoSelecionada = null;
										cbFormaPgto.setContainerDataSource(getFormasPgto());
										txtParcelamento.setReadOnly(false);
										txtParcelamento.setValue("1");
										txtParcelamento.setReadOnly(true);
																												
									}
								}else{
									if(formaPgtoSelecionada.getN_parcelas() == null || formaPgtoSelecionada.getN_parcelas() == 0){
										txtParcelamento.setRequired(false);
										txtParcelamento.setReadOnly(false);
										txtParcelamento.setValue("1");
										txtParcelamento.setReadOnly(true);
									}else{
										txtParcelamento.setRequired(true);
										txtParcelamento.setReadOnly(false);
										txtParcelamento.setValue("");
									}

									dfPrimeiroVenc.setRequired(false);
									dfPrimeiroVenc.setReadOnly(true);									
								}
								
								//txtAcrescPorc2.setReadOnly(false);
								txtDesc.setReadOnly(false);
								
								JavaScript.getCurrent().execute("$('#txtDesc').maskMoney({decimal:',',thousands:'.'})");
								JavaScript.getCurrent().execute("$('#txtAcresc2').maskMoney({decimal:',',thousands:'.'})");
								
								txtValorFormaPgto.setValue(txtSubTotalPedido.getValue());
								txtValorFormaPgto.focus();
								btAdicionarFormPgto.setEnabled(true); 
																
							}else{
								
								formaPgtoSelecionada = null;
								
								if(txtParcelamento.isReadOnly()){
									txtParcelamento.setReadOnly(false);									
								}
								
								txtParcelamento.setValue("1");
								
								if(!txtParcelamento.isReadOnly()){
									txtParcelamento.setReadOnly(true);									
								}
								
								//txtAcrescPorc2.setReadOnly(true);
								//txtDesc.setReadOnly(true);
								
								txtValorFormaPgto.setValue(txtSubTotalPedido.getValue());
							}
						}
					});
					
					addComponent(cbFormaPgto);
				}
		});
		vlPrincipal.addComponent(new FormLayout(){
			{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					txtValorFormaPgto = new TextField("Valor a Pagar (R$)");
					txtValorFormaPgto.setRequired(true); 
					txtValorFormaPgto.setId("txtValorFormaPgto");
					txtValorFormaPgto.addStyleName("caption-align-form-pgto");
					txtValorFormaPgto.setImmediate(true);
					txtValorFormaPgto.setWidth("80px");
					txtValorFormaPgto.addStyleName("align-currency");
					
					txtValorFormaPgto.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
								
							if(!txtParcelamento.isReadOnly() && txtParcelamento.getValue() != null && !txtParcelamento.getValue().equals("0") && !txtParcelamento.getValue().equals("") &&  event.getText() != null && formaPgtoSelecionada.isAprovacao_credito())
							{
								double valorForma = event.getText() != null ? Real.formatStringToDBDouble(event.getText()) : 0;
								double valorDizima = (valorForma/Integer.parseInt(txtParcelamento.getValue()));
								BigDecimal valorPrimeiraParc = new BigDecimal(valorDizima);
								if(Integer.parseInt(txtParcelamento.getValue()) > 1){
									valorPrimeiraParc = valorPrimeiraParc.setScale(2, BigDecimal.ROUND_CEILING);
								}
								txtValorPrimeiraParc.setReadOnly(false);
								txtValorPrimeiraParc.setValue(Real.formatDbToString(String.valueOf(valorPrimeiraParc)));
								
								if(Integer.parseInt(txtParcelamento.getValue()) <2){
									txtValorPrimeiraParc.setReadOnly(true);
								}
							}	
							
							if(event.getText() != null && !event.getText().equals("0,00") && !event.getText().isEmpty() && !event.getText().equals("")){
								double valorInformado = Real.formatStringToDBDouble(event.getText());
								double novoTotal = subTotalPedido-total_pagando;
								if(valorInformado > Real.formatStringToDBDouble(new DecimalFormat("0.00").format(novoTotal))){
									event.getComponent().addStyleName("invalid-txt");
									btAdicionarFormPgto.setEnabled(false);
									Notify.Show("Valor máximo ultrapassado", Notify.TYPE_NOTICE);
								}else{
									event.getComponent().removeStyleName("invalid-txt");
									btAdicionarFormPgto.setEnabled(true);
								}
							}else{
								event.getComponent().addStyleName("invalid-txt");
								btAdicionarFormPgto.setEnabled(false); 
							}
						}
					});
						
					addComponent(txtValorFormaPgto);
					JavaScript.getCurrent().execute("$('#txtValorFormaPgto').maskMoney({decimal:',',thousands:'.'})");
				}
					
		});
		vlPrincipal.addComponent(new FormLayout(){
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					txtParcelamento = new TextField("Nº de Parcelas");
					txtParcelamento.setReadOnly(true);
					txtParcelamento.setId("txtParcelamento");
					txtParcelamento.addStyleName("caption-align-form-pgto");
					txtParcelamento.setImmediate(true);
					txtParcelamento.setWidth("40px");
					
					txtParcelamento.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							if(formaPgtoSelecionada != null && formaPgtoSelecionada.isAprovacao_credito() && event.getText() != null && !event.getText().equals("") &&  !event.getText().equals("0,00") && txtValorFormaPgto.getValue() != null){
								
								qtd_parc = Integer.parseInt(event.getText());
								
								if(qtd_parc > 1){
									//txtValorPrimeiraParc.setRequired(true);
									txtValorPrimeiraParc.setReadOnly(false);
									
									double valorForma = Real.formatStringToDBDouble(txtValorFormaPgto.getValue());
									double valorDizima = (valorForma/qtd_parc);
									BigDecimal valorPrimeiraParc = new BigDecimal(valorDizima); 
									
									String s1= String.valueOf(valorDizima).replaceAll("\\.", ",");
									String[] s2 = s1.split(",");
									
									if(s2.length > 0 && s2[1].length() > 2){
										valorPrimeiraParc = valorPrimeiraParc.setScale(2, BigDecimal.ROUND_CEILING);
									}						
									
									
									txtValorPrimeiraParc.setValue(Real.formatDbToString(String.valueOf(valorPrimeiraParc)));
									JavaScript.getCurrent().execute("$('#txtValorPrimeiraParc').maskMoney({decimal:',',thousands:'.'})");
								}else{
									//txtValorPrimeiraParc.setRequired(false);
									txtValorPrimeiraParc.setReadOnly(false);
									txtValorPrimeiraParc.setValue(txtValorFormaPgto.getValue() != null ? txtValorFormaPgto.getValue() : "0,00");
									
									if(qtd_parc < 2){
										txtValorPrimeiraParc.setReadOnly(true);
									}
								}
								
								if(qtd_parc > formaPgtoSelecionada.getN_parcelas()){
									allowParc = false;
									txtParcelamento.addStyleName("invalid-txt");
									
									Notify.Show("Parcelamento Máximo é de: "+formaPgtoSelecionada.getN_parcelas().toString(), Notify.TYPE_ERROR);
								}else{
									allowParc = true;
									txtParcelamento.removeStyleName("invalid-txt");
								}
							}else{
								txtParcelamento.removeStyleName("invalid-txt");
							}
						}
					});
					
					txtParcelamento.addBlurListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
						//	if(txtParcelamento.getValue() == null || txtParcelamento.getValue().equals("") || txtParcelamento.getValue().isEmpty() || txtParcelamento.getValue().equals("0")){
							//	txtParcelamento.setValue("1");
							//}
						}
					});
					
					addComponent(txtParcelamento);
					txtParcelamento.setReadOnly(false);
					txtParcelamento.setValue("1");
					txtParcelamento.setReadOnly(true);
					JavaScript.getCurrent().execute("$('#txtParcelamento').mask('0000000000')");
				}
		});
		vlPrincipal.addComponent(new FormLayout(){
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					dfPrimeiroVenc = new DateField("Primeiro Vencimento");
					dfPrimeiroVenc.addStyleName("caption-align-form-pgto");	
					dfPrimeiroVenc.setDateFormat("dd/MM/yyyy");
					dfPrimeiroVenc.setReadOnly(true);
														
					addComponent(dfPrimeiroVenc);
				}
		});
		vlPrincipal.addComponent(new FormLayout(){
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					txtValorPrimeiraParc = new TextField("Valor Primeiro Vencimento");
					txtValorPrimeiraParc.addStyleName("caption-align-form-pgto");				
					txtValorPrimeiraParc.setReadOnly(true);
					txtValorPrimeiraParc.setId("txtValorPrimeiraParc");
					txtValorPrimeiraParc.setWidth("80px");					
					txtValorPrimeiraParc.addStyleName("align-currency");
					txtValorPrimeiraParc.setImmediate(true);
					txtValorPrimeiraParc.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							if(event.getText() != null && !event.getText().equals("") && !event.getText().equals("0,00")){
								
								double valorPrimeiraParc = Real.formatStringToDBDouble(event.getText());
								double valorTotalForma = Real.formatStringToDBDouble(txtValorFormaPgto.getValue());
								if(valorPrimeiraParc < valorTotalForma){
									event.getComponent().removeStyleName("invalid-txt");								
								}else{
									event.getComponent().addStyleName("invalid-txt");									
									Notify.Show("Valor da Primeira Parcela não pode ultrapassar o valor total da forma de pgto.", Notify.TYPE_NOTICE);
								}
							}else{
								event.getComponent().addStyleName("invalid-txt");									
								Notify.Show("Informe um valor Maior que 0,00", Notify.TYPE_NOTICE);
							}
						}
					});
														
					addComponent(txtValorPrimeiraParc);
					
					txtValorPrimeiraParc.setReadOnly(false);
					txtValorPrimeiraParc.setValue("0,00");
					txtValorPrimeiraParc.setReadOnly(true);
					JavaScript.getCurrent().execute("$('#txtValorPrimeiraParc').maskMoney({decimal:',',thousands:'.'})");
				}
		});
		vlPrincipal.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				addStyleName("button-margem-forma-pgto");
				setMargin(true);
				setSpacing(true);
				
				btAdicionarFormPgto = new Button("Adicionar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						addFormaPgto(event);
					}
				});
				btAdicionarFormPgto.setWidth("100px");
				btAdicionarFormPgto.addStyleName(Reindeer.BUTTON_SMALL);
				
				btAdicionarFormPgto.setEnabled(false); 
				btAdicionarFormPgto.addShortcutListener(sl1);
				
				addComponent(btAdicionarFormPgto);
			}
		});
		
		vlPrincipal.setCaption("principal");
		TabSheet.Tab tprincipal = ts.addTab(vlPrincipal, "Principal");
		
		if(haver != null){
			vlHaver.setCaption("haver");
			TabSheet.Tab thaver = ts.addTab(vlHaver, "Haver");
		}
		
		 
		 vlRoot.addComponent(ts);
		//Fim tab Pagamentos		
		
		
		vl1 = new VerticalLayout();
		vl1.addComponent(new Label("<h2 style='text-align: right;margin: 35px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL A PAGAR (R$) "+Real.formatDbToString(pedido.getSubTotal().toString())+ "</h2>", ContentMode.HTML));
		
		//vl2 = new VerticalLayout();
		
		vlFormasPgto = new VerticalLayout();
		//vlFormasPgtoTotal = new VerticalLayout();
		//vlFormasPgtoTotal.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) 0,00</h2>", ContentMode.HTML));
		vlFormasPgtoTotalRestante = new VerticalLayout();
		vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+"</h2>", ContentMode.HTML));
		
		VerticalLayout hlRootValores = new VerticalLayout(){
			{
				setWidth("274px");
				addComponent(vl1);
				addComponent(vlFormasPgto);
				//addComponent(vlFormasPgtoTotal);
				addComponent(vlFormasPgtoTotalRestante);
				
			}
		};
		hlRootValores.addStyleName("root_valores");
		vlRoot.addComponent(hlRootValores);
			
	    vlFormasPgtoTotalGeral = new VerticalLayout();
	    vlFormasPgtoTotalGeral.addComponent(new Label("<h2 style='padding-top: 14px;text-align: right;margin: 0px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+"</h2>", ContentMode.HTML));
	    VerticalLayout hlRootValorTotalGeral = new VerticalLayout(){
			{
				setWidth("274px");
				addComponent(vlFormasPgtoTotalGeral);				
			}
		};
		
		hlRootValorTotalGeral.addStyleName("root_valores");
		
		
		
		vlRoot.addComponent(new HorizontalLayout(){		
			{		
				addComponent(new FormLayout(){
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);
							
							txtDesc = new TextField("Desc (%)");
							txtDesc.addStyleName("caption-align-form-pgto");
							txtDesc.setWidth("80px");
							txtDesc.setId("txtDesc");
							txtDesc.addStyleName("align-currency");
							txtDesc.setImmediate(true);
							txtDesc.setReadOnly(true);
							txtDesc.setEnabled(false);
							
							txtDesc.addTextChangeListener(new FieldEvents.TextChangeListener() {
								
								@Override
								public void textChange(TextChangeEvent event) {
									if(event.getText() != null && !event.getText().equals("0,00") && !event.getText().equals("") && totais_forma_pgto.size() == 1){
										
										for(Entry<Integer,TotaisHash> entry : totais_forma_pgto.entrySet()) {
											formaPgtoSelecionada = FormaPgtoDAO.find(entry.getKey());
											break;
										}
										
										
										  if(Real.formatStringToDBDouble(event.getText()) > formaPgtoSelecionada.getDesc_max()){
											  Notify.Show("Desconto Máximo Permitido é de: "+formaPgtoSelecionada.getDesc_max() ,Notify.TYPE_ERROR);
										  	  event.getComponent().addStyleName("invalid-txt");
										  }else{
										
											  event.getComponent().removeStyleName("invalid-txt");
										
											  double percentual = Real.formatStringToDBDouble(event.getText()) / 100.0 ; 
											  double valor_final = txtAcrescPorc2.getValue() != null && !txtAcrescPorc2.getValue().equals("0,00") && !txtAcrescPorc2.getValue().isEmpty() ? 
													  Real.formatStringToDBDouble(new DecimalFormat("0.00").format((pedido.getSubTotal()+Real.formatStringToDBDouble(txtAcrescPorc2.getValue()))))												  
													   - Real.formatStringToDBDouble(new DecimalFormat("0.00").format((percentual * (pedido.getSubTotal()+Real.formatStringToDBDouble(txtAcrescPorc2.getValue()))))): pedido.getSubTotal() - Real.formatStringToDBDouble(new DecimalFormat("0.00").format((percentual * pedido.getSubTotal())));
											 
											 BigDecimal valorPrimeiraParc = new BigDecimal(valor_final);
														
											  valorPrimeiraParc = valorPrimeiraParc.setScale(2, BigDecimal.ROUND_CEILING);
											  subTotalPedido = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(valor_final));
											 
										
											  double  vlr_forma = 0;
											  
											  for(Entry<Integer, TotaisHash> entry : totais_forma_pgto.entrySet()) {		
												  if(entry.getKey().equals(formaPgtoSelecionada.getId())){
													  TotaisHash t = entry.getValue();
													  vlr_forma = t.getValor();														
												  }
											  }
											  
											  if(vlr_forma_default == 0){
												  vlr_forma_default = vlr_forma;
											  }
												
											  double vlr_pedido =  pedido.getSubTotal();
											  double vlrDesconto = (Real.formatStringToDBDouble(event.getText())* pedido.getSubTotal())/100;
											  double vlr_diferenca = vlr_pedido - vlrDesconto;
											  double vlr_desconto = vlr_pedido - vlr_diferenca;
											  double vlr_novo_forma = vlr_forma_default - vlr_desconto;
											  
											  if(vlr_novo_forma < 0){
												  vlr_novo_forma = 0;
											  }
											
											  totais_forma_pgto.remove(formaPgtoSelecionada.getId());
											  totais_forma_pgto.put(formaPgtoSelecionada.getId(), new TotaisHash(formaPgtoSelecionada.getId(), vlr_novo_forma, Integer.parseInt(txtParcelamento.getValue())));
											  											  
													
										      vlFormasPgtoTotalGeral.removeAllComponents();
										      vlFormasPgtoTotalGeral.addComponent(new Label("<h2 style='padding-top: 14px;text-align: right;margin: 0px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+"</h2>", ContentMode.HTML));
											 
											  //vl2.replaceComponent(lbDesconto, BuildLbDesconto(Real.formatDbToString(String.valueOf(vlr_desc_total))));
										  }
									}else{
										
										if(totais_forma_pgto.size() == 1){
											for(Entry<Integer, TotaisHash> entry : totais_forma_pgto.entrySet()) {
												formaPgtoSelecionada = FormaPgtoDAO.find(entry.getKey());
												break;
											}
											
											 double percentual = event.getText() != null && !event.getText().equals("") && !event.getText().equals("0,00") && !event.getText().isEmpty() ? Real.formatStringToDBDouble(event.getText()) / 100.0 : 0/100.0; 
											 //double valor_final = txtAcrescPorc2.getValue() != null && !txtAcrescPorc2.getValue().equals("0,00") && !txtAcrescPorc2.getValue().isEmpty() ? (pedido.getSubTotal()+Real.formatStringToDBDouble(txtAcrescPorc2.getValue())) +(percentual * (pedido.getSubTotal()+Real.formatStringToDBDouble(txtAcrescPorc2.getValue()))) : pedido.getSubTotal() - (percentual * pedido.getSubTotal());
											 double valor_final = txtAcrescPorc2.getValue() != null && !txtAcrescPorc2.getValue().equals("0,00") && !txtAcrescPorc2.getValue().isEmpty() ? 
													  Real.formatStringToDBDouble(new DecimalFormat("0.00").format((pedido.getSubTotal()+Real.formatStringToDBDouble(txtAcrescPorc2.getValue()))))												  
													   - Real.formatStringToDBDouble(new DecimalFormat("0.00").format((percentual * (pedido.getSubTotal()+Real.formatStringToDBDouble(txtAcrescPorc2.getValue()))))): pedido.getSubTotal() - Real.formatStringToDBDouble(new DecimalFormat("0.00").format((percentual * pedido.getSubTotal())));
											
											 subTotalPedido =Real.formatStringToDBDouble(new DecimalFormat("0.00").format(valor_final));
											 
											 double  vlr_forma = 0;
											  
											  for(Entry<Integer, TotaisHash> entry : totais_forma_pgto.entrySet()) {		
												  if(entry.getKey().equals(formaPgtoSelecionada.getId())){
													  TotaisHash t = entry.getValue();
													  vlr_forma = t.getValor();														
												  }
											  }
												
											  
											  double vlr_pedido =  pedido.getSubTotal();
											  double vlrDesconto = event.getText() != null && pedido != null && pedido.getSubTotal() != null ? (Real.formatStringToDBDouble(event.getText())* pedido.getSubTotal())/100 : 0;
											  double vlr_diferenca = vlr_pedido - vlrDesconto;
											  double vlr_desconto = vlr_pedido - vlr_diferenca;
											  double vlr_novo_forma = vlr_forma - vlr_desconto;
											  
											  if(vlr_novo_forma < 0){
												  vlr_novo_forma = 0;
											  }
											
											  totais_forma_pgto.remove(formaPgtoSelecionada.getId());
											  totais_forma_pgto.put(formaPgtoSelecionada.getId(), new TotaisHash(formaPgtoSelecionada.getId(), vlr_novo_forma, Integer.parseInt(txtParcelamento.getValue())));
											 
										}
										
										 
										//vl2.replaceComponent(lbTotal, BuildLbValorTotal(Real.formatDbToString(String.valueOf(pedido.getValor()-total_pagando))));	
										subTotalPedido =  txtAcrescPorc2.getValue() != null && !txtAcrescPorc2.getValue().equals("0,00") && !txtAcrescPorc2.getValue().isEmpty() ? pedido.getSubTotal()+Real.formatStringToDBDouble(txtAcrescPorc2.getValue()) : pedido.getSubTotal();
										//	totais_forma_pgto.remove(formaPgtoSelecionada.getId());
										//	totais_forma_pgto.put(formaPgtoSelecionada.getId(), total);
											
										vlFormasPgtoTotalGeral.removeAllComponents();
										vlFormasPgtoTotalGeral.addComponent(new Label("<h2 style='padding-top: 14px;text-align: right;margin: 0px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+"</h2>", ContentMode.HTML));
										
										 
										// vl2.replaceComponent(lbDesconto, BuildLbDesconto("0,00"));
									}
								}
							});
							
							addComponent(txtDesc);
							JavaScript.getCurrent().execute("$('#txtDesc').maskMoney({decimal:',',thousands:'.'})");
						}
				});
				
				
			}
		});
		
		vlRoot.addComponent(hlRootValorTotalGeral);
		
		
		
	}	
	private JPAContainer<FormasPgto> getFormasPgto(){
		JPAContainer<FormasPgto> container = JPAContainerFactory.make(FormasPgto.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(pedido.getCliente() == null){
			container.addContainerFilter(Filters.eq("vincular_cliente", false));
		}
		
		return container;
	}
	private Label BuildLbAcrescimo(String valor){
		return lbAcrescimo = new Label("<h2 style='text-align: right;margin:0;font-size: 13px;font-family: Courier New;'>"+valor+"</h2>", ContentMode.HTML);		
	}
	private Label BuildLbDesconto(String valor){
		return lbDesconto = new Label("<h2 style='text-align: right;margin:0;font-size: 13px;font-family: Courier New;'>"+valor+"</h2>", ContentMode.HTML);		
	}	
	private Label BuildLbValorTotal(String valor){
		return lbTotal = new Label("<h2 style='text-align: right;margin:0;border-top: 1px solid #c3c3c3;font-size: 13px;font-family: Courier New;'>"+valor+"</h2>", ContentMode.HTML);		
	}
	public Button buildBtSalvar() {
		
		btSalvar = new Button("Finalizar (F10)", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				Integer size = 0;
				
				if(totais_forma_pgto.size() > 0){
					size = 1;
				}
				if(haverSelecionado != null){
					size = 1;
				}
				
				boolean primeirVenc = !dfPrimeiroVenc.isReadOnly() && dfPrimeiroVenc.isEnabled() ? dfPrimeiroVenc.isValid() : true ;
				boolean parcelamentValid = !txtParcelamento.isReadOnly() && txtParcelamento.isEnabled() ? txtParcelamento.isValid() : true;
				boolean valorPrimeiroParc = !txtValorPrimeiraParc.isReadOnly() && txtValorPrimeiraParc.isEnabled() ? txtValorPrimeiraParc.isValid() : true;
				double valorTotalVenda = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(subTotalPedido)) ;	
				 										  
				double percentual =!txtDesc.isReadOnly() && txtDesc.getValue() != null &&  !txtDesc.getValue().equals("") && !txtDesc.getValue().equals("0,00")?  Real.formatStringToDBDouble(txtDesc.getValue()) / 100.0 : 0; 
				double valor_final = txtAcrescPorc2.getValue() != null && !txtAcrescPorc2.getValue().equals("0,00") && !txtAcrescPorc2.getValue().isEmpty() ? 
						Real.formatStringToDBDouble(new DecimalFormat("0.00").format((pedido.getSubTotal()+Real.formatStringToDBDouble(txtAcrescPorc2.getValue()))))												  
						- Real.formatStringToDBDouble(new DecimalFormat("0.00").format((percentual * (pedido.getSubTotal()+Real.formatStringToDBDouble(txtAcrescPorc2.getValue()))))): pedido.getSubTotal() - Real.formatStringToDBDouble(new DecimalFormat("0.00").format((percentual * pedido.getSubTotal())));
				
						valorTotalVenda = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(valor_final));
						
				double valorAdicionado =Real.formatStringToDBDouble(new DecimalFormat("0.00").format((Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total_pagando)) - (percentual * Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total_pagando)))))) ;	
				if(percentual > 0){
					valorAdicionado = valorTotalVenda;
				}

				if(size > 0 && valorAdicionado == valorTotalVenda && primeirVenc && parcelamentValid && valorPrimeiroParc){
					
						if(txtAcrescPorc2.getValue() != null && !txtAcrescPorc2.getValue().equals("0,00") && !txtAcrescPorc2.getValue().equals("")){
							pedido.setTotal_acres(Real.formatStringToDBFloat(txtAcrescPorc2.getValue()));
						}
						
						if(txtDesc.getValue() != null && !txtDesc.getValue().equals("0,00") && !txtDesc.getValue().equals("")){
							pedido.setTotal_desc(Real.formatStringToDBFloat(txtDesc.getValue()));
						}
						
						pedido.setData(new Date());
						pedido.setValor(new Float(subTotalPedido));
						pedido.setSituacao("F");
						
						if(txtParcelamento.getValue() == null || txtParcelamento.getValue().equals("")){
							txtParcelamento.setReadOnly(false);
							txtParcelamento.setValue("1");
							txtParcelamento.setReadOnly(true);
						}
						 
						double valorPrimeiraParc = txtValorPrimeiraParc  != null && txtValorPrimeiraParc.getValue() != null && !txtValorPrimeiraParc.getValue().equals("") ? Real.formatStringToDBDouble(txtValorPrimeiraParc.getValue()): 0;
										
						fireEvent(new FormaPgtoEvent(getUI(),pedido,Integer.parseInt(txtParcelamento.getValue()),dfPrimeiroVenc.getValue(),valorPrimeiraParc,totais_forma_pgto,haverSelecionado,haver_valor,parcelas_forma_pgto,true));
				
				}else{
					
					if(size == 0){
						Notify.Show("Nenhuma forma de pgto adicionada", Notify.TYPE_WARNING);
					}
					
					if(valorTotalVenda > valorAdicionado){
						Notify.Show("Complete o total da venda com outra forma de pgto.", Notify.TYPE_WARNING);
					}
					
					if(valorTotalVenda < valorAdicionado){
						Notify.Show("Valor total das formas de pgto. não pode exceder valor total da venda.", Notify.TYPE_WARNING);
					}
					
				}
				
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("F10", ShortcutAction.KeyCode.F10,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}
	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
									
				GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja Cancelar este Pedido ?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					public void onClose(DialogEvent event) {
						
						if(event.isConfirm()){
							boolean check = PedidoDAO.CancelarPedido(pedido);
							if(check){
								close();
								Notify.Show("Pedido Cancelado com Sucesso",Notify.TYPE_SUCCESS);
							}
						}else{
							fireEvent(new FormaPgtoEvent(getUI(),pedido, 0,null,0,totais_forma_pgto,haverSelecionado,haver_valor,parcelas_forma_pgto,false));
							close();
						}
					}
				});
				
				getUI().addWindow(gd);				
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
	private boolean allowFormaPgto(){
		for(Entry<Integer, TotaisHash> entry : totais_forma_pgto.entrySet()) {
			
			if(entry.getKey() == formaPgtoSelecionada.getId()){
				return false;
			}
		}
		
		return true;
	}
	
	private void reloadFormasPgto(){
		vlFormasPgto.removeAllComponents();
		for(Entry<Integer, TotaisHash> entry : totais_forma_pgto.entrySet()) {
			
			FormasPgto formaPgto = FormaPgtoDAO.find(entry.getKey());
			TotaisHash t= entry.getValue();			
			Double valorFormaPGto = t.getValor();
			
			Button btX = new Button();
			btX.setIcon(new ThemeResource("icons/icon_close.png"));									
			btX.setStyleName(BaseTheme.BUTTON_LINK);
			btX.addStyleName("bt-close");
			btX.setId(String.valueOf(formaPgto.getId()));
			btX.addClickListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					if(event.getComponent().getParent().getParent() instanceof VerticalLayout && event.getButton() != null && totais_forma_pgto != null){
						
						FormasPgto formaPgto = FormaPgtoDAO.find(Integer.parseInt(event.getButton().getId()));
						if(formaPgto != null && formaPgto.isAprovacao_credito()){
							txtParcelamento.setReadOnly(false);
							txtParcelamento.setValue("1");
							txtParcelamento.setReadOnly(true);
							
							dfPrimeiroVenc.setReadOnly(false);
							dfPrimeiroVenc.setValue(new Date());
							dfPrimeiroVenc.setReadOnly(true);
							
							txtValorPrimeiraParc.setReadOnly(false);
							txtValorPrimeiraParc.setValue("0,00");
							txtValorPrimeiraParc.setReadOnly(true);
						}
						
						double desconto = txtDesc != null && !txtDesc.isReadOnly() && txtDesc.getValue() != null && !txtDesc.getValue().equals("")   && !txtDesc.getValue().isEmpty()  ? Real.formatStringToDBDouble(txtDesc.getValue()) : 0;
						
						double percentual = desconto / 100.0; 
						double valor_desconto = (percentual * pedido.getSubTotal());																			
						
						TotaisHash t = totais_forma_pgto.get(Integer.parseInt(event.getButton().getId()));
						double valorForma = t.getValor()+valor_desconto;
						subTotalPedido = subTotalPedido+valor_desconto;
						
						//double valorForma = totais_forma_pgto.get(Integer.parseInt(event.getButton().getId()));
						total_pagando = total_pagando - valorForma;
						totais_forma_pgto.remove(Integer.parseInt(event.getButton().getId()));
						cbFormaPgto.setEnabled(true);
						cbFormaPgto.setValue(null); 
						txtValorFormaPgto.setEnabled(true);
						btAdicionarFormPgto.setEnabled(true); 
						txtDesc.setValue("0,00");
						//txtAcrescPorc2.setValue("0,00");
						
						//vlFormasPgtoTotal.removeAllComponents();			
						
						if(total_pagando < subTotalPedido){
							//vlFormasPgtoTotal.addComponent(	new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));
							
							double restante = subTotalPedido-total_pagando;										
							vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) "+Real.formatDbToString(String.valueOf(restante))+"</h2>", ContentMode.HTML));			
						}else{
							//vlFormasPgtoTotal.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));										
							vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) 0,00</h2>", ContentMode.HTML));
						}

						
						((VerticalLayout)event.getComponent().getParent().getParent()).removeComponent(event.getComponent().getParent());
																														
						if(totais_forma_pgto.size() == 1 && Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total)) == 
								Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total_pagando))){
							
							FormasPgto forma = FormaPgtoDAO.find(Integer.parseInt(event.getButton().getId()));
							if(forma.getDesc_max() > 0){
								txtDesc.setEnabled(true); 
							}else{
								txtDesc.setEnabled(false);
							}
							//txtAcrescPorc2.setEnabled(true); 
						}else{
							txtDesc.setEnabled(false); 
							//txtAcrescPorc2.setEnabled(false); 
						}
						
						vlFormasPgtoTotalGeral.removeAllComponents();
						vlFormasPgtoTotalGeral.addComponent(new Label("<h2 style='padding-top: 14px;text-align: right;margin: 0px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+"</h2>", ContentMode.HTML));
					}
				}
			});
			
			
			
			
			Label lbValorFormaPgto = new Label("<h2 style='text-align: right;margin: 0px ;border-top: 1px solid #c3c3c3; background-color: #FCFFD4;font-size: 13px;font-family: Courier New;'>"+formaPgto.getNome()+" (R$) "+Real.formatDbToString(String.valueOf(valorFormaPGto))+"</h2>", ContentMode.HTML);

			HorizontalLayout hlformaPgto = new HorizontalLayout(lbValorFormaPgto, btX);
			hlformaPgto.setExpandRatio(lbValorFormaPgto, 1.0f);
			
			hlformaPgto.setWidth("100%");
			vlFormasPgto.addComponent(hlformaPgto);
		}
	}
	private void addFormaPgtoHaver(ClickEvent event){
		if(txtValorFormaPgtoHaver.getValue() != null && !txtValorFormaPgtoHaver.getValue().equals("0,00") && haverSelecionado != null){
			
			double valorInformado = Real.formatStringToDBDouble(txtValorFormaPgtoHaver.getValue());					
			if(haverSelecionado.getValor_total() >=Real.formatStringToDBDouble(txtValorFormaPgtoHaver.getValue()) && converterDoubleDoisDecimais(valorInformado) <= converterDoubleDoisDecimais(pedido.getValor())){
				
					double valor_disponivel_haver = haverSelecionado.getValor_total();
					double novo_valor_disponivel = valor_disponivel_haver- valorInformado;
					haverSelecionado.setValor_total(novo_valor_disponivel);	
					total_pagando = total_pagando+valorInformado;														
					vlFormasPgto.removeAllComponents();
						
						
					Button btXHaver = new Button();
					btXHaver.setIcon(new ThemeResource("icons/icon_close.png"));									
					btXHaver.setStyleName(BaseTheme.BUTTON_LINK);
					btXHaver.addStyleName("bt-close");
					btXHaver.setId(String.valueOf(haverSelecionado.getId()));
					btXHaver.addClickListener(new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							
							if(event.getComponent().getParent().getParent() instanceof VerticalLayout && event.getButton() != null){
								
								total_pagando = total_pagando - haver_valor;
								haverSelecionado.setValor_total(haverSelecionado.getValor_total()+haver_valor);
								haver_valor = 0;
								txtValorDisponivel.setReadOnly(false);
								txtValorDisponivel.setValue(Real.formatDbToString(String.valueOf(haverSelecionado.getValor_total())));
								txtValorDisponivel.setReadOnly(true);
								
								((VerticalLayout)event.getComponent().getParent().getParent()).removeComponent(event.getComponent().getParent());
								
								vlFormasPgtoTotalRestante.removeAllComponents();
								if(total_pagando < subTotalPedido){																	
									double restante = subTotalPedido-total_pagando;										
									vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) "+Real.formatDbToString(String.valueOf(restante))+"</h2>", ContentMode.HTML));			
								}else{										
									vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) 0,00</h2>", ContentMode.HTML));
								}
								
								//haverSelecionado = null;
								haver_valor = 0;
							}
						}
					});
												
					Label lbValorFormaPgtoHaver = new Label("<h2 style='text-align: right;margin: 0px ;border-top: 1px solid #c3c3c3; background-color: #FCFFD4;font-size: 13px;font-family: Courier New; margin-right: 19px;' >HAVER (R$) "+Real.formatDbToString(String.valueOf(valorInformado))+"</h2>", ContentMode.HTML);

					HorizontalLayout hlformaPgtoHaver = new HorizontalLayout(lbValorFormaPgtoHaver);
					hlformaPgtoHaver.setExpandRatio(lbValorFormaPgtoHaver, 1.0f);
					
					hlformaPgtoHaver.setWidth("100%");
					vlFormasPgto.addComponent(hlformaPgtoHaver);
						
						
						
						for(Entry<Integer, TotaisHash> entry : totais_forma_pgto.entrySet()) {
							
							FormasPgto formaPgto = FormaPgtoDAO.find(entry.getKey());
							TotaisHash t = entry.getValue();
							final Double valorPgto = t.getValor();
							
							Button btX = new Button();
							btX.setIcon(new ThemeResource("icons/icon_close.png"));									
							btX.setStyleName(BaseTheme.BUTTON_LINK);
							btX.addStyleName("bt-close");
							btX.setId(String.valueOf(formaPgto.getId()));
							btX.addClickListener(new Button.ClickListener() {
								
								@Override
								public void buttonClick(ClickEvent event) {
									
									if(event.getComponent().getParent().getParent() instanceof VerticalLayout && event.getButton() != null && totais_forma_pgto != null){
										
										FormasPgto formaPgto = FormaPgtoDAO.find(Integer.parseInt(event.getButton().getId()));
										if(formaPgto != null && formaPgto.isAprovacao_credito()){
											txtParcelamento.setReadOnly(false);
											txtParcelamento.setValue("1");
											txtParcelamento.setReadOnly(true);
											
											dfPrimeiroVenc.setReadOnly(false);
											dfPrimeiroVenc.setValue(new Date());
											dfPrimeiroVenc.setReadOnly(true);
											
											txtValorPrimeiraParc.setReadOnly(false);
											txtValorPrimeiraParc.setValue("0,00");
											txtValorPrimeiraParc.setReadOnly(true);
										}
										
										
										double desconto = txtDesc != null && !txtDesc.isReadOnly() && txtDesc.getValue() != null && !txtDesc.getValue().equals("")   && !txtDesc.getValue().isEmpty()  ? Real.formatStringToDBDouble(txtDesc.getValue()) : 0;
										
										double percentual = desconto / 100.0; 
										double valor_desconto = Real.formatStringToDBDouble(new DecimalFormat("0.00").format((percentual * subTotalPedido)));																			
										
										//removido aqui
										//double valorForma = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(totais_forma_pgto.get(Integer.parseInt(event.getButton().getId()))))+valor_desconto;
										double valorAcrescimo = txtAcrescPorc2 != null && !txtAcrescPorc2.isReadOnly() && txtAcrescPorc2.getValue() != null && !txtAcrescPorc2.getValue().equals("")   && !txtAcrescPorc2.getValue().isEmpty() ? Real.formatStringToDBDouble(txtAcrescPorc2.getValue()) : 0;
										subTotalPedido = Real.formatStringToDBDouble(String.valueOf(new DecimalFormat("0.00").format(pedido.getSubTotal()+valorAcrescimo)));
										
										totais_forma_pgto.remove(Integer.parseInt(event.getButton().getId()));
	
										total_pagando = 0;
										for(Entry<Integer, TotaisHash> entry : totais_forma_pgto.entrySet()) {
											//FormasPgto formaPgto = FormaPgtoDAO.find(entry.getKey());
											TotaisHash t = entry.getValue();
											Double valorPgto = t.getValor();
											total_pagando = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total_pagando+valorPgto));
										}
																				
										cbFormaPgto.setEnabled(true);
										cbFormaPgto.setValue(null); 
										txtValorFormaPgto.setEnabled(true);
										btAdicionarFormPgto.setEnabled(true); 
										txtDesc.setValue("0,00");
										
										//vlFormasPgtoTotal.removeAllComponents();			
										vlFormasPgtoTotalRestante.removeAllComponents();
										if(total_pagando < subTotalPedido){
											//vlFormasPgtoTotal.addComponent(	new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));
											
											double restante = subTotalPedido-total_pagando;										
											vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) "+Real.formatDbToString(String.valueOf(restante))+"</h2>", ContentMode.HTML));			
										}else{
											//vlFormasPgtoTotal.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));										
											vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) 0,00</h2>", ContentMode.HTML));
										}
										
										((VerticalLayout)event.getComponent().getParent().getParent()).removeComponent(event.getComponent().getParent());
																																		
										if(totais_forma_pgto.size() == 1 && Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total)) == 
												Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total_pagando))){
											
											FormasPgto forma = FormaPgtoDAO.find(Integer.parseInt(event.getButton().getId()));
											if(forma.getDesc_max() > 0){
												txtDesc.setEnabled(true); 
											}else{
												txtDesc.setEnabled(false);
											}
											//txtAcrescPorc2.setEnabled(true); 
										}else{
											txtDesc.setEnabled(false); 
											//txtAcrescPorc2.setEnabled(false); 
										}
										
										vlFormasPgtoTotalGeral.removeAllComponents();
										vlFormasPgtoTotalGeral.addComponent(new Label("<h2 style='padding-top: 14px;text-align: right;margin: 0px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+"</h2>", ContentMode.HTML));
									}
								}
							});
							
							
							Label lbValorFormaPgto = new Label("<h2 style='text-align: right;margin: 0px ;border-top: 1px solid #c3c3c3; background-color: #FCFFD4;font-size: 13px;font-family: Courier New;'>"+formaPgto.getNome()+" (R$) "+Real.formatDbToString(String.valueOf(valorPgto))+"</h2>", ContentMode.HTML);
	
							HorizontalLayout hlformaPgto = new HorizontalLayout(lbValorFormaPgto, btX);
							hlformaPgto.setExpandRatio(lbValorFormaPgto, 1.0f);
							
							hlformaPgto.setWidth("100%");
							vlFormasPgto.addComponent(hlformaPgto);
						}
						
															
						//vlFormasPgtoTotal.removeAllComponents();			
						vlFormasPgtoTotalRestante.removeAllComponents();
						if(total_pagando < subTotalPedido){
							//vlFormasPgtoTotal.addComponent(	new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));
							
							double rest = subTotalPedido-total_pagando;
							vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) "+Real.formatDbToString(String.valueOf(rest))+"</h2>", ContentMode.HTML));
						}else{
							//vlFormasPgtoTotal.addComponent(	new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));
							vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) 0,00</h2>", ContentMode.HTML));
						}
						
						//cbFormaPgto.setValue(null); 
						txtValorFormaPgto.setValue("0,00");
						
						if(total_pagando >= subTotalPedido){
							//event.getButton().setEnabled(false);						
							btSalvar.focus();
						}else{					
							//cbFormaPgto.focus();
						}
						
						double t_pagando = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total_pagando));
						double t_valor = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(subTotalPedido));
						int total_form = totais_forma_pgto.size();
						
						txtValorFormaPgtoHaver.setValue("");
						//txtValorFormaPgtoHaver.setEnabled(false); 
						//tbHaver.select(null);
						
			}else{
				
				if(valorInformado > pedido.getValor()){
					Notify.Show("O Valor informado é maior do que o total do pedido", Notify.TYPE_ERROR);
				}else{
					Notify.Show("Não há "+txtValorFormaPgtoHaver.getValue()+" disponível no HAVER selecionado, informe um valor menor.", Notify.TYPE_ERROR);					
				}								
			}
			
		}else{
			if(!allowFormaPgto()){
				Notify.Show("Esta forma de pagamento já foi adicionada", Notify.TYPE_WARNING);
			}
		}
		
	}
	
	public static double converterDoubleDoisDecimais(double precoDouble) {  
	    DecimalFormat fmt = new DecimalFormat("0.00");       
	    String string = fmt.format(precoDouble);  
	    String[] part = string.split("[,]");  
	    String string2 = part[0]+"."+part[1];  
	        double preco = Double.parseDouble(string2);  
	    return preco;  
	}  
	private void addFormaPgto(ClickEvent event){
		if(txtValorFormaPgto.getValue() != null && !txtValorFormaPgto.getValue().equals("0,00") && allowFormaPgto()){
			double valorInformado = Real.formatStringToDBDouble(txtValorFormaPgto != null && txtValorFormaPgto.getValue() != null ? txtValorFormaPgto.getValue() : "0,00");
			
			boolean allow = true;
			if(pedido.getCliente() != null && formaPgtoSelecionada != null && formaPgtoSelecionada.isAprovacao_credito()){
				String valor = CreditoClienteDAO.getSaldo(pedido.getCliente().getId());
				
				if(valor != null){
					double creditoSaldo = new Float(valor);
					if(creditoSaldo < valorInformado){
						allow = false;
						Notify.Show("Limite de Crédito Excedido! Selecione outra Forma de Pgto.",Notify.TYPE_ERROR);
					}
				}else{
					allow = false;
					Notify.Show("Limite de Crédito Excedido! Selecione outra Forma de Pgto.",Notify.TYPE_ERROR);
				}
			}
			
			if(formaPgtoSelecionada != null && formaPgtoSelecionada.getN_parcelas() != null && formaPgtoSelecionada.getN_parcelas() > 0){
				if(!txtParcelamento.isValid() || txtParcelamento.getValue() == null || txtParcelamento.getValue().equals("") || txtParcelamento.getValue().isEmpty() || txtParcelamento.getValue().equals("0")){
					allow = false;
				}
			}
		
			if(allow){
			
				totais_forma_pgto.put(formaPgtoSelecionada.getId(), new TotaisHash(formaPgtoSelecionada.getId(), valorInformado, Integer.parseInt(txtParcelamento.getValue())) );
				parcelas_forma_pgto.put(formaPgtoSelecionada.getId(), Integer.parseInt(txtParcelamento.getValue()));
			
				
				//if(valorInformado < subTotalPedido && total_pagando < subTotalPedido){
					
					total_pagando = total_pagando+valorInformado;
					double restante = subTotalPedido-total_pagando;
					//vl2.replaceComponent(lbTotal, BuildLbValorTotal(Real.formatDbToString(new String().valueOf(restante))));
													
					vlFormasPgto.removeAllComponents();
					for(Entry<Integer, TotaisHash> entry : totais_forma_pgto.entrySet()) {
						
						FormasPgto formaPgto = FormaPgtoDAO.find(entry.getKey());
						TotaisHash t = entry.getValue();
						final Double valorPgto = t.getValor();
						
						Button btX = new Button();
						btX.setIcon(new ThemeResource("icons/icon_close.png"));									
						btX.setStyleName(BaseTheme.BUTTON_LINK);
						btX.addStyleName("bt-close");
						btX.setId(String.valueOf(formaPgto.getId()));
						btX.addClickListener(new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								
								if(event.getComponent().getParent().getParent() instanceof VerticalLayout && event.getButton() != null && totais_forma_pgto != null){
									
									FormasPgto formaPgto = FormaPgtoDAO.find(Integer.parseInt(event.getButton().getId()));
									if(formaPgto != null && formaPgto.isAprovacao_credito()){
										txtParcelamento.setReadOnly(false);
										txtParcelamento.setValue("1");
										txtParcelamento.setReadOnly(true);
										
										dfPrimeiroVenc.setReadOnly(false);
										dfPrimeiroVenc.setValue(new Date());
										dfPrimeiroVenc.setReadOnly(true);
										
										txtValorPrimeiraParc.setReadOnly(false);
										txtValorPrimeiraParc.setValue("0,00");
										txtValorPrimeiraParc.setReadOnly(true);
									}
									
									
									double desconto = txtDesc != null && !txtDesc.isReadOnly() && txtDesc.getValue() != null && !txtDesc.getValue().equals("")   && !txtDesc.getValue().isEmpty()  ? Real.formatStringToDBDouble(txtDesc.getValue()) : 0;
									
									double percentual = desconto / 100.0; 
									double valor_desconto = Real.formatStringToDBDouble(new DecimalFormat("0.00").format((percentual * subTotalPedido)));																			
									
									//removido aqui
									//double valorForma = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(totais_forma_pgto.get(Integer.parseInt(event.getButton().getId()))))+valor_desconto;
									double valorAcrescimo = txtAcrescPorc2 != null && !txtAcrescPorc2.isReadOnly() && txtAcrescPorc2.getValue() != null && !txtAcrescPorc2.getValue().equals("")   && !txtAcrescPorc2.getValue().isEmpty() ? Real.formatStringToDBDouble(txtAcrescPorc2.getValue()) : 0;
									subTotalPedido = Real.formatStringToDBDouble(String.valueOf(new DecimalFormat("0.00").format(pedido.getSubTotal()+valorAcrescimo)));
									
									totais_forma_pgto.remove(Integer.parseInt(event.getButton().getId()));

									total_pagando = 0;
									for(Entry<Integer, TotaisHash> entry : totais_forma_pgto.entrySet()) {
										//FormasPgto formaPgto = FormaPgtoDAO.find(entry.getKey());
										TotaisHash t = entry.getValue();
										Double valorPgto = t.getValor();
										total_pagando = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total_pagando+valorPgto));
									}
									if(haverSelecionado != null && haver_valor > 0){
										total_pagando = total_pagando+haver_valor;
									}
									
									cbFormaPgto.setEnabled(true);
									cbFormaPgto.setValue(null); 
									txtValorFormaPgto.setEnabled(true);
									btAdicionarFormPgto.setEnabled(true); 
									txtDesc.setValue("0,00");
									
									//vlFormasPgtoTotal.removeAllComponents();			
									vlFormasPgtoTotalRestante.removeAllComponents();
									if(total_pagando < subTotalPedido){
										//vlFormasPgtoTotal.addComponent(	new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));
										
										double restante = subTotalPedido-total_pagando;										
										vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) "+Real.formatDbToString(String.valueOf(restante))+"</h2>", ContentMode.HTML));			
									}else{
										//vlFormasPgtoTotal.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));										
										vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) 0,00</h2>", ContentMode.HTML));
									}
									
									((VerticalLayout)event.getComponent().getParent().getParent()).removeComponent(event.getComponent().getParent());
																																	
									if(totais_forma_pgto.size() == 1 && Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total)) == 
											Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total_pagando))){
										
										FormasPgto forma = FormaPgtoDAO.find(Integer.parseInt(event.getButton().getId()));
										if(forma.getDesc_max() > 0){
											txtDesc.setEnabled(true); 
										}else{
											txtDesc.setEnabled(false);
										}
										//txtAcrescPorc2.setEnabled(true); 
									}else{
										txtDesc.setEnabled(false); 
										//txtAcrescPorc2.setEnabled(false); 
									}
									
									vlFormasPgtoTotalGeral.removeAllComponents();
									vlFormasPgtoTotalGeral.addComponent(new Label("<h2 style='padding-top: 14px;text-align: right;margin: 0px 19px 0 0;font-size: 13px;font-family: Courier New;'>TOTAL (R$) "+Real.formatDbToString(String.valueOf(subTotalPedido))+"</h2>", ContentMode.HTML));
								}
							}
						});
												
						Label lbValorFormaPgto = new Label("<h2 style='text-align: right;margin: 0px ;border-top: 1px solid #c3c3c3; background-color: #FCFFD4;font-size: 13px;font-family: Courier New;'>"+formaPgto.getNome()+" (R$) "+Real.formatDbToString(String.valueOf(valorPgto))+"</h2>", ContentMode.HTML);

						HorizontalLayout hlformaPgto = new HorizontalLayout(lbValorFormaPgto, btX);
						hlformaPgto.setExpandRatio(lbValorFormaPgto, 1.0f);
						
						hlformaPgto.setWidth("100%");
						vlFormasPgto.addComponent(hlformaPgto);
					}
					if(haverSelecionado != null && haver_valor > 0){
						Button btXHaver = new Button();
						btXHaver.setIcon(new ThemeResource("icons/icon_close.png"));									
						btXHaver.setStyleName(BaseTheme.BUTTON_LINK);
						btXHaver.addStyleName("bt-close");
						btXHaver.setId(String.valueOf(haverSelecionado.getId()));
						btXHaver.addClickListener(new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								
								if(event.getComponent().getParent().getParent() instanceof VerticalLayout && event.getButton() != null){
									
									total_pagando = total_pagando - haver_valor;
									haverSelecionado.setValor_total(haverSelecionado.getValor_total()+haver_valor);
									haver_valor = 0;
									txtValorDisponivel.setReadOnly(false);
									txtValorDisponivel.setValue(Real.formatDbToString(String.valueOf(haverSelecionado.getValor_total())));
									txtValorDisponivel.setReadOnly(true);
									
									((VerticalLayout)event.getComponent().getParent().getParent()).removeComponent(event.getComponent().getParent());
									
									vlFormasPgtoTotalRestante.removeAllComponents();
									if(total_pagando < subTotalPedido){																	
										double restante = subTotalPedido-total_pagando;										
										vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) "+Real.formatDbToString(String.valueOf(restante))+"</h2>", ContentMode.HTML));			
									}else{										
										vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) 0,00</h2>", ContentMode.HTML));
									}
									
									haverSelecionado = null;
									haver_valor = 0;
								}
							}
						});
													
						Label lbValorFormaPgtoHaver = new Label("<h2 style='text-align: right;margin: 0px ;border-top: 1px solid #c3c3c3; background-color: #FCFFD4;font-size: 13px;font-family: Courier New;margin-right: 19px;'>HAVER (R$) "+Real.formatDbToString(String.valueOf(haver_valor))+"</h2>", ContentMode.HTML);

						HorizontalLayout hlformaPgtoHaver = new HorizontalLayout(lbValorFormaPgtoHaver);
						hlformaPgtoHaver.setExpandRatio(lbValorFormaPgtoHaver, 1.0f);
						
						hlformaPgtoHaver.setWidth("100%");
						vlFormasPgto.addComponent(hlformaPgtoHaver);
					}
					
					
												
					//vlFormasPgtoTotal.removeAllComponents();			
					vlFormasPgtoTotalRestante.removeAllComponents();
					if(total_pagando < subTotalPedido){
						//vlFormasPgtoTotal.addComponent(	new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));
						
						double rest = subTotalPedido-total_pagando;
						vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(171, 2, 2, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) "+Real.formatDbToString(String.valueOf(rest))+"</h2>", ContentMode.HTML));
					}else{
						//vlFormasPgtoTotal.addComponent(	new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 14px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>TOTAL PAGO (R$) "+Real.formatDbToString(String.valueOf(total_pagando))+"</h2>", ContentMode.HTML));
						vlFormasPgtoTotalRestante.addComponent(new Label("<h2 style='color: rgba(43, 125, 18, 1);padding-top: 2px;text-align: right;margin: 0px 19px 0px 0;border-top: 1px solid #c3c3c3; font-size: 13px;font-family: Courier New;'>RESTANTE (R$) 0,00</h2>", ContentMode.HTML));
					}
					
					//cbFormaPgto.setValue(null); 
					txtValorFormaPgto.setValue("0,00");
					
					if(total_pagando >= subTotalPedido){
						event.getButton().setEnabled(false);
						cbFormaPgto.setEnabled(false);
						txtValorFormaPgto.setEnabled(false); 
						
						txtParcelamento.setReadOnly(true);
						dfPrimeiroVenc.setReadOnly(true);
						txtValorPrimeiraParc.setReadOnly(true);
						
						
						btSalvar.focus();
					}else{					
						cbFormaPgto.focus();
					}
					
					double t_pagando = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(total_pagando));
					double t_valor = Real.formatStringToDBDouble(new DecimalFormat("0.00").format(subTotalPedido));
					int total_form = totais_forma_pgto.size();
					
					
					if(total_form == 1 && t_pagando == t_valor){
						
						FormasPgto form = FormaPgtoDAO.find(formaPgtoSelecionada.getId());
						if(form.getDesc_max() > 0){
							txtDesc.setEnabled(true); 
						}else{
							txtDesc.setEnabled(false);							
						}						
					}else{
						txtDesc.setEnabled(false); 
					}
				//}
			}else{
				if(formaPgtoSelecionada.getN_parcelas() > 0){
					if(!txtParcelamento.isValid() || txtParcelamento.getValue() == null || txtParcelamento.getValue().equals("") || txtParcelamento.getValue().isEmpty() || txtParcelamento.getValue().equals("0")){
						txtParcelamento.addStyleName("invalid-txt");
					}else{
						txtParcelamento.removeStyleName("invalid-txt");
					}
				}else{
					txtParcelamento.removeStyleName("invalid-txt");
				}
			}
		}else{
			if(!allowFormaPgto()){
				Notify.Show("Esta forma de pagamento já foi adicionada", Notify.TYPE_WARNING);
			}
		}
	}
	

	public void addListerner(FormaPgtoListerner target){
		try {
			Method method = FormaPgtoListerner.class.getDeclaredMethod("onClose", FormaPgtoEvent.class);
			addListener(FormaPgtoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(FormaPgtoListerner target){
		removeListener(FormaPgtoEvent.class, target);
	}
	public static class FormaPgtoEvent extends Event{
				
		private boolean confirm;
		private EcfPreVendaCabecalho pedido;
		private Integer qtd_parc;
		private String  tipoTitulo;
		private Date dataPrimVenc;
		private double valorPrimeiraParc;
		private HashMap<Integer, TotaisHash> totais_forma_pgto = new HashMap<>();
		private HaverCab haverSelecionado;
		private double haver_valor;
		private HashMap<Integer, Integer> parc_forma_pgto = new HashMap<>();
		
		public FormaPgtoEvent(Component source, EcfPreVendaCabecalho pedido, Integer qtd_parc,Date dataPrimVenc, double valorPrimeiraParc,HashMap<Integer, TotaisHash> totais_forma_pgto, HaverCab haverSelecionado,double haver_valor, 
				HashMap<Integer, Integer> parc_forma_pgto,boolean confirm) {
			super(source);
		
			this.pedido = pedido;
			this.confirm = confirm;			
			this.qtd_parc = qtd_parc;
			this.dataPrimVenc = dataPrimVenc;
			this.valorPrimeiraParc = valorPrimeiraParc;
			this.tipoTitulo = tipoTitulo;
			this.totais_forma_pgto = totais_forma_pgto;
			this.haverSelecionado = haverSelecionado;
			this.haver_valor = haver_valor;
			this.parc_forma_pgto = parc_forma_pgto;
		}
		
		public HashMap<Integer, TotaisHash> getTotaisFormaPgto(){
			return totais_forma_pgto;
		}
		public HaverCab getHaver(){
			return haverSelecionado;
		}
		public double getHaverValor(){
			return haver_valor;
		}
		public HashMap<Integer, Integer> getParcFormaPgto(){
			return parc_forma_pgto;
		} 
		public String getTipoTitulo(){
			return tipoTitulo;
		}
		public Date getDataPrimVenc(){
			return dataPrimVenc;
		}
		public Integer getQtdParc(){
			return qtd_parc;
		}
		public EcfPreVendaCabecalho getPedido(){
			return pedido;
		}
		public boolean isConfirm() {
			return confirm;
		}		
		public double getValorPrimeiraParc(){
			return valorPrimeiraParc;
		}
		
	}
	public interface FormaPgtoListerner extends Serializable{
		public void onClose(FormaPgtoEvent event);
	}

	
}
