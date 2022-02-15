package com.digital.opuserp.view.modulos.pedido.pedidoProduto;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.dao.FormaPgtoDAO;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.HaverDAO;
import com.digital.opuserp.dao.ItensPedidoDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SeriaisPedidoDAO;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.dao.TotaisPedidoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.domain.HaverDetalhe;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SeriaisPedido;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.domain.TotaisPedido;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.ScpUtil;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.FormaPgtoEditor.FormaPgtoEvent;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.ItemEditor.ItemEvent;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.PedidoEditor.PedidoEvent;
import com.digital.opuserp.view.util.Notify;
//import com.github.wolfie.refresher.Refresher;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
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
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class PedidoView extends VerticalLayout {
	
	JPAContainer<EcfPreVendaCabecalho> container;
	
	Table tb;
	TextField tfBusca;
	
	private Button btConsultarPreco;
	private Button btNovo;
	private Button btEditar;
	private Button btConverter;
	private Button btFinalizar;
	private Button btImprimir;
	private Button btCancelar;	
	private Button btVisualizar;	
	private Button btLogs;
	private Button btEnviarPrevenda;
	
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	ComboBox cbTipo;
	Window window;
	
	public PedidoView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.setWidth("100%");
			
			hlButons.addComponent(BuildbtConcultarPreco());
			hlButons.setComponentAlignment(btConsultarPreco, Alignment.TOP_LEFT);
			hlButons.setExpandRatio(btConsultarPreco, 2);
			
			hlButons.addComponent(BuildbtNovo());
			hlButons.addComponent(BuildbtEditar());		
			hlButons.addComponent(BuildbtConverterEmPedido());
			hlButons.addComponent(BuildbtFinalizar());
			hlButons.addComponent(BuildbtVisualizar());
			hlButons.addComponent(BuildbtImprimirDav());
			hlButons.addComponent(BuildbtCancelar());
			hlButons.addComponent(BuildbtLog());
			
			if(OpusERP4UI.getUsuarioLogadoUI().getUsername().equals("marconi")){
				hlButons.addComponent(new Button("Enviar PreVenda", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						if(tb.getValue() != null){
							EntityItem<EcfPreVendaCabecalho> ei_pedido = (EntityItem<EcfPreVendaCabecalho>)tb.getItem(tb.getValue());
							EnviarPreVenda(ei_pedido.getEntity());
						}
					}
				}));
			}
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(BuildCbTipo());
					addComponent(BuildCbStatus());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildLbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
			lbLegend.setWidth("285px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
			//addExtension(buildRefresher2());	
		}
	}

//	Refresher refresher2;
//	public Refresher buildRefresher2(){
//		
//		
//		refresher2 = new Refresher();
//		refresher2.setRefreshInterval(300000);
//		refresher2.addListener(new Refresher.RefreshListener() {
//			
//			@Override
//			public void refresh(Refresher source) {
//				replaceComponent(tb, buildTbGeneric());
//				setExpandRatio(tb, 1);		
//				//addFilter(tfBusca.getValue());
//				btEditar.setEnabled(false);
//				btConverter.setEnabled(false);
//				btFinalizar.setEnabled(false);
//				btImprimir.setEnabled(false);
//				btCancelar.setEnabled(false);
//				btVisualizar.setEnabled(false);
//				
//				refresh_qtd();
//			}
//		});
//		
//		return refresher2;
//	}
	
	private ComboBox cbStatus;
	private ComboBox BuildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		cbStatus.addItem("TODOS");
		cbStatus.addItem("ABERTO");
		cbStatus.addItem("CANCELADO");
		cbStatus.addItem("FINALIZADO");
		cbStatus.select("ABERTO");
		cbStatus.setImmediate(true);
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {

				
				
				refresh();
			}
		});
		
		return cbStatus;
	}
		
	private ComboBox BuildCbTipo(){
		
		cbTipo = new ComboBox(null);		
		cbTipo.setNullSelectionAllowed(false);
		cbTipo.setTextInputAllowed(false);
		cbTipo.setImmediate(true);		
		cbTipo.addItem("ORCAMENTO");
		cbTipo.addItem("PEDIDO");
		cbTipo.addItem("DEVOLUCAO");
		cbTipo.select("PEDIDO");
		
		cbTipo.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbTipo.getValue() != null){					
					refresh();
				}				
			}
		});
				
		return cbTipo;
		
	}
		
	public void refresh_qtd(){
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
		//addFilter(tfBusca.getValue());
		btEditar.setEnabled(false);
		btConverter.setEnabled(false);
		btFinalizar.setEnabled(false);
		btImprimir.setEnabled(false);
		btCancelar.setEnabled(false);
		btVisualizar.setEnabled(false);
		
		refresh_qtd();
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof PedidoView)
		{
			//btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
//			if(btNovo != null || slNovo != null){				
//				btNovo.removeShortcutListener(slNovo);
//			}
			
			if(btEditar != null || slEditar != null){
				btEditar.removeShortcutListener(slEditar);			
			}
		}
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
//	public ShortcutListener buildShortCutNovo(){
//		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
//			
//			
//			public void handleAction(Object sender, Object target) {
//				btNovo.click();
//			}
//		};
//		return slNovo;
//	}
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btEditar.click();
			}
		};
		return slEditar;
	}
	public JPAContainer<EcfPreVendaCabecalho> buildContainer(){
		container = JPAContainerFactory.makeBatchable(EcfPreVendaCabecalho.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));				
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("cliente.doc_cpf_cnpj");
		//container.addNestedContainerProperty("formaPagtoID.nome");
		container.addNestedContainerProperty("naturezaOperacao.descricao");
		
		if(cbTipo != null && cbTipo.getValue() != null){			
			container.addContainerFilter(Filters.eq("tipo", cbTipo.getValue().toString()));	
		}
		
		if(cbStatus != null && cbStatus.getValue() != null){
			if(cbStatus.getValue().toString().equals("ABERTO")){
				container.addContainerFilter(Filters.eq("situacao", "A"));				
			}
			if(cbStatus.getValue().toString().equals("CANCELADO")){
				container.addContainerFilter(Filters.eq("situacao", "C"));
			}
			if(cbStatus.getValue().toString().equals("FINALIZADO")){
				container.addContainerFilter(Filters.eq("situacao", "F"));
			}
		}
		
		if(tfBusca.getValue() != null && !tfBusca.getValue().isEmpty() && !tfBusca.getValue().equals("")){
			String s = tfBusca.getValue();
			Object[] collums = tb.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			try {
				Integer cod = Integer.parseInt(s);
				
				for(Object c:collums){		 			
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
						filtros.add(new Like(c.toString(), "%"+cod+"%", false));
					}	
				}
				
			} catch (Exception e) {
				
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date = sdf.parse(s);
					
					for(Object c:collums){
						
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
							filtros.add(Filters.eq(c.toString(), date));
						}	
						
						
					}
				}catch(Exception e2){
					
					for(Object c:collums){
						//{"id","P","data_ex","turno","grupo.nome","subgrupo.nome","tipo_subgrupo.nome","cliente.nome_razao","end.cidade","end.bairro","prioridade","tecnico","veiculo_id.cod_veiculo","obs","concentrador","base","plano","material"});
						if(!c.equals("P") && !c.equals("veiculo_id.cod_veiculo") && !c.equals("end.cidade") && !c.equals("end.bairro")								
								&& !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));
						}	
						
					}
				}

			}

			
			container.addContainerFilter(Filters.or(filtros));
		}
		
		container.sort(new Object[]{"id"}, new boolean[]{false});	
		return container;
	}

	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
				
				if(colId.equals("cliente.nome_razao") && tb.getItem(rowId).getItemProperty(colId).getValue() == null){
					return "";
				}
								
			
				if (colId.equals("data")) {
				      Date dateValue = (Date) v;				        
				      return dateValue != null ? new SimpleDateFormat("dd/MM/yyyy").format(dateValue) : "";				        
				}
				if (colId.equals("hora_pv")) {
				      Date dateValue = (Date) v;				        
				      return dateValue != null ? new SimpleDateFormat("HH:mm:ss").format(dateValue) : "";				        
				}
				
				
				if (v instanceof Float) {					
					return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
				}
				
				if(colId.equals("situacao") && tb.getItem(rowId).getItemProperty(colId).getValue() != null){
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue().equals("A")){
						return "ABERTO";
					}
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue().equals("F")){
						return "FECHADO";
					}
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue().equals("C")){
						return "CANCELADO";
					}
					
				}
				
