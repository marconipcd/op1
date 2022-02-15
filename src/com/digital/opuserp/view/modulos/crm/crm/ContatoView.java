package com.digital.opuserp.view.modulos.crm.crm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlertaPendenciaDAO;
import com.digital.opuserp.dao.AlteracoesCrmDAO;
import com.digital.opuserp.dao.AlteracoesOseDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.CrmDAO;
import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.dao.SetoresDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesCrm;
import com.digital.opuserp.domain.AlteracoesOse;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.CrmFormasContato;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.domain.SetoresUsuario;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.crm.crm.ContatoEditor.NovoContatoEvent;
import com.digital.opuserp.view.modulos.crm.crm.CrmEditor.EditarContatoEvent;
import com.digital.opuserp.view.modulos.crm.historico.HistoricoAlteracoesCrm;
import com.digital.opuserp.view.modulos.crm.historico.HistoricoView;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.RoteirizacaoEditor;
import com.digital.opuserp.view.modulos.ordemServico.roteirizacao.RoteirizacaoEditor.RoteirizacaoEvent;
import com.digital.opuserp.view.util.Notify;
import com.github.wolfie.refresher.Refresher;
//import com.github.wolfie.refresher.Refresher;
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
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class ContatoView extends VerticalLayout{

	JPAContainer<Crm> container;
	
	Item item;
	
	Table tb;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btPreTratar;
	Button btTratar;
	Button btExcluir;
	Button btHistorico;
	Button btLog;
	Button btAtualizar;
	Button btAnalisar;
	Button btVisualizar;
	String status;
	ComboBox cbSetor;
	ComboBox cbAssunto;
	ComboBox cbStatus;
	Button btEncaminharOutroNivel;
	Button btAbrirOs;
	
	Refresher refresher;
	
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	private boolean janelaAtiva = false;
	
	public ContatoView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			final HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtNovo());
			hlButons.addComponent(BuildbtEditar());
			//hlButons.addComponent(BuildbtPreTratamento());
			hlButons.addComponent(BuildbtTratamento());
			hlButons.addComponent(BuildBtOcorrencia());
			hlButons.addComponent(buildBtAbrirOs());
			hlButons.addComponent(buildBtEncaminharOutroNivel());
			hlButons.addComponent(BuildbtVisualizar());
			hlButons.addComponent(BuildbtAnalisar());
			hlButons.addComponent(BuildbtExcluir());
			hlButons.addComponent(BuildbtHistorico());
			hlButons.addComponent(BuildbtLog());
			
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			
			
			addComponent(new HorizontalLayout(){
				 {
					 setWidth("100%");				 
					 addComponent(BuildbtAtualizar());
					 setComponentAlignment(btAtualizar, Alignment.TOP_LEFT);
					 addComponent(hlButons);
					 setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
					 setExpandRatio(hlButons, 1);
				 }
			 });

			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					
					addComponent(buildCbStatus());
					addComponent(buildCbSetor());
					addComponent(buildCbAssuntos());
					addComponent(buildDfPrevisao());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(lbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Tratar");
			lbLegend.setWidth("250px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setExpandRatio(lbRegistros, 1f);
			
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);

			addExtension(buildRefresher());	
			checkIteracao();
		}
	}


	public Refresher buildRefresher(){
		
		
		refresher = new Refresher();
		refresher.setRefreshInterval(300000);
		refresher.addListener(new Refresher.RefreshListener() {
			
			@Override
			public void refresh(Refresher source) {
								
				checkIteracao();
				
				if(!janelaAtiva){
//					atualizarColumns = true;
					
					replaceComponent(tb, buildTbGeneric());
					hlFloat.replaceComponent(lbRegistros, lbRegistros());
					setExpandRatio(tb, 1);		
					btTratar.setEnabled(false);
					btVisualizar.setEnabled(false);
					
					if(tfBusca.getValue() != null || cbSetor.getValue() != null || cbStatus.getValue() != null){
						addFilter(tfBusca.getValue());
					}
					
					
				}	
				
			}
		});
		
		return refresher;
	}
	
	private void checkIteracao(){
		
		for(Object o:tb.getItemIds()){
			if(o != null && tb != null && tb.getItem(o) != null && tb.getItem(o).getItemProperty("status") != null && 
					tb.getItem(o).getItemProperty("status").getValue() != null && tb.getItem(o).getItemProperty("status").getValue().equals("EM TRATAMENTO")){
				CrmDAO.checkIteracao((Integer)o);
			}
		}
	}
	
	private Component BuildbtAtualizar() {
		btAtualizar = new Button("Atualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
					checkIteracao();
					
					replaceComponent(tb, buildTbGeneric());
					hlFloat.replaceComponent(lbRegistros, lbRegistros());
					setExpandRatio(tb, 1);		
					btTratar.setEnabled(false);
					btExcluir.setEnabled(false);
					btHistorico.setEnabled(false);
					btLog.setEnabled(false);
					btAnalisar.setEnabled(false);
					btVisualizar.setEnabled(false);
					
					if(tfBusca.getValue() != null){
						addFilter(tfBusca.getValue());
					}	
					
			}
		});
			
		return btAtualizar;
	}
	
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
		setExpandRatio(tb, 1);		
		btTratar.setEnabled(false);
		btVisualizar.setEnabled(false);
		btExcluir.setEnabled(false);
		btHistorico.setEnabled(false);
		btLog.setEnabled(false);
		btAnalisar.setEnabled(false);
		
		if(tfBusca.getValue() != null || cbSetor.getValue() != null || cbStatus.getValue() != null){
			addFilter(tfBusca.getValue());
		}
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ContatoView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btTratar.addShortcutListener(buildShortCutTratar());
			
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btTratar != null || slEditar != null){				
				btTratar.removeShortcutListener(slEditar);
			}

		}
	}
	
	public Label lbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados   <br/>   <span style='font-size: 0.9em;'> <span style='color: #72D600;'>VERDE</span>: Em tratamento | <span style='color: #ED2121;'>VERMELHO</span>: Data com prazo esgotado  <span style='background: #fedbdb;color: #ED2121; border: 0.001em solid #e69595;'>VERMELHO TARJA</span>: Data e hora com prazo esgotado | <span style='background: #fedbdb; color: #ED2121; border: 0.001em solid #e69595;'><strong>NEGRITO</strong></span>: Alta prioridade</span>", ContentMode.HTML);
		return lbRegistros;
	}
	
	public ShortcutListener buildShortCutTratar(){
		slEditar = new ShortcutListener("Tratar",ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btTratar.click();
			}
		};
		return slEditar;
	}
	
	public JPAContainer<Crm> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Crm.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));	
		
		if(cbStatus.getValue() != null){
			
			if(!cbStatus.getValue().toString().equals("TODOS")){				
				if(cbStatus.getValue().toString().equals("PENDENTES")){					
					container.addContainerFilter(Filters.not(Filters.eq("status", "EFETUADO")));			
					//container.addContainerFilter(Filters.not(Filters.eq("status", "EM ANALISE")));			
				}else if(cbStatus.getValue().toString().equals("EFETUADOS")){
						container.addContainerFilter(Filters.eq("status", "EFETUADO"));
				}else if(cbStatus.getValue().toString().equals("EM ANALISE")){
					container.addContainerFilter(Filters.eq("status", "EM ANALISE"));
				}
			}
		}
		
		if(cbSetor.getValue() != null){
			container.addContainerFilter(Filters.eq("setor", SetoresDAO.find((Integer) cbSetor.getItem(cbSetor.getValue()).getItemProperty("id").getValue())));
		}
		
		
		List<Filter> filters = new ArrayList<>();
		List<SetoresUsuario> setores_usuario = SetoresDAO.getSetoresByUser();
		
		if(setores_usuario.size() == 0){
			filters.add(Filters.eq("setor", new Setores(0)));
		}
		
		for (SetoresUsuario setor : setores_usuario) {
			
			Setores setor1 = SetoresDAO.find(setor.getSetor_id());
			
			if(!filters.contains(setor1)){
				filters.add(Filters.eq("setor", setor1));
			}
			
			List<Setores> setores_filhos = SetoresDAO.getSetoresChild(setor1);
			if(setores_filhos.size() > 0){			
				for (Setores setores : setores_filhos) {
					if(!filters.contains(setores)){
						filters.add(Filters.eq("setor", setores));
					}
					
					List<Setores> setores_netos = SetoresDAO.getSetoresChild(setores);		
					
					if(setores_netos.size() >0){
						
						for (Setores setores2 : setores_netos) {
							if(!filters.contains(setores2)){
								filters.add(Filters.eq("setor", setores2));
							}
							
							List<Setores> setores_bisnetos = SetoresDAO.getSetoresChild(setores2);	
							
							if(setores_bisnetos.size() > 0){
								for (Setores setores3 : setores_bisnetos) {
									if(!filters.contains(setores3)){
										filters.add(Filters.eq("setor", setores3));
									}
									
									List<Setores> setores_tataranetos = SetoresDAO.getSetoresChild(setores3);
									
									if(setores_tataranetos.size() > 0){
										for (Setores setores4 : setores_tataranetos) {
											if(!filters.contains(setores4)){
												filters.add(Filters.eq("setor", setores4));
											}
											
											//
										}
									}
								}
							}
						}
					}
				}				
			}
		}
		
		if(filters.size() > 0){
			container.addContainerFilter(Filters.or(filters));
		}
		
		
		container.addNestedContainerProperty("setor.nome");
		container.addNestedContainerProperty("cliente.id");
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("cliente.contato");
		container.addNestedContainerProperty("crm_assuntos.nome");
		container.addNestedContainerProperty("crm_assuntos.prioridade");
		container.addNestedContainerProperty("contrato.id");
		container.addNestedContainerProperty("contrato.base.identificacao");
		container.addNestedContainerProperty("crm_formas_contato.nome");
		
		container.sort(new String[] {"data_agendado"}, new boolean[]{true});

	
		return container;
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
//	
//	public JPAContainer<Crm> buildContainer(){
//		container = JPAContainerFactory.makeBatchable(Crm.class, ConnUtil.getEntity());
//		container.setAutoCommit(false);
//		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));	
//		
////		if(!cbStatus.getValue().toString().equals("EFETUADOS")){
////			if(OpusERP4UI.getUsuarioLogadoUI().getVisualizar_todos_crm() != null && 
////					OpusERP4UI.getUsuarioLogadoUI().getVisualizar_todos_crm() == 0){
////				container.addContainerFilter(Filters.eq("operador", OpusERP4UI.getUsuarioLogadoUI().getUsername()));
////			}
////		}
//		
//		if(cbStatus.getValue() != null){
//			
//			if(!cbStatus.getValue().toString().equals("TODOS")){				
//				if(cbStatus.getValue().toString().equals("PENDENTES")){					
//					container.addContainerFilter(Filters.not(Filters.eq("status", "EFETUADO")));			
//					//container.addContainerFilter(Filters.not(Filters.eq("status", "EM ANALISE")));			
//				}else if(cbStatus.getValue().toString().equals("EFETUADOS")){
//						container.addContainerFilter(Filters.eq("status", "EFETUADO"));
//				}else if(cbStatus.getValue().toString().equals("EM ANALISE")){
//					container.addContainerFilter(Filters.eq("status", "EM ANALISE"));
//				}
//			}
//		}
//		
//		if(cbSetor.getValue() != null){
//			container.addContainerFilter(Filters.eq("setor", SetoresDAO.find((Integer) cbSetor.getItem(cbSetor.getValue()).getItemProperty("id").getValue())));
//		}
//		
//		
//		List<Filter> filters = new ArrayList<>();
//		List<SetoresUsuario> setores_usuario = SetoresDAO.getSetoresByUser();
//		
//		if(setores_usuario.size() == 0){
//			filters.add(Filters.eq("setor", new Setores(0)));
//		}
//		
//		for (SetoresUsuario setor : setores_usuario) {
//				
//				Setores setor1 = SetoresDAO.find(setor.getSetor_id());
//				
//				if(!filters.contains(setor1)){
//					filters.add(Filters.eq("setor", setor1));
//				}
//				
//				List<Setores> setores_filhos = SetoresDAO.getSetoresChild(setor1);
//				if(setores_filhos.size() > 0){			
//					for (Setores setores : setores_filhos) {
//						if(!filters.contains(setores)){
//							filters.add(Filters.eq("setor", setores));
//						}
//						
//						List<Setores> setores_netos = SetoresDAO.getSetoresChild(setores);		
//						
//						if(setores_netos.size() >0){
//							
//							for (Setores setores2 : setores_netos) {
//								if(!filters.contains(setores2)){
//									filters.add(Filters.eq("setor", setores2));
//								}
//								
//								List<Setores> setores_bisnetos = SetoresDAO.getSetoresChild(setores2);	
//								
//								if(setores_bisnetos.size() > 0){
//									for (Setores setores3 : setores_bisnetos) {
//										if(!filters.contains(setores3)){
//											filters.add(Filters.eq("setor", setores3));
//										}
//										
//										List<Setores> setores_tataranetos = SetoresDAO.getSetoresChild(setores3);
//										
//										if(setores_tataranetos.size() > 0){
//											for (Setores setores4 : setores_tataranetos) {
//												if(!filters.contains(setores4)){
//													filters.add(Filters.eq("setor", setores4));
//												}
//												
//												//
//											}
//										}
//									}
//								}
//							}
//						}
//					}				
//				}
//			}
//	
//		
//		if(filters.size() > 0){
//			container.addContainerFilter(Filters.or(filters));
//		}
//		
//		
//		container.addNestedContainerProperty("cliente.id");
//		container.addNestedContainerProperty("cliente.nome_razao");
//		container.addNestedContainerProperty("cliente.contato");
//
//		
//		
//		//if(cbSetor != null && cbSetor.getValue() != null){
//			container.addNestedContainerProperty("setor.nome");
//		//}
//		
//		//if(cbAssunto != null && cbAssunto.getValue() != null){
//			container.addNestedContainerProperty("crm_assuntos.nome");
//			container.addNestedContainerProperty("crm_assuntos.prioridade");
//		//}
//		
//			
//		container.addNestedContainerProperty("contrato.id");
//		container.addNestedContainerProperty("contrato.base.identificacao");
//		container.addNestedContainerProperty("crm_formas_contato.nome");
//		
//		container.sort(new String[] {"data_agendado"}, new boolean[]{true});
//
//	
//		return container;
//	}

	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			
				
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,Property<?> property) {
				
				
				if(colId.equals("status")){
					if(rowId != null && colId != null && tb != null && tb.getItem(rowId).getItemProperty(colId).getValue() != null && tb.getItem(rowId).getItemProperty(colId).getValue().equals("AGENDADO")){
						return "PENDENTE";
					}else{
						return super.formatPropertyValue(rowId, colId, property);
					}
				}
				
				if(colId.equals("setor.nome")){
					if(tb.getItem(rowId).getItemProperty(colId).getValue().toString().equals("SEM SETOR")){
						return "";
					}else{
						return super.formatPropertyValue(rowId, colId, property);
					}
				}
				
				if(colId.equals("crm_assuntos.nome")){
					if(tb.getItem(rowId).getItemProperty(colId).getValue().toString().equals("NAO DEFINIDO")){
						return "";
					}else{
						return super.formatPropertyValue(rowId, colId, property);
					}
				}
				
				
				if(colId.equals("id")){					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}
				
				if(colId.equals("contrato.id")){
					if(tb.getItem(rowId).getItemProperty("contrato.id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("contrato.id").getValue()).toString();				
					}	
				}
				
				
				if(tb.getType(colId).equals(Date.class)){	
					if(colId.equals("data_agendado")){
						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");						
							return sdf.format(tb.getItem(rowId).getItemProperty(colId).getValue());
						}else{
							return super.formatPropertyValue(rowId, colId, property);	
						}
						
					}else{
						
						
						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
							SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");						
							return sdf.format(tb.getItem(rowId).getItemProperty(colId).getValue());
						}else{
							return super.formatPropertyValue(rowId, colId, property);	
						}
						
						
					}
				}else{
					return super.formatPropertyValue(rowId, colId, property);
				}
			}
				
			
		};	
		
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {
                
                if (propertyId == null)
                    return "row-header-default"; // Will not actually be visible
               
                
                Item item = source.getItem(itemId); 
                if(item != null && item.getItemProperty("data_agendado").getValue() != null){
	                
                	Date dataPrevista = (Date)item.getItemProperty("data_agendado").getValue();                		
	                
	                if(item.getItemProperty("status").getValue() != null){
		                status = item.getItemProperty("status").getValue().toString();
		                
		                if(status.equals("AGENDADO") && !dataPrevista.after(new Date())){
		                	
		                	
			                	if(item.getItemProperty("hora_agendado").getValue() != null){
			                		try{
			                			
			                			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				                		
				                		DateTime dt1 = new DateTime(sdf.parse(item.getItemProperty("data_agendado").getValue().toString()+" "+item.getItemProperty("hora_agendado").getValue().toString()));
				                		DateTime dt2 = new DateTime();			                		
				                	
				                		if(dt1.isBeforeNow()){
				                			return "row-header-crm-atrasado-hora"; 
				                		}else{	
				                			if(item.getItemProperty("crm_assuntos.prioridade").getValue() != null && item.getItemProperty("crm_assuntos.prioridade").getValue().equals("ALTA")){
					                			return "row-header-crm-alta-prioridade";
					                		}else{
					                			return "row-header-crm-atrasado";
					                		}
				                		}
				                		
			                		}catch(Exception e){
			                			e.printStackTrace();
			                			if(item.getItemProperty("crm_assuntos.prioridade").getValue() != null && item.getItemProperty("crm_assuntos.prioridade").getValue().equals("ALTA")){
				                			return "row-header-crm-alta-prioridade";
				                		}else{
				                			return "row-header-crm-atrasado";
				                		}
			                		}
			                	}else{		 
			                		if(item.getItemProperty("crm_assuntos.prioridade").getValue() != null && item.getItemProperty("crm_assuntos.prioridade").getValue().equals("ALTA")){
			                			return "row-header-crm-alta-prioridade";
			                		}else{
			                			return "row-header-crm-atrasado";
			                		}
			                	}
		                	
		                	
		                }else  if(status.equals("EM ANALISE") && !dataPrevista.after(new Date())){
		                    return "row-header-crm-em-analise"; 
		                }else if(status.equals("AGENDADO") && dataPrevista.after(new Date())){		                	
		                	
			                	if(item.getItemProperty("crm_assuntos.prioridade").getValue() != null && item.getItemProperty("crm_assuntos.prioridade").getValue().equals("ALTA")){
		                			return "row-header-crm-alta-prioridade";
		                		}else{
		                			return "row-header-default";
		                		}    	
		                	
		                }else if(status.equals("EFETUADO")){
		                	return "row-header-default";  
		                }else if(status.equals("EM TRATAMENTO") || status.equals("PRE-TRATAMENTO")){
		                	return "row-header-crm-em-tratamento";  
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
		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		//tb.setConverter("id", null);				
		tb.setMultiSelect(true); 
	
//		if(cbSetor != null && cbSetor.getValue() != null ){
//			tb.setVisibleColumns(new Object[] {"id","setor.nome","operador","nivel","data_agendado","hora_agendado","qtd","contrato.id","cliente.nome_razao","contrato.base.identificacao","contato",
//					"conteudo","status","operador_tratamento","data_inicio_tratamento","tempo_total_tratamento","crm_formas_contato.nome"});
//		}else if(cbAssunto != null && cbAssunto.getValue() != null){
//			tb.setVisibleColumns(new Object[] {"id","operador","nivel","data_agendado","hora_agendado","qtd","contrato.id","cliente.nome_razao","contrato.base.identificacao","contato",
//					"crm_assuntos.nome","crm_assuntos.prioridade","conteudo","status","operador_tratamento","data_inicio_tratamento","tempo_total_tratamento","crm_formas_contato.nome"});
//		}else if(cbSetor != null && cbSetor.getValue() != null && cbAssunto != null && cbAssunto.getValue() != null){
			tb.setVisibleColumns(new Object[] {"id","setor.nome","operador","nivel","data_agendado","hora_agendado","qtd","contrato.id","cliente.nome_razao","contrato.base.identificacao","contato",
					"crm_assuntos.nome","crm_assuntos.prioridade","conteudo","status","operador_tratamento","data_inicio_tratamento","tempo_total_tratamento","crm_formas_contato.nome"});
//		}else{
//			tb.setVisibleColumns(new Object[] {"id","operador","nivel","data_agendado","hora_agendado","qtd","contrato.id","cliente.nome_razao","contrato.base.identificacao","contato",
//					"conteudo","status","operador_tratamento","data_inicio_tratamento","tempo_total_tratamento","crm_formas_contato.nome"});
//		}
		
		
		tb.addGeneratedColumn("qtd", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				if(source != null && itemId != null && 
					source.getItem(itemId).getItemProperty("setor") != null && 
					source.getItem(itemId).getItemProperty("cliente") != null &&
					source.getItem(itemId).getItemProperty("contrato") != null &&
					source.getItem(itemId).getItemProperty("setor").getValue() != null && 
					source.getItem(itemId).getItemProperty("cliente").getValue() != null && 
					source.getItem(itemId).getItemProperty("contrato").getValue() != null){
					
					Setores setor = (Setores)source.getItem(itemId).getItemProperty("setor").getValue();
					Cliente cliente = (Cliente)source.getItem(itemId).getItemProperty("cliente").getValue();
					AcessoCliente contrato = (AcessoCliente)source.getItem(itemId).getItemProperty("contrato").getValue();
					//Ose ose = (Ose)source.getItem(itemId).getItemProperty("ose").getValue();
					
					DateTime dNow = new DateTime(new Date());
					Date dLast12Months = dNow.minusMonths(12).toDate();
					
					EntityManager em = ConnUtil.getEntity();
					Query q = em.createQuery("select c from Crm c where c.contrato=:contrato and c.setor=:setor and c.cliente=:cliente and c.data_agendado > :data", Crm.class);
					q.setParameter("setor", setor);
					q.setParameter("cliente", cliente);
					q.setParameter("contrato", contrato);
					q.setParameter("data", dLast12Months);
					
					StringBuilder s = new StringBuilder();
					
					for (Crm crm: (List<Crm>)q.getResultList()) {					
						s.append("| ");				
					}
					
					return s.toString();
				}else{
					return "";
				}
			}
		});
		
		
		tb.setColumnCollapsed("data_inicio_tratamento", true);
		tb.setColumnCollapsed("tempo_total_tratamento", true);
		
		//tb.setColumnCollapsed("contrato.id", true);
		tb.setColumnCollapsed("contrato.base.identificacao", true);
		//tb.setColumnCollapsed("contrato.id",true)
		tb.setColumnHeader("id", "Protocolo");
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("setor.nome", "Setor");
		tb.setColumnHeader("operador", "Aberto por");
		tb.setColumnHeader("nivel", "Nível");
		tb.setColumnHeader("data_agendado", "Previsão");
		tb.setColumnHeader("hora_agendado", "Hora");
		tb.setColumnHeader("qtd", "Qtd.");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("contato", "Contato");
		tb.setColumnHeader("crm_assuntos.nome", "Assunto");
		tb.setColumnHeader("crm_assuntos.prioridade", "Prioridade");
		
		tb.setColumnHeader("operador_tratamento", "Operador Tratamento");
		tb.setColumnHeader("conteudo", "Observação");
		tb.setColumnHeader("contrato.id", "Contrato*");
		tb.setColumnHeader("contrato.base.identificacao", "Concentrador*");
		tb.setColumnHeader("crm_formas_contato.nome", "Forma Contato");
		
		tb.setColumnHeader("data_inicio_tratamento", "Data Inicio Tratamento");
		tb.setColumnHeader("tempo_total_tratamento", "Tempo Total Tratamento");
		
				
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {

				if(event.getProperty().getValue() != null){
					
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if( selecteds.size() == 1 && tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue() != null && 
							tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("AGENDADO") || 
							tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("PRE-TRATAMENTO") ){
						
						//btPreTratar.setEnabled(true);
						btTratar.setEnabled(true);
						btAnalisar.setEnabled(false);
						
						btAbrirOs.setEnabled(true);
						btEncaminharOutroNivel.setEnabled(true);
					}else{
						//btPreTratar.setEnabled(false);
						btTratar.setEnabled(false);						
					}
					
					if( selecteds.size() == 1 && tb.getItem(selecteds.toArray()[0]).getItemProperty("status") != null && 
						tb.getItem(selecteds.toArray()[0]).getItemProperty("status").getValue().equals("EM ANALISE") ){
						
						btAnalisar.setEnabled(true);
						btAbrirOs.setEnabled(false);
						btEncaminharOutroNivel.setEnabled(false);
					}else{
						btAnalisar.setEnabled(false);
					}					
					

					btOcorrencia.setEnabled(false);
					btHistorico.setEnabled(false);
					btLog.setEnabled(false);
					btVisualizar.setEnabled(false);
					if(selecteds.size() == 1){
						btHistorico.setEnabled(true);
						btLog.setEnabled(true);
						btVisualizar.setEnabled(true);
						btOcorrencia.setEnabled(true);
					}
					
					if(selecteds.size() > 0){
						btExcluir.setEnabled(true);						
					}					
				}else{
					//btPreTratar.setEnabled(false);
					btTratar.setEnabled(false);
					btExcluir.setEnabled(false);
					btHistorico.setEnabled(false);
					btAnalisar.setEnabled(false);
					btLog.setEnabled(false);
					btVisualizar.setEnabled(false);
					btAbrirOs.setEnabled(false);
					btEncaminharOutroNivel.setEnabled(false);
					btOcorrencia.setEnabled(false);
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
		tb.setColumnCollapsible("data_agendado", false);		
		//tb.sort(new Object[]{"data_agendado"}, new boolean[]{true});
		
		return tb;
	}
	
	DateField dfPrevisao;
	private DateField buildDfPrevisao(){
		
		dfPrevisao = new DateField();
		dfPrevisao.setDateFormat("dd/MM/yyyy");
		dfPrevisao.setImmediate(true);
		
		dfPrevisao.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				addFilter(tfBusca.getValue());
			}
		});
		
		return dfPrevisao;
	}
	
	private JPAContainer<Setores> buildContainerSetores(){
		JPAContainer<Setores> container = JPAContainerFactory.make(Setores.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		return container;
	}
	
	private JPAContainer<CrmAssunto> buildContainerAssunto(){
		JPAContainer<CrmAssunto> container = JPAContainerFactory.make(CrmAssunto.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		return container;
	}
	
	
	private ComboBox buildCbAssuntos(){
		cbAssunto = new ComboBox(null, buildContainerAssunto());
		cbAssunto.setImmediate(true);
		cbAssunto.setTextInputAllowed(false);
		cbAssunto.setItemCaptionPropertyId("nome");
		
		cbAssunto.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {		
				
				addFilter(tfBusca.getValue());				
			}
		});
		
		return cbAssunto;
	}
	
	private ComboBox buildCbSetor(){
		cbSetor = new ComboBox(null, buildContainerSetores());
		cbSetor.setImmediate(true);
		cbSetor.setTextInputAllowed(false);
		cbSetor.setItemCaptionPropertyId("nome");
		
		cbSetor.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {			
				
				//replaceComponent(tb, buildTbGeneric());
				addFilter(tfBusca.getValue());				
			}
		});
		
		return cbSetor;
	}
		
	private ComboBox buildCbStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.setTextInputAllowed(false);
		
		cbStatus.addItem("TODOS");			
		cbStatus.addItem("PENDENTES");		
		cbStatus.addItem("EFETUADOS");	
		cbStatus.select("PENDENTES");
		cbStatus.setImmediate(true);
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbStatus.getValue().toString().equals("PENDENTES")){
					dfPrevisao.setValue(null);						
				}
								
				addFilter(tfBusca.getValue());					
			}
		});
		
		return cbStatus;
	}
	
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setInputPrompt("Buscar...");
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
					addFilter(event.getText());			
			}
		});
		return tfBusca;
	}
	
	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.sort(new String[] {"id"}, new boolean[]{false});
		
		
		if(cbAssunto.getValue() != null){
			container.addContainerFilter(Filters.eq("crm_assuntos", cbAssunto.getValue()));
		}
		
		if(cbStatus.getValue() != null){
			
			if(!cbStatus.getValue().toString().equals("TODOS")){
				
				if(cbStatus.getValue().toString().equals("PENDENTES")){					
					container.addContainerFilter(Filters.not(Filters.eq("status", "EFETUADO")));			
					//container.addContainerFilter(Filters.not(Filters.eq("status", "EM ANALISE")));			
				}else if(cbStatus.getValue().toString().equals("EFETUADOS")){
					container.addContainerFilter(Filters.eq("status", "EFETUADO"));
				}
			}
		}
		if(cbSetor.getValue() != null){
			container.addContainerFilter(Filters.eq("setor", SetoresDAO.find((Integer) cbSetor.getItem(cbSetor.getValue()).getItemProperty("id").getValue())));
		}
		
		if(dfPrevisao.getValue() != null){
			container.addContainerFilter(Filters.eq("data_agendado", dfPrevisao.getValue()));
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
		
		
		List<Filter> filters = new ArrayList<>();
		List<SetoresUsuario> setores_usuario = SetoresDAO.getSetoresByUser();
		
		if(setores_usuario.size() == 0){
			filters.add(Filters.eq("setor", new Setores(0)));
		}
		
		for (SetoresUsuario setor : setores_usuario) {
			
			Setores setor1 = SetoresDAO.find(setor.getSetor_id());
			
			if(!filters.contains(setor1)){
				filters.add(Filters.eq("setor", setor1));
			}
			
			List<Setores> setores_filhos = SetoresDAO.getSetoresChild(setor1);
			if(setores_filhos.size() > 0){			
				for (Setores setores : setores_filhos) {
					if(!filters.contains(setores)){
						filters.add(Filters.eq("setor", setores));
					}
					
					List<Setores> setores_netos = SetoresDAO.getSetoresChild(setores);		
					
					if(setores_netos.size() >0){
						
						for (Setores setores2 : setores_netos) {
							if(!filters.contains(setores2)){
								filters.add(Filters.eq("setor", setores2));
							}
							
							List<Setores> setores_bisnetos = SetoresDAO.getSetoresChild(setores2);	
							
							if(setores_bisnetos.size() > 0){
								for (Setores setores3 : setores_bisnetos) {
									if(!filters.contains(setores3)){
										filters.add(Filters.eq("setor", setores3));
									}
									
									List<Setores> setores_tataranetos = SetoresDAO.getSetoresChild(setores3);
									
									if(setores_tataranetos.size() > 0){
										for (Setores setores4 : setores_tataranetos) {
											if(!filters.contains(setores4)){
												filters.add(Filters.eq("setor", setores4));
											}
											
											//
										}
									}
								}
							}
						}
					}
				}				
			}
		}
		
		if(filters.size() > 0){
			container.addContainerFilter(Filters.or(filters));
		}
		
		
		container.addContainerFilter(Filters.or(filtros));		
		container.applyFilters();
		//container.sort(new String[] {"data_agendado","hora_agendado"}, new boolean[]{true,true});
		container.sort(new String[] {"data_agendado"}, new boolean[]{true});
		 
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
		
		checkIteracao();
	}

