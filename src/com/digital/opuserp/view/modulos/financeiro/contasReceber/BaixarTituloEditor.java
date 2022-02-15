package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.ParametrosBoleto;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.comp.RealTextField.RealTextField;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class BaixarTituloEditor extends Window {
	
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	TextField tfValorRecebido;
	
	Integer codContaReceber;
	Date dataVencimento;
	String valorTitulo;
	TextField tfValorTroco;
	
	
	Label lbJuros = new Label();
	Label lbMulta = new Label();
	Label lbAtualizacaoMonetaria = new Label();
	
	//AcessoCliente contrato;
	PlanoAcesso plano;
	ContaBancaria contaBancaria;
	
	boolean allow_recebido;
	
	String parcela;
	boolean adiantado;
	ContasReceber boleto;
	
	public BaixarTituloEditor(String title, boolean modal, Integer codContaReceber, Date dataVencimento,String valorTitulo){
		
		this.codContaReceber = codContaReceber;
		this.dataVencimento = dataVencimento;
		this.valorTitulo = valorTitulo;
		
		ContasReceber cr = ContasReceberDAO.find(codContaReceber);
		boleto = cr;
		adiantado = ContasReceberDAO.checkBoletoAdiantadoPorContrato(cr);
		
		contaBancaria = getContaBancaria();	
		String codContrato = cr.getN_doc().split("/")[0].toString();
		parcela = cr.getN_doc().split("/").length > 1 && cr.getN_doc().split("/")[1].toString().split("-").length > 1? cr.getN_doc().split("/")[1].toString().split("-")[1].toString() : "1";
		
		try{			
			plano = PlanoAcessoDAO.find(cr.getPlano_contrato());
		}catch(Exception e){
			
		}
		
		
		setWidth("498px");
		setHeight("471px");
		
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
		tfValorPagmento.setSelectionRange(0, tfValorPagmento.getValue().length());
	}
	
	
	TextField tfDiasEmAtraso;
	TextField tfJuros;
	TextField tfMulta;
	TextField tfValorTitulo;
	TextField tfTaxa;
	TextField tfDesconto;
	
	RealTextField tfValorPagmento;
	ComboBox cbFormaPagamento;
	
	private TextField txtValorAtualizacaoMonetaria;
	private TextField txtTaxaNegativacao;
	
	
	public void buildLayout(){
		
		
		 vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					tfValorTitulo = new TextField("Valor Título (R$)");
					tfValorTitulo.setStyleName("caption-align-contas-baixa-haver");
					tfValorTitulo.setValue(valorTitulo);
					tfValorTitulo.setReadOnly(true);
					tfValorTitulo.addStyleName("align-currency");
					
										
					addComponent(tfValorTitulo);
					
				}
			});
		
		  vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					tfTaxa = new TextField("Taxa (R$)");
					tfTaxa.setStyleName("caption-align-contas-baixa-haver");
					tfTaxa.addStyleName("align-currency");
					if(plano != null ){
						
						if(plano.getTaxa_boleto().equals("SIM")){
							tfTaxa.setValue(Real.formatDbToString(contaBancaria.getTaxa_boleto()));
						}else{							
							tfTaxa.setValue("0,00");							
						}
					}else{
						tfTaxa.setValue(Real.formatDbToString(contaBancaria.getTaxa_boleto()));
					}
					tfTaxa.setReadOnly(true);
										
					addComponent(tfTaxa);
					
				}
			});
			
		 
		  vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						tfDesconto = new TextField("Desconto (R$)");
						tfDesconto.setStyleName("caption-align-contas-baixa-haver");
						tfDesconto.addStyleName("align-currency");
						if(plano != null){
							
							//parcela.equals("12") ? "0" :
							
							double vlr_boleto = Real.formatStringToDBDouble(valorTitulo);
							double vlr_plano = Real.formatStringToDBDouble(plano.getValor());
							
							if(vlr_boleto != vlr_plano && vlr_plano > 0){
								
								double vlr_desconto_calc = Real.formatStringToDBDouble(plano.getDesconto());//----> DESCONTO PLANO -> 5,00
								double vlr_boleto_calc = Real.formatStringToDBDouble(valorTitulo);//----> VALOR BOLETO -> 104,90
								double vlr_plano_calc = Real.formatStringToDBDouble(plano.getValor());//--->  VALOR PLANO -> 74,90
								
								double perc_vlr_boleto = (vlr_boleto_calc / vlr_plano_calc) * 100;//140,05
								double vlr_desconto_pro_rata = (vlr_desconto_calc * perc_vlr_boleto) / 100;
								

								if(boleto.getQtdDiasProRata() != null){
									vlr_desconto_pro_rata = (vlr_desconto_pro_rata / 30) * boleto.getQtdDiasProRata();
								}
								
								
								
								tfDesconto.setValue(Real.formatDbToString(String.valueOf(vlr_desconto_pro_rata)));
								
								
								
							}else{
								double vlr_desconto_pro_rata = Real.formatStringToDBDouble(plano.getDesconto());
								
								if(boleto.getQtdDiasProRata() != null){
									vlr_desconto_pro_rata = (vlr_desconto_pro_rata / 30) * boleto.getQtdDiasProRata();
								}
								
								tfDesconto.setValue( Real.formatDbToString(String.valueOf(vlr_desconto_pro_rata)));
							}
						}
						tfDesconto.setReadOnly(true);
											
						addComponent(tfDesconto);
						
					}
				});
				
		
			vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					tfDiasEmAtraso = new TextField("Dias em Atraso");
					tfDiasEmAtraso.setStyleName("caption-align-contas-baixa-haver");
					tfDiasEmAtraso.setReadOnly(true);
					tfDiasEmAtraso.addStyleName("align-currency");
										
					addComponent(tfDiasEmAtraso);
					
				}
			});
			
			vlRoot.addComponent(
					new HorizontalLayout(){					
					{
						addComponent(
							new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
									
								tfJuros = new TextField("Juros (R$)");
								tfJuros.setStyleName("caption-align-contas-baixa-haver");
								tfJuros.setReadOnly(true);
								tfJuros.addStyleName("align-currency");
																				
								addComponent(tfJuros);
											
									
							}
						});
						
						addComponent(lbJuros);
					}
			});
			
			vlRoot.addComponent(
					new HorizontalLayout(){					
					{
						addComponent(
							new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
									
								tfMulta = new TextField("Multa (R$)");
								tfMulta.setStyleName("caption-align-contas-baixa-haver");
								tfMulta.setReadOnly(true);
								tfMulta.addStyleName("align-currency");
													
								addComponent(tfMulta);
								
							}
						});
						
						addComponent(lbMulta);
					}
			});
			
			vlRoot.addComponent(
											
							new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
									
								txtTaxaNegativacao = new TextField("Taxa Negativação (R$)");
								txtTaxaNegativacao.setStyleName("caption-align-contas-baixa-haver");
								txtTaxaNegativacao.setReadOnly(true);
								txtTaxaNegativacao.addStyleName("align-currency");
													
								addComponent(txtTaxaNegativacao);
														
							}
			});
			vlRoot.addComponent(new HorizontalLayout(){					
				{
					addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
							
							txtValorAtualizacaoMonetaria = new TextField("Atualização Monetária (R$)");
							txtValorAtualizacaoMonetaria.setStyleName("caption-align-contas-baixa-haver");
							txtValorAtualizacaoMonetaria.setReadOnly(true);
							txtValorAtualizacaoMonetaria.addStyleName("align-currency");
												
							addComponent(txtValorAtualizacaoMonetaria);
							
						}
					});
					
					addComponent(lbAtualizacaoMonetaria);
				}
		});
			
			
			lbJuros.addStyleName("lbPercentFinanceiro");
			lbMulta.addStyleName("lbPercentFinanceiro");
			lbAtualizacaoMonetaria.addStyleName("lbPercentFinanceiro");
			
			
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						tfValorPagmento = new RealTextField("Total a Pagar (R$)");
						tfValorPagmento.setStyleName("caption-align-contas-baixa-haver");
						//tfValorPagmento.focus();
						
						tfValorPagmento.addStyleName("align-currency");
						tfValorPagmento.setId("txtValorPagamento");
						tfValorPagmento.setRequired(true);
						tfValorPagmento.setImmediate(true);
						tfValorPagmento.setTextChangeEventMode(TextChangeEventMode.LAZY);
						tfValorPagmento.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								if(event.getText() != null && !event.getText().isEmpty() && !event.getText().equals("") 
										&& tfValorRecebido.getValue() != null && !tfValorRecebido.getValue().equals("")){
									
									Double total = Real.formatStringToDBDouble(event.getText());
									Double valorRecebido = Real.formatStringToDBDouble(tfValorRecebido.getValue());
									
									if(total > valorRecebido){			
										tfValorRecebido.addStyleName("invalid-txt");
										allow_recebido = false;
									}else{
										double valor = valorRecebido - total;
										
										tfValorRecebido.removeStyleName("invalid-txt");
										tfValorTroco.setReadOnly(false);
										tfValorTroco.setValue(Real.formatDbToString(String.valueOf(valor)));
										tfValorTroco.setReadOnly(true);		
										allow_recebido = true;
									}
								}
							}
						});
						
						addComponent(tfValorPagmento);
						JavaScript.getCurrent().execute("$('#txtValorPagamento').maskMoney({decimal:',',thousands:'.'})");
						
						
						
					}
				});
			
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						//tfValorRecebido.focus();
						tfValorRecebido = new RealTextField("Recebido (R$)");
						tfValorRecebido.setStyleName("caption-align-contas-baixa-haver");
						tfValorRecebido.addStyleName("align-currency");
						tfValorRecebido.setId("tfValorRecebido");
						tfValorRecebido.setImmediate(true);
						tfValorRecebido.setRequired(true);
						tfValorRecebido.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								if(event.getText() != null && !event.getText().isEmpty() && !event.getText().equals("")){
									
									Double total = Real.formatStringToDBDouble(tfValorPagmento.getValue());
									Double valorRecebido = Real.formatStringToDBDouble(event.getText());
									
									if(valorRecebido <  total){			
										tfValorRecebido.addStyleName("invalid-txt");
										double valor = valorRecebido - total;
										tfValorTroco.setReadOnly(false);
										tfValorTroco.setValue(Real.formatDbToString(String.valueOf(valor)));
										tfValorTroco.setReadOnly(true);
										
										allow_recebido = false;
									}else{
										double valor = valorRecebido - total;
										
										tfValorRecebido.removeStyleName("invalid-txt");
										tfValorTroco.setReadOnly(false);
										tfValorTroco.setValue(Real.formatDbToString(String.valueOf(valor)));
										tfValorTroco.setReadOnly(true);		
										allow_recebido = true;
									}
								}
							}
						});
						
						addComponent(tfValorRecebido);
						
						JavaScript.getCurrent().execute("$('#tfValorRecebido').maskMoney({decimal:',',thousands:'.'})");
						
						
					}
				});
			
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						tfValorTroco = new RealTextField("Troco (R$)");
						tfValorTroco.setStyleName("caption-align-contas-baixa-haver");
						tfValorTroco.addStyleName("align-currency");
						tfValorTroco.setId("txtValorTroco");
						tfValorTroco.setRequired(true);
