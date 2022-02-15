package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.ConfigOrdemServicoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ConfigOse;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.TipoItemProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.ProdutoUtil.ProdutoEvent;
import com.digital.opuserp.view.util.ProdutoUtil.ProdutoListerner;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Table.Align;

public class ProdutoImportarUtil  extends Window {
	
	JPAContainer<Produto> container;
	
	TextField tfBusca;
	Table tbProdutos;
	Button btSalvar;
	Button btCancelar;
	Cliente cliente;
	
	Integer CodComp;
	
	
	public ProdutoImportarUtil(boolean modal, boolean center){
		
		
		super("Selecione um Produto");
		
		
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
				setExpandRatio(tbProdutos, 2);
				
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
	
	
	List<Filter> filtros = new ArrayList<Filter>();
	
	public JPAContainer<Produto> buildJpaContainer(){
			
		container = JPAContainerFactory.make(Produto.class, ConnUtil.getEntity());	
		container.addContainerFilter(Filters.not(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId())));
		container.addContainerFilter(Filters.eq("empresaId.grupo", "DIGITAL"));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
	
		
		if(CodComp != null){
			List<MovimentoEntDetalhe> md = CompraDAO.findByCabt(CodComp);	
			if(md!=null){
				for(MovimentoEntDetalhe movDet: md){	
					if(movDet.getProdutoId()!=null){
						filtros.add(new Like("id", "%"+movDet.getProdutoId()+"%", false));
					}
				}
			}
			container.addContainerFilter(Filters.or(filtros));
		}
		
		
		container.applyFilters();
		container.sort(new String[]{"nome"},new boolean[]{true});
		return container;
	}
	
	
	
	public Table buildTb(){
		tbProdutos = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("valorVenda")){
					
//					Item item = tbProdutos.getItem(tbProdutos.getValue());
					Produto produto = null;	
					
					if(tbProdutos.getItem(rowId) != null){
						EntityItem <Produto> item = (EntityItem <Produto>)tbProdutos.getItem(rowId);
						produto = item.getEntity();					
					}
					
					if(cliente != null && cliente.getTabela_preco() != null && produto != null && produto.getOutras_tb_desc().equals("SIM")){
						
						double percentual = cliente.getTabela_preco().getDesconto() / 100;
						double valor_final = new Double(tbProdutos.getItem(rowId).getItemProperty(colId).getValue().toString())  - (percentual * new Double(tbProdutos.getItem(rowId).getItemProperty(colId).getValue().toString()));
						
						return "R$ "+Real.formatDbToString(String.valueOf(valor_final));
					}else{
						return "R$ "+Real.formatDbToString(tbProdutos.getItem(rowId).getItemProperty(colId).getValue().toString());
					}					
				
				}else if(colId.equals("valorCusto")){
					return "R$ "+Real.formatDbToString(tbProdutos.getItem(rowId).getItemProperty(colId).getValue().toString());
				}
				
				if(colId.equals("id")){
					
					if(tbProdutos.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tbProdutos.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}			
				return super.formatPropertyValue(rowId, colId, property);			
				
			}		
		};
		
		tbProdutos.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				
				 if (propertyId == null)
	                    return "row-header-default";               

				 	EntityItem <Produto> item = (EntityItem<Produto>) source.getItem(itemId); 
				 	
				 	if(item != null && item.getEntity() != null){
				 	Produto produto = item.getEntity();
				 	
				 		if(cliente != null && cliente.getTabela_preco() != null && produto.getOutras_tb_desc() != null && produto.getOutras_tb_desc().equals("SIM") && produto.getOutras_tb_desc().equals("SIM")){
							return "row-header-crm-em-tratamento";  
						}else{
							return "row-header-default";      															
						}
					}else{
						return "row-header-default";	
					}
					
			}
		
		});
		
		tbProdutos.setSizeFull();
		tbProdutos.setSelectable(true);

		tbProdutos.setVisibleColumns(new Object[]{"id","gTin","nome","valorVenda","qtdEstoque","garantia"});
		
		tbProdutos.setColumnHeader("id", "Código");
		tbProdutos.setColumnHeader("gTin", "Cód. Barras");
		tbProdutos.setColumnHeader("nome", "Nome");
		tbProdutos.setColumnHeader("valorCusto", "Custo");
		tbProdutos.setColumnHeader("valorVenda", "Valor");
		tbProdutos.setColumnHeader("qtdEstoque", "Saldo");
		tbProdutos.setColumnHeader("garantia", "Garantia");
		
		//tbProdutos.setConverter("id", null);
		
		tbProdutos.setColumnWidth("gTin", 80);
		tbProdutos.setColumnWidth("garantia", 90);
		tbProdutos.setColumnWidth("qtdEstoque", 40);
		tbProdutos.setColumnWidth("id", 50);
		
		tbProdutos.setColumnAlignment("valorVenda", Align.RIGHT);
		tbProdutos.setColumnAlignment("valorCusto", Align.RIGHT);
		
		
		tbProdutos.addListener(new ItemClickListener() {
			
			Produto material;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					btSalvar.click();					
				}
			}
		});
		return tbProdutos;
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
		
		ConfigOse configOse = ConfigOrdemServicoDAO.getTipoItemDefault();
		
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.not(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId())));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
			
		if(CodComp!=null){
			container.addContainerFilter(Filters.or(filtros));
		}
		
		Object[] collums = tbProdutos.getVisibleColumns();		
		List<Filter> filtrosInternos = new ArrayList<Filter>();		

		try {
			Integer cod = Integer.parseInt(s);
			
			for(Object c:collums){		 			
				if(!tbProdutos.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtrosInternos.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				
				if(!tbProdutos.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtrosInternos.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
			
		} catch (Exception e) {
			
			for(Object c:collums){		 			
				if(!tbProdutos.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtrosInternos.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
		}
		
		
		container.addContainerFilter(Filters.or(filtrosInternos));
		container.applyFilters();	
		
		container.sort(new String[]{"nome"},new boolean[]{true});
	}

	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tbProdutos.getValue()!= null){
					Item item = tbProdutos.getItem(tbProdutos.getValue());		
					
					EntityManager em = ConnUtil.getEntity();
					Produto material = em.find(Produto.class, Integer.parseInt(item.getItemProperty("id").toString())); 
					
					fireEvent(new ProdutoEvent(getUI(), material));
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

	
	
	public void addListerner(ProdutoListerner target){
		try {
			Method method = ProdutoListerner.class.getDeclaredMethod("onSelected",ProdutoEvent.class);
			addListener(ProdutoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ProdutoListerner target){
		removeListener(ProdutoEvent.class, target);
	}
	public static class ProdutoEvent extends Event{

		private Produto produto;
		
		
		public ProdutoEvent(Component source, Produto produto) {
			super(source);		
			this.produto  = produto;
			
		}

		public Produto getProduto() {
								
			return produto;
		}		
	}
	public interface ProdutoListerner{
		public void onSelected(ProdutoEvent event);
	}
}

