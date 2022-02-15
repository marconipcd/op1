package com.digital.opuserp.view.modulos.configuracoes.config_financeiro.controle;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.ceps.CepEditor;
import com.digital.opuserp.view.modulos.cadastro.ceps.CepEditor.CepEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_financeiro.controle.ControleEditor.ControleEvent;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class ControleView extends VerticalLayout implements GenericView {

	JPAContainer<ControleTitulo> container;
	
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
	

	public ControleView(boolean act){
	
		if(act){
		setSizeFull();				
				
		gmDAO = new GerenciarModuloDAO();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(BuildbtTransferencia());
		hlButons.addComponent(BuildbtAceitar());
		hlButons.addComponent(BuildbtCancelar());
		
		addComponent(hlButons);
		setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(buildTfbusca());
		
		addComponent(buildTbGeneric());
		
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
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);	
		addFilter(tfBusca.getValue());
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ControleView)
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
	
	public JPAContainer<ControleTitulo> buildContainer(){
		container = JPAContainerFactory.makeBatchable(ControleTitulo.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.sort(new String[] {"nome"}, new boolean[]{true});
		container.setAutoCommit(false);
		container.addNestedContainerProperty("conta_bancaria.nome");
		
		return container;
	}
	
	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setVisibleColumns(new Object[]{"id","nome","conta_bancaria.nome"});
//		tb.addGeneratedColumn("Conta", new Table.ColumnGenerator() {
//			
//			@Override
//			public Object generateCell(Table source, Object itemId, Object columnId) {
//				
//				
//				return ((ContaBancaria)source.getItem(itemId).getItemProperty("conta_bancaria").getValue()).getN_conta()+"-"
//						+((ContaBancaria)source.getItem(itemId).getItemProperty("conta_bancaria").getValue()).getNome_banco();
//			}
//		});
		
		tb.setColumnCollapsed("id", true);	
		
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome", "Controle");
		tb.setColumnHeader("conta_bancaria.nome", "Contrato de Cobrança");
		
		
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
		
		tb.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()  && tb.getValue() != null){
					
					CepEditor cEditor = new CepEditor(tb.getItem(tb.getValue()), "Editar Controle", true);
					cEditor.addListerner(new CepEditor.CepListerner() {
						
						@Override
						public void onClose(CepEvent event) {
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
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(c.toString().equals("nome") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}		
		
		container.addContainerFilter(Filters.or(filtros));		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
				
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
//				{
									
						final BeanItem<ControleTitulo> newItem = new BeanItem<ControleTitulo>(new ControleTitulo());
										
						ControleEditor cEditor = new ControleEditor(newItem, "Cadastrar Controle", true);
						cEditor.addListerner(new ControleEditor.ControleListerner() {
							
							@Override
							public void onClose(ControleEvent event) {
								if(event.isConfirm()){
									
									try {
										ControleTitulo cAdd = newItem.getBean();	
										cAdd.setNome(cAdd.getNome().toUpperCase());
										cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
										
										container.addEntity(cAdd);	
										container.commit();
										tb.focus();
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Controle"));
									} catch (Exception e) {
										e.printStackTrace();
									}
									
								}
							}
						});
						
						cEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
								refresh();
							}
						});
						
						getUI().addWindow(cEditor);
//				}else{
//					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um Controle", Type.ERROR_MESSAGE);
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
				
					ControleEditor cEditor = new ControleEditor(tb.getItem(tb.getValue()), "Editar Controle", true);
					cEditor.addListerner(new ControleEditor.ControleListerner() {
						
						@Override
						public void onClose(ControleEvent event) {
							if(event.isConfirm()){
								container.commit();
								tb.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um CEP"));
							}
						}
					});
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
							refresh();
						}
					});
					
					getUI().addWindow(cEditor);
				
//				}else{
//					Notification.show("Atenção", "Você não Possui Permissão para Editar um Controle", Type.ERROR_MESSAGE);
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
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Controle Selecionado?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									container.removeItem(tb.getValue());
									container.commit();
									Notification.show("Controle Excluido com Sucesso!");
									tb.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Controle"));
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
//				}else{
//					Notification.show("Atenção", "Você não Possui Permissão para Excluir um Controle", Type.ERROR_MESSAGE);
//				}
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
