package com.digital.opuserp.view.modulos.financeiro.preferencias;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.TabelasPreco;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.categoria.CategoriaEditor.CategoriaEvent;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class DefinirTabelaPrecoEditor extends Window {
	
		
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;	
	VerticalLayout vlRoot;
	
	private Item item;
	private TextField txtDesconto;
	private FieldGroup fg;
	
	public DefinirTabelaPrecoEditor(Item item, String title, boolean modal){
				
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
	
	private JPAContainer<TabelasPreco> getTabelasPreco(){
		JPAContainer<TabelasPreco> tabelas = JPAContainerFactory.makeBatchable(TabelasPreco.class, ConnUtil.getEntity());
		tabelas.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
		
		return tabelas;
	}
	
	public void buildLayout(){
		

		fg = new FieldGroup(item);
					
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					final ComboBox cbTabelaPreco = new ComboBox("Tabela de Preço", getTabelasPreco());
					cbTabelaPreco.setStyleName("caption-align-credito-cliente-deflimitecredito");
					cbTabelaPreco.setNullSelectionAllowed(true);
					cbTabelaPreco.setConverter(new SingleSelectConverter(cbTabelaPreco));
					cbTabelaPreco.setItemCaptionPropertyId("nome");
					cbTabelaPreco.setRequired(false);
					cbTabelaPreco.setImmediate(true);					
					cbTabelaPreco.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							
							if(txtDesconto != null){
								txtDesconto.setReadOnly(false);
								
								if(cbTabelaPreco.getValue() != null){								
									txtDesconto.setValue(Real.formatDbToString(cbTabelaPreco.getItem(cbTabelaPreco.getValue()).getItemProperty("desconto").getValue().toString()));
								}else{
									txtDesconto.setValue("");
								}
								txtDesconto.setReadOnly(true);
							}
						}
					});
										
					cbTabelaPreco.focus();
					
					addComponent(cbTabelaPreco);
					fg.bind(cbTabelaPreco, "tabela_preco");
									
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
										
					txtDesconto = new TextField("Desconto(%)");				
					txtDesconto.setStyleName("caption-align-credito-cliente-deflimitecredito");	
					txtDesconto.setReadOnly(true);
					
					if(item.getItemProperty("tabela_preco").getValue() != null){
						TabelasPreco tabela = (TabelasPreco) item.getItemProperty("tabela_preco").getValue();
						
						txtDesconto.setReadOnly(false);
						txtDesconto.setValue(Real.formatDbToString(String.valueOf(tabela.getDesconto())));
						txtDesconto.setReadOnly(true);
					}
					
					addComponent(txtDesconto);
									
				}
		});
		
		
		
		
	}

	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
			
				if(fg.isValid()){
					try {				
						
						fg.commit();				
						fireEvent(new DefinirTabelaPrecoEvent(getUI(), item, true));					
						
						Notification.show("Tabela de Preço Definida com Sucesso!");
						close();
					}catch (Exception e) {	
						e.printStackTrace();
						//Notification.show("Não foi Possivel Salvar as Alterações!");
					}
				}else{
					
					for (Field<?> field: fg.getFields()) {
						
						if(!field.isValid()){
							field.addStyleName("invalid-txt");
						}else{
							field.removeStyleName("invalid-txt");
						}
					}
					
					Notification.show("Não é Possivel Salvar, Verifique os Campos Obrigatórios/Inválidos e Tente Novamente!");
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
	
	
	public void addListerner(DefinirTabelaPrecoListerner target){
		try {
			Method method = DefinirTabelaPrecoListerner.class.getDeclaredMethod("onClose", DefinirTabelaPrecoEvent.class);
			addListener(DefinirTabelaPrecoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(DefinirTabelaPrecoListerner target){
		removeListener(DefinirTabelaPrecoEvent.class, target);
	}
	public static class DefinirTabelaPrecoEvent extends Event{

		private boolean confirm;
		private Item item;
		
		public DefinirTabelaPrecoEvent(Component source, Item item, boolean confirm) {
			super(source);			
			this.confirm = confirm;			
			this.item = item;
		}

		public boolean isConfirm() {
			return confirm;
		}	
		
		public Item getItem(){
			return item;
		}
	}
	public interface DefinirTabelaPrecoListerner extends Serializable{
		public void onClose(DefinirTabelaPrecoEvent event);
	}

	
}
