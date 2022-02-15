package com.digital.opuserp.view.modulos.cadastro.veiculos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.VeiculoDAO;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.veiculos.VeiculosEditor.VeiculoEvent;
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
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class VeiculosView extends VerticalLayout implements GenericView{

	//
	JPAContainer<Veiculos> container;
	
	Table tb;
	TextField tfBusca;
	ComboBox cbStatus;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	
	GerenciarModuloDAO gmDAO;
	
	public VeiculosView(boolean act) {
		
		if(act){		
			setSizeFull();
			
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtTransferencia());
			hlButons.addComponent(BuildbtAceitar());
			hlButons.addComponent(BuildbtCancelar());
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildStatus());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
	
			addComponent(buildTbGeneric());
			//addComponent(buildLbRegistros());
			
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildLbRegistros());
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
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof VeiculosView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btEditar != null || slEditar != null){
				btEditar.removeShortcutListener(slEditar);			
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
				btEditar.click();
			}
		};
		return slEditar;
	}
	
	public JPAContainer<Veiculos> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Veiculos.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(cbStatus.getValue().toString() != null){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		}
		
		return container;
	}

	@Override
	public Table buildTbGeneric() {	
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);		
		tb.setColumnCollapsed("id", true);	
		tb.setColumnCollapsed("garantia", true);
		tb.setColumnCollapsed("cor", true);
		tb.setColumnCollapsed("opcionais", true);
		tb.setColumnCollapsed("chassi", true);
		tb.setColumnCollapsed("combustivel", true);
		tb.setColumnCollapsed("ano_modelo",true);
		tb.setColumnCollapsed("cpf_cnpj_proprietario", true);
		tb.setColumnCollapsed("nome_proprietario", true);
		tb.setColumnCollapsed("venc_ipva_impostos", true);
		tb.setColumnCollapsed("data_ultima_revisao", true);
		tb.setColumnCollapsed("venc_seguro", true);
		tb.setColumnCollapsed("data_cadastro", true);
		tb.setColumnCollapsed("data_garantia", true);
		tb.setColumnCollapsed("revisao_geral", true);
		tb.setColumnCollapsed("seguradora", true);
		tb.setColumnCollapsed("modelo", true);
		
		tb.setColumnCollapsible("tipo", false);		
		
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("cod_veiculo", "Código do Veiculo");
		tb.setColumnHeader("tipo", "Tipo");
		tb.setColumnHeader("marca", "Marca");
		tb.setColumnHeader("modelo", "Modelo");
		tb.setColumnHeader("cor", "Cor");
		tb.setColumnHeader("opcionais", "Opcionais");
		tb.setColumnHeader("placa", "Placa");
		tb.setColumnHeader("chassi", "Chassi");
		tb.setColumnHeader("combustivel", "Combustível");
		tb.setColumnHeader("ano_fab", "Ano de Fabricação");
		tb.setColumnHeader("ano_modelo", "Ano do Modelo");
		tb.setColumnHeader("cpf_cnpj_proprietario", "CPF/CNPJ do Proprietário");
		tb.setColumnHeader("nome_proprietario", "Proprietário");
		tb.setColumnHeader("venc_ipva_impostos", "Vencimento IPVA Impostos");
		tb.setColumnHeader("km_atual", "KM Atual");
		tb.setColumnHeader("data_ultima_revisao", "Data da Última Revisão");
		tb.setColumnHeader("venc_seguro", "Vencimento do Seguro");
		tb.setColumnHeader("data_cadastro", "Data de Cadastro");
		tb.setColumnHeader("data_garantia", "Data da Garantia ");
		tb.setColumnHeader("revisao_geral", "Revisão Geral");
		tb.setColumnHeader("seguradora", "Seguradora");
		
		tb.setVisibleColumns(new Object[] {"id","cod_veiculo","tipo","marca","modelo","cor","opcionais","placa",
				"chassi","combustivel","ano_fab","ano_modelo","cpf_cnpj_proprietario","nome_proprietario","venc_ipva_impostos",
				"km_atual","data_ultima_revisao","venc_seguro","data_cadastro","data_garantia","seguradora","revisao_geral"});
				
		
		
		tb.setImmediate(true);		
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditar.setEnabled(true);
					btExcluir.setEnabled(true);
				}else{
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
				}
			}
		});	
		
		
		tb.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()  && tb.getValue() != null){
					
					VeiculosEditor VeiEditor = new VeiculosEditor(tb.getItem(tb.getValue()), "Editar Veiculo", true);
					VeiEditor.addListerner(new VeiculosEditor.VeiculoListerner() {
						
						@Override
						public void onClose(VeiculoEvent event) {
							if(event.isConfirm()){
								container.commit();
								tb.focus();
							}
						}
					});
					
					VeiEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
					
					getUI().addWindow(VeiEditor);
					VeiEditor.center();
					VeiEditor.setModal(true);	
				}
			}
		});
		
		
		//List<Veiculos> veiculos = VeiculoDAO.getVeiculos();
		
