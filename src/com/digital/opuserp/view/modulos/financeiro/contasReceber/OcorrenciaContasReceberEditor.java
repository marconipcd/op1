package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.OcorrenciaContasReceberDAO;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.OcorrenciaContasReceber;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class OcorrenciaContasReceberEditor extends Window implements GenericEditor {
	
	private Button btSalvar; 
	private Button btCancelar;	
	private VerticalLayout vlRoot;		
		
	private TextArea taOcorrencia; 
	
	//Integer codContrato;
	ContasReceber contas_receber;
	public OcorrenciaContasReceberEditor( String title, boolean modal, ContasReceber contas_receber){
				
		this.contas_receber = contas_receber;
		
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
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField txtCodOs = new TextField("Cod.");		
					txtCodOs.setStyleName("caption-align");
					txtCodOs.setNullRepresentation("");				
					txtCodOs.setRequired(true);
					txtCodOs.setValue(contas_receber.getId().toString());
					
					addComponent(txtCodOs);
				
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
					
					txtCliente.setValue(contas_receber.getCliente().getNome_razao());
									
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
	JPAContainer<OcorrenciaContasReceber> container;
	public JPAContainer<OcorrenciaContasReceber> buildJpaContainer(){
		container = JPAContainerFactory.make(OcorrenciaContasReceber.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("contas_receber",contas_receber));
				
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
				String data = sdf.format(source.getItem(itemId).getItemProperty("data").getValue());
				
				String s = data+" - "+
						source.getItem(itemId).getItemProperty("operador").getValue().toString()+" - "+source.getItem(itemId).getItemProperty("tipo").getValue().toString();
				
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
											
						boolean check = OcorrenciaContasReceberDAO.add(new OcorrenciaContasReceber(null, taOcorrencia.getValue(), contas_receber, OpusERP4UI.getUsuarioLogadoUI().getUsername(), new Date()));
						
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
				
					fireEvent(new OcorreciaContasReceberEvent(getUI(), taOcorrencia.getValue(), false));
					close();					
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
	
	
	public void addListerner(OcorreciaContasReceberListerner target){
		try {
			Method method = OcorreciaContasReceberListerner.class.getDeclaredMethod("onClose", OcorreciaContasReceberEvent.class);
			addListener(OcorreciaContasReceberEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(OcorreciaContasReceberListerner target){
		removeListener(OcorreciaContasReceberEvent.class, target);
	}
	public static class OcorreciaContasReceberEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private String ocorrencia;
		
		public OcorreciaContasReceberEvent(Component source, String ocorrencia, boolean confirm) {
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
	public interface OcorreciaContasReceberListerner extends Serializable{
		public void onClose(OcorreciaContasReceberEvent event);
	}
}

