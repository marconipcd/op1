package com.digital.opuserp.view.home.apps.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.Perguntas;
import com.digital.opuserp.util.ConnUtil;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.FontWeight;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.addon.charts.themes.VaadinTheme;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class PesquisaSatisfacaoChart extends CssLayout {
	
	private Chart chart;
    //private Button btConfigure;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;    
    
    private ComboBox cbAssunto;
    private ComboBox cbPerguntas;
    
    //private ComboBox cbPeriodicidade;
    List<Object[]> result1=null;
    
    public PesquisaSatisfacaoChart() {
    	
    	addStyleName("layout-panel");
        setSizeFull();
        
        buildLayout();
    }
    
    VerticalLayout vlRoot = new VerticalLayout();
    private void buildLayout(){
    	removeAllComponents();
    	
    	addComponent(buildBtrefresh());
        addComponent(buildBtMaximize());
        addComponent(buildBtClose());
        
        vlRoot.setSizeFull();
        addComponent(vlRoot); 
        
        vlRoot.addComponent(new VerticalLayout(){
        	{
        		//setMargin(true);
        		setStyleName("v-margin-left-10");
        		
        		//addComponent(buildCbPeriodicidade());
            	addComponent(buildcbAssuntos(false));
            	addComponent(buildcbPerguntas(false));
        	}
        });
        
        
        vlRoot.addComponent(buildChart(true));
        vlRoot.addComponent(buildNotaMedia());
        
        vlRoot.setExpandRatio(chart, 1f);
    }
    
    VerticalLayout vlNotaMedia;
    private VerticalLayout buildNotaMedia(){
    	
    	vlNotaMedia = new VerticalLayout(){
    		{
    			
//    			
//    			Integer qtdRegular = 0;
//    			Integer qtdBom = 0;
//    			Integer qtdExcelente = 0;
//    			
//    		    for(Object[] o:result1)
//		        {	     
//		        	String tipo = (String) o[1];
//		        	
//		        	if(tipo.equals("03-Regular")){
//		        		qtdRegular =Integer.parseInt(o[2].toString());
//		        	}
//		        	
//		        	if(tipo.equals("04-Bom")){
//		        		qtdBom =Integer.parseInt(o[2].toString());
//		        	}
//		        	
//		        	if(tipo.equals("05-Excelente")){
//		        		qtdExcelente =Integer.parseInt(o[2].toString());
//		        	}
//				}
//    		    
//    		    //System.out.println("03-Regular ->"+qtdRegular);
//    		    //System.out.println("04-Bom ->"+qtdBom);
//    		    //System.out.println("05-Excelente ->"+qtdExcelente);
//    		    
//    		    Integer i3 = 3 * qtdRegular;
//    		    Integer i4 = 4 * qtdBom;
//    		    Integer i5 = 5 * qtdExcelente;
//    		    double totalPontos = i3+i4+i5;
//    		    double totalPessoas = qtdRegular+qtdBom+qtdExcelente;
//    		    double media = totalPontos / totalPessoas;
//    		    
//    			
//    			Label lb = new Label("<span style='padding-left:18px';>Média:  "+String.format("%.2f", media)+"</span>", ContentMode.HTML);
//    			addComponent(lb); 
    			
    			setWidth("100%");
    			double media = gerarMediaGeralMensal();
    			Label lb = new Label("<div style='width:100%; text-align:center;'>"
    					+ "<span style='   background-color: #c3ab7d;  padding-left: 11px;  margin-top: 15px;  color: #ffffff;  border-radius: 4px;  padding-right: 11px;   padding-top: 6px;  padding-bottom: 10px;  font-weight: bold;  font-size: 1.2em;'>Média geral:  "+String.format("%.2f", media)+"</span></div>", ContentMode.HTML);
    			lb.setWidth("100%");
    			
    			addComponent(lb);
    			
    		}
    	};
    	
    	return vlNotaMedia;
    }
    
    public double gerarMediaGeralMensal(){
		
		EntityManager em = ConnUtil.getEntity();
				
    	Query q = em.createNativeQuery("SELECT DISTINCT(r.COD_PERGUNTA) FROM crm_perguntas_rel r, crm c WHERE c.ID = r.COD_CRM ");    	
    	    	
    	List<Integer> perguntas = q.getResultList(); 
    	
    	double soma_medias = 0, media_geral = 0;
    	int qtdMedias = 0;
    	
    	for (Integer pergunta: perguntas) {
    		
    		if(pergunta == 19 || pergunta ==25 || pergunta == 27){
    			
	    	
			    		
			    		double qtdRegular,qtdBom,qtdExcelente	= 0;
			    		
			    		Query qRegular = em.createNativeQuery("SELECT r.RESPOSTA FROM crm_perguntas_rel r, crm c WHERE r.COD_PERGUNTA =:p AND c.ID = r.COD_CRM  "
			        			+ "AND r.RESPOSTA LIKE '03-Regular' ");
			    		qRegular.setParameter("p", pergunta);		    		
			    		qtdRegular = qRegular.getResultList().size();
			    		
			    		Query qBom = em.createNativeQuery("SELECT r.RESPOSTA FROM crm_perguntas_rel r, crm c WHERE r.COD_PERGUNTA =:p AND c.ID = r.COD_CRM  "
			        			+ "AND r.RESPOSTA LIKE '04-Bom' ");
			    		qBom.setParameter("p", pergunta);		    
			    		qtdBom = qBom.getResultList().size();
			    		
			    		Query qExcelente = em.createNativeQuery("SELECT r.RESPOSTA FROM crm_perguntas_rel r, crm c WHERE r.COD_PERGUNTA =:p AND c.ID = r.COD_CRM  "
			        			+ "AND r.RESPOSTA LIKE '05-Excelente' ");
			    		qExcelente.setParameter("p", pergunta);		    	
			    		qtdExcelente = qExcelente.getResultList().size();
	
			    		double pontos_regular, pontos_bom, pontos_excelente, total_pontos, total_pessoas = 0;
			    		double media = 0;
			    		
			    		pontos_regular = qtdRegular * 3;
			    		pontos_bom = qtdBom * 4;
			    		pontos_excelente = qtdExcelente * 5;
			    		total_pontos = pontos_regular+pontos_bom+pontos_excelente;
			    		total_pessoas = qtdRegular+qtdBom+qtdExcelente;
			    		media = total_pontos / total_pessoas;
			    		//System.out.println("MEDIA: "+media);
			    		
			    		soma_medias = soma_medias + media;
			    		qtdMedias++;
	    		
	    		
			}
	    	
    	
    	}
    	return (soma_medias/qtdMedias);
	}
    
//    private ComboBox buildCbPeriodicidade(){
//    	cbPeriodicidade = new ComboBox();
//    	cbPeriodicidade.setNullSelectionAllowed(false);
//    	cbPeriodicidade.setTextInputAllowed(false);
//    	
//    	cbPeriodicidade.addStyleName("margin-left-10");
//    	
//    	cbPeriodicidade.addItem("MENSAL");
//    	cbPeriodicidade.addItem("TRIMESTRAL");
//    	cbPeriodicidade.addItem("ANUAL");
//    	cbPeriodicidade.select("MENSAL");
//    	
//    	cbPeriodicidade.addValueChangeListener(new ValueChangeListener() {
//			
//			@Override
//			public void valueChange(ValueChangeEvent event) {
//				if(cbPeriodicidade.getParent().getParent() instanceof CssLayout){
//					((CssLayout)cbPeriodicidade.getParent().getParent()).replaceComponent(chart, buildChart(true));
//					((CssLayout)cbPeriodicidade.getParent().getParent()).replaceComponent(vlNotaMedia,buildNotaMedia());
//				}
//				
//				if(cbPeriodicidade.getParent().getParent() instanceof VerticalLayout){
//					((VerticalLayout)cbPeriodicidade.getParent().getParent()).replaceComponent(chart, buildChart(false));					
//					((VerticalLayout)cbPeriodicidade.getParent().getParent()).replaceComponent(vlNotaMedia,buildNotaMedia());
//					
//					((VerticalLayout)cbPeriodicidade.getParent().getParent()).setExpandRatio(chart, 1f);
//				}
//			}
//		});
//    	
//    	return cbPeriodicidade;
//    }
    
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
    
    JPAContainer<Perguntas> containerPergunta;
    private JPAContainer<Perguntas> buildContainerPerguntas(){
    	containerPergunta = JPAContainerFactory.make(Perguntas.class, ConnUtil.getEntity());
    	
    	if(cbAssunto != null && cbAssunto.getValue() != null){
    		EntityItem<CrmAssunto> itemCrmAssunto = (EntityItem<CrmAssunto>)cbAssunto.getItem(cbAssunto.getValue());
    		containerPergunta.addContainerFilter(Filters.eq("assunto", itemCrmAssunto.getEntity()));
    	}
    	
    	containerPergunta.addContainerFilter(Filters.eq("status", "ATIVO"));
    	containerPergunta.addContainerFilter(Filters.eq("pre_respostas", true));    	
    	containerPergunta.sort(new Object[]{"pergunta"}, new boolean[]{true});
    	
    	return containerPergunta;
    }
  
  
    
  private ComboBox buildcbAssuntos(boolean margin){
    	JPAContainer<CrmAssunto> containerAssunto = JPAContainerFactory.makeReadOnly(CrmAssunto.class, ConnUtil.getEntity());
    	containerAssunto.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
    	containerAssunto.addContainerFilter(Filters.eq("perguntas_ativas", "ATIVO"));
    	
    	cbAssunto = new ComboBox(null,containerAssunto);    	
    	cbAssunto.addStyleName("margin-left-10");
    	
    	cbAssunto.setTextInputAllowed(false);
    	cbAssunto.setNullSelectionAllowed(true);
    	cbAssunto.setItemCaptionPropertyId("nome");
    	if(containerAssunto.getItemIds().toArray().length > 0){
    		cbAssunto.select(containerAssunto.getItemIds().toArray()[0]);
    	}else{
    		cbAssunto.setEnabled(false); 
    	}
    	cbAssunto.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(cbAssunto.getValue() != null){
					
					if(cbAssunto.getParent() instanceof CssLayout){
						((CssLayout) cbAssunto.getParent()).replaceComponent(cbPerguntas, buildcbPerguntas(false));
						((CssLayout)cbPerguntas.getParent()).replaceComponent(chart, buildChart(true));			
					}
					
					if(cbAssunto.getParent() instanceof VerticalLayout){
						
						if(cbAssunto.getParent() instanceof VerticalLayout){
							((VerticalLayout) cbAssunto.getParent()).replaceComponent(cbPerguntas, buildcbPerguntas(true));						
						}
						
						if(cbAssunto.getParent().getParent() instanceof CssLayout){
							((CssLayout)cbAssunto.getParent().getParent()).replaceComponent(chart, buildChart(true));							
						}
						
						if(cbAssunto.getParent().getParent() instanceof VerticalLayout){
							((VerticalLayout)cbAssunto.getParent().getParent()).replaceComponent(chart, buildChart(false));
							((VerticalLayout)cbAssunto.getParent().getParent()).setExpandRatio(chart, 1f);				
							
						}
					}
					
				}
			}
		});
    	
    	//cbAssunto.addItem("TODOS");
    	//cbAssunto.select("TODOS");
    	cbAssunto.select(null);
    	
    	return cbAssunto;
    }
    
    
    private ComboBox buildcbPerguntas(boolean margin){
    	
    	if(cbAssunto.isEnabled()){
    		cbPerguntas = new ComboBox(null,buildContainerPerguntas());
    	}else{
    		cbPerguntas = new ComboBox();
    	}

   		cbPerguntas.addStyleName("margin-left-10");
    	cbPerguntas.setTextInputAllowed(false);
    	cbPerguntas.setWidth("90%");
    	cbPerguntas.setNullSelectionAllowed(false);
    	cbPerguntas.setItemCaptionPropertyId("pergunta");
    	cbPerguntas.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(cbPerguntas.getParent() instanceof VerticalLayout){
					if(cbPerguntas.getParent().getParent() instanceof CssLayout){
						((CssLayout)cbPerguntas.getParent().getParent()).replaceComponent(chart, buildChart(true));
						((CssLayout)cbPerguntas.getParent().getParent()).replaceComponent(vlNotaMedia,buildNotaMedia());
						
						
					}
					
					if(cbPerguntas.getParent().getParent() instanceof VerticalLayout){
						((VerticalLayout)cbPerguntas.getParent().getParent()).replaceComponent(chart, buildChart(false));
						((VerticalLayout)cbPerguntas.getParent().getParent()).replaceComponent(vlNotaMedia,buildNotaMedia());

						((VerticalLayout)cbPerguntas.getParent().getParent()).setExpandRatio(chart, 1f);
					}
				}
			}
		});
    	
    	if(containerPergunta != null){
    		cbPerguntas.select(containerPergunta.getItemIds().toArray()[0]);
    	}else{
    		cbPerguntas.setEnabled(false); 
    	}
    	
    	
    	
    	return cbPerguntas;
    }
