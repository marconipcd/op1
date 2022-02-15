package com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario;

import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario.UsuarioEditor.UsuarioCancelEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario.UsuarioEditor.UsuarioSalveEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario.UsuarioEditor.UsuarioCancelEvent.UsuarioCancelListener;
import com.digital.opuserp.view.modulos.configuracoes.config_sistema.usuario.UsuarioEditor.UsuarioSalveEvent.UsuarioSalvedListener;
import com.digital.opuserp.view.util.Notify;
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

public class UsuarioView extends VerticalLayout{
	//
		JPAContainer<Usuario> containerUsuario;

	Button btNovo;
	Button btEditar;
	Button btDeletar;
	Button btGerenciarEmpresas;
	Button btPermissoesGerais;
	TextField tfBusca;	
	Table tbUsuario;
	HorizontalLayout hlFloat ;
	
	private Label lbRegistros;
	
	HorizontalLayout hlTableUser;
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	public UsuarioView(boolean act) {
		
		if(act){
		
		//Definindo o sizeFull para o componente root verticalLayout
		setSizeFull();

		// Instaciando Bot�es
		btNovo = buildBtNovo();
		btEditar = buildBtEditar();
		btDeletar = buildBtDeletar();
		btGerenciarEmpresas = buildBtGerenciarEnpresas();
		btPermissoesGerais =  buildBtPermissoesGerais();
		
		
		
		// Instaciando TextFild
		tfBusca = buildTfBusca();
		
		
		
						
		//configurando o layout dos butoons
		HorizontalLayout  hlButtons = new HorizontalLayout();
		hlButtons.addComponent(btNovo);
		hlButtons.addComponent(btEditar);
		hlButtons.addComponent(btDeletar);
		hlButtons.addComponent(btGerenciarEmpresas);
		hlButtons.addComponent(btPermissoesGerais);
		
	
		
		//configurando o layout do tfBUsca
		HorizontalLayout hlBusca = new HorizontalLayout();
		hlBusca.setWidth("100%");		
		hlBusca.addComponent(tfBusca);

			
		hlTableUser = new HorizontalLayout();
		hlTableUser.addComponent(buildTbUsuario());
		//hlTableUser.setExpandRatio(tbUsuario, 2);
		hlTableUser.setSizeFull();
		
		//Adicionando componentes ao layout root 
		addComponent(hlButtons);
		setComponentAlignment(hlButtons, Alignment.TOP_RIGHT);
		addComponent(hlBusca);
		addComponent(hlTableUser);
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
		
		setExpandRatio(hlTableUser, 2);			
		}
	}
	
	public void refresh(){		
		hlTableUser.replaceComponent(tbUsuario, buildTbUsuario());
		hlTableUser.setExpandRatio(tbUsuario, 1);		
	}
	
