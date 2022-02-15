package com.digital.opuserp.view.modulos.configuracoes.config_acesso.materiais;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
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

public class ConfigMaterialAcessoView extends VerticalLayout implements GenericView {

	JPAContainer<Empresa> container;
	
	Table tb;
	TextField tfBusca;
	Button btDefinirMaterial;
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
		
	ShortcutListener slEditar;
	
	public ConfigMaterialAcessoView(){		
		
		setSizeFull();
				
		HorizontalLayout hlButons = new HorizontalLayout();		
		hlButons.addComponent(BuildbtDefinir());
		
		addComponent(hlButons);
		setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
				addComponent(buildTfbusca());
				setExpandRatio(tfBusca, 1.0f);
			}
		});
		
		addComponent(buildTbGeneric());
		
		hlFloat = new HorizontalLayout();
		hlFloat.setWidth("100%");
		hlFloat.addComponent(lbRegistros());
		Label lbLegend = new Label("ENTER - Definir Material Padrão");
		lbLegend.setWidth("250px");
		hlFloat.addComponent(lbLegend);
		hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
		hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
		
		
		addComponent(hlFloat);
		
		setExpandRatio(tb, 1);
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ConfigMaterialAcessoView)
		{
			///btNovo.addShortcutListener(buildShortCutNovo());
			//btEditar.addShortcutListener(buildShortCutEditar());
		}else{
			//btNovo.removeShortcutListener(slNovo);
			//btEditar.removeShortcutListener(slEditar);
		}
	}
	
	public Label lbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	
	public ShortcutListener buildShortCutDefinir(){
		slEditar = new ShortcutListener("Definir Material",ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btDefinirMaterial.click();
			}
		};
		return slEditar;
	}
	public JPAContainer<Empresa> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Empresa.class, ConnUtil.getEntity());		
		container.setReadOnly(true);				
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(false);		
		tb.setColumnHeader("nome_fantasia", "Empresa");	
		
		tb.setVisibleColumns(new Object[] {"nome_fantasia"});
		
		tb.setImmediate(true);
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){					
					btDefinirMaterial.setEnabled(true);
				}else{					
					btDefinirMaterial.setEnabled(false);
				}
			}
		});
		
		tb.setColumnCollapsible("id", false);
				
		
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
		container.removeAllContainerFilters();
		container.addContainerFilter(		
						new Like("nome_fantasia", "%"+s+"%", false));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		

		return null;
	}

	@Override
	public Button BuildbtAceitar() {
		return null;
	}

	
	public Button BuildbtDefinir() {
		btDefinirMaterial = new Button("Definir Material Acesso", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(tb.getValue() != null){
					Integer codEmpresa = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
					ConfigMaterialAcessoEditor configMAterialAcesso = new ConfigMaterialAcessoEditor("Definir Grupo de Material", true, codEmpresa);
					
					
					getUI().addWindow(configMAterialAcesso);
				}
			}
		});
		btDefinirMaterial.setEnabled(false);
		return btDefinirMaterial;
	}

	

	@Override
	public Button BuildbtCancelar() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
