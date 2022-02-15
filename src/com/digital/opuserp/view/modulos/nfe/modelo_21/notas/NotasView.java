package com.digital.opuserp.view.modulos.nfe.modelo_21.notas;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import br.com.caelum.stella.boleto.Datas;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.NfeDAO;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.dao.OsiDAO;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.Osi;
import com.digital.opuserp.domain.ParametrosBoleto;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.util.boletos.boleto.Banco;
import com.digital.opuserp.util.boletos.boleto.Boleto;
import com.digital.opuserp.util.boletos.boleto.Emissor;
import com.digital.opuserp.util.boletos.boleto.Sacado;
import com.digital.opuserp.util.boletos.boleto.bancos.BancoDoBrasil;
import com.digital.opuserp.util.boletos.boleto.bancos.Sicredi;
import com.digital.opuserp.view.modulos.nfe.modelo_21.notas.NotasEditor.NotasEvent;
import com.digital.opuserp.view.modulos.nfe.modelo_21.notas.NotasEditorBoleto.NotasEditorBoletoEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
//import com.github.wolfie.refresher.Refresher;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class NotasView extends VerticalLayout implements GenericView {

	JPAContainer<NfeMestre> container;
	
	Table tb;
	TextField tfBusca;
	
	Button btNovo;
	Button btEditar;
	Button btCancelar;
	Button btExcluir;
	Button btRelatorio;
	Button btImprimir;
	Button btEnviarEmail;
	
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
				hlButons.addComponent(BuildBtCancelar());
				hlButons.addComponent(BuildbtRelatorio());
				hlButons.addComponent(BuildbtImprimir());
				hlButons.addComponent(BuildbtEnviarEmail());
				
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
			//	hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
							
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
	
