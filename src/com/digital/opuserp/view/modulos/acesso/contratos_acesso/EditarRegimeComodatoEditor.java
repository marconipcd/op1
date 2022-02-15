package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.Validator;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.EditarRegimeProprioEditor.EditarRegimeEvent;
import com.digital.opuserp.view.modulos.acesso.contratos_acesso.EditarRegimeProprioEditor.EditarRegimeListerner;
import com.digital.opuserp.view.util.MaterialUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.MaterialUtil.MaterialEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
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
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.themes.BaseTheme;

public class EditarRegimeComodatoEditor extends Window {

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
	
	//private TextField txtSerial;
	private ContratosAcesso contratoAcesso;

	public EditarRegimeComodatoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		contratoAcesso = (ContratosAcesso) item.getItemProperty("contrato").getValue();
		
		setWidth("751px");
		setHeight("368px");
		
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
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					
										
					TextField txtRegime = new TextField("Regime Atual");
										
					txtRegime.setValue(item.getItemProperty("regime").getValue().toString());
					txtRegime.setStyleName("caption-align");					
					txtRegime.setRequired(true);
					txtRegime.setWidth("241px");
					txtRegime.setReadOnly(true);
					
					addComponent(txtRegime);
					
				}
		});	
		
		
		
		final Produto materialAntigo = item.getItemProperty("material").getValue() != null ? (Produto) item.getItemProperty("material").getValue() : null;
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					
					TextField tfMaterialAtual = new TextField("Material Atual");
					tfMaterialAtual.setValue(materialAntigo != null ? materialAntigo.getNome() : "NENHUM");
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
					
					String enderecoMac = item.getItemProperty("endereco_mac").getValue()  != null ? item.getItemProperty("endereco_mac").getValue().toString() : "";
					
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
					
					ComboBox cbRegime = new ComboBox("Novo Regime");
					cbRegime.setNullSelectionAllowed(false);
					cbRegime.addItem("COMODATO");
					cbRegime.select("COMODATO");
					
					cbRegime.setStyleName("caption-align");					
					cbRegime.setRequired(true);
					cbRegime.setWidth("241px");
					cbRegime.focus();
					
					addComponent(cbRegime);
					fieldGroup.bind(cbRegime, "regime");
				}
		});	
		
			
		vlRoot.addComponent(new HorizontalLayout(){
			{
											
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
					preencher = true;
				}
				
				final TextField tfCodMaterial = new TextField("Material");				
				tfCodMaterial.setWidth("60px");				
				tfCodMaterial.setNullRepresentation("");
				tfCodMaterial.setStyleName("caption-align");
				tfCodMaterial.setImmediate(true);
				tfCodMaterial.setId("txtCodMaterial");
				
				JavaScript.getCurrent().execute("$('#txtCodMaterial').mask('00000000')");						
								
				tfCodMaterial.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ProdutoDAO cDAO = new ProdutoDAO();
						material_selecionado = new Produto();
																	
						if(event.getText()!=null && 
								!event.getText().isEmpty() && 
								!event.getText().equals("") 
							){
							
														
							material_selecionado = cDAO.find(Integer.parseInt(event.getText()));		
							
							if(material_selecionado != null){
								tfDescricaoMaterial.setReadOnly(false);
								tfDescricaoMaterial.setValue(material_selecionado.getNome());
								tfDescricaoMaterial.setReadOnly(true);
								
								if(cbSerial != null){
									cbSerial.setEnabled(true);
									cbSerial.setContainerDataSource(buildContainerSeriais(material_selecionado.getId()));		
									cbSerial.setItemCaptionPropertyId("serial");cbSerial.setItemCaptionPropertyId("serial");
								}
							}else {
								tfDescricaoMaterial.setReadOnly(false);
								tfDescricaoMaterial.setValue("");
								tfDescricaoMaterial.setReadOnly(true);		
								
								if(cbSerial != null){
									cbSerial.setEnabled(false);
									cbSerial.setContainerDataSource(null);
								}
								
							
							}
						}
						
					}
				});

				tfCodMaterial.setRequired(true);		
				tfDescricaoMaterial = new TextField();
				tfDescricaoMaterial.setReadOnly(true);
				tfDescricaoMaterial.setWidth("450px");			
									
				
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						MaterialUtil cUtil = new MaterialUtil(true, true);
						cUtil.addListerner(new MaterialUtil.MaterialListerner() {
							
							@Override
							public void onSelected(MaterialEvent event) {
									
									if(event.getMaterial() != null ){
										
										tfCodMaterial.setValue(event.getMaterial().getId().toString());
										tfDescricaoMaterial.setReadOnly(false);
										tfDescricaoMaterial.setValue(event.getMaterial().getNome());
										tfDescricaoMaterial.setReadOnly(true);
										material_selecionado = event.getMaterial();		
										
										if(cbSerial != null){
											cbSerial.setEnabled(true);
											cbSerial.setContainerDataSource(buildContainerSeriais(event.getMaterial().getId()));
											cbSerial.setItemCaptionPropertyId("serial");
										}
									}else{
										
										if(event.getMaterial() != null && materialAntigo != null &&  materialAntigo.getId().equals(event.getMaterial().getId())){											
											tfCodMaterial.setValue("");											
											Notify.Show("Não é possivel selecionar o  mesmo material!", Notify.TYPE_ERROR);
										}
										
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});			
				
				FormLayout frmCodigoCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
												
						addComponent(tfCodMaterial);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");							
						addComponent(btSearchCliente);							
					}
				}; 
							
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoMaterial);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);	

			}	
		});
		
					
			
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					
					
					cbSerial = new ComboBox("Endereço MAC");
					cbSerial.setEnabled(false);
					cbSerial.setNullSelectionAllowed(false);
					cbSerial.setStyleName("caption-align");
					cbSerial.setRequired(true);
					
					cbSerial.setImmediate(true);
					
					cbSerial.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(cbSerial.isValid()){
								valid_mac = true;
							}else{
								valid_mac = false;
							}
						}
					});
					
					addComponent(cbSerial);
						
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
				if(fieldGroup.isValid() && valid_mac){
					try {	
						item.getItemProperty("material").setValue(material_selecionado);					
						item.getItemProperty("endereco_mac").setValue(cbSerial.getItem(cbSerial.getValue()).getItemProperty("serial").getValue().toString());				
						
						fieldGroup.commit();				
						fireEvent(new EditarRegimeEvent(getUI(), item, motivo,  true));							
						
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
					fieldGroup.discard();				
					fireEvent(new EditarRegimeEvent(getUI(), item,motivo,false));					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(fieldGroup.isValid() && valid_mac){
									try {	
										
										fieldGroup.commit();				
										fireEvent(new EditarRegimeEvent(getUI(), item,motivo, true));
											
									} catch (Exception e) {										
										e.printStackTrace();
										Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
									}
								}else{
									Notify.Show_Invalid_Submit_Form();
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new EditarRegimeEvent(getUI(), item,motivo, false));
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
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
	
	
	
	public void addListerner(EditarRegimeListerner target){
		try {
			Method method = EditarRegimeListerner.class.getDeclaredMethod("onClose", EditarRegimeEvent.class);
			addListener(EditarRegimeEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(EditarRegimeListerner target){
		removeListener(EditarRegimeEvent.class, target);
	}
	public static class EditarRegimeEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private String motivo;
		private Integer codSerial;
		
		public EditarRegimeEvent(Component source, Item item,String motivo,  boolean confirm) {
			super(source);
			this.item = item;
			this.motivo = motivo;
			this.confirm = confirm;
			this.codSerial = codSerial;
			
		}
		
//		public Integer getCodSerial(){
//			return codSerial;
//		}

		public Item getItem() {
			return item;
		}	
		
		public String getMotivo() {
			return motivo;
		}	

		public boolean isConfirm() {
			return confirm;
		}	
		
		
	}
	public interface EditarRegimeListerner extends Serializable{
		public void onClose(EditarRegimeEvent event);
	}

}