//	public void addFilter(String s) {
//		container.removeAllContainerFilters();
//		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
//		container.sort(new String[] {"id"}, new boolean[]{false});
//		
//		
//		if(cbStatus.getValue().toString().equals("PENDENTES") || cbStatus.getValue().toString().equals("TODOS")){
//			if(OpusERP4UI.getUsuarioLogadoUI().getVisualizar_todos_crm() != null && OpusERP4UI.getUsuarioLogadoUI().getVisualizar_todos_crm() == 0){
//				container.addContainerFilter(Filters.eq("operador", OpusERP4UI.getUsuarioLogadoUI().getUsername()));
//			}
//		}else if(cbStatus.getValue().toString().equals("EFETUADOS")){
//			if(cbSetor.getValue() == null || tfBusca.getValue() == null || tfBusca.getValue().equals("") || tfBusca.getValue().isEmpty()){
//				if(OpusERP4UI.getUsuarioLogadoUI().getVisualizar_todos_crm() != null && OpusERP4UI.getUsuarioLogadoUI().getVisualizar_todos_crm() == 0){
//					container.addContainerFilter(Filters.eq("operador", OpusERP4UI.getUsuarioLogadoUI().getUsername()));
//				}
//			}
//		}
//		
//		if(cbAssunto.getValue() != null){
//			container.addContainerFilter(Filters.eq("crm_assuntos", cbAssunto.getValue()));
//		}
//		
//		if(cbStatus.getValue() != null){
//			
//			if(!cbStatus.getValue().toString().equals("TODOS")){
//				
//				if(cbStatus.getValue().toString().equals("PENDENTES")){					
//					container.addContainerFilter(Filters.not(Filters.eq("status", "EFETUADO")));			
//					//container.addContainerFilter(Filters.not(Filters.eq("status", "EM ANALISE")));			
//				}else if(cbStatus.getValue().toString().equals("EFETUADOS")){
//					container.addContainerFilter(Filters.eq("status", "EFETUADO"));
//				}
//			}
//		}
//		if(cbSetor.getValue() != null){
//			container.addContainerFilter(Filters.eq("setor", SetoresDAO.find((Integer) cbSetor.getItem(cbSetor.getValue()).getItemProperty("id").getValue())));
//		}
//		
//		if(dfPrevisao.getValue() != null){
//			container.addContainerFilter(Filters.eq("data_agendado", dfPrevisao.getValue()));
//		}
//		
//		Object[] collums = tb.getVisibleColumns();		
//		List<Filter> filtros = new ArrayList<Filter>();		
//		
//		try {
//			Integer cod = Integer.valueOf(s);
//			DateTime date = null ;
//			
//				for(Object c:collums){		 			
//						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
//							filtros.add(new Like(c.toString(), "%"+cod+"%", false));
//						}			
//						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
//							filtros.add(new Like(c.toString(), "%"+s+"%", false));
//						}		
//			    }
//			
//		} catch (Exception e) {
//			
//			
//			for(Object c:collums){		 
//				
//				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
//					filtros.add(new Like(c.toString(), "%"+s+"%", false));
//				}			
//			}
//		}
//		
//		
//		List<Filter> filters = new ArrayList<>();
//		List<SetoresUsuario> setores_usuario = SetoresDAO.getSetoresByUser();
//		
//		if(setores_usuario.size() == 0){
//			filters.add(Filters.eq("setor", new Setores(0)));
//		}
//		
//		for (SetoresUsuario setor : setores_usuario) {
//			
//			Setores setor1 = SetoresDAO.find(setor.getSetor_id());
//			
//			if(!filters.contains(setor1)){
//				filters.add(Filters.eq("setor", setor1));
//			}
//			
//			List<Setores> setores_filhos = SetoresDAO.getSetoresChild(setor1);
//			if(setores_filhos.size() > 0){			
//				for (Setores setores : setores_filhos) {
//					if(!filters.contains(setores)){
//						filters.add(Filters.eq("setor", setores));
//					}
//					
//					List<Setores> setores_netos = SetoresDAO.getSetoresChild(setores);		
//					
//					if(setores_netos.size() >0){
//						
//						for (Setores setores2 : setores_netos) {
//							if(!filters.contains(setores2)){
//								filters.add(Filters.eq("setor", setores2));
//							}
//							
//							List<Setores> setores_bisnetos = SetoresDAO.getSetoresChild(setores2);	
//							
//							if(setores_bisnetos.size() > 0){
//								for (Setores setores3 : setores_bisnetos) {
//									if(!filters.contains(setores3)){
//										filters.add(Filters.eq("setor", setores3));
//									}
//									
//									List<Setores> setores_tataranetos = SetoresDAO.getSetoresChild(setores3);
//									
//									if(setores_tataranetos.size() > 0){
//										for (Setores setores4 : setores_tataranetos) {
//											if(!filters.contains(setores4)){
//												filters.add(Filters.eq("setor", setores4));
//											}
//											
//											//
//										}
//									}
//								}
//							}
//						}
//					}
//				}				
//			}
//		}
//	
//		
//		if(filters.size() > 0){
//			container.addContainerFilter(Filters.or(filters));
//		}
//		
//		
//		container.addContainerFilter(Filters.or(filtros));		
//		container.applyFilters();
//		//container.sort(new String[] {"data_agendado","hora_agendado"}, new boolean[]{true,true});
//		container.sort(new String[] {"data_agendado"}, new boolean[]{true});
//		
//		hlFloat.replaceComponent(lbRegistros, lbRegistros());
//		
//		checkIteracao();
//	}
	
	public Button buildBtEncaminharOutroNivel() {
		btEncaminharOutroNivel = new Button("Encaminhar Nível", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				final Set<Object> selecteds = (Set<Object>)tb.getValue();
				
				if(selecteds.size() == 1){
						
						GenericDialog gd = new GenericDialog("Confirme para Continuar !", "Deseja encaminhar este CRM para outro Nível ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								EntityManager em = ConnUtil.getEntity();
								Crm crm = em.find(Crm.class, tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue());
								
								if(!crm.getNivel().equals("NIVEL III")){
									if(crm.getNivel().equals("NIVEL I")){
										crm.setNivel("NIVEL II");
									}else if(crm.getNivel().equals("NIVEL II")){
										crm.setNivel("NIVEL III");
									}
									
									em.getTransaction().begin();
									em.merge(crm);
									em.getTransaction().commit();
											
									btAtualizar.click();
								}else{
									Notify.Show("O Crm já esta no nível máximo !", Notify.TYPE_NOTICE);
								}
								
							}
						});
						
						getUI().addWindow(gd); 
				
				}
			}
		});		
		
		btEncaminharOutroNivel.setEnabled(false); 
		return btEncaminharOutroNivel;
	}
	
	public Button buildBtAbrirOs() {
		btAbrirOs = new Button("Abrir OS", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
						
				GenericDialog gd = new GenericDialog("Confirme para Continuar !", "Deseja abrir uma OS para este CRM ?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						
						final Set<Object> selecteds = (Set<Object>)tb.getValue();
						
						if(selecteds.size() == 1){
						
							EntityItem<Crm> entityItem = (EntityItem<Crm>)tb.getItem(selecteds.toArray()[0]); 
							Crm crmSelecionado = entityItem.getEntity();
							
							final BeanItem<Ose> item = new BeanItem<Ose>(new Ose());
							
							final RoteirizacaoEditor roteirizacaoEditor = new RoteirizacaoEditor(item, "Abrir Novo Chamado",crmSelecionado.getCliente(),crmSelecionado.getConteudo(),true);
							roteirizacaoEditor.addListerner(new RoteirizacaoEditor.RoteirizacaoListerner() {
								
								
								public void onClose(RoteirizacaoEvent event) {
									if(event.isConfirm()){
										
										try{
											
											Ose ose = item.getBean();									
											ose.setValor(Real.formatDbToString(String.valueOf(ose.getTipo_subgrupo().getValor())));
											OseDAO.save(ose);
											
											
											//Atualiza Cliente
											Cliente cliente = (Cliente)item.getItemProperty("cliente").getValue();								
											ClienteDAO.saveCliente(cliente);
											
											//Atualiza Endereço
											EnderecoDAO.save((Endereco)item.getItemProperty("end").getValue());									
											
											String valor = Real.formatDbToString(String.valueOf(ose.getTipo_subgrupo().getValor()));
											
											
											if(!valor.equals("0,00") && !valor.equals("null") && cliente != null && ose != null 
													&& ose.getData_ex() != null){
												ConnUtil.getEntity().getTransaction().begin();
												SimpleDateFormat sdf = new  SimpleDateFormat("yy");
												Date dataAtual = new Date();
												
												ContasReceber c;
												if(ose.getContrato() != null && ose.getContrato().getId() != null){
													c = ContasReceberDAO.gerarBoletos(new ContasReceber(cliente,ose.getContrato().getId()+"-"+"OS"+ose.getId().toString(),valor, new Date(), ose.getVencimento(), "ABERTO", "BOLETO","SERVICO"));
												}else{
													c = ContasReceberDAO.gerarBoletos(new ContasReceber(cliente,"OS"+ose.getId().toString(),valor, new Date(), ose.getVencimento(), "ABERTO", "BOLETO","SERVICO"));
												}
												ConnUtil.getEntity().getTransaction().commit();;
												
												
												if(c != null){
													SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM/yyyy");
													Notification.show("Título Gerado", "Código: "+c.getId().toString()+"\n"+
																					   "Nº Documento: "+c.getN_doc()+"\n"+
															                           "Vencimento: "+sdfData.format(c.getData_vencimento()),Type.ERROR_MESSAGE);
												}
											}
											
											AlteracoesOseDAO.add(new AlteracoesOse(null, "ABERTA", new Ose((Integer)event.getItem().getItemProperty("id").getValue()), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
											
											roteirizacaoEditor.close();
											Notify.Show("Os Aberta com Sucesso!", Notify.TYPE_SUCCESS);
											
											
										}catch(Exception e){
											e.printStackTrace();
											Notify.Show(e.getMessage(), Notify.TYPE_ERROR);
										}
									}
								}
							});
							
							
							
							getUI().addWindow(roteirizacaoEditor);	
						}
					}
				});
				
				getUI().addWindow(gd); 
			}
		});		
		
		btAbrirOs.setEnabled(false); 
		return btAbrirOs;
	}

	public Button BuildbtEditar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Novo"))				
				{
				
					final Set<Object> selecteds = (Set<Object>)tb.getValue();
					
					if(selecteds.size() == 1){
						
						 
						CrmEditor contatoEditor = new CrmEditor(tb.getItem(selecteds.toArray()[0]), "Editar crm", true);
						contatoEditor.addListerner(new CrmEditor.EditarContatoListerner() {
							
							@Override
							public void onClose(EditarContatoEvent event) {
								if(event.isConfirm()){
									
									try{
													
										container.commit();
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou Um Novo CRM "));
										//AlteracoesCrmDAO.save(new AlteracoesCrm(null, "EDIÇÃO DE CRM", crm, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										
										btAtualizar.click();
										
									}catch(Exception e){
										e.printStackTrace();
									}
								}
							}
						});
						
						getUI().addWindow(contatoEditor);
						janelaAtiva = true;
						
						contatoEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								janelaAtiva = false;							
							}
						});
				
					}
					
				}else{
					Notify.Show("Você não tem Permissão para Criar um Novo CRM", Notify.TYPE_ERROR);
				}
				
			}
		});

		
		

		return btEditar;
	}
	
	
	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Novo"))				
				{
				
					final BeanItem<Crm> newItem = new BeanItem<Crm>(new Crm());
					ContatoEditor contatoEditor = new ContatoEditor(newItem, "Novo Contato", true,codSubModulo);
					contatoEditor.addListerner(new ContatoEditor.NovoContatoListerner() {
						
						@Override
						public void onClose(NovoContatoEvent event) {
							if(event.isConfirm()){
								
								try{
									Crm crm = newItem.getBean();
									crm.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
									crm.setNivel("NIVEL I");
									
									if(crm.getContrato() != null){
										if(crm.getContrato().getPlano() != null && crm.getContrato().getPlano().getNome() != null){
											crm.setPlano(crm.getContrato().getPlano().getNome());
										}else{
											crm.setPlano("SEM PLANO");
										}
										
										if(crm.getContrato().getBase() != null && crm.getContrato().getBase().getIdentificacao() != null){	
											crm.setConcentrador(crm.getContrato().getBase().getIdentificacao());
										}else{
											crm.setConcentrador("SEM CONCENTRADOR");
										}
									}
									
																											
									CrmDAO.insertCRM(crm);	
									
									AlteracoesCrmDAO.save(new AlteracoesCrm(null,
											crm.getConteudo(), crm, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									
									
									
									refresh();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou Um Novo CRM | COD: "+crm.getId().toString()));
									AlteracoesCrmDAO.save(new AlteracoesCrm(null, "CADASTRO DE CRM", crm, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}
					});
					
					getUI().addWindow(contatoEditor);
					janelaAtiva = true;
					
					contatoEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							janelaAtiva = false;							
						}
					});
				
				}else{
					Notify.Show("Você não tem Permissão para Criar um Novo CRM", Notify.TYPE_ERROR);
				}
				
			}
		});

		
		ShortcutListener slbtOK = new ShortcutListener("Novo", ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btNovo.click();
			}
		};
		
		btNovo.addShortcutListener(slbtOK);

		return btNovo;
	}
	
	
	
	
	public Button BuildbtAnalisar() {
		btAnalisar = new Button("Analisar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tb.getValue()!=null){
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Analisar"))				
					{
						final Set<Object> selecteds = (Set<Object>)tb.getValue();
						
						if(selecteds.size() == 1){
							
								item = tb.getItem(selecteds.toArray()[0]);
								final Crm crm = CrmDAO.find((Integer) item.getItemProperty("id").getValue());
		
								ContatoEditor contatoEditor = new ContatoEditor(tb.getItem(selecteds.toArray()[0]), "Analisar Crm", true,codSubModulo);
								contatoEditor.addListerner(new ContatoEditor.NovoContatoListerner() {
									
									@Override
									public void onClose(NovoContatoEvent event) {
										if(event.isConfirm()){
											
											try{
												container.commit();
												tb.focus();
												refresh();
												
												LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "ANALISOU Um Novo CRM | COD: "+crm.getId().toString()));
												AlteracoesCrmDAO.save(new AlteracoesCrm(null, "ANALISE DE CRM", crm, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
											}catch(Exception e){
												e.printStackTrace();
											}
										}
									}
								});
								
								getUI().addWindow(contatoEditor);
								janelaAtiva = true;
								
								contatoEditor.addCloseListener(new Window.CloseListener() {
									
									@Override
									public void windowClose(CloseEvent e) {
										janelaAtiva = false;							
									}
								});
						}else{
							Notify.Show("Selecione somente um CRM por vez!", Notify.TYPE_WARNING);
						}
					}else{
						Notify.Show("Você não tem Permissão para Criar um Novo CRM", Notify.TYPE_ERROR);
					}
					
				}
				
			}
		});
		
		
