package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.MateriaisAlocadosDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class ProtocoloRetiradaMaterial implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ProtocoloRetiradaMaterial(Integer id, final Usuario tecnico, Date data)throws Exception {

		EntityManager em = ConnUtil.getEntity();		
		Document doc = new Document(PageSize.A4, 24, 24, 14, 14);
		
		try {
			PdfWriter.getInstance(doc, baos);
			doc.open();
			

			//Logo
			byte[] logo = OpusERP4UI.getEmpresa().getLogo_empresa();
			Image imgLogo = Image.getInstance(logo);
			imgLogo.setAlignment(Element.ALIGN_CENTER);
			imgLogo.setBorder(0);
			imgLogo.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.BOTTOM);
			imgLogo.setBorderColor(new BaseColor(255,255,255));
			
			
			
			PdfPTable pTable1 = new PdfPTable(new float[]{0.7f,1.5f,0.7f});
			pTable1.getDefaultCell().setBorder(0);
			pTable1.addCell(getCellTitulo("", Element.ALIGN_LEFT, false,14));			
			pTable1.addCell(imgLogo); 
			pTable1.addCell(getCellTitulo("", Element.ALIGN_LEFT, false,14));
			
			
			doc.add(pTable1);			
			
			PdfPTable pTable2 = new PdfPTable(new float[]{1f});		
			pTable2.addCell(getCellTitulo("PROTOCOLO DE RETIRADA DE MATERIAL - Nº "+id.toString(), Element.ALIGN_CENTER, true,14));
			pTable2.setSpacingAfter(30f);
			pTable2.setSpacingBefore(30f);
			doc.add(pTable2);
			
			PdfPTable pTable3 = new PdfPTable(new float[]{0.2f,1f});		
			pTable3.addCell(getCellTitulo("TÉCNICO: ", Element.ALIGN_LEFT, false,14));
			pTable3.addCell(getCellTitulo(tecnico.getUsername().toUpperCase(), Element.ALIGN_LEFT, true,14));
			pTable3.setSpacingAfter(25f);
			pTable3.setSpacingBefore(10f);
			doc.add(pTable3);
			
			PdfPTable pTable4 = new PdfPTable(new float[]{1f,0.3f});		
			pTable4.addCell(getCellTituloComBorda("PRODUTO", Element.ALIGN_LEFT, true));
			pTable4.addCell(getCellTituloComBorda("QUANTIDADE", Element.ALIGN_LEFT, true));		
			
			doc.add(pTable4);
			
			
			
			List<MateriaisAlocadosDetalhe> materiais = OseDAO.getMateriais(id);
			
			for (MateriaisAlocadosDetalhe material : materiais) {
				
				
				Produto p = ProdutoDAO.find(material.getMaterial());
				
				PdfPTable pTableItens = new PdfPTable(new float[]{1f,0.3f});		
				pTableItens.addCell(getCellTituloComBorda(p.getNome(), Element.ALIGN_LEFT, false));
				pTableItens.addCell(getCellTituloComBorda(material.getQtd().toString(), Element.ALIGN_RIGHT, false));			
				
				
				doc.add(pTableItens);
			}
			
					
			
			
			
			PdfPTable pTableLinhaAssinatura = new PdfPTable(new float[]{1f});		
			pTableLinhaAssinatura.addCell(getCellTitulo("_____________________________________", Element.ALIGN_LEFT, false,10));	
			pTableLinhaAssinatura.setSpacingBefore(30f);
			doc.add(pTableLinhaAssinatura);
			
			PdfPTable pTableAssinatura = new PdfPTable(new float[]{1f});		
			pTableAssinatura.addCell(getCellTitulo(tecnico.getUsername().toUpperCase(), Element.ALIGN_LEFT, false,10));					
			doc.add(pTableAssinatura);
			
			
			SimpleDateFormat sdfDia = new SimpleDateFormat("dd");
			SimpleDateFormat sdfMes = new SimpleDateFormat("MMMMM");
			SimpleDateFormat sdfAno = new SimpleDateFormat("y");
			
			PdfPTable pTableData = new PdfPTable(new float[]{1f});		
			pTableData.addCell(getCellTitulo("Belo Jardim/PE, "+sdfDia.format(data)+" de "+sdfMes.format(data)+" de "+sdfAno.format(data)+"", Element.ALIGN_RIGHT, true,12));		
			pTableData.setSpacingBefore(30f);
			doc.add(pTableData);
			
			PdfPTable pTableRazaoSocial = new PdfPTable(new float[]{1f});		
			pTableRazaoSocial.addCell(getCellTitulo(OpusERP4UI.getEmpresa().getRazao_social().toUpperCase(), Element.ALIGN_RIGHT, false,10));		
			pTableRazaoSocial.setSpacingBefore(30f);
			doc.add(pTableRazaoSocial);
			

						
			
		} finally {
			if (doc.isOpen() && doc != null) {
				doc.close();
			}
		}

	}
	
	private PdfPCell getCellTituloComBorda(String s, Integer align, boolean negrito){
		float paddingTop=0;
		float paddingBottom=5;
		
		
		Font fCampo;
		if(!negrito){
			fCampo = new Font(FontFamily.HELVETICA, 10);
		}else{
			fCampo = new Font(FontFamily.HELVETICA, 10,Font.BOLD);
		}
		
		PdfPCell pCell = new PdfPCell();
		//pCell.setPaddingBottom(paddingBottom);
		//pCell.setPaddingTop(paddingTop);	
		pCell.setBorderWidthLeft(1f);
		pCell.setBorderWidthRight(1f);
		//pCell.
		
		Paragraph p = new Paragraph(s,fCampo);
		p.setAlignment(align);
		
		
		pCell.addElement(p);
		//pCell.setBackgroundColor(new BaseColor(242,242,242));
		pCell.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT | Rectangle.BOTTOM);
		//pCell.setBorderColor(new BaseColor(242, 242, 242));
		
		return pCell;
	}
	
	
	private PdfPCell getCellTitulo(String s, Integer align, boolean negrito, Integer tamanho){
		float paddingTop=0;
		float paddingBottom=5;
		
		
		Font fCampo;
		if(!negrito){
			fCampo = new Font(FontFamily.HELVETICA, tamanho);
		}else{
			fCampo = new Font(FontFamily.HELVETICA, tamanho,Font.BOLD);
		}
		
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingBottom(paddingBottom);
		pCell.setPaddingTop(paddingTop);	
		
		Paragraph p = new Paragraph(s,fCampo);
		p.setAlignment(align);
		
		
		pCell.addElement(p);
		//pCell.setBackgroundColor(new BaseColor(242,242,242));
		pCell.setBorder(0);
				
		return pCell;
	}
	
	
	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}

