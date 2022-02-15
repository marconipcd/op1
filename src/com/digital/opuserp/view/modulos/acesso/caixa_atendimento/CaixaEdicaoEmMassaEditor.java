package com.digital.opuserp.view.modulos.acesso.caixa_atendimento;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

import com.digital.opuserp.interfaces.GenericEditor;
import com.vaadin.data.Item;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CaixaEdicaoEmMassaEditor extends Window implements GenericEditor {
	
	
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	
	VerticalLayout vlRoot;
	String identificacaoAtual;
	boolean allow_save;
	
	boolean valid_ip = true;
	
	public CaixaEdicaoEmMassaEditor(String title, boolean modal){
	
		
//		if (item.getItemProperty("identificacao").getValue() != null) {
//			identificacaoAtual = item.getItemProperty("identificacao").getValue().toString();
//			allow_save = true;
//		}
		
		configLayout();	
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
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
	
	

	ComboBox cbOlt;
	
	private void configLayout(){
		////1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("723px");
				setHeight("172px");
		//}
	}
	
	private ComboBox cbSituacao;
	public void buildLayout(){
		
		vlRoot.addComponent(	new FormLayout(){
			{
			
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				cbSituacao = new ComboBox("Situação");
				cbSituacao.setRequired(true); 
				cbSituacao.addItem("NORMAL");
				cbSituacao.addItem("MANUTENCAO");
				cbSituacao.addItem("PROBLEMA");
				cbSituacao.setNullSelectionAllowed(false);
				cbSituacao.setStyleName("caption-align-concentrador");
				cbSituacao.setTextInputAllowed(false);
				
				addComponent(cbSituacao);
			
						
			}
		});
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {			
				fireEvent(new CaixaEdicaoEmMassaEvent(getUI(), cbSituacao.getValue().toString(), true));	
				close();
			}
		});
		btSalvar.setStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("Salvar", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);		
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
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
	
	public void addListerner(CaixaEdicaoEmMassaListerner target){
		try {
			Method method = CaixaEdicaoEmMassaListerner.class.getDeclaredMethod("onClose", CaixaEdicaoEmMassaEvent.class);
			addListener(CaixaEdicaoEmMassaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CaixaEdicaoEmMassaListerner target){
		removeListener(CaixaEdicaoEmMassaEvent.class, target);
	}
	public static class CaixaEdicaoEmMassaEvent extends Event{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Item item;
		private boolean confirm;
		private String situacao;
		
		public CaixaEdicaoEmMassaEvent(Component source,String situacao, boolean confirm) {
			super(source);	
			this.confirm = confirm;
			this.situacao = situacao;
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}	
		
		public String getSituacao(){
			return situacao;
		}
	}
	public interface CaixaEdicaoEmMassaListerner extends Serializable{
		public void onClose(CaixaEdicaoEmMassaEvent event);
	}
	
}
