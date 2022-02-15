package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.dao.UsuarioDAO;
import com.digital.opuserp.dao.VeiculoDAO;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.VeiculoUtil;
import com.digital.opuserp.view.util.VeiculoUtil.VeiculosEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AtribuirTecnicoEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TextField tfCod;
	
	private ComboBox cbUsuarios;
	private ComboBox cbAuxiliar;
	private ComboBox cbEquipes;
	
	private TextField txtCodVeiculo;
	private TextField txtNomeVeiculo;
	private Veiculos VeiculoSelecionado;
	
	private DateField dtAtribuicao;
	boolean valid_data = true;
	
	public AtribuirTecnicoEditor(String title, boolean modal){
		
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
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					cbEquipes = new ComboBox("Equipe");	
					
					cbEquipes.addItem("ESTRUTURA LANCAMENTO");
					cbEquipes.addItem("ESTRUTURA FUSAO");
					cbEquipes.addItem("ATIVACAO");
					cbEquipes.addItem("REPARO");
					cbEquipes.addItem("SUPORTE INTERNO");
					cbEquipes.addItem("CANCELAMENTO");
					
					cbEquipes.setStyleName("caption-align");
					cbEquipes.setNullSelectionAllowed(false);
					//cbEquipes.setItemCaptionPropertyId("username");					
					cbEquipes.setRequired(true);	
					cbEquipes.setTextInputAllowed(false);					
					cbEquipes.focus();					
					
					cbEquipes.setImmediate(true);
										
					addComponent(cbEquipes);				
					//fieldGroup.bind(cbEquipes,"operador");
				}
		});	
					
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					cbUsuarios = new ComboBox("Técnico", getUsuarios());					
					cbUsuarios.setStyleName("caption-align");
					cbUsuarios.setNullSelectionAllowed(false);
					cbUsuarios.setItemCaptionPropertyId("username");					
					cbUsuarios.setRequired(true);	
					cbUsuarios.setTextInputAllowed(false);					
					cbUsuarios.focus();					
					
					cbUsuarios.setImmediate(true);
										
					addComponent(cbUsuarios);				
					fieldGroup.bind(cbUsuarios,"operador");
				}
		});	
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					cbAuxiliar = new ComboBox("Auxiliar", getUsuarios());					
					cbAuxiliar.setStyleName("caption-align");
					cbAuxiliar.setNullSelectionAllowed(false);
					cbAuxiliar.setItemCaptionPropertyId("username");					
					cbAuxiliar.setRequired(true);	
					cbAuxiliar.setTextInputAllowed(false);					
					cbAuxiliar.focus();					
					
					cbAuxiliar.setImmediate(true);
										
					addComponent(cbAuxiliar);				
					fieldGroup.bind(cbAuxiliar,"auxiliar");
				}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				
				
				JPAContainer<Veiculos> containerClientes = JPAContainerFactory.make(Veiculos.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
								
				txtCodVeiculo = new TextField("Veículo");				
				txtCodVeiculo.setWidth("60px");				
				txtCodVeiculo.setNullRepresentation("");
				txtCodVeiculo.setStyleName("caption-align");
				txtCodVeiculo.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				txtCodVeiculo.setImmediate(true);
						
				
				
				txtCodVeiculo.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						VeiculoSelecionado = new Veiculos();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							VeiculoSelecionado = VeiculoDAO.findbyCod(event.getText());								
							
							if(VeiculoSelecionado != null){
								txtNomeVeiculo.setReadOnly(false);
								txtNomeVeiculo.setValue(VeiculoSelecionado.getMarca()+" - "+VeiculoSelecionado.getModelo()+" - "+VeiculoSelecionado.getPlaca());
								txtNomeVeiculo.setReadOnly(true);
															
							}else {
								txtNomeVeiculo.setReadOnly(false);
								txtNomeVeiculo.setValue("");
								txtNomeVeiculo.setReadOnly(true);
								
							}
						}else{
							txtNomeVeiculo.setReadOnly(false);
							txtNomeVeiculo.setValue("");
							txtNomeVeiculo.setReadOnly(true);	
							
						}
					}
				});

				txtCodVeiculo.setRequired(false);		
				txtNomeVeiculo = new TextField();
				txtNomeVeiculo.setTabIndex(2000);
				txtNomeVeiculo.setReadOnly(true);
				txtNomeVeiculo.setWidth("450px");
				
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setTabIndex(300000);
				btSearchCliente.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						VeiculoUtil cUtil = new VeiculoUtil(true, true);
						cUtil.addListerner(new VeiculoUtil.VeiculoListerner() {
							
							@Override
							public void onSelected(
									VeiculosEvent event) {
									if(event.getVeiculo() != null){
										txtCodVeiculo.setValue(event.getVeiculo().getCod_veiculo().toString());
										txtNomeVeiculo.setReadOnly(false);
										txtNomeVeiculo.setValue(event.getVeiculo().getMarca()+" - "+event.getVeiculo().getModelo()+" - "+event.getVeiculo().getPlaca());
										txtNomeVeiculo.setReadOnly(true);
										VeiculoSelecionado = event.getVeiculo();
										
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
												
						addComponent(txtCodVeiculo);							
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
						
						addComponent(txtNomeVeiculo);							
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
					
					dtAtribuicao = new DateField("Data");
					dtAtribuicao.setStyleName("caption-align");
					dtAtribuicao.setRequired(true);	
					dtAtribuicao.setValue(new Date());
					
					dtAtribuicao.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(dtAtribuicao.getValue() != null){
								try{
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									Date dt1 = sdf.parse(sdf.format(dtAtribuicao.getValue()));
									Date dt2 = sdf.parse(sdf.format(new Date()));
									if(dtAtribuicao.getValue().after(new Date()) || dt1.equals(dt2)){
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
					
										
					addComponent(dtAtribuicao);				
					
				}
		});	
	}
	
	
	
	
	private JPAContainer<Usuario> getUsuarios(){
		JPAContainer<Usuario> container = JPAContainerFactory.make(Usuario.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("funcao", "tecnico"));
		container.sort(new Object[]{"username"}, new boolean[]{true});
		return container;
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				if(valid_data && cbUsuarios.isValid() && VeiculoSelecionado != null && cbEquipes.isValid()){
					try {				
							
						Usuario u = UsuarioDAO.find((Integer)cbUsuarios.getItem(cbUsuarios.getValue()).getItemProperty("id").getValue());
						Usuario uAux = UsuarioDAO.find((Integer)cbAuxiliar.getItem(cbAuxiliar.getValue()).getItemProperty("id").getValue());
						
						//boolean valid = OseDAO.encaminharOseValid(u, VeiculoSelecionado);
								
						//if(valid){
							fireEvent(new AtribuirTecnicoEvent(getUI(), u,uAux,VeiculoSelecionado,cbEquipes.getValue().toString(),dtAtribuicao.getValue(), true));
						//}else{
						//	Notify.Show("Veículo ou Usuário já estão encaminhados para outra ordem de serviço!", Notify.TYPE_ERROR);
						//}
							
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
					
					Notify.Show_Invalid_Submit_Form();
					
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
					fireEvent(new AtribuirTecnicoEvent(getUI(), null,null,null,null,null, false));
					close();
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
	
	
	public void addListerner(AtribuirTecnicoListerner target){
		try {
			Method method = AtribuirTecnicoListerner.class.getDeclaredMethod("onClose", AtribuirTecnicoEvent.class);
			addListener(AtribuirTecnicoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AtribuirTecnicoListerner target){
		removeListener(AtribuirTecnicoEvent.class, target);
	}
	public static class AtribuirTecnicoEvent extends Event{
		
		private String equipe;
		private Veiculos veiculoSelecionado;
		private Usuario usuario;
		private Usuario uAux;			
		private boolean confirm;
		private Date dataAtribuicao;
		
		
		public AtribuirTecnicoEvent(Component source, Usuario usuario,Usuario uAux,Veiculos veiculoSelecionado, String equipe, Date dataAtribuicao, boolean confirm) {
			super(source);
			this.usuario = usuario;
			this.uAux = uAux;	
			this.confirm = confirm;		
			this.veiculoSelecionado = veiculoSelecionado;
			this.equipe = equipe;
			this.dataAtribuicao = dataAtribuicao;
		}		

		public Usuario getUaux() {
			return uAux;
		}
		
		public Usuario getUsuario() {
			return usuario;
		}

		public boolean isConfirm() {
			return confirm;
		}
		
		public String getEquipe(){
			return equipe;
		}
		
		public Veiculos getVeiculo(){
			return veiculoSelecionado;
		}
		
		public Date getAtribuicao(){
			return dataAtribuicao;
		}
	}
	public interface AtribuirTecnicoListerner extends Serializable{
		public void onClose(AtribuirTecnicoEvent event);
	}

	
}

