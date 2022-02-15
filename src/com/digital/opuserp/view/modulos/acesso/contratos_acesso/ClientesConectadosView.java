package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.util.List;
import java.util.Map;

import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.MikrotikUtil;
//import com.github.wolfie.refresher.Refresher;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class ClientesConectadosView extends Window {

	
	ComboBox cbConcentrador;
	ComboBox cbInterface;
	Table tb;
	HorizontalLayout hlCb;
	Button btAtualizar;
	VerticalLayout vl;
	Label lbRegistros;
	
	public ClientesConectadosView(){
		
		super("Usuários Conectados");
		setWidth("913px");
		setHeight("530px");
		
		
		vl = new VerticalLayout(){
			{
				hlCb = new HorizontalLayout(){
					{												
						addComponent(buildCbConcentrador());
						addComponent(buildCbInterface());
						addComponent(buildBtAtualizar());
						btAtualizar.focus();
						setComponentAlignment(btAtualizar, Alignment.MIDDLE_RIGHT);
						setExpandRatio(btAtualizar, 1.0f);
					}
				};
				hlCb.setWidth("100%");
				
				addComponent(hlCb);
				addComponent(buildTb(null));
				setExpandRatio(tb, 1.0f);
				addComponent(buildLbRegistros());
			}
		};
		vl.setMargin(true); 
		vl.setHeight("100%");
		
		setContent(vl);
		
		
	}
	private Table buildTb(List<Map<String, String>> conectados ){
		tb = new Table();
		tb.setWidth("100%");
		tb.setHeight("100%");
		
		if(conectados != null){
			tb.addContainerProperty("Login", String.class, null);
			tb.addContainerProperty("Cliente", String.class, null);
			tb.addContainerProperty("Plano", String.class, null);
			tb.addContainerProperty("IP", String.class, null);
			tb.addContainerProperty("Swith", String.class, null);
			tb.addContainerProperty("Material", String.class, null);
			tb.addContainerProperty("Uptime", String.class, null);
			
			Integer i = 0;
			for (Map<String, String> map : conectados) {
				
				AcessoCliente contrato = ContratosAcessoDAO.getContratoPorLogin(map.get("name").toString());
				
				Map<String, String> registration = null;
			

				if(contrato != null && contrato.getInterfaces() != null && contrato.getInterfaces().equals(cbInterface.getValue().toString())){

				
					String swith = "";
					if(contrato.getSwith() != null && contrato.getSwith().getIdentificacao() != null){
						swith = contrato.getSwith().getIdentificacao();
					}
					
					tb.addItem(new Object[]{
							map.get("name").toString(),
							contrato.getCliente().getNome_razao(),
							contrato.getPlano().getNome(),
							map.get("address").toString(),							
							swith,
							contrato.getMaterial().getNome(),
							map.get("uptime").toString()
					}, i); 
					i++;
				}
			}
		}
		return tb;
	}
	
	private Button buildBtAtualizar(){
		btAtualizar = new Button("Atualizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(final ClickEvent event) {
				if(cbConcentrador.getValue() != null){
					EntityItem<Concentrador> entityItem = (EntityItem<Concentrador>) cbConcentrador.getItem(cbConcentrador.getValue());
					List<Map<String, String>> conectados = MikrotikUtil.getUsuariosConectados(entityItem.getEntity().getEndereco_ip(), entityItem.getEntity().getUsuario(), entityItem.getEntity().getSenha());
					vl.replaceComponent(tb, buildTb(conectados));
					vl.setExpandRatio(tb, 1.0f);
					
//					final Refresher re = new Refresher();
//					re.setRefreshInterval(20000);
//					re.addListener(new Refresher.RefreshListener() {
//						
//						@Override
//						public void refresh(Refresher source) {
//							event.getButton().setEnabled(true);		
//							removeExtension(re); 
//						}
//					});
//					addExtension(re); 
					
				}
			}
		});
		btAtualizar.setDisableOnClick(true);
		
		
		btAtualizar.addStyleName(Reindeer.BUTTON_SMALL);
		return btAtualizar;
	}
		
	private ComboBox buildCbConcentrador(){
		
			JPAContainer<Concentrador> container = JPAContainerFactory.makeReadOnly(Concentrador.class, ConnUtil.getEntity());			
			container.sort(new Object[]{"identificacao"}, new boolean[]{true});
			
			cbConcentrador = new ComboBox(null,container);
			cbConcentrador.setItemCaptionPropertyId("identificacao");
			cbConcentrador.addValueChangeListener(new Property.ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					hlCb.replaceComponent(cbInterface, buildCbInterface());				
				}
			});			
	
		return cbConcentrador;
	}
	private ComboBox buildCbInterface(){
		
		cbInterface = new ComboBox();
		if(cbConcentrador.getValue() != null){
			EntityItem<Concentrador> entityItem = (EntityItem<Concentrador>) cbConcentrador.getItem(cbConcentrador.getValue());
			
			List<Map<String, String>> interfaces = MikrotikUtil.listarInterfaces(entityItem.getEntity().getUsuario(),entityItem.getEntity().getSenha() , entityItem.getEntity().getEndereco_ip(), Integer.parseInt(entityItem.getEntity().getPorta_api()));
			for (Map<String, String> map : interfaces) {
				cbInterface.addItem(map.get("name"));
			}			
		}
		
		cbInterface.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbConcentrador.getValue() != null){
					EntityItem<Concentrador> entityItem = (EntityItem<Concentrador>) cbConcentrador.getItem(cbConcentrador.getValue());
					List<Map<String, String>> conectados = MikrotikUtil.getUsuariosConectados(entityItem.getEntity().getEndereco_ip(), entityItem.getEntity().getUsuario(), entityItem.getEntity().getSenha());
					vl.replaceComponent(tb, buildTb(conectados));
					vl.setExpandRatio(tb, 1.0f);
					vl.replaceComponent(lbRegistros, buildLbRegistros());
				}
			}
		});			

		return cbInterface;
	}
	
	private Label buildLbRegistros(){
		lbRegistros =  new Label(tb.getItemIds().size()+" Registros encontrados");
		return lbRegistros;
	}
}
