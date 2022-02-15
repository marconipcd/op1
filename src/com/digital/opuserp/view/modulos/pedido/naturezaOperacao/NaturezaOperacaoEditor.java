package com.digital.opuserp.view.modulos.pedido.naturezaOperacao;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.util.Notify;
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

public class NaturezaOperacaoEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TextField tfCod;
	
		
	private ComboBox cbVisualizar;
	private ComboBox cbEcf;
	
	public NaturezaOperacaoEditor(final Item item, String title, boolean modal){
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
					
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					ComboBox cbStatus = new ComboBox("Status");
					cbStatus.setNullSelectionAllowed(false);
					cbStatus.setTextInputAllowed(false);
					cbStatus.setRequired(true);
					cbStatus.addItem("ATIVO");
					cbStatus.addItem("INATIVO");
					cbStatus.addStyleName("caption-align");
					
					
					addComponent(cbStatus);				
					fieldGroup.bind(cbStatus,"status");
				}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					TextField txtDescricao = new TextField("Descrição");
					txtDescricao.setRequired(true);
					txtDescricao.setNullRepresentation("");
					txtDescricao.addStyleName("caption-align");
					txtDescricao.setWidth("250px");
					
					addComponent(txtDescricao);				
					fieldGroup.bind(txtDescricao,"descricao");
				}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					TextField txtAplicacao = new TextField("Aplicação");
					txtAplicacao.setRequired(true);
					txtAplicacao.setNullRepresentation("");
					txtAplicacao.addStyleName("caption-align");
					txtAplicacao.setWidth("250px");
					
					addComponent(txtAplicacao);				
					fieldGroup.bind(txtAplicacao,"aplicacao");
				}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					cbVisualizar = new ComboBox("Visualizar");
					cbVisualizar.setTextInputAllowed(false);
					cbVisualizar.setNullSelectionAllowed(false);
					cbVisualizar.setRequired(true);
					cbVisualizar.addItem("SIM");
					cbVisualizar.addItem("NAO");
					cbVisualizar.addStyleName("caption-align");
					
					addComponent(cbVisualizar);		
					
					if(item.getItemProperty("visualizar").getValue() != null && (boolean)item.getItemProperty("visualizar").getValue()){
						cbVisualizar.select("SIM");
					}
					
					if(item.getItemProperty("visualizar").getValue() != null && !(boolean)item.getItemProperty("visualizar").getValue()){
						cbVisualizar.select("NAO");
					}
					
				}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					cbEcf = new ComboBox("Emitir ECF");
					cbEcf.setTextInputAllowed(false);
					cbEcf.setNullSelectionAllowed(false);
					cbEcf.setRequired(true);
					cbEcf.addItem("SIM");
					cbEcf.addItem("NAO");
					cbEcf.addStyleName("caption-align");
					
					addComponent(cbEcf);		
					
					fieldGroup.bind(cbEcf,"emitir_ecf");
				}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					TextField txtStatusSaida = new TextField("Status de saída");					
					txtStatusSaida.setRequired(true);					
					txtStatusSaida.addStyleName("caption-align");
					
					addComponent(txtStatusSaida);		
					
					fieldGroup.bind(txtStatusSaida,"status_saida");
				}
		});
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
					
				ComboBox cbTipo = new ComboBox("Tipo");		
				cbTipo.setNullSelectionAllowed(false); 
				cbTipo.setRequired(true);
				cbTipo.addItem("ENTRADA");
				cbTipo.addItem("SAIDA");
				cbTipo.addStyleName("caption-align");
				
				addComponent(cbTipo);		
				
				fieldGroup.bind(cbTipo,"tipo");
			}
		});
				
	}
	
	
	
	public Button buildBtFechar(){
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		return btCancelar;
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && cbVisualizar.isValid()){
					try {	
						
						item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
						
						if(cbVisualizar.getValue().toString().equals("SIM")){
							item.getItemProperty("visualizar").setValue(true);
						}else{
							item.getItemProperty("visualizar").setValue(false);
						}
						
						fieldGroup.commit();						
						fireEvent(new NaturezaOperacaoEvent(getUI(), item, true));						
							
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
					fireEvent(new NaturezaOperacaoEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(fieldGroup.isValid() && cbVisualizar.isValid()){
									try {				
										
										fieldGroup.commit();				
										fireEvent(new NaturezaOperacaoEvent(getUI(), item, true));						
											
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
							}else{							
								fieldGroup.discard();				
								fireEvent(new TransportadoraEvent(getUI(), item, false));
								close();						
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
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
	
	
	public void addListerner(NaturezaOperacaoListerner target){
		try {
			Method method = NaturezaOperacaoListerner.class.getDeclaredMethod("onClose", NaturezaOperacaoEvent.class);
			addListener(NaturezaOperacaoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(NaturezaOperacaoListerner target){
		removeListener(NaturezaOperacaoEvent.class, target);
	}
	public static class NaturezaOperacaoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public NaturezaOperacaoEvent(Component source, Item item, boolean confirm) {
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
	public interface NaturezaOperacaoListerner extends Serializable{
		public void onClose(NaturezaOperacaoEvent event);
	}

	
}
