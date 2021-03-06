package com.digital.opuserp.view.modulos.relatorio.Consumo;

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

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.RelatorioDAO;
import com.digital.opuserp.domain.RadAcct;
import com.digital.opuserp.domain.RelatorioPre;
import com.digital.opuserp.domain.RelatorioPreColunas;
import com.digital.opuserp.domain.RelatorioPreFiltro;
import com.digital.opuserp.util.ByteUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.exporterpdf.ExcelExporter;
import com.digital.opuserp.util.TimeUtil;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.digital.opuserp.view.modulos.relatorio.Consumo.NovoRelatorioConsumo.AddFiltroConsumoEvent;
import com.digital.opuserp.view.modulos.relatorio.Consumo.NovoRelatorioConsumo.RelatorioConsumoEvent;
import com.digital.opuserp.view.modulos.relatorio.Consumo.SalvarRelatorioConsumoEditor.SalvarRelatorioConsumoEvent;
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

public class RelatorioConsumoView extends VerticalLayout{
	
	private JPAContainer<RadAcct> container;	
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
//	private List<MovimentoEntDetalhe> resultDetalhe;
	
	public RelatorioConsumoView(boolean act){		
		
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
		
		if(c instanceof RelatorioConsumoView)
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

	DateTime data = new DateTime(new Date()).minusMonths(3);	
	Date dt = data.toDate();
	
	public JPAContainer<RadAcct> buildJpaContainer(){
		container = JPAContainerFactory.make(RadAcct.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.not(new Equal("acctstoptime",(null))));
		container.sort(new String[]{"radacctid"}, new boolean[]{false});
		container.applyFilters();
		return container;
	}

	
	public Table buildTbGeneric(){
		tb = new Table(null, buildJpaContainer());
		tb.setWidth("100%");
		tb.setHeight("100%");
//		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		
		tb.setColumnCollapsible("radacctid",false);	
		tb.setColumnHeader("radacctid", "C??digo");
		tb.setColumnHeader("username", "Login");
		tb.setColumnHeader("acctstarttime", "Data Inicio");
		tb.setColumnHeader("acctstoptime", "Data Final");
		tb.setColumnHeader("acctsessiontime", "Tempo Total");
		tb.setColumnHeader("acctinputoctets", "Upload");
		tb.setColumnHeader("acctoutputoctets", "Download");
		tb.setColumnHeader("acctterminatecause", "Causa do T??rmino");		
		tb.setColumnHeader("framedipaddress", "Ip");		
		
		
		tb.addGeneratedColumn("acctinputoctets", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				String valor = ByteUtil.humanReadableByteCount((long)source.getItem(itemId).getItemProperty("acctinputoctets").getValue(), true);				
				return valor;
			}
		});
		
		tb.addGeneratedColumn("acctoutputoctets", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				String valor = ByteUtil.humanReadableByteCount((long)source.getItem(itemId).getItemProperty("acctoutputoctets").getValue(), true);				
				return valor;
			}
		});
		
		
		tb.addGeneratedColumn("acctsessiontime", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				Date valorInicial = (Date)source.getItem(itemId).getItemProperty("acctstarttime").getValue();

				Date dt2 = new Date();
				long Second = (dt2.getTime()-valorInicial.getTime()) / 1000;
				
				Integer segundos = Integer.parseInt(String.valueOf(Second));
				
				String valor = "";
				valor = TimeUtil.formataTempo((Integer)source.getItem(itemId).getItemProperty("acctsessiontime").getValue());					
				
//				if(valor.equals("0d 0h 0min")){
//					valor = TimeUtil.formataTempo(segundos);
//				}
				return valor;
			}
		});
		
		
