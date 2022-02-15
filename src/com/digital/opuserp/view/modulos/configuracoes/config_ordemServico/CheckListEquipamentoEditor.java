package com.digital.opuserp.view.modulos.configuracoes.config_ordemServico;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.SwithDAO;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.caixa_atendimento.CaixaAtendimentoEditor.SwithEvent;
import com.digital.opuserp.view.modulos.acesso.caixa_atendimento.CaixaAtendimentoEditor.SwithListerner;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.themes.BaseTheme;

public class CheckListEquipamentoEditor extends Window implements GenericEditor {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String identificacaoAtual;
	boolean allow_save;
	
	public CheckListEquipamentoEditor(Item item, String title, boolean modal){
		this.item = item;
		
//		if (item.getItemProperty("identificacao").getValue() != null) {
//			identificacaoAtual = item.getItemProperty("identificacao").getValue().toString();
//			allow_save = true;
//		}
		
		configLayout();	
		
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
	
	
	
	
	private void configLayout(){
		////1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("723px");
				setHeight("278px");		
		//}
	}
	
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
				new FormLayout(){					
				{					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
					
					TextField txtNome = new TextField("Nome");
					txtNome.setStyleName("caption-align-concentrador");
					txtNome.setWidth("200px");
										
					addComponent(txtNome);
					fieldGroup.bind(txtNome, "nome");
				}
			});	
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid()){
					try {
						fieldGroup.commit();				
						fireEvent(new CheckListEquipamentoEvent(getUI(), item, true));						
						
					} catch (CommitException e) {
						Notify.Show("Não foi Possivel Salvar o Item!", Notify.TYPE_ERROR);
						e.printStackTrace();
					}
					close();
				}else{
					Notify.Show_Invalid_Submit_Form();
				}
				
			}
		});
		btSalvar.setStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("Salvar", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);		
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new CheckListEquipamentoEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
								if(event.isConfirm() && allow_save){
									if(fieldGroup.isValid()){
										try {
											fieldGroup.commit();				
											fireEvent(new CheckListEquipamentoEvent(getUI(), item, true));		
											
											close();
										} catch (Exception e) {
											Notify.Show("Não foi Possivel Salvar o Concentrador!", Notify.TYPE_ERROR);
											e.printStackTrace();
										}
									}else{
										Notify.Show_Invalid_Submit_Form();
									}
								}else{							
									fieldGroup.discard();				
									fireEvent(new CheckListEquipamentoEvent(getUI(), item, false));
									close();													
								}
							
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
			}
		});
		
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}
	
	
	public void addListerner(CheckListEquipamentoListerner target){
		try {
			Method method = CheckListEquipamentoListerner.class.getDeclaredMethod("onClose", CheckListEquipamentoEvent.class);
			addListener(CheckListEquipamentoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CheckListEquipamentoListerner target){
		removeListener(CheckListEquipamentoEvent.class, target);
	}
	public static class CheckListEquipamentoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public CheckListEquipamentoEvent(Component source, Item item, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;			
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface CheckListEquipamentoListerner extends Serializable{
		public void onClose(CheckListEquipamentoEvent event);
	}
	
}
