package com.digital.opuserp.view.modulos.relatorio.ContasPagar;

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

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasDAO;
import com.digital.opuserp.domain.Contas;
import com.digital.opuserp.domain.ContasPagar;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Fornecedor;
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

public class ExportContasPagar implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportContasPagar(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista, List<Object> columns)throws Exception {

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
		CriteriaQuery<ContasPagar> criteriaQuery = cb.createQuery(ContasPagar.class);
		Root<ContasPagar> rootContas = criteriaQuery.from(ContasPagar.class);
		EntityType<ContasPagar> type = em.getMetamodel().entity(ContasPagar.class);

			List<Predicate> criteria = new ArrayList<Predicate>();

			criteria.add(cb.equal(rootContas.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

			if (lista.size() > 0) {
				for (SearchParameters s : lista) {

					if (s.getOperador().equals("IGUAL")) {
						
						if (s.getCampo().equals("fornecedor.razao_social")) {
							criteria.add(cb.like(cb.lower(rootContas.get("fornecedor").<String>get("razao_social")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("conta.descricao")) {
														
								String ct[] = s.getValor().split("-");							
								String valor = ct[1].toString();

//							criteria.add(cb.like(cb.lower(rootContas.get("conta").<String>get("descricao")), "%" + valor.toLowerCase()+ "%"));

							  criteria.add(cb.like(cb.lower(rootContas.get("conta").<String>get("descricao")), valor.toLowerCase()));
						}					
	
						if (s.getCampo().equals("forma_pgto")) {
							
							String ct[] = s.getValor().split("-");							
							String valor = ct[0].toString();

							criteria.add(cb.like(cb.lower(rootContas.<String>get(s.getCampo())), valor.toLowerCase()));
						}	
						
						if (!s.getCampo().equals("fornecedor.razao_social") && !s.getCampo().equals("conta.descricao") && !s.getCampo().equals("forma_pgto")) {
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.equal(rootContas.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.like(cb.lower(rootContas.<String>get(s.getCampo())), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.equal(rootContas.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							if(rootContas.get(s.getCampo()).getJavaType().equals(Double.class)){
								criteria.add(cb.equal(rootContas.<Double>get(s.getCampo()), s.getValor().toLowerCase()));
							}
											
							
						}
	
					}else if (s.getOperador().equals("DIFERENTE")) {
						if (s.getCampo().equals("fornecedor.razao_social")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("fornecedor").<String>get("razao_social")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("conta.descricao")) {
							
							String ct[] = s.getValor().split("-");							
							String valor = ct[1].toString();

							criteria.add(cb.notLike(cb.lower(rootContas.get("conta").<String>get("descricao")), valor.toLowerCase()));
//							criteria.add(cb.notLike(cb.lower(rootContas.get("conta").<String>get("descricao")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("forma_pgto")) {
							
							String ct[] = s.getValor().split("-");							
							String valor = ct[0].toString();

							criteria.add(cb.notLike(cb.lower(rootContas.<String>get(s.getCampo())), valor.toLowerCase()));
						}	
						
						if (!s.getCampo().equals("fornecedor.razao_social") && !s.getCampo().equals("conta.descricao") && !s.getCampo().equals("forma_pgto")) {												
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.notEqual(rootContas.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.notLike(cb.lower(rootContas.<String> get(s.getCampo())), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.notEqual(rootContas.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							if(rootContas.get(s.getCampo()).getJavaType().equals(Double.class)){
								criteria.add(cb.notEqual(rootContas.<Double>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							
						}
	
					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("fornecedor.razao_social")) {
							criteria.add(cb.like(cb.lower(rootContas.get("fornecedor").<String>get("razao_social")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("conta.descricao")) {
							
							String ct[] = s.getValor().split("-");							
							String valor = ct[1].toString();

							criteria.add(cb.like(cb.lower(rootContas.get("conta").<String>get("descricao")), "%" + valor.toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("forma_pgto")) {
							
							String ct[] = s.getValor().split("-");							
							String valor = ct[0].toString();

							criteria.add(cb.like(cb.lower(rootContas.<String> get(s.getCampo())),"%" +valor.toLowerCase() + "%"));							
						}
						
						if (!s.getCampo().equals("fornecedor.razao_social") && !s.getCampo().equals("conta.descricao") && !s.getCampo().equals("forma_pgto")) {												
													
							criteria.add(cb.like(cb.lower(rootContas.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						
						if (s.getCampo().equals("fornecedor.razao_social")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("fornecedor").<String>get("razao_social")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("conta.descricao")) {
							
							String ct[] = s.getValor().split("-");							
							String valor = ct[1].toString();
							
							criteria.add(cb.notLike(cb.lower(rootContas.get("conta").<String>get("descricao")),"%" + valor.toLowerCase() + "%"));
							
//							criteria.add(cb.notLike(cb.lower(rootContas.get("conta").<String>get("descricao")), "%" + s.getValor().toLowerCase()+ "%"));
						}
	
						if (s.getCampo().equals("forma_pgto")) {
							
							String ct[] = s.getValor().split("-");							
							String valor = ct[0].toString();

							criteria.add(cb.notLike(cb.lower(rootContas.<String> get(s.getCampo())),"%" + valor.toLowerCase() + "%"));
						}
						
						if (!s.getCampo().equals("fornecedor.razao_social") && !s.getCampo().equals("conta.descricao") && !s.getCampo().equals("forma_pgto")) {												
														
							criteria.add(cb.notLike(cb.lower(rootContas.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
					} else if (s.getOperador().equals("MAIOR QUE")) {
						
						try{						
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.greaterThanOrEqualTo(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							if(rootContas.get(s.getCampo()).getJavaType().equals(Double.class)){
								criteria.add(cb.greaterThanOrEqualTo(rootContas.<Double> get(s.getCampo()), Double.valueOf(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){								
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.greaterThanOrEqualTo(rootContas.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
						
					} else if (s.getOperador().equals("MENOR QUE")) {
						
						
						
						try{						
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.lessThanOrEqualTo(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Double.class)){
								criteria.add(cb.lessThanOrEqualTo(rootContas.<Double> get(s.getCampo()), Double.valueOf(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.lessThanOrEqualTo(rootContas.<Date> get(s.getCampo()),  sdf.parse(s.getValor())));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
						
						
					} else if (s.getOperador().equals("MAIOR IGUAL QUE")) {
						
						try{						
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.greaterThanOrEqualTo(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							if(rootContas.get(s.getCampo()).getJavaType().equals(Double.class)){
								criteria.add(cb.greaterThanOrEqualTo(rootContas.<Double> get(s.getCampo()), Double.valueOf(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.greaterThanOrEqualTo(rootContas.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
						
					} else if (s.getOperador().equals("MENOR IGUAL QUE")) {
						
						try{						
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.lessThanOrEqualTo(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							if(rootContas.get(s.getCampo()).getJavaType().equals(Double.class)){
								criteria.add(cb.lessThanOrEqualTo(rootContas.<Double> get(s.getCampo()), Double.valueOf(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.lessThanOrEqualTo(rootContas.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
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
				criteriaQuery.orderBy(cb.asc(rootContas.join("fornecedor").get("razao_social")));				
			}else if(selectFiltro(order).equals("conta.descricao")){
				criteriaQuery.orderBy(cb.asc(rootContas.join("conta").get("descricao")));					
			}else{
				criteriaQuery.orderBy(cb.asc(rootContas.get(selectFiltro(order))));				
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
			Paragraph pExport = new Paragraph("RELATÓRIO CONTAS À PAGAR",fTitulo);
			pExport.setAlignment(Element.ALIGN_CENTER);
			pExport.setSpacingAfter(10);
			doc.add(pExport);
			
			//FiILTROS					
			StringBuilder SbTipo = new StringBuilder();
			StringBuilder SbOperad =  new StringBuilder();
			StringBuilder SbValor = new StringBuilder();
			
			List<ContasPagar> contas = q.getResultList();
			
			
			if (lista.size() > 0) {
				
				for (SearchParameters s : lista) {	
					Contas ct = null;
					if(s.getCampo().equals("forma_pgto")){
						
						String conta[] = s.getValor().split("-");
						
						ct = ContasDAO.findCod_Ref(conta[0].toString());
						
						SbTipo.append(selectHeader(s.getCampo())+"\n");
						SbOperad.append(s.getOperador()+"\n");
						SbValor.append(ct.getcod_cta_ref()+"-"+ct.getDescricao()+"\n");				
					
					}else if(s.getCampo().equals("conta.descricao")){
						
						for(ContasPagar c: contas){
							
							ct = ContasDAO.find(c.getConta().getId());
						}
							
								
								SbTipo.append(selectHeader(s.getCampo())+"\n");
								SbOperad.append(s.getOperador()+"\n");
								SbValor.append(ct.getcod_cta_ref()+"-"+ct.getDescricao()+"\n");				
						
						
					
					}else{
						SbTipo.append(selectHeader(s.getCampo())+"\n");
						SbOperad.append(s.getOperador()+"\n");
						SbValor.append(s.getValor()+"\n");
					}
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

//			List<ContasPagar> contas = q.getResultList();

			
			float[] f = new float[columns.size()+1];
			
			Integer i=0;
			for (Object c : columns) {
				
			    if(selectHeader(c.toString()).equals("CÓD.")){
			    	f[i] = (0.06f);	
			    }
			    if(selectHeader(c.toString()).equals("N.DOC")){
			    	f[i] = (0.12f);	
			    }
			    if(selectHeader(c.toString()).equals("VALOR R$")){
			    	f[i] = (0.11f);	
			    }
			    if(selectHeader(c.toString()).equals("PGTO R$")){
			    	f[i] = (0.10f);	
			    }
			    if(selectHeader(c.toString()).equals("VENC.")){
			    	f[i] = (0.11f);	
			    }
			    if(selectHeader(c.toString()).equals("PGTO")){
			    	f[i] = (0.11f);	
			    }
			    if(selectHeader(c.toString()).equals("C.MOVIMENTO")){
			    	f[i] = (0.25f);	
			    }                                       
			    if(selectHeader(c.toString()).equals("STATUS")){
			    	f[i] = (0.15f);	
			    }
			    if(selectHeader(c.toString()).equals("CADASTRO")){
			    	f[i] = (0.12f);	
			    }
			    if(selectHeader(c.toString()).equals("TIPO")){
			    	f[i] = (0.11f);	
			    }
			    if(selectHeader(c.toString()).equals("TERMO")){
			    	f[i] = (0.12f);	
			    }
			    if(selectHeader(c.toString()).equals("PARCELA")){
			    	f[i] = (0.11f);	
			    }									  
			    if(selectHeader(c.toString()).equals("DESCRIÇÃO")){
			    	f[i] = (0.50f);	
			    }
			    if(selectHeader(c.toString()).equals("FORNECEDOR")){
			    	f[i] = (0.65f);	
			    }
			    if(selectHeader(c.toString()).equals("CONTA")){
			    	f[i] = (0.25f);	
			    }			    
			    i++;
     		  }					
			f[i] = (0.05f);	
			double despesa = 0;			
			double custo = 0;
			double variavel = 0;
			double fixa = 0;
			double banco = 0;
			double cheque = 0;			
			double cartCredito = 0;			
			double cartDebito = 0;			
			double totalPago = 0;			
			double totalAserPago = 0;			
			Integer reg= 0;
			Integer cont = 0;
			PdfPTable tbConteudo2 = new PdfPTable(f);
			
			
			Contas ct = new Contas();
			
			if(tipo.equals("MULTI COLUNA")){
				
				PdfPTable tbTopo = new PdfPTable(f);
				tbTopo.setWidthPercentage(100f);				
				
				for (Object c : columns) {
					cont++;
					
					PdfPCell pCell = new PdfPCell(new Phrase(selectHeader(c.toString()), fCampoBold));
					pCell.setPaddingTop(2);
					pCell.setPaddingBottom(4);
					pCell.setBackgroundColor(new BaseColor(114, 131, 151));
					pCell.setBorderColor(new BaseColor(255, 255, 255));	
					pCell.setBorderWidth(1.5f);
					tbTopo.addCell(pCell);
					
					if(cont==columns.size()){
						PdfPCell pCell1 = new PdfPCell(new Phrase("ATZ", fCampoBold));
						pCell1.setPaddingTop(2);
						pCell1.setPaddingBottom(4);
						pCell1.setBackgroundColor(new BaseColor(114, 131, 151));
						pCell1.setBorderColor(new BaseColor(255, 255, 255));	
						pCell.setBorderWidth(1.5f);
						tbTopo.addCell(pCell1);						
					}
				}
				
				doc.add(tbTopo);				
				
				
				for (ContasPagar cliente : contas) {
	
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(5f);
					
					PdfPTable tbConteudo = new PdfPTable(f);
					tbConteudo.setWidthPercentage(100f);
									
					
					
					if(cliente.getValor_titulo()!=null && !cliente.getValor_titulo().equals("")){
						totalAserPago = totalAserPago + cliente.getValor_titulo();						
					}
					
					if(cliente.getValor_pagamento() != null && !cliente.getValor_pagamento().equals("")){
		
						totalPago = totalPago + cliente.getValor_pagamento();
						
						if(cliente.getForma_pgto()!=null && !cliente.getForma_pgto().equals("")){
								if(cliente.getTermo().equals("DESPESA")){
									despesa = despesa + cliente.getValor_pagamento();
								}
								if(cliente.getTermo().equals("CUSTO")){
									custo = custo + cliente.getValor_pagamento();
								}
								if(cliente.getTipo().equals("VARIAVEL")){
									variavel = variavel + cliente.getValor_pagamento();
								}
								if(cliente.getTipo().equals("FIXA")){
									fixa = fixa + cliente.getValor_pagamento();
								}
								
						}
					}
					
					PdfPCell pCell3 = new PdfPCell();
					pCell3.setPaddingTop(0);
					pCell3.setPaddingBottom(4);
					pCell3.setBackgroundColor(new BaseColor(232, 235, 237));
					pCell3.setBorderColor(new BaseColor(255, 255, 255));	
					pCell3.setBorderWidth(1.5f);
					
					
						Integer dias= 0;
						Paragraph valorColuna2 = null;
							
							if(cliente.getStatus().equals("PAGO")){
								
								 DateTime dt1 = new DateTime(cliente.getData_vencimento());
								 DateTime dt2 = new DateTime(cliente.getData_pago());
								 dias = Days.daysBetween(dt1, dt2).getDays();
								 valorColuna2 = new Paragraph(String.valueOf(dias),fCampo);
								 valorColuna2.setAlignment(Element.ALIGN_CENTER);
								 
							}else if(cliente.getStatus().equals("A PAGAR") && cliente.getData_vencimento().before(new Date())){
								
								DateTime dt1 = new DateTime(cliente.getData_vencimento());
								DateTime dt2 = new DateTime(new DateTime());
								dias = Days.daysBetween(dt1, dt2).getDays();								
								valorColuna2 = new Paragraph(String.valueOf(dias),fCampo);
								valorColuna2.setAlignment(Element.ALIGN_CENTER);
								
							}else if(cliente.getStatus().equals("A PAGAR") && cliente.getData_vencimento().after(new Date())){								
								valorColuna2 = new Paragraph("0",fCampo);													
								valorColuna2.setAlignment(Element.ALIGN_CENTER);
							}else{
								valorColuna2 = new Paragraph(" ",fCampo);																					
							}
							
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
							
							metodo = metodo.replace(".descricao", "");
							metodo = metodo.replace(".razao_social", "");

							Class cls = cliente.getClass();
							
//							Method method = null;
//							if(!c.toString().equals("ATZ")){
							Method method = cls.getMethod(metodo);
								
								if ((method.invoke(cliente) instanceof Double || method.invoke(cliente) instanceof String || method.invoke(cliente) instanceof Integer || method.invoke(cliente) instanceof Date)) {
									
									String valor = method.invoke(cliente).toString();
									
									Paragraph valorColuna = null;
									
									if(c.toString().equals("valor_pagamento")||c.toString().equals("valor_titulo")){
										valorColuna = new Paragraph(Real.formatDbToString(String.valueOf(valor)),fCampo);
										valorColuna.setAlignment(Element.ALIGN_RIGHT);
										
									}else if(c.toString().equals("data_cadastro")||c.toString().equals("data_vencimento")||c.toString().equals("data_pago")){
										
										String dateform = dtUtil.parseDataBra(valor);
										valorColuna = new Paragraph(dateform,fCampo);									
									}else if(c.toString().equals("forma_pgto")){										
											ct = ContasDAO.findCod_Ref(valor);
											if(ct != null){
												valor = ct.getDescricao();											
												valorColuna = new Paragraph(valor,fCampo);
											}else{
												valorColuna = new Paragraph(valor,fCampo);																					
											}								
									}else{
										valorColuna = new Paragraph(valor,fCampo);
									}
									
									pCell.addElement(valorColuna);		
						
									
								} else if (method.invoke(cliente) instanceof Fornecedor) {
									
									Fornecedor cat = (Fornecedor) method.invoke(cliente);
									
									String valorColuna = "";
									
									if(c.toString().equals("fornecedor.razao_social")){
										valorColuna = cat.getRazao_social();
									}										
									
									pCell.addElement(new Phrase(valorColuna, fCampo));
									
								}else if(method.invoke(cliente) instanceof Contas){
									
									Contas cat = (Contas)method.invoke(cliente);
								
									String valorColuna = "";
									
									if(c.toString().equals("conta.descricao")){
										valorColuna = cat.getDescricao();
									}	
									
									pCell.addElement(new Phrase(valorColuna, fCampo));
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
				
				columns.add("ATZ");
				for (Object c : columns) {
					reg++;
					
					Paragraph TotalPago = null;
					PdfPCell pCell2 = new PdfPCell();
					
					if(c.toString().equals("valor_pagamento")){
						TotalPago = new Paragraph(Real.formatDbToString(String.valueOf(totalPago)),fCampo);
						TotalPago.setAlignment(Element.ALIGN_RIGHT);													
						pCell2.setBackgroundColor(new BaseColor(232, 235, 237));						
					}else if(c.toString().equals("valor_titulo")){
						TotalPago = new Paragraph(Real.formatDbToString(String.valueOf(totalAserPago)),fCampo);
						TotalPago.setAlignment(Element.ALIGN_RIGHT);													
						pCell2.setBackgroundColor(new BaseColor(232, 235, 237));						
					}else{
						TotalPago = new Paragraph(" ",fCampo);				
					}
					
					pCell2.setPaddingTop(0);
					pCell2.setPaddingBottom(4);
					pCell2.setBorderColor(new BaseColor(255, 255, 255));	
					pCell2.setBorderWidth(1.5f);
					pCell2.addElement(TotalPago);
					
//					PdfPTable tbConteudo2 = new PdfPTable(16);
					tbConteudo2.setWidthPercentage(100f);					
					tbConteudo2.addCell(pCell2);											
					if(reg==columns.size()){
						doc.add(tbConteudo2);	
					}
				}
			}

			
			
			if(tipo.equals("COLUNA ÚNICA")){
				for (ContasPagar conta : contas) {
	
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(25f);
	
					
					if(conta.getValor_pagamento() != null && !conta.getValor_pagamento().equals("")){
						
						totalPago = totalPago + Real.formatStringToDBDouble(conta.getValor_pagamento().toString());
					
						if(conta.getForma_pgto()!=null && !conta.getForma_pgto().equals("")){
							
							if(conta.getTermo().equals("DESPESA")){
								despesa = despesa + conta.getValor_pagamento();
							}
							if(conta.getTermo().equals("CUSTO")){
								custo = custo + conta.getValor_pagamento();
							}
							if(conta.getTipo().equals("VARIAVEL")){
								variavel = variavel + conta.getValor_pagamento();
							}
							if(conta.getTipo().equals("FIXA")){
								fixa = fixa + conta.getValor_pagamento();
							}
							
					}
						
					}

					
					for (Object c : columns) {
						try {
							PdfPCell pCell = new PdfPCell();
							pCell.setPaddingBottom(5);
							pCell.setPaddingTop(0);
							pCell.addElement(new Phrase(selectHeader(c.toString()), fCampo));
							
							String metodo = "get"
									+ c.toString().substring(0, 1).toUpperCase()
									+ c.toString().substring(1,c.toString().length());
							
							metodo = metodo.replace(".descricao", "");
							metodo = metodo.replace(".razao_social", "");
							
							Class cls = conta.getClass();
							Method method = cls.getMethod(metodo);
	
							if(method.invoke(conta)==null||method.invoke(conta).toString().equals("")){
								pCell.addElement(new Phrase(" ", fConteudo));		
							}	
														
							if (method.invoke(conta) instanceof String || method.invoke(conta) instanceof Integer || method.invoke(conta) instanceof Date) {
								String valor = method.invoke(conta).toString();
																			
								Paragraph valorColuna = null;
								
								if(c.toString().equals("valor_pagamento")||c.toString().equals("valor_titulo")){
									valorColuna = new Paragraph(Real.formatDbToString(String.valueOf(valor)),fCampo);
//									valorColuna.setAlignment(Element.ALIGN_RIGHT);
									
								}else if(c.toString().equals("data_cadastro")||c.toString().equals("data_vencimento")||c.toString().equals("data_pago")){
									
									String dateform = dtUtil.parseDataBra(valor);
									valorColuna = new Paragraph(dateform,fCampo);									
								}else if(c.toString().equals("forma_pgto")){										
										ct = ContasDAO.findCod_Ref(valor);
										if(ct != null){
											valor = ct.getDescricao();											
											valorColuna = new Paragraph(valor,fCampo);
										}else{
											valorColuna = new Paragraph(valor,fCampo);																					
										}								
								}else{
									valorColuna = new Paragraph(valor,fCampo);
								}
								
								
								pCell.addElement(valorColuna);

//								pCell.addElement(new Phrase(valor, fConteudo));									
							
							} else if (method.invoke(conta) instanceof Fornecedor) {
								
								Fornecedor cat = (Fornecedor) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("fornecedor.razao_social")){
									valorColuna = cat.getRazao_social();
								}										
								
								pCell.addElement(new Phrase(valorColuna, fCampo));
								
							}else if(method.invoke(conta) instanceof Contas){
								
								Contas cat = (Contas)method.invoke(conta);
							
								String valorColuna = "";
								
								if(c.toString().equals("conta.descricao")){
									valorColuna = cat.getDescricao();
								}	
								
								pCell.addElement(new Phrase(valorColuna, fCampo));
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
			
			
			CriteriaQuery<ContasPagar> criteriaQueryGroup = cb.createQuery(ContasPagar.class);
			Root<ContasPagar> rootGroup = criteriaQueryGroup.from(ContasPagar.class);
			
			
			if (selectFiltro(resumo).equals("fornecedor.razao_social")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("fornecedor").get("razao_social");
				
				criteriaQueryGroup.groupBy(rootGroup.join("fornecedor").get("razao_social"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(ContasPagar.class,coluna, qtd));
			}
											
			if (selectFiltro(resumo).equals("conta.descricao")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("conta").get("descricao");
				
				criteriaQueryGroup.groupBy(rootGroup.join("conta").get("descricao"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(ContasPagar.class,coluna, qtd));
			}

			

			if (!selectFiltro(resumo).equals("fornecedor.razao_social") && !selectFiltro(resumo).equals("conta.descricao")) {
				
				
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
				criteriaQueryGroup.select(cb.construct(ContasPagar.class,coluna, qtd));
				
			}

			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);				
			
			for (ContasPagar c :(List<ContasPagar>) qGroup.getResultList()) {

				Paragraph pResum = new Paragraph();
				
				if(selectFiltro(resumo).equals("fornecedor.razao_social")||selectFiltro(resumo).equals("conta.descricao")){
				
						pResum = new Paragraph(c.getColuna(), fCaptionsBold);		
					

				}else{
				
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(String.class)){
						
						if(selectFiltro(resumo).equals("forma_pgto")){										
							ct = ContasDAO.findCod_Ref(c.getColuna());
							if(ct != null){															
								pResum = new Paragraph(ct.getDescricao(), fCaptionsBold);
							}else{
								pResum = new Paragraph(c.getColuna(), fCaptionsBold);																					
							}
						}else{
							pResum = new Paragraph(c.getColuna(), fCaptionsBold);									
						}
	
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
					
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Double.class)){
						pResum = new Paragraph(Real.formatDbToString(String.valueOf(c.getColuna_Double())), fCaptionsBold);			
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
					if(c.getColuna().equals("DESPESA")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(despesa)),fCaptionsBold);
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("CUSTO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(custo)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("VARIAVEL")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(variavel)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("FIXA")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(fixa)),fCaptionsBold);					
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
		
				if(selectFiltro(resumo).equals("tipo")||selectFiltro(resumo).equals("termo")){
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
			
			if(selectFiltro(resumo).equals("tipo")||selectFiltro(resumo).equals("termo")){
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
	
	public String selectHeader(String s) {
	
		String filtro = "";
		if(s.equals("id")){
			filtro = "CÓD.";			
		}else if(s.equals("n_doc")){
			filtro = "N.DOC";					
		}else if(s.equals("valor_titulo")){
			filtro = "VALOR R$";	
		}else if(s.equals("valor_pagamento")){
			filtro = "PGTO R$";					
		}else if(s.equals("data_vencimento")){
			filtro = "VENC.";			
		}else if(s.equals("data_pago")){
			filtro = "PGTO";							
		}else if(s.equals("forma_pgto")){
			filtro = "C.MOVIMENTO";										
		}else if(s.equals("status")){
			filtro = "STATUS";					
		}else if(s.equals("tipo")){
			filtro = "TIPO";
		}else if(s.equals("data_cadastro")){
			filtro = "CADASTRO";										
		}else if(s.equals("parcela")){
			filtro = "PARCELA";										
		}else if(s.equals("descricao")){
			filtro = "DESCRIÇÃO";										
		}else if(s.equals("termo")){
			filtro = "TERMO";										
		}else if(s.equals("conta.descricao")){
			filtro = "CONTA";										
		}else if(s.equals("fornecedor.razao_social")){
			filtro = "FORNECEDOR";										
		}

		return filtro;
	}
	
	public String selectUpHeader(String s) {
		
		String filtro = "";
		if(s.equals("id")){
			filtro = "CÓDIGO";			
		}else if(s.equals("n_doc")){
			filtro = "NÚMERO DO DOCUMENTO";					
		}else if(s.equals("valor_titulo")){
			filtro = "VALOR TÍTULO";			
		}else if(s.equals("valor_pagamento")){
			filtro = "VALOR PAGO";					
		}else if(s.equals("data_vencimento")){
			filtro = "DATA DE VENCIMENTO";			
		}else if(s.equals("data_pago")){
			filtro = "DATA DE PAGAMENTO";							
		}else if(s.equals("forma_pgto")){
			filtro = "CONTA MOVIMENTO";										
		}else if(s.equals("status")){
			filtro = "STATUS";					
		}else if(s.equals("tipo")){
			filtro = "TIPO";											
		}else if(s.equals("data_cadastro")){
			filtro = "DATA DE CADRASTRO";										
		}else if(s.equals("parcela")){
			filtro = "PARCELA";										
		}else if(s.equals("descricao")){
			filtro = "DESCRIÇÃO";										
		}else if(s.equals("termo")){
			filtro = "TERMO";										
		}else if(s.equals("conta.descricao")){
			filtro = "CONTA";										
		}else if(s.equals("fornecedor.razao_social")){
			filtro = "FORNECEDOR";										
		}
		
		return filtro;
	}
	

	
	
	public String selectFiltro(String s) {
		
		String filtro = "";
		if(s.equals("Código")){
			filtro = "id";			
		}else if(s.equals("Número do Documento")){
			filtro = "n_doc";					
		}else if(s.equals("Valor Título")){
			filtro = "valor_titulo";			
		}else if(s.equals("Valor Pago")){
			filtro = "valor_pagamento";					
		}else if(s.equals("Data de Vencimento")){
			filtro = "data_vencimento";			
		}else if(s.equals("Data de Pagamento")){
			filtro = "data_pago";							
		}else if(s.equals("Conta Movimento")){
			filtro = "forma_pgto";										
		}else if(s.equals("Status")){
			filtro = "status";					
		}else if(s.equals("Tipo")){
			filtro = "tipo";											
		}else if(s.equals("Data de Cadastro")){
			filtro = "data_cadastro";										
		}else if(s.equals("Parcela")){
			filtro = "parcela";										
		}else if(s.equals("Descrição")){
			filtro = "descricao";										
		}else if(s.equals("Termo")){
			filtro = "termo";										
		}else if(s.equals("Conta")){
			filtro = "conta.descricao";										
		}else if(s.equals("Fornecedor")){
			filtro = "fornecedor.razao_social";										
		}
				
		return filtro;
	}	

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}
