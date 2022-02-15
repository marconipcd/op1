package com.digital.opuserp.view.modulos.pedido.compras;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogProdutoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.pedido.compras.ComprasEditor.ComprasEvent;
import com.digital.opuserp.view.modulos.pedido.compras.ItemEditor.ItemEvent;
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

public class ComprasView extends VerticalLayout {
	
	private static final long serialVersionUID = 1L;

	JPAContainer<MovimentoEntCabecalho> container;
	
	private Table tb;
	private TextField tfBusca;
	
	private Button btNovo;
	private Button btEditar;
	private Button btFinalizar;
	private Button btCancelar;	
	private Button btVisualizar;
	
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	Window window;
	
	public ComprasView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
//			hlButons.setWidth("100%");
			

			hlButons.addComponent(BuildbtNovo());
			hlButons.addComponent(BuildbtEditar());			
			hlButons.addComponent(BuildbtFinalizar());
			hlButons.addComponent(BuildbtVisualizar());			
			hlButons.addComponent(BuildbtCancelar());
			
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");					
					addComponent(BuildCbStatus());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildLbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
			lbLegend.setWidth("285px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
		}
	}
	
	private ComboBox cbStatus;
	private ComboBox BuildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		cbStatus.addItem("TODOS");
		cbStatus.addItem("ABERTO");
		cbStatus.addItem("CANCELADO");
		cbStatus.addItem("FINALIZADO");
		cbStatus.select("ABERTO");
		cbStatus.setImmediate(true);
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {

				addFilter(tfBusca.getValue());	
				
				refresh();
			}
		});
		
		return cbStatus;
	}
	
	
		
	public void refresh_qtd(){
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
		addFilter(tfBusca.getValue());
		btEditar.setEnabled(false);		
		btFinalizar.setEnabled(false);		
		btCancelar.setEnabled(false);
		btVisualizar.setEnabled(false);
		
		refresh_qtd();
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ComprasView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btEditar != null || slEditar != null){
				btEditar.removeShortcutListener(slEditar);			
			}
		}
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btNovo.click();
			}
		};
		return slNovo;
	}
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btEditar.click();
			}
		};
		return slEditar;
	}
	public JPAContainer<MovimentoEntCabecalho> buildContainer(){
		container = JPAContainerFactory.makeBatchable(MovimentoEntCabecalho.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
							
		if(cbStatus != null && cbStatus.getValue() != null){
			if(cbStatus.getValue().toString().equals("ABERTO")){
				container.addContainerFilter(Filters.eq("situacao", "A"));				
			}
			if(cbStatus.getValue().toString().equals("CANCELADO")){
				container.addContainerFilter(Filters.eq("situacao", "C"));
			}
			if(cbStatus.getValue().toString().equals("FINALIZADO")){
				container.addContainerFilter(Filters.eq("situacao", "F"));
			}
		}
						
		container.addNestedContainerProperty("fornecedor.fantasia");
			
		if(tfBusca.getValue() != null && !tfBusca.getValue().isEmpty() && !tfBusca.getValue().equals("")){
			String s = tfBusca.getValue();
			Object[] collums = tb.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			try {
				Integer cod = Integer.parseInt(s);
				
				for(Object c:collums){		 			
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
						filtros.add(new Like(c.toString(), "%"+cod+"%", false));
					}	
				}
				
			} catch (Exception e) {
				
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date = sdf.parse(s);
					
					for(Object c:collums){
						
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
							filtros.add(Filters.eq(c.toString(), date));
						}	
						
						
					}
				}catch(Exception e2){
					
					for(Object c:collums){
						//{"id","P","data_ex","turno","grupo.nome","subgrupo.nome","tipo_subgrupo.nome","cliente.nome_razao","end.cidade","end.bairro","prioridade","tecnico","veiculo_id.cod_veiculo","obs","concentrador","base","plano","material"});
						if(!c.equals("P") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));
						}	
						
					}
				}

			}

			
			container.addContainerFilter(Filters.or(filtros));
		}
		
		container.sort(new Object[]{"id"}, new boolean[]{false});	
		return container;
	}

	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
			
				if (v instanceof Date) {
				      Date dateValue = (Date) v;				        
				      return new SimpleDateFormat("dd/MM/yyyy").format(dateValue);
				        
				}
				
				if (v instanceof Float && colId.toString().equals("valorTotal")) {					
					return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
				}
				
				if(colId.equals("situacao") && tb.getItem(rowId).getItemProperty(colId).getValue() != null){
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue().equals("A")){
						return "ABERTO";
					}
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue().equals("F")){
						return "FECHADO";
					}
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue().equals("C")){
						return "CANCELADO";
					}
					
				}
				
				if(colId.equals("formaPagtoID.nome") && tb.getItem(rowId).getItemProperty(colId).getValue() == null){
					return "NÃO DEFINIDO";
				}
				
				if(colId.equals("id") || colId.equals("codNf")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}
				
								
				return super.formatPropertyValue(rowId, colId, property);
			}
		};		
		
		
		
		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		
		
		
		tb.setVisibleColumns(new Object[] {"id","fornecedor.fantasia","codNf","qtdItens","valorTotal","dataHora","usuario","situacao"});
		
		
		tb.setColumnHeader("id", "Número");
		tb.setColumnHeader("fornecedor.fantasia", "Fornecedor");
		tb.setColumnHeader("codNf", "Nota Fiscal");
		tb.setColumnHeader("qtdItens", "Qtd. Itens");
		tb.setColumnHeader("dataHora", "Data");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("usuario", "Usuário");
		tb.setColumnHeader("valorTotal", "Total R$");
		tb.setColumnHeader("situacao", "Status");
		tb.setColumnHeader("formaPagtoID.nome", "Forma de Pgto.");
	
		tb.setColumnAlignment("valorTotal", Align.RIGHT);
		tb.setColumnCollapsed("usuario", true);
		
		//tb.setConverter("id", null);
		//tb.setConverter("codNf", null);
		
		
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){

					btEditar.setEnabled(false);
					btFinalizar.setEnabled(false);
					btCancelar.setEnabled(false);	
					btVisualizar.setEnabled(true);

					
					if(tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("A")){						
						btFinalizar.setEnabled(true);																						
					}
					
					if(tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("A")){				
						btEditar.setEnabled(true);												
					}					
				
					if(!tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("C")){						
						btCancelar.setEnabled(true);					
					}
					
				}else{

					btCancelar.setEnabled(false);					
					btEditar.setEnabled(false);
					btFinalizar.setEnabled(false);
					btVisualizar.setEnabled(false);
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
				
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			
			public String getStyle(Table source, Object itemId, Object propertyId) {

				 if (propertyId == null){
	                    return "row-header-default"; // Will not actually be visible
				 }else{
	               
	                Item item = source.getItem(itemId);
	                
	                if(item != null && item.getItemProperty("situacao") != null && item.getItemProperty("situacao").getValue() != null){
		                String status = item.getItemProperty("situacao").getValue().toString();
		               
		                if(status.equals("F")){
		                	return "row-header-pedido-fechado";    
		                }else if(status.equals("C")){
		                	return "row-header-pedido-cancelado";    
		                }else if(status.equals("A")){
		                	return "row-header-pedido-aberto";       
		                }	                
	                }else{
	                	 return "row-header-default";
	                }
				 }
				 return "row-header-default";
			}
		});
		
		
		
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					if(tb.getValue() != null){
						tb.select(tb.getValue());
						btEditar.click();						
					}
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
		tfBusca.setInputPrompt("Buscar...");
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}

	
	public void addFilter(String s) {
		container.removeAllContainerFilters();
		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
				
		if(cbStatus != null && cbStatus.getValue() != null){
			if(cbStatus.getValue().toString().equals("ABERTO")){
				container.addContainerFilter(Filters.eq("situacao", "A"));
			}
			if(cbStatus.getValue().toString().equals("CANCELADO")){
				container.addContainerFilter(Filters.eq("situacao", "C"));
			}
			if(cbStatus.getValue().toString().equals("FINALIZADO")){
				container.addContainerFilter(Filters.eq("situacao", "F"));
			}
		}
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
			for(Object c:collums){		 			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}	
			}
			
		} catch (Exception e) {
			
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date date = sdf.parse(s);
				
				for(Object c:collums){
					
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
						filtros.add(Filters.eq(c.toString(), date));
					}	
					
					
				}
			}catch(Exception e2){
				
				for(Object c:collums){

					if(!c.equals("P") && !c.equals("veiculo_id.cod_veiculo") 	&& !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
					
				}
			}

		}

		
		container.addContainerFilter(Filters.or(filtros));
		container.sort(new Object[]{"id"}, new boolean[]{false});	
		container.applyFilters();
		
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
					final BeanItem<MovimentoEntCabecalho> item = new BeanItem<MovimentoEntCabecalho>(new MovimentoEntCabecalho());
					
					final ComprasEditor compraEditor = new ComprasEditor(item, "Nova Entrada", true, false);
					compraEditor.addListerner(new ComprasEditor.ComprasListerner() {
						
						
						public void onClose(ComprasEvent event) {
							
							System.out.println("Itens: "+event.getItens().size());
							final MovimentoEntCabecalho compra = item.getBean();
							compra.setSituacao("F");
							compra.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
							compra.setQtdItens(new Float(event.getItens().size()));
							//compra.setValorTotal(new Float(1));
								
							CompraDAO.save(compra);
							
							for (MovimentoEntDetalhe det : event.getItens()) {
								det.setMovimentoEntCabecalhoId(compra.getId());
								
								Produto p = ProdutoDAO.find(det.getProdutoId());
								
								//-----Logg
								LogProdutoDAO.registraLog(new LogProduto(null,p, "NOVO PEDIDO COMPRA", p.getQtdEstoque() != null ? p.getQtdEstoque() : 0 ,p.getQtdEstoque() != null ? p.getQtdEstoque() + det.getQuantidade(): 0+det.getQuantidade(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
								//-----Logg
								
								p.setQtdEstoqueDeposito(p.getQtdEstoqueDeposito() != null ? p.getQtdEstoqueDeposito() + det.getQuantidade(): 0+det.getQuantidade());
								p.setTaxaIpi(det.getIpi());
								
								if(det.getDiferenca_aliquota() > 0){
									Float nAliquota = new Float(OpusERP4UI.getEmpresa().getAliquota_interna() - det.getDiferenca_aliquota());									
									p.setDiferenca_aliquota(nAliquota);
								}
								
								
								p.setUltima_compra(DataUtil.formatDateBra(new Date()));
								p.setData_validade(compra.getData_validade());
								
								EntityManager em = ConnUtil.getEntity();
								em.getTransaction().begin();
								em.merge(p);
								em.getTransaction().commit();
								
								CompraDAO.saveItem(det);
							}
							
							compraEditor.close();									
							refresh();
							

						}
					});
					
					compraEditor.addCloseListener(new Window.CloseListener() {
						
						
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
					
					getUI().addWindow(compraEditor);		
				}else{
					Notify.Show("Você não Possui Permissão para Abrir uma Compra",Notify.TYPE_ERROR);
				}
			}
		});

		return btNovo;
	}

		
	public Button BuildbtEditar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Editar"))				
				{									
					
					if(tb.getItem(tb.getValue()).getItemProperty("usuario").getValue().toString().equals(OpusERP4UI.getUsuarioLogadoUI().getUsername())){
						final ComprasEditor compraEditor  = new ComprasEditor(tb.getItem(tb.getValue()), "Editar", true,false);
						
						compraEditor.addListerner(new ComprasEditor.ComprasListerner() {
							
							@Override
							public void onClose(ComprasEvent event) {
								if(event.isConfirm()){
									try{		
										EntityItem<MovimentoEntCabecalho> entityItem = (EntityItem<MovimentoEntCabecalho>) event.getItem(); 
										final MovimentoEntCabecalho compra = entityItem.getEntity();
										compra.setSituacao("A");
										compra.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
										
										CompraDAO.save(compra);							
										
										compraEditor.close();									
										refresh();
										
										
										final ItemEditor itemEditor =new ItemEditor(compra, "Itens", true);
										itemEditor.addListerner(new ItemEditor.ItemListerner() {
											
											@Override
											public void onClose(ItemEvent event) {
												
												if(event.isConfirm()){
													
													try{
														Float total = new Float(0);
														for (MovimentoEntDetalhe item : event.getItens()) {																													
															total = total+item.getValorCusto()*item.getQuantidade();											
														}														
														
														compra.setSituacao("F");														
														compra.setQtdItens(new Float(event.getItens().size()));
														compra.setValorTotal(total);
														CompraDAO.save(compra);														
														
														itemEditor.close();
														refresh();													
														
													}catch(Exception e){
														e.printStackTrace();
														Notify.Show("ERRo: "+e.getMessage(), Notify.TYPE_ERROR);
													}
																									
												
												}else{
													try{
														Float total = new Float(0);
														for (MovimentoEntDetalhe item : event.getItens()) {
																													
															total = total+item.getValorCusto()*item.getQuantidade();													
														}
														
														//pedido.setSituacao("A");
														compra.setValorTotal(total);
														compra.setQtdItens(new Float(event.getItens().size()));
														CompraDAO.save(compra);
														
														itemEditor.close();
														refresh();
														
													}catch(Exception e){
														e.printStackTrace();
														Notify.Show("ERRo: "+e.getMessage(), Notify.TYPE_ERROR);
													}
													
													refresh();												
												}
											}
										});
										
										itemEditor.addCloseListener(new Window.CloseListener() {
											
											@Override
											public void windowClose(CloseEvent e) {
												refresh();
												tb.focus();
												
											}
										});
										
										getUI().addWindow(itemEditor);
										
									}catch(Exception e){
										e.printStackTrace();
										Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
									}
								}else{
									try{
										EntityItem<MovimentoEntCabecalho> entityItem = (EntityItem<MovimentoEntCabecalho>) event.getItem(); 
										final MovimentoEntCabecalho compra = entityItem.getEntity();
										compra.setSituacao("A");
										compra.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
																														
										CompraDAO.save(compra);							
										
										compraEditor.close();									
										refresh();
									}catch(Exception e){
										e.printStackTrace();	
										Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
									}
								}
							}
						});
						
						
						
						
						
						getUI().addWindow(compraEditor);				
					}else{
						Notify.Show("Você não Pode Editar uma Compra criada Por Outro Usuário",Notify.TYPE_ERROR);
					}
					
				}else{
					Notify.Show("Você não Possui Permissão para Editar esta Compra",Notify.TYPE_ERROR);
				}
			}
		});
		
		btEditar.setEnabled(false);		
		return btEditar;
		
	}
	
	
	public Button BuildbtFinalizar(){
		btFinalizar = new Button("Finalizar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Finalizar"))				
				{
					
					if(tb.getItem(tb.getValue()).getItemProperty("usuario").getValue().toString().equals(OpusERP4UI.getUsuarioLogadoUI().getUsername())){	
						final MovimentoEntCabecalho pedido = CompraDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
					
						
						final ItemEditor itemEditor = new  ItemEditor(pedido, "Itens", true);
						itemEditor.addListerner(new ItemEditor.ItemListerner() {
							
							@Override
							public void onClose(ItemEvent event) {
								if(event.isConfirm()){
									
									try{
										Float total = new Float(0);
										for (MovimentoEntDetalhe item : event.getItens()) {
																									
											total = total+item.getValorCusto()*item.getQuantidade();													
										}
										
										pedido.setSituacao("F");
										pedido.setValorTotal(total);
										pedido.setQtdItens(new Float(event.getItens().size()));
										CompraDAO.save(pedido);
																				
										itemEditor.close();
										refresh();
										
									}catch(Exception e){
										e.printStackTrace();
										Notify.Show("ERRo: "+e.getMessage(), Notify.TYPE_ERROR);
									}
								
								
									
									
								
								}else{
									try{
										Float total = new Float(0);
										for (MovimentoEntDetalhe item : event.getItens()) {
																									
											total = total+item.getValorCusto()*item.getQuantidade();													
										}
										
										pedido.setSituacao("F");
										pedido.setValorTotal(total);
										pedido.setQtdItens(new Float(event.getItens().size()));
										CompraDAO.save(pedido);
										
										
										itemEditor.close();
										refresh();
										
									}catch(Exception e){
										e.printStackTrace();
										Notify.Show("ERRo: "+e.getMessage(), Notify.TYPE_ERROR);
									}
								
									
									refresh();												
								}
							}
						});
									
									getUI().addWindow(itemEditor);											
										
					}else{
						Notify.Show("Você não Pode Finalizar uma Compra criado Por Outro Usuário",Notify.TYPE_ERROR);
					}
				}else{				
					Notify.Show("Você não Possui Permissão para Finalizar uma Compra",Notify.TYPE_ERROR);				
				}
				
			}
		});
		
		btFinalizar.setEnabled(false);
		return btFinalizar;
	}
	
	public Button BuildbtVisualizar(){
		btVisualizar = new Button("Visualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
					ComprasEditor comprasEditor = new ComprasEditor(tb.getItem(tb.getValue()), "Visualizar", true, true);
					getUI().addWindow(comprasEditor);
				
			}
		});
		
		btVisualizar.setEnabled(false);
		return btVisualizar;
	}
	

	
	
	public Button BuildbtCancelar(){
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cancelar"))				
				{

					GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja realmente Cancelar este Pedido ?", true, true);
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							
							if(event.isConfirm()){
								try {
									MovimentoEntCabecalho compra = CompraDAO.find2((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
									
										boolean check = CompraDAO.CancelarPedido(compra);
										if(check){
											refresh();
											Notify.Show("Compra Cancelada com Sucesso",Notify.TYPE_SUCCESS);
										}
									
								} catch (Exception e) {
									e.printStackTrace();
									Notify.Show("Não foi Possivel Cancelar a Campra!", Notify.TYPE_ERROR);
								}
							}
						}
					});
					
					gd.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();							
						}
					});
					
					getUI().addWindow(gd);
								
				}else{				
					Notify.Show("Você não Possui Permissão para Cancelar iuma Compra",Notify.TYPE_ERROR);				
				}
				
			}
		});
		
		btCancelar.setEnabled(false);
		return btCancelar;
	}

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}

