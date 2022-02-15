package com.digital.opuserp.view.modulos.ordemServico.grupoServico;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.domain.GrupoServico;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.modulos.ordemServico.servicos.ServicoEditor.ServicoEvent;
import com.digital.opuserp.view.modulos.ordemServico.servicos.ServicoEditor.ServicoListerner;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;

public class GrupoServicoEditor extends Window implements GenericEditor {
	
	Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private TextField txtValorCusto;
	private TextField txtMargemLucro;
	private TextField txtPrecoVenda;
	private TextField txtValorLucro;
	private ComboBox cbFracionar;
	
	public GrupoServicoEditor(Item item, String title, boolean modal){
		this.item = item;
		
		setWidth("620px");
		
		configLayout();	
		
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
	
	
	
	
	private void configLayout(){
		////1366x768
		//if(OpusERP4UI.browser.getScreenWidth() >= 1024 && OpusERP4UI.browser.getScreenHeight() >= 768){
				setWidth("528px");
				setHeight("389px");		
		//}
	}
	
	
	
	public void buildLayout(){
		
		
		if(item.getItemProperty("valor_venda").getValue() != null){			
			item.getItemProperty("valor_venda").setValue(Real.formatDbToString(item.getItemProperty("valor_venda").getValue().toString()));
		}
		
		if(item.getItemProperty("valor_custo").getValue() != null){			
			item.getItemProperty("valor_custo").setValue(Real.formatDbToString(item.getItemProperty("valor_custo").getValue().toString()));
		}
		
		fieldGroup = new FieldGroup(item);
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");								
					
									
					TextField txtCod = new TextField("Cod.");				
					txtCod.setWidth("170px");				
					txtCod.setStyleName("caption-align-servico");
					txtCod.setNullRepresentation("");
					txtCod.focus();
					txtCod.setRequired(true);
					txtCod.setMaxLength(200);
					
					
					addComponent(txtCod);
					
					if(item.getItemProperty("id").getValue() != null){
						txtCod.setValue(item.getItemProperty("id").getValue().toString());
					}else{
						txtCod.setValue(ServicoDAO.getNextID());
					}
					
					txtCod.setReadOnly(true);
					//fieldGroup.bind(txtCod, "id");
										
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");								
					
									
					TextField txtNome = (TextField)fieldGroup.buildAndBind("Nome", "nome");				
					txtNome.setWidth("100%");				
					txtNome.setStyleName("caption-align-servico");
					txtNome.setNullRepresentation("");
					txtNome.setRequired(true);
					txtNome.setMaxLength(200);
					txtNome.focus();
					
					
					addComponent(txtNome);
					fieldGroup.bind(txtNome, "nome");
										
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");								
					
									
					TextArea txtDescricao = new TextArea("Descrição");		
					txtDescricao.setWidth("100%");	
					txtDescricao.setHeight("60px");
					txtDescricao.setStyleName("caption-align-servico");
					txtDescricao.setNullRepresentation("");
					txtDescricao.setRequired(false);
					txtDescricao.setMaxLength(200);
					
					
					addComponent(txtDescricao);
					fieldGroup.bind(txtDescricao, "descricao");
										
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");								
					
									
					txtValorCusto = (TextField)fieldGroup.buildAndBind("Valor Custo (R$)", "valor_custo");				
					txtValorCusto.setWidth("95px");				
					txtValorCusto.setStyleName("caption-align-servico");
					txtValorCusto.addStyleName("align-currency");
					txtValorCusto.setNullRepresentation("");
					txtValorCusto.setRequired(true);
					txtValorCusto.setMaxLength(200);
					txtValorCusto.setId("txtValorCusto");
				
					
					addComponent(txtValorCusto);				
					
					JavaScript.getCurrent().execute("$('#txtValorCusto').maskMoney({decimal:',',thousands:'.'})");		
					
				}
		});
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");								
					
									
					txtPrecoVenda = (TextField)fieldGroup.buildAndBind("Valor Venda (R$)", "valor_venda");				
					txtPrecoVenda.setWidth("95px");				
					txtPrecoVenda.setStyleName("caption-align-servico");
					txtPrecoVenda.addStyleName("align-currency");
					txtPrecoVenda.setNullRepresentation("");
					txtPrecoVenda.setRequired(true);
					txtPrecoVenda.setMaxLength(200);
					txtPrecoVenda.setId("txtPrecoVenda");
					
					addComponent(txtPrecoVenda);
					JavaScript.getCurrent().execute("$('#txtPrecoVenda').maskMoney({decimal:',',thousands:'.'})");					
				}
		});
		
		
	
		vlRoot.addComponent(
				new FormLayout(){					
				{					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					ComboBox cbGrupo = new ComboBox("Grupo", getGruposServicos());
					cbGrupo.setItemCaptionPropertyId("nome");
					cbGrupo.setStyleName("caption-align-servico");
					cbGrupo.setNullSelectionAllowed(false);					
					cbGrupo.setRequired(true);
					cbGrupo.setWidth("220px");
					cbGrupo.setConverter(new SingleSelectConverter(cbGrupo));					
					
					addComponent(cbGrupo);
					fieldGroup.bind(cbGrupo, "grupo");
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					cbFracionar = new ComboBox("Fracionar");							
					cbFracionar.setStyleName("caption-align-servico");
					cbFracionar.setNullSelectionAllowed(false);					
					cbFracionar.setRequired(true);
					cbFracionar.addItem("SIM");
					cbFracionar.addItem("NAO");
					
					addComponent(cbFracionar);
					
					
					if(item.getItemProperty("fracionar").getValue() != null && item.getItemProperty("fracionar").getValue().toString().equals("1")){
						cbFracionar.select("SIM");
					}else{
						cbFracionar.select("NAO");
					}
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{					
					setMargin(true);
					setSpacing(true);
					setStyleName("form-cutom");						
									
					ComboBox cbStatus = new ComboBox("Status");							
					cbStatus.setStyleName("caption-align-servico");
					cbStatus.setNullSelectionAllowed(false);					
					cbStatus.setRequired(true);
					cbStatus.addItem("ATIVO");
					cbStatus.addItem("INATIVO");
					
					cbStatus.select("ATIVO");
					
					addComponent(cbStatus);
					fieldGroup.bind(cbStatus, "status");
				}
		});
		
		
		
		
		
		
				
		
	}
	
	private JPAContainer<GrupoServico> getGruposServicos(){
		JPAContainer<GrupoServico> container = JPAContainerFactory.make(GrupoServico.class, ConnUtil.getEntity());
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa()));
		
		return container;
	}

	@Override
	public Button buildBtSalvar() {
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid()){
					try {
						
						
						
						if(cbFracionar.getValue().toString().equals("SIM")){
							item.getItemProperty("fracionar").setValue(1);
						}else{
							item.getItemProperty("fracionar").setValue(0);
						}							

						fieldGroup.commit();					
						item.getItemProperty("valor_venda").setValue(Real.formatStringToDB(item.getItemProperty("valor_venda").getValue().toString()));
						item.getItemProperty("valor_custo").setValue(Real.formatStringToDB(item.getItemProperty("valor_custo").getValue().toString()));
						
						fireEvent(new ServicoEvent(getUI(), item, true));						
						
					} catch (CommitException e) {
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);

						e.printStackTrace();
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
		btSalvar.setStyleName("default");
		
		ShortcutListener clTb = new ShortcutListener("Salvar", ShortcutAction.KeyCode.ENTER, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		btSalvar.addShortcutListener(clTb);		
		return btSalvar;
	}

	@Override
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new ServicoEvent(getUI(), item, false));
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
											
											
											
											if(cbFracionar.getValue().toString().equals("SIM")){
												item.getItemProperty("fracionar").setValue(1);
											}else{
												item.getItemProperty("fracionar").setValue(0);
											}							

											fieldGroup.commit();					
											item.getItemProperty("valor_venda").setValue(Real.formatStringToDB(item.getItemProperty("valor_venda").getValue().toString()));
											item.getItemProperty("valor_custo").setValue(Real.formatStringToDB(item.getItemProperty("valor_custo").getValue().toString()));
											
											fireEvent(new ServicoEvent(getUI(), item, true));						
											
										} catch (CommitException e) {
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
								}else{							
									fieldGroup.discard();				
									fireEvent(new ServicoEvent(getUI(), item, false));
									close();													
								}
							
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
			}
		});
		
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}
	
	
	public void addListerner(ServicoListerner target){
		try {
			Method method = ServicoListerner.class.getDeclaredMethod("onClose", ServicoEvent.class);
			addListener(ServicoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ServicoListerner target){
		removeListener(ServicoEvent.class, target);
	}
	public static class ServicoEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public ServicoEvent(Component source, Item item, boolean confirm) {
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
	public interface ServicoListerner extends Serializable{
		public void onClose(ServicoEvent event);
	}
	
}
