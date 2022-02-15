package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CfopDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cfop;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.CfopUtil;
import com.digital.opuserp.view.util.CfopUtil.CfopUtilEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class AlterarTipoNfeEditor  extends Window {

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
		
	private Cfop cfop_selecionado;		
	boolean valid_mac = false;	
	private TextField txtSerial;
	private AcessoCliente contratoAcesso;
	
	private TextField tfDescricaoCfop;

	public AlterarTipoNfeEditor(Item item, String title, boolean modal){
		this.item = item;
		if(item.getItemProperty("cfop_nfe").getValue() != null){
			cfop_selecionado = CfopDAO.find((Integer)item.getItemProperty("cfop_nfe").getValue());
		}
		EntityItem<AcessoCliente> enty =(EntityItem<AcessoCliente>)item;
		contratoAcesso = enty.getEntity();
		
		
		setWidth("808px");
		setHeight("291px");
		
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
		
		final Cliente cliente = (Cliente) item.getItemProperty("cliente").getValue();
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField tfCodContrato = new TextField("Contrato");
					tfCodContrato.setValue(contratoAcesso.getId().toString());
					tfCodContrato.setReadOnly(true);
					tfCodContrato.setWidth("62px");
					tfCodContrato.setStyleName("caption-align-tipo-nfe");
					
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
					tfCliente.setStyleName("caption-align-tipo-nfe");
					
					addComponent(tfCliente);
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);

					ComboBox cbTipoNf = new  ComboBox("Tipo de NF");
					cbTipoNf.setRequired(true);
					cbTipoNf.setNullSelectionAllowed(false);
					cbTipoNf.addItem("NFE-MOD21");
					cbTipoNf.addItem("NFE-SERVICO");
					cbTipoNf.setStyleName("caption-align-tipo-nfe");
					fieldGroup.bind(cbTipoNf, "emitir_nfe");
					
					addComponent(cbTipoNf);
				}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{											
				
				if(item.getItemProperty("cfop_nfe").getValue() != null){
					cfop_selecionado = CfopDAO.find((Integer)item.getItemProperty("cfop_nfe").getValue());
				}
				
				final TextField tfCodCfop = new TextField("Nat. Operação");				
				tfCodCfop.setWidth("60px");				
				tfCodCfop.setNullRepresentation("");
				tfCodCfop.setStyleName("caption-align-tipo-nfe");
				tfCodCfop.setImmediate(true);
				tfCodCfop.setId("txtCodMaterial");
				
				if(cfop_selecionado != null){
					tfCodCfop.setValue(cfop_selecionado.getId().toString());				
				}
				
				JavaScript.getCurrent().execute("$('#txtCodMaterial').mask('00000000')");						
								
				tfCodCfop.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ProdutoDAO cDAO = new ProdutoDAO();
						cfop_selecionado = new Cfop();
						
						if(event.getText()!=null && 
								!event.getText().isEmpty() && 
								!event.getText().equals("") 
							){
							
														
							cfop_selecionado = CfopDAO.find(Integer.parseInt(event.getText()));		
							
							if(cfop_selecionado != null){
								tfDescricaoCfop.setReadOnly(false);
								tfDescricaoCfop.setValue(cfop_selecionado.getDescricao());
								tfDescricaoCfop.setReadOnly(true);
								
							}else {
								tfDescricaoCfop.setReadOnly(false);
								tfDescricaoCfop.setValue("");
								tfDescricaoCfop.setReadOnly(true);		
								
							}
						}
						
					}
				});

				tfCodCfop.setRequired(true);		
				tfDescricaoCfop = new TextField();
				tfDescricaoCfop.setReadOnly(true);
				tfDescricaoCfop.setWidth("450px");	
				if(cfop_selecionado != null){
					tfDescricaoCfop.setReadOnly(false);
					tfDescricaoCfop.setValue(cfop_selecionado.getDescricao());
					tfDescricaoCfop.setReadOnly(true);
				}
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						CfopUtil cUtil = new CfopUtil(true, true, OpusERP4UI.getEmpresa().getId(), true);
						cUtil.addListerner(new CfopUtil.CfopUtilListerner() {
							
							@Override
							public void onSelected(CfopUtilEvent event) {
									
									if(event.getCfop() != null ){
										
										tfCodCfop.setValue(event.getCfop().getId().toString());
										tfDescricaoCfop.setReadOnly(false);
										tfDescricaoCfop.setValue(event.getCfop().getDescricao());
										tfDescricaoCfop.setReadOnly(true);
										cfop_selecionado = event.getCfop();
										
										
									}else{
										
//										if(event.getCfop() != null){											
//											tfCodCfop.setValue("");											
//											Notify.Show("Não é possivel selecionar o  mesmo material!", Notify.TYPE_ERROR);
//										}
										
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
												
						addComponent(tfCodCfop);							
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
						
						addComponent(tfDescricaoCfop);							
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

					ComboBox cbGerarNfCBoletoAberto = new  ComboBox("Gerar NF C/ Boleto Aberto");
					cbGerarNfCBoletoAberto.setRequired(true);
					cbGerarNfCBoletoAberto.setNullSelectionAllowed(false);
					cbGerarNfCBoletoAberto.addItem("SIM");
					cbGerarNfCBoletoAberto.addItem("NAO");
					cbGerarNfCBoletoAberto.setStyleName("caption-align-tipo-nfe");
					fieldGroup.bind(cbGerarNfCBoletoAberto, "emitir_nfe_c_boleto_aberto");
					
					addComponent(cbGerarNfCBoletoAberto);
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);

					ComboBox cbGerarNfAutomatico = new  ComboBox("Gerar NF Automático");
					cbGerarNfAutomatico.setRequired(true);
					cbGerarNfAutomatico.setNullSelectionAllowed(false);
					cbGerarNfAutomatico.addItem("SIM");
					cbGerarNfAutomatico.addItem("NAO");
					cbGerarNfAutomatico.setStyleName("caption-align-tipo-nfe");
					fieldGroup.bind(cbGerarNfAutomatico, "emitir_nfe_automatico");
					
					addComponent(cbGerarNfAutomatico);
				}
		});
		
		
	}

		
	public Button buildBtSalvar() {
		
	btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid()){
					try {	
						
						fieldGroup.commit();
						
						if(cfop_selecionado != null && cfop_selecionado.getId() != null){
							item.getItemProperty("cfop_nfe").setValue(cfop_selecionado.getId());
						}
						fireEvent(new AlterarTipoNfeEvent(getUI(), item, true));							
						
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
					if(cfop_selecionado != null){
						item.getItemProperty("cfop_nfe").setValue(cfop_selecionado.getId());
					}
					fieldGroup.discard();				
					fireEvent(new AlterarTipoNfeEvent(getUI(), item,false));					
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
										fireEvent(new AlterarTipoNfeEvent(getUI(), item,true));										
									} catch (Exception e) {											
										Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);	
										e.printStackTrace();
									}
								}else{
									Notify.Show_Invalid_Submit_Form();
								}
							}else{								
								item.getItemProperty("cfop_nfe").setValue(cfop_selecionado.getId());
								fieldGroup.discard();				
								fireEvent(new AlterarTipoNfeEvent(getUI(), item,false));											
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
	
	
	
	public void addListerner(AlterarTipoNfeListerner target){
		try {
			Method method = AlterarTipoNfeListerner.class.getDeclaredMethod("onClose", AlterarTipoNfeEvent.class);
			addListener(AlterarTipoNfeEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AlterarTipoNfeListerner target){
		removeListener(AlterarTipoNfeEvent.class, target);
	}
	public static class AlterarTipoNfeEvent extends Event{
		
		private Item item;
		private boolean confirm;
			
		public AlterarTipoNfeEvent(Component source, Item item,boolean confirm) {
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
	public interface AlterarTipoNfeListerner extends Serializable{
		public void onClose(AlterarTipoNfeEvent event);
	}

}