//		tb.addGeneratedColumn("acctterminatecause", new Table.ColumnGenerator() {
//			
//			@Override
//			public Object generateCell(Table source, Object itemId, Object columnId) {
//				
//				
//				String valor = source.getItem(itemId).getItemProperty("acctterminatecause").getValue().toString();
//				
//				if(valor.equals("User-Request")){
//					valor = "Usu??rio Requisitou";
//				}else if(valor.equals("Lost-Carrier")){
//					valor = "Conex??o Perdida";
//				}else if(valor.equals("Lost-Service")){
//					valor = "Servi??o foi Perdido";
//				}else if(valor.equals("Idle-Timeout")){
//					valor = "Tempo de Ociosidade Esgotou";
//				}else if(valor.equals("Session-Timeout")){
//					valor = "Tempo de Sess??o Esgotou";
//				}else if(valor.equals("Admin-Reset")){
//					valor = "Resetado pelo Admin";
//				}else if(valor.equals("Admin-Reboot")){
//					valor = "Reiniciado pelo Admin";
//				}else if(valor.equals("Port-Error")){
//					valor = "Erro na Porta";
//				}else if(valor.equals("NAS-Error")){
//					valor = "Erro no Concentrador";
//				}else if(valor.equals("NAS-Request")){
//					valor = "Requisitado no Contrador";
//				}else if(valor.equals("NAS-Reboot")){
//					valor = "Reiniciado pelo Concentrador";
//				}else if(valor.equals("Port-Unneeded")){
//					valor = "Porta Desnecess??ria";
//				}else if(valor.equals("Port-Preempted")){
//					valor = "Porta Interrompida";
//				}else if(valor.equals("Port-Suspended")){
//					valor = "Porta Suspensa";
//				}else if(valor.equals("Service-Unavailable")){
//					valor = "Porta n??o Dispon??vel";
//				}else if(valor.equals("Callback")){
//					valor = "Opus Callback";
//				}else if(valor.equals("User-Error")){
//					valor = "Erro do Usu??rio";
//				}else if(valor.equals("Host-Request")){
//					valor = "Requisitado pelo Host";
//				}
//				return valor;
//			}
//		});
		
		
		
		tb.setVisibleColumns(new Object[]{"radacctid","username","acctstarttime","acctstoptime","acctsessiontime","acctinputoctets","acctoutputoctets","acctterminatecause","framedipaddress"});
		
		
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
//			tb.setColumnCollapsed(ob.toString(), true);
		}
		
		
		for (RelatorioPreColunas relatorioPreColunas : colunas) {
			tb.setColumnCollapsible(relatorioPreColunas.getColuna(), true);
//			tb.setColumnCollapsed(relatorioPreColunas.getColuna(), false);
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
		
		
		if(s.equals("C??digo")){
			filtro = "radacctid";												
		}else if(s.equals("Login")){
			filtro = "username";					
		}else if(s.equals("Data Inicio")){
			filtro = "acctstarttime";					
		}else if(s.equals("Data Final")){
			filtro = "acctstoptime";					
		}else if(s.equals("Tempo Total")){
			filtro = "acctsessiontime";					
		}else if(s.equals("Upload")){
			filtro = "acctinputoctets";					
		}else if(s.equals("Download")){
			filtro = "acctoutputoctets";		
		}else if(s.equals("Causa do T??rmino")){
			filtro = "acctterminatecause";		
		}else if(s.equals("Ip")){
			filtro = "framedipaddress";		
		}
		return filtro;		
	}
	
	public boolean adicionarFiltro() throws UnsupportedFilterException, ParseException{
		boolean check = true;
		container.removeAllContainerFilters();
//		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));

		if(ordenacao != null && !ordenacao.equals("Bairro") && !ordenacao.equals("Cidade") && 
		   !ordenacao.equals("Endere??o") && !ordenacao.equals("UF")){
			
			container.sort(new Object[]{selectFiltro(ordenacao)}, new boolean[]{true});
		}

					
		
		for(SearchParameters sp:listaParametros){
			
				if(sp.getOperador().equals("IGUAL")){

					
					if(container.getType(sp.getCampo()) == String.class){
						
						
						check = true;
						
						if(sp.getCampo().equals("fornecedor.razao_social")){
				
							container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), false));					
						}else{
							
							if(sp.getCampo().equals("situacao")){								
								container.addContainerFilter(new Equal(sp.getCampo(),(sp.getValor().substring(0, 1))));							
							}else{							
								container.addContainerFilter(new Equal(sp.getCampo(),(sp.getValor())));
							}
						}
	
					}else if(container.getType(sp.getCampo()) == Date.class){
						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						container.addContainerFilter(new Equal(sp.getCampo(), sdf.parse(sp.getValor())));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
						
					}else if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(new Equal(sp.getCampo(), Long.parseLong(valor.toString())));					
					}
					
				}else if(sp.getOperador().equals("DIFERENTE")){
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
							
						
						if(sp.getCampo().equals("fornecedor.razao_social")){
							
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(),sp.getValor(),false)));
						}else{
							if(sp.getCampo().equals("situacao")){								
								container.addContainerFilter(Filters.not(new Equal(sp.getCampo(),(sp.getValor().substring(0, 1)))));							
							}else{							
								container.addContainerFilter(Filters.not(new Equal(sp.getCampo(),(sp.getValor()))));
							}
						}
								
					}else if(container.getType(sp.getCampo()) == Date.class){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), sdf.parseObject(sp.getValor()))));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), Integer.parseInt(sp.getValor()))));
						
					}else if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), Long.parseLong(valor.toString()))));					
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
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
						
					if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(new Greater(sp.getCampo(), Long.parseLong(valor.toString())));					
					}
					
				}else if(sp.getOperador().equals("MENOR QUE")){
					
										
					if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							check = true;						
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
					if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(new Less(sp.getCampo(), Long.parseLong(valor.toString())));					
					}
				}else if(sp.getOperador().equals("MAIOR IGUAL QUE")){
										
					if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							check = true;						
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
					if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), Long.parseLong(valor.toString())));					
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
					if(container.getType(sp.getCampo()) == Long.class){
						Integer valor = Integer.parseInt(sp.getValor().toString());
						valor = valor*1024;
						container.addContainerFilter(new LessOrEqual(sp.getCampo(), Long.parseLong(valor.toString())));					
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
				
						NovoRelatorioConsumo v = new NovoRelatorioConsumo("Editar Relat??rio Consumo", true,tipo, orientacao, ordenacao,resumo, listaParametros );
//						v.addListerner(new NovoRelatorioConsumo.RelatorioConsumoListerner() {
						v.addListerner(new NovoRelatorioConsumo.RelatorioConsumoListerner() {
							
							@Override
							public void onClose(RelatorioConsumoEvent event) {
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
										
											Notification.show("Relat??rio Salvo com Sucesso", Type.ERROR_MESSAGE);
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

						v.addListerner(new NovoRelatorioConsumo.AddFiltroConsumoListerner() {
							
							@Override
							public void onClose(AddFiltroConsumoEvent event) {
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
					Notification.show("Voc?? precisa Carregar um Relat??rio Salvo para Editar Filtros!", Type.ERROR_MESSAGE);
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
//				container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
				container.addContainerFilter(Filters.not(new Equal("acctstoptime",(null))));

				container.applyFilters();
				
				hlFloat.replaceComponent(lbRegistros, lbRegistros());
				
				NovoRelatorioConsumo v = new NovoRelatorioConsumo("Novo Relat??rio de Compra", true);
				v.addListerner(new NovoRelatorioConsumo.RelatorioConsumoListerner() {
					
					@Override
					public void onClose(RelatorioConsumoEvent event) {
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
				
				v.addListerner(new NovoRelatorioConsumo.AddFiltroConsumoListerner() {
					
					@Override
					public void onClose(AddFiltroConsumoEvent event) {
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
								
				final SalvarRelatorioConsumoEditor salvarEditor = new SalvarRelatorioConsumoEditor(codSubModulo,"Salvar Relat??rio", true);
				salvarEditor.addListerner(new SalvarRelatorioConsumoEditor.SalvarRelatorioConsumoListerner() {
					
					@Override
					public void onClose(SalvarRelatorioConsumoEvent event) {
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
							
							Notification.show("Relat??rio Salvo com Sucesso", Type.ERROR_MESSAGE);
							btSalvar.setEnabled(false);
						}else{					
								
//							getUI().addWindow(salvarEditor);
						//	Notification.show("Informe um Nome para o Relat??rio e Parametros de Busca", Type.ERROR_MESSAGE);
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
								
				winRelatorios = new Window("Relat??rios Salvos");
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
									//Chamar Fun????o de Montar
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
						tbRelatorios.setColumnHeader("data_cadastro", "Data de Cria????o");
						tbRelatorios.setColumnHeader("codUsuario.username", "Autor");
						tbRelatorios.setColumnHeader("tipo_relatorio", "Tipo");
						tbRelatorios.setColumnHeader("ordenacao_relatorio", "Ordena????o");
						tbRelatorios.setColumnHeader("orientacao", "Orienta????o");
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
										GenericDialog gd = new GenericDialog("Confirme para Continuar", "Voc?? deseja realmente Excluir este Relat??rio?", true,true);
										gd.addListerner(new GenericDialog.DialogListerner() {
											
											@Override
											public void onClose(DialogEvent event) {
												if(event.isConfirm()){		
													Integer codRelatorio  = (Integer)tbRelatorios.getItem(itemId).getItemProperty("id").getValue();
													RelatorioDAO relatorioDAO = new RelatorioDAO();
													boolean check = relatorioDAO.apagarRelatorio(codRelatorio);
													
													if(check){
														Notification.show("Relat??rio foi Exclu??do com Sucesso!");
														
														if(relatoriosContainer != null){
															refresh();
															relatoriosContainer.refresh();
														}
													}else{
														Notification.show("N??o foi Possivel Realizar a Exclus??o do Relat??rio");
													}										
												}
											}
										});
										
										getUI().addWindow(gd);
									}
								});
								btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
								btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
								btDeletar.setDescription("Deletar Relat??rio");
								
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
		btGerarChart = new Button("Gerar Gr??fico", new Button.ClickListener() {
			
			
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
						Window win = new Window("Gr??fico de Resumo");
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
				        
				        for(RadAcct c:(List<RadAcct>) qGroup.getResultList())
				        {       	
				        	seriesColum.add(new ListSeries(c.getColuna(),c.getColuna_long()));
				        	seriesPie.add(new DataSeriesItem(c.getColuna(), c.getColuna_long()));
				        	
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
					Notification.show("Aten????o", "Voc?? n??o Possui Permiss??o para Gerar Gr??fico", Type.ERROR_MESSAGE);	
				}
			}
		});
		
		btGerarChart.setEnabled(false);
		
		return btGerarChart;
	}
	
	private TypedQuery<RadAcct> gerarDadosGRaficos(){
		
		EntityManager em = ConnUtil.getEntity();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RadAcct> criteriaQuery = cb.createQuery(RadAcct.class);
		Root<RadAcct> rootContas = criteriaQuery.from(RadAcct.class);
		EntityType<RadAcct> type = em.getMetamodel().entity(RadAcct.class);

		List<Predicate> criteria = new ArrayList<Predicate>();

//		criteria.add(cb.equal(rootContas.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

		if (listaParametros.size() > 0) {
			
			try{
				for (SearchParameters s : listaParametros) {

					if (s.getOperador().equals("IGUAL")) {
//						if (s.getCampo().equals("fornecedor.razao_social")) {
//							criteria.add(cb.like(cb.lower(rootContas.get("fornecedor").<String>get("razao_social")), s.getValor().toLowerCase()));
//						}					
//						
//						if (!s.getCampo().equals("fornecedor.razao_social")) {
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								criteria.add(cb.equal(rootContas.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.like(cb.lower(rootContas.<String>get(s.getCampo())), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.equal(rootContas.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Long.class)){
								criteria.add(cb.equal(rootContas.<Long>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
//						}

					}else if (s.getOperador().equals("DIFERENTE")) {
//						if (s.getCampo().equals("fornecedor.razao_social")) {
//							criteria.add(cb.notLike(cb.lower(rootContas.get("fornecedor").<String>get("razao_social")), s.getValor().toLowerCase()));
//						}					
									
//						if (!s.getCampo().equals("fornecedor.razao_social")) {							
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								criteria.add(cb.notEqual(rootContas.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.notLike(cb.lower(rootContas.<String> get(s.getCampo())), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.notEqual(rootContas.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Long.class)){
								criteria.add(cb.equal(rootContas.<Long>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
//						}

					} else if (s.getOperador().equals("CONTEM")) {
						
//						if (s.getCampo().equals("fornecedor.razao_social")) {
//							criteria.add(cb.like(cb.lower(rootContas.get("fornecedor").<String>get("razao_social")), "%" + s.getValor().toLowerCase()+ "%"));
//						}					
																		
//						if (!s.getCampo().equals("fornecedor.razao_social")) {
							
							criteria.add(cb.like(cb.lower(rootContas.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
//						}
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						
//						if (s.getCampo().equals("fornecedor.razao_social")) {
//							criteria.add(cb.notLike(cb.lower(rootContas.get("fornecedor").<String>get("razao_social")), "%" + s.getValor().toLowerCase()+ "%"));
//						}					
						
//						if (!s.getCampo().equals("fornecedor.razao_social")) {
							
							criteria.add(cb.like(cb.lower(rootContas.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
//						}
						
					} else if (s.getOperador().equals("MAIOR QUE")) {

							try{						
								if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThan(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Long.class)){
									criteria.add(cb.lessThan(rootContas.<Long> get(s.getCampo()), Long.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){								
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Long.class)){
									criteria.add(cb.lessThan(rootContas.<Long> get(s.getCampo()), Long.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Long.class)){
									criteria.add(cb.lessThan(rootContas.<Long> get(s.getCampo()), Long.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Long.class)){
									criteria.add(cb.lessThan(rootContas.<Long> get(s.getCampo()), Long.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
        
        
		CriteriaQuery<RadAcct> criteriaQueryGroup = cb.createQuery(RadAcct.class);
		Root<RadAcct> rootGroup = criteriaQueryGroup.from(RadAcct.class);	
		
		if (selectFiltro(resumo).equals("fornecedor.razao_social")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("fornecedor").get("razao_social");		
			criteriaQueryGroup.groupBy(rootGroup.join("fornecedor").get("razao_social"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(RadAcct.class,coluna, qtd));
		}
		
		if (!selectFiltro(resumo).equals("fornecedor.razao_social")) {
			
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
			
			
			criteriaQueryGroup.select(cb.construct(RadAcct.class,coluna, qtd));
			
			
		}
		return em.createQuery(criteriaQueryGroup);	
	}
	
	Window winSubMenuNovo;
	private void buildSubMenu(ClickEvent event) {
		winSubMenuNovo = new Window("Escolha uma das Op????es:");
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
						Window win = new Window("Relat??rio de Consumo");
						win.setWidth("800px");
						win.setHeight("600px");
						win.setResizable(false);
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
						
						StreamResource resource = new StreamResource(new ExportarRelatorioConsumo(tipo,ordenacao,orientacao,resumo,listaParametros,columns), "RELATORIO DE CONSUMO"+nomeRelatorio);
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
					Notification.show("Aten????o", "Voc?? n??o Possui Permiss??o para Gerar PDF", Type.ERROR_MESSAGE);	
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
//					Window win = new Window("Relat??rio de Consumo");
//					win.setWidth("800px");
//					win.setHeight("600px");
//					win.setResizable(false);
//					win.center();
//					win.setModal(true);
//					win.setStyleName("disable_scroolbar");
//					
//					StreamResource resource = new StreamResource(new ExportarRelatorioConsumo(tipo,ordenacao,orientacao,resumo,listaParametros,columns), "RELATORIO DE CONSUMO"+nomeRelatorio);
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
//				Notification.show("Aten????o", "Voc?? n??o Possui Permiss??o para Gerar PDF", Type.ERROR_MESSAGE);	
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

