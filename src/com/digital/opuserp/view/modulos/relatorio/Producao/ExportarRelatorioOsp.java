package com.digital.opuserp.view.modulos.relatorio.Producao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
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
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Osp;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.digital.opuserp.view.util.Notify;
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

public class ExportarRelatorioOsp implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportarRelatorioOsp(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista, List<Object> columns)throws Exception {

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
			CriteriaQuery<Osp> criteriaQuery = cb.createQuery(Osp.class);
			Root<Osp> rootCliente = criteriaQuery.from(Osp.class);
			EntityType<Osp> type = em.getMetamodel().entity(Osp.class);

			List<Predicate> criteria = new ArrayList<Predicate>();

			criteria.add(cb.equal(rootCliente.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

			if (lista.size() > 0) {
				for (SearchParameters s : lista) {
					
					if (s.getOperador().equals("IGUAL")) {
						if (s.getCampo().equals("cliente")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("servico.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("servico").<String>get("nome")), s.getValor().toLowerCase()));
						}
						
						if (!s.getCampo().equals("servico.nome") && !s.getCampo().equals("cliente") ) {
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf;
								
								if(s.getCampo().equals("data_previsao_termino") || s.getCampo().equals("data_agendado")){
									sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								}else{
									sdf = new SimpleDateFormat("dd/MM/yyyy");
								}
								criteria.add(cb.equal(rootCliente.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.equal(rootCliente.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
						}

					}else if (s.getOperador().equals("DIFERENTE")) {
						if (s.getCampo().equals("cliente")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("servico.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("servico").<String>get("nome")), s.getValor().toLowerCase()));
						}													

						if (!s.getCampo().equals("servico.nome") && !s.getCampo().equals("cliente") ) {														
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								criteria.add(cb.notEqual(rootCliente.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())), s.getValor().toLowerCase()));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.notEqual(rootCliente.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							
						}

					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("cliente")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("servico.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("servico").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}							
						if (!s.getCampo().equals("servico.nome") && !s.getCampo().equals("cliente") ) {													
							if(rootCliente.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())), "%" + s.getValor().toLowerCase()+ "%"));
							}
						}
											
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						

						if (s.getCampo().equals("cliente")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("servico.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("servico").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}															

						if (!s.getCampo().equals("servico.nome") && !s.getCampo().equals("cliente") ) {																			
							criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
					} else if (s.getOperador().equals("MAIOR QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThan(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
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

			if(selectFiltro(order).equals("cliente")){				
				criteriaQuery.orderBy(cb.asc(rootCliente.join("cliente").get("nome_razao")));				
			}else if(selectFiltro(order).equals("servico.nome")){				
				criteriaQuery.orderBy(cb.asc(rootCliente.join("servico").get("nome")));				
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE PRODUÇÃO",fTitulo);
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
			SbVl.append(tipo+"\n"+orientacao+"\n"+selectHeader(selectFiltro(order))+"\n");

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
			tbform.setSpacingAfter(10);
			
			doc.add(tbform);

			// Estilos de Fonts
			Font fValores = new Font(FontFamily.COURIER, 6, Font.NORMAL);

			List<Osp> osps = q.getResultList();	
			
			
			float[] f = new float[columns.size()];
			
			Integer i=0;
			for (Object c : columns) {
									
				if(selectHeader(c.toString()).equals("Cliente")){
					f[i] = (0.60f);		
				}	
			    if(selectHeader(c.toString()).equals("Código")){
			    	f[i] = (0.14f);	
			    }
			    if(selectHeader(c.toString()).equals("Pedido")){
			    	f[i] = (0.14f);	
			    }
			    if(selectHeader(c.toString()).equals("Servico")){
			    	f[i] = (0.28f);	
			    }
			    if(selectHeader(c.toString()).equals("Descrição")){
			    	f[i] = (0.20f);	
			    }
			    if(selectHeader(c.toString()).equals("Observação")){
			    	f[i] = (0.27f);	
			    }
			    if(selectHeader(c.toString()).equals("Data Agendado")){
			    	f[i] = (0.20f);	
			    }                                         
			    if(selectHeader(c.toString()).equals("Data Previsão Termino")){
			    	f[i] = (0.35f);	
			    }			   		    
			    if(selectHeader(c.toString()).equals("Data Termino")){
			    	f[i] = (0.35f);	
			    }			    
			    if(selectHeader(c.toString()).equals("Operador Abertura")){
			    	f[i] = (0.27f);	
			    }	
			    if(selectHeader(c.toString()).equals("Operador Produção")){
			    	f[i] = (0.30f);	
			    }	   	
				if(selectHeader(c.toString()).equals("Entregar")){
					f[i] = (0.35f);	
				}
				 if(selectHeader(c.toString()).equals("Motivo Cancelamento")){
					 f[i] = (0.35f);	
				}	 
		    	 if(selectHeader(c.toString()).equals("Status")){
		    		 f[i] = (0.25f);	
		    	 }
		    	 if(selectHeader(c.toString()).equals("Setor")){
		    		f[i] = (0.20f);	
		    	 }
		    	 if(selectHeader(c.toString()).equals("Comprador")){
		    		f[i] = (0.30f);	 		
		    	 }
		
				
			    i++;
     		  }					

					
			Integer reg= 0;
//			Integer cont = 0;
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
					
				}
				
				doc.add(tbTopo);				
				
				
				for (Osp osp : osps) {
	
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
							metodo = metodo.replace(".nome", "");
							metodo = metodo.replace(".cod_veiculo", "");							

							

							Class cls = osp.getClass();
							
							Method method = cls.getMethod(metodo);
								
								if (method.invoke(osp) instanceof String || method.invoke(osp) instanceof Integer || method.invoke(osp) instanceof Date) {
									
									String valor = method.invoke(osp).toString();
									
									Paragraph valorColuna = null;
									
									 if(c.toString().equals("data_agendado")||c.toString().equals("data_previsao_termino")||c.toString().equals("data_termino")){
										
										String dateform;
										
										if(c.toString().equals("data_previsao_termino") || c.toString().equals("data_termino")){
											dateform = dtUtil.parseDataHoraBra(valor);
										}else{
											dateform = dtUtil.parseDataBra(valor);
										}
										
										valorColuna = new Paragraph(dateform,fCampo);									
									}else{
										
										if(c.toString().equals("status")){
											
											if(valor.equals("A")){
												valor = "ABERTO";
											}else if(valor.equals("C")){
												valor = "CANCELAMENTO";
											}else if(valor.equals("F")){
												valor = "FECHADO";
											}else if(valor.equals("P")){
												valor = "PRONTO";
											}else if(valor.equals("E")){
												valor = "ENCAMINHADO";
											}else if(valor.equals("AP")){
												valor = "APROVACAO";
											}
										}
										
										valorColuna = new Paragraph(valor,fCampo);									
									}
									
									pCell.addElement(valorColuna);		
						
									
								}else if (method.invoke(osp) instanceof Cliente) {
									
									Cliente cat = (Cliente) method.invoke(osp);
									
									String valorColuna = "";
									
									if(c.toString().equals("cliente")){
										valorColuna = cat.getNome_razao();
									}	
																				
									pCell.addElement(new Phrase(valorColuna, fCampo));								
										
								}else if (method.invoke(osp) instanceof Servico) {
									
									Servico cat = (Servico) method.invoke(osp);
									
									String valorColuna = "";
									
									if(c.toString().equals("servico.nome")){
										valorColuna = cat.getNome();
									}						
									pCell.addElement(new Phrase(valorColuna, fCampo));			

								}

							tbConteudo.addCell(pCell);									
							
						} catch (Exception e) {
							e.printStackTrace();							
							Notify.Show("Métodos Construtores não encontrados!", Notify.TYPE_ERROR);
						}
					}
					tbConteudo.addCell(pCell3);
					doc.add(tbConteudo);	

				}
				

			}

			
			
			if(tipo.equals("COLUNA ÚNICA")){
				for (Osp osp : osps) {
	
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
							metodo = metodo.replace(".cod_veiculo", "");
							metodo = metodo.replace(".nome", "");
							
							Class cls = osp.getClass();
							Method method = cls.getMethod(metodo);
	
							if(method.invoke(osp)==null||method.invoke(osp).toString().equals("")){
								pCell.addElement(new Phrase(" ", fConteudo));		
							}	
														
							if (method.invoke(osp) instanceof String || method.invoke(osp) instanceof Integer || method.invoke(osp) instanceof Date) {
								String valor = method.invoke(osp).toString();
																			
								Paragraph valorColuna = null;
								
								 if(c.toString().equals("data_encaminhamento")||c.toString().equals("data_abertura")||c.toString().equals("data_conclusao")||c.toString().equals("data_ex")){
									String dateform = dtUtil.parseDataBra(valor);
									valorColuna = new Paragraph(dateform,fConteudo);									
								}else{
									valorColuna = new Paragraph(valor,fConteudo);									
								}
								
								pCell.addElement(valorColuna);								
							
							}else if (method.invoke(osp) instanceof Cliente) {

								Cliente cat = (Cliente) method.invoke(osp);
								
								String valorColuna = "";
								
								if(c.toString().equals("cliente")){
									valorColuna = cat.getNome_razao();
								}
								
							}else if (method.invoke(osp) instanceof Servico) {
								
								Servico cat = (Servico) method.invoke(osp);
								
								String valorColuna = "";
								
								if(c.toString().equals("servico.nome")){
									valorColuna = cat.getNome();
								}						
								pCell.addElement(new Phrase(valorColuna, fCampo));			

							}
	
							tb1.addCell(pCell);
													
							
						} catch (Exception e) {
							e.printStackTrace();
							Notify.Show("Métodos Construtores não encontrados!", Notify.TYPE_ERROR);
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
			
			Paragraph pResumoVl = new Paragraph(selectHeader(selectFiltro(resumo)),fSubTitulo);
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
			
			
			CriteriaQuery<Osp> criteriaQueryGroup = cb.createQuery(Osp.class);
			Root<Osp> rootGroup = criteriaQueryGroup.from(Osp.class);
			
			
			if (selectFiltro(resumo).equals("cliente")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("nome_razao");			
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("nome_razao"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Osp.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("servico.nome")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("servico").get("nome");				
				criteriaQueryGroup.groupBy(rootGroup.join("servico").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Osp.class,coluna, qtd));
			}
			
		

			

			if ( !selectFiltro(resumo).equals("servico.nome") && !selectFiltro(resumo).equals("cliente")) {
						
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
				criteriaQueryGroup.select(cb.construct(Osp.class,coluna, qtd));
				
			}

			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);	
	
			for (Osp c :(List<Osp>) qGroup.getResultList()) {

				Paragraph pResum = new Paragraph();
				
				if(selectFiltro(resumo).equals("cliente")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);	
					
				}else if(selectFiltro(resumo).equals("servico.nome")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);		
				
				}else{
				
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(String.class)){
						
						
						String valor = c.getColuna();
						if(resumo.equals("Status")){
							if(c.getColuna().equals("A")){
								valor = "ABERTO";
							}else if(c.getColuna().equals("C")){
								valor = "CANCELAMENTO";
							}else if(c.getColuna().equals("F")){
								valor = "FECHADO";
							}else if(c.getColuna().equals("P")){
								valor = "PRONTO";
							}else if(c.getColuna().equals("E")){
								valor = "ENCAMINHADO";
							}else if(c.getColuna().equals("AP")){
								valor = "APROVACAO";
							}
						}
						pResum = new Paragraph(valor, fCaptionsBold);
					}
					
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Date.class)){
						SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						if(c.getColuna_date()!=null){
							pResum = new Paragraph(smf.format(c.getColuna_date()), fCaptionsBold);												
						}
					}
				
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Integer.class)){
						pResum = new Paragraph(c.getColuna_Inter().toString(), fCaptionsBold);					
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
				
				PdfPTable tbResumo = new PdfPTable(new float[]{0.55f,0.08f,1f});
				tbResumo.setWidthPercentage(100f);	
				tbResumo.addCell(pCellResumo);
				tbResumo.addCell(pCellResumoVl);
				tbResumo.addCell(pCellResumoVazio);
				
				doc.add(tbResumo);																

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


			doc.add(tbTotal);
			
		} finally {
			if (doc.isOpen() && doc != null) {
				doc.close();
			}
		}

	}
	
	public String selectHeader(String s) {
	
		String filtro = "";
		
		if(s.equals("cliente")){
			filtro = "Cliente";							
		}else if(s.equals("id")){
			filtro = "Código";					
		}else if(s.equals("venda_servico_cabecalho_id")){
			filtro = "Pedido";					
		}else if(s.equals("servico.nome")){
			filtro = "Servico";					
		}else if(s.equals("descricao_servico")){
			filtro = "Descrição";					
		}else if(s.equals("observacao")){
			filtro = "Observação";	
		}else if(s.equals("data_agendado")){
			filtro = "Data Agendado";								
		}else if(s.equals("data_previsao_termino")){
			filtro = "Data Previsão Termino";					
		}else if(s.equals("data_termino")){
			filtro = "Data Termino";					
		}else if(s.equals("operador_abertura")){
			filtro = "Operador Abertura";		
		}else if(s.equals("operador_producao")){
			filtro = "Operador Produção";		
		}else if(s.equals("entregar")){
			filtro = "Entregar";		
		}else if(s.equals("motivo_cancelamento")){
			filtro = "Motivo Cancelamento";		
		}else if(s.equals("status")){
			filtro = "Status";		
		}else if(s.equals("setor")){
			filtro = "Setor";		
		}else if(s.equals("comprador")){
			filtro = "Comprador";		
		}
		
		return filtro;
	}
	
	

	public String selectFiltro(String s) {
		
		String filtro = "";
		if(s.equals("Cliente")){
			filtro = "cliente";							
		}else if(s.equals("Código")){
			filtro = "id";					
		}else if(s.equals("venda_servico_cabecalho_id")){
			filtro = "Pedido";					
		}else if(s.equals("Servico")){
			filtro = "servico.nome";					
		}else if(s.equals("Descrição")){
			filtro = "descricao_servico";					
		}else if(s.equals("Observação")){
			filtro = "observacao";	
		}else if(s.equals("Data Agendado")){
			filtro = "data_agendado";								
		}else if(s.equals("Data Previsão Termino")){
			filtro = "data_previsao_termino";					
		}else if(s.equals("Data Termino")){
			filtro = "data_termino";					
		}else if(s.equals("Operador Abertura")){
			filtro = "operador_abertura";		
		}else if(s.equals("Operador Produção")){
			filtro = "operador_producao";		
		}else if(s.equals("Entregar")){
			filtro = "entregar";		
		}else if(s.equals("Motivo Cancelamento")){
			filtro = "motivo_cancelamento";		
		}else if(s.equals("Status")){
			filtro = "status";		
		}else if(s.equals("Setor")){
			filtro = "setor";		
		}else if(s.equals("Comprador")){
			filtro = "comprador";		
		}
		
		return filtro;
	}	

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}

