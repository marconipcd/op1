package com.digital.opuserp.view.modulos.estoque.serial;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SeriaisCompraDAO;
import com.digital.opuserp.dao.SerialDAO;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SeriaisCompra;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.ComprasUtil;
import com.digital.opuserp.view.util.ComprasUtil.CompraEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class SerialEditorDevolucao extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private TextField txtSerial;	
	private TextField tfCodProduto;
	private TextField tfDescricaoProduto;
	private TextField tfCodCompra;
	private TextField tfDescricaoCompra;
	
	Produto produto_selecionado;
	MovimentoEntCabecalho compra_selecionado;
	String serialAtual;
	Button btSearchCompra;
	
	Integer codserial;
	Integer codproduto;
	
	SerialProduto serial;
	
	public SerialEditorDevolucao(Item item, String title, boolean modal){
		this.item = item;
		serial = SerialDAO.find(Integer.parseInt(item.getItemProperty("id").getValue().toString()));
		
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
		setWidth("820px");				
		setHeight("224px");	
	}
	
	
	private JPAContainer<Produto> buildContainerProduto(){
		JPAContainer<Produto> container = JPAContainerFactory.make(Produto.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
		
		return container;
	}
	
	public void buildLayout(){
				
		serialAtual = (String)item.getItemProperty("serial").getValue();
		codserial = (Integer)item.getItemProperty("id").getValue();
		
		fieldGroup = new FieldGroup(item);

		vlRoot.addComponent(new HorizontalLayout(){
			{
				tfCodCompra = new TextField("Pedido de Compra");				
				tfCodCompra.setWidth("60px");				
				tfCodCompra.setNullRepresentation("");
				tfCodCompra.setStyleName("caption-align");
								
				tfCodCompra.addListener(new TextChangeListener(){
					
					@Override
					public void textChange(TextChangeEvent event) {

						compra_selecionado = new MovimentoEntCabecalho();
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
														
							compra_selecionado = CompraDAO.find(Integer.parseInt(event.getText()));		
							
							if(compra_selecionado != null){
								
								
								
								MovimentoEntDetalhe mvoDet = CompraDAO.findByCabProd(compra_selecionado.getId(), serial.getProduto().getId());
								if(serial != null && mvoDet != null){					
									
									tfDescricaoCompra.setReadOnly(false);
									tfDescricaoCompra.setValue(compra_selecionado.getFornecedor().getRazao_social());
									tfDescricaoCompra.setReadOnly(true);		
									
									
									tfCodProduto.setReadOnly(false);
									tfCodProduto.setValue(serial.getProduto().getId().toString());
									tfCodProduto.setReadOnly(true);
									tfDescricaoProduto.setReadOnly(false);
									tfDescricaoProduto.setValue(serial.getProduto().getNome());
									tfDescricaoProduto.setReadOnly(true);
								}else{
									
									tfDescricaoCompra.setReadOnly(false);
									tfDescricaoCompra.setValue("");
									tfDescricaoCompra.setReadOnly(true);		
									
									
									tfCodProduto.setReadOnly(false);
									tfCodProduto.setValue("");
									tfCodProduto.setReadOnly(true);
									tfDescricaoProduto.setReadOnly(false);
									tfDescricaoProduto.setValue(serial.getProduto().getNome());
									tfDescricaoProduto.setReadOnly(true);
									
									Notify.Show("A Compra selecionada não possui nenhum item com produto "+serial.getProduto().getNome(), Notify.TYPE_ERROR);
								}
							}else{
								tfDescricaoCompra.setReadOnly(false);
								tfDescricaoCompra.setValue("");
								tfDescricaoCompra.setReadOnly(true);	
								
								tfCodProduto.setReadOnly(false);
								tfCodProduto.setValue("");
								tfCodProduto.setReadOnly(true);
								tfDescricaoProduto.setReadOnly(false);
								tfDescricaoProduto.setValue("");
								tfDescricaoProduto.setReadOnly(true);
							}
						}else{
							tfDescricaoCompra.setReadOnly(false);
							tfDescricaoCompra.setValue("");
							tfDescricaoCompra.setReadOnly(true);
							compra_selecionado = null;
							tfCodCompra.setValue("");
							
							tfCodProduto.setReadOnly(false);
							tfCodProduto.setValue("");
							tfCodProduto.setReadOnly(true);
							tfDescricaoProduto.setReadOnly(false);
							tfDescricaoProduto.setValue("");
							tfDescricaoProduto.setReadOnly(true);
						}
					}
				});

					
				tfDescricaoCompra = new TextField();
				tfDescricaoCompra.setReadOnly(true);
				tfDescricaoCompra.setWidth("490px");			
												
				btSearchCompra = new Button();
				btSearchCompra.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCompra.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCompra.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						ComprasUtil cUtil = new ComprasUtil(true, true);
						cUtil.addListerner(new ComprasUtil.CompraListerner() {
							
							@Override
							public void onSelected(CompraEvent event) {
									
									if(event.getCompra() != null){
										compra_selecionado = event.getCompra();									

										MovimentoEntDetalhe mvoDet = CompraDAO.findByCabProd(event.getCompra().getId(), serial.getProduto().getId());
										if(serial != null && mvoDet != null){
											
											tfCodCompra.setValue(event.getCompra().getId().toString());
											tfDescricaoCompra.setReadOnly(false);
											tfDescricaoCompra.setValue(event.getCompra().getFornecedor().getRazao_social());
											tfDescricaoCompra.setReadOnly(true);	
											
											tfCodProduto.setReadOnly(false);
											tfCodProduto.setValue(serial.getProduto().getId().toString());
											tfCodProduto.setReadOnly(true);
											tfDescricaoProduto.setReadOnly(false);
											tfDescricaoProduto.setValue(serial.getProduto().getNome());
											tfDescricaoProduto.setReadOnly(true);
										}else{
											tfCodCompra.setValue("");
											tfDescricaoCompra.setReadOnly(false);
											tfDescricaoCompra.setValue("");
											tfDescricaoCompra.setReadOnly(true);	
											
											tfCodProduto.setReadOnly(false);
											tfCodProduto.setValue("");
											tfCodProduto.setReadOnly(true);
											tfDescricaoProduto.setReadOnly(false);
											tfDescricaoProduto.setValue("");
											tfDescricaoProduto.setReadOnly(true);
											
											Notify.Show("A Compra selecionada não possui nenhum item com produto "+serial.getProduto().getNome(), Notify.TYPE_ERROR);
										}
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});			
				
				FormLayout frmCodigoCompra = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");	
						addComponent(tfCodCompra);							
					}
				};
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");							
						addComponent(btSearchCompra);							
					}
				}; 
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);									
						setStyleName("form-cutom-new_hide_require");						
						addComponent(tfDescricaoCompra);							
					}
				}; 
								
				addComponent(frmCodigoCompra);
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);	
			}		
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				tfCodProduto = new TextField("Produto");				
				tfCodProduto.setWidth("60px");				
				tfCodProduto.setNullRepresentation("");
				tfCodProduto.setStyleName("caption-align");
				tfCodProduto.setReadOnly(true);
						
				tfDescricaoProduto = new TextField();
				tfDescricaoProduto.setReadOnly(true);
				tfDescricaoProduto.setWidth("490px");			
				
				FormLayout frmCodigoCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		

						if(codproduto!=null){
										
							    produto_selecionado = ProdutoDAO.find(codproduto);
								if(produto_selecionado!=null){
									tfCodProduto.setReadOnly(false);
									tfCodProduto.setValue(produto_selecionado.getId().toString());
									tfCodProduto.setReadOnly(true);			
								}
						}
						addComponent(tfCodProduto);							
					}
				};
				FormLayout frmDescProduto = new FormLayout(){
					{						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom-new_hide_require");
						
						if(codproduto!=null){							
									produto_selecionado = ProdutoDAO.find(codproduto);					
									if(produto_selecionado != null){
										tfDescricaoProduto.setReadOnly(false);
										tfDescricaoProduto.setValue(produto_selecionado.getNome());
										tfDescricaoProduto.setReadOnly(true);									
									}							
						}
						
						addComponent(tfDescricaoProduto);							
					}
				}; 
				
				addComponent(frmCodigoCliente);
				addComponent(frmDescProduto);
				setExpandRatio(frmDescProduto, 1);	
			}		
		});

	
		if(item.getItemProperty("id").getValue() != null){
			vlRoot.addComponent(	new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);								
									
					txtSerial = new TextField("Serial Atual");
					txtSerial.setWidth("120px");
					txtSerial.setStyleName("caption-align");
					txtSerial.setNullRepresentation("");

					if(item.getItemProperty("id").getValue()!= null && item.getItemProperty("tipo_serial").getValue() != null){
						
						if(item.getItemProperty("tipo_serial").getValue().toString().equals("Endereço MAC")){
							txtSerial.setId("txtSerial2");
							txtSerial.setReadOnly(false);
							txtSerial.setValue(item.getItemProperty("serial").getValue().toString());
							txtSerial.setReadOnly(true);
							JavaScript.getCurrent().execute("$('#txtSerial2').mask('AA:AA:AA:AA:AA:AA')");
						}else{
							txtSerial.setId("txtSerial2");
							txtSerial.setReadOnly(false);
							txtSerial.setValue(item.getItemProperty("serial").getValue().toString());
							txtSerial.setReadOnly(true);
							JavaScript.getCurrent().execute("$('#txtSerial2').mask('AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA')");
						}
					}else if(item.getItemProperty("id").getValue()!= null && item.getItemProperty("tipo_serial").getValue() == null){
						txtSerial.setReadOnly(true);											
					}
					addComponent(txtSerial);	
				}
			});
	
		
		}
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
					@Override
					public void buttonClick(ClickEvent event) {

				
					
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
					fireEvent(new SerialDevolucaoEvent(getUI(), item, false));			
					close();
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
	
	public void addListerner(SerialDevolucaoListerner target){
		try {
			Method method = SerialDevolucaoListerner.class.getDeclaredMethod("onClose", SerialDevolucaoEvent.class);
			addListener(SerialDevolucaoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(SerialDevolucaoEvent.class, target);
	}
	public static class SerialDevolucaoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public SerialDevolucaoEvent(Component source, Item item, boolean confirm) {
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
	public interface SerialDevolucaoListerner extends Serializable{
		public void onClose(SerialDevolucaoEvent event);
	}
	
	
}
