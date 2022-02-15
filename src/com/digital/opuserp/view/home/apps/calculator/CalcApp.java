package com.digital.opuserp.view.home.apps.calculator;

import com.digital.opuserp.dao.PrefeDashDAO;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class CalcApp extends CssLayout implements ClickListener {


	 	private double current = 0.0;
	    private double stored = 0.0;
	    private char lastOperationRequested = 'C';
	    
	    // User interface components
	    private final Label display = new Label("0.0");
	    
	   
	    private Button btClose;
	   
	    
	    public CalcApp(){


	    	addStyleName("layout-panel");
	        setSizeFull();
	        
	       
	        addComponent(buildBtClose());	        
	       
	        display.addStyleName("visor-calc");
	        final GridLayout layout = new GridLayout(4, 5);
	        layout.setCaption("Calculadora");
	        layout.setSizeFull();
	        addComponent(layout);
	        
	        // Create a result label that over all 4 columns in the first row
	        layout.addComponent(display, 0, 0, 3, 0);

	        // The operations for the calculator in the order they appear on the
	        // screen (left to right, top to bottom)
	        String[] operations = new String[] { "7", "8", "9", "/", "4", "5", "6",
	                "*", "1", "2", "3", "-", "0", "=", "C", "+" };

	        for (String caption : operations) {

	            // Create a button and use this application for event handling
	            Button button = new Button(caption);
	            button.setWidth("100%");
	            button.addListener(this);

	            // Add the button to our main layout
	            layout.addComponent(button);
	        }
	    }

		@Override
		public void buttonClick(ClickEvent event) {
			 // Get the button that was clicked
	        Button button = event.getButton();

	        // Get the requested operation from the button caption
	        char requestedOperation = button.getCaption().charAt(0);

	        // Calculate the new value
	        double newValue = calculate(requestedOperation);

	        // Update the result label with the new value
	        display.setValue(String.valueOf(newValue));
		}
		 private double calculate(char requestedOperation) {
		        if ('0' <= requestedOperation && requestedOperation <= '9') {
		            current = current * 10
		                    + Double.parseDouble("" + requestedOperation);
		            return current;
		        }
		        switch (lastOperationRequested) {
		        case '+':
		            stored += current;
		            break;
		        case '-':
		            stored -= current;
		            break;
		        case '/':
		            stored /= current;
		            break;
		        case '*':
		            stored *= current;
		            break;
		        case 'C':
		            stored = current;
		            break;
		        }
		        lastOperationRequested = requestedOperation;
		        current = 0.0;
		        if (requestedOperation == 'C') {
		            stored = 0.0;
		        }
		        return stored;
		    }
		 
		 private Button buildBtClose(){
		    	btClose = new Button();
		    	btClose.setWidth("16px");
//		    	btClose.addStyleName("configure");
		    	btClose.addStyleName("btClose");
		    	btClose.setIcon(new ThemeResource("icons/icon_close.png"));
		    	btClose.addStyleName("icon-only");
		    	btClose.addStyleName("borderless");
		    	btClose.setDescription("Fechar");
		    	btClose.addStyleName("small");  
		    	btClose.addClickListener(new ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	  PrefeDashDAO.remove(CalcApp.class.toString());
		                  ((GridLayout)getParent()).removeComponent(btClose.getParent());   
		            }
		        });
		        
		        return btClose;
		    }
}