//						tfValorTroco.setEnabled(false);
						tfValorTroco.setReadOnly(true);	
						
						addComponent(tfValorTroco);
						JavaScript.getCurrent().execute("$('#txtValorTroco').maskMoney({decimal:',',thousands:'.'})");
						
						
					}
				});
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						cbFormaPagamento = new ComboBox("Forma Pgto.");
						cbFormaPagamento.setTextInputAllowed(false);
						cbFormaPagamento.setStyleName("caption-align-contas-baixa-haver");
						cbFormaPagamento.addItem("DINHEIRO");
						cbFormaPagamento.addItem("DEPOSITO");
						cbFormaPagamento.addItem("CHEQUE");
						cbFormaPagamento.addItem("CARTAO CREDITO");
						cbFormaPagamento.addItem("CARTAO DEBITO");
						cbFormaPagamento.addItem("NENHUMA");
						cbFormaPagamento.setNullSelectionAllowed(false);
						cbFormaPagamento.setRequired(true);
						cbFormaPagamento.setRequired(true);
						
						addComponent(cbFormaPagamento);
						
					}
				});		
			
			calcularAtraso();
	}
	
	private ContaBancaria getContaBancaria(){
		EntityManager em = ConnUtil.getEntity();
		
		
		String controle =  ContasReceberDAO.find(codContaReceber).getControle();
		Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
		qControle.setParameter("nome", controle);
		qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		ContaBancaria contaBancaria = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
		
		return contaBancaria;
	}

	private void calcularAtraso(){
		
			ContasReceber boleto = ContasReceberDAO.find(codContaReceber);

		    if(contaBancaria == null){
		    	contaBancaria = getContaBancaria();
		    }
			
			if(contaBancaria != null){
				
				DateTime dt1 = new DateTime(dataVencimento);
				DateTime dt2 = new DateTime(new Date());
		 
				Integer dias = Days.daysBetween(dt1, dt2).getDays();
				Integer meses = Months.monthsBetween(dt1, dt2).getMonths();
				
				Double juros;
				Double multa;
				double taxa_negativacao;
				double  atualizacao_monetaria;
				
				Double jurosTotais;
				if(dias > Integer.parseInt(contaBancaria.getTolerancia_juros_multa()))
				{
					juros = ((Double.parseDouble(contaBancaria.getJuros()) * dias));
					multa = Double.parseDouble(contaBancaria.getMulta());					
					
					tfDesconto.setReadOnly(false);
					tfDesconto.setValue("0,00");
					tfDesconto.setReadOnly(true);
				}else{
					juros = 0.00;
					multa = 0.00;
				}
				
				if(dias > 364){
					taxa_negativacao = contaBancaria.getTaxa_negativacao();
					double taxa_monetaria = contaBancaria.getAtualizacao_monetaria() * meses;
					double vlr_taxa_monetraria = Real.formatStringToDBDouble(boleto.getValor_titulo()) * taxa_monetaria / 100;					
					atualizacao_monetaria = vlr_taxa_monetraria;
					
					if(!boleto.getStatus().equals("NEGATIVADO")){
						taxa_negativacao = 0;
					}						
				}else{
					atualizacao_monetaria = 0.00;
					taxa_negativacao = 0.00;
				}
				
				jurosTotais = juros + multa;
				
				Float valorDocTotal = Float.parseFloat(Real.formatStringToDB(valorTitulo)) + Float.parseFloat(Real.formatStringToDB(contaBancaria.getTaxa_boleto()));
				Double acrescimo = jurosTotais * valorDocTotal/100;
				
				Double vlrJuros1 = Double.parseDouble(juros.toString()) * Double.parseDouble(Real.formatStringToDB(valorTitulo))/100;
				Double vlrJuros = vlrJuros1;
				
				Double vlrMulta1 = Double.parseDouble(multa.toString()) * Double.parseDouble(Real.formatStringToDB(valorTitulo))/100;				
				Double vlrMulta = vlrMulta1;
				
				Double novoValorDoc =  valorDocTotal + acrescimo;
				
				tfDiasEmAtraso.setReadOnly(false);
				tfDiasEmAtraso.setValue(dias > 0 ? dias.toString() : "0");
				tfDiasEmAtraso.setReadOnly(true);
				
				tfJuros.setReadOnly(false);
				
				Double jurosFinal = juros;
				Double vlrJurosArredondado = vlrJuros;
				tfJuros.setValue(Real.formatDbToString(vlrJurosArredondado.toString()));
				tfJuros.setReadOnly(true);
				
				tfMulta.setReadOnly(false);
				tfMulta.setValue(Real.formatDbToString(vlrMulta.toString()));
				tfMulta.setReadOnly(true);				
				
				EntityManager em = ConnUtil.getEntity();
				Query qPb = em.createQuery("select pb from ParametrosBoleto pb where pb.cliente_id = :codCliente", ParametrosBoleto.class);
				qPb.setParameter("codCliente",boleto.getCliente().getId());			
				ParametrosBoleto pb = null;
				boolean cobrarTaxa = true;
				if(qPb.getResultList().size() > 0){
					pb = (ParametrosBoleto) qPb.getSingleResult();
					
					if(!pb.getCobrar_taxa_bancaria()){
						cobrarTaxa = false;
					}
				}
				
				boolean taxBoleto =plano != null  &&  plano.getTaxa_boleto().equals("NAO")  ? false : true;		
				if(!cobrarTaxa && taxBoleto){
					taxBoleto = !taxBoleto;
				}
				
				if(!taxBoleto){
					tfTaxa.setReadOnly(false);
					tfTaxa.setValue("0,00");
					tfTaxa.setReadOnly(true);				
				}
				
				Double taxaBoleto = taxBoleto ?  Double.parseDouble(contaBancaria.getTaxa_boleto()) : 0;
				Double valorFinal = Double.parseDouble(Real.formatStringToDB(valorTitulo)) + taxaBoleto  + vlrJuros1 + vlrMulta1 + taxa_negativacao + atualizacao_monetaria;
								
				Double desconto = tfDesconto.getValue() != null && !tfDesconto.getValue().equals("") && !tfDesconto.getValue().equals("0,00") ? Real.formatStringToDBDouble(tfDesconto.getValue()) : 0;
				
				
				Double valorFinal1 = valorFinal - desconto;
				
				tfValorPagmento.setValue(Real.formatDbToString(valorFinal1.toString()));			
				
				txtTaxaNegativacao.setReadOnly(false);
				txtTaxaNegativacao.setValue(Real.formatDbToString(String.valueOf(taxa_negativacao)));
				txtTaxaNegativacao.setReadOnly(true);
								
				txtValorAtualizacaoMonetaria.setReadOnly(false);
				txtValorAtualizacaoMonetaria.setValue(Real.formatDbToString(String.valueOf(atualizacao_monetaria)));
				txtValorAtualizacaoMonetaria.setReadOnly(true);
				
				lbJuros.setValue(contaBancaria.getJuros()+"% ao dia");
				lbMulta.setValue(Real.formatDbToString(contaBancaria.getMulta())+"%");
				lbAtualizacaoMonetaria.setValue(Real.formatDbToString(String.valueOf(contaBancaria.getAtualizacao_monetaria()))+"% ao mês");

				
			}
			
			
	}
	
	public double calcularPercentual(double valor, double porcentagem) {  
	    double valorFinal = valor * porcentagem / 100.0;  
	    return valorFinal;  
	}
	
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
						
					if(tfValorPagmento.getValue() != null && !tfValorPagmento.getValue().equals("") && cbFormaPagamento.getValue() != null &&
							tfValorRecebido.getValue() != null && !tfValorRecebido.getValue().equals("") && !tfValorRecebido.getValue().isEmpty() &&
							tfValorTroco.getValue() != null && !tfValorTroco.getValue().equals("") && !tfValorTroco.getValue().isEmpty() && allow_recebido && !adiantado){
						
						fireEvent(new BaixarTituloEvent(event.getComponent().getParent(), true, cbFormaPagamento.getValue().toString(), 
								tfValorPagmento.getValue(), Real.formatStringToDBDouble(tfValorRecebido.getValue()), 
								Real.formatStringToDBDouble(tfValorTroco.getValue())));
						
						//Notification.show("Título(s) Baixado Com Sucesso!");
						close();
					}else{
						
						if(adiantado){
							Notify.Show("Não é possivel baixar boleto adiantado!", Notify.TYPE_WARNING);
						}else{							
							Notify.Show("Não é possivel baixar o título, Verifique se todos os campos estão preenchidos e são válidos!", Notify.TYPE_ERROR);
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
		
		private String valor;
		private String formaPgto;
		private Double valor_recebido;
		private Double valor_troco;
				
		private boolean confirm;
		
		public BaixarTituloEvent(Component source, boolean confirm, String formaPgto, String valor, Double valor_recebido, Double valor_troco) {
			super(source);
			
			this.confirm = confirm;		
			this.formaPgto = formaPgto;
			this.valor = valor;
			this.valor_recebido = valor_recebido;
			this.valor_troco = valor_troco;
			
		}

		
		public boolean isConfirm() {
			return confirm;
		}
		public String getValor() {
			return valor;
		}
		public String getFormaPgto() {
			return formaPgto;
		}
		public Double getValor_recebido() {
			return valor_recebido;
		}
		public Double getValor_troco() {
			return valor_troco;
		}

		
	}
	public interface BaixarTituloListerner extends Serializable{
		public void onClose(BaixarTituloEvent event);
	}
	

}
