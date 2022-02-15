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

public class MudarTitularidadeEditor extends Window {

	
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
	private Cliente fiador =null;
	
	private ComboBox cbEnderecos;
	
	public MudarTitularidadeEditor(Item item, String title, boolean modal){
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
		setHeight("270px");
		
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
				tfCodContrato.setValue(item.getItemProperty("id").getValue().toString());
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
				//setWidth("100%");
				
				JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
				containerClientes.addContainerFilter(Filters.eq("status", "INATIVO"));
				
				
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
					preencher = true;
				}
				
				tfCodCliente = new TextField("Novo Titular");				
				tfCodCliente.setRequired(true);	
				tfCodCliente.setWidth("60px");				
				tfCodCliente.setNullRepresentation("");
				tfCodCliente.setStyleName("caption-align");
				tfCodCliente.focus();
				tfCodCliente.setId("txtCodCliente");
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				tfCodCliente.setImmediate(true);
				tfCodCliente.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
									
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							Clientelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));		
							
							if(Clientelecionado != null){
								
								AcessoCliente ac = caDAO.getAcessoByCodAceso(codAcesso);
								
								if(ac.getCliente().getId().equals(Clientelecionado.getId())){
									validarCliente = false;
									
									tfDescricaoCliente.setReadOnly(false);
									tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
									tfDescricaoCliente.setReadOnly(true);
									
									cbEnderecos.setContainerDataSource(buildContainerEnderecos(Clientelecionado.getId()));								
									cbEnderecos.setReadOnly(false);
									cbEnderecos.setRequired(true);
									
									validarTitular = true;
									tfCodCliente.addStyleName("invalid-txt");
									
									Notification.show("Selicione um Cliente diferente!");
									
								}else if(!crDAO.allowNewContrato(Clientelecionado.getDoc_cpf_cnpj())){
									validarCliente = false;
							
									tfDescricaoCliente.setReadOnly(false);
									tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
									tfDescricaoCliente.setReadOnly(true);
									
									cbEnderecos.setContainerDataSource(buildContainerEnderecos(Clientelecionado.getId()));								
									cbEnderecos.setReadOnly(false);
									cbEnderecos.setRequired(true);
									
									validarTitular = true;
									tfCodCliente.addStyleName("invalid-txt");
									
									Notification.show("Cliente Possui Pendencias Finaceiras!");
									
								}else if(fiador != null && Clientelecionado.getId().equals(fiador.getId())){
									validarCliente = false;
							
									tfDescricaoCliente.setReadOnly(false);
									tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
									tfDescricaoCliente.setReadOnly(true);
									
									cbEnderecos.setContainerDataSource(buildContainerEnderecos(Clientelecionado.getId()));								
									cbEnderecos.setReadOnly(false);
									cbEnderecos.setRequired(true);
									
									validarTitular = true;
									tfCodCliente.addStyleName("invalid-txt");
									
									Notification.show("Cliente Não pode ser igual ao Fiador!");								
								
								}else{
									validarCliente = true;

									tfDescricaoCliente.setReadOnly(false);
									tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
									tfDescricaoCliente.setReadOnly(true);
									
									cbEnderecos.setContainerDataSource(buildContainerEnderecos(Clientelecionado.getId()));								
									cbEnderecos.setReadOnly(false);
									cbEnderecos.setRequired(true);
									
									validarTitular = true;
									tfCodCliente.removeStyleName("invalid-txt");
								}
				
							}else {							
								validarTitular = false;								
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue("");
								tfDescricaoCliente.setReadOnly(true);		
								
								cbEnderecos.setContainerDataSource(null);
								if(fieldGroup.getField("endereco") != null){
									fieldGroup.unbind(cbEnderecos);
								}
								cbEnderecos.setReadOnly(true);
							}
						}
					}		
					
				});
	
				tfDescricaoCliente = new TextField();
				tfDescricaoCliente.setReadOnly(true);
				tfDescricaoCliente.setWidth("410px");
			
							
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
									if(event.getCliente() != null ){
										Clientelecionado = event.getCliente();
																		
										if(Clientelecionado != null){
											
											AcessoCliente ac = caDAO.getAcessoByCodAceso(codAcesso);
											
											if(ac.getCliente().getId().equals(Clientelecionado.getId())){
												validarCliente = false;
												
												tfCodCliente.setValue(event.getCliente().getId().toString());
								
												tfDescricaoCliente.setReadOnly(false);
												tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
												tfDescricaoCliente.setReadOnly(true);
												
												cbEnderecos.setContainerDataSource(buildContainerEnderecos(Clientelecionado.getId()));								
												cbEnderecos.setReadOnly(false);
												cbEnderecos.setRequired(true);
												
												validarTitular = true;
												tfCodCliente.addStyleName("invalid-txt");
												
												Notification.show("Selicione um Cliente diferente!");
												
											}else if(!crDAO.allowNewContrato(Clientelecionado.getDoc_cpf_cnpj())){
												validarCliente = false;
										
												tfCodCliente.setValue(event.getCliente().getId().toString());
								
												tfDescricaoCliente.setReadOnly(false);
												tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
												tfDescricaoCliente.setReadOnly(true);
												
												cbEnderecos.setContainerDataSource(buildContainerEnderecos(Clientelecionado.getId()));								
												cbEnderecos.setReadOnly(false);
												cbEnderecos.setRequired(true);
												
												validarTitular = true;
												tfCodCliente.addStyleName("invalid-txt");
												
												Notification.show("Cliente Possui Pendencias Finaceiras!");
											}else{
												validarCliente = true;
												
												tfCodCliente.setValue(event.getCliente().getId().toString());
												
												tfDescricaoCliente.setReadOnly(false);
												tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
												tfDescricaoCliente.setReadOnly(true);
												
												cbEnderecos.setContainerDataSource(buildContainerEnderecos(Clientelecionado.getId()));								
												cbEnderecos.setReadOnly(false);
												cbEnderecos.setRequired(true);
												
												validarTitular = true;
												tfCodCliente.removeStyleName("invalid-txt");

										
											}
											
											
											
										}else {
											
											validarTitular = false;
											tfCodCliente.addStyleName("invalid-txt");
											tfCodCliente.setValue("");
											
											tfDescricaoCliente.setReadOnly(false);
											tfDescricaoCliente.setValue("");
											tfDescricaoCliente.setReadOnly(true);								
										}
										
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
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoCliente);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);	
			
			}	
		});
		
		vlRoot.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");

				cbEnderecos = new ComboBox(){
					@Override
					public String getItemCaption(Object itemId) {
					   Item item = getItem(itemId);
					
					   if (item == null) {
					      return "";
					   }
					     
					   return String.valueOf(item.getItemProperty("cep").getValue()) + ", " + 
					   		  String.valueOf(item.getItemProperty("endereco").getValue()) + ", " +
					   		  String.valueOf(item.getItemProperty("numero").getValue()) + ", " +
					   		  String.valueOf(item.getItemProperty("bairro").getValue()) + ", " +
					   		  String.valueOf(item.getItemProperty("cidade").getValue())+ " - " +
					   		  String.valueOf(item.getItemProperty("uf").getValue());
					}
				};
				
				cbEnderecos.setStyleName("caption-align-endTitular");
				cbEnderecos.setWidth("410px");
				cbEnderecos.setTextInputAllowed(false);
