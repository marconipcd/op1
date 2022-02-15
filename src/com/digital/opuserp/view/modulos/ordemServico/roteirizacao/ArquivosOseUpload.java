package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ArquivosOseDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.ArquivosOse;
import com.digital.opuserp.domain.ArquivosOse2;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.FileUploadUtil;
import com.digital.opuserp.view.util.FileUploadUtil.FileUploadUtilEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class ArquivosOseUpload extends Window {

	GerenciarModuloDAO gmDAO;
	private Ose ose;
	private VerticalLayout vlRoot;
	
	Integer codSubModulo;
	
	public ArquivosOseUpload(String title, boolean modal,Ose ose, final Integer codSubModulo){
		this.ose = ose;
		this.codSubModulo = codSubModulo;
		
		gmDAO = new GerenciarModuloDAO();
		
		setWidth("640px");
		setHeight("297px");
		
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
			

				Button btEnviarNovoArquivo = new Button("Enviar novo arquivo", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
								OpusERP4UI.getUsuarioLogadoUI().getId(), "Upload arquivo"))				
						{
							uploadNovoArquivo();
						}else{
							Notify.Show("Você não Possui Permissão para Adicionar Arquivos", Notify.TYPE_ERROR);
						}
					}
				});
				btEnviarNovoArquivo.setStyleName("default");
				hlButtons.addComponent(btEnviarNovoArquivo);
				
				hlButtons.addComponent(buildBtCancelar());
				
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		buildLayout();
		
	}
	
	private void uploadNovoArquivo(){
			
		final FileUploadUtil imgUtil = new FileUploadUtil("Upload de Arquivo", true,"ordens_servico");
		imgUtil.addListerner(new FileUploadUtil.FileUploadUtilListerner() {
			
			@Override
			public void onClose(FileUploadUtilEvent event) {
				if(event.isConfirm()){
															
					ArquivosOseDAO.save2(new ArquivosOse2(null, ose.getId(),event.getNome(),  new Date(),event.getLink()));				
					
					containerArquivos.refresh();											
					imgUtil.close();
				}
			}
		});
		
		getUI().addWindow(imgUtil);		
	}
	
	
	JPAContainer<ArquivosOse2> containerArquivos;
	private void buildLayout(){
		
		//VerticalLayout
		containerArquivos = JPAContainerFactory.make(ArquivosOse2.class, ConnUtil.getEntity());
		containerArquivos.addContainerFilter(Filters.eq("ose", ose.getId()));
		
		Table tb= new Table(null, containerArquivos);
		tb.setWidth("100%");
		tb.setHeight("166px");
		tb.setStyleName("tabela-upload");
		
		tb.setVisibleColumns(new Object[]{"nome","data","usuario"});
		tb.addGeneratedColumn("x", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				
			
				Button btExcluir = new Button("Excluir", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Excluir arquivo"))				
						{
							GenericDialog gd = new GenericDialog("Confirme para continuar!", "Deseja realmente excluir este arquivo", true, true);
							gd.addListerner(new GenericDialog.DialogListerner() {
								
								@Override
								public void onClose(DialogEvent event) {
									if(event.isConfirm()){
										
										//Exclui arquivo
										containerArquivos.removeItem(itemId);
										containerArquivos.commit();
										Notify.Show("Arquivo excluído com sucesso!", Notify.TYPE_SUCCESS);
										
										//Altera a info de qtd de arquivos
//										List<ArquivosOse2> arquivos= ArquivosOseDAO.listarArquivos(ose);
//										EntityManager em = ConnUtil.getEntity();
//										ose.setArquivo_upload(new String().valueOf(arquivos.size()));
//										em.getTransaction().begin();
//										em.merge(ose);
//										em.getTransaction().commit();
										
									}
								}
							});
						
							getUI().addWindow(gd); 
						}else{
							Notify.Show("Você não Possui Permissão para Excluir Arquivos", Notify.TYPE_ERROR);
						}
					}
				});
				
				btExcluir.addStyleName(Reindeer.BUTTON_LINK);
				btExcluir.addStyleName("bt-up-excluir");
				
				return btExcluir;
			}
		});
		
		tb.addGeneratedColumn("v", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(final Table source,final Object itemId, Object columnId) {
				
				final EntityItem<ArquivosOse2> eiArquivosContrato = (EntityItem<ArquivosOse2>)source.getItem(itemId);
				final ArquivosOse2 arquivo = eiArquivosContrato.getEntity();
				
				Button btVisualizar = new Button("Visualizar", new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Visualizar arquivo"))				
						{	
						
							String link = source.getItem(itemId).getItemProperty("link").getValue().toString();
							link = "https://"+link.split("/")[2]+"/ordens_servico/"+link.split("/")[3];
							
														
							Embedded emb = new Embedded(null, new ExternalResource(link));
							emb.setType(Embedded.TYPE_BROWSER);							
							emb.setSizeFull();
							//emb.set
							
							
							final Window w = new Window("Ordem de Serviço");
							w.setWidth("600px");
							w.setHeight("560px");
							w.setContent(emb); 
							w.setResizable(true);
							w.setModal(true); 
							
							getUI().addWindow(w);	
						}else{					
							Notify.Show("Você não Possui Permissão para Visualizar Arquivos", Notify.TYPE_ERROR);
						}		
					}
				});
				
				btVisualizar.addStyleName(Reindeer.BUTTON_LINK);
				btVisualizar.addStyleName("bt-up-excluir");
				
				return btVisualizar;
			}
		});
				
		vlRoot.addComponent(tb);
	}
	
	public void writeFile(byte[] data, String nome) throws IOException
	 {		
		
	    File file = new File(VaadinService.getCurrent().getBaseDirectory()+"/uploads/"+nome);
	   OutputStream fo = new FileOutputStream(file);
	   // Write the data
	   fo.write(data);
	   // flush the file (down the toilet)
	   fo.flush();
	   // Close the door to keep the smell in.
	   fo.close();
	   
	   FileResource fr = new FileResource(file);
		
		Embedded emb = new Embedded();
		emb.setType(Embedded.TYPE_BROWSER);
		emb.setSource(fr); 
		emb.setSizeFull();
		
		final Window w = new Window("Contrato");
		w.setWidth("600px");
		w.setHeight("560px");
		w.setContent(emb); 
		w.setResizable(true);
		w.setModal(true); 
		
		getUI().addWindow(w);
	 }
	
	Button btCancelar;
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
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
}
