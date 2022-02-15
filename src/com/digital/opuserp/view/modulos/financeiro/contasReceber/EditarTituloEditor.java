package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.CreditoClienteDAO;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.comp.RealTextField.RealTextField;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
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

public class EditarTituloEditor extends Window implements GenericEditor {

	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	
	RealTextField tfValorTitulo; //
	RealTextField tfValorPagamento;
	TextField tfNDoc; //
	DateField dfVencimento; //
	DateField dfPagamento;
	TextField tfCliente;
	
	
	String nomeRazao;
	String valorTitulo;
	String valorPagamento;
	String nDoc; 
	Date dataVencimento;
	Date dataPagamento;
	
	boolean data_venc_valid = false;
	boolean valor_valid = true;
	
	boolean nDoc_valid = true;
	
	
	String controle;
	Integer codCliente;
	
	ComboBox cbControle;
	
	public EditarTituloEditor(String title, boolean modal, String nomeRazao, String valorTitulo,String valorPagamento, String nDoc, 
			Date dataVencimento, Date dataPagamento, String controle,Integer codCliente){
		
		this.nomeRazao = nomeRazao;
		this.valorTitulo = valorTitulo;
		this.valorPagamento = valorPagamento;
		this.nDoc = nDoc;
		this.dataVencimento = dataVencimento;
		this.dataPagamento = dataPagamento;	
		this.controle = controle;
		this.codCliente = codCliente;
		
		setWidth("583px");
		setHeight("337px");
		
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
	
	
	public void buildLayout(){
		
		
			vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					
					tfCliente = new TextField("Cliente");					
					tfCliente.setStyleName("caption-align-editar-titulo");					
					tfCliente.setWidth("100%");
					tfCliente.setValue(nomeRazao);
					tfCliente.setRequired(true);
					tfCliente.setReadOnly(true);	
					
					addComponent(tfCliente);
					
				}
			});
			
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);					
						
						JPAContainer<ControleTitulo> containerControle = JPAContainerFactory.makeReadOnly(ControleTitulo.class,ConnUtil.getEntity());
						containerControle.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
						
						cbControle = new ComboBox("Controle",containerControle);
						cbControle.setStyleName("caption-align-editar-titulo");
						cbControle.setImmediate(true);
						cbControle.setWidth("200px");
						cbControle.setRequired(true);						
						cbControle.setNullSelectionAllowed(false);
						cbControle.setItemCaptionPropertyId("nome");
						
