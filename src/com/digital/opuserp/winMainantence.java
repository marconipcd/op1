package com.digital.opuserp;

//import com.github.wolfie.refresher.Refresher;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class winMainantence extends Window {

	Label lbMaintenance;
	Integer timeMaintenance = 30;
	VerticalLayout vlRoot;
	public winMainantence(){
		
		super("Aviso de Manutenção!");
		//center();
		
		setResizable(false);
		addStyleName("maintenance");
		setWidth("280px");
		setHeight("165px");
		setClosable(false);
				
		setPositionX(OpusERP4UI.getCurrent().getPage().getBrowserWindowWidth()-290);
		setPositionY(OpusERP4UI.getCurrent().getPage().getBrowserWindowHeight()-140);
				
		lbMaintenance = new Label("O Opus será Reiniciado em <strong>"+timeMaintenance.toString()+"</strong> Segundos", ContentMode.HTML);
		lbMaintenance.addStyleName("maintenance");
		
//		Refresher refresher = new Refresher();
//		refresher.setRefreshInterval(1000);
//		refresher.addListener(new Refresher.RefreshListener() {
//			
//			@Override
//			public void refresh(Refresher source) {	
//				if(timeMaintenance > 0){
//					timeMaintenance = timeMaintenance-1;
//					lbMaintenance = new Label("O Opus será Reiniciado em <strong>"+timeMaintenance.toString()+"</strong> Segundos", ContentMode.HTML);
//					lbMaintenance.addStyleName("maintenance");
//					vlRoot.removeAllComponents();
//					vlRoot.addComponent(lbMaintenance); 
//				}
//			}
//		});
		
		vlRoot = new VerticalLayout();
		vlRoot.setSizeFull();
		vlRoot.addComponent(lbMaintenance); 
		setContent(vlRoot);
		
		//addExtension(refresher); 
	}
}
