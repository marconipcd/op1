package com.digital.opuserp.view.modulos.estoque.produto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CestDAO;
import com.digital.opuserp.dao.CfopDAO;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.CofinsDAO;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.CstDAO;
import com.digital.opuserp.dao.CstFormTributoDAO;
import com.digital.opuserp.dao.EcfEmissorCupFiscalDAO;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.dao.FotoProdutoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.GrupoProdutoDAO;
import com.digital.opuserp.dao.IcmsDAO;
import com.digital.opuserp.dao.IpiDAO;
import com.digital.opuserp.dao.LogProdutoDAO;
import com.digital.opuserp.dao.MarcaDAO;
import com.digital.opuserp.dao.NcmDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.PisDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.SeriaisCompraDAO;
import com.digital.opuserp.dao.SerialDAO;
import com.digital.opuserp.dao.SimplesNacionalDAO;
import com.digital.opuserp.domain.CatLojaProduto;
import com.digital.opuserp.domain.CategoriaLojaIntegrada;
import com.digital.opuserp.domain.Cest;
import com.digital.opuserp.domain.Cfop;
import com.digital.opuserp.domain.Cofins;
import com.digital.opuserp.domain.CstCofins;
import com.digital.opuserp.domain.CstFormaTributo;
import com.digital.opuserp.domain.CstIpi;
import com.digital.opuserp.domain.CstOrigem;
import com.digital.opuserp.domain.CstPis;
import com.digital.opuserp.domain.EcfEmissorCupFiscal;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.FotosProdutos;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.Icms;
import com.digital.opuserp.domain.Ipi;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.Marca;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Ncm;
import com.digital.opuserp.domain.Pis;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SeriaisCompra;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.domain.SimplesNacional;
import com.digital.opuserp.domain.TipoItemProduto;
import com.digital.opuserp.domain.UnidadeProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.FtpUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.MarcaUtil;
import com.digital.opuserp.util.MarcaUtil.MarcaUtilEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.CestUtil;
import com.digital.opuserp.view.util.CestUtil.CestEvent;
import com.digital.opuserp.view.util.CfopUtil;
import com.digital.opuserp.view.util.CstFormTributoUtil;
import com.digital.opuserp.view.util.CstOrigemUtil;
import com.digital.opuserp.view.util.EcfEmissorCupFiscalUtil;
import com.digital.opuserp.view.util.FornecedorUtil;
import com.digital.opuserp.view.util.GrupoProdutoUtil;
import com.digital.opuserp.view.util.GrupoProdutoUtil.GrupoUtilEvent;
import com.digital.opuserp.view.util.NcmUtil;
import com.digital.opuserp.view.util.NcmUtil.NcmEvent;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.SimplesNacionalUtil;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class ProdutoEditor extends Window {

	boolean valid_cod_barras;
	TextField txtCodBarras;
	
	TabSheet tbPrincipal;
	Item item;
	Button btSalvar;
	Button btEditar;
	Button btCancelar;
	Button btCompra;
	Button btSerial;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	VerticalLayout vlRootImpostos;
	VerticalLayout vlRootLojaIntegrada;
	TextField txtNome;
	TextField txtUltCompra;
	ComboBox cbItem;
	TextField tfRef;
	private TextField txtCustoTotal;
		
	boolean isFirefox;
	boolean isChrome;
	
	Ncm ncmSelecionado;
	Cest cestSelecionado;
	private TextField txtDescricaoNcm;
	private TextField txtDescricaoCest;
	
	private GerenciarModuloDAO gmDAO;
	
	private TextField txtEstoqMin; 	
	private TextField txtEstoqMax;
	private ComboBox cbSituacaoTributaria;
	private ComboBox cbIat;
	private ComboBox cbIppt;
	ComboBox cbDesconto;
	ComboBox cbFracionar;
	ComboBox cbUtilizaSeriais;
	TextField tfCod;
	Integer codProduto;
	ComboBox cbPermitirOutrasTabela;
	boolean permitir_outras_tabelas = true;
	
	Button btSearchGrupo;
	Button btSearchMarca;
	Button btSearchFornecedor;
	Button btSearchOrigem;
	Button btSearchCfop;
	Button btSearchCst;
	Button btSearchCSOSN;
	Button btSearchECF;
	
	private TextField txtLucro;
	private TextField txtPrecoPromocional;
	private TextField txtPrecoInicVenda;
	private TextField txtMargemLucro;
	private TextField txtDespesaOp;
	private TextField txtDiferencaAliquota;
	private TextField txtEncargo;
	
	private TextField txtIPI;
	private TextField txtAliquotaIcms;
	private TextField txtFrete;
	private TextField txtValorCusto;
	
	Produto produto;
	
	boolean copiar;
	boolean editar;
	boolean codigoBarras;
	String codBarras;
	Integer fracionar;
	Integer codSubModulo;
	
	private Fornecedor fornecedorSelecionado;
	private TextField tfCodFornecedor;
	private TextField tfDescricaoFornecedor;
	private TextField txtCodGrupo;
	private TextField txtCodMarca;
	private TextField txtDescricaoGrupoProd;
	private TextField txtDescricaoMarca;

	private Cfop cfopSelecionado;
	private TextField tfCodCfop;
	private TextField tfDescricaoCfop;

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
	
	private GrupoProduto grupoSelecionado;
	private Marca marcaSelecionado;
	
	TextField dfdataCadastro;
	TextField dfdataAlteracao;
	ComboBox cbStatus;
	
	Icms icms = new Icms();
	private ComboBox cbModBcIcms;
	private TextField txtBaseCalculoICMS;
	private TextField txtAliquotaICMS;
	private ComboBox cbModBcIcmsSt;
	private TextField txtMVAIcmsST;
	private TextField txtBaseCalculoICMSST;
	private TextField txtAliquotaICMSST;
	
	Ipi ipi = new Ipi();
	private ComboBox cbCstIpi;
	private TextField txtCnpjProdutor;
	private ComboBox cbTipoCalculo;
	private TextField txtValorBaseCalculoIpi;
	private TextField txtAliquotaIpi;
	private TextField txtValorIpi;
	
	Pis pis = new Pis();
	private ComboBox cbCstPis;
	private ComboBox cbTipoCalculoPis;
	private TextField txtBaseCalculoPis;
	private TextField txtAliquotaPis;
	private TextField txtValorPis;
	private ComboBox cbCstPisStTipoCalculo;
	private TextField txtBaseCalculoPisSt;
	private TextField txtAliquotaPisSt;
	private TextField txtValorPisSt;
	
	Cofins cofins = new Cofins();
	private ComboBox cbCstCofins;
	private ComboBox cbTipoCalculoCofins;
	private TextField txtBaseCalculoCofins;
	private TextField txtAliquotaCofins;
	private TextField txtValorCofins;
	private ComboBox cbTipoCalculoCofinsSt;
	private TextField txtBaseCalculoCofinsSt;
	private TextField txtAliquotaCofinsSt;
	private TextField txtValorCofinsSt;
	
	ComboBox cbProdutoAtivado;
	ComboBox cbProdutoDestaque;
	TextField txtUrlYoutube;
	ComboBox cbSicronizar;
	
	public ProdutoEditor(final Item item, String title, boolean modal,boolean codigoBarras,final boolean copiar, final boolean editar, Integer codSubModulo){
		this.isFirefox = OpusERP4UI.getCurrent().getPage().getWebBrowser().isFirefox();
		this.isChrome = OpusERP4UI.getCurrent().getPage().getWebBrowser().isChrome();
		
		this.item = item;
		this.codigoBarras = codigoBarras;
		this.copiar = copiar;	
		this.editar = editar;	
		this.codSubModulo = codSubModulo;	
		
		
		setWidth("983px");
		setHeight("654px");
		
		Styles style = Page.getCurrent().getStyles();		
		style.add(".dashboard .v-filterselect-suggestpopup-sim-nao > .popupContent  > .v-filterselect-suggestmenu{width:93px;}");		
		
		gmDAO = new GerenciarModuloDAO();
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		
		vlRootImpostos = new VerticalLayout();	
		vlRootImpostos.addStyleName("sem-padding-tal");
		vlRootImpostos.setMargin(false);
		vlRootImpostos.setSpacing(false); 
		vlRootImpostos.setWidth("100%");
		vlRootImpostos.setMargin(true);	
		
		vlRootLojaIntegrada= new VerticalLayout();	
		vlRootLojaIntegrada.addStyleName("sem-padding-tal");
		vlRootLojaIntegrada.setMargin(false);
		vlRootLojaIntegrada.setSpacing(false); 
		vlRootLojaIntegrada.setWidth("100%");
		vlRootLojaIntegrada.setMargin(true);	
		
		final VerticalLayout vlCategorias = new  VerticalLayout(){			
			{
				
				Integer id = (Integer)item.getItemProperty("id").getValue();
				final Produto produto1 = ProdutoDAO.find(id);
				addComponent(new Label("CATEGORIAS"));
				
				final EntityManager em = ConnUtil.getEntity();
				Query q = em.createQuery("select c from CategoriaLojaIntegrada c ", CategoriaLojaIntegrada.class);
				
				Table tb0 = new Table();
				tb0.addContainerProperty("id", Integer.class, "");
				tb0.addContainerProperty("nome", String.class, "");
				
				tb0.addGeneratedColumn("", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(final Table source, final Object itemId,final Object columnId) {
						final Integer id = (Integer)source.getItem(itemId).getItemProperty("id").getValue();
						
						final CheckBox check = new CheckBox();
						final Query q = em.createQuery("select c from CatLojaProduto c where c.produto=:p and c.categoria=:c", CatLojaProduto.class);
						q.setParameter("p", produto1);
						q.setParameter("c", new CategoriaLojaIntegrada(id));
						
						if(q.getResultList().size() > 0){
							check.setValue(true);
						}else{
							check.setValue(false);
						}
						
						
						check.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								em.getTransaction().begin();
								if(check.getValue()){	
									em.persist(new CatLojaProduto(null, produto1, new CategoriaLojaIntegrada(id)));
								}else{
									for (CatLojaProduto o : (List<CatLojaProduto>)q.getResultList()) {
										em.remove(o); 
									}
								}
								em.getTransaction().commit();
							}
						});
						
						return check;
					}
				});
				
				
				for (CategoriaLojaIntegrada c : (List<CategoriaLojaIntegrada>) q.getResultList()) {					
					tb0.addItem(new Object[]{c.getId(), c.getNome()}, tb0.getItemIds().size()+1);
				}
				
				addComponent(tb0); 
				
			}
		};
				
		vlRootLojaIntegrada.addComponent(new HorizontalLayout(){
			{
				
				setSizeFull(); 
				setHeight("233px");
				addComponent(new FormLayout(){
							{
								
							
								
								cbSicronizar = new ComboBox("Sicronizar");
								cbSicronizar.setNullSelectionAllowed(false);
								cbSicronizar.setStyleName("caption-align");
								cbSicronizar.addItem("SIM");
								cbSicronizar.addItem("NAO");
								
								if(item != null && item.getItemProperty("sic_loja").getValue() != null){
									if((boolean)item.getItemProperty("sic_loja").getValue()){
										cbSicronizar.select("SIM");
									}else{
										cbSicronizar.select("NAO");
									}
								}else{
									cbSicronizar.select("NAO");
								}
								
								
								cbProdutoAtivado = new ComboBox("Produto Ativado");
								cbProdutoAtivado.setNullSelectionAllowed(false);
								cbProdutoAtivado.setStyleName("caption-align");
								cbProdutoAtivado.addItem("SIM");
								cbProdutoAtivado.addItem("NAO");
								
								if(item != null && item.getItemProperty("produto_ativado").getValue() != null){
									if(item.getItemProperty("produto_ativado").getValue().toString().equals("SIM")){
										cbProdutoAtivado.select("SIM");
									}else{
										cbProdutoAtivado.select("NAO");
									}
								}else{
									cbProdutoAtivado.select("NAO");
								}
								
								cbProdutoAtivado.addValueChangeListener(new Property.ValueChangeListener() {
									
									@Override
									public void valueChange(ValueChangeEvent event) {
										
									}
								});
																
								cbProdutoDestaque = new ComboBox("Produto Destaque");
								cbProdutoAtivado.setNullSelectionAllowed(false);
								cbProdutoDestaque.setStyleName("caption-align");
								cbProdutoDestaque.addItem("SIM");
								cbProdutoDestaque.addItem("NAO");
								
								if(item != null && item.getItemProperty("produto_destaque").getValue() != null){
									if(item.getItemProperty("produto_destaque").getValue().toString().equals("SIM")){
										cbProdutoDestaque.select("SIM");
									}else{
										cbProdutoDestaque.select("NAO");
									}
								}else{
									cbProdutoDestaque.select("NAO");
								}
								
								cbProdutoDestaque.addValueChangeListener(new Property.ValueChangeListener() {
									
									@Override
									public void valueChange(ValueChangeEvent event) {
										
									}
								});
								
								txtUrlYoutube = new TextField("URL YouTube");
								txtUrlYoutube.setStyleName("caption-align");
								txtUrlYoutube.setWidth("146px");
								
								addComponent(cbSicronizar);
								addComponent(cbProdutoAtivado);
								addComponent(cbProdutoDestaque);
								addComponent(txtUrlYoutube); 
							}
							
				});
				
				Panel pnLogo = new Panel("Foto");
				pnLogo.setStyleName("pn-img-osp");
			
				pnLogo.setContent(new VerticalLayout(){
					{
						Upload imageUploaded = new Upload(null,null);
						imageUploaded.setButtonCaption("Upload");	
												
						ImageUploader uploader = new ImageUploader();
						imageUploaded.setReceiver(uploader);
						imageUploaded.addSucceededListener(uploader);
						
						List<FotosProdutos> fotos = FotoProdutoDAO.getFotosProduto((Integer)item.getItemProperty("id").getValue());
						
						if(fotos.size() == 1){
							FotosProdutos fp = fotos.get(0);							
							
							try{
								
								String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
								file = new File(basepath + "/WEB-INF/uploads/logo.jpeg");
								
								//writeFile(file, fp.getFoto());
								img = new Image(null, new ExternalResource("https://digitalonline.com.br/loja/fotos_produtos/"+fp.getEnderecoFoto()));
							}catch(Exception e){
								e.printStackTrace();							
							}
						}else{
							img = new Image(null, new ThemeResource("img/default_logo.png"));
						}
												
						img.setWidth("180px");
						img.setHeight("180px");
						
						addComponent(img);
						addComponent(imageUploaded);
						
						setComponentAlignment(img, Alignment.MIDDLE_CENTER);
						setComponentAlignment(imageUploaded, Alignment.MIDDLE_CENTER);
					}
				});
				
				addComponent(pnLogo);
				
				addComponent(vlCategorias);
			}
		}); 
		
		Button btSalvarLoja = new Button("Salvar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				EntityItem<Produto> itemProduto = (EntityItem)item;
				
				
				
				if(cbSicronizar != null && cbSicronizar.getValue() != null){
					
					if(cbSicronizar.getValue().toString().equals("SIM")){
						itemProduto.getEntity().setSic_loja(true);						
					}else{						
						itemProduto.getEntity().setSic_loja(false);
					}
				}
				
				if(cbProdutoAtivado != null && cbProdutoAtivado.getValue() != null){
					itemProduto.getEntity().setProduto_ativado(cbProdutoAtivado.getValue().toString());					
				}
				
				if(cbProdutoDestaque != null && cbProdutoDestaque.getValue() != null){
					itemProduto.getEntity().setProduto_destaque(cbProdutoDestaque.getValue().toString());
				}
				
				if(txtUrlYoutube != null && txtUrlYoutube.getValue() != null){
					itemProduto.getEntity().setUrl_video_youtube(txtUrlYoutube.getValue());
				}
								
				try{
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					String hora = sdf.format(new Date());
					String nomeImagem = item.getItemProperty("id").toString()+hora+"."+file.getName().split("\\.")[1];
					
					
					FotosProdutos fp = new FotosProdutos();
					fp.setEnderecoFoto(nomeImagem);
					fp.setProduto_id(itemProduto.getEntity().getId());
					fp.setData_cadastro(new Date());				
					FotoProdutoDAO.adicionarFoto(fp);

					//ScpUtil.transferirFtp("digitalonline", "ftp.digitalonline.com.br", "idigital123mc", file.getAbsolutePath(), "/www/fotos_produtos/"+nomeImagem);
					
					FtpUtil ftpUploader = new FtpUtil("ftp.digitalonline.com.br", "digitalonline", "idigital123mc");					
					ftpUploader.uploadFile(file.getAbsolutePath(), nomeImagem,"/www/loja/fotos_produtos/");
					ftpUploader.disconnect();
			
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				ProdutoDAO.saveProduto(itemProduto.getEntity());				
				Notify.Show("Produto Sicronizado com Sucesso!", Notify.TYPE_SUCCESS);
				
			}
		});
		
		
		vlRootLojaIntegrada.addComponent(btSalvarLoja);
		
		
		vlRoot.setStyleName("border-form");
		
		if(item.getItemProperty("gTin").getValue() != null){
			codBarras = item.getItemProperty("gTin").getValue().toString();
			valid_cod_barras = true;
		}
		
		if(item.getItemProperty("id").getValue() != null){
			icms = IcmsDAO.findByProduto((Integer)item.getItemProperty("id").getValue());
			ipi = IpiDAO.findByProduto((Integer)item.getItemProperty("id").getValue());
			pis = PisDAO.findByProduto((Integer)item.getItemProperty("id").getValue());
			cofins = CofinsDAO.findByProduto((Integer)item.getItemProperty("id").getValue());
		}
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setHeight("620px");
				setMargin(true);

				addComponent(vlRoot);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.addComponent(buildBtCancelar());
				if(editar){
					hlButtons.addComponent(buildBtSalvar("Editar"));		
				}else{
					hlButtons.addComponent(buildBtSalvar("Salvar"));						
				}
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}

		});
		
		if(codigoBarras){
			buildLayoutCodigoBarras();
		}else{
			buildLayout();
			buildLayoutImpostos();
		}

	}
	
	public String getNextId() {
		ProdutoDAO pDao = new ProdutoDAO();
		return pDao.getNextID();
	}
	
	public void buildLayoutCodigoBarras(){
		
		fieldGroup = new FieldGroup(item);

		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					final TextField txtCodBarras = new TextField("Cod Barras");				
					txtCodBarras.setStyleName("caption-align-produto");				
					txtCodBarras.setRequired(true);
					txtCodBarras.setNullRepresentation("");
					txtCodBarras.setWidth("300px");
										
					txtCodBarras.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
								boolean existe = ProdutoDAO.checkExistCodBarras(event.getText());
								
								if(existe){
									txtCodBarras.addStyleName("invalid-txt");
									valid_cod_barras = false;
								}else{
									txtCodBarras.removeStyleName("invalid-txt");
									valid_cod_barras = true;
								}
								
						}
					});
					
					addComponent(txtCodBarras);								
					fieldGroup.bind(txtCodBarras,"gTin");
				}
			});
	}
	
	private TextField txtCodNcm;
	private Button btSearchNcm;
	
	private TextField txtCodCest;
	private Button btSearchCest;

//	private TextField txtCodCofins;
//	private TextField txtDescricaoCofins;
//	private Cofins cofinsSelecionado;
//	private Button btSearchCofins;
	
//	private TextField txtCodCofinsSt;
//	private TextField txtDescricaoCofinsSt;
//	private Cofinsst cofinsStSelecionado;
//	private Button btSearchCofinsSt;

//	private TextField txtCodIcms;
//	private TextField txtDescricaoIcms;
//	private Icms icmsSelecionado;
//	private Button btSearchIcms;

//	private TextField txtCodII;
//	private TextField txtDescricaoII;
//	private Ii iiSelecionado;
//	private Button btSearchII;
	
//	private TextField txtCodIpi;
//	private TextField txtDescricaoIpi;
//	private Ipi ipiSelecionado;
//	private Button btSearchIpi;
	
//	private TextField txtCodIssqn;
//	private TextField txtDescricaoIssqn;
//	private Issqn issqnSelecionado;
//	private Button btSearchIssqn;
	
//	private TextField txtCodPis;
//	private TextField txtDescricaoPis;
//	private Pis pisSelecionado;
//	private Button btSearchPis;
	
