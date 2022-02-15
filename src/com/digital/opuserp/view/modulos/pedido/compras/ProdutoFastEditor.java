package com.digital.opuserp.view.modulos.pedido.compras;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CestDAO;
import com.digital.opuserp.dao.CfopDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.CstDAO;
import com.digital.opuserp.dao.CstFormTributoDAO;
import com.digital.opuserp.dao.EcfEmissorCupFiscalDAO;
import com.digital.opuserp.dao.GrupoProdutoDAO;
import com.digital.opuserp.dao.MarcaDAO;
import com.digital.opuserp.dao.NcmDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SimplesNacionalDAO;
import com.digital.opuserp.domain.Cest;
import com.digital.opuserp.domain.Cfop;
import com.digital.opuserp.domain.CstFormaTributo;
import com.digital.opuserp.domain.CstOrigem;
import com.digital.opuserp.domain.EcfEmissorCupFiscal;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.Marca;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Ncm;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SimplesNacional;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.MarcaUtil;
import com.digital.opuserp.util.MarcaUtil.MarcaUtilEvent;
import com.digital.opuserp.util.MarcaUtil.MarcaUtilListerner;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.CestUtil;
import com.digital.opuserp.view.util.CestUtil.CestEvent;
import com.digital.opuserp.view.util.CfopUtil;
import com.digital.opuserp.view.util.CstFormTributoUtil;
import com.digital.opuserp.view.util.CstOrigemUtil;
import com.digital.opuserp.view.util.EcfEmissorCupFiscalUtil;
import com.digital.opuserp.view.util.GrupoProdutoUtil;
import com.digital.opuserp.view.util.GrupoProdutoUtil.GrupoUtilEvent;
import com.digital.opuserp.view.util.NcmUtil;
import com.digital.opuserp.view.util.NcmUtil.NcmEvent;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.SimplesNacionalUtil;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class ProdutoFastEditor  extends Window implements GenericEditor {

	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	TextField txtNome;
	
	private TextField txtEstoqMin; 	
	private TextField txtEstoqMax;	
	private TextField txtLucro;
	private TextField txtPrecoInicVenda;
	private TextField txtMargemLucro;
	ComboBox cbUtilizaSeriais;
	private TextField txtIPI;
	private TextField txtIcms;
	private TextField txtFrete;
	private TextField txtValorCusto;
	
	MovimentoEntCabecalho moviCab = new MovimentoEntCabecalho();
	MovimentoEntDetalhe moviDetalhe = new MovimentoEntDetalhe();
	Produto produtoEditado;
	String valorFormatado;
	
	private TextField txtCodNcm;
	private Button btSearchNcm;
	Ncm ncmSelecionado;
	private TextField txtDescricaoNcm;
	
	private TextField txtCodCest;
	private Button btSearchCest;
	Cest cestSelecionado;
	private TextField txtDescricaoCest;
		
	private CstOrigem origemSelecionado;
	private TextField tfCodOrigem;
	private TextField tfDescricaoOrigem;
	
	private CstFormaTributo cstSelecionado;
	private TextField tfCodCst;
	private TextField tfDescricaoTributo;
	
	private SimplesNacional csosnSelecionado;
	private TextField tfCodCsosn;
	private TextField tfDescricaoSimplesN;
	
	private EcfEmissorCupFiscal cstecfSelecionado;
	private TextField tfCodCstEcf;
	private TextField tfDescricaoECF;
	
	private Cfop cfopSelecionado;
	private TextField tfCodCfop;
	private TextField tfDescricaoCfop;
	
	private TextField txtDiferencaAliquota;
	private TextField txtCustoTotal;
	
	Button btSearchOrigem;
	Button btSearchCst;
	Button btSearchCSOSN;
	Button btSearchECF;
	Button btSearchCfop;
	
	Button btSearchGrupo;
	private GrupoProduto grupoSelecionado;
	private TextField txtCodGrupo;
	private TextField txtDescricaoGrupoProd;
	
	Button btSearchMarcas;
	private Marca marcaSelecionado;
	private TextField txtCodMarca;
	private TextField txtDescricaoMarca;
	
	
	public ProdutoFastEditor(Item item, String title, boolean modal){
		this.item = item;
		
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
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom-new");
				addStyleName("caption-align-produto-nome-compras");
									
				setMargin(true);
				setSpacing(true);
													
				TextField txtNomeProduto = new TextField("Nome");
				txtNomeProduto.setRequired(true);
				txtNomeProduto.setNullRepresentation("");
				txtNomeProduto.setWidth("646px");
				
				fieldGroup.bind(txtNomeProduto, "nome");
				addComponent(txtNomeProduto);				
			}
		});
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
					txtCodGrupo = new TextField("Grupo");				
					txtCodGrupo.setWidth("60px");				
					txtCodGrupo.setNullRepresentation("");
					txtCodGrupo.addStyleName("caption-align-editar-titulo");
					txtCodGrupo.setId("txtGrupoProd");
					txtCodGrupo.setTabIndex(9);
					
					JavaScript.getCurrent().execute("$('#txtGrupoProd').mask('0000000000')");
					txtCodGrupo.setImmediate(true);
		
					txtCodGrupo.addListener(new TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							ClienteDAO cDAO = new ClienteDAO();
							grupoSelecionado = new GrupoProduto();
							
							if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
								
								Integer id = Integer.parseInt(event.getText());
								grupoSelecionado = GrupoProdutoDAO.find(id);								
								
								if(grupoSelecionado != null){
									txtDescricaoGrupoProd.setReadOnly(false);
									txtDescricaoGrupoProd.setValue(grupoSelecionado.getNome_grupo());
									txtDescricaoGrupoProd.setReadOnly(true);
									
									
								}else {
									txtDescricaoGrupoProd.setReadOnly(false);
									txtDescricaoGrupoProd.setValue("");
									txtDescricaoGrupoProd.setReadOnly(true);
									
								}
							}else{
								txtDescricaoGrupoProd.setReadOnly(false);
								txtDescricaoGrupoProd.setValue("");
								txtDescricaoGrupoProd.setReadOnly(true);	
								
							}
						}
					});
		
					txtCodGrupo.setRequired(true);		
					txtDescricaoGrupoProd = new TextField();
					txtDescricaoGrupoProd.setTabIndex(2000);
					txtDescricaoGrupoProd.setReadOnly(true);
					txtDescricaoGrupoProd.setWidth("416px");	
										
					if(item.getItemProperty("grupoId") != null && item.getItemProperty("grupoId").getValue() != null){
						grupoSelecionado = (GrupoProduto)item.getItemProperty("grupoId").getValue();				
					}
				
					btSearchGrupo = new Button();
					btSearchGrupo.setStyleName(BaseTheme.BUTTON_LINK);
					btSearchGrupo.setIcon(new ThemeResource("icons/search-16.png"));
					btSearchGrupo.setTabIndex(300000);
					btSearchGrupo.setEnabled(true);
					btSearchGrupo.addClickListener(new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							GrupoProdutoUtil gpUtil = new GrupoProdutoUtil(true, true,OpusERP4UI.getEmpresa().getId());
							gpUtil.addListerner(new GrupoProdutoUtil.GrupoUtilListerner() {
								
								@Override
								public void onSelected(
										GrupoUtilEvent event) {
										if(event.getGrupo() != null){
											
											grupoSelecionado = event.getGrupo();
											
											txtCodGrupo.setValue(event.getGrupo().getId().toString());
											txtDescricaoGrupoProd.setReadOnly(false);
											txtDescricaoGrupoProd.setValue(event.getGrupo().getNome_grupo());
											txtDescricaoGrupoProd.setReadOnly(true);
											
										}
									}							
							});
							
							getUI().addWindow(gpUtil);
						}
					});
					
					
					FormLayout frmCodigoCliente = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");		
													
							addComponent(txtCodGrupo);							
						}
					};
					addComponent(frmCodigoCliente);
			
					FormLayout frmButtonSearchCliente =new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom_hide_error_cell");
							addComponent(btSearchGrupo);							
						}
					}; 
								
					FormLayout frmDescCliente = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);						
							setStyleName("form-cutom");		
							addStyleName("form-cutom_hide_require");
							
							addComponent(txtDescricaoGrupoProd);							
						}
					}; 
					addComponent(frmButtonSearchCliente);
					addComponent(frmDescCliente);
					setExpandRatio(frmDescCliente, 1);		
					
					
					if(txtCodGrupo != null && grupoSelecionado != null){
						txtCodGrupo.setValue(grupoSelecionado.getId().toString());
						//txtCodFornecedor.setReadOnly(true);
					}
					
					if(txtDescricaoGrupoProd != null && grupoSelecionado != null){
						txtDescricaoGrupoProd.setReadOnly(false);
						txtDescricaoGrupoProd.setValue(grupoSelecionado.getNome_grupo());
						txtDescricaoGrupoProd.setReadOnly(true);
					}
					
