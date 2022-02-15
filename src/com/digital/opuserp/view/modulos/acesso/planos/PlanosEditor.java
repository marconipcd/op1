package com.digital.opuserp.view.modulos.acesso.planos;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PlanoAcessoDAO;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PlanosEditor extends Window implements GenericEditor {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	boolean rateValid;
	boolean sTimeOutValid;
	boolean kTimeOutValid;
	boolean iTimeOutValid;
	boolean validarNone;


	ComboBox cb;
	ComboBox cbStatus;
	ComboBox cbPermitirInstalacaoGratis;
	ComboBox cbPermitirComodatoTotal;
	ComboBox cbPermitirServicoManutencao;
	
	
	String planoAtual;
	
	JPAContainer<PlanoAcesso> container_plano;
	public PlanosEditor(Item item, String title, boolean modal){
		this.item = item;
		
		if(item.getItemProperty("nome").getValue() != null){
			planoAtual = item.getItemProperty("nome").getValue().toString();
		}
		//configLayout();	
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.addStyleName("border-form");
		
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
		
		if (item.getItemProperty("id").getValue() != null) {
			rateValid = true;
			sTimeOutValid = true;
			kTimeOutValid = true;
			iTimeOutValid = true;
			validarNone = true;
		} else {
			rateValid = false;
			sTimeOutValid  = false;
			kTimeOutValid  = false;
			iTimeOutValid  = false;
			validarNone = false;
		}
		
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
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					addStyleName("form-cutom");						
								
					JPAContainer<ContratosAcesso> container = JPAContainerFactory.make(ContratosAcesso.class, ConnUtil.getEntity());
					container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
					
					cb = new ComboBox("Contrato", container);
					cb.setNullSelectionAllowed(false);
					cb.setItemCaptionPropertyId("nome");
					cb.setWidth("250px");	;
					cb.setRequired(true);
					cb.addStyleName("caption-align-planos");
					cb.setConverter(new SingleSelectConverter(cb));
					cb.focus();
					cb.setImmediate(true);
					cb.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(container_plano != null){
								container_plano.removeAllContainerFilters();
								container_plano.addContainerFilter(Filters.eq("status", "ATIVO"));
								EntityItem<ContratosAcesso> eiContratos= (EntityItem<ContratosAcesso>)cb.getItem(cb.getValue());
								container_plano.addContainerFilter(Filters.eq("contrato_acesso", eiContratos.getEntity()));
							}
							
							if(((TextField) fieldGroup.getField("nome")) != null){
								String nomePlano = ((TextField) fieldGroup.getField("nome")).getValue();
								
								
								if(planoAtual != null){
									if (cb.getValue() != null && planoAtual != null && !nomePlano.equals(planoAtual)) {
										
										PlanoAcessoDAO pDAO = new PlanoAcessoDAO();						
										 if(pDAO.checkPlanoExist(nomePlano.toUpperCase(),
												 (Integer)cb.getItem(cb.getValue()).getItemProperty("id").getValue())== true){
											 
											 ((TextField) fieldGroup.getField("nome")).removeStyleName("valid-cpf");
											 ((TextField) fieldGroup.getField("nome")).addStyleName("invalid-cpf");
											 validarNone = false;
										 }else{
											 ((TextField) fieldGroup.getField("nome")).removeStyleName("invalid-cpf");
											 ((TextField) fieldGroup.getField("nome")).addStyleName("valid-cpf");
											 validarNone = true;							 
										 }	
									}else{
										
										 if(planoAtual != null && nomePlano.equals(planoAtual)){
											 ((TextField) fieldGroup.getField("nome")).removeStyleName("invalid-cpf");
											 ((TextField) fieldGroup.getField("nome")).addStyleName("valid-cpf");
											 validarNone = true;										 
										 }else{								
											 ((TextField) fieldGroup.getField("nome")).removeStyleName("valid-cpf");
											 ((TextField) fieldGroup.getField("nome")).addStyleName("invalid-cpf");
											 validarNone = false;
										 }
									}
								}else{
									
										if(fieldGroup.getField("nome") != null && fieldGroup.getField("nome").getValue() != null && cb.getValue() != null){
											//String nomePlano2 = 
											PlanoAcessoDAO pDAO = new PlanoAcessoDAO();			
											if(pDAO.checkPlanoExist(nomePlano.toUpperCase(),
													 (Integer)cb.getItem(cb.getValue()).getItemProperty("id").getValue())== true){
												 
												 ((TextField) fieldGroup.getField("nome")).removeStyleName("valid-cpf");
												 ((TextField) fieldGroup.getField("nome")).addStyleName("invalid-cpf");
												 validarNone = false;
											 }else{
												 ((TextField) fieldGroup.getField("nome")).removeStyleName("invalid-cpf");
												 ((TextField) fieldGroup.getField("nome")).addStyleName("valid-cpf");
												 validarNone = true;							 
											 }	
										}else{
												 ((TextField) fieldGroup.getField("nome")).removeStyleName("valid-cpf");
												 ((TextField) fieldGroup.getField("nome")).addStyleName("invalid-cpf");
												 validarNone = false;
												 
										}
											
									
								}
							
							}
						}
					});
															
					addComponent(cb);
										
					fieldGroup.bind(cb, "contrato_acesso");						
				}
			});
		vlRoot.addComponent(new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					addStyleName("form-cutom");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Nome", "nome");				
					tfCod.setWidth("200px");				
					tfCod.addStyleName("caption-align-planos");
					tfCod.setNullRepresentation("");					
					tfCod.setRequired(true);
					tfCod.setMaxLength(200);
					
					tfCod.addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {						
							
							if(planoAtual != null){
						
							if (cb.getValue() != null && planoAtual != null && !event.getText().equals(planoAtual)) {
								
								PlanoAcessoDAO pDAO = new PlanoAcessoDAO();						
								 if(pDAO.checkPlanoExist(event.getText().toUpperCase(),
										 (Integer)cb.getItem(cb.getValue()).getItemProperty("id").getValue())== true){
									 
									 event.getComponent().removeStyleName("valid-cpf");
									 event.getComponent().addStyleName("invalid-cpf");
									 validarNone = false;
								 }else{
									 event.getComponent().removeStyleName("invalid-cpf");
									 event.getComponent().addStyleName("valid-cpf");
									 validarNone = true;							 
								 }	
							}else{
								
								 if(event.getText().equals(planoAtual)){
									 event.getComponent().removeStyleName("invalid-cpf");
									 event.getComponent().addStyleName("valid-cpf");
									 validarNone = true;										 
								 }else{								
									 event.getComponent().removeStyleName("valid-cpf");
									 event.getComponent().addStyleName("invalid-cpf");
									 validarNone = false;
								 }
							}
							}else{
								
								if(cb.getValue() != null){
									PlanoAcessoDAO pDAO = new PlanoAcessoDAO();						
									 if(pDAO.checkPlanoExist(event.getText().toUpperCase(),
											 (Integer)cb.getItem(cb.getValue()).getItemProperty("id").getValue())== true){
										 
										 event.getComponent().removeStyleName("valid-cpf");
										 event.getComponent().addStyleName("invalid-cpf");
										 validarNone = false;
									 }else{
										 event.getComponent().removeStyleName("invalid-cpf");
										 event.getComponent().addStyleName("valid-cpf");
										 validarNone = true;							 
									 }	
								}else{
									 event.getComponent().removeStyleName("valid-cpf");
									 event.getComponent().addStyleName("invalid-cpf");
									 validarNone = false;
								}
							}
						}
					});
					
					tfCod.addBlurListener(new BlurListener() {
						
						public void blur(BlurEvent event) {
							
							if(((TextField)(event.getComponent())) != null && ((TextField)(event.getComponent())).getValue() != null && !((TextField)(event.getComponent())).getValue().toString().isEmpty()){
								
								((TextField)(event.getComponent())).setValue(((TextField)event.getComponent()).getValue().toUpperCase());
								
								if(validarNone == false){									
									Notify.Show("Nome Já Cadastrado", Notify.TYPE_ERROR);
								}						
							}
							
							
						}
					});
					
					addComponent(tfCod); 
					setExpandRatio(tfCod, 2);						
				}
			});
		vlRoot.addComponent(new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					addStyleName("form-cutom");						
									
					TextField txtMaxDownloadUpload = (TextField)fieldGroup.buildAndBind("Max. Upload/Download", "rate_limit");				
					txtMaxDownloadUpload.setWidth("130px");				
					txtMaxDownloadUpload.addStyleName("caption-align-planos");
					txtMaxDownloadUpload.setNullRepresentation("");					
					txtMaxDownloadUpload.setRequired(true);
					txtMaxDownloadUpload.setMaxLength(200);				
					txtMaxDownloadUpload.addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
						
							 Pattern pattern = Pattern.compile("\\d{1,4}[k|M]{1,1}/\\d{1,4}[k|M]{1,1}");
						       Matcher matcher = pattern.matcher(event.getText());
						 
							 if(matcher.matches()){
								 event.getComponent().removeStyleName("invalid-cpf");
								 event.getComponent().addStyleName("valid-cpf");
								 rateValid = true;
							 }else{
								 event.getComponent().removeStyleName("valid-cpf");
								 event.getComponent().addStyleName("invalid-cpf");
								 rateValid = false;
							 }
						}
					});

					
					addComponent(txtMaxDownloadUpload);
					setExpandRatio(txtMaxDownloadUpload, 2);						
				}
			});	
		vlRoot.addComponent(new FormLayout(){					
			{
				
				setMargin(true);
				setSpacing(true);
				addStyleName("form-cutom");						
								
				TextField txtMinDownloadUpload = (TextField)fieldGroup.buildAndBind("Min. Upload/Download", "min_rate_limit");				
				txtMinDownloadUpload.setWidth("130px");				
				txtMinDownloadUpload.addStyleName("caption-align-planos");
				txtMinDownloadUpload.setNullRepresentation("");					
				txtMinDownloadUpload.setRequired(true);
				txtMinDownloadUpload.setMaxLength(200);				
				txtMinDownloadUpload.addListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
					
						 Pattern pattern = Pattern.compile("\\d{1,4}[k|M]{1,1}/\\d{1,4}[k|M]{1,1}");
					       Matcher matcher = pattern.matcher(event.getText());
					 
						 if(matcher.matches()){
							 event.getComponent().removeStyleName("invalid-cpf");
							 event.getComponent().addStyleName("valid-cpf");
							 rateValid = true;
						 }else{
							 event.getComponent().removeStyleName("valid-cpf");
							 event.getComponent().addStyleName("invalid-cpf");
							 rateValid = false;
						 }
					}
				});

				
				addComponent(txtMinDownloadUpload);
				setExpandRatio(txtMinDownloadUpload, 2);						
			}
		});	
		vlRoot.addComponent(new FormLayout(){					
			{
				
				setMargin(true);
				setSpacing(true);
				addStyleName("form-cutom");						
								
				TextField txtPrioridade = (TextField)fieldGroup.buildAndBind("Prioridade", "prioridade");				
				txtPrioridade.setWidth("80px");				
				txtPrioridade.addStyleName("caption-align-planos");
				txtPrioridade.setNullRepresentation("");					
				txtPrioridade.setRequired(true);
				txtPrioridade.setMaxLength(1);
				txtPrioridade.addListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
					
						 Pattern pattern = Pattern.compile("\\d*");
					       Matcher matcher = pattern.matcher(event.getText());
					 
						 if(matcher.matches()){
							 event.getComponent().removeStyleName("invalid-cpf");
							 event.getComponent().addStyleName("valid-cpf");
							  sTimeOutValid = true;
						 }else{
							 event.getComponent().removeStyleName("valid-cpf");
							 event.getComponent().addStyleName("invalid-cpf");
							 sTimeOutValid = false;
						 }
					}
				});
				
				addComponent(txtPrioridade);
				setExpandRatio(txtPrioridade, 2);						
			}
		});
		vlRoot.addComponent(new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					addStyleName("form-cutom");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Session Timeout", "session_timeout");				
					tfCod.setWidth("80px");				
					tfCod.addStyleName("caption-align-planos");
					tfCod.setNullRepresentation("");					
					tfCod.setRequired(true);
					tfCod.setMaxLength(200);
					tfCod.addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
						
							 Pattern pattern = Pattern.compile("\\d*");
						       Matcher matcher = pattern.matcher(event.getText());
						 
							 if(matcher.matches()){
								 event.getComponent().removeStyleName("invalid-cpf");
								 event.getComponent().addStyleName("valid-cpf");
								  sTimeOutValid = true;
							 }else{
								 event.getComponent().removeStyleName("valid-cpf");
								 event.getComponent().addStyleName("invalid-cpf");
								 sTimeOutValid = false;
							 }
						}
					});
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});
		vlRoot.addComponent(new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					addStyleName("form-cutom");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("KeepAlive Timeout", "keepalive_timeout");				
					tfCod.setWidth("80px");				
					tfCod.addStyleName("caption-align-planos");
					tfCod.setNullRepresentation("");					
					tfCod.setRequired(true);
					tfCod.setMaxLength(200);
					tfCod.addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
						
							 Pattern pattern = Pattern.compile("\\d*");
						       Matcher matcher = pattern.matcher(event.getText());
						 
							 if(matcher.matches()){
								 event.getComponent().removeStyleName("invalid-cpf");
								 event.getComponent().addStyleName("valid-cpf");
								  kTimeOutValid = true;
							 }else{
								 event.getComponent().removeStyleName("valid-cpf");
								 event.getComponent().addStyleName("invalid-cpf");
								 kTimeOutValid = false;
							 }
						}
					});
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});
		vlRoot.addComponent(new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					addStyleName("form-cutom");						
									
					TextField tfCod = (TextField)fieldGroup.buildAndBind("Idle Timeout", "idle_timeout");				
					tfCod.setWidth("80px");				
					tfCod.addStyleName("caption-align-planos");
					tfCod.setNullRepresentation("");					
					tfCod.setRequired(true);
					tfCod.setMaxLength(200);
					tfCod.addListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
						
							 Pattern pattern = Pattern.compile("\\d*");
						       Matcher matcher = pattern.matcher(event.getText());
						 
							 if(matcher.matches()){
								 event.getComponent().removeStyleName("invalid-cpf");
								 event.getComponent().addStyleName("valid-cpf");
								  iTimeOutValid = true;
							 }else{
								 event.getComponent().removeStyleName("valid-cpf");
								 event.getComponent().addStyleName("invalid-cpf");
								 iTimeOutValid = false;
							 }
						}
					});
					
					addComponent(tfCod);
					setExpandRatio(tfCod, 2);						
				}
			});
		vlRoot.addComponent(new FormLayout(){					
				{				
					setMargin(true);
					setSpacing(true);
					addStyleName("form-cutom");						
									
					TextField tfCensura = new TextField("Censura");				
					tfCensura.setWidth("80px");				
					tfCensura.addStyleName("caption-align-planos");
					tfCensura.setNullRepresentation("");					
					tfCensura.setRequired(true);
					tfCensura.setMaxLength(200);
					addComponent(tfCensura);
					setExpandRatio(tfCensura, 2);
					
					fieldGroup.bind(tfCensura, "qtd_censura");
					
					
				}
			});
		vlRoot.addComponent(new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					addStyleName("form-cutom-new");						
									
					TextField txtValor = new TextField("Valor (R$)");				
					txtValor.setWidth("80px");				
					txtValor.addStyleName("caption-align-planos");
					txtValor.setNullRepresentation("");					
					txtValor.setRequired(true);
					txtValor.setMaxLength(200);
					txtValor.setId("planoValor");
					txtValor.addStyleName("align-currency");
					addComponent(txtValor);
					setExpandRatio(txtValor, 2);
					
					fieldGroup.bind(txtValor, "valor");
					
					JavaScript.getCurrent().execute("$('#planoValor').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		vlRoot.addComponent(new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					addStyleName("form-cutom");						
									
					TextField txtDesconto = new TextField("Desconto (R$)");				
					txtDesconto.setWidth("67px");				
					txtDesconto.addStyleName("caption-align-planos");
					txtDesconto.setNullRepresentation("");					
					txtDesconto.setRequired(true);
					txtDesconto.setMaxLength(200);
					txtDesconto.setId("vlrDesconto");
					txtDesconto.addStyleName("align-currency");
					addComponent(txtDesconto);
										
					fieldGroup.bind(txtDesconto, "desconto");
					
					JavaScript.getCurrent().execute("$('#vlrDesconto').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		vlRoot.addComponent(new FormLayout(){					
					{						
						setMargin(true);
						setSpacing(true);
						addStyleName("form-cutom");						
						
						ComboBox cbTaxaBoleto = new ComboBox("Taxa de Boleto");							
						cbTaxaBoleto.addStyleName("caption-align-planos");				
						cbTaxaBoleto.addStyleName("align-currency");
						cbTaxaBoleto.setNullSelectionAllowed(false);
						cbTaxaBoleto.setRequired(true);
						cbTaxaBoleto.addItem("SIM");
						cbTaxaBoleto.addItem("NAO");
						
						addComponent(cbTaxaBoleto);
												
						fieldGroup.bind(cbTaxaBoleto, "taxa_boleto");						
					}
		});
		vlRoot.addComponent(	new FormLayout(){					
					{						
						setMargin(true);
						setSpacing(true);
						addStyleName("form-cutom");				
						
						container_plano = JPAContainerFactory.makeBatchable(PlanoAcesso.class, ConnUtil.getEntity());
						
						
						if(cb.getValue() != null){
							container_plano.removeAllContainerFilters();
							container_plano.addContainerFilter(Filters.eq("status", "ATIVO"));
							EntityItem<ContratosAcesso> eiContratos= (EntityItem<ContratosAcesso>)cb.getItem(cb.getValue());
							container_plano.addContainerFilter(Filters.eq("contrato_acesso", eiContratos.getEntity()));
						}else{
							container_plano.addContainerFilter(Filters.eq("status", "ATIVO2"));
						}
						
						ComboBox cbPlanoRenovacao = new ComboBox("Plano de Renovação",container_plano);							
						cbPlanoRenovacao.addStyleName("caption-align-planos");				
						cbPlanoRenovacao.addStyleName("align-currency");
						cbPlanoRenovacao.setNullSelectionAllowed(true);						
						cbPlanoRenovacao.setItemCaptionPropertyId("nome");
						cbPlanoRenovacao.setConverter(new SingleSelectConverter(cbPlanoRenovacao));
												
						addComponent(cbPlanoRenovacao);
												
						fieldGroup.bind(cbPlanoRenovacao, "plano_Renovacao");						
					}
		});
		vlRoot.addComponent(	new FormLayout(){					
					{
						
						setMargin(true);
						setSpacing(true);
						addStyleName("form-cutom-new");						
						
						cbPermitirInstalacaoGratis = new ComboBox("Permitir Instalação Grátis");							
						cbPermitirInstalacaoGratis.addStyleName("caption-align-planos");				
						cbPermitirInstalacaoGratis.addStyleName("align-currency");
						cbPermitirInstalacaoGratis.setNullSelectionAllowed(false);
						cbPermitirInstalacaoGratis.setRequired(true);
						cbPermitirInstalacaoGratis.addItem("SIM");
						cbPermitirInstalacaoGratis.addItem("NAO");
						cbPermitirInstalacaoGratis.select("NAO");
						addComponent(cbPermitirInstalacaoGratis);
						setExpandRatio(cbPermitirInstalacaoGratis, 2);
						
						fieldGroup.bind(cbPermitirInstalacaoGratis, "instalacao_gratis");
						
					}
				});
		vlRoot.addComponent(	new FormLayout(){					
					{
						
						setMargin(true);
						setSpacing(true);
						addStyleName("form-cutom-new");						
						
						cbPermitirComodatoTotal = new ComboBox("Permitir Comodato Total");							
						cbPermitirComodatoTotal.addStyleName("caption-align-planos");				
						cbPermitirComodatoTotal.addStyleName("align-currency");
						cbPermitirComodatoTotal.setNullSelectionAllowed(false);
						cbPermitirComodatoTotal.setRequired(true);
						cbPermitirComodatoTotal.addItem("SIM");
						cbPermitirComodatoTotal.addItem("NAO");
						cbPermitirComodatoTotal.select("NAO");
						addComponent(cbPermitirComodatoTotal);
						setExpandRatio(cbPermitirComodatoTotal, 2);
						
						fieldGroup.bind(cbPermitirComodatoTotal, "permitir_comodato_total");
						
					}
		});
		
		
		vlRoot.addComponent(	new FormLayout(){					
			{				
				setMargin(true);
				setSpacing(true);
				addStyleName("form-cutom-new");						
				
				cbPermitirServicoManutencao = new ComboBox("Permitir Serv. Manut.");							
				cbPermitirServicoManutencao.addStyleName("caption-align-planos");				
				cbPermitirServicoManutencao.addStyleName("align-currency");
				cbPermitirServicoManutencao.setNullSelectionAllowed(false);
				cbPermitirServicoManutencao.setRequired(true);
				cbPermitirServicoManutencao.addItem("SIM");
				cbPermitirServicoManutencao.addItem("NAO");
				cbPermitirServicoManutencao.select("NAO");
				addComponent(cbPermitirServicoManutencao);
				setExpandRatio(cbPermitirServicoManutencao, 2);
				
				fieldGroup.bind(cbPermitirServicoManutencao, "permitir_servico_manutencao");				
			}
		});
		
		vlRoot.addComponent(	new FormLayout(){					
			{
				
				setMargin(true);
				setSpacing(true);
				addStyleName("form-cutom-new");						
				
				ComboBox cbPossuiIttv = new ComboBox("Possui ITTV");							
				cbPossuiIttv.addStyleName("caption-align-planos");				
				cbPossuiIttv.addStyleName("align-currency");
				cbPossuiIttv.setNullSelectionAllowed(false);
				cbPossuiIttv.setRequired(true);
				cbPossuiIttv.addItem("SIM");
				cbPossuiIttv.addItem("NAO");				
				addComponent(cbPossuiIttv);
				setExpandRatio(cbPossuiIttv, 2);
				
				fieldGroup.bind(cbPossuiIttv, "possui_ittv");
				
			}
		});
		vlRoot.addComponent(	new FormLayout(){					
			{
				
				setMargin(true);
				setSpacing(true);
				addStyleName("form-cutom");						
				
				TextField idPlanoIttv = new TextField("Plano ITTV");							
				idPlanoIttv.addStyleName("caption-align-planos");				
				idPlanoIttv.addStyleName("align-currency");				
				idPlanoIttv.setRequired(true);					
				addComponent(idPlanoIttv);
				setExpandRatio(idPlanoIttv, 2);
				
				fieldGroup.bind(idPlanoIttv, "plano_ittv");
				
			}
		});
		vlRoot.addComponent(	new FormLayout(){					
					{
						
						setMargin(true);
						setSpacing(true);
						addStyleName("form-cutom-new");						
						
						cbStatus = new ComboBox("Status");							
						cbStatus.addStyleName("caption-align-planos");				
						cbStatus.addStyleName("align-currency");
						cbStatus.setNullSelectionAllowed(false);
						cbStatus.setRequired(true);
						cbStatus.addItem("ATIVO");
						cbStatus.addItem("INATIVO");
						cbStatus.select("ATIVO");
						addComponent(cbStatus);
						setExpandRatio(cbStatus, 2);
						
						fieldGroup.bind(cbStatus, "status");
						
					}
				});
		
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && rateValid == true && sTimeOutValid == true && kTimeOutValid == true && iTimeOutValid == true && validarNone == true){
					try {
						//item.getItemProperty("contrato_acesso").setValue(new ContratosAcesso(Integer.parseInt(
						//		cb.getItem(cb.getValue()).getItemProperty("id").getValue().toString())));
						fieldGroup.commit();				
						fireEvent(new PlanoEvent(getUI(), item, true));
						
						if (item.getItemProperty("id").getValue() != null) {					
							Notify.Show("Plano Alterado com Sucesso!", Notify.TYPE_SUCCESS);
						}
						
						close();
					} catch (CommitException e) {
						Notify.Show("Não foi Possivel Salvar o Plano!", Notify.TYPE_ERROR);
						e.printStackTrace();
					}
				}else{					
					Notify.Show_Invalid_Submit_Form();
				}
				
			}
		});
		
		ShortcutListener clTb = new ShortcutListener("Salvar", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);
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
					fireEvent(new PlanoEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									if(fieldGroup.isValid() && rateValid == true && sTimeOutValid == true && kTimeOutValid == true && iTimeOutValid == true && validarNone == true){
										try {
											fieldGroup.commit();				
											fireEvent(new PlanoEvent(getUI(), item, true));
											
											if (item.getItemProperty("id").getValue() != null) {														
												Notify.Show("Plano Alterado com Sucesso!", Notify.TYPE_SUCCESS);
											}
											
											close();
										} catch (Exception e) {
											Notify.Show("Não foi Possivel Salvar o Plano!", Notify.TYPE_ERROR);
										}
									}else{
										Notify.Show_Invalid_Submit_Form();
									}
								}else{							
									fieldGroup.discard();				
									fireEvent(new PlanoEvent(getUI(), item, false));
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
	
	
	public void addListerner(PlanoListerner target){
		try {
			Method method = PlanoListerner.class.getDeclaredMethod("onClose", PlanoEvent.class);
			addListener(PlanoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(PlanoListerner target){
		removeListener(PlanoEvent.class, target);
	}
	public static class PlanoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public PlanoEvent(Component source, Item item, boolean confirm) {
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
	public interface PlanoListerner extends Serializable{
		public void onClose(PlanoEvent event);
	}
	
}
