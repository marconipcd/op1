package com.digital.opuserp.view.modulos.acesso.concentrador;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ConcentradorDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.UsuarioConcentradorDAO;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.UsuarioConcentradores;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.concentrador.ConcentradorEditor.ConcentradorEvent;
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
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class ConcentradorView extends VerticalLayout implements GenericView{

	// JPA
	JPAContainer<Concentrador> container;
	JPAContainer<UsuarioConcentradores> containerUsuarioConcentr = JPAContainerFactory.make(UsuarioConcentradores.class, ConnUtil.getEntity());
	
	Table tb;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	Button btInterface;
	Button btUsuarios;
	
	ComboBox cbStatus;
		
	private Label lbRegistros;
	HorizontalLayout hlFloat;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	
	public ConcentradorView(boolean act){
		
		
		if(act){
		
		gmDAO = new GerenciarModuloDAO();
		
		
		setSizeFull();
		
		HorizontalLayout hlButons = new HorizontalLayout();		
		hlButons.addComponent(BuildbtTransferencia());
		hlButons.addComponent(BuildbtAceitar());
		hlButons.addComponent(BuildbtCancelar());
				
		
		HorizontalLayout hlButtonsRoot = new HorizontalLayout();
		hlButtonsRoot.setWidth("100%");
		
		hlButtonsRoot.addComponent(BuildbtInterfaces());
		hlButtonsRoot.setComponentAlignment(btInterface, Alignment.TOP_LEFT);
		hlButtonsRoot.addComponent(BuildbtUsuarios());
		hlButtonsRoot.setComponentAlignment(btUsuarios, Alignment.TOP_LEFT);
		hlButtonsRoot.setExpandRatio(btUsuarios, 1f);
		
		hlButtonsRoot.addComponent(hlButons);
		hlButtonsRoot.setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(hlButtonsRoot);
		
//		addComponent(buildTfbusca());
		addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(buildStatus());
				addComponent(buildTfbusca());
				setExpandRatio(tfBusca, 1.0f);
			}
		});
		
		addComponent(buildTbGeneric());
//		addComponent(buildLbRegistros());
		
		
		
		hlFloat = new HorizontalLayout();
		hlFloat.setWidth("100%");
		hlFloat.addComponent(buildLbRegistros());
		Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
		lbLegend.setWidth("250px");
		hlFloat.addComponent(lbLegend);
		hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
		hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
		
		addComponent(hlFloat);
		
		
		
		setExpandRatio(tb, 1);
		}
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ConcentradorView)
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
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public JPAContainer<Concentrador> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Concentrador.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addNestedContainerProperty("base.nome");
		
		
		if(cbStatus.getValue().toString() != null){
			if(!cbStatus.getValue().equals("TODOS")){
				container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
			}
		}
		
		
		container.sort(new Object[]{"identificacao"}, new boolean[]{true});
				
		return container;
	}
	
	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setVisibleColumns(new Object[] {"id","identificacao","base.nome","infraestrutura","modelo","antena","loop_back","endereco_ip","situacao"});

		tb.setColumnCollapsed("id", true);
		
		tb.setColumnCollapsed("porta_api", true);
		tb.setColumnCollapsed("wireless", true);
		tb.setColumnCollapsed("loop_back", true);
		tb.setColumnCollapsed("infraestrutura", true);
				
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("base.nome", "Local");
		tb.setColumnHeader("identificacao", "Nome");
		tb.setColumnHeader("modelo", "Modelo");
		tb.setColumnHeader("antena", "Antena");
		tb.setColumnHeader("endereco_ip", "Endereço IP");
		
		tb.setColumnHeader("infraestrutura", "Infraestrutura");
		tb.setColumnHeader("loop_back", "Loopback");
		tb.setColumnHeader("situacao", "Situação");
		
		tb.setImmediate(true);
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditar.setEnabled(true);
					btExcluir.setEnabled(true);
//					btInterface.setEnabled(true);					
				}else{
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
//					btInterface.setEnabled(false);
				}
			}
		});
		
	tb.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()  && tb.getValue() != null){
					
					ConcentradorEditor cEditor = new ConcentradorEditor(tb.getItem(tb.getValue()), "Editar Concentrador", true);
					cEditor.addListerner(new ConcentradorEditor.ConcentradorListerner() {
						
						@Override
						public void onClose(ConcentradorEvent event) {
							if(event.isConfirm()){
								container.commit();
							}
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
		tfBusca.setInputPrompt("Buscar...");
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

	public ComboBox buildStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		cbStatus.addItem("TODOS");
		cbStatus.addItem("ATIVO");
		cbStatus.addItem("INATIVO");
		cbStatus.setImmediate(true);
		cbStatus.select("ATIVO");
		cbStatus.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
					addFilter(tfBusca.getValue());				
			}
		});
		
		return cbStatus;
	}
	@Override
	public void addFilter(String s) {
		
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));	
		
		if(cbStatus.getValue().toString() != null){
			if(!cbStatus.getValue().equals("TODOS")){
				container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
			}
		}
		
