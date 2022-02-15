package com.digital.opuserp.view.modulos.financeiro.preferencias;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.CreditoCliente;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class DefinirLimiteCreditoEditor extends Window {

	
	String valor_credito;
	String valor_saldo;
	String valor_utilizado;
	String data_alteracao;
	
	Button btSalvar;
	Button btCancelar;
	Button btAplicar;
	
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
	
	EntityManager em;
	Integer codCliente;
	
	private TextField txtDisponivel;
	private TextField txtUtilizado;
	private TextField txtLimiteCredito;
	private TextField txtDataAlteracao;
	
	public DefinirLimiteCreditoEditor(Integer codCliente, String title, boolean modal){
				
		this.codCliente = codCliente;
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
			
		em = ConnUtil.getEntity();
		Query q = em.createQuery("select cc from CreditoCliente cc where cc.cliente = :cliente", CreditoCliente.class);
								  
		q.setParameter("cliente", new Cliente(codCliente));
		
		if(q.getResultList().size() == 1){
			CreditoCliente cc = (CreditoCliente)q.getSingleResult();
			
			valor_credito = Real.formatDbToString(cc.getLimite_credito());
			valor_saldo = Real.formatDbToString(cc.getSaldo());
			double utilzado = Real.formatStringToDBDouble(valor_credito) - Real.formatStringToDBDouble(valor_saldo);
			valor_utilizado = Real.formatDbToString(String.valueOf(utilzado));
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			data_alteracao = sdf.format(cc.getData_alteracao());
		}else{
			valor_utilizado = "0,00";
			valor_credito = "0,00";
			valor_saldo = "0,00";
		}
		
		
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
				hlButtons.setWidth("100%");
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);

				hlButtons.addComponent(buildBtAplicar());
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				
				addComponent(hlButtons);
				
				hlButtons.setExpandRatio(btAplicar, 1);
				hlButtons.setComponentAlignment(btAplicar, Alignment.BOTTOM_LEFT);
				hlButtons.setComponentAlignment(btCancelar, Alignment.BOTTOM_RIGHT);
				hlButtons.setComponentAlignment(btSalvar, Alignment.BOTTOM_RIGHT);
				
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
														
					txtLimiteCredito = new TextField ("Limite de Crédito(R$)");				
					txtLimiteCredito.setWidth("100%");					
					txtLimiteCredito.setRequired(true);		
					txtLimiteCredito.setStyleName("caption-align-credito-cliente-deflimitecredito");
					txtLimiteCredito.focus();
					txtLimiteCredito.setId("txtLimiteCredito");
					txtLimiteCredito.setImmediate(true);
					txtLimiteCredito.setTextChangeEventMode(TextChangeEventMode.LAZY);
					
					addComponent(txtLimiteCredito);
						
					JavaScript.getCurrent().execute("$('#txtLimiteCredito').maskMoney({decimal:',',thousands:'.'})");
					
					if(valor_credito != null){												
						txtLimiteCredito.setValue(valor_credito);
					}
					
					txtLimiteCredito.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							try{
								
								if(event.getText() != null && valor_utilizado != null && Real.formatStringToDBDouble(event.getText()) >= Real.formatStringToDBDouble(valor_utilizado)){
								
									double restante = Real.formatStringToDBDouble(event.getText()) - Real.formatStringToDBDouble(valor_credito);
									double disponivel = Real.formatStringToDBDouble(valor_saldo) + restante;
									
									txtDisponivel.setReadOnly(false);
									txtDisponivel.setValue(Real.formatDbToString(String.valueOf(disponivel)));
									txtDisponivel.setReadOnly(true);
									
									txtLimiteCredito.removeStyleName("invalid-txt");
								}else{
									txtLimiteCredito.addStyleName("invalid-txt");
								}
								
							}catch(Exception e){
								Notification.show("O Valor informado não Pode ser Validado!");
								e.printStackTrace();
							}
						}
					});
					
					txtLimiteCredito.addBlurListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
							
							if(txtLimiteCredito.getValue() == null){
								txtLimiteCredito.setValue(valor_utilizado);
								txtDisponivel.setReadOnly(false);
								txtDisponivel.setValue(Real.formatDbToString("0.00"));
								txtDisponivel.setReadOnly(true);
							}else{
//								 if(valor_utilizado != null &&  Real.formatStringToDBDouble(txtLimiteCredito.getValue()) < Real.formatStringToDBDouble(valor_utilizado))
//								 {
//									txtLimiteCredito.setValue(valor_utilizado);
//									txtDisponivel.setReadOnly(false);
//									txtDisponivel.setValue(Real.formatDbToString("0.00"));
//									txtDisponivel.setReadOnly(true);
//								 }
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
														
					txtUtilizado = new TextField ("Utilizado(R$)");				
					txtUtilizado.setWidth("100%");					
					txtUtilizado.setRequired(true);	
					txtUtilizado.setReadOnly(true);
					txtUtilizado.setStyleName("caption-align-credito-cliente-deflimitecredito");
					txtUtilizado.setId("txtUtilizado");
					addComponent(txtUtilizado);
					
					if(valor_saldo != null && valor_credito != null){	
						double utilizado = Real.formatStringToDBDouble(valor_credito) - Real.formatStringToDBDouble(valor_saldo);
						txtUtilizado.setReadOnly(false);
						txtUtilizado.setValue(Real.formatDbToString(String.valueOf(utilizado)));
						txtUtilizado.setReadOnly(true);
					}
						
					JavaScript.getCurrent().execute("$('#txtUtilizado').maskMoney({decimal:',',thousands:'.'})");		
					
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtDisponivel = new TextField ("Disponível(R$)");				
					txtDisponivel.setWidth("100%");					
					txtDisponivel.setRequired(true);	
					txtDisponivel.setReadOnly(true);
					txtDisponivel.setStyleName("caption-align-credito-cliente-deflimitecredito");
					txtDisponivel.setId("txtDisponivel");
					addComponent(txtDisponivel);
						
					JavaScript.getCurrent().execute("$('#txtDisponivel').maskMoney({decimal:',',thousands:'.'})");		
					if(valor_saldo != null){	
						txtDisponivel.setReadOnly(false);
						txtDisponivel.setValue(valor_saldo);
						txtDisponivel.setReadOnly(true);
					}
					
				}
			});
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					txtDataAlteracao = new TextField ("Alteração");				
					txtDataAlteracao.setWidth("100%");					
					txtDataAlteracao.setRequired(true);	
					txtDataAlteracao.setReadOnly(true);
					txtDataAlteracao.setStyleName("caption-align-credito-cliente-deflimitecredito");
					
					addComponent(txtDataAlteracao);
						
							
					if(data_alteracao != null){	
						txtDataAlteracao.setReadOnly(false);
						txtDataAlteracao.setValue(data_alteracao);
						txtDataAlteracao.setReadOnly(true);
					}
					
				}
			});
		
		
	}

	
	
	public Button buildBtAplicar() {
		
		btAplicar = new Button("Aplicar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//if(valor_credito && valor_limite != null){
					
					try {										
						
						Query q = em.createQuery("select cc from CreditoCliente cc where cc.cliente =:cliente", CreditoCliente.class);
						q.setParameter("cliente", new Cliente(codCliente));
						
						if(q.getResultList().size() > 0){
							if(q.getResultList().size() == 1){
								
								CreditoCliente cc = (CreditoCliente) q.getSingleResult();
								cc.setLimite_credito(Real.formatStringToDB(txtLimiteCredito.getValue()));
								cc.setSaldo(Real.formatStringToDB(txtDisponivel.getValue()));
														
								
								em.getTransaction().begin();
								em.merge(cc);
								em.getTransaction().commit();
							}
							
							if(q.getResultList().size() > 1){
								
								List<CreditoCliente> result = q.getResultList();
								em.getTransaction().begin();
								for(CreditoCliente cc:result){
									em.remove(cc);
								}
								
								em.persist(new CreditoCliente(null, new Cliente(codCliente), Real.formatStringToDB(valor_credito), Real.formatStringToDB(valor_saldo)));
								em.getTransaction().commit();							
							}
						}else{
							em.getTransaction().begin();							
							em.persist(new CreditoCliente(null, new Cliente(codCliente), Real.formatStringToDB(valor_credito), Real.formatStringToDB(valor_saldo)));
							em.getTransaction().commit();					
						}
						
						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
						
						txtDataAlteracao.setReadOnly(false);
						txtDataAlteracao.setValue(sdf.format(new Date()));
						txtDataAlteracao.setReadOnly(true);
										
						//fireEvent(new DefinirLimiteCreditoEvent(getUI(),true));					
						//Notification.show("Limite de Crédito foi Definido com Sucesso!");
						
						//close();
					} catch (Exception e) {					
						//LogErrorDAO.add(new LogError(null, "CategoriaEditor.java", "buildBtSalvar()", e.toString(),null,OpusERP4UI.EMPRESA_SELECIONADA, OpusERP4UI.USUARIO_LOGADO));					
						Notification.show("Não foi Possivel Salvar as Alterações!");
					}
				//}else{
				//	Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
				//}
				
			}
		});
		

		
		
		//btAplicar.setStyleName("default");
		return btAplicar;
	}
	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//if(valor_credito && valor_limite != null){
					
					try {										
						
						Query q = em.createQuery("select cc from CreditoCliente cc where cc.cliente =:cliente", CreditoCliente.class);
						q.setParameter("cliente", new Cliente(codCliente));
						
						if(q.getResultList().size() > 0){
							if(q.getResultList().size() == 1){
								
								CreditoCliente cc = (CreditoCliente) q.getSingleResult();
								cc.setLimite_credito(Real.formatStringToDB(txtLimiteCredito.getValue()));
								cc.setSaldo(Real.formatStringToDB(txtDisponivel.getValue()));
														
								
								em.getTransaction().begin();
								em.merge(cc);
								em.getTransaction().commit();
							}
							
							if(q.getResultList().size() > 1){
								
								List<CreditoCliente> result = q.getResultList();
								em.getTransaction().begin();
								for(CreditoCliente cc:result){
									em.remove(cc);
								}
								
								em.persist(new CreditoCliente(null, new Cliente(codCliente), Real.formatStringToDB(valor_credito), Real.formatStringToDB(valor_saldo)));
								em.getTransaction().commit();							
							}
						}else{
							em.getTransaction().begin();							
							em.persist(new CreditoCliente(null, new Cliente(codCliente), Real.formatStringToDB(valor_credito), Real.formatStringToDB(valor_saldo)));
							em.getTransaction().commit();					
						}
										
						fireEvent(new DefinirLimiteCreditoEvent(getUI(),true));					
						Notification.show("Limite de Crédito foi Definido com Sucesso!");
						
						close();
					} catch (Exception e) {					
						//LogErrorDAO.add(new LogError(null, "CategoriaEditor.java", "buildBtSalvar()", e.toString(),null,OpusERP4UI.EMPRESA_SELECIONADA, OpusERP4UI.USUARIO_LOGADO));					
						Notification.show("Não foi Possivel Salvar as Alterações!");
					}
				//}else{
				//	Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
				//}
				
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
		return btSalvar;
	}

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				close();
				
