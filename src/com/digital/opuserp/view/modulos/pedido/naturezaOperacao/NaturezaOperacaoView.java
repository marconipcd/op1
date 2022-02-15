package com.digital.opuserp.view.modulos.pedido.naturezaOperacao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.NaturezaOperacao;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.pedido.naturezaOperacao.NaturezaOperacaoEditor.NaturezaOperacaoEvent;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class NaturezaOperacaoView extends VerticalLayout {
	
	JPAContainer<NaturezaOperacao> container;
	
	Table tb;
	TextField tfBusca;
	
	private Button btNovo;	
	private Button btEditar;
	private Button btExcluir;
		
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	private ComboBox cbStatus;
	
	public NaturezaOperacaoView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtNovo());
			hlButons.addComponent(BuildbtEditar());		
			hlButons.addComponent(BuildbtExcluir());
						
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(BuildCbStatus());					
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildLbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Visualizar");
			lbLegend.setWidth("285px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
						
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
		}
	}
		
	private ComboBox BuildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		cbStatus.addItem("ATIVO");
		cbStatus.addItem("INATIVO");
		
		cbStatus.select("ATIVO");
		cbStatus.setImmediate(true);
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		
		return cbStatus;
	}
	
	public void refresh_qtd(){
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof NaturezaOperacaoView)
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
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
						
			public void handleAction(Object sender, Object target) {
				btNovo.click();
			}
		};
		return slNovo;
	}
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {			
			
			public void handleAction(Object sender, Object target) {
				btEditar.click();
			}
		};
		return slEditar;
	}
	public JPAContainer<NaturezaOperacao> buildContainer(){
		container = JPAContainerFactory.makeBatchable(NaturezaOperacao.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(cbStatus != null && cbStatus.getValue() != null){	
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue()));		
		}
							
		if(tfBusca.getValue() != null && !tfBusca.getValue().isEmpty() && !tfBusca.getValue().equals("")){
			String s = tfBusca.getValue();
			Object[] collums = tb.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			try {
				Integer cod = Integer.parseInt(s);
				
				for(Object c:collums){		 			
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
						filtros.add(new Like(c.toString(), "%"+cod+"%", false));
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
					}
				}catch(Exception e2){
					
					for(Object c:collums){
						//{"id","P","data_ex","turno","grupo.nome","subgrupo.nome","tipo_subgrupo.nome","cliente.nome_razao","end.cidade","end.bairro","prioridade","tecnico","veiculo_id.cod_veiculo","obs","concentrador","base","plano","material"});
						if(!c.equals("P") && !c.equals("veiculo_id.cod_veiculo") && !c.equals("end.cidade") && !c.equals("end.bairro")
								
								
								&& !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));
						}	
						
					}
				}

			}
			
			container.addContainerFilter(Filters.or(filtros));
		}
	
		return container;
	}
	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
				
				if(colId.equals("id")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}
			
				if(colId.equals("visualizar")){
					if(tb.getItem(rowId).getItemProperty("visualizar").getValue() != null){		
						if(!(boolean)tb.getItem(rowId).getItemProperty("visualizar").getValue()){							
							return "NÃO";
						}else{
							return "SIM";
						}
					}	
				}
									
				
				return super.formatPropertyValue(rowId, colId, property);
			}
		};			
		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setVisibleColumns(new Object[] {"id","descricao","aplicacao","emitir_ecf","visualizar","status_saida","tipo"});
		
		tb.setColumnCollapsed("emitir_ecf", true);			
		//tb.setColumnCollapsed("id", true);				
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("descricao", "Descrição");
		tb.setColumnHeader("aplicacao", "Aplicação");
		tb.setColumnHeader("visualizar", "Visualizar");
		tb.setColumnHeader("emitir_ecf", "Emitir ECF");
		tb.setColumnHeader("status_saida", "Status de saída");
		tb.setColumnHeader("tipo", "Tipo");
				
		//tb.setConverter("id", null);		
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
										
					btExcluir.setEnabled(true);					
					btEditar.setEnabled(true);				
					
				}else{

					btExcluir.setEnabled(false);					
					btEditar.setEnabled(false);
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
					
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
						
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					if(tb.getValue() != null){
						tb.select(tb.getValue());
						btEditar.click();						
					}
				}
			}
		});
				
		return tb;
	}
		
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setInputPrompt("Buscar...");
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}
	
	public void addFilter(String s) {
		container.removeAllContainerFilters();
		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(cbStatus != null && cbStatus.getValue() != null){	
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue()));	
		}
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
			for(Object c:collums){		 			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
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
										
				}
			}catch(Exception e2){
				
				for(Object c:collums){
					//{"id","P","data_ex","turno","grupo.nome","subgrupo.nome","tipo_subgrupo.nome","cliente.nome_razao","end.cidade","end.bairro","prioridade","tecnico","veiculo_id.cod_veiculo","obs","concentrador","base","plano","material"});
					if(!c.equals("P") && !c.equals("veiculo_id.cod_veiculo") && !c.equals("end.cidade") && !c.equals("end.bairro")
							
							
							&& !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
					
				}
			}

		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
					final BeanItem<NaturezaOperacao> item = new BeanItem<NaturezaOperacao>(new NaturezaOperacao());
					
					final NaturezaOperacaoEditor naturezaEditor = new NaturezaOperacaoEditor(item, "Novo Cadastro", true);
					naturezaEditor.addListerner(new NaturezaOperacaoEditor.NaturezaOperacaoListerner() {
						
						
						public void onClose(NaturezaOperacaoEvent event) {
							if(event.isConfirm()){
								
								try{
									container.addEntity(item.getBean());
									container.commit();
									container.refresh();
									
									naturezaEditor.close();
									Notify.Show("Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
									
									//refresh();
								}catch(Exception e){
									e.printStackTrace();
									Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
								}
							}
						}
					});
					
					naturezaEditor.addCloseListener(new Window.CloseListener() {
						
						
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
					
					getUI().addWindow(naturezaEditor);		
				}else{
					Notify.Show("Você não Possui Permissão para Cadastrar",Notify.TYPE_ERROR);
				}
			}
		});

		return btNovo;
	}

	
	public Button BuildbtEditar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Editar"))				
				{
					if(tb.getValue() != null){
						final NaturezaOperacaoEditor encaminharEditor = new NaturezaOperacaoEditor(tb.getItem(tb.getValue()),"Editar", true);
						encaminharEditor.addListerner(new NaturezaOperacaoEditor.NaturezaOperacaoListerner() {
														
							public void onClose(NaturezaOperacaoEvent event) {
								if(event.isConfirm()){
									container.commit();
									container.refresh();
									
									Notify.Show("Editado com Sucesso!", Notify.TYPE_SUCCESS);
									encaminharEditor.close();
								}
							}
						});
						
						encaminharEditor.addCloseListener(new Window.CloseListener() {
														
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(encaminharEditor);
					}
				}else{
					Notify.Show("Você não Possui Permissão para Editar",Notify.TYPE_ERROR);
				}
			}
		});
		btEditar.setEnabled(false);
					
		return btEditar;
	}
		
	public Button BuildbtExcluir() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
						
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
				
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Registro Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								container.removeItem(tb.getValue());
								container.commit();
								
								Notify.Show("Natureza Excluída Com Sucesso!", Notify.TYPE_SUCCESS);
								
								refresh_qtd();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma Natureza de Operacao"));
							}							
						}
					}); 
					
					gDialog.addCloseListener(new Window.CloseListener() {
						
						
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
					
					getUI().addWindow(gDialog);
					
				}else{				
					Notify.Show("Você não Possui Permissão para Excluir",Notify.TYPE_ERROR);				
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

