package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;

import br.com.caelum.stella.boleto.bancos.GeradorDeLinhaDigitavel;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContratosAcessoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.util.CheckNdocUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.boletos.boleto.Boleto;
import com.digital.opuserp.util.boletos.boleto.bancos.BancoDoBrasil;
import com.digital.opuserp.util.boletos.boleto.bancos.Sicredi;
import com.digital.opuserp.util.boletos.transform.GeradorDeImagemDoCodigoDeBarrasUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BarcodeInter25;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinService;

public class ImprimirBoletoBeta implements StreamSource{

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	PdfWriter writer = null;
	
	public ImprimirBoletoBeta(List<Boleto> boletos) throws Exception {
		
		EntityManager em = ConnUtil.getEntity();
		
		Document doc = new Document(PageSize.A4, 24, 24, 14, 14);	
		doc.setMargins(12, 12, 12, 12);
		try{
		
			writer = PdfWriter.getInstance(doc, baos);
			writer.setStrictImageSequence(true);
			doc.open();
			
			Integer i = 1;
			for (Boleto boleto : boletos) {
				
					if(boleto.getBanco().getClass() == BancoDoBrasil.class){
							
						StringBuilder str = new StringBuilder();
						
						str.append("<style type='text/css'>");
						str.append(".table1 {");
						str.append("	width: 800px;");
						str.append("	border-bottom-width: 1px;");
						str.append("	border-bottom-style: dashed;");
						str.append("	border-bottom-color: #000;");
						str.append("}");
						
						str.append(".cabecalho {");
						str.append("	font-family: Arial, Helvetica, sans-serif;");
						str.append("	font-size: 14px;");
						str.append("	font-weight: bold;");
						str.append("	text-align: right;");
						str.append("	border-bottom-width: 1px;");
						str.append("	border-bottom-style: solid;");
						str.append("	border-bottom-color: #000;");
						str.append("	vertical-align: bottom;");
						str.append("}");
						
						str.append(".linhas_formatacao {");
						str.append("	font-family: Courier;");
						str.append("	font-size: 8px;");
						str.append("	color: #000;");
						str.append("	border-bottom-width: 1px;");
						str.append("	border-bottom-style: solid;");
						str.append("	border-bottom-color: #000;");
						str.append("	text-align: left;");
						str.append("	vertical-align: bottom;");
						str.append("	border-left-color: #000;");
						str.append("	padding-left: 0px;");
						str.append("}");
						
						str.append(".border-bottom{");
						str.append("	border-bottom-width: 1px;");
						str.append("	border-bottom-style: solid;");
						str.append("	border-bottom-color: #000;");	
						str.append("	font-family: Courier;");
						str.append("	font-size: 8px;");
						str.append("	color: #000;");
						str.append("}");
						
						
						str.append(".border-bottom-dotted{");
						str.append("	border-bottom-width: 1px;");
						str.append("	border-bottom-style: dotted;");
						str.append("	border-bottom-color: #000;");	
						str.append("	font-family: Courier;");
						str.append("	font-size: 8px;");
						str.append("	color: #000;");
						str.append("}");
						
						str.append(".border-bottom-fundo{");
						str.append("	border-bottom-width: 1px;");
						str.append("	border-bottom-style: dotted;");
						str.append("	border-bottom-color: #000;");	
						str.append("	font-family: Courier;");
						str.append("	font-size: 8px;");
						str.append("	color: #000;");
						str.append("	background-color: #CCC;");
						str.append("}");
						
						str.append(".formatacao_valores");
						str.append("{");
						str.append("	font-family: Courier;");
						str.append("	font-size: 8px;");
						str.append("	color: #000;");
						str.append("	padding: 0;");
						str.append("margin: 0;");
						str.append("}");
						
						str.append(".titulo_linhas {");
						str.append("	font-family: Arial, Helvetica, sans-serif;");
						str.append("	font-size: 8px;");
						str.append("	color: #000;");
						str.append("	padding-bottom: 0;");
						str.append("	margin-top: 5px;");
						str.append("	text-align: center;");
						str.append("}");
						
						str.append(".div_vertical {");
						str.append("	background-image: url(imagens/dic_boleto.gif);");
						str.append("}");
						
						str.append(".bg_div {");
						str.append("	background-image: url(imagens/bg_div.gif);");
						str.append("	height: 4px;");
						str.append("}");
						
						str.append(".linhaDigitavel {");
						str.append("	border-bottom-width: 1px;");
						str.append("	border-bottom-style: solid;");
						str.append("	border-bottom-color: #000;");	
						str.append("	font-family: Arial, Helvetica, sans-serif;");
						str.append("	font-size: 14px;");
						str.append("	text-align: right;");
						str.append("	color: #000;");
						str.append("	padding-right: 10px;");
						str.append("}");
						
						
						str.append(".cabecalho_logo{");
						str.append("	vertical-align:top;");
						str.append("	padding-bottom:10px;");
						str.append("	border-bottom-width: 1px;");
						str.append("	border-bottom-style: solid;");
						str.append("	border-bottom-color: #000;");	
						str.append("}");
						
						
						str.append(".aut_mec{");
						str.append("	font-size:8px;");
						str.append("	text-align:right;");
						str.append("	padding-top: 10px;");
						str.append("}");
						
						
						str.append(".table_space{");
						str.append("	margin-top:35px; ");
						str.append("}");
						
						
						str.append("</style>");
				
						if(i % 2 == 0){ 
													
							str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1 border-bottom table_space' >");
						}else{	
							if(i != 1){
								doc.newPage();
							}
							
							
							str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1 border-bottom'>");
						}
						
						i++;
						
						//Linha1, Logotipo, 001-9, Recibo Sacado e Recibo Entrega
						
						String base = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
						//GeradorDeLinhaDigitavel linhaDigitavelGenerator = new GeradorDeLinhaDigitavel();
						//String codigoDeBarras = boleto.getBanco().geraCodigoDeBarrasPara(boleto);
						
						String logo_url = base + "/WEB-INF/boleto/bb_logo.png";
						String logo_digital_bb = base + "/WEB-INF/boleto/logo_digital_bb.png";
										
					
						str.append("	<tr>");
						str.append("		<td>");
						str.append("			<table width='94%' border='0' cellpadding='0' cellspacing='0'>");
						str.append("				<tr>");
						str.append("					<td width='80' class='cabecalho_logo' height='40'>");
						str.append("						<img src='"+logo_url+"' width='155' height='20' />");
						str.append("					</td>");
						
						str.append("					<td width='70' class='cabecalho' >");
						str.append("						|001-9|");
						str.append("					</td>");
						
						AcessoCliente contrato = null;
						if(CheckNdocUtil.checkNdocAcesso(boleto.getNumeroDoDocumento())){
							String[] codigos = boleto.getNumeroDoDocumento().split("/");
							
							
							if(codigos[0] != null){
								try{
									contrato = ContratosAcessoDAO.find(Integer.parseInt(codigos[0].toString()));
								}catch(Exception e){
									
								}
							}
						}
						
						boolean imprimir_cod_barras = false;
						
						ControleTitulo controle = null;
						Query q = em.createQuery("select c from ControleTitulo c where c.nome =:nome and c.empresa_id =:empresa", ControleTitulo.class); 
						q.setParameter("nome", boleto.getControle());
						q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
						if(q.getResultList().size() == 1){
							controle = (ControleTitulo)q.getSingleResult();
							
							if(controle != null && controle.getRegistro() != null && controle.getRegistro().equals("SIM")){
								imprimir_cod_barras = true;
								
								if(contrato != null && contrato.getEmitir_nfe_automatico() != null &&  contrato.getEmitir_nfe_automatico().equals("SIM") && contrato.getEmitir_nfe_c_boleto_aberto() != null && contrato.getEmitir_nfe_c_boleto_aberto().equals("SIM")){
									imprimir_cod_barras = true;
								}else{
									
									if(contrato != null){
										imprimir_cod_barras = false;
									}else{
										imprimir_cod_barras = true;
									}
								}
							}
						}
									
						str.append("					<td width='220' class='cabecalho' >");
						str.append("						<span class='titulo_linhas'>Recibo de Pagador</span><br />");
						
						if(imprimir_cod_barras){ //VOLTAR AQUI
					//		str.append(	"						<span style='font-size:10px;'>"+ linhaDigitavelGenerator.geraLinhaDigitavelPara(codigoDeBarras) +"</span>");
						}
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='140' class='cabecalho' >");
						str.append("						Recibo Entrega");
						str.append("					</td>");
						str.append("				</tr>");
						str.append("			</table>");
						str.append("		</td>");
						str.append("	</tr>");
						str.append("</table>");
						
						
						str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1' class='border-bottom'>");
						str.append("	<tr>");
						str.append("		<td>");
						str.append("			<table width='97%' border='0' cellpadding='0' cellspacing='0'>");
						str.append("				<tr>");
					

						str.append("					<td class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Nome do Pagador/CPF/CNPJ/Endereço</span><br />");			
						str.append(							boleto.getSacado().getNome()+" "+boleto.getSacado().getCpf()+" <br/> "+boleto.getSacado().getEndereco()+" "+boleto.getSacado().getBairro() );
						str.append("						<br/><span class='titulo_linhas'>Sacador/Avalista</span>");			
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='60' class='border-bottom' height='20' >");
						str.append("						<span class='titulo_linhas'>Vencimento</span><br />");			
						str.append(							formatDate(boleto.getDatas().getVencimento()));
						str.append("					</td>");
						
						str.append("					<td width='2%' class='border-bottom' height='20'>");
						str.append("						<img src='imagens/div_txt.gif' width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='60' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>(=) Valor Documento</span><br />");			
						str.append(							Real.formatDbToString(boleto.getValorBoleto().toString()));
						str.append("					</td>");
						
						str.append("					<td width='2%' class='border-bottom' height='20'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='60' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Nº Documento</span><br />");	
						str.append(							boleto.getNumeroDoDocumento());
						str.append("					</td>");
						
						
						str.append("				</tr>");
						str.append("			</table>");
						str.append("		</td>");
						str.append("	</tr>");
						str.append("</table>");
						
						
						str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1' class='border-bottom'>");
						str.append("	<tr>");
						str.append("		<td>");
						str.append("			<table width='97%' border='0' cellpadding='0' cellspacing='0'>");
						str.append("				<tr>");
					

						str.append("					<td width='100' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Nosso Número</span><br />");
						str.append(							boleto.getBanco().getNossoNumeroDoEmissorFormatado(boleto.getEmissor()));
						str.append("					</td>");
						
						str.append("					<td width='2%' class='border-bottom' height='20'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='85' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Nº Documento</span><br />");	
						str.append(							boleto.getNumeroDoDocumento());
						str.append("					</td>");
						
						str.append("					<td width='2%' class='border-bottom' height='20'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='80' class='border-bottom' height='20' >");
						str.append("						<span class='titulo_linhas'>Data de Vencimento</span><br />");			
						str.append(							formatDate(boleto.getDatas().getVencimento()));
						str.append("					</td>");
						
						str.append("					<td width='2%' class='border-bottom' height='20'>");
						str.append("						<img src='imagens/div_txt.gif' width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='100' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Valor do Documento</span><br />");			
						str.append(							Real.formatDbToString(boleto.getValorBoleto().toString()));
						str.append("					</td>");
						
						str.append("					<td width='2%' class='border-bottom' height='20'>");
						str.append("						<img src='imagens/div_txt.gif' width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='75' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>(=) Valor Pago</span><br />");			
						str.append("_ ");
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						
						str.append("					<td width='2%' class='border-bottom' height='20'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='167' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Pagador</span><br />");			
						str.append(							boleto.getSacado().getNome().length() > 36 ? boleto.getSacado().getNome().substring(0,36) : boleto.getSacado().getNome());
						str.append("					</td>");			
						
						str.append("				</tr>");
						str.append("			</table>");
						str.append("		</td>");
						str.append("	</tr>");
						str.append("</table>");
						
						str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1' class='border-bottom'>");
						str.append("	<tr>");
						str.append("		<td>");
						str.append("			<table width='97%' border='0' cellpadding='0' cellspacing='0'>");
						str.append("				<tr>");
					
						str.append("					<td class='border-bottom-dotted'>");
						str.append("						<span class='titulo_linhas'>Nome do Beneficiário/CPF/CNPJ/Endereço</span><br />");			
						str.append("						"+boleto.getEmissor().getCedente()+" "+boleto.getCnpj()+" "+OpusERP4UI.getEmpresa().getEndereco()+"  "+OpusERP4UI.getEmpresa().getBairro()+" "+OpusERP4UI.getEmpresa().getNumero()+" - "+OpusERP4UI.getEmpresa().getCidade()+ "<br/>");
						str.append("						"+boleto.getAgenciaECodigoCedente());
						str.append("						<br/><span class='titulo_linhas'>Agência/Código do Beneficiário</span>");			
						str.append("					</td>");		
						
						str.append("					<td width='100' class='border-bottom' >");
						str.append("						<span class='titulo_linhas'>Autênticação Mecânica</span><br /><br /><br /><br />");			
						
						str.append("					</td>");		
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						

						str.append("					<td width='130' class='border-bottom' >");
						str.append("						<span class='titulo_linhas'>Assinatura do Recebedor</span><br /><br /><br /><br />");			
						str.append("					</td>");
						
						str.append("					<td width='80' class='border-bottom' >");
						str.append("						<span class='titulo_linhas'>Data de Entrega</span><br /><br />");			
						str.append("						____/____/___");
						str.append("					</td>");
					
						
						str.append("				</tr>");
						str.append("			</table>");
						str.append("		</td>");
						str.append("	</tr>");
						str.append("</table>");
						
						
											
						//Parte Boleto: TOPO LINHA DIGITAVEL
						str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1' class='border-bottom'>");
						str.append("	<tr>");
						str.append("		<td colspan='3'>");
						str.append("			<table width='95%' border='0' cellpadding='0' cellspacing='0'>");
						str.append("				<tr>");
						str.append("					<td class='cabecalho_logo' height='40' width='50'>");
						str.append("						<img src='"+logo_url+"'  width='155' height='20' />");
						str.append("					</td>");
						
						str.append("					<td  class='border-bottom' height='20' width='20'>");
						str.append("						<span class='cabecalho'>|001-9|</span>");
						str.append("					</td>");
			        
						str.append("					<td class='linhaDigitavel' width='100'>");
						
						
						
						if(imprimir_cod_barras){//VOLTAR AQUI
					//		str.append(						linhaDigitavelGenerator.geraLinhaDigitavelPara(codigoDeBarras));
						}
						str.append("					</td>");
						str.append("				</tr>");
						str.append("			</table>");
						str.append("		</td>");
						str.append("	</tr>");
						
						//Linha: Local Pagto., Uso Banco, Vencimentoborder-bottom
						str.append("	<tr>");    
						str.append("		<td colspan='3'>");
						str.append("			<table width='95%' border='0' cellpadding='0' cellspacing='0'>");
						str.append("				<tr>");
						
						str.append("					<td width='529' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Local de Pagamento</span><br />");
						str.append("   						Pagável em qualquer banco até o vencimento.");
						str.append("					</td>");
													        
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='194' class='border-bottom-fundo'>");
						str.append("						<span class='titulo_linhas'>Data de Vencimento</span><br />");
						str.append(							"<strong>"+formatDate(boleto.getDatas().getVencimento())+"</strong>");
						str.append("					</td>");
						
						str.append("				</tr>");
						str.append("			</table>");
						str.append("		</td>");
						str.append("	</tr>");
						
						//Linha: Cedente, Agência/Código Cedente
						str.append("	<tr>");
						str.append("		<td colspan='3'>");
						str.append("			<table width='95%' border='0' cellpadding='0' cellspacing='0' class='linhas_formatacao'>");
						str.append("				<tr>");
						
						str.append("					<td width='529' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Nome do Beneficiário</span><br />");
						str.append("						"+boleto.getEmissor().getCedente()+" "+boleto.getCnpj()+"<br/>");
						str.append("					</td>");
						
						str.append("					<td width='1%' >");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
			        
						str.append("					<td width='194' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Agência / Código do Beneficiário</span><br />");
						str.append("						"+boleto.getEmissor().getAgenciaFormatado()+"<br/>");
						str.append("					</td>");
						
						str.append("				</tr>");			
						str.append("			</table>");
						str.append("		</td>");
						str.append("	</tr>");
						
						
						
						//Linha Endereço cedente, nosso numero
						str.append("	<tr>");
						str.append("		<td colspan='3'>");
						str.append("			<table width='95%' border='0' cellpadding='0' cellspacing='0' class='linhas_formatacao'>");
						str.append("				<tr>");
						
						str.append("					<td width='120' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Data do Documento</span><br />");
						str.append(							formatDate(boleto.getDatas().getDocumento()));
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='120' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Nr. Documento</span><br />");
						str.append(							boleto.getNumeroDoDocumento());
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='100' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Espécie DOC</span><br />");
						
						
						if(CheckNdocUtil.checkNdocAcesso(boleto.getNumeroDoDocumento())){
							str.append("						DS");
						}else{
							str.append("						DM");
						}
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='80' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Aceite.</span><br />");
						str.append("						N");
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='75' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Data Processamento</span><br />");
						str.append(							formatDate(boleto.getDatas().getDocumento()));
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
													
						str.append("					<td width='194' class='border-bottom' height='20' >");
						str.append("						<span class='titulo_linhas'>Nosso Número</span><br />");
						str.append(							boleto.getBanco().getNossoNumeroDoEmissorFormatado(boleto.getEmissor()));
						str.append("					</td>");
						str.append("				</tr>");
						str.append("			</table>");
						str.append("		</td>");
						str.append("	</tr>");
						
						//Linha Data Doc. Ndoc, Especie, Aceite, Data Proc. Carteira, Moeda, Quantidade, Valor doc
						str.append("	<tr>");
						str.append("		<td colspan='3'>");
						str.append("			<table width='95%' border='0' cellpadding='0' cellspacing='0' class='linhas_formatacao'>");
						str.append("				<tr>");
						
						str.append("					<td width='120' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Uso do banco</span><br />");					
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='120' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Carteira</span><br />");
						str.append(							boleto.getBanco().getCarteiraDoEmissorFormatado(boleto.getEmissor()));
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='100' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Espécie</span><br />");
						
						
						if(CheckNdocUtil.checkNdocAcesso(boleto.getNumeroDoDocumento())){
							str.append("						DS");
						}else{
							str.append("						DM");
						}
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						
						str.append("					<td width='80' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>Quantidade</span><br />");
						str.append(						boleto.getQtd() != null ? boleto.getQtd() : "");
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='75' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>xValor</span><br />");
						str.append(						"");
						str.append("					</td>");
						
						str.append("					<td width='1%'>");
						str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
						str.append("					</td>");
						
						str.append("					<td width='191' class='border-bottom-fundo'>");
						str.append("						<span class='titulo_linhas'>(=)Valor do Documento</span><br />");
						str.append("						<strong> "+Real.formatDbToString(boleto.getValorBoleto().toString())+"</strong>");
						str.append("					</td>");
						str.append("				</tr>");
						str.append("			</table>");
						str.append("		</td>");
						str.append("	</tr>");
						
						str.append("	<tr>");
						
						str.append("		<td width='400' border='0' valign='top' >");
						str.append("			<span class='titulo_linhas'>Informações de Responsabilidade do Beneficiário</span><br />");
						str.append("<br />");
						
						
						
						if(CheckNdocUtil.checkNdocAcesso(boleto.getNumeroDoDocumento()) && contrato != null && contrato.getPlano() != null){
							str.append("Serviço de Internet – SCM – Plano"+contrato.getPlano().getDownload()+"<br/> Período "+getDataMinus30Days(boleto.getDatas().getVencimento())+" a "+getData(boleto.getDatas().getVencimento())+"<br />");
							String[] codigos = boleto.getNumeroDoDocumento().split("/");
							str.append("Contrato Termo Adesao Nº: "+codigos[0]+"<br/>");												
						}
						
						for (String s : boleto.getInstrucoes()) {
							str.append(s);
						}					
						
//						str.append("<br />");
						
						for (String s : boleto.getDescricoes()) {
							str.append(s);
						}
						
						str.append("<br/>Central de Atendimento da Anatel: 1331 Valor aproximado dos Tributos Federais: 13,45% e Municipais: 2,00% Fonte: IBPT Chave D529CB<br/>");
						str.append("<br />");
						str.append("Nome do Pagador/CPF/CNPJ/Endereço<br />");
						str.append("<strong>"+boleto.getSacado().getNome()+" "+boleto.getSacado().getCpf()+"</strong><br />");
						str.append(boleto.getSacado().getEndereco()+" - "+boleto.getSacado().getBairro()+" - "+boleto.getSacado().getComplemento()+"<br />");
						String ref = boleto.getSacado().getRefencia() != null  && boleto.getSacado().getRefencia().length() > 50 ? boleto.getSacado().getRefencia().substring(0, 50) : boleto.getSacado().getRefencia();
						str.append(boleto.getSacado().getCep()+" - "+boleto.getSacado().getCidade()+"-"+boleto.getSacado().getUf() + " ("+ref+")");					
						
						str.append("		</td>");
						
						
						str.append("		<td width='150' border='0' valign='top' haling='center'>");
						str.append("			<br /><br />");
						
						String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
						String nomeImg = basepath + "/WEB-INF/uploads/cod_"+boleto.getCodBoleto().toString()+".png";
						
						
						
						Image imgCodNNumero = geraImagemDoCodigoDeBarrasParaNNumero(boleto.getCodBoleto().toString(), 30f);									
						File f = new File(nomeImg);
						
						
						try{						
							BufferedImage bi = toBufferedImage(imgCodNNumero, BufferedImage.TYPE_INT_ARGB);
							ImageIO.write(bi, "png", f);
						}catch(Exception e){
							e.printStackTrace();
						}
						
											
					
						str.append("<img src='"+nomeImg+"' /><br/>"+boleto.getCodBoleto().toString());
									
						
						
						str.append("		</td>");
							
						str.append("		<td  valign='top' border='0'>");
						
						str.append("			<table width='84%'  border='0' cellpadding='0' cellspacing='0' class='linhas_formatacao'>");
										
						str.append("				<tr>");
						
						str.append("					<td width='191' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>(-) Desconto / Abatimento</span><br />");
						str.append("						&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
						
					
						
						str.append("				<tr>");
						
						str.append("					<td width='191' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>(+) Juros / Multa</span><br />");
		
						
						str.append("						&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
						
						
										
						
						str.append("				<tr>");
						
						str.append("					<td width='191' class='border-bottom' height='20'>");
						str.append("						<span class='titulo_linhas'>(=) Valor Cobrado</span><br />");
						str.append("						&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
						
						str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
						
	str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
						
	str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
	str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
	str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
	str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
						
						
	str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
						
	str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
	str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
	str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
	str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("					</td>");
						
						str.append("				</tr>");
						
						

						
						
						str.append("				<tr>");
						
						str.append("					<td style='text-align:right; ' width='191' height:'100' >");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("			&nbsp;");
						str.append("						<img style='margin-top:30px;' src='"+logo_digital_bb+"' width='180' />");
						str.append("					</td>");
						
						str.append("				</tr>");
						
									
						
						str.append("			</table>");		
						
						str.append("		</td>");
						
						str.append("	</tr>");				
						
						str.append("</table>");
						
						str.append("<table border='0' cellpadding='0' cellspacing='0' width='100%'>");
						str.append("	<tr>");
						str.append("		<td class='border-bottom'>");
						str.append("			<br/>");
						str.append("			&nbsp;");
						str.append("		</td>");
						str.append("	</tr>");				
						str.append("</table>");
						
						
						str.append("<table border='0' cellpadding='0' cellspacing='0' width='70%'>");
						str.append("	<tr>");
						str.append("		<td class='aut_mec'>");
						str.append("			<br/>");
						str.append("			Autênticação Mecanica");
						str.append("		</td>");
						str.append("	</tr>");				
						str.append("</table>");
											
						//get the XMLWorkerHelper Instance		
						XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
						//convert to PDF
						worker.parseXHtml(writer, doc,new StringReader(str.toString()));
									
							
						
							//Image imagemDoCodigoDeBarras = GeradorDeImagemDoCodigoDeBarrasUtil.geraImagemDoCodigoDeBarrasPara(
							//		boleto.getBanco().geraCodigoDeBarrasPara(boleto), 55.00f);		
						
							//com.itextpdf.text.Image barCode = com.itextpdf.text.Image.getInstance(toBufferedImage(imagemDoCodigoDeBarras, BufferedImage.TYPE_INT_ARGB), null);						
							///barCode.scaleToFit(300f,100f);
							//barCode.setSpacingAfter(500.0f);
							//barCode.setSpacingBefore(100.0f);
							
							//IMPRIMIR CÓDIGO DE BARRAS SOMENTE PARA CONTRATOS QUE EMITEM NOTA FISCAL
							//if(imprimir_cod_barras){
								
							//	doc.add(barCode);
							//}
							
							
							//worker.parseXHtml(writer, doc, new StringReader("<br/>."));
							DottedLineSeparator line = new DottedLineSeparator();						
							line.setPercentage(97.0f);
							line.setOffset(-20.0f);
							
							
							if(i % 2 == 0){ 
								doc.add(line);
							}
							
							
						
						
					}
				
				
				
					if(boleto.getBanco().getClass() == Sicredi.class){
						
					
				
					
					StringBuilder str = new StringBuilder();
					
					str.append("<style type='text/css'>");
					str.append(".table1 {");
					str.append("	width: 800px;");
					str.append("	border-bottom-width: 1px;");
					str.append("	border-bottom-style: dashed;");
					str.append("	border-bottom-color: #000;");
					str.append("}");
					
					str.append(".cabecalho {");
					str.append("	font-family: Arial, Helvetica, sans-serif;");
					str.append("	font-size: 14px;");
					str.append("	font-weight: bold;");
					str.append("	text-align: right;");
					str.append("	border-bottom-width: 1px;");
					str.append("	border-bottom-style: solid;");
					str.append("	border-bottom-color: #000;");
					str.append("	vertical-align: bottom;");
					str.append("}");
					
					str.append(".linhas_formatacao {");
					str.append("	font-family: Courier;");
					str.append("	font-size: 8px;");
					str.append("	color: #000;");
					str.append("	border-bottom-width: 1px;");
					str.append("	border-bottom-style: solid;");
					str.append("	border-bottom-color: #000;");
					str.append("	text-align: left;");
					str.append("	vertical-align: bottom;");
					str.append("	border-left-color: #000;");
					str.append("	padding-left: 0px;");
					str.append("}");
					
					str.append(".border-bottom{");
					str.append("	border-bottom-width: 1px;");
					str.append("	border-bottom-style: solid;");
					str.append("	border-bottom-color: #000;");	
					str.append("	font-family: Courier;");
					str.append("	font-size: 8px;");
					str.append("	color: #000;");
					str.append("}");
					
					
					str.append(".border-bottom-dotted{");
					str.append("	border-bottom-width: 1px;");
					str.append("	border-bottom-style: dotted;");
					str.append("	border-bottom-color: #000;");	
					str.append("	font-family: Courier;");
					str.append("	font-size: 8px;");
					str.append("	color: #000;");
					str.append("}");
					
					str.append(".border-bottom-fundo{");
					str.append("	border-bottom-width: 1px;");
					str.append("	border-bottom-style: dotted;");
					str.append("	border-bottom-color: #000;");	
					str.append("	font-family: Courier;");
					str.append("	font-size: 8px;");
					str.append("	color: #000;");
					str.append("	background-color: #CCC;");
					str.append("}");
					
					str.append(".formatacao_valores");
					str.append("{");
					str.append("	font-family: Courier;");
					str.append("	font-size: 8px;");
					str.append("	color: #000;");
					str.append("	padding: 0;");
					str.append("margin: 0;");
					str.append("}");
					
					str.append(".titulo_linhas {");
					str.append("	font-family: Arial, Helvetica, sans-serif;");
					str.append("	font-size: 8px;");
					str.append("	color: #000;");
					str.append("	padding-bottom: 0;");
					str.append("	margin-top: 5px;");
					str.append("	text-align: center;");
					str.append("}");
					
					str.append(".div_vertical {");
					str.append("	background-image: url(imagens/dic_boleto.gif);");
					str.append("}");
					
					str.append(".bg_div {");
					str.append("	background-image: url(imagens/bg_div.gif);");
					str.append("	height: 4px;");
					str.append("}");
					
					str.append(".linhaDigitavel {");
					str.append("	border-bottom-width: 1px;");
					str.append("	border-bottom-style: solid;");
					str.append("	border-bottom-color: #000;");	
					str.append("	font-family: Arial, Helvetica, sans-serif;");
					str.append("	font-size: 14px;");
					str.append("	text-align: right;");
					str.append("	color: #000;");
					str.append("	padding-right: 10px;");
					str.append("}");
					
					
					str.append(".cabecalho_logo{");
					str.append("	vertical-align:top;");
					str.append("	padding-bottom:10px;");
					str.append("	border-bottom-width: 1px;");
					str.append("	border-bottom-style: solid;");
					str.append("	border-bottom-color: #000;");	
					str.append("}");
					
					
					str.append(".aut_mec{");
					str.append("	font-size:8px;");
					str.append("	text-align:right;");
					str.append("	padding-top: 10px;");
					str.append("}");
					
					
					str.append(".table_space{");
					str.append("	margin-top:35px; ");
					str.append("}");
					
					
					str.append("</style>");
//			
					if(i % 2 == 0){ 
												
						str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1 border-bottom table_space' >");
					}else{	
						if(i != 1){
							doc.newPage();
						}
						
						
						str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1 border-bottom'>");
					}
					
					i++;
//					
//					//Linha1, Logotipo, 001-9, Recibo Sacado e Recibo Entrega
					
					String base = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
					GeradorDeLinhaDigitavel linhaDigitavelGenerator = new GeradorDeLinhaDigitavel();
					StringBuilder codigoDeBarras = new StringBuilder(boleto.getBanco().geraCodigoDeBarrasPara(boleto)); 
					
					codigoDeBarras.insert(5, '.');
					codigoDeBarras.insert(11, "  ");
					codigoDeBarras.insert(18, '.');
					codigoDeBarras.insert(25, "  ");
					codigoDeBarras.insert(32, '.');
					codigoDeBarras.insert(39, "  ");
					codigoDeBarras.insert(42, "  ");
										
					String logo_digital_bb = base + "/WEB-INF/boleto/logo_digital_bb.png";
					String logo_url = "";
					if(boleto.getContaBancaria().getCod_banco().equals("001")){					
						logo_url = base + "/WEB-INF/boleto/bb_logo.png";
					}
					
					if(boleto.getContaBancaria().getCod_banco().equals("748")){					
						logo_url = base + "/WEB-INF/boleto/sicredi_logo.png";
					}
									
				
					str.append("	<tr>");
					str.append("		<td>");
					str.append("			<table width='94%' border='0' cellpadding='0' cellspacing='0'>");
					str.append("				<tr>");
					str.append("					<td width='80' class='cabecalho_logo' height='40'>");
					str.append("						<img src='"+logo_url+"' width='155' height='30' />");
					str.append("					</td>");
					
					str.append("					<td width='70' class='cabecalho' >");
					if(boleto.getContaBancaria().getCod_banco().equals("001")){
						str.append("						|"+boleto.getContaBancaria().getCod_banco()+"-9|");
					}
//					
					if(boleto.getContaBancaria().getCod_banco().equals("748")){
						str.append("						|"+boleto.getContaBancaria().getCod_banco()+"-X|");
					}
					
					
					str.append("					</td>");
					
					AcessoCliente contrato = null;
					if(CheckNdocUtil.checkNdocAcesso(boleto.getNumeroDoDocumento())){
						String[] codigos = boleto.getNumeroDoDocumento().split("/");
						
						
						if(codigos[0] != null){
							try{
								contrato = ContratosAcessoDAO.find(Integer.parseInt(codigos[0].toString()));
							}catch(Exception e){
								
							}
						}
					}
					
					boolean imprimir_cod_barras = false;
					
					ControleTitulo controle = null;
					Query q = em.createQuery("select c from ControleTitulo c where c.nome =:nome and c.empresa_id =:empresa", ControleTitulo.class); 
					q.setParameter("nome", boleto.getControle());
					q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
					if(q.getResultList().size() == 1){
						controle = (ControleTitulo)q.getSingleResult();
						
						if(controle != null && controle.getRegistro() != null && controle.getRegistro().equals("SIM")){
							imprimir_cod_barras = true;
							
							if(contrato != null && contrato.getEmitir_nfe_automatico() != null &&  contrato.getEmitir_nfe_automatico().equals("SIM") && contrato.getEmitir_nfe_c_boleto_aberto() != null && contrato.getEmitir_nfe_c_boleto_aberto().equals("SIM")){
								imprimir_cod_barras = true;
							}else{
								
								if(contrato != null){
									imprimir_cod_barras = false;
								}else{
									imprimir_cod_barras = true;
								}
							}
						}
					}
//								
					str.append("					<td width='220' class='cabecalho' >");
					str.append("						<span class='titulo_linhas'>Recibo de Pagador</span><br />");
					
					if(imprimir_cod_barras){//VOLTAR AQUI
						str.append(	"						<span style='font-size:10px;'>"+ codigoDeBarras.toString() +"</span>");
					}
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='140' class='cabecalho' >");
					str.append("						Recibo Entrega");
					str.append("					</td>");
					str.append("				</tr>");
					str.append("			</table>");
					str.append("		</td>");
					str.append("	</tr>");
					str.append("</table>");
					
//					
					str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1' class='border-bottom'>");
					str.append("	<tr>");
					str.append("		<td>");
					str.append("			<table width='97%' border='0' cellpadding='0' cellspacing='0'>");
					str.append("				<tr>");
				

					str.append("					<td class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Nome do Pagador/CPF/CNPJ/Endereço</span><br />");			
					str.append(							boleto.getSacado().getNome()+" "+boleto.getSacado().getCpf()+" <br/> "+boleto.getSacado().getEndereco()+" "+boleto.getSacado().getBairro() );
					str.append("						<br/><span class='titulo_linhas'>Sacador/Avalista</span>");			
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='60' class='border-bottom' height='20' >");
					str.append("						<span class='titulo_linhas'>Vencimento</span><br />");			
					str.append(							formatDate(boleto.getDatas().getVencimento()));
					str.append("					</td>");
//					
					str.append("					<td width='2%' class='border-bottom' height='20'>");
					str.append("						<img src='imagens/div_txt.gif' width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='60' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>(=) Valor Documento</span><br />");			
					str.append(							Real.formatDbToString(boleto.getValorBoleto().toString()));
					str.append("					</td>");
					
					str.append("					<td width='2%' class='border-bottom' height='20'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='60' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Nº Documento</span><br />");	
					str.append(							boleto.getNumeroDoDocumento());
					str.append("					</td>");
					
					
					str.append("				</tr>");
					str.append("			</table>");
					str.append("		</td>");
					str.append("	</tr>");
					str.append("</table>");
					
					
					str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1' class='border-bottom'>");
					str.append("	<tr>");
					str.append("		<td>");
					str.append("			<table width='97%' border='0' cellpadding='0' cellspacing='0'>");
					str.append("				<tr>");
				

					str.append("					<td width='100' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Nosso Número</span><br />");
					str.append(							boleto.getNossoNumero());
					str.append("					</td>");
					
					str.append("					<td width='2%' class='border-bottom' height='20'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='85' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Nº Documento</span><br />");	
					str.append(							boleto.getNumeroDoDocumento());
					str.append("					</td>");
					
					str.append("					<td width='2%' class='border-bottom' height='20'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='80' class='border-bottom' height='20' >");
					str.append("						<span class='titulo_linhas'>Vencimento</span><br />");			
					str.append(							formatDate(boleto.getDatas().getVencimento()));
					str.append("					</td>");
					
					str.append("					<td width='2%' class='border-bottom' height='20'>");
					str.append("						<img src='imagens/div_txt.gif' width='6' height='18' />");
					str.append("					</td>");
//					
					str.append("					<td width='100' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Valor do Documento</span><br />");			
					str.append(							Real.formatDbToString(boleto.getValorBoleto().toString()));
					str.append("					</td>");
					
					str.append("					<td width='2%' class='border-bottom' height='20'>");
					str.append("						<img src='imagens/div_txt.gif' width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='75' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>(=) Valor Pago</span><br />");			
					str.append("_ ");
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					
					str.append("					<td width='2%' class='border-bottom' height='20'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='167' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Pagador</span><br />");			
					str.append(							boleto.getSacado().getNome().length() > 36 ? boleto.getSacado().getNome().substring(0,36) : boleto.getSacado().getNome());
					str.append("					</td>");			
					
					str.append("				</tr>");
					str.append("			</table>");
					str.append("		</td>");
					str.append("	</tr>");
					str.append("</table>");
					
					str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1' class='border-bottom'>");
					str.append("	<tr>");
					str.append("		<td>");
					str.append("			<table width='97%' border='0' cellpadding='0' cellspacing='0'>");
					str.append("				<tr>");
				
					str.append("					<td class='border-bottom-dotted'>");
					str.append("						<span class='titulo_linhas'>Nome do Beneficiário/CPF/CNPJ/Endereço</span><br />");			
					str.append("						"+boleto.getEmissor().getCedente()+" "+boleto.getCnpj()+" "+OpusERP4UI.getEmpresa().getEndereco()+"  "+OpusERP4UI.getEmpresa().getBairro()+" "+OpusERP4UI.getEmpresa().getNumero()+" - "+OpusERP4UI.getEmpresa().getCidade()+ "<br/>");
					str.append("						"+boleto.getAgenciaECodigoCedente());
					str.append("						<br/><span class='titulo_linhas'>Agência/Código do Beneficiário</span>");			
					str.append("					</td>");		
					
					str.append("					<td width='100' class='border-bottom' >");
					str.append("						<span class='titulo_linhas'>Autênticação Mecânica</span><br /><br /><br /><br />");			
					
					str.append("					</td>");		
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					

					str.append("					<td width='130' class='border-bottom' >");
					str.append("						<span class='titulo_linhas'>Assinatura do Recebedor</span><br /><br /><br /><br />");			
					str.append("					</td>");
					
					str.append("					<td width='80' class='border-bottom' >");
					str.append("						<span class='titulo_linhas'>Data de Entrega</span><br /><br />");			
					str.append("						____/____/___");
					str.append("					</td>");
				
					
					str.append("				</tr>");
					str.append("			</table>");
					str.append("		</td>");
					str.append("	</tr>");
					str.append("</table>");
					
					
										
					//Parte Boleto: TOPO LINHA DIGITAVEL
					str.append("<table border='0' cellpadding='0' cellspacing='0' class='table1' class='border-bottom'>");
					str.append("	<tr>");
					str.append("		<td colspan='3'>");
					str.append("			<table width='95%' border='0' cellpadding='0' cellspacing='0'>");
					str.append("				<tr>");
					str.append("					<td class='cabecalho_logo' height='40' width='50'>");
					str.append("						<img src='"+logo_url+"'  width='155' height='30' />");
					str.append("					</td>");
					
					str.append("					<td  class='border-bottom' height='20' width='20'>");
					str.append("						<span class='cabecalho'>|748-X|</span>");
					str.append("					</td>");
		        
					str.append("					<td class='linhaDigitavel' width='100'>");
					
					
					
					if(imprimir_cod_barras){//VOLTAR AQUI
						str.append(						codigoDeBarras.toString());
					}
					str.append("					</td>");
					str.append("				</tr>");
					str.append("			</table>");
					str.append("		</td>");
					str.append("	</tr>");
					
					//Linha: Local Pagto., Uso Banco, Vencimentoborder-bottom
					str.append("	<tr>");    
					str.append("		<td colspan='3'>");
					str.append("			<table width='95%' border='0' cellpadding='0' cellspacing='0'>");
					str.append("				<tr>");
					
					str.append("					<td width='529' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Local de Pagamento</span><br />");
					str.append("   						Pagável em qualquer banco até o vencimento.");
					str.append("					</td>");
												        
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='194' class='border-bottom-fundo'>");
					str.append("						<span class='titulo_linhas'>Vencimento</span><br />");
					str.append(							"<strong>"+formatDate(boleto.getDatas().getVencimento())+"</strong>");
					str.append("					</td>");
					
					str.append("				</tr>");
					str.append("			</table>");
					str.append("		</td>");
					str.append("	</tr>");
					
					//Linha: Cedente, Agência/Código Cedente
					str.append("	<tr>");
					str.append("		<td colspan='3'>");
					str.append("			<table width='95%' border='0' cellpadding='0' cellspacing='0' class='linhas_formatacao'>");
					str.append("				<tr>");
					
					str.append("					<td width='529' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Beneficiário</span><br />");
					str.append("						"+boleto.getEmissor().getCedente()+" "+boleto.getCnpj()+"<br/>");
					str.append("					</td>");
					
					str.append("					<td width='1%' >");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
		        
					str.append("					<td width='194' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Agência / Código do Beneficiário</span><br />");
					str.append("						"+boleto.getEmissor().getAgenciaFormatado()+"."+boleto.getEmissor().getPostoBeneficiario()+"."+boleto.getEmissor().getContaCorrente()+"<br/>");
					str.append("					</td>");
					
					str.append("				</tr>");			
					str.append("			</table>");
					str.append("		</td>");
					str.append("	</tr>");
					
					
					
					//Linha Endereço cedente, nosso numero
					str.append("	<tr>");
					str.append("		<td colspan='3'>");
					str.append("			<table width='95%' border='0' cellpadding='0' cellspacing='0' class='linhas_formatacao'>");
					str.append("				<tr>");
					
					str.append("					<td width='120' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Data do Documento</span><br />");
					str.append(							formatDate(boleto.getDatas().getDocumento()));
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='120' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Número do Documento</span><br />");
					str.append(							boleto.getNumeroDoDocumento());
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='100' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Espécie Documento</span><br />");
					
					
					if(CheckNdocUtil.checkNdocAcesso(boleto.getNumeroDoDocumento())){
						str.append("						DS");
					}else{
						str.append("						DM");
					}
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='80' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Aceite</span><br />");
					str.append("						NÃO");
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='75' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Data Processamento</span><br />");
					str.append(							formatDate(boleto.getDatas().getDocumento()));
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
												
					str.append("					<td width='194' class='border-bottom' height='20' >");
					str.append("						<span class='titulo_linhas'>Nosso Número</span><br />");
					str.append(							boleto.getNossoNumero());
					str.append("					</td>");
					str.append("				</tr>");
					str.append("			</table>");
					str.append("		</td>");
					str.append("	</tr>");
					
					//Linha Data Doc. Ndoc, Especie, Aceite, Data Proc. Carteira, Moeda, Quantidade, Valor doc
					str.append("	<tr>");
					str.append("		<td colspan='3'>");
					str.append("			<table width='95%' border='0' cellpadding='0' cellspacing='0' class='linhas_formatacao'>");
					str.append("				<tr>");
					
					str.append("					<td width='120' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Uso do banco</span><br />");					
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='120' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Carteira</span><br />");
					str.append(							"1");
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='100' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Espécie</span><br />");
					
					
					str.append("						REAL");
					
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					
					str.append("					<td width='80' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Quantidade</span><br />");
					str.append(						boleto.getQtd() != null ? boleto.getQtd() : "");
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='75' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>Valor</span><br />");
					str.append(						"");
					str.append("					</td>");
					
					str.append("					<td width='1%'>");
					str.append("						<img src='imagens/div_txt.gif'  width='6' height='18' />");
					str.append("					</td>");
					
					str.append("					<td width='191' class='border-bottom-fundo'>");
					str.append("						<span class='titulo_linhas'>(=)Valor do Documento</span><br />");
					str.append("						<strong> "+Real.formatDbToString(boleto.getValorBoleto().toString())+"</strong>");
					str.append("					</td>");
					str.append("				</tr>");
					str.append("			</table>");
					str.append("		</td>");
					str.append("	</tr>");
					
					str.append("	<tr>");
					
					str.append("		<td width='400' border='0' valign='top' >");
					str.append("			<span class='titulo_linhas'>Instruções (Todas as informações deste bloqueto são de exclusiva responsabilidade do beneficiário)</span><br />");
					str.append("<br />");
					
					
					
					if(CheckNdocUtil.checkNdocAcesso(boleto.getNumeroDoDocumento()) && contrato != null && contrato.getPlano() != null && contrato.getPlano().getRate_limit() != null){
						str.append("Serviço de Internet – SCM – Plano "+contrato.getPlano().getRate_limit().split("/")[1]+"<br/> Período "+getDataMinus30Days(boleto.getDatas().getVencimento())+" a "+getData(boleto.getDatas().getVencimento())+"<br />");
						String[] codigos = boleto.getNumeroDoDocumento().split("/");
						str.append("Contrato Termo Adesao Nº: "+codigos[0]+"<br/>");												
					}
					
					for (String s : boleto.getInstrucoes()) {
						str.append(s);
					}					
					
//					str.append("<br />");
					
					for (String s : boleto.getDescricoes()) {
						str.append(s);
					}
					
					str.append("<br/>Central de Atendimento da Anatel: 1331 Valor aproximado dos Tributos Federais: 13,45% e Municipais: 2,00% Fonte: IBPT Chave D529CB<br/>");
					str.append("<br />");
					str.append("Nome do Pagador/CPF/CNPJ/Endereço<br />");
					str.append("<strong>"+boleto.getSacado().getNome()+" "+boleto.getSacado().getCpf()+"</strong><br />");
					str.append(boleto.getSacado().getEndereco()+" - "+boleto.getSacado().getBairro()+" - "+boleto.getSacado().getComplemento()+"<br />");
					String ref = boleto.getSacado().getRefencia() != null  && boleto.getSacado().getRefencia().length() > 50 ? boleto.getSacado().getRefencia().substring(0, 50) : boleto.getSacado().getRefencia();
					str.append(boleto.getSacado().getCep()+" - "+boleto.getSacado().getCidade()+"-"+boleto.getSacado().getUf() + " ("+ref+")");					
					
					str.append("		</td>");
					
					
					str.append("		<td width='150' border='0' valign='top' haling='center'>");
					str.append("			<br /><br />");
					
					String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
					String nomeImg = basepath + "/WEB-INF/uploads/cod_"+boleto.getCodBoleto().toString()+".png";					
					
					
					Image imgCodNNumero = geraImagemDoCodigoDeBarrasParaNNumero(boleto.getCodBoleto().toString(), 30f);										
					File f = new File(nomeImg);
					
					
					try{						
						BufferedImage bi = toBufferedImage(imgCodNNumero, BufferedImage.TYPE_INT_ARGB);
						ImageIO.write(bi, "png", f);
					}catch(Exception e){
						e.printStackTrace();
					}
					
										
				
					str.append("<img src='"+nomeImg+"' /><br/>"+boleto.getCodBoleto().toString());
								
					
					
					str.append("		</td>");
						
					str.append("		<td  valign='top' border='0'>");
					
					str.append("			<table width='84%'  border='0' cellpadding='0' cellspacing='0' class='linhas_formatacao'>");
									
					str.append("				<tr>");
					
					str.append("					<td width='191' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>(-) Desconto / Abatimento</span><br />");
					str.append("						&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
					
				
					
					str.append("				<tr>");
					
					str.append("					<td width='191' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>(+) Juros / Multa</span><br />");
	
					
					str.append("						&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
					
					
								
					
					str.append("				<tr>");
					
					str.append("					<td width='191' class='border-bottom' height='20'>");
					str.append("						<span class='titulo_linhas'>(=) Valor Cobrado</span><br />");
					str.append("						&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
					
					str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
					
str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
					
str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
					
					
str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
					
str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("					</td>");
					
					str.append("				</tr>");
					
					

					
					
					str.append("				<tr>");
					
					str.append("					<td style='text-align:right; ' width='191' height:'100' >");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("			&nbsp;");
					str.append("						<img style='margin-top:30px;' src='"+logo_digital_bb+"' width='180' />");
					str.append("					</td>");
					
					str.append("				</tr>");
					
								
					
					str.append("			</table>");		
					
					str.append("		</td>");
					
					str.append("	</tr>");				
					
					str.append("</table>");
					
					str.append("<table border='0' cellpadding='0' cellspacing='0' width='100%'>");
					str.append("	<tr>");
					str.append("		<td class='border-bottom'>");
					str.append("			<br/>");
					str.append("			&nbsp;");
					str.append("		</td>");
					str.append("	</tr>");				
					str.append("</table>");
					
					
					str.append("<table border='0' cellpadding='0' cellspacing='0' width='70%'>");
					str.append("	<tr>");
					str.append("		<td class='aut_mec'>");
					str.append("			<br/>");
					str.append("			Autênticação Mecanica");
					str.append("		</td>");
					str.append("	</tr>");				
					str.append("</table>");
										
//					//get the XMLWorkerHelper Instance		
					XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
					//convert to PDF
					worker.parseXHtml(writer, doc,new StringReader(str.toString()));
								
						
					
						Image imagemDoCodigoDeBarras = GeradorDeImagemDoCodigoDeBarrasUtil.geraImagemDoCodigoDeBarrasPara(
								boleto.getBanco().geraCodigoDeBarras44(boleto), 55.00f);		
					
						com.itextpdf.text.Image barCode = com.itextpdf.text.Image.getInstance(toBufferedImage(imagemDoCodigoDeBarras, BufferedImage.TYPE_INT_ARGB), null);						
						barCode.scaleToFit(300f,100f);
						barCode.setSpacingAfter(500.0f);
						barCode.setSpacingBefore(100.0f);
						
						//IMPRIMIR CÓDIGO DE BARRAS SOMENTE PARA CONTRATOS QUE EMITEM NOTA FISCAL
						if(imprimir_cod_barras){//VOLTAR AQUI
							
							doc.add(barCode);
						}
						
						
						//worker.parseXHtml(writer, doc, new StringReader("<br/>."));
						DottedLineSeparator line = new DottedLineSeparator();						
						line.setPercentage(97.0f);
						line.setOffset(-20.0f);
						
						
						if(i % 2 == 0){ 
							doc.add(line);
						}
						
						
					
						
												
						
					}
						
						
			}//For
			
			
			
		}catch(Exception e){
			if(doc != null && doc.isOpen() && doc.getPageNumber() > 0){
				doc.close(); 
			}
			
			e.printStackTrace();
		}finally{
			if(doc != null && doc.isOpen()){
				doc.close();
			}
		}	
		
	}
	
	public static Image geraImagemDoCodigoDeBarrasParaNNumero(String nNumero, float altura) {
		BarcodeInter25 bar = new BarcodeInter25();
		bar.setCode(nNumero);
		bar.setExtended(false);
		
		bar.setTextAlignment(Element.ALIGN_LEFT);
		bar.setBarHeight(altura);
		bar.setFont(null);
		bar.setX(0.73f);
		bar.setN(3);
		bar.setAltText(nNumero);
		
		return bar.createAwtImage(Color.BLACK, Color.WHITE);
	}
	
	private BufferedImage toBufferedImage(Image image, int type) {
		return generateBufferedImageFor(image, type);
	}
	
	private  BufferedImage generateBufferedImageFor(Image image, int type) {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		BufferedImage result = new BufferedImage(w, h, type);
		Graphics2D g = result.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return result;
	}
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
	private String formatDate(Calendar data) {
		return sdf.format(data.getTime());
	}
	
	private String getDataMinus30Days(Calendar data) {

		DateTime d = new DateTime(data);
		return sdf.format(d.minusMonths(1).toDateTime().minusDays(1).toDate());
	}
	private String getData(Calendar data) {

		DateTime d = new DateTime(data);
		return sdf.format(d.toDate());
	}

	@Override
	public InputStream getStream() {
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
