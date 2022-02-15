package com.digital.opuserp.util.exporterpdf;

import com.digital.opuserp.util.exporterpdf.filegenerator.CSVFileBuilder;
import com.digital.opuserp.util.exporterpdf.filegenerator.FileBuilder;
import com.vaadin.data.Container;
import com.vaadin.ui.Table;

public class CSVExporter extends Exporter {
    public CSVExporter() {
        super();
    }

    public CSVExporter(Table table) {
        super(table);
    }

    public CSVExporter(Container container,Table table, Object[] visibleColumns) {
        super(container, table,visibleColumns);
    }

    public CSVExporter(Container container,Table table) {
        super(container,table);
    }

    @Override
    protected FileBuilder createFileBuilder(Container container) {
        // TODO Auto-generated method stub
        return new CSVFileBuilder(container);
    }

    @Override
    protected String getDownloadFileName() {
    	if(downloadFileName == null){
    		return "exported-csv.csv";
        }
    	if(downloadFileName.endsWith(".csv")){
    		return downloadFileName;
    	}else{
    		return downloadFileName + ".csv";
    	}
    }

}
