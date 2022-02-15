package com.digital.opuserp.view.modulos.configuracoes.config_nfe.nfe_mod_55;

import com.digital.opuserp.dao.NfeDAO55;
import com.digital.opuserp.domain.ConfigNfeMod55;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class ConfigMod55View extends VerticalLayout {

	
	private ConfigNfeMod55 valueDefaultNfe;
	public ConfigMod55View(){		
		
		valueDefaultNfe = NfeDAO55.getDefaultValue();	
		
		final ComboBox cbTipoDoc = new ComboBox("Tipo do documento");
		cbTipoDoc.setRequired(true);
		cbTipoDoc.setNullSelectionAllowed(false);
		cbTipoDoc.setTextInputAllowed(false);
		cbTipoDoc.addItem("0 - Entrada");
		cbTipoDoc.addItem("1 - Saída");
		cbTipoDoc.setStyleName("caption-align-nfe");				
		
		final ComboBox cbFormaEmissao = new ComboBox("Forma de emissão");
		cbFormaEmissao.setRequired(true);
		cbFormaEmissao.setNullSelectionAllowed(false);
		cbFormaEmissao.setTextInputAllowed(false);
		cbFormaEmissao.addItem("Normal");
		cbFormaEmissao.addItem("Contingência FS-IA");
		cbFormaEmissao.addItem("Contingência via EPEC");
		cbFormaEmissao.addItem("Contingência via DPEC");
		cbFormaEmissao.addItem("Contingência FS-DA");
		cbFormaEmissao.addItem("Contingência SVC-AN");
		cbFormaEmissao.addItem("Contingência SVC-RS");
		cbFormaEmissao.setStyleName("caption-align-nfe");
		
		final ComboBox cbConsumidorFinal = new ComboBox("Consumidor final");
		cbConsumidorFinal.setRequired(true);
		cbConsumidorFinal.setNullSelectionAllowed(false);
		cbConsumidorFinal.setTextInputAllowed(false);
		cbConsumidorFinal.addItem("0 - Não");
		cbConsumidorFinal.addItem("1 - Sim");
		cbConsumidorFinal.setStyleName("caption-align-nfe");
		
		final ComboBox cbDestinoOperacao = new ComboBox("Destino da Operação");
		cbDestinoOperacao.setRequired(true);
		cbDestinoOperacao.setNullSelectionAllowed(false);
		cbDestinoOperacao.setTextInputAllowed(false);
		cbDestinoOperacao.addItem("1 - Operacao Interna");
		cbDestinoOperacao.addItem("2 - Operacao Interestadual");
		cbDestinoOperacao.addItem("3 - Operação com Exterior");
		cbDestinoOperacao.setStyleName("caption-align-nfe");
		
		final ComboBox cbTipoAtendi = new ComboBox("Tipo de atendimento");
		cbTipoAtendi.setRequired(true);
		cbTipoAtendi.setNullSelectionAllowed(false);
		cbTipoAtendi.setTextInputAllowed(false);
		cbTipoAtendi.addItem("Não se aplica");
		cbTipoAtendi.addItem("1 - Operação Presencial");
		cbTipoAtendi.addItem("2 - Operação NÃO Presencial, pela INTERNET");
		cbTipoAtendi.addItem("3 - Operação NÃO Presencial, pela TELEATENDIMENTO");
		cbTipoAtendi.addItem("9 - Operação NÃO Presencial, OUTROS");
		cbTipoAtendi.setStyleName("caption-align-nfe");
		
		final TextArea txtObservacao = new TextArea("Observacao");
		txtObservacao.setNullRepresentation("");
		txtObservacao.setRequired(true);
		txtObservacao.setStyleName("caption-align-nfe");
		txtObservacao.setWidth("440px");
		txtObservacao.setHeight("70px");
		
		if(valueDefaultNfe != null){			
			cbTipoDoc.select(valueDefaultNfe.getTipoDocumento());			
			cbFormaEmissao.select(valueDefaultNfe.getFormaEmissao());
			cbConsumidorFinal.select(valueDefaultNfe.getConsumidorFinal());
			cbDestinoOperacao.select(valueDefaultNfe.getDestinoOperacao());
			cbTipoAtendi.select(valueDefaultNfe.getTipoAtendimento());			
			txtObservacao.setValue(valueDefaultNfe.getObservacao());
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
						addComponent(cbTipoDoc);						
					}
				});
			}
		});
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(cbFormaEmissao);
					}
				});
			}
		});		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(cbDestinoOperacao);
					}
				});
			}
		});		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(cbConsumidorFinal);
					}
				});
			}
		});		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(cbTipoAtendi);
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

		
		Button bt1 = new Button("Salvar");
		bt1.addStyleName("default");
		bt1.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(valueDefaultNfe != null){
					
					if(cbTipoDoc.isValid()){

						valueDefaultNfe.setTipoDocumento(cbTipoDoc.getValue().toString());
						valueDefaultNfe.setFormaEmissao(cbFormaEmissao.getValue().toString());
						valueDefaultNfe.setConsumidorFinal(cbConsumidorFinal.getValue().toString());
						valueDefaultNfe.setDestinoOperacao(cbDestinoOperacao.getValue().toString());
						valueDefaultNfe.setTipoAtendimento(cbTipoAtendi.getValue().toString());
						valueDefaultNfe.setObservacao(txtObservacao.getValue());
						
						NfeDAO55.saveValueDefault(valueDefaultNfe); 
						Notify.Show("Alterações Salvas com Sucesso!", Notify.TYPE_SUCCESS);
					}else{
						Notify.Show_Invalid_Submit_Form();
					}
				}else{
					if(cbTipoDoc.isValid()){
						
						valueDefaultNfe = new ConfigNfeMod55();
						valueDefaultNfe.setTipoDocumento(cbTipoDoc.getValue().toString());						
						valueDefaultNfe.setFormaEmissao(cbFormaEmissao.getValue().toString());
						valueDefaultNfe.setConsumidorFinal(cbConsumidorFinal.getValue().toString());
						valueDefaultNfe.setDestinoOperacao(cbDestinoOperacao.getValue().toString());
						valueDefaultNfe.setTipoAtendimento(cbTipoAtendi.getValue().toString());
						valueDefaultNfe.setObservacao(txtObservacao.getValue());
						
						NfeDAO55.saveValueDefault(valueDefaultNfe); 
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
