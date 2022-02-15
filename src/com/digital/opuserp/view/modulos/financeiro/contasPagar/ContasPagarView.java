package com.digital.opuserp.view.modulos.financeiro.contasPagar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.joda.time.LocalDate;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlertaPendenciaDAO;
import com.digital.opuserp.dao.AlteracoesContasPgDAO;
import com.digital.opuserp.dao.ContasPagarDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.AlteracoesContasPg;
import com.digital.opuserp.domain.AlteracoesContasReceber;
import com.digital.opuserp.domain.ContasPagar;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.financeiro.contasPagar.Adiarconta.AdiarTituloEvent;
import com.digital.opuserp.view.modulos.financeiro.contasPagar.BaixarContasPagar.BaixarTituloEvent;
import com.digital.opuserp.view.modulos.financeiro.contasPagar.ContasPagarEditor.NovoTituloEvent;
import com.digital.opuserp.view.util.Notify;
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
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class ContasPagarView extends VerticalLayout implements GenericView {

	JPAContainer<ContasPagar> container;
	
	Item item;
	Table tb;
	ComboBox cbStatus;
	ComboBox cbTipo;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	Button btBaixar;
	Button btReabrir;
	Button btHistorico;
	Button btAdiar;
	HorizontalLayout hlFloat;
	
	Integer qtdRegistros = 0;
	double vlTotal = new Double(Double.parseDouble("0"));
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	ShortcutListener slBaixar;
	boolean excluirDefinitivamente = false;
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	public ContasPagarView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtTransferencia());
			hlButons.addComponent(BuildbtAceitar());
			hlButons.addComponent(BuildbtCancelar());
			hlButons.addComponent(BuildbtBaixar());
			hlButons.addComponent(BuildbtReabrir());
			hlButons.addComponent(BuildbtAdiar());
			hlButons.addComponent(BuildbtHistorico());
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildComboPeriodo());
					addComponent(buildComboStatus());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
//			refresh(tfBusca.getValue());
			
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			//hlFloat.addComponent(lbRegistros());			
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar | Ctrl+B - Baixar");
			lbLegend.setWidth("395px");
			hlFloat.addComponent(lbLegend);
			//hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			
			addComponent(hlFloat);
			
			
			setExpandRatio(tb, 1);
		}
	}


	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
		addFilter(tfBusca.getValue());
