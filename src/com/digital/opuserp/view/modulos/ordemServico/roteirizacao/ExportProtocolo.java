package com.digital.opuserp.view.modulos.ordemServico.roteirizacao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.OseDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.ChecklistOsInstalacao;
import com.digital.opuserp.domain.ChecklistOsLocalizacao;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class ExportProtocolo implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportProtocolo(Integer cod)throws Exception {

		EntityManager em = ConnUtil.getEntity();		
		Document doc = new Document(PageSize.A4, 24, 24, 14, 14);		
		
		URL url  = getClass().getResource("/com/digital/opuserp/img/quadrado.png");
		URL urlQuadVazio  = getClass().getResource("/com/digital/opuserp/img/quadrado_img_branco.png");
		
		Image imgQuad = Image.getInstance(url);
		Image imgQuadVazio = Image.getInstance(urlQuadVazio);
		
		try {
			PdfWriter.getInstance(doc, baos);
			doc.open();
			
			DataUtil dUtil = new DataUtil();
			
			//Busca
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			Ose ose = em.find(Ose.class, cod);
			Cliente cliente = ose.getCliente();
			
			Endereco  end = null;
			if(ose.getEnd()!=null){
				end = ose.getEnd();				
			}

			AcessoCliente acesso = null;
			if(ose.getContrato()!=null){
			  acesso = ose.getContrato();
			}

			ContasReceberDAO crDAO = new ContasReceberDAO();
			List<ContasReceber> cr = crDAO.procurarBoletosOse(ose);
			
			Date Vencimento = null;
			String formPgt = "";
			String ValorPgt = ose.getValor();
			if(cr!=null){
				if(cr.get(0).getData_vencimento()!=null && cr.get(0).getValor_titulo()!=null && !cr.get(0).getValor_titulo().equals("0,00")){
					Vencimento = cr.get(0).getData_vencimento();
					formPgt = "Boleto";
				}
				if(cr.get(0).getValor_titulo()!=null && !cr.get(0).getValor_titulo().equals("")){
					ValorPgt = cr.get(0).getValor_titulo();
				}
			}

			
//			if(ose.getId_ecf_pre_venda_cabecalho()!=null){
//				EcfPreVendaCabecalho preVenda = em.find(EcfPreVendaCabecalho.class, ose.getId_ecf_pre_venda_cabecalho());
//				FormaPagamento formaPgt = em.find(FormaPagamento.class,preVenda.getFormaPagtoID());
//				
//				String NomePgt = "";
//				if(formaPgt.getNome()!=null && formaPgt.getNome().equals("")){
//					NomePgt = formaPgt.getNome();
//				}
//			}
			String dataAgend = "";
			if(ose.getData_ex()!=null){
				dataAgend = dUtil.parseDataHoraBra(ose.getData_ex().toString());
			}
			
			
			List<Produto> produto = ProdutoDAO.getProdutosMateriais();
					
			//Estilos de Fonts
			Font fCaptions = new Font(FontFamily.HELVETICA, 6);
			Font fCaptionsBold = new Font(FontFamily.HELVETICA, 6, Font.BOLD);
			Font fCampo = new Font(FontFamily.HELVETICA, 5);
			Font fCampoBold = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font fConteudo = new Font(FontFamily.HELVETICA, 7);
			Font fConteudoBold = new Font(FontFamily.HELVETICA, 7, Font.BOLD);
			Font fTitulo  = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font fSubTitulo  = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font fCab = new Font(FontFamily.HELVETICA, 8);

			//titulo
			Paragraph pTitle= new Paragraph("ORDEM DE SERVIÇO Nº "+cod, fTitulo);
			pTitle.setAlignment(Element.ALIGN_CENTER);
			pTitle.setSpacingAfter(10);
			doc.add(pTitle);
			
			//Logo
			byte[] logo = empresa.getLogo_empresa();
			Image imgLogo = Image.getInstance(logo);
			imgLogo.setAlignment(Element.ALIGN_CENTER);
			imgLogo.setSpacingBefore(10);
			imgLogo.setSpacingAfter(5);
			
			Font fEmpresa = new Font(FontFamily.HELVETICA, 9, Font.BOLD);
			Font f = new Font(FontFamily.HELVETICA, 9);
			
			Paragraph pEndereco = new Paragraph(empresa.getEndereco()+ ", " +empresa.getNumero()+" - "+empresa.getBairro()+" - "+empresa.getCidade()+" -"+empresa.getUf()+" - "+empresa.getCep(),fCampoBold);
			pEndereco.setAlignment(Element.ALIGN_CENTER);
			//Paragraph pTelefones = new Paragraph("FONES: "+empresa.getDdd_fone1()+" "+empresa.getFone1()+" / "+empresa.get_0800(),fCampoBold);
			//pTelefones.setAlignment(Element.ALIGN_CENTER);
			Paragraph pcnpj = new Paragraph("CNPJ: "+empresa.getCnpj()+" FONES: "+empresa.getDdd_fone1()+" "+empresa.getFone1()+" / "+empresa.get_0800()   ,fCampoBold);
			pcnpj.setAlignment(Element.ALIGN_CENTER);
			pcnpj.setSpacingAfter(5);
			
			Paragraph pSite = new Paragraph("www.digitalonline.com.br",fCampoBold);
			pSite.setSpacingAfter(10);
			pSite.setAlignment(Element.ALIGN_CENTER);
	
			PdfPCell pCabLogo = new PdfPCell();
			pCabLogo.setBorderWidthLeft(0);
			pCabLogo.setPaddingRight(5);
			pCabLogo.addElement(imgLogo);
			pCabLogo.addElement(pEndereco);
			pCabLogo.addElement(pcnpj);
			//pCabLogo.addElement(pTelefones);
			pCabLogo.addElement(pSite);
						
			
			if(ose.getOperador_abertura()==null){
				ose.setOperador_abertura("");
			}		
			if(ose.getTecnico()==null){
				ose.setTecnico("");
			}		
			
			if(cliente.getDdd_fone1()==null || cliente.getTelefone1()==null || cliente.getTelefone1().equals("")){
				cliente.setDdd_fone1(" ");
			}
			if(cliente.getTelefone1() == null){
				cliente.setTelefone1(" ");
			}			
			if(cliente.getDdd_fone2()==null || cliente.getTelefone2()==null || cliente.getTelefone2().equals("")){
				cliente.setDdd_fone2(" ");
			}
			if(cliente.getTelefone2() == null){
				cliente.setTelefone2(" ");
			}			
			if(cliente.getDdd_cel1()==null || cliente.getCelular1()==null || cliente.getCelular1().equals("")){
				cliente.setDdd_cel1(" ");
			}
			if(cliente.getCelular1() == null){
				cliente.setCelular1(" ");
			}			
			if(cliente.getDdd_cel2()==null || cliente.getCelular2()==null || cliente.getCelular2().equals("")){
				cliente.setDdd_cel2(" ");
			}
			if(cliente.getCelular2() == null){
				cliente.setCelular2(" ");
			}
			
			//Linha 1 Operador			
			Paragraph pAberto = new Paragraph("Aberto por:", fCaptions);
			PdfPCell CellAbertura = new PdfPCell();
			CellAbertura.setBorderWidth(0);
			CellAbertura.setPadding(0);
			CellAbertura.addElement(pAberto);
			
			Paragraph pAbertoVl = new Paragraph(ose.getOperador_abertura(), fCaptionsBold);
			PdfPCell CellAberturaVl = new PdfPCell();
			CellAberturaVl.setBorderWidth(0);
			CellAberturaVl.setPadding(0);
			CellAberturaVl.addElement(pAbertoVl);
			
			Paragraph pDate = new Paragraph("em", fCaptions);
			PdfPCell CellDate = new PdfPCell();
			CellDate.setBorderWidth(0);
			CellDate.setPadding(0);
			CellDate.addElement(pDate);
			
			Paragraph pDateVl = new Paragraph(ose.getData_abertura() != null ? dUtil.parseDataHoraBra(ose.getData_abertura().toString()) : "", fCaptionsBold);
			PdfPCell CellDateVl = new PdfPCell();
			CellDateVl.setBorderWidth(0);
			CellDateVl.setPadding(0);
			CellDateVl.addElement(pDateVl);
			
			Paragraph pAgen = new Paragraph("Agendado para:", fCaptions);
			PdfPCell CellAgen = new PdfPCell();
			CellAgen.setBorderWidth(0);
			CellAgen.setPadding(0);
			CellAgen.addElement(pAgen);
			
			Paragraph pAgenVl = new Paragraph(dataAgend, fCaptionsBold);
			PdfPCell CellAgenVl = new PdfPCell();
			CellAgenVl.setPadding(0);
			CellAgenVl.setBorderWidth(0);
			CellAgenVl.addElement(pAgenVl);
			
			Paragraph pTurno = new Paragraph(ose.getTurno(), fCaptionsBold);
			PdfPCell CellTurno = new PdfPCell();
			CellTurno.setPadding(0);
			CellTurno.setBorderWidth(0);
			CellTurno.addElement(pTurno);
				
			PdfPTable tbAbertura = new PdfPTable(new float[] {0.19f,0.32f,0.10f,0.40f,0.26f,0.35f,0.20f});
			tbAbertura.setWidthPercentage(100f);
			tbAbertura.setSpacingBefore(5);
			tbAbertura.setSpacingAfter(5);
			tbAbertura.addCell(CellAbertura);
			tbAbertura.addCell(CellAberturaVl);
			tbAbertura.addCell(CellDate);
			tbAbertura.addCell(CellDateVl);
			tbAbertura.addCell(CellAgen);
			tbAbertura.addCell(CellAgenVl);
			tbAbertura.addCell(CellTurno);
			
			//Linha 2 Cliente
			Paragraph pCliente = new Paragraph("Cliente:", fCaptions);
			PdfPCell CellpCliente = new PdfPCell();
			CellpCliente.setPadding(0);
			CellpCliente.setBorderWidth(0);
			CellpCliente.addElement(pCliente);
			
			Paragraph pClienteVl = new Paragraph(cliente.getId()+" - "+cliente.getNome_razao(), fCaptionsBold);
			PdfPCell CellClienteVl = new PdfPCell();
			CellClienteVl.setPadding(0);
			CellClienteVl.setBorderWidth(0);
			CellClienteVl.addElement(pClienteVl);

			Paragraph pCpf = new Paragraph("CPF/C.N.P.J:", fCaptions);
			PdfPCell CellpCpf = new PdfPCell();
			CellpCpf.setPadding(0);
			CellpCpf.setBorderWidth(0);
			CellpCpf.addElement(pCpf);
			
			Paragraph pCpfVl = new Paragraph(cliente.getDoc_cpf_cnpj(), fCaptionsBold);
			PdfPCell CellpCpfVl = new PdfPCell();
			CellpCpfVl.setPadding(0);
			CellpCpfVl.setBorderWidth(0);
			CellpCpfVl.addElement(pCpfVl);
			
			PdfPTable tbCliente = new PdfPTable(new float[] {0.14f,0.95f,0.23f,0.60f});
			tbCliente.setWidthPercentage(100f);
			tbCliente.addCell(CellpCliente);
			tbCliente.addCell(CellClienteVl);
			tbCliente.addCell(CellpCpf);
			tbCliente.addCell(CellpCpfVl);
						
			//Linha 3 Contato
			Paragraph pContato = new Paragraph("Contato:", fCaptions);
			PdfPCell CellCpContato = new PdfPCell();
			CellCpContato.setPadding(0);
			CellCpContato.setBorderWidth(0);
			CellCpContato.addElement(pContato);
			
			Paragraph pContatoVl = new Paragraph(cliente.getContato(), fCaptionsBold);
			PdfPCell CellContatoVl = new PdfPCell();
			CellContatoVl.setPadding(0);
			CellContatoVl.setBorderWidth(0);
			CellContatoVl.addElement(pContatoVl);
			
			PdfPTable tbContato = new PdfPTable(new float[] {0.10f,1.25f});
			tbContato.setWidthPercentage(100f);
			tbContato.addCell(CellCpContato);
			tbContato.addCell(CellContatoVl);
			
			//Linha 4 Endereço
			Paragraph pCep= new Paragraph("Cep:", fCaptions);
			PdfPCell CellCep = new PdfPCell();
			CellCep.setPadding(0);
			CellCep.setBorderWidth(0);
			CellCep.addElement(pCep);
	
			Paragraph pCepVl = new Paragraph();	
			if(end!=null){
				pCepVl = new Paragraph(end.getCep(), fCaptionsBold);				
			}
			PdfPCell CellCepVl = new PdfPCell();
			CellCepVl.setPadding(0);
			CellCepVl.setBorderWidth(0);
			CellCepVl.addElement(pCepVl);
			
			PdfPTable tbEndCEp= new PdfPTable(new float[] {0.06f,1.25f});
			tbEndCEp.setWidthPercentage(100f);
			tbEndCEp.addCell(CellCep);
			tbEndCEp.addCell(CellCepVl);
			
			Paragraph pEnd= new Paragraph("Endereço:", fCaptions);
			PdfPCell CellEnd = new PdfPCell();
			CellEnd.setPadding(0);
			CellEnd.setBorderWidth(0);
			CellEnd.addElement(pEnd);
	
			Paragraph pEndVl = new Paragraph();	
			if(end!=null){
				pEndVl = new Paragraph(end.getEndereco()+", "+end.getNumero(), fCaptionsBold);				
			}
			PdfPCell CellEndVl = new PdfPCell();
			CellEndVl.setPadding(0);
			CellEndVl.setBorderWidth(0);
			CellEndVl.addElement(pEndVl);
			
			
			
			//Linha 5 Bairro
			Paragraph pBairro = new Paragraph("Bairro:", fCaptions);							
			PdfPCell CellBairro = new PdfPCell();
			CellBairro.setPadding(0);
			CellBairro.setBorderWidth(0);
			CellBairro.addElement(pBairro);
			
			Paragraph pBairroVl = new Paragraph();	
			if(end!=null && end.getBairro()!=null){
				pBairroVl = new Paragraph(end.getBairro(), fCaptionsBold);									
			}
			PdfPCell CellBairroVl = new PdfPCell();
			CellBairroVl.setPadding(0);
			CellBairroVl.setBorderWidth(0);
			CellBairroVl.addElement(pBairroVl);

			
			Paragraph pCidadde = new Paragraph("Cidade:", fCaptions);							
			PdfPCell CellCidade = new PdfPCell();
			CellCidade.setPadding(0);
			CellCidade.setBorderWidth(0);
			CellCidade.addElement(pCidadde);
			
			
			Paragraph pCidadeVl = new Paragraph();	
			if(end!=null && end.getCidade()!=null){
				pCidadeVl = new Paragraph(end.getCidade(), fCaptionsBold);
			}
			PdfPCell CellCidadeVl = new PdfPCell();
			CellCidadeVl.setPadding(0);
			CellCidadeVl.setBorderWidth(0);
			CellCidadeVl.addElement(pCidadeVl);
			
			PdfPTable tbEnd= new PdfPTable(new float[] {0.24f,1.25f,0.15f,0.69f,0.18f,0.40f});
			tbEnd.setWidthPercentage(100f);
			tbEnd.addCell(CellEnd);
			tbEnd.addCell(CellEndVl);
			tbEnd.addCell(CellBairro);
			tbEnd.addCell(CellBairroVl);
			tbEnd.addCell(CellCidade);
			tbEnd.addCell(CellCidadeVl);
			

			//Linha 6 Complemento
			Paragraph pComplemento = new Paragraph("Complemento:", fCaptions);
			PdfPCell CellComplemento = new PdfPCell();
			CellComplemento.setPadding(0);
			CellComplemento.setBorderWidth(0);
			CellComplemento.addElement(pComplemento);
			
			Paragraph pComplementoVl = new Paragraph();	
			if(end!=null && end.getComplemento()!=null){
				pComplementoVl = new Paragraph(end.getComplemento(), fCaptionsBold);
			}
			
			PdfPCell CellComplementoVl = new PdfPCell();
			CellComplementoVl.setPadding(0);
			CellComplementoVl.setBorderWidth(0);
			CellComplementoVl.addElement(pComplementoVl);
			
			PdfPTable tbComplemento = new PdfPTable(new float[] {0.18f,1.25f});
			tbComplemento.setWidthPercentage(100f);
			tbComplemento.addCell(CellComplemento);
			tbComplemento.addCell(CellComplementoVl);
			
			//Linha 7 Ponto de Referência
			Paragraph pReferencia = new Paragraph("Ponto de Referência:", fCaptions);
			PdfPCell CellReferencia = new PdfPCell();
			CellReferencia.setPadding(0);
			CellReferencia.setBorderWidth(0);
			CellReferencia.addElement(pReferencia);
			
			Paragraph pReferenciaVl = new Paragraph();	
			if(end!=null && end.getReferencia()!=null){
				pReferenciaVl = new Paragraph(end.getReferencia(), fCaptionsBold);
			}
			PdfPCell CellReferenciaVl = new PdfPCell();
			CellReferenciaVl.setPadding(0);
			CellReferenciaVl.setBorderWidth(0);
			CellReferenciaVl.addElement(pReferenciaVl);
			
			PdfPTable tbRef = new PdfPTable(new float[] {0.25f,1.25f});
			tbRef.setWidthPercentage(100f);
			tbRef.addCell(CellReferencia);
			tbRef.addCell(CellReferenciaVl);
			
			//Linha 8 Fones
			Paragraph pFones= new Paragraph("Telefones:", fCaptions);
			PdfPCell CellFones = new PdfPCell();
			CellFones.setPadding(0);
			CellFones.setBorderWidth(0);
			CellFones.addElement(pFones);
			
			Paragraph pFonesVl = new Paragraph(cliente.getDdd_fone1()+" "+cliente.getTelefone1()+"  "+cliente.getDdd_fone2()+" "+cliente.getTelefone2()+"  "+cliente.getDdd_cel1()+" "+cliente.getCelular1()+"  "+cliente.getDdd_cel2()+" "+cliente.getCelular2(), fCaptionsBold);
			PdfPCell CellFonesVl = new PdfPCell();
			CellFonesVl.setPadding(0);
			CellFonesVl.setBorderWidth(0);
			CellFonesVl.addElement(pFonesVl);
			
			
			//Linha 9 Técnico
			Paragraph pEmail= new Paragraph("Email:", fCaptions);
			PdfPCell CellEmail = new PdfPCell();
			CellEmail.setPadding(0);
			CellEmail.setBorderWidth(0);
			CellEmail.addElement(pEmail);
			
			Paragraph pEmailVl = new Paragraph();
			
			if(cliente.getEmail() !=null && !cliente.getEmail().equals("")){
				pEmailVl = new Paragraph(cliente.getEmail(), fCaptionsBold);				
			}
			
			PdfPCell CellpEmailVl = new PdfPCell();
			CellpEmailVl.setPadding(0);
			CellpEmailVl.setBorderWidth(0);
			CellpEmailVl.addElement(pEmailVl);

			PdfPTable tbFones = new PdfPTable(new float[] {0.35f,1.25f, 0.20f,2.00f});
			tbFones.setWidthPercentage(100f);
			tbFones.addCell(CellFones);
			tbFones.addCell(CellFonesVl);
			tbFones.addCell(CellEmail);
			tbFones.addCell(CellpEmailVl);
			
			//PdfPTable tbEmail = new PdfPTable(new float[] {0.12f,2.00f});
			//tbEmail.setWidthPercentage(100f);
			//tbEmail.setSpacingAfter(10);
			//tbEmail.addCell(CellEmail);
			//tbEmail.addCell(CellpEmailVl);
			
			//Linha 10 Técnico
			Paragraph pTecnico= new Paragraph("TÉCNICO:", fCaptions);
			PdfPCell CellTecnico = new PdfPCell();
			CellTecnico.setPadding(0);
			CellTecnico.setBorderWidth(0);
			CellTecnico.addElement(pTecnico);
			
			Paragraph pTecnicoVl = new Paragraph(ose.getTecnico(), fCaptionsBold);
			PdfPCell CellTecnicoVl = new PdfPCell();
			CellTecnicoVl.setPadding(0);
			CellTecnicoVl.setBorderWidth(0);
			CellTecnicoVl.addElement(pTecnicoVl);
			
			PdfPTable tbTecnico = new PdfPTable(new float[] {0.12f,1.25f});
			tbTecnico.setWidthPercentage(100f);
			tbTecnico.addCell(CellTecnico);
			tbTecnico.addCell(CellTecnicoVl);
			
			
			PdfPCell pCabInfo = new PdfPCell();
			pCabInfo.setPaddingLeft(10f);
			pCabInfo.setBorderWidthRight(0);
			pCabInfo.addElement(tbAbertura);
			pCabInfo.addElement(tbCliente);
			pCabInfo.addElement(tbContato);
			pCabInfo.addElement(tbEndCEp);
			pCabInfo.addElement(tbEnd);
			//pCabInfo.addElement(tbBairro);
			pCabInfo.addElement(tbComplemento);
			pCabInfo.addElement(tbRef);
			pCabInfo.addElement(tbFones);
			//pCabInfo.addElement(tbEmail);
			pCabInfo.addElement(tbTecnico);
			
			PdfPTable tb1 = new PdfPTable(new float[] { 0.35f, 0.65f });
			tb1.setWidthPercentage(100f);
			tb1.addCell(pCabLogo);
			tb1.addCell(pCabInfo);
			
			doc.add(tb1);			

			//grupo,subgrupo e tipo
			
			String cabGrupo = "";
			
			if(ose.getTipo_subgrupo().getVisualizar_na_os() != null &&
					ose.getTipo_subgrupo().getVisualizar_na_os().equals("SIM")){
				
				cabGrupo = ose.getGrupo().getNome()+" > "+ose.getSubgrupo().getNome()+" > "+ose.getTipo_subgrupo().getNome();
			}else{
				cabGrupo = ose.getGrupo().getNome()+" > "+ose.getSubgrupo().getNome();
			}
			
			Paragraph pGrupo = new Paragraph(cabGrupo, fTitulo);
			
			
			pGrupo.setAlignment(Element.ALIGN_CENTER);
			pGrupo.setSpacingBefore(5);
			pGrupo.setSpacingAfter(10);
			doc.add(pGrupo);			

			if(acesso!=null){
				//Nº Contrato
				Paragraph pContrato = new Paragraph("Nº de Contrato:", fConteudo);	
				pContrato.setAlignment(Element.ALIGN_RIGHT);
				Paragraph pContratoTipo = new Paragraph("Tipo de Contrato:", fConteudo);
				pContratoTipo.setAlignment(Element.ALIGN_RIGHT);
				Paragraph pContratoPlano = new Paragraph("Plano:", fConteudo);
				pContratoPlano.setAlignment(Element.ALIGN_RIGHT);
				Paragraph pContratoUPDown = new Paragraph("Uploar/Download:", fConteudo);
				pContratoUPDown.setAlignment(Element.ALIGN_RIGHT);
				PdfPCell pCellContrato = new PdfPCell();
//				pCellContrato.setPaddingLeft(5f);			
				pCellContrato.setPaddingBottom(5);			
				pCellContrato.setBorderWidthRight(0);			
				pCellContrato.addElement(pContrato);
				pCellContrato.addElement(pContratoTipo);
				pCellContrato.addElement(pContratoPlano);
				pCellContrato.addElement(pContratoUPDown);
				
				Paragraph pContratovl = new Paragraph(acesso.getId().toString(), fConteudoBold);			
				Paragraph pContratoTipovl = new Paragraph(acesso.getContrato().getNome(), fConteudoBold);
				Paragraph pContratoPlanovl = new Paragraph(acesso.getPlano().getNome(), fConteudoBold);
				Paragraph pContratoUPDownvl = new Paragraph(acesso.getPlano().getRate_limit(), fConteudoBold);
				PdfPCell pCellContratovl = new PdfPCell();
				pCellContratovl.setPaddingBottom(5);			
				pCellContratovl.setBorderWidthRight(0);			
				pCellContratovl.setBorderWidthLeft(0);			
				pCellContratovl.addElement(pContratovl);
				pCellContratovl.addElement(pContratoTipovl);
				pCellContratovl.addElement(pContratoPlanovl);
				pCellContratovl.addElement(pContratoUPDownvl);
				
				//Nº Termino Contrato
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");			
				DateTime dt1 = new DateTime(sdf.parse(sdf.format(new Date())));
				DateTime dt2 = new DateTime(sdf.parse(sdf.format(acesso.getData_venc_contrato())));			
				Integer days = Days.daysBetween(dt1, dt2).getDays();			
				String dias = days.toString();
				
				Paragraph pTerminoCont = new Paragraph("Termino do Contrato:", fConteudo);	
				pTerminoCont.setAlignment(Element.ALIGN_RIGHT);
				Paragraph pTerminoRegime = new Paragraph("Regime:", fConteudo);
				pTerminoRegime.setAlignment(Element.ALIGN_RIGHT);
				Paragraph pTerminoConcentrador = new Paragraph("Concentrador:", fConteudo);
				pTerminoConcentrador.setAlignment(Element.ALIGN_RIGHT);
				Paragraph pTerminoInterf = new Paragraph("Interface:", fConteudo);
				pTerminoInterf.setAlignment(Element.ALIGN_RIGHT);
				Paragraph pTerminoSwith = new Paragraph("Swith:", fConteudo);
				pTerminoSwith.setAlignment(Element.ALIGN_RIGHT);
				PdfPCell CellTermino = new PdfPCell();
				CellTermino.setPaddingBottom(5);			
				CellTermino.setBorderWidthRight(0);			
				CellTermino.setBorderWidthLeft(0);			
				CellTermino.addElement(pTerminoCont);
				//CellTermino.addElement(pTerminoRegime);
				CellTermino.addElement(pTerminoConcentrador);
				CellTermino.addElement(pTerminoInterf);
				CellTermino.addElement(pTerminoSwith);
				
				
				Paragraph pTerminoContvl = new Paragraph();	
				if(days > 0 && acesso.getCarencia().equals("SIM")){
					pTerminoContvl = new Paragraph(dias+" DIAS - COM CARÊNCIA", fConteudoBold);						
				}else{
					pTerminoContvl = new Paragraph(dias+" DIAS - SEM CARÊNCIA", fConteudoBold);											
				}
				Paragraph pTerminoRegemivl = new Paragraph(acesso.getRegime(), fConteudoBold);
				
				Paragraph pTerminoConcentradorvl = new Paragraph();
				if(acesso.getBase()!=null){
					pTerminoConcentradorvl = new Paragraph(acesso.getBase().getIdentificacao(), fConteudoBold);
				}
				Paragraph pTerminoInterfvl = new Paragraph(acesso.getInterfaces(), fConteudoBold);
				
				Paragraph pTerminoSwithvl = new Paragraph();					
				if(acesso.getSwith()!=null){
					pTerminoSwithvl = new Paragraph(acesso.getSwith().getIdentificacao(), fConteudoBold);					
				}
				PdfPCell CellTerminovl = new PdfPCell();
				CellTerminovl.setPaddingBottom(5);			
				CellTerminovl.setBorderWidthRight(0);			
				CellTerminovl.setBorderWidthLeft(0);			
				CellTerminovl.addElement(pTerminoContvl);
				//CellTerminovl.addElement();
				CellTerminovl.addElement(pTerminoConcentradorvl);
				CellTerminovl.addElement(pTerminoInterfvl);
				CellTerminovl.addElement(pTerminoSwithvl);
				
				//Nº Material
				Paragraph pMaterial = new Paragraph("Material:", fConteudo);		
				pMaterial.setAlignment(Element.ALIGN_RIGHT);
				Paragraph pMac = new Paragraph("MAC:", fConteudo);
				pMac.setAlignment(Element.ALIGN_RIGHT);

				PdfPCell CellMat = new PdfPCell();
				CellMat.setPaddingBottom(5);			
				CellMat.setBorderWidthRight(0);			
				CellMat.setBorderWidthLeft(0);			
				CellMat.addElement(pTerminoRegime);
				CellMat.addElement(pMaterial);
				CellMat.addElement(pMac);

				Paragraph pMaterialvl = new Paragraph();					
				if(acesso.getMaterial()!=null){
					pMaterialvl = new Paragraph(acesso.getMaterial().getNome(), fConteudoBold);					
				}
				
				Paragraph pMacvl = new Paragraph();					
				if(acesso.getEndereco_mac()!=null){
					pMacvl = new Paragraph(acesso.getEndereco_mac(), fConteudoBold);					
				}
				PdfPCell CellMatvl = new PdfPCell();
				CellMatvl.setPaddingBottom(5);						
				CellMatvl.setBorderWidthLeft(0);			
				CellMatvl.addElement(pTerminoRegemivl);
				CellMatvl.addElement(pMaterialvl);
				CellMatvl.addElement(pMacvl);

				PdfPTable tbcontrato = new PdfPTable(new float[] {0.30f,0.70f,0.35f,0.50f,0.16f,0.70f});
				tbcontrato.setWidthPercentage(100f);
				tbcontrato.addCell(pCellContrato);						
				tbcontrato.addCell(pCellContratovl);						
				tbcontrato.addCell(CellTermino);						
				tbcontrato.addCell(CellTerminovl);						
				tbcontrato.addCell(CellMat);						
				tbcontrato.addCell(CellMatvl);						
				doc.add(tbcontrato);	
			}else{				
				Paragraph pContrato= new Paragraph("SEM CONTRATO VINCULADO A ESTE ENDEREÇO",fSubTitulo);
				pContrato.setAlignment(Element.ALIGN_CENTER);
				PdfPCell pCellCon = new PdfPCell();
				pCellCon.addElement(pContrato);
				pCellCon.setPaddingBottom(20);
				pCellCon.setPaddingTop(15);
				PdfPTable tbcont = new PdfPTable(new float[] {1f});
				tbcont.setWidthPercentage(100f);
				tbcont.addCell(pCellCon);
//				pContrato.setSpacingAfter(10);
				doc.add(tbcont);
			}
			//CHECK
			
			//LINHA 1
			Paragraph pEqpCliente = new Paragraph("Quant/Equipamento do Cliente:\n"
					+ "____ ________________________________\n"
					+ "____ ________________________________\n"
					+ "____ ________________________________\n"
					+ "____ ________________________________\n"
					+ "____ ________________________________\n"
					+ "____ ________________________________\n",	 fCampo);
			pEqpCliente.setSpacingAfter(5);
			PdfPCell pCellEqp = new PdfPCell();
			pCellEqp.addElement(pEqpCliente);
			pCellEqp.setBorderWidthRight(0);
			pCellEqp.setBorderWidthBottom(0);			
			pCellEqp.setBorderWidthTop(0);
			pCellEqp.setBorderWidthLeft(0);
			
			
			
			Paragraph pChecklist = new Paragraph("Checklist Instalac.:", fCampo);	
			PdfPCell pCellList = new PdfPCell();			
			pCellList.setPaddingLeft(5);
			pCellList.setBorderWidthBottom(0);
			pCellList.setBorderWidthLeft(0);
			pCellList.addElement(pChecklist);
			
			
						
			//Equipamento
//			PdfPCell pCellcolum1 = new PdfPCell();
//			pCellcolum1.setPaddingBottom(0);
//			pCellcolum1.setBorderWidthRight(0);
//			pCellcolum1.setBorderWidthTop(0);
//			pCellcolum1.setBorderWidthBottom(0);
//			pCellcolum1.setBorderWidthLeft(0);
//
//			StringBuilder SbEqp = new StringBuilder();
//			
//			
//			for (int i=0; i < 8; i++) {
//				pCellcolum1.addElement(new Paragraph("____ ___________________  \n", fCampo));
//					
//			}
			
		
//			Paragraph pEqp = new Paragraph(SbEqp.toString(),fCampo);
//			PdfPCell pCellcolum2 = new PdfPCell();
//			pCellcolum2.setPadding(0);
//			pCellcolum2.setBorderWidthLeft(0);
//			pCellcolum2.setBorderWidthRight(0);
//			pCellcolum2.setBorderWidthTop(0);
//			pCellcolum2.setBorderWidthBottom(0);
//			pCellcolum2.addElement(pEqp);

			//Localização
			PdfPCell pCellcolum3 = new PdfPCell();
			pCellcolum3.setBorderWidthLeft(0);
			pCellcolum3.setBorderWidthRight(0);
			pCellcolum3.setBorderWidthTop(0);
			pCellcolum3.setBorderWidthBottom(0);
		
			StringBuilder SbLoc = new StringBuilder();
			
			List<ChecklistOsLocalizacao> checklistsLocalizacao = OseDAO.getAllCheckListLocalizacao();
			for (ChecklistOsLocalizacao check: checklistsLocalizacao) {
				//pCellcolum3.addElement(imgQuad);
				SbLoc.append("[     ]   "+check.getNome()+"\n");				
			}
			
			Paragraph pLoca = new Paragraph(SbLoc.toString(), fCampo);
			PdfPCell pCellcolum4 = new PdfPCell();
			pCellcolum4.setPadding(0);
			pCellcolum4.setBorderWidthLeft(0);
			pCellcolum4.setBorderWidthRight(0);
			pCellcolum4.setBorderWidthTop(0);
			pCellcolum4.setBorderWidthBottom(0);
			pCellcolum4.addElement(pLoca);

			//Checklist
			PdfPCell pCellcolum7_Radio = new PdfPCell();
			pCellcolum7_Radio.setBorderWidthLeft(0);
			pCellcolum7_Radio.setBorderWidthRight(0);
			pCellcolum7_Radio.setBorderWidthTop(0);
			pCellcolum7_Radio.setBorderWidthBottom(0);
			
			PdfPCell pCellcolum7_Cabo= new PdfPCell();
			pCellcolum7_Cabo.setBorderWidthLeft(0);
			pCellcolum7_Cabo.setBorderWidthRight(0);
			pCellcolum7_Cabo.setBorderWidthTop(0);
			pCellcolum7_Cabo.setBorderWidthBottom(0);
			
			PdfPCell pCellcolum7_Fibra= new PdfPCell();
			pCellcolum7_Fibra.setBorderWidthLeft(0);
			pCellcolum7_Fibra.setBorderWidthRight(0);
			pCellcolum7_Fibra.setBorderWidthTop(0);
			pCellcolum7_Fibra.setBorderWidthBottom(0);

			StringBuilder SbCheckRadio = new StringBuilder();
			StringBuilder SbCheckCabo = new StringBuilder();
			StringBuilder SbCheckFibra = new StringBuilder();
			
			List<ChecklistOsInstalacao> checklistsInstalacao = OseDAO.getAllCheckListInstalacaoRadio();
			for (ChecklistOsInstalacao check: checklistsInstalacao) {
				pCellcolum7_Radio.addElement(imgQuad);
				SbCheckRadio.append(check.getNome()+"\n");				
			}
			
			List<ChecklistOsInstalacao> checklistsInstalacaoCabo = OseDAO.getAllCheckListInstalacaoCabo();
			for (ChecklistOsInstalacao check: checklistsInstalacaoCabo) {
				pCellcolum7_Cabo.addElement(imgQuad);
				SbCheckCabo.append(check.getNome()+"\n");				
			}
			
			List<ChecklistOsInstalacao> checklistsInstalacaoFibra = OseDAO.getAllCheckListInstalacaoFibra();
			for (ChecklistOsInstalacao check: checklistsInstalacaoFibra) {
				
				SbCheckFibra.append(check.getNome()+"\n");				
			}
			
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuadVazio);
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuad);
			pCellcolum7_Fibra.addElement(imgQuadVazio);
			pCellcolum7_Fibra.addElement(imgQuad);
			
		
			Paragraph pCheck_Radio= new Paragraph(SbCheckRadio.toString(), fCampo);
			pCheck_Radio.setSpacingAfter(10);
			
			Paragraph pCheck_Cabo= new Paragraph(SbCheckCabo.toString(), fCampo);
			pCheck_Cabo.setSpacingAfter(10);
			
			Paragraph pCheck_Fibra= new Paragraph(SbCheckFibra.toString(), fCampo);
			pCheck_Fibra.setSpacingAfter(10);
			
			PdfPCell pCellcolum8_Radio = new PdfPCell();
			pCellcolum8_Radio.setPadding(0);
			pCellcolum8_Radio.setBorderWidthLeft(0);
			pCellcolum8_Radio.setBorderWidthTop(0);
			pCellcolum8_Radio.setBorderWidthRight(0);
			pCellcolum8_Radio.setBorderWidthBottom(0);
			pCellcolum8_Radio.addElement(pCheck_Radio);
			
			PdfPCell pCellcolum8_Cabo = new PdfPCell();
			pCellcolum8_Cabo.setPadding(0);
			pCellcolum8_Cabo.setBorderWidthLeft(0);
			pCellcolum8_Cabo.setBorderWidthTop(0);
			pCellcolum8_Cabo.setBorderWidthRight(0);
			pCellcolum8_Cabo.setBorderWidthBottom(0);
			pCellcolum8_Cabo.addElement(pCheck_Cabo);
			
			PdfPCell pCellcolum8_Fibra = new PdfPCell();
			pCellcolum8_Fibra.setPadding(0);
			pCellcolum8_Fibra.setBorderWidthLeft(0);
			pCellcolum8_Fibra.setBorderWidthTop(0);
			pCellcolum8_Fibra.setBorderWidthRight(0);
			pCellcolum8_Fibra.setBorderWidthBottom(0);
			pCellcolum8_Fibra.addElement(pCheck_Fibra);

			
			PdfPTable tbColuna1 = new PdfPTable(new float[] {1f});
			tbColuna1.setWidthPercentage(100f);		
			
			
			PdfPTable tbColuna1_22 = new PdfPTable(new float[] {1f});
			tbColuna1_22.setWidthPercentage(100f);
									
			PdfPTable tbColuna1_1 = new PdfPTable(new float[] {1f});
			tbColuna1_1.setWidthPercentage(100f);
			tbColuna1_1.addCell(pCellEqp);
			
			PdfPCell cell1_1 = new PdfPCell();
			cell1_1.setBorderWidthBottom(0);
			cell1_1.setBorderWidthTop(0);
			cell1_1.setBorderWidthLeft(0);
			cell1_1.setBorderWidthRight(0);
			cell1_1.addElement(tbColuna1_1);
					
			//PdfPTable tbColuna1_2 = new PdfPTable(new float[] {1f});
			//tbColuna1_2.setWidthPercentage(100f);
			//tbColuna1_2.addCell(pCellcolum1);
			//tbColuna1_2.addCell(pCellcolum2);
			
			//PdfPCell cell1_2 = new PdfPCell();
			//.setBorderWidthBottom(0);
			//cell1_2.setBorderWidthTop(0);
			//cell1_2.setBorderWidthLeft(0);
			//.setBorderWidthRight(0);
			//cell1_2.addElement(tbColuna1_2);
			
			
			
						
			PdfPTable tbColuna1_4 = new PdfPTable(new float[] {0.23f,1.3f});
			tbColuna1_4.setWidthPercentage(100f);
			tbColuna1_4.addCell(pCellcolum3);
			tbColuna1_4.addCell(pCellcolum4);
			
			PdfPCell tbRow2_cell1_Filtro_1_quad = new PdfPCell();
			tbRow2_cell1_Filtro_1_quad.setPaddingBottom(0);
			tbRow2_cell1_Filtro_1_quad.setPaddingTop(0); 
			tbRow2_cell1_Filtro_1_quad.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_1_quad.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_1_quad.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_1_quad.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_1_quad.addElement(imgQuad);
			
			PdfPCell tbRow2_cell1_Filtro_7 = new PdfPCell();
			tbRow2_cell1_Filtro_7.setPadding(0);
			tbRow2_cell1_Filtro_7.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_7.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_7.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_7.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_7.addElement(new Paragraph("SALA", fCampo));
						
			PdfPTable tbItemSala = new PdfPTable(new float[] {0.18f,1f});
			tbItemSala.setWidthPercentage(100f);										
			tbItemSala.addCell(tbRow2_cell1_Filtro_1_quad);
			tbItemSala.addCell(tbRow2_cell1_Filtro_7);
			
			PdfPCell tbRow2_cell1_Filtro_71 = new PdfPCell();
			tbRow2_cell1_Filtro_71.setPadding(0);
			tbRow2_cell1_Filtro_71.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_71.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_71.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_71.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_71.addElement(new Paragraph("QUARTO", fCampo));
						
			PdfPTable tbItemQuarto = new PdfPTable(new float[] {0.18f,1f});
			tbItemQuarto.setWidthPercentage(100f);										
			tbItemQuarto.addCell(tbRow2_cell1_Filtro_1_quad);
			tbItemQuarto.addCell(tbRow2_cell1_Filtro_71);
			
			PdfPCell tbRow2_cell1_Filtro_72 = new PdfPCell();
			tbRow2_cell1_Filtro_72.setPadding(0);
			tbRow2_cell1_Filtro_72.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_72.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_72.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_72.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_72.addElement(new Paragraph("ESCRITÓRIO", fCampo));
						
			PdfPTable tbItemEscritorio = new PdfPTable(new float[] {0.18f,1f});
			tbItemEscritorio.setWidthPercentage(100f);										
			tbItemEscritorio.addCell(tbRow2_cell1_Filtro_1_quad);
			tbItemEscritorio.addCell(tbRow2_cell1_Filtro_72);
			
			
			PdfPCell tbRow2_cell1_Filtro_73 = new PdfPCell();
			tbRow2_cell1_Filtro_73.setPadding(0);
			tbRow2_cell1_Filtro_73.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_73.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_73.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_73.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_73.addElement(new Paragraph("TERRAÇO", fCampo));
						
			PdfPTable tbItemTerraco = new PdfPTable(new float[] {0.18f,1f});
			tbItemTerraco.setWidthPercentage(100f);										
			tbItemTerraco.addCell(tbRow2_cell1_Filtro_1_quad);
			tbItemTerraco.addCell(tbRow2_cell1_Filtro_73);
			
			PdfPCell tbRow2_cell1_Filtro_74 = new PdfPCell();
			tbRow2_cell1_Filtro_74.setPadding(0);
			tbRow2_cell1_Filtro_74.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_74.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_74.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_74.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_74.addElement(new Paragraph("LOJA", fCampo));
						
			PdfPTable tbItemLoja = new PdfPTable(new float[] {0.18f,1f});
			tbItemLoja.setWidthPercentage(100f);										
			tbItemLoja.addCell(tbRow2_cell1_Filtro_1_quad);
			tbItemLoja.addCell(tbRow2_cell1_Filtro_74);
			
			PdfPCell tbRow2_cell1_Filtro_75 = new PdfPCell();
			tbRow2_cell1_Filtro_75.setPadding(0);
			tbRow2_cell1_Filtro_75.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_75.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_75.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_75.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_75.addElement(new Paragraph("CORREDOR", fCampo));
						
			PdfPTable tbItemCorredor = new PdfPTable(new float[] {0.18f,1f});
			tbItemCorredor.setWidthPercentage(100f);										
			tbItemCorredor.addCell(tbRow2_cell1_Filtro_1_quad);
			tbItemCorredor.addCell(tbRow2_cell1_Filtro_75);
			
										
			tbColuna1.addCell(cell1_1);		
			
			
			PdfPCell pCellLoc = new PdfPCell();
			pCellLoc.setPaddingLeft(3);
			pCellLoc.setBorderWidthBottom(0);
			pCellLoc.setBorderWidthRight(0);
			pCellLoc.setBorderWidthLeft(0);
			pCellLoc.setBorderWidthTop(0);			
			pCellLoc.addElement(new Paragraph("Cômodo do Roteador Principal:", fCampo));
			
			PdfPTable tbColuna1_3 = new PdfPTable(new float[] {1f});
			tbColuna1_3.setWidthPercentage(100f);
			tbColuna1_3.addCell(pCellLoc);
			
			PdfPCell cell1_3 = new PdfPCell();
			//cell1_3.setPaddingBottom(0);
			//cell1_3.setPaddingTop(0);
			cell1_3.setBorderWidthBottom(0);
			cell1_3.setBorderWidthTop(0);
			cell1_3.setBorderWidthLeft(0);
			cell1_3.setBorderWidthRight(0);
			cell1_3.addElement(tbColuna1_3);
			
			tbColuna1_22.addCell(cell1_3);
			
			PdfPCell cell1_3_Sala = new PdfPCell();
			cell1_3_Sala.setPaddingBottom(0);
			cell1_3_Sala.setPaddingTop(0);
			cell1_3_Sala.setBorderWidthBottom(0);
			cell1_3_Sala.setBorderWidthTop(0);
			cell1_3_Sala.setBorderWidthLeft(0);
			cell1_3_Sala.setBorderWidthRight(0);
			cell1_3_Sala.addElement(tbItemSala);
			
			tbColuna1_22.addCell(cell1_3_Sala);
			
			PdfPCell cell1_3_Quarto = new PdfPCell();
			cell1_3_Quarto.setPaddingBottom(0);
			cell1_3_Quarto.setPaddingTop(0);
			cell1_3_Quarto.setBorderWidthBottom(0);
			cell1_3_Quarto.setBorderWidthTop(0);
			cell1_3_Quarto.setBorderWidthLeft(0);
			cell1_3_Quarto.setBorderWidthRight(0);
			cell1_3_Quarto.addElement(tbItemQuarto);
			
			tbColuna1_22.addCell(cell1_3_Quarto);
			
			
			PdfPCell cell1_3_Escritorio = new PdfPCell();
			cell1_3_Escritorio.setPaddingBottom(0);
			cell1_3_Escritorio.setPaddingTop(0);
			cell1_3_Escritorio.setBorderWidthBottom(0);
			cell1_3_Escritorio.setBorderWidthTop(0);
			cell1_3_Escritorio.setBorderWidthLeft(0);
			cell1_3_Escritorio.setBorderWidthRight(0);
			cell1_3_Escritorio.addElement(tbItemEscritorio);
			
			tbColuna1_22.addCell(cell1_3_Escritorio);
			
			
			PdfPCell cell1_3_Terraco = new PdfPCell();
			cell1_3_Terraco.setPaddingBottom(0);
			cell1_3_Terraco.setPaddingTop(0);
			cell1_3_Terraco.setBorderWidthBottom(0);
			cell1_3_Terraco.setBorderWidthTop(0);
			cell1_3_Terraco.setBorderWidthLeft(0);
			cell1_3_Terraco.setBorderWidthRight(0);
			cell1_3_Terraco.addElement(tbItemTerraco);
			
			tbColuna1_22.addCell(cell1_3_Terraco);
			
			
			PdfPCell cell1_3_Corredor = new PdfPCell();
			cell1_3_Corredor.setPaddingBottom(0);
			cell1_3_Corredor.setPaddingTop(0);
			cell1_3_Corredor.setBorderWidthBottom(0);
			cell1_3_Corredor.setBorderWidthTop(0);
			cell1_3_Corredor.setBorderWidthLeft(0);
			cell1_3_Corredor.setBorderWidthRight(0);
			cell1_3_Corredor.addElement(tbItemCorredor);
			
			tbColuna1_22.addCell(cell1_3_Corredor);
			
			PdfPTable tbColuna2 = new PdfPTable(new float[] {1f});
			tbColuna2.setWidthPercentage(100f);
			
			
			Paragraph pMaterialUtilizado = new Paragraph(
					"Material utilizado (QUANTIDADE / DESCRIÇÃO)\n"
					+ "______  ___________________________________________________________________________________________________________\n"
					+ "______  ___________________________________________________________________________________________________________\n"
					+ "______  ___________________________________________________________________________________________________________\n"
					+ "______  ___________________________________________________________________________________________________________\n"
					+ "______  ___________________________________________________________________________________________________________\n"
					+ "______  ___________________________________________________________________________________________________________", fCampo);
			pMaterialUtilizado.setSpacingAfter(5);
			
			PdfPCell pCellMaterialUtilizado = new PdfPCell();
			pCellMaterialUtilizado.addElement(pMaterialUtilizado);
			pCellMaterialUtilizado.setBorderWidthRight(0);
			pCellMaterialUtilizado.setBorderWidthBottom(0);			
			pCellMaterialUtilizado.setBorderWidthTop(0);
			pCellMaterialUtilizado.setBorderWidthLeft(0);
			
			PdfPTable tbColuna2_1 = new PdfPTable(new float[] {1f});
			tbColuna2_1.setWidthPercentage(100f);
			tbColuna2_1.addCell(pCellMaterialUtilizado);
			
			PdfPCell cell2_1 = new PdfPCell();
			cell2_1.setBorderWidthBottom(0);
			cell2_1.setBorderWidthTop(0);
			cell2_1.setBorderWidthLeft(0);
			cell2_1.setBorderWidthRight(0);
			cell2_1.addElement(tbColuna2_1);
			
			Paragraph pMaterialUtilizadoLinhas = new Paragraph(
					"______  ___________________________________________________________________________________________________________"
					+ "\n\n______  ___________________________________________________________________________________________________________"
					+ "\n\n______  ___________________________________________________________________________________________________________"
					+ "\n\n______  ___________________________________________________________________________________________________________", fCampo);
			pMaterialUtilizadoLinhas.setSpacingAfter(5);
			
			PdfPCell pCellMaterialUtilizadoLinhas = new PdfPCell();
			pCellMaterialUtilizadoLinhas.addElement(pMaterialUtilizadoLinhas);
			pCellMaterialUtilizadoLinhas.setBorderWidthRight(0);
			pCellMaterialUtilizadoLinhas.setBorderWidthBottom(0);			
			pCellMaterialUtilizadoLinhas.setBorderWidthTop(0);
			pCellMaterialUtilizadoLinhas.setBorderWidthLeft(0);
			
			PdfPTable tbColuna2_2 = new PdfPTable(new float[] {1f});
			tbColuna2_2.setWidthPercentage(100f);
			tbColuna2_2.addCell(pCellMaterialUtilizadoLinhas);
			
			PdfPCell cell2_2 = new PdfPCell();
			cell2_2.setBorderWidthBottom(0);
			cell2_2.setBorderWidthTop(0);
			cell2_2.setBorderWidthLeft(0);
			cell2_2.setBorderWidthRight(0);
			cell2_2.addElement(tbColuna2_2);
			
			tbColuna2.addCell(cell2_1);
			//tbColuna2.addCell(cell2_2);
			
			
			PdfPTable tbCheckListInstalacao = new PdfPTable(new float[] {1f});
			tbCheckListInstalacao.setWidthPercentage(100f);	
			
			PdfPCell cell2_coluna1 = new PdfPCell();
			cell2_coluna1.setBorderWidthBottom(0);
			cell2_coluna1.setBorderWidthTop(0);
			cell2_coluna1.setBorderWidthLeft(0);
			cell2_coluna1.setBorderWidthRight(0);
			cell2_coluna1.addElement(tbColuna1);
			
			PdfPCell cell2_222222 = new PdfPCell();
			cell2_222222.setBorderWidthBottom(0);
			cell2_222222.setBorderWidthTop(0);
			cell2_222222.setBorderWidthLeft(0);
			cell2_222222.setBorderWidthRight(0);
			cell2_222222.addElement(tbColuna1_22);
			
			PdfPCell cell2_2222223 = new PdfPCell();
			cell2_2222223.setBorderWidthBottom(0);
			cell2_2222223.setBorderWidthTop(0);
			cell2_2222223.setBorderWidthLeft(0);
			cell2_2222223.setBorderWidthRight(0);
			cell2_2222223.addElement(tbColuna2);
			
			
			PdfPTable tbCheck22222= new PdfPTable(new float[] {0.35f,0.25f,1f});
			tbCheck22222.setWidthPercentage(100f);
			tbCheck22222.addCell(cell2_coluna1);
			tbCheck22222.addCell(cell2_222222);
			tbCheck22222.addCell(cell2_2222223);
			
			PdfPTable tbCheck1 = new PdfPTable(new float[] {1f});
			tbCheck1.setWidthPercentage(100f);										
			tbCheck1.addCell(tbCheck22222);
			
			
			Paragraph tbRow2_cell1_paragrafo1 = new Paragraph(
					"Equipamentos conectados a energia por meio de: ", fCampo);			
											
			Paragraph tbRow2_cell1_paragrafo2 = new Paragraph("Fixação do Roteador:", fCampo);
			
			
			
			
						
			PdfPCell tbRow2_cell1 = new PdfPCell();
			tbRow2_cell1.setBorderWidthBottom(0);
			tbRow2_cell1.setBorderWidthTop(0);
			tbRow2_cell1.setBorderWidthLeft(0.5f);
			tbRow2_cell1.setBorderWidthRight(0.5f);
			tbRow2_cell1.addElement(tbRow2_cell1_paragrafo1);
			
			PdfPCell cellCaixaVazio = new PdfPCell();
			cellCaixaVazio.setBorderWidthBottom(0.5f);
			cellCaixaVazio.setBorderWidthTop(0.5f);
			cellCaixaVazio.setBorderWidthLeft(0.5f);
			cellCaixaVazio.setBorderWidthRight(0.5f);
			
			PdfPCell tbRow2_cell1_Filtro = new PdfPCell();
			tbRow2_cell1_Filtro.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro.setBorderWidthTop(0);
			tbRow2_cell1_Filtro.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro.setBorderWidthRight(0);
			
			
			
			
			PdfPCell tbRow2_cell1_Filtro_1 = new PdfPCell();
			tbRow2_cell1_Filtro_1.setPadding(0);
			tbRow2_cell1_Filtro_1.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_1.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_1.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_1.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_1.addElement(new Paragraph("Filtro de linha", fCampo));
						
			PdfPTable tbFiltro_1 = new PdfPTable(new float[] {0.18f,1f});
			tbFiltro_1.setWidthPercentage(100f);										
			tbFiltro_1.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFiltro_1.addCell(tbRow2_cell1_Filtro_1);
			
			tbRow2_cell1_Filtro.addElement(tbFiltro_1);		
			
			PdfPCell tbRow2_cell1_Beijamin = new PdfPCell();
			tbRow2_cell1_Beijamin.setBorderWidthBottom(0);
			tbRow2_cell1_Beijamin.setBorderWidthTop(0);
			tbRow2_cell1_Beijamin.setBorderWidthLeft(0);
			tbRow2_cell1_Beijamin.setBorderWidthRight(0);
						
			PdfPCell tbRow2_cell1_Filtro_2 = new PdfPCell();
			tbRow2_cell1_Filtro_2.setPadding(0);
			tbRow2_cell1_Filtro_2.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_2.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_2.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_2.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_2.addElement(new Paragraph("Beijamin", fCampo));
						
			PdfPTable tbFiltro_2 = new PdfPTable(new float[] {0.18f,1f});
			tbFiltro_2.setWidthPercentage(100f);										
			tbFiltro_2.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFiltro_2.addCell(tbRow2_cell1_Filtro_2);
			
			tbRow2_cell1_Beijamin.addElement(tbFiltro_2);
			
			PdfPCell tbRow2_cell1_Extensao = new PdfPCell();
			tbRow2_cell1_Extensao.setBorderWidthBottom(0);
			tbRow2_cell1_Extensao.setBorderWidthTop(0);
			tbRow2_cell1_Extensao.setBorderWidthLeft(0);
			tbRow2_cell1_Extensao.setBorderWidthRight(0);
			
			PdfPCell tbRow2_cell1_Filtro_3 = new PdfPCell();
			tbRow2_cell1_Filtro_3.setPadding(0);
			tbRow2_cell1_Filtro_3.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_3.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_3.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_3.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_3.addElement(new Paragraph("Extensão", fCampo));
						
			PdfPTable tbFiltro_3 = new PdfPTable(new float[] {0.18f,1f});
			tbFiltro_3.setWidthPercentage(100f);										
			tbFiltro_3.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFiltro_3.addCell(tbRow2_cell1_Filtro_3);
						
			tbRow2_cell1_Extensao.addElement(tbFiltro_3);
			
			PdfPCell tbRow2_cell1_Estabilizador = new PdfPCell();
			tbRow2_cell1_Estabilizador.setBorderWidthBottom(0);
			tbRow2_cell1_Estabilizador.setBorderWidthTop(0);
			tbRow2_cell1_Estabilizador.setBorderWidthLeft(0);
			tbRow2_cell1_Estabilizador.setBorderWidthRight(0);
			
			PdfPCell tbRow2_cell1_Filtro_4 = new PdfPCell();
			tbRow2_cell1_Filtro_4.setPadding(0);
			tbRow2_cell1_Filtro_4.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_4.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_4.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_4.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_4.addElement(new Paragraph("Estabilizador/nobreak", fCampo));
						
			PdfPTable tbFiltro_4 = new PdfPTable(new float[] {0.18f,1f});
			tbFiltro_4.setWidthPercentage(100f);										
			tbFiltro_4.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFiltro_4.addCell(tbRow2_cell1_Filtro_4);
			
			tbRow2_cell1_Estabilizador.addElement(tbFiltro_4);
			
			PdfPCell tbRow2_cell1_Tomada_dupla = new PdfPCell();
			tbRow2_cell1_Tomada_dupla.setBorderWidthBottom(0);
			tbRow2_cell1_Tomada_dupla.setBorderWidthTop(0);
			tbRow2_cell1_Tomada_dupla.setBorderWidthLeft(0);
			tbRow2_cell1_Tomada_dupla.setBorderWidthRight(0);
			
			PdfPCell tbRow2_cell1_Filtro_5 = new PdfPCell();
			tbRow2_cell1_Filtro_5.setPadding(0);
			tbRow2_cell1_Filtro_5.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_5.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_5.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_5.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_5.addElement(new Paragraph("Tomada Dupla", fCampo));
						
			PdfPTable tbFiltro_5 = new PdfPTable(new float[] {0.18f,1f});
			tbFiltro_5.setWidthPercentage(100f);										
			tbFiltro_5.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFiltro_5.addCell(tbRow2_cell1_Filtro_5);
						
			tbRow2_cell1_Tomada_dupla.addElement(tbFiltro_5);
			
			PdfPCell tbRow2_cell1_Tomada_separadas = new PdfPCell();
			tbRow2_cell1_Tomada_separadas.setBorderWidthBottom(0);
			tbRow2_cell1_Tomada_separadas.setBorderWidthTop(0);
			tbRow2_cell1_Tomada_separadas.setBorderWidthLeft(0);
			tbRow2_cell1_Tomada_separadas.setBorderWidthRight(0);
			
			PdfPCell tbRow2_cell1_Filtro_6 = new PdfPCell();
			tbRow2_cell1_Filtro_6.setPadding(0);
			tbRow2_cell1_Filtro_6.setBorderWidthBottom(0);
			tbRow2_cell1_Filtro_6.setBorderWidthTop(0);
			tbRow2_cell1_Filtro_6.setBorderWidthLeft(0);
			tbRow2_cell1_Filtro_6.setBorderWidthRight(0);
			tbRow2_cell1_Filtro_6.addElement(new Paragraph("Tomadas separadas", fCampo));
						
			PdfPTable tbFiltro_6 = new PdfPTable(new float[] {0.18f,1f});
			tbFiltro_6.setWidthPercentage(100f);										
			tbFiltro_6.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFiltro_6.addCell(tbRow2_cell1_Filtro_6);
			
			tbRow2_cell1_Tomada_separadas.addElement(tbFiltro_6);
			
			PdfPTable tbRow_equipamentos_conect = new PdfPTable(new float[] {1f,1f,1f,1f,1f,1f});
			tbRow_equipamentos_conect.setWidthPercentage(100f);										
			tbRow_equipamentos_conect.addCell(tbRow2_cell1_Filtro);
			tbRow_equipamentos_conect.addCell(tbRow2_cell1_Beijamin);
			tbRow_equipamentos_conect.addCell(tbRow2_cell1_Extensao);
			tbRow_equipamentos_conect.addCell(tbRow2_cell1_Estabilizador);
			tbRow_equipamentos_conect.addCell(tbRow2_cell1_Tomada_dupla);
			tbRow_equipamentos_conect.addCell(tbRow2_cell1_Tomada_separadas);
			
			tbRow2_cell1.addElement(tbRow_equipamentos_conect);
		
			PdfPCell tbRow2_1_cell_1 = new PdfPCell();
			
			tbRow2_1_cell_1.setBorderWidthBottom(0);
			tbRow2_1_cell_1.setBorderWidthTop(0);
			tbRow2_1_cell_1.setBorderWidthLeft(0.5f);
			tbRow2_1_cell_1.setBorderWidthRight(0);
			tbRow2_1_cell_1.addElement(tbRow2_cell1_paragrafo2);
			
			
			PdfPCell tbRow2_tbFixacao_1_1 = new PdfPCell();
			tbRow2_tbFixacao_1_1.setPadding(0);
			tbRow2_tbFixacao_1_1.setBorderWidthBottom(0);
			tbRow2_tbFixacao_1_1.setBorderWidthTop(0);
			tbRow2_tbFixacao_1_1.setBorderWidthLeft(0);
			tbRow2_tbFixacao_1_1.setBorderWidthRight(0);
			tbRow2_tbFixacao_1_1.addElement(new Paragraph("Parede", fCampo));
						
			PdfPTable tbFixacao_1 = new PdfPTable(new float[] {0.08f,1f});
			tbFixacao_1.setWidthPercentage(100f);										
			tbFixacao_1.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFixacao_1.addCell(tbRow2_tbFixacao_1_1);
			
			
			PdfPCell tbRow2_tbFixacao_1_2 = new PdfPCell();
			tbRow2_tbFixacao_1_2.setPadding(0);
			tbRow2_tbFixacao_1_2.setBorderWidthBottom(0);
			tbRow2_tbFixacao_1_2.setBorderWidthTop(0);
			tbRow2_tbFixacao_1_2.setBorderWidthLeft(0);
			tbRow2_tbFixacao_1_2.setBorderWidthRight(0);
			tbRow2_tbFixacao_1_2.addElement(new Paragraph("Em cima de Móvel", fCampo));
						
			PdfPTable tbFixacao_2 = new PdfPTable(new float[] {0.08f,1f});
			tbFixacao_2.setWidthPercentage(100f);										
			tbFixacao_2.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFixacao_2.addCell(tbRow2_tbFixacao_1_2);
			
			tbRow2_1_cell_1.addElement(tbFixacao_1);
			tbRow2_1_cell_1.addElement(tbFixacao_2);
			//tbRow2_1_cell_1.addElement(tbRow2_cell1_paragrafo2);
			
			
			PdfPCell tbRow2_1_cell_2 = new PdfPCell();
			tbRow2_1_cell_2.setBorderWidthBottom(0);
			tbRow2_1_cell_2.setBorderWidthTop(0);
			tbRow2_1_cell_2.setBorderWidthLeft(0);
			tbRow2_1_cell_2.setBorderWidthRight(0);
			tbRow2_1_cell_2.addElement(new Paragraph("Roteador do Cliente adequado a sua necessidade ?", fCampo));
			
			
			PdfPCell tbRow2_tbFixacao_1_1_1 = new PdfPCell();
			tbRow2_tbFixacao_1_1_1.setPadding(0);
			tbRow2_tbFixacao_1_1_1.setBorderWidthBottom(0);
			tbRow2_tbFixacao_1_1_1.setBorderWidthTop(0);
			tbRow2_tbFixacao_1_1_1.setBorderWidthLeft(0);
			tbRow2_tbFixacao_1_1_1.setBorderWidthRight(0);
			tbRow2_tbFixacao_1_1_1.addElement(new Paragraph("SIM", fCampo));
						
			PdfPTable tbFixacao_1_1 = new PdfPTable(new float[] {0.08f,1f});
			tbFixacao_1_1.setWidthPercentage(100f);										
			tbFixacao_1_1.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFixacao_1_1.addCell(tbRow2_tbFixacao_1_1_1);
			
			PdfPCell tbRow2_tbFixacao_1_1_1_1 = new PdfPCell();
			tbRow2_tbFixacao_1_1_1_1.setPadding(0);
			tbRow2_tbFixacao_1_1_1_1.setBorderWidthBottom(0);
			tbRow2_tbFixacao_1_1_1_1.setBorderWidthTop(0);
			tbRow2_tbFixacao_1_1_1_1.setBorderWidthLeft(0);
			tbRow2_tbFixacao_1_1_1_1.setBorderWidthRight(0);
			tbRow2_tbFixacao_1_1_1_1.addElement(new Paragraph("NÃO", fCampo));
						
			PdfPTable tbFixacao_1_1_1 = new PdfPTable(new float[] {0.08f,1f});
			tbFixacao_1_1_1.setWidthPercentage(100f);										
			tbFixacao_1_1_1.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFixacao_1_1_1.addCell(tbRow2_tbFixacao_1_1_1_1);
			
			tbRow2_1_cell_2.addElement(tbFixacao_1_1);
			tbRow2_1_cell_2.addElement(tbFixacao_1_1_1);
			
			PdfPCell tbRow2_1_cell_3 = new PdfPCell();
			tbRow2_1_cell_3.setBorderWidthBottom(0);
			tbRow2_1_cell_3.setBorderWidthTop(0);
			tbRow2_1_cell_3.setBorderWidthLeft(0);
			tbRow2_1_cell_3.setBorderWidthRight(0.5f);
			tbRow2_1_cell_3.addElement(new Paragraph("Necessidade de Roteador Secundário ?", fCampo));
			
			PdfPCell tbRow2_tbFixacao_1_1_1_1_1 = new PdfPCell();
			tbRow2_tbFixacao_1_1_1_1_1.setPadding(0);
			tbRow2_tbFixacao_1_1_1_1_1.setBorderWidthBottom(0);
			tbRow2_tbFixacao_1_1_1_1_1.setBorderWidthTop(0);
			tbRow2_tbFixacao_1_1_1_1_1.setBorderWidthLeft(0);
			tbRow2_tbFixacao_1_1_1_1_1.setBorderWidthRight(0);
			tbRow2_tbFixacao_1_1_1_1_1.addElement(new Paragraph("Quantos ? ___________", fCampo));
						
			PdfPTable tbFixacao_1_1_1_1 = new PdfPTable(new float[] {1f});
			tbFixacao_1_1_1_1.setWidthPercentage(100f);										
			//tbFixacao_1_1_1_1.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFixacao_1_1_1_1.addCell(tbRow2_tbFixacao_1_1_1_1_1);
