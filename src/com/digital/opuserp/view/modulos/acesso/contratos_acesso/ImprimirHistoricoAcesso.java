package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.util.ConnUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class ImprimirHistoricoAcesso implements StreamSource{

	
	
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	
	public ImprimirHistoricoAcesso(Integer codAc) throws Exception {
		
		EntityManager em = ConnUtil.getEntity();
		
		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);	
		doc.setMargins(12, 12, 12, 12);
		PdfWriter pdfWriter = null;
		try{
			pdfWriter = PdfWriter.getInstance(doc, baos);
			doc.open();
			
		
			
			HTMLWorker htmlWorker = new HTMLWorker(doc);
			
			
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			AcessoCliente acessoCliente = em.find(AcessoCliente.class, codAc);
			Cliente cliente = em.find(Cliente.class, acessoCliente.getCliente().getId());
			
			
			Query qEnd = em.createQuery("select e from Endereco e where e.clientes = :codCliente", Endereco.class);
			qEnd.setParameter("codCliente", new Cliente(cliente.getId()));			
			Endereco end = null;
			if(qEnd.getResultList().size() > 0){
				end = (Endereco) qEnd.getSingleResult();
			}else{
				end = new Endereco();
				end.setCep("0000");
				end.setEndereco("ENDERECO NÃO FOI ENCONTRADO!");
			}
				
			byte[] logo = empresa.getLogo_empresa();		
			
			if(logo != null){
				Image imgLogo = Image.getInstance(logo);				
				imgLogo.setAlignment(Element.ALIGN_CENTER);
				imgLogo.setSpacingAfter(8);			
				
				imgLogo.scalePercent(76.0f);
								
				doc.add(imgLogo);			
			}
			
			Font fEmpresa = new Font(FontFamily.HELVETICA, 7, Font.BOLD);
			Font f = new Font(FontFamily.HELVETICA, 7);
			
			
			

			
			
		}finally{
			if(doc.isOpen() && doc != null){
				doc.close();
			}
		}		
	}
	
	public static float PixelsToPoints(float value,int dpi)
	{
	   return value / dpi * 72;
	}
	
	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}

}
