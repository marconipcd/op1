package com.digital.opuserp.view.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.standard.PrinterURI;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PrintUtil extends Window {

	
	Table tbImpressoras;
	Button btImprimir;
	Button btCancelar;
	String texto;
	
	public PrintUtil(String titulo,String texto){
		
		super(titulo);
		
		this.texto = texto;
		center();
		setModal(true);
		setWidth("649px");
		setHeight("299px");
		
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				addComponent(buildTbImpressoras());
				setExpandRatio(tbImpressoras, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);				
			}
		});
	}
	
	private Button buildBtSalvar() {
		btImprimir = new Button("Imprimir", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tbImpressoras.getValue()!= null){
					Item item = tbImpressoras.getItem(tbImpressoras.getValue());				
									
			        if (selecionarImpressora(tbImpressoras.getItem(tbImpressoras.getValue()).getItemProperty("Impressora").getValue().toString())) {  
			        	 		   
			          try { 		   
			              DocPrintJob dpj = impressora.createPrintJob();  
			              InputStream stream = new ByteArrayInputStream(texto.getBytes());  
			   
			              DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;  
			              Doc doc = new SimpleDoc(stream, flavor, null);  
			              dpj.print(doc, null);  
			              
			              close();
			   
			           } catch (PrintException e) {   
                           Notify.Show("ERRO: Não Foi Possivel Imprimir!", Notify.TYPE_ERROR);

			               e.printStackTrace();   
			           }    
			         } 
					
					
					
//					fireEvent(new CepEvent(getUI(), cep));
//					close();
				}	
			}	
		});
		
		ShortcutListener slbtOK = new ShortcutListener("Imprimir",ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btImprimir.click();
			}
		};

		btImprimir.addShortcutListener(slbtOK);
		btImprimir.setEnabled(false);

		
		return btImprimir;
	}

	private Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {             
				close();
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
	
	private Table buildTbImpressoras(){
		tbImpressoras = new Table(null);
		tbImpressoras.setWidth("100%");
		tbImpressoras.setHeight("230px");
		tbImpressoras.setSelectable(true);
		
		tbImpressoras.addContainerProperty("Impressora", String.class, "");
		
		tbImpressoras.setImmediate(true);
		tbImpressoras.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(tbImpressoras.getValue() != null){
					 btImprimir.setEnabled(true);
				}else{
					 btImprimir.setEnabled(false);
				}
			}
		});
		
		listarImpressoras();
		
		return tbImpressoras;
	}
	
	private static PrintService impressora;  
	
	
	private void listarImpressoras(){
		try {
			 DocFlavor df = DocFlavor.SERVICE_FORMATTED.PRINTABLE;		
			
			 URI printerURI = new URI("smb://192.168.20.203/BEMA");
			 AttributeSet attributes = new HashAttributeSet();
			 attributes.add(new PrinterURI(printerURI));		
			
			 PrintService[] ps = PrintServiceLookup.lookupPrintServices(null, null);		
			 
			 Integer i = 1;
			 for (PrintService p: ps) {  
	
			     //System.out.println("Impressora encontrada: " + p);              
				 tbImpressoras.addItem(new Object[]{p.getName()}, i);
			     i++;
	
			 }
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private boolean selecionarImpressora(String s){
		DocFlavor df = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
		PrintService[] ps = PrintServiceLookup.lookupPrintServices(df, null);  
		
		for (PrintService p: ps) {  

             if(p.getName().equals(s)){
            	 impressora = p;
            	 return true;
            	 //break;
             }
        } 
		
		return false;
	}
}
