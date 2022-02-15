package com.digital.opuserp.view.modulos.ordemServico.ordemProducao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import com.digital.opuserp.dao.ArquivosOspDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.domain.ArquivosOsp;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.Osp;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.digital.opuserp.view.util.UploadImagemUtil;
import com.digital.opuserp.view.util.UploadImagemUtil.UploadImagemEvent;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class EditarOspEditor extends Window {
	
	private ComboBox cbEntregar;
	private Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
		

	EntityItem<Osp> osp;
	
	private ComboBox cbSetor;
	
	boolean alterar_setor;
	public EditarOspEditor(Item item, String title, boolean modal, boolean alterar_setor){
		
		this.alterar_setor = alterar_setor;
		this.item = item;
		osp = (EntityItem<Osp>)item;
		
		setWidth("915px");
		setHeight("635px");
				
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
	
	public EditarOspEditor(Item item, String title, boolean modal){
		
		this.item = item;
		osp = (EntityItem<Osp>)item;
		
		setWidth("915px");
		setHeight("603px");
				
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
	
	
	
	private TextField tfCodPedido;
	
	public void buildLayout(){
		
	
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					TextField txtPedidoNro = new TextField("Pedido Nº");					
					txtPedidoNro.setStyleName("caption-align");	
					txtPedidoNro.setWidth("70px");
					
					if(item.getItemProperty("id").getValue() != null){
						txtPedidoNro.setValue(item.getItemProperty("id").getValue().toString());
					}
					txtPedidoNro.setReadOnly(true);
										
					addComponent(txtPedidoNro);							
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					TextField txtOrdemServicoNro = new TextField("Ordem de Serviço Nº");					
					txtOrdemServicoNro.setStyleName("caption-align");	
					txtOrdemServicoNro.setWidth("70px");
					
					if(item.getItemProperty("venda_servico_cabecalho_id").getValue() != null){
						txtOrdemServicoNro.setValue(item.getItemProperty("venda_servico_cabecalho_id").getValue().toString());
					}
										
					txtOrdemServicoNro.setReadOnly(true);
					addComponent(txtOrdemServicoNro);							
				}
		});
		
		if(alterar_setor){
			setHeight("632px");
			vlRoot.addComponent(
					new FormLayout(){					
					{
						setStyleName("form-cutom-new");
						setMargin(true);
						setSpacing(true);
							
						cbSetor = new ComboBox("Setor");		
						cbSetor.setNullSelectionAllowed(false);
						cbSetor.setRequired(true);
						cbSetor.setStyleName("caption-align");	
						cbSetor.addItem("CRIACAO");
						cbSetor.addItem("PRE-IMPRESSAO");
						cbSetor.addItem("IMPRESSAO");
						cbSetor.addItem("ACABAMENTO");
						cbSetor.addItem("QUALIDADE");
						cbSetor.addItem("EXPEDICAO");
						
						if(item.getItemProperty("setor").getValue() != null){
							cbSetor.select(item.getItemProperty("setor").getValue().toString());
						}
						
						addComponent(cbSetor);							
					}
			});
		}
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
						
					TextField txtCliente = new TextField("Cliente");					
					txtCliente.setStyleName("caption-align");	
					txtCliente.setWidth("436px");
					
					if(item.getItemProperty("cliente").getValue() != null){
						Cliente c = (Cliente)item.getItemProperty("cliente").getValue();
						txtCliente.setValue(c.getNome_razao());
					}
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
						
					TextField txtComprador = new TextField("Comprador");					
					txtComprador.setStyleName("caption-align");	
					txtComprador.setWidth("318px");
					
					if(item.getItemProperty("venda_servico_cabecalho_id").getValue() != null){
						EcfPreVendaCabecalho pedido = PedidoDAO.find((Integer)item.getItemProperty("venda_servico_cabecalho_id").getValue());
						txtComprador.setValue(pedido.getComprador());
					}
					txtComprador.setReadOnly(true);
										
					addComponent(txtComprador);							
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
						
					TextField txtItem = new TextField("Item");					
					txtItem.setStyleName("caption-align");	
					txtItem.setWidth("700px");
										
					if(item.getItemProperty("descricao_servico").getValue() != null){
						txtItem.setValue(item.getItemProperty("descricao_servico").getValue().toString());
					}
					txtItem.setReadOnly(true);
					
					addComponent(txtItem);							
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					TextField txtQuantidade = new TextField("Quantidade");					
					txtQuantidade.setStyleName("caption-align");	
					txtQuantidade.setWidth("54px");
					
					if(item.getItemProperty("qtd_servico").getValue() != null){
						txtQuantidade.setValue(Real.formatDbToString(item.getItemProperty("qtd_servico").getValue().toString()));
					}
					txtQuantidade.setReadOnly(true);
										
					addComponent(txtQuantidade);							
				}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					TextArea txtObs = new TextArea("Obs");					
					txtObs.setStyleName("caption-align");	
					txtObs.setWidth("700px");
					txtObs.setHeight("63px");
					txtObs.focus();
										
					addComponent(txtObs);	
					fieldGroup.bind(txtObs, "observacao");
				}
		});
		
		vlRoot.addComponent(
			new HorizontalLayout(){
			{
				
						addComponent(new FormLayout(){
							{							
								
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
								
								TextField txtPrevisao = new TextField("Previsão");
								txtPrevisao.setStyleName("caption-align");
								
								if(item.getItemProperty("data_previsao_termino").getValue() != null){
									
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
									txtPrevisao.setValue(sdf.format(item.getItemProperty("data_previsao_termino").getValue()));
								}
								
								txtPrevisao.setReadOnly(true);
																
								addComponent(txtPrevisao);		
								
							}
						});
						
						
						
						addComponent(new FormLayout(){
							{	
								
								setStyleName("form-cutom-new");
								setMargin(true);
								setSpacing(true);
								
								cbEntregar = new ComboBox("Entregar");
								cbEntregar.setNullSelectionAllowed(false);
								cbEntregar.setTextInputAllowed(false);
								
								cbEntregar.addItem("SIM");
								cbEntregar.addItem("NAO");
								cbEntregar.setRequired(true);
								
								if((boolean)item.getItemProperty("entregar").getValue()){
									cbEntregar.select("SIM");
								}else{
									cbEntregar.select("NAO");
								}
								
								addComponent(cbEntregar);		
								
							}
						});
				
				
				
			}
		});
		
		
		vlRoot.addComponent(new FormLayout(){
			{
				setWidth("100%");
				setStyleName("form-cutom-new");
				setMargin(false);
				setSpacing(false);							
			
				final Table tbArquivos = new Table("Arquivos",getArquivos());
				tbArquivos.setStyleName("caption-align-table-osp");
				tbArquivos.setHeight("140px");
				tbArquivos.setWidth("697px");
				tbArquivos.setVisibleColumns(new Object[]{"nome"});
				
				tbArquivos.setColumnHeader("nome", "Nome");
				
				tbArquivos.addGeneratedColumn("x", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(final Table source, final Object itemId, Object columnId) {
						
						
						
						Button btDeletar = new Button(null, new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Excluir este Arquivo?", true,true);
								gd.addListerner(new GenericDialog.DialogListerner() {
									
									@Override
									public void onClose(DialogEvent event) {
										if(event.isConfirm()){		
											
											//EntityItem<ArquivosOsp> fileOsp = (EntityItem<ArquivosOsp>)source.getItem(itemId);
											//ArquivosOspDAO.remove(fileOsp.getEntity());
											container.removeItem(itemId);
											container.commit();
											
											container.refresh();													
										}
									}
								});
								
								getUI().addWindow(gd);
							}
						});
						btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
						btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
						btDeletar.setDescription("Deletar Imagem");
						
						return btDeletar;
					}
				});
				
				tbArquivos.setColumnWidth("x", 20);
				
				tbArquivos.addGeneratedColumn("v", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(final Table source, final Object itemId, Object columnId) {
						
						
						
						Button btVisualizar = new Button(null, new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								Window win = new Window(source.getItem(itemId).getItemProperty("nome").getValue().toString());
								
								try{
									File file;
									String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
									file = new File(basepath + "/WEB-INF/uploads/logo.jpeg");
									
									writeFile(file, (byte[]) source.getItem(itemId).getItemProperty("file").getValue());
									Image img = new Image(null, new FileResource(file));
									
									win.setContent(img);	
									win.center();
									win.setModal(true);
																				
									getUI().addWindow(win);
								}catch(Exception e){
									e.printStackTrace();
									Notify.Show("Arquivo Não Encontrado!", Notify.TYPE_ERROR);
								}
																		
							}
						});
						btVisualizar.setIcon(new ThemeResource("icons/black-eye.png"));
						btVisualizar.setStyleName(BaseTheme.BUTTON_LINK);
						btVisualizar.setDescription("Visualizar Imagem");
						
						return btVisualizar;
					}
				});

				tbArquivos.setColumnWidth("v", 20);
						
				
				addComponent(tbArquivos);
				
			}
		});
		
		
		vlRoot.addComponent(new FormLayout(){
			{
				setMargin(true);
				setSpacing(true);
				setStyleName("form-custom");
				
				Button btAddFile = new Button("Adicionar Imagem", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						final UploadImagemUtil imgUtil = new UploadImagemUtil("Upload de Arquivo", true);
						imgUtil.addListerner(new UploadImagemUtil.UploadImagemListerner() {
							
							@Override
							public void onClose(UploadImagemEvent event) {
								if(event.isConfirm()){
									ArquivosOspDAO.save(new ArquivosOsp(null, osp.getEntity(), event.getNome(), event.getFile()));
									container.refresh();
									
									imgUtil.close();
								}
							}
						});
						
						getUI().addWindow(imgUtil);
						
					}
				});
				btAddFile.setStyleName(Reindeer.BUTTON_SMALL);
				btAddFile.addStyleName("caption-align-button-add-file");
				
				
				
				addComponent(btAddFile);
			}
		});
		
		
	}
	
	JPAContainer<ArquivosOsp> container;
	private JPAContainer<ArquivosOsp> getArquivos(){
		container = JPAContainerFactory.make(ArquivosOsp.class, ConnUtil.getEntity());	
		
		container.addContainerFilter(Filters.eq("osp", osp.getEntity()));
		
		return container;
	}
	
	
	
	public Button buildBtSalvar() {
		btSalvar = new Button("Salvar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(fieldGroup.isValid() && cbEntregar.isValid()){
					try {
					
						if(cbEntregar.getValue().equals("SIM")){
							item.getItemProperty("entregar").setValue(true);							
						}else{
							item.getItemProperty("entregar").setValue(false);
						}
						
						if(cbSetor != null){
							item.getItemProperty("setor").setValue(cbSetor.getValue().toString());
						}
						
						if(fieldGroup.isModified()){
							fieldGroup.commit();							
							fireEvent(new EdicaoOspEvent(getUI(), item, true));
						}else{
							
							if(cbSetor != null && cbSetor.isValid()){
								fireEvent(new EdicaoOspEvent(getUI(), item, true));
							}else{
								fireEvent(new EdicaoOspEvent(getUI(), null, false));
							}
						}
						
					} catch (Exception e) {
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);

						e.printStackTrace();
					}
					
				}else{
					
					 
//					 if(!dfPrevisao.isValid()){
//						 dfPrevisao.addStyleName("invalid-txt");
//					 }else{
//						 dfPrevisao.removeStyleName("invalid-txt");
//					 }
//					 
//					 if(cbPrevisao.isValid()){
//						 cbPrevisao.addStyleName("invalid-txt");
//					 }else{
//						 cbPrevisao.removeStyleName("invalid-txt");
//					 }
//					 
//					 if(pedidoSelecionado == null){
//						 tfCodPedido.addStyleName("invalid-txt");
//					 }else{
//						 tfCodPedido.removeStyleName("invalid-txt");
//					 }
					 
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
							
				fireEvent(new EdicaoOspEvent(getUI(), null, false));
				close();			
			}
		});
		
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}
	
	
	public void addListerner(EdicaoOspListerner target){
		try {
			Method method = EdicaoOspListerner.class.getDeclaredMethod("onClose", EdicaoOspEvent.class);
			addListener(EdicaoOspEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(EdicaoOspListerner target){
		removeListener(EdicaoOspEvent.class, target);
	}
	public static class EdicaoOspEvent extends Event{
		
		private Item item;
		private boolean confirm;
		
		public EdicaoOspEvent(Component source, Item item, boolean confirm) {
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
	public interface EdicaoOspListerner extends Serializable{
		public void onClose(EdicaoOspEvent event);
	}
	
	
	public void writeFile(File file, byte[] data) throws IOException
	 {
	   OutputStream fo = new FileOutputStream(file);
	   // Write the data
	   fo.write(data);
	   // flush the file (down the toilet)
	   fo.flush();
	   // Close the door to keep the smell in.
	   fo.close();
	 }
	

}

