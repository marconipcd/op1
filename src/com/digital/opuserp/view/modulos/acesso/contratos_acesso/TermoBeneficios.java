package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class TermoBeneficios implements StreamSource{

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public TermoBeneficios(Integer codAc) throws Exception {
		
		EntityManager em = ConnUtil.getEntity();
		
		//Document doc = new Document(PageSize.A4, 2f, 2f, 1f, 1f);	
		//doc.setMargins(20, 20, 4, 4);
		
		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);	
		doc.setMargins(24, 24, 22, 22);
		
		PdfWriter pdfWriter = null;
		try{
			pdfWriter = PdfWriter.getInstance(doc, baos);
			doc.open();
			
			HTMLWorker htmlWorker = new HTMLWorker(doc);
			
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			AcessoCliente acessoCliente = em.find(AcessoCliente.class, codAc);
			Cliente cliente = acessoCliente.getCliente();

			String controle =  "ACESSO-POS";
			Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
			qControle.setParameter("nome", controle);
			qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
											
			ContaBancaria cb = null;
			if(qControle.getResultList().size() ==1){
				cb = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
			}
			
			
			Endereco end = acessoCliente.getEndereco();
			URL urlFont  = getClass().getResource("/com/digital/opuserp/font/Arial Narrow.ttf");
			BaseFont base8 = BaseFont.createFont(urlFont.toString(), BaseFont.WINANSI,true);
			Font f8 = new Font(base8, 8f);
			Font f9 = new Font(base8, 9f);
				
			byte[] logo = empresa.getLogo_empresa();		
			
//			if(logo != null){
//				Image imgLogo = Image.getInstance(logo);				
//				imgLogo.setAlignment(Element.ALIGN_CENTER);
//				imgLogo.scalePercent(65.0f);
//								
//				doc.add(imgLogo);			
//			}
			
			if(logo != null){
				Image imgLogo = Image.getInstance(logo);				
				imgLogo.setAlignment(Element.ALIGN_CENTER);
				
				imgLogo.scalePercent(45.0f);
					
				PdfPCell pCellLogo1 = new PdfPCell();
		        pCellLogo1.addElement(new Phrase(""));
		        pCellLogo1.setPaddingBottom(2);
		        pCellLogo1.setPaddingTop(0);
		        pCellLogo1.setBorder(0);
		        
			
				PdfPCell pCellLogo = new PdfPCell();
		        pCellLogo.addElement(imgLogo);
		        pCellLogo.setPaddingBottom(2);
		        pCellLogo.setPaddingTop(2);
		        pCellLogo.setBorder(0);
		        
		        
		        DateTime ano  =new DateTime(acessoCliente.getData_cadastro());
		        
		        PdfPCell pCellCodDireita = new PdfPCell();
		        pCellCodDireita.addElement(new Phrase("Nº "+acessoCliente.getId().toString()+" / "+String.valueOf(ano.getYear()),f9));		        
		        pCellCodDireita.setPaddingBottom(8);
		        pCellCodDireita.setPaddingTop(1);
		        pCellCodDireita.setPaddingLeft(25);
		        
		        
		        PdfPTable tbTopoDireitaCod = new PdfPTable(new float[] {1f});
		        tbTopoDireitaCod.setWidthPercentage(100f);			 
		        tbTopoDireitaCod.addCell(pCellCodDireita);   
		        //tbTopoDireitaCod.setSpacingBefore(10);				 
			     
		        
		        PdfPCell pCellCod = new PdfPCell();
		        pCellCod.addElement(tbTopoDireitaCod);
		        pCellCod.setPaddingBottom(0);
		        pCellCod.setPaddingTop(0);
		        pCellCod.setBorder(0);
		        				
				 PdfPTable tbTopo = new PdfPTable(new float[] {0.30f,1f,0.30f});
				 tbTopo.setWidthPercentage(100f);			 
				 tbTopo.addCell(pCellLogo1);
				 tbTopo.addCell(pCellLogo);
				 tbTopo.addCell(pCellCod);		        
				 tbTopo.setSpacingBefore(10);				 
			     doc.add(tbTopo);	     
			}
				
			Font f12 = new Font(base8, 11f);			
			Font f12b = new Font(base8, 11f, Font.BOLD);			
			Font f12bUnder = new Font(base8, 11f, Font.BOLD | Font.UNDERLINE);

			// Fones Enpresa
			StringBuilder fonesEmp = new StringBuilder();
			if(empresa.getDdd_fone1()!=null && empresa.getFone1()!=null){
				fonesEmp.append(empresa.getDdd_fone1()+" "+empresa.getFone1());				
			}else if(empresa.getFone1()!=null){
				fonesEmp.append(empresa.getFone1());		
			}
			
			if(!empresa.getDdd_fone2().isEmpty() && empresa.getDdd_fone2()!=null && !empresa.getFone2().isEmpty() && empresa.getFone2()!=null && !fonesEmp.toString().isEmpty()){
				fonesEmp.append(" / "+empresa.getDdd_fone2()+" "+empresa.getFone2());				
			}else if(empresa.getDdd_fone2()!=null && empresa.getFone2()!=null){	
				fonesEmp.append(empresa.getDdd_fone2()+" "+empresa.getFone2());		
			}else if(empresa.getFone2()!=null){
				fonesEmp.append(empresa.getFone2());
			}
			
			if(empresa.getDdd_fone3()!=null && !empresa.getFone3().isEmpty() && empresa.getFone3()!=null && !fonesEmp.toString().isEmpty()){
				fonesEmp.append(" / "+empresa.getDdd_fone3()+" "+empresa.getFone3());				
			}else if(empresa.getDdd_fone3()!=null && empresa.getFone3()!=null){	
				fonesEmp.append(empresa.getDdd_fone3()+" "+empresa.getFone3());		
			}else if(empresa.getFone3()!=null){
				fonesEmp.append(empresa.getFone3());
			}
			
			if(!empresa.get_0800().isEmpty() && empresa.get_0800()!=null && !fonesEmp.toString().isEmpty()){
				fonesEmp.append(" /  "+empresa.get_0800());				
			}else if(empresa.get_0800()!=null){	
				fonesEmp.append(empresa.get_0800());		
			}

			// CABECALHO
			Paragraph pRazaoSocial = new Paragraph("CNPJ: "+empresa.getCnpj().toString()+"   "+empresa.getEndereco().toString()
														   + ", "+ empresa.getNumero().toString()+" "+empresa.getBairro().toString()
														   +" "+empresa.getCidade().toString()+"-"+empresa.getUf().toString()
														   +" CEP: "+empresa.getCep().toString(),f8);
			pRazaoSocial.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph pComecial = new Paragraph(" FONE: "+ fonesEmp+ " - " + empresa.getEmail().toString()+" - "+empresa.getSite().toString(),f8);
			pComecial.setAlignment(Element.ALIGN_CENTER);
								
			//doc.add(pRazaoSocial);
			//doc.add(pComecial);
		
			DataUtil dtUtil = new DataUtil();
				String date = null;
				
				if(acessoCliente.getData_alteracao_plano()!=null){
					date = dtUtil.getDataExtensoMes(acessoCliente.getData_alteracao_plano());
				}else{
					date = dtUtil.getDataExtensoMes(acessoCliente.getData_cadastro());				
				}

			
			 Paragraph pTerm = new Paragraph("TERMO DE CONCESSÃO CONDICIONAL DE BENEFÍCIOS X FIDELIDADE",f12b);
			 pTerm.setSpacingAfter(2);
			 pTerm.setSpacingBefore(3);
			 pTerm.setAlignment(Element.ALIGN_CENTER);
			 
			 doc.add(pTerm);
			 
			PdfPCell pCellVazio2= new PdfPCell(new Paragraph(" "));
			pCellVazio2.setBorder(0);

			Paragraph pTopo = new Paragraph(); 
			pTopo.add(new Phrase("O presente instrumento, de um lado a doravante ",f12));
			pTopo.add(new Phrase("DIGITAL TECNOLOGIA & TELECOMUNICAÇÃO LTDA - EPP ",f12b));
			pTopo.add(new Phrase("denominada ",f12));
			pTopo.add(new Phrase("PRESTADORA, ",f12b));
			pTopo.add(new Phrase("qualificada no Contrato de Prestação de Serviço de Comunicação Multimídia devidamente identificado na cláusula 1.1 do  ",f12));
			pTopo.add(new Phrase("TERMO DE ADESÃO ",f12b));
			pTopo.add(new Phrase("e de outro lado, o ",f12));
			pTopo.add(new Phrase("ASSINANTE Sr. (a) "+cliente.getNome_razao()+" ",f12b));
			pTopo.add(new Phrase("devidamente qualificado no termo de adesão nº ",f12));
			pTopo.add(new Phrase(acessoCliente.getId().toString()+" ",f12b));
			pTopo.add(new Phrase("assinado em  ",f12));
			pTopo.add(new Phrase(DataUtil.formatDateBra(acessoCliente.getData_instalacao())+".",f12b));
			pTopo.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pTopo);
			  

		        
		        Paragraph pClausla = new Paragraph("CLÁUSULA PRIMEIRA – DAS CONSIDERAÇÕES PRELIMINARES",f12bUnder);
		        pClausla.setAlignment(Element.ALIGN_CENTER);		    
		        pClausla.setSpacingAfter(3);
		        pClausla.setSpacingBefore(2);
		        doc.add(pClausla);
		        
		        Paragraph pClausla1_1 = new Paragraph();	        
		        pClausla1_1.add(new Phrase("1.1   ",f12b));
		        pClausla1_1.add(new Phrase("O presente ",f12));
		        pClausla1_1.add(new Phrase("TERMO DE CONCESSÃO DE BENEFÍCIOS ",f12b));
		        pClausla1_1.add(new Phrase("encontra-se em consonância com o ",f12));
		        pClausla1_1.add(new Phrase("CONTRATO DE PRESTAÇÃO DE SERVIÇOS DE COMUNICAÇÃO MULTIMÍDIA (SCM), ",f12b));
		        pClausla1_1.add(new Phrase("e acessórios se houver, com seu respectivo ",f12));
		        pClausla1_1.add(new Phrase("TERMO DE ADESÃO. ",f12b));
		        pClausla1_1.add(new Phrase("Todos estes instrumentos formalizados entre as partes, em conjunto, formam um só instrumento para os fins de direito e devem ser lidos e interpretados conjuntamente.",f12));
		        pClausla1_1.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausla1_1);
		        
		        Paragraph pClausla1_2 = new Paragraph();	        
		        pClausla1_2.add(new Phrase("1.2   ",f12b));
		        pClausla1_2.add(new Phrase("Foram apresentados ao ",f12));
		        pClausla1_2.add(new Phrase("ASSINANTE ",f12b));
		        pClausla1_2.add(new Phrase("determinados benefícios antes da contratação dos ",f12));
		        pClausla1_2.add(new Phrase("SERVIÇOS DE COMUNICAÇÃO MULTIMÍDIA (SCM), ",f12b));
		        pClausla1_2.add(new Phrase("tendo como contrapartida a ",f12));
		        pClausla1_2.add(new Phrase("FIDELIZAÇÃO ",f12b));
		        pClausla1_2.add(new Phrase("do ",f12));
		        pClausla1_2.add(new Phrase("ASSINANTE ",f12b));
		        pClausla1_2.add(new Phrase("pelo prazo descrito neste instrumento, tendo também sido apresentados ao ",f12));
		        pClausla1_2.add(new Phrase("ASSINANTE ",f12b));
		        pClausla1_2.add(new Phrase("todas as condições relacionadas a esta ",f12));
		        pClausla1_2.add(new Phrase("FIDELIDADE, ",f12b));
		        pClausla1_2.add(new Phrase("inclusive no que se refere às penalidades decorrentes da rescisão contratual antecipada.",f12));
		        pClausla1_2.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausla1_2);
		        
		        Paragraph pClausla1_3 = new Paragraph();	        
		        pClausla1_3.add(new Phrase("1.3   ",f12b));
		        pClausla1_3.add(new Phrase("O ",f12));
		        pClausla1_3.add(new Phrase("ASSINANTE ",f12b));
		        pClausla1_3.add(new Phrase("optou livremente pela percepção dos benefícios e, por conseguinte, pela contratação sob a condição de fidelidade contratual, tendo total e amplo conhecimento das consequências decorrentes da fidelização contratual, bem como das penalidades decorrentes da fidelização contratual, bem como das penalidades decorrentes da rescisão contratual antecipada, sendo facultado ao ",f12));
		        pClausla1_3.add(new Phrase("ASSINANTE ",f12b));
		        pClausla1_3.add(new Phrase("pela celebração de um contrato com a ",f12));
		        pClausla1_3.add(new Phrase("PRESTADORA ",f12b));
		        pClausla1_3.add(new Phrase("sem a percepção de qualquer benefício. ",f12));
		        pClausla1_3.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausla1_3);
		        
		        
		        //doc.add(Chunk.NEXTPAGE);
		        
		        Paragraph pClausla2 = new Paragraph("CLÁUSULA SEGUNDA – DO BENEFÍCIO CONCEDIDOS AO ASSINANTE",f12bUnder);
		        pClausla2.setAlignment(Element.ALIGN_CENTER);		        
		        pClausla2.setSpacingAfter(5);
		        pClausla2.setSpacingBefore(2);
		        doc.add(pClausla2);
		        
		        Paragraph pClausla2_1 = new Paragraph();	        
		        pClausla2_1.add(new Phrase("2.1   ",f12b));
		        pClausla2_1.add(new Phrase("Conforme contrato formalizado entre as partes, a  ",f12));
		        pClausla2_1.add(new Phrase("PRESTADORA  ",f12b));
		        pClausla2_1.add(new Phrase("concede ao ",f12));
		        pClausla2_1.add(new Phrase("ASSINANTE ",f12b));
		        pClausla2_1.add(new Phrase("o seguinte benefício:",f12));
		        pClausla2_1.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausla2_1);
		        
		        PdfPCell pCellVazioCheck = new PdfPCell();
		        pCellVazioCheck.addElement(new Phrase("[ X ]",f12));		        
		        pCellVazioCheck.setPaddingBottom(4);
		        pCellVazioCheck.setPaddingTop(0);
		        
		        PdfPCell pCellVazioCheck11 = new PdfPCell();
		        pCellVazioCheck11.addElement(new Phrase("[   ]",f12));		        
		        pCellVazioCheck11.setPaddingBottom(4);
		        pCellVazioCheck11.setPaddingTop(0);
		        
		        PdfPCell pCellVazioCheck2 = new PdfPCell();
		        if(acessoCliente.isBeneficio_comodato()){
		        	pCellVazioCheck2.addElement(new Phrase("[ X ]",f12));
		        }else{
		        	pCellVazioCheck2.addElement(new Phrase("[ X ]",f12));
		        }
		        pCellVazioCheck2.setPaddingBottom(2);
		        pCellVazioCheck2.setPaddingTop(0);
		        
		        Paragraph pDescricaoBeneficio = new Paragraph();
		        pDescricaoBeneficio.add(new Phrase("DESCRIÇÃO DO BENEFICIO",f12b));
		        pDescricaoBeneficio.setAlignment(Element.ALIGN_LEFT);
		        
		        PdfPCell pCellDescBeneficio = new PdfPCell();
		        pCellDescBeneficio.addElement(pDescricaoBeneficio);
		        pCellDescBeneficio.setBackgroundColor(new BaseColor(232, 235, 237));
		        pCellDescBeneficio.setPaddingBottom(4);
		        pCellDescBeneficio.setPaddingTop(0);
		        
		        Paragraph pValorBeneficio = new Paragraph();
		        pValorBeneficio.add(new Phrase("VALOR DO BENEFICIO",f12b));
		        pValorBeneficio.setAlignment(Element.ALIGN_LEFT);
		        
		        PdfPCell pCellValorBeneficio = new PdfPCell();
		        pCellValorBeneficio.addElement(pValorBeneficio);
		        pCellValorBeneficio.setBackgroundColor(new BaseColor(232, 235, 237));
		        pCellValorBeneficio.setPaddingBottom(2);
		        pCellValorBeneficio.setPaddingTop(0);
		        
