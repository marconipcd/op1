package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class LiberarBoletosContratoEditor extends Window implements GenericEditor {

		
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
		
	TextField tfInfoValorPrimeiroBoleto;
	TextField tfInfoValorAdesao;
	TextField tfInfoValorPlano;
	
	boolean validarInstalacao = true;
	boolean validarBoleto = true;

	
	ContasReceberDAO crDAO = new ContasReceberDAO();
	
	String valorPrimeiroBoleto;
	String contrato;
	
	Date dataPrimeiroBoleto;
	Date dataInstalacao;
	
	
	private Label lbRegistros;
	
		
	HorizontalLayout hlFloat;
	
	
	TextField tfUploadDownload;
	TextField txtInstGratis;
	ComboBox cbInstalacaoGratis;
	
	private ComboBox cbEnderecos;
	
	AcessoCliente contratoCliente;
	
	TextField txtVlrCodigoCartao;
	
	public LiberarBoletosContratoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		contratoCliente = em.find(AcessoCliente.class, Integer.parseInt(item.getItemProperty("id").getValue().toString()));
		
		setWidth("480px");
		setHeight("334px");
		
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

	
	private BeanItemContainer<Endereco> buildContainerEnderecos(Integer codCliente){
		
		BeanItemContainer<Endereco> enderecos = new BeanItemContainer<>(Endereco.class);
		
		Query q = em.createQuery("select e from Endereco e where  e.clientes = :cliente and e.status = 'ATIVO'", Endereco.class);
		q.setParameter("cliente", new Cliente(codCliente));
				
		if(q.getResultList().size() >0){
			
			for (Endereco end : (List<Endereco>) q.getResultList()) {
				
				Query q2 = em.createQuery("select a from AcessoCliente a where a.endereco=:end and a.status_2 != 'ENCERRADO'", AcessoCliente.class);
				q2.setParameter("end", end);
				AcessoCliente ac = null;
				
				if(q2.getResultList().size() == 0){
					enderecos.addBean(end);
				}
			}
		}
		
		
		return enderecos;
	}
	
	
	
	public String getNextId() {
		CredenciaisAcessoDAO caDao = new CredenciaisAcessoDAO();
		return caDao.getNextID();
	}
	
//	public Label buildLbRegistros(){
//		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
//		return lbRegistros;
//	}
	
	
	EntityManager em = ConnUtil.getEntity();
	private void validarDataInstBoleto(){
		
		Integer codContrato = Integer.parseInt(item.getItemProperty("id").getValue().toString());
		AcessoCliente contrato = contratoCliente;
		
		Integer codPlano = contrato.getPlano().getId();
		
		if(dfDataInstalacao.getValue() != null){																
			dataInstalacao = dfDataInstalacao.getValue(); 
		}
		
		if(dfDataBoleto.getValue() != null){																
			dataPrimeiroBoleto = dfDataBoleto.getValue(); 
		}
								
		if(codContrato != null && codPlano != null && dataInstalacao != null && dataPrimeiroBoleto != null){
			CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
			String valorBoleto = caDAO.calcularValorPrimeiroBoleto(contrato.getContrato().getId(), codPlano, dataInstalacao, dataPrimeiroBoleto );
			
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
			
			if (contrato.getContrato().getTipo_contrato() != null && contrato.getContrato().getTipo_contrato().equals("POS-PAGO")){	
				
				DateTime dataInstacaoMaisUmMes = new DateTime(dfDataInstalacao.getValue()).plusMonths(1).plusDays(1);				
				
				if(dtOnten.compareTo(dataPrimeiroBoleto) < 0 && dtOnten.compareTo(dfDataInstalacao.getValue()) < 0 && 
						dfDataInstalacao.getValue().compareTo(dataPrimeiroBoleto) < 0 && dataInstacaoMaisUmMes.toDate().compareTo(dataPrimeiroBoleto) > 0){
					
					validarBoleto = true;
					validarInstalacao = true;
					
				}else{
					validarBoleto = false;
					validarInstalacao = false;	
					
					
					Notify.Show("Data Inválida", Notify.TYPE_ERROR);

				}
				
				
			}else if(contrato != null && contrato.equals("PRE-PAGO")){
				
				if(dtOnten.compareTo(dataPrimeiroBoleto) < 0 && dtOnten.compareTo(dfDataInstalacao.getValue()) < 0 && 
						dfDataInstalacao.getValue().compareTo(dataPrimeiroBoleto) == 0){
					
					validarBoleto = true;
					validarInstalacao = true;
					
				}else{
					
					validarBoleto = false;
					validarInstalacao = false;	
					
					Notify.Show("Data Inválida", Notify.TYPE_ERROR);
					
		
				}								
			}else if(contrato != null && contrato.equals("GRATIS")){
				validarBoleto = true;
				validarInstalacao = true;
			}
		
		}else{
			
			if(dataInstalacao != null && dataPrimeiroBoleto == null){				

				if(dtOnten.compareTo(dfDataInstalacao.getValue()) < 0){
					validarInstalacao = true;
				}else{
					validarInstalacao = false;					
					Notify.Show("Data Inválida", Notify.TYPE_ERROR);
				}
			}
						
		}
		
		
		if(dataPrimeiroBoleto != null){
			EntityManager em = ConnUtil.getEntity();
			List<ContasReceber> boletos = ContasReceberDAO.procurarBoletoPorAcessoeVencimento(codContrato, dataPrimeiroBoleto);
			
			if(boletos != null){
				validarBoleto = false;
				Notify.Show("Data Inválida, já existe boleto emitido nesta data!", Notify.TYPE_ERROR);
			}
		}
		
		
	}
	
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
	
		vlRoot.addComponent(new FormLayout(){					
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
								
								if(contrato != null && contrato.equals("PRE-PAGO")){
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
		vlRoot.addComponent(new FormLayout(){					
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
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
													
				TextField txtPlano = new TextField ("Plano de Acesso");		
				txtPlano.setWidth("187px");
				txtPlano.setStyleName("caption-align-acesso");
				txtPlano.setId("txtVlrPlano");
				txtPlano.addStyleName("align-currency");
				txtPlano.setValue(contratoCliente.getPlano().getNome()); 
				txtPlano.setReadOnly(true);
				addComponent(txtPlano);				
			
			}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					tfInfoValorPlano = new TextField ("Valor do Plano (R$)");								
					tfInfoValorPlano.setStyleName("caption-align-acesso");
					tfInfoValorPlano.setId("txtVlrPlano");
					tfInfoValorPlano.addStyleName("align-currency");
					tfInfoValorPlano.setValue(contratoCliente.getPlano().getValor()); 
					tfInfoValorPlano.setReadOnly(true);
					addComponent(tfInfoValorPlano);
					
					JavaScript.getCurrent().execute("$('#txtVlrPlano').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		
		vlRoot.addComponent(new FormLayout(){					
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
		
		vlRoot.addComponent(new FormLayout(){					
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
		
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
													
				txtVlrCodigoCartao = new TextField ("Cartão Cliente");
				txtVlrCodigoCartao.setValue(contratoCliente.getCodigo_cartao() != null ? contratoCliente.getCodigo_cartao() : "");
				txtVlrCodigoCartao.setEnabled(false);
				
				txtVlrCodigoCartao.setStyleName("caption-align-acesso");
				txtVlrCodigoCartao.setId("txtBoleto");
			
				addComponent(txtVlrCodigoCartao);
								
				JavaScript.getCurrent().execute("$('#txtVlrCodigoCartao').mask('00000000000000')");
			
			}
		});
	 
		
		
		
		
		
	}

	
	
	
	
	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				concluirCadastro();		
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
					fireEvent(new LiberarBoletosEvent(getUI(), item, false, null,null,null));					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								concluirCadastro();
							}else{							
								fieldGroup.discard();				
								fireEvent(new LiberarBoletosEvent(getUI(), item, false, null,null,null));
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
	
	private void concluirCadastro(){
		
		
		
	
									
		if( fieldGroup.isValid() && validarBoleto == true && validarInstalacao == true && dfDataInstalacao.getValue() != null && dfDataBoleto.getValue() != null &&
				txtVlrCodigoCartao.isValid()){
			try {		
				
				fieldGroup.commit();	
				
				item.getItemProperty("data_instalacao").setValue(dataInstalacao);
				item.getItemProperty("codigo_cartao").setValue(txtVlrCodigoCartao.getValue());
					
				fireEvent(new LiberarBoletosEvent(getUI(), item, true, dataPrimeiroBoleto,valorPrimeiroBoleto,
						txtVlrCodigoCartao.getValue() != null && !txtVlrCodigoCartao.getValue().equals("") ? Integer.parseInt(txtVlrCodigoCartao.getValue()) : 0));

			} catch (Exception e) {											
				Notify.Show("ERRO: Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);		
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
			    
			    
			    if(dfDataInstalacao.getValue() == null){
			    	dfDataInstalacao.addStyleName("invalid-date-txt");
			    }else{
			    	dfDataInstalacao.removeStyleName("invalid-date-txt");
			    }
			    
			    if(dfDataBoleto.getValue() == null){
			    	dfDataBoleto.addStyleName("invalid-date-txt");
			    }else{
			    	dfDataBoleto.removeStyleName("invalid-date-txt");
			    }
			   
			    Notify.Show_Invalid_Submit_Form();
		}		
	}
	
	
	public void addListerner(LiberarBoletosListerner target){
		try {
			Method method = LiberarBoletosListerner.class.getDeclaredMethod("onClose", LiberarBoletosEvent.class);
			addListener(LiberarBoletosEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(LiberarBoletosListerner target){
		removeListener(LiberarBoletosEvent.class, target);
	}
	public static class LiberarBoletosEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private Date dataPrimeiroBoleto;
		private String valorPrimeiroBoleto;
		private Integer codigo_cartao;
		
		public LiberarBoletosEvent(Component source, Item item, boolean confirm,Date dataPrimeiroBoleto, String valorPrimeiroBoleto, Integer codigo_cartao) {
			super(source);
			this.item = item;
			this.confirm = confirm;
			this.dataPrimeiroBoleto = dataPrimeiroBoleto;
			this.valorPrimeiroBoleto = valorPrimeiroBoleto;
			this.codigo_cartao = codigo_cartao;
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
	public interface LiberarBoletosListerner extends Serializable{
		public void onClose(LiberarBoletosEvent event);
	}
	
	
	
	
	
	


}
