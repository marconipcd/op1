package com.digital.opuserp.view.modulos.cadastro.transportadoras;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.TransportadoraDAO;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor2.TransportadoraEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.Less;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class TransportadoraView extends VerticalLayout implements GenericView {

	//
	JPAContainer<Fornecedor> container;
	
	Table tb;
	TextField tfBusca;
	ComboBox cbStatus;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	HorizontalLayout hlFloat;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	
	GerenciarModuloDAO gmDAO;
	
	public TransportadoraView(boolean act){
		
		if(act){
			setSizeFull();
			
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtTransferencia());
			hlButons.addComponent(BuildbtAceitar());
			hlButons.addComponent(BuildbtCancelar());
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildComboStatus());
					addComponent(buildTfbusca());
					setExpandRatio(tfBusca, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
	//		addComponent(buildLbRegistros());
			
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
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof TransportadoraView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btEditar.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btEditar != null || slEditar != null){
				btEditar.removeShortcutListener(slEditar);			
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
				btEditar.click();
			}
		};
		return slEditar;
	}
	
	public JPAContainer<Fornecedor> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Fornecedor.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
		container.addContainerFilter(Filters.eq("transportadora",true));
		if(cbStatus.getValue().toString() != null){
			container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		}
				
		return container;
	}

	@Override
	public Table buildTbGeneric() {
		tb = new Table(null, buildContainer());
		tb.setSizeFull();
		tb.setSelectable(true);
//		tb.setMultiSelect(true);
		tb.setColumnCollapsingAllowed(true);
	
		tb.setColumnCollapsed("endereco", true);
		tb.setColumnCollapsed("numero", true);		
		tb.setColumnCollapsed("bairro", true);
		tb.setColumnCollapsed("cep", true);
		tb.setColumnCollapsed("pais", true);
		tb.setColumnCollapsed("representante", true);
		tb.setColumnCollapsed("fone1", true);
		tb.setColumnCollapsed("fone2", true);
		tb.setColumnCollapsed("fax", true);
		tb.setColumnCollapsed("dtpvendas", true);
		tb.setColumnCollapsed("email", true);
		tb.setColumnCollapsed("home_page", true);
		tb.setColumnCollapsed("contato_representante", true);
		tb.setColumnCollapsed("fone1_representante", true);
		tb.setColumnCollapsed("fone2_representante", true);
		tb.setColumnCollapsed("fone3_representante", true);
		tb.setColumnCollapsed("fone4_representante", true);
		tb.setColumnCollapsed("fax_representante", true);
		tb.setColumnCollapsed("msn_representante", true);
		tb.setColumnCollapsed("cel_representante", true);
		tb.setColumnCollapsed("data_cadastro", true);
		
		tb.setColumnCollapsed("ponto_referencia", true);
		tb.setColumnCollapsed("msn", true);
		tb.setColumnCollapsed("fone3", true);
		tb.setColumnCollapsed("fone4", true);
		tb.setColumnCollapsed("fone0800", true);
		tb.setColumnCollapsed("tipo_de_frete", true);
		tb.setColumnCollapsed("transportadora_padrao_id", true);
		tb.setColumnCollapsed("cnae_fiscal.descricao", true);
		tb.setColumnCollapsed("principais_produtos", true);
		tb.setColumnCollapsed("observacao", true);		
		tb.setColumnCollapsed("limite_de_credito", true);
		tb.setColumnCollapsed("status", true);
		
	
		
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("razao_social", "Nome/Razão Social");
		tb.setColumnHeader("fantasia", "Fantasia");
		tb.setColumnHeader("cnpj", "CPF/CNPJ");		
		tb.setColumnHeader("cep", "CEP");
		tb.setColumnHeader("endereco", "Endereço");
		tb.setColumnHeader("numero", "Número");
		
		tb.setColumnHeader("bairro", "Bairro");
		tb.setColumnHeader("cidade", "Cidade");
		tb.setColumnHeader("uf", "UF");
		tb.setColumnHeader("pais", "Pais");
		tb.setColumnHeader("fax", "Fax");
		tb.setColumnHeader("dtpvendas", "Dept. Vendas");
		tb.setColumnHeader("msn_representante", "E-mail Representante");
		tb.setColumnHeader("representante", "Nome Representante");
		tb.setColumnHeader("ponto_referencia", "Referência");
		tb.setColumnHeader("fone1", "Telefone Principal");
		tb.setColumnHeader("fone2", "Telefone Alternativo 1");		
		tb.setColumnHeader("fone3", "Telefone Alternativo 2");		
		tb.setColumnHeader("fone0800", "Telefone 0800");
		tb.setColumnHeader("email", "E-mail");
		tb.setColumnHeader("msn", "E-mail Alternativo");
		tb.setColumnHeader("home_page", "Site");
		tb.setColumnHeader("data_cadastro", "Data Cadastro");
		tb.setColumnHeader("tipo_de_frete", "Tipo de Frete");
		tb.setColumnHeader("transportadora_padrao_id", "Transportadora Padrão");
		tb.setColumnHeader("ramo_de_atividade", "Ramo de Atividade");		
		tb.setColumnHeader("limite_de_credito", "Limite de Crédito");
		tb.setColumnHeader("contato_representante", "Contato Representante");
		tb.setColumnHeader("fone1_representante", "Telefone Principal Representante");
		tb.setColumnHeader("fone2_representante", "Telefone Alternativo 1 Representante");
		tb.setColumnHeader("fone3_representante", "Telefone Alternativo 2 Representante");		
		tb.setColumnHeader("fax_representante", "Fax Representante");		
		tb.setColumnHeader("observacao", "Obervação");
		tb.setColumnHeader("principais_produtos", "Principais Produtos");
		tb.setColumnHeader("cnae_fiscal.descricao", "Atividade");
		tb.setColumnHeader("tipo_de_frete", "Tipo de Frete");
		tb.setColumnHeader("data_alteracao", "Data Alteração");
						
		tb.setColumnCollapsible("id", false);
		
		tb.setVisibleColumns(new Object[] {"id","razao_social","fantasia","cnpj","cep","endereco",	"bairro","cidade","uf","pais",
				"ponto_referencia","fone1","fone2","fone3","fone0800","email","dtpvendas","home_page",
				"representante","msn_representante","tipo_de_frete",
				"principais_produtos","observacao","limite_de_credito","data_cadastro","data_alteracao"});
		
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
	
		tb.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				
				if(event.isDoubleClick() && tb.getValue() != null){
					
					TransportadoraEditor2 cEditor = new TransportadoraEditor2(tb.getItem(tb.getValue()), "Editar Transportadora", true);
					cEditor.addListerner(new TransportadoraEditor2.TransportadoraListerner() {
						
						@Override
						public void onClose(TransportadoraEvent event) {
							if(event.isConfirm()){
								container.commit();
								tb.focus();
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
	
	public ComboBox buildComboStatus(){
		cbStatus = new ComboBox();
		cbStatus.setNullSelectionAllowed(false);
		cbStatus.addItem("ATIVO");
		cbStatus.addItem("INATIVO");
		cbStatus.setImmediate(true);
		cbStatus.select("ATIVO");
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
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
		container.addContainerFilter(Filters.eq("status", cbStatus.getValue().toString()));
		container.addContainerFilter(Filters.eq("transportadora",true));
		
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
					
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					
						Date date1 = sdf.parse(s+" 00:01:00");
						Date date2 = sdf.parse(s+" 23:59:00");
						
							for(Object c:collums){		 
									if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){										
										filtros.add(Filters.and( new Greater(c.toString(), date1),new Less(c.toString(), date2)));
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
//			}
			
			container.addContainerFilter(Filters.or(filtros));
		
		}
				
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}

	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
						final BeanItem<Fornecedor> newItem = new BeanItem<Fornecedor>(new Fornecedor());
										
						TransportadoraEditor2 cEditor = new TransportadoraEditor2(newItem, "Cadastrar Transportadora", true);
						cEditor.addListerner(new TransportadoraEditor2.TransportadoraListerner() {
							
							@Override
							public void onClose(TransportadoraEvent event) {
								if(event.isConfirm()){
									
									try {
										Fornecedor cAdd = newItem.getBean();
										cAdd.setEmpresa(OpusERP4UI.getEmpresa());
										cAdd.setTrasnportadora(true);
										container.addEntity(cAdd);	
										container.commit();
										tb.focus();
										
										LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou uma Transportadora"));
									} catch (Exception e) {
										
																		
										Notification.show("ERRO: "+e.getMessage());								
		//								LogDAO.addLog(new Log(null, "ERRO ao Cadastrar um Novo Cliente: "+e.getMessage(), OpusERP4_ServerUI.USUARIO_LOGADO.getUsername(), null));
									}
									
								}else{
		//							LogDAO.addLog(new Log(null, "Ação de Cadastro de Cliente Cancelada!", OpusERP4_ServerUI.USUARIO_LOGADO.getUsername(), null));
								}
							}
						});
						
						cEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
								//refresh();
							}
						});
						
						getUI().addWindow(cEditor);
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar uma Transportadora", Type.ERROR_MESSAGE);
				}
			}
		});
		
		return btNovo;
	}

	@Override
	public Button BuildbtAceitar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
				{
					TransportadoraEditor2 cEditor = new TransportadoraEditor2(tb.getItem(tb.getValue()), "Editar Transportadora", true);
					cEditor.addListerner(new TransportadoraEditor2.TransportadoraListerner() {
						
						@Override
						public void onClose(TransportadoraEvent event) {
							if(event.isConfirm()){
								container.commit();
								tb.focus();
								
								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou uma Transportadora"));
							}
							
						}
					});
					
					cEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tb.focus();
							//refresh();
						}
					});
					
					getUI().addWindow(cEditor);
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Editar uma Transportadora", Type.ERROR_MESSAGE);
				}
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
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
				
					TransportadoraDAO tDAO = new TransportadoraDAO();
					if(tDAO.allowRemove((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue(), OpusERP4UI.getEmpresa().getId())){
						GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir a Transportadora Selecionado?", true, true);
						gDialog.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									container.removeItem(tb.getValue());
									container.commit();
									Notification.show("Transportadora Excluida com Sucesso!");								
									tb.focus();
//									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu uma Transportadora"));
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
						
					}else{
						Notification.show("Atenção", "Não é Possivel Excluir esta Transportadora porque Possui Vinculo com Outros Módulos", Type.ERROR_MESSAGE);
					}
					
				
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Excluir uma Transportadora", Type.ERROR_MESSAGE);
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
