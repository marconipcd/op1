package com.digital.opuserp.view.modulos.acesso.redes;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.SwithDAO;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Swith;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.caixa_atendimento.CaixaDeAtendimentoView;
import com.digital.opuserp.view.modulos.acesso.caixa_atendimento.CaixaAtendimentoEditor;
import com.digital.opuserp.view.modulos.acesso.caixa_atendimento.CaixaAtendimentoEditor.SwithEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;

public class redesView extends VerticalLayout implements GenericView{

	//JPA
	JPAContainer<Swith> container;
	
	Table tb;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
				
	private Label lbRegistros;
	HorizontalLayout hlFloat;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	
	public redesView(boolean act){
		
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
		
		if(c instanceof CaixaDeAtendimentoView)
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
	
	public JPAContainer<Swith> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Swith.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addNestedContainerProperty("concentrador.identificacao");
		
		container.sort(new Object[]{"identificacao"}, new boolean[]{true});			
							
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setVisibleColumns(new Object[] {"id","identificacao","concentrador.identificacao","modelo","endereco","numero","referencia"});
		tb.setColumnCollapsed("id", true);
		tb.setColumnCollapsed("referencia", true);
					
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("concentrador.identificacao", "Concentrador");
		tb.setColumnHeader("identificacao", "Indentificação");
		tb.setColumnHeader("modelo", "Modelo");
		tb.setColumnHeader("endereco", "Endereço");
		tb.setColumnHeader("numero", "Número");		
		tb.setColumnHeader("referencia", "Referencia");		
		
		
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
					
					CaixaAtendimentoEditor cEditor = new CaixaAtendimentoEditor(tb.getItem(tb.getValue()), "Editar Swith", true);
					cEditor.addListerner(new CaixaAtendimentoEditor.SwithListerner() {
						
						@Override
						public void onClose(SwithEvent event) {
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

	@Override
	public void addFilter(String s) {
		
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
			
//		Object[] collums = tb.getVisibleColumns();		
//		List<Filter> filtros = new ArrayList<Filter>();		
//		
//		for(Object c:collums){		 
//			
//			if(!c.equals("Nº") && !c.equals("Dias") && !c.equals("Carência") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
//				filtros.add(new Like(c.toString(), "%"+s+"%", false));
//			}			
//		}
//		
//		container.addContainerFilter(Filters.or(filtros));
//		container.applyFilters();
//		container.sort(new String[]{"id"},new boolean[]{false});
//		replaceComponent(lbRegistros, buildLbRegistros());
//	}

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
				
				final BeanItem<Swith> newItem = new BeanItem<Swith>(new Swith());
								
				CaixaAtendimentoEditor cEditor = new CaixaAtendimentoEditor(newItem, "Cadastrar Swith", true);
				cEditor.addListerner(new CaixaAtendimentoEditor.SwithListerner() {
					
					@Override
					public void onClose(SwithEvent event) {
						if(event.isConfirm()){
							
							try {
								Swith cAdd = newItem.getBean();
								cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								container.addEntity(cAdd);	
								container.commit();
								hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
								Notify.Show("Swith Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou Um Swith"));
							} catch (Exception e) {
										
								Notify.Show("ERRO: "+e.getMessage(), Notify.TYPE_SUCCESS);
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu Cadastrar Um Swith"));
							}
							
						}else{
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou um Cadastro de Swith"));
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
					Notify.Show("Você não Possui Permissão para Cadastrar um Swith", Notify.TYPE_ERROR);
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
				
				CaixaAtendimentoEditor cEditor = new CaixaAtendimentoEditor(tb.getItem(tb.getValue()), "Editar Swith", true);
				cEditor.addListerner(new CaixaAtendimentoEditor.SwithListerner() {
					
					@Override
					public void onClose(SwithEvent event) {
						if(event.isConfirm()){
							container.commit();
							hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
							
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou Um Swith"));
						}else{
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou uma Edição de Swith"));
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
					Notify.Show("Você não Possui Permissão para Editar um Swith", Notify.TYPE_ERROR);
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
				
					//TODO Fazer Verificação se Swith está em Uso
					EntityItem<Swith> itemSwith = (EntityItem<Swith>)tb.getItem(tb.getValue());
					boolean allowDelete = SwithDAO.allowDelete(itemSwith.getEntity());
					
					if(allowDelete){
					
						final Integer codConcentrador = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());
										
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Swith Selecionado?", true, true);
							gDialog.addListerner(new GenericDialog.DialogListerner() {
								
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){
										container.removeItem(tb.getValue());
											container.commit();			
											hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
											
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu Um Swith"));
									}
								}
							});
						
						getUI().addWindow(gDialog);
					}else{
						Notify.Show("O Swith selecionado está em uso e não pode ser Excluído!", Notify.TYPE_WARNING);
					}
				}else{
					Notify.Show("Você não Possui Permissão para Excluir um Swith", Notify.TYPE_ERROR);
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