//			
			PdfPCell tbRow2_tbFixacao_1_1_1_1_1_1 = new PdfPCell();
			tbRow2_tbFixacao_1_1_1_1_1_1.setPadding(0);
			tbRow2_tbFixacao_1_1_1_1_1_1.setBorderWidthBottom(0);
			tbRow2_tbFixacao_1_1_1_1_1_1.setBorderWidthTop(0);
			tbRow2_tbFixacao_1_1_1_1_1_1.setBorderWidthLeft(0);
			tbRow2_tbFixacao_1_1_1_1_1_1.setBorderWidthRight(0);
			tbRow2_tbFixacao_1_1_1_1_1_1.addElement(new Paragraph("Não Necessita", fCampo));
						
			PdfPTable tbFixacao_1_1_1_1_1 = new PdfPTable(new float[] {0.08f,1f});
			tbFixacao_1_1_1_1_1.setWidthPercentage(100f);										
			tbFixacao_1_1_1_1_1.addCell(tbRow2_cell1_Filtro_1_quad);
			tbFixacao_1_1_1_1_1.addCell(tbRow2_tbFixacao_1_1_1_1_1_1);
			
			tbRow2_1_cell_3.addElement(tbFixacao_1_1_1_1);
			tbRow2_1_cell_3.addElement(tbFixacao_1_1_1_1_1);
			
			
			PdfPTable tbRow2_1 = new PdfPTable(new float[] {0.30f,0.30f,0.30f});
			tbRow2_1.setWidthPercentage(100f);			
			tbRow2_1.setSpacingAfter(0);
			tbRow2_1.setSpacingBefore(0);
						
			tbRow2_1.addCell(tbRow2_1_cell_1);
			tbRow2_1.addCell(tbRow2_1_cell_2);
			tbRow2_1.addCell(tbRow2_1_cell_3);
					
			PdfPTable tbRow2 = new PdfPTable(new float[] {1f});
			tbRow2.setWidthPercentage(100f);										
			tbRow2.addCell(tbRow2_cell1);
			
			doc.add(tbRow2);
			doc.add(tbRow2_1);	
			
			doc.add(tbCheck1);	
			
			
					
			Paragraph pCheckListInstalacao = new Paragraph("CHECKLIST INSTALAÇÃO - RÁDIO", fCampo);
			pCheckListInstalacao.setSpacingAfter(5);
			
			Paragraph pCheckListInstalacaoCabo = new Paragraph("CHECKLIST INSTALAÇÃO - CABO", fCampo);
			pCheckListInstalacaoCabo.setSpacingAfter(5);
			
			Paragraph pCheckListInstalacaoFibra = new Paragraph("CHECKLIST INSTALAÇÃO - FIBRA", fCampo);
			pCheckListInstalacaoFibra.setSpacingAfter(5);
			
			PdfPTable tbListaCheckInstalacaoRadio = new PdfPTable(new float[] {0.10f,1.3f});
			tbListaCheckInstalacaoRadio.setWidthPercentage(100f);
			tbListaCheckInstalacaoRadio.addCell(pCellcolum7_Radio);
			tbListaCheckInstalacaoRadio.addCell(pCellcolum8_Radio);
			
			PdfPTable tbListaCheckInstalacaoCabo = new PdfPTable(new float[] {0.10f,1.3f});
			tbListaCheckInstalacaoCabo.setWidthPercentage(100f);
			tbListaCheckInstalacaoCabo.addCell(pCellcolum7_Cabo);
			tbListaCheckInstalacaoCabo.addCell(pCellcolum8_Cabo);
			
			PdfPTable tbListaCheckInstalacaoFibra = new PdfPTable(new float[] {0.10f,1.3f});
			tbListaCheckInstalacaoFibra.setWidthPercentage(100f);
			tbListaCheckInstalacaoFibra.addCell(pCellcolum7_Fibra);
			tbListaCheckInstalacaoFibra.addCell(pCellcolum8_Fibra);
			
			PdfPCell pCheckListInstalacaoInterno = new PdfPCell();
			pCheckListInstalacaoInterno.setBorderWidthBottom(0);
			pCheckListInstalacaoInterno.setBorderWidthTop(0);
			pCheckListInstalacaoInterno.setBorderWidthLeft(0);
			pCheckListInstalacaoInterno.setBorderWidthRight(0);
			pCheckListInstalacaoInterno.addElement(pCheckListInstalacao);
			pCheckListInstalacaoInterno.addElement(tbListaCheckInstalacaoRadio);
			
			PdfPCell pCheckListInstalacaoInternoCabo = new PdfPCell();
			pCheckListInstalacaoInternoCabo.setBorderWidthBottom(0);
			pCheckListInstalacaoInternoCabo.setBorderWidthTop(0);
			pCheckListInstalacaoInternoCabo.setBorderWidthLeft(0);
			pCheckListInstalacaoInternoCabo.setBorderWidthRight(0);
			pCheckListInstalacaoInternoCabo.addElement(pCheckListInstalacaoCabo);
			pCheckListInstalacaoInternoCabo.addElement(tbListaCheckInstalacaoCabo);
			
			PdfPCell pCheckListInstalacaoInternoFibra = new PdfPCell();
			pCheckListInstalacaoInternoFibra.setBorderWidthBottom(0);
			pCheckListInstalacaoInternoFibra.setBorderWidthTop(0);
			pCheckListInstalacaoInternoFibra.setBorderWidthLeft(0);
			pCheckListInstalacaoInternoFibra.setBorderWidthRight(0);
			pCheckListInstalacaoInternoFibra.addElement(pCheckListInstalacaoFibra);
			pCheckListInstalacaoInternoFibra.addElement(tbListaCheckInstalacaoFibra);
			
			PdfPTable tbColuna333 = new PdfPTable(new float[] {1f,1f,1f});
			tbColuna333.setWidthPercentage(100f);
			
			tbColuna333.addCell(pCheckListInstalacaoInterno);
			tbColuna333.addCell(pCheckListInstalacaoInternoCabo);
			tbColuna333.addCell(pCheckListInstalacaoInternoFibra);
									
			PdfPCell cell3_111 = new PdfPCell();
			cell3_111.setBorderWidthBottom(0);
			cell3_111.setBorderWidthTop(0);
			cell3_111.setBorderWidthLeft(0);
			cell3_111.setBorderWidthRight(0);
			cell3_111.addElement(tbColuna333);
			
			tbCheckListInstalacao.addCell(cell3_111);
			
			PdfPTable tbCheck2 = new PdfPTable(new float[] {1f});
			tbCheck2.setWidthPercentage(100f);
			tbCheck2.setSpacingAfter(0);
			tbCheck2.setSpacingBefore(0);
			tbCheck2.addCell(tbCheckListInstalacao);
					
			doc.add(tbCheck2);
			
			
			PdfPCell pCellSolicit= new PdfPCell(new Paragraph("Solicito o cancelamento do Servico conforme numero de Contrato acima, comprometendo-me a devolver o material,"+
			" caso regime de comodato, e quitar os boletos em aberto ate o presente mes:"+"\n"+"\n"+"\n"+"______________________________________________________________________________________________________________________________________________________________________________________________"+"\n", fCampo));
			pCellSolicit.setPaddingBottom(5);
			
			PdfPTable tbSolicit = new PdfPTable(new float[] {1f});
			tbSolicit.setWidthPercentage(100f);
			tbSolicit.addCell(pCellSolicit);		
			//tbSolicit.
			doc.add(tbSolicit);	
			
			Paragraph pAutoriRet = new Paragraph("Autorizo a retirada do Equipamento acima: ______________________________________________________________________________________________", fCampo);		
			pAutoriRet.setSpacingAfter(5);
			PdfPCell pCellAutoriRet = new PdfPCell();
			pCellAutoriRet.addElement(pAutoriRet);
			pCellAutoriRet.setPaddingTop(5);
			
			Paragraph pEquipamentoRecebidoPor = new Paragraph("Equipamento recebido por: ______________________________________________________________________________________________", fCampo);		
			pEquipamentoRecebidoPor.setSpacingAfter(5);
			PdfPCell pCellpEquipamentoRecebidoPor = new PdfPCell();
			pCellpEquipamentoRecebidoPor.addElement(pEquipamentoRecebidoPor);
			pCellpEquipamentoRecebidoPor.setPaddingTop(5);
			
			PdfPTable tbAutoriRet = new PdfPTable(new float[] {1f,1f});
			tbAutoriRet.setWidthPercentage(100f);
			tbAutoriRet.addCell(pCellAutoriRet);
			tbAutoriRet.addCell(pCellpEquipamentoRecebidoPor);
			tbAutoriRet.setSpacingAfter(5);
			doc.add(tbAutoriRet);	
			
			
			//Valor PGTO
			Paragraph pServic = new Paragraph("VALOR DO SERVICO SOLICITADO", fConteudo);
			PdfPCell CellServic = new PdfPCell();
			CellServic.setPadding(0);
			CellServic.setBorderWidth(0);
			CellServic.addElement(pServic);
			
			Paragraph pServicVl = new Paragraph("R$: "+ValorPgt+" - GRÁTIS MEDIANTE BENEFÍCIO", fConteudoBold);
			pServicVl.setSpacingAfter(5);
			PdfPCell CellServicVl = new PdfPCell();
			CellServicVl.setPadding(0);
			CellServicVl.setBorderWidth(0);
			CellServicVl.addElement(pServicVl);
			
			//Vencimento
			Paragraph pVenc = new Paragraph("Vencimento:", fConteudo);
			PdfPCell CellVenc = new PdfPCell();
			CellVenc.setPadding(0);
			CellVenc.setBorderWidth(0);
			CellVenc.addElement(pVenc);
			
			Paragraph pVencVl = new Paragraph();
			if(Vencimento != null){
			  pVencVl = new Paragraph(dUtil.parseDataBra(Vencimento).toString(), fConteudoBold);				
			}else{
				pVencVl = new Paragraph("", fConteudoBold);								
			}
			
			PdfPCell CellVencVl = new PdfPCell();
			CellVencVl.setPadding(0);
			CellVencVl.setBorderWidth(0);
			CellVencVl.addElement(pVencVl);
			
			//Forma de PGTO
			Paragraph pForPgt = new Paragraph("Forma de Pagamento:", fConteudo);
			PdfPCell CellPgt = new PdfPCell();
			CellPgt.setPadding(0);
			CellPgt.setBorderWidth(0);
			CellPgt.addElement(pForPgt);
			
			Paragraph pForPgtVl = new Paragraph(formPgt, fConteudoBold);
			PdfPCell CellPgtVl = new PdfPCell();
			CellPgtVl.setPadding(0);
			CellPgtVl.setBorderWidth(0);
			CellPgtVl.addElement(pForPgtVl);
			
			PdfPTable tbPgtVl = new PdfPTable(new float[] {0.65f,0.90f,0.25f,0.30f,0.40f,0.30f});
			tbPgtVl.setWidthPercentage(100f);
			tbPgtVl.setSpacingAfter(5);
			tbPgtVl.addCell(CellServic);
			tbPgtVl.addCell(CellServicVl);
			tbPgtVl.addCell(CellVenc);
			tbPgtVl.addCell(CellVencVl);
			tbPgtVl.addCell(CellPgt);
			tbPgtVl.addCell(CellPgtVl);

			Paragraph pAutoVl = new Paragraph("Autorizo a Execução do Servico conforme condições acima: ____________________________________________________________________________________________________________________________________________", fCampo);
			pAutoVl.setSpacingAfter(3);
			
			PdfPCell pCellVlServic= new PdfPCell();
