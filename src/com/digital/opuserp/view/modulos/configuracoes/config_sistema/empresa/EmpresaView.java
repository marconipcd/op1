package com.digital.opuserp.view.modulos.configuracoes.config_sistema.empresa;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Transportadoras;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class EmpresaView extends VerticalLayout implements GenericView{

	//
	JPAContainer<Empresa> container;
	
	Table tb;
	TextField tfBusca;
	ComboBox cbStatus;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	
	
	private Label lbRegistros;
	
	//ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	public EmpresaView(boolean act){
		
		
		if(act){
		setSizeFull();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		//hlButons.addComponent(BuildbtNovo());
		hlButons.addComponent(BuildbtAceitar());
		//hlButons.addComponent(BuildbtExcluir());
		
		addComponent(hlButons);
		setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(buildComboStatus());
				addComponent(buildTfbusca());
				setExpandRatio(tfBusca, 1.0f);
			}
		});
		
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
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");		
		return lbRegistros;
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof EmpresaView)
		{
			//btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
//			if(btNovo != null || slNovo != null){				
//				btNovo.removeShortcutListener(slNovo);
//			}
			
			if(btEditar != null || slEditar != null){
				btEditar.removeShortcutListener(slEditar);			
			}
		}
	}
//	public ShortcutListener buildShortCutNovo(){
//		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
//			
//			@Override
//			public void handleAction(Object sender, Object target) {
//				btNovo.click();
//			}
//		};
//		return slNovo;
//	}
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btEditar.click();
			}
		};
		return slEditar;
	}
	
	public JPAContainer<Empresa> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Empresa.class, ConnUtil.getEntity());
		container.addNestedContainerProperty("cnae_fiscal.descricao");
		container.setAutoCommit(false);				
		if(cbStatus.getValue().toString() != null){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		}
				
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());
		tb.setSizeFull();		
		tb.setColumnCollapsingAllowed(true);
		tb.setVisibleColumns(new Object[]{"id","cnpj","razao_social", "nome_fantasia","cep", "cidade","endereco","numero","complemento","bairro",
				"uf","pais","referencia","status",
				"responsavel","cnae_fiscal.descricao","inscricao_estadual","inscricao_municipal","socio1","socio2","fone1","fone2","fone3","_0800","email","site"});
		
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("ramo_atividade.nome", "Ramo de Atividade");
		tb.setColumnHeader("cnpj", "CNPJ");
		tb.setColumnHeader("razao_social", "Razão Social");		
		tb.setColumnHeader("nome_fantasia", "Fantasia");		
		tb.setColumnHeader("cep", "CEP");
		tb.setColumnHeader("cidade", "Cidade");
		tb.setColumnHeader("endereco", "Endereço");
		tb.setColumnHeader("numero", "Número");
		tb.setColumnHeader("bairro", "Bairro");		
		tb.setColumnHeader("uf", "UF");
		tb.setColumnHeader("pais", "País");
		tb.setColumnHeader("complemento", "Complemento");
		tb.setColumnHeader("referencia", "Referência");
		tb.setColumnHeader("responsavel", "Responsável");
		tb.setColumnHeader("cpfResponsavel", "CPF Responsável");				
		tb.setColumnHeader("fone1", "Telefone Principal");
		tb.setColumnHeader("fone2", "Telefone Alternativo 1");
		tb.setColumnHeader("fone3", "Telefone Alternativo 2");
		tb.setColumnHeader("_0800", "Telefone 0800");
		tb.setColumnHeader("email", "Email");
		tb.setColumnHeader("site", "Site");
		tb.setColumnHeader("status", "Status");
		tb.setColumnHeader("inscricao_estadual", "Inscrição Estadual");
		tb.setColumnHeader("inscricao_municipal", "Inscrição Municipal");
		tb.setColumnHeader("socio1", "Sócio 1");
		tb.setColumnHeader("socio2", "Sócio 2");
		tb.setColumnHeader("cnae_fiscal.descricao", "Atividade");		
		
		tb.setColumnCollapsed("endereco", true);
		tb.setColumnCollapsed("numero", true);
		tb.setColumnCollapsed("complemento", true);
		tb.setColumnCollapsed("bairro", true);		
		tb.setColumnCollapsed("uf", true);
		tb.setColumnCollapsed("referencia", true);
		tb.setColumnCollapsed("responsavel", true);
		tb.setColumnCollapsed("cpfResponsavel", true);
		tb.setColumnCollapsed("cenae_fiscal", true);
		tb.setColumnCollapsed("inscricao_estadual", true);
		tb.setColumnCollapsed("inscricao_municipal", true);
		tb.setColumnCollapsed("socio1", true);
		tb.setColumnCollapsed("socio2", true);
		tb.setColumnCollapsed("cpfSocio1", true);
		tb.setColumnCollapsed("cpfSocio2", true);
		tb.setColumnCollapsed("fone1", true);
		tb.setColumnCollapsed("fone2", true);
		tb.setColumnCollapsed("_0800", true);
		tb.setColumnCollapsed("email", true);
		tb.setColumnCollapsed("site", true);
		tb.setColumnCollapsed("pais", true);
		tb.setColumnCollapsed("fone3", true);
		tb.setColumnCollapsed("cep", true);
		tb.setColumnCollapsible("id",false);
		
		
		
		tb.setSelectable(true);
