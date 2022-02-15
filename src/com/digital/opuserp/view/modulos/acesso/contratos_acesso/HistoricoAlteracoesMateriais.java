package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesMateriais;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HistoricoAlteracoesMateriais extends Window {

	JPAContainer<AlteracoesMateriais> container;
	
	TextField tfBusca;
	Table tbAlteracoes;
	Button btSalvar;
	Button btCancelar;
	
	
	
	public HistoricoAlteracoesMateriais(boolean modal, boolean center){
		
		super("Histórico de Alteracoes de Materiais");
	
		setWidth("1030px");
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
				
				addComponent(buildTxtBusca());
				addComponent(buildTbChamados());
				setExpandRatio(tbAlteracoes, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				btCancelar.focus();
				//hlButtons.addComponent(buildBtSalvar());
								
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}

		});
	}
	
	private TextField txtBusca;
	private TextField buildTxtBusca(){
		txtBusca = new TextField();
		txtBusca.setWidth("100%");
		txtBusca.setInputPrompt("buscar...");
		
		txtBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
					addFilter(event.getText());
			}
		});
		
		return txtBusca;
	}
	
	public void addFilter(String s) {		
		container.removeAllContainerFilters();
		container.setApplyFiltersImmediately(false);
				
		if(s != null && !s.equals("") && !s.isEmpty()){

			Object[] collums = tbAlteracoes.getVisibleColumns();		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			try {
				Integer cod = Integer.parseInt(s);
				
				for(Object c:collums){		 			
					if(!tbAlteracoes.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
						filtros.add(new Like(c.toString(), "%"+cod+"%", false));
					}			
					
					if( !tbAlteracoes.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
				}
				
			} catch (Exception e) {
					
					for(Object c:collums){					
											
						if(!tbAlteracoes.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));			
						}
					}
					
			 }
			 container.addContainerFilter(Filters.or(filtros));
		}
		
		container.applyFilters();
			
	}
	
	
	public JPAContainer<AlteracoesMateriais> buildJpaContainer(){
		container = JPAContainerFactory.make(AlteracoesMateriais.class, ConnUtil.getEntity());
		//container.addContainerFilter(Filters.eq("contrato", new AcessoCliente(codContrato)));
		//container.addNestedContainerProperty("operador.username");
		
		container.addNestedContainerProperty("acesso_cliente.id");
		container.addNestedContainerProperty("acesso_cliente.cliente.nome_razao");
		container.sort(new String []{"data"}, new boolean[] {false});
		return container;
	}
	
	public Table buildTbChamados(){
		tbAlteracoes = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("id") && tbAlteracoes.getItem(rowId).getItemProperty("id").getValue() != null){
										
					return ((Integer)tbAlteracoes.getItem(rowId).getItemProperty("id").getValue()).toString();										
				}
				
				if(colId.equals("acesso_cliente.id") && tbAlteracoes.getItem(rowId).getItemProperty("acesso_cliente.id").getValue() != null){
					
					return ((Integer)tbAlteracoes.getItem(rowId).getItemProperty("acesso_cliente.id").getValue()).toString();										
				}
				
//				if(colId.equals("acesso_cliente.cliente")){
//					
//					EntityItem<T>
//				}
//								
				return super.formatPropertyValue(rowId, colId, property);			
			}
		};
		
		tbAlteracoes.setSizeFull();
		tbAlteracoes.setSelectable(true);

		tbAlteracoes.setVisibleColumns(new Object[]{"mac","descricao","acesso_cliente.id","acesso_cliente.cliente.nome_razao","regime","data"});
		
		tbAlteracoes.setColumnHeader("id", "Cod");
		tbAlteracoes.setColumnHeader("acesso_cliente.id", "Contrato");
		tbAlteracoes.setColumnHeader("acesso_cliente.cliente.nome_razao", "Cliente");		
		tbAlteracoes.setColumnHeader("regime", "Regime");
		tbAlteracoes.setColumnHeader("mac", "Mac/Serial");
		tbAlteracoes.setColumnHeader("descricao", "Descrição");
		tbAlteracoes.setColumnHeader("data", "Data");
				
		return tbAlteracoes;
	}
	
	private Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
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
