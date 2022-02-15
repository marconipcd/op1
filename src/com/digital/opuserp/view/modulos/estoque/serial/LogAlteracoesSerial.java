package com.digital.opuserp.view.modulos.estoque.serial;

import java.text.SimpleDateFormat;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
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

public class LogAlteracoesSerial extends Window {

	JPAContainer<AlteracoesSerial> container;
	
	TextField tfBusca;
	Table tbAlteracoes;
	Button btSalvar;
	Button btCancelar;
	
	Integer codSerial;
	
	public LogAlteracoesSerial(boolean modal, boolean center, Integer codSerial){
		
		super("Log de Alterações");
		
		this.codSerial = codSerial;
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
	
	public JPAContainer<AlteracoesSerial> buildJpaContainer(){
		container = JPAContainerFactory.make(AlteracoesSerial.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa()));
		container.addContainerFilter(Filters.eq("serial_id", new SerialProduto(codSerial)));
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
					
//					String	cod = String.valueOf(Integer.parseInt(source.getItem(itemId).getItemProperty("id").getValue().toString()));						
					
					String data = sdf.format(source.getItem(itemId).getItemProperty("data_alteracao").getValue());					
					s = data+" - "+
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


