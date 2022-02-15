package com.digital.opuserp.view.modulos.financeiro.formasPgto;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FormasPgtoEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private ComboBox cbNecessarioCredito;
	
	
	public FormasPgtoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		
		//configLayout();	
		
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
				setWidth("832px");
				setHeight("430px");		
		//}
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		
				
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField txtNome = (TextField)fieldGroup.buildAndBind("Nome", "nome");				
					txtNome.setWidth("200px");				
					txtNome.setStyleName("caption-align-forma_pgto");
					txtNome.setNullRepresentation("");					
					txtNome.setRequired(true);
					txtNome.setMaxLength(200);
					txtNome.focus();
					
					addComponent(txtNome);
					setExpandRatio(txtNome, 2);						
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField txtDesconto = (TextField)fieldGroup.buildAndBind("Desconto(%)", "desc_max");				
					txtDesconto.setWidth("50px");				
					txtDesconto.setStyleName("caption-align-forma_pgto");
					txtDesconto.setNullRepresentation("");					
					txtDesconto.setRequired(true);
					txtDesconto.setMaxLength(200);
					txtDesconto.setId("txtDesconto");
					txtDesconto.addStyleName("align-currency");
					
					addComponent(txtDesconto);
					JavaScript.getCurrent().execute("$('#txtDesconto').maskMoney({decimal:',',thousands:'.'})");	
					
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField txtNParcelas = (TextField)fieldGroup.buildAndBind("Nº Máximo de parcelas", "n_parcelas");				
					txtNParcelas.setWidth("200px");				
					txtNParcelas.setStyleName("caption-align-forma_pgto");
					txtNParcelas.setNullRepresentation("");					
					txtNParcelas.setRequired(true);
					txtNParcelas.setMaxLength(200);
										
					addComponent(txtNParcelas);
					setExpandRatio(txtNParcelas, 2);						
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					TextField txtValorMinParc = (TextField)fieldGroup.buildAndBind("Valor mínimo da parcela (R$)", "valor_min_parc");				
					txtValorMinParc.setWidth("50px");				
					txtValorMinParc.setStyleName("caption-align-forma_pgto");
					txtValorMinParc.setNullRepresentation("");					
					txtValorMinParc.setRequired(true);
					txtValorMinParc.setMaxLength(200);
					txtValorMinParc.setId("txtVlrMinParc");
					txtValorMinParc.addStyleName("align-currency");
					
					addComponent(txtValorMinParc);
					JavaScript.getCurrent().execute("$('#txtVlrMinParc').maskMoney({decimal:',',thousands:'.'})");					
				}
		});	

		vlRoot.addComponent(
			new FormLayout(){					
			{					
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");						
								
				ComboBox cbTipoTitulo = new ComboBox("Tipo de Título");
				cbTipoTitulo.setStyleName("caption-align-forma_pgto");
				cbTipoTitulo.setNullSelectionAllowed(false);
				cbTipoTitulo.setRequired(true);
				cbTipoTitulo.addItem("NAO GERAR TITULO");
				cbTipoTitulo.addItem("BOLETO");
				cbTipoTitulo.addItem("TITULO");
				
				addComponent(cbTipoTitulo);
				fieldGroup.bind(cbTipoTitulo, "tipo_titulo");								
			}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					cbNecessarioCredito = new ComboBox("Necessário Crédito");
					cbNecessarioCredito.setNullSelectionAllowed(false);
					cbNecessarioCredito.setRequired(true);
					cbNecessarioCredito.addItem("SIM");
					cbNecessarioCredito.addItem("NÃO");
					cbNecessarioCredito.setStyleName("caption-align-forma_pgto");
					
					
					if(item.getItemProperty("aprovacao_credito").getValue() != null && 
							(boolean)item.getItemProperty("aprovacao_credito").getValue()){
						
						cbNecessarioCredito.select("SIM");
					}else{
						cbNecessarioCredito.select("NÃO");
					}
					
					addComponent(cbNecessarioCredito);
						
				}
		});
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && cbNecessarioCredito.isValid()){
					try {
						
						if(cbNecessarioCredito.getValue().toString().equals("SIM")){
							item.getItemProperty("aprovacao_credito").setValue(true);
						}else{
							item.getItemProperty("aprovacao_credito").setValue(false);
						}

						fieldGroup.commit();				
						fireEvent(new FormaPgtoEvent(getUI(), item, true));
						
					} catch (CommitException e) {
						Notification.show("Não foi Possivel Salvar a Forma de Pgto!");
						e.printStackTrace();
					}
				}
				
				close();
			}
		});
		
		
		btSalvar.setStyleName("default");
		ShortcutListener clTb = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER, null) {
			
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
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new FormaPgtoEvent(getUI(), item, false));
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
											fireEvent(new FormaPgtoEvent(getUI(), item, true));
											
											Notification.show("Forma de Pgto. foi Salva com Sucesso!");
											
											close();
										} catch (Exception e) {
											Notification.show("Não foi Possivel Salvar o Forma de Pgto. !");
										}
									}else{
										Notification.show("Não é Possivel Salvar os Dados Verifique os Campos Inválidos/Obrigatórios!");
									}
								}else{							
									fieldGroup.discard();				
									fireEvent(new FormaPgtoEvent(getUI(), item, false));
									close();													
								}
							
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
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
	
	
	public void addListerner(FormaPgtoListerner target){
		try {
			Method method = FormaPgtoListerner.class.getDeclaredMethod("onClose", FormaPgtoEvent.class);
			addListener(FormaPgtoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(FormaPgtoListerner target){
		removeListener(FormaPgtoEvent.class, target);
	}
	public static class FormaPgtoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public FormaPgtoEvent(Component source, Item item, boolean confirm) {
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
	public interface FormaPgtoListerner extends Serializable{
		public void onClose(FormaPgtoEvent event);
	}
	
}
