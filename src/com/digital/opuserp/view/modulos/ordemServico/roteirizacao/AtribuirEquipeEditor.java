package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.UsuarioDAO;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AtribuirEquipeEditor  extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TextField tfCod;
	
	private ComboBox cbEquipes;	
	
	public AtribuirEquipeEditor(String title, boolean modal){
		
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
		
		fieldGroup = new FieldGroup(item);
					
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					cbEquipes = new ComboBox("Equipe");	
					
					cbEquipes.addItem("ESTRUTURA LANCAMENTO");
					cbEquipes.addItem("ESTRUTURA FUSAO");
					cbEquipes.addItem("ESTRUTURA ATIVACAO");
					cbEquipes.addItem("ESTRUTURA REPARO");
					
					cbEquipes.setStyleName("caption-align");
					cbEquipes.setNullSelectionAllowed(false);
					//cbEquipes.setItemCaptionPropertyId("username");					
					cbEquipes.setRequired(true);	
					cbEquipes.setTextInputAllowed(false);					
					cbEquipes.focus();					
					
					cbEquipes.setImmediate(true);
										
					addComponent(cbEquipes);				
					//fieldGroup.bind(cbEquipes,"operador");
				}
		});	
	}
	
	
	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(cbEquipes.isValid()){
					try {				
							
						//Usuario u = UsuarioDAO.find((Integer).getItemProperty("id").getValue());
											
						fireEvent(new AtribuirEquipesEvent(getUI(), cbEquipes.getValue().toString(),true));	
							
					} catch (Exception e) {					
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
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
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new AtribuirEquipesEvent(getUI(), null, false));
					close();
				}								
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		
		
		return btCancelar;
	}
	
	
	public void addListerner(AtribuirEquipeListerner target){
		try {
			Method method = AtribuirEquipeListerner.class.getDeclaredMethod("onClose", AtribuirEquipesEvent.class);
			addListener(AtribuirEquipesEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AtribuirEquipeListerner target){
		removeListener(AtribuirEquipesEvent.class, target);
	}
	public static class AtribuirEquipesEvent extends Event{
		
		private String equipe;				
		private boolean confirm;
		
		public AtribuirEquipesEvent(Component source, String usuario, boolean confirm) {
			super(source);
			this.equipe = equipe;	
			this.confirm = confirm;			
		}		

		public String getEquipe() {
			return equipe;
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface AtribuirEquipeListerner extends Serializable{
		public void onClose(AtribuirEquipesEvent event);
	}

	
}

