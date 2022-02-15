package com.digital.opuserp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.vaadin.addons.idle.Idle;

import com.boletobancario.boletofacilsdk.BoletoFacil;
import com.boletobancario.boletofacilsdk.enums.BoletoFacilEnvironment;
import com.boletobancario.boletofacilsdk.model.entities.Charge;
import com.boletobancario.boletofacilsdk.model.entities.Payer;
import com.boletobancario.boletofacilsdk.model.response.ChargeResponse;
import com.digital.opuserp.Broadcaster.BroadcastListener;
import com.digital.opuserp.dao.AlertaPendenciaDAO;
import com.digital.opuserp.dao.GerenciaNet;
import com.digital.opuserp.dao.LogDAO;
import com.digital.opuserp.dao.LogErrorDAO;
import com.digital.opuserp.dao.LoginDAO;
import com.digital.opuserp.dao.ModuloDAO;
import com.digital.opuserp.dao.SubModuloDAO;
import com.digital.opuserp.dao.UsuarioDAO;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresasUsuario;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.domain.ModuloUsuario;
import com.digital.opuserp.domain.SubModulo;
import com.digital.opuserp.domain.SubModuloUsuario;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.StringUtil;
import com.digital.opuserp.view.dashboard.DashboardView;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;


@SuppressWarnings("serial")
@Theme("dashboard")
@Title(value="OpusERP4 - Professional Services")
@JavaScript({
	"https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js",
	"jquery.maskMoney.js","jquery.mask.min.js","jnotify.js",
	"confirmation.js",
	"bootstrap.min.js",
	"Moeda.js",
	"notify.js",
	"jnotify.desk.js",
	"jquery.trackpad-scroll-emulator.min.js",
	"custom_scroll.js","roteirizacaoVisaoGeral.js"})

//@Push(value=PushMode.MANUAL)
public class OpusERP4UI extends UI implements BroadcastListener {
	
	//private final DesktopNotifier c = new DesktopNotifier();
	private String version;
	
	private BroadcastListener current_send;
	
	@Override
	public void setLocale(Locale locale) {
		super.setLocale(new Locale("pt", "BR"));
	}
		
	public Long timeAlive = new Long(0);
   
    //UI Escope Variables
    public Usuario USUARIO_LOGADO;
    private Empresa EMPRESA_SELECIONADA;     

    CssLayout root = new CssLayout();
    VerticalLayout loginLayout;    
    Tree menuPrincipal;
    CssLayout content = new CssLayout();
    DashboardView dv;
    
    Button btOk;
    Button signin;
    CssLayout loginPanel;
    TextField username;
    PasswordField password;
    
    ShortcutListener enter;    
    Item item_root;
    
    public static String ENDERECO_SAMBA = "192.168.21.13";
    public static String SENHA_SAMBA = "managerdigi_SB@18";
        	
	@Override
	protected void init(VaadinRequest request) {		
		//request.
		//browser = getPage().getWebBrowser();
        setLocale(new Locale("pt", "BR"));
       
        setContent(root);
        root.addStyleName("root");
        root.setSizeFull();

        Label bg = new Label();
        bg.setSizeUndefined();
        bg.addStyleName("login-bg");
        root.addComponent(bg);

        Properties prop = new Properties();
        
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        String proFile = basepath+"/build.properties";
        
        try {        	
        	//file = new File(basepath + "/WEB-INF/uploads/logo.jpeg");
        	InputStream input = new FileInputStream(proFile);
        	prop.load(input);
        	String versionNumber = prop.getProperty("version.number");
        	String buildNumber =  prop.getProperty("build.number");
        	
        	version = versionNumber+"."+buildNumber;
        } catch (IOException e) {
        	e.printStackTrace();
        } 
      
        buildLoginView(false);       
        configErrorHandle(); 
       
        Broadcaster.register(this);		
        current_send = this;             
	}
	
