package com.digital.opuserp.util.exporterpdf;

import com.digital.opuserp.util.exporterpdf.filegenerator.FileBuilder;
import com.digital.opuserp.util.exporterpdf.filegenerator.PdfFileBuilder;
import com.vaadin.data.Container;
import com.vaadin.ui.Table;

public class PdfExporter extends Exporter {
    public PdfExporter() {
        super();
    }

    public PdfExporter(Table table) {
        super(table);
    }

    public PdfExporter(Container container,Table table, Object[] visibleColumns) {
        super(container, table,visibleColumns);
    }

    public PdfExporter(Container container,Table table) {
        super(container,table);
    }

    @Override
    protected FileBuilder createFileBuilder(Container container) {
        return new PdfFileBuilder(container);
    }

    @Override
    protected String getDownloadFileName() {
    	if(downloadFileName == null){
    		return "exported-pdf.pdf";
        }
    	if(downloadFileName.endsWith(".pdf")){
    		return downloadFileName;
    	}else{
    		return downloadFileName + ".pdf";
    	}
    }

    public void setWithBorder(boolean withBorder) {
        ((PdfFileBuilder) fileBuilder).setWithBorder(withBorder);
    }
    
    public void setHorizontal(boolean horizontal){
    	if(horizontal){
    		//((PdfFileBuilder) fileBuilder).set
    	}
    }
    
}
