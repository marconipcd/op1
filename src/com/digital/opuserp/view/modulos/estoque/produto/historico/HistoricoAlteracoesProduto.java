package com.digital.opuserp.view.modulos.estoque.produto.historico;

import java.text.SimpleDateFormat;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesProduto;
import com.digital.opuserp.domain.Crm;
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

public class HistoricoAlteracoesProduto extends Window {

	JPAContainer<AlteracoesProduto> container;
	
	TextField tfBusca;
	Table tbAlteracoes;
	Button btSalvar;
	Button btCancelar;
	
	Integer codProduto;
	
	public HistoricoAlteracoesProduto(boolean modal, boolean center, Integer codProduto){
		
		super("Log de Alterações");
		
		this.codProduto = codProduto;
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
	
	public JPAContainer<AlteracoesProduto> buildJpaContainer(){
		container = JPAContainerFactory.make(AlteracoesProduto.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa()));
		
		if(codProduto!=null){
			container.addContainerFilter(Filters.eq("produto_id", new Produto(codProduto)));
		}
		
		container.addNestedContainerProperty("produto_id.id");
		container.addNestedContainerProperty("operador.username");
		
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
				
//				if(!s.equals("")){
					return s;					
//				}else{
//					return null;
//				}
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

