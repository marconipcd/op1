package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.EmpresaDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.client.ui.calendar.schedule.DateUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
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
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class NovoCadastroAcessoEditor extends Window implements GenericEditor {

		
	private static final long serialVersionUID = 1L;
	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	JPAContainer<PlanoAcesso> containerPlanos;
	ComboBox cbPlanos;
	ComboBox cbContrato;
	
	boolean validarFiador = true;
	boolean validarCliente = true;
	boolean validarIdade = true;
	boolean validarEndereco = true;
	
	ContasReceberDAO crDAO = new ContasReceberDAO();
	
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
	
	
	TextField tfUploadDownload;
	TextField txtInstGratis;
	TextField txtPermiteServManuten;
	ComboBox cbInstalaçãoGratis;
	ComboBox cbContratoPrestServManuten;
	
	private ComboBox cbEnderecos;
	
	public NovoCadastroAcessoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("822px");
		setHeight("457px");
		
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
	
	private JPAContainer<PlanoAcesso> buildContainerPlanos(){
		containerPlanos = JPAContainerFactory.make(PlanoAcesso.class, ConnUtil.getEntity());
		containerPlanos.addContainerFilter(Filters.eq("status", "ATIVO"));
		containerPlanos.sort(new Object[]{"nome"}, new boolean[]{true});

		if(fieldGroup.getField("contrato") != null){
			Item selectedContrato = ((ComboBox) fieldGroup.getField("contrato")).getItem(((ComboBox) fieldGroup.getField("contrato")).getValue());
			if(selectedContrato != null){				
				containerPlanos.addContainerFilter(Filters.eq("contrato_acesso", 
						new ContratosAcesso(Integer.parseInt(selectedContrato.getItemProperty("id").getValue().toString()))));
				containerPlanos.addContainerFilter(Filters.not(Filters.eq("nome", "BLOQUEADO")));
				containerPlanos.addContainerFilter(Filters.eq("status", "ATIVO"));
			}
		}
		
		return containerPlanos;
	}
	
	public String getNextId() {
		CredenciaisAcessoDAO caDao = new CredenciaisAcessoDAO();
		return caDao.getNextID();
	}
	
//	public Label buildLbRegistros(){
//		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
//		return lbRegistros;
//	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		//Cod
		vlRoot.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");

				TextField tfCod = new TextField("Número de Contrato");

				if (item.getItemProperty("id").getValue() != null) {
					fieldGroup.bind(tfCod, "id");
				} else {
					tfCod.setValue(getNextId());
				}
	
				tfCod.setWidth("60px");
				tfCod.setReadOnly(true);
				tfCod.setStyleName("caption-align-acesso");
				tfCod.setNullRepresentation("");
				addComponent(tfCod);
				setExpandRatio(tfCod, 2);

			}
		});
		//Cliente
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
								Notify.Show("Fiador Inválido", Notify.TYPE_ERROR);
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
		
						Clientelecionado = new Cliente();
						
						//somente numeros contaas receber
						
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							codCliente = Integer.parseInt(event.getText());
							
							ClienteDAO cDAO = new ClienteDAO();
							
							Clientelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));		
							
							if(Clientelecionado != null){
								
								
								DateTime dt1 = new DateTime();
								DateTime dt2 = new DateTime(Clientelecionado.getData_nascimento());
								
								int ano1 = dt1.getYear();
								int ano2 = dt2.getYear();
								
								int mes1 = dt1.getMonthOfYear();
								int mes2 = dt2.getMonthOfYear();
								
								int idade = ano1-ano2;
								
								if(mes1 < mes2){
									idade = idade - 1;
								}
								
								if(idade < 18){
									validarIdade =false;
								}
								
								if(!crDAO.allowNewContrato(Clientelecionado.getDoc_cpf_cnpj()) || Clientelecionado.getBlack_list().equals("SIM") || !validarIdade){
									validarCliente = false;
									
									tfDescricaoCliente.setReadOnly(false);
									tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
									tfDescricaoCliente.setReadOnly(true);
									
									cbEnderecos.setContainerDataSource(buildContainerEnderecos(codCliente));	
									cbEnderecos.setReadOnly(false);
									cbEnderecos.setRequired(true);
									tfCodCliente.addStyleName("invalid-txt");
									
									Notify.Show("CADASTRO NÃO APROVADO", Notify.TYPE_ERROR);
									
									if(!validarIdade){
										Notify.Show("Informe um cliente com pelo menos 18 anos.", Notify.TYPE_ERROR);	
									}
								}else{
									validarCliente = true;
								
									tfDescricaoCliente.setReadOnly(false);
									tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
									tfDescricaoCliente.setReadOnly(true);
									
									cbEnderecos.setContainerDataSource(buildContainerEnderecos(codCliente));								
									cbEnderecos.setReadOnly(false);
									cbEnderecos.setRequired(true);
									tfCodCliente.removeStyleName("invalid-txt");
								}
								
							}else {
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue("");
								tfDescricaoCliente.setReadOnly(true);
								
								cbEnderecos.setContainerDataSource(null);
								if(fieldGroup.getField("endereco") != null){
									fieldGroup.unbind(cbEnderecos);
								}
								cbEnderecos.setReadOnly(true);
								
							}
						}else{
							tfDescricaoCliente.setReadOnly(false);
							tfDescricaoCliente.setValue("");
							tfDescricaoCliente.setReadOnly(true);	
							cbEnderecos.setReadOnly(true);
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
				btSearchCliente.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						ClienteUtil cUtil = new ClienteUtil(true, true,"INATIVO");
						cUtil.addListerner(new ClienteUtil.ClienteListerner() {
							
							@Override
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
									if(event.getCliente() != null ){

										if(!crDAO.allowNewContrato(event.getCliente().getDoc_cpf_cnpj()) || event.getCliente().getBlack_list() != null &&  event.getCliente().getBlack_list().equals("SIM")){
											validarCliente = false;
											
											tfCodCliente.setValue(event.getCliente().getId().toString());
											tfDescricaoCliente.setReadOnly(false);
											tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
											tfDescricaoCliente.setReadOnly(true);
											
											tfCodCliente.addStyleName("invalid-txt");
											
											cbEnderecos.setContainerDataSource(buildContainerEnderecos(event.getCliente().getId()));
											//fieldGroup.bind(cbEnderecos, "endereco");
											cbEnderecos.setReadOnly(false);
											cbEnderecos.setRequired(true);
											
											
											Notify.Show("CADASTRO NÃO APROVADO", Notify.TYPE_ERROR);
											
										}else{
											validarCliente = true;
											
											tfCodCliente.setValue(event.getCliente().getId().toString());
											tfDescricaoCliente.setReadOnly(false);
											tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
											tfDescricaoCliente.setReadOnly(true);
											Clientelecionado = event.getCliente();
											codCliente = Integer.parseInt((event.getCliente().getId().toString()));
											
											cbEnderecos.setContainerDataSource(buildContainerEnderecos(event.getCliente().getId()));
											//fieldGroup.bind(cbEnderecos, "endereco");
											cbEnderecos.setReadOnly(false);
											cbEnderecos.setRequired(true);
											 
											tfCodCliente.removeStyleName("invalid-txt");
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
						setStyleName("form-cutom-new");		
												
						addComponent(tfCodCliente);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");										
						addComponent(btSearchCliente);							
					}
				}; 
							
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
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
		//Endereco
		vlRoot.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");

				cbEnderecos = new ComboBox("Endereço"){
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
				
				cbEnderecos.setStyleName("caption-align-acesso");
				cbEnderecos.setWidth("542px");
				cbEnderecos.setRequired(true);
				cbEnderecos.setNullSelectionAllowed(false);
				cbEnderecos.setTextInputAllowed(false); 	
				
				cbEnderecos.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						//Procurar Todos os Boletos NEGATIVADOS referente a contratos						
						//Verificar se o endereço escolhido é o mesmo do endereço utilizado no contrato vinculado ao boleto
						
						boolean check = true;
						
						if(cbEnderecos.getValue() != null && 
								cbEnderecos.getItem(cbEnderecos.getValue()).getItemProperty("endereco").getValue() != null && 
								cbEnderecos.getItem(cbEnderecos.getValue()).getItemProperty("bairro").getValue() != null &&
								cbEnderecos.getItem(cbEnderecos.getValue()).getItemProperty("numero").getValue() != null && 
								cbEnderecos.getItem(cbEnderecos.getValue()).getItemProperty("cidade").getValue() != null){
							
							check = ContasReceberDAO.checkBoletoVencidoPorEndereco(
									cbEnderecos.getItem(cbEnderecos.getValue()).getItemProperty("endereco").getValue().toString(), 
									cbEnderecos.getItem(cbEnderecos.getValue()).getItemProperty("bairro").getValue().toString(), 
									cbEnderecos.getItem(cbEnderecos.getValue()).getItemProperty("numero").getValue().toString(),
									cbEnderecos.getItem(cbEnderecos.getValue()).getItemProperty("cidade").getValue().toString());							
						}
												
						if(check){
							validarEndereco = false;
							Notify.Show("ENDEREÇO NÃO APROVADO!", Notify.TYPE_WARNING);
							cbEnderecos.addStyleName("invalid-txt");
						}else{
							validarEndereco = true;
							cbEnderecos.removeStyleName("invalid-txt");
							Notify.Show("ENDEREÇO APROVADO!", Notify.TYPE_SUCCESS);
						}
					}
				});
								
				addComponent(cbEnderecos);
			}
		});
		//Fiador
		vlRoot.addComponent(new HorizontalLayout(){
			{
				//setWidth("100%");
				
				JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));

				
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
								
								if(!crDAO.allowNewContrato(Fiadorlecionado.getDoc_cpf_cnpj())){
									validarFiador = false;
									tfCodFiador.addStyleName("invalid-txt");
									
									tfDescricaoFiador.setReadOnly(false);
									tfDescricaoFiador.setValue(Fiadorlecionado.getNome_razao());
									tfDescricaoFiador.setReadOnly(true);
									
									
									Notify.Show("FIADOR NÃO APROVADO", Notify.TYPE_ERROR);
								}else{							
									validarFiador = true;
									tfCodFiador.removeStyleName("invalid-txt");
									
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
				}
				});
				
				
				tfCodFiador.setRequired(false);		
				
				tfDescricaoFiador = new TextField();
				tfDescricaoFiador.setReadOnly(true);
				tfDescricaoFiador.setWidth("450px");
				tfDescricaoFiador.setTabIndex(40000);
							
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
									
									if(!crDAO.allowNewContrato(event.getCliente().getDoc_cpf_cnpj())){
										validarFiador = false;										
										tfCodFiador.addStyleName("invalid-txt");

										tfCodFiador.setValue(event.getCliente().getId().toString());
										tfDescricaoFiador.setReadOnly(false);
										tfDescricaoFiador.setValue(event.getCliente().getNome_razao());
										tfDescricaoFiador.setReadOnly(true);
										Fiadorlecionado = event.getCliente();
									
										
										Notify.Show("FIADOR NÃO APROVADO", Notify.TYPE_ERROR);
										codFiador = Integer.parseInt((event.getCliente().getId().toString()));
									}else{
										validarFiador = true;
										tfCodFiador.removeStyleName("invalid-txt");
										
										tfCodFiador.setValue(event.getCliente().getId().toString());
										tfDescricaoFiador.setReadOnly(false);
										tfDescricaoFiador.setValue(event.getCliente().getNome_razao());
										tfDescricaoFiador.setReadOnly(true);
										Fiadorlecionado = event.getCliente();
										
									}
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
									Notify.Show("FIADOR INVALIDO", Notify.TYPE_ERROR);
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
		//Tipo de Plano
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					JPAContainer<ContratosAcesso> containerContratos = JPAContainerFactory.make(ContratosAcesso.class, ConnUtil.getEntity());
					containerContratos.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
					containerContratos.addContainerFilter(Filters.eq("status", "ATIVO"));
					containerContratos.sort(new Object[]{"nome"}, new boolean[]{true});
					
					cbContrato = new ComboBox("Tipo de Plano", containerContratos);
					cbContrato.setItemCaptionPropertyId("nome");
					cbContrato.setConverter(new SingleSelectConverter(cbContrato));
					cbContrato.setStyleName("caption-align-acesso");
					cbContrato.setNullSelectionAllowed(false);
					cbContrato.setTextInputAllowed(false); 
					cbContrato.setRequired(true);
					cbContrato.setImmediate(true);
					cbContrato.setWidth("541px");
					cbContrato.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(fieldGroup != null && fieldGroup.getField("plano") != null){
								
								
								
								if(tfUploadDownload != null){
									tfUploadDownload.setReadOnly(false);
									tfUploadDownload.setValue("");
									tfUploadDownload.setReadOnly(true);
								}
								
								cbPlanos.setContainerDataSource(buildContainerPlanos());
								cbPlanos.setConverter(new SingleSelectConverter(cbPlanos));
								
								contrato = cbContrato.getItem(cbContrato.getValue()).getItemProperty("tipo_contrato").getValue().toString();
								
//								tfRegime.setReadOnly(false);
//								tfRegime.setValue(cbContrato.getItem(cbContrato.getValue()).getItemProperty("regime").getValue().toString());
//								tfRegime.setReadOnly(true);
								
								
								Float vlContrato = Float.parseFloat((cbContrato.getItem(cbContrato.getValue()).getItemProperty("valor_desconto").getValue().toString().replace(".", "").replace(",", ".")));
								Float vlAdesao = Float.parseFloat((cbContrato.getItem(cbContrato.getValue()).getItemProperty("valor_adesao").getValue().toString().replace(".", "").replace(",", ".")));
								
								Float total = vlAdesao - vlContrato;
								
								
								
//								dfDataBoleto.setReadOnly(false);
//								dfDataBoleto.setValue(null);
								//dfDataInstalacao.setValue(null);
								
								
								
//								if(contrato.equals("PRE-PAGO")){
//									dfDataBoleto.setReadOnly(false);
//									
//									if(dfDataInstalacao.getValue() != null){
//										dfDataBoleto.setValue(dfDataInstalacao.getValue());
//									}
//									
//									dfDataBoleto.setReadOnly(true);
//								}else{
//									dfDataBoleto.setReadOnly(false);									
//								}
							}
						}
					});
										
					addComponent(cbContrato);
					
					fieldGroup.bind(cbContrato,"contrato");
				}
			});
		//Regime
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
									
					ComboBox cbRegime = new ComboBox("Regime");
					cbRegime.setNullSelectionAllowed(false);
					cbRegime.setTextInputAllowed(false); 
					cbRegime.addItem("PROPRIO");
					cbRegime.addItem("PROPRIO (PARCIAL)");
					cbRegime.addItem("COMODATO");
					cbRegime.addItem("COMODATO (TOTAL)");
					
					cbRegime.setStyleName("caption-align-acesso");					
					cbRegime.setRequired(true);
					cbRegime.setWidth("241px");
															
					addComponent(cbRegime);	
					fieldGroup.bind(cbRegime, "regime");
					
				}
		});
		//Plano
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
															
					cbPlanos = new ComboBox("Plano de Acesso");
					cbPlanos.setItemCaptionPropertyId("nome");
					cbPlanos.addStyleName("caption-align-acesso-novo-plano");
					cbPlanos.setNullSelectionAllowed(false);
					cbPlanos.setTextInputAllowed(false); 
					cbPlanos.setRequired(true);
					cbPlanos.setWidth("300px");
					cbPlanos.setImmediate(true);
					
					cbPlanos.addListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							
							
									String permiteServManu = cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("permitir_servico_manutencao").getValue() != null ? cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("permitir_servico_manutencao").toString() : "NAO";
									
									txtPermiteServManuten.setReadOnly(false);
									txtPermiteServManuten.setValue(permiteServManu);
									txtPermiteServManuten.setReadOnly(true);
									
									if(permiteServManu.equals("NAO")){
										cbContratoPrestServManuten.select("NAO");
										cbContratoPrestServManuten.setReadOnly(true);
									}else{
										cbContratoPrestServManuten.setReadOnly(false);
									}
						
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
									
									if(codContrato != null && codPlano != null && dataInstalacao != null && dataPrimeiroBoleto != null){
										CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
										String valorBoleto = caDAO.calcularValorPrimeiroBoleto(codContrato, codPlano, dataInstalacao, dataPrimeiroBoleto);									
									}
							
									if(fieldGroup != null && fieldGroup.getField("plano") != null){

										Float vlContrato = Float.parseFloat((cbContrato.getItem(cbContrato.getValue()).getItemProperty("valor_desconto").getValue().toString().replace(".", "").replace(",", ".")));
										Float vlAdesao = Float.parseFloat((cbContrato.getItem(cbContrato.getValue()).getItemProperty("valor_adesao").getValue().toString().replace(".", "").replace(",", ".")));
										
										Float total = vlAdesao - vlContrato;
									}

									//Preenche RateLimit e Valor do Plano
									if(cbPlanos.getValue() != null){
										String instGratis = cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("instalacao_gratis").toString() != null ? cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("instalacao_gratis").toString() : "NAO";
										
										if(instGratis.equals("SIM")){
											txtInstGratis.setReadOnly(false);
											txtInstGratis.setValue(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("instalacao_gratis").toString());
											txtInstGratis.setReadOnly(true);

										}else{
											txtInstGratis.setReadOnly(false);
											txtInstGratis.setValue("NAO");
											txtInstGratis.setReadOnly(true);
										}
										
										tfUploadDownload.setReadOnly(false);
										tfUploadDownload.setValue(cbPlanos.getItem(cbPlanos.getValue()).getItemProperty("rate_limit").toString());
										tfUploadDownload.setReadOnly(true);
									}
								}
							});

					txtInstGratis = new TextField("Permite Inst. Grátis");
					txtInstGratis.setStyleName("caption-align-acesso");				
					txtInstGratis.setReadOnly(true);
					txtInstGratis.setWidth("70px");
					
					txtPermiteServManuten = new TextField("Permite Serv. Manuten.");
					txtPermiteServManuten.setStyleName("caption-align-acesso");				
					txtPermiteServManuten.setReadOnly(true);
					txtPermiteServManuten.setWidth("70px");
					
					HorizontalLayout hlPlanos = new HorizontalLayout();
					hlPlanos.setMargin(false);
					hlPlanos.setSpacing(false);
					hlPlanos.addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);
							
							addComponent(cbPlanos);
						}});
					
					hlPlanos.addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);
							
							addComponent(txtInstGratis);
						}});
				
					addComponent(hlPlanos);
					
					fieldGroup.bind(cbPlanos,"plano");
	
				}
			});
		//Inst Gratis
		vlRoot.addComponent(	new FormLayout(){{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);										
									
					cbInstalaçãoGratis = new ComboBox("Instalação Grátis");
					cbInstalaçãoGratis.setRequired(true); 
					cbInstalaçãoGratis.setNullSelectionAllowed(false);
					cbInstalaçãoGratis.setStyleName("caption-align-acesso");
					
					cbInstalaçãoGratis.addItem("SIM");
					cbInstalaçãoGratis.addItem("NAO");
					
															
					addComponent(cbInstalaçãoGratis);			
					
					//cbInstalaçãoGratis.setEnabled(false); 
				}
		});
		//Upload - Download		
		vlRoot.addComponent(	new FormLayout(){		{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);										
									
					tfUploadDownload = new TextField("Upload / Download");
					tfUploadDownload.setStyleName("caption-align-acesso");				
					tfUploadDownload.setReadOnly(true);
															
					addComponent(tfUploadDownload);					
				}
		});
		//Prestação de Serviço de Manutenção
		vlRoot.addComponent(	new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);										
								
				cbContratoPrestServManuten = new ComboBox("Serviço de Manutenção");
				cbContratoPrestServManuten.setRequired(true); 
				cbContratoPrestServManuten.setNullSelectionAllowed(false);
				cbContratoPrestServManuten.addStyleName("caption-align-acesso-novo-plano");			
				cbContratoPrestServManuten.addItem("SIM");
				cbContratoPrestServManuten.addItem("NAO");
				cbContratoPrestServManuten.select("NAO");
				
				HorizontalLayout hlPlanos = new HorizontalLayout();
				hlPlanos.setMargin(false);
				hlPlanos.setSpacing(false);
				hlPlanos.addComponent(new FormLayout(){					
					{
						//70
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						addComponent(cbContratoPrestServManuten);
					}});
				
				hlPlanos.addComponent(new FormLayout(){					
					{
						//30
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						addComponent(txtPermiteServManuten);
					}});
			
				addComponent(hlPlanos);
				
				//addComponent(cbContratoPrestServManuten);			
				
				//cbInstalaçãoGratis.setEnabled(false); 
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
					fireEvent(new NovoCadastroEvent(getUI(), item, false));					
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
								fireEvent(new NovoCadastroEvent(getUI(), item, false));
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
		
		if(tfCodCliente.getValue() != null && !tfCodCliente.getValue().equals("") && tfCodFiador.getValue() != null && !tfCodFiador.getValue().equals("")){
			
			int codFornecedor = Integer.parseInt(tfCodFiador.getValue());
			Cliente fiador = ClienteDAO.find(codFornecedor);
			
			if(codCliente.equals(codFiador)||!crDAO.allowNewContrato(fiador.getDoc_cpf_cnpj())){
				
				Notify.Show("FIADOR INVALIDO", Notify.TYPE_ERROR);
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
							
		if(validarEndereco && validarCliente && fieldGroup.isValid() && validarFiador && cbInstalaçãoGratis.getValue() != null && 
				cbContratoPrestServManuten.getValue() != null ){
			try {		
				
				fieldGroup.commit();		
				item.getItemProperty("cliente").setValue(Clientelecionado);
				item.getItemProperty("fiador").setValue(Fiadorlecionado);
//				item.getItemProperty("data_instalacao").setValue(dataInstalacao);
				item.getItemProperty("instalacao_gratis").setValue(cbInstalaçãoGratis.getValue() != null ? cbInstalaçãoGratis.getValue().toString() : "NAO");				
				item.getItemProperty("endereco").setValue(new Endereco((Integer)cbEnderecos.getItem(cbEnderecos.getValue()).getItemProperty("id").getValue()));
				item.getItemProperty("prest_serv_manutecao").setValue(cbContratoPrestServManuten.getValue());
				
				fireEvent(new NovoCadastroEvent(getUI(), item, true));

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
			  
			    
			    if(!tfCodCliente.isValid()|| !validarCliente){
			    	tfCodCliente.addStyleName("invalid-txt");
			    }else{
			    	tfCodCliente.removeStyleName("invalid-txt");  	
			    }
			    
			    if(!tfCodCliente.isValid()|| !validarFiador){
			    	tfCodFiador.addStyleName("invalid-txt");
			    }else{
			    	tfCodFiador.removeStyleName("invalid-txt");  	
			    }
			    
			    
			    if(!cbEnderecos.isValid()  || !validarEndereco){
			    	if(cbEnderecos != null ){
			    		cbEnderecos.addStyleName("invalid-txt");
			    	}
			    	
			    	if(!validarEndereco){
			    		Notify.Show("ENDEREÇO NÃO APROVADO!", Notify.TYPE_WARNING);
			    	}
			    }else{					    	
			    	if(cbEnderecos != null ){
			    		cbEnderecos.removeStyleName("invalid-txt");
			    	}
			    }
			    
//			    if(dfDataInstalacao.getValue() == null){
//			    	dfDataInstalacao.addStyleName("invalid-date-txt");
//			    }else{
//			    	dfDataInstalacao.removeStyleName("invalid-date-txt");
//			    }
//			    
//			    if(dfDataBoleto.getValue() == null){
//			    	dfDataBoleto.addStyleName("invalid-date-txt");
//			    }else{
//			    	dfDataBoleto.removeStyleName("invalid-date-txt");
//			    }

			    Notify.Show_Invalid_Submit_Form();
		}		
	}
	
	
	public void addListerner(NovoCadastroListerner target){
		try {
			Method method = NovoCadastroListerner.class.getDeclaredMethod("onClose", NovoCadastroEvent.class);
			addListener(NovoCadastroEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(NovoCadastroListerner target){
		removeListener(NovoCadastroEvent.class, target);
	}
	public static class NovoCadastroEvent extends Event{
		
		private Item item;
		private boolean confirm;
		//private Date dataPrimeiroBoleto;
		//private String valorPrimeiroBoleto;
		
		public NovoCadastroEvent(Component source, Item item, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;
			//this.dataPrimeiroBoleto = dataPrimeiroBoleto;
			//this.valorPrimeiroBoleto = valorPrimeiroBoleto;
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}	
		
//		public Date getDataPrimeiroBoleto(){
//			return dataPrimeiroBoleto;
//		}
//
//		public String getValorPrimeiroBoleto() {
//			return valorPrimeiroBoleto;
//		}
		
	}
	public interface NovoCadastroListerner extends Serializable{
		public void onClose(NovoCadastroEvent event);
	}
	
	
	
	
	
	


}
