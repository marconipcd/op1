package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.ParametrosBoleto;
import com.digital.opuserp.domain.PreContrato;
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
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class ImprimirPreContrato implements StreamSource{

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ImprimirPreContrato(Integer codAc) throws Exception {
		
		EntityManager em = ConnUtil.getEntity();
		
		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);	
		doc.setMargins(24, 24, 22, 22);
		PdfWriter pdfWriter = null;
		try{
			pdfWriter = PdfWriter.getInstance(doc, baos);
			doc.open();
			
			HTMLWorker htmlWorker = new HTMLWorker(doc);
			
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			PreContrato acessoCliente = em.find(PreContrato.class, codAc);
			Cliente cliente = acessoCliente.getContrato().getCliente();
		
			String controle =  "ACESSO-POS";
			Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
			qControle.setParameter("nome", controle);
			qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
											
			ContaBancaria cb = null;
			if(qControle.getResultList().size() ==1){
				cb = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
			}
			
			Query qPb = em.createQuery("select pb from ParametrosBoleto pb where pb.cliente_id = :codCliente", ParametrosBoleto.class);
			qPb.setParameter("codCliente", cliente.getId());			
			ParametrosBoleto pb = null;
			boolean cobrarTaxa = true;
			if(qPb.getResultList().size() > 0){
				pb = (ParametrosBoleto) qPb.getSingleResult();
				
				if(!pb.getCobrar_taxa_bancaria()){
					cobrarTaxa = false;
				}
			}

			Endereco end = acessoCliente.getContrato().getEndereco();
			
			URL urlFont  = getClass().getResource("/com/digital/opuserp/font/Arial Narrow.ttf");
			BaseFont base8 = BaseFont.createFont(urlFont.toString(), BaseFont.WINANSI,true);
			Font f8 = new Font(base8, 8f);
			Font f9 = new Font(base8, 9f);
				
			byte[] logo = empresa.getLogo_empresa();		
			
			if(logo != null){
				Image imgLogo = Image.getInstance(logo);				
				imgLogo.setAlignment(Element.ALIGN_CENTER);
				
				imgLogo.scalePercent(65.0f);
					
				PdfPCell pCellLogo1 = new PdfPCell();
		        pCellLogo1.addElement(new Phrase(""));
		        pCellLogo1.setPaddingBottom(4);
		        pCellLogo1.setPaddingTop(0);
		        pCellLogo1.setBorder(0);
		        
				PdfPCell pCellLogo = new PdfPCell();
		        pCellLogo.addElement(imgLogo);
		        pCellLogo.setPaddingBottom(4);
		        pCellLogo.setPaddingTop(4);
		        pCellLogo.setBorder(0);
		        
		        DateTime ano  =new DateTime(acessoCliente.getContrato().getData_cadastro());
		        
		        PdfPCell pCellCodDireita = new PdfPCell();
		        pCellCodDireita.addElement(new Phrase("Nº "+acessoCliente.getContrato().getId().toString()+" / "+String.valueOf(ano.getYear()),f9));		        
		        pCellCodDireita.setPaddingBottom(8);
		        pCellCodDireita.setPaddingTop(2);
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
			
			Font f8b = new Font(base8, 8f, Font.BOLD);
			
			Font f12 = new Font(base8, 12f);
			Font f12Under = new Font(base8, 12f, Font.UNDERLINE);
			
			Font f12b = new Font(base8, 11f, Font.BOLD);
			
			Font f12bUnder = new Font(base8, 12f, Font.BOLD | Font.UNDERLINE);

			// Fones Enpresa
			StringBuilder fonesEmp = new StringBuilder();
			if(empresa.getDdd_fone1()!=null && empresa.getFone1()!=null){
				fonesEmp.append(empresa.getDdd_fone1()+" "+empresa.getFone1());				
			}else if(empresa.getFone1() !=null){
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
			Paragraph pTopo1 = new Paragraph("TERMO DE ADESÃO",f12);
			pTopo1.setAlignment(Element.ALIGN_CENTER);
			Paragraph pTopo2 = new Paragraph("AO CONTRATO DE PRESTAÇÃO DE SERVIÇOS DE TELECOMUNICAÇÕES E",f12);
			pTopo2.setAlignment(Element.ALIGN_CENTER);
			Paragraph pTopo3 = new Paragraph("AO CONTRATO DE COMODATO DE EQUIPAMENTOS",f12);
			pTopo3.setAlignment(Element.ALIGN_CENTER);
														
			doc.add(pTopo1);
			doc.add(pTopo2);
			doc.add(pTopo3);
		
			DataUtil dtUtil = new DataUtil();
				String date = null;
				
				if(acessoCliente.getContrato().getData_alteracao_plano()!=null){
					date = dtUtil.getDataExtensoMes(acessoCliente.getContrato().getData_alteracao_plano());
				}else{
					date = dtUtil.getDataExtensoMes(acessoCliente.getContrato().getData_cadastro());				
				}
		 
		  	 Paragraph pContrato = new Paragraph();
		  	 pContrato.add(new Phrase(" TERMO N°.:",f12));
		  	 pContrato.add(new Phrase(" "+acessoCliente.getId().toString(),f12b));
		     
			 Paragraph pBeloJardim = new Paragraph();
			 pBeloJardim.setAlignment(Element.ALIGN_CENTER);
			 pBeloJardim.add(new Phrase("Belo Jardim,  ",f12));
			 pBeloJardim.add(new Phrase(date.toString(),f12b));
			 
			 Paragraph pVirgencia = new Paragraph();
			 pVirgencia.setAlignment(Element.ALIGN_CENTER);
			 pVirgencia.add(new Phrase("VIGÊNCIA:",f12));
			 if(acessoCliente.getContrato().getContrato().getVigencia() == 1 || acessoCliente.getContrato().getContrato().getVigencia() == 0){
				 pVirgencia.add(new Phrase(" "+acessoCliente.getContrato().getContrato().getVigencia().toString() +" MES",f12b));				 
			 }else{
				 pVirgencia.add(new Phrase(" "+acessoCliente.getContrato().getContrato().getVigencia().toString() +" MESES",f12b));				 				 
			 }

			 PdfPCell pCellpContrato= new PdfPCell();
			 pCellpContrato.addElement(pContrato);
			 pCellpContrato.setPaddingTop(0);
			 pCellpContrato.setPaddingBottom(4);
			 pCellpContrato.setBackgroundColor(new BaseColor(232, 235, 237));

			 PdfPCell pCellpBeloJardim= new PdfPCell();
			 pCellpBeloJardim.addElement(pBeloJardim);
			 pCellpBeloJardim.setPaddingTop(0);
			 pCellpBeloJardim.setPaddingBottom(4);
			 pCellpBeloJardim.setBackgroundColor(new BaseColor(232, 235, 237));
			 
			 PdfPCell pCellpVirgencia= new PdfPCell();
			 pCellpVirgencia.addElement(pVirgencia);
			 pCellpVirgencia.setPaddingTop(0);
			 pCellpVirgencia.setPaddingBottom(4);
			 pCellpVirgencia.setBackgroundColor(new BaseColor(232, 235, 237));
			 
			 PdfPTable tbCont = new PdfPTable(new float[] {0.50f, 1f, 0.50f});
			 tbCont.setWidthPercentage(100f);
			 tbCont.addCell(pCellpContrato);
			 tbCont.addCell(pCellpBeloJardim);
			 tbCont.addCell(pCellpVirgencia);

			// 1ª Linha
			Paragraph pPartTermo = new Paragraph("As partes abaixo identificadas resolvem firmar o presente",f12);
			pPartTermo.setAlignment(Element.ALIGN_LEFT);
			
			PdfPCell pCellpPartTermo = new PdfPCell();
			pCellpPartTermo.addElement(pPartTermo);
			pCellpPartTermo.setBorderWidth(0);
			
			Paragraph pPartTermoAdesao = new Paragraph("TERMO DE ADESÃO:",f12b);	
			pPartTermoAdesao.setAlignment(Element.ALIGN_LEFT);		 
			
			PdfPCell pCellpPartTermoAdesao= new PdfPCell();
			pCellpPartTermoAdesao.addElement(pPartTermoAdesao);
			pCellpPartTermoAdesao.setBorderWidth(0);
	
	
			PdfPTable tbPart = new PdfPTable(new float[] {0.80f,0.90f});	
			tbPart.setWidthPercentage(100f);
			tbPart.setSpacingAfter(10);			
			tbPart.addCell(pCellpPartTermo);
			tbPart.addCell(pCellpPartTermoAdesao);
 			
			doc.add(tbPart);
			
			//DADOS PRESTADORA
			// Linha Lateral
	        PdfTemplate template = pdfWriter.getDirectContent().createTemplate(0.15f, 50);
	        BaseFont bf = BaseFont.createFont("Helvetica", "winansi", false);
	        String text = "   DADOS DA PRESTADORA   ";
	        float size = 22;
	        float width = bf.getWidthPoint(text, size);
	        template.beginText();
	        template.setFontAndSize(bf, size);
//	        template.setTextMatrix(0, 2);
	        template.showText(text);
	        template.endText();
	        template.setWidth(width);
	       
	        Image img = Image.getInstance(template);
	        img.setRotationDegrees(90);

			PdfPCell pCellpDadosLateral = new PdfPCell();
			pCellpDadosLateral.addElement(img);
			pCellpDadosLateral.setPaddingRight(16);
			pCellpDadosLateral.setPaddingTop(5);
			pCellpDadosLateral.setBackgroundColor(new BaseColor(232, 235, 237));
		
	        // 1ª Linha
			Paragraph pDadosRazaoSoc = new Paragraph();
			pDadosRazaoSoc.add(new Phrase(" Nome Empresarial:"+"\n",f12));
			pDadosRazaoSoc.add(new Phrase(" "+empresa.getRazao_social(),f12b));
			pDadosRazaoSoc.setSpacingAfter(0);
			pDadosRazaoSoc.setSpacingBefore(0); 
			
			PdfPCell pCellpDadosRazaoSoc = new PdfPCell();
			pCellpDadosRazaoSoc.addElement(pDadosRazaoSoc);
			pCellpDadosRazaoSoc.setPaddingBottom(1);
			pCellpDadosRazaoSoc.setPaddingTop(0);
			pCellpDadosRazaoSoc.setFixedHeight(33);
 
			Paragraph pDadosAut = new Paragraph();	
			pDadosAut.add(new Phrase(" Ato de Autorização - Anatel"+"\n",f12));	
			if(acessoCliente.getContrato().getContrato().getAto_autorizacao()!=null){
				pDadosAut.add(new Phrase(" Nº "+acessoCliente.getContrato().getContrato().getAto_autorizacao(),f12b));				
			}else{
				pDadosAut.add(new Phrase(" ",f12b));								
			}
			PdfPCell pCellppDadosAut= new PdfPCell();
			pCellppDadosAut.addElement(pDadosAut);
			pCellppDadosAut.setPaddingBottom(1);
			pCellppDadosAut.setPaddingTop(0);
			pCellppDadosAut.setFixedHeight(33);
 
			Paragraph pDadosTermo = new Paragraph();	
			pDadosTermo.add(new Phrase(" Termo de Autorização - Anatel"+"\n",f12));	
			if(acessoCliente.getContrato().getContrato().getTermo_autorizacao()!=null){
				pDadosTermo.add(new Phrase(" Nº "+acessoCliente.getContrato().getContrato().getTermo_autorizacao(),f12b));				
			}else{
				pDadosTermo.add(new Phrase(" ",f12b));								
			}
			PdfPCell pCellpDadosTermo= new PdfPCell();
			pCellpDadosTermo.addElement(pDadosTermo);
			pCellpDadosTermo.setPaddingBottom(1);
			pCellpDadosTermo.setPaddingTop(0);
			pCellpDadosTermo.setFixedHeight(33);

			PdfPTable tbDadosPrest = new PdfPTable(new float[] {1f});
			tbDadosPrest.setWidthPercentage(100f);
			tbDadosPrest.setSpacingAfter(3);
			tbDadosPrest.addCell(pCellpDadosRazaoSoc);
			
			//tbDadosPrest.addCell(pCellpDadosTermo);

			// 2ª Linha		
			Paragraph pDadosCNPJ = new Paragraph();
			pDadosCNPJ.add(new Phrase(" CNPJ:"+"\n",f12));
			pDadosCNPJ.add(new Phrase(" "+empresa.getCnpj(),f12b));
			PdfPCell pCellpDadosCNPJ = new PdfPCell();
			pCellpDadosCNPJ.addElement(pDadosCNPJ);
			pCellpDadosCNPJ.setPaddingBottom(1);
			pCellpDadosCNPJ.setPaddingTop(0);
			pCellpDadosCNPJ.setFixedHeight(33);
			
			Paragraph pDadosInscri = new Paragraph();	
			pDadosInscri.add(new Phrase(" Inscrição Estadual: "+"\n",f12));	
			pDadosInscri.add(new Phrase(" "+empresa.getInscricao_estadual(),f12b));	
			PdfPCell pCellpDadosInscri= new PdfPCell();
			pCellpDadosInscri.addElement(pDadosInscri);
			pCellpDadosInscri.setPaddingBottom(1);
			pCellpDadosInscri.setPaddingTop(0);
			pCellpDadosInscri.setFixedHeight(33);
			
			Paragraph pDadosEnd = new Paragraph();	
			pDadosEnd.add(new Phrase(" Endereço:"+"\n",f12));	
			pDadosEnd.add(new Phrase(" "+empresa.getEndereco()+", "+empresa.getNumero(),f12b));	
			
			PdfPCell pCellpDadosEnd= new PdfPCell();
			pCellpDadosEnd.addElement(pDadosEnd);
			pCellpDadosEnd.setPaddingBottom(1);
			pCellpDadosEnd.setPaddingTop(0);
			pCellpDadosEnd.setFixedHeight(33);
			
			PdfPTable tbDadosCNPJ = new PdfPTable(new float[] {0.50f, 0.50f,0.80f, 0.80f});
			tbDadosCNPJ.setWidthPercentage(100f);
			tbDadosCNPJ.setSpacingAfter(3);
			tbDadosCNPJ.addCell(pCellpDadosCNPJ);
			tbDadosCNPJ.addCell(pCellpDadosInscri);
			tbDadosCNPJ.addCell(pCellppDadosAut);
			tbDadosCNPJ.addCell(pCellpDadosTermo);
			//tbDadosCNPJ.addCell(pCellpDadosEnd);
			
			PdfPTable tbDadosEndereco = new PdfPTable(new float[] {1f});
			tbDadosEndereco.setWidthPercentage(100f);
			tbDadosEndereco.setSpacingAfter(3);			
			tbDadosEndereco.addCell(pCellpDadosEnd);
			
			// 3ª Linha		
			Paragraph pDadosCidade = new Paragraph();
			pDadosCidade.add(new Phrase(" Cidade:"+"\n",f12));
			pDadosCidade.add(new Phrase(" "+empresa.getCidade(),f12b));
			
			PdfPCell pCellpDadosCidade = new PdfPCell();
			pCellpDadosCidade.addElement(pDadosCidade);
			pCellpDadosCidade.setPaddingBottom(1);
			pCellpDadosCidade.setPaddingTop(0);
			pCellpDadosCidade.setFixedHeight(33);
			
			Paragraph pDadosEstado = new Paragraph();
			pDadosEstado.add(new Phrase(" Estado:"+"\n",f12));
			pDadosEstado.add(new Phrase(" "+empresa.getUf(),f12b));
			
			PdfPCell pCellpDadosEstado = new PdfPCell();
			pCellpDadosEstado.addElement(pDadosEstado);
			pCellpDadosEstado.setPaddingBottom(1);
			pCellpDadosEstado.setPaddingTop(0);
			pCellpDadosEstado.setFixedHeight(33);
			
			Paragraph pDadosBairro = new Paragraph();	
			pDadosBairro.add(new Phrase(" Bairro: "+"\n",f12));	
			pDadosBairro.add(new Phrase(" "+empresa.getBairro(),f12b));	
			PdfPCell pCellpDadosBairro= new PdfPCell();
			pCellpDadosBairro.addElement(pDadosBairro);
			pCellpDadosBairro.setPaddingBottom(1);
			pCellpDadosBairro.setPaddingTop(0);
			pCellpDadosBairro.setFixedHeight(33);
			
			Paragraph pDadosCep= new Paragraph();	
			pDadosCep.add(new Phrase(" CEP:"+"\n",f12));	
			pDadosCep.add(new Phrase(" "+empresa.getCep(),f12b));	
			PdfPCell pCellpCep= new PdfPCell();
			pCellpCep.addElement(pDadosCep);
			pCellpCep.setPaddingBottom(1);
			pCellpCep.setPaddingTop(0);
			
			PdfPTable tbDadosCidade = new PdfPTable(new float[] {0.60f,0.60f,  0.60f,1f});
			tbDadosCidade.setWidthPercentage(100f);
			tbDadosCidade.setSpacingAfter(3);
			tbDadosCidade.addCell(pCellpDadosBairro);
			tbDadosCidade.addCell(pCellpDadosCidade);
			tbDadosCidade.addCell(pCellpDadosEstado);		
			tbDadosCidade.addCell(pCellpCep);
			
			// 4ª Linha		
			Paragraph pDadosFone = new Paragraph();
			pDadosFone.add(new Phrase(" Telefone:"+"\n",f12));
			pDadosFone.add(new Phrase(" "+"("+empresa.getDdd_fone1()+") "+empresa.getFone1(),f12b));
			PdfPCell pCellpDadosFone = new PdfPCell();
			pCellpDadosFone.addElement(pDadosFone);
			pCellpDadosFone.setPaddingBottom(1);
			pCellpDadosFone.setPaddingTop(0);
			pCellpDadosFone.setFixedHeight(33);
			
			Paragraph pDadosSAC = new Paragraph();	
			pDadosSAC.add(new Phrase(" S.A.C."+"\n",f12));	
			pDadosSAC.add(new Phrase(" "+empresa.get_0800(),f12b));	
			PdfPCell pCellDadosSAC= new PdfPCell();
			pCellDadosSAC.addElement(pDadosSAC);
			pCellDadosSAC.setPaddingBottom(1);
			pCellDadosSAC.setPaddingTop(0);
			pCellDadosSAC.setFixedHeight(33);
			
			Paragraph pDadosSite = new Paragraph();	
			pDadosSite.add(new Phrase(" Site\n",f12));	
			pDadosSite.add(new Phrase(" "+empresa.getSite(),f12b));	
			PdfPCell pCellDadosSite= new PdfPCell();
			pCellDadosSite.addElement(pDadosSite);
			pCellDadosSite.setPaddingBottom(1);
			pCellDadosSite.setPaddingTop(0);
			pCellDadosSite.setFixedHeight(33);
			
			Paragraph pDadosEmail= new Paragraph();	
			pDadosEmail.add(new Phrase(" E-mail:"+"\n",f12));	
			pDadosEmail.add(new Phrase(" "+empresa.getEmail(),f12b));	
			PdfPCell pCellpDadosEmail= new PdfPCell();
			pCellpDadosEmail.addElement(pDadosEmail);
			pCellpDadosEmail.setPaddingBottom(1);
			pCellpDadosEmail.setPaddingTop(0);			
			pCellpDadosEmail.setFixedHeight(33);
			
			PdfPTable tbDadosFone = new PdfPTable(new float[] {0.60f,0.60f, 0.70f, 1f});
			tbDadosFone.setWidthPercentage(100f);
			tbDadosFone.addCell(pCellpDadosFone);
			tbDadosFone.addCell(pCellDadosSAC);
			tbDadosFone.addCell(pCellDadosSite);
			tbDadosFone.addCell(pCellpDadosEmail);

			PdfPCell pCellpDadosCont= new PdfPCell();
			pCellpDadosCont.setBorderWidthRight(0);
			pCellpDadosCont.addElement(tbDadosPrest);
			pCellpDadosCont.addElement(tbDadosCNPJ);
			pCellpDadosCont.addElement(tbDadosEndereco);			
			pCellpDadosCont.addElement(tbDadosCidade);
			pCellpDadosCont.addElement(tbDadosFone);
			pCellpDadosCont.setPaddingTop(0);
			pCellpDadosCont.setPaddingBottom(0);
			pCellpDadosCont.setBorderWidthLeft(0);
			
			PdfPCell pCellVazio= new PdfPCell(new Paragraph(" "));
			PdfPCell pCellVazio2= new PdfPCell(new Paragraph(" "));
			pCellVazio2.setBorder(0);
						
			
			PdfPTable tbDadosRoot = new PdfPTable(new float[] {0.40f,0.10f, 4.40f,});
			tbDadosRoot.setWidthPercentage(100f);
			tbDadosRoot.setSpacingAfter(8);
			tbDadosRoot.addCell(pCellpDadosLateral);			
			tbDadosRoot.addCell(pCellVazio);	
			tbDadosRoot.addCell(pCellpDadosCont);
	        doc.add(tbDadosRoot);
	        
	        
	        //DADOS ASSINANTE
	        // Linha Lateral             
	        PdfTemplate templateAss = pdfWriter.getDirectContent().createTemplate(0.10f, 50);
	        BaseFont bfAss = BaseFont.createFont("Helvetica", "winansi", false);
	        String textAss = "   DADOS ASSINANTE   ";
	        float sizeAss = 22;
	        float widthAss = bfAss.getWidthPoint(textAss, sizeAss);
	        templateAss.beginText();
	        templateAss.setFontAndSize(bfAss, sizeAss);
	        templateAss.showText(textAss);
	        templateAss.endText();
	        templateAss.setWidth(widthAss);
	        
	        Image imgAss = Image.getInstance(templateAss);
	        imgAss.setRotationDegrees(90);
	        
	        PdfPCell pCellpDadosLateralAss = new PdfPCell();
	        pCellpDadosLateralAss.addElement(imgAss);
	        pCellpDadosLateralAss.setPaddingRight(16);
	        pCellpDadosLateralAss.setPaddingTop(5);
	        pCellpDadosLateralAss.setBackgroundColor(new BaseColor(232, 235, 237));
            
	        // 1ª Linha
	        Paragraph pDadosRazaoSocAss = new Paragraph();
	        pDadosRazaoSocAss.add(new Phrase(" Nome / Razão Social:"+"\n",f12));
	        pDadosRazaoSocAss.add(new Phrase(" "+cliente.getNome_razao(),f12b));
	        PdfPCell pCellpDadosRazaoSocAss = new PdfPCell();
	        pCellpDadosRazaoSocAss.addElement(pDadosRazaoSocAss);
	        pCellpDadosRazaoSocAss.setPaddingBottom(1);
	        pCellpDadosRazaoSocAss.setPaddingTop(0);	 
	        pCellpDadosRazaoSocAss.setFixedHeight(33);
	        
	        Paragraph pDadosContAss = new Paragraph();	
	        pDadosContAss.add(new Phrase(" Apelido/Contato:"+"\n",f12));	
	        pDadosContAss.add(new Phrase(" "+cliente.getContato(),f12b));	
	        PdfPCell pCellpDadosContAss= new PdfPCell();
	        pCellpDadosContAss.addElement(pDadosContAss);
	        pCellpDadosContAss.setPaddingBottom(1);
	        pCellpDadosContAss.setPaddingTop(0);
	        pCellpDadosContAss.setFixedHeight(33);
            
	        Paragraph pDadosNasAss = new Paragraph();	
	        pDadosNasAss.add(new Phrase(" Data Nascimento:"+"\n",f12));	
	        pDadosNasAss.add(new Phrase(" "+dtUtil.parseDataBra(cliente.getData_nascimento()),f12b));	
	        PdfPCell pCellpDadosNasAss= new PdfPCell();
	        pCellpDadosNasAss.addElement(pDadosNasAss);
	        pCellpDadosNasAss.setPaddingBottom(1);
	        pCellpDadosNasAss.setPaddingTop(0);
	        pCellpDadosNasAss.setFixedHeight(33);
	        
	        PdfPTable tbDadosAss = new PdfPTable(new float[] {1f, 0.50f, 0.50f});
	        tbDadosAss.setWidthPercentage(100f);
	        tbDadosAss.setSpacingAfter(3);
	        tbDadosAss.addCell(pCellpDadosRazaoSocAss);
	        tbDadosAss.addCell(pCellpDadosContAss);
	        tbDadosAss.addCell(pCellpDadosNasAss);	        
	        
	        // 2ª Linha			        
	        Paragraph pDadosRgAss = new Paragraph();	
	        pDadosRgAss.add(new Phrase(" RG / IE:"+"\n",f12));	
	        if(cliente.getDoc_rg_insc_estadual()!=null){
	    	   	pDadosRgAss.add(new Phrase(" "+cliente.getDoc_rg_insc_estadual(),f12b));	
	    	}else{
	    		pDadosRgAss.add(new Phrase(" ",f12b));		    		
	    	}
	        
	        PdfPCell pCellpDadosRgAss= new PdfPCell();
	        pCellpDadosRgAss.addElement(pDadosRgAss);
	        pCellpDadosRgAss.setPaddingBottom(1);
	        pCellpDadosRgAss.setPaddingTop(0);
	        pCellpDadosRgAss.setFixedHeight(33);
	        
	        Paragraph pDadosCPFAss = new Paragraph();	
	        pDadosCPFAss.add(new Phrase(" CPF / CNPJ:"+"\n",f12));	
	        pDadosCPFAss.add(new Phrase(" "+cliente.getDoc_cpf_cnpj(),f12b));	
	        PdfPCell pCellpDadosCPFAss= new PdfPCell();
	        pCellpDadosCPFAss.addElement(pDadosCPFAss);
	        pCellpDadosCPFAss.setPaddingBottom(1);
	        pCellpDadosCPFAss.setPaddingTop(0);
	        pCellpDadosCPFAss.setFixedHeight(33);
	        
	        Paragraph pDadosProAss = new Paragraph();
	        pDadosProAss.add(new Phrase(" Profissão:"+"\n",f12));
	        
	        if(cliente.getProfissao()!=null){
	        	pDadosProAss.add(new Phrase(" "+cliente.getProfissao(),f12b));	
	        }else{
	        	pDadosProAss.add(new Phrase(" "));
	        }
	        
	        PdfPCell pCellpDadosProAss = new PdfPCell();
	        pCellpDadosProAss.addElement(pDadosProAss);
	        pCellpDadosProAss.setPaddingBottom(1);
	        pCellpDadosProAss.setPaddingTop(0);
	        pCellpDadosProAss.setFixedHeight(33);
	        
	        PdfPTable tbDadosRgAss = new PdfPTable(new float[] {1f, 1f, 1f});
	        tbDadosRgAss.setWidthPercentage(100f);
	        tbDadosRgAss.setSpacingAfter(3);
	        tbDadosRgAss.addCell(pCellpDadosRgAss);
	        tbDadosRgAss.addCell(pCellpDadosCPFAss);
	        tbDadosRgAss.addCell(pCellpDadosProAss);
	        
	        // 3ª Linha		
	        
	        String fone1DDD = " ";
	        String fone2DDD = " ";
	        String fone3DDD = " ";
	        String fone4DDD = " ";
	        String fone1 = " ";
	        String fone2 = " ";
	        String fone3 = " ";
	        String fone4 = " ";
	        
	        if(cliente.getTelefone1()!=null && !cliente.getTelefone1().isEmpty()){
	        	fone1DDD = cliente.getDdd_fone1();
	        	fone1 = cliente.getTelefone1();
	        }
	        if(cliente.getTelefone2()!=null && !cliente.getTelefone2().isEmpty()){
	        	fone2DDD = cliente.getDdd_fone2();
	        	fone2 = cliente.getTelefone2();
	        }
	        if(cliente.getCelular1()!=null && !cliente.getCelular1().isEmpty()){
	        	fone3DDD = cliente.getDdd_cel1();
	        	fone3 = cliente.getCelular1();
	        }
	        if(cliente.getCelular2()!=null && !cliente.getCelular2().isEmpty()){
	        	fone4DDD = cliente.getDdd_cel2();
	        	fone4 = cliente.getCelular2();
	        }
	        
	        Paragraph pDadosFoneAss = new Paragraph();
	        pDadosFoneAss.add(new Phrase(" Fone Principal:"+"\n",f12));
	        pDadosFoneAss.add(new Phrase(" "+fone1DDD+" "+fone1,f12b));
	        PdfPCell pCellpDadosFoneAss = new PdfPCell();
	        pCellpDadosFoneAss.addElement(pDadosFoneAss);
	        pCellpDadosFoneAss.setPaddingBottom(1);
	        pCellpDadosFoneAss.setPaddingTop(0);
	        pCellpDadosFoneAss.setFixedHeight(33);

	        Paragraph pDadosFone1Ass = new Paragraph();
	        pDadosFone1Ass.add(new Phrase(" Fone Alternativo 1:"+"\n",f12));
	        pDadosFone1Ass.add(new Phrase(" "+fone2DDD+" "+fone2,f12b));
	        PdfPCell pCellpDadosFone1Ass = new PdfPCell();
	        pCellpDadosFone1Ass.addElement(pDadosFone1Ass);
	        pCellpDadosFone1Ass.setPaddingBottom(1);
	        pCellpDadosFone1Ass.setPaddingTop(0);
	        pCellpDadosFone1Ass.setFixedHeight(33);
	        
	        Paragraph pDadosFone2Ass = new Paragraph();
	        pDadosFone2Ass.add(new Phrase(" Fone Alternativo 2:"+"\n",f12));
	        pDadosFone2Ass.add(new Phrase(" "+fone3DDD+" "+fone3,f12b));
	        PdfPCell pCellpDadosFone2Ass = new PdfPCell();
	        pCellpDadosFone2Ass.addElement(pDadosFone2Ass);
	        pCellpDadosFone2Ass.setPaddingBottom(1);
	        pCellpDadosFone2Ass.setPaddingTop(0);
	        pCellpDadosFone2Ass.setFixedHeight(33);
	        
	        Paragraph pDadosFone3Ass = new Paragraph();
	        pDadosFone3Ass.add(new Phrase(" Fone Alternativo 3:"+"\n",f12));
	        pDadosFone3Ass.add(new Phrase(" "+fone4DDD+" "+fone4,f12b));
	        PdfPCell pCellpDadosFone3Ass = new PdfPCell();
	        pCellpDadosFone3Ass.addElement(pDadosFone3Ass);
	        pCellpDadosFone3Ass.setPaddingBottom(1);
	        pCellpDadosFone3Ass.setPaddingTop(0);
	        pCellpDadosFone3Ass.setFixedHeight(33);
	        
	        PdfPTable tbDadosFoneAss= new PdfPTable(new float[] {1f, 1f, 1f, 1f});
	        tbDadosFoneAss.setWidthPercentage(100f);
	        tbDadosFoneAss.setSpacingAfter(3);
	        tbDadosFoneAss.addCell(pCellpDadosFoneAss);
	        tbDadosFoneAss.addCell(pCellpDadosFone1Ass);
	        tbDadosFoneAss.addCell(pCellpDadosFone2Ass);
	        tbDadosFoneAss.addCell(pCellpDadosFone3Ass);
	        	        
	        //4°Linha
	        String clienteEmail = "";
	        if(cliente.getEmail()!=null){
	        	clienteEmail = cliente.getEmail() != null ? cliente.getEmail() : "";
	        	clienteEmail = cliente.getEmailAlternativo() != null ? clienteEmail+" , "+cliente.getEmailAlternativo() : clienteEmail;
	        }
	    	Paragraph pAssEmail= new Paragraph();	
	    	pAssEmail.add(new Phrase(" Endereço de E-mail:"+"\n",f12));	
	    	pAssEmail.add(new Phrase(" "+clienteEmail,f12b));	
			PdfPCell pCellpAssEmail= new PdfPCell();
			pCellpAssEmail.addElement(pAssEmail);
			pCellpAssEmail.setPaddingBottom(1);
			pCellpAssEmail.setPaddingTop(0);
			pCellpAssEmail.setFixedHeight(33);
			
	        PdfPTable tbEmailAss= new PdfPTable(new float[] {1f});
	        tbEmailAss.setWidthPercentage(100f);
	        tbEmailAss.addCell(pCellpAssEmail);
	        
	        //Cell ROOT DADOS DO CLIENTE
	        PdfPCell pCellpDadosAss= new PdfPCell();
	        pCellpDadosAss.setBorderWidthRight(0);
	        pCellpDadosAss.addElement(tbDadosAss);
	        pCellpDadosAss.addElement(tbDadosRgAss);
	        pCellpDadosAss.addElement(tbDadosFoneAss);
	        pCellpDadosAss.addElement(tbEmailAss);
	        pCellpDadosAss.setBorderWidthLeft(0);
	        pCellpDadosAss.setPaddingBottom(0);
	        pCellpDadosAss.setPaddingTop(0);
			
	        PdfPTable tbDadosRootAss = new PdfPTable(new float[] {0.40f,0.10f, 4.40f,});
	        tbDadosRootAss.setWidthPercentage(100f);
	        tbDadosRootAss.setSpacingAfter(8);
	        tbDadosRootAss.addCell(pCellpDadosLateralAss);			
	        tbDadosRootAss.addCell(pCellVazio);	
	        tbDadosRootAss.addCell(pCellpDadosAss);
	        doc.add(tbDadosRootAss);

	        
	        //ENDEREÇOS DO ASSINANTE
	        // Linha Lateral Principal            
	        PdfTemplate templatePrinc = pdfWriter.getDirectContent().createTemplate(0.10f, 50);
	        BaseFont bfPrinc = BaseFont.createFont("Helvetica", "winansi", false);
	        String textPrinc = "PRINCIPAL     ";
	        float sizePrinc = 12;
	        float widthPrinc = bfPrinc.getWidthPoint(textPrinc, sizePrinc);
	        templatePrinc.beginText();
	        templatePrinc.setFontAndSize(bfPrinc, sizePrinc);
	        templatePrinc.setTextMatrix(0,17);
	        templatePrinc.showText(textPrinc);
	        templatePrinc.endText();
	        templatePrinc.setWidth(widthPrinc);
	        
	        Image imgPrinc = Image.getInstance(templatePrinc);
	        imgPrinc.setRotationDegrees(90);
	        
	        PdfPCell pCellpDadosLateralPrinc = new PdfPCell();
	        pCellpDadosLateralPrinc.addElement(imgPrinc);
	        pCellpDadosLateralPrinc.setBackgroundColor(new BaseColor(232, 235, 237));
	        
	        // 1ª Linha
	        Paragraph pDadosASSEnd = new Paragraph();
	        pDadosASSEnd.add(new Phrase(" Endereço:"+"\n",f12));
	        pDadosASSEnd.add(new Phrase(" "+cliente.getEndereco_principal().getEndereco(),f12b));
	        PdfPCell pCellpDadosASSEndVl = new PdfPCell();
	        pCellpDadosASSEndVl.addElement(pDadosASSEnd);
	        pCellpDadosASSEndVl.setPaddingBottom(1);
	        pCellpDadosASSEndVl.setPaddingTop(0);
	        pCellpDadosASSEndVl.setFixedHeight(33);
	        	        
	        Paragraph pDadosAssN = new Paragraph();	
	        pDadosAssN.add(new Phrase(" Nº:"+"\n",f12));	
	        pDadosAssN.add(new Phrase(" "+cliente.getEndereco_principal().getNumero(),f12b));	
	        PdfPCell pCellpDadosAssN= new PdfPCell();
	        pCellpDadosAssN.addElement(pDadosAssN);
	        pCellpDadosAssN.setPaddingBottom(1);
	        pCellpDadosAssN.setPaddingTop(0);
	        pCellpDadosAssN.setFixedHeight(33);
	        
	        Paragraph pDadosAssComp = new Paragraph();	
	        pDadosAssComp.add(new Phrase(" Complemento:"+"\n",f12));	
	        pDadosAssComp.add(new Phrase(" "+cliente.getEndereco_principal().getComplemento(),f12b));
	        PdfPCell pCellpDadosAssCompVl = new PdfPCell();
	        pCellpDadosAssCompVl.addElement(pDadosAssComp);
	        pCellpDadosAssCompVl.setPaddingBottom(1);
	        pCellpDadosAssCompVl.setPaddingTop(0);
	        pCellpDadosAssCompVl.setFixedHeight(33);

	        PdfPTable tbDadosAssEnd = new PdfPTable(new float[] {1f, 0.20f, 0.40f});
	        tbDadosAssEnd.setWidthPercentage(100f);
	        tbDadosAssEnd.setSpacingAfter(3);
	        tbDadosAssEnd.addCell(pCellpDadosASSEndVl);
	        tbDadosAssEnd.addCell(pCellpDadosAssN);
	        tbDadosAssEnd.addCell(pCellpDadosAssCompVl);
	        
	        // 2ª Linha
	        Paragraph pDadosAssBairro = new Paragraph();
	        pDadosAssBairro.add(new Phrase(" Bairro:"+"\n",f12));
	        pDadosAssBairro.add(new Phrase(" "+cliente.getEndereco_principal().getBairro(),f12b));
	        PdfPCell pCellpDadosAssBairro = new PdfPCell();
	        pCellpDadosAssBairro.addElement(pDadosAssBairro);
	        pCellpDadosAssBairro.setPaddingBottom(1);
	        pCellpDadosAssBairro.setPaddingTop(0);
	        pCellpDadosAssBairro.setFixedHeight(33);

	        Paragraph pDadosAssCidade = new Paragraph();	
	        pDadosAssCidade.add(new Phrase(" Cidade:"+"\n",f12));	
	        pDadosAssCidade.add(new Phrase(" "+cliente.getEndereco_principal().getCidade(),f12b));	
	        PdfPCell pCellpDadosAssCidade = new PdfPCell();
	        pCellpDadosAssCidade.addElement(pDadosAssCidade);
	        pCellpDadosAssCidade.setPaddingBottom(1);
	        pCellpDadosAssCidade.setPaddingTop(0);
	        pCellpDadosAssCidade.setFixedHeight(33);

	        Paragraph pDadosAssUf = new Paragraph();	
	        pDadosAssUf.add(new Phrase(" Estado:"+"\n",f12));	
	        pDadosAssUf.add(new Phrase(" "+cliente.getEndereco_principal().getUf(),f12b));
	        PdfPCell pCellpDadosAssUf = new PdfPCell();
	        pCellpDadosAssUf.addElement(pDadosAssUf);
	        pCellpDadosAssUf.setPaddingBottom(1);
	        pCellpDadosAssUf.setPaddingTop(0);
	        pCellpDadosAssUf.setFixedHeight(33);
	        
	        Paragraph pDadosAssCep = new Paragraph();	
	        pDadosAssCep.add(new Phrase(" CEP:"+"\n",f12));	
	        pDadosAssCep.add(new Phrase(" "+cliente.getEndereco_principal().getCep(),f12b));
	        PdfPCell pCellpDadosAssCep = new PdfPCell();
	        pCellpDadosAssCep.addElement(pDadosAssCep);
	        pCellpDadosAssCep.setPaddingBottom(1);
	        pCellpDadosAssCep.setPaddingTop(0);
	        pCellpDadosAssCep.setFixedHeight(33);
	        	        
	        PdfPTable tbDadosAssBairro= new PdfPTable(new float[] {0.60f, 0.40f, 0.20f, 0.40f});
	        tbDadosAssBairro.setWidthPercentage(100f);
	        tbDadosAssBairro.addCell(pCellpDadosAssBairro);
	        tbDadosAssBairro.addCell(pCellpDadosAssCidade);
	        tbDadosAssBairro.addCell(pCellpDadosAssUf);
	        tbDadosAssBairro.addCell(pCellpDadosAssCep);
	        	        
	        PdfPCell pCellpAssPrinc= new PdfPCell();
	        pCellpAssPrinc.setBorderWidthRight(0);
	        pCellpAssPrinc.addElement(tbDadosAssEnd);
	        pCellpAssPrinc.addElement(tbDadosAssBairro);
	        pCellpAssPrinc.setBorderWidthLeft(0);
	        pCellpAssPrinc.setPaddingTop(0);
	             
	        PdfPTable tbDadosRootPrinc = new PdfPTable(new float[] {0.40f,0.10f, 4.40f,});
	        tbDadosRootPrinc.setWidthPercentage(100f);
	        tbDadosRootPrinc.setSpacingAfter(3);			
	        tbDadosRootPrinc.addCell(pCellpDadosLateralPrinc);			
	        tbDadosRootPrinc.addCell(pCellVazio);	
	        tbDadosRootPrinc.addCell(pCellpAssPrinc);

	        
	        // Linha Lateral Cobrança              
	        PdfTemplate templateCobran = pdfWriter.getDirectContent().createTemplate(0.10f, 50);
	        BaseFont bfCobran = BaseFont.createFont("Helvetica", "winansi", false);
	        String textCobran = "COBRANÇA     ";
	        float sizeCobran = 12;
	        float widthCobran = bfCobran.getWidthPoint(textCobran, sizeCobran);
	        templateCobran.beginText();
	        templateCobran.setFontAndSize(bfCobran, sizeCobran);
	        templateCobran.setTextMatrix(0, 17);
	        templateCobran.showText(textCobran);
	        templateCobran.endText();
	        templateCobran.setWidth(widthCobran);
	        
	        Image imgCobran = Image.getInstance(templateCobran);
	        imgCobran.setRotationDegrees(90);
	        
	        PdfPCell pCellpDadosLateralCobran = new PdfPCell();
	        pCellpDadosLateralCobran.addElement(imgCobran);
	        pCellpDadosLateralCobran.setBackgroundColor(new BaseColor(232, 235, 237));
	        
	        // 1ª Linha
	        Paragraph pDadosCobranEnd = new Paragraph();
	        pDadosCobranEnd.add(new Phrase(" Endereço:"+"\n",f12));
	        pDadosCobranEnd.add(new Phrase(" "+cliente.getEndereco_principal().getEndereco(),f12b));
	        PdfPCell pCellpDadosCobranEndVl = new PdfPCell();
	        pCellpDadosCobranEndVl.addElement(pDadosCobranEnd);
	        pCellpDadosCobranEndVl.setPaddingBottom(1);
	        pCellpDadosCobranEndVl.setPaddingTop(0);
	        pCellpDadosCobranEndVl.setFixedHeight(33);
	        
	        
	        Paragraph pDadosCobranN = new Paragraph();	
	        pDadosCobranN.add(new Phrase(" Nº:"+"\n",f12));	
	        pDadosCobranN.add(new Phrase(" "+cliente.getEndereco_principal().getNumero(),f12b));	
	        PdfPCell pCellpDadosCobranN= new PdfPCell();
	        pCellpDadosCobranN.addElement(pDadosCobranN);
	        pCellpDadosCobranN.setPaddingBottom(1);
	        pCellpDadosCobranN.setPaddingTop(0);
	        
	        Paragraph pDadosCobranComp = new Paragraph();	
	        pDadosCobranComp.add(new Phrase(" Complemento:"+"\n",f12));	
	        pDadosCobranComp.add(new Phrase(" "+cliente.getEndereco_principal().getComplemento(),f12b));
	        PdfPCell pCellpDadosCobranCompVl = new PdfPCell();
	        pCellpDadosCobranCompVl.addElement(pDadosCobranComp);
	        pCellpDadosCobranCompVl.setPaddingBottom(1);
	        pCellpDadosCobranCompVl.setPaddingTop(0);
	        pCellpDadosCobranCompVl.setFixedHeight(33);
	        
	        PdfPTable tbDadosCobranEnd = new PdfPTable(new float[] {1f, 0.20f, 0.40f});
	        tbDadosCobranEnd.setWidthPercentage(100f);
	        tbDadosCobranEnd.setSpacingAfter(3);
	        tbDadosCobranEnd.addCell(pCellpDadosCobranEndVl);
	        tbDadosCobranEnd.addCell(pCellpDadosCobranN);
	        tbDadosCobranEnd.addCell(pCellpDadosCobranCompVl);
	        
	        // 2ª Linha
	        Paragraph pDadosCobranBairro = new Paragraph();
	        pDadosCobranBairro.add(new Phrase(" Bairro:"+"\n",f12));
	        pDadosCobranBairro.add(new Phrase(" "+cliente.getEndereco_principal().getBairro(),f12b));
	        PdfPCell pCellpDadosCobranBairro = new PdfPCell();
	        pCellpDadosCobranBairro.addElement(pDadosCobranBairro);
	        pCellpDadosCobranBairro.setPaddingBottom(1);
	        pCellpDadosCobranBairro.setPaddingTop(0);
	        pCellpDadosCobranBairro.setFixedHeight(33);
	        
	        Paragraph pDadosCobranCidade = new Paragraph();	
	        pDadosCobranCidade.add(new Phrase(" Cidade:"+"\n",f12));	
	        pDadosCobranCidade.add(new Phrase(" "+cliente.getEndereco_principal().getCidade(),f12b));	
	        PdfPCell pCellpDadosCobranCidade = new PdfPCell();
	        pCellpDadosCobranCidade.addElement(pDadosCobranCidade);
	        pCellpDadosCobranCidade.setPaddingBottom(1);
	        pCellpDadosCobranCidade.setPaddingTop(0);
	        pCellpDadosCobranCidade.setFixedHeight(33);
	        
	        Paragraph pDadosCobranUf = new Paragraph();	
	        pDadosCobranUf.add(new Phrase(" Estado:"+"\n",f12));	
	        pDadosCobranUf.add(new Phrase(" "+cliente.getEndereco_principal().getUf(),f12b));
	        PdfPCell pCellpDadosCobranUf = new PdfPCell();
	        pCellpDadosCobranUf.addElement(pDadosCobranUf);
	        pCellpDadosCobranUf.setPaddingBottom(1);
	        pCellpDadosCobranUf.setPaddingTop(0);
	        pCellpDadosCobranUf.setFixedHeight(33);
	        
	        Paragraph pDadosCobranCep = new Paragraph();	
	        pDadosCobranCep.add(new Phrase(" CEP:"+"\n",f12));	
	        pDadosCobranCep.add(new Phrase(" "+cliente.getEndereco_principal().getCep(),f12b));
	        PdfPCell pCellpDadosCobranCep = new PdfPCell();
	        pCellpDadosCobranCep.addElement(pDadosCobranCep);
	        pCellpDadosCobranCep.setPaddingBottom(1);
	        pCellpDadosCobranCep.setPaddingTop(0);
	        pCellpDadosCobranCep.setFixedHeight(33);
	        
	        PdfPTable tbDadosCobranBairro= new PdfPTable(new float[] {0.60f, 0.40f, 0.20f, 0.40f});
	        tbDadosCobranBairro.setWidthPercentage(100f);
	        tbDadosCobranBairro.addCell(pCellpDadosCobranBairro);
	        tbDadosCobranBairro.addCell(pCellpDadosCobranCidade);
	        tbDadosCobranBairro.addCell(pCellpDadosCobranUf);
	        tbDadosCobranBairro.addCell(pCellpDadosCobranCep);
	        
	        PdfPCell pCellpCobranPrinc= new PdfPCell();
	        pCellpCobranPrinc.setBorderWidthRight(0);
	        pCellpCobranPrinc.addElement(tbDadosCobranEnd);
	        pCellpCobranPrinc.addElement(tbDadosCobranBairro);
	        pCellpCobranPrinc.setBorderWidthLeft(0);
	        pCellpCobranPrinc.setPaddingTop(0);
	        pCellpCobranPrinc.setPaddingBottom(0);
	        
	        PdfPTable tbDadosRootCobranc = new PdfPTable(new float[] {0.40f,0.10f, 4.40f,});
	        tbDadosRootCobranc.setWidthPercentage(100f);
	        tbDadosRootCobranc.setSpacingAfter(3);			
	        tbDadosRootCobranc.addCell(pCellpDadosLateralCobran);			
	        tbDadosRootCobranc.addCell(pCellVazio);	
	        tbDadosRootCobranc.addCell(pCellpCobranPrinc);
	        
	     // Linha Lateral Instalação     
	        PdfTemplate templateInstal = pdfWriter.getDirectContent().createTemplate(0.10f, 50);
	        BaseFont bfInstal = BaseFont.createFont("Helvetica", "winansi", false);
	        String textInstal = "INSTALAÇÃO            ";
	        float sizeInstal = 12;
	        float widthInstal = bfInstal.getWidthPoint(textInstal, sizeInstal);
	        templateInstal.beginText();
	        templateInstal.setFontAndSize(bfInstal, sizeInstal);
	        templateInstal.setTextMatrix(0, 17);
	        templateInstal.showText(textInstal);
	        templateInstal.endText();
	        templateInstal.setWidth(widthInstal);
	        
	        Image imgInstal = Image.getInstance(templateInstal);
	        imgInstal.setRotationDegrees(90);
	        
	        PdfPCell pCellpDadosLateralInstal = new PdfPCell();
	        pCellpDadosLateralInstal.addElement(imgInstal);
	        pCellpDadosLateralInstal.setBackgroundColor(new BaseColor(232, 235, 237));
	        
	        // 1ª Linha
	        Paragraph pDadosInstalEnd = new Paragraph();
	        pDadosInstalEnd.add(new Phrase(" Endereço:"+"\n",f12));
	        pDadosInstalEnd.add(new Phrase(" "+end.getEndereco(),f12b));
	        PdfPCell pCellpDadosInstalEnd = new PdfPCell();
	        pCellpDadosInstalEnd.addElement(pDadosInstalEnd);
	        pCellpDadosInstalEnd.setPaddingBottom(1);
	        pCellpDadosInstalEnd.setPaddingTop(0);
	        pCellpDadosInstalEnd.setFixedHeight(33);
	        
	        
	        Paragraph pDadosInstalN = new Paragraph();	
	        pDadosInstalN.add(new Phrase(" Nº:"+"\n",f12));	
	        pDadosInstalN.add(new Phrase(" "+end.getNumero(),f12b));	
	        PdfPCell pCellpDadosInstalN = new PdfPCell();
	        pCellpDadosInstalN.addElement(pDadosInstalN );
	        pCellpDadosInstalN.setPaddingBottom(1);
	        pCellpDadosInstalN.setPaddingTop(0);
	        pCellpDadosInstalN.setFixedHeight(33);

	        Paragraph pDadosInstalComp = new Paragraph();	
	        pDadosInstalComp.add(new Phrase(" Complemento:"+"\n",f12));	
	        pDadosInstalComp.add(new Phrase(" "+end.getComplemento(),f12b));
	        PdfPCell pCellpDadosInstalComp = new PdfPCell();
	        pCellpDadosInstalComp.addElement(pDadosInstalComp);
	        pCellpDadosInstalComp.setPaddingBottom(1);
	        pCellpDadosInstalComp.setPaddingTop(0);
	        pCellpDadosInstalComp.setFixedHeight(33);
	        
	        PdfPTable tbDadosInstalEnd = new PdfPTable(new float[] {1f, 0.20f, 0.40f});
	        tbDadosInstalEnd.setWidthPercentage(100f);
	        tbDadosInstalEnd.setSpacingAfter(3);
	        tbDadosInstalEnd.addCell(pCellpDadosInstalEnd);
	        tbDadosInstalEnd.addCell(pCellpDadosInstalN);
	        tbDadosInstalEnd.addCell(pCellpDadosInstalComp);
	        
	        // 2ª Linha
	        Paragraph pDadosInstalBairro = new Paragraph();
	        pDadosInstalBairro.add(new Phrase(" Bairro:"+"\n",f12));
	        pDadosInstalBairro.add(new Phrase(" "+end.getBairro(),f12b));
	        PdfPCell pCellpDadosInstalBairro = new PdfPCell();
	        pCellpDadosInstalBairro.addElement(pDadosInstalBairro);
	        pCellpDadosInstalBairro.setPaddingBottom(1);
	        pCellpDadosInstalBairro.setPaddingTop(0);
	        pCellpDadosInstalBairro.setFixedHeight(33);
	        
	        Paragraph pDadosInstalCidade = new Paragraph();	
	        pDadosInstalCidade.add(new Phrase(" Cidade:"+"\n",f12));	
	        pDadosInstalCidade.add(new Phrase(" "+end.getCidade(),f12b));	
	        PdfPCell pCellpDadosInstalCidade = new PdfPCell();
	        pCellpDadosInstalCidade.addElement(pDadosInstalCidade);
	        pCellpDadosInstalCidade.setPaddingBottom(1);
	        pCellpDadosInstalCidade.setPaddingTop(0);
	        pCellpDadosInstalCidade.setFixedHeight(33);
	        
	        Paragraph pDadosInstalUf = new Paragraph();	
	        pDadosInstalUf.add(new Phrase(" Estado:"+"\n",f12));	
	        pDadosInstalUf.add(new Phrase(" "+end.getUf(),f12b));
	        PdfPCell pCellpDadosInstalUf = new PdfPCell();
	        pCellpDadosInstalUf.addElement(pDadosInstalUf);
	        pCellpDadosInstalUf.setPaddingBottom(1);
	        pCellpDadosInstalUf.setPaddingTop(0);
	        pCellpDadosInstalUf.setFixedHeight(33);
	        
	        Paragraph pDadosInstalCep = new Paragraph();	
	        pDadosInstalCep.add(new Phrase(" CEP:"+"\n",f12));	
	        pDadosInstalCep.add(new Phrase(" "+end.getCep(),f12b));
	        PdfPCell pCellpDadosInstalCep = new PdfPCell();
	        pCellpDadosInstalCep.addElement(pDadosInstalCep);
	        pCellpDadosInstalCep.setPaddingBottom(1);
	        pCellpDadosInstalCep.setPaddingTop(0);
	        pCellpDadosInstalCep.setFixedHeight(33);
	        
	        PdfPTable tbDadosInstalBairro= new PdfPTable(new float[] {0.60f, 0.40f, 0.20f, 0.40f});
	        tbDadosInstalBairro.setWidthPercentage(100f);
	        tbDadosInstalBairro.setSpacingAfter(3);
	        tbDadosInstalBairro.addCell(pCellpDadosInstalBairro);
	        tbDadosInstalBairro.addCell(pCellpDadosInstalCidade);
	        tbDadosInstalBairro.addCell(pCellpDadosInstalUf);
	        tbDadosInstalBairro.addCell(pCellpDadosInstalCep);
	        
	        //Linha3
	        Paragraph pDadosInstalRef = new Paragraph();	
	        pDadosInstalRef.add(new Phrase(" Ponto de referência:"+"\n",f12));	
	        pDadosInstalRef.add(new Phrase(" "+end.getReferencia() != null ? end.getReferencia() : "",f12b));
	        PdfPCell pCellpDadosInstalRef = new PdfPCell();
	        pCellpDadosInstalRef.addElement(pDadosInstalRef);
	        pCellpDadosInstalRef.setPaddingBottom(1);
	        pCellpDadosInstalRef.setPaddingTop(0);
	        pCellpDadosInstalRef.setFixedHeight(33);
	        
	        PdfPTable tbDadosInstalRef= new PdfPTable(new float[] {1f});
	        tbDadosInstalRef.setWidthPercentage(100f);
	        tbDadosInstalRef.addCell(pCellpDadosInstalRef);
	        
	        PdfPCell pCellInstalPrinc= new PdfPCell();
	        pCellInstalPrinc.setBorderWidthRight(0);
	        pCellInstalPrinc.addElement(tbDadosInstalEnd);
	        pCellInstalPrinc.addElement(tbDadosInstalBairro);
	        pCellInstalPrinc.addElement(tbDadosInstalRef);
	        pCellInstalPrinc.setBorderWidthLeft(0);
	        pCellInstalPrinc.setPaddingTop(0);
	        pCellInstalPrinc.setPaddingBottom(0);
	        
	        PdfPTable tbDadosRootInstal = new PdfPTable(new float[] {0.40f,0.10f, 4.40f,});
	        tbDadosRootInstal.setWidthPercentage(100f);		
	        tbDadosRootInstal.addCell(pCellpDadosLateralInstal);			
	        tbDadosRootInstal.addCell(pCellVazio);	
	        tbDadosRootInstal.addCell(pCellInstalPrinc);	        
	        
	        
	     // Linha Lateral Endereço ROOT     
	        PdfTemplate templateEndRoot = pdfWriter.getDirectContent().createTemplate(0.10f, 50);
	        BaseFont bfEndRoot = BaseFont.createFont("Helvetica", "winansi", false);
	        String textEndRoot = "ENDEREÇO DO ASSINANTE               ";
	        float sizeEndRoot = 26;
	        float widthEndRoot = bfEndRoot.getWidthPoint(textEndRoot, sizeEndRoot);
	        templateEndRoot.beginText();
	        templateEndRoot.setFontAndSize(bfEndRoot, sizeEndRoot);
	        templateEndRoot.setTextMatrix(0,14);
	        templateEndRoot.showText(textEndRoot);
	        templateEndRoot.endText();
	        templateEndRoot.setWidth(widthEndRoot);
	        
	        Image imgEndRoot = Image.getInstance(templateEndRoot);
	        imgEndRoot.setRotationDegrees(90);
	        
	        PdfPCell pCellImgEndRoot = new PdfPCell();
	        pCellImgEndRoot.addElement(imgEndRoot);
	        
	        //Cell Root Endereço
	        PdfPCell pCellEndRoot= new PdfPCell();
	        pCellEndRoot.setBorderWidthRight(0);
	        pCellEndRoot.addElement(tbDadosRootPrinc);
	        pCellEndRoot.addElement(tbDadosRootCobranc);
	        pCellEndRoot.addElement(tbDadosRootInstal);
	        pCellEndRoot.setBorderWidthLeft(0);
	        pCellEndRoot.setPadding(0);
	        
	        //RootEndereço do cliente
	        PdfPTable tbRootEnd = new PdfPTable(new float[] {0.10f, 2.00f,});
	        tbRootEnd.setWidthPercentage(100f);
//	        tbRootEnd.setSpacingAfter(5);			
	        tbRootEnd.addCell(pCellImgEndRoot);			
	        tbRootEnd.addCell(pCellEndRoot);	
	        doc.add(tbRootEnd);
	        
	        
	       //Roda Pé Page 1
	       Paragraph rodaPePg = new Paragraph("TERMO DE ADESÃO - "+empresa.getRazao_social()+" - Página 1 de 4                                        emitido por: "+OpusERP4UI.getUsuarioLogadoUI().getUsername(),f8b);
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
//-----------------------------------PAGINA 2-------------------------------------------------------------------------------------------------------
	        
	       
	        
	        //Clausula Primeira
	        Paragraph pClausla = new Paragraph("CLÁUSULA PRIMEIRA - DA ADESÃO AO CONTRATO DE PRESTAÇÃO DE SERVIÇOS DE",f12bUnder);
	        pClausla.setAlignment(Element.ALIGN_CENTER);
	        Paragraph pClauslaCont = new Paragraph("TELECOMUNICAÇÕES E AO CONTRATO DE COMODATO DE EQUIPAMENTO",f12bUnder);
	        pClauslaCont.setAlignment(Element.ALIGN_CENTER);	        
	        doc.add(pClausla);
	        doc.add(pClauslaCont);        
	        //1.1
	        String nCartorioSCM = "   ";
	        if(acessoCliente.getContrato().getContrato().getReg_cartorio_scm()!=null){
	        	nCartorioSCM = acessoCliente.getContrato().getContrato().getReg_cartorio_scm(); 
	        }
	        Paragraph pClausla1 = new Paragraph();	        
	        pClausla1.add(new Phrase("1.1   ",f12b));
	        pClausla1.add(new Phrase("Pelo presente instrumento, o",f12));
	        pClausla1.add(new Phrase(" ASSINANTE ",f12b));
	        pClausla1.add(new Phrase("adere aos termos e condições do",f12));
	        pClausla1.add(new Phrase(" CONTRATO DE PRESTAÇÃO DE SERVIÇOS DE TELECOMUNICAÇÕES",f12b));
	        pClausla1.add(new Phrase(", registrado junto ao",f12));
	        pClausla1.add(new Phrase(" Cartório de Registro de Títulos e Documentos , ",f12b));
	        pClausla1.add(new Phrase("do Município de ",f12));
	        pClausla1.add(new Phrase("Belo Jardim, ",f12b));
	        pClausla1.add(new Phrase(" estado do",f12));
	        pClausla1.add(new Phrase(" Pernambuco, ",f12b));
	        pClausla1.add(new Phrase("sob o nº ",f12));
	        pClausla1.add(new Phrase(nCartorioSCM,f12b));
	        pClausla1.add(new Phrase(", e disponível no endereço virtual eletrônico",f12));
	        pClausla1.add(new Phrase(" www.digitalonline.com.br.",f12bUnder));
	        pClausla1.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausla1);
	        
	        //1.2
	        String nCartorio = "   ";
	        if(acessoCliente.getContrato().getContrato().getReg_cartorio()!=null){
	        	nCartorio = acessoCliente.getContrato().getContrato().getReg_cartorio(); 
	        }
	        Paragraph pClausla1_2 = new Paragraph();
	        pClausla1_2.add(new Phrase("1.2   ",f12b));
	        pClausla1_2.add(new Phrase("Pelo mesmo instrumento, o",f12));
	        pClausla1_2.add(new Phrase(" ASSINANTE ",f12b));
	        pClausla1_2.add(new Phrase("adere aos termos e condições do",f12));	        
	        pClausla1_2.add(new Phrase(" CONTRATO DE COMODATO DE EQUIPAMENTOS",f12b));
	        pClausla1_2.add(new Phrase(", registrado junto ao",f12));
	        pClausla1_2.add(new Phrase(" Cartório de Registro de Títulos e Documentos ",f12b));
	        pClausla1_2.add(new Phrase("do Município de ",f12));
	        pClausla1_2.add(new Phrase(" Belo Jardim, ",f12b));
	        pClausla1_2.add(new Phrase("no Estado do",f12));
	        pClausla1_2.add(new Phrase(" Pernambuco, ",f12b));
	        pClausla1_2.add(new Phrase("sob o nº " ,f12));
	        pClausla1_2.add(new Phrase(nCartorio,f12b));
	        pClausla1_2.add(new Phrase(" em " ,f12));
	        pClausla1_2.add(new Phrase(DataUtil.getDataExtensoMes(acessoCliente.getContrato().getContrato().getReg_cartorio_data()),f12b));
	        pClausla1_2.add(new Phrase(" e disponível no endereço virtual eletrônico",f12));
	        pClausla1_2.add(new Phrase(" www.digitalonline.com.br.",f12bUnder));
	        pClausla1_2.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausla1_2);
	        
	        //1.3
	        Paragraph pClausla1_3 = new Paragraph();
	        pClausla1_3.add(new Phrase("1.3   ",f12b));
	        pClausla1_3.add(new Phrase("O",f12));
	        pClausla1_3.add(new Phrase(" ASSINANTE ",f12b));
	        pClausla1_3.add(new Phrase("declara neste ato",f12));
	        pClausla1_3.add(new Phrase(" DETER PLENA CAPACIDADE PARA CELEBRAR O PRESENTE,"+
	        		" HAVER RECEBIDO, LIDO, COMPREENDIDO E CONCORDADO COM OS TERMOS E CONDIÇÕES DO "+
	        		"CONTRATO DE PRESTAÇÃO DOS SERVIÇOS DE TELECOMUNICAÇÕES.",f12b));
	        pClausla1_3.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausla1_3);
	        
	        //Clausula Segunda
	        Paragraph pClausla2 = new Paragraph("CLÁUSULA SEGUNDA - DO ENDEREÇO PARA INSTALAÇÃO",f12bUnder);
	        pClausla2.setAlignment(Element.ALIGN_CENTER);
	        pClausla2.setSpacingBefore(10);
	        doc.add(pClausla2);	        
	        
	        //2.1
	        Paragraph pClausla2_1 = new Paragraph();	               
	        pClausla2_1.add(new Phrase("2.1   ",f12b));
	        pClausla2_1.add(new Phrase("O",f12));
	        pClausla2_1.add(new Phrase(" ASSINANTE ",f12b));
	        pClausla2_1.add(new Phrase("indica o endereço (ponto de acesso) acima para instalação dos equipamentos necessários para a prestação dos serviços contratados.",f12));
	        pClausla2_1.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausla2_1);
	        
	        Paragraph pClausla2_2 = new Paragraph();	               
	        pClausla2_2.add(new Phrase("2.2   ",f12b));
	        pClausla2_2.add(new Phrase("O prazo para instalação do Serviço de Comunicação Multimídia (SCM) é de até ",f12));
	        pClausla2_2.add(new Phrase("15 (Quinze) dias,",f12b));
	        pClausla2_2.add(new Phrase(" contados da data da ciência da ",f12));
	        pClausla2_2.add(new Phrase("PRESTADORA, ",f12b));
	        pClausla2_2.add(new Phrase("da assinatura do presente ",f12));
	        pClausla2_2.add(new Phrase("TERMO DE ADESÃO ",f12b));
	        pClausla2_2.add(new Phrase("pelo ",f12));
	        pClausla2_2.add(new Phrase("ASSINANTE.",f12b));
	        pClausla2_2.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausla2_2);
	        
	        Paragraph pClausla2_3 = new Paragraph();	               
	        pClausla2_3.add(new Phrase("2.3   ",f12b));
	        pClausla2_3.add(new Phrase("Será observada previamente pela ",f12));
	        pClausla2_3.add(new Phrase("PRESTADORA ",f12b));
	        pClausla2_3.add(new Phrase("a viabilidade técnica e as condições climáticas e físicas para a instalação do serviço no endereço de instalação indicado pelo ",f12));
	        pClausla2_3.add(new Phrase("ASSINANTE.",f12b));
	        pClausla2_3.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausla2_3);
	        
	        //Clausula Terceira
	        Paragraph pClausla3 = new Paragraph("CLÁUSULA TERCEIRA - DO PLANO DE SERVIÇO",f12bUnder);
	        pClausla3.setAlignment(Element.ALIGN_CENTER);
	        pClausla3.setSpacingBefore(5);
	        doc.add(pClausla3);
	        //3.1
	        Paragraph pClausla3_1 = new Paragraph();
	        pClausla3_1.add(new Phrase("3.1   ",f12b));
	        pClausla3_1.add(new Phrase("A" ,f12));
	        pClausla3_1.add(new Phrase(" PRESTADORA ",f12b));
	        pClausla3_1.add(new Phrase("prestará o serviço de acordo com o ",f12));
	        pClausla3_1.add(new Phrase(" PLANO DE SERVIÇO ",f12b));
	        pClausla3_1.add(new Phrase("escolhido de forma espontânea pelo ",f12));
	        pClausla3_1.add(new Phrase(" ASSINANTE, ",f12b));
	        pClausla3_1.add(new Phrase(" conforme detalhamento abaixo:",f12));
	        pClausla3_1.setAlignment(Element.ALIGN_JUSTIFIED);
	        pClausla3_1.setSpacingBefore(1);
	        doc.add(pClausla3_1);
	        
	        //Linha 1
	        Paragraph pPlano = new Paragraph();
	        pPlano.add(new Phrase("PLANO",f12b));
	        pPlano.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellpPlano= new PdfPCell();
	        pCellpPlano.addElement(pPlano);
	        pCellpPlano.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellpPlano.setPaddingBottom(0);
	        pCellpPlano.setPaddingTop(0);
	        
	        Paragraph pTipoPlano = new Paragraph();
	        pTipoPlano.add(new Phrase("TIPO DE PLANO",f12b));
	        pTipoPlano.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellpTipoPlano= new PdfPCell();
	        pCellpTipoPlano.addElement(pTipoPlano);
	        pCellpTipoPlano.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellpTipoPlano.setPaddingBottom(0);
	        pCellpTipoPlano.setPaddingTop(0);

	        	        	        
	        Paragraph pPlanoVeloc = new Paragraph();
	        pPlanoVeloc.add(new Phrase("VELOCIDADE DE UPLOADS / DOWNLOADS",f12b));
	        pPlanoVeloc.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellpPlanoVeloc= new PdfPCell();
	        pCellpPlanoVeloc.addElement(pPlanoVeloc);
	        pCellpPlanoVeloc.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellpPlanoVeloc.setPaddingBottom(0);
	        pCellpPlanoVeloc.setPaddingTop(0);
	        //pCellpPlanoVeloc.setFixedHeight(38);
	        
	        Paragraph pPlanoGarantia = new Paragraph();
	        pPlanoGarantia.add(new Phrase(" FRANQUIA",f12b));
	        pPlanoGarantia.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellpPlanoGarantia = new PdfPCell();
	        pCellpPlanoGarantia.addElement(pPlanoGarantia);
	        pCellpPlanoGarantia.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellpPlanoGarantia.setPaddingBottom(0);
	        pCellpPlanoGarantia.setPaddingTop(0);
	       // pCellpPlanoGarantia.setFixedHeight(38);
	        
	        Paragraph pPlanoGarantiaVazio = new Paragraph("",f12b);
	        PdfPCell pCellpPlanoGarantiaVazio = new PdfPCell();
	        pCellpPlanoGarantiaVazio.addElement(pPlanoGarantiaVazio);
	        pCellpPlanoGarantiaVazio.setBorderWidth(0);
	        
	        PdfPTable tbTipoPlano = new PdfPTable(new float[] {0.05f,0.12f,0.39f,0.42f, 0.12f});
	        tbTipoPlano.setWidthPercentage(100f);		
	        tbTipoPlano.setSpacingBefore(3);
	        tbTipoPlano.addCell(pCellpPlanoGarantiaVazio);			
	        tbTipoPlano.addCell(pCellpPlano);
	        tbTipoPlano.addCell(pCellpTipoPlano);
	        tbTipoPlano.addCell(pCellpPlanoVeloc);		        
	        tbTipoPlano.addCell(pCellpPlanoGarantia);		        
	        tbTipoPlano.setSpacingBefore(30);
	        doc.add(tbTipoPlano);
	        
	        //Linha 2
	        Paragraph pPlanoVl = new Paragraph();
	        pPlanoVl.add(new Phrase(acessoCliente.getPlano().getNome(),f12b));
	        pPlanoVl.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellpPlanoVl= new PdfPCell();
	        pCellpPlanoVl.addElement(pPlanoVl);
	        pCellpPlanoVl.setPaddingTop(0);
	        
	        Paragraph pTipoPlanoVl = new Paragraph();
	        pTipoPlanoVl.add(new Phrase(acessoCliente.getPlano().getContrato_acesso().getNome(),f12b));
	        pTipoPlanoVl.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellpTipoPlanoVl= new PdfPCell();
	        pCellpTipoPlanoVl.addElement(pTipoPlanoVl);
	        pCellpTipoPlanoVl.setPaddingTop(0);

	       
			String rateLimit[] = acessoCliente.getPlano().getRate_limit().toString().split("/");
			String up = rateLimit[0];
			String down = rateLimit[1];

	        Paragraph pPlanoVelocVl = new Paragraph();
	        pPlanoVelocVl.add(new Phrase(up+"bps"+" upload / "+down+"bps"+" download",f12b));
	        pPlanoVelocVl.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellpPlanoVelocVl= new PdfPCell();
	        pCellpPlanoVelocVl.addElement(pPlanoVelocVl);
	        //pCellpPlanoVelocVl.setPaddingBottom(10);
	        //pCellpPlanoVelocVl.setPaddingBottom(1);
	        pCellpPlanoVelocVl.setPaddingTop(0);
	        //pCellpPlanoVelocVl.setFixedHeight(38);

	        Paragraph pPlanoGarantiaVl = new Paragraph();
	        pPlanoGarantiaVl.add(new Phrase(acessoCliente.getContrato().getContrato().getFranquia(),f12b));
	        pPlanoGarantiaVl.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellpPlanoGarantiaVl = new PdfPCell();
	        pCellpPlanoGarantiaVl.addElement(pPlanoGarantiaVl);
	        //pCellpPlanoGarantiaVl.setPaddingBottom(10);
	        pCellpPlanoGarantiaVl.setPaddingTop(0);
	        //pCellpPlanoGarantiaVl.setFixedHeight(38);
	        
	        Paragraph pPlanoGarantiaVazioVl = new Paragraph("X",f12b);
	        PdfPCell pCellpPlanoGarantiaVazioVl = new PdfPCell();
	        pPlanoGarantiaVazioVl.setAlignment(Element.ALIGN_CENTER);
	        pCellpPlanoGarantiaVazioVl.addElement(pPlanoGarantiaVazioVl);
	        pCellpPlanoGarantiaVazioVl.setPaddingTop(0);
	        //pCellpPlanoGarantiaVazioVl.setPaddingBottom(10);
	        
	        PdfPTable tbTipoPlanoVl = new PdfPTable(new float[] {0.05f,0.12f,0.39f,0.42f, 0.12f});
	        tbTipoPlanoVl.setWidthPercentage(100f);		
	        tbTipoPlanoVl.addCell(pCellpPlanoGarantiaVazioVl);			
	        tbTipoPlanoVl.addCell(pCellpPlanoVl);
	        tbTipoPlanoVl.addCell(pCellpTipoPlanoVl);	   
	        tbTipoPlanoVl.addCell(pCellpPlanoVelocVl);		        
	        tbTipoPlanoVl.addCell(pCellpPlanoGarantiaVl);		        
	        doc.add(tbTipoPlanoVl);
	        
	        Paragraph pTaxaTransmissao = new Paragraph();
	        pTaxaTransmissao.add(new Phrase("TAXA DE TRANSMISSÃO INSTANTÂNEA",f12b));
	        pTaxaTransmissao.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellTaxaTransmissao = new PdfPCell();
	        pCellTaxaTransmissao.addElement(pTaxaTransmissao);
	        pCellTaxaTransmissao.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellTaxaTransmissao.setPaddingBottom(1);
	        pCellTaxaTransmissao.setPaddingTop(0);
	        
	        
	        Paragraph pTaxaTransmissaoMedia = new Paragraph();
	        pTaxaTransmissaoMedia.add(new Phrase("TAXA DE TRANSMISSÃO MÉDIA",f12b));
	        pTaxaTransmissaoMedia.setAlignment(Element.ALIGN_CENTER);
	        
	        PdfPCell pCellTaxaTransmissaoMedia = new PdfPCell();
	        pCellTaxaTransmissaoMedia.addElement(pTaxaTransmissaoMedia);
	        pCellTaxaTransmissaoMedia.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellTaxaTransmissaoMedia.setPaddingBottom(1);
	        pCellTaxaTransmissaoMedia.setPaddingTop(0);
	        
	        
	        Paragraph pGarantiaBanda = new Paragraph();
	        pGarantiaBanda.add(new Phrase("GARANTIA DE BANDA",f12b));
	        pGarantiaBanda.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellGarantiaBanda= new PdfPCell();
	        pCellGarantiaBanda.addElement(pGarantiaBanda);
	        pCellGarantiaBanda.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellGarantiaBanda.setPaddingBottom(1);
	        pCellGarantiaBanda.setPaddingTop(0);
	        
	        
	        Paragraph pTramissaoInstantaneaValor = new Paragraph();
	        pTramissaoInstantaneaValor.add(new Phrase(acessoCliente.getPlano().getContrato_acesso().getTaxa_instantanea()+"% da Velocidade Contratada",f12b));
	        pTramissaoInstantaneaValor.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellpTramissaoInstantaneaValor = new PdfPCell();
	        pCellpTramissaoInstantaneaValor.addElement(pTramissaoInstantaneaValor);
	        pCellpTramissaoInstantaneaValor.setPaddingBottom(10);
	        pCellpTramissaoInstantaneaValor.setPaddingTop(0);
	        
	        
	        Paragraph pTaxaTramissaoMediaValor = new Paragraph();
	        pTaxaTramissaoMediaValor.add(new Phrase(acessoCliente.getPlano().getContrato_acesso().getTaxa_media()+" % da Velocidade Contratada",f12b));
	        pTaxaTramissaoMediaValor.setAlignment(Element.ALIGN_CENTER);
	        PdfPCell pCellpTaxaTramissaoMediaValor = new PdfPCell();
	        pCellpTaxaTramissaoMediaValor.addElement(pTaxaTramissaoMediaValor);
	        pCellpTaxaTramissaoMediaValor.setPaddingBottom(10);
	        pCellpTaxaTramissaoMediaValor.setPaddingTop(0);
	        
	        
	        Paragraph pTaxaVazioVl = new Paragraph("X",f12b);
	        PdfPCell pCellpTaxaVazioVl = new PdfPCell();
	        pPlanoGarantiaVazioVl.setAlignment(Element.ALIGN_CENTER);
	        pCellpPlanoGarantiaVazioVl.addElement(pTaxaVazioVl);
	        pCellpPlanoGarantiaVazioVl.setPaddingBottom(10);
	        
	        PdfPTable tbTaxas= new PdfPTable(new float[] {0.50f,0.70f,0.70f});
	        tbTaxas.setWidthPercentage(100f);		
	        tbTaxas.addCell(pCellpTaxaVazioVl);			
	        tbTaxas.addCell(pCellTaxaTransmissao);	        
	        tbTaxas.addCell(pCellTaxaTransmissaoMedia);         
	        tbTaxas.setSpacingBefore(30);
	        doc.add(tbTaxas);
	        
	        PdfPTable tbTaxasValores = new PdfPTable(new float[] {0.50f,0.70f,0.70f});
	        tbTaxasValores.setWidthPercentage(100f);		
	        tbTaxasValores.addCell(pCellGarantiaBanda);			
	        tbTaxasValores.addCell(pCellpTramissaoInstantaneaValor);	        
	        tbTaxasValores.addCell(pCellpTaxaTramissaoMediaValor);              
	        doc.add(tbTaxasValores);
	        
	        
	        
	        //Clausula Quarta
	        Paragraph pClausla4 = new Paragraph("CLÁUSULA QUARTA - DOS VALORES E FORMAS DE PAGAMENTO",f12bUnder);
	        pClausla4.setSpacingBefore(10);
	        pClausla4.setSpacingAfter(0);
	        pClausla4.setAlignment(Element.ALIGN_CENTER);
	        doc.add(pClausla4);
	        //4.1
	        Paragraph pClausla4_1 = new Paragraph();
	        pClausla4_1.add(new Phrase("4.1   ",f12b));
	        pClausla4_1.add(new Phrase("Para ativação e prestação dos serviços contratados, o" ,f12));
	        pClausla4_1.add(new Phrase(" ASSINANTE ",f12b));
	        pClausla4_1.add(new Phrase("deverá efetuar o pagamento em favor da ",f12));
	        pClausla4_1.add(new Phrase(" PRESTADORA ",f12b));
	        pClausla4_1.add(new Phrase("dos valores e na forma descrita abaixo.",f12));
	        pClausla4_1.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausla4_1);
	               
		  		
			boolean taxBoleto =acessoCliente != null  &&  acessoCliente.getPlano().getTaxa_boleto().equals("NAO") ? false : true;		
			
			if(cobrarTaxa){
				if(!taxBoleto){
					cobrarTaxa = false;
				}
			}
	        
	            
	        
	     //df.format(acessoCliente.getData_venc_contrato())   
	       //acessoCliente.getContrato().getTipo_contrato()
	        
	        Paragraph pValores = new Paragraph();
	        pValores.add(new Phrase("INSTALAÇÃO",f12b));
	        pValores.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellValores = new PdfPCell();
	        pCellValores.addElement(pValores);
	        pCellValores.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellValores.setPaddingBottom(4);
	        pCellValores.setPaddingTop(0);
	        
	        Paragraph pMensalidade = new Paragraph();
	        pMensalidade.add(new Phrase("MENSALIDADE",f12b));
	        pMensalidade.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellMensalidade = new PdfPCell();
	        pCellMensalidade.addElement(pMensalidade);
	        pCellMensalidade.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellMensalidade.setPaddingBottom(4);
	        pCellMensalidade.setPaddingTop(0);
	        //pCellValores.setFixedHeight(38);
	        
	        Paragraph pValorInstalacao = new Paragraph();
	        pValorInstalacao.add(new Phrase("Valor instalação:",f12));
	        pValorInstalacao.setAlignment(Element.ALIGN_LEFT);
	        
	        Paragraph pValorInstalacaoValor = new Paragraph();
	        pValorInstalacaoValor.add(new Phrase("ver O.S.",f12b));
	        pValorInstalacaoValor.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellpValoresInstalacao= new PdfPCell();
	        pCellpValoresInstalacao.addElement(pValorInstalacao);
	        pCellpValoresInstalacao.addElement(pValorInstalacaoValor);
	        pCellpValoresInstalacao.setPaddingTop(0);
	        pCellpValoresInstalacao.setPaddingBottom(3);
	        
	        
	        Paragraph pValorMensalidade = new Paragraph();
	        pValorMensalidade.add(new Phrase("Valor da Mensalidade:",f12));
	        pValorMensalidade.setAlignment(Element.ALIGN_LEFT);
	        
	        Paragraph pValorMensalidadeValor = new Paragraph();
	        
	        if(cobrarTaxa){	
	        	Double total = Real.formatStringToDBDouble(acessoCliente.getPlano().getValor()) + new Double(cb.getTaxa_boleto());	        	
	        	pValorMensalidadeValor.add(new Phrase(" R$ "+Real.formatDbToString(total.toString()),f12b));		        
	        }else{
	        	pValorMensalidadeValor.add(new Phrase(" R$ "+acessoCliente.getPlano().getValor(),f12b));
	        }	   
	        
	        pValorMensalidadeValor.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellpValorMensalidade= new PdfPCell();
	        pCellpValorMensalidade.addElement(pValorMensalidade);
	        pCellpValorMensalidade.addElement(pValorMensalidadeValor);
	        pCellpValorMensalidade.setPaddingTop(0);
	        pCellpValorMensalidade.setPaddingBottom(3);
	        
	        
	        Paragraph pDataVencimento = new Paragraph();
	        pDataVencimento.add(new Phrase("Vencimento:",f12));
	        pDataVencimento.setAlignment(Element.ALIGN_LEFT);
	        
	        List<ContasReceber> boletos = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(acessoCliente.getId());
	        String dia = "";
	        if(boletos != null){
	        	DateTime data = new DateTime(boletos.get(0).getData_vencimento());
	        	dia = String.valueOf(data.getDayOfMonth());
	        }
	        
	        Paragraph pDataVencimentoValor = new Paragraph();
	        pDataVencimentoValor.add(new Phrase("DIA "+dia,f12b));
	        pDataVencimentoValor.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellDataVencimento= new PdfPCell();
	        pCellDataVencimento.addElement(pDataVencimento);
	        pCellDataVencimento.addElement(pDataVencimentoValor);
	        pCellDataVencimento.setPaddingTop(0);
	        pCellDataVencimento.setPaddingBottom(3);
	        
	        Paragraph pFormaCobranca = new Paragraph();
	        pFormaCobranca.add(new Phrase("Forma de Cobrança:",f12));
	        pFormaCobranca.setAlignment(Element.ALIGN_LEFT);
	        
	        Paragraph pFormaCobrancaValor = new Paragraph();
	        pFormaCobrancaValor.add(new Phrase("BOLETO BANCÁRIO",f12b));
	        pFormaCobrancaValor.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellFormaCobranca= new PdfPCell();
	        pCellFormaCobranca.addElement(pFormaCobranca);
	        pCellFormaCobranca.addElement(pFormaCobrancaValor);
	        pCellFormaCobranca.setPaddingTop(0);
	        pCellFormaCobranca.setPaddingBottom(3);
	        
	        Paragraph pFormaEntrega= new Paragraph();
	        pFormaEntrega.add(new Phrase("Forma de Entrega:",f12));
	        pFormaEntrega.setAlignment(Element.ALIGN_LEFT);
	        
	        Paragraph pFormaEntregaValor = new Paragraph();
	        pFormaEntregaValor.add(new Phrase("CORREIOS/ E-MAIL",f12b));
	        pFormaEntregaValor.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellFormaEntrega = new PdfPCell();
	        pCellFormaEntrega.addElement(pFormaEntrega);
	        pCellFormaEntrega.addElement(pFormaEntregaValor);
	        pCellFormaEntrega.setPaddingTop(0);
	        pCellFormaEntrega.setPaddingBottom(3);
	        
	        Paragraph pVazioPadrao = new Paragraph("",f12);
	        pVazioPadrao.setAlignment(Element.ALIGN_RIGHT);
	        PdfPCell cellVazioPadrao = new PdfPCell();
	        cellVazioPadrao.addElement(pVazioPadrao);
	        cellVazioPadrao.setPaddingBottom(10);
	        cellVazioPadrao.setBorder(0); 
	        
	        PdfPTable tbValores = new PdfPTable(new float[] {0.25f,0.02f, 0.30f, 0.30f, 0.40f, 0.30f});
	        tbValores.setWidthPercentage(100f);
	        tbValores.addCell(pCellValores);
	        tbValores.addCell(pCellpPlanoGarantiaVazio);
	        tbValores.addCell(pCellMensalidade);
	        tbValores.addCell(pCellpPlanoGarantiaVazio);
	        tbValores.addCell(pCellpPlanoGarantiaVazio);
	        tbValores.addCell(pCellpPlanoGarantiaVazio);
	        tbValores.setSpacingBefore(30);
	        doc.add(tbValores);
	        
	        PdfPTable tbValorLinha2 = new PdfPTable(new float[] {0.25f,0.02f,0.30f,0.30f,0.40f, 0.30f});
	        tbValorLinha2.setWidthPercentage(100f);
	        tbValorLinha2.addCell(pCellpValoresInstalacao);
	        tbValorLinha2.addCell(pCellpPlanoGarantiaVazio);
	        tbValorLinha2.addCell(pCellpValorMensalidade);
	        tbValorLinha2.addCell(pCellDataVencimento);
	        tbValorLinha2.addCell(pCellFormaCobranca);
	        tbValorLinha2.addCell(pCellFormaEntrega);
	        //tbValorInst.setSpacingBefore(10);
	        //tbValorInst.setSpacingAfter(10);
	        doc.add(tbValorLinha2);
	        
	         Paragraph rodaPePg2 = new Paragraph("TERMO DE ADESÃO - "+empresa.getRazao_social()+" - Página 2 de 4                                         emitido por: "+OpusERP4UI.getUsuarioLogadoUI().getUsername(),f8b);
		     rodaPePg2.setAlignment(Element.ALIGN_RIGHT);

			 PdfPCell pCellRod2 = new PdfPCell();
		 	 pCellRod2.addElement(rodaPePg2);
		 	  
			 pCellRod2.setBorderWidthBottom(0);
			 pCellRod2.setBorderWidthRight(0);
			 pCellRod2.setBorderWidthLeft(0);

			 Rectangle page2 = doc.getPageSize();
			 PdfPTable foot2 = new PdfPTable(new float[] {1f});
			 foot2.setWidthPercentage(100f);
		     foot2.addCell(pCellRod2);
		     foot2.setTotalWidth(page2.getWidth() - doc.leftMargin() - doc.rightMargin());
		     foot2.writeSelectedRows(0, -4, doc.leftMargin(), doc.bottomMargin() + 10,pdfWriter.getDirectContent());
	                
	        //Visitas de assistencia
	        Paragraph pVisita = new Paragraph();
	        pVisita.add(new Phrase("ASSISTÊNCIA TÉCNICA / MANUTENÇÃO",f12b));
	        pVisita.setAlignment(Element.ALIGN_CENTER);
	        
	        PdfPCell pCellpVisita= new PdfPCell();
	        pCellpVisita.addElement(pVisita);
	        pCellpVisita.setPaddingTop(0);
	        pCellpVisita.setPaddingBottom(3);
	        
	        Paragraph pConsulta = new Paragraph();
	        pConsulta.add(new Phrase("Os valores referentes a Assistência Técnica / Manutenção devem ser consultados com a Prestadora previamente a solicitação de serviço.",f12));
	        PdfPCell pCellpConsulta= new PdfPCell();
	        pCellpConsulta.addElement(pConsulta);
	        pCellpConsulta.setPaddingTop(0);
	        pCellpConsulta.setPaddingBottom(3);

	        PdfPTable tbVisita = new PdfPTable(new float[] {0.50f,1f});
	        tbVisita.setWidthPercentage(100f);
	        tbVisita.addCell(pCellpVisita);
	        tbVisita.addCell(pCellpConsulta);
	        tbVisita.setSpacingBefore(10);
	        tbVisita.setSpacingAfter(10);
	        doc.add(tbVisita);
	        
	        Paragraph pClausula4ParagrafoUnico = new Paragraph();
	        pClausula4ParagrafoUnico.add(new Phrase("Parágrafo Único   ",f12b));
	        pClausula4ParagrafoUnico.add(new Phrase("As penalidades pelo não cumprimento das obrigações aqui assumidas estão dispostas no ",f12));
	        pClausula4ParagrafoUnico.add(new Phrase("Contrato de Prestação de Serviços de Telecomunicações, ",f12b));
	        pClausula4ParagrafoUnico.add(new Phrase("estando ciente o ",f12));
	        pClausula4ParagrafoUnico.add(new Phrase("ASSINANTE ",f12b));
	        pClausula4ParagrafoUnico.add(new Phrase("das condições impostas em caso de inadimplência.",f12));	     
	        pClausula4ParagrafoUnico.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausula4ParagrafoUnico);

	        doc.add(Chunk.NEXTPAGE);
	        
	        Paragraph pClausla5 = new Paragraph("CLÁUSULA QUINTA – DO COMODATO DE EQUIPAMENTOS",f12bUnder);
	        pClausla5.setSpacingBefore(10);
	        pClausla5.setSpacingAfter(0);
	        pClausla5.setAlignment(Element.ALIGN_CENTER);
	        doc.add(pClausla5);
	        
	        Paragraph pClausla5_1 = new Paragraph();
	        pClausla5_1.add(new Phrase("5.1   ",f12b));
	        pClausla5_1.add(new Phrase("Para tornar viável a prestação do " ,f12));
	        pClausla5_1.add(new Phrase("Serviço de Telecomunicações, a PRESTADORA ",f12b));
	        pClausla5_1.add(new Phrase("poderá ceder a título de ",f12));
	        pClausla5_1.add(new Phrase("COMODATO ",f12b));
	        pClausla5_1.add(new Phrase("os direitos de uso e gozo dos equipamentos descritos abaixo, caso o assinante aceite, devendo estes serem utilizados única e exclusivamente para a execução dos serviços ora contratados no ",f12));
	        pClausla5_1.add(new Phrase("Contrato de Prestação de Serviços de Telecomunicações  ",f12b));
	        pClausla5_1.add(new Phrase("e, serão instalados no endereço acima informado pelo ",f12));
	        pClausla5_1.add(new Phrase("ASSINANTE.",f12b));
	        pClausla5_1.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausla5_1);
	        
	        PdfPCell pCellVazioSemBorda = new PdfPCell(new Paragraph(" "));
	        pCellVazioSemBorda.setBorder(0);        
	        
	        PdfPCell pCellEquipRegime = new PdfPCell(new Phrase("Regime",f12));	        
	        pCellEquipRegime.setPaddingBottom(4);
	        pCellEquipRegime.setPaddingTop(0);
	        
	        PdfPCell pCellEquipRegimeX = new PdfPCell(new Phrase("X",f12b));	        
	        pCellEquipRegimeX.setPaddingBottom(4);
	        pCellEquipRegimeX.setPaddingTop(0);
	        
	        PdfPCell pCellEquipComodato = new PdfPCell(new Phrase("COMODATO",f12b));	        
	        pCellEquipComodato.setPaddingBottom(4);
	        pCellEquipComodato.setPaddingTop(0);
	        
	        PdfPCell pCellEquipComodatoValor = new PdfPCell(new Phrase("Um Equipamento - cedido em comodato sujeito ao Contrato de Comodato",f12));	        
	        pCellEquipComodatoValor.setPaddingBottom(4);
	        pCellEquipComodatoValor.setPaddingTop(0);

	        PdfPCell pCellEquipComodatoTotal = new PdfPCell(new Phrase("COMODATO (TOTAL)",f12b));	        
	        pCellEquipComodatoTotal.setPaddingBottom(4);
	        pCellEquipComodatoTotal.setPaddingTop(0);
	        
	        PdfPCell pCellEquipComodatoTotalValor = new PdfPCell(new Phrase("Dois Equipamentos - cedidos em comodato sujeito ao Contrato de Comodato",f12));	        
	        pCellEquipComodatoTotalValor.setPaddingBottom(4);
	        pCellEquipComodatoTotalValor.setPaddingTop(0);
	        
	        PdfPCell pCellEquipProprio = new PdfPCell(new Phrase("PROPRIO",f12b));	        
	        pCellEquipProprio.setPaddingBottom(4);
	        pCellEquipProprio.setPaddingTop(0);
	        
	        PdfPCell pCellEquipProprioValor = new PdfPCell(new Phrase("Um Equipamento - utilizados de propriedade do Assinante",f12));	        
	        pCellEquipProprioValor.setPaddingBottom(4);
	        pCellEquipProprioValor.setPaddingTop(0);
	        
	        PdfPCell pCellEquipProprioParcial = new PdfPCell(new Phrase("PROPRIO (PARCIAL)",f12b));	        
	        pCellEquipProprioParcial.setPaddingBottom(4);
	        pCellEquipProprioParcial.setPaddingTop(0);
	        
	        PdfPCell pCellEquipDescProprioParcial = new PdfPCell(new Phrase("Um Equipamento cedido em comodato sujeito ao Contrato de Comodato juntamente com equipamento de propriedade do Assinante.",f12));	        
	        pCellEquipDescProprioParcial.setPaddingBottom(4);
	        pCellEquipDescProprioParcial.setPaddingTop(0);
	        
	        PdfPTable tbEquipamentoRegime1 = new PdfPTable(new float[] {0.10f,0.30f,1f});
	        tbEquipamentoRegime1.setWidthPercentage(100f);
	        tbEquipamentoRegime1.addCell(pCellVazio);
	        tbEquipamentoRegime1.addCell(pCellEquipRegime);
	        tbEquipamentoRegime1.addCell(pCellVazioSemBorda);	  
	        tbEquipamentoRegime1.setSpacingBefore(20);
	        doc.add(tbEquipamentoRegime1);
	        
	        PdfPTable tbEquipamentoRegime2 = new PdfPTable(new float[] {0.10f,0.30f,1f});
	        tbEquipamentoRegime2.setWidthPercentage(100f);
	        tbEquipamentoRegime2.addCell(pCellVazio);
	        tbEquipamentoRegime2.addCell(pCellEquipComodato);
	        tbEquipamentoRegime2.addCell(pCellEquipComodatoValor);	        
	        doc.add(tbEquipamentoRegime2);
	        
	        PdfPTable tbEquipamentoRegime5 = new PdfPTable(new float[] {0.10f,0.30f,1f});
	        tbEquipamentoRegime5.setWidthPercentage(100f);
	        tbEquipamentoRegime5.addCell(pCellVazio);
	        tbEquipamentoRegime5.addCell(pCellEquipComodatoTotal);
	        tbEquipamentoRegime5.addCell(pCellEquipComodatoTotalValor);	        
	        doc.add(tbEquipamentoRegime5);
	        
	        PdfPTable tbEquipamentoRegime3 = new PdfPTable(new float[] {0.10f, 0.30f,1f});
	        tbEquipamentoRegime3.setWidthPercentage(100f);
	        tbEquipamentoRegime3.addCell(pCellVazio);
	        tbEquipamentoRegime3.addCell(pCellEquipProprio);
	        tbEquipamentoRegime3.addCell(pCellEquipProprioValor);	        
	        doc.add(tbEquipamentoRegime3);
	        
	        PdfPTable tbEquipamentoRegime4 = new PdfPTable(new float[] {0.10f, 0.30f,1f});
	        tbEquipamentoRegime4.setWidthPercentage(100f);
	        tbEquipamentoRegime4.addCell(pCellVazio);
	        tbEquipamentoRegime4.addCell(pCellEquipProprioParcial);
	        tbEquipamentoRegime4.addCell(pCellEquipDescProprioParcial);	        
	        doc.add(tbEquipamentoRegime4);
	        
	        Paragraph pSim = new Paragraph();
	        pSim.add(new Phrase("SIM",f12b));
	        pSim.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellSim = new PdfPCell();
	        pCellSim.addElement(pSim);
	        pCellSim.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellSim.setPaddingBottom(4);
	        pCellSim.setPaddingTop(0);
	        
	        Paragraph pNao = new Paragraph();
	        pNao.add(new Phrase("NÃO",f12b));
	        pNao.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellNao = new PdfPCell();
	        pCellNao.addElement(pNao);
	        pCellNao.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellNao.setPaddingBottom(4);
	        pCellNao.setPaddingTop(0);
	        
	        //Paragraph pEquipComo = new Paragraph();
	        //pEquipComo.add(new Phrase("EQUIPAMENTO EM COMODATO",f12b));
	       // pEquipComo.setAlignment(Element.ALIGN_LEFT);
	        
	       // PdfPCell pCellEquipComo = new PdfPCell();
	      //  pCellEquipComo.addElement(pEquipComo);
	       // pCellEquipComo.setBackgroundColor(new BaseColor(232, 235, 237));
	       // pCellEquipComo.setPaddingBottom(4);
	        //pCellEquipComo.setPaddingTop(0);
	        
	        String checkComodato =  acessoCliente.getContrato().getRegime().equals("COMODATO") ? "(     )" : "(     )";
	        String checkProprio=  !acessoCliente.getContrato().getRegime().equals("COMODATO") ? "(     )" : "(     )";
	        
	        Paragraph pAssinalarComodato = new Paragraph();
	        pAssinalarComodato.add(new Phrase(checkComodato,f12b));
	        pAssinalarComodato.setAlignment(Element.ALIGN_LEFT);
	        
	        Paragraph pAssinalarProprio = new Paragraph();
	        pAssinalarProprio.add(new Phrase(checkProprio,f12b));
	        pAssinalarProprio.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellAssinalarComodato = new PdfPCell();
	        pCellAssinalarComodato.addElement(pAssinalarComodato);	       
	        pCellAssinalarComodato.setPaddingBottom(4);
	        pCellAssinalarComodato.setPaddingTop(0);
	        
	        PdfPCell pCellAssinalarProprio = new PdfPCell();
	        pCellAssinalarProprio.addElement(pAssinalarProprio);	       
	        pCellAssinalarProprio.setPaddingBottom(4);
	        pCellAssinalarProprio.setPaddingTop(0);
	        
	        PdfPCell pCellVaziosBorda= new PdfPCell(new Paragraph(" "));		
	        pCellVaziosBorda.setBorder(0); 
	        
	        PdfPCell pCellAssinatura = new PdfPCell();
	        pCellAssinatura.addElement(new Phrase("ASSINATURA: _________________________________________________",f12b));	       
	        pCellAssinatura.setPaddingBottom(4);
	        pCellAssinatura.setPaddingTop(0);
	        pCellAssinatura.setBorder(0);
	        
	        PdfPTable tbAssinatura = new PdfPTable(new float[] {0.60f, 1f});
	        tbAssinatura.setWidthPercentage(100f);
	        tbAssinatura.addCell(pCellVaziosBorda);
	        tbAssinatura.addCell(pCellAssinatura);
	        tbAssinatura.setSpacingBefore(20);
	        doc.add(tbAssinatura);
	                
	        Paragraph pClausla5_2 = new Paragraph();
	        pClausla5_2.add(new Phrase("5.2   ",f12b));
	        pClausla5_2.add(new Phrase("Os equipamentos cedidos em " ,f12));
	        pClausla5_2.add(new Phrase("COMODATO ",f12b));
	        pClausla5_2.add(new Phrase("ou ",f12));	      
	        pClausla5_2.add(new Phrase("COMODATO (TOTAL) ",f12b));
	        pClausla5_2.add(new Phrase("são os seguintes: ",f12));	      

	        pClausla5_2.setSpacingBefore(4);
	        pClausla5_2.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausla5_2);
	        
	        Paragraph pEquipTransm = new Paragraph();
	        pEquipTransm.add(new Phrase("EQUIPAMENTO",f12b));
	        pEquipTransm.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellEquipTransm = new PdfPCell();
	        pCellEquipTransm.addElement(pEquipTransm);
	        pCellEquipTransm.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellEquipTransm.setPaddingBottom(4);
	        pCellEquipTransm.setPaddingTop(0);
	        
	        PdfPTable tbEquipamentoTransmi = new PdfPTable(new float[] {0.03f,0.50f});
	        tbEquipamentoTransmi.setWidthPercentage(100f);
	        tbEquipamentoTransmi.addCell(pCellVazio);	   
	        tbEquipamentoTransmi.addCell(pCellEquipTransm);	        
	        tbEquipamentoTransmi.setSpacingBefore(10);
	        doc.add(tbEquipamentoTransmi);
	        
	        Paragraph pEquip1 = new Paragraph();
	        pEquip1.add(new Phrase("Antena de Transmissão e Recepção de Radiofrequência:",f12b));
	        pEquip1.setAlignment(Element.ALIGN_LEFT);
	        
	        Paragraph pEquip2 = new Paragraph();
	        pEquip2.add(new Phrase("Receptor para rede Óptica (ONU)",f12b));
	        pEquip2.setAlignment(Element.ALIGN_LEFT);
	        
	        Paragraph pEquip3 = new Paragraph();
	        pEquip3.add(new Phrase("Roteador para rede Ethernet e/ou Wirelless",f12b));
	        pEquip3.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellEquip1 = new PdfPCell();
	        pCellEquip1.addElement(pEquip1);	        
	        pCellEquip1.setPaddingBottom(4);
	        pCellEquip1.setPaddingTop(0);
	        
	        PdfPCell pCellEquip2 = new PdfPCell();
	        pCellEquip2.addElement(pEquip2);	        
	        pCellEquip2.setPaddingBottom(4);
	        pCellEquip2.setPaddingTop(0);
	        
	        PdfPCell pCellEquip1Selected = new PdfPCell();
	        pCellEquip1Selected.addElement(new Phrase("    "));	        
	        pCellEquip1Selected.setPaddingBottom(4);
	        pCellEquip1Selected.setPaddingTop(0);
	        
	        PdfPCell pCellEquip2Selected = new PdfPCell();
	        pCellEquip2Selected.addElement(new Phrase("    "));	        
	        pCellEquip2Selected.setPaddingBottom(4);
	        pCellEquip2Selected.setPaddingTop(0);
	        
	        PdfPCell pCellEquip3 = new PdfPCell();
	        pCellEquip3.addElement(pEquip3);	        
	        pCellEquip3.setPaddingBottom(4);
	        pCellEquip3.setPaddingTop(0);
	        	        
	        Paragraph pEquipNSerie = new Paragraph();
	        pEquipNSerie.add(new Phrase("Número de série:",f12b));
	        pEquipNSerie.setAlignment(Element.ALIGN_LEFT);
	        	        
	        PdfPTable tbEquipamento1 = new PdfPTable(new float[] {0.03f,0.50f});
	        tbEquipamento1.setWidthPercentage(100f);
	        
	        if(acessoCliente.getContrato().getRegime().equals("COMODATO")){
	        	tbEquipamento1.addCell(pCellEquip1Selected);
	        	tbEquipamento1.addCell(pCellEquip1);
	        }else{	        	
	        	tbEquipamento1.addCell(pCellVazio);
	        	tbEquipamento1.addCell(pCellEquip1);
	        }
	        
	        PdfPTable tbEquipamento2 = new PdfPTable(new float[] {0.03f,0.50f});
	        tbEquipamento2.setWidthPercentage(100f);
	        
	        if(acessoCliente.getContrato().getRegime().equals("PROPRIO (PARCIAL)") || acessoCliente.getContrato().getRegime().equals("COMODATO (TOTAL)")){	        	
	        	tbEquipamento2.addCell(pCellEquip2Selected);
	        	tbEquipamento2.addCell(pCellEquip2);
	        }else{
	        	tbEquipamento2.addCell(pCellVazio);
	        	tbEquipamento2.addCell(pCellEquip2);
	        }
	        
	        
	        PdfPTable tbEquipamento3 = new PdfPTable(new float[] {0.03f,0.50f});
	        tbEquipamento3.setWidthPercentage(100f);
	        
	        if(acessoCliente.getContrato().getRegime().equals("COMODATO (TOTAL)")){
		        tbEquipamento3.addCell(pCellEquip2Selected);
		        tbEquipamento3.addCell(pCellEquip3);
	        }else{
	        	tbEquipamento3.addCell(pCellVazio);
		        tbEquipamento3.addCell(pCellEquip3);
	        }
	   
	        doc.add(tbEquipamento1);
	        doc.add(tbEquipamento2);
	        doc.add(tbEquipamento3);
	        
	        PdfPCell pCellEquipNSerie = new PdfPCell();
	        pCellEquipNSerie.addElement(pEquipNSerie);	        
	        pCellEquipNSerie.setPaddingBottom(4);
	        pCellEquipNSerie.setPaddingTop(0);
	        
	        
	     
	        
	        
	        Paragraph pEquipDiversos = new Paragraph();
	        pEquipDiversos.add(new Phrase("Diversos",f12b));
	        pEquipDiversos.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellEquipDiversos = new PdfPCell();
	        pCellEquipDiversos.addElement(pEquipDiversos);
	        pCellEquipDiversos.setBackgroundColor(new BaseColor(232, 235, 237));
	        pCellEquipDiversos.setPaddingBottom(4);
	        pCellEquipDiversos.setPaddingTop(0);
	        
	        Paragraph pEquipConectores = new Paragraph();
	        pEquipConectores.add(new Phrase("Conector(es):",f12b));
	        pEquipConectores.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellEquipConectores= new PdfPCell();
	        pCellEquipConectores.addElement(pEquipConectores);	       
	        pCellEquipConectores.setPaddingBottom(4);
	        pCellEquipConectores.setPaddingTop(0);
	        
	        Paragraph pEquipCabos = new Paragraph();
	        pEquipCabos.add(new Phrase("Cabo(s):",f12b));
	        pEquipCabos.setAlignment(Element.ALIGN_LEFT);
	        
	        PdfPCell pCellEquipCabos = new PdfPCell();
	        pCellEquipCabos.addElement(pEquipCabos);	    
	        pCellEquipCabos.setPaddingBottom(4);
	        pCellEquipCabos.setPaddingTop(0);
	        
	        PdfPTable tbEquipamentoDiversos = new PdfPTable(new float[] {1f});
	        tbEquipamentoDiversos.setWidthPercentage(100f);
	        tbEquipamentoDiversos.addCell(pCellEquipDiversos);	        
	        tbEquipamentoDiversos.setSpacingBefore(20);
	        doc.add(tbEquipamentoDiversos);
	        
	        PdfPTable tbEquipamentoDiversos2 = new PdfPTable(new float[] {0.39f,0.50f,0.50f,1f});
	        tbEquipamentoDiversos2.setWidthPercentage(100f);
	        tbEquipamentoDiversos2.addCell(pCellEquipConectores);
	        tbEquipamentoDiversos2.addCell(pCellVazio);
	        tbEquipamentoDiversos2.addCell(pCellEquipCabos);
	        tbEquipamentoDiversos2.addCell(pCellVazio);
	        doc.add(tbEquipamentoDiversos2);
	        
	        Paragraph pClausla6 = new Paragraph("CLÁUSULA SEXTA – DOS BENEFÍCIOS E FIDELIDADE CONTRATUAL",f12bUnder);
	        pClausla6.setSpacingBefore(10);
	        pClausla6.setSpacingAfter(0);
	        pClausla6.setAlignment(Element.ALIGN_CENTER);
	        doc.add(pClausla6);
	        
	        Paragraph pClausula6_1 = new Paragraph();
	        pClausula6_1.add(new Phrase("6.1   ",f12b));
	        pClausula6_1.add(new Phrase("A ",f12));
	        pClausula6_1.add(new Phrase("PRESTADORA ",f12b));
	        pClausula6_1.add(new Phrase("irá ceder o uso e gozo dos equipamentos descritos no presente ",f12));
	        pClausula6_1.add(new Phrase("TERMO DE ADESÃO ",f12b));
	        pClausula6_1.add(new Phrase("ao ",f12));
	        pClausula6_1.add(new Phrase("ASSINANTE, ",f12b));
	        pClausula6_1.add(new Phrase("mediante ",f12));
	        pClausula6_1.add(new Phrase("BENEFÍCIO ",f12b));
	        pClausula6_1.add(new Phrase("constante no ",f12));
	        pClausula6_1.add(new Phrase("TERMO DE CONCESSÃO CONDICIONAL DE BENEFÍCIOS. , ",f12b));
	        pClausula6_1.add(new Phrase("Em contrapartida, o ",f12));
	        pClausula6_1.add(new Phrase("ASSINANTE ",f12b));
	        pClausula6_1.add(new Phrase("vincula-se contratualmente ",f12Under));
	        pClausula6_1.add(new Phrase("a ",f12));
	        pClausula6_1.add(new Phrase("PRESTADORA ",f12b));
	        pClausula6_1.add(new Phrase("no prazo previsto no ",f12));
	        pClausula6_1.add(new Phrase("TERMO DE CONCESSÃO CONDICIONAL DE BENEFÍCIOS. ",f12b));	        
	        pClausula6_1.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausula6_1);
	        
	        
	        Paragraph pClausula6_2 = new Paragraph();
	        pClausula6_2.add(new Phrase("6.2   ",f12b));
	        pClausula6_2.add(new Phrase("Sendo do interesse do ",f12));
	        pClausula6_2.add(new Phrase("ASSINANTE, ",f12b));
	        pClausula6_2.add(new Phrase("obter o ",f12));
	        pClausula6_2.add(new Phrase("BENEFÍCIO  ",f12b));
	        pClausula6_2.add(new Phrase("oferecido pela ",f12));
	        pClausula6_2.add(new Phrase("PRESTADORA, ",f12b));
	        pClausula6_2.add(new Phrase("o ",f12));
	        pClausula6_2.add(new Phrase("ASSINANTE ",f12b));
	        pClausula6_2.add(new Phrase("deverá pactuar com a ",f12));
	        pClausula6_2.add(new Phrase("PRESTADORA, ",f12b));
	        pClausula6_2.add(new Phrase("por meio do ",f12));
	        pClausula6_2.add(new Phrase("TERMO DE CONCESSÃO CONDICIONAL DE BENEFÍCIOS, ",f12b));
	        pClausula6_2.add(new Phrase("documento no qual será identificado o ",f12));
	        pClausula6_2.add(new Phrase("BENEFÍCIO ",f12b));
	        pClausula6_2.add(new Phrase("concedido, o prazo de fidelidade contratual e as penalidades aplicáveis em caso de rescisão antecipada do contrato.",f12));
	        pClausula6_2.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausula6_2);
	        
	        Paragraph rodaPePg3 = new Paragraph("TERMO DE ADESÃO - "+empresa.getRazao_social()+" - Página 3 de 4                                       emitido por: "+OpusERP4UI.getUsuarioLogadoUI().getUsername(),f8b);
		    rodaPePg3.setAlignment(Element.ALIGN_RIGHT);

			   PdfPCell pCellRod3 = new PdfPCell();
		 	   pCellRod3.addElement(rodaPePg3);
		 	  
			   pCellRod3.setBorderWidthBottom(0);
			   pCellRod3.setBorderWidthRight(0);
			   pCellRod3.setBorderWidthLeft(0);

			   Rectangle page3 = doc.getPageSize();
			   PdfPTable foot3 = new PdfPTable(new float[] {1f});
			   foot3.setWidthPercentage(100f);
		       foot3.addCell(pCellRod3);
		       foot3.setTotalWidth(page3.getWidth() - doc.leftMargin() - doc.rightMargin());
		       foot3.writeSelectedRows(0, -4, doc.leftMargin(), doc.bottomMargin() + 10,pdfWriter.getDirectContent());
	        
	        
	        
	        Paragraph pClausula6_3 = new Paragraph();
	        pClausula6_3.add(new Phrase("6.3   ",f12b));
	        pClausula6_3.add(new Phrase("O ",f12));
	        pClausula6_3.add(new Phrase("ASSINANTE, ",f12b));
	        pClausula6_3.add(new Phrase("reconhece e declara ter sido a ele facultada a opção de celebrar contrato com a ",f12));
	        pClausula6_3.add(new Phrase("PRESTADORA  ",f12b));
	        pClausula6_3.add(new Phrase("sem a percepção de qualquer ",f12));
	        pClausula6_3.add(new Phrase("BENEFÍCIO, ",f12b));
	        pClausula6_3.add(new Phrase("estando, nesse caso, isento de fidelidade contratual e não assinará o ",f12));
	        pClausula6_3.add(new Phrase("TERMO DE CONCESSÃO CONDICIONAL DE BENEFÍCIOS. ",f12b));
	        pClausula6_3.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausula6_3);
	        
	        Paragraph pClausula6_4 = new Paragraph();
	        pClausula6_4.add(new Phrase("6.4   ",f12b));
	        pClausula6_4.add(new Phrase("Toda e qualquer mudança nas instalações ou configurações estabelecidas ou planos solicitadas pelo ",f12));
	        pClausula6_4.add(new Phrase("ASSINANTE, ",f12b));
	        pClausula6_4.add(new Phrase(" incluindo a posterior mudança no local da prestação do serviço, fica desde já condicionada à existência de disponibilidade e viabilidade técnica no local da instalação do serviço.  ",f12));
	        pClausula6_4.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausula6_4);
	        
	        Paragraph pClausula6_4_Paragrafo_Unico = new Paragraph();
	        pClausula6_4_Paragrafo_Unico.add(new Phrase("Parágrafo Único. ",f12b));
	        pClausula6_4_Paragrafo_Unico.add(new Phrase("Havendo solicitação para local em que a ",f12));
	        pClausula6_4_Paragrafo_Unico.add(new Phrase("PRESTADORA, ",f12b));
	        pClausula6_4_Paragrafo_Unico.add(new Phrase("não possua viabilidade técnica, o ",f12));
	        pClausula6_4_Paragrafo_Unico.add(new Phrase("ASSINANTE ",f12b));
	        pClausula6_4_Paragrafo_Unico.add(new Phrase("declara ciência de que será cobrada multa por rescisão antecipada, nos moldes acordados no Contrato de Permanência.",f12));	        
	        pClausula6_4_Paragrafo_Unico.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausula6_4_Paragrafo_Unico);

	        
	        doc.add(Chunk.NEXTPAGE);
	        
	        Paragraph pClausla7 = new Paragraph("CLÁUSULA SÉTIMA – DAS DISPOSIÇÕES GERAIS",f12bUnder);
	        pClausla7.setSpacingBefore(10);
	        pClausla7.setSpacingAfter(0);
	        pClausla7.setAlignment(Element.ALIGN_CENTER);
	        doc.add(pClausla7);
	        
	        
	        Paragraph pClausula7_1 = new Paragraph();
	        pClausula7_1.add(new Phrase("7.1   ",f12b));
	        pClausula7_1.add(new Phrase("O ",f12));
	        pClausula7_1.add(new Phrase("ASSINANTE, ",f12b));
	        pClausula7_1.add(new Phrase("declara, para todos os fins de direito, que a aceitação aos termos do ",f12));
	        pClausula7_1.add(new Phrase("Contrato de Prestação de Serviços de Telecomunicações, ",f12b));
	        pClausula7_1.add(new Phrase("formalizada por este ",f12));
	        pClausula7_1.add(new Phrase("TERMO DE ADESÃO, ",f12b));
	        pClausula7_1.add(new Phrase("é a expressão de sua vontade. Em face do expresso reconhecimento da legitimidade da presente contratação é que o ",f12));
	        pClausula7_1.add(new Phrase("ASSINANTE ",f12b));
	        pClausula7_1.add(new Phrase("não poderá escusar-se de cumprir as condições ora pactuadas.",f12));
	        pClausula7_1.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausula7_1);
	        
	        
	        
	        
	        Paragraph pClausula7_2 = new Paragraph();
	        pClausula7_2.add(new Phrase("7.2   ",f12b));
	        pClausula7_2.add(new Phrase("A partir da assinatura deste ",f12));
	        pClausula7_2.add(new Phrase("TERMO DE ADESÃO, ",f12b));
	        pClausula7_2.add(new Phrase("ficam as partes obrigadas ao fiel cumprimento das cláusulas contidas no ",f12));
	        pClausula7_2.add(new Phrase("Contrato de Prestação de Serviços de Telecomunicações. ",f12b));
	        pClausula7_2.add(new Phrase("O presente ",f12));
	        pClausula7_2.add(new Phrase("TERMO DE ADESÃO ",f12b));
	        pClausula7_2.add(new Phrase("vigorará enquanto estiver vigente o ",f12));
	        pClausula7_2.add(new Phrase("Contrato de Prestação de Serviços de Telecomunicações. ",f12b));	        
	        pClausula7_2.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausula7_2);
	        
	        Paragraph pClausula7_3 = new Paragraph();
	        pClausula7_3.add(new Phrase("7.3   ",f12b));
	        pClausula7_3.add(new Phrase("O presente ",f12));
	        pClausula7_3.add(new Phrase("TERMO DE ADESÃO ",f12b));
	        pClausula7_3.add(new Phrase("poderá ser modificado no todo ou em parte, por meio de ",f12));
	        pClausula7_3.add(new Phrase("Termo Aditivo.",f12b));
	        pClausula7_3.setAlignment(Element.ALIGN_JUSTIFIED);
	        doc.add(pClausula7_3);
	        
	        //doc.add(Chunk.NEXTPAGE);
	        
	        //----------------------PAGINA 4-------------------------------------//
	        
	        Paragraph pClausla8 = new Paragraph("CLÁUSULA OITAVA – DA SUCESSÃO E FORO",f12bUnder);
	        pClausla8.setSpacingBefore(10);
	        pClausla8.setSpacingAfter(0);
	        pClausla8.setAlignment(Element.ALIGN_CENTER);	        
	        doc.add(pClausla8);
	        
	        
	        Paragraph pClausula8_1 = new Paragraph();
	        pClausula8_1.add(new Phrase("8.1   ",f12b));
	        pClausula8_1.add(new Phrase("O presente instrumento obriga herdeiros e/ou sucessores, a qualquer tempo, sendo neste ato eleito pelas partes o foro da Comarca da Cidade de ",f12));
	        pClausula8_1.add(new Phrase("Belo Jardim, ",f12b));
	        pClausula8_1.add(new Phrase("estado do ",f12));
	        pClausula8_1.add(new Phrase("Pernambuco, ",f12b));
	        pClausula8_1.add(new Phrase("competente para dirimir quaisquer questões referentes ao presente, com renúncia expressa de qualquer outro, por mais privilegiado que seja.",f12));
	        pClausula8_1.setAlignment(Element.ALIGN_JUSTIFIED);	  
	        doc.add(pClausula8_1);
	        
	        //ASSINANTE
	        Paragraph pClausulaAssinante = new Paragraph();
	        pClausulaAssinante.setSpacingAfter(5);
	        pClausulaAssinante.add(new Phrase("E, por estar de acordo, o",f12));
	        pClausulaAssinante.add(new Phrase(" ASSINANTE ",f12b));
	        pClausulaAssinante.add(new Phrase("adere ao presente documento assinando em 02 (duas) vias de igual teor por sua livre vontade, declarando ainda, não estar assinando e/ou aceitando"+
	        " o presente sob premente coação, estado de necessidade ou outra forma de vício de consentimento, tendo conhecimento de todo direito e obrigação que assume nesta data.",f12));	    
	        doc.add(pClausulaAssinante);
	        
	        //BELO JARDIM
	        Paragraph pBelo = new Paragraph("BELO JARDIM/PE, "+date,f12b);
	        pBelo.setAlignment(Element.ALIGN_RIGHT);
	        pBelo.setSpacingAfter(30);
	        doc.add(pBelo);
	        
	        
	        //COLUNA 1
	        PdfPCell pCellRepresentante = new PdfPCell();
	        pCellRepresentante.setBorderWidth(0);
	        pCellRepresentante.addElement(new Paragraph("______________________________________",f12b));
	        pCellRepresentante.addElement(new Paragraph("NOME: "+cliente.getNome_razao(),f12b));        
	        pCellRepresentante.addElement(new Paragraph("REPRESENTANTE LEGAL",f12));
	        pCellRepresentante.addElement(new Paragraph("CPF/CNPJ: "+cliente.getDoc_cpf_cnpj(),f12));
	       	        
	        //COLUNA 2
	        PdfPCell pCellAvalista = new PdfPCell();
	        pCellAvalista.setBorderWidth(0);
	        pCellAvalista.addElement(new Paragraph("______________________________________",f12b));
	        
	        String fiadorNome = " ";
	        String fiadorDoc = " ";
	        if(acessoCliente.getContrato().getFiador()!=null){
	        	fiadorNome = acessoCliente.getContrato().getFiador().getNome_razao();
	        	fiadorDoc = acessoCliente.getContrato().getFiador().getDoc_cpf_cnpj();
	        }
	        pCellAvalista.addElement(new Paragraph("NOME: "+fiadorNome,f12b));        
	        pCellAvalista.addElement(new Paragraph("AVALISTA",f12));
	        pCellAvalista.addElement(new Paragraph("CPF/CNPJ: "+fiadorDoc,f12));
	       	        
	        PdfPTable tbAss = new PdfPTable( new float[]{2.50f,0.10f,2.50f});   
	        tbAss.setWidthPercentage(100f);		
	        tbAss.addCell(pCellRepresentante);
	        tbAss.addCell(pCellVazio2);
	        tbAss.addCell(pCellAvalista);
	        doc.add(tbAss);
	        
	      

	     

	        Paragraph rodaPePg4 = new Paragraph("TERMO DE ADESÃO - "+empresa.getRazao_social()+" - Página 4 de 4                                        emitido por: "+OpusERP4UI.getUsuarioLogadoUI().getUsername(),f8b);
		       rodaPePg4.setAlignment(Element.ALIGN_RIGHT);

			   PdfPCell pCellRod4 = new PdfPCell();
		 	   pCellRod4.addElement(rodaPePg4);
		 	  
			   pCellRod4.setBorderWidthBottom(0);
			   pCellRod4.setBorderWidthRight(0);
			   pCellRod4.setBorderWidthLeft(0);

			   Rectangle page4 = doc.getPageSize();
			   PdfPTable foot4 = new PdfPTable(new float[] {1f});
			   foot4.setWidthPercentage(100f);
		       foot4.addCell(pCellRod4);
		       foot4.setTotalWidth(page3.getWidth() - doc.leftMargin() - doc.rightMargin());
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
