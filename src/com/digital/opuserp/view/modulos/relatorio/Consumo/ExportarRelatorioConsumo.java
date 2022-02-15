package com.digital.opuserp.view.modulos.relatorio.Consumo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.RadAcct;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.NaturezaOperacao;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ByteUtil;
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
import com.vaadin.ui.Notification;

public class ExportarRelatorioConsumo implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportarRelatorioConsumo(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista, List<Object> columns)throws Exception {

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
			CriteriaQuery<RadAcct> criteriaQuery = cb.createQuery(RadAcct.class);
			Root<RadAcct> rootCliente = criteriaQuery.from(RadAcct.class);
			EntityType<RadAcct> type = em.getMetamodel().entity(RadAcct.class);

			List<Predicate> criteria = new ArrayList<Predicate>();
			List<MovimentoEntDetalhe> resultDetalhe = null;

//			criteria.add(cb.equal(rootCliente.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

			if (lista.size() > 0) {
				for (SearchParameters s : lista) {
					
					if (s.getOperador().equals("IGUAL")) {
	
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								criteria.add(cb.equal(rootCliente.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(String.class)){
								if(s.getCampo().equals("situacao")){
									criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().subSequence(0, 1).toString().toUpperCase()));
								}else{
									criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
								}
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.equal(rootCliente.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Long.class)){
								criteria.add(cb.equal(rootCliente.<Long>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
//						}

					}else if (s.getOperador().equals("DIFERENTE")) {
//						if (s.getCampo().equals("fornecedor.razao_social")) {
//							criteria.add(cb.notLike(cb.lower(rootCliente.get("fornecedor").<String>get("razao_social")), s.getValor().toLowerCase()));
//						}					
//						
//						if (!s.getCampo().equals("fornecedor.razao_social")) {								
							
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								criteria.add(cb.notEqual(rootCliente.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(String.class)){
								if(s.getCampo().equals("situacao")){
									criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())), s.getValor().substring(0,1).toString().toUpperCase()));
								}else{
									criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())), s.getValor().toLowerCase()));
								}
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.notEqual(rootCliente.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Long.class)){
								criteria.add(cb.notEqual(rootCliente.<Long>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
//						}

					} else if (s.getOperador().equals("CONTEM")) {
						
//						if (s.getCampo().equals("fornecedor.razao_social")) {
//							criteria.add(cb.like(cb.lower(rootCliente.get("fornecedor").<String>get("razao_social")), "%" + s.getValor().toLowerCase()+ "%"));
//						}					
//																		
//						if (!s.getCampo().equals("fornecedor.razao_social")) {
							
//							if(s.getCampo().equals("situacao")){
//								criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().subSequence(0, 1).toString().toUpperCase()));
//							}else{
								criteria.add(cb.like(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
//							}
//						}										
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						

//						if (s.getCampo().equals("fornecedor.razao_social")) {
//							criteria.add(cb.notLike(cb.lower(rootCliente.get("fornecedor").<String>get("razao_social")), "%" + s.getValor().toLowerCase()+ "%"));
//						}					
								
//						if (!s.getCampo().equals("fornecedor.razao_social")) {
							
//							if(s.getCampo().equals("situacao")){						
//								criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().subSequence(0, 1).toString().toUpperCase() + "%"));
//							}else{
								criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
//							}
//						}
					} else if (s.getOperador().equals("MAIOR QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThan(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Long.class)){
									criteria.add(cb.greaterThan(rootCliente.<Long> get(s.getCampo()), Long.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){								
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Long.class)){
									criteria.add(cb.lessThan(rootCliente.<Long> get(s.getCampo()), Long.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Long.class)){
									criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Long> get(s.getCampo()), Long.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Long.class)){
									criteria.add(cb.lessThanOrEqualTo(rootCliente.<Long> get(s.getCampo()), Long.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE CONSUMO",fTitulo);
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

			List<RadAcct> Consumo = q.getResultList();	
			
			
			float[] f = new float[columns.size()];
			
			Integer i=0;
			for (Object c : columns) {		
							
			    if(selectHeader(c.toString()).equals("COD.")){
			    	f[i] = (0.50f);	
			    }
			    if(selectHeader(c.toString()).equals("LOGIN")){
			    	f[i] = (0.50f);	
			    }
			    if(selectHeader(c.toString()).equals("DATA INICIAL")){
			    	f[i] = (0.60f);	
			    }
			    if(selectHeader(c.toString()).equals("DATA FINAL")){
			    	f[i] = (0.60f);	
			    }
			    if(selectHeader(c.toString()).equals("TEMPO TOTAL")){
			    	f[i] = (0.60f);	
			    }
			    if(selectHeader(c.toString()).equals("UPLOAD")){
			    	f[i] = (0.60f);	
			    }
			    if(selectHeader(c.toString()).equals("DOWNLOAD")){
			    	f[i] = (0.60f);	
			    }
			    if(selectHeader(c.toString()).equals("IP")){
			    	f[i] = (1f);	
			    }
			    if(selectHeader(c.toString()).equals("CAUSA DO TERMINO")){
			    	f[i] = (1f);	
			    }
			    i++;
     		  }				

			
			PdfPTable tbConteudo2 = new PdfPTable(f);
			
			if(tipo.equals("MULTI COLUNA")){
				
				PdfPTable tbTopo = new PdfPTable(f);
				tbTopo.setWidthPercentage(100f);				
				
				for (Object c : columns) {
//					cont++;
					
					PdfPCell pCell = new PdfPCell(new Phrase(selectHeader(c.toString()), fCampoBold));
					pCell.setPaddingTop(2);
					pCell.setPaddingBottom(4);
					pCell.setBackgroundColor(new BaseColor(114, 131, 151));
					pCell.setBorderColor(new BaseColor(255, 255, 255));	
					pCell.setBorderWidth(1.5f);
					tbTopo.addCell(pCell);
					
//					if(cont==columns.size()){
//						PdfPCell pCell1 = new PdfPCell(new Phrase("ATZ", fCampoBold));
//						pCell1.setPaddingTop(2);
//						pCell1.setPaddingBottom(4);
//						pCell1.setBackgroundColor(new BaseColor(114, 131, 151));
//						pCell1.setBorderColor(new BaseColor(255, 255, 255));	
//						pCell.setBorderWidth(1.5f);
//						tbTopo.addCell(pCell1);						
//					}
				}
				
				doc.add(tbTopo);				
				
				
				for (RadAcct cliente : Consumo) {
	
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(5f);
					
					PdfPTable tbConteudo = new PdfPTable(f);
					tbConteudo.setWidthPercentage(100f);
					
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
					
					
					
					for (Object c : columns) {
						
						try {
							PdfPCell pCell = new PdfPCell();
							pCell.setPaddingTop(0);
							pCell.setPaddingBottom(4);
							pCell.setBackgroundColor(new BaseColor(232, 235, 237));
							pCell.setBorderColor(new BaseColor(255, 255, 255));	
							pCell.setBorderWidth(1.5f);
	
							String metodo = "get"
									+ c.toString().substring(0, 1).toUpperCase()
									+ c.toString().substring(1,c.toString().length());
							
							metodo = metodo.replace(".nome_razao", "");
							metodo = metodo.replace(".contato", "");
							metodo = metodo.replace(".nome", "");

							Class cls = cliente.getClass();
							
							Method method = cls.getMethod(metodo);
								
								if (method.invoke(cliente) instanceof String || method.invoke(cliente) instanceof Long || method.invoke(cliente) instanceof Integer || method.invoke(cliente) instanceof Date) {
									
									String valor = method.invoke(cliente).toString();
									
									Paragraph valorColuna = null;
									
									 if(c.toString().equals("acctstarttime") || c.toString().equals("acctstoptime")){
										
										String dateform = dtUtil.parseDataBra(valor);
										valorColuna = new Paragraph(dateform,fCampo);			

									}else if(c.toString().equals("acctinputoctets") || c.toString().equals("acctoutputoctets")){
										
										String valorLong = ByteUtil.humanReadableByteCount(Long.parseLong(valor), true);											
										valorColuna = new Paragraph(valorLong,fCampo);	
										
									}else{
										valorColuna = new Paragraph(valor,fCampo);									
									}
									
									pCell.addElement(valorColuna);		
					
								}

							tbConteudo.addCell(pCell);									
							
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show("ERRO!");
						}
					}
					tbConteudo.addCell(pCell3);
					doc.add(tbConteudo);	

				}
			}
			
			if(tipo.equals("COLUNA ÚNICA")){
				for (RadAcct Compra : Consumo) {
	
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(25f);

					for (Object c : columns) {
						try {
							PdfPCell pCell = new PdfPCell();
							pCell.setPaddingBottom(5);
							pCell.setPaddingTop(0);
							pCell.addElement(new Phrase(selectHeader(c.toString()), fCampo));
							
							String metodo = "get"
									+ c.toString().substring(0, 1).toUpperCase()
									+ c.toString().substring(1,c.toString().length());
							
							metodo = metodo.replace(".nome_razao", "");
							metodo = metodo.replace(".id", "");
							metodo = metodo.replace(".email", "");
							metodo = metodo.replace(".telefone1", "");
							metodo = metodo.replace(".telefone2", "");
							metodo = metodo.replace(".celular1", "");
							metodo = metodo.replace(".celular2", "");
							metodo = metodo.replace(".transportadora_padrao", "");
							metodo = metodo.replace(".nome", "");
							metodo = metodo.replace(".descricao", "");							
							metodo = metodo.replace(".bairro", "");
							metodo = metodo.replace(".cidade", "");
							metodo = metodo.replace(".pais", "");
							metodo = metodo.replace(".complemento", "");
							metodo = metodo.replace(".referencia", "");
							metodo = metodo.replace(".endereco", "");
							metodo = metodo.replace(".numero", "");
							metodo = metodo.replace(".uf", "");
							metodo = metodo.replace(".cep", "");
							
							Class cls = Compra.getClass();
							Method method = cls.getMethod(metodo);
	
							if(method.invoke(Compra)==null||method.invoke(Compra).toString().equals("")){
								pCell.addElement(new Phrase(" ", fConteudo));		
							}	
														
							if (method.invoke(Compra) instanceof String || method.invoke(Compra) instanceof Long || method.invoke(Compra) instanceof Integer || method.invoke(Compra) instanceof Date) {
								String valor = method.invoke(Compra).toString();
					
								valor = method.invoke(Compra).toString();										

								Paragraph valorColuna = null;
								
								 if(c.toString().equals("acctstarttime") || c.toString().equals("acctstoptime")){
										
										String dateform = dtUtil.parseDataBra(valor);
										valorColuna = new Paragraph(dateform,fCampo);									
								 }else if(c.toString().equals("acctinputoctets") || c.toString().equals("acctoutputoctets")){
										
										String valorLong = ByteUtil.humanReadableByteCount(Long.parseLong(valor), true);											
										valorColuna = new Paragraph(valorLong,fCampo);	
										
									}else{
										valorColuna = new Paragraph(valor,fCampo);									
									}

								pCell.addElement(valorColuna);
							}
	
							tb1.addCell(pCell);
													
							
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show("ERRO!");
						}
					}
	
					doc.add(tb1);
					
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
			
			
			CriteriaQuery<RadAcct> criteriaQueryGroup = cb.createQuery(RadAcct.class);
			Root<RadAcct> rootGroup = criteriaQueryGroup.from(RadAcct.class);
		
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
							
				criteriaQueryGroup.select(cb.construct(RadAcct.class,coluna, qtd));

				
			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);	
	
			for (RadAcct c :(List<RadAcct>) qGroup.getResultList()) {

				Paragraph pResum = new Paragraph();
					
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(String.class)){
						pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					}else
					
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Date.class)){
						SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						if(c.getColuna_date()!=null){
							pResum = new Paragraph(smf.format(c.getColuna_date()), fCaptionsBold);												
						}
					}else
				
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Integer.class)){
						pResum = new Paragraph(c.getColuna_Inter().toString(), fCaptionsBold);					
					}else{
					
//					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Long.class)){
//						if(c.getColuna_long() != null){
							String valor = ByteUtil.humanReadableByteCount(c.getColuna_long(), true);											
							pResum = new Paragraph(valor, fCaptionsBold);					
//						}

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
			
			
		} finally {
			if (doc.isOpen() && doc != null) {
				doc.close();
			}
		}

	}
	
	public String selectUpHeader(String s) {
	
		String filtro = "";
	
		if(s.equals("radacctid")){
			filtro = "Código";												
		}else if(s.equals("username")){
			filtro = "Login";					
		}else if(s.equals("acctstarttime")){
			filtro = "Data Inicio";					
		}else if(s.equals("acctstoptime")){
			filtro = "Data Final";					
		}else if(s.equals("acctsessiontime")){
			filtro = "Tempo Total";					
		}else if(s.equals("acctinputoctets")){
			filtro = "Upload";					
		}else if(s.equals("acctoutputoctets")){
			filtro = "Download";		
		}else if(s.equals("acctterminatecause")){
			filtro = "Causa do Término";		
		}else if(s.equals("framedipaddress")){
			filtro = "Ip";		
		}
		
		return filtro;
	}
	
	public String selectHeader(String s) {
		
		String filtro = "";
		if(s.equals("radacctid")){
			filtro = "COD.";												
		}else if(s.equals("username")){
			filtro = "LOGIN";					
		}else if(s.equals("acctstarttime")){
			filtro = "DATA INICIAL";					
		}else if(s.equals("acctstoptime")){
			filtro = "DATA FINAL";					
		}else if(s.equals("acctsessiontime")){
			filtro = "TEMPO TOTAL";					
		}else if(s.equals("acctinputoctets")){
			filtro = "UPLOAD";					
		}else if(s.equals("acctoutputoctets")){
			filtro = "DOWNLOAD";		
		}else if(s.equals("acctterminatecause")){
			filtro = "CAUSA DO TERMINO";		
		}else if(s.equals("framedipaddress")){
			filtro = "IP";		
		}
		
		return filtro;
	}

	public String selectFiltro(String s) {
		
		String filtro = "";		
		
		if(s.equals("Código")){
			filtro = "radacctid";												
		}else if(s.equals("Login")){
			filtro = "username";					
		}else if(s.equals("Data Inicio")){
			filtro = "acctstarttime";					
		}else if(s.equals("Data Final")){
			filtro = "acctstoptime";					
		}else if(s.equals("Tempo Total")){
			filtro = "acctsessiontime";					
		}else if(s.equals("Upload")){
			filtro = "acctinputoctets";					
		}else if(s.equals("Download")){
			filtro = "acctoutputoctets";		
		}else if(s.equals("Causa do Término")){
			filtro = "acctterminatecause";		
		}else if(s.equals("Ip")){
			filtro = "framedipaddress";		
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

