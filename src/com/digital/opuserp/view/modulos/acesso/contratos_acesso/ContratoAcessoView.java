package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AcessoDAO;
import com.digital.opuserp.dao.AlteracoesContratoDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.CrmDAO;
import com.digital.opuserp.dao.Desbloqueio72hDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.IttvDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.dao.SubModuloDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesContrato;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.CrmFormasContato;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.RadAcct;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.domain.Swith;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.AlterarConcentradorAcessoView.AlterarConcentradorAcessoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.AlterarCredenciaisAcessoView.AlterarCredenciaisAcessoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.AlterarEnderecoEditor.AlterarEnderecoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.AlterarFiadorEditor.MudarFiadorEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.AlterarIpFixoEditor.AlterarIpFixoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.AlterarIpFixoEditor.AlterarIpFixoListerner;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.AlterarTipoNfeEditor.AlterarTipoNfeEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.ComprarCreditoEditor.ComprarCreditosAcessoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.ComprarCreditoEditor.ComprarCreditosAcessoListerner;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.EditarMaterialEditor.EditarProdutoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.EditarMaterialEditorComodatoTotal.EditarMaterialComodatoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.EditarRegimeProprioEditor.EditarRegimeEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.LiberarBoletosContratoEditor.LiberarBoletosEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.LiberarCartaoClienteEditor.LiberarCartaoClienteEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.LiberarCredenciaisAcessoEditor.LiberarCredenciaisAcessoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.MudancaPlanoEditor.RenovarPlanoAcessoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.MudancaVencimentoEditor.MudancaVencimentoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.MudarTitularidadeEditor.MudarTitularidadeEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.NovoCadastroAcessoEditor.NovoCadastroEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.PendenciasEditor.PendenciasEditorEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.RemoverMaterial.RemoverMaterialEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.RenovarContratoAcessoEditor.RenovarContratoEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.SuspenderContratoEditor.SuspenderContratoEvent;
import com.digital.opuserp.view.modulos.acesso.notificacoes.NotificacoesView;
import com.digital.opuserp.view.util.Notify;
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
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
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
import com.vaadin.ui.themes.Reindeer;

public class ContratoAcessoView extends VerticalLayout {
	
	JPAContainer<AcessoCliente> container;
	
    Item item;
	//Refresher refresher;
	
	List<Object> columns;
	boolean atualizarColumns = false;
	boolean janelaAtiva = false;
		
	Table tb;
	TextField tfBusca;
	ComboBox cbStatus;
	
	Object[] visibleColumns;
	
	//Funcões Administrativas (Limpar Data de Alteração de Plano, Liberar Alteração de Plano com valor Menor, )
	Button btFuncoesAdminsitrativas;
	
	//Novo e Reativar
	Button btClientesConectados;
	Button btNovo;	
	Button btLiberar;
	Button btImprimir;
		
	//Mudanças de Vencimento e Titularidade
	Button btEdicoes;	
	Button btEncerrar;	
    //Button btAddFiltros;
	Button btAtualizar;

	//Visualizar todasas informações do contrato
	Button btVisualizar;
	
	//Historico de Acesso e Chamados
	Button btHistorico;
	
	ComboBox cbStatusCredenciasAcesso;
		
	HorizontalLayout hlFloat;
	HorizontalLayout hlButons;
	private Label lbRegistros;

	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	Window window;
	RadAcct radacct;
	
	private Integer codSubModulo;

	GerenciarModuloDAO gmDAO;
	private Button btLog;
	
	private Button btBloquear;

	public ContratoAcessoView(boolean act){
		
		if(act){
		setSizeFull();
		
		gmDAO = new GerenciarModuloDAO();
		
		hlButons = new HorizontalLayout();
		
		Integer cSubModulo = SubModuloDAO.findToId("Contratos de Acesso");
		if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(cSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Funcoes Administrativas"))				
		{
			hlButons.addComponent(BuildbtFuncoesAdministrativas());
		}
		
		hlButons.addComponent(BuildbtNovo());		
		hlButons.addComponent(BuildbtLiberar());
		hlButons.addComponent(BuildbtDocumentos());		
		hlButons.addComponent(BuildbtEdicoes());		
		hlButons.addComponent(BuildbtEncerrar());		
//		hlButons.addComponent(BuildbtAddFiltros());
		hlButons.addComponent(BuildbtVisualizar());		
		hlButons.addComponent(BuildbtHistorico());			
		hlButons.addComponent(BuildbtClientesConectador());
		hlButons.addComponent(buildBtBloquear("Bloquear/Desbloquear"));
		hlButons.addComponent(buildBtPendencia());
		hlButons.addComponent(buildBtLog());
				
//		HorizontalLayout hlButonsAtualizar = new HorizontalLayout();
//		hlButonsAtualizar.addComponent(BuildbtAtualizar());
		
//		addComponent(hlButonsAtualizar);
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

//		addComponent(hlButons);
//		setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
	
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
//		Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
//		lbLegend.setWidth("250px");
//		hlFloat.addComponent(lbLegend);
		hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
//		hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
	
	
		addComponent(hlFloat);		
		setExpandRatio(tb, 1);
		}
		
		//addExtension(buildRefresher());	
	}

	Object sortPropertyId;
	boolean isSortAscending = false;
	
//	
//	public Refresher buildRefresher(){
//	
//		
//		refresher = new Refresher();
//		refresher.setRefreshInterval(300000);
//		refresher.addListener(new Refresher.RefreshListener() {
//			
//			@Override
//			public void refresh(Refresher source) {
//								
//				if(!janelaAtiva){
//					atualizarColumns = true;
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
////					btAddFiltros.setEnabled(false);
//					btEdicoes.setEnabled(false);
//					btEncerrar.setEnabled(false);
//					btHistorico.setEnabled(false);
//					btImprimir.setEnabled(false);
//					btLiberar.setEnabled(false);
//					btVisualizar.setEnabled(false);
////					btBloquear.setCaption("Desbloquear");
////					btBloquear.setEnabled(false);
//				}		
//			}
//		});
//		
//		return refresher;
//	}
	
	
	
	private Component BuildbtAtualizar() {
		btAtualizar = new Button("Atualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				columns = new ArrayList<Object>();
				Object[] visible_columns = tb.getVisibleColumns();		
				for(Object c:visible_columns){
					if(!tb.isColumnCollapsed(c.toString())){					   	
						columns.add(c);
					}	
				}

				sortPropertyId = tb.getSortContainerPropertyId();				
				isSortAscending = tb.isSortAscending();
				
				replaceComponent(tb, buildTbGeneric());
				setExpandRatio(tb, 1);		
				
				for (Object object : columns) {					
					tb.setColumnCollapsed(object, false);
				}
						
				hlFloat.replaceComponent(lbRegistros, buildLbRegistros());

				btEdicoes.setEnabled(false);
				btEncerrar.setEnabled(false);
				btHistorico.setEnabled(false);
				btImprimir.setEnabled(false);
				//btLiberar.setEnabled(false);
				btVisualizar.setEnabled(false);
				//btBloquear.setCaption("Bloquear/Desbloquear");
				btBloquear.setEnabled(false);
				btPendencia.setEnabled(false); 
				//refresh();				
			}
		});
//		btAtualizar.setStyleName("atualizar");
		return btAtualizar;
	}

//bkp
//	private Component BuildbtVisualizar() {
//		btVisualizar = new Button("Visualizar", new Button.ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//				
//
//				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
//						OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar Contrato"))				
//				{
//					if (tb.getValue() != null){		
//						item = tb.getItem(tb.getValue());
//						VisualizarContrato visualizar = new VisualizarContrato(item,"Visualizar Contrato", true);
//						getUI().addWindow(visualizar);
//					}
//					
//				}else{					
//					Notify.Show("Você não Possui Permissão para Visualizar Contrato de Acesso", Notify.TYPE_ERROR);
//				}				
//			}
//		});
//		btVisualizar.setEnabled(false);											
//		return btVisualizar;
//	}
	
	private Component BuildbtVisualizar() {
		btVisualizar = new Button("Visualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				

				if (winSubMenuVisualizar != null && winSubMenuVisualizar.getUI() != null)
					winSubMenuVisualizar.close();
	             else {
	            	 
	            	 if(winSubMenuNovo != null && winSubMenuNovo.getUI() != null){
	            		 winSubMenuNovo.close();
	            	 }
	            	 
	            	 if(winSubMenuHistorico != null && winSubMenuHistorico.getUI() != null){
	            		 winSubMenuHistorico.close();
	            	 }
	            	 
	            	 if(winSubMenuMudanca != null && winSubMenuMudanca.getUI() != null){
	            		 winSubMenuMudanca.close();
	            	 }
	            	 
	            	 if(winSubMenuImprimir != null && winSubMenuImprimir.getUI() != null){
	            		 winSubMenuImprimir.close();
	            	 }
	            	 
	            	 if(winSubMenuBloqueioDesbloqueio != null && winSubMenuBloqueioDesbloqueio.getUI() != null){
							winSubMenuBloqueioDesbloqueio.close();
					 }
	            	 
	            	 if(winSubMenuFuncoesAdministrativas != null && winSubMenuFuncoesAdministrativas.getUI() != null){
	            		 winSubMenuFuncoesAdministrativas.close();
					 }
	            	 if(winSubMenuLiberar != null && winSubMenuLiberar.getUI() != null){
	            		 winSubMenuLiberar.close();
					 }
           	 
				     buildSubMenuVisualizar(event);
				     
					 getUI().addWindow(winSubMenuVisualizar);
					 winSubMenuVisualizar.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuVisualizar.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }	
				
				
			}
		});
		btVisualizar.setEnabled(false);											
		return btVisualizar;
	}
																												
	private Component BuildbtHistorico() {
		btHistorico = new Button("Histórico", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (winSubMenuHistorico != null && winSubMenuHistorico.getUI() != null)
					winSubMenuHistorico.close();
				else {
					
					if(winSubMenuLiberar != null && winSubMenuLiberar.getUI() != null){
						winSubMenuLiberar.close();
					}
					
					if(winSubMenuImprimir != null && winSubMenuImprimir.getUI() != null){
						winSubMenuImprimir.close();
					}
					
					if(winSubMenuMudanca != null && winSubMenuMudanca.getUI() != null){
						winSubMenuMudanca.close();
					}
					
					if(winSubMenuNovo != null && winSubMenuNovo.getUI() != null){
						winSubMenuNovo.close();
					}
					
					if(winSubMenuBloqueioDesbloqueio != null && winSubMenuBloqueioDesbloqueio.getUI() != null){
						winSubMenuBloqueioDesbloqueio.close();
					}
					
					if(winSubMenuFuncoesAdministrativas != null && winSubMenuFuncoesAdministrativas.getUI() != null){
						winSubMenuFuncoesAdministrativas.close();
					}
					if(winSubMenuVisualizar != null && winSubMenuVisualizar.getUI() != null){
	            		 winSubMenuVisualizar.close();
					 }
					
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
				
			}
		});
		btHistorico.setEnabled(false);
		return btHistorico;
	}



	private Component BuildbtEncerrar() {
		btEncerrar = new Button("Encerrar",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 	OpusERP4UI.getUsuarioLogadoUI().getId(), "Encerrar Contrato"))				
				{							
						if(tb.getValue() != null){
							
							item = tb.getItem(tb.getValue());
							final AcessoCliente ac = ContratosAcessoDAO.find((Integer) item.getItemProperty("id").getValue());
							Cliente cliente = (Cliente) item.getItemProperty("cliente").getValue();
							Integer codAcesso =(Integer)item.getItemProperty("id").getValue();
							ContasReceberDAO crDAO = new ContasReceberDAO();						

							GenericDialog cdEncerrar = new GenericDialog("Confirme para Continuar", "Você deseja Realmente Encerrar este Contrato?", true, true);
							cdEncerrar.addListerner(new GenericDialog.DialogListerner() {
								
								@Override
								public void onClose(DialogEvent event) {
										if(event.isConfirm()){
											CredenciaisAcessoDAO caDAo = new CredenciaisAcessoDAO();
											caDAo.encerrarContrato((Integer)item.getItemProperty("id").getValue());
																					
											refresh();
											
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Encerrou um Contrato de Acesso"));
											AlteracoesContratoDAO.save(new AlteracoesContrato(null, "ENCERRAMENTO CONTRATO", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										}
								}
							});
							
							getUI().addWindow(cdEncerrar);

						
						}
				}else{					
					Notify.Show("Você não Possui Permissão para Encerrar um Contrato", Notify.TYPE_ERROR);
				}
			}
		});
		btEncerrar.setEnabled(false);
		return btEncerrar;
	}
	
	

	private void refresh(){
		btAtualizar.click();
	}

	private Component BuildbtEdicoes() {
		btEdicoes = new Button("Alterar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				 if (winSubMenuMudanca != null && winSubMenuMudanca.getUI() != null)
				     	winSubMenuMudanca.close();
	             else {
	            	 
	            	 if(winSubMenuLiberar != null && winSubMenuLiberar.getUI() != null){
	            		 winSubMenuLiberar.close();
	            	 }
	            	 if(winSubMenuHistorico != null && winSubMenuHistorico.getUI() != null){
	            		 winSubMenuHistorico.close();
	            	 }	            	 
	            	 if(winSubMenuNovo != null && winSubMenuNovo.getUI() != null){
	            		 winSubMenuNovo.close();
	            	 }
	            	 if(winSubMenuImprimir != null && winSubMenuImprimir.getUI() != null){
	            		 winSubMenuImprimir.close();
	            	 }	            	 
	            	 if(winSubMenuBloqueioDesbloqueio != null && winSubMenuBloqueioDesbloqueio.getUI() != null){
						winSubMenuBloqueioDesbloqueio.close();
					 }	            	 
	            	 if(winSubMenuFuncoesAdministrativas != null && winSubMenuFuncoesAdministrativas.getUI() != null){
	            		 winSubMenuFuncoesAdministrativas.close();
					 }	  
	            	 if(winSubMenuVisualizar != null && winSubMenuVisualizar.getUI() != null){
	            		 winSubMenuVisualizar.close();
					 }
	            	 
				     buildSubMenuMudanca(event);
				     
					 getUI().addWindow(winSubMenuMudanca);
					 winSubMenuMudanca.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuMudanca.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }					
			}
		});
		btEdicoes.setEnabled(false);
		return btEdicoes;
	}

	

	private Component BuildbtDocumentos() {
		btImprimir = new Button("Documentos",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if (winSubMenuImprimir != null && winSubMenuImprimir.getUI() != null)
					winSubMenuImprimir.close();
				else {												
					
					if(winSubMenuLiberar != null && winSubMenuLiberar.getUI() != null){
						winSubMenuLiberar.close();
					}
					
					if(winSubMenuMudanca != null && winSubMenuMudanca.getUI() != null){
						winSubMenuMudanca.close();
					}
					
					if(winSubMenuNovo != null && winSubMenuNovo.getUI() != null){
						winSubMenuNovo.close();
					}
					
					if(winSubMenuBloqueioDesbloqueio != null && winSubMenuBloqueioDesbloqueio.getUI() != null){
						winSubMenuBloqueioDesbloqueio.close();
					}
					
					if(winSubMenuFuncoesAdministrativas != null && winSubMenuFuncoesAdministrativas.getUI() != null){
						winSubMenuFuncoesAdministrativas.close();
					}
					if(winSubMenuVisualizar != null && winSubMenuVisualizar.getUI() != null){
	            		 winSubMenuVisualizar.close();
					 }
					
					buildSubMenuImprimir(event);
					
					getUI().addWindow(winSubMenuImprimir);
					winSubMenuImprimir.focus();
					((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
						
						@Override
						public void layoutClick(LayoutClickEvent event) {
							winSubMenuImprimir.close();
							((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
						}
					});
				}	
				
				
				
			}
	
		});
		btImprimir.setEnabled(false);		
		return btImprimir;
	}

	private Component BuildbtLiberar() {
		btLiberar = new Button("Liberar",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if (winSubMenuLiberar != null && winSubMenuLiberar.getUI() != null)
					winSubMenuLiberar.close();
	             else {
	            	 
	            	 if(winSubMenuNovo != null && winSubMenuNovo.getUI() != null){
	            		 winSubMenuNovo.close();
	            	 }
	            	 
	            	 if(winSubMenuHistorico != null && winSubMenuHistorico.getUI() != null){
	            		 winSubMenuHistorico.close();
	            	 }
	            	 
	            	 if(winSubMenuMudanca != null && winSubMenuMudanca.getUI() != null){
	            		 winSubMenuMudanca.close();
	            	 }
	            	 
	            	 if(winSubMenuImprimir != null && winSubMenuImprimir.getUI() != null){
	            		 winSubMenuImprimir.close();
	            	 }
	            	 
	            	 if(winSubMenuBloqueioDesbloqueio != null && winSubMenuBloqueioDesbloqueio.getUI() != null){
							winSubMenuBloqueioDesbloqueio.close();
					 }
	            	 
	            	 if(winSubMenuFuncoesAdministrativas != null && winSubMenuFuncoesAdministrativas.getUI() != null){
	            		 winSubMenuFuncoesAdministrativas.close();
					 }
	            	 if(winSubMenuVisualizar != null && winSubMenuVisualizar.getUI() != null){
	            		 winSubMenuVisualizar.close();
					 }
	            	 
           	 
				     buildSubMenuLiberar(event);
				     
					 getUI().addWindow(winSubMenuLiberar);
					 winSubMenuLiberar.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuLiberar.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }		
				
				
				
			}
		});
		
		btLiberar.setEnabled(false);
		return btLiberar;
	}

	

	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public void buildShortcurEvents(Component c){
		
//		if(c instanceof CredenciaisAcessoView)
//		{
//			btNovo.addShortcutListener(buildShortCutNovo());
//			btEdicoes.addShortcutListener(buildShortCutEditar());
//		}else{
//			btNovo.removeShortcutListener(slNovo);	
//			btEdicoes.removeShortcutListener(slEditar);
//		}
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
				btEdicoes.click();
			}
		};
		return slEditar;
	}
	
	public JPAContainer<AcessoCliente> buildContainer(){
		
		container = JPAContainerFactory.makeBatchable(AcessoCliente.class, ConnUtil.getEntity());		
		container.setAutoCommit(false);
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("cliente.id");
		container.addNestedContainerProperty("plano.nome");
		container.addNestedContainerProperty("base.identificacao");
		container.addNestedContainerProperty("swith.identificacao");
		container.addNestedContainerProperty("swith.olt");
		container.addNestedContainerProperty("material.nome");
		container.addNestedContainerProperty("onu.nome");
		container.addNestedContainerProperty("contrato.nome");
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		addFilter(tfBusca.getValue());
		
		if(sortPropertyId == null){
			container.sort(new String[]{"id"},new boolean[]{false});
		}
		
		
		return container;
	}
		
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("arquivo_upload")){
					
				}
				
				if(colId.equals("id")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}
				
				if(colId.equals("data_venc_contrato")){
					
					try{					
						DateTime dt1 = new DateTime();
						DateTime dt2 = new DateTime((Date)tb.getItem(rowId).getItemProperty(colId).getValue());
						
						Integer days = Days.daysBetween(dt1, dt2).getDays();
						return days.toString();
					}catch(Exception e){
						e.printStackTrace();
						return "";
					}										
				}
				
				if(colId.equals("gpon")){
					try{
						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null && tb.getItem(rowId).getItemProperty("swith").getValue() != null){
							String valor = "";
							Swith s = (Swith)tb.getItem(rowId).getItemProperty("swith").getValue();
							valor = s.getPon()+"/"+tb.getItem(rowId).getItemProperty(colId).getValue().toString();
							
							return valor;
						}
						
						return "";
					}catch(Exception e){
						e.printStackTrace();
						return "ERR";
					}	
				}
				
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setImmediate(true);
		
		
		if(!atualizarColumns){
			tb.setColumnCollapsed("regime", true);
			tb.setColumnCollapsed("login", true);
			tb.setColumnCollapsed("senha", true);
			tb.setColumnCollapsed("endereco_mac", true);
			tb.setColumnCollapsed("carencia", true);
			tb.setColumnCollapsed("data_instalacao", true);
			tb.setColumnCollapsed("swith.identificacao", true);
			tb.setColumnCollapsed("interfaces", true);
			tb.setColumnCollapsed("signal_strength", true);
			tb.setColumnCollapsed("sinal_db", true);
			tb.setColumnCollapsed("onu.nome", true);
		}else{
			tb.setColumnCollapsed("regime", true);
			tb.setColumnCollapsed("login", true);
			tb.setColumnCollapsed("senha", true);
			tb.setColumnCollapsed("endereco_mac", true);
			tb.setColumnCollapsed("carencia", true);
			tb.setColumnCollapsed("data_instalacao", true);
			tb.setColumnCollapsed("swith.identificacao", true);
			tb.setColumnCollapsed("sinal_db", true);
			tb.setColumnCollapsed("interfaces", true);
			tb.setColumnCollapsed("signal_strength", true);
			tb.setColumnCollapsed("id", true);
			tb.setColumnCollapsed("plano.nome", true);
			tb.setColumnCollapsed("contrato.nome", true);
			tb.setColumnCollapsed("base.identificacao", true);
			tb.setColumnCollapsed("status_2", true);
			tb.setColumnCollapsed("dias", true);
			tb.setColumnCollapsed("material.nome", true);
			tb.setColumnCollapsed("onu.nome", true);
			
			for(Object colum : columns){
				tb.setColumnCollapsed(colum.toString(),false);
			}
		}
		
		tb.setColumnCollapsible("cliente.nome_razao",false);	
		tb.setColumnHeader("id", "Nº");
		tb.setColumnHeader("codigo_cartao", "Código cartão");
		tb.setColumnHeader("cliente.nome_razao", "Cliente*");
		tb.setColumnHeader("plano.nome", "Plano*");
		tb.setColumnHeader("base.identificacao", "Concentrador*");
		tb.setColumnHeader("interfaces", "Interfaces");
		tb.setColumnHeader("signal_strength", "Signal Strength");
		tb.setColumnHeader("sinal_db", "Sinal DB");
		tb.setColumnHeader("swith.identificacao","Caixa Atendimento*");
		tb.setColumnHeader("material.nome", "Material*");
		tb.setColumnHeader("contrato.nome", "Contrato*");
		tb.setColumnHeader("login", "Username");
		tb.setColumnHeader("senha", "Senha");
		tb.setColumnHeader("endereco_ip", "Endereço Fixo");
		tb.setColumnHeader("endereco_mac", "Endereço MAC");
		tb.setColumnHeader("status_2", "Status");
		tb.setColumnHeader("regime", "Regime");
		tb.setColumnHeader("data_venc_contrato", "Dias");
		tb.setColumnHeader("onu.nome", "Onu*");
		tb.setColumnHeader("onu_serial", "Onu Serial");
		tb.setColumnHeader("gpon", "Porta GPON");
		tb.setColumnHeader("swith.olt", "OLT*");
		tb.setColumnHeader("tem_pendencia", "Pendência");
		tb.setColumnHeader("arquivo_upload", "Up");

		tb.setColumnCollapsed("swith.olt", true);
		
		tb.setColumnHeader("data_instalacao", "Data Instalação");		
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {
                
                if (propertyId == null)
                    return "row-header-default"; // Will not actually be visible
               
                Item item = source.getItem(itemId);
                
//                if(item.getItemProperty("Up").getValue() != null && item.getItemProperty("Up").getValue().toString().equals("!")){
//                	return "row-header-exclamacao"; 
//                }
                
                if(item.getItemProperty("status_2").getValue() != null){
	                String status = item.getItemProperty("status_2").getValue().toString();
	               
	                if(status.equals("BLOQUEADO")){
	                    return "row-header-bloqueado"; // Will not actually be visible
	                }else if(status.equals("ENCERRADO")){
	                	return "row-header-encerrado"; // Will not actually be visible	               
	                }else{	                	
	                	if(item.getItemProperty("tem_pendencia").getValue() != null && item.getItemProperty("tem_pendencia").getValue().toString().equals("SIM")){	                		
	                		return "row-header-pendente";
	                	}else{
	                		return "row-header-default";	                		
	                	}	                	
	                }
                
                }else{
                	 return "row-header-default";
                }
               
            }
        });
        
		tb.addGeneratedColumn("Up", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {

				//Image img = new Image(null, new ThemeResource("icons/check-32-inative.png"));
				
				if(source.getItem(itemId).getItemProperty("arquivo_upload").getValue() != null){	
					if(source.getItem(itemId).getItemProperty("arquivo_upload").getValue().toString().equals("0")){
						return "";
					}else{
						return source.getItem(itemId).getItemProperty("arquivo_upload").getValue().toString();
					}
				}else{					
					return new Label("<div style='text-align:center;width: 100%;color: red;font-size: 14px;font-weight: bold;'>!</div>",ContentMode.HTML);					
				}
				
			}
		});
		
		tb.setColumnAlignment("Up", Align.CENTER);
		
		tb.addGeneratedColumn("Carência", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
								
				String carencia = "SEM CARÊNCIA";
				
				if(itemId != null){
					AcessoCliente ac = container.getItem(itemId).getEntity();
					
					if(ac.getCarencia() != null && ac.getCarencia().equals("SIM") && ac.getContrato().getCarencia().equals("SIM")){			
						try{
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							
							DateTime dt1 = new DateTime(sdf.parse(sdf.format(new Date())));
							DateTime dt2 = new DateTime(sdf.parse(sdf.format(ac.getData_venc_contrato()))).plusMonths(1);			 
							Integer months = Months.monthsBetween(dt1, dt2).getMonths();
							
							carencia = months.toString()+" MESES";
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}else{
					return null;
				}
				
				return carencia;
			}
		});
		
				   
		tb.setVisibleColumns(new Object[] {
				"id","Up","codigo_cartao","cliente.nome_razao","plano.nome","contrato.nome","regime","Carência","data_venc_contrato","base.identificacao","interfaces","onu.nome","onu_serial","swith.identificacao","swith.olt","gpon","sinal_db","signal_strength",
				"material.nome","login","senha","endereco_ip","endereco_mac","status_2","data_instalacao","tem_pendencia"});
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					
					if(btFuncoesAdminsitrativas != null){
						btFuncoesAdminsitrativas.setEnabled(false);
					}
					btLiberar.setEnabled(true);
					btImprimir.setEnabled(false);
					btEdicoes.setEnabled(false);
					btEncerrar.setEnabled(false);
					btVisualizar.setEnabled(false);
					btHistorico.setEnabled(false);
