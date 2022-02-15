package com.digital.opuserp.util;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class BlurListernerUtil implements BlurListener {

	
	
	@Override
	public void blur(BlurEvent event) {
		if(event.getComponent() instanceof TextField){
			try{
				((TextField)event.getComponent()).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
			}catch(Exception e){
				e.printStackTrace();
				Notification.show("ERRO", "O Sistema acaba de se recuperar de um Erro na classe (BlurListernerUtil:21)!", Type.ERROR_MESSAGE);
			}
		}
		
		
	}	
}
