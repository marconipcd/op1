package com.digital.opuserp.view.modulos.relatorio.Produto;

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
import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ComoNosConheceu;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.Marca;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.UnidadeProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
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
import com.vaadin.ui.Notification;

public class ExportProduto implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportProduto(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista, List<Object> columns)throws Exception {

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
			CriteriaQuery<Produto> criteriaQuery = cb.createQuery(Produto.class);
			Root<Produto> rootCliente = criteriaQuery.from(Produto.class);
			EntityType<Produto> type = em.getMetamodel().entity(Produto.class);

			List<Predicate> criteria = new ArrayList<Predicate>();

			criteria.add(cb.equal(rootCliente.get("empresaId"), OpusERP4UI.getEmpresa().getId()));

			if (lista.size() > 0) {
				for (SearchParameters s : lista) {

					if (s.getOperador().equals("IGUAL")) {
						if (s.getCampo().equals("fornecedorId.razao_social")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("fornecedorId").<String>get("razao_social")), s.getValor().toLowerCase()));
						}						
						if (s.getCampo().equals("grupoId.nome_grupo")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("grupoId").<String>get("nome_grupo")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("marcasId.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("marcasId").<String>get("nome")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cst_origem.referencia")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("cst_origem").<String>get("referencia")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cst_forma_tributo.referencia")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("cst_forma_tributo").<String>get("referencia")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("simples_nacional.referencia")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("simples_nacional").<String>get("referencia")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("unidadeProduto.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("unidadeProduto").<String>get("nome")), s.getValor().toLowerCase()));
						}
					

						if (!s.getCampo().equals("fornecedorId.razao_social") && !s.getCampo().equals("grupoId.nome_grupo") && 
								!s.getCampo().equals("marcasId.nome") && !s.getCampo().equals("cst_origem.referencia") &&
								!s.getCampo().equals("cst_forma_tributo.referencia") && !s.getCampo().equals("simples_nacional.referencia")
								&& !s.getCampo().equals("unidadeProduto.nome")) {
							
							
//							    criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
							
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

						if (s.getCampo().equals("fornecedorId.razao_social")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("fornecedorId").<String>get("razao_social")), s.getValor().toLowerCase()));
						}						
						if (s.getCampo().equals("grupoId.nome_grupo")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("grupoId").<String>get("nome_grupo")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("marcasId.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("marcasId").<String>get("nome")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cst_origem.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("cst_origem").<String>get("referencia")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cst_forma_tributo.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("cst_forma_tributo").<String>get("referencia")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("simples_nacional.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("simples_nacional").<String>get("referencia")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("unidadeProduto.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("unidadeProduto").<String>get("nome")), s.getValor().toLowerCase()));
						}
						
						if (!s.getCampo().equals("fornecedorId.razao_social") && !s.getCampo().equals("grupoId.nome_grupo") && 
								!s.getCampo().equals("marcasId.nome") && !s.getCampo().equals("cst_origem.referencia") &&
								!s.getCampo().equals("cst_forma_tributo.referencia") && !s.getCampo().equals("simples_nacional.referencia")
								&& !s.getCampo().equals("unidadeProduto.nome")){
							
//							criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())), s.getValor().toLowerCase()));
							
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
						
						
						if (s.getCampo().equals("fornecedorId.razao_social")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("fornecedorId").<String>get("razao_social")), "%" + s.getValor().toLowerCase()+ "%"));
						}						
						if (s.getCampo().equals("grupoId.nome_grupo")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("grupoId").<String>get("nome_grupo")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("marcasId.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("marcasId").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cst_origem.referencia")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("cst_origem").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cst_forma_tributo.referencia")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("cst_forma_tributo").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("simples_nacional.referencia")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("simples_nacional").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("unidadeProduto.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("unidadeProduto").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}
					
						
						if (!s.getCampo().equals("fornecedorId.razao_social") && !s.getCampo().equals("grupoId.nome_grupo") && 
								!s.getCampo().equals("marcasId.nome") && !s.getCampo().equals("cst_origem.referencia") &&
								!s.getCampo().equals("cst_forma_tributo.referencia") && !s.getCampo().equals("simples_nacional.referencia")
								&& !s.getCampo().equals("unidadeProduto.nome")) {
							
							criteria.add(cb.like(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
					} else if (s.getOperador().equals("NAO CONTEM")) {

						if (s.getCampo().equals("fornecedorId.razao_social")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("fornecedorId").<String>get("razao_social")), "%" + s.getValor().toLowerCase()+ "%"));
						}						
						if (s.getCampo().equals("grupoId.nome_grupo")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("grupoId").<String>get("nome_grupo")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("marcasId.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("marcasId").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cst_origem.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("cst_origem").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cst_forma_tributo.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("cst_forma_tributo").<String>get("referencia")),"%" +  s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("simples_nacional.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("simples_nacional").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("unidadeProduto.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.join("unidadeProduto").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (!s.getCampo().equals("fornecedorId.razao_social") && !s.getCampo().equals("grupoId.nome_grupo") && 
								!s.getCampo().equals("marcasId.nome") && !s.getCampo().equals("cst_origem.referencia") &&
								!s.getCampo().equals("cst_forma_tributo.referencia") && !s.getCampo().equals("simples_nacional.referencia")
								&& !s.getCampo().equals("unidadeProduto.nome")) {
							
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
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.greaterThan(rootCliente.<Date> get(s.getCampo()), dtValor));
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
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)+ date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.lessThan(rootCliente.<Date> get(s.getCampo()), dtValor));
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
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)+ date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Date> get(s.getCampo()), dtValor));
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
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)	+ date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.lessThanOrEqualTo(rootCliente.<Date> get(s.getCampo()), dtValor));
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

			
		
			
			if (selectCampo(order).equals("fornecedorId.razao_social")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("fornecedorId").get("razao_social")));
			}else if (selectCampo(order).equals("grupoId.nome_grupo")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("grupoId").get("nome_grupo")));
			}else if (selectCampo(order).equals("marcasId.nome")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("marcasId").get("nome")));
			}else if (selectCampo(order).equals("cst_origem.referencia")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cst_origem").get("referencia")));
			}else if (selectCampo(order).equals("cst_forma_tributo.referencia")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cst_forma_tributo").get("referencia")));
			}else if (selectCampo(order).equals("simples_nacional.referencia")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("simples_nacional").get("referencia")));
			}else if (selectCampo(order).equals("unidadeProduto.nome")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("unidadeProduto").get("nome")));
			}else{	
				criteriaQuery.orderBy(cb.asc(rootCliente.get(selectCampo(order))));
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE PRODUTOS",fTitulo);
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

			List<Produto> produtos = q.getResultList();

			
					
			float[] f = new float[columns.size()];
			
			Integer i=0;
			for (Object c : columns) {
	
				if(changeHeaderColumn(c.toString()).equals("CÓDIGO")){
					f[i] = (0.15f);		
				}	
			    if(changeHeaderColumn(c.toString()).equals("GRUPO")){
			    	f[i] = (0.45f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("MARCA")){
			    	f[i] = (0.25f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("NOME")){
			    	f[i] = (0.70f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("FORNECEDOR")){
			    	f[i] = (0.50f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("UN")){
			    	f[i] = (0.15f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("COD. BARRAS")){
			    	f[i] = (0.35f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("VALOR CUSTO R$")){
			    	f[i] = (0.25f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("VALOR VENDA R$")){
			    	f[i] = (0.25f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("LUCRO R$")){
			    	f[i] = (0.20f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("GARANTIA")){
			    	f[i] = (0.28f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("QTD. ESTOQUE")){
			    	f[i] = (0.25f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("QTD. ESTOQUE DEPOSITO")){
			    	f[i] = (0.29f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("REFERENCIA")){
			    	f[i] = (0.25f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("UTILIZAR SERIAIS")){
			    	f[i] = (0.28f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("FRACIONAR")){
			    	f[i] = (0.20f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("STATUS")){
			    	f[i] = (0.15f);	
			    }
			    i++;
     		  }					

			float totalValorCusto = 0;			
			float totalValorVenda = 0;			
			float totalValorLucro = 0;	
			Integer reg= 0;
			Integer cont = 0;
			PdfPTable tbConteudo2 = new PdfPTable(f);
			
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
				
				
				for (Produto produto : produtos) {
	
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(5f);
		
					PdfPTable tbConteudo = new PdfPTable(f);
					tbConteudo.setWidthPercentage(100f);
				
					if(produto.getValorCusto()!=null && !produto.getValorCusto().equals("")){
						totalValorCusto = totalValorCusto + produto.getValorCusto();						
					}
					if(produto.getValorVenda()!=null && !produto.getValorVenda().equals("")){
						totalValorVenda = totalValorVenda + produto.getValorVenda();						
					}
					if(produto.getLucro()!=null && !produto.getLucro().equals("")){
						totalValorLucro = totalValorLucro + produto.getLucro();						
					}
					
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
							
							metodo = metodo.replace(".nome", "");		
							metodo = metodo.replace(".nome_grupo", "");	
							metodo = metodo.replace("_grupo", "");	
							metodo = metodo.replace(".razao_social","");
														
							Class cls = produto.getClass();
							Method method = cls.getMethod(metodo);
							
							
							if (method.invoke(produto) instanceof Float || method.invoke(produto) instanceof String || method.invoke(produto) instanceof Integer || method.invoke(produto) instanceof Date) {
									
								String valor = method.invoke(produto).toString();								
								Paragraph valorColuna = null;								
								
								if(c.toString().equals("valorCusto")||c.toString().equals("valorVenda")||c.toString().equals("lucro")){
									valorColuna = new Paragraph(Real.formatDbToString(String.valueOf(valor)),fCampo);
									valorColuna.setAlignment(Element.ALIGN_RIGHT);
								}else{
									valorColuna = new Paragraph(valor,fCampo);
								}
																						
								pCell.addElement(valorColuna);				
								
								
							} else if (method.invoke(produto) instanceof Fornecedor) {
								Fornecedor cat = (Fornecedor) method.invoke(produto);
								pCell.addElement(new Phrase(cat.getRazao_social(),fCampo));
							} else if (method.invoke(produto) instanceof GrupoProduto) {
								GrupoProduto grupoProduto = (GrupoProduto) method.invoke(produto);
								pCell.addElement(new Phrase(grupoProduto.getNome_grupo(), fCampo));
							} else if (method.invoke(produto) instanceof Marca) {
								Marca marcaProduto = (Marca) method.invoke(produto);
								pCell.addElement(new Phrase(marcaProduto.getNome(), fCampo));
							} else if (method.invoke(produto) instanceof UnidadeProduto) {
								UnidadeProduto undProduto = (UnidadeProduto) method.invoke(produto);
								pCell.addElement(new Phrase(undProduto.getNome(), fCampo));
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
				
				for (Object c : columns) {
					reg++;
					
					Paragraph TotalPago = null;
					PdfPCell pCell2 = new PdfPCell();
					
					if(c.toString().equals("valorCusto")){
						TotalPago = new Paragraph(Real.formatDbToString(String.valueOf(totalValorCusto)),fCampo);
						TotalPago.setAlignment(Element.ALIGN_RIGHT);													
						pCell2.setBackgroundColor(new BaseColor(232, 235, 237));						
					}else if(c.toString().equals("valorVenda")){
						TotalPago = new Paragraph(Real.formatDbToString(String.valueOf(totalValorVenda)),fCampo);
						TotalPago.setAlignment(Element.ALIGN_RIGHT);													
						pCell2.setBackgroundColor(new BaseColor(232, 235, 237));						
					}else if(c.toString().equals("lucro")){
						TotalPago = new Paragraph(Real.formatDbToString(String.valueOf(totalValorLucro)),fCampo);
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
				for (Produto produto : produtos) {
	
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
							
							metodo = metodo.replace(".nome", "");		
							metodo = metodo.replace(".nome_grupo", "");					
							metodo = metodo.replace("_grupo", "");	
							metodo = metodo.replace(".razao_social","");
							
							Class cls = produto.getClass();
							Method method = cls.getMethod(metodo);
	
							if(method.invoke(produto)==null||method.invoke(produto).toString().equals("")){
								pCell.addElement(new Phrase(" ", fConteudo));		
							}	
										
							
							if (method.invoke(produto) instanceof String || method.invoke(produto) instanceof Integer || method.invoke(produto) instanceof Date) {
								
															
								String valor = method.invoke(produto).toString();								
								Paragraph valorColuna = null;								
								
								if(c.toString().equals("valorCusto")||c.toString().equals("valorVenda")||c.toString().equals("lucro")){
									valorColuna = new Paragraph(Real.formatDbToString(String.valueOf(valor)),fCampo);
									valorColuna.setAlignment(Element.ALIGN_RIGHT);
								}else{
									valorColuna = new Paragraph(valor,fCampo);
								}
																						
								pCell.addElement(valorColuna);									
							
							} else if (method.invoke(produto) instanceof Fornecedor) {
								Fornecedor cat = (Fornecedor) method.invoke(produto);
								pCell.addElement(new Phrase(cat.getRazao_social(),fCampo));
							} else if (method.invoke(produto) instanceof GrupoProduto) {
								GrupoProduto grupoProduto = (GrupoProduto) method.invoke(produto);
								pCell.addElement(new Phrase(grupoProduto.getNome_grupo(), fCampo));
							} else if (method.invoke(produto) instanceof Marca) {
								Marca marcaProduto = (Marca) method.invoke(produto);
								pCell.addElement(new Phrase(marcaProduto.getNome(), fCampo));
							} else if (method.invoke(produto) instanceof UnidadeProduto) {
								UnidadeProduto undProduto = (UnidadeProduto) method.invoke(produto);
								pCell.addElement(new Phrase(undProduto.getNome(), fCampo));
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
			
			
			CriteriaQuery<Produto> criteriaQueryGroup = cb.createQuery(Produto.class);
			Root<Produto> rootGroup = criteriaQueryGroup.from(Produto.class);
			
			
			if (selectCampo(resumo).equals("fornecedorId.razao_social")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("fornecedorId").get("razao_social");
				
				criteriaQueryGroup.groupBy(rootGroup.join("fornecedorId").get("razao_social"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Produto.class,coluna, qtd));
			}

			if (selectCampo(resumo).equals("grupoId.nome_grupo")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("grupoId").get("nome_grupo");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("grupoId").get("nome_grupo"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Produto.class,coluna, qtd));
			}

			if (selectCampo(resumo).equals("marcasId.nome")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("marcasId").get("nome");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("marcasId").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Produto.class,coluna, qtd));
			}						
			if (selectCampo(resumo).equals("cst_origem.referencia")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cst_origem").get("referencia");
				
				criteriaQueryGroup.groupBy(rootGroup.join("cst_origem").get("referencia"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Produto.class,coluna, qtd));
			}
			if (selectCampo(resumo).equals("cst_forma_tributo.referencia")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cst_forma_tributo").get("referencia");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("cst_forma_tributo").get("referencia"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Produto.class,coluna, qtd));
			}
			if (selectCampo(resumo).equals("simples_nacional.referencia")) {

				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("simples_nacional").get("referencia");
				
				criteriaQueryGroup.groupBy(rootGroup.join("simples_nacional").get("referencia"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Produto.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("unidadeProduto.nome")) {

				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("unidadeProduto").get("nome");
				
				criteriaQueryGroup.groupBy(rootGroup.join("unidadeProduto").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Produto.class,coluna, qtd));
				
			}
		
			
		

			if (!selectCampo(resumo).equals("fornecedorId.razao_social") && !selectCampo(resumo).equals("grupoId.nome_grupo") && 
					!selectCampo(resumo).equals("marcasId.nome") && !selectCampo(resumo).equals("cst_origem.referencia") &&
					!selectCampo(resumo).equals("cst_forma_tributo.referencia") && !selectCampo(resumo).equals("simples_nacional.referencia")
					&& !selectCampo(resumo).equals("unidadeProduto.nome")) {
				
				
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
				
				
				//criteriaQueryGroup.where(cb.not(cb.equal(rootGroup.join("endereco_principal").get("cidade"), "BELO JARDIM")));
				criteriaQueryGroup.select(cb.construct(Produto.class,coluna, qtd));
				
			}

			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);			
			
			
			for (Produto c :(List<Produto>) qGroup.getResultList()) {
				
				Paragraph pResum = new Paragraph();
				
				if (selectCampo(resumo).equals("fornecedorId.razao_social") || selectCampo(resumo).equals("grupoId.nome_grupo") || 
						selectCampo(resumo).equals("marcasId.nome") || selectCampo(resumo).equals("cst_origem.referencia") ||
						selectCampo(resumo).equals("cst_forma_tributo.referencia") || selectCampo(resumo).equals("simples_nacional.referencia")
						|| selectCampo(resumo).equals("unidadeProduto.nome")) {
					
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					pResum.setAlignment(Element.ALIGN_LEFT);
				}else{
					if(rootGroup.get(selectCampo(resumo)).getJavaType().equals(String.class)){
						pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					}
					
					if(rootGroup.get(selectCampo(resumo)).getJavaType().equals(Float.class)){
						pResum = new Paragraph(Real.formatDbToString(String.valueOf(c.getColuna_float())), fCaptionsBold);
					}
					
					if(rootGroup.get(selectCampo(resumo)).getJavaType().equals(Integer.class)){
						pResum = new Paragraph(String.valueOf(c.getColuna_Integer()), fCaptionsBold);
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
		
		if(s.equals("nome")){
			filtro = "NOME";			
		}else if(s.equals("id")){
			filtro = "CÓDIGO";			
		}else if(s.equals("fornecedorId.razao_social")){
			filtro = "FORNECEDOR";			
		}else if(s.equals("grupoId.nome_grupo")){
			filtro = "GRUPO";			
		}else if(s.equals("marcasId.nome")){
			filtro = "MARCA";			
		}else if(s.equals("unidadeProduto.nome")){
			filtro = "UN";			
		}else if(s.equals("valorCusto")){
			filtro = "VALOR CUSTO R$";			
		}else if(s.equals("valorVenda")){
			filtro = "VALOR VENDA R$";			
		}else if(s.equals("lucro")){
			filtro = "LUCRO R$";			
		}else if(s.equals("garantia")){
			filtro = "GARANTIA";			
		}else if(s.equals("qtdEstoque")){
			filtro = "QTD. ESTOQUE";			
		}else if(s.equals("qtdEstoqueDeposito")){
			filtro = "QTD. ESTOQUE DEPOSITO";			
		}else if(s.equals("referencia")){
			filtro = "REFERENCIA";			
		}else if(s.equals("fracionar")){
			filtro = "FRACIONAR";			
		}else if(s.equals("utilizaSeriais")){
			filtro = "UTILIZAR SERIAIS";			
		}else if(s.equals("status")){
			filtro = "STATUS";			
		}				
		return filtro;
	}
	
//		switch (string) {
//
//		case "id":
//			return "CÓDIGO";
//			// break;
//
//		case "nome":
//			return "NOME";
//			// break;
//
//		case "fornecedorId.razao_social":
//			return "FORNECEDOR";
//			// break;
//			
//		case "grupoId.nome_grupo":
//			return "GRUPO";
//			// break;
//
//		case "marcasId.nome":
//			return "MARCA";
//			// break;
//
//		case "unidadeProduto.nome":
//			return "UN";
//			// break;
//
//		case "valorCusto":
//			return "VALOR CUSTO";
//			// break;
//			
//		case "valorVenda":
//			return "VALOR VENDA";
//			// break;
//
//		case "lucro":
//			return "LUCRO";
//			// break;
//
//		case "garantia":
//			return "GARANTIA";
//			// break;
//
//		case "qtdEstoque":
//			return "QTD. ESTOQUE";
//			// break;
//
//		case "referencia":
//			return "REFERENCIA";
//			// break;
//
//		case "fracionar":
//			return "FRACIONAR";
//			// break;
//
//		case "utilizaSeriais":
//			return "PERMITIR SERIAIS";
//			// break;
//
//		case "status":
//			return "STATUS";
//			// break;
//
//		default:
//			return "Coluna Não Identificada";
//			// break;
//		}

//	}
	
	public String selectCampo(String s) {
		
		String filtro = "";
		if(s.equals("NOME")){
			filtro = "nome";			
		}else if(s.equals("CÓDIGO")){
			filtro = "id";			
		}else if(s.equals("FORNECEDOR")){
			filtro = "fornecedorId.razao_social";			
		}else if(s.equals("GRUPO")){
			filtro = "grupoId.nome_grupo";			
		}else if(s.equals("MARCA")){
			filtro = "marcasId.nome";			
		}else if(s.equals("UN")){
			filtro = "unidadeProduto.nome";			
		}else if(s.equals("VALOR CUSTO")){
			filtro = "valorCusto";			
		}else if(s.equals("VALOR VENDA")){
			filtro = "valorVenda";			
		}else if(s.equals("LUCRO")){
			filtro = "lucro";			
		}else if(s.equals("GARANTIA")){
			filtro = "garantia";			
		}else if(s.equals("QTD. ESTOQUE")){
			filtro = "qtdEstoque";			
		}else if(s.equals("QTD. ESTOQUE DEPOSITO")){
			filtro = "qtdEstoqueDeposito";			
		}else if(s.equals("REFERENCIA")){
			filtro = "referencia";			
		}else if(s.equals("FRACIONAR")){
			filtro = "fracionar";			
		}else if(s.equals("UTILIZAR SERIAIS")){
			filtro = "utilizaSeriais";			
		}else if(s.equals("STATUS")){
			filtro = "status";			
		}				
		return filtro;
	}

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}
