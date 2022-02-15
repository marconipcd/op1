package com.digital.opuserp.util.exporterpdf.filegenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.data.Container;

public class PdfFileBuilder extends FileBuilder {
    private Document document;
    private PdfPTable table;
    private static Font catFont = new Font(Font.FontFamily.HELVETICA, 8,  Font.BOLD);
    private static Font subFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);    
    private static Font cellFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    
    private boolean withBorder;

    public PdfFileBuilder(Container container) {
        super(container);
    }

    @Override
    protected void buildHeader() {
        if (getHeader() != null) {
            Paragraph title = new Paragraph(getHeader(), catFont);
            title.add(new Paragraph(" "));
            title.setAlignment(Element.ALIGN_CENTER);
            try {
                document.add(title);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void buildColumnHeaderCell(String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header, subFont));
        
        if (!withBorder) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        table.addCell(cell);
    }

    @Override
    protected void buildCell(Object value) {
    	PdfPCell cell;
    	if(value == null){
    		cell = new PdfPCell(new Phrase("",cellFont));
    	}else if(value instanceof Calendar){
    		Calendar calendar = (Calendar) value;
    		cell = new PdfPCell(new Phrase(formatDate(calendar.getTime()),cellFont));
    	}else if(value instanceof Date){
    		cell = new PdfPCell(new Phrase(formatDate((Date) value),cellFont));
    	}else {
    		cell = new PdfPCell(new Phrase(value.toString(),cellFont));
    	}
        
        if (!withBorder) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        table.addCell(cell);
    }

    @Override
    protected String getFileExtension() {
        return ".pdf";
    }

    @Override
    protected void writeToFile() {
        try {
            document.add(table);            
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public boolean isWithBorder() {
        return withBorder;
    }

    public void setWithBorder(boolean withBorder) {
        this.withBorder = withBorder;
    }

    @Override
    protected void resetContent() {
        document = new Document(PageSize.A4, -50, -50, 20, 20);
        
        Object[] columns = getColumns();
        float[] f = new float[columns.length];
		
		Integer i=0;
		for (Object c : columns) {

			if(c.toString().equals("nome")){
				f[i] = (0.07f);		
			}	
			if(c.toString().equals("uptime")){
				f[i] = (0.08f);		
			}	
			if(c.toString().equals("routeros")){
				f[i] = (0.05f);		
			}
			if(c.toString().equals("firmware")){
				f[i] = (0.05f);		
			}
			if(c.toString().equals("cpu")){
				f[i] = (0.03f);		
			}
			if(c.toString().equals("architecture_name")){
				f[i] = (0.06f);		
			}
			if(c.toString().equals("board_name")){
				f[i] = (0.10f);		
			}
			if(c.toString().equals("data")){
				f[i] = (0.08f);		
			}
			
			if(c.toString().equals("ssid")){
				f[i] = (0.11f);		
			}
			if(c.toString().equals("mac")){
				f[i] = (0.08f);		
			}
			if(c.toString().equals("channel")){
				f[i] = (0.04f);		
			}
			if(c.toString().equals("authenticated_clientes")){
				f[i] = (0.05f);		
			}
			if(c.toString().equals("distancia")){
				f[i] = (0.04f);		
			}
			if(c.toString().equals("ccq")){
				f[i] = (0.03f);		
			}
			
		    i++;
 		 }					
        
        table = new PdfPTable(f);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.open();
    }
    
//    public void setHorizontal(){
//    	document.set
//    }
}
