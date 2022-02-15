package com.digital.opuserp.view.modulos.estoque.serial;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesSerialDAO;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SeriaisCompraDAO;
import com.digital.opuserp.dao.SerialDAO;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SeriaisCompra;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.Validator;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.modulos.estoque.serial.SerialEditor.SerialEvent;
import com.digital.opuserp.view.modulos.estoque.serial.SerialEditor.SerialListerner;
import com.digital.opuserp.view.util.ComprasUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ProdutoUtil;
import com.digital.opuserp.view.util.ComprasUtil.CompraEvent;
import com.digital.opuserp.view.util.ProdutoUtil.ProdutoEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.themes.BaseTheme;

public class SerialEditorReadOnly extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private ComboBox cbProduto; 
	private ComboBox cbTipoSerial;
	private TextField txtSerial;
	private TextField txtSerialNovo;
	private TextArea txtAreaSerial;
	private TextField txtSaldo;
	private TextField txtSerialAtivos;
	private TextField tfCodProduto;
	private TextField tfDescricaoProduto;
	private TextField tfCodCompra;
	private TextField tfDescricaoCompra;
	private Integer serialAtivo;
	List<SerialProduto> serialProd;
	Produto produto_selecionado;
	MovimentoEntCabecalho compra_selecionado;
	String serialAtual;
	Button btSearchCliente;
	Integer codserial;
	Integer serialLivres = 0;

	
	private boolean serialValido;
	
	public SerialEditorReadOnly(Item item, String title, boolean modal){
		this.item = item;
		
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
				//hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
		buildLayout();
	}
	
	
	
	private void configLayout(){
		//1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("820px");
				
				if(item.getItemProperty("id").getValue()!= null){
					setHeight("340px");
				}else{
					setHeight("370px");
				}

		//}	
	}
	
	
	private JPAContainer<Produto> buildContainerProduto(){
		JPAContainer<Produto> container = JPAContainerFactory.make(Produto.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresaId", OpusERP4UI.getEmpresa().getId()));
		
		return container;
	}
	
	public void buildLayout(){
		
		SerialDAO sDAO = new SerialDAO();
		
		if(item.getItemProperty("id").getValue()!= null && item.getItemProperty("tipo_serial").getValue() != null){
			serialValido = true;
			serialAtual = (String)item.getItemProperty("serial").getValue();
			codserial = (Integer)item.getItemProperty("id").getValue();
		}	
		
		
		
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
//						CompraDAO cDAO = new CompraDAO();

						compra_selecionado = new MovimentoEntCabecalho();
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							compra_selecionado = CompraDAO.find(Integer.parseInt(event.getText()));		
							
							if(compra_selecionado != null){
								
								serialProd = SerialDAO.findByCodProd(compra_selecionado.getId());
								
								tfDescricaoCompra.setReadOnly(false);
								tfDescricaoCompra.setValue(compra_selecionado.getFornecedor().getRazao_social());
								tfDescricaoCompra.setReadOnly(true);		
								
								tfCodProduto.setValue("");
								tfDescricaoProduto.setReadOnly(false);
								tfDescricaoProduto.setValue("");
								tfDescricaoProduto.setReadOnly(true);
							}else{
								tfDescricaoCompra.setReadOnly(false);
								tfDescricaoCompra.setValue("");
								tfDescricaoCompra.setReadOnly(true);	
							}
						}else{
							tfDescricaoCompra.setReadOnly(false);
							tfDescricaoCompra.setValue("");
							tfDescricaoCompra.setReadOnly(true);
							compra_selecionado = null;
							
							tfDescricaoProduto.setReadOnly(false);
							tfDescricaoProduto.setValue("");
							tfDescricaoProduto.setReadOnly(true);
							
							tfCodCompra.setValue("");
						}
					}
				});

					
				tfDescricaoCompra = new TextField();
				tfDescricaoCompra.setReadOnly(true);
				tfDescricaoCompra.setWidth("490px");			
									
				
							
				btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						ComprasUtil cUtil = new ComprasUtil(true, true);
						cUtil.addListerner(new ComprasUtil.CompraListerner() {
							
							@Override
							public void onSelected(CompraEvent event) {
									
									if(event.getCompra() != null){
										compra_selecionado = event.getCompra();
										
//										serialProd = SerialDAO.findByCodProd(compra_selecionado.getId());

										tfCodCompra.setValue(event.getCompra().getId().toString());
										tfDescricaoCompra.setReadOnly(false);
										tfDescricaoCompra.setValue(event.getCompra().getFornecedor().getRazao_social());
										tfDescricaoCompra.setReadOnly(true);	
										
										tfCodProduto.setValue("");
										tfDescricaoProduto.setReadOnly(false);
										tfDescricaoProduto.setValue("");
										tfDescricaoProduto.setReadOnly(true);
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
						setStyleName("form-cutom-new");		

						if(codserial!=null){
							SeriaisCompra scp = SeriaisCompraDAO.find((codserial));
							if(scp!=null){
								MovimentoEntDetalhe md = CompraDAO.findByCod(scp.getItemPedido().getId());
								if(md!=null){
									compra_selecionado = CompraDAO.find(md.getMovimentoEntCabecalhoId());					
									if(compra_selecionado!=null){
										tfCodCompra.setReadOnly(false);
										tfCodCompra.setValue(compra_selecionado.getId().toString());
										tfCodCompra.setReadOnly(true);			
									}
								}
							}
						}
						addComponent(tfCodCompra);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");							
						addComponent(btSearchCliente);							
					}
				}; 
							
				if(item.getItemProperty("id").getValue() != null){
					btSearchCliente.setEnabled(false);
					tfCodCompra.setReadOnly(true);
				}
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom-new_hide_require");
						
						
						if(codserial!=null){
							SeriaisCompra scp = SeriaisCompraDAO.find((codserial));
							if(scp!=null){
								MovimentoEntDetalhe md = CompraDAO.findByCod(scp.getItemPedido().getId());
								if(md!=null){
									compra_selecionado = CompraDAO.find(md.getMovimentoEntCabecalhoId());					
									if(compra_selecionado != null){
										tfDescricaoCompra.setReadOnly(false);
										tfDescricaoCompra.setValue(compra_selecionado.getFornecedor().getRazao_social());
										tfDescricaoCompra.setReadOnly(true);									
									}
								}
							}
						}
						
						addComponent(tfDescricaoCompra);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);	
			}		
		});
		
		
		

		vlRoot.addComponent(new HorizontalLayout(){
			{
											
				
//				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("produto").getValue() != null){
//					produto_selecionado = (Produto)item.getItemProperty("produto").getValue();
//				}
				
				tfCodProduto = new TextField("Produto");				
				tfCodProduto.setWidth("60px");				
				tfCodProduto.setNullRepresentation("");
				tfCodProduto.setStyleName("caption-align");
				tfCodProduto.setImmediate(true);
				tfCodProduto.setRequired(true);	
						
								
				tfCodProduto.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ProdutoDAO cDAO = new ProdutoDAO();
						serialProd = null;
						serialAtivo = 0;
						produto_selecionado = new Produto();
						produto_selecionado = null;
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							if(compra_selecionado != null){
								List<MovimentoEntDetalhe> md = CompraDAO.findByCabt(compra_selecionado.getId());
								
								for(MovimentoEntDetalhe movDet: md){	
									String cod = String.valueOf(movDet.getProdutoId());
									if(event.getText().equals(cod)){
										produto_selecionado = cDAO.find(Integer.parseInt(event.getText()));	
									}
								}
							}else{
								produto_selecionado = cDAO.find(Integer.parseInt(event.getText()));										
							}
							
							if(produto_selecionado != null){
								
								serialProd = SerialDAO.findByCodProd(produto_selecionado.getId());
								
									if(produto_selecionado.getQtdEstoque()!=null){
										txtSaldo.setReadOnly(false);
										DecimalFormat df = new DecimalFormat("0.");     
										String saldo = df.format(produto_selecionado.getQtdEstoque());  
										saldo = saldo.replace(",", "");
										txtSaldo.setValue(saldo); 
										
										txtSaldo.setReadOnly(true);									
									if(serialProd!=null){
										for(SerialProduto serial:serialProd){
											if(serial.getStatus().equals("ATIVO")){
												serialAtivo ++;
											}
										}	
									}else{
										txtSerialAtivos.setReadOnly(false);
										txtSerialAtivos.setValue("0");
										txtSerialAtivos.setReadOnly(true);
									}
									serialLivres = Integer.parseInt(txtSaldo.getValue())-serialAtivo;
									
									txtSerialAtivos.setReadOnly(false);
									txtSerialAtivos.setValue(String.valueOf(serialAtivo));
									txtSerialAtivos.setReadOnly(true);
								}
		
								tfDescricaoProduto.setReadOnly(false);
								tfDescricaoProduto.setValue(produto_selecionado.getNome());
								tfDescricaoProduto.setReadOnly(true);														
							}else{
								tfDescricaoProduto.setReadOnly(false);
								tfDescricaoProduto.setValue("");
								tfDescricaoProduto.setReadOnly(true);	
								
								txtSerialAtivos.setReadOnly(false);
								txtSerialAtivos.setValue("0");
								txtSerialAtivos.setReadOnly(true);
								
								txtSaldo.setReadOnly(false);
								txtSaldo.setValue("0"); 
								txtSaldo.setReadOnly(true);
							}
						}
					}
				});

					
				tfDescricaoProduto = new TextField();
				tfDescricaoProduto.setReadOnly(true);
				tfDescricaoProduto.setWidth("490px");			
									
				
							
				btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.addListener(new Button.ClickListener() {
				ProdutoUtil cUtil;	
					@Override
					public void buttonClick(ClickEvent event) {
						
						
						if(compra_selecionado != null){																				
							cUtil = new ProdutoUtil(true, true, compra_selecionado.getId());
						}else{
							cUtil = new ProdutoUtil(true, true);
						}
						cUtil.addListerner(new ProdutoUtil.ProdutoListerner() {
							
							@Override
							public void onSelected(ProdutoEvent event) {
									
									if(event.getProduto() != null){
										produto_selecionado = event.getProduto();
										serialAtivo = 0;
										serialProd = SerialDAO.findByCodProd(produto_selecionado.getId());
										
										if(produto_selecionado.getQtdEstoque()!=null){
											txtSaldo.setReadOnly(false);
											DecimalFormat df = new DecimalFormat("0.");     
											String saldo = df.format(produto_selecionado.getQtdEstoque());  
											saldo = saldo.replace(",", "");
											txtSaldo.setValue(saldo); 
											
											txtSaldo.setReadOnly(true);
										}
										
										if(serialProd!=null){
											for(SerialProduto serial:serialProd){
												if(serial.getStatus().equals("ATIVO")){
													serialAtivo ++;
												}
											}																	
											
											txtSerialAtivos.setReadOnly(false);
											txtSerialAtivos.setValue(String.valueOf(serialAtivo));
											txtSerialAtivos.setReadOnly(true);
										}
										
										serialLivres = Integer.parseInt(txtSaldo.getValue())-serialAtivo;
										
										tfCodProduto.setValue(event.getProduto().getId().toString());
										tfDescricaoProduto.setReadOnly(false);
										tfDescricaoProduto.setValue(event.getProduto().getNome());
										tfDescricaoProduto.setReadOnly(true);											
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
						setStyleName("form-cutom-new");		
												
						if(item.getItemProperty("id").getValue() != null && item.getItemProperty("produto").getValue() != null){
							produto_selecionado = (Produto)item.getItemProperty("produto").getValue();
							tfCodProduto.setReadOnly(false);
							tfCodProduto.setValue(produto_selecionado.getId().toString());
							tfCodProduto.setReadOnly(true);			
						}
						addComponent(tfCodProduto);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");							
						addComponent(btSearchCliente);							
					}
				}; 
							
				if(item.getItemProperty("id").getValue() != null){
					btSearchCliente.setEnabled(false);
				}
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom-new_hide_require");
						
						if(item.getItemProperty("id").getValue() != null && item.getItemProperty("produto").getValue() != null){
							produto_selecionado = (Produto)item.getItemProperty("produto").getValue();
							tfDescricaoProduto.setReadOnly(false);
							tfDescricaoProduto.setValue(produto_selecionado.getNome());
							tfDescricaoProduto.setReadOnly(true);									
						}
						
						addComponent(tfDescricaoProduto);							
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
								
				txtSaldo = new TextField("Saldo");				
				txtSaldo.setWidth("50px");				
				txtSaldo.setStyleName("caption-align");
				txtSaldo.setNullRepresentation("");
				txtSaldo.setReadOnly(true);		
				txtSaldo.addStyleName("align-currency");
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("produto").getValue() != null){
					produto_selecionado = (Produto)item.getItemProperty("produto").getValue();						
					if(produto_selecionado.getQtdEstoque()!=null){
						txtSaldo.setReadOnly(false);
											
						DecimalFormat df = new DecimalFormat("0.");     
						String saldo = df.format(produto_selecionado.getQtdEstoque());  
						saldo = saldo.replace(",", "");
						txtSaldo.setValue(saldo); 
						txtSaldo.setReadOnly(true);
					}					
				}
				
				addComponent(txtSaldo);	
				
			}
		});
			
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);								
									
					txtSerialAtivos = new TextField("Seriais Ativos");				
					txtSerialAtivos.setWidth("50px");				
					txtSerialAtivos.setStyleName("caption-align");
					txtSerialAtivos.setNullRepresentation("");
					txtSerialAtivos.addStyleName("align-currency");
					
					if(item.getItemProperty("id").getValue() != null && item.getItemProperty("produto").getValue() != null){
						produto_selecionado = (Produto)item.getItemProperty("produto").getValue();
						serialProd = SerialDAO.findByCodProd(produto_selecionado.getId());	
						serialAtivo = 0;
						if(serialProd!=null){							
							for(SerialProduto serial:serialProd){
								if(serial.getStatus().equals("ATIVO")){
									serialAtivo ++;
								}
							}										
							txtSerialAtivos.setReadOnly(false);
							txtSerialAtivos.setValue(String.valueOf(serialAtivo));
							txtSerialAtivos.setReadOnly(true);
						}						
					}	
					txtSerialAtivos.setReadOnly(true);		
					
					addComponent(txtSerialAtivos);	
					
				}
			});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom-new");						
									
					cbTipoSerial = new ComboBox("Tipo de Serial");
					cbTipoSerial.setStyleName("caption-align");
					cbTipoSerial.setNullSelectionAllowed(false);					
					cbTipoSerial.setRequired(true);
					cbTipoSerial.addItem("Endereço MAC");
					cbTipoSerial.addItem("Número Serial");
					cbTipoSerial.setWidth("120px");
					cbTipoSerial.setTextInputAllowed(false);							
									
					if(item.getItemProperty("id").getValue()!= null && item.getItemProperty("tipo_serial").getValue() != null){
						
						if(item.getItemProperty("tipo_serial").getValue().equals("MAC")){	
							cbTipoSerial.setReadOnly(false);	
							cbTipoSerial.setValue("Endereço MAC");
							cbTipoSerial.setReadOnly(true);	
						}else if(item.getItemProperty("tipo_serial").getValue().equals("SERIAL")){
							cbTipoSerial.setReadOnly(false);	
							cbTipoSerial.setValue("Número Serial");
							cbTipoSerial.setReadOnly(true);	
						}				
					}else if(item.getItemProperty("id").getValue()!= null && item.getItemProperty("tipo_serial").getValue() == null){
						cbTipoSerial.setReadOnly(true);							
					}
										
					addComponent(cbTipoSerial);
				}
		});
		
		if(item.getItemProperty("id").getValue() != null){
			vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);								
									
					txtSerial = new TextField("Serial Atual");				
	//				txtSerial.setWidth("100%");		
					txtSerial.setWidth("120px");
					txtSerial.setStyleName("caption-align");
					txtSerial.setNullRepresentation("");

					if(item.getItemProperty("id").getValue()!= null && item.getItemProperty("tipo_serial").getValue() != null){
						
						if(cbTipoSerial.getValue().toString().equals("Endereço MAC")){
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
	
			
		}else{		
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);								
										
						txtAreaSerial = new TextArea("Seriais");				
		//				txtSerial.setWidth("100%");		
						txtAreaSerial.setWidth("95%");
						txtAreaSerial.setHeight("80px");
						txtAreaSerial.setStyleName("caption-align");
						txtAreaSerial.setNullRepresentation("");
						txtAreaSerial.setRequired(true);
						txtAreaSerial.setImmediate(true);					
						
						addComponent(txtAreaSerial);	
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
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
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
	
	public void addListerner(SerialListerner target){
		try {
			Method method = SerialListerner.class.getDeclaredMethod("onClose", SerialEvent.class);
			addListener(SerialEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(SerialEvent.class, target);
	}
	public static class SerialEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public SerialEvent(Component source, Item item, boolean confirm) {
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
	public interface SerialListerner extends Serializable{
		public void onClose(SerialEvent event);
	}
	
	
}
