package com.digital.opuserp.view.modulos.estoque.produto;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesProdutoDAO;
import com.digital.opuserp.dao.CofinsDAO;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.GrupoProdutoDAO;
import com.digital.opuserp.dao.IcmsDAO;
import com.digital.opuserp.dao.IpiDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.LojaIntegradaDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.PisDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SerialDAO;
import com.digital.opuserp.domain.AlteracoesProduto;
import com.digital.opuserp.domain.Cofins;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.Icms;
import com.digital.opuserp.domain.Ipi;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Pis;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.domain.TipoItemProduto;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.estoque.produto.AlterarNcm.AlterarEvent;
import com.digital.opuserp.view.modulos.estoque.produto.CorrecaoSaldo.SerialEvent;
import com.digital.opuserp.view.modulos.estoque.produto.ProdutoEditor.ProdutoEvent;
import com.digital.opuserp.view.modulos.estoque.produto.historico.HistoricoAlteracoesProduto;
import com.digital.opuserp.view.modulos.estoque.produto.historico.HistoricoEntradas;
import com.digital.opuserp.view.modulos.estoque.produto.historico.HistoricoSaidas;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ProdutoImportarUtil;
//import com.github.wolfie.refresher.Refresher;
import com.hexiong.jdbf.DBFWriter;
import com.hexiong.jdbf.JDBFException;
import com.hexiong.jdbf.JDBField;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.QueryModifierDelegate;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.LessOrEqual;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class ProdutoView extends VerticalLayout implements GenericView {

	private JPAContainer<Produto> container;
	
	private Table tb;
	private TextField tfBusca;
	private Button btNovo;
	private Button btVisualizar;
	private Button btSerial;
	private Button btAlterar;
	private Button btCopiar;
	private Button btEstoque;
	private Button btHistorico;
	private Button btExcluir;
	private Button btGerarDBF;
	private Button btExportar;
	private Button btLog;
	private Button btLojaIntegrada;	
	private Button btAtualizarProdutos;
	private Label lbRegistros;
	
	private boolean copiar = true;
	private boolean ref = true;
	private Float novaQtd = new Float(0);

	private HorizontalLayout hlFloat;	
	
	private ShortcutListener slNovo;
	private ShortcutListener slEditar;
	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	
	private ComboBox cbStatus;
	private ComboBox cbStatusSaldo;
	private ComboBox cbTipoItem;
	
	public ProdutoView(boolean act){		
		
		if(act){
			
		setSizeFull();
		
		gmDAO = new GerenciarModuloDAO();
		
		tfBusca = buildTfbusca();
		
		container = buildContainer();
		
		tb = buildTbGeneric();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		
		if(OpusERP4UI.getUsuarioLogadoUI().getUsername().equals("marconi")){
			hlButons.addComponent(BuildbtAtualizarProdutos());
		}
		
		
		hlButons.addComponent(BuildbtTransferencia());
		hlButons.addComponent(BuildBtImportar());
		hlButons.addComponent(BuildbtAceitar());
		hlButons.addComponent(BuildbtAlterar());
		hlButons.addComponent(BuildbtCopiar());		
		hlButons.addComponent(BuildbtCorrecaoEstoque());
		hlButons.addComponent(BuildbtHistorico());
		hlButons.addComponent(BuildbtLog());
		hlButons.addComponent(BuildbtGerarDBF());
		hlButons.addComponent(BuildbtExportar());
		hlButons.addComponent(BuildbtCancelar());
	//	hlButons.addComponent(BuildbtLojaIntegrada());
		
		addComponent(hlButons);
		setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
				
				addComponent(buildCbStatus());
				addComponent(buildCbTipoItem());
				addComponent(buildCbSaldo());
				addComponent(buildCbGrupo());
				addComponent(buildTfbusca());
				setExpandRatio(tfBusca, 1.0f);
			}
		});
		
		addComponent(buildTbGeneric());
		
		hlFloat = new HorizontalLayout();
		hlFloat.setWidth("100%");
		hlFloat.addComponent(lbRegistros());
		Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
		lbLegend.setWidth("250px");
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
		addFilter(tfBusca.getValue());
		btVisualizar.setEnabled(false);
		btExcluir.setEnabled(false);
		btCopiar.setEnabled(false);
		btHistorico.setEnabled(false);		
		btEstoque.setEnabled(false);
		btAlterar.setEnabled(false);
	}
	private ComboBox buildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		cbStatus.addItem("ATIVO");
		cbStatus.addItem("INATIVO");
		cbStatus.addItem("EM DIGITACAO");
		cbStatus.setImmediate(true);
		cbStatus.select("ATIVO");
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		
		return cbStatus;
	}
	
	private ComboBox buildCbSaldo(){
		cbStatusSaldo = new ComboBox();
		cbStatusSaldo.setNullSelectionAllowed(false);
		cbStatusSaldo.setTextInputAllowed(false);
		cbStatusSaldo.addItem("QUALQUER SALDO");
		cbStatusSaldo.addItem("PONTO DE PEDIDO");
		cbStatusSaldo.addItem("SEM SALDO");
		cbStatusSaldo.addItem("COM SALDO");
		cbStatusSaldo.addItem("NECESSITA REPOSIÇÃO");
		cbStatusSaldo.setImmediate(true);
		cbStatusSaldo.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		cbStatusSaldo.select("QUALQUER SALDO");
		
		
		
		return cbStatusSaldo;
	}
	
	private JPAContainer<TipoItemProduto> buildTipoItemProduto(){
		JPAContainer<TipoItemProduto> container = JPAContainerFactory.make(TipoItemProduto.class, ConnUtil.getEntity());
		
		
		return container;
	}
	
	private ComboBox buildCbTipoItem(){
		cbTipoItem = new ComboBox(null,buildTipoItemProduto());		
		cbTipoItem.setTextInputAllowed(false);
		//cbTipoItem.addItem("TODOS OS TIPOS");
		//cbTipoItem.addItem("Mercadoria para Revenda");
		//cbTipoItem.addItem("Material de uso e consumo");
		//cbTipoItem.addItem("Matéria-prima");
		//cbTipoItem.addItem("Ativo Imobilizado");
		
		cbTipoItem.setItemCaptionPropertyId("nome");
		cbTipoItem.setImmediate(true);
		cbTipoItem.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {

				addFilter(tfBusca.getValue());
			}
		});
		//cbTipoItem.select("TODOS OS TIPOS");
		
		
		
		return cbTipoItem;
	}
	
	
	ComboBox cbGrupos;
	private ComboBox buildCbGrupo(){
		
		List<GrupoProduto> grupos = GrupoProdutoDAO.getAll();
		
		cbGrupos = new ComboBox();
		cbGrupos.setNullSelectionAllowed(false);
		cbGrupos.setTextInputAllowed(false);
		cbGrupos.addItem("TODOS OS GRUPOS");
		
		for (GrupoProduto grupoProduto : grupos) {
			cbGrupos.addItem(grupoProduto.getNome_grupo());
		}
		
		cbGrupos.setImmediate(true);
		cbGrupos.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {

				addFilter(tfBusca.getValue());
			}
		});
		cbGrupos.select("TODOS OS GRUPOS");
				
		return cbGrupos;
	}
	

	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ProdutoView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			//btVisualizar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btVisualizar != null || slEditar != null){
				//btVisualizar.removeShortcutListener(slEditar);			
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
				btNovo.click();				
			}
		};
		return slNovo;
	}
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btVisualizar.click();
				btVisualizar.removeShortcutListener(slEditar);
				//addExtension(buildRefresher());
			}
		};
		
		return slEditar;
	}
	public JPAContainer<Produto> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Produto.class,ConnUtil.getEntity());
		
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
		
		if(cbStatus != null && cbStatus.getValue() != null){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue()));
		}
		
	//if(cbStatusSaldo !=null && cbStatusSaldo.getValue().toString()!=null && !cbStatusSaldo.getValue().toString().equals("QUALQUER SALDO")){
	//		
			//if(cbStatusSaldo.getValue().toString().equals("SEM SALDO")){
			//	container.addContainerFilter(Filters.eq("qtdEstoque", 0));	
		   //  	container.addContainerFilter(new LessOrEqual("qtdEstoque", 0));
		  //}else if(cbStatusSaldo.getValue().toString().equals("PONTO DE PEDIDO")){
