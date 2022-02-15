package com.digital.opuserp.view.modulos.ordemServico.assistenciaTecnica;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.OsiDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.AlteracoesAssistencia;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ConfigOsi;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.Osi;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.ServicosItensOsi;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;

public class ExportLaudo implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportLaudo(Integer cod)throws Exception {

		EntityManager em = ConnUtil.getEntity();
		
		
		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);
		

		try {
			PdfWriter.getInstance(doc, baos);
			doc.open();
			
			DataUtil dUtil = new DataUtil();
			
			//Busca
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			Osi osi = em.find(Osi.class, cod);
			Cliente cliente = osi.getCliente();
			
			Endereco  end = null;
			if(osi.getEnd()!=null){
				end = osi.getEnd();				
			}

			String dataAgend = "";
			if(osi.getData_agendamento()!=null){
				dataAgend = dUtil.parseDataBra(osi.getData_agendamento()).toString();
			}
			
			List<ServicosItensOsi> servicosItens = null;
			if(OsiDAO.getServicos(cod)!=null){
				servicosItens = OsiDAO.getServicos(cod);				
			}
			
			
			ContasReceberDAO crDAO = new ContasReceberDAO();
			List<ContasReceber> cr = crDAO.procurarBoletosOsi(cod);
			
			Date Vencimento = null;
			String formPgt = "";
			if(cr!=null){
				if(cr.get(0).getData_vencimento()!=null){
					Vencimento = cr.get(0).getData_vencimento();
				}
				if(cr.get(0).getForma_pgto()!=null && !cr.get(0).getForma_pgto().equals("")){
					formPgt = cr.get(0).getForma_pgto();
				}
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
			Font fSubTitulo  = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
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
			Paragraph pTelefones = new Paragraph("FONES: "+empresa.getDdd_fone1()+" "+empresa.getFone1()+" / "+empresa.get_0800(),fCampoBold);
			pTelefones.setAlignment(Element.ALIGN_CENTER);
			Paragraph pcnpj = new Paragraph("CNPJ: "+empresa.getCnpj(),fCampoBold);
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
			pCabLogo.addElement(pTelefones);
			pCabLogo.addElement(pSite);
						
			
			if(osi.getOperador()==null){
				osi.setOperador("");
			}		
			if(osi.getTecnico()==null){
				osi.setTecnico("");
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
			
			Paragraph pAbertoVl = new Paragraph(osi.getOperador(), fCaptionsBold);
			PdfPCell CellAberturaVl = new PdfPCell();
			CellAberturaVl.setBorderWidth(0);
			CellAberturaVl.setPadding(0);
			CellAberturaVl.addElement(pAbertoVl);
			
			Paragraph pDate = new Paragraph("em", fCaptions);
			PdfPCell CellDate = new PdfPCell();
			CellDate.setBorderWidth(0);
			CellDate.setPadding(0);
			CellDate.addElement(pDate);
			
			Paragraph pDateVl = new Paragraph(dUtil.parseDataHoraBra(osi.getData_entrada().toString()), fCaptionsBold);
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
				
			PdfPTable tbAbertura = new PdfPTable(new float[] {0.19f,0.32f,0.10f,0.40f,0.26f,0.60f });
			tbAbertura.setWidthPercentage(100f);
			tbAbertura.setSpacingBefore(5);
			tbAbertura.setSpacingAfter(5);
			tbAbertura.addCell(CellAbertura);
			tbAbertura.addCell(CellAberturaVl);
			tbAbertura.addCell(CellDate);
			tbAbertura.addCell(CellDateVl);
			tbAbertura.addCell(CellAgen);
			tbAbertura.addCell(CellAgenVl);
			
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
			
			PdfPTable tbEnd= new PdfPTable(new float[] {0.12f,1.25f});
			tbEnd.setWidthPercentage(100f);
			tbEnd.addCell(CellEnd);
			tbEnd.addCell(CellEndVl);
			
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
			
			PdfPTable tbBairro = new PdfPTable(new float[] {0.10f,0.89f,0.12f,0.60f});
			tbBairro.setWidthPercentage(100f);
			tbBairro.addCell(CellBairro);
			tbBairro.addCell(CellBairroVl);
			tbBairro.addCell(CellCidade);
			tbBairro.addCell(CellCidadeVl);

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
				pReferenciaVl = new Paragraph(end.getComplemento(), fCaptionsBold);
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
			
			Paragraph pFonesVl = new Paragraph(cliente.getDdd_fone1()+" "+cliente.getTelefone1()+"   "+cliente.getDdd_fone2()+" "+cliente.getTelefone2()+"   "+cliente.getDdd_cel1()+" "+cliente.getCelular1()+"   "+cliente.getDdd_cel2()+" "+cliente.getCelular2(), fCaptionsBold);
			PdfPCell CellFonesVl = new PdfPCell();
			CellFonesVl.setPadding(0);
			CellFonesVl.setBorderWidth(0);
			CellFonesVl.addElement(pFonesVl);
			
			PdfPTable tbFones = new PdfPTable(new float[] {0.12f,1.25f});
			tbFones.setWidthPercentage(100f);
			tbFones.setSpacingAfter(10);
			tbFones.addCell(CellFones);
			tbFones.addCell(CellFonesVl);
			
			//Linha 9 Técnico
			Paragraph pTecnico= new Paragraph("TÉCNICO:", fCaptions);
			PdfPCell CellTecnico = new PdfPCell();
			CellTecnico.setPadding(0);
			CellTecnico.setBorderWidth(0);
			CellTecnico.addElement(pTecnico);
			
			Paragraph pTecnicoVl = new Paragraph(osi.getTecnico(), fCaptionsBold);
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
			pCabInfo.addElement(tbEnd);
			pCabInfo.addElement(tbBairro);
			pCabInfo.addElement(tbComplemento);
			pCabInfo.addElement(tbRef);
			pCabInfo.addElement(tbFones);
			pCabInfo.addElement(tbTecnico);
			
			PdfPTable tb1 = new PdfPTable(new float[] { 0.35f, 0.65f });
			tb1.setWidthPercentage(100f);
			tb1.addCell(pCabLogo);
			tb1.addCell(pCabInfo);
			
			doc.add(tb1);			

			//grupo,subgrupo e tipo
			
			Paragraph pGrupo = new Paragraph("ASSISTÊNCIA TÉCNICA", fTitulo);
			pGrupo.setAlignment(Element.ALIGN_CENTER);
			pGrupo.setSpacingBefore(10);
			pGrupo.setSpacingAfter(10);
			doc.add(pGrupo);			
			
			//Equipameneto do cliente
			Paragraph pEqp = new Paragraph("Equipamento do Cliente:", fCaptions);	
			Paragraph pEqpVl = new Paragraph(osi.getEquipamento(), fCaptionsBold);	
			pEqpVl.setSpacingAfter(5);
		
			PdfPCell pCellEqp= new PdfPCell();
			pCellEqp.setBorderWidthRight(0);
			pCellEqp.addElement(pEqp);
			pCellEqp.setPaddingLeft(5);
			
			PdfPCell pCellEqpVl= new PdfPCell();
			pCellEqpVl.addElement(pEqpVl);
			pCellEqpVl.setBorderWidthLeft(0);
			
			PdfPTable tbEqp = new PdfPTable(new float[] {0.20f,1.25f});
			tbEqp.setWidthPercentage(100f);
			tbEqp.addCell(pCellEqp);
			tbEqp.addCell(pCellEqpVl);
			doc.add(tbEqp);	
			
			//Acessorio
			Paragraph pAcess = new Paragraph("Acessórios:", fCaptions);	
			
			Paragraph pAcessVl = new Paragraph();				
			if(osi.getAcessorios()!=null && !osi.getAcessorios().equals("")){
				pAcessVl = new Paragraph(osi.getAcessorios(), fCaptionsBold);	
			}
			pAcessVl.setSpacingAfter(5);
			
			PdfPCell pCellAcess= new PdfPCell();
			pCellAcess.addElement(pAcess);
			pCellAcess.setPaddingLeft(5);
			pCellAcess.setBorderWidthRight(0);
			
			PdfPCell pCellAcessVl= new PdfPCell();
			pCellAcessVl.addElement(pAcessVl);
			pCellAcessVl.setBorderWidthLeft(0);
			
			PdfPTable tbAcess = new PdfPTable(new float[] {0.20f,1.25f});
			tbAcess.setWidthPercentage(100f);
			tbAcess.addCell(pCellAcess);
			tbAcess.addCell(pCellAcessVl);
			doc.add(tbAcess);	
			
			//OBS
			Paragraph pObs = new Paragraph("OBSERVAÇÕES:", fCaptions);	
			
			Paragraph pObsVl = new Paragraph();				
			if(osi.getObservacao()!=null && !osi.getObservacao().equals("")){
				pObsVl = new Paragraph(osi.getObservacao(), fCaptionsBold);	
			}
			pObsVl.setSpacingAfter(10);
			
			PdfPCell pCellObs= new PdfPCell();
			pCellObs.addElement(pObs);
			pCellObs.setPaddingLeft(5);
			pCellObs.setBorderWidthRight(0);
			
			PdfPCell pCellObsVl= new PdfPCell();
			pCellObsVl.addElement(pObsVl);
			pCellObsVl.setBorderWidthLeft(0);
			
			PdfPTable tbObs = new PdfPTable(new float[] {0.20f,1.25f});
			tbObs.setWidthPercentage(100f);
			tbObs.addCell(pCellObs);
			tbObs.addCell(pCellObsVl);
			doc.add(tbObs);	
			
			
			
			//Serviços
			//Linha 1
			Paragraph pServ = new Paragraph("Serviços Realizados:", fCaptions);
			PdfPCell pCellServ= new PdfPCell();
			pCellServ.setPaddingLeft(5);
			pCellServ.setPaddingRight(0);
			pCellServ.setPaddingBottom(0);
			pCellServ.setPaddingTop(0);
			pCellServ.setBorderWidthRight(0);
			pCellServ.setBorderWidthBottom(0);
			pCellServ.addElement(pServ);
			
			Paragraph pQuantidade = new Paragraph("Quantidade", fCaptions);					
			PdfPCell pCellQuantidade= new PdfPCell();
			pCellQuantidade.setPaddingLeft(5);
			pCellQuantidade.setPaddingRight(0);
			pCellQuantidade.setPaddingBottom(0);
			pCellQuantidade.setPaddingTop(0);
			pCellQuantidade.setBorderWidthRight(0);
			pCellQuantidade.setBorderWidthLeft(0);
			pCellQuantidade.setBorderWidthBottom(0);
			pCellQuantidade.addElement(pQuantidade);
			
			Paragraph pDescricao = new Paragraph("Descrição do Serviço", fCaptions);	
			pDescricao.setSpacingAfter(5);
			PdfPCell pCellDescricao= new PdfPCell();
			pCellDescricao.setPaddingLeft(30);
			pCellDescricao.setPaddingRight(0);
			pCellDescricao.setPaddingBottom(0);
			pCellDescricao.setPaddingTop(0);
			pCellDescricao.setBorderWidthRight(0);
			pCellDescricao.setBorderWidthLeft(0);
			pCellDescricao.setBorderWidthBottom(0);
			pCellDescricao.addElement(pDescricao);
			
			Paragraph pValor = new Paragraph("Valor R$", fCaptions);					
			PdfPCell pCellvl= new PdfPCell();
			pCellvl.setPadding(0);
//			pCellvl.setBorderWidthRight(0);
			pCellvl.setBorderWidthLeft(0);
			pCellvl.setBorderWidthBottom(0);
			pCellvl.addElement(pValor);
			
			Paragraph pTotal = new Paragraph("Total", fCaptions);		
			PdfPCell pCellTotal= new PdfPCell();
			pCellTotal.setPadding(0);
			pCellTotal.setBorderWidthLeft(0);
			pCellTotal.setBorderWidthBottom(0);
			pCellTotal.addElement(pTotal);
						
			PdfPTable tbLinha1 = new PdfPTable(new float[] {0.25f,0.40f,0.35f});
			tbLinha1.setWidthPercentage(100f);
			tbLinha1.addCell(pCellServ);
			tbLinha1.addCell(pCellDescricao);
			tbLinha1.addCell(pCellvl);
			doc.add(tbLinha1);
			
			//Linha 2
			double vlServico = 0;
			PdfPCell pCellRoot= new PdfPCell();
			for (ServicosItensOsi servItem: servicosItens) {
			
				Paragraph pvazio = new Paragraph(" ", fCampoBold);
				PdfPCell pCellpvazio= new PdfPCell();
				pCellpvazio.setBorderWidth(0);
				pCellpvazio.setPadding(0);
				pCellpvazio.addElement(pvazio);
				
				Paragraph pQuantidadeVl = new Paragraph("01", fCampoBold);	
				pQuantidadeVl.setAlignment(Element.ALIGN_CENTER);
				PdfPCell pCellQuantidadeVl= new PdfPCell();
				pCellQuantidadeVl.setBorderWidth(0);
				pCellQuantidadeVl.setPadding(0);
				pCellQuantidadeVl.addElement(pQuantidadeVl);
				
				Paragraph pDescricaoVl = new Paragraph(servItem.getServico().getNome(), fCaptionsBold);					
				PdfPCell pCellDescricaoVl= new PdfPCell();
				pCellDescricaoVl.setPaddingLeft(30);
				pCellDescricaoVl.setPaddingRight(0);
				pCellDescricaoVl.setPaddingBottom(0);
				pCellDescricaoVl.setPaddingTop(0);
				pCellDescricaoVl.setBorderWidth(0);
				pCellDescricaoVl.addElement(pDescricaoVl);
				
				Paragraph pValorVl = new Paragraph(Real.formatDbToString(String.valueOf(servItem.getServico().getValor_venda().toString())), fCaptionsBold);					
				PdfPCell pCellvalorVl= new PdfPCell();
				pCellvalorVl.setPadding(0);
				pCellvalorVl.setBorderWidth(0);
				pCellvalorVl.addElement(pValorVl);
				
				Paragraph pTotalVl = new Paragraph(Real.formatDbToString(String.valueOf(servItem.getServico().getValor_venda().toString())), fCaptionsBold);		
				PdfPCell pCellTotalVl= new PdfPCell();
				pCellTotalVl.setPadding(0);
				pCellTotalVl.setBorderWidth(0);
				pCellTotalVl.addElement(pTotalVl);
				
//				PdfPTable tbLinhaVl = new PdfPTable(new float[] {0.35f,0.15f,1f,0.25f,0.80f});
				PdfPTable tbLinhaVl = new PdfPTable(new float[] {0.25f,0.40f,0.35f});
				tbLinhaVl.setWidthPercentage(100f);
				tbLinhaVl.addCell(pCellpvazio);
//				tbLinhaVl.addCell(pCellQuantidadeVl);
				tbLinhaVl.addCell(pCellDescricaoVl);
				tbLinhaVl.addCell(pCellvalorVl);
//				tbLinhaVl.addCell(pCellTotalVl);			
				
				pCellRoot.addElement(tbLinhaVl);				
				
				vlServico = vlServico + Double.parseDouble(servItem.getServico().getValor_venda());
			}
			
			pCellRoot.setPaddingBottom(25);
			pCellRoot.setBorderWidthTop(0);
			pCellRoot.setBorderWidthBottom(0);
			
			PdfPTable tbLinhaVl1 = new PdfPTable(new float[] {1f});
			tbLinhaVl1.setWidthPercentage(100f);
			tbLinhaVl1.addCell(pCellRoot);
			doc.add(tbLinhaVl1);
			

			//Valor do servico
			Paragraph pVlServic = new Paragraph("  Valor Total do Serviço", fConteudo);
			PdfPCell CellVlServic = new PdfPCell();
			CellVlServic.setPaddingTop(5);
			CellVlServic.setPaddingBottom(5);
			CellVlServic.setBorderWidthBottom(0);
			CellVlServic.setBorderWidthRight(0);;
			CellVlServic.addElement(pVlServic);
						
			
			Paragraph pVlServicVl = new Paragraph("R$: "+Real.formatDbToString(String.valueOf(vlServico)), fConteudoBold);
			PdfPCell CellVlServicVl = new PdfPCell();
			CellVlServicVl.setPaddingBottom(5);
			CellVlServicVl.setPaddingTop(5);
			CellVlServicVl.setBorderWidthBottom(0);
			CellVlServicVl.setBorderWidthLeft(0);
			CellVlServicVl.addElement(pVlServicVl);
			
			PdfPTable tbServ = new PdfPTable(new float[] {0.23f,1.40f});
			tbServ.setWidthPercentage(100f);
			tbServ.addCell(CellVlServic);
			tbServ.addCell(CellVlServicVl);
			doc.add(tbServ);
			
			//Forma de PGTO
			Paragraph pForPgt = new Paragraph("   Forma de Pagamento:", fCaptions);
			PdfPCell CellPgt = new PdfPCell();
			CellPgt.setPadding(0);
			CellPgt.setBorderWidthTop(0);
			CellPgt.setBorderWidthBottom(0);
			CellPgt.setBorderWidthRight(0);
			CellPgt.addElement(pForPgt);
			
			Paragraph pForPgtVl = new Paragraph("Boleto", fCaptionsBold);
			PdfPCell CellPgtVl = new PdfPCell();
			CellPgtVl.setPadding(0);
			CellPgtVl.setBorderWidthTop(0);
			CellPgtVl.setBorderWidthBottom(0);
			CellPgtVl.setBorderWidthLeft(0);
			CellPgtVl.addElement(pForPgtVl);
			
			PdfPTable tbPgtVl = new PdfPTable(new float[] {0.17f,1.25f});
			tbPgtVl.setWidthPercentage(100f);
			tbPgtVl.addCell(CellPgt);
			tbPgtVl.addCell(CellPgtVl);
			doc.add(tbPgtVl);
			
			//Vencimento
			Paragraph pVenc = new Paragraph("   Vencimento:", fCaptions);
			PdfPCell CellVenc = new PdfPCell();
			CellVenc.setPadding(0);
			CellVenc.setBorderWidthTop(0);
			CellVenc.setBorderWidthBottom(0);
			CellVenc.setBorderWidthRight(0);
			CellVenc.addElement(pVenc);
			
			Paragraph pVencVl = new Paragraph();				
			if(Vencimento!=null){
				pVencVl = new Paragraph(dUtil.parseDataBra(Vencimento), fCaptionsBold);					
			}
			PdfPCell CellVencVl = new PdfPCell();
			CellVencVl.setPadding(0);
			CellVencVl.setBorderWidthTop(0);
			CellVencVl.setBorderWidthBottom(0);
			CellVencVl.setBorderWidthLeft(0);
			CellVencVl.addElement(pVencVl);
			
			PdfPTable tbVenc = new PdfPTable(new float[] {0.12f,1.45f});
			tbVenc.setWidthPercentage(100f);
			tbVenc.addCell(CellVenc);
			tbVenc.addCell(CellVencVl);
			doc.add(tbVenc);
		
			Paragraph pAutoVl = new Paragraph("   Autorizo a Execucao do Servico no valor mensionado acima: _______________________________________________________________________", fCaptionsBold);
			pAutoVl.setSpacingAfter(5);
			
			PdfPCell CellAuto = new PdfPCell();
			CellAuto.setPaddingTop(15);
			CellAuto.setBorderWidthTop(0);
			CellAuto.addElement(pAutoVl);
			
			PdfPTable tbAuto = new PdfPTable(new float[] {1f});
			tbAuto.setWidthPercentage(100f);
			tbAuto.addCell(CellAuto);
			doc.add(tbAuto);

			//Laudo Tecnico
			Paragraph pLaudo = new Paragraph("   Laudo do Técnico", fCaptions);
			pLaudo.setSpacingAfter(15);
			PdfPCell CellLaudo = new PdfPCell();
			CellLaudo.setPaddingTop(5);
			CellLaudo.setBorderWidthRight(0);
			CellLaudo.addElement(pLaudo);
			
			Paragraph pLaudoVl = new Paragraph();				
			if(osi.getConclusao()!=null && !osi.getConclusao().equals("")){
				pLaudoVl = new Paragraph(osi.getConclusao(), fCaptionsBold);
			}
			pLaudoVl.setSpacingAfter(15);
			PdfPCell CellLaudoVl = new PdfPCell();
			CellLaudoVl.setPaddingTop(5);
			CellLaudoVl.setBorderWidthLeft(0);
			CellLaudoVl.addElement(pLaudoVl);
			
			
			//Ocorrencias
			Paragraph pOcorrencias = new Paragraph("   Ocorrências", fCaptions);
			pOcorrencias.setSpacingAfter(15);
			PdfPCell CellOcorrencias = new PdfPCell();
			CellOcorrencias.setPaddingTop(5);
			CellOcorrencias.setBorderWidthRight(0);
			CellOcorrencias.addElement(pOcorrencias);
			
			
			List<AlteracoesAssistencia> ocorrencias = OsiDAO.getOcorrencias(osi);
			
			PdfPCell CellOcorrencia = new PdfPCell();
			CellOcorrencia.setPaddingTop(5);
			CellOcorrencia.setBorderWidthLeft(0);

			if(ocorrencias != null){
				
				for (AlteracoesAssistencia ocorrencia : ocorrencias) {
					Paragraph pOcorrencia = new Paragraph(ocorrencia.getTipo(), fCaptionsBold);				
					//pOcorrencia.setSpacingAfter(15);

					CellOcorrencia.addElement(pOcorrencia);					
				}
									
			}
			
			PdfPTable tbLaudo = new PdfPTable(new float[] {0.25f,1.25f});
			tbLaudo.setWidthPercentage(100f);
			tbLaudo.addCell(CellLaudo);
			tbLaudo.addCell(CellLaudoVl);
			doc.add(tbLaudo);
			
			PdfPTable tbOcorrencia = new PdfPTable(new float[] {0.25f,1.25f});
			tbOcorrencia.setWidthPercentage(100f);
			tbOcorrencia.addCell(CellOcorrencias);
			tbOcorrencia.addCell(CellOcorrencia);
			doc.add(tbOcorrencia);
			
			//Peças Substituidas			
			Paragraph pPecas = new Paragraph("   Peças Substituidas:", fCaptions);
			pPecas.setSpacingAfter(15);
			PdfPCell CellPecas = new PdfPCell();
			CellPecas.setPaddingTop(5);
			CellPecas.setBorderWidthRight(0);
			CellPecas.addElement(pPecas);
			
			Paragraph pPecasVl = new Paragraph();				
			if(osi.getPecas_subs()!=null && !osi.getPecas_subs().equals("")){
				 pPecasVl = new Paragraph(osi.getPecas_subs(), fCaptionsBold);
			}
			pPecasVl.setSpacingAfter(15);
			PdfPCell CellPecasVl = new PdfPCell();
			CellPecasVl.setPaddingTop(5);
			CellPecasVl.setBorderWidthLeft(0);
			CellPecasVl.addElement(pPecasVl);
			
			PdfPTable tbPecas = new PdfPTable(new float[] {0.25f,1.25f});
			tbPecas.setWidthPercentage(100f);
			tbPecas.addCell(CellPecas);
			tbPecas.addCell(CellPecasVl);
			doc.add(tbPecas);
			
			//Data Conclusão
			Paragraph pDataConclu = new Paragraph("   Data Conclusão:", fCaptions);
			PdfPCell CellDataConclu = new PdfPCell();
			CellDataConclu.setPadding(0);
			CellDataConclu.setBorderWidth(0);
			CellDataConclu.addElement(pDataConclu);
			
			Paragraph pDataConcluVl = new Paragraph();				
			if(osi.getData_conclusao()!=null){
				pDataConcluVl = new Paragraph(dUtil.parseDataHoraBra(osi.getData_conclusao().toString()), fCaptionsBold);
			}
			PdfPCell CellDataConcluVl = new PdfPCell();
			CellDataConcluVl.setPadding(0);
			CellDataConcluVl.setBorderWidth(0);
			CellDataConcluVl.addElement(pDataConcluVl);
			
			Paragraph pDataEntrega = new Paragraph("Data Entrega:", fCaptions);
			PdfPCell CellDataEntrega = new PdfPCell();
			CellDataEntrega.setPadding(0);
			CellDataEntrega.setBorderWidth(0);
			CellDataEntrega.addElement(pDataEntrega);
			
			Paragraph pDataEntregaVl = new Paragraph();				
			if(osi.getData_entrega()!=null){
				pDataEntregaVl = new Paragraph(dUtil.parseDataHoraBra(osi.getData_entrega().toString()), fCaptionsBold);				
			}
			PdfPCell CellDataEntregaVl = new PdfPCell();
			CellDataEntregaVl.setPadding(0);
			CellDataEntregaVl.setBorderWidth(0);
			CellDataEntregaVl.addElement(pDataEntregaVl);
			
			PdfPTable tbData = new PdfPTable(new float[] {0.15f,0.20f,0.11f,1.15f});
			tbData.setWidthPercentage(100f);
			tbData.setSpacingAfter(20);
			tbData.addCell(CellDataConclu);
			tbData.addCell(CellDataConcluVl);
			tbData.addCell(CellDataEntrega);
			tbData.addCell(CellDataEntregaVl);
			doc.add(tbData);

			
			//Importante
			Paragraph pImportante = new Paragraph("IMPORTANTE: LEIA ATENTAMENTE ANTES DE ASSINAR", fConteudo);
			pImportante.setAlignment(Element.ALIGN_CENTER);
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
			
			Paragraph pAt = new Paragraph("Atenção", fCampo);
			PdfPCell pCellAt= new PdfPCell();	
			pCellAt.addElement(pAt);
			pCellAt.setPaddingLeft(4);
			pCellAt.setPaddingRight(0);
			pCellAt.setPaddingTop(0);
			pCellAt.setPaddingBottom(0);
			pCellAt.setBorderWidthBottom(0);	
			pCellAt.setBorderWidthTop(0);	
			
			PdfPTable tbAt = new PdfPTable(new float[] {1f});
			tbAt.setWidthPercentage(100f);
			tbAt.addCell(pCellAt);
			doc.add(tbAt);
			
			StringBuilder sbNumero = new StringBuilder();
			sbNumero.append("  1."+"\n"+"  2."+"\n"+"  3."+"\n"+"  4."+"\n"+" 5.");
			Paragraph pCaseN = new Paragraph(sbNumero.toString(), fCampoBold);
			
			StringBuilder sbVl = new StringBuilder();
			sbVl.append(" Os equipamentos reparados terão garantia de 90 (noventa) dias sobre os serviços executados a contar a partir da Conclusão da Ordem de Serviço, não incluindo mau uso e alterações nas características do equipamento;"+"\n"+
			" Não estão cobertos pela garantia, defeitos provenientes de dano elétrico, problemas gerados por softwares ou erros de operação;"+"\n"+
			" O equipamento só será entregue mediante a apresentação deste documento;"+"\n"+
			" Não nos responsabilizamos por programas instalados em seu equipamento."+"\n"+" A não retirada do equipamento no prazo de 90 (noventa) dias, implicará na venda do equipamento para custear despesas do serviço e armazenamento.");
			
			Paragraph pCaseVl = new Paragraph(sbVl.toString(),fCampo);
			pCaseVl.setSpacingAfter(3);
			
			PdfPCell pCellpCaseN= new PdfPCell();	
			pCellpCaseN.addElement(pCaseN);
			pCellpCaseN.setPaddingLeft(2);
			pCellpCaseN.setPaddingRight(0);
			pCellpCaseN.setPaddingTop(0);
			pCellpCaseN.setPaddingBottom(0);
			pCellpCaseN.setBorderWidthTop(0);
			pCellpCaseN.setBorderWidthBottom(0);
			pCellpCaseN.setBorderWidthRight(0);
			
			PdfPCell pCellpCaseVl= new PdfPCell();	
			pCellpCaseVl.addElement(pCaseVl);
			pCellpCaseVl.setPadding(0);
			pCellpCaseVl.setBorderWidthTop(0);
			pCellpCaseVl.setBorderWidthBottom(0);
			pCellpCaseVl.setBorderWidthLeft(0);
				
			PdfPTable tbpCase1 = new PdfPTable(new float[] {0.2f,11f});
			tbpCase1.setWidthPercentage(100f);
			tbpCase1.addCell(pCellpCaseN);
			tbpCase1.addCell(pCellpCaseVl);
			doc.add(tbpCase1);	
			
			
			Paragraph pDig = new Paragraph("A DIGITAL se isenta de toda e qualquer responsabilidade dos programas instalados em seu micro, portanto aconselhamos que o cliente mantenha seu backup atualizado.", fCampo);
			pDig.setSpacingAfter(5);
			pDig.setSpacingBefore(5);
			Paragraph pSera = new Paragraph();
			pSera.add(new Phrase("* Será cobrado taxa de Laudo Técnico caso o orçamento não seja aceito no valor de ", fCampo));
						
			ConfigOsi confOsi = OsiDAO.getConfigOsi(OpusERP4UI.getEmpresa().getId());
			
			pSera.add(new Phrase("R$ "+confOsi.getValor_laudo(), fCampoBold));				
			
			PdfPCell pCellSera= new PdfPCell();	
			pCellSera.addElement(pDig);
			pCellSera.addElement(pSera);
			pCellSera.setPaddingLeft(4);
			pCellSera.setPaddingBottom(5);	
			pCellSera.setBorderWidthTop(0);	
			
			PdfPTable tbSera = new PdfPTable(new float[] {1f});
			tbSera.setWidthPercentage(100f);
			tbSera.setSpacingAfter(10);
			tbSera.addCell(pCellSera);
			doc.add(tbSera);
			
			//Assinatura
			Paragraph pCampoData = new Paragraph("____/____/____", fCaptions);
			pCampoData.setSpacingBefore(25);
			pCampoData.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph pVazio = new Paragraph(" ", fCaptions);
			pVazio.setAlignment(Element.ALIGN_CENTER);

			PdfPCell pCellCampoData= new PdfPCell();	
			pCellCampoData.addElement(pCampoData);
			pCellCampoData.addElement(pVazio);
			pCellCampoData.setPadding(0);
			pCellCampoData.setBorder(0);
			
			Paragraph pCampoAss = new Paragraph("_________________________________________", fCaptions);
			pCampoAss.setSpacingBefore(25);
			pCampoAss.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph pCampoAssVl = new Paragraph("Nome do Cliente ou Preposto", fCaptions);
			pCampoAssVl.setAlignment(Element.ALIGN_CENTER);
			
			PdfPCell pCellCampoAss= new PdfPCell();	
			pCellCampoAss.addElement(pCampoAss);
			pCellCampoAss.addElement(pCampoAssVl);
			pCellCampoAss.setPadding(0);
			pCellCampoAss.setBorder(0);
			
			Paragraph pCampoCnpj = new Paragraph("_________________________________________", fCaptions);
			pCampoCnpj.setSpacingBefore(25);
			pCampoCnpj.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph pCampoCnpjVl = new Paragraph("CPF / CNPJ", fCaptions);
			pCampoCnpjVl.setAlignment(Element.ALIGN_CENTER);
			
			PdfPCell pCellCampoCnpj= new PdfPCell();	
			pCellCampoCnpj.addElement(pCampoCnpj);
			pCellCampoCnpj.addElement(pCampoCnpjVl);
			pCellCampoCnpj.setPadding(0);
			pCellCampoCnpj.setBorder(0);
			
			Paragraph pCampoAss1 = new Paragraph("_________________________________________", fCaptions);
			pCampoAss1.setSpacingBefore(25);
			pCampoAss1.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph pCampoAssVl1 = new Paragraph("Assinatura do Cliente ou Preposto", fCaptions);
			pCampoAssVl1.setAlignment(Element.ALIGN_CENTER);
			
			PdfPCell pCellCampoAss1= new PdfPCell();	
			pCellCampoAss1.addElement(pCampoAss1);
			pCellCampoAss1.addElement(pCampoAssVl1);
			pCellCampoAss1.setPadding(0);
			pCellCampoAss1.setBorder(0);

			PdfPTable tbAssinatura = new PdfPTable(new float[] {0.30f,1f,1f,1f});
			tbAssinatura.setWidthPercentage(100f);
			tbAssinatura.addCell(pCellCampoData);
			tbAssinatura.addCell(pCellCampoAss);
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


