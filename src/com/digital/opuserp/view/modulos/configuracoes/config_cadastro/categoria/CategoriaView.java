package com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria;

import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CategoriasDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaEditor.CategoriaEvent;
import com.digital.opuserp.view.util.Notify;
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

public class CategoriaView extends VerticalLayout implements GenericView {

	JPAContainer<Categorias> container;
	
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
	
	public CategoriaView(boolean act){		
		
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
			
			getCadastrosPendentes();
		}
	}
	
	public void getCadastrosPendentes(){
		final List<Categorias> categoriasPendentes = CategoriasDAO.getCategoriasPendentes();
		if(categoriasPendentes.size() > 0){						
			GenericDialog gd = new GenericDialog("Existe um ou mais Cadastros Pendentes", "Deseja Recupera-las ?", true, true);
			gd.addListerner(new GenericDialog.DialogListerner() {
				
				@Override
				public void onClose(DialogEvent event) {
					if(event.isConfirm()){
						for(final Categorias c:categoriasPendentes){
							
							final BeanItem<Categorias> newItem = new BeanItem<Categorias>(c);					
							
							CategoriaEditor cEditor = new CategoriaEditor(newItem, "RECUPERADO: Edição Categoria", true);
							cEditor.addListerner(new CategoriaEditor.CategoriaListerner() {
								
								@Override
								public void onClose(CategoriaEvent event) {
									if(event.isConfirm()){
										
										try {
											Categorias cAdd = newItem.getBean();
											cAdd.setStatus("ATIVO");
											
											container.addEntity(cAdd);	
											container.commit();
											tb.focus();
	
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou uma Categoria"));
										} catch (Exception e) {
																											
											Notification.show("ERRO: "+e.getMessage());											
										}
										
									}else{
										CategoriasDAO.remove(c);
									}
								}
							});
						
							cEditor.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									tfBusca.focus();
									//refresh();
								}
							});
						
							OpusERP4UI.getCurrent().getUI().addWindow(cEditor);				
						}
					
					}else{
						for(Categorias c:categoriasPendentes){
							CategoriasDAO.remove(c); 
						}
					}
				}
			});
			
			OpusERP4UI.getCurrent().getUI().addWindow(gd); 
			
			
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
		
		if(c instanceof CategoriaView)
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
	public JPAContainer<Categorias> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Categorias.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
	
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("id", true);				
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome", "Nome");	
		
		tb.setVisibleColumns(new Object[] {"id","nome"});
		
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
		
		tb.setColumnCollapsible("nome", false);
		
		
		tb.sort(new Object[]{"nome"}, new boolean[]{true});
		
		
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
				
			
				
					final Categorias c = CategoriasDAO.save(new Categorias(OpusERP4UI.getEmpresa().getId(), "", "WORK", OpusERP4UI.getUsuarioLogadoUI().getUsername()));										
					
					final BeanItem<Categorias> newItem = new BeanItem<Categorias>(c);					
				
					CategoriaEditor cEditor = new CategoriaEditor(newItem, "Cadastrar Categoria", true);
					cEditor.addListerner(new CategoriaEditor.CategoriaListerner() {
						
						@Override
						public void onClose(CategoriaEvent event) {
							if(event.isConfirm()){
								
								try {
									Categorias cAdd = newItem.getBean();
									cAdd.setStatus("ATIVO");
									
									container.addEntity(cAdd);	
									container.commit();
									tb.focus();
	
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou uma Categoria"));
								} catch (Exception e) {
																									
									Notification.show("ERRO: "+e.getMessage());								
								}
								
							}else{
								CategoriasDAO.remove(c); 
							}
						}
					});
				
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();
							//refresh();
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
			
				
				CategoriaEditor cEditor = new CategoriaEditor(tb.getItem(tb.getValue()), "Editar Categoria", true);
				cEditor.addListerner(new CategoriaEditor.CategoriaListerner() {
					
					@Override
					public void onClose(CategoriaEvent event) {
						if(event.isConfirm()){
							container.commit();			
							
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou uma Categoria"));
						}
					}
				});
				
				cEditor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();
						//refresh();
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
				
			
				
					ClienteDAO cDAO = new ClienteDAO();
					Integer categoria = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());
					
					if (cDAO.categoriaEmUso(OpusERP4UI.getEmpresa(), new Categorias(categoria)) == true){
						
						Notification.show("Categoria em uso não pode ser excluida!");					
					
					}else{					
					
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a Categoria Selecionado?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									container.removeItem(tb.getValue());
									container.commit();
									Notification.show("Categoria Excluída com Sucesso!");
									tb.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma Categoria"));
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
