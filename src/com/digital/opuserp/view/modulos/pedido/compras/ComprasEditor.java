package com.digital.opuserp.view.modulos.pedido.compras;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.NaturezaOperacao;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.pedido.compras.ProdutoFastEditor.ProdutoFastEditorEvent;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.QtdEditor;
import com.digital.opuserp.view.modulos.pedido.pedidoProduto.QtdEditor.QtdEvent;
import com.digital.opuserp.view.util.FornecedorUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ProdutoUtil;
import com.digital.opuserp.view.util.ProdutoUtil.ProdutoEvent;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class ComprasEditor extends Window implements GenericEditor {

	Item item;
	
	private Button btSalvar;
	private Button btFinalizar;
	private Button btCancelar;
	private Button btFechar;
	
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private TextField txtDescricaoFornecedor;
	private TextField txtCodFornecedor;	
	private Fornecedor FornecedorSelecionado;	
	private TextField txtValorTotal;
	private DateField dfDataValidade;
	
	private Table tbItens;
	private Table tbItensProduto;
	private TextField txtNotaFiscal;
	private TextField txtValorTotalNota;
	private TextArea txtObservacao;
	
	private ComboBox cbTipoDoc;
	private ComboBox cbNaturezaOperacao;
	
	boolean visualizar;
	public ComprasEditor(final Item item, String title, boolean modal, final boolean visualizar){
		
		setWidth("1153px");		
		this.item = item;
		this.visualizar = visualizar;
		
		setCaption(title);
		setModal(modal);
		setResizable(true);
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
				
				
				if(!visualizar){
					//hlButtons.addComponent(buildBtSave());
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtSalvar());
				}else{
					hlButtons.addComponent(buildBtFechar());
				}
				
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
			if(visualizar){
				buildLayoutVisualizar();
				setHeight("344px");				
			}else{
				
				vlRoot.addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						TextField txtOs = new TextField("Número");
						txtOs.setNullRepresentation("");
						txtOs.setStyleName("caption-align-pedido");
						
						addComponent(txtOs);
						
						//fieldGroup.bind(txtOs, "id");
						
						if(item.getItemProperty("id").getValue() != null){
							txtOs.setValue(item.getItemProperty("id").getValue().toString());
						}else{
							txtOs.setValue(CompraDAO.getNextID());
						}
						txtOs.setReadOnly(true);
					}
				});
				
			
				
				vlRoot.addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						cbTipoDoc = new ComboBox("Tipo do documento");
						cbTipoDoc.setRequired(true);
						cbTipoDoc.setNullSelectionAllowed(false);
						cbTipoDoc.setTextInputAllowed(false);
						cbTipoDoc.addItem("Entrada");
						
						cbTipoDoc.setStyleName("caption-align-pedido");
						
						cbTipoDoc.select("Entrada");
						
						addComponent(cbTipoDoc); 
						//fieldGroup.bind(cbTipoDoc, "tipo_documento");
						
					}
				});
				
				vlRoot.addComponent(new FormLayout(){
					{
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");
						
						JPAContainer<NaturezaOperacao> containerNatureza = JPAContainerFactory.makeReadOnly(NaturezaOperacao.class, ConnUtil.getEntity());
						containerNatureza.addContainerFilter(Filters.eq("tipo", "ENTRADA"));
						
						cbNaturezaOperacao = new ComboBox("Natureza da operação",containerNatureza);
						cbNaturezaOperacao.setItemCaptionPropertyId("descricao");
						cbNaturezaOperacao.setRequired(true);
						cbNaturezaOperacao.setNullSelectionAllowed(false);
						cbNaturezaOperacao.setTextInputAllowed(false);						
						cbNaturezaOperacao.setStyleName("caption-align-pedido");
						
						
						Collection<?> itens =cbNaturezaOperacao.getItemIds();
						for (Object object : itens) {
							EntityItem<NaturezaOperacao> entityItem = (EntityItem<NaturezaOperacao>)cbNaturezaOperacao.getItem(object);
							if(entityItem.getEntity().getDescricao().equals("COMPRA PRA COMERCIALIZACAO")){
								cbNaturezaOperacao.select(object);								
							}
						}
						
												
						addComponent(cbNaturezaOperacao); 
						
						
						
						//fieldGroup.bind(cbNaturezaOperacao, "natureza_operacao");
					}
				});

				
				vlRoot.addComponent(new FormLayout(){
					{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
						
						final Upload xmlUploaded = new Upload("Arquivo XML",null);
						xmlUploaded.setButtonCaption("Upload");								
												
						
						XmlUploader xmlUpload = new XmlUploader();						
						xmlUploaded.setReceiver(xmlUpload);
						xmlUploaded.addSucceededListener(xmlUpload);
						xmlUploaded.setErrorHandler(new ErrorHandler() {
							
							
							@Override
							public void error(com.vaadin.server.ErrorEvent event) {
								//System.out.println("Nenhum arquivo"event.toString());
								Notify.Show("Selecione um arquivo", Notify.TYPE_WARNING);
							}
						});
						
						
						xmlUploaded.setStyleName("caption-align-pedido");
						
						addComponent(xmlUploaded);
					}
				});
				
				//		
				vlRoot.addComponent(new HorizontalLayout(){
							{
												
								boolean preencher = false;
								if(item.getItemProperty("id").getValue() != null && item.getItemProperty("fornecedor").getValue() != null){
									preencher = true;
								}
								
								txtCodFornecedor = new TextField("Fornecedor");				
								txtCodFornecedor.setWidth("60px");				
								txtCodFornecedor.setNullRepresentation("");
								txtCodFornecedor.setStyleName("caption-align-pedido");
								txtCodFornecedor.setId("txtCodFornecedor");
								txtCodFornecedor.focus();
								
								JavaScript.getCurrent().execute("$('#txtCodFornecedor').mask('0000000000')");
								txtCodFornecedor.setImmediate(true);
										
								
								
								txtCodFornecedor.addListener(new TextChangeListener() {
									
									@Override
									public void textChange(TextChangeEvent event) {
										
										FornecedorSelecionado = new Fornecedor();
										
										if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
																													
											FornecedorSelecionado = FornecedorDAO.findFornecedor(Integer.parseInt(event.getText()));		
											
											if(FornecedorSelecionado != null){
												txtDescricaoFornecedor.setReadOnly(false);
												txtDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
												txtDescricaoFornecedor.setReadOnly(true);												
											}else {
												txtDescricaoFornecedor.setReadOnly(false);
												txtDescricaoFornecedor.setValue("");
												txtDescricaoFornecedor.setReadOnly(true);																			
											}
										}else{
											txtDescricaoFornecedor.setReadOnly(false);
											txtDescricaoFornecedor.setValue("");
											txtDescricaoFornecedor.setReadOnly(true);							
										}
									}
								});

								txtCodFornecedor.setRequired(true);		
								txtDescricaoFornecedor = new TextField();
								txtDescricaoFornecedor.setTabIndex(2000);
								txtDescricaoFornecedor.setReadOnly(true);
								txtDescricaoFornecedor.setWidth("592px");
													
								if(item.getItemProperty("fornecedor") != null && item.getItemProperty("fornecedor").getValue() != null){
									FornecedorSelecionado = (Fornecedor)item.getItemProperty("fornecedor").getValue();				
								}
										
								final Button btSearchFornecedor = new Button();
								btSearchFornecedor.setStyleName(BaseTheme.BUTTON_LINK);
								btSearchFornecedor.setIcon(new ThemeResource("icons/search-16.png"));
								btSearchFornecedor.setTabIndex(300000);
								
								if(item.getItemProperty("fornecedor").getValue() != null){
									btSearchFornecedor.setEnabled(false);
								}
								btSearchFornecedor.addClickListener(new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										FornecedorUtil cUtil = new FornecedorUtil(true, true, true);
										cUtil.addListerner(new FornecedorUtil.FornecedorListerner() {
											
											@Override
											public void onSelected(
													FornecedorUtil.FornecedorEvent event) {
													if(event.getFornecedor() != null){
														txtCodFornecedor.setValue(event.getFornecedor().getId().toString());
														txtDescricaoFornecedor.setReadOnly(false);
														txtDescricaoFornecedor.setValue(event.getFornecedor().getRazao_social());
														txtDescricaoFornecedor.setReadOnly(true);
														FornecedorSelecionado = event.getFornecedor();														
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
										setStyleName("form-cutom-new");		
																
										addComponent(txtCodFornecedor);							
									}
								};
								addComponent(frmCodigoFornecedor);
						
								FormLayout frmButtonSearchFornecedor =new FormLayout(){
									{
										
										setMargin(true);
										setSpacing(true);
										setStyleName("form-cutom-new_hide_error_cell");										
										addComponent(btSearchFornecedor);							
									}
								}; 
											
								FormLayout frmDescFornecedor = new FormLayout(){
									{
										
										setMargin(true);
										setSpacing(true);						
										setStyleName("form-cutom-new");		
										addStyleName("form-cutom-new_hide_require");
										
										addComponent(txtDescricaoFornecedor);							
									}
								}; 
								addComponent(frmButtonSearchFornecedor);
								addComponent(frmDescFornecedor);
								setExpandRatio(frmDescFornecedor, 1);		
								
								
								if(txtCodFornecedor != null && FornecedorSelecionado != null){
									txtCodFornecedor.setValue(FornecedorSelecionado.getId().toString());
									txtCodFornecedor.setReadOnly(true);
								}
								
								if(txtDescricaoFornecedor != null && FornecedorSelecionado != null){
									txtDescricaoFornecedor.setReadOnly(false);
									txtDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
									txtDescricaoFornecedor.setReadOnly(true);
								}

							}	
						});

						vlRoot.addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								txtNotaFiscal = new TextField("Nota Fiscal");
								txtNotaFiscal.setNullRepresentation("");
								txtNotaFiscal.setStyleName("caption-align-pedido");
								txtNotaFiscal.setRequired(true);
								
								addComponent(txtNotaFiscal);								
//								fieldGroup.bind(txtNotaFiscal, "id");								
							}
						});
						
						vlRoot.addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								txtValorTotalNota = new TextField("Valor total da nota");
								txtValorTotalNota.setNullRepresentation("");
								txtValorTotalNota.setStyleName("caption-align-pedido");
								txtValorTotalNota.setRequired(true);
								
								addComponent(txtValorTotalNota);								
							}
						});
						vlRoot.addComponent(new FormLayout(){
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
								
								dfDataValidade = new DateField("Data de Validade");								
								dfDataValidade.setStyleName("caption-align-pedido");
															
								addComponent(dfDataValidade);								
							}
						});
						
						vlRoot.addComponent(new VerticalLayout(){
							{
								tbItensProduto = new Table();	
								tbItensProduto.addStyleName("tabela_compras");
								tbItensProduto.setSelectable(true);
								tbItensProduto.setColumnCollapsingAllowed(true);
								tbItensProduto.addContainerProperty("Código", String.class, "");
								tbItensProduto.addContainerProperty("Produto", String.class, "");
								tbItensProduto.addContainerProperty("Nome Produto", String.class, "");
								tbItensProduto.addContainerProperty("Qtd Deposito Anterior", String.class, "");
								tbItensProduto.addContainerProperty("Qtd Loja Anterior", String.class, "");
								tbItensProduto.addContainerProperty("Qtd", String.class, "");
								tbItensProduto.addContainerProperty("Valor Custo", String.class, "");
								tbItensProduto.addContainerProperty("Valor Total", String.class, "");
								tbItensProduto.addContainerProperty("Ipi", String.class, "");
								tbItensProduto.addContainerProperty("Aliq", String.class, "");
								tbItensProduto.addContainerProperty("CEST", String.class, "");
								tbItensProduto.addContainerProperty("Código de Barras", String.class, "");								
								
								tbItensProduto.setColumnCollapsed("Ipi", true);
								tbItensProduto.setColumnCollapsed("Aliq", true);
								tbItensProduto.setColumnCollapsed("CEST", true);
								tbItensProduto.setColumnCollapsed("Código de Barras", true);
								
								tbItensProduto.setColumnAlignment("Valor Custo", Align.RIGHT);
								tbItensProduto.setColumnAlignment("Valor Total", Align.RIGHT);
								
								tbItensProduto.setSizeFull();								
								tbItensProduto.addGeneratedColumn("--", new Table.ColumnGenerator() {
									
									@Override
									public Object generateCell(final Table source, final Object itemId, Object columnId) {

										
									
										if(source.getItem(itemId).getItemProperty("Nome Produto").getValue() != null){	
											//Label lb = new Label("Vinculado");
											
											Button bt1 = new Button("Buscar", new Button.ClickListener() {
												
												@Override
												public void buttonClick(ClickEvent event) {
													ProdutoUtil produtoUtil = new ProdutoUtil(true, true);
													produtoUtil.addListerner(new ProdutoUtil.ProdutoListerner() {
														
														@Override
														public void onSelected(ProdutoEvent event) {
															if(event.getProduto() != null){
																Produto p = event.getProduto();
																String codBarras = source.getItem(itemId).getItemProperty("Código de Barras").getValue().toString();
																
																if(!codBarras.equals("SEM GTIN")){
																	p.setgTin(codBarras);																
																	ProdutoDAO.alterar(p);
																}else{
																	source.getItem(itemId).getItemProperty("Código de Barras").setValue(p.getgTin());
																}
																
																ProdutoDAO.limpaCodBarrasDuplicado(codBarras,p);		
																
																source.getItem(itemId).getItemProperty("Nome Produto").setValue(p.getNome());
																source.getItem(itemId).getItemProperty("Qtd Deposito Anterior").setValue(p.getQtdEstoqueDeposito() != null ? p.getQtdEstoqueDeposito().toString() : "");
																source.getItem(itemId).getItemProperty("Qtd Loja Anterior").setValue(p.getQtdEstoque() != null ? p.getQtdEstoque().toString() : "");
																
																Container container = source.getContainerDataSource();
																source.setContainerDataSource(null);
																source.setContainerDataSource(container); 
																																																
																source.setColumnCollapsed("Ipi", true);
																source.setColumnCollapsed("Aliq", true);
																source.setColumnCollapsed("Código de Barras", true);
																
																Notify.Show("Produto vinculado com sucesso!", Notify.TYPE_SUCCESS);																
																
																p.setValorCusto(Real.formatStringToDBFloat(source.getItem(itemId).getItemProperty("Valor Custo").getValue().toString().replace("R$ ", "")));
																ConnUtil.getEntity().getTransaction().begin();
																ProdutoDAO.saveProduto(p);
																ConnUtil.getEntity().getTransaction().commit();
																
																final BeanItem<Produto> itemProduto = new BeanItem<Produto>(p);
																final ProdutoFastEditor produtoFastEditor = new ProdutoFastEditor(itemProduto, "Editar Produto",true);
																produtoFastEditor.addListerner(new ProdutoFastEditor.ProdutoFastEditorListerner() {
																	
																	@Override
																	public void onClose(ProdutoFastEditorEvent event) {
																		if(event.isConfirm()){
																			
																			Produto pNovo = itemProduto.getBean();
																			Float qtd = new Float(source.getItem(itemId).getItemProperty("Qtd").getValue().toString());
																			//pNovo.setQtdEstoque(pNovo.getQtdEstoque()+qtd);
																			pNovo.setFornecedorId(FornecedorSelecionado);																
																			ConnUtil.getEntity().getTransaction().begin();
																			ProdutoDAO.saveProduto(pNovo);
																			ConnUtil.getEntity().getTransaction().commit();
																			
																			produtoFastEditor.close();
																		}
																	}
																});
															
																getUI().addWindow(produtoFastEditor); 
															}
														}
													});
													
													getUI().addWindow(produtoUtil);		
												}
											});											
											bt1.setReadOnly(false);
											bt1.setStyleName(Reindeer.BUTTON_LINK);
											
											
											Button bt = bt = new Button("Alterar", new Button.ClickListener() {
												
												@Override
												public void buttonClick(ClickEvent event) {
													String codBarras = source.getItem(itemId).getItemProperty("Código de Barras").getValue().toString();
													Produto p = ProdutoDAO.findByCodBarras(codBarras);
													
													if(p != null){
														p.setTaxaIpi(new Float(Real.formatStringToDBDouble(source.getItem(itemId).getItemProperty("Ipi").getValue().toString())));														
														p.setDiferenca_aliquota(new Float(Real.formatStringToDBDouble(source.getItem(itemId).getItemProperty("Aliq").getValue().toString()))); 
													}
													
													Item item = source.getItem(itemId);//
													
													if(p != null && itemId != null && item.getItemProperty("Valor Custo") != null && item.getItemProperty("Valor Custo").getValue() != null){
														p.setValorCusto(Real.formatStringToDBFloat(item.getItemProperty("Valor Custo").getValue().toString().replace("R$ ", "")));
													}
													
													if(item.getItemProperty("CEST") != null && item.getItemProperty("CEST").getValue() != null && p != null){
														p.setCest(item.getItemProperty("CEST").getValue().toString());
													}
																									
													final BeanItem<Produto> itemProduto = new BeanItem<Produto>(p != null ? p : new Produto());
													final ProdutoFastEditor produtoFastEditor = new ProdutoFastEditor(itemProduto, "Editar Produto",true);
													produtoFastEditor.addListerner(new ProdutoFastEditor.ProdutoFastEditorListerner() {
														
														@Override
														public void onClose(ProdutoFastEditorEvent event) {
															if(event.isConfirm()){
																
																Produto pNovo = itemProduto.getBean();
																Float qtd = new Float(source.getItem(itemId).getItemProperty("Qtd").getValue().toString());
																pNovo.setFornecedorId(FornecedorSelecionado);
																
																ConnUtil.getEntity().getTransaction().begin();
																ProdutoDAO.saveProduto(pNovo);
																ConnUtil.getEntity().getTransaction().commit();
																
																produtoFastEditor.close();
															}
														}
													});
												
													getUI().addWindow(produtoFastEditor); 
													
												}
											});
											bt.setStyleName(Reindeer.BUTTON_LINK);

											HorizontalLayout hl = new HorizontalLayout();
											hl.setSpacing(true);
											//hl.addComponent(lb);
											hl.addComponent(bt1);
											hl.addComponent(bt);
											
											return hl;
										}else{
											HorizontalLayout hl = new HorizontalLayout();
											hl.setSpacing(true);
											
											Button bt = bt = new Button("Buscar", new Button.ClickListener() {
												
												@Override
												public void buttonClick(ClickEvent event) {
													ProdutoUtil produtoUtil = new ProdutoUtil(true, true);
													produtoUtil.addListerner(new ProdutoUtil.ProdutoListerner() {
														
														@Override
														public void onSelected(ProdutoEvent event) {
															if(event.getProduto() != null){
																Produto p = event.getProduto();
																String codBarras = source.getItem(itemId).getItemProperty("Código de Barras").getValue().toString();
																
																
																
																if(!codBarras.equals("SEM GTIN")){
																	p.setgTin(codBarras);
																	ProdutoDAO.alterar(p);
																}else{
																	source.getItem(itemId).getItemProperty("Código de Barras").setValue(p.getgTin());
																}
																
																
																
																
																ProdutoDAO.limpaCodBarrasDuplicado(codBarras,p);														
																
																source.getItem(itemId).getItemProperty("Nome Produto").setValue(p.getNome());
																source.getItem(itemId).getItemProperty("Qtd Deposito Anterior").setValue(p.getQtdEstoqueDeposito() != null ? p.getQtdEstoqueDeposito().toString() : "0");
																source.getItem(itemId).getItemProperty("Qtd Loja Anterior").setValue(p.getQtdEstoque() != null ? p.getQtdEstoque().toString() : "0");
																
																Container container = source.getContainerDataSource();
																source.setContainerDataSource(null);
																source.setContainerDataSource(container); 
																
																source.setColumnCollapsed("Ipi", true);
																source.setColumnCollapsed("Aliq", true);
																source.setColumnCollapsed("Código de Barras", true);
																
																Notify.Show("Produto vinculado com sucesso!", Notify.TYPE_SUCCESS);																
																
															}
														}
													});
													
													getUI().addWindow(produtoUtil);		
												}
											});											
											bt.setReadOnly(false);
											bt.setStyleName(Reindeer.BUTTON_LINK);
											
											Button btCadastrar = new Button("Cadastrar", new Button.ClickListener() {
												
												@Override
												public void buttonClick(ClickEvent event) {
													Produto p =  new Produto();
													
													String gTin = source.getItem(itemId).getItemProperty("Código de Barras").getValue().toString();
													
													if(gTin.equals("SEM GTIN")){
														gTin = source.getItem(itemId).getItemProperty("Código").getValue().toString();
													}
													
													
													p.setgTin(gTin);
													p.setNome(source.getItem(itemId).getItemProperty("Produto").getValue().toString());
													p.setValorCusto(new Float(Real.formatStringToDB(source.getItem(itemId).getItemProperty("Valor Custo").getValue().toString().replace("R$ ", ""))));
													p.setTaxaIpi(new Float(Real.formatStringToDBDouble(source.getItem(itemId).getItemProperty("Ipi").getValue().toString())));
													p.setDiferenca_aliquota(new Float(Real.formatStringToDBDouble(source.getItem(itemId).getItemProperty("Aliq").getValue().toString())));
													p.setFornecedorId(FornecedorSelecionado);
													p.setEmpresaId(OpusERP4UI.getEmpresa());
													p.setStatus("EM DIGITACAO");
													p.setDataAlteracao(new Date());
													
													ConnUtil.getEntity().getTransaction().begin();
													ProdutoDAO.saveProduto(p);
													ConnUtil.getEntity().getTransaction().commit();
													
													source.getItem(itemId).getItemProperty("Nome Produto").setValue(p.getNome());
													
													Container container = source.getContainerDataSource();
													source.setContainerDataSource(null);
													source.setContainerDataSource(container);
													
													source.setColumnCollapsed("Ipi", true);
													source.setColumnCollapsed("Aliq", true);
													source.setColumnCollapsed("Código de Barras", true);
													
													Notify.Show("Produto cadastrado com sucesso!", Notify.TYPE_SUCCESS);
												}
											});
											btCadastrar.setStyleName(Reindeer.BUTTON_LINK);
											
											hl.addComponent(bt);
											hl.addComponent(btCadastrar);
																						
											return hl;
										}
																				
									}
								});

								tbItensProduto.setCellStyleGenerator(new Table.CellStyleGenerator() {
									
									@Override
									public String getStyle(Table source, Object itemId, Object propertyId) {
										if(source.getItem(itemId).getItemProperty("Nome Produto").getValue() != null){
											return "row-header-compras_verde";
										}else{
											return "row-header-compras_normal";
										}
									}
								});
								
								final Button btAdicionarItem = new Button("Adicionar Item", new Button.ClickListener() {
									
									@Override
									public void buttonClick(ClickEvent event) {
										ProdutoUtil produtoUtil = new ProdutoUtil(true, true);
										produtoUtil.addListerner(new ProdutoUtil.ProdutoListerner() {
											
											@Override
											public void onSelected(final ProdutoEvent event1) {
												if(event1.getProduto() != null){
													
													QtdEditor qtdEditor = new QtdEditor("Quantidade", true, event1.getProduto().getFracionar() == 1 ? true : false, null,true);
													qtdEditor.addListerner(new QtdEditor.QtdListerner() {
														
														@Override
														public void onClose(final QtdEvent event2) {
															if(event2.isConfirm()){
																
																
																final Produto p = event1.getProduto();
																
																final BeanItem<Produto> itemProduto = new BeanItem<Produto>(p);
																final ProdutoFastEditor produtoFastEditor = new ProdutoFastEditor(itemProduto, "Editar Produto",true);
																produtoFastEditor.addListerner(new ProdutoFastEditor.ProdutoFastEditorListerner() {
																	
																	@Override
																	public void onClose(ProdutoFastEditorEvent event) {
																		if(event.isConfirm()){
																			BeanItem<Produto> bItemP = (BeanItem<Produto>)event.getItem();
																			Produto p = bItemP.getBean();
																			ProdutoDAO.alterar(p); 
																			tbItensProduto.addItem(new Object[]{p.getId().toString(),"",p.getNome(),p.getQtdEstoqueDeposito().toString(),p.getQtdEstoque().toString(),event2.getQtd().toString(),"R$ "+Real.formatDbToString(String.valueOf(p.getValorCusto())),
																					"R$ "+Real.formatDbToString(String.valueOf(p.getValorVenda())),"0","0",p.getCest(),p.getgTin()}, tbItensProduto.getItemIds().size());

																			System.out.println("Quantidade de Itens: "+tbItensProduto.getItemIds().size());
																			produtoFastEditor.close();
																		}
																	}
																});
															
																getUI().addWindow(produtoFastEditor); 
																
															}
														}
													});
													
													getUI().addWindow(qtdEditor); 
												}
											}
										});
										
										getUI().addWindow(produtoUtil);		
									}
								});
								btAdicionarItem.setStyleName(Reindeer.BUTTON_SMALL);
								
								
								
								addComponent(new HorizontalLayout(){
									{									
										setWidth("100%");
										addComponent(btAdicionarItem); 
										setComponentAlignment(btAdicionarItem, Alignment.MIDDLE_RIGHT);
									}
								}); 
								addComponent(tbItensProduto);
								
								
							}
						});

			}
		
		
	}
	
	boolean novo = false;
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new HorizontalLayout(){{addComponent(new FormLayout(){					
					    {
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
					
							TextField txtOs = new TextField("Número");
							txtOs.setNullRepresentation("");
							txtOs.setStyleName("caption-align-pedido");
							
							addComponent(txtOs);
							
							//fieldGroup.bind(txtOs, "id");
							
							if(item.getItemProperty("id").getValue() != null){
								txtOs.setValue(item.getItemProperty("id").getValue().toString());
							}else{
								txtOs.setValue(CompraDAO.getNextID());
							}
							txtOs.setReadOnly(true);													
					    }
					});
					
					addComponent(new FormLayout(){					
						    {
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
						
								TextField txtAbertura = new TextField("Abertura");
								txtAbertura.setStyleName("caption-align-pedido-abertura");
								txtAbertura.setNullRepresentation("");
								addComponent(txtAbertura);
								
								SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
								
								if(item.getItemProperty("id").getValue() != null){
									Date dataPv = (Date)item.getItemProperty("dataHora").getValue();
																		
									txtAbertura.setValue(sdf1.format(dataPv));
									txtAbertura.setReadOnly(true);
								}else{
									txtAbertura.setValue(sdf1.format(new Date()));
									txtAbertura.setReadOnly(true);
								}
						    }
						});
				}
		});
			
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("fornecedor").getValue() != null){
					preencher = true;
				}
				
				txtCodFornecedor = new TextField("Fornecedor");				
				txtCodFornecedor.setWidth("60px");				
				txtCodFornecedor.setNullRepresentation("");
				txtCodFornecedor.setStyleName("caption-align-pedido");
				txtCodFornecedor.setId("txtCodFornecedor");
				txtCodFornecedor.focus();
				
				JavaScript.getCurrent().execute("$('#txtCodFornecedor').mask('0000000000')");
				txtCodFornecedor.setImmediate(true);
						
				
				
				txtCodFornecedor.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						FornecedorSelecionado = new Fornecedor();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							FornecedorSelecionado = FornecedorDAO.findFornecedor(Integer.parseInt(event.getText()));		
							
							if(FornecedorSelecionado != null){
								txtDescricaoFornecedor.setReadOnly(false);
								txtDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
								txtDescricaoFornecedor.setReadOnly(true);
								
							}else {
								txtDescricaoFornecedor.setReadOnly(false);
								txtDescricaoFornecedor.setValue("");
								txtDescricaoFornecedor.setReadOnly(true);
															
							}
						}else{
							txtDescricaoFornecedor.setReadOnly(false);
							txtDescricaoFornecedor.setValue("");
							txtDescricaoFornecedor.setReadOnly(true);							
						}
					}
				});

				txtCodFornecedor.setRequired(true);		
				txtDescricaoFornecedor = new TextField();
				txtDescricaoFornecedor.setTabIndex(2000);
				txtDescricaoFornecedor.setReadOnly(true);
				txtDescricaoFornecedor.setWidth("592px");
									
				if(item.getItemProperty("fornecedor") != null && item.getItemProperty("fornecedor").getValue() != null){
					FornecedorSelecionado = (Fornecedor)item.getItemProperty("fornecedor").getValue();				
				}
						
				final Button btSearchFornecedor = new Button();
				btSearchFornecedor.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchFornecedor.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchFornecedor.setTabIndex(300000);
				
				if(item.getItemProperty("fornecedor").getValue() != null){
					btSearchFornecedor.setEnabled(false);
				}
				btSearchFornecedor.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						FornecedorUtil cUtil = new FornecedorUtil(true, true, true);
						cUtil.addListerner(new FornecedorUtil.FornecedorListerner() {
							
							@Override
							public void onSelected(
									FornecedorUtil.FornecedorEvent event) {
									if(event.getFornecedor() != null){
										txtCodFornecedor.setValue(event.getFornecedor().getId().toString());
										txtDescricaoFornecedor.setReadOnly(false);
										txtDescricaoFornecedor.setValue(event.getFornecedor().getRazao_social());
										txtDescricaoFornecedor.setReadOnly(true);
										FornecedorSelecionado = event.getFornecedor();
										
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
												
						addComponent(txtCodFornecedor);							
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
						
						addComponent(txtDescricaoFornecedor);							
					}
				}; 
				addComponent(frmButtonSearchFornecedor);
				addComponent(frmDescFornecedor);
				setExpandRatio(frmDescFornecedor, 1);		
				
				
				if(txtCodFornecedor != null && FornecedorSelecionado != null){
					txtCodFornecedor.setValue(FornecedorSelecionado.getId().toString());
					txtCodFornecedor.setReadOnly(true);
				}
				
				if(txtDescricaoFornecedor != null && FornecedorSelecionado != null){
					txtDescricaoFornecedor.setReadOnly(false);
					txtDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
					txtDescricaoFornecedor.setReadOnly(true);
				}

			}	
		});
		
		vlRoot.addComponent(new FormLayout(){					
			{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
						
					TextField txtCodNf = new TextField("Nota Fiscal");
					txtCodNf.setNullRepresentation("");
					txtCodNf.setRequired(true);					
					txtCodNf.setStyleName("caption-align-pedido");
					
					addComponent(txtCodNf);	
					fieldGroup.bind(txtCodNf, "codNf");
					
					
			}
	   });
		
		vlRoot.addComponent(new FormLayout(){					
				{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						txtValorTotal = new TextField("Valor Total dos Produtos (R$)");
						txtValorTotal.setNullRepresentation("");
						txtValorTotal.setRequired(true);					
						txtValorTotal.setStyleName("caption-align-pedido");
						txtValorTotal.addStyleName("align-currency");
						txtValorTotal.setId("txtValorTotal");
						
						addComponent(txtValorTotal);	
						JavaScript.getCurrent().execute("$('#txtValorTotal').maskMoney({decimal:',',thousands:'.'})");
						
						
						if(item.getItemProperty("valorTotal").getValue() != null){
							txtValorTotal.setValue(Real.formatDbToString(item.getItemProperty("valorTotal").getValue().toString()));
						}
						
						
						txtValorTotal.setReadOnly(true);
						
				}
		   });
		
		
		
	}

	
	
	public void buildLayoutVisualizar(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
			new HorizontalLayout(){
				{
					addComponent(
						new FormLayout(){					
					    {
							setStyleName("form-cutom");
							setMargin(true);
							setSpacing(true);
					
							TextField txtOs = new TextField("Compra Número");
							txtOs.setStyleName("caption-align-pedido");
							addComponent(txtOs);
							
							if(item.getItemProperty("id").getValue() != null){
								txtOs.setValue(item.getItemProperty("id").getValue().toString());
								txtOs.setReadOnly(true);
							}
					    }
					});
					
					addComponent(
							new FormLayout(){					
						    {
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
						
								TextField txtAbertura = new TextField("Abertura");
								txtAbertura.setStyleName("caption-align-pedido-abertura");
								addComponent(txtAbertura);
								
								SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
								
								if(item.getItemProperty("id").getValue() != null){
									Date dataPv = (Date)item.getItemProperty("dataHora").getValue();
									
									txtAbertura.setValue(sdf1.format(dataPv));
									txtAbertura.setReadOnly(true);
								}
						    }
						});
				}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
								
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("fornecedor").getValue() != null){
					preencher = true;
				}
				
				txtCodFornecedor = new TextField("Fornecedor");				
				txtCodFornecedor.setWidth("60px");				
				txtCodFornecedor.setNullRepresentation("");
				txtCodFornecedor.setStyleName("caption-align-pedido");
				txtCodFornecedor.setId("txtCodFornecedor");
				
				JavaScript.getCurrent().execute("$('#txtCodFornecedor').mask('0000000000')");
				txtCodFornecedor.setImmediate(true);
						
				
				
				txtCodFornecedor.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						
						FornecedorSelecionado = new Fornecedor();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							FornecedorSelecionado = FornecedorDAO.find(Integer.parseInt(event.getText()));		
							
							if(FornecedorSelecionado != null){
								txtDescricaoFornecedor.setReadOnly(false);
								txtDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
								txtDescricaoFornecedor.setReadOnly(true);
								
							}else {
								txtDescricaoFornecedor.setReadOnly(false);
								txtDescricaoFornecedor.setValue("");
								txtDescricaoFornecedor.setReadOnly(true);
															
							}
						}else{
							txtDescricaoFornecedor.setReadOnly(false);
							txtDescricaoFornecedor.setValue("");
							txtDescricaoFornecedor.setReadOnly(true);							
						}
					}
				});

				txtCodFornecedor.setRequired(true);		
				txtDescricaoFornecedor = new TextField();
				txtDescricaoFornecedor.setTabIndex(2000);
				txtDescricaoFornecedor.setReadOnly(true);
				txtDescricaoFornecedor.setWidth("592px");
									
				if(item.getItemProperty("fornecedor") != null && item.getItemProperty("fornecedor").getValue() != null){
					FornecedorSelecionado = (Fornecedor)item.getItemProperty("fornecedor").getValue();				
				}
						
				final Button btSearchFornecedor = new Button();
				btSearchFornecedor.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchFornecedor.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchFornecedor.setTabIndex(300000);
				btSearchFornecedor.setEnabled(false);
				btSearchFornecedor.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						FornecedorUtil cUtil = new FornecedorUtil(true, true);
						cUtil.addListerner(new FornecedorUtil.FornecedorListerner() {
							
							@Override
							public void onSelected(
									FornecedorUtil.FornecedorEvent event) {
									if(event.getFornecedor() != null){
										txtCodFornecedor.setValue(event.getFornecedor().getId().toString());
										txtDescricaoFornecedor.setReadOnly(false);
										txtDescricaoFornecedor.setValue(event.getFornecedor().getRazao_social());
										txtDescricaoFornecedor.setReadOnly(true);
										FornecedorSelecionado = event.getFornecedor();
										
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
												
						addComponent(txtCodFornecedor);							
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
						
						addComponent(txtDescricaoFornecedor);							
					}
				}; 
				addComponent(frmButtonSearchFornecedor);
				addComponent(frmDescFornecedor);
				setExpandRatio(frmDescFornecedor, 1);		
				
				
				if(txtCodFornecedor != null && FornecedorSelecionado != null){
					txtCodFornecedor.setValue(FornecedorSelecionado.getId().toString());
					txtCodFornecedor.setReadOnly(true);
				}
				
				if(txtDescricaoFornecedor != null && FornecedorSelecionado != null){
					txtDescricaoFornecedor.setReadOnly(false);
					txtDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
					txtDescricaoFornecedor.setReadOnly(true);
				}

			}	
		});
		
		vlRoot.addComponent(
			new FormLayout(){					
			{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
						
					TextField txtCodNf = new TextField("Nota Fiscal");
					txtCodNf.setRequired(true);					
					txtCodNf.setStyleName("caption-align-pedido");
					
					addComponent(txtCodNf);	
					fieldGroup.bind(txtCodNf, "codNf");
					
					txtCodNf.setReadOnly(true);
					
					
			}
	   });
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					tbItens = new Table("Itens"){
						@Override
						protected String formatPropertyValue(Object rowId, Object colId,
								Property<?> property) {
							
							if(colId.equals("VALOR") && tbItens.getItem(rowId).getItemProperty(colId).getValue() != null){
								
								
								return "R$ "+Real.formatDbToString(tbItens.getItem(rowId).getItemProperty(colId).getValue().toString());
							}				
							
						
							
							return super.formatPropertyValue(rowId, colId, property);
						
							
						}
					};
					tbItens.addContainerProperty("COD", Integer.class, null);
					tbItens.addContainerProperty("NOME", String.class, null);
					tbItens.addContainerProperty("QTD", String.class, null);
					tbItens.addContainerProperty("CUSTO (R$)", String.class, null);
					tbItens.addContainerProperty("TOTAL (R$)", String.class, null);
					
					tbItens.setColumnAlignment("VALOR (R$)", Align.RIGHT);
					tbItens.setColumnAlignment("TOTAL (R$)", Align.RIGHT);

					tbItens.setWidth("650px");
					tbItens.setHeight("175px");
					tbItens.addStyleName("tb-itens");
					tbItens.addStyleName("caption-align-pedido");
					
					tbItens.setColumnAlignment("CUSTO (R$)", Align.RIGHT);
					//tbItens.set

					
					List<MovimentoEntDetalhe> itens = CompraDAO.getItensCompra((Integer)item.getItemProperty("id").getValue());
					double valorTotal = 0;
					for (MovimentoEntDetalhe s : itens) {
												
						Produto p = ProdutoDAO.find(s.getProdutoId());
						
						if(s.getQuantidade()!=null && s.getValorCusto()!=null){
							Float total = s.getQuantidade() * s.getValorCusto();
							tbItens.addItem(new Object[]{p.getId(), p.getNome(), Real.formatDbToString(s.getQuantidade().toString()),Real.formatDbToString(s.getValorCusto().toString()),Real.formatDbToString(total.toString()) }, tbItens.getItemIds().size()+1);							
						}
					}								
					
					addComponent(tbItens);							
				}
		});	
		
		vlRoot.addComponent(new FormLayout(){					
				{
						setStyleName("form-cutom");
						setMargin(true);
						setSpacing(true);
							
						txtValorTotal = new TextField("Valor Total dos Produtos (R$)");
						txtValorTotal.setRequired(true);					
						txtValorTotal.setStyleName("caption-align-pedido");
						txtValorTotal.addStyleName("align-currency");
						txtValorTotal.setId("txtValorTotal");
						
						addComponent(txtValorTotal);	
						JavaScript.getCurrent().execute("$('#txtValorTotal').maskMoney({decimal:',',thousands:'.'})");
												
						if(item.getItemProperty("valorTotal").getValue() != null){
							txtValorTotal.setValue(Real.formatDbToString(item.getItemProperty("valorTotal").getValue().toString()));
							txtValorTotal.setReadOnly(true);							
						}
				}
		   });
			
	}
	
	
	
	@Override
	public Button buildBtSalvar() {
		
		btFinalizar = new Button("Finalizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(FornecedorSelecionado != null && cbNaturezaOperacao.isValid() && cbTipoDoc.isValid() && txtNotaFiscal.isValid() && txtValorTotalNota.isValid()){
					try {				
						
						item.getItemProperty("empresa_id").setValue(OpusERP4UI.getEmpresa().getId());
						item.getItemProperty("fornecedor").setValue(FornecedorSelecionado);												
						item.getItemProperty("dataHora").setValue(new Date());						
						item.getItemProperty("tipo").setValue("COMPRA");
						item.getItemProperty("codNf").setValue(Integer.parseInt(txtNotaFiscal.getValue()));
						item.getItemProperty("valorTotal").setValue(new Float(Real.formatStringToDB(txtValorTotalNota.getValue())));
						item.getItemProperty("tipo_documento").setValue(cbTipoDoc.getValue().toString());

						if(dfDataValidade.getValue() != null){
							item.getItemProperty("data_validade").setValue(dfDataValidade.getValue());
						}
						
						EntityItem<NaturezaOperacao> entityItemNatOp = (EntityItem)cbNaturezaOperacao.getContainerDataSource().getItem(cbNaturezaOperacao.getValue()); 
						item.getItemProperty("natureza_operacao").setValue(entityItemNatOp.getEntity());
						
						//item.getItemProperty("observacao").setValue(txtObservacao.getValue());
						
						List<MovimentoEntDetalhe> itens = new ArrayList<>();
						boolean valid_itens = true;
						for (Object item : tbItensProduto.getItemIds()) {
							
							if(tbItensProduto.getContainerDataSource().getItem(item).getItemProperty("Nome Produto").getValue() != null){
								String cod = tbItensProduto.getContainerDataSource().getItem(item).getItemProperty("Código de Barras").getValue().toString();
								String ipi = tbItensProduto.getContainerDataSource().getItem(item).getItemProperty("Ipi").getValue().toString();
								String aliquo = tbItensProduto.getContainerDataSource().getItem(item).getItemProperty("Aliq").getValue().toString();
								
								Float qtd = new Float(tbItensProduto.getContainerDataSource().getItem(item).getItemProperty("Qtd").getValue().toString());
								Float vlr_custo = new Float(Real.formatStringToDB(tbItensProduto.getContainerDataSource().getItem(item).getItemProperty("Valor Custo").getValue().toString().replace("R$ ", "")));
								Float ipi2 = new Float( ipi != null ? new String().valueOf(Real.formatStringToDBDouble(ipi)): "0");
								
								Produto p = ProdutoDAO.findByCodBarras(cod);
								
								if(p != null){								
									itens.add(new MovimentoEntDetalhe(null, p.getId(), null, p.getUnidadeProduto() != null ? p.getUnidadeProduto().getId() : null,p.getQtdEstoqueDeposito() != null ? p.getQtdEstoqueDeposito() : new Float(0), qtd, vlr_custo, p.getValorVenda(), p.getTaxaIcms(),ipi2, null, p.getGarantia(), null, Real.formatStringToDBFloat(aliquo)));							
								}
							}else{
								valid_itens = false;
								break;
							}
						}
						
						
						if(valid_itens){
							fireEvent(new ComprasEvent(getUI(), item,itens, true));						
						}else{
							Notify.Show("Um ou mais itens, não estão vinculados!", Notify.TYPE_ERROR);
						}
					} catch (Exception e) {					
						e.printStackTrace();					
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
					}
				}else{
					
					if(!cbTipoDoc.isValid()){
						cbTipoDoc.addStyleName("invalid-txt");
					}else{
						cbTipoDoc.removeStyleName("invalid-txt");
					}
						
					if(!cbNaturezaOperacao.isValid()){
						cbNaturezaOperacao.addStyleName("invalid-txt");
					}else{
						cbNaturezaOperacao.removeStyleName("invalid-txt");
					}
					
					if(FornecedorSelecionado == null){
						txtCodFornecedor.addStyleName("invalid-txt");
					}else{
						txtCodFornecedor.removeStyleName("invalid-txt");
					}
					
					if(!txtNotaFiscal.isValid()){
						txtNotaFiscal.addStyleName("invalid-txt");
					}else{
						txtNotaFiscal.removeStyleName("invalid-txt");
					}
					
					if(!txtValorTotalNota.isValid()){
						txtValorTotalNota.addStyleName("invalid-txt");
					}else{
						txtValorTotalNota.removeStyleName("invalid-txt");
					}
									
					Notify.Show_Invalid_Submit_Form();
					
				}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.F2,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btFinalizar.click();
			}
		};
		
		btFinalizar.addShortcutListener(slbtOK);
		
		btFinalizar.setStyleName("default");
		return btFinalizar;
	}
	


	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
							
				close();									
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
	
	
	public Button buildBtFechar() {
		btFechar = new Button("Fechar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {								
				close();			
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btFechar.click();
			}
		};
		
		btFechar.addShortcutListener(slbtCancelar);
		
		
		return btFechar;
	}
	
	
	public void addListerner(ComprasListerner target){
		try {
			Method method = ComprasListerner.class.getDeclaredMethod("onClose", ComprasEvent.class);
			addListener(ComprasEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ComprasListerner target){
		removeListener(ComprasEvent.class, target);
	}
	public static class ComprasEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private List<MovimentoEntDetalhe> itens;
		
		public ComprasEvent(Component source, Item item,List<MovimentoEntDetalhe> itens, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;
			this.itens = itens;
		}
		
		public List<MovimentoEntDetalhe> getItens(){
			return itens;
		}

		public Item getItem() {
			return item;
		}	

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ComprasListerner extends Serializable{
		public void onClose(ComprasEvent event);
	}
	public File file;
	private String logo;
	public class XmlUploader implements Receiver, SucceededListener{
		
		
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			FileOutputStream fos = null;
			
			if(filename != null && !filename.equals("")){
				String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();	
				try {
					
					file = new File(basepath + "/WEB-INF/uploads/" + filename);
					fos = new FileOutputStream(file);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Notify.Show("O Arquivo não pode ser Aberto", Notify.TYPE_ERROR);
					return null;
				}
				
			}
			
			return fos;
		}

		@Override
		public void uploadSucceeded(SucceededEvent event) {
			lerXml();
		}

	}
	
	private String xml = "";
	private void lerXml(){
		
					if(file != null){
			
			 try {  
				  
				    
				    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();  
				    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();  
				    Document doc = dBuilder.parse(file);  
				  
				    //optional, but recommended  
				    //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work  
				    doc.getDocumentElement().normalize();  
				  
				    System.out.println("Root element :" + doc.getDocumentElement().getNodeName());  
				  
				    NodeList nList = doc.getElementsByTagName("nfeProc");  
				  
				    System.out.println("----------------------------");  
				  
				    for (int temp = 0; temp < nList.getLength(); temp++) {  
				  
				        Node nNode = nList.item(temp);  
				  
				        System.out.println("\nCurrent Element :" + nNode.getNodeName());  
				  
				        if (nNode.getNodeType() == Node.ELEMENT_NODE) {  
				  
				            Element eElement = (Element) nNode;  
				  
				            String cnpjFornecedor = eElement.getElementsByTagName("CNPJ").item(0) != null ? eElement.getElementsByTagName("CNPJ").item(0).getTextContent() : "";
				            String nomeFornecedor = eElement.getElementsByTagName("xNome").item(0) != null ? eElement.getElementsByTagName("xNome").item(0).getTextContent() : "";
				            String ieFornecedor = eElement.getElementsByTagName("IE").item(0) != null ? eElement.getElementsByTagName("IE").item(0).getTextContent() : "";
				            String endFornecedor = eElement.getElementsByTagName("xLgr").item(0) != null ? eElement.getElementsByTagName("xLgr").item(0).getTextContent() : "";
				            String numeroFornecedor = eElement.getElementsByTagName("nro").item(0) != null ? eElement.getElementsByTagName("nro").item(0).getTextContent() : "";
				            String bairroFornecedor = eElement.getElementsByTagName("xBairro").item(0) != null ? eElement.getElementsByTagName("xBairro").item(0).getTextContent() : "";
				            String codMunicipioFornecedor = eElement.getElementsByTagName("cMun").item(0) != null ?  eElement.getElementsByTagName("cMun").item(0).getTextContent() : "";
				            String municipioFornecedor = eElement.getElementsByTagName("xMun").item(0) != null ? eElement.getElementsByTagName("xMun").item(0).getTextContent() : "";
				            String ufFornecedor = eElement.getElementsByTagName("UF").item(0) != null ? eElement.getElementsByTagName("UF").item(0).getTextContent() : "";
				            String cepFornecedor = eElement.getElementsByTagName("CEP").item(0) != null ? eElement.getElementsByTagName("CEP").item(0).getTextContent() : "";
				            String codpaisFornecedor = eElement.getElementsByTagName("cPais").item(0) != null ? eElement.getElementsByTagName("cPais").item(0).getTextContent() : "";
				            String paisFornecedor = eElement.getElementsByTagName("xPais").item(0) != null ? eElement.getElementsByTagName("xPais").item(0).getTextContent() : "";
				            String foneFornecedor =  eElement.getElementsByTagName("fone").item(0) != null ? eElement.getElementsByTagName("fone").item(0).getTextContent() : "";
				            String ncm =  eElement.getElementsByTagName("NCM").item(0) != null ? eElement.getElementsByTagName("NCM").item(0).getTextContent() : "";
				            String cest =  eElement.getElementsByTagName("CEST").item(0) != null ? eElement.getElementsByTagName("CEST").item(0).getTextContent() : "";
				            
				            FornecedorSelecionado = FornecedorDAO.findFornecedorCnpj(eElement.getElementsByTagName("CNPJ").item(0).getTextContent().toString());
				            
				            if(FornecedorSelecionado != null){
								txtDescricaoFornecedor.setReadOnly(false);
								txtDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
								txtDescricaoFornecedor.setReadOnly(true);
								
								txtCodFornecedor.setValue(FornecedorSelecionado.getId().toString());
															
							}else {

								FornecedorSelecionado = FornecedorDAO.save(new Fornecedor(null, OpusERP4UI.getEmpresa(), 
										nomeFornecedor, nomeFornecedor, cnpjFornecedor, ieFornecedor, endFornecedor, numeroFornecedor, bairroFornecedor, municipioFornecedor, ufFornecedor, paisFornecedor, cepFornecedor, "81", foneFornecedor, 
										null, null, null, null, null, null, null, null, null, null,	null, null, null, null, null, null, null, null, null,	null, null, null, null, null, null, new Date(), new Date(), null, null,null, 
										null, null, null, null, null, null, null, null, new Date(), null, "ATIVO", true));
								
								if(FornecedorSelecionado != null){
									txtDescricaoFornecedor.setReadOnly(false);
									txtDescricaoFornecedor.setValue(FornecedorSelecionado.getRazao_social());
									txtDescricaoFornecedor.setReadOnly(true);
									
									txtCodFornecedor.setValue(FornecedorSelecionado.getId().toString());
								}else {								
									txtDescricaoFornecedor.setReadOnly(false);
									txtDescricaoFornecedor.setValue("");
									txtDescricaoFornecedor.setReadOnly(true);
									txtCodFornecedor.setValue("");					
								}
							}
				            
				            
				            String nNota = eElement.getElementsByTagName("nNF").item(0).getTextContent();
				            txtNotaFiscal.setValue(nNota);
				            
				            String vNota = eElement.getElementsByTagName("vNF").item(0).getTextContent();
				            txtValorTotalNota.setValue(Real.formatDbToString(vNota));
				            
				            System.out.println("Produtos : " + eElement.getAttribute("det"));
				            tbItensProduto.removeAllItems();
				            for(int i=0;i<eElement.getElementsByTagName("prod").getLength();i++){
			
				            	
				            	Produto p  = ProdutoDAO.findByCodBarras(!eElement.getElementsByTagName("cEAN").item(i).getTextContent().isEmpty() &&  !eElement.getElementsByTagName("cEAN").item(i).getTextContent().equals("SEM GTIN") ? eElement.getElementsByTagName("cEAN").item(i).getTextContent() : eElement.getElementsByTagName("cProd").item(i).getTextContent().replace(".", "").replace("-", ""));
				            	if(p != null && p.getgTin().equals("SEM GTIN")){
				            		p = null;				            	
				            	}
				            	
				            	String aliquo = eElement.getElementsByTagName("pICMS").item(i) != null ? eElement.getElementsByTagName("pICMS").item(i).getTextContent():"0";					
								
								double diferenca_aliquota = new Double(aliquo);
								diferenca_aliquota = new Float(OpusERP4UI.getEmpresa().getAliquota_interna()) - new Float(aliquo);								
							
								tbItensProduto.addItem(new Object[]{
										eElement.getElementsByTagName("cProd").item(i).getTextContent().replace(".", "").replace("-", ""),
										eElement.getElementsByTagName("xProd").item(i).getTextContent(),p != null ? p.getNome() : null,
												p != null && p.getQtdEstoqueDeposito() != null ? p.getQtdEstoqueDeposito().toString() : null,p != null && p.getQtdEstoque() != null ? p.getQtdEstoque().toString() : null,eElement.getElementsByTagName("qCom").item(i).getTextContent(),
										"R$ "+Real.formatDbToString(eElement.getElementsByTagName("vUnCom").item(i).getTextContent()),
										"R$ "+Real.formatDbToString(eElement.getElementsByTagName("vProd").item(i).getTextContent()),
										eElement.getElementsByTagName("pIPI").item(i) != null ? Real.formatDbToString(String.valueOf(eElement.getElementsByTagName("pIPI").item(i).getTextContent())):"0,00",
										Real.formatDbToString(String.valueOf(diferenca_aliquota)), cest,
										!eElement.getElementsByTagName("cEAN").item(i).getTextContent().isEmpty() && !eElement.getElementsByTagName("cEAN").item(i).getTextContent().equals("SEM GTIN") ? eElement.getElementsByTagName("cEAN").item(i).getTextContent() :eElement.getElementsByTagName("cProd").item(i).getTextContent().replace(".", "").replace("-", "")
								},i);    	
							
				            }
				            
				            
				        }  
				    }  
				    } catch (Exception e) {  
				    	e.printStackTrace();
				    }  
			
			//System.out.println(xml);
			Notify.Show("Xml importado com sucesso!", Notify.TYPE_SUCCESS);
					}
			

	}
}
