package com.digital.opuserp.view.modulos.relatorio.Compras;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CompraDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
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

public class ExportarRelatorioCompra implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportarRelatorioCompra(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista, List<Object> columns)throws Exception {

		EntityManager em = ConnUtil.getEntity();
		
		
		Document doc;
		if(orientacao.equals("RETRATO")){
			doc = new Document(PageSize.A4, 24, 24, 24, 24);
		}else{
			doc = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
		}
		

		try {

			// -----------BUSCA

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MovimentoEntCabecalho> criteriaQuery = cb.createQuery(MovimentoEntCabecalho.class);
			Root<MovimentoEntCabecalho> rootCliente = criteriaQuery.from(MovimentoEntCabecalho.class);
			EntityType<MovimentoEntCabecalho> type = em.getMetamodel().entity(MovimentoEntCabecalho.class);

			List<Predicate> criteria = new ArrayList<Predicate>();
			List<MovimentoEntDetalhe> resultDetalhe = null;

			criteria.add(cb.equal(rootCliente.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));
			criteria.add(cb.equal(rootCliente.get("tipo"), "COMPRA"));

			if (lista.size() > 0) {
				for (SearchParameters s : lista) {
					
					if (s.getOperador().equals("IGUAL")) {
						if (s.getCampo().equals("fornecedor.razao_social")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("fornecedor").<String>get("razao_social")), s.getValor().toLowerCase()));
						}					
						
						if (!s.getCampo().equals("fornecedor.razao_social")) {
	
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.equal(rootCliente.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(String.class)){						
									criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
								}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.equal(rootCliente.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
								criteria.add(cb.equal(rootCliente.<Float>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
						}

					}else if (s.getOperador().equals("DIFERENTE")) {
						if (s.getCampo().equals("fornecedor.razao_social")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("fornecedor").<String>get("razao_social")), s.getValor().toLowerCase()));
						}					
						
						if (!s.getCampo().equals("fornecedor.razao_social")) {								
							
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.notEqual(rootCliente.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(String.class)){
									criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())), s.getValor().toLowerCase()));
								
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.notEqual(rootCliente.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
								criteria.add(cb.notEqual(rootCliente.<Float>get(s.getCampo()), s.getValor().toLowerCase()));
							}
						}	
						

					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("fornecedor.razao_social")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("fornecedor").<String>get("razao_social")), "%" + s.getValor().toLowerCase()+ "%"));
						}else{
							criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), "%" +s.getValor().toLowerCase()+ "%"));
						}
					
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						

						if (s.getCampo().equals("fornecedor.razao_social")){
							criteria.add(cb.notLike(cb.lower(rootCliente.get("fornecedor").<String>get("razao_social")), "%" + s.getValor().toLowerCase()+ "%"));
						}else{
							criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
						
					} else if (s.getOperador().equals("MAIOR QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThan(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.greaterThan(rootCliente.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){								
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.greaterThan(rootCliente.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
								
							}catch(Exception e)
							{
								e.printStackTrace();		
							}
					} else if (s.getOperador().equals("MENOR QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.lessThan(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.lessThan(rootCliente.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.lessThan(rootCliente.<Date> get(s.getCampo()),  sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							
						
					} else if (s.getOperador().equals("MAIOR IGUAL QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
		
					} else if (s.getOperador().equals("MENOR IGUAL QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.lessThanOrEqualTo(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.lessThanOrEqualTo(rootCliente.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.lessThanOrEqualTo(rootCliente.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
						}
				}
			}

			if (criteria.size() == 0) {
				throw new RuntimeException("no criteria");
			} else if (criteria.size() == 1) {
				criteriaQuery.where(criteria.get(0));
			} else {
				criteriaQuery.where(cb.and(criteria.toArray(new Predicate[0])));
			}

			if(selectFiltro(order).equals("fornecedor.razao_social")){				
				criteriaQuery.orderBy(cb.asc(rootCliente.join("fornecedor").get("razao_social")));							
			}else{
				criteriaQuery.orderBy(cb.asc(rootCliente.get(selectFiltro(order))));				
			}
			TypedQuery q = em.createQuery(criteriaQuery);		
						
			
			// -----------BUSCA

			PdfWriter writer = PdfWriter.getInstance(doc, baos);
			doc.open();
			
			
			//Estilos de Fonts
			Font fCaptions = new Font(FontFamily.HELVETICA, 6);
			Font fCaptionsBold = new Font(FontFamily.HELVETICA, 6, Font.BOLD);
			Font fCampo = new Font(FontFamily.HELVETICA, 5);
			Font fCampoBold = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font fConteudo = new Font(FontFamily.HELVETICA, 7, Font.BOLD);
			Font fTitulo  = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font fSubTitulo  = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
			Font fCab = new Font(FontFamily.HELVETICA, 8);

			// Cabeçalho
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			DataUtil dtUtil = new DataUtil();
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");	
			String hora;
			hora = " às "+ sdf.format(new Date());

			String 	date = dtUtil.getDataExtenso(new Date());

			StringBuilder SbCabecalho = new StringBuilder();
			SbCabecalho.append("OPUS ERP4"+"\n"+"EMPRESA:"+"\n"+"EMISSÃO:"+"\n"+"OPERADOR:");
			
			StringBuilder SbCabecalhoVl = new StringBuilder();
			SbCabecalhoVl.append("\n"+empresa.getNome_fantasia()+"\n"+date+hora+"\n"+OpusERP4UI.getUsuarioLogadoUI().getUsername());
			
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE COMPRA",fTitulo);
			pExport.setAlignment(Element.ALIGN_CENTER);
			pExport.setSpacingAfter(10);
			doc.add(pExport);
			
			//FiILTROS					
			StringBuilder SbTipo = new StringBuilder();
			StringBuilder SbOperad =  new StringBuilder();
			StringBuilder SbValor = new StringBuilder();
			
			if (lista.size() > 0) {
				for (SearchParameters s : lista) {	
	
					SbTipo.append(selectHeader(s.getCampo())+"\n");
					SbOperad.append(s.getOperador()+"\n");
					SbValor.append(s.getValor()+"\n");
					
				}
			}
			Paragraph pCampo = new Paragraph(SbTipo.toString(),fSubTitulo);
			pCampo.setAlignment(Element.ALIGN_LEFT);
			Paragraph pOperqador = new Paragraph(SbOperad.toString(),fCab);
			pOperqador.setAlignment(Element.ALIGN_LEFT);
			Paragraph pValor= new Paragraph(SbValor.toString(),fSubTitulo);
			pValor.setAlignment(Element.ALIGN_LEFT);

			PdfPCell pCellTipo = new PdfPCell();
			pCellTipo.setBorderWidth(0);
			pCellTipo.addElement(pCampo);	
			
			PdfPCell pCellOperador = new PdfPCell();
			pCellOperador.setBorderWidth(0);
			pCellOperador.addElement(pOperqador);		
			
			PdfPCell pCellValor = new PdfPCell();
			pCellValor.setBorderWidth(0);
			pCellValor.addElement(pValor);		
			
			PdfPTable tbTipo = new PdfPTable(new float[]{0.32f,0.30f,1f});
			tbTipo.setWidthPercentage(100f);	
			tbTipo.addCell(pCellTipo);
			tbTipo.addCell(pCellOperador);
			tbTipo.addCell(pCellValor);
			tbTipo.setSpacingAfter(10);
			
			doc.add(tbTipo);
				
			//TIPO		
			StringBuilder SbForm= new StringBuilder();
			StringBuilder SbVl= new StringBuilder();
			
			
			SbForm.append("TIPO:"+"\n"+"ORIENTAÇÃO:"+"\n"+"ORDENAÇÃO"+"\n");			
			SbVl.append(tipo+"\n"+orientacao+"\n"+selectUpHeader(selectFiltro(order))+"\n");

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

			// Estilos de Fonts
			Font fValores = new Font(FontFamily.COURIER, 6, Font.NORMAL);

			List<MovimentoEntCabecalho> Compras = q.getResultList();	
			
			
			float[] f = new float[columns.size()];
			
			Integer i=0;
			for (Object c : columns) {		
							
			    if(selectHeader(c.toString()).equals("COD.")){
			    	f[i] = (0.10f);	
			    }
			    if(selectHeader(c.toString()).equals("NOTA FISCAL")){
			    	f[i] = (0.20f);	
			    }
			    if(selectHeader(c.toString()).equals("QTD ITENS")){
			    	f[i] = (0.20f);	
			    }
			    if(selectHeader(c.toString()).equals("VALOR TOTAL")){
			    	f[i] = (0.20f);	
			    }
			    if(selectHeader(c.toString()).equals("DATA")){
			    	f[i] = (0.20f);	
			    }
			    if(selectHeader(c.toString()).equals("FORNECEDOR")){
			    	f[i] = (1f);	
			    }
			    if(selectHeader(c.toString()).equals("STATUS")){
			    	f[i] = (0.20f);	
			    }
			    i++;
     		  }				
			
			double dinheiro = 0;			
			double deposito = 0;
			double banco = 0;
			double cheque = 0;			
			double cartCredito = 0;			
			double cartDebito = 0;			
			double totalPago = 0;			
			double totalAserPago = 0;	
			double boleto = 0;
			double nenhuma = 0;
			
			Integer reg= 0;
			Integer cont = 0;
			
			PdfPTable tbConteudo2 = new PdfPTable(f);
			
			
			if(tipo.equals("DETALHAMENTO")){
				
				PdfPTable tbTopo = new PdfPTable(f);
				tbTopo.setWidthPercentage(100f);				
				
				PdfPCell pCellTop = new PdfPCell();
				for (Object c : columns) {
					
					pCellTop = new PdfPCell(new Phrase(selectHeader(c.toString()), fCampoBold));
					pCellTop.setPaddingTop(2);
					pCellTop.setPaddingBottom(4);
					pCellTop.setBackgroundColor(new BaseColor(114, 131, 151));
					pCellTop.setBorderColor(new BaseColor(255, 255, 255));	
					pCellTop.setBorderWidth(1.5f);
					tbTopo.addCell(pCellTop);
				}
				
				//Produto
				PdfPTable tbItens = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
				PdfPCell pCellProduto = new PdfPCell();			
				PdfPCell pCellQtdproduto = new PdfPCell();	
				PdfPCell pCellVlrUnit = new PdfPCell();						
				PdfPCell pCellValorToTal = new PdfPCell();
							
				
				doc.add(tbTopo);	
				Integer qtd = 0;
				for (MovimentoEntCabecalho Compra : Compras) {

					Integer qtdP = 0;
					resultDetalhe = CompraDAO.getItensCompra(Compra.getId());
					if(resultDetalhe.size() > 0){
						qtdP = resultDetalhe.size();
					}
					
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(5f);
					

					PdfPCell pCell3 = new PdfPCell();
					pCell3.setPaddingTop(0);
					pCell3.setPaddingBottom(4);
					pCell3.setBackgroundColor(new BaseColor(232, 235, 237));
					pCell3.setBorderColor(new BaseColor(255, 255, 255));	
					pCell3.setBorderWidth(1.5f);					
					
					Integer dias= 0;
					Paragraph valorColuna2 = null;
					valorColuna2 = new Paragraph(" ",fCampo);							
					pCell3.addElement(valorColuna2);	
										
				
					PdfPTable tbConteudo = new PdfPTable(f);
					tbConteudo.setWidthPercentage(100f);	
					if(qtd != 0){
						tbConteudo.setSpacingBefore(3);						
					}
					qtd++;
					
					
					for (Object c : columns) {
						
						PdfPCell pCellConteudo = new PdfPCell();
						pCellConteudo.setPaddingTop(2);
						pCellConteudo.setPaddingBottom(4);
						pCellConteudo.setBackgroundColor(new BaseColor(184, 191, 198));
						pCellConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						pCellConteudo.setBorderWidth(1.5f);
						
						Paragraph valorColuna3 = null;
						valorColuna3 = new Paragraph(" ",fCampoBold);							

						if(selectHeader(c.toString()).equals("COD.")){
							valorColuna3 = new Paragraph(Compra.getId().toString(),fCampoBold);					
						}else if(selectHeader(c.toString()).equals("NOTA FISCAL")){
							valorColuna3 = new Paragraph(Compra.getCodNf().toString(),fCampoBold);
						}else if(selectHeader(c.toString()).equals("FORNECEDOR")){
							valorColuna3 = new Paragraph(Compra.getFornecedor().getRazao_social(),fCampoBold);
						}else if(selectHeader(c.toString()).equals("QTD ITENS")){
							valorColuna3 = new Paragraph(String.valueOf(qtdP),fCampoBold);
						}else if(selectHeader(c.toString()).equals("VALOR TOTAL")){
							valorColuna3 = new Paragraph("R$ "+Real.formatDbToString(String.valueOf(Compra.getValorTotal())),fCampoBold);
							valorColuna3.setAlignment(Element.ALIGN_RIGHT);
						}else if(selectHeader(c.toString()).equals("DATA")){
							valorColuna3 = new Paragraph(dtUtil.parseDataBra(Compra.getDataHora()),fCampoBold);
						}else if(selectHeader(c.toString()).equals("STATUS")){
							valorColuna3 = new Paragraph(Compra.getSituacao().toString(),fCampoBold);
						}
							
						pCellConteudo.addElement(valorColuna3);	
						tbConteudo.addCell(pCellConteudo);
					}

					doc.add(tbConteudo);	

					Paragraph p = new Paragraph();
					p.setAlignment(Element.ALIGN_RIGHT);
	
					resultDetalhe = CompraDAO.getItensCompra(Compra.getId());

					if(resultDetalhe!=null){
												
						PdfPCell pCell1 = new PdfPCell(new Paragraph("PRODUTO",fCampoBold));		
						pCell1.setPaddingTop(2);
						pCell1.setPaddingBottom(4);
						pCell1.setBackgroundColor(new BaseColor(232, 235, 237));
						pCell1.setBorderColor(new BaseColor(255, 255, 255));
						pCell1.setBorderWidth(1.5f);
						
						PdfPCell pCell2 = new PdfPCell(new Paragraph("QTD. ANTERIOR",fCampoBold));		
						pCell2.setPaddingTop(2);
						pCell2.setPaddingBottom(4);
						pCell2.setBackgroundColor(new BaseColor(232, 235, 237));
						pCell2.setBorderColor(new BaseColor(255, 255, 255));
						pCell2.setBorderWidth(1.5f);
						
						PdfPCell pCell4 = new PdfPCell(new Paragraph("QUANTIDADE",fCampoBold));		
						pCell4.setPaddingTop(2);
						pCell4.setPaddingBottom(4);
						pCell4.setBackgroundColor(new BaseColor(232, 235, 237));
						pCell4.setBorderColor(new BaseColor(255, 255, 255));
						pCell4.setBorderWidth(1.5f);
						
						PdfPCell pCell5 = new PdfPCell(new Paragraph("VALOR VENDA",fCampoBold));		
						pCell5.setPaddingTop(2);
						pCell5.setPaddingBottom(4);
						pCell5.setBackgroundColor(new BaseColor(232, 235, 237));
						pCell5.setBorderColor(new BaseColor(255, 255, 255));
						pCell5.setBorderWidth(1.5f);
						
						PdfPCell pCell6 = new PdfPCell(new Paragraph("GARANTIA",fCampoBold));		
						pCell6.setPaddingTop(2);
						pCell6.setPaddingBottom(4);
						pCell6.setBackgroundColor(new BaseColor(232, 235, 237));
						pCell6.setBorderColor(new BaseColor(255, 255, 255));
						pCell6.setBorderWidth(1.5f);
																		
						PdfPTable tbItensCab = new PdfPTable(new float[] {1f,0.20f, 0.20f, 0.20f, 0.30f});
						tbItensCab.setWidthPercentage(99f);
						tbItensCab.addCell(pCell1);
						tbItensCab.addCell(pCell2);
						tbItensCab.addCell(pCell4);
						tbItensCab.addCell(pCell5);
						tbItensCab.addCell(pCell6);
						
						doc.add(tbItensCab);  
						
						for(MovimentoEntDetalhe detalhe: resultDetalhe){
								
//								if(Compra.getTipoVenda().equals("PRODUTO")){
								
									String nomoProduto =" ";
									Produto produto = new Produto();
									if(detalhe.getProdutoId() != null && detalhe.getProdutoId()!= 0){
										produto = ProdutoDAO.find(detalhe.getProdutoId());	
										if(produto!=null && produto.getNome()!=null){
											nomoProduto = produto.getNome();
										}
									}

									Paragraph pNomeProduto  = new Paragraph(nomoProduto,fCampo);
									PdfPCell pCellNomeProduto = new PdfPCell(pNomeProduto);		
									pCellNomeProduto.setPaddingTop(2);
									pCellNomeProduto.setPaddingBottom(4);
									pCellNomeProduto.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellNomeProduto.setBorderColor(new BaseColor(255, 255, 255));
									pCellNomeProduto.setBorderWidth(1.5f);

									
									Paragraph pQtdAnterior  = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getQuantidade_anterior())),fCampo);											
									PdfPCell pCellQtdAnterior  = new PdfPCell(pQtdAnterior);		
									pCellQtdAnterior.setPaddingTop(2);
									pCellQtdAnterior.setPaddingBottom(4);
									pCellQtdAnterior.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellQtdAnterior.setBorderColor(new BaseColor(255, 255, 255));
									pCellQtdAnterior.setBorderWidth(1.5f);
									
									Paragraph pQtd  = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getQuantidade())),fCampo);											
									PdfPCell pCellQtd  = new PdfPCell(pQtd);		
									pCellQtd.setPaddingTop(2);
									pCellQtd.setPaddingBottom(4);
									pCellQtd.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellQtd.setBorderColor(new BaseColor(255, 255, 255));
									pCellQtd.setBorderWidth(1.5f);
									
									
									p = new Paragraph("R$ "+Real.formatDbToString(String.valueOf(detalhe.getValorVenda())),fCampo);
									
									Paragraph pValorVenda  = new Paragraph("R$ "+Real.formatDbToString(String.valueOf(detalhe.getValorVenda())),fCampo);											
									pValorVenda.setAlignment(Element.ALIGN_RIGHT);
									PdfPCell pCellValorVenda  = new PdfPCell(p);		
									pCellValorVenda.setPaddingTop(2);
									pCellValorVenda.setPaddingBottom(4);
									pCellValorVenda.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellValorVenda.setBorderColor(new BaseColor(255, 255, 255));
									pCellValorVenda.setBorderWidth(1.5f);
									
									Paragraph pGarantia  = new Paragraph(detalhe.getGarantia(),fCampo);											
									pGarantia.setAlignment(Element.ALIGN_RIGHT);
									PdfPCell pCellGarantia  = new PdfPCell(pGarantia);		
									pCellGarantia.setPaddingTop(2);
									pCellGarantia.setPaddingBottom(4);
									pCellGarantia.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellGarantia.setBorderColor(new BaseColor(255, 255, 255));
									pCellGarantia.setBorderWidth(1.5f);
									
									tbItens = new PdfPTable(new float[] {1f,0.20f, 0.20f, 0.20f, 0.30f});
									tbItens.setWidthPercentage(99f);
									tbItens.addCell(pCellNomeProduto);
									tbItens.addCell(pCellQtdAnterior);
									tbItens.addCell(pCellQtd);
									tbItens.addCell(pCellValorVenda);
									tbItens.addCell(pCellGarantia);

									
									doc.add(tbItens);                  

								}
							}
						}
		
				}



			Paragraph pQtdRegistro = new Paragraph(String.valueOf(q.getResultList().size())+" Registros Encontrados",fCampo);
			pQtdRegistro.setAlignment(Element.ALIGN_LEFT);
			doc.add(pQtdRegistro);
					
			Paragraph pResumo = new Paragraph("RESUMO:",fCab);
			pResumo.setAlignment(Element.ALIGN_LEFT);
			PdfPCell pCellRe = new PdfPCell();
			pCellRe.setBorderWidth(0);
			pCellRe.addElement(pResumo);		
			
			Paragraph pResumoVl = new Paragraph(selectUpHeader(selectFiltro(resumo)),fSubTitulo);
			pResumo.setAlignment(Element.ALIGN_LEFT);
			PdfPCell pCellReVl = new PdfPCell();
			pCellReVl.setBorderWidth(0);
			pCellReVl.addElement(pResumoVl);
			
			PdfPTable tbResu = new PdfPTable(new float[]{0.25f,1f});
			tbResu.setWidthPercentage(100f);	
			tbResu.addCell(pCellRe);
			tbResu.addCell(pCellReVl);
			tbResu.setSpacingBefore(20);
			tbResu.setSpacingAfter(10);
			
			doc.add(tbResu);
			
			
			CriteriaQuery<MovimentoEntCabecalho> criteriaQueryGroup = cb.createQuery(MovimentoEntCabecalho.class);
			Root<MovimentoEntCabecalho> rootGroup = criteriaQueryGroup.from(MovimentoEntCabecalho.class);
		
			if (selectFiltro(resumo).equals("fornecedor.razao_social")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("fornecedor").get("razao_social");		
				criteriaQueryGroup.groupBy(rootGroup.join("fornecedor").get("razao_social"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(MovimentoEntCabecalho.class,coluna, qtd));
			}
			
			if (!selectFiltro(resumo).equals("fornecedor.razao_social")) {			
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get(selectFiltro(resumo));				
				criteriaQueryGroup.groupBy(rootGroup.get(selectFiltro(resumo)));			
				
				if (criteria.size() == 0) {
					throw new RuntimeException("no criteria");
				} else if (criteria.size() == 1) {					
					criteriaQueryGroup.where(criteria.get(0));
				} else {					
					criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
				}
				
				
				//criteriaQueryGroup.where(cb.not(cb.equal(rootGroup.join("endereco_principal").get("cidade"), "BELO JARDIM")));
				criteriaQueryGroup.select(cb.construct(MovimentoEntCabecalho.class,coluna, qtd));
				
			}

			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);	
	
			for (MovimentoEntCabecalho c :(List<MovimentoEntCabecalho>) qGroup.getResultList()) {

				Paragraph pResum = new Paragraph();
				
				if(selectFiltro(resumo).equals("fornecedor.razao_social")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);	
					
				}else{
					
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(String.class)){
						pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					}
					
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Date.class)){
						SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy");
						if(c.getColuna_date()!=null){
							pResum = new Paragraph(smf.format(c.getColuna_date()), fCaptionsBold);												
						}
					}
				
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Integer.class)){
						pResum = new Paragraph(c.getColuna_Inter().toString(), fCaptionsBold);					
					}
					
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Float.class)){
						pResum = new Paragraph(String.valueOf(c.getColuna_Float()), fCaptionsBold);					
					}
				}
				pResum.setAlignment(Element.ALIGN_LEFT);
				
				
				
				
				PdfPCell pCellResumo = new PdfPCell();
				pCellResumo.setPaddingTop(0);
				pCellResumo.setPaddingBottom(4f);
				pCellResumo.addElement(pResum);
				pCellResumo.setBackgroundColor(new BaseColor(232, 235, 237));
				pCellResumo.setBorderColor(new BaseColor(255, 255, 255));	
				pCellResumo.setBorderWidth(1.5f);
				
				Paragraph pResumolVl = new Paragraph(c.getQtd().toString(),fCaptions);
				pResumolVl.setAlignment(Element.ALIGN_RIGHT);
				
				PdfPCell pCellResumoVl = new PdfPCell();
				pCellResumoVl.addElement(pResumolVl);
				pCellResumoVl.setBackgroundColor(new BaseColor(232, 235, 237));
				pCellResumoVl.setBorderColor(new BaseColor(255, 255, 255));	
				pCellResumoVl.setBorderWidth(1.5f);
				
				Paragraph pResumoVazio = new Paragraph("");
				
				PdfPCell pCellResumoVazio = new PdfPCell();
				pCellResumoVazio.addElement(pResumoVazio);
				pCellResumoVazio.setBorderColor(new BaseColor(255, 255, 255));	
				pCellResumoVazio.setBorderWidth(1.5f);
					
				Paragraph pResumoVazioPgt = null;
				
				if(c.getColuna()!=null){				
					if(c.getColuna().equals("DINHEIRO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(dinheiro)),fCaptionsBold);
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("BANCO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(banco)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("DEPOSITO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(deposito)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("CHEQUE")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(cheque)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("CARTAO CREDITO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(cartCredito)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("CARTAO DEBITO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(cartDebito)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("BOLETO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(boleto)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("NENHUMA")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(nenhuma)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}
				}
						
				PdfPCell pCellResumoVazioPgt = new PdfPCell();
				pCellResumoVazioPgt.addElement(pResumoVazioPgt);
				pCellResumoVazioPgt.setBackgroundColor(new BaseColor(232, 235, 237));
				pCellResumoVazioPgt.setBorderColor(new BaseColor(255, 255, 255));	
				pCellResumoVazioPgt.setBorderWidth(1.5f);			
								
				PdfPTable tbResumo = new PdfPTable(new float[]{0.55f,0.08f,1f});
				tbResumo.setWidthPercentage(100f);	
				tbResumo.addCell(pCellResumo);
				tbResumo.addCell(pCellResumoVl);
				tbResumo.addCell(pCellResumoVazio);
				
				PdfPTable tbResumoPgt = new PdfPTable(new float[]{0.55f,0.08f,0.10f,1f});
				tbResumoPgt.setWidthPercentage(100f);	
				tbResumoPgt.addCell(pCellResumo);
				tbResumoPgt.addCell(pCellResumoVl);
				tbResumoPgt.addCell(pCellResumoVazioPgt);
				tbResumoPgt.addCell(pCellResumoVazio);
		
				if(selectFiltro(resumo).equals("formaPagtoID.nome")){
					doc.add(tbResumoPgt);											
				}else{
					doc.add(tbResumo);																
				}
			}
			
			Paragraph ptotal = new Paragraph("TOTAL:",fCaptions);
			ptotal.setAlignment(Element.ALIGN_LEFT);
			PdfPCell pCellTotal = new PdfPCell();
			pCellTotal.setBorderWidth(0);	
			pCellTotal.addElement(ptotal);
			
			Paragraph pTotalVl = new Paragraph(""+q.getResultList().size(),fCaptionsBold);
			pTotalVl.setAlignment(Element.ALIGN_RIGHT);
			
			PdfPCell pCellTotalVl = new PdfPCell();
			pCellTotalVl.setBorderWidth(0);
			pCellTotalVl.addElement(pTotalVl);
			
			Paragraph pTotalPgt = new Paragraph(Real.formatDbToString(String.valueOf(totalPago))	,fCaptionsBold);
			pTotalPgt.setAlignment(Element.ALIGN_RIGHT);
			
			PdfPCell pCellTotalPgt = new PdfPCell();
			pCellTotalPgt.setBorderWidth(0);
			pCellTotalPgt.addElement(pTotalPgt);
			
			Paragraph pTotalVazio = new Paragraph("");		
			PdfPCell pCellTotalVazio = new PdfPCell();
			pCellTotalVazio.addElement(pTotalVazio);
			pCellTotalVazio.setBorderWidth(0);
			
			PdfPTable tbTotal = new PdfPTable(new float[]{0.55f,0.08f,1f});
			tbTotal.setWidthPercentage(100f);	
			tbTotal.addCell(pCellTotal);
			tbTotal.addCell(pCellTotalVl);
			tbTotal.addCell(pCellTotalVazio);
			tbTotal.setSpacingBefore(10);
			
			PdfPTable tbTotalPgt = new PdfPTable(new float[]{0.55f,0.08f,0.10f,1f});
			tbTotalPgt.setWidthPercentage(100f);	
			tbTotalPgt.addCell(pCellTotal);
			tbTotalPgt.addCell(pCellTotalVl);
			tbTotalPgt.addCell(pCellTotalPgt);
			tbTotalPgt.addCell(pCellTotalVazio);
			tbTotalPgt.setSpacingBefore(10);
			
			if(selectFiltro(resumo).equals("formaPagtoID.nome")){
				doc.add(tbTotalPgt);								
			}else{
				doc.add(tbTotal);
			}
			
			
		} finally {
			if (doc.isOpen() && doc != null) {
				doc.close();
			}
		}

	}
	
	public String selectUpHeader(String s) {
	
		String filtro = "";
	
		if(s.equals("id")){
			filtro = "Código";												
		}else if(s.equals("empresa_id")){
			filtro = "Cod.Empresa";					
		}else if(s.equals("codNf")){
			filtro = "Nota Fiscal";					
		}else if(s.equals("qtdItens")){
			filtro = "Quantidade Itens";					
		}else if(s.equals("valorTotal")){
			filtro = "Valor Total";					
		}else if(s.equals("dataHora")){
			filtro = "Data";					
		}else if(s.equals("fornecedor.razao_social")){
			filtro = "Fornecedor";		
		}else if(s.equals("situacao")){
			filtro = "Status";		
		}
		
		return filtro;
	}
	
	public String selectHeader(String s) {
		
		String filtro = "";
		if(s.equals("id")){
			filtro = "COD.";												
		}else if(s.equals("empresa_id")){
			filtro = "COD.EMPRESA";					
		}else if(s.equals("codNf")){
			filtro = "NOTA FISCAL";					
		}else if(s.equals("qtdItens")){
			filtro = "QTD ITENS";					
		}else if(s.equals("valorTotal")){
			filtro = "VALOR TOTAL";					
		}else if(s.equals("dataHora")){
			filtro = "DATA";					
		}else if(s.equals("fornecedor.razao_social")){
			filtro = "FORNECEDOR";		
		}else if(s.equals("situacao")){
			filtro = "STATUS";		
		}
		
		return filtro;
	}

	public String selectFiltro(String s) {
		
		String filtro = "";		
		
		if(s.equals("Código")){
			filtro = "id";												
		}else if(s.equals("Cod.Empresa")){
			filtro = "empresa_id";					
		}else if(s.equals("Nota Fiscal")){
			filtro = "codNf";					
		}else if(s.equals("Quantidade Itens")){
			filtro = "qtdItens";					
		}else if(s.equals("Valor Total")){
			filtro = "valorTotal";					
		}else if(s.equals("Data")){
			filtro = "dataHora";					
		}else if(s.equals("Fornecedor")){
			filtro = "fornecedor.razao_social";		
		}else if(s.equals("Status")){
			filtro = "situacao";		
		}
				
		return filtro;
	}	

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

	
	private PdfPCell buildCellValor(String s){
		
		Font fCampo = new Font(FontFamily.HELVETICA, 5);
		PdfPCell pCell = new PdfPCell();
		pCell.setPaddingTop(0);
		pCell.setPaddingBottom(4);
		pCell.setBackgroundColor(new BaseColor(184, 191, 198));
		pCell.setBorderColor(new BaseColor(255, 255, 255));	
		pCell.setBorderWidth(1.5f);					
						
		pCell.addElement(new Phrase(s, fCampo));
		
		return pCell;
	}
	
	private PdfPCell buildPdfCelTop(String s){
		Font fCampoBold = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
		PdfPCell pCellTop = new PdfPCell(new Phrase(s, fCampoBold));
		pCellTop.setPaddingTop(2);
		pCellTop.setPaddingBottom(4);
		pCellTop.setBackgroundColor(new BaseColor(114, 131, 151));
		pCellTop.setBorderColor(new BaseColor(255, 255, 255));	
		pCellTop.setBorderWidth(1.5f);
		
		return pCellTop;
	}
	
	

}

