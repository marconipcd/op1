package com.digital.opuserp.view.modulos.cadastro.clientes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.EnderecoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.SubModuloDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.ParametrosBoleto;
import com.digital.opuserp.interfaces.GenericView;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEdicaoRapida.ClienteEdicaoRapidaEvent;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEdicaoReadOnly.ClienteEdicaoReadOnlyEvent;
import com.digital.opuserp.view.modulos.cadastro.clientes.ClienteEditor.ClienteEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.QueryModifierDelegate;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

//COLOCAR ABA DE ALUNO


public class ClientesView extends VerticalLayout implements GenericView {
	
	Table tbGeneric;
	TextField tfBusca;
	Button btNovo;
	//Button btEditar;
	Button btEditaoRapida;
	Button btExcluir;
	Button btImportar;
	Button btHistoricoCliente;	
	
	HorizontalLayout hlFloat;	
	
	JPAContainer<Cliente> container;
	
	private Label lbRegistros;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	Cliente clienteNovo;
	
	public ClientesView(boolean act){
		
		if(act){
		
				setSizeFull();
				
				gmDAO = new GerenciarModuloDAO();
						
				tfBusca = buildTfbusca();		
				//tbGeneric = buildTbGeneric();
				
				HorizontalLayout hlButons = new HorizontalLayout();
				hlButons.addComponent(BuildbtTransferencia());
				hlButons.addComponent(BuildbtEdicoes());
				//hlButons.addComponent(BuildbtEditarSimplificado());
				hlButons.addComponent(BuildbtCancelar());
				hlButons.addComponent(buildBtImportar());
				hlButons.addComponent(buildBtHistoricoCliente());
					
				addComponent(hlButons);
				setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
				
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");				
						addComponent(tfBusca);
						setExpandRatio(tfBusca, 1.0f);
					}
				});
				
				addComponent(buildTbGeneric());
		//		addComponent(buildLbRegistros());
				
				hlFloat  = new HorizontalLayout();
				hlFloat.setWidth("100%");
				hlFloat.addComponent(buildLbRegistros());
				hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
				
				addComponent(hlFloat);
				
				setExpandRatio(tbGeneric, 1);
		}
	}
	public void refresh(){
		replaceComponent(tbGeneric, buildTbGeneric());
		setExpandRatio(tbGeneric, 1);	
		addFilter(tfBusca.getValue());
		btEdicoes.setEnabled(false);
		btExcluir.setEnabled(false);
		btHistoricoCliente.setEnabled(false);
	}
	public Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof ClientesView)
		{
			btNovo.addShortcutListener(buildShortCutNovo());
			btEdicoes.addShortcutListener(buildShortCutEditar());
		}else{
			if(btNovo != null || slNovo != null){				
				btNovo.removeShortcutListener(slNovo);
			}
			
			if(btEdicoes != null || slEditar != null){
				btEdicoes.removeShortcutListener(slEditar);			
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
				btEdicoes.click();
			}
		};
		return slEditar;
	}
	
	
	public JPAContainer<Cliente> buildContainer(){
		container = new JPAContainerFactory().makeBatchable(Cliente.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.removeAllContainerFilters();
		container.setApplyFiltersImmediately(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		container.applyFilters();
		container.addNestedContainerProperty("categoria.nome");
		container.addNestedContainerProperty("como_nos_conheceu.nome");
		container.addNestedContainerProperty("endereco_principal.endereco");
		container.addNestedContainerProperty("endereco_principal.bairro");
		container.addNestedContainerProperty("endereco_principal.cidade");
		container.addNestedContainerProperty("endereco_principal.uf");
		container.addNestedContainerProperty("endereco_principal.numero");
		
		container.sort(new String[] {"id"}, new boolean[]{false});
		
		return container;
	}

	
	
	
	@Override
	public Table buildTbGeneric() {
		
		tbGeneric = new Table(null, buildContainer()){
			
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
				
				if (v instanceof Date && colId.equals("data_nascimento") ) {
				         Date dateValue = (Date) v;

				         return new SimpleDateFormat("dd/MM/yyyy").format(dateValue);			        	 

				}
				
				if(colId.equals("email")){
					
					if(tbGeneric.getItem(rowId).getItemProperty("email").getValue() != null){
						
						
						return tbGeneric.getItem(rowId).getItemProperty("email").getValue().toString().toLowerCase();
						
					}
					
					
				}else if(colId.equals("emailAlternativo")){
					
					if(tbGeneric.getItem(rowId).getItemProperty("emailAlternativo").getValue() != null){
						
						
						return tbGeneric.getItem(rowId).getItemProperty("emailAlternativo").getValue().toString().toLowerCase();
						
					}
					
					
				}else if(colId.equals("site")){
					
					if(tbGeneric.getItem(rowId).getItemProperty("site").getValue() != null){
						
						
						return tbGeneric.getItem(rowId).getItemProperty("site").getValue().toString().toLowerCase();
						
					}
					
					
				}else if(colId.equals("id")){
					
					if(tbGeneric.getItem(rowId).getItemProperty("id").getValue() != null){						
						return ((Integer)tbGeneric.getItem(rowId).getItemProperty("id").getValue()).toString();				
					}																
				}else if(colId.equals("status")){
					
					if(tbGeneric.getItem(rowId).getItemProperty("status").getValue() != null && tbGeneric.getItem(rowId).getItemProperty("status").getValue().equals("WORKING")){						
						return (tbGeneric.getItem(rowId).getItemProperty(colId).getValue()).toString()+"("+tbGeneric.getItem(rowId).getItemProperty("operador_cadastro").getValue().toString()+")";				
					}else{
						return super.formatPropertyValue(rowId, colId, property);
					}
				}else{
					return super.formatPropertyValue(rowId, colId, property);

				}
				
				
				return "";
				
				
				
			}
			
		};
		tbGeneric.setSizeFull();
		tbGeneric.setSelectable(true);
		
		
		tbGeneric.getVisibleColumns();
		
		boolean blacklist = false;
		if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(SubModuloDAO.findToId("Clientes"), OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Black List"))				
		{
			blacklist = true;
		}
		
		if(blacklist)				
		{
			tbGeneric.setVisibleColumns(new Object[]{"id", "nome_razao","tipo_pessoa","doc_cpf_cnpj","data_nascimento","categoria.nome","status", "data_alteracao",
				"doc_rg_insc_estadual", "nome_fantasia",
				"contato", "sexo", "telefone1", "telefone2","celular1", "celular2", "email", "msn", "como_nos_conheceu.nome",
				"endereco_principal.endereco","endereco_principal.numero","endereco_principal.bairro",
				"endereco_principal.cidade","endereco_principal.uf","obs",  "data_cadastro","black_list","aluno","curso","horario_curso","titular_contrato"});
		}else{
			tbGeneric.setVisibleColumns(new Object[]{"id", "nome_razao","tipo_pessoa","doc_cpf_cnpj","data_nascimento","categoria.nome","status", "data_alteracao",
					"doc_rg_insc_estadual", "nome_fantasia",
					"contato", "sexo", "telefone1", "telefone2","celular1", "celular2", "email", "msn", "como_nos_conheceu.nome",
					"endereco_principal.endereco","endereco_principal.numero","endereco_principal.bairro",
					"endereco_principal.cidade","endereco_principal.uf","obs",  "data_cadastro","aluno","curso","horario_curso","titular_contrato"});
		}
		
		
		tbGeneric.setColumnHeader("aluno", "Aluno");
		tbGeneric.setColumnHeader("curso", "Curso");
		tbGeneric.setColumnHeader("horario_curso", "Horário curso");
		tbGeneric.setColumnHeader("titular_contrato", "Titular contrato");
		
		tbGeneric.setColumnHeader("tipo_pessoa", "Tipo Pessoa");
		tbGeneric.setColumnCollapsible("tipo_pessoa", true);
		
		if(blacklist)				
		{
			tbGeneric.setColumnHeader("black_list", "Black List");
		}
		
		
		tbGeneric.setColumnHeader("id", "Código");
		tbGeneric.setColumnHeader("nome_razao", "Nome Razão");
		tbGeneric.setColumnHeader("doc_cpf_cnpj", "CPF/CNPJ");
		tbGeneric.setColumnHeader("status", "Status");
		tbGeneric.setColumnHeader("doc_rg_insc_estadual", "Insc. Estadual/RG");
		tbGeneric.setColumnHeader("categoria.nome", "Categoria");
		tbGeneric.setColumnHeader("endereco_principal.endereco", "Rua");
		tbGeneric.setColumnHeader("endereco_principal.numero", "Numero");
		tbGeneric.setColumnHeader("endereco_principal.bairro", "Bairro");
		tbGeneric.setColumnHeader("endereco_principal.cidade", "Cidade");
		tbGeneric.setColumnHeader("endereco_principal.uf", "UF");
		tbGeneric.setColumnHeader("nome_fantasia", "Nome Fantasia");
		tbGeneric.setColumnHeader("contato", "Contato");
		tbGeneric.setColumnHeader("sexo", "Sexo");
		tbGeneric.setColumnHeader("data_nascimento", "Data de Nascimento");
		tbGeneric.setColumnHeader("telefone1", "Telefone Principal");
		tbGeneric.setColumnHeader("telefone2", "Telefone Alternativo 1");
		tbGeneric.setColumnHeader("celular1", "Telefone Alternativo 2");
		tbGeneric.setColumnHeader("celular2", "Telefone Alternativo 3");
		tbGeneric.setColumnHeader("email", "Email Principal");
		tbGeneric.setColumnHeader("msn", "Email Alternativo");
		tbGeneric.setColumnHeader("como_nos_conheceu.nome", "Como nos Conheceu?");
		tbGeneric.setColumnHeader("obs", "OBS");
		tbGeneric.setColumnHeader("data_cadastro", "Data de Cadastro");
		tbGeneric.setColumnHeader("data_alteracao", "Data de Alteração");
		
		tbGeneric.setColumnCollapsingAllowed(true);
		tbGeneric.setColumnCollapsed("doc_rg_insc_estadual", true);
		tbGeneric.setColumnCollapsed("nome_fantasia", true);
		tbGeneric.setColumnCollapsed("contato", true);		
		tbGeneric.setColumnCollapsed("sexo", true);
		tbGeneric.setColumnCollapsed("data_nascimento", true);		
		//tbGeneric.setColumnCollapsed("telefone1", true);
		//tbGeneric.setColumnCollapsed("telefone2", true);
		//tbGeneric.setColumnCollapsed("celular1", true);
		//tbGeneric.setColumnCollapsed("celular2", true);
		tbGeneric.setColumnCollapsed("email", true);
		tbGeneric.setColumnCollapsed("msn", true);
		tbGeneric.setColumnCollapsed("como_nos_conheceu", true);
		tbGeneric.setColumnCollapsed("obs", true);		
		tbGeneric.setColumnCollapsed("data_cadastro", true);		
		tbGeneric.setColumnCollapsed("tipo_pessoa", true);
		tbGeneric.setColumnCollapsed("como_nos_conheceu.nome", true);
		tbGeneric.setColumnCollapsed("endereco_principal.endereco", true);
		tbGeneric.setColumnCollapsed("endereco_principal.bairro", true);
		tbGeneric.setColumnCollapsed("endereco_principal.cidade", true);
		tbGeneric.setColumnCollapsed("endereco_principal.numero", true);
		
		
		//tbGeneric.setConverter("id", null);
		tbGeneric.setImmediate(true);	
		
		tbGeneric.setColumnCollapsible("nome_razao", false);
		
		
		
		
		tbGeneric.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {	
				if(event.getProperty().getValue() != null){
					btEdicoes.setEnabled(true);
					btExcluir.setEnabled(true);
					btHistoricoCliente.setEnabled(true);
				}else{
					btEdicoes.setEnabled(false);
					btExcluir.setEnabled(false);
					btHistoricoCliente.setEnabled(false);
				}
			}
		});
		
		
		tbGeneric.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				
				if(event.isDoubleClick() && tbGeneric.getValue() != null){
					btEdicoes.click();
				}
			}
		});
		
		//tbGeneric.setPageLength(30);
		
		
		return tbGeneric;
		
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
		container.getEntityProvider().setQueryModifierDelegate(null);
		container.setApplyFiltersImmediately(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));		
		container.sort(new String[] {"id"}, new boolean[]{false});
		
		if(s != null && !s.equals("") && !s.isEmpty()){

			Object[] collums = tbGeneric.getVisibleColumns();	
		
			List<Filter> filtros = new ArrayList<Filter>();		
			
			
			try {
				Integer cod = Integer.valueOf(s);			
				
					for(Object c:collums){		 			
						if(!tbGeneric.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
							filtros.add(new Like(c.toString(), "%"+cod+"%", false));
						}			
						if(!tbGeneric.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
							filtros.add(new Like(c.toString(), "%"+s+"%", false));
						}		
		
					}
				
				
			} catch (Exception e) {
			
//				try {				
//					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//					Date date = sdf.parse(s);
//
//						for(Object c:collums){		 
//							if(!tbGeneric.isColumnCollapsed(c.toString()) && c.toString().equals("data_nascimento") && container.getType(c.toString()) == Date.class){					   	
//								filtros.add(Filters.eq(c.toString(), date));
//							}			
//						}	
//					
//				} catch (Exception e2) {

					try {				
					
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
						
						Date date1 = sdf.parse(s+" 00:01:00");
						Date date2 = sdf.parse(s+" 23:59:00");
						
							for(Object c:collums){		 
									if(!tbGeneric.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Date.class){
										
										if(!c.toString().equals("data_nascimento")){
											filtros.add(Filters.and( new Greater(c.toString(), date1),new Less(c.toString(), date2)));
										}else{
											Date date = sdf2.parse(s);
											filtros.add(Filters.eq(c.toString(), date));
										}
									
								}
							}	
						
						
					} catch (Exception e2) {
						for(Object c:collums){		 
							
							if(!tbGeneric.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
								filtros.add(new Like(c.toString(), "%"+s+"%", false));
							}	
						}
					}
				}
//			}
			
			container.addContainerFilter(Filters.or(filtros));
		
		}
				
		container.applyFilters();
		container.sort(new String[] {"id"}, new boolean[]{false});
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	
	public void addFilterPFisica(String s) {
		container.removeAllContainerFilters();
		container.getEntityProvider().setQueryModifierDelegate(new QueryModifierDelegate() {
			
			@Override
			public void queryWillBeBuilt(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void queryHasBeenBuilt(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void orderByWillBeAdded(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query,
					List<Order> orderBy) {
//				Root<?> root = query.getRoots().iterator().next();
//                orderBy.clear();
//                orderBy.add(criteriaBuilder.desc(root.get("id")));
			}
			
			@Override
			public void orderByWasAdded(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void filtersWillBeAdded(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query,
					List<Predicate> predicates) {
				// TODO Auto-generated method stub
				Root<?> fromPerson = query.getRoots().iterator().next();

		        // Add a "WHERE age > 116" expression
		        Path<String> cpf_cnpj = fromPerson.<String>get("doc_cpf_cnpj");
		        Path<String> empresa = fromPerson.<String>get("empresa");
		        predicates.add(criteriaBuilder.equal(criteriaBuilder.length(cpf_cnpj), 11));
		        predicates.add(criteriaBuilder.equal(empresa, OpusERP4UI.getEmpresa().getId()));
			}
			
			@Override
			public void filtersWereAdded(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		//container.sort(new String[] {"id"}, new boolean[]{false});
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	public void addFilterPJuridica(String s) {
		container.removeAllContainerFilters();
		container.getEntityProvider().setQueryModifierDelegate(new QueryModifierDelegate() {
			
			@Override
			public void queryWillBeBuilt(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void queryHasBeenBuilt(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void orderByWillBeAdded(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query,
					List<Order> orderBy) {
				
//				Root<?> root = query.getRoots().iterator().next();
//                orderBy.clear();
//                orderBy.add(criteriaBuilder.desc(root.get("id")));
			}
			
			@Override
			public void orderByWasAdded(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void filtersWillBeAdded(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query,
					List<Predicate> predicates) {
				// TODO Auto-generated method stub
				Root<?> fromPerson = query.getRoots().iterator().next();

		        // Add a "WHERE age > 116" expression
		        Path<String> cpf_cnpj = fromPerson.<String>get("doc_cpf_cnpj");
		        Path<String> empresa = fromPerson.<String>get("empresa");
		        predicates.add(criteriaBuilder.gt(criteriaBuilder.length(cpf_cnpj), 11));
		        predicates.add(criteriaBuilder.equal(empresa, OpusERP4UI.getEmpresa().getId()));
		        
			}
			
			@Override
			public void filtersWereAdded(CriteriaBuilder arg0, CriteriaQuery<?> arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		//container.sort(new String[] {"id"}, new boolean[]{false});
		container.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	
	public Button buildBtImportar(){
		btImportar = new Button("Importar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Importar"))				
				{
				
					ImportCliente importar = new ImportCliente("Selecione um Cliente", true, codSubModulo);
					getUI().addWindow(importar);
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Importar um Cliente", Type.ERROR_MESSAGE);
				}
			}
		});
		
		return btImportar;
	}
	

	
	
	
	
	public Button buildBtHistoricoCliente(){
		btHistoricoCliente = new Button("Histórico", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Exportar Ficha do Cliente"))				
				{
				
						try{
							Window window = new Window();
							window.setCaption("Ficha do Cliente");
					        window.setWidth("800px");
					        window.setHeight("600px");
					        window.setResizable(true);
					        window.center();
					        window.setModal(true);
					        window.setStyleName("disable_scroolbar");
					        Integer codCliente = Integer.parseInt(tbGeneric.getItem(tbGeneric.getValue()).getItemProperty("id").getValue().toString()); 
					        String nomeCliente = tbGeneric.getItem(tbGeneric.getValue()).getItemProperty("nome_razao").getValue().toString();
					        StreamResource resource = new StreamResource(new FichaCliente(codCliente), "FichaDoCliente_"+nomeCliente+".pdf");
					        resource.getStream();			        
					        resource.setMIMEType("application/pdf");			        
					        
					        Embedded e = new Embedded();
					        e.setSizeFull();
					        e.setType(Embedded.TYPE_BROWSER); 
					        e.setSource(resource);
					        
					        window.setContent(e);
					        getUI().addWindow(window);
					        
					        LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Visualizou uma Ficha de Cliente"));
						}catch(Exception e){
							System.out.println("Deu ERRADO!");
							e.printStackTrace();
						}
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Exportar a Ficha de um Cliente", Type.ERROR_MESSAGE);
				}
			}
		});
		btHistoricoCliente.setEnabled(false);
		return btHistoricoCliente;
	}

	
	
	@Override
	public Button BuildbtTransferencia() {
		
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Cadastrar"))				
				{
							
						Cliente cNovo = new Cliente();
						cNovo.setStatus("WORKING");
						cNovo.setEmpresa(OpusERP4UI.getEmpresa());
						cNovo.setOperador_cadastro(OpusERP4UI.getUsuarioLogadoUI().getUsername());
						
						final BeanItem<Cliente> clienteItem = new BeanItem<Cliente>(ClienteDAO.saveCliente(cNovo));			
									
						ClienteEditor cEditor = new ClienteEditor(clienteItem,"Cadastrar Cliente", true, null,codSubModulo);
				
						// CLIENTE
						cEditor.addListerner(new ClienteEditor.ClientesListerner() {
					
								@Override
								public void onClose(ClienteEvent event) {
									if(event.isConfirm()){
										
										try{
											Cliente addCliente = clienteItem.getBean();
											
											if(addCliente.getBlack_list() == null){
												addCliente.setBlack_list("NAO"); 
											}
											
											addCliente.setEmpresa(new Empresa(OpusERP4UI.getEmpresa().getId()));
											
											if(clienteItem.getItemProperty("nome_razao").getValue() != null){
												addCliente.setNome_razao(addCliente.getNome_razao().toUpperCase());
											}
											if(clienteItem.getItemProperty("nome_fantasia").getValue() != null){
												addCliente.setNome_fantasia(addCliente.getNome_fantasia().toUpperCase());
											}
											addCliente.setContato(addCliente.getContato().toUpperCase());
											if(clienteItem.getItemProperty("obs").getValue() != null){
												addCliente.setObs(addCliente.getObs().toUpperCase());
											}

											addCliente.setTabela_preco(null);
											addCliente.setStatus("INATIVO");
	
											clienteNovo = ClienteDAO.saveCliente(addCliente,event.getEnderecos());
											
											//SEM TAXA PARA CLIENTES NOVOS
											ParametrosBoleto pb = new ParametrosBoleto(null, clienteNovo.getId(), false);
											EntityManager em = ConnUtil.getEntity();
											em.getTransaction().begin();
											em.persist(pb);
											em.getTransaction().commit();
											
											
											LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cadastrou um Cliente"));
											refresh();
										}catch(Exception e){
											e.printStackTrace();
										}
									}else{
										ClienteDAO.remove(clienteItem.getBean());
									}
								}
						});
						
						cEditor.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tbGeneric.focus();
								//refresh();							
							}
						});
						
						getUI().addWindow(cEditor);
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Cadastrar um Cliente", Type.ERROR_MESSAGE);
				}
			}
		});
		
		return btNovo;
	}
	Button btEdicoes;	
	
	private Component BuildbtEdicoes() {
		btEdicoes = new Button("Consultar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				 if (winSubMenuEdicoes != null && winSubMenuEdicoes.getUI() != null)
					 winSubMenuEdicoes.close();
	             else {
	            	buildSubMenuEdicoes(event);
				     
					 getUI().addWindow(winSubMenuEdicoes);
					 winSubMenuEdicoes.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	               				winSubMenuEdicoes.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }					
			}
		});
		btEdicoes.setEnabled(false);
		return btEdicoes;
	}
	
	
	Window winSubMenuEdicoes;
	private void buildSubMenuEdicoes(ClickEvent event) {
		winSubMenuEdicoes = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuEdicoes.setContent(l);
        winSubMenuEdicoes.setWidth("300px");
        winSubMenuEdicoes.addStyleName("notifications");
        winSubMenuEdicoes.setClosable(false);
        winSubMenuEdicoes.setResizable(false);
        winSubMenuEdicoes.setDraggable(false);
        winSubMenuEdicoes.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuEdicoes.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuEdicoes.setCloseShortcut(KeyCode.ESCAPE, null);
        
        Button bt1 = new Button("Visualizar",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
					final BeanItem<Cliente> clienteItem = new BeanItem<Cliente>(ClienteDAO.find(Integer.parseInt(tbGeneric.getItem(tbGeneric.getValue()).getItemProperty("id").getValue().toString())));			
					ClienteEdicaoReadOnly editor = new ClienteEdicaoReadOnly(clienteItem, "Dados do cliente", true, null,codSubModulo, true);
					
					// CLIENTE
					editor.addListerner(new ClienteEdicaoReadOnly.ClienteEdicaoReadOnlyListerner() {
	
						@Override
						public void onClose(ClienteEdicaoReadOnlyEvent event) {
								if(event.isConfirm()){							
									
									Cliente addCliente = clienteItem.getBean();
									ClienteDAO.saveCliente(addCliente,event.getEnderecos());											
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Cliente"));

									refresh();
						
								}
						}
					});
									
					editor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tbGeneric.focus();
						}
					});
					
					getUI().addWindow(editor);
			
				
			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        
        Button bt2 = new Button("Edicão Rápida",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Edicao Rapida"))					
				{				
					final BeanItem<Cliente> clienteItem = new BeanItem<Cliente>(ClienteDAO.find(Integer.parseInt(tbGeneric.getItem(tbGeneric.getValue()).getItemProperty("id").getValue().toString())));			
					final ClienteEdicaoRapida editor = new ClienteEdicaoRapida(clienteItem, "Edição Rápida", true,codSubModulo);
					
					// CLIENTE
					editor.addListerner(new ClienteEdicaoRapida.ClienteEdicaoRapidaListerner() {
	
						@Override
						public void onClose(ClienteEdicaoRapidaEvent event) {
								if(event.isConfirm()){							
									
									Cliente addCliente = clienteItem.getBean();
									ClienteDAO.saveCliente(addCliente);											
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Editou um Cliente"));

									refresh();
									editor.close();
								}
						}
					});
									
					editor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tbGeneric.focus();
						}
					});
					
					getUI().addWindow(editor);		
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Utilizar a Edição Rápida", Type.ERROR_MESSAGE);
				}
				
			}
		});
        bt2.setPrimaryStyleName("btSubMenu");
        
        l.addComponent(bt1);
        l.addComponent(bt2); 
	}


	@Override
	public Button BuildbtAceitar() {
		btEdicoes = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Alterar"))				
				{
					
					
							
							
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Editar um Cliente", Type.ERROR_MESSAGE);
				}
			}
		});
		btEdicoes.setEnabled(false);

		return btEdicoes;
	}

	@Override
	public Button BuildbtCancelar() {
		
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
					
					ClienteDAO cDao = new ClienteDAO();
					if(cDao.allowRemove((Integer)tbGeneric.getItem(tbGeneric.getValue()).getItemProperty("id").getValue(), OpusERP4UI.getEmpresa().getId())){
						
						final GenericDialog confirm = new GenericDialog("Confirme antes de excluir", "Deseja realmente excluir?", "Excluir", "Cancelar", "100px", "80px", true, true);
						
						confirm.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									// PEGA O COD DO CLIENTE PARA BUSCAR OS ENDEREÇOS
									Integer codCliente = Integer.parseInt(tbGeneric.getItem(tbGeneric.getValue()).getItemProperty("id").toString());
															
									//REMOVE TODOS OS ENDERECOS DO CLIENTE
									EnderecoDAO.deleteEnderecos(codCliente);
									
									// REMOVE O CLIENTE
									container.removeItem(tbGeneric.getValue());
									container.commit();
									
									confirm.close();
									Notification.show("Excluído com sucesso!");
									tbGeneric.focus();
									
									LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Excluiu um Cliente"));
								
								}
							
							}
						});
						
						confirm.addCloseListener(new Window.CloseListener() {
							
							@Override
							public void windowClose(CloseEvent e) {
								tbGeneric.focus();
							}
						});
						
						getUI().addWindow(confirm);
					}else{
						Notification.show("Atenção", "Não é Possivel Excluir este Cliente porque Possui Vinculo com Outros Módulos", Type.ERROR_MESSAGE);
					}
				
				
				
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Excluir um Cliente", Type.ERROR_MESSAGE);
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
