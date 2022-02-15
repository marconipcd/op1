package com.digital.opuserp.view.modulos.relatorio.Acesso;

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
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.RelatorioPre;
import com.digital.opuserp.domain.RelatorioPreColunas;
import com.digital.opuserp.domain.RelatorioPreFiltro;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.exporterpdf.ExcelExporter;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.digital.opuserp.view.modulos.relatorio.Acesso.NovoRelatorioAcesso.AddFiltroAcessoEvent;
import com.digital.opuserp.view.modulos.relatorio.Acesso.NovoRelatorioAcesso.RelatorioAcessoEvent;
import com.digital.opuserp.view.modulos.relatorio.Acesso.SalvarRelatorioAcessoEditor.SalvarRelatorioAcessoEvent;
import com.digital.opuserp.view.util.Notify;
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
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class RelatorioAcessoView extends VerticalLayout{
	
	private JPAContainer<AcessoCliente> container;	
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
	private Table tbRelatorios;
	private String nomeRelatorio;
	
	Window winRelatorios;
	
	private RelatorioPre relatorioPre;
	private List<RelatorioPreFiltro> filtrosRelatorio;
	private List<RelatorioPreColunas> colunasVisiveis;
	
	public RelatorioAcessoView(boolean act){		
		
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
		
		if(c instanceof RelatorioAcessoView)
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
	
	
	public JPAContainer<AcessoCliente> buildContainer(){
		container = JPAContainerFactory.makeBatchable(AcessoCliente.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("cliente.id");
		container.addNestedContainerProperty("cliente.email");
		
		container.addNestedContainerProperty("cliente.telefone1");
		container.addNestedContainerProperty("cliente.telefone2");
		container.addNestedContainerProperty("cliente.celular1");
		container.addNestedContainerProperty("cliente.celular2");
		
		container.addNestedContainerProperty("plano.nome");
		container.addNestedContainerProperty("base.identificacao");
		container.addNestedContainerProperty("swith.identificacao");
		container.addNestedContainerProperty("material.nome");				
		container.addNestedContainerProperty("contrato.nome");
		
		container.addNestedContainerProperty("endereco.cep");
		container.addNestedContainerProperty("endereco.endereco");
		container.addNestedContainerProperty("endereco.numero");
		container.addNestedContainerProperty("endereco.cidade");
		container.addNestedContainerProperty("endereco.bairro");
		container.addNestedContainerProperty("endereco.uf");
		container.addNestedContainerProperty("endereco.pais");
		container.addNestedContainerProperty("endereco.complemento");
		container.addNestedContainerProperty("endereco.referencia");
			
		return container;
	}

	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}			
				
				if(colId.equals("cliente.email") && tb.getItem(rowId).getItemProperty(colId).getValue() != null){										
						return tb.getItem(rowId).getItemProperty(colId).getValue().toString().toLowerCase();											
				}	
				
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		
		tb.setVisibleColumns(new Object[] {"id","cliente.nome_razao","cliente.telefone1","cliente.telefone2","cliente.celular1","cliente.celular2","cliente.email","plano.nome","contrato.nome","regime","base.identificacao","interfaces","signal_strength","swith.identificacao","material.nome",
				"login","senha","endereco_ip","endereco_mac","status_2","data_instalacao","data_alteracao_plano",
				"endereco.cep","endereco.endereco",
				"endereco.numero","endereco.cidade","endereco.bairro","endereco.uf","endereco.pais","endereco.complemento","endereco.referencia"});
		
		
		
		tb.setColumnCollapsible("cliente.nome_razao",false);	
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("cliente.email", "Email");
		tb.setColumnHeader("cliente.telefone1", "Telefone1");
		tb.setColumnHeader("cliente.telefone2", "Telefone2");
		tb.setColumnHeader("cliente.celular1", "Celular1");
		tb.setColumnHeader("cliente.celular2", "Celular2");
		tb.setColumnHeader("plano.nome", "Plano");
		tb.setColumnHeader("base.identificacao", "Concentrador");
		tb.setColumnHeader("interfaces", "Interface");
		tb.setColumnHeader("signal_strength", "Signal Strength");
		tb.setColumnHeader("swith.identificacao","Swith");
		tb.setColumnHeader("material.nome", "Material");
		tb.setColumnHeader("contrato.nome", "Contrato");
		tb.setColumnHeader("login", "Username");
		tb.setColumnHeader("senha", "Senha");
		tb.setColumnHeader("endereco_ip", "Endereço IP");
		tb.setColumnHeader("endereco_mac", "Endereço MAC");
		tb.setColumnHeader("status_2", "Status");
		tb.setColumnHeader("regime", "Regime");
		tb.setColumnHeader("data_instalacao", "Data Instalação");
		tb.setColumnHeader("data_alteracao_plano", "Data Alteração Plano");

		tb.setColumnHeader("endereco.cep", "CEP");
		tb.setColumnHeader("endereco.endereco", "Endereço");
		tb.setColumnHeader("endereco.numero", "Número");
		tb.setColumnHeader("endereco.cidade", "Cidade");
		tb.setColumnHeader("endereco.bairro", "Bairro");
		tb.setColumnHeader("endereco.uf", "UF");
		tb.setColumnHeader("endereco.pais", "País");
		tb.setColumnHeader("endereco.complemento", "Complemento");
		tb.setColumnHeader("endereco.referencia", "Referencia");
		
		tb.setColumnCollapsingAllowed(true);

		tb.setColumnCollapsed("endereco.cep", true);
		tb.setColumnCollapsed("endereco.endereco", true);
		tb.setColumnCollapsed("endereco.numero", true);
		tb.setColumnCollapsed("endereco.cidade", true);
		tb.setColumnCollapsed("endereco.bairro", true);
		tb.setColumnCollapsed("endereco.uf", true);
		tb.setColumnCollapsed("endereco.pais", true);
		tb.setColumnCollapsed("endereco.complemento",true);
		tb.setColumnCollapsed("endereco.referencia", true);
		
		//tb.setConverter("id", null);
		tb.setImmediate(true);
		
		tb.setColumnCollapsible("nome_razao", false);
		
		
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
		
		listaParametros = new ArrayList<>();
		Integer i = 1;
		for (RelatorioPreFiltro filtro : filtros) {
			
			
			String idSearch = String.valueOf(listaParametros.size())+filtro.getValor();
			listaParametros.add(new SearchParameters(idSearch, filtro.getColuna(), filtro.getOperador(), filtro.getValor()));
													
			adicionarFiltro();
			
								
			
			i++;
		}
		
	}
	
	public String selectFiltro(String s) {
		
		
		String filtro = "";
		if(s.equals("Código")){
			filtro = "id";			
		}else if(s.equals("Cliente")){
			filtro = "cliente.nome_razao";			
		}else if(s.equals("Celular1")){
			filtro = "cliente.celular1";			
		}else if(s.equals("Celular2")){
			filtro = "cliente.celular2";			
		}else if(s.equals("Telefone1")){
			filtro = "cliente.telefone1";			
		}else if(s.equals("Telefone2")){
			filtro = "cliente.telefone2";			
		}else if(s.equals("Plano")){
			filtro = "plano.nome";			
		}else if(s.equals("Concentrador")){
			filtro = "base.identificacao";	
		}else if(s.equals("Interface")){
			filtro = "interfaces";	
		}else if(s.equals("Signal Strength")){
			filtro = "signal_strength";			
		}else if(s.equals("Swith")){
			filtro = "swith.identificacao";			
		}else if(s.equals("Material")){
			filtro = "material.nome";			
		}else if(s.equals("Contrato")){
			filtro = "contrato.nome";			
		}else if(s.equals("Username")){
			filtro = "login";			
		}else if(s.equals("Senha")){
			filtro = "senha";			
		}else if(s.equals("Endereço IP")){
			filtro = "endereco_ip";			
		}else if(s.equals("Endereço MAC")){
			filtro = "endereco_mac";			
		}else if(s.equals("Status")){
			filtro = "status_2";			
		}else if(s.equals("Regime")){
			filtro = "regime";			
		}else if(s.equals("Data Instalação")){
			filtro = "data_instalacao";		
		}else if(s.equals("Data Alteração Plano")){
			filtro = "data_alteracao_plano";
		}else if(s.equals("CEP")){
			filtro = "endereco.cep";			
		}else if(s.equals("Endereço")){
			filtro = "endereco.endereco";			
		}else if(s.equals("Número")){
			filtro = "endereco.numero";			
		}else if(s.equals("Bairro")){
			filtro = "endereco.bairro";			
		}else if(s.equals("Cidade")){
			filtro = "endereco.cidade";			
		}else if(s.equals("Pais")){
			filtro = "endereco.pais";			
		}else if(s.equals("Complemento")){
			filtro = "endereco.complemento";			
		}else if(s.equals("Referencia")){
			filtro = "endereco.referencia";	
		}		
		return filtro;
	}
	
	public boolean adicionarFiltro(){
		boolean check = true;
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(ordenacao != null){
			container.sort(new Object[]{selectFiltro(ordenacao)}, new boolean[]{true});
		}
					
		
		for(SearchParameters sp:listaParametros){
			
				if(sp.getOperador().equals("IGUAL")){
							
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
											
						if(sp.getCampo().equals("endereco.cep") || sp.getCampo().equals("endereco.endereco") || sp.getCampo().equals("endereco.numero")
								|| sp.getCampo().equals("endereco.bairro") || sp.getCampo().equals("endereco.cidade") || sp.getCampo().equals("endereco.pais")
								|| sp.getCampo().equals("endereco.complemento") || sp.getCampo().equals("endereco.referencia")||sp.getCampo().equals("categoria.nome")){
							
							container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), false));
						}else{
							container.addContainerFilter(new Equal(sp.getCampo(), sp.getValor()));
						}

						if(sp.getCampo().equals("cliente.nome_razao") || sp.getCampo().equals("plano.nome") || sp.getCampo().equals("base.identificacao")
								|| sp.getCampo().equals("swith.identificacao") || sp.getCampo().equals("material.nome") || sp.getCampo().equals("contrato.nome")){
							
							container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), false));
						}else{	
							container.addContainerFilter(new Equal(sp.getCampo(), sp.getValor()));
						}
	
						
					}else if(container.getType(sp.getCampo()) == Date.class){
						String date = sp.getValor();
						Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3)+date.substring(6,10)));
						container.addContainerFilter(new Equal(sp.getCampo(), dtValor));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
					}
					
				}else if(sp.getOperador().equals("DIFERENTE")){
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
								
						
						if(sp.getCampo().equals("endereco.cep") || sp.getCampo().equals("endereco.endereco") || sp.getCampo().equals("endereco.numero")
								|| sp.getCampo().equals("endereco.bairro") || sp.getCampo().equals("endereco.cidade") || sp.getCampo().equals("endereco.pais")
								|| sp.getCampo().equals("endereco.complemento") || sp.getCampo().equals("endereco.referencia")||sp.getCampo().equals("categoria.nome")){
							
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(), sp.getValor(), false)));
						}else{
							container.addContainerFilter(Filters.not(new Equal(sp.getCampo(), sp.getValor())));
						}
						
						
						if(sp.getCampo().equals("cliente.nome_razao") || sp.getCampo().equals("plano.nome") || sp.getCampo().equals("base.identificacao")
								|| sp.getCampo().equals("swith.identificacao") || sp.getCampo().equals("material.nome") || sp.getCampo().equals("contrato.nome")){
							
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(), sp.getValor(), false)));
						}else{
							container.addContainerFilter(Filters.not(new Equal(sp.getCampo(), sp.getValor())));
						}
						
						
						
						
					}else if(container.getType(sp.getCampo()) == Date.class){
						String date = sp.getValor();
						Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3)+date.substring(6,10)));
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), dtValor)));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), Integer.parseInt(sp.getValor()))));
					}
					
				}else if(sp.getOperador().equals("CONTEM")){
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
						container.addContainerFilter(new Like(sp.getCampo(),"%"+sp.getValor()+"%", false));
					}
					
				}else if(sp.getOperador().equals("NAO CONTEM")){
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
						container.addContainerFilter(Filters.not(new Like(sp.getCampo(),"%"+sp.getValor()+"%", false)));			
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
							e.printStackTrace();
						}					
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new Greater(sp.getCampo(), Integer.parseInt(sp.getValor())));					
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
							e.printStackTrace();
						}
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new Less(sp.getCampo(), Integer.parseInt(sp.getValor())));					
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
							e.printStackTrace();
						}						
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), Integer.parseInt(sp.getValor())));					
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
							e.printStackTrace();
						}
						
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new LessOrEqual(sp.getCampo(), Integer.parseInt(sp.getValor())));					
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
				
						NovoRelatorioAcesso v = new NovoRelatorioAcesso("Editar Relatório de Acesso", true,tipo, orientacao, ordenacao,resumo, listaParametros );
						v.addListerner(new NovoRelatorioAcesso.RelatorioAcessoListerner() {
							
							@Override
							public void onClose(RelatorioAcessoEvent event) {
								if(event.isConfirm()){
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
									relatoriDAO.addRelatorio(relatorioPre, filtrosRelatorio,colunasVisiveis);
									

									Notify.Show("Relatório Salvo com Sucesso", Notify.TYPE_SUCCESS);
									
									btSalvar.setEnabled(false);
									btImprimir.setEnabled(true);
									btGerarChart.setEnabled(true);
								}
			 				}
						});
						
						v.addListerner(new NovoRelatorioAcesso.AddFiltroAcessoListerner() {
							
							@Override
							public void onClose(AddFiltroAcessoEvent event) {
								listaParametros = event.getFiltros();
								adicionarFiltro();
							}
						});
						
						
						getUI().addWindow(v);
				
				}else{
					Notify.Show("Você precisa Carregar um Relatório Salvo para Editar Filtros!", Notify.TYPE_WARNING);
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
				
				NovoRelatorioAcesso v = new NovoRelatorioAcesso("Novo Relatório de Acesso", true);
				v.addListerner(new NovoRelatorioAcesso.RelatorioAcessoListerner() {
					
					@Override
					public void onClose(RelatorioAcessoEvent event) {
						if(event.isConfirm()){
							listaParametros = event.getParametros();
							tipo = event.getTipo();
							ordenacao = event.getOrdenacao();
							orientacao = event.getOrientacao();
							resumo = event.getResumo();
							
							adicionarFiltro();
							btSalvar.setEnabled(true);
							btImprimir.setEnabled(true);
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
							//btImprimir.setEnabled(false);
						}
	 				}
				});
				
				v.addListerner(new NovoRelatorioAcesso.AddFiltroAcessoListerner() {
					
					@Override
					public void onClose(AddFiltroAcessoEvent event) {
						listaParametros = event.getFiltros();
						adicionarFiltro();
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
				
				final SalvarRelatorioAcessoEditor salvarEditor = new SalvarRelatorioAcessoEditor(codSubModulo,"Salvar Relatório", true);
				salvarEditor.addListerner(new SalvarRelatorioAcessoEditor.SalvarRelatorioAcessoListerner() {
					
					@Override
					public void onClose(SalvarRelatorioAcessoEvent event) {
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
							

							Notify.Show("Relatório Salvo com Sucesso !", Notify.TYPE_SUCCESS);
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
														Notify.Show("Relatório foi Excluído com Sucesso!", Notify.TYPE_SUCCESS);

														
														if(relatoriosContainer != null){
															refresh();
															relatoriosContainer.refresh();
														}
													}else{
														Notification.show("");
														Notify.Show("Não foi Possivel Realizar a Exclusão do Relatório !", Notify.TYPE_ERROR);

														
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
						win.setResizable(false);
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
				        
				        for(AcessoCliente c:(List<AcessoCliente>) qGroup.getResultList())
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
					Notify.Show("Você não Possui Permissão para Gerar Gráfico !", Notify.TYPE_ERROR);
				}
			}
		});
		
		btGerarChart.setEnabled(false);
		
		return btGerarChart;
	}
	
	
	
	
	
	private TypedQuery<AcessoCliente> gerarDadosGRaficos(){
		
		EntityManager em = ConnUtil.getEntity();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AcessoCliente> criteriaQuery = cb.createQuery(AcessoCliente.class);
		Root<AcessoCliente> rootCliente = criteriaQuery.from(AcessoCliente.class);
		EntityType<AcessoCliente> type = em.getMetamodel().entity(AcessoCliente.class);

		List<Predicate> criteria = new ArrayList<Predicate>();

		criteria.add(cb.equal(rootCliente.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

		if (listaParametros.size() > 0) {
			for (SearchParameters s : listaParametros) {

				if (s.getOperador().equals("IGUAL")) {
					if (s.getCampo().equals("cliente.nome_razao")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
					}

					if (s.getCampo().equals("plano.nome")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("plano").<String>get("nome")), s.getValor().toLowerCase()));
					}
					
					if (s.getCampo().equals("base.identificacao")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("base").<String>get("identificacao")), s.getValor().toLowerCase()));
					}
					
					if (s.getCampo().equals("swith.identificacao")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("swith").<String>get("identificacao")), s.getValor().toLowerCase()));
					}
					
					if (s.getCampo().equals("material.nome")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("material").<String>get("nome")), s.getValor().toLowerCase()));
					}
					
					if (s.getCampo().equals("contrato.nome")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("contrato").<String>get("nome")), s.getValor().toLowerCase()));
					}
					
					//enderecos
					if (s.getCampo().equals("endereco.cep")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco").<String>get("cep")), s.getValor().toLowerCase()));
					}						
					if (s.getCampo().equals("endereco.numero")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco").<String>get("numero")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.endereco")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco").<String>get("endereco")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.bairro")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco").<String>get("bairro")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.cidade")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco").<String>get("cidade")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.uf")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco").<String>get("uf")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.pais")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco").<String>get("pais")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.complemento")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco").<String>get("complemento")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.referencia")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco").<String>get("referencia")), s.getValor().toLowerCase()));
					}

					if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("plano.nome") && 
							!s.getCampo().equals("base.identificacao") && !s.getCampo().equals("swith.identificacao") &&
							!s.getCampo().equals("material.nome") && !s.getCampo().equals("contrato.nome")&&
							!s.getCampo().equals("endereco.cep") && !s.getCampo().equals("endereco.numero") &&
							!s.getCampo().equals("endereco.endereco") && !s.getCampo().equals("endereco.bairro") &&
							!s.getCampo().equals("endereco.uf") && !s.getCampo().equals("endereco.cidade") && 
							!s.getCampo().equals("endereco.pais") && !s.getCampo().equals("endereco.complemento") &&
							!s.getCampo().equals("endereco.referencia")) {
						criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
					}
					
				}else if (s.getOperador().equals("DIFERENTE")) {
					if (s.getCampo().equals("cliente.nome_razao")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
					}

					if (s.getCampo().equals("plano.nome")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("plano").<String>get("nome")),s.getValor().toLowerCase()));
					}
					
					if (s.getCampo().equals("base.identificacao")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("base").<String>get("identificacao")),s.getValor().toLowerCase()));
					}
					
					if (s.getCampo().equals("swith.identificacao")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("swith").<String>get("identificacao")),s.getValor().toLowerCase()));
					}
					
					if (s.getCampo().equals("material.nome")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("material").<String>get("nome")),s.getValor().toLowerCase()));
					}
					
					if (s.getCampo().equals("contrato.nome")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("contrato").<String>get("nome")),s.getValor().toLowerCase()));
					}
					
					//enderecos
					if (s.getCampo().equals("endereco.cep")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("cep")),  s.getValor().toLowerCase()));
					}						
					if (s.getCampo().equals("endereco.numero")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("numero")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.endereco")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("endereco")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.bairro")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("bairro")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.cidade")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("cidade")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.uf")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("uf")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.pais")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("pais")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.complemento")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("complemento")),  s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco.referencia")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("referencia")), s.getValor().toLowerCase()));
					}
					
					if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("plano.nome") && 
							!s.getCampo().equals("base.identificacao") && !s.getCampo().equals("swith.identificacao") &&
							!s.getCampo().equals("material.nome") && !s.getCampo().equals("contrato.nome")&&
							!s.getCampo().equals("endereco.cep") && !s.getCampo().equals("endereco.numero") &&
							!s.getCampo().equals("endereco.endereco") && !s.getCampo().equals("endereco.bairro") &&
							!s.getCampo().equals("endereco.uf") && !s.getCampo().equals("endereco.cidade") && 
							!s.getCampo().equals("endereco.pais") && !s.getCampo().equals("endereco.complemento") &&
							!s.getCampo().equals("endereco.referencia")) {
						criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
					}
					

				} else if (s.getOperador().equals("CONTEM")) {
					
					if (s.getCampo().equals("cliente.nome_razao")) {
						criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")),"%" + s.getValor().toLowerCase()+ "%"));							
					}

					if (s.getCampo().equals("plano.nome")) {
						criteria.add(cb.like(cb.lower(rootCliente.get("plano").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (s.getCampo().equals("base.identificacao")) {
						criteria.add(cb.like(cb.lower(rootCliente.get("base").<String>get("identificacao")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (s.getCampo().equals("swith.identificacao")) {
						criteria.add(cb.like(cb.lower(rootCliente.get("swith").<String>get("identificacao")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (s.getCampo().equals("material.nome")) {
						criteria.add(cb.like(cb.lower(rootCliente.get("material").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (s.getCampo().equals("contrato.nome")) {
						criteria.add(cb.like(cb.lower(rootCliente.get("contrato").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					//enderecos
					if (s.getCampo().equals("endereco.cep")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco").<String>get("cep")), "%" + s.getValor().toLowerCase()+ "%"));
					}						
					if (s.getCampo().equals("endereco.numero")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco").<String>get("numero")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.endereco")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.bairro")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.cidade")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.uf")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.pais")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco").<String>get("pais")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.complemento")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco").<String>get("complemento")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.referencia")) {
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
					}
				
					if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("plano.nome") && 
							!s.getCampo().equals("base.identificacao") && !s.getCampo().equals("swith.identificacao") &&
							!s.getCampo().equals("material.nome") && !s.getCampo().equals("contrato.nome")&&
							!s.getCampo().equals("endereco.cep") && !s.getCampo().equals("endereco.numero") &&
							!s.getCampo().equals("endereco.endereco") && !s.getCampo().equals("endereco.bairro") &&
							!s.getCampo().equals("endereco.uf") && !s.getCampo().equals("endereco.cidade") && 
							!s.getCampo().equals("endereco.pais") && !s.getCampo().equals("endereco.complemento") &&
							!s.getCampo().equals("endereco.referencia")) {
						criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
					}
					
					
				} else if (s.getOperador().equals("NAO CONTEM")) {
					
					if (s.getCampo().equals("cliente.nome_razao")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")),"%" + s.getValor().toLowerCase()+ "%"));
					}

					if (s.getCampo().equals("plano.nome")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("plano").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (s.getCampo().equals("base.identificacao")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("base").<String>get("identificacao")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (s.getCampo().equals("swith.identificacao")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("swith").<String>get("identificacao")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (s.getCampo().equals("material.nome")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("material").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (s.getCampo().equals("contrato.nome")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("contrato").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					//enderecos
					if (s.getCampo().equals("endereco.cep")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("cep")), "%" + s.getValor().toLowerCase()+ "%"));
					}						
					if (s.getCampo().equals("endereco.numero")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("numero")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.endereco")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.cidade")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.uf")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.pais")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("pais")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.complemento")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("complemento")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco.referencia")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("plano.nome") && 
							!s.getCampo().equals("base.identificacao") && !s.getCampo().equals("swith.identificacao") &&
							!s.getCampo().equals("material.nome") && !s.getCampo().equals("contrato.nome")&&
							!s.getCampo().equals("endereco.cep") && !s.getCampo().equals("endereco.numero") &&
							!s.getCampo().equals("endereco.endereco") && !s.getCampo().equals("endereco.bairro") &&
							!s.getCampo().equals("endereco.uf") && !s.getCampo().equals("endereco.cidade") && 
							!s.getCampo().equals("endereco.pais") && !s.getCampo().equals("endereco.complemento") &&
							!s.getCampo().equals("endereco.referencia")) {
						criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
					}
				
				} else if (s.getOperador().equals("MAIOR QUE")) {
					
					try{						
						if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
							criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
						}
						
						if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
							String date = s.getValor();
							Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3) + date.substring(6, 10)));
							criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Date> get(s.getCampo()), dtValor));
						}
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					
					
				} else if (s.getOperador().equals("MENOR QUE")) {
					
					
					
					try{						
						if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
							criteria.add(cb.lessThanOrEqualTo(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
						}
						
						if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
							String date = s.getValor();
							Date dtValor = new Date(Date.parse(date.substring(3, 6)+ date.substring(0, 3) + date.substring(6, 10)));
							criteria.add(cb.lessThanOrEqualTo(rootCliente.<Date> get(s.getCampo()), dtValor));
						}
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					
					
					
				} else if (s.getOperador().equals("MAIOR IGUAL QUE")) {
					
					
					try{						
						if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
							criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
						}
						
						if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
							String date = s.getValor();
							Date dtValor = new Date(Date.parse(date.substring(3, 6)+ date.substring(0, 3) + date.substring(6, 10)));
							criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Date> get(s.getCampo()), dtValor));
						}
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					
					
				} else if (s.getOperador().equals("MENOR IGUAL QUE")) {
					
					try{						
						if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
							criteria.add(cb.lessThanOrEqualTo(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
						}
						
						if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
							String date = s.getValor();
							Date dtValor = new Date(Date.parse(date.substring(3, 6)	+ date.substring(0, 3) + date.substring(6, 10)));
							criteria.add(cb.lessThanOrEqualTo(rootCliente.<Date> get(s.getCampo()), dtValor));
						}
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
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
        
        
		CriteriaQuery<AcessoCliente> criteriaQueryGroup = cb.createQuery(AcessoCliente.class);
		Root<AcessoCliente> rootGroup = criteriaQueryGroup.from(AcessoCliente.class);
		
		
		if (selectFiltro(resumo).equals("cliente.nome_razao")) {
			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("nome_razao");
			
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("nome_razao"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}

		if (selectFiltro(resumo).equals("plano.nome")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("plano").get("nome");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("plano").get("nome"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("base.identificacao")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("base").get("identificacao");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("base").get("identificacao"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("swith.identificacao")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("swith").get("identificacao");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("swith").get("identificacao"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("material.nome")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("material").get("nome");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("material").get("nome"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("contrato.nome")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("contrato").get("nome");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("contrato").get("nome"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}	
		
		
		if (selectFiltro(resumo).equals("endereco.referencia")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco").get("referencia");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("referencia"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("endereco.complemento")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco").get("complemento");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("complemento"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("endereco.pais")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco").get("pais");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("pais"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("endereco.cidade")) {
			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco").get("cidade");
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("cidade"));			
			
			
			if (criteria.size() == 0) {
				throw new RuntimeException("no criteria");
			} else if (criteria.size() == 1) {					
				criteriaQueryGroup.where(cb.and(criteria.get(0)));
			} else {					
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
			}
		
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			
		}
		
		if (selectFiltro(resumo).equals("endereco.uf")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco").get("uf");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("uf"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("endereco.bairro")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco").get("bairro");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("bairro"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("endereco.endereco")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco").get("endereco");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("endereco"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("endereco.numero")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco").get("numero");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("numero"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("endereco.cep")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco").get("cep");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("cep"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
		}

		if (!selectFiltro(resumo).equals("cliente.nome_razao") && !selectFiltro(resumo).equals("plano.nome") && 
			!selectFiltro(resumo).equals("base.identificacao") && !selectFiltro(resumo).equals("swith.identificacao") &&
			!selectFiltro(resumo).equals("material.nome") && !selectFiltro(resumo).equals("contrato.nome") &&
			
			!selectFiltro(resumo).equals("endereco.cep") && !selectFiltro(resumo).equals("endereco.numero") &&
			!selectFiltro(resumo).equals("endereco.endereco") && !selectFiltro(resumo).equals("endereco.bairro") &&
			!selectFiltro(resumo).equals("endereco.uf") && !selectFiltro(resumo).equals("endereco.cidade")&&			
			!selectFiltro(resumo).equals("endereco.pais") && !selectFiltro(resumo).equals("endereco.complemento")&&
			!selectFiltro(resumo).equals("endereco.referencia")) {
			
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
			
		
			//criteriaQueryGroup.where(cb.not(cb.equal(rootGroup.join("endereco").get("cidade"), "BELO JARDIM")));
			criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			
			
		}
		return em.createQuery(criteriaQueryGroup);	
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
						Window win = new Window("Relatório de Acesso");
						win.setWidth("800px");
						win.setHeight("600px");
						win.setResizable(true);
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
						
						StreamResource resource = new StreamResource(new ExportAcesso(tipo,ordenacao,orientacao,resumo,listaParametros,columns), "RELATORIO DE ACESSO"+nomeRelatorio);
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
					Notify.Show("Você não Possui Permissão para Gerar PDF !", Notify.TYPE_ERROR);
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



	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}

	

	
}
