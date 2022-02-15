package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

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
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ComprarCreditoEditor extends Window implements GenericEditor{

	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
	ComboBox cbPlanos;
	
	
	boolean validarContrato = true; 
	
	String planoAtual;
	TextField tfCarencia;
	TextField tfVencProxBoleto;
	DateField dfDataBoleto;
	
	TextField tfUploadDownload;
	TextField tfInfoValorPlano;
	TextField tfInfoValorPrimeiroBoleto;
	TextField tfNovosDias;
	Integer diasRestantes;
	
	public ComprarCreditoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("640px");
		setHeight("468px");
		
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
		
		

		final Cliente cliente = (Cliente) item.getItemProperty("cliente").getValue();
		
		vlRoot.addComponent(
				new FormLayout(){					
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
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField tfCliente = new TextField("Titular Atual");
					tfCliente.setValue(cliente.getNome_razao());
					tfCliente.setReadOnly(true);
					tfCliente.setWidth("360px");
					tfCliente.setStyleName("caption-align-renovar-acesso");
					
					addComponent(tfCliente);
				}
		});
		
				
		
		vlRoot.addComponent(
			new FormLayout(){					
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
		vlRoot.addComponent(
			new FormLayout(){					
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
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField tfDownUpAtual = new TextField("Download / Upload");
					tfDownUpAtual.setValue(pa.getRate_limit());
					tfDownUpAtual.setReadOnly(true);
					tfDownUpAtual.setWidth("250px");
					tfDownUpAtual.setStyleName("caption-align-renovar-acesso");
					
					addComponent(tfDownUpAtual);	
						
				}
			});
	
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					String dias = "";				
					Date getData_venc_contrato = (Date)item.getItemProperty("data_venc_contrato").getValue();

					DateTime dt1 = new DateTime(new Date());
					DateTime dt2 = new DateTime(getData_venc_contrato).plusDays(1);
					
					Integer days = Days.daysBetween(dt1, dt2).getDays();
					diasRestantes = days;
					dias = days.toString();
										
					TextField tfTerminoCarencia = new TextField("Término dos Créditos Atual");
					tfTerminoCarencia.setValue(dias+" DIAS");
					tfTerminoCarencia.setReadOnly(true);
					tfTerminoCarencia.setWidth("250px");
					tfTerminoCarencia.setStyleName("caption-align-renovar-acesso");
					
					addComponent(tfTerminoCarencia);	
						
				}
			});
		
		
		vlRoot.addComponent(
			new FormLayout(){					
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
													
						PlanoAcesso pa = (PlanoAcesso)item.getItemProperty("plano").getValue();
						String planoAtual = cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("nome").toString();					

												
								if(!pa.getNome().equals(planoAtual) && diasRestantes > 1){

									
									Notify.Show("Escolha o Mesmo Plano, Cliente ainda Possui Créditos!", Notify.TYPE_ERROR);
									
									tfUploadDownload.setReadOnly(false);
									tfUploadDownload.setValue("");
									tfUploadDownload.setReadOnly(true);
									
									tfInfoValorPlano.setReadOnly(false);
									tfInfoValorPlano.setValue("");
									tfInfoValorPlano.setReadOnly(true);

									dfDataBoleto.setReadOnly(false);
									dfDataBoleto.removeAllValidators();
									dfDataBoleto.setReadOnly(true);
									
									tfNovosDias.setReadOnly(false);
									tfNovosDias.setValue("");
									tfNovosDias.setReadOnly(true);
			
									cbPlanos.removeAllValidators();
									
									validarContrato = false;
									
								}else{
			
									ContratosAcessoDAO cDAO = new ContratosAcessoDAO();
									Integer codPlano = Integer.parseInt(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("id").toString());
									ContratosAcesso ca = cDAO.getContrato(codPlano);						
							
							//Preenche RateLimit e Valor do Plano
							if(cbPlanos.getValue() != null){
								
								try{
									
									tfUploadDownload.setReadOnly(false);
									tfUploadDownload.setValue(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("rate_limit").toString());
									tfUploadDownload.setReadOnly(true);
									
									tfInfoValorPlano.setReadOnly(false);
									tfInfoValorPlano.setValue(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("valor").toString());
									tfInfoValorPlano.setReadOnly(true);

									dfDataBoleto.setReadOnly(false);
									dfDataBoleto.setValue(new Date());
									dfDataBoleto.setReadOnly(true);
									
									DateTime dt2 = new DateTime(new Date()).plusMonths(1);	
									DateTime dt1 = new DateTime (new Date());
									
									Integer dias = 0;
									
									if(diasRestantes > 0){
										dias = Days.daysBetween(dt1, dt2).getDays() + diasRestantes;
									}else{
										dias = Days.daysBetween(dt1, dt2).getDays();
									}
									tfNovosDias.setReadOnly(false);
									tfNovosDias.setValue(dias.toString()+" DIAS");
									tfNovosDias.setReadOnly(true);

									validarContrato = true;
									
								}catch(Exception e){
									e.printStackTrace();
								}
							}
							

						}
					}
				});

				addComponent(cbPlanos);																
			}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
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
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					tfInfoValorPlano = new TextField ("Valor do Boleto (R$)");								
					tfInfoValorPlano.setStyleName("caption-align-renovar-acesso");
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
					
					dfDataBoleto = new DateField("Vencimento do Boleto");
					dfDataBoleto.setDateFormat("dd/MM/yyyy");
					dfDataBoleto.setWidth("150px");
					dfDataBoleto.setStyleName("caption-align-renovar-acesso");
					dfDataBoleto.setReadOnly(true);
					
					addComponent(dfDataBoleto);	
						
				}
			});		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
															
					tfNovosDias = new TextField("Vencimento dos Novos Créditos");
					tfNovosDias.setReadOnly(true);
					tfNovosDias.setWidth("250px");
					tfNovosDias.setStyleName("caption-align-renovar-acesso");
					
					addComponent(tfNovosDias);	
						
				}
			});
	
	}
	
	private void concluirCompra(){
		
		
			if(cbPlanos.getValue() != null && validarContrato){
				try {										
					
					EntityItem<PlanoAcesso> entityItem = (EntityItem<PlanoAcesso>)cbPlanos.getItem(cbPlanos.getValue());
					fireEvent(new ComprarCreditosAcessoEvent(getUI(),true,entityItem.getEntity()));					
				} catch (Exception e) {											
					e.printStackTrace();
					Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);		
				}
			}else{				
				Notify.Show_Invalid_Submit_Form();
				
				if(cbPlanos.getValue() == null){
					cbPlanos.addStyleName("invalid-combo-txt");
				}else{
					cbPlanos.removeStyleName("invalid-combo-txt");	
				}
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
					concluirCompra();
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
					fireEvent(new ComprarCreditosAcessoEvent(getUI(),false,null));	
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){								
						
								if(!validarContrato){
									
									Notify.Show("Falta mais de 1 Mês para vecer esse plano, escolha um outro Plano!", Notify.TYPE_WARNING);
								}else{	
									
									if(cbPlanos.getValue() != null && validarContrato){
										try {										
											EntityItem<PlanoAcesso> entityItem = (EntityItem<PlanoAcesso>)cbPlanos.getItem(cbPlanos.getValue());
											fireEvent(new ComprarCreditosAcessoEvent(getUI(),true,entityItem.getEntity()));			
										} catch (Exception e) {												
											e.printStackTrace();
											Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
										}
									}else{
										Notify.Show_Invalid_Submit_Form();
									}
								}
							}else{										
								fireEvent(new ComprarCreditosAcessoEvent(getUI(), false, null));
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
	
	
	public void addListerner(ComprarCreditosAcessoListerner target){
		try {
			Method method = ComprarCreditosAcessoListerner.class.getDeclaredMethod("onClose", ComprarCreditosAcessoEvent.class);
			addListener(ComprarCreditosAcessoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ComprarCreditosAcessoListerner target){
		removeListener(ComprarCreditosAcessoEvent.class, target);
	}
	public static class ComprarCreditosAcessoEvent extends Event{
		
		
		private boolean confirm;
		private Date dataPrimeiroBoleto;		
		private PlanoAcesso planoNovo;
		
		public ComprarCreditosAcessoEvent(Component source,boolean confirm,PlanoAcesso panoNovo ) {
			super(source);
			
			this.planoNovo = panoNovo;
			this.confirm = confirm;			
		}		
		
		public PlanoAcesso getPlanoNovo() {
			return planoNovo;
		}	

		public boolean isConfirm() {
			return confirm;
		}	
		
		
	}
	public interface ComprarCreditosAcessoListerner extends Serializable{
		public void onClose(ComprarCreditosAcessoEvent event);
	}
}