//			pCellVlServic.addElement(tbServicVl);
//			pCellVlServic.addElement(tbVenc);
			pCellVlServic.addElement(tbPgtVl);
			pCellVlServic.addElement(pAutoVl);
			
			PdfPTable tbVlServic = new PdfPTable(new float[] {1f});
			tbVlServic.setWidthPercentage(100f);
			tbVlServic.addCell(pCellVlServic);
			doc.add(tbVlServic);	
			
			//OBS
			Paragraph pObs = new Paragraph("OBSERVAÇÕES:", fCampo);					
			Paragraph pObsVl = new Paragraph(ose.getObs(), fCaptionsBold);	
			//pObsVl.setSpacingAfter(15);
			
			PdfPCell pCellObs= new PdfPCell();
			pCellObs.addElement(pObs);
			pCellObs.addElement(pObsVl);
			pCellObs.setPaddingBottom(1);
			
			PdfPTable tbObs = new PdfPTable(new float[] {1f});
			tbObs.setWidthPercentage(100f);
			tbObs.addCell(pCellObs);
			doc.add(tbObs);	
			
			//Conclusão
			Paragraph pConclusao = new Paragraph("CONCLUSÃO:", fCampo);								
			PdfPCell pCellConclusao= new PdfPCell();
			pCellConclusao.setBorderWidthBottom(0);
			pCellConclusao.addElement(pConclusao);

			PdfPTable tbConclusao = new PdfPTable(new float[] {1f});
			tbConclusao.setWidthPercentage(100f);
			tbConclusao.addCell(pCellConclusao);
			
			doc.add(tbConclusao);	
			
			Paragraph pConclusaoVl = new Paragraph();												
			if(ose.getConclusao()!= null && !ose.getConclusao().equals(" ")){
				pConclusaoVl = new Paragraph(ose.getConclusao(), fCampoBold);												
			}else{
				pConclusaoVl = new Paragraph(" ", fCampoBold);																
			}
			PdfPCell pCellConclusaoVl= new PdfPCell();
			pCellConclusaoVl.setBorderWidthTop(0);
			pCellConclusaoVl.setPaddingBottom(30);
			pCellConclusaoVl.addElement(pConclusaoVl);
			
			PdfPTable tbConclusaoVl = new PdfPTable(new float[] {1f});
			tbConclusaoVl.setWidthPercentage(100f);
			tbConclusaoVl.setSpacingAfter(5);
			tbConclusaoVl.addCell(pCellConclusaoVl);
			doc.add(tbConclusaoVl);	
			
			
			
			//Importante
			Paragraph pImportante = new Paragraph("IMPORTANTE: LEIA ATENTAMENTE ANTES DE ASSINAR", fCampo);
			pImportante.setAlignment(Element.ALIGN_CENTER);
			pImportante.setSpacingBefore(3);
			pImportante.setSpacingAfter(3);
			
			PdfPCell pCellImportante= new PdfPCell();	
			pCellImportante.addElement(pImportante);
			pCellImportante.setBackgroundColor(new BaseColor(216,216,216));
			
			PdfPTable tbImportante = new PdfPTable(new float[] {1f});
			tbImportante.setWidthPercentage(100f);
			tbImportante.addCell(pCellImportante);
			doc.add(tbImportante);	
			
			//Clausulas 
			Paragraph pCase = new Paragraph("Ao assinar este documento, declaro estar ciente:", fCaptionsBold);
			PdfPCell pCellpCase= new PdfPCell();	
			pCellpCase.addElement(pCase);
			pCellpCase.setPaddingLeft(4);
			pCellpCase.setPaddingBottom(5);
			pCellpCase.setBorderWidthBottom(0);	
			pCellpCase.setBorderWidthTop(0);	
			
			PdfPTable tbpCase = new PdfPTable(new float[] {1f});
			tbpCase.setWidthPercentage(100f);
			tbpCase.addCell(pCellpCase);
			doc.add(tbpCase);
			
