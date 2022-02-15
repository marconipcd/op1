package com.digital.opuserp.view.util;

import java.lang.reflect.Method;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.dao.GrupoProdutoDAO;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.FornecedorUtil.FornecedorEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class GrupoProdutoUtil extends Window {

	//JPAContainer<GrupoProduto> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;
	
	Integer codEmpresa;
	
	public GrupoProdutoUtil(boolean modal, boolean center, Integer codEmpresa){
		
		
		
		super("Selecione um Grupo de Produto");
		
		this.codEmpresa = codEmpresa;
		
		setWidth("830px");
		setHeight("360px");
		setIcon(new ThemeResource("icons/search-32.png"));
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);
						
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				//addComponent(buildTextField());
				
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						addComponent(buildTextField());				
						addComponent(buildBtBuscar());
						setExpandRatio(tfBusca, 1.0f);
					}
				});
				
				addComponent(buildTb());
				setExpandRatio(tb, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		});
	}
	
	private void atualizarTabela(){
		List<GrupoProduto> grupos = GrupoProdutoDAO.getGrupos(tfBusca.getValue());
		
		tb.removeAllItems();
		for (GrupoProduto f: grupos) {
			tb.addItem(new Object[]{
					f.getId().toString() != null ? f.getId().toString() : "", 
					f.getNome_grupo() != null ? f.getNome_grupo() : ""}, 
					tb.getItemIds().size()+1);
		}
	
	}
	
	public Button buildBtBuscar(){
		Button bt = new Button("Buscar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				atualizarTabela();
			}
		});
		bt.setStyleName(Reindeer.BUTTON_SMALL);
		return bt;
	}
	
	public Table buildTb(){
		tb = new Table();
		tb.setWidth("100%");
		tb.setHeight("230px");
		tb.setSelectable(true);
		tb.setColumnHeader("nome_grupo", "Grupo");
		tb.setColumnCollapsingAllowed(false);		
		
		tb.setColumnCollapsingAllowed(true);
		tb.addContainerProperty("id", String.class, "");
		tb.addContainerProperty("nome_grupo", String.class, "");
		tb.setColumnCollapsed("id", true);
		
		
		tb.addListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick() && tb.getValue() != null){
					Integer codGrupo= Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());			
					GrupoProduto grupo = GrupoProdutoDAO.find(codGrupo);				
				
					fireEvent(new GrupoUtilEvent(getUI(), grupo));
					close();
					
				}
			}
		});
		
		atualizarTabela();
		return tb;
	}
	
	
	public TextField buildTextField() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				
			}
		});
		return tfBusca;
	}
	
	private Button buildBtSalvar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tb.getValue()!= null){
					Integer codFornecedor= Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());			
					GrupoProduto grupo = GrupoProdutoDAO.find(codFornecedor);				
				
					fireEvent(new GrupoUtilEvent(getUI(), grupo));
					close();
				}	
			}	
		});
		
		ShortcutListener slbtOK = new ShortcutListener("Ok",ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};

		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}

	private Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}	
		});
		
			ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}

	
	
	public void addListerner(GrupoUtilListerner target){
		try {
			Method method = GrupoUtilListerner.class.getDeclaredMethod("onSelected",GrupoUtilEvent.class);
			addListener(GrupoUtilEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(GrupoUtilListerner target){
		removeListener(GrupoUtilEvent.class, target);
	}
	public static class GrupoUtilEvent extends Event{

		private GrupoProduto grupo;
		
		public GrupoUtilEvent(Component source, GrupoProduto grupo) {
			super(source);		
			this.grupo  = grupo;
		}

		public GrupoProduto getGrupo() {
			return grupo;
		}		
	}
	public interface GrupoUtilListerner{
		public void onSelected(GrupoUtilEvent event);
	}
}
