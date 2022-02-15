package com.digital.opuserp.view.modulos.estoque.serial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesSerialDAO;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.SeriaisCompraDAO;
import com.digital.opuserp.dao.SeriaisPedidoDAO;
import com.digital.opuserp.dao.SerialDAO;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.SeriaisCompra;
import com.digital.opuserp.domain.SeriaisPedido;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaEditor;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaEditor.CategoriaEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaView;
import com.digital.opuserp.view.modulos.estoque.serial.SerialEditor.SerialEvent;
import com.digital.opuserp.view.modulos.estoque.serial.SerialEditorDevolucao.SerialDevolucaoEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class SerialView extends VerticalLayout implements GenericView  {

	JPAContainer<SerialProduto> container;
	
	Table tb;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btVisualizar;
	Button btExcluir;
	Button btLog;
	//Button btDevolucao;
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private ComboBox cbStatus;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	public SerialView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtTransferencia());
			hlButons.addComponent(BuildbtVisualizar());
			hlButons.addComponent(BuildbtAceitar());
			//hlButons.addComponent(BuildbtDevolucao());
			hlButons.addComponent(BuildbtLog());
			hlButons.addComponent(BuildbtCancelar());
			
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
		btLog.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	private ComboBox buildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		cbStatus.addItem("TODOS");
		cbStatus.addItem("ATIVO");
		cbStatus.addItem("VENDIDO");
		cbStatus.addItem("COMODATO");
		cbStatus.addItem("AVARIADO");
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
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof CategoriaView)
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
			}
		};
		return slEditar;
	}
	public JPAContainer<SerialProduto> buildContainer(){
		container = JPAContainerFactory.makeBatchable(SerialProduto.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addNestedContainerProperty("produto.id");
		container.addNestedContainerProperty("produto.nome");
		container.addNestedContainerProperty("produto.qtdEstoque");
		container.addNestedContainerProperty("produto.qtdEstoqueDeposito");
		container.addContainerFilter(Filters.eq("produto.empresaId", OpusERP4UI.getEmpresa()));
		container.sort(new String[] {"produto.nome"}, new boolean[]{true});
		
			if(cbStatus != null && cbStatus.getValue() != null && !cbStatus.getValue().toString().equals("TODOS")){
					container.addContainerFilter(Filters.eq("status", cbStatus.getValue()));
			}
	
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
					
		tb.setVisibleColumns(new Object[] {"id","produto.id","produto.nome","produto.qtdEstoque","produto.qtdEstoqueDeposito","serial","status","data_cadastro","data_alteracao"});

		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("produto.id", "Código Produto");
		tb.setColumnHeader("produto.nome", "Produto");
		tb.setColumnHeader("produto.qtdEstoque", "Saldo Loja");
		tb.setColumnHeader("produto.qtdEstoqueDeposito", "Saldo Deposito");
		tb.setColumnHeader("serial", "Serial");	
		tb.setColumnHeader("status", "Status");	
		tb.setColumnHeader("data_cadastro", "Data Cadastro");	
		tb.setColumnHeader("data_alteracao", "Data Alteração");	
		
		tb.setColumnAlignment("produto.qtdEstoque", Align.RIGHT);
		tb.setColumnAlignment("produto.qtdEstoqueDeposito", Align.RIGHT);
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditar.setEnabled(true);
					btVisualizar.setEnabled(true);
					btExcluir.setEnabled(true);
					btLog.setEnabled(true);
				//	btDevolucao.setEnabled(true); 
				}else{
					btEditar.setEnabled(false);
					btVisualizar.setEnabled(false);
					btExcluir.setEnabled(false);
					btLog.setEnabled(false);
				//	btDevolucao.setEnabled(false); 
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
			
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()   && tb.getValue() != null){
					CategoriaEditor cEditor = new CategoriaEditor(tb.getItem(tb.getValue()), "Editar Categoria", true);
					cEditor.addListerner(new CategoriaEditor.CategoriaListerner() {
						
						@Override
						public void onClose(CategoriaEvent event) {
							if(event.isConfirm()){
								container.commit();							
							}
						}
					});			
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
					
					getUI().addWindow(cEditor);
				  }
				
			}
		});
		

		
		return tb;
	}
	

	@Override
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}

	@Override
	public void addFilter(String s) {
		container.removeAllContainerFilters();
		
		container.addContainerFilter(Filters.eq("produto.empresaId", OpusERP4UI.getEmpresa()));
		
		if(cbStatus != null && cbStatus.getValue() != null && !cbStatus.getValue().toString().equals("TODOS")){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue()));
		}
		
		if(s != null && !s.equals("") && !s.isEmpty()){
			
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.valueOf(s);
			DateTime date = null ;
			
				for(Object c:collums){		 			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}		
			}
			
		} catch (Exception e) {
			
			
			try {				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date date = sdf.parse(s);

					for(Object c:collums){		 
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
							filtros.add(Filters.eq(c.toString(), date));
						}			
					}	
				
			} catch (Exception e2) {
				
				try {
					Float vl = Real.formatStringToDBFloat(s);
									
					for(Object c:collums){		 
						
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Float.class){					   	
							filtros.add(Filters.eq(c.toString(), vl));
						}			
					}	
					
				} catch (Exception e3) {
					
					for(Object c:collums){		 
						
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));
						}	
					}
				}
			}
		}		
		container.addContainerFilter(Filters.or(filtros));
	  }
		container.applyFilters();	
		container.sort(new String[] {"produto.nome"}, new boolean[]{true});
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
				final BeanItem<SerialProduto> newItem = new BeanItem<SerialProduto>(new SerialProduto());
								
				SerialEditor cEditor = new SerialEditor(newItem, "Cadastrar Serial", true);
				cEditor.addListerner(new SerialEditor.SerialListerner() {
					
					@Override
					public void onClose(SerialEvent event) {
						if(event.isConfirm()){
							
							try {
								SerialProduto cAdd = newItem.getBean();
								cAdd.setStatus("ATIVO");
								cAdd.setData_cadastro(new Date());
								cAdd.setData_alteracao(new Date());
								
								container.addEntity(cAdd);	
								container.commit();
								tb.focus();

								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Serial"));
								AlteracoesSerialDAO.save(new AlteracoesSerial(null, "CADASTROU", cAdd,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
							} catch (Exception e) {
																								
								Notification.show("ERRO: "+e.getMessage());								
							}
							
						}else{
//							LogDAO.addLog(new Log(null, "Ação de Cadastro de Cliente Cancelada!", OpusERP4_ServerUI.getUsuarioLogadoUI().getUsername(), null));
						}
					}
				});
				
				cEditor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tfBusca.focus();
//						refresh();
					}
				});
				
				getUI().addWindow(cEditor);
				
				}else{
					Notify.Show("Você não Possui Permissão para Cadastrar um Serial", Notify.TYPE_ERROR);
				}
			}
		});

		return btNovo;
	}
	
