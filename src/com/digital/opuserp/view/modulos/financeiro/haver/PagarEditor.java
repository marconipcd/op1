package com.digital.opuserp.view.modulos.financeiro.haver;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PagarEditor extends Window  {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private TextField txtValor;
	
	public PagarEditor(final Item item, String title, boolean modal){
		this.item = item;
				
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
			
				hlButtons.addComponent(buildBtCancelar("Cancelar"));
				hlButtons.addComponent(buildBtSalvar());
				
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		buildLayout();		
	}
	
	boolean valid_valor = false;
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					txtValor = new TextField ("Valor do Haver");				
					txtValor.setWidth("80px");				
					txtValor.setStyleName("caption-align");
					txtValor.setNullRepresentation("");			
					txtValor.setRequired(true);
					txtValor.setId("txtValor");
					txtValor.addStyleName("align-currency");
					
					txtValor.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
								if(event.getText() != null && !event.getText().equals("0,00")){
									
										double vlr_pagar = Real.formatStringToDBDouble(event.getText());
										double vlr_disponivel = (double)item.getItemProperty("valor_total").getValue();
									
										if(vlr_pagar <= vlr_disponivel){
											txtValor.removeStyleName("invalid-txt");
											valid_valor  = true;
										}else{
											txtValor.addStyleName("invalid-txt");
											valid_valor  = false;
										}
									
								}
						}
					});
					
					addComponent(txtValor);					
										
					JavaScript.getCurrent().execute("$('#txtValor').maskMoney({decimal:',',thousands:'.'})");
				}
			});		
	}
		
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(valid_valor){
					try {												
						fieldGroup.commit();				
						fireEvent(new PagarHaverEvent(getUI(), item, Real.formatStringToDBDouble(txtValor.getValue()),true));					
						
					} catch (Exception e) {					
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(),Notify.TYPE_ERROR);
					}
				}else{
					
					for (Field<?> field: fieldGroup.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
					}
					
					if(!valid_valor){
						txtValor.addStyleName("invalid-txt");
					}else{
						txtValor.removeStyleName("invalid-txt");
					}
									
					Notify.Show_Invalid_Submit_Form();
				}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}

	
	public Button buildBtCancelar(String lb) {
		btCancelar = new Button(lb, new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new PagarHaverEvent(getUI(), item,0,false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(txtValor.isValid()){
									try {											
										fieldGroup.commit();				
										fireEvent(new PagarHaverEvent(getUI(), item,Real.formatStringToDBDouble(txtValor.getValue()), true));								
									} catch (Exception e) {					
										e.printStackTrace();
										Notify.Show("ERRO: "+e.getLocalizedMessage(),Notify.TYPE_ERROR);
									}
								}else{									
									for (Field<?> field: fieldGroup.getFields()) {										
										if(!field.isValid()){
											field.addStyleName("invalid-txt");
										}else{
											field.removeStyleName("invalid-txt");
										}
									}	
									
									Notify.Show_Invalid_Submit_Form();
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new PagarHaverEvent(getUI(), item,0, false));
								close();						
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		
		return btCancelar;
	}
	
	
	public void addListerner(PagarHaverListerner target){
		try {
			Method method = PagarHaverListerner.class.getDeclaredMethod("onClose", PagarHaverEvent.class);
			addListener(PagarHaverEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(PagarHaverListerner target){
		removeListener(PagarHaverEvent.class, target);
	}
	public static class PagarHaverEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private double valor;
		
		public PagarHaverEvent(Component source, Item item,double valor, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;			
			this.valor = valor;
		}

		public Item getItem() {
			return item;
		}
		public double getValor(){
			return valor;
		}
		public boolean isConfirm() {
			return confirm;
		}		
		
	}
	public interface PagarHaverListerner extends Serializable{
		public void onClose(PagarHaverEvent event);
	}

	
}
