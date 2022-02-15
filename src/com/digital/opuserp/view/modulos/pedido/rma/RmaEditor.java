package com.digital.opuserp.view.modulos.pedido.rma;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SeriaisCompraDAO;
import com.digital.opuserp.dao.SerialProdutoDAO;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.RmaDetalhe;
import com.digital.opuserp.domain.SeriaisCompra;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.FornecedorUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ProdutoUtil;
import com.digital.opuserp.view.util.SeriaisUtil;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
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
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class RmaEditor  extends Window implements GenericEditor {

	
	
	private Button btFinalizar;
	private Button btCancelar;
	private Button btFechar;
	
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private TextField txtDescricaoFornecedor;
	private TextField txtCodFornecedor;	
		
	
	private Button btSearchSerial;
	private TextField txtDescricaoSerial;
	private TextField txtCodSerial;	
	private SerialProduto SerialSelecionado;	
	
	private TextField txtDescricaoProduto;
	private TextField txtCodProduto;	
	private Produto ProdutoSelecionado;
	
	private TextField txtSerial;
	private TextArea txtDefeito;
	private DateField txtVencimento;
	private ComboBox cbStatus;
	private TextField txtNotaFiscal; 
	

	
	boolean visualizar;
	public RmaEditor(String title, boolean modal, final boolean editor){
		
		setWidth("834px");
		setHeight("334px");
		
		this.visualizar = editor;
		
		setCaption(title);
		setModal(modal);
		setResizable(true);
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
				
				
				if(!editor){
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtSalvar());
				}else{
					hlButtons.addComponent(buildBtFechar());
					hlButtons.addComponent(buildBtSalvar());
				}
				
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
			//if(editor){
			//	buildLayoutEditor();				
			//}else{
				buildLayout();				
		//}		
	}
	
	
	
	
	public void buildLayout(){
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
				boolean preencher = false;

				txtCodProduto = new TextField("Produto");				
				txtCodProduto.setWidth("60px");				
				txtCodProduto.setNullRepresentation("");
				txtCodProduto.setStyleName("caption-align");
				txtCodProduto.setId("txtCodProduto");
				txtCodProduto.focus();
								
				JavaScript.getCurrent().execute("$('#txtCodProduto').mask('0000000000')");
				txtCodProduto.setImmediate(true);
						
				txtCodProduto.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						ProdutoSelecionado = new Produto();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							ProdutoSelecionado = ProdutoDAO.find(Integer.parseInt(event.getText()));		
							
							if(ProdutoSelecionado != null){
								txtDescricaoProduto.setReadOnly(false);
								txtDescricaoProduto.setValue(ProdutoSelecionado.getNome());
								txtDescricaoProduto.setReadOnly(true);
								
								
								btSearchSerial.setEnabled(true);
								txtCodSerial.setEnabled(true);
								
								txtCodSerial.focus();
							}else {
								txtDescricaoProduto.setReadOnly(false);
								txtDescricaoProduto.setValue("");
								txtDescricaoProduto.setReadOnly(true);
															
								btSearchSerial.setEnabled(false);
								txtCodSerial.setEnabled(false);
							}
						}else{
							txtDescricaoProduto.setReadOnly(false);
							txtDescricaoProduto.setValue("");
							txtDescricaoProduto.setReadOnly(true);				
							
							btSearchSerial.setEnabled(false);
							txtCodSerial.setEnabled(false);
						}
					}
				});

				txtCodProduto.setRequired(true);		
				txtDescricaoProduto = new TextField();
				txtDescricaoProduto.setTabIndex(2000);
				txtDescricaoProduto.setReadOnly(true);
				txtDescricaoProduto.setWidth("480px");
					
				final Button btSearchProduto = new Button();
				btSearchProduto.setStyleName(BaseTheme.BUTTON_LINK);				
				btSearchProduto.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchProduto.setTabIndex(300000);
				
				btSearchProduto.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						ProdutoUtil cUtil = new ProdutoUtil(true, true);
						cUtil.addListerner(new ProdutoUtil.ProdutoListerner() {
							
							@Override
							public void onSelected(ProdutoUtil.ProdutoEvent event) {
									if(event.getProduto() != null){
										txtCodProduto.setValue(event.getProduto().getId().toString());
										txtDescricaoProduto.setReadOnly(false);
										txtDescricaoProduto.setValue(event.getProduto().getNome());
										txtDescricaoProduto.setReadOnly(true);
										ProdutoSelecionado = event.getProduto();				
										
										
										btSearchSerial.setEnabled(true);
										txtCodSerial.setEnabled(true);
										
										txtCodSerial.focus();
									}else{
										btSearchSerial.setEnabled(false);
										txtCodSerial.setEnabled(false);
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});
				
				
				FormLayout frmCodigoProduto = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
												
						addComponent(txtCodProduto);							
					}
				};
				addComponent(frmCodigoProduto);
		
				FormLayout frmButtonSearchProduto =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell_ajuste_bt_rma");										
						addComponent(btSearchProduto);							
					}
				}; 
							
				FormLayout frmDescProduto = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(txtDescricaoProduto);							
					}
				}; 
				addComponent(frmButtonSearchProduto);
				addComponent(frmDescProduto);
				setExpandRatio(frmDescProduto, 1);		
				
				
				if(txtCodProduto != null && ProdutoSelecionado != null){
					txtCodProduto.setValue(ProdutoSelecionado.getId().toString());
					txtCodProduto.setReadOnly(true);
				}
				
				if(txtDescricaoProduto!= null && ProdutoSelecionado != null){
					txtDescricaoProduto.setReadOnly(false);
					txtDescricaoProduto.setValue(ProdutoSelecionado.getNome());
					txtDescricaoProduto.setReadOnly(true);
				}

			}	
		});
		vlRoot.addComponent(new HorizontalLayout(){
			{
									
				txtCodSerial = new TextField("Serial");				
				txtCodSerial.setWidth("60px");				
				txtCodSerial.setNullRepresentation("");
				txtCodSerial.setStyleName("caption-align");
				txtCodSerial.setId("txtCodSerial");
				txtCodSerial.setEnabled(false);
								
				JavaScript.getCurrent().execute("$('#txtCodSerial').mask('0000000000')");
				txtCodSerial.setImmediate(true);
						
				txtCodSerial.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						SerialSelecionado = new SerialProduto();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							SerialSelecionado = SerialProdutoDAO.find(Integer.parseInt(event.getText()));		
							
							if(SerialSelecionado != null){
								txtDescricaoSerial.setReadOnly(false);
								txtDescricaoSerial.setValue(SerialSelecionado.getSerial());
								txtDescricaoSerial.setReadOnly(true);
								
								SeriaisCompra serialCompra = SeriaisCompraDAO.find(SerialSelecionado.getId());
								if(serialCompra != null && serialCompra.getItemPedido() != null && serialCompra.getItemPedido().getMovimentoEntCabecalhoId() != null){
									MovimentoEntCabecalho pedido = CompraDAO.find(serialCompra.getItemPedido().getMovimentoEntCabecalhoId());
									
									if(pedido != null){																				
										txtNotaFiscal.setValue(pedido.getCodNf().toString());
										Date sugestaoData = new DateTime(pedido.getDataHora()).plusMonths(12).toDate();
										txtVencimento.setValue(sugestaoData); 
									}
								}
								
								txtDefeito.focus();
								
							}else {
								txtDescricaoSerial.setReadOnly(false);
								txtDescricaoSerial.setValue("");
								txtDescricaoSerial.setReadOnly(true);
															
							}
						}else{
							txtDescricaoSerial.setReadOnly(false);
							txtDescricaoSerial.setValue("");
							txtDescricaoSerial.setReadOnly(true);							
						}
					}
				});

				txtCodSerial.setRequired(false);		
				txtDescricaoSerial = new TextField();
				txtDescricaoSerial.setTabIndex(2000);
				txtDescricaoSerial.setReadOnly(true);
				txtDescricaoSerial.setWidth("480px");

				btSearchSerial = new Button();
				btSearchSerial.setEnabled(false); 
				btSearchSerial.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchSerial.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchSerial.setTabIndex(300000);

				btSearchSerial.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						SeriaisUtil cUtil = new SeriaisUtil(true, true, ProdutoSelecionado, true);
						cUtil.addListerner(new SeriaisUtil.SerialListerner() {
							
							@Override
							public void onSelected(SeriaisUtil.SerialEvent event) {
									if(event.getSerial() != null){
										txtCodSerial.setValue(event.getSerial().getId().toString());
										txtDescricaoSerial.setReadOnly(false);
										txtDescricaoSerial.setValue(event.getSerial().getSerial());
										txtDescricaoSerial.setReadOnly(true);
										SerialSelecionado = event.getSerial();						
										
										SeriaisCompra serialCompra = SeriaisCompraDAO.find(SerialSelecionado.getId());
										if(serialCompra != null && serialCompra.getItemPedido() != null && serialCompra.getItemPedido().getMovimentoEntCabecalhoId() != null){
											MovimentoEntCabecalho pedido = CompraDAO.find(serialCompra.getItemPedido().getMovimentoEntCabecalhoId());
											
											if(pedido != null){
																								
												txtNotaFiscal.setValue(pedido.getCodNf().toString());
												
												Date sugestaoData = new DateTime(pedido.getDataHora()).plusMonths(12).toDate();
												txtVencimento.setValue(sugestaoData); 
											}
										}
										
										txtDefeito.focus();
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});
				
				
				FormLayout frmCodigoSerial = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
												
						addComponent(txtCodSerial);							
					}
				};
				addComponent(frmCodigoSerial);
		
				FormLayout frmButtonSearchSerial =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchSerial);							
					}
				}; 
							
				FormLayout frmDescSerial = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(txtDescricaoSerial);							
					}
				}; 
				addComponent(frmButtonSearchSerial);
				addComponent(frmDescSerial);
				setExpandRatio(frmDescSerial, 1);		
				
				
				if(txtCodSerial!= null && SerialSelecionado != null){
					txtCodSerial.setValue(SerialSelecionado.getId().toString());
					txtCodSerial.setReadOnly(true);
				}
				
				if(txtDescricaoSerial != null && SerialSelecionado != null){
					txtDescricaoSerial.setReadOnly(false);
					txtDescricaoSerial.setValue(SerialSelecionado.getSerial());
					txtDescricaoSerial.setReadOnly(true);
				}

			}	
		});
		vlRoot.addComponent(new FormLayout(){				
		    {
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
		
				txtNotaFiscal = new TextField("NF Compra");
				txtNotaFiscal.setRequired(true); 
				txtNotaFiscal.setWidth("151px");
				txtNotaFiscal.setStyleName("caption-align");
				txtNotaFiscal.setNullRepresentation("");
				txtNotaFiscal.setMaxLength(50);
				addComponent(txtNotaFiscal);
				
		    }
		});		
		vlRoot.addComponent(new FormLayout(){					
		    {
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
		
				txtDefeito = new TextArea("Defeito");
				txtDefeito.setRequired(true);
				txtDefeito.setWidth("280px");
				txtDefeito.setHeight("60px");
				txtDefeito.setStyleName("caption-align");
				txtDefeito.setNullRepresentation("");
				txtDefeito.setMaxLength(200);
				addComponent(txtDefeito);
				
		    }
		});
		vlRoot.addComponent(new FormLayout(){					
		    {
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
		
				txtVencimento = new DateField("Vencimento");
				txtVencimento.setRequired(true); 
				txtVencimento.setStyleName("caption-align");				
				addComponent(txtVencimento);
				
		    }
		});
		vlRoot.addComponent(new FormLayout(){					
		    {
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
		
				cbStatus = new ComboBox("Status");
				cbStatus.setNullSelectionAllowed(false); 
				cbStatus.addItem("A ENVIAR");
				cbStatus.addItem("AGUARDANDO COLETA");
				cbStatus.addItem("ENVIADO");
				cbStatus.addItem("CREDITADO");
				cbStatus.addItem("FORA DE GARANTIA");
				cbStatus.setRequired(true); 
				cbStatus.setStyleName("caption-align");				
				addComponent(cbStatus);
				
		    }
		});
	}

	
	@Override
	public Button buildBtSalvar() {
		
		btFinalizar = new Button("Finalizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(ProdutoSelecionado != null && 
						ProdutoSelecionado.getQtdEstoque() > 0 && 
						txtNotaFiscal.isValid() && 
						txtDefeito.isValid() && 
						txtVencimento.isValid() && 
						cbStatus.isValid()){
								
					RmaDetalhe rma = new RmaDetalhe(null, OpusERP4UI.getEmpresa().getId(),  ProdutoSelecionado, txtNotaFiscal.getValue(), 
							txtDefeito.getValue(), txtVencimento.getValue(), cbStatus.getValue().toString(), SerialSelecionado);
					
					fireEvent(new RmaEvent(getUI(), rma,  true));
					
				}else{
					
					if(ProdutoSelecionado == null){
						txtCodProduto.addStyleName("invalid-txt");
					}else{
						txtCodProduto.removeStyleName("invalid-txt");
					}
					
					
					if(!txtDefeito.isValid()){
						txtDefeito.addStyleName("invalid-txt");
					}else{ 
						txtDefeito.removeStyleName("invalid-txt");
					}
					
					if(!txtVencimento.isValid()){
						txtVencimento.addStyleName("invalid-txt");
					}else{
						txtVencimento.removeStyleName("invalid-txt");
					}
					
					if(!txtNotaFiscal.isValid()){
						txtNotaFiscal.addStyleName("invalid-txt");
					}else{
						txtNotaFiscal.removeStyleName("invalid-txt");
					}
					
					if(!cbStatus.isValid()){
						cbStatus.addStyleName("invalid-txt");
					}else{
						cbStatus.removeStyleName("invalid-txt");
					}
					
					
					
					Notify.Show_Invalid_Submit_Form();
					
					
					if(ProdutoSelecionado != null && ProdutoSelecionado.getQtdEstoque() == 0){
						Notify.Show("Produto não tem Saldo!", Notify.TYPE_WARNING);
					}
				}
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btFinalizar.click();
			}
		};
		
		btFinalizar.addShortcutListener(slbtOK);
		
		btFinalizar.setStyleName("default");
		return btFinalizar;
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
	
	
	public Button buildBtFechar() {
		btFechar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {								
				close();			
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btFechar.click();
			}
		};
		
		btFechar.addShortcutListener(slbtCancelar);
		
		
		return btFechar;
	}
	
	
	public void addListerner(RmaListerner target){
		try {
			Method method = RmaListerner.class.getDeclaredMethod("onClose", RmaEvent.class);
			addListener(RmaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(RmaListerner target){
		removeListener(RmaEvent.class, target);
	}
	public static class RmaEvent extends Event{
		
		private RmaDetalhe rma;
		private boolean confirm;
		
		public RmaEvent(Component source, RmaDetalhe rma, boolean confirm) {
			super(source);
			this.rma = rma;
			this.confirm = confirm;
					
		}
		public RmaDetalhe getRma(){
			return rma;
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface RmaListerner extends Serializable{
		public void onClose(RmaEvent event);
	}
	
	
	
}
