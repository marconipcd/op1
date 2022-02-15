package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.EmpresaDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.ClienteUtil2;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class ReativarContratoAcessoEditor extends Window implements GenericEditor {

		
	private static final long serialVersionUID = 1L;
	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	String valorBoleto;
	DateField dfDataInstalacao;
	DateField dfDataBoleto;
	
	JPAContainer<PlanoAcesso> containerPlanos;
	ComboBox cbPlanos;
	ComboBox cbContrato;
	
	TextField tfInfoValorPrimeiroBoleto;
	TextField tfInfoValorAdesao;
	TextField tfInfoValorPlano;
	
	boolean validarInstalacao = true;
	boolean validarBoleto = true;
	boolean validarFiador = true;
	
	String valorPrimeiroBoleto;
	String contrato;
	
	Date dataPrimeiroBoleto;
	Date dataInstalacao;
	Cliente Clientelecionado;
	Cliente Fiadorlecionado;
	
	Integer codCliente;
	Integer codFiador;
	
	private Label lbRegistros;
	
	TextField tfDescricaoCliente;
	TextField tfDescricaoFiador;
	
	TextField tfCodFiador;
	TextField tfCodCliente;
	
	 HorizontalLayout hlFloat;
	
	

	TextField tfRegime;
	TextField tfUploadDownload;
	
	public ReativarContratoAcessoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("795px");
		setHeight("445px");
		
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
	
	private JPAContainer<PlanoAcesso> buildContainerPlanos(){
		containerPlanos = JPAContainerFactory.make(PlanoAcesso.class, ConnUtil.getEntity());
		containerPlanos.sort(new Object[]{"nome"}, new boolean[]{true});

		if(fieldGroup.getField("contrato") != null){
			Item selectedContrato = ((ComboBox) fieldGroup.getField("contrato")).getItem(((ComboBox) fieldGroup.getField("contrato")).getValue());
			if(selectedContrato != null){				
				containerPlanos.addContainerFilter(Filters.eq("contrato_acesso", 
						new ContratosAcesso(Integer.parseInt(selectedContrato.getItemProperty("id").getValue().toString()))));
				containerPlanos.addContainerFilter(Filters.not(Filters.eq("nome", "BLOQUEADO")));
			}
		}
		
		return containerPlanos;
	}
	
	
//	public Label buildLbRegistros(){
//		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
//		return lbRegistros;
//	}
	
	
	private void validarDataInstBoleto(){
		
		Integer codContrato = null;
		if(fieldGroup.getField("contrato") != null && fieldGroup.getField("contrato").getValue() != null){
			codContrato =(Integer) ((ComboBox) fieldGroup.getField("contrato")).getItem(fieldGroup.getField("contrato").getValue()).getItemProperty("id").getValue();
		}
		
		Integer codPlano = null;
		if(fieldGroup.getField("plano") != null && fieldGroup.getField("plano").getValue() != null){
			Integer valueItem = (Integer)((ComboBox) fieldGroup.getField("plano")).getValue();
			Item itemTeste = ((ComboBox) fieldGroup.getField("plano")).getItem(valueItem);
			codPlano =(Integer) itemTeste.getItemProperty("id").getValue();
		}
		
		if(dfDataInstalacao.getValue() != null){																
			dataInstalacao = dfDataInstalacao.getValue(); 
		}
		
		if(dfDataBoleto.getValue() != null){																
			dataPrimeiroBoleto = dfDataBoleto.getValue(); 
		}
								
		if(codContrato != null && codPlano != null && dataInstalacao != null && dataPrimeiroBoleto != null){
			CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
			String valorBoleto = caDAO.calcularValorPrimeiroBoleto(codContrato, codPlano, dataInstalacao, dataPrimeiroBoleto );
			
			if(valorBoleto != null){
				tfInfoValorPrimeiroBoleto.setReadOnly(false);
				tfInfoValorPrimeiroBoleto.setValue(Real.formatDbToString(valorBoleto));	
				valorPrimeiroBoleto = tfInfoValorPrimeiroBoleto.getValue();
				tfInfoValorPrimeiroBoleto.setReadOnly(true);
			}		
		}
		
		
		Calendar data = Calendar.getInstance(); 
		data.setTime(new Date());
		data.add(Calendar.DAY_OF_MONTH, -1);	
		Date dtOnten = data.getTime();	
		
		DataUtil dUtil = new DataUtil();
		
		if(dataInstalacao != null && dataPrimeiroBoleto != null){
			
			if (contrato.equals("POS-PAGO")){	
				
				DateTime dataInstacaoMaisUmMes = new DateTime(dfDataInstalacao.getValue()).plusMonths(1).plusDays(1);				
				
				if(dtOnten.compareTo(dataPrimeiroBoleto) < 0 && dtOnten.compareTo(dfDataInstalacao.getValue()) < 0 && 
						dfDataInstalacao.getValue().compareTo(dataPrimeiroBoleto) < 0 && dataInstacaoMaisUmMes.toDate().compareTo(dataPrimeiroBoleto) > 0){
					
					validarBoleto = true;
					validarInstalacao = true;
					
				}else{
					validarBoleto = false;
					validarInstalacao = false;	
					
					Notification.show("Data Inválida!");

				}
				
				
			}else if(contrato.equals("PRE-PAGO")){
				
				if(dtOnten.compareTo(dataPrimeiroBoleto) < 0 && dtOnten.compareTo(dfDataInstalacao.getValue()) < 0 && 
						dfDataInstalacao.getValue().compareTo(dataPrimeiroBoleto) == 0){
					
					validarBoleto = true;
					validarInstalacao = true;
					
				}else{
					
					validarBoleto = false;
					validarInstalacao = false;	
					
					Notification.show("Data Inválida!");
		
				}								
			}
		
		}else{
			
			if(dataInstalacao != null && dataPrimeiroBoleto == null){				

				if(dtOnten.compareTo(dfDataInstalacao.getValue()) < 0){
					validarInstalacao = true;
				}else{
					validarInstalacao = false;
					
					Notification.show("Data Inválida!");
				}
			}
						
		}
		
		
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
				tfCodCliente.setStyleName("caption-align-acesso");
				tfCodCliente.focus();
				tfCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				tfCodCliente.setImmediate(true);
						
				tfCodCliente.addBlurListener(new FieldEvents.BlurListener() {
					
					@Override
					public void blur(BlurEvent event) {
						//tfCodFiador.focus();
						
						if(tfCodCliente.getValue() != null && !tfCodCliente.getValue().equals("") && tfCodFiador.getValue() != null && !tfCodFiador.getValue().equals("")){
							if(codCliente.equals(codFiador)){
								Notification.show("Fiador Invalido!");
								validarFiador=false;
							}else{
								validarFiador= true;
							}
						}else{
							validarFiador= true;							
					   }
					}
				});
				
				tfCodCliente.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						Clientelecionado = new Cliente();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							codCliente = Integer.parseInt(event.getText());
							
							Clientelecionado = cDAO.getCliente(codCliente);		
													
							if(Clientelecionado != null){
								CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
								AcessoCliente ac = caDAO.getAcessoByCliente(Clientelecionado.getId());
								
								if(ac!=null){
								if(ac.getStatus_2().equals("ENCERRADO")){
									
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
								tfDescricaoCliente.setReadOnly(true);
								}else{
									Notification.show("O Cliente não possui Contrato Encerrado!");
									}
								}
							}else {
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
				
				
				tfCodCliente.setRequired(true);		
				tfDescricaoCliente = new TextField();
				tfDescricaoCliente.setTabIndex(2000);
				tfDescricaoCliente.setReadOnly(true);
				tfDescricaoCliente.setWidth("450px");
				
				
									
				if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
					EmpresaDAO eDAO = new EmpresaDAO();
					Cliente c = eDAO.getCliente(((Cliente)item.getItemProperty("cliente").getValue()).getId());
					
					if(c != null){
						tfCodCliente.setValue(c.getId().toString());
						tfCodCliente.setReadOnly(false);
						tfDescricaoCliente.setValue(c.getNome_razao());
						tfDescricaoCliente.setReadOnly(true);
					}
				}
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setTabIndex(300000);
				btSearchCliente.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						ClienteUtil2 cUtil2 = new ClienteUtil2(true, true,"ENCERRADO");
						cUtil2.addListerner(new ClienteUtil2.ClienteListerner() {
							
							@Override
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil2.ClienteEvent event) {
									if(event.getCliente() != null){
										tfCodCliente.setValue(event.getCliente().getId().toString());
										tfDescricaoCliente.setReadOnly(false);
										tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
										tfDescricaoCliente.setReadOnly(true);
										Clientelecionado = event.getCliente();
										codCliente = Integer.parseInt((event.getCliente().getId().toString()));
									}
								}							
						});
						
						getUI().addWindow(cUtil2);
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
		
		
		//Fiador
		vlRoot.addComponent(new HorizontalLayout(){
			{
				//setWidth("100%");
				
				JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
								
				
//				boolean preencher = false;
//				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
//					preencher = true;
//				}
				
				tfCodFiador = new TextField("Fiador");				
				tfCodFiador.setWidth("60px");				
				tfCodFiador.setNullRepresentation("");
				tfCodFiador.setImmediate(true);
				tfCodFiador.setId("txtCodFiador");
				
				JavaScript.getCurrent().execute("$('#txtCodFiador').mask('0000000000')");
				tfCodFiador.setStyleName("caption-align-acesso");

				//tfCodFiador.focus();
				
				
				tfCodFiador.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						Fiadorlecionado = new Cliente();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							codFiador = Integer.parseInt(event.getText());
														
							Fiadorlecionado = cDAO.getCliente(Integer.parseInt(event.getText()));	
							
							if(Fiadorlecionado != null){
								tfDescricaoFiador.setReadOnly(false);
								tfDescricaoFiador.setValue(Fiadorlecionado.getNome_razao());
								tfDescricaoFiador.setReadOnly(true);
							}else {
								tfDescricaoFiador.setReadOnly(false);
								tfDescricaoFiador.setValue("");
								tfDescricaoFiador.setReadOnly(true);
							}
						}else{
							tfDescricaoFiador.setReadOnly(false);
							tfDescricaoFiador.setValue("");
							tfDescricaoFiador.setReadOnly(true);							
						}
					}
				});
				
				
				tfCodFiador.setRequired(false);		
				
				tfDescricaoFiador = new TextField();
				tfDescricaoFiador.setReadOnly(true);
				tfDescricaoFiador.setWidth("450px");
				tfDescricaoFiador.setTabIndex(40000);
									
