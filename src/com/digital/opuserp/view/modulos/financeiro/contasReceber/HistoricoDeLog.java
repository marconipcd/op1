package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.text.SimpleDateFormat;

import com.digital.opuserp.domain.AlteracoesContasReceber;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
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

public class HistoricoDeLog extends Window {

	JPAContainer<AlteracoesContasReceber> container;
	
	TextField tfBusca;
	Table tbAlteracoes;
	Button btSalvar;
	Button btCancelar;
	
	Integer codContaReceber;
	
	public HistoricoDeLog(boolean modal, boolean center, Integer codContaReceber){
		
		super("Histórico de Ocorrências");
		
		this.codContaReceber = codContaReceber;
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
	
	public JPAContainer<AlteracoesContasReceber> buildJpaContainer(){
		container = JPAContainerFactory.make(AlteracoesContasReceber.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("conta_Receber", new ContasReceber(codContaReceber)));
		container.addNestedContainerProperty("operador.username");
		
		container.sort(new String []{"id"}, new boolean[] {true});
		return container;
	}
	
	public Table buildTbChamados(){
		tbAlteracoes = new Table(null, buildJpaContainer());
		tbAlteracoes.setWidth("100%");
		tbAlteracoes.setHeight("230px");
		tbAlteracoes.setSelectable(true);

		
		tbAlteracoes.addGeneratedColumn("Ocorrência", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String data = sdf.format(source.getItem(itemId).getItemProperty("data_alteracao").getValue());
				
				String s = data+" - "+
						source.getItem(itemId).getItemProperty("operador.username").getValue().toString()+" - "+source.getItem(itemId).getItemProperty("tipo").getValue().toString();
				
				return s;
			}
		});
		
		tbAlteracoes.setVisibleColumns(new Object[]{"Ocorrência"});
			
		
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
