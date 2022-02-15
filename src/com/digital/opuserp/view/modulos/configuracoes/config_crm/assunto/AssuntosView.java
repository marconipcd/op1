package com.digital.opuserp.view.modulos.configuracoes.config_crm.assunto;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.UsuarioDAO;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.setores.SetoresView;
import com.digital.opuserp.view.modulos.configuracoes.config_crm.assunto.AssuntosEditor.CRmAssuntoSavedEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class AssuntosView extends VerticalLayout {
	
	TextField tfBusca;
	Table tbGeneric;
	Button btNovo;
	Button btEditar;
	Button btPesquisa;
	Button btExcluir;
	HorizontalLayout hlFloat;
	
	
	JPAContainer<CrmAssunto> containerAssuntosCrm;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	
	GerenciarModuloDAO gmDAO;
	
	public AssuntosView(boolean act){
		
		if(act){
		setSizeFull();
		
		
		gmDAO = new GerenciarModuloDAO();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(BuildbtNovo());
		hlButons.addComponent(BuildbtEditar());
		hlButons.addComponent(BuildbtPesquisa());
		hlButons.addComponent(BuildbtExcluir());
		
		addComponent(hlButons);
		setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(buildTfbusca());		
		addComponent(buildTbGeneric());
		
		//addComponent(buildLbRegistros());
		
		hlFloat = new HorizontalLayout();
		hlFloat.setWidth("100%");
		hlFloat.addComponent(buildLbRegistros());
		Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
		lbLegend.setWidth("250px");
		hlFloat.addComponent(lbLegend);
		hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
		hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
		
		
		addComponent(hlFloat);
		
		setExpandRatio(tbGeneric, 1);
		}
						
	}
	
	public void refresh(){
		replaceComponent(tbGeneric, buildTbGeneric());
		setExpandRatio(tbGeneric, 1);
		addFilter(tfBusca.getValue());
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
		btPesquisa.setEnabled(false); 
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(containerAssuntosCrm.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof SetoresView)
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

	public JPAContainer<CrmAssunto> buildContainer(){
		containerAssuntosCrm = new JPAContainerFactory().makeBatchable(CrmAssunto.class, ConnUtil.getEntity());
		containerAssuntosCrm.setAutoCommit(false);
		containerAssuntosCrm.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		return containerAssuntosCrm;
	}
	
	
	
	public Table buildTbGeneric() {
		tbGeneric = new Table(null, buildContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("mostrar_central")){
					
					if((boolean)tbGeneric.getItem(rowId).getItemProperty("mostrar_central").getValue()){
						return "SIM";
					}else{
						return "NÃO";
					}
					
				}else{
					return super.formatPropertyValue(rowId, colId, property);
				}
			}
		};
		tbGeneric.setSizeFull();
		tbGeneric.setSelectable(true);
		
		tbGeneric.setVisibleColumns(new Object[]{"id", "nome", "conteudo", "mostrar_central","perguntas_ativas","status"});
		
		tbGeneric.setColumnHeader("id", "Código");
		tbGeneric.setColumnHeader("nome", "Nome");
		tbGeneric.setColumnHeader("conteudo", "Conteúdo");
		tbGeneric.setColumnHeader("mostrar_central", "Mostrar na Central");
		tbGeneric.setColumnHeader("perguntas_ativas", "Pesquisa");
		tbGeneric.setColumnHeader("status", "Status");
		
		tbGeneric.setColumnCollapsingAllowed(true);
			
		
		tbGeneric.setColumnCollapsible("nome", false);
		
		tbGeneric.setImmediate(true);
		tbGeneric.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditar.setEnabled(true);
					btExcluir.setEnabled(true);
					btPesquisa.setEnabled(true);
				}else{
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
					btPesquisa.setEnabled(false);
				}
				
			}
		});
		
		
		tbGeneric.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()  && tbGeneric.getValue() != null){
					
//					SetoresEditor editor = new SetoresEditor(tbGeneric.getItem(tbGeneric.getValue()), "Editar Setor");
//					
//					editor.addListener(new SetoresEditor.SetoresSavedListener() {
//						
//						@Override
//						public void setoresSaved(SetoresSavedEvent event) {
//							containerAssuntosCrm.commit();
//							replaceComponent(tbGeneric, buildTbGeneric());
//							tbGeneric.focus();
//						}
//					});
//					
//					editor.addCloseListener(new Window.CloseListener() {
//						
//						@Override
//						public void windowClose(CloseEvent e) {
//							tbGeneric.focus();
//						}
//					});
					
					//getUI().addWindow(editor);
					
				}
			}
		});
		
		return tbGeneric;
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
		containerAssuntosCrm.removeAllContainerFilters();
		containerAssuntosCrm.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		containerAssuntosCrm.addContainerFilter(
				Filters.or(
						new Like("id", s, false),
						new Like("nome", "%"+s+"%", false)
				));
		containerAssuntosCrm.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	
	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
