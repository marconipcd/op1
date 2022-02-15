package com.digital.opuserp.view.modulos.financeiro.haver;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.domain.HaverDetalhe;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HaverDuplicadoMesclarEditor extends Window {

	VerticalLayout vlRoot;
	List<HaverCab> listHaver;
	
	public HaverDuplicadoMesclarEditor(String title, boolean modal, List<HaverCab> listCab){
		
		this.listHaver = listCab;
		
		setResizable(false);
		setClosable(false);
		
		setCaption(title);
		setModal(modal);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				addComponent(vlRoot);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
								
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		buildLayout();
	}
	
	private void buildLayout(){
		for (HaverCab haver: listHaver) {
			
			Label lb = new Label(haver.getId().toString()+" - "+haver.getCliente().getNome_razao(), ContentMode.HTML);
			vlRoot.addComponent(lb); 
			
			JPAContainer<HaverDetalhe> container = JPAContainerFactory.make(HaverDetalhe.class, ConnUtil.getEntity());
			container.addContainerFilter(Filters.eq("haverCab", haver));
			
			Table tb = new Table(null, container);
			tb.setSizeFull(); 
			tb.setVisibleColumns(new Object[]{"tipo","data_emissao","doc","n_doc","referente","valor"});
			tb.setColumnHeader("tipo", "Tipo");
			tb.setColumnHeader("data_emissao", "Emissão");
			tb.setColumnHeader("doc", "Doc");
			tb.setColumnHeader("n_doc", "N. Doc");
			tb.setColumnHeader("referente", "Ref");
			tb.setColumnHeader("valor", "Valor");	
			
			vlRoot.addComponent(tb); 
		}
	}
	
	
	Button btSalvar;
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
			
					try {				
						
					} catch (Exception e) {					
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(),Notify.TYPE_ERROR);
					}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}
	
	
	Button btCancelar;
	public Button buildBtCancelar() {
		
		btCancelar = new Button("OK", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
			
					try {				
												
						//fireEvent(new HaverEvent(getUI(), item, ClienteSelecionado,true));					
						
					} catch (Exception e) {					
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(),Notify.TYPE_ERROR);
					}
				
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtOK);
		
		btCancelar.setStyleName("default");
		return btCancelar;
	}
	
	public void addListerner(HaverMesclarListerner target){
		try {
			Method method = HaverMesclarListerner.class.getDeclaredMethod("onClose", HaverMesclarEvent.class);
			addListener(HaverMesclarEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(HaverMesclarListerner target){
		removeListener(HaverMesclarEvent.class, target);
	}
	public static class HaverMesclarEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private Cliente cliente;
		
		public HaverMesclarEvent(Component source, Item item,Cliente cliente, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;
			this.cliente = cliente;
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
		public Cliente getCliente(){
			return cliente;
		}
	}
	public interface HaverMesclarListerner extends Serializable{
		public void onClose(HaverMesclarEvent event);
	}

}