//					btBloquear.setCaption("Bloquear");
					btBloquear.setEnabled(false);
					//btLog.setEnabled(true);
					btPendencia.setEnabled(true);
					
					if(tb.getItem(tb.getValue()).getItemProperty("status_2").getValue() != null && 
							tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().equals("ATIVO")){
						btImprimir.setEnabled(true);
						btEdicoes.setEnabled(true);
						btEncerrar.setEnabled(true);
						btVisualizar.setEnabled(true);
						btHistorico.setEnabled(true);
//						btBloquear.setCaption("Bloquear");
     					btBloquear.setEnabled(true);
     					if(btFuncoesAdminsitrativas != null){
     						btFuncoesAdminsitrativas.setEnabled(true);
     					}
					}
					
					if(tb.getItem(tb.getValue()).getItemProperty("status_2").getValue() != null && 
							tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().equals("SUSPENSO")){
						btImprimir.setEnabled(true);
						btEdicoes.setEnabled(true);
						btEncerrar.setEnabled(true);
						btVisualizar.setEnabled(true);
						btHistorico.setEnabled(true);
						//btLiberar.setEnabled(true);
						if(btFuncoesAdminsitrativas != null){
							btFuncoesAdminsitrativas.setEnabled(true);
						}
					}					
					if(tb.getItem(tb.getValue()).getItemProperty("status_2").getValue() != null && 
							tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().equals("BLOQUEADO")){
						btImprimir.setEnabled(true);					
						btEncerrar.setEnabled(true);
						btEdicoes.setEnabled(true);
						btVisualizar.setEnabled(true);
						btHistorico.setEnabled(true);
//						btBloquear.setCaption("Desbloquear");
						btBloquear.setEnabled(true);
						
						if(btFuncoesAdminsitrativas != null){
							btFuncoesAdminsitrativas.setEnabled(true);
						}
					}
					
					if(tb.getItem(tb.getValue()).getItemProperty("status_2").getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().equals("ENCERRADO")){						
						btVisualizar.setEnabled(true);
						btHistorico.setEnabled(true);
//						btBloquear.setCaption("Bloquear");
						btBloquear.setEnabled(false);
						
						if(btFuncoesAdminsitrativas != null){
							btFuncoesAdminsitrativas.setEnabled(false);
						}
					}
					
					if(tb.getItem(tb.getValue()).getItemProperty("status_2").getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().equals("PENDENTE_INSTALACAO")){
						//btLiberar.setEnabled(true);
						btImprimir.setEnabled(true);
						btEdicoes.setEnabled(true);
						btEncerrar.setEnabled(true);
						btVisualizar.setEnabled(true);
//						btBloquear.setCaption("Bloquear");
						btBloquear.setEnabled(false);
						
						if(btFuncoesAdminsitrativas != null){
							btFuncoesAdminsitrativas.setEnabled(true);
						}
						
						
					
					}
					
				}else{
					btPendencia.setEnabled(false);
					btLiberar.setEnabled(false);
					btImprimir.setEnabled(false);
					btEdicoes.setEnabled(false);
					btEncerrar.setEnabled(false);
					btVisualizar.setEnabled(false);
					btHistorico.setEnabled(false);
//					btBloquear.setCaption("Bloquear");
					btBloquear.setEnabled(false);
					//btLog.setEnabled(false);
					
					if(btFuncoesAdminsitrativas != null){
						btFuncoesAdminsitrativas.setEnabled(false);
					}
				}
			}
		});
		
		if(sortPropertyId != null){
			tb.sort(new Object[]{sortPropertyId},new boolean[]{isSortAscending});			
		}

		return tb;
	}
	
	private boolean temFidelidade(AcessoCliente ac){
			boolean carencia = false;		
			
			if(ac.getCarencia() != null && ac.getCarencia().equals("SIM") && ac.getContrato().getCarencia().equals("SIM")){			
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					
					DateTime dt1 = new DateTime(sdf.parse(sdf.format(new Date())));
					DateTime dt2 = new DateTime(sdf.parse(sdf.format(ac.getData_venc_contrato()))).plusMonths(1);			 
					Integer months = Months.monthsBetween(dt1, dt2).getMonths();
					
					if(months > 0){
						carencia = true;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			
			if(carencia){ 
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					
					if(sdf.parse(sdf.format(ac.getData_alteracao_plano())).equals(sdf.parse(sdf.format(new Date())))){
						carencia  = false;
					}
				}catch (Exception e) {
					
				}
			}
			
			return carencia;
	}
	
	public ComboBox buildCbStatus(){
		
		cbStatusCredenciasAcesso = new ComboBox();
		cbStatusCredenciasAcesso.setNullSelectionAllowed(false);
		cbStatusCredenciasAcesso.setImmediate(true);
		cbStatusCredenciasAcesso.setTextInputAllowed(false); 
		cbStatusCredenciasAcesso.addItem("TODOS");			
		
		EntityManager em = ConnUtil.getEntity();		
		Query q = em.createNativeQuery("SELECT ac.STATUS_2 FROM acesso_cliente as ac GROUP BY STATUS_2");
		
		List<Object> result = q.getResultList();		
		for(Object o:result){
			cbStatusCredenciasAcesso.addItem(o.toString());			
		}
		cbStatusCredenciasAcesso.select("ATIVO");

		cbStatusCredenciasAcesso.setImmediate(true);
		cbStatusCredenciasAcesso.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				tb.refreshRowCache();
				//btLiberar.setEnabled(false);
				btImprimir.setEnabled(false);
				btEdicoes.setEnabled(false);
				btEncerrar.setEnabled(false);
//				btAddFiltros.setEnabled(false);
				btVisualizar.setEnabled(false);
				btHistorico.setEnabled(false);
//				btBloquear.setCaption("Bloquear");
//				btBloquear.setEnabled(false);
				
				addFilter(tfBusca.getValue());				
			}
		});
		
		cbStatusCredenciasAcesso.addItem("PENDENTE UPLOAD");			
		
		return cbStatusCredenciasAcesso;	
	}
	
	
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setInputPrompt("Buscar...");
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
	
	public void addFilter(Integer s) {
		// TODO Auto-generated method stub		
	}
	
	public void addFilter(String s) {		
		container.removeAllContainerFilters();
		container.setApplyFiltersImmediately(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(!cbStatusCredenciasAcesso.getValue().equals("TODOS")){			
			
			if(!cbStatusCredenciasAcesso.getValue().equals("PENDENTE UPLOAD")){
				container.addContainerFilter(Filters.eq("status_2", cbStatusCredenciasAcesso.getValue().toString()));				
			}else{
								
				//SELECT * FROM `acesso_cliente` WHERE `STATUS_2` != "ENCERRADO" AND `DATA_INSTALACAO` >= "2021-10-01" 
				//AND `DATA_INSTALACAO` <= '2021-11-31' AND `ARQUIVO_UPLOAD` =0 OR `STATUS_2` != "ENCERRADO" 
				//AND `DATA_ALTERACAO_PLANO` >= "2021-10-01" AND `DATA_ALTERACAO_PLANO` <= '2021-11-31' 
				//AND `ARQUIVO_UPLOAD` =0
				
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					
					container.addContainerFilter(Filters.eq("status_2", "ATIVO"));
					container.addContainerFilter(Filters.or(Filters.isNull("arquivo_upload"), Filters.eq("arquivo_upload", 0)));
					container.addContainerFilter(Filters.or(
							Filters.gteq("data_instalacao", sdf.parse("01/10/2021")),
							Filters.gteq("data_alteracao_plano", sdf.parse("01/10/2021"))
									));
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}
		
		if(s != null && !s.equals("") && !s.isEmpty()){

			Object[] collums = tb.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			try {
				Integer cod = Integer.parseInt(s);
				
				for(Object c:collums){		 			
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
						filtros.add(new Like(c.toString(), "%"+cod+"%", false));
					}			
					
					if(!c.equals("Up") && !c.equals("material.nome") && !c.equals("Dias") && !c.equals("Carência") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
				}
				
			} catch (Exception e) {
					
					for(Object c:collums){					
											
								if(!c.equals("Up") && !c.equals("material.nome") && !c.equals("Dias") && !c.equals("Carência") && !tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
									filtros.add(new Like(c.toString(), "%"+s+"%", false));			
								}
						}
				}
			container.addContainerFilter(Filters.or(filtros));
		}
		
		container.applyFilters();
		
		if(lbRegistros != null){
			hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
		}

		
		if(sortPropertyId == null){
			container.sort(new String[]{"id"},new boolean[]{false});
		}
		
		
	}
	
	public Button BuildbtClientesConectador(){
		btClientesConectados = new Button("Conectados", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				ClientesConectadosView clientesConectadosview = new ClientesConectadosView();
				clientesConectadosview.center();
				clientesConectadosview.setModal(true); 
				
				getUI().addWindow(clientesConectadosview);
			}
		});
		
		return btClientesConectados;
	}
	
	
	public Button buildBtBloquear(String s){
		btBloquear = new Button(s, new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
							
						if(tb.getValue() != null){		
							
							 if (winSubMenuBloqueioDesbloqueio != null && winSubMenuBloqueioDesbloqueio.getUI() != null)
								 winSubMenuBloqueioDesbloqueio.close();
				             else {
				            	 
				            	 if(winSubMenuLiberar != null && winSubMenuLiberar.getUI() != null){
				            		 winSubMenuLiberar.close();
				            	 }
				            	 
				            	 if(winSubMenuHistorico != null && winSubMenuHistorico.getUI() != null){
				            		 winSubMenuHistorico.close();
				            	 }
				            	 
				            	 if(winSubMenuNovo != null && winSubMenuNovo.getUI() != null){
				            		 winSubMenuNovo.close();
				            	 }				            	 
				            	 
				            	 if(winSubMenuMudanca != null && winSubMenuMudanca.getUI() != null){
				            		 winSubMenuMudanca.close();
				            	 }
				            	 
				            	 if(winSubMenuImprimir != null && winSubMenuImprimir.getUI() != null){
				            		 winSubMenuImprimir.close();
				            	 }
				            	 
				            	 if(winSubMenuFuncoesAdministrativas != null && winSubMenuFuncoesAdministrativas.getUI() != null){
				            		 winSubMenuFuncoesAdministrativas.close();
				            	 }
				            	 if(winSubMenuVisualizar != null && winSubMenuVisualizar.getUI() != null){
				            		 winSubMenuVisualizar.close();
								 }
				            	 
							     buildSubMenuBloqueioDesbloqueio(event);
							     
								 getUI().addWindow(winSubMenuBloqueioDesbloqueio);
								 winSubMenuBloqueioDesbloqueio.focus();
				                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
				                             
				               			@Override
				                        public void layoutClick(LayoutClickEvent event) {
				               				winSubMenuBloqueioDesbloqueio.close();
				                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
				                        }
				                 });
				             }			
							

											
						}
				
				
			}
		});
		btBloquear.setEnabled(false);
		return btBloquear;
	}
	
	Button btPendencia;
	public Button buildBtPendencia(){
		btPendencia = new Button("Pendências", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Pendencia Contrato"))				
				{
				
						Item item = tb.getItem(tb.getValue());
						
						final PendenciasEditor pendenciasEditor = new PendenciasEditor(item, "Pendências", true);
						pendenciasEditor.addListerner(new PendenciasEditor.PendenciasEditorListerner() {
							
							@Override
							public void onClose(PendenciasEditorEvent event) {								
									
									pendenciasEditor.close();								
									refresh();
						
							}
						});
						
						getUI().addWindow(pendenciasEditor);
				}else{
					Notify.Show("Você não Possui Permissão para Comprar Créditos Pré-Pago", Notify.TYPE_ERROR);
				}
				
			}
		});

		btPendencia.setEnabled(false); 
		return btPendencia;
	}
	
	public Button buildBtLog(){
		btLog = new Button("Log", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
//				if(tb.getValue() != null){				
//						Integer codAcesso = (Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue();
//						HistoricoAlteracoes histAlte = new HistoricoAlteracoes(true, true,codAcesso);
//						
//						getUI().addWindow(histAlte);			
//				}
				
				
				 if (winSubMenuLogs != null && winSubMenuLogs.getUI() != null)
					 winSubMenuLogs.close();
	             else {
	            	 
	            	 if(winSubMenuLiberar != null && winSubMenuLiberar.getUI() != null){
	            		 winSubMenuLiberar.close();
	            	 }
	            	 
	            	 if(winSubMenuHistorico != null && winSubMenuHistorico.getUI() != null){
	            		 winSubMenuHistorico.close();
	            	 }
	            	 
	            	 if(winSubMenuMudanca != null && winSubMenuMudanca.getUI() != null){
	            		 winSubMenuMudanca.close();
	            	 }
	            	 

	            	 if(winSubMenuNovo != null && winSubMenuNovo.getUI() != null){
	            		 winSubMenuNovo.close();
	            	 }
	            	 
	            	 if(winSubMenuFuncoesAdministrativas != null && winSubMenuFuncoesAdministrativas.getUI() != null){
	            		 winSubMenuFuncoesAdministrativas.close();
	            	 }
	            	 if(winSubMenuVisualizar != null && winSubMenuVisualizar.getUI() != null){
	            		 winSubMenuVisualizar.close();
					 }
	            	             	 
				     buildSubMenuLogs(event);
				     
					 getUI().addWindow(winSubMenuLogs);
					 winSubMenuLogs.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuLogs.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }	
				
			}
		});
		//btLog.setEnabled(false);
		return btLog;
	}
	
	public Button BuildbtFuncoesAdministrativas() {
		btFuncoesAdminsitrativas = new Button("Funções Admin.", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				 if (winSubMenuFuncoesAdministrativas != null && winSubMenuFuncoesAdministrativas.getUI() != null)
					 winSubMenuFuncoesAdministrativas.close();
	             else {
	            	 
	            	 if(winSubMenuHistorico != null && winSubMenuHistorico.getUI() != null){
	            		 winSubMenuHistorico.close();
	            	 }
	            	 
	            	 if(winSubMenuMudanca != null && winSubMenuMudanca.getUI() != null){
	            		 winSubMenuMudanca.close();
	            	 }
	            	 
	            	 if(winSubMenuImprimir != null && winSubMenuImprimir.getUI() != null){
	            		 winSubMenuImprimir.close();
	            	 }
	            	 
	            	 if(winSubMenuBloqueioDesbloqueio != null && winSubMenuBloqueioDesbloqueio.getUI() != null){
							winSubMenuBloqueioDesbloqueio.close();
					 }
	            	 if(winSubMenuVisualizar != null && winSubMenuVisualizar.getUI() != null){
	            		 winSubMenuVisualizar.close();
					 }

	            	             	 
	            	 buildSubMenuFuncoesAdmin(event);
				     
					 getUI().addWindow(winSubMenuFuncoesAdministrativas);
					 winSubMenuFuncoesAdministrativas.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuFuncoesAdministrativas.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }				
			}
		});
		btFuncoesAdminsitrativas.setStyleName("admin");
		btFuncoesAdminsitrativas.setEnabled(false); 
		return btFuncoesAdminsitrativas;
	}

	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				 if (winSubMenuNovo != null && winSubMenuNovo.getUI() != null)
					 winSubMenuNovo.close();
	             else {
	            	 
	            	 if(winSubMenuLiberar != null && winSubMenuLiberar.getUI() != null){
	            		 winSubMenuLiberar.close();
	            	 }	            	 
	            	 if(winSubMenuHistorico != null && winSubMenuHistorico.getUI() != null){
	            		 winSubMenuHistorico.close();
	            	 }	            	 
	            	 if(winSubMenuMudanca != null && winSubMenuMudanca.getUI() != null){
	            		 winSubMenuMudanca.close();
	            	 }	            	 
	            	 if(winSubMenuImprimir != null && winSubMenuImprimir.getUI() != null){
	            		 winSubMenuImprimir.close();
	            	 }	            	 
	            	 if(winSubMenuBloqueioDesbloqueio != null && winSubMenuBloqueioDesbloqueio.getUI() != null){
							winSubMenuBloqueioDesbloqueio.close();
					 }
	            	 if(winSubMenuFuncoesAdministrativas != null && winSubMenuFuncoesAdministrativas.getUI() != null){
	            		 winSubMenuFuncoesAdministrativas.close();
					 }

	            	             	 
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
		
		return btNovo;
	}
	
	Window winSubMenuFuncoesAdministrativas;
	private void buildSubMenuFuncoesAdmin(ClickEvent event) {
		winSubMenuFuncoesAdministrativas = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuFuncoesAdministrativas.setContent(l);
        winSubMenuFuncoesAdministrativas.setWidth("300px");
        winSubMenuFuncoesAdministrativas.addStyleName("notifications");
        winSubMenuFuncoesAdministrativas.setClosable(false);
        winSubMenuFuncoesAdministrativas.setResizable(false);
        winSubMenuFuncoesAdministrativas.setDraggable(false);
        winSubMenuFuncoesAdministrativas.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuFuncoesAdministrativas.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuFuncoesAdministrativas.setCloseShortcut(KeyCode.ESCAPE, null);
        
        Button btLiberarPlanoMenorValor = new Button("Liberar/Colocar Verificação de Plano com Menor Valor", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
			
						closeAllWindows();
				
						if(tb.getValue() != null){
							
							EntityItem<AcessoCliente> entityItemContrato = (EntityItem<AcessoCliente>)tb.getItem(tb.getValue());
							final AcessoCliente contrato = entityItemContrato.getEntity();
							
							if(contrato != null){
								
								GenericDialog gdConfirm = new GenericDialog("Confirme para continuar!", "Deseja realmente RETIRAR/COLOCAR a verificação de planos com valor menor na alteração de Plano ? ", true, true);
								gdConfirm.addListerner(new GenericDialog.DialogListerner() {
									
									@Override
									public void onClose(DialogEvent event) {
										if(event.isConfirm()){
											boolean check = AcessoDAO.LiberarColocarVerificacaoPlanoValorMenor(contrato);
											
											if(check){
												Notify.Show("Liberado/Colocado Verificação de Plano de Menor Valor para Este Contrato", Notify.TYPE_SUCCESS);
											}else{
												Notify.Show("Erro ao Liberar/Colocar Verificação de Plano de Menor Valor", Notify.TYPE_ERROR);
											}
										}
									}
								});
								
								getUI().addWindow(gdConfirm);
								
							}
						}				
				
			}		
		});     
        btLiberarPlanoMenorValor.setPrimaryStyleName("btSubMenu");
        
        Button btLimparDataAlteracaoPlano = new Button("Limpar Data Alteração Plano", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				closeAllWindows();
				
				if(tb.getValue() != null){
					
					EntityItem<AcessoCliente> entityItemContrato = (EntityItem<AcessoCliente>)tb.getItem(tb.getValue());
					final AcessoCliente contrato = entityItemContrato.getEntity();
					
					if(contrato != null){
						
						GenericDialog gdConfirm = new GenericDialog("Confirme para continuar!", 
								"Deseja realmente Limpar a data de Alteração de Plano ? ", true, true);
						gdConfirm.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									boolean check = AcessoDAO.LimparDataAlteracaoPlano(contrato);											
									
									if(check){
										Notify.Show("Data de Alteração de Plano foi resetada para Este Contrato", Notify.TYPE_SUCCESS);
									}else{
										Notify.Show("Erro ao limpar a data de alteração", Notify.TYPE_ERROR);
									}
								}
							}
						});
						
						getUI().addWindow(gdConfirm);
						
					}
				}						
						
			}
		});
        btLimparDataAlteracaoPlano.setPrimaryStyleName("btSubMenu");

        l.addComponent(btLiberarPlanoMenorValor);
        l.addComponent(btLimparDataAlteracaoPlano);
        
       
    }
	Window winSubMenuLiberar;
	Window winSubMenuVisualizar;
	
	
	private void buildSubMenuVisualizar(ClickEvent event) {
		winSubMenuVisualizar = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();

        winSubMenuVisualizar.setContent(l);
        winSubMenuVisualizar.setWidth("300px");
        winSubMenuVisualizar.addStyleName("notifications");
        winSubMenuVisualizar.setClosable(false);
        winSubMenuVisualizar.setResizable(false);
        winSubMenuVisualizar.setDraggable(false);
        winSubMenuVisualizar.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuVisualizar.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuVisualizar.setCloseShortcut(KeyCode.ESCAPE, null);
        
       
        Button btDadosCliente = new Button("Dados do cliente", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				

				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar Contrato"))				
				{
					if (tb.getValue() != null){		
						item = tb.getItem(tb.getValue());
						VisualizarContratoDadosCliente visualizar = new VisualizarContratoDadosCliente(item,"Visualizar Contrato", true);
						getUI().addWindow(visualizar);
					}
					
				}else{					
					Notify.Show("Você não Possui Permissão para Visualizar Contrato de Acesso", Notify.TYPE_ERROR);
				}				
			}
		});
        btDadosCliente.setPrimaryStyleName("btSubMenu");
        
        Button btInformacoesTecnica = new Button("Informações tecnicas", new Button.ClickListener() {
			
			@Override 
			public void buttonClick(ClickEvent event) {
				

				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar Contrato"))				
				{
					if (tb.getValue() != null){		
						item = tb.getItem(tb.getValue());
						VisualizarContratoInfoTecnica visualizar = new VisualizarContratoInfoTecnica(item,"Visualizar Contrato", true);
						getUI().addWindow(visualizar);
					}
					
				}else{					
					Notify.Show("Você não Possui Permissão para Visualizar Contrato de Acesso", Notify.TYPE_ERROR);
				}				
			}
		});
        btInformacoesTecnica.setPrimaryStyleName("btSubMenu");
       
        l.addComponent(btDadosCliente);
        l.addComponent(btInformacoesTecnica);
        
    }
	
	private void buildSubMenuLiberar(ClickEvent event) {
		winSubMenuLiberar = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();

        winSubMenuLiberar.setContent(l);
        winSubMenuLiberar.setWidth("300px");
        winSubMenuLiberar.addStyleName("notifications");
        winSubMenuLiberar.setClosable(false);
        winSubMenuLiberar.setResizable(false);
        winSubMenuLiberar.setDraggable(false);
        winSubMenuLiberar.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuLiberar.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuLiberar.setCloseShortcut(KeyCode.ESCAPE, null);
        
        Button btLiberarCartaoCliente = new Button("Liberar Cartão Cliente", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
											
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Liberar Cartao Cliente"))				
				{					
						closeAllWindows();
						
						if(tb.getValue() != null){
							janelaAtiva = true;
							
							Object value = tb.getValue();
							btAtualizar.click();
							item = tb.getItem(value);	
							
							final EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item;						
							final String codigo_antigo = entityItem.getEntity().getCodigo_cartao(); 
							
							final LiberarCartaoClienteEditor lcrEditor = new LiberarCartaoClienteEditor("Liberar Cartão Cliente",true);
							
							lcrEditor.addListerner(new LiberarCartaoClienteEditor.LiberarCartaoClienteListerner() {
								
								@Override
								public void onClose(LiberarCartaoClienteEvent event) {
									if(event.isConfirm()){			

										boolean check = AcessoDAO.liberarCartaoCliente(entityItem.getEntity(), codigo_antigo, event.getCodigoCartao());
										
										if(check){											
											lcrEditor.close();
											Notify.Show("Cartão liberado com Sucesso!", Notify.TYPE_SUCCESS);
											refresh();
											
											AlteracoesContratoDAO.save(new AlteracoesContrato(null, "LIBEROU CARTAO: "+event.getCodigoCartao(),entityItem.getEntity() , OpusERP4UI.getUsuarioLogadoUI(), new Date()));
										}							
									}									
								}
							});
							
							lcrEditor.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									tb.focus();
									janelaAtiva= false;
								}
							});
							
							getUI().addWindow(lcrEditor);
						}
					}else{
						Notify.Show("Você não Possui Permissão para Liberar Cartão Cliente", Notify.TYPE_ERROR);
					}				
			
			}
		});
        btLiberarCartaoCliente.setPrimaryStyleName("btSubMenu"); 
        
        Button btLiberarCredenciais = new Button("Liberar Credenciais", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tb.getValue() != null && 
						tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().toString().equals("PENDENTE_INSTALACAO")){
					
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
							OpusERP4UI.getUsuarioLogadoUI().getId(), "Liberar Credenciais"))				
					{						
							closeAllWindows();
							
							if(tb.getValue() != null){
								
								boolean check_boletos_gerados = false;
								Integer contrato = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
								AcessoCliente c_cliente = ContratosAcessoDAO.find(contrato);									
								if(!c_cliente.getContrato().getNome().equals("GRATIS")){
									List<ContasReceber> boletos = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(contrato);
									
									if(boletos != null && boletos.size() > 0){
										check_boletos_gerados = true;
									}
								}else{
									check_boletos_gerados = true;
								}
								
								
								
								if(check_boletos_gerados){						
								
										janelaAtiva = true;
										
										Object value = tb.getValue();
										btAtualizar.click();
										item = tb.getItem(value);	
										
										EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item;
										final BeanItem<AcessoCliente> beanItem = new BeanItem<AcessoCliente>(entityItem.getEntity());
										
										final LiberarCredenciaisAcessoEditor lcrEditor = new LiberarCredenciaisAcessoEditor(beanItem, "Liberar Credenciais de Acesso",true);
										
										lcrEditor.addListerner(new LiberarCredenciaisAcessoEditor.LiberarCredenciaisAcessoListerner() {
											
											@Override
											public void onClose(LiberarCredenciaisAcessoEvent event) {
												
												if(event.isConfirm()){										
													
													boolean check;
													AcessoCliente contrato = beanItem.getBean();
													if(contrato.getRegime().equals("COMODATO (TOTAL)")){
														check = AcessoDAO.liberarAcessoComodatoTotal(contrato, event.getCodSerial(), event.getCodMac());
													}else{
														check = AcessoDAO.liberarAcesso(contrato,event.getCodSerial());
													}
																							
													if(check){
														
														List<ContasReceber> boletos = ContasReceberDAO.buscarTitulosAbertosNaoVencidosDeAcessoPorContrato(beanItem.getBean().getId());
														ContasReceber primeiroBoleto = boletos.get(0);
														Date dataCrm = new DateTime(primeiroBoleto.getData_vencimento()).plusDays(5).toDate();
														
														Ose ose = OseDAO.findByContrato(contrato);													
														//check = AcessoDAO.liberarAcesso(contrato,event.getCodSerial())
														//Buscar Setor, Assunto, Forma de Contato
														Crm crm = new Crm();
														crm.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
														crm.setSetor(new Setores(2));
														crm.setCliente(primeiroBoleto.getCliente());
														crm.setCrm_assuntos(new CrmAssunto(363));
														crm.setCrm_formas_contato(new CrmFormasContato(15));
														crm.setContato(primeiroBoleto.getCliente().getContato());
														crm.setOrigem("ROTINA");
														crm.setConteudo("POS-VENDA INSTALACAO PRIMEIRO BOLETO");
														crm.setData_agendado(dataCrm);
														crm.setData_cadastro(new Date());
														crm.setStatus("AGENDADO");
														crm.setOperador(ose != null ? ose.getOperador_abertura() : "OSE NAO LOCALIZADA");
														
														boolean check1 = CrmDAO.insertCRM(crm);
														System.out.println(check1);
														
														
														lcrEditor.close();
														refresh();
														Notify.Show("Contrato Liberado com Sucesso!", Notify.TYPE_SUCCESS);
													}
												}else{
													lcrEditor.close();
													Notify.Show("liberação de acesso cancelada", Notify.TYPE_NOTICE);
												}
												
											}
										});
										
										lcrEditor.addCloseListener(new Window.CloseListener() {
											
											@Override
											public void windowClose(CloseEvent e) {
												tb.focus();
												janelaAtiva= false;
											}
										});
										
										getUI().addWindow(lcrEditor);
								}else{
									Notify.Show("É Necessário gerar os Boletos antes de gerar as Credenciais", Notify.TYPE_ERROR);
								}
							}
					}else{						
						Notify.Show("Você não Possui Permissão para Liberar Credenciais", Notify.TYPE_ERROR);
					}
					
				}else{					
					Notify.Show("Só é permitido liberar credenciais para contratos PENDENTE INSTALAÇÃO!", Notify.TYPE_WARNING);
				}
				
				
				
				
		
			}
		});
        btLiberarCredenciais.setPrimaryStyleName("btSubMenu");
      
        Button btLiberarBoletos = new Button("Liberar Boletos", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tb.getValue() != null && 
						tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().toString().equals("PENDENTE_INSTALACAO")){
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Liberar Boletos"))				
				{					
						closeAllWindows();
						
						if(tb.getValue() != null){
							janelaAtiva = true;
							
							Object value = tb.getValue();
							btAtualizar.click();
							item = tb.getItem(value);	
							
							EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item;
							final BeanItem<AcessoCliente> beanItem = new BeanItem<AcessoCliente>(entityItem.getEntity());
							
							final LiberarBoletosContratoEditor lcrEditor = new LiberarBoletosContratoEditor(beanItem, "Liberar Boletos",true);
							
							lcrEditor.addListerner(new LiberarBoletosContratoEditor.LiberarBoletosListerner() {
								
								@Override
								public void onClose(LiberarBoletosEvent event) {
									if(event.isConfirm()){			

										boolean check = AcessoDAO.liberarBoletos(beanItem.getBean(), event.getDataPrimeiroBoleto(),event.getValorPrimeiroBoleto());
										
										if(check){
											
											lcrEditor.close();
											Notify.Show("Boletos liberados com Sucesso!", Notify.TYPE_SUCCESS);
											refresh();
										}
							
									}else{
										lcrEditor.close();
										Notify.Show("liberação de boletos cancelada.", Notify.TYPE_NOTICE);
									}
									
								}
							});
							
							lcrEditor.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									tb.focus();
									janelaAtiva= false;
								}
							});
							
							getUI().addWindow(lcrEditor);
						}
					}else{
						Notify.Show("Você não Possui Permissão para Liberar Boletos", Notify.TYPE_ERROR);
					}
				
				}else{
					Notify.Show("Só é permitido liberar boletos para contratos PENDENTE INSTALAÇÃO!", Notify.TYPE_WARNING);
				}
			}
		});
        btLiberarBoletos.setPrimaryStyleName("btSubMenu"); 
       
        Button btLiberarItTv = new Button("Liberar ItTV", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				if(tb.getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().toString().equals("ATIVO")){
										
					final Window winLiberarITTV = new Window("Liberar ITTV");
					
					final VerticalLayout vlRootITTV = new VerticalLayout(){
						{
								setStyleName("border-form");
								setMargin(true);
								
								EntityManager em = ConnUtil.getEntity();
								final AcessoCliente contrato = em.find(AcessoCliente.class, tb.getItem(tb.getValue()).getItemProperty("id").getValue());
								
								addComponent(new FormLayout(){
									{
										setStyleName("form-cutom");
										setMargin(true);
										setSpacing(true);
										
										TextField txtNome = new TextField("Nome");
										txtNome.setRequired(true);
										txtNome.setWidth("280px");
										txtNome.setStyleName("caption-align-ittv");	
										txtNome.setValue(contrato.getCliente().getNome_razao());
										txtNome.setReadOnly(true);

										addComponent(txtNome);
									}
								});
								
								final TextField txtEmail = new TextField("Email"); 
								addComponent(new FormLayout(){
									{
										setStyleName("form-cutom");
										setMargin(true);
										setSpacing(true);
										
										txtEmail.setRequired(true);
										txtEmail.setWidth("280px");
										txtEmail.setStyleName("caption-align-ittv");
										txtEmail.setValue(contrato.getCliente().getEmail().toLowerCase());
										txtEmail.setReadOnly(true); 

										addComponent(txtEmail);
									}
								});					
								
								final TextField txtUsername = new TextField("Usuário");
								txtUsername.setRequired(true);
								txtUsername.setWidth("280px");
								txtUsername.setStyleName("caption-align-ittv");
								addComponent(new FormLayout(){
									{
										setStyleName("form-cutom");
										setMargin(true);
										setSpacing(true);
										
										if(contrato.getU_ittv() != null){
											txtUsername.setValue(contrato.getU_ittv());
										}
										addComponent(txtUsername);
										if(contrato.getCliente().getEmail() != null){
											txtUsername.setValue(contrato.getCliente().getEmail().toLowerCase());	
										}
										txtUsername.setReadOnly(true); 
									}
								});
								
								final TextField txtSenha = new TextField("Senha");
								txtSenha.setRequired(true);
								
								txtSenha.setStyleName("caption-align-ittv");
								addComponent(new FormLayout(){
									{
										setStyleName("form-cutom");
										setMargin(true);
										setSpacing(true);
										
										if(contrato.getS_ittv() != null){
											txtSenha.setValue(contrato.getS_ittv());
										}
										addComponent(txtSenha);
									}
								});
															
								
								HorizontalLayout hlButtons = new HorizontalLayout();
								hlButtons.setStyleName("hl_buttons_bottom");
								hlButtons.setSpacing(true);
								hlButtons.setMargin(true);
								//hlButtons.addComponent(buildBtCancelar());
								
								if(contrato.getIttv_id() != null){
									Button btPainel = new Button("painel");
									btPainel.setStyleName(Reindeer.BUTTON_LINK);
									
									String link = "https://resellers.ittv.com.br/cliente/"+contrato.getIttv_id();
									BrowserWindowOpener openIpPool = new BrowserWindowOpener(link);
									//openIpPool.setFeatures("height=300,width=200");
									openIpPool.extend(btPainel);									
									hlButtons.addComponent(btPainel);
								}
								
								hlButtons.addComponent(new Button("Cadastrar", new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										try{
											if(txtUsername.getValue() != null && !txtUsername.getValue().isEmpty() && !txtUsername.getValue().equals("") &&
													txtSenha.getValue() != null && !txtSenha.getValue().isEmpty() && !txtSenha.getValue().equals("") &&
													txtSenha.getValue().toString().length() >= 7){
												
												String emailCliente = contrato.getCliente().getEmail();
												
												if(emailCliente == null || emailCliente.equals("") || emailCliente.isEmpty()){
													contrato.getCliente().setEmail(txtEmail.getValue());
												}
												
												IttvDAO.liberarNovoUsuario(contrato, txtUsername.getValue(), txtSenha.getValue());
												
												winLiberarITTV.close();
											}else{
												Notify.Show("Campos obrigatórios precisam ser preenchidos!", Notify.TYPE_WARNING);
												
												if(txtSenha.getValue().toString().length() < 7){
													Notify.Show("Informe uma senha de no mínimo 7 digitos", Notify.TYPE_WARNING);
												}
												
											}
										}catch(Exception e){
											e.printStackTrace();
										}
									}
								}));
								
								
								
								
								
								addComponent(hlButtons);
								setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);	
							
						}						
					};					
					winLiberarITTV.setContent(new VerticalLayout(){
						{
							setMargin(true);
							addComponent(vlRootITTV);
						}
					});
					
					winLiberarITTV.setWidth("433px");
					winLiberarITTV.setHeight("250px");
					winLiberarITTV.setResizable(false);
										
					vlRootITTV.setSizeFull();
					
					winLiberarITTV.setModal(true);
					winLiberarITTV.center();
					getUI().addWindow(winLiberarITTV); 
					
				}else{
					Notify.Show("Só é permitido liberar ITTV para contratos ATIVOS!", Notify.TYPE_WARNING);
				}
				
				//Cadastrar Customer
								
				//Liberar Senha
				
				//Libera Plano
								
				//marconi@digitalonline.com.br
				//60e34b5c8015bc00432a39e3

				//token
				//eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoidDB0NGx3M2IiLCJpYXQiOjE2MTk1NDQzODZ9.EBBexM2pxtmD6HYqobonUg8t9l1eYZITtmvWRf_lLC4

				//TOTALWEB SMART TOTAL
				//5f995ca5592ce60001fe4e1f

				//TOTALWEB SMART PLUS
				//5f995bb3592ce60001fe4e14

				//TOTALWEB SMART MINI
				//5f995aba592ce60001fe4e07
				
			}
		});
        btLiberarItTv.setPrimaryStyleName("btSubMenu");
        
        l.addComponent(btLiberarCartaoCliente);
        l.addComponent(btLiberarBoletos);
        l.addComponent(btLiberarCredenciais);
        
        EntityItem<AcessoCliente> entityItem =  (EntityItem<AcessoCliente>)tb.getItem(tb.getValue());
        AcessoCliente contratoSelecionado = entityItem.getEntity();
        
        if(contratoSelecionado.getPlano().getPossui_ittv() != null && contratoSelecionado.getPlano().getPossui_ittv().equals("SIM")){
        	l.addComponent(btLiberarItTv);
        }
    }	
	
	Window winSubMenuNovo;
	Window wNovoPreContrato;
	
	private void buildSubMenu(ClickEvent event) {
		winSubMenuNovo = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuNovo.setContent(l);
        winSubMenuNovo.setWidth("300px");
        winSubMenuNovo.addStyleName("notifications");
        winSubMenuNovo.setClosable(false);
        winSubMenuNovo.setResizable(false);
        winSubMenuNovo.setDraggable(false);
        winSubMenuNovo.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuNovo.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuNovo.setCloseShortcut(KeyCode.ESCAPE, null);
        
        Button bt1Renovar = new Button("Renovar Contrato", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
			
			closeAllWindows();
			if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Renovar Contrato"))				
			{
						if(tb.getValue() != null){
						
								Object value = tb.getValue();
								btAtualizar.click();
								item = tb.getItem(value);	
							
								EntityManager em = ConnUtil.getEntity();
								
								final Integer codAcesso = (Integer) item.getItemProperty("id").getValue();
								AcessoCliente ac = em.find(AcessoCliente.class, codAcesso);																
								ContasReceberDAO crDAO = new ContasReceberDAO();
								
								DateTime dt1 = new DateTime();
								DateTime dt2 = new DateTime(ac.getData_venc_contrato()).plusMonths(1).plusDays(1);			 
								Integer months = Months.monthsBetween(dt1, dt2).getMonths();	

								DateTime dt3 = new DateTime(ac.getData_venc_contrato());	
								Integer days = Days.daysBetween(dt1, dt3).getDays();	
								
								boolean data_allow = true;
								if(ac.getData_alteracao_plano() != null){
									DateTime dtt1 = new DateTime();
									DateTime dtt2 = new DateTime(ac.getData_renovacao()).plusMonths(1).plusDays(1);
									
									if(dtt1.toDate().before(dtt2.toDate())){
										data_allow = false;
									}
								}
					

								if(ac.getContrato().getTipo_contrato().equals("PRE-PAGO")){									
									Notify.Show("Cliente Possui Contrato PRE-PAGO!", Notify.TYPE_WARNING);									
								}else if(!data_allow){
									Notify.Show("Não é possivel realizar mais de 1 renovação em menos de 1 mês!", Notify.TYPE_ERROR);		
								}else if(crDAO.getBoletosAcessoVencidosPorContrato(ac.getId()) == true){
									Notify.Show("Contrato Possui Boletos Vencidos!", Notify.TYPE_WARNING);		
								}else if(months > 1 ){//|| days <= -1									
									Notify.Show("Contrato Fora do Período de Renovação", Notify.TYPE_WARNING);
								}else{
									janelaAtiva = true;
									final RenovarContratoAcessoEditor rpae = new RenovarContratoAcessoEditor(item, "Renovar Contrato", true);
																				
									rpae.addListerner(new RenovarContratoAcessoEditor.RenovarContratoListerner() {
										
										@Override
										public void onClose(RenovarContratoEvent event) {
											if(event.isConfirm()){								
													
													EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item; 
													boolean check  = AcessoDAO.renovarContratoAcesso(entityItem.getEntity());
													
													if(check){
														rpae.close();
														Notify.Show("Contrato renovado com sucesso", Notify.TYPE_SUCCESS);
														refresh();														
													}
													
											}else{
												rpae.close();
												Notify.Show("renovação cancelado.", Notify.TYPE_NOTICE);
											}
												
										}
									});
									
									rpae.addCloseListener(new Window.CloseListener() {
										
										@Override
										public void windowClose(CloseEvent e) {
											tb.focus();
											janelaAtiva = false;
										}
									});
									
									getUI().addWindow(rpae);
								}			
						}				
				}else{					
					Notify.Show("Você não Possui Permissão para Renovar Contratos", Notify.TYPE_ERROR);
				}
			}		
		});     
        bt1Renovar.setPrimaryStyleName("btSubMenu");
        
        Button btNovoCadastro = new Button("Novo Cadastro", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar Novo Contrato"))				
				{				
					
						janelaAtiva = true;
				
						final BeanItem<AcessoCliente> bItemNew = new BeanItem<AcessoCliente>(new AcessoCliente());
						final NovoCadastroAcessoEditor newAcessoEditor = new NovoCadastroAcessoEditor(bItemNew, "Novo Cadastro de Acesso", true);
						newAcessoEditor.addListerner(new NovoCadastroAcessoEditor.NovoCadastroListerner() {
							
							@Override
							public void onClose(NovoCadastroEvent event) {
								if(event.isConfirm()){								
										boolean check = AcessoDAO.novoCadastro(bItemNew.getBean());
										
										if(check){
											newAcessoEditor.close();
											Notify.Show("Contrato de acesso cadastrado com sucesso!", Notify.TYPE_SUCCESS);
											refresh();
										}
								}else{
									newAcessoEditor.close();
									Notify.Show("cadastro cancelado.", Notify.TYPE_NOTICE);
								}
							}
						});
						
						newAcessoEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus(); 
								janelaAtiva = false;
							}
						});
						
						getUI().addWindow(newAcessoEditor);
				
				
				}else{					
					Notify.Show("Você não Possui Permissão para Cadastrar um Novo Contrato", Notify.TYPE_ERROR);
				}
			}
		});
        btNovoCadastro.setPrimaryStyleName("btSubMenu");
        
        Button btComprarCreditos = new Button("Comprar Creditos", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				janelaAtiva = true;
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Comprar Creditos"))				
				{
				
					if(tb.getValue() != null){
	
						Object value = tb.getValue();
						btAtualizar.click();
						item = tb.getItem(value);	
						
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item; 
						final AcessoCliente ac = entityItem.getEntity();
							
						if(ContasReceberDAO.verificarBoletoPendentePrePago(ac.getId())){
							Notify.Show("Contrato já possui Créditos pendente pagamento!", Notify.TYPE_WARNING);	
						}else if(!ac.getContrato().getTipo_contrato().equals("PRE-PAGO")){						
							Notify.Show("Cliente não Possui Contrato PRE-PAGO!", Notify.TYPE_WARNING);
						}else{	
							
							final ComprarCreditoEditor ccedt = new ComprarCreditoEditor(item, "Comprar Créditos", true);
							ccedt.addListerner(new ComprarCreditosAcessoListerner() {
								
								@Override
								public void onClose(ComprarCreditosAcessoEvent event) {
									if(event.isConfirm()){
										
										boolean check = AcessoDAO.comprarCreditos(ac, event.getPlanoNovo());
										
										if(check){
											ccedt.close();
											refresh();
											Notify.Show("Compra de crédito realizada com sucesso!", Notify.TYPE_SUCCESS);
										}
									}else{
										ccedt.close();
										Notify.Show("compra de créditos cancelada.", Notify.TYPE_NOTICE);
									}
										
								}
							});
							
							ccedt.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									tb.focus();			
									janelaAtiva = false;
								}
							});
							
							getUI().addWindow(ccedt);
						}			
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Comprar Créditos Pré-Pago", Notify.TYPE_ERROR);
				}
				
			}
		});
        btComprarCreditos.setPrimaryStyleName("btSubMenu");
        btComprarCreditos.setEnabled(true);
        
        Button btPreContrato = new Button("PreContrato", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				janelaAtiva = true;
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "PreContrato"))				
				{
				
					if(tb.getValue() != null){	
						Item item = tb.getItem(tb.getValue());
						PreContratoView preView = new PreContratoView(item, "Pré-Contrato", true);
						getUI().addWindow(preView); 
					}
				
				}else{
					Notify.Show("Você não possui permissão para gerar um Pré-Contrato", Notify.TYPE_ERROR);
				}
				
			}
		});
        btPreContrato.setPrimaryStyleName("btSubMenu");
        
        
        l.addComponent(btNovoCadastro);
        l.addComponent(bt1Renovar);
        l.addComponent(btComprarCreditos);
        //l.addComponent(btPreContrato);
       
    }
	
	private void closeAllWindows(){
		if(winSubMenuHistorico != null){ winSubMenuHistorico.close();}
		if(winSubMenuMudanca != null){winSubMenuMudanca.close();}
		if(winSubMenuNovo != null){winSubMenuNovo.close();}
		if(winSubMenuBloqueioDesbloqueio != null){winSubMenuBloqueioDesbloqueio.close();}
		if(winSubMenuFuncoesAdministrativas != null){winSubMenuFuncoesAdministrativas.close();}
	}
	
	Window winSubMenuMudanca;
	Window winSubMenuBloqueioDesbloqueio;
	
	
	private void buildSubMenuMudanca(ClickEvent event) {
		winSubMenuMudanca = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuMudanca.setContent(l);
        winSubMenuMudanca.setWidth("300px");
        winSubMenuMudanca.addStyleName("notifications");
        winSubMenuMudanca.setClosable(false);
        winSubMenuMudanca.setResizable(false);
        winSubMenuMudanca.setDraggable(false);
        winSubMenuMudanca.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuMudanca.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuMudanca.setCloseShortcut(KeyCode.ESCAPE, null);

        Button btReativarContrato = new Button("Reativar Contrato", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Reativar")){
					
						if(tb.getValue() != null){
							
									Object value = tb.getValue();
									btAtualizar.click();
									item = tb.getItem(value);																	
									EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>) item; 
									
									AcessoCliente ac = entityItem.getEntity();								 
									boolean check  = AcessoDAO.reativarAcesso(ac);
									
									if(check){										
										refresh();
										Notify.Show("Contrato Reativado com sucesso!", Notify.TYPE_SUCCESS);													
									}							
						}
					
				}
			}			
		});
        btReativarContrato.setPrimaryStyleName("btSubMenu");
        
        Button btSuspenderContrato = new Button("Suspender Contrato", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
					closeAllWindows();			
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Suspender"))				
					{
							if(tb.getValue() != null){
									
									Object value = tb.getValue();
									btAtualizar.click();
									item = tb.getItem(value);																	
									EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>) item; 
									
									final AcessoCliente ac = entityItem.getEntity();
																			
									janelaAtiva = true;
									final SuspenderContratoEditor rpae = new SuspenderContratoEditor(item, "Suspender Contrato", true);
									rpae.addListerner(new SuspenderContratoEditor.SuspenderContratoListerner() {
										
										@Override
										public void onClose(SuspenderContratoEvent event) {
											if(event.isConfirm()){
																									
												boolean check  = AcessoDAO.suspenderContrato(ac, event.getDataFimSuspensao());
												
												if(check){
													rpae.close();
													refresh();
													Notify.Show("Contrato Suspenso com sucesso!", Notify.TYPE_SUCCESS);													
												}
											}else{
												rpae.close();
												Notify.Show("alteraçao de plano cancelada", Notify.TYPE_NOTICE);				
											}
												
										}
									});
									
									rpae.addCloseListener(new Window.CloseListener() {
										
										@Override
										public void windowClose(CloseEvent e) {
											tb.focus();
											janelaAtiva = false;
										}
									});
									
									getUI().addWindow(rpae);
							}			
					}else{					
						Notify.Show("Você não Possui Permissão para Suspender Contratos", Notify.TYPE_ERROR);
					}	
				
			}		
		});       
        btSuspenderContrato.setPrimaryStyleName("btSubMenu");
                
        Button btAlterarPlano = new Button("Alterar Plano", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			closeAllWindows();			
			if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Plano"))				
			{
						if(tb.getValue() != null){
													
								EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>) tb.getItem(tb.getValue()); 
								
								final AcessoCliente contrato_selecionado = entityItem.getEntity();
																		
								List<ContasReceber> boletosAbertos = ContasReceberDAO.procurarAbertoBoletosDoAcessoPorContrato(contrato_selecionado.getId());
								
								boolean data_allow = true;
								if(contrato_selecionado.getData_alteracao_plano() != null){
									DateTime dt1 = new DateTime();
									DateTime dt2 = new DateTime(contrato_selecionado.getData_alteracao_plano()).plusMonths(1).plusDays(1);
									
									if(dt1.toDate().before(dt2.toDate())){
										data_allow = false;
									}
								}
								
								//-- Condição de Instalação Grátis retirado porque já não mais se aplica.
//								else if(ac.getInstalacao_gratis() != null && ac.getInstalacao_gratis().equals("SIM")){
//									Notify.Show("Cliente Possui Instalação Grátis", Notify.TYPE_ERROR);
//								}
								
								//Retirado experimentalmente o bloqueio de alteração de 
								
								
								final NfeMestre nfe = AcessoDAO.verificaBoletosNfeAllow(contrato_selecionado.getId());
								
								if(boletosAbertos == null && !contrato_selecionado.getContrato().getTipo_contrato().equals("GRATIS")){
							         Notify.Show("Cliente Não Possui Boletos!", Notify.TYPE_ERROR);							         
								}else if(!data_allow){
									Notify.Show("Não é possivel realizar mais de 1 alteração de plano em menos de 1 mês!", Notify.TYPE_ERROR);									
								}else if(ContasReceberDAO.getBoletosAcessoVencidosPorContrato(contrato_selecionado.getId()) == true ){
									 Notify.Show("Contrato Possui Boletos Vencidos!", Notify.TYPE_ERROR);									
								}else if(contrato_selecionado.getContrato().getTipo_contrato().equals("PRE-PAGO")){									 
									 Notify.Show("Cliente Possui Contrato PRE-PAGO!", Notify.TYPE_ERROR);
								}else if(ContasReceberDAO.getBoletosAcessoAdiantadosPorContrato(contrato_selecionado.getId()) == true ){									
									 Notify.Show("Contrato Possui Boletos Adiantados!", Notify.TYPE_ERROR);									
								}else{
									janelaAtiva = true;
									final MudancaPlanoEditor rpae = new MudancaPlanoEditor(entityItem, "Alterar Plano Acesso", true);
									rpae.addListerner(new MudancaPlanoEditor.RenovarPlanoAcessoListerner() {
										
										@Override
										public void onClose(RenovarPlanoAcessoEvent event) {
											if(event.isConfirm()){
																									
													boolean check  = AcessoDAO.alteraPlano(contrato_selecionado,event.getPlanoNovo(), nfe, event.getInstGratis());
													
													if(check){
														rpae.close();
														
														//Abrir Boletos para Imprimir
													    //List<ContasReceber> boletos = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(Integer.parseInt(item.getItemProperty("id").getValue().toString()));														
														
														BoletosGeradosImpressaoUtil boletosUtil = new BoletosGeradosImpressaoUtil(true,true, contrato_selecionado.getId()); 
														getUI().addWindow(boletosUtil);
														
														refresh();
														Notify.Show("Plano alterado com sucesso", Notify.TYPE_SUCCESS);													
														Notification.show("Necessário Imprimir Contrato!",Type.ERROR_MESSAGE);
													}
											}else{
												rpae.close();
												Notify.Show("alteraçao de plano cancelada", Notify.TYPE_NOTICE);				
											}
												
										}
									});
									
									rpae.addCloseListener(new Window.CloseListener() {
										
										@Override
										public void windowClose(CloseEvent e) {
											tb.focus();
											janelaAtiva = false;
										}
									});
									
									getUI().addWindow(rpae);
								}			
						}	
				}else{					
					Notify.Show("Você não Possui Permissão para Renovar Contratos", Notify.TYPE_ERROR);
				}
			}		
		});       
        btAlterarPlano.setPrimaryStyleName("btSubMenu");
        
        Button btAlterarCredenciais = new Button("Alterar Credenciais", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Credenciais"))				
				{
					
					if(tb.getValue() != null){
						Object value = tb.getValue();
						btAtualizar.click();
						item = tb.getItem(value);	
						
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item; 
						final AcessoCliente ac = entityItem.getEntity();						
						final BeanItem<AcessoCliente> beanItem = new BeanItem<AcessoCliente>(ac);

						if(ac.getStatus_2().equals("PENDENTE_INSTALACAO")){							
							Notify.Show("Cliente Possui Instalação Pendente!", Notify.TYPE_NOTICE);	
						}else{
							janelaAtiva = true;
							final AlterarCredenciaisAcessoView acaEditor = new AlterarCredenciaisAcessoView(beanItem, "Alterar Credenciais", true);
							
							acaEditor.addListerner(new AlterarCredenciaisAcessoView.AlterarCredenciaisAcessoListerner() {
								
								@Override
								public void onClose(AlterarCredenciaisAcessoEvent event) {
									if(event.isConfirm()){
										
										boolean check = AcessoDAO.alterarCredenciais(beanItem.getBean(), item.getItemProperty("login").getValue().toString());
										
										if(check){										
											acaEditor.close();
											refresh();
											Notify.Show("Credenciais alteradas com sucesso", Notify.TYPE_SUCCESS);							
										}
									}else{
										acaEditor.close();
										Notify.Show("alteraçao de credencial cancelada", Notify.TYPE_NOTICE);			
									}
								}
							});
							
							acaEditor.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									tb.focus();
									janelaAtiva = false;
								}
							});
							
							getUI().addWindow(acaEditor);
					
						}
					}
				
				
				}else{
					Notify.Show("Você não Possui Permissão para Editar Credenciais de Acesso", Notify.TYPE_ERROR);
				}
			}
		});
        btAlterarCredenciais.setPrimaryStyleName("btSubMenu");
        
        Button btAltConcentrador = new Button("Alterar Concentrador", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Concentrador"))				
				{
					
					if(tb.getValue() != null){
						
						Object value = tb.getValue();
						btAtualizar.click();
						item = tb.getItem(value);	
						
						EntityItem<AcessoCliente> entityItem =(EntityItem<AcessoCliente>)item;					
						final AcessoCliente ac = ContratosAcessoDAO.find(entityItem.getEntity().getId());
						final BeanItem<AcessoCliente> beanItem = new BeanItem<AcessoCliente>(entityItem.getEntity());

						if(ac.getStatus_2().equals("PENDENTE_INSTALACAO")){							
							Notify.Show("Cliente Possui Instalação Pendente!", Notify.TYPE_ERROR);	
						}else if(ac.getEndereco_ip() != null && !ac.getEndereco_ip().equals("") && !ac.getEndereco_ip().isEmpty()){
							Notify.Show("Não é possivel alterar o concentrador antes de remover o ip fixo!", Notify.TYPE_ERROR);
						}else{
							janelaAtiva = true;
							final AlterarConcentradorAcessoView acaEditor = new AlterarConcentradorAcessoView(beanItem, "Alterar Concentrador", true);
														
							acaEditor.addListerner(new AlterarConcentradorAcessoView.AlterarConcentradorAcessoListerner() {
								
								@Override
								public void onClose(AlterarConcentradorAcessoEvent event) {
									if(event.isConfirm()){
										boolean check = AcessoDAO.alterarConcentrador(beanItem.getBean(), ac);				
										
										if(check){
											acaEditor.close();
											refresh();
											Notify.Show("Concentrador alterado com sucesso", Notify.TYPE_SUCCESS);					
										}
									}else{
										acaEditor.close();
										Notify.Show("alteraçao de concentrador cancelada", Notify.TYPE_NOTICE);		
									}
								}
							});
							
							acaEditor.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									tb.focus();
									janelaAtiva = false;
								}
							});
							
							getUI().addWindow(acaEditor);
					
						}
					}
				
				
				}else{
					Notify.Show("Você não Possui Permissão para Editar Concentrador de Acesso", Notify.TYPE_ERROR);
				}
			}
		});
        btAltConcentrador.setPrimaryStyleName("btSubMenu");
        
        Button btAlterarTitularidade = new Button("Alterar Titularidade", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Titularidade"))				
				{
					if(tb.getValue() != null){
						
							Object value = tb.getValue();
							btAtualizar.click();
							item = tb.getItem(value);	
						
							EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item; 
							final AcessoCliente ac = entityItem.getEntity();		
							final Cliente clienteAntigo = ac.getCliente(); 
							BeanItem<AcessoCliente> beanItem = new BeanItem<AcessoCliente>(ac);
												
							if(ac.getContrato().getTipo_contrato().equals("GRATIS")){
								Notify.Show("Cliente Possui Contrato Grátis!", Notify.TYPE_WARNING);					
							}else if(ContasReceberDAO.getBoletosAcessoVencidosPorContrato(ac.getId()) == true){						
								Notify.Show("O Contrato Possui Boletos de Acesso Vencidos!", Notify.TYPE_WARNING);
							}else{	
								janelaAtiva = true;
								final MudarTitularidadeEditor mudarTituEditor = new MudarTitularidadeEditor(beanItem, "Alterar Titularidade", true);
								mudarTituEditor.addListerner(new MudarTitularidadeEditor.MudarTitularidadeListerner() {
								
								@Override
								public void onClose(MudarTitularidadeEvent event) {
										if(event.isConfirm()){
											
												boolean check = AcessoDAO.alterarTitularidade(ac,clienteAntigo);
												if(check){
													mudarTituEditor.close();
													refresh();
													Notify.Show("Titular alterada com sucesso", Notify.TYPE_SUCCESS);
												}
										}else{
											mudarTituEditor.close();
											Notify.Show("Titular alterada com sucesso", Notify.TYPE_SUCCESS);
										}
									}
								});	
								
								mudarTituEditor.addCloseListener(new Window.CloseListener() {
									
									@Override
									public void windowClose(CloseEvent e) {
										janelaAtiva = false;
									}
								});
								
								getUI().addWindow(mudarTituEditor);
						  }
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Mudar Titularidade de um Contrato", Notify.TYPE_ERROR);
				}
				
			}
		});
        btAlterarTitularidade.setPrimaryStyleName("btSubMenu");
        
        Button btAlterarVencimento = new Button("Alterar Vencimento", new Button.ClickListener() {
        	
			
			@Override
			public void buttonClick(ClickEvent event) {
				
						closeAllWindows();				
						if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(),	OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Vencimento") && tb.getValue() != null)				
						{
							if(tb.getValue() != null){
								
								Object value = tb.getValue();
								btAtualizar.click();
								item = tb.getItem(value);	
								
								EntityManager em = ConnUtil.getEntity();								
								ContasReceberDAO crDAO = new ContasReceberDAO();								
								CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
								
								final Integer codAcesso = Integer.parseInt(item.getItemProperty("id").toString());	
								PlanoAcesso planoCliente =  (PlanoAcesso) item.getItemProperty("plano").getValue();
								
								boolean allowAdiantados = caDAO.allowRenovacaoComBoletosAdiantados(codAcesso,planoCliente.getId());
								boolean check = crDAO.buscarTitulosAdiantadosDeAcessoPorContrato(codAcesso);
								final AcessoCliente ac = em.find(AcessoCliente.class, codAcesso);
			
								List<ContasReceber> contasreceber = crDAO.procurarTodosBoletosDoAcessoPorContrato(ac.getId());
								
								if(contasreceber == null && !ac.getContrato().getTipo_contrato().equals("GRATIS")){
							        Notify.Show("Cliente Não Possui Boletos!", Notify.TYPE_ERROR);						         
								}else if(ac.getContrato().getTipo_contrato().equals("GRATIS")){
							        Notify.Show("Cliente Possui Contrato Grátis!", Notify.TYPE_ERROR);
								}else if(ac.getContrato().getTipo_contrato().equals("PRE-PAGO")){
									Notify.Show("Cliente Possui Contrato PRE-PAGO!", Notify.TYPE_ERROR);
								}else if(crDAO.getBoletosAcessoVencidosPorContrato(codAcesso) || allowAdiantados == false || check == true){								
									Notify.Show("O Cliente Possui Boletos de Acesso Vencidos ou Adiantados!", Notify.TYPE_ERROR);		
								}else{											
									janelaAtiva = true;
									final MudancaVencimentoEditor mVenc = new MudancaVencimentoEditor(item,codAcesso,"Alterar Vencimento", true);					
									mVenc.addListerner(new MudancaVencimentoEditor.MudancaVencimentoListerner() {
										
										@Override
										public void onClose(MudancaVencimentoEvent event) {
											
											if(event.isConfirm()){
												
												boolean check = AcessoDAO.alterarVencimento(ac,event.getNovaData());
												if(check){
													mVenc.close();
													refresh();
													Notify.Show("Vencimento alterado com sucesso!", Notify.TYPE_SUCCESS);
												}
											}else{
												mVenc.close();
												Notify.Show("alteração de vencimento cancelada", Notify.TYPE_NOTICE);												
											}											
										}
									});
									
									mVenc.addCloseListener(new Window.CloseListener() {
										
										@Override
										public void windowClose(CloseEvent e) {
											janelaAtiva = false;
										}
									});
									
									getUI().addWindow(mVenc);
							}
						}else{
							Notify.Show("Você não Possui Permissão para Mudar Vencimento de um Contrato", Notify.TYPE_ERROR);
						}
					 }
					}
				});
        btAlterarVencimento.setPrimaryStyleName("btSubMenu");

        Button btAlterarMaterial = new Button("Alterar Material", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(),OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Material"))				
				{
				
					if(tb.getValue() != null){
											
							EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)tb.getItem(tb.getValue());
							
							final AcessoCliente ac = entityItem.getEntity();							
							String macAntigo = ac.getEndereco_mac();
							Produto materialAntigo = ac.getMaterial();
							
							if(ac.getRegime().equals("PROPRIO (PARCIAL)")){								
								macAntigo = ac.getOnu_serial();
								materialAntigo = ac.getOnu();
							}
							
							final String serial = macAntigo;
							final Produto material = materialAntigo;
							
							final BeanItem<AcessoCliente> beanItem = new BeanItem<AcessoCliente>(ac);
		
							if(ac.getStatus_2().equals("PENDENTE_INSTALACAO")){								
								Notify.Show("Cliente Possui Instalação Pendente!", Notify.TYPE_ERROR);		
							}else{					
							
								janelaAtiva = true;
								
								if(beanItem.getBean().getRegime().equals("COMODATO (TOTAL)")){
									
									final String macRoteadorAntigo = ac.getEndereco_mac();
									final Produto roteadorAntigo = ac.getMaterial();
									
									final String serialOnuAntigo = ac.getOnu_serial();
									final Produto onuAntigo = ac.getOnu();
									
									
									final EditarMaterialEditorComodatoTotal acaEditor = new EditarMaterialEditorComodatoTotal(beanItem, "Alterar Material", true);								
									acaEditor.addListerner(new EditarMaterialEditorComodatoTotal.EditarMaterialComodatoListerner() {
										
										@Override
										public void onClose(EditarMaterialComodatoEvent event) {
											if(event.isConfirm()){
														
												boolean check;			
												check = AcessoDAO.alterarMaterialComodatoTotal(
														beanItem.getBean(), 
														event.getMotivo(),
														event.getCodMacRoteador(),
														event.getCodSerialOnu(),
														macRoteadorAntigo,
														event.getCodMacRoteador() != null ? roteadorAntigo : null,
														serialOnuAntigo,														
														event.getCodSerialOnu() != null ? onuAntigo : null);							
											
												if(check){
													acaEditor.close();
													refresh();
													Notify.Show("Material alterado com sucesso", Notify.TYPE_SUCCESS);
												}
											}else{
												acaEditor.close();
												Notify.Show("alteraçao de material cancelada", Notify.TYPE_NOTICE);		
											}
										}
									});					
									
									acaEditor.addCloseListener(new Window.CloseListener() {
										
										@Override
										public void windowClose(CloseEvent e) {
											tb.focus();
											janelaAtiva = false;
										}
									});
									
									getUI().addWindow(acaEditor);
								}else{
									
									final String serialOnuAntigo = ac.getOnu_serial();
									
									final EditarMaterialEditor acaEditor = new EditarMaterialEditor(beanItem, "Alterar Material", true);								
									acaEditor.addListerner(new EditarMaterialEditor.EditarProdutoListerner() {
										
										@Override
										public void onClose(EditarProdutoEvent event) {
											if(event.isConfirm()){
														
												boolean check;
																	
												check = AcessoDAO.alterarMaterial(beanItem.getBean(), event.getMotivo(),event.getCodSerial(),serial, material, serialOnuAntigo);												
												
												if(check){
													acaEditor.close();
													refresh();
													Notify.Show("Material alterado com sucesso", Notify.TYPE_SUCCESS);
												}
											}else{
												acaEditor.close();
												Notify.Show("alteraçao de material cancelada", Notify.TYPE_NOTICE);		
											}
										}
									});					
									
									acaEditor.addCloseListener(new Window.CloseListener() {
										
										@Override
										public void windowClose(CloseEvent e) {
											tb.focus();
											janelaAtiva = false;
										}
									});
									
									getUI().addWindow(acaEditor);
								}
								
								
							}
					}
				}else{					
					Notify.Show("Você não Possui Permissão para Editar o Material do Contrato", Notify.TYPE_ERROR);
				}
			}
		});
        btAlterarMaterial.setPrimaryStyleName("btSubMenu");
        
        Button btRemoverMaterial = new Button("Remover Material", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(),OpusERP4UI.getUsuarioLogadoUI().getId(), "Remover Material"))				
				{
				
					EntityManager em = ConnUtil.getEntity();
					boolean allow = tb.getValue() != null ? true : false;
					
					if(allow){
						
							Object value = tb.getValue();
							btAtualizar.click();
							item = tb.getItem(value);	
						
							Integer codAcesso = Integer.parseInt(item.getItemProperty("id").getValue().toString());
							final AcessoCliente ac = em.find(AcessoCliente.class, codAcesso);
		
							if(ac.getStatus_2().equals("PENDENTE_INSTALACAO")){						
								Notify.Show("Cliente Possui Instalação Pendente!", Notify.TYPE_WARNING);
							}else if(!ac.getRegime().equals("COMODATO")){
								Notify.Show("O regime do contrato precisa ser Comodato!", Notify.TYPE_WARNING);
							}else if(ac.getMaterial() == null){
								Notify.Show("O contrato não possui material vinculado!!", Notify.TYPE_WARNING);
							}else{						
							
										janelaAtiva = true;
																										
										final RemoverMaterial removerMaterialEditor = new RemoverMaterial(item, "Remover Material", true);								
										
										removerMaterialEditor.addListerner(new RemoverMaterial.RemoverMaterialListerner() {
											
											@Override
											public void onClose(RemoverMaterialEvent event) {
												if(event.isConfirm()){
															
													boolean check  = AcessoDAO.removerMaterial(ac);
													if(check){
														removerMaterialEditor.close();
														refresh();
														Notify.Show("Material removido com sucesso!", Notify.TYPE_SUCCESS);													
													}
												}else{
													removerMaterialEditor.close();
													Notify.Show("remoção cancelada", Notify.TYPE_NOTICE);
												}
											}
										});		
										
										
										
										removerMaterialEditor.addCloseListener(new Window.CloseListener() {
											
											@Override
											public void windowClose(CloseEvent e) {
												janelaAtiva = false;
											}
										});
										
										getUI().addWindow(removerMaterialEditor);
								
								
							}
					}
				}else{					
					Notify.Show("Você não Possui Permissão para remover o Material do Contrato", Notify.TYPE_ERROR);
				}
			}
		});
        btRemoverMaterial.setPrimaryStyleName("btSubMenu");
        
        Button btAlterarEndereco = new Button("Alterar Endereço", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
			
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Endereco"))				
				{
					if(tb.getValue() != null){
						Object value = tb.getValue();
						btAtualizar.click();
						item = tb.getItem(value);	
						
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item;
						final AcessoCliente ac = entityItem.getEntity();						
						final BeanItem<AcessoCliente> beanItem = new BeanItem<AcessoCliente>(ac);
												
						if(ContasReceberDAO.getBoletosAcessoVencidosPorContrato(ac.getId()) == true){						
							Notify.Show("O Contrato Possui Boletos de Acesso Vencidos!", Notify.TYPE_ERROR);
						}else{
								janelaAtiva = true;
								final AlterarEnderecoEditor alterarEndEditor = new AlterarEnderecoEditor(beanItem, "Alterar Endereço", true);
								alterarEndEditor.addListerner(new AlterarEnderecoEditor.AlterarEnderecoListerner() {
									
									@Override
									public void onClose(AlterarEnderecoEvent event) {
										if(event.isConfirm()){
											
											boolean check = AcessoDAO.alterarEndereco(beanItem.getBean());
											
											if(check){
												alterarEndEditor.close();
												refresh();
												Notify.Show("Endereço do Acesso Alterado com Sucesso!", Notify.TYPE_SUCCESS);
											}
										}else{
											alterarEndEditor.close();
											Notify.Show("alteração de endereço cancelado", Notify.TYPE_NOTICE);
										}
									}
								});
								
								alterarEndEditor.addCloseListener(new Window.CloseListener() {
									
									@Override
									public void windowClose(CloseEvent e) {
										janelaAtiva = false;
									}
								});
								getUI().addWindow(alterarEndEditor);
						}
						
					}				
				}else{					
					Notify.Show("Você não Possui Permissão para Alterar Endereços", Notify.TYPE_ERROR);
				}
			}		
		});
        btAlterarEndereco.setPrimaryStyleName("btSubMenu");
        
        Button btAlterarRegimeProprio = new Button("Alterar Regime para PROPRIO", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Regime Proprio"))				
				{
				
					if(tb.getValue() != null){
						
						Object value = tb.getValue();
						btAtualizar.click();
						item = tb.getItem(value);	
						
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item; 
						final AcessoCliente ac = entityItem.getEntity();
					
						
						final String regimeAntigo = ac.getRegime();
						final Produto materialAntigo = ac.getMaterial();
						final String macAntigo = ac.getEndereco_mac();
						final Produto onuAntigo = ac.getOnu();
						final String onuAntigoSerial = ac.getOnu_serial();

						if(ac.getStatus_2().equals("PENDENTE_INSTALACAO")){						
							Notify.Show("Cliente Possui Instalação Pendente!", Notify.TYPE_ERROR);
						}else{
									
								if(!regimeAntigo.equals("COMODATO (TOTAL)")){
							
								janelaAtiva = true;
								
								final EditarRegimeProprioEditor acaEditor = new EditarRegimeProprioEditor(item, "Alterar Regime para Proprio", true);
															
								acaEditor.addListerner(new EditarRegimeProprioEditor.EditarRegimeListerner() {
									
									@Override
									public void onClose(EditarRegimeEvent event) {
										if(event.isConfirm()){
											
											boolean check = false;
											if(regimeAntigo.equals("PROPRIO")){												
												check = AcessoDAO.alterarRegimeProprio(ac,null,"",null,"");
											}
											
											if(regimeAntigo.equals("PROPRIO (PARCIAL)")){												
												check = AcessoDAO.alterarRegimeProprio(ac,null,"",onuAntigo,onuAntigoSerial);
											}
											
											if(regimeAntigo.equals("COMODATO")){
												check = AcessoDAO.alterarRegimeProprio(ac,materialAntigo,macAntigo,null,"");
											}
											
											if(check){
												acaEditor.close();
												refresh();
												Notify.Show("Regime alterado com sucesso", Notify.TYPE_SUCCESS);
											}
										}else{
											acaEditor.close();
											Notify.Show("alteração de regime cancelado", Notify.TYPE_NOTICE);
										}
									}							
								});									
								
								
								acaEditor.addCloseListener(new Window.CloseListener() {
									
									@Override
									public void windowClose(CloseEvent e) {
										janelaAtiva =	false;
										tb.focus();
									}
								});
								
								getUI().addWindow(acaEditor);
							}
						}
					}
				}else{
					Notify.Show("Você não Possui Permissão para Editar o Regime do Contrato", Notify.TYPE_ERROR);
				}
			}
		});
        btAlterarRegimeProprio.setPrimaryStyleName("btSubMenu");
        
        Button btAlterarRegimeComodato = new Button("Alterar Regime para COMODATO", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Regime Comodato"))				
				{
									
					if(tb.getValue() != null){
						
						Object value = tb.getValue();
						btAtualizar.click();
						item = tb.getItem(value);
						
						
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item; 
						final AcessoCliente ac = entityItem.getEntity();
						final Produto materialAntigo = ac.getMaterial();
						final String macAntigo = ac.getEndereco_mac();

						if(ac.getCliente().getCredito_cliente() == null){
							ac.getCliente().setCredito_cliente("NAO INFORMADO");
						}

						if(ac.getStatus_2().equals("PENDENTE_INSTALACAO")){						
							Notify.Show("Cliente Possui Instalação Pendente!", Notify.TYPE_ERROR);
						}else if(!ac.getRegime().equals("PROPRIO")){
							Notify.Show("O regime do contrato precisa ser Próprio!", Notify.TYPE_ERROR);
						}else if(ac.getCliente().getCredito_cliente().equals("INADIMPLENTE")){
							Notify.Show("Crédito não Aprovado!", Notify.TYPE_ERROR);
						}else{
							
								janelaAtiva = true;
								
								final EditarRegimeComodatoEditor acaEditor = new EditarRegimeComodatoEditor(item, "Alterar Regime para Comodato", true);
															
								acaEditor.addListerner(new EditarRegimeComodatoEditor.EditarRegimeListerner() {
									
									@Override
									public void onClose(com.digital.opuserp.view.modulos.acesso.contratos_acesso.EditarRegimeComodatoEditor.EditarRegimeEvent event) {
										if(event.isConfirm()){
											boolean check = AcessoDAO.alterarRegimeComodato(ac,materialAntigo,macAntigo);
											
											if(check){
												acaEditor.close();
												refresh();
												Notify.Show("Regime alterado com sucesso", Notify.TYPE_SUCCESS);
											}
										}else{
											acaEditor.close();
											Notify.Show("alteração de regime cancelado", Notify.TYPE_NOTICE);
										}
									}							
								});									
								
								
								acaEditor.addCloseListener(new Window.CloseListener() {
									
									@Override
									public void windowClose(CloseEvent e) {
										janelaAtiva =	false;
										tb.focus();
									}
								});
								
								getUI().addWindow(acaEditor);
							
						}
					}
				}else{
					Notify.Show("Você não Possui Permissão para Editar o Regime do Contrato", Notify.TYPE_ERROR);
				}
			}
		});
        btAlterarRegimeComodato.setPrimaryStyleName("btSubMenu");
               
        Button btAlterarRegimeComodatoTotal = new Button("Alterar Regime para COMODATO (TOTAL)", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(),OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Regime Comodato (Total)"))				
				{									
					if(tb.getValue() != null){
						
						Object value = tb.getValue();
						btAtualizar.click();
						item = tb.getItem(value);
						
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item; 
						final AcessoCliente ac = entityItem.getEntity();
						final Produto materialAntigo = ac.getMaterial();
						final String macAntigo = ac.getEndereco_mac();

						if(ac.getCliente().getCredito_cliente() == null){
							ac.getCliente().setCredito_cliente("NAO INFORMADO");
						}
						
						//-- Condição de Fidelidade Retirado
//						else if(temFidelidade(ac)){ 
//							Notify.Show("Contrato ainda Tem Fidelidade!", Notify.TYPE_ERROR);
//						}
						
						if(ac.getStatus_2().equals("PENDENTE_INSTALACAO")){						
							Notify.Show("Cliente Possui Instalação Pendente!", Notify.TYPE_ERROR);
						}else if(ac.getCliente().getCredito_cliente().equals("INADIMPLENTE")){
							Notify.Show("Crédito não Aprovado!", Notify.TYPE_ERROR);
						}else if(ac.getPlano().getPermitir_comodato_total() == null || !ac.getPlano().getPermitir_comodato_total().equals("SIM")){
							Notify.Show("O Plano não Permite esta Alteração!", Notify.TYPE_ERROR);
						}else{
							
								if(ac.getRegime().equals("PROPRIO")){
										janelaAtiva = true;
										
										final EditarRegimeProprioParaComodatoTotalEditor acaEditor = new EditarRegimeProprioParaComodatoTotalEditor(item, "Alterar Regime para COMODATO (TOTAL)", true);															
										acaEditor.addListerner(new EditarRegimeProprioParaComodatoTotalEditor.EditarRegimeProprioParaComodatoTotalEditorListerner() {
											
											@Override
											public void onClose(EditarRegimeProprioParaComodatoTotalEditor.EditarRegimeProprioParaComodatoTotalEditorEvent event) {
												if(event.isConfirm()){
													boolean check = AcessoDAO.alterarRegimeProprioParaComodatoTotal(ac,event.getCodSerialRoteador(),event.getCodSerialOnu());
													
													if(check){
														acaEditor.close();
														refresh();
														Notify.Show("Regime alterado com sucesso", Notify.TYPE_SUCCESS);
													}
												}else{
													acaEditor.close();
													Notify.Show("alteração de regime cancelado", Notify.TYPE_NOTICE);
												}
											}							
										});									
										
										
										acaEditor.addCloseListener(new Window.CloseListener() {
											
											@Override
											public void windowClose(CloseEvent e) {
												janelaAtiva =	false;
												tb.focus();
											}
										});
										
										getUI().addWindow(acaEditor);
								}else if(ac.getRegime().equals("COMODATO")){
									janelaAtiva = true;
									
									final EditarRegimeComodatoParaComodatoTotalEditor acaEditor = new EditarRegimeComodatoParaComodatoTotalEditor(item, "Alterar Regime para COMODATO (TOTAL)", true);															
									acaEditor.addListerner(new EditarRegimeComodatoParaComodatoTotalEditor.EditarRegimeComodatoParaComodatoTotalEditorListerner() {
										
										@Override
										public void onClose(EditarRegimeComodatoParaComodatoTotalEditor.EditarRegimeComodatoParaComodatoTotalEditorEvent event) {
											if(event.isConfirm()){
												boolean check = AcessoDAO.alterarRegimeComodatoParaComodatoTotal(ac,event.getCodSerialOnu());
												
												if(check){
													acaEditor.close();
													refresh();
													Notify.Show("Regime alterado com sucesso", Notify.TYPE_SUCCESS);
												}
											}else{
												acaEditor.close();
												Notify.Show("alteração de regime cancelado", Notify.TYPE_NOTICE);
											}
										}							
									});									
									
									
									acaEditor.addCloseListener(new Window.CloseListener() {
										
										@Override
										public void windowClose(CloseEvent e) {
											janelaAtiva =	false;
											tb.focus();
										}
									});
									
									getUI().addWindow(acaEditor);
							}else if(ac.getRegime().equals("PROPRIO (PARCIAL)")){
								janelaAtiva = true;
								
								final EditarRegimeProprioParcialParaComodatoTotalEditor acaEditor = new EditarRegimeProprioParcialParaComodatoTotalEditor(item, "Alterar Regime para COMODATO (TOTAL)", true);															
								acaEditor.addListerner(new EditarRegimeProprioParcialParaComodatoTotalEditor.EditarRegimeProprioParcialParaComodatoTotalEditorListerner() {
									
									@Override
									public void onClose(EditarRegimeProprioParcialParaComodatoTotalEditor.EditarRegimeProprioParcialParaComodatoTotalEditorEvent event) {
										if(event.isConfirm()){
											boolean check = AcessoDAO.alterarRegimeProprioParcialParaComodatoTotal(ac,event.getCodSerialRoteador());
											
											if(check){
												acaEditor.close();
												refresh();
												Notify.Show("Regime alterado com sucesso", Notify.TYPE_SUCCESS);
											}
										}else{
											acaEditor.close();
											Notify.Show("alteração de regime cancelado", Notify.TYPE_NOTICE);
										}
									}							
								});									
								
								
								acaEditor.addCloseListener(new Window.CloseListener() {
									
									@Override
									public void windowClose(CloseEvent e) {
										janelaAtiva =	false;
										tb.focus();
									}
								});
								
								getUI().addWindow(acaEditor);
						}else{
									Notify.Show("A Alteração de Regime ainda não esta disponível para este Contrato!", Notify.TYPE_NOTICE);
							}
						}
					}
				}else{
					Notify.Show("Você não Possui Permissão para Editar o Regime do Contrato", Notify.TYPE_ERROR);
				}
			}
		});
        btAlterarRegimeComodatoTotal.setPrimaryStyleName("btSubMenu");
        
        Button btAlterarFiador = new Button("Alterar Fiador", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Fiador"))		
				{		
					
					if(tb.getValue() != null){			
						
						Object value = tb.getValue();
						btAtualizar.click();
						item = tb.getItem(value);	
						
						EntityItem<AcessoCliente> entityItem =(EntityItem<AcessoCliente>)item; 
						AcessoCliente ac = entityItem.getEntity();
						final BeanItem<AcessoCliente> beanItem = new BeanItem<AcessoCliente>(ac);
						janelaAtiva = true;
						final AlterarFiadorEditor mudarTituEditor = new AlterarFiadorEditor(beanItem, "Alterar Fiador", true);
						mudarTituEditor.addListerner(new AlterarFiadorEditor.MudarFiadorListerner() {
							
							@Override
							public void onClose(MudarFiadorEvent event) {
									if(event.isConfirm()){																		
										boolean check = AcessoDAO.alterarFiador(beanItem.getBean());
										
										if(check){
											mudarTituEditor.close();
											refresh();
											Notify.Show("Fiador alterado com sucesso!", Notify.TYPE_SUCCESS);
										}
									 }else{
										 mudarTituEditor.close();
										 Notify.Show("alteração de fiador cancelada.", Notify.TYPE_NOTICE);
									 }
								}
							});
						
						mudarTituEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								janelaAtiva = false;
							}
						});
												
						getUI().addWindow(mudarTituEditor);
					}				
				}else{
					Notify.Show("Você não Possui Permissão para Mudar Fiador de um Contrato", Notify.TYPE_ERROR);
				}
				
			}
		});
        btAlterarFiador.setPrimaryStyleName("btSubMenu");
        
        Button btAlterarTipoNf = new Button("Alterar Tipo de NF", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Tipo Nfe"))		
				{		
					
					if(tb.getValue() != null){			
						
						Object value = tb.getValue();
						btAtualizar.click();
						item = tb.getItem(value);	
						
						EntityItem<AcessoCliente> entityItem =(EntityItem<AcessoCliente>)item; 
						AcessoCliente ac = entityItem.getEntity();
						final BeanItem<AcessoCliente> beanItem = new BeanItem<AcessoCliente>(ac);
						janelaAtiva = true;
						final AlterarTipoNfeEditor alterarTipoNfeEditor = new AlterarTipoNfeEditor(item, "Alterar Tipo Nfe", true);
						alterarTipoNfeEditor.addListerner(new AlterarTipoNfeEditor.AlterarTipoNfeListerner() {
							
							@Override
							public void onClose(AlterarTipoNfeEvent event) {
									if(event.isConfirm()){																		
										boolean check = AcessoDAO.alterarTipoNF(beanItem.getBean());
										
										if(check){
											alterarTipoNfeEditor.close();
											refresh();
											Notify.Show("Tipo Nfe alterado com sucesso!", Notify.TYPE_SUCCESS);
										}
									 }else{
										 alterarTipoNfeEditor.close();
										 Notify.Show("alteração de tipo nfe cancelada.", Notify.TYPE_NOTICE);
									 }
								}
							});
						
						alterarTipoNfeEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								janelaAtiva = false;
							}
						});
												
						getUI().addWindow(alterarTipoNfeEditor);
					}				
				}else{
					Notify.Show("Você não Possui Permissão para Tipo Nfe ", Notify.TYPE_ERROR);
				}
			}
		});
        btAlterarTipoNf.setPrimaryStyleName("btSubMenu");
        
        Button btAlterarIpFixo = new Button("Alterar IP Fixo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				closeAllWindows();
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(),	OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar Ip Fixo"))		
				{		
					
					if(tb.getValue() != null){
						Object value = tb.getValue();
						item = tb.getItem(value);	
						EntityItem<AcessoCliente> entityItem =(EntityItem<AcessoCliente>)item; 
						AcessoCliente ac = entityItem.getEntity();
						
						//Checar se o contrato não está sendo utilizado como 
						boolean estaVinculadoCaixaAtendimento = AcessoDAO.checkVinculoCaixaAtendimento(ac);
						
						if(ac.getStatus_2().equals("PENDENTE_INSTALACAO")){						
							Notify.Show("Cliente Possui Instalação Pendente!", Notify.TYPE_ERROR);
						}else if(estaVinculadoCaixaAtendimento){
							Notify.Show("Este contrato esta vinculado a uma caixa de atendimento para monitoramento!", Notify.TYPE_ERROR);
						}else{
						
							final BeanItem<AcessoCliente> beanItem = new BeanItem<AcessoCliente>(ac);
							janelaAtiva = true;
							final AlterarIpFixoEditor alterarTipoNfeEditor = new AlterarIpFixoEditor(item, "Alterar Ip Fixo", true);
							
							alterarTipoNfeEditor.addListerner(new AlterarIpFixoListerner() {
								
								@Override
								public void onClose(AlterarIpFixoEvent event) {
										if(event.isConfirm()){			
											AcessoCliente ac = event.getAcesso();
											ac.setEndereco_ip(event.getEnderecoIp());
											
											EntityManager em = ConnUtil.getEntity();
											em.getTransaction().begin();										
											em.merge(ac);										
											em.getTransaction().commit();
											
											boolean check = AcessoDAO.alterarIpFixo(ac);
											
											
											if(check){
												//alterarTipoNfeEditor.close();
												refresh();
												Notify.Show("Tipo Nfe alterado com sucesso!", Notify.TYPE_SUCCESS);
											}
										 }else{
											 alterarTipoNfeEditor.close();
											 Notify.Show("alteração de tipo nfe cancelada.", Notify.TYPE_NOTICE);
										 }
									}
								});
							
							alterarTipoNfeEditor.addCloseListener(new Window.CloseListener() {
								
								@Override
								public void windowClose(CloseEvent e) {
									janelaAtiva = false;
								}
							});
													
							getUI().addWindow(alterarTipoNfeEditor);
						}				
					}
				}else{
					Notify.Show("Você não Possui Permissão para Alterar Ip Fixo ", Notify.TYPE_ERROR);
				}
			}
		});
        btAlterarIpFixo.setPrimaryStyleName("btSubMenu");
        
        if(tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().equals("BLOQUEADO")){
        	btAlterarIpFixo.setEnabled(true);
        	btAlterarTipoNf.setEnabled(true);
        	btAlterarFiador.setEnabled(false);
        	btAlterarRegimeComodato.setEnabled(false);
        	btAlterarRegimeProprio.setEnabled(false);
        	btAlterarRegimeComodatoTotal.setEnabled(false);
        	btAlterarEndereco.setEnabled(false);
        	btRemoverMaterial.setEnabled(false);
        	btAlterarMaterial.setEnabled(false);
        	btAlterarVencimento.setEnabled(false);
        	btAlterarTitularidade.setEnabled(false);
        	btAltConcentrador.setEnabled(false);
        	btAlterarCredenciais.setEnabled(false);
        	btAlterarPlano.setEnabled(false);
        	btSuspenderContrato.setEnabled(false);
        	
        }else{
        	btAlterarIpFixo.setEnabled(true);
        	btAlterarTipoNf.setEnabled(true);
        	btAlterarFiador.setEnabled(true);
        	btAlterarRegimeComodato.setEnabled(true);
        	btAlterarRegimeProprio.setEnabled(true);
        	btAlterarRegimeComodatoTotal.setEnabled(true);
        	btAlterarEndereco.setEnabled(true);
        	btRemoverMaterial.setEnabled(true);
        	btAlterarMaterial.setEnabled(true);
        	btAlterarVencimento.setEnabled(true);
        	btAlterarTitularidade.setEnabled(true);
        	btAltConcentrador.setEnabled(true);
        	btAlterarCredenciais.setEnabled(true);
        	btAlterarPlano.setEnabled(true);
        	btSuspenderContrato.setEnabled(true);
        }
        
        if(tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().equals("SUSPENSO")){
        	btReativarContrato.setEnabled(true);
        	btAlterarIpFixo.setEnabled(false);
        	btAlterarTipoNf.setEnabled(false);
        	btAlterarFiador.setEnabled(false);
        	btAlterarRegimeComodato.setEnabled(false);
        	btAlterarRegimeProprio.setEnabled(false);
        	btAlterarRegimeComodatoTotal.setEnabled(false);
        	btAlterarEndereco.setEnabled(false);
        	btRemoverMaterial.setEnabled(false);
        	btAlterarMaterial.setEnabled(false);
        	btAlterarVencimento.setEnabled(false);
        	btAlterarTitularidade.setEnabled(false);
        	btAltConcentrador.setEnabled(false);
        	btAlterarCredenciais.setEnabled(false);
        	btAlterarPlano.setEnabled(false);
        	btSuspenderContrato.setEnabled(false);
        }else{
        	btReativarContrato.setEnabled(false);
        }
        
        //---------TESTE
        Button btMigrarPlanosClientesCabo = new Button("Migrar Planos", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				EntityManager em = ConnUtil.getEntity();

				PlanoAcesso planoAntigo = PlanoAcessoDAO.find(143);
				PlanoAcesso planoNovo = PlanoAcessoDAO.find(251);
				
				if(planoAntigo != null && planoNovo != null){
						
						Query q1 = em.createQuery("select ac from AcessoCliente ac where "
								+ "ac.status_2!='ENCERRADO' AND "
								+ "ac.status_2 != 'PENDENTE_INSTALACAO' AND "
								+ "ac.plano=:p AND "
								+ "ac.base !=:c1 AND ac.base !=:c2", AcessoCliente.class);
						
						q1.setParameter("p", planoAntigo);
						q1.setParameter("c1", new Concentrador(105));
						q1.setParameter("c2", new Concentrador(132));
						
						
						for (AcessoCliente ac : (List<AcessoCliente>)q1.getResultList()) {
							System.out.println(ac.getCliente().getNome_razao());
							boolean check  = AcessoDAO.alteraPlano(ac,planoNovo, null, ac.getInstalacao_gratis());
						}
						
				}
				
			}
		}); 
        //  l.addComponent(btMigrarPlanosClientesCabo);
       //---------TESTE
        
        l.addComponent(btAlterarPlano);
        l.addComponent(btAlterarCredenciais);
        l.addComponent(btAltConcentrador);
        l.addComponent(btAlterarIpFixo);
        l.addComponent(btAlterarMaterial);
        l.addComponent(btRemoverMaterial); 
        l.addComponent(btAlterarRegimeProprio);
        l.addComponent(btAlterarRegimeComodato);
        l.addComponent(btAlterarRegimeComodatoTotal);
        l.addComponent(btAlterarTitularidade);
        l.addComponent(btAlterarFiador);        
        l.addComponent(btAlterarVencimento);
        l.addComponent(btAlterarEndereco);
        l.addComponent(btAlterarTipoNf);
        l.addComponent(btSuspenderContrato);
        l.addComponent(btReativarContrato);
        
        
        
        
        
    
    }
	
	
	
	
	Window winSubMenuHistorico;
	private void buildSubMenuBloqueioDesbloqueio(ClickEvent event) {
		winSubMenuBloqueioDesbloqueio = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuBloqueioDesbloqueio.setContent(l);
        winSubMenuBloqueioDesbloqueio.setWidth("300px");
        winSubMenuBloqueioDesbloqueio.addStyleName("notifications");
        winSubMenuBloqueioDesbloqueio.setClosable(false);
        winSubMenuBloqueioDesbloqueio.setResizable(false);
        winSubMenuBloqueioDesbloqueio.setDraggable(false);
        winSubMenuBloqueioDesbloqueio.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuBloqueioDesbloqueio.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuBloqueioDesbloqueio.setCloseShortcut(KeyCode.ESCAPE, null);

        
        Button bt1 = new Button("Agendar Bloqueio/Desbloqueio",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(),	OpusERP4UI.getUsuarioLogadoUI().getId(), "Agendar Bloqueio/Desbloqueio"))				
				{				
					final Integer codAcesso = (Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue();
					final AcessoCliente ac = ContratosAcessoDAO.find(codAcesso);
					
					AgendamentoBloqueioDesbloqueioEditor agendamentoEditor = new AgendamentoBloqueioDesbloqueioEditor(true,true, ac);					
					getUI().addWindow(agendamentoEditor); 
					
				}else{
					Notify.Show("Você não Possui Permissão para Agendar Bloqueio dos Contratos", Notify.TYPE_ERROR);
				}
				
			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        
        Button bt2 = new Button("Bloquear/Desbloquear", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Bloquear/Desbloquear"))				
				{
					final Integer codAcesso = (Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue();
					final AcessoCliente ac = ContratosAcessoDAO.find(codAcesso);
					
					
					if(!ac.getStatus_2().equals("BLOQUEADO")){
					GenericDialog cdEncerrar = new GenericDialog("Confirme para Continuar", "Você deseja Realmente Bloquear este Contrato?", true, true);
					cdEncerrar.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									CredenciaisAcessoDAO.bloquearContratoClienteTotal(codAcesso);						
																	
									refresh();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Bloqueou um Contrato de Acesso"));
									AlteracoesContratoDAO.save(new AlteracoesContrato(null, "BLOQUEIO CONTRATO", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
																				
								}
						}
					});					
					getUI().addWindow(cdEncerrar);
					
					}else{
						GenericDialog cdEncerrar = new GenericDialog("Confirme para Continuar", "Você deseja Realmente Desbloquear este Contrato?", true, true);
						cdEncerrar.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
									if(event.isConfirm()){

										CredenciaisAcessoDAO.DesbloquearContrato(codAcesso);									
										
										refresh();
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Desbloqueou um Contrato de Acesso"));
										AlteracoesContratoDAO.save(new AlteracoesContrato(null, "DESBLOQUEIO CONTRATO", ac, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
																					
									}
							}
						});					
						getUI().addWindow(cdEncerrar);
					}
						
				}else{
					Notify.Show("Você não Possui Permissão para Bloquear/Desbloquear Contratos", Notify.TYPE_ERROR);
				}
				
				
			}
		});
        bt2.setPrimaryStyleName("btSubMenu");
        
        Button bt3 = new Button("Desbloqueio 72h", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Desbloqueio 72h"))				
				{
				
						if(tb.getItem(tb.getValue()).getItemProperty("status_2").getValue().equals("BLOQUEADO")){
							
								final Integer codAcesso = (Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue();
								final Window wBoletosBloqueados = new Window("Boletos bloqueados");
								wBoletosBloqueados.setModal(true);
								wBoletosBloqueados.center();
								wBoletosBloqueados.setWidth("600px");
								wBoletosBloqueados.setHeight("250px");
								wBoletosBloqueados.setContent(new VerticalLayout(){
									{											
										setWidth("100%");
										setHeight("200px");
										
										setMargin(true);
										setSpacing(true); 
										
										final Table tbBoletos = new Table();
										tbBoletos.setWidth("100%");
										tbBoletos.setHeight("167px");
										tbBoletos.setSelectable(true);
										
										tbBoletos.addContainerProperty("Cod", BigInteger.class, null);
										tbBoletos.addContainerProperty("Ndoc", String.class, "");
										tbBoletos.addContainerProperty("Valor", String.class, "");
										tbBoletos.addContainerProperty("Vencimento", String.class, "");						
										tbBoletos.addContainerProperty("Status", String.class, "");
										tbBoletos.addContainerProperty("Bloqueado", String.class, "");
										
										List<ContasReceber> boletos = ContasReceberDAO.procurarBoletosBloqueadosPorContrato(codAcesso);

										if(boletos != null && boletos.size() >0){
											for (ContasReceber contasReceber : boletos) {											
												tbBoletos.addItem(new Object[]{new BigInteger(String.valueOf(contasReceber.getId())), contasReceber.getN_doc(),"R$ "+contasReceber.getValor_titulo(), DataUtil.formatDateBra(contasReceber.getData_vencimento()),contasReceber.getStatus(),contasReceber.getBloqueado()}, tbBoletos.getItemIds().size()+1);						
											}
										}
										
										addComponent(tbBoletos);
										
										
										HorizontalLayout hlButton = new HorizontalLayout();
										hlButton.setWidth("100%");
										
										Button btDesbloquear = new Button("Desbloquear", new Button.ClickListener() {
											
											@Override
											public void buttonClick(ClickEvent event) {
												//Verificar se o boleto está vinculado a algum desbloqueio anterior.
												
												if(tbBoletos.getValue() != null){
													final Integer cod_boleto_selecionado = Integer.parseInt(tbBoletos.getItem(tbBoletos.getValue()).getItemProperty("Cod").getValue().toString());
													boolean check = Desbloqueio72hDAO.checarSeBoletoPodeSerDesbloqueado(cod_boleto_selecionado);
													
													if(check){
														GenericDialog gd = new GenericDialog("Cofirme para continuar!", "Você deseja realmente desbloquear este contrato por 72h?", true, true);
														gd.addListerner(new GenericDialog.DialogListerner() {
															
															@Override
															public void onClose(DialogEvent event) {
																	if(event.isConfirm()){
																		if(tb.getValue() != null){
																			
																			final Integer codAcesso = (Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue();												
																			boolean check = Desbloqueio72hDAO.desbloquear72horas(codAcesso, cod_boleto_selecionado); 
																											
																			if(check){
																				refresh();
																				wBoletosBloqueados.close();
																			}
																			
																		}
																	}
															}
														});
														
														getUI().addWindow(gd);
													}else{
														Notify.Show("O Boleto selecionado já foi utilizado em outro desbloqueio.", Notify.TYPE_NOTICE);
													}
												}else{
													Notify.Show("Selecione um boleto!", Notify.TYPE_NOTICE);
												}
											}
										});
										
										hlButton.addComponent(btDesbloquear);
										
										addComponent(hlButton);
										
										setExpandRatio(tbBoletos, 1f);
										
									}
								});
								
								getUI().addWindow(wBoletosBloqueados); 
																
						}else{
							Notify.Show("Não é necessário DESBLOQUEAR contratos ativos!", Notify.TYPE_NOTICE);
						}
				
				}else{
					Notify.Show("Você não Possui Permissão para Desbloquear por 48h", Notify.TYPE_ERROR);
				}
				
			}
		});
        bt3.setPrimaryStyleName("btSubMenu");
        
        l.addComponent(bt1);
        l.addComponent(bt2);
        l.addComponent(bt3);
    }
	
	
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

        
        Button bt1 = new Button("Histórico de Acesso",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Histórico de Acesso"))				
				{				
					if(tb.getValue() != null && tb.getItem(tb.getValue()).getItemProperty("login").getValue() != null){
						item = tb.getItem(tb.getValue());
						getUI().addWindow(new HistoricoAcesso(true, true, item.getItemProperty("login").getValue().toString(),item));
						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Visualizou o Histórico de Acesso de um Contrato de Acesso"));
						
						
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Visualizar o Histórico de Acesso dos Contratos", Notify.TYPE_ERROR);
				}
				
			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        
        Button bt2 = new Button("Histórico de Chamados", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Histórico de Chamados"))				
				{
				
					if(tb.getValue() != null){
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)tb.getItem(tb.getValue()); 
						item = tb.getItem(tb.getValue());
						getUI().addWindow(new HistoricoChamados(true, true, entityItem.getEntity()));
						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Visualizou o Histórico de Chamados de um Contrato de Acesso"));
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Visualizar o Histórico de Chamados dos Contratos", Notify.TYPE_ERROR);
				}
				
			}
		});
        bt2.setPrimaryStyleName("btSubMenu");
        
        Button bt4 = new Button("Histórico de Contatos", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Histórico de Contatos"))				
				{
				
					if(tb.getValue() != null){
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)tb.getItem(tb.getValue()); 
						item = tb.getItem(tb.getValue());
						getUI().addWindow(new HistoricoContato(true, true, entityItem.getEntity()));
						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Visualizou o Histórico de Contatos de um Contrato de Acesso"));
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Visualizar o Histórico de Contatos do Contratos", Notify.TYPE_ERROR);
				}
				
			}
		});
        bt4.setPrimaryStyleName("btSubMenu");
        
        Button bt3 = new Button("Histórico de Pagamentos",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Histórico de Pagamentos")){		
					
					if(tb.getValue() != null){						
						
						Integer codAcesso = (Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue();
						HistoricoPagamentos histAlte = new HistoricoPagamentos(true, true,codAcesso);
						
						getUI().addWindow(histAlte);						
						
					}
				
				}else{
					Notify.Show("Você não Possui Permissão para Visualizar o Histórico de Pagamentos dos Contratos", Notify.TYPE_ERROR);
				}
				
			}
		});
        bt3.setPrimaryStyleName("btSubMenu");
        
        Button bt5 = new Button("Histórico de Crédito", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				EntityItem<AcessoCliente> itemContrato = (EntityItem<AcessoCliente>)tb.getItem(tb.getValue());
				HistoricoCredito historicoCredito = new HistoricoCredito(true, true, itemContrato.getEntity());
				
				getUI().addWindow(historicoCredito); 
			}
		});
        bt5.setPrimaryStyleName("btSubMenu");
        
        l.addComponent(bt1);
        l.addComponent(bt2);
        l.addComponent(bt4);
        l.addComponent(bt3);
        l.addComponent(bt5); 
       
    }
	
	
	Window winSubMenuImprimir;
	private void buildSubMenuImprimir(ClickEvent event) {
		winSubMenuImprimir = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuImprimir.setContent(l);
        winSubMenuImprimir.setWidth("300px");
        winSubMenuImprimir.addStyleName("notifications");
        winSubMenuImprimir.setClosable(false);
        winSubMenuImprimir.setResizable(false);
        winSubMenuImprimir.setDraggable(false);
        winSubMenuImprimir.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuImprimir.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuImprimir.setCloseShortcut(KeyCode.ESCAPE, null);

        Button bt1 = new Button("Termo de Adesão",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Imprimir Contrato"))				
				{			
					try{
						window = new Window();
						window.setCaption("Termo de Adesão");
				        window.setWidth("800px");
				        window.setHeight("600px");
				        window.setResizable(true);
				        window.center();
				        window.setModal(true);
				        window.setStyleName("disable_scroolbar");		
				        window.setCloseShortcut(KeyCode.ESCAPE, null);
				        
				        Integer codAc = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
				        String nomeCliente = tb.getItem(tb.getValue()).getItemProperty("cliente.nome_razao").getValue().toString();
				        String codCliente = tb.getItem(tb.getValue()).getItemProperty("cliente.id").getValue().toString();
				        StreamResource resource = new StreamResource(new ImprimirContrato(codAc), "TERMO ADESAO "+String.valueOf(codAc)+"-"+nomeCliente+".pdf");
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
				        
				        AcessoCliente acesso = ContratosAcessoDAO.find(codAc);
				        
				        LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Imprimiu um Contrato de Acesso"));
				        AlteracoesContratoDAO.save(new AlteracoesContrato(null, "IMPRIMIU CONTRATO", acesso, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
					
					}catch(Exception e){
						System.out.println("Deu ERRADO!");
						e.printStackTrace();
						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Imprimir um Contrato de Acesso"));
					}
				
				}else{					
					Notify.Show("Você não Possui Permissão para Editar Contrato", Notify.TYPE_ERROR);
				}
								
			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        
        Button bt2 = new Button("Termo de Recisão", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					window = new Window();
					window.setCaption("Termo de Recisão");
			        window.setWidth("800px");
			        window.setHeight("600px");
			        window.setResizable(true);
			        window.center();
			        window.setModal(true);
			        window.setStyleName("disable_scroolbar");		
			        window.setCloseShortcut(KeyCode.ESCAPE, null);
			        
			        Integer codAc = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
			        String nomeCliente = tb.getItem(tb.getValue()).getItemProperty("cliente.nome_razao").getValue().toString();
			        String codCliente = tb.getItem(tb.getValue()).getItemProperty("cliente.id").getValue().toString();
			        
			        StreamResource resource = new StreamResource(new ComunicadoRecisao(codAc), "TERMO RECISAO "+String.valueOf(codAc)+"-"+nomeCliente+".pdf");
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
			        
			        AcessoCliente acesso = ContratosAcessoDAO.find(codAc);
			        
			        //LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Imprimiu um Contrato de Acesso"));
			        //AlteracoesContratoDAO.save(new AlterarcoesContrato(null, "IMPRIMIU CONTRATO", acesso, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
				
				}catch(Exception e){
					System.out.println("Deu ERRADO!");
					e.printStackTrace();
					
					LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Imprimir um Contrato de Acesso"));
				}
			}
		});
        bt2.setPrimaryStyleName("btSubMenu");
        
        Button bt3 = new Button("Termo de Benefícios", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try{
					window = new Window();
					window.setCaption("Termo de Benefícios");
			        window.setWidth("800px");
			        window.setHeight("600px");
			        window.setResizable(true);
			        window.center();
			        window.setModal(true);
			        window.setStyleName("disable_scroolbar");		
			        window.setCloseShortcut(KeyCode.ESCAPE, null);
			        
			        Integer codAc = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
			        String nomeCliente = tb.getItem(tb.getValue()).getItemProperty("cliente.nome_razao").getValue().toString();
			        String codCliente = tb.getItem(tb.getValue()).getItemProperty("cliente.id").getValue().toString();
			        
			        StreamResource resource = new StreamResource(new TermoBeneficios(codAc), "TERMO BENEFICIO "+String.valueOf(codAc)+"-"+nomeCliente+".pdf");
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
			        
			        AcessoCliente acesso = ContratosAcessoDAO.find(codAc);
			        
			        
				}catch(Exception e){
					System.out.println("Deu ERRADO!");
					e.printStackTrace();
					
					LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Imprimir um Termo de Beneficios"));
				}
			}
		});
        bt3.setPrimaryStyleName("btSubMenu");
        
        l.addComponent(bt1);
        l.addComponent(bt3);
        l.addComponent(bt2);
        
        if(tb.getValue() != null){
			
			EntityItem<AcessoCliente> contratoEntity = (EntityItem<AcessoCliente>)tb.getItem(tb.getValue());
			final AcessoCliente contrato = contratoEntity.getEntity();
			
			Button bt4 = new Button("Todos os documentos", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
											
						ArquivosUpload au = new ArquivosUpload("Documentos no Amazon", true, contrato,codSubModulo);
						au.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								btAtualizar.click();
							}
						});
						
						getUI().addWindow(au); 						
					
				}
			});
			
	        bt4.setPrimaryStyleName("btSubMenu"); 
	        l.addComponent(bt4);
			
			//final ArquivosContrato arquivo = ArquivosContratoDAO.buscarArquivo(contrato);
			
