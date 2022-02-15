package com.digital.opuserp.view.modulos.configuracoes.config_financeiro.contrato_cobranca;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.vaadin.data.Item;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ContratoCobrancaEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
		
	public ContratoCobrancaEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("750px");
		
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
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
					
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtNome = new TextField ("Nome");				
					txtNome.setWidth("80px");				
					txtNome.setStyleName("caption-align-contrato-bancario");
					txtNome.setNullRepresentation("");
					txtNome.setRequired(true);					
					txtNome.focus();
					
					addComponent(txtNome);
					
					fieldGroup.bind(txtNome,"nome");
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtCodBanco = new TextField ("Código do Banco");				
					txtCodBanco.setWidth("80px");				
					txtCodBanco.setStyleName("caption-align-contrato-bancario");
					txtCodBanco.setNullRepresentation("");
					txtCodBanco.setRequired(true);					
					txtCodBanco.focus();
					
					addComponent(txtCodBanco);
					
					fieldGroup.bind(txtCodBanco,"cod_banco");
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtNomeBanco = new TextField ("Nome do Banco");				
					txtNomeBanco.setWidth("350px");				
					txtNomeBanco.setStyleName("caption-align-contrato-bancario");
					txtNomeBanco.setNullRepresentation("");
					txtNomeBanco.setRequired(true);					
										
					addComponent(txtNomeBanco);
					
					fieldGroup.bind(txtNomeBanco,"nome_banco");
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtAgencia = new TextField ("Número da Agência");				
					txtAgencia.setWidth("80px");				
					txtAgencia.setStyleName("caption-align-contrato-bancario");
					txtAgencia.setNullRepresentation("");
					txtAgencia.setRequired(true);					
										
					addComponent(txtAgencia);
					
					fieldGroup.bind(txtAgencia,"agencia_banco");
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtNumeroConta = new TextField ("Número da Conta");				
					txtNumeroConta.setWidth("160px");				
					txtNumeroConta.setStyleName("caption-align-contrato-bancario");
					txtNumeroConta.setNullRepresentation("");
					txtNumeroConta.setRequired(true);					
										
					addComponent(txtNumeroConta);
					
					fieldGroup.bind(txtNumeroConta,"n_conta");
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtConvenio = new TextField ("Convênio");				
					txtConvenio.setWidth("160px");				
					txtConvenio.setStyleName("caption-align-contrato-bancario");
					txtConvenio.setNullRepresentation("");
					txtConvenio.setRequired(true);					
										
					addComponent(txtConvenio);
					
					fieldGroup.bind(txtConvenio,"convenio");
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtContrato = new TextField ("Contrato");				
					txtContrato.setWidth("160px");				
					txtContrato.setStyleName("caption-align-contrato-bancario");
					txtContrato.setNullRepresentation("");
					txtContrato.setRequired(true);					
										
					addComponent(txtContrato);
					
					fieldGroup.bind(txtContrato,"contrato");
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtCarteira = new TextField ("Carteira");				
					txtCarteira.setWidth("80px");				
					txtCarteira.setStyleName("caption-align-contrato-bancario");
					txtCarteira.setNullRepresentation("");
					txtCarteira.setRequired(true);					
										
					addComponent(txtCarteira);
					
					fieldGroup.bind(txtCarteira,"carteira");
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtVariacao = new TextField ("Variação");				
					txtVariacao.setWidth("80px");				
					txtVariacao.setStyleName("caption-align-contrato-bancario");
					txtVariacao.setNullRepresentation("");
					txtVariacao.setRequired(true);					
										
					addComponent(txtVariacao);
					
					fieldGroup.bind(txtVariacao,"variacao_carteira");
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					ComboBox cbTipo = new ComboBox("Tipo");				
					cbTipo.setWidth("150px");				
					cbTipo.setStyleName("caption-align-contrato-bancario");
					cbTipo.setNullSelectionAllowed(false);					
					cbTipo.setRequired(true);	
					cbTipo.addItem("SEM REGISTRO");
					
					addComponent(cbTipo);
					
					fieldGroup.bind(cbTipo,"tipo");
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextField txtCpfCnpj = new TextField ("CPF/CNPJ");				
					txtCpfCnpj.setWidth("150px");				
					txtCpfCnpj.setStyleName("caption-align-contrato-bancario");
					txtCpfCnpj.setNullRepresentation("");
					txtCpfCnpj.setValue(OpusERP4UI.getEmpresa().getCnpj());
					txtCpfCnpj.setReadOnly(true);
										
					addComponent(txtCpfCnpj);					
					
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtEmpresa = new TextField ("Empresa");				
					txtEmpresa.setWidth("100%");				
					txtEmpresa.setStyleName("caption-align-contrato-bancario");
					txtEmpresa.setNullRepresentation("");
					txtEmpresa.setValue(OpusERP4UI.getEmpresa().getRazao_social());
					txtEmpresa.setReadOnly(true);
										
					addComponent(txtEmpresa);					
					
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtCedente = new TextField ("Cendente (Fantasia)");				
					txtCedente.setWidth("350px");				
					txtCedente.setStyleName("caption-align-contrato-bancario");
					txtCedente.setNullRepresentation("");
					txtCedente.setValue(OpusERP4UI.getEmpresa().getNome_fantasia());
					txtCedente.setReadOnly(true);
										
					addComponent(txtCedente);					
					
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtEndereco = new TextField ("Endereço");				
					txtEndereco.setWidth("100%");				
					txtEndereco.setStyleName("caption-align-contrato-bancario");
					txtEndereco.setNullRepresentation("");
					txtEndereco.setValue(OpusERP4UI.getEmpresa().getCep()+","+OpusERP4UI.getEmpresa().getEndereco()+","+OpusERP4UI.getEmpresa().getNumero()+","+OpusERP4UI.getEmpresa().getBairro()+" "+OpusERP4UI.getEmpresa().getCidade()+"-"+OpusERP4UI.getEmpresa().getUf());
					txtEndereco.setReadOnly(true);
										
					addComponent(txtEndereco);					
					
				}
		});
		
		
		vlRoot.addComponent(
				new HorizontalLayout(){					
					{
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
																	
								TextField txtMulta = new TextField ("Multa (%)");				
								txtMulta.setWidth("80px");				
								txtMulta.setStyleName("caption-align-contrato-bancario");
								txtMulta.setNullRepresentation("");
								txtMulta.setRequired(true);					
													
								addComponent(txtMulta);
								
								fieldGroup.bind(txtMulta,"multa");
							}
						});
						
						
						addComponent(new Label("após o vencimento"){
							{
								addStyleName("margin-top-10");
							}
						});	
						
						//addComponent(new CheckBox("Informar nas instruções"));
					}
		});
		
		vlRoot.addComponent(
				new HorizontalLayout(){					
					{
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																	
								TextField txtJuros = new TextField ("Juros (%)");				
								txtJuros.setWidth("80px");				
								txtJuros.setStyleName("caption-align-contrato-bancario");
								txtJuros.setNullRepresentation("");
								txtJuros.setRequired(true);					
													
								addComponent(txtJuros);
								
								fieldGroup.bind(txtJuros,"juros");
							}
						});
						
						
						addComponent(new Label("após o vencimento"){
							{
								//addStyleName("margin-top-10");
							}
						});	
						
						//addComponent(new CheckBox("Informar nas instruções"));
					}
		});
		
