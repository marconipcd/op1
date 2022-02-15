package com.digital.opuserp.view.modulos.estoque.tabelaPreco;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.TabelasPreco;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.estoque.tabelaPreco.TabelaPrecoEditor.TabelaPrecoEvent;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class TabelaPrecoView extends VerticalLayout implements GenericView {

	private JPAContainer<TabelasPreco> container;
	
	private Table tb;
	private TextField tfBusca;
	private Button btNovo;
	private Button btEditar;
	private Button btExcluir;
	private Label lbRegistros;

	private HorizontalLayout hlFloat;	
	
	private ShortcutListener slNovo;
	private ShortcutListener slEditar;
	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	
	public TabelaPrecoView(boolean act){		
		
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
		
		if(c instanceof TabelaPrecoView)
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
	public JPAContainer<TabelasPreco> buildContainer(){
		container = JPAContainerFactory.makeBatchable(TabelasPreco.class,ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));	
	
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
		tb.setColumnHeader("desconto", "Desconto");	
		
		tb.setVisibleColumns(new Object[] {"id","nome","desconto"});
		
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
					btEditar.click();
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
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));	
		container.addContainerFilter(new Like("nome", "%"+s+"%", false));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
					
					final BeanItem<TabelasPreco> newItem = new BeanItem<TabelasPreco>(new TabelasPreco());
									
					final TabelaPrecoEditor cEditor = new TabelaPrecoEditor(newItem, "Cadastrar Tabela de Preço", true);
					cEditor.addListerner(new TabelaPrecoEditor.TabelaPrecoListerner() {
						
						@Override
						public void onClose(TabelaPrecoEvent event) {
							if(event.isConfirm()){
								
								try {
									TabelasPreco cAdd = newItem.getBean();
									cAdd.setEmpresa(OpusERP4UI.getEmpresa());
									container.addEntity(cAdd);	
									container.commit();
									cEditor.close();
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou uma Tabela de Preço"));
									
									Notify.Show("Tabela de Preço Cadastrada Com Sucesso!", Notify.TYPE_SUCCESS);
								} catch (Exception e) {
									e.printStackTrace();															
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);								
								}
								
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
				
				}else{					
					Notify.Show("Você não Possui Permissão para Cadastrar uma Tabela de Preço", Notify.TYPE_ERROR);
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
				
					final TabelaPrecoEditor cEditor = new TabelaPrecoEditor(tb.getItem(tb.getValue()), "Editar Tabela de Preço", true);
					cEditor.addListerner(new TabelaPrecoEditor.TabelaPrecoListerner() {
						
						@Override
						public void onClose(TabelaPrecoEvent event) {
							if(event.isConfirm()){
								container.commit();			
								
								cEditor.close();
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou uma Tabela de Preço"));

								Notify.Show("Tabela de Preço Alterada Com Sucesso!", Notify.TYPE_SUCCESS);
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
				}else{					
					Notify.Show("Você não Possui Permissão para Editar uma Tabela de Preço", Notify.TYPE_ERROR);
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
				
					boolean allow = false;
					if (allow){
						
						Notify.Show("Tabela de Preço em Uso não pode ser Excluida!", Notify.TYPE_NOTICE);					
					
					}else{					
					
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a Table de Preço Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								container.removeItem(tb.getValue());
								container.commit();
								Notify.Show("Tabela de Preço Excluída com Sucesso!", Notify.TYPE_SUCCESS);
								tb.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma Tabela de Preço"));
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
				}else{
					Notify.Show("Você não Possui Permissão para Excluir uma Tabela de Preço", Notify.TYPE_ERROR);
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
