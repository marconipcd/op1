package com.digital.opuserp.view.modulos.financeiro.contasPagar;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.digital.opuserp.dao.ContasDAO;
import com.digital.opuserp.domain.Contas;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.comp.RealTextField.RealTextField;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClientesListerner;
import com.digital.opuserp.view.modulos.financeiro.contasPagar.BaixarContasPagar.BaixarTituloEvent;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Adiarconta extends Window implements GenericEditor {

	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;

	Item item;

	DateField dfVencimento; //
	Date dataVencimento;

		
	boolean data_venc_valid = false;
	boolean valor_valid = true;
	
	boolean nDoc_valid = true;
	
	String controle;
	Integer codCliente;
	
	
	public Adiarconta(Item item, String title, boolean modal){
		this.item = item;		
		
		setWidth("450px");
		setHeight("180px");
		
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
	
	
	public String getNextId() {
		ContasDAO cDao = new ContasDAO();
		return cDao.getNextID();
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);

			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);						
						
						dfVencimento = new DateField("Data Vencimento");
						dfVencimento.setStyleName("caption-align-editar-titulo");
						dfVencimento.setImmediate(true);
						dfVencimento.setDateFormat("dd/MM/yyyy");
						dfVencimento.setRequired(true);
												
						addComponent(dfVencimento);
						fieldGroup.bind(dfVencimento, "data_vencimento");
						
					}
			});
			

	
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
						
				if(dfVencimento.isValid()){
					try {
						item.getItemProperty("data_vencimento").setValue(dfVencimento.getValue());
						fieldGroup.commit();
						fireEvent(new AdiarTituloEvent(event.getComponent().getParent(),true,item));
						
						Notification.show("Título Alterado Com Sucesso!");
						close();
					} catch (CommitException e) {			
						e.printStackTrace();
						Notification.show("Erro!");
					}

				}else{						
				
					if(!dfVencimento.isValid()){
						dfVencimento.addStyleName("invalid-txt");
					}else{
						dfVencimento.removeStyleName("invalid-txt");
					}
					
					Notification.show("Não é Possivel Alterar Tituilo, Verifique se todos os Campos estão Preenchidos!");
					}		
				}
		});	
		
		btSalvar.addStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("OK", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);
		
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();				
			}
		});
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}
	
	public void addListerner(AdiarTituloListerner target){
		try {
			Method method = AdiarTituloListerner.class.getDeclaredMethod("onClose", AdiarTituloEvent.class);
			addListener(AdiarTituloEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ClientesListerner target){
		removeListener(AdiarTituloEvent.class, target);
	}
	public static class AdiarTituloEvent extends Event{
		
		private String id;
		private String n_cod;
		private String termo;
		private String tipo;
		private Contas conta;
		private Fornecedor fornecedor;
		private String descricao;
		private Date dataVenciemento;
		private String valor;
		private String qtd;
		private String intervalo;	
		private boolean confirm;
		
		private Item item;	

		public AdiarTituloEvent(Component souce, boolean confirm, Item item) {
			super(souce);
			this.confirm = confirm;
			this.item = item;
		}

		public AdiarTituloEvent(Component souce, String termo, String tipo,
				Contas conta, Fornecedor fornecedor, String descricao,
				Date dataVenciemento, String valor, String qtd,
				String intervalo, boolean confirm,String n_cod,String id) {
			super(souce);
			
			this.termo = termo;
			this.tipo = tipo;
			this.conta = conta;
			this.fornecedor = fornecedor;
			this.descricao = descricao;
			this.dataVenciemento = dataVenciemento;
			this.valor = valor;
			this.qtd = qtd;
			this.intervalo = intervalo;
			this.confirm = confirm;
			this.n_cod = n_cod;
			this.id = id;
		}

		public String getTermo() {
			return termo;
		}

		public void setTermo(String termo) {
			this.termo = termo;
		}

		public String getTipo() {
			return tipo;
		}

		public void setTipo(String tipo) {
			this.tipo = tipo;
		}
		

		public Contas getConta() {
			return conta;
		}

		public Fornecedor getFornecedor() {
			return fornecedor;
		}

		public void setFornecedor(Fornecedor fornecedor) {
			this.fornecedor = fornecedor;
		}

		public String getDescricao() {
			return descricao;
		}

		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}

		public Date getDataVenciemento() {
			return dataVenciemento;
		}

		public void setDataVenciemento(Date dataVenciemento) {
			this.dataVenciemento = dataVenciemento;
		}

		public String getValor() {
			return valor;
		}

		public void setValor(String valor) {
			this.valor = valor;
		}

		public String getQtd() {
			return qtd;
		}

		public void setQtd(String qtd) {
			this.qtd = qtd;
		}

		public String getIntervalo() {
			return intervalo;
		}

		public void setIntervalo(String intervalo) {
			this.intervalo = intervalo;
		}

		public boolean isConfirm() {
			return confirm;
		}

		public void setConfirm(boolean confirm) {
			this.confirm = confirm;
		}

		public String getN_cod() {
			return n_cod;
		}

		public void setN_cod(String n_cod) {
			this.n_cod = n_cod;
		}

		public void setConta(Contas conta) {
			this.conta = conta;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
	
	}
	public interface AdiarTituloListerner extends Serializable{
		public void onClose(AdiarTituloEvent event);
	}

}

