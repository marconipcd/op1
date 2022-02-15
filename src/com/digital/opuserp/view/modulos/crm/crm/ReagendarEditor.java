package com.digital.opuserp.view.modulos.crm.crm;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlertaPendenciaDAO;
import com.digital.opuserp.dao.AlteracoesCrmDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.CrmDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.SubModuloDAO;
import com.digital.opuserp.domain.AlteracoesCrm;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.CrmFormasContato;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.crm.crm.ContatoEditor.NovoContatoEvent;
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
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ReagendarEditor extends Window {

	Item item;
	
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;


	boolean validarData;
	DateField dfPrevisao;
	private Integer codCrm;
	private TextField txtHora;
	private ComboBox cbMotivo;

	public ReagendarEditor(Item item, String title, boolean modal){
		this.item = item;
		
		codCrm = (Integer) item.getItemProperty("id").getValue();
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");
		
//		setWidth("960px");
		
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
				
				
				hlButtons.addComponent(buildBtSalvar("Reagendar"));
				
				
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
												
						final ComboBox cbSetor = new ComboBox("Setor", buildContainerSetores());
						cbSetor.setRequired(true);
						cbSetor.setWidth("260px");
						cbSetor.setTextInputAllowed(false);
						cbSetor.setNullSelectionAllowed(false);					
						cbSetor.setConverter(new SingleSelectConverter(cbSetor));
						cbSetor.setItemCaptionPropertyId("nome");
						cbSetor.setStyleName("caption-align-acesso");
										
						cbSetor.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(cbSetor.getValue() != null){
									if(fieldGroup.getField("crm_assuntos") != null){
										((ComboBox)fieldGroup.getField("crm_assuntos")).setContainerDataSource(buildContainerAssunto());
										((ComboBox)fieldGroup.getField("crm_assuntos")).setEnabled(true);
									}
								}
							}
						});
						
						addComponent(cbSetor);					
						fieldGroup.bind(cbSetor,"setor");
					}
				});
			
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
													
						ComboBox cbAssunto = new ComboBox("Assunto", buildContainerAssunto());
						cbAssunto.setRequired(true);
						cbAssunto.setWidth("260px");
						cbAssunto.setTextInputAllowed(false);
						cbAssunto.setNullSelectionAllowed(false);					
						cbAssunto.setConverter(new SingleSelectConverter(cbAssunto));
						cbAssunto.setItemCaptionPropertyId("nome");
						cbAssunto.setStyleName("caption-align-acesso");
											
						addComponent(cbAssunto);					
						fieldGroup.bind(cbAssunto,"crm_assuntos");						
					}
				});
		
		
			vlRoot.addComponent(
				new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						
						ComboBox cbFormaContato = new ComboBox("Contato Feedback", buildContainerFormaContato());
						cbFormaContato.setRequired(true);
						cbFormaContato.setWidth("260px");
						cbFormaContato.setTextInputAllowed(false);
						cbFormaContato.setNullSelectionAllowed(false);					
						cbFormaContato.setConverter(new SingleSelectConverter(cbFormaContato));			
						cbFormaContato.setItemCaptionPropertyId("nome");
						cbFormaContato.setStyleName("caption-align-acesso");
						
						addComponent(cbFormaContato);					
						fieldGroup.bind(cbFormaContato,"contato_feedback");
					}
				});
												
				vlRoot.addComponent(new FormLayout(){
							{

									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									dfPrevisao = new DateField ("Data");
									dfPrevisao.setStyleName("caption-align-acesso");
									dfPrevisao.setDateFormat("dd/MM/yyyy");														
									dfPrevisao.setRequired(true);

									addComponent(dfPrevisao);
									fieldGroup.bind(dfPrevisao,"data_agendado");
										
									dfPrevisao.addListener(new FieldEvents.BlurListener() {

										@Override
										public void blur(BlurEvent event) {
											
											Calendar data = Calendar.getInstance(); 
											data.setTime(new Date());
											data.add(Calendar.DAY_OF_MONTH, -1);	
											Date dtOnten = data.getTime();	
											
											if(dfPrevisao.getValue()!=null && !dfPrevisao.getValue().equals("") && dtOnten.compareTo(dfPrevisao.getValue()) < 0){										
												validarData = true;
											}else{
												validarData = false;
												
												Notify.Show("Data Inválida!", Notify.TYPE_ERROR);

											}
											
											
											txtHora.focus();																
										}
									});
							}
						});
						
				vlRoot.addComponent(new FormLayout(){
							{

									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									txtHora = new TextField ("Hora");									
									txtHora.setStyleName("caption-align-acesso");								
									txtHora.setNullRepresentation("");
									txtHora.setId("txtHoraCrm2");
									
									JavaScript.getCurrent().execute("$('#txtHoraCrm2').mask('00:00')");
									
									addComponent(txtHora);
									//fieldGroup.bind(txtHora,"hora_agendado");
							}
						});
						
						
				vlRoot.addComponent(new FormLayout(){
							{

									setStyleName("form-cutom");
									setMargin(true);
									setSpacing(true);
																		
									cbMotivo = new ComboBox ("Motivo");									
									cbMotivo.setStyleName("caption-align-acesso");								
									cbMotivo.setNullSelectionAllowed(false);
									cbMotivo.setTextInputAllowed(false);						
									cbMotivo.setRequired(true);
									cbMotivo.setWidth("260px");
									cbMotivo.addItem("NAO CONSEGUIU CONTATO");
									cbMotivo.addItem("DIRECIONADO PARA OUTRO SETOR");
									cbMotivo.addItem("ALTERADO FORMA DE CONTATO");									
									cbMotivo.addItem("REMARCADO PELO CLIENTE");
									cbMotivo.addItem("EM PROCESSO DE ANALISE");
									cbMotivo.addItem("AGUARDANDO RETORNO PELO WHATSAPP");
									
									addComponent(cbMotivo);
									fieldGroup.bind(cbMotivo,"motivo_reagendamento");
							}
						});

	}
	
	private JPAContainer<CrmFormasContato> buildContainerFormaContato(){
		
		JPAContainer<CrmFormasContato> container = JPAContainerFactory.make(CrmFormasContato.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		return container;
	}
	
	private JPAContainer<Setores> buildContainerSetores(){
		
		JPAContainer<Setores> container = JPAContainerFactory.make(Setores.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		return container;
	}
	
	private JPAContainer<CrmAssunto> buildContainerAssunto(){
		
		JPAContainer<CrmAssunto> container = JPAContainerFactory.make(CrmAssunto.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		if(fieldGroup.getField("setor").getValue() != null){
			EntityItem<Setores> setor = (EntityItem<Setores>) ((ComboBox)fieldGroup.getField("setor")).getItem(fieldGroup.getField("setor").getValue()); 
			
			if(setor != null){
				container.addContainerFilter(Filters.eq("setor", setor.getEntity()));
			}
		}
		
		container.sort(new String[] {"nome"}, new boolean[]{true});
		return container;
	}
	
	private String getNextNProtocolo(){
		return CrmDAO.getNextID();
	}

	
	public Button buildBtSalvar(String s) {
		
		btSalvar = new Button(s, new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
												
				Calendar data = Calendar.getInstance(); 
				data.setTime(new Date());
				data.add(Calendar.DAY_OF_MONTH, -1);	
				Date dtOnten = data.getTime();	
				
				if(dfPrevisao.getValue()!=null && !dfPrevisao.getValue().equals("") && dtOnten.compareTo(dfPrevisao.getValue()) < 0){										
					validarData = true;
				}else{
					validarData = false;
				}
				
				if(fieldGroup.isValid() && !cbMotivo.getValue().equals("") && validarData){
					try{
						
						fieldGroup.commit();

						EntityManager em = ConnUtil.getEntity();
						Crm crm = em.find(Crm.class, codCrm);
						
						SimpleDateFormat sdf = null;
						
						if(txtHora.getValue() != null && !txtHora.getValue().isEmpty() && !txtHora.getValue().equals("")){
							sdf = new SimpleDateFormat("hh:mm");
							crm.setHora_agendado(sdf.parse(txtHora.getValue()));
						}
						
						crm.setStatus("AGENDADO");
						crm.setOperador_tratamento("");
						crm.setData_agendado(dfPrevisao.getValue());
						crm.setMotivo_reagendamento((String) cbMotivo.getValue());
						crm.setSetor((Setores)item.getItemProperty("setor").getValue());
						crm.setCrm_assuntos((CrmAssunto)item.getItemProperty("crm_assuntos").getValue());
//						crm.setCrm_formas_contato((CrmFormasContato)item.getItemProperty("crm_formas_contato").getValue());
						crm.setContato_feedback((CrmFormasContato)item.getItemProperty("contato_feedback").getValue());

						crm.setReagendado(true);
						
						CrmDAO.saveCrm(crm);
					
						Notify.Show("Reagendado com Sucesso", Notify.TYPE_SUCCESS);
						
						LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Reagendou um CRM | COD: "+crm.getId().toString()));
						AlteracoesCrmDAO.save(new AlteracoesCrm(null, "REAGENDAMENTO DE CRM: "+cbMotivo.getValue().toString(), crm, OpusERP4UI.getUsuarioLogadoUI(), new Date()));				
						
						close();
						
						AlertaPendenciaDAO.removePendencia(SubModuloDAO.findToId("Contatos"), crm.getId());
						fireEvent(new ReagendadoEvent(getUI().getCurrent(), item, true));
						
					} catch (Exception e) {				
						e.printStackTrace();
						Notify.Show("Não foi Possivel Reagendar o Crm!", Notify.TYPE_ERROR);
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

	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

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
		
		
		return btCancelar;
	}
	
	
	public void addListerner(ReagendamentoListerner target){
		try {
			Method method = ReagendamentoListerner.class.getDeclaredMethod("onClose", ReagendadoEvent.class);
			addListener(ReagendadoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ReagendamentoListerner target){
		removeListener(ReagendadoEvent.class, target);
	}
	public static class ReagendadoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public ReagendadoEvent(Component source, Item item,boolean confirm) {
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
	public interface ReagendamentoListerner extends Serializable{
		public void onClose(ReagendadoEvent event);
	}

	
}
