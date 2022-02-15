package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
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
import com.vaadin.data.util.BeanItemContainer;
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

public class AlterarEnderecoEditor extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String motivo;
	ComboBox cbProduto;
	
	private ComboBox cbEnderecos;
	
	Produto material_selecionado;
	
	
	private JPAContainer<SerialProduto> containerSeriais;
		
	boolean valid_mac = false;
	
	private TextField txtSerial;
	private ContratosAcesso contratoAcesso;
	
	Endereco endereco;

	public AlterarEnderecoEditor(Item item, String title, boolean modal){
		this.item = item;
		endereco = (Endereco) item.getItemProperty("endereco").getValue();
		contratoAcesso = (ContratosAcesso) item.getItemProperty("contrato").getValue();
		
		setWidth("751px");
		setHeight("247px");
		
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
	
	
	private BeanItemContainer<Endereco> buildContainerEnderecos(Integer codCliente){
		
		BeanItemContainer<Endereco> enderecos = new BeanItemContainer<>(Endereco.class);
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select e from Endereco e where  e.clientes = :cliente and e.status = 'ATIVO'", Endereco.class);
		q.setParameter("cliente", new Cliente(codCliente));
		
		
		if(q.getResultList().size() >0){
			
			for (Endereco end : (List<Endereco>) q.getResultList()) {
				
				Query q2 = em.createQuery("select a from AcessoCliente a where a.endereco=:end and a.status_2 != 'ENCERRADO'", AcessoCliente.class);
				q2.setParameter("end", end);
				AcessoCliente ac = null;
				
				if(q2.getResultList().size() == 0){
					enderecos.addBean(end);
				}
			}
		}
		
		
		return enderecos;
	}

	private void buildLayout() {
		
		fieldGroup = new FieldGroup(item);
		
		final Cliente cliente = (Cliente) item.getItemProperty("cliente").getValue();
		
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
					
					TextField tfCliente = new TextField("Cliente");
					tfCliente.setValue(cliente.getNome_razao());
					tfCliente.setReadOnly(true);
					tfCliente.setWidth("541px");
					tfCliente.setStyleName("caption-align");
					
					addComponent(tfCliente);
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);

					TextField tfEnderecoAtual = new TextField("Endereço Atual");
					tfEnderecoAtual.setValue(endereco.getCep()+", "+endereco.getEndereco()+", "+endereco.getNumero()+", "+endereco.getBairro()+", "+endereco.getCidade()+" - "+endereco.getUf());
					tfEnderecoAtual.setReadOnly(true);
					tfEnderecoAtual.setWidth("541px");
					tfEnderecoAtual.setStyleName("caption-align");
					
					addComponent(tfEnderecoAtual);
				}
		});
		
		vlRoot.addComponent(new FormLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom-new");

				cbEnderecos = new ComboBox("Endereço"){
					@Override
					public String getItemCaption(Object itemId) {
					   Item item = getItem(itemId);
					
					   if (item == null) {
					      return "";
					   }
					     
					   return String.valueOf(item.getItemProperty("cep").getValue()) + ", " + 
				   		  String.valueOf(item.getItemProperty("endereco").getValue()) + ", " +
				   		  String.valueOf(item.getItemProperty("numero").getValue()) + ", " +
				   		  String.valueOf(item.getItemProperty("bairro").getValue()) + ", " +
				   		  String.valueOf(item.getItemProperty("cidade").getValue())+ " - " +
				   		  String.valueOf(item.getItemProperty("uf").getValue());
				}
					
				};
				cbEnderecos.setStyleName("caption-align");				
				cbEnderecos.setWidth("542px");
				cbEnderecos.setContainerDataSource(buildContainerEnderecos(cliente.getId()));;
				cbEnderecos.setRequired(true);
				cbEnderecos.setTextInputAllowed(false);
				cbEnderecos.setNullSelectionAllowed(false);
				cbEnderecos.setReadOnly(true);
				cbEnderecos.focus();
												
				addComponent(cbEnderecos);				

				item.getItemProperty("endereco").setValue(null);
				fieldGroup.bind(cbEnderecos, "endereco");

			}
		});
		
		
	}

	private JPAContainer<SerialProduto> buildContainerSeriais(Integer codProduto){
		containerSeriais = JPAContainerFactory.make(SerialProduto.class, ConnUtil.getEntity());
		containerSeriais.addContainerFilter(Filters.eq("produto", new Produto(codProduto)));
		containerSeriais.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		return containerSeriais;
	}
	
	public Button buildBtSalvar() {
		
	btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid()){
					try {	
												
						fieldGroup.commit();				
						fireEvent(new AlterarEnderecoEvent(getUI(), item, true));							
						
					} catch (Exception e) {											
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
						e.printStackTrace();
					}
				}else{
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
		//btSalvar.setEnabled(false);
		return btSalvar;
	}


	public Button buildBtCancelar() {
			btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					item.getItemProperty("endereco").setValue(endereco);					
					fieldGroup.discard();				
					fireEvent(new AlterarEnderecoEvent(getUI(), item,false));					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(fieldGroup.isValid()){
									try {										
										fieldGroup.commit();				
										fireEvent(new AlterarEnderecoEvent(getUI(), item,true));										
									} catch (Exception e) {											
										Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);	
										e.printStackTrace();
									}
								}else{
									Notify.Show_Invalid_Submit_Form();
								}
							}else{								
								item.getItemProperty("endereco").setValue(endereco);
								fieldGroup.discard();				
								fireEvent(new AlterarEnderecoEvent(getUI(), item,false));											
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
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
	
	
	
	public void addListerner(AlterarEnderecoListerner target){
		try {
			Method method = AlterarEnderecoListerner.class.getDeclaredMethod("onClose", AlterarEnderecoEvent.class);
			addListener(AlterarEnderecoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AlterarEnderecoListerner target){
		removeListener(AlterarEnderecoEvent.class, target);
	}
	public static class AlterarEnderecoEvent extends Event{
		
		private Item item;
		private boolean confirm;
			
		public AlterarEnderecoEvent(Component source, Item item,boolean confirm) {
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
	public interface AlterarEnderecoListerner extends Serializable{
		public void onClose(AlterarEnderecoEvent event);
	}

}
