package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MudancaPlanoEditor extends Window implements GenericEditor{

	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
	ComboBox cbPlanos;
	ComboBox cbInstGratis;
	
	
	boolean validarContrato = true; 
	
	String planoAtual;
	TextField tfCarencia;
	TextField tfVencProxBoleto;
	
	TextField tfUploadDownload;
	TextField tfInfoValorPlano;
	TextField tfInfoValorPrimeiroBoleto;
	
	
	public MudancaPlanoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		
		
		setWidth("640px");
		setHeight("537px");
		
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
		
		

		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				TextField tfCodContrato = new TextField("Contrato");
				tfCodContrato.setValue(item.getItemProperty("id").toString());
				tfCodContrato.setReadOnly(true);
				tfCodContrato.setWidth("62px");
				tfCodContrato.setStyleName("caption-align-renovar-acesso");
				
				addComponent(tfCodContrato);
			}
		});
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				Cliente ca = (Cliente)item.getItemProperty("cliente").getValue();
				TextField tfContratoAtual = new TextField("Titular Atual");
				tfContratoAtual.setValue(ca.getNome_razao());
				tfContratoAtual.setReadOnly(true);
				tfContratoAtual.setStyleName("caption-align-renovar-acesso");
				tfContratoAtual.setWidth("360px");				
				
				addComponent(tfContratoAtual);	
				
			}
		});
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
				
				ContratosAcesso ca = (ContratosAcesso)item.getItemProperty("contrato").getValue();
				TextField tfContratoAtual = new TextField("Contrato Atual");
				tfContratoAtual.setValue(ca.getNome());
				tfContratoAtual.setReadOnly(true);
				tfContratoAtual.setStyleName("caption-align-renovar-acesso");
				tfContratoAtual.setWidth("360px");				
				
				addComponent(tfContratoAtual);	
				
			}
		});
		
		final PlanoAcesso pa = (PlanoAcesso)item.getItemProperty("plano").getValue();
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				planoAtual = pa.getNome();
				TextField tfPlanoAtual = new TextField("Plano Atual");
				tfPlanoAtual.setValue(pa.getNome());
				tfPlanoAtual.setReadOnly(true);
				tfPlanoAtual.setWidth("250px");
				tfPlanoAtual.setStyleName("caption-align-renovar-acesso");
				
				addComponent(tfPlanoAtual);	
					
			}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField tfDownUpAtual = new TextField("Upload/Download ");
					tfDownUpAtual.setValue(pa.getRate_limit());
					tfDownUpAtual.setReadOnly(true);
					tfDownUpAtual.setWidth("250px");
					tfDownUpAtual.setStyleName("caption-align-renovar-acesso");
					
					addComponent(tfDownUpAtual);	
						
				}
			});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					String carencia = "SEM CARÊNCIA";
					String getCarencia = item.getItemProperty("carencia").getValue().toString();
					Date getData_venc_contrato = (Date)item.getItemProperty("data_venc_contrato").getValue();
					ContratosAcesso getContrato = (ContratosAcesso)item.getItemProperty("contrato").getValue();
					
					if(getCarencia.equals("SIM") && getContrato.getCarencia().equals("SIM")){			
						
						
						DateTime dt1 = new DateTime(new Date());
						DateTime dt2 = new DateTime(getData_venc_contrato).plusMonths(1);			 
						Integer months = Months.monthsBetween(dt1, dt2).getMonths();
						
						carencia = months.toString()+" MESES";					
					}
										
					TextField txCarenciaAtual = new TextField("Carência Atual");
					txCarenciaAtual.setValue(carencia);
					txCarenciaAtual.setReadOnly(true);
					txCarenciaAtual.setWidth("250px");
					txCarenciaAtual.setStyleName("caption-align-renovar-acesso");
					
					addComponent(txCarenciaAtual);	
						
				}
			});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					String dias = "";				
					Date getData_venc_contrato = (Date)item.getItemProperty("data_venc_contrato").getValue();

					DateTime dt1 = new DateTime(new Date());
					DateTime dt2 = new DateTime(getData_venc_contrato).plusDays(1);
					
					Integer days = Days.daysBetween(dt1, dt2).getDays();
					
					dias = days.toString();
										
					TextField tfTerminoCarencia = new TextField("Termino de Contrato Atual");
					tfTerminoCarencia.setValue(dias+" DIAS");
					tfTerminoCarencia.setReadOnly(true);
					tfTerminoCarencia.setWidth("250px");
					tfTerminoCarencia.setStyleName("caption-align-renovar-acesso");
					
					addComponent(tfTerminoCarencia);	
						
				}
			});
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
				
				final ContratosAcesso ca = (ContratosAcesso)item.getItemProperty("contrato").getValue();
				JPAContainer<PlanoAcesso> planos = JPAContainerFactory.make(PlanoAcesso.class, ConnUtil.getEntity());
				planos.addContainerFilter(Filters.eq("status", "ATIVO"));
				planos.addContainerFilter(Filters.eq("contrato_acesso", ca));
				planos.addContainerFilter(Filters.not(Filters.eq("nome", "BLOQUEADO")));
				planos.sort(new Object[]{"nome"}, new boolean[]{true});

				cbPlanos = new ComboBox("Novo Plano", planos);
				cbPlanos.setNullSelectionAllowed(false);
				cbPlanos.setRequired(true);
				cbPlanos.setImmediate(true);
				cbPlanos.setWidth("250px");
				cbPlanos.focus();
				cbPlanos.setStyleName("caption-align-renovar-acesso");
				cbPlanos.setItemCaptionPropertyId("nome");				
										
				cbPlanos.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						final CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
						AcessoCliente ac = caDAO.getAcessoByCodAceso(Integer.parseInt(item.getItemProperty("id").getValue().toString()));
						
						//boolean allowRenovar = caDAO.allowRenovacaoComBoletosAdiantados(ac.getId(), (Integer) cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("id").getValue());
							
						PlanoAcesso pa = (PlanoAcesso)item.getItemProperty("plano").getValue();
						String planoAtual = cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("nome").toString();					

						boolean allowAlterar =  true;
						
						if(temFidelidade(ac)){							
							Double valorPlanoAtual = Real.formatStringToDBDouble(ac.getPlano().getValor());
							Double valorPlanoNovo = Real.formatStringToDBDouble(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("valor").toString());
							
							if(valorPlanoNovo < valorPlanoAtual){
								allowAlterar = false;
							}
							
							if(ac.isN_controla_vlr_plano()){
								allowAlterar = true;
							}
						}
						
						if(allowAlterar){						
																				
							if(!pa.getNome().equals(planoAtual)){
								if(ac.getContrato().getCarencia().equals("SIM")){		
										
													
										ContratosAcessoDAO cDAO = new ContratosAcessoDAO();
										Integer codPlano = Integer.parseInt(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("id").toString());
										ContratosAcesso ca = cDAO.getContrato(codPlano);
										
										if(ca != null){
											String months = ca.getVigencia().toString();
											String carencia = months.toString()+" MESES";	
											
											tfCarencia.setReadOnly(false);
											tfCarencia.setValue(carencia);
											tfCarencia.setReadOnly(true);
											validarContrato = true;
										}								
									
								}else{						
									tfCarencia.setReadOnly(false);
									tfCarencia.setValue("SEM CARÊNCIA");
									tfCarencia.setReadOnly(true);
									validarContrato = true;					
								}	
							}else{							
								
								Notify.Show("Plano Inválido, Selecione um Plano diferente do Plano Atual", Notify.TYPE_ERROR);
								validarContrato = false;
								
							}
							
							
							
							//Preenche RateLimit e Valor do Plano
							if(cbPlanos.getValue() != null){
								
								try{
									
									
									tfUploadDownload.setReadOnly(false);
									tfUploadDownload.setValue(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("rate_limit").toString());
									tfUploadDownload.setReadOnly(true);
									
									tfInfoValorPlano.setReadOnly(false);
									tfInfoValorPlano.setValue(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("valor").toString());
									tfInfoValorPlano.setReadOnly(true);
									
									tfInfoValorPrimeiroBoleto.setReadOnly(false);
									
																			 									
									tfInfoValorPrimeiroBoleto.setValue(caDAO.calcDiferencaValorPlano(ac.getId(), (Integer)cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("id").getValue()));
									tfInfoValorPrimeiroBoleto.setReadOnly(true);
									
								}catch(Exception e){
									e.printStackTrace();
								}
								
							}
						}else{
							validarContrato = false;													
							//Notify.Show("Escolha um Plano de Valor Maior para Cliente com Boletos Adiantados!", Notify.TYPE_ERROR);
							Notify.Show("Não é possível alterar para um plano de valor menor!", Notify.TYPE_ERROR);
						}
						
						
						//Condição de Plano que Permite Comodato Total