//		btEditar.setEnabled(false);
//		btExcluir.setEnabled(false);
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ContasPagarView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
			btBaixar.addShortcutListener(buildShortCutBaixar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btEditar != null || slEditar != null){
				btEditar.removeShortcutListener(slEditar);			
			}
			
			if(btBaixar != null || slBaixar != null){
				btBaixar.removeShortcutListener(slBaixar);			
			}
		}
	}
	
	public Label lbRegistros(){
				
		lbRegistros = new Label();
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
				btEditar.click();
			}
		};
		return slEditar;
	}
	
	public ShortcutListener buildShortCutBaixar(){
		slBaixar = new Button.ClickShortcut(btBaixar, KeyCode.B, ModifierKey.CTRL) {		
			
			@Override
			public void handleAction(Object sender, Object target) {
				btBaixar.click();
			}
		};
		return slBaixar;
	}
	
	public JPAContainer<ContasPagar> buildContainer(){
		container = JPAContainerFactory.makeBatchable(ContasPagar.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));	
		container.addNestedContainerProperty("conta.descricao");
		container.addNestedContainerProperty("fornecedor.razao_social");
		
		if(cbStatus != null && cbStatus.getValue() != null){
			
			if(cbStatus.getValue().equals("PAGO")){		
				container.sort(new String[]{"data_pago"}, new boolean[] {false});
				container.addContainerFilter(Filters.eq("status", "PAGO"));
			}else{
				container.sort(new String[]{"data_vencimento"}, new boolean[] {true});
				container.addContainerFilter(Filters.eq("status", "A PAGAR"));
			}
		}
		container.getItemIds();
		
		//for(Object o: container.getItemIds()){			
		//	vlTotal = vlTotal +	container.getItem(o).getEntity().getValor_titulo();
		//}
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		
		tb = new Table(null, buildContainer()){
			
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
			
				if (v instanceof Date) {
				      Date dateValue = (Date) v;
				      return new SimpleDateFormat("dd/MM/yyyy").format(dateValue);
				}
				
				if(colId.equals("valor_titulo") || colId.equals("valor_pagamento")){
					
					
//					if(cbStatus.getValue().toString().equals("A PAGAR")){
//						if(colId.equals("valor_titulo")){
//							double vlr = Real.formatStringToDBDouble(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
//							vlTotal = vlTotal + vlr;
//							lbRegistros.setCaption(String.valueOf(container.size()) + " Registros Encontrados | Valor Total R$ "+Real.formatDbToString(String.valueOf(vlTotal)));
//						}
//					}
//					
//					if(cbStatus.getValue().toString().equals("PAGO")){
//						if(colId.equals("valor_pagamento")){
//							double vlr = Real.formatStringToDBDouble(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
//							vlTotal = vlTotal + vlr;
//							lbRegistros.setCaption(String.valueOf(container.size()) + " Registros Encontrados | Valor Total R$ "+Real.formatDbToString(String.valueOf(vlTotal)));
//						}
//					}
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){										
						return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());						
					}
				}	
				
				
				
				
				return super.formatPropertyValue(rowId, colId, property);
			}
			
			
			
		};		

		tb.setSizeFull();
		tb.setMultiSelect(true); 
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("data_cadastro", true);				
		tb.setColumnCollapsed("tipo", true);				

		tb.setVisibleColumns(new Object[] {"id","n_doc","termo","conta.descricao","tipo","fornecedor.razao_social","descricao","parcela","valor_titulo",
				"data_vencimento","valor_pagamento","data_pago","data_cadastro"});

		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("termo", "Termo");
		tb.setColumnHeader("n_doc", "N° Documento");
		tb.setColumnHeader("tipo", "Tipo");
		tb.setColumnHeader("conta.descricao", "Conta");
		tb.setColumnHeader("fornecedor.razao_social", "Fornecedor");
		tb.setColumnHeader("descricao", "Descrição");
		tb.setColumnHeader("parcela", "Parcela");
		tb.setColumnHeader("valor_titulo", "Valor");
		tb.setColumnHeader("data_vencimento", "Data Vencimento");
		tb.setColumnHeader("valor_pagamento", "Valor Pago");
		tb.setColumnHeader("data_pago", "Data Pagamento");
		tb.setColumnHeader("data_cadastro", "Data Cadastro");
		
		tb.setColumnAlignment("valor_titulo", Align.RIGHT);
		tb.setColumnAlignment("valor_pagamento", Align.RIGHT);
		
		

		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			
			public String getStyle(Table source, Object itemId, Object propertyId) {

				
				
				
				 
				
				if (propertyId == null){
	                    return "row-header-default"; // Will not actually be visible
				}else{
	               
		                
	            	try{
	                	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		                Date data_ex = (Date)source.getItem(itemId).getItemProperty("data_vencimento").getValue();
	 
					        
			            if(cbStatus.getValue().equals("PAGO")){
	                		return "row-header-pago";
	                	}else if(!sdf.parse(sdf.format(data_ex)).after(sdf.parse(sdf.format(new Date())))){
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

		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {	
				if(event.getProperty().getValue() != null && !cbStatus.getValue().toString().equals("PAGO")){
					
					final Set<Object> selecteds = (Set<Object>)event.getProperty().getValue();
					
					btBaixar.setEnabled(true);
					if(selecteds.size() == 1){
						btEditar.setEnabled(true);
						btHistorico.setEnabled(true);
						btAdiar.setEnabled(true);						
					}else{
						btEditar.setEnabled(false);
						btHistorico.setEnabled(false);
						btAdiar.setEnabled(false);
					}
					
					if(selecteds.size() >= 1){
						btExcluir.setEnabled(true);						
					}else{
						btExcluir.setEnabled(false);
					}
					
					if(selecteds.size() > 1){
						btBaixar.setEnabled(false);
					}

					btReabrir.setEnabled(false);
				}else if(event.getProperty().getValue() != null && cbStatus.getValue().toString().equals("PAGO")){
					
					final Set<Object> selecteds = (Set<Object>)event.getProperty().getValue();
					
					btEditar.setEnabled(false);
					btBaixar.setEnabled(false);
					btAdiar.setEnabled(false);
					
					if(selecteds.size() == 1){
						btHistorico.setEnabled(true);
					}else{
						btHistorico.setEnabled(false);
					}
					
					if(selecteds.size() >= 1){
						btExcluir.setEnabled(true);
						btReabrir.setEnabled(true);
					}
				
				}else{
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
					btHistorico.setEnabled(false);
					btReabrir.setEnabled(false);
					btBaixar.setEnabled(false);
					btAdiar.setEnabled(false);
				}
			}
		});

		return tb;
	}
	
	public ComboBox buildComboStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		cbStatus.addItem("A PAGAR");
		cbStatus.addItem("PAGO");				
				

		cbStatus.setImmediate(true);
		cbStatus.select("A PAGAR");
		
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {

				vlTotal = 0;
				addFilter(tfBusca.getValue());
			}
		});	
		
		return cbStatus;	
	}
	
	public ComboBox buildComboPeriodo(){
		cbTipo = new ComboBox();
		cbTipo.setNullSelectionAllowed(false);
		cbTipo.setTextInputAllowed(false);
		cbTipo.addItem("FIXA");
		cbTipo.addItem("VARIAVEL");
		cbTipo.addItem("TODAS");

		cbTipo.setImmediate(true);
		cbTipo.select("TODAS");
		
		cbTipo.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {

				addFilter(tfBusca.getValue());

			}
		});	
		
		return cbTipo;	
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
				vlTotal = 0;
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}

	@Override
	public void addFilter(String s) {
		vlTotal = 0;
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));

		if(cbStatus.getValue().equals("PAGO")){		
			container.sort(new String[]{"data_pago"}, new boolean[] {false});
		}else{
			container.sort(new String[]{"data_vencimento"}, new boolean[] {true});
		}
		
		if(!cbTipo.getValue().equals("TODAS")){			
			container.addContainerFilter(Filters.eq("tipo", cbTipo.getValue().toString()));
		}
			
		container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		
		if(s != null && !s.equals("") && !s.isEmpty()){

			Object[] collums = tb.getVisibleColumns();	
		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			
			try {
				Integer cod = Integer.valueOf(s);			
				
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
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date = sdf.parse(s);
				
					
					
						for(Object c:collums){		 
							if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
								filtros.add(Filters.eq(c.toString(), date));
							}			
						}	
					
				} catch (Exception e2) {
					
					try {
						Double vl = Real.formatStringToDBDouble(s);
											
						for(Object c:collums){		 
							
							if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Double.class){					   	
								filtros.add(Filters.eq(c.toString(), vl));
							}			
						}	
						
					} catch (Exception e3) {
						
						for(Object c:collums){		 
							
							if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
								filtros.add(new Like(c.toString(), "%"+s+"%", false));
							}	
						}
					}
				}
			}
			
			container.addContainerFilter(Filters.or(filtros));
		
		}
				
		container.applyFilters();
		

		
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
		btHistorico.setEnabled(false);
		btReabrir.setEnabled(false);
		btBaixar.setEnabled(false);
		btAdiar.setEnabled(false);
		
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
				final BeanItem<ContasPagar> novoConta = new BeanItem<ContasPagar>(new ContasPagar());
				
				ContasPagarEditor contasEditor = new ContasPagarEditor(novoConta,"Novo Título", true);
				contasEditor.addListerner(new ContasPagarEditor.NovoTituloListerner() {
					
					@Override
					public void onClose(NovoTituloEvent event) {
						if(event.isConfirm()){
							EntityManager em = ConnUtil.getEntity();
						try {
													
							em.getTransaction().begin();
							
							ContasPagarDAO contaDAO = new ContasPagarDAO();

							if(event.getIntervalo().equals("ANUAL")){
								for (int i = 0; i < Integer.parseInt(event.getQtd()); i++) {
									
									LocalDate localDate = new LocalDate(event.getDataVenciemento());
									LocalDate moreDays = localDate.plusYears(i);	
									Date dataBoleto = moreDays.toDate();
									
									contaDAO.gerarBoletos(new ContasPagar(event.getConta(), Real.formatStringToDBDouble(event.getValor()), new Date(), dataBoleto,
											"A PAGAR", event.getTipo(), event.getTermo(), event.getFornecedor(), event.getDescricao(),
											 event.getIntervalo(), OpusERP4UI.getEmpresa().getId(),
											 event.getN_cod()+"-"+String.format("%02d", i+1)+"/"+String.format("%02d",Integer.parseInt(event.getQtd())),
											 String.format("%02d", i+1)+"/"+String.format("%02d",Integer.parseInt(event.getQtd()))));
									
								}
							}
							
								if(event.getIntervalo().equals("MENSAL")){
									for (int i = 0; i < Integer.parseInt(event.getQtd()); i++) {
										
										LocalDate localDate = new LocalDate(event.getDataVenciemento());
										LocalDate moreDays = localDate.plusMonths(i);	
										Date dataBoleto = moreDays.toDate();
										
										contaDAO.gerarBoletos(new ContasPagar(event.getConta(), Real.formatStringToDBDouble(event.getValor()), new Date(), dataBoleto,
												"A PAGAR", event.getTipo(), event.getTermo(), event.getFornecedor(), event.getDescricao(),
												 event.getIntervalo(), OpusERP4UI.getEmpresa().getId(),
												 event.getN_cod()+"-"+String.format("%02d", i+1)+"/"+String.format("%02d",Integer.parseInt(event.getQtd())),
												 String.format("%02d", i+1)+"/"+String.format("%02d",Integer.parseInt(event.getQtd()))));
										
									}
								}
								
								if(event.getIntervalo().equals("QUINZENAL")){
									
									LocalDate localDate = new LocalDate(event.getDataVenciemento());
									
									for (int i = 0; i < Integer.parseInt(event.getQtd()); i++) {										
										LocalDate moreDays = localDate.plusWeeks(i);	
										Date dataBoleto = moreDays.toDate();
										localDate = localDate.plusWeeks(1);
										
										contaDAO.gerarBoletos(new ContasPagar(event.getConta(), Real.formatStringToDBDouble(event.getValor()), new Date(), dataBoleto,
												"A PAGAR", event.getTipo(), event.getTermo(), event.getFornecedor(), event.getDescricao(),
												 event.getIntervalo(), OpusERP4UI.getEmpresa().getId(),
												 event.getN_cod()+"-"+String.format("%02d", i+1)+"/"+String.format("%02d",Integer.parseInt(event.getQtd())),
												 String.format("%02d", i+1)+"/"+String.format("%02d",Integer.parseInt(event.getQtd()))));
									}
								}
								
								if(event.getIntervalo().equals("SEMANAL")){
									for (int i = 0; i < Integer.parseInt(event.getQtd()); i++) {
										
										LocalDate localDate = new LocalDate(event.getDataVenciemento());
										LocalDate moreDays = localDate.plusWeeks(i);	
										Date dataBoleto = moreDays.toDate();
										
										contaDAO.gerarBoletos(new ContasPagar(event.getConta(), Real.formatStringToDBDouble(event.getValor()), new Date(), dataBoleto,
												"A PAGAR", event.getTipo(), event.getTermo(), event.getFornecedor(), event.getDescricao(),
												event.getIntervalo(), OpusERP4UI.getEmpresa().getId(),
												event.getN_cod()+"-"+String.format("%02d", i+1)+"/"+String.format("%02d",Integer.parseInt(event.getQtd())),
												String.format("%02d", i+1)+"/"+String.format("%02d",Integer.parseInt(event.getQtd()))));
									}
								}
														
							em.getTransaction().commit();								
							refresh();							
													
						} catch (Exception e) {							
							e.printStackTrace();
						}
					}
						
					}
				});
				
				
				getUI().addWindow(contasEditor);
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um Boleto", Type.ERROR_MESSAGE);
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
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
				{
				if(tb.getValue()!=null){
					
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					if(selecteds.size() == 1){
					
							item = tb.getItem(selecteds.toArray()[0]);							
							final ContasPagar cp = ContasPagarDAO.find((Integer) item.getItemProperty("id").getValue());
							
							ContasPagarEditor contasEditor = new ContasPagarEditor(tb.getItem(selecteds.toArray()[0]), "Editar Conta a Pagar", true);
							contasEditor.addListerner(new ContasPagarEditor.NovoTituloListerner() {
								
								@Override
								public void onClose(NovoTituloEvent event) {
									if(event.isConfirm()){
										container.commit();
										tb.focus();
									}
									
								}
							});
							
							contasEditor.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									tb.focus();
									refresh();						
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou uma Conta | COD: "+cp.getId().toString()));
									AlteracoesContasPgDAO.save(new AlteracoesContasPg(null, "EDITOU", cp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
								}
							});
							getUI().addWindow(contasEditor);
					}
				}
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Alterar um Boleto", Type.ERROR_MESSAGE);
				}
			}
		});
		btEditar.setEnabled(false);
		
