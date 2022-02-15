package com.digital.opuserp.view.modulos.estoque.tipoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.TipoItemDAO;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.domain.TipoItemProduto;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaView;
import com.digital.opuserp.view.modulos.estoque.tipoItem.TipoItemEditor.TipoItemProdutoEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
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

public class TipoItemView extends VerticalLayout implements GenericView  {

	JPAContainer<TipoItemProduto> container;
	
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
	
	public TipoItemView(boolean act){		
		
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
	public JPAContainer<TipoItemProduto> buildContainer(){
		container = JPAContainerFactory.makeBatchable(TipoItemProduto.class, ConnUtil.getEntity());
		container.setAutoCommit(false);		
			
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
					
		tb.setVisibleColumns(new Object[] {"id","nome"});

		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome", "Nome");
		
		tb.setColumnExpandRatio("nome",1f);
		
		
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
		
			
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()   && tb.getValue() != null){
					
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
		
		
		if(s != null && !s.equals("") && !s.isEmpty()){
			
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.valueOf(s);
			DateTime date = null ;
			
				for(Object c:collums){		 			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}		
			}
			
		} catch (Exception e) {
			
			
			try {				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date date = sdf.parse(s);

					for(Object c:collums){		 
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
							filtros.add(Filters.eq(c.toString(), date));
						}			
					}	
				
			} catch (Exception e2) {
				
				try {
					Float vl = Real.formatStringToDBFloat(s);
									
					for(Object c:collums){		 
						
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Float.class){					   	
							filtros.add(Filters.eq(c.toString(), vl));
						}			
					}	
					
				} catch (Exception e3) {
					
					for(Object c:collums){		 
						
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));
						}	
					}
				}
			}
		}		
		container.addContainerFilter(Filters.or(filtros));
	  }
		container.applyFilters();	
		container.sort(new String[] {"produto.nome"}, new boolean[]{true});
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{				
					final BeanItem<TipoItemProduto> newItem = new BeanItem<TipoItemProduto>(new TipoItemProduto());
					
					final TipoItemEditor tipoItemProduto = new TipoItemEditor(newItem, "Tipo Item Produto", true);				    
					tipoItemProduto.addListerner(new TipoItemEditor.TipoItemProdutoListerner() {
						
						@Override
						public void onClose(TipoItemProdutoEvent event) {
							if(event.isConfirm()){
								container.addEntity(newItem.getBean());
								container.commit();
								
								Notify.Show("Tipo Item cadastrado com sucesso!", Notify.TYPE_SUCCESS);
								
								tipoItemProduto.close();
							}
						}
					});
										
					getUI().addWindow(tipoItemProduto); 
				
				}else{
					Notify.Show("Você não Possui Permissão para Cadastrar um Serial", Notify.TYPE_ERROR);
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
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Editar"))				
				{
				
						if(tb.getValue() != null){
						
							final TipoItemEditor tipoItemEditor = new TipoItemEditor(tb.getItem(tb.getValue()), "Editar Tipo Imte", true);
							tipoItemEditor.addListerner(new TipoItemEditor.TipoItemProdutoListerner() {
								
								@Override
								public void onClose(TipoItemProdutoEvent event) {
									
									if(event.isConfirm()){
										
										EntityItem<TipoItemProduto> entityItem = (EntityItem<TipoItemProduto>)event.getItem();
										TipoItemDAO.save(entityItem.getEntity());
										container.refresh();
										
										tipoItemEditor.close();
										
										Notify.Show("Tipo Item alterado com sucesso!", Notify.TYPE_SUCCESS);
									}
								}
							});
							
							getUI().addWindow(tipoItemEditor);
						}
				}else{
					Notify.Show("Você não Possui Permissão para Editar um Serial", Notify.TYPE_ERROR);
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
													
					
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Tipo Item Selecionado?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm() && tb.getValue() != null){
									 
									Item item = tb.getItem(tb.getValue());
									Integer id = Integer.parseInt(item.getItemProperty("id").getValue().toString());
									
									TipoItemDAO.excluir(id);
									container.refresh();
									
									
									Notify.Show("Tipo Item excluído com sucesso!", Notify.TYPE_SUCCESS);									
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
					
				}else{					
					Notify.Show("Você não Possui Permissão para Excluir um Serial", Notify.TYPE_ERROR);
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
