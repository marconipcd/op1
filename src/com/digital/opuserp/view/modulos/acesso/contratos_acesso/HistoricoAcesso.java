package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.RadAcct;
import com.digital.opuserp.util.ByteUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.util.TimeUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.Item;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HistoricoAcesso extends Window {
	
	JPAContainer<RadAcct> container;
	
	TextField tfBusca;
	Table tbChamados;
	Button btSalvar;
	Button btCancelar;
	Button btImprimir;
	Button btDesconectar;
	Button btAtualizar;
	
	Integer valorInicial;
	
	String username;
	Item item;
	Window window;
		
	VerticalLayout vl;
	
	public HistoricoAcesso(boolean modal, boolean center, String username, Item item){
		
		super("Histórico de Acesso");
		
		this.username = username;
		this.item = item;
		
		
		setWidth("1100px");
		setHeight("400px");
		
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
						
		vl = new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				HorizontalLayout hlButtons2 = new HorizontalLayout();
				hlButtons2.setSpacing(true);
				hlButtons2.setMargin(true);
				hlButtons2.setStyleName("hl_buttons_bottom");
//				hlButtons2.addComponent(buildBtImprimir());
				hlButtons2.addComponent(buildBtAtualizar());
				hlButtons2.addComponent(buildBtDesconectar());
				
				addComponent(hlButtons2);
				setComponentAlignment(hlButtons2, Alignment.TOP_RIGHT);
				
				
				addComponent(buildTextField());
				addComponent(buildTbChamados());
				setExpandRatio(tbChamados, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				//hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
				
				}
			
				
		};
		setContent(vl);
	}
	
	public JPAContainer<RadAcct> buildJpaContainer(){
		container = JPAContainerFactory.make(RadAcct.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("username", username));
		container.sort(new String[]{"radacctid"}, new boolean[]{false});
		container.applyFilters();
		return container;
	}
	
	public Table buildTbChamados(){
		tbChamados = new Table(null, buildJpaContainer());
		tbChamados.setWidth("100%");
		tbChamados.setHeight("230px");
		tbChamados.setSelectable(true);
		
		tbChamados.setColumnHeader("acctstarttime", "Data Inicio");
		tbChamados.setColumnHeader("acctstoptime", "Data Final");
		tbChamados.setColumnHeader("acctsessiontime", "Tempo Total");
		tbChamados.setColumnHeader("acctinputoctets", "Upload");
		tbChamados.setColumnHeader("acctoutputoctets", "Download");
		tbChamados.setColumnHeader("acctterminatecause", "Causa do Término");		
		tbChamados.setColumnHeader("callingstationid", "Mac");	
		tbChamados.setColumnHeader("framedipaddress", "Ip");
		
		
		tbChamados.addGeneratedColumn("acctinputoctets", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				String valor = ByteUtil.humanReadableByteCount((long)source.getItem(itemId).getItemProperty("acctinputoctets").getValue(), true);				
				return valor;
			}
		});
		
		tbChamados.addGeneratedColumn("acctoutputoctets", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				String valor = ByteUtil.humanReadableByteCount((long)source.getItem(itemId).getItemProperty("acctoutputoctets").getValue(), true);				
				return valor;
			}
		});
		
		
		tbChamados.addGeneratedColumn("acctsessiontime", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				Date valorInicial = (Date)source.getItem(itemId).getItemProperty("acctstarttime").getValue();

				Date dt2 = new Date();
				long Second = (dt2.getTime()-valorInicial.getTime()) / 1000;
				
				Integer segundos = Integer.parseInt(String.valueOf(Second));
				
				String valor = "";
				valor = TimeUtil.formataTempo((Integer)source.getItem(itemId).getItemProperty("acctsessiontime").getValue());					
				
				if(valor.equals("0d 0h 0min")){
					valor = TimeUtil.formataTempo(segundos);
				}
				return valor;
			}
		});
		
		
		tbChamados.addGeneratedColumn("acctterminatecause", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				
				String valor = source.getItem(itemId).getItemProperty("acctterminatecause").getValue().toString();
				
				if(valor.equals("User-Request")){
					valor = "Usuário Requisitou";
				}else if(valor.equals("Lost-Carrier")){
					valor = "Conexão Perdida";
				}else if(valor.equals("Lost-Service")){
					valor = "Serviço foi Perdido";
				}else if(valor.equals("Idle-Timeout")){
					valor = "Tempo de Ociosidade Esgotou";
				}else if(valor.equals("Session-Timeout")){
					valor = "Tempo de Sessão Esgotou";
				}else if(valor.equals("Admin-Reset")){
					valor = "Resetado pelo Admin";
				}else if(valor.equals("Admin-Reboot")){
					valor = "Reiniciado pelo Admin";
				}else if(valor.equals("Port-Error")){
					valor = "Erro na Porta";
				}else if(valor.equals("NAS-Error")){
					valor = "Erro no Concentrador";
				}else if(valor.equals("NAS-Request")){
					valor = "Requisitado no Contrador";
				}else if(valor.equals("NAS-Reboot")){
					valor = "Reiniciado pelo Concentrador";
				}else if(valor.equals("Port-Unneeded")){
					valor = "Porta Desnecessária";
				}else if(valor.equals("Port-Preempted")){
					valor = "Porta Interrompida";
				}else if(valor.equals("Port-Suspended")){
					valor = "Porta Suspensa";
				}else if(valor.equals("Service-Unavailable")){
					valor = "Porta não Disponível";
				}else if(valor.equals("Callback")){
					valor = "Opus Callback";
				}else if(valor.equals("User-Error")){
					valor = "Erro do Usuário";
				}else if(valor.equals("Host-Request")){
					valor = "Requisitado pelo Host";
				}
				return valor;
			}
		});
		
		
		
		tbChamados.setVisibleColumns(new Object[]{"acctstarttime","acctstoptime","acctsessiontime","acctinputoctets","acctoutputoctets","acctterminatecause","callingstationid","framedipaddress"});
		
		
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
		container.addContainerFilter(Filters.eq("username", username));
		Object[] collums = tbChamados.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
		Integer cod = Integer.valueOf(s);
		
			for(Object c:collums){		 			
				if(!tbChamados.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				if(!tbChamados.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
		
		} catch (Exception e) {
	
			for(Object c:collums){		 			
				if(!tbChamados.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}			
			}
		}

		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();
		container.sort(new String[]{"radacctid"}, new boolean[]{true});
		
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

	
	private Button buildBtImprimir() {
		btImprimir = new Button("Imprimir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
//				try{
//					window = new Window();
//					window.setCaption("Imprimir Histórico de Acesso");
//			        window.setWidth("800px");
//			        window.setHeight("600px");
//			        window.setResizable(false);
//			        window.center();
//			        window.setModal(true);
//			        window.setStyleName("disable_scroolbar");		
//			        window.setCloseShortcut(KeyCode.ESCAPE, null);
//			        
//			        
//			        AcessoCliente ac = (AcessoCliente) item.getItemProperty("id").getValue();
//			        Cliente cliente = (Cliente) item.getItemProperty("cliente").getValue();
////			        Integer codAc = Integer.parseInt(tbChamados.getItem(tbChamados.getValue()).getItemProperty("id").getValue().toString()); 
////			        String nomeCliente = tbChamados.getItem(tbChamados.getValue()).getItemProperty("cliente.nome_razao").getValue().toString();
////			        String codCliente = tbChamados.getItem(tbChamados.getValue()).getItemProperty("cliente.id").getValue().toString();
//			        StreamResource resource = new StreamResource(new ImprimirHistoricoAcesso(ac.getId()), "Histórico de Acesso "+cliente.getId()+" - "+cliente.getNome_razao()+".pdf");
//			        resource.getStream();			        
//			        resource.setMIMEType("application/pdf");
//			        resource.setCacheTime(0);
//			        
//			        Embedded e = new Embedded();
//			        e.setSizeFull();
//			        e.setType(Embedded.TYPE_BROWSER); 
//			        e.setSource(resource);			     
//			        window.setContent(e);
//			        getUI().addWindow(window);
//			        window.focus();
//			        
//				}catch(Exception e){
//					System.out.println("Deu ERRADO!");
//					e.printStackTrace();
//				}
				
			}
		});
		return btImprimir;
	}
	
	private Button buildBtAtualizar() {
		btAtualizar = new Button("Atualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
							
				vl.replaceComponent(tbChamados, buildTbChamados());
				vl.setExpandRatio(tbChamados, 1);
			}
		});
		return btAtualizar;	
	}
	
	private Button buildBtDesconectar() {
		btDesconectar = new Button("Desconectar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(item != null){
					Concentrador base = (Concentrador) item.getItemProperty("base").getValue();
					boolean check = MikrotikUtil.desconectarCliente(base.getUsuario(), base.getSenha(), base.getEndereco_ip(), Integer.parseInt(base.getPorta_api()), username);
					if(check){						
						Notify.Show("O Cliente foi Desconectado Com Sucesso!", Notify.TYPE_SUCCESS);
					}else{
						Notify.Show("O Cliente já esta Desconectado!", Notify.TYPE_NOTICE);
					}
				}
				vl.replaceComponent(tbChamados, buildTbChamados());
				vl.setExpandRatio(tbChamados, 1);
			}
		});
		return btDesconectar;	
	}
	
}
