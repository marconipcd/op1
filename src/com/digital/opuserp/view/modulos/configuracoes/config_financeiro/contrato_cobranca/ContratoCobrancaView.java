package com.digital.opuserp.view.modulos.configuracoes.config_financeiro.contrato_cobranca;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaEditor;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaEditor.CategoriaEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_financeiro.contrato_cobranca.ContratoCobrancaEditor.ContratoCobrancaEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
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

public class ContratoCobrancaView extends VerticalLayout implements GenericView {

	JPAContainer<ContaBancaria> container;
	
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
	
	public ContratoCobrancaView(boolean act){		
		
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
		addFilter(tfBusca.getValue());
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ContratoCobrancaView)
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
	public JPAContainer<ContaBancaria> buildContainer(){
		container = JPAContainerFactory.makeBatchable(ContaBancaria.class,ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));	
	
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("id", true);				

		tb.setVisibleColumns(new Object[] {"nome","cod_banco","agencia_banco","n_conta","nome_banco","cidade_uf","convenio","contrato","carteira","variacao_carteira","tipo"});
		
		tb.setColumnHeader("nome", "Nome");
		tb.setColumnHeader("cod_banco", "Código do Banco");
		tb.setColumnHeader("agencia_banco", "Nº Agencia");
		tb.setColumnHeader("n_conta", "Conta");
		tb.setColumnHeader("nome_banco", "Banco");
		tb.setColumnHeader("cidade_uf", "Cidade");
		tb.setColumnHeader("convenio", "Convênio");
		tb.setColumnHeader("contrato", "Contrato");
		tb.setColumnHeader("carteira", "Carteira");
		tb.setColumnHeader("variacao_carteira", "Variação");
		tb.setColumnHeader("tipo", "Tipo");
		
		
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
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
		tb.setColumnCollapsible("cod_banco", false);
		
		
		tb.sort(new Object[]{"cod_banco"}, new boolean[]{true});
		
		
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
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(new Like("nome", "%"+s+"%", false));
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
				
					final BeanItem<ContaBancaria> newItem = new BeanItem<ContaBancaria>(new ContaBancaria());
									
					ContratoCobrancaEditor cEditor = new ContratoCobrancaEditor(newItem, "Cadastrar Contrato Cobrança", true);
					cEditor.addListerner(new ContratoCobrancaEditor.ContratoCobrancaListerner() {
						
						@Override
						public void onClose(ContratoCobrancaEvent event) {
							if(event.isConfirm()){
								
								try {
									ContaBancaria cAdd = newItem.getBean();
									cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
									container.addEntity(cAdd);	
									container.commit();
									tb.focus();
	
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou uma Categoria"));
								} catch (Exception e) {
																									
									Notification.show("ERRO: "+e.getMessage());								
								}
								
							}
						}
					});
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();
							refresh();
						}
					});
					
					getUI().addWindow(cEditor);
				
//				}else{
//					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um Contrato de Cobrança", Type.ERROR_MESSAGE);
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
				
					ContratoCobrancaEditor cEditor = new ContratoCobrancaEditor(tb.getItem(tb.getValue()), "Editar Contrato de Cobrança", true);
					cEditor.addListerner(new ContratoCobrancaEditor.ContratoCobrancaListerner() {
						
						@Override
						public void onClose(ContratoCobrancaEvent event) {
							if(event.isConfirm()){
								container.commit();			
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Contrato de Cobrança"));
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
//					Notification.show("Atenção", "Você não Possui Permissão para Editar um Contrato de Cobrança", Type.ERROR_MESSAGE);
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
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
				
//					
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Contrato de Cobrança Selecionado?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									container.removeItem(tb.getValue());
									container.commit();
									Notification.show("Contrato de Cobrança Excluído com Sucesso!");
									tb.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Contrato de Cobrança"));
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
					Notification.show("Atenção", "Você não Possui Permissão para Excluir um Contrato de Cobrança", Type.ERROR_MESSAGE);
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
