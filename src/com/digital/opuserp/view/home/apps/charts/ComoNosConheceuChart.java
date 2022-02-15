package com.digital.opuserp.view.home.apps.charts;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class ComoNosConheceuChart extends CssLayout {

	
	private Chart chart;
    //private Button btConfigure;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;    
    
    public ComoNosConheceuChart() {
    	
    	addStyleName("layout-panel");
        setSizeFull();
                      
        buildLayout();
    }
    
    private void buildLayout(){
    	removeAllComponents();
    	
    	addComponent(buildBtrefresh());
        addComponent(buildBtMaximize());
        addComponent(buildBtClose());
        addComponent(buildChart(true));
    }
    
    private Chart buildChart(boolean label){
    	
    	    	
    	chart = new Chart();
    	//PlotOptionsPie pie = new PlotOptionsPie();
        //pie.setShadow(false);
      //  chart.getConfiguration().setPlotOptions(pie);

    	
    	
    	chart.setCaption("Gráfico Como nos Conheceu");
        
    	chart.getConfiguration().disableCredits();
    	chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.PIE);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(false);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().setExporting(true);
       // chart.getConfiguration().getTooltip().setValueDecimals(2);       
        chart.getConfiguration().getTooltip().setFormatter("this.y");
        chart.setLocale(new Locale("pt", "BR"));     
        
        
//        if(label){
//        	chart.setWidth("80%");
//        	chart.setHeight("80%");
//        }else{
        	chart.setWidth("100%");
        	chart.setHeight("100%");
      //  }

        
        DataSeries series = new DataSeries();

        EntityManager em = ConnUtil.getEntity();
        Query q = em.createNativeQuery("SELECT cnc.NOME AS DATA, COUNT( cnc.NOME ) AS QTD " +
        		"FROM como_nos_conheceu cnc, clientes " +
        		"WHERE clientes.COMO_NOS_CONHECEU_ID = cnc.id " +
        		"AND clientes.EMPRESA_ID = :codEmpresa " +
        		"GROUP BY cnc.NOME " +
        		"ORDER BY QTD DESC ");
        
        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        
        List<Object[]> result = q.getResultList();
       
        PlotOptionsPie outerSeriesOptions = new PlotOptionsPie();
        //outerSeriesOptions.setSize(318);
        outerSeriesOptions.setDataLabels(new Labels());
        outerSeriesOptions
                .getDataLabels()
                .setFormatter(
                        "this.y > 1 ? ''+this.point.name+' ('+ this.percentage.toFixed(2) +' %)' : null");   
        
        for(Object[] o:result)
        {             
        	series.add(new DataSeriesItem((String)o[0],(Number)o[1]));       	
        }        
        
        series.setPlotOptions(outerSeriesOptions);     
        chart.getConfiguration().setSeries(series);
        
        return chart;
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
            	   PrefeDashDAO.remove(ComoNosConheceuChart.class.toString());
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
                Window winMaximize = new Window("Gráfico Como nos Conheceu");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");
                winMaximize.setContent(buildChart(false));
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
	
}
