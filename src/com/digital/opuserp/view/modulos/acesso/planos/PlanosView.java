package com.digital.opuserp.view.modulos.acesso.planos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.RadGroupReply;
import com.digital.opuserp.domain.RadUserGgroup;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
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

public class PlanosView extends VerticalLayout implements GenericView {

	JPAContainer<PlanoAcesso> container;
	
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
	
	public PlanosView(boolean act){
		
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
		
		if(c instanceof PlanosView)
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
	
	public JPAContainer<PlanoAcesso> buildContainer(){
		container = JPAContainerFactory.makeBatchable(PlanoAcesso.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addNestedContainerProperty("contrato_acesso.nome");
		//container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.EMPRESA_SELECIONADA.getId()));
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
		
		//tb.setColumnExpandRatio("contrato_acesso.nome", 1);
		
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
	
	
	public ComboBox buildStatus(){
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
				
				final BeanItem<PlanoAcesso> newItem = new BeanItem<PlanoAcesso>(new PlanoAcesso());
								
				PlanosEditor cEditor = new PlanosEditor(newItem, "Cadastrar Plano de Acesso", true);
				cEditor.addListerner(new PlanosEditor.PlanoListerner() {
					
					@Override
					public void onClose(PlanoEvent event) {
						if(event.isConfirm()){
							
							EntityManager em = ConnUtil.getEntity();
							
							try {
								
																
								PlanoAcesso cAdd = newItem.getBean();
								RadGroupReply radGroupreply = new RadGroupReply();
								
								
								em.getTransaction().begin();
								em.persist(new RadGroupReply(null, cAdd.getContrato_acesso().getId().toString()+"_"+cAdd.getNome(), 
										"Idle-Timeout", "=", cAdd.getIdle_timeout()));
								
								em.persist(new RadGroupReply(null, cAdd.getContrato_acesso().getId().toString()+"_"+cAdd.getNome(), 
										"Session-Timeout", "=", cAdd.getSession_timeout()));
								
								em.persist(new RadGroupReply(null, cAdd.getContrato_acesso().getId().toString()+"_"+cAdd.getNome(), 
										"Mikrotik-Rate-Limit", "=", cAdd.getRate_limit()+" 0/0 0/0 0/0 "+cAdd.getPrioridade()+" "+cAdd.getMin_rate_limit()));
								
								
								em.getTransaction().commit();
								
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
								
									Integer codPlano = (Integer)event.getItem().getItemProperty("id").getValue();
									String IdleTimeOutValue = event.getItem().getItemProperty("idle_timeout").getValue().toString();
									String SessionTimeOutValue = event.getItem().getItemProperty("session_timeout").getValue().toString();
									String MikrotikRateLimitValue = event.getItem().getItemProperty("rate_limit").getValue().toString()+" 0/0 0/0 0/0 "+event.getItem().getItemProperty("prioridade").getValue().toString()+" "+event.getItem().getItemProperty("min_rate_limit").getValue().toString();
									ContratosAcesso contrato = (ContratosAcesso)event.getItem().getItemProperty("contrato_acesso").getValue();
									String GroupNameValue = contrato.getId().toString()+"_"+event.getItem().getItemProperty("nome").getValue().toString();
									
									PlanoAcesso plano = em.find(PlanoAcesso.class, codPlano);
									
									if(plano != null){
													
										em = ConnUtil.getEntity();
										
										em.getTransaction().begin();
										
										String groupName = plano.getContrato_acesso().getId().toString()+"_"+plano.getNome();
										
										//Alterar Clientes
										Query qRadUserGroup = em.createQuery("select rug from RadUserGgroup rug where rug.groupname=:grupoAntigo", RadUserGgroup.class);
										qRadUserGroup.setParameter("grupoAntigo", groupName);
										
										List<RadUserGgroup> listGroup = qRadUserGroup.getResultList();
										for (RadUserGgroup radUserGroup: listGroup) {
											radUserGroup.setGroupname(GroupNameValue);
											em.merge(radUserGroup);
										}
										//Alterar Clientes
										
										//Idle TimeOut
										Query qRadGroupReplyIdleTimeOut = em.createQuery("select rgr from RadGroupReply rgr where " +
												"rgr.groupname = :groupName and rgr.attribute = 'Idle-Timeout' ", RadGroupReply.class);									
										qRadGroupReplyIdleTimeOut.setParameter("groupName", groupName);
										
										//Atualiza Ilde TimeOut
										if(qRadGroupReplyIdleTimeOut.getResultList().size() == 1){
											RadGroupReply rgr = (RadGroupReply)qRadGroupReplyIdleTimeOut.getSingleResult();
											rgr.setValue(IdleTimeOutValue);
											rgr.setGroupname(GroupNameValue);
											em.merge(rgr);
										}else{
											for(RadGroupReply rgr:(List<RadGroupReply>)qRadGroupReplyIdleTimeOut.getResultList()){
												em.remove(rgr);
											}
											em.persist(new RadGroupReply(null, GroupNameValue,"Idle-Timeout", "=", IdleTimeOutValue));
										}
										
										//Session TimeOut
										Query qRadGroupSessionTimeOut = em.createQuery("select rgr from RadGroupReply rgr where " +
												"rgr.groupname = :groupName and rgr.attribute = 'Session-Timeout' ", RadGroupReply.class);
										qRadGroupSessionTimeOut.setParameter("groupName", groupName);
										
										//Atualiza Session TimeOut
										if(qRadGroupSessionTimeOut.getResultList().size() == 1){
											RadGroupReply rgr = (RadGroupReply)qRadGroupSessionTimeOut.getSingleResult();
											rgr.setValue(SessionTimeOutValue);
											rgr.setGroupname(GroupNameValue);
											em.merge(rgr);
										}else{
											for(RadGroupReply rgr:(List<RadGroupReply>)qRadGroupSessionTimeOut.getResultList()){
												em.remove(rgr);
											}
											em.persist(new RadGroupReply(null, GroupNameValue,"Session-Timeout", "=", IdleTimeOutValue));
										}
										
										//Mikrotik Rate Limit
										Query qRadGroupMikrotikRateLimit = em.createQuery("select rgr from RadGroupReply rgr where " +
												"rgr.groupname = :groupName and rgr.attribute = 'Mikrotik-Rate-Limit' ", RadGroupReply.class);
										qRadGroupMikrotikRateLimit.setParameter("groupName", groupName);
										
										//Atualiza Rate Limit
										if(qRadGroupMikrotikRateLimit.getResultList().size() == 1){
											RadGroupReply rgr = (RadGroupReply)qRadGroupMikrotikRateLimit.getSingleResult();
											rgr.setValue(MikrotikRateLimitValue);
											rgr.setGroupname(GroupNameValue);
											em.merge(rgr);
										}else{
											for(RadGroupReply rgr:(List<RadGroupReply>)qRadGroupMikrotikRateLimit.getResultList()){
												em.remove(rgr);
											}
											em.persist(new RadGroupReply(null, GroupNameValue,"Mikrotik-Rate-Limit", "=", IdleTimeOutValue));
										}
										
										
										em.getTransaction().commit();
										container.commit();
										hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
										
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou Um Plano"));
									}
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
