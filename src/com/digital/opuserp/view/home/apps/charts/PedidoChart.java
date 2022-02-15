package com.digital.opuserp.view.home.apps.charts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
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

public class PedidoChart extends CssLayout {
	
	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btRefresh;
    
    private Button btClose;
    private ComboBox cbPeriodicidade;
    
    public PedidoChart() {
    	
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

        		 addComponent(buildCbSeletor(false));
        		 addComponent(buildCbPeriodicidade());
        	 }
         });
         addComponent(buildChart(true)); 
    }
    
    private ComboBox cbSeletor;
    private ComboBox buildCbSeletor(boolean setMarginLeft){
    	cbSeletor = new ComboBox();
    	if(setMarginLeft){
    		cbSeletor.addStyleName("margin-left-10");
    	}
    	cbSeletor.setNullSelectionAllowed(false);
    	cbSeletor.setTextInputAllowed(false);
    	cbSeletor.addItem("COLETIVO");
    	cbSeletor.addItem("INDIVIDUAL");
    	cbSeletor.select("COLETIVO");
    	cbSeletor.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbPeriodicidade.getParent() instanceof VerticalLayout){
					((VerticalLayout)cbSeletor.getParent().getParent()).replaceComponent(chart, buildChart(true));
					((VerticalLayout)cbSeletor.getParent().getParent()).setExpandRatio(chart, 1f);
				}
				if(cbPeriodicidade.getParent() instanceof HorizontalLayout){
					if(cbSeletor.getParent().getParent() instanceof CssLayout){
						((CssLayout)cbSeletor.getParent().getParent()).replaceComponent(chart, buildChart(true));
					}
					
					if(cbSeletor.getParent().getParent() instanceof VerticalLayout){
						((VerticalLayout)cbSeletor.getParent().getParent()).replaceComponent(chart, buildChart(false));
					}
				}
			}
		});
    	
    	return cbSeletor;
    }
    
    private ComboBox buildCbPeriodicidade(){
    	cbPeriodicidade = new ComboBox(null);
    	
    	cbPeriodicidade.setNullSelectionAllowed(false);
    	cbPeriodicidade.setTextInputAllowed(false); 
    	//cbPeriodicidade.setRequired(true);
    	
    	cbPeriodicidade.addItem("DIÁRIO");
    	cbPeriodicidade.addItem("MENSAL");
    	cbPeriodicidade.addItem("ANUAL");
    	
    	cbPeriodicidade.select("MENSAL"); 
    	
    	cbPeriodicidade.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(cbPeriodicidade.getParent() instanceof VerticalLayout){
					((VerticalLayout)cbPeriodicidade.getParent().getParent()).replaceComponent(chart, buildChart(true));
					((VerticalLayout)cbPeriodicidade.getParent().getParent()).setExpandRatio(chart, 1f);						
					
				}
				
				if(cbPeriodicidade.getParent() instanceof HorizontalLayout){
					if(cbPeriodicidade.getParent().getParent() instanceof CssLayout){
						((CssLayout)cbPeriodicidade.getParent().getParent()).replaceComponent(chart, buildChart(true));
					}
					
					if(cbPeriodicidade.getParent().getParent() instanceof VerticalLayout){
						((VerticalLayout)cbPeriodicidade.getParent().getParent()).replaceComponent(chart, buildChart(false));
					}
				}
			}
		});    	
    	return cbPeriodicidade;
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
            	  PrefeDashDAO.remove(PedidoChart.class.toString());
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
                 ((CssLayout)btRefresh.getParent()).replaceComponent(chart, buildChart(true));
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
                Window winMaximize = new Window("Gráfico Evolução de Vendas");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");
                winMaximize.setContent(new VerticalLayout(){
                	{
                		setSizeFull();
                		setMargin(true); 
                		setSpacing(true);
                		addComponent(new HorizontalLayout(){
                       	 {                       		 
                       		 addComponent(buildCbSeletor(true));
                       		 addComponent(buildCbPeriodicidade());
                       	 }
                        });
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

    private Chart buildChart(boolean dataLabel){
    	chart = new Chart();
    	chart.setLocale(new Locale("pt", "BR"));
    	chart.getConfiguration().setExporting(true);
    	if(dataLabel){
    		chart.setCaption("Gráfico Evolução de Vendas");    	
    		chart.getConfiguration().getLegend().setEnabled(false);
    	}
    	
    	
    	chart.getConfiguration().disableCredits();
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);       
        chart.getConfiguration().getxAxis().setMin(0);
        
        chart.getConfiguration().getxAxis().setAllowDecimals(true);
        
        if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("DIÁRIO")){
        	chart.getConfiguration().getxAxis().setTitle("Últimos 30 dias");        	
        }else if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("MENSAL")){
        	chart.getConfiguration().getxAxis().setTitle("Últimos 12 meses");        	
        }else if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("ANUAL")){
        	chart.getConfiguration().getxAxis().setTitle("Últimos 5 anos");        	
        }        
        
        
        chart.getConfiguration().getTooltip().setValuePrefix("R$ ");
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
        		+ "return '<strong>'+valor_x.replace(' ','')+'</strong>'+': R$ '+retorno;"
        		+ ""
        		+ "}");
        
        chart.getConfiguration().getyAxis().setTitle("Real (R$)");
       
        chart.setLocale(new Locale("pt", "BR"));     
        
        chart.setWidth("100%");

        if(!dataLabel){
        	chart.setHeight("97%");
        }else{
        	chart.setHeight("90%");
        }

        EntityManager em = ConnUtil.getEntity();
        
        Query q = null;
        
        
        if(cbSeletor.getValue().toString().equals("COLETIVO")){
        	chart.getConfiguration().getxAxis().setCategories("");
	        if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("DIÁRIO")){
	        	q = em.createNativeQuery("SELECT DATE_FORMAT( DATA_PV,  '%d/%m/%Y' ) AS DATA, SUM( valor) AS QTD FROM  ecf_pre_venda_cabecalho where `SITUACAO` = 'F' AND TIPO = 'PEDIDO' AND EMPRESA_ID = :codEmpresa AND NATUREZA_OPERACAO = :codNatureza GROUP BY DATE_FORMAT( DATA_PV,  '%d/%m/%Y' ) ORDER BY `DATA_PV` DESC LIMIT 0,30");
	        }else if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("MENSAL")){
	        	q = em.createNativeQuery("SELECT DATE_FORMAT( DATA_PV,  '%m/%Y' ) AS DATA, SUM( valor) AS QTD FROM  ecf_pre_venda_cabecalho where `SITUACAO` = 'F' AND TIPO = 'PEDIDO' AND EMPRESA_ID = :codEmpresa AND NATUREZA_OPERACAO = :codNatureza GROUP BY DATE_FORMAT( DATA_PV,  '%m/%Y' ) ORDER BY `DATA_PV` DESC LIMIT 0,12");
	        }else if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("ANUAL")){
	        	q = em.createNativeQuery("SELECT DATE_FORMAT( DATA_PV,  '%Y' ) AS DATA, SUM( valor) AS QTD FROM  ecf_pre_venda_cabecalho where `SITUACAO` = 'F' AND TIPO = 'PEDIDO' AND EMPRESA_ID = :codEmpresa AND NATUREZA_OPERACAO = :codNatureza GROUP BY DATE_FORMAT( DATA_PV,  '%Y' ) ORDER BY `DATA_PV` DESC LIMIT 0,10");
	        }
	        
	        if(q != null){
		        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
		        
		        if(OpusERP4UI.getEmpresa().getId().equals(1)){
		        	q.setParameter("codNatureza", 3);
		        }else if(OpusERP4UI.getEmpresa().getId().equals(5)){
		    		q.setParameter("codNatureza", 22);
		    	}else if(OpusERP4UI.getEmpresa().getId().equals(6)){
		    		q.setParameter("codNatureza", 33);
		    	}else{
		        	q.setParameter("codNatureza", 9);
		        }
	                
		        List<Object[]> result = Lists.reverse(q.getResultList());
		        List<Series> series = new ArrayList<Series>();
		        
		        Labels dataLabels = new Labels();
		        //dataLabels.setFormatter("Moeda.formatar(this.y,2,',','.');");
		        
		        dataLabels.setEnabled(true);
		        dataLabels.setRotation(-90);
		        dataLabels.setColor(new SolidColor(255, 255, 255));
		        dataLabels.setAlign(HorizontalAlign.CENTER);
		        dataLabels.setX(5);
		        dataLabels.setY(50);        
		        dataLabels.setFormatter("function(){"
		        		+ "var valor=this.y;"
		        		+ "var casas=2;"
		        		+ "var separdor_decimal = ',';"
		        		+ "var separador_milhar = '.';"
		        		
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
		        Style style = new Style();
		        
		        style.setFontSize("10px");
		        style.setFontFamily("Verdana, sans-serif");
		        dataLabels.setStyle(style);
		        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
		        plotOptionsColumn.setDataLabels(dataLabels);
		   

		        for(Object[] o:result)
		        {
		        	
		        	ListSeries serie = new ListSeries((String)o[0],(Number)o[1]);
		        	if(!dataLabel){
		        		serie.setPlotOptions(plotOptionsColumn);
		        	}
		        	series.add(serie);       	   	
		        }        
		       
		        chart.getConfiguration().setSeries(series);
	        
	        }
        }
        
        if(cbSeletor.getValue().toString().equals("INDIVIDUAL")){
        	
        	    Labels dataLabels = new Labels();
		       
		        dataLabels.setEnabled(true);
		        dataLabels.setRotation(-90);
		        dataLabels.setAlign(HorizontalAlign.CENTER);
		        //dataLabels.setX(5);
		        dataLabels.setY(-30);        
		        dataLabels.setFormatter("function(){"
		        		+ "var valor=this.y;"
		        		+ "var casas=2;"
		        		+ "var separdor_decimal = ',';"
		        		+ "var separador_milhar = '.';"
		        		
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
		        Style style = new Style();
		        
		        style.setFontSize("7px");
		        style.setFontFamily("Verdana, sans-serif");
		        dataLabels.setStyle(style);
		        final PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
		        plotOptionsColumn.setDataLabels(dataLabels);
        	
        	
        	
        	
        	if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("DIÁRIO")){
	        	//q = em.createNativeQuery("SELECT DATE_FORMAT( DATA_PV,  '%d/%m/%Y' ) AS DATA, SUM( valor) AS QTD FROM  ecf_pre_venda_cabecalho where `SITUACAO` = 'F' AND TIPO = 'PEDIDO' AND EMPRESA_ID = :codEmpresa AND NATUREZA_OPERACAO = :codNatureza GROUP BY DATE_FORMAT( DATA_PV,  '%d/%m/%Y' ) ORDER BY `DATA_PV` DESC LIMIT 0,30");
        		
        		//Configura Categorias
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				
				Labels labels = new Labels();
		        labels.setRotation(-45);
		        labels.setAlign(HorizontalAlign.RIGHT);
		       
				
				chart.getConfiguration().getxAxis().setCategories(
						sdf.format(new DateTime().minusDays(31).toDate()).toString(),
						sdf.format(new DateTime().minusDays(30).toDate()).toString(),
						sdf.format(new DateTime().minusDays(29).toDate()).toString(),
						sdf.format(new DateTime().minusDays(28).toDate()).toString(),
						sdf.format(new DateTime().minusDays(27).toDate()).toString(),
						sdf.format(new DateTime().minusDays(26).toDate()).toString(),
						sdf.format(new DateTime().minusDays(25).toDate()).toString(),
						sdf.format(new DateTime().minusDays(24).toDate()).toString(),
						sdf.format(new DateTime().minusDays(23).toDate()).toString(),
						sdf.format(new DateTime().minusDays(22).toDate()).toString(),
						sdf.format(new DateTime().minusDays(21).toDate()).toString(),
						sdf.format(new DateTime().minusDays(20).toDate()).toString(),
						sdf.format(new DateTime().minusDays(19).toDate()).toString(),
						sdf.format(new DateTime().minusDays(18).toDate()).toString(),
						sdf.format(new DateTime().minusDays(17).toDate()).toString(),
						sdf.format(new DateTime().minusDays(16).toDate()).toString(),
						sdf.format(new DateTime().minusDays(15).toDate()).toString(),
						sdf.format(new DateTime().minusDays(14).toDate()).toString(),
						sdf.format(new DateTime().minusDays(13).toDate()).toString(),
						sdf.format(new DateTime().minusDays(12).toDate()).toString(),
						sdf.format(new DateTime().minusDays(11).toDate()).toString(),
						sdf.format(new DateTime().minusDays(10).toDate()).toString(),
						sdf.format(new DateTime().minusDays(9).toDate()).toString(),
						sdf.format(new DateTime().minusDays(8).toDate()).toString(),
						sdf.format(new DateTime().minusDays(7).toDate()).toString(),
						sdf.format(new DateTime().minusDays(6).toDate()).toString(),
						sdf.format(new DateTime().minusDays(5).toDate()).toString(),
						sdf.format(new DateTime().minusDays(4).toDate()).toString(),
						sdf.format(new DateTime().minusDays(3).toDate()).toString(),
						sdf.format(new DateTime().minusDays(2).toDate()).toString(),
						sdf.format(new DateTime().minusDays(1).toDate()).toString(),
						sdf.format(new DateTime().toDate()).toString());
	        	        	
				//Pega Vendedores
	        	List<String> resultVendedores = getVendedoresDiario();
	        	Map<String, List<Object[]>> mapResultados = new HashMap<>();
	        	
	        	//Adiciona Series
	        	for (String obVendedor : resultVendedores) {
	        		ListSeries serie = new ListSeries(obVendedor);

	     	        List<Object[]> result =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(31).toDate()));
	     	        if(result != null && result.size() > 0){
	     	        	serie.addData((Number)result.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result2 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(30).toDate()));
	     	        if(result2 != null && result2.size() > 0){
	     	        	serie.addData((Number)result2.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result3 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(29).toDate()));
	     	        if(result3 != null && result3.size() > 0){
	     	        	serie.addData((Number)result3.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        List<Object[]> result4 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(28).toDate()));
	     	        if(result4 != null && result4.size() > 0){
	     	        	serie.addData((Number)result4.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        
	     	        List<Object[]> result5 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(27).toDate()));
	     	        if(result5 != null && result5.size() > 0){
	     	        	serie.addData((Number)result5.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result6 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(26).toDate()));
	     	        if(result6 != null && result6.size() > 0){
	     	        	serie.addData((Number)result6.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result7 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(25).toDate()));
	     	        if(result7 != null && result7.size() > 0){
	     	        	serie.addData((Number)result7.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result8 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(24).toDate()));
	     	        if(result8 != null && result8.size() > 0){
	     	        	serie.addData((Number)result8.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result9 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(23).toDate()));
	     	        if(result9 != null && result9.size() > 0){
	     	        	serie.addData((Number)result9.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result10 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(22).toDate()));
	     	        if(result10 != null && result10.size() > 0){
	     	        	serie.addData((Number)result10.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result11 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(21).toDate()));
	     	        if(result11 != null && result11.size() > 0){
	     	        	serie.addData((Number)result11.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result12 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(20).toDate()));
	     	        if(result12 != null && result12.size() > 0){
	     	        	serie.addData((Number)result12.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result13 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(19).toDate()));
	     	        if(result13 != null && result13.size() > 0){
	     	        	serie.addData((Number)result13.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result14 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(18).toDate()));
	     	        if(result14 != null && result14.size() > 0){
	     	        	serie.addData((Number)result14.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result15 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(17).toDate()));
	     	        if(result15 != null && result15.size() > 0){
	     	        	serie.addData((Number)result15.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result16 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(16).toDate()));
	     	        if(result16 != null && result16.size() > 0){
	     	        	serie.addData((Number)result16.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result17 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(15).toDate()));
	     	        if(result17 != null && result17.size() > 0){
	     	        	serie.addData((Number)result17.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result18 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(14).toDate()));
	     	        if(result18 != null && result18.size() > 0){
	     	        	serie.addData((Number)result18.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result19 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(13).toDate()));
	     	        if(result19 != null && result19.size() > 0){
	     	        	serie.addData((Number)result19.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result20 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(12).toDate()));
	     	        if(result20 != null && result20.size() > 0){
	     	        	serie.addData((Number)result20.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result21 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(11).toDate()));
	     	        if(result21 != null && result21.size() > 0){
	     	        	serie.addData((Number)result21.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result22 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(10).toDate()));
	     	        if(result22 != null && result22.size() > 0){
	     	        	serie.addData((Number)result22.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result23 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(9).toDate()));
	     	        if(result23 != null && result23.size() > 0){
	     	        	serie.addData((Number)result23.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result24 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(8).toDate()));
	     	        if(result24 != null && result24.size() > 0){
	     	        	serie.addData((Number)result24.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result25 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(7).toDate()));
	     	        if(result25 != null && result25.size() > 0){
	     	        	serie.addData((Number)result25.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result26 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(6).toDate()));
	     	        if(result26 != null && result26.size() > 0){
	     	        	serie.addData((Number)result26.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result27 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(5).toDate()));
	     	        if(result27 != null && result27.size() > 0){
	     	        	serie.addData((Number)result27.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result28 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(4).toDate()));
	     	        if(result28 != null && result28.size() > 0){
	     	        	serie.addData((Number)result28.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result29 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(3).toDate()));
	     	        if(result29 != null && result29.size() > 0){
	     	        	serie.addData((Number)result29.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result30 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(2).toDate()));
	     	        if(result30 != null && result30.size() > 0){
	     	        	serie.addData((Number)result30.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result31 =buildValuesDiario(obVendedor, sdf.format(new DateTime().minusDays(1).toDate()));
	     	        if(result31 != null && result31.size() > 0){
	     	        	serie.addData((Number)result31.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result32 =buildValuesDiario(obVendedor, sdf.format(new DateTime().toDate()));
	     	        if(result32 != null && result32.size() > 0){
	     	        	serie.addData((Number)result32.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        	
	     	        }
	     	        
	     	        
	     	        serie.setPlotOptions(plotOptionsColumn);
	        		chart.getConfiguration().addSeries(serie);
	        		 chart.getConfiguration().getxAxis().setLabels(labels); 
	        	}
	        }else if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("MENSAL")){
	        	
	        	
	        	//Configura Categorias
				SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
				chart.getConfiguration().getxAxis().setCategories(sdf.format(new DateTime().minusMonths(13).toDate()).toString(),sdf.format(new DateTime().minusMonths(12).toDate()).toString(),sdf.format(new DateTime().minusMonths(11).toDate()).toString(),sdf.format(new DateTime().minusMonths(10).toDate()).toString(),sdf.format(new DateTime().minusMonths(9).toDate()).toString(),sdf.format(new DateTime().minusMonths(8).toDate()).toString(),sdf.format(new DateTime().minusMonths(7).toDate()).toString(),sdf.format(new DateTime().minusMonths(6).toDate()).toString(),sdf.format(new DateTime().minusMonths(5).toDate()).toString(),sdf.format(new DateTime().minusMonths(4).toDate()).toString(),sdf.format(new DateTime().minusMonths(3).toDate()).toString(),sdf.format(new DateTime().minusMonths(2).toDate()).toString(),sdf.format(new DateTime().minusMonths(1).toDate()).toString(),sdf.format(new DateTime().toDate()).toString());
	        	        	
				//Pega Vendedores
	        	List<String> resultVendedores = getVendedoresMensal();
	        	Map<String, List<Object[]>> mapResultados = new HashMap<>();
	        	
	        	//Adiciona Series
	        	for (String obVendedor : resultVendedores) {
	        		ListSeries serie = new ListSeries(obVendedor);

	     	        List<Object[]> result =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(13).toDate()));
	     	        if(result != null && result.size() > 0){
	     	        	serie.addData((Number)result.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result2 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(12).toDate()));
	     	        if(result2 != null && result2.size() > 0){
	     	        	serie.addData((Number)result2.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result3 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(11).toDate()));
	     	        if(result3 != null && result3.size() > 0){
	     	        	serie.addData((Number)result3.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        List<Object[]> result4 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(10).toDate()));
	     	        if(result4 != null && result4.size() > 0){
	     	        	serie.addData((Number)result4.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        
	     	        List<Object[]> result5 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(9).toDate()));
	     	        if(result5 != null && result5.size() > 0){
	     	        	serie.addData((Number)result5.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result6 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(8).toDate()));
	     	        if(result6 != null && result6.size() > 0){
	     	        	serie.addData((Number)result6.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result7 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(7).toDate()));
	     	        if(result7 != null && result7.size() > 0){
	     	        	serie.addData((Number)result7.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result8 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(6).toDate()));
	     	        if(result8 != null && result8.size() > 0){
	     	        	serie.addData((Number)result8.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result9 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(5).toDate()));
	     	        if(result9 != null && result9.size() > 0){
	     	        	serie.addData((Number)result9.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result10 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(4).toDate()));
	     	        if(result10 != null && result10.size() > 0){
	     	        	serie.addData((Number)result10.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result11 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(3).toDate()));
	     	        if(result11 != null && result11.size() > 0){
	     	        	serie.addData((Number)result11.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result12 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(2).toDate()));
	     	        if(result12 != null && result12.size() > 0){
	     	        	serie.addData((Number)result12.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result13 =buildValuesMensal(obVendedor, sdf.format(new DateTime().minusMonths(1).toDate()));
	     	        if(result13 != null && result13.size() > 0){
	     	        	serie.addData((Number)result13.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       List<Object[]> result14 =buildValuesMensal(obVendedor, sdf.format(new DateTime().toDate()));
	     	        if(result14 != null && result14.size() > 0){
	     	        	serie.addData((Number)result14.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	       serie.setPlotOptions(plotOptionsColumn);
	        	   chart.getConfiguration().addSeries(serie);
	        	}
	        }else if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("ANUAL")){
	        		        	
	        	//Configura Categorias
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
				chart.getConfiguration().getxAxis().setCategories(sdf.format(new DateTime().minusYears(2).toDate()).toString(),sdf.format(new DateTime().minusYears(1).toDate()).toString(),sdf.format(new DateTime().toDate()).toString());
	        	        	
				//Pega Vendedores
	        	List<String> resultVendedores = getVendedoresAnual();
	        	Map<String, List<Object[]>> mapResultados = new HashMap<>();
	        	
	        	//Adiciona Series
	        	for (String obVendedor : resultVendedores) {
	        		ListSeries serie = new ListSeries(obVendedor);

	     	        List<Object[]> result =buildValuesAnual(obVendedor, sdf.format(new DateTime().minusYears(2).toDate()));
	     	        if(result != null && result.size() > 0){
	     	        	serie.addData((Number)result.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result2 =buildValuesAnual(obVendedor, sdf.format(new DateTime().minusYears(1).toDate()));
	     	        if(result2 != null && result2.size() > 0){
	     	        	serie.addData((Number)result2.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }
	     	        
	     	        List<Object[]> result3 =buildValuesAnual(obVendedor, sdf.format(new DateTime().toDate()));
	     	        if(result3 != null && result3.size() > 0){
	     	        	serie.addData((Number)result3.get(0)[1]);
	     	        }else{
	     	        	serie.addData(0);
	     	        }     	        
	     	        
	     	       serie.setPlotOptions(plotOptionsColumn);
	        		chart.getConfiguration().addSeries(serie);    	        

	        	}        	
	        }
        	
        	
        }
        
        
        
        return chart;
    }
    
    
    private List<Object[]> buildValuesDiario(String obVendedor, String data){
    	EntityManager em = ConnUtil.getEntity();
    	Query q = em.createNativeQuery("SELECT DATE_FORMAT( DATA_PV,  '%d/%m/%Y' ) AS DATA, SUM( valor) AS QTD "
				+ "FROM  ecf_pre_venda_cabecalho where DATE_FORMAT( DATA_PV,  '%d/%m/%Y' )=:dia AND  VENDEDOR = :vendedor AND `SITUACAO` = 'F' AND TIPO = 'PEDIDO' AND EMPRESA_ID = :codEmpresa AND NATUREZA_OPERACAO = :codNatureza "
				+ "GROUP BY DATE_FORMAT( DATA_PV,  '%d/%m/%Y' )");
		
		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("vendedor", obVendedor);
		q.setParameter("dia", data);
	        
        if(OpusERP4UI.getEmpresa().getId().equals(1)){
        	q.setParameter("codNatureza", 3);
        }else if(OpusERP4UI.getEmpresa().getId().equals(5)){
    		q.setParameter("codNatureza", 22);
    	}else if(OpusERP4UI.getEmpresa().getId().equals(6)){
    		q.setParameter("codNatureza", 33);
    	}else{
        	q.setParameter("codNatureza", 9);
        }
        
        List<Object[]> lis = q.getResultList();
        return lis;
    }
    
    private List<Object[]> buildValuesMensal(String obVendedor, String data){
    	EntityManager em = ConnUtil.getEntity();
    	Query q = em.createNativeQuery("SELECT DATE_FORMAT( DATA_PV,  '%m/%Y' ) AS DATA, SUM( valor) AS QTD "
				+ "FROM  ecf_pre_venda_cabecalho where DATE_FORMAT( DATA_PV,  '%m/%Y' )=:ano AND  VENDEDOR = :vendedor AND `SITUACAO` = 'F' AND TIPO = 'PEDIDO' AND EMPRESA_ID = :codEmpresa AND NATUREZA_OPERACAO = :codNatureza "
				+ "GROUP BY DATE_FORMAT( DATA_PV,  '%m/%Y' )");
		
		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("vendedor", obVendedor);
		q.setParameter("ano", data);
	        
        if(OpusERP4UI.getEmpresa().getId().equals(1)){
        	q.setParameter("codNatureza", 3);
        }else if(OpusERP4UI.getEmpresa().getId().equals(5)){
    		q.setParameter("codNatureza", 22);
    	}else if(OpusERP4UI.getEmpresa().getId().equals(6)){
    		q.setParameter("codNatureza", 33);
    	}else{
        	q.setParameter("codNatureza", 9);
        }
        
        List<Object[]> lis = q.getResultList();
        return lis;
    }
    private List<Object[]> buildValuesAnual(String obVendedor, String data){
    	EntityManager em = ConnUtil.getEntity();
    	Query q = em.createNativeQuery("SELECT DATE_FORMAT( DATA_PV,  '%Y' ) AS DATA, SUM( valor) AS QTD "
				+ "FROM  ecf_pre_venda_cabecalho where DATE_FORMAT( DATA_PV,  '%Y' )=:ano AND  VENDEDOR = :vendedor AND `SITUACAO` = 'F' AND TIPO = 'PEDIDO' AND EMPRESA_ID = :codEmpresa AND NATUREZA_OPERACAO = :codNatureza "
				+ "GROUP BY DATE_FORMAT( DATA_PV,  '%Y' )");
		
		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("vendedor", obVendedor);
		q.setParameter("ano", data);
	        
        if(OpusERP4UI.getEmpresa().getId().equals(1)){
        	q.setParameter("codNatureza", 3);
        }else if(OpusERP4UI.getEmpresa().getId().equals(5)){
    		q.setParameter("codNatureza", 22);
    	}else if(OpusERP4UI.getEmpresa().getId().equals(6)){
    		q.setParameter("codNatureza", 33);
    	}else{
        	q.setParameter("codNatureza", 9);
        }
        
        List<Object[]> lis = q.getResultList();
        return lis;
    }
    
    private List<String> getVendedoresDiario(){
    	EntityManager em = ConnUtil.getEntity();
    	Query qVendedores = em.createNativeQuery("SELECT VENDEDOR FROM  ecf_pre_venda_cabecalho where `SITUACAO` = 'F' AND TIPO = 'PEDIDO' AND "
    			+ "EMPRESA_ID = :codEmpresa AND NATUREZA_OPERACAO = :codNatureza AND DATA_PV > :dataInicio GROUP BY VENDEDOR ORDER BY VENDEDOR DESC");
    	qVendedores.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
    	
    	
    	qVendedores.setParameter("dataInicio", new DateTime().minusMonths(1).toDate());
    	
    	
    	if(OpusERP4UI.getEmpresa().getId().equals(1)){
    		qVendedores.setParameter("codNatureza", 3);
    	}else if(OpusERP4UI.getEmpresa().getId().equals(5)){
    		qVendedores.setParameter("codNatureza", 22);
    	}else if(OpusERP4UI.getEmpresa().getId().equals(6)){
    		qVendedores.setParameter("codNatureza", 33);
    	}else{
    		qVendedores.setParameter("codNatureza", 9);
    	}
    	
    	return qVendedores.getResultList();
    }
    
    private List<String> getVendedoresMensal(){
    	EntityManager em = ConnUtil.getEntity();
    	Query qVendedores = em.createNativeQuery("SELECT VENDEDOR FROM  ecf_pre_venda_cabecalho where `SITUACAO` = 'F' AND TIPO = 'PEDIDO' AND "
    			+ "EMPRESA_ID = :codEmpresa AND NATUREZA_OPERACAO = :codNatureza AND DATA_PV > :dataInicio GROUP BY VENDEDOR ORDER BY VENDEDOR DESC");
    	qVendedores.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
    	
    	
    	qVendedores.setParameter("dataInicio", new DateTime().minusYears(1).toDate());
    	
    	
    	if(OpusERP4UI.getEmpresa().getId().equals(1)){
    		qVendedores.setParameter("codNatureza", 3);
    	}else if(OpusERP4UI.getEmpresa().getId().equals(5)){
    		qVendedores.setParameter("codNatureza", 22);
    	}else if(OpusERP4UI.getEmpresa().getId().equals(6)){
    		qVendedores.setParameter("codNatureza", 33);
    	}else{
    		qVendedores.setParameter("codNatureza", 9);
    	}
    	
    	return qVendedores.getResultList();
    }
    
    private List<String> getVendedoresAnual(){
    	EntityManager em = ConnUtil.getEntity();
    	Query qVendedores = em.createNativeQuery("SELECT VENDEDOR FROM  ecf_pre_venda_cabecalho where `SITUACAO` = 'F' AND TIPO = 'PEDIDO' AND "
    			+ "EMPRESA_ID = :codEmpresa AND NATUREZA_OPERACAO = :codNatureza GROUP BY VENDEDOR ORDER BY VENDEDOR DESC");
    	qVendedores.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
    	
    	if(OpusERP4UI.getEmpresa().getId().equals(1)){
    		qVendedores.setParameter("codNatureza", 3);
    	}else if(OpusERP4UI.getEmpresa().getId().equals(5)){
    		qVendedores.setParameter("codNatureza", 22);
    	}else if(OpusERP4UI.getEmpresa().getId().equals(6)){
    		qVendedores.setParameter("codNatureza", 33);
    	}else{
    		qVendedores.setParameter("codNatureza", 9);
    	}
    	
    	return qVendedores.getResultList();
    }

}
