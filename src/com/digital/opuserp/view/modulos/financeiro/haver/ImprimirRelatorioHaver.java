package com.digital.opuserp.view.modulos.financeiro.haver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.HaverDetalhe;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Table;

public class ImprimirRelatorioHaver implements StreamSource{

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ImprimirRelatorioHaver(List<HaverDetalhe> haverDetalhes) throws Exception {
		
		calcular_valor_disponivel(haverDetalhes);
		
		EntityManager em = ConnUtil.getEntity();
		
		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);	
		doc.setMargins(24, 24, 12, 12);
		PdfWriter pdfWriter = null;
		try{
			pdfWriter = PdfWriter.getInstance(doc, baos);
			doc.open();
			
			
			HTMLWorker htmlWorker = new HTMLWorker(doc);
			
			
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			String controle =  "ACESSO-POS";
			Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
			qControle.setParameter("nome", controle);
			qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
											
			ContaBancaria cb = null;
			if(qControle.getResultList().size() ==1){
				cb = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
			}
			
			
			
			URL urlFont  = getClass().getResource("/com/digital/opuserp/font/Arial Narrow.ttf");
			BaseFont base8 = BaseFont.createFont(urlFont.toString(), BaseFont.WINANSI,true);
			Font f8 = new Font(base8, 8f);
			Font f9 = new Font(base8, 9f);
			
				
			byte[] logo = empresa.getLogo_empresa();		
			
			if(logo != null){
				Image imgLogo = Image.getInstance(logo);				
				imgLogo.setAlignment(Element.ALIGN_CENTER);
				
				imgLogo.scalePercent(65.0f);
					
				PdfPCell pCellLogo = new PdfPCell();
		        pCellLogo.addElement(imgLogo);
		        pCellLogo.setPaddingBottom(4);
		        pCellLogo.setPaddingTop(4);
		        pCellLogo.setBorder(0);
		        		
				 PdfPTable tbTopo = new PdfPTable(new float[] {1f});
				 tbTopo.setWidthPercentage(100f);			 
				 tbTopo.addCell(pCellLogo);				  
				 tbTopo.setSpacingBefore(10);				 
			     doc.add(tbTopo);	     
			}
			
			
			
			
			
			
			Font f12 = new Font(base8, 7f);			
			Font f12b = new Font(base8, 8f, Font.BOLD);
			Font f13b = new Font(base8, 13f, Font.BOLD);
			
			
			

			// Fones Enpresa
			StringBuilder fonesEmp = new StringBuilder();
			if(empresa.getDdd_fone1()!=null && empresa.getFone1()!=null){
				fonesEmp.append(empresa.getDdd_fone1()+" "+empresa.getFone1());				
			}else if(empresa.getFone1() !=null){
				fonesEmp.append(empresa.getFone1());		
			}
			
			if(!empresa.getDdd_fone2().isEmpty() && empresa.getDdd_fone2()!=null && !empresa.getFone2().isEmpty() && empresa.getFone2()!=null && !fonesEmp.toString().isEmpty()){
				fonesEmp.append(" / "+empresa.getDdd_fone2()+" "+empresa.getFone2());				
			}else if(empresa.getDdd_fone2()!=null && empresa.getFone2()!=null){	
				fonesEmp.append(empresa.getDdd_fone2()+" "+empresa.getFone2());		
			}else if(empresa.getFone2()!=null){
				fonesEmp.append(empresa.getFone2());
			}
			
			if(empresa.getDdd_fone3()!=null && !empresa.getFone3().isEmpty() && empresa.getFone3()!=null && !fonesEmp.toString().isEmpty()){
				fonesEmp.append(" / "+empresa.getDdd_fone3()+" "+empresa.getFone3());				
			}else if(empresa.getDdd_fone3()!=null && empresa.getFone3()!=null){	
				fonesEmp.append(empresa.getDdd_fone3()+" "+empresa.getFone3());		
			}else if(empresa.getFone3()!=null){
				fonesEmp.append(empresa.getFone3());
			}
			
			if(!empresa.get_0800().isEmpty() && empresa.get_0800()!=null && !fonesEmp.toString().isEmpty()){
				fonesEmp.append(" /  "+empresa.get_0800());				
			}else if(empresa.get_0800()!=null){	
				fonesEmp.append(empresa.get_0800());		
			}

			// CABECALHO
			Paragraph pTopo1 = new Paragraph("DETALHAMENTO DE HAVER",f12);
			pTopo1.setAlignment(Element.ALIGN_CENTER);														
			doc.add(pTopo1);			
		
			DataUtil dtUtil = new DataUtil();
			String date = null;
				
		
//		  	Paragraph pPartTermoAdesao = new Paragraph("TERMO DE ADESÃO:",f12b);	
//			pPartTermoAdesao.setAlignment(Element.ALIGN_LEFT);		 
//			
//			PdfPCell pCellpPartTermoAdesao= new PdfPCell();
//			pCellpPartTermoAdesao.addElement(pPartTermoAdesao);
//			pCellpPartTermoAdesao.setBorderWidth(0);
//	
//	
//			PdfPTable tbPart = new PdfPTable(new float[] {0.80f,0.90f});	
//			tbPart.setWidthPercentage(100f);
//			tbPart.setSpacingAfter(10);			
//			tbPart.addCell(pCellpPartTermo);
//			tbPart.addCell(pCellpPartTermoAdesao);
// 			
//			doc.add(tbPart);
			
				        
			Paragraph p01 = new Paragraph("TIPO",f12b);	
			p01.setAlignment(Element.ALIGN_LEFT);		 
			
			PdfPCell pCell01= new PdfPCell();
			pCell01.addElement(p01);
			pCell01.setBorderWidth(1);
			
			
			Paragraph p02 = new Paragraph("DATA DE EMISSÃO",f12b);	
			p02.setAlignment(Element.ALIGN_LEFT);		 
			
			PdfPCell pCell02= new PdfPCell();
			pCell02.addElement(p02);
			pCell02.setBorderWidth(1);
			
			
			Paragraph p03 = new Paragraph("DOCUMENTO",f12b);	
			p03.setAlignment(Element.ALIGN_LEFT);		 
			
			PdfPCell pCell03= new PdfPCell();
			pCell03.addElement(p03);
			pCell03.setBorderWidth(1);
			
			Paragraph p04 = new Paragraph("Nº DOC",f12b);	
			p04.setAlignment(Element.ALIGN_LEFT);		 
			
			PdfPCell pCell04= new PdfPCell();
			pCell04.addElement(p04);
			pCell04.setBorderWidth(1);
			
			Paragraph p05 = new Paragraph("REFERENTE",f12b);	
			p05.setAlignment(Element.ALIGN_LEFT);		 
			
			PdfPCell pCell05= new PdfPCell();
			pCell05.addElement(p05);
			pCell05.setBorderWidth(1);
			
			Paragraph p06 = new Paragraph("DESCRIÇÃO",f12b);	
			p06.setAlignment(Element.ALIGN_LEFT);		 
			
			PdfPCell pCell06= new PdfPCell();
			pCell06.addElement(p06);
			pCell06.setBorderWidth(1);
			
			Paragraph p07 = new Paragraph("OPERADOR",f12b);	
			p07.setAlignment(Element.ALIGN_LEFT);		 
			
			PdfPCell pCell07= new PdfPCell();
			pCell07.addElement(p07);
			pCell07.setBorderWidth(1);
			
			Paragraph p08 = new Paragraph("VALOR",f12b);	
			p08.setAlignment(Element.ALIGN_LEFT);		 
			
			PdfPCell pCell08= new PdfPCell();
			pCell08.addElement(p08);
			pCell08.setBorderWidth(1);
	
	
			PdfPTable tbPart = new PdfPTable(new float[] {0.3f,0.6f,0.5f,0.5f,1f,1f,0.4f,0.3f});	
			tbPart.setWidthPercentage(100f);
			tbPart.setSpacingBefore(50);			
			tbPart.addCell(pCell01);
			tbPart.addCell(pCell02);
			tbPart.addCell(pCell03);
			tbPart.addCell(pCell04);
			tbPart.addCell(pCell05);
			tbPart.addCell(pCell06);
			tbPart.addCell(pCell07);
			tbPart.addCell(pCell08);
 			
			doc.add(tbPart);
			
			
			for (HaverDetalhe haver : haverDetalhes) {
				Paragraph pTipo = new Paragraph(haver.getTipo(),f12);	
				pTipo.setAlignment(Element.ALIGN_LEFT);		 
				
				PdfPCell pCelltipo= new PdfPCell();
				pCelltipo.addElement(pTipo);
				pCelltipo.setBorderWidth(1);
				
				
				Paragraph pDataEmissao = new Paragraph(DataUtil.formatDateBra(haver.getData_emissao()),f12);	
				pDataEmissao.setAlignment(Element.ALIGN_LEFT);		 
				
				PdfPCell pCellEmissao= new PdfPCell();
				pCellEmissao.addElement(pDataEmissao);
				pCellEmissao.setBorderWidth(1);
				
				
				Paragraph pDoc = new Paragraph(haver.getDoc(),f12);	
				pDoc.setAlignment(Element.ALIGN_LEFT);		 
				
				PdfPCell pCellDoc= new PdfPCell();
				pCellDoc.addElement(pDoc);
				pCellDoc.setBorderWidth(1);
				
				Paragraph pNDoc = new Paragraph(haver.getN_doc(),f12);	
				pNDoc.setAlignment(Element.ALIGN_LEFT);		 
				
				PdfPCell pCellNDoc= new PdfPCell();
				pCellNDoc.addElement(pNDoc);
				pCellNDoc.setBorderWidth(1);
				
				Paragraph pReferente = new Paragraph(haver.getReferente(),f12);	
				pReferente.setAlignment(Element.ALIGN_LEFT);		 
				
				PdfPCell pCellReferente= new PdfPCell();
				pCellReferente.addElement(pReferente);
				pCellReferente.setBorderWidth(1);
				
				Paragraph pDescricao = new Paragraph(haver.getDescricao(),f12);	
				pDescricao.setAlignment(Element.ALIGN_LEFT);		 
				
				PdfPCell pCellDescricao= new PdfPCell();
				pCellDescricao.addElement(pDescricao);
				pCellDescricao.setBorderWidth(1);
				
				Paragraph pOperador = new Paragraph(haver.getOperador(),f12);	
				pOperador.setAlignment(Element.ALIGN_LEFT);		 
				
				PdfPCell pCellOperador= new PdfPCell();
				pCellOperador.addElement(pOperador);
				pCellOperador.setBorderWidth(1);
				
				Paragraph pValor = new Paragraph(Real.formatDbToString(String.valueOf(haver.getValor())),f12);	
				pValor.setAlignment(Element.ALIGN_LEFT);		 
				
				PdfPCell pCellValor= new PdfPCell();
				pCellValor.addElement(pValor);
				pCellValor.setBorderWidth(1);
		
		
				PdfPTable tbItens = new PdfPTable(new float[] {0.3f,0.6f,0.5f,0.5f,1f,1f,0.4f,0.3f});	
				tbItens.setWidthPercentage(100f);
				//tbItens.setSpacingAfter(10);			
				tbItens.addCell(pCelltipo);
				tbItens.addCell(pCellEmissao);
				tbItens.addCell(pCellDoc);
				tbItens.addCell(pCellNDoc);
				tbItens.addCell(pCellReferente);
				tbItens.addCell(pCellDescricao);
				tbItens.addCell(pCellOperador);
				tbItens.addCell(pCellValor);
	 			
				doc.add(tbItens);
			}
	      
			Paragraph pSaldo = new Paragraph("R$ "+Real.formatDbToString(String.valueOf(valor_disponivel)),f13b);	
			pSaldo.setAlignment(Element.ALIGN_LEFT);		 
			
			doc.add(pSaldo);
	     
		}catch(Exception e){
			e.printStackTrace();
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
	
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}	

	double valor_disponivel = 0;
	private void calcular_valor_disponivel(List<HaverDetalhe> haverDetalhes){
		valor_disponivel = 0;
		
		for (HaverDetalhe haver: haverDetalhes) {
			String status = haver.getStatus();
			String tipo = haver.getTipo();
			double valor_linha = haver.getValor();
			
			if(status.equals("ATIVO")){
				if(tipo.equals("ENTRADA")){
					valor_disponivel = valor_disponivel + valor_linha;
				}else{
					valor_disponivel = valor_disponivel - valor_linha;
				}
			}
		}
	}
}
