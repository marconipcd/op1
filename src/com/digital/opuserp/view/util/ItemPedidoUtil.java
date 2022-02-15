package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
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

public class ItemPedidoUtil extends Window {
	
	JPAContainer<EcfPreVendaDetalhe> container;
	
	TextField tfBusca;
	Table tbProdutos;
	Button btSalvar;
	Button btCancelar;
	Cliente cliente;
	
	Integer CodComp;
	
	EcfPreVendaCabecalho pedido;
	
	public ItemPedidoUtil(boolean modal, boolean center, EcfPreVendaCabecalho pedido){
		
		super("Selecione um Produto");
		this.pedido = pedido;
			
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
	
		
	public Table buildTb(){
		
		tbProdutos = new Table();
		
		tbProdutos.setWidth("100%");
		tbProdutos.setHeight("230px");
		tbProdutos.setSelectable(true);
		
		tbProdutos.addContainerProperty("Item", Integer.class, 0);
		tbProdutos.addContainerProperty("Cod", Integer.class, 0);
		tbProdutos.addContainerProperty("Nome", String.class, "");		
		tbProdutos.addContainerProperty("Qtd", String.class, "");
		tbProdutos.addContainerProperty("Valor", String.class, "");
		//tbProdutos.addContainerProperty("Valor Promocional", String.class, "");
    
		List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedidoAtivos(pedido.getN_nf_devolucao().getId());
		
		for (EcfPreVendaDetalhe item: itens) {
			
			if(pedido.getTipo().equals("DEVOLUCAO")){
				Produto produto = ProdutoDAO.find(item.getProdutoId());
				
				tbProdutos.addItem(new Object[]{item.getId(),item.getProdutoId(), produto.getNome(), 
						item.getQuantidade().toString(), Real.formatDbToString(String.valueOf(item.getValorTotal()))}, tbProdutos.getItemIds().size() + 1);
			}else{			
				Produto produto = ProdutoDAO.find(item.getProdutoId());
				
				double valor_final = 0;
				
				if(pedido.getCliente() != null && pedido.getCliente().getTabela_preco() != null){
					double percentual = pedido.getCliente().getTabela_preco().getDesconto() / 100;
				 	valor_final = new Double(item.getValorUnitario())  - (percentual * new Double(item.getValorUnitario()));			
				}else{
					valor_final = new Double(item.getValorUnitario());				
				}
				
				tbProdutos.addItem(new Object[]{item.getId(), produto.getId(), produto.getNome(), 
						item.getQuantidade().toString(), Real.formatDbToString(String.valueOf(valor_final))}, tbProdutos.getItemIds().size() + 1);
			}			
		}
		
		tbProdutos.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
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
//					addFilter(event.getText());				
//				}
			}
		});
		return tfBusca;
	}
	

	public void addFilter(String s) {
		
	}

	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tbProdutos.getValue()!= null){
					Item item = tbProdutos.getItem(tbProdutos.getValue());			
										
					fireEvent(new ItemPedidoEvent(getUI(), (Integer)item.getItemProperty("Cod").getValue(), (Integer)item.getItemProperty("Item").getValue() ));
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

	
	
	public void addListerner(ItemPedidoListerner target){
		try {
			Method method = ItemPedidoListerner.class.getDeclaredMethod("onSelected",ItemPedidoEvent.class);
			addListener(ItemPedidoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ProdutoListerner target){
		removeListener(ProdutoEvent.class, target);
	}
	public static class ItemPedidoEvent extends Event{

		private Integer produto_id;
		private Integer item_id;
		
		public ItemPedidoEvent(Component source, Integer produto_id, Integer item_id) {
			super(source);		
			this.produto_id  = produto_id;
			this.item_id  = item_id;			
		}

		public Integer getProdutoId() {
								
			return produto_id;
		}	
		public Integer getItemId() {
			
			return item_id;
		}	
	}
	public interface ItemPedidoListerner{
		public void onSelected(ItemPedidoEvent event);
	}
}

