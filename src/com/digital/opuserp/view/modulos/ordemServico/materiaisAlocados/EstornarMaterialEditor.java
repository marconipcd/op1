package com.digital.opuserp.view.modulos.ordemServico.materiaisAlocados;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.MateriaisAlocadosDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.EstoqueMovel;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.MaterialUtil;
import com.digital.opuserp.view.util.MaterialUtil.MaterialEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class EstornarMaterialEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
		
	boolean validarData;
	boolean valid_data = false;
	
	EstoqueMovel estoqueMovel;
		
	public EstornarMaterialEditor(String title,EstoqueMovel estoqueMovel,boolean modal){
				
		this.estoqueMovel = estoqueMovel;
		this.setWidth("810px");
		this.setHeight("270px");
		
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
	
	private JPAContainer<Veiculos> getVeiculos(){
		JPAContainer<Veiculos> container = JPAContainerFactory.make(Veiculos.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		return container;
	}
	
	private TextField tfDescricaoMaterial;
	Produto material_selecionado;
	
	JPAContainer<Veiculos> containerVeiculos;
	ComboBox cbVeiculos;
	
	TextField txtQtd;
	TextField txtQtdEstoque;
	TextField tfCodMaterial;
	
	Table tb;
	
	TextField txtQtdTotal;
	TextField txtQtdEstornar;
	TextField txtQtdRestante;
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField txtVeiculo = new TextField("Veiculo");
					txtVeiculo.setStyleName("caption-align");
					txtVeiculo.setWidth("400px");
					txtVeiculo.setValue(estoqueMovel.getVeiculo().getId().toString()+", "+estoqueMovel.getVeiculo().getMarca()+", "+estoqueMovel.getVeiculo().getModelo()+", "+estoqueMovel.getVeiculo().getCor());
					txtVeiculo.setEnabled(false); 
					
					addComponent(txtVeiculo);				

				}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				TextField txtMaterial = new TextField("Material");
				txtMaterial.setStyleName("caption-align");
				txtMaterial.setWidth("600px");
				txtMaterial.setValue(estoqueMovel.getProduto().getNome());
				txtMaterial.setEnabled(false);
				
				addComponent(txtMaterial); 
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				txtQtdTotal = new TextField("Qtd. Total");
				txtQtdTotal.setStyleName("caption-align");
				txtQtdTotal.setWidth("80px");
				txtQtdTotal.setValue(Real.formatDbToString(String.valueOf(estoqueMovel.getQtd())));
				txtQtdTotal.setEnabled(false);
				
				addComponent(txtQtdTotal); 
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				txtQtdEstornar = new TextField("Qtd. Estornar");
				txtQtdEstornar.setId("txtQtdEstornar");
				txtQtdEstornar.setStyleName("caption-align");
				txtQtdEstornar.setWidth("80px");
				
				txtQtdEstornar.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						if(event.getText() != null && !event.getText().equals("") && !event.getText().equals("0,00")){
							
							
									double vlrTotal = Real.formatStringToDBDouble(txtQtdTotal.getValue());
									double vlrQtdEstornar = Real.formatStringToDBDouble(event.getText());
									
									if(vlrQtdEstornar <= vlrTotal){
										double vlrQtdRestante = vlrTotal - vlrQtdEstornar;
										
										txtQtdRestante.setEnabled(true);
										txtQtdRestante.setValue(Real.formatDbToString(String.valueOf(vlrQtdRestante)));
										txtQtdRestante.setEnabled(false);								
								    }
							
						}
					}
				});
				
				if(estoqueMovel.getProduto().getFracionar() == 1){					
					JavaScript.getCurrent().execute("$('#txtQtdEstornar').maskMoney({decimal:',',thousands:'.'})");
				}
				
				txtQtdEstornar.focus();
				addComponent(txtQtdEstornar); 
			}
		});
		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
				
				txtQtdRestante = new TextField("Qtd. Restante");
				txtQtdRestante.setStyleName("caption-align");
				txtQtdRestante.setWidth("80px");
				txtQtdRestante.setValue("0,00");
				txtQtdRestante.setEnabled(false); 
				
				addComponent(txtQtdRestante); 
			}
		});
		
	}
	
			
	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(txtQtdEstornar != null && txtQtdEstornar.getValue() != null){
					
					double qtdEstornar = Real.formatStringToDBDouble(txtQtdEstornar.getValue());
					double qtdAtual = estoqueMovel.getQtd();
					double qtdRestante = qtdAtual - qtdEstornar;
					
					estoqueMovel.setQtd(qtdRestante);
					
					fireEvent(new EstornarMaterialEvent(getUI(),estoqueMovel,qtdEstornar,true));						
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
												
				fieldGroup.discard();				
				fireEvent(new EstornarMaterialEvent(getUI(),null, 0,false));
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
	
	
	public void addListerner(EstornarMaterialListerner target){
		try {
			Method method = EstornarMaterialListerner.class.getDeclaredMethod("onClose", EstornarMaterialEvent.class);
			addListener(EstornarMaterialEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(EstornarMaterialListerner target){
		removeListener(EstornarMaterialEvent.class, target);
	}
	public static class EstornarMaterialEvent extends Event{
		
		private EstoqueMovel estoqueMovel;
				
		private boolean confirm;		
		private double qtdEstornar;
		
		public EstornarMaterialEvent(Component source, EstoqueMovel estoqueMovel, double qtdEstornar, boolean confirm) {
			super(source);
									
			this.confirm = confirm;			
			this.estoqueMovel = estoqueMovel;
			this.qtdEstornar = qtdEstornar;
		}

		public EstoqueMovel getEstoqueMovel(){
			return estoqueMovel;
		}
		
		public double getQtdEstornar(){
			return qtdEstornar;
		}
	
		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface EstornarMaterialListerner extends Serializable{
		public void onClose(EstornarMaterialEvent event);
	}

	
}
