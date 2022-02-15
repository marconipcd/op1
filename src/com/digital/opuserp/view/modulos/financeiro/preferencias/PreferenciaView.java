	package com.digital.opuserp.view.modulos.financeiro.preferencias;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.financeiro.preferencias.DefinirTabelaPrecoEditor.DefinirTabelaPrecoEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class PreferenciaView extends VerticalLayout {

	JPAContainer<Cliente> container;
	
	Table tb;
	TextField tfBusca;
	Button btDefinirTabelaPreco;
	Button btDefinirTaxaBoleto;
	Button btDefinirLimiteCredito;
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	
	
	public PreferenciaView(boolean act){
		
		if(act){		
		setSizeFull();
		gmDAO = new GerenciarModuloDAO();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(BuildbtDefinirTabelaPreco());
		hlButons.addComponent(BuildbtDefinirTaxaBoleto());
		hlButons.addComponent(BuildbtDefinirLimiteCredito());
		
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
		hlFloat.addComponent(buildlbRegistros());		
		hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			
		addComponent(hlFloat);
		
		setExpandRatio(tb, 1);
		}
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
	}
	
	
	
	public Label buildlbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	
	
	public JPAContainer<Cliente> buildContainer(){
		container = new JPAContainerFactory().makeBatchable(Cliente.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.removeAllContainerFilters();
		container.setApplyFiltersImmediately(false);
		container.addNestedContainerProperty("tabela_preco.nome");
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		container.applyFilters();
	
		
		container.sort(new String[] {"id"}, new boolean[]{false});
		return container;
		
		
	}
	
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				} else{
					return super.formatPropertyValue(rowId, colId, property);
				}
								
				return "";			
			}
		};		
		tb.setSizeFull();
		tb.setSelectable(true);
		
		tb.setColumnCollapsingAllowed(true);
						
		tb.setVisibleColumns(new Object[] {"id","nome_razao","nome_fantasia","doc_cpf_cnpj","tabela_preco.nome"});

		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome_razao", "Nome / Razão");
		tb.setColumnHeader("nome_fantasia", "Fantasia");	
		tb.setColumnHeader("doc_cpf_cnpj", "CPF/CNPJ");
		tb.setColumnHeader("tabela_preco.nome", "Tabela de Preço");
				
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btDefinirTabelaPreco.setEnabled(true);
					btDefinirTaxaBoleto.setEnabled(true);
					btDefinirLimiteCredito.setEnabled(true);
				}else{
					btDefinirTabelaPreco.setEnabled(false);
					btDefinirTaxaBoleto.setEnabled(false);
					btDefinirLimiteCredito.setEnabled(false);
				}
			}
		});
		
		tb.setColumnCollapsible("nome", false);
		//tb.setConverter("id", null);
		
		
		
		
		return tb;
	}
	

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


	public void addFilter(String s) {
		
		container.removeAllContainerFilters();
		container.getEntityProvider().setQueryModifierDelegate(null);
		container.setApplyFiltersImmediately(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));		
		
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.valueOf(s);
			
			for(Object c:collums){		 			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class  && !c.toString().equals("tabela_preco.nome")){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class  && !c.toString().equals("tabela_preco.nome")){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
			
		} catch (Exception e) {
						
			for(Object c:collums){		 
				
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class && !c.toString().equals("tabela_preco.nome")){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}			
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));		
		container.applyFilters();
		container.sort(new String[] {"id"}, new boolean[]{false});
		hlFloat.replaceComponent(lbRegistros, buildlbRegistros());
	}


	public Button BuildbtDefinirTabelaPreco() {
		btDefinirTabelaPreco = new Button("Definir Tabela de Preço", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Definir Tabela de Preço"))				
				{
				
					DefinirTabelaPrecoEditor defTabelaPreco = new DefinirTabelaPrecoEditor(tb.getItem(tb.getValue()), "Definir Tabela de Preço", true);
					defTabelaPreco.addListerner(new DefinirTabelaPrecoEditor.DefinirTabelaPrecoListerner() {
						
						@Override
						public void onClose(DefinirTabelaPrecoEvent event) {
							if(event.isConfirm()){
								
								//b.commit();
								container.commit();
								//container.refresh();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Definiu Uma Tabela de Preço"));
							}
						}
					});
					
					
					defTabelaPreco.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();						
						}
					});
					
					
					getUI().addWindow(defTabelaPreco);		
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Definir Tabela de Preço", Type.ERROR_MESSAGE);
				}
				
			}
		});
		btDefinirTabelaPreco.setEnabled(false);
		return btDefinirTabelaPreco;
	}

	
	public Button BuildbtDefinirTaxaBoleto() {
		btDefinirTaxaBoleto = new Button("Definir Taxa de Boleto", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Definir Taxa Boleto"))				
				{
					DefinirTaxaBoletoEditor defTaxaBoleto = new DefinirTaxaBoletoEditor((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue(), 
							"Taxa de Boletos", true);
					
					
					defTaxaBoleto.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();						
						}
					});
					getUI().addWindow(defTaxaBoleto);
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Definir Taxa de Boleto", Type.ERROR_MESSAGE);
				}

			}
		});
		btDefinirTaxaBoleto.setEnabled(false);
					
		return btDefinirTaxaBoleto;
	}

	
	public Button BuildbtDefinirLimiteCredito() {
		btDefinirLimiteCredito = new Button("Definir Limite de Crédito", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Definir Limite de Crédito"))				
				{
					DefinirLimiteCreditoEditor defLimiteCredito = new DefinirLimiteCreditoEditor(
							(Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue(), "Definir Limite de Crédito", true);
					
					defLimiteCredito.addCloseListener(new Window.CloseListener() {
					
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();						
						}
					});
				
					getUI().addWindow(defLimiteCredito);
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Definir Limite de Crédito", Type.ERROR_MESSAGE);
				}
			}
		});
		btDefinirLimiteCredito.setEnabled(false);
		return btDefinirLimiteCredito;
	}

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}

