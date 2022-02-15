package com.digital.opuserp.view.modulos.acesso.redes;

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
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class redesEditor extends Window implements GenericEditor {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String identificacaoAtual;
	boolean allow_save;
	
	public redesEditor(Item item, String title, boolean modal){
		this.item = item;
		
		if (item.getItemProperty("identificacao").getValue() != null) {
			identificacaoAtual = item.getItemProperty("identificacao").getValue().toString();
			allow_save = true;
		}
		
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
	
	
	public void updateCep(Ceps cep) {
		
		if(cep != null && 
		   cep.getCep() != null && 
		   cep.getEndereco() != null && 
		   cep.getBairro() != null && 
		   cep.getCidade() != null && 
		   cep.getPais() != null && 
		   cep.getUf() != null){

			((TextField) fieldGroup.getField("endereco")).setValue(cep.getEndereco().toUpperCase());

		}
		
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
									
					final TextField txtIdentificacao = (TextField)fieldGroup.buildAndBind("Identificação", "identificacao");				
					txtIdentificacao.setWidth("300px");				
					txtIdentificacao.setStyleName("caption-align-concentrador");
					txtIdentificacao.setNullRepresentation("");
					txtIdentificacao.focus();
					txtIdentificacao.setRequired(true);
					txtIdentificacao.setMaxLength(200);
					txtIdentificacao.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
							
							if(event.getText() != null && !event.getText().equals("")){
								
								if(!event.getText().equals(identificacaoAtual)){
								
									CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
									
									 Pattern pattern = Pattern.compile("[a-z-_0-9]{4,}");
								     Matcher matcher = pattern.matcher(event.getText());
		
								     if(SwithDAO.findByNome(event.getText()) != null){
											allow_save = false;
											event.getComponent().setStyleName("invalid-cpf");
										}else{
											allow_save = true;
											event.getComponent().setStyleName("valid-cpf");
										}
								
								}else{
									event.getComponent().setStyleName("valid-cpf");
									allow_save = true;
								}
							}
							
							
							
						}
					});
					
					addComponent(txtIdentificacao);											
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					
					JPAContainer<Concentrador> bases = JPAContainerFactory.make(Concentrador.class, ConnUtil.getEntity());
					bases.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
										
					ComboBox cbConcentrador = new ComboBox("Concentrador", bases);
					cbConcentrador.setItemCaptionPropertyId("identificacao");
					cbConcentrador.setNullSelectionAllowed(false);
					cbConcentrador.setRequired(true);
					cbConcentrador.setConverter(new SingleSelectConverter(cbConcentrador));																	
					cbConcentrador.setStyleName("caption-align-concentrador");
					cbConcentrador.setWidth("300px");
					
					
					addComponent(cbConcentrador);
					fieldGroup.bind(cbConcentrador, "concentrador");
				}
			});
		
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom-new");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Modelo", "modelo");				
					tfCod.setWidth("300px");				
					tfCod.setStyleName("caption-align-concentrador");
					tfCod.setNullRepresentation("");					
					tfCod.setRequired(true);
					tfCod.setMaxLength(200);
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});

		
		// Endereco e Número
				vlRoot.addComponent(new HorizontalLayout() {
					{
//						setWidth("90%");
						FormLayout flEndereco = new FormLayout();
						flEndereco.setMargin(true);
						flEndereco.setSpacing(true);
						flEndereco.setWidth("100%");
						flEndereco.setStyleName("form-cutom");
						flEndereco.addComponent(fieldGroup.buildAndBind("Endereço","endereco"));
						((TextField) fieldGroup.getField("endereco")).setWidth("300px");
						((TextField) fieldGroup.getField("endereco")).setNullRepresentation("");
						((TextField) fieldGroup.getField("endereco")).setStyleName("caption-align-base");
						((TextField) fieldGroup.getField("endereco")).setRequired(true);
						((TextField) fieldGroup.getField("endereco")).setImmediate(true);
						((TextField) fieldGroup.getField("endereco")).setTextChangeEventMode(TextChangeEventMode.LAZY);
						((TextField) fieldGroup.getField("endereco")).addBlurListener(new FieldEvents.BlurListener() {

							@Override
							public void blur(BlurEvent event) {												
								if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
									
									((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
								}
							}
						});
										
						addComponent(flEndereco);
						setExpandRatio(flEndereco, 1);

						
						final Button btSearch = new Button(null, new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								com.digital.opuserp.view.util.CepUtil cep = new com.digital.opuserp.view.util.CepUtil(true, true);
								cep.addListerner(new com.digital.opuserp.view.util.CepUtil.CepListerner() {

//									final String cepEndereco = ((TextField) fgEndereco.getField("cep")).getValue();
									
									@Override
									public void onSelected(com.digital.opuserp.view.util.CepUtil.CepEvent event) {
										updateCep(event.getCep());
									}
								});

								getUI().addWindow(cep);
							}
						});
				
				btSearch.setStyleName(BaseTheme.BUTTON_LINK);
				btSearch.setIcon(new ThemeResource("icons/search-16.png"));
				btSearch.setDescription("Procurar por Cep");

				addComponent(new FormLayout() {
					{
						setStyleName("form-cutom");
						addComponent(btSearch);
					}
				});

						
						addComponent(new FormLayout() {
							{
								setMargin(true);
								setSpacing(true);
								setWidth("50px");
								setStyleName("form-cutom");
								addComponent(fieldGroup.buildAndBind("Nº","numero"));
								((TextField) fieldGroup.getField("numero")).setWidth("64px");
								((TextField) fieldGroup.getField("numero")).setNullRepresentation("");
								((TextField) fieldGroup.getField("numero")).setRequired(true);
								((TextField) fieldGroup.getField("numero")).setMaxLength(10);
								((TextField) fieldGroup.getField("numero")).setTextChangeEventMode(TextChangeEventMode.LAZY);

							}
						});
					}
				});
				
				vlRoot.addComponent(
						new FormLayout(){					
						{
							
							setMargin(true);
							setSpacing(true);
							setWidth("100%");
							setStyleName("form-cutom");						
											
							TextField tfCod = (TextField)fieldGroup.buildAndBind("Referencia", "referencia");	
							tfCod.setWidth("100%");				
							tfCod.setStyleName("caption-align-concentrador");
							tfCod.setNullRepresentation("");					
							tfCod.setRequired(false);
							tfCod.setMaxLength(250);
							
							addComponent(tfCod);
							setExpandRatio(tfCod, 2);		
						}
					});
		
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && allow_save){
					try {
						fieldGroup.commit();				
						fireEvent(new SwithEvent(getUI(), item, true));
						
						if (item.getItemProperty("id").getValue() != null) {												
							Notify.Show("Swith Alterado com Sucesso!", Notify.TYPE_SUCCESS);
						}
						
					} catch (CommitException e) {
						Notify.Show("Não foi Possivel Salvar o Swith!", Notify.TYPE_ERROR);
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
					fireEvent(new SwithEvent(getUI(), item, false));
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
											fireEvent(new SwithEvent(getUI(), item, true));
		
											if(fieldGroup.isModified()){
												Notify.Show("Swith Salvo com Sucesso!", Notify.TYPE_SUCCESS);
											}
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
									fireEvent(new SwithEvent(getUI(), item, false));
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
	
	
	public void addListerner(SwithListerner target){
		try {
			Method method = SwithListerner.class.getDeclaredMethod("onClose", SwithEvent.class);
			addListener(SwithEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(SwithListerner target){
		removeListener(SwithEvent.class, target);
	}
	public static class SwithEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public SwithEvent(Component source, Item item, boolean confirm) {
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
	public interface SwithListerner extends Serializable{
		public void onClose(SwithEvent event);
	}
	
}