	private void buildLoginView(boolean exit) {
        if (exit) {
            root.removeAllComponents();            
        }
        
        addStyleName("login");

        loginLayout = new VerticalLayout();
        loginLayout.setSizeFull();
        loginLayout.addStyleName("login-layout");
        root.addComponent(loginLayout);

        loginPanel = new CssLayout();
        loginPanel.addStyleName("login-panel");

        HorizontalLayout labels = new HorizontalLayout();
        labels.setWidth("100%");
        labels.setMargin(true);
        labels.addStyleName("labels");
        loginPanel.addComponent(labels);

        Label welcome = new Label("Bem vindo");
        welcome.setSizeUndefined();
        welcome.addStyleName("h4");
        labels.addComponent(welcome);
        labels.setComponentAlignment(welcome, Alignment.MIDDLE_LEFT);

        Label title = new Label("OpusERP Professional <span class='opus_version'>"+version+"</span>", ContentMode.HTML);
        title.setSizeUndefined();
        title.addStyleName("h2");
        title.addStyleName("light");
        labels.addComponent(title);
        labels.setComponentAlignment(title, Alignment.MIDDLE_RIGHT);

        HorizontalLayout fields = new HorizontalLayout();
        fields.setSpacing(true);
        fields.setMargin(true);
        fields.addStyleName("fields");

        username = new TextField("Login");
        username.focus();
        fields.addComponent(username);

        password = new PasswordField("Senha");
        //new CapsLockWarning().extend(password);
        fields.addComponent(password);

        final Button signin2 = new Button("Entrar", new ClickListener() { 
			
			@Override
			public void buttonClick(ClickEvent event) {
								    
	               Usuario result = LoginDAO.fazerLogin(new Usuario(username.getValue(), password.getValue(), null));
	                    
	                if(result != null){ 
	                	event.getButton().removeShortcutListener(enter);
	                    OpusERP4UI.setUsuarioLogadoUI(result);
	                    buildMainSelecEmpresas();
	                    
	                    LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Login Efetuado com Sucesso"));

	                    Notify.Show("Login Efetuado com Sucesso!", Notify.TYPE_SUCCESS);
	                }else{
	                    if (loginPanel.getComponentCount() > 2) {
	                        // Remove the previous error message
	                        loginPanel.removeComponent(loginPanel.getComponent(2));
	                    }
	                    Label error = new Label("\"Usuário ou Senha Incorretos!. <span>Porfavor tente Novamente</span>\"", ContentMode.HTML);
	                    error.addStyleName("error");
	                    error.setSizeUndefined();
	                    error.addStyleName("light");
	                    // Add animation
	                    error.addStyleName("v-animate-reveal");
	                    loginPanel.addComponent(error);
	                    username.focus();
	                    
	                    Notify.Show("Usuário e/ou Senha Inválido(s)", Notify.TYPE_ERROR);
	                  	                    
	                    Page.getCurrent().getJavaScript().execute("new Effect.Shake('login-panel')");
	                }
			}
		});
        signin2.addStyleName("default");       
        fields.addComponent(signin2);
        fields.setComponentAlignment(signin2, Alignment.BOTTOM_LEFT);

        enter = new ShortcutListener("Entrar", ShortcutAction.KeyCode.ENTER, null) {
        	
            @Override
            public void handleAction(Object sender, Object target) {
                signin2.click();
            }
        }; 

        signin2.addShortcutListener(enter);
        loginPanel.addComponent(fields);
        loginLayout.addComponent(loginPanel);
        loginLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
    }
	
	private void testarBoletoFacil(){
		BoletoFacil boletoFacil = new BoletoFacil(BoletoFacilEnvironment.SANDBOX, "38FEAE574CEAF9144008D17AA64C0BE806E3901AEC67B38D90B5BC05D4F04DDA");
		
		Payer payer = new Payer();
		payer.setName("Marconi Pagador");
		payer.setCpfCnpj("05872109407");

		Charge charge = new Charge();
		charge.setDescription("Cobrança teste gerada pelo SDK Java");
		charge.setAmount(BigDecimal.valueOf(49.00));
		charge.setPayer(payer);
		charge.setInstallments(12);

		ChargeResponse response = boletoFacil.issueCharge(charge);
		//if (response.) {
			for (Charge c : response.getData().getCharges()) {
				System.out.println(c);
			}
		//}
	}
	
