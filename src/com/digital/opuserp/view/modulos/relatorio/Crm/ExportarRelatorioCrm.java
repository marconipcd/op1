package com.digital.opuserp.view.modulos.relatorio.Crm;

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
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.CrmFormasContato;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
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

public class ExportarRelatorioCrm implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportarRelatorioCrm(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista, List<Object> columns)throws Exception {

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
			CriteriaQuery<Crm> criteriaQuery = cb.createQuery(Crm.class);
			Root<Crm> rootCliente = criteriaQuery.from(Crm.class);
			EntityType<Crm> type = em.getMetamodel().entity(Crm.class);

			List<Predicate> criteria = new ArrayList<Predicate>();

			criteria.add(cb.equal(rootCliente.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

			if (lista.size() > 0) {
				for (SearchParameters s : lista) {

					if (s.getOperador().equals("IGUAL")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.contato")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("contato")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("crm_assuntos.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("crm_assuntos").<String>get("nome")), s.getValor().toLowerCase()));
						}	
						
						if (s.getCampo().equals("setor.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("setor").<String>get("nome")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("contrato.id")) {
							criteria.add(cb.equal(rootCliente.get("contrato").<String>get("id"), s.getValor().toLowerCase()));
						}

						if (!s.getCampo().equals("cliente.contato") && !s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("crm_assuntos.nome")&& !s.getCampo().equals("setor.nome") && !s.getCampo().equals("contrato.id")) {
							
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
							
						}

					}else if (s.getOperador().equals("DIFERENTE")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("cliente.contato")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("contato")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("crm_assuntos.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("crm_assuntos").<String>get("nome")), s.getValor().toLowerCase()));
						}

						if (s.getCampo().equals("setor.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("setor").<String>get("nome")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("contrato.id")) {
							criteria.add(cb.notEqual(rootCliente.get("contrato").<Integer>get("id"), s.getValor().toLowerCase()));
						}

						if (!s.getCampo().equals("cliente.contato") && !s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("crm_assuntos.nome")&& !s.getCampo().equals("setor.nome") && !s.getCampo().equals("contrato.id")) {
																					
							
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
							
							
						}

					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.contato")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("contato")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("crm_assuntos.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("crm_assuntos").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("setor.nome")) {							
							criteria.add(cb.like(cb.lower(rootCliente.get("setor").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));						
						}						
						if (s.getCampo().equals("contrato.id")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("contrato").<String>get("id")),"%" + s.getValor().toLowerCase()+ "%"));
						}						
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						

						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.contato")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("contato")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("crm_assuntos.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("crm_assuntos").<String>get("nome")), s.getValor().toLowerCase()));
						}						
						if (s.getCampo().equals("setor.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("setor").<String>get("nome")), s.getValor().toLowerCase()));
						}						
						if (s.getCampo().equals("contrato.id")) {
							criteria.add(cb.notEqual(rootCliente.get("contrato").<Integer>get("id"), s.getValor().toLowerCase()));
						}
						if (!s.getCampo().equals("cliente.contato") && !s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("crm_assuntos.nome")&& !s.getCampo().equals("setor.nome") && !s.getCampo().equals("contrato.id")) {
														
							criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
												
					} else if (s.getOperador().equals("MAIOR QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThan(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
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

			if(selectFiltro(order).equals("cliente.nome_razao")){				
				criteriaQuery.orderBy(cb.asc(rootCliente.join("cliente").get("nome_razao")));				
			}else if(selectFiltro(order).equals("cliente.contato")){				
				criteriaQuery.orderBy(cb.asc(rootCliente.join("cliente").get("contato")));				
			}else if(selectFiltro(order).equals("crm_assuntos.nome")){
				criteriaQuery.orderBy(cb.asc(rootCliente.join("crm_assuntos").get("nome")));				
			}else if(selectFiltro(order).equals("setor.nome")){
				criteriaQuery.orderBy(cb.asc(rootCliente.join("setor").get("nome")));				
			}else if(selectFiltro(order).equals("contrato.id")){
				criteriaQuery.orderBy(cb.asc(rootCliente.join("contrato").get("id")));				
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE CRM",fTitulo);
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
			tbform.setSpacingAfter(10);
			
			doc.add(tbform);

			// Estilos de Fonts
			Font fValores = new Font(FontFamily.COURIER, 6, Font.NORMAL);

			List<Crm> contas = q.getResultList();

			
			float[] f = new float[columns.size()+1];
			
			Integer i=0;
			for (Object c : columns) {
	
				if(selectHeader(c.toString()).equals("PROTOCOLO")){
					f[i] = (0.12f);	
				}
				if(selectHeader(c.toString()).equals("ABERTURA")){
					f[i] = (0.15f);	
				}
				if(selectHeader(c.toString()).equals("HORA")){
					f[i] = (0.20f);	
				}
				if(selectHeader(c.toString()).equals("SETOR")){
					f[i] = (0.35f);	
				}
				if(selectHeader(c.toString()).equals("ASSUNTO")){
					f[i] = (0.40f);	
				}	
				if(selectHeader(c.toString()).equals("CLIENTE")){
					f[i] = (0.60f);		
				}	
				if(selectHeader(c.toString()).equals("CONTEUDO")){
					f[i] = (0.60f);		
				}
			    if(selectHeader(c.toString()).equals("CONTATO")){
			    	f[i] = (0.45f);	
			    }         
			    if(selectHeader(c.toString()).equals("FORMA CONTATO")){
			    	f[i] = (0.45f);	
			    }  
			    if(selectHeader(c.toString()).equals("PREVISÃO")){
			    	f[i] = (0.23f);	
			    }	
			    if(selectHeader(c.toString()).equals("CONCLUSÃO")){
			    	f[i] = (0.08f);	
			    }	
			   
			    if(selectHeader(c.toString()).equals("FEEDBACK")){
			    	f[i] = (0.23f);	
			    }	
			    if(selectHeader(c.toString()).equals("T. TRAT.")){
			    	f[i] = (0.05f);	
			    }	
			    if(selectHeader(c.toString()).equals("OPERADOR")){
			    	f[i] = (0.06f);	
			    }	
			    if(selectHeader(c.toString()).equals("STATUS")){
			    	f[i] = (0.20f);	
			    }			    
			    if(selectHeader(c.toString()).equals("REAGENDAMENTO")){
			    	f[i] = (0.35f);	
			    }
			    if(selectHeader(c.toString()).equals("CONTRATO")){
			    	f[i] = (0.15f);	
			    }
			    if(selectHeader(c.toString()).equals("PLANO")){
			    	f[i] = (0.07f);	
			    }
			    if(selectHeader(c.toString()).equals("CONCENTRADOR")){
			    	f[i] = (0.07f);	
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
					PdfPCell pdfCellHora = new PdfPCell(new Phrase("HORA", fCampoBold));
					pdfCellHora.setBackgroundColor(new BaseColor(114, 131, 151));
					pdfCellHora.setBorderColor(new BaseColor(255, 255, 255));
					pdfCellHora.setBorderWidth(1.5f);
					pdfCellHora.setPaddingTop(2);
					pdfCellHora.setPaddingBottom(4);
				
				tbTopo.addCell(pdfCellHora);
				doc.add(tbTopo);				
				
				
				for (Crm cliente : contas) {
	
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
							metodo = metodo.replace(".id", "");

							Class cls = cliente.getClass();
							
							Method method = cls.getMethod(metodo);
							
							Paragraph valorColuna3 = null;
							valorColuna3 = new Paragraph(" ",fCampoBold);
							
//							if(selectHeader(c.toString()).equals("HORA")){								
//								valorColuna3 = new Paragraph(DataUtil.formatHoraBra(cliente.getData_cadastro()),fCampoBold);					
////							}else if(selectHeader(c.toString()).equals("TIPO")){
////								valorColuna3 = new Paragraph(cliente.getTipo(),fCampoBold);
//							}
							
								
								if (method.invoke(cliente) instanceof String || method.invoke(cliente) instanceof Integer || method.invoke(cliente) instanceof Date) {
									
									String valor = method.invoke(cliente).toString();
									
									Paragraph valorColuna = null;
									
									
//									 if(c.toString().equals("data_cadastro")){
//											
//										String dateform = dtUtil.parseDataBra(valor);
//										valorColuna = new Paragraph(dateform,fCampo);										
//									}else if(c.toString().equals("data_efetuado")){
//										
//										String dateform = dtUtil.parseDataHoraBra(valor);
//										valorColuna = new Paragraph(dateform,fCampo);
//									}else{
//										valorColuna = new Paragraph(valor,fCampo);																			
//									}
//									
//									 pCell.addElement(valorColuna);	

									 if(c.toString().equals("data_efetuado")){
										
										String dateform = dtUtil.parseDataHoraBra(valor);
										valorColuna = new Paragraph(dateform,fCampo);									
									}else{
										valorColuna = new Paragraph(valor,fCampo);									
									}
									
									pCell.addElement(valorColuna);		
//						
									
								}else if (method.invoke(cliente) instanceof Cliente) {
									
									Cliente cat = (Cliente) method.invoke(cliente);
									
									String valorColuna = "";
									
									if(c.toString().equals("cliente.nome_razao")){
										valorColuna = cat.getNome_razao();
									}	
									
									if(c.toString().equals("cliente.contato")){
										valorColuna = cat.getContato();
									}											
									pCell.addElement(new Phrase(valorColuna, fCampo));								
										
								}else if (method.invoke(cliente) instanceof CrmAssunto) {
									
									CrmAssunto cat = (CrmAssunto) method.invoke(cliente);
									
									String valorColuna = "";
									
									if(c.toString().equals("crm_assuntos.nome")){
										valorColuna = cat.getNome();
									}						
									pCell.addElement(new Phrase(valorColuna, fCampo));			

								}else if (method.invoke(cliente) instanceof Setores) {
									
									Setores cat = (Setores) method.invoke(cliente);
									
									String valorColuna = "";
									
									if(c.toString().equals("setor.nome")){
										valorColuna = cat.getNome();
									}						
									pCell.addElement(new Phrase(valorColuna, fCampo));		
									
								}else if (method.invoke(cliente) instanceof CrmFormasContato) {
									
									CrmFormasContato cat = (CrmFormasContato) method.invoke(cliente);
									
									String valorColuna = "";
									
									if(c.toString().equals("contato_feedback.nome")){
										valorColuna = cat.getNome();
									}		
									if(c.toString().equals("crm_formas_contato.nome")){
										valorColuna = cat.getNome();
									}
									pCell.addElement(new Phrase(valorColuna, fConteudo));			
								}else if (method.invoke(cliente) instanceof AcessoCliente) {
									
									AcessoCliente cat = (AcessoCliente) method.invoke(cliente);
									
									String valorColuna = "";
									
									if(c.toString().equals("contrato.id")){
										valorColuna = cat.getId().toString();
									}		
									if(c.toString().equals("contrato.id")){
										valorColuna = cat.getId().toString();
									}
									pCell.addElement(new Phrase(valorColuna, fConteudo));			
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
				
//				columns.add("ATZ");
//				for (Object c : columns) {
//					reg++;
//					
//					Paragraph TotalPago = null;
//					PdfPCell pCell2 = new PdfPCell();
//
//					TotalPago = new Paragraph(" ",fCampo);				
//					
//					pCell2.setPaddingTop(0);
//					pCell2.setPaddingBottom(4);
//					pCell2.setBorderColor(new BaseColor(255, 255, 255));	
//					pCell2.setBorderWidth(1.5f);
//					pCell2.addElement(TotalPago);
//
//					tbConteudo2.setWidthPercentage(100f);					
//					tbConteudo2.addCell(pCell2);											
//					if(reg==columns.size()){
//						doc.add(tbConteudo2);	
//					}
//				}
			}

			
			
			if(tipo.equals("COLUNA ÚNICA")){
				for (Crm conta : contas) {
	
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
							metodo = metodo.replace(".contato", "");
							metodo = metodo.replace(".nome", "");
							metodo = metodo.replace(".id", "");
							
							Class cls = conta.getClass();
							Method method = cls.getMethod(metodo);
	
							if(method.invoke(conta)==null||method.invoke(conta).toString().equals("")){
								pCell.addElement(new Phrase(" ", fConteudo));		
							}	
														
							if (method.invoke(conta) instanceof String || method.invoke(conta) instanceof Integer || method.invoke(conta) instanceof Date) {
								String valor = method.invoke(conta).toString();
																			
								Paragraph valorColuna = null;
								
								if(c.toString().equals("data_agendado")){
									
									String dateform = dtUtil.parseDataBra(valor);
									valorColuna = new Paragraph(dateform,fConteudo);									
								}else{
									valorColuna = new Paragraph(valor,fConteudo);									
								}
								
								pCell.addElement(valorColuna);								
							
							}else if (method.invoke(conta) instanceof Cliente) {

								Cliente cat = (Cliente) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("cliente.nome_razao")){
									valorColuna = cat.getNome_razao();
								}
								
								if(c.toString().equals("cliente.contato")){
									valorColuna = cat.getContato();
								}						

								pCell.addElement(new Phrase(valorColuna, fConteudo));

							}else if (method.invoke(conta) instanceof CrmAssunto) {
								
								CrmAssunto cat = (CrmAssunto) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("crm_assuntos.nome")){
									valorColuna = cat.getNome();
								}						
								pCell.addElement(new Phrase(valorColuna, fConteudo));			

							}else if (method.invoke(conta) instanceof Setores) {
								
								Setores cat = (Setores) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("setor.nome")){
									valorColuna = cat.getNome();
								}						
								pCell.addElement(new Phrase(valorColuna, fConteudo));	
								
							}else if (method.invoke(conta) instanceof CrmFormasContato) {
								
								Setores cat = (Setores) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("contato_feedback.nome")){
									valorColuna = cat.getNome();
								}						
								if(c.toString().equals("crm_formas_contato.nome")){
									valorColuna = cat.getNome();
								}
								pCell.addElement(new Phrase(valorColuna, fConteudo));			
							}
							
							else if (method.invoke(conta) instanceof AcessoCliente) {
								
								AcessoCliente cat = (AcessoCliente) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("contrato.id")){
									valorColuna = cat.getId().toString();
								}						
								if(c.toString().equals("contrato.id")){
									valorColuna = cat.getId().toString();
								}
								pCell.addElement(new Phrase(valorColuna, fConteudo));			
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
			
			
			CriteriaQuery<Crm> criteriaQueryGroup = cb.createQuery(Crm.class);
			Root<Crm> rootGroup = criteriaQueryGroup.from(Crm.class);
			
			
			if (selectFiltro(resumo).equals("cliente.nome_razao")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("nome_razao");			
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("nome_razao"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("cliente.contato")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("contato");				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("contato"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("crm_assuntos.nome")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("crm_assuntos").get("nome");			
				criteriaQueryGroup.groupBy(rootGroup.join("crm_assuntos").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("contato_feedback.nome")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("contato_feedback").get("nome");			
				criteriaQueryGroup.groupBy(rootGroup.join("contato_feedback").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("setor.nome")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("setor").get("nome");				
				criteriaQueryGroup.groupBy(rootGroup.join("setor").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("contrato.id")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("contrato").get("id");				
				criteriaQueryGroup.groupBy(rootGroup.join("contrato").get("id"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
			}

			

			if (!selectFiltro(resumo).equals("cliente.contato") && !selectFiltro(resumo).equals("cliente.nome_razao")
					&& !selectFiltro(resumo).equals("crm_assuntos.nome") && !selectFiltro(resumo).equals("setor.nome")&& !selectFiltro(resumo).equals("contato_feedback.nome")) {
						
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
				criteriaQueryGroup.select(cb.construct(Crm.class,coluna, qtd));
				
			}

			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);	
	
			for (Crm c :(List<Crm>) qGroup.getResultList()) {

				Paragraph pResum = new Paragraph();
				
				if(selectFiltro(resumo).equals("cliente.nome_razao")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);	
					
				}else if(selectFiltro(resumo).equals("cliente.contato")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);		
				
				}else if(selectFiltro(resumo).equals("crm_assuntos.nome")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					
				}else if(selectFiltro(resumo).equals("contato_feedback.nome")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					
				}else if(selectFiltro(resumo).equals("setor.nome")){
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
			
//			Paragraph pTotalPgt = new Paragraph(Real.formatDbToString(String.valueOf(totalPago))	,fCaptionsBold);
//			pTotalPgt.setAlignment(Element.ALIGN_RIGHT);
			
//			PdfPCell pCellTotalPgt = new PdfPCell();
//			pCellTotalPgt.setBorderWidth(0);
//			pCellTotalPgt.addElement(pTotalPgt);
			
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
//			
//			PdfPTable tbTotalPgt = new PdfPTable(new float[]{0.55f,0.08f,0.10f,1f});
//			tbTotalPgt.setWidthPercentage(100f);	
//			tbTotalPgt.addCell(pCellTotal);
//			tbTotalPgt.addCell(pCellTotalVl);
//			tbTotalPgt.addCell(pCellTotalPgt);
//			tbTotalPgt.addCell(pCellTotalVazio);
//			tbTotalPgt.setSpacingBefore(10);

			doc.add(tbTotal);
			
		} finally {
			if (doc.isOpen() && doc != null) {
				doc.close();
			}
		}

	}
	
	public String selectHeader(String s) {
	
		String filtro = "";
		if(s.equals("cliente.nome_razao")){
			filtro = "CLIENTE";							
		}else if(s.equals("id")){
			filtro = "PROTOCOLO";					
		}else if(s.equals("setor.nome")){
			filtro = "SETOR";					
		}else if(s.equals("data_cadastro")){
			filtro = "ABERTURA";					
		}else if(s.equals("hora_agendado")){
			filtro = "HORA";					
		}else if(s.equals("cliente.contato")){
			filtro = "CONTATO";					
		}else if(s.equals("status")){
			filtro = "STATUS";					
		}else if(s.equals("crm_assuntos.nome")){
			filtro = "ASSUNTO";							
		}else if(s.equals("operador_tratamento")){
			filtro = "OPERADOR";					
		}else if(s.equals("data_agendado")){
			filtro = "PREVISÃO";							
		}else if(s.equals("tempo_atendimento")){
			filtro = "T. TRAT.";							
		}else if(s.equals("motivo_reagendamento")){
			filtro = "REAGENDAMENTO";					
		}else if(s.equals("data_efetuado")){
			filtro = "CONCLUSÃO";					
		}else if(s.equals("conteudo")){
			filtro = "CONTEUDO";					
		}else if(s.equals("contato_feedback.nome")){
			filtro = "FEEDBACK";					
		}else if(s.equals("crm_formas_contato.nome")){
			filtro = "FORMA CONTATO";					
		}else if(s.equals("contrato.id")){
			filtro = "CONTRATO";					
		}else if(s.equals("plano")){
			filtro = "PLANO";					
		}else if(s.equals("concentrador")){
			filtro = "CONCENTRADOR";					
		}

		return filtro;
	}
	
	public String selectUpHeader(String s) {
		
		String filtro = "";
//		if(s.equals("cliente.nome_razao")){
//			filtro = "CLIENTE";							
//		}else if(s.equals("id")){
//			filtro = "CÓDIGO";					
//		}else if(s.equals("setor.nome")){
//			filtro = "SETOR";					
//		}else if(s.equals("data_agendado")){
//			filtro = "DATA DE AGENDAMENTO";					
//		}else if(s.equals("hora_agendado")){
//			filtro = "HORA DE AGENDAMENTO";					
//		}else if(s.equals("cliente.contato")){
//			filtro = "CONTATO";					
//		}else if(s.equals("status")){
//			filtro = "STATUS";					
//		}else if(s.equals("crm_assuntos.nome")){
//			filtro = "CRM ASSUNTOS";					
//		
//		}else if(s.equals("operador_tratamento")){
//			filtro = "OPEREDOR TRATAMENTO";					
//		}else if(s.equals("data_inicio_tratamento")){
//			filtro = "DATA INICIO TRATAMENTO";					
//		}else if(s.equals("tempo_total_tratamento")){
//			filtro = "TEMPO TOTAL TRATAMENTO";					
//		}else if(s.equals("motivo_reagendamento")){
//			filtro = "MOTIVO REAGENDAR";					
//		}
		if(s.equals("cliente.nome_razao")){
			filtro = "CLIENTE";							
		}else if(s.equals("id")){
			filtro = "PROTOCOLO";					
		}else if(s.equals("setor.nome")){
			filtro = "SETOR";					
		}else if(s.equals("data_cadastro")){
			filtro = "ABERTURA";					
		}else if(s.equals("hora_agendado")){
			filtro = "HORA";					
		}else if(s.equals("cliente.contato")){
			filtro = "CONTATO";					
		}else if(s.equals("status")){
			filtro = "STATUS";					
		}else if(s.equals("crm_assuntos.nome")){
			filtro = "ASSUNTO";							
		}else if(s.equals("operador_tratamento")){
			filtro = "OPERADOR";					
		}else if(s.equals("data_agendado")){
			filtro = "PREVISÃO";							
		}else if(s.equals("data_efetuado")){
			filtro = "CONCLUSÃO";							
		}else if(s.equals("tempo_atendimento")){
			filtro = "T. TRAT.";							
		}else if(s.equals("motivo_reagendamento")){
			filtro = "REAGENDAMENTO";					
		}else if(s.equals("data_efetuado")){
			filtro = "CONCLUSÃO";					
		}else if(s.equals("conteudo")){
			filtro = "CONTEUDO";					
		}else if(s.equals("contato_feedback.nome")){
			filtro = "FEEDBACK";					
		}else if(s.equals("crm_formas_contato.nome")){
			filtro = "FORMA CONTATO";					
		}else if(s.equals("contrato.id")){
			filtro = "CONTRATO";					
		}else if(s.equals("plano")){
			filtro = "PLANO";					
		}else if(s.equals("concentrador")){
			filtro = "CONCENTRADOR";					
		}
		return filtro;
	}

	public String selectFiltro(String s) {
		
		String filtro = "";		
		if(s.equals("Cliente")){
			filtro = "cliente.nome_razao";							
		}else if(s.equals("Código")){
			filtro = "id";					
		}else if(s.equals("Setor")){
			filtro = "setor.nome";					
		}else if(s.equals("Data Cadastro")){
			filtro = "data_cadastro";					
		}else if(s.equals("Data Agendado")){
			filtro = "data_agendado";					
		}else if(s.equals("Data Conclusão")){
			filtro = "data_efetuado";					
		}else if(s.equals("Hora Agendado")){
			filtro = "hora_agendado";					
		}else if(s.equals("Contato")){
			filtro = "cliente.contato";					
		}else if(s.equals("Status")){
			filtro = "status";					
		}else if(s.equals("Crm Assuntos")){
			filtro = "crm_assuntos.nome";				
		}else if(s.equals("Operador Tratamento")){
			filtro = "operador_tratamento";					
		}else if(s.equals("Data Inicio Tratamento")){
			filtro = "data_inicio_tratamento";					
		}else if(s.equals("Tempo Total Tratamento")){
			filtro = "tempo_total_tratamento";					
		}else if(s.equals("Motivo Reagendamento")){
			filtro = "motivo_reagendamento";					
		}else if(s.equals("Data Abertura")){
			filtro = "data_cadastro";
		}else if(s.equals("Contrato")){
			filtro = "contrato.id";
		}else if(s.equals("Plano")){
			filtro = "plano";
		}else if(s.equals("Concentrador")){
			filtro = "concentrador";
		}else if(s.equals("Conteudo")){
			filtro = "conteudo";
		}						
		
//		if(s.equals("CLIENTE")){
//			filtro = "cliente.nome_razao";							
//		}else if(s.equals("PROTOCOLO")){
//			filtro = "id";					
//		}else if(s.equals("SETOR")){
//			filtro = "setor.nome";					
//		}else if(s.equals("ABERTURA")){
//			filtro = "data_cadastro";					
//		}else if(s.equals("HORA")){
//			filtro = "hora_agendado";					
//		}else if(s.equals("CONTATO")){
//			filtro = "cliente.contato";					
//		}else if(s.equals("STATUS")){
//			filtro = "status";					
//		}else if(s.equals("ASSUNTO")){
//			filtro = "crm_assuntos.nome";							
//		}else if(s.equals("operador_tratamento")){
//			filtro = "OPERADOR";					
//		}else if(s.equals("PREVISÃO")){
//			filtro = "data_agendado";							
//		}else if(s.equals("TEMPO TRATAMENTO")){
//			filtro = "tempo_total_tratamento";							
//		}else if(s.equals("REAGENDAMENTO")){
//			filtro = "motivo_reagendamento";					
//		}else if(s.equals("CONCLUSÃO")){
//			filtro = "data_efetuado";					
//		}
		
		
		
		
		
		return filtro;
	}	

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}
