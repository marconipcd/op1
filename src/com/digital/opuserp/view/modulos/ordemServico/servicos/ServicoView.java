package com.digital.opuserp.view.modulos.ordemServico.servicos;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.ordemServico.servicos.ServicoEditor.ServicoEvent;
import com.digital.opuserp.view.util.Notify;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class ServicoView extends VerticalLayout implements GenericView{

	JPAContainer<Servico> container;
	
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
	
	public ServicoView(boolean act){
		
		
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
				addComponent(buildCbStatus());
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
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ServicoView)
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
	public JPAContainer<Servico> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Servico.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addNestedContainerProperty("grupo.nome");
		
		if(cbStatus != null && !cbStatus.getValue().equals("TODOS")){
			if(cbStatus.getValue().equals("ATIVO")){
				container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
			}
			if(cbStatus.getValue().equals("INATIVO")){
				container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
			}
		}
	
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){

			
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(tb.getItem(rowId).getItemProperty(colId).getValue() != null  && colId.equals("valor_venda")){
					
					
					return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
				}				
				
				if(colId.equals("fracionar") && tb.getItem(rowId).getItemProperty(colId).getValue().toString().equals("1")){
					return "SIM";
				}
				
				if(colId.equals("fracionar") && tb.getItem(rowId).getItemProperty(colId).getValue().toString().equals("0")){
					return "NAO";
				}
				
				return super.formatPropertyValue(rowId, colId, property);
			
				
			}
				
			
		};		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("id", true);				
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome", "Nome");	
		tb.setColumnHeader("valor_venda", "Valor Venda");	
		tb.setColumnHeader("grupo.nome", "Grupo");	
		tb.setColumnHeader("status", "Status");	
		tb.setColumnHeader("fracionar", "Fracionar");	
		tb.setColumnHeader("descricao", "Descrição");	
		
		tb.setColumnAlignment("valor_venda", Align.RIGHT);
		
		tb.setVisibleColumns(new Object[] {"id","nome","descricao","grupo.nome","valor_venda","fracionar","status"});
		
		tb.setColumnCollapsed("fracionar", true);
		tb.setColumnCollapsed("descricao", true);
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {
                
                if (propertyId == null)
                    return "row-header-default"; 
               
                Item item = source.getItem(itemId);
            
                if(item.getItemProperty("status").getValue() != null){
	                String status = item.getItemProperty("status").getValue().toString();
	               
	                if(status.equals("INATIVO")){
	                    return "row-header-encerrado";
	                }else{
	                    return "row-header-default"; 
	                }
                
                }else{
                	 return "row-header-default";
                }

               
            }
        });
		
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
			}
		});
		
		tb.setColumnCollapsible("id", false);
		
		
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
	
	private ComboBox cbStatus;
	private ComboBox buildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.addItem("TODOS");
		cbStatus.addItem("ATIVO");
		cbStatus.addItem("INATIVO");
		cbStatus.setImmediate(true);
		
		cbStatus.select("ATIVO");
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
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
		
		if(cbStatus != null && !cbStatus.getValue().equals("TODOS")){
			if(cbStatus.getValue().equals("ATIVO")){
				container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
			}
			if(cbStatus.getValue().equals("INATIVO")){
				container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
			}
		}

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
				if(!c.toString().equals("valor_custo") && !c.toString().equals("valor_venda") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}			
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		container.sort(new Object[]{"nome"}, new boolean[]{true});
		
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
		container.sort(new String[]{"nome"},new boolean[]{false});
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
   				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
				final BeanItem<Servico> newItem = new BeanItem<Servico>(new Servico());
								
				final ServicoEditor cEditor = new ServicoEditor(newItem, "Cadastrar Servico", true);
				cEditor.addListerner(new ServicoEditor.ServicoListerner() {
					
					@Override
					public void onClose(ServicoEvent event) {
						if(event.isConfirm()){
							
							try {
								Servico cAdd = newItem.getBean();
								cAdd.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								container.addEntity(cAdd);	
								container.commit();
								
								Notify.Show("Serviço Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
								tb.focus();
								
								cEditor.close();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Serviço"));
							} catch (Exception e) {
								e.printStackTrace();															
								Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);							
							}
							
						}else{
//							LogDAO.addLog(new Log(null, "Ação de Cadastro de Cliente Cancelada!", OpusERP4_ServerUI.getUsuarioLogadoUI().getUsername(), null));
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
					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um Serviço", Type.ERROR_MESSAGE);
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
				
					final ServicoEditor cEditor = new ServicoEditor(tb.getItem(tb.getValue()), "Editar Servico", true);
					cEditor.addListerner(new ServicoEditor.ServicoListerner() {
						
						@Override
						public void onClose(ServicoEvent event) {
							if(event.isConfirm()){
								container.commit();			
								Notify.Show("Serviço Editado com Sucesso!", Notify.TYPE_SUCCESS);
								cEditor.close();
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Serviço"));
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
					Notify.Show("Você não Possui Permissão para Editar um Serviço", Notify.TYPE_ERROR);
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
					
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Serviço Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								container.removeItem(tb.getValue());
								container.commit();
								
								Notify.Show("Serviço Excluído com Sucesso!", Notify.TYPE_SUCCESS);								
								tb.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Serviço"));
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
					Notify.Show("Você não Possui Permissão para Excluir um Serviço", Notify.TYPE_ERROR);
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