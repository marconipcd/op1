package com.digital.opuserp.view.modulos.financeiro.preferencias;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.ParametrosBoleto;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class DefinirTaxaBoletoEditor extends Window {

	ParametrosBoleto pp;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
	
	EntityManager em;
	Integer codCliente;
	
	private ComboBox cbParametro;
	
	public DefinirTaxaBoletoEditor(Integer codCliente, String title, boolean modal){
				
		this.codCliente = codCliente;
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
			
		em = ConnUtil.getEntity();
		Query q = em.createQuery("select cc from ParametrosBoleto cc where cc.cliente_id =:cliente", ParametrosBoleto.class);
								  
		q.setParameter("cliente", codCliente);
		
		if(q.getResultList().size() == 1){
			
			pp = (ParametrosBoleto)q.getSingleResult();
			
		}
		
		
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
														
					cbParametro = new ComboBox ("Cobrar Taxa");				
					cbParametro.setRequired(true);		
					cbParametro.setStyleName("caption-align-credito-cliente-deflimitecredito");
					cbParametro.focus();
					cbParametro.setNullSelectionAllowed(false);
					cbParametro.addItem("SIM");
					cbParametro.addItem("NÃO");
					
					if(pp == null){
						cbParametro.select("NÃO");
					}else{
						if(pp.getCobrar_taxa_bancaria()){
							cbParametro.select("SIM");
						}else{
							cbParametro.select("NÃO");
						}
					}
					

					addComponent(cbParametro);
					
				}
			});
	
		
	}

	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//if(valor_credito && valor_limite != null){
					
					try {										
						
						if(pp != null){
							if(cbParametro.getValue().toString().equals("SIM")){
								pp.setCobrar_taxa_bancaria(true);
							}else{
								pp.setCobrar_taxa_bancaria(false);
							}
							
							em.getTransaction().begin();
							em.merge(pp);
							em.getTransaction().commit();
						}else{
							em.getTransaction().begin();
							
							if(cbParametro.getValue().toString().equals("SIM")){
								em.persist(new ParametrosBoleto(null, codCliente, true));
							}else{
								em.persist(new ParametrosBoleto(null, codCliente, false));
							}
							
							em.getTransaction().commit();					
						}
										
						fireEvent(new DefinirTaxaBoletoEvent(getUI(),true));					
						Notification.show("Taxa de Boleto Definida com Sucesso!");
						
						close();
					} catch (Exception e) {					
						//LogErrorDAO.add(new LogError(null, "CategoriaEditor.java", "buildBtSalvar()", e.toString(),null,OpusERP4UI.EMPRESA_SELECIONADA, OpusERP4UI.USUARIO_LOGADO));					
						Notification.show("Não foi Possivel Salvar as Alterações!");
					}
				//}else{
				//	Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
				//}
				
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
	
	
	public void addListerner(DefinirTaxaBoletoListerner target){
		try {
			Method method = DefinirTaxaBoletoListerner.class.getDeclaredMethod("onClose", DefinirTaxaBoletoEvent.class);
			addListener(DefinirTaxaBoletoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(DefinirTaxaBoletoListerner target){
		removeListener(DefinirTaxaBoletoEvent.class, target);
	}
	public static class DefinirTaxaBoletoEvent extends Event{

		private boolean confirm;
		
		public DefinirTaxaBoletoEvent(Component source, boolean confirm) {
			super(source);			
			this.confirm = confirm;			
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface DefinirTaxaBoletoListerner extends Serializable{
		public void onClose(DefinirTaxaBoletoEvent event);
	}

	
}
