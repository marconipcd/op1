package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.MateriaisAlocadosDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.EstoqueMovel;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.MaterialUtil;
import com.digital.opuserp.view.util.MaterialUtil.MaterialEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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

public class AlocarMaterialDoNadaEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
		
	boolean validarData;
	boolean valid_data = false;
	
	
		
	public AlocarMaterialDoNadaEditor(String title, boolean modal){
				
		
		this.setWidth("810px");
		this.setHeight("690px");
		
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
	
	private JPAContainer<Veiculos> getVeiculos(){
		JPAContainer<Veiculos> container = JPAContainerFactory.make(Veiculos.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		return container;
	}
	
	private TextField tfDescricaoMaterial;
	Produto material_selecionado;
	
	JPAContainer<Veiculos> containerVeiculos;
	ComboBox cbVeiculos;
	
	TextField txtQtd;
	TextField txtQtdEstoque;
	TextField tfCodMaterial;
	
	Table tb;
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					cbVeiculos = new ComboBox("Veiculo", getVeiculos()){
						@Override
						public String getItemCaption(Object itemId) {
						   Item item = getItem(itemId);
						
						   if (item == null) {
						      return "";
						   }
						     
						   return String.valueOf(item.getItemProperty("cod_veiculo")) + ", " +		   		  
					   		  String.valueOf(item.getItemProperty("marca")) + ", " +
					   		  String.valueOf(item.getItemProperty("modelo")) + ", " +
					   		  String.valueOf(item.getItemProperty("cor"));
						}
					};
					
					
					cbVeiculos.setStyleName("caption-align");
					cbVeiculos.setNullSelectionAllowed(false);										
					cbVeiculos.setRequired(true);	
					cbVeiculos.setTextInputAllowed(false);					
					cbVeiculos.focus();					
					
					cbVeiculos.setImmediate(true);
										
					addComponent(cbVeiculos);				

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
																	
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							material_selecionado = cDAO.find(Integer.parseInt(event.getText()));		
							
							if(material_selecionado != null){
								tfDescricaoMaterial.setReadOnly(false);
								tfDescricaoMaterial.setValue(material_selecionado.getNome());
								tfDescricaoMaterial.setReadOnly(true);
								
								txtQtdEstoque.setReadOnly(false);
								txtQtdEstoque.setValue(material_selecionado.getQtdEstoqueDeposito().toString());
								txtQtdEstoque.setReadOnly(true);
								
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
										txtQtdEstoque.setReadOnly(false);
										txtQtdEstoque.setValue(event.getMaterial().getQtdEstoqueDeposito().toString());
										txtQtdEstoque.setReadOnly(true);
										
										
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
					
				txtQtdEstoque = new TextField("Qtd. Estoque");					
				txtQtdEstoque.setStyleName("caption-align");										
				txtQtdEstoque.setRequired(true);
				txtQtdEstoque.setId("txtQtdEstoque");
				txtQtdEstoque.setReadOnly(true);
				
				//JavaScript.getCurrent().execute("$('#txtQtd').maskMoney({decimal:',',thousands:'.'})");
				//JavaScript.getCurrent().execute("$('#txtQtd').mask('00000000000000')");
									
				addComponent(txtQtdEstoque);
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
					
					txtQtd.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							if(txtQtdEstoque.getValue() != null && event.getText() != null && !txtQtdEstoque.getValue().equals("") && !event.getText().equals("")){
								
								Float qtdEstoque = new Float(txtQtdEstoque.getValue());
								Float qtd 		 = new Float(event.getText());
								
								if(qtd > qtdEstoque){
									Notify.Show("Quantidade alocada não pode ser maior do que a quantidade disponível!", Notify.TYPE_ERROR);
									txtQtd.addStyleName("invalid-txt");
								}else{
									txtQtd.removeStyleName("invalid-txt");
								}
								
							}
						}
					});
					

					JavaScript.getCurrent().execute("$('#txtQtd').mask('00000000000000')");
										
					addComponent(txtQtd);
				}
		});
			
		vlRoot.addComponent(new FormLayout(){
			{
				tb = new Table(){
					
					protected String formatPropertyValue(Object rowId, Object colId,
							Property<?> property) {
						
						Object v = property.getValue();
					
						if (v instanceof Veiculos) {
						         Veiculos veiculo = (Veiculos) v;
						         
						         if(colId.equals("Veiculo")){						        	 
						        	 return veiculo.getCod_veiculo()+", "+veiculo.getMarca()+", "+veiculo.getModelo()+", "+veiculo.getCor();
						         }
						}
						
						
						return super.formatPropertyValue(rowId, colId, property);
					}
				};
				tb.setWidth("747px");
				tb.setImmediate(true);
				tb.addContainerProperty("Cod",   String.class, "");				
				tb.addContainerProperty("Veiculo",   Veiculos.class, null);
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
											
											Integer codEstoque = Integer.parseInt(tb.getItem(itemId).getItemProperty("Cod").getValue().toString());
											Integer qtdEstornar = Integer.parseInt(tb.getItem(itemId).getItemProperty("Qtd").getValue().toString());
											
											boolean check = MateriaisAlocadosDAO.estornarMaterialSemEstoque(codEstoque,qtdEstornar);
											
											if(check){
												boolean c = tb.removeItem(itemId);
											
//												List<Item> itens = new ArrayList<Item>();
//												for (Object itemId: tb.getItemIds()) {												
//													itens.add(tb.getItem(itemId));
//												}
//												
//												tb.removeAllItems();
//												
//												for (Item item : itens) {
//													tb.addItem(item);
//												}
//												
												
												if(c){
													Notify.Show("Material Estornado com Sucesso!", Notify.TYPE_SUCCESS);
												}
											}else{
												Notify.Show("Não foi possível remover ", Notify.TYPE_ERROR);
											}
											
											
											
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
						
						if(txtQtdEstoque.getValue() != null && txtQtd.getValue() != null && !txtQtdEstoque.getValue().equals("") && !txtQtd.getValue().equals("")){
							Float qtdEstoque = new Float(txtQtdEstoque.getValue());
							Float qtd 		 = new Float(txtQtd.getValue());
							
							boolean check_qtd = false;
							
							if(qtd > qtdEstoque){
								check_qtd = false;
								Notify.Show("Quantidade alocada não pode ser maior do que a quantidade disponível!", Notify.TYPE_ERROR);
								txtQtd.addStyleName("invalid-txt");
							}else{
								
								check_qtd = true;
								txtQtd.removeStyleName("invalid-txt");
							}
							
							if(material_selecionado != null && txtQtd.getValue() != null && check_qtd && !txtQtd.getValue().equals("")){
								
								EntityItem<Veiculos> entityUsuario = (EntityItem<Veiculos>)cbVeiculos.getItem(cbVeiculos.getValue());
								Veiculos veiculoSelecionado = entityUsuario.getEntity();	
								
								EstoqueMovel estoqMovel = MateriaisAlocadosDAO.alocarMaterialSemEstoque(veiculoSelecionado, material_selecionado, Integer.parseInt(txtQtd.getValue()));
								
								Integer tt = tb.getItemIds().size()+1;
								
								String materialSelecionado = material_selecionado.getNome();
								String qtdAdd = txtQtd.getValue();
								
								tb.addItem(new Object[]{estoqMovel.getId().toString(),veiculoSelecionado, materialSelecionado,qtdAdd},null );
								//tb.addItem()
								
								
								tfCodMaterial.setValue("");
								tfDescricaoMaterial.setReadOnly(false);
								tfDescricaoMaterial.setValue("");
								tfDescricaoMaterial.setReadOnly(true);
								txtQtd.setValue("");
								material_selecionado = null;
								
								txtQtdEstoque.setReadOnly(false);
								txtQtdEstoque.setValue("");
								txtQtdEstoque.setReadOnly(true);
								
								
								
							}
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
						
					
						
										
					fireEvent(new AlocarMaterialDoNadaEvent(getUI(),null,null, true));		
						
				}else{
															
					if(cbVeiculos.getValue() == null){
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
				
				
				if(tb.getItemIds().size() > 0){
					
					for(Object itemId : tb.getItemIds()){
					
						Integer codEstoque = Integer.parseInt(tb.getItem(itemId).getItemProperty("Cod").getValue().toString());
						Integer qtdEstornar = Integer.parseInt(tb.getItem(itemId).getItemProperty("Qtd").getValue().toString());
						
						boolean check = MateriaisAlocadosDAO.estornarMaterialSemEstoque(codEstoque,qtdEstornar);
						
						Notify.Show("Material Estornado com Sucesso!", Notify.TYPE_SUCCESS);
					}
				}
				
				fieldGroup.discard();				
				fireEvent(new AlocarMaterialDoNadaEvent(getUI(),null,null,false));
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
	
	
	public void addListerner(AlocarMaterialDoNadaListerner target){
		try {
			Method method = AlocarMaterialDoNadaListerner.class.getDeclaredMethod("onClose", AlocarMaterialDoNadaEvent.class);
			addListener(AlocarMaterialDoNadaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AlocarMaterialDoNadaListerner target){
		removeListener(AlocarMaterialDoNadaEvent.class, target);
	}
	public static class AlocarMaterialDoNadaEvent extends Event{
		
		private Veiculos veiculo;
		List<Object[]> materiaisList;
		
		private boolean confirm;
		
		public AlocarMaterialDoNadaEvent(Component source, Veiculos veiculo, List<Object[]> materiaisList, boolean confirm) {
			super(source);
			this.veiculo = veiculo;
			this.materiaisList = materiaisList;
						
			this.confirm = confirm;			
		}

		public Veiculos getVeiculo() {
			return veiculo;
		}	
		
		public List<Object[]> getMateriaisList(){
			return materiaisList;
		}
		
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface AlocarMaterialDoNadaListerner extends Serializable{
		public void onClose(AlocarMaterialDoNadaEvent event);
	}

	
}
