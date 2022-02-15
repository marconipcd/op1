package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesOseDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.ContratoManutencaoDAO;
import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.dao.GrupoOseDAO;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.dao.SubGrupoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesOse;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratoManutencao;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.GrupoOse;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.OseProduto;
import com.digital.opuserp.domain.PlanosManutencao;
import com.digital.opuserp.domain.SubGrupoOse;
import com.digital.opuserp.domain.Swith;
import com.digital.opuserp.domain.TipoSubGrupoOse;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class RoteirizacaoEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TextField tfCod;
	
	private ComboBox cbTurno;
	private DateField dfPrevisao;
	private ComboBox cbHora;
	private Cliente ClienteSelecionado;
	private TextField txtCodCliente;
	private TextField txtDescricaoCliente;
	private TextField txtContato;
	
	private TextField txtDDD1;
	private TextField txtTelefone1;
	private TextField txtDDD2;
	private TextField txtTelefone2;
	private TextField txtDDD3;
	private TextField txtTelefone3;
	private TextField txtDDD4;
	private TextField txtTelefone4;
	private TextArea txtConclusao;
	
	private TextField txtEmail;
	private TextField txtValor;
	//private CheckBox ckbGratis;
	private ComboBox cbEndereco;
	
	private TextField txtComplemento;
	private TextField txtReferencia;
	private TextField txtContrato;
	private TextField txtStatusContrato;
	private TextField txtPrioridade;
	private TextField txtLimite;
	
	private AcessoCliente contratoSelecionado;
	private DateField dfVencimento;
	TextField txtQtd ;
	
	TextArea taUsoInterno;
	
	boolean validarData;
	boolean valid_data = false;
	
	private ComboBox cbSubGrupo;
	
	private String conteudo;
	TextField txtLocalizacao;
	
	ComboBox cbCto;
	
	public RoteirizacaoEditor(final Item item, String title, boolean modal){
		this.item = item;
		
		
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
				
				
				if(item != null && item.getItemProperty("id").getValue() == null){
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtSalvar());
				}else{
					hlButtons.addComponent(buildBtOcorrencia());
					hlButtons.addComponent(buildBtFechar());
				}
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		if(item != null && item.getItemProperty("id").getValue() != null){
			buildLayoutReadlyOnly();
		}else{
			buildLayout();			
		}
	}
	public RoteirizacaoEditor(final Item item, String title, Cliente c,String conteudo, boolean modal){
		this.item = item;
		this.conteudo = conteudo;
		
		if(c != null){
			ClienteSelecionado = c;
		}
		
		
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
				
				
				if(item != null && item.getItemProperty("id").getValue() == null){
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtSalvar());
				}else{
					hlButtons.addComponent(buildBtOcorrencia());
					hlButtons.addComponent(buildBtFechar());
				}
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		if(item != null && item.getItemProperty("id").getValue() != null){
			buildLayoutReadlyOnly();
		}else{
			buildLayout();			
		}
	}
	
	public void buildLayoutReadlyOnly(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(
					new FormLayout(){					
				    {
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
				
						TextField txtOs = new TextField("OS Número");
						txtOs.setStyleName("caption-align");
						addComponent(txtOs);
						
						if(item.getItemProperty("id").getValue() != null){
							txtOs.setValue(item.getItemProperty("id").getValue().toString());
							txtOs.setReadOnly(true);
						}
				    }
				});
				
				addComponent(
						new FormLayout(){					
					    {
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
					
							TextField txtAbertura = new TextField("Abertura");
							txtAbertura.setStyleName("caption-align-aberturaVisualizar");
							txtAbertura.setWidth("220px");
							addComponent(txtAbertura);
							
							if(item.getItemProperty("id").getValue() != null){
									
								String data = "";
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								if(item.getItemProperty("data_abertura").getValue() != null){		
									Date dataCad = (Date)item.getItemProperty("data_abertura").getValue();
									data = sdf.format(dataCad);
									txtAbertura.setValue(data +", por "+item.getItemProperty("operador_abertura").getValue());	
								}else{
									
								}
								txtAbertura.setReadOnly(true);
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
					
				final ComboBox cbGrupo = new ComboBox("Grupo", getGrupos());					
				cbGrupo.setStyleName("caption-align");
				cbGrupo.setNullSelectionAllowed(false);
				cbGrupo.setItemCaptionPropertyId("nome");					
				cbGrupo.setRequired(true);	
				cbGrupo.setTextInputAllowed(false);
				cbGrupo.setWidth("220px");
				cbGrupo.setConverter(new SingleSelectConverter(cbGrupo));								
				cbGrupo.setImmediate(true);
				
				cbGrupo.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						if(fieldGroup.getField("subgrupo") != null){
							((ComboBox)fieldGroup.getField("subgrupo")).setReadOnly(false);							
							((ComboBox)fieldGroup.getField("subgrupo")).setContainerDataSource(getSubGrupos());
							((ComboBox)fieldGroup.getField("subgrupo")).setItemCaptionPropertyId("nome");
							((ComboBox)fieldGroup.getField("subgrupo")).setConverter(new SingleSelectConverter(((ComboBox)fieldGroup.getField("subgrupo"))));
						}
						
					}
				});
				
				addComponent(cbGrupo);				
				fieldGroup.bind(cbGrupo,"grupo");
				cbGrupo.setReadOnly(true);
			}
		});
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){					
				{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						cbSubGrupo = new ComboBox("SubGrupo");					
						cbSubGrupo.setStyleName("caption-align");
						cbSubGrupo.setNullSelectionAllowed(false);									
						cbSubGrupo.setRequired(true);	
						cbSubGrupo.setReadOnly(true);
						cbSubGrupo.setTextInputAllowed(false);								
						cbSubGrupo.setImmediate(true);
						cbSubGrupo.setWidth("220px");
						
						cbSubGrupo.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(fieldGroup.getField("tipo_subgrupo") != null){
									((ComboBox)fieldGroup.getField("tipo_subgrupo")).setReadOnly(false);							
									((ComboBox)fieldGroup.getField("tipo_subgrupo")).setContainerDataSource(getTipoSubGrupos());
									((ComboBox)fieldGroup.getField("tipo_subgrupo")).setItemCaptionPropertyId("nome");
									((ComboBox)fieldGroup.getField("tipo_subgrupo")).setConverter(new SingleSelectConverter(((ComboBox)fieldGroup.getField("tipo_subgrupo"))));
																	
								}
							}
						});
						
						addComponent(cbSubGrupo);					
						
						if(((ComboBox)fieldGroup.getField("grupo")).getValue() != null){
							cbSubGrupo.setReadOnly(false);							
							cbSubGrupo.setContainerDataSource(getSubGrupos());
							cbSubGrupo.setItemCaptionPropertyId("nome");
							cbSubGrupo.setConverter(new SingleSelectConverter(cbSubGrupo));						
						}
	
						fieldGroup.bind(cbSubGrupo,"subgrupo");
						cbSubGrupo.setReadOnly(true);
					}
				});
					
				addComponent(new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						txtPrioridade = new TextField("Prioridade");
						txtPrioridade.setWidth("42px");
						if(((ComboBox)fieldGroup.getField("subgrupo")).getValue() != null){
							txtPrioridade.setValue(((ComboBox)fieldGroup.getField("subgrupo")).getItem(((ComboBox)fieldGroup.getField("subgrupo")).getValue()).getItemProperty("prioridade").getValue().toString());
							txtPrioridade.setReadOnly(true);
						}
						addComponent(txtPrioridade);				
						
					}
				});
			}			
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
				{
					addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
								
							final ComboBox cbTipoSubGrupo = new ComboBox("Tipo");					
							cbTipoSubGrupo.setStyleName("caption-align");
							cbTipoSubGrupo.setNullSelectionAllowed(false);
							cbTipoSubGrupo.setRequired(true);	
							cbTipoSubGrupo.setReadOnly(true);
							cbTipoSubGrupo.setTextInputAllowed(false);
							cbTipoSubGrupo.setImmediate(true);
							cbTipoSubGrupo.setWidth("220px");
												
							cbTipoSubGrupo.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									if(cbTipoSubGrupo.getValue() != null && txtValor != null){
										txtValor.setReadOnly(false);
										
										if(cbTipoSubGrupo.getItem(cbTipoSubGrupo.getValue()).getItemProperty("valor").getValue() != null && (Double)cbTipoSubGrupo.getItem(cbTipoSubGrupo.getValue()).getItemProperty("valor").getValue() > 0){
											txtValor.setValue(Real.formatDbToString(cbTipoSubGrupo.getItem(cbTipoSubGrupo.getValue()).getItemProperty("valor").getValue().toString()));
										}
										
										txtValor.setReadOnly(true);
									}else{
										if(txtValor != null){
											txtValor.setReadOnly(false);
											txtValor.setValue("");
											txtValor.setReadOnly(true);
										}
									}
								}
							});
							
							addComponent(cbTipoSubGrupo);
							
							if(((ComboBox)fieldGroup.getField("subgrupo")).getValue() != null){
								cbTipoSubGrupo.setReadOnly(false);							
								cbTipoSubGrupo.setContainerDataSource(getTipoSubGrupos());
								cbTipoSubGrupo.setItemCaptionPropertyId("nome");
								cbTipoSubGrupo.setConverter(new SingleSelectConverter(cbTipoSubGrupo));						
							}
							
							fieldGroup.bind(cbTipoSubGrupo,"tipo_subgrupo");
							cbTipoSubGrupo.setReadOnly(true);
						}
					});
					
					addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
								
							txtValor = new TextField("Valor (R$)");
							//txtValor.setStyleName("caption-align");
							txtValor.addStyleName("align-currency");
							txtValor.setReadOnly(true);
							
							addComponent(txtValor);		
							
							if(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getValue() != null && txtValor != null){
								txtValor.setReadOnly(false);
								
								if(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getItem(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getValue()).getItemProperty("valor").getValue() != null){
									txtValor.setValue(Real.formatDbToString(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getItem(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getValue()).getItemProperty("valor").getValue().toString()));
								}
								
								txtValor.setReadOnly(true);
							}else{
								if(txtValor != null){
									txtValor.setReadOnly(false);
									txtValor.setValue("");
									txtValor.setReadOnly(true);
								}
							}
							
							txtValor.setReadOnly(true);
							
						}
					});
					
					addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
							
							
							DateField dfVencimento = new  DateField("Vencimento");							
							//dfVencimento.setStyleName("caption-align");							
							dfVencimento.setReadOnly(true);
							
							addComponent(dfVencimento);							
				
						}
					});
				}
		});
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				JPAContainer<Swith> containerCto = JPAContainerFactory.makeReadOnly(Swith.class, ConnUtil.getEntity());
				containerCto.addContainerFilter(new Like("identificacao", "%CTO%"));
				
				cbCto = new ComboBox("CTO", containerCto);
				cbCto.setItemCaptionPropertyId("identificacao");						 
				cbCto.setStyleName("caption-align");
				cbCto.setNullSelectionAllowed(false);
				cbCto.setRequired(false);				
				//cbCto.setTextInputAllowed(false);
				cbCto.setImmediate(true);
				cbCto.setWidth("220px");		
				cbCto.setConverter(new SingleSelectConverter(cbCto));
				
				fieldGroup.bind(cbCto, "cto");
				cbCto.setReadOnly(true);
				
				addComponent(cbCto);		
								
				
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
				
				txtCodCliente = new TextField("Cliente");				
				txtCodCliente.setWidth("60px");				
				txtCodCliente.setNullRepresentation("");
				txtCodCliente.setStyleName("caption-align");
				txtCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				txtCodCliente.setImmediate(true);
					
				txtCodCliente.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						ClienteSelecionado = new Cliente();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							if(ClienteSelecionado != null){
								txtDescricaoCliente.setReadOnly(false);
								txtDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
								txtDescricaoCliente.setReadOnly(true);
								
								txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
								txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
								txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
								txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
								
								txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
								txtTelefone3.setValue(ClienteSelecionado.getCelular1());
								txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
								txtTelefone4.setValue(ClienteSelecionado.getCelular2());
								
								txtContato.setValue(ClienteSelecionado.getContato());
								txtEmail.setValue(ClienteSelecionado.getEmail());
								
								cbEndereco.setContainerDataSource(getEnderecos());
								fieldGroup.bind(cbEndereco, "end"); 
								
								
							}else {
								txtDescricaoCliente.setReadOnly(false);
								txtDescricaoCliente.setValue("");
								txtDescricaoCliente.setReadOnly(true);
								
								
								txtDDD1.setValue("");
								txtTelefone1.setValue("");
								txtDDD2.setValue("");
								txtTelefone2.setValue("");
								
								txtDDD3.setValue("");
								txtTelefone3.setValue("");
								txtDDD4.setValue("");
								txtTelefone4.setValue("");
								
								txtContato.setValue("");
								txtEmail.setValue("");
								
								cbEndereco.setContainerDataSource(null);
								
								if(fieldGroup.getField("end") != null){
									fieldGroup.unbind(cbEndereco);
								}
								
							}
						}else{
							txtDescricaoCliente.setReadOnly(false);
							txtDescricaoCliente.setValue("");
							txtDescricaoCliente.setReadOnly(true);	
							
							txtDDD1.setValue("");
							txtTelefone1.setValue("");
							txtDDD2.setValue("");
							txtTelefone2.setValue("");
							
							txtDDD3.setValue("");
							txtTelefone3.setValue("");
							txtDDD4.setValue("");
							txtTelefone4.setValue("");
							
							txtContato.setValue("");
							txtEmail.setValue("");
							
							cbEndereco.setContainerDataSource(null);
							if(fieldGroup.getField("end") != null){
								fieldGroup.unbind(cbEndereco);
							}
						}
					}
				});

				txtCodCliente.setRequired(true);		
				txtDescricaoCliente = new TextField();
				txtDescricaoCliente.setTabIndex(2000);
				txtDescricaoCliente.setReadOnly(true);
				txtDescricaoCliente.setWidth("592px");
				
				
									
				if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
					ClienteSelecionado = ClienteDAO.find(((Cliente)item.getItemProperty("cliente").getValue()).getId());				
				}
				
				
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setTabIndex(300000);
				btSearchCliente.setEnabled(false);
				btSearchCliente.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						ClienteUtil cUtil = new ClienteUtil(true, true,"INATIVO");
						cUtil.addListerner(new ClienteUtil.ClienteListerner() {
							
							@Override
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
									if(event.getCliente() != null){
										txtCodCliente.setValue(event.getCliente().getId().toString());
										txtDescricaoCliente.setReadOnly(false);
										txtDescricaoCliente.setValue(event.getCliente().getNome_razao());
										txtDescricaoCliente.setReadOnly(true);
										ClienteSelecionado = event.getCliente();
										
										txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
										txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
										txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
										txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
										
										txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
										txtTelefone3.setValue(ClienteSelecionado.getCelular1());
										txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
										txtTelefone4.setValue(ClienteSelecionado.getCelular2());
										
										
										txtContato.setValue(ClienteSelecionado.getContato());
										txtEmail.setValue(ClienteSelecionado.getEmail());
										
										cbEndereco.setContainerDataSource(getEnderecos());
										fieldGroup.bind(cbEndereco, "end"); 
										
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
												
						addComponent(txtCodCliente);							
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
						
						addComponent(txtDescricaoCliente);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);		
				
				
				if(txtCodCliente != null && ClienteSelecionado != null){
					txtCodCliente.setValue(ClienteSelecionado.getId().toString());
					txtCodCliente.setReadOnly(true);
				}
				
				if(txtDescricaoCliente != null && ClienteSelecionado != null){
					txtDescricaoCliente.setReadOnly(false);
					txtDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
					txtDescricaoCliente.setReadOnly(true);
				}

			}	
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtContato = new TextField ("Contato");								
					txtContato.setStyleName("caption-align");
					txtContato.setId("txtBoleto");
					txtContato.setNullRepresentation("");					
					txtContato.setWidth("570px");
									
					addComponent(txtContato);
					fieldGroup.bind(txtContato, "contato");
					txtContato.setReadOnly(true);
								
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
					cbEndereco.setStyleName("caption-align");	
					cbEndereco.setImmediate(true);
					cbEndereco.setTextInputAllowed(false);
					cbEndereco.setConverter(new SingleSelectConverter(cbEndereco));
					
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
									txtContrato.setValue(contratoSelecionado.getId().toString()+" - "+contratoSelecionado.getContrato().getNome()+" | "+contratoSelecionado.getRegime());
									txtContrato.setReadOnly(true);
//									String carencia;
//									
//									if(!carencia.equals("SEM CARENCIA")){
//										bc = ac.getContrato().getValor_equipamento();												
//									}
//									
//									if(ac.getRegime().equals("PROPRIO")){
//										bc = "0,00";
//									}
//									
//									int qtd_boletos_abertos = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(contratoSelecionado.getId()) != null ? ContasReceberDAO.procurarBoletosDoAcessoPorContrato(contratoSelecionado.getId()).size() : 0;
//									double valor_multa = 0;
//									double valor_adesao = !contratoSelecionado.getCarencia().equals("SEM CARENCIA") ? Real.formatStringToDBDouble(contratoSelecionado.getCarencia().getContrato().getValor_adesao()) : 0;
//									double valor_equipamento = Real.formatStringToDBDouble(bc);
//									double valor_total = valor_adesao+valor_equipamento;
									
									
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
					
					if(ClienteSelecionado != null && item.getItemProperty("end").getValue() != null){
						cbEndereco.setContainerDataSource(getEnderecos());
						fieldGroup.bind(cbEndereco, "end"); 
					}
					
					cbEndereco.setReadOnly(true);
									
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
						txtComplemento.setStyleName("caption-align");
						txtComplemento.setWidth("300px");
						txtComplemento.setImmediate(true);
						txtComplemento.setTextChangeEventMode(TextChangeEventMode.LAZY);
						txtComplemento.setRequired(true);
						txtComplemento.setDescription("Casa, Apto, Galpão, Anexo, etc");						
						
						addComponent(txtComplemento);	
						
						if(cbEndereco.getValue() != null){
							txtComplemento.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
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
						
						if(cbEndereco.getValue() != null && cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue() != null){
							txtReferencia.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue().toString());
						}
						
						txtReferencia.setReadOnly(true);
					}
				});
			}
		});
		
		vlRoot.addComponent(new VerticalLayout() {
			{										
					addComponent(new FormLayout() {
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");
							
							txtLocalizacao = new TextField("Url de Localização");
							txtLocalizacao.setNullRepresentation("");
							txtLocalizacao.setWidth("521px");
							txtLocalizacao.setStyleName("caption-align-url-localizacao");							
							txtLocalizacao.setReadOnly(true);
												
							addComponent(txtLocalizacao);		
														
							if(cbEndereco.getValue() != null && cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("localizacao").getValue() != null){
								txtLocalizacao.setReadOnly(false);
								txtLocalizacao.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("localizacao").getValue().toString());
								txtLocalizacao.setReadOnly(true);
								
								BrowserWindowOpener openIpPool = new BrowserWindowOpener(txtLocalizacao.getValue());
								openIpPool.setFeatures("height=600,width=800");
								openIpPool.extend(txtLocalizacao);
							}
							
							
							
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
								txtContrato.setStyleName("caption-align");
								txtContrato.setReadOnly(true);
													
								addComponent(txtContrato);
								
								if(item.getItemProperty("contrato").getValue() != null){
									AcessoCliente contrato = (AcessoCliente) item.getItemProperty("contrato").getValue();
									
									txtContrato.setReadOnly(false);
									txtContrato.setValue(contrato.getId()+" - "+contrato.getContrato().getNome()+" | "+contrato.getRegime());
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
				
		
		

		// Telefone 1,2
		vlRoot.addComponent(new HorizontalLayout() {
				{
					addComponent(new FormLayout() {
						{
							setStyleName("form-cutom-new");
							txtDDD1 = new TextField("Telefone Principal");
							txtDDD1.setNullRepresentation("");
							txtDDD1.setStyleName("telefone");
							txtDDD1.addStyleName("caption-align");
							txtDDD1.setRequired(true);
							txtDDD1.setMaxLength(2);
							txtDDD1.setWidth("40px");

							addComponent(txtDDD1);
							
							if(ClienteSelecionado != null){
								txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
							}
							
							txtDDD1.setReadOnly(true);
						}
					});

					addComponent(new FormLayout() {
						{
							// setMargin(true);
							// setSpacing(true);
							setStyleName("form-cutom-new_hide_require");
							
							txtTelefone1 = new TextField();
							addComponent(txtTelefone1);
							txtTelefone1.setNullRepresentation("");
							txtTelefone1.setWidth("105px");
							txtTelefone1.setMaxLength(11);
							txtTelefone1.setRequired(true);
							
							if(ClienteSelecionado != null){
								txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
							}
							
							txtTelefone1.setReadOnly(true);
						}
					});

					addComponent(new FormLayout() {
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");

							txtDDD2 = new TextField();
							txtDDD2.setNullRepresentation("");
							txtDDD2.setWidth("40px");
							txtDDD2.setMaxLength(2);

							addComponent(txtDDD2);
							
							if(ClienteSelecionado != null){
								txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
							}
							
							txtDDD2.setReadOnly(true);
						}
					});

					addComponent(new FormLayout() {
						{
							// setMargin(true);
							// setSpacing(true);
							setStyleName("form-cutom-new");
							
							txtTelefone2 = new TextField();
							addComponent(txtTelefone2);
							txtTelefone2.setNullRepresentation("");
							txtTelefone2.setWidth("105px");
							txtTelefone2.setMaxLength(11);

							if(ClienteSelecionado != null){
								txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
							}
							
							txtTelefone2.setReadOnly(true);
						}
					});

					addComponent(new FormLayout() {
						{
							setStyleName("form-cutom-new");
							txtDDD3 = new TextField();
							txtDDD3.setNullRepresentation("");
							txtDDD3.setStyleName("telefone");							
							txtDDD3.setRequired(false);
							txtDDD3.setMaxLength(2);
							txtDDD3.setWidth("40px");

							addComponent(txtDDD3);
							
							if(ClienteSelecionado != null){
								txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
							}
							
							txtDDD3.setReadOnly(true);
						}
					});

					addComponent(new FormLayout() {
						{
							// setMargin(true);
							// setSpacing(true);
							setStyleName("form-cutom-new_hide_require");
							
							txtTelefone3 = new TextField();
							addComponent(txtTelefone3);
							txtTelefone3.setNullRepresentation("");
							txtTelefone3.setWidth("105px");
							txtTelefone3.setMaxLength(11);
							txtTelefone3.setRequired(true);
							
							if(ClienteSelecionado != null){
								txtTelefone3.setValue(ClienteSelecionado.getCelular1());
							}
							
							txtTelefone3.setReadOnly(true);
						}
					});

					addComponent(new FormLayout() {
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");

							txtDDD4 = new TextField();
							txtDDD4.setNullRepresentation("");
							txtDDD4.setWidth("40px");
							txtDDD4.setMaxLength(2);

							addComponent(txtDDD4);
							
							if(ClienteSelecionado != null){
								txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
							}
							
							txtDDD4.setReadOnly(true);
						}
					});

					addComponent(new FormLayout() {
						{
							// setMargin(true);
							// setSpacing(true);
							setStyleName("form-cutom-new");
							
							txtTelefone4 = new TextField();
							addComponent(txtTelefone4);
							txtTelefone4.setNullRepresentation("");
							txtTelefone4.setWidth("105px");
							txtTelefone4.setMaxLength(11);
							
							if(ClienteSelecionado != null){
								txtTelefone4.setValue(ClienteSelecionado.getCelular2());
							}
							
							txtTelefone4.setReadOnly(true);

						}
					});

				}
			});
		vlRoot.addComponent(new HorizontalLayout(){
						{
							setWidth("100%");
							
							addComponent(new FormLayout(){					
								{
									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									txtEmail = new TextField ("E-mail");								
									txtEmail.setStyleName("caption-align");						
									txtEmail.setNullRepresentation("");					
									txtEmail.setWidth("315px");
									txtEmail.setRequired(true);
													
									addComponent(txtEmail);		
									
									if(ClienteSelecionado != null){
										txtEmail.setValue(ClienteSelecionado.getEmail());
									}
									
									//txtEmail.setReadOnly(true);
									
									
								}
							});
							
							addComponent(new FormLayout(){					
								{
									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									CheckBox chk = new CheckBox("Não possui e-mail");
									
									chk.addValueChangeListener(new Property.ValueChangeListener() {
										
										@Override
										public void valueChange(ValueChangeEvent event) {
											txtEmail.setRequired(!txtEmail.isRequired());
											if(!txtEmail.isRequired()){
												txtEmail.setValue("");
											}
										}
									});
									
									addComponent(chk); 
								}
							});
						}
					});
			
		vlRoot.addComponent(new HorizontalLayout(){
				{
							
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
																	
								TextArea taOBservacao = new TextArea ("Observação");
								taOBservacao.setWidth("186px");
								taOBservacao.setHeight("80px");
								taOBservacao.setStyleName("caption-align");						
								taOBservacao.setNullRepresentation("");		
								taOBservacao.addStyleName("uppercase");
								
												
								addComponent(taOBservacao);			
								fieldGroup.bind(taOBservacao, "obs");
								
								taOBservacao.setReadOnly(true);
							}
						});
						
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
																	
								TextArea taUsoInterno = new TextArea ("Uso Interno");
								taUsoInterno.setWidth("186px");
								taUsoInterno.setHeight("50px");
								taUsoInterno.setStyleName("caption-align-h60");						
								taUsoInterno.setNullRepresentation("");		
								taUsoInterno.addStyleName("uppercase");
								
												
								addComponent(taUsoInterno);			
								fieldGroup.bind(taUsoInterno, "uso_interno");
								
								taUsoInterno.setReadOnly(true);
							}
						});
						
						addComponent(new FormLayout(){
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
									
								txtConclusao = new TextArea("Conclusão");				
								txtConclusao.setWidth("186px");				
								txtConclusao.setHeight("80px");
								txtConclusao.addStyleName("caption-align-h60");
								txtConclusao.setNullRepresentation("");		
								
								addComponent(txtConclusao);		
								fieldGroup.bind(txtConclusao, "conclusao");
								
								txtConclusao.setReadOnly(true);
									
							}
						});
				}
		
		});
			
		vlRoot.addComponent(new FormLayout(){
				
				{
					setStyleName("form-custom-table");
					setMargin(true);
					setSpacing(true);
					
					JPAContainer<OseProduto> container = JPAContainerFactory.make(OseProduto.class, ConnUtil.getEntity());
					EntityItem<Ose> eItem = (EntityItem<Ose>)item;
					container.addContainerFilter(Filters.eq("ose", eItem.getEntity()));
					container.addNestedContainerProperty("produto.produto.nome");
					
					Table tb = new Table(null, container);		
					tb.addStyleName("tb-itens");
					tb.setVisibleColumns(new Object[] {"produto.produto.nome","qtd"});
					tb.setColumnHeader("produto.produto.nome", "Produto(s)");
					tb.setColumnHeader("qtd", "Qtd");
					
					tb.setWidth("740px");
					tb.setHeight("95px");
					
					addComponent(tb); 
					
				}
		});
			
		vlRoot.addComponent(new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
															
						TextField txtUltimaOcorrencia = new TextField ("Ultima Ocorrência");
						txtUltimaOcorrencia.setWidth("687px");						
						txtUltimaOcorrencia.setStyleName("caption-align");						
						txtUltimaOcorrencia.setNullRepresentation("");					
										
						addComponent(txtUltimaOcorrencia);			
						
						
						List<AlteracoesOse> alteracoes = AlteracoesOseDAO.getAltercoesAssistencia((Integer)item.getItemProperty("id").getValue());
						if(alteracoes.size() > 0){
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							String data = sdf.format(((AlteracoesOse)alteracoes.get(alteracoes.size()-1)).getData_alteracao());
							txtUltimaOcorrencia.setValue(data+" - "+
									((AlteracoesOse)alteracoes.get(alteracoes.size()-1)).getOperador().getUsername()+" - "+((AlteracoesOse)alteracoes.get(alteracoes.size()-1)).getTipo());
						}						
						txtUltimaOcorrencia.setReadOnly(true);
					}
			});
			
		vlRoot.addComponent(new HorizontalLayout(){
						{
							
							addComponent(new FormLayout(){
								{
									
									setStyleName("form-cutom-new");
									setMargin(true);
									setSpacing(true);
									
									final DateField dfPrevisao = new DateField ("Data");
									dfPrevisao.setDateFormat("dd/MM/yyyy HH:mm:ss");
									dfPrevisao.setResolution(DateField.RESOLUTION_HOUR);
									dfPrevisao.setResolution(DateField.RESOLUTION_MIN);
									dfPrevisao.setStyleName("caption-align");														
									dfPrevisao.setRequired(true);
									
									addComponent(dfPrevisao);
									fieldGroup.bind(dfPrevisao,"data_ex");
									dfPrevisao.setReadOnly(true);
									
									dfPrevisao.addValueChangeListener(new Property.ValueChangeListener() {
										
										@Override
										public void valueChange(ValueChangeEvent event) {
											EntityItem<SubGrupoOse> subgrupo = (EntityItem<SubGrupoOse>)((ComboBox) fieldGroup.getField("subgrupo")).getItem(((ComboBox) fieldGroup.getField("subgrupo")).getValue());
											if(subgrupo != null && subgrupo.getEntity().isLimite()){
												
												ContratoManutencao contrato = ContratoManutencaoDAO.getContratoPorCliente(ClienteSelecionado);
												if(contrato != null){
													PlanosManutencao planoManutencao = contrato.getPlano_manutencao();							
													Integer qtd_ose_mensal = OseDAO.getOsePorClienteSubGrupo(ClienteSelecionado, subgrupo.getEntity(), dfPrevisao.getValue());
													
													Integer qtd_restante = qtd_ose_mensal > planoManutencao.getLimite_mensal() ? qtd_ose_mensal - planoManutencao.getLimite_mensal() : planoManutencao.getLimite_mensal() - qtd_ose_mensal;
													
													txtLimite.setReadOnly(false);
													txtLimite.setValue(qtd_restante.toString());
													txtLimite.setReadOnly(true);																					
												}
											}
											
										}
									});
									
									

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
												Notification.show("Data Inválida!");												
											}
											
											
											//txtHora.focus();																
										}
									});		
								}
							});
							
							addComponent(new FormLayout(){					
								{
									setStyleName("form-cutom-new");
									setMargin(true);
									setSpacing(true);
										
									ComboBox cbTurno = new ComboBox("Turno");					
									cbTurno.setStyleName("caption-align");
									cbTurno.setNullSelectionAllowed(false);
									cbTurno.setRequired(true);	
									cbTurno.setTextInputAllowed(false);
									cbTurno.addItem("MANHA");
									cbTurno.addItem("TARDE");
									cbTurno.addItem("INTEGRAL");
									cbTurno.select("INTEGRAL");
									
									addComponent(cbTurno);					
									fieldGroup.bind(cbTurno,"turno");
									
									cbTurno.setReadOnly(true);
								}
							
							});
							
							addComponent(
									new FormLayout(){					
									{
										setStyleName("form-cutom-new");
										setMargin(true);
										setSpacing(true);
											
										TextField txtTempoAtendimento = new TextField("Tempo Atendimento");
										txtTempoAtendimento.setStyleName("caption-align");
										txtTempoAtendimento.setWidth("150px");
										
										if(item.getItemProperty("data_abertura").getValue() != null && item.getItemProperty("data_conclusao").getValue() != null){
										
												Date data1 = (Date)item.getItemProperty("data_abertura").getValue();						
												Date data2 = (Date)item.getItemProperty("data_conclusao").getValue();

				     						    long segundos = (data2.getTime() - data1.getTime()) / 1000;
												int semanas = (int)Math.floor(segundos / 604800);
												segundos -= semanas * 604800;
												int dias = (int)Math.floor(segundos / 86400);
												segundos -= dias * 86400;
												int horas = (int)Math.floor(segundos / 3600);
												segundos -= horas * 3600;
												int minutos = (int)Math.floor(segundos / 60);
												segundos -= minutos * 60;

												 // exibe o resultado
												
												//System.out.println("As duas datas tem " +  semanas + " semanas, " + dias + " dias, " +horas + " horas, " + minutos + " minutos e " + segundos + " segundos de diferença");
												txtTempoAtendimento.setValue(dias + "d, " +horas + "h, " + minutos+"min");
										}
										
										txtTempoAtendimento.setReadOnly(true);
									
										addComponent(txtTempoAtendimento);					
									}
							});
						}
					});	
		
			
			
			
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					final ComboBox cbGrupo = new ComboBox("Grupo", getGrupos());					
					cbGrupo.setStyleName("caption-align");
					cbGrupo.setNullSelectionAllowed(false);
					cbGrupo.setItemCaptionPropertyId("nome");					
					cbGrupo.setRequired(true);	
					cbGrupo.setTextInputAllowed(false);
					cbGrupo.setConverter(new SingleSelectConverter(cbGrupo));
					cbGrupo.setWidth("220px");
					cbGrupo.focus();
					cbGrupo.setImmediate(true); 
					
					cbGrupo.setImmediate(true);
					cbGrupo.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(fieldGroup.getField("subgrupo") != null){
								((ComboBox)fieldGroup.getField("subgrupo")).setReadOnly(false);							
								((ComboBox)fieldGroup.getField("subgrupo")).setContainerDataSource(getSubGrupos());
								((ComboBox)fieldGroup.getField("subgrupo")).setItemCaptionPropertyId("nome");
								((ComboBox)fieldGroup.getField("subgrupo")).setConverter(new SingleSelectConverter(((ComboBox)fieldGroup.getField("subgrupo"))));
							}
							
							if(fieldGroup.getField("grupo") != null){
								String grupo = cbGrupo.getItem(cbGrupo.getValue()).getItemProperty("nome").getValue().toString();
								
								System.out.println(grupo);
								
								if(grupo.equals("INSTALACAO") || grupo.equals("MUDANCA ENDERECO")){
									
									try{
										cbCto.setRequired(true);
										cbCto.setEnabled(true);
										fieldGroup.bind(cbCto,"cto");
									}catch(Exception e){
										
									}
								}else{
									try{
										cbCto.setRequired(false);
										cbCto.setEnabled(false);									
										fieldGroup.unbind(cbCto);
									}catch(Exception e){
										
									}
								}
								
							}
							
							
						}
					});
					
					addComponent(cbGrupo);				
					fieldGroup.bind(cbGrupo,"grupo");
				}
		});
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){					
				{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						final ComboBox cbSubGrupo = new ComboBox("SubGrupo");					
						cbSubGrupo.setStyleName("caption-align");
						cbSubGrupo.setNullSelectionAllowed(false);									
						cbSubGrupo.setRequired(true);	
						cbSubGrupo.setReadOnly(true);
						cbSubGrupo.setTextInputAllowed(false);								
						cbSubGrupo.setImmediate(true);
						cbSubGrupo.setWidth("220px");
						
						cbSubGrupo.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(fieldGroup.getField("tipo_subgrupo") != null){
									((ComboBox)fieldGroup.getField("tipo_subgrupo")).setReadOnly(false);							
									((ComboBox)fieldGroup.getField("tipo_subgrupo")).setContainerDataSource(getTipoSubGrupos());
									((ComboBox)fieldGroup.getField("tipo_subgrupo")).setItemCaptionPropertyId("nome");
									((ComboBox)fieldGroup.getField("tipo_subgrupo")).setConverter(new SingleSelectConverter(((ComboBox)fieldGroup.getField("tipo_subgrupo"))));
									
									if(cbSubGrupo.getValue() != null){
										txtPrioridade.setReadOnly(false);
										txtPrioridade.setValue(cbSubGrupo.getItem(cbSubGrupo.getValue()).getItemProperty("prioridade").getValue().toString());
										txtPrioridade.setReadOnly(true);
										
										//txtLimite.setReadOnly(false);
										//txtLimite.setValue(cbSubGrupo.getItem(cbSubGrupo.getValue()).getItemProperty("limite").getValue().toString());
										//txtLimite.setReadOnly(true);
									}
								}
							}
						});
						
						addComponent(cbSubGrupo);					
						
						if(((ComboBox)fieldGroup.getField("grupo")).getValue() != null){
							cbSubGrupo.setReadOnly(false);							
							cbSubGrupo.setContainerDataSource(getSubGrupos());
							cbSubGrupo.setItemCaptionPropertyId("nome");
							cbSubGrupo.setConverter(new SingleSelectConverter(cbSubGrupo));						
						}
	
						fieldGroup.bind(cbSubGrupo,"subgrupo");
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						txtPrioridade = new TextField("Prioridade");
						txtPrioridade.setReadOnly(true);
						txtPrioridade.setWidth("42px");
						if(((ComboBox)fieldGroup.getField("subgrupo")).getValue() != null){
							txtPrioridade.setValue(((ComboBox)fieldGroup.getField("subgrupo")).getItem(((ComboBox)fieldGroup.getField("subgrupo")).getValue()).getItemProperty("prioridade").getValue().toString());
							txtPrioridade.setReadOnly(true);
						}
						addComponent(txtPrioridade);				
						
					}
				});
				
				addComponent(new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						txtLimite = new TextField("Limite");
						txtLimite.setReadOnly(true);
						txtLimite.setWidth("42px");
						//if(((ComboBox)fieldGroup.getField("subgrupo")).getValue() != null){
						//	txtLimite.setValue(((ComboBox)fieldGroup.getField("subgrupo")).getItem(((ComboBox)fieldGroup.getField("subgrupo")).getValue()).getItemProperty("limite").getValue().toString());
						//	txtLimite.setReadOnly(true);
						//}
						addComponent(txtLimite);				
						
					}
				});
			}			
		});
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					final ComboBox cbTipoSubGrupo = new ComboBox("Tipo");					
					cbTipoSubGrupo.setStyleName("caption-align");
					cbTipoSubGrupo.setNullSelectionAllowed(false);
					cbTipoSubGrupo.setRequired(true);	
					cbTipoSubGrupo.setReadOnly(true);
					cbTipoSubGrupo.setTextInputAllowed(false);
					cbTipoSubGrupo.setImmediate(true);
					cbTipoSubGrupo.setWidth("220px");
										
					cbTipoSubGrupo.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							
							if(cbTipoSubGrupo.getValue() != null && cbTipoSubGrupo.getItem(cbTipoSubGrupo.getValue()).getItemProperty("descricao") != null && cbTipoSubGrupo.getItem(cbTipoSubGrupo.getValue()).getItemProperty("descricao").getValue() != null){
								
								taUsoInterno.setInputPrompt(cbTipoSubGrupo.getItem(cbTipoSubGrupo.getValue()).getItemProperty("descricao").getValue().toString());
							}else{
								taUsoInterno.setInputPrompt("");
							}
							
							
							
							if(cbTipoSubGrupo.getValue() != null && txtValor != null && txtQtd != null){
								//txtValor.setReadOnly(false);
								
								
								
								if(cbTipoSubGrupo.getItem(cbTipoSubGrupo.getValue()).getItemProperty("valor").getValue() != null && (Double)cbTipoSubGrupo.getItem(cbTipoSubGrupo.getValue()).getItemProperty("valor").getValue() > 0){
									txtValor.setReadOnly(false);
									txtValor.setValue(Real.formatDbToString(cbTipoSubGrupo.getItem(cbTipoSubGrupo.getValue()).getItemProperty("valor").getValue().toString()));
									txtValor.setReadOnly(true);
									dfVencimento.setReadOnly(false); 
									dfVencimento.setValue(new Date());
									dfVencimento.setReadOnly(true); 
								}
								
								//txtQtd.setReadOnly(false);
								
								
			 					
							}else{
								if(txtValor != null){
									txtValor.setReadOnly(false);
									txtValor.setValue("");
									txtValor.setReadOnly(true);
								}
								
								if(txtQtd != null){
									txtQtd.setReadOnly(false);
									txtQtd.setValue("");
									txtQtd.setReadOnly(true);
									
								}
								
								dfVencimento.setReadOnly(false);
								dfVencimento.setValue(null);
								dfVencimento.setReadOnly(true);
							}
						}
					});
					
					addComponent(cbTipoSubGrupo);
					
					if(((ComboBox)fieldGroup.getField("subgrupo")).getValue() != null){
						cbTipoSubGrupo.setReadOnly(false);							
						cbTipoSubGrupo.setContainerDataSource(getTipoSubGrupos());
						cbTipoSubGrupo.setItemCaptionPropertyId("nome");
						cbTipoSubGrupo.setConverter(new SingleSelectConverter(cbTipoSubGrupo));						
					}
					
					fieldGroup.bind(cbTipoSubGrupo,"tipo_subgrupo");
				}
		});
		
		
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				JPAContainer<Swith> containerCto = JPAContainerFactory.makeReadOnly(Swith.class, ConnUtil.getEntity());
				containerCto.addContainerFilter(new Like("identificacao", "%CTO%"));
				
				cbCto = new ComboBox("CTO", containerCto);
				cbCto.setItemCaptionPropertyId("identificacao");						 
				cbCto.setStyleName("caption-align");
				cbCto.setNullSelectionAllowed(false);
				cbCto.setRequired(false);				
				//cbCto.setTextInputAllowed(false);
				cbCto.setImmediate(true);
				cbCto.setWidth("220px");		
				cbCto.setConverter(new SingleSelectConverter(cbCto));
				cbCto.setEnabled(false);
				
				addComponent(cbCto);		
								
				
			}
		});
		
		
		//AQUI
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					taUsoInterno = new TextArea ("Uso Interno");
					taUsoInterno.setWidth("687px");
					taUsoInterno.setHeight("50px");
					taUsoInterno.setStyleName("caption-align");		
					taUsoInterno.addStyleName("uppercase");
					
					taUsoInterno.setNullRepresentation("");					
									
					addComponent(taUsoInterno);			
					fieldGroup.bind(taUsoInterno, "uso_interno");
					//if(conteudo != null && !conteudo.equals("")){
					//	taUsoInterno.setValue(conteudo); 
					//}
				}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
					addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
									
								txtValor = new TextField("Valor (R$)");
								txtValor.setStyleName("caption-align");
								txtValor.addStyleName("align-currency");
								txtValor.setReadOnly(true);
								
								addComponent(txtValor);		
								
								if(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getValue() != null && txtValor != null){
									txtValor.setReadOnly(false);
									
									if(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getItem(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getValue()).getItemProperty("valor").getValue() != null){
										txtValor.setValue(Real.formatDbToString(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getItem(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getValue()).getItemProperty("valor").getValue().toString()));
									}
									
									txtValor.setReadOnly(true);
								}else{
									if(txtValor != null){
										txtValor.setReadOnly(false);
										txtValor.setValue("");
										txtValor.setReadOnly(true);
									}
								}
								
							}
					});
					
					addComponent(new FormLayout(){					
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);							
							
							dfVencimento = new  DateField("Primeiro Vencimento");							
							dfVencimento.setStyleName("caption-align");				
							dfVencimento.setDateFormat("dd/MM/yyyy");
							
							fieldGroup.bind(dfVencimento, "vencimento");
							dfVencimento.setReadOnly(true);
							
							dfVencimento.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									Date dt1 = new Date();
									Date dt2 = dfVencimento.getValue();
									
									Integer dias = Days.daysBetween(new DateTime(dt1), new DateTime(dt2)).getDays();
									
									if(dias > 29){
										Notify.Show("A data escolhida excede o limite permitido!", Notify.TYPE_ERROR);
										dfVencimento.setValue(new Date());
									}
								}
							});
							
							
							addComponent(dfVencimento);			
						}
					});
					
					addComponent(new FormLayout(){
						{
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
							
							txtQtd = new TextField("Qtd.:");
							txtQtd.setWidth("50px");
							txtQtd.setStyleName("caption-align-rote-qtd");
							txtQtd.setValue("1");
							txtQtd.setId("txtQtd");
							txtQtd.setReadOnly(true);

							JavaScript.getCurrent().execute("$('#txtQtd').mask('0')");
							
							txtQtd.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									if(txtQtd != null && txtQtd.getValue() != null && !txtQtd.getValue().equals("") ){
										
										
											Integer v = Integer.parseInt(txtQtd.getValue());
											
											if(v > 6){
												txtQtd.setValue("6");
											}
										
									}else{
										txtQtd.setValue("1");
									}
								}
							});
														
							addComponent(txtQtd); 
						}
					});
			