//			Paragraph pAt = new Paragraph("Atenção", fCampo);
//			PdfPCell pCellAt= new PdfPCell();	
//			pCellAt.addElement(pAt);
//			pCellAt.setPaddingLeft(4);
//			pCellAt.setPaddingRight(0);
//			pCellAt.setPaddingTop(0);
//			pCellAt.setPaddingBottom(0);
//			pCellAt.setBorderWidthBottom(0);	
//			pCellAt.setBorderWidthTop(0);	
//			
//			PdfPTable tbAt = new PdfPTable(new float[] {1f});
//			tbAt.setWidthPercentage(100f);
//			tbAt.addCell(pCellAt);
//			doc.add(tbAt);
			
			StringBuilder sbNumero = new StringBuilder();
			sbNumero.append(" 1."+"\n"+" 2."+"\n"+"\n"+" 3."+"\n"+" 4.");
			Paragraph pCaseN = new Paragraph(sbNumero.toString(), fCampoBold);
			
			StringBuilder sbVl = new StringBuilder();
			sbVl.append("Que o SERVICO contratado sera prestado com base nos termos e condições de CONTRATO DE PRESTAÇÃO DE SERVCOS da DIGITAL, contrato este que recebi copia e li;"+"\n"+
			"Que a desconexão do servico não me isenta da quitação de debitos pendentes relativos aos serviços prestados até a data de solicitação nem ao pagamento em virtude de quebra "+
			"de fidelidade, ficando a DIGITAL, autorizada nesses casos a cobrar o valor corrigido do beneficio utilizado;"+"\n"+
			"Que as visitas técnicas serão cobradas quando as condições não estiverem inclusas no pacote de Serviços Contratado;"+"\n"+
			"Que os equipamentos instalados e cedidos em modelo de comodato estão sob minha responsabilidade, devendo devolve-los no momentos de recisão em condições perfeitas. "+
			"Caso seja detectado pelos técnicos avarias ou aulterações, havera cobrança a titulo de reposição dos equipamentos.");
			
			Paragraph pCaseVl = new Paragraph(sbVl.toString(),fCampo);
			pCaseVl.setSpacingAfter(3);
			
			PdfPCell pCellpCaseN= new PdfPCell();	
			pCellpCaseN.addElement(pCaseN);
			pCellpCaseN.setPadding(0);
			pCellpCaseN.setBorderWidthTop(0);
			pCellpCaseN.setBorderWidthRight(0);
			
			PdfPCell pCellpCaseVl= new PdfPCell();	
			pCellpCaseVl.addElement(pCaseVl);
			pCellpCaseVl.setPadding(0);
			pCellpCaseVl.setBorderWidthTop(0);
			pCellpCaseVl.setBorderWidthLeft(0);
				
			PdfPTable tbpCase1 = new PdfPTable(new float[] {0.1f,7f});
			tbpCase1.setWidthPercentage(100f);
			tbpCase1.addCell(pCellpCaseN);
			tbpCase1.addCell(pCellpCaseVl);