//		        Paragraph pDescricaoBeneficioValor = new Paragraph();
//		        pDescricaoBeneficioValor.add(new Phrase("Isenção da Taxa de Adesão",f12));
//		        pDescricaoBeneficioValor.setAlignment(Element.ALIGN_LEFT);
		        
		        Paragraph pDescricaoBeneficioIsencaoTaxaInstValor = new Paragraph();
		        pDescricaoBeneficioIsencaoTaxaInstValor.add(new Phrase("Isenção da Taxa de Instalação",f12));
		        pDescricaoBeneficioIsencaoTaxaInstValor.setAlignment(Element.ALIGN_LEFT);
		        
//		        PdfPCell pCellDescBeneficioValor = new PdfPCell();
//		        pCellDescBeneficioValor.addElement(pDescricaoBeneficioValor);		        
//		        pCellDescBeneficioValor.setPaddingBottom(4);
//		        pCellDescBeneficioValor.setPaddingTop(0);
		        
		        PdfPCell pCellDescBeneficioIsencaoInst= new PdfPCell();
		        pCellDescBeneficioIsencaoInst.addElement(pDescricaoBeneficioIsencaoTaxaInstValor);		        
		        pCellDescBeneficioIsencaoInst.setPaddingBottom(4);
		        pCellDescBeneficioIsencaoInst.setPaddingTop(0);
		        	        