//					addComponent(new FormLayout(){					
//						{
//							setStyleName("form-cutom");
//							addStyleName("caption-align-produto-marca");
//							
//							setMargin(true);
//							setSpacing(true);
//																
//							ComboBox cbMarca = new ComboBox("Marca", getMarcas());		
//							cbMarca.setNullSelectionAllowed(false);
//							cbMarca.setRequired(true);
//							cbMarca.setWidth("159px");
//							cbMarca.setConverter(new SingleSelectConverter(cbMarca));
//							cbMarca.setItemCaptionPropertyId("nome");
//							cbMarca.setTabIndex(10);
//							
//							fieldGroup.bind(cbMarca,"marcasId");
//							addComponent(cbMarca);
//						}
//					});	

			
			}	
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
					txtCodMarca = new TextField("Marca");				
					txtCodMarca.setWidth("60px");				
					txtCodMarca.setNullRepresentation("");
					txtCodMarca.addStyleName("caption-align-editar-titulo");
					txtCodMarca.setId("txtCodMarca");
					txtCodMarca.setTabIndex(9);
					
					JavaScript.getCurrent().execute("$('#txtCodMarca').mask('0000000000')");
					txtCodMarca.setImmediate(true);
		
					txtCodMarca.addListener(new TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
							marcaSelecionado = new Marca();
							
							if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
								
								Integer id = Integer.parseInt(event.getText());
								marcaSelecionado = MarcaDAO.find(id);								
								
								if(marcaSelecionado != null){
									txtDescricaoMarca.setReadOnly(false);
									txtDescricaoMarca.setValue(marcaSelecionado.getNome());
									txtDescricaoMarca.setReadOnly(true);
									
									
								}else {
									txtDescricaoMarca.setReadOnly(false);
									txtDescricaoMarca.setValue("");
									txtDescricaoMarca.setReadOnly(true);
									
								}
							}else{
								txtDescricaoMarca.setReadOnly(false);
								txtDescricaoMarca.setValue("");
								txtDescricaoMarca.setReadOnly(true);	
								
							}
						}
					});
		
					txtCodMarca.setRequired(true);		
					txtDescricaoMarca = new TextField();
					txtDescricaoMarca.setTabIndex(2000);
					txtDescricaoMarca.setReadOnly(true);
					txtDescricaoMarca.setWidth("416px");	
										
					if(item.getItemProperty("marcasId") != null && item.getItemProperty("marcasId").getValue() != null){
						marcaSelecionado = (Marca)item.getItemProperty("marcasId").getValue();				
					}
				
					btSearchMarcas= new Button();
					btSearchMarcas.setStyleName(BaseTheme.BUTTON_LINK);
					btSearchMarcas.setIcon(new ThemeResource("icons/search-16.png"));
					btSearchMarcas.setTabIndex(300000);
					btSearchMarcas.setEnabled(true);
					btSearchMarcas.addClickListener(new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							MarcaUtil gpUtil = new MarcaUtil(true, true,OpusERP4UI.getEmpresa().getId());
							gpUtil.addListerner(new MarcaUtilListerner() {
								
								@Override
								public void onSelected(
										MarcaUtilEvent event) {
										if(event.getMarca() != null){
											
											marcaSelecionado = event.getMarca();
											
											txtCodMarca.setValue(event.getMarca().getId().toString());
											txtDescricaoMarca.setReadOnly(false);
											txtDescricaoMarca.setValue(event.getMarca().getNome());
											txtDescricaoMarca.setReadOnly(true);
											
										}
									}							
							});
							
							getUI().addWindow(gpUtil);
						}
					});
					
					
					FormLayout frmCodigoCliente = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom");		
													
							addComponent(txtCodMarca);							
						}
					};
					addComponent(frmCodigoCliente);
			
					FormLayout frmButtonSearchCliente =new FormLayout(){
						{
							setMargin(true);
							setSpacing(true);
							setStyleName("form-cutom_hide_error_cell");
							addComponent(btSearchMarcas);							
						}
					}; 
								
					FormLayout frmDescCliente = new FormLayout(){
						{
							
							setMargin(true);
							setSpacing(true);						
							setStyleName("form-cutom");		
							addStyleName("form-cutom_hide_require");
							
							addComponent(txtDescricaoMarca);							
						}
					}; 
					addComponent(frmButtonSearchCliente);
					addComponent(frmDescCliente);
					setExpandRatio(frmDescCliente, 1);		
					
					
					if(txtCodMarca != null && marcaSelecionado != null){
						txtCodMarca.setValue(marcaSelecionado.getId().toString());
						//txtCodFornecedor.setReadOnly(true);
					}
					
					if(txtDescricaoMarca != null && marcaSelecionado != null){
						txtDescricaoMarca.setReadOnly(false);
						txtDescricaoMarca.setValue(marcaSelecionado.getNome());
						txtDescricaoMarca.setReadOnly(true);
					}
					
					}	
		});

		vlRoot.addComponent(new HorizontalLayout(){
			{
				addStyleName("caption-align-produto-impostos-ncm");
				
				txtCodNcm = new TextField("NCM");	
				txtCodNcm.focus();
				txtCodNcm.setWidth("60px");				
				txtCodNcm.setNullRepresentation("");
				txtCodNcm.setRequired(true);
				txtCodNcm.setTabIndex(12);
				//txtCodNcm.setStyleName("caption-align-produto");
				
				txtCodNcm.setId("txtNcm");
				
				JavaScript.getCurrent().execute("$('#txtNcm').mask('0000000000')");
				txtCodNcm.setImmediate(true);
				
				txtCodNcm.addTextChangeListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						ncmSelecionado = new Ncm();
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							Integer id = Integer.parseInt(event.getText());
							ncmSelecionado = NcmDAO.getNcm(id);				
							
							if(ncmSelecionado != null){
								txtDescricaoNcm.setReadOnly(false);
								txtDescricaoNcm.setValue(ncmSelecionado.getDescricao());
								txtDescricaoNcm.setReadOnly(true);							
							}else {
								txtDescricaoNcm.setReadOnly(false);
								txtDescricaoNcm.setValue("");
								txtDescricaoNcm.setReadOnly(true);
								ncmSelecionado = null;
							}
						}else{
							txtDescricaoNcm.setReadOnly(false);
							txtDescricaoNcm.setValue("");
							txtDescricaoNcm.setReadOnly(true);	
							
							ncmSelecionado = null;
						}
					}
				});
				
				//txtCodIpi.setRequired(true);		
				txtDescricaoNcm = new TextField();
				txtDescricaoNcm.setTabIndex(2000);
				txtDescricaoNcm.setReadOnly(true);
				txtDescricaoNcm.setWidth("649px");	
				
				if(item.getItemProperty("ncm") != null && item.getItemProperty("ncm").getValue() != null){
					try{
						ncmSelecionado = NcmDAO.getNcm(Integer.parseInt(item.getItemProperty("ncm").getValue().toString()));
					}catch(Exception e){
						e.printStackTrace();
					}
				}

				//TODO FAZER DEPOIS QUE ATRELAR NO PRODUTO				
				btSearchNcm = new Button();			
				btSearchNcm.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchNcm.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchNcm.setTabIndex(300000);
				btSearchNcm.setEnabled(true);
				btSearchNcm.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						NcmUtil gpUtil = new NcmUtil(true, true);
						gpUtil.addListerner(new NcmUtil.NcmListerner() {
							
							@Override
							public void onSelected(NcmEvent event) {
								if(event.getNcm()!= null){
									
									ncmSelecionado = event.getNcm();
									
									txtCodNcm.setValue(event.getNcm().getCodigo().toString());
									txtDescricaoNcm.setReadOnly(false);
									txtDescricaoNcm.setValue(event.getNcm().getDescricao());
									txtDescricaoNcm.setReadOnly(true);
									
								}
							}							
						});
						
						getUI().addWindow(gpUtil);
					}
				});
				
				
				FormLayout frmCodigoNcm = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						addComponent(txtCodNcm);							
					}
				};
				addComponent(frmCodigoNcm);
				
				FormLayout frmButtonSearchNcm =new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");
						addComponent(btSearchNcm);							
					}
				}; 
				
				FormLayout frmDescNcm = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(txtDescricaoNcm);							
					}
				}; 
				addComponent(frmButtonSearchNcm);
				addComponent(frmDescNcm);
				setExpandRatio(frmDescNcm, 1);		
				
				
				if(txtCodNcm != null && ncmSelecionado != null){
					txtCodNcm.setValue(ncmSelecionado.getCodigo().toString());
					//txtCodFornecedor.setReadOnly(true);
				}
				
				if(txtDescricaoNcm != null && ncmSelecionado != null){
					txtDescricaoNcm.setReadOnly(false);
					txtDescricaoNcm.setValue(ncmSelecionado.getDescricao());
					txtDescricaoNcm.setReadOnly(true);
				}
				
			}	
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addStyleName("caption-align-produto-impostos-cest");
				
				txtCodCest = new TextField("CEST");	
				txtCodCest.focus();
				txtCodCest.setWidth("60px");				
				txtCodCest.setNullRepresentation("");				
				txtCodCest.setTabIndex(12);
				
				txtCodCest.setId("txtCest");
				
				JavaScript.getCurrent().execute("$('#txtCest').mask('0000000000')");
				txtCodCest.setImmediate(true);
				
				txtCodCest.addTextChangeListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						cestSelecionado = new Cest();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							cestSelecionado = CestDAO.getCest(event.getText());				
							
							if(cestSelecionado != null){
								txtDescricaoCest.setReadOnly(false);
								txtDescricaoCest.setValue(cestSelecionado.getDescricao());
								txtDescricaoCest.setReadOnly(true);							
							}else {
								txtDescricaoCest.setReadOnly(false);
								txtDescricaoCest.setValue("");
								txtDescricaoCest.setReadOnly(true);
								cestSelecionado = null;
							}
						}else{
							txtDescricaoCest.setReadOnly(false);
							txtDescricaoCest.setValue("");
							txtDescricaoCest.setReadOnly(true);	
							
							cestSelecionado = null;
						}
					}
				});
					
				txtDescricaoCest = new TextField();
				txtDescricaoCest.setTabIndex(2000);
				txtDescricaoCest.setReadOnly(true);
				txtDescricaoCest.setWidth("649px");	
				
				if(item.getItemProperty("cest") != null && item.getItemProperty("cest").getValue() != null){
					try{
						cestSelecionado = CestDAO.getCest(item.getItemProperty("cest").getValue().toString());
					}catch(Exception e){
						e.printStackTrace();
					}
				}

				//TODO FAZER DEPOIS QUE ATRELAR NO PRODUTO				
				btSearchCest = new Button();			
				btSearchCest.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCest.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCest.setTabIndex(300000);
				btSearchCest.setEnabled(true);
				btSearchCest.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						CestUtil gpUtil = new CestUtil(true, true);
						gpUtil.addListerner(new CestUtil.CestListerner() {
							
							@Override
							public void onSelected(CestEvent event) {
								if(event.getCest() != null){
									
									cestSelecionado = event.getCest();
									
									txtCodCest.setValue(event.getCest().getCest().toString());
									txtDescricaoCest.setReadOnly(false);
									txtDescricaoCest.setValue(event.getCest().getDescricao());
									txtDescricaoCest.setReadOnly(true);
									
								}
							}							
						});
						
						getUI().addWindow(gpUtil);
					}
				});
				
				
				FormLayout frmCodigoCest = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						addComponent(txtCodCest);							
					}
				};
				addComponent(frmCodigoCest);
				
				FormLayout frmButtonSearchCest =new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");
						addComponent(btSearchCest);							
					}
				}; 
				
				FormLayout frmDescCest = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(txtDescricaoCest);							
					}
				}; 
				addComponent(frmButtonSearchCest);
				addComponent(frmDescCest);
				setExpandRatio(frmDescCest, 1);		
				
				
				if(txtCodCest != null && cestSelecionado != null){
					txtCodCest.setValue(cestSelecionado.getCest().toString());
				}
				
				if(txtDescricaoCest != null && cestSelecionado != null){
					txtDescricaoCest.setReadOnly(false);
					txtDescricaoCest.setValue(cestSelecionado.getDescricao());
					txtDescricaoCest.setReadOnly(true);
				}
				
			}	
		});

		
		vlRoot.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom-new");
				addStyleName("caption-align-produto-garantia-compras");
									
				setMargin(true);
				setSpacing(true);
													
				ComboBox cbGarantia = new ComboBox ("Garantia");
				cbGarantia.setTextInputAllowed(false);
				cbGarantia.setNullSelectionAllowed(false);
				cbGarantia.setRequired(true);				
				cbGarantia.setWidth("206px");
				cbGarantia.addItem("NENHUMA");
				cbGarantia.addItem("1MES");
				cbGarantia.addItem("3MESES");
				cbGarantia.addItem("6MESES");
				cbGarantia.addItem("1ANO-LOJA");
				cbGarantia.addItem("1ANO-ASSISTENCIA");
				cbGarantia.setTabIndex(8);
				
				fieldGroup.bind(cbGarantia, "garantia");
				addComponent(cbGarantia);				
			}
		});
		vlRoot.addComponent(new HorizontalLayout(){
			{	
				
				addStyleName("vl-principal");

				tfCodOrigem = new TextField("Origem");				
				tfCodOrigem.setWidth("60px");				
				tfCodOrigem.setNullRepresentation("");
				tfCodOrigem.setStyleName("caption-align-produto-tab-tab");
				tfCodOrigem.setId("origem");
				tfCodOrigem.setRequired(true);
				tfCodOrigem.setTabIndex(13);
				
				
				JavaScript.getCurrent().execute("$('#origem').mask('00000000000000')");
				tfCodOrigem.setImmediate(true);		
				
				tfCodOrigem.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						origemSelecionado = new CstOrigem();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							Integer codOrigem = Integer.parseInt(event.getText());
							origemSelecionado = CstDAO.findCod(codOrigem);		
							
							if(origemSelecionado != null){
								tfDescricaoOrigem.setReadOnly(false);
								tfDescricaoOrigem.setValue(origemSelecionado.getReferencia());
								tfDescricaoOrigem.setReadOnly(true);
								
							}else{
								tfDescricaoOrigem.setReadOnly(false);
								tfDescricaoOrigem.setValue("");
								tfDescricaoOrigem.setReadOnly(true);
															
								origemSelecionado = null;
							}
						}else{
							tfDescricaoOrigem.setReadOnly(false);
							tfDescricaoOrigem.setValue("");
							tfDescricaoOrigem.setReadOnly(true);					
							
							origemSelecionado = null;
						}
					}
				});

