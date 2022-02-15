package com.digital.opuserp.view.home.apps.indicacoes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.IndicacoesDAO;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.IndicacoesCliente;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
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
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Not;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class IndicacoesVisaoGeral  extends CssLayout {
	
	private Chart chart;
    private Button btConfigure;
    private Button btMaximize;
    private Button btClose;
    private Button btRefresh;

  
    private VerticalLayout vLPri;    
  
    Refresher refresher;
    DateField dt;
    
    public IndicacoesVisaoGeral() {
    	
    	gmDAO = new GerenciarModuloDAO();
    	
    	addStyleName("layout-panel");
        setSizeFull();
        
        buildLayout();
        
        
        addExtension(buildRefresher());	
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
    
    ComboBox cbStatus;
    JPAContainer<IndicacoesCliente> container;
    
    TextField txtBusca;
    
    Table tb;
    //Table tb2;
    GerenciarModuloDAO gmDAO;
    
    private JPAContainer<IndicacoesCliente> buildContainer(){
    	container = JPAContainerFactory.makeReadOnly(IndicacoesCliente.class, ConnUtil.getEntity());
    	
    	
    	container.addContainerFilter(new Not(new Like("status", "CANCELADO")));	
		container.addNestedContainerProperty("cliente_que_indicou.nome_razao");
		container.addNestedContainerProperty("cliente_indicado.nome_razao");
		container.addNestedContainerProperty("cliente_que_indicou.doc_cpf_cnpj");
		
		return container;
    }
    
    private void buildLayout(){
    		
    		removeAllComponents();
    		
    		HorizontalLayout hlCol1 = new HorizontalLayout();  
    		hlCol1.setCaption("INDICAÇÕES");
    		hlCol1.setSizeFull();
    		  
			hlCol1.addComponent(buildTb()); 
			
			HorizontalLayout hl12 = new HorizontalLayout(){
				{
					setWidth("100%");
					
					cbStatus = new ComboBox();
					cbStatus.setImmediate(true); 
					cbStatus.setNullSelectionAllowed(false); 
					cbStatus.addItem("CREDITADO");
					cbStatus.addItem("EM ANALISE");
					cbStatus.addItem("APROVADO");
					cbStatus.addItem("REPROVADO");
					cbStatus.addItem("CREDITADO");
					cbStatus.addItem("TODOS");
					cbStatus.select("TODOS");
					
					cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							pesquisar(txtBusca.getValue());
						}
					});
					
					txtBusca = new TextField();
					txtBusca.setImmediate(true); 
					txtBusca.setWidth("100%");
					txtBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							pesquisar(event.getText());
						}
					});
										
					addComponent(cbStatus);
					addComponent(txtBusca); 
					setExpandRatio(txtBusca, 1f);
				}
			};
			
			addComponent(hl12); 
    	    addComponent(hlCol1);
    	    
			addComponent(buildBtMaximize());
			addComponent(buildBtClose());      
    }
    
    private Table buildTb() {
	
    	
    	 tb = new Table(null, buildContainer()){
				@Override
				protected String formatPropertyValue(Object rowId, Object colId,
						Property<?> property) {
					
					if(colId.equals("cliente_que_indicou.nome_razao") && tb.getItem(rowId).getItemProperty("cliente_que_indicou").getValue() != null &&  tb.getItem(rowId).getItemProperty("cliente_que_indicou.doc_cpf_cnpj").getValue() != null && tb.getItem(rowId).getItemProperty("cliente_que_indicou.nome_razao").getValue() != null){									
						
						String valor= tb.getItem(rowId).getItemProperty("cliente_que_indicou.doc_cpf_cnpj").getValue().toString()+" - "+tb.getItem(rowId).getItemProperty("cliente_que_indicou.nome_razao").getValue().toString();
						
							return valor ;				
											
					}
					
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
 	    tb.setVisibleColumns(new Object[]{"id","cliente_indicado.nome_razao","cliente_que_indicou.nome_razao","status","boleto", "data_cadastro"});
 	    tb.setColumnHeader("cliente_indicado.nome_razao", "Cliente");
 	    
 	    tb.setColumnHeader("cliente_que_indicou.nome_razao", "Indicado por");
 	    tb.setColumnHeader("status", "Status");
 	    tb.setColumnHeader("data_cadastro", "Data de Cadastro");
 	    tb.setColumnHeader("boleto", "Boleto");
 	    tb.setColumnHeader("id", "Cod");
 	    
 	    
 	    tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
				
				@Override
				public String getStyle(Table source, Object itemId, Object propertyId) {
					
					if(source.getItem(itemId).getItemProperty("status").getValue() != null && 
							source.getItem(itemId).getItemProperty("status").getValue().toString().equals("EM ANALISE")){
						return "row-header-indicacao-em-analise";   
					}else if(source.getItem(itemId).getItemProperty("status").getValue() != null && 
							source.getItem(itemId).getItemProperty("status").getValue().toString().equals("CREDITADO")){
						return "row-header-indicacao-em-creditado";   
					}else if(source.getItem(itemId).getItemProperty("status").getValue() != null && 
							source.getItem(itemId).getItemProperty("status").getValue().toString().equals("APROVADO")){
						return "row-header-indicacao-em-aprovado";   
					}
					
					return null;
				}
			});
 	    
 	    
 	    if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(116, OpusERP4UI.getEmpresa().getId(), 
 	    		OpusERP4UI.getUsuarioLogadoUI().getId(), "Vincular Boleto")){
 	    	
	    	    tb.addGeneratedColumn("v", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(final Table source, final Object itemId, Object columnId) {
						
						Button btVincular = new Button("Vincular", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
	
								
								EntityItem<IndicacoesCliente> entityIndicacao = (EntityItem<IndicacoesCliente>)source.getItem(itemId);
								final IndicacoesCliente indicacaoCliente = entityIndicacao.getEntity();
								
								
								final Window w = new Window("Selecione um boleto para Vincular");
								w.setWidth("928px");
								w.setHeight("417px");
								w.center();
								w.setModal(true);
								
								Cliente clienteQueIndicou = (Cliente)source.getItem(itemId).getItemProperty("cliente_que_indicou").getValue();
																
								JPAContainer<ContasReceber> containerBoletos = JPAContainerFactory.makeReadOnly(ContasReceber.class, ConnUtil.getEntity());
								containerBoletos.addContainerFilter(Filters.eq("cliente", clienteQueIndicou));
								containerBoletos.addContainerFilter(Filters.eq("status", "ABERTO"));
								containerBoletos.addNestedContainerProperty("cliente.nome_razao");
								
								tb2 = new Table(null, containerBoletos){
									@Override
									protected String formatPropertyValue(Object rowId, Object colId,
											Property<?> property) {
										
									
										if(tb2.getType(colId).equals(Date.class)){
											
											if(tb2.getItem(rowId).getItemProperty(colId).getValue() != null){
												SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
												
												String data = tb2.getItem(rowId).getItemProperty(colId).getValue().toString();
												return sdf.format(tb2.getItem(rowId).getItemProperty(colId).getValue());
											}else{
												return null;
											}
											
										}
								
										
										return super.formatPropertyValue(rowId, colId, property);			
									}
								};
								tb2.setSizeFull();
								
								tb2.setVisibleColumns(new Object[]{"id","cliente.nome_razao","n_doc","valor_titulo","data_vencimento"});
								
								tb2.setColumnHeader("id", "Cod");
								tb2.setColumnHeader("cliente.nome_razao", "Cliente");
								tb2.setColumnHeader("n_doc", "N doc");																
								tb2.setColumnHeader("valor_titulo", "Valor Titulo");
								tb2.setColumnHeader("data_vencimento", "Data Vencimento");
								
								tb2.addGeneratedColumn("", new Table.ColumnGenerator() {
									
									@Override
									public Object generateCell(final Table source, final Object itemId, Object columnId) {
											
										Button btSelecionar = new Button("selecionar", new Button.ClickListener() {
											
											@Override
											public void buttonClick(ClickEvent event) {
												String cod_boleto = source.getItem(itemId).getItemProperty("id").getValue().toString();
												indicacaoCliente.setBoleto(cod_boleto); 
												
												boolean c = IndicacoesDAO.salvar(indicacaoCliente);
												
												if(c){
													Notify.Show("Boleto vinculado com sucesso!", Notify.TYPE_SUCCESS);
													w.close();
													
													refresh();
												}
												
											}
										});
										
										btSelecionar.setStyleName(Reindeer.BUTTON_LINK);
										return btSelecionar;
									}
								});
								
								
								VerticalLayout vlCont = new VerticalLayout();
								vlCont.setSizeFull();
								
								vlCont.addComponent(tb2);
								
								w.setContent(vlCont); 
								w.center();
								w.setModal(true);
								
								
								getUI().addWindow(w);
							}
						});
						
						btVincular.setStyleName(Reindeer.BUTTON_LINK);
						
						return btVincular;
					}
				});
 	    }
 	    
 	   if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(116, OpusERP4UI.getEmpresa().getId(), 
	    		OpusERP4UI.getUsuarioLogadoUI().getId(), "Creditar")){
	    	
	    	    tb.addGeneratedColumn("c", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(final Table source, final Object itemId, Object columnId) {
						
						
						if(		itemId != null && source.getItem(itemId) != null && 
								source.getItem(itemId).getItemProperty("status") != null && 
								source.getItem(itemId).getItemProperty("status").getValue() != null && 
								source.getItem(itemId).getItemProperty("status").getValue().toString().equals("APROVADO")){
							
								Button btCreditar = new Button("Creditar", new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
			
										
										EntityItem<IndicacoesCliente> entityIndicacao = (EntityItem<IndicacoesCliente>)source.getItem(itemId);
										final IndicacoesCliente indicacaoCliente = entityIndicacao.getEntity();
										
										GenericDialog gd = new GenericDialog("Confirme para continuar", "Deseja realmente creditar a indicação:  "+indicacaoCliente.getId().toString(), true, true);
										gd.addListerner(new GenericDialog.DialogListerner() {
											
											@Override
											public void onClose(DialogEvent event) {
												
													if(event.isConfirm()){
												
														indicacaoCliente.setStatus("CREDITADO");
														boolean ch = IndicacoesDAO.salvar(indicacaoCliente);
														
														if(ch){
															Notify.Show("Indicação creditada com sucesso!", Notify.TYPE_SUCCESS);
															refresh(); 
														}
													
													}
													
											}
										});
										
										
										getUI().addWindow(gd); 						
										
									}
								});
								
								btCreditar.setStyleName(Reindeer.BUTTON_LINK);
								
								return btCreditar;
						
						}else{
							
								return new String("");
						}
					}
				});
	    }
 	    
 	    
 	    return tb;
	}

	private void refresh(){
    	buildLayout(); 
    }
    
    private void pesquisar(String busca){
    	
    	container.removeAllContainerFilters();
    	
    	if(cbStatus != null && !cbStatus.getValue().toString().equals("TODOS")){
    		container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
    	}
    	
    	
    		container.addContainerFilter(
    				
    				Filters.or(new Like("cliente_indicado.nome_razao", "%"+busca+"%", false),
    						new Like("cliente_que_indicou.nome_razao", "%"+busca+"%", false) ));
    				
    	
    	
    	container.applyFilters();
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
            	  PrefeDashDAO.remove(IndicacoesVisaoGeral.class.toString());
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
                winMaximize = new Window("INDICAÇÕES");
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
    
    Table tb2;
    Table tb3;
    Table tb4; 
    private void setLayoutModal(Window w){
    	w.setContent(new VerticalLayout(){
                	{
                		setSizeFull();
                		setMargin(true); 
                		
                		vLPri = new VerticalLayout(){
                			{
                				removeAllComponents();
                				
                	    		HorizontalLayout hlCol1 = new HorizontalLayout();                  	    
                	    		hlCol1.setWidth("100%");
                	    		
                	    	    tb3 = new Table(null, buildContainer()){
                					@Override
                					protected String formatPropertyValue(Object rowId, Object colId,
                							Property<?> property) {
                						
                						if(colId.equals("cliente_que_indicou.nome_razao") && 
                								tb3.getItem(rowId).getItemProperty("cliente_que_indicou").getValue() != null &&  
                								tb3.getItem(rowId).getItemProperty("cliente_que_indicou.doc_cpf_cnpj").getValue() != null && 
                								tb3.getItem(rowId).getItemProperty("cliente_que_indicou.nome_razao").getValue() != null){									
                							
                							String valor= tb3.getItem(rowId).getItemProperty("cliente_que_indicou.doc_cpf_cnpj").getValue().toString()+" - "+tb3.getItem(rowId).getItemProperty("cliente_que_indicou.nome_razao").getValue().toString();
                							
                								return valor ;				
                												
                						}
                						
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
                	    	    tb3.setVisibleColumns(new Object[]{"id","cliente_indicado.nome_razao", "cliente_que_indicou.nome_razao","status", "boleto","data_cadastro"});
                	    	    tb3.setColumnHeader("cliente_indicado.nome_razao", "Cliente");
                	    	    tb3.setColumnHeader("cliente_que_indicou.nome_razao", "Indicado por");
                	    	    tb3.setColumnHeader("status", "Status");
                	    	    tb3.setColumnHeader("data_cadastro", "Data de Cadastro");                	    	    
                	    	    tb3.setColumnHeader("boleto", "Boleto");
                	    	    tb3.setColumnHeader("id", "Cod");
                	    	    
                	    	    tb3.setCellStyleGenerator(new Table.CellStyleGenerator() {
                					
                					@Override
                					public String getStyle(Table source, Object itemId, Object propertyId) {
                						
                						if(source.getItem(itemId).getItemProperty("status").getValue() != null && 
                								source.getItem(itemId).getItemProperty("status").getValue().toString().equals("EM ANALISE")){
                							return "row-header-indicacao-em-analise";   
                						}else if(source.getItem(itemId).getItemProperty("status").getValue() != null && 
                								source.getItem(itemId).getItemProperty("status").getValue().toString().equals("CREDITADO")){
                							return "row-header-indicacao-em-creditado";   
                						}else if(source.getItem(itemId).getItemProperty("status").getValue() != null && 
                								source.getItem(itemId).getItemProperty("status").getValue().toString().equals("APROVADO")){
                							return "row-header-indicacao-em-aprovado";   
                						}
                						
                						return null;
                					}
                				});
                	    	    
                	    	    if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(116, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Vincular Boleto")){
                		    	    tb3.addGeneratedColumn("v", new Table.ColumnGenerator() {
                						
                						@Override
                						public Object generateCell(final Table source, final Object itemId, Object columnId) {
                							
                							Button btVincular = new Button("Vincular", new Button.ClickListener() {
                								
                								@Override
                								public void buttonClick(ClickEvent event) {
                		
                									
                									EntityItem<IndicacoesCliente> entityIndicacao = (EntityItem<IndicacoesCliente>)source.getItem(itemId);
                									final IndicacoesCliente indicacaoCliente = entityIndicacao.getEntity();
                									
                									
                									final Window w = new Window("Selecione um boleto para Vincular");
                									w.setWidth("928px");
                									w.setHeight("417px");
                									w.center();
                									w.setModal(true);
                									
                									Cliente clienteQueIndicou = (Cliente)source.getItem(itemId).getItemProperty("cliente_que_indicou").getValue();
                																	
                									JPAContainer<ContasReceber> containerBoletos = JPAContainerFactory.makeReadOnly(ContasReceber.class, ConnUtil.getEntity());
                									containerBoletos.addContainerFilter(Filters.eq("cliente", clienteQueIndicou));
                									containerBoletos.addContainerFilter(Filters.eq("status", "ABERTO"));
                									containerBoletos.addNestedContainerProperty("cliente.nome_razao");
                									
                									 tb4 = new Table(null, containerBoletos){
                										@Override
                										protected String formatPropertyValue(Object rowId, Object colId,
                												Property<?> property) {
                											
                										
                											if(tb4.getType(colId).equals(Date.class)){
                												
                												if(tb4.getItem(rowId).getItemProperty(colId).getValue() != null){
                													SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                													
                													String data = tb4.getItem(rowId).getItemProperty(colId).getValue().toString();
                													return sdf.format(tb4.getItem(rowId).getItemProperty(colId).getValue());
                												}else{
                													return null;
                												}
                												
                											}
                									
                											
                											return super.formatPropertyValue(rowId, colId, property);			
                										}
                									};
                									tb4.setSizeFull();
                									
                									tb4.setVisibleColumns(new Object[]{"id","cliente.nome_razao","n_doc","valor_titulo","data_vencimento"});
                									
                									tb4.setColumnHeader("id", "Cod");
                									tb4.setColumnHeader("cliente.nome_razao", "Cliente");
                									tb4.setColumnHeader("n_doc", "N doc");																
                									tb4.setColumnHeader("valor_titulo", "Valor Titulo");
                									tb4.setColumnHeader("data_vencimento", "Data Vencimento");
                									
                									tb4.addGeneratedColumn("", new Table.ColumnGenerator() {
                										
                										@Override
                										public Object generateCell(final Table source, final Object itemId, Object columnId) {
                												
                											Button btSelecionar = new Button("selecionar", new Button.ClickListener() {
                												
                												@Override
                												public void buttonClick(ClickEvent event) {
                													String cod_boleto = source.getItem(itemId).getItemProperty("id").getValue().toString();
                													indicacaoCliente.setBoleto(cod_boleto); 
                													
                													boolean c = IndicacoesDAO.salvar(indicacaoCliente);
                													
                													if(c){
                														Notify.Show("Boleto vinculado com sucesso!", Notify.TYPE_SUCCESS);
                														w.close();
                														
                														refresh();
                													}
                													
                												}
                											});
                											
                											btSelecionar.setStyleName(Reindeer.BUTTON_LINK);
                											return btSelecionar;
                										}
                									});
                									
                									
                									VerticalLayout vlCont = new VerticalLayout();
                									vlCont.setSizeFull();
                									
                									vlCont.addComponent(tb4);
                									
                									w.setContent(vlCont); 
                									w.center();
                									w.setModal(true);
                									
                									
                									getUI().addWindow(w);
                								}
                							});
                							
                							btVincular.setStyleName(Reindeer.BUTTON_LINK);
                							
                							return btVincular;
                						}
                					});
                		    	    
                		    	    if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(116, OpusERP4UI.getEmpresa().getId(), 
                		    	    		OpusERP4UI.getUsuarioLogadoUI().getId(), "Creditar")){
                		    	    	
                		    	    	    tb3.addGeneratedColumn("c", new Table.ColumnGenerator() {
                		    					
                		    					@Override
                		    					public Object generateCell(final Table source, final Object itemId, Object columnId) {
                		    						
                		    						
                		    						if(		itemId != null && source.getItem(itemId) != null && 
                		    								source.getItem(itemId).getItemProperty("status") != null && 
                		    								source.getItem(itemId).getItemProperty("status").getValue() != null && 
                		    								source.getItem(itemId).getItemProperty("status").getValue().toString().equals("APROVADO")){
                		    							
                		    								Button btCreditar = new Button("Creditar", new Button.ClickListener() {
                		    									
                		    									@Override
                		    									public void buttonClick(ClickEvent event) {
                		    			
                		    										
                		    										EntityItem<IndicacoesCliente> entityIndicacao = (EntityItem<IndicacoesCliente>)source.getItem(itemId);
                		    										final IndicacoesCliente indicacaoCliente = entityIndicacao.getEntity();
                		    										
                		    										GenericDialog gd = new GenericDialog("Confirme para continuar", "Deseja realmente creditar a indicação:  "+indicacaoCliente.getId().toString(), true, true);
                		    										gd.addListerner(new GenericDialog.DialogListerner() {
                		    											
                		    											@Override
                		    											public void onClose(DialogEvent event) {
                		    												
                		    													if(event.isConfirm()){
                		    												
                		    														indicacaoCliente.setStatus("CREDITADO");
                		    														boolean ch = IndicacoesDAO.salvar(indicacaoCliente);
                		    														
                		    														if(ch){
                		    															Notify.Show("Indicação creditada com sucesso!", Notify.TYPE_SUCCESS);
                		    															refresh(); 
                		    														}
                		    													
                		    													}
                		    													
                		    											}
                		    										});
                		    										
                		    										
                		    										getUI().addWindow(gd); 						
                		    										
                		    									}
                		    								});
                		    								
                		    								btCreditar.setStyleName(Reindeer.BUTTON_LINK);
                		    								
                		    								return btCreditar;
                		    						
                		    						}else{
                		    							
                		    								return new String("");
                		    						}
                		    					}
                		    				});
                		    	    }
                		    	    
                	 	    }
                	    	       	    
                				hlCol1.addComponent(tb3); 
                				
                				
                				HorizontalLayout hl12 = new HorizontalLayout(){
                					{
                						setWidth("100%");
                						
                						cbStatus = new ComboBox();
                						cbStatus.setImmediate(true); 
                						cbStatus.setNullSelectionAllowed(false); 
                						cbStatus.addItem("EM ANALISE");
                						cbStatus.addItem("APROVADO");
                						cbStatus.addItem("REPROVADO");
                						cbStatus.addItem("CREDITADO");
                						cbStatus.addItem("TODOS");
                						cbStatus.select("TODOS");
                						
                						cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
                							
                							@Override
                							public void valueChange(ValueChangeEvent event) {
                								pesquisar(txtBusca.getValue());
                							}
                						});
                						
                						txtBusca = new TextField();
                						txtBusca.setImmediate(true); 
                						txtBusca.setWidth("100%");
                						txtBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
                							
                							@Override
                							public void textChange(TextChangeEvent event) {
                								pesquisar(event.getText());
                							}
                						});
                											
                						addComponent(cbStatus);
                						addComponent(txtBusca); 
                						setExpandRatio(txtBusca, 1f);
                					}
                				};
                				
                				addComponent(hl12); 
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
