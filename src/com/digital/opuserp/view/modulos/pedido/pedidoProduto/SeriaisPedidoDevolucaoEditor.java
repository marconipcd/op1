package com.digital.opuserp.view.modulos.pedido.pedidoProduto;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.dao.SeriaisPedidoDAO;
import com.digital.opuserp.dao.SerialProdutoDAO;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.SeriaisPedido;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SeriaisPedidoDevolucaoEditor extends Window{
	
	private Button btSalvar;
	private Button btCancelar;
	private VerticalLayout vlRoot;
	private Table tb;	
	
	
	private EcfPreVendaDetalhe itemPedido;
	
	TextField tfFiltro;	
	List<SerialProduto> seriais = new ArrayList<>();
	JPAContainer<SerialProduto> container_seriais;
	
	public SeriaisPedidoDevolucaoEditor(EcfPreVendaDetalhe itemPedido, String title,boolean modal){
		this.itemPedido = itemPedido;
		
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
		
		vlRoot.removeAllComponents();
		vlRoot.addComponent(
			new FormLayout(){					
			{								
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");						
						addComponent(buildTfFiltro());
						setExpandRatio(tfFiltro, 1.0f);
					}
				});			
			}
		});
		
		vlRoot.addComponent(
			new FormLayout(){					
			{				
				addComponent(buildTbGeneric());					
			}
		});			
		
		vlRoot.addComponent(
				new FormLayout(){					
				{				
					addComponent(buildLbRegistros());					
				}
		});
	}
	private Label lbRegistros;
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(tb.getItemIds().size()) + " Seriais Encontrados");
		return lbRegistros;
	}
	public Table buildTbGeneric() {
		tb = new Table(null);		
		tb.setWidth("750px");
		tb.setHeight("25em");
		tb.setSelectable(true);						
		tb.setImmediate(true);
		
		tb.addContainerProperty("Cod", Integer.class, 0);
		tb.addContainerProperty("Serial", String.class, "");
		
		List<SeriaisPedido> seriais_pedido = SeriaisPedidoDAO.getSeriais(itemPedido);
		for (SeriaisPedido seriaisPedido : seriais_pedido) {
			tb.addItem(new Object[]{seriaisPedido.getSerial().getId(),seriaisPedido.getSerial().getSerial()}, tb.getItemIds().size()+1);
		}
		
		tb.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				
				final CheckBox chk = new CheckBox(null, false);
				chk.addStyleName("no-padding-left");
				chk.setImmediate(true);
				
				if(exist(source.getItem(itemId).getItemProperty("Serial").getValue().toString())){
					chk.setValue(true);
				}else{
					chk.setValue(false);
				}
				
				
				chk.addValueChangeListener(new Property.ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						
						Item item = source.getItem(itemId);
						if(chk.getValue()){
							SerialProduto s = SerialProdutoDAO.find((Integer)item.getItemProperty("Cod").getValue());
							seriais.add(s);
							
//							if(tipo.equals("PEDIDO")){
//								s.setStatus("VENDIDO");								
//							}else{
//								s.setStatus("ATIVO");																
//							}
//							
//							SerialProdutoDAO.save(s);
						}else{
							
							for (Object serial : seriais.toArray()) {
								SerialProduto s = ((SerialProduto)serial);
								if(s.getId().equals((Integer)item.getItemProperty("id").getValue())){									
									seriais.remove(serial);
									//s.setStatus("ATIVO");
									//SerialProdutoDAO.save(s);
								}
							}
						}
					}
				});
				
				
				
				VerticalLayout vl = new VerticalLayout();
				vl.addComponent(chk);
				
				return vl;
			}
		});

		tb.setColumnWidth("x", 20);
		
		return tb;
	}
	public boolean exist(String serial){
		for (SerialProduto sp : seriais) {
			if(sp.getSerial().equals(serial)){
				return true;
			}
		}
		
		return false;
	}
	
	private TextField buildTfFiltro(){
		tfFiltro = new TextField();
		tfFiltro.setWidth("100%");
		tfFiltro.setImmediate(true);
		tfFiltro.setDescription("buscar...");
		
		tfFiltro.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				if(event.getText() != null){
					//addfilter(event.getText());
				}
			}
		});
		
		return tfFiltro;
	}
    
	public Button buildBtSalvar() {
		
		btSalvar = new Button("Selecionar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
					try {				
						
						boolean allow_finsh = true;
						for (SerialProduto sp : seriais) {
							SerialProduto sps =  SerialProdutoDAO.find(sp.getId());
							if(!sps.getStatus().equals("VENDIDO")){
								allow_finsh = false;								
								seriais.remove(sp);
								//vlRoot.replaceComponent(tb, buildTbGeneric());
								//buildTbGeneric();
								buildLayout();								
								break;
							}
						}
						
						if(allow_finsh){
							fireEvent(new SerialPedidoDevolucaoEvent(getUI(),seriais, true));
							close();
						}
							
					} catch (Exception e) {					
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
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
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				fireEvent(new SerialPedidoDevolucaoEvent(getUI(),seriais, false));
				close();				
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
	public void addListerner(SerialPedidoDevolucaoListerner target){
		try {
			Method method = SerialPedidoDevolucaoListerner.class.getDeclaredMethod("onClose", SerialPedidoDevolucaoEvent.class);
			addListener(SerialPedidoDevolucaoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(SerialPedidoDevolucaoListerner target){
		removeListener(SerialPedidoDevolucaoEvent.class, target);
	}
	public static class SerialPedidoDevolucaoEvent extends Event{
				
		private boolean confirm;
		private List<SerialProduto> seriais;
		
		public SerialPedidoDevolucaoEvent(Component source,List<SerialProduto> seriais, boolean confirm) {
			super(source);		
			this.seriais = seriais;
			this.confirm = confirm;			
		}
		
		
		public List<SerialProduto> getSeriais(){
			return seriais;
		}
		
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface SerialPedidoDevolucaoListerner extends Serializable{
		public void onClose(SerialPedidoDevolucaoEvent event);
	}

	
}
