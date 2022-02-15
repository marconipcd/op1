package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
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
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ReagendarEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TextField tfCod;
	
		
	private DateField dfNovaData;
	private boolean valid_data;
	
	private ComboBox cbTurno;
	private DateField dfPrevisao;
	private ComboBox cbHora;
	private TextArea txtMotivo;
	
	public ReagendarEditor(String title, boolean modal){
		
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
		
					
		
		vlRoot.addComponent(
				new HorizontalLayout(){
					{
						
						addComponent(new FormLayout(){
							{
								
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
								
								dfPrevisao = new DateField ("Previsão");
								dfPrevisao.setDateFormat("dd/MM/yyyy");																		
								dfPrevisao.setRequired(true);
								dfPrevisao.setImmediate(true);
								dfPrevisao.setStyleName("caption-align");
								
								dfPrevisao.addValueChangeListener(new Property.ValueChangeListener() {
									
									@Override
									public void valueChange(ValueChangeEvent event) {
										if(dfPrevisao.getValue() != null){
											try{
												SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
												if(dfPrevisao.getValue().after(new Date()) || dfPrevisao.getValue().equals(sdf.parse(sdf.format(new Date())))){
													valid_data = true;
												}else{
													valid_data = false;
													Notify.Show("Data Inválida, Selecione uma Data Maior ou Igual a Hoje!", Notify.TYPE_ERROR);
												}
											}catch(Exception e){
												e.printStackTrace();
												valid_data = false;
											}
										}
										
									}
								});
								
								addComponent(dfPrevisao);
								
								
							}
						});
						
						addComponent(new FormLayout(){
							{
								
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
								
								cbHora = new ComboBox ();																							
								cbHora.setRequired(false);
								cbHora.setImmediate(true);
								cbHora.setTextInputAllowed(false);
								cbHora.setWidth("70px");
								cbHora.setNullSelectionAllowed(true);
								
								cbHora.addItem("08:00");
								cbHora.addItem("08:30");
								cbHora.addItem("09:00");
								cbHora.addItem("09:30");
								cbHora.addItem("10:00");
								cbHora.addItem("10:30");
								cbHora.addItem("11:00");
								cbHora.addItem("11:30");									
								cbHora.addItem("14:00");
								cbHora.addItem("14:30");
								cbHora.addItem("15:00");
								cbHora.addItem("15:30");
								cbHora.addItem("16:00");
								cbHora.addItem("16:30");
								cbHora.addItem("17:00");
								cbHora.addItem("17:30");
								cbHora.addItem("18:00");
								cbHora.addItem("18:30");
								cbHora.addItem("19:00");
								cbHora.addItem("19:30");
								cbHora.addItem("20:00");
								
								cbHora.addValueChangeListener(new Property.ValueChangeListener() {
									
									@Override
									public void valueChange(ValueChangeEvent event) {
										if(cbHora.getValue().equals("08:00") || cbHora.getValue().equals("08:30") || cbHora.getValue().equals("09:00") ||
												cbHora.getValue().equals("09:30") || cbHora.getValue().equals("10:00") || cbHora.getValue().equals("10:30") ||
												cbHora.getValue().equals("11:00") || cbHora.getValue().equals("11:30")){
											cbTurno.select("MANHA");
										}else{
											cbTurno.select("TARDE");
										}	
									}
								});
																			
								addComponent(cbHora);
								
								
							}
						});
						
						addComponent(new FormLayout(){					
										{
											setStyleName("form-cutom-new");
											setMargin(true);
											setSpacing(true);
												
											cbTurno = new ComboBox("Turno");					
											cbTurno.setNullSelectionAllowed(false);
											cbTurno.setRequired(true);	
											cbTurno.setTextInputAllowed(false);
											cbTurno.addItem("MANHA");
											cbTurno.addItem("TARDE");
											cbTurno.addItem("INTEGRAL");
											cbTurno.select("INTEGRAL");
											
											addComponent(cbTurno);					
										}
								});
					}
				});	
		
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				txtMotivo = new TextArea("Motivo");
				txtMotivo.setRequired(true);				
				//txtMotivo.set
				txtMotivo.setWidth("464px");
				txtMotivo.setHeight("100px");
				txtMotivo.setStyleName("caption-align");
				
				addComponent(txtMotivo);
			}
		});
		
		
		
	}
	
	
	
	private JPAContainer<Usuario> getUsuarios(){
		JPAContainer<Usuario> container = JPAContainerFactory.make(Usuario.class, ConnUtil.getEntity());
		
		return container;
	}


	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(dfPrevisao.isValid() && valid_data && cbHora.isValid() && cbTurno.isValid() && txtMotivo.isValid()){
					try {	
						
//						if(cbTurno.getValue() != null){
//							item.getItemProperty("turno").setValue(cbTurno.getValue());
//						}
						
						Date dataNova;
						if(cbHora.getValue() != null){
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");							
							dataNova = sdf.parse(sdf.format(dfPrevisao.getValue()).replaceAll("00:00:00", "")+""+cbHora.getValue()+":00");
						}else{
							dataNova = dfPrevisao.getValue();
						}
										
						fireEvent(new ReagendarRoteirizacaoEvent(getUI(),dataNova,txtMotivo.getValue(),cbTurno.getValue().toString(), true));						
							
					} catch (Exception e) {					
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
					}
				}else{
					
					if(!txtMotivo.isValid()){
						txtMotivo.addStyleName("invalid-txt");
					}else{
						txtMotivo.removeStyleName("invalid-txt");
					}
					
					if(!valid_data){
						dfPrevisao.addStyleName("invalid-txt");
					}else{
						dfPrevisao.removeStyleName("invalid-txt");
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
				
				if(!dfPrevisao.isModified()){
					fireEvent(new ReagendarRoteirizacaoEvent(getUI(), null,null,null, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(dfPrevisao.isValid() && valid_data && cbHora.isValid() && cbTurno.isValid() && txtMotivo.isValid()){
									try {				
										
										fieldGroup.commit();	
										fireEvent(new ReagendarRoteirizacaoEvent(getUI(), dfNovaData.getValue(),txtMotivo.getValue(),cbTurno.getValue().toString(), true));					
											
									} catch (Exception e) {					
										e.printStackTrace();					
										Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
									}
								}else{
									
									if(!txtMotivo.isValid()){
										txtMotivo.addStyleName("invalid-txt");
									}else{
										txtMotivo.removeStyleName("invalid-txt");
									}
																		
									if(!valid_data){
										dfPrevisao.addStyleName("invalid-txt");
									}else{
										dfPrevisao.removeStyleName("invalid-txt");
									}
									
									Notify.Show_Invalid_Submit_Form();
									
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
	
	
	public void addListerner(ReagendarRoteirizacaoListerner target){
		try {
			Method method = ReagendarRoteirizacaoListerner.class.getDeclaredMethod("onClose", ReagendarRoteirizacaoEvent.class);
			addListener(ReagendarRoteirizacaoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ReagendarRoteirizacaoListerner target){
		removeListener(ReagendarRoteirizacaoEvent.class, target);
	}
	public static class ReagendarRoteirizacaoEvent extends Event{
		
		private Date novaData;
		private String motivo;
		private String turno;
		
		private boolean confirm;
		
		public ReagendarRoteirizacaoEvent(Component source, Date novaData,String motivo,String turno, boolean confirm) {
			super(source);
			this.novaData = novaData;	
			this.motivo = motivo;
			this.turno = turno;
			this.confirm = confirm;
		}		

		public String getMotivo(){
			return motivo;
		}
		public String getTurno(){
			return turno;
		}
		public Date getNovaData() {
			return novaData;
		}
		
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ReagendarRoteirizacaoListerner extends Serializable{
		public void onClose(ReagendarRoteirizacaoEvent event);
	}

	
}


