package com.digital.opuserp.view.modulos.financeiro.haver;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.EmpresaDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.HaverCab;
import com.digital.opuserp.domain.HaverDetalhe;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.ClienteUtil;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class HaverEditorDetalhamento extends Window  {

	private Item item;
	private Button btSalvar;
	private Button btCancelar;
	private Button btImprimir;
	private FieldGroup fieldGroup;
	private VerticalLayout vlRoot;	
	private TextField tfCodCliente;
	private TextField tfDescricaoCliente;
	private Cliente ClienteSelecionado;	
	private Cliente ClienteSelecionadoInd;	
	private ComboBox cbDoc;
	private String referencia;
	private TextField txtNdoc;	
	private TextField txtValor;	
	boolean indicacao;
	
	Label lb_total_disponivel = new Label("<h1 style='margin-top: 0px;margin-bottom: 0px;'>0,00</h1>", ContentMode.HTML);
	double valor_disponivel = 0;
	
	Table tb;
	JPAContainer<HaverDetalhe> container;
	
	public HaverEditorDetalhamento(Item item, String title, boolean modal){
		
		
		
		this.item = item;
		setWidth("1184px");
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
				
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				addComponent(vlRoot);
				
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setWidth("100%");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				
								
				hlButtons.addComponent(lb_total_disponivel);
				hlButtons.addComponent(buildBtImprimir("Imprimir"));
				hlButtons.addComponent(buildBtCancelar("Fechar"));
				
				hlButtons.setComponentAlignment(lb_total_disponivel, Alignment.BOTTOM_LEFT);
				hlButtons.setComponentAlignment(btImprimir, Alignment.BOTTOM_RIGHT);
				hlButtons.setComponentAlignment(btCancelar, Alignment.BOTTOM_RIGHT);
				
				hlButtons.setExpandRatio(lb_total_disponivel, 1f);
				
				addComponent(hlButtons);

			}
		});
		
		
		buildLayout();
		
	}
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
					
		EntityItem<HaverCab> entityItem = (EntityItem<HaverCab>)item;
		container = JPAContainerFactory.makeReadOnly(HaverDetalhe.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("haverCab", entityItem.getEntity()));
				
		tb = new Table(null, container){
			
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				Object v = property.getValue();
			
				if (v instanceof Date) {
				      Date dateValue = (Date) v;
				      return new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(dateValue);
				}
				
				if(colId.equals("valor_disponivel") || colId.equals("valor")){
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null) {										
						return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());		
					}
				}	
				
				return super.formatPropertyValue(rowId, colId, property);
			}	
		};		
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {

				if(source.getItem(itemId).getItemProperty("status").getValue().toString().equals("ATIVO")){
					return "default";					
				}else{					
					return "header-pedido-cancelado";
				}
			}
		});

		tb.setSizeFull();
		tb.setColumnCollapsingAllowed(true); 
		tb.setColumnHeader("doc", "Documento");
		tb.setColumnHeader("n_doc", "Nº Doc.");
		tb.setColumnHeader("referente", "Referente");		
		tb.setColumnHeader("operador", "Operador");
		tb.setColumnHeader("data_emissao", "Data Emissão");
		tb.setColumnHeader("valor", "Valor");		
		tb.setColumnHeader("tipo", "Tipo");
		tb.setColumnHeader("status", "Status");		
		
		tb.setColumnCollapsed("status", true);
		tb.setColumnAlignment("valor", Align.RIGHT);
		//tb.setColumnCollapsed("id", true);
		tb.setVisibleColumns(new Object[] {"tipo","data_emissao","doc","n_doc","referente","operador","valor","status"});
		
		vlRoot.addComponent(new ComboBox(){
			{
				setTextInputAllowed(false);
				setNullSelectionAllowed(false); 
				addItem("TODOS");
				addItem("ENTRADA");
				addItem("SAÍDA");
				
				select("TODOS");
				
				
				addValueChangeListener(new Property.ValueChangeListener() {
					
					
					@Override
					public void valueChange(
							com.vaadin.data.Property.ValueChangeEvent event) {
						
						filtrar(event.getProperty().getValue().toString());
					}
				});
			}
		});
		vlRoot.addComponent(tb);
			
		calcular_valor_disponivel(tb);
	}
	private void calcular_valor_disponivel(Table tb){
		valor_disponivel = 0;
		Collection<?> itemIds = tb.getItemIds();
		for (Object itemId: itemIds) {
			String status = tb.getItem(itemId).getItemProperty("status").getValue().toString();
			String tipo = tb.getItem(itemId).getItemProperty("tipo").getValue().toString();
			double valor_linha = Double.parseDouble(tb.getItem(itemId).getItemProperty("valor").getValue().toString());
			
			if(status.equals("ATIVO")){
				if(tipo.equals("ENTRADA")){
					valor_disponivel = valor_disponivel + valor_linha;
				}else{
					valor_disponivel = valor_disponivel - valor_linha;
				}
			}
		}

		lb_total_disponivel.setValue("<h1 style='margin-top: 0px;margin-bottom: 0px;'>R$ "+Real.formatDbToString(String.valueOf(valor_disponivel))+"</h1>");
	}	
	private void filtrar(String s){
		if(s != null && s != ""){
			container.removeAllContainerFilters();
			EntityItem<HaverCab> entityItem = (EntityItem<HaverCab>)item;			
			container.addContainerFilter(Filters.eq("haverCab", entityItem.getEntity()));
			
			if(s.equals("ENTRADA")){
				container.addContainerFilter(Filters.eq("tipo", "ENTRADA"));	
			}
			
			if(s.equals("SAÍDA")){
				container.addContainerFilter(Filters.eq("tipo", "SAIDA"));	
			}
		}
		
		calcular_valor_disponivel(tb);
	}	
	public void buildLayoutReadOnly(){
		
		fieldGroup = new FieldGroup(item);
					
		vlRoot.addComponent(
				new HorizontalLayout(){
					{
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																	
								TextField txtCodigo = new TextField ("Código");				
								txtCodigo.setWidth("80px");				
								txtCodigo.setStyleName("caption-align");
								txtCodigo.setNullRepresentation("");										
								
								addComponent(txtCodigo);					
								
								fieldGroup.bind(txtCodigo,"id");
								txtCodigo.setReadOnly(true);					
							}
						});
						
						
						addComponent(new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
																	
								TextField txtDataEmissao = new TextField ("Data de Emissão");				
								txtDataEmissao.setWidth("130px");				
								txtDataEmissao.setStyleName("caption-align");
								txtDataEmissao.setNullRepresentation("");			
								txtDataEmissao.addStyleName("align-404-data-emissao");
								
								
								addComponent(txtDataEmissao);					
								
								if(item.getItemProperty("data_emissao").getValue() != null){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
									txtDataEmissao.setValue(sdf.format(item.getItemProperty("data_emissao").getValue()));
								}
								
								txtDataEmissao.setReadOnly(true);					
							}
						});
					}
		});
		
		vlRoot.addComponent(new HorizontalLayout(){
			{				
				JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
				containerClientes.addContainerFilter(Filters.eq("status", "INATIVO"));
				
				
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
					preencher = true;
				}
				
				tfCodCliente = new TextField("Cliente");				
				tfCodCliente.setWidth("60px");				
				tfCodCliente.setNullRepresentation("");
				tfCodCliente.setStyleName("caption-align");				
				tfCodCliente.setId("txtCodCliente");
				
				JavaScript.getCurrent().execute("$('#txtCodCliente').mask('0000000000')");
				tfCodCliente.setImmediate(true);
				
				tfCodCliente.addListener(new TextChangeListener() {
					
					
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						ClienteSelecionado = new Cliente();
						
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
							ClienteSelecionado = cDAO.getCliente(Integer.parseInt(event.getText()));		
							
							if(ClienteSelecionado != null){
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue(ClienteSelecionado.getNome_razao());
								tfDescricaoCliente.setReadOnly(true);								
							}else {
								tfDescricaoCliente.setReadOnly(false);
								tfDescricaoCliente.setValue("");
								tfDescricaoCliente.setReadOnly(true);								
							}
						}else{
							tfDescricaoCliente.setReadOnly(false);
							tfDescricaoCliente.setValue("");
							tfDescricaoCliente.setReadOnly(true);							
						}
					}
				});

				tfCodCliente.setRequired(true);		
				tfDescricaoCliente = new TextField();
				tfDescricaoCliente.setTabIndex(2000);
				tfDescricaoCliente.setReadOnly(true);
				tfDescricaoCliente.setWidth("562px");
									
				if(item.getItemProperty("cliente") != null && item.getItemProperty("cliente").getValue() != null){
					EmpresaDAO eDAO = new EmpresaDAO();
					Cliente c = eDAO.getCliente(((Cliente)item.getItemProperty("cliente").getValue()).getId());
					
					if(c != null){
						tfCodCliente.setValue(c.getId().toString());
						tfCodCliente.setReadOnly(false);
						
						tfDescricaoCliente.setReadOnly(false);
						tfDescricaoCliente.setValue(c.getNome_razao());
						tfDescricaoCliente.setReadOnly(true);
					}
				}
							
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setTabIndex(300000);
				btSearchCliente.setEnabled(false);
				btSearchCliente.addClickListener(new Button.ClickListener() {
					
					
					public void buttonClick(ClickEvent event) {
						ClienteUtil cUtil = new ClienteUtil(true, true,"INATIVO");
						cUtil.addListerner(new ClienteUtil.ClienteListerner() {
							
							
							public void onSelected(
									com.digital.opuserp.view.util.ClienteUtil.ClienteEvent event) {
									if(event.getCliente() != null){
										tfCodCliente.setValue(event.getCliente().getId().toString());
										tfDescricaoCliente.setReadOnly(false);
										tfDescricaoCliente.setValue(event.getCliente().getNome_razao());
										tfDescricaoCliente.setReadOnly(true);
										ClienteSelecionado = event.getCliente();

									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});
				
				FormLayout frmCodigoCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(tfCodCliente);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchCliente =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");										
						addComponent(btSearchCliente);							
					}
				}; 
							
				FormLayout frmDescCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfDescricaoCliente);							
					}
				}; 
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);	
				
				if(item.getItemProperty("cliente").getValue() != null){
					Cliente c = (Cliente)item.getItemProperty("cliente").getValue();
					tfCodCliente.setValue(c.getId().toString());
					tfCodCliente.setReadOnly(true);
					
					tfDescricaoCliente.setReadOnly(false);
					tfDescricaoCliente.setValue(c.getNome_razao());
					tfDescricaoCliente.setReadOnly(true);
				}
			}	
		});
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					ComboBox cbNdoc = new ComboBox("Documento");
					cbNdoc.setNullSelectionAllowed(false);
					cbNdoc.setWidth("250px");				
					cbNdoc.setStyleName("caption-align");
					cbNdoc.addItem("CHEQUE");
					cbNdoc.addItem("DINHEIRO");
					cbNdoc.addItem("PEDIDO DE VENDA");
					cbNdoc.addItem("VALE COMPRA");
					cbNdoc.setRequired(true);
										
					if(item.getItemProperty("doc").getValue().toString().equals("INDICACAO")){
						cbNdoc.addItem("INDICACAO");
						cbNdoc.select("INDICACAO");
						cbNdoc.setReadOnly(true);
						addComponent(cbNdoc);
					}else{
						addComponent(cbNdoc);					
						fieldGroup.bind(cbNdoc,"doc");						
						cbNdoc.setReadOnly(true);
					}
				}
	  });
		
	   vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtDoc = new TextField ("Nº Documento");				
					txtDoc.setWidth("250px");				
					txtDoc.setStyleName("caption-align");
					txtDoc.setNullRepresentation("");			
					txtDoc.setRequired(true);
					
					addComponent(txtDoc);					
					
					fieldGroup.bind(txtDoc,"nDoc");
					txtDoc.setReadOnly(true);					
				}
		});
		
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					TextField txtReferente = new TextField ("Referente a");				
					txtReferente.setWidth("250px");				
					txtReferente.setStyleName("caption-align");
					txtReferente.setNullRepresentation("");			
					txtReferente.setRequired(true);
					
					addComponent(txtReferente);					
					
					
					fieldGroup.bind(txtReferente,"referente");
					txtReferente.setReadOnly(true);
					
				}
		});
		
		vlRoot.addComponent(new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					txtValor = new TextField ("Valor do Haver");				
					txtValor.setWidth("80px");				
					txtValor.setStyleName("caption-align");
					txtValor.setNullRepresentation("");			
					txtValor.setRequired(true);
					txtValor.setId("txtValor");
					txtValor.addStyleName("align-currency");
					
					addComponent(txtValor);	
					
					if(item.getItemProperty("valor").getValue() != null){
						txtValor.setValue(Real.formatDbToString(item.getItemProperty("valor").getValue().toString()));
					}
					
					txtValor.setReadOnly(true);					
					JavaScript.getCurrent().execute("$('#txtValor').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextField txtValorUtilizado = new TextField ("Valor Utilizado");				
					txtValorUtilizado.setWidth("80px");				
					txtValorUtilizado.setStyleName("caption-align");
					txtValorUtilizado.setNullRepresentation("");			
					txtValorUtilizado.setRequired(true);
					txtValorUtilizado.setId("txtValorUtilizado");
					txtValorUtilizado.addStyleName("align-currency");
					
					addComponent(txtValorUtilizado);	
					
					if(item.getItemProperty("valor_disponivel").getValue() != null && item.getItemProperty("valor").getValue() != null){
						double valorUtilizado = new Double(item.getItemProperty("valor").getValue().toString()) - new Double(item.getItemProperty("valor_disponivel").getValue().toString());
						txtValorUtilizado.setValue(Real.formatDbToString(String.valueOf(valorUtilizado)));
					}
					
					txtValorUtilizado.setReadOnly(true);					
					JavaScript.getCurrent().execute("$('#txtValorUtilizado').maskMoney({decimal:',',thousands:'.'})");
				}
			});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextField txtValorDisponivel = new TextField ("Valor Disponível");				
					txtValorDisponivel.setWidth("80px");				
					txtValorDisponivel.setStyleName("caption-align");
					txtValorDisponivel.setNullRepresentation("");			
					txtValorDisponivel.setRequired(true);
					txtValorDisponivel.setId("txtValorDisponivel");
					txtValorDisponivel.addStyleName("align-currency");
					
					addComponent(txtValorDisponivel);	
					
									
					if(item.getItemProperty("valor_disponivel").getValue() != null){
						txtValorDisponivel.setValue(Real.formatDbToString(item.getItemProperty("valor_disponivel").getValue().toString()));
					}
					
					txtValorDisponivel.setReadOnly(true);					
					JavaScript.getCurrent().execute("$('#txtValorUtilizado').maskMoney({decimal:',',thousands:'.'})");
				}
			});

		
		
	}
	

	
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && ClienteSelecionado != null && txtValor.isValid()){
					try {				
						
						item.getItemProperty("cliente").setValue(ClienteSelecionado);	
						item.getItemProperty("data_emissao").setValue(new Date());
						item.getItemProperty("data_alteracao").setValue(new Date());
						item.getItemProperty("valor").setValue(Real.formatStringToDBDouble(txtValor.getValue()));
						item.getItemProperty("valor_disponivel").setValue(Real.formatStringToDBDouble(txtValor.getValue()));
						item.getItemProperty("status").setValue("DISPONIVEL");
						item.getItemProperty("usuario").setValue(OpusERP4UI.getUsuarioLogadoUI().getUsername());
						
						if(indicacao){						
							referencia = "INDICACAO: "+ClienteSelecionadoInd.getId()+"-"+ClienteSelecionadoInd.getNome_razao();													
							item.getItemProperty("referente").setValue(referencia);
							item.getItemProperty("nDoc").setValue(txtNdoc.getValue());
							item.getItemProperty("doc").setValue(cbDoc.getValue().toString());	
							item.getItemProperty("data_alteracao").setValue(new Date());
						}
						
						fieldGroup.commit();				
						fireEvent(new HaverEvent(getUI(), item, true));					
						
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
					
					if(txtValor.getValue().equals("") || txtValor.getValue().isEmpty() || txtValor.getValue().equals("")){
						txtValor.addStyleName("invalid-txt");
					}else{
						txtValor.removeStyleName("invalid-txt");
					}
					
					if(ClienteSelecionado == null){
						tfCodCliente.addStyleName("invalid-txt");
						tfDescricaoCliente.addStyleName("invalid-txt");
					}else{
						tfCodCliente.removeStyleName("invalid-txt");
						tfDescricaoCliente.removeStyleName("invalid-txt");
					}
					
					Notify.Show_Invalid_Submit_Form();
				}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}

	
	public Button buildBtImprimir(String lb) {
		btImprimir = new Button(lb, new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				try{
					Window window = new Window();
					window.setCaption("Detalhamento de Haver");
			        window.setWidth("800px");
			        window.setHeight("600px");
			        window.setResizable(false);
			        window.center();
			        window.setModal(true);
			        window.setStyleName("disable_scroolbar");		
			        window.setCloseShortcut(KeyCode.ESCAPE, null);
			        

			        List<HaverDetalhe> haverDetalhes= new ArrayList<HaverDetalhe>();
			        Collection<?> itens = tb.getItemIds();
			        for (Object itemId: itens) {
						EntityItem<HaverDetalhe> entityItem = (EntityItem<HaverDetalhe>)tb.getItem(itemId);
						haverDetalhes.add(entityItem.getEntity());
					}
			        
			        StreamResource resource = new StreamResource(new ImprimirRelatorioHaver(haverDetalhes), "DETALHAMENTO DE HAVER.pdf");
			        resource.getStream();			        
			        resource.setMIMEType("application/pdf");
			        resource.setCacheTime(0);
			        
			        Embedded e = new Embedded();
			        e.setSizeFull();
			        e.setType(Embedded.TYPE_BROWSER); 
			        e.setSource(resource);			     
			        window.setContent(e);
			        getUI().addWindow(window);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		return btImprimir;
	}
	
	public Button buildBtCancelar(String lb) {
		btCancelar = new Button(lb, new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new HaverEvent(getUI(), item, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(fieldGroup.isValid() && ClienteSelecionado != null &&  txtValor.isValid()){
									try {	
										
										item.getItemProperty("cliente").setValue(ClienteSelecionado);	
										item.getItemProperty("data_emissao").setValue(new Date());
										item.getItemProperty("valor").setValue(Real.formatDbToString(txtValor.getValue()));
										item.getItemProperty("status").setValue("ABERTO");
										item.getItemProperty("data_alteracao").setValue(new Date());

										
										if(indicacao){
											referencia = "INDICACAO: "+ClienteSelecionadoInd.getId()+"-"+ClienteSelecionadoInd.getNome_razao();	
											item.getItemProperty("referente").setValue(referencia);
											item.getItemProperty("nDoc").setValue(txtNdoc.getValue());
											item.getItemProperty("doc").setValue(cbDoc.getValue().toString());	
											item.getItemProperty("data_alteracao").setValue(new Date());

											
										}
										
										fieldGroup.commit();				
										fireEvent(new HaverEvent(getUI(), item, true));					
										
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
								fireEvent(new HaverEvent(getUI(), item, false));
								close();						
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		
		
		return btCancelar;
	}
	
	public void addListerner(HaverListerner target){
		try {
			Method method = HaverListerner.class.getDeclaredMethod("onClose", HaverEvent.class);
			addListener(HaverEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(HaverListerner target){
		removeListener(HaverEvent.class, target);
	}
	public static class HaverEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public HaverEvent(Component source, Item item, boolean confirm) {
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
	public interface HaverListerner extends Serializable{
		public void onClose(HaverEvent event);
	}

	
}
