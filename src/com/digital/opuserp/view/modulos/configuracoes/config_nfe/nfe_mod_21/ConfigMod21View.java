package com.digital.opuserp.view.modulos.configuracoes.config_nfe.nfe_mod_21;

import org.vaadin.openesignforms.ckeditor.CKEditorConfig;
import org.vaadin.openesignforms.ckeditor.CKEditorTextField;
import org.vaadin.openesignforms.ckeditor.widgetset.client.ui.CKEditor;

import com.digital.opuserp.dao.NfeDAO;
import com.digital.opuserp.domain.ConfigCentralAssinante;
import com.digital.opuserp.domain.ConfigNfe;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ConfigMod21View extends VerticalLayout {

	
	private ConfigNfe valueDefaultNfe;
	public ConfigMod21View(){		
		
		valueDefaultNfe = NfeDAO.getDefaultValue();	
		
		final TextField txtVlrAproxTributos = new TextField("Valor aproximado de tributos(%)");
		txtVlrAproxTributos.setRequired(true);
		txtVlrAproxTributos.addStyleName("caption-align-config-nfe");
		txtVlrAproxTributos.addStyleName("align-currency");
		txtVlrAproxTributos.setId("txtVlrAproxTributos");
		txtVlrAproxTributos.focus();
		
		JavaScript.getCurrent().execute("$('#txtVlrAproxTributos').maskMoney({decimal:',',thousands:'.'})");
		
		
		final TextArea txtObservacao = new TextArea("Observação");
		txtObservacao.setRequired(true);
		txtObservacao.addStyleName("caption-align-config-nfe");
		txtObservacao.setWidth("470px");
		txtObservacao.setHeight("150px");
						
		final TextField txtServer = new TextField("Server");
		txtServer.setRequired(true);
		txtServer.addStyleName("caption-align-config-nfe");
		
		final TextField txtPorta = new TextField("Porta");
		txtPorta.setRequired(true);
		txtPorta.addStyleName("caption-align-config-nfe");
		txtPorta.setWidth("55px");
		
		final TextField txtLogin = new TextField("Login");
		txtLogin.setRequired(true);
		txtLogin.addStyleName("caption-align-config-nfe");
		txtLogin.setWidth("287px");
		
		final TextField txtSenha = new TextField("Senha");
		txtSenha.setRequired(true);
		txtSenha.addStyleName("caption-align-config-nfe");
		txtSenha.setWidth("287px");
		
		
		final TextField txtCopiaOculta = new TextField("Copia oculta");
		txtCopiaOculta.setRequired(true);
		txtCopiaOculta.addStyleName("caption-align-config-nfe");
		txtCopiaOculta.setWidth("287px");
		
		CKEditorConfig config = new CKEditorConfig();
        config.useCompactTags();
        config.disableElementsPath();
        config.setResizeDir(CKEditorConfig.RESIZE_DIR.HORIZONTAL);
        config.disableSpellChecker();
        config.setToolbarCanCollapse(false);
        config.setWidth("740px");
        config.setHeight("100%");
        config.addToRemovePlugins("insert");
        config.addToRemovePlugins("forms");
        config.addToRemovePlugins("links");
                
        final CKEditorTextField txtMensagemEmail = new CKEditorTextField(config);
        txtMensagemEmail.setCaption("Mensagem");
        txtMensagemEmail.addStyleName("caption-align-config-nfe");
        txtMensagemEmail.setHeight("400px");
		
		
		

		if(valueDefaultNfe != null){
			txtVlrAproxTributos.setValue(Real.formatDbToString(String.valueOf(valueDefaultNfe.getValor_aprox_trib())));
			txtObservacao.setValue(valueDefaultNfe.getObservacao());
			//txtChave.setValue(valueDefaultNfe.getChave());
			txtServer.setValue(valueDefaultNfe.getServer_email());
			txtPorta.setValue(valueDefaultNfe.getPorta_email());
			txtLogin.setValue(valueDefaultNfe.getLogin());
			txtSenha.setValue(valueDefaultNfe.getSenha());
			txtCopiaOculta.setValue(valueDefaultNfe.getEmail_copia_oculta());
			txtMensagemEmail.setValue(valueDefaultNfe.getMsg_email());
		}
						
		VerticalLayout vl1 = new VerticalLayout();
		vl1.addComponent(new Label("Valores padrão",ContentMode.HTML));
		vl1.addStyleName("vl1_central_auth");
		vl1.setWidth("966px");
		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						addComponent(txtVlrAproxTributos); 
					}
				});
			}
		});
		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(txtObservacao); 
					}
				});
			}
		});
						
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(txtServer); 
					}
				});
			}
		});
		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(txtPorta); 
					}
				});
			}
		});
		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(txtLogin); 
					}
				});
			}
		});
		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(txtSenha); 
					}
				});
			}
		});
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(txtCopiaOculta); 
					}
				});
			}
		});
		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(txtMensagemEmail); 
					}
				});
			}
		});
		
		
		
		
		Button bt1 = new Button("Salvar");
		bt1.addStyleName("default");
		bt1.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(valueDefaultNfe != null){
					
					if(txtVlrAproxTributos.isValid() && txtObservacao.isValid()){

						valueDefaultNfe.setValor_aprox_trib(Real.formatStringToDBDouble(txtVlrAproxTributos.getValue()));			
						valueDefaultNfe.setObservacao(txtObservacao.getValue());
						//valueDefaultNfe.setChave(txtChave.getValue());
						valueDefaultNfe.setServer_email(txtServer.getValue());
						valueDefaultNfe.setPorta_email(txtPorta.getValue());
						valueDefaultNfe.setLogin(txtLogin.getValue());
						valueDefaultNfe.setSenha(txtSenha.getValue());
						valueDefaultNfe.setEmail_copia_oculta(txtCopiaOculta.getValue());
						valueDefaultNfe.setMsg_email(txtMensagemEmail.getValue());
						
						NfeDAO.saveValueDefault(valueDefaultNfe); 
						Notify.Show("Alterações Salvas com Sucesso!", Notify.TYPE_SUCCESS);
					}else{
						Notify.Show_Invalid_Submit_Form();
					}
				}else{
					if(txtVlrAproxTributos.isValid() && txtObservacao.isValid()){
						
						valueDefaultNfe = new ConfigNfe();
						valueDefaultNfe.setValor_aprox_trib(Real.formatStringToDBDouble(txtVlrAproxTributos.getValue()));			
						valueDefaultNfe.setObservacao(txtObservacao.getValue());
						
						NfeDAO.saveValueDefault(valueDefaultNfe); 
						Notify.Show("Alterações Salvas com Sucesso!", Notify.TYPE_SUCCESS);
						Notify.Show("Alterações Salvas com Sucesso!", Notify.TYPE_SUCCESS);
					}else{
						Notify.Show_Invalid_Submit_Form();
					}
				}
			}
		});
		
		
		//HorizontalLayout hl1 = new HorizontalLayout();
		vl1.addComponent(bt1);
		vl1.setComponentAlignment(bt1, com.vaadin.ui.Alignment.MIDDLE_RIGHT);
		
		
		addComponent(vl1); 
		
	}
		
}
