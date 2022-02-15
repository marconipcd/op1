package com.digital.opuserp.view.modulos.configuracoes.config_acesso.materiais;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.ConfigAcesso;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.GrupoProdutoUtil;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.GrupoProdutoUtil.GrupoUtilEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class ConfigMaterialAcessoEditor extends Window implements GenericEditor {
	
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	VerticalLayout vlRoot;
	
	private Integer codEmpresa;
	private Integer codMaterial;
	
	GrupoProduto grupoSelecionado;
	ConfigAcesso configAcesso;
	EntityManager em;
	
	public ConfigMaterialAcessoEditor(String title, boolean modal, Integer codEmpresa){
		
		this.codEmpresa = codEmpresa;
		
		em = ConnUtil.getEntity();
		
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
									
		vlRoot.addComponent(new HorizontalLayout(){
			{
				
				Query q = em.createQuery("select cnf from ConfigAcesso cnf where cnf.secao ='GRUPO_PRODUTO' and cnf.empresa_id =:codEmpresa", ConfigAcesso.class);
				q.setParameter("codEmpresa", codEmpresa);
				
				ConfigAcesso config = null;
				if(q.getResultList().size() == 1)
				{
					config = (ConfigAcesso)q.getSingleResult();
					configAcesso = config;					
				}
				
				
				boolean preencher = false;
				if(config != null){
					preencher = true;
				}
				
				final TextField tfCodigoGrupo = new TextField("Grupo de Material");				
				tfCodigoGrupo.setWidth("75px");				
				tfCodigoGrupo.setNullRepresentation("");
				tfCodigoGrupo.setStyleName("caption-align-fornecedores");
				tfCodigoGrupo.setRequired(true);		
				
				final TextField tfNomeGrupo = new TextField();
				tfNomeGrupo.setReadOnly(true);
				tfNomeGrupo.setWidth("290px");
				
				
				
				if(preencher){
					GrupoProduto grupo = em.find(GrupoProduto.class, Integer.parseInt(config.getValor()));
					
					if(grupo != null){
						tfCodigoGrupo.setValue(grupo.getId().toString());
						tfNomeGrupo.setReadOnly(false);
						tfNomeGrupo.setValue(grupo.getNome_grupo());
						tfNomeGrupo.setReadOnly(true);
					}
				}
				
				
				final Button btSearchCnae = new Button();
				btSearchCnae.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchCnae.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchCnae.setDescription("Pesquisar CNAE");
				btSearchCnae.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						
						GrupoProdutoUtil cUtil = new GrupoProdutoUtil(true, true, codEmpresa);
						cUtil.addListerner(new GrupoProdutoUtil.GrupoUtilListerner() {
							
							@Override
							public void onSelected(GrupoUtilEvent event) {
								if(event.getGrupo() != null){
									tfCodigoGrupo.setValue(event.getGrupo().getId().toString());
									tfNomeGrupo.setReadOnly(false);
									tfNomeGrupo.setValue(event.getGrupo().getNome_grupo());
									tfNomeGrupo.setReadOnly(true);
									grupoSelecionado = event.getGrupo();
									
									
									if(configAcesso != null){
										configAcesso.setValor(grupoSelecionado.getId().toString());
									}else{
										configAcesso = new ConfigAcesso(null, codEmpresa, "GRUPO_PRODUTO", grupoSelecionado.getId().toString());
									}
									
								}
								
							}
						});
						
						getUI().addWindow(cUtil);
						
					}
				});
							
				
				FormLayout frmCodigoCnae = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom");		
												
						addComponent(tfCodigoGrupo);							
					}
				};
				addComponent(frmCodigoCnae);
				
				
				FormLayout frmButtonSearchCnae =new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom_hide_error_cell");										
						addComponent(btSearchCnae);							
					}
				}; 
				
				
				FormLayout frmDescCnae = new FormLayout(){
					{
						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");							
						addComponent(tfNomeGrupo);							
					}
				}; 
				addComponent(frmButtonSearchCnae);
				addComponent(frmDescCnae);
				setExpandRatio(frmDescCnae, 1);
				
				
				
			}	
		});
		
	

		
		
	}

	@Override
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(configAcesso != null){
					try {										
						
						em.getTransaction().begin();
						
						if(configAcesso.getId() != null){
							em.merge(configAcesso);
						}else{
							em.persist(configAcesso);
						}
						
						em.getTransaction().commit();
						
						
						Notify.Show("Material Definido com Sucesso!", Notify.TYPE_SUCCESS);
						
						close();
					} catch (Exception e) {					
					
						e.printStackTrace();
						Notify.Show("Não foi Possivel Salvar as Alterações!", Notify.TYPE_ERROR);
					}
				}else{
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
	
	
	

	
}
