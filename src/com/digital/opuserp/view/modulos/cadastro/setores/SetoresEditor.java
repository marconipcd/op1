package com.digital.opuserp.view.modulos.cadastro.setores;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.SetoresDAO;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SetoresEditor extends Window{
	
	Button btCancelar;
	Button btSalvar;	
	VerticalLayout vlRoot;
	
	Boolean validSetor;
	String setorCadastrado;
	String setorAtual;
	
	private TextField tfNome;
	
	private Integer id;
	private String nome;
	
		
	public SetoresEditor(Integer id,String nome, String titulo){
		
		this.id = id;
		this.nome = nome;
		
		center();
		setModal(true);
		setCaption(titulo);
		setClosable(false);
		setResizable(false);
		
		btSalvar = buildBtSalvar();
		btCancelar = buildBtCancelar();
		
		vlRoot = new VerticalLayout();
		vlRoot.setWidth("100%");
		vlRoot.addStyleName("border-form");
		vlRoot.setMargin(true);
		
		
		if(nome != null){
			setorAtual  = nome;
			validSetor = true;
		}
		
		
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
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				tfNome = new TextField("Nome");
				tfNome.setRequired(true);
				tfNome.setNullRepresentation("");
				tfNome.setWidth("300px");
				tfNome.focus();
				tfNome.setImmediate(true);
				tfNome.setStyleName("caption-align");
				tfNome.setTextChangeEventMode(TextChangeEventMode.LAZY);
				tfNome.addBlurListener(new FieldEvents.BlurListener() {
					
					@Override
					public void blur(BlurEvent event) {
							if(event.getComponent() != null && ((TextField) (event.getComponent())) != null && event.getComponent().toString() != null){
								((TextField) (event.getComponent())).setValue(((TextField) (event.getComponent())).getValue().toUpperCase());
							}
						}
					});
				

				tfNome.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						setorCadastrado = event.getText();
						setorCadastrado.replaceAll(" ",""); 
						
																	
						if(!event.getText().toUpperCase().equals(setorAtual) && !event.getText().isEmpty()){
							
								Integer codEmpresa = Integer.parseInt(OpusERP4UI.getEmpresa().getId().toString());
								
								SetoresDAO stDAO = new SetoresDAO();
								boolean setor = stDAO.getSetores(codEmpresa, event.getText().toUpperCase());
								
								if(setor){
									validSetor = true;
									event.getComponent().addStyleName("setor-valido");
									event.getComponent().removeStyleName("setor-invalido");
								}else{
									validSetor = false;
									event.getComponent().addStyleName("setor-invalido");
									event.getComponent().removeStyleName("setor-valido");
								}
							
						}else{
							validSetor = true;
							event.getComponent().removeStyleName("setor-invalido");
							event.getComponent().removeStyleName("setor-valido");
						}						
					}					
					
				});
				
				addComponent(tfNome);
				tfNome.setValue(nome);
				
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				cbSetorPai = new ComboBox("Setor Pai", getSetores());
				cbSetorPai.setNullSelectionAllowed(true);
				cbSetorPai.setStyleName("caption-align");
				cbSetorPai.setItemCaptionPropertyId("nome");
				
				addComponent(cbSetorPai);
				
				if(id != null){
					
					
					Setores setor = SetoresDAO.find(id);
					
					if(setor != null && setor.getSetor_root() != null){
						cbSetorPai.select(setor.getSetor_root().getId());
					}
				}
				
				
				
			}
		});
	}
	
	private ComboBox cbSetorPai;
	private Setores setor_root = null;
	
	private JPAContainer<Setores>  getSetores(){
		JPAContainer<Setores> setores = JPAContainerFactory.make(Setores.class, ConnUtil.getEntity());
		setores.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(id != null){
			setores.addContainerFilter(Filters.not(Filters.eq("id", id)));
		}
		
		return setores;
	}
	
	private Button buildBtSalvar(){
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				if(cbSetorPai.getValue() != null){
					setor_root = SetoresDAO.find((Integer)cbSetorPai.getItem(cbSetorPai.getValue()).getItemProperty("id").getValue());
				}
			
				if(validSetor == true ){
					try {
						
						nome = tfNome.getValue();

						fireEvent(new SetoresSavedEvent(getUI(), id, nome, setor_root, true));
						
						if(id == null){
							Notification.show("Setor Cadastrado com Sucesso!");
						}else{								
							Notification.show("Setor Alterado com Sucesso!");
						}
						
						close();
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
						Notification.show("Não foi Possivel Salvar as Alterações!");                          
					}
					
				}else{
										
						
					if(!tfNome.isValid()){
						tfNome.addStyleName("invalid-txt");
					}else{
						tfNome.removeStyleName("invalid-txt");
					}
					
					
					Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
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
		
		
		btSalvar.addStyleName("default");
		return btSalvar;
	}
	
	private Button buildBtCancelar(){
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				nome = tfNome.getValue();
				
				if(cbSetorPai.getValue() != null){
					setor_root = SetoresDAO.find((Integer)cbSetorPai.getItem(cbSetorPai.getValue()).getItemProperty("id").getValue());
				}
				
				if(tfNome.isValid()){
					
					final GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							try {
								if(event.isConfirm()){
									
									if(tfNome.isValid() && validSetor == true){

										fireEvent(new SetoresSavedEvent(getUI(), id, nome, setor_root, true));							
										
										if(id == null){
											Notification.show("Setor Cadastrado com Sucesso!");
										}else{								
											Notification.show("Setor Alterado com Sucesso!");
										}
										
										gDialog.close();
										close();
										
									}else{
										Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
									}
								}else{									
									gDialog.close();
									close();
								}
							} catch (Exception e) {
								System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
								Notification.show("Não foi Possivel Salvar as Alterações!");
							}
							
						}
					});
					getUI().addWindow(gDialog);
				}else{
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
	
	
	public void addListener(SetoresSavedListener listener){
		try {
			Method method = SetoresSavedListener.class.getDeclaredMethod("setoresSaved", 
					new Class[]{SetoresSavedEvent.class});
			addListener(SetoresSavedEvent.class, listener, method);
		} catch (Exception e) {
			System.out.println("O método não foi encontrado: "+e.getMessage());
		}
	}
	
	public void removeListener(SetoresSavedListener listener){
		removeListener(SetoresSavedEvent.class, listener);
	}
	
	public static class SetoresSavedEvent extends Event{
		
		private boolean confirm;
		
		private Integer id;
		private String nome;
		private Setores setor_root;
		
		public SetoresSavedEvent(Component source, Integer id,String nome,Setores setor_root, boolean confirm) {
			super(source);
			
			this.confirm = confirm;
			
			this.nome = nome;
			this.id = id;			
			this.setor_root = setor_root;
		}
		
		public Integer getId(){
			return id;
		}
		public String getNome(){
			return nome;
		}
		public boolean isConfirm() {
			return confirm;
		}
		public Setores getSetorRoot(){
			return setor_root;
		}
	}
	
	public interface SetoresSavedListener extends Serializable{
		public void setoresSaved(SetoresSavedEvent event);
	}	

}
