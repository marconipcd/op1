package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesContratoDAO;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesContrato;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MudancaVencimentoEditor extends Window implements GenericEditor{

	Integer codAcesso;
	DateField novaData;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	boolean validarData = false;
	
	TextField tfValorProximoBoleto;
	
	String dateAntiga = "";
	TextField tfVencimentoAtual;
	Date vencBoleto = null;
	Date dataInstalacao = null;
	
	Item item;
	
	public MudancaVencimentoEditor(Item item,Integer codAcesso, String title, boolean modal){
		
		this.item = item;
		this.codAcesso = codAcesso;
		
		setWidth("630px");
		setHeight("273px");
		
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
	
	private void buildLayout() {
	
	fieldGroup = new FieldGroup();
	
		
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				TextField tfCodContrato = new TextField("Contrato");
				tfCodContrato.setValue(item.getItemProperty("id").getValue().toString());
				tfCodContrato.setReadOnly(true);
				tfCodContrato.setWidth("62px");
				tfCodContrato.setStyleName("caption-align-mud-venc");
				
				addComponent(tfCodContrato);
			}
		});
	
	
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					Cliente cliente = (Cliente) item.getItemProperty("cliente").getValue();
					
					TextField tfNomeCliente = new TextField("Titular Atual");
					tfNomeCliente.setValue(cliente.getNome_razao());
					tfNomeCliente.setReadOnly(true);
					tfNomeCliente.setWidth("360px");
					tfNomeCliente.setStyleName("caption-align-mud-venc");
					
					addComponent(tfNomeCliente);
				}
		});		
	
				
		CredenciaisAcessoDAO cDAO = new CredenciaisAcessoDAO();	
		vencBoleto = cDAO.buscarDataVencBoleto(codAcesso);
		AcessoCliente ac = cDAO.getAcessoByCodAceso(codAcesso);
		dataInstalacao = ac.getData_instalacao();

		SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");

		if(vencBoleto != null){
			dateAntiga = form.format(vencBoleto);		
		}
				
		tfVencimentoAtual = new TextField("Próximo Vencimento");
		tfVencimentoAtual.setValue(dateAntiga);
		tfVencimentoAtual.setReadOnly(true);
		tfVencimentoAtual.setStyleName("caption-align-mud-venc");
		
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				addComponent(tfVencimentoAtual);
			}
		});
		
		novaData = new DateField("Novo Vencimento");				
		fieldGroup.bind(novaData, "data_vencimento");
		novaData.setRequired(true);
		novaData.setImmediate(true);
		novaData.focus();
		novaData.setDateFormat("dd/MM/yyyy");
		novaData.setStyleName("caption-align-mud-venc");
		novaData.setDescription("Informar data Maior ou igual que a Data de Hoje e menor que data do próximo vencimento.");
		
		novaData.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(novaData.getValue()!= null && !novaData.getValue().equals("")){

					Calendar dataProxima = Calendar.getInstance();
					if(vencBoleto!=null){
						dataProxima.setTime(vencBoleto);											
					}
					Date dtProxima = dataProxima.getTime();
							
					if(novaData.getValue()!=null && dataProxima != null && dataInstalacao != null){
						try{
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							
							DateTime dtHoje = new DateTime(sdf.parse(sdf.format(new Date())));
							DateTime dtNova = new DateTime(sdf.parse(sdf.format(novaData.getValue())));
							DateTime dtProxBoleto = new DateTime(sdf.parse(sdf.format(dtProxima)));
							
							Integer qtdDias1 = Days.daysBetween(dtHoje, dtNova).getDays();
							Integer qtdDias2 = Days.daysBetween(dtNova, dtProxBoleto).getDays();
									
							
							if(qtdDias1 >= 0 && qtdDias2 > 0){
							//if(new Date().compareTo(novaData.getValue()) <= 1 && novaData.getValue().compareTo(new DateTime(dtProxima).toDate()) < 0 && novaData.getValue().compareTo(dataInstalacao) > 0 ){
								
								validarData = true;
							}else {
								validarData = false;				
							}
						
						}catch(Exception e){
							e.printStackTrace();
							validarData = false;
						}
					}else{						
						Notify.Show("Data de Instalação do Cliente Não Encontrada!", Notify.TYPE_ERROR);
					}
					
						String valorBoleto = "";
						
					if(validarData ){
						ContasReceberDAO cDAO = new ContasReceberDAO();
						EntityItem<AcessoCliente> entityItem = (EntityItem<AcessoCliente>)item; 
						valorBoleto = cDAO.CalcularProRataBoletoAlteracaoVencimento(entityItem.getEntity(), novaData.getValue());
						tfValorProximoBoleto.setReadOnly(false);
						tfValorProximoBoleto.setValue(valorBoleto);					
						tfValorProximoBoleto.setReadOnly(true);
						tfValorProximoBoleto.addStyleName("");
					}else{
						tfValorProximoBoleto.setReadOnly(false);
						tfValorProximoBoleto.setValue("0,00");					
						tfValorProximoBoleto.setReadOnly(true);
						
						
						Notify.Show("Data Inválido, Selecione uma Diferente!", Notify.TYPE_ERROR);
					}
						
				btCancelar.focus();
				}
			
			}
		});

		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom-new");
				addComponent(novaData);
			}
		});
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);					
					
					tfValorProximoBoleto = new TextField("Valor do Próximo Boleto (R$)");				
					tfValorProximoBoleto.setReadOnly(true);
					tfValorProximoBoleto.setStyleName("caption-align-mud-venc");
					tfValorProximoBoleto.addStyleName("align-currency");
					tfValorProximoBoleto.setId("tfVlPlano");
					
					addComponent(tfValorProximoBoleto);					
					JavaScript.getCurrent().execute("$('#tfVlPlano').maskMoney({decimal:',',thousands:'.'})");
				}
			});

	}



	@Override
	public Button buildBtSalvar() {
	btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && validarData){					
						fireEvent(new MudancaVencimentoEvent(getUI(),  true,novaData.getValue()));				
				}else{					
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
					fireEvent(new MudancaVencimentoEvent(getUI(),  false,null));					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								fireEvent(new MudancaVencimentoEvent(getUI(),  true,novaData.getValue()));	
							}else{									
								fireEvent(new MudancaVencimentoEvent(getUI(),  false,null));													
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
	
	
	
	
	
	public void addListerner(MudancaVencimentoListerner target){
		try {
			Method method = MudancaVencimentoListerner.class.getDeclaredMethod("onClose", MudancaVencimentoEvent.class);
			addListener(MudancaVencimentoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(MudancaVencimentoListerner target){
		removeListener(MudancaVencimentoEvent.class, target);
	}
	public static class MudancaVencimentoEvent extends Event{		
		
		private boolean confirm;		
		private Date nova_data;
		
		public MudancaVencimentoEvent(Component source, boolean confirm, Date nova_data) {
			super(source);			
			this.confirm = confirm;	
			this.nova_data = nova_data;
		}
		
		public boolean isConfirm() {
			return confirm;
		}			
		
		public Date getNovaData(){
			return nova_data;
		}
		
	}
	public interface MudancaVencimentoListerner extends Serializable{
		public void onClose(MudancaVencimentoEvent event);
	}

	
	
}

