package com.digital.opuserp.view.modulos.acesso.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.BaseDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.LogErrorDAO;
import com.digital.opuserp.domain.Base;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.base.BaseEditor.BaseEvent;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class BaseView extends VerticalLayout implements GenericView {

		//JPA
		JPAContainer<Base> container;
		
		Table tb;
		TextField tfBusca;
		Button btNovo;
		Button btEditar;
		Button btExcluir;
		
		private Label lbRegistros;
		
		ShortcutListener slNovo;
		ShortcutListener slEditar;
		
		GerenciarModuloDAO gmDAO;
		private Integer codSubModulo;
		
		public BaseView(boolean act){
			
			if(act){
			
			gmDAO = new GerenciarModuloDAO();
			setSizeFull();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtTransferencia());
			hlButons.addComponent(BuildbtAceitar());
			hlButons.addComponent(BuildbtCancelar());
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(buildTfbusca());
			
			addComponent(buildTbGeneric());
			addComponent(buildLbRegistros());
			setExpandRatio(tb, 1);
			}
		}

		public Label buildLbRegistros(){
			lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
			return lbRegistros;
		}
		
		public JPAContainer<Base> buildContainer(){
			container = JPAContainerFactory.makeBatchable(Base.class, ConnUtil.getEntity());
			container.setAutoCommit(false);
			container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));			
			container.sort(new Object[]{"nome"}, new boolean[]{true});
			
			return container;
		}
		

		public void buildShortcurEvents(Component c){
			
			if(c instanceof BaseView)
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
			tb = new Table(null, buildContainer()){
				@Override
				protected String formatPropertyValue(Object rowId, Object colId,Property property){
					Object v = property.getValue();
					
					if(v instanceof Date){
						Date date = (Date) v;
						
						int ano = date.getYear()+1900;
						String dia;
						String mes;
						
						if(String.valueOf(date.getDate()).length() == 1){
							dia = "0"+String.valueOf(date.getDate());
						}else{
							dia = String.valueOf(date.getDate());
						}
						
						if(String.valueOf(date.getMonth()+1).length() == 1){
							mes = "0"+String.valueOf(date.getMonth()+1);
						}else{
							mes = String.valueOf(date.getMonth()+1);
						}
						
						return dia+"/"+mes+"/"+ano;						
					}				
					
					return super.formatPropertyValue(rowId, colId, property);
					
				}
			};
			tb.setSizeFull();
			tb.setSelectable(true);
			tb.setColumnCollapsingAllowed(true);
		
			tb.setVisibleColumns(new Object[] {"id","nome","altura","altitude","tipo_torre","tensao","tipo_tensao","bateria","prox_troca","cidade","bairro","situacao"});

			tb.setColumnCollapsed("id", true);
			
			tb.setColumnHeader("id", "Cod");
			tb.setColumnHeader("nome", "Nome");
			tb.setColumnHeader("altura", "Altura");			
			tb.setColumnHeader("altitude", "Altitude");			
			tb.setColumnHeader("tipo_torre", "Tipo Torre");
			tb.setColumnHeader("tensao", "Tensão");
			tb.setColumnHeader("tipo_tensao", "Tipo Tensão");
			tb.setColumnHeader("bateria", "Bateria");
			tb.setColumnHeader("prox_troca", "Vencimento Bateria");
			tb.setColumnHeader("cidade", "Cidade");
			tb.setColumnHeader("bairro", "Bairro");
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
			
			tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
				
				@Override
				public void itemClick(ItemClickEvent event) {
					if(event.isDoubleClick()){
						BaseEditor cEditor = new BaseEditor(tb.getItem(tb.getValue()), "Editar Base", true);
						cEditor.addListerner(new BaseEditor.BaseListerner() {
							
							@Override
							public void onClose(BaseEvent event) {
								if(event.isConfirm()){
									container.commit();
									tfBusca.focus();
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
			container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
				
			
			Object[] collums = tb.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			for(Object c:collums){		 
				
				if(!c.equals("Nº") && !c.equals("Dias") && !c.equals("Carência") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}			
			}
			
			container.addContainerFilter(Filters.or(filtros));
			container.applyFilters();			
			container.sort(new Object[]{"nome"}, new boolean[]{true});
			replaceComponent(lbRegistros, buildLbRegistros());
		}

		@Override
		public Button BuildbtTransferencia() {
			btNovo = new Button("Novo", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
					{
					
						final BeanItem<Base> newItem = new BeanItem<Base>(new Base());
										
						BaseEditor cEditor = new BaseEditor(newItem, "Cadastrar Base", true);
						cEditor.addListerner(new BaseEditor.BaseListerner() {
							
							@Override
							public void onClose(BaseEvent event) {
								if(event.isConfirm()){
									
									try {
										Base cAdd = newItem.getBean();
										cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
										container.addEntity(cAdd);	
										container.commit();


										
										if(tfBusca.getValue() != null){
											addFilter(tfBusca.getValue());
										}
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou uma Base"));
										Notify.Show("Base Cadastrada com Sucesso!", Notify.TYPE_SUCCESS);

									} catch (Exception e) {									
																		
										Notify.Show("Não foi Possivel Salvar Base!", Notify.TYPE_ERROR);
										LogErrorDAO.add(new LogError(null, e.getClass().getName(), e.getClass().getSimpleName(),e.getLocalizedMessage(),
												OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));
									}
									
								}else{
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastramento de Base Cancelado!"));
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
						Notify.Show("Você não Possui Permissão para Cadastrar uma Base", Notify.TYPE_WARNING);
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
					
					BaseEditor cEditor = new BaseEditor(tb.getItem(tb.getValue()), "Editar Base", true);
					cEditor.addListerner(new BaseEditor.BaseListerner() {
						
						@Override
						public void onClose(BaseEvent event) {
							if(event.isConfirm()){
								container.commit();
								
								if(tfBusca.getValue() != null){
									addFilter(tfBusca.getValue());
								}
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou uma Base!"));
	                            Notify.Show("Base Alterada Com Sucesso!", Notify.TYPE_SUCCESS);
							}else{
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Edição de Base Cancelada."));
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
						Notify.Show("Você não Possui Permissão para Editar uma Base", Notify.TYPE_WARNING);
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
					
						final Integer codContratoAcesso = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());
						
						BaseDAO baseDAO = new BaseDAO();						
						if(baseDAO.allowRemove(codContratoAcesso)==false){
							Notify.Show("A Base Selecionada está em Uso!", Notify.TYPE_ERROR);
						}else{
						
						
						
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a Base Selecionada?", true, true);
							gDialog.addListerner(new GenericDialog.DialogListerner() {
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){
										container.removeItem(tb.getValue());
										container.commit();
										
										replaceComponent(lbRegistros, buildLbRegistros());
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma Base!"));
			                            Notify.Show("Base Excluída Com Sucesso!", Notify.TYPE_SUCCESS);

									}
								}
							});
						
						getUI().addWindow(gDialog);
						
						
					  }
					
					}else{
						
						Notify.Show("Você não Possui Permissão para Excluir uma Base!", Notify.TYPE_WARNING);

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