package com.digital.opuserp.view.modulos.nfe.modelo_55;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CofinsDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.MunicipioDAO;
import com.digital.opuserp.dao.NfeDAO;
import com.digital.opuserp.dao.NfeDAO55;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.PisDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Cofins;
import com.digital.opuserp.domain.ConfigNfeMod55;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Estados;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Nfe;
import com.digital.opuserp.domain.Pis;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.view.util.Notify;
//import com.github.wolfie.refresher.Refresher;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class NotasView extends VerticalLayout implements GenericView {

	JPAContainer<Nfe> container;
	
	Table tb;
	TextField tfBusca;
	
	Button btNovo;
	Button btEditar;
	Button btExportar;
	Button btExcluir;
	
	HorizontalLayout hlFloat;
	ComboBox cbDataEmissao;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	//Refresher refresher;
	

	public NotasView(boolean act){
	
		if(act){
				setSizeFull();				
						
				gmDAO = new GerenciarModuloDAO();
				
				HorizontalLayout hlButons = new HorizontalLayout();
				hlButons.addComponent(BuildbtTransferencia());
				hlButons.addComponent(BuildbtAceitar());
				hlButons.addComponent(BuildbtExportar());
				hlButons.addComponent(BuildbtCancelar());
				
				addComponent(hlButons);
				setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
								
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						addComponent(buildCbDataEmissaoFiltro());
						addComponent(buildTfbusca());
						
						setExpandRatio(tfBusca, 1f);
					}
				});
				
				addComponent(buildTbGeneric());

				hlFloat = new HorizontalLayout();
				hlFloat.setWidth("100%");
				hlFloat.addComponent(buildLbRegistros());
				Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
				lbLegend.setWidth("250px");
				//hlFloat.addComponent(lbLegend);
				hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
				//hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
							
				addComponent(hlFloat);				
				setExpandRatio(tb, 1);
				
				if(cbDataEmissao.getItemIds().toArray().length > 0){
					cbDataEmissao.select(cbDataEmissao.getItemIds().toArray()[cbDataEmissao.getItemIds().toArray().length -1]);
				}
		}
		
		//addExtension(buildRefresher());	
	}
	
	List<Object> columns;
	Object sortPropertyId;
	boolean isSortAscending = false;
	