//	private TextField txtCodPisSt;
//	private TextField txtDescricaoPisSt;
//	private Pisst pisStSelecionado;
//	private Button btSearchPisSt;
	
	public void buildLayoutImpostos(){

		TabSheet tab = new TabSheet();
		tab.setWidth("100%");
		tab.setHeight("291px");
	    tab.setStyleName("custom-form_tabsheet");
	    tab.addStyleName("vl-tributacao");
	    
	    VerticalLayout vlIcms = new VerticalLayout(){
	    	{				    		
				addComponent(new HorizontalLayout(){
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

//					tfCodOrigem.setRequired(true);		
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
									public void onSelected(CstOrigemUtil.CstEvent event) {
										
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

				HorizontalLayout hl6 = new HorizontalLayout();
				hl6.setWidth("100%");

				hl6.addComponent(new HorizontalLayout(){
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
						
//						tfCodTributo.setRequired(true);		
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
			
				HorizontalLayout hl7 = new HorizontalLayout();
				hl7.setWidth("100%");
				
				hl7.addComponent(new HorizontalLayout(){
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
			
				HorizontalLayout hl8 = new HorizontalLayout();
				hl8.setWidth("100%");
				
				hl8.addComponent(new HorizontalLayout(){
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
						
//						tfCodECF.setRequired(true);		
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
					
				HorizontalLayout hl9 = new HorizontalLayout();
				hl9.addComponent(hl6);
				hl9.addComponent(hl7);
				hl9.addComponent(hl8);
				hl9.addComponent(new FormLayout(){					
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
								
						txtAliquotaIcms = new TextField ("Aliquota ICMS (%)");				
						txtAliquotaIcms.setWidth("84px");				
						txtAliquotaIcms.setStyleName("caption-align-produto-aliquota-icms");
						txtAliquotaIcms.setMaxLength(200);
						txtAliquotaIcms.setId("txtIcms");
						txtAliquotaIcms.addStyleName("align-currency");
						txtAliquotaIcms.setRequired(true); 
						txtAliquotaIcms.setTabIndex(18);
						
						if(item.getItemProperty("taxaIcms").getValue() != null){
							txtAliquotaIcms.setValue(Real.formatDbToString(item.getItemProperty("taxaIcms").getValue().toString()));
						}else{							
							txtAliquotaIcms.setValue(Real.formatDbToString(String.valueOf(OpusERP4UI.getEmpresa().getAliquota_interna())));
						}
						
						
						addComponent(txtAliquotaIcms);				
						
						
						//fieldGroup.bind(txtIcms, "taxaIcms");

					}
				});
				
				addComponent(hl9);	
				
				
				addComponent(new HorizontalLayout(){
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

//						tfCodOrigem.setRequired(true);		
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
				
				HorizontalLayout hl1 = new HorizontalLayout();
				hl1.addStyleName("hl-slot-hl1-icms-tributacao");
				
				VerticalLayout vl1 = new VerticalLayout();
				vl1.addStyleName("hl-slot-vl1-icms-tributacao");
				vl1.addComponent(new FormLayout() {
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						cbModBcIcms = new ComboBox("Modalidade BC ICMS");
						cbModBcIcms.setTextInputAllowed(false);
						cbModBcIcms.setNullSelectionAllowed(true);					
						cbModBcIcms.setWidth("180px");				
						cbModBcIcms.setStyleName("caption-align-icms");
						cbModBcIcms.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(cbModBcIcms.getValue() != null){
									
									if(txtBaseCalculoICMS != null){
										txtBaseCalculoICMS.setReadOnly(false);
										txtBaseCalculoICMS.setRequired(true);
										JavaScript.getCurrent().execute("$('#txtBaseCalculoICMS').maskMoney({decimal:',',thousands:'.'})");
									}
									
									if(txtAliquotaICMS != null){
										txtAliquotaICMS.setReadOnly(false);
										txtAliquotaICMS.setRequired(true);
										JavaScript.getCurrent().execute("$('#txtAliquotaICMS').maskMoney({decimal:',',thousands:'.'})");
									}
								
									
								}else{
									
									txtBaseCalculoICMS.setReadOnly(false);
									txtAliquotaICMS.setReadOnly(false);
									txtBaseCalculoICMS.setValue(null);									
									txtAliquotaICMS.setValue(null);
									txtBaseCalculoICMS.setReadOnly(true);
									txtAliquotaICMS.setReadOnly(true);
									
								
								}
							
							}
						});
						
						cbModBcIcms.addItem("0 - Margem Valor Agregado");						
						cbModBcIcms.addItem("1 - Preço Tabelado Máximo");
						cbModBcIcms.addItem("2 - Valor da Operação");
						
						if(icms != null){
							cbModBcIcms.select(icms.getMod_bc_icms());
						}
						
						addComponent(cbModBcIcms);
						//fieldGroup.bind(cbModCalculo, "modalidade_base_calculo_icms");
						
					}
				});
				vl1.addComponent(new FormLayout() {
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtBaseCalculoICMS = new TextField("Base Calc. ICMS (R$)");				
						txtBaseCalculoICMS.setWidth("130px");				
						txtBaseCalculoICMS.setStyleName("caption-align-icms");
						txtBaseCalculoICMS.addStyleName("align-currency");
						txtBaseCalculoICMS.setNullRepresentation("");
						txtBaseCalculoICMS.setMaxLength(200);
						txtBaseCalculoICMS.setId("txtBaseCalculoICMS");
						
						
						addComponent(txtBaseCalculoICMS);
						
						
						if(icms != null){
							txtBaseCalculoICMS.setValue(Real.formatDbToString(String.valueOf(icms.getBase_calc_icms())));							
							txtBaseCalculoICMS.setRequired(true);
						}else{
							txtBaseCalculoICMS.setReadOnly(true);							
						}
						
						JavaScript.getCurrent().execute("$('#txtBaseCalculoICMS').maskMoney({decimal:',',thousands:'.'})");

					}
				});
				vl1.addComponent(new FormLayout() {
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtAliquotaICMS = new TextField("Alíquota ICMS (%)");				
						txtAliquotaICMS.setWidth("130px");				
						txtAliquotaICMS.setStyleName("caption-align-icms");
						txtAliquotaICMS.addStyleName("align-currency");
						txtAliquotaICMS.setNullRepresentation("");
						txtAliquotaICMS.setMaxLength(200);						
						txtAliquotaICMS.setId("txtAliquotaICMS");
						
						if(icms != null){
							txtAliquotaICMS.setValue(Real.formatDbToString(String.valueOf(icms.getAliquota_icms())));
							txtAliquotaICMS.setRequired(true);
						}else{
							txtAliquotaICMS.setReadOnly(true);
						}

						addComponent(txtAliquotaICMS);
						
					}
				});
				
				VerticalLayout vl2 = new VerticalLayout();
				vl2.addStyleName("hl-slot-vl2-icms-st-tributacao");
				vl2.addComponent(new FormLayout() {
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						cbModBcIcmsSt = new ComboBox("Modalidade BC ICMS ST");
						cbModBcIcmsSt.setTextInputAllowed(false);
						cbModBcIcmsSt.setNullSelectionAllowed(true);						
						cbModBcIcmsSt.setWidth("210px");				
						cbModBcIcmsSt.setStyleName("caption-align-icms");
						
						cbModBcIcmsSt.addItem("0 - Margem Valor Agregado");
						cbModBcIcmsSt.addItem("1 - Pauta");						
						cbModBcIcmsSt.addItem("2 - Preço Tabelado Máximo");
						cbModBcIcmsSt.addItem("3 - Valor da Operação");
						
						if(icms != null){
							cbModBcIcmsSt.select(icms.getMod_bc_icms_st());
						}
						
						cbModBcIcmsSt.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								if(cbModBcIcmsSt.getValue() != null){
									
									if(txtMVAIcmsST != null){
										txtMVAIcmsST.setReadOnly(false);
										txtMVAIcmsST.setRequired(true);
										JavaScript.getCurrent().execute("$('#txtMVAIcmsST').maskMoney({decimal:',',thousands:'.'})");
									}
									
									if(txtBaseCalculoICMSST != null){
										txtBaseCalculoICMSST.setReadOnly(false);
										txtBaseCalculoICMSST.setRequired(true);
										JavaScript.getCurrent().execute("$('#txtBaseCalculoICMSST').maskMoney({decimal:',',thousands:'.'})");
									}
									
									if(txtAliquotaICMSST != null){
										txtAliquotaICMSST.setReadOnly(false);
										txtAliquotaICMSST.setRequired(true);
										JavaScript.getCurrent().execute("$('#txtAliquotaICMSST').maskMoney({decimal:',',thousands:'.'})");
									}
								
								}else{
									
									txtMVAIcmsST.setReadOnly(false);
									txtBaseCalculoICMSST.setReadOnly(false);
									txtAliquotaICMSST.setReadOnly(false);
									
									
									txtMVAIcmsST.setValue(null);
									txtBaseCalculoICMSST.setValue(null);
									txtAliquotaICMSST.setValue(null);
									
									txtMVAIcmsST.setReadOnly(true);
									txtBaseCalculoICMSST.setReadOnly(true);
									txtAliquotaICMSST.setReadOnly(true);
								}
							}
						});
						
						addComponent(cbModBcIcmsSt);
						//fieldGroup.bind(cbModCalculo, "modalidade_base_calculo_icms");
						
					}
				});
				vl2.addComponent(new FormLayout() {
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtMVAIcmsST = new TextField("MVA (%)");				
						txtMVAIcmsST.setWidth("130px");				
						txtMVAIcmsST.setStyleName("caption-align-icms");
						txtMVAIcmsST.addStyleName("align-currency");
						txtMVAIcmsST.setNullRepresentation("");
						txtMVAIcmsST.setMaxLength(200);
						
						txtMVAIcmsST.setId("txtMVAIcmsST");
						
						addComponent(txtMVAIcmsST);
						
						if(icms != null){
							txtMVAIcmsST.setValue(Real.formatDbToString(String.valueOf(icms.getMva())));							
							JavaScript.getCurrent().execute("$('#txtMVAIcmsST').maskMoney({decimal:',',thousands:'.'})");
							txtMVAIcmsST.setRequired(true);
						}else{
							txtMVAIcmsST.setReadOnly(true);							
						}
						
						
						
					}
				});
				vl2.addComponent(new FormLayout() {
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtBaseCalculoICMSST = new TextField("Base Calc. ICMS ST(R$)");				
						txtBaseCalculoICMSST.setWidth("130px");				
						txtBaseCalculoICMSST.setStyleName("caption-align-icms");
						txtBaseCalculoICMSST.addStyleName("align-currency");
						txtBaseCalculoICMSST.setNullRepresentation("");
						txtBaseCalculoICMSST.setMaxLength(200);
						
						txtBaseCalculoICMSST.setId("txtBaseCalculoICMSST");
						
						if(icms != null){
							txtBaseCalculoICMSST.setValue(Real.formatDbToString(String.valueOf(icms.getBase_calc_icms_st())));						
							txtBaseCalculoICMSST.setRequired(true);
						}else{
							txtBaseCalculoICMSST.setReadOnly(true);							
						}
						
						addComponent(txtBaseCalculoICMSST);
										
					}
				});
				vl2.addComponent(new FormLayout() {
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						txtAliquotaICMSST = new TextField("Alíquota ICMS ST(%)");				
						txtAliquotaICMSST.setWidth("130px");				
						txtAliquotaICMSST.setStyleName("caption-align-icms");
						txtAliquotaICMSST.addStyleName("align-currency");
						txtAliquotaICMSST.setNullRepresentation("");
						txtAliquotaICMSST.setMaxLength(200);
						
						txtAliquotaICMSST.setId("txtAliquotaICMSST");
												
						addComponent(txtAliquotaICMSST);
						
						if(icms != null){
							txtAliquotaICMSST.setValue(Real.formatDbToString(String.valueOf(icms.getAliquota_icms_st())));							

							txtAliquotaICMSST.setRequired(true);
						}else{
							txtAliquotaICMSST.setReadOnly(true);							
						}
						
						
					}
				});

				
				
				hl1.addComponent(vl1);
				hl1.addComponent(vl2);
				
				
				addComponent(hl1); 

	    	}
	    };
	    vlIcms.setMargin(false);
	    vlIcms.setSpacing(false);
	    
	    tab.addTab(vlIcms, "ICMS");
	    	    
	    final HorizontalLayout hlIpi = new HorizontalLayout();
	    hlIpi.addStyleName("vl-principal");
	    hlIpi.setSizeFull();
	    
	    VerticalLayout vl1 = new VerticalLayout(){
	    	{	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					JPAContainer<CstIpi> containerCstIpi = JPAContainerFactory.makeReadOnly(CstIpi.class, ConnUtil.getEntity());
	    					cbCstIpi = new  ComboBox("CST IPI", containerCstIpi){
	    						@Override
	    						public String getItemCaption(Object itemId) {
	    						   Item item = getItem(itemId);
	    						
	    						   if (item == null) {
	    						      return "";
	    						   }
	    						     
	    						   return String.valueOf(item.getItemProperty("codigo").getValue()) + " -  " + 
	    						   		  String.valueOf(item.getItemProperty("descricao").getValue());
	    						}
	    					};
	    					cbCstIpi.setTextInputAllowed(false);
	    					cbCstIpi.setStyleName("caption-align-ipi-tab");
	    					//cbCstIpi.setItemCaptionPropertyId("descricao");
	    					
	    					if(ipi != null){	    						
	    						for (Object itemId: containerCstIpi.getItemIds()) {
									EntityItem<CstIpi> entityItemCstIpi = containerCstIpi.getItem(itemId);
									if(entityItemCstIpi.getEntity().equals(ipi.getCst_ipi())){
										cbCstIpi.select(itemId); 
										break;
									}
								}
	    					}
	    					
	    	    			addComponent(cbCstIpi);
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					
	    					txtCnpjProdutor = new TextField("CNPJ do Produtor");
	    					txtCnpjProdutor.setStyleName("caption-align-ipi-tab");
	    					txtCnpjProdutor.setNullRepresentation("");
	    					txtCnpjProdutor.setId("txtCnpjProdutor");
	    					
	    					if(ipi != null){
	    						txtCnpjProdutor.setValue(ipi.getCnpj_produtor());
	    					}
	    					
	    	    			addComponent(txtCnpjProdutor);
	    	    			
	    	    			JavaScript.getCurrent().execute("$('#txtCnpjProdutor').mask('00.000.000/0000-00')");
	    				}
	    			}); 
	    			
	    	}
	    };
	        
	    VerticalLayout vl2 = new VerticalLayout(){
	    	{
	    			addComponent(new FormLayout(){
	    				{
	    					
	    					setStyleName("form-cutom");
	    					
	    					cbTipoCalculo = new ComboBox("Tipo de Cálculo");
	    					cbTipoCalculo.setStyleName("caption-align-icms");    					
	    					cbTipoCalculo.setTextInputAllowed(false);	    					
	    					cbTipoCalculo.addItem("Percentual");
	    					cbTipoCalculo.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									if(cbTipoCalculo.getValue() != null){
										if(txtValorBaseCalculoIpi != null){
											txtValorBaseCalculoIpi.setReadOnly(false);
											JavaScript.getCurrent().execute("$('#txtValorBaseCalculoIpi').maskMoney({decimal:',',thousands:'.'})");														
										}
										
										if(txtAliquotaIpi != null){
											txtAliquotaIpi.setReadOnly(false);
											JavaScript.getCurrent().execute("$('#txtAliquotaIpi').maskMoney({decimal:',',thousands:'.'})");											
										}
										
										if(txtValorIpi != null){
											txtValorIpi.setReadOnly(false);
											JavaScript.getCurrent().execute("$('#txtValorIpi').maskMoney({decimal:',',thousands:'.'})");
										}
									}else{
										if(txtValorBaseCalculoIpi != null){
											txtValorBaseCalculoIpi.setValue(null);
											txtValorBaseCalculoIpi.setReadOnly(true);
										}
										
										if(txtAliquotaIpi != null){
											txtAliquotaIpi.setValue(null);
											txtAliquotaIpi.setReadOnly(true);
										}
										
										if(txtValorIpi != null){
											txtValorIpi.setValue(null);
											txtValorIpi.setReadOnly(true);
										}
									}
								}
							});
	    					
	    					
	    					if(ipi != null){
	    						cbTipoCalculo.select(ipi.getTipo_calculo());
	    					}
	    					
	    					
	    					addComponent(cbTipoCalculo);
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					
	    					setStyleName("form-cutom");
	    					txtValorBaseCalculoIpi = new TextField("Valor Base de Cálculo (%)");
	    					txtValorBaseCalculoIpi.setNullRepresentation("");
	    					txtValorBaseCalculoIpi.setStyleName("caption-align-icms");
	    					txtValorBaseCalculoIpi.addStyleName("align-currency");
	    					txtValorBaseCalculoIpi.setReadOnly(true);
	    					txtValorBaseCalculoIpi.setId("txtValorBaseCalculoIpi"); 
	    					
	    					if(ipi != null){
	    						txtValorBaseCalculoIpi.setReadOnly(false);
	    						txtValorBaseCalculoIpi.setValue(Real.formatDbToString(String.valueOf(ipi.getValor_base_calculo())));
	    					}
	    					
	    					addComponent(txtValorBaseCalculoIpi);
	    					
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtAliquotaIpi = new TextField("Aliquota (%)");
	    					txtAliquotaIpi.setNullRepresentation("");
	    					txtAliquotaIpi.setReadOnly(true);
	    					txtAliquotaIpi.setStyleName("caption-align-icms");
	    					txtAliquotaIpi.addStyleName("align-currency");
	    					txtAliquotaIpi.setId("txtAliquotaIpi");
	    					
	    					if(ipi != null){
	    						txtAliquotaIpi.setReadOnly(false);
	    						txtAliquotaIpi.setValue(Real.formatDbToString(String.valueOf(ipi.getAliquota())));
	    					}
	    					
	    					addComponent(txtAliquotaIpi);
	    					
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtValorIpi = new TextField("Valor do IPI (R$)");
	    					txtValorIpi.setNullRepresentation("");
	    					txtValorIpi.setStyleName("caption-align-icms");
	    					txtValorIpi.addStyleName("align-currency");
	    					txtValorIpi.setReadOnly(true);
	    					txtValorIpi.setId("txtValorIpi");
	    					
	    					if(ipi != null){
	    						txtValorIpi.setReadOnly(false);
	    						txtValorIpi.setValue(Real.formatDbToString(String.valueOf(ipi.getValor_ipi())));
	    					}
	    					
	    					addComponent(txtValorIpi);
	    					
	    				}
	    			});
	    	}
	    };
	    hlIpi.addComponent(vl1);
	    hlIpi.addComponent(vl2);
	    
	    
	    
	    
	    
	    tab.addTab(new VerticalLayout(){
	    	{
	    		addComponent(hlIpi); 
	    	}
	    }, "IPI");
	    
	    
	    final HorizontalLayout hlPis = new HorizontalLayout();
	    hlPis.addStyleName("vl-principal");
	    hlPis.setSizeFull();
	   
	    VerticalLayout hlPis_v1 = new VerticalLayout(){
	    	{	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					JPAContainer<CstPis> containerCstPis = JPAContainerFactory.makeReadOnly(CstPis.class, ConnUtil.getEntity());
	    					
	    					cbCstPis = new  ComboBox("CST PIS", containerCstPis){
	    						@Override
	    						public String getItemCaption(Object itemId) {
	    						   Item item = getItem(itemId);
	    						
	    						   if (item == null) {
	    						      return "";
	    						   }
	    						     
	    						   return String.valueOf(item.getItemProperty("id").getValue()) + " -  " + 
	    						   		  String.valueOf(item.getItemProperty("descricao").getValue());
	    						}
	    					};	    					
	    					cbCstPis.setTextInputAllowed(false);
	    					cbCstPis.setStyleName("caption-align-pis-tab");
	    					cbCstPis.setNullSelectionAllowed(true);
	    					cbCstPis.setItemCaptionPropertyId("descricao");
	    					cbCstPis.setWidth("260px");
	    					
	    					if(pis != null && pis.getCst_pis() != null){	    						
	    						for (Object itemId: containerCstPis.getItemIds()) {
									EntityItem<CstPis> entityItemCstIpi = containerCstPis.getItem(itemId);
									if(entityItemCstIpi.getEntity().equals(pis.getCst_pis())){
										cbCstPis.select(itemId); 
										break;
									}
								}
	    					}else{
	    						for (Object itemId: containerCstPis.getItemIds()) {
									EntityItem<CstPis> entityItemCstIpi = containerCstPis.getItem(itemId);
									if(entityItemCstIpi.getEntity().getId().equals(99)){
										cbCstPis.select(itemId); 
										break;
									}
								}
	    					}
	    					
	    	    			addComponent(cbCstPis);
	    				}
	    			});    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					cbTipoCalculoPis = new  ComboBox("Tipo de Cálculo");
	    					cbTipoCalculoPis.setStyleName("caption-align-pis-tab");
	    					cbTipoCalculoPis.addItem("Valor");
	    					cbTipoCalculoPis.addItem("Percentual");
	    					cbTipoCalculoPis.setTextInputAllowed(false);
	    					cbTipoCalculoPis.setNullSelectionAllowed(true);
	    					cbTipoCalculoPis.setTextInputAllowed(false); 
	    					
	    					cbTipoCalculoPis.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
										if(cbTipoCalculoPis.getValue() != null){
											if(txtBaseCalculoPis != null){												
												txtBaseCalculoPis.setReadOnly(false); 
												JavaScript.getCurrent().execute("$('#txtBaseCalculoPis').maskMoney({decimal:',',thousands:'.'})");
											}
											
											if(txtAliquotaPis != null){
												txtAliquotaPis.setReadOnly(false);
												JavaScript.getCurrent().execute("$('#txtAliquotaPis').maskMoney({decimal:',',thousands:'.'})");
											}
											
											if(txtValorPis != null){
												txtValorPis.setReadOnly(false); 
												JavaScript.getCurrent().execute("$('#txtValorPis').maskMoney({decimal:',',thousands:'.'})");
											}
										}else{
											txtBaseCalculoPis.setValue(null);
											txtBaseCalculoPis.setReadOnly(true);
											txtAliquotaPis.setValue(null);
											txtAliquotaPis.setReadOnly(true);
											txtValorPis.setValue(null);
											txtValorPis.setReadOnly(true);
										}
								}
							});
	    					
	    					if(pis != null && pis.getTipo_calculo() != null){
	    						cbTipoCalculoPis.select(pis.getTipo_calculo());
	    					}else{
	    						cbTipoCalculoPis.select("Percentual");
	    					}
	    					
	    	    			addComponent(cbTipoCalculoPis);
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtBaseCalculoPis = new TextField("Base de Cálculo");
	    					txtBaseCalculoPis.setNullRepresentation("");
	    					txtBaseCalculoPis.setStyleName("caption-align-pis-tab");
	    					txtBaseCalculoPis.addStyleName("align-currency");
	    					txtBaseCalculoPis.setReadOnly(true);
	    					txtBaseCalculoPis.setId("txtBaseCalculoPis");
	    					
	    					if(pis != null){
	    						txtBaseCalculoPis.setReadOnly(false);
	    						txtBaseCalculoPis.setValue(Real.formatDbToString(String.valueOf(pis.getBase_calculo_pis())));	    						
	    					}
	    					
	    					addComponent(txtBaseCalculoPis);
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtAliquotaPis = new TextField("Alíquota (%)");
	    					txtAliquotaPis.setNullRepresentation("");
	    					txtAliquotaPis.setStyleName("caption-align-pis-tab");
	    					txtAliquotaPis.addStyleName("align-currency");
	    					txtAliquotaPis.setReadOnly(true);
	    					txtAliquotaPis.setId("txtAliquotaPis");
	    					
	    					if(pis != null){
	    						txtAliquotaPis.setReadOnly(false);
	    						txtAliquotaPis.setValue(Real.formatDbToString(String.valueOf(pis.getAliquota())));	    						
	    					}
	    					
	    					addComponent(txtAliquotaPis); 
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtValorPis = new TextField("Valor do PIS (R$)");
	    					txtValorPis.setNullRepresentation("");
	    					txtValorPis.setStyleName("caption-align-pis-tab");
	    					txtValorPis.addStyleName("align-currency");
	    					txtValorPis.setReadOnly(true);
	    					txtValorPis.setId("txtValorPis");
	    					
	    					if(pis != null){
	    						txtValorPis.setReadOnly(false);
	    						txtValorPis.setValue(Real.formatDbToString(String.valueOf(pis.getValor_pis())));	    						
	    					}
	    					
	    					addComponent(txtValorPis);
	    				}
	    			});
	    	}
	    };
	    hlPis.addComponent(hlPis_v1);
	    VerticalLayout hlPis_v2 = new VerticalLayout(){
	    	{	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					cbCstPisStTipoCalculo = new  ComboBox("PIS ST - Tipo de Cálculo");
	    					cbCstPisStTipoCalculo.setTextInputAllowed(false);
	    					cbCstPisStTipoCalculo.setNullSelectionAllowed(true);
	    					cbCstPisStTipoCalculo.setTextInputAllowed(false);
	    					cbCstPisStTipoCalculo.setStyleName("caption-align-icms");
	    					
	    					cbCstPisStTipoCalculo.addItem("Percentual");
	    					
	    					cbCstPisStTipoCalculo.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
									if(cbCstPisStTipoCalculo.getValue() != null){
										if(txtBaseCalculoPisSt != null){
											txtBaseCalculoPisSt.setReadOnly(false); 
											JavaScript.getCurrent().execute("$('#txtBaseCalculoPisSt').maskMoney({decimal:',',thousands:'.'})");
										}
										
										if(txtAliquotaPisSt != null){
											txtAliquotaPisSt.setReadOnly(false);
											JavaScript.getCurrent().execute("$('#txtAliquotaPisSt').maskMoney({decimal:',',thousands:'.'})");
										}
										
										if(txtValorPisSt != null){
											txtValorPisSt.setReadOnly(false);
											JavaScript.getCurrent().execute("$('#txtValorPisSt').maskMoney({decimal:',',thousands:'.'})");
										}
									}else{
										txtBaseCalculoPisSt.setValue(null);
										txtBaseCalculoPisSt.setReadOnly(true);
										txtAliquotaPisSt.setValue(null);
										txtAliquotaPisSt.setReadOnly(true);
										txtValorPisSt.setValue(null);
										txtValorPisSt.setReadOnly(true);
									}
								}
							});
	    					
	    					if(pis != null && pis.getTipo_calculo_pis_st() != null){
	    						cbCstPisStTipoCalculo.select(pis.getTipo_calculo_pis_st());	    						
	    					}
	    					
	    	    			addComponent(cbCstPisStTipoCalculo);
	    				}
	    			});  
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtBaseCalculoPisSt = new TextField("Base de Cálculo");
	    					txtBaseCalculoPisSt.setNullRepresentation("");
	    					txtBaseCalculoPisSt.setStyleName("caption-align-icms");
	    					txtBaseCalculoPisSt.addStyleName("align-currency");
	    					txtBaseCalculoPisSt.setReadOnly(true);
	    					txtBaseCalculoPisSt.setId("txtBaseCalculoPisSt");
	    					
	    					if(pis != null){
	    						txtBaseCalculoPisSt.setReadOnly(false);
	    						txtBaseCalculoPisSt.setValue(Real.formatDbToString(String.valueOf(pis.getBase_calculo_pis_st())));	    						
	    					}
	    					
	    					addComponent(txtBaseCalculoPisSt); 
	    					
	    					
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtAliquotaPisSt = new TextField("Aliquota (%)");
	    					txtAliquotaPisSt.setNullRepresentation("");
	    					txtAliquotaPisSt.setStyleName("caption-align-icms");
	    					txtAliquotaPisSt.addStyleName("align-currency");
	    					txtAliquotaPisSt.setReadOnly(true);
	    					txtAliquotaPisSt.setId("txtAliquotaPisSt");
	    					
	    					if(pis != null){
	    						txtAliquotaPisSt.setReadOnly(false);
	    						txtAliquotaPisSt.setValue(Real.formatDbToString(String.valueOf(pis.getAliquota_pis_st())));	    						
	    					}
	    					addComponent(txtAliquotaPisSt); 
	    				}
	    			});
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtValorPisSt = new TextField("Valor do PIS ST (R$)");
	    					txtValorPisSt.setNullRepresentation("");
	    					txtValorPisSt.setStyleName("caption-align-icms");
	    					txtValorPisSt.addStyleName("align-currency");	    					
	    					txtValorPisSt.setReadOnly(true);
	    					txtValorPisSt.setId("txtValorPisSt");
	    					
	    					if(pis != null){
	    						txtValorPisSt.setReadOnly(false);
	    						txtValorPisSt.setValue(Real.formatDbToString(String.valueOf(pis.getValor_pis_st())));	    						
	    					}
	    					
	    					addComponent(txtValorPisSt); 
	    				}
	    			});
	    			
	    	}
	    };
	    hlPis.addComponent(hlPis_v2);
	    
	    tab.addTab(new VerticalLayout(){
	    	{
	    		addComponent(hlPis);
	    	}
	    }, "PIS");
	    
	    
	    final HorizontalLayout hlCofins = new HorizontalLayout();
	    hlCofins.addStyleName("vl-principal");
	    hlCofins.setSizeFull();
	    
	    VerticalLayout hlCofins_v1 = new VerticalLayout(){
	    	{	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					JPAContainer<CstCofins> containerCofins = JPAContainerFactory.makeReadOnly(CstCofins.class, ConnUtil.getEntity());
	    					
	    					cbCstCofins = new  ComboBox("CST COFINS", containerCofins){
	    						@Override
	    						public String getItemCaption(Object itemId) {
	    						   Item item = getItem(itemId);
	    						
	    						   if (item == null) {
	    						      return "";
	    						   }
	    						     
	    						   return String.valueOf(item.getItemProperty("id").getValue()) + " -  " + 
	    						   		  String.valueOf(item.getItemProperty("descricao").getValue());
	    						}
	    					};   	
	    					cbCstCofins.setTextInputAllowed(false);
	    					cbCstCofins.setWidth("290px");
	    					cbCstCofins.setStyleName("caption-align-cofins-tab");
	    					cbCstCofins.setNullSelectionAllowed(true);
	    					cbCstCofins.setTextInputAllowed(false);
	    					cbCstCofins.setItemCaptionPropertyId("descricao");
	    					
	    					if(cofins != null && cofins.getCst_cofins() != null){	    						
	    						for (Object itemId: containerCofins.getItemIds()) {
									EntityItem<CstCofins> entityItemCstIpi = containerCofins.getItem(itemId);
									if(entityItemCstIpi.getEntity().equals(cofins.getCst_cofins())){
										cbCstCofins.select(itemId); 
										break;
									}
								}
	    					}else{
	    						for (Object itemId: containerCofins.getItemIds()) {
									EntityItem<CstCofins> entityItemCstIpi = containerCofins.getItem(itemId);
									if(entityItemCstIpi.getEntity().getId().equals(99)){
										cbCstCofins.select(itemId); 
										break;
									}
								}
	    					}
	    						    					
	    	    			addComponent(cbCstCofins);
	    				}
	    			});    
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					cbTipoCalculoCofins = new  ComboBox("Tipo de Cálculo");	    					
	    					cbTipoCalculoCofins.setStyleName("caption-align-cofins-tab");
	    					cbTipoCalculoCofins.setTextInputAllowed(false);
	    					cbTipoCalculoCofins.setNullSelectionAllowed(true);
	    					cbTipoCalculoCofins.setTextInputAllowed(false);
	    					cbTipoCalculoCofins.addItem("Percentual");
	    					cbTipoCalculoCofins.addItem("Valor");
	    					
	    					cbTipoCalculoCofins.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
										if(cbTipoCalculoCofins.getValue() != null){
											if(txtBaseCalculoCofins != null){												
												txtBaseCalculoCofins.setReadOnly(false); 
												JavaScript.getCurrent().execute("$('#txtBaseCalculoCofins').maskMoney({decimal:',',thousands:'.'})");												
											}
											
											if(txtAliquotaCofins != null){
												txtAliquotaCofins.setReadOnly(false);
												JavaScript.getCurrent().execute("$('#txtAliquotaCofins').maskMoney({decimal:',',thousands:'.'})");												
											}
											
											if(txtValorCofins != null){
												txtValorCofins.setReadOnly(false); 
												JavaScript.getCurrent().execute("$('#txtValorCofins').maskMoney({decimal:',',thousands:'.'})");
												
											}
										}else{
											txtBaseCalculoCofins.setValue(null);
											txtBaseCalculoCofins.setReadOnly(true);
											txtAliquotaCofins.setValue(null);
											txtAliquotaCofins.setReadOnly(true);
											txtValorCofins.setValue(null);
											txtValorCofins.setReadOnly(true);
										}
								}
							});
	    					
	    					if(cofins != null && cofins.getTipo_calculo() != null){
	    						cbTipoCalculoCofins.select(cofins.getTipo_calculo());
	    					}else{
	    						cbTipoCalculoCofins.select("Percentual");
	    					}
	    						    						    					
	    	    			addComponent(cbTipoCalculoCofins);	    	    			
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtBaseCalculoCofins = new TextField("Base de Cálculo");	    		
	    					txtBaseCalculoCofins.setNullRepresentation("");
	    					txtBaseCalculoCofins.setStyleName("caption-align-cofins-tab");
	    					txtBaseCalculoCofins.addStyleName("align-currency");
	    					txtBaseCalculoCofins.setReadOnly(true); 
	    					txtBaseCalculoCofins.setId("txtBaseCalculoCofins");
	    					
	    					if(cofins != null){
	    						txtBaseCalculoCofins.setReadOnly(false); 
	    						txtBaseCalculoCofins.setValue(Real.formatDbToString(String.valueOf(cofins.getBase_calculo_cofins())));
	    					}
	    							
	    	    			addComponent(txtBaseCalculoCofins);	    	    			
	    				}
	    			});    
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtAliquotaCofins = new TextField("Aliquota (%)");	    
	    					txtAliquotaCofins.setNullRepresentation("");
	    					txtAliquotaCofins.setStyleName("caption-align-cofins-tab");
	    					txtAliquotaCofins.addStyleName("align-currency");
	    					txtAliquotaCofins.setReadOnly(true); 
	    					txtAliquotaCofins.setId("txtAliquotaCofins");
	    					
	    					if(cofins != null){
	    						txtAliquotaCofins.setReadOnly(false); 
	    						txtAliquotaCofins.setValue(Real.formatDbToString(String.valueOf(cofins.getAliquota_cofins())));
	    					}
	    							
	    	    			addComponent(txtAliquotaCofins);	    	    			
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtValorCofins = new TextField("Valor do COFINS (R$)");	  
	    					txtValorCofins.setNullRepresentation("");
	    					txtValorCofins.setStyleName("caption-align-cofins-tab");
	    					txtValorCofins.addStyleName("align-currency");
	    					txtValorCofins.setReadOnly(true); 
	    					txtValorCofins.setId("txtValorCofins");
	    					
	    					if(cofins != null){
	    						txtValorCofins.setReadOnly(false); 
	    						txtValorCofins.setValue(Real.formatDbToString(String.valueOf(cofins.getValor_cofins())));
	    					}
	    							
	    	    			addComponent(txtValorCofins);	    	    			
	    				}
	    			});
	    			
	    			
	    	}
	    };
	    hlCofins.addComponent(hlCofins_v1);	    
	    VerticalLayout hlCofins_v2 = new VerticalLayout(){
	    	{	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					cbTipoCalculoCofinsSt = new  ComboBox("Tipo de Cálculo ST");	    					
	    					cbTipoCalculoCofinsSt.setStyleName("caption-align-icms");
	    					cbTipoCalculoCofinsSt.setNullSelectionAllowed(true);
	    					cbTipoCalculoCofinsSt.setTextInputAllowed(false);
	    					cbTipoCalculoCofinsSt.addItem("Percentual");
	    					cbTipoCalculoCofinsSt.addItem("Valor");
	    					
	    					cbTipoCalculoCofinsSt.addValueChangeListener(new Property.ValueChangeListener() {
								
								@Override
								public void valueChange(ValueChangeEvent event) {
										if(cbTipoCalculoCofinsSt.getValue() != null){
											if(txtBaseCalculoCofinsSt != null){												
												txtBaseCalculoCofinsSt.setReadOnly(false); 
												JavaScript.getCurrent().execute("$('#txtBaseCalculoCofinsSt').maskMoney({decimal:',',thousands:'.'})");												
											}
											
											if(txtAliquotaCofinsSt != null){
												txtAliquotaCofinsSt.setReadOnly(false);
												JavaScript.getCurrent().execute("$('#txtAliquotaCofinsSt').maskMoney({decimal:',',thousands:'.'})");												
											}
											
											if(txtValorCofinsSt != null){
												txtValorCofinsSt.setReadOnly(false); 
												JavaScript.getCurrent().execute("$('#txtValorCofinsSt').maskMoney({decimal:',',thousands:'.'})");
											}
										}else{
											if(txtBaseCalculoCofinsSt != null){
												txtBaseCalculoCofinsSt.setReadOnly(false);											
												txtBaseCalculoCofinsSt.setValue(null);
												txtBaseCalculoCofinsSt.setReadOnly(true);
											}
											
											if(txtAliquotaCofinsSt != null){
												txtAliquotaCofinsSt.setReadOnly(false);
												txtAliquotaCofinsSt.setValue(null);
												txtAliquotaCofinsSt.setReadOnly(true);
											}
											
											if(txtValorCofinsSt != null){
												txtValorCofinsSt.setReadOnly(false);
												txtValorCofinsSt.setValue(null);
												txtValorCofinsSt.setReadOnly(true);
											}
										}
								}
							});
	    					
	    					if(cofins != null && cofins.getTipo_calculo_st() != null){
	    						cbTipoCalculoCofinsSt.select(cofins.getTipo_calculo_st());
	    					}
	    					
	    						    					
	    	    			addComponent(cbTipoCalculoCofinsSt);
	    				}
	    			});    
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtBaseCalculoCofinsSt = new TextField("Base de Cálculo");	 
	    					txtBaseCalculoCofinsSt.setNullRepresentation("");
	    					txtBaseCalculoCofinsSt.setStyleName("caption-align-icms");
	    					txtBaseCalculoCofinsSt.addStyleName("align-currency");
	    					txtBaseCalculoCofinsSt.setReadOnly(true); 
	    					txtBaseCalculoCofinsSt.setId("txtBaseCalculoCofinsSt");
	    					
	    					if(cofins != null){
	    						txtBaseCalculoCofinsSt.setReadOnly(false); 
	    						txtBaseCalculoCofinsSt.setValue(Real.formatDbToString(String.valueOf(cofins.getBase_calculo_cst())));
	    					}
	    							
	    	    			addComponent(txtBaseCalculoCofinsSt);	    	    			
	    				}
	    			});    
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtAliquotaCofinsSt = new TextField("Aliquota (%)");	    	
	    					txtAliquotaCofinsSt.setNullRepresentation("");
	    					txtAliquotaCofinsSt.setStyleName("caption-align-icms");
	    					txtAliquotaCofinsSt.addStyleName("align-currency");
	    					txtAliquotaCofinsSt.setReadOnly(true);
	    					txtAliquotaCofinsSt.setId("txtAliquotaCofinsSt");
	    					
	    					if(cofins != null){
	    						txtAliquotaCofinsSt.setReadOnly(false);
	    						txtAliquotaCofinsSt.setValue(Real.formatDbToString(String.valueOf(cofins.getAliquota_cofins_st())));
	    					}
	    							
	    	    			addComponent(txtAliquotaCofinsSt);	    	    			
	    				}
	    			});
	    			
	    			addComponent(new FormLayout(){
	    				{
	    					setStyleName("form-cutom");
	    					
	    					txtValorCofinsSt = new TextField("Valor do PIS ST (R$)");	    		
	    					txtValorCofinsSt.setNullRepresentation("");
	    					txtValorCofinsSt.setStyleName("caption-align-icms");
	    					txtValorCofinsSt.addStyleName("align-currency");
	    					txtValorCofinsSt.setReadOnly(true); 
	    					txtValorCofinsSt.setId("txtValorCofinsSt");
	    					
	    					if(cofins != null){
	    						txtValorCofinsSt.setReadOnly(false); 
	    						txtValorCofinsSt.setValue(Real.formatDbToString(String.valueOf(cofins.getAliquota_cofins_st())));
	    					}
	    							
	    	    			addComponent(txtValorCofinsSt);	    	    			
	    				}
	    			});
	    			
	    			
	    	}
	    };
	    hlCofins.addComponent(hlCofins_v2);
	    
	    tab.addTab(new VerticalLayout(){
	    	{
	    		addComponent(hlCofins); 
	    	}
	    }, "COFINS");
	    
		vlRootImpostos.addComponent(tab);
		vlRootImpostos.setMargin(false);
		vlRootImpostos.setSpacing(false);
	
		tab.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				
				if(event.getTabSheet().getTab(event.getTabSheet().getSelectedTab()).getCaption().toString().equals("ICMS")){
					
					if(editar){
						cbModBcIcms.setReadOnly(true);
						txtBaseCalculoICMS.setReadOnly(true);
						txtAliquotaICMS.setReadOnly(true);
						cbModBcIcmsSt.setReadOnly(true);
						txtMVAIcmsST.setReadOnly(true);
						txtBaseCalculoICMSST.setReadOnly(true);
						txtAliquotaICMSST.setReadOnly(true);
						
						tfCodCfop.setReadOnly(true);
						tfCodOrigem.setReadOnly(true);
						tfCodCst.setReadOnly(true);
						tfCodCsosn.setReadOnly(true);
						tfCodCstEcf.setReadOnly(true);
						
						btSearchOrigem.setEnabled(false); 
						btSearchCst.setEnabled(false);
						btSearchCSOSN.setEnabled(false);
						btSearchECF.setEnabled(false);
						btSearchCfop.setEnabled(false);
						
					}else{
						cbModBcIcms.setReadOnly(false);
						txtBaseCalculoICMS.setReadOnly(false);
						txtAliquotaICMS.setReadOnly(false);
						cbModBcIcmsSt.setReadOnly(false);
						txtMVAIcmsST.setReadOnly(false);
						txtBaseCalculoICMSST.setReadOnly(false);
						txtAliquotaICMSST.setReadOnly(false);
						
						tfCodCfop.setReadOnly(false);
						tfCodOrigem.setReadOnly(false);
						tfCodCst.setReadOnly(false);
						tfCodCsosn.setReadOnly(false);
						tfCodCstEcf.setReadOnly(false);
						
						btSearchOrigem.setEnabled(true);						 
						btSearchCst.setEnabled(true);
						btSearchCSOSN.setEnabled(true);
						btSearchECF.setEnabled(true);
						btSearchCfop.setEnabled(true);
						
					}
					
					JavaScript.getCurrent().execute("$('#txtIcms').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtMVAIcmsST').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtBaseCalculoICMSST').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtAliquotaICMSST').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtAliquotaICMS').maskMoney({decimal:',',thousands:'.'})");				
					JavaScript.getCurrent().execute("$('#txtBaseCalculoICMS').maskMoney({decimal:',',thousands:'.'})");					
				}
				
				if(event.getTabSheet().getTab(event.getTabSheet().getSelectedTab()).getCaption().toString().equals("IPI")){		
					
					
					if(editar){
						cbCstIpi.setReadOnly(true);
						txtCnpjProdutor.setReadOnly(true);
						cbTipoCalculo.setReadOnly(true);
						txtValorBaseCalculoIpi.setReadOnly(true);
						txtAliquotaIpi.setReadOnly(true);
						txtValorIpi.setReadOnly(true);						
					}else{
						cbCstIpi.setReadOnly(false);
						txtCnpjProdutor.setReadOnly(false);
						cbTipoCalculo.setReadOnly(false);
						txtValorBaseCalculoIpi.setReadOnly(false);
						txtAliquotaIpi.setReadOnly(false);
						txtValorIpi.setReadOnly(false);
					}
					
					
					JavaScript.getCurrent().execute("$('#txtValorBaseCalculoIpi').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtAliquotaIpi').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtValorIpi').maskMoney({decimal:',',thousands:'.'})");				
					JavaScript.getCurrent().execute("$('#txtCnpjProdutor').mask('00.000.000/0000-00')");
				}
				
				if(event.getTabSheet().getTab(event.getTabSheet().getSelectedTab()).getCaption().toString().equals("PIS")){
					
					if(editar){
						
						cbCstPis.setReadOnly(true);
						cbTipoCalculoPis.setReadOnly(true);
						txtBaseCalculoPis.setReadOnly(true);
						txtAliquotaPis.setReadOnly(true);
						txtValorPis.setReadOnly(true);
						cbCstPisStTipoCalculo.setReadOnly(true);
						txtBaseCalculoPisSt.setReadOnly(true);
						txtAliquotaPisSt.setReadOnly(true);
						txtValorPisSt.setReadOnly(true);						
					}else{
						cbCstPis.setReadOnly(false);
						cbTipoCalculoPis.setReadOnly(false);
						txtBaseCalculoPis.setReadOnly(false);
						txtAliquotaPis.setReadOnly(false);
						txtValorPis.setReadOnly(false);
						cbCstPisStTipoCalculo.setReadOnly(false);
						txtBaseCalculoPisSt.setReadOnly(false);
						txtAliquotaPisSt.setReadOnly(false);
						txtValorPisSt.setReadOnly(false);
					}
					
					JavaScript.getCurrent().execute("$('#txtBaseCalculoPis').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtAliquotaPis').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtValorPis').maskMoney({decimal:',',thousands:'.'})");					
					JavaScript.getCurrent().execute("$('#txtBaseCalculoPisSt').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtAliquotaPisSt').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtValorPisSt').maskMoney({decimal:',',thousands:'.'})");
				}
				
				if(event.getTabSheet().getTab(event.getTabSheet().getSelectedTab()).getCaption().toString().equals("COFINS")){
					
					if(editar){
						cbCstCofins.setReadOnly(true);
						cbTipoCalculoCofins.setReadOnly(true);
						txtBaseCalculoCofins.setReadOnly(true);
						txtAliquotaCofins.setReadOnly(true);
						txtValorCofins.setReadOnly(true);
						cbTipoCalculoCofinsSt.setReadOnly(true);
						txtBaseCalculoCofinsSt.setReadOnly(true);
						txtAliquotaCofinsSt.setReadOnly(true);
						txtValorCofinsSt.setReadOnly(true);
					}else{
						cbCstCofins.setReadOnly(false);
						cbTipoCalculoCofins.setReadOnly(false);
						txtBaseCalculoCofins.setReadOnly(false);
						txtAliquotaCofins.setReadOnly(false);
						txtValorCofins.setReadOnly(false);
						cbTipoCalculoCofinsSt.setReadOnly(false);
						txtBaseCalculoCofinsSt.setReadOnly(false);
						txtAliquotaCofinsSt.setReadOnly(false);
						txtValorCofinsSt.setReadOnly(false);
					}
					
					
					JavaScript.getCurrent().execute("$('#txtBaseCalculoCofins').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtAliquotaCofins').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtValorCofins').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtBaseCalculoCofinsSt').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtAliquotaCofinsSt').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtValorCofinsSt').maskMoney({decimal:',',thousands:'.'})");					
				}
				
			}
		});
	
	}
	
	JPAContainer<TipoItemProduto> containerTipoItem;
	public JPAContainer<TipoItemProduto> buildContainer() {
		containerTipoItem = new JPAContainerFactory().make(TipoItemProduto.class,ConnUtil.getEntity());			
		
		return containerTipoItem;
	}
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		//Código, Cadastro, Alteração, Status	
		HorizontalLayout hl1 = new HorizontalLayout();
		hl1.setWidthUndefined();
					
		hl1.addComponent(new FormLayout(){
				{
					
					addStyleName("form-cutom");
					addStyleName("caption-align-produto-codigo"); 
					
					setMargin(true);
					setSpacing(true);
					
					tfCod = new TextField("Código");
					tfCod.setWidth("80px");
					
					if (item.getItemProperty("id").getValue() != null && !copiar) {
						fieldGroup.bind(tfCod, "id");
					}else{
						tfCod.setValue(getNextId());
					}
					
									
					tfCod.setReadOnly(true);
					addComponent(tfCod);
					
					if(item.getItemProperty("id").getValue() != null && !copiar){
						codProduto = (Integer)item.getItemProperty("id").getValue();
					}else{
						codProduto = Integer.parseInt(tfCod.getValue());
					}
				}
		});		
		hl1.addComponent(new FormLayout(){
			{								
				setStyleName("form-cutom");
				addStyleName("caption-align-produto-cadastro");
				setMargin(true);
				setSpacing(true);

				dfdataCadastro = new TextField("Cadastro");				
				dfdataCadastro.setImmediate(true);				
				dfdataCadastro.setWidth("140px");
						
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
				if(item.getItemProperty("dataEstoque").getValue() != null && !copiar){		
					Date dataCad = (Date)item.getItemProperty("dataEstoque").getValue();
					dfdataCadastro.setValue(sdf.format(dataCad));
				}else{
					dfdataCadastro.setValue(sdf.format(new Date()));
				}

				dfdataCadastro.setReadOnly(true);
				addComponent(dfdataCadastro);
			}
		});
		hl1.addComponent(new FormLayout(){
			{								
				setStyleName("form-cutom");
				addStyleName("caption-align-produto-alteracao");
				setMargin(true);
				setSpacing(true);
								
				dfdataAlteracao = new TextField("Alteração");
				dfdataAlteracao.setImmediate(true);		
				dfdataAlteracao.setWidth("140px");	
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
				if(item.getItemProperty("dataAlteracao").getValue() != null){		
					Date dataCad = (Date)item.getItemProperty("dataAlteracao").getValue();
					dfdataAlteracao.setValue(sdf.format(dataCad));
				}else{
					dfdataAlteracao.setValue(sdf.format(new Date()));
				}
					
				dfdataAlteracao.setReadOnly(true);
				addComponent(dfdataAlteracao);
			}
		});
		hl1.addComponent(new FormLayout(){
			{

				setStyleName("form-cutom");
				addStyleName("caption-align-produto-status");			
				setMargin(true);
				setSpacing(true);
													
				cbStatus = new ComboBox("Status");
				cbStatus.setTextInputAllowed(false);
				cbStatus.setNullSelectionAllowed(false);
				cbStatus.setRequired(true);
				cbStatus.addItem("ATIVO");
				cbStatus.addItem("INATIVO");
				cbStatus.addItem("EM DIGITACAO");
				cbStatus.setWidth("100px");
				
				if(item.getItemProperty("id").getValue() == null){
					cbStatus.select("ATIVO");
				}else{
					fieldGroup.bind(cbStatus,"status");
				}
				addComponent(cbStatus);
			}
		});
						
		vlRoot.addComponent(hl1);
						
		//referencia, cod barras, tipo item	
		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.setWidthUndefined();
		
		hl2.addComponent(new FormLayout(){
			{
				setStyleName("form-cutom-new");
				addStyleName("caption-align-produto-referencia");
				
				setMargin(true);
				setSpacing(true);
				
				tfRef = new TextField("Referência");
				tfRef.setNullRepresentation("");
				tfRef.setWidth("80px");
				tfRef.setTabIndex(1);				
				tfRef.focus();

				if(item.getItemProperty("id").getValue() == null || copiar){
					tfRef.setValue(codProduto.toString());
				}else{
					fieldGroup.bind(tfRef,"referencia");
				}
				addComponent(tfRef);
			}
		});		
		hl2.addComponent(new FormLayout(){
			{
					setStyleName("form-cutom-new");
					addStyleName("caption-align-produto-cod-barras");
					
					setMargin(true);
					setSpacing(true);
														
					txtCodBarras = new TextField("Código de Barras");				
					txtCodBarras.setRequired(true);
					txtCodBarras.setNullRepresentation("");	
					txtCodBarras.setWidth("150px");
					txtCodBarras.setTabIndex(2);
					
					txtCodBarras.addTextChangeListener(new FieldEvents.TextChangeListener() {
						
						@Override
						public void textChange(TextChangeEvent event) {
							boolean existe = ProdutoDAO.checkExistCodBarras(event.getText());
							
							if(existe && !event.getText().equals(codBarras)){
								txtCodBarras.addStyleName("invalid-txt");
								valid_cod_barras = false;
							}else{
								txtCodBarras.removeStyleName("invalid-txt");
								valid_cod_barras = true;
							}
						}
					});
										
					fieldGroup.bind(txtCodBarras,"gTin");
					if(copiar){
						txtCodBarras.setValue("");
					}
					
					addComponent(txtCodBarras);								
				}
			});		
		hl2.addComponent(new FormLayout(){
			{
						setStyleName("form-cutom-new");
						addStyleName("caption-align-produto-tipo-item");
						
						setMargin(true);
						setSpacing(true);
						
						cbItem = new ComboBox("Tipo de Item", buildContainer());
						cbItem.setTextInputAllowed(false);
						cbItem.setNullSelectionAllowed(false);
						cbItem.setRequired(true);
						cbItem.setWidth("174px");
						cbItem.setItemCaptionPropertyId("nome");
						cbItem.setConverter(new SingleSelectConverter(cbItem));
						
//						cbItem.addItem("Ativo Imobilizado");
//						cbItem.addItem("Matéria-prima");
//						cbItem.addItem("Material de uso e consumo");
//						cbItem.addItem("Mercadoria para Revenda");

						cbItem.setTabIndex(3);
						
						fieldGroup.bind(cbItem,"tipo_item");
						
						addComponent(cbItem);	
			}
		});
		
		vlRoot.addComponent(hl2);

		//produto
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
//					teste teField = new teste();
//					teField.addReplaceRule("|", "");
														
					txtNome = new TextField ("Produto");				
					txtNome.setWidth("740px");				
					txtNome.addStyleName("caption-align-editar-titulo");
					txtNome.setNullRepresentation("");
					txtNome.setRequired(true);
					txtNome.setMaxLength(200);
					txtNome.addStyleName("destaque-campo");
					txtNome.setTabIndex(4);
					
					
					fieldGroup.bind(txtNome,"nome");
					addComponent(txtNome);				
				}
			});

		

		//Unidade, Fracionar, Peso, Garantia
		HorizontalLayout hl3 = new HorizontalLayout();
		hl3.setWidthUndefined();
		
		hl3.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				addStyleName("caption-align-produto-unidade");
				
				setMargin(true);
				setSpacing(true);
				
				ComboBox cbUnd = new ComboBox("Unidade", getUnds()){
					@Override
					public String getItemCaption(Object itemId) {
					   Item item = getItem(itemId);
					
					   if (item == null) {
					      return "";
					   }
					     
					   return String.valueOf(item.getItemProperty("nome").getValue()) + " - " + 
					   		  String.valueOf(item.getItemProperty("descricao").getValue());
					}
				};
				cbUnd.setTextInputAllowed(false);
				cbUnd.setRequired(true);				
				cbUnd.setWidth("120px");
				cbUnd.setNullSelectionAllowed(false);
				cbUnd.setConverter(new SingleSelectConverter(cbUnd));
				cbUnd.setItemCaptionPropertyId("nome");
				cbUnd.setTabIndex(5);
				
				
				
								
				

				fieldGroup.bind(cbUnd,"unidadeProduto");
				
				if(cbUnd.getValue() == null){
					Collection<Object> itens = (Collection<Object>) cbUnd.getItemIds();

					for (Object itemId : itens) {
							if(cbUnd.getItem(itemId).getItemProperty("nome").getValue().toString().equals("UN")){
								cbUnd.select(itemId); 
							}
					}

				}

				addComponent(cbUnd);					
			}
		});	
		hl3.addComponent(new HorizontalLayout(){
			{
					addStyleName("caption-align-produto-fracionar");
				
					cbFracionar = new ComboBox ("Fracionar");
					cbFracionar.setTextInputAllowed(false);
					cbFracionar.setWidth("55px");				
					cbFracionar.setNullSelectionAllowed(false);
					cbFracionar.addItem("SIM");
					cbFracionar.addItem("NAO");
					cbFracionar.setRequired(true);
					cbFracionar.setTabIndex(6);
					
					if(item.getItemProperty("fracionar").getValue() != null && item.getItemProperty("fracionar").getValue().equals(1)){
						cbFracionar.select("SIM");
					}else{
						cbFracionar.select("NAO");
					}
					
					cbFracionar.setImmediate(true);
//					cbFracionar.addListener(new Property.ValueChangeListener(){
//
//						@Override
//						public void valueChange(ValueChangeEvent event) {
//							if(cbFracionar.getValue().toString().equals("NAO")){
//							//	cbUtilizaSeriais.addItem("SIM");
//							//	cbUtilizaSeriais.addItem("NAO");															
//							}else{
//								//cbUtilizaSeriais.addItem("NAO");			
//								//cbUtilizaSeriais.removeItem("SIM");			
//								cbUtilizaSeriais.select("NAO");
//							}
//						}
//					});
																					
					addComponent(new FormLayout(){
						{
							setStyleName("form-cutom-new");				
							
							setMargin(true);
							setSpacing(true);						
							addComponent(cbFracionar);								
						}
					});					
				}
		});
		hl3.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					addStyleName("caption-align-produto-peso");
					
					setMargin(true);
					setSpacing(true);
														
					TextField txtPeso = new TextField ("Peso(Kg)");				
					txtPeso.setWidth("92px");				
					txtPeso.setNullRepresentation("");
					txtPeso.setId("txtpeso");
					txtPeso.setTabIndex(7);
					
					fieldGroup.bind(txtPeso,"peso");
					addComponent(txtPeso);				
					
					JavaScript.getCurrent().execute("$('#txtpeso').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		hl3.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					addStyleName("caption-align-produto-garantia");
										
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
		
		vlRoot.addComponent(hl3);

		//Grupo , Marca
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
				
//				addComponent(new FormLayout(){					
//					{
//						setStyleName("form-cutom");
//						addStyleName("caption-align-produto-marca");
//						
//						setMargin(true);
//						setSpacing(true);
//															
//						ComboBox cbMarca = new ComboBox("Marca");		
//						cbMarca.setNullSelectionAllowed(false);
//						cbMarca.setRequired(true);
//						cbMarca.setWidth("159px");
//						cbMarca.setConverter(new SingleSelectConverter(cbMarca));
//						cbMarca.setItemCaptionPropertyId("nome");
//						cbMarca.setTabIndex(10);
//						
//						
//						
//						fieldGroup.bind(cbMarca,"marcasId");
//						addComponent(cbMarca);
//					}
//				});	

				
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
					ClienteDAO cDAO = new ClienteDAO();
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
		
			btSearchMarca = new Button();
			btSearchMarca.setStyleName(BaseTheme.BUTTON_LINK);
			btSearchMarca.setIcon(new ThemeResource("icons/search-16.png"));
			btSearchMarca.setTabIndex(300000);
			btSearchMarca.setEnabled(true);
			btSearchMarca.addClickListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					MarcaUtil marcaUtil = new MarcaUtil(true, true,OpusERP4UI.getEmpresa().getId());
					marcaUtil.addListerner(new MarcaUtil.MarcaUtilListerner() {
						
						@Override
						public void onSelected(MarcaUtilEvent event) {
								if(event.getMarca() != null){
									
									marcaSelecionado = event.getMarca();
									
									txtCodMarca.setValue(event.getMarca().getId().toString());
									txtDescricaoMarca.setReadOnly(false);
									txtDescricaoMarca.setValue(event.getMarca().getNome());
									txtDescricaoMarca.setReadOnly(true);
									
								}
							}							
					});
					
					getUI().addWindow(marcaUtil);
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
					addComponent(btSearchMarca);							
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

		HorizontalLayout hl5 = new HorizontalLayout();
		hl5.setWidth("75%");

		//Ultima Compra
		hl5.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
													
				txtUltCompra = new TextField ("Última Compra");				
				txtUltCompra.setWidth("200px");				
				txtUltCompra.addStyleName("caption-align-editar-titulo");
				txtUltCompra.setNullRepresentation("");
//				txtPeso.addStyleName("uppercase");
				
				List<MovimentoEntDetalhe> movimentoDetalhe = ProdutoDAO.movEntDet(codProduto);
				if(movimentoDetalhe!=null){
					Produto prod = new ProdutoDAO().find(codProduto);
					MovimentoEntDetalhe moviDetalhe = movimentoDetalhe.get(0);				
					MovimentoEntCabecalho moviCab = ProdutoDAO.movEntCab(moviDetalhe.getMovimentoEntCabecalhoId());	
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					
					Double valorDouble = Double.parseDouble(String.valueOf(moviDetalhe.getQuantidade()));															  
					DecimalFormat df = new DecimalFormat();  
					df.applyPattern("#,##0");  					
					String valorFormatado = df.format(valorDouble);
					
					if(moviCab!=null){
						String und = prod.getUnidadeProduto() != null ? prod.getUnidadeProduto().getNome() : "UN";
						String ultcomp = valorFormatado +"-"+und+" | "+sdf.format(moviCab.getDataHora());
						txtUltCompra.setValue(ultcomp);
					}
				}
				
				txtUltCompra.setReadOnly(true);
				addComponent(txtUltCompra);				
			}
		});
		hl5.addComponent(new FormLayout(){					
			{
				setStyleName("form-cutom-new");
				setMargin(true);
				setSpacing(true);
				btCompra = new Button("Últimas Compras", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						getUI().addWindow(new UltimasCompras(true, true, codProduto));
	
					}
				});		
				
				if(txtUltCompra.getValue()==null||txtUltCompra.getValue().equals("")){
					btCompra.setEnabled(false);				
				}
				
				btCompra.setStyleName(Reindeer.BUTTON_SMALL);
				addComponent(btCompra);
			}
		});		
		vlRoot.addComponent(hl5);
		
		//Fornecedor
		vlRoot.addComponent(new HorizontalLayout(){
			{
				addStyleName("caption-align-produto-fornecedor");
				
				tfCodFornecedor = new TextField("Fornecedor");				
				tfCodFornecedor.setWidth("60px");				
				tfCodFornecedor.setNullRepresentation("");
				tfCodFornecedor.setStyleName("caption-align-editar-titulo");
				tfCodFornecedor.setId("tfCodFornecedorProd");
				tfCodFornecedor.setTabIndex(11);
				
				
				JavaScript.getCurrent().execute("$('#tfCodFornecedorProd').mask('00000000000000')");
				tfCodFornecedor.setImmediate(true);		
				
				tfCodFornecedor.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						fornecedorSelecionado = new Fornecedor();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							Integer id = Integer.parseInt(event.getText());
							fornecedorSelecionado = FornecedorDAO.find(id);		
							
							if(fornecedorSelecionado != null){
								tfDescricaoFornecedor.setReadOnly(false);
								tfDescricaoFornecedor.setValue(fornecedorSelecionado.getRazao_social());
								tfDescricaoFornecedor.setReadOnly(true);
								
							}else{
								tfDescricaoFornecedor.setReadOnly(false);
								tfDescricaoFornecedor.setValue("");
								tfDescricaoFornecedor.setReadOnly(true);
								fornecedorSelecionado = null;
							}
						}else{
							tfDescricaoFornecedor.setReadOnly(false);
							tfDescricaoFornecedor.setValue("");
							tfDescricaoFornecedor.setReadOnly(true);
							
							fornecedorSelecionado = null;
						}
					}
				});

				tfCodFornecedor.setRequired(true);		
				tfDescricaoFornecedor = new TextField();
				tfDescricaoFornecedor.setTabIndex(2000);
				tfDescricaoFornecedor.setReadOnly(true);
				tfDescricaoFornecedor.setWidth("649px");
						
				if(item.getItemProperty("fornecedorId") != null && item.getItemProperty("fornecedorId").getValue() != null){
					fornecedorSelecionado = (Fornecedor)item.getItemProperty("fornecedorId").getValue();				
				}
				
				btSearchFornecedor = new Button();
				btSearchFornecedor.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchFornecedor.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchFornecedor.setTabIndex(300000);

				btSearchFornecedor.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						FornecedorUtil cUtil = new FornecedorUtil(true, true, true);
						cUtil.addListerner(new FornecedorUtil.FornecedorListerner() {
							
							@Override
							public void onSelected(
									FornecedorUtil.FornecedorEvent event) {
									if(event.getFornecedor() != null){
										tfCodFornecedor.setValue(event.getFornecedor().getId().toString());
										tfDescricaoFornecedor.setReadOnly(false);
										tfDescricaoFornecedor.setValue(event.getFornecedor().getRazao_social());
										tfDescricaoFornecedor.setReadOnly(true);
										fornecedorSelecionado = event.getFornecedor();
										
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
												
						addComponent(tfCodFornecedor);							
					}
				};
				addComponent(frmCodigoFornecedor);
		
				FormLayout frmButtonSearchFornecedor =new FormLayout(){
					{						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchFornecedor);							
					}
				}; 
							
				FormLayout frmDescFornecedor = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoFornecedor);							
					}
				}; 
	
				addComponent(frmButtonSearchFornecedor);
				addComponent(frmDescFornecedor);
				setExpandRatio(frmDescFornecedor, 1);	
				
				if(fornecedorSelecionado != null && tfCodFornecedor != null){
					tfCodFornecedor.setValue(fornecedorSelecionado.getId().toString());
//					tfCodFornecedor.setReadOnly(true);
				}
				
				if(fornecedorSelecionado != null && tfDescricaoFornecedor != null){
					tfDescricaoFornecedor.setReadOnly(false);
					tfDescricaoFornecedor.setValue(fornecedorSelecionado.getRazao_social());
					tfDescricaoFornecedor.setReadOnly(true);
				}
			}			
		});
				
		
		tbPrincipal = new TabSheet() {
			{
				setStyleName("custom-form_tabsheet");						
				setHeight("316px");
				setWidth("933px");
				
				
				addTab(new VerticalLayout() {
					{
							{										
			
								addComponent(new HorizontalLayout(){
									{
										addStyleName("caption-align-produto-impostos-ncm");
										
										txtCodNcm = new TextField("NCM");				
										txtCodNcm.setWidth("60px");				
										txtCodNcm.setNullRepresentation("");
										txtCodNcm.setRequired(true);
										txtCodNcm.setTabIndex(12);
										
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
												//e.printStackTrace();
												System.out
														.println("Não foi possivel buscar NCM");
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
	
								
								addComponent(new HorizontalLayout(){
									{
										addStyleName("caption-align-produto-impostos-cest");
										
										txtCodCest = new TextField("CEST");				
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
										
										//txtCodIpi.setRequired(true);		
										txtDescricaoCest = new TextField();
										txtDescricaoCest.setTabIndex(2000);
										txtDescricaoCest.setReadOnly(true);
										txtDescricaoCest.setWidth("649px");	
										
										if(item.getItemProperty("cest") != null && item.getItemProperty("cest").getValue() != null){
											try{												
												Cest cest = CestDAO.getCest(item.getItemProperty("cest").getValue().toString());												
												cestSelecionado = cest;
											}catch(Exception e){												
												System.out.println("Não foi possivel buscar CEST");
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
														if(event.getCest()!= null){
															
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
											//txtCodFornecedor.setReadOnly(true);
										}
										
										if(txtDescricaoCest != null && cestSelecionado != null){
											txtDescricaoCest.setReadOnly(false);
											txtDescricaoCest.setValue(cestSelecionado.getDescricao());
											txtDescricaoCest.setReadOnly(true);
										}
										
									}	
								});
								
								
								HorizontalLayout hl10 = new HorizontalLayout();
								hl10.setWidth("580px");
								
								hl10.addComponent(new FormLayout(){					
										{
											setStyleName("form-cutom-new");
											setMargin(true);
											setSpacing(true);
														
											
											cbIat = new ComboBox ("IAT");				
											cbIat.setNullSelectionAllowed(false);
											cbIat.setStyleName("caption-align-produto-tab");					
//											cbIat.setRequired(true);
											cbIat.setWidth("120px");
											cbIat.addItem("A-Arredondamento");
											cbIat.addItem("T-Truncamento");
											cbIat.setRequired(true);
											cbIat.setTabIndex(19);
											
											if(item.getItemProperty("id").getValue() == null){
												cbIat.select("T-Truncamento");							
											}
																
											addComponent(cbIat);		
											
											
											if(item.getItemProperty("iat").getValue() != null && item.getItemProperty("iat").getValue().toString().equals("A")){
												cbIat.select("A-Arredondamento");
											}
											
											if(item.getItemProperty("iat").getValue() != null && item.getItemProperty("iat").getValue().toString().equals("T")){
												cbIat.select("T-Truncamento");
											}

										}
									});		
								hl10.addComponent(new FormLayout(){					
										{
											setStyleName("form-cutom-new");
											setMargin(true);
											setSpacing(true);
													
											
											cbIppt = new ComboBox ("IPPT");
											cbIppt.setTextInputAllowed(false);
											cbIppt.setNullSelectionAllowed(false);
											cbIppt.setStyleName("caption-align-produto-ippt");				
//											cbIppt.setRequired(true);
											cbIppt.setWidth("120px");
											cbIppt.addItem("P-Própria");
											cbIppt.addItem("T-Terceiro");
											cbIppt.setRequired(true);
											cbIppt.setTabIndex(19);
											
											if(item.getItemProperty("id").getValue() == null){
												cbIppt.select("T-Terceiro");							
											}
																
											addComponent(cbIppt);		
											
											if(item.getItemProperty("ippt").getValue() != null && item.getItemProperty("ippt").getValue().toString().equals("P")){
												cbIppt.select("P-Própria");
											}
											
											if(item.getItemProperty("ippt").getValue() != null && item.getItemProperty("ippt").getValue().toString().equals("T")){
												cbIppt.select("T-Terceiro");
											}
											
										}
									});
								
								addComponent(hl10);
								
								HorizontalLayout hl11 = new HorizontalLayout();
								hl11.setWidthUndefined();
								
								hl11.addComponent(new FormLayout(){
									{
											setStyleName("form-cutom-new");
											addStyleName("caption-align-produto-valor-custo");
											
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
													
													Float valorCusto = Real.formatStringToDBFloat("0,00");
													Float valorIpi = Real.formatStringToDBFloat("0,00");
													Float valorFrete = Real.formatStringToDBFloat("0,00");
													Float valorDiferencaAliquota = Real.formatStringToDBFloat("0,00");
													
													if(event.getText() != null && !event.getText().equals("") && !event.getText().isEmpty()){
														valorCusto = Real.formatStringToDBFloat(event.getText());
													}														
													
													if(txtIPI.getValue() != null && !txtIPI.getValue().equals("") && !txtIPI.getValue().isEmpty()){
														valorIpi = Real.formatStringToDBFloat(txtIPI.getValue());
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
													
												    if(event.getText().equals("0,00")){
												    	txtCustoTotal.setReadOnly(false);
													    txtCustoTotal.setValue("0,00");
													    txtCustoTotal.setReadOnly(true);
												    }else{
												    	txtCustoTotal.setReadOnly(false);
													    txtCustoTotal.setValue(Real.formatDbToString(String.valueOf(valor_final)));
													    txtCustoTotal.setReadOnly(true);
												    }
												    
												    //Atualizar Custo
												    if(txtPrecoInicVenda.getValue() != null && event.getText() != null && !event.getText().equals("0,00")){
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
											});
											
											if(item.getItemProperty("valorCusto").getValue() != null){
												txtValorCusto.setValue(Real.formatDbToString(item.getItemProperty("valorCusto").getValue().toString()));
											}
											
											
											
											addComponent(txtValorCusto);				
											
											
											
										}
									});
								hl11.addComponent(new FormLayout(){
									{
											setStyleName("form-cutom-new");
											addStyleName("caption-align-produto-valor-venda");
											
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
											
											
										}
									});
								
								
								
								hl11.addComponent(new FormLayout(){
									{
											
											setStyleName("form-cutom");
											addStyleName("caption-align-produto-permitir-outras-tabelas");
											
											setMargin(true);
											setSpacing(true);
											
											txtPrecoPromocional = new TextField ("Valor Promocional");				
											txtPrecoPromocional.setWidth("55px");				
											txtPrecoPromocional.setRequired(true);
											txtPrecoPromocional.setMaxLength(200);
											txtPrecoPromocional.setId("txtPrecoPromocional");
											txtPrecoPromocional.addStyleName("sim-nao");											
											txtPrecoPromocional.setTabIndex(24);
											
											txtPrecoPromocional.addBlurListener(new FieldEvents.BlurListener() {
												
												@Override
												public void blur(BlurEvent event) {
													if(txtPrecoPromocional.getValue() == null || txtPrecoPromocional.getValue().equals("") || 
															txtPrecoPromocional.getValue().equals("0,00")){
														
														permitir_outras_tabelas = true;																
														cbPermitirOutrasTabela.removeStyleName("invalid");
													}
												}
											});
											
											txtPrecoPromocional.addTextChangeListener(new FieldEvents.TextChangeListener() {
												
												@Override
												public void textChange(TextChangeEvent event) {
													if(event.getText() != null && !event.getText().equals("")){
														double vlr_promo = Real.formatStringToDBDouble(event.getText());
														if(vlr_promo > 0){
															if(cbPermitirOutrasTabela.getValue().toString().equals("SIM")){
																permitir_outras_tabelas = false;																
																cbPermitirOutrasTabela.addStyleName("invalid");
																
																Notify.Show("Não é possível permitir outras tabelas para produtos com preço promocional!", Notify.TYPE_ERROR);
															}else{
																permitir_outras_tabelas = true;																
																cbPermitirOutrasTabela.removeStyleName("invalid");
															}
														}
													}else{
														permitir_outras_tabelas = true;																
														cbPermitirOutrasTabela.removeStyleName("invalid");
													}
												}
											});
											
											if(item.getItemProperty("preco_promocional").getValue() != null){
												txtPrecoPromocional.setValue(Real.formatDbToString(item.getItemProperty("preco_promocional").getValue().toString()));
											}
											
											//txtPrecoPromocional.setEnabled(false);
											JavaScript.getCurrent().execute("$('#txtPrecoPromocional').maskMoney({decimal:',',thousands:'.'})");
											
											addComponent(txtPrecoPromocional);				
											
										}
								});

								addComponent(hl11);
								
								
								HorizontalLayout hl12 = new HorizontalLayout();
								hl12.setWidthUndefined();
								
								hl12.addComponent(new FormLayout(){					
									{
										setStyleName("form-cutom");
										addStyleName("caption-align-produto-ipi");
										
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
										
										
									}
								});
								hl12.addComponent(new FormLayout(){					
										{
											setStyleName("form-cutom");
											addStyleName("caption-align-produto-lucro");
											
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
								//
								
								hl12.addComponent(new FormLayout(){					
									{
										setStyleName("form-cutom");
										addStyleName("caption-align-produto-permitir-outras-tabelas");
										
										setMargin(true);
										setSpacing(true);

										cbPermitirOutrasTabela = new ComboBox ("Permitir outras tabelas");
										cbPermitirOutrasTabela.setImmediate(true);
										cbPermitirOutrasTabela.setTextInputAllowed(false);
										cbPermitirOutrasTabela.setNullSelectionAllowed(false);
										//cbPermitirOutrasTabela.setStyleName("caption-align-editar-titulo");	
										cbPermitirOutrasTabela.addStyleName("sim-nao");
										cbPermitirOutrasTabela.setRequired(true);
										cbPermitirOutrasTabela.setWidth("55px");
										
										cbPermitirOutrasTabela.addItem("SIM");
										cbPermitirOutrasTabela.addItem("NAO");
										cbPermitirOutrasTabela.setTabIndex(30);

										if(item.getItemProperty("id").getValue()!= null){
											fieldGroup.bind(cbPermitirOutrasTabela, "outras_tb_desc");
										}else{
											cbPermitirOutrasTabela.select("NAO");
										}
										
										cbPermitirOutrasTabela.addValueChangeListener(new Property.ValueChangeListener() {
											
											@Override
											public void valueChange(ValueChangeEvent event) {
												if(cbPermitirOutrasTabela.getValue() != null && !cbPermitirOutrasTabela.getValue().toString().equals("") &&
														cbPermitirOutrasTabela.getValue().toString().equals("SIM")){
													
													if(txtPrecoPromocional.getValue() != null && !txtPrecoPromocional.getValue().equals("")){
														double vlr_promocional = Real.formatStringToDBDouble(txtPrecoPromocional.getValue());
														if(vlr_promocional > 0){
															permitir_outras_tabelas = false;
															cbPermitirOutrasTabela.addStyleName("invalid");
															
															Notify.Show("Não é possível permitir outras tabelas para produtos com preço promocional!", Notify.TYPE_ERROR);
														}else{
															permitir_outras_tabelas = true;
															cbPermitirOutrasTabela.removeStyleName("invalid");
														}														
													}else{
														permitir_outras_tabelas = true;
														cbPermitirOutrasTabela.removeStyleName("invalid");
													}
												}else{
													permitir_outras_tabelas = true;
													cbPermitirOutrasTabela.removeStyleName("invalid");
												}
											}
										});
										
										addComponent(cbPermitirOutrasTabela);	
									}
								});			
								addComponent(hl12);
								

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
											
											
										}
									});
								hl13.addComponent(new FormLayout(){					
										{
											setStyleName("form-cutom");
											addStyleName("caption-align-produto-margem-lucro");
											
											setMargin(true);
											setSpacing(true);
												
											
											txtMargemLucro = new TextField ("Margem de Lucro (%)");				
											txtMargemLucro.setWidth("56px");			
											txtMargemLucro.setMaxLength(200);
											txtMargemLucro.setId("txtMargemLucro");
											txtMargemLucro.addStyleName("align-currency");
											txtMargemLucro.setTabIndex(26);
																
											if(item.getItemProperty("margemLucro").getValue() != null){
												txtMargemLucro.setValue(Real.formatDbToString(item.getItemProperty("margemLucro").getValue().toString()));
											}
											txtMargemLucro.setReadOnly(true);
											
											addComponent(txtMargemLucro);				
											
//											txtMargemLucro.setValue("0,00");
										}
									});
								//
								
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
								
								addComponent(hl13);
																
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
												txtDiferencaAliquota.setValue(Real.formatDbToString(item.getItemProperty("diferenca_aliquota").getValue().toString()));
											}else{
												txtDiferencaAliquota.setValue("0,00");
											}

											addComponent(txtDiferencaAliquota);				
											

											
										}
									});
								
								
								addComponent(hl14);
								
								HorizontalLayout hl15 = new HorizontalLayout();
								hl15.setWidthUndefined();							
								
								hl15.addComponent(new FormLayout(){
									{
											setStyleName("form-cutom");
											addStyleName("caption-align-produto-custo-total");
											
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
//											JavaScript.getCurrent().execute("$('#txtEstoqMin').mask('0000000000')");
											

											
										}
									});
								
								
								
								hl15.addComponent(new FormLayout(){
									{
											setStyleName("form-cutom");
											addStyleName("caption-align-produto-avaliacao-clientes");
											
											setMargin(true);
											setSpacing(true);
																				
											ComboBox cbAvalCliente = new ComboBox ("Avaliação dos Clientes");
											cbAvalCliente.setTextInputAllowed(false);
											cbAvalCliente.setWidth("55px");				
											cbAvalCliente.addItem("1");
											cbAvalCliente.addItem("2");
											cbAvalCliente.addItem("3");
											cbAvalCliente.addItem("4");
											cbAvalCliente.addItem("5");
											cbAvalCliente.setTabIndex(32);
									
											fieldGroup.bind(cbAvalCliente, "aval_cliente");	
											if(copiar){
												cbAvalCliente.select(null);
											}
											addComponent(cbAvalCliente);
										}
									});
								
								addComponent(hl15);
								
								
								addComponent(
										new FormLayout(){					
										{
											setStyleName("form-cutom");
											setMargin(true);
											setSpacing(true);
																				
											TextField txtSaldo = new TextField ("Saldo");				
											txtSaldo.setWidth("80px");				
											txtSaldo.setStyleName("caption-align-produto-saldo");	
//											txtSaldo.setNullRepresentation("");
											txtSaldo.setMaxLength(200);
											txtSaldo.setId("txtSaldo");
											txtSaldo.addStyleName("align-currency");
											txtSaldo.setTabIndex(28);
											
											if(item.getItemProperty("qtdEstoque").getValue() != null){
												txtSaldo.setValue(Real.formatDbToString(item.getItemProperty("qtdEstoque").getValue().toString()));
											}
											if(copiar){
												txtSaldo.setValue("");
											}
											
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

								//FIM  PRINCIPAL
							}
						}														
					},"Principal");
				
				addTab(new VerticalLayout() {
					{					
						{				
							vlRootImpostos.addStyleName("tab-tributacao");
							addComponent(vlRootImpostos);							
						}
					}														
				},"Tributação");

				addTab(new VerticalLayout() {
					{
						{								
							final Table tbSeriais = new Table();
							tbSeriais.setWidth("100%");
							tbSeriais.setHeight("250px");
							
							tbSeriais.addContainerProperty("Código", String.class, "");
							tbSeriais.addContainerProperty("Serial", String.class, "");
							tbSeriais.addContainerProperty("Data Compra", String.class, "");
							
							final List<SerialProduto> seriais = SerialDAO.findActiveByCodProd((Integer)item.getItemProperty("id").getValue());
							
							if(seriais != null){
								Integer i=0;
								for (SerialProduto s: seriais) {
									SeriaisCompra sc = SeriaisCompraDAO.find(s.getId());
									String dataCompra = "";
									if(sc != null){
										MovimentoEntCabecalho mec = CompraDAO.find(sc.getItemPedido().getMovimentoEntCabecalhoId());
										dataCompra = DataUtil.formatDateBra(mec.getDataHora());
									}
									tbSeriais.addItem(new Object[]{s.getId().toString(), s.getSerial(),dataCompra}, i);
									i++;
								}
							}
													
							addComponent(new VerticalLayout(){
								{
									setSizeFull();
									
									addComponent(tbSeriais);
									
									if(seriais != null){
										addComponent(new Label(String.valueOf(seriais.size())+" registros encontrados"));
									}
								}
							});																				
						}
					}														
				},"Seriais");

				
				addTab(new VerticalLayout() {
					{					
						{				
							vlRootLojaIntegrada.addStyleName("tab-tributacao");
							addComponent(vlRootLojaIntegrada);							
						}
					}														
				},"Loja Integrada");
				
			}				
		};

		
		
		tbPrincipal.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				
				if(event.getTabSheet().getTab(event.getTabSheet().getSelectedTab()).getCaption().toString().equals("Principal")){
					JavaScript.getCurrent().execute("$('#txtValorCusto').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtPrecoInicVenda').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtPrecoPermitirDesconto').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#taxaIpi').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtLucro').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtFrete').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtMargemLucro').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtAliquota').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#tfCustoMedio').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtEstoqMin').maskMoney({decimal:',',thousands:'.'})");
				}
				
				if(event.getTabSheet().getTab(event.getTabSheet().getSelectedTab()).getCaption().toString().equals("Tributação")){
					
					if(editar){
						cbModBcIcms.setReadOnly(true);
						txtBaseCalculoICMS.setReadOnly(true);
						txtAliquotaICMS.setReadOnly(true);
						cbModBcIcmsSt.setReadOnly(true);
						txtMVAIcmsST.setReadOnly(true);
						txtBaseCalculoICMSST.setReadOnly(true);
						txtAliquotaICMSST.setReadOnly(true);
						
						tfCodCfop.setReadOnly(true);
						tfCodOrigem.setReadOnly(true);
						tfCodCst.setReadOnly(true);
						tfCodCsosn.setReadOnly(true);
						tfCodCstEcf.setReadOnly(true);
						
						btSearchCfop.setEnabled(false); 
						btSearchOrigem.setEnabled(false); 
						btSearchCst.setEnabled(false);
						btSearchCSOSN.setEnabled(false);
						btSearchECF.setEnabled(false);
					}else{
						cbModBcIcms.setReadOnly(false);
						txtBaseCalculoICMS.setReadOnly(false);
						txtAliquotaICMS.setReadOnly(false);
						cbModBcIcmsSt.setReadOnly(false);
						txtMVAIcmsST.setReadOnly(false);
						txtBaseCalculoICMSST.setReadOnly(false);
						txtAliquotaICMSST.setReadOnly(false);
						
						tfCodCfop.setReadOnly(false);
						tfCodOrigem.setReadOnly(false);
						tfCodCst.setReadOnly(false);
						tfCodCsosn.setReadOnly(false);
						tfCodCstEcf.setReadOnly(false);
						
						btSearchOrigem.setEnabled(true);
						btSearchCst.setEnabled(true);
						btSearchCSOSN.setEnabled(true);
						btSearchECF.setEnabled(true);
					}
					
					
					JavaScript.getCurrent().execute("$('#txtIcms').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtMVAIcmsST').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtBaseCalculoICMSST').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtAliquotaICMSST').maskMoney({decimal:',',thousands:'.'})");
					JavaScript.getCurrent().execute("$('#txtAliquotaICMS').maskMoney({decimal:',',thousands:'.'})");				
					JavaScript.getCurrent().execute("$('#txtBaseCalculoICMS').maskMoney({decimal:',',thousands:'.'})");					
				}
				
		
			}
		});
		vlRoot.addComponent(tbPrincipal);

		if(editar){
			fieldGroup.setReadOnly(true);		
			txtCodGrupo.setReadOnly(true);
			btCompra.setReadOnly(true);
			tfCodFornecedor.setReadOnly(true);
			cbIat.setReadOnly(true);
			cbIppt.setReadOnly(true);
			txtIPI.setReadOnly(true);
			cbFracionar.setReadOnly(true);
			txtValorCusto.setReadOnly(true);
			txtFrete.setReadOnly(true);
			txtDiferencaAliquota.setReadOnly(true);
			//txtPermitirDesconto.setReadOnly(true);
			//txtAliquotaIcms.setReadOnly(true);
			
			btSearchNcm.setEnabled(false);
			btSearchCest.setEnabled(false);
			
			Styles styles = Page.getCurrent().getStyles();					        
	        styles.add(".dashboard .v-slot-caption-align-produto-frete{margin-left: 100px;}");
	        styles.add(".dashboard .v-slot-caption-align-produto-unidade{margin-left: 106px;}");
	        
	        styles.add(".dashboard .v-slot-caption-align-produto-utiliza-seriais {margin-left: 224px;");
	        styles.add(".dashboard .v-slot-caption-align-produto-garantia {margin-left: 18px; }");
	        styles.add(".dashboard .v-slot-caption-align-produto-marca {margin-left: 15px; }");
	       
	        if(isFirefox){
	        	styles.add(".dashboard .v-slot-caption-align-produto-tipo-item {margin-left: 90px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-qtd-minima{	margin-left: 84px;}");
	        	styles.add(".dashboard .v-caption-caption-align-produto-saldo {width: 453px; }");
	        	styles.add(".dashboard .v-slot-caption-align-produto-valor-custo{margin-left: 59px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-custo-total{margin-left: 60px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-ipi{	margin-left: 115px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-permitir-outras-tabelas{	margin-left: 148px;}");
		    }
	        if(isChrome){
	        	styles.add(".dashboard .v-slot-caption-align-produto-tipo-item {margin-left: 87px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-custo-total{margin-left: 59px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-qtd-minima{	margin-left: 81px;}");
	        	styles.add(".dashboard .v-caption-caption-align-produto-saldo {width: 450px; }");
	        	styles.add(".dashboard .v-slot-caption-align-produto-valor-custo{margin-left: 57px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-ipi{	margin-left: 113px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-permitir-outras-tabelas{	margin-left: 149px;}");
		    }
	        
	        styles.add(".dashboard .v-slot-caption-align-produto-status {margin-left: 20px;}");	        
	        styles.add(".dashboard .v-slot-caption-align-produto-valor-venda{margin-left: 100px;}");
	        styles.add(".dashboard .v-slot-caption-align-produto-permitir-desconto{margin-left:151px;}");	        	        
	        styles.add(".dashboard .v-slot-caption-align-produto-avaliacao-clientes{	margin-left: 151px;}");        
	        styles.add(".dashboard .v-slot-caption-align-produto-lucro{	margin-left: 161px;}");
	        
			txtEstoqMin.setReadOnly(true);
			txtPrecoInicVenda.setReadOnly(true);
			txtPrecoPromocional.setReadOnly(true); 

			//btSearchCSOSN.setEnabled(false);
			//btSearchCst.setEnabled(false);
			//btSearchECF.setEnabled(false);
			btSearchFornecedor.setEnabled(false);
			btSearchGrupo.setEnabled(false);
			//btSearchOrigem.setEnabled(false);
		}else{
			
			btSearchNcm.setEnabled(true);
			btSearchCest.setEnabled(true);
			Styles styles = Page.getCurrent().getStyles();					        
	        styles.add(".dashboard .v-slot-caption-align-produto-frete{margin-left: 92px;}");
	        styles.add(".dashboard .v-slot-caption-align-produto-unidade{margin-left: 100px;}");
	        styles.add(".dashboard .v-caption-caption-align-produto-saldo {width: 451px; }");	        
	        styles.add(".dashboard .v-slot-caption-align-produto-utiliza-seriais {margin-left: 218px;");
	        styles.add(" .dashboard .v-slot-caption-align-produto-garantia {margin-left: 3px; }");
	        styles.add(".dashboard .v-slot-caption-align-produto-marca {margin-left: 8px; }");
	        
	        
	        if(isFirefox){
	        	styles.add(".dashboard .v-slot-caption-align-produto-tipo-item {margin-left: 76px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-qtd-minima{	margin-left: 75px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-avaliacao-clientes{	margin-left: 153px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-valor-custo{margin-left: 51px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-ipi{	margin-left: 107px;}");
	        	styles.add(".dashboard .v-slot-caption-align-produto-permitir-desconto{margin-left: 149px;}");
		    }
	        if(isChrome){
		        styles.add(".dashboard .v-slot-caption-align-produto-tipo-item {margin-left: 73px;}");
		        styles.add(".dashboard .v-slot-caption-align-produto-qtd-minima{	margin-left: 72px;}");
		        styles.add(".dashboard .v-slot-caption-align-produto-avaliacao-clientes{	margin-left: 152px;}");
		        styles.add(".dashboard .v-slot-caption-align-produto-valor-custo{margin-left: 49px;}");
		        styles.add(".dashboard .v-slot-caption-align-produto-ipi{	margin-left: 105px;}");
		        styles.add(".dashboard .v-slot-caption-align-produto-permitir-desconto{margin-left: 148px;}");
		    }
	        
	        styles.add(".dashboard .v-slot-caption-align-produto-status {margin-left: 13px;}");
	        
	        styles.add(".dashboard .v-slot-caption-align-produto-valor-venda{margin-left: 91px;}");
	        
	        styles.add(".dashboard .v-slot-caption-align-produto-impostos-ncm{margin-left: 117px;margin-top: 14px;}");
	        
	        
	        styles.add(".dashboard .v-slot-caption-align-produto-lucro{	margin-left: 160px;}");
	        styles.add(".dashboard .v-slot-caption-align-produto-permitir-outras-tabelas{	margin-left: 144px;}");
	        
	        
	        JavaScript.getCurrent().execute("$('#txtAliquota').maskMoney({decimal:',',thousands:'.'})");
			JavaScript.getCurrent().execute("$('#txtPrecoPermitirDesconto').maskMoney({decimal:',',thousands:'.'})");
			JavaScript.getCurrent().execute("$('#txtPrecoInicVenda').maskMoney({decimal:',',thousands:'.'})");
			JavaScript.getCurrent().execute("$('#txtValorCusto').maskMoney({decimal:',',thousands:'.'})");
			JavaScript.getCurrent().execute("$('#taxaIpi').maskMoney({decimal:',',thousands:'.'})");
			JavaScript.getCurrent().execute("$('#txtFrete').maskMoney({decimal:',',thousands:'.'})");
			JavaScript.getCurrent().execute("$('#txtEstoqMin').maskMoney({decimal:',',thousands:'.'})");		
		}
	}
	

	JPAContainer<UnidadeProduto> containerUnd;
	private JPAContainer<UnidadeProduto> getUnds(){
		containerUnd= JPAContainerFactory.make(UnidadeProduto.class, ConnUtil.getEntity());
		if(OpusERP4UI.getEmpresa().getId() == 1 || OpusERP4UI.getEmpresa().getId() == 5){
			containerUnd.addContainerFilter(Filters.or(Filters.eq("empresa_id", 1), Filters.eq("empresa_id", 5)));
		}else{
			containerUnd.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		}
		
		containerUnd.sort(new Object[]{"nome"}, new boolean[]{true});
		return containerUnd;
	}
	
