package com.digital.opuserp.view.modulos.configuracoes.config_acesso.central;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CentralAssinanteDAO;
import com.digital.opuserp.domain.ConfigCentralAssinante;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ConfigCentralAssinanteView extends VerticalLayout {

	
	private ConfigCentralAssinante valueDefaultConfigCentral;
	public ConfigCentralAssinanteView(){		
		
		valueDefaultConfigCentral = CentralAssinanteDAO.getDefaultValue();
				
		JPAContainer<Setores> containerSetores = JPAContainerFactory.makeReadOnly(Setores.class, ConnUtil.getEntity());
		containerSetores.addContainerFilter(Filters.eq("empresa_id",OpusERP4UI.getEmpresa().getId()));
		
		final ComboBox cbSetor = new ComboBox("Setor", containerSetores);
		cbSetor.setRequired(true);
		cbSetor.setNullSelectionAllowed(false);
		cbSetor.setItemCaptionPropertyId("nome");
		cbSetor.addStyleName("caption-align-config-central");
		if(valueDefaultConfigCentral != null && valueDefaultConfigCentral.getSetor() != null){
			cbSetor.select(valueDefaultConfigCentral.getSetor().getId());
		}
		
		JPAContainer<CrmAssunto> containerAssunto = JPAContainerFactory.makeReadOnly(CrmAssunto.class, ConnUtil.getEntity());
		containerAssunto.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));		
		final ComboBox cbAssunto = new ComboBox("Assunto", containerAssunto);
		cbAssunto.setRequired(true);
		cbAssunto.setNullSelectionAllowed(false);
		cbAssunto.setItemCaptionPropertyId("nome");
		cbAssunto.addStyleName("caption-align-config-central");
		cbAssunto.setWidth("263px");
		if(valueDefaultConfigCentral != null && valueDefaultConfigCentral.getSetor() != null){
			cbAssunto.select(valueDefaultConfigCentral.getAssunto().getId());
		}
		
				
		VerticalLayout vl1 = new VerticalLayout();
		vl1.addComponent(new Label("Autenticação de Usuário",ContentMode.HTML));
		vl1.addStyleName("vl1_central_auth");
		vl1.setWidth("351px");
		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						addComponent(cbSetor); 
					}
				});
			}
		});
		
		vl1.addComponent(new HorizontalLayout(){
			{
				addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						addComponent(cbAssunto); 
					}
				});
			}
		});
		
		
		Button bt1 = new Button("Salvar");
		bt1.addStyleName("default");
		bt1.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(valueDefaultConfigCentral != null){
					
					if(cbSetor.isValid() && cbAssunto.isValid()){
						EntityItem<CrmAssunto> entityAssunto = (EntityItem<CrmAssunto>)cbAssunto.getItem(cbAssunto.getValue());
						valueDefaultConfigCentral.setAssunto(entityAssunto.getEntity());
						
						
						EntityItem<Setores> entitySetor = (EntityItem<Setores>)cbSetor.getItem(cbSetor.getValue());
						valueDefaultConfigCentral.setSetor(entitySetor.getEntity());
						
						CentralAssinanteDAO.saveValueDefault(valueDefaultConfigCentral); 
						Notify.Show("Alterações Salvas com Sucesso!", Notify.TYPE_SUCCESS);
					}else{
						Notify.Show_Invalid_Submit_Form();
					}
				}else{
					if(cbSetor.isValid() && cbAssunto.isValid()){
						valueDefaultConfigCentral = new ConfigCentralAssinante();
						EntityItem<CrmAssunto> entityAssunto = (EntityItem<CrmAssunto>)cbAssunto.getItem(cbAssunto.getValue());
						valueDefaultConfigCentral.setAssunto(entityAssunto.getEntity());
						
						
						EntityItem<Setores> entitySetor = (EntityItem<Setores>)cbSetor.getItem(cbSetor.getValue());
						valueDefaultConfigCentral.setSetor(entitySetor.getEntity());			
						
						CentralAssinanteDAO.saveValueDefault(valueDefaultConfigCentral);
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