//		Object[] collums = tb.getVisibleColumns();		
//		List<Filter> filtros = new ArrayList<Filter>();		
//		
//		for(Object c:collums){		 
//			
//			if(!c.equals("Nº") && !c.equals("Dias") && !c.equals("Carência") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
//				filtros.add(new Like(c.toString(), "%"+s+"%", false));
//			}			
//		}
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();	

		try {
		Integer cod = Integer.valueOf(s);
		
			for(Object c:collums){		 			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
				filtros.add(new Like(c.toString(), "%"+cod+"%", false));
			}			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}	
		}
		
	} catch (Exception e) {

		for(Object c:collums){		 			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
	}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		container.sort(new Object[]{"identificacao"}, new boolean[]{true});
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
//		container.sort(new String[]{"id"},new boolean[]{false});
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
				final BeanItem<Concentrador> newItem = new BeanItem<Concentrador>(new Concentrador());
								
				ConcentradorEditor cEditor = new ConcentradorEditor(newItem, "Cadastrar Concentrador", true);
				cEditor.addListerner(new ConcentradorEditor.ConcentradorListerner() {
					
					@Override
					public void onClose(ConcentradorEvent event) {
						if(event.isConfirm()){
							
							try {
								Concentrador cAdd = newItem.getBean();
								cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								container.addEntity(cAdd);	
								container.commit();
								hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
								Notify.Show("Concentrador Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Concentrador"));
							} catch (Exception e) {								
								e.printStackTrace();							
								Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não foi Possivel Concluir Cadastro de Concentrador, Uma Excessão foi Gerada"));
							}
							
						}else{
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou um Cadastro de Concentrador"));
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
					Notify.Show("Você não Possui Permissão para Cadastrar um Concentrador", Notify.TYPE_ERROR);
				}
			}
		});
		return btNovo;
	}

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
				{
				
				ConcentradorEditor cEditor = new ConcentradorEditor(tb.getItem(tb.getValue()), "Editar Concentrador", true);
				cEditor.addListerner(new ConcentradorEditor.ConcentradorListerner() {
					
					@Override
					public void onClose(ConcentradorEvent event) {
						if(event.isConfirm()){
							container.commit();
							hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
							
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Concentrador"));
						}else{
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou Edição de Concentrador"));
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
					Notify.Show("Você não Possui Permissão para Editar um Concentrador", Notify.TYPE_ERROR);
				}
			}
		});
		btEditar.setEnabled(false);

		return btEditar;
	}

	
	public Button BuildbtInterfaces(){
		btInterface = new Button("Analítico Concentradores", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar Interfaces"))				
				{

							InterfacesReadOnly interfaces = new InterfacesReadOnly(true, true);

							getUI().addWindow(interfaces);							

				}else{					
					Notify.Show("Você não Possui Permissão para Visualizar as Analítico Concentrador", Notify.TYPE_ERROR);
				}
			}
		});
		return btInterface;
	}
	public Button BuildbtUsuarios(){
		btUsuarios = new Button("Usuários", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar Usuarios"))				
				{
													
					Window w = new Window("Usuários");
					w.setContent(new VerticalLayout(){
						{
							setSizeFull();
							setMargin(true);
							setSpacing(true);
							
							addComponent(new FormLayout(){
								{
									setWidth("100%");
									
									final TextField txtUsuario = new TextField("Usuário");
									txtUsuario.setWidth("100%");
									txtUsuario.setRequired(true);
									txtUsuario.setEnabled(false);
									addComponent(txtUsuario);
									
									final PasswordField txtSenha = new PasswordField("Senha");
									txtSenha.setWidth("100%");
									txtSenha.setRequired(true);	
									txtSenha.setEnabled(false);
									addComponent(txtSenha);
									
									final ComboBox cbGrupo = new ComboBox("Grupo");
									cbGrupo.setRequired(true);
									cbGrupo.setNullSelectionAllowed(false);									
									cbGrupo.setWidth("200px");
									cbGrupo.addItem("full");
									cbGrupo.addItem("read");
									cbGrupo.setEnabled(false);
									addComponent(cbGrupo);
									
									final Button btNovo = new Button("Novo", new Button.ClickListener() {
										
										@Override
										public void buttonClick(ClickEvent event) {
											
											if(event.getButton().getCaption().equals("Novo")){
												event.getButton().setCaption("Salvar");
												
												txtUsuario.setEnabled(true);
												txtUsuario.focus();
												
												txtSenha.setEnabled(true);
												cbGrupo.setEnabled(true); 										
												
												
											}else{
												
												
												 if(txtUsuario.isValid() && txtSenha.isValid() && cbGrupo.isValid()){
													 
													 if(!UsuarioConcentradorDAO.buscarUsuarioRadius(txtUsuario.getValue())){
														 boolean check = UsuarioConcentradorDAO.cadastrarUsuario(txtUsuario.getValue(), txtSenha.getValue(), cbGrupo.getValue().toString());
														 
														 if(check){
															 Notify.Show("Usuário cadastrado com sucesso!", Notify.TYPE_SUCCESS);
															 event.getButton().setCaption("Novo");
															 
															 txtUsuario.setValue("");
															 txtUsuario.setEnabled(false);
															 
															 txtSenha.setValue("");
															 txtSenha.setEnabled(false);
															 
															 cbGrupo.select("");
															 cbGrupo.setEnabled(false);
															 
															 containerUsuarioConcentr.refresh();														 
														 }
													 }else{
														 Notify.Show("Não é possível salvar, Usuário já existe no Radius!", Notify.TYPE_WARNING);														 
													 }
													 
												 }
											}
											
											
										}
									});
									Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
										
										@Override
										public void buttonClick(ClickEvent event) {
											 
											 txtUsuario.setEnabled(true); 
											 txtUsuario.setValue("");
											 txtUsuario.setEnabled(false);
											 
											 txtSenha.setEnabled(true);
											 txtSenha.setValue("");
											 txtSenha.setEnabled(false);
											 
											 cbGrupo.setEnabled(true);
											 cbGrupo.select("");
											 cbGrupo.setEnabled(false);
											 
											 btNovo.setCaption("Novo");
										}
									});
									
									HorizontalLayout hl = new HorizontalLayout();
									hl.setWidth("100%");
									hl.addComponent(btNovo);
									hl.addComponent(btCancelar);
									hl.setComponentAlignment(btNovo, Alignment.MIDDLE_RIGHT);
									hl.setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
									hl.setExpandRatio(btNovo, 1f);
									
									addComponent(hl); 
									
								}
							});
							
							VerticalLayout vl1 = new VerticalLayout(){
								{
									setSizeFull();
									
									TextField txtBuscar = new TextField();
									txtBuscar.addTextChangeListener(new FieldEvents.TextChangeListener() {
										
										@Override
										public void textChange(TextChangeEvent event) {
											containerUsuarioConcentr.removeAllContainerFilters();
											containerUsuarioConcentr.addContainerFilter(new Like("usuario", "%"+event.getText()+"%", false));
											containerUsuarioConcentr.applyFilters();
										}
									});
									txtBuscar.setInputPrompt("buscar...");
									txtBuscar.setWidth("100%");
									addComponent(txtBuscar); 
									
									
									final Table tb = new Table(null,containerUsuarioConcentr);
									tb.setSelectable(true);
									
									tb.setSizeFull();
									tb.setVisibleColumns(new Object[]{"usuario","grupo","data_alteracao"});
									
									tb.setColumnHeader("usuario", "Usuário");
									tb.setColumnHeader("grupo", "Grupo");
									tb.setColumnHeader("data_alteracao", "Data Alteração");
									
									tb.addGeneratedColumn("e", new Table.ColumnGenerator() {
										
										@Override
										public Object generateCell(final Table source,final Object itemId, Object columnId) {

											Button btExcluir = new Button("excluir", new Button.ClickListener() {
												
												@Override
												public void buttonClick(ClickEvent event) {
													GenericDialog gd = new GenericDialog("Confirme para continuar!", "Deseja realmente excluir o usuario ?", true, true);
													gd.addListerner(new GenericDialog.DialogListerner() {
														
														@Override
														public void onClose(DialogEvent event) {
															if(event.isConfirm()){
																EntityItem<UsuarioConcentradores> uItem = (EntityItem<UsuarioConcentradores>)source.getItem(itemId);
																boolean check = UsuarioConcentradorDAO.excluirUsuario(uItem.getEntity());
																
																if(check){
																	Notify.Show("Usuário Excluído com sucesso!", Notify.TYPE_SUCCESS); 
																	containerUsuarioConcentr.refresh();
																}
															}
														}
													});
													getUI().addWindow(gd);
													
												}
											});
											btExcluir.setStyleName(Reindeer.BUTTON_LINK);
											
											return btExcluir;
										}
									});
									
									addComponent(tb); 
									setExpandRatio(tb, 1f);
								}
							};
							addComponent(vl1);
							setExpandRatio(vl1, 1f);
						}
					});
					w.setWidth("386px");
					w.setHeight("351px");
					w.center();
					w.setModal(true); 
					
					getUI().addWindow(w);

				}else{					
					Notify.Show("Você não Possui Permissão para Cadastrar Usuários", Notify.TYPE_ERROR);
				}
			}
		});
		return btUsuarios;
	}
	
	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
				
					final Integer codConcentrador = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());
					
					ConcentradorDAO concentradorDAO = new ConcentradorDAO();						
					if(concentradorDAO.allowRemove(codConcentrador)==false){
						
						Notify.Show("O Concentrador Selecionado está em Uso!", Notify.TYPE_WARNING);						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Tentou Excluir Um Concentrador em Uso"));	
					}else{
						
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Concentrador Selecionado?", true, true);
							gDialog.addListerner(new GenericDialog.DialogListerner() {
								
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){
										container.removeItem(tb.getValue());
										container.commit();	
										hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu Um Concentrador"));								
									}
								}
							});
						
						getUI().addWindow(gDialog);
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Excluir um Concentrador", Notify.TYPE_ERROR);
				}
		}	
		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
	
}
