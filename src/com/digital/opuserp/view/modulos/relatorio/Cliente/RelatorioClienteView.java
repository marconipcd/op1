package com.digital.opuserp.view.modulos.relatorio.Cliente;

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
import com.digital.opuserp.dao.SubModuloDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.RelatorioPre;
import com.digital.opuserp.domain.RelatorioPreColunas;
import com.digital.opuserp.domain.RelatorioPreFiltro;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.exporterpdf.ExcelExporter;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.digital.opuserp.view.modulos.relatorio.Cliente.NovoRelatorioCliente.AddFiltroEvent;
import com.digital.opuserp.view.modulos.relatorio.Cliente.NovoRelatorioCliente.RelatorioClienteEvent;
import com.digital.opuserp.view.modulos.relatorio.Cliente.SalvarRelatorioEditor.SalvarRelatorioEvent;
import com.digital.opuserp.view.modulos.relatorio.Pesquisa.ExportarRelatorioPesquisa;
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
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
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

public class RelatorioClienteView extends VerticalLayout{
	
	private JPAContainer<Cliente> container;	
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
	
	public RelatorioClienteView(boolean act){		
		
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
		
		if(c instanceof RelatorioClienteView)
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
	
	
	public JPAContainer<Cliente> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Cliente.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		container.addNestedContainerProperty("categoria.nome");
		container.addNestedContainerProperty("como_nos_conheceu.nome");
		container.addNestedContainerProperty("endereco_principal.cep");
		container.addNestedContainerProperty("endereco_principal.endereco");
		container.addNestedContainerProperty("endereco_principal.numero");
		container.addNestedContainerProperty("endereco_principal.cidade");
		container.addNestedContainerProperty("endereco_principal.bairro");
		container.addNestedContainerProperty("endereco_principal.uf");
		container.addNestedContainerProperty("endereco_principal.pais");
		container.addNestedContainerProperty("endereco_principal.complemento");
		container.addNestedContainerProperty("endereco_principal.referencia");

	
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
				
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		
		boolean blacklist = false;
		if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(SubModuloDAO.findToId("Clientes"), OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Black List"))				
		{
			blacklist = true;
		}
		
		if(blacklist)				
		{
			tb.setVisibleColumns(new Object[]{"id", "nome_razao","tipo_pessoa", "doc_cpf_cnpj", "doc_rg_insc_estadual", "nome_fantasia",
				"contato", "sexo", "telefone1", "telefone2","celular1", "celular2", "email", "msn", "como_nos_conheceu.nome",
				"obs",  "data_cadastro", "data_alteracao", "status", "como_quer_ser_chamado","categoria.nome","endereco_principal.cep","endereco_principal.endereco",
				"endereco_principal.numero","endereco_principal.cidade","endereco_principal.bairro","endereco_principal.uf","endereco_principal.pais","endereco_principal.complemento","endereco_principal.referencia","black_list"});
		}else{
			tb.setVisibleColumns(new Object[]{"id", "nome_razao","tipo_pessoa", "doc_cpf_cnpj", "doc_rg_insc_estadual", "nome_fantasia",
					"contato", "sexo", "telefone1", "telefone2","celular1", "celular2", "email", "msn", "como_nos_conheceu.nome",
					"obs",  "data_cadastro", "data_alteracao", "status", "como_quer_ser_chamado","categoria.nome","endereco_principal.cep","endereco_principal.endereco",
					"endereco_principal.numero","endereco_principal.cidade","endereco_principal.bairro","endereco_principal.uf","endereco_principal.pais","endereco_principal.complemento","endereco_principal.referencia"});
		}
		
		if(blacklist)				
		{
			tb.setColumnHeader("black_list", "Black List");
		}
		
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome_razao", "Nome/Razão Social");
		tb.setColumnHeader("tipo_pessoa", "Tipo Pessoa");
		tb.setColumnHeader("doc_cpf_cnpj", "CPF/CNPJ");
		tb.setColumnHeader("doc_rg_insc_estadual", "Insc. Estadual/RG");
		tb.setColumnHeader("categoria.nome", "Categoria");
		tb.setColumnHeader("nome_fantasia", "Nome Fantasia");
		tb.setColumnHeader("contato", "Contato");
		tb.setColumnHeader("sexo", "Sexo");
		tb.setColumnHeader("data_nascimento", "Data de Nascimento");
		tb.setColumnHeader("telefone1", "Telefone Principal");
		tb.setColumnHeader("telefone2", "Telefone Alternativo 1");
		tb.setColumnHeader("celular1", "Telefone Alternativo 2");
		tb.setColumnHeader("celular2", "Telefone Alternativo 3");
		tb.setColumnHeader("email", "Email Principal");
		tb.setColumnHeader("msn", "Email Alternativo");
		tb.setColumnHeader("como_nos_conheceu.nome", "Como nos Conheceu?");
		tb.setColumnHeader("categoria.nome", "Categoria");
		tb.setColumnHeader("obs", "OBS");
		tb.setColumnHeader("data_cadastro", "Data de Cadastro");
		tb.setColumnHeader("data_alteracao", "Data de Alteração");
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("como_quer_ser_chamado", "Como Quer Ser Chamado");
		tb.setColumnHeader("endereco_principal.cep", "CEP");
		tb.setColumnHeader("endereco_principal.endereco", "Endereço");
		tb.setColumnHeader("endereco_principal.numero", "Número");
		tb.setColumnHeader("endereco_principal.cidade", "Cidade");
		tb.setColumnHeader("endereco_principal.bairro", "Bairro");
		tb.setColumnHeader("endereco_principal.uf", "UF");
		tb.setColumnHeader("endereco_principal.pais", "País");
		tb.setColumnHeader("endereco_principal.complemento", "Complemento");
		tb.setColumnHeader("endereco_principal.referencia", "Referencia");
		
		
		
		
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("doc_rg_insc_estadual", true);
		tb.setColumnCollapsed("nome_fantasia", true);
		tb.setColumnCollapsed("contato", true);		
		tb.setColumnCollapsed("sexo", true);
		tb.setColumnCollapsed("data_nascimento", true);		
		tb.setColumnCollapsed("telefone1", true);
		tb.setColumnCollapsed("telefone2", true);
		tb.setColumnCollapsed("celular1", true);
		tb.setColumnCollapsed("celular2", true);
		tb.setColumnCollapsed("email", true);
		tb.setColumnCollapsed("msn", true);
		tb.setColumnCollapsed("como_nos_conheceu.nome", true);
		tb.setColumnCollapsed("categoria.nome", true);
		tb.setColumnCollapsed("obs", true);		
		tb.setColumnCollapsed("data_cadastro", true);
		tb.setColumnCollapsed("status", true);
		tb.setColumnCollapsed("como_quer_ser_chamado", true);
		tb.setColumnCollapsed("endereco_principal.cep", true);
		tb.setColumnCollapsed("endereco_principal.endereco", true);
		tb.setColumnCollapsed("endereco_principal.numero", true);
		tb.setColumnCollapsed("endereco_principal.cidade", true);
		tb.setColumnCollapsed("endereco_principal.bairro", true);
		tb.setColumnCollapsed("endereco_principal.uf", true);
		tb.setColumnCollapsed("endereco_principal.pais", true);
		tb.setColumnCollapsed("endereco_principal.complemento",true);
		tb.setColumnCollapsed("endereco_principal.referencia", true);
		tb.setColumnCollapsed("tipo_pessoa", true);

		
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
		
		tb.setColumnCollapsible("nome_razao", false);
		
		
		tb.sort(new Object[]{"nome_razao"}, new boolean[]{true});
		
		
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
		if(s.equals("Nome/Razão Social")){
			filtro = "nome_razao";			
		}else if(s.equals("Código")){
			filtro = "id";			
		}else if(s.equals("CPF/CNPJ")){
			filtro = "doc_cpf_cnpj";			
		}else if(s.equals("Inscrição Estadual/RG")){
			filtro = "doc_rg_insc_estadual";			
		}else if(s.equals("Nome Fantasia")){
			filtro = "nome_fantasia";			
		}else if(s.equals("Sexo")){
			filtro = "sexo";			
		}else if(s.equals("Data de Nascimento")){
			filtro = "data_nascimento";			
		}else if(s.equals("Telefone Principal")){
			filtro = "telefone1";			
		}else if(s.equals("Telefone Alternativo")){
			filtro = "telefone2";			
		}else if(s.equals("Tratamento")){
			filtro = "tratamento";			
		}else if(s.equals("Nome Fantasia")){
			filtro = "nome_fantasia";			
		}else if(s.equals("Contato")){
			filtro = "contato";			
		}else if(s.equals("Telefone Alternativo 2")){
			filtro = "celular1";			
		}else if(s.equals("Telefone Alternativo 3")){
			filtro = "celular2";			
		}else if(s.equals("Email Principal")){
			filtro = "email";			
		}else if(s.equals("Como nos Conheceu?")){
			filtro = "como_nos_conheceu.nome";			
		}else if(s.equals("Email Alternativo")){
			filtro = "msn";			
		}else if(s.equals("OBS")){
			filtro = "obs";			
		}else if(s.equals("Data de Cadastro")){
			filtro = "data_cadastro";			
		}else if(s.equals("Data de Alteração")){
			filtro = "data_alteracao";			
		}else if(s.equals("Categoria")){
			filtro = "categoria.nome";			
		}else if(s.equals("Como Quer Ser Chamado")){
			filtro = "como_quer_ser_chamado";			
		}else if(s.equals("Status")){
			filtro = "status";			
		}else if(s.equals("CEP")){
			filtro = "endereco_principal.cep";			
		}else if(s.equals("Endereço")){
			filtro = "endereco_principal.endereco";			
		}else if(s.equals("Número")){
			filtro = "endereco_principal.numero";			
		}else if(s.equals("Bairro")){
			filtro = "endereco_principal.bairro";			
		}else if(s.equals("Cidade")){
			filtro = "endereco_principal.cidade";			
		}else if(s.equals("Pais")){
			filtro = "endereco_principal.pais";			
		}else if(s.equals("Complemento")){
			filtro = "endereco_principal.complemento";			
		}else if(s.equals("Referencia")){
			filtro = "endereco_principal.referencia";			
		}else if(s.equals("Tipo Pessoa")){
			filtro = "tipo_pessoa";			
		}else if(s.equals("Black List")){
			filtro = "black_list";			
		}
				
		return filtro;
	}
	
	public boolean adicionarFiltro(){
		boolean check = true;
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		
		if(ordenacao != null){
			container.sort(new Object[]{selectFiltro(ordenacao)}, new boolean[]{true});
		}
					
		
		for(SearchParameters sp:listaParametros){
			
				if(sp.getOperador().equals("IGUAL")){
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
						
						
						if(sp.getCampo().equals("endereco_principal.cep") || sp.getCampo().equals("endereco_principal.endereco") || sp.getCampo().equals("endereco_principal.numero")
								|| sp.getCampo().equals("endereco_principal.bairro") || sp.getCampo().equals("endereco_principal.cidade") || sp.getCampo().equals("endereco_principal.pais")
								|| sp.getCampo().equals("endereco_principal.complemento") || sp.getCampo().equals("endereco_principal.referencia")||sp.getCampo().equals("categoria.nome")){
							
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
								
						
						if(sp.getCampo().equals("endereco_principal.cep") || sp.getCampo().equals("endereco_principal.endereco") || sp.getCampo().equals("endereco_principal.numero")
								|| sp.getCampo().equals("endereco_principal.bairro") || sp.getCampo().equals("endereco_principal.cidade") || sp.getCampo().equals("endereco_principal.pais")
								|| sp.getCampo().equals("endereco_principal.complemento") || sp.getCampo().equals("endereco_principal.referencia")||sp.getCampo().equals("categoria.nome")){
							
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(), sp.getValor(), false)));
						}else{
							container.addContainerFilter(Filters.not(new Equal(sp.getCampo(), sp.getValor())));
						}
						
						
						
						
					}else if(container.getType(sp.getCampo()) == Date.class){
						String date = sp.getValor();
						Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3)+date.substring(6,10)));
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), dtValor)));
						
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
					
					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_nascimento")){
						
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
					
					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_nascimento")){
						
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
					
					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_nascimento")){
						
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
					
					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_nascimento")){
						
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
					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_nascimento")){
						
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
					
					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_nascimento")){
						
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
					
					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_nascimento")){
						
						
						try {
							check = true;							
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");							
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new LessOrEqual(sp.getCampo(), dtValor));						
						} catch (ParseException e) {
							
							e.printStackTrace();
						}
						
					}
					
					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_nascimento")){
						
						
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
				
						NovoRelatorioCliente v = new NovoRelatorioCliente("Editar Relatório de Cliente", true,tipo, orientacao, ordenacao,resumo, listaParametros );
						v.addListerner(new NovoRelatorioCliente.RelatorioClienteListerner() {
							
							@Override
							public void onClose(RelatorioClienteEvent event) {
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
						
						v.addListerner(new NovoRelatorioCliente.AddFiltroListerner() {
							
							@Override
							public void onClose(AddFiltroEvent event) {
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
				container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
				container.applyFilters();
				
				hlFloat.replaceComponent(lbRegistros, lbRegistros());
				
				NovoRelatorioCliente v = new NovoRelatorioCliente("Novo Relatório de Cliente", true);
				v.addListerner(new NovoRelatorioCliente.RelatorioClienteListerner() {
					
					@Override
					public void onClose(RelatorioClienteEvent event) {
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
				
				v.addListerner(new NovoRelatorioCliente.AddFiltroListerner() {
					
					@Override
					public void onClose(AddFiltroEvent event) {
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
				
				final SalvarRelatorioEditor salvarEditor = new SalvarRelatorioEditor(codSubModulo,"Salvar Relatório", true);
				salvarEditor.addListerner(new SalvarRelatorioEditor.SalvarRelatorioListerner() {
					
					@Override
					public void onClose(SalvarRelatorioEvent event) {
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
				        
				        for(Cliente c:(List<Cliente>) qGroup.getResultList())
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
	
	private TypedQuery<Cliente> gerarDadosGRaficos(){
		
		EntityManager em = ConnUtil.getEntity();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Cliente> criteriaQuery = cb.createQuery(Cliente.class);
		Root<Cliente> rootCliente = criteriaQuery.from(Cliente.class);
		EntityType<Cliente> type = em.getMetamodel().entity(Cliente.class);

		List<Predicate> criteria = new ArrayList<Predicate>();

		criteria.add(cb.equal(rootCliente.get("empresa"), OpusERP4UI.getEmpresa()));

		if (listaParametros.size() > 0) {
			for (SearchParameters s : listaParametros) {

				if (s.getOperador().equals("IGUAL")) {
					if (s.getCampo().equals("categoria.nome")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("categoria").<String>get("nome")), s.getValor().toLowerCase()));
					}

					if (s.getCampo().equals("como_nos_conheceu.nome")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("como_nos_conheceu").<String>get("nome")), s.getValor().toLowerCase()));
					}
					
					//endereco_principals
					if (s.getCampo().equals("endereco_principal.cep")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal_principal").<String>get("cep")), s.getValor().toLowerCase()));
					}						
					if (s.getCampo().equals("endereco_principal.numero")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("numero")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.endereco")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("endereco")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.bairro")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("bairro")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.cidade")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("cidade")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.uf")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("uf")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.pais")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("pais")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.complemento")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("complemento")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.referencia")) {
						criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("referencia")), s.getValor().toLowerCase()));
					}

					if (!s.getCampo().equals("categoria.nome") && !s.getCampo().equals("como_nos_conheceu.nome") && 
							!s.getCampo().equals("endereco_principal.cep") && !s.getCampo().equals("endereco_principal.numero") &&
							!s.getCampo().equals("endereco_principal.endereco") && !s.getCampo().equals("endereco_principal.bairro") &&
							!s.getCampo().equals("endereco_principal.uf") && !s.getCampo().equals("endereco_principal.cidade") && 
							!s.getCampo().equals("endereco_principal.pais") && !s.getCampo().equals("endereco_principal.complemento") &&
							!s.getCampo().equals("endereco_principal.referencia")) {
						criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
					}

				}else if (s.getOperador().equals("DIFERENTE")) {
					if (s.getCampo().equals("categoria.nome")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("categoria").<String>get("nome")), s.getValor().toLowerCase()));
					}

					if (s.getCampo().equals("como_nos_conheceu.nome")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("como_nos_conheceu").<String>get("nome")),s.getValor().toLowerCase()));
					}
					
					//endereco_principals
					if (s.getCampo().equals("endereco_principal.cep")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("cep")),  s.getValor().toLowerCase()));
					}						
					if (s.getCampo().equals("endereco_principal.numero")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("numero")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.endereco")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("endereco")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.bairro")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("bairro")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.cidade")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("cidade")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.uf")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("uf")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.pais")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("pais")), s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.complemento")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("complemento")),  s.getValor().toLowerCase()));
					}
					if (s.getCampo().equals("endereco_principal.referencia")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("referencia")), s.getValor().toLowerCase()));
					}
					
					if (!s.getCampo().equals("categoria.nome") && !s.getCampo().equals("como_nos_conheceu.nome") && 
							!s.getCampo().equals("endereco_principal.cep") && !s.getCampo().equals("endereco_principal.numero") &&
							!s.getCampo().equals("endereco_principal.endereco") && !s.getCampo().equals("endereco_principal.bairro") &&
							!s.getCampo().equals("endereco_principal.uf") && !s.getCampo().equals("endereco_principal.cidade") && 
							!s.getCampo().equals("endereco_principal.pais") && !s.getCampo().equals("endereco_principal.complemento") &&
							!s.getCampo().equals("endereco_principal.referencia")) {
						
						criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())), s.getValor().toLowerCase()));
					}

				} else if (s.getOperador().equals("CONTEM")) {
					
					if (s.getCampo().equals("categoria.nome")) {
						criteria.add(cb.like(cb.lower(rootCliente.get("categoria").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));							
					}

					if (s.getCampo().equals("como_nos_conheceu.nome")) {
						criteria.add(cb.like(cb.lower(rootCliente.get("como_nos_conheceu").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					//endereco_principals
					if (s.getCampo().equals("endereco_principal.cep")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("cep")), "%" + s.getValor().toLowerCase()+ "%"));
					}						
					if (s.getCampo().equals("endereco_principal.numero")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("numero")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.endereco")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.bairro")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.cidade")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.uf")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.pais")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("pais")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.complemento")) {							
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("complemento")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.referencia")) {
						criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (!s.getCampo().equals("categoria.nome") && !s.getCampo().equals("como_nos_conheceu.nome") && 
							!s.getCampo().equals("endereco_principal.cep") && !s.getCampo().equals("endereco_principal.numero") &&
							!s.getCampo().equals("endereco_principal.endereco") && !s.getCampo().equals("endereco_principal.bairro") &&
							!s.getCampo().equals("endereco_principal.uf") && !s.getCampo().equals("endereco_principal.cidade") && 
							!s.getCampo().equals("endereco_principal.pais") && !s.getCampo().equals("endereco_principal.complemento") &&
							!s.getCampo().equals("endereco_principal.referencia")) {
						
						criteria.add(cb.like(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
					}
				} else if (s.getOperador().equals("NAO CONTEM")) {
					
					if (s.getCampo().equals("categoria.nome")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("categoria").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
					}

					if (s.getCampo().equals("como_nos_conheceu.nome")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("como_nos_conheceu").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
					}
					
					//endereco_principals
					if (s.getCampo().equals("endereco_principal.cep")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("cep")), "%" + s.getValor().toLowerCase()+ "%"));
					}						
					if (s.getCampo().equals("endereco_principal.numero")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("numero")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.endereco")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.bairro")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.cidade")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.uf")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.pais")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("pais")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.complemento")) {							
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("complemento")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					if (s.getCampo().equals("endereco_principal.referencia")) {
						criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
					}
					
					if (!s.getCampo().equals("categoria.nome") && !s.getCampo().equals("como_nos_conheceu.nome") && 
							!s.getCampo().equals("endereco_principal.cep") && !s.getCampo().equals("endereco_principal.numero") &&
							!s.getCampo().equals("endereco_principal.endereco") && !s.getCampo().equals("endereco_principal.bairro") &&
							!s.getCampo().equals("endereco_principal.uf") && !s.getCampo().equals("endereco_principal.cidade") && 
							!s.getCampo().equals("endereco_principal.pais") && !s.getCampo().equals("endereco_principal.complemento") &&
							!s.getCampo().equals("endereco_principal.referencia")) {
						
						criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
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

		//criteriaQuery.orderBy(cb.asc(rootCliente.get(selectCampo(order))));
		
		
	
		TypedQuery q = em.createQuery(criteriaQuery);
        
        
		CriteriaQuery<Cliente> criteriaQueryGroup = cb.createQuery(Cliente.class);
		Root<Cliente> rootGroup = criteriaQueryGroup.from(Cliente.class);
		
		
		if (selectCampo(resumo).equals("categoria.nome")) {
			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("categoria").get("nome");
			
			criteriaQueryGroup.groupBy(rootGroup.join("categoria").get("nome"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
		}

		if (selectCampo(resumo).equals("como_nos_conheceu.nome")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("como_nos_conheceu").get("nome");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("como_nos_conheceu").get("nome"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
		}
		
		//endereco_principals
		if (selectCampo(resumo).equals("endereco_principal.cep")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco_principal").get("cep");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("cep"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
		}						
		if (selectCampo(resumo).equals("endereco_principal.numero")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco_principal").get("qtd");
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("qtd"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
		}
		if (selectCampo(resumo).equals("endereco_principal.endereco")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco_principal").get("endereco");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("endereco"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
		}
		if (selectCampo(resumo).equals("endereco_principal.bairro")) {

			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco_principal").get("bairro");
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("bairro"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			
		}
		if (selectCampo(resumo).equals("endereco_principal.cidade")) {
			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco_principal").get("cidade");
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("cidade"));			
			
			
			if (criteria.size() == 0) {
				throw new RuntimeException("no criteria");
			} else if (criteria.size() == 1) {					
				criteriaQueryGroup.where(cb.and(criteria.get(0)));
			} else {					
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
			}
		
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			
		}
		if (selectCampo(resumo).equals("endereco_principal.uf")) {
			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco_principal").get("uf");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("uf"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			
		}
		if (selectCampo(resumo).equals("endereco_principal.pais")) {
			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco_principal").get("pais");		
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("pais"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			
		}
		if (selectCampo(resumo).equals("endereco_principal.complemento")) {
			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco_principal").get("complemento");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("complemento"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			
		}
		if (selectCampo(resumo).equals("endereco_principal.referencia")) {
							
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("endereco_principal").get("referencia");	
			
			criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("referencia"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			
		}

		if (!selectCampo(resumo).equals("categoria.nome") && !selectCampo(resumo).equals("como_nos_conheceu.nome") && 
				!selectCampo(resumo).equals("endereco_principal.cep") && !selectCampo(resumo).equals("endereco_principal.numero") &&
				!selectCampo(resumo).equals("endereco_principal.endereco") && !selectCampo(resumo).equals("endereco_principal.bairro") &&
				!selectCampo(resumo).equals("endereco_principal.uf") && !selectCampo(resumo).equals("endereco_principal.cidade") && 
				!selectCampo(resumo).equals("endereco_principal.pais") && !selectCampo(resumo).equals("endereco_principal.complemento") &&
				!selectCampo(resumo).equals("endereco_principal.referencia")) {
			
			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get(selectCampo(resumo));				
			criteriaQueryGroup.groupBy(rootGroup.get(selectCampo(resumo)));			
			
			if (criteria.size() == 0) {
				throw new RuntimeException("no criteria");
			} else if (criteria.size() == 1) {					
				criteriaQueryGroup.where(criteria.get(0));
			} else {					
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
			}
			
			
			//criteriaQueryGroup.where(cb.not(cb.equal(rootGroup.join("endereco_principal").get("cidade"), "BELO JARDIM")));
			criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			
			
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
						Window win = new Window("Relatório de Clientes");
						win.setWidth("800px");
						win.setHeight("600px");
						win.setResizable(false);
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
						
						StreamResource resource = new StreamResource(new ExportCliente(tipo,ordenacao,orientacao,resumo,listaParametros,columns), "RELATORIO DE CLIENTES"+nomeRelatorio);
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
//					try {
//						
//						List<Object> columns = new ArrayList<Object>();
//						Object[] visible_columns = tb.getVisibleColumns();		
//						for(Object c:visible_columns){
//							if(!tb.isColumnCollapsed(c.toString())){					   	
//								columns.add(c);
//							}	
//						}
//						
//						if(tfNomeRelatorio.getValue()!=null && !tfNomeRelatorio.getValue().equals("")){
//							nomeRelatorio = " - "+relatorioPre.getNome_relatorio()+".pdf";
//						}else{
//							nomeRelatorio = ".pdf";
//						}
//						// INSTANCIA UMA NOVA JANELA E ADICIONA SUAS PROPRIEDADES
//						Window win = new Window("Relatório de Clientes");
//						win.setWidth("800px");
//						win.setHeight("600px");
//						win.setResizable(false);
//						win.center();
//						win.setModal(true);
//						win.setStyleName("disable_scroolbar");
//						
//						StreamResource resource = new StreamResource(new ExportCliente(tipo,ordenacao,orientacao,resumo,listaParametros,columns), "RELATORIO DE CLIENTES"+nomeRelatorio);
//						resource.getStream();
//						resource.setMIMEType("application/pdf");
//						resource.setCacheTime(0);
//						
//						Embedded e = new Embedded();
//						e.setSizeFull();
//						e.setType(Embedded.TYPE_BROWSER);
//						e.setSource(resource);
//						
//						win.setContent(e);
//						getUI().addWindow(win);
//					} catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();
//						System.out.println("Erro: "+e.getMessage());
//						System.out.println("Causado por: "+e.getCause());
//					}
//				
//				}else{
//					Notify.Show("Você não Possui Permissão para Gerar PDF !", Notify.TYPE_ERROR);
//				}
//			}
//		});
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
//	}



	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}

public String selectCampo(String s) {
		
		String filtro = "";
		if(s.equals("Nome/Razão Social")){
			filtro = "nome_razao";			
		}else if(s.equals("Código")){
			filtro = "id";			
		}else if(s.equals("CPF/CNPJ")){
			filtro = "doc_cpf_cnpj";			
		}else if(s.equals("Inscrição Estadual/RG")){
			filtro = "doc_rg_insc_estadual";			
		}else if(s.equals("Nome Fantasia")){
			filtro = "nome_fantasia";			
		}else if(s.equals("Sexo")){
			filtro = "sexo";			
		}else if(s.equals("Data de Nascimento")){
			filtro = "data_nascimento";			
		}else if(s.equals("Telefone Principal")){
			filtro = "telefone1";			
		}else if(s.equals("Telefone Alternativo")){
			filtro = "telefone2";			
		}else if(s.equals("Tratamento")){
			filtro = "tratamento";			
		}else if(s.equals("Nome Fantasia")){
			filtro = "nome_fantasia";			
		}else if(s.equals("Contato")){
			filtro = "contato";			
		}else if(s.equals("Telefone Alternativo 2")){
			filtro = "celular1";			
		}else if(s.equals("Telefone Alternativo 3")){
			filtro = "celular2";			
		}else if(s.equals("Email Principal")){
			filtro = "email";			
		}else if(s.equals("Como nos Conheceu?")){
			filtro = "como_nos_conheceu.nome";			
		}else if(s.equals("Email Alternativo")){
			filtro = "msn";			
		}else if(s.equals("OBS")){
			filtro = "obs";			
		}else if(s.equals("Data de Cadastro")){
			filtro = "data_cadastro";			
		}else if(s.equals("Data de Alteração")){
			filtro = "data_alteracao";			
		}else if(s.equals("Categoria")){
			filtro = "categoria.nome";			
		}else if(s.equals("Como Quer Ser Chamado")){
			filtro = "como_quer_ser_chamado";			
		}else if(s.equals("Status")){
			filtro = "status";			
		}else if(s.equals("CEP")){
			filtro = "endereco_principal.cep";			
		}else if(s.equals("Endereço")){
			filtro = "endereco_principal.endereco";			
		}else if(s.equals("Número")){
			filtro = "endereco_principal.numero";			
		}else if(s.equals("Bairro")){
			filtro = "endereco_principal.bairro";			
		}else if(s.equals("Cidade")){
			filtro = "endereco_principal.cidade";			
		}else if(s.equals("Pais")){
			filtro = "endereco_principal.pais";			
		}else if(s.equals("Complemento")){
			filtro = "endereco_principal.complemento";			
		}else if(s.equals("Referencia")){
			filtro = "endereco_principal.referencia";			
		}else if(s.equals("Tipo Pessoa")){
			filtro = "tipo_pessoa";			
		}else if(s.equals("Black List")){
			filtro = "black_list";			
		}
				
		return filtro;
	}

	
}
