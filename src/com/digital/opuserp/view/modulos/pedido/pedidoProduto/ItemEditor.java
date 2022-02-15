package com.digital.opuserp.view.modulos.pedido.pedidoProduto;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesSerialDAO;
import com.digital.opuserp.dao.EcfPreVendaDetalheDAO;
import com.digital.opuserp.dao.LogProdutoDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SeriaisPedidoDAO;
import com.digital.opuserp.dao.SerialDAO;
import com.digital.opuserp.dao.SerialProdutoDAO;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SeriaisPedido;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.QtdEditor.QtdEvent;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.SeriaisPedidooEditor.SerialPedidoEvent;
import com.digital.opuserp.view.util.ItemPedidoUtil;
import com.digital.opuserp.view.util.ItemPedidoUtil.ItemPedidoEvent;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ProdutoUtil;
import com.digital.opuserp.view.util.ServicoUtil;
import com.vaadin.data.Property;
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

	EcfPreVendaCabecalho pedido;
	
	Button btSave;
	Button btFinalizar;
	Button btCancelar;
	Button btConverter;
	Double  valorComDesconto;
	FormLayout flPrincipal;

	VerticalLayout vlRoot;
	
	Table tb;
	
	List<Float> valores = new ArrayList<Float>();
	
	public ItemEditor(final EcfPreVendaCabecalho pedido, String title, boolean modal){
		
		 
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
				
				
				final HorizontalLayout hlButtonConvert = new HorizontalLayout();				
				hlButtonConvert.setSpacing(true);
				hlButtonConvert.setStyleName("hl_buttons_bottom");
				hlButtonConvert.setMargin(true);
				
				hlButtonConvert.addComponent(buildBtConverter());
				
				
				final HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
							
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				
				addComponent(new HorizontalLayout(){
					{
						
						setWidth("100%");
						
						if(pedido.getTipo().equals("ORCAMENTO")){
							addComponent(hlButtonConvert);
							setComponentAlignment(hlButtonConvert, Alignment.BOTTOM_LEFT);
						}
						
						addComponent(hlButtons);
						setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
					}
				});
				
				
				
			}
		});
		
		
		
		buildLayout();			
		
	}
	
	
	private List<EcfPreVendaDetalhe> itens = new ArrayList<>();
	private Label lbPedido;
	private HorizontalLayout hlTopo;
	
	public void buildLayout(){
		
		vlRoot.addComponent(new FormLayout(){
			{
				hlTopo = new HorizontalLayout();
				hlTopo.setWidth("100%");
				
				lbPedido = new Label("<h2 style='margin:0;padding:0;'> "+pedido.getNaturezaOperacao().getDescricao()+" Nº: "+pedido.getId().toString()+"</h2>", ContentMode.HTML);
				
				
				hlTopo.addComponent(lbPedido);
				hlTopo.setComponentAlignment(lbPedido, Alignment.BOTTOM_LEFT);
				hlTopo.setExpandRatio(lbPedido, 1);
				
				final Button btAddItens = new Button("(F2) Adicionar Itens", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						if(pedido.getNaturezaOperacao().getTipo().equals("ENTRADA")){
							 ItemPedidoUtil itemPedidoUtil = new ItemPedidoUtil(true, true, pedido);
							 itemPedidoUtil.addListerner(new ItemPedidoUtil.ItemPedidoListerner() {
								
								@Override
								public void onSelected(ItemPedidoEvent event) {
									
									
									final EcfPreVendaDetalhe itemPedido = EcfPreVendaDetalheDAO.find(event.getItemId());
									List<SeriaisPedido> seriais = SeriaisPedidoDAO.getSeriais(itemPedido);
									List<SerialProduto> seriais_produto = new ArrayList<>();
									if(seriais.size() > 0){	
										
										for (SeriaisPedido serial : seriais) {
											seriais_produto.add(serial.getSerial());
											ConnUtil.getEntity().getTransaction().begin();
												SerialProduto serialP = serial.getSerial();
												serialP.setStatus("ATIVO");
												SerialDAO.atualizaeSerial(serialP); 											
											ConnUtil.getEntity().getTransaction().commit();
											
											
												AlteracoesSerialDAO.save(new AlteracoesSerial(null, "PRE-VENDA DEVOLUCAO "+itemPedido.getEcfPreVendaCabecalhoId(), serialP,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
											
										}
										
										
										EcfPreVendaDetalhe itemDevolucao = PedidoDAO.saveItem(new EcfPreVendaDetalhe(null, itemPedido.getProdutoId(), 
												pedido.getId(), tb.getItemIds().size()+1, itemPedido.getQuantidade(), itemPedido.getValorUnitario(), itemPedido.getValorTotal(), "N"));
										
										SeriaisPedidoDAO.save(itemDevolucao,seriais_produto);	
										
										itemPedido.setCancelado("S");
										ConnUtil.getEntity().getTransaction().begin();
										ConnUtil.getEntity().merge(itemPedido);
										ConnUtil.getEntity().getTransaction().commit();
										
										if(pedido.getNaturezaOperacao().getDescricao().equals("DEVOLUCAO EM GARANTIA")){
											Produto p = ProdutoDAO.find(itemPedido.getProdutoId());
											
											//-----Logg
											LogProdutoDAO.registraLog(new LogProduto(null,p, "PEDIDO DEVOLUCAO EM GARANTIA COM SERIAIS", p.getQtdEstoque(),p.getQtdEstoque()+itemPedido.getQuantidade(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
											//-----Logg
											
											//p.setQtdEstoque(p.getQtdEstoque());
											ProdutoDAO.edit(p,itemPedido.getQuantidade(),"S");		
										}
																							
										Float valor = itemDevolucao.getValorTotal();								

										addItem(ProdutoDAO.find(itemPedido.getProdutoId()), itemPedido.getQuantidade(),itemPedido.getValorTotal());		
										 
									}else{									
									
										EcfPreVendaDetalhe itemDevolucao = PedidoDAO.saveItem(new EcfPreVendaDetalhe(null, itemPedido.getProdutoId(), 
												pedido.getId(), tb.getItemIds().size()+1, itemPedido.getQuantidade(), itemPedido.getValorUnitario(), itemPedido.getValorTotal(), "N"));
																												
										itemPedido.setCancelado("S");
										ConnUtil.getEntity().getTransaction().begin();
										ConnUtil.getEntity().merge(itemPedido);
										ConnUtil.getEntity().getTransaction().commit();
																														
										if(pedido.getNaturezaOperacao().getDescricao().equals("DEVOLUCAO EM GARANTIA")){
											Produto p = ProdutoDAO.find(itemPedido.getProdutoId());
											
											//-----Logg
											LogProdutoDAO.registraLog(new LogProduto(null,p, "PEDIDO DEVOLUCAO EM GARANTIA SEM SERIAIS", p.getQtdEstoque(),p.getQtdEstoque()+itemPedido.getQuantidade(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
											//-----Logg
											
											//p.setQtdEstoque(p.getQtdEstoque()+itemPedido.getQuantidade());
											//ProdutoDAO.edit(p);		
											ProdutoDAO.edit(p,itemPedido.getQuantidade(),"A");		
										}

										Float valor = itemDevolucao.getValorTotal();										
										//valores.add(valor);
										
										addItem(ProdutoDAO.find(itemPedido.getProdutoId()), itemPedido.getQuantidade(),itemPedido.getValorUnitario());			
										
										//Haver h = HaverDAO.add(new Haver(null, pedido.getCliente(), "VALE COMPRA", "PV/"+pedido.getId().toString(), "DEVOLUÇÃO", OpusERP4UI.getUsuarioLogadoUI().getUsername(), new Date(), 	valor, "DISPONIVEL"));
										//AlteracoesHaverDAO.add(new AlteracoesHaver(null, "ABERTO", h, h.getValor(), 0, h.getValor(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										
									}		
																		
								}
							});
							 
							 
							  getUI().addWindow(itemPedidoUtil);
							 
						}else{
							if(pedido.getTipoVenda().equals("SERVICO")){
								ServicoUtil servicoUtil = new ServicoUtil(true, true, pedido.getCliente());
								servicoUtil.addListerner(new ServicoUtil.ServicoListerner() {
									
									@Override
									public void onSelected(ServicoUtil.ServicoEvent event) {
										
										final Servico p = event.getServico();
										double valor_final = 0;
										
										if(p != null && pedido.getCliente() != null && pedido.getCliente().getTabela_preco() != null && p.getOutras_tb_desc().contains("SIM")){
											
											double percentual = pedido.getCliente().getTabela_preco().getDesconto() / 100;										
											valor_final = new Double(p.getValor_venda())  - (percentual * new Double(p.getValor_venda()));
											valorComDesconto = valor_final;
											
											//p.setValor_venda(String.valueOf(valor_final));
										}else{										
											valor_final =p != null && p.getValor_venda() != null ? new Double(p.getValor_venda()) : 0;
										}
										
										
										boolean fracionar = false;
										
										if(p != null){
											if(p.getFracionar()!= null && p.getFracionar() == 1){
												fracionar = true;
												}												
										}
										
										final double valor_final2 = valor_final;
										QtdEditor qtdEditor = new QtdEditor("Quantidade", true, fracionar, null,true);
										qtdEditor.addListerner(new QtdEditor.QtdListerner() {
											
											@Override
											public void onClose(QtdEvent event) {
												if(event.isConfirm()){
													
													addItem(p, event.getQtd(),valor_final2);												
													
													double valorTotal = new Double(valor_final2) * event.getQtd();
													PedidoDAO.saveItem(new EcfPreVendaDetalhe(null, p.getId(), pedido.getId(), tb.getItemIds().size()+1, new Float(event.getQtd()), new Float(valor_final2), new Float(valorTotal), "N"));
													
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
													
								
								getUI().addWindow(servicoUtil);
							}
							
							
							if(pedido.getTipoVenda().equals("PRODUTO")){
								ProdutoUtil produtoUtil = new ProdutoUtil(true, true, pedido.getCliente(), pedido.getTipo_produto());
								produtoUtil.addListerner(new ProdutoUtil.ProdutoListerner() {
									
									@Override
									public void onSelected(ProdutoUtil.ProdutoEvent event) {
										
										if(event.getProduto().getQtdEstoque()>0 || !pedido.getTipo().equals("PEDIDO") ){
												final Produto p = event.getProduto();
												double valor_final=0;
												
												
												if(p != null && p.getPreco_promocional() != null && p.getPreco_promocional() > 0){
													valor_final = p.getPreco_promocional();
												}else{
													if(pedido.getCliente() != null && pedido.getCliente().getTabela_preco() != null && p.getOutras_tb_desc().contains("SIM")){
														
														double percentual = pedido.getCliente().getTabela_preco().getDesconto() / 100;
														valor_final = p.getValorVenda() - (percentual * p.getValorVenda());
														valorComDesconto = valor_final;
													}else{																								
														valor_final = p.getValorVenda();													
													}
												}
												
												
												
												
												boolean fracionar = false;
												
												if(p != null){
													if(p.getFracionar()!= null && p.getFracionar() == 1){
														fracionar = true;
														}												
												}
												
												final double valor_final2 = valor_final;
												QtdEditor qtdEditor = new QtdEditor("Quantidade", true, fracionar, p.getQtdEstoque(),true);
												qtdEditor.addListerner(new QtdEditor.QtdListerner() {
													
													@Override
													public void onClose(QtdEvent event) {
														if(event.isConfirm()){
															final Float qtd = event.getQtd();
															
															
																if(p.getUtilizaSeriais() != null && p.getUtilizaSeriais().equals("SIM") && p.getQtdEstoque() > 0){
																	final SeriaisPedidooEditor serialPedidoEditor = new SeriaisPedidooEditor(p,qtd,"Selecione o(s) Seriais",pedido.getTipo().toString(), true);
																	serialPedidoEditor.addListerner(new SeriaisPedidooEditor.SerialPedidoListerner() {
																		
																		@Override
																		public void onClose(SerialPedidoEvent event) {
																			if(event.isConfirm()){
																																							
																				addItem(p, qtd, valor_final2);
																				
																				if(pedido.getTipo().equals("PEDIDO")){
																					
																					//-----Logg
																					LogProdutoDAO.registraLog(new LogProduto(null,p, "PEDIDO BAIXA DE PRODUTO COM SERIAIS", p.getQtdEstoque(),p.getQtdEstoque()-qtd, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
																					//-----Logg
																					
																					//p.setQtdEstoque(p.getQtdEstoque()-qtd);
																					boolean check = ProdutoDAO.edit(p,qtd,"S");		
																					
																					if(check){
																						List<SerialProduto> seriais = event.getSeriais();
																						for (SerialProduto sp : seriais) {
																							sp.setStatus(pedido.getNaturezaOperacao().getStatus_saida());
																							SerialProdutoDAO.save(sp);
																						}
																						
																						double valorTotal = new Double(valor_final2) * qtd;//																				
																						EcfPreVendaDetalhe itemPedido = PedidoDAO.saveItem(new EcfPreVendaDetalhe(null, p.getId(), pedido.getId(), tb.getItemIds().size()+1, new Float(qtd), new Float(valor_final2), new Float(valorTotal), "N"));
																				
																						if(itemPedido != null && event.getSeriais() != null && event.getSeriais().size() > 0){
																							SeriaisPedidoDAO.save(itemPedido, event.getSeriais());
																							SerialDAO serialDAO = new SerialDAO();
																							
																							if(pedido.getTipo().equals("PEDIDO")){
																								for(SerialProduto serial: event.getSeriais()){
																									serial.setData_alteracao(new Date());
																									serialDAO.atualizaeSerial(serial);
																									AlteracoesSerialDAO.save(new AlteracoesSerial(null, "PRE-VENDA "+itemPedido.getEcfPreVendaCabecalhoId(), serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));																					
																								}
																							}
																						}
																						
																						serialPedidoEditor.close();
																					}
																				}else{																
																				
																					double valorTotal = new Double(valor_final2) * qtd;//																				
																					EcfPreVendaDetalhe itemPedido = PedidoDAO.saveItem(new EcfPreVendaDetalhe(null, p.getId(), pedido.getId(), tb.getItemIds().size()+1, new Float(qtd), new Float(valor_final2), new Float(valorTotal), "N"));
																			
																					if(itemPedido != null && event.getSeriais() != null && event.getSeriais().size() > 0){
																						SeriaisPedidoDAO.save(itemPedido, event.getSeriais());
																						SerialDAO serialDAO = new SerialDAO();
																						
																						if(pedido.getTipo().equals("PEDIDO")){
																							for(SerialProduto serial: event.getSeriais()){
																								serial.setData_alteracao(new Date());
																								serialDAO.atualizaeSerial(serial);
																								AlteracoesSerialDAO.save(new AlteracoesSerial(null, "PRE-VENDA "+itemPedido.getEcfPreVendaCabecalhoId(), serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));																					
																							}
																						}
																					}
																					
																					serialPedidoEditor.close();
																				}
																			}
																		}
																	});
																	
																	getUI().addWindow(serialPedidoEditor);
																	
																	serialPedidoEditor.addCloseListener(new Window.CloseListener() {
																		
																		@Override
																		public void windowClose(CloseEvent e) {
																			tb.focus();															
																		}
																	});
																	
																}else{																
																	
																	
																	if(pedido.getTipo().equals("PEDIDO")){
																		
																		//-----Logg
																		LogProdutoDAO.registraLog(new LogProduto(null,p, "PEDIDO BAIXA DE PRODUTO SEM SERIAIS", p.getQtdEstoque(),p.getQtdEstoque()-event.getQtd(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
																		//-----Logg
																		
																		//p.setQtdEstoque(p.getQtdEstoque()-event.getQtd());
																		boolean check = ProdutoDAO.edit(p,event.getQtd(),"S");		

																		if(check){
																			addItem(p, event.getQtd(), valor_final2);
																			double valorTotal = new Double(valor_final2) * event.getQtd();																
																			PedidoDAO.saveItem(new EcfPreVendaDetalhe(null, p.getId(), pedido.getId(), tb.getItemIds().size()+1, new Float(event.getQtd()), new Float (valor_final2), new Float(valorTotal), "N"));
																		}
																	}else{
																		addItem(p, event.getQtd(), valor_final2);
																		double valorTotal = new Double(valor_final2) * event.getQtd();																
																		PedidoDAO.saveItem(new EcfPreVendaDetalhe(null, p.getId(), pedido.getId(), tb.getItemIds().size()+1, new Float(event.getQtd()), new Float (valor_final2), new Float(valorTotal), "N"));
																	}
																																		
																	
																}
															
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
										
										}else{
											Notify.Show("Produto Escolhido Não tem Saldo!", Notify.TYPE_ERROR);
										}
									}
								});
													
								
								getUI().addWindow(produtoUtil);
							}
						}
						
						
						
						
						
						
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
		
		
		
		
		
		List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(pedido.getId());
		
		for (EcfPreVendaDetalhe item : itens) {
			
			
			if(pedido.getTipoVenda().equals("PRODUTO")){
				Produto p = ProdutoDAO.find(item.getProdutoId());
				double valor_final = 0;		
				if(p != null && pedido.getCliente() != null && pedido.getCliente().getTabela_preco() != null && p.getOutras_tb_desc().contains("SIM")){
					
					double percentual = pedido.getCliente().getTabela_preco().getDesconto() / 100;										
					valor_final = new Double(p.getValorVenda())  - (percentual * new Double(p.getValorVenda()));
									
					item.setValorUnitario(Float.parseFloat(String.valueOf(valor_final)));
				
					addItem(p, item.getQuantidade(), valor_final);
				}else{				
					addItem(p, item.getQuantidade(), p.getValorVenda());
				}							
			}
			
			if(pedido.getTipoVenda().equals("SERVICO")){
				Servico p = ServicoDAO.find(item.getProdutoId());
				double valor_final = 0;
								
				if(p != null && pedido.getCliente() != null && pedido.getCliente().getTabela_preco() != null && p.getOutras_tb_desc().contains("SIM") ){
					
					double percentual = pedido.getCliente().getTabela_preco().getDesconto() / 100;										
					valor_final = new Double(p.getValor_venda())  - (percentual * new Double(p.getValor_venda()));
					
					item.setValorUnitario(Float.parseFloat(String.valueOf(valor_final)));
									
					addItem(p, item.getQuantidade(),valor_final);
				}else{				
					addItem(p, item.getQuantidade(),new Double(p.getValor_venda()));
				}
			
			}						
		}
	}
	
	HorizontalLayout hlBase;
	Label lbSubTotal;
	Button btDeletar;
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
				
				if(colId.equals("id")){
					
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
		
		if(pedido.getTipoVenda().equals("PRODUTO")){
			tb.addContainerProperty("Produto", String.class, null);
			tb.setColumnExpandRatio("Produto", 1);
		}else{
			tb.addContainerProperty("Serviço", String.class, null);
			tb.setColumnExpandRatio("Serviço", 1);
		}
		
		tb.addContainerProperty("Qtd.", String.class, null);
		tb.addContainerProperty("Valor Unitario", String.class, null);
		tb.addContainerProperty("Valor Total", String.class, null);
		
		tb.setColumnAlignment("Valor Unitário", Align.RIGHT);
		tb.setColumnAlignment("Valor Total", Align.RIGHT);
		tb.setColumnHeader("Codigo", "Código");
		tb.setColumnHeader("Valor Unitario", "Valor Unitário");
		
		
		tb.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				
				
				
				btDeletar = new Button(null, new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Cancelar este Item?", true,true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){		
									if(pedido.getTipoVenda().equals("PRODUTO")){
										
										itens.remove(source.getItem(itemId));
										removerItem(ProdutoDAO.find((Integer)source.getItem(itemId).getItemProperty("Codigo").getValue()));
										Notify.Show("Item Cancelado com Sucesso!", Notify.TYPE_SUCCESS);
									}else{
										itens.remove(source.getItem(itemId));
										removerItem(ServicoDAO.find((Integer)source.getItem(itemId).getItemProperty("Codigo").getValue()));
										Notify.Show("Item Cancelado com Sucesso!", Notify.TYPE_SUCCESS);
									}
									
									
								}
								btDeletar.setEnabled(true); 
							}
						});
						
						getUI().addWindow(gd);
					}
				});
				btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
				btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
				btDeletar.setDescription("Cancelar Item");
				btDeletar.setDisableOnClick(true); 
				
				return btDeletar;
			}
		});

		tb.setColumnWidth("x", 20);
				
		tb.setColumnWidth("Qtd.", 35);
		tb.setColumnAlignment("Qtd.", Align.CENTER);

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
			
			for(EcfPreVendaDetalhe item: PedidoDAO.getItensPedido(pedido.getId())){
				if(item.getProdutoId().equals(s.getId())){
					PedidoDAO.deleteItem(item);
					
					EcfPreVendaCabecalho pedido = PedidoDAO.find(item.getEcfPreVendaCabecalhoId());
					if(pedido.getTipo().equals("PEDIDO") && pedido.getNaturezaOperacao().getDescricao().equals("VENDA")){	
						
						//-----Logg
						LogProdutoDAO.registraLog(new LogProduto(null,s, "REMOVEU ITEM PEDIDO", s.getQtdEstoque(),s.getQtdEstoque()+item.getQuantidade(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
						//-----Logg
						
						//s.setQtdEstoque(s.getQtdEstoque()+item.getQuantidade());
						ProdutoDAO.edit(s, item.getQuantidade(),"A");					
					}
					
					if(pedido.getNaturezaOperacao().getDescricao().equals("DEVOLUCAO EM GARANTIA")){
						//-----Logg
						LogProdutoDAO.registraLog(new LogProduto(null,s, "REMOVEU ITEM PEDIDO DEVOLUCAO EM GARANTIA", s.getQtdEstoque(),s.getQtdEstoque()-item.getQuantidade(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
						//-----Logg
						
						//s.setQtdEstoque(s.getQtdEstoque()-item.getQuantidade());
						ProdutoDAO.edit(s,item.getQuantidade(),"S");		
						
						
						List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(pedido.getN_nf_devolucao().getId());
						for (EcfPreVendaDetalhe ecfPreVendaDetalhe : itens) {
							
							EcfPreVendaDetalhe itemPedidoSaida = ecfPreVendaDetalhe;
							if(itemPedidoSaida.getProdutoId().equals(s.getId())){
								ConnUtil.getEntity().getTransaction().begin();
								itemPedidoSaida.setCancelado("N");
								ConnUtil.getEntity().merge(itemPedidoSaida);
								ConnUtil.getEntity().getTransaction().commit();
								
								Query q = ConnUtil.getEntity().createQuery("select sp from SeriaisPedido sp where sp.itemPedido = :item", SeriaisPedido.class);
								q.setParameter("item", itemPedidoSaida);
								
								for (SeriaisPedido serialPedido : (List<SeriaisPedido>)q.getResultList()) {
									SerialProduto serial = serialPedido.getSerial();
									
									ConnUtil.getEntity().getTransaction().begin();
									serial.setStatus("VENDIDO");
									ConnUtil.getEntity().merge(serial);
									ConnUtil.getEntity().getTransaction().commit();
								}
							}
						}

					}
				}
			}
			
			
			itens = new ArrayList<>();
			
			for(Object itemId : tb.getItemIds().toArray()){				
				itens.add(new EcfPreVendaDetalhe(null, s.getId(), pedido.getId(), tb.getItemIds().size()+1, new Float(tb.getItem(itemId).getItemProperty("Qtd.").getValue().toString()), s.getValorVenda(), new Float(Real.formatStringToDBDouble(tb.getItem(itemId).getItemProperty("Valor Total").getValue().toString())), "N"));
			}
			
			double totalGeral = 0;
			for (Object itemId : tb.getItemIds()) {
				double valor = Double.parseDouble(Real.formatStringToDB(tb.getItem(itemId).getItemProperty("Valor Total").getValue().toString()));
				totalGeral = totalGeral + valor;
			}
			
			hlBase.replaceComponent(lbSubTotal, BuildLbValorTotal(Real.formatDbToString(String.valueOf(totalGeral))));
			pedido.setSubTotal(new Float(totalGeral));
			pedido.setValor(new Float(totalGeral));
		}
	}
	
	private void removerItem(Servico s){
		if(tb != null){
			
			for(Object itemId : tb.getItemIds().toArray()){
				if(tb.getItem(itemId).getItemProperty("Codigo").getValue().equals(s.getId())){
					tb.removeItem(itemId);
				}
			}
			
			for(EcfPreVendaDetalhe item: PedidoDAO.getItensPedido(pedido.getId())){
				if(item.getProdutoId().equals(s.getId())){					
					PedidoDAO.deleteItem(item);					
				}
			}
			

			itens = new ArrayList<>();
			
			for(Object itemId : tb.getItemIds().toArray()){		
				itens.add(new EcfPreVendaDetalhe(null, s.getId(), pedido.getId(), tb.getItemIds().size()+1, new Float(tb.getItem(itemId).getItemProperty("Qtd.").getValue().toString()), new Float(s.getValor_venda()), new Float(Real.formatStringToDBDouble(tb.getItem(itemId).getItemProperty("Valor Total").getValue().toString())), "N"));

			}			
			
			double totalGeral = 0;
			for (Object itemId : tb.getItemIds()) {
				double valor = Double.parseDouble(Real.formatStringToDB(tb.getItem(itemId).getItemProperty("Valor Total").getValue().toString()));
				totalGeral = totalGeral + valor;
			}
			
			hlBase.replaceComponent(lbSubTotal, BuildLbValorTotal(Real.formatDbToString(String.valueOf(totalGeral))));
			pedido.setSubTotal(new Float(totalGeral));
			pedido.setValor(new Float(totalGeral));
		}
	}
	
	
	private void addItem(Produto s, Float qtd,double valorFinal){
		
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
				
				
				if(s != null && s.getPreco_promocional() != null && s.getPreco_promocional() > 0){
					valorComDesconto = Double.parseDouble(String.valueOf(s.getPreco_promocional()));
				}else{
					
					if(pedido.getCliente() != null && pedido.getCliente().getTabela_preco() != null && s.getOutras_tb_desc().contains("SIM")){
						
						double percentual = pedido.getCliente().getTabela_preco().getDesconto() / 100;
						valorComDesconto = s.getValorVenda() - (percentual * s.getValorVenda());
					}else{
						valorComDesconto = Double.parseDouble(String.valueOf(s.getValorVenda()));
					}
				}
				
				
				qtd = qtd+qtd_antiga;
				double valorTotal = valorComDesconto * qtd;
				
				if(pedido.getTipo().equals("DEVOLUCAO")){
					tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), 
							Real.formatDbToString(String.valueOf(valorFinal).toString()), 
							Real.formatDbToString(String.valueOf(valorFinal))}, s.getId());
				}else{
					tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), Real.formatDbToString(String.valueOf(valorComDesconto).toString()), Real.formatDbToString(String.valueOf(valorTotal))}, s.getId());
				}

				itens = new ArrayList<>();
				
				for(Object ob: tb.getItemIds().toArray()){					
					itens.add(new EcfPreVendaDetalhe(null, (Integer)tb.getItem(ob).getItemProperty("Codigo").getValue(), pedido.getId(), tb.getItemIds().size()+1, new Float(tb.getItem(ob).getItemProperty("Qtd.").getValue().toString()), Real.formatStringToDBFloat(tb.getItem(ob).getItemProperty("Valor Unitario").getValue().toString()), Real.formatStringToDBFloat(tb.getItem(ob).getItemProperty("Valor Total").getValue().toString()), "N"));
				}

				
			}else{
				
				
				if(s != null && s.getPreco_promocional() != null && s.getPreco_promocional() > 0){
					valorComDesconto = Double.parseDouble(String.valueOf(s.getPreco_promocional()));
				}else{
					
					if(pedido.getCliente() != null && pedido.getCliente().getTabela_preco() != null && s.getOutras_tb_desc().contains("SIM")){
						
						double percentual = pedido.getCliente().getTabela_preco().getDesconto() / 100;
						valorComDesconto = s.getValorVenda() - (percentual * s.getValorVenda());
					}else{
						valorComDesconto = Double.parseDouble(String.valueOf(s.getValorVenda()));
					}
				}
				
								
				double valorTotal = valorComDesconto * qtd;
				
				if(pedido.getTipo().equals("DEVOLUCAO")){
					tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), 
							Real.formatDbToString(String.valueOf(valorFinal).toString()), 
							Real.formatDbToString(String.valueOf(valorFinal))}, s.getId());
				}else{
					tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), Real.formatDbToString(String.valueOf(valorComDesconto).toString()), Real.formatDbToString(String.valueOf(valorTotal))}, s.getId());
				}
			
				itens = new ArrayList<>();
				
				for(Object ob: tb.getItemIds().toArray()){
					itens.add(new EcfPreVendaDetalhe(null, (Integer)tb.getItem(ob).getItemProperty("Codigo").getValue(), pedido.getId(), tb.getItemIds().size()+1, new Float(tb.getItem(ob).getItemProperty("Qtd.").getValue().toString()), Real.formatStringToDBFloat(tb.getItem(ob).getItemProperty("Valor Unitario").getValue().toString()), Real.formatStringToDBFloat(tb.getItem(ob).getItemProperty("Valor Total").getValue().toString()), "N"));
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
		}
		
	}
	
	private void addItem(Servico s, Float qtd, double valorFinal){
		
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
				
				
				if(pedido.getCliente() != null && pedido.getCliente().getTabela_preco() != null && s.getOutras_tb_desc().contains("SIM")){
					
					double percentual = pedido.getCliente().getTabela_preco().getDesconto() / 100;
					valorComDesconto =  new Double(s.getValor_venda())  - (percentual * new Double(s.getValor_venda()));
				}else{
					valorComDesconto = Double.parseDouble(String.valueOf(s.getValor_venda()));
				}
				
				
				qtd = qtd+qtd_antiga;
				double valorTotal = new Float(valorFinal) * qtd;
//				tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), Real.formatDbToString(String.valueOf(valorTotal)), Real.formatDbToString(String.valueOf(valorTotal))}, s.getId());
				tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), Real.formatDbToString(String.valueOf(valorComDesconto).toString()), Real.formatDbToString(String.valueOf(valorTotal))}, s.getId());
				
				itens = new ArrayList<>();
				
				for(Object ob: tb.getItemIds().toArray()){					
					//itens.add(new EcfPreVendaDetalhe(null, (Integer)tb.getItem(ob).getItemProperty("Codigo").getValue(), pedido.getId(), tb.getItemIds().size()+1, new Float(qtd), new Float(Real.formatStringToDBDouble(s.getValor_venda())), new Float(valorTotal), "N"));
					itens.add(new EcfPreVendaDetalhe(null, (Integer)tb.getItem(ob).getItemProperty("Codigo").getValue(), pedido.getId(), tb.getItemIds().size()+1, new Float(tb.getItem(ob).getItemProperty("Qtd.").getValue().toString()), Real.formatStringToDBFloat(tb.getItem(ob).getItemProperty("Valor Unitario").getValue().toString()), Real.formatStringToDBFloat(tb.getItem(ob).getItemProperty("Valor Total").getValue().toString()), "N"));
				}

				
			}else{
				
				if(pedido.getCliente() != null && pedido.getCliente().getTabela_preco() != null && s.getOutras_tb_desc().contains("SIM")){
					
					double percentual = pedido.getCliente().getTabela_preco().getDesconto() / 100;
					valorComDesconto = 	new Double(s.getValor_venda())  - (percentual * new Double(s.getValor_venda()));
				}else{
					valorComDesconto = Double.parseDouble(String.valueOf(s.getValor_venda()));
				}
				
				double valorTotal = valorFinal * qtd;
//				tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), Real.formatDbToString(s.getValor_venda()), Real.formatDbToString(String.valueOf(valorTotal))}, s.getId());
				tb.addItem(new Object[]{s.getId(),s.getNome(),qtd.toString(), Real.formatDbToString(String.valueOf(valorComDesconto).toString()), Real.formatDbToString(String.valueOf(valorTotal))}, s.getId());

				
				itens = new ArrayList<>();
				
				for(Object ob: tb.getItemIds().toArray()){					
				
					//itens.add(new EcfPreVendaDetalhe(null, s.getId(), pedido.getId(), tb.getItemIds().size()+1, new Float(qtd), new Float(Real.formatStringToDBDouble(s.getValor_venda())), new Float(valorTotal), "N"));
					itens.add(new EcfPreVendaDetalhe(null, (Integer)tb.getItem(ob).getItemProperty("Codigo").getValue(), pedido.getId(), tb.getItemIds().size()+1, new Float(tb.getItem(ob).getItemProperty("Qtd.").getValue().toString()), Real.formatStringToDBFloat(tb.getItem(ob).getItemProperty("Valor Unitario").getValue().toString()), Real.formatStringToDBFloat(tb.getItem(ob).getItemProperty("Valor Total").getValue().toString()), "N"));
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
		}
		
	}
	private Label BuildLbValorTotal(String valor){
		return lbSubTotal = new Label("<h2 style='text-align: right;'>SubTotal R$ "+valor+"</h2>", ContentMode.HTML);
		
	}
	
	public Button buildBtConverter(){
		btConverter = new Button("Converter em Pedido", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					
					GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Converter este Orçamento em Pedido ?", true, true);
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								
								if(pedido.getTipoVenda().equals("PRODUTO")){
									List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(pedido.getId());
									boolean allow = true;
									for (EcfPreVendaDetalhe produto : itens) {
										Produto p = ProdutoDAO.find(produto.getProdutoId());
										List<SeriaisPedido> seriais = SeriaisPedidoDAO.getSeriais(new EcfPreVendaDetalhe(produto.getEcfPreVendaCabecalhoId()));
																				
										if(p.getQtdEstoque() < produto.getQuantidade() || seriais.size() ==0 && p.getUtilizaSeriais().equals("SIM")){
											allow = false;
											//break;
											
											if(p.getQtdEstoque() < produto.getQuantidade()){
												Notify.Show("Item: "+p.getId()+" com saldo indisponível, adicione outro produto !", Notify.TYPE_ERROR);
											}
											
											if(seriais.size() == 0){
												Notify.Show("Item: "+p.getId()+" não tem serial,  adicione outro produto !", Notify.TYPE_ERROR);
											}
										}
									}
									
									
									
									if(allow){
										
										for (EcfPreVendaDetalhe item : itens) {
											Produto p = ProdutoDAO.find(item.getProdutoId());
											
											//-----Logg
											LogProdutoDAO.registraLog(new LogProduto(null,p, "CONVERTEU EM PEDIDO", p.getQtdEstoque(),p.getQtdEstoque()-item.getQuantidade(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
											//-----Logg
											
											//p.setQtdEstoque(p.getQtdEstoque()-item.getQuantidade());
											ProdutoDAO.edit(p,item.getQuantidade(),"S" );												
										}
										
										pedido.setTipo("PEDIDO");			
										pedido.setData(new Date());
										PedidoDAO.save(pedido);
										
										hlTopo.replaceComponent(lbPedido, new Label("<h2 style='margin:0;padding:0;'> Pedido Nº: "+pedido.getId().toString()+"</h2>", ContentMode.HTML));
																
										Notify.Show("Orçamento Convertido em Pedido!", Notify.TYPE_SUCCESS);
									}else{
										//Notify.Show("Não foi Possivel Converter este Orçamento em Pedido, um ou mais Itens não Disponíveis no Estoque!", Notify.TYPE_ERROR);
									}
								}else{
								
									pedido.setTipo("PEDIDO");									
									PedidoDAO.save(pedido);
									
									hlTopo.replaceComponent(lbPedido, new Label("<h2 style='margin:0;padding:0;'> Pedido Nº: "+pedido.getId().toString()+"</h2>", ContentMode.HTML));
									Notify.Show("Orçamento Convertido em Pedido!", Notify.TYPE_SUCCESS);
								}
							}
						}
					});
					
					
					gd.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
								tb.focus();								
						}
					});
					
					
					getUI().addWindow(gd);
					
				}catch(Exception e){
					e.printStackTrace();
					Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
				}
			}
		});
		
		return btConverter;
	}
		
	@Override
	public Button buildBtSalvar() {
		
		btFinalizar = new Button("(F10) Finalizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(itens.size() > 0){
					try {												
						Object[] itemIds = tb.getItemIds().toArray();
						
						valores.clear();
						for (Object itemId: itemIds) {
							double valor = Real.formatStringToDBDouble(tb.getItem(itemId).getItemProperty("Valor Total").getValue().toString());
							valores.add(new Float(valor));						
						}
						
						fireEvent(new ItemEvent(getUI(),itens, valores, true));						
							
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
				
									
				GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja Cancelar este Pedido ?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						
						if(event.isConfirm()){
							boolean check = PedidoDAO.CancelarPedido(pedido);
							if(check){
								close();
								Notify.Show("Pedido Cancelado com Sucesso",Notify.TYPE_SUCCESS);
							}
						}else{
							fireEvent(new ItemEvent(getUI(),itens, valores,false));
							close();
						}
					}
				});
				
				getUI().addWindow(gd);				
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
		private List<EcfPreVendaDetalhe> itens;
		private List<Float> valores;
		
		public ItemEvent(Component source, List<EcfPreVendaDetalhe> itens, List<Float> valores, boolean confirm) {
			super(source);
		
			this.itens = itens;
			this.valores = valores;
			this.confirm = confirm;			
		}
		
		
		public List<EcfPreVendaDetalhe> getItens(){
			return itens;
		}
		public List<Float> getValores(){
			return valores;
		}
		
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ItemListerner extends Serializable{
		public void onClose(ItemEvent event);
	}

	
}
