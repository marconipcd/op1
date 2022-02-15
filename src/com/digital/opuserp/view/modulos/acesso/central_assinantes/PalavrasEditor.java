package com.digital.opuserp.view.modulos.acesso.central_assinantes;

import com.digital.opuserp.domain.ListaSitesFiltroC;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;

public class PalavrasEditor extends Window {

	private ListaSitesFiltroC lista;
	VerticalLayout vlRoot;
	
	public PalavrasEditor(String title, ListaSitesFiltroC lista, boolean modal){
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
			}
		});
		buildLayout();
	}
	private void buildLayout(){
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
//					VerticalLayout vlEditor = 
					
				}
		});
	}
}