//		ShortcutListener slbtOK = new ShortcutListener("Analisar", ShortcutAction.KeyCode.F2,null) {
//			
//			@Override
//			public void handleAction(Object sender, Object target) {
//				btNovo.click();
//			}
//		};
		
//		btAnalisar.addShortcutListener(slbtOK);
		btAnalisar.setEnabled(false);
		return btAnalisar;
	}

	
	

	public Button BuildbtExcluir(){
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
					if(tb.getValue() != null){
						
						
						GenericDialog gd = new GenericDialog("Confirme para Continuar !", "Deseja Realmente Excluir este CRM", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									
									final Set<Object> selecteds = (Set<Object>)tb.getValue();
									
									for (Object object : selecteds) {
										try{	
											
											item = tb.getItem(object);
											EntityItem<Crm> entityItem = (EntityItem<Crm>)item;
											Crm crm = entityItem.getEntity(); 
											
											container.removeItem(object);
											container.commit();
											
											AlertaPendenciaDAO.removePendencia(codSubModulo, crm.getId()); 											
											
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um CRM | COD: "+crm.getId().toString()));
											AlteracoesCrmDAO.save(new AlteracoesCrm(null, "EXCLUSÃO DE CRM", crm, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
											Notify.Show("Ordem de Serviço Excluída com Sucesso!", Notify.TYPE_SUCCESS);
													
										}catch(Exception e){
											e.printStackTrace();
										}
									
									}
									
									
								}
							}
						});
						
						getUI().addWindow(gd);
						
					}
				}else{
					Notify.Show("Você não tem Permissão para Excluir CRM", Notify.TYPE_ERROR);
				}
			}
		});
		
		
		btExcluir.setEnabled(false);
		return btExcluir;
	}
	
	public Button BuildbtPreTratamento() {
		btPreTratar = new Button("Pre-Tratar", new Button.ClickListener() {
			boolean gravar = false;
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "PreTratar"))				
				{
					if(tb.getValue() != null){
						
						final Set<Object> selecteds = (Set<Object>)tb.getValue();
						
						if(selecteds.size() == 1){
						
							final Integer codCrm = (Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue();
							
							boolean check = CrmDAO.preTratar(codCrm);
							if(check){								
								refresh();
								Notify.Show("CRM em PreTratamento", Notify.TYPE_SUCCESS);		
							}
								
						}
						
					}else{
						Notify.Show("Selecione somente um CRM por vez!", Notify.TYPE_WARNING);
					}
				}else{
					Notify.Show("Você não tem Permissão para PreTratar CRM", Notify.TYPE_ERROR);
				}
			}
		});
		
		//btPreTratar.setEnabled(false);
		return btPreTratar;
	}
	
	Button btOcorrencia;
	public Button BuildBtOcorrencia(){
		
		btOcorrencia = new Button("Ocorrência", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Ocorrencias"))				
				{
					
				
					if(tb.getValue() != null){
						final Set<Object> selecteds = (Set<Object>)tb.getValue();
						
						if(selecteds.size() == 1){
							final OcorrenciasEditor ocorrenciaEditor = new OcorrenciasEditor(tb.getItem(selecteds.toArray()[0]), "Ocorrências CRM", true, (Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue());
																					
							getUI().addWindow(ocorrenciaEditor); 
						}
					}
				}else{
					Notify.Show("Você não tem Permissão para Visualizar/Cadastrar Ocorrências!", Notify.TYPE_ERROR);
				}
			}
		});
		
		
		btOcorrencia.setEnabled(false); 
		return btOcorrencia;
	}
	
	//Backup Tratar
	public Button BuildbtTratamento() {
		btTratar = new Button("Tratar", new Button.ClickListener() {
			boolean gravar = false;
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar"))				
				{
					if(tb.getValue() != null){
						
						final Set<Object> selecteds = (Set<Object>)tb.getValue();
						
						if(selecteds.size() == 1){
						
						final Integer codCrm = (Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue();
						
						ContatoEditor contatoEditor = new ContatoEditor(tb.getItem(selecteds.toArray()[0]), "Tratar", true,codSubModulo);
						contatoEditor.addListerner(new ContatoEditor.NovoContatoListerner() {
							
							@Override
							public void onClose(NovoContatoEvent event) {
								if(event.isConfirm()){
									try{
										
										gravar = true;
										
										Crm crm = CrmDAO.find(codCrm);
										crm.setStatus("EFETUADO");
										crm.setConteudo(event.getItem().getItemProperty("conteudo").getValue().toString());
										crm.setContato_feedback((CrmFormasContato) event.getItem().getItemProperty("contato_feedback").getValue());
										crm.setData_efetuado(new Date());
										crm.setOperador_tratamento(OpusERP4UI.getUsuarioLogadoUI().getUsername());
										
										DateTime dt1 = new DateTime(crm.getData_inicio_tratamento());
										DateTime dt2 = new DateTime(crm.getData_efetuado());
										
										Period p = new Period(dt1, dt2);
										SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
										
										
										Date time = sdf.parse(p.getHours()+":"+p.getMinutes()+":"+p.getSeconds());
										crm.setTempo_total_tratamento(time);
										
										
										SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
										DateTime dt11 = new DateTime(crm.getData_cadastro());
										DateTime dt22 = new DateTime(crm.getData_efetuado());
										
										Integer dias = Days.daysBetween(dt11, dt22).getDays();
										if(dias > 1){
											crm.setConcluido_no_prazo(false);
										}else{
											crm.setConcluido_no_prazo(true);
										}
										
										CrmDAO.updateCRM(crm);
																			
										refresh();
										
										AlertaPendenciaDAO.removePendencia(codSubModulo, codCrm); 
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Tratou um CRM | COD: "+crm.getId().toString()));
										AlteracoesCrmDAO.save(new AlteracoesCrm(null, "TRATAMENTO DE CRM", crm, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
									
									
									
									}catch(Exception e){
										e.printStackTrace();
									}
								}else{
									Crm crm = CrmDAO.find(codCrm);
									crm.setOperador_tratamento("");
									CrmDAO.updateCRM(crm);
								}
							}
						});
						
						
						Crm crm = CrmDAO.find(codCrm);
						crm.setStatus("EM TRATAMENTO");						
						crm.setOperador_tratamento(OpusERP4UI.getUsuarioLogadoUI().getUsername());
						crm.setData_inicio_tratamento(new Date());
						CrmDAO.updateCRM(crm);
															
						refresh();
						
												
						getUI().addWindow(contatoEditor);
						janelaAtiva = true;
						
						contatoEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								janelaAtiva = false;							
								refresh();
							}
						});
						
					}
						
					}else{
						Notify.Show("Selecione somente um CRM por vez!", Notify.TYPE_WARNING);
					}
				}else{
					Notify.Show("Você não tem Permissão para Visualizar CRM", Notify.TYPE_ERROR);
				}
			}
		});
		
		btTratar.setEnabled(false);
		return btTratar;
	}
	
	
