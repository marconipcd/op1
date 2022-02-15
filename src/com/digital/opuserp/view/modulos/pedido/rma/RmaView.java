package com.digital.opuserp.view.modulos.pedido.rma;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.NaturezaOperacaoDAO;
import com.digital.opuserp.dao.RmaDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.NaturezaOperacao;
import com.digital.opuserp.domain.RmaDetalhe;
import com.digital.opuserp.domain.RmaMestre;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.pedido.rma.RmaNew.RmaNewEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class RmaView extends VerticalLayout {
	
	private TreeTable tb;
	private TextField tfBusca;
	
	private Button btNovo;
	private Button btEditar;
	private Button btFechar;
	private Button btStatus;
	private Button btLog;
	private Button btImprimir;
	private Button btExcluir;	
	
	HorizontalLayout hlFloat;	
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;	
	
	private ComboBox cbStatus;
	private ComboBox cbFornecedor;
	
	public RmaView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
            
			hlButons.addComponent(BuildbtNovo());
			hlButons.addComponent(BuildbtEditar());
			hlButons.addComponent(BuildbtFechar());
			hlButons.addComponent(BuildbtStatus());
			hlButons.addComponent(BuildbtLog());		
			hlButons.addComponent(BuildbtImprimir());			
			hlButons.addComponent(BuildbtExcluir());
						
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildCbStatus());
					addComponent(buildCbFornecedor());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
			lbLegend.setWidth("285px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
			
			refresh("");
		}
	}
	
	public ComboBox buildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false); 
		cbStatus.addItem("ABERTO");
		cbStatus.addItem("FECHADO");
		
		cbStatus.select("ABERTO");

		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
					refresh(tfBusca.getValue());									
			}
		});
		
		return cbStatus;		
	}
	
	public ComboBox buildCbFornecedor(){
		JPAContainer<Fornecedor> container = JPAContainerFactory.makeReadOnly(Fornecedor.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("fornecedor",true));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		container.sort(new Object[]{"razao_social"}, new boolean[]{true});
		
		cbFornecedor = new ComboBox(null, container);
		cbFornecedor.setNullSelectionAllowed(true);
		cbFornecedor.setItemCaptionPropertyId("razao_social");		
		cbFornecedor.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {				
				refresh("");				
			}
		});
				
		return cbFornecedor;
		
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof RmaView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btStatus.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btStatus != null || slEditar != null){
				btStatus.removeShortcutListener(slEditar);			
			}
		}
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
				btStatus.click();
			}
		};
		return slEditar;
	}
	
	public Table buildTbGeneric() {
		tb = new TreeTable();
		
		
		tb.setWidth("100%");
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		//tb.setMultiSelect(true);
		tb.setMultiSelectMode(MultiSelectMode.DEFAULT);
		
		tb.addContainerProperty("Cod", Integer.class, null);
		tb.addContainerProperty("Fornecedor", String.class, "");
		tb.addContainerProperty("Produto", String.class, "");
		tb.addContainerProperty("Serial", String.class, "");
		tb.addContainerProperty("NF.Compra", String.class, "");
		tb.addContainerProperty("Valor", String.class, "");
		tb.addContainerProperty("Defeito", String.class, "");
		tb.addContainerProperty("Venc", Date.class, null);		
		tb.addContainerProperty("Status", String.class, "");
		tb.addContainerProperty("Alteração", Date.class, null);	
		
		tb.setColumnCollapsed("Cod", true);		
		tb.setColumnAlignment("Valor", Align.RIGHT);
		
		tb.setVisibleColumns(new Object[]{"Cod","Fornecedor","Produto","Serial","NF.Compra","Valor","Defeito","Venc","Status","Alteração"});
				
		tb.setImmediate(true);
		
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				((Table) event.getSource()).select(event.getItemId());
				if(event.isDoubleClick()){
					if(event.getItem() != null){
					//	addFilter(event.getItem().getItemProperty("Cliente").getValue().toString());
					}
				}				
			}
		});
		
		
		
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){						
					btImprimir.setEnabled(true);
					btExcluir.setEnabled(true);
					btStatus.setEnabled(true);
					btLog.setEnabled(true);
					btEditar.setEnabled(true);
				}else{
					btExcluir.setEnabled(false);
					btLog.setEnabled(false);
					btStatus.setEnabled(false);
					btImprimir.setEnabled(false);
					btEditar.setEnabled(false);
				}
			}
		});
		
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				
				if(source.getItem(itemId).getItemProperty("Fornecedor").getValue() != null && source.getItem(itemId).getItemProperty("Produto").getValue() == null){
					return "bold";
				}
				return null;
			}
		});
		
		return tb;
	}
	Integer qtdRegistros = 0;
	private void refresh(String valorBusca){
		
		if(tb != null)

		tb.removeAllItems();
		qtdRegistros = 0;
		
		Float qtdRegistrosAReceber = 0.00f;
		Float qtdRegistrosPagos = 0.00f;
		
		List<RmaDetalhe> result = null;
		List<RmaMestre> resultMestre = null;

		EntityManager em = ConnUtil.getEntity();
		Query qMestre;
		Query q;
		
		qMestre= em.createQuery("select m from RmaMestre m where m.empresa_id =:empresa and m.status =:status", RmaMestre.class);
		qMestre.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		qMestre.setParameter("status", cbStatus.getValue().toString());
		
		resultMestre = qMestre.getResultList();
		
		Integer i = 0;			
		Integer parentId = null;
		
		for(RmaMestre c:resultMestre){
			
													
					q = em.createQuery("select r from RmaDetalhe r where r.rma_mestre_id =:mestre", RmaDetalhe.class);
					q.setParameter("mestre", c.getId());
					result = q.getResultList();
					
					if(q.getResultList().size() > 0){
				
						tb.addItem(new Object[]{c.getId(), c.getFornecedor().getFantasia(),null,null,null,null,null,null,null,null}, i);
						tb.setChildrenAllowed(i, true);
						parentId = i;
						i++;
						
						
						for(RmaDetalhe det:result){
							if( cbStatus.getValue().toString().equals("ABERTO") && !det.getStatus().equals("CREDITADO")){
								tb.addItem(new Object[]{det.getId(),null,det.getProduto().getNome(),det.getSerial() != null ? det.getSerial().getSerial() : "", det.getNf_compra(),Real.formatDbToString(String.valueOf(det.getProduto().getValorCusto())),det.getDefeito().toUpperCase(), det.getVenc(), det.getStatus(), det.getData_mudanca_status()}, i);
								tb.setChildrenAllowed(i, false);						
								tb.setParent(i, parentId);
								i++;
							}
							
							if( cbStatus.getValue().toString().equals("FECHADO") && !det.getStatus().equals("A ENVIAR") && !det.getStatus().equals("AGUARDANDO COLETA") && !det.getStatus().equals("ENVIADO")){
								tb.addItem(new Object[]{det.getId(),null,det.getProduto().getNome(),det.getSerial() != null ? det.getSerial().getSerial() : "", det.getNf_compra(),Real.formatDbToString(String.valueOf(det.getProduto().getValorCusto())),det.getDefeito().toUpperCase(), det.getVenc(), det.getStatus(), det.getData_mudanca_status()}, i);
								tb.setChildrenAllowed(i, false);						
								tb.setParent(i, parentId);
								i++;
							}
						}
						
						qtdRegistros++;							
					}
				
				
				
				
		}
		
			
		tb.setSizeFull();
				
//		if(tb.getContainerDataSource().size() == 11){
//			tb.select(1);
//		}
		
		
		if(tb.getContainerDataSource().rootItemIds().size() == 1){
			Collection<?> itemIds = tb.getContainerDataSource().getItemIds();
			for (Object object : itemIds) {
				System.out.println(object);
				tb.setCollapsed(object, false);
				break;
			}
			
		}
		
		if(qtdRegistros > 0){
			qtdRegistros--;
		}
		//if(lbRegistros != null){
		//	hlFloat.replaceComponent(lbRegistros,BuildLbRegistros(qtdRegistros, Real.formatDbToString(qtdRegistrosAReceber.toString()), Real.formatDbToString(qtdRegistrosPagos.toString())));
		//}
	}
	
	//public Label BuildLbRegistros(Integer qtd, String valorReceber, String valorPago){
		//lbRegistros = new Label(qtd.toString() + " Registros Encontrados | "+" À Receber: "+valorReceber+" | Pagos: "+valorPago);
		//return lbRegistros;
	//}
	
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setInputPrompt("Buscar...");
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			public void textChange(TextChangeEvent event) {
				refresh(event.getText());
			}
		});
		return tfBusca;
	}

	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
					
					final RmaNew rmaEditor = new RmaNew("Novo RMA", true, false, null);
					rmaEditor.addListerner(new RmaNew.RmaNewListerner() {
						
						@Override
						public void onClose(RmaNewEvent event) {
							
							if(event.isConfirm()){
								List<RmaDetalhe> itens = event.getItens();
																
								RmaMestre rmaM = event.getMestre();
								RmaDAO.saveMestre(rmaM);
								
								for (RmaDetalhe item :itens) {
									item.setRma_mestre_id(rmaM.getId());
									RmaDAO.save(item);
									
									Cliente cliente = ClienteDAO.findByCpf(OpusERP4UI.getEmpresa().getCnpj());			
									EcfPreVendaCabecalho pedido = new EcfPreVendaCabecalho();
									pedido.setCliente(cliente);
									pedido.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
									pedido.setData(new Date());
									pedido.setTipo("RMA");										
									pedido.setSituacao("F");
									pedido.setRma_id(item.getId());
									
									
									NaturezaOperacao natureza = NaturezaOperacaoDAO.findbyName("CONSERTO");
									pedido.setNaturezaOperacao(natureza);
									
									
									EcfPreVendaDetalhe detalhe = new EcfPreVendaDetalhe();
									detalhe.setProdutoId(item.getProduto().getId());
									detalhe.setQuantidade(new Float(1));
									
									EntityManager em = ConnUtil.getEntity();
									em.getTransaction().begin();
									
									
									em.persist(pedido);
									detalhe.setEcfPreVendaCabecalhoId(pedido.getId());
									em.persist(detalhe);
									
									em.getTransaction().commit();
								}
																
								refresh(tfBusca.getValue());								
								rmaEditor.close();
							}
						}
					});

					getUI().addWindow(rmaEditor); 
					
				}else{
					Notify.Show("Você não Possui Permissão para Abrir um RMA",Notify.TYPE_ERROR);
				}
			}
		});

		return btNovo;
	}
	
	public Button BuildbtFechar(){
		btFechar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja realmente Excluir este RMA ?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						
						if(event.isConfirm()){
							try {		
								
								final Integer id_rma = (Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue();
								RmaDAO.fechar(id_rma);
								
								refresh(tfBusca.getValue());
								
								Notify.Show("RMA Fechado com Sucesso!", Notify.TYPE_SUCCESS);			
								
							} catch (Exception e) {
								e.printStackTrace();								
							}
						}
					}
				});
				
				getUI().addWindow(gd);
				
				
			}
		});
		
		return btFechar;
	}
	
	public Button BuildbtEditar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
					
			public void buttonClick(ClickEvent event) {
				
				if(tb.getItem(tb.getValue()).getItemProperty("Produto").getValue() == null){
						
						final Integer id_rma = (Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue();
					
						final RmaNew rma = new RmaNew("Editar RMA",true, true, id_rma);
						rma.addListerner(new RmaNew.RmaNewListerner() {
							
							@Override
							public void onClose(RmaNewEvent event) {
								//RmaDAO.removeItensRma(id_rma);
								List<RmaDetalhe> itens = event.getItens();
								
								if(itens != null){
									for (RmaDetalhe item : itens) {
										item.setRma_mestre_id(id_rma);
										RmaDAO.save(item);		
										
										Cliente cliente = ClienteDAO.findByCpf(OpusERP4UI.getEmpresa().getCnpj());			
										EcfPreVendaCabecalho pedido = new EcfPreVendaCabecalho();
										pedido.setCliente(cliente);
										pedido.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
										pedido.setData(new Date());
										pedido.setTipo("RMA");										
										pedido.setSituacao("F");
										
										NaturezaOperacao natureza = NaturezaOperacaoDAO.findbyName("CONSERTO");
										pedido.setNaturezaOperacao(natureza);
										
										
										EcfPreVendaDetalhe detalhe = new EcfPreVendaDetalhe();
										detalhe.setProdutoId(item.getProduto().getId());
										detalhe.setQuantidade(new Float(1));
										
										EntityManager em = ConnUtil.getEntity();
										em.getTransaction().begin();
										
										
										em.persist(pedido);
										detalhe.setEcfPreVendaCabecalhoId(pedido.getId());
										em.persist(detalhe);
										
										em.getTransaction().commit();
										
									}
								}
								
								refresh(tfBusca.getValue());
								
								rma.close();							
								
							}
						});
						
						getUI().addWindow(rma);
				
				}else{
					Notify.Show("Não é possivel editar um ITEM", Notify.TYPE_NOTICE);
					Notify.Show("Selecione um RMA para editar", Notify.TYPE_NOTICE);
				}
			}
		});

		btEditar.setEnabled(false); 
		return btEditar;
	}
		
	
	private Component BuildbtStatus() {
		btStatus = new Button("Status", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Log"))				
				{
						if(tb.getItem(tb.getValue()).getItemProperty("Produto").getValue() != null){
							if (winSubMenuCorrecoes != null && winSubMenuCorrecoes.getUI() != null)
								winSubMenuCorrecoes.close();
								
								BuildbtStatus(event);
								
								getUI().addWindow(winSubMenuCorrecoes);
								winSubMenuCorrecoes.focus();
								((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
									
									@Override
									public void layoutClick(LayoutClickEvent event) {
										winSubMenuCorrecoes.close();
										((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
									}
								});
						}else{						
							Notify.Show("Selecione um ITEM para editar", Notify.TYPE_NOTICE);
						}								
								
				}else{
					Notify.Show("Você não Possui Permissão para alterar status de RMA",Notify.TYPE_ERROR);
				}
				
			}
		});
		btStatus.setEnabled(false);
		return btStatus;
	}
	
	Window winSubMenuCorrecoes;
	private void BuildbtStatus(ClickEvent event) {
		winSubMenuCorrecoes = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
       
        winSubMenuCorrecoes.setContent(l);
        winSubMenuCorrecoes.setWidth("300px");
        winSubMenuCorrecoes.addStyleName("notifications");
        winSubMenuCorrecoes.setClosable(false);
        winSubMenuCorrecoes.setResizable(false);
        winSubMenuCorrecoes.setDraggable(false);
        winSubMenuCorrecoes.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuCorrecoes.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuCorrecoes.setCloseShortcut(KeyCode.ESCAPE, null);

        Button bt1 = new Button("A Enviar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
					Integer cod = (Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue();				
					boolean save = RmaDAO.mudarStatus(cod, event.getButton().getCaption().toUpperCase());
					
					if(save){
						refresh("");
						RmaDAO.registraLog(RmaDAO.find(cod), "MUDOU STATUS PARA: "+event.getButton().getCaption().toUpperCase());
					}
				
					winSubMenuCorrecoes.close();
			}	
		});        
        bt1.setPrimaryStyleName("btSubMenu");
        
        Button bt2 = new Button("Aguardando Coleta", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Integer cod = (Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue();				
				boolean save = RmaDAO.mudarStatus(cod, event.getButton().getCaption().toUpperCase());
				
				if(save){
					refresh("");
					RmaDAO.registraLog(RmaDAO.find(cod), "MUDOU STATUS PARA: "+event.getButton().getCaption().toUpperCase());
				}
			
				winSubMenuCorrecoes.close();
			}	
		});        
        bt2.setPrimaryStyleName("btSubMenu");
        
        Button bt3 = new Button("Enviado", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Integer cod = (Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue();				
				boolean save = RmaDAO.mudarStatus(cod, event.getButton().getCaption().toUpperCase());
				
				if(save){
					refresh("");
					RmaDAO.registraLog(RmaDAO.find(cod), "MUDOU STATUS PARA: "+event.getButton().getCaption().toUpperCase());
				}
			
				winSubMenuCorrecoes.close();
			}	
		});        
        bt3.setPrimaryStyleName("btSubMenu");
        
        Button bt4 = new Button("Creditado", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Integer cod = (Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue();				
				boolean save = RmaDAO.mudarStatus(cod, event.getButton().getCaption().toUpperCase());
				
				if(save){
					refresh("");
					RmaDAO.registraLog(RmaDAO.find(cod), "MUDOU STATUS PARA: "+event.getButton().getCaption().toUpperCase());
				}
			
				winSubMenuCorrecoes.close();
			}	
		});        
        bt4.setPrimaryStyleName("btSubMenu");
        
        Button bt5 = new Button("Fora de Garantia", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Integer cod = (Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue();				
				boolean save = RmaDAO.mudarStatus(cod, event.getButton().getCaption().toUpperCase());
				
				if(save){
					refresh("");
					RmaDAO.registraLog(RmaDAO.find(cod), "MUDOU STATUS PARA: "+event.getButton().getCaption().toUpperCase());
				}
			
				winSubMenuCorrecoes.close();
			}	
		});        
        bt5.setPrimaryStyleName("btSubMenu");
        
        l.addComponent(bt1);
        l.addComponent(bt2);
        l.addComponent(bt3);
        l.addComponent(bt4);
        l.addComponent(bt5);
        
    }
	
	public Button BuildbtLog() {
		btLog = new Button("Log", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Log"))				
				{									
						RmaDetalhe rma = RmaDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue());
						
						HistoricoAlteracoesRma historicoWindow = new HistoricoAlteracoesRma(true, true, rma);
						getUI().addWindow(historicoWindow);

				}else{
					Notify.Show("Você não Possui Permissão para visualizar log do RMA",Notify.TYPE_ERROR);
				}
			}
		});
		
		btLog.setEnabled(false);		
		return btLog;
		
	}
	
	public Button BuildbtImprimir(){
		btImprimir = new Button("Imprimir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Espelho"))				
				{
					try {
						// INSTANCIA UMA NOVA JANELA E ADICIONA SUAS PROPRIEDADES
						Window win = new Window("Protocolo de Roterização");
						win.setWidth("1000px");
						win.setHeight("800px");
						win.setResizable(true);
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
						
						RmaMestre rma = RmaDAO.findRMA((Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue());
						
						StreamResource resource;
						resource = new StreamResource(new AutorizacaoRecebimento(rma), "AUTORIZACAO DE RECEBIMENTO.pdf");
						resource.getStream();
						resource.setMIMEType("application/pdf");
						resource.setCacheTime(0);
						
						Embedded e = new Embedded();
						e.setSizeFull();
						e.setType(Embedded.TYPE_BROWSER);
						e.setSource(resource);
						
						win.setContent(e);
						getUI().addWindow(win);
						
						win.addCloseListener(new Window.CloseListener() {
							
							
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
				
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{				
					Notify.Show("Você não Possui Permissão para Excluir um RMA",Notify.TYPE_ERROR);				
				}
			}
		});
		
		btImprimir.setEnabled(false);
		return btImprimir;
	}
	
	public Button BuildbtExcluir(){
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{

					GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja realmente Excluir este RMA ?", true, true);
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							
							if(event.isConfirm()){
								try {		
									
									Integer cod = (Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue();
									boolean check = RmaDAO.remover(cod);
									
									if(check){
										refresh("");
										Notify.Show("RMA excluído com Sucesso!", Notify.TYPE_SUCCESS);
									}
									
									
								} catch (Exception e) {
									e.printStackTrace();
									Notify.Show("Não foi Possivel Cancelar o RMA!", Notify.TYPE_ERROR);
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
					
					
								
				}else{				
					Notify.Show("Você não Possui Permissão para Excluir um RMA",Notify.TYPE_ERROR);				
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

