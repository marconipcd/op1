package com.digital.opuserp.view.home.apps.charts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GrupoProdutoDAO;
import com.digital.opuserp.dao.MarcaDAO;
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

public class ExportTopProdutosChart implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportTopProdutosChart(String titulo, String periodicidade, String grupo,String marca, String salesOnly, Object SortContainerPropertyId, boolean isSortAscending)throws Exception {

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
		
		if(!grupo.equals("TODOS OS GRUPOS") || !marca.equals("TODOS AS MARCAS")){
			
			if(!grupo.equals("TODOS OS GRUPOS")){ 
			if(saleOnly){
		        q = em.createNativeQuery("SELECT (SELECT ID FROM produto WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
	        			+ "(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
	        			+ "(SELECT VALOR_VENDA FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR, "
	        			+ "SUM(`QUANTIDADE`) as QTD, (SELECT (SELECT NOME_GRUPO FROM  grupo_produto WHERE grupo_produto.ID=produto.GRUPO_ID) FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as GRUPO, "
	        			+ "(SELECT (SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=produto.ID_UNIDADE_PRODUTO) AS UNIDADE FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as UNIDADE, "
	        			+ "(SELECT QTD_ESTOQUE FROM produto WHERE ID=pvd.ID_PRODUTO) as QTD_ESTOQUE, "
	        			+ "(SELECT (SELECT DATA_PV FROM ecf_pre_venda_cabecalho WHERE ecf_pre_venda_cabecalho.ID = ecf_pre_venda_detalhe.ID_ECF_PRE_VENDA_CABECALHO) AS ULTIMA_VENDA FROM ecf_pre_venda_detalhe WHERE ecf_pre_venda_detalhe.ID_PRODUTO =pvd.ID_PRODUTO ORDER BY ID DESC LIMIT 0 , 1 ) as ULTIMA_VENDA, "
	        			+ "(SELECT VALOR_CUSTO FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR_CUSTO "
		        		+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc "
		        		+ "WHERE EXISTS(SELECT NOME FROM produto p WHERE p.ID=pvd.ID_PRODUTO AND p.GRUPO_ID=:grupo AND p.TIPO_ITEM = 3) AND "
		        		+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM produto WHERE ID = pvd.ID_PRODUTO) "
		        		+ "AND pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO = 'PEDIDO' AND pvc.TIPO_VENDA = 'PRODUTO' "
		        		+ "AND pvc.SITUACAO = 'F' AND "
		        		+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
		        		+ "AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY "+sort+" "+order);
		        
		        
		        
        	}else{
        		q = em.createNativeQuery("SELECT p.ID as ID_PRODUTO, p.NOME as PRODUTO, p.VALOR_VENDA as VALOR, p.QTD_ESTOQUE, "
        				+ "(SELECT NOME_GRUPO FROM  grupo_produto as gp WHERE gp.ID=p.GRUPO_ID) as GRUPO, "
        				+ "(SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=p.ID_UNIDADE_PRODUTO) as UNIDADE, "
        				+ "(SELECT (SELECT DATA_HORA FROM movimento_ent_cabecalho WHERE movimento_ent_detalhe.MOVIMENTO_ENT_CABECALHO_ID = movimento_ent_cabecalho.ID) AS ULTIMA_VENDA FROM movimento_ent_detalhe WHERE `PRODUTO_ID` =p.ID ORDER BY ID DESC LIMIT 0 , 1) as ULTIMA_COMPRA, "
        				+ "p.VALOR_CUSTO "
        				+ "FROM produto as p "
        				+ "WHERE "
        				+ "p.STATUS = 'ATIVO' AND "
        				+ "p.QTD_ESTOQUE > 0 AND "
        				+ "p.TIPO_ITEM = 3 AND "
        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' AND ec.TIPO_VENDA = 'PRODUTO' "
        				+ "AND ec.TIPO_VENDA = 'PRODUTO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa))) "
        				+ "AND p.EMPRESA_ID = :codEmpresa AND p.GRUPO_ID=:grupo ORDER by "+sort+" "+order);
        	}
			
			q.setParameter("grupo", GrupoProdutoDAO.findbyName(grupo));
			
			}else{
				if(!marca.equals("TODOS AS MARCAS")){
					if(saleOnly){
				        q = em.createNativeQuery("SELECT (SELECT ID FROM produto WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
			        			+ "(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
			        			+ "(SELECT VALOR_VENDA FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR, "
			        			+ "SUM(`QUANTIDADE`) as QTD, (SELECT (SELECT NOME_GRUPO FROM  grupo_produto WHERE grupo_produto.ID=produto.GRUPO_ID) FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as GRUPO, "
			        			+ "(SELECT (SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=produto.ID_UNIDADE_PRODUTO) AS UNIDADE FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as UNIDADE, "
			        			+ "(SELECT QTD_ESTOQUE FROM produto WHERE ID=pvd.ID_PRODUTO) as QTD_ESTOQUE, "
			        			+ "(SELECT (SELECT DATA_PV FROM ecf_pre_venda_cabecalho WHERE ecf_pre_venda_cabecalho.ID = ecf_pre_venda_detalhe.ID_ECF_PRE_VENDA_CABECALHO) AS ULTIMA_VENDA FROM ecf_pre_venda_detalhe WHERE ecf_pre_venda_detalhe.ID_PRODUTO =pvd.ID_PRODUTO ORDER BY ID DESC LIMIT 0 , 1 ) as ULTIMA_VENDA, "
			        			+ "(SELECT VALOR_CUSTO FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR_CUSTO "
				        		+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc "
				        		+ "WHERE EXISTS(SELECT NOME FROM produto p WHERE p.ID=pvd.ID_PRODUTO AND p.MARCAS_ID=:marca  AND p.TIPO_ITEM = 3) AND "
				        		+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM produto WHERE ID = pvd.ID_PRODUTO) "
				        		+ "AND pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO = 'PEDIDO' AND pvc.TIPO_VENDA = 'PRODUTO' "
				        		+ "AND pvc.SITUACAO = 'F' AND "
				        		+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
				        		+ "AND pvc.EMPRESA_ID = :codEmpresa GROUP BY ID_PRODUTO ORDER BY "+sort+" "+order);
				        
				        
				        
		        	}else{
		        		q = em.createNativeQuery("SELECT p.ID as ID_PRODUTO, p.NOME as PRODUTO, p.VALOR_VENDA as VALOR, p.QTD_ESTOQUE, "
		        				+ "(SELECT NOME_GRUPO FROM  grupo_produto as gp WHERE gp.ID=p.GRUPO_ID) as GRUPO, "
		        				+ "(SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=p.ID_UNIDADE_PRODUTO) as UNIDADE, "
		        				+ "(SELECT (SELECT DATA_HORA FROM movimento_ent_cabecalho WHERE movimento_ent_detalhe.MOVIMENTO_ENT_CABECALHO_ID = movimento_ent_cabecalho.ID) AS ULTIMA_VENDA FROM movimento_ent_detalhe WHERE `PRODUTO_ID` =p.ID ORDER BY ID DESC LIMIT 0 , 1) as ULTIMA_COMPRA, "
		        				+ "p.VALOR_CUSTO "
		        				+ "FROM produto as p "
		        				+ "WHERE "
		        				+ "p.STATUS = 'ATIVO' AND "
		        				+ "p.QTD_ESTOQUE > 0 AND "
		        				+ "p.TIPO_ITEM = 3 AND "
		        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
		        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' AND ec.TIPO_VENDA = 'PRODUTO' "
		        				+ "AND ec.TIPO_VENDA = 'PRODUTO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa))) "
		        				+ "AND p.EMPRESA_ID = :codEmpresa AND p.MARCAS_ID=:marca ORDER by "+sort+" "+order);
		        	}
					
					q.setParameter("marca", MarcaDAO.findbyName(marca));
				}else{
					if(saleOnly){
						q = em.createNativeQuery("SELECT (SELECT ID FROM produto WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
			        			+ "(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
			        			+ "(SELECT VALOR_VENDA FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR, "
			        			+ "SUM(`QUANTIDADE`) as QTD, (SELECT (SELECT NOME_GRUPO FROM  grupo_produto WHERE grupo_produto.ID=produto.GRUPO_ID) as GRUPO FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as GRUPO, "
			        			+ "(SELECT (SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=produto.ID_UNIDADE_PRODUTO) AS UNIDADE FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as UNIDADE, "
			        			+ "(SELECT QTD_ESTOQUE FROM produto WHERE ID=pvd.ID_PRODUTO) as QTD_ESTOQUE, "
			        			+ "(SELECT (SELECT DATA_PV FROM ecf_pre_venda_cabecalho WHERE ecf_pre_venda_cabecalho.ID = ecf_pre_venda_detalhe.ID_ECF_PRE_VENDA_CABECALHO) AS ULTIMA_VENDA FROM ecf_pre_venda_detalhe WHERE ecf_pre_venda_detalhe.ID_PRODUTO =pvd.ID_PRODUTO ORDER BY ID DESC LIMIT 0 , 1 ) as ULTIMA_VENDA, "
			        			+ "(SELECT VALOR_CUSTO FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR_CUSTO "
			        			+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE "
			        			+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM produto p WHERE p.ID = pvd.ID_PRODUTO AND  p.TIPO_ITEM = 3) AND "
			        			+ "pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO_VENDA = 'PRODUTO' AND pvc.TIPO = 'PEDIDO' AND "
			        			+ "pvc.SITUACAO = 'F' AND pvc.EMPRESA_ID = :codEmpresa AND "
			        			+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
			        			+ "GROUP BY ID_PRODUTO ORDER BY "+sort+" "+order);
			        	 
			        	}else{
			        		q = em.createNativeQuery("SELECT p.ID as ID_PRODUTO, p.NOME as PRODUTO, p.VALOR_VENDA as VALOR, p.QTD_ESTOQUE, "
			        				+ "(SELECT NOME_GRUPO FROM  grupo_produto as gp WHERE gp.ID=p.GRUPO_ID) as GRUPO,"
			        				+ "(SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=p.ID_UNIDADE_PRODUTO) as UNIDADE,"
			        				+ "(SELECT (SELECT DATA_HORA FROM movimento_ent_cabecalho WHERE movimento_ent_detalhe.MOVIMENTO_ENT_CABECALHO_ID = movimento_ent_cabecalho.ID) AS ULTIMA_VENDA FROM movimento_ent_detalhe WHERE `PRODUTO_ID` =p.ID ORDER BY ID DESC LIMIT 0 , 1) as ULTIMA_COMPRA, "
			        				+ "p.VALOR_CUSTO "
			        				+ "FROM produto as p "
			        				+ "WHERE "
			        				+ "p.STATUS = 'ATIVO' AND "
			        				+ "p.QTD_ESTOQUE > 0 AND "        	
			        				+ "p.TIPO_ITEM = 3 AND "
			        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
			        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' AND ec.TIPO_VENDA = 'PRODUTO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa))) "
			        				+ "AND p.EMPRESA_ID = :codEmpresa  ORDER by "+sort+" "+order);
			        	}
					q.setParameter("codEmpresa", OpusERP4UI.getEmpresa().getId());
				}
			}
			
		}else{
			if(saleOnly){
				q = em.createNativeQuery("SELECT (SELECT ID FROM produto WHERE ID=pvd.ID_PRODUTO) as ID_PRODUTO, "
	        			+ "(SELECT NOME FROM produto WHERE ID=pvd.ID_PRODUTO) as PRODUTO,"
	        			+ "(SELECT VALOR_VENDA FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR, "
	        			+ "SUM(`QUANTIDADE`) as QTD, (SELECT (SELECT NOME_GRUPO FROM  grupo_produto WHERE grupo_produto.ID=produto.GRUPO_ID) as GRUPO FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as GRUPO, "
	        			+ "(SELECT (SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=produto.ID_UNIDADE_PRODUTO) AS UNIDADE FROM produto WHERE produto.ID=pvd.ID_PRODUTO) as UNIDADE, "
	        			+ "(SELECT QTD_ESTOQUE FROM produto WHERE ID=pvd.ID_PRODUTO) as QTD_ESTOQUE, "
	        			+ "(SELECT (SELECT DATA_PV FROM ecf_pre_venda_cabecalho WHERE ecf_pre_venda_cabecalho.ID = ecf_pre_venda_detalhe.ID_ECF_PRE_VENDA_CABECALHO) AS ULTIMA_VENDA FROM ecf_pre_venda_detalhe WHERE ecf_pre_venda_detalhe.ID_PRODUTO =pvd.ID_PRODUTO ORDER BY ID DESC LIMIT 0 , 1 ) as ULTIMA_VENDA, "
	        			+ "(SELECT VALOR_CUSTO FROM produto WHERE ID=pvd.ID_PRODUTO) as VALOR_CUSTO "
	        			+ "FROM ecf_pre_venda_detalhe as pvd, ecf_pre_venda_cabecalho as pvc WHERE "
	        			+ "pvd.ID_ECF_PRE_VENDA_CABECALHO = pvc.ID AND EXISTS(SELECT * FROM produto p WHERE p.ID = pvd.ID_PRODUTO AND p.TIPO_ITEM = 3 ) AND "
	        			+ "pvc.DATA_PV >= :data1 AND pvc.DATA_PV <= :data2 AND pvc.TIPO_VENDA = 'PRODUTO' AND pvc.TIPO = 'PEDIDO' AND "
	        			+ "pvc.SITUACAO = 'F' AND pvc.EMPRESA_ID = :codEmpresa AND "
	        			+ "pvc.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa) "
	        			+ "GROUP BY ID_PRODUTO ORDER BY "+sort+" "+order);
	        	 
	        	}else{
	        		q = em.createNativeQuery("SELECT p.ID as ID_PRODUTO, p.NOME as PRODUTO, p.VALOR_VENDA as VALOR, p.QTD_ESTOQUE, "
	        				+ "(SELECT NOME_GRUPO FROM  grupo_produto as gp WHERE gp.ID=p.GRUPO_ID) as GRUPO,"
	        				+ "(SELECT NOME FROM  unidade_produto WHERE unidade_produto.ID=p.ID_UNIDADE_PRODUTO) as UNIDADE,"
	        				+ "(SELECT (SELECT DATA_HORA FROM movimento_ent_cabecalho WHERE movimento_ent_detalhe.MOVIMENTO_ENT_CABECALHO_ID = movimento_ent_cabecalho.ID) AS ULTIMA_VENDA FROM movimento_ent_detalhe WHERE `PRODUTO_ID` =p.ID ORDER BY ID DESC LIMIT 0 , 1) as ULTIMA_COMPRA, "
	        				+ "p.VALOR_CUSTO "
	        				+ "FROM produto as p "
	        				+ "WHERE "
	        				+ "p.STATUS = 'ATIVO' AND "
	        				+ "p.QTD_ESTOQUE > 0 AND "        	
	        				+ "p.TIPO_ITEM = 3 AND "
	        				+ "NOT EXISTS(SELECT * FROM ecf_pre_venda_detalhe as e WHERE e.ID_PRODUTO=p.ID AND "
	        				+ "EXISTS(SELECT * FROM ecf_pre_venda_cabecalho ec WHERE ec.ID = e.ID_ECF_PRE_VENDA_CABECALHO AND ec.TIPO='PEDIDO' AND ec.TIPO_VENDA = 'PRODUTO' AND ec.SITUACAO = 'F' AND ec.NATUREZA_OPERACAO 	= (SELECT ID FROM natureza_operacao WHERE DESCRICAO ='VENDA' AND EMPRESA_ID =:codEmpresa))) "
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

				tb =  new PdfPTable(new float[] {0.3f,0.3f,2f,0.8f,0.3f,0.3f,0.3f,0.3f,0.3f,0.3f});
				tb.setWidthPercentage(100f);
				
				tbTopo = new PdfPTable(new float[] {0.3f,0.3f,2f,0.8f,0.3f,0.3f,0.3f,0.3f,0.3f,0.3f});
				tbTopo.setWidthPercentage(100f);
				
				tbTopo.addCell(getCellTopo("Ranking"));
				tbTopo.addCell(getCellTopo("Código"));
				tbTopo.addCell(getCellTopo("Produto"));
				tbTopo.addCell(getCellTopo("Grupo"));
				tbTopo.addCell(getCellTopo("Valor"));
				tbTopo.addCell(getCellTopo("Valor Custo"));
				tbTopo.addCell(getCellTopo("Vendidos"));
				tbTopo.addCell(getCellTopo("Saldo"));
				tbTopo.addCell(getCellTopo("Unidade"));
				tbTopo.addCell(getCellTopo("Ultima Venda"));			
			}else{
				tb =  new PdfPTable(new float[] {0.3f,0.3f,2f,0.8f,0.3f,0.3f,0.3f,0.3f,0.3f});
				tb.setWidthPercentage(100f);
				
				tbTopo = new PdfPTable(new float[] {0.3f,0.3f,2f,0.8f,0.3f,0.3f,0.3f,0.3f,0.3f});
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
				tbTopo.addCell(getCellTopo("Valor Custo"));	
				tbTopo.addCell(getCellTopo("Saldo"));
				tbTopo.addCell(getCellTopo("Unidade"));				
				tbTopo.addCell(getCellTopo("Ultima Compra"));			
			}
			
			Integer i=1;
			 for (Object ob: q.getResultList()) {
				 
				 if(saleOnly){
					 tb.addCell(getCell(String.valueOf(i),Element.ALIGN_CENTER));
			        	i++;
			        	
					 tb.addCell(getCell(((Object[])ob)[0].toString().toUpperCase(),0));				 
					 tb.addCell(getCell(((Object[])ob)[1].toString().toUpperCase(),0));
					 tb.addCell(getCell(((Object[])ob)[4].toString().toUpperCase(),0));
					 
					 String valor = "R$ "+Real.formatDbToString(((Object[])ob)[2].toString());
					 tb.addCell(getCell(valor,Element.ALIGN_RIGHT));
					 
					 //tb.addCell(getCell(((Object[])ob)[3].toString(),0));
					 
					 //Valor Custo					 
					 tb.addCell(getCell(((Object[])ob)[8].toString(),Element.ALIGN_CENTER));
					
					 tb.addCell(getCell(String.valueOf(Float.valueOf(((Object[])ob)[3].toString()).intValue()),Element.ALIGN_CENTER));
					 
					 
					 tb.addCell(getCell(String.valueOf(Float.valueOf(((Object[])ob)[6].toString()).intValue()),Element.ALIGN_CENTER));
					 tb.addCell(getCell(((Object[])ob)[5].toString().toUpperCase(),0));

		        	SimpleDateFormat sdf2 =  new SimpleDateFormat("dd/MM/yyyy");
		        	tb.addCell(getCell(sdf2.format(((Object[])ob)[7]),0));
		        	
				 }else{
					 tb.addCell(getCell(String.valueOf(i),Element.ALIGN_CENTER));
			        	i++;	
					 
					String valor = "R$ "+Real.formatDbToString(((Object[])ob)[2].toString());
	        		
	        		tb.addCell(getCell(((Object[])ob)[0].toString().toUpperCase(),0));
	        		tb.addCell(getCell(((Object[])ob)[1].toString().toUpperCase(),0));
	        		tb.addCell(getCell(((Object[])ob)[4].toString().toUpperCase(),0));
	        		tb.addCell(getCell(valor,Element.ALIGN_RIGHT));
	        		
	        		//Valor Custo
	        		tb.addCell(getCell(((Object[])ob)[7].toString(),Element.ALIGN_CENTER));
	        		
	        		tb.addCell(getCell(String.valueOf(Float.valueOf(((Object[])ob)[3].toString()).intValue()), Element.ALIGN_CENTER));
	        		
					 
	        		tb.addCell(getCell(((Object[])ob)[5].toString().toUpperCase(),0));
	        		

	        		Query qUltimaCompra = em.createNativeQuery("SELECT (SELECT DATA_HORA FROM movimento_ent_cabecalho WHERE movimento_ent_detalhe.MOVIMENTO_ENT_CABECALHO_ID = movimento_ent_cabecalho.ID) AS ULTIMA_VENDA FROM movimento_ent_detalhe WHERE `PRODUTO_ID` =:codProduto ORDER BY ID DESC LIMIT 0 , 1");
	        		qUltimaCompra.setParameter("codProduto", ((Object[])ob)[0]);
	        		
	        		
	        		if(((Object[])ob)[6] != null){	        		
				    		SimpleDateFormat sdf2 =  new SimpleDateFormat("dd/MM/yyyy");		        		
			        		tb.addCell(getCell(sdf2.format(((Object[])ob)[6]),0));	        		
		        	}else{
		        		tb.addCell(getCell("",0));
		        	}
	        		
	        		
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
		case "Valor Custo":
			return "VALOR CUSTO";
			
		case "Vendido":
			return "QTD";
			
		case "Saldo":
			return "QTD_ESTOQUE";
			
		case "Unidade":
			return "UNIDADE";
		
		case "Última Venda":
			return "ULTIMA_VENDA";
			
		case "Última Compra":
			return "ULTIMA_COMPRA";
			
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

