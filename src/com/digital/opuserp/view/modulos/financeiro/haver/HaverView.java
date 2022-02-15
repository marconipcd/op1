package com.digital.opuserp.view.modulos.financeiro.haver;

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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import me.legrange.mikrotik.ResultListener;

import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;
import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.HaverDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.domain.HaverDetalhe;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.ScpUtil;
import com.digital.opuserp.view.modulos.financeiro.haver.HaverDuplicadoMesclarEditor.HaverMesclarEvent;
import com.digital.opuserp.view.modulos.financeiro.haver.HaverEditor.HaverEvent;
import com.digital.opuserp.view.modulos.financeiro.haver.PagarEditor.PagarHaverEvent;
import com.digital.opuserp.view.util.Notify;
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
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
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

public class HaverView extends VerticalLayout implements GenericView {

	private JPAContainer<HaverCab> container;
	
	private Table tb;
	private TextField tfBusca;
	private Button btNovo;
	private Button btLiberar;
	private Button btDetalhamento;
	private Button btHistorico;
	private Button btImprimir;
	private Button btEditar;
	private Button btPagar;
	private Label lbRegistros;

	private HorizontalLayout hlFloat;	
	
	private ShortcutListener slNovo;
	private ShortcutListener slEditar;
	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	
	public HaverView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtNovoHaver());
			hlButons.addComponent(BuildbtPagar());
			hlButons.addComponent(BuildbtDetalhamento());
			hlButons.addComponent(BuildbtImprimir());
			
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildCbStatus());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildlbRegistros());
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
		hlFloat.replaceComponent(lbRegistros, buildlbRegistros());
		
		btDetalhamento.setEnabled(false);
		//btHistorico.setEnabled(false);
		btImprimir.setEnabled(false);
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof HaverView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btDetalhamento.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btEditar != null || slEditar != null){
				btDetalhamento.removeShortcutListener(slEditar);			
			}
		}
	}
	
	public Label buildlbRegistros(){
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
				btEditar.click();
			}
		};
		return slEditar;
	}
	public JPAContainer<HaverCab> buildContainer(){
		container = JPAContainerFactory.makeBatchable(HaverCab.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addNestedContainerProperty("cliente.doc_cpf_cnpj");
		container.addNestedContainerProperty("cliente.nome_razao");
		//container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
	
		if(cbStatus != null && cbStatus.getValue() != null){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		}

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
				
				if(colId.equals("valor_total")){
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null) {										
					return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
		
					}
				}	
				
				return super.formatPropertyValue(rowId, colId, property);
			}	
		};		
		

		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);

		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("cliente.doc_cpf_cnpj", "CPF");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");		
		tb.setColumnHeader("data_alteracao", "Data Alteração");
		tb.setColumnHeader("valor_total", "Valor");
		
		tb.setColumnHeader("status", "Status");
		
		
		tb.setColumnAlignment("valor_total", Align.RIGHT);	
		tb.setVisibleColumns(new Object[] {"id","cliente.doc_cpf_cnpj","cliente.nome_razao","valor_total","data_alteracao","status"});
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btDetalhamento.setEnabled(true);
					//btHistorico.setEnabled(true);
					btImprimir.setEnabled(true);
