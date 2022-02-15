package com.digital.opuserp.view.modulos.cadastro.fornecedores;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
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


public class HistoricoCompras implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	public HistoricoCompras(Integer codFornecedor) throws Exception{
		
		EntityManager em = ConnUtil.getEntity();

		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);	
		
		try{
			PdfWriter.getInstance(doc, baos);
			doc.open();
			
			int result = 0;
			float quantidade = new Float(0); 
			String valor = "";
			String valorUni = "";
			float paddingTop=0;
			float paddingBottom=5;
			//Cabecalho			
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			Fornecedor fornecedor = em.find(Fornecedor.class, codFornecedor);
		
			//Estilos de Fonts
			Font fCampo = new Font(FontFamily.HELVETICA, 5);
			Font fCampoBold = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font fTituloBold  = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font fTitulo  = new Font(FontFamily.HELVETICA, 10);
			Font fSubTitulo  = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
			Font fCab = new Font(FontFamily.HELVETICA, 8);
			
			//Cabeçario
			DataUtil dtUtil = new DataUtil();
			String 	date = dtUtil.getDataExtenso(new Date());
			
			StringBuilder SbCabecalho = new StringBuilder();
			SbCabecalho.append("OPUS ERP4"+"\n"+"EMPRESA:"+"\n"+"EMISSÃO:"+"\n"+"OPERADOR:");
			
			StringBuilder SbCabecalhoVl = new StringBuilder();
			SbCabecalhoVl.append("\n"+empresa.getNome_fantasia()+"\n"+date+"\n"+OpusERP4UI.getUsuarioLogadoUI().getUsername());
			
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
								
			if(fornecedor != null){
					
				Paragraph pPedido = new Paragraph("HISTÓRICO DE COMPRAS", fTituloBold);
				pPedido.setAlignment(Element.ALIGN_CENTER);
				pPedido.setSpacingAfter(10);		
				doc.add(pPedido);				
				
				//TIPO		
				StringBuilder SbForm= new StringBuilder();
				StringBuilder SbVl= new StringBuilder();
				
				SbForm.append("FORNECEDOR:"+"\n"+"\n"+"ORDENAÇÃO:");			
				SbVl.append(fornecedor.getId()+" - "+fornecedor.getRazao_social()+"\n"+"\n"+"CÓDIGO");
	
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
				
				doc.add(tbform);

				FormasPgto formaPgto = new FormasPgto(); 
				
				

				
					List<MovimentoEntCabecalho> movimentoCab = null;
					 Produto produto = null;
					
					Query cab = em.createQuery("select c from MovimentoEntCabecalho c where c.empresa_id =:empresa_id and c.situacao='F' and c.fornecedor=:fornecedor order by c.id asc",MovimentoEntCabecalho.class);
					cab.setParameter("empresa_id", OpusERP4UI.getEmpresa().getId());	
					cab.setParameter("fornecedor", new Fornecedor(fornecedor.getId()));
					
					if(cab.getResultList().size() > 0){
						movimentoCab = cab.getResultList();
						result = cab.getResultList().size();
					}
					
					if(movimentoCab!=null){
						for(MovimentoEntCabecalho movCab: movimentoCab){
							
							if(movCab.getTipo()== null || !movCab.getTipo().equals("CORRECAO")){	
							Query q = em.createQuery("select c from MovimentoEntDetalhe c where c.movimentoEntCabecalhoId =:movCab order by c.id asc",MovimentoEntDetalhe.class);
							q.setParameter("movCab",movCab.getId());	
							List<MovimentoEntDetalhe> movimentoDetalhe = null;
							
							if(q.getResultList().size() > 0){
								movimentoDetalhe = q.getResultList();
							}
							
							
							
							Phrase pNf = new Phrase("NF", fCampoBold);		
							Phrase pData = new Phrase("DATA ENTRADA", fCampoBold);	
							Phrase pVlTotal = new Phrase("VALOR TOTAL", fCampoBold);	
							Phrase pOperador = new Phrase("OPERADOR", fCampoBold);			
							
							PdfPCell pCellpNf = new PdfPCell();
							pCellpNf.addElement(pNf	);
							pCellpNf.setBackgroundColor(new BaseColor(114, 131, 151));			
							pCellpNf.setBorderWidth(1.5f);	
							pCellpNf.setBorderColor(new BaseColor(255, 255, 255));
							pCellpNf.setPaddingBottom(paddingBottom);
							pCellpNf.setPaddingTop(paddingTop);
											
							PdfPCell pCellpData = new PdfPCell();
							pCellpData.addElement(pData);
							pCellpData.setBackgroundColor(new BaseColor(114, 131, 151));
							pCellpData.setBorderColor(new BaseColor(255, 255, 255));	
							pCellpData.setBorderWidth(1.5f);
							pCellpData.setPaddingBottom(paddingBottom);
							pCellpData.setPaddingTop(paddingTop);
							
							PdfPCell pCellpVlTotal = new PdfPCell();
							pCellpVlTotal.addElement(pVlTotal);
							pCellpVlTotal.setBackgroundColor(new BaseColor(114, 131, 151));
							pCellpVlTotal.setBorderColor(new BaseColor(255, 255, 255));	
							pCellpVlTotal.setBorderWidth(1.5f);
							pCellpVlTotal.setPaddingBottom(paddingBottom);
							pCellpVlTotal.setPaddingTop(paddingTop);
							
							PdfPCell pCellpOperador = new PdfPCell();
							pCellpOperador.addElement(pOperador);
							pCellpOperador.setBackgroundColor(new BaseColor(114, 131, 151));
							pCellpOperador.setBorderColor(new BaseColor(255, 255, 255));	
							pCellpOperador.setBorderWidth(1.5f);
							pCellpOperador.setPaddingBottom(paddingBottom);
							pCellpOperador.setPaddingTop(paddingTop);

							PdfPTable tb1 = new PdfPTable(new float[] {0.10f,0.80f,0.10f,0.20f});
							tb1.setWidthPercentage(100f);
							tb1.setSpacingBefore(5);
							
							tb1.addCell(pCellpNf);
							tb1.addCell(pCellpData);
							tb1.addCell(pCellpVlTotal);
							tb1.addCell(pCellpOperador);
							doc.add(tb1);
							
							
							Paragraph pNfVl = new Paragraph("", fCampoBold);
							if(movCab.getCodNf()!=null && !movCab.getCodNf().equals("")){
								pNfVl = new Paragraph(String.valueOf(movCab.getCodNf()), fCampoBold);
							}
							
							Paragraph pDataVl = new Paragraph("", fCampoBold);
							if(movCab.getDataHora()!=null && !movCab.getDataHora().equals("")){
								pDataVl = new Paragraph(dtUtil.parseDataHoraBra(movCab.getDataHora().toString()),fCampoBold);
							}
							
							Paragraph pVlTotalVl = new Paragraph("", fCampoBold);
							if(movCab.getValorTotal()!=null && !movCab.getValorTotal().equals("")){
								String valorTotal = "R$ "+Real.formatDbToString(String.valueOf(movCab.getValorTotal()));
								pVlTotalVl = new Paragraph(valorTotal,fCampoBold);
								pVlTotalVl.setAlignment(Element.ALIGN_RIGHT);
							}
							
							Paragraph pOperadorVl = new Paragraph("", fCampoBold);
							if(movCab.getUsuario()!=null && !movCab.getUsuario().equals("")){
								pOperadorVl = new Paragraph(movCab.getUsuario(),fCampoBold);
							}
							
							
							PdfPCell pCellpDataVlt = new PdfPCell();
							pCellpDataVlt.addElement(pDataVl);
							pCellpDataVlt.setBackgroundColor(new BaseColor(184, 191, 198));
							pCellpDataVlt.setBorderColor(new BaseColor(255, 255, 255));		
							pCellpDataVlt.setBorderWidth(1.5f);
							pCellpDataVlt.setPaddingBottom(paddingBottom);
							pCellpDataVlt.setPaddingTop(paddingTop);
							
							PdfPCell pCellpOperadorVl = new PdfPCell();
							pCellpOperadorVl.addElement(pOperadorVl);
							pCellpOperadorVl.setBackgroundColor(new BaseColor(184, 191, 198));
							pCellpOperadorVl.setBorderColor(new BaseColor(255, 255, 255));		
							pCellpOperadorVl.setBorderWidth(1.5f);
							pCellpOperadorVl.setPaddingBottom(paddingBottom);
							pCellpOperadorVl.setPaddingTop(paddingTop);
							
							PdfPCell pCellpVlTotalVl = new PdfPCell();
							pCellpVlTotalVl.addElement(pVlTotalVl);
							pCellpVlTotalVl.setBackgroundColor(new BaseColor(184, 191, 198));
							pCellpVlTotalVl.setBorderColor(new BaseColor(255, 255, 255));		
							pCellpVlTotalVl.setBorderWidth(1.5f);
							pCellpVlTotalVl.setPaddingBottom(paddingBottom);
							pCellpVlTotalVl.setPaddingTop(paddingTop);
							
							PdfPCell pCellpNfVl = new PdfPCell();
							pCellpNfVl.addElement(pNfVl);
							pCellpNfVl.setBackgroundColor(new BaseColor(184, 191, 198));	
							pCellpNfVl.setBorderColor(new BaseColor(255, 255, 255));	
							pCellpNfVl.setBorderWidth(1.5f);
							pCellpNfVl.setPaddingBottom(paddingBottom);
							pCellpNfVl.setPaddingTop(paddingTop);
							
							PdfPTable tb2 = new PdfPTable(new float[] {0.10f,0.80f,0.10f,0.20f});
							tb2.setWidthPercentage(100f);		
							
							tb2.addCell(pCellpNfVl);
							tb2.addCell(pCellpDataVlt);
							tb2.addCell(pCellpVlTotalVl);
							tb2.addCell(pCellpOperadorVl);
							doc.add(tb2);	

							if(movimentoDetalhe != null){						
								for(MovimentoEntDetalhe movdetalhe: movimentoDetalhe){
									
									produto = em.find(Produto.class, movdetalhe.getProdutoId());
									quantidade = movdetalhe.getQuantidade();
									valorUni = "R$ "+Real.formatDbToString(String.valueOf(movdetalhe.getValorCusto()));
									double vlTotal = movdetalhe.getValorCusto() * movdetalhe.getQuantidade();
									valor = "R$ "+Real.formatDbToString(String.valueOf(vlTotal));								
									
									Paragraph pCodProdutoVl = new Paragraph("", fCampo);
									if(produto !=null){
										pCodProdutoVl = new Paragraph(String.valueOf(produto.getId()), fCampo);
									}
									
									Paragraph pProdutoVl = new Paragraph("", fCampo);
									if(produto !=null){
										pProdutoVl = new Paragraph(produto.getNome(), fCampo);
									}
									
									Paragraph pUnVl = new Paragraph("", fCampo);	
									if(produto !=null && produto.getUnidadeProduto().getNome()!=null && !produto.getUnidadeProduto().getNome().equals("")){
										pUnVl = new Paragraph(produto.getUnidadeProduto().getNome(), fCampo);
										pUnVl.setAlignment(Element.ALIGN_CENTER);
									}
									
									Paragraph pQtdVl = new Paragraph("", fCampo);	
	//								if(quantidade!=null && !movCab.getQtd().equals("")){
										pQtdVl = new Paragraph(Real.formatDbToString(String.valueOf(quantidade)), fCampo);
										pQtdVl.setAlignment(Element.ALIGN_CENTER);
	//								}
									
									Paragraph pProdutoVlUNi = new Paragraph("", fCampo);
									pProdutoVlUNi = new Paragraph(valorUni, fCampo);
									pProdutoVlUNi.setAlignment(Element.ALIGN_RIGHT);
										
										
									Paragraph pVAlorVl = new Paragraph(valor, fCampo);
									pVAlorVl.setAlignment(Element.ALIGN_RIGHT);
									
									PdfPCell pCellpCodProdutoVl = new PdfPCell();
									pCellpCodProdutoVl.addElement(pCodProdutoVl);
									pCellpCodProdutoVl.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellpCodProdutoVl.setBorderColor(new BaseColor(255, 255, 255));		
									pCellpCodProdutoVl.setBorderWidth(1.5f);
									pCellpCodProdutoVl.setPaddingBottom(paddingBottom);
									pCellpCodProdutoVl.setPaddingTop(paddingTop);
									
									
									PdfPCell pCellpProdutoVl = new PdfPCell();
									pCellpProdutoVl.addElement(pProdutoVl);
									pCellpProdutoVl.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellpProdutoVl.setBorderColor(new BaseColor(255, 255, 255));		
									pCellpProdutoVl.setBorderWidth(1.5f);
									pCellpProdutoVl.setPaddingBottom(paddingBottom);
									pCellpProdutoVl.setPaddingTop(paddingTop);
									
									
									PdfPCell pCellpUnVl = new PdfPCell();
									pCellpUnVl.addElement(pUnVl);
									pCellpUnVl.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellpUnVl.setBorderColor(new BaseColor(255, 255, 255));		
									pCellpUnVl.setBorderWidth(1.5f);
									pCellpUnVl.setPaddingBottom(paddingBottom);
									pCellpUnVl.setPaddingTop(paddingTop);
									
									
									PdfPCell pCellpQtdVl = new PdfPCell();
									pCellpQtdVl.addElement(pQtdVl);
									pCellpQtdVl.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellpQtdVl.setBorderColor(new BaseColor(255, 255, 255));		
									pCellpQtdVl.setBorderWidth(1.5f);
									pCellpQtdVl.setPaddingBottom(paddingBottom);
									pCellpQtdVl.setPaddingTop(paddingTop);
									
									PdfPCell pCellpProdutoVlUNi = new PdfPCell();
									pCellpProdutoVlUNi.addElement(pProdutoVlUNi);
									pCellpProdutoVlUNi.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellpProdutoVlUNi.setBorderColor(new BaseColor(255, 255, 255));		
									pCellpProdutoVlUNi.setBorderWidth(1.5f);
									pCellpProdutoVlUNi.setPaddingBottom(paddingBottom);
									pCellpProdutoVlUNi.setPaddingTop(paddingTop);
								
									PdfPCell pCellpVAlorVl = new PdfPCell();
									pCellpVAlorVl.addElement(pVAlorVl);
									pCellpVAlorVl.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellpVAlorVl.setBorderColor(new BaseColor(255, 255, 255));		
									pCellpVAlorVl.setBorderWidth(1.5f);
									pCellpVAlorVl.setPaddingBottom(paddingBottom);
									pCellpVAlorVl.setPaddingTop(paddingTop);
									
									PdfPTable tb3 = new PdfPTable(new float[] {0.10f,0.80f,0.10f,0.10f,0.15f,0.15f});
									tb3.setWidthPercentage(97f);		
									
									tb3.addCell(pCellpCodProdutoVl);
									tb3.addCell(pCellpProdutoVl);
									tb3.addCell(pCellpQtdVl);
									tb3.addCell(pCellpUnVl);
									tb3.addCell(pCellpProdutoVlUNi);
									tb3.addCell(pCellpVAlorVl);
									doc.add(tb3);	
			
								}	
							}
							}	

						}									
					}			
				 }
	
			Paragraph pRegistro = new Paragraph("   "+String.valueOf(result)+" Registros encontrados",new Font(FontFamily.HELVETICA, 4));
			doc.add(pRegistro);
		
		}finally{
			if(doc.isOpen() && doc != null){
				doc.close();
			}
		}
		
	}

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
		
	}
}