//	public Button BuildbtTratamento() {
//		btTratar = new Button("Tratar", new Button.ClickListener() {
//			boolean gravar = false;
//			@Override
//			public void buttonClick(ClickEvent event) {
//				
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar"))				
//				{
//						final Set<Object> selecteds = (Set<Object>)tb.getValue();
//						
//						if(selecteds.size() == 1){
//							final EntityItem<Crm> ei_crm = (EntityItem<Crm>)tb.getItem(selecteds.toArray()[0]);
//						    TratarCrmEditor tratarCrm = new TratarCrmEditor(tb.getItem(selecteds.toArray()[0]), "Tratar", true);
//						    tratarCrm.addListerner(new TratarCrmEditor.TratarCrmEditorListerner() {
//								
//								@Override
//								public void onClose(TratarCrmEditorEvent event) {
//									
//									if(event.isConfirm()){
//										String ocorrencia = event.getOcorrencia();
//										
//										Crm crm  = ei_crm.getEntity();
//										crm.setStatus("EM TRATAMENTO");
//										
//										CrmDAO.saveCrm(crm);
//										
//										boolean check = AlteracoesCrmDAO.save(new AlteracoesCrm(null,ocorrencia, crm, 
//												OpusERP4UI.getUsuarioLogadoUI(), new Date()));
//																			
//									}
//								}
//							});
//						    
//						    getUI().addWindow(tratarCrm);
//						}
//				}else{
//					
//				}
//			}
//		});
//		
//		btTratar.setEnabled(false);
//		return btTratar;
//	}
	
	public Button BuildbtVisualizar() {
		btVisualizar = new Button("Visualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar"))				
