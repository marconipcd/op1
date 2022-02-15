package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.PreContratoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.PreContrato;
import com.digital.opuserp.domain.Swith;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.PreContratoEditor.PreContratoEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PreContratoView  extends Window  {

	private static final long serialVersionUID = 1L;
	
	Item item;
	Button btNovo; 
	Button btImprimir;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
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
	
	public PreContratoView(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("888px");
		setHeight("510px");
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(true);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.addComponent(buildBtImprimir());
				hlButtons.addComponent(buildBtNovo());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);

				addComponent(vlRoot);
			}
		});		

		buildLayout();
	}
	Table tb;
	public void buildLayout(){
		
		EntityItem<PreContrato> entityItem = (EntityItem<PreContrato>)item;
		JPAContainer<PreContrato> container = JPAContainerFactory.makeReadOnly(PreContrato.class, ConnUtil.getEntity());
		container.addNestedContainerProperty("plano.nome");
		container.addNestedContainerProperty("contrato.id");
		container.addContainerFilter(Filters.eq("contrato", entityItem.getEntity()));
		
		tb = new Table(null, container){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("contrato.id")){
					
					if(tb.getItem(rowId).getItemProperty("contrato.id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("contrato.id").getValue()).toString();				
					}					
				}
				
				return super.formatPropertyValue(rowId, colId, property);			
			}
		}; 
		 tb.setVisibleColumns(new Object[]{"id", "contrato.id", "plano.nome","tipo_plano","isencao_instalacao"});
		 tb.setColumnHeader("id", "Cod");
		 tb.setColumnHeader("contrato.id", "Contrato");
		 tb.setColumnHeader("plano.nome", "Plano");
		 tb.setColumnHeader("tipo_plano", "Tipo Plano");
		 tb.setColumnHeader("isencao_instalacao", "Instalação Grátis");
		 tb.setSizeFull();
		 tb.setSelectable(true);
		 tb.setMultiSelect(false); 
		 
		 tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(event.getProperty().getValue() != null){
					btImprimir.setEnabled(true);					
				}else{
					btImprimir.setEnabled(false);
				}
			}
		});
		 
		 vlRoot.addComponent(tb);
	}

	public Button buildBtNovo() {
		
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final PreContratoEditor preContrato = new PreContratoEditor(item, "Novo Pré-Contrato", true);
				preContrato.addListerner(new PreContratoEditor.PreContratoListerner() {

					@Override
					public void onClose(com.digital.opuserp.view.modulos.acesso.contratos_acesso.PreContratoEditor.PreContratoEvent event) {
						
						if(event.isConfirm()){
							boolean check = PreContratoDAO.salvarPreContrato(event.getPreContrato());
							
							if(check){
									Notify.Show("PreContrato cadastrado com sucesso!", Notify.TYPE_SUCCESS);
									preContrato.close();
							}
						}
					}								
				});
				
				getUI().addWindow(preContrato); 
		    }		
		});
		
		ShortcutListener slbtOK = new ShortcutListener("Novo", ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btNovo.click();
			}
		};
		
		btNovo.addShortcutListener(slbtOK);		
		btNovo.setStyleName("default");
		//btSalvar.setEnabled(false);
		return btNovo;
	}
	public Button buildBtImprimir() {
		btImprimir = new Button("Imprimir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
					if(tb.getValue() != null){
									
						try{
							
							Window window = new Window();
							window.setCaption("Termo de Adesão");
					        window.setWidth("800px");
					        window.setHeight("600px");
					        window.setResizable(true);
					        window.center();
					        window.setModal(true);
					        window.setStyleName("disable_scroolbar");		
					        window.setCloseShortcut(KeyCode.ESCAPE, null);
																					
							StreamResource resource = new StreamResource(new ImprimirPreContrato(Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString())), "PRE_CONTRATO.pdf");
					        resource.getStream();			        
					        resource.setMIMEType("application/pdf");
					        resource.setCacheTime(0);
					        
					        Embedded e = new Embedded();
					        e.setSizeFull();
					        e.setType(Embedded.TYPE_BROWSER); 
					        e.setSource(resource);			     
					        window.setContent(e);
					        getUI().addWindow(window);
					        window.focus();
					        
						}catch(Exception e){
							e.printStackTrace();
						}
					}
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btImprimir.click();
			}
		};
		
		btImprimir.addShortcutListener(slbtCancelar);
		btImprimir.setEnabled(false);
		
		return btImprimir;
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
		
		private Item item;
		private boolean confirm;
		
		public PreContratoEvent(Component source, Item item, boolean confirm) {
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
	public interface PreContratoListerner extends Serializable{
		public void onClose(PreContratoEvent event);
	}
}
