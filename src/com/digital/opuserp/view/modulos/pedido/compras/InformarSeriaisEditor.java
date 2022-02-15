package com.digital.opuserp.view.modulos.pedido.compras;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.digital.opuserp.dao.SerialDAO;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class InformarSeriaisEditor extends Window implements GenericEditor {
	
	private Button btSave;
	private Button btFinalizar;
	private Button btCancelar;
		
	private FormLayout flPrincipal;
	private VerticalLayout vlRoot;
	
	private Table tb;
	
	
	Float qtd;
	
	public InformarSeriaisEditor(String title, boolean modal, Float qtd){
		this.qtd=qtd;
		
		setWidth("640px");
		setHeight("350px");
		
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
				
				final HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
							
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				
				addComponent(new HorizontalLayout(){
					{
						
						setWidth("100%");
						
					
						
						addComponent(hlButtons);
						setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
					}
				});
				
				
				
			}
		});
		
		
		
		buildLayout();			
		
	}
	
	
	
	
	public void buildLayout(){
			
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				HorizontalLayout hlBtTopo = new HorizontalLayout();
				
				final TextField txtSerial = new TextField();
				txtSerial.setWidth("536px");
				Button btAdd = new Button("Add", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						Pattern pattern = Pattern.compile("[A-Za-z0-9:]{1,}");
				        Matcher matcher = pattern.matcher(txtSerial.getValue().toString());
				 
				        if(matcher.matches()){
				        	if(!SerialDAO.checkSerialExist(txtSerial.getValue())){
								addItem(txtSerial.getValue());
								txtSerial.setValue("");
							}else{
								Notify.Show("O Serial Informado Já Existe!", Notify.TYPE_ERROR);
							}
				        }else{
				        	Notify.Show("O Serial Informado é Inválido!", Notify.TYPE_ERROR);
				        }
						
//						if(!SerialDAO.checkSerialExist(txtSerial.getValue())){
//							addItem(txtSerial.getValue());
//							txtSerial.setValue("");
//						}else{
//							Notify.Show("O Serial Informado Já Existe!", Notify.TYPE_ERROR);
//						}
					}
				});
				btAdd.addStyleName(Reindeer.BUTTON_SMALL);
				
				hlBtTopo.addComponent(txtSerial);
				hlBtTopo.addComponent(btAdd);
				
				addComponent(hlBtTopo);				
			}
		});			
		vlRoot.addComponent(
				new FormLayout(){					
				{				
					addComponent(buildTbGeneric());					
				}
		});				
	}
	
	HorizontalLayout hlBase;
	Label lbSubTotal;
	
	public Table buildTbGeneric() {
		tb = new Table();		
		tb.setWidth("567px");
		tb.setHeight("12em");
		tb.setSelectable(true);						
		tb.setImmediate(true);
		
		tb.addContainerProperty("Serial",String.class, null);
				
		
		
		tb.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				
				
				
				Button btDeletar = new Button(null, new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Remover este Item?", true,true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){		
									removerItem(source.getItem(itemId).getItemProperty("Serial").getValue().toString());
								}
							}
						});
						
						getUI().addWindow(gd);
					}
				});
				btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
				btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
				btDeletar.setDescription("Cancelar Item");
				
				return btDeletar;
			}
		});

		tb.setColumnWidth("x", 20);
		
		tb.focus();
		return tb;
	}
	
	private void removerItem(String s){
		if(tb != null){
			for(Object ob: tb.getItemIds().toArray()){
				if(tb.getItem(ob).getItemProperty("Serial").getValue().equals(s)){										
					tb.removeItem(ob);
					Notify.Show("Item Removido com Sucesso", Notify.TYPE_SUCCESS);
					break;
				}
			}
			
//			Notify.Show("Não foi Possivel Remove o Serial", Notify.TYPE_ERROR);
		}else{
			Notify.Show("Não foi Possivel Remove o Serial", Notify.TYPE_ERROR);
		}
	}
	
		
	
	private void addItem(String s){
		
		if(tb != null){	
			
			boolean exist= false;	
			Float qtd_antiga = new Float(0);
			for(Object ob: tb.getItemIds().toArray()){
				if(tb.getItem(ob).getItemProperty("Serial").getValue().equals(s)){										
					exist = true;
					break;
				}
			}
		  
			if(!exist){				
				tb.addItem(new Object[]{s}, tb.getItemIds().size()+1);
				
			}		
		}
		
	}
	
	
	
	
	@Override
	public Button buildBtSalvar() {
		
		btFinalizar = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(tb.getItemIds().size() == qtd){
					try {	
						
						List<String> seriais = new ArrayList<>();
						for(Object ob: tb.getItemIds().toArray()){					
							seriais.add(tb.getItem(ob).getItemProperty("Serial").getValue().toString());
						}
						
						fireEvent(new SerialItemEvent(getUI(),true, seriais));							
					} catch (Exception e) {					
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
					}
				}else{					
					Notify.Show_Invalid_Submit_Form();					
				}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.F10,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btFinalizar.click();
			}
		};
		
		btFinalizar.addShortcutListener(slbtOK);
		
		btFinalizar.setStyleName("default");
		return btFinalizar;
	}
	
	public Button buildBtSave(){
		btSave = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
								
			}
		});
		
		return btSave;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
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
	
	
	public void addListerner(SerialItemListerner target){
		try {
			Method method = SerialItemListerner.class.getDeclaredMethod("onClose", SerialItemEvent.class);
			addListener(SerialItemEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(SerialItemListerner target){
		removeListener(SerialItemEvent.class, target);
	}
	public static class SerialItemEvent extends Event{
				
		private boolean confirm;
		private List<String> seriais;
		
		public SerialItemEvent(Component source,  boolean confirm, List<String> seriais) {
			super(source);
			this.confirm = confirm;
			this.seriais = seriais;
		}
		
		public List<String> getSeriais(){
			return seriais;
		}
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface SerialItemListerner extends Serializable{
		public void onClose(SerialItemEvent event);
	}

	
}
