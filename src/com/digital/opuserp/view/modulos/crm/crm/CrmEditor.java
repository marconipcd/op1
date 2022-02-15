package com.digital.opuserp.view.modulos.crm.crm;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesOseDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.dao.CrmDAO;
import com.digital.opuserp.dao.EmpresaDAO;
import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.dao.LogErrorDAO;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesOse;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.CrmFormasContato;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.EmailUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.crm.crm.ContatoEditor.EncaminharContatoEvent;
import com.digital.opuserp.view.modulos.crm.crm.CrmPesquisaEditor.CrmPesquisarEditorEvent;
import com.digital.opuserp.view.modulos.crm.crm.ReagendarEditor.ReagendadoEvent;
import com.digital.opuserp.view.modulos.crm.historico.HistoricoAlteracoesCrm;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.RoteirizacaoEditor;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.RoteirizacaoEditor.RoteirizacaoEvent;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.Notify;
import com.github.wolfie.refresher.Refresher;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
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
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class CrmEditor extends Window {
	
	
EntityManager em = ConnUtil.getEntity();
	
	

	Item item;
	Button btSalvar;
	Button btCancelar;
		
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TextField tfNProtocolo;
	DateField dfPrevisao;
	boolean validarData;
	boolean tratar = false;

	private TextField tfCodCliente;
	private TextField tfDescricaoCliente;
	private Cliente Clientelecionado = new Cliente();
	private TextField txtCpfCnpj;
	private TextField txtContato;
	
	
	private TextArea taObservacao;	
	private TextField tfDDD_fone1;
	private TextField tfDDD_fone2;
	private TextField tfDDD_cel1;
	private TextField tfDDD_cel2;

	private TextField tfTelefone1;
	private TextField tfTelefone2;
	private TextField tfTelefone3;
	private TextField tfTelefone4;
	
	private TextField txtEmail;
	private Integer codCrm;
	private String status = "";
	
	private ComboBox txtHora;
	
	private boolean valid_email;
	
	private TextField txtStatus;
	private TextField txtDataAbertura;
	private String emailInicial;
	
	private Date hora_inicial_tratamento;
	private Label lbTempoTratamento;
	
	private HorizontalLayout hlHoraTratamento;
	
	Endereco entityEndereco;
	
	String title;
	
	Crm crm;
	private Integer codCliente;
	
	
	public CrmEditor(final Item item, final String title, boolean modal){
		
		setHeight("690px");
		this.item = item;
		this.title = title;
		
		if(item.getItemProperty("id").getValue() != null){
			EntityItem<Crm> entityICrm = (EntityItem<Crm>) item;		
			crm = entityICrm.getEntity();
		}
		
		
		codCrm = (Integer) item.getItemProperty("id").getValue();
		status = (String) item.getItemProperty("status").getValue();
		
		
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
				
				hlHoraTratamento = new HorizontalLayout();
				hlHoraTratamento.addComponent(getDurationTratamento());
				
				HorizontalLayout hlLog = new HorizontalLayout();
				hlLog.setStyleName("hl_buttons_bottom");
				hlLog.setSpacing(true);
				hlLog.setMargin(true);	
								
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);		
				
				hlButtons.addComponent(buildBtCancelar("Cancelar"));
				hlButtons.addComponent(buildBtSalvar("Gravar"));
				
				HorizontalLayout hlBtRoot = new HorizontalLayout();
				hlBtRoot.setWidth("100%");
				
			
				
				
				hlBtRoot.addComponent(hlButtons);
				hlBtRoot.setExpandRatio(hlButtons, 1f);
				hlBtRoot.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
				addComponent(hlBtRoot);
			}
		});
		
			
			buildLayout();			
			
		}


	
	private Label getDurationTratamento(){
		
		DateTime data1 = new DateTime(hora_inicial_tratamento);
		DateTime data2 = new DateTime();
		
		Period period = new Period(data1, data2);
		
		
		
		lbTempoTratamento =  new Label("<h1>"+String.format("%02d", period.getMinutes())+":"+String.format("%02d", period.getSeconds())+"</h1>",ContentMode.HTML);
		lbTempoTratamento.setHeight("55px");
		
		return lbTempoTratamento;
		
	}
	private TextField txtComplemento;
	private TextField txtReferencia;
	private TextField txtContrato;
	private TextField txtStatusContrato;
	private ComboBox cbEndereco;
	private AcessoCliente contratoSelecionado;
	
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
					
		vlRoot.addComponent(
				new HorizontalLayout(){
					{
						setWidth("100%");
						
						addComponent(new FormLayout(){
							{

									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									tfNProtocolo = new TextField ("Nº de Protocolo");				
									tfNProtocolo.setStyleName("caption-align-acesso");
									tfNProtocolo.setNullRepresentation("");
									
									if(codCrm == null){
										tfNProtocolo.setValue(getNextNProtocolo());
									}else{
										tfNProtocolo.setValue(codCrm.toString());
									}
									
									tfNProtocolo.setReadOnly(true);

									addComponent(tfNProtocolo);
									
									tfNProtocolo.setEnabled(false);
							}
						});
						
						addComponent(new FormLayout(){
							{

									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																											
									txtDataAbertura = new TextField ("Data de Abertura");	
									txtDataAbertura.setStyleName("caption-align-crmData");
									txtDataAbertura.setWidth("220px");
									
									String data = "";
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
									if(item.getItemProperty("data_cadastro").getValue() != null){		
										Date dataCad = (Date)item.getItemProperty("data_cadastro").getValue();
										data = sdf.format(dataCad);
										
										if(!item.getItemProperty("operador").getValue().toString().equals("")){
											txtDataAbertura.setValue(data +", por "+item.getItemProperty("operador").getValue().toString());											
										}else{
											txtDataAbertura.setValue(data +", por "+item.getItemProperty("origem").getValue().toString());
										}
										
									}else{
										data = sdf.format(new Date());
										txtDataAbertura.setValue(data +", por "+OpusERP4UI.getUsuarioLogadoUI().getUsername());
									}
									
									txtDataAbertura.setReadOnly(true);
									addComponent(txtDataAbertura);				
									
									txtDataAbertura.setEnabled(false);
							}
						});
						
						
						addComponent(new FormLayout(){
							{

									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									txtStatus = new TextField ("Status");	
									txtStatus.setWidth("174px");
									txtStatus.setStyleName("caption-align-crmData");
									txtStatus.setNullRepresentation("");
									if(codCrm != null){
										txtStatus.setValue("EM ANALISE");
									}else{
										txtStatus.setValue("PENDENTE");															
									}
									txtStatus.setReadOnly(true);					

									addComponent(txtStatus);
									
									txtStatus.setEnabled(false);
							}
						});
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
				
				tfCodCliente = new TextField("Cliente");				
				tfCodCliente.setWidth("60px");				
				tfCodCliente.setNullRepresentation("");
				tfCodCliente.setStyleName("caption-align-acesso");
				tfCodCliente.focus();
				tfCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				tfCodCliente.setImmediate(true);
						
				
				
				tfCodCliente.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						Clientelecionado = new Cliente();
						
						//somente numeros contaas receber
						
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							codCliente = Integer.parseInt(event.getText());
							
							Clientelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));		
							
							if(Clientelecionado != null){
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
								tfDescricaoCliente.setReadOnly(true);	
								
								txtCpfCnpj.setReadOnly(false);
								txtCpfCnpj.setValue(Clientelecionado.getDoc_cpf_cnpj());
								txtCpfCnpj.setReadOnly(true);
								
								txtContato.setValue(Clientelecionado.getContato());
								tfDDD_fone1.setValue(Clientelecionado.getDdd_fone1());
								tfDDD_fone2.setValue(Clientelecionado.getDdd_fone2());
								tfDDD_cel1.setValue(Clientelecionado.getDdd_cel1());
								tfDDD_cel2.setValue(Clientelecionado.getDdd_cel2());
								tfTelefone1.setValue(Clientelecionado.getTelefone1());
								tfTelefone2.setValue(Clientelecionado.getTelefone2());
								tfTelefone3.setValue(Clientelecionado.getCelular1());
								tfTelefone4.setValue(Clientelecionado.getCelular2());
								txtEmail.setValue(Clientelecionado.getEmail());
								emailInicial = Clientelecionado.getEmail();
								
								valid_email =true;
								
								cbEndereco.setContainerDataSource(getEnderecos());
								
							}else {
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue("");
								tfDescricaoCliente.setReadOnly(true);	
								
								txtCpfCnpj.setReadOnly(false);
								txtCpfCnpj.setValue("");
								txtCpfCnpj.setReadOnly(true);
								txtContato.setValue("");
								tfDDD_fone1.setValue("");
								tfDDD_fone2.setValue("");
								tfDDD_cel1.setValue("");
								tfDDD_cel2.setValue("");
								tfTelefone1.setValue("");
								tfTelefone2.setValue("");
								tfTelefone3.setValue("");
								tfTelefone4.setValue("");
								txtEmail.setValue("");
								emailInicial = "";
								
								valid_email =true;
								
								cbEndereco.setContainerDataSource(null);
							}
						}else{
							tfDescricaoCliente.setReadOnly(false);
							tfDescricaoCliente.setValue("");
							tfDescricaoCliente.setReadOnly(true);	
							
							txtCpfCnpj.setReadOnly(false);
							txtCpfCnpj.setValue("");
							txtCpfCnpj.setReadOnly(true);
							txtContato.setValue("");
							tfDDD_fone1.setValue("");
							tfDDD_fone2.setValue("");
							tfDDD_cel1.setValue("");
							tfDDD_cel2.setValue("");
							tfTelefone1.setValue("");
							tfTelefone2.setValue("");
							tfTelefone3.setValue("");
							tfTelefone4.setValue("");
							txtEmail.setValue("");
							emailInicial = "";
							
							valid_email =true;
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
						Clientelecionado = c;
						tfCodCliente.setValue(c.getId().toString());
						tfCodCliente.setReadOnly(false);
						
						if(codCrm != null){
							tfCodCliente.setReadOnly(true);
							tfCodCliente.setRequired(false);		

						}
						tfDescricaoCliente.setReadOnly(false);						
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
							public void onSelected(com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
								
									if(event.getCliente() != null){
										tfCodCliente.setValue(event.getCliente().getId().toString());
										tfDescricaoCliente.setReadOnly(false);
										tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
										tfDescricaoCliente.setReadOnly(true);
										Clientelecionado = event.getCliente();
										codCliente = Integer.parseInt((event.getCliente().getId().toString()));			
										
										txtCpfCnpj.setReadOnly(false);
										txtCpfCnpj.setValue(Clientelecionado.getDoc_cpf_cnpj());
										txtCpfCnpj.setReadOnly(true);
										
										txtContato.setValue(Clientelecionado.getContato());
										tfDDD_fone1.setValue(Clientelecionado.getDdd_fone1());
										tfDDD_fone2.setValue(Clientelecionado.getDdd_fone2());
										tfDDD_cel1.setValue(Clientelecionado.getDdd_cel1());
										tfDDD_cel2.setValue(Clientelecionado.getDdd_cel2());
										tfTelefone1.setValue(Clientelecionado.getTelefone1());
										tfTelefone2.setValue(Clientelecionado.getTelefone2());
										tfTelefone3.setValue(Clientelecionado.getCelular1());
										tfTelefone4.setValue(Clientelecionado.getCelular2());
										txtEmail.setValue(Clientelecionado.getEmail());
										
										valid_email =true;
										emailInicial = Clientelecionado.getEmail();
										
										cbEndereco.setContainerDataSource(getEnderecos());
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
				
				tfCodCliente.setEnabled(false);
				tfDescricaoCliente.setEnabled(false);
				

			}	
		});
		
		vlRoot.addComponent(new FormLayout()	{				
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtCpfCnpj = new TextField ("CPF/CNPJ");				
					txtCpfCnpj.setStyleName("caption-align-acesso");
					txtCpfCnpj.setNullRepresentation("");	
					
					if(Clientelecionado.getDoc_cpf_cnpj() != null){
						txtCpfCnpj.setValue(Clientelecionado.getDoc_cpf_cnpj());
					}
					
					txtCpfCnpj.setReadOnly(true);

					addComponent(txtCpfCnpj);					
					//fieldGroup.bind(tfNProtocolo,"nome");
					
					txtCpfCnpj.setEnabled(false);
				}
			});
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					txtContato = new TextField ("Contato");				
					txtContato.setStyleName("caption-align-acesso");
					txtContato.setNullRepresentation("");		
					txtContato.setRequired(true);		
					
					if(Clientelecionado.getContato() != null){
						txtContato.setValue(Clientelecionado.getContato());
					}
					
					if(codCrm != null){
						txtContato.setReadOnly(true);
						txtContato.setRequired(false);		
					}

					addComponent(txtContato);					
					
					txtContato.setEnabled(false);
				}
			});
		
		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom-new");
						tfDDD_fone1 = new TextField("Telefone Principal");
						tfDDD_fone1.setNullRepresentation("");
						tfDDD_fone1.setStyleName("caption-align-contato-crm");
						tfDDD_fone1.setRequired(true);
						tfDDD_fone1.setMaxLength(2);
						tfDDD_fone1.setWidth("40px");
						
						if(Clientelecionado.getDdd_fone1() != null){
							tfDDD_fone1.setValue(Clientelecionado.getDdd_fone1());
						}

						if(codCrm != null){
							tfDDD_fone1.setReadOnly(true);
							tfDDD_fone1.setRequired(false);

						}
						addComponent(tfDDD_fone1);
						
						tfDDD_fone1.setEnabled(false);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom-new_hide_require");
						
						tfTelefone1 = new TextField();
						
						addComponent(tfTelefone1);
						tfTelefone1.setNullRepresentation("");					
						tfTelefone1.setWidth("105px");
						tfTelefone1.setMaxLength(11);
						tfTelefone1.setRequired(true);
						
						if(Clientelecionado.getTelefone1() != null){
							tfTelefone1.setValue(Clientelecionado.getTelefone1());
						}
						
						if(codCrm != null){
							tfTelefone1.setReadOnly(true);
						}
						
						tfTelefone1.setEnabled(false);
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");

						tfDDD_fone2 = new TextField("Telefone Alternativo");
						tfDDD_fone2.setNullRepresentation("");
						tfDDD_fone2.setWidth("40px");
						tfDDD_fone2.setMaxLength(2);
						
						if(Clientelecionado.getDdd_fone2() != null){
							tfDDD_fone2.setValue(Clientelecionado.getDdd_fone2());
						}

						if(codCrm != null){
							tfDDD_fone2.setReadOnly(true);
						}
						
						addComponent(tfDDD_fone2);
						
						tfDDD_fone2.setEnabled(false);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom-new");
						
						tfTelefone2 = new TextField();
						
						addComponent(tfTelefone2);
						tfTelefone2.setNullRepresentation("");
						tfTelefone2.setWidth("105px");
						tfTelefone2.setMaxLength(11);
						
						if(Clientelecionado.getTelefone2() != null){
							tfTelefone2.setValue(Clientelecionado.getTelefone2());
						}
						
						if(codCrm != null){
							tfTelefone2.setReadOnly(true);
						}

						tfTelefone2.setEnabled(false);
					}
				});

			}
		});

		// //Telefone 3,4

		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom");
						tfDDD_cel1 = new TextField("Telefone Alternativo");
						tfDDD_cel1.setNullRepresentation("");
						tfDDD_cel1.setStyleName("caption-align-contato-crm");
						tfDDD_cel1.setWidth("40px");
						tfDDD_cel1.setMaxLength(2);
						
						if(Clientelecionado.getDdd_cel1() != null){
							tfDDD_cel1.setValue(Clientelecionado.getDdd_cel1());
						}

						if(codCrm != null){
							tfDDD_cel1.setReadOnly(true);
						}
						
						addComponent(tfDDD_cel1);
						
						tfDDD_cel1.setEnabled(false);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom");
						
						tfTelefone3 = new TextField();
						
						addComponent(tfTelefone3);
						tfTelefone3.setNullRepresentation("");
						tfTelefone3.setWidth("105px");
						tfTelefone3.setMaxLength(11);
						
						if(Clientelecionado.getCelular1() != null){
							tfTelefone3.setValue(Clientelecionado.getCelular1());
						}
						
						if(codCrm != null){
							tfTelefone3.setReadOnly(true);
						}
						
						tfTelefone3.setEnabled(false);
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						tfDDD_cel2 = new TextField("Telefone Alternativo");
						tfDDD_cel2.setNullRepresentation("");
						tfDDD_cel2.setMaxLength(2);
						tfDDD_cel2.setWidth("40px");
						
						if(Clientelecionado.getCelular2() != null){
							tfDDD_cel2.setValue(Clientelecionado.getDdd_cel2());
						}
						
						if(codCrm != null){
							tfDDD_cel2.setReadOnly(true);
						}

						addComponent(tfDDD_cel2);
						
						tfDDD_cel2.setEnabled(false);
					}
				});

				addComponent(new FormLayout() {
					{
					
						setStyleName("form-cutom");
						
						
						tfTelefone4 = new TextField();
						
						addComponent(tfTelefone4);
						tfTelefone4.setNullRepresentation("");
						tfTelefone4.setWidth("105px");
						tfTelefone4.setMaxLength(11);
						
						if(Clientelecionado.getCelular2() != null){
							tfTelefone4.setValue(Clientelecionado.getCelular2());
						}
						
						tfTelefone4.setEnabled(false);
					}
				});
			}
		});
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					txtEmail = new TextField ("E-mail");		
					txtEmail.setWidth("260px");
					txtEmail.setStyleName("caption-align-acesso");
					txtEmail.setNullRepresentation("");	
					
					if(Clientelecionado.getEmail() != null){
						txtEmail.setValue(Clientelecionado.getEmail());
						valid_email = true;
						emailInicial = Clientelecionado.getEmail();
					}
					
					if(codCrm != null){
						txtEmail.setReadOnly(true);
					}
					
					txtEmail.setImmediate(true);
					txtEmail.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
							if(event.getText() != null && !event.getText().equals("") && !event.getText().equals(emailInicial)){
								if(EmailUtil.validate(event.getText()) && !event.getText().equals(emailInicial)){
															
									if(ClienteDAO.checkEmailExists(event.getText()) && !event.getText().equals(emailInicial)){
										event.getComponent().addStyleName("invalid-cpf");
										valid_email = false;
									}else{
										event.getComponent().removeStyleName("invalid-cpf");
										valid_email = true;	
									}
								}else{
									
									if(!EmailUtil.validate(event.getText())){
										event.getComponent().addStyleName("invalid-cpf");
										valid_email = false;
									}else{
										event.getComponent().removeStyleName("invalid-cpf");
										valid_email = true;
									}
								}
							}
							
						}
					});
					
					txtEmail.addBlurListener(new FieldEvents.BlurListener() {
						
						@Override
						public void blur(BlurEvent event) {
							if(!valid_email){
								Notify.Show("E-mail Inválido!", Notify.TYPE_ERROR);
							}
						}
					});
					
					addComponent(txtEmail);		
					
					txtEmail.setEnabled(false);
				}
			});
		
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
									
				Endereco end = (Endereco)item.getItemProperty("end").getValue();
				
				TextField cbEndereco = new TextField("Endereços");
				
				if(end != null && end.getCep() != null && end.getEndereco() != null && end.getNumero() != null && end.getBairro() != null && end.getCidade() != null && end.getUf() != null){
					cbEndereco.setValue(end.getCep()+", "+end.getEndereco()+", "+end.getNumero()+", "+end.getBairro()+", "+end.getCidade()+", "+end.getUf());
				}
				
				cbEndereco.setWidth("685px");
				cbEndereco.setRequired(true);				
				cbEndereco.setStyleName("caption-align-acesso");	
				cbEndereco.setEnabled(false);
											
				addComponent(cbEndereco);								
			}
		});
		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtComplemento = new TextField("Complemento");
						txtComplemento.setNullRepresentation("");
						txtComplemento.setStyleName("caption-align-acesso");
						txtComplemento.setWidth("300px");
						txtComplemento.setImmediate(true);
						txtComplemento.setTextChangeEventMode(TextChangeEventMode.LAZY);
						txtComplemento.setRequired(true);
						txtComplemento.setDescription("Casa, Apto, Galpão, Anexo, etc");						
						
						addComponent(txtComplemento);	
						
						Endereco end = (Endereco)item.getItemProperty("end").getValue();
						if(end != null && end.getComplemento() != null){
							txtComplemento.setValue(end.getComplemento());
						}
						
						txtComplemento.setEnabled(false);
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtReferencia = new TextField("Referência");
						txtReferencia.setNullRepresentation("");
						txtReferencia.setWidth("300px");
						txtReferencia.setImmediate(true);
						txtReferencia.setTextChangeEventMode(TextChangeEventMode.LAZY);
						
						addComponent(txtReferencia);
						
						Endereco end = (Endereco)item.getItemProperty("end").getValue();
						
						if(end != null && end.getReferencia() != null){
							txtReferencia.setValue(end.getReferencia());
						}
						txtReferencia.setEnabled(false);
					}
				});
			}
		});
		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new HorizontalLayout(){
					
					{
						setWidth("100%");
						
						addComponent(new FormLayout() {
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								txtContrato = new TextField("Contrato");
								txtContrato.setNullRepresentation("");
								txtContrato.setWidth("521px");
								txtContrato.setStyleName("caption-align-acesso");
								txtContrato.setReadOnly(true);
													
								addComponent(txtContrato);
								
								if(item.getItemProperty("contrato").getValue() != null){
									AcessoCliente contrato = (AcessoCliente) item.getItemProperty("contrato").getValue();
									
									txtContrato.setReadOnly(false);
									txtContrato.setValue(contrato.getId()+" - "+contrato.getContrato().getNome());
									txtContrato.setReadOnly(true);
								}
								
								txtContrato.setEnabled(false);
							}
						});
						
						addComponent(new FormLayout() {
							{
								setMargin(true);
								setSpacing(true);
								setStyleName("form-cutom");
								
								txtStatusContrato = new TextField();
								txtStatusContrato.setNullRepresentation("");
								txtStatusContrato.setWidth("138px");								
								txtStatusContrato.setReadOnly(true);
													
								addComponent(txtStatusContrato);
								
								if(item.getItemProperty("contrato").getValue() != null){
									AcessoCliente contrato = (AcessoCliente) item.getItemProperty("contrato").getValue();
									
									txtStatusContrato.setReadOnly(false);
									txtStatusContrato.setValue(contrato.getStatus_2());
									txtStatusContrato.setReadOnly(true);
								}
								
								txtStatusContrato.setEnabled(false);
							}
						});
					}
				});
			}
		});
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
						
					
					ComboBox cbFormaContato = new ComboBox("Forma de Contato", buildContainerFormaContato());
					cbFormaContato.setRequired(true);
					cbFormaContato.setWidth("260px");
					cbFormaContato.setTextInputAllowed(false);
					cbFormaContato.setNullSelectionAllowed(false);					
					cbFormaContato.setConverter(new SingleSelectConverter(cbFormaContato));			
					cbFormaContato.setItemCaptionPropertyId("nome");
					cbFormaContato.setStyleName("caption-align-acesso");
					
					addComponent(cbFormaContato);					
					fieldGroup.bind(cbFormaContato,"crm_formas_contato");
					
					cbFormaContato.setEnabled(false); 
				}
			});
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
						
					
					final ComboBox cbSetor = new ComboBox("Setor", buildContainerSetores());
					cbSetor.setRequired(true);
					cbSetor.setWidth("260px");
					cbSetor.setTextInputAllowed(false);
					cbSetor.setNullSelectionAllowed(false);					
					cbSetor.setConverter(new SingleSelectConverter(cbSetor));
					cbSetor.setItemCaptionPropertyId("nome");
					cbSetor.setStyleName("caption-align-acesso");
					
					cbSetor.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(cbSetor.getValue() != null){
								if(fieldGroup.getField("crm_assuntos") != null){
									((ComboBox)fieldGroup.getField("crm_assuntos")).setContainerDataSource(buildContainerAssunto());
									((ComboBox)fieldGroup.getField("crm_assuntos")).setEnabled(true);
								}
							}
						}
					});
					
					
					addComponent(cbSetor);					
					fieldGroup.bind(cbSetor,"setor");
					
					cbSetor.setEnabled(false);
				}
			});
		
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					
					final ComboBox cbAssunto = new ComboBox("Assunto", buildContainerAssunto());
					cbAssunto.setRequired(true);
					cbAssunto.setWidth("260px");
					cbAssunto.setTextInputAllowed(false);
					cbAssunto.setNullSelectionAllowed(false);					
					cbAssunto.setConverter(new SingleSelectConverter(cbAssunto));
					cbAssunto.setItemCaptionPropertyId("nome");
					cbAssunto.setStyleName("caption-align-acesso");
					cbAssunto.setImmediate(true);
					cbAssunto.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(((TextArea) fieldGroup.getField("conteudo")) != null && cbAssunto.getValue() != null && cbAssunto.getItem(cbAssunto.getValue()) != null){
								((TextArea) fieldGroup.getField("conteudo")).setValue(cbAssunto.getItem(cbAssunto.getValue()).getItemProperty("conteudo").getValue().toString());
							}
						}
					});
					
					
					addComponent(cbAssunto);					
					fieldGroup.bind(cbAssunto,"crm_assuntos");
					
					if(item.getItemProperty("crm_assuntos").getValue() == null){
						cbAssunto.setEnabled(false); 
					}
					
					cbAssunto.setEnabled(false); 
				}
			});
				
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					
					taObservacao = new TextArea("Conteúdo");
					taObservacao.setRequired(true);	
					taObservacao.setNullRepresentation("");
					taObservacao.setWidth("797");
					taObservacao.setHeight("111px");
					taObservacao.setStyleName("caption-align-acesso");
					taObservacao.addStyleName("uppercase");
					taObservacao.addStyleName("font-courier-new");
					
					addComponent(taObservacao);					
					fieldGroup.bind(taObservacao,"conteudo");
					
					
					
				}
			});
		
		
		vlRoot.addComponent(
				new HorizontalLayout(){
					{
												
						addComponent(new FormLayout(){
							{

									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									dfPrevisao = new DateField ("Previsão Feedback");
									dfPrevisao.setDateFormat("dd/MM/yyyy");
									dfPrevisao.setStyleName("caption-align-acesso");														
									dfPrevisao.setRequired(true);

									addComponent(dfPrevisao);
									fieldGroup.bind(dfPrevisao,"data_agendado");
													
									dfPrevisao.addListener(new FieldEvents.BlurListener() {

										@Override
										public void blur(BlurEvent event) {
											
											Calendar data = Calendar.getInstance(); 
											data.setTime(new Date());
											data.add(Calendar.DAY_OF_MONTH, -1);	
											Date dtOnten = data.getTime();	
											
											if(dfPrevisao.getValue()!=null && !dfPrevisao.getValue().equals("") && dtOnten.compareTo(dfPrevisao.getValue()) < 0){										
												validarData = true;
											}else{
												validarData = false;
												
												Notify.Show("Data Inválida!", Notify.TYPE_ERROR);

											}
											
											
											txtHora.focus();																
										}
									});		
									
									dfPrevisao.setEnabled(false); 
							}
						});
						
						addComponent(new FormLayout(){
							{

									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									txtHora = new ComboBox("Hora");
									
									if(item.getItemProperty("id").getValue() != null){
										txtHora.setRequired(false);									
										txtHora.setNullSelectionAllowed(true);	
									}else{
										txtHora.setRequired(true);									
										txtHora.setNullSelectionAllowed(false);
									}
									
									
									
									txtHora.setId("txtHoraCrm");
									txtHora.setWidth("100px");
									txtHora.addItem("08:30");
									txtHora.addItem("09:00");
									txtHora.addItem("09:30");
									txtHora.addItem("10:00");
									txtHora.addItem("10:30");
									txtHora.addItem("11:00");
									txtHora.addItem("11:30");
									txtHora.addItem("14:00");
									txtHora.addItem("14:30");
									txtHora.addItem("15:00");
									txtHora.addItem("15:30");
									txtHora.addItem("16:00");
									txtHora.addItem("16:30");
									txtHora.addItem("17:00");
									txtHora.addItem("17:30");
									if(item.getItemProperty("hora_agendado").getValue() != null){
										txtHora.setValue(item.getItemProperty("hora_agendado").getValue().toString());
									}
									
									JavaScript.getCurrent().execute("$('#txtHoraCrm').mask('00:00')");
									
									addComponent(txtHora);
									
									
									txtHora.setEnabled(false); 
							}
						});
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								
								ComboBox cbFormaContato = new ComboBox("Contato Feedback", buildContainerFormaContato());
								
								if(item.getItemProperty("id").getValue() != null){									
									cbFormaContato.setRequired(false);
								}else{
									cbFormaContato.setRequired(true);
								}
								
								
								cbFormaContato.setWidth("260px");
								cbFormaContato.setTextInputAllowed(false);
								cbFormaContato.setNullSelectionAllowed(false);					
								cbFormaContato.setConverter(new SingleSelectConverter(cbFormaContato));			
								cbFormaContato.setItemCaptionPropertyId("nome");
								cbFormaContato.setStyleName("caption-align-crmFeed");
								
								addComponent(cbFormaContato);					
								fieldGroup.bind(cbFormaContato,"contato_feedback");
								
								cbFormaContato.setEnabled(false); 
							}
						});
					}
		});	
	}
	
	private JPAContainer<Endereco> getEnderecos(){
		JPAContainer<Endereco> container = JPAContainerFactory.make(Endereco.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("clientes", Clientelecionado));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		return container;
	}
	
	private JPAContainer<CrmFormasContato> buildContainerFormaContato(){
		
		JPAContainer<CrmFormasContato> container = JPAContainerFactory.make(CrmFormasContato.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.sort(new String[] {"nome"}, new boolean[]{true});
		return container;
	}
	
	private JPAContainer<Setores> buildContainerSetores(){
		
		JPAContainer<Setores> container = JPAContainerFactory.make(Setores.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.sort(new String[] {"nome"}, new boolean[]{true});
		return container;
	}
	
	private JPAContainer<CrmAssunto> buildContainerAssunto(){
		
		JPAContainer<CrmAssunto> container = JPAContainerFactory.make(CrmAssunto.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		if(fieldGroup.getField("setor").getValue() != null){
			EntityItem<Setores> setor = (EntityItem<Setores>) ((ComboBox)fieldGroup.getField("setor")).getItem(fieldGroup.getField("setor").getValue()); 
			
			if(setor != null){
				container.addContainerFilter(Filters.eq("setor", setor.getEntity()));
			}
		}
		
		container.sort(new String[] {"nome"}, new boolean[]{true});
		return container;
	}
	
	private String getNextNProtocolo(){
		return CrmDAO.getNextID();
	}

	Refresher refresher;
	Integer time = 1;
	public Refresher buildRefresher(){
		
		
		refresher = new Refresher();
		refresher.setRefreshInterval(1000);
		refresher.addListener(new Refresher.RefreshListener() {
			
			@Override
			public void refresh(Refresher source) {	
				
				time++;
				hlHoraTratamento.replaceComponent(lbTempoTratamento, getDurationTratamento());			
				
				if(time % 9 == 0){  
					CrmDAO.registrarIteracao(codCrm);  
				}  
				
			}
		});
		
		return refresher;
	}
	
	
	public Button buildBtSalvar(String s) {
			
		btSalvar = new Button(s, new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				    
					
					GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Deseja Realmente Gravar este CRM ?", true, true);
									
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								
								if(codCrm!=null){
									validarData = true;			
								}
								
								if(txtEmail.getValue() == null || txtEmail.getValue().isEmpty() || txtEmail.getValue() == ""){
									valid_email = true;
								}
								
								boolean validacao = false;
								
								if(tfNProtocolo.getValue() != null){
									if(!taObservacao.getValue().equals("") ){
										validacao = true;
									}else{
										validacao = false;
									}
								}else{
									if(fieldGroup.isValid() && valid_email && tfCodCliente.getValue() != null && !tfCodCliente.getValue().equals("") &&								   
											   validarData){
										validacao = true;
									}else{
										validacao = false;
									}
								}
								
								if(validacao){
									
									try{
											
											
											fieldGroup.commit();				
																
											fireEvent(new EditarContatoEvent(getUI(), item, true));
											
											Notify.Show("Gravado com Sucesso", Notify.TYPE_SUCCESS);
											
											close();
										
										
										
										
									} catch (Exception e) {					
										e.printStackTrace();					
										
										Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
										LogErrorDAO.add(new LogError(null, this.getClass().getSimpleName(), "buildBtSalvar", e.getLocalizedMessage(), OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
									}
								}else{
									
									for (Field<?> field: fieldGroup.getFields()) {
										
										if(!field.isValid()){
											field.addStyleName("invalid-txt");
										}else{
											field.removeStyleName("invalid-txt");
										}	
									}
									
									if(!valid_email){
										txtEmail.addStyleName("invalid-txt");
									}else{
										txtEmail.removeStyleName("invalid-txt");
									}
					
									if(tfCodCliente.getValue()==null||tfCodCliente.getValue().equals("")){
										tfCodCliente.addStyleName("invalid-txt");							
									}else{
										tfCodCliente.removeStyleName("invalid-txt");
									}
									
									if(tfDDD_fone1.getValue()==null||tfDDD_fone1.getValue().equals("")){
										tfDDD_fone1.addStyleName("invalid-txt");							
									}else{
										tfDDD_fone1.removeStyleName("invalid-txt");
									}
									
									if(tfTelefone1.getValue()==null||tfTelefone1.getValue().equals("")){
										tfTelefone1.addStyleName("invalid-txt");							
									}else{
										tfTelefone1.removeStyleName("invalid-txt");
									}				
									
									if(txtContato.getValue()==null||txtContato.getValue().equals("")){
										txtContato.addStyleName("invalid-txt");							
									}else{
										txtContato.removeStyleName("invalid-txt");
									}	
									
									if(item.getItemProperty("data_agendado").getValue()==null||item.getItemProperty("data_agendado").getValue().equals("")||!validarData){
										dfPrevisao.addStyleName("invalid-txt");							
									}else{
										dfPrevisao.removeStyleName("invalid-txt");
									}
									
									if(item.getItemProperty("hora_agendado").getValue()==null||item.getItemProperty("hora_agendado").getValue().equals("")||!validarData){
										txtHora.addStyleName("invalid-txt");							
									}else{
										txtHora.removeStyleName("invalid-txt");
									}
									
									
									Notify.Show_Invalid_Submit_Form();
								}
						}
								
						}
					});
			
			
					getUI().addWindow(gd); 
			}		
	
		});

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				if(!((Component)target).getStateType().getName().equals("com.vaadin.shared.ui.textarea.TextAreaState")){
					btSalvar.click(); 
				}			
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}


	
	
	
	
	public Button buildBtCancelar(String s) {
		btCancelar = new Button(s, new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				//if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new EditarContatoEvent(getUI(), item, false));
					fireEvent(new EncaminharContatoEvent(getUI(), item, false));

					//					removeExtension(refresherTempoTratamento);
					
					close();			
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

	
	
	
	public void addListerner(EditarContatoListerner target){
		try {
			Method method = EditarContatoListerner.class.getDeclaredMethod("onClose", EditarContatoEvent.class);
			addListener(EditarContatoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(EditarContatoListerner target){
		removeListener(EditarContatoEvent.class, target);
	}
	public static class EditarContatoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public EditarContatoEvent(Component source, Item item, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;			
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface EditarContatoListerner extends Serializable{
		public void onClose(EditarContatoEvent event);
	}
	
		
}
