package com.digital.opuserp.view.modulos.acesso.central_assinantes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.ListaSitesFiltroCDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ListaSitesFiltroC;
import com.digital.opuserp.domain.ListaSitesFiltroD;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.central_assinantes.DefinirCredenciaisEditor.DefinirCredenciaisEvent;
import com.digital.opuserp.view.modulos.acesso.central_assinantes.ListasEditor.ListaEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
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
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class CentralAssinantesView extends VerticalLayout {
	
	JPAContainer<Cliente> container;
	
	Table tb;
	TextField tfBusca;
	Button btDefinirCredenciais;
	
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	
	
	public CentralAssinantesView(boolean act){
		
		if(act){		
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			TabSheet ts_principal = new TabSheet();
			 ts_principal.setSizeFull();
			 
			 TabSheet.Tab tCredenciais = ts_principal.addTab(buildLayoutCredenciais(), "Credenciais");
			 TabSheet.Tab tAutocensura = ts_principal.addTab(buildLayoutAutocensura(), "Autocensura");
			 
			 			 
			 addComponent(ts_principal);
			
			
		}
	}
	public void buildShortcurEvents(Component c){
		
		if(c instanceof CentralAssinantesView)
		{
//			btNovo.addShortcutListener(buildShortCutNovo());
//			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
//			if(btNovo != null || slNovo != null){				
//				btNovo.removeShortcutListener(slNovo);
//			}
//			
//			if(btEditar != null || slEditar != null){
//				btEditar.removeShortcutListener(slEditar);			
//			}
		}
	}
	
	private JPAContainer<ListaSitesFiltroC> containerLista;
	private Button btCadastrarLista;
	private Button btEditarLista;
	private Button btExcluirLista;
	private Button btPalavras;	
	private TextField txtBusca;
	private Table tbListas;
	private Label lbRegistroslista;
	
	private VerticalLayout buildLayoutAutocensura(){
		VerticalLayout vl1 = new VerticalLayout();
		vl1.setSizeFull();
		
		HorizontalLayout hlButtons = new HorizontalLayout();
		hlButtons.addComponent(buildBtCadastrarLista());
		hlButtons.addComponent(buildBtEditarLista());
		hlButtons.addComponent(buildBtExcluirLista());
		hlButtons.addComponent(buildBtPalavras());
		
		vl1.addComponent(hlButtons);
		vl1.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
		vl1.addComponent(buildTxtBusca());
		vl1.addComponent(buildTbLista());
		vl1.addComponent(buildLbRegistrosListas());
		vl1.setExpandRatio(tbListas, 1);
						
		
		return vl1;
	}
	private Label buildLbRegistrosListas(){
		lbRegistroslista =new Label(String.valueOf(containerLista.size()) + " Registros Encontrados");
		return lbRegistroslista;
	}
	
	private JPAContainer<ListaSitesFiltroC> buildContainerLista(){
		containerLista = JPAContainerFactory.makeBatchable(ListaSitesFiltroC.class, ConnUtil.getEntity());
		containerLista.setAutoCommit(false); 
		return containerLista;
	}
	
	private Table buildTbLista(){
		tbListas = new Table(null, buildContainerLista());
		tbListas.setWidth("100%");
		tbListas.setSelectable(true); 
		tbListas.setVisibleColumns(new Object[]{"id", "nome","data_atualizacao","status"});
		tbListas.setColumnHeader("id", "Cod.");
		tbListas.setColumnHeader("nome", "Nome");
		tbListas.setColumnHeader("data_atualizacao", "Ultima Atualização");
		tbListas.setColumnHeader("status", "Status");
		
		tbListas.setColumnExpandRatio("nome", 1);
		tbListas.setImmediate(true);
		tbListas.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditarLista.setEnabled(true);
					btExcluirLista.setEnabled(true);
					btPalavras.setEnabled(true);
				}else{
					btEditarLista.setEnabled(false);
					btPalavras.setEnabled(false);
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
		return tbListas;		
	}
	
	private TextField buildTxtBusca(){
		txtBusca = new TextField();
		txtBusca.setWidth("100%");
		txtBusca.setInputPrompt("buscar...");
		txtBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		txtBusca.setImmediate(true);
//		txtBusca.focus();
		txtBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				if(event.getText() != null){
					addFilter(event.getText());
				}
			}
		});
		
		return txtBusca;
	}
	
	public void addFilterListar(String s) {
		containerLista.removeAllContainerFilters();
		containerLista.getEntityProvider().setQueryModifierDelegate(null);
		containerLista.setApplyFiltersImmediately(false);
//		containerLista.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));		
		containerLista.sort(new String[] {"id"}, new boolean[]{false});
		
		Object[] collums = tbListas.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.valueOf(s);
			
				for(Object c:collums){		 			
				if(!tbListas.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				if(!tbListas.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
			
		} catch (Exception e) {
			
			
			for(Object c:collums){		 
				
				if(!tbListas.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}			
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));		
		container.applyFilters();
		container.sort(new String[] {"id"}, new boolean[]{false});
		hlFloat.replaceComponent(lbRegistroslista, buildLbRegistrosListas());
	}
	private Button buildBtCadastrarLista(){
		btCadastrarLista = new Button("Cadastrar Lista", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar Lista Autocensura"))				
				{
				
				final BeanItem<ListaSitesFiltroC> item = new BeanItem<ListaSitesFiltroC>(new ListaSitesFiltroC());				
				ListasEditor listaEditor = new ListasEditor(item, "Cadastrar Nova Lista", true);
				listaEditor.addListerner(new ListasEditor.ListaListerner() {
					
					@Override
					public void onClose(ListaEvent event) {
						if(event.isConfirm()){
							try {
								ListaSitesFiltroC cAdd = item.getBean();								
//								cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								containerLista.addEntity(cAdd);	
								containerLista.commit();
								tbListas.focus();

								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou uma Lista de Autocensura"));
							} catch (Exception e) {
																								
								Notification.show("ERRO: "+e.getMessage());								
							}
						}else{
							containerLista.discard();
						}
					}
				});
				
				getUI().addWindow(listaEditor);
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar uma Lista de Autocensura", Type.ERROR_MESSAGE);
				}
				
			}
		});
		
		return btCadastrarLista;
	}
	
	private Button buildBtEditarLista(){
		btEditarLista = new Button("Editar Lista", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Editar Lista Autocensura"))				
				{
				
				ListasEditor listaEditor = new ListasEditor(tbListas.getItem(tbListas.getValue()), "Cadastrar Nova Lista", true);
				listaEditor.addListerner(new ListasEditor.ListaListerner() {
					
					@Override
					public void onClose(ListaEvent event) {
						if(event.isConfirm()){
							try {
								
//								cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								tbListas.commit();	
								containerLista.commit();
								tbListas.focus();

								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Alterou uma Lista de Autocensura"));
							} catch (Exception e) {
								containerLista.discard();
								tbListas.discard();
								Notification.show("ERRO: "+e.getMessage());								
							}
						}else{
							containerLista.discard();
							tbListas.discard();
						}
					}
				});
				
				getUI().addWindow(listaEditor);
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Editar uma Lista de Autocensura", Type.ERROR_MESSAGE);
				}
			}
		});
		
		return btEditarLista;
	}
	
	private Button buildBtExcluirLista(){
		btExcluirLista = new Button("Excluir Lista", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir Lista Autocensura"))				
				{
				
				GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a lista Selecionada?", true, true);
				gDialog.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						if(event.isConfirm()){
							containerLista.removeItem(tbListas.getValue());
							containerLista.commit();
							Notification.show("Lista Excluída com Sucesso!");
							tb.focus();
							
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma Lista"));
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
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Excluir uma Lista de Autocensura", Type.ERROR_MESSAGE);
				}
			}
		});
		
		return btExcluirLista;
	}
	Object itemIdTb= null;
	private Button buildBtPalavras(){
		btPalavras = new Button("Palavras", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Palavra Autocensura"))				
				{
				
				final EntityItem<ListaSitesFiltroC> itemlista = (EntityItem<ListaSitesFiltroC>)tbListas.getItem(tbListas.getValue());
				final Window winRelatorios = new Window("Palavras da Lista "+itemlista.getEntity().getNome());
				winRelatorios.setWidth("850px");
				winRelatorios.setHeight("373px");
				winRelatorios.center();
				winRelatorios.setModal(true);
				winRelatorios.setResizable(false);
				winRelatorios.setClosable(false);
				
				winRelatorios.setContent(new VerticalLayout(){
					{
						setSizeFull();
						setMargin(true);
						
						final JPAContainer<ListaSitesFiltroD>containerPalavras = JPAContainerFactory.make(ListaSitesFiltroD.class, ConnUtil.getEntity());
						EntityItem<ListaSitesFiltroD> item =(EntityItem<ListaSitesFiltroD>) tbListas.getItem(tbListas.getValue());
						containerPalavras.addContainerFilter(Filters.eq("lista_sites_filtro_c", item.getEntity()));
						
						
						containerPalavras.sort(new Object[]{"data_cadastro"}, new boolean[]{false});

						final Table tbPalavras = new Table(null, containerPalavras);
						
						final TextField txtSite = new TextField("Site/Palavra");
						txtSite.setWidth("100%");
						final Button btSalvar = new Button("Salvar", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								if(itemIdTb != null){
									tbPalavras.getItem(itemIdTb).getItemProperty("site").setValue(txtSite.getValue());
									tbPalavras.commit();
									containerPalavras.commit();
									
									ListaSitesFiltroC lista = (ListaSitesFiltroC)tbPalavras.getItem(itemIdTb).getItemProperty("lista_sites_filtro_c").getValue();
									lista.setData_atualizacao(new Date());
									
									ListaSitesFiltroCDAO.save(lista);	
									
									txtSite.setValue("");
									containerPalavras.refresh();
									tbPalavras.select(null); 
									itemIdTb = null;
								}else{
									ListaSitesFiltroD palavra = new ListaSitesFiltroD(null, itemlista.getEntity(), txtSite.getValue(), new Date());
									containerPalavras.addEntity(palavra);
									containerPalavras.commit();
									containerPalavras.refresh();
									tbPalavras.select(null);
									txtSite.setValue("");
									
									
									itemlista.getEntity().setData_atualizacao(new Date());
									ListaSitesFiltroCDAO.save(itemlista.getEntity());									
									
									itemIdTb = null;
								}
							}
						});
						btSalvar.addStyleName("default");
						btSalvar.addStyleName(Reindeer.BUTTON_SMALL);
						
						final Button btNovo = new Button("Novo", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								txtSite.setValue("");
								tbPalavras.select(null); 
								itemIdTb = null;
								txtSite.focus();
							}
						});
						btNovo.addStyleName(Reindeer.BUTTON_SMALL);
						
						HorizontalLayout hl = new HorizontalLayout(){
							{
								FormLayout frmLSite = new FormLayout(){
									{
										setMargin(false);
										setSpacing(false); 
										addComponent(txtSite);
									}
								};
								
								addComponent(frmLSite);
								addComponent(btSalvar);
								addComponent(btNovo);
								setComponentAlignment(frmLSite, Alignment.BOTTOM_LEFT);
								setComponentAlignment(btNovo, Alignment.BOTTOM_RIGHT);
								setComponentAlignment(btSalvar, Alignment.BOTTOM_RIGHT);
								setExpandRatio(frmLSite, 1);
							}											
						};
						hl.setWidth("100%");
						addComponent(hl); 
						
						VerticalLayout vlDiv = new VerticalLayout();
						vlDiv.setMargin(true); 
						vlDiv.setWidth("100%");
						vlDiv.addComponent(new Label("<hr/>", ContentMode.HTML));
						
						addComponent(vlDiv);
						
						TextField tfBusca = new TextField();
						tfBusca.setInputPrompt("Buscar...");
						tfBusca.setWidth("100%");
						tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
						tfBusca.focus();
						tfBusca.addListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								addFilter(event.getText());
							}
						});
						
						addComponent(tfBusca);
						

						//Busca Relatorios
						

						
						
						
						
						
						
						tbPalavras.setWidth("100%");
						tbPalavras.setHeight("230px");
						tbPalavras.setSelectable(true);          
						tbPalavras.setVisibleColumns(new Object[]{"site","data_cadastro"});

						tbPalavras.setColumnHeader("site", "Palavra/Site");
						tbPalavras.setColumnHeader("data_cadastro", "Cadastro");
						
						tbPalavras.setColumnExpandRatio("site", 1);					
						
						tbPalavras.setImmediate(true);
						tbPalavras.addItemClickListener(new ItemClickEvent.ItemClickListener() {
							
							@Override
							public void itemClick(ItemClickEvent event) {
								if(event.isDoubleClick() && event.getItem() != null){
									EntityItem<ListaSitesFiltroD> item =(EntityItem<ListaSitesFiltroD>) event.getItem();
									txtSite.setValue(item.getEntity().getSite());
									txtSite.focus();
									itemIdTb = event.getItemId();
								}
							}
						});
						
						
						
						
						tbPalavras.addGeneratedColumn("x", new Table.ColumnGenerator() {
							
							@Override
							public Object generateCell(final Table source, final Object itemId, Object columnId) {
								
								
								
								Button btDeletar = new Button(null, new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Excluir este Site?", true,true);
										gd.addListerner(new GenericDialog.DialogListerner() {
											
											@Override
											public void onClose(DialogEvent event) {
												if(event.isConfirm()){												
													
														containerPalavras.removeItem(itemId);
														containerPalavras.commit();
														Notify.Show("Site Excluído com Sucesso!", Notify.TYPE_SUCCESS);																														
												}
											}
										});
										
										getUI().addWindow(gd);
									}
								});
								btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
								btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
								btDeletar.setDescription("Deletar Relatório");
								
								return btDeletar;
							}
						});
						
						tbPalavras.setColumnWidth("x", 20);
								
						addComponent(tbPalavras);
						setExpandRatio(tbPalavras, 2);
						
						HorizontalLayout hlButtons = new HorizontalLayout();
						hlButtons.setSpacing(true);
						hlButtons.setMargin(true);
						hlButtons.setStyleName("hl_buttons_bottom");
						hlButtons.addComponent(new Button("Cancelar", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
									winRelatorios.close();				
							}
						}));
						
						
						
						addComponent(hlButtons);
						setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
						
						
						ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
							
							@Override
							public void handleAction(Object sender, Object target) {
								winRelatorios.close();
							}
						};
						hlButtons.addShortcutListener(slbtCancelar);
					}
				});
				
				getUI().addWindow(winRelatorios);
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Adicionar uma Palavra de Autocensura", Type.ERROR_MESSAGE);
				}
			}
		});
		
		return btPalavras;
	}
	
	
	private VerticalLayout buildLayoutCredenciais(){
		
		VerticalLayout vl1 = new VerticalLayout();
		vl1.setSizeFull();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(BuildbtDefinirCredenciais());
				
		vl1.addComponent(hlButons);
		vl1.setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		vl1.addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(buildTfbusca());				
			}
		});
		
		vl1.addComponent(buildTbGeneric());
		
		hlFloat = new HorizontalLayout();
		hlFloat.setWidth("100%");
		hlFloat.addComponent(buildlbRegistros());		
		hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			
		vl1.addComponent(hlFloat);
		
		vl1.setExpandRatio(tb, 1);
		
		return vl1;
	}
	
	
	
	//public void refresh(){
	//	replaceComponent(tb, buildTbGeneric());
	//	setExpandRatio(tb, 1);		
	//}
	
	
	
	public Label buildlbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	
	
	public JPAContainer<Cliente> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Cliente.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		container.sort(new Object[]{"nome_razao"}, new boolean[]{true});
						
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
				}			
				
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};		
		tb.setSizeFull();
		tb.setSelectable(true);
		
		tb.setColumnCollapsingAllowed(true);
						
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome_razao", "Nome / Razão");	
		tb.setColumnHeader("doc_cpf_cnpj", "CPF/CNPJ");
		tb.setVisibleColumns(new Object[] {"id","nome_razao","doc_cpf_cnpj"});
		
		tb.setImmediate(true);
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){					
					btDefinirCredenciais.setEnabled(true);
				}else{
					btDefinirCredenciais.setEnabled(false);					
				}
			}
		});
		
		tb.setColumnCollapsible("nome", false);
		//tb.setConverter("id", null);
		
		
		
		
		return tb;
	}
	

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


	public void addFilter(String s) {
		
		
		container.removeAllContainerFilters();
		container.setApplyFiltersImmediately(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		container.sort(new String[] {"id"}, new boolean[]{false});
		
		try{
			Integer cod = Integer.valueOf(s);
			container.addContainerFilter(Filters.eq("id", cod));
		}catch(Exception e){
		
			
			Object[] collums = tb.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			for(Object c:collums){		 
				
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}			
			}
			
			container.addContainerFilter(Filters.or(filtros));
		
		}
		
		container.applyFilters();
		container.sort(new String[] {"id"}, new boolean[]{false});
		hlFloat.replaceComponent(lbRegistros, buildlbRegistros());
	}


	public Button BuildbtDefinirCredenciais() {
		btDefinirCredenciais = new Button("Definir Credenciais", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Definir Credenciais"))				
				{
				
				DefinirCredenciaisEditor defCredenciais = new DefinirCredenciaisEditor((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue(), "Credenciais", true);
				defCredenciais.addListerner(new DefinirCredenciaisEditor.DefinirCredenciaisListerner() {
					
					@Override
					public void onClose(DefinirCredenciaisEvent event) {
						if(event.isConfirm()){
							Notify.Show("Dados Gravados Com Sucesso!", Notify.TYPE_SUCCESS);
						}
					}
				});
				
				getUI().addWindow(defCredenciais);
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Definir Credenciais", Type.ERROR_MESSAGE);
				}
				
			}
		});
		btDefinirCredenciais.setEnabled(false);
		return btDefinirCredenciais;
	}

	
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}

