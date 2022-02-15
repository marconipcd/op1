package com.digital.opuserp.util.exporterpdf;

import com.digital.opuserp.util.exporterpdf.filegenerator.ExcelFileBuilder;
import com.digital.opuserp.util.exporterpdf.filegenerator.FileBuilder;
import com.vaadin.data.Container;
import com.vaadin.ui.Table;

public class ExcelExporter extends Exporter {
    public ExcelExporter() {
        super();
    }

    public ExcelExporter(Table table) {
        super(table);
    }

    public ExcelExporter(Container container, Table table,Object[] visibleColumns) {
        super(container, table,visibleColumns);
    }

    public ExcelExporter(Container container,Table table) {
        super(container,table);
    }

    @Override
    protected FileBuilder createFileBuilder(Container container) {
        return new ExcelFileBuilder(container);
    }

    @Override
    protected String getDownloadFileName() {
    	if(downloadFileName == null){
    		return "exported-excel.xls";
        }
    	if(downloadFileName.endsWith(".xls")){
    		return downloadFileName;
    	}else{
    		return downloadFileName + ".xls";
    	}
    }
}
