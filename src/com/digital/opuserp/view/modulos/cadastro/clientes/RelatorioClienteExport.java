package com.digital.opuserp.view.modulos.cadastro.clientes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.EnderecoCobranca;
import com.digital.opuserp.domain.EnderecoEntrega;
import com.digital.opuserp.util.ConnUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.data.Item;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Window;

public class RelatorioClienteExport implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	public RelatorioClienteExport(String data,Date dataInicial, Date dataFinal, String filtro, String valorFiltro, String order, String orderDirection) throws Exception{
		
		EntityManager em = ConnUtil.getEntity();

		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);	
		
		try{
			PdfWriter.getInstance(doc, baos);
			doc.open();
					
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			
			String campoData;
			data = "CADASTRO";
			if(data.equals("CADASTRO")){
				campoData = "data_cadastro";
			}else{
				campoData = "data_alteracao";
			}
			
			Query q = em.createQuery("select c from Cliente c", Cliente.class);
			//q.setParameter("dataInit", dataInicial);			
			//q.setParameter("dataFim", dataFinal);
			
			List<Cliente> result = q.getResultList();

			
			//Cabecalho
			byte[] logo = empresa.getLogo_empresa();
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
			
			
			for(Cliente c:result){
				doc.add(new Paragraph(c.getNome_razao()));
			}
			
			
			
			//Linha 1
			
//			Phrase pCodigo = new Phrase("CÓDIGO:", fCaptions);
//			Phrase pCodigoValor = new Phrase(cliente.getId().toString(), new Font(FontFamily.COURIER, 8));
//			
//			Phrase pCadastro = new Phrase("CADASTRO:", fCaptions);
//			Phrase pCadastroValor = new Phrase(cliente.getData_cadastro().toString(), new Font(FontFamily.COURIER, 8));
//			
//			Phrase pAlteracao = new Phrase("ALTERAÇÃO:",  fCaptions);
//			Phrase pAlteracaoValor = new Phrase(cliente.getData_alteracao().toString(), new Font(FontFamily.COURIER, 8));
//			
//			Phrase pNome = new Phrase("NOME:",  fCaptions);
//			Phrase pNomeValor = new Phrase(cliente.getNome_razao(), new Font(FontFamily.COURIER, 8));
//			
//			Phrase pCpf= new Phrase("CPF:",  fCaptions);
//			Phrase pCpfValor = new Phrase(cliente.getDoc_cpf_cnpj(), new Font(FontFamily.COURIER, 8));
//			
//			Phrase pRg= new Phrase("RG:",  fCaptions);
//			Phrase pRgValor = new Phrase(cliente.getDoc_rg_insc_estadual(), new Font(FontFamily.COURIER, 8));
//			
//			PdfPCell pCellCodigo = new PdfPCell();
//			pCellCodigo.addElement(pCodigo);
//			pCellCodigo.addElement(pCodigoValor);
//						
//			PdfPCell pCellCadastro = new PdfPCell();
//			pCellCadastro.addElement(pCadastro);
//			pCellCadastro.addElement(pCadastroValor);
//			
//			PdfPCell pCellAlteracao = new PdfPCell();
//			pCellAlteracao.addElement(pAlteracao);
//			pCellAlteracao.addElement(pAlteracaoValor);
//			
//			PdfPCell pCellNome = new PdfPCell();
//			pCellNome.addElement(pNome);
//			pCellNome.addElement(pNomeValor);
//			
//			PdfPCell pCellCpf = new PdfPCell();
//			pCellCpf.addElement(pCpf);
//			pCellCpf.addElement(pCpfValor);
//			
//			PdfPCell pCellRg = new PdfPCell();
//			pCellRg.addElement(pRg);
//			pCellRg.addElement(pRgValor);
//						
//			PdfPTable tb1 = new PdfPTable(new float[] { 0.07f, 0.10f, 0.10f,0.45f,0.15f,0.15f });
//			tb1.setWidthPercentage(100f);
//			tb1.addCell(pCellCodigo);
//			tb1.addCell(pCellCadastro);
//			tb1.addCell(pCellAlteracao);
//			tb1.addCell(pCellNome);
//			tb1.addCell(pCellCpf);
//			tb1.addCell(pCellRg);
//			
//			
//			doc.add(tb1);
			
		}finally{
			if(doc.isOpen() && doc != null){
				doc.close();
			}
		}
		
	}

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
		
	}
}