//				
//				List<Produto> produto = ProdutoDAO.listProduto(OpusERP4UI.getEmpresa().getId());				
//				for(Produto p :produto){	
//					if(p.getEstoqueMin() != null && !p.getEstoqueMin().equals("") && p.getQtdEstoque() != null && !p.getQtdEstoque().equals("")){
//						if(p.getEstoqueMin().equals(p.getQtdEstoque())){
//							container.addContainerFilter(Filters.eq("estoqueMin", p.getQtdEstoque()));
//						}
//				  }
//				}
	//		}else if(cbStatusSaldo.getValue().toString().equals("COM SALDO")){
//				container.addContainerFilter(Filters.not(Filters.eq("qtdEstoque", 0)));	
	//			container.addContainerFilter(new Greater("qtdEstoque", 0));
	//		}
	//	}
		
		container.addNestedContainerProperty("grupoId.nome_grupo");
		container.addNestedContainerProperty("marcasId.nome");
		container.addNestedContainerProperty("unidadeProduto.nome");
		container.addNestedContainerProperty("fornecedorId.razao_social");
		
		container.addNestedContainerProperty("cst_origem.cst_origem_cod");
		container.addNestedContainerProperty("cst_forma_tributo.cst_tributo_cod");
		container.addNestedContainerProperty("simples_nacional.simples_nacional_cod");
		container.addNestedContainerProperty("ecf_cup_filcal.ecf_emissor_cod");
		container.addNestedContainerProperty("tipo_item.nome");
	
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("valorVenda") || colId.equals("valorCusto") || colId.equals("custo_total") || colId.equals("lucro") || colId.equals("preco_promocional")){					
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
						return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
					}
				}else if(colId.equals("margemLucro") ){
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
						return Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString())+" %";
					}
				}else if(colId.equals("id")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				 }else if(colId.equals("nome")){
					 
					 if(tb.getItem(rowId).getItemProperty("preco_promocional").getValue() != null && new Float(tb.getItem(rowId).getItemProperty("preco_promocional").getValue().toString()) > 0){
						 return (tb.getItem(rowId).getItemProperty("nome").getValue()).toString()+" (PROMOCIONAL)";
					 }else{
						 return (tb.getItem(rowId).getItemProperty("nome").getValue()).toString();
					 }
					 
				 }else if(colId.equals("produto_ativado")){
					 
					 if(tb.getItem(rowId).getItemProperty("produto_ativado").getValue() != null && tb.getItem(rowId).getItemProperty("produto_ativado").getValue().equals("SIM")){
						 return "V";
					 }
						 
				 }else{
					 
					return super.formatPropertyValue(rowId, colId, property);
				 }

				return "";
			
				
			}
		};	
		tb.setNullSelectionAllowed(false); 
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.getVisibleColumns();
		
		tb.setVisibleColumns(new Object[] {"id","referencia","nome","produto_ativado","qtdEstoque","qtdEstoqueDeposito","estoqueMin","dataEstoque",
				"dataAlteracao","status","gTin","tipo_item.nome","marcasId.nome","peso","garantia","grupoId.nome_grupo",
				"unidadeProduto.nome","fornecedorId.razao_social","ncm","cst_origem.cst_origem_cod","cst_forma_tributo.cst_tributo_cod",
				"simples_nacional.simples_nacional_cod","ecf_cup_filcal.ecf_emissor_cod","iat","ippt","valorCusto","custo_total","valorVenda","preco_promocional","lucro","margemLucro",
				"outras_tb_desc","utilizaSeriais","fracionar","aval_cliente","ultima_compra","cest","ajustado","data_validade"});
		
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("gTin", "Código de Barras");
		tb.setColumnHeader("nome", "Produto");
		tb.setColumnHeader("produto_ativado", "E-commerce");
		tb.setColumnHeader("valorVenda", "Valor Venda");
		tb.setColumnHeader("preco_promocional", "Preço Promocional");
		tb.setColumnHeader("qtdEstoque", "Saldo Loja");
		tb.setColumnHeader("qtdEstoqueDeposito", "Saldo Deposito");
		tb.setColumnHeader("estoqueMin", "Quantidade Mínima");
		tb.setColumnHeader("grupoId.nome_grupo", "Grupo");
		tb.setColumnHeader("dataEstoque", "Data Cadastro");
		tb.setColumnHeader("dataAlteracao", "Data Alteração");
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("referencia", "Referência");
		tb.setColumnHeader("tipo_item.nome", "Tipo de Item");
		tb.setColumnHeader("marcasId.nome", "Marca");
		tb.setColumnHeader("peso", "Peso");
		tb.setColumnHeader("garantia", "Garantia");
		tb.setColumnHeader("unidadeProduto.nome", "Unidade");
		tb.setColumnHeader("fornecedorId.razao_social", "Fornecedor");
		tb.setColumnHeader("cst_origem.cst_origem_cod", "Origem");
		tb.setColumnHeader("cst_forma_tributo.cst_tributo_cod", "CST");
		tb.setColumnHeader("simples_nacional.simples_nacional_cod", "CSONS");
		tb.setColumnHeader("ecf_cup_filcal.ecf_emissor_cod", "CST-ECF");
		tb.setColumnHeader("ncm", "NCM");
		tb.setColumnHeader("ippt", "IPPT");
		tb.setColumnHeader("iat", "IAT");
		tb.setColumnHeader("valorCusto", "Valor Custo");
		tb.setColumnHeader("ultima_compra", "Ultima Compra");
		tb.setColumnHeader("outras_tb_desc", "Permitir Outras Tabelas");
		tb.setColumnHeader("utilizaSeriais", "Utiliza Seriail");
		tb.setColumnHeader("fracionar", "Fracionar");
		tb.setColumnHeader("aval_cliente", "Avaliação dos Cliente");
		tb.setColumnHeader("custo_total", "Valor Custo Total");
		tb.setColumnHeader("lucro", "Valor Lucro");
		tb.setColumnHeader("margemLucro", "Margem Lucro");
		tb.setColumnHeader("cest", "CEST");
		tb.setColumnHeader("ajustado", "Ajustado");
		tb.setColumnHeader("data_validade", "Data de Validade");
		

		tb.setColumnAlignment("valorCusto", Align.RIGHT);
		tb.setColumnAlignment("custo_total", Align.RIGHT);
		tb.setColumnAlignment("lucro", Align.RIGHT);
		tb.setColumnAlignment("margemLucro", Align.RIGHT);
		tb.setColumnAlignment("valorVenda", Align.RIGHT);
		tb.setColumnAlignment("qtdEstoque", Align.CENTER);
		//cod
		//ref
		//produto
		//saldo
		//valor venda
		//ultima compra
		if(OpusERP4UI.getEmpresa().getId() == 4){
			tb.setColumnCollapsingAllowed(true);		
			tb.setColumnCollapsed("ajustado", true);
			tb.setColumnCollapsed("preco_promocional", true);
			tb.setColumnCollapsed("qtdEstoqueDeposito", true);
			tb.setColumnCollapsed("produto_ativado", true);
			tb.setColumnCollapsed("custo_total", true);
			tb.setColumnCollapsed("lucro", true);
			tb.setColumnCollapsed("margemLucro", true);		
			tb.setColumnCollapsed("estoqueMin", true);
			tb.setColumnCollapsed("grupoId.nome_grupo", true);
			tb.setColumnCollapsed("dataEstoque", true);
			tb.setColumnCollapsed("dataAlteracao", true);
			tb.setColumnCollapsed("status", true);
			tb.setColumnCollapsed("gTin", true);
			tb.setColumnCollapsed("tipo_item.nome", true);
			tb.setColumnCollapsed("marcasId.nome", true);
			tb.setColumnCollapsed("peso", true);
			tb.setColumnCollapsed("garantia", true);
			tb.setColumnCollapsed("unidadeProduto.nome", true);
			tb.setColumnCollapsed("fornecedorId.razao_social", true);
			tb.setColumnCollapsed("cst_origem.cst_origem_cod", true);
			tb.setColumnCollapsed("cst_forma_tributo.cst_tributo_cod", true);	
			tb.setColumnCollapsed("ncm", true);
			tb.setColumnCollapsed("grupoId.nome_grupo", true);
			tb.setColumnCollapsed("aval_cliente", true);
			tb.setColumnCollapsed("fracionar", true);
			tb.setColumnCollapsed("utilizaSeriais", true);
			tb.setColumnCollapsed("outras_tb_desc", true);
			tb.setColumnCollapsed("valorCusto", true);
			tb.setColumnCollapsed("iat", true);
			tb.setColumnCollapsed("ippt", true);
			tb.setColumnCollapsed("ncm", true);
			tb.setColumnCollapsed("ecf_cup_filcal.ecf_emissor_cod", true);
			tb.setColumnCollapsed("simples_nacional.simples_nacional_cod", true);
			tb.setColumnCollapsed("comissao", true);
			tb.setColumnCollapsed("cest", true);
		}else{
			tb.setColumnCollapsingAllowed(true);		
			tb.setColumnCollapsed("custo_total", true);
			tb.setColumnCollapsed("lucro", true);
			tb.setColumnCollapsed("margemLucro", true);		
			tb.setColumnCollapsed("estoqueMin", true);
			tb.setColumnCollapsed("grupoId.nome_grupo", true);
			tb.setColumnCollapsed("dataEstoque", true);
			tb.setColumnCollapsed("dataAlteracao", true);
			tb.setColumnCollapsed("status", true);
			tb.setColumnCollapsed("gTin", true);
			tb.setColumnCollapsed("tipo_item.nome", true);
			tb.setColumnCollapsed("marcasId.nome", true);
			tb.setColumnCollapsed("peso", true);
			tb.setColumnCollapsed("garantia", true);
			tb.setColumnCollapsed("unidadeProduto.nome", true);
			tb.setColumnCollapsed("fornecedorId.razao_social", true);
			tb.setColumnCollapsed("cst_origem.cst_origem_cod", true);
			tb.setColumnCollapsed("cst_forma_tributo.cst_tributo_cod", true);	
			tb.setColumnCollapsed("ncm", true);
			tb.setColumnCollapsed("grupoId.nome_grupo", true);
			tb.setColumnCollapsed("aval_cliente", true);
			tb.setColumnCollapsed("fracionar", true);
			tb.setColumnCollapsed("utilizaSeriais", true);
			tb.setColumnCollapsed("outras_tb_desc", true);
			tb.setColumnCollapsed("valorCusto", true);
			tb.setColumnCollapsed("iat", true);
			tb.setColumnCollapsed("ippt", true);
			tb.setColumnCollapsed("ncm", true);
			tb.setColumnCollapsed("ecf_cup_filcal.ecf_emissor_cod", true);
			tb.setColumnCollapsed("simples_nacional.simples_nacional_cod", true);
			tb.setColumnCollapsed("comissao", true);
			tb.setColumnCollapsed("cest", true);
		}
		
		
		
		
		tb.setImmediate(true);	
		tb.setColumnCollapsible("nome", false);
		
		tb.sort(new Object[]{"nome"}, new boolean[]{true});
		
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btVisualizar.setEnabled(true);
					btExcluir.setEnabled(true);
					btCopiar.setEnabled(true);
					btEstoque.setEnabled(true);					
					btHistorico.setEnabled(true);
					btAlterar.setEnabled(true);