//	public Refresher buildRefresher(){
//			
//		refresher = new Refresher();
//		refresher.setRefreshInterval(500000);
//		refresher.addListener(new Refresher.RefreshListener() {
//			
//			@Override
//			public void refresh(Refresher source) {
//						
//					columns = new ArrayList<Object>();
//					Object[] visible_columns = tb.getVisibleColumns();		
//					for(Object c:visible_columns){
//						if(!tb.isColumnCollapsed(c.toString())){					   	
//							columns.add(c);
//						}	
//					}
//					
//					sortPropertyId = tb.getSortContainerPropertyId();				
//					isSortAscending = tb.isSortAscending();
//					
//					replaceComponent(tb, buildTbGeneric());
//					setExpandRatio(tb, 1);											
//					addFilter(tfBusca.getValue());
//
//			}
//		});
//		
//		return refresher;
//	}
	
	public ComboBox buildCbDataEmissaoFiltro(){
		
		cbDataEmissao = new ComboBox();
		cbDataEmissao.setNullSelectionAllowed(false);
		cbDataEmissao.setTextInputAllowed(false);
		
		List<String>datas = NfeDAO.getDatas();		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");		
		Format formatter = new SimpleDateFormat("MMMM/yyyy"); 
	    
		try{
			for (String string : datas) {
				cbDataEmissao.addItem(formatter.format(sdf.parse(string)));
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		
		cbDataEmissao.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca != null && tfBusca.getValue() != null ? tfBusca.getValue() : "");
			}
		});
		
		return cbDataEmissao;
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
		
		if(c instanceof NotasView)
		{
			//btNovo.addShortcutListener(buildShortCutNovo());
			//btCancelar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
		//	if(btCancelar != null || slEditar != null){
			//	btCancelar.removeShortcutListener(slEditar);			
		//	}
		}
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
			//	btCancelar.click();
			}
		};
		return slEditar;
	}
	
	public JPAContainer<Nfe> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Nfe.class, ConnUtil.getEntity());
		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.applyFilters();
		
		container.setAutoCommit(false);
		container.addNestedContainerProperty("pedido.cliente.nome_razao");
		container.addNestedContainerProperty("pedido.cliente.doc_cpf_cnpj");
		
		return container;
	}
	
	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());
		
		tb.setSizeFull();
		tb.setSelectable(true);

		tb.setVisibleColumns(new Object[]{"numero","serie","data_emissao","pedido.cliente.doc_cpf_cnpj","pedido.cliente.nome_razao","uf","tipo_documento"});	
		
		tb.setColumnHeader("serie", "Serie");
		tb.setColumnHeader("numero", "Número");
		tb.setColumnHeader("data_emissao", "Data Emissão");
		tb.setColumnHeader("pedido.cliente.nome_razao", "Destinatário");
		tb.setColumnHeader("pedido.cliente.doc_cpf_cnpj", "CPF/CNPJ");

		tb.setColumnHeader("uf", "UF Dest.");
		tb.setColumnHeader("tipo_documento", "Tipo");
										
		tb.setImmediate(true);		
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btNovo.setEnabled(true);
					btEditar.setEnabled(true);
					btExcluir.setEnabled(true);
					btExportar.setEnabled(true);					
				}else{
					btNovo.setEnabled(false);
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
					btExportar.setEnabled(false);
				}
			}
		});
		
		if(sortPropertyId != null){
			tb.sort(new Object[]{sortPropertyId},new boolean[]{isSortAscending});			
		}
					
		return tb;
	}
	
	private String formatarCodNf(String s){		
		return s.substring(0, 3)+"."+s.substring(3, 6)+"."+s.substring(6, 9);
	}

	@Override
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

	@Override
	public void addFilter(String s) {

		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
//		Object[] collums = tb.getVisibleColumns();		
//		List<Filter> filtros = new ArrayList<Filter>();		
//		
//		if(cbDataEmissao.getValue() != null){
//			try{
//				SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
//				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");			
//				Format formatter = new SimpleDateFormat("yyyy-MM-dd"); 
//				
//				container.addContainerFilter(Filters.gt("data_emissao", sdf1.parse(formatter.format(new DateTime(sdf.parse("01/"+cbDataEmissao.getValue().toString())).minusDays(1).toDate()))));
//				container.addContainerFilter(Filters.lt("data_emissao", sdf1.parse(formatter.format(new DateTime(sdf.parse("31/"+cbDataEmissao.getValue().toString())).plusDays(1).toDate()))));
//				
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//		for(Object c:collums){		 
//			
//			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
//				filtros.add(new Like(c.toString(), "%"+s+"%", false));
//			}			
//		}		
//		
//		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final BeanItem<Nfe> newItem = new BeanItem<Nfe>(new Nfe());
				
				com.digital.opuserp.view.modulos.nfe.modelo_55.NotasEditor notaEditor = new com.digital.opuserp.view.modulos.nfe.modelo_55.NotasEditor(newItem, "Gerar NFe", true,codSubModulo);
				notaEditor.addListerner(new com.digital.opuserp.view.modulos.nfe.modelo_55.NotasEditor.NotasListerner() {
					
					@Override
					public void onClose(com.digital.opuserp.view.modulos.nfe.modelo_55.NotasEditor.NotasEvent event) {
						if(event.isConfirm()){
								Nfe nfe = newItem.getBean();		
								nfe.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								container.addEntity(nfe);
								container.commit();
								tb.focus();
								container.refresh();
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Gerou Nfe modelo 55"));
								Notify.Show("Nfe modelo 55 gerada com sucesso!", Notify.TYPE_SUCCESS);
																
						}
					}

				});
				
				notaEditor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();							
					}
				});
				
				getUI().addWindow(notaEditor);
			}
			
		});
		
		return btNovo;
	}
	

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		return btEditar;
	}
	public Button BuildbtExportar() {
		btExportar = new Button("Exportar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			  try{
				
				  gerarFile();
				
			  }catch(Exception e){
				  e.printStackTrace();
			  }
			}
		});
		
		return btExportar;
	}	

	private void gerarFile(){
		SimpleDateFormat sdfAno = new SimpleDateFormat("yy");						
	       
        try{
        	
        	String nomeArquivo  = "NOTAFISCAL.txt";        	
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
	
	public String createFile()  throws Exception{	
		
		
		Nfe nfe = ConnUtil.getEntity().find(Nfe.class, tb.getItem(tb.getValue()).getItemProperty("id").getValue());
		
		Estados estado = new Estados();
		
		if(nfe != null && nfe.getUf() != null){
			Query qEstado = ConnUtil.getEntity().createQuery("select e from Estados e where e.sigla=:sigla", Estados.class);
			qEstado.setParameter("sigla", nfe.getUf());
			if(qEstado.getResultList().size() == 1){
				estado = (Estados)qEstado.getSingleResult();
			}				
		}
		
		
		StringBuilder sb = new StringBuilder();
		String quebra = "\n";						

		SimpleDateFormat sdf = new SimpleDateFormat("yyMM");
		
		//2015-09-01T08:33:00-03:00
		SimpleDateFormat sdf_data_emissao = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX",new Locale("pt", "BR"));
		//List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(pedido.getId());
		
		if(nfe != null && estado != null){
		
			String versao = "3.10";
			String cUF = estado.getCodigo_ibge().toString(); 
			String natOp = nfe.getNaturezaOperacao().getDescricao();
			String indPgto = nfe.getForma_pagamento().split("-")[0];
			String mod = "55";
			String serie = "1";
			String nNF = nfe.getNumero() != null ? nfe.getNumero() : "";
			String dhEmi = sdf_data_emissao.format(nfe.getData_emissao());
			String tpNF = nfe.getTipo_documento().split("-")[0];
			String idDest = nfe.getDestino_operacao().split("-")[0];
			
			
			
			String cMunFG =cUF+MunicipioDAO.findByName(nfe.getPedido().getCliente().getEndereco_principal().getCidade()).getCodigo_ibge().toString();
			String tpImp = "1";
			String tpEmis = "1";
			String cDV = "6";
			String tpAmb = "1";
			String finNFe = "1";
			String indFinal = "0";
			String indPres = "0";
			String procEmi = "3";
			String verProc = "3.10.61";
			String dhCont = "";
			String xJust = "";
			
			sb.append("NOTAFISCAL|1"+quebra);
			
			sb.append("A|"+versao+"||"+quebra);	
			sb.append("B|"+cUF+"||"+natOp+"|"+indPgto+"|"+mod+"|"+serie+"|"+nNF+"|"+dhEmi+"||"+tpNF+"|"+idDest+"|"+cMunFG+"|"+tpImp+"|"+tpEmis+"|"+cDV+"|"+tpAmb+"|"+finNFe+"|"+indFinal+"|"+indPres+"|"+procEmi+"|"+verProc+"|"+dhCont+"|"+xJust+"|"+quebra);

			
			String xNome  = OpusERP4UI.getEmpresa().getRazao_social();
			String xFant = OpusERP4UI.getEmpresa().getNome_fantasia();
			String IE = OpusERP4UI.getEmpresa().getInscricao_estadual();
			String IEST = "";
			String IM = "0904125";
			String CNAE = "6190601";
			String CRT = "1";

			sb.append("C|"+xNome+"|"+xFant+"|"+IE+"|"+IEST+"|"+IM+"|"+CNAE+"|"+CRT+"|"+quebra);
			
			String CNPJ = OpusERP4UI.getEmpresa().getCnpj();
			
			sb.append("C02|"+CNPJ+"|"+quebra);
			
			String xLgr = OpusERP4UI.getEmpresa().getEndereco();
			String nro = OpusERP4UI.getEmpresa().getNumero();
			String xCPL = "";
			String xBairro = OpusERP4UI.getEmpresa().getBairro();
			String cMun = cMunFG;
			String xMun = OpusERP4UI.getEmpresa().getCidade();
			String UF = OpusERP4UI.getEmpresa().getUf();
			String CEP = OpusERP4UI.getEmpresa().getCep();
			String cPais = "1058";
			String xPais = OpusERP4UI.getEmpresa().getPais();
			String fone = OpusERP4UI.getEmpresa().getFone1();
			
			//C05|xLgr|nro|xCPL|xBairro|cMun|xMun|UF|CEP|cPais|xPais|fone| 
			sb.append("C05|"+xLgr+"|"+nro+"|"+xCPL+"|"+xBairro+"|"+cMun+"|"+xMun+"|"+UF+"|"+CEP+"|"+cPais+"|"+xPais+"|"+fone+"|"+quebra);
			
			String xNomeCliente = nfe.getPedido().getCliente().getNome_razao();
			String indIEDest = "9";
			String IECliente = "";
			String ISUF = "";
			String email = nfe.getPedido().getCliente().getEmail() != "" ? nfe.getPedido().getCliente().getEmail() : "";
			
			//E|xNome|indIEDest|IE|ISUF|IM|email| 
			sb.append("E|"+xNomeCliente+"|"+indIEDest+"|"+IECliente+"|"+IECliente+"|"+ISUF+"|"+email+"|"+quebra);
						
			if(nfe.getPedido().getCliente().getDoc_cpf_cnpj().length() > 11){
				sb.append("E02|"+nfe.getPedido().getCliente().getDoc_cpf_cnpj()+"|"+quebra);
			}else{
				sb.append("E03|"+nfe.getPedido().getCliente().getDoc_cpf_cnpj()+"|"+quebra);
			}
			
			String xLgrCliente = nfe.getPedido().getCliente().getEndereco_principal().getEndereco();
			String nroCliente = nfe.getPedido().getCliente().getEndereco_principal().getNumero();
			String xCplCliente = "";
			String xBairroCliente = nfe.getPedido().getCliente().getEndereco_principal().getBairro();
			String cMunCliente =cUF+MunicipioDAO.findByName(nfe.getPedido().getCliente().getEndereco_principal().getCidade()).getCodigo_ibge().toString();
			String xMunCliente = nfe.getPedido().getCliente().getEndereco_principal().getCidade();
			String UFCliente = nfe.getPedido().getCliente().getEndereco_principal().getUf();
			String CEPCliente = nfe.getPedido().getCliente().getEndereco_principal().getCep();
			String cPaisCliente = "1058";
			String xPaisCliente = nfe.getPedido().getCliente().getEndereco_principal().getPais();
			String foneCliente =  nfe.getPedido().getCliente().getTelefone1();
			
			///E05|xLgr|nro|xCpl|xBairro|cMun|xMun|UF|CEP|cPais|xPais|fone| 
			sb.append("E05|"+xLgrCliente+"|"+nroCliente+"|"+xCplCliente+"|"+xBairroCliente+"|"+cMunCliente+"|"+xMunCliente+"|"+UFCliente+"|"+CEPCliente+"|"+cPaisCliente+"|"+xPaisCliente+"|"+foneCliente+"|"+quebra);
						
			List<EcfPreVendaDetalhe> itens = PedidoDAO.getItensPedido(nfe.getPedido().getId());
			
			int i = 1;
			for (EcfPreVendaDetalhe item : itens) {
				
				Produto produto = ProdutoDAO.find(item.getProdutoId());
				
				if(produto.getCfop() == null){
					Notify.Show("Um ou mais produto desta nota estão sem um CFOP!", Notify.TYPE_ERROR);
					return "ERRO";
				}
				
				
				
				sb.append("H|"+String.valueOf(i)+"||"+quebra);
				i++;
				
				//I|cProd|cEAN|xProd|NCM|EXTIPI|CFOP|uCom|qCom|vUnCom|vProd|cEANTrib|uTrib|qTrib|vUnTrib|vFrete|vSeg|vDesc| vOutro|indTot|xPed|nItemPed|nFCI|
				String cProd = produto.getId().toString();
				String cEAN = produto.getgTin();
				String xProd = produto.getNome();
				String NCM = produto.getNcm();
				String EXTIPI = "";
				String CFOP = produto.getCfop() != null ? produto.getCfop().getId().toString() : "";
				String uCom = produto.getUnidadeProduto().getNome();
				String qCom = String.format("%.4f", item.getQuantidade()).replace(",", ".");
				
				String vUnCom = String.format("%.10f",item.getValorUnitario()).replace(",", ".");
				
				String vProd =  String.format("%.2f",item.getValorUnitario()*item.getQuantidade()).replace(",", ".");
				String cEANTrib = produto.getgTin();
				String uTrib = produto.getUnidadeProduto().getNome();
				String qTrib = String.format("%.4f", item.getQuantidade()).replace(",", ".");
				String vUnTrib = String.format("%.10f",item.getValorUnitario()).replace(",", ".");				    
				String vFrete = "";
				String vSeg = "";
				String vDesc = "";
				String vOutro = "";
				String indTot = "1";
				String xPed = "";
				String nItemPed = "1";
				String nFCI = "";
			
				sb.append("I|"+cProd+"|"+cEAN+"|"+xProd+"|"+NCM+"|"+EXTIPI+"|"+CFOP+"|"+uCom+"|"+qCom+"|"+vUnCom+"|"+vProd+"|"+cEANTrib+"|"+uTrib+"|"+qTrib+"|"+vUnTrib+"|"+vFrete+"|"+vSeg+"|"+vDesc+"|"+vOutro+"|"+indTot+"|"+xPed+"|"+nItemPed+"|"+nFCI+"|"+quebra);
				sb.append("M||"+quebra);
				sb.append("N|"+quebra);
				
				String orig = "0";
				String CSOSN = produto.getSimples_nacional().getSimples_nacional_cod().toString();
				String vBCSTRet = "";
				String vICMSSTRet = "";
				
				///N10g|orig|CSOSN|vBCSTRet|vICMSSTRet| 
				if(CSOSN.equals("500")){
					sb.append("N10g|"+orig+"|"+CSOSN+"|"+vBCSTRet+"|"+vICMSSTRet+"|"+quebra);
				}else if(CSOSN.equals("102")){
					sb.append("N10d|"+orig+"|"+CSOSN+"|"+quebra);
				}
				
								
				String CST = produto.getCst_forma_tributo().getCst_tributo_cod();
			
				
				Pis pisProduto = PisDAO.findByProduto(produto.getId());
				Cofins cofins = CofinsDAO.findByProduto(produto.getId());
				
				sb.append("Q|"+quebra);
				
				if(pisProduto != null){
					Notify.Show("Pis do Produto: "+produto.getNome()+" Inexistente!", Notify.TYPE_ERROR);
				}
				
				if(cofins != null){
					Notify.Show("Cofins do Produto: "+produto.getNome()+" Inexistente!", Notify.TYPE_ERROR);
				}
				
				
				sb.append("Q05|"+pisProduto.getCst_pis().getId().toString()+"|"+StringUtil.preencheCom2(String.valueOf(pisProduto.getValor_pis()),"0",4,2)+"|"+quebra);
				sb.append("Q10|"+StringUtil.preencheCom2(String.valueOf(pisProduto.getAliquota_pis_st()),"0",6,2)+"|0.0000|"+quebra);
				
				sb.append("S|"+quebra);
				sb.append("S05|"+cofins.getCst_cofins().getId().toString()+"|"+StringUtil.preencheCom2(String.valueOf(cofins.getValor_cofins()),"0",4,2)+"|"+quebra);							 
				sb.append("S09|"+StringUtil.preencheCom2(String.valueOf(cofins.getAliquota_cofins()),"0",6,2)+"|0.0000|"+quebra);
				
			}
									
			sb.append("W|"+quebra);
			
			String vBC = "0.00";
			String vICMS = "0.00";
			String vICMSDeson = "0.00";
			String vBCST = "0.00";
			String vST = "0.00";
			String vProd = String.format("%.2f",nfe.getPedido().getSubTotal()).replace(",", ".");
			String vFrete = "0.00";
			String vSeg = "0.00";
			String vDesc =  nfe.getPedido().getTotal_desc() != null && nfe.getPedido().getTotal_desc() > 0 ?  String.format("%.2f",new Double(String.valueOf((nfe.getPedido().getSubTotal()*nfe.getPedido().getTotal_desc()) / 100))).replace(",", ".") : "";
			String vII = "0.00";
			String vIPI = "0.00";
			String vPIS = "0.00";
			String vCOFINS = "0.00";
			String vOutro = "0.00";
			String vNF = String.format("%.2f",nfe.getPedido().getSubTotal()).replace(",", ".");
			String vTotTrib = "0.00";
			
			//W02|vBC|vICMS|vICMSDeson|vBCST|vST|vProd|vFrete|vSeg|vDesc|vII|vIPI|vPIS|vCOFINS|vOutro|vNF|vTotTrib| 
			sb.append("W02|"+vBC+"|"+vICMS+"|"+vICMSDeson+"|"+vBCST+"|"+vST+"|"+vProd+"|"+vFrete+"|"+vSeg+"|"+vDesc+"|"+vII+"|"+vIPI+"|"+vPIS+"|"+vCOFINS+"|"+vOutro+"|"+vNF+"|"+vTotTrib+"|"+quebra);
			
			sb.append("X|9|"+quebra);
//		sb.append("Y|"+quebra);
//		sb.append("Y02|1|"+nfe.getPedido().getValor()+"|0.00|"+nfe.getPedido().getValor()+"|"+quebra);
//		sb.append("Y07|1|2015-09-02|150.00|"+quebra);
			
			ConfigNfeMod55 default_value = NfeDAO55.getDefaultValue();
			sb.append("Z||"+default_value.getObservacao()+" // PV:"+nfe.getPedido().getId().toString()+"|"+quebra);
			
			return sb.toString();
			
		}
		
		return null;
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
	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a Nfe Selecionado?", true, true);
				gDialog.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						if(event.isConfirm()){
							container.removeItem(tb.getValue());
							container.commit();
							Notify.Show("Nfe Excluída com Sucesso!", Notify.TYPE_SUCCESS);
							tb.focus();
						}							
					}
				});
				
				gDialog.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();							
					}
				});
			
				getUI().addWindow(gDialog);
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
