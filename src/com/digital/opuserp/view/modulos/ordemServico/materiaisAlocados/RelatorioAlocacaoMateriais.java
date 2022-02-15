package com.digital.opuserp.view.modulos.ordemServico.materiaisAlocados;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesEstoqueMovel;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Veiculos;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class RelatorioAlocacaoMateriais implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	
	private boolean find(List<String> lista, String s){
		
		for (String string : lista) {
			if(string.equals(s)){
				return true;
			}
		}
		
		return false;
	}
	
	public RelatorioAlocacaoMateriais(Date data1, Date data2, Veiculos v) throws Exception{
				
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE ALOCAÇÃO",fTitulo);
			pExport.setAlignment(Element.ALIGN_CENTER);
			pExport.setSpacingAfter(10);
			doc.add(pExport);
			
			PdfPTable tbCabData = new PdfPTable(new float[]{0.25f,1f});
			tbCabData.setWidthPercentage(100f);	
			tbCabData.addCell(getCellHeadCab("DATA"));
			tbCabData.addCell(getCellHeadCab(sdf1.format(data_inicio) + " a " +sdf1.format(data_final) ));
			doc.add(tbCabData);
						
			//Usuários			
			Query q = em.createQuery("select v from AlteracoesEstoqueMovel v where v.data_alteracao >= :di and v.data_alteracao <= :df and v.estoqueMovel.veiculo=:veiculo and v.descricao like '%ALOCADO%'", AlteracoesEstoqueMovel.class);
			q.setParameter("di", data_inicio);
			q.setParameter("df", data_final);
			q.setParameter("veiculo", v);
				
			
			PdfPCell pCellBranco = new PdfPCell();
			pCellBranco.setBorder(0); 
			
			

			PdfPTable tb1 = new PdfPTable(new float[] { 1f});
			tb1.setWidthPercentage(100f);
			tb1.addCell(getCellHeadCab("VEICULO: "+veiculo.getCod_veiculo()+" - "+veiculo.getMarca()));
			tb1.setSpacingAfter(new Float(10f));
			tb1.setSpacingBefore(new Float(20f));
			
			doc.add(tb1);
			List<String> produtos = new ArrayList<>();
			for (AlteracoesEstoqueMovel o: (List<AlteracoesEstoqueMovel>)q.getResultList()) {
				
				if(!find(produtos, o.getEstoqueMovel().getProduto().getNome())){
					produtos.add(o.getEstoqueMovel().getProduto().getNome());
					PdfPTable tbProduto = new PdfPTable(new float[] { 0.1f,1f});
					tbProduto.setWidthPercentage(100f);					
					tbProduto.addCell(getCellHeadCab("PRODUTO:"));
					tbProduto.addCell(getCellHeadCab(o.getEstoqueMovel().getProduto().getNome()));	
					tbProduto.setSpacingAfter(new Float(2f));
					tbProduto.setSpacingBefore(new Float(10f));
					
					doc.add(tbProduto);
					
					PdfPTable tbProdutoCab1 = new PdfPTable(new float[] { 0.05f, 0.12f, 0.23f,0.03f});
					tbProdutoCab1.setWidthPercentage(100f);
					tbProdutoCab1.addCell(pCellBranco);
					tbProdutoCab1.addCell(getCellHeadCinza("DATA"));
					tbProdutoCab1.addCell(getCellHeadCinza("USUÁRIO"));
					tbProdutoCab1.addCell(getCellHeadCinza("QTD.:"));
					
					doc.add(tbProdutoCab1);
				
					double total = 0;
					for (AlteracoesEstoqueMovel o2: (List<AlteracoesEstoqueMovel>)q.getResultList()) {
		
						
						if(o2.getEstoqueMovel().getProduto().equals(o.getEstoqueMovel().getProduto())){
							
							PdfPTable tbProdutoCab = new PdfPTable(new float[] { 0.05f, 0.12f, 0.23f,0.03f});
							tbProdutoCab.setWidthPercentage(100f);
							tbProdutoCab.addCell(pCellBranco);
							tbProdutoCab.addCell(getCellHeadCinza(sdf1.format(o2.getData_alteracao())));
							tbProdutoCab.addCell(getCellHeadCinza(o2.getUsuario().getUsername()));
							tbProdutoCab.addCell(getCellHeadCinzaRight(o2.getQtd().toString()));
							
							doc.add(tbProdutoCab);
								
							total = total + o2.getQtd();
						}
							
					}
				
					PdfPTable tbItensTotal = new PdfPTable(new float[] { 0.05f, 0.12f, 0.23f,0.18f});
					tbItensTotal.setWidthPercentage(100f);
					tbItensTotal.addCell(pCellBranco);
					tbItensTotal.addCell(pCellBranco);								
					tbItensTotal.addCell(getCellHeadBoldRight("TOTAL ALOCADO: "));
					tbItensTotal.addCell(getCellHeadCinzaRight(Real.formatDbToString(new String().valueOf(total))));
					
					doc.add(tbItensTotal);
				
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
