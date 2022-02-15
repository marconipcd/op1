package com.digital.opuserp.view.home.apps.indicadores;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class IndicadoresApp extends CssLayout {
	
	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btRefresh;
    
    private Button btClose;
    private ComboBox cbIndicadores;
    
    public IndicadoresApp() {
    	
    	addStyleName("layout-panel");
        setSizeFull();
        
        buildLayout();
    }
    private void buildLayout(){
    	
    	 removeAllComponents();
    	
    	 addComponent(buildBtrefresh());
         addComponent(buildBtMaximize());
         addComponent(buildBtClose());
         
         addComponent(new HorizontalLayout(){
        	 {
        		 //setWidth("100%");

        		 //addComponent(buildCbSeletor(false));
        		 addComponent(buildCbIndicadores());
        	 }
         });
         addComponent(buildChartDeltaVendas(true)); 
    }
    

    private ComboBox buildCbIndicadores(){
    	cbIndicadores = new ComboBox(null);
    	
    	cbIndicadores.setNullSelectionAllowed(false);
    	cbIndicadores.setTextInputAllowed(false); 
    	//cbPeriodicidade.setRequired(true);
    	
    	cbIndicadores.addItem("DELTA VENDAS");
    	cbIndicadores.addItem("TICKET MÉDIO");
    	cbIndicadores.addItem("VENDA DE PRODUTOS E SERVIÇOS");
    	cbIndicadores.addItem("OS EXECUTADAS");
    	cbIndicadores.addItem("SATISFAÇÃO DO MÊS");
    	
    	cbIndicadores.select("DELTA VENDAS"); 
    	
    	cbIndicadores.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(cbIndicadores.getParent() instanceof VerticalLayout){
					
					if(cbIndicadores.getValue().toString().equals("DELTA VENDAS")){
						((VerticalLayout)cbIndicadores.getParent().getParent()).replaceComponent(chart, buildChartDeltaVendas(true));
						((VerticalLayout)cbIndicadores.getParent().getParent()).setExpandRatio(chart, 1f);	
					}
					
					if(cbIndicadores.getValue().toString().equals("TICKET MÉDIO")){
						((VerticalLayout)cbIndicadores.getParent().getParent()).replaceComponent(chart, buildChartTicketMedio(true));
						((VerticalLayout)cbIndicadores.getParent().getParent()).setExpandRatio(chart, 1f);	
					}
					
				}
				
				if(cbIndicadores.getParent() instanceof HorizontalLayout){
					
					if(cbIndicadores.getValue().toString().equals("DELTA VENDAS")){
						if(cbIndicadores.getParent().getParent() instanceof CssLayout){
							((CssLayout)cbIndicadores.getParent().getParent()).replaceComponent(chart, buildChartDeltaVendas(true));
						}
						
						if(cbIndicadores.getParent().getParent() instanceof VerticalLayout){
							((VerticalLayout)cbIndicadores.getParent().getParent()).replaceComponent(chart, buildChartDeltaVendas(false));
						}
					}
					
					
				}
			}
		});    	
    	return cbIndicadores;
    }
    
    private Button buildBtConfigure(){
    	btConfigure = new Button();
    	btConfigure.addStyleName("configure");
        btConfigure.addStyleName("icon-cog");
        btConfigure.addStyleName("icon-only");
        btConfigure.addStyleName("borderless");
        btConfigure.addStyleName("small");
        btConfigure.setDescription("Configurar");
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
            	  PrefeDashDAO.remove(IndicadoresApp.class.toString());
                  ((GridLayout)getParent()).removeComponent(btClose.getParent());   
            }
        });
        
        return btClose;
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
                 ((CssLayout)btRefresh.getParent()).replaceComponent(chart, buildChartDeltaVendas(true));
            }
        });
        
        return btRefresh;
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
                Window winMaximize = new Window("Indicadores");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");
                winMaximize.setContent(new VerticalLayout(){
                	{
                		setSizeFull();
                		setMargin(true); 
                		setSpacing(true);
                		addComponent(new HorizontalLayout(){
                       	 {                       		 
                       		 //addComponent(buildCbSeletor(true));
                       		 addComponent(buildCbIndicadores());
                       	 }
                        });
                		addComponent(buildChartDeltaVendas(false));
                		setExpandRatio(chart, 1f);
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

    private Chart buildChartTicketMedio(boolean dataLabel){
    	chart = new Chart();
    	chart.setLocale(new Locale("pt", "BR"));
    	chart.getConfiguration().setExporting(true);
    	if(dataLabel){
    		chart.setCaption("Indicadores");    	
    		chart.getConfiguration().getLegend().setEnabled(false);
    	}
    	 
    	chart.getConfiguration().disableCredits();
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);       
        chart.getConfiguration().getxAxis().setMin(0);        
        chart.getConfiguration().getxAxis().setAllowDecimals(true);
        chart.getConfiguration().getxAxis().setTitle("Últimos 12 meses");
        chart.getConfiguration().getyAxis().setTitle("valor");
        
        chart.setLocale(new Locale("pt", "BR"));     
        
        chart.setWidth("100%");

        if(!dataLabel){
        	chart.setHeight("97%");
        }else{
        	chart.setHeight("90%");
        }

        EntityManager em = ConnUtil.getEntity();
        
        Query q_instalacao = em.createNativeQuery("SELECT DATE_FORMAT( DATA_CONCLUSAO, '%M/%Y' ) AS DATA, COUNT( ID ) AS QTD FROM "
        		+ "ose WHERE STATUS_2 NOT LIKE 'ABERTO' AND GRUPO_ID =2 AND EMPRESA_ID =1 GROUP BY "
        		+ "DATE_FORMAT( DATA_CONCLUSAO, '%m/%Y' ) ORDER BY `DATA_CONCLUSAO` DESC LIMIT 0 , 13");
        
        Query q_cancelamento = em.createNativeQuery("SELECT DATE_FORMAT( DATA_CONCLUSAO, '%m/%Y' ) AS DATA, COUNT( ID ) AS QTD FROM "
        		+ "ose WHERE STATUS_2 NOT LIKE 'ABERTO' AND GRUPO_ID =4 AND EMPRESA_ID =1 GROUP BY "
        		+ "DATE_FORMAT( DATA_CONCLUSAO, '%m/%Y' ) ORDER BY `DATA_CONCLUSAO` DESC LIMIT 0 , 13");
              	        
	        if(q_instalacao != null && q_cancelamento != null){
		        	 
		        List<Object[]> r_instalacao = Lists.reverse(q_instalacao.getResultList());
		        List<Object[]> r_cancelamento = Lists.reverse(q_cancelamento.getResultList());
		        
		        List<Series> series = new ArrayList<Series>();
		        
		        Labels dataLabels = new Labels();		   
		        
		        dataLabels.setEnabled(true);
		        dataLabels.setRotation(-90);
		        dataLabels.setColor(new SolidColor(255, 255, 255));
		        dataLabels.setAlign(HorizontalAlign.CENTER);
		        dataLabels.setX(5);
		        dataLabels.setY(80); 
		        dataLabels.setFormatter(
		        "function(){"
		        	+ "var valor_y=this.y;"
		        	+ "var valor_x=this.series.name;"
		     
		        	+ "return valor_x+' - '+valor_y"        		
		        	
	        	+ "}");
		      	
		        Style style = new Style();		        
		        style.setFontSize("10px");
		        style.setFontFamily("Verdana, sans-serif");
		        dataLabels.setStyle(style);
		        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
		        plotOptionsColumn.setDataLabels(dataLabels);
		   		
		        for(Integer i = 0;i < 13; i++)
		        {		        			        	
		        	Integer delta_vendas = Integer.parseInt(r_instalacao.get(i)[1].toString()) - Integer.parseInt(r_cancelamento.get(i)[1].toString());		        	
		        	ListSeries serie = new ListSeries((String)r_instalacao.get(i)[0],delta_vendas);  	
	        		serie.setPlotOptions(plotOptionsColumn);
		        	series.add(serie);	        	
		        }    
		           
		        chart.getConfiguration().setSeries(series);	        
	        }
     
        
        return chart;
    }
    
    private Chart buildChartDeltaVendas(boolean dataLabel){
    	chart = new Chart();
    	chart.setLocale(new Locale("pt", "BR"));
    	chart.getConfiguration().setExporting(true);
    	if(dataLabel){
    		chart.setCaption("Indicadores");    	
    		chart.getConfiguration().getLegend().setEnabled(false);
    	}
    	 
    	chart.getConfiguration().disableCredits();
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);       
        chart.getConfiguration().getxAxis().setMin(0);        
        chart.getConfiguration().getxAxis().setAllowDecimals(true);
        chart.getConfiguration().getxAxis().setTitle("Últimos 12 meses");
        chart.getConfiguration().getyAxis().setTitle("valor");
        
        chart.setLocale(new Locale("pt", "BR"));     
        
        chart.setWidth("100%");

        if(!dataLabel){
        	chart.setHeight("97%");
        }else{
        	chart.setHeight("90%");
        }

        EntityManager em = ConnUtil.getEntity();
        
        Query q_instalacao = em.createNativeQuery("SELECT DATE_FORMAT( DATA_CONCLUSAO, '%M/%Y' ) AS DATA, COUNT( ID ) AS QTD FROM "
        		+ "ose WHERE STATUS_2 NOT LIKE 'ABERTO' AND GRUPO_ID =2 AND EMPRESA_ID =1 GROUP BY "
        		+ "DATE_FORMAT( DATA_CONCLUSAO, '%m/%Y' ) ORDER BY `DATA_CONCLUSAO` DESC LIMIT 0 , 13");
        
        Query q_cancelamento = em.createNativeQuery("SELECT DATE_FORMAT( DATA_CONCLUSAO, '%m/%Y' ) AS DATA, COUNT( ID ) AS QTD FROM "
        		+ "ose WHERE STATUS_2 NOT LIKE 'ABERTO' AND GRUPO_ID =4 AND EMPRESA_ID =1 GROUP BY "
        		+ "DATE_FORMAT( DATA_CONCLUSAO, '%m/%Y' ) ORDER BY `DATA_CONCLUSAO` DESC LIMIT 0 , 13");
              	        
	        if(q_instalacao != null && q_cancelamento != null){
		        	 
		        List<Object[]> r_instalacao = Lists.reverse(q_instalacao.getResultList());
		        List<Object[]> r_cancelamento = Lists.reverse(q_cancelamento.getResultList());
		        
		        List<Series> series = new ArrayList<Series>();
		        
		        Labels dataLabels = new Labels();		   
		        
		        dataLabels.setEnabled(true);
		        dataLabels.setRotation(-90);
		        dataLabels.setColor(new SolidColor(255, 255, 255));
		        dataLabels.setAlign(HorizontalAlign.CENTER);
		        dataLabels.setX(5);
		        dataLabels.setY(80); 
		        dataLabels.setFormatter(
		        "function(){"
		        	+ "var valor_y=this.y;"
		        	+ "var valor_x=this.series.name;"
		     
		        	+ "return valor_x+' - '+valor_y"        		
		        	
	        	+ "}");
		      	
		        Style style = new Style();		        
		        style.setFontSize("10px");
		        style.setFontFamily("Verdana, sans-serif");
		        dataLabels.setStyle(style);
		        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
		        plotOptionsColumn.setDataLabels(dataLabels);
		   		
		        for(Integer i = 0;i < 13; i++)
		        {		        			        	
		        	Integer delta_vendas = Integer.parseInt(r_instalacao.get(i)[1].toString()) - Integer.parseInt(r_cancelamento.get(i)[1].toString());		        	
		        	ListSeries serie = new ListSeries((String)r_instalacao.get(i)[0],delta_vendas);  	
	        		serie.setPlotOptions(plotOptionsColumn);
		        	series.add(serie);	        	
		        }    
		           
		        chart.getConfiguration().setSeries(series);	        
	        }
     
        
        return chart;
    }
    
    
}