//				tfCodOrigem.setRequired(true);		
				tfDescricaoOrigem = new TextField();
				tfDescricaoOrigem.setTabIndex(2000);
				tfDescricaoOrigem.setReadOnly(true);
				tfDescricaoOrigem.setWidth("649px");
						
				btSearchOrigem = new Button();
				btSearchOrigem.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchOrigem.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchOrigem.setTabIndex(300000);

				if(item.getItemProperty("cst_origem") != null && item.getItemProperty("cst_origem").getValue() != null){
					origemSelecionado = (CstOrigem)item.getItemProperty("cst_origem").getValue();				
				}
				
				btSearchOrigem.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						CstOrigemUtil cUtil = new CstOrigemUtil(true, true);
						cUtil.addListerner(new CstOrigemUtil.CstListerner() {
							
							@Override
							public void onSelected(
									CstOrigemUtil.CstEvent event) {
									if(event.getCstOrigem() != null){
										tfCodOrigem.setValue(event.getCstOrigem().getCst_origem_cod().toString());
										tfDescricaoOrigem.setReadOnly(false);
										tfDescricaoOrigem.setValue(event.getCstOrigem().getReferencia());
										tfDescricaoOrigem.setReadOnly(true);
										origemSelecionado = event.getCstOrigem();
										
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});
				
				
				FormLayout frmCodigoFornecedor = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
												
						addComponent(tfCodOrigem);							
					}
				};
				addComponent(frmCodigoFornecedor);
		
				FormLayout frmButtonSearchFornecedor =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchOrigem);							
					}
				}; 
							
				FormLayout frmDescFornecedor = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoOrigem);							
					}
				}; 
				addComponent(frmButtonSearchFornecedor);
				addComponent(frmDescFornecedor);
				setExpandRatio(frmDescFornecedor, 1);		
				
				
				if(origemSelecionado != null && tfCodOrigem != null){
					tfCodOrigem.setValue(origemSelecionado.getCst_origem_cod().toString());
				}
				
				if(origemSelecionado != null && tfDescricaoOrigem != null){
					tfDescricaoOrigem.setReadOnly(false);
					tfDescricaoOrigem.setValue(origemSelecionado.getReferencia());
					tfDescricaoOrigem.setReadOnly(true);
				}
	     }
	  });
		
		HorizontalLayout hlLinhaCst = new HorizontalLayout();
		
		hlLinhaCst.addComponent(new HorizontalLayout(){
			{
				
				tfCodCst = new TextField("CST");				
				tfCodCst.setWidth("60px");				
				tfCodCst.setNullRepresentation("");
				tfCodCst.setStyleName("caption-align-produto-tab-tab");
				tfCodCst.setId("tributo");
				tfCodCst.setRequired(true);
				tfCodCst.setTabIndex(13);
				
				
				JavaScript.getCurrent().execute("$('#tributo').mask('00000000000000')");
				tfCodCst.setImmediate(true);		
				
				tfCodCst.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						cstSelecionado = new CstFormaTributo();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							String codTributo = event.getText();
							cstSelecionado = CstFormTributoDAO.findCod(codTributo);		
							
							if(cstSelecionado != null){
								tfDescricaoTributo.setReadOnly(false);
								tfDescricaoTributo.setValue(cstSelecionado.getReferencia());
								tfDescricaoTributo.setReadOnly(true);										
							}else{
								tfDescricaoTributo.setReadOnly(false);
								tfDescricaoTributo.setValue("");
								tfDescricaoTributo.setReadOnly(true);
							
								cstSelecionado = null;
							}
						}else{
							tfDescricaoTributo.setReadOnly(false);
							tfDescricaoTributo.setValue("");
							tfDescricaoTributo.setReadOnly(true);			
							
							cstSelecionado = null;
						}
					}
				});
				
