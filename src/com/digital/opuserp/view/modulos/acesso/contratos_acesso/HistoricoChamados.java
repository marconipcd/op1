package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HistoricoChamados extends Window {

	JPAContainer<Ose> container;
	
	TextField tfBusca;
	Table tbChamados;
	Button btSalvar;
	Button btCancelar;
	
	AcessoCliente codContrato;
	
	public HistoricoChamados(boolean modal, boolean center, AcessoCliente codContrato){
		
		super("Histórico de Chamados");
		
		this.codContrato = codContrato;
		setWidth("964px");
		setHeight("360px");
		
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
						
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
								
				addComponent(buildTextField());
				addComponent(buildTbChamados());
				setExpandRatio(tbChamados, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtVisualizar());
				hlButtons.addComponent(buildBtCancelar());
				//hlButtons.addComponent(buildBtSalvar());
								
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}

		});
	}
	Integer codCliente;
	public HistoricoChamados(boolean modal, boolean center, Integer codCliente){
		
		super("Histórico de Chamados");
		
		this.codCliente = codCliente;
		setWidth("964px");
		setHeight("360px");
		
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
						
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
								
				addComponent(buildTextField());
				addComponent(buildTbChamados());
				setExpandRatio(tbChamados, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtVisualizar());
				hlButtons.addComponent(buildBtCancelar());
				//hlButtons.addComponent(buildBtSalvar());
								
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}

		});
	}
	
	public JPAContainer<Ose> buildJpaContainer(){
		container = JPAContainerFactory.make(Ose.class, ConnUtil.getEntity());
		
		if(codContrato != null && codCliente == null){
			container.addContainerFilter(Filters.eq("contrato", codContrato));
		}
		
		if(codContrato == null && codCliente != null){
			container.addContainerFilter(Filters.eq("cliente", new Cliente(codCliente)));
		}
			
		
		container.sort(new String []{"id"}, new boolean[] {false});
		
		container.addNestedContainerProperty("tipo_subgrupo.nome");
		container.addNestedContainerProperty("grupo.nome");
		container.addNestedContainerProperty("subgrupo.nome");
		container.addNestedContainerProperty("tipo_subgrupo.nome");
		
		return container;
	}
	
	public Table buildTbChamados(){
		tbChamados = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
				
				if (v instanceof Date) {
				         Date dateValue = (Date) v;
			        	 return new SimpleDateFormat("dd/MM/yyyy").format(dateValue);
				}		
				
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};
		tbChamados.setWidth("100%");
		tbChamados.setHeight("230px");
		tbChamados.setSelectable(true);
		tbChamados.setColumnHeader("id", "Código");
		tbChamados.setColumnHeader("grupo.nome", "Grupo");
		tbChamados.setColumnHeader("tipo_subgrupo.nome", "Tipo");
		tbChamados.setColumnHeader("subgrupo.nome", "Subgrupo");
		tbChamados.setColumnHeader("data_ex", "Data Previsão");
		tbChamados.setColumnHeader("data_conclusao", "Data Conclusão");
		tbChamados.setColumnHeader("tecnico", "Técnico");
		tbChamados.setColumnHeader("operador_abertura", "Operador");
//		
		tbChamados.setColumnHeader("status", "Status");
		
		
		tbChamados.setVisibleColumns(new Object[]{"id","grupo.nome","subgrupo.nome","tipo_subgrupo.nome","data_ex","data_conclusao","tecnico","operador_abertura","status"});
		
		
		return tbChamados;
	}
	
	
	public TextField buildTextField() {
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
	

	public void addFilter(String s) {
		container.removeAllContainerFilters();
		
		if(codContrato != null && codCliente == null){
			container.addContainerFilter(Filters.eq("contrato", codContrato));
		}
		
		if(codContrato == null && codCliente != null){
			container.addContainerFilter(Filters.eq("cliente", new Cliente(codCliente)));
		}
		
		container.addContainerFilter(
				Filters.or(
						Filters.like("tipo", "%"+s+"%", false),
						Filters.like("data_ex", "%"+s+"%", false),
						Filters.like("tecnico", "%"+s+"%", false),
						Filters.like("operador", "%"+s+"%", false),						
						Filters.like("status", "%"+s+"%", false)));
		container.applyFilters();
	}
	
	private Button btVisualizar;
	private Button buildBtVisualizar(){
		btVisualizar = new Button("Visualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(tbChamados.getValue() != null){
					final Window win = new Window("Observação");
					win.setWidth("459px");
					win.setHeight("320px");
					win.setResizable(false);
					win.setModal(true);
					win.addShortcutListener(new ShortcutListener("ESC", ShortcutAction.KeyCode.ESCAPE, null) {
						
						@Override
						public void handleAction(Object sender, Object target) {
							win.close();							
						}
					});
					
					
					final TextArea taObs = new TextArea("Observação");
					if(tbChamados.getItem(tbChamados.getValue()).getItemProperty("obs").getValue() != null){
						taObs.setValue(tbChamados.getItem(tbChamados.getValue()).getItemProperty("obs").getValue().toString());
					}
					taObs.setSizeFull();
					taObs.addStyleName("uppercase");
					
					
					final TextArea ta = new TextArea("Conclusão");
					if(tbChamados.getItem(tbChamados.getValue()).getItemProperty("conclusao").getValue() != null){
						ta.setValue(tbChamados.getItem(tbChamados.getValue()).getItemProperty("conclusao").getValue().toString());
					}
					ta.setSizeFull();
					ta.addStyleName("uppercase");
					
					win.setContent(new VerticalLayout(){
						{
							setMargin(true);
							setSizeFull();
							addComponent(taObs);
							addComponent(ta);
						}
					});
					
					getUI().addWindow(win);
					win.center();
					win.focus();
				}
			}
		});
		
		return btVisualizar;
	}
		
	private Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}	
		});
		
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}

}
