package com.digital.opuserp.view.home.apps.atendimentos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CrmAssuntosDAO;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
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

public class AtendimentosVisaoGeral extends CssLayout {
	
	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btRefresh;
    
    private Button btClose;
    private ComboBox cbPeriodicidade;
    
    public AtendimentosVisaoGeral() {
    	
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
						((VerticalLayout)cbSeletor.getParent().getParent()).replaceComponent(chart, buildChart(true));
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
    	
    	cbPeriodicidade.addItem("HOJE");
    	cbPeriodicidade.addItem("MÊS ATUAL");
    	cbPeriodicidade.addItem("MÊS ANTERIOR");
    	
    	cbPeriodicidade.select("MÊS ATUAL"); 
    	
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
						((VerticalLayout)cbPeriodicidade.getParent().getParent()).replaceComponent(chart, buildChart(true));
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
            	  PrefeDashDAO.remove(AtendimentosVisaoGeral.class.toString());
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
                Window winMaximize = new Window("Gráfico de Atendimentos");
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
                		addComponent(buildChart(true));
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
    		chart.setCaption("Gráfico de Atendimentos");    	
    		chart.getConfiguration().getLegend().setEnabled(false);
    	}
    	
    	
    	chart.getConfiguration().disableCredits();
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);       
        chart.getConfiguration().getxAxis().setMin(0);
        
        chart.getConfiguration().getxAxis().setAllowDecimals(true);
        
        if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("HOJE")){
        	chart.getConfiguration().getxAxis().setTitle("Hoje");        	
        }else if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("MÊS ATUAL")){
        	chart.getConfiguration().getxAxis().setTitle("Mês atual");        	
        }else if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("MÊS ANTERIOR")){
        	chart.getConfiguration().getxAxis().setTitle("Mês anterior");        	
        }    
        
        chart.setLocale(new Locale("pt", "BR"));     
        
        chart.setWidth("100%");

        if(!dataLabel){
        	chart.setHeight("97%");
        }else{
        	chart.setHeight("90%");
        }

        EntityManager em = ConnUtil.getEntity();
        
        Query q = null;
        
        
        if(cbSeletor.getValue().toString().equals("INDIVIDUAL")){
        	if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("MÊS ANTERIOR")){
	        	
	        	//Configura Categorias
				SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
				chart.getConfiguration().getxAxis().setCategories(OpusERP4UI.getUsuarioLogadoUI().getUsername());
	        	        	
				//Pega Vendedores
	        	List<String> resultOperadores = new ArrayList<>();
	        	resultOperadores.add(OpusERP4UI.getUsuarioLogadoUI().getUsername());
	        	 
	        	List<CrmAssunto> assuntos = CrmAssuntosDAO.getAssuntos("2");
	        	for (CrmAssunto assunto: assuntos) {
	        		List<DataSeriesItem> list_sdi = new ArrayList<>();
	        		for (String operador : resultOperadores) {
	        			List<Object[]> result =buildValuesMensal(operador, sdf.format(new DateTime().minusMonths(1).toDate()),assunto.getId().toString());
	        			
	        			if(result != null && result.size() > 0){
		        			for (Object[] crms: result) {
		        				Integer qtd = Integer.parseInt(crms[1].toString());
		        				String tipo = crms[0].toString();
		        						        					        				
		        				DataSeriesItem dsi = new DataSeriesItem(tipo, qtd);		        				
		        				list_sdi.add(dsi);	        				
		        			}
	        			}else{
	        				DataSeriesItem dsi = new DataSeriesItem(assunto.getNome(), 0);	        				
	        				list_sdi.add(dsi);	        				
	        			}   
	        		}
	        		
	        		DataSeries se = new DataSeries(list_sdi);	     
	        		//se.setConfiguration(new Configuration().set);
	        		
	        	    chart.getConfiguration().addSeries(se);
				}
	        	
	        }
        	
        	
        	if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("MÊS ATUAL")){
	        	
	        	//Configura Categorias
				SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
				chart.getConfiguration().getxAxis().setCategories(OpusERP4UI.getUsuarioLogadoUI().getUsername());
	        	
				//Pega Vendedores
	        	List<String> resultOperadores = new ArrayList<>();
	        	resultOperadores.add(OpusERP4UI.getUsuarioLogadoUI().getUsername());
	        	 
	        	List<CrmAssunto> assuntos = CrmAssuntosDAO.getAssuntos("2");
	        	for (CrmAssunto assunto: assuntos) {
	        		List<DataSeriesItem> list_sdi = new ArrayList<>();
	        		for (String operador : resultOperadores) {
	        			List<Object[]> result =buildValuesMensal(operador, sdf.format(new DateTime().toDate()),assunto.getId().toString());
	        			
	        			if(result != null && result.size() > 0){
		        			for (Object[] crms: result) {
		        				Integer qtd = Integer.parseInt(crms[1].toString());
		        				String tipo = crms[0].toString();
		        						        					        				
		        				DataSeriesItem dsi = new DataSeriesItem(tipo, qtd);
		        				list_sdi.add(dsi);	        				
		        			}
	        			}else{
	        				DataSeriesItem dsi = new DataSeriesItem(assunto.getNome(), 0);	        				
	        				list_sdi.add(dsi);	        				
	        			}   
	        		}
	        		
	        		DataSeries se = new DataSeries(list_sdi);	     
	        		
	        	    chart.getConfiguration().addSeries(se);
				}
	        	
	        }
        	
        	if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("HOJE")){
	        	
	        	//Configura Categorias
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				chart.getConfiguration().getxAxis().setCategories(OpusERP4UI.getUsuarioLogadoUI().getUsername());
	        	
				//Pega Vendedores
	        	List<String> resultOperadores = new ArrayList<>();
	        	resultOperadores.add(OpusERP4UI.getUsuarioLogadoUI().getUsername());
	        	 
	        	List<CrmAssunto> assuntos = CrmAssuntosDAO.getAssuntos("2");
	        	for (CrmAssunto assunto: assuntos) {
	        		List<DataSeriesItem> list_sdi = new ArrayList<>();
	        		for (String operador : resultOperadores) {
	        			List<Object[]> result =buildValuesDiario(operador, sdf.format(new DateTime().toDate()),assunto.getId().toString());
	        			
	        			if(result != null && result.size() > 0){
		        			for (Object[] crms: result) {
		        				Integer qtd = Integer.parseInt(crms[1].toString());
		        				String tipo = crms[0].toString();
		        						        					        				
		        				DataSeriesItem dsi = new DataSeriesItem(tipo, qtd);
		        				list_sdi.add(dsi);	        				
		        			}
	        			}else{
	        				DataSeriesItem dsi = new DataSeriesItem(assunto.getNome(), 0);	        				
	        				list_sdi.add(dsi);	        				
	        			}   
	        		}
	        		
	        		DataSeries se = new DataSeries(list_sdi);	     
	        		
	        	    chart.getConfiguration().addSeries(se);
				}
	        	
	        }
        }
        
        if(cbSeletor.getValue().toString().equals("COLETIVO")){
        	
        	if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("MÊS ANTERIOR")){
	        	
	        	//Configura Categorias
				SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
				chart.getConfiguration().getxAxis().setCategories("brunorebelo", "aldenis","rosy","andreza","eduardo","grazielly");
	        	        	
				//Pega Vendedores
	        	List<String> resultOperadores = new ArrayList<>();
	        	resultOperadores.add("brunorebelo");
	        	resultOperadores.add("aldenis");
	        	resultOperadores.add("rosy");
	        	resultOperadores.add("andreza");
	        	resultOperadores.add("eduardo");
	        	resultOperadores.add("grazielly");
	        	 
	        	List<CrmAssunto> assuntos = CrmAssuntosDAO.getAssuntos("2");
	        	for (CrmAssunto assunto: assuntos) {
	        		List<DataSeriesItem> list_sdi = new ArrayList<>();
	        		for (String operador : resultOperadores) {
	        			List<Object[]> result =buildValuesMensal(operador, sdf.format(new DateTime().minusMonths(1).toDate()),assunto.getId().toString());
	        			
	        			if(result != null && result.size() > 0){
		        			for (Object[] crms: result) {
		        				Integer qtd = Integer.parseInt(crms[1].toString());
		        				String tipo = crms[0].toString();
		        						        					        				
		        				DataSeriesItem dsi = new DataSeriesItem(tipo, qtd);		        				
		        				list_sdi.add(dsi);	        				
		        			}
	        			}else{
	        				DataSeriesItem dsi = new DataSeriesItem(assunto.getNome(), 0);	        				
	        				list_sdi.add(dsi);	        				
	        			}   
	        		}
	        		
	        		DataSeries se = new DataSeries(list_sdi);	     
	        		//se.setConfiguration(new Configuration().set);
	        		
	        	    chart.getConfiguration().addSeries(se);
				}
	        	
	        }
        	
        	
        	if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("MÊS ATUAL")){
	        	
	        	//Configura Categorias
				SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
				chart.getConfiguration().getxAxis().setCategories("brunorebelo", "aldenis","rosy","andreza","eduardo","grazielly");
	        	        	
				//Pega Vendedores
	        	List<String> resultOperadores = new ArrayList<>();
	        	resultOperadores.add("brunorebelo");
	        	resultOperadores.add("aldenis");
	        	resultOperadores.add("rosy");
	        	resultOperadores.add("andreza");
	        	resultOperadores.add("eduardo");
	        	resultOperadores.add("grazielly");
	        	 
	        	List<CrmAssunto> assuntos = CrmAssuntosDAO.getAssuntos("2");
	        	for (CrmAssunto assunto: assuntos) {
	        		List<DataSeriesItem> list_sdi = new ArrayList<>();
	        		for (String operador : resultOperadores) {
	        			List<Object[]> result =buildValuesMensal(operador, sdf.format(new DateTime().toDate()),assunto.getId().toString());
	        			
	        			if(result != null && result.size() > 0){
		        			for (Object[] crms: result) {
		        				Integer qtd = Integer.parseInt(crms[1].toString());
		        				String tipo = crms[0].toString();
		        						        					        				
		        				DataSeriesItem dsi = new DataSeriesItem(tipo, qtd);
		        				list_sdi.add(dsi);	        				
		        			}
	        			}else{
	        				DataSeriesItem dsi = new DataSeriesItem(assunto.getNome(), 0);	        				
	        				list_sdi.add(dsi);	        				
	        			}   
	        		}
	        		
	        		DataSeries se = new DataSeries(list_sdi);	     
	        		
	        	    chart.getConfiguration().addSeries(se);
				}
	        	
	        }
        	
        	if(cbPeriodicidade.getValue() != null && cbPeriodicidade.getValue().toString().equals("HOJE")){
	        	
	        	//Configura Categorias
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				chart.getConfiguration().getxAxis().setCategories("brunorebelo", "aldenis","rosy","andreza","eduardo","grazielly");
	        	        	
				//Pega Vendedores
	        	List<String> resultOperadores = new ArrayList<>();
	        	resultOperadores.add("brunorebelo");
	        	resultOperadores.add("aldenis");
	        	resultOperadores.add("rosy");
	        	resultOperadores.add("andreza");
	        	resultOperadores.add("eduardo");
	        	resultOperadores.add("grazielly");
	        	 
	        	List<CrmAssunto> assuntos = CrmAssuntosDAO.getAssuntos("2");
	        	for (CrmAssunto assunto: assuntos) {
	        		List<DataSeriesItem> list_sdi = new ArrayList<>();
	        		for (String operador : resultOperadores) {
	        			List<Object[]> result =buildValuesDiario(operador, sdf.format(new DateTime().toDate()),assunto.getId().toString());
	        			
	        			if(result != null && result.size() > 0){
		        			for (Object[] crms: result) {
		        				Integer qtd = Integer.parseInt(crms[1].toString());
		        				String tipo = crms[0].toString();
		        						        					        				
		        				DataSeriesItem dsi = new DataSeriesItem(tipo, qtd);
		        				list_sdi.add(dsi);	        				
		        			}
	        			}else{
	        				DataSeriesItem dsi = new DataSeriesItem(assunto.getNome(), 0);	        				
	        				list_sdi.add(dsi);	        				
	        			}   
	        		}
	        		
	        		DataSeries se = new DataSeries(list_sdi);	     
	        		
	        	    chart.getConfiguration().addSeries(se);
				}
	        	
	        }
        }
        
        return chart;
    }
    
    private List<Object[]> buildValuesMensal(String obVendedor, String data, String assunto){
    	EntityManager em = ConnUtil.getEntity();
    	Query q = em.createNativeQuery("SELECT "
    			+ "ca.NOME AS TIPO, "
    			+ "count(ca.NOME) AS QTD "
				+ "FROM  crm c, crm_assuntos ca where ca.ID = c.CRM_ASSUNTOS_ID AND c.CRM_ASSUNTOS_ID =:assunto AND DATE_FORMAT( DATA_EFETUADO,  '%m/%Y' )=:ano AND  "
				+ "c.STATUS = 'EFETUADO' AND  c.ORIGEM = 'OPUS' AND "
				+ "c.OPERADOR = :vendedor AND c.EMPRESA_ID =:codEmpresa AND c.OPERADOR NOT LIKE '' GROUP BY ca.NOME");
		
    	q.setParameter("ano", data);
    	q.setParameter("vendedor", obVendedor);
		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("assunto", assunto);
	        
        List<Object[]> lis = q.getResultList();
        return lis;
    }
    private List<Object[]> buildValuesDiario(String obVendedor, String data, String assunto){
    	EntityManager em = ConnUtil.getEntity();
    	Query q = em.createNativeQuery("SELECT "
    			+ "ca.NOME AS TIPO, "
    			+ "count(ca.NOME) AS QTD "
				+ "FROM  crm c, crm_assuntos ca where ca.ID = c.CRM_ASSUNTOS_ID AND c.CRM_ASSUNTOS_ID =:assunto AND DATE_FORMAT( DATA_EFETUADO,  '%m/%Y' )=:ano AND  "
				+ "c.STATUS = 'EFETUADO' AND  c.ORIGEM = 'OPUS' AND "
				+ "c.OPERADOR = :vendedor AND c.EMPRESA_ID =:codEmpresa AND c.OPERADOR NOT LIKE '' GROUP BY ca.NOME");
		
    	q.setParameter("ano", data);
    	q.setParameter("vendedor", obVendedor);
		q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("assunto", assunto);
	        
        List<Object[]> lis = q.getResultList();
        return lis;
    }
    
}