//					btLiberar.setEnabled(true);
				}else{
					btDetalhamento.setEnabled(false);
//					btHistorico.setEnabled(false);
					btImprimir.setEnabled(false);
	//				btLiberar.setEnabled(false);
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {
                
                if (propertyId == null)
                    return "row-header-default"; 
               
                Item item = source.getItem(itemId);
                   
                	if(item.getItemProperty("status").getValue() != null){
                		String status = item.getItemProperty("status").getValue().toString();
	               
		                if(status.equals("UTILIZADO")){
		                    return "row-header-bloqueado"; 
		                }else{
		                	return "row-header-osi-encaminhado";
		                }
	                }
                	
                	return "row-header-default"; 
            	}
        });
		
			
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()   && tb.getValue() != null){
					btDetalhamento.click();
				}				
			}
		});
		

		
		return tb;
	}
	
	private ComboBox cbStatus;
	public ComboBox buildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false); 
		cbStatus.addItem("DISPONIVEL");
		cbStatus.addItem("UTILIZADO");
				
		cbStatus.select("DISPONIVEL");
		
		cbStatus.setImmediate(true);
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		
		return cbStatus;
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
				refresh();
				addFilter(event.getText());
			}
		});
		return tfBusca;
		
	}

	

	@Override
	public void addFilter(String s) {
		container.removeAllContainerFilters();
/*		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));*/
		
		if(cbStatus != null && cbStatus.getValue() != null){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		}
		
		
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
			
			
			for(Object c:collums){		 
				
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}			
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildlbRegistros());
	}
	
	public Button BuildbtDetalhamento(){
		btDetalhamento = new Button("Detalhamento", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar"))				
				{
					HaverEditorDetalhamento haverEditor = new HaverEditorDetalhamento(tb.getItem(tb.getValue()), "Detalhamento de Haver", true);
					getUI().addWindow(haverEditor); 
					haverEditor.focus();
					//HaverEditor haverEditor = new HaverEditor(tb.getItem(tb.getValue()), "Visualizar", true,true);
					//getUI().addWindow(haverEditor);					
				}else{
					Notify.Show("Você não Possui Permissão para Visualizar um Haver", Notify.TYPE_ERROR);
				}				
			}
		});
		
		btDetalhamento.setEnabled(false);
		return btDetalhamento;
	}
	
	public Button BuildbtHistorico(){
		btHistorico= new Button("Histórico", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Historico"))				
				{
				
					HistoricoAlteracoes historivoView = new HistoricoAlteracoes(true, true, (Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
					getUI().addWindow(historivoView);					
				}else{
					Notify.Show("Você não Possui Permissão para Visualizar o Histórico desse Haver", Notify.TYPE_ERROR);
				}
			}
		});
		
		btHistorico.setEnabled(false);
		return btHistorico;
	}
	
	public Button BuildbtImprimir(){
		btImprimir = new Button("Imprimir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Imprimir"))				
				{					
//					try{						
//												
//						InputStream stream = new ByteArrayInputStream(HaverDAO.getLayoutRecibo(HaverDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue())).getBytes());											
//						
//						String urlPrint = "";
//						if(!OpusERP4UI.getEmpresa().getId().equals(3)){
//							urlPrint = "http://192.168.20.145:631/printers/BEMA_PRODUCAO";
//						}else{
//							urlPrint = "http://192.168.20.145:631/printers/BEMA_GRAFICA";
//						}
//						
//						CupsPrinter printer = new CupsPrinter(new URL(urlPrint), "BEMA_PRODUCAO", true);		        	    
//		        	   						
//						HashMap<String, String> attributes = new HashMap<String, String>();	    	   
//		        	    	        	    		        	    		        	    
//		        	    PrintJob printJob = new PrintJob.Builder(stream)
//		        	    							.jobName("OpusERP4")
//		        	    							.userName(OpusERP4UI.getUsuarioLogadoUI().getUsername())
//		        	    							.copies(1)  	        	    							
//		        	    							.build();
//		        	    
//		        	    PrintRequestResult printRequestResult = printer.print(printJob);		
//		        	    
//		        	    if(printRequestResult.isSuccessfulResult()){
//		        	    	Notify.Show("Recibo Esta Sendo Impresso",Notify.TYPE_SUCCESS);
//		        	    }else{		        	    	
//		        	    	Notify.Show("Houve um Problema ao Tentar Imprimir, Espere um Momento e Tente Novamente",Notify.TYPE_ERROR);
//		        	    }
//		        	    
//		        	    LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Imprimiu um Recibo de Um Haver"));
//						
//					}catch(Exception e){
//						e.printStackTrace();						
//					}
					
					
					
					if(OpusERP4UI.getEmpresa().getId().equals(1) || OpusERP4UI.getEmpresa().getId().equals(3)){
						try{
							
							Haver c = HaverDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
							
							File f = new File("/DJMONITOR/HAVER"+c.getId().toString()+".txt");
							
							BufferedWriter br = new BufferedWriter(new FileWriter(f));  
							 					
							if(f.canWrite()){
							
								StringBuilder s = new StringBuilder();
								String newLine = System.getProperty("line.separator");
								SimpleDateFormat formHora= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");						
								
									
								s.append("                         d i g i t a l "+newLine);
								s.append("   Rua Adjar Maciel, 35 Centro Belo Jardim/PE CEP: 55.150-040"+newLine);
								s.append("        CNPJ: 07.578.965/0001-05     IE: 18.3.050.0329"+newLine);
																										
								s.append(newLine);
								s.append("----------------------------------------------------------------"+newLine);	
								s.append("                       CREDITO EM HAVER");
								s.append(newLine);
								s.append("----------------------------------------------------------------"+newLine);	
								s.append(newLine);
								
								s.append("CODIGO: "+c.getId());		
								s.append(newLine);
								s.append("DATA DE EMISSAO: "+formHora.format(c.getData_emissao()));
								s.append(newLine);
								s.append("EMITIDO POR: "+c.getUsuario());
								s.append(newLine);
								s.append("VALOR: "+Real.formatDbToString(String.valueOf(c.getValor())));
								s.append(newLine);
								s.append("DOCUMENTO: "+c.getDoc());
								s.append(newLine);
								s.append("NUMERO DOC: "+c.getnDoc());
								s.append(newLine);
								s.append("Referente a: DEPOSITO DE CHEQUE");
								s.append(newLine);
								s.append("OBS: E INDISPENSAVEL A APRESENTACAO DESTE CUPOM PARA USO DO CREDITO");								
								s.append(newLine);
								s.append(newLine);
								s.append(newLine);
								s.append("      _____________________________________________________"+newLine);							
								s.append("                        Autorizado por");
								
								br.write(s.toString());  
								br.close();							
								
															
								if(OpusERP4UI.getEmpresa().getId().equals(1)){
									ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@16", "/DJMONITOR/HAVER"+c.getId().toString()+".txt", "/dados/PRINT/DIGITAL/HAVER"+c.getId().toString()+".txt");
								}else if(OpusERP4UI.getEmpresa().getId().equals(3)){
									ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@16", "/DJMONITOR/HAVER"+c.getId().toString()+".txt", "/dados/PRINT/DIGITALGRAFICA/HAVER"+c.getId().toString()+".txt");
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
				}else{				
					Notification.show("Atenção", "Você não Possui Permissão para Imprimir Recibos", Type.ERROR_MESSAGE);				
				}
			}
		});
		
		btImprimir.setEnabled(false);
		return btImprimir;
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
				
				}else{					
					Notify.Show("Você não Possui Permissão para Cadastrar um Haver", Notify.TYPE_ERROR);
				}
			}
		});

		return btNovo;
	}
	
	
	private Component BuildbtNovoHaver() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
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

		return btNovo;
	}
	
	private Component BuildbtPagar() {
		btPagar = new Button("Pagar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Pagar"))				
				{					
					try{
						
						final Item item = tb.getItem(tb.getValue());
						final PagarEditor pagarEditor = new PagarEditor(item, "Pagar Haver", true);
						pagarEditor.addListerner(new PagarEditor.PagarHaverListerner() {
							
							@Override
							public void onClose(PagarHaverEvent event) {
								try{
										if(event.isConfirm()){
											
											double valor_haver = (double)item.getItemProperty("valor_total").getValue();
											final double valor_pgto = event.getValor();
											double novo_valor_haver= valor_haver - valor_pgto;
											
											item.getItemProperty("valor_total").setValue(novo_valor_haver);
											if(novo_valor_haver <= 0){
												item.getItemProperty("status").setValue("UTILIZADO");
											}
											tb.commit();
											container.commit();
											
											EntityItem<HaverCab> entityItem = (EntityItem<HaverCab>)item;
											
											final HaverDetalhe haver_detalhe = new HaverDetalhe(null, entityItem.getEntity(), "SAIDA", event.getValor(), "PAGAMENTO", "", "", "", new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername(), "ATIVO");
											HaverDAO.add(haver_detalhe);
											pagarEditor.close();
												
											GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja imprimir recibo de Pgto. ?", true, true);
											gDialog.setCaptionCANCEL("Sair sem Salvar!");
											gDialog.setCaptionOK("Salvar");
											
											gDialog.addListerner(new GenericDialog.DialogListerner() {
												
												@Override
												public void onClose(DialogEvent event) {
													if(event.isConfirm()){
														try{
															InputStream stream = new ByteArrayInputStream(HaverDAO.getLayoutReciboPagamento(haver_detalhe, valor_pgto).getBytes());											
															
															String urlPrint = "";
															if(!OpusERP4UI.getEmpresa().getId().equals(3)){
																urlPrint = "http://192.168.20.145:631/printers/BEMA_PRODUCAO";
															}else{
																urlPrint = "http://192.168.20.145:631/printers/BEMA_GRAFICA";
															}
															
															CupsPrinter printer = new CupsPrinter(new URL(urlPrint), "BEMA_PRODUCAO", true);		        	    
											        	   						
															HashMap<String, String> attributes = new HashMap<String, String>();	    	   
											        	    	        	    		        	    		        	    
											        	    PrintJob printJob = new PrintJob.Builder(stream)
											        	    							.jobName("OpusERP4")
											        	    							.userName(OpusERP4UI.getUsuarioLogadoUI().getUsername())
											        	    							.copies(1)  	        	    							
											        	    							.build();
											        	    
											        	    PrintRequestResult printRequestResult = printer.print(printJob);		
											        	    
											        	    if(printRequestResult.isSuccessfulResult()){
											        	    	Notify.Show("Recibo Esta Sendo Impresso",Notify.TYPE_SUCCESS);
											        	    }else{		        	    	
											        	    	Notify.Show("Houve um Problema ao Tentar Imprimir, Espere um Momento e Tente Novamente",Notify.TYPE_ERROR);
											        	    }
											        	    
											        	    LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Imprimiu um Recibo de Pgto de Um Haver"));
														}catch(Exception e){
															e.printStackTrace();
														}
													}
												}
											});
													
											getUI().addWindow(gDialog);
										}
				        	    
								}catch(Exception e){
										e.printStackTrace();
								}
							}
						});
						
						getUI().addWindow(pagarEditor);
						
						
								
						
						
					}catch(Exception e){
						e.printStackTrace();						
					}
				}else{				
					Notification.show("Atenção", "Você não Possui Permissão para Imprimir Recibos", Type.ERROR_MESSAGE);				
				}
			}
		});
		
		return btPagar;
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

    
        Button bt1 = new Button("Haver", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar Haver"))				
				{
				
					
					final BeanItem<HaverDetalhe> newItem = new BeanItem<HaverDetalhe>(new HaverDetalhe());
									
					final HaverEditor cEditor = new HaverEditor(newItem, "Novo Haver", true,false);
					cEditor.addListerner(new HaverEditor.HaverListerner() {
						
						@Override						
						public void onClose(HaverEvent event) {
							if(event.isConfirm()){
								
								try {
									
									Cliente cliente = event.getCliente();
									EntityManager em = ConnUtil.getEntity();
									
									
									em.getTransaction().begin();
									
									HaverCab hCab = new HaverCab(null, cliente,  new Date(), "DISPONIVEL");
									em.persist(hCab); 
									HaverDetalhe cAdd = newItem.getBean();	
									cAdd.setHaverCab(hCab); 
									cAdd.setStatus("ATIVO");
									em.persist(cAdd);
									
									em.getTransaction().commit();
									
									Query q = em.createQuery("select hc from HaverCab hc where hc.cliente.doc_cpf_cnpj=:cpf", HaverCab.class);
									q.setParameter("cpf", cliente.getDoc_cpf_cnpj());
								
									if(q.getResultList().size() > 1){
										em.getTransaction().begin();
											
											Integer i = 0;
											HaverCab haver1 = null;
											for (HaverCab haver: (List<HaverCab>) q.getResultList()) {
												if(i == 0){
													haver1 = haver;
												}else{
													Query q3 = em.createQuery("select hd from HaverDetalhe hd where hd.haverCab=:h", HaverDetalhe.class);
													q3.setParameter("h", haver);
													
													for (HaverDetalhe hd: (List<HaverDetalhe>)q3.getResultList()) {
														if(haver1 != null){
															hd.setHaverCab(haver1);
															em.merge(hd);
															em.remove(haver);
														}
													}
												}												
												i++;
											}											
								
										em.getTransaction().commit();
									}
									
									if(q.getResultList().size() ==0){
										
									}
																	
									//HaverEmpresaDAO.add(h, OpusERP4UI.getEmpresa());
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "ABERTA"));
									//AlteracoesHaverDAO.add(new AlteracoesHaver(null, "ABERTO", cAdd, cAdd.getValor(), 0, cAdd.getValor(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									
									cEditor.close();
									refresh();
									Notify.Show("Haver Cadastrado Com Sucesso!", Notify.TYPE_SUCCESS);
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
							refresh();
						}
					});
					
					getUI().addWindow(cEditor);
				}else{					
					Notify.Show("Você não Possui Permissão para Cadastrar um Haver", Notify.TYPE_ERROR);
				}
			  }	
		});
        
        bt1.setPrimaryStyleName("btSubMenu");
        
        l.addComponent(bt1);
        
	}
	
	public Button BuildbtLiberar(){
		btLiberar = new Button("Liberar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Liberar"))				
				{
					if(tb.getValue() != null){
						
						EntityItem<Haver> entityHaver = (EntityItem<Haver>)tb.getItem(tb.getValue());						
						LiberarHaverEditor libHaver = new LiberarHaverEditor(entityHaver.getEntity(), "Liberar Haver", true);
						
						getUI().addWindow(libHaver); 
					}
				}else{
					Notify.Show("Você não Possui Permissão para Liberar um Haver", Notify.TYPE_ERROR);
				}
			}
		});
		
		
		btLiberar.setEnabled(false); 
		return btLiberar;
	}

	@Override
	public Button BuildbtAceitar() {
		return null;
	}

	@Override
	public Button BuildbtCancelar() {
		return null;
	}

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}




