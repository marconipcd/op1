package com.digital.opuserp.view.modulos.estoque.gerenciamento;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciamentoEstoqueDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.TransferenciaProduto;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.veiculos.VeiculosView;
import com.digital.opuserp.view.modulos.estoque.gerenciamento.AjusteEditor.AjusteTransferenciaEvent;
import com.digital.opuserp.view.modulos.estoque.gerenciamento.TransferirProdutoEditor.TransferirProdutoEvent;
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
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class GerenciamentoEstoqueView  extends VerticalLayout implements GenericView{


	JPAContainer<TransferenciaProduto> container;
	
	Table tb;
	TextField tfBusca;
	ComboBox cbStatus;
	Button btNovo;
	Button btAceitar;
	Button btExcluir;
	Button btAjuste;
	
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	
	GerenciarModuloDAO gmDAO;
	
	public GerenciamentoEstoqueView(boolean act) {
		
		if(act){		
			setSizeFull();
			
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			
			//hlButons.addComponent(buildbtAjuste());
			hlButons.addComponent(BuildbtTransferencia());
			hlButons.addComponent(BuildbtAceitar());
			hlButons.addComponent(BuildbtCancelar());
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildStatus());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
	
			addComponent(buildTbGeneric());
			//addComponent(buildLbRegistros());
			
			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");
			hlFloat.addComponent(buildLbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Editar");
			lbLegend.setWidth("250px");
			hlFloat.addComponent(lbLegend);
			hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);
			
			
			addComponent(hlFloat);
			
			
			setExpandRatio(tb, 1);
		}
		
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric());
		setExpandRatio(tb, 1);		
		addFilter(tfBusca.getValue());
		btAceitar.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof VeiculosView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btAceitar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btAceitar != null || slEditar != null){
				btAceitar.removeShortcutListener(slEditar);			
			}
		}
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
				btAceitar.click();
			}
		};
		return slEditar;
	}
	
	public JPAContainer<TransferenciaProduto> buildContainer(){
		container = JPAContainerFactory.makeBatchable(TransferenciaProduto.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
				
		container.addContainerFilter(Filters.eq("produto.empresaId", OpusERP4UI.getEmpresa().getId()));
		
		if(cbStatus.getValue().toString() != null){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		}
		
		container.addNestedContainerProperty("produto.nome");
		
		return container;
	}

	@Override
	public Table buildTbGeneric() {	
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
				
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("tipo_transferencia", "Tipo");
		tb.setColumnHeader("tipo", "Local");
		tb.setColumnHeader("produto.nome", "Produto");
		tb.setColumnHeader("qtd", "Qtd.:");
		tb.setColumnHeader("data_inicio_transferencia", "Data");
		tb.setColumnHeader("usuario_transferencia", "Operador");
		tb.setColumnHeader("usuario_aceitou_transferencia", "Aceito Por");
		tb.setColumnHeader("data_conclusao_transferencia", "Em");
		tb.setColumnHeader("status", "Status");
		
		tb.setVisibleColumns(new Object[] {"id","tipo_transferencia","tipo","produto.nome","qtd","data_inicio_transferencia",
				"usuario_transferencia","usuario_aceitou_transferencia","data_conclusao_transferencia","status"});
		
		tb.setColumnWidth("data_conclusao_transferencia",150);
			
		tb.setImmediate(true);		
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(tb.getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status").getValue().toString().equals("PENDENTE")){
					btAceitar.setEnabled(true);
					btExcluir.setEnabled(true); 
				}else{
					btAceitar.setEnabled(false);
					btExcluir.setEnabled(false);
				}
			}
		});	
		
		tb.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				
			}
		});
		
		
		return tb;
	}

	public ComboBox buildStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.addItem("PENDENTE");
		cbStatus.addItem("CONCLUIDO");
		cbStatus.addItem("CANCELADO");
		
		cbStatus.setImmediate(true);
		cbStatus.select("PENDENTE");
		cbStatus.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
					addFilter(tfBusca.getValue());				
			}
		});
		
		return cbStatus;
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
		container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		
		container.addContainerFilter(Filters.eq("produto.empresaId", OpusERP4UI.getEmpresa().getId()));
				
		if(s != null && !s.equals("") && !s.isEmpty()){

			Object[] collums = tb.getVisibleColumns();	
		
			List<Filter> filtros = new ArrayList<Filter>();		
						
			try {
				Integer cod = Integer.valueOf(s);			
				
					for(Object c:collums){		 			
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
							filtros.add(new Like(c.toString(), "%"+cod+"%", false));
						}			
						if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));
						}		
		
					}
				
				
			} catch (Exception e) {

				try {				

						SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

						for(Object c:collums){		 
							if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){

									Date date = sdf2.parse(s);
									filtros.add(Filters.eq(c.toString(), date));
								}						
							}
						
					} catch (Exception e2) {
						for(Object c:collums){		 
							
							if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
								filtros.add(new Like(c.toString(), "%"+s+"%", false));
							}	
						}
					}
				}
	
			container.addContainerFilter(Filters.or(filtros));
		
		}
				
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
		
	}
	
	public Button buildbtAjuste(){
		btAjuste = new Button("Ajuste", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
					final BeanItem<TransferenciaProduto> novaTransferencia = new BeanItem<TransferenciaProduto>(new TransferenciaProduto());
					final AjusteEditor ae = new AjusteEditor(novaTransferencia, "Ajuste Transferência",true);
					
					ae.addListerner(new AjusteEditor.AjusteTransferenciaListener() {
						
						@Override
						public void onClose(AjusteTransferenciaEvent event) {
							if(event.isConfirm()){
																
								boolean check = GerenciamentoEstoqueDAO.AjusteTransferencia(novaTransferencia.getBean());
								
								if(check){
									Notify.Show("Ajuste Feito Com Sucesso!", Notify.TYPE_SUCCESS);
									
									refresh();
								}
							}else{
								Notify.Show("Ajuste Cancelado", Notify.TYPE_NOTICE);
							}
							ae.close();
						}
					});
					
					getUI().addWindow(ae); 
			}
		});
		
		return btAjuste;
	}
	
	

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Transferência", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Transferencia"))				
				{
				
					final BeanItem<TransferenciaProduto> novaTransferencia = new BeanItem<TransferenciaProduto>(new TransferenciaProduto());
														
					final TransferirProdutoEditor janelaTransferir = new TransferirProdutoEditor(novaTransferencia, "Nova Transferência", true);
					janelaTransferir.addListerner(new TransferirProdutoEditor.TransferirProdutoListener() {
						
						@Override
						public void onClose(TransferirProdutoEvent event) {
							if(event.isConfirm()){								
								boolean check  = GerenciamentoEstoqueDAO.TransferirProduto(novaTransferencia.getBean());
								
								if(check){
									Notify.Show("Transferência Iniciada com Sucesso!", Notify.TYPE_SUCCESS);
									
									refresh();
								}else{
									Notify.Show("Não foi possível Iniciar uma Transferência!", Notify.TYPE_ERROR);
								}
							}else{
								Notify.Show("Transferência Cancelada", Notify.TYPE_NOTICE);
							}
							
							janelaTransferir.close();
						}
					});
									
					getUI().addWindow(janelaTransferir);
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Transferir um Produto!", Type.ERROR_MESSAGE);
				}
			}
		});
		
		return btNovo;
	}

	
	public Button BuildbtAceitar() {
		btAceitar = new Button("Aceitar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Aceitar"))				
				{
					
						if(tb.getValue() != null && tb.getItem(tb.getValue()) != null && tb.getItem(tb.getValue()).getItemProperty("id").getValue() != null){
							
							GenericDialog gd = new GenericDialog("Confirme para continuar!", "Deseja realmente aceitar esta Transferência ?", true, true);
							
							gd.addListerner(new GenericDialog.DialogListerner() {
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){
										EntityItem<TransferenciaProduto> entityT = (EntityItem<TransferenciaProduto>) tb.getItem(tb.getValue());
										TransferenciaProduto t = entityT.getEntity();
										t.setStatus("CONCLUIDO");
										t.setUsuario_aceitou_transferencia(OpusERP4UI.getUsuarioLogadoUI().getUsername());
										t.setData_conclusao_transferencia(new Date());
										
										Produto p = ProdutoDAO.find(t.getProduto().getId());
																	
										Float qtdAtual = new Float(0);
										if(t.getTipo().equals("DEPOSITO->LOJA")){
											qtdAtual = new Float(p.getQtdEstoqueDeposito());
										}
										
										if(t.getTipo().equals("LOJA->DEPOSITO")){
											qtdAtual = new Float(p.getQtdEstoque());
										}
										
										
										Float qtdTranferir = t.getQtd();
										
										if(qtdTranferir > qtdAtual){  				
											Notify.Show("Não é possível Aceitar uma Transferência de Quantidade maior do que Disponível!", Notify.TYPE_ERROR);
										}else{
											boolean check = GerenciamentoEstoqueDAO.AceitarTransferenciaProduto(t);
											
											if(check){
												Notify.Show("Transferência Aceita com Sucesso!", Notify.TYPE_SUCCESS);
												refresh();
											}
										}
									}
								}
							});			
							
							getUI().addWindow(gd); 
						}
						
				}else{
					Notification.show("Atenção", "Você não Possui Permissão Aceitar uma Transferência!", Type.ERROR_MESSAGE);
				}
			}
		});
		
	
		btAceitar.setEnabled(false);
		return btAceitar;
	}
	
	
	
	@Override
	public Button BuildbtCancelar() {
		btExcluir = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cancelar"))				
				{
					if(tb.getValue() != null && tb.getItem(tb.getValue()).getItemProperty("status").getValue().toString().equals("PENDENTE")){
						
						GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Rejeitar Esta Transferência ?", true, true);
						
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									EntityItem<TransferenciaProduto> entityT = (EntityItem<TransferenciaProduto>) tb.getItem(tb.getValue());
									TransferenciaProduto t = entityT.getEntity();
									t.setStatus("CANCELADO");
									
									boolean check = GerenciamentoEstoqueDAO.CancelarTransferenciaProduto(t);
									
									if(check){
										refresh();
										Notify.Show("Transferência Cancelada com Sucesso!", Notify.TYPE_SUCCESS);
									}
									
								}
							}
						});
						
						getUI().addWindow(gd); 
						
					}
				}else{
					Notification.show("Atenção", "Você não Possui Permissão Cancelar uma Transferência!", Type.ERROR_MESSAGE);
				}
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
