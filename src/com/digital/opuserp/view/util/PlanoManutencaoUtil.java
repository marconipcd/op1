package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.PlanosManutencao;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.ClienteUtil.ClienteEvent;
import com.digital.opuserp.view.util.ClienteUtil.ClienteListerner;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.themes.Reindeer;

public class PlanoManutencaoUtil extends Window {

	JPAContainer<PlanosManutencao> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;

	private String status;
	
	private Label lbRegistros;
	HorizontalLayout hlFloat;
	
	
	public PlanoManutencaoUtil(boolean modal, boolean center){
		
		
		super("Selecione um Plano de Manutenção");
		this.status = status;
		
		addStyleName("teste");
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
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSelecionar());
				
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
		
	public JPAContainer<PlanosManutencao> buildJpaContainer(){
		container = JPAContainerFactory.make(PlanosManutencao.class, ConnUtil.getEntity());		
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		
		//container.sort(new String[]{"nome_razao"},new boolean[]{true});
		return container;
	}
	
	
	
	public Table buildTb(){
		tb = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				
				if(colId.equals("valor_mensal") && tb.getItem(rowId).getItemProperty(colId).getValue() != null){					
					return "R$ "+tb.getItem(rowId).getItemProperty(colId).getValue().toString();
				}else if(colId.equals("id")){
						
						if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
							return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
						}					
				} else{
						return super.formatPropertyValue(rowId, colId, property);
				}
				return ""; 
				
			}
		};
		tb.setWidth("100%");
		tb.setHeight("230px");
		tb.setSelectable(true);
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome", "Nome");
		tb.setColumnHeader("valor_mensal", "Valor Mensal");
		tb.setColumnHeader("limite_mensal", "Limite Mensal");
		tb.setColumnHeader("numeroAtivos", "Nº Ativos");
		tb.setColumnHeader("vigencia", "Vigência");
		
		tb.setVisibleColumns(new Object[]{"id","nome","valor_mensal","limite_mensal","numeroAtivos","vigencia"});

		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
								
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					EntityItem<PlanosManutencao> item = (EntityItem<PlanosManutencao>)((Table)event.getComponent()).getItem(ItemId);

					fireEvent(new PlanoManutencaoUtilEvent(getUI(), item.getEntity()));
					close();
					
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
		tfBusca.setImmediate(true);
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
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
			
		
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
		//container.sort(new String[]{"nome_razao"},new boolean[]{true});
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
					EntityItem<PlanosManutencao> item = (EntityItem<PlanosManutencao>)tb.getItem(tb.getValue());			
							
					fireEvent(new PlanoManutencaoUtilEvent(getUI(), item.getEntity()));
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
		btSalvar.addStyleName(Reindeer.BUTTON_SMALL);
		
		return btSalvar;
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
		btCancelar.addStyleName(Reindeer.BUTTON_SMALL);
		
		return btCancelar;
	}

	
	
	public void addListerner(PlanoManutencaoUtilListerner target){
		try {
			Method method = PlanoManutencaoUtilListerner.class.getDeclaredMethod("onSelected",PlanoManutencaoUtilEvent.class);
			addListener(PlanoManutencaoUtilEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(PlanoManutencaoUtilListerner target){
		removeListener(PlanoManutencaoUtilEvent.class, target);
	}
	public static class PlanoManutencaoUtilEvent extends Event{

		private PlanosManutencao planoManutencao;
		
		public PlanoManutencaoUtilEvent(Component source, PlanosManutencao planoManutencao) {
			super(source);		
			this.planoManutencao  = planoManutencao;
		}

		public PlanosManutencao getPlanoManutencao() {
			
			return planoManutencao;
		}		
	}
	public interface PlanoManutencaoUtilListerner{
		public void onSelected(PlanoManutencaoUtilEvent event);
	}
}
