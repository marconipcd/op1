package com.digital.opuserp.view.modulos.estoque.produto;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.AlteracoesProdutoDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SerialDAO;
import com.digital.opuserp.dao.SerialProdutoDAO;
import com.digital.opuserp.domain.AlteracoesProduto;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.Validator;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


public class ListarSerial extends Window{

	
	
	private Button btSalvar;
	private Button btExcluir;
	private Button btCancelar;
	private FormLayout flPrincipal;

	private VerticalLayout vlRoot;
	
	private Table tb;
	private boolean serialValido;
	Integer codProduto;
	TextField tfFiltro; 
	TextField txtSerial; 
	ComboBox cbStatus;
	
		public ListarSerial(Integer codProduto,boolean modal){
		this.codProduto = codProduto;
		
		setCaption("Serial");
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		setWidth("560px");
		setHeight("422px");
		
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
								
				hlButtons.addComponent(buildBtSalvar());
				hlButtons.addComponent(buildBtExcluir());
				hlButtons.addComponent(buildBtCancelar());
								
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
				
		buildLayout();	
		
	}
		
	List<SerialProduto> seriais = new ArrayList<>();
	public void buildLayout(){
		
		
		vlRoot.addComponent(
			new FormLayout(){					
			{								
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						addComponent(buildStatus());
						addComponent(buildTfFiltro());
						setExpandRatio(tfFiltro, 1.0f);
					}
				});			
			}
		});

		vlRoot.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);								
				
				Produto produto = ProdutoDAO.find(codProduto);
				TextField txtCodProd = new TextField("Produto");				
				txtCodProd.setWidth("100%");				
				txtCodProd.setStyleName("caption-align-editar-serialLista");
				txtCodProd.setValue(produto.getId().toString()+" - "+produto.getNome());
				txtCodProd.setReadOnly(true);			
				
				addComponent(txtCodProd);	
			}
		});	
		
		
		
//		HorizontalLayout hl = new HorizontalLayout();
//		hl.setWidth("500px");		
		vlRoot.addComponent(new FormLayout(){					

			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);								
				
				txtSerial = new TextField("Serial");				
				txtSerial.setWidth("200px");				
				txtSerial.setStyleName("caption-align-editar-serialLista");
				txtSerial.setNullRepresentation("");
				txtSerial.setRequired(true);
				txtSerial.setImmediate(true);					
				txtSerial.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						if(event.getText() != null && !event.getText().equals("") && !event.getText().isEmpty()){
							if(!SerialDAO.checkSerialExist(event.getText())){
								serialValido = true;
								event.getComponent().removeStyleName("invalid-serial");
								event.getComponent().setStyleName("valid-serial");
							}else{
								event.getComponent().removeStyleName("valid-serial");
								event.getComponent().addStyleName("invalid-serial");
								serialValido = false;								
							}
						}
					}
				});									
				addComponent(txtSerial);	
			}
		});							

//		vlRoot.addComponent(hl);
		
		vlRoot.addComponent(
			flPrincipal = new FormLayout(){					
			{				
				addComponent(buildTbGeneric());					
			}
		});			
	}
	
	public Table buildTbGeneric() {
		tb = new Table(null, getSeriais());		
		tb.setWidth("494px");
		tb.setHeight("12em");
		tb.setSelectable(true);						
		tb.setImmediate(true);
		
		tb.setVisibleColumns(new Object[]{"serial"});
		tb.setColumnHeader("serial", "Serial");
		
		return tb;
	}
	
		
	
	public ComboBox buildStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.addItem("ATIVO");
		cbStatus.addItem("VENDIDO");
		cbStatus.setImmediate(true);
		cbStatus.select("ATIVO");
		cbStatus.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
					addfilter(tfFiltro.getValue());				
			}
		});
		
		return cbStatus;
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
					addfilter(event.getText());
				}
			}
		});
		
		return tfFiltro;
	}

	private void addfilter(String s){
		container_seriais.setApplyFiltersImmediately(false);
		container_seriais.removeAllContainerFilters();	
		if(codProduto!=null){
			container_seriais.addContainerFilter(Filters.eq("produto", codProduto));
		}
		container_seriais.addContainerFilter(Filters.eq("status", "ATIVO"));		
		container_seriais.addContainerFilter(new Like("serial", "%"+s+"%", false));
		
		if(cbStatus.getValue().toString() != null){
			container_seriais.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		}
		
		container_seriais.applyFilters();
	}
	
	JPAContainer<SerialProduto> container_seriais;
	private JPAContainer<SerialProduto> getSeriais(){
		container_seriais = JPAContainerFactory.make(SerialProduto.class, ConnUtil.getEntity());
		
		if(codProduto!=null){
			container_seriais.addContainerFilter(Filters.eq("produto", codProduto));
		}	
		
		return container_seriais;
	}
	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				Produto produto = ProdutoDAO.find(codProduto);
				
				if(serialValido == true && codProduto != null && !txtSerial.getValue().equals("")){
					SerialDAO.addSerial(codProduto, txtSerial.getValue());
					txtSerial.setValue("");
					Notify.Show("Serial Cadastrado Com Sucesso!", Notify.TYPE_SUCCESS);
					
					AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "CADASTROU SERIAL PRODUTO", produto,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
				
				}else{
					Notify.Show("Serial não pode ser Cadastrado!", Notify.TYPE_ERROR);
				}
				flPrincipal.replaceComponent(tb, buildTbGeneric());
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
	
	public Button buildBtExcluir() {
			
		btExcluir  = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				
				if(tb.getValue()!=null){
					
				final Produto produto = ProdutoDAO.find(codProduto);
	
				GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Serial Selecionado?", true, true);
				gDialog.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						if(event.isConfirm()){
							
							container_seriais.removeItem(tb.getValue());
							container_seriais.commit();
							Notify.Show("Serial Excluído com Sucesso!", Notify.TYPE_SUCCESS);
							
							AlteracoesProdutoDAO.save(new AlteracoesProduto(null, "EXCLUIU SERIAL PRODUTO", produto,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));

							tb.focus();
							
							LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Serial"));
						}							
					}
				});
				
				getUI().addWindow(gDialog);
				}
			}
		});
		
		return btExcluir;
	}

	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				fireEvent(new SerialPedidoEvent(getUI(),seriais, false));
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
	
	
	public void addListerner(SerialPedidoListerner target){
		try {
			Method method = SerialPedidoListerner.class.getDeclaredMethod("onClose", SerialPedidoEvent.class);
			addListener(SerialPedidoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(SerialPedidoListerner target){
		removeListener(SerialPedidoEvent.class, target);
	}
	public static class SerialPedidoEvent extends Event{
				
		private boolean confirm;
		private List<SerialProduto> seriais;
		
		public SerialPedidoEvent(Component source,List<SerialProduto> seriais, boolean confirm) {
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
	public interface SerialPedidoListerner extends Serializable{
		public void onClose(SerialPedidoEvent event);
	}

	
}