//				if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
//					EmpresaDAO eDAO = new EmpresaDAO();
//					Cliente c = eDAO.getCliente(((Cliente)item.getItemProperty("cliente").getValue()).getId());
//					
//					if(c != null){
//						tfCodFiador.setValue(c.getId().toString());
//						tfCodFiador.setReadOnly(false);
//						tfDescricaoCliente.setValue(c.getNome_razao());
//						tfDescricaoCliente.setReadOnly(true);
//					}
//				}
							
				final Button btSearchFiador = new Button();
				btSearchFiador.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchFiador.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchFiador.setTabIndex(500000);
				btSearchFiador.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						ClienteUtil cUtil = new ClienteUtil(true, true,null);
						cUtil.addListerner(new ClienteUtil.ClienteListerner() {
							
							@Override
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
								if(event.getCliente() != null){
									tfCodFiador.setValue(event.getCliente().getId().toString());
									tfDescricaoFiador.setReadOnly(false);
									tfDescricaoFiador.setValue(event.getCliente().getNome_razao());
									tfDescricaoFiador.setReadOnly(true);
									Fiadorlecionado = event.getCliente();
									
									codFiador = Integer.parseInt((event.getCliente().getId().toString()));

								}							
							}
						});
						
						getUI().addWindow(cUtil);
					}
				});			
				
				tfCodFiador.addBlurListener(new FieldEvents.BlurListener() {
					
					@Override
					public void blur(BlurEvent event) {	
						//cbContrato.focus();
						
						if(tfCodCliente.getValue() != null && !tfCodCliente.getValue().equals("") && tfCodFiador.getValue() != null && !tfCodFiador.getValue().equals("")){
								if(codCliente.equals(codFiador)){
									Notification.show("Fiador Invalido!");
									validarFiador=false;
								}else{
									validarFiador= true;
								}
							}else{
								validarFiador= true;							
						   }
					}
				});
				
				FormLayout frmCodigoFiador = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
												
						addComponent(tfCodFiador);							
					}
				};
				addComponent(frmCodigoFiador);
		
				FormLayout frmButtonSearchFiador =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchFiador);							
					}
				}; 
							
				FormLayout frmDescFiador = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");				
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoFiador);							
					}
				}; 
				addComponent(frmButtonSearchFiador);
				addComponent(frmDescFiador);
				setExpandRatio(frmDescFiador, 1);	
				
