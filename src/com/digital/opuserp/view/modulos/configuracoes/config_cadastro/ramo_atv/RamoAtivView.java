package com.digital.opuserp.view.modulos.configuracoes.config_cadastro.ramo_atv;


import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.RamoAtividade;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaEditor;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaEditor.CategoriaEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class RamoAtivView extends VerticalLayout implements GenericView {

	JPAContainer<RamoAtividade> container;
	
	Table tb;
	TextField tfBusca;
	//ComboBox cbStatus;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	public RamoAtivView(){		
		
		setSizeFull();
		gmDAO = new GerenciarModuloDAO();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(BuildbtTransferencia());
		hlButons.addComponent(BuildbtAceitar());
		hlButons.addComponent(BuildbtCancelar());
		
		addComponent(hlButons);
		setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
			//	addComponent(buildComboStatus());
				addComponent(buildTfbusca());
				setExpandRatio(tfBusca, 1.0f);
			}
		});
		
		addComponent(buildTbGeneric());
		//addComponent(lbRegistros());
		
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
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof RamoAtivView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
			btNovo.removeShortcutListener(slNovo);
			btEditar.removeShortcutListener(slEditar);
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
	public JPAContainer<RamoAtividade> buildContainer(){
		container = JPAContainerFactory.makeBatchable(RamoAtividade.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
			
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());		
		tb.setSizeFull();
		tb.setSelectable(true);
//		tb.setMultiSelect(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("id", true);
				
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome", "Nome");		
		
		tb.setVisibleColumns(new Object[] {"id","nome"});
		
		tb.setImmediate(true);
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditar.setEnabled(true);
					btExcluir.setEnabled(true);
				}else{
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
				}
			}
		});
		
		tb.setColumnCollapsible("nome", false);
		
		tb.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				
				if(event.isDoubleClick()   && tb.getValue() != null){
					CategoriaEditor cEditor = new CategoriaEditor(tb.getItem(tb.getValue()), "Editar Ramo de Atividade", true);
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
	
//	public ComboBox buildComboStatus(){
//		cbStatus = new ComboBox();
//		cbStatus.setNullSelectionAllowed(false);
//		cbStatus.addItem("ATIVO");
//		cbStatus.addItem("INATIVO");
//		cbStatus.setImmediate(true);
//		cbStatus.select("ATIVO");
//		cbStatus.addListener(new Property.ValueChangeListener() {
//			
//			@Override
//			public void valueChange(ValueChangeEvent event) {
//				addFilter(tfBusca.getValue());				
//			}
//		});		
//		return cbStatus;	
//	}

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
//		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
//		container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		container.addContainerFilter(		
						new Like("nome", "%"+s+"%", false));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
//				{
				
				final BeanItem<RamoAtividade> newItem = new BeanItem<RamoAtividade>(new RamoAtividade());
								
				CategoriaEditor cEditor = new CategoriaEditor(newItem, "Cadastrar Ramo de Atividade", true);
				cEditor.addListerner(new CategoriaEditor.CategoriaListerner() {
					
					@Override
					public void onClose(CategoriaEvent event) {
						if(event.isConfirm()){
							
							try {
								RamoAtividade cAdd = newItem.getBean();
								container.addEntity(cAdd);	
								container.commit();
								tb.focus();
//								Notification.show("Categoria Cadastrada com Sucesso!");
							} catch (Exception e) {
										
								Notify.Show("ERRO: "+e.getMessage(), Notify.TYPE_ERROR);
//								LogDAO.addLog(new Log(null, "ERRO ao Cadastrar um Novo Cliente: "+e.getMessage(), OpusERP4_ServerUI.getUsuarioLogadoUI().getUsername(), null));
							}
							
						}else{
//							LogDAO.addLog(new Log(null, "Ação de Cadastro de Cliente Cancelada!", OpusERP4_ServerUI.getUsuarioLogadoUI().getUsername(), null));
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
				
//				}else{
//					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um Ramo de Atividade", Type.ERROR_MESSAGE);
//				}
			}
		});

		return btNovo;
	}

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
//				{
				
				CategoriaEditor cEditor = new CategoriaEditor(tb.getItem(tb.getValue()), "Editar Ramo de Atividade", true);
				cEditor.addListerner(new CategoriaEditor.CategoriaListerner() {
					
					@Override
					public void onClose(CategoriaEvent event) {
						if(event.isConfirm()){
							container.commit();			
//							Notification.show("Categoria Alterada com Sucesso!");
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
//				}else{
//					Notification.show("Atenção", "Você não Possui Permissão para Editar um Ramo de Atividade", Type.ERROR_MESSAGE);
//				}
			}
		});
		btEditar.setEnabled(false);
					
		return btEditar;
	}

	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
//				{
				
					ClienteDAO cDAO = new ClienteDAO();
					Integer categoria = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());
					
//					if (cDAO.categoriaEmUso(OpusERP4UI.getEmpresa(), new Categorias(categoria)) == true){
//						
//						Notification.show("Ramo de Atividade em uso não pode ser excluido!");					
//					
//					}else{					
					
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Ramo de Atividade Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								container.removeItem(tb.getValue());
								container.commit();
								Notify.Show("Ramo de Atividade Excluído com Sucesso!", Notify.TYPE_SUCCESS);
								tb.focus();
							}
//							final Set selecionado = (Set) tb.getValue();
//							for(final Iterator i = selecionado.iterator(); i.hasNext();){
//								final Object id = i.next();
//								if(tb.containsId(id)){
//									tb.removeItem(id);
//									container.commit();
//									Notification.show("Categoria Excluída com Sucesso!");
//								}
//							}
							
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
//				}else{
//					Notification.show("Atenção", "Você não Possui Permissão para Excluir um Ramo de Atividade", Type.ERROR_MESSAGE);
//				}
//			}
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
