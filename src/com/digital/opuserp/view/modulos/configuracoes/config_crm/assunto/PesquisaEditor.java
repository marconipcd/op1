package com.digital.opuserp.view.modulos.configuracoes.config_crm.assunto;

import com.digital.opuserp.dao.CrmAssuntosDAO;
import com.digital.opuserp.dao.CrmPesquisaDAO;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.Perguntas;
import com.digital.opuserp.domain.Respostas;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class PesquisaEditor extends Window {

	VerticalLayout vlRoot;
	private CrmAssunto crmAssunto;
	private ComboBox cbStatus;
	private Button btCancelar;
	
	public PesquisaEditor(CrmAssunto crmAssunto, String titulo){
		
		this.crmAssunto = crmAssunto;
		
		center();
		setModal(true);
		setCaption(titulo);
		setClosable(false);
		setResizable(false);
					
		vlRoot = new VerticalLayout();
		vlRoot.setWidth("100%");
		vlRoot.addStyleName("border-form");
		vlRoot.setMargin(true);
		
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
				hlButtons.addComponent(buildBtFechar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		buildLayout();
	}
	
	private Table tbPerguntas;
	private Table tbRespostas;
	private Button btAdicionarPergunta;
	private Button btAdicionarResposta;
	private JPAContainer<Perguntas> containerPerguntas;
	private Button btConcluir;
	
	
	private Button buildBtFechar(){
		btConcluir = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(cbStatusPesquisa.isValid()){
					crmAssunto.setPerguntas_ativas(cbStatusPesquisa.getValue().toString());
					CrmAssuntosDAO.save(crmAssunto);
					close();
				}
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Ok",
				ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btConcluir.click();
			}
		};
		
		btConcluir.focus();
		btConcluir.addShortcutListener(slbtCancelar); 
		btConcluir.addStyleName("default");
		
		return btConcluir;
	}
	
	
	private Button buildBtCancelar(){
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
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
		
		
		return btCancelar;
	}
	
	VerticalLayout vl1;
	private ComboBox cbStatusPesquisa;
	public void buildLayout(){
		
		
		vl1 = new VerticalLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-cutom");
				
				cbStatusPesquisa = new ComboBox("Status");
				cbStatusPesquisa.setTextInputAllowed(false);
				cbStatusPesquisa.setNullSelectionAllowed(false);
				cbStatusPesquisa.setRequired(true);
				cbStatusPesquisa.addItem("ATIVO");
				cbStatusPesquisa.addItem("INATIVO");
				
				if(crmAssunto.getPerguntas_ativas() != null){
					if(crmAssunto.getPerguntas_ativas().equals("ATIVO")){
						cbStatusPesquisa.select("ATIVO");
					}else{
						cbStatusPesquisa.select("INATIVO");
					}
				}else{
					cbStatusPesquisa.select("INATIVO");
				}
				
				
				addComponent(new HorizontalLayout(){
					{
						setWidth("100%");
						
						addComponent(cbStatusPesquisa);						
						addComponent(buildbtAdicionarPergunta());
						
						setComponentAlignment(cbStatusPesquisa, Alignment.MIDDLE_LEFT);
						setComponentAlignment(btAdicionarPergunta, Alignment.BOTTOM_RIGHT);
					}
				});
				
				addComponent(buildTbPergunta());
				
				addComponent(buildbtAdicionarResposta());
				addComponent(buildTbResposta()); 	
			}
		};
		
		
		vlRoot.addComponent(vl1);
		
	}
	private Table buildTbPergunta(){
		tbPerguntas = new Table(null, buildContainer());
		tbPerguntas.setVisibleColumns(new Object[]{"pergunta"});
		tbPerguntas.setWidth("700px");
		tbPerguntas.setHeight("200px");
		tbPerguntas.setSelectable(true); 
		//tbPerguntas.setImmediate(true); 
		
		tbPerguntas.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				
				
				
				Button btDeletar = new Button(null, new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Excluir esta Pergunta?", true,true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){		
									
									
									EntityItem<Perguntas> itemPergunta = (EntityItem<Perguntas>) tbPerguntas.getItem(itemId); 													
									
									boolean check = CrmPesquisaDAO.remove(itemPergunta.getEntity()); 
									
									if(check){										
										Notify.Show("Pergunta Excluída com Sucesso!", Notify.TYPE_SUCCESS);
										vl1.replaceComponent(tbPerguntas, buildTbPergunta());
									}else{												
										Notify.Show("Não foi Possivel Realizar a Exclusão da Pergunta !", Notify.TYPE_ERROR);												
									}										
								}
							}
						});
						
						getUI().addWindow(gd);
					}
				});
				btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
				btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
				btDeletar.setDescription("Deletar Pergunta");
				
				return btDeletar;
			}
		});
		
		tbPerguntas.addGeneratedColumn("e", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				
				
				
				Button btEditar = new Button(null, new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
							
						EntityItem<Perguntas> itemPergunta = (EntityItem<Perguntas>) tbPerguntas.getItem(itemId);	
						final Perguntas pergunta = itemPergunta.getEntity();
						
						final TextField txtPergunta = new TextField("Pergunta");
						txtPergunta.setWidth("450px");
						txtPergunta.setRequired(true); 
						txtPergunta.setValue(pergunta.getPergunta());
						
						cbStatus = new ComboBox("Status");
						cbStatus.setTextInputAllowed(false);
						cbStatus.setNullSelectionAllowed(false);
						cbStatus.setRequired(true); 
						cbStatus.addItem("ATIVO");
						cbStatus.addItem("INATIVO");
//						cbStatus.setImmediate(true);
//						cbStatus.addListener(new Property.ValueChangeListener() {
//							
//							@Override
//							public void valueChange(ValueChangeEvent event) {	
//								if(event.getProperty().getValue() != null){
//									pergunta.setStatus(event.getProperty().toString()); 
//								}
//							}
//						});
											
						if(pergunta.getStatus() != null){
							if(pergunta.getStatus().equals("ATIVO")){
								cbStatus.select("ATIVO");
							}else{
								cbStatus.select("INATIVO");
							}
						}else{
							cbStatus.select("INATIVO");
						}
						
						
						final CheckBox ckbPrePergunta = new CheckBox("Pre-definir Perguntas?");
						ckbPrePergunta.setRequired(true);						
						ckbPrePergunta.setValue(pergunta.isPre_respostas());
						ckbPrePergunta.addStyleName("ckb-pre-perguntas");
												
						final Window win_editar_pergunta = new Window("Editar Pergunta");
						win_editar_pergunta.center();
						win_editar_pergunta.setModal(true);				
						win_editar_pergunta.setClosable(false);
						win_editar_pergunta.setResizable(false);
						
						win_editar_pergunta.setContent(new VerticalLayout(){
							{
								setWidth("100%");
								
								setMargin(true);
								
								addComponent(new VerticalLayout(){
									{
										setMargin(true);
										addStyleName("border-form");
										
										addComponent(txtPergunta);
										addComponent(cbStatus);
										addComponent(ckbPrePergunta);
										
										HorizontalLayout hlButtonsBo = new HorizontalLayout(){
											{
												//setWidth("100%");
												Button btSalvar = new Button("Salvar", new Button.ClickListener() {
													
													@Override
													public void buttonClick(ClickEvent event) {
														if(txtPergunta.isValid() && cbStatus.isValid() && ckbPrePergunta.isValid()){
															 String status = cbStatus.getValue().toString();
															 pergunta.setPergunta(txtPergunta.getValue());
															 pergunta.setStatus(status);
															 pergunta.setPre_respostas(ckbPrePergunta.getValue());
															 CrmPesquisaDAO.save(pergunta);
															 Notify.Show("Pergunta Alterada com Sucesso", Notify.TYPE_SUCCESS);
															 vl1.replaceComponent(tbPerguntas, buildTbPergunta());
															 btAdicionarResposta.setEnabled(false);
															 tbRespostas.setContainerDataSource(null); 
															 win_editar_pergunta.close();
														}else{
															
															if(!txtPergunta.isValid()){
																txtPergunta.addStyleName("invalid-txt");
															}else{
																txtPergunta.removeStyleName("invalid-txt");
															}
															
															if(!cbStatus.isValid()){
																cbStatus.addStyleName("invalid-txt");
															}else{
																cbStatus.removeStyleName("invalid-txt");
															}
															
															 Notify.Show_Invalid_Submit_Form();
														}
													}
												});
												btSalvar.setStyleName(Reindeer.BUTTON_SMALL);
												
												Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
													
													@Override
													public void buttonClick(ClickEvent event) {
														win_editar_pergunta.close();
													}
												});
												btCancelar.setStyleName(Reindeer.BUTTON_SMALL);
												
												addComponent(btSalvar);
												addComponent(btCancelar);
												setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
												setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
											}
										};
										
										addComponent(hlButtonsBo);
										setComponentAlignment(hlButtonsBo, Alignment.MIDDLE_RIGHT);
										txtPergunta.focus(); 
									}
								});
							}
						});
						
						getUI().addWindow(win_editar_pergunta); 
															
					}
				});
				btEditar.setIcon(new ThemeResource("icons/mail_new.png"));
				btEditar.setStyleName(BaseTheme.BUTTON_LINK);
				btEditar.setDescription("Editar Pergunta");
				
				return btEditar;
			}
		});
		
		tbPerguntas.setColumnWidth("x", 20);
		tbPerguntas.setColumnWidth("e", 20);
		
		tbPerguntas.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			
			public String getStyle(Table source, Object itemId, Object propertyId) {

				 if (propertyId == null){
	                    return "row-header-default"; // Will not actually be visible
				 }else{
	               
	                Item item = source.getItem(itemId);
	                
	                if(item != null && item.getItemProperty("status") != null && item.getItemProperty("status").getValue().equals("INATIVO")){
	                	               
			            return "row-header-pedido-cancelado";	                
		                
	                }else{
	                	 return "row-header-default";
	                }
				 }
				 
			}
		});
						
		tbPerguntas.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {	
				if(event.getProperty().getValue() != null && (boolean)tbPerguntas.getItem(event.getProperty().getValue()).getItemProperty("pre_respostas").getValue() == true){
					btAdicionarResposta.setEnabled(true);
					vl1.replaceComponent(tbRespostas, buildTbResposta());
				}else{
					btAdicionarResposta.setEnabled(false);
					tbRespostas.setContainerDataSource(null); 
				}
			}
		});
		
