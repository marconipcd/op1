package com.digital.opuserp.view.modulos.pedido.rma;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.FornecedorDAO;
import com.digital.opuserp.dao.RmaDAO;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.RmaDetalhe;
import com.digital.opuserp.domain.RmaMestre;
import com.digital.opuserp.interfaces.GenericEditor;
import com.digital.opuserp.view.modulos.pedido.rma.RmaEditor.RmaEvent;
import com.digital.opuserp.view.util.FornecedorUtil;
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
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class RmaNew extends Window implements GenericEditor {

	private Button btFinalizar;
	private Button btCancelar;
	private Button btFechar;
	
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	
	private TextField txtDescricaoFornecedor;
	private TextField txtCodFornecedor;	
	private Fornecedor FornecedorSelecionado;	
	private TextField txtCodProduto;	
	private Produto ProdutoSelecionado;
	
	private TextArea txtDefeito;
	private DateField txtVencimento;
	private ComboBox cbStatus;
	private TextField txtNotaFiscal; 
	
	private Integer id_rma; 
	

	List<RmaDetalhe> itens = new ArrayList<>();
	List<RmaDetalhe> itens_editor = new ArrayList<>();
	RmaMestre rma;
	
	boolean editor;
	public RmaNew(String title, boolean modal, final boolean editor, final Integer id_rma){
		
		setWidth("892px");
		
		if(id_rma != null){
			rma = RmaDAO.findRMA(id_rma);
			FornecedorSelecionado = rma.getFornecedor();
		}
		
		this.editor = editor;
		this.id_rma = id_rma;
		
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
				
				
				if(!editor){
					hlButtons.addComponent(buildBtCancelar());
					hlButtons.addComponent(buildBtSalvar());
				}else{					
					hlButtons.addComponent(buildBtSalvar());
				}
				
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		
		
			//if(editor){
			//	buildLayoutEditor();				
			//}else{
				buildLayout();				
		//}		
	}
	
	
	
	Table tbItens;
	public void buildLayout(){
		
		
		vlRoot.addComponent(new HorizontalLayout(){
			{
									
				txtCodFornecedor = new TextField("Fornecedor");				
				txtCodFornecedor.setWidth("60px");				
				txtCodFornecedor.setNullRepresentation("");
				txtCodFornecedor.setStyleName("caption-align");
				txtCodFornecedor.setId("txtCodFornecedor");
				//txtCodFornecedor.focus();
				
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
				txtDescricaoFornecedor.setWidth("480px");
						
				final Button btSearchFornecedor = new Button();
				btSearchFornecedor.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchFornecedor.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchFornecedor.setTabIndex(300000);

				btSearchFornecedor.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						FornecedorUtil cUtil = new FornecedorUtil(true, true, true);
						cUtil.addListerner(new FornecedorUtil.FornecedorListerner() {
							
							@Override
							public void onSelected(FornecedorUtil.FornecedorEvent event) {
								
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
				
				if(FornecedorSelecionado != null){
					btSearchFornecedor.setEnabled(false);
				}

			}	
		});
		
		Button btAdicionarItem = new Button("Adicionar Item", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				final RmaEditor rmEditor = new RmaEditor("Adicionar Item", true, true);
				rmEditor.addListerner(new RmaEditor.RmaListerner() {

					@Override
					public void onClose(RmaEvent event) {
						if(event.isConfirm()){
							RmaDetalhe rma = event.getRma();
							tbItens.addItem(new Object[]{tbItens.getItemIds().size()+1, rma.getProduto().getNome(), rma.getNf_compra(),rma.getDefeito(), rma.getVenc(), rma.getStatus()}, tbItens.getItemIds().size()+1);
							
							if(!editor){
								itens.add(rma);
							}else{
								itens_editor.add(rma);
							}
							
							rmEditor.close();
						}
					}
				});
				
				getUI().addWindow(rmEditor);
			}
		});
		btAdicionarItem.setStyleName(Reindeer.BUTTON_SMALL);
	
		tbItens = new Table();
		tbItens.setSizeFull();
		
		tbItens.addContainerProperty("Cod", Integer.class,null);
		tbItens.addContainerProperty("Produto", String.class,"");
		tbItens.addContainerProperty("NF", String.class,"");
		tbItens.addContainerProperty("Defeito", String.class,"");
		tbItens.addContainerProperty("Vencimento", Date.class,null);
		tbItens.addContainerProperty("Status", String.class,"");
		
		tbItens.addGeneratedColumn("#", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source,final Object itemId, Object columnId) {
				Button btRemove = new Button("Remover", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {			
						
						for (Iterator<RmaDetalhe> i = itens.iterator(); i.hasNext();) {
							  RmaDetalhe item= i.next();
							  if(item.getId() == source.getItem(itemId).getItemProperty("Cod").getValue()){
							    i.remove();
							  }
						}
						Integer cod = (Integer)source.getItem(itemId).getItemProperty("Cod").getValue();
						
						if(cod != null){
							boolean check = RmaDAO.remover(cod);
						}
											
						source.removeItem(itemId);
						source.commit();
						
											
					}
				});
				btRemove.setStyleName(Reindeer.BUTTON_SMALL);
				
				return btRemove;
			}
		});
				
		vlRoot.addComponent(btAdicionarItem);
		vlRoot.addComponent(tbItens); 
		
		if(rma != null){
			itens = RmaDAO.getItensRma(rma.getId());
			for (RmaDetalhe rmaDetalhe : itens) {
				
				tbItens.addItem(new Object[]{rmaDetalhe.getId(), rmaDetalhe.getProduto().getNome(), rmaDetalhe.getNf_compra(), rmaDetalhe.getDefeito(), rmaDetalhe.getVenc(), rmaDetalhe.getStatus()}, tbItens.getItemIds().size()+1);
				
			}
		}
		
	}

	
	@Override
	public Button buildBtSalvar() {
		
		btFinalizar = new Button("Finalizar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(FornecedorSelecionado != null && tbItens.size() > 0){		
					if(!editor){
						fireEvent(new RmaNewEvent(getUI(),new RmaMestre(null, OpusERP4UI.getEmpresa().getId(), FornecedorSelecionado, "ABERTO", new Date()) , itens, true));
					}else{
						fireEvent(new RmaNewEvent(getUI(),new RmaMestre(null, OpusERP4UI.getEmpresa().getId(), FornecedorSelecionado, "ABERTO", new Date()) , itens_editor, true));
					}
				}else{				
					fireEvent(new RmaNewEvent(getUI(),null , null, true));
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
		
		if(editor){
			btFinalizar.setCaption("Salvar");
		}
		
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
	
	
	public void addListerner(RmaNewListerner target){
		try {
			Method method = RmaNewListerner.class.getDeclaredMethod("onClose", RmaNewEvent.class);
			addListener(RmaNewEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(RmaNewListerner target){
		removeListener(RmaNewEvent.class, target);
	}
	public static class RmaNewEvent extends Event{
		
		private RmaMestre rMestre;
		private List<RmaDetalhe> itens;
		private boolean confirm;
		
		public RmaNewEvent(Component source,RmaMestre rMestre, List<RmaDetalhe> itens, boolean confirm) {
			super(source);
			this.itens = itens;
			this.rMestre = rMestre;
			this.confirm = confirm;					
		}
		public List<RmaDetalhe> getItens(){
			return itens;
		}
		public RmaMestre getMestre(){
			return rMestre;
		}

		public boolean isConfirm() {
			return confirm;
		}		
	}
	public interface RmaNewListerner extends Serializable{
		public void onClose(RmaNewEvent event);
	}
	
	
	
}

