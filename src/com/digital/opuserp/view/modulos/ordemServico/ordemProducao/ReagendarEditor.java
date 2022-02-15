package com.digital.opuserp.view.modulos.ordemServico.ordemProducao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.digital.opuserp.view.util.Notify;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ReagendarEditor extends Window {
	
	
	private DateField dfDataPrevisao;
	private Button btSalvar;
	private Button btCancelar;
	private VerticalLayout vlRoot;
	
	public ReagendarEditor(String title, boolean modal){
		
	
		
		
		setWidth("356px");
		setHeight("171px");
				
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
	
	
	public void buildLayout(){
		
	
				
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					dfDataPrevisao = new DateField("Previsãoº");					
					dfDataPrevisao.setStyleName("caption-align");	
					dfDataPrevisao.setDateFormat("dd/MM/yyyy HH:mm:ss");
					dfDataPrevisao.setResolution(DateField.RESOLUTION_HOUR);
					dfDataPrevisao.setResolution(DateField.RESOLUTION_MIN);
					dfDataPrevisao.setWidth("130px");
					dfDataPrevisao.setRequired(true);
					dfDataPrevisao.focus();
														
					addComponent(dfDataPrevisao);
				}
		});
				
		
	}
	
	public Button buildBtSalvar() {
		btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(dfDataPrevisao.isValid()){
					try {					
							
						fireEvent(new ReagendarOspEvent(getUI(), dfDataPrevisao.getValue(), true));
						
						
					} catch (Exception e) {
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
						e.printStackTrace();
					}
					
				}else{				
					 
					 Notify.Show_Invalid_Submit_Form();
				}
				
			}
		});
		btSalvar.setStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("Salvar", ShortcutAction.KeyCode.ENTER, null) {
			
			
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);		
		return btSalvar;
	}

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
							
				fireEvent(new ReagendarOspEvent(getUI(), null, false));
				close();			
			}
		});
		
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}
	
	
	public void addListerner(ReagendarOspListerner target){
		try {
			Method method = ReagendarOspListerner.class.getDeclaredMethod("onClose", ReagendarOspEvent.class);
			addListener(ReagendarOspEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ReagendarOspListerner target){
		removeListener(ReagendarOspEvent.class, target);
	}
	public static class ReagendarOspEvent extends Event{
				
		private boolean confirm;
		private Date data;
		
		public ReagendarOspEvent(Component source,Date data, boolean confirm) {
			super(source);		
			this.confirm = confirm;
			this.data = data;
		}
			
		public boolean isConfirm() {
			return confirm;
		}		
		public Date getData(){
			return data;
		}
	}
	public interface ReagendarOspListerner extends Serializable{
		public void onClose(ReagendarOspEvent event);
	}
	
	
	

}

