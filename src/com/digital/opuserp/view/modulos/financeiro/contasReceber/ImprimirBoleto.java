package com.digital.opuserp.view.modulos.financeiro.contasReceber;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import br.com.caelum.stella.boleto.Boleto;
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;

import com.digital.opuserp.util.GeradorBoletoUtil;
import com.vaadin.server.StreamResource.StreamSource;



public class ImprimirBoleto implements StreamSource{

	ByteArrayInputStream baos;

	public ImprimirBoleto(Boleto... boletos) throws Exception {
		
			
			GeradorBoletoUtil gerador = new GeradorBoletoUtil(boletos);
			
			//PdfWriter.getInstance(doc, baos);
			baos = new ByteArrayInputStream(gerador.geraPDF());				
	}
	
	
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return baos;
	}	
	
}

