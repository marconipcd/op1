package com.digital.opuserp.view.modulos.configuracoes.config_cadastro.cnae;

import com.digital.opuserp.domain.Cnae;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.cnae.CnaeEditor.CnaeEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_cadastro.ramo_atv.RamoAtivView;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class CnaeView extends VerticalLayout implements GenericView {
	
	JPAContainer<Cnae> container;
	
	Table tb;
	TextField tfBusca;
	//ComboBox cbStatus;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	
	
	public CnaeView(){		
		
		setSizeFull();
		
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(BuildbtTransferencia());
		hlButons.addComponent(BuildbtAceitar());
		hlButons.addComponent(BuildbtCancelar());
		
		addComponent(hlButons);
		setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		addComponent(new HorizontalLayout(){
			{
				setWidth("100%");
			//	addComponent(buildComboStatus());
				addComponent(buildTfbusca());
				setExpandRatio(tfBusca, 1.0f);
			}
		});
		
		addComponent(buildTbGeneric());
		//addComponent(lbRegistros());
		
		hlFloat = new HorizontalLayout();
		hlFloat.setWidth("100%");
		hlFloat.addComponent(lbRegistros());
		Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
		lbLegend.setWidth("250px");
		hlFloat.addComponent(lbLegend);
		hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
		hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
		
		
		addComponent(hlFloat);
		
		setExpandRatio(tb, 1);
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof RamoAtivView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
			btNovo.removeShortcutListener(slNovo);
			btEditar.removeShortcutListener(slEditar);
		}
	}
	
	public Label lbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btNovo.click();
			}
		};
		return slNovo;
	}
	public ShortcutListener buildShortCutEditar(){
		slEditar = new ShortcutListener("Editar",ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btEditar.click();
			}
		};
		return slEditar;
	}
	public JPAContainer<Cnae> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Cnae.class,ConnUtil.getEntity());
		container.setAutoCommit(false);
			
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);		
				
		tb.setVisibleColumns(new Object[] {"codigo","descricao"});

		tb.setColumnCollapsible("codigo", false);
		
		tb.setColumnHeader("codigo", "Código");
		tb.setColumnHeader("descricao", "Descrição");		
		
		tb.setColumnExpandRatio("descricao", 2);
		
		
		tb.setImmediate(true);
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditar.setEnabled(true);
					btExcluir.setEnabled(true);
				}else{
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
				}
			}
		});
		
		tb.setColumnCollapsible("nome", false);
		
		tb.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				
				if(event.isDoubleClick()   && tb.getValue() != null){
					CnaeEditor cEditor = new CnaeEditor(tb.getItem(tb.getValue()), "Editar Ramo de Atividade", true);
					cEditor.addListerner(new CnaeEditor.CnaeListerner() {
						
						@Override
						public void onClose(CnaeEvent event) {
							if(event.isConfirm()){
								container.commit();							
							}
						}
					});			
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
					
					getUI().addWindow(cEditor);
				  }
			}
		});
		
		
		return tb;
	}
	


	@Override
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}

	@Override
	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(
				Filters.or(
						new Like("codigo", "%"+s+"%", false),
						new Like("descricao", "%"+s+"%", false)
						));
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, lbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				

				
				final BeanItem<Cnae> newItem = new BeanItem<Cnae>(new Cnae());
								
				CnaeEditor cEditor = new CnaeEditor(newItem, "Cadastrar Cnae", true);
				cEditor.addListerner(new CnaeEditor.CnaeListerner() {
					
					@Override
					public void onClose(CnaeEvent event) {
						if(event.isConfirm()){
							
							try {
								Cnae cAdd = newItem.getBean();
								container.addEntity(cAdd);	
								container.commit();
								tb.focus();
							} catch (Exception e) {
								
								e.printStackTrace();								
								Notify.Show("Não Foi Possivel Cadastrar o CNAE", Notify.TYPE_ERROR);
							}
							
						}else{
//							LogDAO.addLog(new Log(null, "Ação de Cadastro de Cliente Cancelada!", OpusERP4_ServerUI.getUsuarioLogadoUI().getUsername(), null));
						}
					}
				});
				
				cEditor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();
					}
				});
				
				getUI().addWindow(cEditor);

				
			}
		});

		return btNovo;
	}

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				
				CnaeEditor cEditor = new CnaeEditor(tb.getItem(tb.getValue()), "Editar CNAE", true);
				cEditor.addListerner(new CnaeEditor.CnaeListerner() {
					
					@Override
					public void onClose(CnaeEvent event) {
						if(event.isConfirm()){
							container.commit();
						}
					}
				});
				
				cEditor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tb.focus();
					}
				});
				
				getUI().addWindow(cEditor);

			}
		});
		btEditar.setEnabled(false);
					
		return btEditar;
	}

	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

									
					
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o CNAE Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								container.removeItem(tb.getValue());
								container.commit();
								Notify.Show("CNAE Excluído com Sucesso!", Notify.TYPE_SUCCESS);
								tb.focus();
							}						
						}
					});
					
					gDialog.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
						}
					});
				
				getUI().addWindow(gDialog);
					}


		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
}
