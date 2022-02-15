package com.digital.opuserp.view.modulos.relatorio.Roterizacao;

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
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.GrupoOse;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.SubGrupoOse;
import com.digital.opuserp.domain.TipoSubGrupoOse;
import com.digital.opuserp.domain.Veiculos;
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

public class ExportarRelatorioOse implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportarRelatorioOse(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista, List<Object> columns)throws Exception {

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
			CriteriaQuery<Ose> criteriaQuery = cb.createQuery(Ose.class);
			Root<Ose> rootCliente = criteriaQuery.from(Ose.class);
			EntityType<Ose> type = em.getMetamodel().entity(Ose.class);

			List<Predicate> criteria = new ArrayList<Predicate>();

			criteria.add(cb.equal(rootCliente.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

			if (lista.size() > 0) {
				for (SearchParameters s : lista) {
					
					if (s.getOperador().equals("IGUAL")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("subgrupo.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("subgrupo").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("grupo.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("grupo").<String>get("nome")), s.getValor().toLowerCase()));
						}						
						if (s.getCampo().equals("tipo_subgrupo.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("tipo_subgrupo").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("veiculo_id.cod_veiculo")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("veiculo_id").<String>get("cod_veiculo")), s.getValor().toLowerCase()));
						}		
						
						if (s.getCampo().equals("contrato.id")) {
							criteria.add(cb.equal(cb.lower(rootCliente.get("contrato").<String>get("id")),s.getValor().toLowerCase()));
						}

						if (!s.getCampo().equals("subgrupo.nome") && 
							!s.getCampo().equals("cliente.nome_razao") && 
							!s.getCampo().equals("grupo.nome")&& 
							!s.getCampo().equals("tipo_subgrupo.nome")&&
							!s.getCampo().equals("veiculo_id.cod_veiculo") &&
							!s.getCampo().equals("contrato.id")) {
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
						if (s.getCampo().equals("subgrupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("subgrupo").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("grupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("grupo").<String>get("nome")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("tipo_subgrupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("tipo_subgrupo").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("veiculo_id.cod_veiculo")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("veiculo_id").<String>get("cod_veiculo")), s.getValor().toLowerCase()));
						}			
						
						if (s.getCampo().equals("contrato.id")) {
							criteria.add(cb.notEqual(cb.lower(rootCliente.get("contrato").<String>get("id")),s.getValor().toLowerCase()));
						}

						if (!s.getCampo().equals("subgrupo.nome") && 
							!s.getCampo().equals("cliente.nome_razao") && 
							!s.getCampo().equals("grupo.nome") && 
							!s.getCampo().equals("tipo_subgrupo.nome") &&
							!s.getCampo().equals("veiculo_id.cod_veiculo") &&
							!s.getCampo().equals("contrato.id")) {		
														
							
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
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("subgrupo.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("subgrupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("grupo.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("grupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						
						if (s.getCampo().equals("tipo_subgrupo.nome")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("tipo_subgrupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}																					
						if (s.getCampo().equals("veiculo_id.cod_veiculo")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("veiculo_id").<String>get("cod_veiculo")), "%" + s.getValor().toLowerCase()+ "%"	));
						}					
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						

						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("subgrupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("subgrupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("grupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("grupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						
						if (s.getCampo().equals("tipo_subgrupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("tipo_subgrupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("veiculo_id.cod_veiculo")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("veiculo_id").<String>get("cod_veiculo")), "%" + s.getValor().toLowerCase()+ "%"));
						}
				

						if (!s.getCampo().equals("subgrupo.nome") && 
								!s.getCampo().equals("cliente.nome_razao") &&
								!s.getCampo().equals("grupo.nome")&& 
								!s.getCampo().equals("tipo_subgrupo.nome") && 
								!s.getCampo().equals("veiculo_id.cod_veiculo")) {
																
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

			if(selectFiltro(order).equals("cliente.nome_razao")){				
				criteriaQuery.orderBy(cb.asc(rootCliente.join("cliente").get("nome_razao")));				
			}else if(selectFiltro(order).equals("subgrupo.nome")){				
				criteriaQuery.orderBy(cb.asc(rootCliente.join("subgrupo").get("nome")));				
			}else if(selectFiltro(order).equals("grupo.nome")){
				criteriaQuery.orderBy(cb.asc(rootCliente.join("grupo").get("nome")));				
			}else if(selectFiltro(order).equals("tipo_subgrupo.nome")){
				criteriaQuery.orderBy(cb.asc(rootCliente.join("tipo_subgrupo").get("nome")));				
			}else if(selectFiltro(order).equals("veiculo_id.cod_veiculo")){
				criteriaQuery.orderBy(cb.asc(rootCliente.join("veiculo_id").get("cod_veiculo")));				
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE ROTERIZAÇÃO",fTitulo);
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

			List<Ose> contas = q.getResultList();	
			
			
			float[] f = new float[columns.size()];
			
			Integer i=0;
			for (Object c : columns) {

				if(selectHeader(c.toString()).equals("CLIENTE")){
					f[i] = (0.65f);		
				}	
				if(selectHeader(c.toString()).equals("CONTRATO")){
				    	f[i] = (0.20f);	
				}
				
				if(selectHeader(c.toString()).equals("CÓD.")){
			    	f[i] = (0.20f);	
			    }
			    if(selectHeader(c.toString()).equals("SUBGRUPO")){
			    	f[i] = (0.28f);	
			    }
			    if(selectHeader(c.toString()).equals("GRUPO")){
			    	f[i] = (0.25f);	
			    }
			    if(selectHeader(c.toString()).equals("T.SUBGRUPO")){
			    	f[i] = (0.27f);	
			    }
			    if(selectHeader(c.toString()).equals("CONTATO")){
			    	f[i] = (0.30f);	
			    }                                         
			    if(selectHeader(c.toString()).equals("STATUS")){
			    	f[i] = (0.20f);	
			    }
			    if(selectHeader(c.toString()).equals("DT.PREVISÃO")){
			    	f[i] = (0.22f);	
			    }				    
			    if(selectHeader(c.toString()).equals("DT.ENCAMINHADO")){
			    	f[i] = (0.27f);	
			    }	
			    if(selectHeader(c.toString()).equals("DT.ABERTURA")){
			    	f[i] = (0.23f);	
			    }	
			    if(selectHeader(c.toString()).equals("DT.CONCLUSÃO")){
			    	f[i] = (0.27f);	
			    }
			    if(selectHeader(c.toString()).equals("DT.FECHAMENTO")){
			    	f[i] = (0.27f);	
			    }	
			    if(selectHeader(c.toString()).equals("CONCLUSÃO")){
			    	f[i] = (1f);	
			    }	   	
				if(selectHeader(c.toString()).equals("INIC. TRAT.")){
					f[i] = (0.35f);	
				}
				 if(selectHeader(c.toString()).equals("TEMP. TRAT.")){
					 f[i] = (0.35f);	
				}	 
		    	 if(selectHeader(c.toString()).equals("MOTI.REAGEND.")){
		    		 f[i] = (0.35f);	
		    	 }
		    	 if(selectHeader(c.toString()).equals("TÉCNICO")){
		    		f[i] = (0.20f);	
		    	 }
		    	 if(selectHeader(c.toString()).equals("PRIORID.")){
		    		f[i] = (0.30f);	 		
		    	 }
				 if(selectHeader(c.toString()).equals("SERVIÇO")){
					f[i] = (0.25f);		 		
				 }
				 if(selectHeader(c.toString()).equals("TIP. ENCAM.")){
					f[i] = (0.35f);	
				 }
				 if(selectHeader(c.toString()).equals("OP. ABERTURA")){
					f[i] = (0.32f);	
				 }
				 if(selectHeader(c.toString()).equals("PLANO")){
					f[i] = (0.20f);	
				 }
				 if(selectHeader(c.toString()).equals("CONCENTR.")){
					f[i] = (0.30f);	
				 }
				 if(selectHeader(c.toString()).equals("BASE")){
					f[i] = (0.25f);	
				 }
				 if(selectHeader(c.toString()).equals("TURNO")){
					f[i] = (0.19f);	
				 }
				 if(selectHeader(c.toString()).equals("ENDEREÇO")){
					 f[i] = (0.20f);	
				 }
				 if(selectHeader(c.toString()).equals("BAIRRO")){
					f[i] = (0.35f);	
				 }
				 if(selectHeader(c.toString()).equals("CIDADE")){
					f[i] = (0.35f);	
				  }
				 if(selectHeader(c.toString()).equals("VEICULO")){
					 f[i] = (0.20f);	
				 }
				 if(selectHeader(c.toString()).equals("MATERIAL")){
					 f[i] = (0.30f);	
				 }
				 if(selectHeader(c.toString()).equals("PROBLEMA")){
					 f[i] = (0.30f);	
				 }
				 if(selectHeader(c.toString()).equals("USO INTERNO")){
					 f[i] = (0.30f);	
				 }
				 if(selectHeader(c.toString()).equals("PROBLEMA")){
					 f[i] = (0.30f);	
				 }
				 if(selectHeader(c.toString()).equals("OPERAD.")){
					 f[i] = (0.25f);	
				 }
				 if(selectHeader(c.toString()).equals("TEMPO DE ATENDIMENTO")){
					 f[i] = (0.25f);	
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
				
				
				for (Ose ose : contas) {
	
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
							metodo = metodo.replace(".id", "");
//							metodo = metodo.replace(".bairro", "");
//							metodo = metodo.replace(".pais", "");
//							metodo = metodo.replace(".complemento", "");
//							metodo = metodo.replace(".referencia", "");
//							metodo = metodo.replace(".endereco", "");
//							metodo = metodo.replace(".numero", "");
//							metodo = metodo.replace(".uf", "");
//							metodo = metodo.replace(".cep", "");
							

							Class cls = ose.getClass();
							
							Method method = cls.getMethod(metodo);
								
								if (method.invoke(ose) instanceof String || method.invoke(ose) instanceof Integer || method.invoke(ose) instanceof Date) {
									
									String valor = method.invoke(ose).toString();
									
									Paragraph valorColuna = null;
									
									 if(c.toString().equals("data_encaminhamento")||c.toString().equals("data_abertura")||c.toString().equals("data_conclusao")||c.toString().equals("data_ex")){
										
										String dateform = dtUtil.parseDataBra(valor);
										valorColuna = new Paragraph(dateform,fCampo);									
									}else{
										valorColuna = new Paragraph(valor,fCampo);									
									}
									
									pCell.addElement(valorColuna);		
						
									
								}else if (method.invoke(ose) instanceof Cliente) {
									
									Cliente cat = (Cliente) method.invoke(ose);
									
									String valorColuna = "";
									
									if(c.toString().equals("cliente.nome_razao")){
										valorColuna = cat.getNome_razao();
									}	
																				
									pCell.addElement(new Phrase(valorColuna, fCampo));								
										
								}else if (method.invoke(ose) instanceof GrupoOse) {
									
									GrupoOse cat = (GrupoOse) method.invoke(ose);
									
									String valorColuna = "";
									
									if(c.toString().equals("grupo.nome")){
										valorColuna = cat.getNome();
									}						
									pCell.addElement(new Phrase(valorColuna, fCampo));			

								}else if (method.invoke(ose) instanceof SubGrupoOse) {
									
									SubGrupoOse cat = (SubGrupoOse) method.invoke(ose);
									
									String valorColuna = "";
									
									if(c.toString().equals("subgrupo.nome")){
										valorColuna = cat.getNome();
									}						
									pCell.addElement(new Phrase(valorColuna, fCampo));			
								
								}else if (method.invoke(ose) instanceof TipoSubGrupoOse) {
									
									TipoSubGrupoOse cat = (TipoSubGrupoOse) method.invoke(ose);
									
									String valorColuna = "";
									
									if(c.toString().equals("tipo_subgrupo.nome")){
										valorColuna = cat.getNome();
									}						
									pCell.addElement(new Phrase(valorColuna, fCampo));			
								
								}else if (method.invoke(ose) instanceof Veiculos) {
									
									Veiculos cat = (Veiculos) method.invoke(ose);
									
									String valorColuna = "";
									
									if(c.toString().equals("veiculo_id.cod_veiculo")){
										valorColuna = cat.getCod_veiculo();
									}						
									pCell.addElement(new Phrase(valorColuna, fCampo));			
								}else if (method.invoke(ose) instanceof AcessoCliente) {
									
									AcessoCliente cat = (AcessoCliente) method.invoke(ose);
									
									String valorColuna = "";
									
									if(c.toString().equals("contrato.id")){
										valorColuna = cat.getId().toString();
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
				

			}

			
			
			if(tipo.equals("COLUNA ÚNICA")){
				for (Ose conta : contas) {
	
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
							metodo = metodo.replace(".id", "");
							
							Class cls = conta.getClass();
							Method method = cls.getMethod(metodo);
	
							if(method.invoke(conta)==null||method.invoke(conta).toString().equals("")){
								pCell.addElement(new Phrase(" ", fConteudo));		
							}	
														
							if (method.invoke(conta) instanceof String || method.invoke(conta) instanceof Integer || method.invoke(conta) instanceof Date) {
								String valor = method.invoke(conta).toString();
																			
								Paragraph valorColuna = null;
								
								 if(c.toString().equals("data_encaminhamento")||c.toString().equals("data_abertura")||c.toString().equals("data_conclusao")||c.toString().equals("data_ex")){
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
								
							}else if (method.invoke(conta) instanceof GrupoOse) {
								
								GrupoOse cat = (GrupoOse) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("grupo.nome")){
									valorColuna = cat.getNome();
								}						
								pCell.addElement(new Phrase(valorColuna, fCampo));			

							}else if (method.invoke(conta) instanceof SubGrupoOse) {
								
								SubGrupoOse cat = (SubGrupoOse) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("subgrupo.nome")){
									valorColuna = cat.getNome();
								}						
								pCell.addElement(new Phrase(valorColuna, fCampo));			
							
							}else if (method.invoke(conta) instanceof TipoSubGrupoOse) {
								
								TipoSubGrupoOse cat = (TipoSubGrupoOse) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("tipo_subgrupo.nome")){
									valorColuna = cat.getNome();
								}						
								pCell.addElement(new Phrase(valorColuna, fCampo));	
								
							}else if (method.invoke(conta) instanceof Veiculos) {
								
								Veiculos cat = (Veiculos) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("veiculo_id.cod_veiculo")){
									valorColuna = cat.getCod_veiculo();
								}						
								pCell.addElement(new Phrase(valorColuna, fCampo));			
							}else if (method.invoke(conta) instanceof AcessoCliente) {
								
								AcessoCliente cat = (AcessoCliente) method.invoke(conta);
								
								String valorColuna = "";
								
								if(c.toString().equals("contrato.id")){
									valorColuna = cat.getId().toString();
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
			
			
			CriteriaQuery<Ose> criteriaQueryGroup = cb.createQuery(Ose.class);
			Root<Ose> rootGroup = criteriaQueryGroup.from(Ose.class);
			
			
			if (selectFiltro(resumo).equals("cliente.nome_razao")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("nome_razao");			
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("nome_razao"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("grupo.nome")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("grupo").get("nome");				
				criteriaQueryGroup.groupBy(rootGroup.join("grupo").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("subgrupo.nome")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("subgrupo").get("nome");			
				criteriaQueryGroup.groupBy(rootGroup.join("subgrupo").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("tipo_subgrupo.nome")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("tipo_subgrupo").get("nome");				
				criteriaQueryGroup.groupBy(rootGroup.join("tipo_subgrupo").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
			}
			if (selectFiltro(resumo).equals("veiculo_id.cod_veiculo")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("veiculo_id").get("cod_veiculo");				
				criteriaQueryGroup.groupBy(rootGroup.join("veiculo_id").get("cod_veiculo"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("contrato.id")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<Integer> coluna = rootGroup.get("contrato").get("id");				
				criteriaQueryGroup.groupBy(rootGroup.join("contrato").get("id"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
			}

			

			if (!selectFiltro(resumo).equals("veiculo_id.cod_veiculo") && 
					!selectFiltro(resumo).equals("grupo.nome") && 
					!selectFiltro(resumo).equals("cliente.nome_razao") && 
					!selectFiltro(resumo).equals("subgrupo.nome") && 
					!selectFiltro(resumo).equals("tipo_subgrupo.nome") && 
					!selectFiltro(resumo).equals("contrato.id")) {
						
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
				criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
				
			}

			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);	
	
			for (Ose c :(List<Ose>) qGroup.getResultList()) {

				Paragraph pResum = new Paragraph();
				
				if(selectFiltro(resumo).equals("cliente.nome_razao")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);	
					
				}else if(selectFiltro(resumo).equals("grupo.nome")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);		
				
				}else if(selectFiltro(resumo).equals("subgrupo.nome")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					
				}else if(selectFiltro(resumo).equals("tipo_subgrupo.nome")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					
				}else if(selectFiltro(resumo).equals("veiculo_id.cod_veiculo")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);
				}else if(selectFiltro(resumo).equals("contrato.id")){
					if(c.getContrato() != null){
						pResum = new Paragraph(c.getContrato().getId().toString(), fCaptionsBold);
					}else{
						pResum = new Paragraph("", fCaptionsBold);
					}
				}else{
				
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(String.class)){
						pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					}
					
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Date.class)){
						SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						//SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy ");
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
			filtro = "CÓD.";					
		}else if(s.equals("subgrupo.nome")){
			filtro = "SUBGRUPO";					
		}else if(s.equals("grupo.nome")){
			filtro = "GRUPO";					
		}else if(s.equals("tipo_subgrupo.nome")){
			filtro = "T.SUBGRUPO";				
		}else if(s.equals("contato")){
			filtro = "CONTATO";					
		}else if(s.equals("status")){
			filtro = "STATUS";					
		}else if(s.equals("data_ex")){
			filtro = "DT.PREVISÃO";	
		}else if(s.equals("data_encaminhamento")){
			filtro = "DT.ENCAMINHADO";	
		}else if(s.equals("data_abertura")){
			filtro = "DT.ABERTURA";	
		}else if(s.equals("data_conclusao")){
			filtro = "DT.CONCLUSÃO";								
		}else if(s.equals("data_fechamento")){
			filtro = "DT.FECHAMENTO";								
		}else if(s.equals("data_inicio_tratamento")){
			filtro = "INIC. TRAT.";					
		}else if(s.equals("tempo_total_tratamento")){
			filtro = "TEMP. TRAT.";					
		}else if(s.equals("motivo_reagendamento")){
			filtro = "MOTI.REAGEND.";					
		}else if(s.equals("operador")){
			filtro = "OPERAD.";							
		}else if(s.equals("conclusao")){
			filtro = "CONCLUSÃO";		
		}else if(s.equals("tecnico")){
			filtro = "TÉCNICO";		
		}else if(s.equals("ausente")){
			filtro = "AUSENTE";		
		}else if(s.equals("prioridade")){
			filtro = "PRIORID.";		
		}else if(s.equals("nota_fiscal")){
			filtro = "N FISC.";		
		}else if(s.equals("tipo_servico")){
			filtro = "SERVIÇO";		
		}else if(s.equals("problema")){
			filtro = "PROBLEMA";		
		}else if(s.equals("uso_interno")){
			filtro = "USO INTERNO";		
		}else if(s.equals("veiculo_id.cod_veiculo")){
			filtro = "VEICULO";		
		}else if(s.equals("operador_abertura")){
			filtro = "OP. ABERTURA";		
		}else if(s.equals("material")){
			filtro = "MATERIAL";		
		}else if(s.equals("plano")){
			filtro = "PLANO";		
		}else if(s.equals("concentrador")){
			filtro = "CONCENTR.";			
		}else if(s.equals("base")){
			filtro = "BASE";	
		}else if(s.equals("turno")){
			filtro = "TURNO";						
		}else if(s.equals("endereco")){
			filtro = "ENDEREÇO";					
		}else if(s.equals("bairro")){
			filtro = "BAIRRO";			
		}else if(s.equals("cidade")){
			filtro = "CIDADE";			
		}else if(s.equals("tempo_atendimento")){
			filtro = "TEMPO DE ATENDIMENTO";			
		}else if(s.equals("contrato.id")){
			filtro = "CONTRATO";			
		}
		
		return filtro;
	}
	
	public String selectUpHeader(String s) {
		
		String filtro = "";
		if(s.equals("cliente.nome_razao")){
			filtro = "CLIENTE";							
		}else if(s.equals("id")){
			filtro = "CÓDIGO";					
		}else if(s.equals("subgrupo.nome")){
			filtro = "SUBGRUPO";					
		}else if(s.equals("grupo.nome")){
			filtro = "GRUPO";					
		}else if(s.equals("tipo_subgrupo.nome")){
			filtro = "TIPO SUBGRUPO";				
		}else if(s.equals("contato")){
			filtro = "CONTATO";					
		}else if(s.equals("status")){
			filtro = "STATUS";					
		}else if(s.equals("data_ex")){
			filtro = "DATA PREVISÃO";	
		}else if(s.equals("data_encaminhamento")){
			filtro = "DATA ENCAMINHADO";	
		}else if(s.equals("data_abertura")){
			filtro = "DATA ABERTURA";	
		}else if(s.equals("data_conclusao")){
			filtro = "DATA CONCLUÃO";								
		}else if(s.equals("data_fechamento")){
			filtro = "Data Fechamento";			
		}else if(s.equals("data_inicio_tratamento")){
			filtro = "DATA INICIO TRATAMENTO";					
		}else if(s.equals("tempo_total_tratamento")){
			filtro = "TEMPO TOTAL TRATAMENTO";					
		}else if(s.equals("motivo_reagendamento")){
			filtro = "MOTIVO REAGENDAMENTO";					
		}else if(s.equals("operador")){
			filtro = "OPERADOR";							
		}else if(s.equals("conclusao")){
			filtro = "CONCLUSÃO";		
		}else if(s.equals("tecnico")){
			filtro = "TÉCNICO";		
		}else if(s.equals("ausente")){
			filtro = "AUSENTE";		
		}else if(s.equals("prioridade")){
			filtro = "PRIORIDADE";		
		}else if(s.equals("nota_fiscal")){
			filtro = "NOTA FISCAL";		
		}else if(s.equals("tipo_servico")){
			filtro = "SERVIÇO";		
		}else if(s.equals("problema")){
			filtro = "PROBLEMA";		
		}else if(s.equals("uso_interno")){
			filtro = "USO INTERNO";		
		}else if(s.equals("veiculo_id.cod_veiculo")){
			filtro = "VEICULO";		
		}else if(s.equals("operador_abertura")){
			filtro = "OPERADOR ABERTURA";		
		}else if(s.equals("material")){
			filtro = "MATERIAL";		
		}else if(s.equals("plano")){
			filtro = "PLANO";		
		}else if(s.equals("concentrador")){
			filtro = "CONCENTRADOR";			
		}else if(s.equals("base")){
			filtro = "BASE";	
		}else if(s.equals("turno")){
			filtro = "TURNO";				
		}else if(s.equals("end.cep")){
			filtro = "CEP";			
		}else if(s.equals("endereco")){
			filtro = "ENDEREÇO";			
		}else if(s.equals("end.numero")){
			filtro = "Nº";			
		}else if(s.equals("bairro")){
			filtro = "BAIRRO";			
		}else if(s.equals("cidade")){
			filtro = "CIDADE";			
		}else if(s.equals("end.uf")){
			filtro = "UF";			
		}else if(s.equals("end.pais")){
			filtro = "PAIS";			
		}else if(s.equals("end.complemento")){
			filtro = "COMPLEMENTO";			
		}else if(s.equals("end.referencia")){
			filtro = "REFERENCIA";			
		}else if(s.equals("tempo_atendimento")){
			filtro = "TEMPO DE ATENDIMENTO";			
		}else if(s.equals("contrato.id")){
			filtro = "CONTRATO";			
		}
		
		return filtro;
	}

	public String selectFiltro(String s) {
		
		String filtro = "";		
		if(s.equals("Cliente")){
			filtro = "cliente.nome_razao";							
		}else if(s.equals("Código")){
			filtro = "id";					
		}else if(s.equals("SubGrupo")){
			filtro = "subgrupo.nome";					
		}else if(s.equals("Grupo")){
			filtro = "grupo.nome";					
		}else if(s.equals("Tipo SubGrupo")){
			filtro = "tipo_subgrupo.nome";				
		}else if(s.equals("Contato")){
			filtro = "contato";					
		}else if(s.equals("Status")){
			filtro = "status";					
		}else if(s.equals("Data Previsão")){
			filtro = "data_ex";	
		}else if(s.equals("Data Encaminhado")){
			filtro = "data_encaminhamento";	
		}else if(s.equals("Data Abertura")){
			filtro = "data_abertura";	
		}else if(s.equals("Data Conclusão")){
			filtro = "data_conclusao";							
		}else if(s.equals("Data Fechamento")){
			filtro = "data_fechamento";			
		}else if(s.equals("Data Inicio Tratamento")){
			filtro = "data_inicio_tratamento";					
		}else if(s.equals("Tempo Total Tratamento")){
			filtro = "tempo_total_tratamento";					
		}else if(s.equals("Motivo Reagendamento")){
			filtro = "motivo_reagendamento";					
		}else if(s.equals("Operador")){
			filtro = "operador";							
		}else if(s.equals("Conclusão")){
			filtro = "conclusao";		
		}else if(s.equals("Técnico")){
			filtro = "tecnico";		
		}else if(s.equals("Ausente")){
			filtro = "ausente";		
		}else if(s.equals("Prioridade")){
			filtro = "prioridade";		
		}else if(s.equals("Nota Fiscal")){
			filtro = "nota_fiscal";		
		}else if(s.equals("Serviço")){
			filtro = "tipo_servico";		
		}else if(s.equals("Problema")){
			filtro = "problema";		
		}else if(s.equals("Uso Interno")){
			filtro = "uso_interno";		
		}else if(s.equals("Veiculo")){
			filtro = "veiculo_id.cod_veiculo";
		}else if(s.equals("Operador Abertura")){
			filtro = "operador_abertura";		
		}else if(s.equals("Material")){
			filtro = "material";		
		}else if(s.equals("Plano")){
			filtro = "plano";		
		}else if(s.equals("Concentrador")){
			filtro = "concentrador";			
		}else if(s.equals("Base")){
			filtro = "base";	
		}else if(s.equals("Turno")){
			filtro = "turno";	
		}else if(s.equals("CEP")){
			filtro = "end.cep";			
		}else if(s.equals("Endereço")){
			filtro = "endereco";			
		}else if(s.equals("Número")){
			filtro = "end.numero";			
		}else if(s.equals("Bairro")){
			filtro = "bairro";			
		}else if(s.equals("Cidade")){
			filtro = "cidade";			
		}else if(s.equals("Pais")){
			filtro = "end.pais";			
		}else if(s.equals("Complemento")){
			filtro = "end.complemento";			
		}else if(s.equals("Referencia")){
			filtro = "end.referencia";	
		}else if(s.equals("Tempo de Atendimento")){
			filtro = "tempo_atendimento";			
		}else if(s.equals("Contrato")){
			filtro = "contrato.id";			
		}
				
		return filtro;
	}	

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}

