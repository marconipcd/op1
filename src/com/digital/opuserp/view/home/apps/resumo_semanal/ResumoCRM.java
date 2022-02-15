package com.digital.opuserp.view.home.apps.resumo_semanal;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.dao.CrmDAO;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.crm.historico.HistoricoView;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.charts.Chart;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class ResumoCRM extends CssLayout {
	
	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;    
  
    private VerticalLayout vLPri;    
  
    private Table tb;
    
    public ResumoCRM() {
    	
    	addStyleName("layout-panel");
        setSizeFull();
        
        buildLayout();
    }
    
   
    
    private void buildLayout(){
    	removeAllComponents();
    	
    	addComponent(buildBtrefresh());        
    	addComponent(buildBtMaximize());    	
        addComponent(buildBtClose());   
        
        
        //addComponent(buildCbSetor());
        //addComponent(buildTbView(true));
        
        
        addComponent(new HorizontalLayout(){
        	{
        		addStyleName("barra-botao-resumo-crm");
        		setSpacing(false);
        		setMargin(false);
        		setWidth("100%");
        		addComponent(BuildbtHistorico());
        		setComponentAlignment(btHistorico, Alignment.MIDDLE_RIGHT);
        	}
        });
        
        EntityManager em = ConnUtil.getEntity();
        Query q = em.createQuery("select c from Crm c where c.data_agendado >=:dt1 and c.data_agendado <=:dt2 and c.setor =:setor", Crm.class);
        q.setParameter("setor", new Setores(18));
        q.setParameter("dt1", new DateTime().toDate());
        q.setParameter("dt2", new DateTime().plusDays(5).toDate());
        
        tb = new Table();
        tb.setSizeFull();
        tb.setSelectable(true);
        tb.setCaption("CRM ");
        tb.addContainerProperty("Cod", Integer.class, null);
        tb.addContainerProperty("Cliente", String.class, null);
        tb.addContainerProperty("Qtd", Integer.class, null);
        
        tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(tb.getValue() != null){
					btHistorico.setEnabled(true);
				}else{
					btHistorico.setEnabled(false);
				}
			}
		});
        
        for (Crm crm : (List<Crm>)q.getResultList()) {
			Query q2 = em.createQuery("select c from Crm c where c.setor=:setor and c.cliente=:cliente and "
					+ "c.contrato=:contrato and c.data_agendado >=:dt1 and c.data_agendado <=:dt2", Crm.class);
			q2.setParameter("setor", crm.getSetor());
			q2.setParameter("cliente", crm.getCliente());
			q2.setParameter("contrato", crm.getContrato());
			q2.setParameter("dt1", new DateTime().minusMonths(12).toDate());
		    q2.setParameter("dt2", new DateTime().toDate());
			
			
			if(q2.getResultList().size() > 1){
				tb.addItem(new Object[]{crm.getId(), crm.getCliente().getNome_razao(), q2.getResultList().size()}, tb.getItemIds().size()+1);
			}
		}
        
        addComponent(tb); 
    }
    
    Button btHistorico;
    private Button BuildbtHistorico() {
		btHistorico = new Button("Histórico", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
								
					
					if(tb.getValue() != null){
						
							HistoricoView historicoView;
							
							if(tb.getValue() != null){
								
								Integer codCRM = (Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue();
								Crm crm = CrmDAO.find(codCRM);
								historicoView = new HistoricoView(true, true, crm.getCliente().getId(), crm.getSetor(), crm.getContrato());

								getUI().addWindow(historicoView);
							}
											
							
						
					}
				
				
			}
			
		});
		btHistorico.setEnabled(false);
		return btHistorico;
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
                 //((CssLayout)btRefresh.getParent()).replaceComponent(tbPLan, buildTbView(true));
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
            	  PrefeDashDAO.remove(ResumoCRM.class.toString());
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
                		//vLPri.addComponent(buildTbView(true));
                	
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

   
}
