package com.digital.opuserp.view.modulos.crm.historico;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.CrmDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.CrmFormasContato;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HistoricoView extends Window {

	JPAContainer<Crm> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;
	Button btVisualizar;

	private Integer codCliente;
	
	private Label lbRegistros;
	HorizontalLayout hlFloat;
	
	Setores setor;
	AcessoCliente contrato;
	public HistoricoView(boolean modal, boolean center, Integer codCliente, Setores setor, AcessoCliente contrato){
		
		
		
		super("Histórico de Contatos");
		addStyleName("no-scrool");
		this.codCliente = codCliente;
		this.setor = setor;
		this.contrato = contrato;
		
		setWidth("830px");
		setHeight("360px");
		setIcon(new ThemeResource("icons/search-32.png"));
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
				addComponent(buildTb());
				setExpandRatio(tb, 2);

				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);	

				hlButtons.setStyleName("hl_buttons_bottom");
				
				hlButtons.addComponent(buildBtVisualizar());
				hlButtons.addComponent(buildBtFechar());
				
				
				hlFloat = new HorizontalLayout();
				hlFloat.setStyleName("horizontal-hlFloatUtil");
				hlFloat.addComponent(buildLbRegistros());
				hlFloat.setComponentAlignment(lbRegistros, Alignment.MIDDLE_LEFT);

				HorizontalLayout hlRoor = new HorizontalLayout();
				hlRoor.setWidth("100%");
				hlRoor.setStyleName("horizontal-hlRootUtil");
				hlRoor.addComponent(hlFloat);
				hlRoor.setComponentAlignment(hlFloat, Alignment.TOP_LEFT);
				hlRoor.addComponent(hlButtons);
				hlRoor.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			
				addComponent(hlRoor);
			}
		});
	}
		
	public JPAContainer<Crm> buildJpaContainer(){
		container = JPAContainerFactory.make(Crm.class, ConnUtil.getEntity());
		
		container.addNestedContainerProperty("setor.nome");
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("cliente.contato");
		container.addNestedContainerProperty("crm_assuntos.nome");
		container.addNestedContainerProperty("contrato.id");
		
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		if(codCliente != null){
			container.addContainerFilter(Filters.eq("cliente", ClienteDAO.find(codCliente)));				
		}
		
		if(setor != null){
			container.addContainerFilter(Filters.eq("setor", setor));
		}
		
		if(contrato != null){
			container.addContainerFilter(Filters.eq("contrato", contrato));
		}
		
		container.sort(new String[]{"id"},new boolean[]{false});
		return container;
	}
	
	
	
	public Table buildTb(){
		tb = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,Property<?> property) {
				
				if(tb.getType(colId).equals(Date.class)){	
					if(colId.equals("data_agendado")){
						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");						
							return sdf.format(tb.getItem(rowId).getItemProperty(colId).getValue());
						}else{
							return super.formatPropertyValue(rowId, colId, property);	
						}
						
					}else{
						
						
						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
							SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");						
							return sdf.format(tb.getItem(rowId).getItemProperty(colId).getValue());
						}else{
							return super.formatPropertyValue(rowId, colId, property);	
						}
						
						
					}
				}
				
				if(colId.equals("id")){
					
					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}					
				}
				
				return super.formatPropertyValue(rowId, colId, property);
				
			}
		};
		tb.setWidth("100%");
		tb.setHeight("230px");
		tb.setSelectable(true);
		
		tb.setVisibleColumns(new Object[] {"id","setor.nome","data_agendado","hora_agendado","contrato.id","cliente.nome_razao","cliente.contato","crm_assuntos.nome","operador_tratamento","status","motivo_reagendamento"});
		
		tb.setColumnHeader("id", "Protocolo");
		tb.setColumnHeader("setor.nome", "Setor");
		tb.setColumnHeader("contrato.id", "Contrato");
		tb.setColumnHeader("data_agendado", "Previsão");
		tb.setColumnHeader("hora_agendado", "Hora");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("cliente.contato", "Contato");
		tb.setColumnHeader("crm_assuntos.nome", "Assunto");
		tb.setColumnHeader("operador_tratamento", "Operador");
		tb.setColumnHeader("status", "Status");	
		tb.setColumnHeader("motivo_reagendamento", "Motivo Reagendamento");
		
		//tb.setConverter("id", null);
		
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					
					if(item != null && item.getItemProperty("id").getValue() != null){
						Crm crm = CrmDAO.find((Integer)item.getItemProperty("id").getValue());				
						fireEvent(new HistoricoCrmEvent(getUI(), crm));
						close();
					}
					
				}
				
			}
		});
		return tb;
	}
	
	
	public TextField buildTextField() {
		tfBusca = new TextField();
		tfBusca.setInputPrompt("Buscar...");
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());				
			}
		});
		return tfBusca;
	}
	

	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(codCliente != null){
			container.addContainerFilter(Filters.eq("cliente", ClienteDAO.find(codCliente)));				
		}
			
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
				for(Object c:collums){		 			
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
						filtros.add(new Like(c.toString(), "%"+cod+"%", false));
					}			
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}
				}
		} catch (Exception e) {
			
			for(Object c:collums){
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"id"},new boolean[]{false});
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	
	
	
	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tb.getValue()!= null){
					Item item = tb.getItem(tb.getValue());			
					
					Crm crm = new Crm(
							Integer.parseInt(item.getItemProperty("id").toString()), 
							Integer.parseInt(item.getItemProperty("empresa_id").toString()), 
							(Setores)item.getItemProperty("setor").getValue(), 
							(Cliente)item.getItemProperty("cliente").getValue(), 
							(CrmAssunto)item.getItemProperty("crm_assuntos").getValue(), 
							(CrmFormasContato)item.getItemProperty("crm_formas_contato").getValue(), 
							item.getItemProperty("contato").getValue().toString(), 
							item.getItemProperty("origem").getValue().toString(), 
							item.getItemProperty("conteudo").getValue().toString(), 
							(Date)item.getItemProperty("data_agendado").getValue(), 
							(Date)item.getItemProperty("hora_agendado").getValue(), 
							(Date)item.getItemProperty("data_cadastro").getValue(), 
							(Date)item.getItemProperty("data_efetuado").getValue(), 
							item.getItemProperty("status").getValue().toString(), 
							item.getItemProperty("operador").getValue().toString());
					
					fireEvent(new HistoricoCrmEvent(getUI(), crm));
					close();
				}	
			}	
		});
		
		ShortcutListener slbtOK = new ShortcutListener("Ok",ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};

		btSalvar.addShortcutListener(slbtOK);

		
		return btSalvar;
	}
	
	private Button buildBtVisualizar(){
		btVisualizar = new Button("Visuzalizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tb.getValue() != null){
					final Window win = new Window("Conteúdo");
					win.setWidth("459px");
					win.setHeight("178px");
					win.setResizable(false);
					win.setModal(true);
					win.addShortcutListener(new ShortcutListener("ESC", ShortcutAction.KeyCode.ESCAPE, null) {
						
						@Override
						public void handleAction(Object sender, Object target) {
							win.close();							
						}
					});
					
					final TextArea ta = new TextArea();
					ta.setValue(tb.getItem(tb.getValue()).getItemProperty("conteudo").getValue().toString());
					ta.setSizeFull();
					ta.addStyleName("uppercase");
					ta.addStyleName("font-courier-new");
					
					//ta.setReadOnly(true);
					
					win.setContent(new VerticalLayout(){
						{
							setMargin(true);
							setSizeFull();
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

	private Button buildBtFechar() {
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

	
	
	public void addListerner(HistoricoCrmListerner target){
		try {
			Method method = HistoricoCrmListerner.class.getDeclaredMethod("onSelected",HistoricoCrmEvent.class);
			addListener(HistoricoCrmEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(HistoricoCrmListerner target){
		removeListener(HistoricoCrmEvent.class, target);
	}
	public static class HistoricoCrmEvent extends Event{

		private Crm crm;
		
		public HistoricoCrmEvent(Component source, Crm crm) {
			super(source);		
			this.crm  = crm;
		}

		public Crm getCrm() {
			
			return crm;
		}		
	}
	public interface HistoricoCrmListerner{
		public void onSelected(HistoricoCrmEvent event);
	}
}
