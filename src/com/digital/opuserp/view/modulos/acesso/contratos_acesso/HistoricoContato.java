package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;

public class HistoricoContato extends Window {

	JPAContainer<Crm> container;
	
	TextField tfBusca;
	Table tbChamados;
	Button btSalvar;
	Button btCancelar;
	
	//Integer codCliente;
	AcessoCliente codContrato;
	
	public HistoricoContato(boolean modal, boolean center, AcessoCliente codContrato){
		
		super("Histórico de Contato");
		
		//this.codCliente = codCliente;
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
	
	public JPAContainer<Crm> buildJpaContainer(){
		container = JPAContainerFactory.make(Crm.class, ConnUtil.getEntity());
		//container.addContainerFilter(Filters.eq("cliente", new Cliente(codCliente)));
		container.addContainerFilter(Filters.eq("contrato", codContrato));
		container.sort(new String []{"id"}, new boolean[] {false});
		
		//container.addNestedContainerProperty("tipo_subgrupo.nome");
		//container.addNestedContainerProperty("grupo.nome");
		//container.addNestedContainerProperty("subgrupo.nome");
		container.addNestedContainerProperty("crm_assuntos.nome");
		container.addNestedContainerProperty("setor.nome");
		
		
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
		tbChamados.setColumnHeader("id", "Protocolo");
		tbChamados.setColumnHeader("data_efetuado", "Data");
		tbChamados.setColumnHeader("data_agendado", "Data Previsão");
		tbChamados.setColumnHeader("setor.nome", "Setor");
		tbChamados.setColumnHeader("crm_assuntos.nome", "Assunto");
		tbChamados.setColumnHeader("operador_tratamento", "Operador");
		tbChamados.setColumnHeader("plano", "Plano");
		tbChamados.setColumnHeader("concentrador", "Concentrador");
		tbChamados.setColumnHeader("status", "Status");		
		
		tbChamados.setVisibleColumns(new Object[]{"id","data_efetuado","data_agendado","setor.nome","crm_assuntos.nome","operador_tratamento","plano","concentrador","status"});
		
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
		container.addContainerFilter(Filters.eq("contrato",codContrato));
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
					
					
					final TextArea taObs = new TextArea();
					if(tbChamados.getItem(tbChamados.getValue()).getItemProperty("conteudo").getValue() != null){
						taObs.setValue(tbChamados.getItem(tbChamados.getValue()).getItemProperty("conteudo").getValue().toString());
					}
					taObs.setReadOnly(true);
					taObs.setSizeFull();
					taObs.addStyleName("uppercase");
					
					
					
					
					win.setContent(new VerticalLayout(){
						{
							setMargin(true);
							setSizeFull();
							addComponent(taObs);
							
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
