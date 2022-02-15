package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ConfigOrdemServicoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.ConfigOse;
import com.digital.opuserp.domain.EstoqueMovel;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.TipoItemProduto;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ProdutoEstoqueMovelUtil  extends Window {
	
	JPAContainer<EstoqueMovel> container;
	
	TextField tfBusca;
	Table tbProdutos;
	Button btSalvar;
	Button btCancelar;
	Veiculos veiculo;
	
	Integer CodComp;
	
	public ProdutoEstoqueMovelUtil(boolean modal, boolean center, Veiculos veiculo){
		
		
		super("Selecione um Produto");
		this.veiculo = veiculo;
			
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
	
	TipoItemProduto itemProduto;
	
	
	
	
	boolean ordem_servico = false;
	
		
	
	List<Filter> filtros = new ArrayList<Filter>();
	
	public JPAContainer<EstoqueMovel> buildJpaContainer(){
				
		container = JPAContainerFactory.make(EstoqueMovel.class, ConnUtil.getEntity());	
		container.addNestedContainerProperty("produto.nome");
		container.addNestedContainerProperty("veiculo.cod_veiculo");
		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("veiculo", veiculo));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
						
		container.applyFilters();
		container.sort(new String[]{"produto.nome"},new boolean[]{true});
		return container;
	}
	
	
	
	public Table buildTb(){
		tbProdutos = new Table(null, buildJpaContainer());
					
		tbProdutos.setWidth("100%");
		tbProdutos.setHeight("230px");
		tbProdutos.setSelectable(true);

		tbProdutos.setVisibleColumns(new Object[]{"id","veiculo.cod_veiculo","produto.nome","qtd"});
		
		tbProdutos.setColumnHeader("id", "Código");
		tbProdutos.setColumnHeader("veiculo.cod_veiculo", "Veículo");
		tbProdutos.setColumnHeader("produto.nome", "Produto");
		tbProdutos.setColumnHeader("qtd", "Qtd.:");
		
		tbProdutos.addListener(new ItemClickListener() {
			
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
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("veiculo", veiculo));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
	
		
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
		
		container.sort(new String[]{"produto.nome"},new boolean[]{true});
	}

	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tbProdutos.getValue()!= null){
					EntityItem<EstoqueMovel> entityItem = (EntityItem<EstoqueMovel>)tbProdutos.getItem(tbProdutos.getValue());
					
					EstoqueMovel material = entityItem.getEntity();
					
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

		private EstoqueMovel produto;
		
		
		public ProdutoEvent(Component source, EstoqueMovel produto) {
			super(source);		
			this.produto  = produto;
			
		}

		public EstoqueMovel getProduto() {
								
			return produto;
		}		
	}
	public interface ProdutoListerner{
		public void onSelected(ProdutoEvent event);
	}
}

