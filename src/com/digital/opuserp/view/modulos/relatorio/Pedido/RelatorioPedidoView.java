package com.digital.opuserp.view.modulos.relatorio.Pedido;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.RelatorioDAO;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.RelatorioPre;
import com.digital.opuserp.domain.RelatorioPreColunas;
import com.digital.opuserp.domain.RelatorioPreFiltro;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.exporterpdf.ExcelExporter;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.digital.opuserp.view.modulos.relatorio.Pedido.NovoRelatorioPedido.AddFiltroPedidoEvent;
import com.digital.opuserp.view.modulos.relatorio.Pedido.NovoRelatorioPedido.RelatorioPedidoEvent;
import com.digital.opuserp.view.modulos.relatorio.Pedido.SalvarRelatorioPedidoEditor.SalvarRelatorioPedidoEvent;
import com.digital.opuserp.view.modulos.relatorio.Pesquisa.ExportarRelatorioPesquisa;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Compare.LessOrEqual;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class RelatorioPedidoView extends VerticalLayout{
	
	private JPAContainer<EcfPreVendaCabecalho> container;	
	private JPAContainer<RelatorioPre> relatoriosContainer;	
	private Table tb;
	private TextField tfNomeRelatorio;	
	private Button btNovo;
	private Button btEditar;
	private Button btSalvar;
	private Button btCarregar;
	private Button btImprimir;
	private Button btGerarChart;
	private HorizontalLayout hlFloat;	
	private Label lbRegistros;	
	private ShortcutListener slNovo;
	private ShortcutListener slImprimir;	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	private List<SearchParameters> listaParametros = new ArrayList<SearchParameters>();
	private String tipo;
	private String ordenacao;
	private String orientacao;
	private String resumo;
	private String nomeRelatorio;
	private Table tbRelatorios;
	
	Window winRelatorios;
	
	private RelatorioPre relatorioPre;
	private List<RelatorioPreFiltro> filtrosRelatorio;
	private List<RelatorioPreColunas> colunasVisiveis;
	
	public RelatorioPedidoView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtNovo());
			hlButons.addComponent(BuildbtEditar());
			hlButons.addComponent(BuildbtSalvar());
			hlButons.addComponent(BuildbtCarregar());
			hlButons.addComponent(BuildbtImprimir());
			hlButons.addComponent(BuildbtGerarChart());
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildTfbusca());
					setExpandRatio(tfNomeRelatorio, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");				
			hlFloat.addComponent(lbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Imprimir");
			lbLegend.setWidth("300px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);

			addComponent(hlFloat);			
			setExpandRatio(tb, 1);
			
		}
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric()); 	
		setExpandRatio(tb, 1);		
		btSalvar.setEnabled(false);
//		btCarregar.setEnabled(false);
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof RelatorioPedidoView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btImprimir.addShortcutListener(buildShortCutImprimir());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btImprimir != null || slImprimir != null){
				btImprimir.removeShortcutListener(slImprimir);			
			}
		}
	}
	
	public Label lbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btNovo.click();
			}
		};
		return slNovo;
	}
	
	public ShortcutListener buildShortCutImprimir(){
		slImprimir = new ShortcutListener("Imprimir",ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btImprimir.click();
			}
		};
		return slImprimir;
	}

	public JPAContainer<EcfPreVendaCabecalho> buildContainer(){
		container = JPAContainerFactory.makeBatchable(EcfPreVendaCabecalho.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));		
		container.addContainerFilter(Filters.not(new Equal("tipo","CORRECAO")));
		
		
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("naturezaOperacao.descricao");
		
		container.addNestedContainerProperty("cliente.id");
		container.addNestedContainerProperty("cliente.email");
		container.addNestedContainerProperty("cliente.telefone1");
		container.addNestedContainerProperty("cliente.telefone2");
		container.addNestedContainerProperty("cliente.celular1");
		container.addNestedContainerProperty("cliente.celular2");

		container.addNestedContainerProperty("cliente.transportadora_padrao");
		container.addNestedContainerProperty("cliente.endereco_principal.cep");
		container.addNestedContainerProperty("cliente.endereco_principal.endereco");
		container.addNestedContainerProperty("cliente.endereco_principal.numero");
		container.addNestedContainerProperty("cliente.endereco_principal.cidade");
		container.addNestedContainerProperty("cliente.endereco_principal.bairro");
		container.addNestedContainerProperty("cliente.endereco_principal.uf");
		container.addNestedContainerProperty("cliente.endereco_principal.pais");
		container.addNestedContainerProperty("cliente.endereco_principal.complemento");
		container.addNestedContainerProperty("cliente.endereco_principal.referencia");
			
		return container;
	}

	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(tb.getType(colId).equals(Date.class)){				
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat shf = new SimpleDateFormat("HH:mm:ss");
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
						if(colId.equals("hora_pv")){
							return shf.format((Date)tb.getItem(rowId).getItemProperty(colId).getValue());				
						}else{
							return sdf.format((Date)tb.getItem(rowId).getItemProperty(colId).getValue());				
						}					
					}else{
						return super.formatPropertyValue(rowId, colId, property);	
					}				
				}
				
				if(tb.getType(colId).equals(Float.class)){				
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
						return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());					
					}else{
						return super.formatPropertyValue(rowId, colId, property);	
					}				
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
		
		
		//tipo","naturezaOperacao.descricao","tipoVenda","cliente.email","cliente.endereco_principal.endereco","cliente.endereco_principal.cidade","cliente.endereco_principal.bairro","cliente.endereco_principal.uf",
		tb.setVisibleColumns(new Object[] {"id","data","hora_pv","tipo","naturezaOperacao.descricao","tipoVenda","cliente.nome_razao","comprador","vendedor","situacao","total_acres","subTotal","total_desc","valor"});

		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("tipo", "Tipo");   
		tb.setColumnHeader("naturezaOperacao.descricao","Natureza Operação");
		tb.setColumnHeader("tipoVenda", "Natureza Itens");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("cliente.email", "Email");
		tb.setColumnHeader("cliente.endereco_principal.endereco", "Endereço");
		tb.setColumnHeader("cliente.endereco_principal.cidade", "Cidade");
		tb.setColumnHeader("cliente.endereco_principal.bairro", "Bairro");
		tb.setColumnHeader("cliente.endereco_principal.uf", "UF");
		tb.setColumnHeader("data","Data");
		tb.setColumnHeader("hora_pv","Hora");
		//tb.setColumnHeader("formas_pgto","Forma Pagamento");
		tb.setColumnHeader("vendedor","Vendedor");	
		tb.setColumnHeader("entregar","Entrega");	
		tb.setColumnHeader("comprador","Comprador");			
		tb.setColumnHeader("total_acres","Acresc. R$");			
		tb.setColumnHeader("total_desc","Desc. %");			
		tb.setColumnHeader("subTotal","SubToal");			
		tb.setColumnHeader("valor","Valor Total");
		tb.setColumnHeader("situacao","Status");	
		
		
		tb.setColumnCollapsible("cliente.nome_razao",false);	
		
		
		
		tb.setColumnCollapsingAllowed(true);
		
		//tb.setConverter("id", null);

		
		tb.setImmediate(true);
		
		
		
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
					
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
		tb.sort(new Object[]{"id"}, new boolean[]{false});

		
		return tb;
	}

	public TextField buildTfbusca() {
		tfNomeRelatorio = new TextField();
		tfNomeRelatorio.setWidth("100%");
		tfNomeRelatorio.addStyleName("align-center");
		tfNomeRelatorio.addStyleName("bold");
		tfNomeRelatorio.setValue("");
		tfNomeRelatorio.setReadOnly(true);

		return tfNomeRelatorio;
	}

	
	public void addFilter(Integer s) {
		// TODO Auto-generated method stub
		
	}

	
	public void addFilter(String s) {
		relatoriosContainer.removeAllContainerFilters();
		relatoriosContainer.addContainerFilter(Filters.eq("codEmpresa", OpusERP4UI.getEmpresa().getId()));
		relatoriosContainer.addContainerFilter(Filters.eq("codUsuario", OpusERP4UI.getUsuarioLogadoUI()));
		relatoriosContainer.addContainerFilter(Filters.eq("codSubModulo", getCodSubModulo()));
		relatoriosContainer.sort(new Object[]{"data_cadastro"}, new boolean[]{false});
		relatoriosContainer.addNestedContainerProperty("codUsuario.username");	
		
		Object[] collums = tbRelatorios.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 

			
			
			if(!c.toString().equals("x")){					   		
				if(relatoriosContainer.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}			
			}			
		}
		
		relatoriosContainer.addContainerFilter(Filters.or(filtros));
		relatoriosContainer.applyFilters();