//		vlRoot.addComponent(
//				new HorizontalLayout(){					
//					{
//						addComponent(new FormLayout(){					
//							{
//								setStyleName("form-cutom");
//								setMargin(true);
//								setSpacing(true);
//																	
//								TextField txtTolerancia = new TextField ("Tolerância (dias)");				
//								txtTolerancia.setWidth("80px");				
//								txtTolerancia.setStyleName("caption-align-contrato-bancario");
//								txtTolerancia.setNullRepresentation("");
//								txtTolerancia.setRequired(true);					
//													
//								addComponent(txtTolerancia);
//								
//								fieldGroup.bind(txtTolerancia,"tolerancia");
//							}
//						});
//						
//						
//						addComponent(new Label("após o vencimento"){
//							{
//								//addStyleName("margin-top-10");
//							}
//						});	
//						
//						//addComponent(new CheckBox("Informar nas instruções"));
//					}
//		});
		
		vlRoot.addComponent(
				new HorizontalLayout(){					
					{
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																	
								TextField txtToleranciaJuros = new TextField ("Tolerância Jurso/Multa (dias)");				
								txtToleranciaJuros.setWidth("80px");				
								txtToleranciaJuros.setStyleName("caption-align-contrato-bancario");
								txtToleranciaJuros.setNullRepresentation("");
								txtToleranciaJuros.setRequired(true);					
													
								addComponent(txtToleranciaJuros);
								
								fieldGroup.bind(txtToleranciaJuros,"tolerancia_juros_multa");
							}
						});
						
						
						addComponent(new Label("após o vencimento"){
							{
								//addStyleName("margin-top-10");
							}
						});	
						
						//addComponent(new CheckBox("Informar nas instruções"));
					}
		});
		
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextField txtInstrucoes = new TextField ("Instruções");				
					txtInstrucoes.setWidth("100%");				
					txtInstrucoes.setStyleName("caption-align-contrato-bancario");
					txtInstrucoes.setNullRepresentation("");
					txtInstrucoes.setRequired(false);					
										
					addComponent(txtInstrucoes);
					
					fieldGroup.bind(txtInstrucoes,"instrucoes1");
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtInstrucoes = new TextField ("Instruções");				
					txtInstrucoes.setWidth("100%");				
					txtInstrucoes.setStyleName("caption-align-contrato-bancario");
					txtInstrucoes.setNullRepresentation("");
					txtInstrucoes.setRequired(false);					
										
					addComponent(txtInstrucoes);
					
					fieldGroup.bind(txtInstrucoes,"instrucoes2");
				}
		});

		
		
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid()){
					try {				
						
						fieldGroup.commit();				
						fireEvent(new ContratoCobrancaEvent(getUI(), item, true));					
						Notification.show("Dados Salvos com Sucesso!");
						
						close();
					} catch (Exception e) {					
						//LogErrorDAO.add(new LogError(null, "CategoriaEditor.java", "buildBtSalvar()", e.toString(),null,OpusERP4UI.EMPRESA_SELECIONADA, OpusERP4UI.USUARIO_LOGADO));					
						Notification.show("Não foi Possivel Salvar as Alterações!");
					}
				}else{
					
					for (Field<?> field: fieldGroup.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
					}
					
					Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
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
					fireEvent(new ContratoCobrancaEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								try {
									fieldGroup.commit();				
									fireEvent(new ContratoCobrancaEvent(getUI(), item, true));									
									if(item.getItemProperty("id").getValue() == null){
										
										Notification.show("Categoria Cadastrado com Sucesso!");
									}else{								
										Notification.show("Categoria Alterado com Sucesso!");
									}
									close();
								} catch (Exception e) {
									System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
									Notification.show("Não foi Possivel Salvar as Alterações!");
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new TransportadoraEvent(getUI(), item, false));
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
	
	
	public void addListerner(ContratoCobrancaListerner target){
		try {
			Method method = ContratoCobrancaListerner.class.getDeclaredMethod("onClose", ContratoCobrancaEvent.class);
			addListener(ContratoCobrancaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ContratoCobrancaListerner target){
		removeListener(ContratoCobrancaEvent.class, target);
	}
	public static class ContratoCobrancaEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public ContratoCobrancaEvent(Component source, Item item, boolean confirm) {
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
	public interface ContratoCobrancaListerner extends Serializable{
		public void onClose(ContratoCobrancaEvent event);
	}

	
}