//			tbpCase.setSpacingAfter(3);
			doc.add(tbpCase1);	
			
			//Info
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append(" Técnico ensinou a conectar o seu equipamento? Sim (   ) Não (   )"+"\n"+"\n"+
			" O Servico de Internet da DIGITAL foi testado e esta funcionando em perfeitas condições? Sim (   ) Não (   )"+"\n"+"\n"+
			" Houve danos no imovel ou equipamentos, e este(s) descrito(s) acima:? Sim (   ) Nao (   )");
			Paragraph pinfo = new Paragraph(sbInfo.toString(), fCampo);
			pinfo.setSpacingBefore(5);
			pinfo.setSpacingAfter(5);
			
			PdfPCell pCellpinfo = new PdfPCell();	
			pCellpinfo.addElement(pinfo);
//		pCellpinfo.setPadding(0);
			pCellpinfo.setBorderWidthTop(0);
			pCellpinfo.setBorderWidthRight(0);
			pCellpinfo.setBorderWidthBottom(0);
			
			StringBuilder sbAv = new StringBuilder();
			sbAv.append("Avaliacao do serviço"+"\n"+"técnico realizado"+"\n"+"(pessimo) 1 - 5 (otimo)"+"\n"+"\n"+"_________________________");
			Paragraph pAv = new Paragraph(sbAv.toString(), fCampo);
			pAv.setAlignment(Element.ALIGN_CENTER);
			pAv.setSpacingBefore(5);
			pAv.setSpacingAfter(5);
			
			PdfPCell pCellAv= new PdfPCell();	
			pCellAv.addElement(pAv);
