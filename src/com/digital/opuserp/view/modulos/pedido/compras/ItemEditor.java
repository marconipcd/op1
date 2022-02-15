package com.digital.opuserp.view.modulos.pedido.compras;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SeriaisCompraDAO;
import com.digital.opuserp.dao.SerialDAO;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.pedido.compras.InformarSeriaisEditor.SerialItemEvent;
import com.digital.opuserp.view.modulos.pedido.compras.ProdutoFastEditor.ProdutoFastEditorEvent;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.QtdEditor;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.QtdEditor.QtdEvent;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ProdutoUtil;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class ItemEditor extends Window implements GenericEditor {

	MovimentoEntCabecalho pedido;
	
	Button btSave;
	Button btFinalizar;
	Button btCancelar;
	
	
	FormLayout flPrincipal;

	VerticalLayout vlRoot;
	
	Table tb;
	
	public ItemEditor(final MovimentoEntCabecalho pedido, String title, boolean modal){
		this.pedido = pedido;
		
		setWidth("920px");
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");
		
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				addComponent(vlRoot);
				
				final HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
							
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				
				addComponent(new HorizontalLayout(){
					{
						
						setWidth("100%");
						
					
						
						addComponent(hlButtons);
						setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
					}
				});
				
				
				
			}
		});
		
		
		
		buildLayout();			
		
	}
	
	
	private List<MovimentoEntDetalhe> itens = new ArrayList<>();
	private Label lbPedido;
	private HorizontalLayout hlTopo;
	
	public void buildLayout(){
		
		vlRoot.addComponent(new FormLayout(){
			{
				hlTopo = new HorizontalLayout();
				hlTopo.setWidth("100%");
								
				lbPedido = new Label("<h2 style='margin:0;padding:0;'> Compra Nº: "+pedido.getId().toString()+"</h2>", ContentMode.HTML);				
				
				hlTopo.addComponent(lbPedido);
				hlTopo.setComponentAlignment(lbPedido, Alignment.BOTTOM_LEFT);
				hlTopo.setExpandRatio(lbPedido, 1);
				
				final Button btAddItens = new Button("(F2) Adicionar Itens", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
							
							ProdutoUtil produtoUtil = new ProdutoUtil(true, true);
							produtoUtil.addListerner(new ProdutoUtil.ProdutoListerner() {
								
								@Override
								public void onSelected(ProdutoUtil.ProdutoEvent event) {
									
							
											final Produto p = event.getProduto();
											boolean fracionar = false;
											if(p.getFracionar() != null && p.getFracionar() == 1){
												fracionar = true;
											}
											
											QtdEditor qtdEditor = new QtdEditor("Quantidade", true, fracionar, p.getQtdEstoque(),false);
											qtdEditor.addListerner(new QtdEditor.QtdListerner() {
												
												@Override
												public void onClose(QtdEvent event) {
													if(event.isConfirm()){
														final Float qtd = event.getQtd();
														
														
//															if(p.getUtilizaSeriais() != null && p.getUtilizaSeriais().equals("SIM")){
//																//Cadastrar Seriais
//																
//																final Float qtd2 = event.getQtd();
//																final InformarSeriaisEditor informarSeriais = new InformarSeriaisEditor("Informar Seriais", true, event.getQtd());
//																informarSeriais.addListerner(new InformarSeriaisEditor.SerialItemListerner() {
//																	
//																	@Override
//																	public void onClose(SerialItemEvent event) {
//																		if(event.isConfirm()){
//																			
//																			final List<String> seriais =event.getSeriais();
//																			
//																			final BeanItem<Produto> itemProduto = new BeanItem<Produto>(p);
//																			final ProdutoFastEditor produtoFastEditor = new ProdutoFastEditor(itemProduto, "Editar Produto",true);
//																			produtoFastEditor.addListerner(new ProdutoFastEditor.ProdutoFastEditorListerner() {
//																				
//																				@Override
//																				public void onClose(ProdutoFastEditorEvent event) {
//																					if(event.isConfirm()){
//																						
//																						Produto pNovo = itemProduto.getBean();
//																						
//																						addItem(pNovo, qtd);
//																						
//																						//Atualiza Saldo do Produto
//																						pNovo.setQtdEstoque(pNovo.getQtdEstoque()+qtd);
//																						pNovo.setFornecedorId(pedido.getFornecedor());
//																						ProdutoDAO.alterar(pNovo);		
//																																					
//																						double valorTotal = new Double(p.getValorVenda()) * qtd;
////																						CompraDAO.saveItem(new MovimentoEntDetalhe(null, pNovo.getId(), pedido.getId(), 1, qtd, pNovo.getValorCusto(), pNovo.getValorVenda(), pNovo.getTaxaIcms(), new Float(pNovo.getTaxaIpi()),new Float(0), pNovo.getGarantia(), "NAO"));
//																						
////																						MovimentoEntDetalhe item = CompraDAO.saveItem(new MovimentoEntDetalhe(null, p.getId(), pedido.getId(), 1,qtd2, p.getValorCusto(), p.getValorVenda(), p.getTaxaIcms(), new Float(p.getTaxaIpi()),new Float(0), p.getGarantia(), "NAO"));
//																						MovimentoEntDetalhe item = CompraDAO.saveItem(new MovimentoEntDetalhe(null, pNovo.getId(), pedido.getId(), 1, qtd, pNovo.getValorCusto(), pNovo.getValorVenda(), Float.parseFloat(String.valueOf("0")), new Float(pNovo.getTaxaIpi()),new Float(0), pNovo.getGarantia(), "NAO"));
//
//																						//Cadastra Seriais
//																						for (String s : seriais) {
//																							SerialProduto serialProduto = SerialDAO.addSerial(p.getId(), s);
//																							
//																							if(serialProduto != null){
//																								SeriaisCompraDAO.save(item, serialProduto);
//																							}																				
//																						}
//																						
//																						
//																						produtoFastEditor.close();
//																						
//																						
//																					}
//																				}
//																			});	
//																																					
//																			informarSeriais.close();
//																			getUI().addWindow(produtoFastEditor);
//																			
//																		}
//																	}
//																});
//																
//																getUI().addWindow(informarSeriais);
//																
//															}else{
																
																final BeanItem<Produto> itemProduto = new BeanItem<Produto>(p);
																final ProdutoFastEditor produtoFastEditor = new ProdutoFastEditor(itemProduto, "Editar Produto",true);
																produtoFastEditor.addListerner(new ProdutoFastEditor.ProdutoFastEditorListerner() {
																	
																	@Override
																	public void onClose(ProdutoFastEditorEvent event) {
																		if(event.isConfirm()){
																			
																			Produto pNovo = itemProduto.getBean();
																			
																			addItem(pNovo, qtd);
																			
																			//Atualiza Saldo do Produto
																			pNovo.setQtdEstoque(pNovo.getQtdEstoque()+qtd);
																			pNovo.setFornecedorId(pedido.getFornecedor());
																			ProdutoDAO.alterar(pNovo);		
																																		
																			double valorTotal = new Double(p.getValorVenda()) * qtd;
																			CompraDAO.saveItem(new MovimentoEntDetalhe(null, pNovo.getId(), pedido.getId(), 1, qtd, pNovo.getValorCusto(), pNovo.getValorVenda(), Float.parseFloat(String.valueOf("0")), new Float(pNovo.getTaxaIpi()),new Float(0), pNovo.getGarantia(), "NAO"));
																			
																			produtoFastEditor.close();
																		}
																	}
																});
															
																getUI().addWindow(produtoFastEditor); 
//															}
														
													}
												}
												
											});
											
											
											getUI().addWindow(qtdEditor);
											qtdEditor.addCloseListener(new Window.CloseListener() {
												
												@Override
												public void windowClose(CloseEvent e) {
													tb.focus();											
												}
											});
									
									
								}
							});
												
							
							getUI().addWindow(produtoUtil);
						
						
					}
				});
				
				ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.F2,null) {
					
					@Override
					public void handleAction(Object sender, Object target) {
						btAddItens.click();
					}
				};
				
				btAddItens.addShortcutListener(slbtOK);
				btAddItens.addStyleName(Reindeer.BUTTON_SMALL);
				
				
				hlTopo.addComponent(btAddItens);
				hlTopo.setComponentAlignment(btAddItens, Alignment.BOTTOM_RIGHT);
								
				addComponent(hlTopo);				
			}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{				
					addComponent(buildTbGeneric());					
				}
		});
		
		
		vlRoot.addComponent(new FormLayout(){
			{
				hlBase = new HorizontalLayout();
				hlBase.setWidth("100%");
				hlBase.setHeight("30px");
						
				
				lbSubTotal = new Label("<h2 style='text-align: right;height: 30px;'>SubTotal R$ "+"0,00"+"</h2>", ContentMode.HTML);
				
				hlBase.addComponent(lbSubTotal);
				hlBase.setComponentAlignment(lbSubTotal, Alignment.BOTTOM_RIGHT);
				
				
				addComponent(hlBase);
			}
		});
		
		
		
		
		
		List<MovimentoEntDetalhe> itens = CompraDAO.getItensCompra(pedido.getId());
		
		for (MovimentoEntDetalhe item : itens) {
			
			Produto p = ProdutoDAO.find(item.getProdutoId());
			addItem(p, item.getQuantidade());		
		}
				
	}
	
	HorizontalLayout hlBase;
	Label lbSubTotal;
	
	public Table buildTbGeneric() {
		tb = new Table(){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("Valor Unitario") || colId.equals("Valor Total")){
					if(tb != null && rowId != null && colId != null)
					
					return "R$ "+super.formatPropertyValue(rowId, colId, property);
				}				
				
				if(colId.equals("Qtd.") && tb != null && rowId != null && colId != null){
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
						String s = tb.getItem(rowId).getItemProperty(colId).getValue().toString();
						String f = String.format("%.2f", new Float(s)); 
						
						return f.replace(".", ",");
					}
				}
				
				if(colId.equals("Codigo")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}
				
				return super.formatPropertyValue(rowId, colId, property);
			
				
			}
		};		
		tb.setWidth("100%");
		tb.setHeight("25em");
		tb.setSelectable(true);						
		tb.setImmediate(true);
		
		tb.addContainerProperty("Codigo", Integer.class, null);
				
		tb.addContainerProperty("Produto", String.class, null);
		tb.setColumnExpandRatio("Produto", 1);
		
		
		tb.addContainerProperty("Qtd.", String.class, null);
		tb.addContainerProperty("Valor Custo", String.class, null);
		tb.addContainerProperty("Valor Total", String.class, null);
		
		tb.setColumnAlignment("Valor Custo", Align.RIGHT);
		tb.setColumnAlignment("Valor Total", Align.RIGHT);
		tb.setColumnHeader("Codigo", "Código");
		
		
		
		tb.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				
				
				
				Button btDeletar = new Button(null, new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Cancelar este Item?", true,true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){		
										
									itens.remove(source.getItem(itemId));
									removerItem(ProdutoDAO.find((Integer)source.getItem(itemId).getItemProperty("Codigo").getValue()));
									Notify.Show("Item Cancelado com Sucesso!", Notify.TYPE_SUCCESS);
									
								}
							}
						});
						
						getUI().addWindow(gd);
					}
				});
				btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
				btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
				btDeletar.setDescription("Cancelar Item");
				
				return btDeletar;
			}
		});

		tb.setColumnWidth("x", 20);
				
		tb.setColumnWidth("Qtd.", 35);
		tb.setColumnAlignment("Qtd.", Align.CENTER);
		
		//tb.setConverter("Codigo", null);
		tb.focus();
		return tb;
	}
	
	private void removerItem(Produto s){
		if(tb != null){
			
			for(Object itemId : tb.getItemIds().toArray()){
				if(tb.getItem(itemId).getItemProperty("Codigo").getValue().equals(s.getId())){
					tb.removeItem(itemId);
				}
			}
			
			for(MovimentoEntDetalhe item: CompraDAO.getItensCompra(pedido.getId())){
				if(item.getProdutoId().equals(s.getId())){
					SeriaisCompraDAO.removeByItem(item);
					CompraDAO.deleteItem(item);
					//s.setQtdEstoque(s.getQtdEstoque()-item.getQuantidade());
					ProdutoDAO.edit(s, item.getQuantidade(), "S");		
					
				}
			}
			
			
			itens = new ArrayList<>();
			
			for(Object itemId : tb.getItemIds().toArray()){	
								
				itens.add(new MovimentoEntDetalhe(null, s.getId(), pedido.getId(), 1, new Float(tb.getItem(itemId).getItemProperty("Qtd.").getValue().toString()), s.getValorVenda(), s.getValorVenda(), Float.parseFloat(String.valueOf("0")),new Float( s.getTaxaIpi()),new Float(0), s.getGarantia(),"NAO"));
			}
			
			double totalGeral = 0;
			for (Object itemId : tb.getItemIds()) {
				double valor = Double.parseDouble(Real.formatStringToDB(tb.getItem(itemId).getItemProperty("Valor Total").getValue().toString()));
				totalGeral = totalGeral + valor;
			}
			
			hlBase.replaceComponent(lbSubTotal, BuildLbValorTotal(Real.formatDbToString(String.valueOf(totalGeral))));
			pedido.setValorTotal(new Float(totalGeral));
		}
	}
	
		
	
	private void addItem(Produto s, Float qtd){
		
		if(tb != null){	
			
			boolean exist= false;	
			Float qtd_antiga = new Float(0);
			for(Object ob: tb.getItemIds().toArray()){
				if(tb.getItem(ob).getItemProperty("Codigo").getValue().equals(s.getId())){
					qtd_antiga = new Float(tb.getItem(ob).getItemProperty("Qtd.").getValue().toString());				
					tb.removeItem(ob);					
					exist = true;
					break;
				}
			}
		  
			if(exist){
				
				qtd = qtd+qtd_antiga;
				double valorTotal = s.getValorCusto() * qtd;
				tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), Real.formatDbToString(s.getValorCusto().toString()), Real.formatDbToString(String.valueOf(valorTotal))}, s.getId());

				
				itens = new ArrayList<>();
				
				for(Object ob: tb.getItemIds().toArray()){			
					itens.add(new MovimentoEntDetalhe(null, s.getId(), pedido.getId(), 1, new Float(tb.getItem(ob).getItemProperty("Qtd.").getValue().toString()), s.getValorCusto(), s.getValorVenda(),Float.parseFloat(String.valueOf("0")),new Float( s.getTaxaIpi()),new Float(0), s.getGarantia(),"NAO"));
//					itens.add(new EcfPreVendaDetalhe(null, (Integer)tb.getItem(ob).getItemProperty("Codigo").getValue(), pedido.getId(), tb.getItemIds().size()+1, new Float(tb.getItem(ob).getItemProperty("Qtd.").getValue().toString()), Real.formatStringToDBFloat(tb.getItem(ob).getItemProperty("Valor Unitario").getValue().toString()), Real.formatStringToDBFloat(tb.getItem(ob).getItemProperty("Valor Total").getValue().toString()), "N"));
				}

				
			}else{
				
				double valorTotal = s.getValorCusto() * qtd;
				tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), Real.formatDbToString(s.getValorCusto().toString()), Real.formatDbToString(String.valueOf(valorTotal))}, s.getId());

				
				itens = new ArrayList<>();
				
				for(Object ob: tb.getItemIds().toArray()){
					itens.add(new MovimentoEntDetalhe(null, s.getId(), pedido.getId(), 1, new Float(tb.getItem(ob).getItemProperty("Qtd.").getValue().toString()), s.getValorCusto(), s.getValorVenda(), Float.parseFloat(String.valueOf("0")),new Float( s.getTaxaIpi()),new Float(0), s.getGarantia(),"NAO"));
					
				}
			}
			
			
			
			
		}

		double totalGeral = 0;
		if(tb != null){
			for (Object itemId : tb.getItemIds()) {
				double valor = Double.parseDouble(Real.formatStringToDB(tb.getItem(itemId).getItemProperty("Valor Total").getValue().toString()));
				totalGeral = totalGeral + valor;
			}
			
			hlBase.replaceComponent(lbSubTotal, BuildLbValorTotal(Real.formatDbToString(String.valueOf(totalGeral))));
			pedido.setValorTotal(new Float(totalGeral));
		}
		
	}
	
	
	private Label BuildLbValorTotal(String valor){
		return lbSubTotal = new Label("<h2 style='text-align: right;'>SubTotal R$ "+valor+"</h2>", ContentMode.HTML);
		
	}
	
	
	@Override
	public Button buildBtSalvar() {
		
		btFinalizar = new Button("(F10) Finalizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(itens.size() > 0){
					try {				
						
						
						fireEvent(new ItemEvent(getUI(),itens, true));						
							
					} catch (Exception e) {					
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
					}
				}else{
					
					
					
					Notify.Show_Invalid_Submit_Form();
					
				}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.F10,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btFinalizar.click();
			}
		};
		
		btFinalizar.addShortcutListener(slbtOK);
		
		btFinalizar.setStyleName("default");
		return btFinalizar;
	}
	
	public Button buildBtSave(){
		btSave = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
								
			}
		});
		
		return btSave;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				close();						
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		
		
		return btCancelar;
	}
	
	
	public void addListerner(ItemListerner target){
		try {
			Method method = ItemListerner.class.getDeclaredMethod("onClose", ItemEvent.class);
			addListener(ItemEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ItemListerner target){
		removeListener(ItemEvent.class, target);
	}
	public static class ItemEvent extends Event{
				
		private boolean confirm;
		private List<MovimentoEntDetalhe> itens;
		
		public ItemEvent(Component source, List<MovimentoEntDetalhe> itens,  boolean confirm) {
			super(source);
		
			this.itens = itens;
			this.confirm = confirm;			
		}
		
		
		public List<MovimentoEntDetalhe> getItens(){
			return itens;
		}
		
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ItemListerner extends Serializable{
		public void onClose(ItemEvent event);
	}

	
}
