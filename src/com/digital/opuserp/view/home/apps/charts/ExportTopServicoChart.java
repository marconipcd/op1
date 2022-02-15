package com.digital.opuserp.view.home.apps.charts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GrupoServicoDAO;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class ExportTopServicoChart implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportTopServicoChart(String titulo, String periodicidade, String grupo, String salesOnly, Object SortContainerPropertyId, boolean isSortAscending)throws Exception {

		EntityManager em = ConnUtil.getEntity();
		Query q;
		
		 boolean saleOnly = false;
		if(salesOnly.equals("icons/cart_icon_vazio.png")){
			saleOnly = true;
		}
		
		String sort = null;
		if(SortContainerPropertyId != null){
			sort = getColumnSort(SortContainerPropertyId.toString());
		}else{
			if(saleOnly){
				sort = "QTD";
				isSortAscending = false;
			}else{
				sort = "PRODUTO";
				isSortAscending = true;
			}
		}
		
		String order = null;
		if(isSortAscending){
			order = "ASC";
		}else{
			order = "DESC";
		}
		
		if(!grupo.equals("TODOS OS GRUPOS")){
			if(saleOnly){
				  q = em.createNativeQuery("SELECT (SELECT ID FROM servicos WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
		        			+ "(SELECT NOME FROM servicos WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
		        			+ "(SELECT VALOR_VENDA FROM servicos WHERE ID=pvd.ID_PRODUTO) as VALOR, "
		        			+ "SUM(`QUANTIDADE`) as QTD, "
		        			+ "(SELECT (SELECT NOME FROM  grupo_servico WHERE grupo_servico.ID=servicos.grupo) FROM servicos WHERE servicos.ID=pvd.ID_PRODUTO) as GRUPO, "	        			
		        			+ "(SELECT (SELECT DATA_PV FROM ecf_pre_venda_cabecalho WHERE ecf_pre_venda_cabecalho.ID = ecf_pre_venda_detalhe.ID_ECF_PRE_VENDA_CABECALHO) AS ULTIMA_VENDA FROM ecf_pre_venda_detalhe WHERE ecf_pre_venda_detalhe.ID_PRODUTO =pvd.ID_PRODUTO ORDER BY ID DESC LIMIT 0 , 1 ) as ULTIMA_VENDA "
			        		+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc "
			        		+ "WHERE EXISTS(SELECT NOME FROM servicos WHERE ID=pvd.ID_PRODUTO AND grupo=:grupo) AND "
			        		+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM servicos WHERE ID = pvd.ID_PRODUTO) "
			        		+ "AND pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO = 'PEDIDO' AND pvc.TIPO_VENDA = 'SERVICO' "
			        		+ "AND pvc.SITUACAO = 'F' AND "
			        		+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
			        		+ "AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY "+sort+" "+order);
		        
        	}else{
        		q = em.createNativeQuery("SELECT p.ID as ID_PRODUTO, p.NOME as PRODUTO, p.VALOR_VENDA as VALOR,  "
        				+ "(SELECT NOME FROM  grupo_servico as gp WHERE gp.ID=p.grupo) as GRUPO "        				
        				+ "FROM servicos as p "
        				+ "WHERE "
        				+ "p.STATUS = 'ATIVO' AND "
        				+ "p.grupo = :grupo AND "	        				   				
        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' AND ec.TIPO_VENDA = 'SERVICO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa))) "
        				+ "AND p.EMPRESA_ID = :codEmpresa  ORDER by "+sort+" "+order);
        	}
			
			q.setParameter("grupo", GrupoServicoDAO.findbyName(grupo));
			
		}else{
			if(saleOnly){
				q = em.createNativeQuery("SELECT (SELECT ID FROM servicos WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
	        			+ "(SELECT NOME FROM servicos WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
	        			+ "(SELECT VALOR_VENDA FROM servicos WHERE ID=pvd.ID_PRODUTO) as VALOR, "
	        			+ "SUM(`QUANTIDADE`) as QTD, (SELECT (SELECT NOME FROM  grupo_servico WHERE grupo_servico.ID=servicos.grupo) as GRUPO FROM servicos WHERE servicos.ID=pvd.ID_PRODUTO) as GRUPO, "	        			
	        			+ "(SELECT (SELECT DATA_PV FROM ecf_pre_venda_cabecalho WHERE ecf_pre_venda_cabecalho.ID = ecf_pre_venda_detalhe.ID_ECF_PRE_VENDA_CABECALHO) AS ULTIMA_VENDA FROM ecf_pre_venda_detalhe WHERE ecf_pre_venda_detalhe.ID_PRODUTO =pvd.ID_PRODUTO ORDER BY ID DESC LIMIT 0 , 1 ) as ULTIMA_VENDA "
	        			+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE "
	        			+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM servicos WHERE ID = pvd.ID_PRODUTO) AND "
	        			+ "pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO_VENDA = 'SERVICO' AND pvc.TIPO = 'PEDIDO' AND "
	        			+ "pvc.SITUACAO = 'F' AND pvc.EMPRESA_ID = :codEmpresa AND "
	        			+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
	        			+ "GROUP BY ID_PRODUTO ORDER BY "+sort+" "+order);
	        	 
	        	}else{
	        		q = em.createNativeQuery("SELECT p.ID as ID_PRODUTO, p.NOME as PRODUTO, p.VALOR_VENDA as VALOR,  "
	        				+ "(SELECT NOME FROM  grupo_servico as gp WHERE gp.ID=p.grupo) as GRUPO "        				
	        				+ "FROM servicos as p "
	        				+ "WHERE "
	        				+ "p.STATUS = 'ATIVO' AND "	        				   				
	        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
	        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' AND ec.TIPO_VENDA = 'SERVICO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa))) "
	        				+ "AND p.EMPRESA_ID = :codEmpresa  ORDER by "+sort+" "+order);
	        	}
		}
        q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
        
        if(saleOnly){
        	 if(periodicidade.equals("1 MES")){
  	        	q.setParameter("data1",new DateTime().minusMonths(1).toDate());
  	        	q.setParameter("data2", new DateTime().toDate());
  	        }else if(periodicidade.equals("3 MESES")){
  	        	q.setParameter("data1",new DateTime().minusMonths(3).toDate());
  	        	q.setParameter("data2", new DateTime().toDate());
  	        }else if(periodicidade.equals("1 ANO")){
  	        	q.setParameter("data1",new DateTime().minusYears(1).toDate());
  	        	q.setParameter("data2", new DateTime().toDate());
  	        }else if(periodicidade.equals("2 ANOS")){
  	        	q.setParameter("data1",new DateTime().minusYears(2).toDate());
  	        	q.setParameter("data2", new DateTime().toDate());
  	        }
        }
		
		
		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);		
		Font fCaptionsBold = new Font(FontFamily.HELVETICA, 6, Font.BOLD);
				
		try {
			PdfWriter.getInstance(doc, baos);
			doc.open();
			
			StringBuilder SbForm= new StringBuilder();
			StringBuilder SbVl= new StringBuilder();		
			
			if(saleOnly){
				SbForm.append("PERIODICIDADE:"+"\n"+"GRUPO:"+"\n");			
				SbVl.append(periodicidade+"\n"+grupo+"\n");
			}else{
				SbForm.append("GRUPO:"+"\n");			
				SbVl.append(grupo+"\n");
			}
			
			Font fSubTitulo  = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
			Font fCab = new Font(FontFamily.HELVETICA, 8);
			Paragraph formato = new Paragraph(SbForm.toString(),fCab);
			Paragraph ordenacao = new Paragraph(SbVl.toString(),fSubTitulo);
			
			PdfPCell pCellForm = new PdfPCell();
			pCellForm.setBorderWidth(0);
			pCellForm.addElement(formato);		
			
			PdfPCell pCellVl = new PdfPCell();
			pCellVl.setBorderWidth(0);
			pCellVl.addElement(ordenacao);
			
			PdfPTable tbform = new PdfPTable(new float[]{0.25f,1f});
			tbform.setWidthPercentage(100f);	
			tbform.addCell(pCellForm);
			tbform.addCell(pCellVl);
			tbform.setSpacingAfter(5);		
			
			Paragraph pTitle= new Paragraph(titulo);
			pTitle.setAlignment(Element.ALIGN_CENTER);
			pTitle.setSpacingAfter(10);
			doc.add(pTitle);
			
			final PdfPTable tb;
			final PdfPTable tbTopo;
			
			if(saleOnly){

				tb =  new PdfPTable(new float[] {0.3f,0.3f,2f,0.8f,0.3f,0.3f,0.3f});
				tb.setWidthPercentage(100f);
				
				tbTopo = new PdfPTable(new float[] {0.3f,0.3f,2f,0.8f,0.3f,0.3f,0.3f});
				tbTopo.setWidthPercentage(100f);
				tbTopo.addCell(getCellTopo("Ranking"));
				tbTopo.addCell(getCellTopo("Código"));
				tbTopo.addCell(getCellTopo("Produto"));
				tbTopo.addCell(getCellTopo("Grupo"));
				tbTopo.addCell(getCellTopo("Valor"));
				tbTopo.addCell(getCellTopo("Vendidos"));				
				tbTopo.addCell(getCellTopo("Ultima Venda"));			
			}else{
				tb =  new PdfPTable(new float[] {0.3f,0.3f,2f,0.8f,0.3f});
				tb.setWidthPercentage(100f);
				
				tbTopo = new PdfPTable(new float[] {0.3f,0.3f,2f,0.8f,0.3f});
				tbTopo.setWidthPercentage(100f);				
				
				PdfPCell pCellTop = new PdfPCell();
				pCellTop.setPaddingTop(2);
				pCellTop.setPaddingBottom(4);
				pCellTop.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellTop.setBorderColor(new BaseColor(255, 255, 255));	
				pCellTop.setBorderWidth(1.5f);
				
				tbTopo.addCell(getCellTopo("Ranking"));
				tbTopo.addCell(getCellTopo("Código"));
				tbTopo.addCell(getCellTopo("Produto"));
				tbTopo.addCell(getCellTopo("Grupo"));
				tbTopo.addCell(getCellTopo("Valor"));			
							
			}
			
			Integer i = 1;
			 for (Object ob: q.getResultList()) {
				 
				 if(saleOnly){
					 tb.addCell(getCell(String.valueOf(i),Element.ALIGN_CENTER));
			        	i++;	
					 
					 tb.addCell(getCell(((Object[])ob)[0].toString().toUpperCase(),0));				 
					 tb.addCell(getCell(((Object[])ob)[1].toString().toUpperCase(),0));
					 
					 String grupo2 = "";
					 if(((Object[])ob)[4] != null){
						 grupo2 = ((Object[])ob)[4].toString().toUpperCase();
					 }
					 tb.addCell(getCell(grupo2,0));
					 
					 String valor = "R$ "+Real.formatDbToString(((Object[])ob)[2].toString());
					 tb.addCell(getCell(valor,Element.ALIGN_RIGHT));
					 
					 tb.addCell(getCell(String.valueOf(Float.valueOf(((Object[])ob)[3].toString()).intValue()),Element.ALIGN_CENTER));					 
		        	SimpleDateFormat sdf2 =  new SimpleDateFormat("dd/MM/yyyy");
		        	tb.addCell(getCell(sdf2.format(((Object[])ob)[5]),0));		
				 }else{
			        	
					 tb.addCell(getCell(String.valueOf(i),Element.ALIGN_CENTER));
			        	i++;	
					 
					String valor ="";
					if(((Object[])ob)[2] != null){
						valor = "R$ "+Real.formatDbToString(((Object[])ob)[2].toString());
					}
	        		
	        		tb.addCell(getCell(((Object[])ob)[0].toString().toUpperCase(),0));
	        		tb.addCell(getCell(((Object[])ob)[1].toString().toUpperCase(),0));
	        		
	        		String grupo2 = "";
	        		if(((Object[])ob)[3] != null ){
	        			grupo2 = ((Object[])ob)[3].toString().toUpperCase();
	        		}
	        		tb.addCell(getCell(grupo2,0));
	        		tb.addCell(getCell(valor,Element.ALIGN_RIGHT));
				 }
				
				tb.completeRow();
				
			 }
			

//				Integer i = 0;
//				for(PdfPRow r: tb.getRows()) {
//				  for(PdfPCell c: r.getCells()) {
//					  c.setBorder(0); 
//					  if(i % 2 ==1){
//						  c.setBackgroundColor(BaseColor.GRAY );
//					  }else{
//						  c.setBackgroundColor(BaseColor.WHITE);
//					  }
//				  }			
//				  
//				  i++;
//				  
//				}


			 doc.add(tbform);
			 doc.add(tbTopo);
			 doc.add(tb);
			 
			String s = String.valueOf(q.getResultList().size())+" Registros encontrados";
			Paragraph pLegenda= new Paragraph(s,fCaptionsBold);
			pLegenda.setAlignment(Element.ALIGN_LEFT);
			pLegenda.setSpacingAfter(5);
			doc.add(pLegenda);
															
		} finally {
			if (doc.isOpen() && doc != null) {
				doc.close();
			}
		}

	}
	
	private PdfPCell getCellTopo(String valor){
		Font fCaptionsBold = new Font(FontFamily.HELVETICA, 6, Font.BOLD);						
		PdfPCell pCellTop = new PdfPCell(new Phrase(valor, fCaptionsBold));
		pCellTop.setPaddingTop(2);
		pCellTop.setPaddingBottom(4);
		pCellTop.setBackgroundColor(new BaseColor(114, 131, 151));
		pCellTop.setBorderColor(new BaseColor(255, 255, 255));	
		pCellTop.setBorderWidth(1.5f);
		
		return pCellTop;
	}
	
	private PdfPCell getCell(String valor, int align){
		PdfPCell pCellConteudo = new PdfPCell();
		pCellConteudo.setPaddingTop(2);
		pCellConteudo.setPaddingBottom(4);
		pCellConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
		pCellConteudo.setBorderColor(new BaseColor(255, 255, 255));	
		pCellConteudo.setBorderWidth(1.5f);
		
		Paragraph valorP = null;
		Font fCampo = new Font(FontFamily.HELVETICA, 5);
		valorP = new Paragraph(valor,fCampo);
						
		if(align != 0){
			valorP.setAlignment(align);			
		}
		
		valorP.setAlignment(align); 
		
		pCellConteudo.addElement(valorP);
		
		return pCellConteudo;
	}
	
	private String getColumnSort(String s){
		switch (s) {
		case "Código":
			return "ID_PRODUTO";
			
		case "Produto":
			return "PRODUTO";
			
		case "Grupo":
			return "GRUPO";
			
		case "Valor":
			return "VALOR";
			
		case "Vendido":
			return "QTD";
		
		case "Última Venda":
			return "ULTIMA_VENDA";
			
		default:
			return null;			
		}
	}
	
	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}