//		tb.setMultiSelect(true);
		tb.setImmediate(true);
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditar.setEnabled(true);
				}else{
					btEditar.setEnabled(false);
				}
			}
		});
		
		
		tb.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()  && tb.getValue() != null){
					
					EmpresaEditor cEditor = new EmpresaEditor(tb.getItem(tb.getValue()), "Editar Empresa", true);
					cEditor.addListener(new EmpresaEditor.EmpresaSavedListener() {
						
						@Override
						public void empresaSaved(
								com.digital.opuserp.view.modulos.configuracoes.config_sistema.empresa.EmpresaEditor.EmpresaSavedEvent event) {
							
							if(event.isConfirm()){
								container.commit();
								
								
//								EntityManager em = ConnUtil.getEntityManager();
//								Empresa e = em.find(Empresa.class, event.getEventoItem().getItemProperty("id").getValue());
//								e.setCenae_fiscal(event.getCnae());
//								em.getTransaction().begin();
//								em.merge(e);
//								em.getTransaction().commit();
								
								
								
								tb.focus();
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
	
	public ComboBox buildComboStatus(){
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
		
		container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
		
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final BeanItem<Transportadoras> newItem = new BeanItem<Transportadoras>(new Transportadoras());
								
//				EmpresaEditor_beta cEditor = new EmpresaEditor_beta(newItem, "Editar Empre", true);
//				cEditor.addListerner(new EmpresaSa);
				
				//getUI().addWindow(cEditor);
			}
		});
		return btNovo;
	}

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				EmpresaEditor cEditor = new EmpresaEditor(tb.getItem(tb.getValue()), "Editar Empresa", true);
				cEditor.addListener(new EmpresaEditor.EmpresaSavedListener() {
					
					@Override
					public void empresaSaved(
							com.digital.opuserp.view.modulos.configuracoes.config_sistema.empresa.EmpresaEditor.EmpresaSavedEvent event) {
						
						if(event.isConfirm()){
							container.commit();
							
							
//							EntityManager em = ConnUtil.getEntityManager();
//							Empresa e = em.find(Empresa.class, event.getEventoItem().getItemProperty("id").getValue());
//							e.setCenae_fiscal(em.find(Cnae.class, event.getCnae().getId()));
//							em.getTransaction().begin();
//							em.merge(e);
//							em.getTransaction().commit();
							
							
							tb.focus();
						}
						
					}
				});
				
				cEditor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();
						refresh();
					}
				});
				
				getUI().addWindow(cEditor);
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
				GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a Transportadora Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
//							if(event.isConfirm()){
//								container.removeItem(tb.getValue());
//								container.commit();
//							}							
						}
					});
					
					gDialog.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
							refresh();
						}
					});
				
				getUI().addWindow(gDialog);
			}
		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}
	

	
	
	
}