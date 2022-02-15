package com.digital.opuserp.view.modulos.acesso.concentrador;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Base;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Validator;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConcentradorEditor extends Window implements GenericEditor {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	boolean valid_ip;
	boolean apiValid ;
		
	public ConcentradorEditor(Item item, String title, boolean modal){
		this.item = item;
		
		
		//configLayout();	
		
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
		
		if (item.getItemProperty("id").getValue() != null) {
			valid_ip = true;
			apiValid = true;
		} else {
			valid_ip  = false;
			apiValid  = false;
		}
		
		buildLayout();
	}
	
	
	
	
	private void configLayout(){
		////1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("862px");
				setHeight("460px");		
		//}
	}
	
	public void updateCep(Ceps cep){
		((TextField) fieldGroup.getField("cep")).setValue(cep.getCep());
		((TextField) fieldGroup.getField("endereco")).setValue(cep.getEndereco());
		((TextField) fieldGroup.getField("numero")).setValue("");
		((TextField) fieldGroup.getField("bairro")).setValue(cep.getBairro());
		((TextField) fieldGroup.getField("cidade")).setValue(cep.getCidade());
		((TextField) fieldGroup.getField("uf")).setValue(cep.getUf());
		((TextField) fieldGroup.getField("pais")).setValue(cep.getPais());
		((TextField) fieldGroup.getField("numero")).focus();
	}
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Identificação", "identificacao");				
					tfCod.setWidth("200px");				
					tfCod.setStyleName("caption-align-concentrador");
					tfCod.setNullRepresentation("");
					tfCod.focus();
					tfCod.setMaxLength(100);
					tfCod.setRequired(true);
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					
					JPAContainer<Base> bases = JPAContainerFactory.make(Base.class, ConnUtil.getEntity());
					bases.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
					
					ComboBox cbBase = new ComboBox("Local", bases);
					cbBase.setItemCaptionPropertyId("nome");
					cbBase.setNullSelectionAllowed(false);
					cbBase.setRequired(true);
					cbBase.setConverter(new SingleSelectConverter(cbBase));																	
					cbBase.setStyleName("caption-align-concentrador");
					
					
					addComponent(cbBase);
					fieldGroup.bind(cbBase, "base");
				}
			});
		
		vlRoot.addComponent(	new FormLayout(){
				{
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");

					ComboBox CbInfra = new ComboBox("Infraestrutura");
					CbInfra.addItem("RADIO FREQUENCIA");
					CbInfra.addItem("PAR METALICO");
					CbInfra.addItem("FIBRA OPTICA");
					CbInfra.setNullSelectionAllowed(true);
//					CbInfra.setRequired(true);
					CbInfra.setTextInputAllowed(false);
					CbInfra.setStyleName("caption-align-concentrador");
					
					addComponent(CbInfra);
					fieldGroup.bind(CbInfra, "infraestrutura");
											
					
				}
		});
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom-new");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Modelo", "modelo");				
					tfCod.setWidth("200px");				
					tfCod.setStyleName("caption-align-concentrador");
					tfCod.setNullRepresentation("");


					tfCod.setRequired(true);
					tfCod.setMaxLength(200);
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom-new");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Antena", "antena");				
					tfCod.setWidth("200px");				
					tfCod.setStyleName("caption-align-concentrador");
					tfCod.setNullRepresentation("");

					
					tfCod.setRequired(true);
					tfCod.setMaxLength(200);
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Endereço IP", "endereco_ip");				
					tfCod.setWidth("150px");				
					tfCod.setStyleName("caption-align-concentrador");
					tfCod.setNullRepresentation("");				
					tfCod.setRequired(true);
					tfCod.setMaxLength(20);
					tfCod.addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
						
							if(!event.getText().equals("") && event.getText() != null){
								if(Validator.IPAddress(event.getText())){
									event.getComponent().setStyleName("valid-cpf");
									valid_ip = true;
								}else{
									event.getComponent().setStyleName("invalid-cpf");
									valid_ip = false;
								}
							}
						}
					});
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Loopback", "loop_back");				
					tfCod.setWidth("150px");				
					tfCod.setStyleName("caption-align-concentrador");
					tfCod.setNullRepresentation("");				
					tfCod.setMaxLength(20);
					tfCod.addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
						
							if(!event.getText().equals("") && event.getText() != null){
								if(Validator.IPAddress(event.getText())){
									event.getComponent().setStyleName("valid-cpf");
									valid_ip = true;
								}else{
									event.getComponent().setStyleName("invalid-cpf");
									valid_ip = false;
								}
							}
						}
					});
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});
		
		vlRoot.addComponent(new FormLayout(){					
			{
				
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");						
								
				TextField tfCod = (TextField)fieldGroup.buildAndBind("Usuário", "usuario");				
				tfCod.setWidth("180px");				
				tfCod.setStyleName("caption-align-concentrador");
				tfCod.setNullRepresentation("");
				
				tfCod.setRequired(true);
				tfCod.setMaxLength(40);
				
				addComponent(tfCod);
				setExpandRatio(tfCod, 2);						
			}
		});
		
		vlRoot.addComponent(new FormLayout(){					
			{
				
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");						
								
				TextField tfCod = (TextField)fieldGroup.buildAndBind("Senha", "senha");				
				tfCod.setWidth("180px");				
				tfCod.setStyleName("caption-align-concentrador");
				tfCod.setNullRepresentation("");
				
				tfCod.setRequired(true);
				tfCod.setMaxLength(40);
				
				addComponent(tfCod);
				setExpandRatio(tfCod, 2);						
			}
		});
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Porta API", "porta_api");				
					tfCod.setWidth("60px");				
					tfCod.setStyleName("caption-align-concentrador");
					tfCod.setNullRepresentation("");	
					tfCod.setRequired(true);
					tfCod.setMaxLength(20);
					tfCod.addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
						
							 Pattern pattern = Pattern.compile("\\d*");
						       Matcher matcher = pattern.matcher(event.getText());
						 
							 if(matcher.matches()){
								 event.getComponent().removeStyleName("invalid-cpf");
								 event.getComponent().setStyleName("valid-cpf");
								  apiValid = true;
							 }else{
								 event.getComponent().removeStyleName("valid-cpf");
								 event.getComponent().setStyleName("invalid-cpf");
								 apiValid = false;
							 }
						}
					});
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});
	
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
					
					ComboBox cbWire = new ComboBox("Utiliza Access-List?");
					cbWire.setNullSelectionAllowed(false);
					cbWire.addItem("SIM");
					cbWire.addItem("NAO");
					cbWire.setWidth("80px");
					cbWire.setRequired(true);
					cbWire.setStyleName("caption-align-concentrador");
					
					addComponent(cbWire);
					fieldGroup.bind(cbWire, "wireless");
											
				}
			});
		
		vlRoot.addComponent(	new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextArea taObservacao = new TextArea("Observação");									
					taObservacao.setStyleName("caption-align-base");
					taObservacao.setNullRepresentation("");
					taObservacao.setWidth("100%");
					taObservacao.setHeight("50px");
					taObservacao.setMaxLength(255);
					addComponent(taObservacao);					
					
					fieldGroup.bind(taObservacao, "observacao");
				}
		});
		
		vlRoot.addComponent(	new FormLayout(){
				{
				
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					ComboBox cbStatus = new ComboBox("Status");
					cbStatus.addItem("ATIVO");
					cbStatus.addItem("INATIVO");
					cbStatus.setNullSelectionAllowed(false);
					cbStatus.setStyleName("caption-align-concentrador");
					cbStatus.setTextInputAllowed(false);
					
					addComponent(cbStatus);
					fieldGroup.bind(cbStatus, "status");
							
				}
		});
		
		vlRoot.addComponent(	new FormLayout(){
				{
				
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					ComboBox cbFibra = new ComboBox("Fibra");
					cbFibra.setRequired(true); 
					cbFibra.addItem("SIM");
					cbFibra.addItem("NAO");
					cbFibra.setNullSelectionAllowed(false);
					cbFibra.setStyleName("caption-align-concentrador");
					cbFibra.setTextInputAllowed(false);
					
					addComponent(cbFibra);
					fieldGroup.bind(cbFibra, "fibra");
							
				}
		});
		
		vlRoot.addComponent(	new FormLayout(){
			{
			
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				ComboBox cbSituacao = new ComboBox("Situação");
				cbSituacao.setRequired(true); 
				cbSituacao.addItem("NORMAL");
				cbSituacao.addItem("MANUTENCAO");
				cbSituacao.addItem("PROBLEMA");
				cbSituacao.setNullSelectionAllowed(false);
				cbSituacao.setStyleName("caption-align-concentrador");
				cbSituacao.setTextInputAllowed(false);
				
				addComponent(cbSituacao);
				fieldGroup.bind(cbSituacao, "situacao");
						
			}
		});
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && valid_ip == true && apiValid == true){
					try {
						fieldGroup.commit();				
						fireEvent(new ConcentradorEvent(getUI(), item, true));
						
						if (item.getItemProperty("id").getValue() != null) {
							Notify.Show("Concentrador Alterado com Sucesso!", Notify.TYPE_SUCCESS);
						}
						
					} catch (CommitException e) {						
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
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
					fireEvent(new ConcentradorEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									if(fieldGroup.isValid() && valid_ip == true && apiValid == true){
										try {
											fieldGroup.commit();				
											fireEvent(new ConcentradorEvent(getUI(), item, true));
											
											if (item.getItemProperty("id").getValue() != null) {
												Notify.Show("Concentrador Alterado com Sucesso!", Notify.TYPE_SUCCESS);
											}else{
												Notify.Show("Concentrador Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
											}
											
											close();
										} catch (Exception e) {
											e.printStackTrace();
											Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
										}
									}else{										
										Notify.Show_Invalid_Submit_Form();
									}
								}else{							
									fieldGroup.discard();				
									fireEvent(new ConcentradorEvent(getUI(), item, false));
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
	
	
	public void addListerner(ConcentradorListerner target){
		try {
			Method method = ConcentradorListerner.class.getDeclaredMethod("onClose", ConcentradorEvent.class);
			addListener(ConcentradorEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ConcentradorListerner target){
		removeListener(ConcentradorEvent.class, target);
	}
	public static class ConcentradorEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public ConcentradorEvent(Component source, Item item, boolean confirm) {
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
	public interface ConcentradorListerner extends Serializable{
		public void onClose(ConcentradorEvent event);
	}
	
}