	private void buildMainSelecEmpresas(){
	       
        root.removeAllComponents();     
        
        addStyleName("login");

        loginLayout = new VerticalLayout();
        loginLayout.setSizeFull();
        loginLayout.addStyleName("login-layout");
        root.addComponent(loginLayout);

        final CssLayout loginPanel = new CssLayout();
        loginPanel.addStyleName("login-panel");

        HorizontalLayout labels = new HorizontalLayout();
        labels.setWidth("100%");
        labels.setMargin(true);
        labels.addStyleName("labels");
        loginPanel.addComponent(labels);

        Label welcome = new Label("Olá "+OpusERP4UI.getUsuarioLogadoUI().getUsername()+",Escolha uma Empresa para Prosseguir!");
        welcome.setSizeUndefined();
        welcome.addStyleName("h4");
        labels.addComponent(welcome);
        labels.setComponentAlignment(welcome, Alignment.MIDDLE_LEFT);

        
        VerticalLayout fields = new VerticalLayout();        
        fields.setMargin(true);
        fields.addStyleName("fields");
        fields.setHeight("195px");

        final BeanItemContainer<Empresa> empresas = new BeanItemContainer<Empresa>(Empresa.class);
        
        if(!OpusERP4UI.getUsuarioLogadoUI().getUsername().equals("admin")){
        
	         
	        List<EmpresasUsuario> result = LoginDAO.getEmpresasUsuario(OpusERP4UI.getUsuarioLogadoUI());
	        
	        if(result != null){
		        for(EmpresasUsuario e:result){
		            empresas.addBean(e.getEmpresa());
		        }
	        }
        
        }else{
        	 
 	        List<Empresa> result = LoginDAO.getEmpresas();
 	        
 	        if(result != null){
 	        	if(result.size() == 1){
 	        		 OpusERP4UI.setEmpresa(result.get(0)); 
                     buildMainView();
 	        	}
 	        	
 	        	for(Empresa e:result){
 	            	empresas.addBean(e);
 	        	}
 	        }
        }
        
        final Table tb_empresas = new Table(null, empresas);        
        tb_empresas.setVisibleColumns(new Object[] { "id", "razao_social","nome_fantasia" });
        tb_empresas.setSelectable(true);
        tb_empresas.setSizeUndefined();
        tb_empresas.setImmediate(true);
        tb_empresas.setNullSelectionAllowed(false);
        tb_empresas.setColumnHeader("id", "Cod");
        tb_empresas.setColumnHeader("razao_social", "Razão Social");
        tb_empresas.setColumnHeader("nome_fantasia", "Fantasia");
        tb_empresas.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                    if(event.isDoubleClick()){                    	
                    	 ((Table) event.getSource()).select(event.getItemId());
                        OpusERP4UI.setEmpresa(empresas.getItem(event.getItemId()).getBean());
                        buildMainView();
                    }
            }
        });
        
        tb_empresas.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(tb_empresas.getValue() != null){
					btOk.setEnabled(true);
				}else{
					btOk.setEnabled(false);
				}
			}
		});
        
        tb_empresas.setNullSelectionAllowed(false);
        tb_empresas.focus();
        
        HorizontalLayout hlButtons = new HorizontalLayout(){
        	{
        		setSpacing(true);
        		
        		
        		Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						buildLoginView(true);
					}
				});
        		
        		
        		addComponent(btCancelar);
        		
        		
        		btOk = new Button("Ok", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
				
	                    OpusERP4UI.setEmpresa(empresas.getItem(tb_empresas.getValue()).getBean());
						buildMainView();
					}
				});
        		btOk.setStyleName("default");
        		btOk.setEnabled(false);
        		
        		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        btOk.click();
                    }
                };
                
                btOk.addShortcutListener(slbtOK);
        		
        		addComponent(btOk);
        		
        		
        	}
        };
        
        
        fields.addComponent(tb_empresas);
        fields.addComponent(hlButtons);
        fields.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
        
       
        loginPanel.addComponent(fields);
        loginPanel.setHeight("250px");

        loginLayout.addComponent(loginPanel);
        loginLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
        
        
    }
	
	private void buildMainView() {
		
		
		VerticalLayout vl = new VerticalLayout();
		vl.setWidth("32px");
		vl.setStyleName("bt_help");
		Button btHelp = new Button("");
		btHelp.setIcon(new ThemeResource("icons/help-icon.png"));
		btHelp.setStyleName(Reindeer.BUTTON_LINK);
		btHelp.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {					
					Page.getCurrent().open("http://179.127.32.7/wiki_opus/", "_blank", true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		vl.addComponent(btHelp);
		
		root.addComponent(vl); 
    	
    	Notify.Show("Olá "+USUARIO_LOGADO.getUsername()+", Bom Trabalho", Notify.TYPE_SUCCESS);
    	
        removeStyleName("login");
        if (null != loginLayout) {
        	root.removeComponent(loginLayout);
        }
                
        HorizontalLayout hl1 = new HorizontalLayout();
        hl1.setSizeFull();
        hl1.addStyleName("main-view");
        
        final VerticalLayout vl_sidebar = new VerticalLayout();
        vl_sidebar.addStyleName("sidebar");
        vl_sidebar.setWidth(null);
        vl_sidebar.setHeight("100%");
        
        hl1.addComponent(vl_sidebar);
        
        CssLayout Branding = new CssLayout();
        Branding.addStyleName("branding");
        
        Label logo = new Label("<span>OpusERP4</span> Professional Services", com.vaadin.shared.ui.label.ContentMode.HTML);
        logo.setSizeUndefined();
        Branding.addComponent(logo);
        
        vl_sidebar.addComponent(Branding);
        
        menuPrincipal = new Tree();       
        
        vl_sidebar.addComponent(menuPrincipal);
        vl_sidebar.setExpandRatio(menuPrincipal, 1);
        
        VerticalLayout vl1 =  new VerticalLayout();
        vl1.setSizeUndefined();
        vl1.addStyleName("user");
        
        Image img_avatar_usuario = new Image(null,new ThemeResource("img/profile-pic.png"));
        img_avatar_usuario.setWidth("34px");
        
        Label lbUsername = new Label(OpusERP4UI.getUsuarioLogadoUI().getUsername());        
        lbUsername.setWidth("100%");
        
        HorizontalLayout hluser = new HorizontalLayout();
        hluser.setWidth("100%");
        
        hluser.addComponent(img_avatar_usuario);
        hluser.addComponent(lbUsername);
        hluser.setExpandRatio(lbUsername, 1.0f);
        vl1.addComponent(hluser);
        
        Image img_avatar_empresa = new Image(null,new ThemeResource("img/Company-34.png"));
        img_avatar_empresa.setWidth("34px");
                
        Label lbEmpresaNomeFantasia = new Label(OpusERP4UI.getEmpresa().getNome_fantasia());        
        lbEmpresaNomeFantasia.setWidth("100%");        

        HorizontalLayout hlEmpresa = new HorizontalLayout();
        hlEmpresa.setWidth("100%");
        
        hlEmpresa.addComponent(img_avatar_empresa);
        hlEmpresa.addComponent(lbEmpresaNomeFantasia);
        hlEmpresa.setExpandRatio(lbEmpresaNomeFantasia, 1.0f);
        vl1.addComponent(hlEmpresa);        
        
        
                               
        MenuBar.Command cmd = new MenuBar.Command() {
            @Override
            public void menuSelected(
                MenuBar.MenuItem selectedItem) {
                if(selectedItem.getText().equals("Alternar de Empresa")){
                	buildMainSelecEmpresas();
                }else if(selectedItem.getText().equals("Alterar Senha")){
                	buildChangePassword();
                }else if(selectedItem.getText().equals("Avisar Menutenção")){               	
                	//Broadcaster.broadcast("", current_send, Type_Func.ALERT_MAINTENANCE);                	
                }
            }

            
            boolean valid = false;
			private void buildChangePassword() {
				
				VerticalLayout vlRoot = new VerticalLayout();
				vlRoot.setMargin(true);				
				
				final Window win = new Window("Alterar Senha", vlRoot);
				
				
				vlRoot.addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						PasswordField tfSenhaAtual = new PasswordField("Senha Atual");
						tfSenhaAtual.setTextChangeEventMode(TextChangeEventMode.LAZY);
						tfSenhaAtual.setImmediate(true);
						tfSenhaAtual.addListener(new FieldEvents.TextChangeListener() {
							
							@Override
							public void textChange(TextChangeEvent event) {
								if(getUsuarioLogadoUI().getPassword().equals(StringUtil.md5(event.getText()))){
									valid = true;
								}else{
									valid = false;
								}
							}
						});
						addComponent(tfSenhaAtual);
						
					}
				});
				
				final PasswordField tfSenhaNova = new PasswordField("Nova Senha");
				vlRoot.addComponent(new FormLayout(){
					{
						setStyleName("form-cutom-new");
						tfSenhaNova.setImmediate(true);
						addComponent(tfSenhaNova);						
					}
				});
				
				vlRoot.addComponent(new HorizontalLayout(){
					{
						
						setMargin(true);
						setWidth("100%");
						
						Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								win.close();								
							}
						});						
						addComponent(btCancelar);
						
						
						Button btAlterar = new Button("Alterar", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								boolean teste = valid;
								String senhaNova = tfSenhaNova.getValue();
								if(valid == true && !tfSenhaNova.getValue().equals("")){
									UsuarioDAO uDAO = new UsuarioDAO();
									getUsuarioLogadoUI().setPassword(StringUtil.md5(tfSenhaNova.getValue()));
									uDAO.changePassword(getUsuarioLogadoUI());
									win.close();
								}else{									
									Notify.Show("Sua Senha Atual não Esta Correta!", Notify.TYPE_ERROR);
								}
							}
						});
						
						btAlterar.addStyleName("default");
						addComponent(btAlterar);
						
					
						setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
						setComponentAlignment(btAlterar, Alignment.MIDDLE_RIGHT);
						
					}
				});
			
				
				getUI().addWindow(win);
				win.center();
				win.setModal(true);
			}

        };
        
        MenuBar settings = new MenuBar();
        MenuBar.MenuItem settingsMenu = settings.addItem("",null);
        settingsMenu.setStyleName("icon-cog");
        settingsMenu.addItem("Alterar Senha", cmd);
        settingsMenu.addSeparator();
        settingsMenu.addItem("Alternar de Empresa", cmd);
        settingsMenu.addSeparator();
        
        if(OpusERP4UI.getUsuarioLogadoUI().getUsername().equals("marconi")){
        	settingsMenu.addItem("Calculadora", cmd);
            settingsMenu.addSeparator();
            settingsMenu.addItem("Avisar Menutenção", cmd);
            settingsMenu.addSeparator();
            settingsMenu.addItem("Chat", cmd);
        }
                
        //vl1.addComponent(settings);

        Button exit = new NativeButton("Exit");
        exit.addStyleName("icon-cancel");
        exit.setDescription("Sair");
        //vl1.addComponent(exit);
        
        HorizontalLayout hlButtonsUser = new HorizontalLayout();
        hlButtonsUser.addStyleName("hl_buttons_user");
        hlButtonsUser.setWidth("100%");
        hlButtonsUser.addComponent(settings);
        hlButtonsUser.addComponent(exit);
        
        vl1.addComponent(hlButtonsUser);
        //vl1.addComponent(exit);
        
        exit.addClickListener(new Button.ClickListener() {
           @Override
           public void buttonClick(Button.ClickEvent event) {
        	   
        	   GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Fazer Logout?", true, true);
        			   gd.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){


								Notify.Show("Logout Realizado com Sucesso!", Notify.TYPE_SUCCESS);
								
								getUI().getCurrent().getSession().close();
								getUI().getCurrent().close();
								VaadinService.getCurrentRequest().getWrappedSession().invalidate();
								
								getPage().reload();

							}
						}
					});   
        			   
        	   getUI().addWindow(gd);
           }
        });
        
        
        exit.addShortcutListener(new Button.ClickShortcut(exit, KeyCode.L, ModifierKey.CTRL));
        
        
        vl_sidebar.addComponent(vl1);
        hl1.addComponent(content);
        content.setSizeFull();
        content.addStyleName("view-content");
        hl1.setExpandRatio(content, 1);
        
        
        content.removeAllComponents();       
        dv = new DashboardView();                       
        content.addComponent(dv);
        
        root.addComponent(hl1);
        
         menuPrincipal.addExpandListener(new Tree.ExpandListener() {
			
			@Override
			public void nodeExpand(ExpandEvent event) {
				for (Object item1 : menuPrincipal.getItemIds()) {
          		  if(menuPrincipal.isRoot(item1)){
	            		  if(event.getItemId() != item1){
	            			  menuPrincipal.collapseItem(item1);
	            		  }else{
	            			  menuPrincipal.expandItem(item1);
	            		  }
          		  }
          	  }
			}
		});
        
         
         menuPrincipal.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
            
                    if(!menuPrincipal.isRoot(event.getItemId())){
                    	
                        try {
							dv.addTab(event.getItem().getItemProperty("modulo").getValue().toString());
						} catch (Exception e) {	
							LogErrorDAO.add(new LogError(null, "OpusERP4UI", "buildMainView", e.getMessage(), getEmpresa(), getUsuarioLogadoUI()));
							e.printStackTrace();							
						}
                    }else{
                        if(!menuPrincipal.isExpanded(event.getItemId())){
                            menuPrincipal.expandItem(event.getItemId());
                            setItem_root(event.getItem());
                        }else{
                            menuPrincipal.collapseItem(event.getItemId());
                        }
                    }
                    
                    
                    if(menuPrincipal.getItem(event.getItemId()).getItemProperty("modulo").getValue().equals("Página Inicial")){
                    	
                    	try {
							dv.addTab(event.getItem().getItemProperty("modulo").getValue().toString());
						} catch (Exception e) {						
							e.printStackTrace();
						}
                    }
                
             
            }
        });

       initMenuPrincipal();
        
        Idle.track(getUI(), 4000, new Idle.Listener() {
			
	            @Override
	            public void userInactive() {
	            	
	            	List<Object> idsExpandidos = new ArrayList<>();
	            	Collection<?> ids = menuPrincipal.getItemIds();
	            	
	            	for (Object itemId : ids) {
						if(menuPrincipal.isExpanded(itemId)){
							idsExpandidos.add(itemId);									
						}
					}
	            	
	            	menuPrincipal.removeAllItems();
					initMenuPrincipal();
					
					for (Object itemId : idsExpandidos) {
						menuPrincipal.expandItem(itemId);
					}
	            }
	
	            @Override
	            public void userActive() {
	            	//status.setValue("You are now active");
	            }
	        });      
        
       
        	
    }	
	
	private void initMenuPrincipal(){
	    	//RotinaDAO.RefreshPendencias();
	    	
	       
	        
	        Item item = null;
	        int itemId = 0;  
	        int itemRoot = 0;
	        
	        HierarchicalContainer hwContainer = new HierarchicalContainer();        
	        hwContainer.addContainerProperty("modulo", String.class, null);
	        hwContainer.addContainerProperty("icone", ThemeResource.class, null);

	        
	        	
        	item = hwContainer.addItem(itemId);
            item.getItemProperty("modulo").setValue("Página Inicial");
            item.getItemProperty("icone").setValue(new ThemeResource("icons/pagina-inicial-icon.png"));      
            hwContainer.setChildrenAllowed(itemId, false);
      
            itemId ++;
	        

	        		List<ModuloUsuario> modulos =  LoginDAO.getModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());

	        		 Collections.sort (modulos, new Comparator() {  
	        	            public int compare(Object o1, Object o2) {  
	        	                ModuloUsuario p1 = (ModuloUsuario) o1;  
	        	                ModuloUsuario p2 = (ModuloUsuario) o2;  
	        	                return p1.getModulo().getOrdem() < p2.getModulo().getOrdem() ? -1 : (p1.getModulo().getOrdem() > p2.getModulo().getOrdem() ? +1 : 0);  
	        	            }  
	        	        });
	        		 
	                for(ModuloUsuario m : modulos){  

	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("Cadastros")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));                   
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		
		
		                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
		
		                    for(SubModuloUsuario s: SubModulos){    
		                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
		                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
		                            item = hwContainer.addItem(itemId);
		                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());		                            
		                            hwContainer.setParent(itemId, itemRoot);
		                            hwContainer.setChildrenAllowed(itemId, false);
		                            itemId ++;  
		                    	}
		                     }                  
	                	}
	                	
	                
	                	
	                	String ip = Page.getCurrent().getWebBrowser().getAddress();
	                	//System.out.println(ip);
	                	
	                	
	                	///if(ip.equals("179.127.32.10") && !getUsuarioLogadoUI().getUsername().equals("ademir")){
		                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("Acesso")){
		
		                		Integer codModulo = m.getModulo().getId();
		                        item = hwContainer.addItem(itemId);
			                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());           
			                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
			                    hwContainer.setChildrenAllowed(itemId, true);
			                    itemRoot = itemId;
			                    itemId ++;
			
			
			                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
			
			                    for(SubModuloUsuario s: SubModulos){    
			                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
			                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
			                            item = hwContainer.addItem(itemId);
			                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
			                            hwContainer.setParent(itemId, itemRoot);
			                            hwContainer.setChildrenAllowed(itemId, false);
			                            itemId ++;  
			                    	}
			                     }                  
		                	}
	                	//}
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("Financeiro")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());         
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		
		
		                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
		
		                    for(SubModuloUsuario s: SubModulos){    
		                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
		                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
		                            item = hwContainer.addItem(itemId);
		                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
		                            hwContainer.setParent(itemId, itemRoot);
		                            hwContainer.setChildrenAllowed(itemId, false);
		                            itemId ++;  
		                    	}
		                     }                  
	                	}
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("Pedido")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());        
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		
		
		                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
		
		                    for(SubModuloUsuario s: SubModulos){    
		                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
		                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
		                            item = hwContainer.addItem(itemId);
		                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
		                            hwContainer.setParent(itemId, itemRoot);
		                            hwContainer.setChildrenAllowed(itemId, false);
		                            itemId ++;  
		                    	}
		                     }                  
	                	}
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("Estoque")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
   	                        item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());
   	                        item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		
		
		                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
		
		                    for(SubModuloUsuario s: SubModulos){    
		                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
		                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
		                            item = hwContainer.addItem(itemId);
		                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
		                            hwContainer.setParent(itemId, itemRoot);
		                            hwContainer.setChildrenAllowed(itemId, false);
		                            itemId ++;  
		                    	}
		                     }                  
	                	}
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("NFe")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());        
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		
		
		                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
		
		                    for(SubModuloUsuario s: SubModulos){    
		                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
		                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
		                            item = hwContainer.addItem(itemId);
		                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
		                            hwContainer.setParent(itemId, itemRoot);
		                            hwContainer.setChildrenAllowed(itemId, false);
		                            itemId ++;  
		                    	}
		                     }                  
	                	}
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("NFe Mod-55")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());        
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		
		
		                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
		
		                    for(SubModuloUsuario s: SubModulos){    
		                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
		                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
		                            item = hwContainer.addItem(itemId);
		                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
		                            hwContainer.setParent(itemId, itemRoot);
		                            hwContainer.setChildrenAllowed(itemId, false);
		                            itemId ++;  
		                    	}
		                     }                  
	                	}
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("Ordens de Serviço")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());      
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		
		
		                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
		
		                    for(SubModuloUsuario s: SubModulos){    
		                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
		                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
		                            item = hwContainer.addItem(itemId);
		                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
		                            hwContainer.setParent(itemId, itemRoot);
		                            hwContainer.setChildrenAllowed(itemId, false);
		                            itemId ++;  
		                    	}
		                     }                  
	                	}
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("Manutenção")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());      
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		
		
		                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
		
		                    for(SubModuloUsuario s: SubModulos){    
		                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
		                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
		                            item = hwContainer.addItem(itemId);
		                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
		                            hwContainer.setParent(itemId, itemRoot);
		                            hwContainer.setChildrenAllowed(itemId, false);
		                            itemId ++;  
		                    	}
		                     }                  
	                	}
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("Relatorios")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());                
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		
		
		                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
		
		                    for(SubModuloUsuario s: SubModulos){    
		                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
		                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
		                            item = hwContainer.addItem(itemId);
		                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
		                            hwContainer.setParent(itemId, itemRoot);
		                            hwContainer.setChildrenAllowed(itemId, false);
		                            itemId ++;  
		                    	}
		                     }                  
	                	}
	                	
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("CRM")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());                   
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		
		
		                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
		
		                    for(SubModuloUsuario s: SubModulos){    
		                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
		                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
		                            item = hwContainer.addItem(itemId);
		                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
		                            hwContainer.setParent(itemId, itemRoot);
		                            hwContainer.setChildrenAllowed(itemId, false);
		                            itemId ++;  
		                    	}
		                     }                  
	                	}
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("Ajuda")){

	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());                   
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, false);
		                    itemRoot = itemId;
		                    itemId ++;
		
		                
	                	}
	                	
	                	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && m.getModulo().getNome_modulo().equals("Configurações")){

	                		
	                		
	                		Integer codModulo = m.getModulo().getId();
		                    item = hwContainer.addItem(itemId);
		                    item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());                   
		                    item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));
		                    hwContainer.setChildrenAllowed(itemId, true);
		                    itemRoot = itemId;
		                    itemId ++;
		                    
		                    if(OpusERP4UI.getUsuarioLogadoUI().getFuncao().equals("admin")){
		                     
		                    	 item = hwContainer.addItem(itemId);
			                     item.getItemProperty("modulo").setValue("Config-Sistema");
			                     hwContainer.setParent(itemId, itemRoot);
			                     hwContainer.setChildrenAllowed(itemId, false);
			                     itemId ++;      
			                     
			                     item = hwContainer.addItem(itemId);
			                     item.getItemProperty("modulo").setValue("Config-Cadastro");
			                     hwContainer.setParent(itemId, itemRoot);
			                     hwContainer.setChildrenAllowed(itemId, false);
			                     itemId ++;       
			                     
			                     item = hwContainer.addItem(itemId);
			                     item.getItemProperty("modulo").setValue("Config-Financeiro");
			                     hwContainer.setParent(itemId, itemRoot);
			                     hwContainer.setChildrenAllowed(itemId, false);
			                     itemId ++;
			                     
			                     item = hwContainer.addItem(itemId);
			                     item.getItemProperty("modulo").setValue("Config-Acesso");
			                     hwContainer.setParent(itemId, itemRoot);
			                     hwContainer.setChildrenAllowed(itemId, false);
			                     itemId ++;
			                     
			                     item = hwContainer.addItem(itemId);
			                     item.getItemProperty("modulo").setValue("Config-Crm");
			                     hwContainer.setParent(itemId, itemRoot);
			                     hwContainer.setChildrenAllowed(itemId, false);
			                     itemId ++;
			                     
			                     item = hwContainer.addItem(itemId);
			                     item.getItemProperty("modulo").setValue("Config-Ordens de Serviço");
			                     hwContainer.setParent(itemId, itemRoot);
			                     hwContainer.setChildrenAllowed(itemId, false);
			                     itemId ++;
			                     			                     
			                     item = hwContainer.addItem(itemId);
			                     item.getItemProperty("modulo").setValue("Config-Nfe");
			                     hwContainer.setParent(itemId, itemRoot);
			                     hwContainer.setChildrenAllowed(itemId, false);
			                     itemId ++;
			                     
			                     item = hwContainer.addItem(itemId);
			                     item.getItemProperty("modulo").setValue("Config-Cobrança por E-Mail");
			                     hwContainer.setParent(itemId, itemRoot);
			                     hwContainer.setChildrenAllowed(itemId, false);
			                     itemId ++;
			                      		                    
		                    }else{
		
			                    List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
			
			                    for(SubModuloUsuario s: SubModulos){    
			                        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
			                    	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
			                            item = hwContainer.addItem(itemId);
			                            item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());                            
			                            hwContainer.setParent(itemId, itemRoot);
			                            hwContainer.setChildrenAllowed(itemId, false);
			                            itemId ++;  
			                    	}
			                     }
		                    }
	                	}
	                }
	           
		            
		            menuPrincipal.setImmediate(true);        
			        menuPrincipal.setItemCaptionPropertyId("modulo");       
			        //menuPrincipal.setItemIconPropertyId("icone");
			        menuPrincipal.setSizeUndefined();
			        menuPrincipal.setStyleName("menuPrincipal");
			        menuPrincipal.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
			        menuPrincipal.setContainerDataSource(hwContainer);
			        
			        menuPrincipal.setItemStyleGenerator(new Tree.ItemStyleGenerator() {
						
						@Override
						public String getStyle(Tree source, Object itemId) {
							
							if(!source.isRoot(itemId)){
								String modulo = source.getItem(itemId).getItemProperty("modulo").getValue().toString();
								Integer codSubModulo = SubModuloDAO.findToId(modulo);
								Integer qtd = 0;
								if(modulo.equals("Contatos")){
									qtd = AlertaPendenciaDAO.getPendenciasCrm(codSubModulo);
								}else{
									qtd = AlertaPendenciaDAO.getPendencias(codSubModulo);
								}
								
								if(qtd >= 99){
									qtd = 99;
								}
								
								return String.valueOf(qtd);
							}else{
								String modulo = source.getItem(itemId).getItemProperty("modulo").getValue().toString();
								Integer codModulo = ModuloDAO.getId(modulo);
								
								List<SubModulo> listSubm = SubModuloDAO.getSuModuloByModulo(codModulo);
								for (SubModulo subModulo : listSubm) {
															
									if(modulo.equals("CRM")){
										if(AlertaPendenciaDAO.getPendenciasCrm(subModulo.getId()) > 0){
											return String.valueOf("alerta-menu");
										}
									}else{
										if(AlertaPendenciaDAO.getPendencias(subModulo.getId()) > 0){
											return String.valueOf("alerta-menu");
										}
									}						
								}
								
								return "";
							}
						}
					});
			        
			        
			        for (Object item1 : menuPrincipal.getItemIds()) {
			            //Here the icon can be set to anything. Just an example.			        	
			        	if(hwContainer.getItem(item1).getItemProperty("icone").getValue() != null){
			        		menuPrincipal.setItemIcon(item1, (ThemeResource)hwContainer.getItem(item1).getItemProperty("icone").getValue());
			        	}else{
			        		menuPrincipal.setItemIcon(item1, new ThemeResource("icons/node-icon.png"));
			        	}
			        }
	        
	    }
	
	private void configErrorHandle(){
		
	
		
   	 UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
           	 
           	 
                for (Throwable t = event.getThrowable(); t != null; t = t.getCause()){
                    if (t.getCause() != null){ 
                        
                   	 
                   	 System.out.println("*****************************************************************************************");
                   	 System.out.println("ERRO: "+t.getCause());
                   	 System.out.println("*****************************************************************************************");
                        
                        if(t.getCause().getStackTrace() != null){
	                         for(StackTraceElement s: t.getCause().getStackTrace()){
	                        	     	 
	                        	 File file = new File(s.getClass().getName());
	                        	 if(s.getLineNumber() > 0 && s.getClassName().contains("com.digital.opuserp")){
	                        	 
	                        	 
		                        	 System.out.println("ARQUIVO :"+s.getFileName());
		                        	 System.out.println("LINHA :"+s.getLineNumber());
		                        	 System.out.println("CLASSE :"+s.getClassName());
		                        	 System.out.println("METODO :"+s.getMethodName());
		                        	 
		                        	 LogErrorDAO.add(new LogError(null, s.getClassName()+":"+s.getLineNumber(), s.getMethodName(), t.getCause().toString(), OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI()));                        	 
		                             Notify.Show("Falha no SISTEMA: Sua Ação Resultou em Falha, Porfavor Avise ao Administrador.", Notify.TYPE_ERROR);
		                             Notify.Show(s.getFileName()+" "+s.getLineNumber()+" "+s.getClassName()+" "+s.getMethodName(), Notify.TYPE_ERROR);
		                        	 break;
	                        	 
	                        	 }
	                    		
	                    	 }
	                         
	                     System.out.println("*****************************************************************************************");
                        }
                                                 
                    }
                		 
                }
                


              
            } 
        });
   }

	public static OpusERP4UI getCurrent() {
			return (OpusERP4UI) UI.getCurrent();
	}    
	
	public static Usuario getUsuarioLogadoUI() {
		return OpusERP4UI.getCurrent().USUARIO_LOGADO;
	}
	
	public static Empresa getEmpresa() {
		return OpusERP4UI.getCurrent().EMPRESA_SELECIONADA;
	}
	
	public static void setUsuarioLogadoUI(Usuario u) {
		OpusERP4UI.getCurrent().USUARIO_LOGADO = u;
	}
	
	public static void setEmpresa(Empresa e) {
		OpusERP4UI.getCurrent().EMPRESA_SELECIONADA = e;
	}
	
	public static String getEnderecoSamba(){
		return ENDERECO_SAMBA;
	}
	
	public static String getSenhaSamba(){
		return SENHA_SAMBA;
	}
		
	@Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }

    @Override
    public void receiveBroadcast(final String message, final BroadcastListener listener_send) {
        access(new Runnable() {
            @Override
            public void run() {
            	
            	if(message != null && !message.equals("") && !current_send.equals(listener_send) ){
            		Notification n = new Notification("Message received",message, Type.TRAY_NOTIFICATION);
            		n.show(getPage());
            		push();
            	}
            }
        });
    }

	@Override
	public void avisoManutencao(final BroadcastListener listener_send) {
		
		 access(new Runnable() {
	            @Override
	            public void run() {	        
	            	if(!current_send.equals(listener_send) ){
	            		Notify.ShowOnDeskManutencao("Aviso de Manutenção!", "O Sistema será reiniciado nos Próximos 30 Segundos!");
	            		push();
	            	}
	            }
	     });
	}

	public Item getItem_root() {
		return item_root;
	}

	public void setItem_root(Item item_root) {
		this.item_root = item_root;
	}

}