//		tbPerguntas.addValueChangeListener(new Property.ValueChangeListener() {
//			
//			@Override
//			public void valueChange(ValueChangeEvent event) {
//				if(tbPerguntas.getValue() != null){
//					vl1.replaceComponent(tbRespostas, buildTbResposta());
//				}
//			}
//		});

		return tbPerguntas;
	}
	private Table buildTbResposta(){
		
		
		
		tbRespostas = new Table();
		
		if(tbPerguntas.getValue() != null){
			EntityItem<Perguntas> itemPergunta = (EntityItem<Perguntas>) tbPerguntas.getItem(tbPerguntas.getValue()); 
			JPAContainer<Respostas> containerResposta = JPAContainerFactory.make(Respostas.class, ConnUtil.getEntity());
			containerResposta.addContainerFilter(Filters.eq("pergunta", itemPergunta.getEntity()));
			tbRespostas.setContainerDataSource(containerResposta); 
			tbRespostas.setVisibleColumns(new Object[]{"resposta"});
			
			tbRespostas.addGeneratedColumn("x", new Table.ColumnGenerator() {
				
				@Override
				public Object generateCell(final Table source, final Object itemId, Object columnId) {
					
					
					
					Button btDeletar = new Button(null, new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Excluir esta Resposta?", true,true);
							gd.addListerner(new GenericDialog.DialogListerner() {
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){		
										
										
										EntityItem<Respostas> itemPergunta = (EntityItem<Respostas>) tbRespostas.getItem(itemId); 													
										
										boolean check = CrmPesquisaDAO.removeResposta(itemPergunta.getEntity()); 
										
										if(true){										
											Notify.Show("Resposta Excluída com Sucesso!", Notify.TYPE_SUCCESS);
											vl1.replaceComponent(tbRespostas, buildTbResposta());
										}else{												
											Notify.Show("Não foi Possivel Realizar a Exclusão da Resposta !", Notify.TYPE_ERROR);												
										}										
									}
								}
							});
							
							getUI().addWindow(gd);
						}
					});
					btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
					btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
					btDeletar.setDescription("Deletar Resposta");
					
					return btDeletar;
				}
			});
					
			tbRespostas.setColumnWidth("x", 20);
		}
		
		tbRespostas.setWidth("700px");
		tbRespostas.setHeight("200px");
		
		
		
		
		return tbRespostas;
	}
	private JPAContainer<Perguntas> buildContainer(){
		containerPerguntas = JPAContainerFactory.make(Perguntas.class, ConnUtil.getEntity());
		containerPerguntas.addContainerFilter(Filters.eq("assunto", crmAssunto));
		
		return containerPerguntas;
	}
	
	private Button buildbtAdicionarResposta(){
		btAdicionarResposta = new Button("Adicionar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final TextField txtResposta = new TextField("Resposta");
				txtResposta.setWidth("450px");
				txtResposta.setRequired(true); 
				
				final Window win_nova_pergunta = new Window("Nova Resposta");
				win_nova_pergunta.center();
				win_nova_pergunta.setModal(true);				
				win_nova_pergunta.setClosable(false);
				win_nova_pergunta.setResizable(false);
				
				win_nova_pergunta.setContent(new VerticalLayout(){
					{
						setWidth("100%");
						
						setMargin(true);
						
						addComponent(new VerticalLayout(){
							{
								setMargin(true);
								addStyleName("border-form");
								
								addComponent(txtResposta);
								
								HorizontalLayout hlButtonsBo = new HorizontalLayout(){
									{
										//setWidth("100%");
										Button btSalvar = new Button("Salvar", new Button.ClickListener() {
											
											@Override
											public void buttonClick(ClickEvent event) {
												if(txtResposta.isValid()){
													 EntityItem<Perguntas> itemPergunta = (EntityItem<Perguntas>) tbPerguntas.getItem(tbPerguntas.getValue());
													 CrmPesquisaDAO.saveResposta(new Respostas(null, itemPergunta.getEntity(), txtResposta.getValue()));
													 Notify.Show("Resposta Cadastrada com Sucesso", Notify.TYPE_SUCCESS);
													 vl1.replaceComponent(tbRespostas, buildTbResposta());
													 
													 win_nova_pergunta.close();
												}else{
													
													 txtResposta.addStyleName("invalid-txt");
													
													 Notify.Show_Invalid_Submit_Form();
												}
											}
										});
										btSalvar.setStyleName(Reindeer.BUTTON_SMALL);
										
										Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
											
											@Override
											public void buttonClick(ClickEvent event) {
												win_nova_pergunta.close();
											}
										});
										btCancelar.setStyleName(Reindeer.BUTTON_SMALL);
										
										addComponent(btSalvar);
										addComponent(btCancelar);
										setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
										setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
									}
								};
								
								addComponent(hlButtonsBo);
								setComponentAlignment(hlButtonsBo, Alignment.MIDDLE_RIGHT);
								txtResposta.focus(); 
							}
						});
					}
				});
				
				getUI().addWindow(win_nova_pergunta); 
			}
		});
		btAdicionarResposta.setStyleName(Reindeer.BUTTON_SMALL);
		btAdicionarResposta.setEnabled(false);
		
		return btAdicionarResposta;
	}
	
	private Button buildbtAdicionarPergunta(){
		btAdicionarPergunta = new Button("Adicionar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final TextField txtPergunta = new TextField("Pergunta");
				txtPergunta.setWidth("450px");
				txtPergunta.setRequired(true); 
				
				final ComboBox cbStatus = new ComboBox("Status");
				cbStatus.setTextInputAllowed(false);
				cbStatus.setNullSelectionAllowed(false);
				cbStatus.addItem("ATIVO");
				cbStatus.addItem("INATIVO");
				cbStatus.setRequired(true); 
				
				final CheckBox ckbPrePergunta = new CheckBox("Pre-definir Perguntas?");
				ckbPrePergunta.setRequired(true);
				ckbPrePergunta.setValue(true);
				ckbPrePergunta.addStyleName("ckb-pre-perguntas");
				
				final Window win_nova_pergunta = new Window("Nova Pergunta");
				win_nova_pergunta.center();
				win_nova_pergunta.setModal(true);				
				win_nova_pergunta.setClosable(false);
				win_nova_pergunta.setResizable(false);
				
				win_nova_pergunta.setContent(new VerticalLayout(){
					{
						setWidth("100%");
						
						setMargin(true);
						
						addComponent(new VerticalLayout(){
							{
								setMargin(true);
								addStyleName("border-form");
								
								addComponent(txtPergunta);
								addComponent(cbStatus);
								addComponent(ckbPrePergunta);
								
								HorizontalLayout hlButtonsBo = new HorizontalLayout(){
									{
										//setWidth("100%");
										Button btSalvar = new Button("Salvar", new Button.ClickListener() {
											
											@Override
											public void buttonClick(ClickEvent event) {
												if(txtPergunta.isValid() && cbStatus.isValid() && ckbPrePergunta.isValid()){
													 CrmPesquisaDAO.save(new Perguntas(null, crmAssunto, txtPergunta.getValue(),ckbPrePergunta.getValue(), cbStatus.getValue().toString()));
													 Notify.Show("Pergunta Cadastrada com Sucesso", Notify.TYPE_SUCCESS);
													 vl1.replaceComponent(tbPerguntas, buildTbPergunta());
													 
													 win_nova_pergunta.close();
												}else{
													
													if(!txtPergunta.isValid()){
														txtPergunta.addStyleName("invalid-txt");
													}else{
														txtPergunta.removeStyleName("invalid-txt");
													}
													
													if(!cbStatus.isValid()){
														cbStatus.addStyleName("invalid-txt");
													}else{
														cbStatus.removeStyleName("invalid-txt");
													}
													
													 Notify.Show_Invalid_Submit_Form();
												}
											}
										});
										btSalvar.setStyleName(Reindeer.BUTTON_SMALL);
										
										Button btCancelar = new Button("Cancelar", new Button.ClickListener() {
											
											@Override
											public void buttonClick(ClickEvent event) {
												win_nova_pergunta.close();
											}
										});
										btCancelar.setStyleName(Reindeer.BUTTON_SMALL);
										
										addComponent(btSalvar);
										addComponent(btCancelar);
										setComponentAlignment(btSalvar, Alignment.MIDDLE_RIGHT);
										setComponentAlignment(btCancelar, Alignment.MIDDLE_RIGHT);
									}
								};
								
								addComponent(hlButtonsBo);
								setComponentAlignment(hlButtonsBo, Alignment.MIDDLE_RIGHT);
								txtPergunta.focus(); 
							}
						});
					}
				});
				
				getUI().addWindow(win_nova_pergunta); 
			}
		});
		btAdicionarPergunta.setStyleName(Reindeer.BUTTON_SMALL);
		
		return btAdicionarPergunta;
	}
}
