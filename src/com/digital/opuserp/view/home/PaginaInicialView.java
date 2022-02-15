package com.digital.opuserp.view.home;


import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.dao.SubModuloDAO;
import com.digital.opuserp.domain.PreferenciasDashboard;
import com.digital.opuserp.domain.SubModuloUsuario;
import com.digital.opuserp.view.home.apps.atendimentos.AtendimentosVisaoGeral;
import com.digital.opuserp.view.home.apps.atualizacoes.AtualizacoesSoftApp;
import com.digital.opuserp.view.home.apps.calculator.CalcApp;
import com.digital.opuserp.view.home.apps.charts.ComoNosConheceuChart;
import com.digital.opuserp.view.home.apps.charts.ContratosAcessoChart;
import com.digital.opuserp.view.home.apps.charts.PedidoChart;
import com.digital.opuserp.view.home.apps.charts.PesquisaSatisfacaoChart;
import com.digital.opuserp.view.home.apps.charts.RoteirizacaoChart;
import com.digital.opuserp.view.home.apps.charts.Top100ClientesChart;
import com.digital.opuserp.view.home.apps.charts.Top100ConsumoChart;
import com.digital.opuserp.view.home.apps.charts.Top100GrupoServicosChart;
import com.digital.opuserp.view.home.apps.charts.Top100GruposChart;
import com.digital.opuserp.view.home.apps.charts.Top100ProdutosChart;
import com.digital.opuserp.view.home.apps.charts.Top100ServicosChart;
import com.digital.opuserp.view.home.apps.indicacoes.IndicacoesVisaoGeral;
import com.digital.opuserp.view.home.apps.indicadores.IndicadoresApp;
import com.digital.opuserp.view.home.apps.monitor.MonitorRedesApp;
import com.digital.opuserp.view.home.apps.notepad.NotepadApp;
import com.digital.opuserp.view.home.apps.plano_acao.PlanoAcaoApp;
import com.digital.opuserp.view.home.apps.resumo_semanal.ResumoCRM;
import com.digital.opuserp.view.home.apps.roteirizacoes.RoteirizacoesVisaoGeral;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PaginaInicialView extends VerticalLayout implements View {

    Table t;
    HorizontalLayout hlButtonsTop;
    Button btNotifcacoes;
    Button btApps;
    TextArea notes;
    GridLayout gridLayout;
    List<SubModuloUsuario> subModulos;
    
    public PaginaInicialView() {
        setSizeFull();
        addStyleName("dashboard-view");

        //Toolbar
        hlButtonsTop = buildToolbar();
                
        //HorizontalLayout hlBusca = new HorizontalLayout();
        //hlBusca.addComponent(new SuggestSearch());
       
        //addComponent(hlBusca);  
        addComponent(hlButtonsTop);

        gridLayout = new GridLayout(3, 2);
        gridLayout.setSizeFull();
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);        
        
        addComponent(gridLayout);
        setExpandRatio(gridLayout, 1f);
        
        subModulos = SubModuloDAO.getSubModulosByUsuarioPorModulo(22);	
        List<PreferenciasDashboard> apps = PrefeDashDAO.getApps();
        for (PreferenciasDashboard p : apps) {
        	
        	Component c; 
        	
        	if(p.getApp().equals(PedidoChart.class.toString()) && checkPermission("Gráfico Evolução de Vendas")){
        		c = new PedidoChart();
        	}else if(p.getApp().equals(NotepadApp.class.toString())  && checkPermission("Bloco de Notas")){
        		c = new NotepadApp();
        	}else if(p.getApp().equals(ComoNosConheceuChart.class.toString())  && checkPermission("Gráfico Como nos Conheceu")){
        		c = new ComoNosConheceuChart();
        	}else if(p.getApp().equals(CalcApp.class.toString())  && checkPermission("Calculadora")){
        		c = new CalcApp();
        	}else if(p.getApp().equals(PesquisaSatisfacaoChart.class.toString())  && checkPermission("Gráfico Pesquisa de Satisfação")){
        		c = new PesquisaSatisfacaoChart();
        	}else if(p.getApp().equals(RoteirizacaoChart.class.toString())  && checkPermission("Gráfico Roteirização")){
        		c = new RoteirizacaoChart();
        	}else if(p.getApp().equals(ContratosAcessoChart.class.toString())  && checkPermission("Gráfico Contratos de Acesso")){
        		c = new ContratosAcessoChart();
        	}else if(p.getApp().equals(Top100ProdutosChart.class.toString())  && checkPermission("Gráfico TOP Vendas Produtos")){
        		c = new Top100ProdutosChart();
        	}else if(p.getApp().equals(Top100ServicosChart.class.toString())  && checkPermission("Gráfico TOP Vendas Seviços")){
        		c = new Top100ServicosChart();
        	}else if(p.getApp().equals(Top100ClientesChart.class.toString())  && checkPermission("Gráfico TOP Clientes")){
        		c = new Top100ClientesChart();
        	}else if(p.getApp().equals(Top100GruposChart.class.toString())  && checkPermission("Gráfico TOP Vendas Grupo de Produtos")){
        		c = new Top100GruposChart();
        	}else if(p.getApp().equals(Top100GrupoServicosChart.class.toString())  && checkPermission("Gráfico TOP Vendas Grupo de Serviços")){
        		c = new Top100GrupoServicosChart();
        	}else if(p.getApp().equals(Top100ConsumoChart.class.toString())  && checkPermission("Gráfico TOP Consumo")){
        		c = new Top100ConsumoChart();
        	}else if(p.getApp().equals(MonitorRedesApp.class.toString())  && checkPermission("Monitor de Redes")){
        		c = new MonitorRedesApp();
        	}else if(p.getApp().equals(ResumoCRM.class.toString())  && checkPermission("Resumo CRM")){
        		c = new ResumoCRM();
        	}else if(p.getApp().equals(RoteirizacoesVisaoGeral.class.toString())  && checkPermission("Roteirizacoes Visao Geral")){
        		c = new RoteirizacoesVisaoGeral();
        	}else if(p.getApp().equals(IndicacoesVisaoGeral.class.toString())  && checkPermission("Indicacoes Visao Geral")){
        		c = new IndicacoesVisaoGeral();
        	}else if(p.getApp().equals(AtualizacoesSoftApp.class.toString())  && checkPermission("Atualizacoes Soft App")){
        		c = new AtualizacoesSoftApp();
        	}else if(p.getApp().equals(AtendimentosVisaoGeral.class.toString())  && checkPermission("Atendimentos")){
        		c = new AtendimentosVisaoGeral();
        	}else if(p.getApp().equals(PlanoAcaoApp.class.toString())  && checkPermission("Plano de Acao")){
        		c = new PlanoAcaoApp();
        	}else if(p.getApp().equals(IndicadoresApp.class.toString())  && checkPermission("Indicadores")){
        		c = new IndicadoresApp();
        	}else{
        		c = null;
        	}
        	
        	if(c != null){
	        	if(gridLayout.getComponent(0, 0) == null){        		
					gridLayout.addComponent(c,0,0);
					PrefeDashDAO.add(c.getClass().toString());
				}else if(gridLayout.getComponent(1, 0) == null){
					gridLayout.addComponent(c, 1, 0);
					PrefeDashDAO.add(c.getClass().toString());
				}else if(gridLayout.getComponent(2, 0) == null){
					gridLayout.addComponent(c, 2, 0);
					PrefeDashDAO.add(c.getClass().toString());
				}else if(gridLayout.getComponent(0, 1) == null){
					gridLayout.addComponent(c, 0, 1);
					PrefeDashDAO.add(c.getClass().toString());
				}else if(gridLayout.getComponent(1, 1) == null){
					gridLayout.addComponent(c, 1, 1);
					PrefeDashDAO.add(c.getClass().toString());
				}else if(gridLayout.getComponent(2, 1) == null){
					gridLayout.addComponent(c, 2, 1);
					PrefeDashDAO.add(c.getClass().toString());
				}
        	}
		}
    }
    
    private boolean checkPermission(String nomeSubmodulo){
    	
    	for (SubModuloUsuario sub : subModulos) {
			if(sub.getSubmodulo().getNome().equals(nomeSubmodulo))
			{
				return true;				
			}
				
		}
    	
    	if(nomeSubmodulo.equals("Bloco de Notas")){
    		PrefeDashDAO.remove(NotepadApp.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico Evolução de Vendas")){
			PrefeDashDAO.remove(PedidoChart.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico Como nos Conheceu")){
			PrefeDashDAO.remove(ComoNosConheceuChart.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico Pesquisa de Satisfação")){
			PrefeDashDAO.remove(PesquisaSatisfacaoChart.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico Roteirização")){
			PrefeDashDAO.remove(RoteirizacaoChart.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico Contratos de Acesso")){
			PrefeDashDAO.remove(ContratosAcessoChart.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico TOP Vendas Produtos")){
			PrefeDashDAO.remove(Top100ProdutosChart.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico TOP Vendas Seviços")){
			PrefeDashDAO.remove(Top100ServicosChart.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico TOP Clientes")){
			PrefeDashDAO.remove(Top100ClientesChart.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico TOP Vendas Grupo de Produtos")){
			PrefeDashDAO.remove(Top100GruposChart.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico TOP Vendas Grupo de Serviços")){
			PrefeDashDAO.remove(Top100GrupoServicosChart.class.toString());
		}else if(nomeSubmodulo.equals("Calculadora")){			
			PrefeDashDAO.remove(CalcApp.class.toString());
		}else if(nomeSubmodulo.equals("Gráfico TOP Consumo")){			
			PrefeDashDAO.remove(CalcApp.class.toString());
		}else if(nomeSubmodulo.equals("Monitor Redes")){			
			PrefeDashDAO.remove(MonitorRedesApp.class.toString());
		}else if(nomeSubmodulo.equals("Resumo CRM")){			
			PrefeDashDAO.remove(ResumoCRM.class.toString());
		}else if(nomeSubmodulo.equals("Roteirizacoes Visao Geral")){			
			PrefeDashDAO.remove(RoteirizacoesVisaoGeral.class.toString());
		}else if(nomeSubmodulo.equals("Indicações Visao Geral")){			
			PrefeDashDAO.remove(IndicacoesVisaoGeral.class.toString());
		}else if(nomeSubmodulo.equals("Atualizacoes Soft App")){			
			PrefeDashDAO.remove(AtualizacoesSoftApp.class.toString());
		}else if(nomeSubmodulo.equals("Atendimentos")){			
			PrefeDashDAO.remove(AtendimentosVisaoGeral.class.toString());
		}else if(nomeSubmodulo.equals("Plano de Acao")){			
			PrefeDashDAO.remove(PlanoAcaoApp.class.toString());
		}else if(nomeSubmodulo.equals("Indicadores")){			
			PrefeDashDAO.remove(IndicadoresApp.class.toString());
		}
    	
    	
    	    	
    	return false;
    }

    private HorizontalLayout buildToolbar(){
    	 hlButtonsTop = new HorizontalLayout();
         hlButtonsTop.setWidth("100%");
         hlButtonsTop.setSpacing(true);
         hlButtonsTop.addStyleName("toolbar");
         
         String nomeUsuarioLogado = OpusERP4UI.getUsuarioLogadoUI().getUsername().toString().substring(0, 1).toUpperCase()+
        		 OpusERP4UI.getUsuarioLogadoUI().getUsername().toString().substring(1, OpusERP4UI.getUsuarioLogadoUI().getUsername().length());
         
         final Label title = new Label("Olá, "+nomeUsuarioLogado+" Bom Trabalho!");
         title.setSizeUndefined();
         title.addStyleName("h1");
         hlButtonsTop.addComponent(title);
         hlButtonsTop.setComponentAlignment(title, Alignment.MIDDLE_LEFT);
         hlButtonsTop.setExpandRatio(title, 1);
         
        // hlButtonsTop.addComponent(buildButtonNofity());
         //hlButtonsTop.setComponentAlignment(btNotifcacoes, Alignment.MIDDLE_LEFT);
         
         hlButtonsTop.addComponent(buildButtonApp());
         hlButtonsTop.setComponentAlignment(btApps, Alignment.MIDDLE_LEFT);
         
         return hlButtonsTop;
    }
    private Button buildButtonNofity(){
    	btNotifcacoes = new Button("2");
        btNotifcacoes.setDescription("Notificações (2 não lidas)");
        btNotifcacoes.setEnabled(false);
        btNotifcacoes.addStyleName("notifications");
        btNotifcacoes.addStyleName("unread");
        btNotifcacoes.addStyleName("icon-only");
        btNotifcacoes.addStyleName("icon-bell");
        btNotifcacoes.addClickListener(new ClickListener() {
            
        	@Override
            public void buttonClick(ClickEvent event) {
               // ((DashboardUI) getUI()).clearDashboardButtonBadge();
                event.getButton().removeStyleName("unread");
                event.getButton().setDescription("Notificações");

                if (notifications != null && notifications.getUI() != null)
                    notifications.close();
                else {
                    buildNotifications(event);
                    getUI().addWindow(notifications);
                    notifications.focus();
                    ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
                                
                    			@Override
                                public void layoutClick(LayoutClickEvent event) {
                                    notifications.close();
                                    ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
                                }
                            });
                }

            }
        });
        
        return btNotifcacoes;
    }
    
    private Button buildButtonApp(){
    	 btApps = new Button();
         btApps.setDescription("DeskApps");
         btApps.setEnabled(true);
         btApps.addStyleName("notifications");        
         btApps.addStyleName("icon-only");
         //btApps.addStyleName("icon-bell");
         btApps.setIcon(new ThemeResource("icons/icon_app.png"));
         btApps.addClickListener(new ClickListener() {
             
         	@Override
             public void buttonClick(ClickEvent event) {
               
                 if (apps_win != null && apps_win.getUI() != null)
                 	apps_win.close();
                 else {
                     buildMenuApps(event);
                     getUI().addWindow(apps_win);
                     apps_win.focus();
                     ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
                                 
                     			@Override
                                 public void layoutClick(LayoutClickEvent event) {
                     				apps_win.close();
                                     ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
                                 }
                             });
                 }

             }
         });
         return btApps;
    }
    
    
   

   

    @Override
    public void enter(ViewChangeEvent event) {
       // DataProvider dataProvider = ((DashboardUI) getUI()).dataProvider;
        //t.setContainerDataSource(dataProvider.getRevenueByTitle());
    }

    

    
    Window apps_win;
    private void buildMenuApps(ClickEvent event) {
    	
    	apps_win = new Window("Apps Disponíveis");
        VerticalLayout l = new VerticalLayout(){
        	{
        		for(final SubModuloUsuario s: subModulos){    
                	   
                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();  
                	
                			final Button bt = new Button(s.getSubmodulo().getNome(), new ClickListener() {
	        					
	        					@Override
	        					public void buttonClick(ClickEvent event) {
	        						Component componentApp;
	        						
	        						if(s.getSubmodulo().getNome().equals("Bloco de Notas")){
	                        			componentApp = new NotepadApp();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico Evolução de Vendas")){
	                        			componentApp = new PedidoChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico Como nos Conheceu")){
	                        			componentApp = new ComoNosConheceuChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico Pesquisa de Satisfação")){
	                        			componentApp = new PesquisaSatisfacaoChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico Roteirização")){
	                        			componentApp = new RoteirizacaoChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico Contratos de Acesso")){
	                        			componentApp = new ContratosAcessoChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico TOP Vendas Produtos")){
	                        			componentApp = new Top100ProdutosChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico TOP Vendas Seviços")){
	                        			componentApp = new Top100ServicosChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico TOP Clientes")){
	                        			componentApp = new Top100ClientesChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico TOP Vendas Grupo de Produtos")){
	                        			componentApp = new Top100GruposChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico TOP Vendas Grupo de Serviços")){
	                        			componentApp = new Top100GrupoServicosChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Gráfico TOP Consumo")){
	                        			componentApp = new Top100ConsumoChart();
	                        		}else if(s.getSubmodulo().getNome().equals("Calculadora")){
	                        			componentApp = new CalcApp();
	                        		}else if(s.getSubmodulo().getNome().equals("Monitor de Redes")){
	                        			componentApp = new MonitorRedesApp();
	                        		}else if(s.getSubmodulo().getNome().equals("Resumo CRM")){
	                        			componentApp = new ResumoCRM();
	                        		}else if(s.getSubmodulo().getNome().equals("Roteirizacoes Visao Geral")){
	                        			componentApp = new RoteirizacoesVisaoGeral();
	                        		}else if(s.getSubmodulo().getNome().equals("Indicacoes Visao Geral")){
	                        			componentApp = new IndicacoesVisaoGeral();
	                        		}else if(s.getSubmodulo().getNome().equals("Atualizacoes Soft App")){
	                        			componentApp = new AtualizacoesSoftApp();
	                        		}else if(s.getSubmodulo().getNome().equals("Atendimentos")){
	                        			componentApp = new AtendimentosVisaoGeral();
	                        		}else if(s.getSubmodulo().getNome().equals("Plano de Acao")){
	                        			componentApp = new PlanoAcaoApp();
	                        		}else if(s.getSubmodulo().getNome().equals("Indicadores")){
	                        			componentApp = new IndicadoresApp();
	                        		}else{
	                        			componentApp = null;
	                        		}
	        						
	        						
	        						if(gridLayout.getComponentCount() < 6){
	        							
	        							String classeString = componentApp.getClass().toString();
	        							
	        							if(PrefeDashDAO.allow(classeString)){	
	        								if(gridLayout.getComponent(0, 0) == null){
	        									gridLayout.addComponent(componentApp,0,0);
	        									PrefeDashDAO.add(classeString);
	        								}else if(gridLayout.getComponent(1, 0) == null){
	        									gridLayout.addComponent(componentApp, 1, 0);
	        									PrefeDashDAO.add(classeString);
	        								}else if(gridLayout.getComponent(2, 0) == null){
	        									gridLayout.addComponent(componentApp, 2, 0);
	        									PrefeDashDAO.add(classeString);
	        								}else if(gridLayout.getComponent(0, 1) == null){
	        									gridLayout.addComponent(componentApp, 0, 1);
	        									PrefeDashDAO.add(classeString);
	        								}else if(gridLayout.getComponent(1, 1) == null){
	        									gridLayout.addComponent(componentApp, 1, 1);
	        									PrefeDashDAO.add(classeString);
	        								}else if(gridLayout.getComponent(2, 1) == null){
	        									gridLayout.addComponent(componentApp, 2, 1);
	        									PrefeDashDAO.add(classeString);
	        								}else{
	        									Notify.Show("Não há Espaço para Novos Aplicativos...",  Notify.TYPE_WARNING);
	        									Notify.Show("Você deve fechar alguns dos Aplicativos Abertos antes de abrir outro.",  Notify.TYPE_WARNING);
	        								}
	        							}
	        						}else{
	        							Notify.Show("Não há Espaço para Novos Aplicativos...",  Notify.TYPE_WARNING);
	        							Notify.Show("Você deve fechar alguns dos Aplicativos Abertos antes de abrir outro.",  Notify.TYPE_WARNING);
	        						}
	        						
	        						apps_win.close();
	        					}
	        				});
	                		bt.addStyleName("borderless");
	                		bt.addStyleName("small");        		
	                		//bt.setIcon(new ThemeResource(s.getSubmodulo().getIcon()));
	                		bt.setDescription(s.getSubmodulo().getDescricao());
	                		
	                		HorizontalLayout hlbt = new HorizontalLayout(){
	                			{
	                				Image img;
	                				
	                				
	                				//if(PrefeDashDAO.appExists(componentApp.getClass().toString())){
	                				//	 img = new Image(null, new ThemeResource("icons/check-32.png"));	                					
	                				//}else{
	                					 img = new Image(null, new ThemeResource("icons/check-32-inative.png"));
	                				//}               				
	                				
	                				addComponent(img); 
	                				addComponent(bt);
	                			}
	                		};
	                		
	                		addComponent(hlbt); 
	                		
                		//}
                	//}
                 }    
        		
        		
        		
        		
        	
        		
        		
        		
        	}
        };
        l.setMargin(true);
        l.setSpacing(true);
        
        apps_win.setContent(l);
        apps_win.setWidth("450px");
        apps_win.addStyleName("notifications");
        apps_win.setClosable(false);
        apps_win.setResizable(false);
        apps_win.setDraggable(false);
        apps_win.setPositionX(event.getClientX() - event.getRelativeX());
        apps_win.setPositionY(event.getClientY() - event.getRelativeY());
        apps_win.setCloseShortcut(KeyCode.ESCAPE, null);
               
    }
    
    Window notifications;
    private void buildNotifications(ClickEvent event) {
        notifications = new Window("Notificações");
        VerticalLayout l = new VerticalLayout();
        l.setMargin(true);
        l.setSpacing(true);
        notifications.setContent(l);
        notifications.setWidth("300px");
        notifications.addStyleName("notifications");
        notifications.setClosable(false);
        notifications.setResizable(false);
        notifications.setDraggable(false);
        notifications.setPositionX(event.getClientX() - event.getRelativeX());
        notifications.setPositionY(event.getClientY() - event.getRelativeY());
        notifications.setCloseShortcut(KeyCode.ESCAPE, null);

        Label label = new Label(
                "<hr><b> Um novo Chamado foi Aberto"                        
                        + "</b><br><span>25 minutos atrás</span><br>", ContentMode.HTML);
        l.addComponent(label);

       
    }

}
