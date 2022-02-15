package com.digital.opuserp.view.home;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PrintLogDAO;
import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.domain.PrintLog;
import com.digital.opuserp.util.ConnUtil;
import com.github.wolfie.desktopnotifications.DesktopNotifier;
//import com.github.wolfie.refresher.Refresher;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class PaginaInicialAdminErrorView extends VerticalLayout{
	
	
	JPAContainer<LogError> logs;
	Table tbLogs;
	TextField tfBusca;
	
	Button btAtualizar;
	Button btRemover;
	
	//Refresher refresher;
	
	public PaginaInicialAdminErrorView(){
		
		setSizeFull();
		
		//addComponent(buildTextFieldBusca());
		
		HorizontalLayout hlButtons = new HorizontalLayout();
		hlButtons.addComponent(buildButtonRefresh());
		hlButtons.addComponent(buildButtonRemover());
		
		addComponent(hlButtons);
		addComponent(buildTbLogs());
		
		setExpandRatio(tbLogs, 1);
		
		//addExtension(buildRefresher());		
	}
	
	private Button buildButtonRemover(){
		btRemover = new Button("Deletar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final Set<Object> selecteds = (Set<Object>)tbLogs.getValue();
				
				EntityManager em = ConnUtil.getEntity();
				em.getTransaction().begin();	
				for (Object object : selecteds) {
																	
																	
						final LogError cr = em.find(LogError.class, Integer.parseInt(tbLogs.getItem(object).getItemProperty("id").getValue().toString()));
						
						if(cr != null){			
							
							//Verifica se Tem Screen
							Query q = em.createQuery("select p from PrintLog p where p.log = :log", PrintLog.class);
							q.setParameter("log", cr);
							
							if(q.getResultList().size() > 0){
								
								for(PrintLog p : (List<PrintLog>)q.getResultList()){
									em.remove(p);
								}
							}
							
							em.remove(cr);							
						}
				}
				em.getTransaction().commit();
			}
		});
		
		btRemover.setEnabled(false);
		
		return btRemover;
	}
	
	//private  final DesktopNotifier c = new DesktopNotifier();
	private Button buildButtonRefresh(){
		btAtualizar = new Button("Refresh", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				replaceComponent(tbLogs, buildTbLogs());	
				setExpandRatio(tbLogs, 1);
				
				//c.requestPermission();
				
				//OpusERP4UI.getNotify().showHtmlNotification(new ThemeResource("notification.html"));
				//OpusERP4UI.getNotify().showNotification(new ThemeResource("icon.png"), "Header", "body");
			}
		});
		
		return btAtualizar;
	}
//	public Refresher buildRefresher(){
//		refresher = new Refresher();
//		refresher.setRefreshInterval(4000);
//		refresher.addListener(new Refresher.RefreshListener() {
//			
//			@Override
//			public void refresh(Refresher source) {
//				replaceComponent(tbLogs, buildTbLogs());
//				setExpandRatio(tbLogs, 1);
//			}
//		});
//		
//		return refresher;
//	}
	
	private JPAContainer<LogError> buildJpaContainer(){
		
		logs = JPAContainerFactory.make(LogError.class, ConnUtil.getEntity());
		logs.addNestedContainerProperty("usuario.username");
		
		//if(tfBusca.getValue() != null || !tfBusca.getValue().isEmpty()){
		//	addFilter(tfBusca.getValue());
		//}
		
		logs.sort(new Object[]{"id"}, new boolean[]{false});
		
		
		
		return logs;
	}
	
	public void writeFile(File file, byte[] data) throws IOException
	 {
	   OutputStream fo = new FileOutputStream(file);
	   // Write the data
	   fo.write(data);
	   // flush the file (down the toilet)
	   fo.flush();
	   // Close the door to keep the smell in.
	   fo.close();
	 }
	
	private Table buildTbLogs(){
		tbLogs = new Table(null,buildJpaContainer());
		
		tbLogs.setSelectable(true);
		tbLogs.setMultiSelect(true);
		tbLogs.setImmediate(true);
		tbLogs.addGeneratedColumn("screen", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source,final Object itemId, Object columnId) {
				
				Button btLinkScreen = new Button("Visualizar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						PrintLogDAO printDAO = new PrintLogDAO();
						PrintLog screen = printDAO.getScreenByLog(Integer.parseInt(source.getItem(itemId).getItemProperty("id").toString()));
						
						if(screen != null){
							try{
								
								
								
								Window winScreen = new Window("PrintScreen do ERRO!");
								winScreen.setHeight("598px");
								winScreen.setWidth("1020px");
								winScreen.center();
								winScreen.setModal(true); 
								
								String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
								File file = new File(basepath + "/WEB-INF/uploads/logo.jpeg");
								
							    writeFile(file, (byte[]) screen.getScreen());
							    Image print = new Image(null, new FileResource(file));
							    print.setSizeFull();
							    
							    winScreen.setContent(print);
							    
							    getUI().addWindow(winScreen);
							    
							    
							}catch(Exception e){
								
								
								e.printStackTrace();
							}
						
						}
						
					}
				});
				
				btLinkScreen.setStyleName(BaseTheme.BUTTON_LINK);
				
				
				return btLinkScreen;
			}
		});
		
		tbLogs.setVisibleColumns(new Object[]{"id","classe","funcao","detalhes","usuario.username","data","screen"});
		tbLogs.setColumnHeader("usuario.username", "Usuário");
		
		
		tbLogs.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(tbLogs.getValue() != null){				
					btRemover.setEnabled(true);
				}else{
					btRemover.setEnabled(false);
				}
			}
		});
		
		tbLogs.setSizeFull();
		
		
		
		
//		tbLogs.setVisibleColumns(new Object[]{"operador","acao","data_cadastro"});
//
//		tbLogs.setColumnHeader("id", "COD");
//		tbLogs.setColumnHeader("operador", "Operador");
//		tbLogs.setColumnHeader("acao", "Ação");
//		tbLogs.setColumnHeader("data_cadastro", "Data");
				
		
		return tbLogs;
	}
	
	private TextField buildTextFieldBusca(){
		tfBusca = new TextField();
		tfBusca.setWidth("100%");		
		tfBusca.setTextChangeEventMode(TextChangeEventMode.EAGER);
		tfBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				
				if(!event.getText().isEmpty() && event.getText() != null){
					//addFilter(event.getText());
				}
			}
		});
		
		return tfBusca;
	}
	
	private void addFilter(String s){
		logs.removeAllContainerFilters();
		
		if(tbLogs != null){
			Object[] collums = tbLogs.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			for(Object c:collums){		 
				
				if(!tbLogs.isColumnCollapsed(c.toString()) && logs.getType(c.toString()) == String.class){					   	
					filtros.add(Filters.like(c.toString(), "%"+s+"%", false));
				}			
			}
		
			logs.addContainerFilter(Filters.or(filtros));
		}
		
	}
}