//			if(arquivo != null){
//				Button bt4 = new Button("Visualizar Arquivo", new Button.ClickListener() {
//					
//					@Override
//					public void buttonClick(ClickEvent event) {
//						try{									
//								String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
//								File file = new File(basepath + "/WEB-INF/uploads/logo.jpeg");
//								
//								writeFile((byte[]) arquivo.getFile());								
//						}catch(Exception e){							
//							e.printStackTrace();					
//							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Não Conseguiu Imprimir um Termo de Beneficios"));
//						}
//					}
//				});
//		        bt4.setPrimaryStyleName("btSubMenu"); 
//		        l.addComponent(bt4);
//			}
			//else{
				
		//	}			
        }
        
    }
	
	
	Window winSubMenuLogs;
	private void buildSubMenuLogs(ClickEvent event) {
		winSubMenuLogs = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuLogs.setContent(l);
        winSubMenuLogs.setWidth("300px");
        winSubMenuLogs.addStyleName("notifications");
        winSubMenuLogs.setClosable(false);
        winSubMenuLogs.setResizable(false);
        winSubMenuLogs.setDraggable(false);
        winSubMenuLogs.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuLogs.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuLogs.setCloseShortcut(KeyCode.ESCAPE, null);
        
        Button bt1 = new Button("Log de Alterações",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tb.getValue() != null){
					
						Integer codAcesso = (Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue();
						HistoricoAlteracoes histAlte = new HistoricoAlteracoes(true, true,codAcesso);
						
						getUI().addWindow(histAlte);
						
				}
				
			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        
        Button bt2 = new Button("Log de Alterações de Materiais",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				HistoricoAlteracoesMateriais histAlte = new HistoricoAlteracoesMateriais(true, true);				
				getUI().addWindow(histAlte);				
				
			}
		});
        bt2.setPrimaryStyleName("btSubMenu");
        
        Button bt3 = new Button("Notificações", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				NotificacoesView notifW = new NotificacoesView();
				getUI().addWindow(notifW);
			}
		});
        bt3.setPrimaryStyleName("btSubMenu");
     
        l.addComponent(bt1);
        l.addComponent(bt2);
        l.addComponent(bt3); 
        
             
    }
		
	public Button BuildbtExcluir() {
		
		return null;
	}
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}
	
	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}

	public void writeFile(byte[] data) throws IOException
	 {		
		
		File file = new File(VaadinService.getCurrent().getBaseDirectory()+"/uploads/contrato1.pdf");
	   OutputStream fo = new FileOutputStream(file);
	   // Write the data
	   fo.write(data);
	   // flush the file (down the toilet)
	   fo.flush();
	   // Close the door to keep the smell in.
	   fo.close();
	   
	   FileResource fr = new FileResource(file);
		
		Embedded emb = new Embedded();
		emb.setType(Embedded.TYPE_BROWSER);
		emb.setSource(fr); 
		emb.setSizeFull();
		
		final Window w = new Window("Contrato");
		w.setWidth("600px");
		w.setHeight("560px");
		w.setContent(emb); 
		w.setResizable(true);
		w.setModal(true); 
		
		getUI().addWindow(w);
	 }

}
