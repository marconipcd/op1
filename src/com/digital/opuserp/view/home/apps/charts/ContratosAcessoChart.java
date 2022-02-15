package com.digital.opuserp.view.home.apps.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.util.ConnUtil;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.FontWeight;
import com.vaadin.addon.charts.themes.VaadinTheme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
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

public class ContratosAcessoChart extends CssLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8646259086665094879L;

	private Chart chart;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;    
    private ComboBox cbResumo;
    private ComboBox cbTipo;
    
    public ContratosAcessoChart() {
    	
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
        	
        	private static final long serialVersionUID = 1L;
        	
        	{
        		setWidth("100%");
        		setHeight("25px"); 

        		addComponent(buildCbResumo(false));
        		addComponent(buildCbTipo());
        		setComponentAlignment(cbResumo, Alignment.TOP_LEFT);
				setComponentAlignment(cbTipo, Alignment.TOP_RIGHT);
        	}
        });
        
        addComponent(buildChart(true));
    }
    
    private ComboBox buildCbTipo(){
    	cbTipo = new ComboBox();
    	cbTipo.setNullSelectionAllowed(false);
    	cbTipo.setTextInputAllowed(false); 
    	cbTipo.addItem("COLUNA");
    	cbTipo.addItem("PIZZA");
    	cbTipo.select("PIZZA");    	
    	
    	cbTipo.addStyleName("margin-left-10");
    	cbTipo.setImmediate(true);
    	cbTipo.addValueChangeListener(new Property.ValueChangeListener() {
			
    		private static final long serialVersionUID = 1L;
    		
			@Override
			public void valueChange(ValueChangeEvent event) {
					
				if(cbTipo.getParent().getParent() instanceof CssLayout){
					((CssLayout) cbTipo.getParent().getParent()).replaceComponent(chart, buildChart(true));
				}
				
				if(cbTipo.getParent().getParent() instanceof VerticalLayout){
					((VerticalLayout) cbTipo.getParent().getParent()).replaceComponent(chart, buildChart(false));
					((VerticalLayout) cbTipo.getParent().getParent()).setExpandRatio(chart, 1f);
				}
			}
		});
    	
    	return cbTipo;
    }
    
    private ComboBox buildCbResumo(boolean setStyle){
    	cbResumo = new ComboBox(null);
    	cbResumo.setNullSelectionAllowed(false);
    	cbResumo.setTextInputAllowed(false);
    	cbResumo.addItem("STATUS");
    	cbResumo.addItem("BASE");    	
    	cbResumo.addItem("CONCENTRADOR");
    	cbResumo.addItem("MATERIAL");
    	cbResumo.addItem("BAIRRO");
    	cbResumo.addItem("CIDADE");
    	cbResumo.addItem("PLANO");
    	cbResumo.addItem("CONTRATO");
    	
    	cbResumo.select("STATUS");
    	
    	if(setStyle){
    		cbResumo.addStyleName("margin-left-10");
    	}
    	
    	
    	cbResumo.setImmediate(true);
    	cbResumo.addValueChangeListener(new Property.ValueChangeListener() {
    		
    		private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
					
				if(cbResumo.getParent().getParent() instanceof CssLayout){
					((CssLayout) cbResumo.getParent().getParent()).replaceComponent(chart, buildChart(true));
				}
				
				if(cbResumo.getParent().getParent() instanceof VerticalLayout){
					((VerticalLayout) cbResumo.getParent().getParent()).replaceComponent(chart, buildChart(false));
					((VerticalLayout) cbResumo.getParent().getParent()).setExpandRatio(chart, 1f);
				}
			}
		});
    	
    	return cbResumo;
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
    		
    		private static final long serialVersionUID = 1L;
    		
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
    	btClose.addStyleName("btClose");
    	btClose.setIcon(new ThemeResource("icons/icon_close.png"));
    	btClose.addStyleName("icon-only");
    	btClose.addStyleName("borderless");
    	btClose.setDescription("Fechar");
    	btClose.addStyleName("small");  
    	btClose.addClickListener(new ClickListener() {
    		
    		private static final long serialVersionUID = 1L;
    		
            @Override
            public void buttonClick(ClickEvent event) {
            	  PrefeDashDAO.remove(ContratosAcessoChart.class.toString());
                  ((GridLayout)getParent()).removeComponent(btClose.getParent());   
            }
        });
        
        return btClose;
    }
    
    private Button buildBtMaximize(){
    	btMaximize = new Button();
    	btMaximize.setWidth("16px");
    	btMaximize.addStyleName("btMaximize");
    	btMaximize.setIcon(new ThemeResource("icons/icon_maximize.png"));
    	btMaximize.addStyleName("icon-only");
    	btMaximize.addStyleName("borderless");
    	btMaximize.setDescription("Maximizar");
    	btMaximize.addStyleName("small");
    	btMaximize.addClickListener(new ClickListener() {
    		
    		private static final long serialVersionUID = 1L;
    		
            @Override
            public void buttonClick(ClickEvent event) {
                Window winMaximize = new Window("Gráfico Contratos de Acesso");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");
                winMaximize.setContent(new VerticalLayout(){
                	private static final long serialVersionUID = 1L;
                	
                	{
                		setSizeFull();
                		setMargin(true);
                		setSpacing(true);
                		
                		addComponent(new HorizontalLayout(){
                			private static final long serialVersionUID = 1L;
                			
                			{
                				setWidth("100%");
                				setHeight("30px");

                				addComponent(buildCbResumo(true));
                				addComponent(buildCbTipo());
                				setComponentAlignment(cbResumo, Alignment.TOP_LEFT);
                				setComponentAlignment(cbTipo, Alignment.TOP_RIGHT);
                				
                			}
                		});
                		
                		addComponent(buildChart(false));
                		setExpandRatio(chart, 1f);
                	}
                });
                winMaximize.setModal(true);
                winMaximize.center();
                
                winMaximize.addCloseListener(new Window.CloseListener() {
					
                	private static final long serialVersionUID = 1L;
                	
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
    		chart.setCaption("Gráfico Contratos de Acesso");
    		chart.getConfiguration().getLegend().setEnabled(false);
    	}
        chart.getConfiguration().setTitle("");
        
        if(cbTipo != null && cbTipo.getValue() != null && cbTipo.getValue().toString().equals("COLUNA")){
        	chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        }else{
        	chart.getConfiguration().getChart().setType(ChartType.PIE);
        }
        
        chart.getConfiguration().disableCredits();
        chart.getConfiguration().getxAxis().getLabels().setEnabled(false);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().setExporting(true);
        chart.getConfiguration().getTooltip().setFormatter("this.series.name +': '+ this.y");      
        chart.getConfiguration().getyAxis().setTitle("Quantidade");   
        chart.setLocale(new Locale("pt", "BR"));  
        chart.getConfiguration().getTooltip().setFormatter("function(){"
        		+ "var valor=this.y;"
        		+ "var valor_x=this.series.name;"            	
        		+ "return '<strong>'+valor_x+'</strong>'+': '+valor;"
        		+ ""
        		+ "}");
        
        chart.setWidth("100%");
        if(dataLabel && cbTipo.getValue().toString().equals("COLUNA")){
        	chart.setHeight("90%");
        }
        
        if(dataLabel && !cbTipo.getValue().toString().equals("COLUNA")){
        	chart.setHeight("80%");
        }
        
        if(!dataLabel){
        	chart.setHeight("100%");
        }
        
        EntityManager em = ConnUtil.getEntity();
        
        
        Query q = null;
        
        if(cbResumo != null && cbResumo.getValue() != null && cbResumo.getValue().toString().equals("STATUS")){
        	q = em.createNativeQuery("SELECT STATUS_2, COUNT( STATUS_2 ) AS QTD FROM  `acesso_cliente` WHERE EMPRESA_ID = :codEmpresa AND STATUS_2 !=  'ENCERRADO' GROUP BY STATUS_2");
        }else if(cbResumo != null && cbResumo.getValue() != null && cbResumo.getValue().toString().equals("PLANO")){
        	q = em.createNativeQuery("SELECT (SELECT NOME FROM planos_acesso WHERE ID =PLANOS_ACESSO_ID), COUNT( STATUS_2 ) AS QTD FROM  `acesso_cliente` WHERE EMPRESA_ID = :codEmpresa AND STATUS_2 !=  'ENCERRADO' GROUP BY PLANOS_ACESSO_ID");
        }else if(cbResumo != null && cbResumo.getValue() != null && cbResumo.getValue().toString().equals("BASE")){
        	q = em.createNativeQuery("SELECT (SELECT base.NOME FROM bases, base WHERE bases.ID =BASES_ID AND bases.BASE_ID = base.ID) AS BASE, COUNT( (SELECT base.NOME FROM bases, base WHERE bases.ID =BASES_ID AND bases.BASE_ID = base.ID)) AS QTD FROM  `acesso_cliente` as ac WHERE EMPRESA_ID = :codEmpresa AND EXISTS((SELECT base.NOME FROM bases, base WHERE bases.ID =BASES_ID AND bases.BASE_ID = base.ID)) AND STATUS_2 !=  'ENCERRADO' GROUP BY (SELECT base.NOME FROM bases, base WHERE bases.ID =BASES_ID AND bases.BASE_ID = base.ID)");
        }else if(cbResumo != null && cbResumo.getValue() != null && cbResumo.getValue().toString().equals("CONTRATO")){
        	q = em.createNativeQuery("SELECT (SELECT NOME FROM contratos_acesso WHERE ID =CONTRATOS_ACESSO_ID), COUNT( STATUS_2 ) AS QTD FROM  `acesso_cliente` WHERE EMPRESA_ID = :codEmpresa AND EXISTS(SELECT * FROM contratos_acesso WHERE ID =CONTRATOS_ACESSO_ID) AND STATUS_2 !=  'ENCERRADO' GROUP BY CONTRATOS_ACESSO_ID");
        }else if(cbResumo != null && cbResumo.getValue() != null && cbResumo.getValue().toString().equals("MATERIAL")){
        	q = em.createNativeQuery("SELECT (SELECT NOME FROM produto WHERE ID = MATERIAL_ACESSO_ID ), COUNT( STATUS_2 ) AS QTD FROM  `acesso_cliente` WHERE EMPRESA_ID =:codEmpresa AND EXISTS ( SELECT * FROM produto WHERE ID = MATERIAL_ACESSO_ID) AND STATUS_2 !=  'ENCERRADO' GROUP BY MATERIAL_ACESSO_ID LIMIT 0 , 30");
        }else if(cbResumo != null && cbResumo.getValue() != null && cbResumo.getValue().toString().equals("BAIRRO")){        	
        	q = em.createNativeQuery("SELECT (SELECT BAIRRO FROM enderecos_principais WHERE ID = ENDERECO_ID) AS BAIRROS, COUNT( (SELECT BAIRRO FROM enderecos_principais WHERE ID = ENDERECO_ID)) AS QTD FROM  acesso_cliente WHERE  EMPRESA_ID =:codEmpresa AND  STATUS_2 !=  'ENCERRADO' GROUP BY  (SELECT BAIRRO FROM enderecos_principais WHERE ID = ENDERECO_ID) ORDER by QTD DESC LIMIT 0 , 30");
        }else if(cbResumo != null && cbResumo.getValue() != null && cbResumo.getValue().toString().equals("CIDADE")){        	
        	q = em.createNativeQuery("SELECT (SELECT CIDADE FROM enderecos_principais WHERE ID = ENDERECO_ID) AS CIDADE, COUNT( (SELECT BAIRRO FROM enderecos_principais WHERE ID = ENDERECO_ID)) AS QTD FROM  acesso_cliente WHERE  EMPRESA_ID =:codEmpresa AND  STATUS_2 !=  'ENCERRADO' GROUP BY  (SELECT CIDADE FROM enderecos_principais WHERE ID = ENDERECO_ID) ORDER by QTD DESC LIMIT 0 , 30");
        }else if(cbResumo != null && cbResumo.getValue() != null && cbResumo.getValue().toString().equals("CONCENTRADOR")){        	
        	q = em.createNativeQuery("SELECT (SELECT NOME FROM bases WHERE ID =BASES_ID), COUNT( STATUS_2 ) AS QTD FROM  `acesso_cliente` WHERE EMPRESA_ID = :codEmpresa AND EXISTS(SELECT * FROM bases WHERE ID =BASES_ID) AND STATUS_2 !=  'ENCERRADO' GROUP BY BASES_ID");
        }
        
        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());

        if(q != null){
	        	                
	        List<Object[]> result = Lists.reverse(q.getResultList());
	        	        
	        List<Series> series1 = new ArrayList<Series>();
	        
	        Color[] colors = new VaadinTheme().getColors();
	        PlotOptionsColumn column = new PlotOptionsColumn();
	        column.setCursor(Cursor.POINTER);
	        column.setDataLabels(new Labels(true));
	        column.getDataLabels().setColor(colors[0]);
	        column.getDataLabels().getStyle().setFontWeight(FontWeight.BOLD);
	        column.getDataLabels().setFormatter("this.y");
	        
	        chart.getConfiguration().setPlotOptions(column);
	      
	        if(cbTipo != null && cbTipo.getValue() != null && cbTipo.getValue().toString().equals("COLUNA")){
	        	chart.getConfiguration().getTooltip().setFormatter("function(){"
	            		+ "var valor=this.y;"
	            		+ "var valor_x=this.series.name;"            	
	            		+ "return '<strong>'+valor_x+'</strong>'+': '+valor;"
	            		+ ""
	            		+ "}");
	        }else{
	        	chart.getConfiguration().getTooltip().setFormatter("function(){"
		        		+ "var valor=this.y;"
		        		+ "var valor_x=this.point.name;"            	
		        		+ "return '<strong>'+valor_x+'</strong>'+': '+valor;"
		        		+ ""
		        		+ "}");
	        }
	        
	        DataSeries dse = new DataSeries();
	        dse.setName("Qtd.:");
	       	        	        
	        List<DataSeriesItem> listDsi = new ArrayList<>();
	        for(Object[] o:result)
	        {	        	
		        if(cbTipo != null && cbTipo.getValue() != null && cbTipo.getValue().toString().equals("COLUNA")){
		        	ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);
		        	series1.add(serie);
		        }else{
		        	listDsi.add(new DataSeriesItem((String)o[0],(Number)o[1]));
		        }	        	        	
	        }        
	        
	        dse.setData(listDsi);
	        
	        if(cbTipo != null && cbTipo.getValue() != null && cbTipo.getValue().toString().equals("COLUNA")){
	        	chart.getConfiguration().setSeries(series1);
	        }else{
	        	
	        	PlotOptionsPie outerSeriesOptions = new PlotOptionsPie();
	            //outerSeriesOptions.setSize(318);
	            outerSeriesOptions.setDataLabels(new Labels());
	            outerSeriesOptions
	                    .getDataLabels()
	                    .setFormatter(
	                    		"this.y > 1 ? ''+this.point.name+' ('+ this.percentage.toFixed(2) +' %)' : null");
	            dse.setPlotOptions(outerSeriesOptions);     
	        	chart.getConfiguration().setSeries(dse);
	        }
        }
        
        return chart;
    }
}
