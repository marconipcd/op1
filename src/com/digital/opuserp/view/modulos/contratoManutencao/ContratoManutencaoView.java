package com.digital.opuserp.view.modulos.contratoManutencao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.ContratoManutencaoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ContratoManutencao;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.contratoManutencao.ContratoManutencaoEditor.ContratoManutencaoEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
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
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ContratoManutencaoView extends VerticalLayout {

	private JPAContainer<ContratoManutencao> container;
	
	private Table tb;
	
	private TextField txtBusca;
	
	private Button btNovo;
	private Button btExcluir;
	
	private HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	private ShortcutListener slNovo;
	private ShortcutListener slEditar;
	
	private Integer codSubModulo;
	
	private GerenciarModuloDAO gmDAO;
	
	public ContratoManutencaoView(boolean act){
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButtons = new HorizontalLayout();
			hlButtons.addComponent(BuildbtNovo());
			hlButtons.addComponent(BuildbtExcluir());
			
			addComponent(hlButtons); 
			setComponentAlignment(hlButtons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildTxtBusca());					
				}
	
			});
			
			addComponent(buildTb());
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildLbRegistros());
			
			Label lbLegend = new Label("F2 - Novo Cadastro");
			lbLegend.setWidth("250px");
			
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			addComponent(hlFloat);
			
			setExpandRatio(tb, 1f);
		}
	}
	
	public void refreshRegistro(){
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	public void refresh(){
		replaceComponent(tb, buildTb());
		setExpandRatio(tb, 1f);
		addFilter(txtBusca.getValue());
		btExcluir.setEnabled(false);
		
		refreshRegistro();
	}
	
	private void buildShortcutEvents(Component c){
		if(c instanceof PlanoManutencaoView){
			btNovo.addShortcutListener(buildShortCurNovo());
		}else{
			if(btNovo != null || slNovo != null){
				btNovo.removeShortcutListener(slNovo);				
			}
		}
	}

	private ShortcutListener buildShortCurNovo() {
		slNovo = new ShortcutListener("Novo", ShortcutAction.KeyCode.F2, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btNovo.click();
			}
		};
		
		return slNovo;
	}
//	private ShortcutListener buildShortCurEditar() {
//		slEditar = new ShortcutListener("Editar", ShortcutAction.KeyCode.ENTER, null) {
//			
//			@Override
//			public void handleAction(Object sender, Object target) {
//				btEditar.click();
//			}
//		};
//		
//		return slEditar;
//	}
	
	private JPAContainer<ContratoManutencao> buildContainer(){
		container = JPAContainerFactory.makeBatchable(ContratoManutencao.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("plano_manutencao.nome");
		
		container.sort(new Object[]{"id"}, new boolean[]{true});
		
		return container;
	}

	private void addFilter(String s) {
				
		container.removeAllContainerFilters();
		container.setApplyFiltersImmediately(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		try {
			Integer cod = Integer.parseInt(s);
			
			for(Object c:collums){		 			
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
					filtros.add(new Like(c.toString(), "%"+cod+"%", false));
				}			
				
				if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}	
			}
			
		} catch (Exception e) {
			
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date date = sdf.parse(s);
				
				for(Object c:collums){
					
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){					   	
						filtros.add(Filters.eq(c.toString(), date));
					}	
					
					if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
						
				}
			}catch(Exception e2){
				
				for(Object c:collums){
					
					if(container.getType(c.toString()) == String.class){					   	
						filtros.add(new Like(c.toString(), "%"+s+"%", false));
					}	
					
				}
			}

		}
		
		container.applyFilters();
	}

	private Label buildLbRegistros() {
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}

	private Table buildTb() {
		tb = new Table(null,  buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setImmediate(true);
		
		tb.setColumnAlignment("valor_mensal", Align.RIGHT);
		tb.setVisibleColumns(new Object[]{"id", "cliente.nome_razao", "plano_manutencao.nome", "data_cadastro"});
		
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("cliente.nome_razao", "Cliente");
		tb.setColumnHeader("plano_manutencao.nome", "Plano");
		tb.setColumnHeader("data_cadastro", "Data Cadastro");
		
		tb.setColumnWidth("id", 40);
		
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){					
					btNovo.setEnabled(true);
					btExcluir.setEnabled(true);
					
				}else{
					btNovo.setEnabled(false);
					btExcluir.setEnabled(false); 
				}
				
			}
		});
		
		tb.removeStyleName("corrige-tamanho-table");
		tb.addStyleName("corrige-tamanho-table");
		
		
		
		return tb;
	}

	private TextField buildTxtBusca() {
		
		txtBusca = new TextField();
		txtBusca.setWidth("100%");
		txtBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		txtBusca.focus();
		txtBusca.setInputPrompt("Buscar...");
		txtBusca.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		
		return txtBusca;
	}
	
	private Button BuildbtExcluir() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir")){
					
					final EntityItem<ContratoManutencao> pmItem = (EntityItem<ContratoManutencao>)tb.getItem(tb.getValue());
					
					
					boolean check = true;
					List<ContasReceber> boletos = ContasReceberDAO.procurarAbertoBoletosDoAcessoPorContratoManutencao(pmItem.getEntity().getId());
					Integer qtd = boletos != null ? boletos.size() : 0; 
					
					
					if(qtd == 0){
					
						GenericDialog gd = new GenericDialog("Confirme para continuar!", "Você deseja realmente excluir este contrato de manutenção ?", true, true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									boolean checkRemove = container.removeItem(pmItem.getItemId());
									container.commit();
									
									
									if(checkRemove){
										Notify.Show("Contrato de Manutenção excluído com sucesso!", Notify.TYPE_SUCCESS);
										container.refresh();
									}								
								}
							}
						});
						
						getUI().addWindow(gd);
					
					}else{
						Notify.Show("Contrato não pode ser excluído porque possui boletos em aberto!", Notify.TYPE_ERROR);
					}
				}else{
					Notify.Show("Você não Possui Permissão para Excluir um Plano de Manutenção", Notify.TYPE_ERROR);
				}
			}
		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}

	
	private Button BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar")){
					final BeanItem<ContratoManutencao> item = new BeanItem<ContratoManutencao>(new ContratoManutencao());
					
					final ContratoManutencaoEditor cmEditor = new ContratoManutencaoEditor(item, "Cadastrar Novo Contrato", true);
					cmEditor.addListerner(new ContratoManutencaoEditor.ContratoManutencaoListerner() {
						
						@Override
						public void onClose(ContratoManutencaoEvent event) {
							if(event.isConfirm()){
																
								DateTime dt = new DateTime(event.getDataPrimeiroBoleto());
								dt.plusMonths(event.getPlanoManutencao().getVigencia());
								
								boolean check = ContratoManutencaoDAO.cadastrar(event.getPlanoManutencao(), event.getCliente(), dt.plusMonths(event.getPlanoManutencao().getVigencia()).toDate(),event.getDataPrimeiroBoleto(), event.getValorPrimeiroBoleto());							
							
								if(check){
									Notify.Show("Contrato de Manutenção cadastrado com sucesso!", Notify.TYPE_SUCCESS);
									cmEditor.close();
									refresh();
								}
							}
						}
					});
					
					getUI().addWindow(cmEditor); 
					
				}else{
					Notify.Show("Você não Possui Permissão para Cadastrar um novo Plano de Manutenção", Notify.TYPE_ERROR);
				}
			}
		});
		return btNovo;
	}
	
	private Integer getCodSubModulo(){
		return codSubModulo;
	}
	public void setCodSubModulo(Integer codSubModulo){
		this.codSubModulo = codSubModulo;
	}
}