//					btSerial.setEnabled(true);
//					btLojaIntegrada.setEnabled(true);
				}else{	
					btVisualizar.setEnabled(false);
					btExcluir.setEnabled(false);
					btCopiar.setEnabled(false);
					btEstoque.setEnabled(false);
					btHistorico.setEnabled(false);
					btAlterar.setEnabled(false);
//					btSerial.setEnabled(false);
//					btLojaIntegrada.setEnabled(false); 
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
			
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()   && tb.getValue() != null){
					btVisualizar.click();
				}				
			}
		});
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {
                
                if (propertyId == null)
                    return "row-header-default"; // Will not actually be visible
               
                Item item = source.getItem(itemId); 
                
                if(OpusERP4UI.getEmpresa().getId() != 4 && item != null){
                	
                	Float qtd = (Float) item.getItemProperty("qtdEstoque").getValue();
                	Float qtdDeposito = (Float) item.getItemProperty("qtdEstoqueDeposito").getValue();
                	
	                if(source.getItem(itemId).getItemProperty("preco_promocional").getValue() != null && !source.getItem(itemId).getItemProperty("preco_promocional").getValue().toString().equals("0.0") && qtd != null && qtdDeposito != null && qtd <= (new Float(0.0)) && qtdDeposito <= (new Float(0.00))){                		
	                	
	                	Double vlrPromo = new Double( source.getItem(itemId).getItemProperty("preco_promocional").getValue().toString());
	                	
	                	if(vlrPromo > 0){
	                		return "row-header-produto-sem-saldo-promocional";
	                	}
	                	                	                
	                }
	                
	                if(source.getItem(itemId).getItemProperty("preco_promocional").getValue() != null ){                		
	                	
	                	Double vlrPromo = new Double( source.getItem(itemId).getItemProperty("preco_promocional").getValue().toString());
	                	
	                	if(qtd <= 0 && qtdDeposito >= 1){
	                		return "row-header-reposicao-promo";
	                	}else{	                		
	                		if(vlrPromo > 0){	                	
	                			return "row-header-end-principal";
	                		}                	                
	                	}
	                }
	                
	                if(qtd <= 0 && qtdDeposito >= 1){
	                
	                	  return "row-header-reposicao";	                	
	                	
	                }
	                
	                
	                
                }
                	
                
                
                if(item != null && item.getItemProperty("qtdEstoque").getValue() != null){
                	
                	 if(OpusERP4UI.getEmpresa().getId() != 4){
 
		                	Float qtd = (Float) item.getItemProperty("qtdEstoque").getValue();
		                	Float qtdDeposito = (Float) item.getItemProperty("qtdEstoqueDeposito").getValue();
		                	
			                if(qtd != null && qtdDeposito != null && qtd <= (new Float(0.0)) && qtdDeposito <= (new Float(0.00))){
				                return "row-header-produto-sem-saldo"; 		          
			                }else{
			                    return "row-header-default"; 
			                }
                	 }else{
                		 return "row-header-default";
                	 }
	                
                }else{
                	 return "row-header-default";
                }
            }            
        });
	
		return tb;
	}
	

	@Override
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setInputPrompt("BUSCAR...");
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}

	

	@Override
	public void addFilter(String s) {
		container.setApplyFiltersImmediately(false);
		container.removeAllContainerFilters();
		container.setQueryModifierDelegate(null);
		
		container.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));	
		
		if(cbGrupos != null && cbGrupos.getValue() != null && !cbGrupos.getValue().toString().equals("TODOS OS GRUPO")){
			GrupoProduto grupo = GrupoProdutoDAO.findbyName(cbGrupos.getValue().toString());
			if(grupo != null){
				container.addContainerFilter(Filters.eq("grupoId", grupo));
			}
		}
		
		if(cbStatusSaldo != null && cbStatusSaldo.getValue() != null){
			if(cbStatusSaldo.getValue().toString().equals("SEM SALDO")){
	
				container.addContainerFilter(new LessOrEqual("qtdEstoque", 0));
				
			}
			else if(cbStatusSaldo.getValue().toString().equals("PONTO DE PEDIDO")){	
				
				//container.addContainerFilter(Filters.eq("ponto_de_pedido", "SIM"));
				
				
				
				container.setQueryModifierDelegate(new QueryModifierDelegate() {
					
					@Override
					public void queryWillBeBuilt(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void queryHasBeenBuilt(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void orderByWillBeAdded(CriteriaBuilder arg0, CriteriaQuery<?> arg1,
							List<Order> arg2) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void orderByWasAdded(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void filtersWillBeAdded(CriteriaBuilder arg0, CriteriaQuery<?> arg1,
							List<Predicate> arg2) {
							Root<?> fromPerson = arg1.getRoots().iterator().next();

				        
							Path<Float> age = fromPerson.<Float>get("estoqueMin");
							Path<Float> qtdEstoque = fromPerson.<Float>get("qtdEstoque");
				        
							arg2.add(arg0.lessThanOrEqualTo(qtdEstoque, age));
					}
					
					@Override
					public void filtersWereAdded(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
			
			else if(cbStatusSaldo.getValue().toString().equals("COM SALDO")){
	
				container.addContainerFilter(new Greater("qtdEstoque", 0));
			}	
			
			else if(cbStatusSaldo.getValue().toString().equals("NECESSITA REPOSIÇÃO")){
				container.addContainerFilter(new LessOrEqual("qtdEstoque", 0));
				container.addContainerFilter(new Greater("qtdEstoqueDeposito", 0));
			}
		}
		
		if(cbTipoItem != null && cbTipoItem.getValue() != null){
			
			EntityItem<TipoItemProduto> entityItem = (EntityItem<TipoItemProduto>)cbTipoItem.getItem(cbTipoItem.getValue());
			container.addContainerFilter(Filters.eq("tipo_item.nome", entityItem.getEntity().getNome()));
		}
		
		if(cbStatus != null && cbStatus.getValue() != null){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue()));
		}
		
		if(s != null && !s.equals("") && !s.isEmpty()){
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.valueOf(s);
			DateTime date = null ;
			
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
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date date = sdf.parse(s);

					for(Object c:collums){		 
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
							filtros.add(Filters.eq(c.toString(), date));
						}			
					}	
				
			} catch (Exception e2) {
				
				try {
					Float vl = Real.formatStringToDBFloat(s);
									
					for(Object c:collums){		 
						
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Float.class){					   	
							filtros.add(Filters.eq(c.toString(), vl));
						}			
					}	
					
				} catch (Exception e3) {
					
					for(Object c:collums){		 
						
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));
						}	
						
						if(s.equals("PROMOCIONAL")){
							filtros.add(Filters.gt("preco_promocional", new Float(0)));
						}
						
					}
				}
			}
		}		
		container.addContainerFilter(Filters.or(filtros));
	  }
		container.applyFilters();
		
		if(lbRegistros != null){
			hlFloat.replaceComponent(lbRegistros, lbRegistros());
		}
	 }
	
	
		
	private Button btAtualizarCodigo;
	public Button BuildbtAtualizarCodigoBarras(){
		btAtualizarCodigo = new Button("Atualizar Código de Barras", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "AtualizarCodigoBarras"))				
				{
					
									
					final ProdutoEditor cEditor = new ProdutoEditor(tb.getItem(tb.getValue()), "Atualizar Código Barras", true, true,false,false,null);
					cEditor.addListerner(new ProdutoEditor.ProdutoListerner() {
						
						@Override
						public void onClose(ProdutoEvent event) {
							if(event.isConfirm()){
								
								try {
																			
									tb.commit();
									container.commit();
									cEditor.close();
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Atualizou um Código de Barras do Produto"));
									
									Notify.Show("Código de Barras Atualizado Com Sucesso!", Notify.TYPE_SUCCESS);
								} catch (Exception e) {
									e.printStackTrace();															
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);								
								}
								
							}
						}

						
					});
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();
							//refresh();
						}
					});
					
					getUI().addWindow(cEditor);
				
				}else{					
					Notify.Show("Você não Possui Permissão para Atualizar um Código de Barras de  Produto", Notify.TYPE_ERROR);
				}
			}
		});

		btAtualizarCodigo.setEnabled(false);
		return btAtualizarCodigo;
	}
	
	public Button BuildBtImportar(){
		Button btImportar = new Button("Importar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				ProdutoImportarUtil produtoImpUtil = new ProdutoImportarUtil(true, true);
				produtoImpUtil.addListerner(new ProdutoImportarUtil.ProdutoListerner() {
					
					@Override
					public void onSelected(
							com.digital.opuserp.view.util.ProdutoImportarUtil.ProdutoEvent event) {
						
						if(event.getProduto() != null){
							
							final Produto produtoNovo = event.getProduto();
							produtoNovo.setUltima_compra(null);
							produtoNovo.setEmpresaId(OpusERP4UI.getEmpresa());
							produtoNovo.setQtdEstoque(new Float(0));
							produtoNovo.setQtdEstoqueDeposito(new Float(0));
							produtoNovo.setQtd((long)0);
							produtoNovo.setId(null);
							produtoNovo.setDataAlteracao(new Date());
							produtoNovo.setDataEstoque(new Date());
							
							ProdutoDAO.saveProduto(produtoNovo);
							container.refresh();
							
							final BeanItem<Produto> bItemProduto = new BeanItem<Produto>(produtoNovo);
							
							final ProdutoEditor cEditor = new ProdutoEditor(bItemProduto, "Editar Produto", true,false,false,true,codSubModulo);
							cEditor.addListerner(new ProdutoEditor.ProdutoListerner() {
								
								@Override
								public void onClose(ProdutoEvent event) {
									if(event.isConfirm()){
									
										try{
											container.commit();									
											tb.focus();	
											//refresh();
											
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Produto"));
											AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "ALTEROU DE PRODUTO", bItemProduto.getBean(),OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
											Icms icms = event.getIcms();
											Ipi ipi = event.getIpi();
											Pis pis = event.getPis();
											Cofins cofins = event.getCofins();
											
									
											
											//Produto pAtualizado = ProdutoDAO.find(itemProduto.getEntity().getId());
											//itemProduto.getEntity().setQtd(pAtualizado.getQtd());
											
											ProdutoDAO.saveProduto(bItemProduto.getBean());
											
											icms.setProduto(bItemProduto.getBean());
											ipi.setProduto(bItemProduto.getBean());
											pis.setProduto(bItemProduto.getBean());
											cofins.setProduto(bItemProduto.getBean());
											
											
											IcmsDAO.save(icms);
											IpiDAO.save(ipi); 
											PisDAO.save(pis); 
											CofinsDAO.save(cofins); 
											
											Notify.Show("Produto Importado Com Sucesso!", Notify.TYPE_SUCCESS);		
											
											container.refresh();
										
										} catch (Exception e) {
											e.printStackTrace();															
											Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);								
										}
									}
								}
							});
							cEditor.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									tfBusca.focus();							
								}
							});
							
							getUI().addWindow(cEditor);
							
							
						}
					}
				});
				
				getUI().addWindow(produtoImpUtil);
			}
		});
		
		return btImportar;
	}
	
	public Button BuildbtAtualizarProdutos(){
		btAtualizarProdutos = new Button("Atualizar Produtos", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				EntityManager em = ConnUtil.getEntity();
				Query q = em.createQuery("select p from Produto p where p.sic_loja=true", Produto.class);
				
				List<Produto> lista_produtos_com_erro = new ArrayList<>();
				
				List<Produto> lista_produtos = q.getResultList();
				for (Produto p : lista_produtos) {
					
					System.out.println("ATUALIZANDO PRODUTOS NA LOJA INTEGRADA;;;;;;;;;;;;;;;;;;;;;;;;;;;");
								
//					String result = LojaIntegradaDAO.alterarProduto(p.getCod_pro_loja(), p.getNome(), p.getDesc_produto(), p.getProduto_ativado(), 
//							p.getProduto_destaque(), p.getGrupoId().getId().toString(), p.getGrupoId().getNome_grupo(), 
//							p.getUrl_video_youtube(),p.getId().toString());
					
					PedidoDAO.sincronizarEstoque(p.getCod_pro_loja(), p.getQtdEstoque().toString());
					
//					if(result == null){
//						lista_produtos_com_erro.add(p);
//					}
					
					
					System.out.println(p.getNome()+" --- ATUALIZADO!");
				}
				
				
				for (Produto p : lista_produtos_com_erro) {
					
					System.out.println("ATUALIZANDO PRODUTOS QUE DERAM ERRO;;;;;;;;;;;;;;;;;;;;;;;;;;");
								
//					String result = LojaIntegradaDAO.alterarProduto(p.getCod_pro_loja(), p.getNome(), p.getDesc_produto(), p.getProduto_ativado(), 
//							p.getProduto_destaque(), p.getGrupoId().getId().toString(), p.getGrupoId().getNome_grupo(), 
//							p.getUrl_video_youtube(),p.getId().toString());
					
					PedidoDAO.sincronizarEstoque(p.getCod_pro_loja(), p.getQtdEstoque().toString());
					
//					if(result == null){
//						lista_produtos_com_erro.add(p);
//					}
					
					
					System.out.println(p.getNome()+" --- ATUALIZADO!");
				}
				
				System.out.println("ATUALIZACAO CONCLUIDA;;;;;;;;;;;;;;;;;;;;;;;;;;;");
				
				
				
//				EntityManager em = ConnUtil.getEntity();
//				
//				Query q = em.createQuery("select p from Produto p where p.empresaId=1 or p.empresaId=5", Produto.class);
//				//q.setParameter("g", new GrupoProduto(13));
//				
//				for (Produto p: (List<Produto>)q.getResultList()) {
//					
//					
//					
//					em.getTransaction().begin();
//					
//					if(p.getGrupoId() != null){
//						if(p.getGrupoId().getId() == 2 || p.getGrupoId().getId() == 13 || p.getGrupoId().getId() == 23 || p.getGrupoId().getId() == 34 || p.getGrupoId().getId() == 35 || p.getGrupoId().getId() == 39 || p.getGrupoId().getId() == 54 || p.getGrupoId().getId() == 58 || p.getGrupoId().getId() == 65 || p.getGrupoId().getId() == 69 ||p.getGrupoId().getId() == 71 || p.getGrupoId().getId() == 74 || p.getGrupoId().getId() == 75 || p.getGrupoId().getId() == 80){
//							em.persist(new CatLojaProduto(null, p, new CategoriaLojaIntegrada(1)));
//						}
//						
//						if(p.getGrupoId().getId() == 22 || p.getGrupoId().getId() == 37 || p.getGrupoId().getId() == 38 || p.getGrupoId().getId() == 44 || p.getGrupoId().getId() == 52 || p.getGrupoId().getId() == 78 || p.getGrupoId().getId() == 95 || p.getGrupoId().getId() == 17 || p.getGrupoId().getId() == 19 || p.getGrupoId().getId() == 36 || p.getGrupoId().getId() == 46 || p.getGrupoId().getId() == 47 || p.getGrupoId().getId() == 51 || p.getGrupoId().getId() == 56 || p.getGrupoId().getId() == 60 || p.getGrupoId().getId() == 61 || p.getGrupoId().getId() == 62 || p.getGrupoId().getId() == 63 || p.getGrupoId().getId() == 64 || p.getGrupoId().getId() == 77 || p.getGrupoId().getId() == 79){
//							em.persist(new CatLojaProduto(null, p, new CategoriaLojaIntegrada(2)));
//						}
//						
//						if(p.getGrupoId().getId() == 43 || p.getGrupoId().getId() == 45 || p.getGrupoId().getId() == 1 || p.getGrupoId().getId() == 6 || p.getGrupoId().getId() == 7 || p.getGrupoId().getId() == 11 || p.getGrupoId().getId() == 40 || p.getGrupoId().getId() == 49 || p.getGrupoId().getId() == 50 || p.getGrupoId().getId() == 57 || p.getGrupoId().getId() == 67 || p.getGrupoId().getId() == 68 || p.getGrupoId().getId() == 76 || p.getGrupoId().getId() == 82 ){
//							em.persist(new CatLojaProduto(null, p, new CategoriaLojaIntegrada(3)));
//						}
//						
//						if(p.getGrupoId().getId() == 16 || p.getGrupoId().getId() == 48){
//							em.persist(new CatLojaProduto(null, p, new CategoriaLojaIntegrada(4)));
//						}
//						
//						if(p.getGrupoId().getId() == 18 || p.getGrupoId().getId() == 20 || p.getGrupoId().getId() == 21 || p.getGrupoId().getId() == 27 || p.getGrupoId().getId() == 59 || p.getGrupoId().getId() == 66 || p.getGrupoId().getId() == 72){
//							em.persist(new CatLojaProduto(null, p, new CategoriaLojaIntegrada(5)));
//						}
//						
//						if(p.getGrupoId().getId() == 12 || p.getGrupoId().getId() == 53 || p.getGrupoId().getId() == 55 || p.getGrupoId().getId() == 70 || p.getGrupoId().getId() == 94 || p.getGrupoId().getId() == 154 || p.getGrupoId().getId() == 159 || p.getGrupoId().getId() == 153 || p.getGrupoId().getId() == 161 || p.getGrupoId().getId() == 163){
//							em.persist(new CatLojaProduto(null, p, new CategoriaLojaIntegrada(6)));
//						}
//						
//						if(p.getGrupoId().getId() == 8 || p.getGrupoId().getId() == 14 || p.getGrupoId().getId() == 32 || p.getGrupoId().getId() == 42 || p.getGrupoId().getId() == 73 || p.getGrupoId().getId() == 81 || p.getGrupoId().getId() == 41 || p.getGrupoId().getId() == 83 ){
//							em.persist(new CatLojaProduto(null, p, new CategoriaLojaIntegrada(7)));
//						}
//					}
//					em.getTransaction().commit();
//				}
				
				
				
			}
		});
		
		return btAtualizarProdutos;
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
					
					final BeanItem<Produto> newItem = new BeanItem<Produto>(new Produto());
									
					final ProdutoEditor cEditor = new ProdutoEditor(newItem, "Cadastrar Produto", true,false,false,false,null);
					cEditor.addListerner(new ProdutoEditor.ProdutoListerner() {
						
						@Override
						public void onClose(ProdutoEvent event) {
							if(event.isConfirm()){
								
								try {
									Produto cAdd = newItem.getBean();									
									cAdd.setEmpresaId(OpusERP4UI.getEmpresa());
									ProdutoDAO.insertProduto(cAdd);
									tb.focus();
									//refresh();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Produto"));								
									Notify.Show("Produto Cadastrado Com Sucesso!", Notify.TYPE_SUCCESS);									
									AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "CADASTRO DE PRODUTO", cAdd,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									
									Icms icms = event.getIcms();
									Ipi ipi = event.getIpi();
									Pis pis = event.getPis();
									Cofins cofins = event.getCofins();
																		
									icms.setProduto(cAdd);
									ipi.setProduto(cAdd);
									pis.setProduto(cAdd);
									cofins.setProduto(cAdd);
									
									IcmsDAO.save(icms);
									IpiDAO.save(ipi); 
									PisDAO.save(pis); 
									CofinsDAO.save(cofins);
									
									refresh();
								
								} catch (Exception e) {
									e.printStackTrace();															
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);								
								}
							}
						}

						
					});
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();		
						}
					});
					
					getUI().addWindow(cEditor);
				
				}else{					
					Notify.Show("Você não Possui Permissão para Cadastrar um Produto", Notify.TYPE_ERROR);
				}
			}
		});

		return btNovo;
	}

	@Override
	public Button BuildbtAceitar() {
		btVisualizar = new Button("Visualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar"))				
				{
					
					//addExtension(buildRefresher());
				
					Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
					final Produto produto = ProdutoDAO.find(codProduto);
		
					final ProdutoEditor cEditor = new ProdutoEditor(tb.getItem(tb.getValue()), "Editar Produto", true,false,false,true,codSubModulo);
					cEditor.addListerner(new ProdutoEditor.ProdutoListerner() {
						
						@Override
						public void onClose(ProdutoEvent event) {
							if(event.isConfirm()){
							
								try{
									container.commit();									
									tb.focus();	
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Produto"));
									AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "ALTEROU DE PRODUTO", produto,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									Icms icms = event.getIcms();
									Ipi ipi = event.getIpi();
									Pis pis = event.getPis();
									Cofins cofins = event.getCofins();
									
									EntityItem<Produto> itemProduto = (EntityItem<Produto>)event.getItem();
									
									
									icms.setProduto(itemProduto.getEntity());
									ipi.setProduto(itemProduto.getEntity());
									pis.setProduto(itemProduto.getEntity());
									cofins.setProduto(itemProduto.getEntity());
									
									IcmsDAO.save(icms);
									IpiDAO.save(ipi); 
									PisDAO.save(pis); 
									CofinsDAO.save(cofins); 
									
									String cod_loja = itemProduto.getEntity().getCod_pro_loja() != null && !itemProduto.getEntity().getCod_pro_loja().equals("") ? itemProduto.getEntity().getCod_pro_loja() : null;
									
									if(cod_loja != null && OpusERP4UI.getEmpresa().getId() != 6&& OpusERP4UI.getEmpresa().getId() != 4){
//										LojaIntegradaDAO.alterarProduto(cod_loja, itemProduto.getEntity().getNome(), itemProduto.getEntity().getDesc_produto(), itemProduto.getEntity().getProduto_ativado(), 
//												itemProduto.getEntity().getProduto_destaque(), itemProduto.getEntity().getGrupoId().getId().toString(), itemProduto.getEntity().getGrupoId().getNome_grupo(), 
//												itemProduto.getEntity().getUrl_video_youtube(),itemProduto.getEntity().getId().toString());
//										
//										
//										LojaIntegradaDAO.alterarPrecos(
//												cod_loja, 
//												itemProduto.getEntity().getValorVenda().toString(), 
//												itemProduto.getEntity().getValorVenda().toString(),
//												itemProduto.getEntity().getPreco_promocional() != null ? itemProduto.getEntity().getPreco_promocional().toString() : itemProduto.getEntity().getValorVenda().toString());
//										
									}
									
									
									if(cod_loja == null && OpusERP4UI.getEmpresa().getId() != 6&& OpusERP4UI.getEmpresa().getId() != 4){
										
										String cod = itemProduto.getEntity().getId().toString();
																			
										
									}
									

									ProdutoDAO.saveProduto(itemProduto.getEntity());
									Notify.Show("Produto Alterado Com Sucesso!", Notify.TYPE_SUCCESS);		
									
								} catch (Exception e) {
									e.printStackTrace();															
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);								
								}
							}
						}
					});
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();							
						}
					});
					
					getUI().addWindow(cEditor);
				}else{					
					Notify.Show("Você não Possui Permissão para Editar um Produto", Notify.TYPE_ERROR);
				}
			}
		});
		btVisualizar.setEnabled(false);
		btVisualizar.setDisableOnClick(true); 
		
		btVisualizar.addShortcutListener(new ShortcutListener("Enter",ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btVisualizar.click();
				btVisualizar.setEnabled(false);
				//addExtension(buildRefresher());
			}
		});
					
		return btVisualizar;
	}
	
	

	
