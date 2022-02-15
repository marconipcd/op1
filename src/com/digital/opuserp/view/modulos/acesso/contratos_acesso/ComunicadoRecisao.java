package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.WordUtils;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.ParametrosBoleto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class ComunicadoRecisao implements StreamSource{

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ComunicadoRecisao(Integer codAc) throws Exception {
		
		EntityManager em = ConnUtil.getEntity();
		
		Document doc = new Document(PageSize.A4, 2f, 2f, 1f, 1f);	
		doc.setMargins(24, 24, 12, 12);
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
			
			Query qPb = em.createQuery("select pb from ParametrosBoleto pb where pb.cliente_id = :codCliente", ParametrosBoleto.class);
			qPb.setParameter("codCliente", acessoCliente.getCliente().getId());			
			ParametrosBoleto pb = null;
			boolean cobrarTaxa = true;
			if(qPb.getResultList().size() > 0){
				pb = (ParametrosBoleto) qPb.getSingleResult();
				
				if(!pb.getCobrar_taxa_bancaria()){
					cobrarTaxa = false;
				}
			}

			Endereco end = acessoCliente.getEndereco();
			
				
			byte[] logo = empresa.getLogo_empresa();		
			
			if(logo != null){
				Image imgLogo = Image.getInstance(logo);				
				imgLogo.setAlignment(Element.ALIGN_CENTER);
				imgLogo.scalePercent(65.0f);
								
				doc.add(imgLogo);			
			}
			
			URL urlFont  = getClass().getResource("/com/digital/opuserp/font/Arial Narrow.ttf");
			
			BaseFont base8 = BaseFont.createFont(urlFont.toString(), BaseFont.WINANSI,true);
			Font f8 = new Font(base8, 6f);			

			Paragraph p = new Paragraph(OpusERP4UI.getEmpresa().getEndereco()+", "+OpusERP4UI.getEmpresa().getNumero()+" "+OpusERP4UI.getEmpresa().getBairro()+" / "+OpusERP4UI.getEmpresa().getCidade()+"-"+OpusERP4UI.getEmpresa().getUf(),f8);
			p.setAlignment(Element.ALIGN_CENTER);
			doc.add(p);
			
			Font f8b = new Font(base8, 8f, Font.BOLD);			
			Font f12 = new Font(base8, 12f);			
			Font f12b = new Font(base8, 12f, Font.BOLD);			
			Font f12bUnder = new Font(base8, 12f, Font.BOLD | Font.UNDERLINE);

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

			
			 Paragraph pTerm = new Paragraph("COMUNICADO DE RESCISÃO CONTRATUAL",f12b);
			 pTerm.setSpacingAfter(60);
			 pTerm.setSpacingBefore(30);
			 pTerm.setAlignment(Element.ALIGN_CENTER);
			 
			 doc.add(pTerm);
			 
			 Paragraph p2 = new Paragraph("Prezado(a) "+acessoCliente.getCliente().getNome_razao(),f12);
			 p2.setSpacingAfter(60);
			 p2.setAlignment(Element.ALIGN_LEFT);
			 
			 doc.add(p2);
			 
			 
			 Paragraph p3 = new Paragraph("                 Venho pelo presente comunicar que o Contrato de Prestação de Serviço de Comunicação Multimídia nº. "+codAc.toString()+", firmado em "+DataUtil.formatDateBra(acessoCliente.getData_cadastro())+" encontra-se rescindido junto à esta empresa por motivos de continuada inadimplência.",f12);
			 p3.setSpacingAfter(30);
			 p3.setAlignment(Element.ALIGN_JUSTIFIED);
			 
		  	 doc.add(p3);
		  	 
		  	 Paragraph p4 = new Paragraph("                  Assim, comunicamos formalmente o cancelamento a fim de veicular que serão tomadas s seguintes providências:",f12);
			 p4.setSpacingAfter(30);
			 p4.setAlignment(Element.ALIGN_JUSTIFIED);
			 
		  	 doc.add(p4);
		  	 
		  	Paragraph p5 = new Paragraph("                            •   Estará sujeito à inscrição da dívida nos órgãos de proteção ao crédito;",f12);
			 p5.setSpacingAfter(5);
			 p5.setAlignment(Element.ALIGN_LEFT);
			 
		  	 doc.add(p5);
		  	 
		  	Paragraph p6 = new Paragraph("                            •	   Cobrança das mensalidades em atraso acrescidas de multa e juros de acordo com o estabelecido em                                       Contrato;",f12);
			 p6.setSpacingAfter(5);
			 p6.setAlignment(Element.ALIGN_LEFT);
			 
		  	 doc.add(p6);
		  	 
		  	Paragraph p7 = new Paragraph("                            • 	  Caso persista a inadimplência, sujeito à cobrança forçada dos valores judicialmente.",f12);
			 p7.setSpacingAfter(30);
			 p7.setAlignment(Element.ALIGN_LEFT);
			 
		  	 doc.add(p7);
		  	 
		  	
		  	Paragraph p8 = new Paragraph("                  Dessa forma, a fim de evitar-se tal situação, reiteramos o pedido no sentido de seu comparecimento em nosso escritório ou contato no Tel. (81) 3726-3125 ou 0800 081 3125, no intuito de solucionarmos esta pendência.",f12);
			 p8.setSpacingAfter(30);
			 p8.setAlignment(Element.ALIGN_JUSTIFIED);
			 
		  	 doc.add(p8);
		  	 
		  	Paragraph p9 = new Paragraph("                    Solicitamos desconsiderar a presente notificação caso o referido débito já tenha sido quitado.",f12);
			 p9.setSpacingAfter(60);
			 p9.setAlignment(Element.ALIGN_JUSTIFIED);
			 
		  	 doc.add(p9);
		  			  	 
		  	String cidade = OpusERP4UI.getEmpresa().getCidade();
		  	String uf= OpusERP4UI.getEmpresa().getUf();
		  	 
		  	Calendar cal = Calendar.getInstance();
		  	String mes = new SimpleDateFormat("MMMM").format(cal.getTime());
		  	String dia = new SimpleDateFormat("d").format(cal.getTime());
		  	String ano = new SimpleDateFormat("y").format(cal.getTime());
		  	
		  	Paragraph p10 = new Paragraph(WordUtils.capitalize(cidade.toLowerCase()) +" - "+uf+", "+dia+" de "+mes+" de "+ano+".",f12);
			 p10.setSpacingAfter(60);
			 p10.setAlignment(Element.ALIGN_CENTER);
			 
		  	 doc.add(p10);
		  	 
		  	 
		  	Paragraph p11 = new Paragraph(OpusERP4UI.getEmpresa().getRazao_social(),f12);
		  	p11.setSpacingAfter(30);
		  	p11.setAlignment(Element.ALIGN_RIGHT);
			
		  	doc.add(p11);
		  	
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
