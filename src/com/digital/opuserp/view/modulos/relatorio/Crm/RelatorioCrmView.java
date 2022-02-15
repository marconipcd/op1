package com.digital.opuserp.view.modulos.relatorio.Crm;

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
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.RelatorioPre;
import com.digital.opuserp.domain.RelatorioPreColunas;
import com.digital.opuserp.domain.RelatorioPreFiltro;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.exporterpdf.ExcelExporter;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.digital.opuserp.view.modulos.relatorio.Crm.NovoRelatorioCrm.AddFiltroCrmEvent;
import com.digital.opuserp.view.modulos.relatorio.Crm.NovoRelatorioCrm.RelatorioCrmEvent;
import com.digital.opuserp.view.modulos.relatorio.Crm.SalvarRelatorioCrmEditor.SalvarRelatorioCrmEvent;
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
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class RelatorioCrmView extends VerticalLayout{
	
	private JPAContainer<Crm> container;	
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
	
	public RelatorioCrmView(boolean act){		
		
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
		
		if(c instanceof RelatorioCrmView)
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

	public JPAContainer<Crm> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Crm.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));		
		container.addNestedContainerProperty("setor.nome");
		container.addNestedContainerProperty("crm_assuntos.nome");
		container.addNestedContainerProperty("cliente.id");
		container.addNestedContainerProperty("cliente.contato");
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("crm_formas_contato.nome");
		container.addNestedContainerProperty("contato_feedback.nome");
		container.addNestedContainerProperty("contrato.id");
		
			
		return container;
	}

	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(tb.getType(colId).equals(Date.class)){
				
					if(colId.equals("data_cadastro")||colId.equals("data_efetuado")){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
							return sdf.format((Date)tb.getItem(rowId).getItemProperty(colId).getValue());					
						}else{
							return super.formatPropertyValue(rowId, colId, property);	
						}
						
//					}else if(colId.equals("data_agendado")){
//						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
//							SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");						
//							return sdf.format(tb.getItem(rowId).getItemProperty(colId).getValue());
//						}else{
//							return super.formatPropertyValue(rowId, colId, property);	
//						}				
					}else{
						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
							SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");						
							return sdf.format(tb.getItem(rowId).getItemProperty(colId).getValue());
						}else{
							return super.formatPropertyValue(rowId, colId, property);	
						}	
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

		tb.setVisibleColumns(new Object[] {"id","data_cadastro","crm_formas_contato.nome","setor.nome","crm_assuntos.nome","conteudo",
				"cliente.nome_razao","cliente.contato","data_agendado","hora_agendado","data_efetuado","tempo_atendimento","operador_tratamento",
				"contato_feedback.nome","status","motivo_reagendamento","contrato.id","plano","concentrador"});

		tb.setColumnHeader("id", "Cod.");
		tb.setColumnHeader("setor.nome", "Setor");
		tb.setColumnHeader("data_agendado", "Data Agendado");
		tb.setColumnHeader("hora_agendado", "Hora Agendado");
		tb.setColumnHeader("data_efetuado", "Data Conclusão");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("cliente.contato", "Contato");
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("crm_assuntos.nome", "Crm Assuntos");
		tb.setColumnHeader("operador_tratamento", "Operador Tratamento");
		tb.setColumnHeader("data_inicio_tratamento", "Data Inicio Tratamento");
		tb.setColumnHeader("tempo_atendimento", "Tempo Total Tratamento");
		tb.setColumnHeader("motivo_reagendamento", "Motivo Reagendamento");
		tb.setColumnHeader("data_cadastro", "Data Abertura");
		tb.setColumnHeader("hora", "Hora Abertura");
		tb.setColumnHeader("crm_formas_contato.nome","Forma de Contato");
		tb.setColumnHeader("contato_feedback.nome","Contato Feedback");
		tb.setColumnHeader("conteudo","Conteúdo");
		tb.setColumnHeader("contrato.id","Contrato");
		tb.setColumnHeader("plano","Plano");
		tb.setColumnHeader("concentrador","Concentrador");
	
		tb.setColumnCollapsingAllowed(true);
		
		tb.setColumnCollapsed("conteudo", true);
		tb.setColumnCollapsed("contato_feedback.nome", true);
		tb.setColumnCollapsed("crm_formas_contato.nome", true);
		tb.setColumnCollapsed("hora_agendado", true);
		
		//tb.setConverter("id", null);

		
		tb.setImmediate(true);
		
		tb.setColumnCollapsible("cliente.nome_razao", true);
		
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
					
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
		tb.sort(new Object[]{"cliente.nome_razao"}, new boolean[]{true});

		
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
		}else if(s.equals("Setor")){
			filtro = "setor.nome";					
		}else if(s.equals("Data Cadastro")){
			filtro = "data_cadastro";					
		}else if(s.equals("Data Agendado")){
			filtro = "data_agendado";					
		}else if(s.equals("Data Conclusão")){
			filtro = "data_efetuado";					
		}else if(s.equals("Hora Agendado")){
			filtro = "hora_agendado";					
		}else if(s.equals("Contato")){
			filtro = "cliente.contato";					
		}else if(s.equals("Status")){
			filtro = "status";					
		}else if(s.equals("Crm Assuntos")){
			filtro = "crm_assuntos.nome";				
		}else if(s.equals("Operador Tratamento")){
			filtro = "operador_tratamento";					
		}else if(s.equals("Data Inicio Tratamento")){
			filtro = "data_inicio_tratamento";					
		}else if(s.equals("Tempo Total Tratamento")){
			filtro = "tempo_total_tratamento";					
		}else if(s.equals("Motivo Reagendamento")){
			filtro = "motivo_reagendamento";					
		}else if(s.equals("Data Abertura")){
			filtro = "data_cadastro";
		}else if(s.equals("Contrato")){
			filtro = "contrato.id";
		}else if(s.equals("Plano")){
			filtro = "plano";
		}else if(s.equals("Concentrador")){
			filtro = "concentrador";
		}else if(s.equals("Conteudo")){
			filtro = "conteudo";
		}
		return filtro;
	}
	
	public boolean adicionarFiltro() throws UnsupportedFilterException, ParseException{
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
						
						
						if(sp.getCampo().equals("cliente.nome_razao")){							
							container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), false));
							
						}else if(sp.getCampo().equals("cliente.contato")){							
							container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), false));
							
						}else if(sp.getCampo().equals("crm_assuntos.nome")){
							container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), false));
							
						}else if(sp.getCampo().equals("setor.nome")){
							container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), false));
						}else{
							container.addContainerFilter(new Equal(sp.getCampo(), sp.getValor()));
						}
						
						
						
						
					}else if(container.getType(sp.getCampo()) == Date.class){
						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						container.addContainerFilter(new Equal(sp.getCampo(), sdf.parse(sp.getValor())));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
					}
					
				}else if(sp.getOperador().equals("DIFERENTE")){
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
								
						
						if(sp.getCampo().equals("cliente.nome_razao")){	
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(), sp.getValor(), false)));
							
						}else if(sp.getCampo().equals("cliente.contato")){							
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(), sp.getValor(), false)));
							
						}else if(sp.getCampo().equals("crm_assuntos.nome")){
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(), sp.getValor(), false)));
							
						}else if(sp.getCampo().equals("setor.nome")){
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(), sp.getValor(), false)));						
						}else{
							container.addContainerFilter(Filters.not(new Equal(sp.getCampo(), sp.getValor())));
						}
								
					}else if(container.getType(sp.getCampo()) == Date.class){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), sdf.parseObject(sp.getValor()))));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						//container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
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
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");							
							container.addContainerFilter(new Greater(sp.getCampo(), sdf.parse(sp.getValor())));
							
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new Greater(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					
				}else if(sp.getOperador().equals("MENOR QUE")){
					
										
					if(container.getType(sp.getCampo()) == Date.class ){
						
						try {
							check = true;						
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");													
							container.addContainerFilter(new Less(sp.getCampo(), sdf.parse(sp.getValor())));
						} catch (Exception e) {
							// TODO Auto-generated catch block
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
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");							
							container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), sdf.parse(sp.getValor())));
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
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
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");						
							container.addContainerFilter(new LessOrEqual(sp.getCampo(), sdf.parse(sp.getValor())));						
						} catch (ParseException e) {
							// TODO Auto-generated catch block
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
				
						NovoRelatorioCrm v = new NovoRelatorioCrm("Editar Relatório Crm", true,tipo, orientacao, ordenacao,resumo, listaParametros );
						v.addListerner(new NovoRelatorioCrm.RelatorioCrmListerner() {
							
							@Override
							public void onClose(RelatorioCrmEvent event) {
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
										relatoriDAO.addRelatorio(relatorioPre, filtrosRelatorio,colunasVisiveis);
										
										Notification.show("Relatório Salvo com Sucesso", Type.ERROR_MESSAGE);
										btSalvar.setEnabled(false);
										btImprimir.setEnabled(true);
										btGerarChart.setEnabled(true);
									}catch (Exception e) {
										e.printStackTrace();
									}
									
									
								}
									
								
			 				}
						});
						
						v.addListerner(new NovoRelatorioCrm.AddFiltroCrmListerner() {
							
							@Override
							public void onClose(AddFiltroCrmEvent event) {
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
				
				NovoRelatorioCrm v = new NovoRelatorioCrm("Novo Relatório Crm", true);
				v.addListerner(new NovoRelatorioCrm.RelatorioCrmListerner() {
					
					@Override
					public void onClose(RelatorioCrmEvent event) {
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
							//btImprimir.setEnabled(false);
						}
	 				}
				});
				
				v.addListerner(new NovoRelatorioCrm.AddFiltroCrmListerner() {
					
					@Override
					public void onClose(AddFiltroCrmEvent event) {
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
								
				final SalvarRelatorioCrmEditor salvarEditor = new SalvarRelatorioCrmEditor(codSubModulo,"Salvar Relatório", true);
				salvarEditor.addListerner(new SalvarRelatorioCrmEditor.SalvarRelatorioCrmListerner() {
					
					@Override
					public void onClose(SalvarRelatorioCrmEvent event) {
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
				        
				        for(Crm c:(List<Crm>) qGroup.getResultList())
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
	
	private TypedQuery<Crm> gerarDadosGRaficos(){
		
		EntityManager em = ConnUtil.getEntity();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Crm> criteriaQuery = cb.createQuery(Crm.class);
		Root<Crm> rootContas = criteriaQuery.from(Crm.class);
		EntityType<Crm> type = em.getMetamodel().entity(Crm.class);

		List<Predicate> criteria = new ArrayList<Predicate>();

		criteria.add(cb.equal(rootContas.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

		if (listaParametros.size() > 0) {
			
			try{
				for (SearchParameters s : listaParametros) {
	
					if (s.getOperador().equals("IGUAL")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.contato")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("contato")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("crm_assuntos.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("crm_assuntos").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("setor.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("setor").<String>get("nome")), s.getValor().toLowerCase()));
						}					
							
						if (!s.getCampo().equals("cliente.contato") && !s.getCampo().equals("cliente.nome_razao") && 
							!s.getCampo().equals("crm_assuntos.nome")&& !s.getCampo().equals("setor.nome")) {
							
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
							
						}
	
					}else if (s.getOperador().equals("DIFERENTE")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.contato")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("contato")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("crm_assuntos.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("crm_assuntos").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("setor.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("setor").<String>get("nome")), s.getValor().toLowerCase()));
						}					
							
						if (!s.getCampo().equals("cliente.contato") && !s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("crm_assuntos.nome")&& !s.getCampo().equals("setor.nome")) {
														
							
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
							
							
						}
	
					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.contato")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("contato")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("crm_assuntos.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("crm_assuntos").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("setor.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("setor").<String>get("nome")), s.getValor().toLowerCase()));
						}					
							
						if (!s.getCampo().equals("cliente.contato") && !s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("crm_assuntos.nome")&& !s.getCampo().equals("setor.nome")) {							
							
							criteria.add(cb.like(cb.lower(rootContas.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
					} else if (s.getOperador().equals("NAO CONTEM")) {
						
	
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.contato")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("contato")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("crm_assuntos.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("crm_assuntos").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("setor.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("setor").<String>get("nome")), s.getValor().toLowerCase()));
						}					
							
						if (!s.getCampo().equals("cliente.contato") && !s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("crm_assuntos.nome")&& !s.getCampo().equals("setor.nome")) {
							
							criteria.add(cb.notLike(cb.lower(rootContas.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
					} else if (s.getOperador().equals("MAIOR QUE")) {
						
						try{						
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.greaterThanOrEqualTo(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){								
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.greaterThanOrEqualTo(rootContas.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
						
					} else if (s.getOperador().equals("MENOR QUE")) {
						
						
						
						try{						
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.lessThanOrEqualTo(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.lessThanOrEqualTo(rootContas.<Date> get(s.getCampo()),  sdf.parse(s.getValor())));
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

		//criteriaQuery.orderBy(cb.asc(rootContas.get(selectFiltro(order))));
		
		
	
		TypedQuery q = em.createQuery(criteriaQuery);
        
        
		CriteriaQuery<Crm> criteriaQueryGroup = cb.createQuery(Crm.class);
		Root<Crm> rootGroup = criteriaQueryGroup.from(Crm.class);
		
		
		if (selectFiltro(resumo).equals("cliente.nome_razao")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("nome_razao");		
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("nome_razao"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("cliente.contato")) {				
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("contato");				
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("contato"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("crm_assuntos.nome")) {				
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("crm_assuntos").get("nome");			
			criteriaQueryGroup.groupBy(rootGroup.join("crm_assuntos").get("nome"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("setor.nome")) {				
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("setor").get("nome");				
			criteriaQueryGroup.groupBy(rootGroup.join("setor").get("nome"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
		}
	
		
		if (!selectFiltro(resumo).equals("cliente.contato") && !selectFiltro(resumo).equals("cliente.nome_razao") &&
			!selectFiltro(resumo).equals("crm_assuntos.nome") && !selectFiltro(resumo).equals("setor.nome")) {
			
			
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
			
			
			criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
			
			
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
						Window win = new Window("Relatório de CRM");
						win.setWidth("800px");
						win.setHeight("600px");
						win.setResizable(true);
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
						
						StreamResource resource = new StreamResource(new ExportarRelatorioCrm(tipo,ordenacao,orientacao,resumo,listaParametros,columns), "RELATORIO DE CRM"+nomeRelatorio);
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

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}

	
	
}