//-- Suellen pediu para retirar
//-------------------
//					addComponent(new FormLayout(){
//						{
//							//ProcessBuilder
//							
//							setStyleName("form-cutom");
//							setMargin(true);
//							setSpacing(true);
//							
//							ckbGratis = new CheckBox("Grátis");
//							ckbGratis.setStyleName("caption-align-rote-qtd");
//							
//							ckbGratis.addValueChangeListener(new Property.ValueChangeListener() {
//								
//								@Override
//								public void valueChange(ValueChangeEvent event) {
//									txtValor.setEnabled(!txtValor.isEnabled());
//									dfVencimento.setEnabled(!dfVencimento.isEnabled());
//									txtQtd.setEnabled(!txtQtd.isEnabled());
//									
//								}
//							});
//							
//							addComponent(ckbGratis);
//						}
//					});
			}
		});
		
		
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				//setWidth("100%");
				
				JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
				containerClientes.addContainerFilter(Filters.eq("status", "INATIVO"));
				
				
				boolean preencher = false;
				if(item != null){
					if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
						preencher = true;
					}					
				}
				
				txtCodCliente = new TextField("Cliente");				
				txtCodCliente.setWidth("60px");				
				txtCodCliente.setNullRepresentation("");
				txtCodCliente.setStyleName("caption-align");
				txtCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				txtCodCliente.setImmediate(true);
						
				
				
				txtCodCliente.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						ClienteSelecionado = new Cliente();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
														
							ClienteSelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));
							
							EntityItem<SubGrupoOse> subgrupo = (EntityItem<SubGrupoOse>)((ComboBox) fieldGroup.getField("subgrupo")).getItem(((ComboBox) fieldGroup.getField("subgrupo")).getValue());
							if(subgrupo != null && subgrupo.getEntity().isLimite()){
								
								ContratoManutencao contrato = ContratoManutencaoDAO.getContratoPorCliente(ClienteSelecionado);
								if(contrato != null){
									PlanosManutencao planoManutencao = contrato.getPlano_manutencao();							
									Integer qtd_ose_mensal = OseDAO.getOsePorClienteSubGrupo(ClienteSelecionado, subgrupo.getEntity(), new Date());
									
									Integer qtd_restante = qtd_ose_mensal > planoManutencao.getLimite_mensal() ? qtd_ose_mensal - planoManutencao.getLimite_mensal() : planoManutencao.getLimite_mensal() - qtd_ose_mensal;
									
									txtLimite.setReadOnly(false);
									txtLimite.setValue(qtd_restante.toString());
									txtLimite.setReadOnly(true);																					
								}
							}

							
							if(ClienteSelecionado != null){
								txtDescricaoCliente.setReadOnly(false);
								txtDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
								txtDescricaoCliente.setReadOnly(true);
								
								txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
								txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
								txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
								txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
								
								txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
								txtTelefone3.setValue(ClienteSelecionado.getCelular1());
								txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
								txtTelefone4.setValue(ClienteSelecionado.getCelular2());
								
								txtContato.setValue(ClienteSelecionado.getContato());
								txtEmail.setValue(ClienteSelecionado.getEmail());
								
								cbEndereco.setContainerDataSource(getEnderecos());
								fieldGroup.bind(cbEndereco, "end"); 
								
								
							}else {
								txtDescricaoCliente.setReadOnly(false);
								txtDescricaoCliente.setValue("");
								txtDescricaoCliente.setReadOnly(true);
								
								
								txtDDD1.setValue("");
								txtTelefone1.setValue("");
								txtDDD2.setValue("");
								txtTelefone2.setValue("");
								
								txtDDD3.setValue("");
								txtTelefone3.setValue("");
								txtDDD4.setValue("");
								txtTelefone4.setValue("");
								
								txtContato.setValue("");
								txtEmail.setValue("");
								
								cbEndereco.setContainerDataSource(null);
								
								if(fieldGroup.getField("end") != null){
									fieldGroup.unbind(cbEndereco);
								}
								
							}
						}else{
							txtDescricaoCliente.setReadOnly(false);
							txtDescricaoCliente.setValue("");
							txtDescricaoCliente.setReadOnly(true);	
							
							txtDDD1.setValue("");
							txtTelefone1.setValue("");
							txtDDD2.setValue("");
							txtTelefone2.setValue("");
							
							txtDDD3.setValue("");
							txtTelefone3.setValue("");
							txtDDD4.setValue("");
							txtTelefone4.setValue("");
							
							txtContato.setValue("");
							txtEmail.setValue("");
							
							cbEndereco.setContainerDataSource(null);
							if(fieldGroup.getField("end") != null){
								fieldGroup.unbind(cbEndereco);
							}
						}
					}
				});

				txtCodCliente.setRequired(true);		
				txtDescricaoCliente = new TextField();
				txtDescricaoCliente.setTabIndex(2000);
				txtDescricaoCliente.setReadOnly(true);
				txtDescricaoCliente.setWidth("592px");
				
				
				if(item!=null){					
					if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
						ClienteSelecionado = ClienteDAO.find(((Cliente)item.getItemProperty("cliente").getValue()).getId());				
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
										txtCodCliente.setValue(event.getCliente().getId().toString());
										txtDescricaoCliente.setReadOnly(false);
										txtDescricaoCliente.setValue(event.getCliente().getNome_razao());
										txtDescricaoCliente.setReadOnly(true);
										ClienteSelecionado = event.getCliente();
										
										txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
										txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
										txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
										txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
										
										txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
										txtTelefone3.setValue(ClienteSelecionado.getCelular1());
										txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
										txtTelefone4.setValue(ClienteSelecionado.getCelular2());
										
										
										txtContato.setValue(ClienteSelecionado.getContato());
										txtEmail.setValue(ClienteSelecionado.getEmail());
										
										cbEndereco.setContainerDataSource(getEnderecos());
										fieldGroup.bind(cbEndereco, "end"); 
										
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
												
						addComponent(txtCodCliente);	
						if(ClienteSelecionado != null){
							txtCodCliente.setValue(ClienteSelecionado.getId().toString());
						}
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
						
						addComponent(txtDescricaoCliente);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);		
				
				
				if(txtCodCliente != null && ClienteSelecionado != null){
					txtCodCliente.setValue(ClienteSelecionado.getId().toString());
				}
				
				if(txtDescricaoCliente != null && ClienteSelecionado != null){
					txtDescricaoCliente.setReadOnly(false);
					txtDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
					txtDescricaoCliente.setReadOnly(true);
				}

			}	
		});
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtContato = new TextField ("Contato");								
					txtContato.setStyleName("caption-align");
					txtContato.setId("txtBoleto");
					txtContato.setNullRepresentation("");					
					txtContato.setWidth("570px");
									
					addComponent(txtContato);
					fieldGroup.bind(txtContato, "contato");
					
					if(ClienteSelecionado != null){
						txtContato.setValue(ClienteSelecionado.getContato());
					}
					
				
				}
		});
		vlRoot.addComponent(
				new FormLayout(){					
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
					cbEndereco.setStyleName("caption-align");	
					cbEndereco.setImmediate(true);
					cbEndereco.setTextInputAllowed(false);
					//cbEndereco.setItemCaptionPropertyId("endereco");
					
							   
					
					cbEndereco.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							
							if(cbEndereco.getValue() != null && 
									cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("localizacao").getValue() != null && txtLocalizacao != null){
								String localizacao = cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("localizacao").getValue().toString();
								txtLocalizacao.setValue(localizacao);
							}else{
								txtLocalizacao.setValue("");
							}
							
							
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
									txtContrato.setValue(contratoSelecionado.getId().toString()+" - "+contratoSelecionado.getContrato().getNome()+" | "+contratoSelecionado.getRegime());
									txtContrato.setReadOnly(true);
									
									txtStatusContrato.setReadOnly(false);
									txtStatusContrato.setValue(contratoSelecionado.getStatus_2());
									txtStatusContrato.setReadOnly(true);
								}else{
									txtContrato.addStyleName("red-contrato");
									txtContrato.setReadOnly(false);
									txtContrato.setValue("SEM CONTRATO VINCULADO A ESTE ENDEREÇO");
									txtContrato.setReadOnly(true);
									
									txtStatusContrato.setReadOnly(false);
									txtStatusContrato.setValue("");
									txtStatusContrato.setReadOnly(true);
								}
								
							}else{
								if(txtComplemento != null && txtReferencia != null){
									txtReferencia.setValue("");
									txtComplemento.setValue("");
								}
							}
							
							//Atualizar Valor se for cancelamento com Fidelidade
							EntityItem<GrupoOse> entityItemGrupo = (EntityItem<GrupoOse>)((ComboBox)fieldGroup.getField("grupo")).getItem(((ComboBox)fieldGroup.getField("grupo")).getValue());
							EntityItem<TipoSubGrupoOse> entityItemTipoSubGrupo = (EntityItem<TipoSubGrupoOse>)((ComboBox)fieldGroup.getField("tipo_subgrupo")).getItem(((ComboBox)fieldGroup.getField("tipo_subgrupo")).getValue());
							
							if(entityItemGrupo != null && entityItemGrupo.getEntity().getNome().equals("CANCELAMENTO") && entityItemTipoSubGrupo != null && entityItemTipoSubGrupo.getEntity().getValor() > 0){
							
								String bc = "0,00";
								String bi = "0,00";
								String carencia = "";
								Integer months = 0;
								if(contratoSelecionado != null && contratoSelecionado.getCarencia() != null && contratoSelecionado.getCarencia().equals("SIM") && contratoSelecionado.getContrato().getCarencia().equals("SIM")){			
									
									try{
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										
										DateTime dt1 = new DateTime(sdf.parse(sdf.format(new Date())));
										DateTime dt2 = new DateTime(sdf.parse(sdf.format(contratoSelecionado.getData_venc_contrato()))).plusMonths(1);			 
										months = Months.monthsBetween(dt1, dt2).getMonths();
										
										carencia = months.toString()+" MESES";
									}catch(Exception e){
										e.printStackTrace();
									}
									
									if(months > 0){
										carencia = months.toString()+" MESES";
									}else{
										carencia = "SEM CARENCIA";
									}
								}else{
									carencia = "SEM CARENCIA";
								}
								
								if(!carencia.equals("SEM CARENCIA")){
									bc = Real.formatDbToString(String.valueOf(contratoSelecionado.getValor_beneficio_comodato()));												
								}
								
								if(contratoSelecionado.getRegime().equals("PROPRIO")){
									bc = "0,00";
								}
								if(contratoSelecionado.getInstalacao_gratis() != null && contratoSelecionado.getInstalacao_gratis().equals("SIM")){

									EntityManager em = ConnUtil.getEntity();
									Query qValorBenfInst = em.createNativeQuery("SELECT ts.VALOR FROM ose o, tipos_ose g, tipos_subgrupo ts WHERE o.GRUPO_ID = g.ID AND ts.ID = "
											+ "o.TIPO_SUBGRUPO_ID AND g.NOME LIKE '%INSTALACAO%' AND o.ACESSO_CLIENTE_ID = :contrato ");
									qValorBenfInst.setParameter("contrato", contratoSelecionado.getId());
									
									if(qValorBenfInst.getResultList().size() > 0){
										bi = Real.formatDbToString(qValorBenfInst.getResultList().toArray()[0].toString());
									}
									//bi = "120,00";												
								}else{
									bi = "0,00";
								}
							
								int qtd_boletos_abertos = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(contratoSelecionado.getId()) != null ? ContasReceberDAO.procurarBoletosDoAcessoPorContrato(contratoSelecionado.getId()).size() : 0;
								double valor_multa = 0;
								double valor_adesao = !carencia.equals("SEM CARENCIA") ? contratoSelecionado.getValor_beneficio_adesao() : 0;
								double valor_equipamento = Real.formatStringToDBDouble(bc);
								double valor_instalacao =  Real.formatStringToDBDouble(bi);
								double valor_total = valor_adesao+valor_equipamento+valor_instalacao;
								
								if(qtd_boletos_abertos == 12){
									valor_multa = valor_total;
								}else if(qtd_boletos_abertos == 11){
									valor_multa = (valor_total*91.67)/100;
								}else if(qtd_boletos_abertos == 10){
									valor_multa = (valor_total*83.34)/100;
								}else if(qtd_boletos_abertos == 9){
									valor_multa = (valor_total*75.01)/100;
								}else if(qtd_boletos_abertos == 8){
									valor_multa = (valor_total*66.68)/100;
								}else if(qtd_boletos_abertos == 7){
									valor_multa = (valor_total*58.35)/100;
								}else if(qtd_boletos_abertos == 6){
									valor_multa = (valor_total*50.02)/100;
								}else if(qtd_boletos_abertos == 5){
									valor_multa = (valor_total*41.69)/100;
								}else if(qtd_boletos_abertos == 4){
									valor_multa = (valor_total*33.36)/100;
								}else if(qtd_boletos_abertos == 3){
									valor_multa = (valor_total*25.03)/100;
								}else if(qtd_boletos_abertos == 2){
									valor_multa = (valor_total*16.70)/100;
								}else if(qtd_boletos_abertos == 1){
									valor_multa = (valor_total*8.37)/100;
								}
								
								txtValor.setReadOnly(false);
								txtValor.setValue(Real.formatDbToString(String.valueOf(valor_multa)));
								txtValor.setReadOnly(true);
								
							}
						}
					});
									
					addComponent(cbEndereco);
					
					if(ClienteSelecionado != null && item.getItemProperty("end").getValue() != null){
						cbEndereco.setContainerDataSource(getEnderecos());
						cbEndereco.setConverter(new SingleSelectConverter(cbEndereco));
						fieldGroup.bind(cbEndereco, "end"); 
					}
					
					if(ClienteSelecionado != null && item.getItemProperty("end").getValue() == null){
						cbEndereco.setContainerDataSource(getEnderecos());
						cbEndereco.setConverter(new SingleSelectConverter(cbEndereco));
						fieldGroup.bind(cbEndereco, "end"); 
					}
									
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
						txtComplemento.setStyleName("caption-align");
						txtComplemento.setWidth("300px");
						txtComplemento.setImmediate(true);
						txtComplemento.setTextChangeEventMode(TextChangeEventMode.LAZY);
						txtComplemento.setRequired(true);
						txtComplemento.setDescription("Casa, Apto, Galpão, Anexo, etc");						
						
						addComponent(txtComplemento);	
						
						if(cbEndereco.getValue() != null){
							txtComplemento.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
						}
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
						
						if(cbEndereco.getValue() != null){
							txtReferencia.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
						}
					}
				});
			}
		});
		
		vlRoot.addComponent(new VerticalLayout() {
			{										
					addComponent(new FormLayout() {
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");
							
							txtLocalizacao = new TextField("Url de Localização");
							txtLocalizacao.setNullRepresentation("");
							txtLocalizacao.setWidth("521px");
							txtLocalizacao.setStyleName("caption-align-url-localizacao");
							
												
							addComponent(txtLocalizacao);								
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
								txtContrato.setStyleName("caption-align");
								txtContrato.setReadOnly(true);
													
								addComponent(txtContrato);
								
								if(item!=null){
									if(item.getItemProperty("contrato") != null && item.getItemProperty("contrato").getValue() != null){
										AcessoCliente contrato = (AcessoCliente) item.getItemProperty("contrato").getValue();
										
										txtContrato.setReadOnly(false);
										txtContrato.setValue(contrato.getId()+" - "+contrato.getContrato().getNome()+" | "+contrato.getRegime());
										txtContrato.setReadOnly(true);
									}
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
								
								if(item != null && item.getItemProperty("contrato") != null && item.getItemProperty("contrato").getValue() != null){
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
		

		// Telefone 1,2
		vlRoot.addComponent(new HorizontalLayout() {
				{
					addComponent(new FormLayout() {
						{
							setStyleName("form-cutom-new");
							txtDDD1 = new TextField("Telefone Principal");
							txtDDD1.setNullRepresentation("");
							txtDDD1.setStyleName("telefone");
							txtDDD1.addStyleName("caption-align");
							txtDDD1.setRequired(true);
							txtDDD1.setMaxLength(2);
							txtDDD1.setWidth("40px");

							addComponent(txtDDD1);
							
							if(ClienteSelecionado != null){
								txtDDD1.setValue(ClienteSelecionado.getDdd_fone1());
							}
						}
					});

					addComponent(new FormLayout() {
						{
							// setMargin(true);
							// setSpacing(true);
							setStyleName("form-cutom-new_hide_require");
							
							txtTelefone1 = new TextField();
							addComponent(txtTelefone1);
							txtTelefone1.setNullRepresentation("");
							txtTelefone1.setWidth("105px");
							txtTelefone1.setMaxLength(11);
							txtTelefone1.setRequired(true);
							
							if(ClienteSelecionado != null){
								txtTelefone1.setValue(ClienteSelecionado.getTelefone1());
							}
						}
					});

					addComponent(new FormLayout() {
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom-new");

							txtDDD2 = new TextField("Telefone Alternativo");
							txtDDD2.setNullRepresentation("");
							txtDDD2.setWidth("40px");
							txtDDD2.setMaxLength(2);

							addComponent(txtDDD2);
							
							if(ClienteSelecionado != null){
								txtDDD2.setValue(ClienteSelecionado.getDdd_fone2());
							}
						}
					});

					addComponent(new FormLayout() {
						{
							// setMargin(true);
							// setSpacing(true);
							setStyleName("form-cutom-new");
							
							txtTelefone2 = new TextField();
							addComponent(txtTelefone2);
							txtTelefone2.setNullRepresentation("");
							txtTelefone2.setWidth("105px");
							txtTelefone2.setMaxLength(11);

							if(ClienteSelecionado != null){
								txtTelefone2.setValue(ClienteSelecionado.getTelefone2());
							}
						}
					});

				}
			});
			vlRoot.addComponent(new HorizontalLayout() {
				{
					addComponent(new FormLayout() {
						{
							setStyleName("form-cutom");
							txtDDD3 = new TextField("Telefone Alternativo");
							txtDDD3.setNullRepresentation("");
							txtDDD3.setStyleName("telefone");
							txtDDD3.addStyleName("caption-align");
							txtDDD3.setRequired(false);
							txtDDD3.setMaxLength(2);
							txtDDD3.setWidth("40px");

							addComponent(txtDDD3);
							
							if(ClienteSelecionado != null){
								txtDDD3.setValue(ClienteSelecionado.getDdd_cel1());
							}
						}
					});

					addComponent(new FormLayout() {
						{
							// setMargin(true);
							// setSpacing(true);
							setStyleName("form-cutom_hide_require");
							
							txtTelefone3 = new TextField();
							addComponent(txtTelefone3);
							txtTelefone3.setNullRepresentation("");
							txtTelefone3.setWidth("105px");
							txtTelefone3.setMaxLength(11);
							txtTelefone3.setRequired(true);
							
							if(ClienteSelecionado != null){
								txtTelefone3.setValue(ClienteSelecionado.getCelular1());
							}
						}
					});

					addComponent(new FormLayout() {
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");

							txtDDD4 = new TextField("Telefone Alternativo");
							txtDDD4.setNullRepresentation("");
							txtDDD4.setWidth("40px");
							txtDDD4.setMaxLength(2);

							addComponent(txtDDD4);
							
							if(ClienteSelecionado != null){
								txtDDD4.setValue(ClienteSelecionado.getDdd_cel2());
							}
						}
					});

					addComponent(new FormLayout() {
						{
							// setMargin(true);
							// setSpacing(true);
							setStyleName("form-cutom");
							
							txtTelefone4 = new TextField();
							addComponent(txtTelefone4);
							txtTelefone4.setNullRepresentation("");
							txtTelefone4.setWidth("105px");
							txtTelefone4.setMaxLength(11);
							
							if(ClienteSelecionado != null){
								txtTelefone4.setValue(ClienteSelecionado.getCelular2());
							}

						}
					});

				}
			});
			
//			vlRoot.addComponent(
//					new FormLayout(){					
//					{
//						setStyleName("form-cutom");
//						setMargin(true);
//						setSpacing(true);
//															
//						txtEmail = new TextField ("E-mail");								
//						txtEmail.setStyleName("caption-align");						
//						txtEmail.setNullRepresentation("");					
//						txtEmail.setWidth("420px");
//										
//						addComponent(txtEmail);		
//						
//						if(ClienteSelecionado != null){
//							txtEmail.setValue(ClienteSelecionado.getEmail());
//						}
//					}
//			});
			
			
			vlRoot.addComponent(
					
					new HorizontalLayout(){
						{
							setWidth("100%");
							
							addComponent(new FormLayout(){					
								{
									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									txtEmail = new TextField ("E-mail");								
									txtEmail.setStyleName("caption-align");						
									txtEmail.setNullRepresentation("");					
									txtEmail.setWidth("220px");
									txtEmail.setRequired(true); 
													
									addComponent(txtEmail);		
									
									if(ClienteSelecionado != null){
										txtEmail.setValue(ClienteSelecionado.getEmail());
									}
									
									//txtEmail.setReadOnly(true);
								}
							});
							
							addComponent(new FormLayout(){					
								{
									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									CheckBox chk = new CheckBox("Não possui e-mail");
									
									chk.addValueChangeListener(new Property.ValueChangeListener() {
										
										@Override
										public void valueChange(ValueChangeEvent event) {
											txtEmail.setRequired(!txtEmail.isRequired());
											if(!txtEmail.isRequired()){
												txtEmail.setValue("");
											}
										}
									});
									
									addComponent(chk); 
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
															
						TextArea taOBservacao = new TextArea ("Observação");
						taOBservacao.setWidth("687px");
						taOBservacao.setHeight("50px");
						taOBservacao.setStyleName("caption-align");		
						taOBservacao.addStyleName("uppercase");
						
						taOBservacao.setNullRepresentation("");					
										
						addComponent(taOBservacao);			
						fieldGroup.bind(taOBservacao, "obs");
						if(conteudo != null && !conteudo.equals("")){
							taOBservacao.setValue(conteudo); 
						}
					}
			});
			
			vlRoot.addComponent(
					new HorizontalLayout(){
						{
							
							addComponent(new FormLayout(){
								{
									
									setStyleName("form-cutom-new");
									setMargin(true);
									setSpacing(true);
									
									dfPrevisao = new DateField ("Previsão");
									dfPrevisao.setDateFormat("dd/MM/yyyy");									
									dfPrevisao.setStyleName("caption-align");														
									dfPrevisao.setRequired(true);
									dfPrevisao.setImmediate(true);
									
									dfPrevisao.addValueChangeListener(new Property.ValueChangeListener() {
										
										@Override
										public void valueChange(ValueChangeEvent event) {
											if(dfPrevisao.getValue() != null){
												try{
													SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
													if(dfPrevisao.getValue().after(new Date()) || dfPrevisao.getValue().equals(sdf.parse(sdf.format(new Date())))){
														valid_data = true;
													}else{
														valid_data = false;
														Notify.Show("Data Inválida, Selecione uma Data Maior ou Igual a Hoje!", Notify.TYPE_ERROR);
													}
												}catch(Exception e){
													e.printStackTrace();
													valid_data = false;
												}
											}
											
										}
									});
									
									addComponent(dfPrevisao);
									
									
								}
							});
							
							addComponent(new FormLayout(){
								{
									
									setStyleName("form-cutom-new");
									setMargin(true);
									setSpacing(true);
									
									cbHora = new ComboBox ();																							
									cbHora.setRequired(false);
									cbHora.setImmediate(true);
									cbHora.setTextInputAllowed(false);
									cbHora.setWidth("70px");
									cbHora.setNullSelectionAllowed(true);
									
									cbHora.addItem("08:30");
									cbHora.addItem("09:00");
									cbHora.addItem("09:30");
									cbHora.addItem("10:00");
									cbHora.addItem("10:30");
									cbHora.addItem("11:00");
									cbHora.addItem("11:30");									
									cbHora.addItem("14:00");
									cbHora.addItem("14:30");
									cbHora.addItem("15:00");
									cbHora.addItem("15:30");
									cbHora.addItem("16:00");
									cbHora.addItem("16:30");
									cbHora.addItem("17:00");
									cbHora.addItem("17:30");
									cbHora.addItem("18:00");
									cbHora.addItem("18:30");
									cbHora.addItem("19:00");
									cbHora.addItem("19:30");
									cbHora.addItem("20:00");
									
									cbHora.addValueChangeListener(new Property.ValueChangeListener() {
										
										@Override
										public void valueChange(ValueChangeEvent event) {
											if(cbHora.getValue() != null){
												if(cbHora.getValue().equals("08:00") || cbHora.getValue().equals("08:30") || cbHora.getValue().equals("09:00") ||
														cbHora.getValue().equals("09:30") || cbHora.getValue().equals("10:00") || cbHora.getValue().equals("10:30") ||
														cbHora.getValue().equals("11:00") || cbHora.getValue().equals("11:30")){
													cbTurno.select("MANHA");
												}else{
													cbTurno.select("TARDE");
												}	
											}
										}
									});
																				
									addComponent(cbHora);
									
									
								}
							});
							
							addComponent(new FormLayout(){					
											{
												setStyleName("form-cutom-new");
												setMargin(true);
												setSpacing(true);
													
												cbTurno = new ComboBox("Turno");					
												cbTurno.setNullSelectionAllowed(false);
												cbTurno.setRequired(true);	
												cbTurno.setTextInputAllowed(false);
												cbTurno.addItem("MANHA");
												cbTurno.addItem("TARDE");
												cbTurno.addItem("INTEGRAL");
												cbTurno.select("INTEGRAL");
												
												addComponent(cbTurno);					
												fieldGroup.bind(cbTurno,"turno");
											}
									});
						}
					});	
		
						
			
	}
	
	private JPAContainer<Endereco> getEnderecos(){
		JPAContainer<Endereco> container = JPAContainerFactory.make(Endereco.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("clientes", ClienteSelecionado));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		return container;
	}
	
	
	private JPAContainer<TipoSubGrupoOse> getTipoSubGrupos(){
		JPAContainer<TipoSubGrupoOse> container = JPAContainerFactory.make(TipoSubGrupoOse.class, ConnUtil.getEntity());
		
		if(fieldGroup != null && fieldGroup.getField("subgrupo") != null && fieldGroup.getField("subgrupo").getValue() != null){
			SubGrupoOse subGrupo = 
					SubGrupoDAO.find((Integer)((ComboBox)fieldGroup.getField("subgrupo")).getItem(((ComboBox)fieldGroup.getField("subgrupo")).getValue()).getItemProperty("id").getValue());
			
			if(subGrupo != null){
				container.addContainerFilter(Filters.eq("subgrupo_id", subGrupo));
				container.addContainerFilter(Filters.eq("status", "ATIVO"));
			}
		}
		
		container.sort(new Object[]{"nome"}, new boolean[]{true});
		
		return container;
	}
	
	private JPAContainer<SubGrupoOse> getSubGrupos(){
		JPAContainer<SubGrupoOse> container = JPAContainerFactory.make(SubGrupoOse.class, ConnUtil.getEntity());
		
		if(fieldGroup != null && fieldGroup.getField("grupo") != null && fieldGroup.getField("grupo").getValue() != null){
			GrupoOse grupo = GrupoOseDAO.find((Integer)((ComboBox)fieldGroup.getField("grupo")).getItem(((ComboBox)fieldGroup.getField("grupo")).getValue()).getItemProperty("id").getValue());
			
			if(grupo != null){
				container.addContainerFilter(Filters.eq("grupo", grupo));
				container.addContainerFilter(Filters.eq("status", "ATIVO"));
			}
		}
		
		container.sort(new Object[]{"nome"}, new boolean[]{true});
		
		return container;
	}
	
	private JPAContainer<GrupoOse> getGrupos(){
		JPAContainer<GrupoOse> container = JPAContainerFactory.make(GrupoOse.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		container.sort(new Object[]{"nome"}, new boolean[]{true});
		return container;
	}
	
	private Button btOcorrencias;
	private Button btFechar;
	
	public Button buildBtOcorrencia(){
		btOcorrencias = new Button("Ocorrências", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				HistoricoAlteracoes historicoAlteracoes = new HistoricoAlteracoes(true, true, (Integer)item.getItemProperty("id").getValue());
				getUI().addWindow(historicoAlteracoes);				
			}
		});
		
		return btOcorrencias;
	}

	public Button buildBtFechar(){
		btFechar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		return btFechar;
	}

	
	
	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
								
				EntityItem<GrupoOse> entityItemGrupo = (EntityItem<GrupoOse>)((ComboBox)fieldGroup.getField("grupo")).getItem(((ComboBox)fieldGroup.getField("grupo")).getValue());
				boolean allow_grupo_qtd = true;
				if(entityItemGrupo != null){
					if(!OseDAO.allowQtdToday(entityItemGrupo.getEntity(), dfPrevisao.getValue())){
						allow_grupo_qtd = false;					
					}					
				}
							
				if(fieldGroup.isValid() && cbEndereco.isValid() && ClienteSelecionado != null && dfPrevisao.isValid() && valid_data && allow_grupo_qtd && txtEmail.isValid()){
					try {				
						
						EntityItem<SubGrupoOse> subgrupo = (EntityItem<SubGrupoOse>)((ComboBox) fieldGroup.getField("subgrupo")).getItem(((ComboBox) fieldGroup.getField("subgrupo")).getValue());
						
						boolean check = true;
						if(subgrupo != null && subgrupo.getEntity().isLimite()){
							
							ContratoManutencao contrato = ContratoManutencaoDAO.getContratoPorCliente(ClienteSelecionado);
							if(contrato != null){
								PlanosManutencao planoManutencao = contrato.getPlano_manutencao();							
								Integer qtd_ose_mensal = OseDAO.getOsePorClienteSubGrupo(ClienteSelecionado, subgrupo.getEntity(), dfPrevisao.getValue());
								
								if(qtd_ose_mensal != null && qtd_ose_mensal >= planoManutencao.getLimite_mensal()){								
									check = false;
								}
							}
						}
						
						
						if(check){
						
							ClienteSelecionado.setDdd_fone1(txtDDD1.getValue());
							ClienteSelecionado.setDdd_fone2(txtDDD2.getValue());
							ClienteSelecionado.setTelefone1(txtTelefone1.getValue());
							ClienteSelecionado.setTelefone2(txtTelefone2.getValue());
							ClienteSelecionado.setDdd_cel1(txtDDD3.getValue());
							ClienteSelecionado.setDdd_cel2(txtDDD4.getValue());
							ClienteSelecionado.setCelular1(txtTelefone3.getValue());
							ClienteSelecionado.setCelular2(txtTelefone4.getValue());						
							ClienteSelecionado.setEmail(txtEmail.getValue());
							ClienteSelecionado.setContato(txtContato.getValue());
							
							
							
							if(cbHora.getValue() != null){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");							
								item.getItemProperty("data_ex").setValue(sdf.parse(sdf.format(dfPrevisao.getValue()).replaceAll("00:00:00", "")+""+cbHora.getValue()+":00"));
							}else{
								item.getItemProperty("data_ex").setValue(dfPrevisao.getValue());
							}
							
							item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
							item.getItemProperty("cliente").setValue(ClienteSelecionado);					
							item.getItemProperty("bairro").setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("bairro").getValue());
							item.getItemProperty("endereco").setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("endereco").getValue());
							item.getItemProperty("cidade").setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("cidade").getValue());
							item.getItemProperty("referencia").setValue(txtReferencia.getValue());
							
							
	
							if(item.getItemProperty("id").getValue() == null){
								item.getItemProperty("status").setValue("ABERTO");
								item.getItemProperty("data_abertura").setValue(new Date());
								item.getItemProperty("operador_abertura").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
							}
							
							
							if(contratoSelecionado != null){
								item.getItemProperty("contrato").setValue(contratoSelecionado);
							}												
							if(contratoSelecionado != null &&  contratoSelecionado.getPlano() != null){							
								item.getItemProperty("plano").setValue(contratoSelecionado.getPlano().getNome());
							}
							if(contratoSelecionado != null && contratoSelecionado.getBase() != null ){
								item.getItemProperty("concentrador").setValue(contratoSelecionado.getBase().getIdentificacao());
							}
							if(contratoSelecionado != null && contratoSelecionado.getBase() != null && contratoSelecionado.getBase().getBase() != null){
								item.getItemProperty("base").setValue(contratoSelecionado.getBase().getBase().getNome());
							}
							if(contratoSelecionado != null && contratoSelecionado.getMaterial() != null){
								item.getItemProperty("material").setValue(contratoSelecionado.getMaterial().getNome());
							}
							
							EntityItem<Endereco> eiEnd = (EntityItem<Endereco>)((ComboBox)fieldGroup.getField("end")).getItem(((ComboBox)fieldGroup.getField("end")).getValue());
							fieldGroup.unbind(((ComboBox)fieldGroup.getField("end")));
							fieldGroup.commit();	
							
							item.getItemProperty("end").setValue(eiEnd.getEntity());
							
							Endereco end = (Endereco)item.getItemProperty("end").getValue();
							end.setComplemento(txtComplemento.getValue());
							end.setReferencia(txtReferencia.getValue());
							end.setLocalizacao(txtLocalizacao.getValue());
														
							fireEvent(new RoteirizacaoEvent(getUI(), item, true, txtQtd.getValue(), false));		
						
						}else{
							Notify.Show("Limite de Ordens de Serviços para este cliente foi excedida!", Notify.TYPE_ERROR);
						}
							
					} catch (Exception e) {		
						
						fieldGroup.bind(cbEndereco, "end");
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
					}
				}else{
					
					for (Field<?> field: fieldGroup.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
						
						if(!txtEmail.isValid()){
							txtEmail.addStyleName("invalid-txt");
						}else{
							txtEmail.removeStyleName("invalid-txt");
						}
					
						
					}
					
					if(!fieldGroup.isValid() && !cbEndereco.isValid() && ClienteSelecionado == null && !dfPrevisao.isValid() && !valid_data && !txtEmail.isValid()){
						Notify.Show_Invalid_Submit_Form();	
					}
					if(!allow_grupo_qtd){
						Notify.Show("O Limite diário de OS com o Grupo: "+entityItemGrupo.getEntity().getNome()+" já foi atingido!", Notify.TYPE_ERROR);
					}
					
					
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
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new RoteirizacaoEvent(getUI(), item, false, null, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								
								EntityItem<GrupoOse> entityItemGrupo = (EntityItem<GrupoOse>)((ComboBox)fieldGroup.getField("grupo")).getItem(((ComboBox)fieldGroup.getField("grupo")).getValue());
								boolean allow_grupo_qtd = true;
								if(entityItemGrupo != null){
									if(!OseDAO.allowQtdToday(entityItemGrupo.getEntity(), dfPrevisao.getValue())){
										allow_grupo_qtd = false;
									}					
								}
								
								
								if(fieldGroup.isValid() && cbEndereco.isValid() && ClienteSelecionado != null && dfPrevisao.isValid() && valid_data && allow_grupo_qtd){						
								
									try {				
										
										ClienteSelecionado.setDdd_fone1(txtDDD1.getValue());
										ClienteSelecionado.setDdd_fone2(txtDDD2.getValue());
										ClienteSelecionado.setTelefone1(txtTelefone1.getValue());
										ClienteSelecionado.setTelefone2(txtTelefone2.getValue());
										ClienteSelecionado.setDdd_cel1(txtDDD3.getValue());
										ClienteSelecionado.setDdd_cel2(txtDDD4.getValue());
										ClienteSelecionado.setCelular1(txtTelefone3.getValue());
										ClienteSelecionado.setCelular2(txtTelefone4.getValue());
										ClienteSelecionado.setCelular2(txtContato.getValue());
										ClienteSelecionado.setEmail(txtEmail.getValue());
										ClienteSelecionado.setContato(txtContato.getValue());
										
										item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
										item.getItemProperty("cliente").setValue(ClienteSelecionado);
										item.getItemProperty("status").setValue("ABERTO");
										item.getItemProperty("bairro").setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("bairro").getValue());
										item.getItemProperty("cidade").setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("cidade").getValue());
										item.getItemProperty("endereco").setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("endereco").getValue());
										item.getItemProperty("referencia").setValue(txtReferencia.getValue());
										item.getItemProperty("localizacao").setValue(txtLocalizacao.getValue());
										
//										Endereco end = EnderecoDAO.find((Integer)cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("id").getValue());
//										end.setReferencia(txtReferencia.getValue());
//										end.setComplemento(txtComplemento.getValue());
//										
//										item.getItemProperty("end").setValue(end);
										
										if(contratoSelecionado != null){
											item.getItemProperty("contrato").setValue(contratoSelecionado);
										}
										
										
										fieldGroup.commit();				
										fireEvent(new RoteirizacaoEvent(getUI(), item, true, txtQtd.getValue(), false));						
											
									} catch (Exception e) {					
										e.printStackTrace();					
										Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
									}
								}else{
									
									for (Field<?> field: fieldGroup.getFields()) {
										
										if(!field.isValid()){
											field.addStyleName("invalid-txt");
										}else{
											field.removeStyleName("invalid-txt");
										}
									}
									
									if(!fieldGroup.isValid() && !cbEndereco.isValid() && ClienteSelecionado == null && !dfPrevisao.isValid() && !valid_data){
										Notify.Show_Invalid_Submit_Form();	
									}
									if(!allow_grupo_qtd){
										Notify.Show("O Limite diário de OS com o Grupo: "+entityItemGrupo.getEntity().getNome()+" já foi atingido!", Notify.TYPE_ERROR);
									}
									
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new TransportadoraEvent(getUI(), item, false));
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
		
		
		return btCancelar;
	}
	
	
	public void addListerner(RoteirizacaoListerner target){
		try {
			Method method = RoteirizacaoListerner.class.getDeclaredMethod("onClose", RoteirizacaoEvent.class);
			addListener(RoteirizacaoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(RoteirizacaoListerner target){
		removeListener(RoteirizacaoEvent.class, target);
	}
	public static class RoteirizacaoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private  String qtd;
		
		
		public RoteirizacaoEvent(Component source, Item item, boolean confirm, String qtd, boolean gratis) {
			super(source);
			this.item = item;
			this.confirm = confirm;			
			this.qtd = qtd;
			//this.gratis = gratis;
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
		
		public String getQtd(){
			return qtd;
		}
		
	
	}
	public interface RoteirizacaoListerner extends Serializable{
		public void onClose(RoteirizacaoEvent event);
	}

	
}