//				if(colId.equals("formaPagtoID.nome") && tb.getItem(rowId).getItemProperty(colId).getValue() == null){
//					return "NÃO DEFINIDO";
//				}
				
				if(colId.equals("id")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}
				
								
				return super.formatPropertyValue(rowId, colId, property);
			}
		};		
	
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
			
		tb.setVisibleColumns(new Object[] {"id","naturezaOperacao.descricao","tipoVenda","data","hora_pv",
				"cliente.doc_cpf_cnpj","cliente.nome_razao","vendedor","valor","situacao"});
				
		tb.setColumnHeader("id", "Número");
		tb.setColumnHeader("naturezaOperacao.descricao", "Natureza Operação");
		tb.setColumnHeader("tipoVenda", "Natureza Itens");
		tb.setColumnHeader("data", "Data");
		tb.setColumnHeader("hora_pv", "Hora");
		tb.setColumnHeader("cliente.doc_cpf_cnpj", "CPF");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("vendedor", "Vendedor");
		//tb.setColumnHeader("valor", "Total R$");
		tb.setColumnHeader("situacao", "Status");		
		tb.setColumnCollapsible("cliente.nome_razao",false);	
		tb.setColumnAlignment("valor", Align.RIGHT);					
		//tb.setConverter("id", null);				
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
						
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
										
					btConverter.setEnabled(false);
					btImprimir.setEnabled(false);
					btEditar.setEnabled(false);
					btFinalizar.setEnabled(false);
					btCancelar.setEnabled(false);	
					btVisualizar.setEnabled(true);
					btLogs.setEnabled(true);
					
					
					String tipo = tb.getItem(tb.getValue()).getItemProperty("tipo").getValue().toString();
					
					if(tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("A")){						
						btFinalizar.setEnabled(true);																						
					}
					
					if(tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("A") && tipo.equals("PEDIDO")){				
						btEditar.setEnabled(true);												
					}
					
					if(tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("F") && tipo.equals("ORCAMENTO")){						
						btEditar.setEnabled(true);												
					}
					
					if(tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("A") && tipo.equals("ORCAMENTO")){						
						btEditar.setEnabled(true);												
					}
					
					if(!tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("C")){
						
						btCancelar.setEnabled(true);					
					}
					
					if(tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("F")){																	
						btImprimir.setEnabled(true);
					}
					
					if(tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("A") && tipo.equals("ORCAMENTO")){																	
						btImprimir.setEnabled(true);
					}
					
					if(!tb.getItem(tb.getValue()).getItemProperty("situacao").getValue().equals("C") && !tipo.equals("PEDIDO") && !tipo.equals("DEVOLUCAO")){						
						btConverter.setEnabled(true);					
					}
					
					
				}else{

					btConverter.setEnabled(false);
					btImprimir.setEnabled(false);
					btCancelar.setEnabled(false);					
					btEditar.setEnabled(false);
					btFinalizar.setEnabled(false);
					btVisualizar.setEnabled(false);
					btLogs.setEnabled(false);
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
				
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			
			public String getStyle(Table source, Object itemId, Object propertyId) {

				 if (propertyId == null){
	                    return "row-header-default"; // Will not actually be visible
				 }else{
	               
	                Item item = source.getItem(itemId);
	                
	                if(item != null && item.getItemProperty("situacao") != null && item.getItemProperty("situacao").getValue() != null){
		                String status = item.getItemProperty("situacao").getValue().toString();
		               
		                if(status.equals("F")){
		                	return "row-header-pedido-fechado";    
		                }else if(status.equals("C")){
		                	return "row-header-pedido-cancelado";    
		                }else if(status.equals("A")){
		                	return "row-header-pedido-aberto";       
		                }	                
	                }else{
	                	 return "row-header-default";
	                }
				 }
				 return "row-header-default";
			}
		});
				
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					if(tb.getValue() != null){
						tb.select(tb.getValue());
						btEditar.click();						
					}
				}
			}
		});
				
		return tb;
	}

	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setInputPrompt("Buscar...");
		tfBusca.addListener(new FieldEvents.TextChangeListener() {			
			
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
			
		});
		return tfBusca;
	}
	
	public void addFilter(String s) {
		container.removeAllContainerFilters();
		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(cbTipo != null && cbTipo.getValue() != null){						
			container.addContainerFilter(Filters.eq("tipo", cbTipo.getValue().toString()));
		}
		
		if(cbStatus != null && cbStatus.getValue() != null){
			if(cbStatus.getValue().toString().equals("ABERTO")){
				container.addContainerFilter(Filters.eq("situacao", "A"));
			}
			if(cbStatus.getValue().toString().equals("CANCELADO")){
				container.addContainerFilter(Filters.eq("situacao", "C"));
			}
			if(cbStatus.getValue().toString().equals("FINALIZADO")){
				container.addContainerFilter(Filters.eq("situacao", "F"));
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
			}
			
		} catch (Exception e) {
			
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date date = sdf.parse(s);
				
				for(Object c:collums){
					
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
						filtros.add(Filters.eq(c.toString(), date));
					}	
					
					
				}
			}catch(Exception e2){
				
				for(Object c:collums){

					if(!c.equals("P") && !c.equals("veiculo_id.cod_veiculo") && !c.equals("end.cidade") && !c.equals("end.bairro") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
					
				}
			}

		}

		
		container.addContainerFilter(Filters.or(filtros));
		container.sort(new Object[]{"id"}, new boolean[]{false});	
		container.applyFilters();
		
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	public Button BuildbtConcultarPreco(){
		btConsultarPreco = new Button("Consultar Preço", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				ConsultarPrecoEditor consultarPrecoEditor = new ConsultarPrecoEditor(true, true);
				getUI().addWindow(consultarPrecoEditor);
				
				consultarPrecoEditor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();
					}
				});
			}
		});
		
		ShortcutListener clTb = new ShortcutListener("Search", ShortcutAction.KeyCode.F3, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btConsultarPreco.click();
			}
		};
		btConsultarPreco.addShortcutListener(clTb);
		
		btConsultarPreco.addStyleName("default");
		btConsultarPreco.setIcon(new ThemeResource("img/Search-32.png"));
		return btConsultarPreco;
	}
	
	//Refresher refresher; 
