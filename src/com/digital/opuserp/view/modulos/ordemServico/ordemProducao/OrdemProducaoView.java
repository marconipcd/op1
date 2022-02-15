package com.digital.opuserp.view.modulos.ordemServico.ordemProducao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

import org.joda.time.DateTime;
import org.vaadin.addons.idle.Idle;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlertaPendenciaDAO;
import com.digital.opuserp.dao.AlteracoesOspDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.OspDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.domain.AlteracoesOsp;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Osp;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.ordemServico.ordemProducao.EditarOspEditor.EdicaoOspEvent;
import com.digital.opuserp.view.modulos.ordemServico.ordemProducao.ImportarEditor.ImportarEvent;
import com.digital.opuserp.view.modulos.ordemServico.ordemProducao.InformarOcorrenciaEditor.OcorreciaOspEvent;
import com.digital.opuserp.view.modulos.ordemServico.ordemProducao.ReagendarEditor.ReagendarOspEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.QueryModifierDelegate;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.addon.jpacontainer.filter.JoinFilter;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class OrdemProducaoView extends VerticalLayout {

	private JPAContainer<Osp> container;	
	private Table tb;
	private TextField tfBusca;	
	
	
	private ComboBox cbSetor;	
	private Button btImpotarPedido;
	private Button btEditar;
	private Button btEncaminhar;
	private Button btOCorrencia;
	private Button btConcluir;
	private Button btReagendar;
	private Button btFinalizar;
	private Button btEntregar;
	private Button btVisualizar;
	private Button btExcluir;
	private Button btRefresh;
	private Button btQualidade;
	private Button btExecutar;
	private Button btSolicAprov;
	private Button btReprovar;
	private Button btLog;

	
	private HorizontalLayout hlFloat;
	private Label lbRegistros;	
	private ShortcutListener slNovo;
	private ShortcutListener slEditar;	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	private DateField dfDataPrevisao;
	
	public OrdemProducaoView(boolean act, Integer codSubModulo){		
		
		if(act){
			this.codSubModulo = codSubModulo;
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.setWidth("100%");
						
			hlButons.addComponent(BuildbtRefresh());
			hlButons.setExpandRatio(btRefresh, 1.f);
			
			hlButons.addComponent(BuildbtImportarPedido());
			hlButons.addComponent(BuildbtEditar());
			hlButons.addComponent(BuildbtQualidade());
			hlButons.addComponent(BuildbtExecutar());
			hlButons.addComponent(BuildbtVisualizar());
			hlButons.addComponent(buildbtExcluir());
			hlButons.addComponent(buildbtLog());
			
			addComponent(hlButons);
			
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildCbAtras());
					addComponent(buildCbSetor());
					addComponent(buildDfPrevisao());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(lbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar ");
			lbLegend.setWidth("250px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
			
			Idle.track(OpusERP4UI.getCurrent(), 300000, new Idle.Listener() {
				
	            @Override
	            public void userInactive() {
	            	if(OpusERP4UI.getCurrent().getUI().getWindows().size() == 0){
						refresh_now();
					}	            	
	            }
	
	            @Override
	            public void userActive() {
	            	
	            }
	        });
		}
	}
	
	DateField dfPrevisao;
	private DateField buildDfPrevisao(){
		
		dfPrevisao = new DateField();
		dfPrevisao.setDateFormat("dd/MM/yyyy");
		dfPrevisao.setImmediate(true);
		
		dfPrevisao.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		
		return dfPrevisao;
	}
	
	private Button buildbtLog() {
		btLog = new Button("Log", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final Set<Object> selecteds = (Set<Object>)tb.getValue();
				
				if(selecteds.size() == 1){
					HistoricoAlteracoes historicoAlteracoes = new HistoricoAlteracoes(true, true, (Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue());				
					getUI().addWindow(historicoAlteracoes);
				}
			}
		});
		
		btLog.setEnabled(false);
		return btLog;
	}

	public void refresh_now(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
				
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
		btEditar.setEnabled(false);
		btVisualizar.setEnabled(false);
		btExcluir.setEnabled(false);
		
		if(tfBusca.getValue() != null){
			addFilter(tfBusca.getValue());
		}
		//container.refresh();
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof OrdemProducaoView)
		{
			btImpotarPedido.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btImpotarPedido != null || slNovo != null){				
				btImpotarPedido.removeShortcutListener(slNovo);
			}
			
			if(btEditar != null || slEditar != null){
				btEditar.removeShortcutListener(slEditar);			
			}
		}
	}
	
	public Label lbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btImpotarPedido.click();
			}
		};
		return slNovo;
	}
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btEditar.click();
			}
		};
		return slEditar;
	}
	public JPAContainer<Osp> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Osp.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
				
		if(cbSetor.getValue() != null && cbSetor.getValue().equals("ENTREGUE")){
			container.addContainerFilter(Filters.eq("status", "P"));
		}else{
			container.addContainerFilter(Filters.not(Filters.eq("status", "P")));
		}
		
		if(cbSetor.getValue() != null && !cbSetor.getValue().equals("TODOS") && !cbSetor.getValue().equals("ENTREGUE")){
			container.addContainerFilter(Filters.eq("setor", cbSetor.getValue()));
		}
		
		//container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("servico.nome");
	//container.setAdditionalFilterablePropertyIds("cliente_nome_razao");
	
		return container;
	}
	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
			
				if (v instanceof Date) {
				         Date dateValue = (Date) v;
				         return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateValue);
				}
				
				if (v instanceof Boolean) {
			         boolean booleanValue = (boolean) v;
			         
			         if(booleanValue){
			        	 return "SIM";
			         }else{
			        	 return "NAO";
			         }
				}
				if(colId.equals("cliente")){
					if(tb.getItem(rowId).getItemProperty("cliente").getValue() != null){
						return ((Cliente)tb.getItem(rowId).getItemProperty("cliente").getValue()).getNome_razao();
					}else{
						return "";
					}
				}
				if(colId.equals("status")){
					if(v != null){
						if(v.equals("A")){
							return "ABERTO";
						}else if(v.equals("P")){
							return "ENTREGUE";
						}else if(v.equals("F")){
							return "CONCLUÍDO";
						}else if(v.equals("E")){
							return "ENCAMINHADO";
						}else if(v.equals("AP")){
							return "APROVAÇÃO";
						}
					}
				}
				
				 if(colId.equals("id")){
						
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				 }
				 
				 if(colId.equals("venda_servico_cabecalho_id")){
						
						if(tb.getItem(rowId).getItemProperty("venda_servico_cabecalho_id").getValue() != null){						
							return ((Integer)tb.getItem(rowId).getItemProperty("venda_servico_cabecalho_id").getValue()).toString();				
						}					
				 }
				
				return super.formatPropertyValue(rowId, colId, property);
			}
		};		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setMultiSelect(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setVisibleColumns(new Object[] {"id","venda_servico_cabecalho_id","cliente","comprador","data_previsao_termino","entregar","servico.nome","setor","status","operador_producao","operador_abertura","observacao"});
					
		tb.setColumnHeader("id", "Cod.");		
		tb.setColumnHeader("venda_servico_cabecalho_id", "Pedido");
		tb.setColumnHeader("cliente", "Cliente");
		tb.setColumnHeader("comprador", "Comprador");
		tb.setColumnHeader("data_previsao_termino", "Data Previsão");
		tb.setColumnHeader("servico.nome", "Descrição Serviço");
		tb.setColumnHeader("setor", "Setor");
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("operador_producao", "Operador Produção");
		
		tb.setColumnHeader("operador_abertura", "Operador Abertura");
		tb.setColumnHeader("observacao", "Observação");
		tb.setColumnHeader("entregar", "Entrega");
		
		tb.setColumnCollapsed("operador_abertura", true);
		tb.setColumnCollapsed("observacao", true);
		
		
		//tb.setConverter("id", null);
		//tb.setConverter("venda_servico_cabecalho_id", null);
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					btExecutar.setEnabled(true);
					btEditar.setEnabled(true);				
					btVisualizar.setEnabled(true);
					btExcluir.setEnabled(true);		
					btQualidade.setEnabled(true);
					btLog.setEnabled(true);
					
					
					if(btOCorrencia != null){
						btOCorrencia.setEnabled(true); 
					}
					
					if(btVisualizar != null){
						btVisualizar.setEnabled(true); 
					}
					
					if(selecteds.size() > 1){
						btLog.setEnabled(false);
						btEditar.setEnabled(false);
						
						if(btOCorrencia != null){
							btOCorrencia.setEnabled(false); 
						}
						
						if(btVisualizar != null){
							btVisualizar.setEnabled(false); 
						}
					}
					
				}else{
					btExecutar.setEnabled(false);
					btEditar.setEnabled(false);
					btVisualizar.setEnabled(false);
					btExcluir.setEnabled(false);
					btQualidade.setEnabled(false);
					btLog.setEnabled(false);
					
					if(btOCorrencia != null){
						btOCorrencia.setEnabled(false); 
					}
					
					if(btVisualizar != null){
						btVisualizar.setEnabled(false); 
					}
				}	
				
			}
		});
			
		
		tb.sort(new Object[]{"data_previsao_termino"}, new boolean[]{true});
				
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {

				 if (propertyId == null){
	                    return "row-header-default"; // Will not actually be visible
				 }else{
	               
	                Item item = source.getItem(itemId);
	                
	                if(item != null && item.getItemProperty("status") != null && item.getItemProperty("status").getValue() != null){
		                String status = item.getItemProperty("status").getValue().toString();
		               
		                if(status.equals("E")){
		                	return "row-header-osi-encaminhado";    
		                }else if(status.equals("AP")){
		                	return "row-header-osp-aprovacao";    
		                }	                
	                }
	                
	                try{
	                	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                	Date data_ex = (Date)source.getItem(itemId).getItemProperty("data_previsao_termino").getValue();
	                	
	                	
	                	if(data_ex != null && item.getItemProperty("status").getValue() != null && !sdf.parse(sdf.format(data_ex)).after(sdf.parse(sdf.format(new Date()))) && !item.getItemProperty("status").getValue().toString().equals("P")){
	                		return "row-header-atrasado";
	                	}else{		                		
	                		return "row-header-default";
	                	}
                	}catch(Exception e){
                		e.printStackTrace();
                		return "row-header-default";
                	}
	                
				 }
			}
		});
		
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					btEditar.click();
				}
			}
		});
				
		return tb;
	}
	
	public DateField buildDfbusca(){
		dfDataPrevisao = new DateField();
		dfDataPrevisao.setDateFormat("dd/MM/yyyy");
		
		return dfDataPrevisao;
	}
	
	private ComboBox cbAtras;
	public ComboBox buildCbAtras(){
		cbAtras = new ComboBox();
		cbAtras.setWidth("105px");
		cbAtras.setNullSelectionAllowed(false);
		cbAtras.setTextInputAllowed(false);
		cbAtras.addItem("TODOS");
		cbAtras.addItem("ATRASADOS");		
		cbAtras.select("TODOS"); 		
		cbAtras.setImmediate(true);
		cbAtras.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		
		return cbAtras;
	}
	
	public ComboBox buildCbSetor(){
		cbSetor = new ComboBox(null);
		cbSetor.setNullSelectionAllowed(false);
		cbSetor.setTextInputAllowed(false);
				
		cbSetor.addItem("TODOS");
		cbSetor.addItem("CRIACAO");
		cbSetor.addItem("PRE-IMPRESSAO");
		cbSetor.addItem("IMPRESSAO");
		cbSetor.addItem("ACABAMENTO");
		cbSetor.addItem("QUALIDADE");
		cbSetor.addItem("EXPEDICAO");
		
		if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar Entregues"))				
		{
			cbSetor.addItem("ENTREGUE");
		}
		
		cbSetor.select("CRIACAO");
		cbSetor.setImmediate(true);
		cbSetor.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		
		return cbSetor;
	}
		
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.focus();
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.setImmediate(true);
		tfBusca.setInputPrompt("Buscar...");
		tfBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}

	public void addFilter(String s) {
		
		container.setApplyFiltersImmediately(false);
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(cbAtras.getValue() != null){
			if(cbAtras.getValue().equals("ATRASADOS")){				
				container.addContainerFilter(Filters.lt("data_previsao_termino", new Date()));
			}
		}
				
		if(cbSetor.getValue() != null && cbSetor.getValue().equals("ENTREGUE")){
			container.addContainerFilter(Filters.eq("status", "P"));
		}else{
			container.addContainerFilter(Filters.not(Filters.eq("status", "P")));
		}
		
		if(cbSetor.getValue() != null && !cbSetor.getValue().equals("TODOS") && !cbSetor.getValue().equals("ENTREGUE")){
			container.addContainerFilter(Filters.eq("setor", cbSetor.getValue()));
		}
		
		if(dfPrevisao.getValue() != null){
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
				DateTime dt = new DateTime(dfPrevisao.getValue());
				dt.minusDays(1);
				
				container.addContainerFilter(Filters.gteq("data_previsao_termino", sdf.parse(DataUtil.formatDateBra(dt.toDate())+" 00:00:00")));
				container.addContainerFilter(Filters.lteq("data_previsao_termino", sdf.parse(DataUtil.formatDateBra(dfPrevisao.getValue())+" 23:59:00")));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
			for(Object c:collums){		 			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
			
		} catch (Exception e) {
			
			for(Object c:collums){
				
				try{
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){	
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						filtros.add(Filters.eq(c.toString(), sdf.parse(s+":00")));
					}else{
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							if(c.toString().equals("cliente_nome_razao")){
								if(s != null && !s.equals("") && !s.isEmpty()){
									filtros.add(new Like(c.toString(), "%"+s+"%", false));
								}
							}else{
								
								filtros.add(new Like(c.toString(), "%"+s+"%", false));
							}
						}
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Cliente.class){
							if(s != null && !s.equals("") && !s.isEmpty()){
								
								filtros.add(new JoinFilter("cliente", new Like("nome_razao", "%"+s+"%", false)));							
							}
						}
					}
				}catch(Exception ex){
					//ex.printStackTrace();
					
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){			
							
							filtros.add(new Like(c.toString(), "%"+s+"%", false));

					}
					
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Cliente.class){
						if(s != null && !s.equals("") && !s.isEmpty()){
							filtros.add(Filters.or(new Like("cliente.nome_razao", "%"+s+"%", false), new IsNull("cliente")));
						}
					}
				}
				
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
		
	}
	
	public Button BuildbtQualidade() {
		btQualidade= new Button("Qualidade", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				 if (winSubMenuQualidade != null && winSubMenuQualidade.getUI() != null)
					 winSubMenuQualidade.close();
	             else {
	            	 
	            	 if(winSubMenuNovo != null && winSubMenuNovo.getUI() != null){
	            		 winSubMenuNovo.close();
	            	 }
	            	             	 
				     buildSubMenuQualidade(event);
				     
					 getUI().addWindow(winSubMenuQualidade);
					 winSubMenuQualidade.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuQualidade.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }				
			}
		});
		btQualidade.setEnabled(false); 
		return btQualidade;
	}
	
	public Button BuildbtExecutar() {
		btExecutar= new Button("Executar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				 if (winSubMenuNovo != null && winSubMenuNovo.getUI() != null)
					 winSubMenuNovo.close();
	             else {
	            	 
	            	 if(winSubMenuQualidade != null && winSubMenuQualidade.getUI() != null){
	            		 winSubMenuQualidade.close();
	            	 }

	            	             	 
				     buildSubMenu(event);
				     
					 getUI().addWindow(winSubMenuNovo);
					 winSubMenuNovo.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	              				winSubMenuNovo.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }				
			}
		});
		btExecutar.setEnabled(false); 
		return btExecutar;
	}
	
	Window winSubMenuNovo;
	private void buildSubMenu(ClickEvent event) {
		
		winSubMenuNovo = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        winSubMenuNovo.setContent(l);
        winSubMenuNovo.setWidth("300px");
        winSubMenuNovo.addStyleName("notifications");
        winSubMenuNovo.setClosable(false);
        winSubMenuNovo.setResizable(false);
        winSubMenuNovo.setDraggable(false);
        winSubMenuNovo.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuNovo.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuNovo.setCloseShortcut(KeyCode.ESCAPE, null);
       
        l.addComponent(BuildbtOcorrencia());
        l.addComponent(BuildbtReagendar());
        l.addComponent(BuildbtEncaminhar());
        l.addComponent(BuildbtConcluir());
        l.addComponent(buildbtFinalizar());
        l.addComponent(buildbtEntregar());
    }
	
	Window winSubMenuQualidade;
	private void buildSubMenuQualidade(ClickEvent event) {
		
		winSubMenuQualidade = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        winSubMenuQualidade.setContent(l);
        winSubMenuQualidade.setWidth("300px");
        winSubMenuQualidade.addStyleName("notifications");
        winSubMenuQualidade.setClosable(false);
        winSubMenuQualidade.setResizable(false);
        winSubMenuQualidade.setDraggable(false);
        winSubMenuQualidade.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuQualidade.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuQualidade.setCloseShortcut(KeyCode.ESCAPE, null);
       
        l.addComponent(BuildBtSolicAprov());
        l.addComponent(BuildBtReprovar());        
    }
	
	public Button BuildbtImportarPedido(){
		
		btImpotarPedido = new Button("Importar Pedido", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Importar"))				
				{
					final ImportarEditor importarEditor = new ImportarEditor("Importar Pedido", true);
					importarEditor.addListerner(new ImportarEditor.ImportarListerner() {
						
						@Override
						public void onClose(ImportarEvent event) {
							if(event.isConfirm()){
								
								EcfPreVendaCabecalho p = event.getPedido();
								List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(p.getId());
	
								for (EcfPreVendaDetalhe item: itens) {
									Servico s = ServicoDAO.find(item.getProdutoId());
												
									if(s != null && s.getProducao().equals("SIM")){
										boolean entregar = false;
										if(p.getEntregar().equals("SIM")){
											entregar = true;
										}
																		
										Osp osp = OspDAO.save(new Osp(null, OpusERP4UI.getEmpresa().getId(), p.getCliente(), p.getId(), s, s.getNome(), new Double(item.getQuantidade().toString()), "", item.getOrdem(), null, new Date(), event.getPrevisao(), 
												null, null, null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), null, entregar, null, "A",event.getSetor(),p.getComprador()));
										
										AlteracoesOspDAO.add(new AlteracoesOsp(null, osp.getSetor()+" IMPORTADA", osp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									} 
								}
								
								importarEditor.close();
								
								refresh_now();
								cbSetor.select("CRIACAO");
								Notify.Show("Importado com Sucesso!", Notify.TYPE_SUCCESS);
							}
						}
					});
					
					importarEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
					
					getUI().addWindow(importarEditor);
				}else{
					Notify.Show("Você não Possui Permissão para Importar",Notify.TYPE_ERROR);
				}
				
			}
		});
		
		return btImpotarPedido;
	}
	
	public Button BuildBtSolicAprov(){
		final Set<Object> selecteds = (Set<Object>)tb.getValue();
		btSolicAprov = new Button("Solicitar Aprovação", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Solicitar Aprovação"))				
				{
				
						for (Object object : selecteds) {
							try{
								EntityItem<Osp> entityOsp = (EntityItem<Osp>)tb.getItem(object);			
								Osp osp = entityOsp.getEntity();						
								osp.setStatus("AP");						
								
								OspDAO.save(osp);
								refresh_now();
								
								AlteracoesOspDAO.add(new AlteracoesOsp(null, "APROVAÇÃO", osp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
								Notify.Show("Aprovação Solicitada!", Notify.TYPE_SUCCESS);
										
							}catch(Exception e){
								e.printStackTrace();
							}
						}
				
				}else{
					Notify.Show("Você não Possui Permissão para Solicitar a Aprovação para Esta Ordem de Serviço",Notify.TYPE_ERROR);
				}
			}
		});
		
		btSolicAprov.setPrimaryStyleName("btSubMenu");
		return btSolicAprov;
	}
	
	public Button BuildBtReprovar(){
		final Set<Object> selecteds = (Set<Object>)tb.getValue();
		btReprovar = new Button("Reprovar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Reprovar"))				
				{
					for (Object object : selecteds) {
						try{
							EntityItem<Osp> entityOsp = (EntityItem<Osp>)tb.getItem(object);			
							Osp osp = entityOsp.getEntity();						
							osp.setStatus("A");				
							osp.setOperador_producao(null); 
							
							OspDAO.save(osp);
							
							AlteracoesOspDAO.add(new AlteracoesOsp(null, "REPROVADO", osp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
							Notify.Show("Ordem de Serviço Reprovada!", Notify.TYPE_SUCCESS);
									
							refresh_now();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}else{
					Notify.Show("Você não Possui Permissão para Reprovar a Solicitação desta Ordem de Serviço",Notify.TYPE_ERROR);
				}
			}
		});
		
		btReprovar.setPrimaryStyleName("btSubMenu");
		return btReprovar;
	}
	
	public Button BuildbtEditar(){
		
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Editar"))				
				{
					
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					if(selecteds.size() == 1){
						
						final EditarOspEditor editarOspEditor = new EditarOspEditor(tb.getItem(selecteds.toArray()[0]), "Editar Item de Produção", true, gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Setor"));
						editarOspEditor.addListerner(new EditarOspEditor.EdicaoOspListerner() {
							
							@Override
							public void onClose(EdicaoOspEvent event) {
								
								try{
									
									if(event.isConfirm()){
										tb.commit();
										container.commit();
										refresh_now();
										
										EntityItem<Osp> entityOsp = (EntityItem<Osp>)event.getItem();
										AlteracoesOspDAO.add(new AlteracoesOsp(null, entityOsp.getEntity().getSetor()+" EDITADA", entityOsp.getEntity(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										
										Notify.Show("Alterações Salvas Com Sucesso", Notify.TYPE_SUCCESS);
										editarOspEditor.close();
									}else{
										editarOspEditor.close();
									}
								}catch(Exception e){
									e.printStackTrace();
									container.discard();
									tb.discard();
									
									Notify.Show("ERRO: "+e.getMessage(), Notify.TYPE_ERROR);
								}
							}
						});
						
						editarOspEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(editarOspEditor);
						
					}else{
						Notify.Show("Selecione Apena uma Osp para Editar!",Notify.TYPE_WARNING);
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Editar Ordem de Serviço",Notify.TYPE_ERROR);
				}
				
			}
		});
		
		btEditar.setEnabled(false);
		return btEditar;
	}
	
	public Button BuildbtEncaminhar(){
		
		final Set<Object> selecteds = (Set<Object>)tb.getValue();
		btEncaminhar = new Button("Encaminhar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Encaminhar"))				
				{
					if(tb.getValue() != null){
						GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja realmente Encaminhar esta Ordem de Serviço ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								
								if(event.isConfirm()){
									try{
										
										
										for (Object object : selecteds) {
										
											EntityItem<Osp> entityOsp = (EntityItem<Osp>)tb.getItem(object);			
											Osp osp = entityOsp.getEntity();
											if(!osp.getStatus().equals("E")){
												osp.setStatus("E");
												osp.setOperador_producao(OpusERP4UI.getUsuarioLogadoUI().getUsername());
												OspDAO.save(osp);
												refresh_now();
											
												AlteracoesOspDAO.add(new AlteracoesOsp(null, osp.getSetor()+" ENCAMINHADA", osp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
												AlertaPendenciaDAO.removePendencia(codSubModulo, osp.getId());
												
												Notify.Show("Ordem de Serviço "+osp.getId().toString()+" Encaminhada com Sucesso!", Notify.TYPE_SUCCESS);
											}
										}
												
									}catch(Exception e){
										e.printStackTrace();
									}
								}
							}
						});
						
						gd.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(gd);					
					}
				}else{
					Notify.Show("Você não Possui Permissão para Encaminhar Ordem de Serviço",Notify.TYPE_ERROR);
				}
			}
		});
		
		if(selecteds.size() > 0){
			btEncaminhar.setEnabled(true);
		}else{			
			btEncaminhar.setEnabled(false);		
		}
		
		btEncaminhar.setPrimaryStyleName("btSubMenu");
		return btEncaminhar;
	}
	public Button BuildbtOcorrencia(){
		
		final Set<Object> selecteds = (Set<Object>)tb.getValue();
		btOCorrencia = new Button("Ocorrência", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
								
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Ocorrencia"))				
				{
					
					
					if(selecteds.size() == 1){
					
						final InformarOcorrenciaEditor informarOcorrenciaEditor = new InformarOcorrenciaEditor(tb.getItem(selecteds.toArray()[0]), "Ocorrência", true);
						informarOcorrenciaEditor.addListerner(new InformarOcorrenciaEditor.OcorrenciaOspListerner() {
							
							
							public void onClose(OcorreciaOspEvent event) {
								if(event.isConfirm()){
									
									EntityItem<Osp> entityOsp = (EntityItem<Osp>)tb.getItem(selecteds.toArray()[0]);			
									Osp osp = entityOsp.getEntity();
									
									AlteracoesOspDAO.add(new AlteracoesOsp(null, event.getOcorrencia(), osp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									informarOcorrenciaEditor.close();
									
									Notify.Show("Ocorrência Informada com Sucesso!", Notify.TYPE_SUCCESS);
								}
							}
						});
						
						informarOcorrenciaEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(informarOcorrenciaEditor);
						
					}else{
						Notify.Show("Selecione Apena uma Osp para Informar uma Ocorrência",Notify.TYPE_WARNING);
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Registrar uma Ocorrência",Notify.TYPE_ERROR);
				}
			}
		});
		
		
		if(selecteds.size() ==1){
			btOCorrencia.setEnabled(true);
		}else{			
			btOCorrencia.setEnabled(false);		
		}
		
		btOCorrencia.setPrimaryStyleName("btSubMenu");
		
		return btOCorrencia;
	}
	
	public Button BuildbtConcluir(){
		
		final Set<Object> selecteds = (Set<Object>)tb.getValue();
		btConcluir = new Button("Concluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Concluir"))				
				{
					
						GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja realmente Concluir esta Ordem de Serviço ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								
								if(event.isConfirm()){
									
									
									for (Object object : selecteds) {
									
										try{
											EntityItem<Osp> entityOsp = (EntityItem<Osp>)tb.getItem(object);			
											Osp osp = entityOsp.getEntity();
											
											if(!osp.getSetor().equals("EXPEDICAO") && (osp.getStatus().equals("E") || osp.getStatus().equals("AP"))){
											
												osp.setStatus("A");
												osp.setOperador_producao(null);
												String setor = osp.getSetor();
																						
												if(osp.getSetor().equals("CRIACAO")){
													osp.setSetor("PRE-IMPRESSAO");
												}else if(osp.getSetor().equals("PRE-IMPRESSAO")){
													osp.setSetor("IMPRESSAO");
												}else if(osp.getSetor().equals("IMPRESSAO")){
													osp.setSetor("ACABAMENTO");
												}else if(osp.getSetor().equals("ACABAMENTO")){
													osp.setSetor("QUALIDADE");
												}else if(osp.getSetor().equals("QUALIDADE")){
													osp.setSetor("EXPEDICAO");
												}
												
												OspDAO.save(osp);
												refresh_now();							
											
												AlteracoesOspDAO.add(new AlteracoesOsp(null, setor+" CONCLUÍDA", osp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
												
												Notify.Show("Ordem de Serviço Conluída com Sucesso!", Notify.TYPE_SUCCESS);											
											}
													
										}catch(Exception e){
											e.printStackTrace();
										}
									
									}
									
								}
							}
						});
						
						gd.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(gd);					
					
				
				}else{
					Notify.Show("Você não Possui Permissão para Concluir uma Ordem de Serviço",Notify.TYPE_ERROR);
				}
			}
		});
		
		if(selecteds.size() > 0){
			btConcluir.setEnabled(true);
		}else{			
			btConcluir.setEnabled(false);		
		}
		
		btConcluir.setPrimaryStyleName("btSubMenu");
		return btConcluir;
	}
	
	public Button BuildbtReagendar(){
		
		final Set<Object> selecteds = (Set<Object>)tb.getValue();
		btReagendar = new Button("Reagendar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Reagendar"))				
				{
						
							final ReagendarEditor reagendarEditor = new ReagendarEditor("Reagendar Ordem de Serviço", true);
							reagendarEditor.addListerner(new ReagendarEditor.ReagendarOspListerner() {
								
								@Override
								public void onClose(ReagendarOspEvent event) {
									if(event.isConfirm()){
										
										for (Object object : selecteds) {
											
											try{
												EntityItem<Osp> entityOsp = (EntityItem<Osp>)tb.getItem(object);			
												Osp osp = entityOsp.getEntity();
												osp.setData_previsao_termino(event.getData());
												
												OspDAO.save(osp);
												
												reagendarEditor.close();
												
												AlteracoesOspDAO.add(new AlteracoesOsp(null, "REAGENDADO", osp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
												Notify.Show("Ordes(s) de Serviço re-agendadas com Sucesso!",Notify.TYPE_ERROR);
												refresh_now();
														
											}catch(Exception e){
												e.printStackTrace();
											}
																			
										}
										
									}
								}
							});
							
							reagendarEditor.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									tb.focus();
								}
							});
							
							getUI().addWindow(reagendarEditor);					
					
				}else{
					Notify.Show("Você não Possui Permissão para Reagendar uma Ordem de Serviço",Notify.TYPE_ERROR);
				}
			}
		});
		
		btReagendar.setPrimaryStyleName("btSubMenu");
		
		if(selecteds.size() > 0){
			btReagendar.setEnabled(true);
		}else{			
			btReagendar.setEnabled(false);		
		}
		
		return btReagendar;
	}
	
	public Button buildbtFinalizar(){
		
		final Set<Object> selecteds = (Set<Object>)tb.getValue();
		btFinalizar= new Button("Finalizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Finalizar"))				
				{
					if(tb.getValue() != null){
						GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja realmente Finalizar esta Ordem de Serviço ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									
									for (Object object : selecteds) {
									
										try{
											EntityItem<Osp> entityOsp = (EntityItem<Osp>)tb.getItem(object);			
											Osp osp = entityOsp.getEntity();
											
											if(!osp.getStatus().equals("F")){
											
												osp.setStatus("F");			
												String setor = osp.getSetor();
												osp.setSetor("EXPEDICAO");
												osp.setData_termino(new Date());
												
												OspDAO.save(osp);
												refresh_now();
											
												//cbSetor.select("EXPEDICAO");
												AlteracoesOspDAO.add(new AlteracoesOsp(null, setor+" FINALIZADA", osp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
												AlertaPendenciaDAO.removePendencia(codSubModulo, osp.getId());
																								
												Notify.Show("Ordem de Serviço Finalizada com Sucesso!", Notify.TYPE_SUCCESS);
											}
													
										}catch(Exception e){
											e.printStackTrace();
										}
																		
									}
								}
							}
						});
						
						gd.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(gd);					
					}	
				}else{
					Notify.Show("Você não Possui Permissão para Finalizar uma Ordem de Serviço",Notify.TYPE_ERROR);
				}
			}
		});
		
		if(selecteds.size() > 0){
			btFinalizar.setEnabled(true);
		}else{			
			btFinalizar.setEnabled(false);		
		}
		
		btFinalizar.setPrimaryStyleName("btSubMenu");
		return btFinalizar;
	}
	
	public Button buildbtEntregar(){
		
		final Set<Object> selecteds = (Set<Object>)tb.getValue();
		btEntregar = new Button("Entregar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Entregar"))				
				{
					if(tb.getValue() != null){
						GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja realmente Entregar esta Ordem de Serviço ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									
									for (Object object : selecteds) {
										try{
											EntityItem<Osp> entityOsp = (EntityItem<Osp>)tb.getItem(object);			
											Osp osp = entityOsp.getEntity();
											
											if(!osp.getStatus().equals("P") && osp.getSetor().equals("EXPEDICAO")){
											
											
												osp.setStatus("P");						
																			
												OspDAO.save(osp);
												refresh_now();
											
												
												AlteracoesOspDAO.add(new AlteracoesOsp(null,osp.getSetor()+" ENTREGUE", osp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
												
												
												Notify.Show("Ordem de Serviço Entregue com Sucesso!", Notify.TYPE_SUCCESS);
												
											}
													
										}catch(Exception e){
											e.printStackTrace();
										}
									}
								}
							}
						});
						
						gd.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(gd);					
					}				
				
				}else{
					Notify.Show("Você não Possui Permissão para Entregar uma Ordem de Serviço",Notify.TYPE_ERROR);
				}
			}
		});
		
		if(selecteds.size() > 0){
			btEntregar.setEnabled(true);
		}else{			
			btEntregar.setEnabled(false);		
		}		
		
		btEntregar.setPrimaryStyleName("btSubMenu");
		return btEntregar;
	}
	private Button BuildbtRefresh(){
		btRefresh = new Button("Atualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				refresh_now();	
			}
		});
		
		return btRefresh;
	}
	public Button BuildbtVisualizar(){
		
		btVisualizar= new Button("Visualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar"))				
				{
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(tb.getValue() != null && selecteds.size() == 1){
						VisualizarOspEditor visualizarEditor = new VisualizarOspEditor(tb.getItem(selecteds.toArray()[0]), "Visualizar Ordem de Serviço", true);
						
						visualizarEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(visualizarEditor);
					}else{
						Notify.Show("Selecione apena uma OSP para Visualiza-la",Notify.TYPE_WARNING);
					}
				}else{
					Notify.Show("Você não Possui Permissão para Visualizar uma Ordem de Serviço",Notify.TYPE_ERROR);
				}
			}
		});
		
		btVisualizar.setEnabled(false);
		return btVisualizar;
	}
	
	public Button buildbtExcluir(){
		
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
					if(tb.getValue() != null){
						GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja realmente Excluir esta Ordem de Serviço ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									final Set<Object> selecteds = (Set<Object>)tb.getValue();
									
									for (Object object : selecteds) {
										try{	
											
											EntityItem<Osp> itemOsp = (EntityItem<Osp>)tb.getItem(object);
											OspDAO.removeRegistrosDependentes(itemOsp.getEntity());
											
											AlertaPendenciaDAO.removePendencia(codSubModulo, itemOsp.getEntity().getId());
											container.removeItem(object);
											container.commit();
											//refresh_now();
											
											Notify.Show("Ordem de Serviço Excluída com Sucesso!", Notify.TYPE_SUCCESS);
													
										}catch(Exception e){
											e.printStackTrace();
										}
									
									}
								}
							}
						});
						
						gd.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(gd);					
					}
				}else{
					Notify.Show("Você não Possui Permissão para Excluir uma Ordem de Serviço",Notify.TYPE_ERROR);
				}
			}
		});
		
		btExcluir.setEnabled(false);
		return btExcluir;
	}

	
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}