//				tfCodTributo.setRequired(true);		
				tfDescricaoTributo = new TextField();
				tfDescricaoTributo.setTabIndex(2000);
				tfDescricaoTributo.setReadOnly(true);
				tfDescricaoTributo.setWidth("100px");
				
				if(item.getItemProperty("cst_forma_tributo") != null && item.getItemProperty("cst_forma_tributo").getValue() != null){
					cstSelecionado = (CstFormaTributo)item.getItemProperty("cst_forma_tributo").getValue();				
				}
				
				btSearchCst = new Button();
				btSearchCst.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCst.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCst.setTabIndex(300000);
				
				btSearchCst.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						CstFormTributoUtil cUtil = new CstFormTributoUtil(true, true);
						cUtil.addListerner(new CstFormTributoUtil.CstListerner() {
							
							@Override
							public void onSelected(
									CstFormTributoUtil.CstEvent event) {
								if(event.getCstTributo() != null){
									tfCodCst.setValue(event.getCstTributo().getCst_tributo_cod());
									tfDescricaoTributo.setReadOnly(false);
									tfDescricaoTributo.setValue(event.getCstTributo().getReferencia());
									tfDescricaoTributo.setReadOnly(true);
									cstSelecionado = event.getCstTributo();
									
								}
							}							
						});
						
						getUI().addWindow(cUtil);
					}
				});
				
				
				addComponent(new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
						
						addComponent(tfCodCst);							
					}
				});

				
				addComponent(new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchCst);							
					}
				}); 
				

				if(cstSelecionado != null && tfCodCst != null){
					tfCodCst.setValue(cstSelecionado.getCst_tributo_cod());
				}

		
			}
		});
		hlLinhaCst.addComponent(new HorizontalLayout(){
			{

			tfCodCsosn = new TextField("CSOSN");				
			tfCodCsosn.setWidth("60px");				
			tfCodCsosn.setNullRepresentation("");
			tfCodCsosn.setStyleName("caption-align-produto-csosn");
			tfCodCsosn.setId("simples");
			tfCodCsosn.setRequired(true);
			tfCodCsosn.setTabIndex(16);
			
			
			JavaScript.getCurrent().execute("$('#simples').mask('00000000000000')");
			tfCodCsosn.setImmediate(true);		
			
			tfCodCsosn.addListener(new TextChangeListener() {
				
				@Override
				public void textChange(TextChangeEvent event) {
					
					csosnSelecionado = new SimplesNacional();
					
					if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
						Integer codSimples = Integer.parseInt(event.getText());
						csosnSelecionado = SimplesNacionalDAO.findCod(codSimples);		
						
						if(csosnSelecionado != null){
							tfDescricaoSimplesN.setReadOnly(false);
							tfDescricaoSimplesN.setValue(csosnSelecionado.getReferencia());
							tfDescricaoSimplesN.setReadOnly(true);
							
						}else{
							tfDescricaoSimplesN.setReadOnly(false);
							tfDescricaoSimplesN.setValue("");
							tfDescricaoSimplesN.setReadOnly(true);
							
							csosnSelecionado = null;
							
						}
					}else{
						tfDescricaoSimplesN.setReadOnly(false);
						tfDescricaoSimplesN.setValue("");
						tfDescricaoSimplesN.setReadOnly(true);							
						
						csosnSelecionado = null;
					}
				}
			});
			
			tfDescricaoSimplesN = new TextField();
			tfDescricaoSimplesN.setTabIndex(2000);
			tfDescricaoSimplesN.setReadOnly(true);
			tfDescricaoSimplesN.setWidth("100px");
			
			if(item.getItemProperty("simples_nacional") != null && item.getItemProperty("simples_nacional").getValue() != null){
				csosnSelecionado = (SimplesNacional)item.getItemProperty("simples_nacional").getValue();				
			}
			
			btSearchCSOSN = new Button();
			btSearchCSOSN.setStyleName(BaseTheme.BUTTON_LINK);
			btSearchCSOSN.setIcon(new ThemeResource("icons/search-16.png"));
			btSearchCSOSN.setTabIndex(300000);
			
			btSearchCSOSN.addClickListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					SimplesNacionalUtil cUtil = new SimplesNacionalUtil(true, true);
					cUtil.addListerner(new SimplesNacionalUtil.CstListerner() {
						
						@Override
						public void onSelected(
								SimplesNacionalUtil.CstEvent event) {
							if(event.getCstSimples() != null){
								tfCodCsosn.setValue(event.getCstSimples().getSimples_nacional_cod().toString());
								tfDescricaoSimplesN.setReadOnly(false);
								tfDescricaoSimplesN.setValue(event.getCstSimples().getReferencia());
								tfDescricaoSimplesN.setReadOnly(true);
								csosnSelecionado = event.getCstSimples();
								
							}
						}							
					});
					
					getUI().addWindow(cUtil);
				}
			});
			
			addComponent(new FormLayout(){
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");		
					
					addComponent(tfCodCsosn);							
				}
			});
			
			addComponent(new FormLayout(){
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom_hide_error_cell");										
					addComponent(btSearchCSOSN);							
				}
			}); 
			
			if(csosnSelecionado != null && tfCodCsosn != null){
				tfCodCsosn.setValue(csosnSelecionado.getSimples_nacional_cod().toString());
			}	
		}
	});	
		hlLinhaCst.addComponent(new HorizontalLayout(){
			{
				
			tfCodCstEcf = new TextField("CST-ECF");				
			tfCodCstEcf.setWidth("60px");				
			tfCodCstEcf.setNullRepresentation("");
			tfCodCstEcf.setStyleName("caption-align-produto-cstecf");
			tfCodCstEcf.setId("ecf");
			tfCodCstEcf.setRequired(true);
			tfCodCstEcf.setTabIndex(17);
			
			JavaScript.getCurrent().execute("$('#ecf').mask('SSSS')");
			tfCodCstEcf.setImmediate(true);		
			
			tfCodCstEcf.addListener(new TextChangeListener() {
				
				@Override
				public void textChange(TextChangeEvent event) {
					
					cstecfSelecionado = new EcfEmissorCupFiscal();
					
					if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
						String ecf = event.getText();
						cstecfSelecionado = EcfEmissorCupFiscalDAO.findCod(ecf);		
						
						if(cstecfSelecionado != null){
							tfDescricaoECF.setReadOnly(false);
							tfDescricaoECF.setValue(cstecfSelecionado.getReferencia());
							tfDescricaoECF.setReadOnly(true);										
						}else{
							tfDescricaoECF.setReadOnly(false);
							tfDescricaoECF.setValue("");
							tfDescricaoECF.setReadOnly(true);										
							cstecfSelecionado = null;										
						}
					}else{
						tfDescricaoECF.setReadOnly(false);
						tfDescricaoECF.setValue("");
						tfDescricaoECF.setReadOnly(true);
						cstecfSelecionado = null;
					}
				}
			});
			
//			tfCodECF.setRequired(true);		
			tfDescricaoECF = new TextField();
			tfDescricaoECF.setTabIndex(2000);
			tfDescricaoECF.setReadOnly(true);
			tfDescricaoECF.setWidth("100px");
			
			if(item.getItemProperty("ecf_cup_filcal") != null && item.getItemProperty("ecf_cup_filcal").getValue() != null){
				cstecfSelecionado = (EcfEmissorCupFiscal)item.getItemProperty("ecf_cup_filcal").getValue();				
			}
			
			btSearchECF = new Button();
			btSearchECF.setStyleName(BaseTheme.BUTTON_LINK);
			btSearchECF.setIcon(new ThemeResource("icons/search-16.png"));
			btSearchECF.setTabIndex(300000);
			
			btSearchECF.addClickListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					EcfEmissorCupFiscalUtil cUtil = new EcfEmissorCupFiscalUtil(true, true);
					cUtil.addListerner(new EcfEmissorCupFiscalUtil.CstListerner() {
						
						@Override
						public void onSelected(
								EcfEmissorCupFiscalUtil.CstEvent event) {
							if(event.getCstEcf() != null){
								tfCodCstEcf.setValue(event.getCstEcf().getEcf_emissor_cod());
								tfDescricaoECF.setReadOnly(false);
								tfDescricaoECF.setValue(event.getCstEcf().getReferencia());
								tfDescricaoECF.setReadOnly(true);
								cstecfSelecionado = event.getCstEcf();
								
							}
						}							
					});
					
					getUI().addWindow(cUtil);
				}
			});
			
			
			FormLayout frmCodigoFornecedor2 = new FormLayout(){
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");		
					
					addComponent(tfCodCstEcf);							
				}
			};
			addComponent(frmCodigoFornecedor2);
			
			FormLayout frmButtonSearchFornecedor2 =new FormLayout(){
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom_hide_error_cell");										
					addComponent(btSearchECF);							
				}
			}; 
			
			FormLayout frmDescFornecedor2 = new FormLayout(){
				{								
					setMargin(true);
					setSpacing(true);						
					setStyleName("form-cutom");		
					addStyleName("form-cutom_hide_require");
					
					addComponent(tfDescricaoECF);							
				}
			}; 
			addComponent(frmButtonSearchFornecedor2);

			
			if(cstecfSelecionado != null && tfCodCstEcf != null){
				tfCodCstEcf.setValue(cstecfSelecionado.getEcf_emissor_cod());
			}

		}
	});	
		
		vlRoot.addComponent(hlLinhaCst);
		vlRoot.addComponent(new HorizontalLayout(){
			{	
				
				addStyleName("vl-principal");

				tfCodCfop= new TextField("CFOP");				
				tfCodCfop.setWidth("60px");				
				tfCodCfop.setNullRepresentation("");
				tfCodCfop.setStyleName("caption-align-produto-tab-tab");
				tfCodCfop.setId("cfop");
				tfCodCfop.setRequired(true);
				tfCodCfop.setTabIndex(13);
								
				JavaScript.getCurrent().execute("$('#cfop').mask('00000000000000')");
				tfCodCfop.setImmediate(true);		
				
				tfCodCfop.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						cfopSelecionado = new Cfop();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							Integer codOrigem = Integer.parseInt(event.getText());
							cfopSelecionado = CfopDAO.find(codOrigem);		
							
							if(cfopSelecionado != null){
								tfDescricaoCfop.setReadOnly(false);
								tfDescricaoCfop.setValue(cfopSelecionado.getDescricao());
								tfDescricaoCfop.setReadOnly(true);
								
							}else{
								tfDescricaoCfop.setReadOnly(false);
								tfDescricaoCfop.setValue("");
								tfDescricaoCfop.setReadOnly(true);
															
								cfopSelecionado = null;
							}
						}else{
							tfDescricaoCfop.setReadOnly(false);
							tfDescricaoCfop.setValue("");
							tfDescricaoCfop.setReadOnly(true);					
							
							cfopSelecionado = null;
						}
					}
				});

				tfDescricaoCfop = new TextField();
				tfDescricaoCfop.setTabIndex(2000);
				tfDescricaoCfop.setReadOnly(true);
				tfDescricaoCfop.setWidth("649px");
						
				btSearchCfop = new Button();
				btSearchCfop.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCfop.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCfop.setTabIndex(300000);

				if(item.getItemProperty("cfop") != null && item.getItemProperty("cfop").getValue() != null){
					cfopSelecionado = (Cfop)item.getItemProperty("cfop").getValue();				
				}
				
				btSearchCfop.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						CfopUtil cUtil = new CfopUtil(true, true, OpusERP4UI.getEmpresa().getId(), false);
						cUtil.addListerner(new CfopUtil.CfopUtilListerner() {
							
							@Override
							public void onSelected(
									CfopUtil.CfopUtilEvent event) {
									if(event.getCfop() != null){
										tfCodCfop.setValue(event.getCfop().getId().toString());
										tfDescricaoCfop.setReadOnly(false);
										tfDescricaoCfop.setValue(event.getCfop().getDescricao());
										tfDescricaoCfop.setReadOnly(true);
										cfopSelecionado = event.getCfop();
										
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});
				
				
				FormLayout frmCodigoCfop = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
												
						addComponent(tfCodCfop);							
					}
				};
				addComponent(frmCodigoCfop);
		
				FormLayout frmButtonSearchFornecedor =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchCfop);							
					}
				}; 
							
				FormLayout frmDescFornecedor = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoCfop);							
					}
				}; 
				addComponent(frmButtonSearchFornecedor);
				addComponent(frmDescFornecedor);
				setExpandRatio(frmDescFornecedor, 1);		
				
				
				if(cfopSelecionado != null && tfCodCfop!= null){
					tfCodCfop.setValue(cfopSelecionado.getId().toString());
				}
				
				if(cfopSelecionado != null && tfDescricaoCfop != null){
					tfDescricaoCfop.setReadOnly(false);
					tfDescricaoCfop.setValue(cfopSelecionado.getDescricao());
					tfDescricaoCfop.setReadOnly(true);
				}
	     }
	  });
				
		List<MovimentoEntDetalhe> movimentoDetalhe = ProdutoDAO.movEntDet(Integer.parseInt(item.getItemProperty("id").getValue().toString()));
		List<MovimentoEntDetalhe> detalhes = new ArrayList<MovimentoEntDetalhe>();
		
		if(movimentoDetalhe!=null){
			for (MovimentoEntDetalhe movimentoEntDetalhe : movimentoDetalhe) {
				moviCab = ProdutoDAO.movEntCab(movimentoEntDetalhe.getMovimentoEntCabecalhoId());	
				
				if(moviCab != null && moviCab.getTipo() != null && moviCab.getTipo().equals("COMPRA")){
					detalhes.add(movimentoEntDetalhe);			
				}
			}
		
		
			produtoEditado = new ProdutoDAO().find(Integer.parseInt(item.getItemProperty("id").getValue().toString()));
			
			if(detalhes.size() > 0){
				moviDetalhe = detalhes.get(0);
			}
			
			
			
			
			Double valorDouble = Double.parseDouble(String.valueOf(moviDetalhe.getQuantidade() != null ? moviDetalhe.getQuantidade() : 0));															  
			DecimalFormat df = new DecimalFormat();  
			df.applyPattern("#,##0");  					
			valorFormatado = df.format(valorDouble);
			
		}
		
		HorizontalLayout hlUltimaCompra = new HorizontalLayout();
		//hlUltimaCompra.setWidth("100%");
		
		
		
		FormLayout fml1 = new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
																			
					TextField txtUltCompra = new TextField ("Última Compra (R$)");				
					txtUltCompra.setWidth("137px");				
					txtUltCompra.addStyleName("caption-align-editar-titulo");
					txtUltCompra.setNullRepresentation("");
					
					if(moviCab!=null && moviDetalhe != null && moviDetalhe.getValorCusto() != null){					
						txtUltCompra.setValue(Real.formatDbToString(moviDetalhe.getValorCusto().toString()));						
					}
					
					txtUltCompra.setReadOnly(true); 
					addComponent(txtUltCompra);					
				}
		};
		
		FormLayout fml2 = new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
										
					TextField txtUltimaCompraOutrasInformacoes = new TextField();
					txtUltimaCompraOutrasInformacoes.setWidth("578px");					
					txtUltimaCompraOutrasInformacoes.setNullRepresentation("");
					
					if(moviCab!=null && produtoEditado != null && moviCab.getFornecedor() != null){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						String und = produtoEditado.getUnidadeProduto() != null ? produtoEditado.getUnidadeProduto().getNome() : "UN";
						String ultcomp = valorFormatado +"-"+und+" | "+sdf.format(moviCab.getDataHora()) + moviCab.getFornecedor().getRazao_social() ;
						txtUltimaCompraOutrasInformacoes.setValue(ultcomp);						
					}
					
					txtUltimaCompraOutrasInformacoes.setReadOnly(true);
					addComponent(txtUltimaCompraOutrasInformacoes);	
			}
		};
		
		hlUltimaCompra.addComponent(fml1);
		hlUltimaCompra.addComponent(fml2);
		//hlUltimaCompra.setExpandRatio(fml2, 1f);
		
		vlRoot.addComponent(hlUltimaCompra);

		HorizontalLayout hl11 = new HorizontalLayout();
		hl11.setWidthUndefined();
		
		hl11.addComponent(new FormLayout(){
			{
					setStyleName("form-cutom-new");
					addStyleName("caption-align-produto-valor-custo-compras");
					
					setMargin(true);
					setSpacing(true);
						
					txtValorCusto = new TextField ("Valor Custo (R$)");				
					txtValorCusto.setWidth("80px");											
					txtValorCusto.setRequired(true);
					txtValorCusto.setMaxLength(200);
					txtValorCusto.setId("txtValorCusto");
					txtValorCusto.addStyleName("align-currency");
					txtValorCusto.setTabIndex(19);
					
					txtValorCusto.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							calcular(event.getText());
							
						}
					});
					
					if(item.getItemProperty("valorCusto").getValue() != null){
						txtValorCusto.setValue(Real.formatDbToString(item.getItemProperty("valorCusto").getValue().toString()));						
					}
					
					
					
					addComponent(txtValorCusto);				
					
					JavaScript.getCurrent().execute("$('#txtValorCusto').maskMoney({decimal:',',thousands:'.'})");
					
				}
			});
		hl11.addComponent(new FormLayout(){
			{
					setStyleName("form-cutom-new");
					addStyleName("caption-align-produto-valor-venda-compras");
					
					setMargin(true);
					setSpacing(true);
						
					
					txtPrecoInicVenda = new TextField ("Valor Venda (R$)");				
					txtPrecoInicVenda.setWidth("80px");				
					txtPrecoInicVenda.setRequired(true);
					txtPrecoInicVenda.setMaxLength(200);
					txtPrecoInicVenda.setId("txtPrecoInicVenda");
					txtPrecoInicVenda.addStyleName("align-currency");
					txtPrecoInicVenda.addStyleName("destaque-campo");
					txtPrecoInicVenda.setTabIndex(24);
					
					txtPrecoInicVenda.addTextChangeListener(new TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
							if(txtValorCusto.getValue() != null && !txtValorCusto.getValue().equals("0,00")){
								
								Float valorVenda = Real.formatStringToDBFloat("0,00");
								Float valorCustoTotal = Real.formatStringToDBFloat("0,00");
								
								if(event.getText() != null && !event.getText().equals("") && !event.getText().isEmpty()){
									valorVenda = Real.formatStringToDBFloat(event.getText());
								}													
								if(txtCustoTotal.getValue() != null && !txtCustoTotal.getValue().equals("") && !txtCustoTotal.getValue().isEmpty() && !txtValorCusto.getValue().equals("0,00")){
									valorCustoTotal = Real.formatStringToDBFloat(txtCustoTotal.getValue());

									Float lucro = valorVenda - valorCustoTotal;
									txtLucro.setReadOnly(false);
									txtLucro.setValue(Real.formatDbToString(String.valueOf(lucro)));
									txtLucro.setReadOnly(true);
									
									double p_lucro = ((valorVenda/valorCustoTotal)-1) * 100;
									
									txtMargemLucro.setReadOnly(false);														
									txtMargemLucro.setValue(Real.formatDbToString(String.valueOf(p_lucro)));														
									txtMargemLucro.setReadOnly(true);
									
									if(valorVenda < valorCustoTotal){
										event.getComponent().addStyleName("invalid");
									}else{
										event.getComponent().removeStyleName("invalid");
									}
								}
								
								
							}
								
						}
					});
					
					
					if(item.getItemProperty("valorVenda").getValue() != null){
						txtPrecoInicVenda.setValue(Real.formatDbToString(item.getItemProperty("valorVenda").getValue().toString()));
					}
					
					addComponent(txtPrecoInicVenda);				
					JavaScript.getCurrent().execute("$('#txtPrecoInicVenda').maskMoney({decimal:',',thousands:'.'})");
					
				}
			});
		
		vlRoot.addComponent(hl11); 
		
		HorizontalLayout hl12 = new HorizontalLayout();
		hl12.setWidthUndefined();
		
		hl12.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				addStyleName("caption-align-produto-ipi-compras");
				
				setMargin(true);
				setSpacing(true);
				
				txtIPI = new TextField ("IPI (%)");				
				txtIPI.setWidth("56px");				
				txtIPI.setMaxLength(200);
				txtIPI.setId("taxaIpi");
				txtIPI.addStyleName("align-currency");
				txtIPI.setRequired(true);
				txtIPI.setTabIndex(20);
			
				txtIPI.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						Float valorCusto = Real.formatStringToDBFloat("0,00");
						Float valorIpi = Real.formatStringToDBFloat("0,00");
						Float valorFrete = Real.formatStringToDBFloat("0,00");
						Float valorDiferencaAliquota = Real.formatStringToDBFloat("0,00");
						
						if(txtValorCusto.getValue() != null && !txtValorCusto.getValue().equals("") && !txtValorCusto.getValue().isEmpty()){
							valorCusto = Real.formatStringToDBFloat(txtValorCusto.getValue());
						}														
						
						if(event.getText() != null && !event.getText().equals("") && !event.getText().isEmpty()){
							valorIpi = Real.formatStringToDBFloat(event.getText());
						}
						
						if(txtFrete.getValue() != null && !txtFrete.getValue().equals("") && !txtFrete.getValue().isEmpty()){
							valorFrete = Real.formatStringToDBFloat(txtFrete.getValue());
						}		
						
						if(txtDiferencaAliquota.getValue() != null && !txtDiferencaAliquota.getValue().equals("") && !txtDiferencaAliquota.getValue().isEmpty()){
							valorDiferencaAliquota = Real.formatStringToDBFloat(txtDiferencaAliquota.getValue());
						}		
						
						Float somaPercentuais = valorIpi+valorFrete+valorDiferencaAliquota;
						
					    double percentual = somaPercentuais / 100.0; 
					    double valor_final = valorCusto + (percentual * valorCusto);
						
					    txtCustoTotal.setReadOnly(false);
					    txtCustoTotal.setValue(Real.formatDbToString(String.valueOf(valor_final)));
					    txtCustoTotal.setReadOnly(true);
					    
					    txtPrecoInicVenda.setValue(txtPrecoInicVenda.getValue());
					    
					    
					    Float valorVenda = Real.formatStringToDBFloat("0,00");
						Float valorCustoTotal = Real.formatStringToDBFloat("0,00");
						
						if(txtPrecoInicVenda.getValue() != null && !txtPrecoInicVenda.getValue().equals("") && !txtPrecoInicVenda.getValue().isEmpty()){
							valorVenda = Real.formatStringToDBFloat(txtPrecoInicVenda.getValue());
						}													
						if(txtCustoTotal.getValue() != null && !txtCustoTotal.getValue().equals("") && !txtCustoTotal.getValue().isEmpty() && !txtValorCusto.getValue().equals("0,00")){
							valorCustoTotal = Real.formatStringToDBFloat(txtCustoTotal.getValue());
					    
						    Float lucro = valorVenda - valorCustoTotal;
							txtLucro.setReadOnly(false);
							txtLucro.setValue(Real.formatDbToString(String.valueOf(lucro)));
							txtLucro.setReadOnly(true);
																		    												     
						    double p_lucro = ((valorVenda/valorCustoTotal)-1) * 100;
					
							txtMargemLucro.setReadOnly(false);
							txtMargemLucro.setValue(Real.formatDbToString(String.valueOf(p_lucro)));													
							txtMargemLucro.setReadOnly(true);
							
							if(valorVenda < valorCustoTotal){
								txtPrecoInicVenda.addStyleName("invalid");
							}else{
								txtPrecoInicVenda.removeStyleName("invalid");
							}
						}
					}
				});
				
				if(item.getItemProperty("taxaIpi").getValue() != null){
					txtIPI.setValue(Real.formatDbToString(item.getItemProperty("taxaIpi").getValue().toString()));
				}else{
					txtIPI.setValue("0,00");
				}
				
				
				addComponent(txtIPI);				
				
				JavaScript.getCurrent().execute("$('#taxaIpi').maskMoney({decimal:',',thousands:'.'})");
				
			}
		});
		hl12.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					addStyleName("caption-align-produto-lucro-compras");
					
					setMargin(true);
					setSpacing(true);
														
					txtLucro = new TextField ("Lucro (R$)");				
					txtLucro.setWidth("80px");
					txtLucro.setMaxLength(200);
					txtLucro.setId("txtLucro");
					txtLucro.addStyleName("align-currency");
					txtLucro.setTabIndex(25);
					
					
					if(item.getItemProperty("lucro").getValue() != null){
						txtLucro.setValue(Real.formatDbToString(item.getItemProperty("lucro").getValue().toString()));
					}
					txtLucro.setReadOnly(true);
					
					addComponent(txtLucro);				
					

				}
			});
		vlRoot.addComponent(hl12);
		
		HorizontalLayout hl13 = new HorizontalLayout();
		hl13.setWidthUndefined();
		
		hl13.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					addStyleName("caption-align-produto-frete");
					
					setMargin(true);
					setSpacing(true);
						
					txtFrete = new TextField ("Frete (%)");				
					txtFrete.setWidth("56px");				
					txtFrete.setRequired(true);
					txtFrete.setMaxLength(200);
					txtFrete.setId("txtFrete");
					txtFrete.addStyleName("align-currency");
					txtFrete.setTabIndex(21);
					
					txtFrete.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
							Float valorCusto = Real.formatStringToDBFloat("0,00");
							Float valorIpi = Real.formatStringToDBFloat("0,00");
							Float valorFrete = Real.formatStringToDBFloat("0,00");
							Float valorDiferencaAliquota = Real.formatStringToDBFloat("0,00");
							
							if(txtValorCusto.getValue() != null && !txtValorCusto.getValue().equals("") && !txtValorCusto.getValue().isEmpty()){
								valorCusto = Real.formatStringToDBFloat(txtValorCusto.getValue());
							}														
							
							if(txtIPI.getValue() != null && !txtIPI.getValue().equals("") && !txtIPI.getValue().isEmpty()){
								valorIpi = Real.formatStringToDBFloat(txtIPI.getValue());
							}
							
							if(event.getText() != null && !event.getText().equals("") && !event.getText().isEmpty()){
								valorFrete = Real.formatStringToDBFloat(event.getText());
							}		
							
							if(txtDiferencaAliquota.getValue() != null && !txtDiferencaAliquota.getValue().equals("") && !txtDiferencaAliquota.getValue().isEmpty()){
								valorDiferencaAliquota = Real.formatStringToDBFloat(txtDiferencaAliquota.getValue());
							}		
							
							Float somaPercentuais = valorIpi+valorFrete+valorDiferencaAliquota;
							
						    double percentual = somaPercentuais / 100.0; 
						    double valor_final = valorCusto + (percentual * valorCusto);
							
						    txtCustoTotal.setReadOnly(false);
						    txtCustoTotal.setValue(Real.formatDbToString(String.valueOf(valor_final)));
						    txtCustoTotal.setReadOnly(true);
						    
						    txtPrecoInicVenda.setValue(txtPrecoInicVenda.getValue());
						    
						    
						    Float valorVenda = Real.formatStringToDBFloat("0,00");
							Float valorCustoTotal = Real.formatStringToDBFloat("0,00");
							
							if(txtPrecoInicVenda.getValue() != null && !txtPrecoInicVenda.getValue().equals("") && !txtPrecoInicVenda.getValue().isEmpty()){
								valorVenda = Real.formatStringToDBFloat(txtPrecoInicVenda.getValue());
							}													
							if(txtCustoTotal.getValue() != null && !txtCustoTotal.getValue().equals("") && !txtCustoTotal.getValue().isEmpty() && !txtValorCusto.getValue().equals("0,00")){
								valorCustoTotal = Real.formatStringToDBFloat(txtCustoTotal.getValue());
						    
							    Float lucro = valorVenda - valorCustoTotal;
								txtLucro.setReadOnly(false);
								txtLucro.setValue(Real.formatDbToString(String.valueOf(lucro)));
								txtLucro.setReadOnly(true);
								
								double p_lucro = ((valorVenda/valorCustoTotal)-1) * 100;

								txtMargemLucro.setReadOnly(false);

								txtMargemLucro.setValue(Real.formatDbToString(String.valueOf(p_lucro)));
								txtMargemLucro.setReadOnly(true);
								
								if(valorVenda < valorCustoTotal){
									txtPrecoInicVenda.addStyleName("invalid");
								}else{
									txtPrecoInicVenda.removeStyleName("invalid");
								}
							}
						}
					});
					
					if(item.getItemProperty("frete").getValue() != null){
						txtFrete.setValue(Real.formatDbToString(item.getItemProperty("frete").getValue().toString()));												
					}else{
						txtFrete.setValue("0,00");
					}
					
					addComponent(txtFrete);				
					JavaScript.getCurrent().execute("$('#txtFrete').maskMoney({decimal:',',thousands:'.'})");
					
				}
			});
		hl13.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					addStyleName("caption-align-produto-margem-lucro");
					
					setMargin(true);
					setSpacing(true);
						
					
					txtMargemLucro = new TextField ("Margem de Lucro (%)");				
					txtMargemLucro.setWidth("80px");			
					txtMargemLucro.setMaxLength(200);
					txtMargemLucro.setId("txtMargemLucro");
					txtMargemLucro.addStyleName("align-currency");
					txtMargemLucro.setTabIndex(26);
										
					if(item.getItemProperty("margemLucro").getValue() != null){
						txtMargemLucro.setValue(Real.formatDbToString(item.getItemProperty("margemLucro").getValue().toString()));
					}
					txtMargemLucro.setReadOnly(true);
					
					addComponent(txtMargemLucro);				

				}
			});
		
		hl13.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom");
				addStyleName("caption-align-produto-utiliza-seriais");
				
				setMargin(true);
				setSpacing(true);
											
				cbUtilizaSeriais = new ComboBox ("Utiliza Seriais");
				cbUtilizaSeriais.setTextInputAllowed(false);
				cbUtilizaSeriais.setNullSelectionAllowed(false);
				cbUtilizaSeriais.setRequired(true);
				cbUtilizaSeriais.setWidth("55px");
				cbUtilizaSeriais.setImmediate(true);		
				cbUtilizaSeriais.addItem("SIM");
				cbUtilizaSeriais.addItem("NAO");
				cbUtilizaSeriais.setTabIndex(31);
				
				addComponent(cbUtilizaSeriais);				

				fieldGroup.bind(cbUtilizaSeriais, "utilizaSeriais");
				
				addComponent(cbUtilizaSeriais);					
			}
		});

		vlRoot.addComponent(hl13);
		
		HorizontalLayout hl14 = new HorizontalLayout();
		hl14.setWidth("875px");	
		hl14.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					txtDiferencaAliquota = new TextField ("Diferença Aliquota (%)");				
					txtDiferencaAliquota.setWidth("56px");				
					txtDiferencaAliquota.setStyleName("caption-align-produto-tab");	
					txtDiferencaAliquota.setRequired(true);
					txtDiferencaAliquota.setMaxLength(200);
					txtDiferencaAliquota.setId("txtAliquota");
					txtDiferencaAliquota.addStyleName("align-currency");
					txtDiferencaAliquota.setTabIndex(22);
					
					
					txtDiferencaAliquota.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							
							Float valorCusto = Real.formatStringToDBFloat("0,00");
							Float valorIpi = Real.formatStringToDBFloat("0,00");
							Float valorFrete = Real.formatStringToDBFloat("0,00");
							Float valorDiferencaAliquota = Real.formatStringToDBFloat("0,00");
							
							if(txtValorCusto.getValue() != null && !txtValorCusto.getValue().equals("") && !txtValorCusto.getValue().isEmpty()){
								valorCusto = Real.formatStringToDBFloat(txtValorCusto.getValue());
							}														
							
							if(txtIPI.getValue() != null && !txtIPI.getValue().equals("") && !txtIPI.getValue().isEmpty()){
								valorIpi = Real.formatStringToDBFloat(txtIPI.getValue());
							}
							
							if(txtFrete.getValue() != null && !txtFrete.getValue().equals("") && !txtFrete.getValue().isEmpty()){
								valorFrete = Real.formatStringToDBFloat(txtFrete.getValue());
							}		
							
							if(event.getText() != null && !event.getText().equals("") && !event.getText().isEmpty()){
								valorDiferencaAliquota = Real.formatStringToDBFloat(event.getText());
							}	
							
							Float somaPercentuais = valorIpi+valorFrete+valorDiferencaAliquota;
							
						    double percentual = somaPercentuais / 100.0; 
						    double valor_final = valorCusto + (percentual * valorCusto);
							
						    txtCustoTotal.setReadOnly(false);
						    txtCustoTotal.setValue(Real.formatDbToString(String.valueOf(valor_final)));
						    txtCustoTotal.setReadOnly(true);
						    
						    txtPrecoInicVenda.setValue(txtPrecoInicVenda.getValue());
						    
						    Float valorVenda = Real.formatStringToDBFloat("0,00");
							Float valorCustoTotal = Real.formatStringToDBFloat("0,00");
							
							if(txtPrecoInicVenda.getValue() != null && !txtPrecoInicVenda.getValue().equals("") && !txtPrecoInicVenda.getValue().isEmpty()){
								valorVenda = Real.formatStringToDBFloat(txtPrecoInicVenda.getValue());
							}													
							if(txtCustoTotal.getValue() != null && !txtCustoTotal.getValue().equals("") && !txtCustoTotal.getValue().isEmpty() && !txtValorCusto.getValue().equals("0,00")){
								valorCustoTotal = Real.formatStringToDBFloat(txtCustoTotal.getValue());
						    
							    Float lucro = valorVenda - valorCustoTotal;
								txtLucro.setReadOnly(false);
								txtLucro.setValue(Real.formatDbToString(String.valueOf(lucro)));
								txtLucro.setReadOnly(true);
								
								double p_lucro = ((valorVenda/valorCustoTotal)-1) * 100;
								
								txtMargemLucro.setReadOnly(false);														
								txtMargemLucro.setValue(Real.formatDbToString(String.valueOf(p_lucro)));														
								txtMargemLucro.setReadOnly(true);
								
								if(valorVenda < valorCustoTotal){
									txtPrecoInicVenda.addStyleName("invalid");
								}else{
									txtPrecoInicVenda.removeStyleName("invalid");
								}
							}
						}
					});
					
					if(item.getItemProperty("diferenca_aliquota").getValue() != null){
						
						if(produtoEditado == null){
							produtoEditado = new ProdutoDAO().find(Integer.parseInt(item.getItemProperty("id").getValue().toString()));
						}
						
						if(!produtoEditado.getFornecedorId().getUf().equals(OpusERP4UI.getEmpresa().getUf())){
							txtDiferencaAliquota.setValue(Real.formatDbToString(item.getItemProperty("diferenca_aliquota").getValue().toString()));							
						}else{
							txtDiferencaAliquota.setValue("0,00");
						}
					}else{
						txtDiferencaAliquota.setValue("0,00");
					}

					addComponent(txtDiferencaAliquota);				
					
					JavaScript.getCurrent().execute("$('#txtAliquota').maskMoney({decimal:',',thousands:'.'})");
					
				}
			});
		
		vlRoot.addComponent(hl14); 
		
		HorizontalLayout hl15 = new HorizontalLayout();
		hl15.setWidthUndefined();							
		
		hl15.addComponent(new FormLayout(){
			{
					setStyleName("form-cutom");
					addStyleName("caption-align-produto-custo-total-compras");
					
					setMargin(true);
					setSpacing(true);
														
					txtCustoTotal = new TextField("Custo Total (R$)");				
					txtCustoTotal.setNullRepresentation("");
					txtCustoTotal.setWidth("80px");
					txtCustoTotal.addStyleName("align-currency");
					txtCustoTotal.setId("tfCustoMedio");
					txtCustoTotal.setTabIndex(23);
					
					if(item.getItemProperty("custo_total").getValue() != null){
						txtCustoTotal.setValue(Real.formatDbToString(item.getItemProperty("custo_total").getValue().toString()));
					}else{
						txtCustoTotal.setValue("0,00");
					}
					
					txtCustoTotal.setReadOnly(true);
					addComponent(txtCustoTotal);			
					
				}
			});
		hl15.addComponent(new FormLayout(){
			{
					setStyleName("form-cutom");
					addStyleName("caption-align-produto-qtd-minima");
					
					setMargin(true);
					setSpacing(true);
														
					txtEstoqMin = new TextField ("Quantidade Mínima");				
					txtEstoqMin.setWidth("80px");					
					txtEstoqMin.setRequired(true);
					txtEstoqMin.setMaxLength(200);
					txtEstoqMin.setId("txtEstoqMin");
					txtEstoqMin.addStyleName("align-currency");
					txtEstoqMin.setTabIndex(27);
					
					addComponent(txtEstoqMin);		
					
					
					if(item.getItemProperty("estoqueMin").getValue() != null){
						txtEstoqMin.setValue(Real.formatDbToString(item.getItemProperty("estoqueMin").getValue().toString()));
					}else{
						txtEstoqMin.setValue("0,00");
					}
//					JavaScript.getCurrent().execute("$('#txtEstoqMin').mask('0000000000')");
					
					JavaScript.getCurrent().execute("$('#txtEstoqMin').maskMoney({decimal:',',thousands:'.'})");
					
				}
			});
		
		vlRoot.addComponent(hl15); 

		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					addStyleName("caption-align-produto-saldo-compras");
					
					setMargin(true);
					setSpacing(true);
														
					TextField txtSaldo = new TextField ("Saldo");				
					txtSaldo.setWidth("80px");				
					txtSaldo.setStyleName("caption-align-produto-saldo");	