//	public Refresher buildRefresher(){		
//		
//		refresher = new Refresher();
//		refresher.setRefreshInterval(3000);
//		refresher.addListener(new Refresher.RefreshListener() {
//			
//			@Override
//			public void refresh(Refresher source) {
//				btNovo.setEnabled(true);
//				btNovo.addShortcutListener(buildShortCutNovo());
//				removeExtension(source); 
//				//source.remove();
//			}
//		});
//		
//		return refresher;
//	}
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("F2",ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btNovo.click();
				btNovo.removeShortcutListener(slNovo);
				//addExtension(buildRefresher());
			}
		};
		
		return slEditar;
	}
	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
					final BeanItem<EcfPreVendaCabecalho> item = new BeanItem<EcfPreVendaCabecalho>(new EcfPreVendaCabecalho());
					
					final PedidoEditor pedidoEditor = new PedidoEditor(item, "Novo Orçamento | Pedido", true, false, codSubModulo);
					pedidoEditor.addListerner(new PedidoEditor.PedidoListerner() {
						
						public void onClose(PedidoEvent event) {
							if(event.isConfirm()){
								
								try{									
									final EcfPreVendaCabecalho pedido = item.getBean();
									pedido.setSituacao("A");
									pedido.setVendedor(OpusERP4UI.getUsuarioLogadoUI().getUsername());
																		
									//Atualiza Cliente									
									if(item.getItemProperty("cliente").getValue() != null){
										Cliente cliente = (Cliente)item.getItemProperty("cliente").getValue();								
										ClienteDAO.saveCliente(cliente);
										
										//Atualiza Endereço
										if(pedido.getEnd() != null){
											EnderecoDAO.save(pedido.getEnd());									
										}
									}
									
									PedidoDAO.save(pedido);							
									pedidoEditor.close();				
									
									cbTipo.select(pedido.getTipo());
									
									if(!cbStatus.getValue().toString().equals("FINALIZADO")){
										container.refresh();									
									}
									
									chamaItens(pedido); 
									
								}catch(Exception e){
									e.printStackTrace();
									Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
								}
							}else{
								try{
									final EcfPreVendaCabecalho pedido = item.getBean();
									pedido.setSituacao("A");
									pedido.setVendedor(OpusERP4UI.getUsuarioLogadoUI().getUsername());
																		
									//Atualiza Cliente
									Cliente cliente = (Cliente)item.getItemProperty("cliente").getValue();								
									ClienteDAO.saveCliente(cliente);
									
									//Atualiza Endereço
									if(pedido.getEnd() != null){
										EnderecoDAO.save(pedido.getEnd());									
									}
									
									PedidoDAO.save(pedido);		
									
									cbTipo.select(pedido.getTipo());

									if(!cbStatus.getValue().toString().equals("FINALIZADO")){
										container.refresh();									
									}
									
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}
					});
					
					pedidoEditor.addCloseListener(new Window.CloseListener() {
						
						
						public void windowClose(CloseEvent e) {
							tb.focus();
							btNovo.setEnabled(true);
						}
					});
					
					getUI().addWindow(pedidoEditor);		
				}else{
					Notify.Show("Você não Possui Permissão para Abrir um  Pedido",Notify.TYPE_ERROR);
				}
			}
		});
		
		btNovo.addShortcutListener(new ShortcutListener("F2",ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btNovo.click();
				btNovo.setEnabled(false);
				//addExtension(buildRefresher());
			}
		});
		btNovo.setDisableOnClick(true); 

		return btNovo;
	}
	
	
	
	
	
	public void chamaItens(final EcfPreVendaCabecalho pedido){
		
		final ItemEditor itemEditor =new ItemEditor(pedido, "Itens", true);
		itemEditor.addListerner(new ItemEditor.ItemListerner() {
			
			@Override
			public void onClose(ItemEvent event) {
				
				if(event.isConfirm()){
					
					try{
						Float total = new Float(0);
						for (EcfPreVendaDetalhe item : event.getItens()) {																					
							total = total+item.getValorTotal();			
							
							//Atualiza Loja Integrada
							Produto p = ProdutoDAO.find(item.getProdutoId());
							//if(p.getCod_pro_loja() != null){
							//PedidoDAO.sincronizarEstoque(p.getCod_pro_loja(),p.getQtdEstoque().toString());								
							//}
						}
						
						
						if(pedido.getTipo().equals("PEDIDO") && pedido.getNaturezaOperacao().getTipo().equals("SAIDA")){
							pedido.setSituacao("A");
						}else{
							pedido.setSituacao("F");
						}
						
						pedido.setValor(total);
						pedido.setSubTotal(total);
						pedido.setTotal_acres(new Float(0));
						pedido.setTotal_desc(new Float(0));
						PedidoDAO.save(pedido);
						
						itemEditor.close();
						
						if(!cbStatus.getValue().toString().equals("FINALIZADO")){
							container.refresh();									
						}
						
						if(pedido.getTipo().equals("ORCAMENTO")){
							concluiOrcamento(pedido);
						}
						
						if(pedido.getTipo().equals("PEDIDO") && pedido.getNaturezaOperacao().getTipo().equals("SAIDA")){
							concluiPedido(pedido);
						}
						
						if(pedido.getTipo().equals("DEVOLUCAO")){
							
							
							List<Float> valores = event.getValores();

							Float valor_total = new Float(0);
							for (Float valor: valores) {		
								valor_total = valor_total + valor;								
							}
							
							EcfPreVendaCabecalho pedidoSaida = pedido.getN_nf_devolucao();
							Float desconto = pedido.getN_nf_devolucao().getTotal_desc();
							
							if(desconto != null && desconto > 0){
								List<EcfPreVendaDetalhe> itens_saida = PedidoDAO.getItensPedido(pedido.getN_nf_devolucao().getId());											
								Float novo_desconto = (desconto / itens_saida.size()) * valores.size();
								Float valor_desconto = (valor_total * novo_desconto) / 100;						
								valor_total = valor_total - valor_desconto;
							}
							
							pedido.setSubTotal(total);
							pedido.setTotal_desc(desconto); 
							pedido.setValor(valor_total); 
							PedidoDAO.save(pedido);
							
							//Haver
							HaverCab haver = HaverDAO.buscarHaver(pedido.getCliente());
							if(haver != null){
								
								HaverDetalhe haverdet = new HaverDetalhe(null, haver, "ENTRADA", valor_total, "PEDIDO", pedido.getId().toString(), "DEVOLUCAO", "",new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername(), "ATIVO"); 
								HaverDAO.saveDet(haverdet);
							}else{
								EntityManager em = ConnUtil.getEntity();
								em.getTransaction().begin();
								em.persist(new HaverCab(null, pedido.getCliente(),new Date(), "DISPONIVEL"));
								em.getTransaction().commit();
								
								haver = HaverDAO.buscarHaver(pedido.getCliente());
								HaverDetalhe haverdet = new HaverDetalhe(null, haver, "ENTRADA", valor_total, "PEDIDO", pedido.getId().toString(), "DEVOLUCAO", "",new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername(), "ATIVO"); 
								HaverDAO.saveDet(haverdet);
							}
							
							List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(pedido.getId());
							
							for (EcfPreVendaDetalhe item : itens) {
								//Gerar Entrada
								Fornecedor Fornecedor = FornecedorDAO.findbyCnpjEmp(OpusERP4UI.getEmpresa().getCnpj(),OpusERP4UI.getEmpresa().getId());
								MovimentoEntCabecalho compra = new MovimentoEntCabecalho();
								MovimentoEntDetalhe contaDetalhe = new MovimentoEntDetalhe();
								Integer CodId = Integer.parseInt(CompraDAO.getNextID());
								
								compra.setId(CodId); 
								compra.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								compra.setCodNf(0);
								compra.setFornecedor(Fornecedor);
								compra.setQtdItens(new Float(0));
								compra.setValorTotal(new Float(0));
								compra.setDataHora(new Date());
								compra.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
								compra.setSituacao("F");
								compra.setTipo("DEVOLUCAO");
								
								contaDetalhe.setMovimentoEntCabecalhoId(compra.getId());
								contaDetalhe.setProdutoId(item.getProdutoId());
								contaDetalhe.setQuantidade(item.getQuantidade());
								
								CompraDAO.save(compra);
								CompraDAO.saveItem(contaDetalhe);								
							}
							
						}
						
					}catch(Exception e){
						e.printStackTrace();						
					}
								
				}else{
					try{
						Float total = new Float(0);
						for (EcfPreVendaDetalhe item : event.getItens()) {
																					
							total = total+item.getValorTotal();														
						}
						
						pedido.setSituacao("A");
						pedido.setValor(total);
						pedido.setSubTotal(total);
						pedido.setTotal_acres(new Float(0));
						pedido.setTotal_desc(new Float(0));
						PedidoDAO.save(pedido);
						
						
						itemEditor.close();
						if(!cbStatus.getValue().toString().equals("FINALIZADO")){
							container.refresh();									
						}
						
						
					}catch(Exception e){
						e.printStackTrace();						
					}
							
				}
			}
		});
		
		itemEditor.addCloseListener(new Window.CloseListener() {
			
			@Override
			public void windowClose(CloseEvent e) {
				if(!cbStatus.getValue().toString().equals("FINALIZADO")){
					container.refresh();									
				}
				tb.focus();
				
			}
		});
		
		getUI().addWindow(itemEditor);
	}
	public void concluiOrcamento(final EcfPreVendaCabecalho pedido){
		//final EcfPreVendaCabecalho p = pedido;
		
		GenericDialog cd = new GenericDialog("Confirme para Continuar!", "Deseja Imprimir Comanda ?", true, true);
		
		cd.addListerner(new GenericDialog.DialogListerner() {
			
			@Override
			public void onClose(DialogEvent event) {
				if(event.isConfirm()){
					Imprimir(pedido);
					
					GenericDialog cd2 = new GenericDialog("Confirme para Continuar!", "Re-Imprimir ?", true, true);
					cd2.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								Imprimir(pedido);
							}
						}
					});
					
					getUI().addWindow(cd2);
				}
			}
		});
		
		getUI().addWindow(cd);
	}
	public void concluiPedido(final EcfPreVendaCabecalho pedido){
		final FormaPgtoEditor formaPgto = new FormaPgtoEditor(pedido, "Forma de Pagamento", true);
		formaPgto.addListerner(new FormaPgtoEditor.FormaPgtoListerner() {
			
			@Override
			public void onClose(FormaPgtoEvent event) {
				 
				if(event.isConfirm()){														
					
					
					//EMITE BOLETOS SE HOUVEREM																
					for(Entry<Integer, TotaisHash> entry : event.getTotaisFormaPgto().entrySet()) {
						FormasPgto formaPgto = FormaPgtoDAO.find(entry.getKey());
						TotaisHash t = entry.getValue();
						Double valorPgto = t.getValor();
						if(formaPgto.getTipo_titulo().equals("BOLETO")){																				
							try {
										if(event.getPedido() != null && event.getTotaisFormaPgto().get(formaPgto.getId()) != null
												&& event.getParcFormaPgto().get(formaPgto.getId()) != null && event.getDataPrimVenc() != null
										){																	
											ContasReceberDAO.gerarBoletosPedido(event.getPedido(),valorPgto, event.getParcFormaPgto().get(formaPgto.getId()),event.getDataPrimVenc(), event.getValorPrimeiraParc());
										}else{
											Notify.Show("Não foi possivel gerar boletos", Notify.TYPE_ERROR);
										}
								
							} catch (Exception e) {
								e.printStackTrace();
							}																			
						}
					}//ghgfhgf
					
					
					final EcfPreVendaCabecalho p = event.getPedido();
					event.getPedido().setSituacao("F");
					PedidoDAO.save(event.getPedido());
					TotaisPedidoDAO.save(event.getTotaisFormaPgto(), event.getPedido(), event.getHaver(), event.getHaverValor());
					
					GenericDialog cd = new GenericDialog("Confirme para Continuar!", "Deseja Imprimir Comanda ?", true, true);
					
					cd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								Imprimir(p);
								
								GenericDialog cd2 = new GenericDialog("Confirme para Continuar!", "Re-Imprimir ?", true, true);
								cd2.addListerner(new GenericDialog.DialogListerner() {
									
									@Override
									public void onClose(DialogEvent event) {
										if(event.isConfirm()){
											Imprimir(p);
										}
									}
								});
								
								getUI().addWindow(cd2);
							}
						}
					});
					
					getUI().addWindow(cd);
					
					if(pedido.getTipo().equals("PEDIDO") && event.getPedido().getTipoVenda().equals("PRODUTO") && 
							event.getPedido().getNaturezaOperacao().getEmitir_ecf().equals("SIM") && event.getPedido().getNaturezaOperacao().getTipo().equals("SAIDA")){
						EnviarPreVenda(pedido);					
					}
					
					if(!cbStatus.getValue().toString().equals("FINALIZADO")){
						container.refresh();									
					}
					
					formaPgto.close();
					Notify.Show("Pedido Concluído com Sucesso!", Notify.TYPE_SUCCESS);
				}
			}
				
		});

		
		formaPgto.addCloseListener(new Window.CloseListener() {
			
			@Override
			public void windowClose(CloseEvent e) {
				tb.focus();
			}
		});
		
		getUI().addWindow(formaPgto);
	}

	public Button BuildbtConverterEmPedido(){
		btConverter = new Button("Converter em Pedido", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "ConverterPedido"))				
				{

					if(tb.getItem(tb.getValue()).getItemProperty("vendedor").getValue().toString().equals(OpusERP4UI.getUsuarioLogadoUI().getUsername())){				
					
						final EcfPreVendaCabecalho pedido = PedidoDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
						chamaItens(pedido); 
					
					}else{
						Notify.Show("Você não Pode Editar um Pedido criado Por Outro Usuário",Notify.TYPE_ERROR);
					}
					
				}else{
					Notify.Show("Você não Possui Permissão para Converter em Pedido",Notify.TYPE_ERROR);
				}
			}
		});
		btConverter.setEnabled(false);
					
		return btConverter;
	}
	
	public Button BuildbtEditar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Editar"))				
				{			
					
					boolean allow_edit;
					if(tb.getItem(tb.getValue()).getItemProperty("vendedor").getValue().toString().equals(OpusERP4UI.getUsuarioLogadoUI().getUsername())){
						allow_edit = true;
					}else{
						allow_edit = false;
					}
					
					final boolean allow_change_pro = gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Propriedade");
					if(allow_change_pro){
						allow_edit = allow_change_pro;
					}
					
					if(allow_edit){
											
						final PedidoEditor pedidoEditor  = new PedidoEditor(tb.getItem(tb.getValue()), "Editar", true,false, allow_change_pro);
						
						pedidoEditor.addListerner(new PedidoEditor.PedidoListerner() {
							
							@Override
							public void onClose(PedidoEvent event) {
								if(event.isConfirm()){
									try{		
										EntityItem<EcfPreVendaCabecalho> entityItem = (EntityItem<EcfPreVendaCabecalho>) event.getItem(); 
										final EcfPreVendaCabecalho pedido = entityItem.getEntity();
										pedido.setSituacao("A");
										
										if(!allow_change_pro){
											pedido.setVendedor(OpusERP4UI.getUsuarioLogadoUI().getUsername());
										}
																			
										//Atualiza Cliente
										Cliente cliente = (Cliente)event.getItem().getItemProperty("cliente").getValue();								
										ClienteDAO.saveCliente(cliente);
										
										//Atualiza Endereço
										if(pedido.getEnd() != null){
											EnderecoDAO.save(pedido.getEnd());									
										}
										
										PedidoDAO.save(pedido);							
										
										pedidoEditor.close();									
										if(!cbStatus.getValue().toString().equals("FINALIZADO")){
											container.refresh();									
										}							
										
										chamaItens(pedido); 										
									}catch(Exception e){
										e.printStackTrace();
										Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
									}
								}else{
									try{
										EntityItem<EcfPreVendaCabecalho> entityItem = (EntityItem<EcfPreVendaCabecalho>) event.getItem(); 
										final EcfPreVendaCabecalho pedido = entityItem.getEntity();
										//pedido.setSituacao("A");
										if(!allow_change_pro){
										  pedido.setVendedor(OpusERP4UI.getUsuarioLogadoUI().getUsername());
										}
																			
										//Atualiza Cliente
										Cliente cliente = (Cliente)event.getItem().getItemProperty("cliente").getValue();								
										ClienteDAO.saveCliente(cliente);
										
										//Atualiza Endereço
										if(pedido.getEnd() != null){
											EnderecoDAO.save(pedido.getEnd());									
										}
										
										PedidoDAO.save(pedido);							
										
										pedidoEditor.close();									
										if(!cbStatus.getValue().toString().equals("FINALIZADO")){
											container.refresh();									
										}
									}catch(Exception e){
										e.printStackTrace();	
										Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
									}
								}
							}
						});
						
						getUI().addWindow(pedidoEditor);				
					}else{
						Notify.Show("Você não Pode Editar um Pedido criado Por Outro Usuário",Notify.TYPE_ERROR);
					}
					
				}else{
					Notify.Show("Você não Possui Permissão para Editar Pedido",Notify.TYPE_ERROR);
				}
			}
		});
		
		btEditar.setEnabled(false);		
		return btEditar;
		
	}
	
	
	
	
	
	
		
	public Button BuildbtFinalizar(){
		btFinalizar = new Button("Finalizar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Finalizar"))				
				{
					
					if(tb.getItem(tb.getValue()).getItemProperty("vendedor").getValue().toString().equals(OpusERP4UI.getUsuarioLogadoUI().getUsername())){	
						final EcfPreVendaCabecalho pedido = PedidoDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
																
						chamaItens(pedido); 		
					}else{
						Notify.Show("Você não Pode Finalizar um Pedido criado Por Outro Usuário",Notify.TYPE_ERROR);
					}
				}else{				
					Notify.Show("Você não Possui Permissão para Finalizar",Notify.TYPE_ERROR);				
				}
				
			}
		});
		
		btFinalizar.setEnabled(false);
		return btFinalizar;
	}
	
	public Button BuildbtVisualizar(){
		btVisualizar = new Button("Visualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Imprimir"))				
				{
					PedidoEditor pedidoEditor = new PedidoEditor(tb.getItem(tb.getValue()), "Visualizar", true, true, codSubModulo);
					getUI().addWindow(pedidoEditor);
				}
			}
		});
		
		btVisualizar.setEnabled(false);
		return btVisualizar;
	}
	
	public Button BuildbtImprimirDav(){
		btImprimir = new Button("Imprimir", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
					
					 if (winSubMenuNovo != null && winSubMenuNovo.getUI() != null)
						 winSubMenuNovo.close();
		             else {
		            	 
//		            	 if(winSubMenuHistorico != null && winSubMenuHistorico.getUI() != null){
//		            		 winSubMenuHistorico.close();
//		            	 }
//		            	 
//		            	 if(winSubMenuMudanca != null && winSubMenuMudanca.getUI() != null){
//		            		 winSubMenuMudanca.close();
//		            	 }
//		            	             	 
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
		
		btImprimir.setEnabled(false);
		return btImprimir;
	}
	
	Window winSubMenuNovo;
	private void buildSubMenu(ClickEvent event) {
		winSubMenuNovo = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuNovo.setContent(l);
        winSubMenuNovo.setWidth("300px");
        winSubMenuNovo.addStyleName("notifications");
        winSubMenuNovo.setClosable(false);
        winSubMenuNovo.setResizable(false);
        winSubMenuNovo.setDraggable(false);
        winSubMenuNovo.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuNovo.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuNovo.setCloseShortcut(KeyCode.ESCAPE, null);
        
        Button bt1 = new Button("PDF", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
			
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Pdf"))				
				{
					try{
						window = new Window();
						window.setCaption(cbTipo.getValue().toString());
				        window.setWidth("800px");
				        window.setHeight("600px");
				        window.setResizable(true);
				        window.center();
				        window.setModal(true);
				        window.setStyleName("disable_scroolbar");		
				        window.setCloseShortcut(KeyCode.ESCAPE, null);

				        Integer cod = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
				        
				        String nomeCliente = tb.getItem(tb.getValue()).getItemProperty("cliente").getValue() != null ? ((Cliente) tb.getItem(tb.getValue()).getItemProperty("cliente").getValue()).getNome_razao() : tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString();				        
				        StreamResource resource = new StreamResource(new ImprimirPedido(cod,cbTipo.getValue().toString()),cbTipo.getValue().toString()+"_"+nomeCliente+".pdf");
				        resource.getStream();			        
				        resource.setMIMEType("application/pdf");
				        resource.setCacheTime(0);
				        
				        Embedded e = new Embedded();
				        e.setSizeFull();
				        e.setType(Embedded.TYPE_BROWSER); 
				        e.setSource(resource);			     
				        window.setContent(e);
				        getUI().addWindow(window);
				        window.focus();
				        
				        LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), cbTipo.getValue().toString()));
				        
					}catch(Exception e){
						System.out.println("Deu ERRADO!");
						e.printStackTrace();
						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Imprimir um "+cbTipo.getValue().toString()));
					}	

					
				}else{					
					Notify.Show("Você não Possui Permissão para Imprimir um "+cbTipo.getValue().toString(), Notify.TYPE_ERROR);
				}
			}		
		});
        bt1.setPrimaryStyleName("btSubMenu");
               
        Button bt2 = new Button("Comanda", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Imprimir"))				
				{
				
					if(PedidoDAO.getItensPedido((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue()).size() > 0){
						Imprimir(PedidoDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue()));				
					}else{
						Notify.Show("Não é Possivel Imprimir Cupom sem Produtos os Serviços", Notify.TYPE_ERROR);
					}
				
				}else{					
					Notify.Show("Você não Possui Permissão para Imprimir Cupom", Notify.TYPE_ERROR);
				}
			}
		});
        bt2.setPrimaryStyleName("btSubMenu");
        
        l.addComponent(bt1);
        l.addComponent(bt2);     
    }
	
	public Button BuildbtLog(){
		btLogs = new Button("Log", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				boolean check_permissao = gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar Logs");
				
				if(check_permissao){
					
					EntityItem<EcfPreVendaCabecalho> entityPedido = (EntityItem<EcfPreVendaCabecalho>)tb.getItem(tb.getValue());
					EcfPreVendaCabecalho pedido= entityPedido.getEntity();
					
					HistoricoLogsProdutoEditor logsEditor = new HistoricoLogsProdutoEditor(true, true, pedido);
					getUI().addWindow(logsEditor);
					
				}else{
					Notify.Show("Você não tem permissão para Visualizar os Logs!", Notify.TYPE_ERROR);
				}
			}
		});
				
		btLogs.setEnabled(false); 
		return btLogs;
	}
	
	public Button BuildbtCancelar(){
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				try{
				
						final EcfPreVendaCabecalho pedido = PedidoDAO.find2((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
						boolean boletos_vinculados = pedido != null ? ContasReceberDAO.allowDeletePv(pedido):true;
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						Date d = sdf.parse(sdf.format(new Date()));
						
						String permissao = pedido.getData().equals(d) ? "Cancelar" : "Cancelar Retroativo";
						boolean permissao_allow = gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), permissao);
						
						boolean allow_devolucao = true;
						if(pedido.getTipoVenda() == null || pedido.getTipoVenda().equals("PRODUTO") && pedido.getTipo().equals("PEDIDO") && pedido.getNaturezaOperacao().getTipo().equals("SAIDA")){
							boolean foi_usada = PedidoDAO.foi_usada_em_devolucao(pedido);
							allow_devolucao = !foi_usada;
						}
		
						
						if(permissao_allow && boletos_vinculados && allow_devolucao)				
						{
		
							GenericDialog gd = new GenericDialog("Confirme para Continuar", "Deseja realmente Cancelar este Pedido ?", true, true);
							gd.addListerner(new GenericDialog.DialogListerner() {
								
								@Override
								public void onClose(DialogEvent event) {
									
									if(event.isConfirm()){
										try {											
												boolean check = PedidoDAO.CancelarPedido(pedido);
												
												if(check){
													refresh();
													Notify.Show("Pedido Cancelado com Sucesso",Notify.TYPE_SUCCESS);
												}
										} catch (Exception e) {
											e.printStackTrace();
										}
										
										btCancelar.setEnabled(true); 
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
							
							if(!permissao_allow){
								Notify.Show("Você não Possui Permissão para Cancelar Pedido",Notify.TYPE_ERROR);
							}
							if(!boletos_vinculados){
								Notify.Show("Este pedido possui boletos vinculados, não pode ser cancelado!",Notify.TYPE_NOTICE);
							}
							if(!allow_devolucao){
								Notify.Show("Não é Possivel Cancelar Pedido utilizado em DEVOLUÇÃO",Notify.TYPE_ERROR);
							}
							
						}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		btCancelar.setDisableOnClick(true);
		btCancelar.setEnabled(false);
		return btCancelar;
	}

	
	private void EnviarPreVenda(EcfPreVendaCabecalho pedido){
		
		try{
			
			File f = new File("/DJMONITOR/"+pedido.getId().toString()+".djp");
			BufferedWriter br = new BufferedWriter(new FileWriter(f));  
			 					
			if(f.canWrite()){
			
				StringBuilder sb = new StringBuilder();
				String quebra = "\n";
					
			    //-INICIO---REGISTRO PRE		
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
				List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(pedido.getId());
				
				String codCliente = pedido.getCliente() != null ? pedido.getCliente().getId().toString() : "0";
				String nomeCliente = pedido.getCliente() != null ? pedido.getCliente().getNome_razao() : "";
				String cpfCliente = pedido.getCliente() != null ? pedido.getCliente().getDoc_cpf_cnpj() : "";
				
				 double percentual = pedido.getTotal_desc() / 100.0; 
				 double valor_final_desconto = percentual * pedido.getSubTotal();
				
				String DESCONTO = String.valueOf(valor_final_desconto);
				
				sb.append("PRE|"+pedido.getId().toString()+"|"+sdf.format(new Date())+"|"+codCliente+"|"+nomeCliente+"|"+cpfCliente+"|1|"+pedido.getSubTotal().toString()+"|"+
						DESCONTO+"|"+pedido.getTotal_acres().toString()+"|"+String.valueOf(itens.size())+quebra);
				System.out.println("PRE|"+pedido.getId().toString()+"|"+sdf.format(new Date())+"|"+codCliente+"|"+nomeCliente+"|"+cpfCliente+"|1|"+pedido.getSubTotal().toString()+"|"+
						DESCONTO+"|"+pedido.getTotal_acres().toString()+"|"+String.valueOf(itens.size()));
			    //-FIM---REGISTRO PRE
			
			
			    //-INICIO---REGISTRO PIT--LOOP
				Integer SEQUENCIA = 0;
				
				for (EcfPreVendaDetalhe item : itens) {
					
					Produto p = ProdutoDAO.find(item.getProdutoId());
				
					//PARAMETROS PREVENDA DETALHE
					String ORDEM = item.getOrdem().toString();
					String COD_EXTERNO_PRODUTO = item.getProdutoId().toString();
					String QTD = Real.formatDbToString(item.getQuantidade().toString());
					String PRECO_UNITARIO = p.getValorVenda().toString();
					String VALOR_TOTAL = item.getValorTotal().toString();		
					
					
					String COD_BARRAS = p.getgTin();		
					String DESCRICAO = p.getNome();
					String UNIDADE = p.getUnidadeProduto().getNome();
					
					String ST = p.getEcf_cup_filcal().getEcf_emissor_cod();
					String ICMS = Real.formatDbToString(p.getTaxaIcms().toString());
					String QTD_ESTOQUE = p.getQtdEstoque().toString();
					String IPPT = p.getIppt();
					String PRODUCAO = "";
					
					if(IPPT.equals("T"))
					{
						PRODUCAO = "N";
					}else{
						PRODUCAO = "S";
					}
					SEQUENCIA++;
					
					//ESCREVE REGISTRO PIT
					sb.append("PIT|"+SEQUENCIA+"|"+COD_EXTERNO_PRODUTO+"|"+item.getQuantidade()+"|"+item.getValorUnitario()+"|0.00|0.00|"+VALOR_TOTAL+"|"+COD_BARRAS+"|"+DESCRICAO+"||"+UNIDADE+"|"+ST+"|"+p.getTaxaIcms()+"|N|N|N|"+PRODUCAO+"|"+p.getQtdEstoque()+"||"+OpusERP4UI.getUsuarioLogadoUI().getId().toString()+"|"+OpusERP4UI.getUsuarioLogadoUI().getUsername()+"|"+COD_BARRAS+"||"+COD_BARRAS+"|||||||||"+p.getNcm()+"||-1|"+p.getCest()+"|"+quebra);
					
					System.out.println("PIT|"+SEQUENCIA+"|"+COD_EXTERNO_PRODUTO+"|"+item.getQuantidade()+"|"+item.getValorUnitario()+"|0.00|0.00|"+VALOR_TOTAL+"|"+COD_BARRAS+"|"+DESCRICAO+"||"+UNIDADE+"|"+ST+"|"+p.getTaxaIcms()+"|N|N|N|"+PRODUCAO+"|"+p.getQtdEstoque()+"||"+OpusERP4UI.getUsuarioLogadoUI().getId().toString()+"|"+OpusERP4UI.getUsuarioLogadoUI().getUsername()+"|"+COD_BARRAS+"||"+COD_BARRAS+"|||||||||"+p.getNcm()+"||-1|"+p.getCest()+"|"+quebra);
					
				}
			
				br.write(sb.toString());  
				br.close();  
				
				if(pedido.getEmpresa_id().equals(1)){
					ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@18", "/DJMONITOR/"+pedido.getId().toString()+".djp", "/dados/ECF/PREVENDA/"+pedido.getId().toString()+".djp");
				}else if(pedido.getEmpresa_id().equals(4)){
					ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@18", "/DJMONITOR/"+pedido.getId().toString()+".djp", "/dados/COMERCIALPINTO/ECF/PREVENDA/"+pedido.getId().toString()+".djp");
				}else if(pedido.getEmpresa_id().equals(5)){
					ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@18", "/DJMONITOR/"+pedido.getId().toString()+".djp", "/dados/ESPACODIGITAL/ECF/PREVENDA/"+pedido.getId().toString()+".djp");
				}else{
					ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@18", "/DJMONITOR/"+pedido.getId().toString()+".djp", "/dados/ECF/DIGITALGRAFICA/"+pedido.getId().toString()+".djp");
				}
				f.delete();
			}else{
				Notify.Show("Não é Possivel Gravar o Arquivo", Notify.TYPE_ERROR);
			}
		}catch(Exception e){
			e.printStackTrace();
			Notify.Show("ERRO: "+e.getMessage(), Notify.TYPE_ERROR);
			Notify.Show("ERRO: "+e.getCause(), Notify.TYPE_ERROR);
		}
				   
	}
	
	private void Imprimir(EcfPreVendaCabecalho pedido){
		try{						
					
			
			if(OpusERP4UI.getEmpresa().getId().equals(4)){
				String urlPrint = "";
				boolean allow = true;
				
				if(!OpusERP4UI.getEmpresa().getId().equals(3)){
					urlPrint = "http://192.168.21.145:631/printers/BEMA_PRODUCAO";
					allow = true;
				}else{
					urlPrint = "http://192.168.21.145:631/printers/BEMA_GRAFICA";
					allow = false;
				}
				
				InputStream stream = new ByteArrayInputStream(PedidoDAO.getLayoutPedido(pedido, allow).getBytes());  
				
				CupsPrinter printer = new CupsPrinter(new URL(urlPrint), "BEMA_PRODUCAO", true);		        	    
	    	   
				
				HashMap<String, String> attributes = new HashMap<String, String>();	    	   
	    	    	        	    		        	    		        	    
	    	    PrintJob printJob = new PrintJob.Builder(stream)
	    	    							.jobName("OpusERP4")
	    	    							.userName(OpusERP4UI.getUsuarioLogadoUI().getUsername())
	    	    							.copies(1)  	        	    							
	    	    							.build();
	    	    
	    	    PrintRequestResult printRequestResult = printer.print(printJob);		
	    	    
	    	    if(printRequestResult.isSuccessfulResult()){
	    	    	Notify.Show("Pedido Esta Sendo Impresso", Notify.TYPE_SUCCESS);
	    	    }else{
	    	    	Notify.Show("Houve um Problema ao Tentar Imprimir, Espere um Momento e Tente Novamente", Notify.TYPE_SUCCESS);
	    	    }
	    	    
	    	    LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Imprimiu um Pedido"));
			}
			
			if(OpusERP4UI.getEmpresa().getId().equals(1) || OpusERP4UI.getEmpresa().getId().equals(3) || OpusERP4UI.getEmpresa().getId().equals(5) || 
					OpusERP4UI.getEmpresa().getId().equals(6)){
				
				File f = new File("/DJMONITOR/PEDIDO"+pedido.getId().toString()+".txt");
				BufferedWriter br = new BufferedWriter(new FileWriter(f));  
				 					
				if(f.canWrite()){
					
					StringBuilder s = new StringBuilder();
					String quebra = System.getProperty("line.separator");
				
					if(OpusERP4UI.getEmpresa().getId().equals(1) || OpusERP4UI.getEmpresa().getId().equals(3)){
							s.append("                          d i g i t a l");
							s.append(quebra);
							s.append("      Rua Adjar Maciel, 35 Centro Belo Jardim/PE CEP: 55.150-040");
							s.append(quebra);					
							s.append("            CNPJ: 07.578.965/0001-05 IE: 18.3.050.0329");
							s.append(quebra);
					}else if(OpusERP4UI.getEmpresa().getId().equals(5)){
							s.append("                   E S P A C O D I G I T A L");
							s.append(quebra);
							s.append("  Rua Padre Cicero, 07 Sao Pedro Belo Jardim/PE CEP: 55.150-340");
							s.append(quebra);					
							s.append("             CNPJ: 33.056.081/0001-09 IE: 081948581");
							s.append(quebra);
					}else if(OpusERP4UI.getEmpresa().getId().equals(6)){
						s.append("                   E S P A C O C A F E");
						s.append(quebra);
						s.append("  Rua Padre Cicero, 07 Sao Pedro Belo Jardim/PE CEP: 55.150-340");
						s.append(quebra);					
						s.append("             CNPJ: 33.056.081/0001-09 IE: 081948581");
						s.append(quebra);
				}
				
					
					if(pedido.getTipo().equals("PEDIDO")){				
						s.append("                            PRE-VENDA");
						s.append(quebra);
					}else{					
						s.append("                            ORCAMENTO");
						s.append(quebra);
					}
				
					s.append(quebra);
						
					s.append("----------------------------------------------------------------"+quebra);					
					s.append("                     NAO E DOCUMENTO FISCAL");
					s.append(quebra);
					s.append("                    NAO E VALIDO COMO RECIBO");
					s.append(quebra);
					s.append("             NAO E VALIDO COMO GARANTIA DE MERCADORIA");
					s.append(quebra);					
					s.append("----------------------------------------------------------------"+quebra);
								
					SimpleDateFormat formHora= new SimpleDateFormat("HH:mm:ss");
					String hora = formHora.format(pedido.getHora_pv());
				
					SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
					String hoje = form.format(pedido.getData());
					String cliente = pedido.getCliente() != null ? pedido.getCliente().getNome_razao() : "NENHUM CLIENTE VINCULADO";
					String vendedor = pedido.getVendedor() != null ? pedido.getVendedor() : "NENHUM VENDEDOR";
					String comprador = pedido.getComprador() != null ? pedido.getComprador() : "NENHUM COMPRADOR INFORMADO";
								
					s.append("CODIGO: "+pedido.getId().toString());
					s.append(quebra);
					s.append("NATUREZA OPERACAO: "+pedido.getNaturezaOperacao().getDescricao());
					s.append(quebra);
					s.append("DATA DE EMISSAO: "+hoje+" AS "+hora);
					s.append(quebra);
					s.append("EMITIDO POR: "+vendedor);
					s.append(quebra);				
					s.append("CLIENTE: "+cliente);
					s.append(quebra);
					s.append("COMPRADOR: "+comprador);
					s.append(quebra);
				
					if(pedido.getCliente() != null && pedido.getCliente().getEndereco_principal() != null){
						s.append("END.: "+checkText(pedido.getCliente().getEndereco_principal().getEndereco())+","+checkText(pedido.getCliente().getEndereco_principal().getNumero())+" - "+checkText(pedido.getCliente().getEndereco_principal().getBairro())+" "+checkText(pedido.getCliente().getEndereco_principal().getCidade())+"-"+checkText(pedido.getCliente().getEndereco_principal().getUf()));
						s.append(quebra);
					}else{
						s.append("SEM CLIENTE OU ENDEREÇO VINCULADO.");
						s.append(quebra);
					}
				
					if(pedido.getCliente() != null){
						s.append("FONE: ");
							if(pedido.getCliente().getTelefone1() != null && !pedido.getCliente().getTelefone1().equals("")){
								s.append("("+pedido.getCliente().getDdd_fone1()+") "+pedido.getCliente().getTelefone1());
							}
							
							if(pedido.getCliente().getTelefone2() != null && !pedido.getCliente().getTelefone2().equals("")){
								s.append("/ ("+pedido.getCliente().getDdd_fone2()+") "+pedido.getCliente().getTelefone2());
							}
							
							if(pedido.getCliente().getCelular1() != null && !pedido.getCliente().getCelular1().equals("")){
								s.append("/ ("+pedido.getCliente().getDdd_cel1()+") "+pedido.getCliente().getCelular1());
							}
							
							if(pedido.getCliente().getCelular2() != null && !pedido.getCliente().getCelular2().equals("")){
								s.append("/ ("+pedido.getCliente().getDdd_cel2()+") "+pedido.getCliente().getCelular2());
							}
						s.append(quebra);
					}
				
				
				
					s.append("ENTREGA: "+checkText(pedido.getEntregar()));				
					s.append(quebra);			
					s.append("----------------------------------------------------------------");
					s.append(quebra);			
					
	
					s.append("COD     DESCRICAO");
					s.append(quebra);	
					s.append("QTD     VALOR R$        TOTAL R$          GARANTIA");
					s.append(quebra);	
				

					s.append("----------------------------------------------------------------");
					s.append(quebra);	
				
					List<EcfPreVendaDetalhe> itens = ItensPedidoDAO.getItens(pedido);
					double totalItens = 0;
					for (EcfPreVendaDetalhe item : itens) {
						if(pedido.getTipoVenda().equals("PRODUTO")){							
							
							Produto p = ProdutoDAO.find(item.getProdutoId());
							
							s.append(p.getId().toString()+"    "+p.getNome());
							s.append(quebra);			
				     		s.append(Real.formatDbToString(item.getQuantidade().toString())+"     "+Real.formatDbToString(item.getValorUnitario().toString())+"            "+Real.formatDbToString(item.getValorTotal().toString())+"             "+p.getGarantia());
				     		s.append(quebra);			
				     		totalItens = totalItens+item.getValorTotal();
									
									
						}else{
										
							Servico p = ServicoDAO.find(item.getProdutoId());
							
							s.append(p.getId().toString()+"    "+p.getNome());
							s.append(quebra);			
				     		s.append(Real.formatDbToString(item.getQuantidade().toString())+"     "+Real.formatDbToString(item.getValorUnitario().toString())+"            "+Real.formatDbToString(item.getValorTotal().toString())+"      "+"         NENHUMA");
				     		s.append(quebra);			
				     		
						}
					}
					s.append("----------------------------------------------------------------");
					s.append(quebra);	
					s.append(String.format("%29s",Real.formatDbToString(String.valueOf(totalItens))));
					s.append(quebra);	
					
					
					//COLOCAR OS SERIAIS
					s.append(quebra);
					for (EcfPreVendaDetalhe item : itens) {
						List<SeriaisPedido> serial = SeriaisPedidoDAO.getSeriais(item);
						if(serial.size()!= 0){
							s.append(item.getProdutoId()+" SN: ");						
						}
						
						Integer i=0;
						for (SeriaisPedido seriaisPedido : serial) {
							if(i.equals(0)){						
								s.append(seriaisPedido.getSerial().getSerial());
							}else{
								s.append(","+seriaisPedido.getSerial().getSerial());
							}
							i++;
						}
						s.append(quebra);	
					}
					
					
					if(pedido.getTipo().equals("PEDIDO") || pedido.getTipo().equals("DEVOLUCAO")){
						s.append(quebra);
						
						s.append("ACRESCIMO (R$): "+Real.formatDbToString(pedido.getTotal_acres().toString()));
						s.append(quebra);	
						s.append("SUBTOTAL (R$): "+Real.formatDbToString(String.valueOf(pedido.getSubTotal()+pedido.getTotal_acres())));
						s.append(quebra);					
						s.append("DESCONTO (%): "+Real.formatDbToString(pedido.getTotal_desc().toString()));
						s.append(quebra);							
						s.append("TOTAL (R$): "+Real.formatDbToString(pedido.getValor().toString()));
						s.append(quebra);							
						
						s.append(quebra);
						List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedido);
						for (TotaisPedido totaisPedido : totais) {
							
							if(totaisPedido.getForma_pgto() != null){
								if(totaisPedido.getForma_pgto().getNome().equals("BOLETO") || totaisPedido.getForma_pgto().getNome().equals("CARTAO CREDITO")){
									s.append("FORMA DE PGTO: "+totaisPedido.getForma_pgto().getNome()+ " : "+Real.formatDbToString(String.valueOf(totaisPedido.getValor()))+" / "+String.valueOf(totaisPedido.getParcelas()));
									s.append(quebra);	
								}else{
									
									if(totaisPedido.getForma_pgto() != null){
										s.append("FORMA DE PGTO: "+totaisPedido.getForma_pgto().getNome()+ " : "+Real.formatDbToString(String.valueOf(totaisPedido.getValor())));
										s.append(quebra);	
									}
									
								}
							}else{
								if(totaisPedido.getHaver() != null){
									s.append("FORMA DE PGTO: HAVER: "+Real.formatDbToString(String.valueOf(totaisPedido.getValor())));
									s.append(quebra);	
								}
							}
						}
						
						s.append(quebra);
						s.append("OBS.: "+pedido.getObs());
						s.append(quebra);	
					}
				
					s.append(quebra);
					s.append(quebra);
					s.append(quebra);
					s.append(quebra);
					
					s.append("      _____________________________________________");		
					s.append(quebra);
					s.append(quebra);
					
					br.write(s.toString());  
					br.close();							
					//f.delete();
												
					if(OpusERP4UI.getEmpresa().getId().equals(6)){
						ScpUtil.transferir("root", OpusERP4UI.getEnderecoSamba(), OpusERP4UI.getSenhaSamba(), "/DJMONITOR/PEDIDO"+pedido.getId().toString()+".txt", "/dados/PRINT/ESPACOCAFE/PEDIDO"+pedido.getId().toString()+".txt");						
					}else{
						ScpUtil.transferir("root", OpusERP4UI.getEnderecoSamba(), OpusERP4UI.getSenhaSamba(), "/DJMONITOR/PEDIDO"+pedido.getId().toString()+".txt", "/dados/PRINT/DIGITAL/PEDIDO"+pedido.getId().toString()+".txt");
					}
						
//					}else if(OpusERP4UI.getEmpresa().getId().equals(3)){
//						//ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@18", "/DJMONITOR/PEDIDO"+pedido.getId().toString()+".txt", "/dados/PRINT/DIGITALGRAFICA/PEDIDO"+pedido.getId().toString()+".txt");
//						ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@18", "/DJMONITOR/PEDIDO"+pedido.getId().toString()+".txt", "/dados/PRINT/DIGITAL/PEDIDO"+pedido.getId().toString()+".txt");
//					}
				
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();						
		}
		
		
		
	}
		

	private static String checkText(String s){
		if(s != null){
			return s;
		}
		
		return "";
	}

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}

