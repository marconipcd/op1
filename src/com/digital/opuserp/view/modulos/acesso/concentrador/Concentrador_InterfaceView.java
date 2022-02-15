package com.digital.opuserp.view.modulos.acesso.concentrador;

import java.util.List;

import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.util.mk_bean.Interface;
import com.digital.opuserp.view.modulos.acesso.concentrador.Concentrador_InterfaceEditor.InterfaceEditorEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Concentrador_InterfaceView extends Window{

	BeanItemContainer<Interface> interfaces;
	List<Interface> listInterfaces;
	
	Button btEditar;
	Table tbInterfaces;
	
	String usuario;
	String senha;
	String ip;
	Integer porta;
	
	public  Concentrador_InterfaceView(String usuario, String senha, String ip,Integer porta){
		super("Interfaces");
		
		this.usuario = usuario;
		this.senha = senha;
		this.ip = ip;
		this.porta = porta;
		
		final HorizontalLayout hlButtons = new HorizontalLayout();		
		hlButtons.addComponent(new Button("Habilitar/Desabilitar"));
		hlButtons.addComponent(buildBtEditar());
		
		interfaces = new BeanItemContainer<Interface>(Interface.class);
		refreshListaInterfaces();
		
		
		tbInterfaces = new Table(null, interfaces);
		tbInterfaces.setSelectable(true);
		tbInterfaces.setImmediate(true);
		
		tbInterfaces.setVisibleColumns(new Object[]{"name","type","disabled","mtu","l2mtu","comment"});
		
		tbInterfaces.setColumnCollapsingAllowed(true);
		tbInterfaces.setColumnCollapsed("mtu", true);
		tbInterfaces.setColumnCollapsed("l2mtu", true);
				
		tbInterfaces.setColumnHeader("name", "Nome");
		tbInterfaces.setColumnHeader("type", "Tipo");		
		tbInterfaces.setColumnHeader("disabled", "Desabilitada");
		tbInterfaces.setColumnHeader("mtu", "MTU");
		tbInterfaces.setColumnHeader("l2mtu", "L2MTU");
		tbInterfaces.setColumnHeader("comment", "Comentário");
		
		
		tbInterfaces.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				
				if(source.getItem(itemId).getItemProperty("disabled").equals("false")){
					return "table_style_disabled";
				}else{
					return null;
				}
				
			}
		});
		
		
		tbInterfaces.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(tbInterfaces.getValue() != null){
					btEditar.setEnabled(true);
				}else{
					btEditar.setEnabled(false);
				}
			}
		});
		
		tbInterfaces.setSizeUndefined();		
		
		setContent(new VerticalLayout(){
			{
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.TOP_RIGHT);
				addComponent(tbInterfaces);
				setExpandRatio(tbInterfaces, 2);
			}
		});
		
	}
	
	private void refreshListaInterfaces(){
		
//		listInterfaces = MikrotikUtil.listarInterfaces(usuario,senha,ip,porta);
//		if(listInterfaces != null){
//			interfaces.removeAllItems();
//			for(Interface intt:listInterfaces){
//				interfaces.addBean(intt);
//			}
//		}else{
//			interfaces.removeAllItems();
//		}
	}
	private Button buildBtEditar(){
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tbInterfaces.getValue() != null){
				
					Concentrador_InterfaceEditor win_edit = new Concentrador_InterfaceEditor(usuario, senha, ip, porta, 
							tbInterfaces.getItem(tbInterfaces.getValue()).getItemProperty("name").toString(), 
							tbInterfaces.getItem(tbInterfaces.getValue()).getItemProperty("comment").toString());
					
					win_edit.addListerner(new Concentrador_InterfaceEditor.InterfaceEditorListerner() {
						
						@Override
						public void onSave(InterfaceEditorEvent event) {
							
							Notify.Show("Dados Gravados Com Sucesso!", Notify.TYPE_SUCCESS);
							refreshListaInterfaces();
						}
					});
				
					getUI().addWindow(win_edit);
				
				}
				
			}
		});
		btEditar.setEnabled(false);
		
		return btEditar;
	}

}
