package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.FiadorDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.ClienteUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class AlterarFiadorEditor extends Window {

	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
	
	ComboBox cbPlanos;
	ComboBox cbExclusaoBoletos;
	Cliente Clientelecionado;
	Cliente FiadorSelecionado;
	
	Integer codCliente;
	Integer codFiador;
	
	FieldGroup fieldGroup;
	
	String loginAtual;	
	
	Integer codAcesso;	
	
//	boolean valid_usuario = false;
//	boolean valid_senha = false;
	boolean validarFiador = true;
	boolean validarTitular = false;
	boolean validarCliente = true;
	
	ContasReceberDAO crDAO = new ContasReceberDAO();
	CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
	
	private TextField tfCodCliente;
	private TextField tfDescricaoCliente;
	
	private TextField tfCodFiador;
	private TextField tfDescricaoFiador;
		
	private Cliente ca;
	private Cliente fiador = null;
	
	private ComboBox cbEnderecos;
	
	public AlterarFiadorEditor(Item item, String title, boolean modal){
		this.item = item;
		
		if (item.getItemProperty("login").getValue() != null) {
			loginAtual = item.getItemProperty("login").getValue().toString();
		}
		
		if (item.getItemProperty("id").getValue() != null) {
			codAcesso = (Integer)item.getItemProperty("id").getValue();
		}
		
		ca = (Cliente)item.getItemProperty("cliente").getValue();
		
		if (item.getItemProperty("fiador").getValue() != null && !item.getItemProperty("fiador").getValue().equals("")){
			fiador = (Cliente)item.getItemProperty("fiador").getValue();			
		}
		
		setWidth("745px");
		setHeight("245px");
		
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
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select e from Endereco e where  e.clientes = :cliente", Endereco.class);
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
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
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
				tfCodContrato.setStyleName("caption-align");
				
				addComponent(tfCodContrato);
			}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){					
			{
				final TextField tfCodAtual = new TextField("Titular Atual");
				tfCodAtual.setValue(ca.getId().toString());
				tfCodAtual.setWidth("60px");
				tfCodAtual.setReadOnly(true);
				tfCodAtual.setStyleName("caption-align");
								
				FormLayout flCodAtual = new FormLayout(){
					{	
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");								
						addComponent(tfCodAtual);							
					}
				};
				
				addComponent(flCodAtual);	
				
				final TextField tfContratoAtual = new TextField("-");
				tfContratoAtual.setValue(ca.getNome_razao());
				tfContratoAtual.setWidth("412px");
				tfContratoAtual.setReadOnly(true);
//				tfContratoAtual.setStyleName("caption-align");
								
				FormLayout flContratoAtual = new FormLayout(){
					{	
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");								
						addComponent(tfContratoAtual);							
					}
				};
				
				addComponent(flContratoAtual);		
			}
		});
		

		vlRoot.addComponent(new HorizontalLayout(){		
			{
								
				EntityManager em = ConnUtil.getEntity();
				
				final TextField tfCodFiadorAtual = new TextField("Fiador Atual");
				
				if(fiador != null){					
					tfCodFiadorAtual.setValue(fiador.getId().toString());
				}
				
				tfCodFiadorAtual.setWidth("60px");
				tfCodFiadorAtual.setReadOnly(true);
				tfCodFiadorAtual.setStyleName("caption-align");
				
				FormLayout flCodFiadorAtual = new FormLayout(){
					{	
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");								
						addComponent(tfCodFiadorAtual);							
					}
				};
				
				addComponent(flCodFiadorAtual);
				
				final TextField tfFiadorAtual = new TextField("-");
				
				if(fiador != null){
					tfFiadorAtual.setValue(fiador.getNome_razao());
				}
				
				tfFiadorAtual.setWidth("412px");
				tfFiadorAtual.setReadOnly(true);
				
				FormLayout flFiadorAtual = new FormLayout(){
					{	
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");								
						addComponent(tfFiadorAtual);							
					}
				};
				
				addComponent(flFiadorAtual);			
			}		
		});
				
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
				
				tfCodFiador = new TextField("Novo Fiador");				
				tfCodFiador.setWidth("60px");				
				tfCodFiador.setNullRepresentation("");
				tfCodFiador.setStyleName("caption-align");
				tfCodFiador.focus();
				tfCodFiador.setId("txtCodFiador");
				
				JavaScript.getCurrent().execute("$('#txtCodFiador').mask('0000000000')");
				
				tfCodFiador.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						FiadorSelecionado = new Cliente();
												
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							codFiador = Integer.parseInt(event.getText());
							FiadorSelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));									
							if(FiadorSelecionado != null){
								
								if(!crDAO.allowNewContrato(FiadorSelecionado.getDoc_cpf_cnpj())){
									validarFiador = false;
									tfCodFiador.addStyleName("invalid-txt");
								
									tfDescricaoFiador.setReadOnly(false);
									tfDescricaoFiador.setValue(FiadorSelecionado.getNome_razao());
									tfDescricaoFiador.setReadOnly(true);
									Notification.show("Fiador Possui Pendencias Finaceiras!");
									
								}else if(crDAO.allowNewContrato(FiadorSelecionado.getDoc_cpf_cnpj()) &&  ca.getId().equals(FiadorSelecionado.getId())){
									
									validarFiador = false;
									tfCodFiador.addStyleName("invalid-txt");
								
									tfDescricaoFiador.setReadOnly(false);
									tfDescricaoFiador.setValue(FiadorSelecionado.getNome_razao());
									tfDescricaoFiador.setReadOnly(true);
									Notification.show("Não é Possivel Escolher o Mesmo Cliente!");	
									
								}else{
									validarFiador = true;
									tfCodFiador.removeStyleName("invalid-txt");
									tfDescricaoFiador.setReadOnly(false);
									tfDescricaoFiador.setValue(FiadorSelecionado.getNome_razao());
									tfDescricaoFiador.setReadOnly(true);														
									}
									
							}else{
								FiadorSelecionado = null;
								tfDescricaoFiador.setReadOnly(false);
								tfDescricaoFiador.setValue("");
								tfDescricaoFiador.setReadOnly(true);
						}
					   }else{
						   FiadorSelecionado = null;
						   validarFiador = true;
						   tfDescricaoFiador.setReadOnly(false);
						   tfDescricaoFiador.setValue("");
						   tfDescricaoFiador.setReadOnly(true);
					   }
					  }
					});

					tfCodFiador.setRequired(false);		
					tfDescricaoFiador = new TextField();
					tfDescricaoFiador.setReadOnly(true);
					tfDescricaoFiador.setWidth("410px");
						
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						ClienteUtil cUtil = new ClienteUtil(true, true,"INATIVO");
						cUtil.addListerner(new ClienteUtil.ClienteListerner() {
							
							@Override
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
								
								if(event.getCliente()!=null && !event.getCliente().equals("")){
													
									FiadorSelecionado = event.getCliente();									
									if(FiadorSelecionado != null){
										
										if(!crDAO.allowNewContrato(FiadorSelecionado.getDoc_cpf_cnpj())){
											validarFiador = false;
											tfCodFiador.addStyleName("invalid-txt");
										
											tfDescricaoFiador.setReadOnly(false);
											tfDescricaoFiador.setValue(FiadorSelecionado.getNome_razao());
											tfDescricaoFiador.setReadOnly(true);
																					
											tfCodFiador.setValue(FiadorSelecionado.getId().toString());
											
											
											Notification.show("Fiador Possui Pendencias Finaceiras!");
											
										}else if(crDAO.allowNewContrato(FiadorSelecionado.getDoc_cpf_cnpj()) &&  ca.getId().equals(FiadorSelecionado.getId())){
											
											validarFiador = false;
											tfCodFiador.addStyleName("invalid-txt");
										
											tfDescricaoFiador.setReadOnly(false);
											tfDescricaoFiador.setValue(FiadorSelecionado.getNome_razao());
											tfDescricaoFiador.setReadOnly(true);
											tfCodFiador.setValue(FiadorSelecionado.getId().toString());
											Notification.show("Não é Possivel Escolher o Mesmo Cliente!");	
											
										}else{
											validarFiador = true;
											tfCodFiador.removeStyleName("invalid-txt");
											tfDescricaoFiador.setReadOnly(false);
											tfDescricaoFiador.setValue(FiadorSelecionado.getNome_razao());
											tfDescricaoFiador.setReadOnly(true);		
											tfCodFiador.setValue(FiadorSelecionado.getId().toString());
											}
											
									}else{
										tfDescricaoFiador.setReadOnly(false);
										tfDescricaoFiador.setValue("");
										tfDescricaoFiador.setReadOnly(true);
								}
							   }else{
								   validarFiador = true;
								   tfDescricaoFiador.setReadOnly(false);
								   tfDescricaoFiador.setValue("");
								   tfDescricaoFiador.setReadOnly(true);
							   }	
							}
						});
						
						getUI().addWindow(cUtil);
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
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchCliente);							
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
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescFiador);
				setExpandRatio(frmDescFiador, 1);	
			
			}	
		});

	
	}

	private void concluirAlteracao(){
		if(validarFiador){
			try {	
				
				final FiadorDAO fDAO = new FiadorDAO();
				
				if(tfDescricaoFiador.getValue().equals("") || tfDescricaoFiador.getValue() == null){						

					if(fiador!=null){
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Remover o Fiador?", true, true);
						gDialog.setCaptionCANCEL("Não");
						gDialog.setCaptionOK("Sim");
						
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									if(fieldGroup.isValid() && validarFiador){
										try {		
											fieldGroup.commit();
											item.getItemProperty("fiador").setValue(FiadorSelecionado);
											fireEvent(new MudarFiadorEvent(getUI(),true,item,null));													
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
										    
										    if(!tfCodCliente.isValid()|| !validarCliente){
										    	tfCodCliente.addStyleName("invalid-txt");
										    }else{
										    	tfCodCliente.removeStyleName("invalid-txt");  	
										    }

										    if(!cbEnderecos.isValid()|| !validarCliente){
										    	cbEnderecos.addStyleName("invalid-txt");
										    }else{
										    	cbEnderecos.removeStyleName("invalid-txt");  	
										    }
										Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
									}
								}else{										
									fireEvent(new MudarFiadorEvent(getUI(), false, null, null));					
								}
							}
						});					
						getUI().addWindow(gDialog);							
					}else{
						fireEvent(new MudarFiadorEvent(getUI(), false, null, null));								
					}

				}else{	
					fieldGroup.commit();
					item.getItemProperty("fiador").setValue(FiadorSelecionado);
					
					fireEvent(new MudarFiadorEvent(getUI(),true,item,(Integer) FiadorSelecionado.getId()));	
					
				}
			} catch (Exception e) {											
				Notification.show("ERRO: Não foi Possivel Salvar as Alterações!");		
				e.printStackTrace();						
			}
		}else{
			Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
		}
	}
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {			
				concluirAlteracao();				
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

					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								concluirAlteracao();
							}else{										
								fireEvent(new MudarFiadorEvent(getUI(), false, null, null));								
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
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
	
	
	public void addListerner(MudarFiadorListerner target){
		try {
			Method method = MudarFiadorListerner.class.getDeclaredMethod("onClose", MudarFiadorEvent.class);
			addListener(MudarFiadorEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(MudarFiadorListerner target){
		removeListener(MudarFiadorEvent.class, target);
	}
	public static class MudarFiadorEvent extends Event{
		
		
		private boolean confirm;
		private Item item;
		private Integer codCliente;
		private Cliente Fiador;
		
		public MudarFiadorEvent(Component source,boolean confirm,Item item, Integer codCliente ) {
			super(source);			
			this.confirm = confirm;
			this.item = item;
			this.codCliente = codCliente;
						
		}

		public Item getItem(){	
			return item;
		}

		public boolean isConfirm() {
			return confirm;
		}	
		public Integer getCodCliente(){
			return codCliente;
		}
		
	}
	public interface MudarFiadorListerner extends Serializable{
		public void onClose(MudarFiadorEvent event);
	}
}

