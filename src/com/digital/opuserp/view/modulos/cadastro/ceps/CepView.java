package com.digital.opuserp.view.modulos.cadastro.ceps;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CepDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.ceps.CepEditor.CepEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
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

public class CepView extends VerticalLayout implements GenericView {

	//JPAContainer<Ceps> containerCeps;
	BeanItemContainer<Ceps> containerCeps = new BeanItemContainer<>(Ceps.class);
	
	Table tb;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	public CepView(boolean act){
	
		if(act){
					
		setSizeFull();				
				
		gmDAO = new GerenciarModuloDAO();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(BuildbtTransferencia());
		hlButons.addComponent(BuildbtAceitar());
		hlButons.addComponent(BuildbtCancelar());
		
		addComponent(hlButons);
		setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(buildTfbusca());
		
		addComponent(buildTbGeneric());
	//	addComponent(buildLbRegistros());
		
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
		lbRegistros = new Label(String.valueOf(containerCeps.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	public void buildShortcurEvents(Component c){
		
		if(c instanceof CepView)
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
	
	public BeanItemContainer<Ceps> buildContainer(){
		containerCeps = CepDAO.getCeps();
		//containerCeps.sort(new String[] {"endereco"}, new boolean[]{true});
				
		return containerCeps;
	}
	
	@Override
	public Table buildTbGeneric() {
			
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setVisibleColumns(new Object[]{"id","cep","endereco","bairro","cidade","uf","pais"});	
		
		tb.setColumnCollapsed("id", true);	
		tb.setColumnCollapsed("uf", true);
		tb.setColumnCollapsed("pais", true);
		
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("cep", "CEP");
		tb.setColumnHeader("endereco", "Endereço");
		tb.setColumnHeader("bairro", "Bairro");
		tb.setColumnHeader("cidade", "Cidade");
		tb.setColumnHeader("uf", "UF");
		tb.setColumnHeader("pais", "Pais");
		
		tb.setColumnCollapsible("endereco", false);
		
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
					
					CepEditor cEditor = new CepEditor(tb.getItem(tb.getValue()), "Editar Cep", true);
					cEditor.addListerner(new CepEditor.CepListerner() {
						
						@Override
						public void onClose(CepEvent event) {
							if(event.isConfirm()){
								//container.commit();
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

		containerCeps.removeAllContainerFilters();
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && containerCeps.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		
		containerCeps.addContainerFilter(Filters.or(filtros));
		//containerCeps.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
							
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
					
						final BeanItem<Ceps> newItem = new BeanItem<Ceps>(new Ceps());
										
						CepEditor cEditor = new CepEditor(newItem, "Cadastrar CEP", true);
						cEditor.addListerner(new CepEditor.CepListerner() {
							
							@Override
							public void onClose(CepEvent event) {
								if(event.isConfirm()){
									
									try {
										Ceps cAdd = newItem.getBean();	
										cAdd.setCep(cAdd.getCep().toUpperCase());
										cAdd.setBairro(cAdd.getBairro().toUpperCase());
										cAdd.setCidade(cAdd.getCidade().toUpperCase());
										cAdd.setEndereco(cAdd.getEndereco().toUpperCase());
										cAdd.setPais(cAdd.getPais().toUpperCase());
										cAdd.setUf(cAdd.getUf().toUpperCase());
										
										String r = CepDAO.save(cAdd); 

										if(r.equals("cadastrado")){
											Notify.Show("Cep cadastrado com sucesso!", Notify.TYPE_SUCCESS);
										}else{
											Notify.Show(r, Notify.TYPE_ERROR);
										}
										
										tb.focus();
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um CEP"));
									} catch (Exception e) {
										
																		
										Notification.show("zndflvjznxlcn: "+e.getMessage());								
		//								LogDAO.addLog(new Log(null, "ERRO ao Cadastrar um Novo Cliente: "+e.getMessage(), OpusERP4_ServerUI.USUARIO_LOGADO.getUsername(), null));
									}
									
								}else{
		//							LogDAO.addLog(new Log(null, "Ação de Cadastro de Cliente Cancelada!", OpusERP4_ServerUI.USUARIO_LOGADO.getUsername(), null));
								}
							}
						});
						
						cEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
								//refresh();
							}
						});
						
						getUI().addWindow(cEditor);
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um CEP", Type.ERROR_MESSAGE);
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
				
					CepEditor cEditor = new CepEditor(tb.getItem(tb.getValue()), "Editar CEP", true);
					cEditor.addListerner(new CepEditor.CepListerner() {
						
						@Override
						public void onClose(CepEvent event) {
							if(event.isConfirm()){
								//containerCeps.commit();
								tb.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um CEP"));
							}
						}
					});
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
							//refresh();
						}
					});
					
					getUI().addWindow(cEditor);
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Editar um CEP", Type.ERROR_MESSAGE);
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
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Cep Selecionado?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									containerCeps.removeItem(tb.getValue());
									//containerCeps.commit();
									Notification.show("Cep Excluido com Sucesso!");
									tb.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um CEP"));
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
					Notification.show("Atenção", "Você não Possui Permissão para Excluir um CEP", Type.ERROR_MESSAGE);
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
