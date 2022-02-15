package com.digital.opuserp.view.modulos.ordemServico.pedidoServico;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FormaPgtoDAO;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
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
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConfiguracaoPgtoEditor extends Window {

	
	private Button btSalvar;
	private Button btCancelar;
		
	private FieldGroup fieldGroup;
	private VerticalLayout vlRoot;
	
	private ComboBox cbFormaPgto;
	private TextField txtDescontoPorc;
	private TextField txtDescontoReal;
	private TextField txtAcrescimoPorc;
	private TextField txtAcrescimoReal;
	
	private TextField txtParcelamento;
	private DateField dfPrimeiroVenc;
	
	
	public ConfiguracaoPgtoEditor( String title, boolean modal){
		
				
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
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					cbFormaPgto = new ComboBox("Forma Pgto.", getFormaPgto());
					cbFormaPgto.setStyleName("caption-align");
					cbFormaPgto.setNullSelectionAllowed(false);
					cbFormaPgto.setItemCaptionPropertyId("nome");
					cbFormaPgto.setRequired(true);
					cbFormaPgto.setImmediate(true);
					cbFormaPgto.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							
						}
					});
					
					addComponent(cbFormaPgto);	
				}
			});
			
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtDescontoPorc = new TextField("Desconto");
					txtDescontoPorc.setStyleName("caption-align");
					txtDescontoPorc.setRequired(true);
					txtDescontoPorc.setId("txtDescontoPorc");
					
					addComponent(txtDescontoPorc);	
					JavaScript.getCurrent().execute("$('#txtDescontoPorc').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtAcrescimoReal = new TextField("Acréscimo");
					txtAcrescimoReal.setStyleName("caption-align");
					txtAcrescimoReal.setRequired(true);
					txtAcrescimoReal.setId("txtAcrescimoReal");
					
					addComponent(txtAcrescimoReal);	
					JavaScript.getCurrent().execute("$('#txtAcrescimoReal').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					txtParcelamento = new TextField("Nº Parcelas");
					txtParcelamento.setStyleName("caption-align");
					txtParcelamento.setRequired(true);
					txtParcelamento.setId("txtParcelamento");
					txtParcelamento.setReadOnly(true);
										
					addComponent(txtParcelamento);	
					JavaScript.getCurrent().execute("$('#txtParcelamento').mask('00')");
				}
			});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					dfPrimeiroVenc = new DateField("Primeiro Venc.");
					dfPrimeiroVenc.setStyleName("caption-align");
					dfPrimeiroVenc.setRequired(true);
					dfPrimeiroVenc.setDateFormat("dd/MM/yyyy");
					dfPrimeiroVenc.setReadOnly(true);
					
					addComponent(dfPrimeiroVenc);	
				}
			});
		
				
		
	}
	private JPAContainer<FormasPgto> getFormaPgto(){
		JPAContainer<FormasPgto> container = JPAContainerFactory.make(FormasPgto.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		return container;
	}
	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(cbFormaPgto.isValid() && txtDescontoPorc.isValid() && txtAcrescimoReal.isValid()){
					try {				
						
						
						if(txtParcelamento.isValid() && dfPrimeiroVenc.isValid()){
							fireEvent(new ConfigFormaPgtoEvent(getUI(), FormaPgtoDAO.find((Integer)cbFormaPgto.getItem(cbFormaPgto.getValue()).getItemProperty("id").getValue()), Real.formatStringToDBDouble(txtDescontoPorc.getValue()), Real.formatStringToDBDouble(txtAcrescimoReal.getValue()), Long.parseLong(txtParcelamento.getValue()), dfPrimeiroVenc.getValue(), true));
						}else{
							fireEvent(new ConfigFormaPgtoEvent(getUI(), FormaPgtoDAO.find((Integer)cbFormaPgto.getItem(cbFormaPgto.getValue()).getItemProperty("id").getValue()), Real.formatStringToDBDouble(txtDescontoPorc.getValue()), Real.formatStringToDBDouble(txtAcrescimoReal.getValue()), null, null, true));
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
					}
				}else{
					
					
					Notify.Show_Invalid_Submit_Form();
				}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			
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
			
			
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new ConfigFormaPgtoEvent(getUI(), (FormasPgto)cbFormaPgto.getItem(cbFormaPgto.getValue()), Real.formatStringToDBDouble(txtDescontoPorc.getValue()), Real.formatStringToDBDouble(txtAcrescimoReal.getValue()), Long.parseLong(txtParcelamento.getValue()), dfPrimeiroVenc.getValue(), false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								try {
									fieldGroup.commit();				
									fireEvent(new ConfigFormaPgtoEvent(getUI(), (FormasPgto)cbFormaPgto.getItem(cbFormaPgto.getValue()), Real.formatStringToDBDouble(txtDescontoPorc.getValue()), Real.formatStringToDBDouble(txtAcrescimoReal.getValue()), Long.parseLong(txtParcelamento.getValue()), dfPrimeiroVenc.getValue(), true));									
								} catch (Exception e) {
									System.out.println("Erro ao cadastrar ou alterar : "+e.getMessage()+".\n Causado por: "+e.getCause());
									Notification.show("Não foi Possivel Salvar as Alterações!");
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new ConfigFormaPgtoEvent(getUI(), null, 0, 0, null, null, false));
								
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		
		
		return btCancelar;
	}
	
	
	public void addListerner(ConfigFormaPgtoListerner target){
		try {
			Method method = ConfigFormaPgtoListerner.class.getDeclaredMethod("onClose", ConfigFormaPgtoEvent.class);
			addListener(ConfigFormaPgtoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ConfigFormaPgtoListerner target){
		removeListener(ConfigFormaPgtoEvent.class, target);
	}
	public static class ConfigFormaPgtoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		private FormasPgto formaPgto;
		private double desconto;
		private double acrescimo;
		private Long nParc;
		private Date dataPriVenc;
		
		public ConfigFormaPgtoEvent(Component source, FormasPgto formapgto, double desc, double acre,Long parc, Date primVenc,boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;		
			
			this.formaPgto = formapgto;
			this.desconto = desc;
			this.acrescimo = acre;
			this.nParc = parc;
			this.dataPriVenc = primVenc;
		}
		
		public Date getDataPriVenc(){
			return dataPriVenc;
		}
		
		public Long getnParc(){
			return nParc;
		}
		
		public double getAcrescimo(){
			return acrescimo;
		}
		
		public double getDesconto(){
			return desconto;			
		}
		
		public FormasPgto getFormaPgto(){
			return formaPgto;
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ConfigFormaPgtoListerner extends Serializable{
		public void onClose(ConfigFormaPgtoEvent event);
	}

	
}
