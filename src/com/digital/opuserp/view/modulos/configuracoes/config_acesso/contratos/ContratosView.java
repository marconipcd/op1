package com.digital.opuserp.view.modulos.configuracoes.config_acesso.contratos;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_acesso.contratos.ContratosEditor.ContratoEvent;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class ContratosView extends VerticalLayout implements GenericView {

	//JPA
	JPAContainer<ContratosAcesso> container;
	
	Table tb;
	ComboBox cbStatus;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	
	public ContratosView(boolean act){
		
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
				addComponent(buildComboStatus());
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

	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public JPAContainer<ContratosAcesso> buildContainer(){
		container = JPAContainerFactory.makeBatchable(ContratosAcesso.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		if(cbStatus.getValue().toString() != null){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		}
				
		container.sort(new Object[]{"nome"}, new boolean[]{true});
		
		return container;
	}
	

	public void buildShortcurEvents(Component c){
		
		if(c instanceof ContratosView)
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
	
	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("id", true);
		tb.setColumnCollapsed("valor_adesao", true);
		tb.setColumnCollapsed("valor_desconto", true);
		tb.setColumnCollapsed("ato_autorizacao", true);
		tb.setColumnCollapsed("termo_autorizacao", true);
		tb.setColumnCollapsed("reg_cartorio", true);
		tb.setColumnCollapsed("reg_cartorio_scm", true);
						
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("nome", "Nome");
		tb.setColumnHeader("vigencia", "Vigência");
		tb.setColumnHeader("valor_adesao", "Valor Adesão");		
		tb.setColumnHeader("valor_desconto", "Valor Desconto");		
		tb.setColumnHeader("tipo_contrato", "Sistema de Cobrança");
		tb.setColumnHeader("bloqueio", "Bloqueio");
		tb.setColumnHeader("bloqueio_total", "Bloqueio total");
		
		tb.setColumnHeader("carencia", "Carência");
		
		tb.setColumnAlignment("valor_adesao", Align.RIGHT);
		tb.setColumnAlignment("valor_desconto", Align.RIGHT);
		
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("ato_autorizacao", "Ato de Autorização");
		tb.setColumnHeader("termo_autorizacao", "Termo de Autorização");
		tb.setColumnHeader("reg_cartorio_scm", "Registro de Cartório SCM");
		tb.setColumnHeader("reg_cartorio", "Registro de Cartório");
		
		tb.setVisibleColumns(new Object[] {"id","nome","vigencia","tipo_contrato","valor_adesao","valor_desconto",
		"bloqueio","bloqueio_total","carencia","status","ato_autorizacao","termo_autorizacao","reg_cartorio_scm","reg_cartorio"});
		
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
					
					ContratosEditor cEditor = new ContratosEditor(tb.getItem(tb.getValue()), "Editar Tipo de Plano", true);
					cEditor.addListerner(new ContratosEditor.ContratoListerner() {
						
						@Override
						public void onClose(ContratoEvent event) {
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

	public ComboBox buildComboStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
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

	
	@Override
	public void addFilter(String s) {
		container.removeAllContainerFilters();
		
		container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
//		container.addContainerFilter(
//				Filters.or(
//						new Like("nome", "%"+s+"%", false)
//						new Like("clausulas", "%"+s+"%", false),
//						new Like("valor_adesao", "%"+s+"%", false),
//						new Like("valor_equipamento", "%"+s+"%", false),
//						new Like("regime", "%"+s+"%", false),
//						new Like("tipo_contrato", "%"+s+"%", false),
//						new Like("carencia", "%"+s+"%", false)
	
//					));
//		container.applyFilters();
		
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

//		for(Object c:collums){		 			
//		if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
//			filtros.add(new Like(c.toString(), "%"+s+"%", false));
//		}			
//	}
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		container.sort(new Object[]{"nome"}, new boolean[]{true});
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				//if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
			//	{
				
					final BeanItem<ContratosAcesso> newItem = new BeanItem<ContratosAcesso>(new ContratosAcesso());
								
				ContratosEditor cEditor = new ContratosEditor(newItem, "Cadastrar Tipo de Plano", true);
				cEditor.addListerner(new ContratosEditor.ContratoListerner() {
					
					@Override
					public void onClose(ContratoEvent event) {
						if(event.isConfirm()){
							
							try {
								ContratosAcesso cAdd = newItem.getBean();
								cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								container.addEntity(cAdd);	
								container.commit();
								hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
								Notify.Show("Contrato Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
							} catch (Exception e) {
								e.printStackTrace();																
								Notify.Show("ERRO: "+e.getMessage(), Notify.TYPE_ERROR);
//								LogDAO.addLog(new Log(null, "ERRO ao Cadastrar um Novo Cliente: "+e.getMessage(), OpusERP4_ServerUI.USUARIO_LOGADO.getUsername(), null));
							}
							
						}else{
//							LogDAO.addLog(new Log(null, "Ação de Cadastro de Cliente Cancelada!", OpusERP4_ServerUI.USUARIO_LOGADO.getUsername(), null));
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
				
				
				
				//}else{
			//		Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um Contrato de Acesso", Type.ERROR_MESSAGE);
				//}
			}
		});
		return btNovo;
	}

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				//if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
			//	{
				
				ContratosEditor cEditor = new ContratosEditor(tb.getItem(tb.getValue()), "Editar Tipo de Plano", true);
				cEditor.addListerner(new ContratosEditor.ContratoListerner() {
					
					@Override
					public void onClose(ContratoEvent event) {
						if(event.isConfirm()){
							container.commit(); 
							hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
							Notify.Show("Tipo de Plano Alterado com Sucesso!", Notify.TYPE_SUCCESS);
						}
					}
				});
				
				getUI().addWindow(cEditor);
				
				cEditor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();							
					}
				});
				
				//}else{
			//		Notification.show("Atenção", "Você não Possui Permissão para Editar um Contrato de Acesso", Type.ERROR_MESSAGE);
				//}
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
				
				//if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
			//	{
				
				final Integer codContratoAcesso = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());
				
				ContratosAcessoDAO paDAO = new ContratosAcessoDAO();						
				if(paDAO.allowRemove(codContratoAcesso)==false){
					Notify.Show("A Tipo de Plano Selecionado está em Uso!", Notify.TYPE_ERROR);
				}else{

				GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Tipo de Plano Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								container.removeItem(tb.getValue());
								container.commit();
								hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
								Notify.Show("Tipo de Plano Excluido com Sucesso!", Notify.TYPE_SUCCESS);
							}
						}
					});
				
				getUI().addWindow(gDialog);
				
				
			}
			//}else{
			//		Notification.show("Atenção", "Você não Possui Permissão para Excluir um Contrato de Acesso", Type.ERROR_MESSAGE);
			//	}
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