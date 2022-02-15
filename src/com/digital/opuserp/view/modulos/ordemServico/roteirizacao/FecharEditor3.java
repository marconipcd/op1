package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FecharEditor3 extends Window {

	private VerticalLayout vlRoot;
	
	public FecharEditor3(){
	
		vlRoot = new VerticalLayout();
		vlRoot.setSizeFull();
		
		setContent(vlRoot); 
		buildLayout();
		buildControllers();
	}
	
	private void buildControllers(){
		
		HorizontalLayout hlBottom = new HorizontalLayout();
		
		Button btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Notify.Show("Salvo com sucesso", Notify.TYPE_SUCCESS);
			}
		});
		
		Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Notify.Show("Cancelado com sucesso", Notify.TYPE_NOTICE);
			}
		});
		
		hlBottom.addComponent(btSalvar);
		hlBottom.addComponent(btCancelar);
	}
	
	private void buildLayout(){
		
		
	   vlRoot.addComponent(new FormLayout(){
		   {
			   
			   EntityManager em = ConnUtil.getEntity();
			   JPAContainer<Usuario> containerTecnico = new JPAContainerFactory().makeReadOnly(Usuario.class, em);
			   
			   ComboBox cbTec = new ComboBox("Tecnico", containerTecnico);
			   cbTec.setItemCaptionPropertyId("username");
			   cbTec.setWidth("80px");
			   cbTec.setId("cbTec");
			   cbTec.setImmediate(true);
			   cbTec.setInputPrompt("Escolha um Técnico");
			   cbTec.setNullSelectionAllowed(false);
			   
			   addComponent(cbTec); 
		   }
	   });
		
		
	   vlRoot.addComponent(new FormLayout(){
		   {
			   TextField txtLaudo = new TextField("xLaud");
			   txtLaudo.setId("txtLaudo");
			   txtLaudo.setImmediate(true);
			   txtLaudo.setNullRepresentation("");
			   txtLaudo.setMaxLength(280);
			   txtLaudo.setStyleName("custom-field");
			   txtLaudo.setWidth("118px");
			   txtLaudo.setInputPrompt("Laudo");
			   
			   addComponent(txtLaudo);			   
		   }
	   });		
	   
	   
	}
	
	
	
	public class FecharEditorEvent extends Event{
		
		Usuario user;
		String laudo;
				
		public FecharEditorEvent(Component source, Usuario user, String laudo) {
			super(source);		
			
			this.user = user;
			this.laudo = laudo;
		}
		
		
		
	}
}