//						cbControle.addItem("ACESSO-POS");
//						cbControle.addItem("ACESSO-PRE");
//						cbControle.addItem("ASSISTENCIA");
//						cbControle.addItem("ALUGUEL");
//						cbControle.addItem("PRODUTO");
//						cbControle.addItem("SERVICO");
//						cbControle.addItem("HAVER");
						
						
						addComponent(cbControle);		
						
						
						for (Object o: cbControle.getItemIds()) {

							if(cbControle.getItem(o).getItemProperty("nome").getValue().toString().equals(controle)){
								cbControle.select(o);	
							}
						}
						
					}
			});
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);						
						
						tfNDoc = new TextField("Nº Documento");
						tfNDoc.setStyleName("caption-align-editar-titulo");
						tfNDoc.setImmediate(true);
						tfNDoc.setRequired(true);
						tfNDoc.setValue(nDoc);		
						
						tfNDoc.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								if(!event.getText().equals(nDoc)){
								
									if(!ContasReceberDAO.allowNdocManual(event.getText()) || ContasReceberDAO.isExistNDoc(event.getText())){
										event.getComponent().addStyleName("invalid-txt");
										nDoc_valid = false;
									}else{
										event.getComponent().removeStyleName("invalid-txt");
										nDoc_valid = true;
									}								
								}else{
									event.getComponent().removeStyleName("invalid-txt");
									nDoc_valid = true;
								}
							}
						});
						
						addComponent(tfNDoc);
						
					}
			});
			
			
			vlRoot.addComponent(
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
						dfVencimento.setValue(dataVencimento);
						
//						Calendar data = Calendar.getInstance(); 
//						data.setTime(new Date());
//						data.add(Calendar.DAY_OF_MONTH, -1);	
//						Date dtOnten = data.getTime();
//
//						if(dfVencimento.getValue() == null || dfVencimento.getValue().equals("")){
//							data_venc_valid = false;
//								
//						}else if(dtOnten.compareTo(dfVencimento.getValue()) > 0){	
//							Notification.show("Não é Possivel Gerar um Boleto Vencido!");
//							data_venc_valid = false;
//							
//						}else{
//							data_venc_valid = true;
//						}
						
//						dfVencimento.addBlurListener(new FieldEvents.BlurListener() {
//							
//							@Override
//							public void blur(BlurEvent event) {
//								Calendar data = Calendar.getInstance(); 
//								data.setTime(new Date());
//								data.add(Calendar.DAY_OF_MONTH, -1);	
//								Date dtOnten = data.getTime();
//
//								if(dfVencimento.getValue() == null || dfVencimento.getValue().equals("")){
//									data_venc_valid = false;
//										
//								}else if(dtOnten.compareTo(dfVencimento.getValue()) > 0){	
//									Notification.show("Não é Possivel Gerar um Boleto Vencido!");
//									data_venc_valid = false;
//									
//								}else{
//									data_venc_valid = true;
//								}
//							}
//						});
						
						addComponent(dfVencimento);
						
					}
			});
			
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);						
						
						dfPagamento = new DateField("Data Pagamento");
						dfPagamento.setStyleName("caption-align-editar-titulo");
						dfPagamento.setImmediate(true);
						dfPagamento.setDateFormat("dd/MM/yyyy");
						dfPagamento.setRequired(true);
						
						if(dataPagamento != null){
							dfPagamento.setEnabled(true);
							dfPagamento.setRequired(true);
							dfPagamento.setValue(dataPagamento);
						}else{
							dfPagamento.setValue(null);
							dfPagamento.setEnabled(false);
							dfPagamento.setRequired(false);
						}
						
						addComponent(dfPagamento);
						
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
						tfValorTitulo.setWidth("80px");
						tfValorTitulo.setRequired(true);
						tfValorTitulo.setValue(valorTitulo);
						tfValorTitulo.setId("txtValorTitulo");
						tfValorTitulo.addStyleName("align-currency");
						tfValorTitulo.focus();
						
						tfValorTitulo.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								if(event.getText() != null && !event.getText().isEmpty() && !event.getText().equals("") && codCliente != null){
									
									CreditoClienteDAO creditoDAO = new CreditoClienteDAO();
									String saldoDisponivel = creditoDAO.getSaldo(codCliente);
									
									if(saldoDisponivel != null){
										Double saldo = Double.parseDouble(saldoDisponivel);
										Double valorBoleto = Double.parseDouble(Real.formatStringToDB(event.getText()));
										Double ValorAtual = Double.parseDouble(Real.formatStringToDB(valorTitulo));
										   
										   if(ValorAtual < valorBoleto){
											   Double acrescimo = valorBoleto - ValorAtual;
											   
											   if(saldo < acrescimo){
												   Notification.show("Saldo Insuficiente!");
												   event.getComponent().addStyleName("invalid-txt");												   
												   valor_valid = false;													   
											   }else{
												   creditoDAO.setSaldo(codCliente,acrescimo);											   
												   valor_valid = true;													   
											   }
										   }else if (ValorAtual > valorBoleto){
											    Double extorno = ValorAtual - valorBoleto;
											    creditoDAO.setSaldoEditor(codCliente, extorno);
												event.getComponent().removeStyleName("invalid-txt");
												valor_valid = true;
											}else{
												event.getComponent().removeStyleName("invalid-txt");
												valor_valid = true;
										}										
									}									
								}
							}
						});
						
						
						addComponent(tfValorTitulo);			
						JavaScript.getCurrent().execute("$('#txtValorTitulo').maskMoney({decimal:',',thousands:'.'})");
						
					}
			});
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);						
						
						tfValorPagamento = new RealTextField("Valor Pagamento (R$)");
						tfValorPagamento.setStyleName("caption-align-editar-titulo");
						tfValorPagamento.setImmediate(true);
						tfValorPagamento.setWidth("80px");
						tfValorPagamento.setRequired(true);
						tfValorPagamento.setId("txtValorPagamento");
						tfValorPagamento.addStyleName("align-currency");
						
						if(valorPagamento != null && !valorPagamento.isEmpty() && valorPagamento != "0,00"){
							tfValorPagamento.setEnabled(true);
							tfValorPagamento.setValue(valorPagamento);
						}else{
							tfValorPagamento.setValue("");
							tfValorPagamento.setEnabled(false);
						}
						
						addComponent(tfValorPagamento);		
						JavaScript.getCurrent().execute("$('#txtValorPagamento').maskMoney({decimal:',',thousands:'.'})");
						
					}
			});
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
						
					if(tfValorTitulo.getValue() != null && tfNDoc.getValue() != null && !tfNDoc.getValue().equals("") && 
							dfVencimento.getValue() != null && cbControle.getValue()!= null && nDoc_valid){
						
						fireEvent(new EditarTituloEvent(event.getComponent().getParent(), true, tfValorTitulo.getValue(), tfValorPagamento.getValue(), 
								tfNDoc.getValue(), dfVencimento.getValue(), dfPagamento.getValue(), cbControle.getItem(cbControle.getValue()).getItemProperty("nome").getValue().toString()));
						Notification.show("Título Editado Com Sucesso!");
						close();
					}else{
						
						if(!tfValorTitulo.isValid()){
							tfValorTitulo.addStyleName("invalid-txt");
						}else{
							tfValorTitulo.removeStyleName("invalid-txt");
						}
						if(!tfNDoc.isValid() || !nDoc_valid){
							tfNDoc.addStyleName("invalid-txt");
						}else{
							tfNDoc.removeStyleName("invalid-txt");
						}
						if(!dfVencimento.isValid() && data_venc_valid){
							dfVencimento.addStyleName("invalid-txt");
						}else{
							dfVencimento.removeStyleName("invalid-txt");
						}
						if(!cbControle.isValid()){
							cbControle.addStyleName("invalid-txt");
						}else{
							cbControle.removeStyleName("invalid-txt");
						}
						
						
						Notification.show("Não é Possivel Editar o Título, Verifique se todos os Campos estão Preenchidos!");
					}					
		
				
			}
		});
		btSalvar.addStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("Salvar", ShortcutAction.KeyCode.ENTER, null) {
			
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
	
	public void addListerner(EditarTituloListerner target){
		try {
			Method method = EditarTituloListerner.class.getDeclaredMethod("onClose", EditarTituloEvent.class);
			addListener(EditarTituloEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(EditarTituloEvent.class, target);
	}
	public static class EditarTituloEvent extends Event{
		
		
		private String valorTitulo;
		private String valorPagamento;
		private String nDoc;
		private Date dataVenciemento;
		private Date dataPagamento;
		private String controle;
				
		private boolean confirm;
		
		public EditarTituloEvent(Component source, boolean confirm, String valorTitulo,	String valorPagamento,String nDoc,Date dataVenciemento,
				Date dataPagamento, String controle) {
			super(source);
			
			this.confirm = confirm;		
			
			this.valorTitulo = valorTitulo;
			this.valorPagamento = valorPagamento;
			this.nDoc = nDoc;
			this.dataVenciemento = dataVenciemento;
			this.dataPagamento = dataPagamento;
			this.controle = controle;
			
		}
		
		public boolean isConfirm() {
			return confirm;
		}
		
		public String getControle(){
			return controle;
		}

		public String getValorTitulo() {
			return valorTitulo;
		}

		public String getValorPagamento() {
			return valorPagamento;
		}

		public String getnDoc() {
			return nDoc;
		}

		public Date getDataVenciemento() {
			return dataVenciemento;
		}

		public Date getDataPagamento() {
			return dataPagamento;
		}

		
		
		
	}
	public interface EditarTituloListerner extends Serializable{
		public void onClose(EditarTituloEvent event);
	}
	

}
