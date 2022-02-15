package com.digital.opuserp.view.modulos.cadastro.fornecedores;

import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.fornecedores.FornecedorEditor.FornecedorEvent;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

public class FornecedorView extends VerticalLayout implements GenericView {

//	
	
	
	
	Table tb;
	TextField tfBusca;
	ComboBox cbStatusFornecedor;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	Button btImportar;
	Button btHistorico;
	HorizontalLayout hlFloat;
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	
	GerenciarModuloDAO gmDAO;
	
	public FornecedorView(boolean act){
		
		if(act){
			setSizeFull();
			
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
			hlButons.addComponent(BuildbtTransferencia());
			hlButons.addComponent(BuildbtAceitar());
			hlButons.addComponent(BuildbtCancelar());
			hlButons.addComponent(buildBtImportar());
			hlButons.addComponent(buildBtHistoricoCompras());
			
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					addComponent(buildComboStatus());
					addComponent(buildTfbusca());
					addComponent(buildBtBuscar());
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
		btEditar.setEnabled(false);
		btExcluir.setEnabled(false);
		btHistorico.setEnabled(false);
		
	}
	
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(tb.getItemIds().size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof FornecedorView)
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
	
	
	private void atualizarTabela(){
		List<Fornecedor> fornecedores = FornecedorDAO.getFornecedores(cbStatusFornecedor.getValue().toString(), tfBusca.getValue());
		
		tb.removeAllItems();
		for (Fornecedor f: fornecedores) {
			tb.addItem(new Object[]{
					f.getId() != null ? f.getId() : 0, 
					f.getRazao_social() != null ? f.getRazao_social() : "",
					f.getFantasia() != null ? f.getFantasia() : "",
					f.getCnpj() != null ? f.getCnpj() : "", 
					f.getCep() != null ? f.getCep() : "", 
					f.getEndereco() != null ? f.getEndereco() : "",
					f.getBairro() != null ? f.getBairro() : "",
					f.getCidade() != null ? f.getCidade() : "", 
					f.getUf() != null ? f.getUf() : "", 
					f.getPais() != null ? f.getPais() : "", 
					f.getPonto_referencia() != null ? f.getPonto_referencia() : "", 
					f.getFone1() != null ? f.getFone1() : "", 
					f.getFone2() != null ? f.getFone2() : "", 
					f.getFone3() != null ? f.getFone3() : "", 
					f.getFone0800() != null ? f.getFone0800() : "",
					f.getEmail() != null ? f.getEmail() : "", 
					f.getDtpvendas() != null ? f.getDtpvendas() : "", 
					f.getHome_page() != null ? f.getHome_page() : "", 
					f.getRepresentante() != null ? f.getRepresentante() : "",
					f.getMsn_representante() != null ? f.getMsn_representante() : "", 
					f.getTipo_de_frete() != null ? f.getTipo_de_frete() : "",
					f.getCnae_fiscal() != null ? f.getCnae_fiscal().getDescricao() : "", 
					f.getPrincipais_produtos() != null ? f.getPrincipais_produtos() : "", 
					f.getObservacao() != null ? f.getObservacao() : "", 
					f.getLimite_de_credito() != null ? f.getLimite_de_credito() : "",
					f.getData_cadastro() != null ?DataUtil.formatDateBra(f.getData_cadastro()) : "", 
					f.getData_alteracao() != null ? DataUtil.formatDateBra(f.getData_alteracao()) : ""}, tb.getItemIds().size()+1);
		}
	
	}
	

	@Override
	public Table buildTbGeneric() {
		
		
		
		tb = new Table(null);
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
						
		tb.setColumnAlignment("limite_de_credito", Align.RIGHT);		
		tb.setColumnCollapsible("id", false);
		
//		tb.setVisibleColumns(new Object[] {"id","razao_social","fantasia","cnpj","cep","endereco",	"bairro","cidade","uf","pais",	"ponto_referencia",
//				"fone1","fone2","fone3","fone0800","email","dtpvendas","home_page","representante","msn_representante","tipo_de_frete",
//				"cnae_fiscal","principais_produtos","observacao","limite_de_credito","data_cadastro","data_alteracao"});
		
		tb.addContainerProperty("id", Integer.class, 0);
		tb.addContainerProperty("razao_social", String.class, "");
		tb.addContainerProperty("fantasia", String.class, "");
		tb.addContainerProperty("cnpj", String.class, "");
		tb.addContainerProperty("cep", String.class, "");
		tb.addContainerProperty("endereco", String.class, "");
		tb.addContainerProperty("bairro", String.class, "");
		tb.addContainerProperty("cidade", String.class, "");
		tb.addContainerProperty("uf", String.class, "");
		tb.addContainerProperty("pais", String.class, "");
		tb.addContainerProperty("ponto_referencia", String.class, "");
		tb.addContainerProperty("fone1", String.class, "");
		tb.addContainerProperty("fone2", String.class, "");
		tb.addContainerProperty("fone3", String.class, "");
		tb.addContainerProperty("fone0800", String.class, "");
		tb.addContainerProperty("email", String.class, "");
		tb.addContainerProperty("dtpvendas", String.class, "");
		tb.addContainerProperty("home_page", String.class, "");
		tb.addContainerProperty("representante", String.class, "");
		tb.addContainerProperty("msn_representante", String.class, "");
		tb.addContainerProperty("tipo_de_frete", String.class, "");
		tb.addContainerProperty("cnae_fiscal", String.class, "");
		tb.addContainerProperty("principais_produtos", String.class, "");
		tb.addContainerProperty("observacao", String.class, "");
		tb.addContainerProperty("limite_de_credito", String.class, "");
		tb.addContainerProperty("data_cadastro", String.class, "");
		tb.addContainerProperty("data_alteracao", String.class, "");
		
	
		tb.setImmediate(true);
		tb.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() != null){
					btEditar.setEnabled(true);
					btExcluir.setEnabled(true);
					btHistorico.setEnabled(true);
				}else{
					btEditar.setEnabled(false);
					btExcluir.setEnabled(false);
					btHistorico.setEnabled(false);
				}
			}
		});
		
		
		tb.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()  && tb.getValue() != null){
					
					if(tb != null && tb.getValue() != null){
						
						Integer codFornecedor = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
						Fornecedor fornecedor = FornecedorDAO.find(codFornecedor);
						
						FornecedorEditor VeiEditor = new FornecedorEditor(fornecedor, "Editar Fornecedor", true);
						VeiEditor.addListerner(new FornecedorEditor.FornecedorListerner() {
							
							@Override
							public void onClose(FornecedorEvent event) {
								if(event.isConfirm()){
									FornecedorDAO.save(event.getFornecedor());
									atualizarTabela();
									
									tb.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Fornecedor"));
								}
							}
						});
						
						VeiEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tb.focus();
							}
						});
						
						getUI().addWindow(VeiEditor);
						VeiEditor.center();
						VeiEditor.setModal(true);	
					}
				}
			}
		});
		
		atualizarTabela();
		return tb;
	}
	
	public ComboBox buildComboStatus(){
		cbStatusFornecedor = new ComboBox();
		cbStatusFornecedor.setNullSelectionAllowed(false);
		cbStatusFornecedor.addItem("ATIVO");
		cbStatusFornecedor.addItem("INATIVO");
		cbStatusFornecedor.setImmediate(true);
		cbStatusFornecedor.select("ATIVO");
		cbStatusFornecedor.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				atualizarTabela();
			}
		});		
		return cbStatusFornecedor;	
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

	@Override
	public TextField buildTfbusca() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		//tfBusca.setTextChangeEventMode(TextChangeEventMode.EAGER);
		tfBusca.focus();		
		tfBusca.setImmediate(true);
		
		tfBusca.addTextChangeListener(new TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				atualizarTabela();
			}
		});
		
	
		return tfBusca;
	}

	public Button buildBtImportar(){
		btImportar = new Button("Importar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Importar"))				
				{
				
					ImportarFornecedor importar =  new ImportarFornecedor ("Selecione um Fornecedor", true);
					getUI().addWindow(importar);
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Importar um Fornecedor", Type.ERROR_MESSAGE);
				}
			}
		});
		
		return btImportar;
	}
	
	@Override
	public void addFilter(String s) {
		
		
	}

	
	
	@Override
	public Button BuildbtTransferencia() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
					
					
					Fornecedor fNovo = new Fornecedor();
					fNovo.setFornecedor(true);
					fNovo.setEmpresa(OpusERP4UI.getEmpresa());
					fNovo.setStatus("EM DIGITACAO");
					
					FornecedorDAO.save(fNovo);
					
					FornecedorEditor cEditor = new FornecedorEditor(fNovo, "Cadastrar Forncecedor", true);
					cEditor.addListerner(new FornecedorEditor.FornecedorListerner() {
						
						@Override
						public void onClose(FornecedorEvent event) {
							if(event.isConfirm()){
								
								try {
									
									FornecedorDAO.save(event.getFornecedor());
									atualizarTabela();
									
									tb.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Fornecedor"));
								} catch (Exception e) {
									
																	
									Notification.show("ERRO: "+e.getMessage());								
	//								LogDAO.addLog(new Log(null, "ERRO ao Cadastrar um Novo Cliente: "+e.getMessage(), OpusERP4_ServerUI.USUARIO_LOGADO.getUsername(), null));
								}
								
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
					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um Fornecedor", Type.ERROR_MESSAGE);
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
					
					if(tb != null && tb.getValue() != null){
					
					    Integer codFornecedor = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString());
					    Fornecedor fornecedor = FornecedorDAO.find(codFornecedor);
						
						FornecedorEditor cEditor = new FornecedorEditor(fornecedor, "Editar Fornecedor", true);
						cEditor.addListerner(new FornecedorEditor.FornecedorListerner() {
							
							@Override
							public void onClose(FornecedorEvent event) {
								if(event.isConfirm()){
									FornecedorDAO.save(event.getFornecedor());
									atualizarTabela();
									
									tb.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Fornecedor"));
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
					}
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Editar um Fornecedor", Type.ERROR_MESSAGE);
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
				
				FornecedorDAO fDAO = new FornecedorDAO();
				if(fDAO.allowRemove((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue(),OpusERP4UI.getEmpresa().getId())){
				
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Você deseja realmente Excluir o Fornecedor Selecionado?", true, true);
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								
								Fornecedor fornecedor = FornecedorDAO.find((Integer)tb.getItem(tb.getValue()).getItemProperty("id").getValue());								
								FornecedorDAO.remove(fornecedor);
								
								Notification.show("Fornecedor Excluido com Sucesso!");
								tb.focus();

								LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Fornecedor"));
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
					Notification.show("Atenção", "Não é Possivel Excluir este Fornecedor porque Possui Vinculo com Outros Módulos", Type.ERROR_MESSAGE);
				}
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Excluir um Fornecedor", Type.ERROR_MESSAGE);
				}
			}
		});
		btExcluir.setEnabled(false);
		return btExcluir;
	}

	
	public Button buildBtHistoricoCompras(){
		btHistorico = new Button("Histórico", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Historico Compras"))				
				{
				
				try{
					Window window = new Window();
					window.setCaption("Historico de Compras");
			        window.setWidth("800px");
			        window.setHeight("600px");
			        window.setResizable(false);
			        window.center();
			        window.setModal(true);
			        window.setStyleName("disable_scroolbar");
			        Integer codCliente = Integer.parseInt(tb.getItem(tb.getValue()).getItemProperty("id").getValue().toString()); 
			        String nomeCliente = tb.getItem(tb.getValue()).getItemProperty("razao_social").getValue().toString();
			        StreamResource resource = new StreamResource(new HistoricoCompras(codCliente), "HISTORICO DE COMPRAS - "+nomeCliente+".pdf");
			        resource.getStream();			        
			        resource.setMIMEType("application/pdf");			        
			        
			        Embedded e = new Embedded();
			        e.setSizeFull();
			        e.setType(Embedded.TYPE_BROWSER); 
			        e.setSource(resource);
			        
			        window.setContent(e);
			        getUI().addWindow(window);
			        
			        LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Visualizou Historico de compras"));
				}catch(Exception e){
					System.out.println("Deu ERRADO!");
					e.printStackTrace();
				}
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Visualizar Historico de Compras", Type.ERROR_MESSAGE);
				}
			}
		});
		btHistorico.setEnabled(false);
		return btHistorico;
	}
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}
