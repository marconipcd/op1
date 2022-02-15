package com.digital.opuserp.view.modulos.acesso.olts;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ConcentradorDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Olts;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.concentrador.ConcentradorEditor;
import com.digital.opuserp.view.modulos.acesso.concentrador.ConcentradorView;
import com.digital.opuserp.view.modulos.acesso.concentrador.InterfacesReadOnly;
import com.digital.opuserp.view.modulos.acesso.concentrador.ConcentradorEditor.ConcentradorEvent;
import com.digital.opuserp.view.modulos.acesso.olts.OltEditor.OltEvent;
import com.digital.opuserp.view.util.Notify;
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
import com.vaadin.ui.ComboBox;
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

public class OltView extends VerticalLayout{

	// JPA
	JPAContainer<Olts> container;
	
	Table tb;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
		
	ComboBox cbStatus;
		
	private Label lbRegistros;
	HorizontalLayout hlFloat;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	
	public OltView(boolean act){
		
		
		if(act){
		
		gmDAO = new GerenciarModuloDAO();
		
		
		setSizeFull();
		
		HorizontalLayout hlButons = new HorizontalLayout();		
		hlButons.addComponent(BuildbtNovo());
		hlButons.addComponent(BuildbtEditar());
		hlButons.addComponent(BuildbtExcluir());
				
		
		HorizontalLayout hlButtonsRoot = new HorizontalLayout();
		hlButtonsRoot.setWidth("100%");
				
		hlButtonsRoot.addComponent(hlButons);
		hlButtonsRoot.setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(hlButtonsRoot);
		
		addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(buildStatus());
				addComponent(buildTfbusca());
				setExpandRatio(tfBusca, 1.0f);
			}
		});
		
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
	
	public JPAContainer<Olts> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Olts.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(cbStatus.getValue().toString() != null){
			if(!cbStatus.getValue().equals("TODOS")){
				container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
			}
		}
		
		container.sort(new Object[]{"identificacao"}, new boolean[]{true});				
		return container;
	}
	
	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setVisibleColumns(new Object[] {"id","identificacao","endereco_ip","situacao","status"});

		tb.setColumnCollapsed("id", true);	
				
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("identificacao", "Nome");
		tb.setColumnHeader("endereco_ip", "Endereço IP");
		tb.setColumnHeader("situacao", "Situação");
		tb.setColumnHeader("status", "Status");

		
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
					
					ConcentradorEditor cEditor = new ConcentradorEditor(tb.getItem(tb.getValue()), "Editar OLT", true);
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
	
	public void addFilter(String s) {
		
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));	
		
		if(cbStatus.getValue().toString() != null){
			if(!cbStatus.getValue().equals("TODOS")){
				container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
			}
		}
		
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

	}

	
	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
						final BeanItem<Olts> newItem = new BeanItem<Olts>(new Olts());
										
						OltEditor cEditor = new OltEditor(newItem, "Cadastrar Concentrador", true);
						cEditor.addListerner(new OltEditor.OltListerner() {
							
							@Override
							public void onClose(OltEvent event) {
								if(event.isConfirm()){
									
									try {
										Olts cAdd = newItem.getBean();
										cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
										container.addEntity(cAdd);	
										container.commit();
										hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
										Notify.Show("OLT Cadastrada com Sucesso!", Notify.TYPE_SUCCESS);
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou uma OLT"));
									} catch (Exception e) {								
										e.printStackTrace();							
										Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não foi Possivel Concluir Cadastro de OLT, Uma Excessão foi Gerada"));
									}
									
								}else{
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou um Cadastro de OLT"));
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
					Notify.Show("Você não Possui Permissão para Cadastrar uma OLT", Notify.TYPE_ERROR);
				}
			}
		});
		return btNovo;
	}

	
	public Button BuildbtEditar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
				{
				
				OltEditor cEditor = new OltEditor(tb.getItem(tb.getValue()), "Editar OLT", true);
				cEditor.addListerner(new OltEditor.OltListerner() {
					
					@Override
					public void onClose(OltEvent event) {
						if(event.isConfirm()){
							container.commit();
							hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
							
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou uma OLT"));
						}else{
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou Edição de OLT"));
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
					Notify.Show("Você não Possui Permissão para Editar uma OLT", Notify.TYPE_ERROR);
				}
			}
		});
		btEditar.setEnabled(false);

		return btEditar;
	}

	
	
	
	public Button BuildbtExcluir() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
				
					final Integer codConcentrador = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());
					
					//TODO 
					ConcentradorDAO concentradorDAO = new ConcentradorDAO();						
					if(concentradorDAO.allowRemove(codConcentrador)==false){
						
						Notify.Show("A OLT Selecionado está em Uso!", Notify.TYPE_WARNING);						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Tentou Excluir Uma OLT em Uso"));	
					}else{
						
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a OLT Selecionado?", true, true);
							gDialog.addListerner(new GenericDialog.DialogListerner() {
								
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){
										container.removeItem(tb.getValue());
										container.commit();	
										hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma OLT"));								
									}
								}
							});
						
						getUI().addWindow(gDialog);
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Excluir uma OLT", Notify.TYPE_ERROR);
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
