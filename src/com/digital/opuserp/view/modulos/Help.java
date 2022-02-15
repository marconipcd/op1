package com.digital.opuserp.view.modulos;

import java.util.List;

import org.vaadin.youtubeplayer.YouTubePlayer;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.LoginDAO;
import com.digital.opuserp.domain.ModuloUsuario;
import com.digital.opuserp.domain.SubModuloUsuario;
import com.kbdunn.vaadin.addons.mediaelement.MediaElementPlayer;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

public class Help extends VerticalLayout {

	HorizontalLayout hlRoot;
	
	
	public Help(){
		
		buildLayout();
	}
	
	
	private void buildLayout(){
		hlRoot = new HorizontalLayout();
		hlRoot.setSizeFull();
		
		final VerticalLayout vlLocalizacao = new VerticalLayout();
		vlLocalizacao.addStyleName("vlLocalizacao");
		vlLocalizacao.setWidth("99%");	
		
		
		 Item item = null;
	        int itemId = 0;  
	        int itemRoot = 0;

		
		HierarchicalContainer hwContainer = new HierarchicalContainer();        
        hwContainer.addContainerProperty("modulo", String.class, null);
        hwContainer.addContainerProperty("icone", ThemeResource.class, null);
        
        //item = hwContainer.addItem(itemId);
        //item.getItemProperty("modulo").setValue("Página Inicial");
       // item.getItemProperty("icone").setValue(new ThemeResource("icons/pagina-inicial-icon.png"));      
        //hwContainer.setChildrenAllowed(itemId, false);
  
        itemId ++;
        
        List<ModuloUsuario> modulos =  LoginDAO.getModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());
        
        for(ModuloUsuario m : modulos){  

        	if(m.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId())){

        		Integer codModulo = m.getModulo().getId();
                item = hwContainer.addItem(itemId);
                item.getItemProperty("modulo").setValue(m.getModulo().getNome_modulo());
              //  item.getItemProperty("icone").setValue(new ThemeResource(m.getModulo().getIcone_modulo()));                   
                hwContainer.setChildrenAllowed(itemId, true);
                itemRoot = itemId;
                itemId ++;


               // List<SubModuloUsuario> SubModulos = LoginDAO.getSubModulosUsuario(OpusERP4UI.getUsuarioLogadoUI());

                //if(SubModulos != null){
	            //    for(SubModuloUsuario s: SubModulos){    
	            //        Integer codSubModuloModulo = s.getSubmodulo().getModulo_id();   
	             //   	if(s.getEmpresa().getId().equals(OpusERP4UI.getEmpresa().getId()) && codModulo.equals(codSubModuloModulo)){
	              //          item = hwContainer.addItem(itemId);
	             //           item.getItemProperty("modulo").setValue(s.getSubmodulo().getNome());		                            
	             //           hwContainer.setParent(itemId, itemRoot);
	             //           hwContainer.setChildrenAllowed(itemId, false);
	            //            itemId ++;  
	            //    	}
	            //     }
              //  }
        	}
        }
				
		Tree menuPrincipal;
		menuPrincipal = new Tree();   
		
		menuPrincipal.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				vlLocalizacao.removeAllComponents();
				String nomeModulo = event.getItem().getItemProperty("modulo").getValue().toString();
				vlLocalizacao.addComponent(new Label("CADASTROS -> "+nomeModulo));
			}
		});
		
		 menuPrincipal.setImmediate(true);        
	     menuPrincipal.setItemCaptionPropertyId("modulo");       
	     menuPrincipal.setSizeUndefined();
	     menuPrincipal.setStyleName("tree-ajuda");
	     menuPrincipal.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
	     menuPrincipal.setContainerDataSource(hwContainer);
	
		VerticalLayout vlRightRoot = new VerticalLayout();
		vlRightRoot.setWidth("100%");
	    		
		Label lbTitulo = new Label("<h1 style='font-size:28px;'>Cadastrando e editando um cliente</h1>", ContentMode.HTML);
		Embedded e = new Embedded(null, new ExternalResource(
	            "http://www.youtube.com/v/6wAbsG8bZ6k&hl=en_US&fs=1&"));
	    //e.setAlternateText("Cadastrando e Editando um Cliente");
	    e.setMimeType("application/x-shockwave-flash");
	    e.setParameter("allowFullScreen", "true");
	    e.setWidth("100%");
	    e.setHeight("600px");
	    		
		VerticalLayout vlConteudo = new VerticalLayout();
		vlConteudo.setSizeFull();		
				
		VerticalLayout vlContVideo = new VerticalLayout();		
//		vlContVideo.setSizeFull();
		vlContVideo.addComponent(lbTitulo);
		vlContVideo.addComponent(e);
		
		VerticalLayout vlContLinks = new VerticalLayout();
		vlContLinks.addStyleName("vlLinks");
		
	//	vlContLinks.setSizeFull();
		//vlContLinks.addComponent(new Label("<a href='#'>* Cadastrando e editando um cliente</a>", ContentMode.HTML));
		//vlContLinks.addComponent(new Label("<a href='#'>* Excluindo um cliente</a>", ContentMode.HTML));
		//vlContLinks.addComponent(new Label("<a href='#'>* Importando um cliente</a>", ContentMode.HTML));
		//vlContLinks.addComponent(new Label("<a href='#'>* Adicionando endereços ao cliente</a>", ContentMode.HTML));
		
		
		vlContLinks.addComponent(new Label("<a href='#'>* Cadastrar e editar cliente</a>", ContentMode.HTML));
		vlContLinks.addComponent(new Label("<a href='#'>* Excluír cliente</a>", ContentMode.HTML));
		vlContLinks.addComponent(new Label("<a href='#'>* Importar cliente</a>", ContentMode.HTML));
		vlContLinks.addComponent(new Label("<a href='#'>* Adicionar outros endereços</a>", ContentMode.HTML));
		
		

		HorizontalLayout hlConteudo = new HorizontalLayout();
		hlConteudo.setWidth("100%");
		hlConteudo.setHeight("600px");
		
		hlConteudo.addComponent(vlContVideo);
		hlConteudo.addComponent(vlContLinks);
		//hlConteudo.setExpandRatio(vlContVideo, 1f);
		
		vlConteudo.addComponent(hlConteudo);
		
		
		vlRightRoot.addComponent(vlLocalizacao);
		vlRightRoot.addComponent(vlConteudo); 
	     
	     hlRoot.addComponent(menuPrincipal);
	     hlRoot.addComponent(vlRightRoot);
	     hlRoot.setExpandRatio(vlRightRoot, 1f);
		

//	     YouTubePlayer player = new YouTubePlayer();
//	     player.setVideoId("qrO4YZeyl0I");
//	     player.setWidth("400px");
//	     player.setHeight("300px");
	        
	     //vlConteudo.addComponent(player); 
		
		addComponent(hlRoot); 
		
	}
}