//	public Button BuildbtDevolucao() {
//		btDevolucao = new Button("Devolução", new Button.ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//				
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Devolucao"))				
//				{
//																				
//						SerialEditorDevolucao cEditor = new SerialEditorDevolucao(tb.getItem(tb.getValue()), "Devolução Serial", true);
//						cEditor.addListerner(new SerialEditorDevolucao.SerialDevolucaoListerner() {
//							
//							@Override
//							public void onClose(SerialDevolucaoEvent event) {
//								if(event.isConfirm()){
//									
//									try {
//										
//									} catch (Exception e) {																										
//										Notification.show("ERRO: "+e.getMessage());								
//									}
//									
//								}
//							}
//						});
//						
//						cEditor.addCloseListener(new Window.CloseListener() {
//							
//							@Override
//							public void windowClose(CloseEvent e) {
//								tfBusca.focus();
//							}
//						});
//						
//						getUI().addWindow(cEditor);
//				
//				}else{
//					Notify.Show("Você não Possui Permissão para Devolver um Serial", Notify.TYPE_ERROR);
//				}
//			}
//		});
//
//		btDevolucao.setEnabled(false); 
//		return btDevolucao;
//	}

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
				{
				
						if(tb.getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status").getValue().toString().equals("ATIVO")){
							
								Integer codSerial = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
								final SerialProduto serial = SerialDAO.find(codSerial);
								
								SerialEditor cEditor = new SerialEditor(tb.getItem(tb.getValue()), "Editar Serial", true);
								cEditor.addListerner(new SerialEditor.SerialListerner() {
									
									@Override
									public void onClose(SerialEvent event) {
										if(event.isConfirm()){
											container.commit();			
											
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Serial"));
											AlteracoesSerialDAO.save(new AlteracoesSerial(null, "EDITOU SERIAL "+serial.getSerial(), serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
											
										}
									}
								});
								
								cEditor.addCloseListener(new Window.CloseListener() {
									
									@Override
									public void windowClose(CloseEvent e) {
										tb.focus();
									}
								});
								
								getUI().addWindow(cEditor);
								
						}else{
							Notify.Show("Só é possível editar Seriais ativos!", Notify.TYPE_ERROR);
						}
				}else{
					Notify.Show("Você não Possui Permissão para Editar um Serial", Notify.TYPE_ERROR);
				}
			}
		});
		btEditar.setEnabled(false);
					
		return btEditar;
	}

	public Button BuildbtVisualizar() {
		btVisualizar = new Button("Visualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {			
			
				Integer codSerial = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
				final SerialProduto serial = SerialDAO.find(codSerial);
					
				SerialEditorReadOnly cEditor = new SerialEditorReadOnly(tb.getItem(tb.getValue()), "Visualizar Serial", true);			
				getUI().addWindow(cEditor);				
			}
		});
		btVisualizar.setEnabled(false);
					
		return btVisualizar;
	}
	
	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
				
					final Integer categoria = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());
					
					SeriaisCompra scp = SeriaisCompraDAO.find((categoria));
					SeriaisPedido spd = SeriaisPedidoDAO.find((categoria));
					
					if(scp!=null){						
						MovimentoEntDetalhe md = CompraDAO.findByCod(scp.getItemPedido().getId());
						if(md!=null){
							MovimentoEntCabecalho compra_selecionado = CompraDAO.find(md.getMovimentoEntCabecalhoId());					
							if(compra_selecionado != null){
								Notify.Show("Serial em uso não pode ser excluido!", Notify.TYPE_ERROR);		
								}
					}else if(spd!=null){
						EcfPreVendaDetalhe ecfdd = PedidoDAO.findByCod(spd.getItemPedido().getId());
						if(ecfdd!=null){
							EcfPreVendaCabecalho ecfCab = PedidoDAO.find(ecfdd.getEcfPreVendaCabecalhoId());					
							if(ecfCab != null){
								Notify.Show("Serial em uso não pode ser excluido!", Notify.TYPE_ERROR);		
								}
							}
						}					
					}else{					
					
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Serial Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								 
								AlteracoesSerialDAO.remove(categoria);
								
								container.removeItem(tb.getValue());
								container.commit();
								Notify.Show("Serial Excluída com Sucesso!", Notify.TYPE_SUCCESS);												
								tb.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma Categoria"));
							}							
						}
					});
					
					gDialog.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
				
				getUI().addWindow(gDialog);
					}
				}else{					
					Notify.Show("Você não Possui Permissão para Excluir um Serial", Notify.TYPE_ERROR);
				}
			}
		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}

	private Component BuildbtLog() {
		btLog = new Button("Log", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
	
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Log"))				
				{
					
				Integer codSerial = null;
				if(tb.getItem(tb.getValue())!=null && tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()!=null){	
					codSerial = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
				}
				
//				if(tb.getValue() != null && tb.getItem(tb.getValue()) != null){
					
					LogAlteracoesSerial logAlteracoes = new LogAlteracoesSerial(true, true,codSerial);				
					getUI().addWindow(logAlteracoes);
//				}
				
				}else{					
					Notify.Show("Você não Possui Permissão para visualisar Histórico do Serial", Notify.TYPE_ERROR);
				}
			}
		});
		btLog.setEnabled(false);
		return btLog;
	}
	
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}
