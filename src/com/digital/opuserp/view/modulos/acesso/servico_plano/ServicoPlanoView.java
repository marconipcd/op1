package com.digital.opuserp.view.modulos.acesso.servico_plano;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.RadGroupReply;
import com.digital.opuserp.domain.ServicoPlano;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.planos.PlanosEditor;
import com.digital.opuserp.view.modulos.acesso.planos.PlanosEditor.PlanoEvent;
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

public class ServicoPlanoView extends VerticalLayout implements GenericView {

	JPAContainer<ServicoPlano> container;
	
	Table tb;
	TextField tfBusca;
	ComboBox cbStatus;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	
	public ServicoPlanoView(boolean act){
		
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
		
		if(c instanceof ServicoPlanoView)
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
	
	public JPAContainer<ServicoPlano> buildContainer(){
		container = JPAContainerFactory.makeBatchable(ServicoPlano.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addNestedContainerProperty("contrato_acesso.nome");
		container.sort(new Object[]{"contrato_acesso.nome"}, new boolean[]{true});	
		container.addContainerFilter(Filters.not(Filters.eq("nome", "BLOQUEADO")));
				
		if(cbStatus.getValue().toString() != null){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		}
		return container;
	}
	

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("id", true);
		
				
		tb.setColumnCollapsed("session_timeout", true);
		tb.setColumnCollapsed("keepalive_timeout", true);
		tb.setColumnCollapsed("idle_timeout", true);		
				
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("nome", "Nome");		
		tb.setColumnHeader("contrato_acesso.nome", "Contrato");
		tb.setColumnHeader("rate_limit", "Upload/Download");		
		tb.setColumnHeader("valor", "Valor R$");		
		tb.setColumnHeader("session_timeout", "Session TimeOut");
		tb.setColumnHeader("keepalive_timeout", "KeepAlive TimeOut");
		tb.setColumnHeader("idle_timeout", "Idle TimeOut");
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("qtd_censura", "Censura");
		
		tb.setColumnAlignment("valor", Align.RIGHT);
		
		tb.setVisibleColumns(new Object[] {"id","nome","contrato_acesso.nome","rate_limit","session_timeout","keepalive_timeout","idle_timeout","valor","qtd_censura","status"});
		
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
					
					PlanosEditor cEditor = new PlanosEditor(tb.getItem(tb.getValue()), "Editar Plano de Acesso", true);
					cEditor.addListerner(new PlanosEditor.PlanoListerner() {
						
						@Override
						public void onClose(PlanoEvent event) {
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
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setInputPrompt("Buscar...");
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
		container.addContainerFilter(Filters.not(Filters.eq("nome", "BLOQUEADO")));
		container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		
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
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
		container.sort(new Object[]{"contrato_acesso.nome"}, new boolean[]{true});	
		
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
				final BeanItem<ServicoPlano> newItem = new BeanItem<ServicoPlano>(new ServicoPlano());
								
				PlanosEditor cEditor = new PlanosEditor(newItem, "Cadastrar Plano de Acesso", true);
				cEditor.addListerner(new PlanosEditor.PlanoListerner() {
					
					@Override
					public void onClose(PlanoEvent event) {
						if(event.isConfirm()){
							
								EntityManager em = ConnUtil.getEntity();
							try {
										
								ServicoPlano cAdd = newItem.getBean();
																
								container.addEntity(cAdd);	
								container.commit();
								hlFloat.replaceComponent(lbRegistros, buildLbRegistros());								
								Notify.Show("Plano de Acesso Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou Um Plano"));	
							} catch (Exception e) {								
											
								if(em.getTransaction().isActive()){
									em.getTransaction().rollback();
								}
								container.discard();
								
								e.printStackTrace();			
								Notify.Show("Não foi Possivel Cadastrar o Plano de Acesso!", Notify.TYPE_ERROR);
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Cadastrar Um Plano, Uma Excessão foi Gerada!"));
							}
							
						}else{
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou um Cadastro de Plano"));
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
					Notify.Show("Você não Possui Permissão para Cadastrar um Plano", Notify.TYPE_ERROR);
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
				
				PlanosEditor cEditor = new PlanosEditor(tb.getItem(tb.getValue()), "Editar Plano de Acesso", true);
				cEditor.addListerner(new PlanosEditor.PlanoListerner() {
					
					@Override
					public void onClose(PlanoEvent event) {
						if(event.isConfirm()){
							
							
							EntityManager em = ConnUtil.getEntity();
							try{
							
									container.commit();
									hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
									
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou Um Plano"));
								
							}catch(Exception e){
								
								if(em.getTransaction().isActive()){
									em.getTransaction().rollback();
								}
								
								container.discard();
								
								
								Notify.Show("Não Foi Possivel Editar o Plano de Acesso, Tente Novamente!", Notify.TYPE_ERROR);
								
								e.printStackTrace();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Cadastrara Um Plano, Uma Excessão foi Gerada!"));
								
							}							
							
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
					Notify.Show("Você não Possui Permissão para Editar um Plano!", Notify.TYPE_ERROR);
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
				
				final Integer codPlanoAcesso = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());
				
				PlanoAcessoDAO paDAO = new PlanoAcessoDAO();						
				if(paDAO.allowRemove(codPlanoAcesso)==false){
					Notify.Show("O Plano Selecionado está em Uso!", Notify.TYPE_ERROR);
					
					LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Tentou Excluir um Plano em Uso!"));
				}else{
				
				if(tb.getValue() != null){
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Contrato Selecionado?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									EntityManager em = ConnUtil.getEntity();
									
									try{
										Integer codPlano = (Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue();
										PlanoAcesso plano = em.find(PlanoAcesso.class, codPlano);
										String groupName = plano.getContrato_acesso().getId()+"_"+plano.getNome();
										
										if(plano != null){
										
											Query q = em.createQuery("select rgr from RadGroupReply rgr where " +
													"rgr.groupname = :groupName", RadGroupReply.class);									
											q.setParameter("groupName", groupName);
											
											if(q.getResultList().size() > 0){
												em.getTransaction().begin();
												
												for(RadGroupReply rgr:(List<RadGroupReply>)q.getResultList()){
													em.remove(rgr);
												}
												
												em.getTransaction().commit();
											}
											
											container.removeItem(tb.getValue());
											container.commit();
											hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
											
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu Um Plano"));
										}	
										
									}catch(Exception e){
										if(em.getTransaction().isActive()){
											em.getTransaction().rollback();
										}
										
										container.discard();
										
										Notify.Show("Não Foi Possivel Excluir o Plano de Acesso, Tente Novamente!", Notify.TYPE_ERROR);
										
										e.printStackTrace();
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Excluir Um Plano, uma Excessão foi Gerada"));
									}
								}
							}
						});
					
					getUI().addWindow(gDialog);
				}
				
				
			  }
				
				}else{
					Notify.Show("Você não Possui Permissão para Excluir um Plano!", Notify.TYPE_ERROR);
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
