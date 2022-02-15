package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.MaterialUtil;
import com.digital.opuserp.view.util.MaterialUtil.MaterialEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class AlocarMaterialEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
		
	boolean validarData;
	boolean valid_data = false;
	
	
		
	public AlocarMaterialEditor(String title, boolean modal){
				
		
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
	
	private JPAContainer<Usuario> getUsuarios(){
		JPAContainer<Usuario> container = JPAContainerFactory.make(Usuario.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("funcao", "tecnico"));
		container.sort(new Object[]{"username"}, new boolean[]{true});
		return container;
	}
	
	private TextField tfDescricaoMaterial;
	Produto material_selecionado;
	
	JPAContainer<Usuario> containerUsuarios;
	ComboBox cbUsuarios;
	
	TextField txtQtd;
	TextField tfCodMaterial;
	
	Table tb;
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					cbUsuarios = new ComboBox("Usuário", getUsuarios());					
					cbUsuarios.setStyleName("caption-align");
					cbUsuarios.setNullSelectionAllowed(false);
					cbUsuarios.setItemCaptionPropertyId("username");					
					cbUsuarios.setRequired(true);	
					cbUsuarios.setTextInputAllowed(false);					
					cbUsuarios.focus();					
					
					cbUsuarios.setImmediate(true);
										
					addComponent(cbUsuarios);				
					//fieldGroup.bind(cbUsuarios,"operador");
				}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
				tfCodMaterial = new TextField("Produto");	
								
				tfCodMaterial.setWidth("60px");				
				tfCodMaterial.setNullRepresentation("");
				tfCodMaterial.setStyleName("caption-align");
				tfCodMaterial.setImmediate(true);
				tfCodMaterial.setId("txtCodMaterial");
				//tfCodMaterial.focus();
				
				JavaScript.getCurrent().execute("$('#txtCodMaterial').mask('00000000')");						
								
				tfCodMaterial.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ProdutoDAO cDAO = new ProdutoDAO();
						material_selecionado = new Produto();
																	
						if(event.getText()!=null && 
								!event.getText().isEmpty() && 
								!event.getText().equals("") 
							){
							
														
							material_selecionado = cDAO.find(Integer.parseInt(event.getText()));		
							
							if(material_selecionado != null){
								tfDescricaoMaterial.setReadOnly(false);
								tfDescricaoMaterial.setValue(material_selecionado.getNome());
								tfDescricaoMaterial.setReadOnly(true);
								
								
							}else {
								tfDescricaoMaterial.setReadOnly(false);
								tfDescricaoMaterial.setValue("");
								tfDescricaoMaterial.setReadOnly(true);		
								
								
							}
						}
						
					}
				});

				tfCodMaterial.setRequired(true);		
				tfDescricaoMaterial = new TextField();
				tfDescricaoMaterial.setReadOnly(true);
				tfDescricaoMaterial.setWidth("416px");			
									
				
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						MaterialUtil cUtil = new MaterialUtil(true, true);
						cUtil.addListerner(new MaterialUtil.MaterialListerner() {
							
							@Override
							public void onSelected(MaterialEvent event) {
									
									if(event.getMaterial() != null ){
										
										tfCodMaterial.setValue(event.getMaterial().getId().toString());
										tfDescricaoMaterial.setReadOnly(false);
										tfDescricaoMaterial.setValue(event.getMaterial().getNome());
										tfDescricaoMaterial.setReadOnly(true);
										material_selecionado = event.getMaterial();		
										
										
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
												
						addComponent(tfCodMaterial);							
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
						setStyleName("form-cutom");		
						addStyleName("form-cutom-new_hide_require");
						
						addComponent(tfDescricaoMaterial);							
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
						
					txtQtd = new TextField("Qtd.:");					
					txtQtd.setStyleName("caption-align");										
					txtQtd.setRequired(true);
					txtQtd.setId("txtQtd");
					
					//JavaScript.getCurrent().execute("$('#txtQtd').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtQtd').mask('00000000000000')");
										
					addComponent(txtQtd);
				}
		});
			
		vlRoot.addComponent(new FormLayout(){
			{
				tb = new Table();
				tb.setWidth("100%");
				
				tb.addContainerProperty("Cod",   String.class, "");
				tb.addContainerProperty("Nome",  String.class, "");
				tb.addContainerProperty("Qtd", String.class, "");
				
				tb.addGeneratedColumn("remover", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source, final Object itemId, Object columnId) {
						
						Button btRemover = new Button("remover", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								
								GenericDialog gd = new GenericDialog("Confirme para continuar!", "Deseja realmente remover este material ?", true,true);
								gd.addListerner(new GenericDialog.DialogListerner() {
									
									@Override
									public void onClose(DialogEvent event) {
										if(event.isConfirm()){
											tb.removeItem(itemId);											
										}
									}
								});
								
								getUI().addWindow(gd); 
								
							}
						});
						
						btRemover.setStyleName(Reindeer.BUTTON_LINK);
						return btRemover;
					}
				});
				
				
				
				Button btAdicionar = new Button("Adicionar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						if(material_selecionado != null && txtQtd.getValue() != null){
							tb.addItem(new Object[]{material_selecionado.getId().toString(),material_selecionado.getNome(),txtQtd.getValue()}, tb.getItemIds().size()+1);
							
							
							tfCodMaterial.setValue("");
							tfDescricaoMaterial.setReadOnly(false);
							tfDescricaoMaterial.setValue("");
							tfDescricaoMaterial.setReadOnly(true);
							txtQtd.setValue("");
							material_selecionado = null;
							
							
							
						}
					}
				});
				
				btAdicionar.setStyleName(Reindeer.BUTTON_SMALL);
								
				VerticalLayout vl1 = new VerticalLayout();
				vl1.setSizeFull();
				
				vl1.addComponent(btAdicionar); 
				vl1.addComponent(tb);
								
				addComponent(vl1); 
			}	
		});
		
	}
	
			
	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tb.getItemIds().size() > 0){
						
					
					List<Object[]> materiaisList = new ArrayList<>();
					
					for (Object itemId : tb.getItemIds()) {
						
						materiaisList.add(new Object[]{tb.getItem(itemId).getItemProperty("Cod").getValue(), tb.getItem(itemId).getItemProperty("Nome").getValue(),tb.getItem(itemId).getItemProperty("Qtd").getValue()});
					}
					
					EntityItem<Usuario> entityUsuario = (EntityItem<Usuario>)cbUsuarios.getItem(cbUsuarios.getValue());
					Usuario tecnicoSelecionado = entityUsuario.getEntity();					
										
					fireEvent(new AlocarMaterialEvent(getUI(),tecnicoSelecionado,materiaisList, true));		
						
				}else{
															
					if(cbUsuarios.getValue() == null){
						Notify.Show("Você precisa informar um Usuário", Notify.TYPE_ERROR);	
					}	
					
					if(material_selecionado == null){
						Notify.Show("Você precisa informar um Material", Notify.TYPE_ERROR);	
					}
					
					if(txtQtd == null){
						Notify.Show("Você precisa informar uma Quantidade", Notify.TYPE_ERROR);	
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
				
				fieldGroup.discard();				
				fireEvent(new AlocarMaterialEvent(getUI(),null,null,false));
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
	
	
	public void addListerner(AlocarMaterialListerner target){
		try {
			Method method = AlocarMaterialListerner.class.getDeclaredMethod("onClose", AlocarMaterialEvent.class);
			addListener(AlocarMaterialEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AlocarMaterialListerner target){
		removeListener(AlocarMaterialEvent.class, target);
	}
	public static class AlocarMaterialEvent extends Event{
		
		private Usuario tecnico;
		List<Object[]> materiaisList;
		
		private boolean confirm;
		
		public AlocarMaterialEvent(Component source, Usuario tecnico, List<Object[]> materiaisList, boolean confirm) {
			super(source);
			this.tecnico = tecnico;
			this.materiaisList = materiaisList;
						
			this.confirm = confirm;			
		}

		public Usuario getTecnico() {
			return tecnico;
		}	
		
		public List<Object[]> getMateriaisList(){
			return materiaisList;
		}
		
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface AlocarMaterialListerner extends Serializable{
		public void onClose(AlocarMaterialEvent event);
	}

	
}