//			ShortcutListener slbtEdit = new ShortcutListener("Editar", ShortcutAction.KeyCode.ENTER,null) {
//			
//			@Override
//			public void handleAction(Object sender, Object target) {
//				btEditar.click();
//			}
//		};
//		
//		btEditar.addShortcutListener(slbtEdit);	
					
		return btEditar;
	}

	
	private Button BuildbtAdiar() {
		btAdiar = new Button("Adiar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Adiar"))				
				{
				if(tb.getValue()!=null){
					
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					if(selecteds.size() == 1){
						item = tb.getItem(selecteds.toArray()[0]);
						final ContasPagar cp = ContasPagarDAO.find((Integer) item.getItemProperty("id").getValue());
						
						Adiarconta contasEditor = new Adiarconta(tb.getItem(selecteds.toArray()[0]), "Adiar Conta a Pagar", true);
						contasEditor.addListerner(new Adiarconta.AdiarTituloListerner() {
							
							@Override
							public void onClose(AdiarTituloEvent event) {
								if(event.isConfirm()){
									container.commit();
									tb.focus();
								}
								
							}
						});
						
						contasEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
								refresh();						
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou uma Conta | COD: "+cp.getId().toString()));
								AlteracoesContasPgDAO.save(new AlteracoesContasPg(null, "ADIOU", cp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
								AlertaPendenciaDAO.removePendencia(codSubModulo, cp.getId());	
							}
						});
						getUI().addWindow(contasEditor);
					}
				}
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Alterar um Boleto", Type.ERROR_MESSAGE);
				}
				
			}
		});
		
		btAdiar.setEnabled(false);
		return btAdiar;
	}
	
	
	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {						
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
				
				GenericDialog gDialog = null;

				excluirDefinitivamente = false;		
				
				
				if(tb.getValue()!=null){

					gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente excluir o(s) Boleto(s) Selecionado(s) ?", "SIM", "NÃO", "250px", "151px", true, true);
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){
										
										final EntityManager em = ConnUtil.getEntity();
										final Set<Object> selecteds = (Set<Object>)tb.getValue();
											
										for (Object object : selecteds) {
																							
																							
												
											Integer codCp = Integer.parseInt(tb.getItem(object).getItemProperty("id").getValue().toString());
											final ContasPagar cp = em.find(ContasPagar.class, codCp);
											
											em.getTransaction().begin();
											
											cp.setStatus("EXCLUIDO");
											em.merge(cp);																											
											
											em.getTransaction().commit();
											
											AlertaPendenciaDAO.removePendencia(codSubModulo, cp.getId());
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma Conta  | COD: "+cp.getId().toString()));
											AlteracoesContasPgDAO.save(new AlteracoesContasPg(null, "EXCLUIU", cp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
												
												
										}
										
										refresh();
										
										Notify.Show("Boleto Excluído com Sucesso!", Notify.TYPE_SUCCESS);										
										tb.focus();
										
									}							
								}
							});
							
							gDialog.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									refresh();
									tb.focus();
								}
							});
						
						getUI().addWindow(gDialog);

					}
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Excluir um Boleto", Type.ERROR_MESSAGE);
			 }
			}
		});
		btExcluir.setEnabled(false);
				
		return btExcluir;
	}

	
	public Button BuildbtBaixar() {
		btBaixar= new Button("Baixar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Baixar"))				
				{
					
					if(tb.getValue() != null){
						
						final Set<Object> selecteds = (Set<Object>)tb.getValue();
						if(selecteds.size() == 1){
	
								final EntityManager em = ConnUtil.getEntity();
				
								Integer codCp = Integer.parseInt(tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue().toString());
								final ContasPagar cp = em.find(ContasPagar.class, codCp);
								
								BaixarContasPagar BaixarCPG = new BaixarContasPagar(tb.getItem(selecteds.toArray()[0]), "Baixar Contas a Pagar",true);
								
								BaixarCPG.addListerner(new BaixarContasPagar.BaixarTituloListerner() {
									
									@Override
									public void onClose(BaixarTituloEvent event) {
										if(event.isConfirm()){
											container.commit();
											tb.focus();
											refresh();
											
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Baixou uma Conta  | COD: "+cp.getId().toString()));
											AlteracoesContasPgDAO.save(new AlteracoesContasPg(null, "BAIXOU", cp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));	
											AlertaPendenciaDAO.removePendencia(codSubModulo, cp.getId());
										}						
									}
								});
								
								getUI().addWindow(BaixarCPG);
				
						}
				
					}
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Baixar um Boleto", Type.ERROR_MESSAGE);
				}
			}
		});
		btBaixar.setEnabled(false);
		
		
