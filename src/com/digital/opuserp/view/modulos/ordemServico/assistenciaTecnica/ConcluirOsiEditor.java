package com.digital.opuserp.view.modulos.ordemServico.assistenciaTecnica;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Osi;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.domain.ServicosItensOsi;
import com.digital.opuserp.domain.TiposProblemasOsi;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.cadastro.transportadoras.TransportadoraEditor.TransportadoraEvent;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.ServicoUtil;
import com.digital.opuserp.view.util.ServicoUtil.ServicoEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class ConcluirOsiEditor extends Window implements GenericEditor {

	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;	
	
	ComboBox cbProblema; 
	
	private Table tbItens;
	
	private List<ServicosItensOsi> itens;
	
	public ConcluirOsiEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("795px");
//		setHeight("265px");
		
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

	
	
	public String getNextId() {
		CredenciaisAcessoDAO caDao = new CredenciaisAcessoDAO();
		return caDao.getNextID();
	}
	
	
	private TextField tfCodServico;
	private TextField tfNomeServico;
	private TextField tfValorServico;
	
	private Servico ServicoSelecionado;
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
				//setWidth("100%");
				
				JPAContainer<Cliente> containerClientes = JPAContainerFactory.make(Cliente.class, ConnUtil.getEntity());
				containerClientes.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
				containerClientes.addContainerFilter(Filters.eq("status", "INATIVO"));
				
				
				boolean preencher = false;
				if(item.getItemProperty("id").getValue() != null && item.getItemProperty("cliente").getValue() != null){
					preencher = true;
				}
				
				tfCodServico = new TextField("Serviço");				
				tfCodServico.setWidth("60px");				
				tfCodServico.setNullRepresentation("");
				tfCodServico.setStyleName("caption-align");
				tfCodServico.focus();
				tfCodServico.setId("txtCodServico");
				
				JavaScript.getCurrent().execute("$('#txtCodServico').mask('0000000000')");
				tfCodServico.setImmediate(true);
						
				
				
				tfCodServico.addListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ClienteDAO cDAO = new ClienteDAO();
						ServicoSelecionado = new Servico();
												
						if(event.getText()!=null && !event.getText().isEmpty() && !event.getText().equals("")){
							
														
							ServicoSelecionado = ServicoDAO.find(Integer.parseInt(event.getText()));		
							
							if(ServicoSelecionado != null){
								tfNomeServico.setReadOnly(false);
								tfNomeServico.setValue(ServicoSelecionado.getNome());
								tfNomeServico.setReadOnly(true);
								
								tfValorServico.setReadOnly(false);
								tfValorServico.setValue(ServicoSelecionado.getValor_venda());
								tfValorServico.setReadOnly(true);
								
								
								
							}else {
								tfNomeServico.setReadOnly(false);
								tfNomeServico.setValue("");
								tfNomeServico.setReadOnly(true);
								
								tfValorServico.setReadOnly(false);
								tfValorServico.setValue("");
								tfValorServico.setReadOnly(true);
							
							}
						}else{
							tfNomeServico.setReadOnly(false);
							tfNomeServico.setValue("");
							tfNomeServico.setReadOnly(true);	
							
							tfValorServico.setReadOnly(false);
							tfValorServico.setValue("");
							tfValorServico.setReadOnly(true);
							
						}
					}
				});

				tfCodServico.setRequired(true);		
				
				tfNomeServico = new TextField();
				tfNomeServico.setTabIndex(2000);
				tfNomeServico.setReadOnly(true);
				tfNomeServico.setWidth("365px");
				
				tfValorServico = new TextField();
				//tfNomeServico.setTabIndex(2000);
				tfValorServico.setReadOnly(true);
				tfValorServico.setWidth("50px");
				
												
											
				final Button btSearchCliente = new Button();
				btSearchCliente.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCliente.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCliente.setTabIndex(300000);
				btSearchCliente.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						ServicoUtil cUtil = new ServicoUtil(true, true);
						cUtil.addListerner(new ServicoUtil.ServicoListerner() {
							
							@Override
							public void onSelected(
									ServicoEvent event) {
									if(event.getServico() != null){
										tfCodServico.setValue(event.getServico().getId().toString());
										tfNomeServico.setReadOnly(false);
										tfNomeServico.setValue(event.getServico().getNome());
										tfNomeServico.setReadOnly(true);
										
										tfValorServico.setReadOnly(false);
										tfValorServico.setValue(event.getServico().getValor_venda());
										tfValorServico.setReadOnly(true);
										
										ServicoSelecionado = event.getServico();
										
										
										
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
												
						addComponent(tfCodServico);							
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
						
						addComponent(tfNomeServico);							
					}
				};
				
				FormLayout frmValorCliente = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						addComponent(tfValorServico);							
					}
				};
				
				FormLayout frmBtAdd = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom-new");		
						addStyleName("form-cutom_hide_require");
						
						Button btAdd = new Button("Add", new Button.ClickListener() {
							
														
							@Override
							public void buttonClick(ClickEvent event) {
								if(ServicoSelecionado != null){
									
																		
									tbItens.addItem(new Object[]{ServicoSelecionado.getId(), ServicoSelecionado.getNome(),ServicoSelecionado.getValor_venda()}, ServicoSelecionado.getId());
									ServicoSelecionado = null;
									tfCodServico.setValue("");
									tfNomeServico.setReadOnly(false);
									tfNomeServico.setValue("");
									tfNomeServico.setReadOnly(false);
									
									tfValorServico.setReadOnly(false);
									tfValorServico.setValue("");
									tfValorServico.setReadOnly(false);
									
																		
									calcularValorTotal();
									
								}
							}
						});
						
						btAdd.setStyleName(Reindeer.BUTTON_SMALL);
						
						
						addComponent(btAdd);							
					}
				};
				
				addComponent(frmButtonSearchCliente);
				addComponent(frmDescCliente);
				setExpandRatio(frmDescCliente, 1);
				addComponent(frmValorCliente);
				addComponent(frmBtAdd);
				

			}	
		});		
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					tbItens = new Table(){
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
					tbItens.addContainerProperty("VALOR", String.class, null);
					tbItens.setWidth("587px");
					tbItens.setHeight("160px");
					tbItens.addStyleName("tb-itens");
					tbItens.addStyleName("caption-align");
					
					tbItens.setColumnAlignment("VALOR", Align.RIGHT);
					
					tbItens.addGeneratedColumn("x", new Table.ColumnGenerator() {
						
						@Override
						public Object generateCell(final Table source, final Object itemId, Object columnId) {
							
							
							
							Button btDeletar = new Button(null, new Button.ClickListener() {
								
								@Override
								public void buttonClick(ClickEvent event) {
									GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Excluir este Item?", true,true);
									gd.addListerner(new GenericDialog.DialogListerner() {
										
										@Override
										public void onClose(DialogEvent event) {
											if(event.isConfirm()){		
												
												tbItens.removeItem(itemId);
												tbItens.commit();
												calcularValorTotal();
																																	
											}
										}
									});
									
									getUI().addWindow(gd);
								}
							});
							btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
							btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
							btDeletar.setDescription("Deletar Relatório");
							
							return btDeletar;
						}
					});
					
					tbItens.setColumnWidth("x", 20);

				
					addComponent(tbItens);
									
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField txtValorOS = new TextField("Valor Total (R$)");
					
					txtValorOS.setStyleName("caption-align");		
					txtValorOS.setNullRepresentation("");				
					txtValorOS.setRequired(true);
					txtValorOS.setId("txtValorOs");
					txtValorOS.addStyleName("align-currency");
					txtValorOS.setReadOnly(true);
					txtValorOS.addStyleName("bold");
					
					
					JavaScript.getCurrent().execute("$('#txtValorOs').maskMoney({decimal:',',thousands:'.'})");
					
									
					addComponent(txtValorOS);
					fieldGroup.bind(txtValorOS, "valor");
									
				}
		});
		
		
		

		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextArea txtLaudo = new TextArea ("Laudo");								
					txtLaudo.setStyleName("caption-align");
					txtLaudo.setId("txtBoleto");
					txtLaudo.setNullRepresentation("");
					txtLaudo.setHeight("40px");
					txtLaudo.setWidth("587px");
					txtLaudo.setRequired(true);
									
					addComponent(txtLaudo);
					fieldGroup.bind(txtLaudo, "conclusao");
				
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
														
					TextArea txtPecasSubstituidas = new TextArea ("Peças Substituídas");								
					txtPecasSubstituidas.setStyleName("caption-align");
					txtPecasSubstituidas.setId("txtBoleto");
					txtPecasSubstituidas.setNullRepresentation("");
					txtPecasSubstituidas.setHeight("40px");
					txtPecasSubstituidas.setWidth("587px");
					txtPecasSubstituidas.setRequired(true);
									
					addComponent(txtPecasSubstituidas);
					fieldGroup.bind(txtPecasSubstituidas, "pecas_subs");
				
				}
		});
		
		
		
	
	
	
	}
	
	private void calcularValorTotal(){
		if(tbItens.getItemIds().size() > 0){
			double valorTotal = 0;
				
			itens = new ArrayList<>();			
			for (Object o: tbItens.getItemIds().toArray()) {				
				valorTotal = valorTotal + Double.parseDouble(tbItens.getItem(o).getItemProperty("VALOR").getValue().toString());	
				itens.add(new ServicosItensOsi(null, new Osi((Integer)item.getItemProperty("id").getValue()), new Servico((Integer)tbItens.getItem(o).getItemProperty("COD").getValue())));
			}
			
			String valorFinal = Real.formatDbToString(String.valueOf(valorTotal));
			
			((TextField)fieldGroup.getField("valor")).setReadOnly(false);
			((TextField)fieldGroup.getField("valor")).setValue(valorFinal);
			((TextField)fieldGroup.getField("valor")).setReadOnly(true);
		}
	}
	
	
	private JPAContainer<TiposProblemasOsi> getProblemas(){
		JPAContainer<TiposProblemasOsi> problemas = JPAContainerFactory.makeReadOnly(TiposProblemasOsi.class, ConnUtil.getEntity());
		problemas.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));		
		problemas.sort(new Object[]{"nome"}, new boolean[]{true});
		return problemas;
	}
	

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

			
									
				if(fieldGroup.isValid()){
					try {		
						
						fieldGroup.commit();		
						item.getItemProperty("status").setValue("FECHADO");						
						item.getItemProperty("data_conclusao").setValue(new Date());
						
						fireEvent(new FecharOsiEvent(getUI(), item,itens, true));
						

					} catch (Exception e) {											
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
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
		//btSalvar.setEnabled(false);
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new FecharOsiEvent(getUI(), item,itens, false));
					close();
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informações Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								try {
									
									if(fieldGroup.isValid()){							
										fieldGroup.commit();		
										item.getItemProperty("status").setValue("FECHADO");						
										item.getItemProperty("data_conclusao").setValue(new Date());
										
										fireEvent(new FecharOsiEvent(getUI(), item,itens, true));								
									}else{
										for (Field<?> field: fieldGroup.getFields()) {						
											if(!field.isValid()){
												field.addStyleName("invalid-txt");
											}else{
												field.removeStyleName("invalid-txt");
											}
									    }
									    
									    if(!cbProblema.isValid()){
									    	cbProblema.addStyleName("invalid-txt");
									    }else{
									    	cbProblema.removeStyleName("invalid-txt");  	
									    }					    

									    Notify.Show_Invalid_Submit_Form();
									}								
								
								} catch (Exception e) {									
									e.printStackTrace();
									Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new TransportadoraEvent(getUI(), item, false));
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
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
	
	
	public void addListerner(FecharOsiListerner target){
		try {
			Method method = FecharOsiListerner.class.getDeclaredMethod("onClose", FecharOsiEvent.class);
			addListener(FecharOsiEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(FecharOsiListerner target){
		removeListener(FecharOsiEvent.class, target);
	}
	public static class FecharOsiEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private List<ServicosItensOsi> itens;
		
		public FecharOsiEvent(Component source, Item item, List<ServicosItensOsi> itens, boolean confirm) {
			super(source);
			this.item = item;
			this.confirm = confirm;
			this.itens = itens;
		}

		public Item getItem() {
			return item;
		}	

		public List<ServicosItensOsi> getItens() {
			return itens;
		}	
		
		public boolean isConfirm() {
			return confirm;
		}	
		
	}
	public interface FecharOsiListerner extends Serializable{
		public void onClose(FecharOsiEvent event);
	}
	
	
	




}