//			pCellAv.setPadding(0);
			pCellAv.setBorderWidthTop(0);
			pCellAv.setBorderWidthLeft(0);
			pCellAv.setBorderWidthRight(0);
			pCellAv.setBorderWidthBottom(0);
			
			
			
			
			Paragraph pAmigo = new Paragraph("Indique um amigo                                                                                                                                                                                                                                              Telefone: (        )                                           ", fCampo);
			pAmigo.setSpacingBefore(5);
			pAmigo.setSpacingAfter(5);
			
			PdfPCell pCellAmigo= new PdfPCell();	
			pCellAmigo.addElement(pAmigo);
		
			
			PdfPTable tbInfo = new PdfPTable(new float[] {1f});
			tbInfo.setWidthPercentage(100f);			
			tbInfo.addCell(pCellAmigo);
			doc.add(tbInfo);	
			
			//Assinatura
			Paragraph pCampoAssTec = new Paragraph("_________________________________________", fCampo);
			pCampoAssTec.setSpacingBefore(25);
			pCampoAssTec.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph pCampoAssTecVl = new Paragraph("Assinatura do Técnico Responsavel", fCampo);
			pCampoAssTecVl.setAlignment(Element.ALIGN_CENTER);
			
			PdfPCell pCellCampoTecAss= new PdfPCell();	
			pCellCampoTecAss.addElement(pCampoAssTec);
			pCellCampoTecAss.addElement(pCampoAssTecVl);
			pCellCampoTecAss.setPadding(0);
			pCellCampoTecAss.setBorder(0);
			
			Paragraph pCampoData = new Paragraph("____/____/____", fCampo);
			pCampoData.setSpacingBefore(25);
			pCampoData.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph pVazio = new Paragraph("Data ", fCampo);
			pVazio.setAlignment(Element.ALIGN_CENTER);

			PdfPCell pCellCampoData= new PdfPCell();	
			pCellCampoData.addElement(pCampoData);
			pCellCampoData.addElement(pVazio);
			pCellCampoData.setPadding(0);
			pCellCampoData.setBorder(0);
			
			Paragraph pCampoAss = new Paragraph("_________________________________________", fCampo);
			pCampoAss.setSpacingBefore(25);
			pCampoAss.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph pCampoAssVl = new Paragraph("Nome do Cliente ou Preposto", fCampo);
			pCampoAssVl.setAlignment(Element.ALIGN_CENTER);
			
			PdfPCell pCellCampoAss= new PdfPCell();	
			pCellCampoAss.addElement(pCampoAss);
			pCellCampoAss.addElement(pCampoAssVl);
			pCellCampoAss.setPadding(0);
			pCellCampoAss.setBorder(0);
			
			Paragraph pCampoCnpj = new Paragraph("___________________________", fCampo);
			pCampoCnpj.setSpacingBefore(25);
			pCampoCnpj.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph pCampoCnpjVl = new Paragraph("CPF / CNPJ", fCampo);
			pCampoCnpjVl.setAlignment(Element.ALIGN_CENTER);
			
			PdfPCell pCellCampoCnpj= new PdfPCell();	
			pCellCampoCnpj.addElement(pCampoCnpj);
			pCellCampoCnpj.addElement(pCampoCnpjVl);
			pCellCampoCnpj.setPadding(0);
			pCellCampoCnpj.setBorder(0);
			
			Paragraph pCampoAss1 = new Paragraph("_________________________________________", fCampo);
			pCampoAss1.setSpacingBefore(25);
			pCampoAss1.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph pCampoAssVl1 = new Paragraph("Assinatura do Cliente ou Preposto", fCampo);
			pCampoAssVl1.setAlignment(Element.ALIGN_CENTER);
			
			PdfPCell pCellCampoAss1= new PdfPCell();	
			pCellCampoAss1.addElement(pCampoAss1);
			pCellCampoAss1.addElement(pCampoAssVl1);
			pCellCampoAss1.setPadding(0);
			pCellCampoAss1.setBorder(0);

			PdfPTable tbAssinatura = new PdfPTable(new float[] {1f,1f,0.60f,0.60f,1f});
			tbAssinatura.setWidthPercentage(100f);
			tbAssinatura.addCell(pCellCampoTecAss);
			tbAssinatura.addCell(pCellCampoAss);
			tbAssinatura.addCell(pCellCampoData);
			tbAssinatura.addCell(pCellCampoCnpj);
			tbAssinatura.addCell(pCellCampoAss1);
			
			PdfPCell pCellAssinatura= new PdfPCell();	
			pCellAssinatura.setPaddingTop(15);
			pCellAssinatura.addElement(tbAssinatura);
			
			PdfPTable tbAssinaturaRoot = new PdfPTable(new float[] {1f});
			tbAssinaturaRoot.setWidthPercentage(100f);
			tbAssinaturaRoot.addCell(pCellAssinatura);
			doc.add(tbAssinaturaRoot);
			
			
			
		} finally {
			if (doc.isOpen() && doc != null) {
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

