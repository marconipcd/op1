package com.digital.opuserp.view.modulos.ordemServico.ordemProducao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import com.digital.opuserp.dao.AlteracoesOspDAO;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.domain.AlteracoesOsp;
import com.digital.opuserp.domain.ArquivosOsp;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.Osp;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class VisualizarOspEditor extends Window {
	
	private ComboBox cbEntregar;
	private Item item;
	Button btSalvar;
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
		

	EntityItem<Osp> osp;
	
	public VisualizarOspEditor(Item item, String title, boolean modal){
		
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
				
				hlButtons.addComponent(buildBtOcorrencias());
				hlButtons.addComponent(buildBtCancelar());				
				
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
						
						if(pedido.getComprador() != null){
							txtComprador.setValue(pedido.getComprador());
						}
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
					
					if(item.getItemProperty("observacao").getValue() != null){
						txtObs.setValue(item.getItemProperty("observacao").getValue().toString());
					}
					txtObs.setReadOnly(true);
					
					
					addComponent(txtObs);					
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
								
								TextField cbEntregar = new TextField("Entregar");
								
								if((boolean)item.getItemProperty("entregar").getValue()){
									cbEntregar.setValue("SIM");
								}else{
									cbEntregar.setValue("NAO");
								}
								
								addComponent(cbEntregar);		
								
							}
						});
				
				
				
			}
		});
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
						
					TextField txtUltimaOcorrencia = new TextField("Ultima Ocorrência");					
					txtUltimaOcorrencia.setStyleName("caption-align");	
					txtUltimaOcorrencia.setWidth("700px");
										
					List<AlteracoesOsp> alteracoes = AlteracoesOspDAO.getAltercoesAssistencia((Integer)item.getItemProperty("id").getValue());
					if(alteracoes.size() > 0){
						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						String data = sdf.format(((AlteracoesOsp)alteracoes.get(alteracoes.size()-1)).getData_alteracao());
						txtUltimaOcorrencia.setValue(data+" - "+
								((AlteracoesOsp)alteracoes.get(alteracoes.size()-1)).getOperador().getUsername()+" - "+((AlteracoesOsp)alteracoes.get(alteracoes.size()-1)).getTipo());
					}
					
					txtUltimaOcorrencia.setReadOnly(true);
					
					
					addComponent(txtUltimaOcorrencia);					
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
		
		
		
		
		
	}
	
	JPAContainer<ArquivosOsp> container;
	private JPAContainer<ArquivosOsp> getArquivos(){
		container = JPAContainerFactory.make(ArquivosOsp.class, ConnUtil.getEntity());	
		
		container.addContainerFilter(Filters.eq("osp", osp.getEntity()));
		
		return container;
	}
	
	
	
	Button btOcorrencias;
	public Button buildBtOcorrencias(){
		btOcorrencias = new Button("Ocorrências", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				HistoricoAlteracoes historicoAlteracoes = new HistoricoAlteracoes(true, true, (Integer)item.getItemProperty("id").getValue());
				historicoAlteracoes.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						btCancelar.focus();
					}
				});
				getUI().addWindow(historicoAlteracoes);				
			}
		});
		
		return btOcorrencias;
	}
	public Button buildBtCancelar() {
		btCancelar = new Button("Fechar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
							
				//fireEvent(new EdicaoOspEvent(getUI(), null, false));
				close();			
			}
		});
		
		ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		btCancelar.focus();
		
		return btCancelar;
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