	private Label buildLbRegistros(){
		lbRegistros = new Label(String.valueOf(containerUsuario.size()) + " Registros Encontrados");
		return lbRegistros;
	}
	
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof UsuarioView)
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
	
	private void addFilter(String s){
		containerUsuario.removeAllContainerFilters();		
		containerUsuario.addContainerFilter(Filters.not(Filters.eq("username", "admin")));		
		Object[] collums = tbUsuario.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tbUsuario.isColumnCollapsed(c.toString()) && containerUsuario.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		containerUsuario.addContainerFilter(Filters.or(filtros));
		containerUsuario.applyFilters();
		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	private Button buildBtNovo(){
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final BeanItem <Usuario> novoUsuario = new BeanItem <Usuario>(new Usuario());
				
				UsuarioEditor usuarioEditor = new UsuarioEditor(novoUsuario, "Cadastro de usuario");
				usuarioEditor.addListerner(new UsuarioSalvedListener() {
					
					@Override
					public void usuarioSalvo(UsuarioSalveEvent event) {
						Usuario user = novoUsuario.getBean();
						user.setPassword(StringUtil.md5(user.getPassword()));
						
						containerUsuario = JPAContainerFactory.make(Usuario.class, "OpusERP4");
						containerUsuario.addEntity(user);			
						hlTableUser.replaceComponent(tbUsuario, buildTbUsuario());
						tbUsuario.focus();
					}
				});
				
				usuarioEditor.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						tbUsuario.focus();
						refresh();
					}
				});
				
				getUI().addWindow(usuarioEditor);
				usuarioEditor.center();
				usuarioEditor.setModal(true);
			}
			
		});
			
		return btNovo;		
	}
	
	private Button buildBtEditar(){
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(tbUsuario.getValue() != null){
					UsuarioEditor usuarioEditor = new UsuarioEditor(tbUsuario.getItem(tbUsuario.getValue()), "Editar Usuario");
					
					usuarioEditor.addListener(new UsuarioCancelListener() {
						
						public void cancelarAlteracao(UsuarioCancelEvent event) {
							containerUsuario.discard();
							hlTableUser.replaceComponent(tbUsuario, buildTbUsuario());
							tbUsuario.focus();
						}
					});
					
					usuarioEditor.addListerner(new UsuarioSalvedListener() {
						
						@Override
						public void usuarioSalvo(UsuarioSalveEvent event) {		
									 
							containerUsuario.commit();		
							hlTableUser.replaceComponent(tbUsuario, buildTbUsuario());
							tbUsuario.focus();
						}
					});
					
					usuarioEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tbUsuario.focus();
							refresh();
						}
					});
					
					getUI().addWindow(usuarioEditor);
					usuarioEditor.center();
					usuarioEditor.setModal(true);	
				}			
			}
		});
		
		
		return btEditar;
	}

	
	private Button buildBtDeletar(){
		btDeletar = new Button("Deletar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(tbUsuario.getValue() != null){				
										
					GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Deseja realmente excluir este usuario?", true,true);
					gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								containerUsuario.removeItem(tbUsuario.getValue());
								containerUsuario.commit();
								Notify.Show("Usuario excluido com sucesso!", Notify.TYPE_SUCCESS);
								tbUsuario.focus();
//								final Set selecionado = (Set) tbUsuario.getValue();
//								for(final Iterator i = selecionado.iterator(); i.hasNext();){
//									final Object id = i.next();
//									if(tbUsuario.containsId(id)){
//										tbUsuario.removeItem(id);
//										containerUsuario.commit();						
//										Notification.show("Usuario excluido com sucesso!");
//									}
//								}
							}
						}
					});
					
					gd.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tbUsuario.focus();
						}
					});
					
					getUI().addWindow(gd);				
				}	
			}
		});
		btDeletar.setEnabled(false);
		return btDeletar;
	}
	private Button  buildBtGerenciarEnpresas(){
		btGerenciarEmpresas = new Button("Gerenciar Empresas", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(tbUsuario.getValue()!= null){				

				Integer codUsuairo = Integer.parseInt(tbUsuario.getItem(tbUsuario.getValue()).getItemProperty("id").toString());
					
					Integer cod = Integer.parseInt(tbUsuario.getItem(tbUsuario.getValue()).getItemProperty("id").toString());
					String username = tbUsuario.getItem(tbUsuario.getValue()).getItemProperty("username").toString();
					String password= tbUsuario.getItem(tbUsuario.getValue()).getItemProperty("password").toString();
					String funcao = tbUsuario.getItem(tbUsuario.getValue()).getItemProperty("funcao").toString();
					
					GerenciarEmpresa empresa = new GerenciarEmpresa("Gerenciar Empresa", true,							
															new Usuario(cod, username, password, funcao));
					
					empresa.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tbUsuario.focus();
						}
					});
					
					getUI().addWindow(empresa);	
				}
			}
		});
		btGerenciarEmpresas.setEnabled(false);
		return btGerenciarEmpresas;
	}

	private Button  buildBtPermissoesGerais(){
		btPermissoesGerais = new Button("Permissões Gerais", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(tbUsuario.getValue()!= null){
					
					Integer cod = Integer.parseInt(tbUsuario.getItem(tbUsuario.getValue()).getItemProperty("id").toString());
					String username = tbUsuario.getItem(tbUsuario.getValue()).getItemProperty("username").toString();
					String password= tbUsuario.getItem(tbUsuario.getValue()).getItemProperty("password").toString();
					String funcao = tbUsuario.getItem(tbUsuario.getValue()).getItemProperty("funcao").toString();
					Integer exibir = Integer.parseInt(tbUsuario.getItem(tbUsuario.getValue()).getItemProperty("visualizar_todos_crm").getValue().toString());		
					
					Usuario u = new Usuario();
					u.setId(cod);					
					u.setUsername(username);
					u.setPassword(password);
					u.setFuncao(funcao);
					u.setVisualizar_todos_crm(exibir); 

					PermissoesGeraisEditor modulos = new PermissoesGeraisEditor("Permissões Gerais", true,u);
					
					modulos.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tbUsuario.focus();
						}
					});
					
					modulos.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							refresh();
						}
					});
					
					getUI().addWindow(modulos);					
				}		
			}
		});
		btPermissoesGerais.setEnabled(false);
		return btPermissoesGerais;
	}

	private TextField buildTfBusca(){
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setInputPrompt("Buscar...");
		
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());			
			}
		});
		
		return tfBusca;
	}
	

	private Table buildTbUsuario(){
		containerUsuario = JPAContainerFactory.makeBatchable(Usuario.class, ConnUtil.getEntity());
		containerUsuario.setAutoCommit(false);
		containerUsuario.addContainerFilter(Filters.not(Filters.eq("username", "admin")));
		containerUsuario.applyFilters();
		
		tbUsuario = new Table();
		tbUsuario.setSizeFull();
		tbUsuario.setContainerDataSource(containerUsuario);
		tbUsuario.setVisibleColumns(new Object[]{"id","username","funcao"});
		tbUsuario.setColumnHeader("username","Nome");
		tbUsuario.setColumnHeader("funcao","Função");
		tbUsuario.setColumnHeader("id","ID");
		tbUsuario.setSelectable(true);	 
//		tbUsuario.setMultiSelect(true);	 
		tbUsuario.setColumnCollapsingAllowed(true);
		tbUsuario.setColumnCollapsed("id",true);
		tbUsuario.setColumnCollapsible("username",false);
		
		
		
		tbUsuario.setImmediate(true);
		tbUsuario.addListener(new  Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(event.getProperty().getValue() != null)
				{
					btDeletar.setEnabled(true);
					btEditar.setEnabled(true);
					btGerenciarEmpresas.setEnabled(true);
					btPermissoesGerais.setEnabled(true);
				}else{
					btDeletar.setEnabled(false);
					btEditar.setEnabled(false);
					btGerenciarEmpresas.setEnabled(false);
					btPermissoesGerais.setEnabled(false);
				}
			}
		});
		
		tbUsuario.addListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					
					UsuarioEditor usuarioEditor = new UsuarioEditor(tbUsuario.getItem(tbUsuario.getValue()), "Editar Usuario");					
					usuarioEditor.addListener(new UsuarioCancelListener() {
						
						public void cancelarAlteracao(UsuarioCancelEvent event) {
							containerUsuario.discard();
							hlTableUser.replaceComponent(tbUsuario, buildTbUsuario());
							tbUsuario.focus();
						}
					});				
					usuarioEditor.addListerner(new UsuarioSalvedListener() {
						
						@Override
						public void usuarioSalvo(UsuarioSalveEvent event) {		
									 
							containerUsuario.commit();		
							hlTableUser.replaceComponent(tbUsuario, buildTbUsuario());
							tbUsuario.focus();
						}
					});
					
					usuarioEditor.addCloseListener(new Window.CloseListener() {
						
						@Override
						public void windowClose(CloseEvent e) {
							tbUsuario.focus();
						}
					});
					
					getUI().addWindow(usuarioEditor);
					usuarioEditor.center();
					usuarioEditor.setModal(true);	
				}
			}
		});		
		return tbUsuario;
	}
	
	

	

}
