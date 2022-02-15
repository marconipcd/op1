package com.digital.opuserp.view.home.apps.charts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.GrupoServico;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class Top100ServicosChart extends CssLayout {

	private JPAContainer<GrupoServico> containerGrupoProd;
	
	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;    
    private ComboBox cbPeriodicidade;
    private ComboBox cbGrupoServicos;
    
    private VerticalLayout vLPri;
    
    private Button btChangeView;
    private Button btChangeViewSaleOnly;
    private Button btImprimir;

    private Table tbPLan;
    
    
    public Top100ServicosChart() {
    	
    	addStyleName("layout-panel");
        setSizeFull();
        
        buildLayout();
    }
    
    private void buildLayout(){
    	removeAllComponents();
    	
    	addComponent(buildBtrefresh());
        addComponent(buildBtMaximize());
        addComponent(buildBtClose());        
        addComponent(buildCbPeriodicidade(true));
        addComponent(buildCbGrupoProduto(false));
        addComponent(buildChart(true));
    }    
    
    private ComboBox buildCbGrupoProduto(boolean setStyle){
    	
    	containerGrupoProd = JPAContainerFactory.makeReadOnly(GrupoServico.class, ConnUtil.getEntity());
    	containerGrupoProd.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
    	containerGrupoProd.sort(new Object[]{"nome"}, new boolean[]{true});
    	
    	cbGrupoServicos = new ComboBox();
    	cbGrupoServicos.addItem("TODOS OS GRUPOS");
    	
    	Iterator<Object> it = containerGrupoProd.getItemIds().iterator();
    	while (it.hasNext()) {
			Object itemId = (Object) it.next();
			EntityItem<GrupoServico> entityItem = containerGrupoProd.getItem(itemId);
			cbGrupoServicos.addItem(entityItem.getEntity().getNome());			
		}
    	cbGrupoServicos.select("TODOS OS GRUPOS");
    	
    	cbGrupoServicos.setNullSelectionAllowed(false); 
    	cbGrupoServicos.setTextInputAllowed(false);    	
    	cbGrupoServicos.setImmediate(true);
    	
    	cbGrupoServicos.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbGrupoServicos.getParent() instanceof HorizontalLayout){
					
					if(btChangeView != null && btChangeView.getIcon().toString().equals("icons/plan_icon.png")){						
						vLPri.replaceComponent(chart, buildChart(false));
						vLPri.setExpandRatio(chart, 1f);
					}
					
					if(btChangeView != null && btChangeView.getIcon().toString().equals("icons/chart_icon.png")){
						vLPri.removeAllComponents();
						
						if(btChangeViewSaleOnly != null && btChangeViewSaleOnly.getIcon().toString().equals("icons/cart_icon.png")){						
							vLPri.addComponent(buildTbView(false));
						}
						if(btChangeViewSaleOnly != null && btChangeViewSaleOnly.getIcon().toString().equals("icons/cart_icon_vazio.png")){						
							vLPri.addComponent(buildTbView(true));
						}
						
				        vLPri.addComponent(buildQtdRegistros());		
				        vLPri.setExpandRatio(tbPLan, 1f);
					}					
				}
				
				if(cbGrupoServicos.getParent() instanceof CssLayout){
					((CssLayout)cbGrupoServicos.getParent()).replaceComponent(chart, buildChart(true));
				}
			}
		});
    	
    	return cbGrupoServicos;
    }
    
    private ComboBox buildCbPeriodicidade(boolean setStyle){
    	cbPeriodicidade = new ComboBox();
    	
    	if(setStyle){
    		cbPeriodicidade.addStyleName("margin-left-18");
    	}else{
    		cbPeriodicidade.addStyleName("margin-left-10");
    	}
    	cbPeriodicidade.setNullSelectionAllowed(false); 
    	cbPeriodicidade.setTextInputAllowed(false);
    	cbPeriodicidade.addItem("1 MES");
    	cbPeriodicidade.addItem("3 MESES");
    	cbPeriodicidade.addItem("1 ANO");
    	cbPeriodicidade.addItem("2 ANOS");
    	cbPeriodicidade.setImmediate(true);
    	cbPeriodicidade.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbPeriodicidade.getParent() instanceof HorizontalLayout){
					
					if(btChangeView != null && btChangeView.getIcon().toString().equals("icons/plan_icon.png")){						
						vLPri.replaceComponent(chart, buildChart(false));
						vLPri.setExpandRatio(chart, 1f);
					}
					
					if(btChangeView != null && btChangeView.getIcon().toString().equals("icons/chart_icon.png")){
						vLPri.removeAllComponents();
						
						if(btChangeViewSaleOnly != null && btChangeViewSaleOnly.getIcon().toString().equals("icons/cart_icon.png")){						
							vLPri.addComponent(buildTbView(false));
						}
						if(btChangeViewSaleOnly != null && btChangeViewSaleOnly.getIcon().toString().equals("icons/cart_icon_vazio.png")){						
							vLPri.addComponent(buildTbView(true));
						}
						
				        vLPri.addComponent(buildQtdRegistros());						
				        vLPri.setExpandRatio(tbPLan, 1f);
					}
				}
				
				if(cbPeriodicidade.getParent() instanceof CssLayout){
					((CssLayout)cbPeriodicidade.getParent()).replaceComponent(chart, buildChart(true));
				}
			}
		});
    	
    	cbPeriodicidade.select("1 MES");
    	
    	return cbPeriodicidade;
    }
    
    private Button buildBtrefresh(){
    	btRefresh = new Button();
    	btRefresh.setWidth("16px");
    	btRefresh.addStyleName("btRefresh");
    	btRefresh.setIcon(new ThemeResource("icons/icon_refresh.png"));
    	btRefresh.addStyleName("icon-only");
    	btRefresh.addStyleName("borderless");
    	btRefresh.setDescription("Atualizar");
    	btRefresh.addStyleName("small");
    	btRefresh.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                 ((CssLayout)btRefresh.getParent()).replaceComponent(chart, buildChart(true));
            }
        });
        
        return btRefresh;
    }
    
    private Button buildBtConfigure(){
    	btConfigure = new Button();
    	btConfigure.addStyleName("configure");
        btConfigure.addStyleName("icon-cog");
        btConfigure.addStyleName("icon-only");
        btConfigure.addStyleName("borderless");
        btConfigure.setDescription("Configurar");
        btConfigure.addStyleName("small");
        btConfigure.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Notify.Show("Função Ainda Não Disponível", Notify.TYPE_WARNING);
            }
        });
        
        return btConfigure;
    }
    
   
    private Button buildBtClose(){
    	btClose = new Button();
    	btClose.setWidth("16px");
//    	btClose.addStyleName("configure");
    	btClose.addStyleName("btClose");
    	btClose.setIcon(new ThemeResource("icons/icon_close.png"));
    	btClose.addStyleName("icon-only");
    	btClose.addStyleName("borderless");
    	btClose.setDescription("Fechar");
    	btClose.addStyleName("small");  
    	btClose.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	  PrefeDashDAO.remove(Top100ServicosChart.class.toString());
                  ((GridLayout)getParent()).removeComponent(btClose.getParent());   
            }
        });
        
        return btClose;
    }
    
    private Button buildBtMaximize(){
    	btMaximize = new Button();
    	btMaximize.setWidth("16px");
//    	btMaximize.addStyleName("configure");
    	btMaximize.addStyleName("btMaximize");
    	btMaximize.setIcon(new ThemeResource("icons/icon_maximize.png"));
    	btMaximize.addStyleName("icon-only");
    	btMaximize.addStyleName("borderless");
    	btMaximize.setDescription("Maximizar");
    	btMaximize.addStyleName("small");
    	btMaximize.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final Window winMaximize = new Window("Gráfico TOP Vendas Serviços");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");
                winMaximize.setContent(new VerticalLayout(){
                	{
                		setSizeFull();
                		setMargin(true); 
                		
                		vLPri = new VerticalLayout();
                		
                		addComponent(new HorizontalLayout(){
                			{
                				setWidth("100%");
                				
                				addComponent(buildCbPeriodicidade(false));
                        		addComponent(buildCbGrupoProduto(true));
                        		setComponentAlignment(cbPeriodicidade, Alignment.TOP_LEFT);
                        		setComponentAlignment(cbGrupoServicos, Alignment.TOP_LEFT);
                				
                				btChangeView = new Button(null, new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										
										if(event.getButton().getIcon().toString().equals("icons/plan_icon.png")){
											event.getButton().setIcon(new ThemeResource("icons/chart_icon.png"));
											btChangeViewSaleOnly.setEnabled(true);
											btImprimir.setEnabled(true);
											
											if(btChangeViewSaleOnly.getIcon().toString().equals("icons/cart_icon.png")){		
												winMaximize.setCaption("Lista Serviços NENHUMA Venda");
											}else{
												winMaximize.setCaption("Lista TOP Vendas Serviços");
											}
											
									        vLPri.removeAllComponents();
									        if(btChangeViewSaleOnly.getIcon().toString().equals("icons/cart_icon.png")){		
									        	vLPri.addComponent(buildTbView(false));
									        }else{
									        	vLPri.addComponent(buildTbView(true));
									        }
									        
									        vLPri.addComponent(buildQtdRegistros());
									        									        
									        vLPri.setExpandRatio(tbPLan, 1f);
										}else{
											event.getButton().setIcon(new ThemeResource("icons/plan_icon.png"));
											btChangeViewSaleOnly.setEnabled(false);
											btImprimir.setEnabled(false);
											winMaximize.setCaption("Gráfico TOP Vendas Serviços");
											vLPri.removeAllComponents();
											vLPri.addComponent(buildChart(false));
										}
									}
								});
                				btChangeView.addStyleName("icon-only");
                				btChangeView.addStyleName("borderless");
                				btChangeView.setIcon(new ThemeResource("icons/plan_icon.png"));
                				
                				
                				
                				btImprimir = new Button(null, new Button.ClickListener() {
                					
                					
                					public void buttonClick(ClickEvent event) {
                						
                									try {
                										// INSTANCIA UMA NOVA JANELA E ADICIONA SUAS PROPRIEDADES
                										Window win = new Window(winMaximize.getCaption());
                										win.setWidth("800px");
                										win.setHeight("600px");
                										//win.setResizable(false);
                										win.center();
                										win.setModal(true);
                										win.setStyleName("disable_scroolbar");
                										
                										
                										
                										StreamResource resource;
                										resource = new StreamResource(new ExportTopServicoChart(winMaximize.getCaption(), cbPeriodicidade.getValue().toString(), cbGrupoServicos.getValue().toString(), btChangeViewSaleOnly.getIcon().toString(),tbPLan.getSortContainerPropertyId(),tbPLan.isSortAscending()), "ToProdutos.pdf");
                										resource.getStream();
                										resource.setMIMEType("application/pdf");
                										resource.setCacheTime(0);
                										
                										Embedded e = new Embedded();
                										e.setSizeFull();
                										e.setType(Embedded.TYPE_BROWSER);
                										e.setSource(resource);
                										
                										win.setContent(e);
                										getUI().addWindow(win);
                								
                									} catch (Exception e1) {
                										// TODO Auto-generated catch block
                										e1.printStackTrace();
                									}
                							}						
                					
                				});
                				btImprimir.addStyleName("icon-only");
                				btImprimir.addStyleName("borderless");
                				btImprimir.setIcon(new ThemeResource("icons/print_icon.png"));
                				btImprimir.setEnabled(false);
                				
                				
                				btChangeViewSaleOnly = new Button();
                				btChangeViewSaleOnly.setDescription("Lista Serviços NENHUMA Venda");
                				btChangeViewSaleOnly.addStyleName("icon-only");
                				btChangeViewSaleOnly.addStyleName("borderless");
                				btChangeViewSaleOnly.setIcon(new ThemeResource("icons/cart_icon_vazio.png"));
                				btChangeViewSaleOnly.setEnabled(false); 
                				btChangeViewSaleOnly.addClickListener(new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										if(event.getButton().getIcon().toString().equals("icons/cart_icon.png")){			
											event.getButton().setIcon(new ThemeResource("icons/cart_icon_vazio.png"));
											event.getButton().setDescription("Lista Serviços NENHUMA Venda");											
											
											winMaximize.setCaption("Lista TOP Vendas Serviços");
									        vLPri.removeAllComponents();
									        vLPri.addComponent(buildTbView(true));				
									        vLPri.addComponent(buildQtdRegistros());
									        vLPri.setExpandRatio(tbPLan, 1f);
										}else{
											event.getButton().setIcon(new ThemeResource("icons/cart_icon.png"));
											event.getButton().setDescription("Lista TOP Vendas Serviços");
											winMaximize.setCaption("Lista Serviços NENHUMA Venda");
											vLPri.removeAllComponents();
									        vLPri.addComponent(buildTbView(false));
									        vLPri.addComponent(buildQtdRegistros());
									        vLPri.setExpandRatio(tbPLan, 1f);
										}
									}
								});
                				
                				               				
                				addComponent(btChangeViewSaleOnly); 
                				setComponentAlignment(btChangeViewSaleOnly, Alignment.BOTTOM_RIGHT);
                				setExpandRatio(btChangeViewSaleOnly, 1f);
                				
                				addComponent(btChangeView);
                				setComponentAlignment(btChangeView, Alignment.BOTTOM_RIGHT);
                				
                				addComponent(btImprimir);               				
                				setComponentAlignment(btImprimir, Alignment.BOTTOM_RIGHT);
                				
                			}
                		});
                	
                		vLPri.setSizeFull();
                		vLPri.addComponent(buildChart(false));
                		addComponent(vLPri);
                		
                		setExpandRatio(vLPri, 1f);
                	}
                });
                winMaximize.setModal(true);
                winMaximize.center();
                
                winMaximize.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {						
						buildLayout();
					}
				});
                
                getUI().addWindow(winMaximize);                
            }
        });
        
        return btMaximize;
    }
    private Label buildQtdRegistros(){
    	
    	Integer qtd = 0;
    	if(tbPLan != null && tbPLan.getItemIds() != null){
    		qtd =  tbPLan.getItemIds().size();
    	}
    	return new Label("<span style='margin:10px;'>"+String.valueOf(qtd)+" Registros encontrados.</span>", ContentMode.HTML);
    }
    private Table buildTbView(boolean salesOnly){
    	
    	tbPLan = new Table();
    	tbPLan.setSelectable(true); 
    	
    	EntityManager em = ConnUtil.getEntity();
        
        Query q;
        
        if(cbGrupoServicos != null && cbGrupoServicos.getValue() != null && !cbGrupoServicos.getValue().toString().equals("TODOS OS GRUPOS")){
        	
        	if(salesOnly){
		        q = em.createNativeQuery("SELECT (SELECT ID FROM servicos WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
	        			+ "(SELECT NOME FROM servicos WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
	        			+ "(SELECT VALOR_VENDA FROM servicos WHERE ID=pvd.ID_PRODUTO) as VALOR, "
	        			+ "SUM(`QUANTIDADE`) as QTD, "
	        			+ "(SELECT (SELECT NOME FROM  grupo_servico WHERE grupo_servico.ID=servicos.grupo) FROM servicos WHERE servicos.ID=pvd.ID_PRODUTO) as GRUPO "
		        		+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc "
		        		+ "WHERE EXISTS(SELECT NOME FROM servicos WHERE ID=pvd.ID_PRODUTO AND grupo=:grupo) AND "
		        		+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM servicos WHERE ID = pvd.ID_PRODUTO) "
		        		+ "AND pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO = 'PEDIDO' AND pvc.TIPO_VENDA = 'SERVICO' "
		        		+ "AND pvc.SITUACAO = 'F' AND "
		        		+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
		        		+ "AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY QTD DESC ");
        	}else{
        		q = em.createNativeQuery("SELECT p.ID, p.NOME, p.VALOR_VENDA,"
        				+ "(SELECT NOME FROM  grupo_servico as gp WHERE gp.ID=p.grupo) as GRUPO "
        				+ "FROM servicos as p "
        				+ "WHERE "
        				+ "p.STATUS = 'ATIVO' AND "
        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' "
        				+ "AND ec.TIPO_VENDA = 'SERVICO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa))) "
        				+ "AND p.EMPRESA_ID = :codEmpresa AND p.grupo=:grupo ORDER by p.NOME ASC");        
        	}
	        
	        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
	        		        
	        Iterator<Object> it = containerGrupoProd.getItemIds().iterator();
	        while (it.hasNext()) {
				Object object = (Object) it.next();
				EntityItem<GrupoServico> entityItem = containerGrupoProd.getItem(object);
				if(entityItem.getEntity().getNome().equals(cbGrupoServicos.getValue().toString())){
					q.setParameter("grupo", entityItem.getEntity().getId());
					break;
				}
			}
	        
        }else{
        	
        	if(salesOnly){
	        	q = em.createNativeQuery("SELECT (SELECT ID FROM servicos WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
	        			+ "(SELECT NOME FROM servicos WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
	        			+ "(SELECT VALOR_VENDA FROM servicos WHERE ID=pvd.ID_PRODUTO) as VALOR, "
	        			+ "SUM(`QUANTIDADE`) as QTD, "
	        			+ "(SELECT (SELECT NOME FROM  grupo_servico WHERE grupo_servico.ID=servicos.grupo) FROM servicos WHERE servicos.ID=pvd.ID_PRODUTO) as GRUPO "        			
	        			+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE "
	        			+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM servicos WHERE ID = pvd.ID_PRODUTO) AND "
	        			+ "pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO_VENDA = 'SERVICO' AND pvc.TIPO = 'PEDIDO' AND "
	        			+ "pvc.SITUACAO = 'F' AND pvc.EMPRESA_ID = :codEmpresa AND "
	        			+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
	        			+ "GROUP BY ID_PRODUTO ORDER BY QTD DESC");
        	
        	}else{
        		q = em.createNativeQuery("SELECT p.ID, p.NOME, p.VALOR_VENDA,"
        				+ "(SELECT NOME FROM  grupo_servico as gp WHERE gp.ID=p.grupo) as GRUPO "
        				+ "FROM servicos as p "
        				+ "WHERE "
        				+ "p.STATUS = 'ATIVO' AND "
        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' "
        				+ "AND ec.TIPO_VENDA = 'SERVICO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa))) "
        				+ "AND p.EMPRESA_ID = :codEmpresa ORDER by p.NOME ASC");       		 
        	}
        	
        	q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        }
        
        if(salesOnly){
	        if(cbPeriodicidade.getValue().toString().equals("1 MES")){
	        	q.setParameter("data1",new DateTime().minusMonths(1).toDate());
	        	q.setParameter("data2", new DateTime().toDate());
	        }else if(cbPeriodicidade.getValue().toString().equals("3 MESES")){
	        	q.setParameter("data1",new DateTime().minusMonths(3).toDate());
	        	q.setParameter("data2", new DateTime().toDate());
	        }else if(cbPeriodicidade.getValue().toString().equals("1 ANO")){
	        	q.setParameter("data1",new DateTime().minusYears(1).toDate());
	        	q.setParameter("data2", new DateTime().toDate());
	        }else if(cbPeriodicidade.getValue().toString().equals("2 ANOS")){
	        	q.setParameter("data1",new DateTime().minusYears(2).toDate());
	        	q.setParameter("data2", new DateTime().toDate());
	        }
        }
        List<Object[]> result = q.getResultList();
        
        tbPLan.addStyleName("ajust-tb-plan");
        tbPLan.setWidth("100%");
        tbPLan.setHeight("100%");
        
        tbPLan.addContainerProperty("Ranking", Integer.class, null);
        tbPLan.addContainerProperty("Código", BigInteger.class, "");
        tbPLan.addContainerProperty("Produto", String.class, "");
        tbPLan.addContainerProperty("Grupo", String.class, "");
        tbPLan.addContainerProperty("Valor", String.class, "");
        
        if(salesOnly){
        	tbPLan.addContainerProperty("Vendidos", BigDecimal.class, null);                	
        	tbPLan.setColumnAlignment("Vendidos", Align.CENTER);        
        }
        
        if(salesOnly){
        	tbPLan.addContainerProperty("Última Venda", Date.class, "");
        	tbPLan.setColumnWidth("Última Venda", 77);
        }
        
        tbPLan.setColumnWidth("Código", 50);
        tbPLan.setColumnExpandRatio("Produto", 1f);        
        tbPLan.setColumnExpandRatio("Grupo", 0.3f);
        tbPLan.setColumnWidth("Vendidos", 65);        
        tbPLan.setColumnWidth("Valor", 80);
        tbPLan.setColumnAlignment("Valor", Align.RIGHT);
        tbPLan.setColumnAlignment("Ranking", Align.CENTER);
                
        Integer i=0;
        for (Object ob: result) {
			
        	try{
	        	if(salesOnly){
		        	Query qUltimaVenda = em.createNativeQuery("SELECT (SELECT DATA_PV FROM ecf_pre_venda_cabecalho "
		        			+ "WHERE ecf_pre_venda_detalhe.ID_ECF_PRE_VENDA_CABECALHO = ecf_pre_venda_cabecalho.ID) AS ULTIMA_VENDA "
		        			+ "FROM `ecf_pre_venda_detalhe` WHERE `ID_PRODUTO` = :codProduto ORDER BY ID DESC LIMIT 0,1");
		        	qUltimaVenda.setParameter("codProduto", ((Object[])ob)[0]);
		        	
		        	List<Object[]> resultUltimaVenda = qUltimaVenda.getResultList();
		        	SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");
		        	Date d = null;
        		
	        		Date dOri = sdf.parse(qUltimaVenda.getSingleResult().toString());
	        		
	        		SimpleDateFormat sdf2 =  new SimpleDateFormat("dd/MM/yyyy");
	        		Date dCerto = sdf2.parse(sdf2.format(dOri));
	        		 
	        		String valor = "R$ "+Real.formatDbToString(((Object[])ob)[2].toString());
	        		 tbPLan.addItem(new Object[]{i+1,((Object[])ob)[0],((Object[])ob)[1],((Object[])ob)[4],valor ,((Object[])ob)[3],dCerto}, i);
	        		 i++;
        		}else{
		        	
	        		String valor = "";
	        		if(((Object[])ob)[2] != null){
	        			valor = "R$ "+Real.formatDbToString(((Object[])ob)[2].toString());
	        		}
	        		
		        	tbPLan.addItem(new Object[]{i+1,new BigInteger(String.valueOf( ((Object[])ob)[0])) ,((Object[])ob)[1],((Object[])ob)[3],valor }, i);
		        	i++;
        		}
        		
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        	
        	
		}
        
        return tbPLan;
    }
    
    private Chart buildChart(boolean dataLabel){
    	chart = new Chart();
    	
    	if(dataLabel){
    		chart.setCaption("Gráfico TOP Vendas Serviços");
    		chart.getConfiguration().getLegend().setEnabled(false);
    	}
        
    	chart.getConfiguration().disableCredits();
    	chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.BAR);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(false);
        chart.getConfiguration().getxAxis().setTickWidth(10);
        chart.getConfiguration().setExporting(true);
        chart.getConfiguration().getTooltip().setFormatter("function(){"        		
        		+ "return '<strong>'+this.series.name+'</strong>:'+this.y ;"        		
        		+ "}");      
        chart.getConfiguration().getyAxis().setTitle("Quantidade");
        chart.getConfiguration().getxAxis().setTitle("Serviços");
        //chart.getConfiguration().getxAxis().set
        chart.getConfiguration().getLegend().setEnabled(false);
        //chart.getConfiguration().getTooltip().setValuePrefix("R$ ");
        chart.setLocale(new Locale("pt", "BR"));     
        
        chart.setWidth("100%");
        
        if(dataLabel){
        	chart.setHeight("90%");
        }else{
        	chart.setHeight("96%");
        }
        
        
        if(cbPeriodicidade.getValue() != null){
	        EntityManager em = ConnUtil.getEntity();
	        
	        Query q;
	        
	        if(cbGrupoServicos != null && cbGrupoServicos.getValue() != null && !cbGrupoServicos.getValue().toString().equals("TODOS OS GRUPOS")){
		        q = em.createNativeQuery("SELECT "
		        		+ "(SELECT NOME FROM servicos WHERE ID=pvd.ID_PRODUTO) as PRODUTO, "
		        		+ "SUM(`QUANTIDADE`) as QTD FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE "
		        		+ "EXISTS(SELECT NOME FROM servicos WHERE ID=pvd.ID_PRODUTO "
		        		+ "AND grupo=:grupo) "
		        		+ "AND pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID "
		        		+ "AND EXISTS(SELECT * FROM servicos WHERE ID = pvd.ID_PRODUTO) "
		        		+ "AND pvc.DATA_PV >= :data1 "
		        		+ "AND pvc.DATA_PV <= :data2 "
		        		+ "AND pvc.TIPO = 'PEDIDO' "
		        		+ "AND pvc.TIPO_VENDA = 'SERVICO' "
		        		+ "AND pvc.SITUACAO = 'F' "
		        		+ "AND pvc.NATUREZA_OPERACAO = (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa)  "
		        		+ "AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY SUM(`QUANTIDADE`) DESC LIMIT 0,30");
		        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
		        		        
		        Iterator<Object> it = containerGrupoProd.getItemIds().iterator();
		        while (it.hasNext()) {
					Object object = (Object) it.next();
					EntityItem<GrupoServico> entityItem = containerGrupoProd.getItem(object);
					if(entityItem.getEntity().getNome().equals(cbGrupoServicos.getValue().toString())){
						q.setParameter("grupo", entityItem.getEntity().getId());
						break;
					}
				}
	        }else{
	        	q = em.createNativeQuery("SELECT (SELECT NOME FROM servicos WHERE ID=pvd.ID_PRODUTO) as PRODUTO, "
	        			+ "SUM(`QUANTIDADE`) as QTD "
	        			+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc "
	        			+ "WHERE pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID "
	        			+ "AND EXISTS(SELECT * FROM servicos WHERE ID = pvd.ID_PRODUTO) "
	        			+ "AND pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 "
	        			+ "AND pvc.TIPO = 'PEDIDO' AND pvc.TIPO_VENDA = 'SERVICO' AND pvc.SITUACAO = 'F' "
	        			+ "AND pvc.NATUREZA_OPERACAO = (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' "
	        			+ "AND EMPRESA_ID =:codEmpresa)  "
	        			+ "AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY SUM(`QUANTIDADE`) DESC LIMIT 0,30");
		        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
	        }
	        
	        if(cbPeriodicidade.getValue().toString().equals("1 MES")){
	        	q.setParameter("data1",new DateTime().minusMonths(1).toDate());
	        	q.setParameter("data2", new DateTime().toDate());
	        }else if(cbPeriodicidade.getValue().toString().equals("3 MESES")){
	        	q.setParameter("data1",new DateTime().minusMonths(3).toDate());
	        	q.setParameter("data2", new DateTime().toDate());
	        }else if(cbPeriodicidade.getValue().toString().equals("1 ANO")){
	        	q.setParameter("data1",new DateTime().minusYears(1).toDate());
	        	q.setParameter("data2", new DateTime().toDate());
	        }else if(cbPeriodicidade.getValue().toString().equals("2 ANOS")){
	        	q.setParameter("data1",new DateTime().minusYears(2).toDate());
	        	q.setParameter("data2", new DateTime().toDate());
	        }
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
	    	String data = sdf.format(new Date());
	
	        List<Object[]> result = Lists.reverse(q.getResultList());
	        List<Series> series = new ArrayList<Series>();
	        
	        Labels dataLabels = new Labels();
		       
	        dataLabels.setEnabled(true);
	        //dataLabels.setRotation(-90);
	        dataLabels.setAlign(HorizontalAlign.CENTER);
	        dataLabels.setX(10);
	        //dataLabels.setY(-30);        
	        dataLabels.setFormatter("this.y");
	        Style style = new Style();
	        
	        style.setFontSize("7px");
	        style.setFontFamily("Verdana, sans-serif");
	        dataLabels.setStyle(style);
	        final PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
	        plotOptionsColumn.setDataLabels(dataLabels);
	        
	        for(Object[] o:result)
	        {
	        	ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);
	        	serie.setPlotOptions(plotOptionsColumn);	        	
	        	series.add(serie);        	
	        }        
	       
	        chart.getConfiguration().setSeries(series);
        }
        
        return chart;
    }
    
    
}