//public Refresher buildRefresher(){
//	
//		
//		refresher = new Refresher();
//		refresher.setRefreshInterval(500000);
//		refresher.addListener(new Refresher.RefreshListener() {
//			
//			@Override
//			public void refresh(Refresher source) {
//								
//				
//					
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
//					
//
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
		btCancelar.setEnabled(false);
		//btExcluir.setEnabled(false);
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	public void buildShortcurEvents(Component c){
		
		if(c instanceof NotasView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btCancelar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btCancelar != null || slEditar != null){
				btCancelar.removeShortcutListener(slEditar);			
			}
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
				btCancelar.click();
			}
		};
		return slEditar;
	}
	
	public JPAContainer<NfeMestre> buildContainer(){
		container = JPAContainerFactory.makeBatchable(NfeMestre.class, ConnUtil.getEntity());		
		container.setAutoCommit(false);
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("contas_receber.id");
		container.addNestedContainerProperty("contrato.id");
		container.addNestedContainerProperty("natOpe.descricao");
		container.addNestedContainerProperty("cliente.email");
		
		container.addNestedContainerProperty("contas_receber.n_numero");
		container.addNestedContainerProperty("contas_receber.n_numero_sicred");
		container.addNestedContainerProperty("contas_receber.transacao_gerencianet");
		
		
		return container;
	}
	
	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("data_emissao") || colId.equals("data_prestacao")){				
											
					if(colId.equals("data_emissao")){
						
						try{
							SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
							return form.format(tb.getItem(rowId).getItemProperty("data_emissao").getValue());
						}catch(Exception e){
							//Notify.Show("Problemas com a NF:"+tb.getItem(rowId).getItemProperty("data_emissao").getValue().toString(), Notify.TYPE_ERROR);
							e.printStackTrace();
							Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
						}
					}else{
						try{
							SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
							return form.format(tb.getItem(rowId).getItemProperty("data_prestacao").getValue());
						}catch(Exception e){
							//Notify.Show("Problemas com a NF:"+tb.getItem(rowId).getItemProperty("data_emissao").getValue().toString(), Notify.TYPE_ERROR);
							e.printStackTrace();
							Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
						}
					}
				}
				
				if(colId.equals("email_enviado")){					
					
					if((boolean)tb.getItem(rowId).getItemProperty(colId).getValue()){
						return "SIM";
					}else{
						return "NÃO";
					}
									
				}
				
				if(colId.equals("id")){					
					return formatarCodNf(StringUtil.preencheCom(tb.getItem(rowId).getItemProperty(colId).getValue().toString(), "0", 9, 1));				
				}
				//if(colId.equals("total_nota")){
					
				//	return Real.formatDbToString(String.valueOf(Real.formatStringToDBDouble(((AcessoCliente)tb.getItem(rowId).getItemProperty("contrato").getValue()).getPlano().getValor())-Real.formatStringToDBDouble(((AcessoCliente)tb.getItem(rowId).getItemProperty("contrato").getValue()).getPlano().getDesconto())));
				//}
				if(colId.equals("situacao_doc")){	
					String status = tb.getItem(rowId).getItemProperty(colId).getValue().toString();
					return status.equals("N") ? "Aceito" : "Cancelado";
				}
			
				
				
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};
		
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {

				if(source.getItem(itemId).getItemProperty("situacao_doc").getValue() != null && source.getItem(itemId).getItemProperty("situacao_doc").getValue().toString().equals("S")){
					return "row-header-pedido-cancelado";
				}else{
					
					if(!(boolean)source.getItem(itemId).getItemProperty("email_enviado").getValue()){
						return "row-header-crm-atrasado";
					}else{
						return "nota-normal";
					}
				}
			}
		});
		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setVisibleColumns(new Object[]{"id","contas_receber.id","contas_receber.n_numero","contas_receber.n_numero_sicred","contas_receber.transacao_gerencianet","cliente.nome_razao","cliente.email","contrato.id","natOpe.descricao","data_emissao","data_prestacao","total_nota","email_enviado","situacao_doc"});	
		
		tb.setColumnHeader("id", "Nº NF");
		tb.setColumnHeader("contas_receber.id", "Boleto");
		tb.setColumnHeader("contas_receber.n_numero", "BB");
		tb.setColumnHeader("contas_receber.n_numero_sicred", "SICRED");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("cliente.email", "Email");
		tb.setColumnHeader("contrato.id", "Contrato");
		tb.setColumnHeader("natOpe.descricao", "Natureza da operação");
		tb.setColumnHeader("data_emissao", "Data emissão");
		tb.setColumnHeader("data_prestacao", "Data prestação");
		tb.setColumnHeader("total_nota", "Total nota");
		tb.setColumnHeader("email_enviado", "Email enviado");
		tb.setColumnHeader("situacao_doc", "Status");
		tb.setColumnHeader("contas_receber.transacao_gerencianet", "Transação");
		
		tb.setColumnCollapsed("cliente.email", true);
		
		tb.setColumnAlignment("total_nota", Align.RIGHT);
								
		tb.setImmediate(true);
		
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					
					btCancelar.setEnabled(true);
					btImprimir.setEnabled(true);
					//btEnviarEmail.setEnabled(true);
				}else{

					btCancelar.setEnabled(false);
					btImprimir.setEnabled(false);
					//btEnviarEmail.setEnabled(false);
				}
			}
		});
		
		if(columns != null){
			for(Object colum : columns){
				tb.setColumnCollapsed(colum.toString(),false);
			}
		}
		
		
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
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		if(cbDataEmissao.getValue() != null){
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");								
				Format formatter = new SimpleDateFormat("yyyy-MM-dd");				
				SimpleDateFormat sdfAno = new SimpleDateFormat("yyyy");			
				
				container.addContainerFilter(Filters.gt("data_emissao", sdf1.parse(formatter.format(new DateTime(sdf.parse("01/"+cbDataEmissao.getValue().toString())).minusDays(1).toDate()))));
				container.addContainerFilter(Filters.lteq("data_emissao", sdf1.parse(formatter.format(new DateTime(sdf.parse(getLastDayOfMonth(new DateTime(sdf.parse("01/"+cbDataEmissao.getValue().toString())).getMonthOfYear(), new DateTime(sdf.parse("01/"+cbDataEmissao.getValue().toString())).getYear())+"/"+cbDataEmissao.getValue().toString())).toDate()))));
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	private static String getLastDayOfMonth(int month, int year) {
	    LocalDate lastDayOfMonth = new LocalDate(year, month, 1).dayOfMonth().withMaximumValue();
	    return lastDayOfMonth.toString("dd");
	}
	
	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Gerar"))				
				{			
					
						final BeanItem<NfeMestre> newItem = new BeanItem<NfeMestre>(new NfeMestre());
										
						NotasEditor cEditor = new NotasEditor(newItem, "Gerar NFe", true);
						cEditor.addListerner(new NotasEditor.NotasListerner() {
							
							@Override
							public void onClose(NotasEvent event) {
								if(event.isConfirm()){
																		
										tb.focus();
										container.refresh();
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Gerou Nfe"));
										Notify.Show("Nfe gerada com sucesso!", Notify.TYPE_SUCCESS);
																		
								}
							}
						});
						
						cEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();							
							}
						});
						
						getUI().addWindow(cEditor);
				}else{					
					Notify.Show("Você não possui permissão para Gerar uma nfe", Notify.TYPE_ERROR);
				}
							
			}
			
		});
		
		return btNovo;
	}

	@Override
	public Button BuildbtAceitar() {
		
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					final EntityItem<NfeMestre> entitryItem = (EntityItem<NfeMestre>)tb.getItem(tb.getValue());
				
					NotasEditorBoleto nEditorBoleto = new NotasEditorBoleto(tb.getItem(tb.getValue()),"Editar Boleto", true);
					nEditorBoleto.addListerner(new NotasEditorBoleto.NotasEditorBoletoListerner() {
						
						@Override
						public void onClose(NotasEditorBoletoEvent event) {
							
							entitryItem.getEntity().setContas_receber(new ContasReceber(event.getBoleto()));
							NfeDAO.save(entitryItem.getEntity());
						}
					});
					
					getUI().addWindow(nEditorBoleto); 
			}
		});
		
		return btEditar;
	}
	
	public Button BuildBtCancelar(){
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cancelar"))				
				{		
					GenericDialog gDialog = new GenericDialog("Confirme para continuar!", "Você deseja realmente cancelar a nfe selecionada?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								
								
								NfeMestre nfe = NfeDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());								
								nfe.setSituacao_doc("S");
								NfeDAO.save(nfe);
								
								Notify.Show("Nfe cancelada com Sucesso!", Notify.TYPE_SUCCESS);
								tb.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cancelou uma nfe"));
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
				
				}else{					
					Notify.Show("Você não possui permissão para Cancelar uma nfe", Notify.TYPE_ERROR);
				}
			}
		});
		
		btCancelar.setEnabled(false); 
		return btCancelar;
	}

	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
					GenericDialog gDialog = new GenericDialog("Confirme para continuar!", "Você deseja realmente excluir a nfe selecionada?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									container.removeItem(tb.getValue());
									container.commit();									
									Notify.Show("Nfe excluída com Sucesso!", Notify.TYPE_SUCCESS);
									tb.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma nfe"));
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
				}else{					
					Notify.Show("Você não possui permissão para excluir uma nfe", Notify.TYPE_ERROR);
				}
			}
		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}
	
	public Button BuildbtRelatorio() {
		btRelatorio = new Button("Relatório", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Relatorio"))				
				{									
					try {
						
						Window win = new Window("Relatório de Notas Fiscais");
						win.setWidth("800px");
						win.setHeight("600px");
						win.setResizable(true);						
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
						
						StreamResource resource = new StreamResource(new ExportNfe("RELATORIO DE NOTAS FISCAIS MODELO 21 - "+cbDataEmissao.getValue().toString().toUpperCase(), tb.getContainerDataSource()), "RELATORIO DE NOTAS FISCAIS MODELO 21 - "+cbDataEmissao.getValue().toString().toUpperCase()+".pdf");
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
						e.printStackTrace();
					}					
					
				}else{					
					Notify.Show("Você não possui permissão para emitir relatório de notas", Notify.TYPE_ERROR);
				}
			}
		});
		
		return btRelatorio;
	}
	
	public Button BuildbtEnviarEmail() {
		btEnviarEmail = new Button("Enviar E-mail", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Enviar Email"))				
				{
					
							EnvioEmail sendEmail = new EnvioEmail("Enviar Email", true, codSubModulo);
							sendEmail.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									refresh();									
								}
							});
							getUI().addWindow(sendEmail); 
					
				}else{					
					Notify.Show("Você não possui permissão para enviar uma nfe", Notify.TYPE_ERROR);
				}
			}
		});
		
		//btEnviarEmail.setEnabled(false); 
		return btEnviarEmail;
	}
	
	public Button BuildbtImprimir() {
		btImprimir = new Button("Imprimir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Imprimir"))				
				{
					
					ContasReceber boleto = (ContasReceber)tb.getItem(tb.getValue()).getItemProperty("contas_receber").getValue();
					
					if(boleto.getTransacao_gerencianet() != null && !boleto.getTransacao_gerencianet().equals("")){
						EntityItem<NfeMestre> nfeItem = (EntityItem<NfeMestre>)tb.getItem(tb.getValue());					
						NfeMestre nfe =  nfeItem.getEntity();
						
								String cad = StringUtil.md52(
								StringUtil.preencheCom(nfe.getCliente().getDoc_cpf_cnpj(), "0", 14, 1)+
								StringUtil.preencheCom(nfe.getId().toString(), "0", 9, 1)+
								StringUtil.preencheCom(Real.formatDbToString(String.valueOf(nfe.getTotal_nota())).replace(",", ""),"0",12,1)+
								StringUtil.preencheCom(Real.formatDbToString(String.valueOf(nfe.getTotal_nota())).replace(",", ""),"0",12,1)+
								"000000000000");
						
						        String pronto = cad.substring(0,4).toUpperCase()+"."+
								cad.substring(4, 8).toUpperCase()+"."+
						        cad.substring(8, 12).toUpperCase()+"."+
								cad.substring(12, 16).toUpperCase()+"."+
						        cad.substring(16, 20).toUpperCase()+"."+
								cad.substring(20, 24).toUpperCase()+"."+
						        cad.substring(24, 28).toUpperCase()+"."+
								cad.substring(28, 32).toUpperCase();
						
														
						final String url = "http://172.17.0.13/boletoNovo/gerarNfe.php?url="+boleto.getN_numero_gerencianet()+"&&hash="+pronto;
						                        //http://172.17.0.13/boletoNovo/gerarNfe.php?url2=https://visualizacao.gerencianet.com.br/emissao/177311_13338_PAMEH7/A4XB-177311-7179-LUALO3

						Window wVideo  = new Window("Imprimir NFe");
				        wVideo.setResizable(true); 
				        wVideo.setModal(true);
				        wVideo.center();
				        wVideo.setWidth("942px");
				        wVideo.setHeight("680px");
				        
				        wVideo.setContent(new VerticalLayout(){
				        	{
				        		setMargin(true);
				        		setSpacing(true);
				        		
				        		setSizeFull();
				        		String link = url;
				        		
				        		final Embedded video = new Embedded(null,new ExternalResource(link));
						        video.setType(Embedded.TYPE_BROWSER);
						        video.setSizeFull();

				        		addComponent(video);			        		
				        	}
				        });
				        
				        getUI().addWindow(wVideo);

					}else{
						try {
							
							Window window;
							window = new Window();
							window.setCaption("Imprimir Boletos");
							window.setWidth("800px");
							window.setHeight("600px");
							window.setResizable(true);
							window.center();
							window.setModal(true);
							window.setStyleName("disable_scroolbar");		
							window.setCloseShortcut(KeyCode.ESCAPE, null);
						
							NfeMestre nfe = NfeDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
							
							StreamResource resource = new StreamResource(new ImprimirBoletoFatura(getBoleto(),nfe,plano), "NF SERVICO COMUNICACAO "+StringUtil.preencheCom(nfe.getId().toString(), "0", 9, 1)+" - "+tb.getItem(tb.getValue()).getItemProperty("cliente.nome_razao").toString()+".pdf");
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
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
										
				}else{					
					Notify.Show("Você não possui permissão para imprimir uma nfe", Notify.TYPE_ERROR);
				}
			}
		});
		
		btImprimir.setEnabled(false);
		return btImprimir;
	}
	PlanoAcesso plano;
	private Boleto getBoleto(){
		EntityManager em = ConnUtil.getEntity();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ContasReceber cr = (ContasReceber) tb.getItem(tb.getValue()).getItemProperty("contas_receber").getValue();
		
		if(cr != null && cr.getPlano_contrato() != null){
			plano = em.find(PlanoAcesso.class, cr.getPlano_contrato());
		}
		
		String controle =  cr.getControle();
		Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
		qControle.setParameter("nome", controle);
		qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		ContaBancaria cb = null;
//		if(qControle.getResultList().size() ==1){
//			cb = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
//		}
		
		Integer anoEmissao = Integer.parseInt(cr.getData_emissao().toString().substring(0, 4).toString());
		Integer mesEmissao = Integer.parseInt(cr.getData_emissao().toString().substring(5, 7).toString());
		Integer diaEmissao = Integer.parseInt(cr.getData_emissao().toString().substring(8, 10).toString());
	
		Integer anoVencimento = Integer.parseInt(cr.getData_vencimento().toString().substring(0, 4).toString()); 
		Integer mesVencimento = Integer.parseInt(cr.getData_vencimento().toString().substring(5, 7).toString()); 
		Integer diaVencimento = Integer.parseInt(cr.getData_vencimento().toString().substring(8, 10).toString());
	
		
		String nossoNumero = "";

		
		if(cr.getN_numero() != null){
			 nossoNumero = cr.getN_numero();
	        	
	        	
	        	if(cr.getControle().equals("ACESSO-PRE") || 
	        			cr.getControle().equals("ACESSO-POS") || 
	        			cr.getControle().equals("ALUGUEL") ||
	        			cr.getControle().equals("ASSISTENCIA")){
	        		
	        		//banco= new BancoDoBrasil();
	        		cb = em.find(ContaBancaria.class, 2);
	        		
	        	}else{
	        		cb = em.find(ContaBancaria.class, 2);
	        	}
	        }
	        
	        if(cr.getN_numero_sicred() != null){
	        	nossoNumero = cr.getN_numero_sicred();
	        	
	        	if(cr.getControle().equals("ACESSO-PRE") || 
	        			cr.getControle().equals("ACESSO-POS") || 
	        			cr.getControle().equals("ALUGUEL") ||
	        			cr.getControle().equals("ASSISTENCIA")){
	        		
	        		//banco= new Sicredi();
	        		cb = em.find(ContaBancaria.class, 5);
	        		
	        	}else{
	        		cb = em.find(ContaBancaria.class, 2);
	        	}
	        }
	        
	        
	        if(cb == null){
	        	cb = em.find(ContaBancaria.class, 2);
	        }
		
		
		String nomeCliente = cr.getCliente().getNome_razao();
		String nDoc =  cr.getN_doc();

	
		Query qPb = em.createQuery("select pb from ParametrosBoleto pb where pb.cliente_id = :codCliente", ParametrosBoleto.class);
		qPb.setParameter("codCliente",cr.getCliente().getId());			
		ParametrosBoleto pb = null;
		boolean cobrarTaxa = true;
		if(qPb.getResultList().size() > 0){
			pb = (ParametrosBoleto) qPb.getSingleResult();
			
			if(!pb.getCobrar_taxa_bancaria()){
				cobrarTaxa = false;
			}
		}
		
		Double vlrBoleto = null; 
		Double valorBoleto = null;
		
		boolean taxBoleto = true;
		
		try{
			//ContasReceber cr = ContasReceberDAO.find(Integer.parseInt(tb.getItem(o).getItemProperty("Cod.").getValue().toString()));									
			String codContrato = cr.getN_doc().split("/")[0].toString();									
			PlanoAcesso oPlanoBoleto = PlanoAcessoDAO.find(cr.getPlano_contrato());
			taxBoleto =oPlanoBoleto != null  &&  oPlanoBoleto.getTaxa_boleto().equals("NAO") ? false : true;		
		}catch(Exception e){
			
		}
		
		if(cobrarTaxa){
			if(!taxBoleto){
				cobrarTaxa = false;
			}
		}
		
		if(cobrarTaxa){
			vlrBoleto = Double.parseDouble(Real.formatStringToDB(cr.getValor_titulo()));
			valorBoleto = vlrBoleto + new Double(cb.getTaxa_boleto());										
		}else{
			valorBoleto = Double.parseDouble(Real.formatStringToDB(cr.getValor_titulo()));										
		}

		//Sacado
		Cliente sacadoCliente = cr.getCliente();
		
		Endereco enderecoSacado = null;

		String[] os = nDoc.split("OS");
		Ose ose =null;	
		Osi osi =null;	
		if(os.length > 1 && controle.equals("SERVICO")){
			ose = OseDAO.find(Integer.parseInt(os[1].split("-")[0]));
		}else if(os.length > 1 && controle.equals("ASSISTENCIA")){
			osi = OsiDAO.find(Integer.parseInt(os[1]));
		}
		
		if(!ContasReceberDAO.allowNdocManual(nDoc)){
		//	String codContrato = nDoc.split("/")[0].toString();
			enderecoSacado = sacadoCliente.getEndereco_principal();
			
		}else if(ose!=null){
			enderecoSacado = ose.getEnd();
		}else if(osi!=null){
			enderecoSacado = osi.getEnd();										
		}else{
			//Endereco
			Query qEndereco = em.createQuery("select e from Endereco e where e.clientes =:cliente and e.principal =:principal", Endereco.class);
			qEndereco.setParameter("cliente", sacadoCliente);
			qEndereco.setParameter("principal", true);
			
			
			if(qEndereco.getResultList().size() == 1){
				enderecoSacado = (Endereco)qEndereco.getSingleResult();
			}
		}
		
		if(sacadoCliente != null && enderecoSacado != null){

			Datas datas = Datas.novasDatas()
					.comDocumento(diaEmissao, mesEmissao, anoEmissao)
					.comProcessamento(Calendar.getInstance().get(Calendar.DATE), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR))
					.comVencimento(diaVencimento, mesVencimento, anoVencimento);  

			Emissor emissor= null; 
			
			
			if(cb != null && cb.getId() == 5){
			
			//sicred
				emissor = Emissor.novoEmissor()  
			        .comCedente(OpusERP4UI.getEmpresa().getRazao_social())  
			        .comAgencia(Integer.parseInt(cb.getAgencia_banco())).comDigitoAgencia('8')  
			        .comContaCorrente(cb.getCod_beneficiario())  
			        .comNumeroConvenio(cb.getConvenio() != null && !cb.getConvenio().equals("") ? Integer.parseInt(cb.getConvenio()) : 0).comDigitoContaCorrente('8')									           
			        .comCarteira(cb.getCarteira() != null && !cb.getCarteira().equals("") ? Integer.parseInt(cb.getCarteira()) : 0)  
			        .comNossoNumero(nossoNumero)
			        .comPostoBeneficiario(cb.getPosto_beneficiario())
			        .comEndereco(OpusERP4UI.getEmpresa().getEndereco()+", "+OpusERP4UI.getEmpresa().getNumero()+" "+OpusERP4UI.getEmpresa().getBairro()+" - "+OpusERP4UI.getEmpresa().getCidade()+" - "+OpusERP4UI.getEmpresa().getUf());
			
			
			}
			
			
			
			
			
			if(cb != null && cb.getId() == 	2){
			//bb
			
			emissor = Emissor.novoEmissor()  
		            .comCedente(OpusERP4UI.getEmpresa().getRazao_social())  
		            .comAgencia(Integer.parseInt(cb.getAgencia_banco())).comDigitoAgencia('8')  
		            .comContaCorrente(cb.getN_conta())  
		            .comNumeroConvenio(Integer.parseInt(cb.getConvenio())).comDigitoContaCorrente('8')									           
		            .comCarteira(Integer.parseInt(cb.getCarteira()))  
		            .comNossoNumero(nossoNumero)
		            .comEndereco(OpusERP4UI.getEmpresa().getEndereco()+", "+OpusERP4UI.getEmpresa().getNumero()+" "+OpusERP4UI.getEmpresa().getBairro()+" - "+OpusERP4UI.getEmpresa().getCidade()+" - "+OpusERP4UI.getEmpresa().getUf());  
			}
			
			
			

	        Sacado sacado = Sacado.novoSacado()  
	        		.comNome(nomeCliente)  
	        		.comCpf(sacadoCliente.getDoc_cpf_cnpj())  
	        		.comEndereco(enderecoSacado.getEndereco()+","+enderecoSacado.getNumero())  
	        		.comBairro(enderecoSacado.getBairro())  
	        		.comCep(enderecoSacado.getCep())  
	        		.comCidade(enderecoSacado.getCidade())  
	        		.comUf(enderecoSacado.getUf())  
	        		.comReferencia(enderecoSacado.getReferencia())
	        		.comComplemento(enderecoSacado.getComplemento());

	        
	        Banco banco = null;
	        
//	        if(cb.getCod_banco().equals("748")){								        
//	        	banco= new Sicredi();
//	        }
//	        
//	        if(cb.getCod_banco().equals("001")){								        
//	        	banco= new BancoDoBrasil();
//	        }
	        
	        
	        String n_numero = "";
	        if(cr.getN_numero() != null){
	        	n_numero = cr.getN_numero();
	        	
	        	
	        	if(cr.getControle().equals("ACESSO-PRE") || 
	        			cr.getControle().equals("ACESSO-POS") || 
	        			cr.getControle().equals("SERVICO")|| 
	        			cr.getControle().equals("PRODUTO")||								        			
	        			cr.getControle().equals("ALUGUEL") ||
	        			cr.getControle().equals("ASSISTENCIA")){
	        		
	        		banco= new BancoDoBrasil();
	        		cb = em.find(ContaBancaria.class, 2);
	        		
	        	}
	        }
	        
	        if(cr.getN_numero_sicred() != null){
	        	n_numero = cr.getN_numero_sicred();
	        	
	        	if(cr.getControle().equals("ACESSO-PRE") || 
	        			cr.getControle().equals("ACESSO-POS") || 
	        			cr.getControle().equals("SERVICO") ||								        			
	        			cr.getControle().equals("PRODUTO")||								        			
	        			cr.getControle().equals("ALUGUEL") ||
	        			cr.getControle().equals("ASSISTENCIA")){
	        		
	        		banco= new Sicredi();
	        		cb = em.find(ContaBancaria.class, 5);
	        		
	        	}
	        }
	        
	        
	        if(banco == null){
	        	banco= new BancoDoBrasil();
	        	cb = em.find(ContaBancaria.class, 2);
	        }

	        
			Boleto boleto1 = Boleto.novoBoleto()
					.comCodBoleto(cr.getId())
		            .comBanco(banco)  
		            .comDatas(datas)						              
		            .comEmissor(emissor)  
		            .comSacado(sacado)  
		            .comValorBoleto(valorBoleto)  
		            .comNumeroDoDocumento(nDoc)  
		            .comInstrucoes(cb.getInstrucoes1(), cb.getInstrucoes2(), cb.getInstrucoes3())							            
		            .comDescricoes(cb.getDemonstrativo1(), cb.getDemonstrativo2(), cb.getDemonstrativo3())
		            .comNossoNumero(n_numero)
		            .comContaBancaria(cb) 
		            .comQtd(cr.getQuantidade())
		            .comCnpj(OpusERP4UI.getEmpresa().getCnpj())
		            .comControle(cr.getControle());		
			
			boleto1.setStatus(cr.getStatus()); 
			
			return boleto1;
		}
		
		
		return null;
	}
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
}
