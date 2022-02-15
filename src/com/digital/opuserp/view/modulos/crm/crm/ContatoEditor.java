package com.digital.opuserp.view.modulos.crm.crm;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

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
import com.digital.opuserp.dao.GerenciarModuloDAO;
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
import com.vaadin.server.BrowserWindowOpener;
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

public class ContatoEditor extends Window {
		
	EntityManager em = ConnUtil.getEntity();
	
	GerenciarModuloDAO gmDAO;
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	Button btTratar;
	Button btReagendar;
	Button btEncaminharOutroNivel;
	Button btAbrirOs;
	Button btLog;
	Button btPesquisa;
	
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
	private Integer codCliente;
	private TextField txtCpfCnpj;
	private TextField txtContato;
		
	private TextArea taObservacao;	
	private TextField tfDDD_fone1;
	private TextField tfDDD_fone2;
	private TextField tfDDD_fone3;
	private TextField tfDDD_fone4;

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
	Integer codSubModulo;
	public ContatoEditor(final Item item, final String title, boolean modal, final Integer codSubModulo){
		
		setHeight("672px");
		this.item = item;
		this.title = title;
		this.codSubModulo = codSubModulo;
		
		gmDAO = new GerenciarModuloDAO();
		
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
//				hlHoraTratamento.setHeight("50px");
				hlHoraTratamento.addComponent(getDurationTratamento());
				
				HorizontalLayout hlLog = new HorizontalLayout();
				hlLog.setStyleName("hl_buttons_bottom");
				hlLog.setSpacing(true);
				hlLog.setMargin(true);	
								
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setWidth("100%");
				
				Button btRegistrarAtendimento = new Button("Registrar Atendimento", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						try{
							
								if(entityEndereco != null){
									item.getItemProperty("end").setValue(entityEndereco);
								}
						
								item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
								item.getItemProperty("cliente").setValue(Clientelecionado);
								item.getItemProperty("origem").setValue("OPUS");
	
								item.getItemProperty("status").setValue("EFETUADO");
								item.getItemProperty("data_efetuado").setValue(new Date());
							
								item.getItemProperty("contato").setValue(Clientelecionado.getContato());
								item.getItemProperty("operador").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
								
								if(txtHora.getValue() != null &&  !txtHora.getValue().equals("")){
									SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
									item.getItemProperty("hora_agendado").setValue(sdf.parse(txtHora.getValue().toString()));
								}
							
								if(fieldGroup.isValid()){
									
									fieldGroup.commit();
								
									BeanItem<Crm> crmItem = (BeanItem<Crm>)item;
									Crm crm = crmItem.getBean();
									
									boolean check = CrmDAO.registrarAtendimento(crm);
									
									if(check){
										Notify.Show("Crm Registrado com Sucesso!", Notify.TYPE_SUCCESS);
										close(); 
									}
								}else{
									Notify.Show("Preencha os campos para concluir!", Notify.TYPE_WARNING);
								}
						
						}catch(Exception e){
								e.printStackTrace();
						}
						
					}
				});
				
												
				if(codCrm != null && title.equals("Visualizar")){
					hlButtons.addComponent(buildBtCancelar("FECHAR"));
					hlButtons.setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
					hlButtons.setExpandRatio(btCancelar, 0.12f);
				}else{
					
					GerenciarModuloDAO gmDAO = new GerenciarModuloDAO();
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Registrar Atendimento") && status == null)				
					{					
						hlButtons.addComponent(btRegistrarAtendimento);
						
						hlButtons.setComponentAlignment(btRegistrarAtendimento, Alignment.MIDDLE_LEFT);
						hlButtons.setExpandRatio(btRegistrarAtendimento, 1f);					
						
						hlButtons.addComponent(buildBtCancelar("Cancelar"));
						hlButtons.setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
						hlButtons.setExpandRatio(btCancelar, 0.12f);
					}else{
					
						hlButtons.addComponent(buildBtCancelar("Cancelar"));
						hlButtons.setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
						hlButtons.setExpandRatio(btCancelar, 0.12f);
					}
				}
				
				if(codCrm != null && status.equals("AGENDADO") && !title.equals("Visualizar")){
					hlButtons.addComponent(BuildBtOcorrencia());
					hlButtons.setComponentAlignment(btOcorrencia, Alignment.MIDDLE_RIGHT);
					hlButtons.setExpandRatio(btOcorrencia, 0.14f);
					hlButtons.addComponent(buildBtReagendar());
					hlButtons.setComponentAlignment(btReagendar, Alignment.MIDDLE_RIGHT);
					hlButtons.setExpandRatio(btReagendar, 0.14f);
					hlButtons.addComponent(buildBtAbrirOs());		
					hlButtons.setComponentAlignment(btAbrirOs, Alignment.MIDDLE_RIGHT);
					hlButtons.setExpandRatio(btAbrirOs, 0.12f);
					
					if(item.getItemProperty("nivel").getValue() != null && !item.getItemProperty("nivel").getValue().equals("NIVEL III")){
						hlButtons.addComponent(buildBtEncaminharOutroNivel());
						hlButtons.setComponentAlignment(btEncaminharOutroNivel, Alignment.MIDDLE_RIGHT);
						hlButtons.setExpandRatio(btEncaminharOutroNivel, 0.19f);
					}
				}
				
				if(codCrm == null){									
					hlButtons.addComponent(buildBtSalvar("OK"));
					hlButtons.setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
				}else if(codCrm!=null && status.equals("EM ANALISE") && !title.equals("Visualizar")){
					hlButtons.addComponent(buildBtSalvar("Agendar"));
					hlButtons.setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
				}else{
					if(crm != null && crm.getCrm_assuntos().getPerguntas_ativas() != null && crm.getCrm_assuntos().getPerguntas_ativas().equals("ATIVO") && !title.equals("Visualizar")){
						hlButtons.addComponent(buildBtSalvar("Avançar"));
						hlButtons.setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
					}else if(!title.equals("Visualizar")){						
						hlButtons.addComponent(buildBtSalvar("Concluir"));
						hlButtons.setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
						hlButtons.setExpandRatio(btSalvar, 0.12f);
					}
					tratar = true;
//					hlButtons.addComponent(buildBtSalvar("Tratar"));
				}

				HorizontalLayout hlBtRoot = new HorizontalLayout();
				hlBtRoot.setWidth("100%");
				
				
				if(codCrm != null && status.equals("AGENDADO") && !title.equals("Visualizar")){
					hlBtRoot.addComponent(hlHoraTratamento);
					hlBtRoot.setExpandRatio(hlHoraTratamento, 0.32f);
				}
