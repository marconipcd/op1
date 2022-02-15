package com.digital.opuserp.view.modulos.estoque.produto;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.dao.LogProdutoDAO;
import com.digital.opuserp.dao.NaturezaOperacaoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.NaturezaOperacao;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CorrecaoSaldo extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private TextField tfCodProduto; 
	private TextField tfProduto; 
	
	private TextField tfSaldoLoja;
	private TextField tfSaldoLojaAlterar;
	
	private TextField tfSaldoDeposito;
	private TextField tfSaldoDepositoAlterar;
	
								
	EcfPreVendaCabecalho pedido;
	EcfPreVendaDetalhe detalhe;

	MovimentoEntCabecalho compra;
	MovimentoEntDetalhe contaDetalhe;
	
	private boolean adicionar;
	private Produto produto;
	
	public CorrecaoSaldo(Produto produto, String title, boolean modal, boolean adicionar){
		this.adicionar = adicionar;	
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
				setWidth("900px");
				//setHeight("243px");

		//}	
	}
	
	ComboBox cbOpcao;

	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(	new FormLayout(){					
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
		
		vlRoot.addComponent(new FormLayout(){					
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
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);								
				
				cbOpcao = new ComboBox("Qual estoque ?");
				cbOpcao.setNullSelectionAllowed(false); 
				cbOpcao.setWidth("100px");
				cbOpcao.addItem("Saldo Loja");				
				cbOpcao.addItem("Saldo Deposito");
				cbOpcao.setStyleName("caption-align");
				
				cbOpcao.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						
						if(cbOpcao.getValue() != null && cbOpcao.getValue().toString().equals("Saldo Loja")){
							tfSaldoLoja.setEnabled(true); 
							tfSaldoLojaAlterar.setEnabled(true); 
							
							tfSaldoDeposito.setEnabled(false);
							tfSaldoDepositoAlterar.setEnabled(false);
						}
						
						if(cbOpcao.getValue() != null && cbOpcao.getValue().toString().equals("Saldo Deposito")){
							tfSaldoLoja.setEnabled(false); 
							tfSaldoLojaAlterar.setEnabled(false); 
							
							tfSaldoDeposito.setEnabled(true);
							tfSaldoDepositoAlterar.setEnabled(true);
						}
					}
				});
				
				
				addComponent(cbOpcao);
				
			}
		});
		
		vlRoot.addComponent(	new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);								
						
						tfSaldoLoja = new TextField("Saldo Loja Atual");				
						tfSaldoLoja.setWidth("100px");				
						tfSaldoLoja.setStyleName("caption-align");
						tfSaldoLoja.setNullRepresentation("");
						tfSaldoLoja.setId("Sald");
						tfSaldoLoja.setValue(Real.formatDbToString(String.valueOf(produto.getQtdEstoque())));						
						addComponent(tfSaldoLoja);			
						tfSaldoLoja.setReadOnly(true);
						
						
					}
				});
		
		vlRoot.addComponent(
				new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);								
						
						if(adicionar){
							tfSaldoLojaAlterar = new TextField("Adicionar");		
						}else{
							tfSaldoLojaAlterar = new TextField("Subtrair");									
						}
						tfSaldoLojaAlterar.setWidth("100px");			
						
						tfSaldoLojaAlterar.setStyleName("caption-align");
						tfSaldoLojaAlterar.setNullRepresentation("");
						tfSaldoLojaAlterar.focus();
						tfSaldoLojaAlterar.setId("AltreSaldo");
						
						tfSaldoLojaAlterar.setEnabled(false); 


						if(produto.getFracionar()!=null && !produto.getFracionar().equals("") && produto.getFracionar().equals(1)){
							JavaScript.getCurrent().execute("$('#AltreSaldo').maskMoney({decimal:',',thousands:'.'})");
						}else{
							JavaScript.getCurrent().execute("$('#AltreSaldo').mask('00000000000000')");
						}
						addComponent(tfSaldoLojaAlterar);	
					}
				});
		
		
		vlRoot.addComponent(	new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);								
				
				tfSaldoDeposito = new TextField("Saldo Deposito Atual");				
				tfSaldoDeposito.setWidth("100px");				
				tfSaldoDeposito.setStyleName("caption-align");
				tfSaldoDeposito.setNullRepresentation("");
				tfSaldoDeposito.setId("Sald");
				tfSaldoDeposito.setValue(Real.formatDbToString(String.valueOf(produto.getQtdEstoqueDeposito())));						
				addComponent(tfSaldoDeposito);			
				tfSaldoDeposito.setReadOnly(true);
				
				
			}
		});

		vlRoot.addComponent(
			new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);								
					
					if(adicionar){
						tfSaldoDepositoAlterar = new TextField("Adicionar");		
					}else{
						tfSaldoDepositoAlterar = new TextField("Subtrair");									
					}
					tfSaldoDepositoAlterar.setWidth("100px");			
					
					tfSaldoDepositoAlterar.setStyleName("caption-align");
					tfSaldoDepositoAlterar.setNullRepresentation("");
					tfSaldoDepositoAlterar.focus();
					tfSaldoDepositoAlterar.setId("AltreSaldo");
	
					tfSaldoDepositoAlterar.setEnabled(false); 
	
					if(produto.getFracionar()!=null && !produto.getFracionar().equals("") && produto.getFracionar().equals(1)){
						JavaScript.getCurrent().execute("$('#AltreSaldo').maskMoney({decimal:',',thousands:'.'})");
					}else{
						JavaScript.getCurrent().execute("$('#AltreSaldo').mask('00000000000000')");
					}
					addComponent(tfSaldoDepositoAlterar);	
				}
		});



		
		
	}
	
	
	
	
	

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(tfSaldoLojaAlterar!=null && tfSaldoLojaAlterar.getValue()!=null && !tfSaldoLojaAlterar.getValue().equals("") || 
				tfSaldoDepositoAlterar != null && tfSaldoDepositoAlterar.getValue() != null && !tfSaldoDepositoAlterar.getValue().equals("")){
					try {
						
						float qtdAtual = 0,qtdNova = 0;  
						
						if(cbOpcao.getValue() != null && cbOpcao.getValue().toString().equals("Saldo Loja")){
							qtdAtual = produto.getQtdEstoque();
							qtdNova =  Real.formatStringToDBFloat(tfSaldoLojaAlterar.getValue());						
							
						}
						
						if(cbOpcao.getValue() != null && cbOpcao.getValue().toString().equals("Saldo Deposito")){
							qtdAtual = produto.getQtdEstoqueDeposito();
							qtdNova =  Real.formatStringToDBFloat(tfSaldoDepositoAlterar.getValue());
						}
							
						Cliente cliente = ClienteDAO.findByCpf(OpusERP4UI.getEmpresa().getCnpj());												
						pedido = new EcfPreVendaCabecalho();
						detalhe = new EcfPreVendaDetalhe();
						
						Fornecedor Fornecedor = FornecedorDAO.findbyCnpjEmp(OpusERP4UI.getEmpresa().getCnpj(),OpusERP4UI.getEmpresa().getId());							
						Integer CodId = Integer.parseInt(CompraDAO.getNextID());
						compra = new MovimentoEntCabecalho();
						contaDetalhe = new MovimentoEntDetalhe();
						
						if(adicionar){							
							qtdAtual = qtdAtual + qtdNova;
							
							compra.setId(CodId);
							compra.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
							compra.setCodNf(0);
							compra.setFornecedor(Fornecedor);
							compra.setQtdItens(new Float(0));
							compra.setValorTotal(new Float(0));
							compra.setDataHora(new Date());
							compra.setUsuario(OpusERP4UI.getUsuarioLogadoUI().getUsername());
							compra.setSituacao("F");
							compra.setTipo("CORRECAO");
							
							contaDetalhe.setMovimentoEntCabecalhoId(compra.getId());
							contaDetalhe.setProdutoId(produto.getId());
							contaDetalhe.setQuantidade(qtdNova);
							
							//-----Logg
							LogProdutoDAO.registraLog(new LogProduto(null,produto, "CORRECAO ESTOQUE", produto.getQtdEstoque(),qtdAtual, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
							//-----Logg
							
							if(cbOpcao.getValue() != null && cbOpcao.getValue().toString().equals("Saldo Loja")){							
								produto.setQtdEstoque(qtdAtual);
							}
							
							if(cbOpcao.getValue() != null && cbOpcao.getValue().toString().equals("Saldo Deposito")){						
								produto.setQtdEstoqueDeposito(qtdAtual);
							}

							ProdutoDAO.alterar(produto);		
							
							fireEvent(new SerialEvent(getUI(),null,null,compra,contaDetalhe,produto, true));								
						}else{
							qtdAtual = qtdAtual - qtdNova;
							
							pedido.setCliente(cliente);
							pedido.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
							pedido.setData(new Date());
							pedido.setTipo("CORRECAO");
							pedido.setSituacao("F");
							
							NaturezaOperacao natureza = NaturezaOperacaoDAO.findbyName("VENDA");
							pedido.setNaturezaOperacao(natureza);
							
							detalhe.setProdutoId(produto.getId());
							
							if(cbOpcao.getValue() != null && cbOpcao.getValue().toString().equals("Saldo Loja")){		
								detalhe.setQuantidade(Real.formatStringToDBFloat(tfSaldoLojaAlterar.getValue()));
							}
							
							if(cbOpcao.getValue() != null && cbOpcao.getValue().toString().equals("Saldo Deposito")){						
								detalhe.setQuantidade(Real.formatStringToDBFloat(tfSaldoDepositoAlterar.getValue()));
							}
							
							//-----Logg
							LogProdutoDAO.registraLog(new LogProduto(null,produto, "CORRECAO ESTOQUE", produto.getQtdEstoque(),qtdAtual, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
							//-----Logg
							
							
							
							if(cbOpcao.getValue() != null && cbOpcao.getValue().toString().equals("Saldo Loja")){							
								produto.setQtdEstoque(qtdAtual);								
							}
							
							if(cbOpcao.getValue() != null && cbOpcao.getValue().toString().equals("Saldo Deposito")){						
								produto.setQtdEstoqueDeposito(qtdAtual);
							}
							
							ProdutoDAO.alterar(produto);		
							
							fireEvent(new SerialEvent(getUI(),pedido,detalhe,null,null,null,true));				
						}

						Notify.Show("Produto Corrigido com Sucesso!", Notify.TYPE_SUCCESS);

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
					
					if(tfSaldoLojaAlterar.getValue()==null||tfSaldoLojaAlterar.getValue().equals("")){
						tfSaldoLojaAlterar.addStyleName("invalid-txt");
					}else{
						tfSaldoLojaAlterar.removeStyleName("invalid-txt");
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
					fireEvent(new SerialEvent(getUI(),null,null,null,null,null,false));
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
									if(adicionar){
										fireEvent(new SerialEvent(getUI(),null,null,compra,contaDetalhe,produto, true));																			
									}else{
										fireEvent(new SerialEvent(getUI(),pedido,detalhe,null,null,null,true));																																	
									}
																	
								
									Notify.Show("Serial Gravado com Sucesso!", Notify.TYPE_SUCCESS);
									
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
								fireEvent(new SerialEvent(getUI(),null,null,null,null,null, false));
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
		private Float qtdNova;
		private EcfPreVendaCabecalho pedido;
		private EcfPreVendaDetalhe detalhe;
		private MovimentoEntCabecalho compra;
		private MovimentoEntDetalhe moviDet;
		private Produto p;
		
		public SerialEvent(Component source,EcfPreVendaCabecalho pedido,EcfPreVendaDetalhe detalhe,MovimentoEntCabecalho compra, MovimentoEntDetalhe moviDet, Produto p,boolean confirm) {
			super(source);
			this.confirm = confirm;			
			this.pedido = pedido;			
			this.detalhe = detalhe;			
			this.compra = compra;			
			this.moviDet = moviDet;		
			this.p = p;
		}

		public boolean isConfirm() {
			return confirm;
		}

		public EcfPreVendaCabecalho getPedido() {
			return pedido;
		}

		public EcfPreVendaDetalhe getDetalhe() {
			return detalhe;
		}

		public MovimentoEntCabecalho getCompra() {
			return compra;
		}

		public MovimentoEntDetalhe getMoviDet() {
			return moviDet;
		}
		
		public Produto getProduto() {
			return p;
		}
	
	}
	public interface SerialListerner extends Serializable{
		public void onClose(SerialEvent event);
	}
	
	
}
