package com.digital.opuserp.view.modulos.financeiro.formasPgto;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.financeiro.formasPgto.FormasPgtoEditor.FormaPgtoEvent;
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

public class FormasPgtoView extends VerticalLayout implements GenericView  {

	JPAContainer<FormasPgto> container;
	
	Table tb;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	
	public Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	HorizontalLayout hlFloat;
	
	
	
	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	
	public FormasPgtoView(boolean act){
		
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
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof FormasPgtoView)
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
	
	public JPAContainer<FormasPgto> buildContainer(){
		container = JPAContainerFactory.makeBatchable(FormasPgto.class, ConnUtil.getEntity());
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
										
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome", "Nome");	
		tb.setColumnHeader("desc_max", "Desconto(%)");		
		tb.setColumnHeader("tipo_titulo", "Tipo Título");
				
		
		tb.setVisibleColumns(new Object[] {"id","nome","desc_max","tipo_titulo"});
				
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
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final BeanItem<FormasPgto> newItem = new BeanItem<FormasPgto>(new FormasPgto());
								
				FormasPgtoEditor cEditor = new FormasPgtoEditor(newItem, "Cadastrar Forma de Pagamento", true);
				cEditor.addListerner(new FormasPgtoEditor.FormaPgtoListerner() {
					
					@Override
					public void onClose(FormaPgtoEvent event) {
						if(event.isConfirm()){							
								
							try {								
																
								FormasPgto cAdd = newItem.getBean();
								cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());						
								container.addEntity(cAdd);	
								container.commit();
								
								Notification.show("Forma de Pgto. Cadastrado com Sucesso!");
							} catch (Exception e) {								
															
								container.discard();
								
								e.printStackTrace();
								Notification.show("Não foi Possivel Cadastrar a Forma de Pgto. ");								
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
						tb.focus();									
					}
				});
				
				
				
				getUI().addWindow(cEditor);
			}
		});
		return btNovo;
	}

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				FormasPgtoEditor cEditor = new FormasPgtoEditor(tb.getItem(tb.getValue()), "Editar Forma de Pagamento", true);
				cEditor.addListerner(new FormasPgtoEditor.FormaPgtoListerner() {
					
					@Override
					public void onClose(FormaPgtoEvent event) {
						if(event.isConfirm()){
							
														
							try{
																
									container.commit();
								
							}catch(Exception e){
								
								
								container.discard();
								
								Notification.show("Não Foi Possivel Editar a Forma de Pagamento Selecionada, Tente Novamente!");
								
								e.printStackTrace();
								
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
				
						if(tb.getValue() != null){
							
								GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a Forma de Pagamento Selecionada?", true, true);
								gDialog.addListerner(new GenericDialog.DialogListerner() {
									
									@Override
									public void onClose(DialogEvent event) {
										if(event.isConfirm()){										
											
											try{																					
													container.removeItem(tb.getValue());
													container.commit();
													
											}catch(Exception e){
												
												
												container.discard();
												
												Notification.show("Não Foi Possivel Excluir a Forma de Pagamento Selecionada, Tente Novamente!");
												
												e.printStackTrace();
											}
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
