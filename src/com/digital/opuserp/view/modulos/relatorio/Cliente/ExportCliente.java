package com.digital.opuserp.view.modulos.relatorio.Cliente;

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
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.itextpdf.text.BaseColor;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.itextpdf.awt.DefaultFontMapper;
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

public class ExportCliente implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportCliente(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista, List<Object> columns)throws Exception {

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
			CriteriaQuery<Cliente> criteriaQuery = cb.createQuery(Cliente.class);
			Root<Cliente> rootCliente = criteriaQuery.from(Cliente.class);
			EntityType<Cliente> type = em.getMetamodel().entity(Cliente.class);

			List<Predicate> criteria = new ArrayList<Predicate>();

			criteria.add(cb.equal(rootCliente.get("empresa"), OpusERP4UI.getEmpresa()));

			if (lista.size() > 0) {
				for (SearchParameters s : lista) {

					if (s.getOperador().equals("IGUAL")) {
						if (s.getCampo().equals("categoria.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("categoria").<String>get("nome")), s.getValor().toLowerCase()));
						}

						if (s.getCampo().equals("como_nos_conheceu.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("como_nos_conheceu").<String>get("nome")), s.getValor().toLowerCase()));
						}
						
						//endereco_principals
						if (s.getCampo().equals("endereco_principal.cep")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal_principal").<String>get("cep")), s.getValor().toLowerCase()));
						}						
						if (s.getCampo().equals("endereco_principal.numero")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("numero")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.endereco")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("endereco")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.bairro")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("bairro")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.cidade")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("cidade")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.uf")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("uf")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.pais")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("pais")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.complemento")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("complemento")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.referencia")) {
							criteria.add(cb.like(cb.lower(rootCliente.join("endereco_principal").<String>get("referencia")), s.getValor().toLowerCase()));
						}

						if (!s.getCampo().equals("categoria.nome") && !s.getCampo().equals("como_nos_conheceu.nome") && 
								!s.getCampo().equals("endereco_principal.cep") && !s.getCampo().equals("endereco_principal.numero") &&
								!s.getCampo().equals("endereco_principal.endereco") && !s.getCampo().equals("endereco_principal.bairro") &&
								!s.getCampo().equals("endereco_principal.uf") && !s.getCampo().equals("endereco_principal.cidade") && 
								!s.getCampo().equals("endereco_principal.pais") && !s.getCampo().equals("endereco_principal.complemento") &&
								!s.getCampo().equals("endereco_principal.referencia")) {
							criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
						}

					}else if (s.getOperador().equals("DIFERENTE")) {
						if (s.getCampo().equals("categoria.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("categoria").<String>get("nome")), s.getValor().toLowerCase()));
						}

						if (s.getCampo().equals("como_nos_conheceu.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("como_nos_conheceu").<String>get("nome")),s.getValor().toLowerCase()));
						}
						
						//endereco_principals
						if (s.getCampo().equals("endereco_principal.cep")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("cep")),  s.getValor().toLowerCase()));
						}						
						if (s.getCampo().equals("endereco_principal.numero")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("numero")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.endereco")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("endereco")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.bairro")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("bairro")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.cidade")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("cidade")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.uf")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("uf")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.pais")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("pais")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.complemento")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("complemento")),  s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco_principal.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("referencia")), s.getValor().toLowerCase()));
						}
						
						if (!s.getCampo().equals("categoria.nome") && !s.getCampo().equals("como_nos_conheceu.nome") && 
								!s.getCampo().equals("endereco_principal.cep") && !s.getCampo().equals("endereco_principal.numero") &&
								!s.getCampo().equals("endereco_principal.endereco") && !s.getCampo().equals("endereco_principal.bairro") &&
								!s.getCampo().equals("endereco_principal.uf") && !s.getCampo().equals("endereco_principal.cidade") && 
								!s.getCampo().equals("endereco_principal.pais") && !s.getCampo().equals("endereco_principal.complemento") &&
								!s.getCampo().equals("endereco_principal.referencia")) {
							
							criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())), s.getValor().toLowerCase()));
						}

					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("categoria.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("categoria").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));							
						}

						if (s.getCampo().equals("como_nos_conheceu.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("como_nos_conheceu").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						//endereco_principals
						if (s.getCampo().equals("endereco_principal.cep")) {							
							criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("cep")), "%" + s.getValor().toLowerCase()+ "%"));
						}						
						if (s.getCampo().equals("endereco_principal.numero")) {							
							criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("numero")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.endereco")) {							
							criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.bairro")) {							
							criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.cidade")) {							
							criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.uf")) {							
							criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.pais")) {							
							criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("pais")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.complemento")) {							
							criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("complemento")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.referencia")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("endereco_principal").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (!s.getCampo().equals("categoria.nome") && !s.getCampo().equals("como_nos_conheceu.nome") && 
								!s.getCampo().equals("endereco_principal.cep") && !s.getCampo().equals("endereco_principal.numero") &&
								!s.getCampo().equals("endereco_principal.endereco") && !s.getCampo().equals("endereco_principal.bairro") &&
								!s.getCampo().equals("endereco_principal.uf") && !s.getCampo().equals("endereco_principal.cidade") && 
								!s.getCampo().equals("endereco_principal.pais") && !s.getCampo().equals("endereco_principal.complemento") &&
								!s.getCampo().equals("endereco_principal.referencia")) {
							
							criteria.add(cb.like(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
					} else if (s.getOperador().equals("NAO CONTEM")) {
						
						if (s.getCampo().equals("categoria.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("categoria").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
						}

						if (s.getCampo().equals("como_nos_conheceu.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("como_nos_conheceu").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						//endereco_principals
						if (s.getCampo().equals("endereco_principal.cep")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("cep")), "%" + s.getValor().toLowerCase()+ "%"));
						}						
						if (s.getCampo().equals("endereco_principal.numero")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("numero")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.endereco")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.bairro")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.cidade")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.uf")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.pais")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("pais")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.complemento")) {							
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("complemento")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco_principal.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("endereco_principal").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (!s.getCampo().equals("categoria.nome") && !s.getCampo().equals("como_nos_conheceu.nome") && 
								!s.getCampo().equals("endereco_principal.cep") && !s.getCampo().equals("endereco_principal.numero") &&
								!s.getCampo().equals("endereco_principal.endereco") && !s.getCampo().equals("endereco_principal.bairro") &&
								!s.getCampo().equals("endereco_principal.uf") && !s.getCampo().equals("endereco_principal.cidade") && 
								!s.getCampo().equals("endereco_principal.pais") && !s.getCampo().equals("endereco_principal.complemento") &&
								!s.getCampo().equals("endereco_principal.referencia")) {
							
							criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
					} else if (s.getOperador().equals("MAIOR QUE")) {
						
						try{						
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.greaterThan(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
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

			
			if (selectCampo(order).equals("categoria.nome")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("categoria").get("nome")));
			}else if (selectCampo(order).equals("como_nos_conheceu.nome")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("como_nos_conheceu").get("nome")));
			}else if (selectCampo(order).equals("endereco_principal.cep")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("cep")));
			}else if (selectCampo(order).equals("endereco_principal.numero")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("numero")));
			}else if (selectCampo(order).equals("endereco_principal.endereco")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("endereco")));
			}else if (selectCampo(order).equals("endereco_principal.bairro")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("bairro")));
			}else if (selectCampo(order).equals("endereco_principal.cidade")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("cidade")));
			}else if (selectCampo(order).equals("endereco_principal.uf")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("uf")));
			}else if (selectCampo(order).equals("endereco_principal.pais")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("pais")));
			}else if (selectCampo(order).equals("endereco_principal.complemento")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("complemento")));
			}else if (selectCampo(order).equals("endereco_principal.referencia")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("referencia")));
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE CLIENTES",fTitulo);
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

			List<Cliente> clientes = q.getResultList();

			
					
			float[] f = new float[columns.size()];
			
			Integer i=0;
			for (Object c : columns) {
	
				if(changeHeaderColumn(c.toString()).equals("CÓDIGO")){
					f[i] = (0.20f);		
				}	
			    if(changeHeaderColumn(c.toString()).equals("NOME/RAZÃO SOCIAL")){
			    	f[i] = (0.90f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("TIPO PESSOA")){
			    	f[i] = (0.30f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("CPF/CNPJ")){
			    	f[i] = (0.37f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("IE/RG")){
			    	f[i] = (0.30f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("CATEGORIA")){
			    	f[i] = (0.35f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("NOME FANTASIA")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("CONTATO")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("SEXO")){
			    	f[i] = (0.30f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("DATA NASC")){
			    	f[i] = (0.30f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("TEL. PRINCIPAL")){
			    	f[i] = (0.30f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("TEL. ALT 1")){
			    	f[i] = (0.30f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("TEL. ALT 2")){
			    	f[i] = (0.30f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("TEL. ALT 3")){
			    	f[i] = (0.30f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("EMAIL PRINCIPAL")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("EMAIL ALT")){
			    	f[i] = (0.40f);	
			    }                                          
			    if(changeHeaderColumn(c.toString()).equals("COMO CONHECEU")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("OBS")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("CADASTRO")){
			    	f[i] = (0.43f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("ALTERAÇÃO")){
			    	f[i] = (0.43f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("STATUS")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("COMO QUER SER CHAMADO")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("CEP")){
			    	f[i] = (0.25f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("ENDEREÇO")){
			    	f[i] = (0.90f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("NÚMERO")){
			    	f[i] = (0.23f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("CIDADE")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("BAIRRO")){
			    	f[i] = (0.42f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("UF")){
			    	f[i] = (0.15f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("PAÍS")){
			    	f[i] = (0.30f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("COMPLEMENTO")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("REFERÊNCIA")){
			    	f[i] = (0.50f);	
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
				
				
				for (Cliente cliente : clientes) {
	
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
							
							metodo = metodo.replace(".nome", "");
							metodo = metodo.replace(".cep", "");
							metodo = metodo.replace(".endereco", "");
							metodo = metodo.replace(".numero", "");
							metodo = metodo.replace(".bairro", "");
							metodo = metodo.replace(".cidade", "");
							metodo = metodo.replace(".uf", "");
							metodo = metodo.replace(".pais", "");
							metodo = metodo.replace(".complemento", "");
							metodo = metodo.replace(".referencia", "");

							Class cls = cliente.getClass();
							Method method = cls.getMethod(metodo);
							
							String valor = null;
							
							if (method.invoke(cliente) instanceof String || method.invoke(cliente) instanceof Integer || method.invoke(cliente) instanceof Date) {
	
								if(c.toString().equals("telefone1")){
									String metodo2 = "getDdd_fone1";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(cliente)!=null && !method2.invoke(cliente).toString().equals("")&& !method.invoke(cliente).toString().equals("")){
										valor = method2.invoke(cliente).toString()+" "+method.invoke(cliente).toString();									
									}else{
										valor = method.invoke(cliente).toString();										
									}
								}else if(c.toString().equals("telefone2")){
									String metodo2 = "getDdd_fone2";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(cliente)!=null && !method2.invoke(cliente).toString().equals("")&& !method.invoke(cliente).toString().equals("")){
										valor = method2.invoke(cliente).toString()+" "+method.invoke(cliente).toString();									
									}else{
										valor = method.invoke(cliente).toString();										
									}	
								}else if(c.toString().equals("celular1")){
									String metodo2 = "getDdd_cel1";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(cliente)!=null && !method2.invoke(cliente).toString().equals("")&& !method.invoke(cliente).toString().equals("")){
										valor = method2.invoke(cliente).toString()+" "+method.invoke(cliente).toString();									
									}else{
										valor = method.invoke(cliente).toString();										
									}	
								}else if(c.toString().equals("celular2")){
									String metodo2 = "getDdd_cel2";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(cliente)!=null && !method2.invoke(cliente).toString().equals("") && !method.invoke(cliente).toString().equals("")){
										valor = method2.invoke(cliente).toString()+" "+method.invoke(cliente).toString();									
									}else{
										valor = method.invoke(cliente).toString();										
									}	
								}else{									
									valor = method.invoke(cliente).toString();
								}																
								pCell.addElement(new Phrase(valor, fConteudo));			
								
							} else if (method.invoke(cliente) instanceof Categorias) {
								Categorias cat = (Categorias) method.invoke(cliente);
								pCell.addElement(new Phrase(cat.getNome(),fCampo));
							} else if (method.invoke(cliente) instanceof ComoNosConheceu) {
								ComoNosConheceu comoNosConheceu = (ComoNosConheceu) method.invoke(cliente);
								pCell.addElement(new Phrase(comoNosConheceu.getNome(), fCampo));
							}else if (method.invoke(cliente) instanceof Endereco) {
								Endereco end = (Endereco) method.invoke(cliente);
								
								String valorColuna = "";
								
								if(c.toString().equals("endereco_principal.cep")){
									valorColuna = end.getCep();
								}else if(c.toString().equals("endereco_principal.uf")){
									valorColuna = end.getUf();
								}else if(c.toString().equals("endereco_principal.endereco")){
									valorColuna = end.getEndereco();
								}else if(c.toString().equals("endereco_principal.bairro")){
									valorColuna = end.getBairro();
								}else if(c.toString().equals("endereco_principal.cidade")){
									valorColuna = end.getCidade();
								}else if(c.toString().equals("endereco_principal.complemento")){
									valorColuna = end.getComplemento();
								}else if(c.toString().equals("endereco_principal.numero")){
									valorColuna = end.getNumero();
								}else if(c.toString().equals("endereco_principal.pais")){
									valorColuna = end.getPais();
								}else if(c.toString().equals("endereco_principal.referencia")){
									valorColuna = end.getReferencia();
								}							
								
								pCell.addElement(new Phrase(valorColuna, fCampo));
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
				for (Cliente cliente : clientes) {
	
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
							metodo = metodo.replace(".cep", "");
							metodo = metodo.replace(".endereco", "");
							metodo = metodo.replace(".numero", "");
							metodo = metodo.replace(".bairro", "");
							metodo = metodo.replace(".cidade", "");
							metodo = metodo.replace(".uf", "");
							metodo = metodo.replace(".pais", "");
							metodo = metodo.replace(".complemento", "");
							metodo = metodo.replace(".referencia", "");
							
							
			
							Class cls = cliente.getClass();
							Method method = cls.getMethod(metodo);
	
							if(method.invoke(cliente)==null||method.invoke(cliente).toString().equals("")){
								pCell.addElement(new Phrase(" ", fConteudo));		
							}	
										
							String valor = null;
							
							if (method.invoke(cliente) instanceof String || method.invoke(cliente) instanceof Integer || method.invoke(cliente) instanceof Date) {
								
								if(c.toString().equals("telefone1")){
									String metodo2 = "getDdd_fone1";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(cliente)!=null && !method2.invoke(cliente).toString().equals("")&& !method.invoke(cliente).toString().equals("")){
										valor = method2.invoke(cliente).toString()+" "+method.invoke(cliente).toString();									
									}else{
										valor = method.invoke(cliente).toString();										
									}
								}else if(c.toString().equals("telefone2")){
									String metodo2 = "getDdd_fone2";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(cliente)!=null && !method2.invoke(cliente).toString().equals("")&& !method.invoke(cliente).toString().equals("")){
										valor = method2.invoke(cliente).toString()+" "+method.invoke(cliente).toString();									
									}else{
										valor = method.invoke(cliente).toString();										
									}	
								}else if(c.toString().equals("celular1")){
									String metodo2 = "getDdd_cel1";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(cliente)!=null && !method2.invoke(cliente).toString().equals("")&& !method.invoke(cliente).toString().equals("")){
										valor = method2.invoke(cliente).toString()+" "+method.invoke(cliente).toString();									
									}else{
										valor = method.invoke(cliente).toString();										
									}	
								}else if(c.toString().equals("celular2")){
									String metodo2 = "getDdd_cel2";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(cliente)!=null && !method2.invoke(cliente).toString().equals("") && !method.invoke(cliente).toString().equals("")){
										valor = method2.invoke(cliente).toString()+" "+method.invoke(cliente).toString();									
									}else{
										valor = method.invoke(cliente).toString();										
									}	
								}else{									
									valor = method.invoke(cliente).toString();
								}
																	
								pCell.addElement(new Phrase(valor, fConteudo));									
							
							} else if (method.invoke(cliente) instanceof Categorias) {
								Categorias cat = (Categorias) method.invoke(cliente);
								pCell.addElement(new Phrase(cat.getNome(),fConteudo));
							} else if (method.invoke(cliente) instanceof ComoNosConheceu) {
								ComoNosConheceu comoNosConheceu = (ComoNosConheceu) method.invoke(cliente);
								pCell.addElement(new Phrase(comoNosConheceu.getNome(), fConteudo));
							}else if (method.invoke(cliente) instanceof Endereco) {
								Endereco end = (Endereco) method.invoke(cliente);
																
								if(end.getComplemento()==null||end.getComplemento().equals(" ")||end.getComplemento().equals("\t")){
									end.setComplemento(" ");
								}
								if(end.getReferencia()==null||end.getReferencia().equals(" ")||end.getComplemento().equals("\t")){
									end.setReferencia(" ");
								}
								
								String valorColuna = "";
								
								if(c.toString().equals("endereco_principal.cep")){
									valorColuna = end.getCep();
								}else if(c.toString().equals("endereco_principal.uf")){
									valorColuna = end.getUf();
								}else if(c.toString().equals("endereco_principal.endereco")){
									valorColuna = end.getEndereco();
								}else if(c.toString().equals("endereco_principal.bairro")){
									valorColuna = end.getBairro();
								}else if(c.toString().equals("endereco_principal.cidade")){
									valorColuna = end.getCidade();
								}else if(c.toString().equals("endereco_principal.complemento")){	
									valorColuna = end.getComplemento();
								}else if(c.toString().equals("endereco_principal.numero")){
									valorColuna = end.getNumero();
								}else if(c.toString().equals("endereco_principal.pais")){
									valorColuna = end.getPais();
								}else if(c.toString().equals("endereco_principal.referencia")){
									valorColuna = end.getReferencia();						
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
			
			
			CriteriaQuery<Cliente> criteriaQueryGroup = cb.createQuery(Cliente.class);
			Root<Cliente> rootGroup = criteriaQueryGroup.from(Cliente.class);
			
			
			if (selectCampo(resumo).equals("categoria.nome")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("categoria").get("nome");
				
				criteriaQueryGroup.groupBy(rootGroup.join("categoria").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			}

			if (selectCampo(resumo).equals("como_nos_conheceu.nome")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("como_nos_conheceu").get("nome");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("como_nos_conheceu").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			}
			
			//endereco_principals
			if (selectCampo(resumo).equals("endereco_principal.cep")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco_principal").get("cep");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("cep"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			}						
			if (selectCampo(resumo).equals("endereco_principal.numero")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco_principal").get("numero");
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("numero"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			}
			if (selectCampo(resumo).equals("endereco_principal.endereco")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco_principal").get("endereco");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("endereco"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
			}
			if (selectCampo(resumo).equals("endereco_principal.bairro")) {

				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco_principal").get("bairro");
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("bairro"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("endereco_principal.cidade")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco_principal").get("cidade");
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("cidade"));			
				
				
				if (criteria.size() == 0) {
					throw new RuntimeException("no criteria");
				} else if (criteria.size() == 1) {					
					criteriaQueryGroup.where(cb.and(criteria.get(0)));
				} else {					
					criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
				}
			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("endereco_principal.uf")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco_principal").get("uf");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("uf"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("endereco_principal.pais")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco_principal").get("pais");		
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("pais"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("endereco_principal.complemento")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco_principal").get("complemento");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("complemento"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("endereco_principal.referencia")) {
								
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco_principal").get("referencia");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco_principal").get("referencia"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
				
			}

			if (!selectCampo(resumo).equals("categoria.nome") && !selectCampo(resumo).equals("como_nos_conheceu.nome") && 
					!selectCampo(resumo).equals("endereco_principal.cep") && !selectCampo(resumo).equals("endereco_principal.numero") &&
					!selectCampo(resumo).equals("endereco_principal.endereco") && !selectCampo(resumo).equals("endereco_principal.bairro") &&
					!selectCampo(resumo).equals("endereco_principal.uf") && !selectCampo(resumo).equals("endereco_principal.cidade") && 
					!selectCampo(resumo).equals("endereco_principal.pais") && !selectCampo(resumo).equals("endereco_principal.complemento") &&
					!selectCampo(resumo).equals("endereco_principal.referencia")) {
				
				
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
				criteriaQueryGroup.select(cb.construct(Cliente.class,coluna, qtd));
				
			}

			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);			
			
			
			for (Cliente c :(List<Cliente>) qGroup.getResultList()) {
				
				Paragraph pResum = new Paragraph();
				
				if (selectCampo(resumo).equals("categoria.nome") || selectCampo(resumo).equals("como_nos_conheceu.nome") ||
						selectCampo(resumo).equals("endereco_principal.cep") || selectCampo(resumo).equals("endereco_principal.numero") ||
						selectCampo(resumo).equals("endereco_principal.endereco") || selectCampo(resumo).equals("endereco_principal.bairro") ||
						selectCampo(resumo).equals("endereco_principal.uf") || selectCampo(resumo).equals("endereco_principal.cidade") || 
						selectCampo(resumo).equals("endereco_principal.pais") || selectCampo(resumo).equals("endereco_principal.complemento") ||
						selectCampo(resumo).equals("endereco_principal.referencia")) {
					
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

	private String changeHeaderColumn(String string) {

		switch (string) {

		case "id":
			return "CÓDIGO";
			// break;

		case "nome_razao":
			return "NOME/RAZÃO SOCIAL";
			// break;

		case "tipo_pessoa":
			return "TIPO PESSOA";
			// break;
			
		case "doc_cpf_cnpj":
			return "CPF/CNPJ";
			// break;

		case "doc_rg_insc_estadual":
			return "IE/RG";
			// break;

		case "categoria.nome":
			return "CATEGORIA";
			// break;

		case "como_nos_conheceu.nome":
			return "COMO CONHECEU";
			// break;
			
		case "nome_fantasia":
			return "NOME FANTASIA";
			// break;

		case "contato":
			return "CONTATO";
			// break;

		case "sexo":
			return "SEXO";
			// break;

		case "data_nascimento":
			return "DATA NASC.";
			// break;

		case "telefone1":
			return "TEL. PRINCIPAL";
			// break;

		case "telefone2":
			return "TEL. ALT 1";
			// break;

		case "celular1":
			return "TEL. ALT 2";
			// break;

		case "celular2":
			return "TEL. ALT 3";
			// break;

		case "email":
			return "EMAIL PRINCIPAL";
			// break;
			
		case "msn":
			return "EMAIL ALT";
			// break;

		case "como_nos_conheceu":
			return "COMO CONHECEU";
			// break;

		case "obs":
			return "OBS";
			// break;

		case "data_cadastro":
			return "CADASTRO";
			// break;

		case "data_alteracao":
			return "ALTERAÇÃO";
			// break;

		case "status":
			return "STATUS";
			// break;

		case "como_quer_ser_chamado":
			return "COMO QUER SER CHAMADO";
			// break;
			
		case "endereco_principal.cep":
			return "CEP";
			// break;

		case "endereco_principal.numero":
			return "NÚMERO";
			// break;
			
		case "endereco_principal.endereco":
			return "ENDEREÇO";
			// break;
			
		case "endereco_principal.bairro":
			return "BAIRRO";
			// break;
			
		case "endereco_principal.cidade":
			return "CIDADE";
			// break;
			
		case "endereco_principal.uf":
			return "UF";
			// break;
			
		case "endereco_principal.pais":
			return "PAÍS";
			// break;
			
		case "endereco_principal.complemento":
			return "COMPLEMENTO";
			// break;
			
		case "tratamento":
			return "TRATAMENTO";
			// break;Filtro
		case "endereco_principal.referencia":
			return "REFERÊNCIA";
			// break;Filtro

		default:
			return "Coluna Não Identificada";
			// break;
		}

	}
	
	public String selectCampo(String s) {
		
		String filtro = "";
		if(s.equals("Nome/Razão Social")){
			filtro = "nome_razao";			
		}else if(s.equals("Código")){
			filtro = "id";			
		}else if(s.equals("CPF/CNPJ")){
			filtro = "doc_cpf_cnpj";			
		}else if(s.equals("Inscrição Estadual/RG")){
			filtro = "doc_rg_insc_estadual";			
		}else if(s.equals("Nome Fantasia")){
			filtro = "nome_fantasia";			
		}else if(s.equals("Sexo")){
			filtro = "sexo";			
		}else if(s.equals("Data de Nascimento")){
			filtro = "data_nascimento";			
		}else if(s.equals("Telefone Principal")){
			filtro = "telefone1";			
		}else if(s.equals("Telefone Alternativo")){
			filtro = "telefone2";			
		}else if(s.equals("Tratamento")){
			filtro = "tratamento";			
		}else if(s.equals("Nome Fantasia")){
			filtro = "nome_fantasia";			
		}else if(s.equals("Contato")){
			filtro = "contato";			
		}else if(s.equals("Telefone Alternativo 2")){
			filtro = "celular1";			
		}else if(s.equals("Telefone Alternativo 3")){
			filtro = "celular2";			
		}else if(s.equals("Email Principal")){
			filtro = "email";			
		}else if(s.equals("Como nos Conheceu?")){
			filtro = "como_nos_conheceu.nome";			
		}else if(s.equals("Email Alternativo")){
			filtro = "msn";			
		}else if(s.equals("OBS")){
			filtro = "obs";			
		}else if(s.equals("Data de Cadastro")){
			filtro = "data_cadastro";			
		}else if(s.equals("Data de Alteração")){
			filtro = "data_alteracao";			
		}else if(s.equals("Categoria")){
			filtro = "categoria.nome";			
		}else if(s.equals("Como Quer Ser Chamado")){
			filtro = "como_quer_ser_chamado";			
		}else if(s.equals("Status")){
			filtro = "status";			
		}else if(s.equals("CEP")){
			filtro = "endereco_principal.cep";			
		}else if(s.equals("Endereço")){
			filtro = "endereco_principal.endereco";			
		}else if(s.equals("Número")){
			filtro = "endereco_principal.numero";			
		}else if(s.equals("Bairro")){
			filtro = "endereco_principal.bairro";			
		}else if(s.equals("Cidade")){
			filtro = "endereco_principal.cidade";			
		}else if(s.equals("Pais")){
			filtro = "endereco_principal.pais";			
		}else if(s.equals("Complemento")){
			filtro = "endereco_principal.complemento";			
		}else if(s.equals("Referencia")){
			filtro = "endereco_principal.referencia";			
		}else if(s.equals("Tipo Pessoa")){
			filtro = "tipo_pessoa";			
		}
				
		return filtro;
	}

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}
