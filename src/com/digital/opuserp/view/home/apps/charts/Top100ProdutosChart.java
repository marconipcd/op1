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
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.Marca;
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

public class Top100ProdutosChart extends CssLayout {

	private JPAContainer<GrupoProduto> containerGrupoProd;
	private JPAContainer<Marca> containerMarca;
	
	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;    
    private ComboBox cbPeriodicidade;
    private ComboBox cbGrupoProduto;
    private ComboBox cbMarca;
    
    private VerticalLayout vLPri;
    
    private Button btChangeView;
    private Button btChangeViewSaleOnly;
    private Button btImprimir;

    private Table tbPLan;
    
    
    public Top100ProdutosChart() {
    	
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
        addComponent(buildCbMarcas(false));
        
        addComponent(buildChart(true));
    }
    
    
    private ComboBox buildCbMarcas(boolean setStyle){
    	
    	containerMarca = JPAContainerFactory.makeReadOnly(Marca.class, ConnUtil.getEntity());
    	containerMarca.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
    	containerMarca.sort(new Object[]{"nome"}, new boolean[]{true});
    	
    	cbMarca = new ComboBox();
    	cbMarca.addItem("TODOS AS MARCAS");
    	
    	//cbGrupoProduto.setContainerDataSource(containerGrupoProd);
    	
    	Iterator<Object> it = containerMarca.getItemIds().iterator();
    	while (it.hasNext()) {
			Object itemId = (Object) it.next();
			EntityItem<Marca> entityItem = containerMarca.getItem(itemId);
			cbMarca.addItem(entityItem.getEntity().getNome());			
		}
    	cbMarca.select("TODOS AS MARCAS");
    	
    	cbMarca.setNullSelectionAllowed(false); 
    	cbMarca.setTextInputAllowed(false);
    	//cbGrupoProduto.setItemCaptionPropertyId("nome_grupo");
    	cbMarca.setImmediate(true);
    	
    	cbMarca.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbMarca.getParent() instanceof HorizontalLayout){
					
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
				
				if(cbMarca.getParent() instanceof CssLayout){
					((CssLayout)cbMarca.getParent()).replaceComponent(chart, buildChart(true));
				}
			}
		});
    	
    	return cbMarca;
    }
    
    private ComboBox buildCbGrupoProduto(boolean setStyle){
    	
    	containerGrupoProd = JPAContainerFactory.makeReadOnly(GrupoProduto.class, ConnUtil.getEntity());
    	containerGrupoProd.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
    	containerGrupoProd.sort(new Object[]{"nome_grupo"}, new boolean[]{true});
    	
    	cbGrupoProduto = new ComboBox();
    	cbGrupoProduto.addItem("TODOS OS GRUPOS");
    	
    	//cbGrupoProduto.setContainerDataSource(containerGrupoProd);
    	
    	Iterator<Object> it = containerGrupoProd.getItemIds().iterator();
    	while (it.hasNext()) {
			Object itemId = (Object) it.next();
			EntityItem<GrupoProduto> entityItem = containerGrupoProd.getItem(itemId);
			cbGrupoProduto.addItem(entityItem.getEntity().getNome_grupo());			
		}
    	cbGrupoProduto.select("TODOS OS GRUPOS");
    	
    	cbGrupoProduto.setNullSelectionAllowed(false); 
    	cbGrupoProduto.setTextInputAllowed(false);
    	//cbGrupoProduto.setItemCaptionPropertyId("nome_grupo");
    	cbGrupoProduto.setImmediate(true);
    	
    	cbGrupoProduto.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbGrupoProduto.getParent() instanceof HorizontalLayout){
					
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
				
				if(cbGrupoProduto.getParent() instanceof CssLayout){
					((CssLayout)cbGrupoProduto.getParent()).replaceComponent(chart, buildChart(true));
				}
			}
		});
    	
    	return cbGrupoProduto;
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
            	  PrefeDashDAO.remove(Top100ProdutosChart.class.toString());
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
                final Window winMaximize = new Window("Gráfico TOP Vendas Produtos");
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
                        		addComponent(buildCbMarcas(true));
                        		setComponentAlignment(cbPeriodicidade, Alignment.TOP_LEFT);
                        		setComponentAlignment(cbGrupoProduto, Alignment.TOP_LEFT);
                        		setComponentAlignment(cbMarca, Alignment.TOP_LEFT);
                				
                				btChangeView = new Button(null, new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										
										if(event.getButton().getIcon().toString().equals("icons/plan_icon.png")){
											event.getButton().setIcon(new ThemeResource("icons/chart_icon.png"));
											btChangeViewSaleOnly.setEnabled(true);
											btImprimir.setEnabled(true);
											
											if(btChangeViewSaleOnly.getIcon().toString().equals("icons/cart_icon.png")){		
												winMaximize.setCaption("Lista Produtos NENHUMA Venda");
											}else{
												winMaximize.setCaption("Lista TOP Vendas Produtos");
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
											winMaximize.setCaption("Gráfico TOP Vendas Produtos");
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
        										resource = new StreamResource(new ExportTopProdutosChart(winMaximize.getCaption(), cbPeriodicidade.getValue().toString(), cbGrupoProduto.getValue().toString(), cbMarca.getValue().toString(), btChangeViewSaleOnly.getIcon().toString(),tbPLan.getSortContainerPropertyId(),tbPLan.isSortAscending()), "ToProdutos.pdf");
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
                				btChangeViewSaleOnly.setDescription("Lista Produtos NENHUMA Venda");
                				btChangeViewSaleOnly.addStyleName("icon-only");
                				btChangeViewSaleOnly.addStyleName("borderless");
                				btChangeViewSaleOnly.setIcon(new ThemeResource("icons/cart_icon_vazio.png"));
                				btChangeViewSaleOnly.setEnabled(false); 
                				btChangeViewSaleOnly.addClickListener(new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										if(event.getButton().getIcon().toString().equals("icons/cart_icon.png")){			
											event.getButton().setIcon(new ThemeResource("icons/cart_icon_vazio.png"));
											event.getButton().setDescription("Lista Produtos NENHUMA Venda");											
											
											winMaximize.setCaption("Lista TOP Vendas Produtos");
									        vLPri.removeAllComponents();
									        vLPri.addComponent(buildTbView(true));				
									        vLPri.addComponent(buildQtdRegistros());
									        vLPri.setExpandRatio(tbPLan, 1f);
										}else{
											event.getButton().setIcon(new ThemeResource("icons/cart_icon.png"));
											event.getButton().setDescription("Lista TOP Vendas Produtos");
											winMaximize.setCaption("Lista Produtos NENHUMA Venda");
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
                		//vLPri.addComponent(buildQtdRegistros());
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
        
        if(cbGrupoProduto != null && cbGrupoProduto.getValue() != null && !cbGrupoProduto.getValue().toString().equals("TODOS OS GRUPOS") ||
        		cbMarca != null && cbMarca.getValue() != null && !cbMarca.getValue().toString().equals("TODOS AS MARCAS")){
        	
        	if(cbGrupoProduto != null && cbGrupoProduto.getValue() != null && !cbGrupoProduto.getValue().toString().equals("TODOS OS GRUPOS")){
        	
        	if(salesOnly){
		        q = em.createNativeQuery("SELECT (SELECT ID FROM produto WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
	        			+ "(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
	        			+ "(SELECT VALOR_VENDA FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR, "
	        			+ "SUM(`QUANTIDADE`) as QTD, (SELECT (SELECT NOME_GRUPO FROM  grupo_produto WHERE grupo_produto.ID=produto.GRUPO_ID) FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as GRUPO, "
	        			+ "(SELECT (SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=produto.ID_UNIDADE_PRODUTO) FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as UNIDADE, "
	        			+ "(SELECT QTD_ESTOQUE FROM produto WHERE ID=pvd.ID_PRODUTO) as QTD_ESTOQUE, "
	        			+ "(SELECT VALOR_CUSTO FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR_CUSTO "
		        		+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc "
		        		+ "WHERE EXISTS(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO AND GRUPO_ID=:grupo AND produto.TIPO_ITEM = 3) AND "
		        		+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM produto WHERE ID = pvd.ID_PRODUTO) "
		        		+ "AND pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO = 'PEDIDO' AND pvc.TIPO_VENDA = 'PRODUTO' "
		        		+ "AND pvc.SITUACAO = 'F' AND "
		        		+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
		        		+ "AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY QTD DESC ");
        	}else{
        		q = em.createNativeQuery("SELECT p.ID, p.NOME, p.VALOR_VENDA, p.QTD_ESTOQUE, "
        				+ "(SELECT NOME_GRUPO FROM  grupo_produto as gp WHERE gp.ID=p.GRUPO_ID) as GRUPO, "
        				+ "(SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=p.ID_UNIDADE_PRODUTO), "
        				+ "p.VALOR_CUSTO "        				
        				+ "FROM produto as p "
        				+ "WHERE "
        				+ "p.STATUS = 'ATIVO' AND "
        				+ "p.QTD_ESTOQUE > 0 AND "
        				+ "p.TIPO_ITEM = 3 AND "
        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' "
        				+ "AND ec.TIPO_VENDA = 'PRODUTO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa)))  "
        				+ "AND p.EMPRESA_ID = :codEmpresa AND p.GRUPO_ID=:grupo ORDER by p.NOME ASC");
        	}
	        
	        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
	        		        
	        Iterator<Object> it = containerGrupoProd.getItemIds().iterator();
	        while (it.hasNext()) {
				Object object = (Object) it.next();
				EntityItem<GrupoProduto> entityItem = containerGrupoProd.getItem(object);
				if(entityItem.getEntity().getNome_grupo().equals(cbGrupoProduto.getValue().toString())){
					q.setParameter("grupo", entityItem.getEntity().getId());
					break;
				}
			}
	        
        	}else{
        		if(cbMarca != null && cbMarca.getValue() != null && !cbMarca.getValue().toString().equals("TODOS AS MARCAS")){
        			if(salesOnly){
        		        q = em.createNativeQuery("SELECT (SELECT ID FROM produto WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
        	        			+ "(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
        	        			+ "(SELECT VALOR_VENDA FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR, "
        	        			+ "SUM(`QUANTIDADE`) as QTD, (SELECT (SELECT NOME_GRUPO FROM  grupo_produto WHERE grupo_produto.ID=produto.GRUPO_ID) FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as GRUPO, "
        	        			+ "(SELECT (SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=produto.ID_UNIDADE_PRODUTO) FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as UNIDADE, "
        	        			+ "(SELECT QTD_ESTOQUE FROM produto WHERE ID=pvd.ID_PRODUTO) as QTD_ESTOQUE, "
        	        			+ "(SELECT VALOR_CUSTO FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR_CUSTO "
        		        		+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc "
        		        		+ "WHERE EXISTS(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO AND MARCAS_ID=:marca AND produto.TIPO_ITEM = 3) AND "
        		        		+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM produto WHERE ID = pvd.ID_PRODUTO) "
        		        		+ "AND pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO = 'PEDIDO' AND pvc.TIPO_VENDA = 'PRODUTO' "
        		        		+ "AND pvc.SITUACAO = 'F' AND "
        		        		+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
        		        		+ "AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY QTD DESC ");
                	}else{
                		q = em.createNativeQuery("SELECT p.ID, p.NOME, p.VALOR_VENDA, p.QTD_ESTOQUE, "
                				+ "(SELECT NOME_GRUPO FROM  grupo_produto as gp WHERE gp.ID=p.GRUPO_ID) as GRUPO, "
                				+ "(SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=p.ID_UNIDADE_PRODUTO), "
                				+ "p.VALOR_CUSTO "
                				+ "FROM produto as p "
                				+ "WHERE "
                				+ "p.STATUS = 'ATIVO' AND "
                				+ "p.QTD_ESTOQUE > 0 AND "
                				+ "p.TIPO_ITEM = 3 AND "
                				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
                				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' "
                				+ "AND ec.TIPO_VENDA = 'PRODUTO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa)))  "
                				+ "AND p.EMPRESA_ID = :codEmpresa AND p.MARCAS_ID=:marca ORDER by p.NOME ASC");
                	}
        			
        			 q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
     		        
        		        Iterator<Object> it = containerMarca.getItemIds().iterator();
        		        while (it.hasNext()) {
        					Object object = (Object) it.next();
        					EntityItem<Marca> entityItem = containerMarca.getItem(object);
        					if(entityItem.getEntity().getNome().equals(cbMarca.getValue().toString())){
        						q.setParameter("marca", entityItem.getEntity().getId());
        						break;
        					}
        				}
        		}else{
        			if(salesOnly){
        	        	q = em.createNativeQuery("SELECT (SELECT ID FROM produto WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
        	        			+ "(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
        	        			+ "(SELECT VALOR_VENDA FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR, "
        	        			+ "SUM(`QUANTIDADE`) as QTD, (SELECT (SELECT NOME_GRUPO FROM  grupo_produto WHERE grupo_produto.ID=produto.GRUPO_ID) FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as GRUPO, "
        	        			+ "(SELECT (SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=produto.ID_UNIDADE_PRODUTO) FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as UNIDADE, "
        	        			+ "(SELECT QTD_ESTOQUE FROM produto WHERE ID=pvd.ID_PRODUTO) as QTD_ESTOQUE, "
        	        			+ "(SELECT VALOR_CUSTO FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR_CUSTO "
        	        			+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE "
        	        			+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM produto WHERE ID = pvd.ID_PRODUTO AND produto.TIPO_ITEM = 3) AND "
        	        			+ "pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO_VENDA = 'PRODUTO' AND pvc.TIPO = 'PEDIDO' AND "
        	        			+ "pvc.SITUACAO = 'F' AND pvc.EMPRESA_ID = :codEmpresa AND "
        	        			+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
        	        			+ "GROUP BY ID_PRODUTO ORDER BY QTD DESC");
        	        	
        	        	}else{
        	        		q = em.createNativeQuery("SELECT p.ID, p.NOME, p.VALOR_VENDA, p.QTD_ESTOQUE, "
        	        				+ "(SELECT NOME_GRUPO FROM  grupo_produto as gp WHERE gp.ID=p.GRUPO_ID) as GRUPO,"
        	        				+ "(SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=p.ID_UNIDADE_PRODUTO) as UNIDADE, "
        	        				+ "p.VALOR_CUSTO "
        	        				+ "FROM produto as p "
        	        				+ "WHERE "
        	        				+ "p.STATUS = 'ATIVO' AND "
        	        				+ "p.QTD_ESTOQUE > 0 AND "      
        	        				+ "p.TIPO_ITEM = 3 AND "
        	        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
        	        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' "
        	        				+ "AND ec.TIPO_VENDA = 'PRODUTO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa)))  "
        	        				+ "AND p.EMPRESA_ID = :codEmpresa ORDER by p.NOME ASC");
        	        		
        	        		 
        	        	}
        	        	q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        		}
        	}
	        
        }else{
        	
        	if(salesOnly){
        	q = em.createNativeQuery("SELECT (SELECT ID FROM produto WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
        			+ "(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
        			+ "(SELECT VALOR_VENDA FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR, "
        			+ "SUM(`QUANTIDADE`) as QTD, (SELECT (SELECT NOME_GRUPO FROM  grupo_produto WHERE grupo_produto.ID=produto.GRUPO_ID) FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as GRUPO, "
        			+ "(SELECT (SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=produto.ID_UNIDADE_PRODUTO) FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as UNIDADE, "
        			+ "(SELECT QTD_ESTOQUE FROM produto WHERE ID=pvd.ID_PRODUTO) as QTD_ESTOQUE, "
        			+ "(SELECT VALOR_CUSTO FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR_CUSTO "
        			+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE "
        			+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM produto WHERE ID = pvd.ID_PRODUTO AND produto.TIPO_ITEM = 3) AND "
        			+ "pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO_VENDA = 'PRODUTO' AND pvc.TIPO = 'PEDIDO' AND "
        			+ "pvc.SITUACAO = 'F' AND pvc.EMPRESA_ID = :codEmpresa AND "
        			+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
        			+ "GROUP BY ID_PRODUTO ORDER BY QTD DESC");
        	
        	}else{
        		q = em.createNativeQuery("SELECT p.ID, p.NOME, p.VALOR_VENDA, p.QTD_ESTOQUE, "
        				+ "(SELECT NOME_GRUPO FROM  grupo_produto as gp WHERE gp.ID=p.GRUPO_ID) as GRUPO,"
        				+ "(SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=p.ID_UNIDADE_PRODUTO) as UNIDADE, "
        				+ "p.VALOR_CUSTO "
        				+ "FROM produto as p "
        				+ "WHERE "
        				+ "p.STATUS = 'ATIVO' AND "
        				+ "p.QTD_ESTOQUE > 0 AND "        		
        				+ "p.TIPO_ITEM = 3 AND "
        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' "
        				+ "AND ec.TIPO_VENDA = 'PRODUTO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa)))  "
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
        
        tbPLan.addContainerProperty("Ranking", Integer.class,null);
        tbPLan.setColumnAlignment("Ranking", Table.Align.CENTER);
        
        tbPLan.addContainerProperty("Código", String.class, "");
        tbPLan.addContainerProperty("Produto", String.class, "");
        tbPLan.addContainerProperty("Grupo", String.class, "");
        tbPLan.addContainerProperty("Valor", String.class, "");
        tbPLan.addContainerProperty("Valor Custo", String.class, "");
        
        if(salesOnly){
        	tbPLan.addContainerProperty("Vendidos", String.class, null);
        	tbPLan.addContainerProperty("Saldo", String.class, null);
        	
        	tbPLan.setColumnAlignment("Vendidos", Align.CENTER);
        	tbPLan.setColumnAlignment("Saldo", Align.CENTER);
        }else{
        	tbPLan.addContainerProperty("Saldo", String.class, null);
        	tbPLan.setColumnAlignment("Saldo", Align.CENTER);
        }
        tbPLan.addContainerProperty("Unidade", String.class, "");
        
        if(salesOnly){
        	tbPLan.addContainerProperty("Última Venda", Date.class, null);
        	tbPLan.setColumnWidth("Última Venda", 77);
        }else{
        	tbPLan.addContainerProperty("Última Compra", Date.class, null);
        	tbPLan.setColumnWidth("Última Compra", 77);
        }
        
        
        
        tbPLan.setColumnWidth("Código", 50);
        tbPLan.setColumnExpandRatio("Produto", 1f);
        //tbPLan.setColumnWidth("Grupo",150);
        tbPLan.setColumnExpandRatio("Grupo", 0.3f);
        tbPLan.setColumnWidth("Vendidos", 65);
        tbPLan.setColumnWidth("Unidade", 25);
        tbPLan.setColumnWidth("Valor", 80);
        tbPLan.setColumnAlignment("Valor", Align.RIGHT);
        
        
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
	        		 tbPLan.addItem(new Object[]{i+1,String.valueOf(((Object[])ob)[0]),String.valueOf(((Object[])ob)[1]),String.valueOf(((Object[])ob)[4]),valor ,String.valueOf(((Object[])ob)[7]),String.valueOf(((Object[])ob)[3]),String.valueOf(((Object[])ob)[6]),String.valueOf(((Object[])ob)[5]),dCerto}, i);
	        		 i++;
        		}else{

		        	
	        		String valor = "R$ "+Real.formatDbToString(((Object[])ob)[2].toString());
	        		String qtd = "";
	        		if(((Object[])ob)[3] != null){
	        			qtd = ((Object[])ob)[3].toString();
	        		}
	        		
	        		
	        		Query qUltimaCompra = em.createNativeQuery("SELECT (SELECT DATA_HORA FROM movimento_ent_cabecalho WHERE movimento_ent_detalhe.MOVIMENTO_ENT_CABECALHO_ID = movimento_ent_cabecalho.ID) AS ULTIMA_VENDA FROM movimento_ent_detalhe WHERE `PRODUTO_ID` =:codProduto ORDER BY ID DESC LIMIT 0 , 1");
	        		qUltimaCompra.setParameter("codProduto", ((Object[])ob)[0]);
	        		
	        		Object ultimaCompra = null;
	        		if(qUltimaCompra.getResultList().size() == 1){
	        			ultimaCompra = qUltimaCompra.getSingleResult();
	        			
	        			
	        			if(ultimaCompra != null){
				        	SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");
				        	Date d = null;
		        		
			        		Date dOri = sdf.parse(ultimaCompra.toString());
			        		
			        		SimpleDateFormat sdf2 =  new SimpleDateFormat("dd/MM/yyyy");
			        		Date dCerto = sdf2.parse(sdf2.format(dOri));
			        		tbPLan.addItem(new Object[]{i+1,String.valueOf( ((Object[])ob)[0]) ,String.valueOf(((Object[])ob)[1]),String.valueOf(((Object[])ob)[4]),valor ,String.valueOf(((Object[])ob)[6]) ,String.valueOf(((Object[])ob)[3]) ,String.valueOf(((Object[])ob)[5]), dCerto}, i);
	        			}else{
	        				tbPLan.addItem(new Object[]{i+1,String.valueOf( ((Object[])ob)[0]) ,String.valueOf(((Object[])ob)[1]),String.valueOf(((Object[])ob)[4]),valor ,String.valueOf(((Object[])ob)[6]) ,String.valueOf(((Object[])ob)[3]) ,String.valueOf(((Object[])ob)[5]), null}, i);
	        			}
		        	}else{
		        		tbPLan.addItem(new Object[]{i+1,String.valueOf( ((Object[])ob)[0]) ,String.valueOf(((Object[])ob)[1]),String.valueOf(((Object[])ob)[4]),valor ,String.valueOf(((Object[])ob)[6]) ,String.valueOf(((Object[])ob)[3]) ,String.valueOf(((Object[])ob)[5]), null}, i);
		        	}
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
    		chart.setCaption("Gráfico TOP Vendas Produtos");
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
        chart.getConfiguration().getxAxis().setTitle("Produtos");
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
	        
	        if(cbGrupoProduto != null && cbGrupoProduto.getValue() != null && !cbGrupoProduto.getValue().toString().equals("TODOS OS GRUPOS") || 
	        		cbMarca != null && cbMarca.getValue() != null && !cbMarca.getValue().toString().equals("TODOS OS GRUPOS")){
		        
	        	if(cbGrupoProduto != null && cbGrupoProduto.getValue() != null && !cbGrupoProduto.getValue().toString().equals("TODOS OS GRUPOS")){
	        	
			        	q = em.createNativeQuery("SELECT "
				        		+ "(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO, "
				        		+ "SUM(`QUANTIDADE`) as QTD FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE "
				        		+ "EXISTS(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO "
				        		+ "AND GRUPO_ID=:grupo) "
				        		+ "AND pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID "
				        		+ "AND EXISTS(SELECT * FROM produto WHERE ID = pvd.ID_PRODUTO) "
				        		+ "AND pvc.DATA_PV >= :data1 "
				        		+ "AND pvc.DATA_PV <= :data2 "
				        		+ "AND pvc.TIPO = 'PEDIDO' "
				        		+ "AND pvc.TIPO_VENDA = 'PRODUTO' "
				        		+ "AND pvc.SITUACAO = 'F' "
				        		+ "AND pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa)  "
				        		+ "AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY SUM(`QUANTIDADE`) DESC LIMIT 0,30");
				        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
				        		        
				        Iterator<Object> it = containerGrupoProd.getItemIds().iterator();
				        while (it.hasNext()) {
							Object object = (Object) it.next();
							EntityItem<GrupoProduto> entityItem = containerGrupoProd.getItem(object);
							if(entityItem.getEntity().getNome_grupo().equals(cbGrupoProduto.getValue().toString())){
								q.setParameter("grupo", entityItem.getEntity().getId());
								break;
							}
						}
		        
	        	}else{
	        		if(cbMarca != null && cbMarca.getValue() != null && !cbMarca.getValue().toString().equals("TODOS AS MARCAS")){
			        		q = em.createNativeQuery("SELECT "
					        		+ "(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO, "
					        		+ "SUM(`QUANTIDADE`) as QTD FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE "
					        		+ "EXISTS(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO "
					        		+ "AND MARCAS_ID=:grupo) "
					        		+ "AND pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID "
					        		+ "AND EXISTS(SELECT * FROM produto WHERE ID = pvd.ID_PRODUTO) "
					        		+ "AND pvc.DATA_PV >= :data1 "
					        		+ "AND pvc.DATA_PV <= :data2 "
					        		+ "AND pvc.TIPO = 'PEDIDO' "
					        		+ "AND pvc.TIPO_VENDA = 'PRODUTO' "
					        		+ "AND pvc.SITUACAO = 'F' "
					        		+ "AND pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa)  "
					        		+ "AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY SUM(`QUANTIDADE`) DESC LIMIT 0,30");
					        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
					        		        
					        Iterator<Object> it = containerMarca.getItemIds().iterator();
					        while (it.hasNext()) {
								Object object = (Object) it.next();
								EntityItem<Marca> entityItem = containerMarca.getItem(object);
								if(entityItem.getEntity().getNome().equals(cbMarca.getValue().toString())){
									q.setParameter("grupo", entityItem.getEntity().getId());
									break;
								}
							}
	        		}else{
	        			q = em.createNativeQuery("SELECT (SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO, SUM(`QUANTIDADE`) as QTD FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM produto WHERE ID = pvd.ID_PRODUTO) AND pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO = 'PEDIDO' AND pvc.TIPO_VENDA = 'PRODUTO' AND pvc.SITUACAO = 'F' AND pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa)  AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY SUM(`QUANTIDADE`) DESC LIMIT 0,30");
	    		        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
	        		}
	        	}
	        }else{
	        	q = em.createNativeQuery("SELECT (SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO, SUM(`QUANTIDADE`) as QTD FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM produto WHERE ID = pvd.ID_PRODUTO) AND pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO = 'PEDIDO' AND pvc.TIPO_VENDA = 'PRODUTO' AND pvc.SITUACAO = 'F' AND pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa)  AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY SUM(`QUANTIDADE`) DESC LIMIT 0,30");
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
