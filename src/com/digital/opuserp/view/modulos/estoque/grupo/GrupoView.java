package com.digital.opuserp.view.modulos.estoque.grupo;

import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.GrupoProdutoDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.estoque.grupo.GrupoEditor.GrupoEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class GrupoView extends VerticalLayout implements GenericView {

	
	
	private Table tb;
	private TextField tfBusca;
	private Button btNovo;
	private Button btEditar;
	private Button btExcluir;
	private Label lbRegistros;

	private HorizontalLayout hlFloat;	
	
	private ShortcutListener slNovo;
	private ShortcutListener slBusca;
	private ShortcutListener slEditar;
	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	
	public GrupoView(boolean act){		
		
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
					addComponent(buildTfbusca());
					addComponent(buildBtBuscar());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(lbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
			lbLegend.setWidth("250px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1);
		}
	}
	
	private Button buildBtBuscar(){
		Button bt = new Button("Buscar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				atualizarTabela();
			}
		});
		
		bt.setStyleName(Reindeer.BUTTON_SMALL);
		
		return bt;
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);	
		addFilter(tfBusca.getValue());
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof GrupoView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
			//tfBusca.addShortcutListener(buildShortCutEnterBusca());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btEditar != null || slEditar != null){
				btEditar.removeShortcutListener(slEditar);			
			}
		}
	}
	
	public Label lbRegistros(){
		String qtd = String.valueOf(tb != null ? tb.getItemIds().size() : 0);
		lbRegistros = new Label(String.valueOf(qtd+" Registros Encontrados"));
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
	

	@Override
	public Table buildTbGeneric() {
		tb = new Table();		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("id", true);				
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome_grupo", "Nome");	
		
		//tb.setVisibleColumns(new Object[] {"id","nome_grupo"});
		
		tb.addContainerProperty("id", Integer.class, 0);
		tb.addContainerProperty("nome_grupo", String.class, "");
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditar.setEnabled(true);
					btExcluir.setEnabled(true);
				}else{
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
				}	
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
		tb.setColumnCollapsible("nome_grupo", false);
		
		
		tb.sort(new Object[]{"nome_grupo"}, new boolean[]{true});
		
		
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()   && tb.getValue() != null){
					btEditar.click();
				}				
			}
		});
		

		atualizarTabela();
		
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
				atualizarTabela();
			}
		});
		return tfBusca;
	}

	

	@Override
	public void addFilter(String s) {
		
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
					
										
					GrupoProduto grupo = new GrupoProduto(null, OpusERP4UI.getEmpresa().getId(), "");
					grupo.setStatus("EM DIGITACAO");
					
					GrupoProdutoDAO.save(grupo);
								
					final GrupoEditor cEditor = new GrupoEditor(grupo, "Cadastrar Grupo", true);
					cEditor.addListerner(new GrupoEditor.GrupoListerner() {
						
						@Override
						public void onClose(GrupoEvent event) {
							if(event.isConfirm()){
								
								try {
									GrupoProdutoDAO.save(event.getGrupoProduto());
									atualizarTabela();
									
									cEditor.close();
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Grupo de Produto"));
									
									Notify.Show("Grupo Cadastrado Com Sucesso!", Notify.TYPE_SUCCESS);
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
							//refresh();
						}
					});
					
					getUI().addWindow(cEditor);
				
				}else{					
					Notify.Show("Você não Possui Permissão para Cadastrar um Grupo", Notify.TYPE_ERROR);
				}
			}
		});

		return btNovo;
	}
	
	private void atualizarTabela(){
		List<GrupoProduto> grupos = GrupoProdutoDAO.getGrupos(tfBusca.getValue());
		
		tb.removeAllItems();
		for (GrupoProduto f: grupos) {
			tb.addItem(new Object[]{
					f.getId() != null ? f.getId() : 0, 
					f.getNome_grupo() != null ? f.getNome_grupo() : "",}, tb.getItemIds().size()+1);
		}
	
	}

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
				{
				
					Integer codGrupo = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
					GrupoProduto grupo = GrupoProdutoDAO.find(codGrupo);
					
					final GrupoEditor cEditor = new GrupoEditor(grupo, "Editar Grupo", true);
					cEditor.addListerner(new GrupoEditor.GrupoListerner() {
						
						@Override
						public void onClose(GrupoEvent event) {
							if(event.isConfirm()){
								GrupoProdutoDAO.save(event.getGrupoProduto());	
								atualizarTabela();
								
								cEditor.close();
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Grupo de Produto"));

								Notify.Show("Grupo Alterada Com Sucesso!", Notify.TYPE_SUCCESS);
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
					Notify.Show("Você não Possui Permissão para Editar um Grupo", Notify.TYPE_ERROR);
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
				
					boolean allow = false;				
					if (allow){
						
						Notify.Show("Grupo em Uso não pode ser excluida!", Notify.TYPE_NOTICE);					
					
					}else{					
					
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a Grupo Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								
								Integer codProduto = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
								GrupoProduto grupo = GrupoProdutoDAO.find(codProduto);
								
								GrupoProdutoDAO.removeGrupo(grupo);
								
								Notify.Show("Grupo Excluído com Sucesso!", Notify.TYPE_SUCCESS);
								tb.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Grupo"));
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
				}else{
					Notify.Show("Você não Possui Permissão para Excluir um Grupo", Notify.TYPE_ERROR);
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
