package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.PlanoAcesso;
import com.vaadin.data.Item;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class RenovarContratoAcessoEditor  extends Window{
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
	
	
		
	
	public RenovarContratoAcessoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("841px");
		setHeight("356px");
		
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
		
		

		final Cliente ca = (Cliente)item.getItemProperty("cliente").getValue();
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				TextField txtCliente = new TextField("Cliente");
				txtCliente.setValue(ca.getNome_razao());
				txtCliente.setReadOnly(true);
				txtCliente.setStyleName("caption-align-renovar-acesso");
				txtCliente.setWidth("360px");				
				
				addComponent(txtCliente);	
				
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
		vlRoot.addComponent(new HorizontalLayout(){
			{
						addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);				
											
							TextField tfPlanoAtual = new TextField("Plano Atual");
							tfPlanoAtual.setValue(pa.getNome());
							tfPlanoAtual.setReadOnly(true);
							tfPlanoAtual.setWidth("250px");
							tfPlanoAtual.setStyleName("caption-align-renovar-acesso");
							
							addComponent(tfPlanoAtual);	
								
						}
					});
						
					addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);				
											
							TextField tfUpDownPlanoAtual = new TextField("Upload/Download");
							tfUpDownPlanoAtual.setValue(pa.getRate_limit());
							tfUpDownPlanoAtual.setReadOnly(true);
							tfUpDownPlanoAtual.setWidth("100px");
							tfUpDownPlanoAtual.setStyleName("caption-align-renovar-acesso");
							
							addComponent(tfUpDownPlanoAtual);	
								
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
		
		
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
						addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);				
											
							TextField tfPlanoRenovacao = new TextField("Plano Renovação");
							tfPlanoRenovacao.setValue(pa.getPlano_Renovacao() != null ? pa.getPlano_Renovacao().getNome() : pa.getNome());
							tfPlanoRenovacao.setReadOnly(true);
							tfPlanoRenovacao.setWidth("250px");
							tfPlanoRenovacao.setStyleName("caption-align-renovar-acesso");
							
							addComponent(tfPlanoRenovacao);	
								
						}
					});
						
					addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);				
											
							TextField tfUpDownPlanoRenovacao = new TextField("Upload/Download");
							tfUpDownPlanoRenovacao.setValue(pa.getPlano_Renovacao() != null ? pa.getPlano_Renovacao().getRate_limit() : pa.getRate_limit());
							tfUpDownPlanoRenovacao.setReadOnly(true);
							tfUpDownPlanoRenovacao.setWidth("100px");
							tfUpDownPlanoRenovacao.setStyleName("caption-align-renovar-acesso");
							
							addComponent(tfUpDownPlanoRenovacao);	
								
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
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
									
					TextField tfValorPlano = new TextField("Valor do Plano (R$)");
					tfValorPlano.setValue(pa.getPlano_Renovacao() != null ? pa.getPlano_Renovacao().getValor() : pa.getValor());
					tfValorPlano.setReadOnly(true);
					tfValorPlano.setWidth("250px");
					tfValorPlano.setStyleName("caption-align-renovar-acesso");
					
					addComponent(tfValorPlano);	
						
				}
			});
		
		
		
		
		
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					ContasReceberDAO crDAO = new ContasReceberDAO();
					Integer codAcesso = (Integer)item.getItemProperty("id").getValue();					
					List<ContasReceber> resConta = crDAO.procurarTodosBoletosDoAcessoPorContrato(codAcesso);					
									
					DateField tfVencPlano = new DateField("Vencimento do Primeiro Boleto");
					
					if(resConta != null && resConta.size() > 0){
						ContasReceber ct = resConta.get(0);
						Date dtVenc = ct.getData_vencimento();
						DateTime dt2 = new DateTime(dtVenc).plusMonths(1);				
						tfVencPlano.setValue(dt2.toDate());
					}
					
					tfVencPlano.setReadOnly(true);
					tfVencPlano.setWidth("250px");
					tfVencPlano.setStyleName("caption-align-renovar-acesso");
					tfVencPlano.setDateFormat("dd/MM/yyyy");
					
					addComponent(tfVencPlano);	
						
				}
			});
		
			
		
	}
	
	private void concluirRenovacao(){
		
				try {										
					fireEvent(new RenovarContratoEvent(getUI(),true));					
				} catch (Exception e) {											
					Notification.show("ERRO: Não foi Possivel Salvar as Alterações!");		
					e.printStackTrace();
				}
			
		
	}

	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
					concluirRenovacao();
			 
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

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
				fireEvent(new RenovarContratoEvent(getUI(),false));				
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
	
	
	public void addListerner(RenovarContratoListerner target){
		try {
			Method method = RenovarContratoListerner.class.getDeclaredMethod("onClose", RenovarContratoEvent.class);
			addListener(RenovarContratoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(RenovarContratoListerner target){
		removeListener(RenovarContratoEvent.class, target);
	}
	public static class RenovarContratoEvent extends Event{
		
		
		private boolean confirm;
				
		public RenovarContratoEvent(Component source,boolean confirm) {
			
			super(source);			
			this.confirm = confirm;			
		}
				
		public boolean isConfirm() {
			return confirm;
		}	
		
		
	}
	public interface RenovarContratoListerner extends Serializable{
		public void onClose(RenovarContratoEvent event);
	}
}