//	Refresher refresher; 
//	public Refresher buildRefresher(){		
//		
//		refresher = new Refresher();
//		refresher.setRefreshInterval(3000);
//		refresher.addListener(new Refresher.RefreshListener() {
//			
//			@Override
//			public void refresh(Refresher source) {
//				btVisualizar.setEnabled(true);
//				btVisualizar.addShortcutListener(buildShortCutEditar());
//				removeExtension(source); 
//				//source.remove();
//			}
//		});
//		
//		return refresher;
//	}
	
	
	public Button BuildbtExportar() {
		btExportar = new Button("Exportar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Exportar"))				
				{				
					Exportar();			
				}else{					
					Notify.Show("Você não Possui Permissão para Exportar Produtos", Notify.TYPE_ERROR);
				}
				
			}
		});
		//btGerarDBF.setEnabled(false);
		return btExportar;
	}
	
	public Button BuildbtGerarDBF() {
		btGerarDBF = new Button("Gerar DBF", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "GerarDBF"))				
				{
				
					
						GenericDialog gDialog = new GenericDialog("Deseja continuar?", "Um arquivo .DBF será gerado, o processo pode ser lento", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									gerarDBF();
									
									Notify.Show("Arquivo DBF gerado com Sucesso!", Notify.TYPE_SUCCESS);														
									tb.focus();	
									//refresh();
									//LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Produto"));
								}							
							}
						});
									
						getUI().addWindow(gDialog);
						
				
				}else{					
					Notify.Show("Você não Possui Permissão para Gerar DBF de Produtos", Notify.TYPE_ERROR);
				}
				
			}
		});
		//btGerarDBF.setEnabled(false);
		return btGerarDBF;
	}
	
	public void gerarDBF(){
				try {
					JDBField[] fields = {
								new JDBField("CODBARRA", 'C', 50, 0),
								new JDBField("DESCRICAO", 'C', 200, 0),
								new JDBField("UNIDADE", 'C', 8, 0),
								new JDBField("EMBALAGEM", 'C', 8, 0),
								new JDBField("CODGRUPO", 'C', 8, 0),
								new JDBField("ALIQUOTA", 'F', 8, 0),
								new JDBField("TIPOTRIB", 'C', 8, 0),
								new JDBField("PRECOVENDA", 'F', 8, 0),
								new JDBField("DESCMAXIMO", 'C', 8, 0),
								new JDBField("CODRETAG", 'C', 8, 0),
								new JDBField("SITUACAO", 'C', 8, 0),
								new JDBField("PESADO", 'C', 8, 0),
								new JDBField("UNIDADE2", 'C', 8, 0),
								new JDBField("EMBALAGEM2", 'C', 8, 0),
								new JDBField("PRECO2", 'C', 8, 0),
								new JDBField("UNIDADE3", 'C', 8, 0),
								new JDBField("EMBALAGEM3", 'C', 8, 0),
								new JDBField("PRECO3", 'C', 8, 0),
								new JDBField("UNIDADE4", 'C', 8, 0),
								new JDBField("EMBALAGEM4", 'C', 8, 0),
								new JDBField("PRECO4", 'C', 8, 0),
								new JDBField("ESTLIMITE", 'C', 8, 0),
								new JDBField("CODBARRA1", 'C', 8, 0),
								new JDBField("CODBARRA2", 'C', 8, 0),
								new JDBField("CODBARRA3", 'C', 8, 0),
								new JDBField("CODBARRA4", 'C', 8, 0),
								new JDBField("CODBARRA5", 'C', 8, 0)
					};
					
					DBFWriter write = new DBFWriter("/tmp/DINCASH.DBF", fields);
					
					List<Produto> produtos= ProdutoDAO.listProduto(OpusERP4UI.getEmpresa().getId());
					
					for (Produto p : produtos) {
						write.addRecord(new Object[]{
								p.getgTin(), 
								p.getNome(), 
								p.getUnidadeProduto() != null ? p.getUnidadeProduto().getNome() : "UN",
								"0.0",
								p.getGrupoId() != null ? p.getGrupoId().getId().toString() : "001",
								p.getDiferenca_aliquota(),
								p.getIat(),
								p.getValorVenda(),
								"0.00",
								"0001",
								"A",
								"N",
								"",
								"0.00",
								"0.00",
								"",
								"",
								"0.00",
								"0.00",
								"",
								"0.00",
								"0.00",
								"0",
								"",
								"",
								"",
								""
								});
					}
						
				    
					
					write.close();
					
					try{
						StreamSource ssZip = new StreamSource() {
							
				            InputStream is = new ByteArrayInputStream(readImageOldWay(new File("/tmp/DINCASH.DBF"))); 
				            @Override
				            public InputStream getStream() {
				                return is;
				            }
				        };
				        
				        StreamResource srZip = new StreamResource(ssZip, "DINCASH.DBF");					        
				        Page.getCurrent().open(srZip, "_blank", true);
					}catch(Exception e){
						e.printStackTrace();
					}
			} catch (JDBFException e) {
			e.printStackTrace();
			}
	}
	
	public byte[] readImageOldWay(File file) throws IOException
	{
	  //Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
	  if(file != null){
		  
		  InputStream is = new FileInputStream(file);
		  // Get the size of the file
		  long length = file.length();
		  // You cannot create an array using a long type.
		  // It needs to be an int type.
		  // Before converting to an int type, check
		  // to ensure that file is not larger than Integer.MAX_VALUE.
		  if (length > Integer.MAX_VALUE)
		  {
			  // File is too large
		  }
		  // Create the byte array to hold the data
		  byte[] bytes = new byte[(int) length];
		  // Read in the bytes
		  int offset = 0;
		  int numRead = 0;
		  while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
		  {
			  offset += numRead;
		  }
		  // Ensure all the bytes have been read in
		  if (offset < bytes.length)
		  {
			  throw new IOException("Could not completely read file " + file.getName());
		  }
		  // Close the input stream and return bytes
		  is.close();
		  return bytes;
	  }
	return null;
	}
	