//				if(item.getItemProperty("id").getValue() != null){
//					hlBtRoot.addComponent(hlHoraTratamento);
//				}

				if(codCrm != null && status.equals("AGENDADO") && !title.equals("Visualizar")){
					hlBtRoot.setComponentAlignment(hlHoraTratamento, Alignment.BOTTOM_LEFT);
					hlBtRoot.setExpandRatio(hlHoraTratamento, 0.32f);
				}
				
				
				if(codCrm != null && title.equals("Visualizar") && status.equals("EFETUADO")){
					hlLog.addComponent(BuildbtLog());
					hlLog.addComponent(buildBtPesquisa());
					hlBtRoot.addComponent(hlLog);
					hlBtRoot.setComponentAlignment(hlLog, Alignment.BOTTOM_LEFT);
					
				}else if(codCrm != null && title.equals("Visualizar")){
					hlLog.addComponent(BuildbtLog());
					hlBtRoot.addComponent(hlLog);
					hlBtRoot.setComponentAlignment(hlLog, Alignment.BOTTOM_LEFT);
				}
								
				
				//hlBtRoot.addComponent(btRegistrarAtendimento); 
				//hlBtRoot.setComponentAlignment(btRegistrarAtendimento, Alignment.BOTTOM_LEFT);
				
				
				hlBtRoot.addComponent(hlButtons);
				
				
				hlBtRoot.setExpandRatio(hlButtons, 0.68f);	
				hlBtRoot.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
				addComponent(hlBtRoot);
			}
		});
		
			if(codCrm != null && status.equals("AGENDADO") && !title.equals("Visualizar")){
				
				
				
				buildLayoutTratar();	
				btReagendar.setEnabled(true);
			}else if(title.equals("Visualizar")){
				
				if(item.getItemProperty("end").getValue() != null){
					entityEndereco = (Endereco)item.getItemProperty("end").getValue();
				}
				
				buildLayoutVisualizar();
			}else{
				buildLayout();			
			}
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
					
		vlRoot.addComponent(	new HorizontalLayout(){
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
									
									tfNProtocolo.setValue(getNextNProtocolo());			
									
									tfNProtocolo.setReadOnly(true);

									addComponent(tfNProtocolo);
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
										txtDataAbertura.setValue(data +", por "+OpusERP4UI.getUsuarioLogadoUI().getUsername());
									}else{
										data = sdf.format(new Date());
										txtDataAbertura.setValue(data +", por "+OpusERP4UI.getUsuarioLogadoUI().getUsername());
									}
									
									txtDataAbertura.setReadOnly(true);
									addComponent(txtDataAbertura);				
									
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
								tfDDD_fone3.setValue(Clientelecionado.getDdd_cel1());
								tfDDD_fone4.setValue(Clientelecionado.getDdd_cel2());
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
								tfDDD_fone3.setValue("");
								tfDDD_fone4.setValue("");
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
							tfDDD_fone3.setValue("");
							tfDDD_fone4.setValue("");
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
										tfDDD_fone3.setValue(Clientelecionado.getDdd_cel1());
										tfDDD_fone4.setValue(Clientelecionado.getDdd_cel2());
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

			}	
		});
		
		vlRoot.addComponent(new FormLayout(){					
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
					//fieldGroup.bind(tfNProtocolo,"nome");
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
						
						
						if(tfDDD_fone1.getValue() != null && !tfDDD_fone1.getValue().equals("") && 
								tfTelefone1.getValue() != null && !tfTelefone1.getValue().equals("")){
							
							tfTelefone1.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone1.setReadOnly(false);
							tfTelefone1.setValue(tfTelefone1.getValue());
							tfTelefone1.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone1.getValue()+tfTelefone1.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone1);
						}
						
						
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
						
						if(tfDDD_fone2.getValue() != null && !tfDDD_fone2.getValue().equals("") && 
								tfTelefone2.getValue() != null && !tfTelefone2.getValue().equals("")){
							
							tfTelefone2.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone2.setReadOnly(false);
							tfTelefone2.setValue(tfTelefone2.getValue());
							tfTelefone2.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone2.getValue()+tfTelefone2.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone2);
						}


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
						tfDDD_fone3 = new TextField("Telefone Alternativo");
						tfDDD_fone3.setNullRepresentation("");
						tfDDD_fone3.setStyleName("caption-align-contato-crm");
						tfDDD_fone3.setWidth("40px");
						tfDDD_fone3.setMaxLength(2);
						
						if(Clientelecionado.getDdd_cel1() != null){
							tfDDD_fone3.setValue(Clientelecionado.getDdd_cel1());
						}

						if(codCrm != null){
							tfDDD_fone3.setReadOnly(true);
						}
						
						addComponent(tfDDD_fone3);
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
						
						if(tfDDD_fone3.getValue() != null && !tfDDD_fone3.getValue().equals("") && 
								tfTelefone3.getValue() != null && !tfTelefone3.getValue().equals("")){
							
							tfTelefone3.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone3.setReadOnly(false);
							tfTelefone3.setValue(tfTelefone3.getValue());
							tfTelefone3.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone3.getValue()+tfTelefone3.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone3);
						}
						
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						tfDDD_fone4 = new TextField("Telefone Alternativo");
						tfDDD_fone4.setNullRepresentation("");
						tfDDD_fone4.setMaxLength(2);
						tfDDD_fone4.setWidth("40px");
						
						if(Clientelecionado.getCelular2() != null){
							tfDDD_fone4.setValue(Clientelecionado.getDdd_cel2());
						}
						
						if(codCrm != null){
							tfDDD_fone4.setReadOnly(true);
						}

						addComponent(tfDDD_fone4);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom");
						
						
						tfTelefone4 = new TextField();
						
						addComponent(tfTelefone4);
						tfTelefone4.setNullRepresentation("");
						tfTelefone4.setWidth("105px");
						tfTelefone4.setMaxLength(11);
						
						if(Clientelecionado.getCelular2() != null){
							tfTelefone4.setValue(Clientelecionado.getCelular2());
						}
						
						if(tfDDD_fone4.getValue() != null && !tfDDD_fone4.getValue().equals("") && 
								tfTelefone4.getValue() != null && !tfTelefone4.getValue().equals("")){
							
							tfTelefone4.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone4.setReadOnly(false);
							tfTelefone4.setValue(tfTelefone4.getValue());
							tfTelefone4.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone4.getValue()+tfTelefone4.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone4);
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
					//fieldGroup.bind(tfNProtocolo,"nome");
				}
			});
		
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
																										
				cbEndereco = new ComboBox("Endereços"){
					@Override
					public String getItemCaption(Object itemId) {
					   Item item = getItem(itemId);
					
					   if (item == null) {
					      return "";
					   }
					     
					   return String.valueOf(item.getItemProperty("cep")) + ", " + 
				   		  String.valueOf(item.getItemProperty("endereco")) + ", " +
				   		  String.valueOf(item.getItemProperty("numero")) + ", " +
				   		  String.valueOf(item.getItemProperty("bairro")) + ", " +
				   		  String.valueOf(item.getItemProperty("cidade"))+ " - " +
				   		  String.valueOf(item.getItemProperty("uf"));
					}
				};
				
				cbEndereco.setWidth("685px");
				cbEndereco.setRequired(true);
				cbEndereco.setNullSelectionAllowed(false);
				cbEndereco.setStyleName("caption-align-acesso");	
				cbEndereco.setImmediate(true);
				cbEndereco.setTextInputAllowed(false);
				//cbEndereco.setConverter(new SingleSelectConverter(cbEndereco));
				
				cbEndereco.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						if(cbEndereco.getValue() != null && txtComplemento != null && txtReferencia != null){
							
							if(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue() != null){
								txtComplemento.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
							}else{
								txtComplemento.setValue("");
							}
															
							if(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue() != null){
								txtReferencia.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue().toString());
							}else{
								txtReferencia.setValue("");
							}
							
							contratoSelecionado = ContratosAcessoDAO.getContratoPorEndereco((Integer)cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("id").getValue());
							
							if(contratoSelecionado != null){
								txtContrato.removeStyleName("red-contrato");
								txtContrato.setReadOnly(false);
								txtContrato.setValue(contratoSelecionado.getId().toString()+" - "+contratoSelecionado.getContrato().getNome());
								txtContrato.setReadOnly(true);
	
								txtStatusContrato.setReadOnly(false);
								txtStatusContrato.setValue(contratoSelecionado.getStatus_2());
								txtStatusContrato.setReadOnly(true);
							}else{
								txtContrato.addStyleName("red-contrato");
								txtContrato.setReadOnly(false);
								txtContrato.setValue("SEM CONTRATO VINCULADO A ESTE ENDEREÇO");
								txtContrato.setReadOnly(true);
							}
							
						}else{
							if(txtComplemento != null && txtReferencia != null){
								txtReferencia.setValue("");
								txtComplemento.setValue("");
							}
						}
					}
				});
								
				addComponent(cbEndereco);
				
				//if(Clientelecionado != null && item.getItemProperty("end").getValue() != null){
				//	cbEndereco.setContainerDataSource(getEnderecos());
				//	fieldGroup.bind(cbEndereco, "end"); 
				//}
				
				//cbEndereco.setReadOnly(true);
								
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
						
						//if(cbEndereco.getValue() != null){
						//	txtComplemento.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
						//}
						
						//txtComplemento.setReadOnly(true);
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
						
						//if(cbEndereco.getValue() != null){
						//	txtReferencia.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue().toString());
						//}
						
						//txtReferencia.setReadOnly(true);
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
				}
			});
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
						
					
					final ComboBox cbSetor = new ComboBox("Setor", buildContainerSetores());
					//cbSetor.setRequired(true);
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
				}
			});
		
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					
					final ComboBox cbAssunto = new ComboBox("Assunto", buildContainerAssunto());
					//cbAssunto.setRequired(true);
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
				}
			});
				
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					
					TextArea taObservacao = new TextArea("Conteúdo");
					taObservacao.setRequired(true);	
					taObservacao.setNullRepresentation("");
					taObservacao.setWidth("797");
					taObservacao.setHeight("111px");
					taObservacao.setStyleName("caption-align-acesso");
					//taObservacao.addStyleName("uppercase");
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
							}
						});
						
						addComponent(new FormLayout(){
							{

									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									txtHora = new ComboBox("Hora");
									//txtHora.setRequired(true); 
									txtHora.setNullSelectionAllowed(false);	
									txtHora.setId("txtHoraCrm");
									txtHora.setWidth("100px");
									txtHora.addItem("08:30");
									txtHora.addItem("09:00");
									txtHora.addItem("09:30");
									txtHora.addItem("10:00");
									txtHora.addItem("10:30");
									txtHora.addItem("11:00");
									txtHora.addItem("11:30");
									txtHora.addItem("12:00");
									txtHora.addItem("12:30");
									txtHora.addItem("13:00");
									txtHora.addItem("13:30");
									txtHora.addItem("14:00");
									txtHora.addItem("14:30");
									txtHora.addItem("15:00");
									txtHora.addItem("15:30");
									txtHora.addItem("16:00");
									txtHora.addItem("16:30");
									txtHora.addItem("17:00");
									txtHora.addItem("17:30");
									txtHora.addItem("18:00");
									txtHora.addItem("18:30");
									txtHora.addItem("19:00");
									txtHora.addItem("19:30");
									if(item.getItemProperty("hora_agendado").getValue() != null){
										txtHora.setValue(item.getItemProperty("hora_agendado").getValue().toString());
									}
									
									JavaScript.getCurrent().execute("$('#txtHoraCrm').mask('00:00')");
									
									addComponent(txtHora);
									//fieldGroup.bind(txtHora,"hora_agendado");
							}
						});
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								
								ComboBox cbFormaContato = new ComboBox("Contato Feedback", buildContainerFormaContato());
								//cbFormaContato.setRequired(true);
								cbFormaContato.setWidth("260px");
								cbFormaContato.setTextInputAllowed(false);
								cbFormaContato.setNullSelectionAllowed(false);					
								cbFormaContato.setConverter(new SingleSelectConverter(cbFormaContato));			
								cbFormaContato.setItemCaptionPropertyId("nome");
								cbFormaContato.setStyleName("caption-align-crmFeed");
								
								addComponent(cbFormaContato);					
								fieldGroup.bind(cbFormaContato,"contato_feedback");
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
	
	public void buildLayoutTratar(){
		
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
								tfNProtocolo.setValue(codCrm.toString());					
								tfNProtocolo.setReadOnly(true);
								
								addComponent(tfNProtocolo);
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
										
										if(item.getItemProperty("operador").getValue() != null){			
											txtDataAbertura.setValue(data +", por "+item.getItemProperty("operador").getValue());	
										}else{
											txtDataAbertura.setValue(data +", por "+OpusERP4UI.getUsuarioLogadoUI().getUsername());											
										}
									}else{
										data = sdf.format(new Date());
										if(item.getItemProperty("operador").getValue() != null){			
											txtDataAbertura.setValue(data +", por "+item.getItemProperty("operador").getValue());	
										}else{
											txtDataAbertura.setValue(data +", por "+OpusERP4UI.getUsuarioLogadoUI().getUsername());											
										}
									}
									
									txtDataAbertura.setReadOnly(true);
									addComponent(txtDataAbertura);				
									
							}
						});
						
						addComponent(new FormLayout(){
							{
								
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								txtStatus = new TextField ("Status");
								txtStatus.setWidth("158px");
								txtStatus.setStyleName("caption-align-crmData");
								txtStatus.setNullRepresentation("");
								txtStatus.setValue("PENDENTE");					
								txtStatus.setReadOnly(true);
								
								addComponent(txtStatus);
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
				tfCodCliente.setReadOnly(true);
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
								tfDDD_fone3.setValue(Clientelecionado.getDdd_cel1());
								tfDDD_fone4.setValue(Clientelecionado.getDdd_cel2());
								tfTelefone1.setValue(Clientelecionado.getTelefone1());
								tfTelefone2.setValue(Clientelecionado.getTelefone2());
								tfTelefone3.setValue(Clientelecionado.getCelular1());
								tfTelefone4.setValue(Clientelecionado.getCelular2());
								txtEmail.setValue(Clientelecionado.getEmail());
								
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
								tfDDD_fone3.setValue("");
								tfDDD_fone4.setValue("");
								tfTelefone1.setValue("");
								tfTelefone2.setValue("");
								tfTelefone3.setValue("");
								tfTelefone4.setValue("");
								txtEmail.setValue("");
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
							tfDDD_fone3.setValue("");
							tfDDD_fone4.setValue("");
							tfTelefone1.setValue("");
							tfTelefone2.setValue("");
							tfTelefone3.setValue("");
							tfTelefone4.setValue("");
							txtEmail.setValue("");
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
						tfCodCliente.setReadOnly(false);
						Clientelecionado = c;
						tfCodCliente.setValue(c.getId().toString());
						tfCodCliente.setReadOnly(true);
						
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
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
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
									tfDDD_fone3.setValue(Clientelecionado.getDdd_cel1());
									tfDDD_fone4.setValue(Clientelecionado.getDdd_cel2());
									tfTelefone1.setValue(Clientelecionado.getTelefone1());
									tfTelefone2.setValue(Clientelecionado.getTelefone2());
									tfTelefone3.setValue(Clientelecionado.getCelular1());
									tfTelefone4.setValue(Clientelecionado.getCelular2());
									txtEmail.setValue(Clientelecionado.getEmail());
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
//						addComponent(btSearchCliente);							
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
				
			}	
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
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
						
						txtCpfCnpj.setReadOnly(true);
						
						addComponent(txtCpfCnpj);					
						//fieldGroup.bind(tfNProtocolo,"nome");
					}
				});
		
		vlRoot.addComponent(
				new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						txtContato = new TextField ("Contato");				
						txtContato.setStyleName("caption-align-acesso");
						txtContato.setNullRepresentation("");		
						
						if(Clientelecionado.getContato() != null){
							txtContato.setValue(Clientelecionado.getContato());
						}
						txtContato.setReadOnly(true);	
						
						
						addComponent(txtContato);					
						//fieldGroup.bind(tfNProtocolo,"nome");
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
						tfDDD_fone1.setReadOnly(true);
						
						addComponent(tfDDD_fone1);
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
						tfTelefone1.setReadOnly(true);
						
						if(tfDDD_fone1.getValue() != null && !tfDDD_fone1.getValue().equals("") && 
								tfTelefone1.getValue() != null && !tfTelefone1.getValue().equals("")){
							
							tfTelefone1.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone1.setReadOnly(false);
							tfTelefone1.setValue(tfTelefone1.getValue());
							tfTelefone1.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone1.getValue()+tfTelefone1.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone1);
						}
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
						tfDDD_fone2.setReadOnly(true);
						
						addComponent(tfDDD_fone2);
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
						tfTelefone2.setReadOnly(true);
						
						if(tfDDD_fone2.getValue() != null && !tfDDD_fone2.getValue().equals("") && 
								tfTelefone2.getValue() != null && !tfTelefone2.getValue().equals("")){
							
							
							tfTelefone2.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone2.setReadOnly(false);
							tfTelefone2.setValue(tfTelefone2.getValue());
							tfTelefone2.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone2.getValue()+tfTelefone2.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone2);
						}
						
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
						tfDDD_fone3 = new TextField("Telefone Alternativo");
						tfDDD_fone3.setNullRepresentation("");
						tfDDD_fone3.setStyleName("caption-align-contato-crm");
						tfDDD_fone3.setWidth("40px");
						tfDDD_fone3.setMaxLength(2);
						
						if(Clientelecionado.getDdd_cel1() != null){
							tfDDD_fone3.setValue(Clientelecionado.getDdd_cel1());
						}
						tfDDD_fone3.setReadOnly(true);
						
						addComponent(tfDDD_fone3);
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
						tfTelefone3.setReadOnly(true);
						
						if(tfDDD_fone3.getValue() != null && !tfDDD_fone3.getValue().equals("") && 
								tfTelefone3.getValue() != null && !tfTelefone3.getValue().equals("")){
							
							tfTelefone3.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone3.setReadOnly(false);
							tfTelefone3.setValue(tfTelefone3.getValue());
							tfTelefone3.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone3.getValue()+tfTelefone3.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone3);
						}
					}
				});
				
				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						tfDDD_fone4 = new TextField("Telefone Alternativo");
						tfDDD_fone4.setNullRepresentation("");
						tfDDD_fone4.setMaxLength(2);
						tfDDD_fone4.setWidth("40px");
						
						if(Clientelecionado.getCelular2() != null){
							tfDDD_fone4.setValue(Clientelecionado.getDdd_fone2());
						}
						tfDDD_fone4.setReadOnly(true);
						
						addComponent(tfDDD_fone4);
					}
				});
				
				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom");
						
						
						tfTelefone4 = new TextField();
						
						addComponent(tfTelefone4);
						tfTelefone4.setNullRepresentation("");
						tfTelefone4.setWidth("105px");
						tfTelefone4.setMaxLength(11);
						
						if(Clientelecionado.getCelular2() != null){
							tfTelefone4.setValue(Clientelecionado.getCelular2());
						}
						tfTelefone4.setReadOnly(true);
						
						if(tfDDD_fone4.getValue() != null && !tfDDD_fone4.getValue().equals("") && 
								tfTelefone4.getValue() != null && !tfTelefone4.getValue().equals("")){
							
							tfTelefone4.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone4.setReadOnly(false);
							tfTelefone4.setValue(tfTelefone1.getValue());
							tfTelefone4.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone4.getValue()+tfTelefone4.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone4);
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
						
						txtEmail = new TextField ("E-mail");		
						txtEmail.setWidth("260px");
						txtEmail.setStyleName("caption-align-acesso");
						txtEmail.setNullRepresentation("");	
						
						if(Clientelecionado.getEmail() != null){
							txtEmail.setValue(Clientelecionado.getEmail());
							valid_email = true;
							
							emailInicial = Clientelecionado.getEmail();
						}
						txtEmail.setReadOnly(true);
						
						addComponent(txtEmail);					
						//fieldGroup.bind(tfNProtocolo,"nome");
					}
				});
		
		vlRoot.addComponent(
				new FormLayout(){					
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
						cbFormaContato.setReadOnly(true);
					}
				});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
						
						
						ComboBox cbSetor = new ComboBox("Setor", buildContainerSetores());
						//cbSetor.setRequired(true);
						cbSetor.setWidth("260px");
						cbSetor.setTextInputAllowed(false);
						cbSetor.setNullSelectionAllowed(false);					
						cbSetor.setConverter(new SingleSelectConverter(cbSetor));
						cbSetor.setItemCaptionPropertyId("nome");
						cbSetor.setStyleName("caption-align-acesso");
										
						addComponent(cbSetor);					
						fieldGroup.bind(cbSetor,"setor");
						cbSetor.setReadOnly(true);
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
								if(((TextArea) fieldGroup.getField("conteudo")) != null){
									((TextArea) fieldGroup.getField("conteudo")).setValue(cbAssunto.getItem(cbAssunto.getValue()).getItemProperty("conteudo").getValue().toString());
								}
							}
						});
						
						
						addComponent(cbAssunto);					
						fieldGroup.bind(cbAssunto,"crm_assuntos");
						cbAssunto.setReadOnly(true);
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
						taObservacao.setWidth("777");
						taObservacao.setHeight("189px");
						taObservacao.setStyleName("caption-align-acesso");
						//taObservacao.addStyleName("uppercase");
						taObservacao.addStyleName("font-courier-new");
						
						addComponent(taObservacao);					
						fieldGroup.bind(taObservacao,"conteudo");
						
						taObservacao.setReadOnly(true);
						
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
								dfPrevisao.setValue(new Date());
								dfPrevisao.setReadOnly(true);
								
								
								
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
							}
						});
						
						addComponent(new FormLayout(){
							{
								
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								txtHora = new ComboBox("Hora");
								txtHora.setRequired(true); 
								txtHora.setNullSelectionAllowed(false);	
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
								txtHora.setReadOnly(true);
								
								JavaScript.getCurrent().execute("$('#txtHoraCrm').mask('00:00')");
								
								addComponent(txtHora);
								//fieldGroup.bind(txtHora,"hora_agendado");
							}
						});
						
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								
								ComboBox cbFormaContato = new ComboBox("Contato Feedback", buildContainerFormaContato());
								cbFormaContato.setRequired(true);
								cbFormaContato.setWidth("260px");
								cbFormaContato.setTextInputAllowed(false);
								cbFormaContato.setNullSelectionAllowed(false);					
								cbFormaContato.setConverter(new SingleSelectConverter(cbFormaContato));			
								cbFormaContato.setItemCaptionPropertyId("nome");
								cbFormaContato.setStyleName("caption-align-crmFeed");
								
								addComponent(cbFormaContato);			
								fieldGroup.bind(cbFormaContato,"contato_feedback");
																	
							}
						});
					}
				});	
		
		
		if(tratar){
			taObservacao.setReadOnly(false);
		
			txtStatus.setReadOnly(false);
			txtStatus.setValue("EM TRATAMENTO");
			txtStatus.setReadOnly(true);
			txtStatus.addStyleName("valid-cpf");

			hora_inicial_tratamento = new Date();
			addExtension(buildRefresher());		
			
		}
		
		
	}
	
	public void buildLayoutVisualizar(){
		
		fieldGroup = new FieldGroup(item);
					
		vlRoot.addComponent(new HorizontalLayout(){
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
									
									tfNProtocolo.setValue(codCrm.toString());			
									
									tfNProtocolo.setReadOnly(true);

									addComponent(tfNProtocolo);
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
										txtDataAbertura.setValue(data +", por "+item.getItemProperty("operador").getValue());	
									}else{
										data = sdf.format(new Date());
										txtDataAbertura.setValue(data +", por "+OpusERP4UI.getUsuarioLogadoUI().getUsername());
									}
									
									txtDataAbertura.setReadOnly(true);
									addComponent(txtDataAbertura);				
									
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
										if(item.getItemProperty("status").getValue().equals("AGENDADO")){	
											txtStatus.setValue("PENDENTE");
										}else if(item.getItemProperty("status").getValue().equals("EFETUADO")){
											txtStatus.setValue("EFETUADO");
										}else{
											txtStatus.setValue("EM TRATAMENTO");
										}
									}
									txtStatus.setReadOnly(true);					

									addComponent(txtStatus);
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
								tfDDD_fone3.setValue(Clientelecionado.getDdd_cel1());
								tfDDD_fone4.setValue(Clientelecionado.getDdd_cel2());
								tfTelefone1.setValue(Clientelecionado.getTelefone1());
								tfTelefone2.setValue(Clientelecionado.getTelefone2());
								tfTelefone3.setValue(Clientelecionado.getCelular1());
								tfTelefone4.setValue(Clientelecionado.getCelular2());
								txtEmail.setValue(Clientelecionado.getEmail());
								emailInicial = Clientelecionado.getEmail();
								
								valid_email =true;
								
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
								tfDDD_fone3.setValue("");
								tfDDD_fone4.setValue("");
								tfTelefone1.setValue("");
								tfTelefone2.setValue("");
								tfTelefone3.setValue("");
								tfTelefone4.setValue("");
								txtEmail.setValue("");
								emailInicial = "";
								
								valid_email =true;
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
							tfDDD_fone3.setValue("");
							tfDDD_fone4.setValue("");
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
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
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
										tfDDD_fone3.setValue(Clientelecionado.getDdd_cel1());
										tfDDD_fone4.setValue(Clientelecionado.getDdd_cel2());
										tfTelefone1.setValue(Clientelecionado.getTelefone1());
										tfTelefone2.setValue(Clientelecionado.getTelefone2());
										tfTelefone3.setValue(Clientelecionado.getCelular1());
										tfTelefone4.setValue(Clientelecionado.getCelular2());
										txtEmail.setValue(Clientelecionado.getEmail());
										
										valid_email =true;
										emailInicial = Clientelecionado.getEmail();
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

			}	
		});
		
		
		vlRoot.addComponent(new FormLayout(){					
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
					//fieldGroup.bind(tfNProtocolo,"nome");
				}
			});
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
						
				TextField txtEndereco = new TextField("Endereço");
				txtEndereco.setStyleName("caption-align-acesso");
				txtEndereco.setWidth("685px");
				
							
				addComponent(txtEndereco);
				
				if(entityEndereco != null){
					txtEndereco.setValue(entityEndereco.getCep()+", "+entityEndereco.getEndereco()+", "+entityEndereco.getBairro()+", "+entityEndereco.getCidade()+" - "+entityEndereco.getUf());					
				}
				
				txtEndereco.setReadOnly(true);
								
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
					
					if(entityEndereco != null){
						txtComplemento.setValue(entityEndereco.getComplemento());					
					}
					
					txtComplemento.setReadOnly(true);
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
					
					if(entityEndereco != null){
						txtReferencia.setValue(entityEndereco.getReferencia());					
					}
					
					txtReferencia.setReadOnly(true);
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
						}
					});
				}
			});
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
						
						
						if(tfDDD_fone1.getValue() != null && !tfDDD_fone1.getValue().equals("") && 
								tfTelefone1.getValue() != null && !tfTelefone1.getValue().equals("")){
							
							tfTelefone1.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone1.setReadOnly(false);
							tfTelefone1.setValue(tfTelefone1.getValue());
							tfTelefone1.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone1.getValue()+tfTelefone1.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone1);
						}
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
						//tfTelefone2	
						
						if(Clientelecionado.getTelefone2() != null){
							tfTelefone2.setValue(Clientelecionado.getTelefone2());
						}
						
						if(codCrm != null){
							tfTelefone2.setReadOnly(true);
						}
						
						if(tfDDD_fone2.getValue() != null && !tfDDD_fone2.getValue().equals("") && 
								tfTelefone2.getValue() != null && !tfTelefone2.getValue().equals("")){
							
							tfTelefone2.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone2.setReadOnly(false);
							tfTelefone2.setValue(tfTelefone2.getValue());
							tfTelefone2.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone2.getValue()+tfTelefone2.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone2);
						}


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
						tfDDD_fone3 = new TextField("Telefone Alternativo");
						tfDDD_fone3.setNullRepresentation("");
						tfDDD_fone3.setStyleName("caption-align-contato-crm");	
						tfDDD_fone3.setWidth("40px");
						tfDDD_fone3.setMaxLength(2);
						
						if(Clientelecionado.getDdd_cel1() != null){
							tfDDD_fone3.setValue(Clientelecionado.getDdd_cel1());
						}

						if(codCrm != null){
							tfDDD_fone3.setReadOnly(true);
						}
						
						addComponent(tfDDD_fone3);
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
//						tfTelefone3.setStyleName("v-caption-caption-align-url-zap-crm");
						
						if(Clientelecionado.getCelular1() != null){
							tfTelefone3.setValue(Clientelecionado.getCelular1());
						}
						
						if(codCrm != null){
							tfTelefone3.setReadOnly(true);
						}
						
						if(tfDDD_fone3.getValue() != null && !tfDDD_fone3.getValue().equals("") && 
								tfTelefone3.getValue() != null && !tfTelefone3.getValue().equals("")){
							
							tfTelefone3.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone3.setReadOnly(false);
							tfTelefone3.setValue(tfTelefone3.getValue());
							tfTelefone3.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone3.getValue()+tfTelefone3.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone3);
						}
						
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						tfDDD_fone4 = new TextField("Telefone Alternativo");
						tfDDD_fone4.setNullRepresentation("");
						tfDDD_fone4.setMaxLength(2);
						tfDDD_fone4.setWidth("40px");
						
						if(Clientelecionado.getCelular2() != null){
							tfDDD_fone4.setValue(Clientelecionado.getDdd_cel2());
						}
						
						if(codCrm != null){
							tfDDD_fone4.setReadOnly(true);
						}

						tfDDD_fone4.setReadOnly(true);
						addComponent(tfDDD_fone4);
					}
				});

				addComponent(new FormLayout() {
					{
						// setMargin(true);
						// setSpacing(true);
						setStyleName("form-cutom");
						
						
						tfTelefone4 = new TextField();
						
						addComponent(tfTelefone4);
						tfTelefone4.setNullRepresentation("");
						tfTelefone4.setWidth("105px");
						tfTelefone4.setMaxLength(11);
						
						if(Clientelecionado.getCelular2() != null){
							tfTelefone4.setValue(Clientelecionado.getCelular2());
						}
						tfTelefone4.setReadOnly(true);
						
						
						if(tfDDD_fone4.getValue() != null && !tfDDD_fone4.getValue().equals("") && 
								tfTelefone4.getValue() != null && !tfTelefone4.getValue().equals("")){
							
							tfTelefone4.setStyleName("v-textfield-v-caption-caption-align-url-zap-crm");
							
							tfTelefone4.setReadOnly(false);
							tfTelefone4.setValue(tfTelefone4.getValue());
							tfTelefone4.setReadOnly(true);
							
							BrowserWindowOpener openIpPool = new BrowserWindowOpener("http://wa.me/550"+tfDDD_fone4.getValue()+tfTelefone4.getValue());
							openIpPool.setFeatures("height=600,width=800");
							openIpPool.extend(tfTelefone4);
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
					
					txtEmail.setReadOnly(true);
					addComponent(txtEmail);		
					//fieldGroup.bind(tfNProtocolo,"nome");
				}
			});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
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
					cbFormaContato.setReadOnly(true);
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
						
					
					final ComboBox cbSetor = new ComboBox("Setor", buildContainerSetores());
					//cbSetor.setRequired(true);
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
					cbSetor.setReadOnly(true);
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
					cbAssunto.setReadOnly(true);
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
					//taObservacao.addStyleName("uppercase");
					taObservacao.addStyleName("font-courier-new");
					
					addComponent(taObservacao);					
					fieldGroup.bind(taObservacao,"conteudo");
					taObservacao.setReadOnly(true);
					
					
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
									
									dfPrevisao.setReadOnly(true);
												
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
											
											txtHora.setReadOnly(true);
//											txtHora.focus();	
											
										}
									});		
							}
						});
						
						addComponent(new FormLayout(){
							{

									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									txtHora = new ComboBox("Hora");
									txtHora.setRequired(true); 
									txtHora.setNullSelectionAllowed(false);	
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
										txtHora.select(item.getItemProperty("hora_agendado").getValue().toString());
									}
									
									JavaScript.getCurrent().execute("$('#txtHoraCrm').mask('00:00')");
									
									addComponent(txtHora);
									txtHora.setReadOnly(true);
									//fieldGroup.bind(txtHora,"hora_agendado");
							}
						});
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								
								ComboBox cbFormaContato = new ComboBox("Contato Feedback", buildContainerFormaContato());
								cbFormaContato.setRequired(true);
								cbFormaContato.setWidth("260px");
								cbFormaContato.setTextInputAllowed(false);
								cbFormaContato.setNullSelectionAllowed(false);					
								cbFormaContato.setConverter(new SingleSelectConverter(cbFormaContato));			
								cbFormaContato.setItemCaptionPropertyId("nome");
								cbFormaContato.setStyleName("caption-align-crmFeedVisu");
								
								addComponent(cbFormaContato);					
								fieldGroup.bind(cbFormaContato,"contato_feedback");
								cbFormaContato.setReadOnly(true);
								
							}
						});
					}
		});	
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
				    
					if(event.getButton().getCaption().equals("Concluir") || event.getButton().getCaption().equals("Avançar")){
					GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Deseja Realmente Fechar este CRM ?", true, true);
									
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
												
								if(fieldGroup.isValid() && valid_email && tfCodCliente.getValue() != null && !tfCodCliente.getValue().equals("") &&								   
								   validarData){
									
									try{
																				
										if(crm.getCrm_assuntos().getPerguntas_ativas().equals("ATIVO")){
											
											CrmPesquisaEditor crmPesqEditor = new CrmPesquisaEditor(crm, "Pesquisa de Satisfação", true,false);
											crmPesqEditor.addListerner(new CrmPesquisaEditor.CrmPesquisarEditorListerner() {
												
												@Override
												public void onClose(CrmPesquisarEditorEvent event) {
													if(event.isConfirm()){
														item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
														item.getItemProperty("cliente").setValue(Clientelecionado);
														item.getItemProperty("origem").setValue("OPUS");
																											
//														item.getItemProperty("contato_feedback").setValue(cbFormaContato.getValue());
														
														if(item.getItemProperty("id").getValue() == null){						
															item.getItemProperty("status").setValue("AGENDADO");											
														}else{
															item.getItemProperty("status").setValue("EFETUADO");
															item.getItemProperty("data_efetuado").setValue(new Date());
														}
														item.getItemProperty("contato").setValue(Clientelecionado.getContato());
														item.getItemProperty("operador").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
														
														if(txtHora.getValue() != null  && !txtHora.getValue().equals("")){
															SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
															try {
																item.getItemProperty("hora_agendado").setValue(sdf.parse(txtHora.getValue().toString()));
															} catch (Exception e) {
															
																e.printStackTrace();
															}
														}
														
														try {
															fieldGroup.commit();
														} catch (CommitException e) {
															
															e.printStackTrace();
														}				
								
														Clientelecionado.setContato(txtContato.getValue());
														Clientelecionado.setTelefone1(tfTelefone1.getValue());
														Clientelecionado.setTelefone2(tfTelefone2.getValue());
														Clientelecionado.setCelular1(tfTelefone3.getValue());
														Clientelecionado.setCelular2(tfTelefone4.getValue());
														Clientelecionado.setDdd_fone1(tfDDD_fone1.getValue());
														Clientelecionado.setDdd_fone2(tfDDD_fone2.getValue());
														Clientelecionado.setDdd_cel1(tfDDD_fone3.getValue());;
														Clientelecionado.setDdd_cel2(tfDDD_fone4.getValue());					
														
														Clientelecionado.setEmail(txtEmail.getValue());					
														
														ClienteDAO.saveCliente(Clientelecionado);
														
														fireEvent(new NovoContatoEvent(getUI(), item, true));
														
														Notify.Show("Gravado com Sucesso", Notify.TYPE_SUCCESS);
														
														//removeExtension(buildRefresher());
														
														
														close();
													}													
												}
											});
											
											getUI().addWindow(crmPesqEditor);
											
										}else{
											item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
											item.getItemProperty("cliente").setValue(Clientelecionado);
											item.getItemProperty("origem").setValue("OPUS");
											
//											item.getItemProperty("contato_feedback").setValue(cbFormaContato.getValue().toString());
											
											if(item.getItemProperty("id").getValue() == null){						
												item.getItemProperty("status").setValue("AGENDADO");											
											}else{
												item.getItemProperty("status").setValue("EFETUADO");
												item.getItemProperty("data_efetuado").setValue(new Date());
											}
											item.getItemProperty("contato").setValue(Clientelecionado.getContato());
											item.getItemProperty("operador").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
											
											if(txtHora.getValue() != null &&  !txtHora.getValue().equals("")){
												SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
												item.getItemProperty("hora_agendado").setValue(sdf.parse(txtHora.getValue().toString()));
											}
											
											fieldGroup.commit();				
					
											Clientelecionado.setContato(txtContato.getValue());
											Clientelecionado.setTelefone1(tfTelefone1.getValue());
											Clientelecionado.setTelefone2(tfTelefone2.getValue());
											Clientelecionado.setCelular1(tfTelefone3.getValue());
											Clientelecionado.setCelular2(tfTelefone4.getValue());
											Clientelecionado.setDdd_fone1(tfDDD_fone1.getValue());
											Clientelecionado.setDdd_fone2(tfDDD_fone2.getValue());
											Clientelecionado.setDdd_cel1(tfDDD_fone3.getValue());;
											Clientelecionado.setDdd_cel2(tfDDD_fone4.getValue());					
											
											Clientelecionado.setEmail(txtEmail.getValue());					
											
											ClienteDAO.saveCliente(Clientelecionado);
											
											fireEvent(new NovoContatoEvent(getUI(), item, true));
											
											Notify.Show("Gravado com Sucesso", Notify.TYPE_SUCCESS);
											
											//removeExtension(buildRefresher());
											
											
											close();
										}
										
										
										
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
					
					if(crm.getCrm_assuntos().getPerguntas_ativas().equals("ATIVO")){
						if(codCrm!=null){
							validarData = true;			
						}
						
						if(txtEmail.getValue() == null || txtEmail.getValue().isEmpty() || txtEmail.getValue() == ""){
							valid_email = true;
						}
										
						if(fieldGroup.isValid() && valid_email && tfCodCliente.getValue() != null && !tfCodCliente.getValue().equals("") &&								   
						   validarData){
							
							try{
																		
								if(crm.getCrm_assuntos().getPerguntas_ativas().equals("ATIVO")){
									
									CrmPesquisaEditor crmPesqEditor = new CrmPesquisaEditor(crm, "Pesquisa de Satisfação", true,false);
									crmPesqEditor.addListerner(new CrmPesquisaEditor.CrmPesquisarEditorListerner() {
										
										@Override
										public void onClose(CrmPesquisarEditorEvent event) {
											if(event.isConfirm()){
												item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
												item.getItemProperty("cliente").setValue(Clientelecionado);
												item.getItemProperty("origem").setValue("OPUS");
												
												if(item.getItemProperty("id").getValue() == null){						
													item.getItemProperty("status").setValue("AGENDADO");											
												}else{
													item.getItemProperty("status").setValue("EFETUADO");
													item.getItemProperty("data_efetuado").setValue(new Date());
												}
												item.getItemProperty("contato").setValue(Clientelecionado.getContato());
												item.getItemProperty("operador").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
												
												if(txtHora.getValue() != null && !txtHora.getValue().equals("")){
													SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
													try {
														item.getItemProperty("hora_agendado").setValue(sdf.parse(txtHora.getValue().toString()));
													} catch (Exception e) {
														
														e.printStackTrace();
													}
												}
												
												try {
													fieldGroup.commit();
												} catch (CommitException e) {
													
													e.printStackTrace();
												}				
						
												Clientelecionado.setContato(txtContato.getValue());
												Clientelecionado.setTelefone1(tfTelefone1.getValue());
												Clientelecionado.setTelefone2(tfTelefone2.getValue());
												Clientelecionado.setCelular1(tfTelefone3.getValue());
												Clientelecionado.setCelular2(tfTelefone4.getValue());
												Clientelecionado.setDdd_fone1(tfDDD_fone1.getValue());
												Clientelecionado.setDdd_fone2(tfDDD_fone2.getValue());
												Clientelecionado.setDdd_cel1(tfDDD_fone3.getValue());;
												Clientelecionado.setDdd_cel2(tfDDD_fone4.getValue());					
												
												Clientelecionado.setEmail(txtEmail.getValue());					
												
												ClienteDAO.saveCliente(Clientelecionado);
												
												fireEvent(new NovoContatoEvent(getUI(), item, true));
												
												Notify.Show("Gravado com Sucesso", Notify.TYPE_SUCCESS);
												
												//removeExtension(buildRefresher());
												
												
												close();
											}													
										}
									});
									
									getUI().addWindow(crmPesqEditor);
									
								}else{
									item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
									item.getItemProperty("cliente").setValue(Clientelecionado);
									item.getItemProperty("origem").setValue("OPUS");
									
									if(item.getItemProperty("id").getValue() == null){						
										item.getItemProperty("status").setValue("AGENDADO");											
									}else{
										item.getItemProperty("status").setValue("EFETUADO");
										item.getItemProperty("data_efetuado").setValue(new Date());
									}
									item.getItemProperty("contato").setValue(Clientelecionado.getContato());
									item.getItemProperty("operador").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
									
									if(txtHora.getValue() != null && !txtHora.getValue().equals("")){
										SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
										item.getItemProperty("hora_agendado").setValue(sdf.parse(txtHora.getValue().toString()));
									}
									
									fieldGroup.commit();				
			
									Clientelecionado.setContato(txtContato.getValue());
									Clientelecionado.setTelefone1(tfTelefone1.getValue());
									Clientelecionado.setTelefone2(tfTelefone2.getValue());
									Clientelecionado.setCelular1(tfTelefone3.getValue());
									Clientelecionado.setCelular2(tfTelefone4.getValue());
									Clientelecionado.setDdd_fone1(tfDDD_fone1.getValue());
									Clientelecionado.setDdd_fone2(tfDDD_fone2.getValue());
									Clientelecionado.setDdd_cel1(tfDDD_fone3.getValue());;
									Clientelecionado.setDdd_cel2(tfDDD_fone4.getValue());					
									
									Clientelecionado.setEmail(txtEmail.getValue());					
									
									ClienteDAO.saveCliente(Clientelecionado);
									
									fireEvent(new NovoContatoEvent(getUI(), item, true));
									
									Notify.Show("Gravado com Sucesso", Notify.TYPE_SUCCESS);
									
									//removeExtension(buildRefresher());
									
									
									close();
								}
								
								
								
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
							
							
							Notify.Show_Invalid_Submit_Form();
						}
					}else{						
						getUI().addWindow(gd);
					}
					
					
					}else{
						if(codCrm!=null){
							validarData = true;			
						}
						
						if(txtEmail.getValue() == null || txtEmail.getValue().isEmpty() || txtEmail.getValue() == ""){
							valid_email = true;
						}
										
						if(fieldGroup.isValid() && valid_email && tfCodCliente.getValue() != null && !tfCodCliente.getValue().equals("") && 
						   tfDDD_fone1.getValue()!=null && !tfDDD_fone1.getValue().equals("") && tfTelefone1.getValue()!=null && 
						   !tfTelefone1.getValue().equals("") && validarData && txtContato.getValue()!=null && !txtContato.getValue().equals("") && txtHora.isValid()){
							try{
								
								if(contratoSelecionado != null){
									item.getItemProperty("contrato").setValue(contratoSelecionado);
									item.getItemProperty("end").setValue(contratoSelecionado.getEndereco());
								}
								
								item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
								item.getItemProperty("cliente").setValue(Clientelecionado);
								item.getItemProperty("origem").setValue("OPUS");
								
								if(item.getItemProperty("id").getValue() == null || status.equals("EM ANALISE")){						
									item.getItemProperty("status").setValue("AGENDADO");
								}else{
									item.getItemProperty("status").setValue("EFETUADO");
									item.getItemProperty("data_efetuado").setValue(new Date());
								}
								item.getItemProperty("contato").setValue(Clientelecionado.getContato());
								item.getItemProperty("operador").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
								
								if(txtHora.getValue() != null && !txtHora.getValue().equals("")){
									SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
									item.getItemProperty("hora_agendado").setValue(sdf.parse(txtHora.getValue().toString()));
								}
								
								fieldGroup.commit();				
		
								Clientelecionado.setContato(txtContato.getValue());
								Clientelecionado.setTelefone1(tfTelefone1.getValue());
								Clientelecionado.setTelefone2(tfTelefone2.getValue());
								Clientelecionado.setCelular1(tfTelefone3.getValue());
								Clientelecionado.setCelular2(tfTelefone4.getValue());
								Clientelecionado.setDdd_fone1(tfDDD_fone1.getValue());
								Clientelecionado.setDdd_fone2(tfDDD_fone2.getValue());
								Clientelecionado.setDdd_cel1(tfDDD_fone3.getValue());;
								Clientelecionado.setDdd_cel2(tfDDD_fone4.getValue());					
								
								Clientelecionado.setEmail(txtEmail.getValue());					
								
								ClienteDAO.saveCliente(Clientelecionado);
								
								
								fireEvent(new NovoContatoEvent(getUI(), item, true));						
								
								Notify.Show("Gravado com Sucesso", Notify.TYPE_SUCCESS);
								
								//removeExtension(buildRefresher());
								//								removeExtension(refresherTempoTratamento);
								
								close();
							} catch (Exception e) {					
								e.printStackTrace();					
								
								Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
								//LogErrorDAO.add(new LogError(null, this.getClass().getSimpleName(), "buildBtSalvar", e.getLocalizedMessage(), OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
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
							
							if(!txtHora.isValid()){
								txtHora.addStyleName("invalid-txt");							
							}else{
								txtHora.removeStyleName("invalid-txt");
							}
							
							if(item.getItemProperty("data_agendado").getValue()==null||item.getItemProperty("data_agendado").getValue().equals("")||!validarData){
								dfPrevisao.addStyleName("invalid-txt");							
							}else{
								dfPrevisao.removeStyleName("invalid-txt");
							}
							
							
							Notify.Show_Invalid_Submit_Form();
						}
					}
//				}
				
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


	
	
	public Button buildBtPesquisa() {
		btPesquisa = new Button("Pesquisa", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(crm.getCrm_assuntos().getPerguntas_ativas().equals("ATIVO")){
					
					CrmPesquisaEditor crmPesqEditor = new CrmPesquisaEditor(crm, "Pesquisa de Satisfação", true, true);
					crmPesqEditor.addListerner(new CrmPesquisaEditor.CrmPesquisarEditorListerner() {
						
						@Override
						public void onClose(CrmPesquisarEditorEvent event) {
							if(event.isConfirm()){

							}													
						}
					});
					
					getUI().addWindow(crmPesqEditor);		
				}
				
//					close();			
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btPesquisa.click();
			}
		};
		
		btPesquisa.addShortcutListener(slbtCancelar);
		return btPesquisa;
	}
	
	
	
	
	
	public Button buildBtCancelar(String s) {
		btCancelar = new Button(s, new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				//if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new NovoContatoEvent(getUI(), item, false));
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

	public Button buildBtEncaminharOutroNivel() {
		btEncaminharOutroNivel = new Button("Encaminhar Nível", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
						
				GenericDialog gd = new GenericDialog("Confirme para Continuar !", "Deseja encaminhar este CRM para outro Nível ?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						EntityManager em = ConnUtil.getEntity();
						Crm crm = em.find(Crm.class, item.getItemProperty("id").getValue());
						
						if(crm.getNivel().equals("NIVEL I")){
							crm.setNivel("NIVEL II");
						}else if(crm.getNivel().equals("NIVEL II")){
							crm.setNivel("NIVEL III");
						}
						
						em.getTransaction().begin();
						em.merge(crm);
						em.getTransaction().commit();
						
						fieldGroup.discard();				
						fireEvent(new NovoContatoEvent(getUI(), item, false));
						fireEvent(new EncaminharContatoEvent(getUI(), item, false));
						
						close();
					}
				});
				
				getUI().addWindow(gd); 
			}
		});		
		
		return btEncaminharOutroNivel;
	}
	
	public Button buildBtAbrirOs() {
		btAbrirOs = new Button("Abrir OS", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
						
				GenericDialog gd = new GenericDialog("Confirme para Continuar !", "Deseja abrir uma OS para este CRM ?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						final BeanItem<Ose> item = new BeanItem<Ose>(new Ose());
						
						final RoteirizacaoEditor roteirizacaoEditor = new RoteirizacaoEditor(item, "Abrir Novo Chamado",Clientelecionado,taObservacao.getValue(),true);
						roteirizacaoEditor.addListerner(new RoteirizacaoEditor.RoteirizacaoListerner() {
							
							
							public void onClose(RoteirizacaoEvent event) {
								if(event.isConfirm()){
									
									try{
										
										Ose ose = item.getBean();									
										ose.setValor(Real.formatDbToString(String.valueOf(ose.getTipo_subgrupo().getValor())));
										OseDAO.save(ose);
										
										
										//Atualiza Cliente
										Cliente cliente = (Cliente)item.getItemProperty("cliente").getValue();								
										ClienteDAO.saveCliente(cliente);
										
										//Atualiza Endereço
										EnderecoDAO.save((Endereco)item.getItemProperty("end").getValue());									
										
										String valor = Real.formatDbToString(String.valueOf(ose.getTipo_subgrupo().getValor()));
										
										
										if(!valor.equals("0,00") && !valor.equals("null") && cliente != null && ose != null 
												&& ose.getData_ex() != null){
											ConnUtil.getEntity().getTransaction().begin();
											SimpleDateFormat sdf = new  SimpleDateFormat("yy");
											Date dataAtual = new Date();
											
											ContasReceber c;
											if(ose.getContrato() != null && ose.getContrato().getId() != null){
												c = ContasReceberDAO.gerarBoletos(new ContasReceber(cliente,ose.getContrato().getId()+"-"+"OS"+ose.getId().toString(),valor, new Date(), ose.getVencimento(), "ABERTO", "BOLETO","SERVICO"));
											}else{
												c = ContasReceberDAO.gerarBoletos(new ContasReceber(cliente,"OS"+ose.getId().toString(),valor, new Date(), ose.getVencimento(), "ABERTO", "BOLETO","SERVICO"));
											}
											ConnUtil.getEntity().getTransaction().commit();;
											
											
											if(c != null){
												SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM/yyyy");
												Notification.show("Título Gerado", "Código: "+c.getId().toString()+"\n"+
																				   "Nº Documento: "+c.getN_doc()+"\n"+
														                           "Vencimento: "+sdfData.format(c.getData_vencimento()),Type.ERROR_MESSAGE);
											}
										}
										
										AlteracoesOseDAO.add(new AlteracoesOse(null, "ABERTA", new Ose((Integer)event.getItem().getItemProperty("id").getValue()), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										
										roteirizacaoEditor.close();
										Notify.Show("Os Aberta com Sucesso!", Notify.TYPE_SUCCESS);
										
										
									}catch(Exception e){
										e.printStackTrace();
										Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
									}
								}
							}
						});
						
						
						
						getUI().addWindow(roteirizacaoEditor);	
					}
				});
				
				getUI().addWindow(gd); 
			}
		});		
		
		return btAbrirOs;
	}
	
	public Button buildBtReagendar() {
		btReagendar = new Button("Reagendar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				ReagendarEditor reagendarEditor = new ReagendarEditor(item, "Reagendar", true);
				reagendarEditor.addListerner(new ReagendarEditor.ReagendamentoListerner() {
					
					@Override
					public void onClose(ReagendadoEvent event) {
						if(event.isConfirm()){
							
							//removeExtension(buildRefresher());
							//removeExtension(refresherTempoTratamento);
							
							close();								
						}
					}
				});
			
				getUI().addWindow(reagendarEditor);
			
			}
		});		
		
		return btReagendar;
	}
	
	Button btOcorrencia;
	public Button BuildBtOcorrencia(){
		
		btOcorrencia = new Button("Ocorrência", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Ocorrencias"))				
				{					
				
					if(item != null){
						
							final OcorrenciasEditor ocorrenciaEditor = new OcorrenciasEditor(item, "Ocorrências CRM", true, (Integer)item.getItemProperty("id").getValue());
																					
							getUI().addWindow(ocorrenciaEditor); 
						
					}
				}else{
					Notify.Show("Você não tem Permissão para Visualizar/Cadastrar Ocorrências!", Notify.TYPE_ERROR);
				}
			}
		});
		
		return btOcorrencia;
	}
	
	private Component BuildbtLog() {
		btLog = new Button("Log", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
					Integer codCrm = (Integer)item.getItemProperty("id").getValue();
					HistoricoAlteracoesCrm histAlte = new HistoricoAlteracoesCrm(true, true,codCrm);
					
					getUI().addWindow(histAlte);
				
			}
		 });
		return btLog;
	    }
	
	
	 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void addListerner(NovoContatoListerner target){
		try {
			Method method = NovoContatoListerner.class.getDeclaredMethod("onClose", NovoContatoEvent.class);
			addListener(NovoContatoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(NovoContatoListerner target){
		removeListener(NovoContatoEvent.class, target);
	}
	public static class NovoContatoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public NovoContatoEvent(Component source, Item item, boolean confirm) {
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
	public interface NovoContatoListerner extends Serializable{
		public void onClose(NovoContatoEvent event);
	}
	
	
	
	
	public void addListerner(EncaminharContatoListerner target){
		try {
			Method method = EncaminharContatoListerner.class.getDeclaredMethod("onOpen", EncaminharContatoEvent.class);
			addListener(EncaminharContatoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(EncaminharContatoListerner target){
		removeListener(EncaminharContatoEvent.class, target);
	}
	public static class EncaminharContatoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public EncaminharContatoEvent(Component source, Item item, boolean confirm) {
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
	public interface EncaminharContatoListerner extends Serializable{
		public void onOpen(EncaminharContatoEvent event);
	}

	
}