//				fieldGroup.bind(tfCodCliente,Clientelecionado);

			}	
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					JPAContainer<ContratosAcesso> containerContratos = JPAContainerFactory.make(ContratosAcesso.class, ConnUtil.getEntity());
					containerContratos.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
					containerContratos.sort(new Object[]{"nome"}, new boolean[]{true});
					
					cbContrato = new ComboBox("Contrato", containerContratos);
					cbContrato.setItemCaptionPropertyId("nome");
					cbContrato.setConverter(new SingleSelectConverter(cbContrato));
					cbContrato.setStyleName("caption-align-acesso");
					cbContrato.setNullSelectionAllowed(false);
					cbContrato.setRequired(true);
					cbContrato.setImmediate(true);
					cbContrato.setWidth("541px");
					cbContrato.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(fieldGroup != null && fieldGroup.getField("plano") != null){
								
								if(tfInfoValorPlano != null){
									tfInfoValorPlano.setReadOnly(false);
									tfInfoValorPlano.setValue("");
									tfInfoValorPlano.setReadOnly(true);
								}
								
								if(tfUploadDownload != null){
									tfUploadDownload.setReadOnly(false);
									tfUploadDownload.setValue("");
									tfUploadDownload.setReadOnly(true);
								}
								
								cbPlanos.setContainerDataSource(buildContainerPlanos());
								cbPlanos.setConverter(new SingleSelectConverter(cbPlanos));
								
								contrato = cbContrato.getItem(cbContrato.getValue()).getItemProperty("tipo_contrato").getValue().toString();
								
								tfRegime.setReadOnly(false);
								tfRegime.setValue(cbContrato.getItem(cbContrato.getValue()).getItemProperty("regime").getValue().toString());
								tfRegime.setReadOnly(true);
								
								
								Float vlContrato = Float.parseFloat((cbContrato.getItem(cbContrato.getValue()).getItemProperty("valor_desconto").getValue().toString().replace(".", "").replace(",", ".")));
								Float vlAdesao = Float.parseFloat((cbContrato.getItem(cbContrato.getValue()).getItemProperty("valor_adesao").getValue().toString().replace(".", "").replace(",", ".")));
								
								Float total = vlAdesao - vlContrato;
								
								tfInfoValorAdesao.setReadOnly(false);
								tfInfoValorAdesao.setValue(Real.formatDbToString(total.toString()));
								tfInfoValorAdesao.setReadOnly(true);
								
								dfDataBoleto.setReadOnly(false);
								dfDataBoleto.setValue(null);
								dfDataInstalacao.setValue(null);
								
								tfInfoValorAdesao.setReadOnly(false);
								tfInfoValorAdesao.setValue("");
								tfInfoValorAdesao.setReadOnly(true);
								
								tfInfoValorPlano.setReadOnly(false);
								tfInfoValorPlano.setValue("");
								tfInfoValorPlano.setReadOnly(true);
								
								tfInfoValorPrimeiroBoleto.setReadOnly(false);
								tfInfoValorPrimeiroBoleto.setValue("");
								tfInfoValorPrimeiroBoleto.setReadOnly(true);
								
								if(contrato.equals("PRE-PAGO")){
									dfDataBoleto.setReadOnly(false);
									
									if(dfDataInstalacao.getValue() != null){
										dfDataBoleto.setValue(dfDataInstalacao.getValue());
									}
									
									dfDataBoleto.setReadOnly(true);
								}else{
									dfDataBoleto.setReadOnly(false);									
								}
							}
						}
					});
										
					addComponent(cbContrato);
					
					fieldGroup.bind(cbContrato,"contrato");
				}
			});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
											
									
					tfRegime = new TextField("Regime");
					tfRegime.setStyleName("caption-align-acesso");					
					tfRegime.setRequired(true);
					tfRegime.setWidth("241px");
					tfRegime.setReadOnly(true);
															
					addComponent(tfRegime);	
					
					
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
															
					cbPlanos = new ComboBox("Plano de Acesso");
					cbPlanos.setItemCaptionPropertyId("nome");
					cbPlanos.setStyleName("caption-align-acesso");
					cbPlanos.setNullSelectionAllowed(false);
					cbPlanos.setRequired(true);
					cbPlanos.setWidth("300px");
					cbPlanos.setImmediate(true);
					
					cbPlanos.addListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
						
									Integer codContrato = null;
									if(fieldGroup.getField("contrato") != null && fieldGroup.getField("contrato").getValue() != null){
										codContrato =(Integer) ((ComboBox) fieldGroup.getField("contrato")).getItem(fieldGroup.getField("contrato").getValue()).getItemProperty("id").getValue();
									}
									
									Integer codPlano = null;
									if(fieldGroup.getField("plano") != null && fieldGroup.getField("plano").getValue() != null){
										Integer valueItem = (Integer)((ComboBox) fieldGroup.getField("plano")).getValue();
										Item itemTeste = ((ComboBox) fieldGroup.getField("plano")).getItem(valueItem);
										codPlano =(Integer) itemTeste.getItemProperty("id").getValue();
									}
									
									if(dfDataInstalacao.getValue() != null){
																		
										dataInstalacao = dfDataInstalacao.getValue(); 
									}
							
									if(codContrato != null && codPlano != null && dataInstalacao != null && dataPrimeiroBoleto != null){
										CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
										String valorBoleto = caDAO.calcularValorPrimeiroBoleto(codContrato, codPlano, dataInstalacao, dataPrimeiroBoleto);
										
										if(valorBoleto != null){
											tfInfoValorPrimeiroBoleto.setReadOnly(false);
											tfInfoValorPrimeiroBoleto.setValue(Real.formatDbToString(valorBoleto));	
											valorPrimeiroBoleto = tfInfoValorPrimeiroBoleto.getValue();
											tfInfoValorPrimeiroBoleto.setReadOnly(true);
									  }						
									}
							
									if(fieldGroup != null && fieldGroup.getField("plano") != null){

										Float vlContrato = Float.parseFloat((cbContrato.getItem(cbContrato.getValue()).getItemProperty("valor_desconto").getValue().toString().replace(".", "").replace(",", ".")));
										Float vlAdesao = Float.parseFloat((cbContrato.getItem(cbContrato.getValue()).getItemProperty("valor_adesao").getValue().toString().replace(".", "").replace(",", ".")));
										
										Float total = vlAdesao - vlContrato;
										
										tfInfoValorAdesao.setReadOnly(false);
										tfInfoValorAdesao.setValue(Real.formatDbToString(total.toString()));
										tfInfoValorAdesao.setReadOnly(true);
									}
									
									
									//Preenche RateLimit e Valor do Plano
									if(cbPlanos.getValue() != null){
										tfUploadDownload.setReadOnly(false);
										tfUploadDownload.setValue(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("rate_limit").toString());
										tfUploadDownload.setReadOnly(true);
										
										tfInfoValorPlano.setReadOnly(false);
										tfInfoValorPlano.setValue(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("valor").toString());
										tfInfoValorPlano.setReadOnly(true);
									}
								}
							});

					addComponent(cbPlanos);
					
					fieldGroup.bind(cbPlanos,"plano");
	
				}
			});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);										
									
					tfUploadDownload = new TextField("Upload / Download");
					tfUploadDownload.setStyleName("caption-align-acesso");				
					tfUploadDownload.setReadOnly(true);
															
					addComponent(tfUploadDownload);					
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
				
					dfDataInstalacao = new DateField("Data Instalação");
					dfDataInstalacao.setStyleName("caption-align-acesso");	
					dfDataInstalacao.setDateFormat("dd/MM/yyyy");
					dfDataInstalacao.setRequired(true);
					dfDataInstalacao.setDescription("Informe uma Data Maior ou Igual que a Data de Hoje");
					dfDataInstalacao.setImmediate(true);
					dfDataInstalacao.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(dfDataInstalacao.getValue() != null){						
								
								if(contrato.equals("PRE-PAGO")){
									dfDataBoleto.setReadOnly(false);
									
									if(dfDataInstalacao.getValue() != null){
										dfDataBoleto.setValue(dfDataInstalacao.getValue());
									}
									
									dfDataBoleto.setReadOnly(true);
								}else{
									dfDataBoleto.setReadOnly(false);									
								}
							}
						}
					});
					
										
					
					
					addComponent(dfDataInstalacao);
					dfDataInstalacao.setImmediate(true);
					dfDataInstalacao.addBlurListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
							dfDataBoleto.focus();
								
							try {
																
								if(dfDataInstalacao.getValue() == null || dfDataInstalacao.getValue().equals("")){
									
									validarBoleto = false;
									validarInstalacao = false;	
									
								}
							} catch (Exception e) {
								
							}
						}
					});
					
					dfDataInstalacao.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(dfDataInstalacao.getValue() != null){
								validarDataInstBoleto();
							}							
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
											
									
					dfDataBoleto = new DateField("Data Primeiro Boleto");
					dfDataBoleto.setStyleName("caption-align-acesso");	
					dfDataBoleto.setDateFormat("dd/MM/yyyy");
					dfDataBoleto.setRequired(true);
										
					dfDataBoleto.setDescription("Informe uma Data maior que a Data de Instalação e menor que 1 Mês");
										
					addComponent(dfDataBoleto);
					dfDataBoleto.setImmediate(true);
					
					
					dfDataBoleto.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							
								if(dfDataBoleto.getValue() != null){							
									validarDataInstBoleto();
								}
								
							
						}
					});
					
					dfDataBoleto.addBlurListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
							btCancelar.focus();						
																			
							if(dfDataBoleto.getValue() == null || dfDataBoleto.getValue().equals("")){								
								validarBoleto = false;
								validarInstalacao = false;										
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
														
					tfInfoValorPlano = new TextField ("Valor do Plano (R$)");								
					tfInfoValorPlano.setStyleName("caption-align-acesso");
					tfInfoValorPlano.setId("txtVlrPlano");
					tfInfoValorPlano.addStyleName("align-currency");
					
					tfInfoValorPlano.setReadOnly(true);
					addComponent(tfInfoValorPlano);
					
					JavaScript.getCurrent().execute("$('#txtVlrPlano').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					tfInfoValorAdesao = new TextField ("Adesão a Pagar (R$)");								
					tfInfoValorAdesao.setStyleName("caption-align-acesso");
					tfInfoValorAdesao.setId("txtVlrAdesao");
					tfInfoValorAdesao.addStyleName("align-currency");
					
					tfInfoValorAdesao.setReadOnly(true);
					addComponent(tfInfoValorAdesao);
					
					JavaScript.getCurrent().execute("$('#txtVlrAdesao').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					tfInfoValorPrimeiroBoleto = new TextField ("Valor Primeiro Boleto (R$)");								
					tfInfoValorPrimeiroBoleto.setStyleName("caption-align-acesso");
					tfInfoValorPrimeiroBoleto.setId("txtBoleto");
					tfInfoValorPrimeiroBoleto.addStyleName("align-currency");
					
					tfInfoValorPrimeiroBoleto.setReadOnly(true);
					addComponent(tfInfoValorPrimeiroBoleto);
					
					JavaScript.getCurrent().execute("$('#txtBoleto').maskMoney({decimal:',',thousands:'.'})");
				
				}
			});
	
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				if(tfCodCliente.getValue() != null && 
						!tfCodCliente.getValue().equals("") && 
						tfCodFiador.getValue() != null && 
						!tfCodFiador.getValue().equals("")){
					
					if(codCliente.equals(codFiador)){
						Notification.show("Fiador Invalido!");
						validarFiador=false;
						tfCodFiador.addStyleName("invalid-txt");
					}else{
						tfCodFiador.removeStyleName("invalid-txt");
						validarFiador= true;
					}
				}else{
					validarFiador = true;					
					tfCodFiador.removeStyleName("invalid-txt");
				}
				
									
				if(fieldGroup.isValid() && validarBoleto == true && validarInstalacao == true && validarFiador==true && dfDataInstalacao.getValue() != null && dfDataBoleto.getValue() != null && tfDescricaoCliente != null && !tfDescricaoCliente.getValue().equals("")){
					try {												
							fieldGroup.commit();		
							item.getItemProperty("cliente").setValue(Clientelecionado);
							item.getItemProperty("fiador").setValue(Fiadorlecionado);
							item.getItemProperty("data_instalacao").setValue(dataInstalacao);
							fireEvent(new ReativarCadastroEvent(getUI(), item, true, dataPrimeiroBoleto,valorPrimeiroBoleto));
														
							Notification.show("Contrato Cadastrado com Sucesso!");							
							close();

					} catch (Exception e) {											
						Notification.show("ERRO: Não foi Possivel Salvar as Alterações!");		
						e.printStackTrace();
					}
				}else{
					
					    for (Field<?> field: fieldGroup.getFields()) {						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
					}
					    
					    if(!tfCodCliente.isValid()){
					    	tfCodCliente.addStyleName("invalid-txt");
					    }else{
					    	tfCodCliente.removeStyleName("invalid-txt");  	
					    }					    

					Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
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
					fireEvent(new ReativarCadastroEvent(getUI(), item, false, dataPrimeiroBoleto,valorPrimeiroBoleto));
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
									
									DateTime dt = new DateTime(dfDataInstalacao.getValue()).plusMonths(1);	
									if(dataPrimeiroBoleto.compareTo(dt.toDate()) > 0){
										validarInstalacao = false;
										Notification.show("A data do primeiro boleto não pode ultrapassar 1 mês");
									}else{
										validarInstalacao = true;
										
									if(tfCodCliente.getValue() != null && !tfCodCliente.getValue().equals("") && tfCodFiador.getValue() != null && !tfCodFiador.getValue().equals("")){
										if(codCliente.equals(codFiador)){
											Notification.show("Fiador Invalido!");
											validarFiador=false;
										}else{
											validarFiador= true;
										}
									}else{
										validarFiador= true;							
									}
									
									if(fieldGroup.isValid() && validarBoleto == true && validarInstalacao == true  && tfDescricaoCliente != null && !tfDescricaoCliente.getValue().equals("")){							
										fieldGroup.commit();				
										fireEvent(new ReativarCadastroEvent(getUI(), item, true, dataPrimeiroBoleto,valorPrimeiroBoleto));
										Notification.show("Acesso Cadastrado com Sucesso!");							
										close();
										
									}else{
										Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");

									}
								  }
								} catch (Exception e) {									
									Notification.show("ERRO: Não foi Possivel Salvar as Alterações!");
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
	
	
	public void addListerner(ReativarCadastroListerner target){
		try {
			Method method = ReativarCadastroListerner.class.getDeclaredMethod("onClose", ReativarCadastroEvent.class);
			addListener(ReativarCadastroEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ReativarCadastroListerner target){
		removeListener(ReativarCadastroEvent.class, target);
	}
	public static class ReativarCadastroEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private Date dataPrimeiroBoleto;
		private String valorPrimeiroBoleto;
		
		public ReativarCadastroEvent(Component source, Item item, boolean confirm,Date dataPrimeiroBoleto, String valorPrimeiroBoleto) {
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
		
		public Date getDataPrimeiroBoleto(){
			return dataPrimeiroBoleto;
		}

		public String getValorPrimeiroBoleto() {
			return valorPrimeiroBoleto;
		}
		
	}
	public interface ReativarCadastroListerner extends Serializable{
		public void onClose(ReativarCadastroEvent event);
	}
	
	
	
	
	public void addListerner(ClienteListerner target){
		try {
			Method method = ClienteListerner.class.getDeclaredMethod("onSelected",ClienteEvent.class);
			addListener(ClienteEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClienteListerner target){
		removeListener(ClienteEvent.class, target);
	}
	public static class ClienteEvent extends Event{

		private Cliente cliente;
		
		public ClienteEvent(Component source, Cliente cliente) {
			super(source);		
			this.cliente  = cliente;
		}

		public Cliente getCliente() {
			
			return cliente;
		}		
	}
	public interface ClienteListerner{
		public void onSelected(ClienteEvent event);
	}
	


}
