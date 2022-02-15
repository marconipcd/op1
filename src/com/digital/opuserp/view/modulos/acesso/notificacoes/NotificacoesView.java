package com.digital.opuserp.view.modulos.acesso.notificacoes;

import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.NotificacaoAcesso;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.acesso.planos.PlanosEditor;
import com.digital.opuserp.view.modulos.acesso.planos.PlanosEditor.PlanoEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Window;

public class NotificacoesView extends  Window implements GenericView {

	JPAContainer<NotificacaoAcesso> container;
	
	Table tb;
		
	HorizontalLayout hlFloat;
		
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	
	public NotificacoesView(){
		
			VerticalLayout vl = new VerticalLayout();
			vl.setSizeFull();
			setContent(vl);
		
		
			setSizeFull();			
			gmDAO = new GerenciarModuloDAO();			
			vl.addComponent(buildTbGeneric());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			
			vl.addComponent(hlFloat);
			vl.setExpandRatio(tb, 1);		
	}
	
	public JPAContainer<NotificacaoAcesso> buildContainer(){
		container = JPAContainerFactory.makeBatchable(NotificacaoAcesso.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		//container.addNestedContainerProperty("contrato_acesso.nome");
		//container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.EMPRESA_SELECIONADA.getId()));
		//container.sort(new Object[]{"contrato_acesso.nome"}, new boolean[]{true});	
		//container.addContainerFilter(Filters.not(Filters.eq("nome", "BLOQUEADO")));
			
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setColumnCollapsed("id", true);
						
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("acesso_cliente.id", "Contrato");		
		tb.setColumnHeader("descricao", "Descricao");
		tb.setColumnHeader("data_notificacao", "Data Notificação");		
		
		tb.setVisibleColumns(new Object[] {"id","acesso_cliente.id","descricao","data_notificacao"});
		
		tb.setImmediate(true);
	
		return tb;
	}

	@Override
	public TextField buildTfbusca() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addFilter(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Button BuildbtTransferencia() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Button BuildbtAceitar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Button BuildbtCancelar() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
		
}
