package com.digital.opuserp.view.home.apps.notepad;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.vaadin.hene.popupbutton.PopupButton;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PrefeDashDAO;
import com.digital.opuserp.domain.Notas;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class NotepadApp extends VerticalLayout {
	
	private Button btMaximize;
	private Button btClose;
	private TextArea notes;
	
	
	
	public NotepadApp(){      
		load();
	}
	private void load(){
		final EntityManager em = ConnUtil.getEntity();
		
		try{
		removeAllComponents();
		setMargin(false);
		setSpacing(false); 
			 
	     addStyleName("layout-panel");
	     setSizeFull();

	     
	     addComponent(new Label("<div style='width: 100px;left: 28px;position: absolute;z-index: 1;top: 11px;width: 137px;font-weight: 600;font-size: 13px;color: #349AFF;text-transform: uppercase;line-height: 38px;'>Bloco de Notas</div>", ContentMode.HTML));
	     addComponent(buildBtMaximize());
	     addComponent(buildBtClose());
	     
	     Query q = em.createQuery("select n from Notas n where n.usuario = :Usuario", Notas.class);
         q.setParameter("Usuario", OpusERP4UI.getUsuarioLogadoUI());     
         
         List<Notas> listNotas = q.getResultList();
         
         HorizontalLayout hlFiles = new HorizontalLayout();
         hlFiles.addStyleName("hlfiles-note");
         hlFiles.setMargin(false);
         hlFiles.setSpacing(false);
         
         addComponent(hlFiles);
         
//         VerticalLayout files = new VerticalLayout();
//         PopupButton popupButton = new PopupButton("Meus arquivos");
//         popupButton.setContent(files); 
//         hlFiles.addComponent(popupButton);

         for (final Notas notas : listNotas) {
        	 
        	 
        	 VerticalLayout vlActionFile = new VerticalLayout();
        	 PopupButton fileActions = new PopupButton(notas.getNota() != null ||  notas.getNota().equals("")? notas.getNota().split("\\n")[0] : "VAZIO");
        	 fileActions.addStyleName("bt-note");
        	 fileActions.setContent(vlActionFile);
        	 
        	 Button btOpen = new Button( "Abrir", new Button.ClickListener() {
 				
 				@Override
 				public void buttonClick(ClickEvent event) {			
 					replaceComponent(notes, buildNote(notas));
 					setExpandRatio(notes, 1.0f); 					
 				}
 			});
        	 btOpen.setWidth("81px");
        	 
        	 Button btExcluir = new Button( "Excluir", new Button.ClickListener() {
  				
  				@Override
  				public void buttonClick(ClickEvent event) {			
  						em.getTransaction().begin();
  						em.remove(notas);
  						em.getTransaction().commit();
  						load();
  				}
  			});
        	 btExcluir.setWidth("81px");
        	 
        	 vlActionFile.addComponent(btOpen);
        	 vlActionFile.addComponent(btExcluir);
//        	 
//        	 btNote.addStyleName("bt-note");       	       	 
//        	 hlFiles.addComponent(btNote);
        	 hlFiles.addComponent(fileActions);
		}
         Button btNew = new Button(null,new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				em.getTransaction().begin();	
				em.persist(new Notas(null, OpusERP4UI.getUsuarioLogadoUI(), OpusERP4UI.getEmpresa(), "VAZIO", new Date()));
				em.getTransaction().commit();
				
				load();
			}
		});
         btNew.addStyleName("bt-note");
         btNew.setIcon(new ThemeResource("icons/Add-sheet-32.png"));
         hlFiles.addComponent(btNew);
         
         if(listNotas.size() > 0){
        	 addComponent(buildNote(listNotas.get(listNotas.size()-1)));
        	 setExpandRatio(notes, 1.0f);
         }
         
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	VerticalLayout vlRootMax = new VerticalLayout();
	private Component loadMax(){
		 final EntityManager em = ConnUtil.getEntity();
		 
		 try{
		
		vlRootMax.removeAllComponents();
		
		
		vlRootMax.setMargin(false);
		vlRootMax.setSpacing(false); 			 
		vlRootMax.addStyleName("layout-panel");
		vlRootMax.setSizeFull();

	     
	     addComponent(new Label("<div style='width: 100px;left: 28px;position: absolute;z-index: 1;top: 11px;width: 137px;font-weight: 600;font-size: 13px;color: #349AFF;text-transform: uppercase;line-height: 38px;'>Bloco de Notas</div>", ContentMode.HTML));
	     addComponent(buildBtMaximize());
	     addComponent(buildBtClose());
	     
	     Query q = em.createQuery("select n from Notas n where n.usuario = :Usuario", Notas.class);
         q.setParameter("Usuario", OpusERP4UI.getUsuarioLogadoUI());     
         
         List<Notas> listNotas = q.getResultList();
         
         HorizontalLayout hlFiles = new HorizontalLayout();
         hlFiles.addStyleName("hlfiles-note");
         hlFiles.setMargin(false);
         hlFiles.setSpacing(false);
         
         vlRootMax.addComponent(hlFiles);
         
         for (final Notas notas : listNotas) {
        	 
        	 
        	 VerticalLayout vlActionFile = new VerticalLayout();
        	 PopupButton fileActions = new PopupButton(notas.getNota() != null ||  notas.getNota().equals("")? notas.getNota().split("\\n")[0] : "VAZIO");
        	 fileActions.addStyleName("bt-note");
        	 fileActions.setContent(vlActionFile);
        	 
        	 Button btOpen = new Button( "Abrir", new Button.ClickListener() {
 				
 				@Override
 				public void buttonClick(ClickEvent event) {			
 					vlRootMax.replaceComponent(notes, buildNote(notas));
 					vlRootMax.setExpandRatio(notes, 1.0f); 					
 				}
 			});
        	 btOpen.setWidth("81px");
        	 
        	 Button btExcluir = new Button( "Excluir", new Button.ClickListener() {
  				
  				@Override
  				public void buttonClick(ClickEvent event) {			
  						em.getTransaction().begin();
  						em.remove(notas);
  						em.getTransaction().commit();
  						
  						loadMax();
  						load();
  				}
  			});
        	 btExcluir.setWidth("81px");
        	 
        	 vlActionFile.addComponent(btOpen);
        	 vlActionFile.addComponent(btExcluir);

        	 hlFiles.addComponent(fileActions);
		}
         Button btNew = new Button(null,new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				em.getTransaction().begin();	
				em.persist(new Notas(null, OpusERP4UI.getUsuarioLogadoUI(), OpusERP4UI.getEmpresa(), "VAZIO", new Date()));
				em.getTransaction().commit();
				
				loadMax();
				load();
			}
		});
         btNew.addStyleName("bt-note");
         btNew.setIcon(new ThemeResource("icons/Add-sheet-32.png"));
         hlFiles.addComponent(btNew);
         
         if(listNotas.size() > 0){
        	 vlRootMax.addComponent(buildNote(listNotas.get(listNotas.size()-1)));
        	 vlRootMax.setExpandRatio(notes, 1.0f);
         }
         
         return  vlRootMax;
         
		 }catch(Exception e){
			 return null;
		 }
	}
	
	 private Button buildBtClose(){
    	btClose = new Button();	    	btClose.addStyleName("btClose");
    	btClose.setIcon(new ThemeResource("icons/icon_close.png"));
    	btClose.addStyleName("icon-only");
    	btClose.addStyleName("borderless");
    	btClose.setDescription("Fechar");
    	btClose.addStyleName("small");  
    	btClose.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	   PrefeDashDAO.remove(NotepadApp.class.toString());
                  ((GridLayout)getParent()).removeComponent(btClose.getParent());   
            }
        });
        
        return btClose;
	 }
	    
    private Button buildBtMaximize(){
    	btMaximize = new Button();
    	btMaximize.addStyleName("btMaximize");
    	btMaximize.setIcon(new ThemeResource("icons/icon_maximize.png"));
    	btMaximize.addStyleName("icon-only");
    	btMaximize.addStyleName("borderless");
    	btMaximize.setDescription("Maximizar");
    	btMaximize.addStyleName("small");
    	btMaximize.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Window winMaximize = new Window("Bloco de Notas");
                winMaximize.setHeight("624px");
                winMaximize.setWidth("989px");
                
                CssLayout cssLay = new CssLayout();
                cssLay.setSizeFull();
                cssLay.addStyleName("notes");
                
                 loadMax();
                cssLay.addComponent(vlRootMax);
                winMaximize.setContent(cssLay);
                
                winMaximize.setModal(true);
                winMaximize.center();
                
                
                winMaximize.addCloseListener(new Window.CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						//((CssLayout)btMaximize.getParent()).replaceComponent(notes, buildNote());
					}
				});
                
                getUI().addWindow(winMaximize);                
            }
        });
        
        return btMaximize;
    }
	    
    private TextArea buildNote(final Notas n){
    	final EntityManager em = ConnUtil.getEntity();
    	
    	try{
         notes = new TextArea();
         notes.addStyleName("notes-max");
         notes.setValue(n.getNota());
         notes.setSizeFull();
         notes.setTextChangeEventMode(TextChangeEventMode.LAZY);
         //notes.setTextChangeTimeout(5000);
         notes.addListener(new FieldEvents.TextChangeListener() {
 			
 			@Override
 			public void textChange(TextChangeEvent event) {
 				n.setNota(event.getText());
 				
 				em.getTransaction().begin();
 				em.persist(n);			
 				em.getTransaction().commit();
 			}
 		});
         
        return notes;
    	}catch(Exception e){
    		return null;
    	}
    }
    
    private TextArea buildNoteMax(){
    	final EntityManager em = ConnUtil.getEntity();
   	 
   	 try{
        Query q = em.createQuery("select n from Notas n where n.usuario = :Usuario", Notas.class);
        q.setParameter("Usuario", OpusERP4UI.getUsuarioLogadoUI());         
        
        final Notas n;
        if(q.getResultList().size() > 0){
        	n = (Notas)q.getSingleResult();
        }else{
        	n = new Notas();
        	n.setUsuario(OpusERP4UI.getUsuarioLogadoUI());
			n.setEmpresa(OpusERP4UI.getEmpresa());
        }

        TextArea notes = new TextArea();
        notes.addStyleName("notes-max");
        notes.setValue(n.getNota());
        notes.setSizeFull();
        notes.setTextChangeEventMode(TextChangeEventMode.LAZY);
        //notes.setTextChangeTimeout(5000);
        notes.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				n.setNota(event.getText());
				
				em.getTransaction().begin();
				em.persist(n);			
				em.getTransaction().commit();
			}
		});
        
       return notes;
   	 }catch(Exception e){
   		 e.printStackTrace();
   		 
   		 return null;
   	 }
   }
	    
	    

	
}