//		btBaixar.addShortcutListener(
//                new Button.ClickShortcut(btBaixar, KeyCode.B, ModifierKey.CTRL));	
		
		
		
		return btBaixar;
	}
	
	
	public Button BuildbtReabrir() {
		btReabrir= new Button("Estorno", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Estornar"))				
				{
				

				if(tb.getValue()!=null){
					GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Re-Abrir o Boleto Selecionado ?", true, true);
					
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							
							EntityManager em = ConnUtil.getEntity();
						
							if(event.isConfirm()){
								
								
								final Set<Object> selecteds = (Set<Object>)tb.getValue();
									
								for (Object object : selecteds) {																		
																					
										em.getTransaction().begin();
										Integer codCp = Integer.parseInt(tb.getItem(object).getItemProperty("id").getValue().toString());
										
										
										ContasPagar cp = em.find(ContasPagar.class, codCp);
										
										cp.setStatus("A PAGAR");
										cp.setForma_pgto(null);
										cp.setData_pago(null);	
										cp.setValor_pagamento(null);
																		
										em.merge(cp);									
										em.getTransaction().commit();
										
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Estornou uma Conta  | COD: "+cp.getId().toString()));
										AlteracoesContasPgDAO.save(new AlteracoesContasPg(null, "ESTORNOU", cp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
								}
								
							}
							
						}
					});
					
					gd.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {						
							tb.focus();			
							refresh();
						}
					});
					
					getUI().addWindow(gd);
										
				}

				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Estornar um Boleto", Type.ERROR_MESSAGE);
				}
			}
		});
		btReabrir.setEnabled(false);
		return btReabrir;
	}
	
	private Button BuildbtHistorico() {
		btHistorico = new Button("Histórico", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Historico"))				
				{
					if(tb.getValue() != null){
						final Set<Object> selecteds = (Set<Object>)tb.getValue();
						if(selecteds.size() == 1){

							Integer codContapg = (Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue();
							HistoricoAlteracoesContasPg histCpg = new HistoricoAlteracoesContasPg(true, true,codContapg);
							
							getUI().addWindow(histCpg);
						}
					}
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para ver Historico de um Boleto", Type.ERROR_MESSAGE);
				}
				
			}
		});
		
		btHistorico.setEnabled(false);
		return btHistorico;
	}
	
	
	
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}