private void Exportar(){
		
		SimpleDateFormat sdfAno = new SimpleDateFormat("yy");						
	       
        try{
        	
        	String nomeArquivo  = "PRODUTO.txt";        	
	        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();	
	        final File zipFile = new File(basepath + "/WEB-INF/uploads/"+nomeArquivo);	        
			BufferedWriter br = new BufferedWriter(new FileWriter(zipFile));
			
			String r = createFile();
			
			if(r != "ERRO"){			
			
					br.write(r);  
					br.close(); 
			        
					StreamSource ssZip = new StreamSource() {
			            InputStream is = new ByteArrayInputStream(readImageOldWay(zipFile)); 
			            @Override
			            public InputStream getStream() {
			                return is;
			            }
			        };
			        
			        StreamResource srZip = new StreamResource(ssZip, zipFile.getName());	        
			        srZip.setMIMEType("application/unknow");
			        
			        Page.getCurrent().open(srZip, "_blank", true);	        
			}

        }catch(Exception e){
        	e.printStackTrace();
        }				   
	}
	
	
	public String createFile(){	
		StringBuilder sb = new StringBuilder();
		String quebra = "\r\n";
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfAno = new SimpleDateFormat("yy");
		
		List<Produto> produtos = ProdutoDAO.listProduto(OpusERP4UI.getEmpresa().getId());
		Integer i = 0;
		for (Produto p: produtos) {
			
				i++;
			
				String fracionar = p.getFracionar() == 1 ? "S" : "N";
				
				sb.append(
						p.getId().toString()+"|"+
					    p.getgTin()+"|"+
					    p.getNome()+"||"+
					    p.getUnidadeProduto().getNome()+"|"+
					    String.format("%.3f", p.getValorVenda()).replace(",", ".")+
					    "|0|"+
					    p.getEcf_cup_filcal().getEcf_emissor_cod()+
					    "|||N|"+ 
					    fracionar +
					    "|S|S|N|"+
					    p.getGrupoId().getId().toString()+
					    "|"+p.getGrupoId().getNome_grupo()+"|||"+
				p.getMarcasId().getId().toString()+"|"+p.getMarcasId().getNome()+"|||0||"+
				p.getNcm()+"|0|"+p.getgTin()+"|||||||||N|"+p.getQtdEstoque());
				
				
				sb.append(quebra);
				
		}

		return sb.toString();
	}
	
	
	public Button BuildbtLojaIntegrada(){
		btLojaIntegrada = new Button("Loja Integrada", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				Integer cod = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
				Produto p = ProdutoDAO.find(cod);
				if(p != null){
					LojaIntegradaEditor loja = new LojaIntegradaEditor("Loja Integrada", p, true, codSubModulo);					
					getUI().addWindow(loja);
				}else{
					Notify.Show("Não foi possível buscar Produto!", Notify.TYPE_ERROR);
				}
				
			}
		});
		
		btLojaIntegrada.setEnabled(false); 
		return btLojaIntegrada;
	}

	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
				
					Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
					Produto produto = ProdutoDAO.find(codProduto);
					
					List<SerialProduto> serProd = SerialDAO.findByCodProd(codProduto);
					List<MovimentoEntDetalhe> moDet = ProdutoDAO.movEntDet(codProduto);
					List<EcfPreVendaDetalhe> EcfDet = ProdutoDAO.EntDet(codProduto);

					if(serProd==null && moDet ==null && EcfDet==null){
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Produto Selecionado?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									container.removeItem(tb.getValue());
									container.commit();
									Notify.Show("Produto Excluído com Sucesso!", Notify.TYPE_SUCCESS);														
									tb.focus();	
									//refresh();
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Produto"));
								}							
							}
						});
						gDialog.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tfBusca.focus();							
							}
						});					
						getUI().addWindow(gDialog);
						
					}else if(serProd!=null){
						Notify.Show("Produto contém Serial(s) Vinculado(s)", Notify.TYPE_ERROR);				
					}else if(moDet!=null){
						Notify.Show("Produto contém Entrada(s) Vinculada(s)", Notify.TYPE_ERROR);										
					}else if(EcfDet!=null){
						Notify.Show("Produto contém Saída(s) Vinculada(s)", Notify.TYPE_ERROR);																
					}
				}else{					
					Notify.Show("Você não Possui Permissão para Excluir um Produto", Notify.TYPE_ERROR);
				}
				
			}
		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}

	private Component BuildbtHistorico() {
		btHistorico = new Button("Histórico", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if (winSubMenuHistorico != null && winSubMenuHistorico.getUI() != null)
					winSubMenuHistorico.close();
					
					buildSubMenuHistorico(event);
					
					getUI().addWindow(winSubMenuHistorico);
					winSubMenuHistorico.focus();
					((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
						
						@Override
						public void layoutClick(LayoutClickEvent event) {
							winSubMenuHistorico.close();
							((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
						}
					});
				
			}
		});
		btHistorico.setEnabled(false);
		return btHistorico;
	}

	Window winSubMenuHistorico;
	private void buildSubMenuHistorico(ClickEvent event) {
		winSubMenuHistorico = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuHistorico.setContent(l);
        winSubMenuHistorico.setWidth("300px");
        winSubMenuHistorico.addStyleName("notifications");
        winSubMenuHistorico.setClosable(false);
        winSubMenuHistorico.setResizable(false);
        winSubMenuHistorico.setDraggable(false);
        winSubMenuHistorico.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuHistorico.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuHistorico.setCloseShortcut(KeyCode.ESCAPE, null);

    
        Button bt2 = new Button("Histórico de Entradas", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Historico de Entradas"))				
				{
				
					Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
	
					if(tb.getValue() != null && tb.getItem(tb.getValue()) != null){
						HistoricoEntradas historicoCompras = new HistoricoEntradas(true, true,codProduto);
					
						getUI().addWindow(historicoCompras);
					}
				
				}else{					
					Notify.Show("Você não Possui Permissão para visualisar Histórico do Produto", Notify.TYPE_ERROR);
				}
				
			}
		});
        
        bt2.setPrimaryStyleName("btSubMenu");
        
        Button bt3 = new Button("Histórico de Saídas", new Button.ClickListener() {
        	
        	@Override
        	public void buttonClick(ClickEvent event) {
        		
        		
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Historico de Saidas"))				
				{
        		
		        		Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
		
						if(tb.getValue() != null && tb.getItem(tb.getValue()) != null){
							HistoricoSaidas historicoVendas = new HistoricoSaidas(true, true,codProduto);
						
							getUI().addWindow(historicoVendas);
						}
        		
				}else{					
					Notify.Show("Você não Possui Permissão para visualisar Histórico do Produto", Notify.TYPE_ERROR);
				}
        		
        	}
        });
        
        bt3.setPrimaryStyleName("btSubMenu");
        l.addComponent(bt2);   
        l.addComponent(bt3);   
    }

	
	
	private Component BuildbtCorrecaoEstoque() {
		btEstoque = new Button("Correção Estoque", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (winSubMenuCorrecoes != null && winSubMenuCorrecoes.getUI() != null)
					winSubMenuCorrecoes.close();
					
					BuildbtCorrecoes(event);
					
					getUI().addWindow(winSubMenuCorrecoes);
					winSubMenuCorrecoes.focus();
					((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
						
						@Override
						public void layoutClick(LayoutClickEvent event) {
							winSubMenuCorrecoes.close();
							((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
						}
					});	
			}
		});
		btEstoque.setEnabled(false);
		return btEstoque;
	}
	
	Window winSubMenuCorrecoes;
	private void BuildbtCorrecoes(ClickEvent event) {
		winSubMenuCorrecoes = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuCorrecoes.setContent(l);
        winSubMenuCorrecoes.setWidth("300px");
        winSubMenuCorrecoes.addStyleName("notifications");
        winSubMenuCorrecoes.setClosable(false);
        winSubMenuCorrecoes.setResizable(false);
        winSubMenuCorrecoes.setDraggable(false);
        winSubMenuCorrecoes.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuCorrecoes.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuCorrecoes.setCloseShortcut(KeyCode.ESCAPE, null);

    
        Button bt1 = new Button("Adicionar Saldo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Adicionar Estoque"))				
				{
				
				Produto produto = null; 
				
				Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
				produto = ProdutoDAO.find(codProduto);			
				
				if(tb.getValue() != null && tb.getItem(tb.getValue()) != null && produto != null){
					
					CorrecaoSaldo correcaoSaldo = new CorrecaoSaldo(produto, "Adicionar Saldo no Estoque", true, true);
					getUI().addWindow(correcaoSaldo);	
					
					correcaoSaldo.addListerner(new CorrecaoSaldo.SerialListerner() {
						
						@Override
						public void onClose(SerialEvent event) {
						
							if(event.isConfirm()){				
								CompraDAO.save(event.getCompra());
								CompraDAO.saveItem(event.getMoviDet());
								novaQtd = event.getMoviDet().getQuantidade();	
								container.refresh();
								tb.focus();	
								
							
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Corrigiu o estoque"));
								AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "ADICIONOU NO ESTOQUE "+Real.formatDbToString(String.valueOf(novaQtd)), event.getProduto(),OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
							}											        		
						}
					});
					correcaoSaldo.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();							
						  }
					  });					
				   }
				}else{					
					Notify.Show("Você não Possui Permissão para Corrigir Estoque do Produto", Notify.TYPE_ERROR);
				}
			  }	
		});
        
        bt1.setPrimaryStyleName("btSubMenu");
        
        Button bt2 = new Button("Subtrair Saldo", new Button.ClickListener() {
        	
        	@Override
        	public void buttonClick(ClickEvent event) {
        		
        		
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Subtrair Estoque"))				
				{
        		
        		
        		Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
        		final Produto produto = ProdutoDAO.find(codProduto);
               		
				if(tb.getValue() != null && tb.getItem(tb.getValue()) != null){
					final CorrecaoSaldo correcaoSaldo = new CorrecaoSaldo(produto, "Subtrair Saldo no Estoque", true, false);
					
					getUI().addWindow(correcaoSaldo);	
					correcaoSaldo.addListerner(new CorrecaoSaldo.SerialListerner() {
						
						@Override
						public void onClose(SerialEvent event) {
						
							if(event.isConfirm()){				
								//PedidoDAO.save(event.getPedido());
								EntityManager em = ConnUtil.getEntity();
								em.getTransaction().begin();
								
								EcfPreVendaCabecalho pedido = event.getPedido();
								em.persist(pedido);
								event.getDetalhe().setEcfPreVendaCabecalhoId(pedido.getId());
								em.persist(event.getDetalhe());
								
								em.getTransaction().commit();
								
								//PedidoDAO.saveItem(event.getDetalhe());
								novaQtd = event.getDetalhe().getQuantidade();
								container.refresh();

								tb.focus();	
								//refresh();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Corrigiu o estoque"));
								AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "SUBTRAIU NO ESTOQUE "+Real.formatDbToString(String.valueOf(novaQtd)), produto,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));												
							}							
						}
						
					});
					correcaoSaldo.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();							
						}
					});					
				}
				
				}else{					
					Notify.Show("Você não Possui Permissão para Corrigir Estoque do Produto", Notify.TYPE_ERROR);
				}      		
        	 }
         });
        
        bt2.setPrimaryStyleName("btSubMenu");
        l.addComponent(bt1);   
        l.addComponent(bt2);   
    }


	


	private Component BuildbtCopiar() {
		
		btCopiar = new Button("Copiar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Copiar"))				
				{

				Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
				final Produto produto = ProdutoDAO.find(codProduto);
				final BeanItem<Produto> newItem = new BeanItem<Produto>(produto);
				
				final ProdutoEditor cEditor = new ProdutoEditor(newItem, "Copiar Produto", true,false,true,false,null);
				cEditor.addListerner(new ProdutoEditor.ProdutoListerner() {

						@Override
						public void onClose(ProdutoEvent event) {
							if(event.isConfirm()){					
									try {
											
										Produto cAdd = newItem.getBean();
										cAdd.setEmpresaId(OpusERP4UI.getEmpresa());
										cAdd.setUltima_compra(null); 
										cAdd.setQtdEstoqueDeposito(new Float(0));
										
									
										container.addEntity(cAdd);								
										container.commit();
										tb.focus();
										//refresh();
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Copiou um Produto"));
										AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "COPIOU PRODUTO", produto,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										Notify.Show("Produto Copiado Com Sucesso!", Notify.TYPE_SUCCESS);
			
									} catch (Exception e) {
										e.printStackTrace();															
										Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
									}
						}
					}
				});
				
				cEditor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tfBusca.focus();
					}
				});
				
				getUI().addWindow(cEditor);
				
				}else{					
					Notify.Show("Você não Possui Permissão para Copiar o Produto", Notify.TYPE_ERROR);
				}
			}	
		});
		btCopiar.setEnabled(false);
		return btCopiar;
	}


