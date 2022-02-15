package com.digital.opuserp.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

public class PrintTest {

	private static PrintService impressora;  
	
	public static void main(String[] args) {
		
		 DocFlavor df = DocFlavor.SERVICE_FORMATTED.PRINTABLE;  
         PrintService[] ps = PrintServiceLookup.lookupPrintServices(df, null);  
         for (PrintService p: ps) {  

             System.out.println("Impressora encontrada: " + p.getName());  

             if (p.getName().equals("BEMA"))  {  

                 System.out.println("Impressora Selecionada: " + p.getName());  
                 impressora = p;  
                 break;
             }  

         } 
         
         
         String texto = "Teste de Impressão";
         
         if (impressora == null) {  
        	  
             String msg = "Nennhuma impressora foi encontrada. Instale uma impressora padrão \r\n(BEMA) e reinicie o programa.";  
             System.out.println(msg);  
   
         } else {  
   
             try {  
   
                 DocPrintJob dpj = impressora.createPrintJob();  
                 InputStream stream = new ByteArrayInputStream(texto.getBytes());  
   
                 DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;  
                 Doc doc = new SimpleDoc(stream, flavor, null);  
                 dpj.print(doc, null);  
   
             } catch (PrintException e) {   
                 e.printStackTrace();   
             }    
         }  
	}
}
