package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
import com.digital.opuserp.domain.PreContrato;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
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
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class PreContratoEditor  extends Window implements GenericEditor {

		
	private static final long serialVersionUID = 1L;
	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	
	VerticalLayout vlRoot;
	
	JPAContainer<PlanoAcesso> containerPlanos;
	ComboBox cbPlanos;
	ComboBox cbContrato;
	
	boolean validarFiador = true;
	boolean validarCliente = true;
	boolean validarEndereco = true;
	
	ContasReceberDAO crDAO = new ContasReceberDAO();
	
	String valorPrimeiroBoleto;
	String contrato;
	
	Date dataPrimeiroBoleto;
	Date dataInstalacao;
	Cliente ClienteSelecionado;
	Cliente Fiadorlecionado;
		
	Integer codCliente;
	Integer codFiador;
	
	private Label lbRegistros;
	
	TextField tfDescricaoCliente;
	TextField tfDescricaoFiador;
	
	TextField txtCodCliente;
	
	HorizontalLayout hlFloat;
	
	TextField tfUploadDownload;
	TextField txtInstGratis;
	ComboBox cbInstalaçãoGratis;
	
	public PreContratoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("888px");
		setHeight("325px");
		
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
	
	
	
	public String getNextId() {
		CredenciaisAcessoDAO caDao = new CredenciaisAcessoDAO();
		return caDao.getNextID();
	}
	
//	public Label buildLbRegistros(){
//		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
//		return lbRegistros;
//	}
	
	
	
	
	public void buildLayout(){
		
	
		vlRoot.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");

				TextField txtCod = new TextField("Número de Contrato");

				if (item.getItemProperty("id").getValue() != null) {					
					txtCod.setValue(item.getItemProperty("id").getValue().toString());
				}
				
				txtCod.setWidth("60px");
				txtCod.setReadOnly(true);
				txtCod.setStyleName("caption-align-acesso");
				txtCod.setNullRepresentation("");
				addComponent(txtCod);	
			}
		});
		vlRoot.addComponent(new HorizontalLayout(){
			{					
				txtCodCliente = new TextField("Cliente");				
				txtCodCliente.setWidth("60px");				
				txtCodCliente.setNullRepresentation("");
				txtCodCliente.setStyleName("caption-align-acesso");
				txtCodCliente.focus();
				txtCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				txtCodCliente.setImmediate(true);
			
				txtCodCliente.setRequired(true);		
				tfDescricaoCliente = new TextField();
				tfDescricaoCliente.setTabIndex(2000);
				tfDescricaoCliente.setReadOnly(true);
				tfDescricaoCliente.setWidth("450px");
								
				if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
					
					EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>) item;					
					Cliente c = entityItem.getEntity().getCliente();
					ClienteSelecionado = c;
					
					if(c != null){
						txtCodCliente.setReadOnly(false);
						txtCodCliente.setValue(c.getId().toString());
						txtCodCliente.setReadOnly(true);
						
						tfDescricaoCliente.setReadOnly(false);
						tfDescricaoCliente.setValue(c.getNome_razao());
						tfDescricaoCliente.setReadOnly(true);
					}
				}
								
				FormLayout frmCodigoCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(txtCodCliente);							
					}
				};
				addComponent(frmCodigoCliente);
					
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoCliente);							
					}
				}; 
			
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);	
			
			}	
		});
		
								
		vlRoot.addComponent(new FormLayout(){					
				{
					EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>) item;
					
					JPAContainer<PlanoAcesso> container = JPAContainerFactory.makeReadOnly(PlanoAcesso.class, ConnUtil.getEntity());
					container.addContainerFilter(Filters.eq("contrato_acesso", entityItem.getEntity().getPlano().getContrato_acesso()));
					container.addContainerFilter(Filters.eq("status", "ATIVO"));
					
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
															
					cbPlanos = new ComboBox("Plano de Acesso", container);
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
						
									
						}
					});

					txtInstGratis = new TextField("Instalação Grátis");
					txtInstGratis.setStyleName("caption-align-acesso");				
					txtInstGratis.setReadOnly(true);
					
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
					
					
	
				}
			});
		vlRoot.addComponent(	new FormLayout(){					
				{
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
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);										
									
					tfUploadDownload = new TextField("Upload / Download");
					tfUploadDownload.setStyleName("caption-align-acesso");				
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
									
					dfDataInstalacao = new DateField("Data de Instação");
					dfDataInstalacao.setStyleName("caption-align-acesso");				
					
					addComponent(dfDataInstalacao);					
				}
		});
			
	}
	
	DateField dfDataInstalacao;

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
		
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
										
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
							
		if(cbPlanos.isValid() && cbInstalaçãoGratis.isValid()){
			try {		
				
				EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item;
				AcessoCliente contrato = entityItem.getEntity();
				
				PreContrato preContrato = new PreContrato();
				preContrato.setId(null);
				preContrato.setContrato(contrato);
				preContrato.setData(new Date());
				//preContrato.setDia_vencimento(dia_vencimento);
				if(cbInstalaçãoGratis != null && cbInstalaçãoGratis.getValue() != null){
				preContrato.setIsencao_instalacao(cbInstalaçãoGratis.getValue().equals("SIM") ? true : false);
				}
				
				EntityItem<PlanoAcesso> entityItemPlano = (EntityItem<PlanoAcesso>)cbPlanos.getItem(cbPlanos.getValue()); 
				preContrato.setPlano(entityItemPlano.getEntity());
				preContrato.setTipo_plano(entityItemPlano.getEntity().getContrato_acesso().getNome());
				preContrato.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
				preContrato.setValor(Real.formatStringToDBFloat(entityItemPlano.getEntity().getValor()));
								
				fireEvent(new PreContratoEvent(getUI(), preContrato, true));

			} catch (Exception e) {											
				Notify.Show("ERRO: Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);		
				e.printStackTrace();
			}
		}else{			
			    Notify.Show_Invalid_Submit_Form();
		}		
	}
		
	public void addListerner(PreContratoListerner target){
		try {
			Method method = PreContratoListerner.class.getDeclaredMethod("onClose", PreContratoEvent.class);
			addListener(PreContratoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(PreContratoListerner target){
		removeListener(PreContratoEvent.class, target);
	}
	
	public static class PreContratoEvent extends Event{
		
		private PreContrato preContrato;
		private boolean confirm;
		
		public PreContratoEvent(Component source, PreContrato preContrato, boolean confirm) {
			super(source);
			this.preContrato = preContrato;
			this.confirm = confirm;
		}
		
		public PreContrato getPreContrato(){
			return this.preContrato;
		}
		
		public boolean isConfirm(){
			return this.confirm;
		}

	}
		
	public interface PreContratoListerner extends Serializable{
		public void onClose(PreContratoEvent event);
	}
	
	
	
	
	
	


}
