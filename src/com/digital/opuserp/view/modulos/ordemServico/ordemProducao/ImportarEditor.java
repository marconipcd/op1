package com.digital.opuserp.view.modulos.ordemServico.ordemProducao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.PedidoUtil;
import com.digital.opuserp.view.util.PedidoUtil.PedidoEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class ImportarEditor extends Window {
	
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	ComboBox cbSetor;
	
	
	private EcfPreVendaCabecalho pedidoSelecionado;
	private DateField dfPrevisao;
	
	
	public ImportarEditor( String title, boolean modal){
		
		setWidth("915px");
		setHeight("495px");
				
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
	
	
	private Table tbItens;
	private Table tbItem;
	
	private TextField tfCodPedido;
	private TextField txtCliente;
	private TextField txtComprador;
	
	public void buildLayout(){
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				
				tfCodPedido = new TextField("Pedido Nº");				
				tfCodPedido.setWidth("75px");				
				tfCodPedido.setNullRepresentation("");
				tfCodPedido.setStyleName("caption-align");
				tfCodPedido.setRequired(true);				
				tfCodPedido.focus();
				tfCodPedido.setTextChangeEventMode(TextChangeEventMode.LAZY);
				tfCodPedido.setId("tfCodPedido");
				JavaScript.getCurrent().execute("$('#tfCodPedido').mask('0000000000')");
				
				tfCodPedido.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					
					public void textChange(TextChangeEvent event) {
						
						if ( event.getText() != null && !event.getText().isEmpty() && !event.getText().equals("")){
														
							EcfPreVendaCabecalho pedido = PedidoDAO.findTypePedido(Integer.parseInt(event.getText()));
							
							if(pedido != null){								
								
								pedidoSelecionado = pedido;
								
								JPAContainer<EcfPreVendaDetalhe> container = JPAContainerFactory.make(EcfPreVendaDetalhe.class, ConnUtil.getEntity());
								container.addContainerFilter(Filters.eq("ecfPreVendaCabecalhoId", pedidoSelecionado.getId()));
								
								tbItens.setContainerDataSource(container); 
								tbItens.setVisibleColumns(new Object[]{"id","produtoId","quantidade","valorUnitario","valorTotal"});
								tbItens.setColumnHeader("id", "Código");
								tbItens.setColumnHeader("produtoId", "Descrição");
								tbItens.setColumnHeader("quantidade", "Qtd.");
								tbItens.setColumnHeader("valorUnitario", "Valor");
								tbItens.setColumnHeader("valorTotal", "Total");
								
								//tbItens.setConverter("id", null);
																
								txtCliente.setReadOnly(false);
								txtCliente.setValue(pedidoSelecionado.getCliente() != null ? pedidoSelecionado.getCliente().getNome_razao() :"");
								txtCliente.setReadOnly(true);								
								
								if(pedidoSelecionado.getComprador() != null && !pedidoSelecionado.getComprador().equals("")&& !pedidoSelecionado.getComprador().equals("null")){
									txtComprador.setReadOnly(false);
									txtComprador.setValue(pedidoSelecionado.getComprador());
									txtComprador.setReadOnly(true);
								}
							}else{
								pedidoSelecionado = null;
								
								tbItens.removeAllItems();
								txtCliente.setReadOnly(false);
								txtCliente.setValue("");
								txtCliente.setReadOnly(true);
								
								txtComprador.setReadOnly(false);
								txtComprador.setValue("");
								txtComprador.setReadOnly(true);
								
							}
						}else{
							pedidoSelecionado = null;
							
							tbItens.removeAllItems();
							txtCliente.setReadOnly(false);
							txtCliente.setValue("");
							txtCliente.setReadOnly(true);
							
							txtComprador.setReadOnly(false);
							txtComprador.setValue("");
							txtComprador.setReadOnly(true);
						}
					}
				});
				
				
											
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setDescription("Pesquisar Cliente");
				btSearchCliente.setTabIndex(10000);
				btSearchCliente.addListener(new Button.ClickListener() {
					
					
					public void buttonClick(ClickEvent event) {
						
						PedidoUtil cUtil = new PedidoUtil(true, true);
						cUtil.addListerner(new PedidoUtil.PedidoListerner() {
							
							
							public void onSelected(PedidoEvent event) {
								if(event.getPedido() != null){
									tfCodPedido.setValue(event.getPedido().getId().toString());									
									pedidoSelecionado = event.getPedido();
									
									JPAContainer<EcfPreVendaDetalhe> container = JPAContainerFactory.make(EcfPreVendaDetalhe.class, ConnUtil.getEntity());
									container.addContainerFilter(Filters.eq("ecfPreVendaCabecalhoId", pedidoSelecionado.getId()));
																		
									tbItens.setContainerDataSource(container); 
									tbItens.setVisibleColumns(new Object[]{"id","produtoId","quantidade","valorUnitario","valorTotal"});									
									tbItens.setColumnHeader("id", "Código");
									tbItens.setColumnHeader("produtoId", "Descrição");
									tbItens.setColumnHeader("quantidade", "Qtd.");
									tbItens.setColumnHeader("valorUnitario", "Valor");
									tbItens.setColumnHeader("valorTotal", "Total");
																		
									txtCliente.setReadOnly(false);
									txtCliente.setValue(pedidoSelecionado.getCliente()!=null ? pedidoSelecionado.getCliente().getNome_razao():"");
									txtCliente.setReadOnly(true);
																		
									if(pedidoSelecionado.getComprador() != null && !pedidoSelecionado.getComprador().equals("")&& !pedidoSelecionado.getComprador().equals("null")){
										txtComprador.setReadOnly(false);
										txtComprador.setValue(pedidoSelecionado.getComprador());
										txtComprador.setReadOnly(true);
									}
								}else{	

									pedidoSelecionado = null;
									
									tbItens.removeAllItems();
									txtCliente.setReadOnly(false);
									txtCliente.setValue("");
									txtCliente.setReadOnly(true);
									
									txtComprador.setReadOnly(false);
									txtComprador.setValue("");
									txtComprador.setReadOnly(true);
									
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
						setStyleName("form-cutom");		
												
						addComponent(tfCodPedido);							
					}
				};
				addComponent(frmCodigoCliente);
		
				FormLayout frmButtonSearchPedido =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchCliente);							
					}
				}; 
							
				
				addComponent(frmButtonSearchPedido);
				
			}	
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					txtCliente = new TextField("Cliente");					
					txtCliente.setStyleName("caption-align");	
					txtCliente.setWidth("350px");
					txtCliente.setReadOnly(true);
										
					addComponent(txtCliente);							
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					txtComprador = new TextField("Comprador");					
					txtComprador.setStyleName("caption-align");
					txtComprador.setWidth("350px");
					txtComprador.setReadOnly(true);
										
					addComponent(txtComprador);							
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);				
									
					tbItens = new Table("Itens"){
						
						protected String formatPropertyValue(Object rowId, Object colId,
								Property<?> property) {
							
							if(colId.equals("valorUnitario") || colId.equals("valorTotal")){				
								return "R$ "+Real.formatDbToString(tbItens.getItem(rowId).getItemProperty(colId).getValue().toString());
							}
							
							if(colId.equals("produtoId")){								
								Servico s = ServicoDAO.find((Integer)tbItens.getItem(rowId).getItemProperty(colId).getValue());
								
								
									if(s != null){return s.getNome();};
								
								
								
								
							}
							

							
							
							
							return super.formatPropertyValue(rowId, colId, property);			
						}
					};
					tbItens.setStyleName("caption-align");
					tbItens.setWidth("600px");
					tbItens.setHeight("220px");
					
					
					tbItens.setCellStyleGenerator(new Table.CellStyleGenerator() {
						
						
						public String getStyle(Table source, Object itemId, Object propertyId) {

							 if (propertyId == null){
				                    return "row-header-default"; // Will not actually be visible
							 }else{
				               
				                Item item = source.getItem(itemId);
				                
				                if(item != null && item.getItemProperty("produtoId") != null && item.getItemProperty("produtoId").getValue() != null){
				                	
				                	Servico s = ServicoDAO.find((Integer)item.getItemProperty("produtoId").getValue());
					                if(s != null){
					                	String producao = s.getProducao();
					               
						                if(producao != null && producao.equals("NAO")){
						                	return "row-header-pedido-cancelado";    
						                }
					                }
				                }else{
				                	 return "row-header-default";
				                }
							 }
							 return "row-header-default";
						}
					});

					
					addComponent(tbItens);
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					dfPrevisao = new DateField("Previsão");					
					dfPrevisao.setStyleName("caption-align");
					dfPrevisao.setDateFormat("dd/MM/yyyy HH:mm:ss");
					dfPrevisao.setResolution(DateField.RESOLUTION_HOUR);
					dfPrevisao.setResolution(DateField.RESOLUTION_MIN);
					dfPrevisao.setRequired(true);
															
					addComponent(dfPrevisao);							
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
										
					
					cbSetor = new ComboBox("Setor");
					cbSetor.setRequired(true); 
					cbSetor.setNullSelectionAllowed(false);
					cbSetor.setTextInputAllowed(false);
					cbSetor.setStyleName("caption-align");
										
					cbSetor.addItem("CRIACAO");
					cbSetor.addItem("PRE-IMPRESSAO");
					cbSetor.addItem("IMPRESSAO");
					cbSetor.addItem("ACABAMENTO");
					cbSetor.addItem("QUALIDADE");
					cbSetor.addItem("EXPEDICAO");
					
					cbSetor.select("CRIACAO");				
					
					addComponent(cbSetor); 
				}
		});
		
		
		
		
	}
	
	private JPAContainer<EcfPreVendaDetalhe> getItensServico(Integer codPedido){
		JPAContainer<EcfPreVendaDetalhe> container = JPAContainerFactory.make(EcfPreVendaDetalhe.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("ecfPreVendaCabecalhoId", codPedido));
		
		return container;
	}
	
	private JPAContainer<EcfPreVendaCabecalho> getPedidosServico(){
		JPAContainer<EcfPreVendaCabecalho> container = JPAContainerFactory.make(EcfPreVendaCabecalho.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("tipo", "PEDIDO"));
		container.addContainerFilter(Filters.eq("tipoVenda", "SERVICO"));
		container.addContainerFilter(Filters.eq("situacao", "F"));
		
		container.addNestedContainerProperty("cliente.nome_razao");
		
		container.sort(new Object[]{"id"}, new boolean[]{false});
		
		return container;
	}

	
	public Button buildBtSalvar() {
		btSalvar = new Button("Importar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(pedidoSelecionado != null && dfPrevisao.isValid() && cbSetor.isValid()){
					try {
					
						fireEvent(new ImportarEvent(getUI(), pedidoSelecionado,dfPrevisao.getValue(),cbSetor.getValue().toString(), true));						
						
					} catch (Exception e) {
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);

						e.printStackTrace();
					}
					
				}else{
					
					 
					 if(!dfPrevisao.isValid()){
						 dfPrevisao.addStyleName("invalid-txt");
					 }else{
						 dfPrevisao.removeStyleName("invalid-txt");
					 }
					 
					 if(!cbSetor.isValid()){
						 cbSetor.addStyleName("invalid-txt");
					 }else{
						 cbSetor.removeStyleName("invalid-txt");
					 }				 
					
					 
					 if(pedidoSelecionado == null){
						 tfCodPedido.addStyleName("invalid-txt");
					 }else{
						 tfCodPedido.removeStyleName("invalid-txt");
					 }
					 
					 Notify.Show_Invalid_Submit_Form();

				}
				
			}
		});
		btSalvar.setStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("Salvar", ShortcutAction.KeyCode.ENTER, null) {
			
			
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);		
		return btSalvar;
	}

	
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
							
				fireEvent(new ImportarEvent(getUI(), null,null,null, false));
				close();			
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar",
				ShortcutAction.KeyCode.ESCAPE, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};

		btCancelar.addShortcutListener(slbtCancelar);
		
		return btCancelar;
	}
	
	
	public void addListerner(ImportarListerner target){
		try {
			Method method = ImportarListerner.class.getDeclaredMethod("onClose", ImportarEvent.class);
			addListener(ImportarEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ImportarListerner target){
		removeListener(ImportarEvent.class, target);
	}
	public static class ImportarEvent extends Event{
		
		private EcfPreVendaCabecalho pedido;
		private Date previsao;
		private String setor;
		private boolean confirm;
		
		public ImportarEvent(Component source, EcfPreVendaCabecalho pedido, Date previsao,String setor, boolean confirm) {
			super(source);
			this.pedido = pedido;
			this.previsao = previsao;
			this.setor = setor;
			this.confirm = confirm;			
		}

		public EcfPreVendaCabecalho getPedido() {
			return pedido;
		}	
		public Date getPrevisao() {
			return previsao;
		}
		
		public String getSetor(){
			return setor;
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface ImportarListerner extends Serializable{
		public void onClose(ImportarEvent event);
	}
	
}
