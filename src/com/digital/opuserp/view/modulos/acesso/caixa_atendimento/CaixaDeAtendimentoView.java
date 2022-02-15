package com.digital.opuserp.view.modulos.acesso.caixa_atendimento;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AcessoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.SwithDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Swith;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.caixa_atendimento.CaixaAtendimentoEditor.SwithEvent;
import com.digital.opuserp.view.modulos.acesso.caixa_atendimento.CaixaEdicaoEmMassaEditor.CaixaEdicaoEmMassaEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class CaixaDeAtendimentoView extends VerticalLayout{

		JPAContainer<Swith> container;
		
		Table tb;
		TextField tfBusca;
		Button btNovo;
		Button btCopiar;
		Button btEditar;
		Button btExcluir;
					
		private Label lbRegistros;
		HorizontalLayout hlFloat;
		
		ShortcutListener slNovo;
		ShortcutListener slEditar;
		
		GerenciarModuloDAO gmDAO;
		private Integer codSubModulo;
		
		public CaixaDeAtendimentoView(boolean act){
			
			if(act){
			setSizeFull();

			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();		
			hlButons.addComponent(BuildbtCopiar());
			hlButons.addComponent(BuildbtNovo());
			hlButons.addComponent(BuildbtEditar());
			hlButons.addComponent(BuildbtExcluir());
							
								
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
						
			addComponent(buildTfbusca());
			
			addComponent(buildTbGeneric());
//			addComponent(buildLbRegistros());
			
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
			container.addNestedContainerProperty("contrato_monitoramento.id");
						
			container.sort(new Object[]{"identificacao"}, new boolean[]{true});			
								
			return container;
		}

		
		public Table buildTbGeneric() {
			tb = new Table(null, buildContainer());
			tb.setMultiSelect(true);
			tb.setSizeFull();
			tb.setSelectable(true);
			tb.setColumnCollapsingAllowed(true);
			tb.setVisibleColumns(new Object[] {"id","identificacao","olt","pon","sinal_db","concentrador.identificacao","modelo","contrato_monitoramento.id","ip_monitoramento","endereco","numero","referencia","situacao"});
			tb.setColumnCollapsed("id", true);
			tb.setColumnCollapsed("referencia", true);
						
			tb.setColumnHeader("contrato_monitoramento.id", "Contrato");
			tb.setColumnHeader("ip_monitoramento", "Ip Monitoramento");
			tb.setColumnHeader("id", "Cod");
			tb.setColumnHeader("concentrador.identificacao", "Concentrador");
			tb.setColumnHeader("identificacao", "Indentificação");
			tb.setColumnHeader("modelo", "Modelo");
			tb.setColumnHeader("pon", "PON");
			tb.setColumnHeader("sinal_db", "Sinal Db");
			tb.setColumnHeader("olt", "OLT");
			tb.setColumnHeader("endereco", "Endereço");
			tb.setColumnHeader("numero", "Número");		
			tb.setColumnHeader("referencia", "Referencia");
			tb.setColumnHeader("situacao", "Situação");		
			
			
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
						
						CaixaAtendimentoEditor cEditor = new CaixaAtendimentoEditor(tb.getItem(tb.getValue()), "Editar Caixa de Atendimento", true);
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

		
		public void addFilter(String s) {
			
			container.removeAllContainerFilters();
			container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
			
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
//			container.sort(new String[]{"id"},new boolean[]{false});
		}
		
		
		public Button BuildbtNovo() {
			btNovo = new Button("Novo", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
					{
						
						
					
					final BeanItem<Swith> newItem = new BeanItem<Swith>(new Swith());
									
					CaixaAtendimentoEditor cEditor = new CaixaAtendimentoEditor(newItem, "Cadastrar Caixa de Atendimento", true);
					cEditor.addListerner(new CaixaAtendimentoEditor.SwithListerner() {
						
						@Override
						public void onClose(SwithEvent event) {
							if(event.isConfirm()){
								
								try {
									Swith cAdd = newItem.getBean();
									cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
									container.addEntity(cAdd);	
									container.commit();
									
									if(event.getItem().getItemProperty("contrato_monitoramento").getValue() != null &&
											event.getItem().getItemProperty("ip_monitoramento").getValue() != null){
										
										AcessoCliente contrato = (AcessoCliente)event.getItem().getItemProperty("contrato_monitoramento").getValue();
										contrato.setEndereco_ip(event.getItem().getItemProperty("ip_monitoramento").getValue().toString());
										
										boolean check1 = AcessoDAO.save(contrato);
										boolean check2 = AcessoDAO.alterarIpFixo(contrato);
										
										if(check1 && check2){
											Notify.Show("Ip de monitoramento fixado em contrato!", Notify.TYPE_SUCCESS);
										}else{
											Notify.Show("Erro ao tentar fixar ip de monitoramento em contrato", Notify.TYPE_ERROR);
										}
									}
									
									hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
									Notify.Show("Swith Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou Uma Caixa de Atendimento"));
								} catch (Exception e) {
											
									Notify.Show("ERRO: "+e.getMessage(), Notify.TYPE_SUCCESS);
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu Cadastrar Uma Caixa de Atendimento"));
								}
								
							}else{
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou um Cadastro de uma Caixa de Atendimento"));
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
						Notify.Show("Você não Possui Permissão para Cadastrar uma Caixa de Atendimento", Notify.TYPE_ERROR);
					}
				}
			});
			return btNovo;
		}
		
		
		public Button BuildbtCopiar() {
			btCopiar = new Button("Copiar", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Copiar"))				
					{
						
						final Set<Object> selecteds = (Set<Object>)tb.getValue();						
						if(selecteds.size() == 1){
						
							EntityItem<Swith> entityItem = (EntityItem<Swith>)tb.getItem(selecteds.toArray()[0]);
							Swith caixa = entityItem.getEntity();
							caixa.setId(null); 
							final BeanItem<Swith> newItem = new BeanItem<Swith>(caixa);
											
							CaixaAtendimentoEditor cEditor = new CaixaAtendimentoEditor(newItem, "Copiar Caixa de Atendimento", true);
							cEditor.addListerner(new CaixaAtendimentoEditor.SwithListerner() {
								
								@Override
								public void onClose(SwithEvent event) {
									if(event.isConfirm()){
										
										try {
											Swith cAdd = newItem.getBean();
											cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
											cAdd.setContrato_monitoramento(null);
											cAdd.setIp_monitoramento(null); 
											
											container.addEntity(cAdd);	
											container.commit();
											hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
											Notify.Show("Caixa copiada com Sucesso!", Notify.TYPE_SUCCESS);
											
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Copiou Uma Caixa de Atendimento"));
										} catch (Exception e) {
													
											Notify.Show("ERRO: "+e.getMessage(), Notify.TYPE_SUCCESS);
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Consegiu Copiar Uma Caixa de Atendimento"));
										}
										
									}else{
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou uma copia de uma Caixa de Atendimento"));
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
							Notify.Show("Não é possível copiar mais de uma caixa de atendimento ao mesmo tempo, selecione apenas uma!", Notify.TYPE_NOTICE);	
						}
					
					}else{
						Notify.Show("Você não Possui Permissão para Copiar uma Caixa de Atendimento", Notify.TYPE_ERROR);
					}
				}
			});
			return btCopiar;
		}

		
		public Button BuildbtEditar() {
			btEditar = new Button("Editar", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
					{
						final Set<Object> selecteds = (Set<Object>)tb.getValue();						
						if(selecteds.size() == 1){
							
							CaixaAtendimentoEditor cEditor = new CaixaAtendimentoEditor(tb.getItem(selecteds.toArray()[0]), "Editar Caixa de Atendimento", true);
							cEditor.addListerner(new CaixaAtendimentoEditor.SwithListerner() {
								
								@Override
								public void onClose(SwithEvent event) {
									if(event.isConfirm()){
										container.commit();
										
										if(event.getItem().getItemProperty("contrato_monitoramento").getValue() != null &&
												event.getItem().getItemProperty("ip_monitoramento").getValue() != null){
											
											AcessoCliente contrato = (AcessoCliente)event.getItem().getItemProperty("contrato_monitoramento").getValue();
											contrato.setEndereco_ip(event.getItem().getItemProperty("ip_monitoramento").getValue().toString());
											
											boolean check1 = AcessoDAO.save(contrato);
											boolean check2 = AcessoDAO.alterarIpFixo(contrato);
											
											if(check1 && check2){
												Notify.Show("Ip de monitoramento fixado em contrato!", Notify.TYPE_SUCCESS);
											}else{
												Notify.Show("Erro ao tentar fixar ip de monitoramento em contrato", Notify.TYPE_ERROR);
											}
										}
										
										hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou Uma Caixa de Atendimento"));
									}else{
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou uma Edição de uma Caixa de Atendimento"));
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
							//Edição da Situação
							
							CaixaEdicaoEmMassaEditor caixaEdiMass = new CaixaEdicaoEmMassaEditor("Edição em massa", true);
							caixaEdiMass.addListerner(new CaixaEdicaoEmMassaEditor.CaixaEdicaoEmMassaListerner() {
								
								@Override
								public void onClose(CaixaEdicaoEmMassaEvent event) {
									if(event.isConfirm()){
										
										for (Object item : selecteds.toArray()) {
											EntityItem<Swith> entityItem = (EntityItem<Swith>)tb.getItem(item);
											
											Swith s = entityItem.getEntity();
											s.setSituacao(event.getSituacao());
											
											boolean check = SwithDAO.atualizar(s);
											if(!check){
												Notify.Show("Problema durante a atualização em massa!", Notify.TYPE_ERROR);
											}
										}
										
										container.refresh();					
										Notify.Show("Atualização concluída", Notify.TYPE_SUCCESS);
									}									
								}
							});
							
							getUI().addWindow(caixaEdiMass); 
							
						}
					
					}else{
						Notify.Show("Você não Possui Permissão para Editar uma Caixa de Atendimento", Notify.TYPE_ERROR);
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
						
						final Set<Object> selecteds = (Set<Object>)tb.getValue();						
						if(selecteds.size() == 1){
							
								//TODO Fazer Verificação se Swith está em Uso
								EntityItem<Swith> itemSwith = (EntityItem<Swith>)tb.getItem(selecteds.toArray()[0]);
								boolean allowDelete = SwithDAO.allowDelete(itemSwith.getEntity());
								
								if(allowDelete){
								
									final Integer codConcentrador = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());
													
									GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a Caixa de Atendimento Selecionado?", true, true);
										gDialog.addListerner(new GenericDialog.DialogListerner() {
											
											
											@Override
											public void onClose(DialogEvent event) {
												if(event.isConfirm()){
													container.removeItem(tb.getValue());
														container.commit();			
														hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
														
														LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu Uma Caixa de Atendimento"));
												}
											}
										});
									
									getUI().addWindow(gDialog);
								}else{
									Notify.Show("A Caixa de Atendimento selecionada está em uso e não pode ser Excluído!", Notify.TYPE_WARNING);
								}
						}else{
							Notify.Show("Não é possível excluir mais de um caixa simultaneamente, selecione apenas uma caixa para excluir!", Notify.TYPE_NOTICE);
						}
					}else{
						Notify.Show("Você não Possui Permissão para Excluir uma Caixa de Atendimento", Notify.TYPE_ERROR);
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
