package com.digital.opuserp.util.comp;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;

import com.vaadin.ui.TextField;

public class NumericTextField extends TextField implements TextChangeListener {

	String lastValue;
	
	public NumericTextField(String caption){
		setCaption(caption);		
		setImmediate(true);
		setTextChangeEventMode(TextChangeEventMode.EAGER);
		addTextChangeListener(this);
	}

	@Override
	public void textChange(TextChangeEvent event) {
		
		String text = event.getText();
	    try {
	        new Integer(text);
	        lastValue = text;
	    } catch (NumberFormatException e) {
	        if(lastValue != null){
	        	setValue(lastValue);
	        }else{
	        	setValue("1");
	        }
	    }
	}
	
}
