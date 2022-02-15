package com.digital.opuserp.view.modulos.contratoManutencao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.PlanoManutencaoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.PlanosManutencao;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.contratoManutencao.PlanoManutencaoEditor.PlanoManutencaoEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class PlanoManutencaoView extends VerticalLayout {

	private JPAContainer<PlanosManutencao> container;
	
	private Table tb;
	
	private TextField txtBusca;
	
	private Button btNovo;
	private Button btEditar;
	private Button btExcluir;
	
	private HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	private ShortcutListener slNovo;
	private ShortcutListener slEditar;
	
	private Integer codSubModulo;
	
	private GerenciarModuloDAO gmDAO;
	
	public PlanoManutencaoView(boolean act){
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButtons = new HorizontalLayout();
			hlButtons.addComponent(BuildbtNovo());
			hlButtons.addComponent(BuildbtEditar());
			hlButtons.addComponent(BuildbtExcluir());
			
			addComponent(hlButtons); 
			setComponentAlignment(hlButtons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildTxtBusca());					
				}
	
			});
			
			addComponent(buildTb());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildLbRegistros());
			
			Label lbLegend = new Label("F2 - Novo Cadastro");
			lbLegend.setWidth("250px");
			
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1f);
		}
	}
	
	public void refreshRegistro(){
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	public void refresh(){
		replaceComponent(tb, buildTb());
		setExpandRatio(tb, 1f);
		addFilter(txtBusca.getValue());
		btExcluir.setEnabled(false);
		
		refreshRegistro();
	}
	
	private void buildShortcutEvents(Component c){
		if(c instanceof PlanoManutencaoView){
			btNovo.addShortcutListener(buildShortCurNovo());
		}else{
			if(btNovo != null || slNovo != null){
				btNovo.removeShortcutListener(slNovo);				
			}
		}
	}

	private ShortcutListener buildShortCurNovo() {
		slNovo = new ShortcutListener("Novo", ShortcutAction.KeyCode.F2, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btNovo.click();
			}
		};
		
		return slNovo;
	}
	private ShortcutListener buildShortCurEditar() {
		slEditar = new ShortcutListener("Editar", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btEditar.click();
			}
		};
		
		return slEditar;
	}
	
	private JPAContainer<PlanosManutencao> buildContainer(){
		container = JPAContainerFactory.makeBatchable(PlanosManutencao.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		
		container.sort(new Object[]{"id"}, new boolean[]{true});
		
		return container;
	}

	private void addFilter(String s) {
				
		container.removeAllContainerFilters();
		container.setApplyFiltersImmediately(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
			for(Object c:collums){		 			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
			
		} catch (Exception e) {
			
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date date = sdf.parse(s);
				
				for(Object c:collums){
					
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
						filtros.add(Filters.eq(c.toString(), date));
					}	
					
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
						
				}
			}catch(Exception e2){
				
				for(Object c:collums){
					
					if(container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
					
				}
			}

		}
		
		container.applyFilters();
	}

	private Label buildLbRegistros() {
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}

	private Table buildTb() {
		tb = new Table(null,  buildContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				
				if(colId.equals("valor_mensal") && tb.getItem(rowId).getItemProperty(colId).getValue() != null){					
					return "R$ "+tb.getItem(rowId).getItemProperty(colId).getValue().toString();
				}else if(colId.equals("id")){
						
						if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
							return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
						}					
				} else{
						return super.formatPropertyValue(rowId, colId, property);
				}
				return ""; 
				
			}
		};
		
		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setImmediate(true);
		
		tb.setColumnAlignment("valor_mensal", Align.RIGHT);
		tb.setVisibleColumns(new Object[]{"id", "nome", "valor_mensal", "limite_mensal","numeroAtivos","vigencia"});
		
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("nome", "Nome");
		tb.setColumnHeader("valor_mensal", "Valor");
		tb.setColumnHeader("limite_mensal", "Limite");
		tb.setColumnHeader("numeroAtivos", "Nº Ativos");
		tb.setColumnHeader("vigencia", "Vigência");
		
		tb.setColumnWidth("id", 40);
		
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){					
					btNovo.setEnabled(true);
					btEditar.setEnabled(true);
				}else{
					btNovo.setEnabled(false);
					btEditar.setEnabled(false); 
				}
				
			}
		});
		
		tb.removeStyleName("corrige-tamanho-table");
		tb.addStyleName("corrige-tamanho-table");
		
		
		
		return tb;
	}

	private TextField buildTxtBusca() {
		
		txtBusca = new TextField();
		txtBusca.setWidth("100%");
		txtBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		txtBusca.focus();
		txtBusca.setInputPrompt("Buscar...");
		txtBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		
		return txtBusca;
	}
	
	private Button BuildbtExcluir() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir")){
					
					final EntityItem<PlanosManutencao> pmItem = (EntityItem<PlanosManutencao>)tb.getItem(tb.getValue());
					
					GenericDialog gd = new GenericDialog("Confirme para continuar!", "Você deseja realmente excluir este plano de manutenção ?", true, true);
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								
								boolean check = PlanoManutencaoDAO.checkUsoPlanoManutencao(pmItem.getEntity());
								if(!check){
									boolean checkRemove = PlanoManutencaoDAO.remove(pmItem.getEntity());
									
									if(checkRemove){
										Notify.Show("Plano de Manutenção excluído com sucesso!", Notify.TYPE_SUCCESS);
										container.refresh();
									}
								}else{
									Notify.Show("O Plano de Manutenção Escolhido esta em uso e não pode ser excluído!", Notify.TYPE_ERROR);
								}
							}
						}
					});
					
					getUI().addWindow(gd); 
				}else{
					Notify.Show("Você não Possui Permissão para Excluir um Plano de Manutenção", Notify.TYPE_ERROR);
				}
			}
		});
		return btExcluir;
	}

	private Button BuildbtEditar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Editar")){
						
					final EntityItem<PlanosManutencao> cmItem = (EntityItem<PlanosManutencao>)tb.getItem(tb.getValue());			

					final PlanoManutencaoEditor cmEditor = new PlanoManutencaoEditor(cmItem, "Editar Plano de Manutenção", true);
					cmEditor.addListerner(new PlanoManutencaoEditor.PlanoManutencaoListerner() {
						
						@Override
						public void onClose(PlanoManutencaoEvent event) {
							if(event.isConfirm()){
								PlanosManutencao cm = cmItem.getEntity();
								boolean check = PlanoManutencaoDAO.save(cm);
								
								if(check){
									Notify.Show("Plano de Manutenção alterado com sucesso!", Notify.TYPE_SUCCESS);
									cmEditor.close();
									container.refresh();
								}							
							}
						}
					});
					
					getUI().addWindow(cmEditor);
				}else{
					Notify.Show("Você não Possui Permissão para Editar um Plano de Manutenção", Notify.TYPE_ERROR);
				}
			}
		});
		return btEditar;
	}

	private Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar")){
					final BeanItem<PlanosManutencao> item = new BeanItem<PlanosManutencao>(new PlanosManutencao());
					
					final PlanoManutencaoEditor cmEditor = new PlanoManutencaoEditor(item, "Cadastrar Novo Plano", true);
					cmEditor.addListerner(new PlanoManutencaoEditor.PlanoManutencaoListerner() {
						
						@Override
						public void onClose(PlanoManutencaoEvent event) {
							if(event.isConfirm()){
								
								PlanosManutencao cm = item.getBean();
								cm.setEmpresa(OpusERP4UI.getEmpresa().getId());
								
								container.addEntity(cm);
								container.commit();
							
								
								Notify.Show("Plano de Manutenção cadastrado com sucesso!", Notify.TYPE_SUCCESS);
								cmEditor.close();
							}
						}
					});
					
					getUI().addWindow(cmEditor); 
					
				}else{
					Notify.Show("Você não Possui Permissão para Cadastrar um novo Plano de Manutenção", Notify.TYPE_ERROR);
				}
			}
		});
		return btNovo;
	}
	
	private Integer getCodSubModulo(){
		return codSubModulo;
	}
	public void setCodSubModulo(Integer codSubModulo){
		this.codSubModulo = codSubModulo;
	}
}
