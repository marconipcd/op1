package com.digital.opuserp.view.modulos.ordemServico.assistenciaTecnica;

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

import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlertaPendenciaDAO;
import com.digital.opuserp.dao.AlteracoesOsiDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.OsiDAO;
import com.digital.opuserp.domain.AlteracoesAssistencia;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ConfigOsi;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Osi;
import com.digital.opuserp.domain.ServicosItensOsi;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.ScpUtil;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.ordemServico.assistenciaTecnica.AssistenciaTecnicaEditor.OsiEvent;
import com.digital.opuserp.view.modulos.ordemServico.assistenciaTecnica.ConcluirOsiEditor.FecharOsiEvent;
import com.digital.opuserp.view.modulos.ordemServico.assistenciaTecnica.EntregarOsiEditor.EntregarOsiEvent;
import com.digital.opuserp.view.modulos.ordemServico.assistenciaTecnica.InformarOcorrenciaEditor.OcorreciaOsiEvent;
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
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.StreamResource;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class AssistenciaTecnicaView extends VerticalLayout{

	private static final long serialVersionUID = 1L;

	JPAContainer<Osi> container;
	
	Table tb;
	TextField tfBusca;
	
	Button btNovo;
	Button btEncaminhar;
	Button btDiagnostico;
	Button btAutorizacao;
	Button btOcorrencia;
	Button btFechar;
	Button btEntregar;	
	Button btExcluir;
	Button btImprimir;
	Button btVisualizar;
	Button btExecutar;
	
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	private ComboBox cbStatus;

	GerenciarModuloDAO gmDAO;
	
	public AssistenciaTecnicaView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtNovo());
			//hlButons.addComponent(BuildbtEncaminhar());
			//hlButons.addComponent(BuildbtFechar());

			hlButons.addComponent(BuildbtExecutar());
			hlButons.addComponent(BuildbtOcorrencia());
			hlButons.addComponent(BuildbtEntregar());
			hlButons.addComponent(BuildbtVisualizar());
			hlButons.addComponent(BuildbtImprimir());
			hlButons.addComponent(BuildbtExcluir());
			
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
			hlFloat.addComponent(buildLbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro ");
			lbLegend.setWidth("250px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
		}
	}
	
	public void refreshRegistros(){
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
		addFilter(tfBusca.getValue());
		//btFechar.setEnabled(false);
		btExcluir.setEnabled(false);
		
		refreshRegistros();
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof AssistenciaTecnicaView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			//btFechar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			//if(slEditar != null){
				//btFechar.removeShortcutListener(slEditar);			
			//}
		}
	}
	
	public Label buildLbRegistros(){
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
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {
								
								
			public void handleAction(Object sender, Object target) {
				btFechar.click();
			}
		};
		return slEditar;
	}
	public JPAContainer<Osi> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Osi.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(cbStatus.getValue().toString().equals("PENDENTE")){
			container.addContainerFilter(Filters.not(Filters.eq("status", "FECHADO")));
			container.addContainerFilter(Filters.not(Filters.eq("status", "ENTREGUE")));
		}else if(cbStatus.getValue().toString().equals("CONCLUIDO")){
			container.addContainerFilter(Filters.eq("status", "FECHADO"));
		}else if(cbStatus.getValue().toString().equals("ENTREGUE")){
			container.addContainerFilter(Filters.eq("status", "ENTREGUE"));
		}
		
		container.addNestedContainerProperty("cliente.nome_razao");
		
		container.sort(new Object[]{"data_agendamento"}, new boolean[]{true});
	
		return container;
	}

	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
			
				if (v instanceof Date) {
				         Date dateValue = (Date) v;
				         return new SimpleDateFormat("dd/MM/yyyy").format(dateValue);
				}
								
				if(colId.equals("status")){
					if(rowId != null && colId != null && tb != null && tb.getItem(rowId).getItemProperty(colId).getValue() != null && tb.getItem(rowId).getItemProperty(colId).getValue().equals("FECHADO")){
						return "CONCLUIDO";
					}else{
						return super.formatPropertyValue(rowId, colId, property);
					}
				}
				
				if(colId.equals("valor") && tb.getItem(rowId).getItemProperty(colId).getValue() != null){					
					return "R$ "+tb.getItem(rowId).getItemProperty(colId).getValue().toString();
				}		
				
				if(colId.equals("id")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}
				
				if(colId.equals("entregar")){
					
					if(tb.getItem(rowId).getItemProperty("entregar").getValue() != null){
						
						if(tb.getItem(rowId).getItemProperty("entregar").getValue().equals("NAO")){
							return "--";
						}else{
							return "SIM";
						}
					}					
				}
					
				
				
				return super.formatPropertyValue(rowId, colId, property);
			}
		};		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		
		//tb.setColumnCollapsed("id", true);				
		tb.setColumnHeader("id", "OS");
		tb.setColumnHeader("data_entrada", "Entrada");
		tb.setColumnHeader("data_agendamento", "Previsão");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("contato", "Contato");
		tb.setColumnHeader("equipamento", "Equipamento");
		tb.setColumnHeader("acessorios", "Acessórios");
		tb.setColumnHeader("observacao", "Observação");
		//DIAS DESDE ENTRADA
		tb.setColumnHeader("operador", "Recebido");
		tb.setColumnHeader("tecnico", "Tecnico");
		tb.setColumnHeader("problema", "Problema");
		tb.setColumnHeader("valor", "Total");
		tb.setColumnHeader("conclusao", "Laudo");
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("entregar", "Entregar");
		
		
		
		//tb.setConverter("id", null);	
		tb.setColumnCollapsed("data_entrada", true);
		tb.setColumnCollapsed("operador", true);
		tb.setColumnCollapsed("problema", true);
		tb.setColumnCollapsed("conclusao", true);
		tb.setColumnCollapsed("acessorios", true);
		tb.setColumnCollapsed("observacao", true);

		tb.setColumnAlignment("valor", Align.RIGHT);
		
		tb.addGeneratedColumn("Dias", new Table.ColumnGenerator() {
			
			
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				String dias = "";				
				
				if(itemId != null && container != null){
					try{
						Osi ac = container.getItem(itemId).getEntity();
						if(ac != null && ac.getData_entrada() != null){		
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							
							DateTime dt1 = new DateTime(sdf.parse(sdf.format(ac.getData_entrada())));
							DateTime dt2 = new DateTime(sdf.parse(sdf.format(new Date())));
							
							Integer days = Days.daysBetween(dt1, dt2).getDays();
							
							dias = days.toString();
						}else{
							dias = "0";
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
	
				return dias;
			}
		});
		
		tb.setVisibleColumns(new Object[] {"id","data_entrada","data_agendamento","cliente.nome_razao","contato","equipamento","acessorios",
				"observacao","Dias","status","tecnico","valor","operador","problema","conclusao","entregar"});
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					
					
					btExcluir.setEnabled(true);
					btImprimir.setEnabled(true);
					//btEncaminhar.setEnabled(false);
					//btFechar.setEnabled(false);
					btEntregar.setEnabled(false);
					btVisualizar.setEnabled(true);
					btOcorrencia.setEnabled(false);	
					btExecutar.setEnabled(true);
					
					if(tb.getItem(tb.getValue()).getItemProperty("status").getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status").getValue().equals("ABERTO")){						
						//btDiagnostico.setEnabled(true); 
					}					
					if(tb.getItem(tb.getValue()).getItemProperty("status").getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status").getValue().equals("ENCAMINHADO")){						
						btOcorrencia.setEnabled(true);					
					}
					if(tb.getItem(tb.getValue()).getItemProperty("status").getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status").getValue().equals("AUTORIZACAO")){						
						btOcorrencia.setEnabled(true);					
					}
					if(tb.getItem(tb.getValue()).getItemProperty("status").getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status").getValue().equals("DIAGNOSTICO")){						
						btOcorrencia.setEnabled(true);						
					}
				
					if(tb.getItem(tb.getValue()).getItemProperty("status").getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status").getValue().equals("FECHADO")){						
						btEntregar.setEnabled(true);
						btExecutar.setEnabled(false);
					}				
						
				}else{

					////btFechar.setEnabled(false);	
					btExcluir.setEnabled(false);
					btImprimir.setEnabled(false);					
					//btEncaminhar.setEnabled(false);
					btEntregar.setEnabled(false);
					btVisualizar.setEnabled(false);
					btExecutar.setEnabled(false);
					
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
			
		
		
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			
			public String getStyle(Table source, Object itemId, Object propertyId) {

				 if (propertyId == null){
	                    return "row-header-default"; // Will not actually be visible
				 }else{
	               
	                Item item = source.getItem(itemId);
	                
	                if(item != null && item.getItemProperty("status") != null && item.getItemProperty("status").getValue() != null){
		                String status = item.getItemProperty("status").getValue().toString();
		               
		                if(status.equals("FECHADO")){
		                    return "row-header-osi-fechado";
		                }else if(status.equals("ENCAMINHADO")){
		                	return "row-header-osi-encaminhado";    
		                }else if(status.equals("ENTREGUE")){
		                	return "row-header-osi-entregue";    
		                }else{
		                
		                	try{
			                	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				                Date data_ex = (Date)source.getItem(itemId).getItemProperty("data_agendamento").getValue();
			                	
			                	
			                	if(!sdf.parse(sdf.format(data_ex)).after(sdf.parse(sdf.format(new Date()))) || sdf.parse(sdf.format(data_ex)).equals(sdf.parse(sdf.format(new Date())))){
			                		return "row-header-atrasado";
			                	}else{		                		
			                		return "row-header-default";
			                	}
		                	}catch(Exception e){
		                		e.printStackTrace();
		                		return "row-header-default";
		                	}
		                }
		                
		               // return "row-header-default";
		                
	                
	                }else{
	                	 return "row-header-default";
	                }
				 }
			}
		});
				
		return tb;
	}
	public ComboBox buildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		
		cbStatus.addItem("PENDENTE");
		cbStatus.addItem("CONCLUIDO");
		cbStatus.addItem("ENTREGUE");
		
		cbStatus.select("PENDENTE");
		cbStatus.setImmediate(true);
		
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());				
			}
		});
		return cbStatus;
	}

	
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setInputPrompt("Buscar...");		
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}

	
	public void addFilter(String s) {
		
		container.removeAllContainerFilters();
		container.setApplyFiltersImmediately(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		
		if(cbStatus.getValue().toString().equals("PENDENTE")){
			container.addContainerFilter(Filters.not(Filters.eq("status", "FECHADO")));
			container.addContainerFilter(Filters.not(Filters.eq("status", "ENTREGUE")));
		}else if(cbStatus.getValue().toString().equals("CONCLUIDO")){
			container.addContainerFilter(Filters.eq("status", "FECHADO"));
		}else if(cbStatus.getValue().toString().equals("ENTREGUE")){
			container.addContainerFilter(Filters.eq("status", "ENTREGUE"));
		}
		
		
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
			for(Object c:collums){		 			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
			
		} catch (Exception e) {
			
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date date = sdf.parse(s);
				
				for(Object c:collums){
					
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
						filtros.add(Filters.eq(c.toString(), date));
					}	
					
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
						
				}
			}catch(Exception e2){
				
				for(Object c:collums){
					
					if(!c.equals("Dias") && !c.equals("vlr_carencia") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
					
				}
			}

		}

		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		
		container.sort(new Object[]{"data_agendamento"}, new boolean[]{true});

		
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
		
	}
	
	
	public Button BuildbtVisualizar() {
		btVisualizar = new Button("Visualizar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar"))				
				{
				
					final AssistenciaTecnicaEditor assisTecEditor = new AssistenciaTecnicaEditor(tb.getItem(tb.getValue()), "Visualizar", true);
					
					assisTecEditor.addCloseListener(new Window.CloseListener() {
						
						
						public void windowClose(CloseEvent e) {
							tb.focus();						
						}
					});
					
					getUI().addWindow(assisTecEditor);	
				}else{				
					Notify.Show("Você não Possui Permissão para Visualizar OSI",Notify.TYPE_ERROR);				
				}
			}
				
		});

		btVisualizar.setEnabled(false);
		return btVisualizar;
	}
	
	
	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
			
					final BeanItem<Osi> item = new BeanItem<Osi>(new Osi());
					final AssistenciaTecnicaEditor assisTecEditor = new AssistenciaTecnicaEditor(item, "Abrir OS", true);
					assisTecEditor.addListerner(new AssistenciaTecnicaEditor.OsiListerner() {
						
						
						public void onClose(OsiEvent event) {
							if(event.isConfirm()){
								Osi osi = item.getBean();
								osi.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								osi.setStatus("ABERTO");
								osi.setOperador(OpusERP4UI.getUsuarioLogadoUI().getUsername());
								osi.setData_entrada(new Date());
								
								Cliente cliente = (Cliente)item.getItemProperty("cliente").getValue();								
								ClienteDAO.saveCliente(cliente);
								
								EnderecoDAO.save((Endereco)item.getItemProperty("end").getValue());
								
//								container.addEntity(osi);
//								container.commit();
								
								OsiDAO.add(osi);
								
								AlteracoesOsiDAO.add(new AlteracoesAssistencia(null, "ABERTA", osi, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
								
								refresh();
								
								Notify.Show("OS Aberta com Sucesso!",Notify.TYPE_SUCCESS);
								assisTecEditor.close();
							}
						}
					});
					
					assisTecEditor.addCloseListener(new Window.CloseListener() {
						
						
						public void windowClose(CloseEvent e) {
							tb.focus();						
						}
					});
					
					getUI().addWindow(assisTecEditor);	
				}else{				
					Notify.Show("Você não Possui Permissão para Cadastrar OSI",Notify.TYPE_ERROR);				
				}
			}
				
		});

		return btNovo;
	}
	
	public Button BuildbtExecutar() {
		btExecutar= new Button("Executar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				 if (winSubMenuNovo != null && winSubMenuNovo.getUI() != null)
					 winSubMenuNovo.close();            	             	 
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
		});
		btExecutar.setEnabled(false); 
		return btExecutar;
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
       
        l.addComponent(BuildbtDiagnostico());
        l.addComponent(BuildbtAguardandoAprovacao());
        l.addComponent(BuildbtEncaminhar());
        l.addComponent(BuildbtFechar());
        
        
    }
	
	public Button BuildbtOcorrencia(){
		btOcorrencia = new Button("Ocorrência", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Ocorrencia"))				
				{
				
					final InformarOcorrenciaEditor informarOcorrenciaEditor = new InformarOcorrenciaEditor(tb.getItem(tb.getValue()), "Ocorrência", true,(Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
					informarOcorrenciaEditor.addListerner(new InformarOcorrenciaEditor.OcorrenciaOsiListerner() {
						
						
						public void onClose(OcorreciaOsiEvent event) {
							if(event.isConfirm()){
								AlteracoesOsiDAO.add(new AlteracoesAssistencia(null, event.getOcorrencia(), new Osi((Integer)event.getItem().getItemProperty("id").getValue()), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
								informarOcorrenciaEditor.close();
								
								Notify.Show("Ocorrência Informada com Sucesso!", Notify.TYPE_SUCCESS);
							}
						}
					});
					
					getUI().addWindow(informarOcorrenciaEditor);
				
				}else{
					Notify.Show("Você não Possui Permissão para Cadastrar Ocorrência",Notify.TYPE_ERROR);
				}
			}
		});
		
		btOcorrencia.setEnabled(false);		
		return btOcorrencia;
	}


	public Button BuildbtAguardandoAprovacao() {
		btAutorizacao = new Button("Aguardar Autorização", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Autorizacao"))				
				{
					if(tb.getValue() != null){
						GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Você deseja realmente mudar o status desta OS para Aguardando Autorização?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									Item item = tb.getItem(tb.getValue());
									Integer id_tabela = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
									
									item.getItemProperty("tecnico").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
									item.getItemProperty("status").setValue("AUTORIZACAO");		
									
									tb.commit();								
									container.commit();
									
									AlteracoesOsiDAO.add(new AlteracoesAssistencia(null, "ESPERA POR AUTORIZAÇÃO DE CLIENTE", new Osi((Integer) item.getItemProperty("id").getValue()), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									AlertaPendenciaDAO.removePendencia(codSubModulo, id_tabela);
									
									Notify.Show("Os colocada em Espera por autorização com Sucesso!",Notify.TYPE_SUCCESS);
								}
							}
						});
						
						gd.addCloseListener(new Window.CloseListener() {
							
							
							public void windowClose(CloseEvent e) {
								tb.focus();						
							}
						});
						
						getUI().addWindow(gd);
					}
				}else{								
					Notify.Show("Você não Possui Permissão para colocar uma os em espera de Autorização",Notify.TYPE_ERROR);
				}	
			}
		});
		//btAutorizacao.setEnabled(false);
		btAutorizacao.setPrimaryStyleName("btSubMenu");
		
		
		if(tb.getItem(tb.getValue()).getItemProperty("status").getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status").getValue().equals("DIAGNOSTICO")){
			btAutorizacao.setEnabled(true);		
		}else{
			btAutorizacao.setEnabled(false);
		}
					
		return btAutorizacao;
	}
	
	public Button BuildbtDiagnostico() {
		btDiagnostico = new Button("Fazer Diagnostico", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Diagnostico"))				
				{
					if(tb.getValue() != null){
						GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Você deseja realmente mudar o status desta OS para Diganostico?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									Item item = tb.getItem(tb.getValue());
									Integer id_tabela = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
									
									item.getItemProperty("tecnico").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
									item.getItemProperty("status").setValue("DIAGNOSTICO");		
									
									tb.commit();								
									container.commit();
									
									AlteracoesOsiDAO.add(new AlteracoesAssistencia(null, "DIAGNOSTICO", new Osi((Integer) item.getItemProperty("id").getValue()), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									AlertaPendenciaDAO.removePendencia(codSubModulo, id_tabela);
									
									Notify.Show("Os colocada em Diganostico com Sucesso!",Notify.TYPE_SUCCESS);
								}
							}
						});
						
						gd.addCloseListener(new Window.CloseListener() {
							
							
							public void windowClose(CloseEvent e) {
								tb.focus();						
							}
						});
						
						getUI().addWindow(gd);
					}
				}else{								
					Notify.Show("Você não Possui Permissão para mudar Status para DIGANOSTICO",Notify.TYPE_ERROR);
				}	
			}
		});
		
		if(tb.getItem(tb.getValue()).getItemProperty("status").getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status").getValue().equals("ABERTO")){
			btDiagnostico.setEnabled(true);		
		}else{
			btDiagnostico.setEnabled(false);
		}
		
		btDiagnostico.setPrimaryStyleName("btSubMenu");
					
		return btDiagnostico;
	}
	
	public Button BuildbtEncaminhar() {
		btEncaminhar = new Button("Encaminhar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Encaminhar"))				
				{
					if(tb.getValue() != null){
						GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Encaminhar a OS Selecionada ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									Item item = tb.getItem(tb.getValue());
									Integer id_tabela = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
									
									item.getItemProperty("data_encaminhamento").setValue(new Date());
									item.getItemProperty("tecnico").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
									item.getItemProperty("status").setValue("ENCAMINHADO");		
									
									tb.commit();								
									container.commit();
									
									AlteracoesOsiDAO.add(new AlteracoesAssistencia(null, "ENCAMINHADA", new Osi((Integer) item.getItemProperty("id").getValue()), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									AlertaPendenciaDAO.removePendencia(codSubModulo, id_tabela);
									
									Notify.Show("OS Encaminhada com Sucesso!",Notify.TYPE_SUCCESS);
								}
							}
						});
						
						gd.addCloseListener(new Window.CloseListener() {
							
							
							public void windowClose(CloseEvent e) {
								tb.focus();						
							}
						});
						
						getUI().addWindow(gd);
					}
				}else{								
					Notify.Show("Você não Possui Permissão para Encaminhar OSI",Notify.TYPE_ERROR);
				}	
			}
		});
		//btEncaminhar.setEnabled(false);
		btEncaminhar.setPrimaryStyleName("btSubMenu");
		
		if(tb.getItem(tb.getValue()).getItemProperty("status").getValue() != null && !tb.getItem(tb.getValue()).getItemProperty("status").getValue().equals("ENCAMINHADO")){
			btEncaminhar.setEnabled(true);		
		}else{
			btEncaminhar.setEnabled(false);
		}
					
		return btEncaminhar;
	}
	
	public Button BuildbtEntregar(){
		btEntregar = new Button("Entregar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Entregar"))				
				{	
					if(tb.getValue() != null && ContasReceberDAO.allowCloseOsi(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString())){
						
						GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Entregar a OS Selecionada ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									final EntregarOsiEditor entregarEditor = new EntregarOsiEditor(tb.getItem(tb.getValue()), "Fechar OS", true);
									entregarEditor.addListerner(new EntregarOsiEditor.EntregarOsiListerner() {
										
										
										public void onClose(EntregarOsiEvent event) {
											if(event.isConfirm()){
																						
																																																												
												try {				
													
													
													event.getItem().getItemProperty("status").setValue("ENTREGUE");
													event.getItem().getItemProperty("data_entrega").setValue(new Date());
													tb.commit();
													container.commit();
													refreshRegistros();
													
													
													AlteracoesOsiDAO.add(new AlteracoesAssistencia(null, "ENTREGUE", new Osi((Integer) event.getItem().getItemProperty("id").getValue()), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
													
													Notify.Show("OS Entregue com Sucesso!", Notify.TYPE_SUCCESS);
													entregarEditor.close();
													
												} catch (Exception e) {														
													e.printStackTrace();
													Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
												}
												
											}else{
												container.discard();
											}
										}
									});
									
									entregarEditor.addCloseListener(new Window.CloseListener() {
										
										
										public void windowClose(CloseEvent e) {
											tb.focus();						
										}
									});
									
									getUI().addWindow(entregarEditor);								
									
								}else{
									tb.focus();
								}
							}
						});
						
						getUI().addWindow(gd);
					}else{
						Notify.Show("Não é Possivel Entregar com o Boleto em Aberto!", Notify.TYPE_WARNING);
					}
				}else{				
					
					Notify.Show("Você não Possui Permissão para Entregar OSI",Notify.TYPE_ERROR);

				}	
			}
		});
		btEntregar.setEnabled(false);
		return btEntregar;
	}
	
	public Button BuildbtImprimir(){
		btImprimir = new Button("Imprimir", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Imprimir"))				
				{	
					
					if (winSubMenuVisualizar != null && winSubMenuVisualizar.getUI() != null)
						winSubMenuVisualizar.close();
					else {
						
												
						buildSubMenuVisualizar(event);
						
						getUI().addWindow(winSubMenuVisualizar);
						winSubMenuVisualizar.focus();
						((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
							
							
							public void layoutClick(LayoutClickEvent event) {
								winSubMenuVisualizar.close();
								((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
							}
						});
					}		
					
					
				}else{							
					Notify.Show("Você não Possui Permissão para Imprimir OSI",Notify.TYPE_ERROR);
				}	
			}
		});
		btImprimir.setEnabled(false);
		return btImprimir;
	}
	Window winSubMenuVisualizar;
	private void buildSubMenuVisualizar(ClickEvent event) {
		winSubMenuVisualizar = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuVisualizar.setContent(l);
        winSubMenuVisualizar.setWidth("300px");
        winSubMenuVisualizar.addStyleName("notifications");
        winSubMenuVisualizar.setClosable(false);
        winSubMenuVisualizar.setResizable(false);
        winSubMenuVisualizar.setDraggable(false);
        winSubMenuVisualizar.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuVisualizar.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuVisualizar.setCloseShortcut(KeyCode.ESCAPE, null);

        Button bt1= new Button("Protocolo de Entrada",new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
//					try{						
//					
//						Osi osi = OsiDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
//						InputStream stream = new ByteArrayInputStream(OsiDAO.getLayoutOsi(osi).getBytes());  
//						
//						String urlPrint = "";
//						if(!OpusERP4UI.getEmpresa().getId().equals(3)){
//							urlPrint = "http://192.168.20.145:631/printers/BEMA_PRODUCAO";
//						}else{
//							urlPrint = "http://192.168.20.145:631/printers/BEMA_GRAFICA";
//						}
//						
//						CupsPrinter printer = new CupsPrinter(new URL(urlPrint), "BEMA_PRODUCAO", true);						
//						HashMap<String, String> attributes = new HashMap<String, String>();	    	   
//								        	    
//						PrintJob printJob = new PrintJob.Builder(stream)
//						.jobName("OpusERP4")
//						.userName(OpusERP4UI.getUsuarioLogadoUI().getUsername())
//						.copies(1)  	        	    							
//						.build();
//						
//						PrintRequestResult printRequestResult = printer.print(printJob);		
//						
//						if(printRequestResult.isSuccessfulResult()){		        	    	
//							Notify.Show("Osi esta Sendo Impressa", Notify.TYPE_SUCCESS);
//						}else{		        	    	
//							Notify.Show("Houve um Problema ao Tentar Imprimir, Espere um Momento e Tente Novamente", Notify.TYPE_ERROR);
//						}
//						
//						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Imprimiu uma OSI"));
//					
//					}catch(Exception e){
//						e.printStackTrace();						
//					}
				
				
				if(OpusERP4UI.getEmpresa().getId().equals(1) || OpusERP4UI.getEmpresa().getId().equals(3)){
					try{
						
						Osi c = OsiDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());
						ConfigOsi confOsi = OsiDAO.getConfigOsi( OpusERP4UI.getEmpresa().getId());
						
						File f = new File("/DJMONITOR/PROTOCOLO"+c.getId().toString()+".txt");
									 //File("/DJMONITOR/"+pedido.getId().toString()+".djp");
						BufferedWriter br = new BufferedWriter(new FileWriter(f));  
						 					
						if(f.canWrite()){
						
							StringBuilder s = new StringBuilder();
							String quebra = System.getProperty("line.separator");
								
							s.append("                          d i g i t a l");
							s.append(quebra);
							s.append("      Rua Adjar Maciel, 35 Centro Belo Jardim/PE CEP: 55.150-040");
							s.append(quebra);					
							s.append("            CNPJ: 07.578.965/0001-05 IE: 18.3.050.0329");														
							s.append(quebra);							
							s.append(quebra);							
							s.append("                        ORDEM DE SERVICO");
							s.append(quebra);
							
							SimpleDateFormat formHora= new SimpleDateFormat("HH:mm:ss");
							String hora = formHora.format(new Date());
							
							SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
							String hoje = form.format(new Date());
							
							
							s.append("----------------------------------------------------------------"+quebra);	
							s.append("OS : "+c.getId().toString()+" EMISSAO: "+hoje+" HORA: "+hora);									
							s.append(quebra);
							s.append("                          INFORMACOES");
							s.append(quebra);
							s.append("----------------------------------------------------------------"+quebra);				
							s.append("Cliente..: "+c.getCliente().getNome_razao());			
							s.append(quebra);	
							s.append("PRODUTO..: "+c.getEquipamento());
							s.append(quebra);
							
							if(c.getAcessorios() != null){
								s.append("ACESSORIOS..: "+c.getAcessorios());
							}else{
								s.append("ACESSORIOS..: ");
							}
							s.append(quebra);
							s.append("OBS..: "+c.getObservacao());
							s.append(quebra);
							s.append("Recebido Por.: "+c.getOperador());
							s.append(quebra);
							s.append(quebra);							
							s.append("Atencao:");
							s.append(quebra);
							s.append("1. Os equipamentos reparados terao garantia de 90 (noventa) dias   ");
							s.append(quebra);
							s.append("sobre os servicos executados a contar a partir da Conclusao da");
							s.append(quebra);
							s.append("Ordem de Servico, nao incluindo mau uso e alteracoes");
							s.append(quebra);
							s.append("nas caracteristicas do equipamento;");
							s.append(quebra);
							s.append("2. Nao estao cobertos pela garantia, defeitos provenientes");
							s.append(quebra);
							s.append("de dano eletrico, problemas gerados por softwares ou erros de operacao;");
							s.append(quebra);
							s.append("3. O equipamento so sera entregue mediante a apresentacao deste documento;");
							s.append(quebra);
							s.append("4. Nao nos responsabilizamos por programas instalados em seu equipamento.");
							s.append(quebra);
							s.append("5. A nao retirada do equipamento no prazo de 90 (noventa) dias ");
							s.append(quebra);
							s.append("implicara na venda do equipamento para custear despesas do ");
							s.append(quebra);
							s.append("servico e armazenamento.");
							s.append(quebra);
							s.append("A DIGITAL se isenta de toda e qualquer responsabilidade dos");
							s.append(quebra);
							s.append("programas instalados em seu micro, portanto aconselhamos que");
							s.append(quebra);
							s.append("o cliente mantenha seu backup atualizado.");
							s.append(quebra);
							s.append("* Sera cobrado taxa de Laudo Tecnico caso o orcamento");
							s.append(quebra);
							s.append("nao seja aceito valor de R$ "+confOsi.getValor_laudo());
							s.append(quebra);
							s.append(quebra);
							s.append(quebra);
							s.append("      _____________________________________________________"+quebra);							               
							s.append("                          Assinatura");
							s.append(quebra);
							
							
							
							br.write(s.toString());  
							br.close();							
							//f.delete();
														
							if(OpusERP4UI.getEmpresa().getId().equals(1)){
								ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@18", "/DJMONITOR/PROTOCOLO"+c.getId().toString()+".txt", "/dados/PRINT/DIGITAL/PROTOCOLO"+c.getId().toString()+".txt");
							}else if(OpusERP4UI.getEmpresa().getId().equals(3)){
								ScpUtil.transferir("root", "192.168.21.13", "managerdigi_SB@18", "/DJMONITOR/PROTOCOLO"+c.getId().toString()+".txt", "/dados/PRINT/DIGITALGRAFICA/PROTOCOLO"+c.getId().toString()+".txt");
							}
						}else{
							Notify.Show("Não é Possivel Gravar o Arquivo", Notify.TYPE_ERROR);
						}
					}catch(Exception e){
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getMessage(), Notify.TYPE_ERROR);
						Notify.Show("ERRO: "+e.getCause(), Notify.TYPE_ERROR);
					}
				
				}

			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        
        Button bt2 = new Button("Ordem de Serviço",new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				try {
					Integer cod  = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
					// INSTANCIA UMA NOVA JANELA E ADICIONA SUAS PROPRIEDADES
					Window win = new Window("Laudo Técnico");
					win.setWidth("800px");
					win.setHeight("600px");
					win.setResizable(false);
					win.center();
					win.setModal(true);
					win.setResizable(true);					
					win.setStyleName("disable_scroolbar");
					
					StreamResource resource;
					resource = new StreamResource(new ExportLaudo(cod), "Laudo.pdf");
					resource.getStream();
					resource.setMIMEType("application/pdf");
					resource.setCacheTime(0);
					
					Embedded e = new Embedded();
					e.setSizeFull();
					e.setType(Embedded.TYPE_BROWSER);
					e.setSource(resource);
					
					win.setContent(e);
					getUI().addWindow(win);
					
					
					win.addCloseListener(new Window.CloseListener() {
						
						
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
			
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}
		});
        bt2.setPrimaryStyleName("btSubMenu");
        
      
        
        l.addComponent(bt1);
        l.addComponent(bt2);

       
    }
	public Button BuildbtFechar(){
		btFechar = new Button("Concluir", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {

				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Concluir"))				
				{
					if(tb.getValue() != null){
						
									
									final ConcluirOsiEditor fecharEditor = new ConcluirOsiEditor(tb.getItem(tb.getValue()), "Concluir OS", true);
									fecharEditor.addListerner(new ConcluirOsiEditor.FecharOsiListerner() {
										
										
										public void onClose(FecharOsiEvent event) {
											if(event.isConfirm()){
												
												Integer id_tabela = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
												Integer codCliente = ((Cliente)event.getItem().getItemProperty("cliente").getValue()).getId();
												
												SimpleDateFormat sdf = new SimpleDateFormat("yy");
												String anoDoc = String.valueOf(sdf.format(new Date()));
												String valor = event.getItem().getItemProperty("valor").getValue().toString();
												
												try {
													
													if(!valor.equals("0,00")){
														ConnUtil.getEntity().getTransaction().begin();
														ContasReceber cr = ContasReceberDAO.gerarBoletos(new ContasReceber(new Cliente(codCliente), "OS"+event.getItem().getItemProperty("id").getValue().toString(), valor, new Date(), new Date(),"ABERTO", "BOLETO","ASSISTENCIA"));
														ConnUtil.getEntity().getTransaction().commit();
													}
													
													container.commit();
											
													for (ServicosItensOsi s : event.getItens()) {
														OsiDAO.addServico(s);													
													}
													
													AlteracoesOsiDAO.add(new AlteracoesAssistencia(null, "CONCLUIDA", new Osi((Integer) event.getItem().getItemProperty("id").getValue()), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
													AlertaPendenciaDAO.removePendencia(codSubModulo, id_tabela);
											
													Notify.Show("OS Fechada com Sucesso!", Notify.TYPE_SUCCESS);
													fecharEditor.close();
												}catch(Exception e){
													e.printStackTrace();
													Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
												}
											}else{
												container.discard();
											}
										}
									});
									
									fecharEditor.addCloseListener(new Window.CloseListener() {
										
										
										public void windowClose(CloseEvent e) {
											tb.focus();						
										}
									});
									
									getUI().addWindow(fecharEditor);								
									
								}else{
									tb.focus();
								}
							
						
					
				
				}else{								
					Notify.Show("Você não Possui Permissão para Fechar OSI",Notify.TYPE_ERROR);
				}	
			}
		});
		
		//btFechar.setEnabled(false);
		btFechar.setPrimaryStyleName("btSubMenu");
		
		return btFechar;
	}

	
	public Button BuildbtExcluir() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
					if(tb.getValue() != null){
						GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a OS Selecionada ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){

									Integer id_tabela = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
									container.removeItem(tb.getValue());
									container.commit();
									
									AlertaPendenciaDAO.removePendencia(codSubModulo, id_tabela);
									refreshRegistros();
									
									Notify.Show("OS Excluída com Sucesso!", Notify.TYPE_SUCCESS);
								}
							}
						});
						
						gd.addCloseListener(new Window.CloseListener() {
							
							
							public void windowClose(CloseEvent e) {
								tb.focus();						
							}
						});
						
						getUI().addWindow(gd);
					}
				}else{				
					
					Notify.Show("Você não Possui Permissão para Excluir OSI", Notify.TYPE_ERROR);
				}	
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