//				{
					if(tb.getValue() != null){
						
						final Set<Object> selecteds = (Set<Object>)tb.getValue();
						
						if(selecteds.size() == 1){
						
						final Integer codCrm = (Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue();
						
						ContatoEditor contatoEditor = new ContatoEditor(tb.getItem(selecteds.toArray()[0]), "Visualizar", true,codSubModulo);
						contatoEditor.addListerner(new ContatoEditor.NovoContatoListerner() {
							
							@Override
							public void onClose(NovoContatoEvent event) {
								if(event.isConfirm()){
									try{
							
									}catch(Exception e){
										e.printStackTrace();
									}
								}
							}
						});									
						refresh();				
						getUI().addWindow(contatoEditor);
						janelaAtiva = true;
						
						contatoEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								janelaAtiva = false;							
								refresh();
							}
						});
						
					}
						
					}else{
						Notify.Show("Selecione somente um CRM por vez!", Notify.TYPE_WARNING);
					}
//				}else{
//					Notify.Show("Você não tem Permissão para Visualizar CRM", Notify.TYPE_ERROR);
//				}
			}
		});
		
		btVisualizar.setEnabled(false);
		return btVisualizar;
	}
	
	private Component BuildbtHistorico() {
		btHistorico = new Button("Histórico", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Histórico de CRM"))				
				{				
					
					if(tb.getValue() != null){
						final Set<Object> selecteds = (Set<Object>)tb.getValue();
						
						if(selecteds.size() == 1){
						
							HistoricoView historicoView;
							
							if(tb.getValue() != null && tb.getItem(selecteds.toArray()[0]) != null){
								historicoView = new HistoricoView(true, true, (Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("cliente.id").getValue(), null, null);
							}else{
								historicoView = new HistoricoView(true, true, null, null, null);
							}
											
							getUI().addWindow(historicoView);
							
						}else{
							Notify.Show("Selecione somente um CRM por vez!", Notify.TYPE_WARNING);
						}
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Visualizar o Histórico de Acesso dos Contratos!", Notify.TYPE_ERROR);
				}
			}
			
		});
		btHistorico.setEnabled(false);
		return btHistorico;
	 }
	
	private Component BuildbtLog() {
	btLog = new Button("Log", new Button.ClickListener() {
		
		@Override
		public void buttonClick(ClickEvent event) {
			
			if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
					OpusERP4UI.getUsuarioLogadoUI().getId(), "Log Alterações"))				
			{
				final Set<Object> selecteds = (Set<Object>)tb.getValue();
				if(selecteds.size() == 1){
						if(tb.getValue() != null){
							Integer codCrm = (Integer)tb.getItem(selecteds.toArray()[0]).getItemProperty("id").getValue();
							HistoricoAlteracoesCrm histAlte = new HistoricoAlteracoesCrm(true, true,codCrm);
							
							getUI().addWindow(histAlte);
						}
				}
			
			}else{
				Notify.Show("Você não Possui Permissão para Visualizar o Log de Alterações", Notify.TYPE_ERROR);
			}
			
		}
	 });
	
	btLog.setEnabled(false);
	return btLog;
    }
	
	
	

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}
