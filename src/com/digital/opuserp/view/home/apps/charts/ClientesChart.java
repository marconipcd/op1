package com.digital.opuserp.view.home.apps.charts;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.util.ConnUtil;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.Series;

public class ClientesChart extends Chart {

    public ClientesChart() {
       
        setCaption("Clientes Cadastrados Nos Ultimos 30 Dias");
        getConfiguration().setTitle("");
        getConfiguration().getChart().setType(ChartType.COLUMN);
        getConfiguration().getxAxis().getLabels().setEnabled(false);
        getConfiguration().getxAxis().setTickWidth(0);
        getConfiguration().setExporting(true);
        setWidth("100%");
        setHeight("100%");

        
        EntityManager em = ConnUtil.getEntity();
        Query q = em.createNativeQuery("SELECT DATE_FORMAT( DATA_CADASTRO,  '%d/%m/%Y' ) AS DATA, COUNT( DATE_FORMAT( DATA_CADASTRO,  '%d/%m/%Y' ) ) " +
        		"AS QTD FROM  clientes where EMPRESA_ID = :codEmpresa GROUP BY DATE_FORMAT( DATA_CADASTRO,  '%d/%m/%Y' )  ORDER BY  DATA_CADASTRO DESC limit 0,30");
        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        
        List<Object[]> result = q.getResultList();
        List<Series> series = new ArrayList<Series>();
        
        for(Object[] o:result)
        {       	
        	series.add(new ListSeries((String)o[0],(Number)o[1]));
        	
        }        
       
        getConfiguration().setSeries(series);

    }

}
