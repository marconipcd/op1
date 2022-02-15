package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SuspenderContratoEditor extends Window implements GenericEditor{

	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
		
	
	DateField txtDataFimSuspensao;
	
	
	public SuspenderContratoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("640px");
		setHeight("161px");
		
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
				hlButtons.setStyleName("hl_buttons_bottom");
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
	
	
	boolean valid_data_suspensao = false;
	public void buildLayout(){

		
		if(!item.getItemProperty("status_2").getValue().toString().equals("SUSPENSO")){
		
			vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					txtDataFimSuspensao = new DateField("Suspender até");
					txtDataFimSuspensao.setRequired(true);
					txtDataFimSuspensao.setStyleName("caption-align-renovar-acesso");
					txtDataFimSuspensao.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							Date dt = txtDataFimSuspensao.getValue();
							
							Integer dias = Days.daysBetween(new DateTime(new Date()), new DateTime(dt)).getDays();
							Integer meses = Months.monthsBetween(new DateTime(new Date()), new DateTime(dt)).getMonths(); 
							
							if(meses > 3){
								valid_data_suspensao = false;
								txtDataFimSuspensao.addStyleName("invalid-cpf");
								txtDataFimSuspensao.removeStyleName("valid-cpf");
							}else{
								valid_data_suspensao = true;
								txtDataFimSuspensao.addStyleName("valid-cpf");
								txtDataFimSuspensao.removeStyleName("invalid-cpf");
							}
							
							
							if(dias < 1){
								valid_data_suspensao = false;
								txtDataFimSuspensao.addStyleName("invalid-cpf");
								txtDataFimSuspensao.removeStyleName("valid-cpf");
							}
						}
					});
									
					addComponent(txtDataFimSuspensao);
				}
			});
		
		}
	}
		

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(txtDataFimSuspensao.getValue() != null && valid_data_suspensao){
															
					fireEvent(new SuspenderContratoEvent(getUI(),true, txtDataFimSuspensao.getValue()));					
					
				}else{
					
					if(txtDataFimSuspensao.getValue() == null){
						Notify.Show("Informe uma data para o Fim da Suspensão!", Notify.TYPE_ERROR);
					}
					
					if(!valid_data_suspensao){
						Notify.Show("A Data informada excede o limite!", Notify.TYPE_ERROR);
					}										
				}
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);		
		btSalvar.setStyleName("default");

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
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
	
	
	public void addListerner(SuspenderContratoListerner target){
		try {
			Method method = SuspenderContratoListerner.class.getDeclaredMethod("onClose", SuspenderContratoEvent.class);
			addListener(SuspenderContratoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(SuspenderContratoListerner target){
		removeListener(SuspenderContratoEvent.class, target);
	}
	public static class SuspenderContratoEvent extends Event{
				
		private boolean confirm;
		private Date dataFimSuspensao;		
		private PlanoAcesso plano;
		
		public SuspenderContratoEvent(Component source,boolean confirm, Date dataFimSuspensao) {
			super(source);
			
			this.dataFimSuspensao = dataFimSuspensao;
			this.confirm = confirm;			
		}
		
		public PlanoAcesso getPlanoNovo() {
			return plano;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
		
		public Date getDataFimSuspensao(){
			return dataFimSuspensao;
		}
	}
	public interface SuspenderContratoListerner extends Serializable{
		public void onClose(SuspenderContratoEvent event);
	}
}
