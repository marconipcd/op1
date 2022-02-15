package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.CreditoClienteDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.comp.RealTextField.RealTextField;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.ClienteUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
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

public class NovoTituloEditor extends Window implements GenericEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	ComboBox cbCliente;
	RealTextField tfValor;
	TextField tfNDoc;
	DateField dfVencimento;
	TextField tfQAtd;
	TextField tfDescricaoCliente;
	TextField tfCodCliente;
	
	Cliente Clientelecionado;
	ComboBox cbControle;
	ComboBox cbIntervalo;
	
	boolean data_venc_valid = false;
	boolean valor_valid = true;
	boolean nDoc_valid = false;
	
	
	public NovoTituloEditor(String title, boolean modal){
		setWidth("687px");
		setHeight("355px");
		
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
		
			
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				
				tfDescricaoCliente = new TextField();
				tfCodCliente = new TextField("Cliente");				
				tfCodCliente.setWidth("75px");				
				tfCodCliente.setNullRepresentation("");
				tfCodCliente.setStyleName("caption-align-novo-titulo");
				tfCodCliente.setRequired(true);		
				tfCodCliente.focus();
				tfCodCliente.setTextChangeEventMode(TextChangeEventMode.LAZY);
				tfCodCliente.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						if ( event.getText() != null && !event.getText().isEmpty() && !event.getText().equals("")){
							ClienteDAO cDAO = new ClienteDAO();
							Cliente cGet = cDAO.getCliente(Integer.parseInt(event.getText()));
							
							if(cGet != null){
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue(cGet.getNome_razao());
								tfDescricaoCliente.setReadOnly(true);
								
								Clientelecionado = cGet;
							}else{
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
				
				
				tfDescricaoCliente.setRequired(true);
				tfDescricaoCliente.setReadOnly(true);
				tfDescricaoCliente.setWidth("305px");
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setDescription("Pesquisar Cliente");
				btSearchCliente.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						ClienteUtil cUtil = new ClienteUtil(true, true,null);
						cUtil.addListerner(new ClienteUtil.ClienteListerner() {
							
							@Override
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
								if(event.getCliente() != null){
									tfCodCliente.setValue(event.getCliente().getId().toString());
									tfDescricaoCliente.setReadOnly(false);
									tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
									tfDescricaoCliente.setReadOnly(true);
									Clientelecionado = event.getCliente();
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
												
						addComponent(tfCodCliente);							
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
					
					JPAContainer<ControleTitulo> containerControle = JPAContainerFactory.makeReadOnly(ControleTitulo.class,ConnUtil.getEntity());
					containerControle.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
					
					cbControle = new ComboBox("Controle", containerControle);
					cbControle.setStyleName("caption-align-novo-titulo");
					cbControle.setImmediate(true);
					cbControle.setWidth("200px");
					cbControle.setRequired(true);
					cbControle.setNullSelectionAllowed(false);
					cbControle.setTextInputAllowed(false);
					cbControle.setItemCaptionPropertyId("nome");
					
					addComponent(cbControle);							
				}
		});
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);						
					
					tfNDoc = new TextField("Nº Documento");
					tfNDoc.setStyleName("caption-align-novo-titulo");
					tfNDoc.setImmediate(true);
					tfNDoc.setMaxLength(40);
					tfNDoc.setRequired(true);
					
					tfNDoc.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
														
							if(!ContasReceberDAO.allowNdocManual(event.getText()) || ContasReceberDAO.isExistNDoc(event.getText())){
								event.getComponent().addStyleName("invalid-txt");
								nDoc_valid = false;
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
					
					
					
					
					dfVencimento = new DateField("Data Primeiro Vencimento");
					dfVencimento.setStyleName("caption-align-novo-titulo");
					dfVencimento.setImmediate(true);
					dfVencimento.setDateFormat("dd/MM/yyyy");
					dfVencimento.setRequired(true);
					dfVencimento.addBlurListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
							Calendar data = Calendar.getInstance(); 
							data.setTime(new Date());
							data.add(Calendar.DAY_OF_MONTH, -1);	
							Date dtOnten = data.getTime();

							if(dfVencimento.getValue() == null || dfVencimento.getValue().equals("")){
								data_venc_valid = false;
									
							}else if(dtOnten.compareTo(dfVencimento.getValue()) > 0){	
								Notification.show("Não é Possivel Gerar um Boleto Vencido!");
								data_venc_valid = false;
								
							}else{
								data_venc_valid = true;
							}
						}
					});
					
					addComponent(dfVencimento);
					
				}
		});
				
		
		
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);						
						
						tfValor = new RealTextField("Valor (R$)");
						tfValor.setStyleName("caption-align-novo-titulo");
						tfValor.setImmediate(true);
						tfValor.setWidth("80px");
						tfValor.setRequired(true);
						tfValor.addStyleName("align-currency");
						tfValor.setId("txtValor");
						
						tfValor.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								if(event.getText() != null && !event.getText().isEmpty() && !event.getText().equals("") && 
									tfCodCliente.getValue() != null && !tfCodCliente.getValue().isEmpty() && !tfCodCliente.getValue().equals("")){
									
									CreditoClienteDAO creditoDAO = new CreditoClienteDAO();
									String saldoDisponivel = creditoDAO.getSaldo(Integer.parseInt(tfCodCliente.getValue()));
									
									if(saldoDisponivel != null){
										Double saldo = Double.parseDouble(saldoDisponivel);
										Double valorBoleto = Double.parseDouble(Real.formatStringToDB(event.getText()));
										
										
										if(tfQAtd != null && tfQAtd.getValue() != null && !tfQAtd.getValue().isEmpty() && !tfQAtd.getValue().equals("")){
											if(valorBoleto * Integer.parseInt(tfQAtd.getValue()) > saldo){
												event.getComponent().addStyleName("invalid-txt");
												valor_valid = false;
											}else{
												event.getComponent().removeStyleName("invalid-txt");
												valor_valid = true;
											}
										}else{
											if(valorBoleto > saldo){
												event.getComponent().addStyleName("invalid-txt");
												valor_valid = false;
											}else{
												event.getComponent().removeStyleName("invalid-txt");
												valor_valid = true;
											}
										}
										
									}									
								}
							}
						});
						
						
						
						addComponent(tfValor);
						JavaScript.getCurrent().execute("$('#txtValor').maskMoney({decimal:',',thousands:'.'})");
						
					}
			});
			
			
			
			
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);						
												
						tfQAtd = new TextField("Qtd.:");
						tfQAtd.setStyleName("caption-align-novo-titulo");
						tfQAtd.setImmediate(true);
						tfQAtd.setWidth("40px");		
						tfQAtd.setRequired(true);
						tfQAtd.setId("txtQtd");
						
						JavaScript.getCurrent().execute("$('#txtQtd').mask('00')");
						
						tfQAtd.addTextChangeListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								
								if(event.getText() != null && !event.getText().isEmpty() && !event.getText().equals("") && 
										tfCodCliente.getValue() != null && !tfCodCliente.getValue().isEmpty() && !tfCodCliente.getValue().equals("") &&
										tfValor.getValue() != null && !tfValor.getValue().isEmpty() && !tfValor.getValue().equals("")){
										
										CreditoClienteDAO creditoDAO = new CreditoClienteDAO();
										String saldoDisponivel = creditoDAO.getSaldo(Integer.parseInt(tfCodCliente.getValue()));
										
										if(saldoDisponivel != null){
											Double saldo = Double.parseDouble(saldoDisponivel);
											Double valorBoleto = Double.parseDouble(Real.formatStringToDB(tfValor.getValue()));
																	
											if(valorBoleto * Integer.parseInt(event.getText()) > saldo){
												tfValor.addStyleName("invalid-txt");
												valor_valid = false;
											}else{
												tfValor.removeStyleName("invalid-txt");
												valor_valid = true;
											}										
										}									
								}
							}
						});
						
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
						cbIntervalo.setStyleName("caption-align-novo-titulo");
						cbIntervalo.setImmediate(true);
						cbIntervalo.setRequired(true);
						cbIntervalo.addItem("SEMANAL");
						cbIntervalo.addItem("MENSAL");
						cbIntervalo.setNullSelectionAllowed(false);
						
						addComponent(cbIntervalo);						
					}
			});
		
		
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
						
					if(tfValor.isValid() && tfNDoc.isValid() && dfVencimento.isValid() && tfQAtd.isValid() && cbControle.isValid() && 
							data_venc_valid && valor_valid && nDoc_valid && cbIntervalo.isValid()){
						fireEvent(new NovoTituloEvent(event.getComponent().getParent(), true,  
								(Integer)Clientelecionado.getId(), tfValor.getValue(), tfNDoc.getValue(), 
								dfVencimento.getValue(), tfQAtd.getValue(), cbControle.getItem(cbControle.getValue()).getItemProperty("nome").getValue().toString(), 
								cbIntervalo.getValue().toString()));
						Notification.show("Título(s) Gerado(s) Com Sucesso!");
						close();
					}else{						
													
							if(!tfCodCliente.isValid()){
								tfCodCliente.addStyleName("invalid-txt");
							}else{
								tfCodCliente.removeStyleName("invalid-txt");
							}
							if(!tfDescricaoCliente.isValid()){
								tfDescricaoCliente.addStyleName("invalid-txt");
							}else{
								tfDescricaoCliente.removeStyleName("invalid-txt");
							}
							if(!tfValor.isValid() || !valor_valid){
								tfValor.addStyleName("invalid-txt");
							}else{
								tfValor.removeStyleName("invalid-txt");
							}
							if(!tfNDoc.isValid() || !nDoc_valid){
								tfNDoc.addStyleName("invalid-txt");
							}else{
								tfNDoc.removeStyleName("invalid-txt");
							}
							if(!dfVencimento.isValid() || !data_venc_valid){
								dfVencimento.addStyleName("invalid-txt");
							}else{
								dfVencimento.removeStyleName("invalid-txt");
							}
							if(!tfQAtd.isValid()){
								tfQAtd.addStyleName("invalid-txt");
							}else{
								tfQAtd.removeStyleName("invalid-txt");
							}				
							
						Notification.show("Não é Possivel Gerar Novo(s) Tituilo(s), Verifique se todos os Campos estão Preenchidos!");
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
		
		private Integer codCliente;
		private String valor;
		private String nDoc;
		private Date dataVenciemento;
		private String qtd;
		private String controle;
		private String intervalo;
		
		private boolean confirm;
		
		public NovoTituloEvent(Component source, boolean confirm, Integer codCliente, String valor, String nDoc, Date dataVencimento, 
				String qtd, String controle, String intervalo) {
			super(source);
			
			this.confirm = confirm;		
			this.codCliente = codCliente;
			this.valor = valor;
			this.nDoc = nDoc;
			this.dataVenciemento = dataVencimento;
			this.qtd = qtd;
			this.controle = controle;
			this.intervalo = intervalo;
			
		}

		
		public boolean isConfirm() {
			return confirm;
		}

		
		public String getIntervalo(){
			return intervalo;
		}

		public String getControle(){
			return controle;
		}
		
		public Integer getCodCliente() {
			return codCliente;
		}


		public String getValor() {
			return valor;
		}


		public String getnDoc() {
			return nDoc;
		}


		public Date getDataVenciemento() {
			return dataVenciemento;
		}


		public String getQtd() {
			return qtd;
		}	
		
		
	}
	public interface NovoTituloListerner extends Serializable{
		public void onClose(NovoTituloEvent event);
	}
	

}
