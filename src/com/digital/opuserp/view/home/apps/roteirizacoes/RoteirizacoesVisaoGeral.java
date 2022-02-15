package com.digital.opuserp.view.home.apps.roteirizacoes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.view.util.Notify;
import com.github.wolfie.refresher.Refresher;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.FontWeight;
import com.vaadin.addon.charts.themes.VaadinTheme;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
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
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class RoteirizacoesVisaoGeral extends CssLayout {
	
	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;    
  
    private VerticalLayout vLPri;    
  
    private Table tbPLan;
    
    Refresher refresher;
    DateField dt;
    
    public RoteirizacoesVisaoGeral() {
    	
    	addStyleName("layout-panel");
        setSizeFull();
        
        buildLayout(new Date());
        
        
        addExtension(buildRefresher());	
    }
    
    public Refresher buildRefresher(){
		
		
		refresher = new Refresher();
		refresher.setRefreshInterval(30000);
		refresher.addListener(new Refresher.RefreshListener() {
			
			@Override
			public void refresh(Refresher source) {
								
				
				 buildLayout(dt != null && dt.getValue() != null ? dt.getValue() : new Date());
				if(winMaximize != null){
					setLayoutModal(winMaximize, dt != null && dt.getValue() != null ? dt.getValue() : new Date());
				}
				 
			}
		});
		
		return refresher;
	}
    
    private void buildLayout(Date data){
    		
    		removeAllComponents();
    		
    		//Implementar Resumo de OS que não estão atribuídas
    		
    		HorizontalLayout hlFiltros = new HorizontalLayout();
    		hlFiltros.setWidth("100%");
    		
    		dt = new DateField();
    		dt.setValue(data);
    		dt.setImmediate(true);
    		dt.addValueChangeListener(new Property.ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					buildLayout(dt.getValue());
				}
			});
    		
    		hlFiltros.addComponent(dt);
    		hlFiltros.setComponentAlignment(dt, Alignment.MIDDLE_CENTER);
    		
    		addComponent(hlFiltros); 
    		
    		HorizontalLayout hlCol1 = new HorizontalLayout();  
    		hlCol1.setCaption("ROTAS ATRIBUÍDAS");
    		hlCol1.setWidth("100%");
    		hlCol1.setStyleName("topo-roteirizacoes-visao-geral");
    		
    		   		
    	
    			VerticalLayout vlEquipe = new VerticalLayout(){;
	    			{
	    				addComponent(new Label("EQUIPE"));
	    				
	    			}
    			};
    			vlEquipe.setStyleName("topo");
    			
    			VerticalLayout vlTecnico = new VerticalLayout(){;
	    			{
	    				addComponent(new Label("TÉCNICO"));
	    			}
    			};
    			vlTecnico.setStyleName("topo");
    			
    			VerticalLayout vlVeiculo = new VerticalLayout(){;
	    			{
	    				addComponent(new Label("VEÍCULO"));
	    			}
    			};
    			vlVeiculo.setStyleName("topo");
    			
    			VerticalLayout vlOsAtribuidas = new VerticalLayout(){;
	    			{
	    				addComponent(new Label("OS. ATRIBUÍDAS"));
	    			}
    			};
    			vlOsAtribuidas.setStyleName("topo");
    		  		
    			hlCol1.addComponent(vlEquipe);
    			hlCol1.addComponent(vlTecnico);
    			hlCol1.addComponent(vlVeiculo);
    			hlCol1.addComponent(vlTecnico);
    			hlCol1.addComponent(vlOsAtribuidas);
    	
    	
    	
    	
    	
    	addComponent(hlCol1);
    	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	final EntityManager em = ConnUtil.getEntity();
    	Query q = em.createNativeQuery("SELECT *,COUNT(TECNICO) AS QTD FROM `ose` , veiculos v WHERE ose.DATA_ATRIBUICAO =:dt AND v.ID = ose.VEICULO_ID AND `STATUS_2` != 'CONCLUIDO' AND `STATUS_2` != 'FECHADO' GROUP BY `TECNICO` ", Ose.class);    	
    	q.setParameter("dt",sdf.format(dt.getValue()));
    	
    	for (final Ose o: (List<Ose>)q.getResultList()) {		
    		
    		final Label lbQtd = new Label("",ContentMode.HTML);
    		final String equipe = o.getEquipe();
		
	    		final HorizontalLayout hlDetalhes = new HorizontalLayout(){
					{
						Query qDetalhes = em.createNativeQuery("SELECT *  FROM `ose`, veiculos v WHERE ose.DATA_ATRIBUICAO =:dt AND v.ID = ose.VEICULO_ID AND v.COD_VEICULO = :veiculo  AND `TECNICO` LIKE :tecnico AND `EQUIPE` LIKE :equipe AND `STATUS_2` != 'CONCLUIDO' AND `STATUS_2` != 'FECHADO' ", Ose.class);
						qDetalhes.setParameter("veiculo", o.getVeiculo_id().getCod_veiculo());
						qDetalhes.setParameter("equipe", equipe);
						qDetalhes.setParameter("tecnico", o.getTecnico());
						qDetalhes.setParameter("dt", sdf.format(dt.getValue()));
						
						
						Table tb = new Table();
						tb.addStyleName("tb-fonte-menor");
						tb.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
						tb.addContainerProperty("Cod", String.class, "");
						tb.addContainerProperty("Grupo", String.class, "");
						tb.addContainerProperty("SubGrupo", String.class, "");
						tb.addContainerProperty("Tipo", String.class, "");
						tb.addContainerProperty("Data", String.class, "");
						tb.addContainerProperty("Cliente", String.class, "");
						tb.addContainerProperty("Status", String.class, "");
						tb.setWidth("100%");
						
						tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
							
							
							public String getStyle(Table source, Object itemId, Object propertyId) {

								 if (propertyId == null){
					                    return "row-header-default"; // Will not actually be visible
								 }else{
					               
					                Item item = source.getItem(itemId);
					                
					                if(item != null && item.getItemProperty("Status") != null && item.getItemProperty("Status").getValue() != null){
						                String status = item.getItemProperty("Status").getValue().toString();
						               
						                if(status.equals("ENCAMINHADO")){
						                	return "row-header-osi-encaminhado";    
						                }else if(status.equals("EM ANDAMENTO")){
						                	return "row-header-osi-encaminhado";    
						                }else if(status.equals("FECHADO")){
						                	return "row-header-osi-entregue";       
						                }else if(status.equals("CONCLUIDO")){
						                	 return "row-header-osi-fechado";
						                }else{
//						                	try{
//							                	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//							                	Date data_ex = (Date)source.getItem(itemId).getItemProperty("Data").getValue();
//							                	
//							                	
//							                	if(!sdf.parse(sdf.format(data_ex)).after(sdf.parse(sdf.format(new Date())))){
//							                		return "row-header-atrasado";
//							                	}else{		                		
//							                		return "row-header-default";
//							                	}
//						                	}catch(Exception e){
//						                		e.printStackTrace();
						                		return "row-header-default";
//						                	}
						                	
						                }
					                
					                }else{
					                	 return "row-header-default";
					                }
								 }
							}
						});
						
						for (Ose ose: (List<Ose>)qDetalhes.getResultList()) {
							tb.addItem(new Object[]{ose.getId().toString(), ose.getGrupo().getNome(), ose.getSubgrupo().getNome(), ose.getTipo_subgrupo().getNome(),DataUtil.formatDateBra(ose.getData_ex()), ose.getCliente().getNome_razao(), ose.getStatus()}, tb.getItemIds().size()+1);
							
						}
						
						addComponent(tb);
					}
				};
				hlDetalhes.setWidth("100%");
				hlDetalhes.setHeight("250px");
				hlDetalhes.setVisible(false); 
    		
				HorizontalLayout hlInfo1 = new HorizontalLayout(){
					{
						VerticalLayout vlEquipe = new VerticalLayout(){
			    			{
			    				Button btClickDetalhes = new Button(o.getEquipe() != null ? o.getEquipe() : "SEM EQUIPE");
			    				btClickDetalhes.setWidth("100%");
			    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
			    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
			    				btClickDetalhes.addClickListener(new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										
										hlDetalhes.setVisible(!hlDetalhes.isVisible());
										
									}
								});
			    				
			    				addComponent(btClickDetalhes); 
			    				
			    			}
		    			};
		    			vlEquipe.setStyleName("registros");
		    			addComponent(vlEquipe);
		    			
		    			VerticalLayout vlVeiculo = new VerticalLayout(){
		    				{
		    					
		    					Button btClickDetalhes = new Button(o.getVeiculo_id().getCod_veiculo()+" - "+o.getVeiculo_id().getModelo());
			    				btClickDetalhes.setWidth("100%");
			    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
			    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
			    				btClickDetalhes.addClickListener(new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										
										hlDetalhes.setVisible(!hlDetalhes.isVisible());
										
									}
								});
			    				
			    				addComponent(btClickDetalhes); 
		    				}
		    			};
		    			vlVeiculo.setStyleName("registros");
		    			addComponent(vlVeiculo);
		    			
		    			VerticalLayout vlTecnico = new VerticalLayout(){
			    			{
			    				Button btClickDetalhes = new Button(o.getTecnico());
			    				btClickDetalhes.setWidth("100%");
			    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
			    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
			    				btClickDetalhes.addClickListener(new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										
										hlDetalhes.setVisible(!hlDetalhes.isVisible());
										
									}
								});
			    				
			    				addComponent(btClickDetalhes); 
			    				
			    			}
		    			};
		    			vlTecnico.setStyleName("registros");
		    			addComponent(vlTecnico);
		    			
		    			
		    			
					}
				};    	
				hlInfo1.setStyleName("topo-roteirizacoes-registros");
				hlInfo1.setWidth("100%");
				
				
				VerticalLayout vlOsAtribuida = new VerticalLayout(){
	    			{	
	    				addComponent(lbQtd);
	    			}
    			};
    			
    			Query q2 = em.createNativeQuery("SELECT COUNT(TECNICO) AS QTD  FROM `ose`, veiculos v WHERE ose.DATA_ATRIBUICAO =:dt AND  v.ID = ose.VEICULO_ID AND v.COD_VEICULO = :veiculo  AND `TECNICO` LIKE :tecnico AND `EQUIPE` LIKE :equipe  AND `STATUS_2` != 'CONCLUIDO' AND `STATUS_2` != 'FECHADO' GROUP BY TECNICO ");
				q2.setParameter("veiculo", o.getVeiculo_id().getCod_veiculo());
				q2.setParameter("equipe", equipe);
				q2.setParameter("tecnico", o.getTecnico());
				q2.setParameter("dt", sdf.format(dt.getValue()));
				
				if(q2.getResultList().size() == 1){
					lbQtd.setValue("<div><span style='color:#e10808;padding: 4px;font-weight:bold;'>"+q2.getSingleResult().toString()+"</span></div>");
				}else{
					lbQtd.setValue("<div><span style='color:#0093ff;padding: 4px;font-weight:bold;'>0</span></div>");
				}
				
				vlOsAtribuida.setStyleName("registros");
    			hlInfo1.addComponent(vlOsAtribuida);
				
				//Loop
		    	
				addComponent(hlInfo1);
				
				
				
				addComponent(hlDetalhes);
    	}
    	
    	Query qQtdSemAtribuiVe = em.createNativeQuery("SELECT * FROM veiculos WHERE STATUS = 'ATIVO' AND NOT EXISTS(SELECT * FROM ose WHERE `DATA_ATRIBUICAO` =:dt AND `VEICULO_ID` = veiculos.ID)", Veiculos.class);
    	qQtdSemAtribuiVe.setParameter("dt",sdf.format(dt.getValue()));
    	
    	for (final Veiculos o: (List<Veiculos>)qQtdSemAtribuiVe.getResultList()) {
			
		    	//Veiculos disponíveis
		    	HorizontalLayout hlInfo1 = new HorizontalLayout(){
					{
						VerticalLayout vlEquipe = new VerticalLayout(){
			    			{
			    				Button btClickDetalhes = new Button(" - ");
			    				btClickDetalhes.setWidth("100%");
			    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
			    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
			    				addComponent(btClickDetalhes); 
			    				
			    			}
		    			};
		    			vlEquipe.setStyleName("registros");
		    			addComponent(vlEquipe);
		    			
		    			VerticalLayout vlVeiculo = new VerticalLayout(){
		    				{
		    					
		    					Button btClickDetalhes = new Button(o.getCod_veiculo()+" - "+o.getModelo());
			    				btClickDetalhes.setWidth("100%");
			    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
			    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
			    				
			    				
			    				addComponent(btClickDetalhes); 
		    				}
		    			};
		    			vlVeiculo.setStyleName("registros");
		    			addComponent(vlVeiculo);
		    			
		    			
		    			VerticalLayout vlTecnico = new VerticalLayout(){
		    				{
		    					
		    					Button btClickDetalhes = new Button(" - ");
			    				btClickDetalhes.setWidth("100%");
			    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
			    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
			    				
			    				
			    				addComponent(btClickDetalhes); 
		    				}
		    			};
		    			vlTecnico.setStyleName("registros");
		    			addComponent(vlTecnico);
		    			
		    			VerticalLayout vlOsAtribuida = new VerticalLayout(){
		    				{
		    					
		    					Button btClickDetalhes = new Button(" - ");
			    				btClickDetalhes.setWidth("100%");
			    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
			    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
			    				
			    				
			    				addComponent(btClickDetalhes); 
		    				}
		    			};
		    			vlOsAtribuida.setStyleName("registros");
		    			addComponent(vlOsAtribuida);
		    			
					}
				};    	
				hlInfo1.setStyleName("topo-roteirizacoes-registros");
				hlInfo1.setWidth("100%");
				addComponent(hlInfo1); 
    	}
    	
    	
    	
    	HorizontalLayout hlRodapeQtdRegistros = new HorizontalLayout();
    	hlRodapeQtdRegistros.setSpacing(false);
    	hlRodapeQtdRegistros.setMargin(false);
    	hlRodapeQtdRegistros.setWidth("100%");
    	
    	HorizontalLayout hlRodapeQtdRegistros2 = new HorizontalLayout();
    	hlRodapeQtdRegistros2.setSpacing(false);
    	hlRodapeQtdRegistros2.setMargin(false);
    	hlRodapeQtdRegistros2.setWidth("100%");
    	
    	Query qQtdSemAtribuiVencida = em.createNativeQuery("SELECT * FROM `ose` WHERE `DATA_EX` <:dt and `STATUS_2` != 'FECHADO' AND `STATUS_2` != 'CONCLUIDO' AND `EQUIPE` IS NOT NULL");
    	qQtdSemAtribuiVencida.setParameter("dt",sdf.format(dt.getValue()));
    	
    	Query qQtdSemAtribui = em.createNativeQuery("SELECT * FROM `ose` WHERE `DATA_EX` =:dt and `STATUS_2` != 'FECHADO' AND `STATUS_2` != 'CONCLUIDO' AND `EQUIPE` IS NULL");
    	qQtdSemAtribui.setParameter("dt",sdf.format(dt.getValue()));
    	Label lbRegistros;
    	
    	if(qQtdSemAtribui.getResultList().size() == 0){
    		lbRegistros = new Label("<div>"+String.valueOf(qQtdSemAtribui.getResultList().size())+" OS não atribuídas.</div>",ContentMode.HTML);
    		lbRegistros.setWidth("145px");
    	}else{
    		lbRegistros = new Label("<div class='invalid'>"+String.valueOf(qQtdSemAtribui.getResultList().size())+" OS não atribuídas.</div>",ContentMode.HTML);
    		lbRegistros.setWidth("145px");
    	}
    	
    	Label lbRegistrosVenc;
    	if(qQtdSemAtribuiVencida.getResultList().size() == 0){
	    	lbRegistrosVenc = new Label("<div>"+String.valueOf(qQtdSemAtribuiVencida.getResultList().size())+" OS Vencidas.</div>",ContentMode.HTML);
	    	lbRegistrosVenc.setWidth("145px");
    	}else{
    		lbRegistrosVenc = new Label("<div class='invalid'>"+String.valueOf(qQtdSemAtribuiVencida.getResultList().size())+" OS Vencidas.</div>",ContentMode.HTML);
	    	lbRegistrosVenc.setWidth("145px");
    	}
    	
    	hlRodapeQtdRegistros.addComponent(lbRegistros); 
    	hlRodapeQtdRegistros.setComponentAlignment(lbRegistros, Alignment.BOTTOM_RIGHT);
    	
    	hlRodapeQtdRegistros2.addComponent(lbRegistrosVenc); 
    	hlRodapeQtdRegistros2.setComponentAlignment(lbRegistrosVenc, Alignment.BOTTOM_RIGHT);
    	
    	addComponent(hlRodapeQtdRegistros);
    	addComponent(hlRodapeQtdRegistros2);
    	
    	
    	
    	//Média de Ativações
    	List<String> tecnicos = new ArrayList<>();
    	List<String[]> ativacoes = new ArrayList<>(); 
    	
    	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
    	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	Query qTecnicos = em.createNativeQuery("SELECT COUNT(*), TECNICO, EQUIPE FROM `ose` WHERE DATA_CONCLUSAO >= :dt1 AND DATA_CONCLUSAO <= :dt2 AND EQUIPE = 'ATIVACAO' GROUP BY TECNICO");
    	
    	try{
    		Date dt1 =sdf2.parse(sdf2.format(sdf1.parse(sdf1.format(new Date())+"-01")));
    		Date dt2 = new DateTime(dt1).plusDays(30).toDate();
    		
    		
    		//new DateTime().get
    		
    		qTecnicos.setParameter("dt1",dt1);
    		qTecnicos.setParameter("dt2",dt2);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	 
    	
    	//qTecnicos.getResultList().size()
    	for (Object o: qTecnicos.getResultList()) {
    		if(((Object[])o)[1] != null){
    			tecnicos.add(((Object[])o)[1].toString());
    		}
		}
    	
    	
    	for (String tecnico : tecnicos) {
			
    		Integer diaMes = new DateTime().getDayOfMonth();
    		
    		for(int i=1; i<diaMes+1; i++){
    			try{
	    			SimpleDateFormat sdfData = new SimpleDateFormat("yyyy-MM-dd");
	    			SimpleDateFormat sdfhora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    			
	    			Date d1 = sdfhora.parse(sdfData.format(new DateTime().minusDays(diaMes-i).toDate())+" 00:00:00");
	    			Date d2 = sdfhora.parse(sdfData.format(new DateTime().minusDays(diaMes-i).toDate())+" 23:00:00");
	    			
	    			Query qAtivacoesPorDia = em.createNativeQuery("SELECT COUNT(*), TECNICO, EQUIPE FROM `ose` WHERE DATA_CONCLUSAO >=:dt1 AND DATA_CONCLUSAO <=:dt2 AND EQUIPE = 'ATIVACAO' AND TECNICO =:t GROUP BY TECNICO");
	    			qAtivacoesPorDia.setParameter("dt1", sdfhora.format(d1));
	    			qAtivacoesPorDia.setParameter("dt2", sdfhora.format(d2));
	    			qAtivacoesPorDia.setParameter("t", tecnico);
	    			
	    			
	    			for (Object o2 : qAtivacoesPorDia.getResultList()) {
						
	    				ativacoes.add(new String[]{((Object[])o2)[1].toString(),((Object[])o2)[0].toString()});
					}
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    		}
    		
		}
    	
    	//Calcular Médias
    	List<String[]> mediaTecnico = new ArrayList<>(); 
    	for (String tecnico : tecnicos) {
    		
    		int qtd = 0;
    		int dias = 0;
    		for (String[] ativTecnicos : ativacoes) {
				if(ativTecnicos[0].toString().equals(tecnico)){
					dias++;
					qtd = qtd+Integer.parseInt(ativTecnicos[1]);
				}
			}
    		
    		mediaTecnico.add(new String[]{tecnico, String.valueOf(new Float(qtd)/new Float(dias))});
			
		}
    	
    	
//    	for (String[] medias : mediaTecnico) {
//			System.out.println(medias);
//		}
    	
    	
    	
    	addComponent(new HorizontalLayout(){
    		{
    			setWidth("100%");
    			Label lb = new Label("<br/><br/><br/>Média de Ativações do mês atual", ContentMode.HTML);
    			lb.setWidth("253px");
    			addComponent(lb);
    			setComponentAlignment(lb, Alignment.MIDDLE_CENTER);
    		}
    	});
    	
    	addComponent(buildChart(mediaTecnico, true));
    	
    	
    	
    	
    	
    	//addComponent(buildBtrefresh());        
    	addComponent(buildBtMaximize(dt.getValue()));
        addComponent(buildBtClose());       
        //addComponent(buildCbSetor());
        //addComponent(buildTbView(true));
    }
    
    
    private Chart buildChart(List<String[]> mediaTecnico, boolean label){
    	chart = new Chart();
    	if(label){
    		//chart.setCaption("Média de Ativações ");
    		chart.getConfiguration().getLegend().setEnabled(true);
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
        chart.getConfiguration().getxAxis().setTitle("Agosto/2018");
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
        
        	
        	//List<String> s = new ArrayList<>();
        	for(Object[] o:mediaTecnico)
        	{        	
        		//if(!s.contains((String)o[2])){
        		//	s.add((String)o[2]);
        		//}
        		BigDecimal aNumber = new BigDecimal(new Float(String.valueOf(o[1])));
        		aNumber = aNumber.setScale(2, BigDecimal.ROUND_HALF_UP);	
        		
        		Number n = aNumber.doubleValue();
        		ListSeries serie = new ListSeries((String)o[0],n);       		        	        	    	
        		chart.getConfiguration().addSeries(serie);
        	}       

        	
			if(mediaTecnico.size() == 1){
				chart.getConfiguration().getxAxis().setCategories(mediaTecnico.get(0).toString());
			}else if(mediaTecnico.size() == 2){
				chart.getConfiguration().getxAxis().setCategories(mediaTecnico.get(0).toString(),mediaTecnico.get(1).toString());
			}else if(mediaTecnico.size() == 3){
				chart.getConfiguration().getxAxis().setCategories(mediaTecnico.get(0).toString(),mediaTecnico.get(1).toString(),mediaTecnico.get(2).toString());
			}
			     
        return chart;
    }
    
    ComboBox cbSetor;
    private ComboBox buildCbSetor(){
		cbSetor = new ComboBox(null, buildContainerSetores());
		cbSetor.setImmediate(true);
		cbSetor.setTextInputAllowed(false);
		cbSetor.setItemCaptionPropertyId("nome");
		
		cbSetor.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {				
				btRefresh.click();			
			}
		});
		
		return cbSetor;
	}
    
    private JPAContainer<Setores> buildContainerSetores(){
		JPAContainer<Setores> container = JPAContainerFactory.make(Setores.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		return container;
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
            	  PrefeDashDAO.remove(RoteirizacoesVisaoGeral.class.toString());
                  ((GridLayout)getParent()).removeComponent(btClose.getParent());   
            }
        });
        
        return btClose;
    }
    
    Window winMaximize;
    
    private Button buildBtMaximize(final Date data){
    	
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
                winMaximize = new Window("ROTAS ATRIBUÍDAS");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");                
                winMaximize.setModal(true);
                winMaximize.center();
                
                winMaximize.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {						
						//buildLayout();
					}
				});
                
                setLayoutModal(winMaximize, data); 
                
                getUI().addWindow(winMaximize);                
            }
        });
        
        return btMaximize;
    }
    
    private void setLayoutModal(Window w, final Date data){
    	w.setContent(new VerticalLayout(){
                	{
                		setSizeFull();
                		setMargin(true); 
                		
                		vLPri = new VerticalLayout(){
                			{
                				removeAllComponents();
                	    		
                	    		HorizontalLayout hlFiltros = new HorizontalLayout();
                	    		hlFiltros.setWidth("100%");
                	    		
                	    		final DateField dt2 = new DateField();
                	    		dt2.setValue(data);
                	    		dt2.setImmediate(true);
                	    		dt2.addValueChangeListener(new Property.ValueChangeListener() {
                					
                					@Override
                					public void valueChange(ValueChangeEvent event) {
                						setLayoutModal(winMaximize,dt2.getValue());
                					}
                				});
                	    		
                	    		hlFiltros.addComponent(dt2);
                	    		hlFiltros.setComponentAlignment(dt2, Alignment.MIDDLE_CENTER);
                	    		
                	    		addComponent(hlFiltros); 
                	    		
                	    		HorizontalLayout hlCol1 = new HorizontalLayout();  
                	    		//hlCol1.setCaption("ROTAS ATRIBUÍDAS");
                	    		hlCol1.setWidth("100%");
                	    		hlCol1.setStyleName("topo-roteirizacoes-visao-geral");
                	    		
                	    		   		
                	    	
                	    			VerticalLayout vlEquipe = new VerticalLayout(){;
                		    			{
                		    				addComponent(new Label("EQUIPE"));
                		    				
                		    			}
                	    			};
                	    			vlEquipe.setStyleName("topo");
                	    			
                	    			VerticalLayout vlTecnico = new VerticalLayout(){;
                		    			{
                		    				addComponent(new Label("TÉCNICO"));
                		    			}
                	    			};
                	    			vlTecnico.setStyleName("topo");
                	    			
                	    			VerticalLayout vlVeiculo = new VerticalLayout(){;
                		    			{
                		    				addComponent(new Label("VEÍCULO"));
                		    			}
                	    			};
                	    			vlVeiculo.setStyleName("topo");
                	    			
                	    			VerticalLayout vlOsAtribuidas = new VerticalLayout(){;
                		    			{
                		    				addComponent(new Label("OS. ATRIBUÍDAS"));
                		    			}
                	    			};
                	    			vlOsAtribuidas.setStyleName("topo");
                	    		  		
                	    			hlCol1.addComponent(vlEquipe);
                	    			hlCol1.addComponent(vlTecnico);
                	    			hlCol1.addComponent(vlVeiculo);
                	    			hlCol1.addComponent(vlTecnico);
                	    			hlCol1.addComponent(vlOsAtribuidas);
                	    	
                	    	
                	    	
                	    	
                	    	
                	    	addComponent(hlCol1);
                	    	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                	    	final EntityManager em = ConnUtil.getEntity();
                	    	Query q = em.createNativeQuery("SELECT *,COUNT(TECNICO) AS QTD FROM `ose` , veiculos v WHERE ose.DATA_ATRIBUICAO =:dt AND v.ID = ose.VEICULO_ID AND `STATUS_2` != 'CONCLUIDO' AND `STATUS_2` != 'FECHADO' GROUP BY `TECNICO` ", Ose.class);    	
                	    	q.setParameter("dt",sdf.format(dt2.getValue()));
                	    	
                	    	for (final Ose o: (List<Ose>)q.getResultList()) {		
                	    		
                	    		final Label lbQtd = new Label("",ContentMode.HTML);
                	    		final String equipe = o.getEquipe();
                			
                		    		final HorizontalLayout hlDetalhes = new HorizontalLayout(){
                						{
                							Query qDetalhes = em.createNativeQuery("SELECT *  FROM `ose`, veiculos v WHERE ose.DATA_ATRIBUICAO =:dt AND v.ID = ose.VEICULO_ID AND v.COD_VEICULO = :veiculo  AND `TECNICO` LIKE :tecnico AND `EQUIPE` LIKE :equipe AND `STATUS_2` != 'CONCLUIDO' AND `STATUS_2` != 'FECHADO' ", Ose.class);
                							qDetalhes.setParameter("veiculo", o.getVeiculo_id().getCod_veiculo());
                							qDetalhes.setParameter("equipe", equipe);
                							qDetalhes.setParameter("tecnico", o.getTecnico());
                							qDetalhes.setParameter("dt", sdf.format(dt2.getValue()));
                							
                							
                							Table tb = new Table();
                							tb.addStyleName("tb-fonte-menor");
                							tb.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
                							tb.addContainerProperty("Cod", String.class, "");
                							tb.addContainerProperty("Grupo", String.class, "");
                							tb.addContainerProperty("SubGrupo", String.class, "");
                							tb.addContainerProperty("Tipo", String.class, "");
                							tb.addContainerProperty("Data", String.class, "");
                							tb.addContainerProperty("Cliente", String.class, "");
                							tb.addContainerProperty("Status", String.class, "");
                							tb.setWidth("100%");
                							
                							tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
                								
                								
                								public String getStyle(Table source, Object itemId, Object propertyId) {

                									 if (propertyId == null){
                						                    return "row-header-default"; // Will not actually be visible
                									 }else{
                						               
                						                Item item = source.getItem(itemId);
                						                
                						                if(item != null && item.getItemProperty("Status") != null && item.getItemProperty("Status").getValue() != null){
                							                String status = item.getItemProperty("Status").getValue().toString();
                							               
                							                if(status.equals("ENCAMINHADO")){
                							                	return "row-header-osi-encaminhado";    
                							                }else if(status.equals("EM ANDAMENTO")){
                							                	return "row-header-osi-encaminhado";    
                							                }else if(status.equals("FECHADO")){
                							                	return "row-header-osi-entregue";       
                							                }else if(status.equals("CONCLUIDO")){
                							                	 return "row-header-osi-fechado";
                							                }else{
//                							                	try{
//                								                	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//                								                	Date data_ex = (Date)source.getItem(itemId).getItemProperty("Data").getValue();
//                								                	
//                								                	
//                								                	if(!sdf.parse(sdf.format(data_ex)).after(sdf.parse(sdf.format(new Date())))){
//                								                		return "row-header-atrasado";
//                								                	}else{		                		
//                								                		return "row-header-default";
//                								                	}
//                							                	}catch(Exception e){
//                							                		e.printStackTrace();
                							                		return "row-header-default";
//                							                	}
//                							                	
                							                }
                						                
                						                }else{
                						                	 return "row-header-default";
                						                }
                									 }
                								}
                							});
                							
                							
                							for (Ose ose: (List<Ose>)qDetalhes.getResultList()) {
                								tb.addItem(new Object[]{ose.getId().toString(), ose.getGrupo().getNome(), ose.getSubgrupo().getNome(), ose.getTipo_subgrupo().getNome(),DataUtil.formatDateBra(ose.getData_ex()), ose.getCliente().getNome_razao(), ose.getStatus()}, tb.getItemIds().size()+1);
                								
                							}
                							
                							addComponent(tb);
                						}
                					};
                					hlDetalhes.setWidth("100%");
                					hlDetalhes.setHeight("250px");
                					hlDetalhes.setVisible(false); 
                	    		
                					HorizontalLayout hlInfo1 = new HorizontalLayout(){
                						{
                							VerticalLayout vlEquipe = new VerticalLayout(){
                				    			{
                				    				Button btClickDetalhes = new Button(o.getEquipe() != null ? o.getEquipe() : "SEM EQUIPE");
                				    				btClickDetalhes.setWidth("100%");
                				    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
                				    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
                				    				btClickDetalhes.addClickListener(new Button.ClickListener() {
                										
                										@Override
                										public void buttonClick(ClickEvent event) {
                											
                											hlDetalhes.setVisible(!hlDetalhes.isVisible());
                											
                										}
                									});
                				    				
                				    				addComponent(btClickDetalhes); 
                				    				
                				    			}
                			    			};
                			    			vlEquipe.setStyleName("registros");
                			    			addComponent(vlEquipe);
                			    			
                			    			VerticalLayout vlVeiculo = new VerticalLayout(){
                			    				{
                			    					
                			    					Button btClickDetalhes = new Button(o.getVeiculo_id().getCod_veiculo()+" - "+o.getVeiculo_id().getModelo());
                				    				btClickDetalhes.setWidth("100%");
                				    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
                				    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
                				    				btClickDetalhes.addClickListener(new Button.ClickListener() {
                										
                										@Override
                										public void buttonClick(ClickEvent event) {
                											
                											hlDetalhes.setVisible(!hlDetalhes.isVisible());
                											
                										}
                									});
                				    				
                				    				addComponent(btClickDetalhes); 
                			    				}
                			    			};
                			    			vlVeiculo.setStyleName("registros");
                			    			addComponent(vlVeiculo);
                			    			
                			    			VerticalLayout vlTecnico = new VerticalLayout(){
                				    			{
                				    				Button btClickDetalhes = new Button(o.getTecnico());
                				    				btClickDetalhes.setWidth("100%");
                				    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
                				    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
                				    				btClickDetalhes.addClickListener(new Button.ClickListener() {
                										
                										@Override
                										public void buttonClick(ClickEvent event) {
                											
                											hlDetalhes.setVisible(!hlDetalhes.isVisible());
                											
                										}
                									});
                				    				
                				    				addComponent(btClickDetalhes); 
                				    				
                				    			}
                			    			};
                			    			vlTecnico.setStyleName("registros");
                			    			addComponent(vlTecnico);
                			    			
                			    			
                			    			
                						}
                					};    	
                					hlInfo1.setStyleName("topo-roteirizacoes-registros");
                					hlInfo1.setWidth("100%");
                					
                					
                					VerticalLayout vlOsAtribuida = new VerticalLayout(){
                		    			{	
                		    				addComponent(lbQtd);
                		    			}
                	    			};
                	    			
                	    			Query q2 = em.createNativeQuery("SELECT COUNT(TECNICO) AS QTD  FROM `ose`, veiculos v WHERE ose.DATA_ATRIBUICAO =:dt AND  v.ID = ose.VEICULO_ID AND v.COD_VEICULO = :veiculo  AND `TECNICO` LIKE :tecnico AND `EQUIPE` LIKE :equipe  AND `STATUS_2` != 'CONCLUIDO' AND `STATUS_2` != 'FECHADO' GROUP BY TECNICO ");
                					q2.setParameter("veiculo", o.getVeiculo_id().getCod_veiculo());
                					q2.setParameter("equipe", equipe);
                					q2.setParameter("tecnico", o.getTecnico());
                					q2.setParameter("dt", sdf.format(dt2.getValue()));
                					
                					if(q2.getResultList().size() == 1){
                						lbQtd.setValue("<div><span style='color:#e10808;padding: 4px;font-weight:bold;'>"+q2.getSingleResult().toString()+"</span></div>");
                					}else{
                						lbQtd.setValue("<div><span style='color:#0093ff;padding: 4px;font-weight:bold;'>0</span></div>");
                					}
                					
                					vlOsAtribuida.setStyleName("registros");
                	    			hlInfo1.addComponent(vlOsAtribuida);
                					
                					//Loop
                			    	
                					addComponent(hlInfo1);
                					
                					
                					
                					addComponent(hlDetalhes);
                	    	}
                	    	
                	    	
                	    	Query qQtdSemAtribuiVe = em.createNativeQuery("SELECT * FROM veiculos WHERE STATUS = 'ATIVO' AND NOT EXISTS(SELECT * FROM ose WHERE `DATA_ATRIBUICAO` =:dt AND `VEICULO_ID` = veiculos.ID)", Veiculos.class);
                	    	qQtdSemAtribuiVe.setParameter("dt",sdf.format(dt.getValue()));
                	    	
                	    	for (final Veiculos o: (List<Veiculos>)qQtdSemAtribuiVe.getResultList()) {
                				
                			    	//Veiculos disponíveis
                			    	HorizontalLayout hlInfo1 = new HorizontalLayout(){
                						{
                							VerticalLayout vlEquipe = new VerticalLayout(){
                				    			{
                				    				Button btClickDetalhes = new Button(" - ");
                				    				btClickDetalhes.setWidth("100%");
                				    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
                				    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
                				    				addComponent(btClickDetalhes); 
                				    				
                				    			}
                			    			};
                			    			vlEquipe.setStyleName("registros");
                			    			addComponent(vlEquipe);
                			    			
                			    			VerticalLayout vlVeiculo = new VerticalLayout(){
                			    				{
                			    					
                			    					Button btClickDetalhes = new Button(o.getCod_veiculo()+" - "+o.getModelo());
                				    				btClickDetalhes.setWidth("100%");
                				    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
                				    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
                				    				
                				    				
                				    				addComponent(btClickDetalhes); 
                			    				}
                			    			};
                			    			vlVeiculo.setStyleName("registros");
                			    			addComponent(vlVeiculo);
                			    			
                			    			
                			    			VerticalLayout vlTecnico = new VerticalLayout(){
                			    				{
                			    					
                			    					Button btClickDetalhes = new Button(" - ");
                				    				btClickDetalhes.setWidth("100%");
                				    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
                				    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
                				    				
                				    				
                				    				addComponent(btClickDetalhes); 
                			    				}
                			    			};
                			    			vlTecnico.setStyleName("registros");
                			    			addComponent(vlTecnico);
                			    			
                			    			VerticalLayout vlOsAtribuida = new VerticalLayout(){
                			    				{
                			    					
                			    					Button btClickDetalhes = new Button(" - ");
                				    				btClickDetalhes.setWidth("100%");
                				    				btClickDetalhes.setStyleName(Reindeer.BUTTON_LINK);
                				    				btClickDetalhes.addStyleName("normalizarLinkRoteirizacoes");
                				    				
                				    				
                				    				addComponent(btClickDetalhes); 
                			    				}
                			    			};
                			    			vlOsAtribuida.setStyleName("registros");
                			    			addComponent(vlOsAtribuida);
                			    			
                						}
                					};    	
                					hlInfo1.setStyleName("topo-roteirizacoes-registros");
                					hlInfo1.setWidth("100%");
                					addComponent(hlInfo1); 
                	    	}
                	    	
                	    	
                	    	HorizontalLayout hlRodapeQtdRegistros = new HorizontalLayout();
                	    	hlRodapeQtdRegistros.setWidth("100%");
                	    	
                	    	Query qQtdSemAtribui = em.createNativeQuery("SELECT * FROM `ose` WHERE `DATA_EX` =:dt and `STATUS_2` != 'FECHADO' AND `STATUS_2` != 'CONCLUIDO' AND `EQUIPE` IS NULL");
                	    	qQtdSemAtribui.setParameter("dt",sdf.format(dt2.getValue()));
                	    	
                	    	Label lbRegistros = new Label("<strong>"+String.valueOf(qQtdSemAtribui.getResultList().size())+"</strong> OS não atribuídas.",ContentMode.HTML);
                	    	lbRegistros.setWidth("145px");
                	    	
                	    	hlRodapeQtdRegistros.addComponent(lbRegistros); 
                	    	hlRodapeQtdRegistros.setComponentAlignment(lbRegistros, Alignment.BOTTOM_RIGHT);
                	    	
                	    	addComponent(hlRodapeQtdRegistros);
                			}
                		};                	
                		vLPri.setWidth("100%");                		
                		
                	
                		addComponent(vLPri);                		
                		setExpandRatio(vLPri, 1f);
                	}
                });
    }

    private Table buildTbView(boolean salesOnly){
    	
    	
    	
    	tbPLan = new Table();
    	
    	tbPLan.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
			
				Item item = source.getItem(itemId); 
				
				if(item.getItemProperty("CRMs").getValue().toString().equals("AGENDADO")){
					return "row-header-crm-resumo-default";	   
				}				
				
				if(item.getItemProperty("CRMs").getValue().toString().equals("EFETUADOS")){
					return "row-header-crm-resumo-em-tratamento";  
				}
				
				if(item.getItemProperty("CRMs").getValue().toString().equals("PENDENTES")){
					return "row-header-crm-resumo-atrasado";   
				}
				
				
				return "row-header-default";	   
				
			}
		});
    	
    	tbPLan.setSelectable(true); 
    	tbPLan.setCaption("Resumo de CRMs");
    	
    	EntityManager em = ConnUtil.getEntity();
        
        Query q;
             
         if(cbSetor.getValue() != null){             
        	 EntityItem<Setores> entitySetores = (EntityItem<Setores>)cbSetor.getItem(cbSetor.getValue()); 
        	 Setores setor = entitySetores.getEntity();
        	 
	        q = em.createQuery("select crm from Crm crm where crm.data_agendado=:dataHoje and crm.empresa_id =:empresa and crm.setor=:setor", Crm.class);
	        q.setParameter("dataHoje", new Date());
	        q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
	        q.setParameter("setor",setor);
         }else{
        	q = em.createQuery("select crm from Crm crm where crm.data_agendado=:dataHoje and crm.empresa_id =:empresa", Crm.class);
 	        q.setParameter("dataHoje", new Date());
 	        q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
         }
	    
        tbPLan.addStyleName("ajust-tb-plan");
        tbPLan.setWidth("100%");
        tbPLan.setHeight("100%");
        
        tbPLan.addContainerProperty("CRMs", String.class,"");
        tbPLan.addContainerProperty("Totais", Integer.class,"");
        
        tbPLan.setColumnWidth("Totais", 60);
        
        
        Integer qtd_total = q.getResultList().size(), qtd_tratados = 0, qtd_pendentes = 0;
        for (Crm crm: (List<Crm>)q.getResultList()) {
			
        	if(crm.getStatus().equals("AGENDADO")){
        		qtd_pendentes = qtd_pendentes+1;
        	}
        	
        	if(crm.getStatus().equals("EFETUADO")){
        		qtd_tratados = qtd_tratados+1;
        	}
		}
        
        
        tbPLan.addItem(new Object[]{"AGENDADOS", qtd_total}, 1);
        tbPLan.addItem(new Object[]{"EFETUADOS", qtd_tratados}, 2);
        tbPLan.addItem(new Object[]{"PENDENTES", qtd_pendentes}, 3);
        
        
        return tbPLan;
    }
    
}
