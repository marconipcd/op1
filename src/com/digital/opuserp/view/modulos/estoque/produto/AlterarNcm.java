package com.digital.opuserp.view.modulos.estoque.produto;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.NcmDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Ncm;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.NcmUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.NcmUtil.NcmEvent;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AlterarNcm extends Window implements GenericEditor {

	boolean valid_cod_barras;
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private TextField tfCodProduto; 
	private TextField tfProduto; 
	private TextField tfAlterar;
	private Button btSearchNcm;
	private TextField txtDescricaoNcm;
	Ncm ncmSelecionado;
	
								
	EcfPreVendaCabecalho pedido;
	EcfPreVendaDetalhe detalhe;

	MovimentoEntCabecalho compra;
	MovimentoEntDetalhe contaDetalhe;
	
	private boolean Codigo;
	private Produto produto;
	
	public AlterarNcm(Produto produto, String title, boolean modal, boolean Codigo){
		this.Codigo = Codigo;	
		this.produto = produto;
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
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		buildLayout();
	}
	
	
	
	private void configLayout(){
		//1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("949px");
				setHeight("207px");

		//}	
	}

	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);								
				
				tfCodProduto = new TextField("Código");
				tfCodProduto.setRequired(true);
				tfCodProduto.setStyleName("caption-align");
				tfCodProduto.setWidth("80px");
				tfCodProduto.setValue(produto.getId().toString());
				tfCodProduto.setReadOnly(true);
				addComponent(tfCodProduto);	
			}
		});
		
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);								
								
				tfProduto = new TextField("Produto");				
				tfProduto.setWidth("100%");				
				tfProduto.setStyleName("caption-align");
				tfProduto.setNullRepresentation("");
				tfProduto.setValue(produto.getNome());
				tfProduto.setReadOnly(true);
				addComponent(tfProduto);	

			}
		});
		
		if(Codigo){
			vlRoot.addComponent(
						new FormLayout(){					
						{
							setStyleName("form-cutom-new");
							setMargin(true);
							setSpacing(true);								
							
							tfAlterar = new TextField("Código de Barras");		
							tfAlterar.setValue(produto.getgTin());							
							tfAlterar.setWidth("100px");							
							tfAlterar.setStyleName("caption-align");
							tfAlterar.setNullRepresentation("");
							tfAlterar.focus();
							
							tfAlterar.addTextChangeListener(new TextChangeListener() {
								
								@Override
								public void textChange(TextChangeEvent event) {
									
									boolean existe = ProdutoDAO.checkExistCodBarras(event.getText());
									
									if(existe){
										tfAlterar.addStyleName("invalid-txt");
										valid_cod_barras = false;
									}else{
										tfAlterar.removeStyleName("invalid-txt");
										valid_cod_barras = true;
									}
								}
							});
							
							addComponent(tfAlterar);	
						}
					
			});
		}else{
			vlRoot.addComponent(new HorizontalLayout(){
				{
					//addStyleName("caption-align-produto-impostos-ncm");
					
					tfAlterar = new TextField("NCM");				
					tfAlterar.setWidth("60px");				
					tfAlterar.setNullRepresentation("");
					tfAlterar.setRequired(true);
					tfAlterar.setTabIndex(12);
					tfAlterar.setStyleName("caption-align");
					
					tfAlterar.setId("txtNcm");
					
					JavaScript.getCurrent().execute("$('#txtNcm').mask('0000000000')");
					tfAlterar.setImmediate(true);
					
					tfAlterar.addTextChangeListener(new TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
							ncmSelecionado = new Ncm();
							if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
								
								Integer id = Integer.parseInt(event.getText());
								ncmSelecionado = NcmDAO.getNcm(id);				
								
								if(ncmSelecionado != null){
									txtDescricaoNcm.setReadOnly(false);
									txtDescricaoNcm.setValue(ncmSelecionado.getDescricao());
									txtDescricaoNcm.setReadOnly(true);							
								}else {
									txtDescricaoNcm.setReadOnly(false);
									txtDescricaoNcm.setValue("");
									txtDescricaoNcm.setReadOnly(true);
									ncmSelecionado = null;
								}
							}else{
								txtDescricaoNcm.setReadOnly(false);
								txtDescricaoNcm.setValue("");
								txtDescricaoNcm.setReadOnly(true);	
								
								ncmSelecionado = null;
							}
						}
					});
					
					//txtCodIpi.setRequired(true);		
					txtDescricaoNcm = new TextField();
					txtDescricaoNcm.setTabIndex(2000);
					txtDescricaoNcm.setReadOnly(true);
					txtDescricaoNcm.setWidth("649px");	
					
					if(produto.getNcm() != null){
						try{
							ncmSelecionado = NcmDAO.getNcm(Integer.parseInt(produto.getNcm()));
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					//TODO FAZER DEPOIS QUE ATRELAR NO PRODUTO
					
					btSearchNcm = new Button();			
					btSearchNcm.setStyleName(BaseTheme.BUTTON_LINK);
					btSearchNcm.setIcon(new ThemeResource("icons/search-16.png"));
					btSearchNcm.setTabIndex(300000);
					btSearchNcm.setEnabled(true);
					btSearchNcm.addClickListener(new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							NcmUtil gpUtil = new NcmUtil(true, true);
							gpUtil.addListerner(new NcmUtil.NcmListerner() {
								
								@Override
								public void onSelected(NcmEvent event) {
									if(event.getNcm()!= null){
										
										ncmSelecionado = event.getNcm();
										
										tfAlterar.setValue(event.getNcm().getCodigo().toString());
										txtDescricaoNcm.setReadOnly(false);
										txtDescricaoNcm.setValue(event.getNcm().getDescricao());
										txtDescricaoNcm.setReadOnly(true);
										
									}
								}							
							});
							
							getUI().addWindow(gpUtil);
						}
					});
					
					
					FormLayout frmCodigoNcm = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");		
							
							addComponent(tfAlterar);							
						}
					};
					addComponent(frmCodigoNcm);
					
					FormLayout frmButtonSearchNcm =new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom_hide_error_cell");
							addComponent(btSearchNcm);							
						}
					}; 
					
					FormLayout frmDescNcm = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);						
							setStyleName("form-cutom");		
							addStyleName("form-cutom_hide_require");
							
							addComponent(txtDescricaoNcm);							
						}
					}; 
					addComponent(frmButtonSearchNcm);
					addComponent(frmDescNcm);
					setExpandRatio(frmDescNcm, 1);		
					
					
					if(tfAlterar != null && ncmSelecionado != null){
						tfAlterar.setValue(ncmSelecionado.getCodigo().toString());
						//txtCodFornecedor.setReadOnly(true);
					}
					
					if(txtDescricaoNcm != null && ncmSelecionado != null){
						txtDescricaoNcm.setReadOnly(false);
						txtDescricaoNcm.setValue(ncmSelecionado.getDescricao());
						txtDescricaoNcm.setReadOnly(true);
					}
					
				}	
			});
		}
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(tfAlterar!=null && tfAlterar.getValue()!=null && !tfAlterar.getValue().equals("") && valid_cod_barras){
					try {

						if(Codigo){									
							produto.setgTin(tfAlterar.getValue());
							ProdutoDAO.alterar(produto);		
							
							fireEvent(new AlterarEvent(getUI(),true));								
						}else{
							produto.setNcm(tfAlterar.getValue());
							ProdutoDAO.alterar(produto);		
							
							fireEvent(new AlterarEvent(getUI(),true));				
						}

						Notify.Show("Produto Alterado com Sucesso!", Notify.TYPE_SUCCESS);

						close();
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
					
					if(tfAlterar.getValue()==null||tfAlterar.getValue().equals("")){
						tfAlterar.addStyleName("invalid-txt");
					}else{
						tfAlterar.removeStyleName("invalid-txt");
					}
					
					if(!valid_cod_barras){
						tfAlterar.addStyleName("invalid-txt");						
					}else{
						tfAlterar.removeStyleName("invalid-txt");						
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
					fireEvent(new AlterarEvent(getUI(),false));
					close();
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
									if(Codigo){
										fireEvent(new AlterarEvent(getUI(),true));																			
									}else{
										fireEvent(new AlterarEvent(getUI(),true));																																	
									}
																	
								
									Notify.Show("Produto Alterado com Sucesso!", Notify.TYPE_SUCCESS);
									
									close();
								} catch (Exception e) {
									e.printStackTrace();
									System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
									Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
								}
							  }else{
								  Notify.Show_Invalid_Submit_Form();
							  }
							}else{							
								fieldGroup.discard();				
								fireEvent(new AlterarEvent(getUI(),false));
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
	
	public void addListerner(AlterarListerner target){
		try {
			Method method = AlterarListerner.class.getDeclaredMethod("onClose", AlterarEvent.class);
			addListener(AlterarEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(AlterarEvent.class, target);
	}
	public static class AlterarEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public AlterarEvent(Component source,boolean confirm) {
			super(source);
			this.confirm = confirm;			
		}

		public boolean isConfirm() {
			return confirm;
		}
	
	}
	public interface AlterarListerner extends Serializable{
		public void onClose(AlterarEvent event);
	}
	
}

