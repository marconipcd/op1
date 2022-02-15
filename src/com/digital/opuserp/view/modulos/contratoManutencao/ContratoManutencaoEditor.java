package com.digital.opuserp.view.modulos.contratoManutencao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContratoManutencaoDAO;
import com.digital.opuserp.dao.EmpresaDAO;
import com.digital.opuserp.dao.PlanoManutencaoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.PlanosManutencao;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.PlanoManutencaoUtil;
import com.digital.opuserp.view.util.PlanoManutencaoUtil.PlanoManutencaoUtilEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class ContratoManutencaoEditor extends Window {

	private Item item;
	private Button btSalvar;
	private Button btCancelar;
	
	private FieldGroup fieldGroup;
	private VerticalLayout vlRoot;
	
	
	
	Integer codCliente;	
	Cliente Clientelecionado;
	TextField tfCodCliente;
	TextField tfDescricaoCliente;
	
	Integer codPlanoManutencao;	
	PlanosManutencao planoMenutencaoSelecionado;
	TextField tfCodPlanoManutencao;
	TextField tfDescricaoPlanoManutencao;
	
	
	DateField txtDataVenc;
	DateField txtDataPrimeiroBoleto;
	TextField txtValorPrimeiroBoleto;
	
	public ContratoManutencaoEditor(final Item item, String title, boolean modal){
	
		this.item = item;
		
		setWidth("800px");
		
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
				
				HorizontalLayout hlButton = new HorizontalLayout();
				hlButton.setStyleName("hl_button_bottom");
				hlButton.setSpacing(true);
				hlButton.setMargin(true);
				
				
				hlButton.addComponent(buildBtFechar());
				hlButton.addComponent(buildBtSalvar());
				
				
				addComponent(hlButton);
				setComponentAlignment(hlButton, Alignment.BOTTOM_RIGHT);
			}
		});
		
		buildLayout();
	}
	private void buildLayout(){
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
				JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
				containerClientes.addContainerFilter(Filters.eq("status", "INATIVO"));
				
				tfCodCliente = new TextField("Cliente");				
				tfCodCliente.setWidth("60px");				
				tfCodCliente.setNullRepresentation("");
				tfCodCliente.setStyleName("caption-align-contrato-manutencao");
				tfCodCliente.focus();
				tfCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				tfCodCliente.setImmediate(true);
						
				
				tfCodCliente.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
		
						Clientelecionado = new Cliente();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							codCliente = Integer.parseInt(event.getText());							
							ClienteDAO cDAO = new ClienteDAO();					
							Clientelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));		
							
							if(Clientelecionado != null){								
									tfDescricaoCliente.setReadOnly(false);
									tfDescricaoCliente.setValue(Clientelecionado.getNome_razao());
									tfDescricaoCliente.setReadOnly(true);							
							}else{
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue("");
								tfDescricaoCliente.setReadOnly(true);								
							}
						}else{
							tfDescricaoCliente.setReadOnly(false);
							tfDescricaoCliente.setValue("");
							tfDescricaoCliente.setReadOnly(true);
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
								
											tfCodCliente.setValue(event.getCliente().getId().toString());
											tfDescricaoCliente.setReadOnly(false);
											tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
											tfDescricaoCliente.setReadOnly(true);
											Clientelecionado = event.getCliente();
											codCliente = Integer.parseInt((event.getCliente().getId().toString()));
											tfCodCliente.removeStyleName("invalid-txt");
											
										}else{
											
											tfCodCliente.setValue(event.getCliente().getId().toString());
											tfDescricaoCliente.setReadOnly(false);
											tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
											tfDescricaoCliente.setReadOnly(true);
											Clientelecionado = null;
											codCliente = null;											
											tfCodCliente.addStyleName("invalid-txt");
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
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
				JPAContainer<PlanosManutencao> containerPlanoManutencao = JPAContainerFactory.make(PlanosManutencao.class, ConnUtil.getEntity());
				containerPlanoManutencao.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
								
				tfCodPlanoManutencao = new TextField("Plano de Manutenção");				
				tfCodPlanoManutencao.setWidth("60px");				
				tfCodPlanoManutencao.setNullRepresentation("");
				tfCodPlanoManutencao.setStyleName("caption-align-contrato-manutencao");				
				tfCodPlanoManutencao.setId("tfCodPlanoManutencao");
				
				JavaScript.getCurrent().execute("$('#tfCodPlanoManutencao').mask('0000000000')");
				tfCodPlanoManutencao.setImmediate(true);
						
				
				tfCodPlanoManutencao.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
		
						planoMenutencaoSelecionado = new PlanosManutencao();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							codPlanoManutencao = Integer.parseInt(event.getText());							
							
							planoMenutencaoSelecionado = PlanoManutencaoDAO.find(Integer.parseInt(event.getText()));		
							
							if(planoMenutencaoSelecionado != null){	
									txtDataPrimeiroBoleto.setEnabled(true); 
								
									tfDescricaoPlanoManutencao.setReadOnly(false);
									tfDescricaoPlanoManutencao.setValue(planoMenutencaoSelecionado.getNome());
									tfDescricaoPlanoManutencao.setReadOnly(true);							
							}else{
								tfDescricaoPlanoManutencao.setReadOnly(false);
								tfDescricaoPlanoManutencao.setValue("");
								tfDescricaoPlanoManutencao.setReadOnly(true);								
							}
						}else{
							tfDescricaoPlanoManutencao.setReadOnly(false);
							tfDescricaoPlanoManutencao.setValue("");
							tfDescricaoPlanoManutencao.setReadOnly(true);
						}
					}
				});
				
				
				
				
				tfCodPlanoManutencao.setRequired(true);		
				tfDescricaoPlanoManutencao = new TextField();
				tfDescricaoPlanoManutencao.setTabIndex(2000);
				tfDescricaoPlanoManutencao.setReadOnly(true);
				tfDescricaoPlanoManutencao.setWidth("450px");
									
				if(item.getItemProperty("plano_manutencao") != null && item.getItemProperty("plano_manutencao").getValue() != null){
					EmpresaDAO eDAO = new EmpresaDAO();
					PlanosManutencao c = PlanoManutencaoDAO.find(((PlanosManutencao)item.getItemProperty("plano_manutencao").getValue()).getId());
					
					if(c != null){
						tfCodPlanoManutencao.setValue(c.getId().toString());
						tfCodPlanoManutencao.setReadOnly(false);
						tfDescricaoPlanoManutencao.setValue(c.getNome());
						tfDescricaoPlanoManutencao.setReadOnly(true);
					}
				}
							
				final Button btSearchPlanoManutencao = new Button();
				btSearchPlanoManutencao.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchPlanoManutencao.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchPlanoManutencao.setTabIndex(300000);
				btSearchPlanoManutencao.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						PlanoManutencaoUtil cUtil = new PlanoManutencaoUtil(true, true);
						cUtil.addListerner(new PlanoManutencaoUtil.PlanoManutencaoUtilListerner() {
							
							@Override
							public void onSelected(PlanoManutencaoUtilEvent event) {
										if(event.getPlanoManutencao() != null ){
											
											txtDataPrimeiroBoleto.setEnabled(true); 
								
											tfCodPlanoManutencao.setValue(event.getPlanoManutencao().getId().toString());
											tfDescricaoPlanoManutencao.setReadOnly(false);
											tfDescricaoPlanoManutencao.setValue(event.getPlanoManutencao().getNome());
											tfDescricaoPlanoManutencao.setReadOnly(true);
											planoMenutencaoSelecionado = event.getPlanoManutencao();
											tfCodPlanoManutencao.removeStyleName("invalid-txt");
											
										}else{
											
											tfCodPlanoManutencao.setValue("");
											tfDescricaoPlanoManutencao.setReadOnly(false);
											tfDescricaoPlanoManutencao.setValue("");
											tfDescricaoPlanoManutencao.setReadOnly(true);
											planoMenutencaoSelecionado = null;											
											tfCodPlanoManutencao.addStyleName("invalid-txt");
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
												
						addComponent(tfCodPlanoManutencao);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchPlanoManutencao =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchPlanoManutencao);							
					}
				}; 
							
				FormLayout frmDescPlanoManutencao = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoPlanoManutencao);							
					}
				}; 
				addComponent(frmButtonSearchPlanoManutencao);
				addComponent(frmDescPlanoManutencao);
				setExpandRatio(frmDescPlanoManutencao, 1);					
			}	
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);		
				setStyleName("form-cutom-new");		
				
				txtDataPrimeiroBoleto = new DateField("Data Primeiro Boleto");
				txtDataPrimeiroBoleto.setEnabled(false); 
				txtDataPrimeiroBoleto.setRequired(true);				
				txtDataPrimeiroBoleto.setStyleName("caption-align-contrato-manutencao");
				
				txtDataPrimeiroBoleto.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						
						if(planoMenutencaoSelecionado != null){
							String vlr = ContratoManutencaoDAO.calcularValorPrimeiroBoleto(planoMenutencaoSelecionado, txtDataPrimeiroBoleto.getValue());
							txtValorPrimeiroBoleto.setReadOnly(false);
							txtValorPrimeiroBoleto.setValue(Real.formatDbToString(vlr)); 
						}
					}
				});
				
				addComponent(txtDataPrimeiroBoleto); 
			}
		});	
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom-new");
				
				txtValorPrimeiroBoleto = new TextField("Valor Primeiro Boleto");
				txtValorPrimeiroBoleto.setReadOnly(true); 
				txtValorPrimeiroBoleto.setStyleName("caption-align-contrato-manutencao");
				
				addComponent(txtValorPrimeiroBoleto); 
			}
		});
	}
	
	private Button buildBtSalvar(){
		
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				concluir();
			}
		});
		
		btSalvar.addStyleName("default");
		
		return btSalvar;
	}
	
	private Button buildBtFechar(){
		
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(!fieldGroup.isModified()){
					fieldGroup.discard();
					fireEvent(new ContratoManutencaoEvent(getUI(), item, false, null,null,null, null));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							
							if(event.isConfirm()){
								concluir();
							}else{
								fieldGroup.discard();
								fireEvent(new ContratoManutencaoEvent(getUI(), item, false, null,null,null, null));
								close();
							}
						}
					});
					
					getUI().addWindow(gDialog);
				}
			}
		});
		
		return btCancelar;
	
	}
	
	private void concluir(){
		if(fieldGroup.isValid() ){
			try{					
				fieldGroup.commit();
				
				fireEvent(new ContratoManutencaoEvent(getUI(), item, true,planoMenutencaoSelecionado,Clientelecionado, txtDataPrimeiroBoleto.getValue(), txtValorPrimeiroBoleto.getValue()));
				
			}catch(Exception e){
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
			

			 Notify.Show_Invalid_Submit_Form();
		}
	}
	
	public void addListerner(ContratoManutencaoListerner target){
		try{
			Method method = ContratoManutencaoListerner.class.getDeclaredMethod("onClose", ContratoManutencaoEvent.class);
			addListener(ContratoManutencaoEvent.class, target, method);
		}catch(Exception e){
			System.out.println("Método não Encontrado!");
		}
	}
	
	public static class ContratoManutencaoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		private PlanosManutencao plano_manutencao;
		private Cliente cliente;
		
		private Date data_primeiro_boleto;
		private String valor_primeiro_boleto;
		
		public ContratoManutencaoEvent(Component source, Item item, boolean confirm, PlanosManutencao plano_manutencao, Cliente cliente, Date data_primeiro_boleto, String valor_primeiro_boleto){
			super(source);
			
			this.item = item;
			this.confirm = confirm;
			this.plano_manutencao = plano_manutencao;
			this.cliente = cliente;
		
			this.data_primeiro_boleto = data_primeiro_boleto;
			this.valor_primeiro_boleto = valor_primeiro_boleto;
		}
		
		public Item getItem(){
			return item;
		}
		public boolean isConfirm(){
			return confirm;
		}
		
		public PlanosManutencao getPlanoManutencao(){
			return plano_manutencao;
		}
		public Cliente getCliente(){
			return cliente;
		}
		
		public Date getDataPrimeiroBoleto(){
			return data_primeiro_boleto;
		}
		
		public String getValorPrimeiroBoleto(){
			return valor_primeiro_boleto;
		}
	}
	public interface ContratoManutencaoListerner extends Serializable{
		public void onClose(ContratoManutencaoEvent event);
	}
}