//	private Component BuildbtSerial() {
//		btSerial = new Button("Serial", new Button.ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
//						OpusERP4UI.getUsuarioLogadoUI().getId(), "Serial"))				
//				{
//					
//					Produto produto = ProdutoDAO.find(Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()));
//					
//					if(tb.getValue()!=null){
//				        Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
//						ListarSerial ls = new ListarSerial(codProduto,true);
//						getUI().addWindow(ls);
//						
//					}
//				}else{					
//					Notify.Show("Você não Possui Permissão para cadastrar Serial do Produto", Notify.TYPE_ERROR);
//				}
//			}
//		});
//		btSerial.setEnabled(false);
//		return btSerial;
//	}
	
	private Component BuildbtLog() {
		btLog = new Button("Log", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
	
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Log"))				
				{
					
				Integer codProduto = null;
				if(tb.getItem(tb.getValue())!=null && tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()!=null){	
					codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
				}
				
//				if(tb.getValue() != null && tb.getItem(tb.getValue()) != null){
					
					HistoricoAlteracoesProduto historicoAlteracoes = new HistoricoAlteracoesProduto(true, true,codProduto);
				
					getUI().addWindow(historicoAlteracoes);
//				}
				
				}else{					
					Notify.Show("Você não Possui Permissão para visualisar Histórico do Produto", Notify.TYPE_ERROR);
				}
			}
		});
		return btLog;
	}
	
	
	private Component BuildbtAlterar() {
		btAlterar = new Button("Alterar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (winSubMenuAlterar != null && winSubMenuAlterar.getUI() != null)
					winSubMenuAlterar.close();
					
					BuildbtAlterar(event);
					
					getUI().addWindow(winSubMenuAlterar);
					winSubMenuAlterar.focus();
					((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
						
						@Override
						public void layoutClick(LayoutClickEvent event) {
							winSubMenuAlterar.close();
							((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
						}
					});	
			}
		});
		btAlterar.setEnabled(false);
		return btAlterar;
	}
	
	
	Window winSubMenuAlterar;
	private void BuildbtAlterar(ClickEvent event) {
		winSubMenuAlterar = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuAlterar.setContent(l);
        winSubMenuAlterar.setWidth("300px");
        winSubMenuAlterar.addStyleName("notifications");
        winSubMenuAlterar.setClosable(false);
        winSubMenuAlterar.setResizable(false);
        winSubMenuAlterar.setDraggable(false);
        winSubMenuAlterar.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuAlterar.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuAlterar.setCloseShortcut(KeyCode.ESCAPE, null);

    
        Button bt1 = new Button("Código de Barras", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Codigo de Barras"))				
				{
				
				Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
				final Produto produto = ProdutoDAO.find(codProduto);
				
				if(tb.getValue() != null && tb.getItem(tb.getValue()) != null){
					
					AlterarNcm alterar = new AlterarNcm(produto, "Alterar Código de Barras", true, true);
					getUI().addWindow(alterar);	
					
					alterar.addListerner(new AlterarNcm.AlterarListerner() {
						
						@Override
						public void onClose(AlterarEvent event) {
						
							if(event.isConfirm()){					
								tfBusca.focus();
								container.refresh();							
							}								
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Corrigiu o estoque"));
							AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "ALTEROU CODIGO DE BARRAS", produto,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			        		
						}
					});
					alterar.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();							
						  }
					  });					
				   }
				}else{					
					Notify.Show("Você não Possui Permissão para Corrigir Estoque do Produto", Notify.TYPE_ERROR);
				}
			  }	
		});
        
        bt1.setPrimaryStyleName("btSubMenu");
        
        Button bt2 = new Button("NCM", new Button.ClickListener() {
        	
        	@Override
        	public void buttonClick(ClickEvent event) {
        		
        		
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(),	OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar NCM"))				
				{
        		
        		
        		Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
        		final Produto produto = ProdutoDAO.find(codProduto);
               		
				if(tb.getValue() != null && tb.getItem(tb.getValue()) != null){
					AlterarNcm alterar = new AlterarNcm(produto, "Alterar NCM", true, false);
					
					getUI().addWindow(alterar);	
					alterar.addListerner(new AlterarNcm.AlterarListerner() {
						
						@Override
						public void onClose(AlterarEvent event) {
						
							if(event.isConfirm()){				
								tfBusca.focus();	
								//container.refresh();
								refresh();
							}							
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Alterou NCM"));
							AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "ALTEROU NCM", produto,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));												
						}
						
					});
					alterar.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tfBusca.focus();							
						}
					});					
				}
				
				}else{					
					Notify.Show("Você não Possui Permissão para Corrigir Estoque do Produto", Notify.TYPE_ERROR);
				}      		
        	 }
         });
        
        bt2.setPrimaryStyleName("btSubMenu");
        l.addComponent(bt1);   
        l.addComponent(bt2);   
    }	
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}	
}
