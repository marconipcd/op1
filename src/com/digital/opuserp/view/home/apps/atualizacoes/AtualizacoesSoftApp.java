package com.digital.opuserp.view.home.apps.atualizacoes;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AtualizacaoSoftDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.AtualizacoesSoftware;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.github.wolfie.refresher.Refresher;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Video;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class AtualizacoesSoftApp   extends CssLayout {
	
    private Button btMaximize;
    private Button btClose;
    
    private VerticalLayout vLPri;    
  
    Refresher refresher;
    DateField dt;
    
    public AtualizacoesSoftApp() {
    	
    	gmDAO = new GerenciarModuloDAO();
    	
    	addStyleName("layout-panel");
        setSizeFull();
        
        buildLayout();
        
     //   addExtension(buildRefresher());	
    }
    
    public Refresher buildRefresher(){
		
		refresher = new Refresher();
		refresher.setRefreshInterval(30000);
		refresher.addListener(new Refresher.RefreshListener() {
			
			@Override
			public void refresh(Refresher source) {
					
				 buildLayout();
				if(winMaximize != null){
					setLayoutModal(winMaximize);
				}				 
			}
		});
		
		return refresher;
	}
        
    JPAContainer<AtualizacoesSoftware> container;
    
    TextField txtBusca;
    
    Table tb;
    Table tb2;
    GerenciarModuloDAO gmDAO;
    
    private JPAContainer<AtualizacoesSoftware> buildContainer(){
    	container = JPAContainerFactory.makeReadOnly(AtualizacoesSoftware.class, ConnUtil.getEntity());
		container.sort(new Object[]{"data"}, new boolean[]{false});
		return container;
    }
    
    private Table buildTb(){
    	 tb = new Table(null, buildContainer()){
 	    	@Override
				protected String formatPropertyValue(Object rowId, Object colId,
						Property<?> property) {
					
										
					if(tb.getType(colId).equals(Date.class)){
						
						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							
							String data = tb.getItem(rowId).getItemProperty(colId).getValue().toString();
							return sdf.format(tb.getItem(rowId).getItemProperty(colId).getValue());
						}else{
							return null;
						}
						
					}
			
					
					return super.formatPropertyValue(rowId, colId, property);			
				}
 	    };
 	    tb.setSizeFull();
 	    tb.setVisibleColumns(new Object[]{"descricao","data"});
 	    tb.setColumnHeader("descricao", "Descrição");
 	    tb.setColumnHeader("data", "Data");
 	    tb.setColumnExpandRatio("descricao", 1f);
 	    
 	    
 	    
 	    
 	    tb.addGeneratedColumn("", new Table.ColumnGenerator() {
				
				@Override
				public Object generateCell(final Table source, final Object itemId, Object columnId) {

					Button btVer = new Button("EXIBIR", new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
													
							final Window win = new Window("Detalhe da Atualização");
							win.setWidth("698px");
							win.setHeight("348");
							win.center();
							win.setModal(true);
							win.setResizable(false);
														
							VerticalLayout vl = new VerticalLayout();							
							vl.setSizeFull();
							
							vl.addComponent(new FormLayout() {
								{
									setMargin(true);
									setSpacing(true);
									setStyleName("form-cutom-new");
																	
									final TextArea txtDescricao = new TextArea("Descrição");
									txtDescricao.setWidth("450px");
									txtDescricao.setHeight("200px");
									txtDescricao.setStyleName("caption-align");		
									txtDescricao.setValue(source.getItem(itemId).getItemProperty("descricao").getValue().toString());
									txtDescricao.setReadOnly(true);
									addComponent(txtDescricao);
									
									final DateField df = new DateField("Data");										
									df.setStyleName("caption-align");
									df.setValue((Date)source.getItem(itemId).getItemProperty("data").getValue());
									df.setReadOnly(true);
									addComponent(df); 
									
								}
							});
												 
							win.setContent(vl);							
							getUI().addWindow(win);
							
						}
					});
					
					btVer.setStyleName(Reindeer.BUTTON_LINK);
					
					Button btLink = null;
					if(source.getItem(itemId).getItemProperty("link").getValue() != null){
						btLink = new Button("DEMO", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								
						        Window wVideo  = new Window("DEMONSTRAÇÃO");
						        wVideo.setResizable(false); 
						        wVideo.setModal(true);
						        wVideo.center();
						        wVideo.setWidth("942px");
						        wVideo.setHeight("680px");
						        
						        wVideo.setContent(new VerticalLayout(){
						        	{
						        		setMargin(true);
						        		setSpacing(true);
						        		
						        		setWidth("940px");
								        setHeight("633px");
						        		
						        		String link = source.getItem(itemId).getItemProperty("link").getValue().toString();
						        		
						        		final Embedded video = new Embedded(null,new ExternalResource(link));
								        video.setType(Embedded.TYPE_BROWSER);
								        video.setSizeFull();
								        //video.setStyleName("center-margin");						        		
	
						        		addComponent(video);
						        		
						        	}
						        });
						        
						        getUI().addWindow(wVideo);
						        
						        
								
							}
							
							
						});
						
						btLink.setStyleName(Reindeer.BUTTON_LINK);
					}
					
					HorizontalLayout btOpcoes = new HorizontalLayout();
					btOpcoes.setStyleName("barra_bt_opcoes");
					btOpcoes.setMargin(false); 
					btOpcoes.setSpacing(true);
					btOpcoes.addComponent(btVer);
					
					if(source.getItem(itemId).getItemProperty("link").getValue() != null){
						btOpcoes.addComponent(new Label(" | "));
						btOpcoes.addComponent(btLink);
					}
					
					return btOpcoes;
				}
			});
 	    
 	    
 	    
 	    if(OpusERP4UI.getUsuarioLogadoUI().getUsername().equals("marconi")){
 	    
	    	    tb.addGeneratedColumn("Editar", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(final Table source, final Object itemId, Object columnId) {
	
						Button btVer = new Button("EDITAR", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
														
								final Window win = new Window("Alterar Atualização");
								win.setWidth("698px");
								win.setHeight("348");
								win.center();
								win.setModal(true);
								win.setResizable(false);
															
								VerticalLayout vl = new VerticalLayout();							
								vl.setSizeFull();
								
								vl.addComponent(new FormLayout() {
									{
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new");
																		
										final TextArea txtDescricao = new TextArea("Descrição");
										txtDescricao.setWidth("450px");
										txtDescricao.setHeight("200px");
										txtDescricao.setStyleName("caption-align");		
										txtDescricao.setValue(source.getItem(itemId).getItemProperty("descricao").getValue().toString());
										
										addComponent(txtDescricao);
										
										final DateField df = new DateField("Data");										
										df.setStyleName("caption-align");
										df.setValue((Date)source.getItem(itemId).getItemProperty("data").getValue());
										
										addComponent(df); 
										
										
										Button btCadastrar = new Button("Ok", new Button.ClickListener() {
											
											@Override
											public void buttonClick(ClickEvent event) {
												boolean c = AtualizacaoSoftDAO.salvar(
														new AtualizacoesSoftware((Integer)source.getItem(itemId).getItemProperty("id").getValue(), 
																txtDescricao.getValue(), df.getValue(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
												
												if(c){
													Notify.Show("Atualização Alterada com Sucesso", Notify.TYPE_SUCCESS);
													win.close();
												
												}
											}
										});
										
										btCadastrar.addStyleName("default");
										
										addComponent(btCadastrar);
										
									}
								});
													 
								win.setContent(vl);							
								getUI().addWindow(win);
								
							}
						});
						
						btVer.setStyleName(Reindeer.BUTTON_LINK);
						
						return btVer;
						
						
						
					}
				});
 	    }  	       	    
 	    
 	    return tb;
    }
    
    private void buildLayout(){
    		
    		removeAllComponents();
    		    		
    		//addComponent(hl); 
    	    addComponent(buildHistoricoAtualizacoes());    
    	    
			addComponent(buildBtMaximize());
			addComponent(buildBtClose());      
    }
    
    Button btCadastrar;
    
    private Button buildBtCadastrar(){
    	btCadastrar = new Button("Nova Atualização", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
			
				final Window win = new Window("Nova Atualização");
				win.setWidth("698px");
				win.setHeight("348");
				win.center();
				win.setModal(true);
				win.setResizable(false);
				
				VerticalLayout vl = new VerticalLayout();							
				vl.setSizeFull();
				
				vl.addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");
														
						final TextArea txtDescricao = new TextArea("Descrição");
						txtDescricao.setWidth("450px");
						txtDescricao.setHeight("200px");
						txtDescricao.setStyleName("caption-align");									
						addComponent(txtDescricao);
						
						final DateField df = new DateField("Data");										
						df.setStyleName("caption-align");
						df.setValue(new Date());
						addComponent(df); 
						
						Button btCadastrar = new Button("Ok", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								boolean c = AtualizacaoSoftDAO.salvar(
										new AtualizacoesSoftware(null, txtDescricao.getValue(), df.getValue(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
								
								if(c){
									Notify.Show("Atualização Cadastrada com Sucesso", Notify.TYPE_SUCCESS);
									win.close();
								
								}
							}
						});
						
						btCadastrar.addStyleName("default");
						
						addComponent(btCadastrar);

					}
				});
				
										
				win.setContent(vl);							
				getUI().addWindow(win);
				
			}
		});
		btCadastrar.addStyleName("default");
		
		return btCadastrar;
    }
    
    private Component buildHistoricoAtualizacoes(){
    	final VerticalLayout vlHistoricoAtualizacoes = new VerticalLayout();  
		vlHistoricoAtualizacoes.setCaption("ATUALIZAÇÕES SOFTWARE");
		vlHistoricoAtualizacoes.setSizeFull();

		
		if(OpusERP4UI.getUsuarioLogadoUI().getUsername().equals("marconi")){
			HorizontalLayout hl = new HorizontalLayout();
			hl.setStyleName("hl-barra-botoes");			
			hl.setWidth("100%");
			hl.addComponent(buildBtCadastrar());
			hl.setComponentAlignment(btCadastrar, Alignment.BOTTOM_RIGHT);		
			vlHistoricoAtualizacoes.addComponent(hl); 
		}
		
		
		
		
		
		txtBusca = new TextField();
		txtBusca.setImmediate(true); 
		txtBusca.setWidth("100%");
		txtBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				pesquisar(event.getText());
			}
		});
		
		vlHistoricoAtualizacoes.addComponent(txtBusca);    	    
		vlHistoricoAtualizacoes.addComponent(buildTb()); 
		vlHistoricoAtualizacoes.setExpandRatio(tb, 1f);
		
