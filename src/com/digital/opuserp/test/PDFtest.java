package com.digital.opuserp.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.util.ConnUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.collection.PdfTargetDictionary;

public class PDFtest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	static EntityManager em;
	public static void main(String[] args) throws Exception {
		
		
		em = ConnUtil.getEntity();
		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);
		OutputStream os = new FileOutputStream("/home/maconi/Documentos/teste.pdf");
		
		try {
			
			PdfWriter.getInstance(doc, os);
			
			doc.open();
			
			//Cabecalho
			
			byte[] logo = em.find(Empresa.class, 1).getLogo_empresa();
			Image imgLogo = Image.getInstance(logo);
			imgLogo.setAlignment(Element.ALIGN_CENTER);
			imgLogo.setSpacingAfter(100);
			
			Font fEmpresa = new Font(FontFamily.HELVETICA, 9, Font.BOLD);
			Font f = new Font(FontFamily.HELVETICA, 9);
			
			Paragraph pRazaoSocial = new Paragraph("DIGITAL TECNOLOGIA E TELECOMUNICAÇÃO LTDA.", fEmpresa);
			pRazaoSocial.setAlignment(Element.ALIGN_CENTER);
			pRazaoSocial.setSpacingBefore(15);
			Paragraph pEndereco = new Paragraph("RUA ADJAR MACIEL, 35 - CENTRO - BELOJARDIM - PE - 55155-040",f);
			pEndereco.setAlignment(Element.ALIGN_CENTER);
			Paragraph pTelefones = new Paragraph("FONES:(81) 37263125, (81) 37261806, 0800813125",f);
			pTelefones.setAlignment(Element.ALIGN_CENTER);
			pTelefones.setSpacingAfter(30);
					
			doc.add(imgLogo);			
			doc.add(pRazaoSocial);
			doc.add(pEndereco);
			doc.add(pTelefones);
			
			//SubTitulo
			Font fSubTitulo  = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
			Paragraph pFichaCiente = new Paragraph("FICHA DO CLIENTE", fSubTitulo);
			pFichaCiente.setAlignment(Element.ALIGN_CENTER);
			pFichaCiente.setSpacingAfter(20);
			
			doc.add(pFichaCiente);
			
			//Estilos de Fonts
			Font fCaptions = new Font(FontFamily.COURIER, 6, Font.BOLD);

			//Identificação
			Paragraph pIdentificacai = new Paragraph("IDENTIFICAÇÃO", fSubTitulo);
			pIdentificacai.setAlignment(Element.ALIGN_LEFT);
			pIdentificacai.setSpacingAfter(10);
			
			doc.add(pIdentificacai);
			
			
			
			//Linha 1
			
			Phrase pCodigo = new Phrase("CÓDIGO:", fCaptions);
			Phrase pCodigoValor = new Phrase("001", new Font(FontFamily.COURIER, 8));
			
			Phrase pCadastro = new Phrase("CADASTRO:", fCaptions);
			Phrase pCadastroValor = new Phrase("07/05/2013", new Font(FontFamily.COURIER, 8));
			
			Phrase pAlteracao = new Phrase("ALTERAÇÃO:",  fCaptions);
			Phrase pAlteracaoValor = new Phrase("07/05/2013", new Font(FontFamily.COURIER, 8));
			
			Phrase pNome = new Phrase("NOME:",  fCaptions);
			Phrase pNomeValor = new Phrase("ADEMIR DE SOUZA PINTO FILHOI", new Font(FontFamily.COURIER, 8));
			
			Phrase pCpf= new Phrase("CPF:",  fCaptions);
			Phrase pCpfValor = new Phrase("036.924.324-29", new Font(FontFamily.COURIER, 8));
			
			Phrase pRg= new Phrase("RG:",  fCaptions);
			Phrase pRgValor = new Phrase("5991456 SSPPE", new Font(FontFamily.COURIER, 8));
			
			PdfPCell pCellCodigo = new PdfPCell();
			pCellCodigo.addElement(pCodigo);
			pCellCodigo.addElement(pCodigoValor);
						
			PdfPCell pCellCadastro = new PdfPCell();
			pCellCadastro.addElement(pCadastro);
			pCellCadastro.addElement(pCadastroValor);
			
			PdfPCell pCellAlteracao = new PdfPCell();
			pCellAlteracao.addElement(pAlteracao);
			pCellAlteracao.addElement(pAlteracaoValor);
			
			PdfPCell pCellNome = new PdfPCell();
			pCellNome.addElement(pNome);
			pCellNome.addElement(pNomeValor);
			
			PdfPCell pCellCpf = new PdfPCell();
			pCellCpf.addElement(pCpf);
			pCellCpf.addElement(pCpfValor);
			
			PdfPCell pCellRg = new PdfPCell();
			pCellRg.addElement(pRg);
			pCellRg.addElement(pRgValor);
						
			PdfPTable tb1 = new PdfPTable(new float[] { 0.07f, 0.10f, 0.10f,0.45f,0.15f,0.15f });
			tb1.setWidthPercentage(100f);
			tb1.addCell(pCellCodigo);
			tb1.addCell(pCellCadastro);
			tb1.addCell(pCellAlteracao);
			tb1.addCell(pCellNome);
			tb1.addCell(pCellCpf);
			tb1.addCell(pCellRg);
			
			
			//Linha 2
			
			Phrase pStatus = new Phrase("STATUS:",  fCaptions);
			Phrase pStatusValor = new Phrase("ATIVO", new Font(FontFamily.COURIER, 8));
			
			Phrase pFantasia = new Phrase("FANTASIA:",  fCaptions);
			Phrase pFantasiaValor = new Phrase("", new Font(FontFamily.COURIER, 8));
			
			Phrase pContato = new Phrase("CONTATO:",  fCaptions);
			Phrase pContatoValor = new Phrase("ADEMIR DE SOUZA PINTO FILHO", new Font(FontFamily.COURIER, 8));
			
			Phrase pSexo = new Phrase("SEXO:",  fCaptions);
			Phrase pSexoValor = new Phrase("MASCULINO", new Font(FontFamily.COURIER, 8));
			
			Phrase pDataNascimento = new Phrase("DATA DE NASCIMENTO:",  fCaptions);
			Phrase pDataNascimentoValor = new Phrase("04/08/1989", new Font(FontFamily.COURIER, 8));

		
			PdfPCell pCellStatus = new PdfPCell();
			pCellStatus.addElement(pStatus);
			pCellStatus.addElement(pStatusValor);
						
			PdfPCell pCellFantasia = new PdfPCell();
			pCellFantasia.addElement(pFantasia);
			pCellFantasia.addElement(pFantasiaValor);
			
			PdfPCell pCellContato = new PdfPCell();
			pCellContato.addElement(pContato);
			pCellContato.addElement(pContatoValor);	
			
			PdfPCell pCellSexo = new PdfPCell();
			pCellSexo.addElement(pSexo);
			pCellSexo.addElement(pSexoValor);
			
			PdfPCell pCellDataNacimento = new PdfPCell();
			pCellDataNacimento.addElement(pDataNascimento);
			pCellDataNacimento.addElement(pDataNascimentoValor);
			
						
			PdfPTable tb2 = new PdfPTable(new float[] { 0.25f, 1f, 1f , 0.30f , 0.40f});
			tb2.setWidthPercentage(100f);
			tb2.addCell(pCellStatus);
			tb2.addCell(pCellFantasia);
			tb2.addCell(pCellContato);
			tb2.addCell(pCellSexo);
			tb2.addCell(pCellDataNacimento);
			
			
			
			doc.add(tb1);
			doc.add(tb2);	
			
			//ENDERECO PRINCIPAL
			Paragraph pEnderecoPrincipal = new Paragraph("ENDEREÇO PRINCIPAL", fSubTitulo);
			pEnderecoPrincipal.setAlignment(Element.ALIGN_LEFT);
			pEnderecoPrincipal.setSpacingAfter(10);
			pEnderecoPrincipal.setSpacingBefore(30);
			doc.add(pEnderecoPrincipal);
			
			Phrase pCepEnd= new Phrase("CEP:",  fCaptions);
			Phrase pCepValorEnd= new Phrase("55155660", new Font(FontFamily.COURIER, 8));
			
			Phrase pEnderecoCepEnd= new Phrase("ENDEREÇO:",  fCaptions);
			Phrase pEnderecoCepValorEnd= new Phrase("VILA DR. FERNANDO DE ABREU", new Font(FontFamily.COURIER, 8));
			
			Phrase pNumeroEnd= new Phrase("NUMERO:",  fCaptions);
			Phrase pNumeroValorEnd= new Phrase("03", new Font(FontFamily.COURIER, 8));
			
			Phrase pBairroEnd= new Phrase("BAIRRO:",  fCaptions);
			Phrase pBairroValorEnd= new Phrase("SÃO PEDRO", new Font(FontFamily.COURIER, 8));
			
			Phrase pCidadeEnd= new Phrase("CIDADE:",  fCaptions);
			Phrase pCidadeValorEnd= new Phrase("BELO JARDIM", new Font(FontFamily.COURIER, 8));
			
			Phrase pUFEnd= new Phrase("UF:",  fCaptions);
			Phrase pUfValorEnd= new Phrase("PE", new Font(FontFamily.COURIER, 8));
			
			Phrase pPaisEnd= new Phrase("PAÍS:",  fCaptions);
			Phrase pPaisValorEnd= new Phrase("BRASIL", new Font(FontFamily.COURIER, 8));
			
			Phrase pComplementoEnd= new Phrase("COMPLEMENTO:",  fCaptions);
			Phrase pComplementoValorEnd= new Phrase("QUADRA C", new Font(FontFamily.COURIER, 8));
			
			Phrase pReferenciaEnd= new Phrase("REFERÊNCIA:",  fCaptions);
			Phrase pReferenciaValorEnd= new Phrase("PROX. A PRAÇA DOS EVENTOS", new Font(FontFamily.COURIER, 8));
			
			PdfPCell pCellCepEnd= new PdfPCell();
			pCellCepEnd.addElement(pCepEnd);
			pCellCepEnd.addElement(pCepValorEnd);
			
			PdfPCell pCellEnderecoEnd= new PdfPCell();
			pCellEnderecoEnd.addElement(pEnderecoCepEnd);
			pCellEnderecoEnd.addElement(pEnderecoCepValorEnd);
			
			PdfPCell pCellNumeroEnd= new PdfPCell();
			pCellNumeroEnd.addElement(pNumeroEnd);
			pCellNumeroEnd.addElement(pNumeroValorEnd);

			PdfPCell pCellBairroEnd= new PdfPCell();
			pCellBairroEnd.addElement(pBairroEnd);
			pCellBairroEnd.addElement(pBairroValorEnd);
			
			PdfPCell pCellCidadeEnd= new PdfPCell();
			pCellCidadeEnd.addElement(pCidadeEnd);
			pCellCidadeEnd.addElement(pCidadeValorEnd);			
			
			PdfPCell pCellUfEnd = new PdfPCell();
			pCellUfEnd.addElement(pUFEnd);
			pCellUfEnd.addElement(pUfValorEnd);
			
			PdfPCell pCellPaisEnd = new PdfPCell();
			pCellPaisEnd.addElement(pPaisEnd);
			pCellPaisEnd.addElement(pPaisValorEnd);
			
			PdfPCell pCellComplementoEnd = new PdfPCell();
			pCellComplementoEnd.addElement(pComplementoEnd);
			pCellComplementoEnd.addElement(pComplementoValorEnd);
			
			PdfPCell pCellReferenciaEnd = new PdfPCell();
			pCellReferenciaEnd.addElement(pReferenciaEnd);
			pCellReferenciaEnd.addElement(pReferenciaValorEnd);
			
			PdfPTable tb3 = new PdfPTable(new float[] { 0.25f, 1f, 0.10f});
			tb3.setWidthPercentage(100f);
			tb3.addCell(pCellCepEnd);
			tb3.addCell(pCellEnderecoEnd);
			tb3.addCell(pCellNumeroEnd);
			doc.add(tb3);
			
			
			PdfPTable tb4 = new PdfPTable(new float[] { 0.25f, 0.20f, 0.05f, 0.20f});
			tb4.setWidthPercentage(100f);
			tb4.addCell(pCellBairroEnd);
			tb4.addCell(pCellCidadeEnd);
			tb4.addCell(pCellUfEnd);
			tb4.addCell(pCellPaisEnd);
			doc.add(tb4);
			
			
			PdfPTable tb5 = new PdfPTable(new float[] { 0.5f, 0.5f});
			tb5.setWidthPercentage(100f);
			tb5.addCell(pCellComplementoEnd);
			tb5.addCell(pCellReferenciaEnd);			
			doc.add(tb5);
			
			
			
			//ENDERECO DE ENTREGA
			Paragraph pEnderecoEntrega = new Paragraph("ENDEREÇO DE ENTREGA", fSubTitulo);
			pEnderecoEntrega.setAlignment(Element.ALIGN_LEFT);
			pEnderecoEntrega.setSpacingAfter(10);
			pEnderecoEntrega.setSpacingBefore(30);
			doc.add(pEnderecoEntrega);
			
			Phrase pCepEndEntrega = new Phrase("CEP:",  fCaptions);
			Phrase pCepValorEndEntrega = new Phrase("55155660", new Font(FontFamily.COURIER, 8));
			
			Phrase pEnderecoCepEndEntrega = new Phrase("ENDEREÇO:",  fCaptions);
			Phrase pEnderecoCepValorEndEntrega = new Phrase("VILA DR. FERNANDO DE ABREU", new Font(FontFamily.COURIER, 8));
			
			Phrase pNumeroEndEntrega = new Phrase("NUMERO:",  fCaptions);
			Phrase pNumeroValorEndEntrega = new Phrase("03", new Font(FontFamily.COURIER, 8));
			
			Phrase pBairroEndEntrega = new Phrase("BAIRRO:",  fCaptions);
			Phrase pBairroValorEndEntrega = new Phrase("SÃO PEDRO", new Font(FontFamily.COURIER, 8));
			
			Phrase pCidadeEndEntrega= new Phrase("CIDADE:",  fCaptions);
			Phrase pCidadeValorEndEntrega = new Phrase("BELO JARDIM", new Font(FontFamily.COURIER, 8));
			
			Phrase pUFEndEntrega= new Phrase("UF:",  fCaptions);
			Phrase pUfValorEndEntrega = new Phrase("PE", new Font(FontFamily.COURIER, 8));
			
			Phrase pPaisEndEntrega= new Phrase("PAÍS:",  fCaptions);
			Phrase pPaisValorEndEntrega = new Phrase("BRASIL", new Font(FontFamily.COURIER, 8));
			
			Phrase pComplementoEndEntrega= new Phrase("COMPLEMENTO:",  fCaptions);
			Phrase pComplementoValorEndEntrega = new Phrase("QUADRA C", new Font(FontFamily.COURIER, 8));
			
			Phrase pReferenciaEndEntrega= new Phrase("REFERÊNCIA:",  fCaptions);
			Phrase pReferenciaValorEndEntrega = new Phrase("PROX. A PRAÇA DOS EVENTOS", new Font(FontFamily.COURIER, 8));
			
			PdfPCell pCellCepEndEntrega = new PdfPCell();
			pCellCepEndEntrega.addElement(pCepEndEntrega);
			pCellCepEndEntrega.addElement(pCepValorEndEntrega);
			
			PdfPCell pCellEnderecoEndEntrega = new PdfPCell();
			pCellEnderecoEndEntrega.addElement(pEnderecoCepEndEntrega);
			pCellEnderecoEndEntrega.addElement(pEnderecoCepValorEndEntrega);
			
			PdfPCell pCellNumeroEndEntrega = new PdfPCell();
			pCellNumeroEndEntrega.addElement(pNumeroEndEntrega);
			pCellNumeroEndEntrega.addElement(pNumeroValorEndEntrega);

			PdfPCell pCellBairroEndEntrega= new PdfPCell();
			pCellBairroEndEntrega.addElement(pBairroEndEntrega);
			pCellBairroEndEntrega.addElement(pBairroValorEndEntrega);
			
			PdfPCell pCellCidadeEndEntrega= new PdfPCell();
			pCellCidadeEndEntrega.addElement(pCidadeEndEntrega);
			pCellCidadeEndEntrega.addElement(pCidadeValorEndEntrega);			
			
			PdfPCell pCellUfEndEntrega = new PdfPCell();
			pCellUfEndEntrega.addElement(pUFEndEntrega);
			pCellUfEndEntrega.addElement(pUfValorEndEntrega);
			
			PdfPCell pCellPaisEndEntrega = new PdfPCell();
			pCellPaisEndEntrega.addElement(pPaisEndEntrega);
			pCellPaisEndEntrega.addElement(pPaisValorEndEntrega);
			
			PdfPCell pCellComplementoEndEntrega = new PdfPCell();
			pCellComplementoEndEntrega.addElement(pComplementoEndEntrega);
			pCellComplementoEndEntrega.addElement(pComplementoValorEndEntrega);
			
			PdfPCell pCellReferenciaEndEntrega = new PdfPCell();
			pCellReferenciaEndEntrega.addElement(pReferenciaEndEntrega);
			pCellReferenciaEndEntrega.addElement(pReferenciaValorEndEntrega);
			
			PdfPTable tb6 = new PdfPTable(new float[] { 0.25f, 1f, 0.10f});
			tb6.setWidthPercentage(100f);
			tb6.addCell(pCellCepEndEntrega);
			tb6.addCell(pCellEnderecoEndEntrega);
			tb6.addCell(pCellNumeroEndEntrega);
			doc.add(tb6);
			
			
			PdfPTable tb7 = new PdfPTable(new float[] { 0.25f, 0.20f, 0.05f, 0.20f});
			tb7.setWidthPercentage(100f);
			tb7.addCell(pCellBairroEndEntrega);
			tb7.addCell(pCellCidadeEndEntrega);
			tb7.addCell(pCellUfEndEntrega);
			tb7.addCell(pCellPaisEndEntrega);
			doc.add(tb7);
			
			
			PdfPTable tb8 = new PdfPTable(new float[] { 0.5f, 0.5f});
			tb8.setWidthPercentage(100f);
			tb8.addCell(pCellComplementoEndEntrega);
			tb8.addCell(pCellReferenciaEndEntrega);			
			doc.add(tb8);
			
			
			
			//ENDERECO DE COBRANCA
			Paragraph pEnderecoCobranca= new Paragraph("ENDEREÇO DE COBRANÇA", fSubTitulo);
			pEnderecoCobranca.setAlignment(Element.ALIGN_LEFT);
			pEnderecoCobranca.setSpacingAfter(10);
			pEnderecoCobranca.setSpacingBefore(30);
			doc.add(pEnderecoCobranca);
			
			Phrase pCepEndCobranca = new Phrase("CEP:",  fCaptions);
			Phrase pCepValorEndCobranca= new Phrase("55155660", new Font(FontFamily.COURIER, 8));
			
			Phrase pEnderecoCepEndCobranca= new Phrase("ENDEREÇO:",  fCaptions);
			Phrase pEnderecoCepValorEndCobranca= new Phrase("VILA DR. FERNANDO DE ABREU", new Font(FontFamily.COURIER, 8));
			
			Phrase pNumeroEndCobranca= new Phrase("NUMERO:",  fCaptions);
			Phrase pNumeroValorEndCobranca= new Phrase("03", new Font(FontFamily.COURIER, 8));
			
			Phrase pBairroEndCobranca= new Phrase("BAIRRO:",  fCaptions);
			Phrase pBairroValorEndCobranca= new Phrase("SÃO PEDRO", new Font(FontFamily.COURIER, 8));
			
			Phrase pCidadeEndCobranca= new Phrase("CIDADE:",  fCaptions);
			Phrase pCidadeValorEndCobranca= new Phrase("BELO JARDIM", new Font(FontFamily.COURIER, 8));
			
			Phrase pUFEndCobranca= new Phrase("UF:",  fCaptions);
			Phrase pUfValorEndCobranca= new Phrase("PE", new Font(FontFamily.COURIER, 8));
			
			Phrase pPaisEndCobranca= new Phrase("PAÍS:",  fCaptions);
			Phrase pPaisValorEndCobranca= new Phrase("BRASIL", new Font(FontFamily.COURIER, 8));
			
			Phrase pComplementoEndCobranca= new Phrase("COMPLEMENTO:",  fCaptions);
			Phrase pComplementoValorEndCobranca= new Phrase("QUADRA C", new Font(FontFamily.COURIER, 8));
			
			Phrase pReferenciaEndCobranca= new Phrase("REFERÊNCIA:",  fCaptions);
			Phrase pReferenciaValorEndCobranca = new Phrase("PROX. A PRAÇA DOS EVENTOS", new Font(FontFamily.COURIER, 8));
			
			PdfPCell pCellCepEndCobranca = new PdfPCell();
			pCellCepEndCobranca.addElement(pCepEndCobranca);
			pCellCepEndCobranca.addElement(pCepValorEndCobranca);
			
			PdfPCell pCellEnderecoEndCobranca = new PdfPCell();
			pCellEnderecoEndCobranca.addElement(pEnderecoCepEndCobranca);
			pCellEnderecoEndCobranca.addElement(pEnderecoCepValorEndCobranca);
			
			PdfPCell pCellNumeroEndCobranca = new PdfPCell();
			pCellNumeroEndCobranca.addElement(pNumeroEndCobranca);
			pCellNumeroEndCobranca.addElement(pNumeroValorEndCobranca);

			PdfPCell pCellBairroEndCobranca= new PdfPCell();
			pCellBairroEndCobranca.addElement(pBairroEndCobranca);
			pCellBairroEndCobranca.addElement(pBairroValorEndCobranca);
			
			PdfPCell pCellCidadeEndCobranca= new PdfPCell();
			pCellCidadeEndCobranca.addElement(pCidadeEndCobranca);
			pCellCidadeEndCobranca.addElement(pCidadeValorEndCobranca);			
			
			PdfPCell pCellUfEndCobranca = new PdfPCell();
			pCellUfEndCobranca.addElement(pUFEndCobranca);
			pCellUfEndCobranca.addElement(pUfValorEndCobranca);
			
			PdfPCell pCellPaisEndCobranca = new PdfPCell();
			pCellPaisEndCobranca.addElement(pPaisEndCobranca);
			pCellPaisEndCobranca.addElement(pPaisValorEndCobranca);
			
			PdfPCell pCellComplementoEndCobranca = new PdfPCell();
			pCellComplementoEndCobranca.addElement(pComplementoEndCobranca);
			pCellComplementoEndCobranca.addElement(pComplementoValorEndCobranca);
			
			PdfPCell pCellReferenciaEndCobranca= new PdfPCell();
			pCellReferenciaEndCobranca.addElement(pReferenciaEndCobranca);
			pCellReferenciaEndCobranca.addElement(pReferenciaValorEndCobranca);
			
			PdfPTable tb9 = new PdfPTable(new float[] { 0.25f, 1f, 0.10f});
			tb9.setWidthPercentage(100f);
			tb9.addCell(pCellCepEndCobranca);
			tb9.addCell(pCellEnderecoEndCobranca);
			tb9.addCell(pCellNumeroEndCobranca);
			doc.add(tb9);
			
			
			PdfPTable tb10 = new PdfPTable(new float[] { 0.25f, 0.20f, 0.05f, 0.20f});
			tb10.setWidthPercentage(100f);
			tb10.addCell(pCellBairroEndCobranca);
			tb10.addCell(pCellCidadeEndCobranca);
			tb10.addCell(pCellUfEndCobranca);
			tb10.addCell(pCellPaisEndCobranca);
			doc.add(tb10);
			
			
			PdfPTable tb11 = new PdfPTable(new float[] { 0.5f, 0.5f});
			tb11.setWidthPercentage(100f);
			tb11.addCell(pCellComplementoEndCobranca);
			tb11.addCell(pCellReferenciaEndCobranca);			
			doc.add(tb11);
			
			
			
			
			//OUTRAS INFORMAÇÕES
			Paragraph pOutrasInfo = new Paragraph("OUTRAS INFORMAÇÕES", fSubTitulo);
			pOutrasInfo.setAlignment(Element.ALIGN_LEFT);
			pOutrasInfo.setSpacingAfter(10);
			pOutrasInfo.setSpacingBefore(30);
			doc.add(pOutrasInfo);
			
			Phrase pTelefonePrincipal = new Phrase("TELEFONE PRINCIPAL:",  fCaptions);
			Phrase pTelefonePrincipalValor= new Phrase("81 37263125", new Font(FontFamily.COURIER, 8));
			
			Phrase pTelefoneAlternativo1 = new Phrase("TELEFONE ALTERNATIVO 1:",  fCaptions);
			Phrase pTelefoneAlternativo1Valor= new Phrase("81 37263125", new Font(FontFamily.COURIER, 8));
			
			Phrase pTelefoneAlternativo2= new Phrase("TELEFONE ALTERNATIVO 2:",  fCaptions);
			Phrase pTelefoneAlternativo2Valor= new Phrase("81 37263125", new Font(FontFamily.COURIER, 8));
			
			Phrase pTelefoneAlternativo3 = new Phrase("TELEFONE ALTERNATIVO 3:",  fCaptions);
			Phrase pTelefoneAlternativo3Valor= new Phrase("81 37263125", new Font(FontFamily.COURIER, 8));
			
			Phrase pTelefoneAlternativo4= new Phrase("TELEFONE ALTERNATIVO 4:",  fCaptions);
			Phrase pTelefoneAlternativo4Valor= new Phrase("81 37263125", new Font(FontFamily.COURIER, 8));
			
			Phrase pEmailPrincipal= new Phrase("EMAIL PRINCIPAL:",  fCaptions);
			Phrase pEmailPrincipalValor= new Phrase("marconi@gmail.com", new Font(FontFamily.COURIER, 8));
			
			Phrase pEmailAlternativo= new Phrase("EMAIL ALTERNATIVO:",  fCaptions);
			Phrase pEmailAlternativoValor= new Phrase("marconi@gmail.com", new Font(FontFamily.COURIER, 8));
			
			Phrase pSite= new Phrase("SITE:",  fCaptions);
			Phrase pSiteValor= new Phrase("www.site.com.br", new Font(FontFamily.COURIER, 8));
			
			PdfPCell pCellTelefonePrincipal = new PdfPCell();
			pCellTelefonePrincipal.addElement(pTelefonePrincipal);
			pCellTelefonePrincipal.addElement(pTelefonePrincipalValor);
		
			PdfPCell pCellTelefoneAlternativo1 = new PdfPCell();
			pCellTelefoneAlternativo1.addElement(pTelefoneAlternativo1);
			pCellTelefoneAlternativo1.addElement(pTelefoneAlternativo1Valor);
			
			PdfPCell pCellTelefoneAlternativo2 = new PdfPCell();
			pCellTelefoneAlternativo2.addElement(pTelefoneAlternativo2);
			pCellTelefoneAlternativo2.addElement(pTelefoneAlternativo2Valor);
			
			PdfPCell pCellTelefoneAlternativo3 = new PdfPCell();
			pCellTelefoneAlternativo3.addElement(pTelefoneAlternativo3);
			pCellTelefoneAlternativo3.addElement(pTelefoneAlternativo3Valor);
			
			PdfPCell pCellTelefoneAlternativo4 = new PdfPCell();
			pCellTelefoneAlternativo4.addElement(pTelefoneAlternativo4);
			pCellTelefoneAlternativo4.addElement(pTelefoneAlternativo4Valor);
			
			PdfPCell pCellEmailPrincipal= new PdfPCell();
			pCellEmailPrincipal.addElement(pEmailPrincipal);
			pCellEmailPrincipal.addElement(pEmailPrincipalValor);
			
			PdfPCell pCellEmailAlternativo= new PdfPCell();
			pCellEmailAlternativo.addElement(pEmailAlternativo);
			pCellEmailAlternativo.addElement(pEmailAlternativoValor);
			
			PdfPCell pCellSite= new PdfPCell();
			pCellSite.addElement(pSite);
			pCellSite.addElement(pSiteValor);
		
			
			PdfPTable tb12 = new PdfPTable(new float[] { 1f, 1f, 1f, 1f});
			tb12.setWidthPercentage(100f);
			tb12.addCell(pCellTelefonePrincipal);
			tb12.addCell(pCellTelefoneAlternativo1);
			tb12.addCell(pCellTelefoneAlternativo2);
			tb12.addCell(pCellTelefoneAlternativo3);
			tb12.addCell(pCellTelefoneAlternativo4);
			doc.add(tb12);
			
			
			PdfPTable tb13 = new PdfPTable(new float[] { 1f, 1f, 1f});
			tb13.setWidthPercentage(100f);
			tb13.addCell(pCellEmailPrincipal);
			tb13.addCell(pCellEmailAlternativo);
			tb13.addCell(pCellSite);			
			doc.add(tb13);
					
			
			System.out.println("Criado!");
			
		
		} finally{
			
			doc.close();
			os.close();
			
			
			if(em.isOpen()){
				em.close();
				System.out.println("Fechou Em");
			}
			
			if(em.getEntityManagerFactory().isOpen()){
				em.getEntityManagerFactory().close();
				System.out.println("Fechou Emf");
			}
		}
		
	}

}
