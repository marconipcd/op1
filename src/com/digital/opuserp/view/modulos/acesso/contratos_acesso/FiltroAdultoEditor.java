package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.FiltroAcesso;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class FiltroAdultoEditor extends Window{

	Item item;	
	Button btFechar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	JPAContainer<FiltroAcesso> containerFiltros;
	Integer codAcesso;

	public FiltroAdultoEditor(String title, boolean modal, Integer codAcesso){
		
		this.codAcesso = codAcesso;
		
		setWidth("590px");
		setHeight("330px");
		
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
				hlButtons.addComponent(buildBtFechar());
				//hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		buildLayout();
	}
	
	
	public void buildLayout(){
		
		fieldGroup = new FieldGroup(item);
					
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					setWidth("100%");
					
					CredenciaisAcessoDAO cDAO = new CredenciaisAcessoDAO();
					
					AcessoCliente ac = cDAO.getAcessoByCodAceso(codAcesso);	
					Cliente cliente = ac.getCliente();				
					
					TextField tfNomeCliente = new TextField("Cliente");
					tfNomeCliente.setValue(cliente.getNome_razao());
					tfNomeCliente.setReadOnly(true);
					tfNomeCliente.setWidth("98%");
					tfNomeCliente.setStyleName("caption-align-autocensura");
					
					addComponent(tfNomeCliente);
				}
		});		
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");		
					setMargin(false);
					setWidth("100%");
					
					final TextField tfConext = new TextField("");
					tfConext.setWidth("98%");
					tfConext.focus();
					tfConext.setStyleName("caption-align-autocensura");

															
					Button btAdd = new Button("Adicionar", new Button.ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							if(tfConext.getValue() != null && !tfConext.getValue().isEmpty() && !tfConext.getValue().equals("")){
								
								CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
								boolean check = caDAO.cadastrarFiltroAcesso(codAcesso, tfConext.getValue());
								
								if(check){
//									containerFiltros.addEntity(new FiltroAcesso(null, codAcesso, tfConext.getValue(), null));
								}else{									
									Notify.Show("Não Foi Possivel Cadastrar o Novo Filtro, talvez o Concentrador não Esteja Disponível", Notify.TYPE_ERROR);
								}
							}
						}
					});
					
					btAdd.addStyleName("default");
					
					HorizontalLayout hlButton = new HorizontalLayout();
					hlButton.setWidth("100%");
					
					hlButton.addComponent(tfConext);
					hlButton.addComponent(btAdd);
					hlButton.setComponentAlignment(btAdd, Alignment.BOTTOM_RIGHT);
					hlButton.setExpandRatio(tfConext, 2f);
					
					addComponent(hlButton);
					
				}
		});
		
		
		setStyleName("form-cutom-new");
		Table tbFiltros = new Table(null, buildContainerFiltros());		
		tbFiltros.setWidth("530px");
		tbFiltros.setHeight("120px");
		tbFiltros.setStyleName("caption-align-autocensura");
		tbFiltros.setImmediate(true);
		tbFiltros.setVisibleColumns(new Object[]{"palavra", "data_cadastro"});
		tbFiltros.setColumnHeader("palavra", "Palavra");
		tbFiltros.setColumnHeader("data_cadastro", "Data de Cadastro");
		tbFiltros.setColumnExpandRatio("palavra", 1f);
		
		tbFiltros.addGeneratedColumn("", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				
				
				
				Button btDeletar = new Button(null, new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Excluir este Filtro?", true,true);
						gd.addListerner(new GenericDialog.DialogListerner() {
							
							@Override
							public void onClose(DialogEvent event) {
								if(event.isConfirm()){
									
									CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
									boolean check = caDAO.removerFiltroAcesso(codAcesso, (String)source.getItem(itemId).getItemProperty("palavra").getValue());
									
									if(check){
										containerFiltros.removeItem(itemId);
//										containerFiltros.commit();
//										containerFiltros.refresh();
									}else{										
										Notify.Show("Não Foi Possivel Remover o Filtro, talvez o Concentrador não Esteja Disponível", Notify.TYPE_ERROR);
									}
								}
							}
						});
						
						getUI().addWindow(gd);
					}
				});
				btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
				btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
				btDeletar.setDescription("Remover Filtro");
				
				return btDeletar;
			}
		});
				
		vlRoot.addComponent(tbFiltros);
		
				
	}


	private JPAContainer<FiltroAcesso> buildContainerFiltros(){
		containerFiltros = JPAContainerFactory.make(FiltroAcesso.class, ConnUtil.getEntity());
		containerFiltros.setApplyFiltersImmediately(false);
		containerFiltros.addContainerFilter(new com.vaadin.data.util.filter.Compare.Equal("acesso_cliente_id", codAcesso));
		containerFiltros.applyFilters();
		
		return containerFiltros;		
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
		btFechar.setEnabled(true);
		
		return btFechar;
	}
	
		
}
