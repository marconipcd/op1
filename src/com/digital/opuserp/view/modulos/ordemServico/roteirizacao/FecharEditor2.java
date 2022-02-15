package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FecharEditor2 extends Window {
	
	
	private VerticalLayout vl = new VerticalLayout();
	
	public FecharEditor2(boolean maximize, boolean center){
		
		vl.setSizeFull();
		setContent(vl); 
		
	}

	public FecharEditor2(){
					
		buildLayout();
		buildControles();	
	}
	
	private void buildUpControl(){
		HorizontalLayout hl = new HorizontalLayout();
		 
		Button btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
								
			}
		});
		
		Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		vl.addComponent(hl); 
	}
	
	private void buildControles(){
		HorizontalLayout hl = new HorizontalLayout();
		
		Button btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		hl.addComponent(btSalvar);
		hl.addComponent(btCancelar);
		hl.setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
		hl.setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
		
		hl.addComponent(hl);		
	}	
	private void buildLayout(){
		
		VerticalLayout vlRoot = new VerticalLayout();
		
		JPAContainer<Usuario> container = new JPAContainerFactory().make(Usuario.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("funcao", "tecnico"));
		
		final ComboBox cb = new ComboBox("Técnico", container);
		cb.setItemCaptionPropertyId("username");
		cb.setNullSelectionAllowed(false); 
		cb.setConvertedValue(new SingleSelectConverter(cb));
		cb.setRequired(true); 
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("custom-layout");
				addComponent(cb); 
			}
		});
		
		
		final TextArea txtLaudo = new TextArea("Laudo");
		txtLaudo.setNullRepresentation("");
		txtLaudo.setRequired(true);
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("custom-layout");
				addComponent(txtLaudo); 
			}
		});
		
		vl.addComponent(vlRoot); 
	}
	private void buildSwithLeftNow(){
		
		VerticalLayout vlRootDown = new VerticalLayout();
		
	}
	
	public class addFecharEvent extends Event{ 
		
		public addFecharEvent(Component ui){
			super(ui);
		}
	}
}
