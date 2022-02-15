package com.digital.opuserp.util;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class GenericDialog extends Window {	

	private String customStyle = "GenericDialog";
	
	private String captionWin = "Confirme para Continuar!";
	private String message = "Realmente Deseja Seguir Nessa Ação?";
	private String captionOK = "SIM";
	private String captionCANCEL = "NAO";
	
	private String width = "250px";
	private String heigth = "140px";
	
	private boolean center = true;
	private boolean bloquearTela = false;
	
	
	Button btOK;
	Button btCANCEL;
	
	
	public GenericDialog(String caption, String message, String captionOK, String captionCANCEL, String width, String heigth, boolean center, boolean bloquearTela){
		
		this.captionWin = caption;
		this.message = message;
		this.captionOK = captionOK;
		this.captionCANCEL = captionCANCEL;
		this.width = width;
		this.heigth = heigth;
		this.center = center;
		this.bloquearTela = bloquearTela;
		
		init();
		
	}
	
	public GenericDialog(String caption, String message, boolean center,boolean bloquearTela){
		
		this.captionWin = caption;
		this.message = message;
		this.center = center;
		this.bloquearTela = bloquearTela;
		
		init();
	}
		
	private void init(){
		
		setStyleName(this.customStyle);
		
		setCaption(this.captionWin);
		setWidth(this.width);
		setHeight(this.heigth);
		setClosable(false);
		setResizable(false);
		
		if(center){
			center();
		}
		if(bloquearTela){
			setModal(true);
		}
		
		VerticalLayout vlroot = new VerticalLayout();
		vlroot.setSizeFull();
		vlroot.setStyleName(this.customStyle);
		
		Label lbMesage = new Label(this.message);
		lbMesage.addStyleName(this.customStyle);
		vlroot.addComponent(lbMesage);
		vlroot.setExpandRatio(lbMesage, 2);
		vlroot.setStyleName(this.customStyle);
		
		HorizontalLayout hlButton = new HorizontalLayout();
		hlButton.addComponent(buildBtOK());
		hlButton.addComponent(buildBtCANCEL());
		hlButton.setStyleName(this.customStyle);
		
		vlroot.addComponent(hlButton);
		vlroot.setComponentAlignment(hlButton, Alignment.BOTTOM_CENTER);
				
		setContent(vlroot);
		
		
	}
	private Button buildBtOK(){
		btOK = new Button(this.captionOK,new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				closeWindow(true);
			}
		});
		btOK.setStyleName("btOK_"+this.customStyle);
		btOK.focus();
		
//		ShortcutListener sl = new ShortcutListener("OK", ShortcutActionHandler.) {
//			
//			@Override
//			public void handleAction(Object sender, Object target) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
		
		return btOK;
	}
	private Button buildBtCANCEL(){
		btCANCEL = new Button(this.captionCANCEL,new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				closeWindow(false);
			}
		});
		btCANCEL.setStyleName("btCANCEL_"+this.customStyle);
		
		return btCANCEL;
	}
	
	private void closeWindow(boolean confirm){
		try{
			fireEvent(new DialogEvent(this, confirm));
			close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	public void setCaptionWin(String captionWin) {
		this.captionWin = captionWin;
	}	
	public void setMessage(String message) {
		this.message = message;
	}	
	public void setCaptionOK(String captionOK) {
		this.captionOK = captionOK;
	}	
	public void setCaptionCANCEL(String captionCANCEL) {
		this.captionCANCEL = captionCANCEL;
	}	
	public void setWidth(String width) {
		this.width = width;
	}	
	public void setHeigth(String heigth) {
		this.heigth = heigth;
	}	
	public void setCenter(boolean center) {
		this.center = center;
	}	
	public void setCustomStyle(String customStyle) {
		this.customStyle = customStyle;
	}	
	public void setBloquearTela(boolean bloquearTela) {
		this.bloquearTela = bloquearTela;
	}
	
	public void addListerner(DialogListerner target){
		try {
			Method method = DialogListerner.class.getDeclaredMethod("onClose", new Class[] {DialogEvent.class});
			addListener(DialogEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(DialogListerner target){
		removeListener(DialogEvent.class, target);
	}
	public static class DialogEvent extends Event{

		private boolean confirm;
		
		public DialogEvent(Component source, boolean isConfirm) {
			super(source);
			this.confirm = isConfirm;
		}
		
		public boolean isConfirm(){
			return this.confirm;
		}
	}
	public interface DialogListerner extends Serializable{
		public void onClose(DialogEvent event);
	}
	
	
}
