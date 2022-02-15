package com.digital.opuserp.view.home.apps.charts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.GrupoOse;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.ChartClickEvent;
import com.vaadin.addon.charts.ChartClickListener;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.FontWeight;
import com.vaadin.addon.charts.themes.VaadinTheme;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class RoteirizacaoChart extends CssLayout {

	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;    
    private ComboBox cbPeriodicidade;
    private ComboBox cbTipoData;
    
    public RoteirizacaoChart() {
    	
    	addStyleName("layout-panel");
        setSizeFull();
        
        buildLayout();
    }
    private void buildLayout(){
    	removeAllComponents();
    	
    	addComponent(buildBtrefresh());
        addComponent(buildBtMaximize());
        addComponent(buildCbPrioridade(true));
        addComponent(buildCbTipoData(true));
        addComponent(buildCbTipos(true));
//        addComponent(new HorizontalLayout(){
//			{
//				setWidth("100%");
//				addComponent(buildCbPrioridade(true));
//				addComponent(buildCbTipoData(true));
//			}
//		});
        
        
        addComponent(buildBtClose());
        addComponent(buildChart(true));        
    }
    
    private ComboBox buildCbPrioridade(boolean setStyle){
    	cbPeriodicidade = new ComboBox();
    	cbPeriodicidade.setNullSelectionAllowed(false);
    	cbPeriodicidade.setTextInputAllowed(false);
    	cbPeriodicidade.addItem("HOJE");
    	cbPeriodicidade.addItem("ONTEM");
    	cbPeriodicidade.addItem("1 MES");
    	cbPeriodicidade.addItem("3 MESES");
    	cbPeriodicidade.addItem("ANO ATUAL");
    	cbPeriodicidade.addItem("ANO ANTERIOR");
    	cbPeriodicidade.addItem("DOIS ANOS ATRAS");
    	cbPeriodicidade.addItem("3 ANOS AGRUPADO");
    	cbPeriodicidade.select("1 MES");
    	cbPeriodicidade.setImmediate(true); 
    	
    	if(setStyle){
    		cbPeriodicidade.addStyleName("margin-left-18");
    	}else{
    		cbPeriodicidade.addStyleName("margin-left-10");
    	}
    	
    	cbPeriodicidade.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbPeriodicidade.getValue() != null){
					if(cbPeriodicidade.getParent() instanceof CssLayout){
						((CssLayout)cbPeriodicidade.getParent()).replaceComponent(chart, buildChart(true));
					}
					
					if(cbPeriodicidade.getParent() instanceof VerticalLayout){
						((VerticalLayout)cbPeriodicidade.getParent()).replaceComponent(chart, buildChart(false));
						((VerticalLayout)cbPeriodicidade.getParent()).setExpandRatio(chart, 1f); 
					}
				}
			}
		});
    	
    	return cbPeriodicidade;
    }
    
    ComboBox cbTipos;
    private ComboBox buildCbTipos(boolean setStyle){
    	
//    	EntityManager em = ConnUtil.getEntity();
//    	Query q = em.createQuery("select go from GrupoOse go where go.empresa_id=:empresa", GrupoOse.class);
//    	q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
//    	List<GrupoOse> tipos = q.getResultList();
    	
    	JPAContainer<GrupoOse> container = JPAContainerFactory.makeReadOnly(GrupoOse.class, ConnUtil.getEntity());
    	container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
    	
    	cbTipos = new ComboBox(null, container);
    	cbTipos.setItemCaptionPropertyId("nome");    	
    	cbTipos.setImmediate(true);
    	
    	if(setStyle){
    		cbTipos.addStyleName("margin-left-18");
    	}else{
    		cbTipos.addStyleName("margin-left-10");
    	}
    	
    	cbTipos.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
					if(cbTipos.getParent() instanceof CssLayout){
						((CssLayout)cbTipos.getParent()).replaceComponent(chart, buildChart(true));
					}
					
					if(cbTipos.getParent() instanceof VerticalLayout){
						((VerticalLayout)cbTipos.getParent()).replaceComponent(chart, buildChart(false));
						((VerticalLayout)cbTipos.getParent()).setExpandRatio(chart, 1f); 
					}

			}
		});
    	
    	return cbTipos;
    }
    
    private ComboBox buildCbTipoData(boolean setStyle){
    	cbTipoData = new ComboBox();
    	cbTipoData.setNullSelectionAllowed(false);
    	cbTipoData.setTextInputAllowed(false);
    	cbTipoData.addItem("DATA ABERTURA");
    	cbTipoData.addItem("DATA CONCLUSAO");    	
    	cbTipoData.select("DATA CONCLUSAO");
    	cbTipoData.setImmediate(true); 
    	
    	if(setStyle){
    		cbTipoData.addStyleName("margin-left-18");
    	}else{
    		cbTipoData.addStyleName("margin-left-10");
    	}
    	
    	cbTipoData.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbTipoData.getValue() != null){
					if(cbTipoData.getParent() instanceof CssLayout){
						((CssLayout)cbTipoData.getParent()).replaceComponent(chart, buildChart(true));
					}
					
					if(cbTipoData.getParent() instanceof VerticalLayout){
						((VerticalLayout)cbTipoData.getParent()).replaceComponent(chart, buildChart(false));
						((VerticalLayout)cbTipoData.getParent()).setExpandRatio(chart, 1f); 
					}
				}
			}
		});
    	
    	return cbTipoData;
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
    
    private Button buildConfigure2(){
    	btConfigure = new Button();
    	btConfigure.addStyleName("configure");
    	btConfigure.addStyleName("icon-cog");
    	btConfigure.addStyleName("icon-only");
    	btConfigure.addStyleName("botderless");
    	btConfigure.setDescription("Configurar");
    	btConfigure.addStyleName("small");
    	btConfigure.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				Notify.Show("Function not allow", Notify.TYPE_ERROR);
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
            	  PrefeDashDAO.remove(RoteirizacaoChart.class.toString());
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
                Window winMaximize = new Window("Gráfico Roteirização");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");
                winMaximize.setContent(new VerticalLayout(){
                	{
                		setSizeFull();
                		setSpacing(true); 
                		setMargin(true); 
                		                	
        				setWidth("100%");
        				addComponent(buildCbPrioridade(false));
        				addComponent(buildCbTipoData(false));
                		
                		addComponent(buildChart(false));
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
    
    private Chart buildChart(boolean label){
    	chart = new Chart();
    	if(label){
    		chart.setCaption("Gráfico Roteirização");
    		chart.getConfiguration().getLegend().setEnabled(false);
    	}
    	
    	chart.getConfiguration().disableCredits();
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().setExporting(true);
        chart.getConfiguration().getTooltip()       
        .setFormatter("this.series.name +': '+ this.y");
        //chart.getConfiguration().getTooltip().setValuePrefix("R$ ");
        chart.getConfiguration().getyAxis().setTitle("Quantidade");
        chart.setLocale(new Locale("pt", "BR"));     
        
        chart.setWidth("100%");
        
        if(label){
        	chart.setHeight("90%");
        }else{
        	chart.setHeight("100%");
        }

        Color[] colors = new VaadinTheme().getColors();
        PlotOptionsColumn column = new PlotOptionsColumn();
        column.setCursor(Cursor.POINTER);
        column.setDataLabels(new Labels(true));
        column.getDataLabels().setColor(colors[0]);
        column.getDataLabels().getStyle().setFontWeight(FontWeight.BOLD);
        column.getDataLabels().setFormatter("this.y");
        
        chart.getConfiguration().setPlotOptions(column);

        
        EntityManager em = ConnUtil.getEntity();
        
        Query q = null;
        
        String data = "DATA CONCLUSAO";
        if(cbTipoData != null && cbTipoData.getValue() != null && cbTipoData.getValue().equals("DATA ABERTURA")){
        	data = "DATA ABERTURA";
        }else if(cbTipoData != null && cbTipoData.getValue() != null && cbTipoData.getValue().equals("DATA CONCLUSAO")){
        	data = "DATA CONCLUSAO";
        }
        
        if(cbPeriodicidade.getValue().toString().equals("HOJE")){
        	
        	if(data.equals("DATA CONCLUSAO")){        	 
        		
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
		        	q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, "
		        			+ "DATE_FORMAT( DATA_CONCLUSAO,  '%d/%m/%Y' ) as DATA FROM  `ose` WHERE DATE_FORMAT( DATA_CONCLUSAO,  '%Y-%m-%d') =:data1 "
		        			+ "AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID");
				}
        	}else{
        		
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' ) >=  :data1  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
	        		q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, "
		        			+ "DATE_FORMAT( DATA_ABERTURA,  '%d/%m/%Y' ) as DATA FROM  `ose` WHERE DATE_FORMAT( DATA_ABERTURA,  '%Y-%m-%d') =:data1 "
		        			+ "AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID");
				}
        	}
        	
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	
        	q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());   		
        	q.setParameter("data1", sdf.format(new Date()));
        	//Lists.reverse(
        	List<Object[]> result = q.getResultList();
        	
        	
        	List<String> s = new ArrayList<>();
        	for(Object[] o:result)
        	{        	
        		if(!s.contains((String)o[2])){
        			s.add((String)o[2]);
        		}
        		
        		ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);       		        	        	    	
        		chart.getConfiguration().addSeries(serie);
        	}       

        	
        	
			if(s.size() == 1){
				chart.getConfiguration().getxAxis().setCategories(s.get(0).toString());
			}else if(s.size() == 2){
				chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString());
			}else if(s.size() == 3){
				chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString(),s.get(2).toString());
			}
			
        	
        }else if(cbPeriodicidade.getValue().toString().equals("ONTEM")){
        	
        	
        	if(data.equals("DATA CONCLUSAO")){    
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, "
							+ "DATE_FORMAT( DATA_CONCLUSAO,  '%d/%m/%Y' ) as DATA FROM  `ose` WHERE DATE_FORMAT( DATA_CONCLUSAO,  '%Y-%m-%d') =:data1 "
							+ "AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID");
				}
        	}else{
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' ) >=  :data1  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
	        		q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, "
							+ "DATE_FORMAT( DATA_ABERTURA,  '%d/%m/%Y' ) as DATA FROM  `ose` WHERE DATE_FORMAT( DATA_ABERTURA,  '%Y-%m-%d') =:data1 "
							+ "AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID");
				}
        	}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());   		
			q.setParameter("data1", sdf.format(new DateTime().minusDays(1).toDate()));
			//Lists.reverse(
			List<Object[]> result = q.getResultList();
			
			
			List<String> s = new ArrayList<>();
			for(Object[] o:result)
			{        	
				if(!s.contains((String)o[2])){
					s.add((String)o[2]);
				}
				
				ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);       		        	        	    	
				chart.getConfiguration().addSeries(serie);
			}       
			
			
			
				if(s.size() == 1){
					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString());
				}else if(s.size() == 2){
					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString());
				}else if(s.size() == 3){
					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString(),s.get(2).toString());
				}
			
			
		}else if(cbPeriodicidade.getValue().toString().equals("1 MES")){
        	

			if(data.equals("DATA CONCLUSAO")){      
				
				if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{					
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
							+ "DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1  AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID");
				}
				
			}else{
				
				if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' ) >=  :data1  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' ) >=  :data1  AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID");
				}
			}
        	q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
                
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    		
        	q.setParameter("data1", sdf.format(new DateTime().toDate()));

        	//Lists.reverse(
        	List<Object[]> result = q.getResultList();
        	
       
        	
        	List<String> s = new ArrayList<>();     	
        	for(Object[] o:result)
        	{        	
        		if(!s.contains((String)o[2])){
        			s.add((String)o[2]);
        		}
        		
        		ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);       		        	        	    	
        		chart.getConfiguration().addSeries(serie);
        	}       

        	
        	
				if(s.size() == 1){
					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString());
				}else if(s.size() == 2){
					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString());
				}else if(s.size() == 3){
					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString(),s.get(2).toString());
				}
			
        	
        }else  if(cbPeriodicidade.getValue().toString().equals("3 MESES")){
        	
        	//chart.getConfiguration().getxAxis().setCategories(categories);
        	SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        	SimpleDateFormat sdfCHora = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        	chart.getConfiguration().getxAxis().setCategories(sdf.format(new DateTime().minusMonths(2).toDate()).toString(),sdf.format(new DateTime().minusMonths(1).toDate()).toString(),sdf.format(new DateTime().toDate()).toString());        		
        	
        	
  //      	SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
//        			+ "DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1  AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID
        	
        	if(data.equals("DATA CONCLUSAO")){      
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m-%d' )  <=  :data2  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
		        	q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m-%d' ) >=  :data1 AND DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m-%d' )  <=  :data2   AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' )  ORDER BY DATA_CONCLUSAO ");
				}
        		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        	
        	}else{
        		
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' )  <=  :data2  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
	        		q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' ) >=  :data1 AND DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' )  <=  :data2   AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' )  ORDER BY DATA_ABERTURA ");
				}
        		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        	}
           
        	try{
        		q.setParameter("data1", sdfCHora.parse(sdfCHora.format(new DateTime().minusMonths(3).toDate()).toString()+" 00:00:00"));
        		q.setParameter("data2", sdfCHora.parse(sdfCHora.format(new DateTime().toDate()).toString()+" 00:00:00"));
        	}catch(Exception e){
        		
        	}
        	
        	if(cbTipos != null && cbTipos.getValue() != null){
        		//	Lists.reverse(
        		List<Object[]> result = q.getResultList();
            	
        	       
            	
            	List<String> s = new ArrayList<>();     	
            	for(Object[] o:result)
            	{        	
            		if(!s.contains((String)o[2])){
            			s.add((String)o[2]);
            		}
            		
            		ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);       		        	        	    	
            		chart.getConfiguration().addSeries(serie);
            	}       

            	
            	
    				if(s.size() == 1){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString());
    				}else if(s.size() == 2){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString());
    				}else if(s.size() == 3){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString(),s.get(2).toString());
    				}
        		
        	}else{

	        	List<Object[]> result = q.getResultList();
	        	
	        	ListSeries serie;
	        	        	
	        	List<Number> hashALTERACAO_MATERIAL = new ArrayList<>();        	
	        	List<Number> hashCANCELAMENTO = new ArrayList<>();
	        	List<Number> hashENTREGA = new ArrayList<>();
	        	List<Number> hashINSTALACAO = new ArrayList<>();
	        	List<Number> hashMUDANCA_ENDERECO = new ArrayList<>();
	        	List<Number> hashREPARO = new ArrayList<>();
	        	List<Number> hashSERVICO = new ArrayList<>();
	        	List<Number> hashMIGRACAO = new ArrayList<>();
	        	List<Number> hashESTRUTURA = new ArrayList<>();
	        	
	        	for(Object[] o:result){
	        		       				
	        		if(((String)o[0]).equals("ESTRUTURA")){
	        			hashESTRUTURA.add((Number)o[1]);
	        		}
	        		if(((String)o[0]).equals("MIGRACAO")){
	        			hashMIGRACAO.add((Number)o[1]);
	        		}
	        		
	        		if(((String)o[0]).equals("ALTERACAO MATERIAL")){
	        			hashALTERACAO_MATERIAL.add((Number)o[1]);
	        		}
	        		
	        		if(((String)o[0]).equals("CANCELAMENTO")){
	        			hashCANCELAMENTO.add((Number)o[1]);
	        		}
	        		
	        		if(((String)o[0]).equals("ENTREGA")){
	        			hashENTREGA.add((Number)o[1]);
	        		}
	        		
	        		if(((String)o[0]).equals("INSTALACAO")){
	        			hashINSTALACAO.add((Number)o[1]);
	        		}
	        		
	        		if(((String)o[0]).equals("MUDANCA ENDERECO")){
	        			hashMUDANCA_ENDERECO.add((Number)o[1]);
	        		}
	        		
	        		if(((String)o[0]).equals("REPARO")){
	        			hashREPARO.add((Number)o[1]);
	        		}
	        		
	        		if(((String)o[0]).equals("SERVICO")){
	        			hashSERVICO.add((Number)o[1]);
	        		}       		
					 
				}	
	        	
	        	if(((Integer)hashALTERACAO_MATERIAL.size()).equals(1)){
	        		serie = new ListSeries("ALTERACAO MATERIAL",hashALTERACAO_MATERIAL.get(0));
	        		chart.getConfiguration().addSeries(serie);
	        	}        	
	        	if(((Integer)hashALTERACAO_MATERIAL.size()).equals(2)){
	        		serie = new ListSeries("ALTERACAO MATERIAL",hashALTERACAO_MATERIAL.get(0),hashALTERACAO_MATERIAL.get(1));
	        		chart.getConfiguration().addSeries(serie);
	        	}        	
	        	if(((Integer)hashALTERACAO_MATERIAL.size()).equals(4)){
	        		serie = new ListSeries("ALTERACAO MATERIAL",hashALTERACAO_MATERIAL.get(1),hashALTERACAO_MATERIAL.get(2),hashALTERACAO_MATERIAL.get(3));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	
	        	
	        	if(((Integer)hashCANCELAMENTO.size()).equals(1)){
	        		serie = new ListSeries("CANCELAMENTO",hashCANCELAMENTO.get(0));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashCANCELAMENTO.size()).equals(2)){
	        		serie = new ListSeries("CANCELAMENTO",hashCANCELAMENTO.get(0),hashCANCELAMENTO.get(1));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashCANCELAMENTO.size()).equals(4)){
	        		serie = new ListSeries("CANCELAMENTO",hashCANCELAMENTO.get(1),hashCANCELAMENTO.get(2),hashCANCELAMENTO.get(3));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	
	        	if(((Integer)hashENTREGA.size()).equals(1)){
	        		serie = new ListSeries("ENTREGA",hashENTREGA.get(0));
	        		chart.getConfiguration().addSeries(serie);
	        	}        	
	        	if(((Integer)hashENTREGA.size()).equals(2)){
	        		serie = new ListSeries("ENTREGA",hashENTREGA.get(0),hashENTREGA.get(1));
	        		chart.getConfiguration().addSeries(serie);
	        	}        	
	        	if(((Integer)hashENTREGA.size()).equals(4)){
	        		serie = new ListSeries("ENTREGA",hashENTREGA.get(1),hashENTREGA.get(2),hashENTREGA.get(3));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	
	        	
	        	if(((Integer)hashINSTALACAO.size()).equals(1)){
	        		serie = new ListSeries("INSTALACAO",hashINSTALACAO.get(0));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashINSTALACAO.size()).equals(2)){
	        		serie = new ListSeries("INSTALACAO",hashINSTALACAO.get(0),hashINSTALACAO.get(1));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashINSTALACAO.size()).equals(4)){
	        		serie = new ListSeries("INSTALACAO",hashINSTALACAO.get(1),hashINSTALACAO.get(2),hashINSTALACAO.get(3));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	
	        	
	        	if(((Integer)hashMUDANCA_ENDERECO.size()).equals(1)){
	        		serie = new ListSeries("MUDANCA ENDERECO",hashMUDANCA_ENDERECO.get(0));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashMUDANCA_ENDERECO.size()).equals(2)){
	        		serie = new ListSeries("MUDANCA ENDERECO",hashMUDANCA_ENDERECO.get(0),hashMUDANCA_ENDERECO.get(1));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashMUDANCA_ENDERECO.size()).equals(4)){
	        		serie = new ListSeries("MUDANCA ENDERECO",hashMUDANCA_ENDERECO.get(1),hashMUDANCA_ENDERECO.get(2),hashMUDANCA_ENDERECO.get(3));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	
	        	
	        	if(((Integer)hashREPARO.size()).equals(1)){
	        	serie = new ListSeries("REPARO",hashREPARO.get(0));
	        	chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashREPARO.size()).equals(2)){
	            	serie = new ListSeries("REPARO",hashREPARO.get(0),hashREPARO.get(1));
	            	chart.getConfiguration().addSeries(serie);
	            }
	        	if(((Integer)hashREPARO.size()).equals(4)){
	            	serie = new ListSeries("REPARO",hashREPARO.get(1),hashREPARO.get(2),hashREPARO.get(3));
	            	chart.getConfiguration().addSeries(serie);
	            }
	        	
	        	
	        	if(((Integer)hashSERVICO.size()).equals(1)){
	        		serie = new ListSeries("SERVICO",hashSERVICO.get(0));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashSERVICO.size()).equals(2)){
	        		serie = new ListSeries("SERVICO",hashSERVICO.get(0),hashSERVICO.get(1));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashSERVICO.size()).equals(4)){
	        		serie = new ListSeries("SERVICO",hashSERVICO.get(1),hashSERVICO.get(2),hashSERVICO.get(3));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	
	        	if(((Integer)hashMIGRACAO.size()).equals(1)){
	        		serie = new ListSeries("MIGRACAO",hashMIGRACAO.get(0));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashMIGRACAO.size()).equals(2)){
	        		serie = new ListSeries("MIGRACAO",hashMIGRACAO.get(0),hashMIGRACAO.get(1));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashMIGRACAO.size()).equals(4)){
	        		serie = new ListSeries("MIGRACAO",hashMIGRACAO.get(1),hashMIGRACAO.get(2),hashMIGRACAO.get(3));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	
	        	if(((Integer)hashESTRUTURA.size()).equals(1)){
	        		serie = new ListSeries("ESTRUTURA",hashESTRUTURA.get(0));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashESTRUTURA.size()).equals(2)){
	        		serie = new ListSeries("ESTRUTURA",hashESTRUTURA.get(0),hashESTRUTURA.get(1));
	        		chart.getConfiguration().addSeries(serie);
	        	}
	        	if(((Integer)hashESTRUTURA.size()).equals(4)){
	        		serie = new ListSeries("ESTRUTURA",hashESTRUTURA.get(1),hashESTRUTURA.get(2),hashESTRUTURA.get(3));
	        		chart.getConfiguration().addSeries(serie);
	        	}
        	
        	
        	}
				
		}else  if(cbPeriodicidade.getValue().toString().equals("ANO ATUAL")){
        	
        	//chart.getConfiguration().getxAxis().setCategories(categories);
        	SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        	SimpleDateFormat sdfCHora = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        	chart.getConfiguration().getxAxis().setMin(0);
        	chart.getConfiguration().getxAxis().setCategories(
        			sdf.format(new DateTime().toDate()).toString(),        		
        			sdf.format(new DateTime().minusMonths(1).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(2).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(3).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(4).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(5).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(6).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(7).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(8).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(9).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(10).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(11).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(12).toDate()).toString(),
        			sdf.format(new DateTime().minusMonths(13).toDate()).toString());
        	
        	if(data.equals("DATA CONCLUSAO")){
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m-%d' )  <=  :data2   AND  DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m-%d' )  <=  :data2  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
		        	q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m-%d' ) >=  :data1 AND DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m-%d' )  <=  :data2   AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' )  ORDER BY  DATA_CONCLUSAO ");
				}
        		
        		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        	}else{
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' )  <=  :data2   AND  DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' )  <=  :data2  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
	        		q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' ) >=  :data1 AND DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' )  <=  :data2   AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' )  ORDER BY  DATA_ABERTURA ");
				}

        		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        	}
           
        	try{
        		q.setParameter("data1", sdfCHora.parse(sdfCHora.format(new DateTime().minusMonths(13).toDate()).toString()+" 00:00:00"));
        		q.setParameter("data2", sdfCHora.parse(sdfCHora.format(new DateTime().toDate()).toString()+" 00:00:00"));
        	}catch(Exception e){
        		
        	}
        	
        	
        	if(cbTipos != null && cbTipos.getValue() != null){
        		//Lists.reverse(
        		List<Object[]> result = q.getResultList();
            	
        	       
            	
            	List<String> s = new ArrayList<>();     	
            	for(Object[] o:result)
            	{        	
            		if(!s.contains((String)o[2])){
            			s.add((String)o[2]);
            		}
            		
            		ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);       		        	        	    	
            		chart.getConfiguration().addSeries(serie);
            	}       

            	
            	
    				if(s.size() == 1){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString());
    				}else if(s.size() == 2){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString());
    				}else if(s.size() == 3){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString(),s.get(2).toString());
    				}
        	}else{
	        	List<String> mesesChave = new ArrayList<>();
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(13).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(12).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(11).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(10).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(9).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(8).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(7).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(6).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(5).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(4).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(3).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(2).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusMonths(1).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().toDate()).toString());
	        	
	        	List<Object[]> result = q.getResultList();
	
	        	
	        	ListSeries serie;
	        	
	        	
	        	HashMap<String,Number> hashALTERACAO_MATERIAL = new HashMap<>();
	        	HashMap<String,Number> hashCANCELAMENTO = new HashMap<>();
	        	HashMap<String,Number> hashENTREGA = new HashMap<>();
	        	HashMap<String,Number> hashINSTALACAO = new HashMap<>();
	        	HashMap<String,Number> hashMUDANCA_ENDERECO = new HashMap<>();        	
	        	HashMap<String,Number> hashREPARO = new HashMap<>();
	        	HashMap<String,Number> hashSERVICO = new HashMap<>();
	        	HashMap<String,Number> hashMIGRACAO = new HashMap<>();
	        	HashMap<String,Number> hashESTRUTURA = new HashMap<>();
	        	
	        	for(Object[] o:result){
	        		       				
	        		
	        		if(((String)o[0]).equals("MIGRACAO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashMIGRACAO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("ESTRUTURA") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashESTRUTURA.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		
	        		if(((String)o[0]).equals("ALTERACAO MATERIAL") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashALTERACAO_MATERIAL.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		
	        		if(((String)o[0]).equals("CANCELAMENTO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashCANCELAMENTO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("ENTREGA") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashENTREGA.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		        		
	        		if(((String)o[0]).equals("INSTALACAO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashINSTALACAO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		        		
	        		if(((String)o[0]).equals("MUDANCA ENDERECO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashMUDANCA_ENDERECO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("REPARO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashREPARO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("SERVICO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashSERVICO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
					 
				}	
	        	
	        	
	        	//ALTERACAO MATERIAL
	        	List<Number> hashALTERACAO_MATERIAL_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashALTERACAO_MATERIAL.get(mes) != null){        				
	        				hashALTERACAO_MATERIAL_COMPLETO.add(hashALTERACAO_MATERIAL.get(mes));        				
	        			}else{        				
	        				hashALTERACAO_MATERIAL_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ALTERACAO MATERIAL");
	        	for (Number number2 : hashALTERACAO_MATERIAL_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	
	        	//CANCELAMENTO
	        	List<Number> hashCANCELAMENTO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashCANCELAMENTO.get(mes) != null){        				
	        				hashCANCELAMENTO_COMPLETO.add(hashCANCELAMENTO.get(mes));        				
	        			}else{        				
	        				hashCANCELAMENTO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("CANCELAMENTO");
	        	for (Number number2 : hashCANCELAMENTO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//ENTREGA
	        	List<Number> hashENTREGA_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashENTREGA.get(mes) != null){        				
	        				hashENTREGA_COMPLETO.add(hashENTREGA.get(mes));        				
	        			}else{        				
	        				hashENTREGA_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ENTREGA");
	        	for (Number number2 : hashENTREGA_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//INSTALACAO
	        	List<Number> hashINSTALACAO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashINSTALACAO.get(mes) != null){        				
	        				hashINSTALACAO_COMPLETO.add(hashINSTALACAO.get(mes));        				
	        			}else{        				
	        				hashINSTALACAO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("INSTALACAO");
	        	for (Number number2 : hashINSTALACAO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//MUDANCA ENDERECO
	        	List<Number> hashMUDANCA_ENDERECO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashINSTALACAO.get(mes) != null){        				
	        				hashMUDANCA_ENDERECO_COMPLETO.add(hashMUDANCA_ENDERECO.get(mes));        				
	        			}else{        				
	        				hashMUDANCA_ENDERECO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("MUDANCA ENDERECO");
	        	for (Number number2 : hashMUDANCA_ENDERECO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//REPARO
	        	List<Number> hashREPARO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashREPARO.get(mes) != null){        				
	        				hashREPARO_COMPLETO.add(hashREPARO.get(mes));        				
	        			}else{        				
	        				hashREPARO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("REPARO");
	        	for (Number number2 : hashREPARO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//SERVICO
	        	List<Number> hashSERVICO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashSERVICO.get(mes) != null){        				
	        				hashSERVICO_COMPLETO.add(hashSERVICO.get(mes));        				
	        			}else{        				
	        				hashSERVICO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("SERVICO");
	        	for (Number number2 : hashSERVICO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//MIGRACAO
	        	List<Number> hashMIGRACAO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashMIGRACAO.get(mes) != null){        				
	        				hashMIGRACAO_COMPLETO.add(hashMIGRACAO.get(mes));        				
	        			}else{        				
	        				hashMIGRACAO_COMPLETO.add(0);
	        			}        		
				}
	        	
	        	serie = new  ListSeries("MIGRACAO");
	        	for (Number number2 : hashMIGRACAO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//ESTRUTURA
	        	List<Number> hashESTRUTURA_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashESTRUTURA.get(mes) != null){        				
	        				hashESTRUTURA_COMPLETO.add(hashMIGRACAO.get(mes));        				
	        			}else{        				
	        				hashESTRUTURA_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ESTRUTURA");
	        	for (Number number2 : hashESTRUTURA_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);

        	
        	}
		}else  if(cbPeriodicidade.getValue().toString().equals("ANO ANTERIOR")){
        	
        	//chart.getConfiguration().getxAxis().setCategories(categories);
        	SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        	SimpleDateFormat sdfAno = new SimpleDateFormat("yyyy");
        	
        	chart.getConfiguration().getxAxis().setMin(0);
        	String anoAnterior = sdfAno.format(new DateTime().minusYears(1).toDate());
        	
        	chart.getConfiguration().getxAxis().setCategories("12/"+anoAnterior, "11/"+anoAnterior, "10/"+anoAnterior,"09/"+anoAnterior,"08/"+anoAnterior,"07/"+anoAnterior,"06/"+anoAnterior,"05/"+anoAnterior, "04/"+anoAnterior,"03/"+anoAnterior,"02/"+anoAnterior,"01/"+anoAnterior);        		
        	
        	if(data.equals("DATA CONCLUSAO")){
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' )  <=  :data2 AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
		        	q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' )  <=  :data2   AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' )  ORDER BY  DATA_CONCLUSAO ");
				}
        		
        		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        	}else{
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' )  <=  :data2   AND  DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' )  <=  :data2  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
	        		q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' )  <=  :data2   AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' )  ORDER BY  DATA_ABERTURA ");
				}

        		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        	}
           
        	try{
        		q.setParameter("data1", sdfAno.format(new DateTime().minusYears(1).toDate())+"-01");
        		q.setParameter("data2", sdfAno.format(new DateTime().minusYears(1).toDate())+"-12");
        	}catch(Exception e){
        		
        	}
        	
        	
        	if(cbTipos != null && cbTipos.getValue() != null){
        		//Lists.reverse(
        		List<Object[]> result = q.getResultList();
            	
        	       
            	
            	List<String> s = new ArrayList<>();     	
            	for(Object[] o:result)
            	{        	
            		if(!s.contains((String)o[2])){
            			s.add((String)o[2]);
            		}
            		
            		ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);       		        	        	    	
            		chart.getConfiguration().addSeries(serie);
            	}       

            	
            	
    				if(s.size() == 1){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString());
    				}else if(s.size() == 2){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString());
    				}else if(s.size() == 3){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString(),s.get(2).toString());
    				}
        	}else{
	        	List<String> mesesChave = new ArrayList<>();
	        	
	        	mesesChave.add("01/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("02/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("03/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("04/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("05/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("06/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("07/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("08/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("09/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("10/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("11/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	mesesChave.add("12/"+sdfAno.format(new DateTime().minusYears(1).toDate()));
	        	        	
	        	List<Object[]> result = q.getResultList();
	
	        	
	        	ListSeries serie;
	        	
	        	
	        	HashMap<String,Number> hashALTERACAO_MATERIAL = new HashMap<>();
	        	HashMap<String,Number> hashCANCELAMENTO = new HashMap<>();
	        	HashMap<String,Number> hashENTREGA = new HashMap<>();
	        	HashMap<String,Number> hashINSTALACAO = new HashMap<>();
	        	HashMap<String,Number> hashMUDANCA_ENDERECO = new HashMap<>();        	
	        	HashMap<String,Number> hashREPARO = new HashMap<>();
	        	HashMap<String,Number> hashSERVICO = new HashMap<>();
	        	HashMap<String,Number> hashMIGRACAO = new HashMap<>();
	        	HashMap<String,Number> hashESTRUTURA = new HashMap<>();
	        	
	        	for(Object[] o:result){
	        		       				
	        		
	        		if(((String)o[0]).equals("MIGRACAO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashMIGRACAO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("ESTRUTURA") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashESTRUTURA.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		
	        		if(((String)o[0]).equals("ALTERACAO MATERIAL") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashALTERACAO_MATERIAL.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		
	        		if(((String)o[0]).equals("CANCELAMENTO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashCANCELAMENTO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("ENTREGA") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashENTREGA.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		        		
	        		if(((String)o[0]).equals("INSTALACAO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashINSTALACAO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		        		
	        		if(((String)o[0]).equals("MUDANCA ENDERECO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashMUDANCA_ENDERECO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("REPARO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashREPARO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("SERVICO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashSERVICO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
					 
				}	
	        	
	        	
	        	//ALTERACAO MATERIAL
	        	List<Number> hashALTERACAO_MATERIAL_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashALTERACAO_MATERIAL.get(mes) != null){        				
	        				hashALTERACAO_MATERIAL_COMPLETO.add(hashALTERACAO_MATERIAL.get(mes));        				
	        			}else{        				
	        				hashALTERACAO_MATERIAL_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ALTERACAO MATERIAL");
	        	for (Number number2 : hashALTERACAO_MATERIAL_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	
	        	//CANCELAMENTO
	        	List<Number> hashCANCELAMENTO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashCANCELAMENTO.get(mes) != null){        				
	        				hashCANCELAMENTO_COMPLETO.add(hashCANCELAMENTO.get(mes));        				
	        			}else{        				
	        				hashCANCELAMENTO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("CANCELAMENTO");
	        	for (Number number2 : hashCANCELAMENTO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//ENTREGA
	        	List<Number> hashENTREGA_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashENTREGA.get(mes) != null){        				
	        				hashENTREGA_COMPLETO.add(hashENTREGA.get(mes));        				
	        			}else{        				
	        				hashENTREGA_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ENTREGA");
	        	for (Number number2 : hashENTREGA_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//INSTALACAO
	        	List<Number> hashINSTALACAO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashINSTALACAO.get(mes) != null){        				
	        				hashINSTALACAO_COMPLETO.add(hashINSTALACAO.get(mes));        				
	        			}else{        				
	        				hashINSTALACAO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("INSTALACAO");
	        	for (Number number2 : hashINSTALACAO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//MUDANCA ENDERECO
	        	List<Number> hashMUDANCA_ENDERECO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashINSTALACAO.get(mes) != null){        				
	        				hashMUDANCA_ENDERECO_COMPLETO.add(hashMUDANCA_ENDERECO.get(mes));        				
	        			}else{        				
	        				hashMUDANCA_ENDERECO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("MUDANCA ENDERECO");
	        	for (Number number2 : hashMUDANCA_ENDERECO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//REPARO
	        	List<Number> hashREPARO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashREPARO.get(mes) != null){        				
	        				hashREPARO_COMPLETO.add(hashREPARO.get(mes));        				
	        			}else{        				
	        				hashREPARO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("REPARO");
	        	for (Number number2 : hashREPARO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//SERVICO
	        	List<Number> hashSERVICO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashSERVICO.get(mes) != null){        				
	        				hashSERVICO_COMPLETO.add(hashSERVICO.get(mes));        				
	        			}else{        				
	        				hashSERVICO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("SERVICO");
	        	for (Number number2 : hashSERVICO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//MIGRACAO
	        	List<Number> hashMIGRACAO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashMIGRACAO.get(mes) != null){        				
	        				hashMIGRACAO_COMPLETO.add(hashMIGRACAO.get(mes));        				
	        			}else{        				
	        				hashMIGRACAO_COMPLETO.add(0);
	        			}        		
				}
	        	
	        	serie = new  ListSeries("MIGRACAO");
	        	for (Number number2 : hashMIGRACAO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//ESTRUTURA
	        	List<Number> hashESTRUTURA_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashESTRUTURA.get(mes) != null){        				
	        				hashESTRUTURA_COMPLETO.add(hashMIGRACAO.get(mes));        				
	        			}else{        				
	        				hashESTRUTURA_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ESTRUTURA");
	        	for (Number number2 : hashESTRUTURA_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);

        	
        	}
		}else  if(cbPeriodicidade.getValue().toString().equals("DOIS ANOS ATRAS")){
        	
        	//chart.getConfiguration().getxAxis().setCategories(categories);
        	SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        	SimpleDateFormat sdfAno = new SimpleDateFormat("yyyy");
        	
        	chart.getConfiguration().getxAxis().setMin(0);
        	String anoAnterior = sdfAno.format(new DateTime().minusYears(2).toDate());
        	
        	chart.getConfiguration().getxAxis().setCategories("12/"+anoAnterior, "11/"+anoAnterior, "10/"+anoAnterior,"09/"+anoAnterior,"08/"+anoAnterior,"07/"+anoAnterior,"06/"+anoAnterior,"05/"+anoAnterior, "04/"+anoAnterior,"03/"+anoAnterior,"02/"+anoAnterior,"01/"+anoAnterior);        		
        	
        	if(data.equals("DATA CONCLUSAO")){
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' )  <=  :data2 AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
		        	q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_CONCLUSAO,  '%Y-%m' )  <=  :data2   AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' )  ORDER BY  DATA_CONCLUSAO ");
				}
        		
        		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        	}else{
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%m/%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' )  <=  :data2   AND  DATE_FORMAT(DATA_ABERTURA,  '%Y-%m-%d' )  <=  :data2  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
	        		q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' ) >=  :data1 AND DATE_FORMAT(DATA_ABERTURA,  '%Y-%m' )  <=  :data2   AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID, DATE_FORMAT( DATA_ABERTURA,  '%m/%Y' )  ORDER BY  DATA_ABERTURA ");
				}

        		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        	}
           
        	try{
        		q.setParameter("data1", sdfAno.format(new DateTime().minusYears(2).toDate())+"-01");
        		q.setParameter("data2", sdfAno.format(new DateTime().minusYears(2).toDate())+"-12");
        	}catch(Exception e){
        		
        	}
        	
        	
        	if(cbTipos != null && cbTipos.getValue() != null){
        		
        		//Lists.reverse(
        		List<Object[]> result = q.getResultList();
            	
        	       
            	
            	List<String> s = new ArrayList<>();     	
            	for(Object[] o:result)
            	{        	
            		if(!s.contains((String)o[2])){
            			s.add((String)o[2]);
            		}
            		
            		ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);       		        	        	    	
            		chart.getConfiguration().addSeries(serie);
            	}       

            	
            	
    				if(s.size() == 1){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString());
    				}else if(s.size() == 2){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString());
    				}else if(s.size() == 3){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString(),s.get(2).toString());
    				}
        	}else{
	        	List<String> mesesChave = new ArrayList<>();
	        	
	        	mesesChave.add("01/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("02/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("03/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("04/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("05/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("06/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("07/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("08/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("09/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("10/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("11/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	mesesChave.add("12/"+sdfAno.format(new DateTime().minusYears(2).toDate()));
	        	        	
	        	List<Object[]> result = q.getResultList();
	
	        	
	        	ListSeries serie;
	        	
	        	
	        	HashMap<String,Number> hashALTERACAO_MATERIAL = new HashMap<>();
	        	HashMap<String,Number> hashCANCELAMENTO = new HashMap<>();
	        	HashMap<String,Number> hashENTREGA = new HashMap<>();
	        	HashMap<String,Number> hashINSTALACAO = new HashMap<>();
	        	HashMap<String,Number> hashMUDANCA_ENDERECO = new HashMap<>();        	
	        	HashMap<String,Number> hashREPARO = new HashMap<>();
	        	HashMap<String,Number> hashSERVICO = new HashMap<>();
	        	HashMap<String,Number> hashMIGRACAO = new HashMap<>();
	        	HashMap<String,Number> hashESTRUTURA = new HashMap<>();
	        	
	        	for(Object[] o:result){
	        		       				
	        		
	        		if(((String)o[0]).equals("MIGRACAO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashMIGRACAO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("ESTRUTURA") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashESTRUTURA.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		
	        		if(((String)o[0]).equals("ALTERACAO MATERIAL") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashALTERACAO_MATERIAL.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		
	        		if(((String)o[0]).equals("CANCELAMENTO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashCANCELAMENTO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("ENTREGA") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashENTREGA.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		        		
	        		if(((String)o[0]).equals("INSTALACAO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashINSTALACAO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		        		
	        		if(((String)o[0]).equals("MUDANCA ENDERECO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashMUDANCA_ENDERECO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("REPARO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashREPARO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("SERVICO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashSERVICO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
					 
				}	
	        	
	        	
	        	//ALTERACAO MATERIAL
	        	List<Number> hashALTERACAO_MATERIAL_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashALTERACAO_MATERIAL.get(mes) != null){        				
	        				hashALTERACAO_MATERIAL_COMPLETO.add(hashALTERACAO_MATERIAL.get(mes));        				
	        			}else{        				
	        				hashALTERACAO_MATERIAL_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ALTERACAO MATERIAL");
	        	for (Number number2 : hashALTERACAO_MATERIAL_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	
	        	//CANCELAMENTO
	        	List<Number> hashCANCELAMENTO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashCANCELAMENTO.get(mes) != null){        				
	        				hashCANCELAMENTO_COMPLETO.add(hashCANCELAMENTO.get(mes));        				
	        			}else{        				
	        				hashCANCELAMENTO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("CANCELAMENTO");
	        	for (Number number2 : hashCANCELAMENTO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//ENTREGA
	        	List<Number> hashENTREGA_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashENTREGA.get(mes) != null){        				
	        				hashENTREGA_COMPLETO.add(hashENTREGA.get(mes));        				
	        			}else{        				
	        				hashENTREGA_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ENTREGA");
	        	for (Number number2 : hashENTREGA_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//INSTALACAO
	        	List<Number> hashINSTALACAO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashINSTALACAO.get(mes) != null){        				
	        				hashINSTALACAO_COMPLETO.add(hashINSTALACAO.get(mes));        				
	        			}else{        				
	        				hashINSTALACAO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("INSTALACAO");
	        	for (Number number2 : hashINSTALACAO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//MUDANCA ENDERECO
	        	List<Number> hashMUDANCA_ENDERECO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashINSTALACAO.get(mes) != null){        				
	        				hashMUDANCA_ENDERECO_COMPLETO.add(hashMUDANCA_ENDERECO.get(mes));        				
	        			}else{        				
	        				hashMUDANCA_ENDERECO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("MUDANCA ENDERECO");
	        	for (Number number2 : hashMUDANCA_ENDERECO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//REPARO
	        	List<Number> hashREPARO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashREPARO.get(mes) != null){        				
	        				hashREPARO_COMPLETO.add(hashREPARO.get(mes));        				
	        			}else{        				
	        				hashREPARO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("REPARO");
	        	for (Number number2 : hashREPARO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//SERVICO
	        	List<Number> hashSERVICO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashSERVICO.get(mes) != null){        				
	        				hashSERVICO_COMPLETO.add(hashSERVICO.get(mes));        				
	        			}else{        				
	        				hashSERVICO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("SERVICO");
	        	for (Number number2 : hashSERVICO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//MIGRACAO
	        	List<Number> hashMIGRACAO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashMIGRACAO.get(mes) != null){        				
	        				hashMIGRACAO_COMPLETO.add(hashMIGRACAO.get(mes));        				
	        			}else{        				
	        				hashMIGRACAO_COMPLETO.add(0);
	        			}        		
				}
	        	
	        	serie = new  ListSeries("MIGRACAO");
	        	for (Number number2 : hashMIGRACAO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//ESTRUTURA
	        	List<Number> hashESTRUTURA_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashESTRUTURA.get(mes) != null){        				
	        				hashESTRUTURA_COMPLETO.add(hashMIGRACAO.get(mes));        				
	        			}else{        				
	        				hashESTRUTURA_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ESTRUTURA");
	        	for (Number number2 : hashESTRUTURA_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);

        	
        	}
		}else  if(cbPeriodicidade.getValue().toString().equals("3 ANOS AGRUPADO")){
        	
        	//chart.getConfiguration().getxAxis().setCategories(categories);
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        	SimpleDateFormat sdfCHora = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        	chart.getConfiguration().getxAxis().setMin(0);
        	chart.getConfiguration().getxAxis().setCategories(sdf.format(new DateTime().minusYears(2).toDate()).toString(),sdf.format(new DateTime().minusYears(1).toDate()).toString(),sdf.format(new DateTime().toDate()).toString());        		
        	
        	if(data.equals("DATA CONCLUSAO")){
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_CONCLUSAO,  '%Y' ) >=  :data1 AND DATE_FORMAT(DATA_CONCLUSAO,  '%Y' )  <=  :data2    AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
		        	q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_CONCLUSAO,  '%Y' ) >=  :data1 AND DATE_FORMAT(DATA_CONCLUSAO,  '%Y' )  <=  :data2   AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID, DATE_FORMAT( DATA_CONCLUSAO,  '%Y' )  ORDER BY  DATA_CONCLUSAO ");
				}
        		
        		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        	}else{
        		if(cbTipos != null && cbTipos.getValue() != null){
					q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_subgrupo WHERE tipos_subgrupo.ID=TIPO_SUBGRUPO_ID) AS TIPOSUB, COUNT(TIPO_SUBGRUPO_ID) AS QTD, DATE_FORMAT( DATA_CONCLUSAO,  '%Y' ) as DATA FROM  `ose` WHERE   "
							+ "  DATE_FORMAT(DATA_ABERTURA,  '%Y' ) >=  :data1 AND DATE_FORMAT(DATA_ABERTURA,  '%Y' )  <=  :data2   AND  DATE_FORMAT(DATA_ABERTURA,  '%Y' )  <=  :data2  AND EMPRESA_ID =:codEmpresa and ose.GRUPO_ID =:grupo GROUP BY TIPOSUB");
					q.setParameter("grupo", cbTipos.getItem(cbTipos.getValue()).getItemProperty("id").getValue());
				}else{
	        		q = em.createNativeQuery("SELECT (SELECT NOME FROM tipos_ose WHERE tipos_ose.ID=GRUPO_ID) AS TIPO, COUNT(GRUPO_ID) AS QTD, DATE_FORMAT( DATA_ABERTURA,  '%Y' ) as DATA FROM  `ose` WHERE  "
		        			+ "DATE_FORMAT(DATA_ABERTURA,  '%Y' ) >=  :data1 AND DATE_FORMAT(DATA_ABERTURA,  '%Y' )  <=  :data2   AND EMPRESA_ID =:codEmpresa GROUP BY GRUPO_ID, DATE_FORMAT( DATA_ABERTURA,  '%Y' )  ORDER BY  DATA_ABERTURA ");
				}

        		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        	}
           
        	try{
        		q.setParameter("data1", sdf.format(new DateTime().minusYears(2).toDate()).toString());
        		q.setParameter("data2", sdf.format(new DateTime().toDate()).toString());
        	}catch(Exception e){
        		
        	}
        	
        	
        	if(cbTipos != null && cbTipos.getValue() != null){
        		//Lists.reverse(
        		List<Object[]> result = q.getResultList();
            	
        	       
            	
            	List<String> s = new ArrayList<>();     	
            	for(Object[] o:result)
            	{        	
            		if(!s.contains((String)o[2])){
            			s.add((String)o[2]);
            		}
            		
            		ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);       		        	        	    	
            		chart.getConfiguration().addSeries(serie);
            	}       

    				if(s.size() == 1){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString());
    				}else if(s.size() == 2){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString());
    				}else if(s.size() == 3){
    					chart.getConfiguration().getxAxis().setCategories(s.get(0).toString(),s.get(1).toString(),s.get(2).toString());
    				}
        	}else{
	        	List<String> mesesChave = new ArrayList<>();
	        
	        	
	        	mesesChave.add(sdf.format(new DateTime().minusYears(2).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().minusYears(1).toDate()).toString());
	        	mesesChave.add(sdf.format(new DateTime().toDate()).toString());
	        	
	        	List<Object[]> result = q.getResultList();
	
	        	
	        	ListSeries serie;
	        	
	        	
	        	HashMap<String,Number> hashALTERACAO_MATERIAL = new HashMap<>();
	        	HashMap<String,Number> hashCANCELAMENTO = new HashMap<>();
	        	HashMap<String,Number> hashENTREGA = new HashMap<>();
	        	HashMap<String,Number> hashINSTALACAO = new HashMap<>();
	        	HashMap<String,Number> hashMUDANCA_ENDERECO = new HashMap<>();        	
	        	HashMap<String,Number> hashREPARO = new HashMap<>();
	        	HashMap<String,Number> hashSERVICO = new HashMap<>();
	        	HashMap<String,Number> hashMIGRACAO = new HashMap<>();
	        	HashMap<String,Number> hashESTRUTURA = new HashMap<>();
	        	
	        	for(Object[] o:result){
	        		       				
	        		
	        		if(((String)o[0]).equals("MIGRACAO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashMIGRACAO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("ESTRUTURA") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashESTRUTURA.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		
	        		if(((String)o[0]).equals("ALTERACAO MATERIAL") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashALTERACAO_MATERIAL.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		
	        		if(((String)o[0]).equals("CANCELAMENTO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashCANCELAMENTO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("ENTREGA") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashENTREGA.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		        		
	        		if(((String)o[0]).equals("INSTALACAO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashINSTALACAO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		        		
	        		if(((String)o[0]).equals("MUDANCA ENDERECO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashMUDANCA_ENDERECO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("REPARO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashREPARO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
	        		
	        		if(((String)o[0]).equals("SERVICO") && (Number)o[1] != null){
	        			if((Number)o[1] != null){       				
	        				hashSERVICO.put((String)o[2], (Number)o[1]);        				
	        			}
	        		}
					 
				}	
	        	
	        	
	        	//ALTERACAO MATERIAL
	        	List<Number> hashALTERACAO_MATERIAL_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashALTERACAO_MATERIAL.get(mes) != null){        				
	        				hashALTERACAO_MATERIAL_COMPLETO.add(hashALTERACAO_MATERIAL.get(mes));        				
	        			}else{        				
	        				hashALTERACAO_MATERIAL_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ALTERACAO MATERIAL");
	        	for (Number number2 : hashALTERACAO_MATERIAL_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	
	        	//CANCELAMENTO
	        	List<Number> hashCANCELAMENTO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashCANCELAMENTO.get(mes) != null){        				
	        				hashCANCELAMENTO_COMPLETO.add(hashCANCELAMENTO.get(mes));        				
	        			}else{        				
	        				hashCANCELAMENTO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("CANCELAMENTO");
	        	for (Number number2 : hashCANCELAMENTO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//ENTREGA
	        	List<Number> hashENTREGA_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashENTREGA.get(mes) != null){        				
	        				hashENTREGA_COMPLETO.add(hashENTREGA.get(mes));        				
	        			}else{        				
	        				hashENTREGA_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ENTREGA");
	        	for (Number number2 : hashENTREGA_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//INSTALACAO
	        	List<Number> hashINSTALACAO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashINSTALACAO.get(mes) != null){        				
	        				hashINSTALACAO_COMPLETO.add(hashINSTALACAO.get(mes));        				
	        			}else{        				
	        				hashINSTALACAO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("INSTALACAO");
	        	for (Number number2 : hashINSTALACAO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//MUDANCA ENDERECO
	        	List<Number> hashMUDANCA_ENDERECO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashINSTALACAO.get(mes) != null){        				
	        				hashMUDANCA_ENDERECO_COMPLETO.add(hashMUDANCA_ENDERECO.get(mes));        				
	        			}else{        				
	        				hashMUDANCA_ENDERECO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("MUDANCA ENDERECO");
	        	for (Number number2 : hashMUDANCA_ENDERECO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//REPARO
	        	List<Number> hashREPARO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashREPARO.get(mes) != null){        				
	        				hashREPARO_COMPLETO.add(hashREPARO.get(mes));        				
	        			}else{        				
	        				hashREPARO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("REPARO");
	        	for (Number number2 : hashREPARO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//SERVICO
	        	List<Number> hashSERVICO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashSERVICO.get(mes) != null){        				
	        				hashSERVICO_COMPLETO.add(hashSERVICO.get(mes));        				
	        			}else{        				
	        				hashSERVICO_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("SERVICO");
	        	for (Number number2 : hashSERVICO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	
	        	//MIGRACAO
	        	List<Number> hashMIGRACAO_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashMIGRACAO.get(mes) != null){        				
	        				hashMIGRACAO_COMPLETO.add(hashMIGRACAO.get(mes));        				
	        			}else{        				
	        				hashMIGRACAO_COMPLETO.add(0);
	        			}        		
				}
	        	
	        	serie = new  ListSeries("MIGRACAO");
	        	for (Number number2 : hashMIGRACAO_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);
	        	
	        	//ESTRUTURA
	        	List<Number> hashESTRUTURA_COMPLETO = new ArrayList<>();
	        	for (String mes : mesesChave) {       		
	        			
	        			if(hashESTRUTURA.get(mes) != null){        				
	        				hashESTRUTURA_COMPLETO.add(hashMIGRACAO.get(mes));        				
	        			}else{        				
	        				hashESTRUTURA_COMPLETO.add(0);
	        			}        		
				}        	
	        	serie = new  ListSeries("ESTRUTURA");
	        	for (Number number2 : hashESTRUTURA_COMPLETO) {
	        		serie.addData(number2);				
				}
	        	chart.getConfiguration().addSeries(serie);

        	
        	}
		}
        	
				
        
        chart.addChartClickListener(new ChartClickListener() {
			
			@Override
			public void onClick(ChartClickEvent event) {
				event.getSource();
			}
		});
        
        
        
                
       
        
        
        return chart;
    }
}