//		        Paragraph pValorBeneficioValor = new Paragraph();
//		        pValorBeneficioValor.add(new Phrase("R$ "+Real.formatDbToString(String.valueOf(acessoCliente.getValor_beneficio_adesao())),f12));
//		        pValorBeneficioValor.setAlignment(Element.ALIGN_LEFT);
//		        
//		        PdfPCell pCellValorBeneficioValor = new PdfPCell();
//		        pCellValorBeneficioValor.addElement(pValorBeneficioValor);
		        
//		        pCellValorBeneficioValor.setPaddingBottom(2);
//		        pCellValorBeneficioValor.setPaddingTop(0);
		        
		        
//		        String bi = "0,00";
//				Query qValorBenfInst = em.createNativeQuery("SELECT ts.VALOR FROM ose o, tipos_ose g, tipos_subgrupo ts WHERE o.GRUPO_ID = g.ID AND ts.ID = "
//						+ "o.TIPO_SUBGRUPO_ID AND g.NOME LIKE '%INSTALACAO%' AND o.ACESSO_CLIENTE_ID = :contrato ");
//				qValorBenfInst.setParameter("contrato", acessoCliente.getId());
//				
//				if(qValorBenfInst.getResultList().size() > 0){
//					bi = Real.formatDbToString(qValorBenfInst.getResultList().toArray()[0].toString());
//				}		        
		        
		        Paragraph pValorBeneficioValorInst = new Paragraph();
		        if(acessoCliente.getInstalacao_gratis() != null && acessoCliente.getInstalacao_gratis().equals("SIM")){
		        	
		        	if(acessoCliente.getCarencia() != null && acessoCliente.getCarencia().equals("SIM")){		        	
			        	String ti = acessoCliente.getIsencao_taxa_instalacao() > 0 ? "R$ "+Real.formatDbToString(String.valueOf(acessoCliente.getIsencao_taxa_instalacao())) : acessoCliente.getContrato().getIsencao_taxa_instalacao();
			        	pValorBeneficioValorInst.add(new Phrase(ti,f12));
		        	}else{
		        		pValorBeneficioValorInst.add(new Phrase("R$ 0,00",f12));
		        	}		        	
		        	
		        }else{
		        	pValorBeneficioValorInst.add(new Phrase("R$ 0,00",f12));
		        }
		        
		        pValorBeneficioValorInst.setAlignment(Element.ALIGN_LEFT);
		        
		        PdfPCell pCellValorBeneficioValorInst = new PdfPCell();
		        pCellValorBeneficioValorInst.addElement(pValorBeneficioValorInst);		        
		        pCellValorBeneficioValorInst.setPaddingBottom(4);
		        pCellValorBeneficioValorInst.setPaddingTop(0);
		        
		        Paragraph pDescricaoBeneficioValor2 = new Paragraph();
		        pDescricaoBeneficioValor2.add(new Phrase("Equipamento (se regime em Comodato)",f12));
		        pDescricaoBeneficioValor2.setAlignment(Element.ALIGN_LEFT);
		        
		        PdfPCell pCellDescBeneficioValor2 = new PdfPCell();
		        pCellDescBeneficioValor2.addElement(pDescricaoBeneficioValor2);		        
		        pCellDescBeneficioValor2.setPaddingBottom(4);
		        pCellDescBeneficioValor2.setPaddingTop(0);
		        		       		        
		        Paragraph pValorBeneficioValor2 = new Paragraph();
		      //  if(acessoCliente.isBeneficio_comodato()){
		        	pValorBeneficioValor2.add(new Phrase("R$ "+Real.formatDbToString(String.valueOf(acessoCliente.getValor_beneficio_comodato())),f12));
