package com.digital.opuserp.view.modulos.ordemServico.pedidoServico;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.ordemServico.pedidoServico.ConfiguracaoEndEntregaEditor.ConfigEndEntregaEvent;
import com.digital.opuserp.view.modulos.ordemServico.pedidoServico.ConfiguracaoItensEditor.ConfigItensEvent;
import com.digital.opuserp.view.modulos.ordemServico.pedidoServico.ConfiguracaoPgtoEditor.ConfigFormaPgtoEvent;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.ClienteUtil.ClienteEvent;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ServicoUtil.ServicoEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class PedidoServicoView extends VerticalLayout{

	private Table tb;
	
	private Button btAbrirPedido;
	private Button btConsultarProduto;
	private Button btCancelarPedido;
	private Button btConcluirPedido;
	
	private HorizontalLayout hlFloat;	
	private Label lbRegistros;
	
	private ShortcutListener slNovo;
	private ShortcutListener slProdutos;
	private ShortcutListener slCancelarPedido;	
	private ShortcutListener slConcluirPedido;
	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	
	private HorizontalLayout hlValorTotal;
	
	private FormasPgto formaPgto;
	private double desconto;
	private double acrescimo;
	private Long nParc;
	private Date dataPriVenc;
	
		
	private Cliente cliente;
	public PedidoServicoView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtAbrirPedido());
			hlButons.addComponent(BuildbtConsultarProduto());
			hlButons.addComponent(BuildbtCancelarPedido());
			hlButons.addComponent(BuildbtConcluirPedido());
			
			hlValorTotal = new HorizontalLayout();
			hlValorTotal.addComponent(BuildLbValorTotal("0,00"));
			
			
			HorizontalLayout hlTopo = new HorizontalLayout();
			hlTopo.setWidth("100%");
			
			hlTopo.addComponent(hlValorTotal);
			hlTopo.addComponent(hlButons);
			hlTopo.setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			hlTopo.setComponentAlignment(hlValorTotal, Alignment.TOP_LEFT);
			hlTopo.setExpandRatio(hlButons, 1);
			
			addComponent(hlTopo);
						
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(lbRegistros());
			Label lbLegend = new Label("F2 - Abrir Pedido | F3 - Consultar Produto | F4 - Concluir Pedido ");
			lbLegend.setWidth("440px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			hlFloat.setExpandRatio(lbLegend, 1);
			
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
		}
	}
	
	private Label lbValorTotal;
	private Label BuildLbValorTotal(String valor){
		return lbValorTotal = new Label("<h2 style=\"margin:0;\">TOTAL: "+valor+"</h2>", ContentMode.HTML);
		
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof PedidoServicoView)
		{
			btAbrirPedido.addShortcutListener(buildShortCutNovo());
			btConsultarProduto.addShortcutListener(buildShortCutProdutos());
			btCancelarPedido.addShortcutListener(buildShortCutCancelarItem());
			btConcluirPedido.addShortcutListener(buildShortCutConcluirPedido());
		}else{
			if(btAbrirPedido != null || slNovo != null){				
				btAbrirPedido.removeShortcutListener(slNovo);
				btConsultarProduto.removeShortcutListener(slProdutos);
				btCancelarPedido.removeShortcutListener(slCancelarPedido);
				btConcluirPedido.removeShortcutListener(slConcluirPedido);
			}		
		}
	}
	
	public Label lbRegistros(){
		lbRegistros = new Label(String.valueOf(1) + " Itens");
		return lbRegistros;
	}
	
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("AbrirPedido",ShortcutAction.KeyCode.F2,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btAbrirPedido.click();
			}
		};
		return slNovo;
	}
	public ShortcutListener buildShortCutProdutos(){
		slProdutos = new ShortcutListener("Produtos",ShortcutAction.KeyCode.F3,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btConsultarProduto.click();
			}
		};
		return slProdutos;
	}
	
	public ShortcutListener buildShortCutCancelarItem(){
		slCancelarPedido = new ShortcutListener("CancelarItem",ShortcutAction.KeyCode.F4,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelarPedido.click();
			}
		};
		return slCancelarPedido;
	}
	
	public ShortcutListener buildShortCutConcluirPedido(){
		slConcluirPedido = new ShortcutListener("ConcluirPedido",ShortcutAction.KeyCode.F5,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btConcluirPedido.click();
			}
		};
		return slConcluirPedido;
	}
	
	public Table buildTbGeneric() {
		tb = new Table();		
		tb.setSizeFull();
		tb.setSelectable(true);						
		tb.setImmediate(true);
		
		tb.addContainerProperty("Codigo", Integer.class, null);
		tb.addContainerProperty("Item", String.class, null);
		tb.addContainerProperty("Qtd.", String.class, null);
		tb.addContainerProperty("Valor Unit.(R$)", String.class, null);
		tb.addContainerProperty("Valor Total.(R$)", String.class, null);
				
		tb.setColumnWidth("Qtd.", 25);
		tb.setColumnAlignment("Qtd.", Align.CENTER);
		
		return tb;
	}
	
	
	public Button BuildbtAbrirPedido() {
		btAbrirPedido = new Button("Abrir Pedido (F2)", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				ClienteUtil clienteUtil = new ClienteUtil(true, true, null);
				clienteUtil.addListerner(new ClienteUtil.ClienteListerner() {
					
					@Override
					public void onSelected(ClienteEvent event) {
						if(event.getCliente() != null){
							cliente = event.getCliente();
							Notify.Show("Pedido Aberto com Sucesso.", Notify.TYPE_SUCCESS);
							
							btConsultarProduto.click();
						}else{
							Notify.Show("Não foi Possivel Abrir o Pedido!", Notify.TYPE_ERROR);
						}
					}
				});
				
				clienteUtil.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();						
					}
				});
				
				getUI().addWindow(clienteUtil);				
			}
		});

		return btAbrirPedido;
	}
	
	public Button BuildbtConsultarProduto() {
		btConsultarProduto = new Button("Produtos (F3)", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				SelecionarProdutoEditor servicoUtil = new SelecionarProdutoEditor(true, true);
				servicoUtil.addListerner(new SelecionarProdutoEditor.ServicoListerner() {
					
					@Override
					public void onSelected(com.digital.opuserp.view.modulos.ordemServico.pedidoServico.SelecionarProdutoEditor.ServicoEvent event) {
						addItem(event.getServico(), 1);
//						btConsultarProduto.click();
					}
				});
				
				servicoUtil.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();						
					}
				});
				
				getUI().addWindow(servicoUtil);
				
			}
		});

		return btConsultarProduto;
	}
	
	private void addItem(Servico s, Integer qtd){
		
		if(tb != null){			
			double valorTotal = Double.parseDouble(s.getValor_venda()) * qtd;
			tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), Real.formatDbToString(s.getValor_venda()), Real.formatDbToString(String.valueOf(valorTotal))}, s.getId());			
		}

		double totalGeral = 0;
		if(tb != null){
			for (Object itemId : tb.getItemIds()) {
				double valor = Double.parseDouble(Real.formatStringToDB(tb.getItem(itemId).getItemProperty("Valor Total.(R$)").getValue().toString()));
				totalGeral = totalGeral + valor;
			}
			hlValorTotal.replaceComponent(lbValorTotal, BuildLbValorTotal(Real.formatDbToString(String.valueOf(totalGeral))));
		}
		
	}
	
	public Button BuildbtCancelarPedido() {
		btCancelarPedido = new Button("Cancelar Pedido (F4)", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				Notify.Show("Implementar", Notify.TYPE_WARNING);
			}
		});

		return btCancelarPedido;
	}
	
	private Endereco endEntrega;
	
	public Button BuildbtConcluirPedido() {
		btConcluirPedido = new Button("Concluir Pedido (F5)", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(cliente != null && tb.getItemIds().size() > 0){
					GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Deseja Realmente Concluir este Pedido ?", true, true);
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								final ConfiguracaoPgtoEditor configPgtoEditor = new ConfiguracaoPgtoEditor("Forma de Pagamento", true);
								configPgtoEditor.addListerner(new ConfiguracaoPgtoEditor.ConfigFormaPgtoListerner() {
									
									@Override
									public void onClose(ConfigFormaPgtoEvent event) {
										if(event.isConfirm()){
											
											formaPgto = event.getFormaPgto();
											desconto = event.getDesconto();
											acrescimo = event.getAcrescimo();
											nParc = event.getnParc();
											dataPriVenc = event.getDataPriVenc();
											
											GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja Selecionar Endereço para Entrega ?", true, true);
											gd.addListerner(new GenericDialog.DialogListerner() {
												
												@Override
												public void onClose(DialogEvent event) {
													if(event.isConfirm()){
														
														final ConfiguracaoEndEntregaEditor cofnigEnEntrega = new ConfiguracaoEndEntregaEditor("Selecione o Endereço de Entrega", cliente, true);
														cofnigEnEntrega.addListerner(new ConfiguracaoEndEntregaEditor.ConfigEndEntregaListerner() {
															
															@Override
															public void onClose(ConfigEndEntregaEvent event) {
																if(event.isConfirm()){
																	endEntrega = event.getEndereco();
																	cofnigEnEntrega.close();
																	
																	List<Servico> itens = new ArrayList<>();
																	for (Object ob : tb.getItemIds().toArray()) {
																		itens.add((Servico)tb.getItem(ob));
																	}
																	
																	ConfiguracaoItensEditor configItens = new ConfiguracaoItensEditor("Fechar Venda", itens, true);
																	configItens.addListerner(new ConfiguracaoItensEditor.ConfigItensListerner() {
																		
																		@Override
																		public void onClose(ConfigItensEvent event) {
																			fecharPreVenda();
																		}
																	});
																	getUI().addWindow(configItens);
																	
																}
															}
														});
														
														
														getUI().addWindow(cofnigEnEntrega);
														
														
													}else{
														
														List<Servico> itens = new ArrayList<>();
														for (Object ob : tb.getItemIds().toArray()) {
															itens.add((Servico)tb.getItem(ob));
														}
														
														ConfiguracaoItensEditor configItens = new ConfiguracaoItensEditor("Fechar Venda", itens, true);
														configItens.addListerner(new ConfiguracaoItensEditor.ConfigItensListerner() {
															
															@Override
															public void onClose(ConfigItensEvent event) {
																fecharPreVenda();
															}
														});
														getUI().addWindow(configItens);
													}
													
												}
											});
											
											getUI().addWindow(gd);		
											configPgtoEditor.close();
										}
									}
								});
								
								getUI().addWindow(configPgtoEditor);							
							}
						}
					});
					
					getUI().addWindow(gd);		
				}else{
					btAbrirPedido.click();
				}
			}
		});

		return btConcluirPedido;
	}
	
	
	private void fecharPreVenda(){
		
		//TODO IMPLEMENTAR FECHAMENTO
		cancelarPreVenda();		
	}
	
	private void cancelarPreVenda(){
		tb.removeAllItems();
		hlValorTotal.replaceComponent(lbValorTotal, BuildLbValorTotal(Real.formatDbToString(String.valueOf("0.00"))));		
		cliente = null;
		formaPgto = null;
		desconto = 0;
		acrescimo = 0;
		nParc = null;
		dataPriVenc = null;
	}
	

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}
