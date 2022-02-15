package com.digital.opuserp.view.modulos.contratoManutencao;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PlanoManutencaoEditor extends Window {

	private Item item;
	private Button btSalvar;
	private Button btCancelar;
	
	private FieldGroup fieldGroup;
	private VerticalLayout vlRoot;
	
	private TextField txtValorMensal;
	private TextField txtLimiteMensal;
	private TextField txtQtdAtivos;
	private TextField txtVigencia;
	
	public PlanoManutencaoEditor(final Item item, String title, boolean modal){
	
		this.item = item;
		
		setWidth("380px");
		
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
				
				HorizontalLayout hlButton = new HorizontalLayout();
				hlButton.setStyleName("hl_button_bottom");
				hlButton.setSpacing(true);
				hlButton.setMargin(true);
				
				
				hlButton.addComponent(buildBtFechar());
				hlButton.addComponent(buildBtSalvar());
				
				
				addComponent(hlButton);
				setComponentAlignment(hlButton, Alignment.BOTTOM_RIGHT);
			}
		});
		
		buildLayout();
	}
	private void buildLayout(){
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true); 
				
				TextField txtNome = new TextField("Nome");
				txtNome.setNullRepresentation("");
				txtNome.setStyleName("caption-align");
				txtNome.setRequired(true);
				txtNome.setWidth("171px");
				txtNome.focus();
				
				addComponent(txtNome);					
				fieldGroup.bind(txtNome, "nome");
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				txtValorMensal = new TextField("Valor Mensal");
				txtValorMensal.setStyleName("caption-align");
				txtValorMensal.setRequired(true);
				txtValorMensal.setId("txtValorMensal");
				txtValorMensal.addStyleName("align-currency");
				txtValorMensal.setWidth("80px");
				
				txtValorMensal.setValue(Real.formatDbToString(item.getItemProperty("valor_mensal").getValue().toString()));
				
				JavaScript.getCurrent().execute("$('#txtValorMensal').maskMoney({decimal:',',thousands:'.'})");
				
				addComponent(txtValorMensal); 
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				txtLimiteMensal = new TextField("Limite Mensal");
				txtLimiteMensal.setNullRepresentation("");
				txtLimiteMensal.setStyleName("caption-align");
				txtLimiteMensal.setRequired(true);		
				txtLimiteMensal.setWidth("60px");
				txtLimiteMensal.setId("txtLimiteMensal");
				
				addComponent(txtLimiteMensal); 
				fieldGroup.bind(txtLimiteMensal, "limite_mensal");
				
				JavaScript.getCurrent().execute("$('#txtLimiteMensal').mask('00000000000000')");
				
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				txtQtdAtivos = new TextField("Nº Ativos");
				txtQtdAtivos.setNullRepresentation("");
				txtQtdAtivos.setStyleName("caption-align");
				txtQtdAtivos.setRequired(true);		
				txtQtdAtivos.setWidth("60px");
				txtQtdAtivos.setId("txtQtdAtivos");
				
				addComponent(txtQtdAtivos); 
				fieldGroup.bind(txtQtdAtivos, "numeroAtivos");
				
				JavaScript.getCurrent().execute("$('#txtQtdAtivos').mask('00000000000000')");
				
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				txtVigencia = new TextField("Vigência");
				txtVigencia.setNullRepresentation("");
				txtVigencia.setStyleName("caption-align");
				txtVigencia.setRequired(true);		
				txtVigencia.setWidth("60px");
				txtVigencia.setId("txtVigencia");
				
				addComponent(txtVigencia); 
				fieldGroup.bind(txtVigencia, "vigencia");
				
				JavaScript.getCurrent().execute("$('#txtVigencia').mask('00000000000000')");
				
			}
		});
		
		
		
	}
	
	private Button buildBtSalvar(){
		
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				concluir();
			}
		});
		
		btSalvar.addStyleName("default");
		
		return btSalvar;
	}
	
	private Button buildBtFechar(){
		
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(!fieldGroup.isModified()){
					fieldGroup.discard();
					fireEvent(new PlanoManutencaoEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							
							if(event.isConfirm()){
								concluir();
							}else{
								fieldGroup.discard();
								fireEvent(new PlanoManutencaoEvent(getUI(), item, false));
								close();
							}
						}
					});
					
					getUI().addWindow(gDialog);
				}
			}
		});
		
		return btCancelar;
	
	}
	
	private void concluir(){
		if(fieldGroup.isValid() && txtValorMensal.isValid() ){
			try{					
				fieldGroup.commit();
				item.getItemProperty("valor_mensal").setValue(Real.formatStringToDBDouble(txtValorMensal.getValue()));
				//item.getItemProperty("limite_mensal").setValue(Integer.parseInt(txtLimiteMensal.getValue()));
				
				fireEvent(new PlanoManutencaoEvent(getUI(), item, true));
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			 for (Field<?> field: fieldGroup.getFields()) {						
				if(!field.isValid()){
					field.addStyleName("invalid-txt");
				}else{
					field.removeStyleName("invalid-txt");
				}
			 }
			 
			 if(!txtValorMensal.isValid() || txtValorMensal.getValue().equals("0,00")){
				 txtValorMensal.addStyleName("invalid-txt");
			 }else{
				 txtValorMensal.removeStyleName("invalid-txt");
			 }
			 
//			 if(!txtLimiteMensal.isValid()){
//				 txtLimiteMensal.addStyleName("invalid-txt");
//			 }else{
//				 txtLimiteMensal.removeStyleName("invalid-txt");
//			 }
			 
			 Notify.Show_Invalid_Submit_Form();
		}
	}
	
	public void addListerner(PlanoManutencaoListerner target){
		try{
			Method method = PlanoManutencaoListerner.class.getDeclaredMethod("onClose", PlanoManutencaoEvent.class);
			addListener(PlanoManutencaoEvent.class, target, method);
		}catch(Exception e){
			System.out.println("Método não Encontrado!");
		}
	}
	
	public static class PlanoManutencaoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public PlanoManutencaoEvent(Component source, Item item, boolean confirm){
			super(source);
			
			this.item = item;
			this.confirm = confirm;
		}
		
		public Item getItem(){
			return item;
		}
		public boolean isConfirm(){
			return confirm;
		}
	}
	public interface PlanoManutencaoListerner extends Serializable{
		public void onClose(PlanoManutencaoEvent event);
	}
}
