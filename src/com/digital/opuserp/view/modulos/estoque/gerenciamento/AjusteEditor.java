package com.digital.opuserp.view.modulos.estoque.gerenciamento;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ProdutoUtil;
import com.digital.opuserp.view.util.ProdutoUtil.ProdutoEvent;
import com.digital.opuserp.view.util.ProdutoUtil.ProdutoListerner;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class AjusteEditor extends Window implements GenericEditor {

		
	private static final long serialVersionUID = 1L;
	
	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private Label lbRegistros;
	
	TextField tfCodProduto;
	TextField tfDescricaoProduto;
	TextField txtQtd;
	TextField txtQtdAtual;

	Produto produtoSelecionado;
	
	HorizontalLayout hlFloat;
	
	boolean validarQtd = false;
	
	boolean permitir_fracionar = false;
	
	public AjusteEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("782px");
		setHeight("270px");
		
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

	ComboBox cbTipo;
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
				
				cbTipo = new ComboBox("Tipo");
				cbTipo.setStyleName("caption-align-acesso");
				cbTipo.setNullSelectionAllowed(false);
				cbTipo.addItem("DEPOSITO->LOJA");
				cbTipo.addItem("LOJA->DEPOSITO");
				cbTipo.setRequired(true); 
				cbTipo.setImmediate(true);
				
				cbTipo.select("DEPOSITO->LOJA");
				
				cbTipo.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						if(cbTipo.getValue().toString().equals("DEPOSITO->LOJA") && produtoSelecionado != null && produtoSelecionado.getQtdEstoqueDeposito() != null){									
							txtQtdAtual.setReadOnly(false);
							txtQtdAtual.setValue(produtoSelecionado.getQtdEstoqueDeposito().toString());
							txtQtdAtual.setReadOnly(true);
							
							txtQtdAtual.setCaption("Qtd. Atual Deposito");
						}
						
						if(cbTipo.getValue().toString().equals("LOJA->DEPOSITO") && produtoSelecionado != null && produtoSelecionado.getQtdEstoque() != null){									
							txtQtdAtual.setReadOnly(false);
							txtQtdAtual.setValue(produtoSelecionado.getQtdEstoque().toString());
							txtQtdAtual.setReadOnly(true);
							
							txtQtdAtual.setCaption("Qtd. Atual Loja");
						}
					}
				});
				
				
				
				addComponent(cbTipo); 
			}
		});
		

		vlRoot.addComponent(new HorizontalLayout(){
			{
				//setWidth("100%");
				
				JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
				containerClientes.addContainerFilter(Filters.eq("status", "INATIVO"));
				
				
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
					preencher = true;
				}
				
				tfCodProduto = new TextField("Produto");				
				tfCodProduto.setWidth("60px");				
				tfCodProduto.setNullRepresentation("");
				tfCodProduto.setStyleName("caption-align-acesso");
				tfCodProduto.focus();
				tfCodProduto.setId("tfCodProduto");
				
				JavaScript.getCurrent().execute("$('#tfCodProduto').mask('0000000000')");
				tfCodProduto.setImmediate(true);
								
				tfCodProduto.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
		
						produtoSelecionado = new Produto();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							
							produtoSelecionado = ProdutoDAO.find(Integer.parseInt(event.getText()));		
							
							if(produtoSelecionado != null){												
									tfDescricaoProduto.setReadOnly(false);
									tfDescricaoProduto.setValue(produtoSelecionado.getNome());
									tfDescricaoProduto.setReadOnly(true);				
									
									if(cbTipo.getValue().toString().equals("DEPOSITO->LOJA")){									
										txtQtdAtual.setReadOnly(false);
										txtQtdAtual.setValue(produtoSelecionado.getQtdEstoqueDeposito().toString());
										txtQtdAtual.setReadOnly(true);
										
										txtQtdAtual.setCaption("Qtd. Atual Deposito");
									}
									
									if(cbTipo.getValue().toString().equals("LOJA->DEPOSITO")){									
										txtQtdAtual.setReadOnly(false);
										txtQtdAtual.setValue(produtoSelecionado.getQtdEstoque().toString());
										txtQtdAtual.setReadOnly(true);
										
										txtQtdAtual.setCaption("Qtd. Atual Loja");
									}
									
									
									if(produtoSelecionado.getFracionar() == 1){
										permitir_fracionar = true;									
										JavaScript.getCurrent().execute("$('#txtQtd').maskMoney({decimal:',',thousands:''})");
									}else{
										permitir_fracionar = false;
										JavaScript.getCurrent().execute("$('#txtQtd').mask('0000000000')");
									}
							}else {
								tfDescricaoProduto.setReadOnly(false);
								tfDescricaoProduto.setValue("");
								tfDescricaoProduto.setReadOnly(true);
								
								txtQtdAtual.setReadOnly(false);
								txtQtdAtual.setValue("");
								txtQtdAtual.setReadOnly(true);
							}
						}else{
							tfDescricaoProduto.setReadOnly(false);
							tfDescricaoProduto.setValue("");
							tfDescricaoProduto.setReadOnly(true);	
							
							txtQtdAtual.setReadOnly(false);
							txtQtdAtual.setValue("");
							txtQtdAtual.setReadOnly(true);
							
							permitir_fracionar = false;
							
						}
					}
				});

				tfCodProduto.setRequired(true);		
				tfDescricaoProduto = new TextField();
				tfDescricaoProduto.setTabIndex(2000);
				tfDescricaoProduto.setReadOnly(true);
				tfDescricaoProduto.setWidth("450px");
												
				if(item.getItemProperty("produto") != null && item.getItemProperty("produto").getValue() != null){
					
						tfCodProduto.setValue(((Produto)item.getItemProperty("produto").getValue()).getId().toString());
						tfCodProduto.setReadOnly(false);
						tfDescricaoProduto.setValue(((Produto)item.getItemProperty("produto").getValue()).getNome());
						tfDescricaoProduto.setReadOnly(true);
						
						txtQtdAtual.setReadOnly(false);
						txtQtdAtual.setValue(((Produto)item.getItemProperty("produto").getValue()).getQtdEstoqueDeposito().toString());
						txtQtdAtual.setReadOnly(true);
					
				}
							
				final Button btSearchProduto = new Button();
				btSearchProduto.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchProduto.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchProduto.setTabIndex(300000);
				btSearchProduto.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						ProdutoUtil produtoUtil = new ProdutoUtil(true, true);
						produtoUtil.addListerner(new ProdutoListerner() {
							
							@Override
							public void onSelected(ProdutoEvent event) {
								if(event.getProduto() != null){
									
									
									tfCodProduto.setValue(event.getProduto().getId().toString());
									tfDescricaoProduto.setReadOnly(false);
									tfDescricaoProduto.setValue(event.getProduto().getNome());
									tfDescricaoProduto.setReadOnly(true);
									
									
									
									produtoSelecionado = event.getProduto();
									
									
									if(produtoSelecionado.getFracionar() == 1){
										permitir_fracionar = true;									
										JavaScript.getCurrent().execute("$('#txtQtd').maskMoney({decimal:',',thousands:''})");
									}else{
										permitir_fracionar = false;
										JavaScript.getCurrent().execute("$('#txtQtd').mask('0000000000')");
									}
									
									if(cbTipo.getValue().toString().equals("DEPOSITO->LOJA")){									
										txtQtdAtual.setReadOnly(false);
										txtQtdAtual.setValue(produtoSelecionado.getQtdEstoqueDeposito().toString());
										txtQtdAtual.setReadOnly(true);
										
										txtQtdAtual.setCaption("Qtd. Atual Deposito");
									}
									
									if(cbTipo.getValue().toString().equals("LOJA->DEPOSITO")){									
										txtQtdAtual.setReadOnly(false);
										txtQtdAtual.setValue(produtoSelecionado.getQtdEstoque().toString());
										txtQtdAtual.setReadOnly(true);
										
										txtQtdAtual.setCaption("Qtd. Atual Loja");
									}
								}else{
									tfCodProduto.setValue("");
									tfDescricaoProduto.setReadOnly(false);
									tfDescricaoProduto.setValue("");
									tfDescricaoProduto.setReadOnly(true);
									
									txtQtdAtual.setReadOnly(false);
									txtQtdAtual.setValue("");
									txtQtdAtual.setReadOnly(true);
									
									produtoSelecionado = null;
									
									permitir_fracionar = false;
								}
							}
						});
						
						getUI().addWindow(produtoUtil);
					}
				});
				
				FormLayout frmCodigoProduto = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(tfCodProduto);							
					}
				};
				addComponent(frmCodigoProduto);
		
				FormLayout frmButtonSearchProduto =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");										
						addComponent(btSearchProduto);							
					}
				}; 
							
				FormLayout frmDescProduto = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoProduto);							
					}
				}; 
				addComponent(frmButtonSearchProduto);
				addComponent(frmDescProduto);
				setExpandRatio(frmDescProduto, 1);	
			}	
		});
		
		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
				
				txtQtdAtual = new TextField("Qtd. Atual");
				txtQtdAtual.setStyleName("caption-align-acesso");
				txtQtdAtual.setNullRepresentation("");
				txtQtdAtual.setWidth("100px");
				txtQtdAtual.setRequired(true); 
				txtQtdAtual.setReadOnly(true); 
				
				addComponent(txtQtdAtual); 
			}
		});
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					txtQtd = new TextField("Qtd.:");
					txtQtd.setStyleName("caption-align-acesso");
					txtQtd.setNullRepresentation("");
					txtQtd.setWidth("100px");
					txtQtd.setRequired(true); 
					//txtQtd.setImmediate(true);
					txtQtd.setId("txtQtd");
					
					JavaScript.getCurrent().execute("$('#txtQtd').maskMoney({decimal:',',thousands:'.'})");
					
													
					fieldGroup.bind(txtQtd,"qtd");
					
					addComponent(txtQtd); 
				}
			});
					
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				concluirCadastro();		
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

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new AjusteTransferenciaEvent(getUI(), item, false));					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								concluirCadastro();
							}else{							
								fieldGroup.discard();				
								fireEvent(new AjusteTransferenciaEvent(getUI(), item, false));
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
	
	private void concluirCadastro(){
		
		if(txtQtd.getValue() != null && !txtQtd.getValue().equals("")){
			
			if(cbTipo.getValue().toString().equals("DEPOSITO->LOJA")){
				Float qtdAtual = new Float(txtQtdAtual.getValue());
				double qtd = Real.formatStringToDBDouble(txtQtd.getValue());
				
				if(qtd > qtdAtual){				
					txtQtd.addStyleName("invalid-txt");
					validarQtd =false;
				}else{
					validarQtd =true;
					txtQtd.removeStyleName("invalid-txt");
				}
			}
			
			if(cbTipo.getValue().toString().equals("LOJA->DEPOSITO")){
				Float qtdAtual = new Float(txtQtdAtual.getValue());
				double qtd = Real.formatStringToDBDouble(txtQtd.getValue());
				
				if(qtd > qtdAtual){				
					txtQtd.addStyleName("invalid-txt");
					validarQtd =false;
				}else{
					validarQtd =true;
					txtQtd.removeStyleName("invalid-txt");
				}
			}
		}
		
		if(produtoSelecionado != null && txtQtd.getValue() != null && validarQtd){
		
			try {		
				
				fieldGroup.commit();		
				
				item.getItemProperty("tipo_transferencia").setValue("AJUSTE");
				item.getItemProperty("tipo").setValue(cbTipo.getValue().toString());
				item.getItemProperty("produto").setValue(produtoSelecionado);
				item.getItemProperty("data_inicio_transferencia").setValue(new Date());
				item.getItemProperty("usuario_transferencia").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
				item.getItemProperty("status").setValue("PENDENTE");
				
				fireEvent(new AjusteTransferenciaEvent(getUI(), item, true));

			} catch (Exception e) {											
				Notify.Show("ERRO: Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);		
				e.printStackTrace();
			}
		}else{
			
			    for (Field<?> field: fieldGroup.getFields()) {						
					if(!field.isValid()){
						field.addStyleName("invalid-txt");
					}else{
						field.removeStyleName("invalid-txt");
					}
			    }
			    
			    if(produtoSelecionado == null){
			    	tfCodProduto.addStyleName("invalid-txt");
			    }else{
			    	tfCodProduto.removeStyleName("invalid-txt");  	
			    }
			    
			    if(!validarQtd){
			    	txtQtd.addStyleName("invalid-txt");
			    	Notify.Show("Não é possível transferir mais do que a Quantidade Disponível!", Notify.TYPE_ERROR);
			    }
			    
			    Notify.Show_Invalid_Submit_Form();
		}		
	}
	
	
	public void addListerner(AjusteTransferenciaListener target){
		try {
			Method method = AjusteTransferenciaListener.class.getDeclaredMethod("onClose", AjusteTransferenciaEvent.class);
			addListener(AjusteTransferenciaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(AjusteTransferenciaListener target){
		removeListener(AjusteTransferenciaEvent.class, target);
	}
	public static class AjusteTransferenciaEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public AjusteTransferenciaEvent(Component source, Item item, boolean confirm) {
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
	public interface AjusteTransferenciaListener extends Serializable{
		public void onClose(AjusteTransferenciaEvent event);
	}
	
	
	
	
	
	


}
