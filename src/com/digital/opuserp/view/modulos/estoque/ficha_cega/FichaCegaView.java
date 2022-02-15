package com.digital.opuserp.view.modulos.estoque.ficha_cega;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesProdutoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.GrupoProdutoDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.AlteracoesProduto;
import com.digital.opuserp.domain.Cofins;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.FichaCegaCab;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.Icms;
import com.digital.opuserp.domain.Ipi;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Pis;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.estoque.produto.ProdutoEditor;
import com.digital.opuserp.view.modulos.estoque.produto.ProdutoEditor.ProdutoEvent;
import com.digital.opuserp.view.modulos.estoque.produto.ProdutoView;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class FichaCegaView extends VerticalLayout {

	private JPAContainer<FichaCegaCab> container;
	
	private Table tb;
	private TextField tfBusca;
	
	private Button btNovo;
	private Button btEditar;
	private Button btExcluir;
	private Button btVisualizar;
	
	private Label lbRegistros;
	
	private HorizontalLayout hlFloat;	
	
	private ShortcutListener slNovo;
	private ShortcutListener slEditar;
	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	
	private ComboBox cbStatus;
	private ComboBox cbStatusSaldo;
	private ComboBox cbTipoItem;
	
	public FichaCegaView(boolean act){		
		
		if(act){
			
			setSizeFull();		
			gmDAO = new GerenciarModuloDAO();		
			
			HorizontalLayout hlButons = new HorizontalLayout();
			
			hlButons.addComponent(BuildbtNovo());	
			hlButons.addComponent(BuildbtEditar());
			hlButons.addComponent(BuildbtVisualizar());	
			hlButons.addComponent(BuildbtExcluir());
		
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					
					addComponent(buildCbStatus());
					addComponent(buildTfbusca());
					
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(lbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
			lbLegend.setWidth("250px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
					
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
		}
	}
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
		addFilter(tfBusca.getValue());
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);		
		btVisualizar.setEnabled(false);
	}
	private ComboBox buildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		cbStatus.addItem("ATIVO");
		cbStatus.addItem("INATIVO");		
		cbStatus.setImmediate(true);
		cbStatus.select("ATIVO");
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		
		return cbStatus;
	}
		
	ComboBox cbGrupos;
	private ComboBox buildCbGrupo(){
		
		List<GrupoProduto> grupos = GrupoProdutoDAO.getAll();		
		cbGrupos = new ComboBox();
		cbGrupos.setNullSelectionAllowed(false);
		cbGrupos.setTextInputAllowed(false);
		cbGrupos.addItem("TODOS OS GRUPOS");
		
		for (GrupoProduto grupoProduto : grupos) {
			cbGrupos.addItem(grupoProduto.getNome_grupo());
		}
		
		cbGrupos.setImmediate(true);
		cbGrupos.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		cbGrupos.select("TODOS OS GRUPOS");
				
		return cbGrupos;
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ProdutoView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}			
		}
	}
	
	public Label lbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btNovo.click();				
			}
		};
		return slNovo;
	}
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btEditar.click();
				btEditar.removeShortcutListener(slEditar);			
			}
		};
		
		return slEditar;
	}
	public JPAContainer<FichaCegaCab> buildContainer(){
		container = JPAContainerFactory.makeBatchable(FichaCegaCab.class,ConnUtil.getEntity());
		
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));		
			
		return container;
	}

	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}			
				
				 }else{					 
					return super.formatPropertyValue(rowId, colId, property);
				 }

				return "";
				
			}
		};	
		tb.setNullSelectionAllowed(false); 
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.getVisibleColumns();
		
		tb.setVisibleColumns(new Object[] {"id","data","usuario"});
		
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("data", "Data");
		tb.setColumnHeader("usuario", "Operador");
		
		tb.setColumnAlignment("valorCusto", Align.RIGHT);
		tb.setColumnAlignment("custo_total", Align.RIGHT);
		tb.setColumnAlignment("lucro", Align.RIGHT);
		tb.setColumnAlignment("margemLucro", Align.RIGHT);
		tb.setColumnAlignment("valorVenda", Align.RIGHT);
		tb.setColumnAlignment("qtdEstoque", Align.CENTER);
		
		tb.setImmediate(true);	
		tb.setColumnCollapsible("nome", false);
		
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditar.setEnabled(true);
					btExcluir.setEnabled(true);				
				}else{	
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);					
					btVisualizar.setEnabled(false);
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
			
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()   && tb.getValue() != null){
					btEditar.click();
				}				
			}
		});
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {
                
                if (propertyId == null)
                    return "row-header-default"; // Will not actually be visible
               
                Item item = source.getItem(itemId); 
                
                if(OpusERP4UI.getEmpresa().getId() != 4 && item != null){
                	
                	Float qtd = (Float) item.getItemProperty("qtdEstoque").getValue();
                	Float qtdDeposito = (Float) item.getItemProperty("qtdEstoqueDeposito").getValue();
                	
	                if(source.getItem(itemId).getItemProperty("preco_promocional").getValue() != null && !source.getItem(itemId).getItemProperty("preco_promocional").getValue().toString().equals("0.0") && qtd != null && qtdDeposito != null && qtd <= (new Float(0.0)) && qtdDeposito <= (new Float(0.00))){                		
	                	
	                	Double vlrPromo = new Double( source.getItem(itemId).getItemProperty("preco_promocional").getValue().toString());
	                	
	                	if(vlrPromo > 0){
	                		return "row-header-produto-sem-saldo-promocional";
	                	}
	                	                	                
	                }
	                
	                if(source.getItem(itemId).getItemProperty("preco_promocional").getValue() != null ){                		
	                	
	                	Double vlrPromo = new Double( source.getItem(itemId).getItemProperty("preco_promocional").getValue().toString());
	                	
	                	if(qtd <= 0 && qtdDeposito >= 1){
	                		return "row-header-reposicao-promo";
	                	}else{	                		
	                		if(vlrPromo > 0){	                	
	                			return "row-header-end-principal";
	                		}                	                
	                	}
	                }
	                
	                if(qtd <= 0 && qtdDeposito >= 1){
	                	  return "row-header-reposicao";	         	
	                }
                }
               
                if(item != null && item.getItemProperty("qtdEstoque").getValue() != null){
                	
                	 if(OpusERP4UI.getEmpresa().getId() != 4){
 
		                	Float qtd = (Float) item.getItemProperty("qtdEstoque").getValue();
		                	Float qtdDeposito = (Float) item.getItemProperty("qtdEstoqueDeposito").getValue();
		                	
			                if(qtd != null && qtdDeposito != null && qtd <= (new Float(0.0)) && qtdDeposito <= (new Float(0.00))){
				                return "row-header-produto-sem-saldo"; 		          
			                }else{
			                    return "row-header-default"; 
			                }
                	 }else{
                		 return "row-header-default";
                	 }
	                
                }else{
                	 return "row-header-default";
                }
            }            
        });
	
		return tb;
	}
	
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setInputPrompt("BUSCAR...");
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}
		
	public void addFilter(String s) {
		container.setApplyFiltersImmediately(false);
		container.removeAllContainerFilters();
		container.setQueryModifierDelegate(null);
		
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));	
						
		if(s != null && !s.equals("") && !s.isEmpty()){
		
			Object[] collums = tb.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			for(Object c:collums){		 			
				//if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
				//	filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				//}			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}		
			}
			
			container.addContainerFilter(Filters.or(filtros));
		}
		container.applyFilters();
		
		if(lbRegistros != null){
			hlFloat.replaceComponent(lbRegistros, lbRegistros());
		}
	 }
	
	
		
	private Button btAtualizarCodigo;
	public Button BuildbtAtualizarCodigoBarras(){
		btAtualizarCodigo = new Button("Atualizar Código de Barras", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "AtualizarCodigoBarras"))				
				{
					
									
					final ProdutoEditor cEditor = new ProdutoEditor(tb.getItem(tb.getValue()), "Atualizar Código Barras", true, true,false,false,null);
					cEditor.addListerner(new ProdutoEditor.ProdutoListerner() {
						
						@Override
						public void onClose(ProdutoEvent event) {
							if(event.isConfirm()){
								
								try {
																			
									tb.commit();
									container.commit();
									cEditor.close();
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Atualizou um Código de Barras do Produto"));
									
									Notify.Show("Código de Barras Atualizado Com Sucesso!", Notify.TYPE_SUCCESS);
								} catch (Exception e) {
									e.printStackTrace();															
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);								
								}
								
							}
						}

						
					});
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();
							//refresh();
						}
					});
					
					getUI().addWindow(cEditor);
				
				}else{					
					Notify.Show("Você não Possui Permissão para Atualizar um Código de Barras de  Produto", Notify.TYPE_ERROR);
				}
			}
		});

		btAtualizarCodigo.setEnabled(false);
		return btAtualizarCodigo;
	}
	
	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
					
					final BeanItem<Produto> newItem = new BeanItem<Produto>(new Produto());
									
					final ProdutoEditor cEditor = new ProdutoEditor(newItem, "Cadastrar Produto", true,false,false,false,null);
					cEditor.addListerner(new ProdutoEditor.ProdutoListerner() {
						
						@Override
						public void onClose(ProdutoEvent event) {
							if(event.isConfirm()){
								
								try {
									Produto cAdd = newItem.getBean();									
									cAdd.setEmpresaId(OpusERP4UI.getEmpresa());
									ProdutoDAO.insertProduto(cAdd);
									tb.focus();
									//refresh();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Produto"));								
									Notify.Show("Produto Cadastrado Com Sucesso!", Notify.TYPE_SUCCESS);									
									AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "CADASTRO DE PRODUTO", cAdd,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									
									Icms icms = event.getIcms();
									Ipi ipi = event.getIpi();
									Pis pis = event.getPis();
									Cofins cofins = event.getCofins();
																		
									icms.setProduto(cAdd);
									ipi.setProduto(cAdd);
									pis.setProduto(cAdd);
									cofins.setProduto(cAdd);
									
									refresh();
								
								} catch (Exception e) {
									e.printStackTrace();															
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);								
								}
							}
						}

						
					});
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();		
						}
					});
					
					getUI().addWindow(cEditor);
				
				}else{					
					Notify.Show("Você não Possui Permissão para Cadastrar um Produto", Notify.TYPE_ERROR);
				}
			}
		});

		return btNovo;
	}

	public Button BuildbtEditar() {
		btEditar = new Button("Visualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar"))				
				{
					
					Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
					final Produto produto = ProdutoDAO.find(codProduto);
		
					final ProdutoEditor cEditor = new ProdutoEditor(tb.getItem(tb.getValue()), "Editar Produto", true,false,false,true,codSubModulo);
					cEditor.addListerner(new ProdutoEditor.ProdutoListerner() {
						
						@Override
						public void onClose(ProdutoEvent event) {
							if(event.isConfirm()){
							
								try{
									container.commit();									
									tb.focus();	
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Produto"));
									AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "ALTEROU DE PRODUTO", produto,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									Icms icms = event.getIcms();
									Ipi ipi = event.getIpi();
									Pis pis = event.getPis();
									Cofins cofins = event.getCofins();
									
									EntityItem<Produto> itemProduto = (EntityItem<Produto>)event.getItem();
									
									
									icms.setProduto(itemProduto.getEntity());
									ipi.setProduto(itemProduto.getEntity());
									pis.setProduto(itemProduto.getEntity());
									cofins.setProduto(itemProduto.getEntity());
									
									String cod_loja = itemProduto.getEntity().getCod_pro_loja() != null && !itemProduto.getEntity().getCod_pro_loja().equals("") ? itemProduto.getEntity().getCod_pro_loja() : null;
									
									
									
									if(cod_loja == null && OpusERP4UI.getEmpresa().getId() != 6&& OpusERP4UI.getEmpresa().getId() != 4){
										
										String cod = itemProduto.getEntity().getId().toString();
																			
										
									}
									

									ProdutoDAO.saveProduto(itemProduto.getEntity());
									Notify.Show("Produto Alterado Com Sucesso!", Notify.TYPE_SUCCESS);		
									
								} catch (Exception e) {
									e.printStackTrace();															
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);								
								}
							}
						}
					});
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();							
						}
					});
					
					getUI().addWindow(cEditor);
				}else{					
					Notify.Show("Você não Possui Permissão para Editar um Produto", Notify.TYPE_ERROR);
				}
			}
		});
		btEditar.setEnabled(false);
		btEditar.setDisableOnClick(true); 
		
		btEditar.addShortcutListener(new ShortcutListener("Enter",ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btEditar.click();
				btEditar.setEnabled(false);
				//addExtension(buildRefresher());
			}
		});
					
		return btEditar;
	}
	
	public String createFile(){	
		StringBuilder sb = new StringBuilder();
		String quebra = "\r\n";
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfAno = new SimpleDateFormat("yy");
		
		List<Produto> produtos = ProdutoDAO.listProduto(OpusERP4UI.getEmpresa().getId());
		Integer i = 0;
		for (Produto p: produtos) {
			
				i++;
			
				String fracionar = p.getFracionar() == 1 ? "S" : "N";
				
				sb.append(
						p.getId().toString()+"|"+
					    p.getgTin()+"|"+
					    p.getNome()+"||"+
					    p.getUnidadeProduto().getNome()+"|"+
					    String.format("%.3f", p.getValorVenda()).replace(",", ".")+
					    "|0|"+
					    p.getEcf_cup_filcal().getEcf_emissor_cod()+
					    "|||N|"+ 
					    fracionar +
					    "|S|S|N|"+
					    p.getGrupoId().getId().toString()+
					    "|"+p.getGrupoId().getNome_grupo()+"|||"+
				p.getMarcasId().getId().toString()+"|"+p.getMarcasId().getNome()+"|||0||"+
				p.getNcm()+"|0|"+p.getgTin()+"|||||||||N|"+p.getQtdEstoque());
				
				
				sb.append(quebra);
				
		}

		return sb.toString();
	}
	
	public Button BuildbtExcluir() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{				
					Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
					Produto produto = ProdutoDAO.find(codProduto);
					
					List<MovimentoEntDetalhe> moDet = ProdutoDAO.movEntDet(codProduto);
					List<EcfPreVendaDetalhe> EcfDet = ProdutoDAO.EntDet(codProduto);

				}else{					
					Notify.Show("Você não Possui Permissão para Excluir um Produto", Notify.TYPE_ERROR);
				}
				
			}
		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}
	
	private Component BuildbtVisualizar() {
		btVisualizar = new Button("Alterar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		btVisualizar.setEnabled(false);
		return btVisualizar;
	}
	
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}	
}