//    private JPAContainer<Perguntas> buildContainer(){
//    	return container;
//    }
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
            	  PrefeDashDAO.remove(PesquisaSatisfacaoChart.class.toString());
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
                Window winMaximize = new Window("Gráfico Pesquisa de Satisfação");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");
                winMaximize.setContent(new VerticalLayout(){
                	{
                		setSizeFull();
                		setMargin(true); 
                		addComponent(new VerticalLayout(){
                        	{
                        		//addComponent(buildCbPeriodicidade());
                            	addComponent(buildcbAssuntos(true));
                            	addComponent(buildcbPerguntas(true));
                        	}
                        });
                		addComponent(buildChart(false));
                		addComponent(buildNotaMedia());
                		setExpandRatio(chart, 1f);
                	}
                });
                winMaximize.setModal(true);
                winMaximize.center();
                
                
                winMaximize.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						//buildLayout();
					}
				});
                
                getUI().addWindow(winMaximize);                
            }
        });
        
        return btMaximize;
    }
    
    private void PegarMediaGeral(){
    
    		//353 - POS-VENDA INSTALACAO
    		//354 - POS-VENDA REPARO
    	
    	Date data1 = new DateTime().minusMonths(1).toDate();
    	Date data2 = new Date();
    	
    	EntityManager em = ConnUtil.getEntity();
    	//Query q = em.createQuery("select r from CrmPesquisaRel r where r.cod_crm. r.data_cadastro >=:data1 and r.data_cadastro <=:data2", CrmPesquisaRel.class);

    	//RESPOSTAS PARA POS-VENDA INSTALACAO
    	Query q = em.createNativeQuery("SELECT DISTINCT(r.COD_PERGUNTA) FROM crm_perguntas_rel r, crm c WHERE c.ID = r.COD_CRM AND c.CRM_ASSUNTOS_ID = 353 "
    			+ "AND r.DATA_CADASTRO >= '2021-05-01'");
    	
    	
    	
    }
    
    private Chart buildChart(boolean caption){
    	
    	
    	chart = new Chart();
    	if(caption){
    		chart.setCaption("Gráfico Pesquisa de Satisfação");
    		chart.getConfiguration().getLegend().setEnabled(false);
    	}
        
    	chart.getConfiguration().disableCredits();
    	chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(false);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().setExporting(true);
        
        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ this.y");
        chart.getConfiguration().getyAxis().setTitle("Quantidade");
        chart.getConfiguration().setTooltip(tooltip); 
        
        chart.setLocale(new Locale("pt", "BR"));     
        
        //chart.setWidth("100%");
        
        //if(caption){
        // chart.setHeight("70%");
        //}else{
        // chart.setHeight("90%");
        //}
        chart.setSizeFull();

        
        if(cbPerguntas.getValue() != null){
        	String pergunta = cbPerguntas.getItem(cbPerguntas.getValue()).getItemProperty("pergunta").getValue().toString();
        	Integer cod_pergunta = (Integer)cbPerguntas.getItem(cbPerguntas.getValue()).getItemProperty("id").getValue();
	        EntityManager em = ConnUtil.getEntity();
	        
	        List<Object[]> result = null;

	        Query q = em.createNativeQuery("SELECT PERGUNTA, RESPOSTA, COUNT( RESPOSTA ) AS QTD FROM crm_perguntas_rel WHERE "
	        		+ "COD_PERGUNTA =:cod_pergunta AND PERGUNTA =:pergunta AND RESPOSTA NOT LIKE 'Nao respondeu' "
	        		+ "GROUP BY PERGUNTA, RESPOSTA ORDER by RESPOSTA DESC");
	        q.setParameter("cod_pergunta", cod_pergunta);
	        q.setParameter("pergunta", pergunta);
	           result = Lists.reverse(q.getResultList());

	        
//	        if(cbPeriodicidade.getValue().toString().equals("TRIMESTRAL")){
//		        Query q = em.createNativeQuery("SELECT PERGUNTA, RESPOSTA, COUNT( RESPOSTA ) AS QTD FROM crm_perguntas_rel WHERE "
//		        		+ "COD_PERGUNTA =:cod_pergunta AND PERGUNTA =:pergunta "
//		        		+ "AND  DATE_FORMAT(DATA_CADASTRO,  '%m/%Y' )>= :mes1 "
//		        		+ "AND  DATE_FORMAT(DATA_CADASTRO,  '%m/%Y' )<= :mes2 "
//		        		+ "GROUP BY PERGUNTA, RESPOSTA ORDER by RESPOSTA DESC");
//		        q.setParameter("cod_pergunta", cod_pergunta);
//		        q.setParameter("pergunta", pergunta);
//		        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
//		        
//		        try{
//		        	q.setParameter("mes1",sdf.format(new DateTime().minusMonths(3).toDate()).toString());
//		        	q.setParameter("mes2",sdf.format(new DateTime().toDate()).toString());
//		        }catch(Exception e){
//		        	e.printStackTrace();
//		        }
//		        result = Lists.reverse(q.getResultList());
//	        }
	        
//	        if(cbPeriodicidade.getValue().toString().equals("ANUAL")){
//		        Query q = em.createNativeQuery("SELECT PERGUNTA, RESPOSTA, COUNT( RESPOSTA ) AS QTD FROM crm_perguntas_rel WHERE "
//		        		+ "COD_PERGUNTA =:cod_pergunta AND PERGUNTA =:pergunta "
//		        		+ "AND  DATE_FORMAT(DATA_CADASTRO,  '%Y' )=:ano "		        		
//		        		+ "GROUP BY PERGUNTA, RESPOSTA ORDER by RESPOSTA DESC");
//		        q.setParameter("cod_pergunta", cod_pergunta);
//		        q.setParameter("pergunta", pergunta);
//		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
//		        
//		        try{
//		        	q.setParameter("ano", sdf.format(new DateTime().toDate()).toString());		        	
//		        }catch(Exception e){
//		        	e.printStackTrace();
//		        }
//		        result = Lists.reverse(q.getResultList());
//		        
//		        
//		        
//	        }
	        
	        result1 = result;
	        	        
	        List<Series> series = new ArrayList<Series>();
	        
	        Labels dataLabels = new Labels();
	        dataLabels.setEnabled(true);
	        //dataLabels.setRotation(-90);
	        dataLabels.setColor(new SolidColor(255, 255, 255));
	        dataLabels.setAlign(HorizontalAlign.CENTER);
	        //dataLabels.setX(4);
	        dataLabels.setY(30);
	        dataLabels.setFormatter("this.y");
	        Style style = new Style();
	        style.setFontSize("15px");
	        style.setFontFamily("Verdana, sans-serif");
	        dataLabels.setStyle(style);
	        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
	        plotOptionsColumn.setDataLabels(dataLabels);
	        //datLabels.setAlign(
	        Color[] colors = new VaadinTheme().getColors();
	        PlotOptionsColumn column = new PlotOptionsColumn();
	        column.setCursor(Cursor.POINTER);
	        column.setDataLabels(new Labels(true));
	        column.getDataLabels().setColor(colors[0]);
	        column.getDataLabels().getStyle().setFontWeight(FontWeight.BOLD);
	        column.getDataLabels().setFormatter("this.y");

	        //conf.setPlotOptions(column);
	        chart.getConfiguration().setPlotOptions(column);
	        
	        if(result != null){
	        for(Object[] o:result)
		        {	     
		        	
		        	ListSeries serie = new ListSeries((String)o[1],(Number)o[2]);
		        	//serie.setPlotOptions(plotOptionsColumn);
		        	series.add(serie);        	
		        }        
	        }
	       
	        chart.getConfiguration().setSeries(series);
        }
        
        return chart;
    }


}
