package com.digital.opuserp.view.home.apps.monitor;

import com.digital.opuserp.dao.PrefeDashDAO;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class MonitorRedesApp extends CssLayout {

	private Button btMaximize;
	 private Button btClose;
	
	public MonitorRedesApp(){
		addStyleName("layout-panel");
        setSizeFull();
        
        buildLayout();
    }
    private void buildLayout(){
    	
    	 removeAllComponents();
    	
    	 
         addComponent(buildBtMaximize());
         addComponent(buildBtClose());
         
//         addComponent(new HorizontalLayout(){
//        	 {
//        		 //setWidth("100%");
//
//        		 addComponent(buildCbSeletor(false));
//        		 addComponent(buildCbPeriodicidade());
//        	 }
//         });
         
         
         addComponent(buildBrowser());
        
    }
    
    private BrowserFrame buildBrowser(){
    	
    	BrowserFrame bf = new BrowserFrame("Monitor de Redes", new ExternalResource("http://172.17.0.15/cacti/plugins/monitor/monitor.php"));
    	bf.setSizeFull();
    	
    	
    	return bf;
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
                Window winMaximize = new Window("Monitor de Redes");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");
                winMaximize.setContent(new VerticalLayout(){
                	{
                		setSizeFull();
                		setMargin(true); 
                		setSpacing(true);
                		addComponent(buildBrowser());
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
            	  PrefeDashDAO.remove(MonitorRedesApp.class.toString());
                  ((GridLayout)getParent()).removeComponent(btClose.getParent());   
            }
        });
        
        return btClose;
    }
}