//				cbEnderecos.setRequired(true);
				cbEnderecos.setNullSelectionAllowed(false);
				//cbEnderecos.setConverter(new SingleSelectConverter(cbEnderecos));
				//cbEnderecos.setReadOnly(true);
				
								
				addComponent(cbEnderecos);
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
				
	}

	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				concluirMudancaTitularidade();
				
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
	
	private void concluirMudancaTitularidade(){
		if(cbEnderecos.getValue() != null && !cbEnderecos.getValue().toString().equals("") && validarCliente && fieldGroup.isValid() && !tfCodCliente.getValue().equals("") && tfCodCliente.getValue() != null && validarTitular){
			try {	
				
				fieldGroup.commit();
				item.getItemProperty("cliente").setValue(Clientelecionado);
				item.getItemProperty("endereco").setValue(new Endereco((Integer)cbEnderecos.getItem(cbEnderecos.getValue()).getItemProperty("id").getValue()));
					
				fireEvent(new MudarTitularidadeEvent(getUI(),true,item,(Integer) Clientelecionado.getId()));	
					
			} catch (Exception e) {											
				Notification.show("ERRO: Não foi Possivel Salvar as Alterações!");		
				e.printStackTrace();
				close();
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
	}

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(cbEnderecos.getValue() != null && !cbEnderecos.getValue().toString().equals("") && !fieldGroup.isModified() && tfCodCliente.getValue().equals("") || tfCodCliente.getValue() == null){									
					fireEvent(new MudarTitularidadeEvent(getUI(),false,null, null));	
					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
							concluirMudancaTitularidade();
								
							}else{										
								fireEvent(new MudarTitularidadeEvent(getUI(), false, null, null));											
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
	
	
	public void addListerner(MudarTitularidadeListerner target){
		try {
			Method method = MudarTitularidadeListerner.class.getDeclaredMethod("onClose", MudarTitularidadeEvent.class);
			addListener(MudarTitularidadeEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(MudarTitularidadeListerner target){
		removeListener(MudarTitularidadeEvent.class, target);
	}
	public static class MudarTitularidadeEvent extends Event{
		
		
		private boolean confirm;
		private Item item;
		private Integer codCliente;
		private Cliente Fiador;
		
		public MudarTitularidadeEvent(Component source,boolean confirm,Item item, Integer codCliente ) {
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
	public interface MudarTitularidadeListerner extends Serializable{
		public void onClose(MudarTitularidadeEvent event);
	}
}
