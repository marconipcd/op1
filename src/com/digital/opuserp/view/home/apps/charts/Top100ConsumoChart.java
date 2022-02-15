package com.digital.opuserp.view.home.apps.charts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.util.ByteUtil;
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
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class Top100ConsumoChart extends CssLayout {
	
	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;    
  
    private VerticalLayout vLPri;    
    private Button btChangeView;
    private Button btChangeViewSaleOnly;
    private Button btImprimir;
   
    private Table tbPLan;
    
    public Top100ConsumoChart() {
    	
    	addStyleName("layout-panel");
        setSizeFull();
        
        buildLayout();
    }
    
   
    
    private void buildLayout(){
    	removeAllComponents();
    	
    	addComponent(buildBtrefresh());        
    	addComponent(buildBtMaximize());
        addComponent(buildBtClose());        
        addComponent(buildDfData(true));               
        addComponent(buildTbView(true));
    }
    
    DateField dfData;
    private DateField buildDfData(boolean setStyle){
    	dfData = new DateField();
    	dfData.setResolution(Resolution.MONTH);
    	dfData.setDateFormat("MMMM/y");
    	
    	if(setStyle){
    		dfData.addStyleName("margin-left-18");
    	}else{
    		dfData.addStyleName("margin-left-10");
    	}
    	
    	dfData.setValue(new Date());
    	
    	dfData.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
					replaceComponent(tbPLan, buildTbView(true)); 
			}
		});
    	    	
    	return dfData;
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
                 ((CssLayout)btRefresh.getParent()).replaceComponent(tbPLan, buildTbView(true));
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
                Notify.Show("Função Aindas Não Disponível", Notify.TYPE_WARNING);
            }
        });
        
        return btConfigure;
    }
    
   
    private Button buildBtClose(){
    	btClose = new Button();
    	btClose.setWidth("16px");
    	//btClose.addStyleName("configure");
    	btClose.addStyleName("btClose");
    	btClose.setIcon(new ThemeResource("icons/icon_close.png"));
    	btClose.addStyleName("icon-only");
    	btClose.addStyleName("borderless");
    	btClose.setDescription("Fechar");
    	btClose.addStyleName("small");  
    	btClose.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	  PrefeDashDAO.remove(Top100ConsumoChart.class.toString());
                  ((GridLayout)getParent()).removeComponent(btClose.getParent());   
            }
        });
        
        return btClose;
    }
    
    private Button buildBtMaximize(){
    	btMaximize = new Button();
    	btMaximize.setWidth("16px");
    	//btMaximize.addStyleName("configure");
    	btMaximize.addStyleName("btMaximize");
    	btMaximize.setIcon(new ThemeResource("icons/icon_maximize.png"));
    	btMaximize.addStyleName("icon-only");
    	btMaximize.addStyleName("borderless");
    	btMaximize.setDescription("Maximizar");
    	btMaximize.addStyleName("small");
    	btMaximize.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final Window winMaximize = new Window("Top Consumo");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");
                winMaximize.setContent(new VerticalLayout(){
                	{
                		setSizeFull();
                		setMargin(true); 
                		
                		vLPri = new VerticalLayout();                	
                		vLPri.setSizeFull();                		
                		vLPri.addComponent(buildTbView(true));
                	
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
    	tbPLan.setCaption("Top Consumo");
    	
    	EntityManager em = ConnUtil.getEntity();
        
        Query q;
             
        Date d;
        
        if(dfData != null && dfData.getValue() != null){
        	d= dfData.getValue();
        }else{
        	d = new Date();
        }
        
        DateTime d1 = new DateTime(d);
        
        Date d_inicio = d1.minusDays(d1.getDayOfMonth()).toDate();
        Date d_fim = new DateTime(d_inicio).plusMonths(1).toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                
        q = em.createNativeQuery("SELECT username, SUM(acctinputoctets) AS DOWNLOAD, SUM(acctoutputoctets) AS UPLOAD FROM "
        		+ "radacct r WHERE  r.acctstarttime > '"+sdf.format(d_inicio)+"' AND r.`acctstoptime` < '"+sdf.format(d_fim)+"'   GROUP BY username "
        		+ "ORDER BY UPLOAD DESC LIMIT 0,100");
	    
        List<Object[]> result = q.getResultList();
        
        
        tbPLan.addStyleName("ajust-tb-plan");
        tbPLan.setWidth("100%");
        tbPLan.setHeight("100%");
        
        tbPLan.addContainerProperty("Ranking", Integer.class,"");
        tbPLan.addContainerProperty("Contrato", String.class,"");
        tbPLan.addContainerProperty("Cliente", String.class,"");
        tbPLan.addContainerProperty("Plano", String.class,"");
        tbPLan.addContainerProperty("Download", String.class, "");
        tbPLan.addContainerProperty("Upload", String.class, "");
        
        tbPLan.setColumnWidth("Ranking", 30);
        tbPLan.setColumnWidth("Download",76);
        tbPLan.setColumnWidth("Upload", 76
        );
        
        Integer i=0;
        for (Object ob: result) {
			
        	try{	  
        		
        		Query q2 = em.createQuery("select a from AcessoCliente a where a.login =:login", AcessoCliente.class);
        		q2.setParameter("login", ((Object[])ob)[0]);
        		
        		AcessoCliente ac;        		
        		String nome = (String)((Object[])ob)[0];
        		String contrato = "";
        		String plano = "";
        		
        		if(q2.getResultList().size() == 1){
        			ac = (AcessoCliente)q2.getSingleResult();
        			nome = ac.getCliente().getNome_razao();
        			contrato = ac.getId().toString();
        			plano = ac.getPlano().getNome();
        		}
        		
        		tbPLan.addItem(new Object[]{i+1,contrato,nome,plano,ByteUtil.humanReadableByteCount((long)Double.parseDouble(String.valueOf(((Object[])ob)[2])), true),
        				ByteUtil.humanReadableByteCount((long)Double.parseDouble(String.valueOf(((Object[])ob)[1])), true)}, i);        		 
        		i++;       		
        	}catch(Exception e){
        		e.printStackTrace();
        	}        	
		}
        
        return tbPLan;
    }
    
    private Chart buildChart(boolean dataLabel){
    	chart = new Chart();
    	
    	if(dataLabel){
    		chart.setCaption("Gráfico TOP Consumo");
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
    
        
	        EntityManager em = ConnUtil.getEntity();
	        
	        Query q;
	      
        	q = em.createNativeQuery("SELECT username, SUM(acctinputoctets) AS DOWNLOAD, SUM(acctoutputoctets) AS UPLOAD FROM "
        		+ "radacct r WHERE  r.acctstarttime >= '2016-06-01' AND r.`acctstoptime` <= '2016-06-31'   GROUP BY username "
        		+ "ORDER BY UPLOAD DESC LIMIT 0,100");
	                
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
      
        
        return chart;
    }
    
    
    
    
}