//		if(OpusERP4UI.getUsuarioLogadoUI().getUsername().equals("marconi")){
//			HorizontalLayout hBt = new HorizontalLayout(){
//				{
//					
//					
//					//addComponent(btCadastrar);
//					//setComponentAlignment(btCadastrar, Alignment.MIDDLE_RIGHT);
//				}
//			};
//			hBt.setWidth("100%");
//			vlHistoricoAtualizacoes.addComponent(hBt);
//		}
		
		return vlHistoricoAtualizacoes;
		
    }
    
    private void pesquisar(String busca){
    	
    	container.removeAllContainerFilters();   	
    	container.addContainerFilter(new Like("descricao", "%"+busca+"%", false));
    	container.applyFilters();
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
            	  PrefeDashDAO.remove(AtualizacoesSoftApp.class.toString());
                  ((GridLayout)getParent()).removeComponent(btClose.getParent());   
            }
        });
        
        return btClose;
    }
    
    Window winMaximize;    
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
                winMaximize = new Window("ATUALIZAÇÕES SOFTWARE");
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
                
                setLayoutModal(winMaximize); 
                
                getUI().addWindow(winMaximize);                
            }
        });
        
        return btMaximize;
    }
    
    Table tb3;
    private void setLayoutModal(Window w){
    	w.setContent(new VerticalLayout(){
                	{
                		setSizeFull();
                		setMargin(true); 
                		
                		vLPri = new VerticalLayout(){
                			{
                				removeAllComponents();
                				
                				container = JPAContainerFactory.makeReadOnly(AtualizacoesSoftware.class, ConnUtil.getEntity());
                				container.sort(new Object[]{"data"}, new boolean[]{false});
                	    		
                	    		HorizontalLayout hlCol1 = new HorizontalLayout();
                	    		hlCol1.setSizeFull();
                	    		
                	    	    tb3 = new Table(null, container){
                	     	    	@Override
                					protected String formatPropertyValue(Object rowId, Object colId,
                							Property<?> property) {
                						
                											
                						if(tb3.getType(colId).equals(Date.class)){
                							
                							if(tb3.getItem(rowId).getItemProperty(colId).getValue() != null){
                								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                								
                								String data = tb3.getItem(rowId).getItemProperty(colId).getValue().toString();
                								return sdf.format(tb3.getItem(rowId).getItemProperty(colId).getValue());
                							}else{
                								return null;
                							}                							
                						}
                				
                						
                						return super.formatPropertyValue(rowId, colId, property);			
                					}
                	    	    };
                	    	    tb3.setSizeFull();
                	    	    tb3.setVisibleColumns(new Object[]{"descricao","data"});
                	    	    tb3.setColumnHeader("descricao", "Descrição");
                	    	    tb3.setColumnHeader("data", "Data");
                	    	    
                	    	    tb3.setColumnExpandRatio("descricao", 1f);
                	    	    
                	    	    
                	    	    tb3.addGeneratedColumn("", new Table.ColumnGenerator() {
                					
                					@Override
                					public Object generateCell(final Table source, final Object itemId, Object columnId) {

                						Button btVer = new Button("EXIBIR", new Button.ClickListener() {
                							
                							@Override
                							public void buttonClick(ClickEvent event) {
                														
                								final Window win = new Window("Detalhe da Atualização");
                								win.setWidth("698px");
                								win.setHeight("348");
                								win.center();
                								win.setModal(true);
                								win.setResizable(false);
                															
                								VerticalLayout vl = new VerticalLayout();							
                								vl.setSizeFull();
                								
                								vl.addComponent(new FormLayout() {
                									{
                										setMargin(true);
                										setSpacing(true);
                										setStyleName("form-cutom-new");
                																		
                										final TextArea txtDescricao = new TextArea("Descrição");
                										txtDescricao.setWidth("450px");
                										txtDescricao.setHeight("200px");
                										txtDescricao.setStyleName("caption-align");		
                										txtDescricao.setValue(source.getItem(itemId).getItemProperty("descricao").getValue().toString());
                										txtDescricao.setReadOnly(true);
                										addComponent(txtDescricao);
                										
                										final DateField df = new DateField("Data");										
                										df.setStyleName("caption-align");
                										df.setValue((Date)source.getItem(itemId).getItemProperty("data").getValue());
                										df.setReadOnly(true);
                										addComponent(df); 
                										
                									}
                								});
                													 
                								win.setContent(vl);							
                								getUI().addWindow(win);
                								
                							}
                						});
                						
                						btVer.setStyleName(Reindeer.BUTTON_LINK);
                						
                						Button btLink = null;
                						if(source.getItem(itemId).getItemProperty("link").getValue() != null){
                							btLink = new Button("DEMO", new Button.ClickListener() {
                								
                								@Override
                								public void buttonClick(ClickEvent event) {
                									
                							        Window wVideo  = new Window("DEMONSTRAÇÃO");
                							        wVideo.setResizable(false); 
                							        wVideo.setModal(true);
                							        wVideo.center();
                							        wVideo.setWidth("942px");
                							        wVideo.setHeight("680px");
                							        
                							        wVideo.setContent(new VerticalLayout(){
                							        	{
                							        		setMargin(true);
                							        		setSpacing(true);
                							        		
                							        		setWidth("940px");
                									        setHeight("633px");
                							        		
                							        		String link = source.getItem(itemId).getItemProperty("link").getValue().toString();
                							        		
                							        		final Embedded video = new Embedded(null,new ExternalResource(link));
                									        video.setType(Embedded.TYPE_BROWSER);
                									        video.setSizeFull();
                									        //video.setStyleName("center-margin");						        		
                		
                							        		addComponent(video);
                							        		
                							        	}
                							        });
                							        
                							        getUI().addWindow(wVideo);
                							        
                							        
                									
                								}
                								
                								
                							});
                							
                							btLink.setStyleName(Reindeer.BUTTON_LINK);
                						}
                						
                						HorizontalLayout btOpcoes = new HorizontalLayout();
                						btOpcoes.setStyleName("barra_bt_opcoes");
                						btOpcoes.setMargin(false); 
                						btOpcoes.setSpacing(true);
                						btOpcoes.addComponent(btVer);
                						
                						if(source.getItem(itemId).getItemProperty("link").getValue() != null){
                							
                							btOpcoes.addComponent(new Label(" | "));
                							btOpcoes.addComponent(btLink);
                						}
                						
                						return btOpcoes;
                					}
                				});
                	 	    
                	 	    
                	 	    
                	 	    if(OpusERP4UI.getUsuarioLogadoUI().getUsername().equals("marconi")){
                	 	    
                		    	    tb3.addGeneratedColumn("Editar", new Table.ColumnGenerator() {
                						
                						@Override
                						public Object generateCell(final Table source, final Object itemId, Object columnId) {
                		
                							Button btVer = new Button("EDITAR", new Button.ClickListener() {
                								
                								@Override
                								public void buttonClick(ClickEvent event) {
                															
                									final Window win = new Window("Alterar Atualização");
                									win.setWidth("698px");
                									win.setHeight("348");
                									win.center();
                									win.setModal(true);
                									win.setResizable(false);
                																
                									VerticalLayout vl = new VerticalLayout();							
                									vl.setSizeFull();
                									
                									vl.addComponent(new FormLayout() {
                										{
                											setMargin(true);
                											setSpacing(true);
                											setStyleName("form-cutom-new");
                																			
                											final TextArea txtDescricao = new TextArea("Descrição");
                											txtDescricao.setWidth("450px");
                											txtDescricao.setHeight("200px");
                											txtDescricao.setStyleName("caption-align");		
                											txtDescricao.setValue(source.getItem(itemId).getItemProperty("descricao").getValue().toString());
                											
                											addComponent(txtDescricao);
                											
                											final DateField df = new DateField("Data");										
                											df.setStyleName("caption-align");
                											df.setValue((Date)source.getItem(itemId).getItemProperty("data").getValue());
                											
                											addComponent(df); 
                											
                											
                											Button btCadastrar = new Button("Ok", new Button.ClickListener() {
                												
                												@Override
                												public void buttonClick(ClickEvent event) {
                													boolean c = AtualizacaoSoftDAO.salvar(
                															new AtualizacoesSoftware((Integer)source.getItem(itemId).getItemProperty("id").getValue(), 
                																	txtDescricao.getValue(), df.getValue(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
                													
                													if(c){
                														Notify.Show("Atualização Alterada com Sucesso", Notify.TYPE_SUCCESS);
                														win.close();
                													
                													}
                												}
                											});
                											
                											btCadastrar.addStyleName("default");
                											
                											addComponent(btCadastrar);
                											
                										}
                									});
                														 
                									win.setContent(vl);							
                									getUI().addWindow(win);
                									
                								}
                							});
                							
                							btVer.setStyleName(Reindeer.BUTTON_LINK);
                							
                							return btVer;
                							
                							
                							
                						}
                					});
                	 	    	}
                	    	                    	    	       	    
                				hlCol1.addComponent(tb3); 
                				
                				
                				txtBusca = new TextField();
        						txtBusca.setImmediate(true); 
        						txtBusca.setWidth("100%");
        						txtBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
        							
        							@Override
        							public void textChange(TextChangeEvent event) {
        								pesquisar(event.getText());
        							}
        						});
        											
        						addComponent(txtBusca);
                	    	    addComponent(hlCol1);
                	    		
    
                			}
                		};                	
                		vLPri.setWidth("100%");                		
                		
                	
                		addComponent(vLPri);                		
                		setExpandRatio(vLPri, 1f);
                	}
                });
    }


    
}
