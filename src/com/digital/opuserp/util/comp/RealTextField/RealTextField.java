package com.digital.opuserp.util.comp.RealTextField;

import com.vaadin.annotations.JavaScript;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.TextField;



public class RealTextField extends TextField implements TextChangeListener, BlurListener {

	String last;
	
	
	public RealTextField(String caption){
		setCaption(caption);
		setImmediate(true);
		addTextChangeListener(this);
		
		
		last = "0.00";
	}

	@Override
	public void textChange(TextChangeEvent event) {
		
//		try {
//		  new Float(event.getText());
//		  last = event.getText();
//		} catch (NumberFormatException e) {
//		  setValue(last);
//		}
		
	}

	@Override
	public void blur(BlurEvent event) {
		//TODO Formatar
	}
	
	
}
