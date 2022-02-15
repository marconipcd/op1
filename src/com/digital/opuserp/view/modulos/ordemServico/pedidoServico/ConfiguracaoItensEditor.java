package com.digital.opuserp.view.modulos.ordemServico.pedidoServico;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConfiguracaoItensEditor extends Window {

	
	private Button btSalvar;
	private Button btCancelar;
		
	private FieldGroup fieldGroup;
	private VerticalLayout vlRoot;
	
	private List<Servico> itens;
		
	public ConfiguracaoItensEditor( String title, List<Servico> itens, boolean modal){
		
		this.itens = itens;		
		
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
	
	
	
	public void buildLayout(){
		
		
		
		vlRoot.addComponent(new FormLayout() {
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");
					
						
					
					Table tb = new Table();		
					tb.setSizeFull();
					tb.setSelectable(true);						
					tb.setImmediate(true);
										
					tb.addContainerProperty("Item", String.class, null);
					tb.addContainerProperty("Observacao", TextField.class, new TextField());
					
									
					
					for (Servico ob :itens) {
						tb.addItem(new Object[]{ob.getNome(), new TextField()}, tb.getItemIds().size()+1);						
					}
					
					addComponent(tb);	
					
				
				}
		});
		
	}
	
	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {

				
				fireEvent(new ConfigItensEvent(getUI(), itens, true));
				
//				if(end != null){
//					try {				
//						
//						fireEvent(new ConfigEndEntregaEvent(getUI(), end, true));						
//					} catch (Exception e) {
//						e.printStackTrace();
//						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
//					}
//				}else{
//					
//					
//					Notify.Show_Invalid_Submit_Form();
//				}
				
				
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

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
				//	fireEvent(new ConfigEndEntregaEvent(getUI(), end, true));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								try {
									fieldGroup.commit();				
					//				fireEvent(new ConfigFormaPgtoEvent(getUI(), (FormasPgto)cbFormaPgto.getItem(cbFormaPgto.getValue()), Real.formatStringToDBDouble(txtDescontoPorc.getValue()), Real.formatStringToDBDouble(txtAcrescimoReal.getValue()), Long.parseLong(txtParcelamento.getValue()), dfPrimeiroVenc.getValue(), true));									
								} catch (Exception e) {
									System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
									Notification.show("Não foi Possivel Salvar as Alterações!");
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new ConfigItensEvent(getUI(),null, false));
								
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
	
	
	public void addListerner(ConfigItensListerner target){
		try {
			Method method = ConfigItensListerner.class.getDeclaredMethod("onClose", ConfigItensEvent.class);
			addListener(ConfigItensEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ConfigItensListerner target){
		removeListener(ConfigItensEvent.class, target);
	}
	public static class ConfigItensEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		
		private List<Servico> itens; 
		public ConfigItensEvent(Component source,List<Servico> itens, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;		
			
			this.itens= itens;
		}
		
		public List<Servico> getItens(){
			return itens;
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ConfigItensListerner extends Serializable{
		public void onClose(ConfigItensEvent event);
	}

	
}