//		        }else{
//		        	pValorBeneficioValor2.add(new Phrase("R$ 0,00",f12));
//		        }
		        pValorBeneficioValor2.setAlignment(Element.ALIGN_LEFT);
		        
		        PdfPCell pCellValorBeneficioValor2 = new PdfPCell();
		        pCellValorBeneficioValor2.addElement(pValorBeneficioValor2);		        
		        pCellValorBeneficioValor2.setPaddingBottom(4);
		        pCellValorBeneficioValor2.setPaddingTop(0);
		        
		        PdfPTable tbValorLinha2 = new PdfPTable(new float[] {0.10f,0.80f,0.80f});
		        tbValorLinha2.setWidthPercentage(100f);
		        tbValorLinha2.addCell(pCellVazio2);
		        tbValorLinha2.addCell(pCellDescBeneficio);
		        tbValorLinha2.addCell(pCellValorBeneficio);		        
		        tbValorLinha2.setSpacingBefore(10);
		        doc.add(tbValorLinha2);	        
		       
//		        PdfPTable tbValorLinha4 = new PdfPTable(new float[] {0.10f,0.80f,0.80f});
//		        tbValorLinha4.setWidthPercentage(100f);
//		        tbValorLinha4.addCell(pCellVazioCheck);
//		        tbValorLinha4.addCell(pCellDescBeneficioValor);
//		        tbValorLinha4.addCell(pCellValorBeneficioValor);		        
//		        doc.add(tbValorLinha4);
		        
		        PdfPTable tbValorLinha51 = new PdfPTable(new float[] {0.10f,0.80f,0.80f});
		        tbValorLinha51.setWidthPercentage(100f);
		        if(acessoCliente.getInstalacao_gratis() != null && acessoCliente.getInstalacao_gratis().equals("SIM")){		        	
		        	tbValorLinha51.addCell(pCellVazioCheck);
		        }else{
		        	tbValorLinha51.addCell(pCellVazioCheck11);
		        }
		        tbValorLinha51.addCell(pCellDescBeneficioIsencaoInst);
		        tbValorLinha51.addCell(pCellValorBeneficioValorInst);		        
		        doc.add(tbValorLinha51);
		        
		        PdfPTable tbValorLinha5 = new PdfPTable(new float[] {0.10f,0.80f,0.80f});
		        tbValorLinha5.setWidthPercentage(100f);
		        tbValorLinha5.addCell(pCellVazioCheck2);
		        tbValorLinha5.addCell(pCellDescBeneficioValor2);
		        tbValorLinha5.addCell(pCellValorBeneficioValor2);		        
		        doc.add(tbValorLinha5);
		        
		        
		        
		        
		        PdfPCell pCellCheckManutenServ = new PdfPCell();
