package com.digital.opuserp.view.home.apps.charts;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PrefeDashDAO;
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
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class Top100ClientesChart extends CssLayout {

	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;    
    private ComboBox cbTipo;
    private Table tbPLan;
    private VerticalLayout vLPri;
    private Button btChangeView;
        
    public Top100ClientesChart() {
    	
    	addStyleName("layout-panel");
        setSizeFull();        
        buildLayout();
    }
    
    private void buildLayout(){
    	removeAllComponents();
    	
    	addComponent(buildBtrefresh());
        addComponent(buildBtMaximize());
        addComponent(buildBtClose());        
        addComponent(buildCbTipo(true));
        addComponent(buildChart(true));
    }
        
    private ComboBox buildCbTipo(boolean setStyle){
    	cbTipo = new ComboBox();
    	
    	if(setStyle){
    		cbTipo.addStyleName("margin-left-18");
    	}else{
    		cbTipo.addStyleName("margin-left-10");
    	}
    	
    	cbTipo.setNullSelectionAllowed(false); 
    	cbTipo.setTextInputAllowed(false);
    	cbTipo.addItem("PEDIDO");
    	cbTipo.addItem("VALOR");
    	cbTipo.setImmediate(true);
    	cbTipo.select("VALOR");
    	cbTipo.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(cbTipo.getParent() instanceof  CssLayout){
						((CssLayout)cbTipo.getParent()).replaceComponent(chart, buildChart(true));
						
				}else{
				
					if(btChangeView != null && btChangeView.getIcon().toString().equals("icons/plan_icon.png")){						
						vLPri.removeAllComponents();
						vLPri.addComponent(buildChart(false));
						vLPri.setExpandRatio(chart, 1f);
					}
					
					if(btChangeView != null && btChangeView.getIcon().toString().equals("icons/chart_icon.png")){
						
						vLPri.removeAllComponents();
						vLPri.addComponent(buildTb());
						vLPri.addComponent(buildQtdRegistros());
				        vLPri.setExpandRatio(tbPLan, 1f);
					}
				}
			}
		});
    	    	
    	
    	
    	return cbTipo;
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
            	  PrefeDashDAO.remove(Top100ClientesChart.class.toString());
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
                final Window winMaximize = new Window("Gráfico TOP Clientes");
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
                				
                				addComponent(buildCbTipo(false));
                				
                				btChangeView = new Button(null, new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {							

										if(event.getButton().getIcon().toString().equals("icons/plan_icon.png")){
											event.getButton().setIcon(new ThemeResource("icons/chart_icon.png"));
											winMaximize.setCaption("Lista TOP Clientes");
											
									        vLPri.removeAllComponents();
									        vLPri.addComponent(buildTb());					
									        vLPri.addComponent(buildQtdRegistros());
									        vLPri.setExpandRatio(tbPLan, 1f);
										}else{
											event.getButton().setIcon(new ThemeResource("icons/plan_icon.png"));
											winMaximize.setCaption("Gráfico TOP Clientes");
											
											vLPri.removeAllComponents();
											vLPri.addComponent(buildChart(false));
										}
									}
								});
                				btChangeView.addStyleName("icon-only");
                				btChangeView.addStyleName("borderless");
                				btChangeView.setIcon(new ThemeResource("icons/plan_icon.png"));
                				
                				addComponent(btChangeView);
                				setComponentAlignment(btChangeView, Alignment.BOTTOM_RIGHT);
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
    
    private Chart buildChart(boolean dataLabel){
    	chart = new Chart();
    	
    	if(dataLabel){
    		chart.setCaption("Gráfico TOP Clientes");
    		chart.getConfiguration().getLegend().setEnabled(false);
    	}
    	
    	chart.getConfiguration().disableCredits();
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.BAR);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(false);
        chart.getConfiguration().getxAxis().setTickWidth(10);
        chart.getConfiguration().setExporting(true);
        chart.getConfiguration().getTooltip().setFormatter("this.series.name +': '+ this.y");      
        chart.getConfiguration().getyAxis().setTitle("Quantidade");
        chart.getConfiguration().getxAxis().setTitle("Clientes");
        //chart.getConfiguration().getxAxis().set
        chart.getConfiguration().getLegend().setEnabled(false);
        //chart.getConfiguration().getTooltip().setValuePrefix("R$ ");
        chart.setLocale(new Locale("pt", "BR"));  
        if(cbTipo != null && cbTipo.getValue() != null && cbTipo.getValue().toString().equals("VALOR")){
        	chart.getConfiguration().getTooltip().setFormatter("function(){"
            		+ "var valor=this.y;"
            		+ "var valor_x=this.series.name;"
            		+ "var casas=2;"
            		+ "var separdor_decimal = ',';"
            		+ "var separador_milhar = '.';"
            		+ ""
            		+ "var valor_total = parseInt(valor * (Math.pow(10,casas)));"
            		+ "var inteiros =  parseInt(parseInt(valor * (Math.pow(10,casas))) / parseFloat(Math.pow(10,casas)));"
            		+ "var centavos = parseInt(parseInt(valor * (Math.pow(10,casas))) % parseFloat(Math.pow(10,casas)));"        		        		
            		+ ""
            		+ "if(centavos%10 == 0 && centavos+''.length<2 ){"
            		+ " centavos = centavos+'0';"
            		+ "}else if(centavos<10){"
            		+ "	centavos = '0'+centavos;"
            		+ "}"
            		+ ""        		
            		+ ""
            		+ "var milhares = parseInt(inteiros/1000);"
            		+ "inteiros = inteiros % 1000; "
            		+ ""
            		+ "var retorno = '';"
            		+ ""
            		+ "if(milhares>0){"
            		+ "retorno = milhares+''+separador_milhar+''+retorno;"
            		+ " if(inteiros == 0){"
            		+ " inteiros = '000';"
            		+ " } else if(inteiros < 10){"
            		+ "inteiros = '00'+inteiros; "
            		+ "} else if(inteiros < 100){"
            		+ "inteiros = '0'+inteiros; "
            		+ "}"
            		+ "}"
            		+ ""
            		+ "retorno += inteiros+''+separdor_decimal+''+centavos;"
            		+ "return '<strong>'+valor_x+'</strong>'+': R$ '+retorno;"
            		+ ""
            		+ "}");
            
            chart.getConfiguration().getyAxis().setTitle("Real (R$)");
        }else{
        	chart.getConfiguration().getTooltip().setFormatter("function(){"
            		+ "var valor=this.y;"
            		+ "var valor_x=this.series.name;"            	
            		+ "return '<strong>'+valor_x+'</strong>'+': '+valor;"
            		+ ""
            		+ "}");
        }
        
        chart.setWidth("100%");
        
        if(dataLabel){
        	chart.setHeight("90%");
        }else{
        	chart.setHeight("96%");
        }
                
        if(cbTipo.getValue() != null){
	        EntityManager em = ConnUtil.getEntity();
	        
	        Query q;
	        
	        if(cbTipo != null && cbTipo.getValue() != null && cbTipo.getValue().toString().equals("PEDIDO")){
	        	q = em.createNativeQuery("SELECT (SELECT NOME_RAZAO FROM clientes WHERE ID=pvc.CLIENTES_ID) as NOME, COUNT(`CLIENTES_ID`) as QTD FROM ecf_pre_venda_cabecalho as pvc WHERE EXISTS(SELECT * FROM clientes WHERE ID = pvc.CLIENTES_ID AND NOME_RAZAO NOT LIKE '%CLIENTE NAO CADASTRADO%') AND pvc.SITUACAO = 'F' AND NATUREZA_OPERACAO = :codNatureza AND TIPO = 'PEDIDO' AND pvc.EMPRESA_ID = :codEmpresa GROUP BY CLIENTES_ID ORDER BY COUNT(`CLIENTES_ID`) DESC LIMIT 0,30");
	        }else{
	        	q = em.createNativeQuery("SELECT (SELECT NOME_RAZAO FROM clientes WHERE ID=pvc.CLIENTES_ID) as NOME, SUM(`VALOR`) as QTD FROM ecf_pre_venda_cabecalho as pvc WHERE EXISTS(SELECT * FROM clientes WHERE ID = pvc.CLIENTES_ID AND NOME_RAZAO NOT LIKE '%CLIENTE NAO CADASTRADO%') AND pvc.SITUACAO = 'F' AND NATUREZA_OPERACAO = :codNatureza AND TIPO = 'PEDIDO' AND pvc.EMPRESA_ID =:codEmpresa GROUP BY CLIENTES_ID ORDER BY SUM(`VALOR`) DESC LIMIT 0,30");	        	
	        }
	        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
	        
	        if(OpusERP4UI.getEmpresa().getId().equals(1)){
	        	q.setParameter("codNatureza", 3);
	        }else{
	        	q.setParameter("codNatureza", 9);
	        }
	
	        List<Object[]> result = Lists.reverse(q.getResultList());
	        List<Series> series = new ArrayList<Series>();
	        
	        Labels dataLabels = new Labels();
		       
	        dataLabels.setEnabled(true);
	        //dataLabels.setRotation(-90);
	        dataLabels.setAlign(HorizontalAlign.CENTER);
	        if(cbTipo.getValue().toString().equals("PEDIDO")){
	        	dataLabels.setX(10);
	        }else{
	        	dataLabels.setX(30);
	        }
	        //dataLabels.setY(-30);
	        
	        if(cbTipo.getValue().toString().equals("PEDIDO")){
	        	dataLabels.setFormatter("this.y");
	        }else{
		        dataLabels.setFormatter("function(){"
	            		+ "var valor=this.y;"
	            		+ "var valor_x=this.series.name;"
	            		+ "var casas=2;"
	            		+ "var separdor_decimal = ',';"
	            		+ "var separador_milhar = '.';"
	            		+ ""
	            		+ "var valor_total = parseInt(valor * (Math.pow(10,casas)));"
	            		+ "var inteiros =  parseInt(parseInt(valor * (Math.pow(10,casas))) / parseFloat(Math.pow(10,casas)));"
	            		+ "var centavos = parseInt(parseInt(valor * (Math.pow(10,casas))) % parseFloat(Math.pow(10,casas)));"        		        		
	            		+ ""
	            		+ "if(centavos%10 == 0 && centavos+''.length<2 ){"
	            		+ " centavos = centavos+'0';"
	            		+ "}else if(centavos<10){"
	            		+ "	centavos = '0'+centavos;"
	            		+ "}"
	            		+ ""        		
	            		+ ""
	            		+ "var milhares = parseInt(inteiros/1000);"
	            		+ "inteiros = inteiros % 1000; "
	            		+ ""
	            		+ "var retorno = '';"
	            		+ ""
	            		+ "if(milhares>0){"
	            		+ "retorno = milhares+''+separador_milhar+''+retorno;"
	            		+ " if(inteiros == 0){"
	            		+ " inteiros = '000';"
	            		+ " } else if(inteiros < 10){"
	            		+ "inteiros = '00'+inteiros; "
	            		+ "} else if(inteiros < 100){"
	            		+ "inteiros = '0'+inteiros; "
	            		+ "}"
	            		+ "}"
	            		+ ""
	            		+ "retorno += inteiros+''+separdor_decimal+''+centavos;"
	            		+ "return 'R$ '+retorno;"
	            		+ ""
	            		+ "}");
	        
	        }
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
    private Table buildTb(){
    	tbPLan = new Table(){
			@Override
            protected String formatPropertyValue(Object rowId, Object colId,
                    Property property) {
                Object v = property.getValue();
                if (v instanceof Date) {
                	return new SimpleDateFormat("dd/MM/yyyy").format(v);
                }
                return super.formatPropertyValue(rowId, colId, property);
            }
		};
		tbPLan.addStyleName("ajust-tb-plan");
		tbPLan.setColumnReorderingAllowed(true); 
		
														
		EntityManager em = ConnUtil.getEntity();
		
		String order = "";
		if(cbTipo != null && cbTipo.getValue().equals("VALOR")){
			order="VALOR DESC";
		}else{
			order="QTD DESC";
		}
	        
	     Query q;									        
	        
	     q = em.createNativeQuery("SELECT  "
	        		+ "pvc.CLIENTES_ID as COD, "
	        		+ "(SELECT NOME_RAZAO FROM clientes WHERE ID=pvc.CLIENTES_ID) as NOME,"
	        		+ "SUM(VALOR) AS VALOR, "
	        		+ "COUNT(CLIENTES_ID) as QTD, "
	        		+ "(SELECT DATA_PV FROM `ecf_pre_venda_cabecalho` WHERE `CLIENTES_ID` = pvc.CLIENTES_ID AND SITUACAO = 'F' AND EMPRESA_ID = :codEmpresa  ORDER BY ID DESC LIMIT 0 , 1) AS ULTIMO_PEDIDO "
	        		+ "FROM ecf_pre_venda_cabecalho as pvc "
	        		+ "WHERE "
	        		+ "EXISTS(SELECT * FROM clientes WHERE ID = pvc.CLIENTES_ID AND NOME_RAZAO NOT LIKE '%CLIENTE NAO CADASTRADO%') "
	        		+ "AND pvc.SITUACAO = 'F' "
	        		+ "AND NATUREZA_OPERACAO =  (SELECT ID FROM natureza_operacao WHERE EMPRESA_ID = :codEmpresa AND DESCRICAO = 'VENDA') "
	        		+ "AND TIPO = 'PEDIDO' "
	        		+ "AND pvc.EMPRESA_ID = :codEmpresa "
	        		+ "GROUP BY CLIENTES_ID "
	        		+ "ORDER BY "+order
	        		+ " LIMIT 0,100");
	     
	     
	     	//q.setMaxResults(100);
	        
	        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
	        									
	        List<Object[]> result = q.getResultList();
	       	        
	        tbPLan.setWidth("100%");
	        tbPLan.setHeight("100%");
	        tbPLan.addContainerProperty("Ranking", Integer.class, "");
	        tbPLan.setColumnWidth("Ranking", 60);
	        tbPLan.setColumnAlignment("Ranking", Table.Align.CENTER);
	        tbPLan.addContainerProperty("Codigo", Integer.class, "");
	        tbPLan.setColumnWidth("Codigo", 60);
	        tbPLan.addContainerProperty("Cliente", String.class, "");
	        tbPLan.setColumnExpandRatio("Cliente", 0.6f);
	        tbPLan.addContainerProperty("Valor", String.class, "");
	        tbPLan.setColumnExpandRatio("Valor", 0.2f);
	        tbPLan.setColumnAlignment("Valor", Table.Align.RIGHT);
	        tbPLan.addContainerProperty("Pedidos", BigInteger.class, "");
	        tbPLan.setColumnAlignment("Pedidos", Table.Align.CENTER);
	        tbPLan.setColumnWidth("Código", 4);
	        tbPLan.addContainerProperty("Último Pedido", Date.class, null);
	        tbPLan.setColumnWidth("Último Pedido", 90);
	        	        
	        Integer i=0;
	        for (Object ob: result) {
				try{
		        	String valor = "R$ "+Real.formatDbToString(((Object[])ob)[2].toString());
		        	
		        	SimpleDateFormat sdf2 =  new SimpleDateFormat("dd/MM/yyyy");
	        		
		        	Date ultimCompra = sdf2.parse(sdf2.format(((Object[])ob)[4]).toString());
		        	tbPLan.addItem(new Object[]{i+1,((Object[])ob)[0],((Object[])ob)[1],valor,((Object[])ob)[3],ultimCompra}, i);
		        	i++;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
	        
	        return tbPLan;
    }
    private Label buildQtdRegistros(){
    	
    	Integer qtd = 0;
    	if(tbPLan != null && tbPLan.getItemIds() != null){
    		qtd =  tbPLan.getItemIds().size();
    	}
    	return new Label("<span style='margin:10px;'>"+String.valueOf(qtd)+" Registros encontrados.</span>", ContentMode.HTML);
    }
}
