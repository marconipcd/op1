package com.digital.opuserp.view.modulos.ordemServico.materiaisAlocados;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.MateriaisAlocadosDAO;
import com.digital.opuserp.domain.EstoqueMovel;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.ScpUtil;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.view.modulos.cadastro.veiculos.VeiculosEditor;
import com.digital.opuserp.view.modulos.cadastro.veiculos.VeiculosEditor.VeiculoEvent;
import com.digital.opuserp.view.modulos.ordemServico.materiaisAlocados.AlocarMaterialEditor.AlocarMaterialEvent;
import com.digital.opuserp.view.modulos.ordemServico.materiaisAlocados.EstornarMaterialEditor.EstornarMaterialEvent;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.RelatorioUsoMateriais;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class MateriaisAlocadosView extends VerticalLayout{

	JPAContainer<EstoqueMovel> container;
	JPAContainer<Veiculos> containerVeiculo; 
	
	Table tb;
	TextField tfBusca;
	ComboBox cbVeiculo;
	Button btAlocar;
	
	
	
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	
	GerenciarModuloDAO gmDAO;
	
	public MateriaisAlocadosView(boolean act) {
		
		if(act){		
			setSizeFull();
			
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.setWidth("100%");
			
						
			Button btRefresh = new Button("Atualizar", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					refresh();
				}
			});
			
			
			hlButons.addComponent(btRefresh);
			hlButons.setComponentAlignment(btRefresh, Alignment.MIDDLE_LEFT);
			hlButons.setExpandRatio(btRefresh, 1f);
			
			
			hlButons.addComponent(BuildbtAlocar());
			hlButons.addComponent(buildBtDevolver());
			hlButons.addComponent(buildBtEditarInfo());
			hlButons.addComponent(buildBtRelatorio());
			hlButons.addComponent(buildBtLogs());
			
			addComponent(hlButons);
			
			
			//setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
						
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildCbVeiculos());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
	
			addComponent(buildTbGeneric());
			//addComponent(buildLbRegistros());
			
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildLbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
			lbLegend.setWidth("250px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			
			addComponent(hlFloat);
			
			
			setExpandRatio(tb, 1);
		}
		
	}
	
	private Button btLogs;
	private Button buildBtLogs(){
		
		btLogs = new Button("Logs", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tb.getValue() != null){
				
					EntityItem<EstoqueMovel> entityItemEstoq = (EntityItem<EstoqueMovel>)tb.getItem(tb.getValue()); 
					
					HistoricoAlteracoesEstoqueMovel histoAlteracoes = new HistoricoAlteracoesEstoqueMovel(true, true, entityItemEstoq.getEntity());
					getUI().addWindow(histoAlteracoes);
				
				}
			}
		});
		
		btLogs.setEnabled(false);
		return btLogs;
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
		addFilter(tfBusca.getValue());
		
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof MateriaisAlocadosView)
		{
			btAlocar.addShortcutListener(buildShortCutNovo());			
		}else{
			if(btAlocar != null || slNovo != null){				
				btAlocar.removeShortcutListener(slNovo);
			}						
		}
	}
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btAlocar.click();
			}
		};
		return slNovo;
	}
	
	
	public JPAContainer<EstoqueMovel> buildContainer(){
		container = JPAContainerFactory.makeBatchable(EstoqueMovel.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addNestedContainerProperty("produto.nome");
		container.addNestedContainerProperty("veiculo.cod_veiculo");
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		
		return container;
	}
	
	public JPAContainer<Veiculos> buildContainerVeiculos(){
		containerVeiculo = JPAContainerFactory.makeBatchable(Veiculos.class, ConnUtil.getEntity());
		containerVeiculo.setAutoCommit(false);
		containerVeiculo.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		containerVeiculo.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		containerVeiculo.sort(new Object[]{"cod_veiculo"}, new boolean[]{true});
		
		return containerVeiculo;
	}

	
	public Table buildTbGeneric() {	
		tb = new Table(null,buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("produto.nome", "Produto");
		tb.setColumnHeader("veiculo.cod_veiculo", "Veiculo");
		tb.setColumnHeader("qtd", "Qtd.:");
		tb.setColumnHeader("data_cadastro", "Data Cadastro");
		tb.setColumnHeader("data_ultima_alteracao", "Data Alteração");
		tb.setColumnHeader("info_adicionais", "Info. Adicionais");
		
		tb.setVisibleColumns(new Object[] {"id","produto.nome","veiculo.cod_veiculo","qtd","data_cadastro","data_ultima_alteracao","info_adicionais"});
				
		tb.setImmediate(true);		
	
		
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(tb.getValue() != null){
					btDevolver.setEnabled(true);
					btLogs.setEnabled(true); 
				}else{
					btDevolver.setEnabled(false);
					btLogs.setEnabled(false);
				}
			}
		});
		
		
		
		tb.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()  && tb.getValue() != null){
					
					VeiculosEditor VeiEditor = new VeiculosEditor(tb.getItem(tb.getValue()), "Editar Veiculo", true);
					VeiEditor.addListerner(new VeiculosEditor.VeiculoListerner() {
						
						@Override
						public void onClose(VeiculoEvent event) {
							if(event.isConfirm()){
								container.commit();
								tb.focus();
							}
						}
					});
					
					VeiEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
					
					getUI().addWindow(VeiEditor);
					VeiEditor.center();
					VeiEditor.setModal(true);	
				}
			}
		});
		return tb;
	}

	public ComboBox buildCbVeiculos(){
		cbVeiculo = new ComboBox(null,buildContainerVeiculos()){
			@Override
			public String getItemCaption(Object itemId) {
			   Item item = getItem(itemId);
			
			   if (item == null) {
			      return "";
			   }
			     
			   return String.valueOf(item.getItemProperty("cod_veiculo")) + ", " +		   		  
		   		  String.valueOf(item.getItemProperty("marca")) + ", " +
		   		  String.valueOf(item.getItemProperty("modelo")) + ", " +
		   		  String.valueOf(item.getItemProperty("cor"));
			}
		};
		
		cbVeiculo.setWidth("261px");
		
		cbVeiculo.setNullSelectionAllowed(true);
		cbVeiculo.setStyleName("caption-align");	
		cbVeiculo.setImmediate(true);
		cbVeiculo.setTextInputAllowed(false);
		cbVeiculo.setConverter(new SingleSelectConverter(cbVeiculo));
		
		
		cbVeiculo.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
					addFilter(tfBusca.getValue());				
			}
		});
		
		return cbVeiculo;
	}
	
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}

	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		if(cbVeiculo.getValue() != null){
			container.addContainerFilter(Filters.eq("veiculo", cbVeiculo.getItem(cbVeiculo.getValue()).getItemProperty("id").getValue()));
		}
		
		if(s != null && !s.equals("") && !s.isEmpty()){

			Object[] collums = tb.getVisibleColumns();	
		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			
			try {
				Integer cod = Integer.valueOf(s);			
				
					for(Object c:collums){		 			
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
							filtros.add(new Like(c.toString(), "%"+cod+"%", false));
						}			
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));
						}		
		
					}
				
				
			} catch (Exception e) {

				try {				

						SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

						for(Object c:collums){		 
							if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){

									Date date = sdf2.parse(s);
									filtros.add(Filters.eq(c.toString(), date));
								}						
							}
						
					} catch (Exception e2) {
						for(Object c:collums){		 
							
							if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
								filtros.add(new Like(c.toString(), "%"+s+"%", false));
							}	
						}
					}
				}
	
			container.addContainerFilter(Filters.or(filtros));
		
		}
				
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
		
	}
	
	
	
	
	Window winSubRelatorio;
	Window winRelatorioMateriaisAlocados;
	Window winRelatorioMateriaisVinculado;
	
	private void buildSubRelatorio(ClickEvent event) {
		
		
		winSubRelatorio = new Window("Opções de Relatórios:");
        VerticalLayout l = new VerticalLayout();
        l.setMargin(true);
        
        winSubRelatorio.setContent(l);
        winSubRelatorio.setWidth("230px");
        winSubRelatorio.addStyleName("notifications");
        winSubRelatorio.setClosable(false);
        winSubRelatorio.setResizable(false);
        winSubRelatorio.setDraggable(false);
        winSubRelatorio.setPositionX((event.getClientX() - event.getRelativeX())+100);
        winSubRelatorio.setPositionY(event.getClientY() - event.getRelativeY());
        winSubRelatorio.setCloseShortcut(KeyCode.ESCAPE, null);
        
        Button bt1RelatorioMateriaisVinculados = new Button("Materiais Vinculados", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
			
		
						 if(winRelatorioMateriaisAlocados != null){
							 winRelatorioMateriaisAlocados.close();
						 }
						
						
						 winRelatorioMateriaisVinculado = new Window("Informe as Datas:");
					     
						 VerticalLayout l1 = new VerticalLayout();
					     l1.setMargin(true);
					        
					     winRelatorioMateriaisVinculado.setContent(l1);
					     winRelatorioMateriaisVinculado.setWidth("230px");
					     winRelatorioMateriaisVinculado.addStyleName("notifications");
					     winRelatorioMateriaisVinculado.setClosable(false);
					     winRelatorioMateriaisVinculado.setResizable(false);
					     winRelatorioMateriaisVinculado.setDraggable(false);
					     winRelatorioMateriaisVinculado.setPositionX((event.getClientX() - event.getRelativeX())+100);
					     winRelatorioMateriaisVinculado.setPositionY(event.getClientY() - event.getRelativeY());
					     winRelatorioMateriaisVinculado.setCloseShortcut(KeyCode.ESCAPE, null);
					     final DateField dtInicial = new DateField("Inicial");
					      dtInicial.setWidth("190px");
					      
					      final DateField dtFinal   = new DateField("Final");
					      dtFinal.setWidth("190px");
					      
					      JPAContainer<Veiculos> containerVeiculos = JPAContainerFactory.makeReadOnly(Veiculos.class, ConnUtil.getEntity());
					      containerVeiculos.addContainerFilter(Filters.eq("status", "ATIVO"));
					      
					      final ComboBox cbVeiculos = new ComboBox("Veículo",containerVeiculos){
								@Override
								public String getItemCaption(Object itemId) {
								   Item item = getItem(itemId);
								
								   if (item == null) {
								      return "";
								   }
								     
								   return String.valueOf(item.getItemProperty("cod_veiculo")) + ", " +		   		  
							   		  String.valueOf(item.getItemProperty("marca")) + ", " +
							   		  String.valueOf(item.getItemProperty("modelo")) + ", " +
							   		  String.valueOf(item.getItemProperty("cor"));
								}
							};
												
					      cbVeiculos.setWidth("192px");
					      cbVeiculos.setConverter(new SingleSelectConverter(cbVeiculos));
					      
					      final Button btGerarRelatorio = new Button("Gerar", new Button.ClickListener() {
								
								@Override
								public void buttonClick(ClickEvent event) {
									
									if(dtInicial.getValue() != null && dtFinal.getValue() != null && cbVeiculos.getValue() != null){
										try {
											
											Window window = new Window();
											window.setCaption("Relatório de uso de Materiais");
									        window.setWidth("800px");
									        window.setHeight("600px");
									        window.setResizable(true);
									        window.center();
									        window.setModal(true);
									        window.setStyleName("disable_scroolbar");
									        
									        EntityItem<Veiculos> entityItem = (EntityItem<Veiculos>)cbVeiculos.getItem(cbVeiculos.getValue());
									        
									        StreamResource resource = new StreamResource(new RelatorioUsoMateriais(dtInicial.getValue(), dtFinal.getValue(),entityItem.getEntity()), "Relatorio_de_Uso_de_Materiais.pdf");
									        resource.getStream();			        
									        resource.setMIMEType("application/pdf");			        
									        
									        Embedded e = new Embedded();
									        e.setSizeFull();
									        e.setType(Embedded.TYPE_BROWSER); 
									        e.setSource(resource);
									        
									        window.setContent(e);
									        getUI().addWindow(window);
									
										} catch (Exception e1) {
									
											e1.printStackTrace();
										}
									}
								}
							});
					      btGerarRelatorio.setStyleName("default");
					      
					      l1.addComponent(dtInicial);
					      l1.addComponent(dtFinal);
					      l1.addComponent(cbVeiculos); 
					      l1.addComponent(new HorizontalLayout(){
					      	{
					      		setWidth("100%");
					      		
					      		addComponent(btGerarRelatorio);
					      		setComponentAlignment(btGerarRelatorio, Alignment.MIDDLE_RIGHT);
					      	}
					      }); 
						
						 getUI().addWindow(winRelatorioMateriaisVinculado);
						 winRelatorioMateriaisVinculado.focus();
		                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
		                             
		               			@Override
		                        public void layoutClick(LayoutClickEvent event) {
		               				winRelatorioMateriaisVinculado.close();
		                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
		                        }
		                 });
									
								
			
			}		
		});     
        bt1RelatorioMateriaisVinculados.setPrimaryStyleName("btSubMenu");
        	
        Button btRelatorioMateriaisAlocados = new Button("Materiais Alocados", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				 if(winRelatorioMateriaisVinculado != null){
				   winRelatorioMateriaisVinculado.close();
				 }
				
				 winRelatorioMateriaisAlocados = new Window("Informe as Datas:");
			     
				 VerticalLayout l1 = new VerticalLayout();
			     l1.setMargin(true);
			        
			     winRelatorioMateriaisAlocados.setContent(l1);
			     winRelatorioMateriaisAlocados.setWidth("230px");
			     winRelatorioMateriaisAlocados.addStyleName("notifications");
			     winRelatorioMateriaisAlocados.setClosable(false);
			     winRelatorioMateriaisAlocados.setResizable(false);
			     winRelatorioMateriaisAlocados.setDraggable(false);
			     winRelatorioMateriaisAlocados.setPositionX((event.getClientX() - event.getRelativeX())+100);
			     winRelatorioMateriaisAlocados.setPositionY(event.getClientY() - event.getRelativeY());
			     winRelatorioMateriaisAlocados.setCloseShortcut(KeyCode.ESCAPE, null);
			      final DateField dtInicial = new DateField("Inicial");
			      dtInicial.setWidth("190px");
			      
			      final DateField dtFinal   = new DateField("Final");
			      dtFinal.setWidth("190px");
			      
			      JPAContainer<Veiculos> containerVeiculos = JPAContainerFactory.makeReadOnly(Veiculos.class, ConnUtil.getEntity());
			      containerVeiculos.addContainerFilter(Filters.eq("status", "ATIVO"));
			      
			      final ComboBox cbVeiculos = new ComboBox("Veículo",containerVeiculos){
						@Override
						public String getItemCaption(Object itemId) {
						   Item item = getItem(itemId);
						
						   if (item == null) {
						      return "";
						   }
						     
						   return String.valueOf(item.getItemProperty("cod_veiculo")) + ", " +		   		  
					   		  String.valueOf(item.getItemProperty("marca")) + ", " +
					   		  String.valueOf(item.getItemProperty("modelo")) + ", " +
					   		  String.valueOf(item.getItemProperty("cor"));
						}
					};
										
			      cbVeiculos.setWidth("192px");
			      cbVeiculos.setConverter(new SingleSelectConverter(cbVeiculos));
			      
			      final Button btGerarRelatorio = new Button("Gerar", new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							
							if(dtInicial.getValue() != null && dtFinal.getValue() != null && cbVeiculos.getValue() != null){
								try {
									
									Window window = new Window();
									window.setCaption("Relatório de uso de Materiais");
							        window.setWidth("800px");
							        window.setHeight("600px");
							        window.setResizable(true);
							        window.center();
							        window.setModal(true);
							        window.setStyleName("disable_scroolbar");
							        			       
							        EntityItem<Veiculos> entityItem = (EntityItem<Veiculos>)cbVeiculos.getItem(cbVeiculos.getValue());
							        
							        StreamResource resource = new StreamResource(new RelatorioAlocacaoMateriais(dtInicial.getValue(), dtFinal.getValue(), entityItem.getEntity()), "Relatorio_de_Uso_de_Materiais.pdf");
							        resource.getStream();			        
							        resource.setMIMEType("application/pdf");			        
							        
							        Embedded e = new Embedded();
							        e.setSizeFull();
							        e.setType(Embedded.TYPE_BROWSER); 
							        e.setSource(resource);
							        
							        window.setContent(e);
							        getUI().addWindow(window);
							
								} catch (Exception e1) {
							
									e1.printStackTrace();
								}
							}
						}
					});
			      btGerarRelatorio.setStyleName("default");
			      
			      l1.addComponent(dtInicial);
			      l1.addComponent(dtFinal);
			      l1.addComponent(cbVeiculos);
			      
			      l1.addComponent(new HorizontalLayout(){
			      	{
			      		setWidth("100%");
			      		
			      		addComponent(btGerarRelatorio);
			      		setComponentAlignment(btGerarRelatorio, Alignment.MIDDLE_RIGHT);
			      	}
			      }); 
				
				 getUI().addWindow(winRelatorioMateriaisAlocados);
				 winRelatorioMateriaisAlocados.focus();
                ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
                            
              			@Override
                       public void layoutClick(LayoutClickEvent event) {
              				winRelatorioMateriaisAlocados.close();
                           ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
                       }
                });
			}
		});
        
        btRelatorioMateriaisAlocados.setPrimaryStyleName("btSubMenu");
        
	    l.addComponent(bt1RelatorioMateriaisVinculados);
	    l.addComponent(btRelatorioMateriaisAlocados);
        
	}

	public Button BuildbtAlocar() {
		btAlocar = new Button("Alocar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alocar"))				
				{
				
					final AlocarMaterialEditor alocarMaterialEditor = new AlocarMaterialEditor("Alocar Materiais", true);
					alocarMaterialEditor.addListerner(new AlocarMaterialEditor.AlocarMaterialListerner() {
						
						@Override
						public void onClose(AlocarMaterialEvent event) {
							if(event.isConfirm()){	
								
								final List<EstoqueMovel> alocacoes = event.getAlocacoes();
								final String tecnico = event.getTecnico();
								final String veiculo = event.getVeiculo();
								
								GenericDialog gd = new GenericDialog("Confirme para continuar", "Deseja imprimir o pedido da alocação", true, true);
								gd.addListerner(new GenericDialog.DialogListerner() {
									
									@Override
									public void onClose(DialogEvent event) {
										if(event.isConfirm()){
											
											imprimirPedido(alocacoes, tecnico,veiculo,"loja");
										}
									}

								});
								
								gd.addCloseListener(new Window.CloseListener() {
									
									@Override
									public void windowClose(CloseEvent e) {
										GenericDialog gd2 = new GenericDialog("Confirme para continua", "Deseja imprimir uma Segunda via ?", true, true);
										gd2.addListerner(new GenericDialog.DialogListerner() {
											
											@Override
											public void onClose(DialogEvent event) {
												if(event.isConfirm()){
													imprimirPedido(alocacoes, tecnico,veiculo,"tecnico");
												}
											}
										});
										
										getUI().addWindow(gd2); 
									}
								});
								
								getUI().addWindow(gd); 
								
								refresh();
								alocarMaterialEditor.close();
							}
						}
					});
					
					
					
					getUI().addWindow(alocarMaterialEditor);
					
				}else{
					Notify.Show("Você não Possui Permissão para Alocar Materiais", Notify.TYPE_ERROR);
				}
				
			}
		});
		
		return btAlocar;
	}
	private void imprimirPedido(List<EstoqueMovel> alocacoes, String tecnico, String veiculo, String via) {
			try{		
					String Nome = StringUtil.md5(alocacoes.get(0).getData_cadastro().toString()+via);
					File f = new File("/DJMONITOR/PEDIDO"+Nome+".txt");
					//File f = new File("E:\\DJMONITOR\\PEDIDO"+Nome+".txt");
					
					BufferedWriter br = new BufferedWriter(new FileWriter(f));  
					 					
					if(f.canWrite()){
						
						StringBuilder s = new StringBuilder();
						String quebra = System.getProperty("line.separator");
					
						s.append("                          D I G I T A L");
						s.append(quebra);
						s.append("  Rua Padre Cicero, 07 Sao Pedro Belo Jardim/PE CEP: 55.150-340");
						s.append(quebra);					
						s.append("             CNPJ: 33.056.081/0001-09 IE: 081948581");
						s.append(quebra);
						s.append(quebra);
						s.append(quebra);
										
						s.append("                            ALOCACAO");
						s.append(quebra);
						
						s.append(quebra);
							
						s.append("----------------------------------------------------------------"+quebra);					
						s.append("                     NAO E DOCUMENTO FISCAL");
						s.append(quebra);
						s.append("                    NAO E VALIDO COMO RECIBO");
						s.append(quebra);										
						s.append("----------------------------------------------------------------"+quebra);
									
						SimpleDateFormat formHora= new SimpleDateFormat("HH:mm:ss");
						String hora = formHora.format(alocacoes.get(0).getData_ultima_alteracao());
					
						SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
						String hoje = form.format(alocacoes.get(0).getData_ultima_alteracao());
					
						
						s.append("NATUREZA OPERACAO: ALOCAÇÃO");
						s.append(quebra);
						s.append("DATA DE EMISSAO: "+hoje+" AS "+hora);
						s.append(quebra);
						s.append("EMITIDO POR: "+OpusERP4UI.getUsuarioLogadoUI().getUsername().toString());
						s.append(quebra);				
						s.append("TECNICO: "+tecnico);
						s.append(quebra);
						s.append("VEICULO: "+veiculo);
						s.append(quebra);
						s.append(quebra);
						s.append(quebra);
						
						s.append("COD     DESCRICAO");
						s.append(quebra);	
						s.append("QTD     VALOR R$      TOTAL R$   ");
						s.append(quebra);	
					
		
						s.append("----------------------------------------------------------------");
						s.append(quebra);	
					
						
						double totalItens = 0;
						for (EstoqueMovel estoqueMovel: alocacoes) {
														
								
								Produto p = estoqueMovel.getProduto();				
								double total = p.getValorVenda() * estoqueMovel.getQtd();
										
								s.append(p.getId().toString()+"    "+p.getNome());
								s.append(quebra);			
					     		s.append(estoqueMovel.getQtd()+"     "+Real.formatDbToString(p.getValorVenda().toString())+"         "+Real.formatDbToString(String.valueOf(total)));
					     		s.append(quebra);			
					     		
					     		totalItens = totalItens + total;
						
						}
						s.append("----------------------------------------------------------------");
						s.append(quebra);	
						s.append(String.format("%29s",Real.formatDbToString(String.valueOf(totalItens))));
						s.append(quebra);	
						
						s.append(quebra);
						
						
						s.append(quebra);
						s.append(quebra);
						s.append(quebra);
						s.append(quebra);
						
						s.append("          _____________________________________________");		
						s.append(quebra);
						s.append(quebra);
						
						br.write(s.toString());  
						br.close();							
		
						ScpUtil.transferir("root", OpusERP4UI.getEnderecoSamba(), OpusERP4UI.getSenhaSamba(), "/DJMONITOR/PEDIDO"+Nome+".txt", "/dados/PRINT/DIGITAL/PEDIDO"+Nome+".txt");
		
					}
					
					
					
					
					
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	private Button btDevolver;
	private Button btImprimir;
	private Button btTransferencia;
	
	private Button buildbtTransferencia(){
		
		btTransferencia = new Button("Transferência", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				
			}
		});
		
		return btTransferencia;
	}
	
	private Button buildBtDevolver(){
		
		btDevolver = new Button("Estornar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Estornar"))				
				{
					
					if(tb.getValue() != null){
												
						EntityItem<EstoqueMovel> entityItem = (EntityItem<EstoqueMovel>)tb.getItem(tb.getValue());
						final EstornarMaterialEditor estornarEditor = new EstornarMaterialEditor("Estornar Material", entityItem.getEntity(), true);
						estornarEditor.addListerner(new EstornarMaterialEditor.EstornarMaterialListerner() {
							
							@Override
							public void onClose(EstornarMaterialEvent event) {
								if(event.isConfirm()){
									
									EstoqueMovel estoqueMovel = event.getEstoqueMovel();									
									Integer codEstoque = estoqueMovel.getId();
									double qtdEstornar = event.getQtdEstornar();																	
									boolean check = MateriaisAlocadosDAO.estornarMaterial(codEstoque,qtdEstornar);
									
									if(check){
										Notify.Show("Estoque Estornado com Sucesso !", Notify.TYPE_SUCCESS);
										refresh();
									}
								}
								
								estornarEditor.close();
							}
						});
						
						getUI().addWindow(estornarEditor); 
				
					}
				}else{
					Notify.Show( "Você não Possui Permissão para Estornar Materiais", Notify.TYPE_ERROR);
				}
				
			}
		});
		
		
		btDevolver.setEnabled(false);
		return btDevolver;
	}
	
	TextArea txtInfoAdicionais ;
	private Button buildBtEditarInfo(){
		btEditarInfo = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				final Window wEditInfoAdicionais = new Window("Editar Informações Adicionais");
				wEditInfoAdicionais.setWidth("679px");
				wEditInfoAdicionais.setHeight("371px");
				
				wEditInfoAdicionais.center();
				wEditInfoAdicionais.setModal(true);
				
				wEditInfoAdicionais.setContent(new VerticalLayout(){
					{
						
						setWidth("100%");
						setMargin(true);
						
						addComponent(new VerticalLayout(){
							{
								
								setSizeFull();
								setMargin(true);
								setStyleName("border-form");
								
								addComponent(new FormLayout(){
									{
										setStyleName("form-cutom-new");
										setMargin(true);
										setSpacing(true);
										
										TextField txtVeiculo = new TextField("Veiculo");
										txtVeiculo.setStyleName("caption-align");
										if(tb != null && 
											tb.getValue() != null &&
											tb.getItem(tb.getValue()) != null &&
											tb.getItem(tb.getValue()).getItemProperty("veiculo").getValue() != null){
											
											Veiculos v = (Veiculos)tb.getItem(tb.getValue()).getItemProperty("veiculo").getValue();
											txtVeiculo.setValue(v.getId().toString()+", "+v.getMarca()+", "+v.getModelo());
											txtVeiculo.setEnabled(false);							
										}
										addComponent(txtVeiculo);
										
										
										TextField txtProduto = new TextField("Produto");
										txtProduto.setStyleName("caption-align");
										if(tb != null && 
												tb.getValue() != null &&
												tb.getItem(tb.getValue()) != null &&
												tb.getItem(tb.getValue()).getItemProperty("produto").getValue() != null){
											
											Produto v = (Produto)tb.getItem(tb.getValue()).getItemProperty("produto").getValue();
											txtProduto.setValue(v.getNome());
											txtProduto.setEnabled(false);
										}
										
										addComponent(txtProduto); 
								
										
										DateField txtDataCadastro = new DateField("Data cadastro");
										if(tb != null && 
												tb.getValue() != null &&
												tb.getItem(tb.getValue()) != null &&
												tb.getItem(tb.getValue()).getItemProperty("data_cadastro").getValue() != null){
											txtDataCadastro.setValue((Date)tb.getItem(tb.getValue()).getItemProperty("data_cadastro").getValue());
											txtDataCadastro.setEnabled(false); 
										}
										
										addComponent(txtDataCadastro); 
										
										
										DateField txtDataUltimaAlteracao = new DateField("Data ultima alteração");
										if(tb != null && 
												tb.getValue() != null &&
												tb.getItem(tb.getValue()) != null &&
												tb.getItem(tb.getValue()).getItemProperty("data_ultima_alteracao").getValue() != null){
											
											txtDataUltimaAlteracao.setValue((Date)tb.getItem(tb.getValue()).getItemProperty("data_ultima_alteracao").getValue());
											txtDataUltimaAlteracao.setEnabled(false); 
										}
										
										addComponent(txtDataUltimaAlteracao);
										
										txtInfoAdicionais = new TextArea("Info. Adicionais");
										txtInfoAdicionais.setWidth("440px");
										txtInfoAdicionais.setHeight("80px");
										if(tb != null && 
												tb.getValue() != null &&
												tb.getItem(tb.getValue()) != null &&
												tb.getItem(tb.getValue()).getItemProperty("info_adicionais").getValue() != null){
											
											txtInfoAdicionais.setValue(tb.getItem(tb.getValue()).getItemProperty("info_adicionais").getValue().toString());									
										}
										txtInfoAdicionais.focus();
										
										addComponent(txtInfoAdicionais); 
										
									}
								});
							}
						});
						
						addComponent(new HorizontalLayout(){
							{
								setStyleName("hl_buttons_bottom");
								setSpacing(true);
								setMargin(true);
								setWidth("100%");
								
								Button btSalvar = new Button("Ok", new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										
										EntityManager em = ConnUtil.getEntity();
										
										EntityItem<EstoqueMovel> entityItem = (EntityItem<EstoqueMovel>)tb.getItem(tb.getValue());
										EstoqueMovel estoque = entityItem.getEntity();
										estoque.setInfo_adicionais(txtInfoAdicionais.getValue());
										
										em.getTransaction().begin();
										em.merge(estoque);
										em.getTransaction().commit();				
										
										Notify.Show("Alterado com sucesso!", Notify.TYPE_SUCCESS);
										refresh();
										
										
										wEditInfoAdicionais.close();
									}
								});
								
								btSalvar.setStyleName("default");								
								addComponent(btSalvar);
								setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
								
							}
						});
						
					}
				});
				
				
				getUI().addWindow(wEditInfoAdicionais); 
				
				
			}
		});
		
		
		return btEditarInfo;
	}
	
	Button btEditarInfo;
	
	private Button buildBtRelatorio(){
		Button btRelatorio = new Button("Relatorio", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Relatorio de Materiais"))				
				{
				
					 if (winSubRelatorio != null && winSubRelatorio.getUI() != null)
						 winSubRelatorio.close();
		             else {
		            	 
		            	
		            	 if(winSubRelatorio != null && winSubRelatorio.getUI() != null){
		            		 winSubRelatorio.close();
		            	 }
		            	
		            	 
		            	 
		            	 buildSubRelatorio(event);
					     
						 getUI().addWindow(winSubRelatorio);
						 winSubRelatorio.focus();
		                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
		                             
		               			@Override
		                        public void layoutClick(LayoutClickEvent event) {
		               				winSubRelatorio.close();
		                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
		                        }
		                 });
		             }
				 
				}else{				
					Notify.Show("Você não Possui Permissão para Visualizar o relatório de Materiais",Notify.TYPE_ERROR);				
				}
			}
		});
		
		return btRelatorio;
	}

	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}
	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	

}
