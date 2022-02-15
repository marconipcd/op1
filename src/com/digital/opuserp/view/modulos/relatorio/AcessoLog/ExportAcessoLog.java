package com.digital.opuserp.view.modulos.relatorio.AcessoLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesContrato;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Usuario;
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

public class ExportAcessoLog implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportAcessoLog(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista, List<Object> columns)throws Exception {

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
			CriteriaQuery<AlteracoesContrato> criteriaQuery = cb.createQuery(AlteracoesContrato.class);
			Root<AlteracoesContrato> rootAcesso = criteriaQuery.from(AlteracoesContrato.class);
			EntityType<AlteracoesContrato> type = em.getMetamodel().entity(AlteracoesContrato.class);

			List<Predicate> criteria = new ArrayList<Predicate>();

			//criteria.add(cb.equal(rootAcesso.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

			if (lista.size() > 0) {
				for (SearchParameters s : lista) {

					if (s.getOperador().equals("IGUAL")) {
						
						if (s.getCampo().equals("operador.username")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("operador").<String>get("username")), s.getValor().toLowerCase()));
						}		
						
						if (!s.getCampo().equals("operador.username")) {
							criteria.add(cb.like(cb.lower(rootAcesso.<String>get(s.getCampo())), s.getValor().toLowerCase()));
						}

					}else if (s.getOperador().equals("DIFERENTE")) {
						
						if (s.getCampo().equals("operador.username")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("operador").<String>get("username")), s.getValor().toLowerCase()));
						}
						
						if (!s.getCampo().equals("operador.username")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.<String>get(s.getCampo())), s.getValor().toLowerCase()));
						}

					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("operador.username")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("operador").<String>get("username")),"%" + s.getValor().toLowerCase()+ "%"));							
						}
											
						if (!s.getCampo().equals("operador.username")) {
							criteria.add(cb.like(cb.lower(rootAcesso.<String>get(s.getCampo())), "%" +s.getValor().toLowerCase()+ "%"));
						}
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						
						if (s.getCampo().equals("operador.username")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("operador").<String>get("username")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (!s.getCampo().equals("operador.username")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.<String>get(s.getCampo())), s.getValor().toLowerCase()));
						}
						
					} else if (s.getOperador().equals("MAIOR QUE")) {
						
						try{						
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.greaterThan(rootAcesso.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Date.class)){
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.greaterThan(rootAcesso.<Date> get(s.getCampo()), dtValor));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
						
					} else if (s.getOperador().equals("MENOR QUE")) {
						
						try{						
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.lessThan(rootAcesso.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Date.class)){
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)+ date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.lessThan(rootAcesso.<Date> get(s.getCampo()), dtValor));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
					} else if (s.getOperador().equals("MAIOR IGUAL QUE")) {
						
						try{						
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.greaterThanOrEqualTo(rootAcesso.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Date.class)){
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)+ date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.greaterThanOrEqualTo(rootAcesso.<Date> get(s.getCampo()), dtValor));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
					} else if (s.getOperador().equals("MENOR IGUAL QUE")) {
						
						try{						
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.lessThanOrEqualTo(rootAcesso.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Date.class)){
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)	+ date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.lessThanOrEqualTo(rootAcesso.<Date> get(s.getCampo()), dtValor));
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

			
			if (selectCampo(order).equals("operador.username")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("operador").get("username")));
			}else{								
				criteriaQuery.orderBy(cb.asc(rootAcesso.get(selectCampo(order))));
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE ACESSO-LOG",fTitulo);
			pExport.setAlignment(Element.ALIGN_CENTER);
			pExport.setSpacingAfter(10);
			doc.add(pExport);
			
			//FiILTROS					
			StringBuilder SbTipo = new StringBuilder();
			StringBuilder SbOperad =  new StringBuilder();
			StringBuilder SbValor = new StringBuilder();
			
			if (lista.size() > 0) {
				for (SearchParameters s : lista) {	
	
					SbTipo.append(changeHeaderColumn(s.getCampo())+"\n");
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
			SbVl.append(tipo+"\n"+orientacao+"\n"+changeHeaderColumn(selectCampo(order))+"\n");

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

			List<AlteracoesContrato> acessos = q.getResultList();
				
			float[] f = new float[columns.size()];
			
			Integer i=0;
			for (Object c : columns) {
	
				if(changeHeaderColumn(c.toString()).equals("COD.")){
					f[i] = (0.10f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("TIPO")){
					f[i] = (0.55f);		
				}
				if(changeHeaderColumn(c.toString()).equals("OPERADOR")){
					f[i] = (0.20f);		
				}
				if(changeHeaderColumn(c.toString()).equals("DATA")){
					f[i] = (0.20f);		
				}
						   
			    i++;
     		 }					

			
			if(tipo.equals("MULTI COLUNA")){

				PdfPTable tbTopo = new PdfPTable(f);
				tbTopo.setWidthPercentage(100f);				
				
				for (Object c : columns) {
					PdfPCell pCell = new PdfPCell(new Phrase(changeHeaderColumn(c.toString()), fCampoBold));
					pCell.setPaddingTop(2);
					pCell.setPaddingBottom(4);
					pCell.setBackgroundColor(new BaseColor(114, 131, 151));
					pCell.setBorderColor(new BaseColor(255, 255, 255));	
					pCell.setBorderWidth(1.5f);
					tbTopo.addCell(pCell);
				}
				
				doc.add(tbTopo);				
				
				
				for (AlteracoesContrato acesso : acessos) {
	
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(5f);
		
					PdfPTable tbConteudo = new PdfPTable(f);
					tbConteudo.setWidthPercentage(100f);
				
					
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
							
							metodo = metodo.replace(".username", "");
							
							Class cls = acesso.getClass();
							Method method = cls.getMethod(metodo);
							
							String valor = null;
							
							if (method.invoke(acesso) instanceof String || method.invoke(acesso) instanceof Integer || method.invoke(acesso) instanceof Date) {
	
																	
								valor = method.invoke(acesso).toString();																								
								
								Paragraph valorColuna = null;
								
								 if(c.toString().equals("data_alteracao")){
									
									String dateform = dtUtil.parseDataBra(valor);
									valorColuna = new Paragraph(dateform,fCampo);									
								}else{
									valorColuna = new Paragraph(valor,fCampo);									
								}

								 pCell.addElement(valorColuna);			
								 
							
							} else if (method.invoke(acesso) instanceof Usuario) {
								Usuario cliente = (Usuario) method.invoke(acesso);
								
								if(c.toString().equals("operador.username")){
									pCell.addElement(new Phrase(cliente.getUsername(),fCampo));
								}
								
//								else if(c.toString().equals("data")){
//									pCell.addElement(new Phrase(cliente.getTelefone2(),fCampo));
//								}
								
							} 
							
							//tbConteudo.addCell(pTopo);
							tbConteudo.addCell(pCell);					
							
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show("ERRO!");
						}
					}
	
					doc.add(tbConteudo);
					
				}
			}
			
			if(tipo.equals("COLUNA ÚNICA")){
				for (AlteracoesContrato acesso : acessos) {
	
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(25f);
	
					for (Object c : columns) {
						try {
							PdfPCell pCell = new PdfPCell();
							pCell.setPaddingBottom(5);
							pCell.setPaddingTop(0);
							pCell.addElement(new Phrase(changeHeaderColumn(c.toString()), fCampo));
							
							String metodo = "get"
									+ c.toString().substring(0, 1).toUpperCase()
									+ c.toString().substring(1,c.toString().length());
							
							metodo = metodo.replace(".username", "");
									
							Class cls = acesso.getClass();
							Method method = cls.getMethod(metodo);
	
							if(method.invoke(acesso)==null||method.invoke(acesso).toString().equals("")){
								pCell.addElement(new Phrase(" ", fConteudo));		
							}	
										
							String valor = null;
							
							if (method.invoke(acesso) instanceof String || method.invoke(acesso) instanceof Integer || method.invoke(acesso) instanceof Date) {
																								
								valor = method.invoke(acesso).toString();																	
								pCell.addElement(new Phrase(valor, fConteudo));									
							
							} else if (method.invoke(acesso) instanceof Cliente) {
								Usuario operador = (Usuario) method.invoke(acesso);
								
								if(c.toString().equals("operador.username")){
									pCell.addElement(new Phrase(operador.getUsername(),fCampo));
								}
								
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
			
			Paragraph pResumoVl = new Paragraph(changeHeaderColumn(selectCampo(resumo)),fSubTitulo);
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
			
			CriteriaQuery<AlteracoesContrato> criteriaQueryGroup = cb.createQuery(AlteracoesContrato.class);
			Root<AlteracoesContrato> rootGroup = criteriaQueryGroup.from(AlteracoesContrato.class);
	
			if (selectCampo(resumo).equals("operador.username")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("operador").get("username");
				
				criteriaQueryGroup.groupBy(rootGroup.join("operador").get("username"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AlteracoesContrato.class,coluna, qtd));
			}
					

			if (!selectCampo(resumo).equals("operador.username")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get(selectCampo(resumo));				
				criteriaQueryGroup.groupBy(rootGroup.get(selectCampo(resumo)));			
				
				if (criteria.size() == 0) {
					throw new RuntimeException("no criteria");
				} else if (criteria.size() == 1) {					
					criteriaQueryGroup.where(criteria.get(0));
				} else {					
					criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
				}
				
				//criteriaQueryGroup.where(cb.not(cb.equal(rootGroup.join("endereco").get("cidade"), "BELO JARDIM")));
				criteriaQueryGroup.select(cb.construct(AlteracoesContrato.class,coluna, qtd));				
			}

			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);			
			
			
			for (AlteracoesContrato c :(List<AlteracoesContrato>) qGroup.getResultList()) {
				
				Paragraph pResum = new Paragraph();
				
				if(selectCampo(resumo).equals("operador.username")){
				
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);				
					pResum.setAlignment(Element.ALIGN_LEFT);
					
				}else{

					if(rootGroup.get(selectCampo(resumo)).getJavaType().equals(String.class)){
						pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					}
					
					if(rootGroup.get(selectCampo(resumo)).getJavaType().equals(Date.class)){
						SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy");
						pResum = new Paragraph(smf.format(c.getColuna_date()), fCaptionsBold);					
					}
					pResum.setAlignment(Element.ALIGN_LEFT);
				}
				
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
				
				PdfPTable tbResumo = new PdfPTable(new float[]{0.55f,0.08f,1f});
				tbResumo.setWidthPercentage(100f);	
				tbResumo.addCell(pCellResumo);
				tbResumo.addCell(pCellResumoVl);
				tbResumo.addCell(pCellResumoVazio);
				
				doc.add(tbResumo);			
			}
			
			Paragraph ptotal = new Paragraph("TOTAL:",fCab);
			ptotal.setAlignment(Element.ALIGN_LEFT);
			PdfPCell pCellTotal = new PdfPCell();
			pCellTotal.setBorderWidth(0);	
			pCellTotal.addElement(ptotal);
			
			Paragraph pTotalVl = new Paragraph(""+q.getResultList().size(),fSubTitulo);
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
	
	
	public static JFreeChart generatePieChart(String titulo, Map<String, Integer> lista) {
        DefaultPieDataset dataSet = new DefaultPieDataset();
              
        Set<String> chaves = lista.keySet();  
        for (String chave : chaves)  
        {            
        	System.out.println(chave + lista.get(chave));  
            dataSet.setValue(chave,lista.get(chave));            
        } 
        
        JFreeChart chart = ChartFactory.createPieChart(titulo, dataSet, false, true, false);

        return chart;
    }
	
	public static JFreeChart generateBarChart(String titulo, Map<String, Integer> lista) {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		             
        Set<String> chaves = lista.keySet();
        Integer i = 0;
        for (String chave : chaves)  
        {            
        	i++; 
            dataSet.setValue(lista.get(chave), lista.get(chave), chave);	           
        } 
        
        JFreeChart chart = ChartFactory.createBarChart(titulo,"Categoria","Valor",
                dataSet, PlotOrientation.VERTICAL, false, true, false);

        return chart;
    }

	private String changeHeaderColumn(String s) {

		String filtro = "";
		if(s.equals("id")){
			filtro = "COD.";			
		}else if(s.equals("tipo")){
			filtro = "TIPO";			
		}else if(s.equals("operador.username")){
			filtro = "OPERADOR";			
		}else if(s.equals("data_alteracao")){
			filtro = "DATA";			
		}
				
		return filtro;

	}
	
	public String selectCampo(String s) {
		
		String filtro = "";
		if(s.equals("Código")){
			filtro = "id";			
		}else if(s.equals("Tipo")){
			filtro = "tipo";			
		}else if(s.equals("Operador")){
			filtro = "operador.username";			
		}else if(s.equals("Data")){
			filtro = "data_alteracao";			
		}
		
		return filtro;
	}

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}