//			if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
//			{
//												
				final BeanItem<CrmAssunto> newItem = new BeanItem<CrmAssunto>(new CrmAssunto());
				
				AssuntosEditor editor = new AssuntosEditor(newItem, "Cadastrar CRM Assunto");
				
				editor.addListener(new AssuntosEditor.CRmAssuntoSavedListener() {
					
					@Override
					public void setoresSaved(CRmAssuntoSavedEvent event) {
						if(event.isConfirm()){
							try {
								CrmAssunto sAdd = newItem.getBean();
								sAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								sAdd.setNome(sAdd.getNome().toUpperCase());
								containerAssuntosCrm.addEntity(sAdd);
								containerAssuntosCrm.commit();
								tbGeneric.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um CRM Assunto"));
							} catch (Exception e) {
								e.printStackTrace();
								Notify.Show("ERRO: "+e.getMessage(), Notify.TYPE_ERROR);
							}
						}else{
							
						}
					}
				});
				
				editor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tbGeneric.focus();
						refresh();
					}
				});
				
				getUI().addWindow(editor);
				editor.center();
				editor.setModal(true);
				
//				}else{
//					Notify.Show("Você não Possui Permissão para Cadastrar um CRM Assunto!", Notify.TYPE_ERROR);
//				}
				
				
			}
		});		
					
		return btNovo;
	}

	public Button BuildbtPesquisa() {
		btPesquisa = new Button("Pesquisa", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Pesquisar"))				
//				{				
					EntityItem<CrmAssunto> itemAssunto = (EntityItem<CrmAssunto>) tbGeneric.getItem(tbGeneric.getValue());
					PesquisaEditor pesquisaEditor = new PesquisaEditor(itemAssunto.getEntity(), "Vincular Perguntas");
					
					getUI().addWindow(pesquisaEditor); 
//				}else{
//					Notify.Show("Você não Possui Permissão para Vincular Pesquisa a um CRM Assunto!", Notify.TYPE_ERROR);
//				}
				
			}
		});
		
		btPesquisa.setEnabled(false); 
		return btPesquisa;
	}
	
	
	public Button BuildbtEditar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
//				{
				
				AssuntosEditor editor = new AssuntosEditor(tbGeneric.getItem(tbGeneric.getValue()), "Editar CRM Assunto");		
				
				editor.addListener(new AssuntosEditor.CRmAssuntoSavedListener() {
					
					@Override
					public void setoresSaved(CRmAssuntoSavedEvent event) {
						if(event.isConfirm()){

							containerAssuntosCrm.commit();
							tbGeneric.focus();
							
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um CRM Assunto"));
						}
					}
				});
				
				editor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tbGeneric.focus();
						refresh();
					}
				});
				
				getUI().addWindow(editor);
				editor.center();
				editor.setModal(true);
				
//				}else{
//					Notify.Show("Você não Possui Permissão para Editar um CRM Assunto!", Notify.TYPE_ERROR);
//				}
				
			}
		});
		btEditar.setEnabled(false);
		
	
		
		return btEditar;
	}

	
	public Button BuildbtExcluir() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
//			if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
//			{
//			
				UsuarioDAO userDAO = new UsuarioDAO();		
				
//				if(userDAO.allowRemove((Integer)tbGeneric.getItem(tbGeneric.getValue()).getItemProperty("id").getValue(), OpusERP4UI.getEmpresa().getId())){
					
					final GenericDialog confirm = new GenericDialog("Confirme para continuar", "Deseja realmente excluir este item?", 
							"Excluir", "Cancelar", "100px", "80px", true, true);
					confirm.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								containerAssuntosCrm.removeItem(tbGeneric.getValue());
								containerAssuntosCrm.commit();
								Notify.Show("Setor Excluído com Sucesso!", Notify.TYPE_ERROR);
								tbGeneric.focus();

								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um CRM Assunto"));
							}
						}
					});
					
					confirm.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tbGeneric.focus();
						}
					});
					
					getUI().addWindow(confirm);
					
//				}else{
//					Notification.show("Atenção", "Não é Possivel Excluir este Setor porque Possui Vinculo com Outros Módulos", Type.ERROR_MESSAGE);
//				}

//				}else{
//					Notify.Show("Você não Possui Permissão para Excluir um CRM Assunto!", Notify.TYPE_ERROR);
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

