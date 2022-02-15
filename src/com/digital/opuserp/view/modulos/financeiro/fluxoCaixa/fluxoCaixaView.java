package com.digital.opuserp.view.modulos.financeiro.fluxoCaixa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.FluxoCaixa;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.financeiro.haver.HaverView;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class fluxoCaixaView extends VerticalLayout implements GenericView {

	private JPAContainer<FluxoCaixa> container;
	
	private Table tb;
	private TextField tfBusca;
	private Button btNovo;
	private Button btExcluir;
	private Label lbRegistros;

	private HorizontalLayout hlFloat;	
	
	private ShortcutListener slNovo;
	private ShortcutListener slEditar;
	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	
	public fluxoCaixaView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			//hlButons.addComponent(BuildbtNovoHaver());
			
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					//addComponent(buildCbStatus());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildlbRegistros());
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
		hlFloat.replaceComponent(lbRegistros, buildlbRegistros());
		
		
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof HaverView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
//			if(btEditar != null || slEditar != null){
//				btDetalhamento.removeShortcutListener(slEditar);			
//			}
		}
	}
	
	public Label buildlbRegistros(){
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
	
	public JPAContainer<FluxoCaixa> buildContainer(){
		container = JPAContainerFactory.makeBatchable(FluxoCaixa.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		///container.addNestedContainerProperty("cliente.doc_cpf_cnpj");
		//container.addNestedContainerProperty("cliente.nome_razao");
		//container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
	
		//if(cbStatus != null && cbStatus.getValue() != null){
		//	container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		//}

		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
			
				if (v instanceof Date) {
				         Date dateValue = (Date) v;

				      return new SimpleDateFormat("dd/MM/yyyy").format(dateValue);

				}
				
				if(colId.equals("valor_total")){
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null) {										
					return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
		
					}
				}	
				
				return super.formatPropertyValue(rowId, colId, property);
			}	
		};		
		

		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);

		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("cliente.doc_cpf_cnpj", "CPF");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");		
		tb.setColumnHeader("data_alteracao", "Data Alteração");
		tb.setColumnHeader("valor_total", "Valor");
		
		tb.setColumnHeader("status", "Status");
		
		
		tb.setColumnAlignment("valor_total", Align.RIGHT);	
		tb.setVisibleColumns(new Object[] {"id","cliente.doc_cpf_cnpj","cliente.nome_razao","valor_total","data_alteracao","status"});
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
				
				}else{
					

				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {
                
                if (propertyId == null)
                    return "row-header-default"; 
               
                Item item = source.getItem(itemId);
                   
                	if(item.getItemProperty("status").getValue() != null){
                		String status = item.getItemProperty("status").getValue().toString();
	               
		                if(status.equals("UTILIZADO")){
		                    return "row-header-bloqueado"; 
		                }else{
		                	return "row-header-osi-encaminhado";
		                }
	                }
                	
                	return "row-header-default"; 
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
				refresh();
				addFilter(event.getText());
			}
		});
		return tfBusca;
		
	}

	

	@Override
	public void addFilter(String s) {
		container.removeAllContainerFilters();

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
			
			
			for(Object c:collums){		 
				
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}			
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildlbRegistros());
	}
	
	
	
	

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
				
				}else{					
					Notify.Show("Você não Possui Permissão para Cadastrar um Haver", Notify.TYPE_ERROR);
				}
			}
		});

		return btNovo;
	}
		
	@Override
	public Button BuildbtAceitar() {
		return null;
	}

	@Override
	public Button BuildbtCancelar() {
		return null;
	}

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}