//		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	private void loadRelatorioColuna(List<RelatorioPreColunas> colunas){
		
		for (Object ob : tb.getVisibleColumns()) {
			tb.setColumnCollapsible(ob.toString(), true);
			tb.setColumnCollapsed(ob.toString(), true);
		}
		
		
		for (RelatorioPreColunas relatorioPreColunas : colunas) {
			tb.setColumnCollapsible(relatorioPreColunas.getColuna(), true);
			tb.setColumnCollapsed(relatorioPreColunas.getColuna(), false);
		}	
		
	}
	
	private void loadRelatorio(List<RelatorioPreFiltro> filtros){
		
		try{
		
			listaParametros = new ArrayList<>();
			Integer i = 1;
			for (RelatorioPreFiltro filtro : filtros) {
				
				
				String idSearch = String.valueOf(listaParametros.size())+filtro.getValor();
				listaParametros.add(new SearchParameters(idSearch, filtro.getColuna(), filtro.getOperador(), filtro.getValor()));
														
				adicionarFiltro();

				i++;
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public String selectFiltro(String s) {
		
		String filtro = "";
		
		if(s.equals("Cliente")){
			filtro = "cliente.nome_razao";												
		}else if(s.equals("Código")){
			filtro = "id";					
		}
//		else if(s.equals("Forma Pagamento")){
//			filtro = "formas_pgto";					
//		}
		else if(s.equals("Data")){
			filtro = "data";				
		}else if(s.equals("Hora")){
			filtro = "hora_pv";				
		}else if(s.equals("Status")){
			filtro = "situacao";
		}else if(s.equals("Natureza Itens")){
			filtro = "tipoVenda";
		}else if(s.equals("Acrescimo")){
			filtro = "total_acres";	
		}else if(s.equals("Desconto")){
			filtro = "total_desc";	
		}else if(s.equals("SubTotal")){
			filtro = "subTotal";	
		}else if(s.equals("Valor Total")){
			filtro = "valor";	
		}else if(s.equals("Natureza Operação")){
			filtro = "naturezaOperacao.descricao";
		}else if(s.equals("Comprador")){
			filtro = "comprador";
		}else if(s.equals("Tipo")){
			filtro = "tipo";
		}else if(s.equals("Vendedor")){
			filtro = "vendedor";
		}else if(s.equals("Entrega")){
			filtro = "entregar";
		}else if(s.equals("Endereço")){
			filtro = "cliente.endereco_principal.endereco";						
		}else if(s.equals("Bairro")){
			filtro = "cliente.endereco_principal.bairro";			
		}else if(s.equals("Cidade")){
			filtro = "cliente.endereco_principal.cidade";					
		}else if(s.equals("UF")){
			filtro = "cliente.endereco_principal.uf";
		}else if(s.equals("Email")){
			filtro = "cliente.email";
		}
		return filtro;
		
	}
	
	public boolean adicionarFiltro() throws UnsupportedFilterException, ParseException{
		boolean check = true;
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.not(new Equal("tipo","CORRECAO")));

		if(ordenacao != null && !ordenacao.equals("Bairro") && !ordenacao.equals("Cidade") && 
		   !ordenacao.equals("Endereço") && !ordenacao.equals("UF")){
			
			container.sort(new Object[]{selectFiltro(ordenacao)}, new boolean[]{true});
		}

					
		
		for(SearchParameters sp:listaParametros){
			
				if(sp.getOperador().equals("IGUAL")){

					
					if(container.getType(sp.getCampo()) == String.class){
						
						
						check = true;
						
						if(sp.getCampo().equals("cliente.nome_razao") || sp.getCampo().equals("cliente.id") || sp.getCampo().equals("cliente.email")|| sp.getCampo().equals("cliente.transportadora_padrao")
							|| sp.getCampo().equals("cliente.endereco_principal.cep") || sp.getCampo().equals("cliente.endereco_principal.endereco") || sp.getCampo().equals("cliente.endereco_principal.numero")
							|| sp.getCampo().equals("cliente.endereco_principal.bairro") || sp.getCampo().equals("cliente.endereco_principal.cidade") || sp.getCampo().equals("cliente.endereco_principal.pais")
							|| sp.getCampo().equals("cliente.endereco_principal.uf") || sp.getCampo().equals("cliente.endereco_principal.referencia")||sp.getCampo().equals("cliente.categoria.nome")
							|| sp.getCampo().equals("cliente.telefone1")|| sp.getCampo().equals("cliente.telefone2") || sp.getCampo().equals("cliente.celular1")||sp.getCampo().equals("cliente.celular2")){
				
							container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), false));					
						}else{
							
							if(sp.getCampo().equals("situacao")){								
								container.addContainerFilter(new Equal(sp.getCampo(),(sp.getValor().substring(0, 1))));							
							}else{							
								container.addContainerFilter(new Equal(sp.getCampo(),(sp.getValor())));
							}
						}
	
					}else if(container.getType(sp.getCampo()) == Date.class){
						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						container.addContainerFilter(new Equal(sp.getCampo(), sdf.parse(sp.getValor())));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
						
					}else if(container.getType(sp.getCampo()) == Float.class){
						
						container.addContainerFilter(new Equal(sp.getCampo(), Float.parseFloat(sp.getValor())));
					}
					
				}else if(sp.getOperador().equals("DIFERENTE")){
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
							
						
						if(sp.getCampo().equals("cliente.nome_razao") || sp.getCampo().equals("cliente.id") || sp.getCampo().equals("cliente.email")|| sp.getCampo().equals("cliente.transportadora_padrao")
								|| sp.getCampo().equals("cliente.endereco_principal.cep") || sp.getCampo().equals("cliente.endereco_principal.endereco") || sp.getCampo().equals("cliente.endereco_principal.numero")
								|| sp.getCampo().equals("cliente.endereco_principal.bairro") || sp.getCampo().equals("cliente.endereco_principal.cidade") || sp.getCampo().equals("cliente.endereco_principal.pais")
								|| sp.getCampo().equals("cliente.endereco_principal.uf") || sp.getCampo().equals("cliente.endereco_principal.referencia")||sp.getCampo().equals("cliente.categoria.nome")
								|| sp.getCampo().equals("cliente.telefone1")|| sp.getCampo().equals("cliente.telefone2") || sp.getCampo().equals("cliente.celular1")||sp.getCampo().equals("cliente.celular2")){
											
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(),sp.getValor(),false)));
						}else{
							if(sp.getCampo().equals("situacao")){								
								container.addContainerFilter(Filters.not(new Equal(sp.getCampo(),(sp.getValor().substring(0, 1)))));							
							}else{							
								container.addContainerFilter(Filters.not(new Equal(sp.getCampo(),(sp.getValor()))));
							}
						}
								
					}else if(container.getType(sp.getCampo()) == Date.class){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), sdf.parseObject(sp.getValor()))));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), Integer.parseInt(sp.getValor()))));
						
					}else if(container.getType(sp.getCampo()) == Float.class){
						
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), Float.parseFloat(sp.getValor()))));
					}
					
				}else if(sp.getOperador().equals("CONTEM")){
					
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
						
						if(sp.getCampo().equals("situacao")){
							container.addContainerFilter(new Like(sp.getCampo(),"%"+sp.getValor().substring(0, 1)+"%", false));
						}else{
							container.addContainerFilter(new Like(sp.getCampo(),"%"+sp.getValor()+"%", false));
						}
					}
					
				}else if(sp.getOperador().equals("NAO CONTEM")){
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
								
						if(sp.getCampo().equals("situacao")){
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(),"%"+sp.getValor().substring(0, 1)+"%", false)));
						}else{
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(),"%"+sp.getValor()+"%", false)));
						}
					}
					
				}else if(sp.getOperador().equals("MAIOR QUE")){
					
										
					if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							check = true;					
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new Greater(sp.getCampo(), dtValor));
							
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new Greater(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					
					if(container.getType(sp.getCampo()) == Float.class){
						check = true;						
						container.addContainerFilter(new Greater(sp.getCampo(), Float.parseFloat(sp.getValor())));					
					}
					
				}else if(sp.getOperador().equals("MENOR QUE")){
					
										
					if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							check = true;						
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							Date dtValor = sdf.parse(date);						
							container.addContainerFilter(new Less(sp.getCampo(), dtValor));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new Less(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					if(container.getType(sp.getCampo()) == Float.class){
						check = true;						
						container.addContainerFilter(new Less(sp.getCampo(), Float.parseFloat(sp.getValor())));					
					}
				}else if(sp.getOperador().equals("MAIOR IGUAL QUE")){
										
					if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							check = true;						
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), dtValor));
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					if(container.getType(sp.getCampo()) == Float.class){
						check = true;						
						container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), Float.parseFloat(sp.getValor())));					
					}
				}else if(sp.getOperador().equals("MENOR IGUAL QUE")){
					
										
					if(container.getType(sp.getCampo()) == Date.class){
									
						try {
							check = true;							
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");							
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new LessOrEqual(sp.getCampo(), dtValor));						
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new LessOrEqual(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					if(container.getType(sp.getCampo()) == Float.class){
						check = true;						
						container.addContainerFilter(new LessOrEqual(sp.getCampo(), Float.parseFloat(sp.getValor())));					
					}
				}
		}		
		
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
		
		return check;
	}
	
	public Button BuildbtEditar(){
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
							
				if(listaParametros != null && listaParametros.size() > 0){
				
						NovoRelatorioPedido v = new NovoRelatorioPedido("Editar Relatório Pedido", true,tipo, orientacao, ordenacao,resumo, listaParametros );
						v.addListerner(new NovoRelatorioPedido.RelatorioPedidoListerner() {
							
							@Override
							public void onClose(RelatorioPedidoEvent event) {
								if(event.isConfirm()){
									
									try{
										listaParametros = event.getParametros();
										tipo = event.getTipo();
										ordenacao = event.getOrdenacao();
										orientacao = event.getOrientacao();
										resumo = event.getResumo();
										
										adicionarFiltro();

																			
										
										if(relatorioPre == null || relatorioPre.getId() == null){
											
											relatorioPre = new RelatorioPre(null, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI(),	codSubModulo, tfNomeRelatorio.getValue(),tipo,ordenacao,orientacao,resumo, new Date());
										}else{
											relatorioPre.setNome_relatorio(tfNomeRelatorio.getValue());
											relatorioPre.setOrdenacao_relatorio(ordenacao);
											relatorioPre.setOrientacao(orientacao);
											relatorioPre.setResumo(resumo);
											relatorioPre.setTipo_relatorio(tipo);
											
										}
										
										filtrosRelatorio = new ArrayList<>();
										
										for (SearchParameters search : listaParametros) {
											filtrosRelatorio.add(new RelatorioPreFiltro(null, relatorioPre, search.getCampo(),search.getOperador(), search.getValor()));
										}
										
										colunasVisiveis = new ArrayList<>();
										Object[] visible_columns = tb.getVisibleColumns();		
										for(Object c:visible_columns){
											if(!tb.isColumnCollapsed(c.toString())){					   	
												colunasVisiveis.add(new RelatorioPreColunas(null, relatorioPre, c.toString()));
											}
										}
											
										RelatorioDAO relatoriDAO = new RelatorioDAO();
										
										if(relatorioPre.getNome_relatorio()!=null && !relatorioPre.getNome_relatorio().equals("")){
											relatoriDAO.addRelatorio(relatorioPre, filtrosRelatorio,colunasVisiveis);											
										
											Notification.show("Relatório Salvo com Sucesso", Type.ERROR_MESSAGE);
											btSalvar.setEnabled(false);
											btImprimir.setEnabled(true);
											btGerarChart.setEnabled(true);
										}
									}catch (Exception e) {
										e.printStackTrace();
									}
									
									
								}
									
								
			 				}
						});
						
						v.addListerner(new NovoRelatorioPedido.AddFiltroPedidoListerner() {
							
							@Override
							public void onClose(AddFiltroPedidoEvent event) {
								try{
									listaParametros = event.getFiltros();
									adicionarFiltro();
								
								}catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						
						
						getUI().addWindow(v);
				
				}else{
					Notification.show("Você precisa Carregar um Relatório Salvo para Editar Filtros!", Type.ERROR_MESSAGE);
				}
			}
		});
		btEditar.setEnabled(false);
		return btEditar;
	}
	
	public Button BuildbtNovo() {
		btNovo = new Button("Novo Filtro", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
								
				relatorioPre = new RelatorioPre();
				listaParametros = new ArrayList<>();
				tipo = null;
				ordenacao = null;
				tfNomeRelatorio.setReadOnly(false);
				tfNomeRelatorio.setValue("");
				tfNomeRelatorio.setReadOnly(true);
				
				
				container.removeAllContainerFilters();
				container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
				container.applyFilters();
				
				hlFloat.replaceComponent(lbRegistros, lbRegistros());
				
				NovoRelatorioPedido v = new NovoRelatorioPedido("Novo Relatório de Pedido", true);
				v.addListerner(new NovoRelatorioPedido.RelatorioPedidoListerner() {
					
					@Override
					public void onClose(RelatorioPedidoEvent event) {
						if(event.isConfirm()){
							try{
								listaParametros = event.getParametros();
								tipo = event.getTipo();
								ordenacao = event.getOrdenacao();
								orientacao = event.getOrientacao();
								resumo = event.getResumo();
								
								adicionarFiltro();
								btSalvar.setEnabled(true);
								btImprimir.setEnabled(true);
								btEditar.setEnabled(true);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else{
							relatorioPre = new RelatorioPre();
							listaParametros = new ArrayList<>();
							tipo = null;
							ordenacao = null;
							
							tfNomeRelatorio.setReadOnly(false);
							tfNomeRelatorio.setValue("");
							tfNomeRelatorio.setReadOnly(true);
							btSalvar.setEnabled(false);
							btEditar.setEnabled(false);
							btGerarChart.setEnabled(false);
							//btImprimir.setEnabled(false);Ose
						}
	 				}
				});
				
				v.addListerner(new NovoRelatorioPedido.AddFiltroPedidoListerner() {
					
					@Override
					public void onClose(AddFiltroPedidoEvent event) {
						try{
							listaParametros = event.getFiltros();
							adicionarFiltro();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
				
				
				getUI().addWindow(v);
			}
		});
		return btNovo;
		
	}
	

	private boolean newFile = true;
	
	public Button BuildbtSalvar(){
		btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
								
				final SalvarRelatorioPedidoEditor salvarEditor = new SalvarRelatorioPedidoEditor(codSubModulo,"Salvar Relatório", true);
				salvarEditor.addListerner(new SalvarRelatorioPedidoEditor.SalvarRelatorioPedidoListerner() {
					
					@Override
					public void onClose(SalvarRelatorioPedidoEvent event) {
						if(!event.getNome().isEmpty() && listaParametros.size() >0 && !ordenacao.isEmpty() && !tipo.isEmpty()){
							
							tfNomeRelatorio.setReadOnly(false);
							tfNomeRelatorio.setValue(event.getNome());
							tfNomeRelatorio.setReadOnly(true);
							
							
							if(relatorioPre == null || relatorioPre.getId() == null){
								
								relatorioPre = new RelatorioPre(null, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI(),	codSubModulo, event.getNome(),tipo,ordenacao,orientacao,resumo, new Date());
							}else{
								relatorioPre.setNome_relatorio(event.getNome());
							}
							
							filtrosRelatorio = new ArrayList<>();
							
							for (SearchParameters search : listaParametros) {
								filtrosRelatorio.add(new RelatorioPreFiltro(null, relatorioPre, search.getCampo(),search.getOperador(), search.getValor()));
							}
							
							colunasVisiveis = new ArrayList<>();
							Object[] visible_columns = tb.getVisibleColumns();		
							for(Object c:visible_columns){
								if(!tb.isColumnCollapsed(c.toString())){					   	
									colunasVisiveis.add(new RelatorioPreColunas(null, relatorioPre, c.toString()));
								}
							}
								
							RelatorioDAO relatoriDAO = new RelatorioDAO();
							relatoriDAO.addRelatorio(relatorioPre, filtrosRelatorio,colunasVisiveis);
							
							Notification.show("Relatório Salvo com Sucesso", Type.ERROR_MESSAGE);
							btSalvar.setEnabled(false);
						}else{					
								
//							getUI().addWindow(salvarEditor);
						//	Notification.show("Informe um Nome para o Relatório e Parametros de Busca", Type.ERROR_MESSAGE);
						}
					}
				});
							
				getUI().addWindow(salvarEditor);
						
			}
		});
		btSalvar.setEnabled(false);
		return btSalvar;
	}
	
	
	public Button BuildbtCarregar(){
		btCarregar = new Button("Carregar", new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
								
				winRelatorios = new Window("Relatórios Salvos");
				winRelatorios.setWidth("850px");
				winRelatorios.setHeight("360px");
				winRelatorios.center();
				winRelatorios.setModal(true);
				winRelatorios.setResizable(false);
				winRelatorios.setClosable(false);
				
				winRelatorios.setContent(new VerticalLayout(){
					{
						setSizeFull();
						setMargin(true);
						
						TextField tfBusca = new TextField();
						tfBusca.setInputPrompt("Buscar...");
						tfBusca.setWidth("100%");
						tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
						tfBusca.focus();
						tfBusca.addListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								addFilter(event.getText());
							}
						});
						
						addComponent(tfBusca);
						

						//Busca Relatorios
						relatoriosContainer = JPAContainerFactory.make(RelatorioPre.class, ConnUtil.getEntity());
						relatoriosContainer.addContainerFilter(Filters.eq("codEmpresa", OpusERP4UI.getEmpresa().getId()));
						relatoriosContainer.addContainerFilter(Filters.eq("codUsuario", OpusERP4UI.getUsuarioLogadoUI()));
						relatoriosContainer.addContainerFilter(Filters.eq("codSubModulo", getCodSubModulo()));
						relatoriosContainer.addNestedContainerProperty("codUsuario.username");
						
						relatoriosContainer.sort(new Object[]{"data_cadastro"}, new boolean[]{false});

						tbRelatorios = new Table(null, relatoriosContainer);
						
						final Button btOk = new Button("Carregar", new Button.ClickListener() {
							
							public void buttonClick(ClickEvent event) {
								RelatorioDAO relatorioDAO = new RelatorioDAO();								
								relatorioPre = relatorioDAO.find((Integer)tbRelatorios.getItem(tbRelatorios.getValue()).getItemProperty("id").getValue());
								
								if(relatorioPre != null && relatorioPre.getId() != null){
									
									
									tfNomeRelatorio.setReadOnly(false);
									tfNomeRelatorio.setValue(relatorioPre.getNome_relatorio());
									tfNomeRelatorio.setReadOnly(true);
									
									tipo = relatorioPre.getTipo_relatorio();
									ordenacao = relatorioPre.getOrdenacao_relatorio();
									orientacao = relatorioPre.getOrientacao();
									resumo = relatorioPre.getResumo();
									//Chamar Função de Montar
									List<RelatorioPreFiltro> filtros = relatorioDAO.getFiltros(relatorioPre);
									List<RelatorioPreColunas> colunas = relatorioDAO.getColunas(relatorioPre);											
									
									
									if(filtros != null){
										loadRelatorio(filtros);
									}
									
									if(colunas != null){
										loadRelatorioColuna(colunas);
									}
									
									winRelatorios.close();
									//change = false;								

									btSalvar.setEnabled(false);
									btImprimir.setEnabled(true);
									btEditar.setEnabled(true);	
									btGerarChart.setEnabled(true);
									//change = false;
								
								}
							}				
							
						});
						
						btOk.setEnabled(false);
						btOk.setStyleName("default");
						
						
						ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
							
							@Override
							public void handleAction(Object sender, Object target) {
								btOk.click();
							}
						};
						
						btOk.addShortcutListener(slbtOK);
						
						
						tbRelatorios.setWidth("100%");
						tbRelatorios.setHeight("230px");
						tbRelatorios.setSelectable(true);          
						tbRelatorios.setVisibleColumns(new Object[]{"nome_relatorio","data_cadastro","codUsuario.username","tipo_relatorio","ordenacao_relatorio",
								"orientacao","resumo"});

						tbRelatorios.setColumnHeader("nome_relatorio", "Nome");
						tbRelatorios.setColumnHeader("data_cadastro", "Data de Criação");
						tbRelatorios.setColumnHeader("codUsuario.username", "Autor");
						tbRelatorios.setColumnHeader("tipo_relatorio", "Tipo");
						tbRelatorios.setColumnHeader("ordenacao_relatorio", "Ordenação");
						tbRelatorios.setColumnHeader("orientacao", "Orientação");
						tbRelatorios.setColumnHeader("resumo", "Resumo");
						
						tbRelatorios.setColumnExpandRatio("nome_relatorio", 1);
						
						
						
						tbRelatorios.setImmediate(true);
						
						tbRelatorios.addListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(tbRelatorios.getValue() != null){
									btOk.setEnabled(true);
								}else{
									btOk.setEnabled(false);
								}
							}
						});
						
						
						tbRelatorios.addGeneratedColumn("x", new Table.ColumnGenerator() {
							
							@Override
							public Object generateCell(final Table source, final Object itemId, Object columnId) {
								
								
								
								Button btDeletar = new Button(null, new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Excluir este Relatório?", true,true);
										gd.addListerner(new GenericDialog.DialogListerner() {
											
											@Override
											public void onClose(DialogEvent event) {
												if(event.isConfirm()){		
													Integer codRelatorio  = (Integer)tbRelatorios.getItem(itemId).getItemProperty("id").getValue();
													RelatorioDAO relatorioDAO = new RelatorioDAO();
													boolean check = relatorioDAO.apagarRelatorio(codRelatorio);
													
													if(check){
														Notification.show("Relatório foi Excluído com Sucesso!");
														
														if(relatoriosContainer != null){
															refresh();
															relatoriosContainer.refresh();
														}
													}else{
														Notification.show("Não foi Possivel Realizar a Exclusão do Relatório");
													}										
												}
											}
										});
										
										getUI().addWindow(gd);
									}
								});
								btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
								btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
								btDeletar.setDescription("Deletar Relatório");
								
								return btDeletar;
							}
						});

						tbRelatorios.setColumnWidth("x", 20);
								
						addComponent(tbRelatorios);
						setExpandRatio(tbRelatorios, 2);
						
						HorizontalLayout hlButtons = new HorizontalLayout();
						hlButtons.setSpacing(true);
						hlButtons.setMargin(true);
						hlButtons.setStyleName("hl_buttons_bottom");
						hlButtons.addComponent(new Button("Cancelar", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
									winRelatorios.close();				
							}
						}));
						
						hlButtons.addComponent(btOk);
						
						addComponent(hlButtons);
						setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
						
						
						ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
							
							@Override
							public void handleAction(Object sender, Object target) {
								winRelatorios.close();
							}
						};
						hlButtons.addShortcutListener(slbtCancelar);
					}
				});
				
				getUI().addWindow(winRelatorios);				
			}		
		});
		
		return btCarregar;
	}
	
	public Button BuildbtGerarChart(){
		btGerarChart = new Button("Gerar Gráfico", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Gerar Grafico"))				
				{
			
					try {
						
						List<Object> columns = new ArrayList<Object>();
						Object[] visible_columns = tb.getVisibleColumns();		
						for(Object c:visible_columns){
							if(!tb.isColumnCollapsed(c.toString())){					   	
								columns.add(c);
							}	
						}
						
						
						// INSTANCIA UMA NOVA JANELA E ADICIONA SUAS PROPRIEDADES
						Window win = new Window("Gráfico de Resumo");
						win.setWidth("800px");
						win.setHeight("600px");
						win.setResizable(true);
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
	
				        final Chart chart = new Chart(ChartType.COLUMN);
				        
				        setCaption(relatorioPre.getNome_relatorio());
				        chart.getConfiguration().setTitle(relatorioPre.getNome_relatorio());
				        
				        chart.getConfiguration().getxAxis().getLabels().setEnabled(false);
				        chart.getConfiguration().getxAxis().setTickWidth(0);
				        chart.getConfiguration().setExporting(true);
				        chart.setWidth("100%");
				        chart.setHeight("100%");		        
				        
						TypedQuery qGroup = gerarDadosGRaficos();		        
				        final List<Series> seriesColum = new ArrayList<Series>();
				        final DataSeries seriesPie = new DataSeries();
				        
				        for(EcfPreVendaCabecalho c:(List<EcfPreVendaCabecalho>) qGroup.getResultList())
				        {       	
				        	seriesColum.add(new ListSeries(c.getColuna(),c.getQtd()));
				        	seriesPie.add(new DataSeriesItem(c.getColuna(), c.getQtd()));
				        	
				        }        
				       
				        chart.getConfiguration().setSeries(seriesColum);
									        
				        final VerticalLayout vlChart = new VerticalLayout();
				        vlChart.setWidth("100%");
				        vlChart.setHeight("100%");
				        			        
				        final ComboBox cbTipoGrafico = new ComboBox();
				        cbTipoGrafico.setNullSelectionAllowed(false);
				        cbTipoGrafico.addStyleName("margin-right");
				        cbTipoGrafico.addItem("COLUNA");			        
				        cbTipoGrafico.addItem("BARRA");
				        cbTipoGrafico.addItem("PIZZA");
				        
				        
				        cbTipoGrafico.select("COLUNA");
				        cbTipoGrafico.setImmediate(true);
				        cbTipoGrafico.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								
								if(cbTipoGrafico.getValue().toString().equals("COLUNA")){
									 chart.getConfiguration().getChart().setType(ChartType.COLUMN);
									 chart.getConfiguration().setSeries(seriesColum);
									
									 chart.drawChart();
								}else if(cbTipoGrafico.getValue().toString().equals("BARRA")){
									 chart.getConfiguration().getChart().setType(ChartType.BAR);
									 chart.getConfiguration().setSeries(seriesColum);
									
									 chart.drawChart();
								}else if(cbTipoGrafico.getValue().toString().equals("PIZZA")){
									 chart.getConfiguration().getChart().setType(ChartType.PIE);
									 chart.getConfiguration().setSeries(seriesPie);
									
									 PlotOptionsPie plotOptions = new PlotOptionsPie();
								     plotOptions.setAllowPointSelect(true);
								     plotOptions.setCursor(Cursor.POINTER);
								     plotOptions.setShowInLegend(true);
								     Labels dataLabels = new Labels();
								     dataLabels.setEnabled(true);
								     chart.getConfiguration().setPlotOptions(plotOptions);
									
									 chart.drawChart();
								}
								
								vlChart.replaceComponent(chart, chart);
								vlChart.setExpandRatio(chart, 1);
								
							}
						});
				        
				        HorizontalLayout hlTopo = new HorizontalLayout();
				        hlTopo.setWidth("100%");
				        hlTopo.addComponent(cbTipoGrafico);
				        hlTopo.setComponentAlignment(cbTipoGrafico, Alignment.TOP_RIGHT);
				        
				        vlChart.addComponent(hlTopo);
				        vlChart.addComponent(chart);
				        vlChart.setExpandRatio(chart, 1);
				        
						win.setContent(vlChart);
						getUI().addWindow(win);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						System.out.println("Erro: "+e.getMessage());
						System.out.println("Causado por: "+e.getCause());
					}	
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Gerar Gráfico", Type.ERROR_MESSAGE);	
				}
			}
		});
		
		btGerarChart.setEnabled(false);
		
		return btGerarChart;
	}
	
	private TypedQuery<EcfPreVendaCabecalho> gerarDadosGRaficos(){
		
		EntityManager em = ConnUtil.getEntity();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EcfPreVendaCabecalho> criteriaQuery = cb.createQuery(EcfPreVendaCabecalho.class);
		Root<EcfPreVendaCabecalho> rootContas = criteriaQuery.from(EcfPreVendaCabecalho.class);
		EntityType<EcfPreVendaCabecalho> type = em.getMetamodel().entity(EcfPreVendaCabecalho.class);

		List<Predicate> criteria = new ArrayList<Predicate>();

		criteria.add(cb.equal(rootContas.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));
		criteria.add(cb.notLike(cb.lower(rootContas.<String> get("tipo")), "CORRECAO".toLowerCase()));

		if (listaParametros.size() > 0) {
			
			try{
				for (SearchParameters s : listaParametros) {

					if (s.getOperador().equals("IGUAL")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.id")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("id")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("email")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("telefone1")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("telefone2")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("celular1")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("celular2")), s.getValor().toLowerCase()));
						}	
						
						//endereco_principals
						if (s.getCampo().equals("cliente.endereco_principal.endereco")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("endereco")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.bairro")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("bairro")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.cidade")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("cidade")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.uf")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("uf")), s.getValor().toLowerCase()));
						}
												
//						if (s.getCampo().equals("formas_pgto")) {
//							criteria.add(cb.like(cb.lower(rootContas.get("formaPagtoID").<String>get("nome")), s.getValor().toLowerCase()));
//						}					
						if (s.getCampo().equals("naturezaOperacao.descricao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("naturezaOperacao").<String>get("descricao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.transportadora_padrao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("transportadora_padrao")), s.getValor().toLowerCase()));
						}					
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.id") && !s.getCampo().equals("cliente.email") && 
								!s.getCampo().equals("cliente.endereco_principal.cep") && !s.getCampo().equals("cliente.endereco_principal.numero") &&
								!s.getCampo().equals("cliente.telefone1") && !s.getCampo().equals("cliente.telefone1") && !s.getCampo().equals("cliente.celular1") &&
								!s.getCampo().equals("cliente.celular2") && !s.getCampo().equals("cliente.transportadora_padrao") &&
								!s.getCampo().equals("cliente.endereco_principal.endereco") && !s.getCampo().equals("cliente.endereco_principal.bairro") &&
								!s.getCampo().equals("cliente.endereco_principal.uf") && !s.getCampo().equals("cliente.endereco_principal.cidade") && 
								!s.getCampo().equals("cliente.endereco_principal.pais") && !s.getCampo().equals("cliente.endereco_principal.complemento") &&
								!s.getCampo().equals("cliente.endereco_principal.referencia")) {
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.equal(rootContas.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.like(cb.lower(rootContas.<String>get(s.getCampo())), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.equal(rootContas.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Float.class)){
								criteria.add(cb.equal(rootContas.<Float>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
						}

					}else if (s.getOperador().equals("DIFERENTE")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.id")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("id")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("email")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("telefone1")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("telefone2")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("celular1")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("celular2")), s.getValor().toLowerCase()));
						}	
						
						//endereco_principals
						if (s.getCampo().equals("cliente.endereco_principal.endereco")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("endereco")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.bairro")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("bairro")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.cidade")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("cidade")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.uf")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("uf")), s.getValor().toLowerCase()));
						}
						
//						if (s.getCampo().equals("formas_pgto")) {
//							criteria.add(cb.notLike(cb.lower(rootContas.get("formaPagtoID").<String>get("nome")), s.getValor().toLowerCase()));
//						}	
						if (s.getCampo().equals("naturezaOperacao.descricao")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("naturezaOperacao").<String>get("descricao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.transportadora_padrao")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("transportadora_padrao")), s.getValor().toLowerCase()));
						}					
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.id") && !s.getCampo().equals("cliente.email") && 
								!s.getCampo().equals("cliente.endereco_principal.cep") && !s.getCampo().equals("cliente.endereco_principal.numero") &&
								!s.getCampo().equals("cliente.telefone1") && !s.getCampo().equals("cliente.telefone1") && !s.getCampo().equals("cliente.celular1") &&
								!s.getCampo().equals("cliente.celular2") &&  !s.getCampo().equals("cliente.transportadora_padrao") &&
								!s.getCampo().equals("cliente.endereco_principal.endereco") && !s.getCampo().equals("cliente.endereco_principal.bairro") &&
								!s.getCampo().equals("cliente.endereco_principal.uf") && !s.getCampo().equals("cliente.endereco_principal.cidade") && 
								!s.getCampo().equals("cliente.endereco_principal.pais") && !s.getCampo().equals("cliente.endereco_principal.complemento") &&
								!s.getCampo().equals("cliente.endereco_principal.referencia")) {							
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.notEqual(rootContas.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.notLike(cb.lower(rootContas.<String> get(s.getCampo())), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.notEqual(rootContas.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Float.class)){
								criteria.add(cb.equal(rootContas.<Float>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
						}

					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.id")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("id")),"%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("email")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("telefone1")),"%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("telefone2")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("celular1")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("celular2")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						
						//endereco_principals
						if (s.getCampo().equals("cliente.endereco_principal.endereco")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.bairro")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.cidade")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.uf")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						
//						if (s.getCampo().equals("formas_pgto")) {
//							criteria.add(cb.like(cb.lower(rootContas.get("formaPagtoID").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
//						}		
						if (s.getCampo().equals("naturezaOperacao.descricao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("naturezaOperacao").<String>get("descricao")), "%" + s.getValor().toLowerCase()+ "%"));
						}			
						if (s.getCampo().equals("cliente.transportadora_padrao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("transportadora_padrao")), "%" + s.getValor().toLowerCase()+ "%"));
						}			
												
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.id") && !s.getCampo().equals("cliente.email") && 
								!s.getCampo().equals("cliente.endereco_principal.cep") && !s.getCampo().equals("cliente.endereco_principal.numero") &&
								!s.getCampo().equals("cliente.telefone1") && !s.getCampo().equals("cliente.telefone1") && !s.getCampo().equals("cliente.celular1") &&
								!s.getCampo().equals("cliente.celular2")  && !s.getCampo().equals("cliente.transportadora_padrao") &&
								!s.getCampo().equals("cliente.endereco_principal.endereco") && !s.getCampo().equals("cliente.endereco_principal.bairro") &&
								!s.getCampo().equals("cliente.endereco_principal.uf") && !s.getCampo().equals("cliente.endereco_principal.cidade") && 
								!s.getCampo().equals("cliente.endereco_principal.pais") && !s.getCampo().equals("cliente.endereco_principal.complemento") &&
								!s.getCampo().equals("cliente.endereco_principal.referencia")) {
							
							criteria.add(cb.like(cb.lower(rootContas.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.id")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("id")),"%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("email")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("telefone1")),"%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("telefone2")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("celular1")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("celular2")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						
						//endereco_principals
						if (s.getCampo().equals("cliente.endereco_principal.endereco")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.bairro")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.cidade")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.uf")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("endereco_principal").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						
//						if (s.getCampo().equals("formas_pgto")) {
//							criteria.add(cb.notLike(cb.lower(rootContas.get("formaPagtoID").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
//						}
						if (s.getCampo().equals("cliente.transportadora_padrao")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("transportadora_padrao")), "%" + s.getValor().toLowerCase()+ "%"));
						}			
												
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.id") && !s.getCampo().equals("cliente.email") && 
								!s.getCampo().equals("cliente.endereco_principal.cep") && !s.getCampo().equals("cliente.endereco_principal.numero") &&
								!s.getCampo().equals("cliente.telefone1") && !s.getCampo().equals("cliente.telefone1") && !s.getCampo().equals("cliente.celular1") &&
								!s.getCampo().equals("cliente.celular2")  && !s.getCampo().equals("cliente.transportadora_padrao") &&
								!s.getCampo().equals("cliente.endereco_principal.endereco") && !s.getCampo().equals("cliente.endereco_principal.bairro") &&
								!s.getCampo().equals("cliente.endereco_principal.uf") && !s.getCampo().equals("cliente.endereco_principal.cidade") && 
								!s.getCampo().equals("cliente.endereco_principal.pais") && !s.getCampo().equals("cliente.endereco_principal.complemento") &&
								!s.getCampo().equals("cliente.endereco_principal.referencia")) {
							
							criteria.add(cb.like(cb.lower(rootContas.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
						
					} else if (s.getOperador().equals("MAIOR QUE")) {

							try{						
								if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThan(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.lessThan(rootContas.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){								
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.greaterThan(rootContas.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
								
							}catch(Exception e)
							{
								e.printStackTrace();		
						}
					} else if (s.getOperador().equals("MENOR QUE")) {

							try{						
								if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.lessThan(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.lessThan(rootContas.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.lessThan(rootContas.<Date> get(s.getCampo()),  sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							
						
					} else if (s.getOperador().equals("MAIOR IGUAL QUE")) {

							try{						
								if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThanOrEqualTo(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.lessThan(rootContas.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.greaterThanOrEqualTo(rootContas.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
		
					} else if (s.getOperador().equals("MENOR IGUAL QUE")) {

							try{						
								if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.lessThanOrEqualTo(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.lessThan(rootContas.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.lessThanOrEqualTo(rootContas.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
						}
				}
			
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		if (criteria.size() == 0) {
			throw new RuntimeException("no criteria");
		} else if (criteria.size() == 1) {
			criteriaQuery.where(criteria.get(0));
		} else {
			criteriaQuery.where(cb.and(criteria.toArray(new Predicate[0])));
		}

		TypedQuery q = em.createQuery(criteriaQuery);
        
        
		CriteriaQuery<EcfPreVendaCabecalho> criteriaQueryGroup = cb.createQuery(EcfPreVendaCabecalho.class);
		Root<EcfPreVendaCabecalho> rootGroup = criteriaQueryGroup.from(EcfPreVendaCabecalho.class);	
		
		if (selectFiltro(resumo).equals("cliente.nome_razao")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("nome_razao");		
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("nome_razao"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("cliente.id")) {			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("id");			
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("id"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
		}

		if (selectFiltro(resumo).equals("cliente.email")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("email");				
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("email"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
		}
		
		//endereco_principals				
		if (selectFiltro(resumo).equals("cliente.endereco_principal.endereco")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("endereco_principal").get("endereco");				
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("endereco_principal").get("endereco"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
		}
		if (selectFiltro(resumo).equals("cliente.endereco_principal.bairro")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("endereco_principal").get("bairro");			
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("endereco_principal").get("bairro"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));			
		}
		if (selectFiltro(resumo).equals("cliente.endereco_principal.cidade")) {		
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("endereco_principal").get("cidade");			
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("endereco_principal").get("cidade"));			
			
			
			if (criteria.size() == 0) {
				throw new RuntimeException("no criteria");
			} else if (criteria.size() == 1) {					
				criteriaQueryGroup.where(cb.and(criteria.get(0)));
			} else {					
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
			}
		
			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			
		}
		if (selectFiltro(resumo).equals("cliente.endereco_principal.uf")) {			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("endereco_principal").get("uf");				
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("endereco_principal").get("uf"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			
		}
		if (selectFiltro(resumo).equals("naturezaOperacao.descricao")) {				
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("naturezaOperacao").get("descricao");				
			criteriaQueryGroup.groupBy(rootGroup.join("naturezaOperacao").get("descricao"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
		}
//		if (selectFiltro(resumo).equals("formas_pgto")) {				
//			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
//			Selection<String> coluna = rootGroup.get("formaPagtoID").get("nome");				
//			criteriaQueryGroup.groupBy(rootGroup.join("formaPagtoID").get("nome"));			
//			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
//			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
//		}
		if (selectFiltro(resumo).equals("cliente.transportadora_padrao")) {				
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("transportadora_padrao");				
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("transportadora_padrao"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
		}
		
		if (!selectFiltro(resumo).equals("cliente.nome_razao") && !selectFiltro(resumo).equals("cliente.id") && !selectFiltro(resumo).equals("cliente.email") &&
				!selectFiltro(resumo).equals("cliente.telefone1") && !selectFiltro(resumo).equals("cliente.telefone2") && !selectFiltro(resumo).equals("cliente.transportadora_padrao") && 
				!selectFiltro(resumo).equals("cliente.celular1") &&!selectFiltro(resumo).equals("cliente.celular2") &&
				!selectFiltro(resumo).equals("cliente.endereco_principal.cep") && !selectFiltro(resumo).equals("cliente.endereco_principal.numero") &&
				!selectFiltro(resumo).equals("cliente.endereco_principal.endereco") && !selectFiltro(resumo).equals("cliente.endereco_principal.bairro") &&
				!selectFiltro(resumo).equals("cliente.endereco_principal.uf") && !selectFiltro(resumo).equals("cliente.endereco_principal.cidade") && 
				!selectFiltro(resumo).equals("cliente.endereco_principal.pais") && !selectFiltro(resumo).equals("cliente.endereco_principal.complemento") &&
				!selectFiltro(resumo).equals("cliente.endereco_principal.referencia")) {
			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get(selectFiltro(resumo));				
			criteriaQueryGroup.groupBy(rootGroup.get(selectFiltro(resumo)));			
			
			if (criteria.size() == 0) {
				throw new RuntimeException("no criteria");
			} else if (criteria.size() == 1) {					
				criteriaQueryGroup.where(criteria.get(0));
			} else {					
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
			}
			
			
			criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			
			
		}
		return em.createQuery(criteriaQueryGroup);	
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
          
        Button bt1 = new Button("Gerar PDF", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Gerar PDF"))				
				{

					try {
						
						List<Object> columns = new ArrayList<Object>();
						Object[] visible_columns = tb.getVisibleColumns();		
						for(Object c:visible_columns){
							if(!tb.isColumnCollapsed(c.toString())){					   	
								columns.add(c);
							}	
						}
	
						if(tfNomeRelatorio.getValue()!=null && !tfNomeRelatorio.getValue().equals("")){
							nomeRelatorio = " - "+relatorioPre.getNome_relatorio()+".pdf";
						}else{
							nomeRelatorio = ".pdf";
						}
						// INSTANCIA UMA NOVA JANELA E ADICIONA SUAS PROPRIEDADES
						Window win = new Window("Relatório de Pedido");
						win.setWidth("800px");
						win.setHeight("600px");
						win.setResizable(true);
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
						
						StreamResource resource = new StreamResource(new ExportarRelatorioPedido(tipo,ordenacao,orientacao,resumo,listaParametros,columns), "RELATORIO DE PEDIDOS"+nomeRelatorio);
						resource.getStream();
						resource.setMIMEType("application/pdf");
						resource.setCacheTime(0);
						
						Embedded e = new Embedded();
						e.setSizeFull();
						e.setType(Embedded.TYPE_BROWSER);
						e.setSource(resource);
						
						win.setContent(e);
						getUI().addWindow(win);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						System.out.println("Erro: "+e.getMessage());
						System.out.println("Causado por: "+e.getCause());
					}
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Gerar PDF", Type.ERROR_MESSAGE);	
				}
			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        
        ExcelExporter bt2 = new ExcelExporter(tb);
        bt2.setCaption("Gerar XLS");
        bt2.setDownloadFileName(relatorioPre.getNome_relatorio());
        bt2.setPrimaryStyleName("btSubMenu");
        bt2.setEnabled(true);
        
        l.addComponent(bt1);        
        l.addComponent(bt2);
       
    }

	public Button BuildbtImprimir(){
		btImprimir = new Button("Gerar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				 if (winSubMenuNovo != null && winSubMenuNovo.getUI() != null)
					 winSubMenuNovo.close();
	             else {
	            	 
	            	 	            	             	 
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


//	public Button BuildbtImprimir(){
//		btImprimir = new Button("Gerar PDF", new Button.ClickListener() {
//			
//			
//			public void buttonClick(ClickEvent event) {
//				
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Gerar PDF"))				
//				{
//
//				try {
//					
//					List<Object> columns = new ArrayList<Object>();
//					Object[] visible_columns = tb.getVisibleColumns();		
//					for(Object c:visible_columns){
//						if(!tb.isColumnCollapsed(c.toString())){					   	
//							columns.add(c);
//						}	
//					}
//
//					if(tfNomeRelatorio.getValue()!=null && !tfNomeRelatorio.getValue().equals("")){
//						nomeRelatorio = " - "+relatorioPre.getNome_relatorio()+".pdf";
//					}else{
//						nomeRelatorio = ".pdf";
//					}
//					// INSTANCIA UMA NOVA JANELA E ADICIONA SUAS PROPRIEDADES
//					Window win = new Window("Relatório de Pedido");
//					win.setWidth("800px");
//					win.setHeight("600px");
//					win.setResizable(true);
//					win.center();
//					win.setModal(true);
//					win.setStyleName("disable_scroolbar");
//					
//					StreamResource resource = new StreamResource(new ExportarRelatorioPedido(tipo,ordenacao,orientacao,resumo,listaParametros,columns), "RELATORIO DE PEDIDOS"+nomeRelatorio);
//					resource.getStream();
//					resource.setMIMEType("application/pdf");
//					resource.setCacheTime(0);
//					
//					Embedded e = new Embedded();
//					e.setSizeFull();
//					e.setType(Embedded.TYPE_BROWSER);
//					e.setSource(resource);
//					
//					win.setContent(e);
//					getUI().addWindow(win);
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//					System.out.println("Erro: "+e.getMessage());
//					System.out.println("Causado por: "+e.getCause());
//				}
//			}else{
//				Notification.show("Atenção", "Você não Possui Permissão para Gerar PDF", Type.ERROR_MESSAGE);	
//			}
//		}
//	});
//		
//		
//		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
//			
//			@Override
//			public void handleAction(Object sender, Object target) {
//				BuildbtImprimir().click();
//			}
//		};
//		
//		btImprimir.addShortcutListener(slbtOK);
//		btImprimir.setEnabled(false);
//		
//		return btImprimir;
//		
//	}

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}

	
	
}

