package com.digital.opuserp.view.modulos.ordemServico.pedidoServico;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
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

public class ConfiguracaoEndEntregaEditor extends Window {

	
	private Button btSalvar;
	private Button btCancelar;
		
	private FieldGroup fieldGroup;
	private VerticalLayout vlRoot;
	private Cliente cliente;
	
	private ComboBox cbEndereco;
	private TextField txtComplemento;
	private TextField txtReferencia;
	
	private Endereco end;
		
	public ConfiguracaoEndEntregaEditor( String title, Cliente cliente, boolean modal){
		
		this.cliente = cliente;		
		
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
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
																											
					cbEndereco = new ComboBox("Endereços", getEndereco()){
						@Override
						public String getItemCaption(Object itemId) {
						   Item item = getItem(itemId);
						
						   if (item == null) {
						      return "";
						   }
						     
						   return String.valueOf(item.getItemProperty("cep")) + ", " + 
					   		  String.valueOf(item.getItemProperty("endereco")) + ", " +
					   		  String.valueOf(item.getItemProperty("numero")) + ", " +
					   		  String.valueOf(item.getItemProperty("bairro")) + ", " +
					   		  String.valueOf(item.getItemProperty("cidade"))+ " - " +
					   		  String.valueOf(item.getItemProperty("uf"));
						}
					};
					
					cbEndereco.setWidth("685px");
					cbEndereco.setRequired(true);
					cbEndereco.setNullSelectionAllowed(false);
					cbEndereco.setStyleName("caption-align");	
					cbEndereco.setImmediate(true);
					cbEndereco.setTextInputAllowed(false);
					cbEndereco.setConverter(new SingleSelectConverter(cbEndereco));
					
					cbEndereco.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(cbEndereco.getValue() != null && txtComplemento != null && txtReferencia != null){
								
								if(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue() != null){
									txtComplemento.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("complemento").getValue().toString());
								}else{
									txtComplemento.setValue("");
								}
																
								if(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue() != null){
									txtReferencia.setValue(cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("referencia").getValue().toString());
								}else{
									txtReferencia.setValue("");
								}
								
								end = EnderecoDAO.find((Integer)cbEndereco.getItem(cbEndereco.getValue()).getItemProperty("id").getValue());
								
							}else{
								if(txtComplemento != null && txtReferencia != null){
									txtReferencia.setValue("");
									txtComplemento.setValue("");
								}
							}
						}
					});
									
					addComponent(cbEndereco);										
					
									
				}
		});
		
		vlRoot.addComponent(new HorizontalLayout() {
			{
				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtComplemento = new TextField("Complemento");
						txtComplemento.setNullRepresentation("");
						txtComplemento.setStyleName("caption-align");
						txtComplemento.setWidth("300px");
						txtComplemento.setImmediate(true);
						txtComplemento.setTextChangeEventMode(TextChangeEventMode.LAZY);
						txtComplemento.setRequired(true);
						txtComplemento.setDescription("Casa, Apto, Galpão, Anexo, etc");						
						
						addComponent(txtComplemento);	
						
					
					}
				});

				addComponent(new FormLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtReferencia = new TextField("Referência");
						txtReferencia.setNullRepresentation("");
						txtReferencia.setWidth("300px");
						txtReferencia.setImmediate(true);
						txtReferencia.setTextChangeEventMode(TextChangeEventMode.LAZY);
						
						addComponent(txtReferencia);
						
						
					}
				});
			}
		});		
		
	}
	private JPAContainer<Endereco> getEndereco(){
		JPAContainer<Endereco> container = JPAContainerFactory.make(Endereco.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("clientes", cliente));
		return container;
	}
	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {

				if(end != null){
					try {				
						
						fireEvent(new ConfigEndEntregaEvent(getUI(), end, true));						
					} catch (Exception e) {
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
					}
				}else{
					
					
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

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new ConfigEndEntregaEvent(getUI(), end, true));
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
								fireEvent(new ConfigEndEntregaEvent(getUI(),null, false));
								
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
	
	
	public void addListerner(ConfigEndEntregaListerner target){
		try {
			Method method = ConfigEndEntregaListerner.class.getDeclaredMethod("onClose", ConfigEndEntregaEvent.class);
			addListener(ConfigEndEntregaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ConfigEndEntregaListerner target){
		removeListener(ConfigEndEntregaEvent.class, target);
	}
	public static class ConfigEndEntregaEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		
		private Endereco end;
		public ConfigEndEntregaEvent(Component source,Endereco end, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;		
			
			this.end = end;
		}
		
		public Endereco getEndereco(){
			return end;
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ConfigEndEntregaListerner extends Serializable{
		public void onClose(ConfigEndEntregaEvent event);
	}

	
}
