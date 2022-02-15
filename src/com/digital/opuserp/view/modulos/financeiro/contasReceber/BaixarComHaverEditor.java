package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.HaverDAO;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.domain.ParametrosBoleto;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.comp.RealTextField.RealTextField;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.modulos.financeiro.haver.QueryModifierCustom;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class BaixarComHaverEditor extends Window {
	
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	TextField tfValorRecebido;
	TextField tfValorRecebido2;
	TextField tfHaverDisponivel;
	TextField tfHaverDisponivel2;
	
	Integer codContaReceber;
	Date dataVencimento;
	String valorTitulo;
	TextField txtSaldoRestante;
	TextField tfValorTroco2;
	TextField tfTotalRecebido;
	
	Label lbJuros = new Label();
	Label lbMulta = new Label();
	Label lbAtualizacaoMonetaria = new Label();
	Cliente c;
	
	private TextField txtValor;	
	private HaverCab haver;
	private TextField tfDesconto;	
	private PlanoAcesso plano;
	
	ContaBancaria contaBancaria;
	
	public BaixarComHaverEditor(String title, boolean modal, Integer codContaReceber, Date dataVencimento,String valorTitulo, Cliente c){
		
		this.codContaReceber = codContaReceber;
		this.dataVencimento = dataVencimento;
		this.valorTitulo = valorTitulo;
		this.c = c;
		ContasReceber cr = ContasReceberDAO.find(codContaReceber);
		contaBancaria = getContaBancaria();	
		try{
			plano = PlanoAcessoDAO.find(cr.getPlano_contrato());
		}catch(Exception e){
			
		}
		
		setWidth("510px");
		setHeight("448px");
		
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
		//tfValorTotalPagar.setSelectionRange(0, tfValorTotalPagar.getValue().length());
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
	
	TextField tfDiasEmAtraso;
	TextField tfJuros;
	TextField tfMulta;
	TextField tfValorTitulo;
	TextField tfTaxa;
	private TextField txtTaxaNegativacao;
	private TextField txtValorAtualizacaoMonetaria;

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
								
								double vlr_desconto_calc = Real.formatStringToDBDouble(plano.getDesconto());
								double vlr_boleto_calc = Real.formatStringToDBDouble(valorTitulo);
								double vlr_plano_calc = Real.formatStringToDBDouble(plano.getValor());
								
								double perc_vlr_boleto = (vlr_boleto_calc / vlr_plano_calc) * 100;
								double vlr_desconto_pro_rata = (vlr_desconto_calc * perc_vlr_boleto) / 100;
								
								tfDesconto.setValue(Real.formatDbToString(String.valueOf(vlr_desconto_pro_rata)));
								
							}else{
								tfDesconto.setValue( plano.getDesconto());
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
			
			vlRoot.addComponent(new HorizontalLayout(){					
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
			
			//taxa negativação, atualização monetaria
			
			vlRoot.addComponent(new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						txtValor = new RealTextField("Valor (R$)");
						txtValor.setStyleName("caption-align-contas-baixa-haver");
						txtValor.addStyleName("align-currency");
						txtValor.setId("txtValor");
						txtValor.setImmediate(true);
						txtValor.setRequired(true);
						txtValor.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {								
							
								
								
								if(event.getText() != null && !event.getText().isEmpty() && !event.getText().equals("")){
									double valor_pagamento =  Real.formatStringToDBDouble(event.getText());
									double haver_disponivel = Real.formatStringToDBDouble(tfHaverDisponivel.getValue());
									double saldo_restante= haver_disponivel - valor_pagamento;
									txtSaldoRestante.setReadOnly(false);
									txtSaldoRestante.setValue(Real.formatDbToString(String.valueOf(saldo_restante)));
									txtSaldoRestante.setReadOnly(true);
								}
							}
						});
						
						addComponent(txtValor);
						
						JavaScript.getCurrent().execute("$('#txtValor').maskMoney({decimal:',',thousands:'.'})");						
					}
				});
			
			
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);

						tfHaverDisponivel = new TextField("Haver Disponível (R$)");
						tfHaverDisponivel.setStyleName("caption-align-contas-baixa-haver");
						tfHaverDisponivel.addStyleName("align-currency");
						tfHaverDisponivel.setId("tfHaverDisponivel");
						tfHaverDisponivel.setReadOnly(true); 						
						
						addComponent(tfHaverDisponivel);
						
						JavaScript.getCurrent().execute("$('#tfHaverDisponivel').maskMoney({decimal:',',thousands:'.'})");						
					}
			});
			
		
			
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						txtSaldoRestante = new TextField("Saldo Restante (R$)");
						txtSaldoRestante.setStyleName("caption-align-contas-baixa-haver");
						txtSaldoRestante.addStyleName("align-currency");
						txtSaldoRestante.setId("txtValorTroco");
						txtSaldoRestante.setReadOnly(true);	
						
						addComponent(txtSaldoRestante);
						JavaScript.getCurrent().execute("$('#txtValorTroco').maskMoney({decimal:',',thousands:'.'})");					
					}
				});
			
			
			calcularAtraso();
			buscarHaver();
	}
	private void buscarHaver(){
		haver = HaverDAO.buscarHaver(c);
		
		if(haver != null && haver.getValor_total() > 0){
			tfHaverDisponivel.setReadOnly(false);
			tfHaverDisponivel.setValue(Real.formatDbToString(String.valueOf(haver.getValor_total())));
			tfHaverDisponivel.setReadOnly(true);
		}else{
			tfHaverDisponivel.setReadOnly(false);
			tfHaverDisponivel.setValue(Real.formatDbToString(String.valueOf("0.00")));
			tfHaverDisponivel.setReadOnly(true);
		}
		
		double valor_total_pagar = Real.formatStringToDBDouble(txtValor.getValue());
		double valor_disponivel =  Real.formatStringToDBDouble(tfHaverDisponivel.getValue());
		
		txtSaldoRestante.setReadOnly(false);
		txtSaldoRestante.setValue(Real.formatDbToString(String.valueOf(valor_disponivel-valor_total_pagar)));
		txtSaldoRestante.setReadOnly(true);
	}
	
	private JPAContainer<Haver> containerHaver;
	private JPAContainer<Haver> buildContainerHaver(){
		containerHaver = JPAContainerFactory.makeBatchable(Haver.class, ConnUtil.getEntity());
		containerHaver.setQueryModifierDelegate(new QueryModifierCustom());
		containerHaver.addContainerFilter(Filters.eq("cliente.doc_cpf_cnpj", c.getDoc_cpf_cnpj()));
		containerHaver.addContainerFilter(Filters.gt("valor_disponivel", new Double("0.00")));
		
		return containerHaver;
	}

	double valor_a_ser_pago = 0;
	private void calcularAtraso(){
		
		
			EntityManager em = ConnUtil.getEntity();
			
			
			ContasReceber boleto = ContasReceberDAO.find(codContaReceber);
			String controle =  boleto.getControle();
			Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
			qControle.setParameter("nome", controle);
			qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
											
			ContaBancaria cb = null;
			if(qControle.getResultList().size() ==1){
				cb = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
			
				ContaBancaria contaBancaria = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
				
				DateTime dt1 = new DateTime(dataVencimento);
				DateTime dt2 = new DateTime(new Date());
		 
				Integer dias = Days.daysBetween(dt1, dt2).getDays();
				Integer meses = Months.monthsBetween(dt1, dt2).getMonths();
				
				Double juros;
				Double multa;
				double taxa_negativacao;
				double  atualizacao_monetaria;
				
				Double jurosTotais;
				if(dias > 5)
				{
					juros = ((Double.parseDouble(contaBancaria.getJuros()) * dias));
					multa = Double.parseDouble(contaBancaria.getMulta());
				}else{
					juros = 0.00;
					multa = 0.00;
				}
				
				if(dias > 364){
					taxa_negativacao = cb.getTaxa_negativacao();
					double taxa_monetaria = cb.getAtualizacao_monetaria() * meses;
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
				tfDiasEmAtraso.setValue(dias.toString());
				tfDiasEmAtraso.setReadOnly(true);
				
				tfJuros.setReadOnly(false);
				
				Double jurosFinal = juros;
				Double vlrJurosArredondado = vlrJuros;
				tfJuros.setValue(Real.formatDbToString(vlrJurosArredondado.toString()));
				tfJuros.setReadOnly(true);
				
				tfMulta.setReadOnly(false);
				tfMulta.setValue(Real.formatDbToString(vlrMulta.toString()));
				tfMulta.setReadOnly(true);		
				
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
				if(!cobrarTaxa){
					tfTaxa.setReadOnly(false);
					tfTaxa.setValue("0,00");
					tfTaxa.setReadOnly(true);				
				}
				
				Double valorFinal = Double.parseDouble(Real.formatStringToDB(valorTitulo)) + 
									Real.formatStringToDBDouble(tfTaxa.getValue()) + 	vlrJuros1 + vlrMulta1 + taxa_negativacao + atualizacao_monetaria;
								
				Double desconto = tfDesconto.getValue() != null && !tfDesconto.getValue().equals("") && !tfDesconto.getValue().equals("0,00") ? Real.formatStringToDBDouble(tfDesconto.getValue()) : 0;
				Double valorFinal1 = valorFinal-desconto;
				
				txtValor.setValue(Real.formatDbToString(valorFinal1.toString()));				
				
				txtTaxaNegativacao.setReadOnly(false);
				txtTaxaNegativacao.setValue(Real.formatDbToString(String.valueOf(taxa_negativacao)));
				txtTaxaNegativacao.setReadOnly(true);
								
				txtValorAtualizacaoMonetaria.setReadOnly(false);
				txtValorAtualizacaoMonetaria.setValue(Real.formatDbToString(String.valueOf(atualizacao_monetaria)));
				txtValorAtualizacaoMonetaria.setReadOnly(true);
				
				lbJuros.setValue(Real.formatDbToString(contaBancaria.getJuros())+"% ao dia");
				lbMulta.setValue(Real.formatDbToString(contaBancaria.getMulta())+"%");
				lbAtualizacaoMonetaria.setValue(Real.formatDbToString(String.valueOf(contaBancaria.getAtualizacao_monetaria()))+"% ao mês");
								
				valor_a_ser_pago = valorFinal1;
			}
				
				
				//DESCONTO
				//-----------------------------------------
		
				
				
				
				
			
			
			
	}
	
	public double calcularPercentual(double valor, double porcentagem) {  
	    double valorFinal = valor * porcentagem / 100.0;  
	    return valorFinal;  
	}
	
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				boolean haver_disponivel = true;
				if(Real.formatStringToDBDouble(txtValor.getValue()) > haver.getValor_total()){		
					haver_disponivel = false;
				}
				
				if(haver_disponivel){				
					//haver.setValor_total(haver.getValor_total() - Real.formatStringToDBDouble(txtValor.getValue()));
//					if(haver.getValor_total() <= 0){
//						haver.setStatus("UTILIZADO");
//					}
					fireEvent(new BaixarTituloHaverEvent(event.getComponent().getParent(), true,haver, txtValor.getValue()));							
					close();					
				}else{
					Notify.Show("O Valor informado precisa ser menor do que o limite disponível no Haver", Notify.TYPE_ERROR);
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

	public static double converterDoubleDoisDecimais(double precoDouble) {  
	    DecimalFormat fmt = new DecimalFormat("0.00");       
	    String string = fmt.format(precoDouble);  
	    String[] part = string.split("[,]");  
	    String string2 = part[0]+"."+part[1];  
	        double preco = Double.parseDouble(string2);  
	    return preco;  
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
	
	public void addListerner(BaixarTituloHaverListerner target){
		try {
			Method method = BaixarTituloHaverListerner.class.getDeclaredMethod("onClose", BaixarTituloHaverEvent.class);
			addListener(BaixarTituloHaverEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(BaixarTituloHaverEvent.class, target);
	}
	public static class BaixarTituloHaverEvent extends Event{
		
			
		private String valor_haver;
		private HaverCab haver;		
		private boolean confirm;
		
		public BaixarTituloHaverEvent(Component source, boolean confirm, HaverCab haver, String valor_haver) {
			super(source);
			this.valor_haver = valor_haver;
			this.haver = haver;
			this.confirm = confirm;	
		}
		
		public boolean isConfirm() {
			return confirm;
		}
		public HaverCab getHaver() {
			return haver;
		}
		public String getValorHaver(){
			return valor_haver;
		}
				
	}
	public interface BaixarTituloHaverListerner extends Serializable{
		public void onClose(BaixarTituloHaverEvent event);
	}
	

}
