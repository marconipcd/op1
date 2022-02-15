package com.digital.opuserp.view.modulos.pedido.rma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.RmaDetalhe;
import com.digital.opuserp.domain.RmaMestre;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class AutorizacaoRecebimento implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public AutorizacaoRecebimento(RmaMestre rma)throws Exception {

		EntityManager em = ConnUtil.getEntity();
		
		Document doc = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
		
		try {
			PdfWriter.getInstance(doc, baos);
			doc.open();
			
			//Logo
			byte[] logo = OpusERP4UI.getEmpresa().getLogo_empresa();
			Image imgLogo = Image.getInstance(logo);
			imgLogo.setAlignment(Element.ALIGN_CENTER);
			imgLogo.setSpacingBefore(10);
			imgLogo.setSpacingAfter(5);

			//Estilos de Fonts			
			Font fTitulo  = new Font(FontFamily.HELVETICA, 13, Font.BOLD);
			Font fTituloNormal  = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
			
			//titulo
			Paragraph pTitle= new Paragraph("Autorização de Recebimento", fTitulo);
			pTitle.setAlignment(Element.ALIGN_CENTER);
			pTitle.setSpacingAfter(10);
			
			PdfPCell pCab = new PdfPCell();
			pCab.setBorderWidthTop(1f);
			pCab.setBorderWidthBottom(1f);
			pCab.setBorderWidthLeft(0);
			pCab.setBorderWidthRight(0);
			pCab.addElement(pTitle);
			
			PdfPTable tbCab = new PdfPTable(new float[] {1f});
			tbCab.setWidthPercentage(100f);
			tbCab.setSpacingBefore(5);
			tbCab.setSpacingAfter(5);
			tbCab.addCell(pCab);
			
			Paragraph pSegundaLinha= new Paragraph("RECEBIMENTO 99CGX1 AUTORIZAÇÃO POSTAGEM Nota: 1 Série: 1 Gerada "+DataUtil.formatDateBra(new Date())+" Cliente : "+OpusERP4UI.getEmpresa().getRazao_social()+" ", fTituloNormal);
			pSegundaLinha.setAlignment(Element.ALIGN_LEFT);
			pSegundaLinha.setSpacingAfter(10); 
			
			PdfPCell cellSegundaLinha = new PdfPCell();			
			cellSegundaLinha.addElement(pSegundaLinha);
			cellSegundaLinha.setBorderWidthTop(0);
			cellSegundaLinha.setBorderWidthBottom(0);
			cellSegundaLinha.setBorderWidthLeft(0);
			cellSegundaLinha.setBorderWidthRight(0);
			
			
			PdfPTable tbSegundaLinha = new PdfPTable(new float[] {1f});
			tbSegundaLinha.setWidthPercentage(100f);
			tbSegundaLinha.setSpacingBefore(5);
			tbSegundaLinha.setSpacingAfter(5);
			tbSegundaLinha.addCell(cellSegundaLinha);

			PdfPTable tbTopoTabela = new PdfPTable(new float[] {1f, 0.3f, 0.3f, 0.4f});
			tbTopoTabela.setWidthPercentage(100f);
			tbTopoTabela.setSpacingBefore(0);
			tbTopoTabela.setSpacingAfter(0);
			tbTopoTabela.addCell(getCellTopoTabela("Item"));
			tbTopoTabela.addCell(getCellTopoTabela("Nota Fiscal"));
			tbTopoTabela.addCell(getCellTopoTabela("Unitário"));
			tbTopoTabela.addCell(getCellTopoTabela("Serial"));
			
			doc.add(tbCab);
			doc.add(tbSegundaLinha);
			doc.add(tbTopoTabela);
			
//			List<RmaDetalhe>itens =  RmaDAO.getRmaPorFornecedor(rma.getFornecedor());
//			double vlr_custo = 0;
//			for (RmaDetalhe item: itens) {
//				
//				PdfPTable tbDados = new PdfPTable(new float[] {1f, 0.3f, 0.3f, 0.4f});
//				tbDados.setWidthPercentage(100f);			
//				tbDados.setSpacingBefore(0);
//				tbDados.setSpacingAfter(0);
//				tbDados.addCell(getCellConteudoTabela(item.getProduto().getNome(),Element.ALIGN_LEFT));
//				tbDados.addCell(getCellConteudoTabela(item.getNf_compra(),Element.ALIGN_LEFT));
//				tbDados.addCell(getCellConteudoTabela("R$ "+Real.formatDbToString(String.valueOf(item.getProduto().getValorCusto())),Element.ALIGN_LEFT));
//				tbDados.addCell(getCellConteudoTabela(item.getSerial().getSerial(),Element.ALIGN_LEFT));
//				
//				vlr_custo = vlr_custo + item.getProduto().getValorCusto();
//				doc.add(tbDados);
//			}
			
			
			PdfPTable tbFooter = new PdfPTable(new float[] {1f, 0.3f, 0.3f, 0.4f});
			tbFooter.setWidthPercentage(100f);			
			tbFooter.setSpacingBefore(0);
			tbFooter.setSpacingAfter(0);
			//tbFooter.addCell(getCellFooterTabela("Peso BRUTO 3,10  Volumes "+String.valueOf(itens.size()), Element.ALIGN_LEFT));
			tbFooter.addCell(getCellFooterTabela("", Element.ALIGN_LEFT));
			//tbFooter.addCell(getCellFooterTabela("R$ "+Real.formatDbToString(String.valueOf(vlr_custo)), Element.ALIGN_LEFT));
			tbFooter.addCell(getCellFooterTabela("", Element.ALIGN_LEFT));
			
			doc.add(tbFooter);
			
		} finally {
			if (doc.isOpen() && doc != null) {
				doc.close();
			}
		}

	}
	
	public PdfPCell getCellTopoTabela(String s){
		Font fTituloNormal  = new Font(FontFamily.HELVETICA, 7, Font.BOLD);
		
		Paragraph p= new Paragraph(s, fTituloNormal);
		p.setAlignment(Element.ALIGN_LEFT);
		p.setSpacingAfter(10); 
		
		PdfPCell cell = new PdfPCell();			
		cell.setPadding(0);
		cell.addElement(p);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1f);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		
		return cell;
	}
	
	public PdfPCell getCellConteudoTabela(String s, Integer orientacao){
		
		Font fTituloNormal  = new Font(FontFamily.HELVETICA, 7, Font.NORMAL);
		
		Paragraph p= new Paragraph(s, fTituloNormal);
		p.setAlignment(orientacao);
		p.setSpacingAfter(10); 
		
		PdfPCell cell = new PdfPCell();			
		cell.setPadding(0);
		cell.addElement(p);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(0);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		
		if(orientacao == Element.ALIGN_LEFT){
			cell.setPaddingRight(0);
		}else{
			cell.setPaddingRight(25);
		}
		
		return cell;
	}
	
	public PdfPCell getCellFooterTabela(String s, Integer orientacao){
		
		Font fTituloNormal  = new Font(FontFamily.HELVETICA, 7, Font.BOLD);
		
		Paragraph p= new Paragraph(s, fTituloNormal);
		p.setAlignment(orientacao);
		p.setSpacingAfter(10); 
		
		PdfPCell cell = new PdfPCell();			
		cell.setPadding(0);
		cell.addElement(p);
		cell.setBorderWidthTop(1f);
		cell.setBorderWidthBottom(0);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		
		if(orientacao == Element.ALIGN_LEFT){
			cell.setPaddingRight(0);
		}else{
			cell.setPaddingRight(25);
		}
		
		return cell;
	}
	
	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
