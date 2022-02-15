package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Calendar;
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
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.NovoCadastroAcessoEditor.NovoCadastroEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.NovoCadastroAcessoEditor.NovoCadastroListerner;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.Notify;
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
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.themes.BaseTheme;

public class NovoAgendamentoEditor extends Window implements GenericEditor {

		
	private static final long serialVersionUID = 1L;
	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	String valorBoleto;
	DateField dfDataInstalacao;
	
	
	JPAContainer<PlanoAcesso> containerPlanos;
	ComboBox cbPlanos;
	ComboBox cbContrato;
	
	TextField tfInfoValorPrimeiroBoleto;
	TextField tfInfoValorAdesao;
	TextField tfInfoValorPlano;
	
	boolean validarInstalacao = true;
	boolean validarBoleto = true;
	boolean validarFiador = true;
	boolean validarCliente = true;
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
	ComboBox cbInstalaçãoGratis;
	
	private ComboBox cbEnderecos;
	
	public NovoAgendamentoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("508px");
		setHeight("189px");
		
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
		
		vlRoot.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");

     			ComboBox cbTipo = new ComboBox("Tipo");
     			cbTipo.setNullSelectionAllowed(false); 
     			cbTipo.addItem("BLOQUEIO");
     			cbTipo.addItem("DESBLOQUEIO");
     			cbTipo.select("BLOQUEIO");
     			
				cbTipo.setWidth("120px");				
				cbTipo.setStyleName("caption-align-acesso");				
				addComponent(cbTipo);		
				
				fieldGroup.bind(cbTipo, "tipo");
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				DateField dfDataAgendamento = new DateField("Data Agendamento");
				dfDataAgendamento.setStyleName("caption-align-acesso");
				
				addComponent(dfDataAgendamento); 
				
				fieldGroup.bind(dfDataAgendamento, "data_agendado");
			}
		});
				
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				try{
					fieldGroup.commit();
					fireEvent(new NovoAgendamentoBloqueioDesbloqueioEvent(getUI(), item, true));
				}catch(Exception e){
					e.printStackTrace();
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
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new NovoAgendamentoBloqueioDesbloqueioEvent(getUI(), item, false));					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){

							}else{							
								fieldGroup.discard();				
								fireEvent(new NovoAgendamentoBloqueioDesbloqueioEvent(getUI(), item, false));
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
	
	public void addListerner(NovoAgendamentoBloqueioDesbloqueioListerner target){
		try {
			Method method = NovoAgendamentoBloqueioDesbloqueioListerner.class.getDeclaredMethod("onClose", NovoAgendamentoBloqueioDesbloqueioEvent.class);
			addListener(NovoAgendamentoBloqueioDesbloqueioEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(NovoAgendamentoBloqueioDesbloqueioListerner target){
		removeListener(NovoAgendamentoBloqueioDesbloqueioEvent.class, target);
	}
	public static class NovoAgendamentoBloqueioDesbloqueioEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public NovoAgendamentoBloqueioDesbloqueioEvent(Component source, Item item, boolean confirm) {
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
	public interface NovoAgendamentoBloqueioDesbloqueioListerner extends Serializable{
		public void onClose(NovoAgendamentoBloqueioDesbloqueioEvent event);
	}
	
}
