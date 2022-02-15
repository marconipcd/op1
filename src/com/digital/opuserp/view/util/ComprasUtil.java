package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ComprasUtil  extends Window {
	
	JPAContainer<MovimentoEntCabecalho> container;
	
	TextField tfBusca;
	Table tbCompras;
	Button btSalvar;
	Button btCancelar;
	Cliente cliente;
	
	
	public ComprasUtil(boolean modal, boolean center, Cliente c){
		
		
		super("Selecione um Compra");
		this.cliente = c;
		
		setWidth("1000px");
		setHeight("360px");
		setIcon(new ThemeResource("icons/search-32.png"));
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
				
				
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				addComponent(buildTextField());
				addComponent(buildTb());
				setExpandRatio(tbCompras, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSelecionar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		});
	}
	
	public ComprasUtil(boolean modal, boolean center){
		
		
		super("Selecione um Compra");
		
		
		setWidth("1000px");
		setHeight("360px");
		setIcon(new ThemeResource("icons/search-32.png"));
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
				
				
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				addComponent(buildTextField());
				addComponent(buildTb());
				setExpandRatio(tbCompras, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSelecionar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		});
	}
		
	public JPAContainer<MovimentoEntCabecalho> buildJpaContainer(){
		container = JPAContainerFactory.make(MovimentoEntCabecalho.class, ConnUtil.getEntity());	
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("situacao", "F"));
		container.addContainerFilter(Filters.not(Filters.eq("fornecedor.razao_social",OpusERP4UI.getEmpresa().getRazao_social())));
		container.addNestedContainerProperty("fornecedor.razao_social");
		
		container.sort(new String[]{"dataHora"},new boolean[]{false});
		return container;
	}
	
	
	
	public Table buildTb(){
		tbCompras = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("valorTotal")){
					
						return "R$ "+Real.formatDbToString(tbCompras.getItem(rowId).getItemProperty(colId).getValue().toString());
					}					
				
				if(colId.equals("id")){
					
					if(tbCompras.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tbCompras.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}			
				return super.formatPropertyValue(rowId, colId, property);			
				
			}		
		};
//		
//		tbCompras.setCellStyleGenerator(new Table.CellStyleGenerator() {
//			
//			@Override
//			public String getStyle(Table source, Object itemId, Object propertyId) {
//				
//				 if (propertyId == null)
//	                    return "row-header-default";               
//
//				 	EntityItem <Compra> item = (EntityItem<Compra>) source.getItem(itemId); 
//				 	Compra Compra = item.getEntity();
//				 	
//				 		if(cliente != null && cliente.getTabela_preco() != null && Compra.getOutras_tb_desc() != null && Compra.getOutras_tb_desc().equals("SIM") && Compra.getOutras_tb_desc().equals("SIM")){
//							return "row-header-crm-em-tratamento";  
//						}else{
//							return "row-header-default";      															
//						}
//					}
//				});
		
		tbCompras.setWidth("100%");
		tbCompras.setHeight("230px");
		tbCompras.setSelectable(true);

		tbCompras.setVisibleColumns(new Object[]{"id","codNf","fornecedor.razao_social","valorTotal","dataHora"});
		
		tbCompras.setColumnHeader("id", "Código");
		tbCompras.setColumnHeader("codNf", "Cod. NF");
		tbCompras.setColumnHeader("fornecedor.razao_social", "Fornecedor");
		tbCompras.setColumnHeader("valorTotal", "Valor");
		tbCompras.setColumnHeader("dataHora", "Data");
	
		
		//tbCompras.setConverter("id", null);
//		tbCompras.setColumnWidth("id", 40);
//		tbCompras.setColumnWidth("codNf", 70);
//		tbCompras.setColumnWidth("fornecedor.razao_social", 99);
//		tbCompras.setColumnWidth("valorTotal", 40);
//		tbCompras.setColumnWidth("dataHora", 40);
		
		tbCompras.setColumnAlignment("valorTotal", Align.RIGHT);

		
		tbCompras.addListener(new ItemClickListener() {
				
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					btSalvar.click();					
				}
			}
		});
		return tbCompras;
	}
	
	public TextField buildTextField() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				
//				try{
//					addFilter(Integer.parseInt(event.getText()));
//				}catch(Exception e){
					addFilter(event.getText());				
//				}
			}
		});
		return tfBusca;
	}
	

	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("situacao", "F"));
		container.addContainerFilter(Filters.not(Filters.eq("fornecedor.razao_social",OpusERP4UI.getEmpresa().getRazao_social())));
		container.addNestedContainerProperty("fornecedor.razao_social");
		
		Object[] collums = tbCompras.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
//		for(Object c:collums){		 
//			
//			if(!tbCompras.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
//				filtros.add(new Like(c.toString(), "%"+s+"%", false));
//			}			
//		}
		
		try {
			Integer cod = Integer.parseInt(s);
			
			for(Object c:collums){		 			
				if(!tbCompras.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				
				if(!tbCompras.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
			
		} catch (Exception e) {
			
			for(Object c:collums){		 			
				if(!tbCompras.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
		}
		
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();	
		
		container.sort(new String[]{"fornecedor.razao_social"},new boolean[]{true});
	}

	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tbCompras.getValue()!= null){
					Item item = tbCompras.getItem(tbCompras.getValue());			
					MovimentoEntCabecalho compra = CompraDAO.find(Integer.parseInt(item.getItemProperty("id").toString()));
					
					fireEvent(new CompraEvent(getUI(), compra));
					close();
				}	
			}	
		});
		
		ShortcutListener slbtOK = new ShortcutListener("Ok",ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};

		btSalvar.addShortcutListener(slbtOK);

		
		return btSalvar;
	}

	private Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}	
		});
		
			ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}

	
	
	public void addListerner(CompraListerner target){
		try {
			Method method = CompraListerner.class.getDeclaredMethod("onSelected",CompraEvent.class);
			addListener(CompraEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CompraListerner target){
		removeListener(CompraEvent.class, target);
	}
	public static class CompraEvent extends Event{

		private MovimentoEntCabecalho Compra;
		
		
		public CompraEvent(Component source, MovimentoEntCabecalho Compra) {
			super(source);		
			this.Compra  = Compra;
			
		}

		public MovimentoEntCabecalho getCompra() {
								
			return Compra;
		}		
	}
	public interface CompraListerner{
		public void onSelected(CompraEvent event);
	}
}