//		tb.addContainerProperty("id", Integer.class, null);
//		tb.addContainerProperty("cod_veiculo", Integer.class, null);
//		tb.addContainerProperty("tipo", String.class, "");
//		tb.addContainerProperty("marca", String.class, "");
//		tb.addContainerProperty("modelo", String.class, "");
//		tb.addContainerProperty("cor", String.class, "");
//		tb.addContainerProperty("opcionais", String.class, "");
//		tb.addContainerProperty("placa", String.class, "");
//		tb.addContainerProperty("chassi", String.class, "");
//		tb.addContainerProperty("combustivel", String.class, "");
//		tb.addContainerProperty("ano_fab", String.class, "");
//		tb.addContainerProperty("ano_modelo", String.class, "");
//		tb.addContainerProperty("cpf_cnpj_proprietario", String.class, "");
//		tb.addContainerProperty("nome_proprietario", String.class, "");
//		tb.addContainerProperty("venc_ipva_impostos", String.class, "");
//		tb.addContainerProperty("km_atual", String.class, "");
//		tb.addContainerProperty("data_ultima_revisao", Date.class, null);
//		tb.addContainerProperty("venc_seguro",Date.class, null);
//		tb.addContainerProperty("data_cadastro", Date.class, null);
//		tb.addContainerProperty("data_garantia",Date.class, null);
//		tb.addContainerProperty("seguradora", Integer.class, null);
//		tb.addContainerProperty("revisao_geral", Integer.class, null);
				
//		for (Veiculos v : veiculos) {
//			tb.addItem(new Object[]{v.getSeguradora()}, tb.getItemIds().size());
//		}
		
		
		return tb;
	}

	public ComboBox buildStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.addItem("ATIVO");
		cbStatus.addItem("INATIVO");
		cbStatus.setImmediate(true);
		cbStatus.select("ATIVO");
		cbStatus.addListener(new Property.ValueChangeListener() {
			
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
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}


	@Override
	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
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

						SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

						for(Object c:collums){		 
							if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){

									Date date = sdf2.parse(s);
									filtros.add(Filters.eq(c.toString(), date));
								}						
							}
						
					} catch (Exception e2) {
						for(Object c:collums){		 
							
							if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
								filtros.add(new Like(c.toString(), "%"+s+"%", false));
							}	
						}
					}
				}
	
			container.addContainerFilter(Filters.or(filtros));
		
		}
				
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
		
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
				
					final BeanItem<Veiculos> novoVeiculo = new BeanItem<Veiculos>(new Veiculos());
									
					VeiculosEditor VeiEditor = new VeiculosEditor(novoVeiculo, "Cadastrar Veiculo", true);
					VeiEditor.addListerner(new VeiculosEditor.VeiculoListerner() {
						
						@Override
						public void onClose(VeiculoEvent event) {						
							if(event.isConfirm()){
								try {
									Veiculos veiculo = novoVeiculo.getBean();
									veiculo.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
									
									container.addEntity(veiculo);	
									container.commit();
									tb.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Veículo"));
								} catch (Exception e) {
									//Notification.show("ERRO: "+e.getMessage());	
								}
							}						
						}
					});
					
					
					
					VeiEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
							//refresh();
						}
					});
					
					getUI().addWindow(VeiEditor);
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um Veículo", Type.ERROR_MESSAGE);
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
					
						if(tb.getValue() != null && tb.getItem(tb.getValue()) != null && tb.getItem(tb.getValue()).getItemProperty("id").getValue() != null){
					
								final EntityManager em = ConnUtil.getEntity();
								
								try{
																		
										em.getTransaction().begin();
										
										Item i = tb.getItem(tb.getValue());
										
										//final Veiculos v = em.find(Veiculos.class, i.getItemProperty("id").getValue());
										
//										Map<String, Object> props = new HashMap<String, Object>();
//										props.put("javax.persistence.lock.timeout", 1);
//										
//										em.lock(v, LockModeType.WRITE, props);
										
										VeiculosEditor VeiEditor = new VeiculosEditor(i, "Editar Veiculo", true);
										VeiEditor.addListerner(new VeiculosEditor.VeiculoListerner() {
											
											@Override
											public void onClose(VeiculoEvent event) {
												if(event.isConfirm()){
														
													container.commit();
													container.refresh();
													
													tb.focus();					
													LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Veículo"));
												}
											}
										});
										
												
										getUI().addWindow(VeiEditor);
										VeiEditor.center();
										VeiEditor.setModal(true);
									
									
									
								}catch(Exception e){	
									if(em.getTransaction().isActive()){
										em.getTransaction().rollback();		
									}
									
									//Notify.Show("O Veículo selecionado já esta sendo utilizado em outra edição!", Notify.TYPE_WARNING);									
								}
							
						}
						
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Editar um Veículo", Type.ERROR_MESSAGE);
				}
			}
		});
		
	
		btEditar.setEnabled(false);
		return btEditar;
	}
	
	
	
	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
				
					VeiculoDAO vDAO = new VeiculoDAO();
					if(vDAO.allowRemove((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue(), OpusERP4UI.getEmpresa().getId())){
						
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Veiculo Selecionado?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
										container.removeItem(tb.getValue());
										container.commit();
										Notification.show("Veiculo Excluído com Sucesso!");
										tb.focus();

										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Veículo"));
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
						Notification.show("Atenção", "Não é Possivel Excluir este Veiculo porque Possui Vinculo com Outros Módulos", Type.ERROR_MESSAGE);
					}
					
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Excluir um Veículo", Type.ERROR_MESSAGE);
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