//		        if(acessoCliente.getPrest_serv_manutecao() != null && acessoCliente.getPrest_serv_manutecao().equals("SIM")){
		        	pCellCheckManutenServ.addElement(new Phrase("[ X ]",f12));
//		        }else{
//		        	pCellCheckManutenServ.addElement(new Phrase("[    ]",f12));
//		        }
		        pCellCheckManutenServ.setPaddingBottom(2);
		        pCellCheckManutenServ.setPaddingTop(0);
		        		        
		        PdfPCell pCellDescPrestServi = new PdfPCell();
		        pCellDescPrestServi.addElement(new Phrase("Isenção de Serviço de Manutenção em Equipamentos "
		        		+ "(Se houver Contrato de prestação de serviços de manutenção)",f12));		        
		        pCellDescPrestServi.setPaddingBottom(4);
		        pCellDescPrestServi.setPaddingTop(0);
		        
		        PdfPCell pCellValorPrestServi = new PdfPCell();
		        pCellValorPrestServi.addElement(new Phrase("R$ "+Real.formatDbToString(String.valueOf(acessoCliente.getIsencao_prest_serv_manutencao())),f12));		        
		        pCellValorPrestServi.setPaddingBottom(4);
		        pCellValorPrestServi.setPaddingTop(0);
		        
		        PdfPTable tbPrestServ = new PdfPTable(new float[] {0.10f,0.80f,0.80f});
		        tbPrestServ.setWidthPercentage(100f);
		        tbPrestServ.addCell(pCellCheckManutenServ);
		        tbPrestServ.addCell(pCellDescPrestServi);
		        tbPrestServ.addCell(pCellValorPrestServi);		        
		        doc.add(tbPrestServ);
		        
		        Paragraph pDescricaoBeneficioTotal = new Paragraph();
		        pDescricaoBeneficioTotal.add(new Phrase("Total dos Benefícios",f12b));
		        pDescricaoBeneficioTotal.setAlignment(Element.ALIGN_LEFT);
		        
		        PdfPCell pCellDescBeneficioTotal = new PdfPCell();
		        pCellDescBeneficioTotal.addElement(pDescricaoBeneficioTotal);		        
		        pCellDescBeneficioTotal.setPaddingBottom(2);
		        pCellDescBeneficioTotal.setPaddingTop(0);
		        
		        double total_bene = 0;
		        //if(acessoCliente.isBeneficio_comodato()){
		        if(acessoCliente.getInstalacao_gratis() != null && acessoCliente.getInstalacao_gratis().equals("SIM")){		
		        	total_bene = acessoCliente.getValor_beneficio_adesao() + acessoCliente.getValor_beneficio_comodato() + acessoCliente.getIsencao_prest_serv_manutencao() + new Double(150); 
		        }else{
//		        	double isen_servi_manuten = 0;
//		        	if(acessoCliente.getIsencao_prest_serv_manutencao() != null){
//		        		isen_servi_manuten = acessoCliente.getIsencao_prest_serv_manutencao();
//		        	}
		        	total_bene = acessoCliente.getValor_beneficio_adesao() + acessoCliente.getValor_beneficio_comodato() + acessoCliente.getIsencao_prest_serv_manutencao();
		        }
		        //}else{
		        	//------
		        //}
		        
		        		       		        
		        Paragraph pValorBeneficioTotalValor = new Paragraph();
		        pValorBeneficioTotalValor.add(new Phrase("R$ "+Real.formatDbToString(String.valueOf(total_bene)),f12b));
		        pValorBeneficioTotalValor.setAlignment(Element.ALIGN_LEFT);
		        
		        PdfPCell pCellValorBeneficioTotalValor = new PdfPCell();
		        pCellValorBeneficioTotalValor.addElement(pValorBeneficioTotalValor);		        
		        pCellValorBeneficioTotalValor.setPaddingBottom(4);
		        pCellValorBeneficioTotalValor.setPaddingTop(0);
		        
		        PdfPTable tbValorLinha6 = new PdfPTable(new float[] {0.10f,0.80f,0.80f});
		        tbValorLinha6.setWidthPercentage(100f);
		        tbValorLinha6.addCell(pCellVazio2);
		        tbValorLinha6.addCell(pCellDescBeneficioTotal);
		        tbValorLinha6.addCell(pCellValorBeneficioTotalValor);		        
		        doc.add(tbValorLinha6);
		        
		        PdfPCell pCellTipoPlano = new PdfPCell();
		        pCellTipoPlano.addElement(new Phrase("Tipo de Plano",f12));		        
		        pCellTipoPlano.setPaddingBottom(4);
		        pCellTipoPlano.setPaddingTop(0);
		        
		        PdfPCell pCellTipoPlanoValor = new PdfPCell();
		        pCellTipoPlanoValor.addElement(new Phrase(acessoCliente.getContrato().getNome(),f12b));		        
		        pCellTipoPlanoValor.setPaddingBottom(4);
		        pCellTipoPlanoValor.setPaddingTop(0);
		        
		        PdfPCell pCellCarencia = new PdfPCell();
		        pCellCarencia.addElement(new Phrase("Carência",f12));		        
		        pCellCarencia.setPaddingBottom(4);
		        pCellCarencia.setPaddingTop(0);
		        
		        PdfPCell pCellCarenciaValor = new PdfPCell();
		        pCellCarenciaValor.addElement(new Phrase("SIM",f12b));		        
		        pCellCarenciaValor.setPaddingBottom(4);
		        pCellCarenciaValor.setPaddingTop(0);
		        
		        PdfPCell pCellTaxaAdesao = new PdfPCell();
		        pCellTaxaAdesao.addElement(new Phrase("Taxa Adesão",f12));		        
		        pCellTaxaAdesao.setPaddingBottom(4);
		        pCellTaxaAdesao.setPaddingTop(0);
		        
		        PdfPCell pCellTaxaAdesaoValor = new PdfPCell();
		        pCellTaxaAdesaoValor.addElement(new Phrase("R$ 450,00",f12b));		        
		        pCellTaxaAdesaoValor.setPaddingBottom(2);
		        pCellTaxaAdesaoValor.setPaddingTop(0);
		        
		        PdfPCell pCellBeneficioAdesao = new PdfPCell();
		        pCellBeneficioAdesao.addElement(new Phrase("Benefício Adesão",f12));		        
		        pCellBeneficioAdesao.setPaddingBottom(2);
		        pCellBeneficioAdesao.setPaddingTop(0);
		        
		        PdfPCell pCellBeneficioAdesaoValor = new PdfPCell();
		        pCellBeneficioAdesaoValor.addElement(new Phrase("R$ 450,00",f12b));		        
		        pCellBeneficioAdesaoValor.setPaddingBottom(2);
		        pCellBeneficioAdesaoValor.setPaddingTop(0);
		        
		        
		        Paragraph pClausla3 = new Paragraph("CLÁUSULA TERCEIRA – DA FIDELIDADE CONTRATUAL",f12bUnder);
		        pClausla3.setAlignment(Element.ALIGN_CENTER);		      
		        pClausla3.setSpacingAfter(5);
		        pClausla3.setSpacingBefore(5);
		        doc.add(pClausla3);
		        
		        Paragraph pClausla3_1 = new Paragraph();	        
		        pClausla3_1.add(new Phrase("3.1   ",f12b));
		        pClausla3_1.add(new Phrase("O presente instrumento formaliza a ",f12));
		        pClausla3_1.add(new Phrase("CONCESSÃO DE BENEFÍCIO ",f12b));
		        pClausla3_1.add(new Phrase("ao  ",f12));
		        pClausla3_1.add(new Phrase("ASSINANTE,  ",f12b));
		        pClausla3_1.add(new Phrase("conforme descrito na ",f12));		  
		        pClausla3_1.add(new Phrase("Clausula Segunda, ",f12b));
		        pClausla3_1.add(new Phrase("e, em contrapartida, o ",f12));
		        pClausla3_1.add(new Phrase("ASSINANTE  ",f12b));
		        pClausla3_1.add(new Phrase("vincula-se contratualmente ",f12bUnder));
		        pClausla3_1.add(new Phrase("a ",f12));
		        pClausla3_1.add(new Phrase("PRESTADORA ",f12b));
		        pClausla3_1.add(new Phrase("pelo período mínimo de ",f12));
		        pClausla3_1.add(new Phrase("12 (DOZE) MESES, ",f12b));
		        pClausla3_1.add(new Phrase("a contar da assinatura do presente instrumento.",f12));
		        pClausla3_1.setAlignment(Element.ALIGN_JUSTIFIED);
		        pClausla3_1.setSpacingAfter(5);
		       // pClausla3_1.setSpacingBefore(20);
		        doc.add(pClausla3_1);
		        
		      
		        Paragraph pClausla3_2 = new Paragraph();	        
		        pClausla3_2.add(new Phrase("3.2   ",f12b));
		        pClausla3_2.add(new Phrase("O período mínimo que trata este artigo é fundamentado no Arts. 57 a 59 da Resolução nº 632/2014 da Anatel, devendo diferenciar-se da “vigência contratual” a qual é regida conforme Cláusula 16.1 do ",f12));
		        pClausla3_2.add(new Phrase("CONTRATO SCM ",f12b));
		        pClausla3_2.add(new Phrase("onde especifica a sua renovação de forma automática caso o ",f12));
		        pClausla3_2.add(new Phrase("ASSINANTE ",f12b));
		        pClausla3_2.add(new Phrase("não oficialize o pedido de suspensão do serviço. ",f12));		
		        pClausla3_2.setSpacingAfter(0);
		       // pClausla3_2.setSpacingBefore(20);
		        doc.add(pClausla3_2);
		        
		        Paragraph pClausla3_21 = new Paragraph();
		        pClausla3_21.add(new Phrase("Parágrafo Único: ",f12b));
		        pClausla3_21.add(new Phrase("A renovação automática é única e exclusiva da prestação do serviço, não estendendo-se a fidelidade, exceto, nos casos em que o ",f12));
		        pClausla3_21.add(new Phrase("ASSINANTE ",f12b));
		        pClausla3_21.add(new Phrase("de forma expressa ",f12));
		        pClausla3_21.add(new Phrase("queira aderir a uma nova proposta ",f12bUnder));
		        pClausla3_21.add(new Phrase("comercial de Fidelidade. ",f12));
		        pClausla3_21.setAlignment(Element.ALIGN_JUSTIFIED);
		        pClausla3_21.setSpacingAfter(4);
		        doc.add(pClausla3_21);
		        
		        
		        Paragraph pClausla3_4 = new Paragraph();	        
		        pClausla3_4.add(new Phrase("3.3   ",f12b));
		        pClausla3_4.add(new Phrase("Caso o ",f12));
		        pClausla3_4.add(new Phrase("ASSINANTE ",f12b));
		        pClausla3_4.add(new Phrase("rescinda o contrato antes do término do prazo de permanência mínima, o ",f12));
		        pClausla3_4.add(new Phrase("ASSINANTE ",f12b));
		        pClausla3_4.add(new Phrase("deverá restituir a ",f12));		  
		        pClausla3_4.add(new Phrase("PRESTADORA ",f12b));
		        pClausla3_4.add(new Phrase("o valor correspondente ao benefício recebido, proporcionalmente ao número de meses restantes para o término do contrato, conforme descrição abaixo: VM = (VB/MF) X MR. ",f12));
		        pClausla3_4.add(new Phrase("Sendo: ",f12));
