package com.digital.opuserp.view.modulos.cadastro.setores;

import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.SetoresDAO;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.setores.SetoresEditor.SetoresSavedEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class SetoresView extends VerticalLayout {
	
	//
	TextField tfBusca;
	TreeTable tb;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	
	
	JPAContainer<Setores> containerSetores;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	
	GerenciarModuloDAO gmDAO;
	
	public SetoresView(boolean act){
		
		if(act){
		setSizeFull();
		
		
		gmDAO = new GerenciarModuloDAO();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(BuildbtNovo());
		hlButons.addComponent(BuildbtEditar());
		hlButons.addComponent(BuildbtExcluir());
		
		addComponent(hlButons);
		setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(buildTfbusca());		
		addComponent(buildtb());
		
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
		replaceComponent(tb, buildtb());
		setExpandRatio(tb, 1);	
		//addFilter(tfBusca.getValue());
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(tb.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof SetoresView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
//			if(btEditar != null || slEditar != null){
//				btEditar.removeShortcutListener(slEditar);			
//			}
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
		slEditar = new ShortcutListener("Buscar",ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				refresh();
			}
		};
		return slEditar;
	}

	
		
	
	public Table buildtb() {
		
		tb = new TreeTable();
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setMultiSelect(false);
		tb.setImmediate(true);
		
		tb.addContainerProperty("Cod", Integer.class, "");
		tb.addContainerProperty("Nome", String.class, "");
		
		tb.setVisibleColumns(new Object[]{"Cod","Nome"});
		tb.setColumnCollapsed("Cod", true);
	
		
		List<Setores> setores = SetoresDAO.getSetoresRoot(tfBusca.getValue());
		
		Integer i = 0;
		Integer rootPai;
		Integer rootFilho;
		Integer rootNeto;
		Integer rootBisNeto;
		Integer rootTataraneto;
		
		for (Setores setor : setores) {
			tb.addItem(new Object[]{setor.getId(), setor.getNome()}, i);
			
			List<Setores> setores_filhos = SetoresDAO.getSetoresChild(setor);
			if(setores_filhos.size() > 0){
				tb.setChildrenAllowed(i, true);
				rootPai = i;
				for (Setores setores2 : setores_filhos) {
					i++;
					tb.addItem(new Object[]{setores2.getId(), setores2.getNome()}, i);
					tb.setParent(i, rootPai);
					
					List<Setores> setores_netos = SetoresDAO.getSetoresChild(setores2);
					if(setores_netos.size()>0){
						tb.setChildrenAllowed(i, true);
						rootFilho = i;
						
						for (Setores setores3 : setores_netos) {
							i++;
							tb.addItem(new Object[]{setores3.getId(), setores3.getNome()}, i);
							tb.setParent(i, rootFilho);
							
							List<Setores> setores_bisnetos = SetoresDAO.getSetoresChild(setores3);
							
							if(setores_bisnetos.size()>0){
								tb.setChildrenAllowed(i, true);
								rootNeto = i;
								
								for (Setores setores4 : setores_bisnetos) {
									i++;
									tb.addItem(new Object[]{setores4.getId(), setores4.getNome()}, i);
									tb.setParent(i, rootNeto);
									
									List<Setores> setores_tataranetos = SetoresDAO.getSetoresChild(setores4);
									
									if(setores_tataranetos.size() >0){
										tb.setChildrenAllowed(i, true);
										rootBisNeto = i;
										
										for (Setores setores5 : setores_tataranetos) {
											i++;
											tb.addItem(new Object[]{setores5.getId(), setores5.getNome()}, i);
											tb.setChildrenAllowed(i, false);
											tb.setParent(i, rootBisNeto);
										}
									}else{
										tb.setChildrenAllowed(i, false);
									}
								}
							}else{
								tb.setChildrenAllowed(i, false);
							}
						}
					}else{
						tb.setChildrenAllowed(i, false);
					}
				}
			}else{
				tb.setChildrenAllowed(i, false);
			}
			
			i++;
		}
		
		
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
					
				if(tb.getValue() != null){
					btEditar.setEnabled(true);
					btExcluir.setEnabled(true);
				}else{
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
				}
				
			}
		});
		
		
		
		
		return tb;
	}

	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.setImmediate(true);
		tfBusca.addShortcutListener(buildShortCutEditar());
		
		return tfBusca;
	}

	public void addFilter(Integer s) {
		// TODO Auto-generated method stub
		
	}

	
	public void addFilter(String s) {
		refresh();
	}

	
	public Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
			{
				
				
				SetoresEditor editor = new SetoresEditor(null,null, "Cadastrar Setor");
				
				editor.addListener(new SetoresEditor.SetoresSavedListener() {
					
					@Override
					public void setoresSaved(SetoresSavedEvent event) {
						if(event.isConfirm()){
							try {
								Setores setor = new Setores();
								setor.setId(event.getId());
								setor.setNome(event.getNome());
								setor.setSetor_root(event.getSetorRoot());
								setor.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
								
								SetoresDAO.commit(setor);
								
								//refresh();
								
								//LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Setor"));
							} catch (Exception e) {
								e.printStackTrace();
								Notification.show("ERRO: "+e.getMessage());
							}
						}else{
							
						}
					}
				});
				
				editor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();
						//refresh();
					}
				});
				
				getUI().addWindow(editor);
				editor.center();
				editor.setModal(true);
				
				}else{
					Notify.Show("Você não Possui Permissão para Cadastrar um Setor", Notify.TYPE_ERROR);
				}
				
				
			}
		});		
					
		return btNovo;
	}

	
	public Button BuildbtEditar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
				{
				
				Integer id = (Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue();
				String nome = tb.getItem(tb.getValue()).getItemProperty("Nome").getValue().toString();
				
				SetoresEditor editor = new SetoresEditor(id,nome, "Editar Setor");		
				
				editor.addListener(new SetoresEditor.SetoresSavedListener() {
					
					@Override
					public void setoresSaved(SetoresSavedEvent event) {
						if(event.isConfirm()){
							Setores setor = new Setores();
							setor.setId(event.getId());
							setor.setNome(event.getNome());
							setor.setSetor_root(event.getSetorRoot());
							setor.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
							
							SetoresDAO.commit(setor);
							
							//refresh();
							
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Setor"));
						}
					}
				});
				
				editor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();
						//refresh();
					}
				});
				
				getUI().addWindow(editor);
				editor.center();
				editor.setModal(true);
				
				}else{
					Notify.Show("Você não Possui Permissão para Editar um Setor", Notify.TYPE_ERROR);
				}
				
			}
		});
		btEditar.setEnabled(false);
		
	
		
		return btEditar;
	}

	
	public Button BuildbtExcluir() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Setor Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								SetoresDAO.remove((Integer)tb.getItem(tb.getValue()).getItemProperty("Cod").getValue());
								Notify.Show("Setor Excluído com Sucesso!", Notify.TYPE_SUCCESS);
								//refresh();
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
					Notify.Show("Você não Possui Permissão para Excluir um Setor", Notify.TYPE_ERROR);
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
