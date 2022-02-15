package com.digital.opuserp.util;

import java.lang.reflect.Method;
import java.util.List;

import com.digital.opuserp.dao.GrupoProdutoDAO;
import com.digital.opuserp.dao.MarcaDAO;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.Marca;
import com.digital.opuserp.view.util.GrupoProdutoUtil.GrupoUtilEvent;
import com.digital.opuserp.view.util.GrupoProdutoUtil.GrupoUtilListerner;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.themes.Reindeer;

public class MarcaUtil extends Window {

	//JPAContainer<GrupoProduto> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;
	
	Integer codEmpresa;
	
	public MarcaUtil(boolean modal, boolean center, Integer codEmpresa){
		
		
		
		super("Selecione uma Marca");
		
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
		List<Marca> marcas = MarcaDAO.getMarcas(tfBusca.getValue());
		
		tb.removeAllItems();
		for (Marca f: marcas) {
			tb.addItem(new Object[]{
					f.getId().toString() != null ? f.getId().toString() : "", 
					f.getNome() != null ? f.getNome() : ""}, 
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
		tb.setColumnHeader("nome", "Marca");
		tb.setColumnCollapsingAllowed(false);		
		
		tb.setColumnCollapsingAllowed(true);
		tb.addContainerProperty("id", String.class, "");
		tb.addContainerProperty("nome", String.class, "");
		tb.setColumnCollapsed("id", true);
		
		
		tb.addListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick() && tb.getValue() != null){
					Integer coMarcad= Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").toString());			
					Marca grupo = MarcaDAO.find(coMarcad);				
				
					fireEvent(new MarcaUtilEvent(getUI(), grupo));
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
					Integer codMarca= Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());			
					Marca grupo = MarcaDAO.find(codMarca);				
				
					fireEvent(new MarcaUtilEvent(getUI(), grupo));
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

	
	
	public void addListerner(MarcaUtilListerner target){
		try {
			Method method = MarcaUtilListerner.class.getDeclaredMethod("onSelected",MarcaUtilEvent.class);
			addListener(MarcaUtilEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(MarcaUtilListerner target){
		removeListener(MarcaUtilEvent.class, target);
	}
	public static class MarcaUtilEvent extends Event{

		private Marca marca;
		
		public MarcaUtilEvent(Component source, Marca marca) {
			super(source);		
			this.marca  = marca;
		}

		public Marca getMarca() {
			return marca;
		}		
	}
	public interface MarcaUtilListerner{
		public void onSelected(MarcaUtilEvent event);
	}
}
