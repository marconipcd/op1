package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesOseDAO;
import com.digital.opuserp.domain.AlteracoesOse;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class InformarOcorrenciaEditor extends Window implements GenericEditor {

	private Item item;
	
	private Button btSalvar; 
	private Button btCancelar;
	
	private VerticalLayout vlRoot;	
	private FormLayout flPrincipal;
	
	private FieldGroup fieldGroup;
	
	private TextArea taOcorrencia; 
	
	Integer codContrato;
	public InformarOcorrenciaEditor(Item item, String title, boolean modal, Integer codContrato){
		this.item = item;
		
		this.codContrato = codContrato;
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		setWidth("750px");
		
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
				//hlButtons.addComponent(buildBtSalvar());
				
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
					
					TextField txtCodOs = new TextField("OS Número");		
					txtCodOs.setStyleName("caption-align");
					txtCodOs.setNullRepresentation("");				
					txtCodOs.setRequired(true);
					
									
					addComponent(txtCodOs);
					fieldGroup.bind(txtCodOs, "id");
					txtCodOs.setReadOnly(true);
									
				}
		});	
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField txtCliente = new TextField("Cliente");		
					txtCliente.setStyleName("caption-align");
					txtCliente.setNullRepresentation("");	
					txtCliente.setWidth("100%");
					txtCliente.setRequired(true);
					
					txtCliente.setValue(((Cliente)item.getItemProperty("cliente").getValue()).getNome_razao());
									
					addComponent(txtCliente);					
					txtCliente.setReadOnly(true);
									
				}
		});	
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					taOcorrencia = new TextArea("Ocorrência");		
					taOcorrencia.setStyleName("caption-align");
					taOcorrencia.setNullRepresentation("");				
					taOcorrencia.setRequired(true);
					taOcorrencia.setHeight("80px");
					taOcorrencia.setWidth("100%");
					taOcorrencia.addStyleName("uppercase");
					taOcorrencia.focus();
														
					addComponent(taOcorrencia);												
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
							
					addComponent(new HorizontalLayout(){
						{
							setWidth("100%");
							addComponent(buildBtSalvar());
							setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
						}
					});
																	
				}
		});	
		
		vlRoot.addComponent(buildTbChamados());												
				
		
	}
	JPAContainer<AlteracoesOse> container;
	public JPAContainer<AlteracoesOse> buildJpaContainer(){
		container = JPAContainerFactory.make(AlteracoesOse.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("ose", new Ose(codContrato)));
		container.addNestedContainerProperty("operador.username");
		
		container.sort(new String []{"id"}, new boolean[] {false});
		return container;
	}
	
	Table tbAlteracoes;
	public Table buildTbChamados(){
		tbAlteracoes = new Table(null, buildJpaContainer());
		tbAlteracoes.setWidth("100%");
		tbAlteracoes.setHeight("230px");
		tbAlteracoes.setSelectable(true);

		
		tbAlteracoes.addGeneratedColumn("Ocorrência", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String data = sdf.format(source.getItem(itemId).getItemProperty("data_alteracao").getValue());
				
				String s = data+" - "+
						source.getItem(itemId).getItemProperty("operador.username").getValue().toString()+" - "+source.getItem(itemId).getItemProperty("tipo").getValue().toString();
				
				return s;
			}
		});
		
		tbAlteracoes.setVisibleColumns(new Object[]{"Ocorrência"});
		
		
		
		return tbAlteracoes;
	}
	
	

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("Gravar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

			
									
				if(taOcorrencia.isValid()){
					try {		
												

						EntityItem<Ose> entityItem = (EntityItem<Ose>)item;
						boolean check = AlteracoesOseDAO.add(new AlteracoesOse(null, taOcorrencia.getValue(), entityItem.getEntity(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
						
						if(check){
							taOcorrencia.setValue("");
							container.refresh();
							Notify.Show("Ocorrência Informada com Sucesso!", Notify.TYPE_SUCCESS);
						}
						

					} catch (Exception e) {									
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
					}
				}else{
					
					    for (Field<?> field: fieldGroup.getFields()) {						
							if(!field.isValid()){
								field.addStyleName("invalid-txt");
							}else{
								field.removeStyleName("invalid-txt");
							}
					    }
					    
					   if(!taOcorrencia.isValid()){
						   taOcorrencia.addStyleName("invalid-txt");
					   }else{
						   taOcorrencia.removeStyleName("invalid-txt");
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
		//btSalvar.setEnabled(false);
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new OcorreciaOsiEvent(getUI(), item,taOcorrencia.getValue(), false));
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
									
									
									
									if(fieldGroup.isValid()){							
									  fireEvent(new OcorreciaOsiEvent(getUI(), item,taOcorrencia.getValue(), true));
																			
									}else{
									  if(!taOcorrencia.isValid()){
										   taOcorrencia.addStyleName("invalid-txt");
									  }else{
										   taOcorrencia.removeStyleName("invalid-txt");
									  }
										  
									  Notify.Show_Invalid_Submit_Form();
									}
								
								
								} catch (Exception e) {									
									e.printStackTrace();
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new OcorreciaOsiEvent(getUI(), item,taOcorrencia.getValue(), false));
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
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
	
	
	public void addListerner(OcorrenciaOsiListerner target){
		try {
			Method method = OcorrenciaOsiListerner.class.getDeclaredMethod("onClose", OcorreciaOsiEvent.class);
			addListener(OcorreciaOsiEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(OcorrenciaOsiListerner target){
		removeListener(OcorreciaOsiEvent.class, target);
	}
	public static class OcorreciaOsiEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private String ocorrencia;
		
		public OcorreciaOsiEvent(Component source, Item item,String ocorrencia, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;
			this.ocorrencia = ocorrencia;
		}

		public String getOcorrencia(){
			return ocorrencia;
		}
		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}	
		
	}
	public interface OcorrenciaOsiListerner extends Serializable{
		public void onClose(OcorreciaOsiEvent event);
	}
	
	
	




}