//				if(!fieldGroup.isModified()){
//					fieldGroup.discard();				
//					fireEvent(new CategoriaEvent(getUI(), item, false));
//					close();
//				}else{
//					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
//					gDialog.setCaptionCANCEL("Sair sem Salvar!");
//					gDialog.setCaptionOK("Salvar");
//					
//					gDialog.addListerner(new GenericDialog.DialogListerner() {
//						
//						@Override
//						public void onClose(DialogEvent event) {
//							if(event.isConfirm()){
//								try {
//									fieldGroup.commit();				
//									fireEvent(new CategoriaEvent(getUI(), item, true));									
//									if(item.getItemProperty("id").getValue() == null){
//										
//						fieldGroup.discard();				
//									Notification.show("Categoria Cadastrado com Sucesso!");
//									}else{								
//										Notification.show("Categoria Alterado com Sucesso!");
//									}
//									close();
//								} catch (Exception e) {
//									System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
//									Notification.show("Não foi Possivel Salvar as Alterações!");
//								}
//							}else{							
//								fieldGroup.discard();				
//								fireEvent(new TransportadoraEvent(getUI(), item, false));
//								close();						
//							}
//						}
//					});					
//					
//					getUI().addWindow(gDialog);
					
//				}				
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		
		
		return btCancelar;
	}
	
	
	public void addListerner(DefinirLimiteCreditoListerner target){
		try {
			Method method = DefinirLimiteCreditoListerner.class.getDeclaredMethod("onClose", DefinirLimiteCreditoEvent.class);
			addListener(DefinirLimiteCreditoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(DefinirLimiteCreditoListerner target){
		removeListener(DefinirLimiteCreditoEvent.class, target);
	}
	public static class DefinirLimiteCreditoEvent extends Event{

		private boolean confirm;
		
		public DefinirLimiteCreditoEvent(Component source, boolean confirm) {
			super(source);			
			this.confirm = confirm;			
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface DefinirLimiteCreditoListerner extends Serializable{
		public void onClose(DefinirLimiteCreditoEvent event);
	}

	
}