//	private JPAContainer<Marca> getMarcas(){
//		JPAContainer<Marca> container = JPAContainerFactory.make(Marca.class, ConnUtil.getEntity());
//		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
//		container.sort(new Object[]{"nome"}, new boolean[]{true});
//		return container;
//	}
	private JPAContainer<GrupoProduto> getGrupos(){
		JPAContainer<GrupoProduto> container = JPAContainerFactory.make(GrupoProduto.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id",OpusERP4UI.getEmpresa().getId()));
		return container;
	}
	
	

	public Button buildBtSalvar(String s) {
		
		btSalvar = new Button(s, new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				
					if(event.getButton().getCaption().equals("Editar")){
						
					if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
							OpusERP4UI.getUsuarioLogadoUI().getId(), "Editar"))				
					{
						editar = false;
						Styles styles = Page.getCurrent().getStyles();					        
						styles.add(".dashboard .v-slot-caption-align-produto-frete{margin-left: 92px;}");
				        styles.add(".dashboard .v-slot-caption-align-produto-unidade{margin-left: 100px;}");
				        styles.add(".dashboard .v-caption-caption-align-produto-saldo {width: 451px; }");	        
				        styles.add(".dashboard .v-slot-caption-align-produto-utiliza-seriais {margin-left: 218px;}");
				        styles.add(".dashboard .v-slot-caption-align-produto-garantia {margin-left: 3px; }");
				        styles.add(".dashboard .v-slot-caption-align-produto-marca {margin-left: 8px; }");
				        
				        
				        if(isFirefox){
				        	styles.add(".dashboard .v-slot-caption-align-produto-tipo-item {margin-left: 76px;}");
				        	styles.add(".dashboard .v-slot-caption-align-produto-qtd-minima{	margin-left: 75px;}");
				        	styles.add(".dashboard .v-slot-caption-align-produto-avaliacao-clientes{	margin-left: 153px;}");
				        	styles.add(".dashboard .v-slot-caption-align-produto-valor-custo{margin-left: 51px;}");
				        	styles.add(".dashboard .v-slot-caption-align-produto-ipi{	margin-left: 107px;}");
				        	styles.add(".dashboard .v-slot-caption-align-produto-permitir-desconto{margin-left: 149px;}");
					    }
				        if(isChrome){
					        styles.add(".dashboard .v-slot-caption-align-produto-tipo-item {margin-left: 73px;}");
					        styles.add(".dashboard .v-slot-caption-align-produto-qtd-minima{	margin-left: 72px;}");
					        styles.add(".dashboard .v-slot-caption-align-produto-avaliacao-clientes{	margin-left: 152px;}");
					        styles.add(".dashboard .v-slot-caption-align-produto-valor-custo{margin-left: 49px;}");
					        styles.add(".dashboard .v-slot-caption-align-produto-ipi{	margin-left: 105px;}");
					        styles.add(".dashboard .v-slot-caption-align-produto-permitir-desconto{margin-left: 148px;}");
					    }
				        
				        styles.add(".dashboard .v-slot-caption-align-produto-status {margin-left: 13px;}");
				        
				        styles.add(".dashboard .v-slot-caption-align-produto-valor-venda{margin-left: 91px;}");
				        
				        styles.add(".dashboard .v-slot-caption-align-produto-impostos-ncm{margin-left: 117px;margin-top: 14px;}");
				        
				        
				        styles.add(".dashboard .v-slot-caption-align-produto-lucro{	margin-left: 160px;}");
				        styles.add(".dashboard .v-slot-caption-align-produto-permitir-outras-tabelas{	margin-left: 144px;}");
				        
				        
					        
					        
				        fieldGroup.setReadOnly(false);		
						txtDiferencaAliquota.setReadOnly(false);
						//txtPermitirDesconto.setReadOnly(false);
						txtAliquotaIcms.setReadOnly(false);
						
						tfCod.setReadOnly(true);		
						txtCodGrupo.setReadOnly(false);
						btCompra.setReadOnly(false);
						tfCodFornecedor.setReadOnly(false);
						tfCodCstEcf.setReadOnly(false);
						tfCodCfop.setReadOnly(false);
						tfCodOrigem.setReadOnly(false);
						tfCodCsosn.setReadOnly(false);
						tfCodCst.setReadOnly(false);
						cbIat.setReadOnly(false);
						cbIppt.setReadOnly(false);
						txtIPI.setReadOnly(false);
						cbFracionar.setReadOnly(false);
						txtValorCusto.setReadOnly(false);
						txtFrete.setReadOnly(false);
						txtCodNcm.setReadOnly(false);						
						
						txtEstoqMin.setReadOnly(false);
						txtPrecoInicVenda.setReadOnly(false);
						txtPrecoPromocional.setReadOnly(false);
						
						btSearchCSOSN.setEnabled(true);
						btSearchCst.setEnabled(true);
						btSearchECF.setEnabled(true);
						btSearchFornecedor.setEnabled(true);
						btSearchGrupo.setEnabled(true);
						btSearchOrigem.setEnabled(true);
						btSearchNcm.setEnabled(true); 
						btSearchCest.setEnabled(true);
						btSearchCfop.setEnabled(true);
						
						
						cbModBcIcms.setReadOnly(false);
						txtBaseCalculoICMS.setReadOnly(false);
						txtAliquotaICMS.setReadOnly(false);
						cbModBcIcmsSt.setReadOnly(false);
						txtMVAIcmsST.setReadOnly(false);
						txtBaseCalculoICMSST.setReadOnly(false);
						txtAliquotaICMSST.setReadOnly(false);
						
						cbCstIpi.setReadOnly(false);
						txtCnpjProdutor.setReadOnly(false);
						cbTipoCalculo.setReadOnly(false);
						txtValorBaseCalculoIpi.setReadOnly(false);
						txtAliquotaIpi.setReadOnly(false);
						txtValorIpi.setReadOnly(false);
						
						cbCstPis.setReadOnly(false);
						cbTipoCalculoPis.setReadOnly(false);
						txtBaseCalculoPis.setReadOnly(false);
						txtAliquotaPis.setReadOnly(false);
						txtValorPis.setReadOnly(false);
						cbCstPisStTipoCalculo.setReadOnly(false);
						txtBaseCalculoPisSt.setReadOnly(false);
						txtAliquotaPisSt.setReadOnly(false);
						txtValorPisSt.setReadOnly(false);
						
						cbCstCofins.setReadOnly(false);
						cbTipoCalculoCofins.setReadOnly(false);
						txtBaseCalculoCofins.setReadOnly(false);
						txtAliquotaCofins.setReadOnly(false);
						txtValorCofins.setReadOnly(false);
						cbTipoCalculoCofinsSt.setReadOnly(false);
						txtBaseCalculoCofinsSt.setReadOnly(false);
						txtAliquotaCofinsSt.setReadOnly(false);
						txtValorCofinsSt.setReadOnly(false);
						
						tfCodCfop.setReadOnly(false);
						tfCodOrigem.setReadOnly(false);
						tfCodCst.setReadOnly(false);
						tfCodCsosn.setReadOnly(false);
						tfCodCstEcf.setReadOnly(false);
						
						JavaScript.getCurrent().execute("$('#txtValorBaseCalculoIpi').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtAliquotaIpi').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtValorIpi').maskMoney({decimal:',',thousands:'.'})");				
						JavaScript.getCurrent().execute("$('#txtCnpjProdutor').mask('00.000.000/0000-00')");
						
						JavaScript.getCurrent().execute("$('#txtBaseCalculoPis').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtAliquotaPis').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtValorPis').maskMoney({decimal:',',thousands:'.'})");					
						JavaScript.getCurrent().execute("$('#txtBaseCalculoPisSt').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtAliquotaPisSt').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtValorPisSt').maskMoney({decimal:',',thousands:'.'})");
										
						JavaScript.getCurrent().execute("$('#txtAliquota').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtPrecoPermitirDesconto').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtPrecoInicVenda').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtValorCusto').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#taxaIpi').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtFrete').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtPrecoPromocional').maskMoney({decimal:',',thousands:'.'})");
						
						JavaScript.getCurrent().execute("$('#txtIcms').maskMoney({decimal:',',thousands:'.'})");						
						JavaScript.getCurrent().execute("$('#txtMVAIcmsST').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtBaseCalculoICMSST').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtAliquotaICMSST').maskMoney({decimal:',',thousands:'.'})");
						JavaScript.getCurrent().execute("$('#txtAliquotaICMS').maskMoney({decimal:',',thousands:'.'})");				
						JavaScript.getCurrent().execute("$('#txtBaseCalculoICMS').maskMoney({decimal:',',thousands:'.'})");		
						
						
						btSalvar.setCaption("Salvar");
						
						btSalvar = new Button("Editar", new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								
								if(!txtIPI.isValid()){
									txtIPI.setValue("0,00");
								}						
								if(!txtFrete.isValid()){
									txtFrete.setValue("0,00");
								}						
								if(!txtDiferencaAliquota.isValid()){
									txtDiferencaAliquota.setValue("0,00");
								}
								if(!txtPrecoInicVenda.isValid()){
									txtPrecoInicVenda.setValue("0,00");
								}
								if(!txtAliquotaIcms.isValid()){
									txtAliquotaIcms.setValue("0,00");
								}
								
								if(txtBaseCalculoPis.getValue().equals("")){
									txtBaseCalculoPis.setValue("0,00");
								}
								if(txtAliquotaPis.getValue().equals("")){
									txtAliquotaPis.setValue("0,00");
								}
								if(txtValorPis.getValue().equals("")){
									txtValorPis.setValue("0,00");
								}								
								if(txtBaseCalculoPisSt.getValue().equals("")){
									txtBaseCalculoPisSt.setValue("0,00");
								}
								if(txtAliquotaPisSt.getValue().equals("")){
									txtAliquotaPisSt.setValue("0,00");
								}
								if(txtValorPisSt.getValue().equals("")){
									txtValorPisSt.setValue("0,00");
								}
								
								if(txtBaseCalculoCofins.getValue().equals("")){
									txtBaseCalculoCofins.setValue("0,00");
								}
								if(txtAliquotaCofins.getValue().equals("")){
									txtAliquotaCofins.setValue("0,00");
								}
								if(txtValorCofins.getValue().equals("")){
									txtValorCofins.setValue("0,00");
								}
								if(txtBaseCalculoCofinsSt.getValue().equals("")){
									txtBaseCalculoCofinsSt.setValue("0,00");
								}
								if(txtAliquotaCofinsSt.getValue().equals("")){
									txtAliquotaCofinsSt.setValue("0,00");
								}
								if(txtValorCofinsSt.getValue().equals("")){
									txtValorCofinsSt.setValue("0,00");
								}
		
								
															
								if(fieldGroup.isValid() && origemSelecionado != null && fornecedorSelecionado != null && grupoSelecionado != null   && marcaSelecionado != null && ncmSelecionado != null && 
										txtAliquotaIcms.isValid() && txtValorCusto.isValid() && txtPrecoInicVenda.isValid() && 
										txtIPI.isValid() && cbPermitirOutrasTabela.isValid() && txtFrete.isValid() && cbUtilizaSeriais.isValid() && txtDiferencaAliquota.isValid() && 
										txtEstoqMin.isValid() && cstSelecionado != null && csosnSelecionado != null && cstecfSelecionado != null  && txtEstoqMin.isValid() 
										
										){
										try {
											
											if(icms == null){
												icms = new Icms();
											}
											
											if(cbModBcIcms.getValue() != null){
												icms.setMod_bc_icms(cbModBcIcms.getValue().toString());
											}else{
												icms.setMod_bc_icms(null);
											}
											if(cbModBcIcmsSt.getValue() != null){
												icms.setMod_bc_icms_st(cbModBcIcmsSt.getValue().toString());
											}else{
												icms.setMod_bc_icms_st(null);
											}
											
											if(txtBaseCalculoICMS.getValue() != null && txtBaseCalculoICMS.getValue() != "" && !txtBaseCalculoICMS.getValue().isEmpty()){
												icms.setBase_calc_icms(Real.formatStringToDBDouble(txtBaseCalculoICMS.getValue()));
											}else{
												icms.setBase_calc_icms(0);
											}
											
											if(txtAliquotaICMS.getValue() != null && txtAliquotaICMS.getValue() != "" && !txtAliquotaICMS.getValue().isEmpty()){
												icms.setAliquota_icms(Real.formatStringToDBDouble(txtAliquotaICMS.getValue()));
											}else{
												icms.setAliquota_icms(0);
											}
											
											if(txtBaseCalculoICMSST.getValue() != null && txtBaseCalculoICMSST.getValue() != "" && !txtBaseCalculoICMSST.getValue().isEmpty()){
												icms.setBase_calc_icms_st(Real.formatStringToDBDouble(txtBaseCalculoICMSST.getValue()));
											}else{
												icms.setBase_calc_icms_st(0);
											}
											
											if(txtAliquotaICMSST.getValue() != null && txtAliquotaICMSST.getValue() != "" && !txtAliquotaICMSST.getValue().isEmpty()){
												icms.setAliquota_icms_st(Real.formatStringToDBDouble(txtAliquotaICMSST.getValue()));
											}else{
												icms.setAliquota_icms_st(0);
											}
											
											if(txtMVAIcmsST.getValue() != null && txtMVAIcmsST.getValue() != "" && !txtMVAIcmsST.getValue().isEmpty()){
												icms.setMva(Real.formatStringToDBDouble(txtMVAIcmsST.getValue()));
											}else{
												icms.setMva(0);
											}
											
											if(ipi == null){
												ipi = new Ipi();
											}
											
											if(cbCstIpi.getValue() != null){
												EntityItem<CstIpi> entityItemCstIpi = (EntityItem<CstIpi>) cbCstIpi.getItem(cbCstIpi.getValue()); 
												ipi.setCst_ipi(entityItemCstIpi.getEntity());
											}else{
												ipi.setCst_ipi(null); 
											}
											
											if(txtCnpjProdutor.getValue() != null && txtCnpjProdutor.getValue() != "" && !txtCnpjProdutor.getValue().isEmpty()){
												ipi.setCnpj_produtor(txtCnpjProdutor.getValue());
											}else{
												ipi.setCnpj_produtor(null); 
											}
											
											if(cbTipoCalculo.getValue() != null){
												ipi.setTipo_calculo(cbTipoCalculo.getValue().toString());
											}else{
												ipi.setTipo_calculo(null);
											}											
											
											if(txtValorBaseCalculoIpi.getValue() != null && txtValorBaseCalculoIpi.getValue() != "" && !txtValorBaseCalculoIpi.getValue().isEmpty()){
												ipi.setValor_base_calculo(Real.formatStringToDBDouble(txtValorBaseCalculoIpi.getValue()));
											}else{
												ipi.setValor_base_calculo(0); 
											}
											
											if(txtAliquotaIpi.getValue() != null && txtAliquotaIpi.getValue() != "" && !txtAliquotaIpi.getValue().isEmpty()){
												ipi.setAliquota(Real.formatStringToDBDouble(txtAliquotaIpi.getValue()));
											}else{
												ipi.setAliquota(0); 
											}
											
											if(txtValorIpi.getValue() != null && txtValorIpi.getValue() != "" && !txtValorIpi.getValue().isEmpty()){
												ipi.setValor_ipi(Real.formatStringToDBDouble(txtAliquotaIpi.getValue()));
											}else{
												ipi.setValor_ipi(0); 
											}
											
											
											if(pis == null){
												pis = new Pis();
											}
											
											if(cbCstPis.getValue() != null){
												EntityItem<CstPis> entityItemCstIpi = (EntityItem<CstPis>) cbCstPis.getItem(cbCstPis.getValue()); 
												pis.setCst_pis(entityItemCstIpi.getEntity());
											}else{
												pis.setCst_pis(null); 
											}
											
											if(cbTipoCalculoPis.getValue() != null){
												pis.setTipo_calculo(cbTipoCalculoPis.getValue().toString());
											}else{
												pis.setTipo_calculo(null);
											}
											
											if(cbCstPisStTipoCalculo.getValue() != null){
												pis.setTipo_calculo_pis_st(cbCstPisStTipoCalculo.getValue().toString());
											}else{
												pis.setTipo_calculo_pis_st(null);
											}
											
											if(txtBaseCalculoPis.getValue() != null && txtBaseCalculoPis.getValue() != "" && !txtBaseCalculoPis.getValue().isEmpty()){
												pis.setBase_calculo_pis(Real.formatStringToDBDouble(txtBaseCalculoPis.getValue()));
											}else{
												pis.setBase_calculo_pis(0);
											}
											
											if(txtAliquotaPis.getValue() != null && txtAliquotaPis.getValue() != "" && !txtAliquotaPis.getValue().isEmpty()){
												pis.setAliquota(Real.formatStringToDBDouble(txtAliquotaPis.getValue()));
											}else{
												pis.setAliquota(0);
											}
											if(txtValorPis.getValue() != null && txtValorPis.getValue() != "" && !txtValorPis.getValue().isEmpty()){
												pis.setValor_pis(Real.formatStringToDBDouble(txtValorPis.getValue()));
											}else{
												pis.setValor_pis(0);
											}				
											
											if(txtBaseCalculoPisSt.getValue() != null && txtBaseCalculoPisSt.getValue() != "" && !txtBaseCalculoPisSt.getValue().isEmpty()){
												pis.setBase_calculo_pis_st(Real.formatStringToDBDouble(txtBaseCalculoPisSt.getValue()));
											}else{
												pis.setBase_calculo_pis_st(0);
											}
											
											if(txtAliquotaPisSt.getValue() != null && txtAliquotaPisSt.getValue() != "" && !txtAliquotaPisSt.getValue().isEmpty()){
												pis.setAliquota_pis_st(Real.formatStringToDBDouble(txtAliquotaPisSt.getValue()));
											}else{
												pis.setAliquota_pis_st(0);
											}
											if(txtValorPisSt.getValue() != null && txtValorPisSt.getValue() != "" && !txtValorPisSt.getValue().isEmpty()){
												pis.setValor_pis_st(Real.formatStringToDBDouble(txtValorPisSt.getValue()));
											}else{
												pis.setValor_pis_st(0);
											}				
											
											
											if(cofins == null){
												cofins = new Cofins();
											}
											
											if(cbCstCofins.getValue() != null){
												EntityItem<CstCofins> entityItemCstCofins = (EntityItem<CstCofins>) cbCstCofins.getItem(cbCstCofins.getValue()); 
												cofins.setCst_cofins(entityItemCstCofins.getEntity());
											}else{
												cofins.setCst_cofins(null); 
											}
											
											if(cbCstCofins.getValue() != null){
												EntityItem<CstCofins> entityItemCstCofins = (EntityItem<CstCofins>) cbCstCofins.getItem(cbCstCofins.getValue()); 
												cofins.setCst_cofins(entityItemCstCofins.getEntity());
											}else{
												cofins.setCst_cofins(null); 
											}
											
											if(cbTipoCalculoCofins.getValue() != null){
												cofins.setTipo_calculo(cbTipoCalculoCofins.getValue().toString());
											}else{
												cofins.setTipo_calculo(null);
											}
											
											if(cbTipoCalculoCofinsSt.getValue() != null){
												cofins.setTipo_calculo_st(cbTipoCalculoCofinsSt.getValue().toString());
											}else{
												cofins.setTipo_calculo_st(null);
											}
											
											if(txtBaseCalculoCofins.getValue() != null && txtBaseCalculoCofins.getValue() != "" && !txtBaseCalculoCofins.getValue().isEmpty()){
												cofins.setBase_calculo_cofins(Real.formatStringToDBDouble(txtBaseCalculoCofins.getValue()));
											}else{
												cofins.setBase_calculo_cofins(0);
											}
											if(txtAliquotaCofins.getValue() != null && txtAliquotaCofins.getValue() != "" && !txtAliquotaCofins.getValue().isEmpty()){
												cofins.setAliquota_cofins(Real.formatStringToDBDouble(txtAliquotaCofins.getValue()));
											}else{
												cofins.setAliquota_cofins(0);
											}
											if(txtValorCofins.getValue() != null && txtValorCofins.getValue() != "" && !txtValorCofins.getValue().isEmpty()){
												cofins.setValor_cofins(Real.formatStringToDBDouble(txtValorCofins.getValue()));
											}else{
												cofins.setValor_cofins(0);
											}
											
											if(txtBaseCalculoCofinsSt.getValue() != null && txtBaseCalculoCofinsSt.getValue() != "" && !txtBaseCalculoCofinsSt.getValue().isEmpty()){
												cofins.setBase_calculo_cst(Real.formatStringToDBDouble(txtBaseCalculoCofinsSt.getValue()));
											}else{
												cofins.setBase_calculo_cst(0);
											}
											if(txtAliquotaCofinsSt.getValue() != null && txtAliquotaCofinsSt.getValue() != "" && !txtAliquotaCofinsSt.getValue().isEmpty()){
												cofins.setAliquota_cofins_st(Real.formatStringToDBDouble(txtAliquotaCofinsSt.getValue()));
											}else{
												cofins.setAliquota_cofins_st(0);
											}
											if(txtValorCofinsSt.getValue() != null && txtValorCofinsSt.getValue() != "" && !txtValorCofinsSt.getValue().isEmpty()){
												cofins.setValor_cofins_st(Real.formatStringToDBDouble(txtValorCofinsSt.getValue()));
											}else{
												cofins.setValor_cofins_st(0);
											}
											
											
											
											fieldGroup.commit();
											
											if(item.getItemProperty("id").getValue() != null && item.getItemProperty("qtdEstoque").getValue() !=null){
												Produto prod = ProdutoDAO.find((Integer)item.getItemProperty("id").getValue());
												if(prod!=null && prod.getQtdEstoque()!=null){
													
													Produto p = ProdutoDAO.find((Integer)item.getItemProperty("id").getValue());
													
													//-----Logg
													LogProdutoDAO.registraLog(new LogProduto(null,p, "SALVAR PRODUTO SEM ALTERACAO", p.getQtdEstoque(),p.getQtdEstoque(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
													//-----Logg
													
													item.getItemProperty("qtdEstoque").setValue(p.getQtdEstoque());
												}
											}
											
											if(item.getItemProperty("id").getValue() != null && copiar){
												item.getItemProperty("id").setValue(codProduto);
											}
											
											if(fornecedorSelecionado != null){
												item.getItemProperty("fornecedorId").setValue(fornecedorSelecionado);
											}
											
										
											
										
											
											item.getItemProperty("status").setValue(cbStatus.getValue().toString());
											item.getItemProperty("grupoId").setValue(grupoSelecionado);
											item.getItemProperty("marcasId").setValue(marcaSelecionado);
											item.getItemProperty("cst_origem").setValue(origemSelecionado);
											item.getItemProperty("cst_forma_tributo").setValue(cstSelecionado);
											item.getItemProperty("ecf_cup_filcal").setValue(cstecfSelecionado);
											item.getItemProperty("simples_nacional").setValue(csosnSelecionado);
											
											item.getItemProperty("referencia").setValue(tfRef.getValue());
											
											item.getItemProperty("outras_tb_desc").setValue(cbPermitirOutrasTabela.getValue().toString());
											
											
											item.getItemProperty("taxaIcms").setValue(new Float(17));
											
											if(item.getItemProperty("id").getValue() == null || copiar){
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
											if(cbSituacaoTributaria != null && cbSituacaoTributaria.getValue() != null){
												item.getItemProperty("ecfIcmsSt").setValue(cbSituacaoTributaria.getValue().toString().substring(0, 1));
											}
											if(cbIat != null && cbIat.getValue() != null){
												item.getItemProperty("iat").setValue(cbIat.getValue().toString().substring(0, 1));
											}
											if(cbIppt != null && cbIppt.getValue() != null){
												item.getItemProperty("ippt").setValue(cbIppt.getValue().toString().substring(0, 1));
											}					
														
											if(txtIPI != null && txtIPI.getValue() != null){
												item.getItemProperty("taxaIpi").setValue(Real.formatStringToDBFloat(txtIPI.getValue()));
											}
											
											if(txtAliquotaIcms != null && txtAliquotaIcms.getValue() != null){
												item.getItemProperty("taxaIcms").setValue(Real.formatStringToDBFloat(txtAliquotaIcms.getValue()));
											}
							
											
							
											if(txtFrete != null && txtFrete.getValue() != null){
												item.getItemProperty("frete").setValue(Real.formatStringToDBFloat(txtFrete.getValue()));
											}
											if(txtValorCusto != null && txtValorCusto.getValue() != null){
												item.getItemProperty("valorCusto").setValue(Real.formatStringToDBFloat(txtValorCusto.getValue()));
											}
											
											if(txtCustoTotal != null && txtCustoTotal.getValue() != null){
												item.getItemProperty("custo_total").setValue(Real.formatStringToDBFloat(txtCustoTotal.getValue()));
											}
											
											if(txtPrecoPromocional != null && txtPrecoPromocional.getValue() != null){
												item.getItemProperty("preco_promocional").setValue(Real.formatStringToDBFloat(txtPrecoPromocional.getValue()));
											}
											
											if(txtLucro != null && txtLucro.getValue() != null){
												item.getItemProperty("lucro").setValue(Real.formatStringToDBFloat(txtLucro.getValue()));
											}
											if(txtMargemLucro != null && txtMargemLucro.getValue() != null){
												item.getItemProperty("margemLucro").setValue(Real.formatStringToDBFloat(txtMargemLucro.getValue()));
											}
									
											if(cbFracionar.getValue().toString().equals("SIM")){
												fracionar = 1;
											}else{
												fracionar = 0;
											}
											
											item.getItemProperty("fracionar").setValue(fracionar);
				
											if(item.getItemProperty("dataEstoque").getValue() == null || copiar){
												item.getItemProperty("dataEstoque").setValue(new Date());
											}
											item.getItemProperty("dataAlteracao").setValue(new Date());	
											
											item.getItemProperty("nome").setValue(item.getItemProperty("nome").getValue().toString().replaceAll("[|]", ""));
											
											
										
											
											
											fireEvent(new ProdutoEvent(getUI(), item, icms, ipi,pis,cofins,true));	
											tbPrincipal.getTab(1).setStyleName("tab-valid");
											//close();
											
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
										
										if(fornecedorSelecionado == null){
											tfCodFornecedor.addStyleName("invalid-txt");
										}else{
											tfCodFornecedor.removeStyleName("invalid-txt");
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
										
										boolean tributacao_invalid = false;
										
										if(origemSelecionado == null){
											tfCodOrigem.addStyleName("invalid-txt");
											tributacao_invalid = true;
										}else{
											tfCodOrigem.removeStyleName("invalid-txt");
										}
										
										if(cstSelecionado == null){
											tfCodCst.addStyleName("invalid-txt");
											tributacao_invalid = true;											
										}else{
											tfCodCst.removeStyleName("invalid-txt");								
										}
										
										if(csosnSelecionado == null){
											tfCodCsosn.addStyleName("invalid-txt");
											tributacao_invalid = true;
										}else{
											tfCodCsosn.removeStyleName("invalid-txt");
										}
										
										if(cstecfSelecionado == null){
											tfCodCstEcf.addStyleName("invalid-txt");
											tributacao_invalid = true;
										}else{
											tfCodCstEcf.removeStyleName("invalid-txt");
										}
										
										if(!txtAliquotaIcms.isValid()){
											txtAliquotaIcms.addStyleName("invalid-txt");
											tributacao_invalid = true;
										}else{
											txtAliquotaIcms.removeStyleName("invalid-txt");
										}
																			
										if(tributacao_invalid){
											tbPrincipal.getTab(1).setStyleName("tab-invalid");
										}else{
											tbPrincipal.getTab(1).setStyleName("tab-valid");
										}
										
										if(!txtValorCusto.isValid()){
											txtValorCusto.addStyleName("invalid-txt");
										}else{
											txtValorCusto.removeStyleName("invalid-txt");
										}
										
										if(!txtPrecoInicVenda.isValid()){
											txtPrecoInicVenda.addStyleName("invalid-txt");
										}else{
											txtPrecoInicVenda.removeStyleName("invalid-txt");
										}
										
//										if(!txtPermitirDesconto.isValid()){
//											txtPermitirDesconto.addStyleName("invalid-txt");
//										}else{
//											txtPermitirDesconto.removeStyleName("invalid-txt");
//										}
										
										if(!txtIPI.isValid()){
											txtIPI.addStyleName("invalid-txt");
										}else{
											txtIPI.removeStyleName("invalid-txt");
										}
										
										if(!cbPermitirOutrasTabela.isValid()){
											cbPermitirOutrasTabela.addStyleName("invalid-txt");
										}else{
											cbPermitirOutrasTabela.removeStyleName("invalid-txt");
										}
										
										if(!txtFrete.isValid()){
											txtFrete.addStyleName("invalid-txt");
										}else{
											txtFrete.removeStyleName("invalid-txt");
										}
										
										if(!cbUtilizaSeriais.isValid()){
											cbUtilizaSeriais.addStyleName("invalid-txt");
										}else{
											cbUtilizaSeriais.removeStyleName("invalid-txt");
										}
										
										if(!txtDiferencaAliquota.isValid()){
											txtDiferencaAliquota.addStyleName("invalid-txt");
										}else{
											txtDiferencaAliquota.removeStyleName("invalid-txt");
										}
										
										if(!txtEstoqMin.isValid()){
											txtEstoqMin.addStyleName("invalid-txt");
										}else{
											txtEstoqMin.removeStyleName("invalid-txt");
										}
										
										
										
										
										Notify.Show_Invalid_Submit_Form();
									}
							 }
						});					
					}else{					
						Notify.Show("Você não Possui Permissão para Editar o Produto", Notify.TYPE_ERROR);
					}
					}else{
						
						if(!txtIPI.isValid()){
							txtIPI.setValue("0,00");
						}						
						if(!txtFrete.isValid()){
							txtFrete.setValue("0,00");
						}						
						if(!txtDiferencaAliquota.isValid()){
							txtDiferencaAliquota.setValue("0,00");
						}
						if(!txtPrecoInicVenda.isValid()){
							txtPrecoInicVenda.setValue("0,00");
						}
						if(!txtAliquotaIcms.isValid()){
							txtAliquotaIcms.setValue("0,00");
						}						
//						if(!txtPermitirDesconto.isValid()){
//							txtPermitirDesconto.setValue("0,00");
//						}
						
						if(fieldGroup.isValid() && origemSelecionado != null && fornecedorSelecionado != null && grupoSelecionado != null &&  marcaSelecionado != null && ncmSelecionado != null && 
								txtAliquotaIcms.isValid() && txtValorCusto.isValid() && txtPrecoInicVenda.isValid() && 
								txtIPI.isValid() && cbPermitirOutrasTabela.isValid() && txtFrete.isValid() && cbUtilizaSeriais.isValid() && txtDiferencaAliquota.isValid() && 
								txtEstoqMin.isValid() && cstSelecionado != null && csosnSelecionado != null && cstecfSelecionado != null  && txtEstoqMin.isValid() && 
								valid_cod_barras  && permitir_outras_tabelas){
							try {
								
								if(icms == null){
									icms = new Icms();
								}
								
								if(cbModBcIcms.getValue() != null){
									icms.setMod_bc_icms(cbModBcIcms.getValue().toString());
								}else{
									icms.setMod_bc_icms(null);
								}
								if(cbModBcIcmsSt.getValue() != null){
									icms.setMod_bc_icms_st(cbModBcIcmsSt.getValue().toString());
								}else{
									icms.setMod_bc_icms_st(null);
								}
								
								if(txtBaseCalculoICMS.getValue() != null && txtBaseCalculoICMS.getValue() != "" && !txtBaseCalculoICMS.getValue().isEmpty()){
									icms.setBase_calc_icms(Real.formatStringToDBDouble(txtBaseCalculoICMS.getValue()));
								}else{
									icms.setBase_calc_icms(0);
								}
								
								if(txtAliquotaICMS.getValue() != null && txtAliquotaICMS.getValue() != "" && !txtAliquotaICMS.getValue().isEmpty()){
									icms.setAliquota_icms(Real.formatStringToDBDouble(txtAliquotaICMS.getValue()));
								}else{
									icms.setAliquota_icms(0);
								}
								
								if(txtBaseCalculoICMSST.getValue() != null && txtBaseCalculoICMSST.getValue() != "" && !txtBaseCalculoICMSST.getValue().isEmpty()){
									icms.setBase_calc_icms_st(Real.formatStringToDBDouble(txtBaseCalculoICMSST.getValue()));
								}else{
									icms.setBase_calc_icms_st(0);
								}
								
								if(txtAliquotaICMSST.getValue() != null && txtAliquotaICMSST.getValue() != "" && !txtAliquotaICMSST.getValue().isEmpty()){
									icms.setAliquota_icms_st(Real.formatStringToDBDouble(txtAliquotaICMSST.getValue()));
								}else{
									icms.setAliquota_icms_st(0);
								}
								
								if(txtMVAIcmsST.getValue() != null && txtMVAIcmsST.getValue() != "" && !txtMVAIcmsST.getValue().isEmpty()){
									icms.setMva(Real.formatStringToDBDouble(txtMVAIcmsST.getValue()));
								}else{
									icms.setMva(0);
								}
								
								
								if(ipi == null){
									ipi = new Ipi();
								}
								
								if(cbCstIpi.getValue() != null){
									EntityItem<CstIpi> entityItemCstIpi = (EntityItem<CstIpi>) cbCstIpi.getItem(cbCstIpi.getValue()); 
									ipi.setCst_ipi(entityItemCstIpi.getEntity());
								}else{
									ipi.setCst_ipi(null); 
								}
								
								if(txtCnpjProdutor.getValue() != null && txtCnpjProdutor.getValue() != "" && !txtCnpjProdutor.getValue().isEmpty()){
									ipi.setCnpj_produtor(txtCnpjProdutor.getValue());
								}else{
									ipi.setCnpj_produtor(null); 
								}
								
								if(cbTipoCalculo.getValue() != null){
									ipi.setTipo_calculo(cbTipoCalculo.getValue().toString());
								}else{
									ipi.setTipo_calculo(null);
								}											
								
								if(txtValorBaseCalculoIpi.getValue() != null && txtValorBaseCalculoIpi.getValue() != "" && !txtValorBaseCalculoIpi.getValue().isEmpty()){
									ipi.setValor_base_calculo(Real.formatStringToDBDouble(txtValorBaseCalculoIpi.getValue()));
								}else{
									ipi.setValor_base_calculo(0); 
								}
								
								if(txtAliquotaIpi.getValue() != null && txtAliquotaIpi.getValue() != "" && !txtAliquotaIpi.getValue().isEmpty()){
									ipi.setAliquota(Real.formatStringToDBDouble(txtAliquotaIpi.getValue()));
								}else{
									ipi.setAliquota(0); 
								}
								
								if(txtValorIpi.getValue() != null && txtValorIpi.getValue() != "" && !txtValorIpi.getValue().isEmpty()){
									ipi.setValor_ipi(Real.formatStringToDBDouble(txtAliquotaIpi.getValue()));
								}else{
									ipi.setValor_ipi(0); 
								}
								
								if(pis == null){
									pis = new Pis();
								}
								
								if(cbCstPis.getValue() != null){
									EntityItem<CstPis> entityItemCstIpi = (EntityItem<CstPis>) cbCstPis.getItem(cbCstPis.getValue()); 
									pis.setCst_pis(entityItemCstIpi.getEntity());
								}else{
									pis.setCst_pis(null); 
								}
								
								if(cbTipoCalculoPis.getValue() != null){
									pis.setTipo_calculo(cbTipoCalculoPis.getValue().toString());
								}else{
									pis.setTipo_calculo(null);
								}
								
								if(cbCstPisStTipoCalculo.getValue() != null){
									pis.setTipo_calculo_pis_st(cbCstPisStTipoCalculo.getValue().toString());
								}else{
									pis.setTipo_calculo_pis_st(null);
								}
								
								if(txtBaseCalculoPis.getValue() != null && txtBaseCalculoPis.getValue() != "" && !txtBaseCalculoPis.getValue().isEmpty()){
									pis.setBase_calculo_pis(Real.formatStringToDBDouble(txtBaseCalculoPis.getValue()));
								}else{
									pis.setBase_calculo_pis(0);
								}
								
								if(txtAliquotaPis.getValue() != null && txtAliquotaPis.getValue() != "" && !txtAliquotaPis.getValue().isEmpty()){
									pis.setAliquota(Real.formatStringToDBDouble(txtAliquotaPis.getValue()));
								}else{
									pis.setAliquota(0);
								}
								if(txtValorPis.getValue() != null && txtValorPis.getValue() != "" && !txtValorPis.getValue().isEmpty()){
									pis.setValor_pis(Real.formatStringToDBDouble(txtValorPis.getValue()));
								}else{
									pis.setValor_pis(0);
								}				
								
								if(txtBaseCalculoPisSt.getValue() != null && txtBaseCalculoPisSt.getValue() != "" && !txtBaseCalculoPisSt.getValue().isEmpty()){
									pis.setBase_calculo_pis_st(Real.formatStringToDBDouble(txtBaseCalculoPisSt.getValue()));
								}else{
									pis.setBase_calculo_pis_st(0);
								}
								
								if(txtAliquotaPisSt.getValue() != null && txtAliquotaPisSt.getValue() != "" && !txtAliquotaPisSt.getValue().isEmpty()){
									pis.setAliquota_pis_st(Real.formatStringToDBDouble(txtAliquotaPisSt.getValue()));
								}else{
									pis.setAliquota_pis_st(0);
								}
								if(txtValorPisSt.getValue() != null && txtValorPisSt.getValue() != "" && !txtValorPisSt.getValue().isEmpty()){
									pis.setValor_pis_st(Real.formatStringToDBDouble(txtValorPisSt.getValue()));
								}else{
									pis.setValor_pis_st(0);
								}		
								
								
								if(cofins == null){
									cofins = new Cofins();
								}
								
								if(cbCstCofins.getValue() != null){
									EntityItem<CstCofins> entityItemCstCofins = (EntityItem<CstCofins>) cbCstCofins.getItem(cbCstCofins.getValue()); 
									cofins.setCst_cofins(entityItemCstCofins.getEntity());
								}else{
									cofins.setCst_cofins(null); 
								}
								
								if(cbCstCofins.getValue() != null){
									EntityItem<CstCofins> entityItemCstCofins = (EntityItem<CstCofins>) cbCstCofins.getItem(cbCstCofins.getValue()); 
									cofins.setCst_cofins(entityItemCstCofins.getEntity());
								}else{
									cofins.setCst_cofins(null); 
								}
								
								if(cbTipoCalculoCofins.getValue() != null){
									cofins.setTipo_calculo(cbTipoCalculoCofins.getValue().toString());
								}else{
									cofins.setTipo_calculo(null);
								}
								
								if(cbTipoCalculoCofinsSt.getValue() != null){
									cofins.setTipo_calculo_st(cbTipoCalculoCofinsSt.getValue().toString());
								}else{
									cofins.setTipo_calculo_st(null);
								}
								
								if(txtBaseCalculoCofins.getValue() != null && txtBaseCalculoCofins.getValue() != "" && !txtBaseCalculoCofins.getValue().isEmpty()){
									cofins.setBase_calculo_cofins(Real.formatStringToDBDouble(txtBaseCalculoCofins.getValue()));
								}else{
									cofins.setBase_calculo_cofins(0);
								}
								if(txtAliquotaCofins.getValue() != null && txtAliquotaCofins.getValue() != "" && !txtAliquotaCofins.getValue().isEmpty()){
									cofins.setAliquota_cofins(Real.formatStringToDBDouble(txtAliquotaCofins.getValue()));
								}else{
									cofins.setAliquota_cofins(0);
								}
								if(txtValorCofins.getValue() != null && txtValorCofins.getValue() != "" && !txtValorCofins.getValue().isEmpty()){
									cofins.setValor_cofins(Real.formatStringToDBDouble(txtValorCofins.getValue()));
								}else{
									cofins.setValor_cofins(0);
								}
								
								if(txtBaseCalculoCofinsSt.getValue() != null && txtBaseCalculoCofinsSt.getValue() != "" && !txtBaseCalculoCofinsSt.getValue().isEmpty()){
									cofins.setBase_calculo_cst(Real.formatStringToDBDouble(txtBaseCalculoCofinsSt.getValue()));
								}else{
									cofins.setBase_calculo_cst(0);
								}
								if(txtAliquotaCofinsSt.getValue() != null && txtAliquotaCofinsSt.getValue() != "" && !txtAliquotaCofinsSt.getValue().isEmpty()){
									cofins.setAliquota_cofins_st(Real.formatStringToDBDouble(txtAliquotaCofinsSt.getValue()));
								}else{
									cofins.setAliquota_cofins_st(0);
								}
								if(txtValorCofinsSt.getValue() != null && txtValorCofinsSt.getValue() != "" && !txtValorCofinsSt.getValue().isEmpty()){
									cofins.setValor_cofins_st(Real.formatStringToDBDouble(txtValorCofinsSt.getValue()));
								}else{
									cofins.setValor_cofins_st(0);
								}
								
								fieldGroup.commit();
								
								if(item.getItemProperty("id").getValue() != null && item.getItemProperty("qtdEstoque").getValue() !=null){
									Produto prod = ProdutoDAO.find((Integer)item.getItemProperty("id").getValue());
									
									//-----Logg
									LogProdutoDAO.registraLog(new LogProduto(null,prod, "SALVAR PRODUTO", prod.getQtdEstoque(),prod.getQtdEstoque(), new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
									//-----Logg
									
									if(prod!=null && prod.getQtdEstoque()!=null){
										item.getItemProperty("qtdEstoque").setValue(prod.getQtdEstoque());
									}
								}
								
								if(item.getItemProperty("id").getValue() != null && copiar){
									item.getItemProperty("id").setValue(codProduto);
								}
								
								if(fornecedorSelecionado != null){
									item.getItemProperty("fornecedorId").setValue(fornecedorSelecionado);
								}
								if(ncmSelecionado != null){
									item.getItemProperty("ncm").setValue(ncmSelecionado.getCodigo().toString()); 
								}else{
									item.getItemProperty("ncm").setValue(null);
								}
								if(cestSelecionado != null){
									item.getItemProperty("cest").setValue(cestSelecionado.getCest().toString()); 
								}else{
									item.getItemProperty("cest").setValue(null);
								}
							
								item.getItemProperty("status").setValue(cbStatus.getValue().toString());
								item.getItemProperty("grupoId").setValue(grupoSelecionado);
								item.getItemProperty("marcasId").setValue(marcaSelecionado);
								item.getItemProperty("cst_origem").setValue(origemSelecionado);
								item.getItemProperty("cst_forma_tributo").setValue(cstSelecionado);
								item.getItemProperty("ecf_cup_filcal").setValue(cstecfSelecionado);
								item.getItemProperty("simples_nacional").setValue(csosnSelecionado);
								item.getItemProperty("cfop").setValue(cfopSelecionado);
								
								item.getItemProperty("referencia").setValue(tfRef.getValue());
								
								item.getItemProperty("outras_tb_desc").setValue(cbPermitirOutrasTabela.getValue().toString());
								
								
								item.getItemProperty("taxaIcms").setValue(new Float(17));
								
								if(item.getItemProperty("id").getValue() == null || copiar){
									item.getItemProperty("qtdEstoque").setValue(new Float(0));
								}
								
								if(txtDiferencaAliquota != null && txtDiferencaAliquota.getValue() != null){
									item.getItemProperty("diferenca_aliquota").setValue(Real.formatStringToDBFloat(txtDiferencaAliquota.getValue()));
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
								if(cbSituacaoTributaria != null && cbSituacaoTributaria.getValue() != null){
									item.getItemProperty("ecfIcmsSt").setValue(cbSituacaoTributaria.getValue().toString().substring(0, 1));
								}
								if(cbIat != null && cbIat.getValue() != null){
									item.getItemProperty("iat").setValue(cbIat.getValue().toString().substring(0, 1));
								}
								if(cbIppt != null && cbIppt.getValue() != null){
									item.getItemProperty("ippt").setValue(cbIppt.getValue().toString().substring(0, 1));
								}					
					
								if(txtIPI != null && txtIPI.getValue() != null){
									item.getItemProperty("taxaIpi").setValue(Real.formatStringToDBFloat(txtIPI.getValue()));
								}

								if(txtAliquotaIcms != null && txtAliquotaIcms.getValue() != null){
									item.getItemProperty("taxaIcms").setValue(Real.formatStringToDBFloat(txtAliquotaIcms.getValue()));
								}
				
								if(txtFrete != null && txtFrete.getValue() != null){
									item.getItemProperty("frete").setValue(Real.formatStringToDBFloat(txtFrete.getValue()));
								}
								if(txtCustoTotal != null && txtCustoTotal.getValue() != null){
									item.getItemProperty("custo_total").setValue(Real.formatStringToDBFloat(txtCustoTotal.getValue()));
								}
								
								if(txtValorCusto != null && txtValorCusto.getValue() != null){
									item.getItemProperty("valorCusto").setValue(Real.formatStringToDBFloat(txtValorCusto.getValue()));
								}
								
								if(txtPrecoPromocional != null && txtPrecoPromocional.getValue() != null){
									item.getItemProperty("preco_promocional").setValue(Real.formatStringToDBFloat(txtPrecoPromocional.getValue()));
								}
								
								if(txtLucro != null && txtLucro.getValue() != null){
									item.getItemProperty("lucro").setValue(Real.formatStringToDBFloat(txtLucro.getValue()));
								}
								if(txtMargemLucro != null && txtMargemLucro.getValue() != null){
									item.getItemProperty("margemLucro").setValue(Real.formatStringToDBFloat(txtMargemLucro.getValue()));
								}
								
								if(cbSicronizar != null && cbSicronizar.getValue() != null){
									
									if(cbSicronizar.getValue().toString().equals("SIM")){
										item.getItemProperty("sic_loja").setValue(true);
									}else{
										item.getItemProperty("sic_loja").setValue(false);
									}
								}
								
								if(cbProdutoAtivado != null && cbProdutoAtivado.getValue() != null){
									item.getItemProperty("produto_ativado").setValue(cbProdutoAtivado.getValue());
								}
								
								if(cbProdutoDestaque != null && cbProdutoDestaque.getValue() != null){
									item.getItemProperty("produto_destaque").setValue(cbProdutoDestaque.getValue());
								}
								
								if(txtUrlYoutube != null && txtUrlYoutube.getValue() != null){
									item.getItemProperty("url_video_youtube").setValue(txtUrlYoutube.getValue());
								}

								if(cbFracionar.getValue().toString().equals("SIM")){
									fracionar = 1;
								}else{
									fracionar = 0;
								}
								
								item.getItemProperty("fracionar").setValue(fracionar);
															
								if(item.getItemProperty("dataEstoque").getValue() == null || copiar){
									item.getItemProperty("dataEstoque").setValue(new Date());
								}
								item.getItemProperty("dataAlteracao").setValue(new Date());	
								
								item.getItemProperty("nome").setValue(item.getItemProperty("nome").getValue().toString().replaceAll("[|]", ""));
								
								fireEvent(new ProdutoEvent(getUI(), item,icms,ipi,pis,cofins, true));			
								tbPrincipal.getTab(1).setStyleName("tab-valid");
								
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
							
							
							
							if(fornecedorSelecionado == null){
								tfCodFornecedor.addStyleName("invalid-txt");
							}else{
								tfCodFornecedor.removeStyleName("invalid-txt");
							}
							
							if(grupoSelecionado == null){
								txtCodGrupo.addStyleName("invalid-txt");
							}else{
								txtCodGrupo.removeStyleName("invalid-txt");
							}
							
							if(ncmSelecionado == null){
								txtCodNcm.addStyleName("invalid-txt");
							}else{
								txtCodNcm.removeStyleName("invalid-txt");
							}
														
							boolean tributacao_invalid = false;
														
							if(origemSelecionado == null){
								tfCodOrigem.addStyleName("invalid-txt");
								tributacao_invalid = true;
							}else{
								tfCodOrigem.removeStyleName("invalid-txt");
							}
							
							if(cstSelecionado == null){
								tfCodCst.addStyleName("invalid-txt");
								tributacao_invalid = true;
								
							}else{
								tfCodCst.removeStyleName("invalid-txt");								
							}
							
							if(csosnSelecionado == null){
								tfCodCsosn.addStyleName("invalid-txt");
								tributacao_invalid = true;
							}else{
								tfCodCsosn.removeStyleName("invalid-txt");
							}
							
							if(cstecfSelecionado == null){
								tfCodCstEcf.addStyleName("invalid-txt");
								tributacao_invalid = true;
							}else{
								tfCodCstEcf.removeStyleName("invalid-txt");
							}
							
							if(!txtAliquotaIcms.isValid()){
								txtAliquotaIcms.addStyleName("invalid-txt");
								tributacao_invalid = true;
							}else{
								txtAliquotaIcms.removeStyleName("invalid-txt");
							}
							
							
							if(tributacao_invalid){
								tbPrincipal.getTab(1).setStyleName("tab-invalid");
							}else{
								tbPrincipal.getTab(1).setStyleName("tab-valid");
							}
							
							
							if(!txtValorCusto.isValid()){
								txtValorCusto.addStyleName("invalid-txt");								
							}else{
								txtValorCusto.removeStyleName("invalid-txt");
							}
							
							if(!txtPrecoInicVenda.isValid()){
								txtPrecoInicVenda.addStyleName("invalid-txt");
							}else{
								txtPrecoInicVenda.removeStyleName("invalid-txt");
							}
						
							if(!txtIPI.isValid()){
								txtIPI.addStyleName("invalid-txt");
							}else{								
								txtIPI.removeStyleName("invalid-txt");
							}
							
							if(!cbPermitirOutrasTabela.isValid()){
								cbPermitirOutrasTabela.addStyleName("invalid-txt");
							}else{
								cbPermitirOutrasTabela.removeStyleName("invalid-txt");
							}
							
							if(!txtFrete.isValid()){
								txtFrete.addStyleName("invalid-txt");
							}else{
								txtFrete.removeStyleName("invalid-txt");
							}
							
							if(!cbUtilizaSeriais.isValid()){
								cbUtilizaSeriais.addStyleName("invalid-txt");
							}else{
								cbUtilizaSeriais.removeStyleName("invalid-txt");
							}
							
							if(!txtDiferencaAliquota.isValid()){
								txtDiferencaAliquota.addStyleName("invalid-txt");
							}else{
								txtDiferencaAliquota.removeStyleName("invalid-txt");
							}
							
							if(!txtEstoqMin.isValid()){
								txtEstoqMin.addStyleName("invalid-txt");
							}else{
								txtEstoqMin.removeStyleName("invalid-txt");
							}
							
							if(valid_cod_barras){
								txtCodBarras.removeStyleName("invalid-txt");
							}else{
								txtCodBarras.addStyleName("invalid-txt");
							}
							
							if(!permitir_outras_tabelas){
								Notify.Show("Não é possível permitir outras tabelas! ", Notify.TYPE_ERROR);
							}
							
							Notify.Show_Invalid_Submit_Form();
						}
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
		btSalvar.setTabIndex(33);
		return btSalvar;
	}

	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new ProdutoEvent(getUI(), item, icms, ipi,pis,cofins, false));
					close();
				}else{
					final GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(fieldGroup.isValid() && valid_cod_barras){
									try {				
										
										fieldGroup.commit();
										item.getItemProperty("nome").setValue(item.getItemProperty("nome").getValue().toString().replaceAll("[|]", ""));
										fireEvent(new ProdutoEvent(getUI(), item, icms, ipi,pis, cofins, true));					
										
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
									
									if(valid_cod_barras){
										txtCodBarras.removeStyleName("invalid-txt");
									}else{
										txtCodBarras.addStyleName("invalid-txt");
									}
									
									
									Notify.Show_Invalid_Submit_Form();
								}
							}else{			
								fieldGroup.discard();				
								fireEvent(new ProdutoEvent(getUI(), item,icms,ipi,pis,cofins, false));
								gDialog.close();
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
		
		btCancelar.setTabIndex(34);
		return btCancelar;
	}

	
	
	public void addListerner(ProdutoListerner target){
		try {
			Method method = ProdutoListerner.class.getDeclaredMethod("onClose", ProdutoEvent.class);
			addListener(ProdutoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ProdutoListerner target){
		removeListener(ProdutoEvent.class, target);
	}
	public static class ProdutoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private Icms icms;
		private Ipi ipi;
		private Pis pis;
		private Cofins cofins;
		
		public ProdutoEvent(Component source, Item item,Icms icms, Ipi ipi, Pis pis,  Cofins cofins, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;
			this.icms = icms;
			this.ipi = ipi;
			this.pis = pis;
			this.cofins = cofins;
		}

		public Icms getIcms(){
			return icms;
		}
		public Ipi getIpi(){
			return ipi;
		}
		public Pis getPis(){
			return pis;
		}
		public Cofins getCofins(){
			return cofins;
		}
		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ProdutoListerner extends Serializable{
		public void onClose(ProdutoEvent event);
	}
	
	File file;
	Image img;
	String nameImagemUploaded;
	public class ImageUploader implements Receiver, SucceededListener{
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			
			FileOutputStream fos = null;
			String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();	
			try {
				
				file = new File(basepath + "/WEB-INF/uploads/" + filename);
				fos = new FileOutputStream(file);
			
				
			} catch (FileNotFoundException e) {
				new Notification("O arquivo nao pode ser aberto! ", e.getMessage(),
						Notification.Type.ERROR_MESSAGE)
				.show(Page.getCurrent());
				return null;
			}
			
			return fos;
		}
		
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			img.setSource(new FileResource(file));
			nameImagemUploaded = event.getFilename();
		}
		
	}
	
	public byte[] getByteArray(File file) throws IOException
	{
	  //Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
	  if(file != null){
		  
		  InputStream is = new FileInputStream(file);
		  // Get the size of the file
		  long length = file.length();
		  // You cannot create an array using a long type.
		  // It needs to be an int type.
		  // Before converting to an int type, check
		  // to ensure that file is not larger than Integer.MAX_VALUE.
		  if (length > Integer.MAX_VALUE)
		  {
			  // File is too large
		  }
		  // Create the byte array to hold the data
		  byte[] bytes = new byte[(int) length];
		  // Read in the bytes
		  int offset = 0;
		  int numRead = 0;
		  while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
		  {
			  offset += numRead;
		  }
		  // Ensure all the bytes have been read in
		  if (offset < bytes.length)
		  {
			  throw new IOException("Could not completely read file " + file.getName());
		  }
		  // Close the input stream and return bytes
		  is.close();
		  return bytes;
	  }
	return null;
	}
	
	public void writeFile(File file, byte[] data) throws IOException
	 {
	   OutputStream fo = new FileOutputStream(file);
	   // Write the data
	   fo.write(data);
	   // flush the file (down the toilet)
	   fo.flush();
	   // Close the door to keep the smell in.
	   fo.close();
	 }
}