//						if(ac.getRegime().equals("COMODATO (TOTAL)")){
//							
//							if(pa.getPermitir_comodato_total() != null && !pa.getPermitir_comodato_total().equals("SIM")){
//								validarContrato = false;
//							}
//							if(pa.getPermitir_comodato_total() == null){
//								validarContrato = false;
//							}					
//							if(!validarContrato){
//								Notify.Show("Não é possível alterar para um plano que não permita comodato total!", Notify.TYPE_ERROR);	
//							}
//							
//						}
						
					}
				});

				addComponent(cbPlanos);																
			}
		});
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);										
									
					tfUploadDownload = new TextField("Upload / Download");
					tfUploadDownload.setStyleName("caption-align-renovar-acesso");				
					tfUploadDownload.setReadOnly(true);
															
					addComponent(tfUploadDownload);					
				}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					tfInfoValorPlano = new TextField ("Valor do Plano (R$)");								
					tfInfoValorPlano.setStyleName("caption-align-renovar-acesso");
					tfInfoValorPlano.setId("txtVlrPlano");
					tfInfoValorPlano.addStyleName("align-currency");
					
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
														
					tfInfoValorPrimeiroBoleto = new TextField ("Valor do Primeiro Boleto (R$)");								
					tfInfoValorPrimeiroBoleto.setStyleName("caption-align-renovar-acesso");
					tfInfoValorPrimeiroBoleto.setId("txtVlrPrimeiroBoleto");
					tfInfoValorPrimeiroBoleto.addStyleName("align-currency");
					
					tfInfoValorPrimeiroBoleto.setReadOnly(true);
					addComponent(tfInfoValorPrimeiroBoleto);
					
					JavaScript.getCurrent().execute("$('#txtVlrPrimeiroBoleto').maskMoney({decimal:',',thousands:'.'})");
				}
		});	
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true); 
					
				tfCarencia = new TextField("Nova Carência");					
				tfCarencia.setReadOnly(true);
				tfCarencia.setWidth("150px");
				tfCarencia.setStyleName("caption-align-renovar-acesso");
				
				addComponent(tfCarencia);	
					
			}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true); 
					
					CredenciaisAcessoDAO cDAO = new CredenciaisAcessoDAO();	
					Date vencBoleto = cDAO.buscarDataVencBoleto((Integer)item.getItemProperty("id").getValue());

					SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
					
					String vencBoletoBR = "";
					if(vencBoleto != null){
						vencBoletoBR = form.format(vencBoleto);
					}
					
					tfVencProxBoleto = new TextField("Venc. Prox. Boleto");	
					tfVencProxBoleto.setValue(vencBoletoBR);
					tfVencProxBoleto.setReadOnly(true);
					tfVencProxBoleto.setWidth("150px");
					tfVencProxBoleto.setStyleName("caption-align-renovar-acesso");
					
					addComponent(tfVencProxBoleto);	
						
				}
			});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
				
				cbInstGratis = new ComboBox("Inst. Grátis");
				cbInstGratis.setNullSelectionAllowed(false);
				cbInstGratis.setRequired(true);
				cbInstGratis.setImmediate(true);
				cbInstGratis.setWidth("61px");				
				cbInstGratis.setStyleName("caption-align-renovar-acesso");
				cbInstGratis.addItem("SIM");
				cbInstGratis.addItem("NAO");
								
				addComponent(cbInstGratis);		
			}
		});
		
	}
	private boolean temFidelidade(AcessoCliente ac){
		boolean carencia = false;		
		
		if(ac.getCarencia() != null && ac.getCarencia().equals("SIM") && ac.getContrato().getCarencia().equals("SIM")){			
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				
				DateTime dt1 = new DateTime(sdf.parse(sdf.format(new Date())));
				DateTime dt2 = new DateTime(sdf.parse(sdf.format(ac.getData_venc_contrato()))).plusMonths(1);			 
				Integer months = Months.monthsBetween(dt1, dt2).getMonths();
				
				if(months > 0){
					carencia = true;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return carencia;
	}
	private void concluirAlteracaoPlano(){
		
		
			if(cbPlanos.getValue() != null && validarContrato && cbInstGratis.getValue() != null){
				try {										
					
					EntityItem<PlanoAcesso> entityItem = (EntityItem<PlanoAcesso>)cbPlanos.getItem(cbPlanos.getValue()); 
					fireEvent(new RenovarPlanoAcessoEvent(getUI(),true,	entityItem.getEntity(), cbInstGratis.getValue().toString()));			
					
				} catch (Exception e) {
					e.printStackTrace();					
				}
			}else{
				Notify.Show_Invalid_Submit_Form();
			}
		
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!validarContrato){					
					Notify.Show("Plano Inválido, Selecione um Plano diferente do Plano Atual", Notify.TYPE_ERROR);
				}else{	
					concluirAlteracaoPlano();
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
				
				if(!cbPlanos.isModified()){									
					fireEvent(new RenovarPlanoAcessoEvent(getUI(),false,null,null));	
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){							
								concluirAlteracaoPlano();
							}else{										
								fireEvent(new RenovarPlanoAcessoEvent(getUI(), false, null,null));									
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
	
	
	public void addListerner(RenovarPlanoAcessoListerner target){
		try {
			Method method = RenovarPlanoAcessoListerner.class.getDeclaredMethod("onClose", RenovarPlanoAcessoEvent.class);
			addListener(RenovarPlanoAcessoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(RenovarPlanoAcessoListerner target){
		removeListener(RenovarPlanoAcessoEvent.class, target);
	}
	public static class RenovarPlanoAcessoEvent extends Event{
		
		
		private boolean confirm;
		private Date dataPrimeiroBoleto;		
		private PlanoAcesso plano;
		private String InstGratis;
		
		public RenovarPlanoAcessoEvent(Component source,boolean confirm, PlanoAcesso plano, String InstGratis ) {
			super(source);
			
			this.plano = plano;
			this.confirm = confirm;
			this.InstGratis = InstGratis;
			
		}

		public String getInstGratis(){
			return InstGratis;
		}
		
		public PlanoAcesso getPlanoNovo() {
			return plano;
		}	

		public boolean isConfirm() {
			return confirm;
		}	
		
		
	}
	public interface RenovarPlanoAcessoListerner extends Serializable{
		public void onClose(RenovarPlanoAcessoEvent event);
	}
}