//		        pClausla3_4.add(new Phrase("       VB = Valor total dos benefícios concedidos;",f12));
//		        pClausla3_4.add(new Phrase("       MF = Número total de meses de fidelidade;",f12));
//		        pClausla3_4.add(new Phrase("       MR = Número total de meses restantes para se completar o prazo da fidelidade;",f12));
		        pClausla3_4.setSpacingAfter(0);
		        pClausla3_4.setSpacingBefore(0);
		        pClausla3_4.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausla3_4);
		        
		        Font f8b = new Font(base8, 8f, Font.BOLD);
		        Paragraph rodaPePg = new Paragraph("TERMO DE BENEFÍCIO - "+empresa.getRazao_social()+" - Página 1 de 2                                        emitido por: "+OpusERP4UI.getUsuarioLogadoUI().getUsername(),f8b);
		        rodaPePg.setAlignment(Element.ALIGN_RIGHT);
		        
		        PdfPCell pCellRod = new PdfPCell();
		        pCellRod.addElement(rodaPePg);	 	  
		        pCellRod.setBorderWidthBottom(0);
		        pCellRod.setBorderWidthRight(0);
		        pCellRod.setBorderWidthLeft(0);
		        
		        Rectangle page = doc.getPageSize();
				   PdfPTable foot = new PdfPTable(new float[] {1f});
				   foot.setWidthPercentage(100f);
			       foot.addCell(pCellRod);
			       foot.setTotalWidth(page.getWidth() - doc.leftMargin() - doc.rightMargin());
			       foot.writeSelectedRows(0, -4, doc.leftMargin(), doc.bottomMargin() + 10,pdfWriter.getDirectContent());
		        
		        doc.add(Chunk.NEXTPAGE);
		        
		        Paragraph pClausla3_40 = new Paragraph();     
		        pClausla3_40.add(new Phrase("VM = Valor da multa; ",f12));
		        pClausla3_40.setSpacingAfter(5);
		        pClausla3_40.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausla3_40);
		        
		        Paragraph pClausla3_41 = new Paragraph();     
		        pClausla3_41.add(new Phrase("VB = Valor total dos benefícios concedidos; ",f12));
		        pClausla3_41.setSpacingAfter(5);
		        pClausla3_41.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausla3_41);
		        
		        Paragraph pClausla3_42 = new Paragraph();     
		        pClausla3_42.add(new Phrase("MF = Número total de meses de fidelidade; ",f12));
		        pClausla3_42.setSpacingAfter(5);
		        pClausla3_42.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausla3_42);
		        
		        Paragraph pClausla3_43 = new Paragraph();     
		        pClausla3_43.add(new Phrase("MR = Número total de meses restantes para se completar o prazo da fidelidade; ",f12));
		        pClausla3_43.setSpacingAfter(5);
		        pClausla3_43.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausla3_43);

		       
			       			       
			  
		        
		        Paragraph pClausla3_3 = new Paragraph();	        
		        pClausla3_3.add(new Phrase("3.4   ",f12b));
		        pClausla3_3.add(new Phrase("Toda e qualquer mudança nas instalações ou configurações estabelecidas ou planos solicitadas pelo ",f12));
		        pClausla3_3.add(new Phrase("ASSINANTE, ",f12b));
		        pClausla3_3.add(new Phrase(" incluindo a posterior mudança no local da prestação do serviço, fica desde já condicionada à existência de disponibilidade e viabilidade técnica no local da instalação do serviço.  ",f12));		       
		        pClausla3_3.setAlignment(Element.ALIGN_JUSTIFIED);
		        pClausla3_3.setSpacingAfter(5);
		        //pClausla3_2.setSpacingBefore(20);
		        doc.add(pClausla3_3);
		       
		        Paragraph pClausula6_4_Paragrafo_Unico = new Paragraph();
		        pClausula6_4_Paragrafo_Unico.add(new Phrase("Parágrafo Único. ",f12b));
		        pClausula6_4_Paragrafo_Unico.add(new Phrase("Havendo solicitação para local em que a ",f12));
		        pClausula6_4_Paragrafo_Unico.add(new Phrase("PRESTADORA, ",f12b));
		        pClausula6_4_Paragrafo_Unico.add(new Phrase("não possua viabilidade técnica, o ",f12));
		        pClausula6_4_Paragrafo_Unico.add(new Phrase("ASSINANTE ",f12b));
		        pClausula6_4_Paragrafo_Unico.add(new Phrase("declara ciência de que será cobrada multa por rescisão antecipada, nos moldes acordados no Contrato de Permanência.",f12));	        
		        pClausula6_4_Paragrafo_Unico.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausula6_4_Paragrafo_Unico);
		        
		        
		        
		        Paragraph pClausla4 = new Paragraph("CLÁUSULA QUARTA – DAS DISPOSIÇÕES FINAIS",f12bUnder);
		        pClausla4.setAlignment(Element.ALIGN_CENTER);		      
		        pClausla4.setSpacingAfter(5);
		        pClausla4.setSpacingBefore(5);
		        doc.add(pClausla4);
		        
		        Paragraph pClausla4_1 = new Paragraph();	        
		        pClausla4_1.add(new Phrase("4.1   ",f12b));
		        pClausla4_1.add(new Phrase("O presente ",f12));
		        pClausla4_1.add(new Phrase("TERMO DE CONCESSÃO CONDICIONAL DE BENEFÍCIOS ",f12b));
		        pClausla4_1.add(new Phrase("forma, em conjunto com o ",f12));
		        pClausla4_1.add(new Phrase("CONTRATO DE PRESTAÇÃO DE SERVIÇOS DE COMUNICAÇÃO MULTIMÍDIA (SCM), ",f12b));
		        pClausla4_1.add(new Phrase("o acessórios, se houver, e o ",f12));
		        pClausla4_1.add(new Phrase("TERMO DE ADESÃO,  ",f12b));		        
		        pClausla4_1.add(new Phrase("título executivo extrajudicial, ",f12bUnder));
		        pClausla4_1.add(new Phrase("para todos os fins de direito.",f12));
		        pClausla4_1.setAlignment(Element.ALIGN_JUSTIFIED);
		        doc.add(pClausla4_1);
		        		    		        
		        Paragraph pBelo = new Paragraph("Belo Jardim/PE, "+date,f12);
		        pBelo.setAlignment(Element.ALIGN_LEFT);
		        pBelo.setSpacingAfter(5);
		        pBelo.setAlignment(Element.ALIGN_RIGHT);
		        doc.add(pBelo);
		 
		        
		        //COLUNA 1
		        PdfPCell pCellRepresentante = new PdfPCell();
		        pCellRepresentante.setBorderWidth(0);
		        pCellRepresentante.addElement(new Paragraph("______________________________________",f12b));
		        pCellRepresentante.addElement(new Paragraph("REP. LEGAL: "+cliente.getNome_razao(),f12b));   
		        pCellRepresentante.addElement(new Paragraph("CPF/CNPJ: "+cliente.getDoc_cpf_cnpj(),f12));
		       	        
		        //COLUNA 2
		        PdfPCell pCellAvalista = new PdfPCell();
		        pCellAvalista.setBorderWidth(0);
		        pCellAvalista.addElement(new Paragraph("______________________________________",f12b));
		        
		        String fiadorNome = " ";
		        String fiadorDoc = " ";
		        if(acessoCliente.getFiador()!=null){
		        	fiadorNome = acessoCliente.getFiador().getNome_razao();
		        	fiadorDoc = acessoCliente.getFiador().getDoc_cpf_cnpj();
		        }
		        pCellAvalista.addElement(new Paragraph("AVALISTA: "+fiadorNome,f12b));        
		        pCellAvalista.addElement(new Paragraph("CPF/CNPJ: "+fiadorDoc,f12));
		       	        
		        PdfPTable tbAss = new PdfPTable( new float[]{2.65f,0.30f,2.30f});   
		        tbAss.setWidthPercentage(100f);		
		        tbAss.addCell(pCellRepresentante);
		        tbAss.addCell(pCellVazio2);
		        tbAss.addCell(pCellAvalista);
		        doc.add(tbAss);
		        
		        
		        Paragraph pRodape = new Paragraph();		        
		        pRodape.add(new Phrase("Contrato de Permanência - ",f12));
		        pRodape.add(new Phrase("DIGITAL TECNOLOGIA & TELECOMUNICAÇÃO LTDA - SAC 0800 081 3125 ",f12b));
		        pRodape.setAlignment(Element.ALIGN_CENTER);
		        pRodape.setSpacingBefore(3);
		        doc.add(pRodape);
		        
		        
		        Paragraph rodaPePg2 = new Paragraph("TERMO DE BENEFÍCIO - "+empresa.getRazao_social()+" - Página 2 de 2                                        emitido por: "+OpusERP4UI.getUsuarioLogadoUI().getUsername(),f8b);
		        rodaPePg2.setAlignment(Element.ALIGN_RIGHT);
		        
		        PdfPCell pCellRod2 = new PdfPCell();
		        pCellRod2.addElement(rodaPePg2);	 	  
		        pCellRod2.setBorderWidthBottom(0);
		        pCellRod2.setBorderWidthRight(0);
		        pCellRod2.setBorderWidthLeft(0);
		        
		        Rectangle page4 = doc.getPageSize();
				   PdfPTable foot4 = new PdfPTable(new float[] {1f});
				   foot4.setWidthPercentage(100f);
			       foot4.addCell(pCellRod2);
			       foot4.setTotalWidth(page4.getWidth() - doc.leftMargin() - doc.rightMargin());
			       foot4.writeSelectedRows(0, -4, doc.leftMargin(), doc.bottomMargin() + 10,pdfWriter.getDirectContent());
		  
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(doc.isOpen() && doc != null){
				doc.close();
			}
		}		
	}

	public static float PixelsToPoints(float value,int dpi)
	{
	   return value / dpi * 72;
	}
	
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}	
	

	
	
	
	
}
