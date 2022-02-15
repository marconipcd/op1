package com.digital.opuserp.view.modulos.financeiro.haver;

import java.text.SimpleDateFormat;

import com.digital.opuserp.domain.AlteracoesHaver;
import com.digital.opuserp.domain.AlteracoesHaverCab;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HistoricoAlteracoes extends  Window {

	JPAContainer<AlteracoesHaverCab> container;
	
	TextField tfBusca;
	Table tbAlteracoes;
	Button btSalvar;
	Button btCancelar;
	
	Integer codHaver;
	
	public HistoricoAlteracoes(boolean modal, boolean center, Integer codContrato){
		
		super("Histórico de Alteracoes");
		
		this.codHaver = codContrato;
		setWidth("830px");
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
				
				addComponent(buildTbChamados());
				setExpandRatio(tbAlteracoes, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				//hlButtons.addComponent(buildBtSalvar());
								
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}

		});
	}
	
	public JPAContainer<AlteracoesHaverCab> buildJpaContainer(){
		container = JPAContainerFactory.make(AlteracoesHaverCab.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("haver", new Haver(codHaver)));
		container.addNestedContainerProperty("operador.username");
		container.addNestedContainerProperty("haver.valor");
		
		container.sort(new String []{"id"}, new boolean[] {true});
		return container;
	}
	
	public Table buildTbChamados(){
		tbAlteracoes = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("valor_haver") || colId.equals("valor_utilizado") || colId.equals("valor_saldo")){					
					
					return "R$ "+Real.formatDbToString(tbAlteracoes.getItem(rowId).getItemProperty(colId).getValue().toString());
				}				
				
				
				return super.formatPropertyValue(rowId, colId, property);
			
				
			}
		};
		tbAlteracoes.setWidth("100%");
		tbAlteracoes.setHeight("230px");
		tbAlteracoes.setSelectable(true);

		
		tbAlteracoes.addGeneratedColumn("Registros", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String data = sdf.format(source.getItem(itemId).getItemProperty("data_alteracao").getValue());
				
				String s = data+" - "+
						source.getItem(itemId).getItemProperty("operador.username").getValue().toString()+" - "+source.getItem(itemId).getItemProperty("tipo").getValue().toString();
				
				return s;
			}
		});
		
		tbAlteracoes.setVisibleColumns(new Object[]{"Registros","valor_haver","valor_utilizado","valor_saldo"});
		
		tbAlteracoes.setColumnHeader("valor_haver", "Valor do Haver");
		tbAlteracoes.setColumnHeader("valor_utilizado", "Valor Utilizado");
		tbAlteracoes.setColumnHeader("valor_saldo", "Saldo");
				
		tbAlteracoes.setColumnAlignment("valor_haver", Align.RIGHT);
		tbAlteracoes.setColumnAlignment("valor_utilizado", Align.RIGHT);
		tbAlteracoes.setColumnAlignment("valor_saldo", Align.RIGHT);
		
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
