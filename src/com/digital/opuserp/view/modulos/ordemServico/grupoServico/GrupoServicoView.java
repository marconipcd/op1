package com.digital.opuserp.view.modulos.ordemServico.grupoServico;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.GrupoServico;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaEditor;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaEditor.CategoriaEvent;
import com.digital.opuserp.view.modulos.ordemServico.servicos.ServicoEditor;
import com.digital.opuserp.view.modulos.ordemServico.servicos.ServicoEditor.ServicoEvent;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class GrupoServicoView extends VerticalLayout implements GenericView{

	JPAContainer<GrupoServico> container;
	
	Table tb;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	public GrupoServicoView(boolean act){
		
		
		if(act){
		
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
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof GrupoServicoView)
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
	public JPAContainer<GrupoServico> buildContainer(){
		container = JPAContainerFactory.makeBatchable(GrupoServico.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));	
		container.sort(new Object[]{"nome"}, new boolean[]{true});

		
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("id", true);				
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome", "Nome");	
		
		tb.setVisibleColumns(new Object[] {"id","nome"});
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
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
		
		tb.setColumnCollapsible("id", false);
		
		
		tb.sort(new Object[]{"nome"}, new boolean[]{true});
		
		
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()   && tb.getValue() != null){
					CategoriaEditor cEditor = new CategoriaEditor(tb.getItem(tb.getValue()), "Editar Servico", true);
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
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));

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
		container.sort(new Object[]{"nome"}, new boolean[]{true});
		
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
   				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
				final BeanItem<GrupoServico> newItem = new BeanItem<GrupoServico>(new GrupoServico());
								
				final ServicoEditor cEditor = new ServicoEditor(newItem, "Cadastrar Grupo de Servico", true);
				cEditor.addListerner(new ServicoEditor.ServicoListerner() {
					
					@Override
					public void onClose(ServicoEvent event) {
						if(event.isConfirm()){
							
							try {
								GrupoServico cAdd = newItem.getBean();
								cAdd.setEmpresa(OpusERP4UI.getEmpresa());
								container.addEntity(cAdd);	
								container.commit();
								
								Notify.Show("Grupo de Serviço Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
								tb.focus();
								
								cEditor.close();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Grupo de Serviço"));
							} catch (Exception e) {
								e.printStackTrace();															
								Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
							
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
						//refresh();
					}
				});
				
				getUI().addWindow(cEditor);
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um Grupo de Serviço", Type.ERROR_MESSAGE);
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
				
					final ServicoEditor cEditor = new ServicoEditor(tb.getItem(tb.getValue()), "Editar Grupo de Servico", true);
					cEditor.addListerner(new ServicoEditor.ServicoListerner() {
						
						@Override
						public void onClose(ServicoEvent event) {
							if(event.isConfirm()){
								container.commit();			
								Notify.Show("Grupo de Serviço Editado com Sucesso!", Notify.TYPE_SUCCESS);
								cEditor.close();
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Grupo de Serviço"));
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
					Notify.Show("Você não Possui Permissão para Editar um Grupo de Serviço", Notify.TYPE_ERROR);
				}
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
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{				
					
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Grupo de Serviço Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								container.removeItem(tb.getValue());
								container.commit();
								
								Notify.Show("Grupo de Serviço Excluído com Sucesso!", Notify.TYPE_SUCCESS);								
								tb.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Grupo de Serviço"));
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
					Notify.Show("Você não Possui Permissão para Excluir um Grupo de Serviço", Notify.TYPE_ERROR);
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