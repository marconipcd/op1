package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class RemoverMaterial extends Window implements GenericEditor{
	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String motivo;
	ComboBox cbProduto;
	
	Produto material_selecionado;
	private TextField tfDescricaoMaterial;
	private ComboBox cbSerial;
	
	private JPAContainer<SerialProduto> containerSeriais;
		
	boolean valid_mac = false;
	
	private TextField txtSerial;
	private ContratosAcesso contratoAcesso;

	public RemoverMaterial(Item item, String title, boolean modal){
		this.item = item;
		
//		if(item.getItemProperty("endereco_mac") != null && item.getItemProperty("endereco_mac").getValue() != null){
//			valid_mac = true;
//		}
		
		contratoAcesso = (ContratosAcesso) item.getItemProperty("contrato").getValue();
		
		setWidth("751px");
		setHeight("278px");
		
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
				btSalvar.focus();
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		buildLayout();
		
	}
	

	private void buildLayout() {
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField tfCodContrato = new TextField("Contrato");
					tfCodContrato.setValue(item.getItemProperty("id").toString());
					tfCodContrato.setReadOnly(true);
					tfCodContrato.setWidth("62px");
					tfCodContrato.setStyleName("caption-align");
					
					addComponent(tfCodContrato);
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					Cliente cliente = (Cliente) item.getItemProperty("cliente").getValue();
					
					TextField tfNomeCliente = new TextField("Titular Atual");
					tfNomeCliente.setValue(cliente.getNome_razao());
					tfNomeCliente.setReadOnly(true);
					tfNomeCliente.setWidth("360px");
					tfNomeCliente.setStyleName("caption-align");
					
					addComponent(tfNomeCliente);
				}
		});	
		
		final Produto materialAtual = (Produto) item.getItemProperty("material").getValue();
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					
					TextField tfMaterialAtual = new TextField("Material Atual");
					tfMaterialAtual.setValue(materialAtual.getNome());
					tfMaterialAtual.setReadOnly(true);
					tfMaterialAtual.setWidth("360px");
					tfMaterialAtual.setStyleName("caption-align");
					
					addComponent(tfMaterialAtual);
				}
		});	
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					String enderecoMac = item.getItemProperty("endereco_mac").getValue().toString();
					
					TextField tfEnderecoMac = new TextField("Endereço MAC atual");
					tfEnderecoMac.setValue(enderecoMac);
					tfEnderecoMac.setReadOnly(true);
					tfEnderecoMac.setWidth("147px");
					tfEnderecoMac.setStyleName("caption-align");
					
					addComponent(tfEnderecoMac);
				}
		});	
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					
					
					TextField tfRegime = new TextField("Regime");
					tfRegime.setValue(item.getItemProperty("regime").getValue().toString());
					tfRegime.setReadOnly(true);
					tfRegime.setWidth("119px");
					tfRegime.setStyleName("caption-align");
					
					addComponent(tfRegime);
				}
		});	
		
	}

	private JPAContainer<SerialProduto> buildContainerSeriais(Integer codProduto){
		containerSeriais = JPAContainerFactory.make(SerialProduto.class, ConnUtil.getEntity());
		containerSeriais.addContainerFilter(Filters.eq("produto", new Produto(codProduto)));
		containerSeriais.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		return containerSeriais;
	}
	
	@Override
	public Button buildBtSalvar() {
		
	btSalvar = new Button("Remover", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					fireEvent(new RemoverMaterialEvent(getUI(), item, true));					
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
					fireEvent(new RemoverMaterialEvent(getUI(), item, false));			
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
	
	
	
	public void addListerner(RemoverMaterialListerner target){
		try {
			Method method = RemoverMaterialListerner.class.getDeclaredMethod("onClose", RemoverMaterialEvent.class);
			addListener(RemoverMaterialEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(RemoverMaterialListerner target){
		removeListener(RemoverMaterialEvent.class, target);
	}
	public static class RemoverMaterialEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		
		public RemoverMaterialEvent(Component source, Item item, boolean confirm) {
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
	public interface RemoverMaterialListerner extends Serializable{
		public void onClose(RemoverMaterialEvent event);
	}

}
