package com.digital.opuserp.view.modulos.configuracoes.config_ordemServico;

import java.util.Date;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ConfigOrdemServicoDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.GrupoOseDAO;
import com.digital.opuserp.dao.SubGrupoDAO;
import com.digital.opuserp.dao.TipoSubGrupoDAO;
import com.digital.opuserp.domain.ChecklistOsEquipamento;
import com.digital.opuserp.domain.ChecklistOsInstalacao;
import com.digital.opuserp.domain.ChecklistOsLocalizacao;
import com.digital.opuserp.domain.ConfigOse;
import com.digital.opuserp.domain.GrupoOse;
import com.digital.opuserp.domain.SubGrupoOse;
import com.digital.opuserp.domain.TipoItemProduto;
import com.digital.opuserp.domain.TipoSubGrupoOse;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.configuracoes.config_ordemServico.GrupoEditor.GrupoEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_ordemServico.SubGrupoEditor.SubGrupoEvent;
import com.digital.opuserp.view.modulos.configuracoes.config_ordemServico.TipoGrupoEditor.TipoSubGrupoEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConfigOrdemServicoView extends VerticalLayout {


	
	Table tb;
	TextField tfBusca;
	Button btNovo;
	Button btEditar;
	Button btExcluir;
	Button btLimiteDiario;
	Button btLimiteMensal;
	HorizontalLayout hlFloat;
	
	
	ShortcutListener slNovo;
	ShortcutListener slEditar;
	
	private Integer codSubModulo;
	GerenciarModuloDAO gmDAO;
	
	public ConfigOrdemServicoView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			 TabSheet ts_principal = new TabSheet();
			 ts_principal.setSizeFull();
			 
			 TabSheet.Tab tGrupo = ts_principal.addTab(buildLayoutGrupo(), "Grupos");
			 TabSheet.Tab tCheckListEquipamento  = ts_principal.addTab(buildLayoutChecklistEquipamento(), "Checklist Equipamento");
			 TabSheet.Tab tCheckListLocalizacao  = ts_principal.addTab(buildLayoutChecklistLocalizacao(), "Checklist Localização");
			 TabSheet.Tab tCheckListInstalacao = ts_principal.addTab(buildLayoutChecklistInstalacao(), "Checklist Instalação");
			 TabSheet.Tab tOutrosParametros = ts_principal.addTab(buildLayoutOutrosParametros(), "Outros Parametros");
			 			 
			 addComponent(ts_principal);
		}
	}
	
	Button btCadastrarGrupo;
	
	
	Window winEditar;
	private void buildSubMenuEditar(ClickEvent event) {
		winEditar = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winEditar.setContent(l);
        winEditar.setWidth("300px");
        winEditar.addStyleName("notifications");
        winEditar.setClosable(false);
        winEditar.setResizable(false);
        winEditar.setDraggable(false);
        winEditar.setPositionX(event.getClientX() - event.getRelativeX());
        winEditar.setPositionY(event.getClientY() - event.getRelativeY());
        winEditar.setCloseShortcut(KeyCode.ESCAPE, null);

        
        Button bt1 = new Button("Editar Grupo",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
						
				
				GrupoEditor grupoEditor = new GrupoEditor(tbGrupo.getItem(tbGrupo.getValue()), "Editar Grupo", true);
				grupoEditor.addListerner(new GrupoEditor.GrupoListerner() {
					
					@Override
					public void onClose(GrupoEvent event) {
						if(event.isConfirm()){
							containerGrupo.commit();
							containerGrupo.refresh();
							
							Notify.Show("Grupo Editado com Sucesso!", Notify.TYPE_SUCCESS);
						}
					}
				});
				
				getUI().addWindow(grupoEditor);
				
							
			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        if(tbGrupo.getValue() != null){
        	bt1.setEnabled(true);
        }else{
        	bt1.setEnabled(false);
        }
        
        Button bt2 = new Button("Editar SubGrupo",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
								
				SubGrupoEditor SubGrupoEditor = new SubGrupoEditor(tbSubGrupo.getItem(tbSubGrupo.getValue()), "Editar SubGrupo", true);
				SubGrupoEditor.addListerner(new SubGrupoEditor.SubGrupoListerner() {
					
					@Override
					public void onClose(SubGrupoEvent event) {
						if(event.isConfirm()){
							containerSubGrupo.commit();
							containerSubGrupo.refresh();
							
							Notify.Show("SubGrupo Editado com Sucesso!", Notify.TYPE_SUCCESS);
						}
					}
				});
				
				getUI().addWindow(SubGrupoEditor);	
			}
		});
        bt2.setPrimaryStyleName("btSubMenu");
        
        if(tbSubGrupo.getValue() != null){
        	bt2.setEnabled(true);
        }else{
        	bt2.setEnabled(false);
        }
        
        Button bt3 = new Button("Editar Tipo SubGrupo",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
				TipoGrupoEditor TipoSubGrupoEditor = new TipoGrupoEditor(tbTipoSubGrupo.getItem(tbTipoSubGrupo.getValue()), "Editar Tipo SubGrupo", true);
				TipoSubGrupoEditor.addListerner(new TipoGrupoEditor.TipoSubGrupoListerner() {
					
					@Override
					public void onClose(TipoSubGrupoEvent event) {
						if(event.isConfirm()){
							containerTipo.commit();
							containerTipo.refresh();
							
							Notify.Show("Tipo SubGrupo Editado com Sucesso!", Notify.TYPE_SUCCESS);
						}
					}
				});
				
				getUI().addWindow(TipoSubGrupoEditor);				
							
			}
		});
        bt3.setPrimaryStyleName("btSubMenu");
        
        if(tbTipoSubGrupo.getValue() != null){
        	bt3.setEnabled(true);
        }else{
        	bt3.setEnabled(false);
        }
        
       
        
        l.addComponent(bt1);
        l.addComponent(bt2);
        l.addComponent(bt3);
      
       
    }
	
	Window winNovo;
	private void buildSubMenuNovo(ClickEvent event) {
		winNovo = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winNovo.setContent(l);
        winNovo.setWidth("300px");
        winNovo.addStyleName("notifications");
        winNovo.setClosable(false);
        winNovo.setResizable(false);
        winNovo.setDraggable(false);
        winNovo.setPositionX(event.getClientX() - event.getRelativeX());
        winNovo.setPositionY(event.getClientY() - event.getRelativeY());
        winNovo.setCloseShortcut(KeyCode.ESCAPE, null);

        
        Button bt1 = new Button("Novo Grupo",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
								
				final BeanItem<GrupoOse> item = new BeanItem<GrupoOse>(new GrupoOse());
				GrupoEditor grupoEditor = new GrupoEditor(item, "Novo Grupo", true);
				grupoEditor.addListerner(new GrupoEditor.GrupoListerner() {
					
					@Override
					public void onClose(GrupoEvent event) {
						if(event.isConfirm()){
							
							if(containerGrupo == null){
								getContainerGrupo();
							}
							
							GrupoOse grupo = item.getBean();
							grupo.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
							containerGrupo.addEntity(grupo);
							containerGrupo.commit();
							containerGrupo.refresh();
							
							Notify.Show("Grupo Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
						}
					}
				});
				
				getUI().addWindow(grupoEditor);				
			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        
        Button bt2 = new Button("Novo SubGrupo",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
								
				final BeanItem<SubGrupoOse> item = new BeanItem<SubGrupoOse>(new SubGrupoOse());
				SubGrupoEditor SubGrupoEditor = new SubGrupoEditor(item, "Novo SubGrupo", true);
				SubGrupoEditor.addListerner(new SubGrupoEditor.SubGrupoListerner() {
					
					@Override
					public void onClose(SubGrupoEvent event) {
						if(event.isConfirm()){
							
							if(containerSubGrupo == null){
								getContainerSubGrupo();
							}
							
							SubGrupoOse grupo = item.getBean();
							containerSubGrupo.addEntity(grupo);
							containerSubGrupo.commit();
							containerSubGrupo.refresh();
							
							Notify.Show("SubGrupo Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
						}
					}
				});
				
				getUI().addWindow(SubGrupoEditor);			
			}
		});
        bt2.setPrimaryStyleName("btSubMenu");
        
        Button bt3 = new Button("Novo Tipo SubGrupo",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
				final BeanItem<TipoSubGrupoOse> item = new BeanItem<TipoSubGrupoOse>(new TipoSubGrupoOse());
				TipoGrupoEditor SubGrupoEditor = new TipoGrupoEditor(item, "Novo Tipo SubGrupo", true);
				SubGrupoEditor.addListerner(new TipoGrupoEditor.TipoSubGrupoListerner() {
					
					@Override
					public void onClose(TipoSubGrupoEvent event) {
						if(event.isConfirm()){
							
							if(containerTipo == null){
								getContainerTipoSubGrupo();
							}
							
							TipoSubGrupoOse TipoSubGrupo = item.getBean();
							TipoSubGrupo.setData_cadastro(new Date());
							containerTipo.addEntity(TipoSubGrupo);
							containerTipo.commit();
							containerTipo.refresh();
							
							
							Notify.Show("Tipo SubGrupo Cadastrado com Sucesso!", Notify.TYPE_SUCCESS);
						}
					}
				});
				
				getUI().addWindow(SubGrupoEditor);			
							
			}
		});
        bt3.setPrimaryStyleName("btSubMenu");
        
       
        
        l.addComponent(bt1);
        l.addComponent(bt2);
        l.addComponent(bt3);
      
       
    }
	
	
	Window winExcluir;
	private void buildSubMenuExcluir(ClickEvent event) {
		winExcluir = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winExcluir.setContent(l);
        winExcluir.setWidth("300px");
        winExcluir.addStyleName("notifications");
        winExcluir.setClosable(false);
        winExcluir.setResizable(false);
        winExcluir.setDraggable(false);
        winExcluir.setPositionX(event.getClientX() - event.getRelativeX());
        winExcluir.setPositionY(event.getClientY() - event.getRelativeY());
        winExcluir.setCloseShortcut(KeyCode.ESCAPE, null);

        
        Button bt1 = new Button("Excluir Grupo",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Deseja Realmente Excluir este Grupo?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						if(event.isConfirm()){
							if(GrupoOseDAO.allowRemove((Integer)tbGrupo.getItem(tbGrupo.getValue()).getItemProperty("id").getValue())){	
								
								GrupoOseDAO.remove((Integer)tbGrupo.getItem(tbGrupo.getValue()).getItemProperty("id").getValue());
								containerGrupo.refresh();
								
								Notify.Show("Grupo Excluído com Sucesso!", Notify.TYPE_SUCCESS);
							}else{
								Notify.Show("Não é Possivel Excluir um Grupo que Tenha SubGrupos", Notify.TYPE_ERROR);
							}
						}							
					}
				});
				
				getUI().addWindow(gd);
				
				
				
			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        if(tbGrupo.getValue() != null){
        	bt1.setEnabled(true);
        }else{
        	bt1.setEnabled(false);
        }
        
        Button bt2 = new Button("Excluir SubGrupo",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
				GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Deseja Realmente Excluir este SubGrupo?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						if(event.isConfirm()){
							if(SubGrupoDAO.allowRemove((Integer)tbSubGrupo.getItem(tbSubGrupo.getValue()).getItemProperty("id").getValue())){	
								
								SubGrupoDAO.remove((Integer)tbSubGrupo.getItem(tbSubGrupo.getValue()).getItemProperty("id").getValue());
								containerSubGrupo.refresh();
								
								Notify.Show("SubGrupo Excluído com Sucesso!", Notify.TYPE_SUCCESS);
							}else{
								Notify.Show("Não é Possivel Excluir um SubGrupo que Tenha Tipo de SubGrupos", Notify.TYPE_ERROR);
							}
						}							
					}
				});
				
				getUI().addWindow(gd);				
								
			}
		});
        bt2.setPrimaryStyleName("btSubMenu");
        if(tbSubGrupo.getValue() != null){
        	bt2.setEnabled(true);
        }else{
        	bt2.setEnabled(false);
        }
        
        Button bt3 = new Button("Excluir Tipo SubGrupo",new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				
								
				GenericDialog gd = new GenericDialog("Confirme para Continuar!", "Deseja Realmente Excluir este Tipo SubGrupo?", true, true);
				gd.addListerner(new GenericDialog.DialogListerner() {
					
					@Override
					public void onClose(DialogEvent event) {
						if(event.isConfirm()){
																
							TipoSubGrupoDAO.remove((Integer)tbTipoSubGrupo.getItem(tbTipoSubGrupo.getValue()).getItemProperty("id").getValue());
							containerTipo.refresh();
							
							Notify.Show("Tipo SubGrupo Excluído com Sucesso!", Notify.TYPE_SUCCESS);
							
						}							
					}
				});
				
				getUI().addWindow(gd);						
			}
		});
        bt3.setPrimaryStyleName("btSubMenu");
        if(tbTipoSubGrupo.getValue() != null){
        	bt3.setEnabled(true);
        }else{
        	bt3.setEnabled(false);
        }
        
       
        
        l.addComponent(bt1);
        l.addComponent(bt2);
        l.addComponent(bt3);
      
       
    }
	
	
	
	
	private Component BuildbtExcluir() {
		btExcluir = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir"))				
				{
					if (winExcluir != null && winExcluir.getUI() != null)
						winExcluir.close();
					else {
						
						if(winEditar != null && winEditar.getUI() != null){
							winEditar.close();
						}
						
						if(winNovo != null && winNovo.getUI() != null){
							winNovo.close();
						}
						
						buildSubMenuExcluir(event);
						
						getUI().addWindow(winExcluir);
						winExcluir.focus();
						((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
							
							@Override
							public void layoutClick(LayoutClickEvent event) {
								winExcluir.close();
								((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
							}
						});
					}
				}else{
					Notify.Show("Você não Possui Permissão para Excluir",Notify.TYPE_ERROR);
				}		
				
			}
		});
		
		return btExcluir;
	}
	
	private Button BuildBtLimiteMensal(){
		btLimiteMensal = new Button("Limite Mensal", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		btLimiteMensal.setEnabled(false); 
		return btLimiteMensal;
	}
	
	private Button BuildbtLimiteDiario() {
		btLimiteDiario = new Button("Limite Diário", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Limite Diario"))				
				{
					
					final Window win  = new Window("Limite Diário");
					win.setWidth("245px");
					win.setHeight("139px");
					
					win.setModal(true);
					win.center();
					
					final VerticalLayout vlRoot = new VerticalLayout();
					EntityItem<GrupoOse> entityItemGrupo = (EntityItem<GrupoOse>)tbGrupo.getItem(tbGrupo.getValue()); 
					final GrupoOse grupo = entityItemGrupo.getEntity();
					final TextField txtQtd = new TextField ("Quantidade");
					
					vlRoot.addComponent(
							new FormLayout(){					
							{
								setStyleName("form-cutom");
								setMargin(true);
								setSpacing(true);
												
								txtQtd.setWidth("103px");							
								txtQtd.setRequired(true);		
								if(grupo != null && grupo.getLimite_diario() != null){
									txtQtd.setValue(grupo.getLimite_diario().toString());
								}else{
									txtQtd.setValue("0");
								}
								txtQtd.focus();
								addComponent(txtQtd);						
							}
						});
					
					
					win.setContent(new VerticalLayout(){
						{
							setWidth("100%");
							setMargin(true);
							addComponent(vlRoot);
							
							
							HorizontalLayout hlButtons = new HorizontalLayout();
							hlButtons.setStyleName("hl_buttons_bottom");
							hlButtons.setSpacing(true);
							hlButtons.setMargin(true);
							hlButtons.addComponent(new Button("Cancelar", new Button.ClickListener() {
								
								@Override
								public void buttonClick(ClickEvent event) {
									win.close();
								}
							}));
							
							Button btOk = new Button("OK", new Button.ClickListener() {
								
								@Override
								public void buttonClick(ClickEvent event) {
									grupo.setLimite_diario(Integer.parseInt(txtQtd.getValue()));
									//containerGrupo.com
									GrupoOseDAO.save(grupo);
									Notify.Show("Limite Diário Salvo com Sucesso!", Notify.TYPE_SUCCESS);
									win.close();
								}
							});
							btOk.addStyleName("default");
							hlButtons.addComponent(btOk);
							
							addComponent(hlButtons);
							setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
						}
					});
					
					getUI().addWindow(win);
				}else{
					Notify.Show("Você não Possui Permissão para Configurar Limite Diário",Notify.TYPE_ERROR);
				}
			}
			
			
			
			
			
		});
		
		btLimiteDiario.setEnabled(false); 
		return btLimiteDiario;
	}
	
	
	private Component BuildbtEditar() {
		btEditar = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Editar"))				
				{
					if (winEditar != null && winEditar.getUI() != null)
						winEditar.close();
					else {
						
						if(winExcluir != null && winExcluir.getUI() != null){
							winExcluir.close();
						}
						
						if(winNovo != null && winNovo.getUI() != null){
							winNovo.close();
						}
						
						buildSubMenuEditar(event);
						
						getUI().addWindow(winEditar);
						winEditar.focus();
						((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
							
							@Override
							public void layoutClick(LayoutClickEvent event) {
								winEditar.close();
								((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
							}
						});
					}		
				}else{
					Notify.Show("Você não Possui Permissão para Editar",Notify.TYPE_ERROR);
				}
				
			}
		});
		
		return btEditar;
	}
	
	private Component BuildbtNovo() {
		btNovo = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Novo"))				
				{
					if (winNovo != null && winNovo.getUI() != null)
						winNovo.close();
					else {
						
						if(winExcluir != null && winExcluir.getUI() != null){
							winExcluir.close();
						}
						if(winEditar != null && winEditar.getUI() != null){
							winEditar.close();
						}
						
						
						
						buildSubMenuNovo(event);
						
						getUI().addWindow(winNovo);
						winNovo.focus();
						((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
							
							@Override
							public void layoutClick(LayoutClickEvent event) {
								winNovo.close();
								((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
							}
						});
					}		
				}else{
					Notify.Show("Você não Possui Permissão para Cadastrar",Notify.TYPE_ERROR);
				}
				
			}
		});
		
		return btNovo;
	}
	
	
	private VerticalLayout buildLayoutGrupo(){
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(BuildbtNovo());
		hlButons.addComponent(BuildbtEditar());
		hlButons.addComponent(BuildbtLimiteDiario());
		hlButons.addComponent(BuildBtLimiteMensal());
		hlButons.addComponent(BuildbtExcluir());
				
		vl.addComponent(hlButons);
		vl.setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		vl.addComponent(buildTbGrupo());
		vl.addComponent(buildTbSubGrupo());
		vl.addComponent(buildTbTipoSubGrupo());
		vl.setExpandRatio(tbTipoSubGrupo, 1);
		
		return vl;
	}
	
	private VerticalLayout buildLayoutChecklistEquipamento(){
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(buildBtNovoChkListEquipamento());
		hlButons.addComponent(buildBtEditarChkListEquipamento());
		hlButons.addComponent(buildBtExcluirChkListEquipamento());	
		
		
		vl.addComponent(hlButons);
		vl.setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		vl.addComponent(buildTbCheckListEquipamento());
		vl.setExpandRatio(tbCheckListEquipamento, 1);
		
		return vl;
	}
	
	Button btNovoChkListEquipamento;
	private Button buildBtNovoChkListEquipamento(){
		btNovoChkListEquipamento = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		return btNovoChkListEquipamento;
	}
	
	Button btEditarChkListEquipamento;
	private Button buildBtEditarChkListEquipamento(){
		btEditarChkListEquipamento = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		return btEditarChkListEquipamento;
	}
	
	Button btExcluirChkListEquipamento;
	private Button buildBtExcluirChkListEquipamento(){
		btExcluirChkListEquipamento = new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		btExcluirChkListEquipamento.setEnabled(false); 
		return btExcluirChkListEquipamento;
	}
	
	
	
	private VerticalLayout buildLayoutChecklistLocalizacao(){
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(buildBtNovoChkListLocalizacao());
		hlButons.addComponent(buildBtEditarChkListLocalizacao());
		hlButons.addComponent(buildBtExcluirChkListLocalizacao());

		vl.addComponent(hlButons);
		vl.setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		vl.addComponent(buildTbCheckListLocalizacao());		
		vl.setExpandRatio(tbCheckListLocalizacao, 1);
		
		return vl;
	}
	
	Button btNovoChkListLocalizacao;
	private Button buildBtNovoChkListLocalizacao(){
		btNovoChkListLocalizacao = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		return btNovoChkListLocalizacao;
	}
	
	Button btEditarChkListLocalizacao;
	private Button buildBtEditarChkListLocalizacao(){
		btEditarChkListLocalizacao = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		return btEditarChkListLocalizacao;
	}
	
	Button btExcluirChkListLocalizacao;
	private Button buildBtExcluirChkListLocalizacao(){
		btExcluirChkListLocalizacao= new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		btExcluirChkListLocalizacao.setEnabled(false); 
		return btExcluirChkListLocalizacao;
	}
	
	private VerticalLayout buildLayoutOutrosParametros(){
		VerticalLayout vl = new VerticalLayout();
				
		vl.addComponent(new FormLayout(){
			{
				
				final ConfigOse config = ConfigOrdemServicoDAO.getTipoItemDefault();
				
				JPAContainer<TipoItemProduto> container = JPAContainerFactory.makeReadOnly(TipoItemProduto.class, ConnUtil.getEntity());
				final ComboBox cb = new ComboBox("Tipo Item Produto", container);
				cb.setNullSelectionAllowed(false); 
				cb.setItemCaptionPropertyId("nome");
				cb.setConverter(new SingleSelectConverter(cb));
				
				if(config != null){
					for (Object item: container.getItemIds()) {
						
						if(config.getTipo_item_default().getId() == (Integer)container.getItem(item).getItemProperty("id").getValue()){
							cb.select(item);
							break;
						}					
					}
				}
				
				addComponent(cb); 
				
				Button bt = new Button("Salvar");
				bt.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						if(cb.getValue() != null){
							EntityItem<TipoItemProduto> item = (EntityItem<TipoItemProduto>)cb.getItem(cb.getValue());
							config.setTipo_item_default(item.getEntity());
							
							ConfigOrdemServicoDAO.save(config);
							
							Notify.Show("Parametro(s) salvo(s) com sucesso!", Notify.TYPE_SUCCESS);
						}
					}
				});
				addComponent(bt);
				
			}
		});
		
		
				
		return vl;
	}
	
	
	private VerticalLayout buildLayoutChecklistInstalacao(){
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		
		HorizontalLayout hlButons = new HorizontalLayout();
		hlButons.addComponent(buildBtNovoChkListInstalacao());
		hlButons.addComponent(buildBtEditarChkListInstalacao());
		hlButons.addComponent(buildBtExcluirChkListInstalacao());	
		
		vl.addComponent(hlButons);
		vl.setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
		
		vl.addComponent(buildTbCheckListInstalacao());
		vl.setExpandRatio(tbCheckListInstalacao, 1);
		
		return vl;
	}
	
	Button btNovoChkListInstalacao;
	private Button buildBtNovoChkListInstalacao(){
		btNovoChkListInstalacao = new Button("Novo", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		return btNovoChkListInstalacao;
	}
	
	Button btEditarChkListInstalacao;
	private Button buildBtEditarChkListInstalacao(){
		btEditarChkListInstalacao = new Button("Editar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		return btEditarChkListInstalacao;
	}
	
	Button btExcluirChkListInstalacao;
	private Button buildBtExcluirChkListInstalacao(){
		btExcluirChkListInstalacao= new Button("Excluir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		btExcluirChkListInstalacao.setEnabled(false); 
		return btExcluirChkListInstalacao;
	}
	
	Table tbGrupo;
	private Table buildTbGrupo(){
		tbGrupo = new Table(null, getContainerGrupo());
		tbGrupo.setSelectable(true);
		tbGrupo.setHeight("150px");
		tbGrupo.setWidth("100%");
		tbGrupo.setVisibleColumns(new Object[]{"nome","limite_diario","status"});
		tbGrupo.setColumnHeader("nome", "Grupo");
		tbGrupo.setColumnHeader("limite_diario", "Limite Diário");
		tbGrupo.setColumnHeader("status", "Status");
		
		tbGrupo.setImmediate(true);
		tbGrupo.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				tbSubGrupo.setContainerDataSource(getContainerSubGrupo());	
				tbTipoSubGrupo.setContainerDataSource(null);
				
				if(tbGrupo.getValue() != null){
					tbSubGrupo.setVisibleColumns(new Object[]{"nome","prioridade","gerar_crm","status"});
					tbSubGrupo.setColumnHeader("nome", "SubGrupo");
					tbSubGrupo.setColumnHeader("prioridade", "Prioridade");
					tbSubGrupo.setColumnHeader("gerar_crm", "Gerar CRM");
					tbSubGrupo.setColumnHeader("status", "Status");
					btLimiteDiario.setEnabled(true);
				}else{
					btLimiteDiario.setEnabled(false);
				}
			}
		});
							
			
		return tbGrupo;
	}
	
	JPAContainer<ChecklistOsEquipamento> containerCheckListEquipamento;
	private JPAContainer<ChecklistOsEquipamento> getContainerChecklistOsEquipamento(){
		containerCheckListEquipamento = JPAContainerFactory.make(ChecklistOsEquipamento.class, ConnUtil.getEntity());
		containerCheckListEquipamento.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		return containerCheckListEquipamento;
	}
	
	Table tbCheckListEquipamento;
	private Table buildTbCheckListEquipamento(){
		tbCheckListEquipamento = new Table(null, getContainerChecklistOsEquipamento());
		tbCheckListEquipamento.setSelectable(true);
		tbCheckListEquipamento.setSizeFull();
		tbCheckListEquipamento.setVisibleColumns(new Object[]{"nome"});
		tbCheckListEquipamento.setColumnHeader("nome", "NOME");			
		tbCheckListEquipamento.setImmediate(true);						
			
		return tbCheckListEquipamento;
	}
	
	JPAContainer<ChecklistOsLocalizacao> containerCheckListLocalizacao;
	private JPAContainer<ChecklistOsLocalizacao> getContainerCheckListLocalizacao(){
		containerCheckListLocalizacao = JPAContainerFactory.make(ChecklistOsLocalizacao.class, ConnUtil.getEntity());
		containerCheckListLocalizacao.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		return containerCheckListLocalizacao;
	}
	
	Table tbCheckListLocalizacao;
	private Table buildTbCheckListLocalizacao(){
		tbCheckListLocalizacao = new Table(null, getContainerCheckListLocalizacao());
		tbCheckListLocalizacao.setSelectable(true);
		tbCheckListLocalizacao.setSizeFull();
		tbCheckListLocalizacao.setVisibleColumns(new Object[]{"nome"});
		tbCheckListLocalizacao.setColumnHeader("nome", "NOME");			
		tbCheckListLocalizacao.setImmediate(true);						
			
		return tbCheckListLocalizacao;
	}
	
	JPAContainer<ChecklistOsInstalacao> containerCheckListInstalacao;
	private JPAContainer<ChecklistOsInstalacao> getContainerCheckListInstalacao(){
		containerCheckListInstalacao = JPAContainerFactory.make(ChecklistOsInstalacao.class, ConnUtil.getEntity());
		containerCheckListInstalacao.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		return containerCheckListInstalacao;
	}
	
	Table tbCheckListInstalacao;
	private Table buildTbCheckListInstalacao(){
		tbCheckListInstalacao = new Table(null, getContainerCheckListInstalacao());
		tbCheckListInstalacao.setSelectable(true);
		tbCheckListInstalacao.setSizeFull();
		tbCheckListInstalacao.setVisibleColumns(new Object[]{"nome"});
		tbCheckListInstalacao.setColumnHeader("nome", "NOME");			
		tbCheckListInstalacao.setImmediate(true);						
			
		return tbCheckListInstalacao;
	}
	
	Table tbSubGrupo;
	private Table buildTbSubGrupo(){
		tbSubGrupo = new Table(null,null);
		tbSubGrupo.setSelectable(true);
		tbSubGrupo.setHeight("150px");
		tbSubGrupo.setWidth("100%");
		
		
		tbSubGrupo.setImmediate(true);
		tbSubGrupo.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				tbTipoSubGrupo.setContainerDataSource(getContainerTipoSubGrupo());		
				
				if(tbSubGrupo.getValue() != null){
					tbTipoSubGrupo.setVisibleColumns(new Object[]{"nome","valor","status"});
					tbTipoSubGrupo.setColumnHeader("nome", "Tipo");
					tbTipoSubGrupo.setColumnHeader("valor", "Valor");
					tbTipoSubGrupo.setColumnHeader("status", "Status");
					tbTipoSubGrupo.setColumnAlignment("valor", Align.RIGHT);
					
					btLimiteMensal.setEnabled(true);
				}else{
					btLimiteMensal.setEnabled(false); 
				}
			}
		});
		
		return tbSubGrupo;
	}
	
	Table tbTipoSubGrupo;
	private Table buildTbTipoSubGrupo(){
		tbTipoSubGrupo = new Table(null,null){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("valor")){
					
					if(tbTipoSubGrupo.getItem(rowId).getItemProperty(colId).getValue() != null){
						return "R$ "+Real.formatDbToString(tbTipoSubGrupo.getItem(rowId).getItemProperty(colId).getValue().toString());
					}else{
						return "";
					}
				}else{
					return super.formatPropertyValue(rowId, colId, property);
				}
			}
		};
		tbTipoSubGrupo.setSelectable(true);
		tbTipoSubGrupo.setSizeFull();
		
		
		
		
		return tbTipoSubGrupo;
	}
	
	JPAContainer<GrupoOse> containerGrupo;
	private JPAContainer<GrupoOse> getContainerGrupo(){
		containerGrupo = JPAContainerFactory.make(GrupoOse.class, ConnUtil.getEntity());
		containerGrupo.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		return containerGrupo;
	}
	
	JPAContainer<SubGrupoOse> containerSubGrupo;
	private JPAContainer<SubGrupoOse> getContainerSubGrupo(){
		
		containerSubGrupo = JPAContainerFactory.make(SubGrupoOse.class, ConnUtil.getEntity());
			
		if(tbGrupo.getValue() != null){
			containerSubGrupo.addContainerFilter(Filters.eq("grupo", GrupoOseDAO.find((Integer)tbGrupo.getItem(tbGrupo.getValue()).getItemProperty("id").getValue())));			
			return containerSubGrupo;
		}else{
			return null;
		}
	}
	
	JPAContainer<TipoSubGrupoOse> containerTipo;
	private JPAContainer<TipoSubGrupoOse> getContainerTipoSubGrupo(){
		
		containerTipo = JPAContainerFactory.make(TipoSubGrupoOse.class, ConnUtil.getEntity());	
			
		if(tbSubGrupo.getValue() != null){
			containerTipo.addContainerFilter(Filters.eq("subgrupo_id",SubGrupoDAO.find((Integer)tbSubGrupo.getItem(tbSubGrupo.getValue()).getItemProperty("id").getValue())));
			return containerTipo;
		}else{
			return null;
		}
	}
	
	
	public void refresh(){
		
	}
	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
}