//					txtSaldo.setNullRepresentation("");
					txtSaldo.setMaxLength(200);
					txtSaldo.setId("txtSaldo");
					txtSaldo.addStyleName("align-currency");
					txtSaldo.setTabIndex(28);
					
					if(item.getItemProperty("qtdEstoque").getValue() != null){
						txtSaldo.setValue(Real.formatDbToString(item.getItemProperty("qtdEstoque").getValue().toString()));
					}
//					if(copiar){
//						txtSaldo.setValue("");
//					}
					
					if(item.getItemProperty("qtdEstoque").getValue() != null && (Float)item.getItemProperty("qtdEstoque").getValue()<= new Float(0)){
						txtSaldo.addStyleName("invalid-serial");
					}else{
						txtSaldo.addStyleName("destaque-campo");						
					}
					
					txtSaldo.setReadOnly(true);
					
					addComponent(txtSaldo);				
					JavaScript.getCurrent().execute("$('#txtSaldo').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		

		calcular(txtValorCusto.getValue());
	}
	
	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
					if(txtIPI.getValue().equals("")){
						txtIPI.setValue("0,00");
					}
					
					if(txtFrete.getValue().equals("")){
						txtFrete.setValue("0,00");
					}
					
					if(txtDiferencaAliquota.getValue().equals("")){
						txtDiferencaAliquota.setValue("0,00");
					}
					
					if(txtEstoqMin.getValue().equals("")){
						txtEstoqMin.setValue("0,00");
					}
							
					if(fieldGroup.isValid() && grupoSelecionado != null && marcaSelecionado != null && origemSelecionado != null && ncmSelecionado != null && cfopSelecionado != null && 
							txtValorCusto.isValid() && txtPrecoInicVenda.isValid() && txtIPI.isValid() && txtFrete.isValid() && txtDiferencaAliquota.isValid() && 	txtEstoqMin.isValid() && 
							cstSelecionado != null && csosnSelecionado != null && cstecfSelecionado != null  && txtEstoqMin.isValid()){
						try {
							if(item.getItemProperty("id").getValue() != null){
								Produto p = ProdutoDAO.find((Integer)item.getItemProperty("id").getValue());
								item.getItemProperty("qtdEstoque").setValue(p != null && p.getQtdEstoque() != null ? p.getQtdEstoque() : 0);
							}							
												
							
							item.getItemProperty("taxaIcms").setValue(new Float(OpusERP4UI.getEmpresa().getAliquota_interna()));
							item.getItemProperty("cst_origem").setValue(origemSelecionado);
							item.getItemProperty("cst_forma_tributo").setValue(cstSelecionado);
							item.getItemProperty("ecf_cup_filcal").setValue(cstecfSelecionado);
							item.getItemProperty("simples_nacional").setValue(csosnSelecionado);
							item.getItemProperty("cfop").setValue(cfopSelecionado);
							item.getItemProperty("grupoId").setValue(grupoSelecionado);
							item.getItemProperty("marcasId").setValue(marcaSelecionado);
							
							
							if(item.getItemProperty("id").getValue() == null){
								item.getItemProperty("qtdEstoque").setValue(new Float(0));
							}							
							if(txtEstoqMin != null && txtEstoqMin.getValue() != null){
								item.getItemProperty("estoqueMin").setValue(Real.formatStringToDBFloat(txtEstoqMin.getValue()));
							}
							if(txtEstoqMax != null && txtEstoqMax.getValue() != null){
								item.getItemProperty("estoqueMax").setValue(Real.formatStringToDBFloat(txtEstoqMax.getValue()));
							}
							if(txtPrecoInicVenda != null && txtPrecoInicVenda.getValue() != null){
								item.getItemProperty("valorVenda").setValue(Real.formatStringToDBFloat(txtPrecoInicVenda.getValue()));
							}							
							if(txtValorCusto != null && txtValorCusto.getValue() != null){
								item.getItemProperty("valorCusto").setValue(Real.formatStringToDBFloat(txtValorCusto.getValue()));
							}
							if(txtIPI != null && txtIPI.getValue() != null){
								item.getItemProperty("taxaIpi").setValue(Real.formatStringToDBFloat(txtIPI.getValue()));
							}
							if(txtFrete != null && txtFrete.getValue() != null){
								item.getItemProperty("frete").setValue(Real.formatStringToDBFloat(txtFrete.getValue()));
							}
							if(txtDiferencaAliquota != null && txtDiferencaAliquota.getValue() != null){
								item.getItemProperty("diferenca_aliquota").setValue(Real.formatStringToDBFloat(txtDiferencaAliquota.getValue()));
							}
							if(txtEstoqMin != null && txtEstoqMin.getValue() != null){
								item.getItemProperty("estoqueMin").setValue(Real.formatStringToDBFloat(txtEstoqMin.getValue()));
							}
							if(txtCustoTotal != null && txtCustoTotal.getValue() != null){
								item.getItemProperty("custo_total").setValue(Real.formatStringToDBFloat(txtCustoTotal.getValue()));
							}
							if(txtMargemLucro != null && txtMargemLucro.getValue() != null){
								item.getItemProperty("margemLucro").setValue(Real.formatStringToDBFloat(txtMargemLucro.getValue()));
							}
							
							if(txtLucro != null && txtLucro.getValue() != null){
								item.getItemProperty("lucro").setValue(Real.formatStringToDBFloat(txtLucro.getValue()));
							}
														
							if(ncmSelecionado != null){
								item.getItemProperty("ncm").setValue(ncmSelecionado.getCodigo().toString()); 
							}else{
								item.getItemProperty("ncm").setValue(null);
							}
							
							if(cestSelecionado != null){
								item.getItemProperty("cest").setValue(cestSelecionado.getCest());
							}else{
								item.getItemProperty("cest").setValue(null); 
							}
							
							fieldGroup.commit();				
							fireEvent(new ProdutoFastEditorEvent(getUI(), item, true));					
							
						} catch (Exception e) {					
							e.printStackTrace();
							Notify.Show("ERRO: "+e.getLocalizedMessage(),Notify.TYPE_ERROR);
						}
					}else{
						
						for (Field<?> field: fieldGroup.getFields()) {
							
							if(!field.isValid()){
								field.addStyleName("invalid-txt");
							}else{
								field.removeStyleName("invalid-txt");
							}
						}
						
						if(grupoSelecionado == null){
							txtCodGrupo.addStyleName("invalid-txt");
						}else{
							txtCodGrupo.removeStyleName("invalid-txt");
						}
						
						if(marcaSelecionado == null){
							txtCodMarca.addStyleName("invalid-txt");
						}else{
							txtCodMarca.removeStyleName("invalid-txt");
						}
						
						if(ncmSelecionado == null){
							txtCodNcm.addStyleName("invalid-txt");
						}else{
							txtCodNcm.removeStyleName("invalid-txt");
						}
												
						if(origemSelecionado == null){
							tfCodOrigem.addStyleName("invalid-txt");
						}else{
							tfCodOrigem.removeStyleName("invalid-txt");
						}
						
						if(csosnSelecionado == null){
							tfCodCsosn.addStyleName("invalid-txt");
						}else{
							tfCodCsosn.removeStyleName("invalid-txt");
						}
						
						if(cstecfSelecionado == null){
							tfCodCstEcf.addStyleName("invalid-txt");
						}else{
							tfCodCstEcf.removeStyleName("invalid-txt");
						}
						
						if(cfopSelecionado == null){
							tfCodCfop.addStyleName("invalid-txt");
						}else{
							tfCodCfop.removeStyleName("invalid-txt");
						}
						
						
						
						
						
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
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new ProdutoFastEditorEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(fieldGroup.isValid()){
									try {		
										
										if(item.getItemProperty("id").getValue() != null){
											item.getItemProperty("qtdEstoque").setValue(ProdutoDAO.find((Integer)item.getItemProperty("id").getValue()).getQtdEstoque());
										}
										
										fieldGroup.commit();				
										fireEvent(new ProdutoFastEditorEvent(getUI(), item, true));					
										
									} catch (Exception e) {					
										e.printStackTrace();
										Notify.Show("ERRO: "+e.getLocalizedMessage(),Notify.TYPE_ERROR);
									}
								}else{
									
									for (Field<?> field: fieldGroup.getFields()) {
										
										if(!field.isValid()){
											field.addStyleName("invalid-txt");
										}else{
											field.removeStyleName("invalid-txt");
										}
									}
									
									Notify.Show_Invalid_Submit_Form();
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new ProdutoFastEditorEvent(getUI(), item, false));
								close();						
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
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
	
	private void calcular(String valorEvent){
		Float valorCusto = Real.formatStringToDBFloat("0,00");
		Float valorIpi = Real.formatStringToDBFloat("0,00");
		Float valorFrete = Real.formatStringToDBFloat("0,00");
		Float valorDiferencaAliquota = Real.formatStringToDBFloat("0,00");
		
		if(valorEvent != null && !valorEvent.equals("") && !valorEvent.isEmpty()){
			valorCusto = Real.formatStringToDBFloat(valorEvent);
		}														
		
		if(txtIPI != null && txtIPI.getValue() != null && !txtIPI.getValue().equals("") && !txtIPI.getValue().isEmpty()){
			valorIpi = Real.formatStringToDBFloat(txtIPI.getValue());
		}
		
		if(txtFrete != null && txtFrete.getValue() != null && !txtFrete.getValue().equals("") && !txtFrete.getValue().isEmpty()){
			valorFrete = Real.formatStringToDBFloat(txtFrete.getValue());
		}		
		
		if(txtDiferencaAliquota != null && txtDiferencaAliquota.getValue() != null && !txtDiferencaAliquota.getValue().equals("") && !txtDiferencaAliquota.getValue().isEmpty()){
			valorDiferencaAliquota = Real.formatStringToDBFloat(txtDiferencaAliquota.getValue());
		}		
		
		Float somaPercentuais = valorIpi+valorFrete+valorDiferencaAliquota;
		
	    double percentual = somaPercentuais / 100.0; 
	    double valor_final = valorCusto + (percentual * valorCusto);
		
	    if(valorEvent.equals("0,00")){
	    	txtCustoTotal.setReadOnly(false);
		    txtCustoTotal.setValue("0,00");
		    txtCustoTotal.setReadOnly(true);
	    }else{
	    	txtCustoTotal.setReadOnly(false);
		    txtCustoTotal.setValue(Real.formatDbToString(String.valueOf(valor_final)));
		    txtCustoTotal.setReadOnly(true);
	    }
	    
	    //Atualizar Custo
	    if(txtPrecoInicVenda.getValue() != null && valorEvent != null && !valorEvent.equals("0,00")){
			    Float valorVenda = Real.formatStringToDBFloat("0,00");
				Float valorCustoTotal = Real.formatStringToDBFloat("0,00");
				
				if(txtPrecoInicVenda.getValue() != null && !txtPrecoInicVenda.getValue().equals("") && !txtPrecoInicVenda.getValue().isEmpty()){
					valorVenda = Real.formatStringToDBFloat(txtPrecoInicVenda.getValue());
				}													
				if(txtCustoTotal.getValue() != null && !txtCustoTotal.getValue().equals("") && !txtCustoTotal.getValue().isEmpty()){
					valorCustoTotal = Real.formatStringToDBFloat(txtCustoTotal.getValue());
				}
				
				Float lucro = valorVenda - valorCustoTotal;
				txtLucro.setReadOnly(false);
				txtLucro.setValue(Real.formatDbToString(String.valueOf(lucro)));
				txtLucro.setReadOnly(true);
				
				double p_lucro = ((valorVenda/valorCustoTotal)-1) * 100;
				
				txtMargemLucro.setReadOnly(false);															
				txtMargemLucro.setValue(Real.formatDbToString(String.valueOf(p_lucro)));														
				txtMargemLucro.setReadOnly(true);
				
				if(valorVenda < valorCustoTotal){
					txtPrecoInicVenda.addStyleName("invalid");
				}else{
					txtPrecoInicVenda.removeStyleName("invalid");
				}
	    }else{
	    	txtLucro.setReadOnly(false);
			txtLucro.setValue("0,00");
			txtLucro.setReadOnly(true);
	   
			txtMargemLucro.setReadOnly(false);
			txtMargemLucro.setValue("0,00");
			txtMargemLucro.setReadOnly(true);
	    	
			txtPrecoInicVenda.removeStyleName("invalid");
			txtPrecoInicVenda.setValue("0,00");
	    	
	    }
	}
	public void addListerner(ProdutoFastEditorListerner target){
		try {
			Method method = ProdutoFastEditorListerner.class.getDeclaredMethod("onClose", ProdutoFastEditorEvent.class);
			addListener(ProdutoFastEditorEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ProdutoFastEditorListerner target){
		removeListener(ProdutoFastEditorEvent.class, target);
	}
	public static class ProdutoFastEditorEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public ProdutoFastEditorEvent(Component source, Item item, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;			
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ProdutoFastEditorListerner extends Serializable{
		public void onClose(ProdutoFastEditorEvent event);
	}
	private JPAContainer<Marca> getMarcas(){
		JPAContainer<Marca> container = JPAContainerFactory.make(Marca.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		container.sort(new Object[]{"nome"}, new boolean[]{true});
		return container;
	}
	
}
