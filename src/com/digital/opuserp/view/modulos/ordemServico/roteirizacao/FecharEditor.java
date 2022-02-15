package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.MateriaisAlocadosDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.TipoSubGrupoDAO;
import com.digital.opuserp.domain.EstoqueMovel;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.OseProduto;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.TipoSubGrupoOse;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.QtdEditor;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.QtdEditor.QtdEvent;
import com.digital.opuserp.view.util.MaterialUtil;
import com.digital.opuserp.view.util.MaterialUtil.MaterialEvent;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ProdutoEstoqueMovelUtil;
import com.digital.opuserp.view.util.ProdutoEstoqueMovelUtil.ProdutoEvent;
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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class FecharEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TextField tfCod;
	
	private TextArea txtConclusao;	
	private ComboBox cbProblema;
	
	Table tb = new Table();
	
	Ose ose;
	
	public FecharEditor(String title, boolean modal, Ose ose){
		
		this.ose = ose;
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		setWidth("600px");
		
		
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
	
	private TextField tfDescricaoMaterial;
	Produto material_selecionado;
	
	JPAContainer<Veiculos> containerVeiculos;
	
	
	TextField txtQtd;	
	TextField tfCodMaterial;
	
	
	public void buildLayout(){
		
					
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					
					//GrupoOse
					List<TipoSubGrupoOse> tipos = TipoSubGrupoDAO.getTipoSubGrupos(ose.getSubgrupo().getId());
											
					cbProblema = new ComboBox("Problema");
					cbProblema.setNullSelectionAllowed(false);		
					
					for (TipoSubGrupoOse to: tipos) {						
						cbProblema.addItem(to.getNome());
					}
					
					
					cbProblema.setRequired(true);
					cbProblema.setStyleName("caption-align");
					cbProblema.setWidth("300px");
					cbProblema.focus();
										
					addComponent(cbProblema);				
					
				}
		});
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					txtConclusao = new TextArea("Conclusão");				
					txtConclusao.addStyleName("uppercase");
					txtConclusao.setHeight("80px");
					txtConclusao.setWidth("350px");
					txtConclusao.setRequired(true);					
					txtConclusao.addStyleName("caption-align");
										
					addComponent(txtConclusao);				
					
				}
		});
		
		if(ose != null && ose.getGrupo().getBaixa_material() != null && ose.getGrupo().getBaixa_material().equals("NORMAL")){
			vlRoot.addComponent(new FormLayout(){
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					VerticalLayout vl = new VerticalLayout();
					vl.setWidth("100%");
									
					Button btAdicionarProduto = new Button("Adicionar");
					btAdicionarProduto.addClickListener(new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							ProdutoEstoqueMovelUtil produtoUtil = new ProdutoEstoqueMovelUtil(true,true,ose.getVeiculo_id());
							produtoUtil.addListerner(new ProdutoEstoqueMovelUtil.ProdutoListerner() {
								
								@Override
								public void onSelected(ProdutoEvent event) {
									if(event.getProduto() != null){
										
										final EstoqueMovel p = event.getProduto();
										
										QtdEditor qtdUtil = new QtdEditor("QTD", true, p.getProduto().getFracionar() == 0 ? false : true, new Float(p.getQtd()), true);										
										qtdUtil.addListerner(new QtdEditor.QtdListerner() {
											
											@Override
											public void onClose(QtdEvent event) {
												if(event.isConfirm()){
													
													double qtd_interna = qtdMaterialAlocada(p);
													double qtd_em_estoque = p.getQtd();
													
													if(qtd_em_estoque >= qtd_interna+event.getQtd()){
														addItem(p, new Float(event.getQtd()));
													}else{
														Notify.Show("Quantidade não disponível", Notify.TYPE_ERROR);
													}
												}
											}
										});
										
										getUI().addWindow(qtdUtil); 
										
									}
								}
							});
														
							getUI().addWindow(produtoUtil);
						}
					});
					
					vl.addComponent(btAdicionarProduto);
					
					tb.setWidth("497px");
					
					tb.addContainerProperty("COD", String.class,"");
					tb.addContainerProperty("PRODUTO", String.class,"");
					tb.addContainerProperty("QTD", Float.class,0);					
					tb.addGeneratedColumn("x", new Table.ColumnGenerator() {
						
						@Override
						public Object generateCell(Table source,final Object itemId, Object columnId) {

							Button bt = new Button("Excluir");
							bt.setStyleName(Reindeer.BUTTON_SMALL);
							bt.addClickListener(new Button.ClickListener() {
								
								@Override
								public void buttonClick(ClickEvent event) {
									removerItem(itemId);
								}
							});
							
							return bt;
						}
					});
										
					tb.setColumnExpandRatio("PRODUTO", 1f);
					
					vl.addComponent(tb); 
					
					addComponent(vl); 
									
				}
			});
		
		}
		
		if(ose != null && ose.getGrupo().getBaixa_material() != null && ose.getGrupo().getBaixa_material().equals("ESTORNO")){
			
			setWidth("823px");
			
			vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
//					cbVeiculos = new ComboBox("Veiculo", getVeiculos()){
//						@Override
//						public String getItemCaption(Object itemId) {
//						   Item item = getItem(itemId);
//						
//						   if (item == null) {
//						      return "";
//						   }
//						     
//						   return String.valueOf(item.getItemProperty("cod_veiculo")) + ", " +		   		  
//					   		  String.valueOf(item.getItemProperty("marca")) + ", " +
//					   		  String.valueOf(item.getItemProperty("modelo")) + ", " +
//					   		  String.valueOf(item.getItemProperty("cor"));
//						}
//					};
//					
//					
//					cbVeiculos.setStyleName("caption-align");
//					cbVeiculos.setNullSelectionAllowed(false);										
//					cbVeiculos.setRequired(true);	
//					cbVeiculos.setTextInputAllowed(false);					
//					cbVeiculos.focus();					
//					
//					cbVeiculos.setImmediate(true);
					
					TextField txtVeiculo = new TextField("Veiculo");
					txtVeiculo.setStyleName("caption-align");
					txtVeiculo.setValue(ose.getVeiculo_id().getCod_veiculo()+","+ose.getVeiculo_id().getMarca()+","+ose.getVeiculo_id().getModelo()+","+ose.getVeiculo_id().getCor());
					txtVeiculo.setEnabled(false); 			
					
					addComponent(txtVeiculo);				

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
						
						if(txtQtd.getValue() != null && !txtQtd.getValue().equals("")){
															
							if(material_selecionado != null && txtQtd.getValue() != null &&  !txtQtd.getValue().equals("") && ose.getVeiculo_id() != null){
								
								
								Veiculos veiculoSelecionado = ose.getVeiculo_id();
								
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
								
							}else{
								if(ose.getVeiculo_id() == null){
									Notify.Show("Não é possível fechar esta os sem Veículo Atribuído!", Notify.TYPE_ERROR);
								}
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
		
		
	}
	
	private JPAContainer<Veiculos> getVeiculos(){
		JPAContainer<Veiculos> container = JPAContainerFactory.make(Veiculos.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		return container;
	}
	
	private void removerItem(Object itemId){
		
		
		int i = 0;
		for(OseProduto ose: itens){
			
			if(ose.getProduto().getId().toString().equals(tb.getItem(itemId).getItemProperty("COD").getValue())){
							
				tb.removeItem(itemId);		
				itens.remove(i);
				break;
			}
			
			i++;
		}
	}
	
	private double qtdMaterialAlocada(EstoqueMovel p){
		
		for (OseProduto oseProduto : itens) {
			if(oseProduto.getProduto().getId() == p.getId()){
				return oseProduto.getQtd();
			}
		}
		
		return 0;
	}
	
	private List<OseProduto> itens = new ArrayList<>();
	private void addItem(EstoqueMovel s, Float qtd){
		
		if(tb != null){	
			
			boolean exist= false;	
			Float qtd_antiga = new Float(0);
			for(Object ob: tb.getItemIds().toArray()){
				if(tb.getItem(ob).getItemProperty("COD").getValue().equals(s.getId().toString())){
					qtd_antiga = new Float(tb.getItem(ob).getItemProperty("QTD").getValue().toString());				
					tb.removeItem(ob);					
					exist = true;
					break;
				}
			}
		  
			if(exist){
								
							
				tb.addItem(new Object[]{s.getId().toString(),s.getProduto().getNome(),qtd_antiga+qtd}, s.getId());
				
				itens = new ArrayList<>();
				
				for(Object ob: tb.getItemIds().toArray()){
					//Produto p = ProdutoDAO.find(Integer.parseInt(tb.getItem(ob).getItemProperty("COD").getValue().toString()));
					double qtd_ = Double.parseDouble(tb.getItem(ob).getItemProperty("QTD").getValue().toString());
					Integer id_pro = Integer.parseInt(tb.getItem(ob).getItemProperty("COD").getValue().toString());
					
					itens.add(new OseProduto(null, ose, new EstoqueMovel(id_pro),null, qtd_));
				}

				
			}else{
				
				
				tb.addItem(new Object[]{s.getId().toString(),s.getProduto().getNome(),qtd}, s.getId());

				
				itens = new ArrayList<>();
				
				for(Object ob: tb.getItemIds().toArray()){
					//Produto p = ProdutoDAO.find(Integer.parseInt(tb.getItem(ob).getItemProperty("COD").getValue().toString()));
					double qtd_ = Double.parseDouble(tb.getItem(ob).getItemProperty("QTD").getValue().toString());
					Integer id_pro = Integer.parseInt(tb.getItem(ob).getItemProperty("COD").getValue().toString());
					
					itens.add(new OseProduto(null, ose, new EstoqueMovel(id_pro),null, qtd_));
				}
			}
			
			
			
			
		}

		double totalGeral = 0;
		
		
	}
	
	
	private JPAContainer<Usuario> BuildUsuarios(){
		JPAContainer<Usuario> container = JPAContainerFactory.make(Usuario.class, ConnUtil.getEntity());
		
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("aaddcont", null));
		container.addContainerFilter(Filters.eq("tnocddaa", "", true));
		
		return container;
	}
	
	private JPAContainer<Usuario> getUsuarios(){
		JPAContainer<Usuario> container = JPAContainerFactory.make(Usuario.class, ConnUtil.getEntity());
		
		return container;
	}


	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(cbProblema.isValid() && txtConclusao.isValid() && itens != null){
					try {									
						fireEvent(new FecharRoteirizacaoEvent(getUI(),cbProblema.getValue().toString(), txtConclusao.getValue(),itens, true));						
					} catch (Exception e) {					
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
					}
				}else{
					
					if(!cbProblema.isValid()){
						cbProblema.addStyleName("invalid-txt");
					}else{
						cbProblema.removeStyleName("invalid-txt");
					}
					
					if(!txtConclusao.isValid()){
						txtConclusao.addStyleName("invalid-txt");
					}else{
						txtConclusao.removeStyleName("invalid-txt");
					}
					
					//if(itens == null){
						
					//}
					
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
				
				if(!txtConclusao.isModified()){
					fireEvent(new FecharRoteirizacaoEvent(getUI(),null, null,null, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(cbProblema.isValid() && txtConclusao.isValid()){
									try {				
										
										fieldGroup.commit();	
										fireEvent(new FecharRoteirizacaoEvent(getUI(), cbProblema.getValue().toString(),txtConclusao.getValue(),itens, true));					
											
									} catch (Exception e) {					
										e.printStackTrace();					
										Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
									}
								}else{
									
									if(!cbProblema.isValid()){
										cbProblema.addStyleName("invalid-txt");
									}else{
										cbProblema.removeStyleName("invalid-txt");
									}
									
									if(!txtConclusao.isValid()){
										txtConclusao.addStyleName("invalid-txt");
									}else{
										txtConclusao.removeStyleName("invalid-txt");
									}
									
									Notify.Show_Invalid_Submit_Form();
									
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
	
	
	public void addListerner(FecharRoteirizacaoListerner target){
		try {
			Method method = FecharRoteirizacaoListerner.class.getDeclaredMethod("onClose", FecharRoteirizacaoEvent.class);
			addListener(FecharRoteirizacaoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(FecharRoteirizacaoListerner target){
		removeListener(FecharRoteirizacaoEvent.class, target);
	}
	public static class FecharRoteirizacaoEvent extends Event{
		
		private String conclusao;
		private String problema;
		private List<OseProduto> itens;
		
		private boolean confirm;
		
		public FecharRoteirizacaoEvent(Component source, String problema,String conclusao,List<OseProduto> itens, boolean confirm) {
			super(source);
			this.conclusao = conclusao;	
			this.problema = problema;
			this.itens = itens;
			this.confirm = confirm;
		}		
		
		public String getProblema(){
			return problema;
		}
		
		public String getConclusao() {
			return conclusao;
		}
		
		public List<OseProduto> getItens(){
			return itens;
		}
		
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface FecharRoteirizacaoListerner extends Serializable{
		public void onClose(FecharRoteirizacaoEvent event);
	}

	
}

