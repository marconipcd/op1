package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class RelatorioUsoMateriais implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	public RelatorioUsoMateriais(Date data1, Date data2, Veiculos v) throws Exception{
				
		EntityManager em = ConnUtil.getEntity();

		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);	
		
		
		Font fCaptions = new Font(FontFamily.COURIER, 6, Font.BOLD);
		Font fCampo = new Font(FontFamily.HELVETICA, 5,Font.BOLD);
		Font fCampoTecnico = new Font(FontFamily.HELVETICA, 5,Font.BOLD);
		Font fCampoBold = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
		Font fConteudoBold = new Font(FontFamily.HELVETICA, 7, Font.BOLD);
		Font fConteudo = new Font(FontFamily.HELVETICA, 7);
		Font fTituloBold  = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
		Font fTitulo  = new Font(FontFamily.HELVETICA, 10);
		Font fSubTitulo  = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
		Font fCab = new Font(FontFamily.HELVETICA, 8);

		float paddingTop=0;
		float paddingBottom=5;
		
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		Date data_inicio = data1;
		Date data_final  = data2;			
		Veiculos veiculo = v;
		
		try{
			PdfWriter.getInstance(doc, baos);
			doc.open();
			
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			DataUtil dtUtil = new DataUtil();
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");	
			String hora = " às "+ sdf.format(new Date());
			String data = dtUtil.getDataExtenso(new Date());

			StringBuilder SbCabecalho = new StringBuilder();
			SbCabecalho.append("OPUS ERP4"+"\n"+"EMPRESA:"+"\n"+"EMISSÃO:"+"\n"+"OPERADOR:");
			
			StringBuilder SbCabecalhoVl = new StringBuilder();
			SbCabecalhoVl.append("\n"+empresa.getNome_fantasia()+"\n"+data+hora+"\n"+OpusERP4UI.getUsuarioLogadoUI().getUsername());
			
			Paragraph Pcabecalho = new Paragraph(SbCabecalho.toString(),fCab);
			Pcabecalho.setAlignment(Element.ALIGN_LEFT);	
			
			Paragraph PcabecalhoVl = new Paragraph(SbCabecalhoVl.toString(),fSubTitulo);										
			PcabecalhoVl.setAlignment(Element.ALIGN_LEFT);
			PcabecalhoVl.setSpacingAfter(20);
				
			PdfPCell cellCab = new PdfPCell();
			cellCab.setBorderWidth(0);
			cellCab.addElement(Pcabecalho);
			
			PdfPCell cellCabVl = new PdfPCell();
			cellCabVl.setBorderWidth(0);
			cellCabVl.addElement(PcabecalhoVl);

			PdfPTable tbCab = new PdfPTable(new float[]{0.25f,1f});
			tbCab.setWidthPercentage(100f);	
			tbCab.addCell(cellCab);
			tbCab.addCell(cellCabVl);
			
			doc.add(tbCab);

			// SUBTITULO
			Paragraph pExport = new Paragraph("RELATÓRIO DE USO DE MATERIAIS",fTitulo);
			pExport.setAlignment(Element.ALIGN_CENTER);
			pExport.setSpacingAfter(10);
			doc.add(pExport);
			
			PdfPTable tbCabData = new PdfPTable(new float[]{0.25f,1f});
			tbCabData.setWidthPercentage(100f);	
			tbCabData.addCell(getCellHeadCab("DATA"));
			tbCabData.addCell(getCellHeadCab(sdf1.format(data_inicio) + " a " +sdf1.format(data_final) ));
			doc.add(tbCabData);
						
			//Usuários			
			Query q = em.createNativeQuery("select v.COD_VEICULO, v.MODELO, v.ID from ose o, veiculos v  WHERE "
					+ "o.VEICULO_ID = v.ID  AND o.DATA_FECHAMENTO >= :di and o.DATA_FECHAMENTO <= :df group by v.COD_VEICULO");
			q.setParameter("di", data_inicio);
			q.setParameter("df", data_final);
			//q.setParameter("v", veiculo);
				
			
			PdfPCell pCellBranco = new PdfPCell();
			pCellBranco.setBorder(0); 
			
			for (Object o: q.getResultList()) {
						
				if(((Object[])o)[0].toString().equals(veiculo.getCod_veiculo().toString())){
					
				
					PdfPTable tb1 = new PdfPTable(new float[] { 1f});
					tb1.setWidthPercentage(100f);
					tb1.addCell(getCellHeadCab("VEICULO: "+((Object[])o)[0].toString()+" - "+((Object[])o)[1].toString()));
					tb1.setSpacingAfter(new Float(10f));
					tb1.setSpacingBefore(new Float(20f));
					
					doc.add(tb1);
					
					PdfPTable tbOsCab = new PdfPTable(new float[] { 0.05f, 0.12f, 0.23f,0.18f,0.2f,0.8f,0.09f});
					tbOsCab.setWidthPercentage(100f);
					tbOsCab.addCell(pCellBranco);
					tbOsCab.addCell(getCellHeadCinza("OS"));
					tbOsCab.addCell(getCellHeadCinza("TECNICO"));
					tbOsCab.addCell(getCellHeadCinza("DATA"));
					tbOsCab.addCell(getCellHeadCinza("GRUPO"));
					tbOsCab.addCell(getCellHeadCinza("CLIENTE"));
					tbOsCab.addCell(getCellHeadCinza("QTD"));
					
					doc.add(tbOsCab);
					
					
				
				}	
			
			}
			
			
			
			
			//------RESUMO
			//--------------------------------
			PdfPTable tbResumoCab = new PdfPTable(new float[] { 0.8f,  0.2f, 1f});
			tbResumoCab.setWidthPercentage(100f);
			tbResumoCab.addCell(getCellHeadBold("RESUMO"));
			//tbResumoCab.addCell(getCellHeadBold("TOTAL RETIRADO"));
			tbResumoCab.addCell(getCellHeadBold("TOTAL UTILIZADO"));
			tbResumoCab.addCell(getCellHeadBold(""));
			tbResumoCab.setSpacingBefore(20f);
			
			doc.add(tbResumoCab);
			
			//---------------------------------------------------Produtos			
			Query qProdutos = em.createNativeQuery("select p.ID, p.NOME from ose o, veiculos v, ose_produto op, produto p  WHERE "
					+ "op.PRODUTO_ID = p.ID "
					+ "AND  op.OSE_ID = o.ID "
					+ "AND o.VEICULO_ID = v.ID "
					+ "AND o.DATA_FECHAMENTO >= :di "
					+ "AND o.DATA_FECHAMENTO <= :df "
					+ "GROUP BY p.NOME");
			
			qProdutos.setParameter("di", data_inicio);
			qProdutos.setParameter("df", data_final);
			
			double total = 0;
			
			for (Object o : qProdutos.getResultList()) {
				
				
				Query qProdutosUtilizado = em.createNativeQuery("select SUM(op.qtd) from ose o, veiculos v, ose_produto op, produto p  WHERE op.PRODUTO_ID = p.ID AND  op.OSE_ID = o.ID "
						+ "AND o.VEICULO_ID = v.ID AND o.DATA_FECHAMENTO >= :di and o.DATA_FECHAMENTO <= :df and p.ID=:produto group by p.NOME");
				
				qProdutosUtilizado.setParameter("di", data_inicio);
				qProdutosUtilizado.setParameter("df", data_final);
				qProdutosUtilizado.setParameter("produto",((Object[])o)[0].toString());
				
				PdfPTable tbResumoProdutos = new PdfPTable(new float[] { 0.8f, 0.2f, 1f});
				tbResumoProdutos.setWidthPercentage(100f);
				tbResumoProdutos.addCell(getCellHeadNormal(((Object[])o)[1].toString()));
				
				tbResumoProdutos.addCell(getCellHeadNormalRight(Real.formatDbToString(qProdutosUtilizado.getSingleResult().toString())));
				tbResumoProdutos.addCell(getCellHeadBold(""));
				
				//doc.add(tbResumoProdutos);
				
				
				total   = total + new Double(qProdutosUtilizado.getSingleResult().toString());
			}	
			//---------------------------------------------------Produtos
			
			
			
			
			
			
							
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(doc.isOpen() && doc != null){
				doc.close();
			}
		}
		
	}
	
	private PdfPCell getCellHeadBoldRight(String s){
		float paddingTop=0;
		float paddingBottom=5;
		
		Font fCampo = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
		
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingBottom(paddingBottom);
		pCell.setPaddingTop(paddingTop);
		
		Paragraph p = new Paragraph(s,fCampo);
		p.setAlignment(Element.ALIGN_RIGHT);
		
		pCell.addElement(p);
		pCell.setBorder(1);
		pCell.setBorderColor(new BaseColor(255, 255, 255));
				
		return pCell;
	}
	
	private PdfPCell getCellHeadBold(String s){
		float paddingTop=0;
		float paddingBottom=5;
		
		Font fCampo = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
		
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingBottom(paddingBottom);
		pCell.setPaddingTop(paddingTop);	
		pCell.addElement(new Phrase( s,  fCampo));
		pCell.setBorder(1);
		pCell.setBorderColor(new BaseColor(255, 255, 255));
				
		return pCell;
	}
	
	private PdfPCell getCellHeadCab(String s){
		float paddingTop=0;
		float paddingBottom=5;
		
		Font fCampo = new Font(FontFamily.HELVETICA, 8);
		
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingBottom(paddingBottom);
		pCell.setPaddingTop(paddingTop);	
		pCell.addElement(new Phrase( s,  fCampo));
		pCell.setBorder(1);
		pCell.setBorderColor(new BaseColor(255, 255, 255));
				
		return pCell;
	}
	
	private PdfPCell getCellHeadNormalRight(String s){
		float paddingTop=0;
		float paddingBottom=5;
		
		Font fCampo = new Font(FontFamily.HELVETICA, 5);
		
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingBottom(paddingBottom);
		pCell.setPaddingTop(paddingTop);	
		
		Paragraph p = new Paragraph(s,fCampo);
		p.setAlignment(Element.ALIGN_RIGHT);
		
		pCell.addElement(p);
		pCell.setBorder(1);
		pCell.setBorderColor(new BaseColor(255, 255, 255));
				
		return pCell;
	}
	
	private PdfPCell getCellHeadNormal(String s){
		float paddingTop=0;
		float paddingBottom=5;
		
		Font fCampo = new Font(FontFamily.HELVETICA, 5);
		
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingBottom(paddingBottom);
		pCell.setPaddingTop(paddingTop);	
		pCell.addElement(new Phrase( s,  fCampo));
		pCell.setBorder(1);
		pCell.setBorderColor(new BaseColor(255, 255, 255));
				
		return pCell;
	}
	
	private PdfPCell getCellHeadCinzaRight(String s){
		float paddingTop=0;
		float paddingBottom=5;
		
		Font fCampo = new Font(FontFamily.HELVETICA, 5,Font.BOLD);
		
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingBottom(paddingBottom);
		pCell.setPaddingTop(paddingTop);	
		
		Paragraph p = new Paragraph(s,fCampo);
		p.setAlignment(Element.ALIGN_RIGHT);
		
		pCell.addElement(p);
		pCell.setBackgroundColor(new BaseColor(204,204,204));
		pCell.setBorder(1);
		pCell.setBorderColor(new BaseColor(255, 255, 255));
		
		return pCell;
	}
	
	private PdfPCell getCellHeadCinza(String s){
		float paddingTop=0;
		float paddingBottom=5;
		
		Font fCampo = new Font(FontFamily.HELVETICA, 5,Font.BOLD);
		
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingBottom(paddingBottom);
		pCell.setPaddingTop(paddingTop);	
		pCell.addElement(new Phrase( s,  fCampo));
		pCell.setBackgroundColor(new BaseColor(204,204,204));
		pCell.setBorder(1);
		pCell.setBorderColor(new BaseColor(255, 255, 255));
		
		return pCell;
	}
	
	private PdfPCell getCellHeadRight(String s){
		float paddingTop=0;
		float paddingBottom=5;
		
		Font fCampo = new Font(FontFamily.HELVETICA, 5);
		
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingBottom(paddingBottom);
		pCell.setPaddingTop(paddingTop);	
		
		Paragraph p = new Paragraph(s,fCampo);
		p.setAlignment(Element.ALIGN_RIGHT);
		
		pCell.addElement(p);
		pCell.setBackgroundColor(new BaseColor(242,242,242));
		pCell.setBorder(1);
		pCell.setBorderColor(new BaseColor(255, 255, 255));
		
		return pCell;
	}
	
	private PdfPCell getCellHead(String s){
		float paddingTop=0;
		float paddingBottom=5;
		
		Font fCampo = new Font(FontFamily.HELVETICA, 5);
		
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingBottom(paddingBottom);
		pCell.setPaddingTop(paddingTop);	
		pCell.addElement(new Phrase( s,  fCampo));
		pCell.setBackgroundColor(new BaseColor(242,242,242));
		pCell.setBorder(1);
		pCell.setBorderColor(new BaseColor(255, 255, 255));
		
		return pCell;
	}

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
		
	}
}
