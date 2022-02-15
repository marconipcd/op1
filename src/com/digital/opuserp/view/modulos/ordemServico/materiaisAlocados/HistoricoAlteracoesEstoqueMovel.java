package com.digital.opuserp.view.modulos.ordemServico.materiaisAlocados;

import java.text.SimpleDateFormat;

import com.digital.opuserp.domain.AlteracoesEstoqueMovel;
import com.digital.opuserp.domain.EstoqueMovel;
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

public class HistoricoAlteracoesEstoqueMovel extends Window {

	JPAContainer<AlteracoesEstoqueMovel> container;
	
	TextField tfBusca;
	Table tbAlteracoes;
	Button btSalvar;
	Button btCancelar;
	
	EstoqueMovel estoqMovel;
		
	public HistoricoAlteracoesEstoqueMovel(boolean modal, boolean center,EstoqueMovel estoqMovel){
		
		super("Log de Alterações");
		
		this.estoqMovel = estoqMovel;
		
		setWidth("830px");
		setHeight("360px");
		
		setModal(modal);
		if(center){
			center();
		}
		
		setResizable(false);
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
	
	public JPAContainer<AlteracoesEstoqueMovel> buildJpaContainer(){
		container = JPAContainerFactory.make(AlteracoesEstoqueMovel.class, ConnUtil.getEntity());		
		container.addContainerFilter(Filters.eq("estoqueMovel", estoqMovel));
		
		container.addNestedContainerProperty("estoqueMovel.produto.nome");
		container.addNestedContainerProperty("usuario.username");		
		
		container.sort(new String []{"id"}, new boolean[] {false});
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
				String s ="";
				
				if(source.getItem(itemId)!=null){
					
					String	cod = String.valueOf(Integer.parseInt(source.getItem(itemId).getItemProperty("produto_id.id").getValue().toString()));						
					
					String data = sdf.format(source.getItem(itemId).getItemProperty("data_alteracao").getValue());					
					s = data+" - "+"("+ cod +") "+
							source.getItem(itemId).getItemProperty("operador.username").getValue().toString()+" - "+source.getItem(itemId).getItemProperty("tipo").getValue().toString();	
				}
				

				return s;					

			}
		});
		
		tbAlteracoes.setVisibleColumns(new Object[]{"descricao","qtd","data_alteracao","usuario.username"});
		tbAlteracoes.setColumnHeader("descricao", "Ocorrência");
		tbAlteracoes.setColumnHeader("data_alteracao", "Data Alteração");
		tbAlteracoes.setColumnHeader("usuario.username", "Operador");
		tbAlteracoes.setColumnHeader("qtd", "Qtd.:");
		
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

