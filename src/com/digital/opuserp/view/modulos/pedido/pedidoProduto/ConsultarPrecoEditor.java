package com.digital.opuserp.view.modulos.pedido.pedidoProduto;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.domain.TipoItemProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConsultarPrecoEditor extends Window {
			
			JPAContainer<Produto> container_produto;
			JPAContainer<Servico> container_servico;
			
			
			ComboBox cbTipoItem;
			TextField tfBusca;
			Table tbProdutos;
			Button btSalvar;
			Button btCancelar;

			public ConsultarPrecoEditor(boolean modal, boolean center){
								
				super("Consultar Preços");
								
				setWidth("1000px");
				setHeight("360px");
				setIcon(new ThemeResource("icons/search-32.png"));
				setModal(modal);
				if(center){
					center();
				}
				
				setClosable(true);
											
						
				setContent(new VerticalLayout(){
					{
						setSizeFull();
						setMargin(true);
						
						HorizontalLayout hl = new HorizontalLayout();
						hl.setWidth("100%");
						
						hl.addComponent(buildCbTipo());
						hl.addComponent(buildCbTipoItem());
						hl.addComponent(buildTextField());
						hl.setExpandRatio(tfBusca, 2);
						
						addComponent(hl);
						addComponent(buildTb("PRODUTO"));
						setExpandRatio(tbProdutos, 2);
						
						HorizontalLayout hlButtons = new HorizontalLayout();
						hlButtons.setSpacing(true);
						hlButtons.setMargin(true);
						hlButtons.setStyleName("hl_buttons_bottom");
						//hlButtons.addComponent(buildBtCancelar());
						
						addComponent(hlButtons);
						setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
						
					}
				});
			}
				
			public JPAContainer<Servico> buildJpaContainer_s(){
				container_servico = JPAContainerFactory.make(Servico.class, ConnUtil.getEntity());	
				container_servico.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
				container_servico.addContainerFilter(Filters.eq("status", "ATIVO"));
						
				container_servico.sort(new String[]{"nome"},new boolean[]{true});
				return container_servico;
			}
			
			public JPAContainer<Produto> buildJpaContainer_p(){
				container_produto = JPAContainerFactory.make(Produto.class, ConnUtil.getEntity());	
				container_produto.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
				container_produto.addContainerFilter(Filters.eq("status", "ATIVO"));
						
				container_produto.sort(new String[]{"nome"},new boolean[]{true});
				return container_produto;
			}
			
			
			
			public Table buildTb(String tipo){
				tbProdutos = new Table(null){
					@Override
					protected String formatPropertyValue(Object rowId, Object colId,
							Property<?> property) {
						
						if(colId.equals("valorVenda") || colId.equals("preco_promocional")){
							
							if(tbProdutos.getItem(rowId).getItemProperty(colId).getValue() != null){
								return "R$ "+Real.formatDbToString(tbProdutos.getItem(rowId).getItemProperty(colId).getValue().toString());
							}else{
								return "";
							}
						}				
						
						if(colId.equals("id")){
							
							if(tbProdutos.getItem(rowId).getItemProperty("id").getValue() != null){						
								return ((Integer)tbProdutos.getItem(rowId).getItemProperty("id").getValue()).toString();				
							}					
						}
						
						return super.formatPropertyValue(rowId, colId, property);
					
						
					}
				};
				
				tbProdutos.setSizeFull();
				tbProdutos.setSelectable(true);
				
				if(tipo.equals("PRODUTO")){
					tbProdutos.setContainerDataSource(buildJpaContainer_p());
					
					tbProdutos.setVisibleColumns(new Object[]{"id","gTin","nome","valorVenda","preco_promocional","qtdEstoque","qtdEstoqueDeposito","garantia"});
					
					tbProdutos.setColumnHeader("id", "Código");
					tbProdutos.setColumnHeader("gTin", "Cód. Barras");
					tbProdutos.setColumnHeader("nome", "Nome");
					tbProdutos.setColumnHeader("valorVenda", "Valor");
					tbProdutos.setColumnHeader("preco_promocional", "Valor Promocional");
					tbProdutos.setColumnHeader("qtdEstoque", "Saldo Loja");
					tbProdutos.setColumnHeader("qtdEstoqueDeposito", "Saldo Deposito");
					tbProdutos.setColumnHeader("garantia", "Garantia");
					
					tbProdutos.setColumnWidth("gTin", 80);
					tbProdutos.setColumnWidth("garantia", 90);
					tbProdutos.setColumnWidth("qtdEstoque", 100);
					tbProdutos.setColumnWidth("qtdEstoqueDeposito", 100);
					tbProdutos.setColumnWidth("id", 50);
					
					tbProdutos.setColumnAlignment("valorVenda", Align.RIGHT);
					tbProdutos.setColumnAlignment("preco_promocional", Align.RIGHT);
				
					//tbProdutos.setConverter("id", null);
				}
				
				if(tipo.equals("SERVICO")){
					tbProdutos.setContainerDataSource(buildJpaContainer_s());
					
					tbProdutos.setVisibleColumns(new Object[]{"id","nome","valor_venda"});

					tbProdutos.setColumnHeader("id", "Código");
					tbProdutos.setColumnHeader("nome", "Nome");
					tbProdutos.setColumnHeader("valor_venda", "Valor");
					tbProdutos.setColumnAlignment("valor_venda", Align.RIGHT);
					
					//tbProdutos.setConverter("id", null);					
				}
				
				

				
				
				
				tbProdutos.addListener(new ItemClickListener() {
					
					Produto material;
					
					@Override
					public void itemClick(ItemClickEvent event) {
						if(event.isDoubleClick() && btSalvar != null){
							btSalvar.click();					
						}
					}
				});
				return tbProdutos;
			}
			
			private ComboBox cbTipo;
			public ComboBox buildCbTipo(){
				cbTipo = new ComboBox();
				cbTipo.setNullSelectionAllowed(false);
				cbTipo.setTextInputAllowed(false);
				
				cbTipo.addItem("PRODUTO");
				cbTipo.addItem("SERVICO");
				cbTipo.select("PRODUTO");
				cbTipo.setImmediate(true);
				cbTipo.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						if(cbTipo.getValue() != null){
							if(cbTipo.getValue().toString().equals("PRODUTO")){
								((VerticalLayout) getContent()).replaceComponent(tbProdutos, buildTb("PRODUTO"));
								((VerticalLayout) getContent()).setExpandRatio(tbProdutos, 2);
							}
							
							if(cbTipo.getValue().toString().equals("SERVICO")){
								((VerticalLayout) getContent()).replaceComponent(tbProdutos, buildTb("SERVICO"));
								((VerticalLayout) getContent()).setExpandRatio(tbProdutos, 2);
							}
						}
					}
				});
				
				
				
				
				return cbTipo;
			}
			public TextField buildTextField() {
				tfBusca = new TextField();
				tfBusca.setWidth("100%");
				tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
				tfBusca.focus();
				tfBusca.addListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						try{
							addFilter(Integer.parseInt(event.getText()));
						}catch(Exception e){
							addFilter(event.getText());				
						}
					}
				});
				return tfBusca;
			}
			private JPAContainer<TipoItemProduto> buildTipoItemProduto(){
				JPAContainer<TipoItemProduto> container = JPAContainerFactory.make(TipoItemProduto.class, ConnUtil.getEntity());
				
				
				return container;
			}
			private ComboBox buildCbTipoItem(){
				cbTipoItem = new ComboBox(null,buildTipoItemProduto());		
				cbTipoItem.setTextInputAllowed(false);
				
				cbTipoItem.setItemCaptionPropertyId("nome");
				cbTipoItem.setImmediate(true);
				cbTipoItem.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {

						addFilter(tfBusca.getValue());
					}
				});
			
				return cbTipoItem;
			}

			public void addFilter(String s) {
				
				if(cbTipo.getValue() != null){
					if(cbTipo.getValue().equals("PRODUTO")){
						container_produto.removeAllContainerFilters();
						container_produto.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
						container_produto.addContainerFilter(Filters.eq("status", "ATIVO"));
						
						if(cbTipoItem != null && cbTipoItem.getValue() != null){
							
							EntityItem<TipoItemProduto> entityItem = (EntityItem<TipoItemProduto>)cbTipoItem.getItem(cbTipoItem.getValue());
							container_produto.addContainerFilter(Filters.eq("tipo_item", entityItem.getEntity()));
						}
						
						Object[] collums = tbProdutos.getVisibleColumns();		
						List<Filter> filtros = new ArrayList<Filter>();		
						
						for(Object c:collums){		 							
							if(!tbProdutos.isColumnCollapsed(c.toString()) && container_produto.getType(c.toString()) == String.class){					   	
								filtros.add(new Like(c.toString(), "%"+s+"%", false));
							}			
						}
						
						container_produto.addContainerFilter(Filters.or(filtros));
						container_produto.applyFilters();		
						container_produto.sort(new String[]{"nome"},new boolean[]{true});
					}
					
					if(cbTipo.getValue().equals("SERVICO")){
						container_servico.removeAllContainerFilters();
						container_servico.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
						container_servico.addContainerFilter(Filters.eq("status", "ATIVO"));
						
						Object[] collums = tbProdutos.getVisibleColumns();		
						List<Filter> filtros = new ArrayList<Filter>();		
						
						for(Object c:collums){		 
							
							if(!tbProdutos.isColumnCollapsed(c.toString()) && container_servico.getType(c.toString()) == String.class){					   	
								filtros.add(new Like(c.toString(), "%"+s+"%", false));
							}			
						}
						
						container_servico.addContainerFilter(Filters.or(filtros));
						container_servico.applyFilters();		
						container_servico.sort(new String[]{"nome"},new boolean[]{true});
					}
				}	
				
				
			}
			
			public void addFilter(Integer s) {
				
				if(cbTipo.getValue() != null){
					
					if(cbTipo.getValue().equals("PRODUTO")){
						container_produto.removeAllContainerFilters();
						container_produto.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
						container_produto.addContainerFilter(Filters.eq("status", "ATIVO"));
						
						Object[] collums = tbProdutos.getVisibleColumns();		
						List<Filter> filtros = new ArrayList<Filter>();		
						
						for(Object c:collums){		 							
							if(!tbProdutos.isColumnCollapsed(c.toString()) && container_produto.getType(c.toString()) == Integer.class){					   	
								filtros.add(new Like(c.toString(), "%"+s+"%", false));
							}			
						}
						
						container_produto.addContainerFilter(Filters.or(filtros));
						container_produto.applyFilters();		
						container_produto.sort(new String[]{"nome"},new boolean[]{true});
					}
					
					if(cbTipo.getValue().equals("SERVICO")){
						container_servico.removeAllContainerFilters();
						container_servico.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
						container_servico.addContainerFilter(Filters.eq("status", "ATIVO"));
						
						Object[] collums = tbProdutos.getVisibleColumns();		
						List<Filter> filtros = new ArrayList<Filter>();		
						
						for(Object c:collums){		 
							
							if(!tbProdutos.isColumnCollapsed(c.toString()) && container_servico.getType(c.toString()) == Integer.class){					   	
								filtros.add(new Like(c.toString(), "%"+s+"%", false));
							}			
						}
						
						container_servico.addContainerFilter(Filters.or(filtros));
						container_servico.applyFilters();		
						container_servico.sort(new String[]{"nome"},new boolean[]{true});
					}
				}
			}
			
			
			

			private Button buildBtCancelar() {
				btCancelar = new Button("Fechar", new Button.ClickListener() {
					
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

			
			
			
		}

