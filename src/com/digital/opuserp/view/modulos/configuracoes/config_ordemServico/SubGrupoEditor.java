package com.digital.opuserp.view.modulos.configuracoes.config_ordemServico;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.CrmFormasContato;
import com.digital.opuserp.domain.GrupoOse;
import com.digital.opuserp.domain.Setores;
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

public class SubGrupoEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private ComboBox cbPrioridade;
	private JPAContainer<CrmAssunto> containerAssunto;
	
	public SubGrupoEditor(Item item, String title, boolean modal){
		this.item = item;
				
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
	
	public JPAContainer<CrmAssunto> buildContainer(){
		containerAssunto = JPAContainerFactory.makeReadOnly(CrmAssunto.class, ConnUtil.getEntity());
		containerAssunto.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(fieldGroup.getField("setor").getValue() != null){
			EntityItem<Setores> setor = (EntityItem<Setores>) ((ComboBox)fieldGroup.getField("setor")).getItem(fieldGroup.getField("setor").getValue()); 
			
			if(setor != null){
				containerAssunto.addContainerFilter(Filters.eq("setor", setor.getEntity()));
			}
		}
		
		return containerAssunto;				
	}
	
	public JPAContainer<GrupoOse> getContainerGrupo(){
		JPAContainer<GrupoOse> container = JPAContainerFactory.make(GrupoOse.class, ConnUtil.getEntity());
		return container;
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					ComboBox cbGrupo = new ComboBox("Grupo", getContainerGrupo());			
					cbGrupo.setWidth("100%");				
					cbGrupo.setNullSelectionAllowed(false);
					cbGrupo.setRequired(true);
					cbGrupo.setConverter(new SingleSelectConverter(cbGrupo));
					cbGrupo.setItemCaptionPropertyId("nome");
					cbGrupo.setStyleName("caption-align");

					cbGrupo.focus();
					
					addComponent(cbGrupo);	
					
					fieldGroup.bind(cbGrupo,"grupo");
				}
			});
					
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtNomeSubGrupo = new TextField ("Nome");				
					txtNomeSubGrupo.setWidth("100%");				
					txtNomeSubGrupo.setNullRepresentation("");
					txtNomeSubGrupo.setRequired(true);
					txtNomeSubGrupo.setStyleName("caption-align");
					
					addComponent(txtNomeSubGrupo);					
					fieldGroup.bind(txtNomeSubGrupo,"nome");
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					cbPrioridade  = new ComboBox("Prioridade");
					cbPrioridade.setRequired(true);
					cbPrioridade.setNullSelectionAllowed(false);
					cbPrioridade.setStyleName("caption-align");
					cbPrioridade.addItem("0");
					cbPrioridade.addItem("1");
					cbPrioridade.addItem("2");
					cbPrioridade.addItem("3");
					cbPrioridade.addItem("4");
					//cbPrioridade.setConverter(new StringToIntegerConverter());
					
					
					addComponent(cbPrioridade);					
					//fieldGroup.bind(cbPrioridade,"prioridade");
					if(item.getItemProperty("prioridade").getValue() != null){
						cbPrioridade.select(item.getItemProperty("prioridade").getValue().toString());
					}
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					final ComboBox cbGerar = new ComboBox("Gerar CRM");
					cbGerar.setRequired(true);
					cbGerar.setStyleName("caption-align");
					cbGerar.setNullSelectionAllowed(false);
					cbGerar.setTextInputAllowed(false); 
					cbGerar.addItem("SIM");
					cbGerar.addItem("NAO");
					cbGerar.setImmediate(true);
					
					cbGerar.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(cbGerar.getValue().toString().equals("SIM")){
								item.getItemProperty("gerar_crm").setValue("SIM");
								if(fieldGroup.getField("setor") != null){
									fieldGroup.getField("setor").setEnabled(true);
									fieldGroup.getField("setor").setRequired(true);
									//fieldGroup.getField("setor").removeStyleName("");		
									JavaScript.getCurrent().execute("document.getElementByClassName('txtSetor').classList.remove('v-disabled')");
									
								}
								if(fieldGroup.getField("crm_assunto") != null){
									fieldGroup.getField("crm_assunto").setEnabled(true);
									fieldGroup.getField("crm_assunto").setRequired(true);
									fieldGroup.getField("crm_assunto").removeStyleName("v-disabled");
								}
								if(fieldGroup.getField("crm_forma_contato") != null){
									fieldGroup.getField("crm_forma_contato").setEnabled(true);
									fieldGroup.getField("crm_forma_contato").setRequired(true); 
									fieldGroup.getField("crm_forma_contato").removeStyleName("v-disabled");
								}
							}else{
								item.getItemProperty("gerar_crm").setValue("NAO");
								if(fieldGroup.getField("setor") != null){
									fieldGroup.getField("setor").setEnabled(false);
									fieldGroup.getField("setor").setRequired(false); 
									fieldGroup.getField("setor").setValue(null); 
								}
								if(fieldGroup.getField("crm_assunto") != null){
									fieldGroup.getField("crm_assunto").setEnabled(false);
									fieldGroup.getField("crm_assunto").setRequired(false); 
									fieldGroup.getField("crm_assunto").setValue(null); 
								}
								if(fieldGroup.getField("crm_forma_contato") != null){
									fieldGroup.getField("crm_forma_contato").setEnabled(false);
									fieldGroup.getField("crm_forma_contato").setRequired(false); 
									fieldGroup.getField("crm_forma_contato").setValue(null);
								}
							}
						}
					});
					
					if(item.getItemProperty("gerar_crm").getValue() != null){
						if(item.getItemProperty("gerar_crm").getValue().toString().equals("SIM")){
							cbGerar.select("SIM");
						}else{
							cbGerar.select("NAO");
						}
					}else{
						cbGerar.select("NAO");
					}
					
					addComponent(cbGerar);					
					
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					JPAContainer<CrmFormasContato> containerFormasContato = JPAContainerFactory.makeReadOnly(CrmFormasContato.class, ConnUtil.getEntity());
					containerFormasContato.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
					
					ComboBox cbFormasContato = new ComboBox("Forma de Contato", containerFormasContato);					
					cbFormasContato.setStyleName("caption-align");
					cbFormasContato.setNullSelectionAllowed(false);
					cbFormasContato.setTextInputAllowed(false); 
					cbFormasContato.setItemCaptionPropertyId("nome");
					cbFormasContato.setConverter(new SingleSelectConverter(cbFormasContato));
										
					addComponent(cbFormasContato);
					fieldGroup.bind(cbFormasContato, "crm_forma_contato");

					if(item.getItemProperty("crm_forma_contato").getValue() == null){
						cbFormasContato.setEnabled(false); 
					}
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					JPAContainer<Setores> containerSetor = JPAContainerFactory.makeReadOnly(Setores.class, ConnUtil.getEntity());
					containerSetor.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
					
					final ComboBox cbSetor = new ComboBox("Setor", containerSetor);	
					cbSetor.setId("txtSetor");
					cbSetor.addStyleName("txtSetor");
					cbSetor.setStyleName("caption-align");
					cbSetor.setNullSelectionAllowed(false);
					cbSetor.setTextInputAllowed(false); 
					cbSetor.setItemCaptionPropertyId("nome");
					cbSetor.setConverter(new SingleSelectConverter(cbSetor));
					cbSetor.setWidth("173px");
					cbSetor.setImmediate(true);
					
					cbSetor.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(cbSetor.getValue() != null){
								if(fieldGroup.getField("crm_assunto") != null){
									((ComboBox)fieldGroup.getField("crm_assunto")).setContainerDataSource(buildContainer());									
								}
							}
						}
					});
					
					
					addComponent(cbSetor);	
					fieldGroup.bind(cbSetor, "setor");

					if(item.getItemProperty("setor").getValue() == null){
						cbSetor.setEnabled(false); 
					}
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
										
					ComboBox cbAssunto = new ComboBox("Assunto", buildContainer());					
					cbAssunto.setStyleName("caption-align");
					cbAssunto.setNullSelectionAllowed(false);
					cbAssunto.setTextInputAllowed(false); 
					cbAssunto.setItemCaptionPropertyId("nome");
					cbAssunto.setConverter(new SingleSelectConverter(cbAssunto));
					cbAssunto.setWidth("204px");
					
					
					addComponent(cbAssunto);
					fieldGroup.bind(cbAssunto, "crm_assunto");

					if(item.getItemProperty("crm_assunto").getValue() == null){
						cbAssunto.setEnabled(false); 
					}
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					ComboBox cbStatus = new ComboBox("Status");
					cbStatus.setNullSelectionAllowed(false);
					cbStatus.setRequired(true);
					cbStatus.addItem("ATIVO");
					cbStatus.addItem("INATIVO");
					cbStatus.setStyleName("caption-align");
					
					
					addComponent(cbStatus);					
					fieldGroup.bind(cbStatus,"status");		
					
				}
			});
		
		
		
		
		
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && cbPrioridade.isValid()){
					try {
						
						item.getItemProperty("prioridade").setValue(Integer.parseInt(cbPrioridade.getValue().toString()));
						
						fieldGroup.commit();				
						fireEvent(new SubGrupoEvent(getUI(), item, true));					
												
						close();
					} catch (Exception e) {					
						e.printStackTrace();
						Notify.Show(e.getLocalizedMessage(), Notify.TYPE_ERROR);
					}
				}else{
					
					for (Field<?> field: fieldGroup.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
					}
					
					if(!cbPrioridade.isValid()){
						cbPrioridade.addStyleName("invalid-txt");
					}else{
						cbPrioridade.removeStyleName("invalid-txt");
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
					fireEvent(new SubGrupoEvent(getUI(), item, false));
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
										fireEvent(new SubGrupoEvent(getUI(), item, true));					
										Notify.Show("Categoria Cadastrada com Sucesso!", Notify.TYPE_SUCCESS);
										
										close();
									} catch (Exception e) {					
										e.printStackTrace();
										Notify.Show(e.getLocalizedMessage(), Notify.TYPE_ERROR);
									}
								}else{
									for (Field<?> field: fieldGroup.getFields()) {
										
										if(!field.isValid()){
											field.addStyleName("invalid-txt");
										}else{
											field.removeStyleName("invalid-txt");
										}
									}
									
									Notify.Show_Invalid_Submit_Form();	
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new SubGrupoEvent(getUI(), item, false));
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
	
	
	public void addListerner(SubGrupoListerner target){
		try {
			Method method = SubGrupoListerner.class.getDeclaredMethod("onClose", SubGrupoEvent.class);
			addListener(SubGrupoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(SubGrupoListerner target){
		removeListener(SubGrupoEvent.class, target);
	}
	public static class SubGrupoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public SubGrupoEvent(Component source, Item item, boolean confirm) {
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
	public interface SubGrupoListerner extends Serializable{
		public void onClose(SubGrupoEvent event);
	}

	
}
