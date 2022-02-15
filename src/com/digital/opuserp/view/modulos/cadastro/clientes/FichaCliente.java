package com.digital.opuserp.view.modulos.cadastro.clientes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CredenciaisAcessoDAO;
import com.digital.opuserp.dao.EcfPreVendaCabecalhoDAO;
import com.digital.opuserp.dao.TotaisPedidoDAO;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ComoNosConheceu;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.CreditoCliente;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.RadAcct;
import com.digital.opuserp.domain.TabelasPreco;
import com.digital.opuserp.domain.TotaisPedido;
import com.digital.opuserp.util.ByteUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.TimeUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
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


public class FichaCliente implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	public FichaCliente(Integer codCliente) throws Exception{
		
		EntityManager em = ConnUtil.getEntity();

		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);	
		
		try{
			PdfWriter.getInstance(doc, baos);
			doc.open();

			 float paddingTop=0;
			 float paddingBottom=5;
			 
			//Cabecalho			
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			Cliente cliente = em.find(Cliente.class, codCliente);

			Query qEnd = em.createQuery("select e from Endereco e where e.clientes = :codCliente and e.principal =:true", Endereco.class);
			qEnd.setParameter("codCliente", new Cliente(cliente.getId()));			
			qEnd.setParameter("true", true);			
			Endereco end = null;
			if(qEnd.getResultList().size() > 0){
				end = (Endereco) qEnd.getSingleResult();
			}else{
				end = new Endereco();
				end.setCep(" ");
				end.setEndereco(" ");
			}
			
			Query qEndAlt1 = em.createQuery("select e from Endereco e where e.clientes = :codCliente and e.principal =:false ORDER BY id DESC", Endereco.class);
			qEndAlt1.setParameter("codCliente", new Cliente(cliente.getId()));													
			qEndAlt1.setParameter("false", false);			
			Endereco endAlt1 = null;
			if(qEndAlt1.getResultList().size() > 0){
				endAlt1 = (Endereco) qEndAlt1.getResultList().get(0);
			}else{
				endAlt1 = new Endereco();
				endAlt1.setCep(" ");
				endAlt1.setEndereco(" ");
				endAlt1.setBairro(" ");
				endAlt1.setComplemento(" ");
			}
			
			Query qEndAlt2 = em.createQuery("select e from Endereco e where e.clientes = :codCliente and e.principal =:false ORDER BY id DESC" , Endereco.class);
			qEndAlt2.setParameter("codCliente", new Cliente(cliente.getId()));			
			qEndAlt2.setParameter("false", false);						
			
			Endereco endAlt2 = null;
			if(qEndAlt2.getResultList().size() > 1){
				endAlt2 = (Endereco) qEndAlt1.getResultList().get(1);
			}else{
				endAlt2 = new Endereco();
				endAlt2.setCep(" ");
				endAlt2.setEndereco(" ");
				endAlt2.setBairro(" ");
				endAlt2.setComplemento(" ");
			}
			
			
			TabelasPreco tabePrecPadr = new TabelasPreco();
			if(cliente.getTabela_preco() == null  ){
				tabePrecPadr.setNome("TABELA PADRÃO ");
			}else{
				tabePrecPadr = cliente.getTabela_preco();
			}

			Query qCreditoCliente = em.createQuery("select c from CreditoCliente c where c.cliente = :codCliente", CreditoCliente.class);
			qCreditoCliente.setParameter("codCliente", new Cliente(cliente.getId()));	
			CreditoCliente creditoClient = new CreditoCliente();
			if(qCreditoCliente.getResultList().size() > 0){
				creditoClient = (CreditoCliente) qCreditoCliente.getSingleResult();
				
			}else{
				creditoClient.setLimite_credito(" ");
				creditoClient.setSaldo(" ");
			}
	
			Query qEcfPreVenCab = em.createQuery("select ecf from EcfPreVendaCabecalho ecf where ecf.cliente = :codCliente", EcfPreVendaCabecalho.class);
			qEcfPreVenCab.setParameter("codCliente", new Cliente(cliente.getId()));	
			EcfPreVendaCabecalho preVend = new EcfPreVendaCabecalho();
			List<EcfPreVendaCabecalho> result = null;
			if(qEcfPreVenCab.getResultList().size() > 0){
				result = qEcfPreVenCab.getResultList();
			}

			Query qContasReceber = em.createQuery("select cr from ContasReceber cr where cr.cliente = :codCliente ORDER BY data_vencimento ASC", ContasReceber.class);
			qContasReceber.setParameter("codCliente",new Cliente(cliente.getId()));	
			List<ContasReceber> contReceber = null; 
			if(qContasReceber.getResultList().size() > 0){
				contReceber = qContasReceber.getResultList();
			}
							
			EcfPreVendaCabecalhoDAO pDAO = new EcfPreVendaCabecalhoDAO();
			String primeiraCompra = pDAO.getPrimeDate(cliente.getId());
			String ultimaCompra = pDAO.getLastDate(cliente.getId());

//			byte[] logo = empresa.getLogo_empresa();		

			//Estilos de Fonts
			Font fCaptions = new Font(FontFamily.COURIER, 6, Font.BOLD);
			Font fCampo = new Font(FontFamily.HELVETICA, 5);
			Font fCampoBold = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font fConteudoBold = new Font(FontFamily.HELVETICA, 7, Font.BOLD);
			Font fConteudo = new Font(FontFamily.HELVETICA, 7);
			Font fTituloBold  = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font fTitulo  = new Font(FontFamily.HELVETICA, 10);
			Font fSubTitulo  = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
			Font fCab = new Font(FontFamily.HELVETICA, 8);
			
			//Cabeçario
			DataUtil dtUtil = new DataUtil();
			String 	date = dtUtil.getDataExtenso(new Date());
			
			StringBuilder SbCabecalho = new StringBuilder();
			SbCabecalho.append("OPUS ERP4"+"\n"+"EMPRESA:"+"\n"+"EMISSÃO:"+"\n"+"OPERADOR:");
			
			StringBuilder SbCabecalhoVl = new StringBuilder();
			SbCabecalhoVl.append("\n"+empresa.getNome_fantasia()+"\n"+date+"\n"+OpusERP4UI.getUsuarioLogadoUI().getUsername());
			
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
								
			//SubTitulo
			Paragraph pFichaCiente = new Paragraph("FICHA DO CLIENTE", fTitulo);
			pFichaCiente.setAlignment(Element.ALIGN_CENTER);
			pFichaCiente.setSpacingAfter(20);
			
			doc.add(pFichaCiente);
			
			//Identificação
			Paragraph pIdentificacai = new Paragraph("IDENTIFICAÇÃO", fSubTitulo);
			pIdentificacai.setAlignment(Element.ALIGN_LEFT);
			pIdentificacai.setSpacingAfter(5);
			
			doc.add(pIdentificacai);
			
			
			
			//Linha 1			
			Phrase pCodigo = new Phrase("CÓDIGO:", fCampo);
			Phrase pCodigoValor = new Phrase(cliente.getId().toString(), fConteudo);
					
			Phrase pNome = new Phrase("NOME/RAZÃO:",  fCampo);
			Phrase pNomeValor = new Phrase(cliente.getNome_razao(),fConteudo);
			
			Phrase pFantasia = new Phrase("FANTASIA:",  fCampo);
			Phrase pFantasiaValor = new Phrase(cliente.getNome_fantasia(), fConteudo);		
			
			Phrase pContato = new Phrase("CONTATO:",  fCampo);
			Phrase pContatoValor = new Phrase(cliente.getContato(), fConteudo);
			
			//Linha 2			
			Phrase pSexo = new Phrase("SEXO:",  fCampo);
			Phrase pSexoValor = new Phrase(cliente.getSexo(), fConteudo);
			
			Phrase pDataNascimento = new Phrase("DATA DE NASCIMENTO:",  fCampo);
			Phrase pDataNascimentoValor = new Phrase();
			if(cliente.getData_nascimento()!=null){
				pDataNascimentoValor = new Phrase(dtUtil.parseDataBra(cliente.getData_nascimento().toString()),fConteudo);		
			}
			
			Phrase pCpf= new Phrase("CPF/CNPJ:",  fCampo);
			Phrase pCpfValor = new Phrase(cliente.getDoc_cpf_cnpj(), fConteudo);
			
			Phrase pRg= new Phrase("RG:",  fCampo);
			Phrase pRgValor = new Phrase(cliente.getDoc_rg_insc_estadual(),fConteudo);
			
			Phrase pCadastro = new Phrase("CADASTRO:", fCampo);
			
			Phrase pCadastroValor = new Phrase();				
			if(cliente.getData_cadastro()!=null){
				pCadastroValor = new Phrase(dtUtil.parseDataHoraBra(cliente.getData_cadastro().toString()), fConteudo);				
			}
			
			Phrase pAlteracao = new Phrase("ALTERAÇÃO:",  fCampo);
			Phrase pAlteracaoValor = new Phrase(dtUtil.parseDataHoraBra(cliente.getData_alteracao().toString()), fConteudo);
			
			Phrase pStatus = new Phrase("STATUS:",  fCampo);
			Phrase pStatusValor = new Phrase(cliente.getStatus(),fConteudo);
			
			//Linha 3	
			
			if(cliente.getDdd_fone1()==null){
				cliente.setDdd_fone1(" ");
			}
			if(cliente.getTelefone1() == null){
				cliente.setTelefone1(" ");
			}			
			if(cliente.getDdd_fone2()==null){
				cliente.setDdd_fone2(" ");
			}
			if(cliente.getTelefone2() == null){
				cliente.setTelefone2(" ");
			}			
			if(cliente.getDdd_cel1()==null){
				cliente.setDdd_cel1(" ");
			}
			if(cliente.getCelular1() == null){
				cliente.setCelular1(" ");
			}			
			if(cliente.getDdd_cel2()==null){
				cliente.setDdd_cel2(" ");
			}
			if(cliente.getCelular2() == null){
				cliente.setCelular2(" ");
			}
			
			Phrase pTelefonePrincipal = new Phrase("TELEFONE PRINCIPAL:",  fCampo);
			Phrase pTelefonePrincipalValor= new Phrase(cliente.getDdd_fone1()+ " " + cliente.getTelefone1(), fConteudo);
			
			Phrase pTelefoneAlternativo1 = new Phrase("TELEFONE ALTERNATIVO:",  fCampo);
			Phrase pTelefoneAlternativo1Valor= new Phrase(cliente.getDdd_fone2()+ " "+cliente.getTelefone2(),fConteudo);
			
			Phrase pTelefoneAlternativo2= new Phrase("TELEFONE ALTERNATIVO:",  fCampo);
			Phrase pTelefoneAlternativo2Valor= new Phrase(cliente.getDdd_cel1()+ " "+cliente.getCelular1(), fConteudo);
			
			Phrase pTelefoneAlternativo3 = new Phrase("TELEFONE ALTERNATIVO:",  fCampo);
			Phrase pTelefoneAlternativo3Valor= new Phrase(cliente.getDdd_cel2()+ " "+cliente.getCelular2(), fConteudo);
			
			
			//Linha 4
			Phrase pSite= new Phrase("SITE:",  fCampo);
			if(cliente.getSite()==null){
				cliente.setSite(" ");
			}
			Phrase pSiteValor= new Phrase(cliente.getSite(),fConteudo);
			
			Phrase pEmailPrincipal= new Phrase("EMAIL PRINCIPAL:",  fCampo);
			Phrase pEmailPrincipalValor= new Phrase(cliente.getEmail(), fConteudo);
			
			Phrase pEmailAlternativo= new Phrase("EMAIL ALTERNATIVO:",  fCampo);
			Phrase pEmailAlternativoValor= new Phrase(cliente.getEmailAlternativo(),fConteudo);
						
			//Linha 5
			Phrase pCategoria= new Phrase("CATEGORIA:",  fCampo);
			Phrase pCategoriaValor= new Phrase(cliente.getCategoria().getNome(),fConteudo);
									
			if(cliente.getComo_nos_conheceu() != null){
				cliente.getComo_nos_conheceu().setNome(cliente.getComo_nos_conheceu().getNome());
			}else{
				cliente.setComo_nos_conheceu(new ComoNosConheceu(null, null, "NÃO INFORMADO", null, null));
			}
			Phrase pComoNosConheceu= new Phrase("COMO NOS CONHECEU?:",  fCampo);
			Phrase pComoNosConheceuValor= new Phrase(cliente.getComo_nos_conheceu().getNome(), fConteudo);
					
			//Linha 6
			Phrase pObservacao= new Phrase("OBSERVAÇÃO:",  fCampo);
			if(cliente.getObs()==null || cliente.getObs().equals("")){
				cliente.setObs(" ");
			}
			Phrase pObservacaoValor= new Phrase(cliente.getObs(), fConteudo);
			
						
			
			//Linha 1
			PdfPCell pCellCodigo = new PdfPCell();
			pCellCodigo.setPaddingBottom(paddingBottom);
			pCellCodigo.setPaddingTop(paddingTop);
			pCellCodigo.addElement(pCodigo);
			pCellCodigo.addElement(pCodigoValor);						
			
			PdfPCell pCellNome = new PdfPCell();
			pCellNome.setPaddingBottom(paddingBottom);
			pCellNome.setPaddingTop(paddingTop);
			pCellNome.addElement(pNome);
			pCellNome.addElement(pNomeValor);
						
			PdfPCell pCellFantasia = new PdfPCell();
			pCellFantasia.setPaddingBottom(paddingBottom);
			pCellFantasia.setPaddingTop(paddingTop);
			pCellFantasia.addElement(pFantasia);
			pCellFantasia.addElement(pFantasiaValor);
			
			PdfPCell pCellContato = new PdfPCell();
			pCellContato.setPaddingBottom(paddingBottom);
			pCellContato.setPaddingTop(paddingTop);
			pCellContato.addElement(pContato);
			pCellContato.addElement(pContatoValor);
			
			PdfPTable tb1 = new PdfPTable(new float[] { 0.20f, 1f, 0.80f, 0.50f});
			tb1.setWidthPercentage(100f);
			tb1.addCell(pCellCodigo);
			tb1.addCell(pCellNome);
			tb1.addCell(pCellFantasia);
			tb1.addCell(pCellContato);
			
			//Linha 2			
			PdfPCell pCellSexo = new PdfPCell();
			pCellSexo.setPaddingBottom(paddingBottom);
			pCellSexo.setPaddingTop(paddingTop);
			pCellSexo.addElement(pSexo);
			pCellSexo.addElement(pSexoValor);
			
			PdfPCell pCellDataNacimento = new PdfPCell();
			pCellDataNacimento.setPaddingBottom(paddingBottom);
			pCellDataNacimento.setPaddingTop(paddingTop);
			pCellDataNacimento.addElement(pDataNascimento);
			pCellDataNacimento.addElement(pDataNascimentoValor);
			
			PdfPCell pCellCpf = new PdfPCell();
			pCellCpf.setPaddingBottom(paddingBottom);
			pCellCpf.setPaddingTop(paddingTop);
			pCellCpf.addElement(pCpf);
			pCellCpf.addElement(pCpfValor);
			
			PdfPCell pCellRg = new PdfPCell();
			pCellRg.setPaddingBottom(paddingBottom);
			pCellRg.setPaddingTop(paddingTop);
			pCellRg.addElement(pRg);
			pCellRg.addElement(pRgValor);
						
			PdfPCell pCellCadastro = new PdfPCell();
			pCellCadastro.setPaddingBottom(paddingBottom);
			pCellCadastro.setPaddingTop(paddingTop);
			pCellCadastro.addElement(pCadastro);
			pCellCadastro.addElement(pCadastroValor);
			
			PdfPCell pCellAlteracao = new PdfPCell();
			pCellAlteracao.setPaddingBottom(paddingBottom);
			pCellAlteracao.setPaddingTop(paddingTop);
			pCellAlteracao.addElement(pAlteracao);
			pCellAlteracao.addElement(pAlteracaoValor);
			
			PdfPCell pCellStatus = new PdfPCell();
			pCellStatus.setPaddingBottom(paddingBottom);
			pCellStatus.setPaddingTop(paddingTop);
			pCellStatus.addElement(pStatus);
			pCellStatus.addElement(pStatusValor);
			
			PdfPTable tb2 = new PdfPTable(new float[] { 0.35f, 0.50f, 0.50f , 0.60f , 0.73f , 0.73f , 0.27f});
			tb2.setWidthPercentage(100f);
			tb2.addCell(pCellSexo);
			tb2.addCell(pCellDataNacimento);
			tb2.addCell(pCellCpf);
			tb2.addCell(pCellRg);			
			tb2.addCell(pCellCadastro);
			tb2.addCell(pCellAlteracao);
			tb2.addCell(pCellStatus);
				
			//Linha 3
			PdfPCell pCellTelefonePrincipal = new PdfPCell();
			pCellTelefonePrincipal.setPaddingBottom(paddingBottom);
			pCellTelefonePrincipal.setPaddingTop(paddingTop);
			pCellTelefonePrincipal.addElement(pTelefonePrincipal);
			pCellTelefonePrincipal.addElement(pTelefonePrincipalValor);
		
			PdfPCell pCellTelefoneAlternativo1 = new PdfPCell();
			pCellTelefoneAlternativo1.setPaddingBottom(paddingBottom);
			pCellTelefoneAlternativo1.setPaddingTop(paddingTop);
			pCellTelefoneAlternativo1.addElement(pTelefoneAlternativo1);
			pCellTelefoneAlternativo1.addElement(pTelefoneAlternativo1Valor);
			
			PdfPCell pCellTelefoneAlternativo2 = new PdfPCell();
			pCellTelefoneAlternativo2.setPaddingBottom(paddingBottom);
			pCellTelefoneAlternativo2.setPaddingTop(paddingTop);
			pCellTelefoneAlternativo2.addElement(pTelefoneAlternativo2);
			pCellTelefoneAlternativo2.addElement(pTelefoneAlternativo2Valor);
			
			PdfPCell pCellTelefoneAlternativo3 = new PdfPCell();
			pCellTelefoneAlternativo3.setPaddingBottom(paddingBottom);
			pCellTelefoneAlternativo3.setPaddingTop(paddingTop);
			pCellTelefoneAlternativo3.addElement(pTelefoneAlternativo3);
			pCellTelefoneAlternativo3.addElement(pTelefoneAlternativo3Valor);
			
			PdfPTable tb3 = new PdfPTable(new float[] { 1f, 1f, 1f ,1f});
			tb3.setWidthPercentage(100f);
			tb3.addCell(pCellTelefonePrincipal);
			tb3.addCell(pCellTelefoneAlternativo1);
			tb3.addCell(pCellTelefoneAlternativo2);
			tb3.addCell(pCellTelefoneAlternativo3);
			
			//Linha 4
			PdfPCell pCellSite = new PdfPCell();
			pCellSite.setPaddingBottom(paddingBottom);
			pCellSite.setPaddingTop(paddingTop);
			pCellSite.addElement(pSite);
			pCellSite.addElement(pSiteValor);
					
			PdfPCell pCellEmailPrincipal= new PdfPCell();
			pCellEmailPrincipal.setPaddingBottom(paddingBottom);
			pCellEmailPrincipal.setPaddingTop(paddingTop);
			pCellEmailPrincipal.addElement(pEmailPrincipal);
			pCellEmailPrincipal.addElement(pEmailPrincipalValor);
			
			PdfPCell pCellEmailAlternativo= new PdfPCell();
			pCellEmailAlternativo.setPaddingBottom(paddingBottom);
			pCellEmailAlternativo.setPaddingTop(paddingTop);
			pCellEmailAlternativo.addElement(pEmailAlternativo);
			pCellEmailAlternativo.addElement(pEmailAlternativoValor);
	
			PdfPTable tb4 = new PdfPTable(new float[] { 1f, 1f, 1f});
			tb4.setWidthPercentage(100f);
			tb4.addCell(pCellSite);	
			tb4.addCell(pCellEmailPrincipal);
			tb4.addCell(pCellEmailAlternativo);
			
			//Linha 5
			PdfPCell pCellCategoria =  new PdfPCell();
			pCellCategoria.setPaddingBottom(paddingBottom);
			pCellCategoria.setPaddingTop(paddingTop);
			pCellCategoria.addElement(pCategoria);
			pCellCategoria.addElement(pCategoriaValor);
			
			PdfPCell pCellComoNosConheceu =  new PdfPCell();
			pCellComoNosConheceu.setPaddingBottom(paddingBottom);
			pCellComoNosConheceu.setPaddingTop(paddingTop);
			pCellComoNosConheceu.addElement(pComoNosConheceu);
			pCellComoNosConheceu.addElement(pComoNosConheceuValor);
			
			PdfPTable tb5 = new PdfPTable(new float[] {1f,1f});
			tb5.setWidthPercentage(100);
			tb5.addCell(pCellCategoria);
			tb5.addCell(pCellComoNosConheceu);
				
			//Linha 6
			PdfPCell pCellObs = new PdfPCell();
			pCellObs.setPaddingBottom(paddingBottom);
			pCellObs.setPaddingTop(paddingTop);
			pCellObs.addElement(pObservacao);
			pCellObs.addElement(pObservacaoValor);
			
			PdfPTable tb6 = new PdfPTable(new float[] {1});
			tb6.setWidthPercentage(100);
			tb6.addCell(pCellObs);
			
			doc.add(tb1);
			doc.add(tb2);	
			doc.add(tb3);	
			doc.add(tb4);
			doc.add(tb5);
			doc.add(tb6);
			

			//ENDERECO PRINCIPAL
			Paragraph pEnderecoPrincipal = new Paragraph("ENDEREÇO PRINCIPAL", fSubTitulo);
			pEnderecoPrincipal.setAlignment(Element.ALIGN_LEFT);
			pEnderecoPrincipal.setSpacingAfter(5);
			pEnderecoPrincipal.setSpacingBefore(30);
			doc.add(pEnderecoPrincipal);
						
			//ENDEREÇO ALTERNATIVO 1
			Phrase pEnderecoCepEnd= new Phrase("ENDEREÇO:",  fCampo);
			Phrase pEnderecoCepValorEnd= new Phrase(end.getEndereco(),fConteudo);
			
			Phrase pNumeroEnd= new Phrase("NUMERO:",  fCampo);
			Phrase pNumeroValorEnd= new Phrase(end.getNumero(), fConteudo);
			
			Phrase pCepEnd= new Phrase("CEP:",  fCampo);
			Phrase pCepValorEnd= new Phrase(end.getCep(), fConteudo);
			
			Phrase pBairroEnd= new Phrase("BAIRRO:",  fCampo);
			Phrase pBairroValorEnd= new Phrase(end.getBairro(),fConteudo);
			
			Phrase pCidadeEnd= new Phrase("CIDADE:",  fCampo);
			Phrase pCidadeValorEnd= new Phrase(end.getCidade(), fConteudo);
			
			Phrase pUFEnd= new Phrase("UF:",  fCampo);
			Phrase pUfValorEnd= new Phrase(end.getUf(), fConteudo);
			
			Phrase pPaisEnd= new Phrase("PAÍS:",  fCampo);
			Phrase pPaisValorEnd= new Phrase(end.getPais(), fConteudo);
			
			Phrase pComplementoEnd= new Phrase("COMPLEMENTO:",  fCampo);
			Phrase pComplementoValorEnd= new Phrase(end.getComplemento(),fConteudo);
			
			Phrase pReferenciaEnd= new Phrase("REFERÊNCIA:",  fCampo);
			Phrase pReferenciaValorEnd= new Phrase(end.getReferencia(),fConteudo);
			
			PdfPCell pCellCepEnd= new PdfPCell();
			pCellCepEnd.setPaddingBottom(paddingBottom);
			pCellCepEnd.setPaddingTop(paddingTop);
			pCellCepEnd.addElement(pCepEnd);
			pCellCepEnd.addElement(pCepValorEnd);
			
			PdfPCell pCellEnderecoEnd= new PdfPCell();
			pCellEnderecoEnd.setPaddingBottom(paddingBottom);
			pCellEnderecoEnd.setPaddingTop(paddingTop);
			pCellEnderecoEnd.addElement(pEnderecoCepEnd);
			pCellEnderecoEnd.addElement(pEnderecoCepValorEnd);
			
			PdfPCell pCellNumeroEnd= new PdfPCell();
			pCellNumeroEnd.setPaddingBottom(paddingBottom);
			pCellNumeroEnd.setPaddingTop(paddingTop);
			pCellNumeroEnd.addElement(pNumeroEnd);
			pCellNumeroEnd.addElement(pNumeroValorEnd);

			PdfPCell pCellBairroEnd= new PdfPCell();
			pCellBairroEnd.setPaddingBottom(paddingBottom);
			pCellBairroEnd.setPaddingTop(paddingTop);
			pCellBairroEnd.addElement(pBairroEnd);
			pCellBairroEnd.addElement(pBairroValorEnd);
			
			PdfPCell pCellCidadeEnd= new PdfPCell();
			pCellCidadeEnd.setPaddingBottom(paddingBottom);
			pCellCidadeEnd.setPaddingTop(paddingTop);
			pCellCidadeEnd.addElement(pCidadeEnd);
			pCellCidadeEnd.addElement(pCidadeValorEnd);			
			
			PdfPCell pCellUfEnd = new PdfPCell();
			pCellUfEnd.setPaddingBottom(paddingBottom);
			pCellUfEnd.setPaddingTop(paddingTop);
			pCellUfEnd.addElement(pUFEnd);
			pCellUfEnd.addElement(pUfValorEnd);
			
			PdfPCell pCellPaisEnd = new PdfPCell();
			pCellPaisEnd.setPaddingBottom(paddingBottom);
			pCellPaisEnd.setPaddingTop(paddingTop);
			pCellPaisEnd.addElement(pPaisEnd);
			pCellPaisEnd.addElement(pPaisValorEnd);
			
			PdfPCell pCellComplementoEnd = new PdfPCell();
			pCellComplementoEnd.setPaddingBottom(paddingBottom);
			pCellComplementoEnd.setPaddingTop(paddingTop);
			pCellComplementoEnd.addElement(pComplementoEnd);
			pCellComplementoEnd.addElement(pComplementoValorEnd);
			
			PdfPCell pCellReferenciaEnd = new PdfPCell();
			pCellReferenciaEnd.setPaddingBottom(paddingBottom);
			pCellReferenciaEnd.setPaddingTop(paddingTop);
			pCellReferenciaEnd.addElement(pReferenciaEnd);
			pCellReferenciaEnd.addElement(pReferenciaValorEnd);
			
			PdfPTable tb7 = new PdfPTable(new float[] {1f,0.10f, 0.25f});
			tb7.setWidthPercentage(100f);
			tb7.addCell(pCellEnderecoEnd);
			tb7.addCell(pCellNumeroEnd);
			tb7.addCell(pCellCepEnd);
			doc.add(tb7);
			
			
			PdfPTable tb8 = new PdfPTable(new float[] { 0.50f, 0.50f, 0.10f, 0.25f});
			tb8.setWidthPercentage(100f);
			tb8.addCell(pCellBairroEnd);
			tb8.addCell(pCellCidadeEnd);
			tb8.addCell(pCellUfEnd);
			tb8.addCell(pCellPaisEnd);
			doc.add(tb8);
				
			PdfPTable tb9 = new PdfPTable(new float[] {0.5f, 0.5f});
			tb9.setWidthPercentage(100f);
			tb9.addCell(pCellComplementoEnd);
			tb9.addCell(pCellReferenciaEnd);			
			doc.add(tb9);
			
			
			
			//ENDERECOS ALTERNATIVOS
			Paragraph pEnderecoEntrega = new Paragraph("ENDEREÇOS ALTERNATIVOS (ordem decrescente de cadastro)", fSubTitulo);
			pEnderecoEntrega.setAlignment(Element.ALIGN_LEFT);
			pEnderecoEntrega.setSpacingAfter(5);
			pEnderecoEntrega.setSpacingBefore(15);
			doc.add(pEnderecoEntrega);
			
			Phrase pCependAlt1 = new Phrase("CEP:",  fCampo);
			Phrase pCepValorendAlt1 = new Phrase(endAlt1.getCep(), fConteudo);
			
			Phrase pEnderecoCependAlt1 = new Phrase("ENDEREÇO:",  fCampo);
			Phrase pEnderecoCepValorendAlt1 = new Phrase(endAlt1.getEndereco(),fConteudo);
			
			Phrase pNumeroendAlt1 = new Phrase("NUMERO:",  fCampo);
			Phrase pNumeroValorendAlt1 = new Phrase(endAlt1.getNumero(),fConteudo);
			
			Phrase pBairroendAlt1 = new Phrase("BAIRRO:",  fCampo);
			Phrase pBairroValorendAlt1 = new Phrase(endAlt1.getBairro(),fConteudo);
			
			Phrase pCidadeendAlt1= new Phrase("CIDADE:",  fCampo);
			Phrase pCidadeValorendAlt1 = new Phrase(endAlt1.getCidade(),fConteudo);
			
			Phrase pUFendAlt1= new Phrase("UF:",  fCampo);
			Phrase pUfValorendAlt1 = new Phrase(endAlt1.getUf(), fConteudo);
			
			Phrase pPaisendAlt1= new Phrase("PAÍS:",  fCampo);
			Phrase pPaisValorendAlt1 = new Phrase(endAlt1.getPais(),fConteudo);
			
			Phrase pComplementoendAlt1= new Phrase("COMPLEMENTO:",  fCampo);
			Phrase pComplementoValorendAlt1 = new Phrase(endAlt1.getComplemento(),fConteudo);
			
			Phrase pReferenciaendAlt1= new Phrase("REFERÊNCIA:",  fCampo);
			Phrase pReferenciaValorendAlt1 = new Phrase(endAlt1.getReferencia(), fConteudo);
			
			PdfPCell pCellCependAlt1 = new PdfPCell();
			pCellCependAlt1.setPaddingBottom(paddingBottom);
			pCellCependAlt1.setPaddingTop(paddingTop);
			pCellCependAlt1.addElement(pCependAlt1);
			pCellCependAlt1.addElement(pCepValorendAlt1);
			
			PdfPCell pCellEnderecoendAlt1 = new PdfPCell();
			pCellEnderecoendAlt1.setPaddingBottom(paddingBottom);
			pCellEnderecoendAlt1.setPaddingTop(paddingTop);
			pCellEnderecoendAlt1.addElement(pEnderecoCependAlt1);
			pCellEnderecoendAlt1.addElement(pEnderecoCepValorendAlt1);
			
			PdfPCell pCellNumeroendAlt1 = new PdfPCell();
			pCellNumeroendAlt1.setPaddingBottom(paddingBottom);
			pCellNumeroendAlt1.setPaddingTop(paddingTop);
			pCellNumeroendAlt1.addElement(pNumeroendAlt1);
			pCellNumeroendAlt1.addElement(pNumeroValorendAlt1);

			PdfPCell pCellBairroendAlt1= new PdfPCell();
			pCellBairroendAlt1.setPaddingBottom(paddingBottom);
			pCellBairroendAlt1.setPaddingTop(paddingTop);
			pCellBairroendAlt1.addElement(pBairroendAlt1);
			pCellBairroendAlt1.addElement(pBairroValorendAlt1);
			
			PdfPCell pCellCidadeendAlt1= new PdfPCell();
			pCellCidadeendAlt1.setPaddingBottom(paddingBottom);
			pCellCidadeendAlt1.setPaddingTop(paddingTop);
			pCellCidadeendAlt1.addElement(pCidadeendAlt1);
			pCellCidadeendAlt1.addElement(pCidadeValorendAlt1);			
			
			PdfPCell pCellUfendAlt1 = new PdfPCell();
			pCellUfendAlt1.setPaddingBottom(paddingBottom);
			pCellUfendAlt1.setPaddingTop(paddingTop);
			pCellUfendAlt1.addElement(pUFendAlt1);
			pCellUfendAlt1.addElement(pUfValorendAlt1);
			
			PdfPCell pCellPaisendAlt1 = new PdfPCell();
			pCellPaisendAlt1.setPaddingBottom(paddingBottom);
			pCellPaisendAlt1.setPaddingTop(paddingTop);
			pCellPaisendAlt1.addElement(pPaisendAlt1);
			pCellPaisendAlt1.addElement(pPaisValorendAlt1);
			
			PdfPCell pCellComplementoendAlt1 = new PdfPCell();
			pCellComplementoendAlt1.setPaddingBottom(paddingBottom);
			pCellComplementoendAlt1.setPaddingTop(paddingTop);
			pCellComplementoendAlt1.addElement(pComplementoendAlt1);
			pCellComplementoendAlt1.addElement(pComplementoValorendAlt1);
			
			PdfPCell pCellReferenciaendAlt1 = new PdfPCell();
			pCellReferenciaendAlt1.setPaddingBottom(paddingBottom);
			pCellReferenciaendAlt1.setPaddingTop(paddingTop);
			pCellReferenciaendAlt1.addElement(pReferenciaendAlt1);
			pCellReferenciaendAlt1.addElement(pReferenciaValorendAlt1);
			
			PdfPTable tb10 = new PdfPTable(new float[] {1f,0.10f, 0.25f});
			tb10.setWidthPercentage(100f);
			tb10.addCell(pCellEnderecoendAlt1);
			tb10.addCell(pCellNumeroendAlt1);
			tb10.addCell(pCellCependAlt1);
			doc.add(tb10);
			
			
			PdfPTable tb11 = new PdfPTable(new float[] {0.50f, 0.50f, 0.10f, 0.25f});
			tb11.setWidthPercentage(100f);
			tb11.addCell(pCellBairroendAlt1);
			tb11.addCell(pCellCidadeendAlt1);
			tb11.addCell(pCellUfendAlt1);
			tb11.addCell(pCellPaisendAlt1);
			doc.add(tb11);
			
			
			PdfPTable tb12 = new PdfPTable(new float[] { 0.5f, 0.5f});
			tb12.setWidthPercentage(100f);
			tb12.addCell(pCellComplementoendAlt1);
			tb12.addCell(pCellReferenciaendAlt1);		
			tb12.setSpacingAfter(20);
			doc.add(tb12);
			
			//ENDEREÇO ALTERNATIVO 2
			Phrase pCependAlt2 = new Phrase("CEP:",  fCampo);
			Phrase pCepValorendAlt2= new Phrase(endAlt2.getCep(), fConteudo);
			
			Phrase pEnderecoCependAlt2= new Phrase("ENDEREÇO:",  fCampo);
			Phrase pEnderecoCepValorendAlt2= new Phrase(endAlt2.getEndereco(), fConteudo);
			
			Phrase pNumeroendAlt2= new Phrase("NUMERO:",  fCampo);
			Phrase pNumeroValorendAlt2= new Phrase(endAlt2.getNumero(), fConteudo);
			
			Phrase pBairroendAlt2= new Phrase("BAIRRO:",  fCampo);
			Phrase pBairroValorendAlt2= new Phrase(endAlt2.getBairro(), fConteudo);
			
			Phrase pCidadeendAlt2= new Phrase("CIDADE:",  fCampo);
			Phrase pCidadeValorendAlt2= new Phrase(endAlt2.getCidade(), fConteudo);
			
			Phrase pUFendAlt2= new Phrase("UF:",  fCampo);
			Phrase pUfValorendAlt2= new Phrase(endAlt2.getUf(),fConteudo);
			
			Phrase pPaisendAlt2= new Phrase("PAÍS:",  fCampo);
			Phrase pPaisValorendAlt2= new Phrase(endAlt2.getPais(),fConteudo);
			
			Phrase pComplementoendAlt2= new Phrase("COMPLEMENTO:",  fCampo);
			Phrase pComplementoValorendAlt2= new Phrase(endAlt2.getComplemento(), fConteudo);
			
			Phrase pReferenciaendAlt2= new Phrase("REFERÊNCIA:",  fCampo);
			Phrase pReferenciaValorendAlt2 = new Phrase(endAlt2.getReferencia(), fConteudo);
			
			PdfPCell pCellCependAlt2 = new PdfPCell();
			pCellCependAlt2.setPaddingBottom(paddingBottom);
			pCellCependAlt2.setPaddingTop(paddingTop);
			pCellCependAlt2.addElement(pCependAlt2);
			pCellCependAlt2.addElement(pCepValorendAlt2);
			
			PdfPCell pCellEnderecoendAlt2 = new PdfPCell();
			pCellEnderecoendAlt2.setPaddingBottom(paddingBottom);
			pCellEnderecoendAlt2.setPaddingTop(paddingTop);
			pCellEnderecoendAlt2.addElement(pEnderecoCependAlt2);
			pCellEnderecoendAlt2.addElement(pEnderecoCepValorendAlt2);
			
			PdfPCell pCellNumeroendAlt2 = new PdfPCell();
			pCellNumeroendAlt2.setPaddingBottom(paddingBottom);
			pCellNumeroendAlt2.setPaddingTop(paddingTop);
			pCellNumeroendAlt2.addElement(pNumeroendAlt2);
			pCellNumeroendAlt2.addElement(pNumeroValorendAlt2);

			PdfPCell pCellBairroendAlt2= new PdfPCell();
			pCellBairroendAlt2.setPaddingBottom(paddingBottom);
			pCellBairroendAlt2.setPaddingTop(paddingTop);
			pCellBairroendAlt2.addElement(pBairroendAlt2);
			pCellBairroendAlt2.addElement(pBairroValorendAlt2);
			
			PdfPCell pCellCidadeendAlt2= new PdfPCell();
			pCellCidadeendAlt2.setPaddingBottom(paddingBottom);
			pCellCidadeendAlt2.setPaddingTop(paddingTop);
			pCellCidadeendAlt2.addElement(pCidadeendAlt2);
			pCellCidadeendAlt2.addElement(pCidadeValorendAlt2);			
			
			PdfPCell pCellUfendAlt2 = new PdfPCell();
			pCellUfendAlt2.setPaddingBottom(paddingBottom);
			pCellUfendAlt2.setPaddingTop(paddingTop);
			pCellUfendAlt2.addElement(pUFendAlt2);
			pCellUfendAlt2.addElement(pUfValorendAlt2);
			
			PdfPCell pCellPaisendAlt2 = new PdfPCell();
			pCellPaisendAlt2.setPaddingBottom(paddingBottom);
			pCellPaisendAlt2.setPaddingTop(paddingTop);
			pCellPaisendAlt2.addElement(pPaisendAlt2);
			pCellPaisendAlt2.addElement(pPaisValorendAlt2);
			
			PdfPCell pCellComplementoendAlt2 = new PdfPCell();
			pCellComplementoendAlt2.setPaddingBottom(paddingBottom);
			pCellComplementoendAlt2.setPaddingTop(paddingTop);
			pCellComplementoendAlt2.addElement(pComplementoendAlt2);
			pCellComplementoendAlt2.addElement(pComplementoValorendAlt2);
			
			PdfPCell pCellReferenciaendAlt2= new PdfPCell();
			pCellReferenciaendAlt2.setPaddingBottom(paddingBottom);
			pCellReferenciaendAlt2.setPaddingTop(paddingTop);
			pCellReferenciaendAlt2.addElement(pReferenciaendAlt2);
			pCellReferenciaendAlt2.addElement(pReferenciaValorendAlt2);
			
			PdfPTable tb13 = new PdfPTable(new float[] {1f,0.10f, 0.25f});
			tb13.setWidthPercentage(100f);
			tb13.addCell(pCellEnderecoendAlt2);
			tb13.addCell(pCellNumeroendAlt2);
			tb13.addCell(pCellCependAlt2);
			doc.add(tb13);
			
			
			PdfPTable tb14 = new PdfPTable(new float[] {0.50f, 0.50f, 0.10f, 0.25f});
			tb14.setWidthPercentage(100f);
			tb14.addCell(pCellBairroendAlt2);
			tb14.addCell(pCellCidadeendAlt2);
			tb14.addCell(pCellUfendAlt2);
			tb14.addCell(pCellPaisendAlt2);
			doc.add(tb14);
			
			
			PdfPTable tb15 = new PdfPTable(new float[] { 0.5f, 0.5f});
			tb15.setWidthPercentage(100f);
			tb15.addCell(pCellComplementoendAlt2);
			tb15.addCell(pCellReferenciaendAlt2);			
			doc.add(tb15);
			

			//OUTRAS INFORMAÇÕES
			Paragraph pOutrasInfo = new Paragraph("OUTRAS INFORMAÇÕES", fSubTitulo);
			pOutrasInfo.setAlignment(Element.ALIGN_LEFT);
			pOutrasInfo.setSpacingAfter(5);
			pOutrasInfo.setSpacingBefore(15);
			doc.add(pOutrasInfo);

		
			Phrase pVendedor = new Phrase("VENDEDOR:", fCampo);
			Phrase pVendedorValor = new Phrase(cliente.getTransportadora_padrao(), fConteudo);
			
			Phrase pTransportadoraPadrao = new Phrase("TRANSPORTADORA PADRÃO:", fCampo);
			Phrase pTransportadoraPadraoValor = new Phrase(cliente.getTransportadora_padrao(), fConteudo);
			
			Phrase pTabelaPrecoPadrao = new Phrase("TABELA DE PREÇO PADRÃO:", fCampo);
			Phrase pTabelaPrecoPadraoValor = new Phrase(tabePrecPadr.getNome(),fConteudo);
			
			Phrase pPrimeiraCompra = new Phrase("PRIMEIRA COMPRA:", fCampo);
			
			Phrase pPrimeiraCompraValor = null;
			if(primeiraCompra != null && !primeiraCompra.equals("Nao Possui Compra!")){
				pPrimeiraCompraValor = new Phrase(dtUtil.parseDataBra(primeiraCompra), fConteudo);				
			}
			
			Phrase pUltimaCompra = new Phrase("ULTIMA COMPRA:", fCampo);

			Phrase pUltimaCompraValor = null;
			if(ultimaCompra != null && !ultimaCompra.equals("Nao Possui Compra!")){
				pUltimaCompraValor = new Phrase(dtUtil.parseDataBra(ultimaCompra), fConteudo);			
			}
			
			Paragraph pLimiteCredito = new Paragraph("LIMITE DE CRÉDITO:", fCampo);			
			Paragraph pLimiteCreditoValor = new Paragraph();
			if(creditoClient.getLimite_credito().equals(" ")){
				pLimiteCreditoValor = new Paragraph(" ", fConteudo);
				pLimiteCreditoValor.setAlignment(Element.ALIGN_RIGHT);
			}else{
				pLimiteCreditoValor = new Paragraph(Real.formatDbToString(String.valueOf(creditoClient.getLimite_credito())), fConteudo);
				pLimiteCreditoValor.setAlignment(Element.ALIGN_RIGHT);
			}
			
			Paragraph pSaldo = new Paragraph("DISPONÍVEL:", fCampo);
			Paragraph pSaldoValor = new Paragraph();
			if(creditoClient.getSaldo().equals(" ")){
				pSaldoValor = new Paragraph(" ", fConteudo);				
				pSaldoValor.setAlignment(Element.ALIGN_RIGHT);
			}else{
				pSaldoValor = new Paragraph(Real.formatDbToString(String.valueOf(creditoClient.getSaldo())), fConteudo);								
				pSaldoValor.setAlignment(Element.ALIGN_RIGHT);
			}
			
			
			
			PdfPCell pCellVendedor = new PdfPCell();
			pCellVendedor.setPaddingBottom(paddingBottom);
			pCellVendedor.setPaddingTop(paddingTop);
			pCellVendedor.addElement(pVendedor);
			pCellVendedor.addElement(pVendedorValor);
			
			PdfPCell pCellTransportadoraPadrao = new PdfPCell();
			pCellTransportadoraPadrao.setPaddingBottom(paddingBottom);
			pCellTransportadoraPadrao.setPaddingTop(paddingTop);
			pCellTransportadoraPadrao.addElement(pTransportadoraPadrao);
			pCellTransportadoraPadrao.addElement(pTransportadoraPadraoValor);
			
			PdfPCell pCellTabelaPrecoPadrao = new PdfPCell();
			pCellTabelaPrecoPadrao.setPaddingBottom(paddingBottom);
			pCellTabelaPrecoPadrao.setPaddingTop(paddingTop);
			pCellTabelaPrecoPadrao.addElement(pTabelaPrecoPadrao);
			pCellTabelaPrecoPadrao.addElement(pTabelaPrecoPadraoValor);
			
			PdfPCell pCellPrimeiraCompra = new PdfPCell();
			pCellPrimeiraCompra.setPaddingBottom(paddingBottom);
			pCellPrimeiraCompra.setPaddingTop(paddingTop);
			pCellPrimeiraCompra.addElement(pPrimeiraCompra);
			pCellPrimeiraCompra.addElement(pPrimeiraCompraValor);
			
			PdfPCell pCellUltimaCompra = new PdfPCell();
			pCellUltimaCompra.setPaddingBottom(paddingBottom);
			pCellUltimaCompra.setPaddingTop(paddingTop);
			pCellUltimaCompra.addElement(pUltimaCompra);
			pCellUltimaCompra.addElement(pUltimaCompraValor);
			
			
			PdfPCell pCellLimiteCredito = new PdfPCell();
			pCellLimiteCredito.setPaddingBottom(paddingBottom);
			pCellLimiteCredito.setPaddingTop(paddingTop);
			pCellLimiteCredito.addElement(pLimiteCredito);
			pCellLimiteCredito.addElement(pLimiteCreditoValor);
			
			PdfPCell pCellSaldo = new PdfPCell();
			pCellSaldo.setPaddingBottom(paddingBottom);
			pCellSaldo.setPaddingTop(paddingTop);
			pCellSaldo.addElement(pSaldo);
			pCellSaldo.addElement(pSaldoValor);
			
			PdfPTable tb16 = new PdfPTable(new float[] {1f, 1f, 1f});
			tb16.setWidthPercentage(100f);
			tb16.addCell(pCellVendedor);
			tb16.addCell(pCellTransportadoraPadrao);			
			tb16.addCell(pCellTabelaPrecoPadrao);			
			doc.add(tb16);
			
			PdfPTable tb17 = new PdfPTable(new float[] {1f, 1f, 1f, 1f});
			tb17.setWidthPercentage(100f);
			tb17.addCell(pCellPrimeiraCompra);
			tb17.addCell(pCellUltimaCompra);			
			tb17.addCell(pCellLimiteCredito);			
			tb17.addCell(pCellSaldo);			
//			tb17.setSpacingAfter(150);			
			doc.add(tb17);

			
//-----------HISTORICO PEDIDOS-------------------------------------------------------------------------------------------------------------------		
			List<EcfPreVendaDetalhe> resultDetalhe = null;
							
			Integer totalPedidos = 0;
			if(result != null){
				
				doc.add(Chunk.NEXTPAGE);
				doc.add(tbCab);
	
				Paragraph pPedido = new Paragraph("HISTÓRICO DE PEDIDOS", fTituloBold);
				pPedido.setAlignment(Element.ALIGN_CENTER);
				pPedido.setSpacingAfter(10);		
				doc.add(pPedido);				
				
				//TIPO		
				StringBuilder SbForm= new StringBuilder();
				StringBuilder SbVl= new StringBuilder();
				
				SbForm.append("CLIENTE:"+"\n"+"\n"+"ORDENAÇÃO:");			
				SbVl.append(cliente.getId()+" - "+cliente.getNome_razao()+"\n"+"\n"+"CÓDIGO");
	
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
				tbform.setSpacingAfter(5);
				
				doc.add(tbform);

				//FormasPgto formaPgto = new FormasPgto(); 
				
				Phrase pCodPedido = new Phrase("COD", fCampoBold);		
				Phrase pTipo = new Phrase("TIPO", fCampoBold);
				Phrase pNatOp = new Phrase("NAT.OPER", fCampoBold);
				Phrase pNatIt = new Phrase("NAT.ITENS", fCampoBold);		
				Phrase pData = new Phrase("DATA", fCampoBold);
				Phrase pHora = new Phrase("HORA", fCampoBold);
				Phrase pFormaPgt = new Phrase("FORMA DE PGTO", fCampoBold);				
				Phrase pAcresc = new Phrase("ACRESC.", fCampoBold);				
				Phrase pDesc = new Phrase("DESC.", fCampoBold);				
				Phrase pSubTotal = new Phrase("SUBTOTAL", fCampoBold);					
				Phrase pTotal = new Phrase("TOTAL", fCampoBold);					
				Phrase pPedidoVendedor = new Phrase("VENDEDOR", fCampoBold);
				Phrase pPedidoComprador = new Phrase("COMPRADOR", fCampoBold);
				Phrase pNomeStatus = new Phrase("STATUS", fCampoBold);				
				Phrase pValor = new Phrase("VALOR", fCampoBold);	
				
				PdfPCell pCellHora = new PdfPCell();
				pCellHora.addElement(pHora);
				pCellHora.setBackgroundColor(new BaseColor(114, 131, 151));			
				pCellHora.setBorderWidth(1.5f);	
				pCellHora.setBorderColor(new BaseColor(255, 255, 255));	
				
				PdfPCell pCellCod = new PdfPCell();
				pCellCod.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCod.addElement(pCodPedido);
				pCellCod.setBorderColor(new BaseColor(255, 255, 255));
				pCellCod.setBorderWidth(1.5f);
				
				PdfPCell pCellData = new PdfPCell();
				pCellData.addElement(pData);
				pCellData.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellData.setBorderColor(new BaseColor(255, 255, 255));	
				pCellData.setBorderWidth(1.5f);
				
				PdfPCell pCellTipo = new PdfPCell();
				pCellTipo.addElement(pTipo);
				pCellTipo.setBackgroundColor(new BaseColor(114, 131, 151));			
				pCellTipo.setBorderWidth(1.5f);	
				pCellTipo.setBorderColor(new BaseColor(255, 255, 255));	
				
				PdfPCell pCellNatOp = new PdfPCell();
				pCellNatOp.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellNatOp.addElement(pNatOp);
				pCellNatOp.setBorderColor(new BaseColor(255, 255, 255));
				pCellNatOp.setBorderWidth(1.5f);
				
				PdfPCell pCellNatIt = new PdfPCell();
				pCellNatIt.addElement(pNatIt);
				pCellNatIt.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellNatIt.setBorderColor(new BaseColor(255, 255, 255));	
				pCellNatIt.setBorderWidth(1.5f);
				
				PdfPCell pCellAcress = new PdfPCell();
				pCellAcress.addElement(pAcresc);
				pCellAcress.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellAcress.setBorderColor(new BaseColor(255, 255, 255));	
				pCellAcress.setBorderWidth(1.5f);
				
				PdfPCell pCellFormPgt = new PdfPCell();
				pCellFormPgt.addElement(pFormaPgt);
				pCellFormPgt.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellFormPgt.setBorderColor(new BaseColor(255, 255, 255));	
				pCellFormPgt.setBorderWidth(1.5f);
				
				PdfPCell pCellDesc = new PdfPCell();
				pCellDesc.addElement(pDesc);
				pCellDesc.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellDesc.setBorderColor(new BaseColor(255, 255, 255));	
				pCellDesc.setBorderWidth(1.5f);
				
				PdfPCell pCellPedidoSubTotal = new PdfPCell();
				pCellPedidoSubTotal.addElement(pSubTotal);
				pCellPedidoSubTotal.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellPedidoSubTotal.setBorderColor(new BaseColor(255, 255, 255));	
				pCellPedidoSubTotal.setBorderWidth(1.5f);
				
				PdfPCell pCellTotal1 = new PdfPCell();
				pCellTotal1.addElement(pTotal);
				pCellTotal1.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellTotal1.setBorderColor(new BaseColor(255, 255, 255));	
				pCellTotal1.setBorderWidth(1.5f);
					
				PdfPCell pCellPedidoVendedor= new PdfPCell();
				pCellPedidoVendedor.addElement(pPedidoVendedor);
				pCellPedidoVendedor.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellPedidoVendedor.setBorderColor(new BaseColor(255, 255, 255));	
				pCellPedidoVendedor.setBorderWidth(1.5f);

				PdfPCell pCellNomeComprador = new PdfPCell();
				pCellNomeComprador.addElement(pPedidoComprador);
				pCellNomeComprador.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellNomeComprador.setBorderColor(new BaseColor(255, 255, 255));	
				pCellNomeComprador.setBorderWidth(1.5f);
				
				PdfPCell pCellPedidoStatus= new PdfPCell();
				pCellPedidoStatus.addElement(pStatus);
				pCellPedidoStatus.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellPedidoStatus.setBorderColor(new BaseColor(255, 255, 255));	
				pCellPedidoStatus.setBorderWidth(1.5f);
				
				PdfPCell pCellPedidoValor= new PdfPCell();
				pCellPedidoValor.addElement(pValor);
				pCellPedidoValor.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellPedidoValor.setBorderColor(new BaseColor(255, 255, 255));	
				pCellPedidoValor.setBorderWidth(1.5f);
				
				PdfPTable tb18 = new PdfPTable(new float[] {0.15f, 0.20f, 0.25f, 0.25f, 0.25f, 0.25f, 0.30f, 0.25f, 0.25f, 0.25f, 0.25f, 0.30f,0.35f, 0.30f});
				tb18.setWidthPercentage(100f);
				tb18.setSpacingBefore(5);
				
				tb18.addCell(pCellCod);
				tb18.addCell(pCellData);
				tb18.addCell(pCellHora);
				tb18.addCell(pCellTipo);
				tb18.addCell(pCellNatOp);
				tb18.addCell(pCellNatIt);
				tb18.addCell(pCellNomeComprador);
				tb18.addCell(pCellPedidoVendedor);	
				tb18.addCell(pCellAcress);
				tb18.addCell(pCellPedidoSubTotal);
				tb18.addCell(pCellDesc);
				tb18.addCell(pCellTotal1);
				tb18.addCell(pCellFormPgt);
				tb18.addCell(pCellPedidoValor);
//				tb18.addCell(pCellPedidoStatus);
				doc.add(tb18);

				
				for(EcfPreVendaCabecalho pedido: result){					
					if(!pedido.getSituacao().equals("C") && !pedido.getTipo().equals("ORCAMENTO")){
						totalPedidos ++;
			
//					if(ecf.getFormaPagtoID() != null){
//						formaPgto = ecf.getFormaPagtoID();			
//					}	
					
						
					Paragraph pCodPedidoValor = new Paragraph(String.valueOf(pedido.getId()), fCampoBold);
					
					Paragraph pDataValor = new Paragraph(dtUtil.parseDataBra(pedido.getData().toString()),fCampoBold);
					
					
					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					df.format(pedido.getHora_pv());
					Date hora = pedido.getHora_pv();
					
					Paragraph pHoraValor = new Paragraph(hora.toString(), fCampoBold);
					
					
					
					Paragraph pAcrescValor = new Paragraph(pedido.getValor() != null ? Real.formatDbToString(String.valueOf(pedido.getTotal_acres())) : "", fCampoBold);
					pAcrescValor.setAlignment(Element.ALIGN_RIGHT);
					
					Paragraph pDescValor = new Paragraph(pedido.getValor() != null ? Real.formatDbToString(String.valueOf(pedido.getTotal_desc())) : "", fCampoBold);
					pDescValor.setAlignment(Element.ALIGN_RIGHT);
					
					Paragraph pSubTotalValor = new Paragraph(pedido.getValor() != null ? Real.formatDbToString(String.valueOf(pedido.getSubTotal())) : "", fCampoBold);
					pSubTotalValor.setAlignment(Element.ALIGN_RIGHT);
					
					Paragraph pTotalValor = new Paragraph(pedido.getValor() != null ? Real.formatDbToString(String.valueOf(pedido.getValor())) : "", fCampoBold);
					pTotalValor.setAlignment(Element.ALIGN_RIGHT);
					
					Paragraph pNomeCompradorValor = new Paragraph(pedido.getComprador(),fCampoBold);

					if(pedido.getComprador()!=null && !pedido.getComprador().equals("")){
					
						pNomeCompradorValor = new Paragraph(pedido.getComprador(),fCampoBold);
					}

					Paragraph pPedidoVendedorValor = new Paragraph(pedido.getVendedor(), fCampoBold);

					Paragraph pTipoValor = new Paragraph(pedido.getTipo(), fCampoBold);
					
					Paragraph pNatOpValor = new Paragraph();
					if(pedido.getNaturezaOperacao()!=null && pedido.getNaturezaOperacao().getDescricao()!=null){
						pNatOpValor = new Paragraph(pedido.getNaturezaOperacao().getDescricao(), fCampoBold);						
					}
									
					Paragraph pNatItValor = new Paragraph(pedido.getTipoVenda(), fCampoBold);
					
					Paragraph pStatusVl = new Paragraph(pedido.getSituacao(), fCampoBold);
					
					PdfPCell pCellCodCont = new PdfPCell();
					pCellCodCont.addElement(pCodPedidoValor);
					pCellCodCont.setBackgroundColor(new BaseColor(184, 191, 198));	
					pCellCodCont.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCodCont.setBorderWidth(1.5f);
					
					PdfPCell pCellTipoVl = new PdfPCell();
					pCellTipoVl.addElement(pTipoValor);
					pCellTipoVl.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellTipoVl.setBorderColor(new BaseColor(255, 255, 255));		
					pCellTipoVl.setBorderWidth(1.5f);
					
					
					PdfPCell pCellNatOpVl = new PdfPCell();
					pCellNatOpVl.addElement(pNatOpValor);
					pCellNatOpVl.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellNatOpVl.setBorderColor(new BaseColor(255, 255, 255));		
					pCellNatOpVl.setBorderWidth(1.5f);
					
					
					PdfPCell pCellNatItVl = new PdfPCell();
					pCellNatItVl.addElement(pNatItValor);
					pCellNatItVl.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellNatItVl.setBorderColor(new BaseColor(255, 255, 255));		
					pCellNatItVl.setBorderWidth(1.5f);
					
					
					PdfPCell pCellDataCont = new PdfPCell();
					pCellDataCont.addElement(pDataValor);
					pCellDataCont.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellDataCont.setBorderColor(new BaseColor(255, 255, 255));		
					pCellDataCont.setBorderWidth(1.5f);
					
					PdfPCell pCellHoraCont = new PdfPCell();
					pCellHoraCont.addElement(pHoraValor);
					pCellHoraCont.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellHoraCont.setBorderColor(new BaseColor(255, 255, 255));		
					pCellHoraCont.setBorderWidth(1.5f);
					
					PdfPCell pCellFormPgtCont = new PdfPCell();
					PdfPCell pCellPgt = new PdfPCell();			
					
					
					
					
					List<TotaisPedido> totais  = TotaisPedidoDAO.getTotais(pedido);					
					for (TotaisPedido totaisPedido : totais) {					
						pCellFormPgtCont.addElement(new Paragraph(totaisPedido.getForma_pgto() != null ? totaisPedido.getForma_pgto().getNome() : "", fCampoBold));
						
						Paragraph pValorForm = new Paragraph(Real.formatDbToString(String.valueOf(totaisPedido.getValor())), fCampoBold);
						pValorForm.setAlignment(Element.ALIGN_RIGHT);						
						pCellPgt.addElement(pValorForm);
					}					
					
					pCellFormPgtCont.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellFormPgtCont.setBorderColor(new BaseColor(255, 255, 255));		
					pCellFormPgtCont.setBorderWidth(1.5f);
					
					pCellPgt.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellPgt.setBorderColor(new BaseColor(255, 255, 255));		
					pCellPgt.setBorderWidth(1.5f);

					PdfPCell pCellAcressCont = new PdfPCell();
					pCellAcressCont.addElement(pAcrescValor);
					pCellAcressCont.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellAcressCont.setBorderColor(new BaseColor(255, 255, 255));		
					pCellAcressCont.setBorderWidth(1.5f);
					
					PdfPCell pCellDescCont = new PdfPCell();
					pCellDescCont.addElement(pDescValor);
					pCellDescCont.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellDescCont.setBorderColor(new BaseColor(255, 255, 255));		
					pCellDescCont.setBorderWidth(1.5f);
					
					PdfPCell pCellPedidoSubTotalCont = new PdfPCell();
					pCellPedidoSubTotalCont.addElement(pSubTotalValor);
					pCellPedidoSubTotalCont.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellPedidoSubTotalCont.setBorderColor(new BaseColor(255, 255, 255));		
					pCellPedidoSubTotalCont.setBorderWidth(1.5f);
					
					PdfPCell pCellTotalCont = new PdfPCell();
					pCellTotalCont.addElement(pTotalValor);
					pCellTotalCont.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellTotalCont.setBorderColor(new BaseColor(255, 255, 255));		
					pCellTotalCont.setBorderWidth(1.5f);
					
					PdfPCell pCellNomeCompradorcont = new PdfPCell();
					pCellNomeCompradorcont.addElement(pNomeCompradorValor);
					pCellNomeCompradorcont.setBackgroundColor(new BaseColor(184, 191, 198));
					pCellNomeCompradorcont.setBorderColor(new BaseColor(255, 255, 255));		
					pCellNomeCompradorcont.setBorderWidth(1.5f);
					
					PdfPCell pCellPedidoVendedorCont= new PdfPCell();
					pCellPedidoVendedorCont.addElement(pPedidoVendedorValor);
					pCellPedidoVendedorCont.setBackgroundColor(new BaseColor(184, 191, 198));					
					pCellPedidoVendedorCont.setBorderColor(new BaseColor(255, 255, 255));					
					pCellPedidoVendedorCont.setBorderWidth(1.5f);
					
					PdfPCell pCellPedidoStatusVl = new PdfPCell();
					pCellPedidoStatusVl.addElement(pStatusValor);
					pCellPedidoStatusVl.setBackgroundColor(new BaseColor(184, 191, 198));					
					pCellPedidoStatusVl.setBorderColor(new BaseColor(255, 255, 255));					
					pCellPedidoStatusVl.setBorderWidth(1.5f);
					
					
					
					PdfPCell pCellPedidoLimpo = new PdfPCell();
					pCellPedidoLimpo.addElement(new Paragraph(""));
					pCellPedidoLimpo.setBackgroundColor(new BaseColor(184, 191, 198));					
					pCellPedidoLimpo.setBorderColor(new BaseColor(255, 255, 255));					
					pCellPedidoLimpo.setBorderWidth(1.5f);
					
					PdfPTable tb19 = new PdfPTable(new float[] {0.15f, 0.20f, 0.25f, 0.25f, 0.25f, 0.25f, 0.30f, 0.25f, 0.25f, 0.25f, 0.25f, 0.30f,0.35f, 0.30f});
					tb19.setWidthPercentage(100f);
//					tb19.setSpacingAfter(2);
//					tb19.setSpacingBefore(3);
				
					
					tb19.addCell(pCellCodCont);
					tb19.addCell(pCellDataCont);
					tb19.addCell(pCellHoraCont);
					tb19.addCell(pCellTipoVl);
					tb19.addCell(pCellNatOpVl);
					tb19.addCell(pCellNatItVl);
					tb19.addCell(pCellNomeCompradorcont);
					tb19.addCell(pCellPedidoVendedorCont);	
					tb19.addCell(pCellAcressCont);
					tb19.addCell(pCellPedidoSubTotalCont);
					tb19.addCell(pCellDescCont);
					tb19.addCell(pCellTotalCont);
					tb19.addCell(pCellPedidoLimpo);
					tb19.addCell(pCellPedidoLimpo);
//					tb19.addCell(pCellPedidoStatusVl);
					doc.add(tb19);
					
					PdfPCell pCellPedidoLimpo2 = new PdfPCell();
					pCellPedidoLimpo2.addElement(new Paragraph(""));				
					pCellPedidoLimpo2.setBorderWidth(0);
				
					PdfPTable tbFormPg = new PdfPTable(new float[] {0.15f, 0.20f, 0.25f, 0.25f, 0.25f, 0.25f, 0.30f, 0.25f, 0.25f, 0.25f, 0.25f, 0.30f,0.35f, 0.30f});
					tbFormPg.setWidthPercentage(100f);
					
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellPedidoLimpo2);
					tbFormPg.addCell(pCellFormPgtCont);
					tbFormPg.addCell(pCellPgt);
					doc.add(tbFormPg);
					
					//resultado
//					for(EcfPreVendaCabecalho ecf: result){	
						Query qEcfPreVenDetalhe = em.createQuery("select ecfdeta from EcfPreVendaDetalhe ecfdeta where ecfdeta.ecfPreVendaCabecalhoId = :cod", EcfPreVendaDetalhe.class);
						qEcfPreVenDetalhe.setParameter("cod", pedido.getId());	 
						if(qEcfPreVenDetalhe.getResultList().size() > 0){
							resultDetalhe = qEcfPreVenDetalhe.getResultList();
						}
		
						Float ValorTotal = new Float(0);
						
						String nomeProduto = "";
						if(resultDetalhe!=null){
							for(EcfPreVendaDetalhe detalhe: resultDetalhe){
									
								if(detalhe.getCancelado() != null && !detalhe.getCancelado().equals("S")){
									Produto produto = new Produto();
									if(detalhe.getProdutoId() != null){
										produto = em.find(Produto.class, detalhe.getProdutoId());	
										if(produto != null){
											nomeProduto = produto.getId()+"-"+produto.getNome();											
										}else{
											nomeProduto = "";
										}
									}
									
									Paragraph pProduto = new Paragraph(nomeProduto, fCampo);
									
									Paragraph pQtdProduto = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getQuantidade())),fCampo);
									pQtdProduto.setAlignment(Element.ALIGN_RIGHT);
									Paragraph pValorUnitario = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getValorUnitario())),fCampo);
									pValorUnitario.setAlignment(Element.ALIGN_RIGHT);
									Paragraph pValorTotal = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getValorTotal())),fCampo);
									pValorTotal.setAlignment(Element.ALIGN_RIGHT);
									
									if(detalhe.getValorTotal()!=null){
										ValorTotal = ValorTotal + detalhe.getValorTotal();
									}
									
									PdfPCell pCellProduto = new PdfPCell();
									pCellProduto.addElement(pProduto);
									pCellProduto.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellProduto.setBorderColor(new BaseColor(255, 255, 255));
									pCellProduto.setBorderWidth(1.5f);
									
									PdfPCell pCellQtdproduto = new PdfPCell();
									pCellQtdproduto.addElement(pQtdProduto);
									pCellQtdproduto.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellQtdproduto.setBorderColor(new BaseColor(255, 255, 255));
									pCellQtdproduto.setBorderWidth(1.5f);
									
									PdfPCell pCellValorUnitario = new PdfPCell();
									pCellValorUnitario.addElement(pValorUnitario);
									pCellValorUnitario.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellValorUnitario.setBorderColor(new BaseColor(255, 255, 255));
									pCellValorUnitario.setBorderWidth(1.5f);
									
									PdfPCell pCellValorToTal = new PdfPCell();
									pCellValorToTal.addElement(pValorTotal);
									pCellValorToTal.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellValorToTal.setBorderColor(new BaseColor(255, 255, 255));
									pCellValorToTal.setBorderWidth(1.5f);
									
									PdfPTable tb20 = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
									tb20.setWidthPercentage(99f);
//									tb20.setSpacingBefore(1);
									tb20.addCell(pCellProduto);
									tb20.addCell(pCellQtdproduto);
									tb20.addCell(pCellValorUnitario);
									tb20.addCell(pCellValorToTal);
									doc.add(tb20);

//									}	
								}
						}
									PdfPTable tbItensTotal = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
									tbItensTotal.setWidthPercentage(99f);
									
//									Paragraph pValorTotal = new Paragraph(Real.formatDbToString(String.valueOf(ValorTotal)),fCampoBold);
//									pValorTotal.setAlignment(Element.ALIGN_RIGHT);
//							
//									PdfPCell pCellVazio = new PdfPCell();
//									pCellVazio.addElement(new Paragraph(" "));
//									pCellVazio.setBackgroundColor(new BaseColor(255, 255, 255));
//									pCellVazio.setBorderColor(new BaseColor(255, 255, 255));
//									pCellVazio.setBorderWidth(1.5f);
//									
//									PdfPCell pCellSoma = new PdfPCell();
//									pCellSoma.addElement(pValorTotal);
//									pCellSoma.setBackgroundColor(new BaseColor(232, 235, 237));
//									pCellSoma.setBorderColor(new BaseColor(255, 255, 255));
//									pCellSoma.setBorderWidth(1.5f);
//									
//									PdfPTable tb21 = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
//									tb21.setWidthPercentage(99f);
//									tb21.addCell(pCellVazio);
//									tb21.addCell(pCellVazio);
//									tb21.addCell(pCellVazio);
//									tb21.addCell(pCellSoma);
//									
//									doc.add(tb21);
//									
//									
									PdfPCell pCellItemVazio = new PdfPCell();					
									pCellItemVazio.setPaddingTop(2);
									pCellItemVazio.setPaddingBottom(4);
									pCellItemVazio.setBackgroundColor(new BaseColor(255, 255, 255));
									pCellItemVazio.setBorderColor(new BaseColor(255, 255, 255));
									pCellItemVazio.setBorderWidth(1.5f);
								
									tbItensTotal.addCell(pCellItemVazio);
									tbItensTotal.addCell(pCellItemVazio);
									tbItensTotal.addCell(pCellItemVazio);
									
									Paragraph PtotaisItens = new Paragraph(Real.formatDbToString(String.valueOf(ValorTotal)),fCampoBold);
									PtotaisItens.setAlignment(Element.ALIGN_RIGHT);
									 
									PdfPCell pCellItem = new PdfPCell(PtotaisItens);
									pCellItem.setPaddingTop(2);
									pCellItem.setPaddingBottom(4);
									pCellItem.setBackgroundColor(new BaseColor(232, 235, 237));
									pCellItem.setBorderColor(new BaseColor(255, 255, 255));
									pCellItem.setBorderWidth(1.5f);
									pCellItem.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
									
									tbItensTotal.addCell(pCellItem);
									doc.add(tbItensTotal);
							
							
							
							
						}	
					
					  }
				
				}			
				
				double dinheiro = 0;			
				double deposito = 0;
				double banco = 0;
				double cheque = 0;			
				double cartCredito = 0;			
				double cartDebito = 0;			
				double nenhuma = 0;			
				double boleto = 0;			
				double totalPago = 0;			
					
				
				Integer contDinheiro = 0;			
				Integer contdeposito = 0;
				Integer contBanco = 0;
				Integer contCheque = 0;			
				Integer contCartCredito = 0;			
				Integer contCcartDebito = 0;				
				Integer contboleto = 0;		
				Integer contNenhuma = 0;		
				
				Paragraph pResumoFormaPgt = null;
				Paragraph pResumoFormaQt = null;
				Paragraph pResumoPgtVl = null;
				
				PdfPCell pCellformaVl = new PdfPCell();
				PdfPCell pCellformaQt = new PdfPCell();
				PdfPCell pCellformaPgt = new PdfPCell();
				
				Paragraph pdinheiro = null;
				Paragraph pdeposito = null;
				Paragraph pbanco = null;
				Paragraph pcheque = null;
				Paragraph pcartCredito = null;
				Paragraph pcartDebito = null;
				Paragraph pnenhuma = null;
				Paragraph pboleto = null;
				Paragraph ptotalPago = null;
				
				Paragraph pdinheiroVl = null;
				Paragraph pdepositoVl = null;
				Paragraph pbancoVl = null;
				Paragraph pchequeVl = null;
				Paragraph pcartCreditoVl = null;
				Paragraph pcartDebitoVl = null;
				Paragraph pnenhumaVl = null;
				Paragraph pboletoVl = null;
				Paragraph ptotalPagoVl = null;
				
				Paragraph pdinheiroCont = null;
				Paragraph pdepositoCont = null;
				Paragraph pbancoCont = null;
				Paragraph pchequeCont = null;
				Paragraph pcartCreditoCont = null;
				Paragraph pcartDebitoCont = null;
				Paragraph pnenhumaCont = null;
				Paragraph pboletoCont = null;
				Paragraph ptotalPagoCont = null;

			for(EcfPreVendaCabecalho pedido: result){					
			
//				
//				Query qEcfPreVenDetalhe = em.createQuery("select ecfdeta from EcfPreVendaDetalhe ecfdeta where ecfdeta.ecfPreVendaCabecalhoId = :cod", EcfPreVendaDetalhe.class);
//				qEcfPreVenDetalhe.setParameter("cod", pedido.getId());	 
//				if(qEcfPreVenDetalhe.getResultList().size() > 0){
//					resultDetalhe = qEcfPreVenDetalhe.getResultList();
//				}
				
					//formaPgto = pedido.getFormaPagtoID();	
//				if(pedido.getValor()!=null && !pedido.getSituacao().equals("C") && pedido.getTipo().equals("PEDIDO")){
//					totalPago = totalPago + pedido.getValor();									
//				}
//					String tlPago =  Real.formatDbToString(String.valueOf(totalPago));				
											
				if(!pedido.getSituacao().equals("C") && !pedido.getTipo().equals("ORCAMENTO")){
						
					
					List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedido);
					for (TotaisPedido totaisPedido : totais) {
						
							FormasPgto formaPgto = totaisPedido.getForma_pgto();
						
							if(formaPgto != null && pedido.getValor() != null && !pedido.getValor().equals(0.0) && !pedido.getValor().equals("")){
								
								totalPago = totalPago + pedido.getValor();
								
								if(formaPgto.getNome().equals("DINHEIRO")){
									contDinheiro++;
									dinheiro = dinheiro + totaisPedido.getValor();	
									pdinheiro = new Paragraph(formaPgto.getNome(),fCab);
									pdinheiroCont = new Paragraph(contDinheiro.toString(),fCab);
									pdinheiroVl = new Paragraph(Real.formatDbToString(String.valueOf(dinheiro)),fSubTitulo);
								
								}
								if(formaPgto.getNome().equals("BANCO")){
									contBanco++;						
									banco = banco + totaisPedido.getValor();
									pbanco = new Paragraph(formaPgto.getNome(),fCab);
									pbancoCont = new Paragraph(contBanco.toString(),fCab);
									pbancoVl = new Paragraph(Real.formatDbToString(String.valueOf(contBanco)),fSubTitulo);							
								}
								if(formaPgto.getNome().equals("CHEQUE")){
									contCheque++;							
									cheque = cheque + totaisPedido.getValor();
									pcheque = new Paragraph(formaPgto.getNome(),fCab);
									pchequeCont = new Paragraph(contCheque.toString(),fCab);
									pchequeVl = new Paragraph(Real.formatDbToString(String.valueOf(cheque)),fSubTitulo);
								
								}
								if(formaPgto.getNome().equals("CARTAO CREDITO")){
									contCartCredito++;						
									cartCredito = cartCredito + totaisPedido.getValor();
									pcartCredito = new Paragraph(formaPgto.getNome(),fCab);
									pcartCreditoCont = new Paragraph(contCartCredito.toString(),fCab);
									pcartCreditoVl = new Paragraph(Real.formatDbToString(String.valueOf(cartCredito)),fSubTitulo);
		
								}
								if(formaPgto.getNome().equals("CARTAO DEBITO")){
									contCcartDebito++;						
									cartDebito = cartDebito + totaisPedido.getValor();
									pcartDebito = new Paragraph(formaPgto.getNome(),fCab);
									pcartDebitoCont = new Paragraph(contCcartDebito.toString(),fCab);
									pcartDebitoVl = new Paragraph(Real.formatDbToString(String.valueOf(cartDebito)),fSubTitulo);
								}	
								if(formaPgto.getNome().equals("DEPOSITO")){
									contdeposito++;							
									deposito = deposito + pedido.getValor();
									pdeposito = new Paragraph(formaPgto.getNome(),fCab);
									pdepositoCont = new Paragraph(contdeposito.toString(),fCab);
									pdepositoVl = new Paragraph(Real.formatDbToString(String.valueOf(deposito)),fSubTitulo);
								}			
								if(formaPgto.getNome().equals("BOLETO")){
									contboleto++;						
									boleto = boleto + totaisPedido.getValor();
									pboleto = new Paragraph(formaPgto.getNome(),fCab);
									pboletoCont = new Paragraph(contboleto.toString(),fCab);
									pboletoVl = new Paragraph(Real.formatDbToString(String.valueOf(boleto)),fSubTitulo);
								}	
								if(formaPgto.getNome().equals("NENHUMA")){
									contNenhuma++;						
									nenhuma = nenhuma + totaisPedido.getValor();					
									pnenhuma = new Paragraph(formaPgto.getNome(),fCab);
									pnenhumaCont = new Paragraph(contNenhuma.toString(),fCab);
									pnenhumaVl = new Paragraph(Real.formatDbToString(String.valueOf(nenhuma)),fSubTitulo);
								}
							}						
					
					}
					}	
				}
				Paragraph pRegistro = new Paragraph("   "+String.valueOf(totalPedidos)+" Registros encontrados",new Font(FontFamily.HELVETICA, 4));
				doc.add(pRegistro);
			
				Paragraph pResumolVl = null;
			
				Paragraph pResumo =  new Paragraph("RESUMO                                           FORMA DE PAG",fSubTitulo);
				pResumo.setSpacingAfter(10);	
				pResumo.setSpacingBefore(20);
				doc.add(pResumo);   

				
				PdfPCell pCellResumo = new PdfPCell();				
				PdfPCell pCellResumoVl = new PdfPCell();		
				PdfPCell pCellResumoVazioPgt = new PdfPCell();
				
				if(pdinheiro!=null){
					pdinheiroCont.setAlignment(Element.ALIGN_RIGHT);	
					pdinheiroVl.setAlignment(Element.ALIGN_RIGHT);	
					pCellResumo.addElement(pdinheiro);	
					pCellResumoVl.addElement(pdinheiroCont);	
					pCellResumoVazioPgt.addElement(pdinheiroVl);	
				}
				if(pbanco!=null){
					pbancoVl.setAlignment(Element.ALIGN_RIGHT);	
					pbancoCont.setAlignment(Element.ALIGN_RIGHT);	
					pCellResumo.addElement(pbanco);					
					pCellResumoVl.addElement(pbancoCont);	
					pCellResumoVazioPgt.addElement(pbancoVl);	
					
				}
				if(pcheque!=null){
					pchequeVl.setAlignment(Element.ALIGN_RIGHT);	
					pchequeCont.setAlignment(Element.ALIGN_RIGHT);	
					pCellResumo.addElement(pcheque);					
					pCellResumoVl.addElement(pchequeCont);	
					pCellResumoVazioPgt.addElement(pchequeVl);
				}
				if(pcartCredito!=null){
					pcartCreditoCont.setAlignment(Element.ALIGN_RIGHT);	
					pcartCreditoVl.setAlignment(Element.ALIGN_RIGHT);	
					pCellResumo.addElement(pcartCredito);					
					pCellResumoVl.addElement(pcartCreditoCont);		
					pCellResumoVazioPgt.addElement(pcartCreditoVl);
				}
				if(pcartDebito!=null){
					pcartDebitoCont.setAlignment(Element.ALIGN_RIGHT);	
					pcartDebitoVl.setAlignment(Element.ALIGN_RIGHT);	
					pCellResumo.addElement(pcartDebito);					
					pCellResumoVl.addElement(pcartDebitoCont);	
					pCellResumoVazioPgt.addElement(pcartDebitoVl);
				}
				if(pdeposito!=null){
					pdepositoCont.setAlignment(Element.ALIGN_RIGHT);	
					pdepositoVl.setAlignment(Element.ALIGN_RIGHT);	
					pCellResumo.addElement(pdeposito);					
					pCellResumoVl.addElement(pdepositoCont);	
					pCellResumoVazioPgt.addElement(pdepositoVl);
				}
				if(pnenhuma!=null){
					pnenhumaCont.setAlignment(Element.ALIGN_RIGHT);	
					pnenhumaVl.setAlignment(Element.ALIGN_RIGHT);	
					pCellResumo.addElement(pnenhuma);					
					pCellResumoVl.addElement(pnenhumaCont);		
					pCellResumoVazioPgt.addElement(pnenhumaVl);
				}
				if(pboleto!=null){
					pboletoCont.setAlignment(Element.ALIGN_RIGHT);	
					pboletoVl.setAlignment(Element.ALIGN_RIGHT);	
					pCellResumo.addElement(pboleto);					
					pCellResumoVl.addElement(pboletoCont);		
					pCellResumoVazioPgt.addElement(pboletoVl);
				}

				pCellResumo.setPaddingTop(0);
				pCellResumo.setBorderWidth(0);
	
				pCellResumoVl.addElement(pResumolVl);
				pCellResumoVl.setBorderWidth(0);
	
				Paragraph pResumoVazio = new Paragraph("");
				
				PdfPCell pCellResumoVazio = new PdfPCell();
				pCellResumoVazio.addElement(pResumoVazio);
				pCellResumoVazio.setBorderWidth(0);

				Paragraph pResumoVazioPgt = null;
					
				pCellResumoVazioPgt.addElement(pResumoVazioPgt);
				pCellResumoVazioPgt.setBorderWidth(0);	
		
				PdfPTable tbResumoPgt = new PdfPTable(new float[]{0.35f,0.08f,0.25f,1f});
				tbResumoPgt.setWidthPercentage(100f);	
				tbResumoPgt.addCell(pCellResumo);
				tbResumoPgt.addCell(pCellResumoVl);
				tbResumoPgt.addCell(pCellResumoVazioPgt);
				tbResumoPgt.addCell(pCellResumoVazio);
				
				doc.add(tbResumoPgt);											
			
			
			Paragraph ptotal = new Paragraph("TOTAL:",fSubTitulo);
			ptotal.setAlignment(Element.ALIGN_LEFT);
			PdfPCell pCellTotal = new PdfPCell();
			pCellTotal.setBorderWidth(0);	
			pCellTotal.addElement(ptotal);
			
			Integer qtBoleto = contDinheiro + contdeposito + contBanco + contCheque + contCartCredito + contCcartDebito + contboleto + contNenhuma;	
			
			
			Paragraph pTotalVl = new Paragraph(qtBoleto.toString(),fSubTitulo);
			pTotalVl.setAlignment(Element.ALIGN_RIGHT);
			
			PdfPCell pCellTotalVl = new PdfPCell();
			pCellTotalVl.setBorderWidth(0);
			pCellTotalVl.addElement(pTotalVl);
			
			Paragraph pTotalPgt = new Paragraph(Real.formatDbToString(String.valueOf(totalPago)),fSubTitulo);
			pTotalPgt.setAlignment(Element.ALIGN_RIGHT);
			
			PdfPCell pCellTotalPgt = new PdfPCell();
			pCellTotalPgt.setBorderWidth(0);
			pCellTotalPgt.addElement(pTotalPgt);
			
			Paragraph pTotalVazio = new Paragraph("");		
			PdfPCell pCellTotalVazio = new PdfPCell();
			pCellTotalVazio.addElement(pTotalVazio);
			pCellTotalVazio.setBorderWidth(0);
			
			PdfPTable tbTotalPgt = new PdfPTable(new float[]{0.35f,0.08f,0.25f,1f});
			tbTotalPgt.setWidthPercentage(100f);	
			tbTotalPgt.addCell(pCellTotal);
			tbTotalPgt.addCell(pCellTotalVl);
			tbTotalPgt.addCell(pCellTotalPgt);
			tbTotalPgt.addCell(pCellTotalVazio);
			tbTotalPgt.setSpacingBefore(10);
			doc.add(tbTotalPgt);								
		}
 			
				
//----------HISTÓRICO DE FINACEIRO -----------------------------------------------------------------------------------------------------------		
			
			if(contReceber!=null){
				doc.add(Chunk.NEXTPAGE);
				doc.add(tbCab);
				
				//HISTORICO FINACEIRO
				Paragraph pfinacero = new Paragraph("HISTÓRICO FINANCEIRO", fTituloBold);
				pfinacero.setAlignment(Element.ALIGN_CENTER);
				pfinacero.setSpacingAfter(10);		
				doc.add(pfinacero);

				
				//TIPO		
				StringBuilder SbForm= new StringBuilder();
				StringBuilder SbVl= new StringBuilder();
				
				SbForm.append("CLIENTE:"+"\n"+"\n"+"ORDENAÇÃO:");			
				SbVl.append(cliente.getId()+" - "+cliente.getNome_razao()+"\n"+"\n"+"VENCIMENTO");
	
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
				
						
				float vlBoletoAberto = 0;
				float vlBoletoPago = 0;
				float vlBoletoVencido = 0;
				Integer boletosAbertos = 0;
				Integer boletosFechado = 0;
				Integer boletosVencido = 0;
				Integer diasEmAtrazo   = 0;
				Float mediaDiasATZ = null;		
				Integer dias1 = 0;
				Integer dias2 = 0;
				Integer dias = 0;
				Integer totalDeBoletos = 0;
				
				
				if(contReceber != null){
					
					 final Paragraph pHitoricoCod = new Paragraph("COD", fCampoBold);
					 pHitoricoCod.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoNDoc = new Paragraph("Nº DOC.", fCampoBold);
					 pHitoricoNDoc.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricontrole = new Paragraph("CONTROLE", fCampoBold);
					 pHitoricontrole.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoNossoN = new Paragraph("NOSSO Nº", fCampoBold);
					 pHitoricoNossoN.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoEmissao = new Paragraph("EMISSÃO", fCampoBold);
					 pHitoricoEmissao.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoVl = new Paragraph("VALOR", fCampoBold);
					 pHitoricoVl.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoVenc = new Paragraph("VENC.", fCampoBold);
					 pHitoricoVenc.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoStatus = new Paragraph("STATUS", fCampoBold);
					 pHitoricoStatus.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoValorPgt = new Paragraph("PGTO R$", fCampoBold);
					 pHitoricoValorPgt.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoDataPgt = new Paragraph("PGTO", fCampoBold);
					 pHitoricoDataPgt.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoFormaPgt = new Paragraph("FORMA PGTO", fCampoBold);
					 pHitoricoFormaPgt.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoBaixa = new Paragraph("BAIXA", fCampoBold);
					 pHitoricoBaixa.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoTipo = new Paragraph("TIPO", fCampoBold);
					 pHitoricoTipo.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoOperador = new Paragraph("OPERADOR", fCampoBold);
					 pHitoricoOperador.setAlignment(Element.ALIGN_CENTER);
					 final Paragraph pHitoricoDATZ = new Paragraph("DATZ", fCampoBold);
					 pHitoricoDATZ.setAlignment(Element.ALIGN_CENTER);
					
					 PdfPCell pCellHitoricoCod = new PdfPCell();
					 pCellHitoricoCod.addElement(pHitoricoCod);
					 pCellHitoricoCod.setPaddingTop(paddingTop);
					 pCellHitoricoCod.setPaddingBottom(4f);
					 pCellHitoricoCod.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoCod.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoCod.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoControle = new PdfPCell();
					 pCellHitoricoControle.addElement(pHitoricontrole);
					 pCellHitoricoControle.setPaddingTop(paddingTop);
	//				 pCellHitoricoControle.setPaddingBottom(paddingBottom);
					 pCellHitoricoControle.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoControle.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoControle.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoNDoc = new PdfPCell();
					 pCellHitoricoNDoc.addElement(pHitoricoNDoc);
					 pCellHitoricoNDoc.setPaddingTop(paddingTop);
	//				 pCellHitoricoNDoc.setPaddingBottom(paddingBottom);
					 pCellHitoricoNDoc.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoNDoc.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoNDoc.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoNossoN = new PdfPCell();
					 pCellHitoricoNossoN.addElement(pHitoricoNossoN);
					 pCellHitoricoNossoN.setPaddingTop(paddingTop);
	//				 pCellHitoricoNossoN.setPaddingBottom(paddingBottom);
					 pCellHitoricoNossoN.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoNossoN.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoNossoN.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoEmissaoValor = new PdfPCell();
					 pCellHitoricoEmissaoValor.addElement(pHitoricoEmissao);
					 pCellHitoricoEmissaoValor.setPaddingTop(paddingTop);
	//				 pCellHitoricoEmissaoValor.setPaddingBottom(paddingBottom);
					 pCellHitoricoEmissaoValor.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoEmissaoValor.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoEmissaoValor.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoVlValor = new PdfPCell();
	//				 pCellHitoricoVlValor.setPaddingBottom(paddingBottom);
					 pCellHitoricoVlValor.addElement(pHitoricoVl);
					 pCellHitoricoVlValor.setPaddingTop(paddingTop);
					 pCellHitoricoVlValor.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoVlValor.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoVlValor.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoVencValor = new PdfPCell();
					 pCellHitoricoVencValor.addElement(pHitoricoVenc);
					 pCellHitoricoVencValor.setPaddingTop(paddingTop);
	//				 pCellHitoricoVencValor.setPaddingBottom(paddingBottom);
					 pCellHitoricoVencValor.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoVencValor.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoVencValor.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoStatus = new PdfPCell();
					 pCellHitoricoStatus.addElement(pHitoricoStatus);
					 pCellHitoricoStatus.setPaddingTop(paddingTop);
	//				 pCellHitoricoStatus.setPaddingBottom(paddingBottom);
					 pCellHitoricoStatus.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoStatus.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoStatus.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoValorPgt = new PdfPCell();
	//				 pCellHitoricoValorPgt.setPaddingBottom(paddingBottom);
					 pCellHitoricoValorPgt.addElement(pHitoricoValorPgt);
					 pCellHitoricoValorPgt.setPaddingTop(paddingTop);
					 pCellHitoricoValorPgt.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoValorPgt.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoValorPgt.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoDataPgt = new PdfPCell();
	//				 pCellHitoricoDataPgt.setPaddingBottom(paddingBottom);
					 pCellHitoricoDataPgt.addElement(pHitoricoDataPgt);
					 pCellHitoricoDataPgt.setPaddingTop(paddingTop);
					 pCellHitoricoDataPgt.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoDataPgt.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoDataPgt.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoFormaPgt = new PdfPCell();
	//				 pCellHitoricoFormaPgt.setPaddingBottom(paddingBottom);
					 pCellHitoricoFormaPgt.addElement(pHitoricoFormaPgt);
					 pCellHitoricoFormaPgt.setPaddingTop(paddingTop);
					 pCellHitoricoFormaPgt.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoFormaPgt.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoFormaPgt.setBorderWidth(2);
					 
					 PdfPCell pCellHitoricoBaixa = new PdfPCell();
	//				 pCellHitoricoBaixa.setPaddingBottom(paddingBottom);
					 pCellHitoricoBaixa.addElement(pHitoricoBaixa);
					 pCellHitoricoBaixa.setPaddingTop(paddingTop);
					 pCellHitoricoBaixa.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoBaixa.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoBaixa.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoTipo = new PdfPCell();
	//				 pCellHitoricoTipo.setPaddingBottom(paddingBottom);
					 pCellHitoricoTipo.addElement(pHitoricoTipo);
					 pCellHitoricoTipo.setPaddingTop(paddingTop);
					 pCellHitoricoTipo.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoTipo.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoTipo.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoOperador = new PdfPCell();
	//				 pCellHitoricoOperador.setPaddingBottom(paddingBottom);
					 pCellHitoricoOperador.addElement(pHitoricoOperador);
					 pCellHitoricoOperador.setPaddingTop(paddingTop);
					 pCellHitoricoOperador.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoOperador.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoOperador.setBorderWidth(1.5f);
					 
					 PdfPCell pCellHitoricoDATZ = new PdfPCell();
	//				 pCellHitoricoDATZ.setPaddingBottom(paddingBottom);
					 pCellHitoricoDATZ.addElement(pHitoricoDATZ);
					 pCellHitoricoDATZ.setPaddingTop(paddingTop);
					 pCellHitoricoDATZ.setBackgroundColor(new BaseColor(114, 131, 151));
					 pCellHitoricoDATZ.setBorderColor(new BaseColor(255, 255, 255));	
					 pCellHitoricoDATZ.setBorderWidth(1.5f);
					 
					 PdfPTable tb21 = new PdfPTable(new float[] {0.30f,0.75f, 0.75f,0.50f, 0.45f, 0.40f, 0.45f, 0.45f, 0.50f, 0.45f, 0.70f, 0.50f, 0.40f, 0.50f, 0.25f});
					 tb21.setWidthPercentage(100f);
					 tb21.setSpacingAfter(1);
					 tb21.addCell(pCellHitoricoCod);
					 tb21.addCell(pCellHitoricoNDoc);
					 tb21.addCell(pCellHitoricoControle);
					 tb21.addCell(pCellHitoricoNossoN);
					 tb21.addCell(pCellHitoricoEmissaoValor);
					 tb21.addCell(pCellHitoricoVlValor);
					 tb21.addCell(pCellHitoricoVencValor);
					 tb21.addCell(pCellHitoricoStatus);
					 tb21.addCell(pCellHitoricoValorPgt);
					 tb21.addCell(pCellHitoricoDataPgt);
					 tb21.addCell(pCellHitoricoFormaPgt);
					 tb21.addCell(pCellHitoricoBaixa);
					 tb21.addCell(pCellHitoricoTipo);
					 tb21.addCell(pCellHitoricoOperador);
					 tb21.addCell(pCellHitoricoDATZ);	
					 
					 doc.add(tb21);
					 
					for(ContasReceber contRe: contReceber){
					 if(!contRe.getStatus().equals("EXCLUIDO")){
						 
						 totalDeBoletos ++;
						 
						 if(contRe.getStatus().equals("FECHADO")&& contRe.getValor_pagamento()!=null){
							 vlBoletoPago = vlBoletoPago+Float.parseFloat(Real.formatStringToDB(contRe.getValor_pagamento()));
							 boletosFechado ++;	
						 }
		 
						 if(contRe.getStatus().equals("ABERTO")){
							 boletosAbertos ++;		
							 vlBoletoAberto = vlBoletoAberto+Float.parseFloat(Real.formatStringToDB(contRe.getValor_titulo()));	
						 }
						 
						 DateTime dt1 = new DateTime(contRe.getData_vencimento());
						 DateTime dt2 = new DateTime(contRe.getData_pagamento());
						 
						 if(new DateTime(new Date()).compareTo(dt1) > 0 && contRe.getStatus().equals("ABERTO")){
							 boletosVencido++;
							 vlBoletoVencido = vlBoletoVencido+Float.parseFloat(Real.formatStringToDB(contRe.getValor_titulo()));	
						 }
						 
						 dias = Days.daysBetween(dt1, dt2).getDays();
						 if(dias > 0 ){	
							 dias1 = dias;
							 dias2 = dias2 + dias1;
							 diasEmAtrazo ++;				 
						 }else{
							 dias1 = 0;
						 }
						 					 
						 Paragraph pHitoricoCodValor = new Paragraph(String.valueOf(contRe.getId()), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoCodValor.setAlignment(Element.ALIGN_LEFT);
						 Paragraph pHitoricoNDocValor = new Paragraph(contRe.getN_doc(), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoNDocValor.setAlignment(Element.ALIGN_LEFT);
						 Paragraph pHitoricoControleValor = new Paragraph(contRe.getControle(), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoControleValor.setAlignment(Element.ALIGN_LEFT);
						 Paragraph pHitoricoNossoNValor = new Paragraph(contRe.getN_numero(), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoNossoNValor.setAlignment(Element.ALIGN_LEFT);
						 Paragraph pHitoricoEmissaoValor = new Paragraph(dtUtil.parseDataBra(String.valueOf(contRe.getData_emissao())), new Font(FontFamily.HELVETICA,5));
						 pHitoricoEmissaoValor.setAlignment(Element.ALIGN_LEFT);
						 Paragraph pHitoricoVlValor = new Paragraph(contRe.getValor_titulo(), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoVlValor.setAlignment(Element.ALIGN_RIGHT);
						 Paragraph pHitoricoVencValor = new Paragraph(dtUtil.parseDataBra(String.valueOf(contRe.getData_vencimento())), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoVencValor.setAlignment(Element.ALIGN_LEFT);
						 Paragraph pHitoricoStatusValor = new Paragraph(contRe.getStatus(), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoStatusValor.setAlignment(Element.ALIGN_LEFT);
						 Paragraph pHitoricoValorPgtValor = new Paragraph(contRe.getValor_pagamento(), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoValorPgtValor.setAlignment(Element.ALIGN_RIGHT);
						
						 Paragraph pHitoricoDataPgtValor = null;						 
						 if(contRe.getData_pagamento()!=null){
							 pHitoricoDataPgtValor = new Paragraph(dtUtil.parseDataBra(String.valueOf(contRe.getData_pagamento())), new Font(FontFamily.HELVETICA, 5));						 
							 pHitoricoDataPgtValor.setAlignment(Element.ALIGN_LEFT);
						 }
						 
						 Paragraph pHitoricoFormaPgtValor = new Paragraph(contRe.getForma_pgto(), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoFormaPgtValor.setAlignment(Element.ALIGN_LEFT);
						 Paragraph pHitoricoBaixaValor = new Paragraph(contRe.getTipo_baixa(), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoBaixaValor.setAlignment(Element.ALIGN_CENTER);
						 Paragraph pHitoricoTipoValor = new Paragraph(contRe.getTipo_titulo(), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoTipoValor.setAlignment(Element.ALIGN_LEFT);
						 Paragraph pHitoricoOperadorValor = new Paragraph(contRe.getOperador(), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoOperadorValor.setAlignment(Element.ALIGN_LEFT);
						 Paragraph pHitoricoDATZValor = new Paragraph(String.valueOf(dias1), new Font(FontFamily.HELVETICA, 5));
						 pHitoricoDATZValor.setAlignment(Element.ALIGN_CENTER);					 
						 PdfPCell pCellHitoricoCodConteudo = new PdfPCell();
						 
						 
						 pCellHitoricoCodConteudo.addElement(pHitoricoCodValor);
						 pCellHitoricoCodConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoCodConteudo.setPaddingBottom(4f);
						 pCellHitoricoCodConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoCodConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoCodConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoNDocConteudo = new PdfPCell();
						 pCellHitoricoNDocConteudo.addElement(pHitoricoNDocValor);
						 pCellHitoricoNDocConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoNDocConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoNDocConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoNDocConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoControleConteudo = new PdfPCell();
						 pCellHitoricoControleConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoControleConteudo.addElement(pHitoricoControleValor);
						 pCellHitoricoControleConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoControleConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoControleConteudo.setBorderWidth(1.5f);
	
						 PdfPCell pCellHitoricoNossoNConteudo = new PdfPCell();
						 pCellHitoricoNossoNConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoNossoNConteudo.addElement(pHitoricoNossoNValor);
						 pCellHitoricoNossoNConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoNossoNConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoNossoNConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoEmissaoValorConteudo = new PdfPCell();
						 pCellHitoricoEmissaoValorConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoEmissaoValorConteudo.addElement(pHitoricoEmissaoValor);
						 pCellHitoricoEmissaoValorConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoEmissaoValorConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoEmissaoValorConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoVlValorConteudo = new PdfPCell();
						 pCellHitoricoVlValorConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoVlValorConteudo.addElement(pHitoricoVlValor);
						 pCellHitoricoVlValorConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoVlValorConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoVlValorConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoVencValorConteudo = new PdfPCell();
						 pCellHitoricoVencValorConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoVencValorConteudo.addElement(pHitoricoVencValor);
						 pCellHitoricoVencValorConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoVencValorConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoVencValorConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoStatusConteudo = new PdfPCell();
						 pCellHitoricoStatusConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoStatusConteudo.addElement(pHitoricoStatusValor);
						 pCellHitoricoStatusConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoStatusConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoStatusConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoValorPgtConteudo = new PdfPCell();
						 pCellHitoricoValorPgtConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoValorPgtConteudo.addElement(pHitoricoValorPgtValor);
						 pCellHitoricoValorPgtConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoValorPgtConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoValorPgtConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoDataPgtConteudo = new PdfPCell();
						 pCellHitoricoDataPgtConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoDataPgtConteudo.addElement(pHitoricoDataPgtValor);
						 pCellHitoricoDataPgtConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoDataPgtConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoDataPgtConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoFormaPgtConteudo = new PdfPCell();
						 pCellHitoricoFormaPgtConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoFormaPgtConteudo.addElement(pHitoricoFormaPgtValor);
						 pCellHitoricoFormaPgtConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoFormaPgtConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoFormaPgtConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoBaixaConteudo = new PdfPCell();
						 pCellHitoricoBaixaConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoBaixaConteudo.addElement(pHitoricoBaixaValor);
						 pCellHitoricoBaixaConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoBaixaConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoBaixaConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoTipoConteudo = new PdfPCell();
						 pCellHitoricoTipoConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoTipoConteudo.addElement(pHitoricoTipoValor);
						 pCellHitoricoTipoConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoTipoConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoTipoConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoOperadorConteudo = new PdfPCell();
						 pCellHitoricoOperadorConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoOperadorConteudo.addElement(pHitoricoOperadorValor);
						 pCellHitoricoOperadorConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoOperadorConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoOperadorConteudo.setBorderWidth(1.5f);
						 
						 PdfPCell pCellHitoricoDATZConteudo = new PdfPCell();
						 pCellHitoricoDATZConteudo.setPaddingTop(paddingTop);
						 pCellHitoricoDATZConteudo.addElement(pHitoricoDATZValor);
						 pCellHitoricoDATZConteudo.setBackgroundColor(new BaseColor(232, 235, 237));
						 pCellHitoricoDATZConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						 pCellHitoricoDATZConteudo.setBorderWidth(1.5f);
						 
						 PdfPTable tb22 = new PdfPTable(new float[] {0.30f,0.75f, 0.75f,0.50f, 0.45f, 0.40f, 0.45f, 0.45f, 0.50f, 0.45f, 0.70f, 0.50f, 0.40f, 0.50f, 0.25f});
						 tb22.setWidthPercentage(100f);
						 tb22.addCell(pCellHitoricoCodConteudo);
						 tb22.addCell(pCellHitoricoNDocConteudo);
						 tb22.addCell(pCellHitoricoControleConteudo);
						 tb22.addCell(pCellHitoricoNossoNConteudo);
						 tb22.addCell(pCellHitoricoEmissaoValorConteudo);
						 tb22.addCell(pCellHitoricoVlValorConteudo);
						 tb22.addCell(pCellHitoricoVencValorConteudo);
						 tb22.addCell(pCellHitoricoStatusConteudo);
						 tb22.addCell(pCellHitoricoValorPgtConteudo);
						 tb22.addCell(pCellHitoricoDataPgtConteudo);
						 tb22.addCell(pCellHitoricoFormaPgtConteudo);
						 tb22.addCell(pCellHitoricoBaixaConteudo);
						 tb22.addCell(pCellHitoricoTipoConteudo);
						 tb22.addCell(pCellHitoricoOperadorConteudo);
						 tb22.addCell(pCellHitoricoDATZConteudo);
						 
						 doc.add(tb22);						 
					 }						
					}
					
					Paragraph pRegistro = new Paragraph("   "+String.valueOf(totalDeBoletos)+" Registros encontrados",new Font(FontFamily.HELVETICA, 4));
					doc.add(pRegistro);
					
					double d = 0;
					double m = 0;
					double mediaDiasAT = 0;
					
					if(diasEmAtrazo == 0){
						mediaDiasATZ = (float) 0;
					}else{
					d =	Double.parseDouble(String.valueOf(dias2));
					m =	Double.parseDouble(String.valueOf(boletosFechado));
						
						mediaDiasAT = (d/m);
					}
	
					Paragraph pHResumo = new Paragraph("RESUMO:", fSubTitulo);
					pHResumo.setSpacingBefore(30);
					pHResumo.setSpacingAfter(10);
					pHResumo.setAlignment(Element.ALIGN_LEFT);
					doc.add(pHResumo);
					
					
					StringBuilder Sbresumo= new StringBuilder();
					StringBuilder Sbresumovl= new StringBuilder();
					
					Sbresumo.append("BOLETOS A RECEBER (R$):"+"\n"+"BOLETOS PAGOS (R$):"+"\n"+"BOLETOS VENCIDOS (R$):"+"\n"+"MÉDIAS DE DIAS EM ATRAZO:");			
					Sbresumovl.append(Real.formatDbToString(String.valueOf(vlBoletoAberto))+"\n"+Real.formatDbToString(String.valueOf(vlBoletoPago))+"\n"+
					Real.formatDbToString(String.valueOf(vlBoletoVencido))+"\n"+Real.formatDbToString(String.valueOf(mediaDiasAT)));
					
					Paragraph resumo = new Paragraph(Sbresumo.toString(),fCab);
					resumo.setAlignment(Element.ALIGN_LEFT);
					PdfPCell pCellresumo = new PdfPCell();
					pCellresumo.setPaddingLeft(0);
					pCellresumo.setBorderWidth(0);
					pCellresumo.addElement(resumo);		
					
					Paragraph resumoVl = new Paragraph(Sbresumovl.toString(),fSubTitulo);
					resumoVl.setAlignment(Element.ALIGN_RIGHT);
					PdfPCell pCellresumoVl = new PdfPCell();
					pCellresumoVl.setBorderWidth(0);
					pCellresumoVl.addElement(resumoVl);
					
					Paragraph vazio = new Paragraph(" ");
					PdfPCell pCellrvazio = new PdfPCell();
					pCellrvazio.setBorderWidth(0);
					pCellrvazio.addElement(vazio);
					
					PdfPTable tbResumo = new PdfPTable(new float[]{0.40f,0.10f,1f});
					tbResumo.setWidthPercentage(100f);	
					tbResumo.addCell(pCellresumo);
					tbResumo.addCell(pCellresumoVl);
					tbResumo.addCell(pCellrvazio);
					tbResumo.setSpacingAfter(10);
					
					doc.add(tbResumo);
		
				}else{
					Paragraph pNaoPossuiDados = new Paragraph("NÃO POSSUI HISTÓRICO FINANCEIRO BOLETO",fCampo);
					doc.add(pNaoPossuiDados);			
				}
	
			}	
			
//----------HISTÓRICO DE ACESSO -----------------------------------------------------------------------------------------------------------
			CredenciaisAcessoDAO caDAO = new CredenciaisAcessoDAO();
			List<AcessoCliente> ac = caDAO.buscarAcessoByCliente(codCliente);
			
			

			long  Up = 0;
			long  down = 0;
			long  temp = 0;
			
			if(ac!=null){
				
				for(AcessoCliente acesso: ac){
						
					Query q = em.createNativeQuery("SELECT * FROM radacct WHERE username = :username and acctstarttime > DATE_SUB(now(), INTERVAL 3 MONTH) ORDER BY acctstarttime DESC", RadAcct.class);					
					q.setParameter("username", acesso.getLogin());

					if(q.getResultList().size()==0){
						q = em.createNativeQuery("SELECT * FROM radacct WHERE username = :username and acctstarttime > DATE_SUB(now(), INTERVAL 24 MONTH) ORDER BY acctstarttime DESC", RadAcct.class);					
						q.setParameter("username", acesso.getLogin());
					}

					
					List<RadAcct> rad = null;			
					if(q.getResultList().size()>0){
						rad = q.getResultList();
					}	
										
					if(rad!=null){
						doc.add(Chunk.NEXTPAGE);  
						doc.add(tbCab);
		
						
						
						Paragraph pAcesso = new Paragraph("HISTÓRICO DE ACESSO - ", fTituloBold);
						pAcesso.setAlignment(Element.ALIGN_RIGHT);
						PdfPCell pCellpAcesso= new PdfPCell();
						pCellpAcesso.setBorderWidth(0);
						pCellpAcesso.addElement(pAcesso);
						
						Paragraph pAcessoVl = new Paragraph("ÚLTIMOS 03 MESES", fTitulo);
						pAcessoVl.setAlignment(Element.ALIGN_LEFT);
						PdfPCell pCellpAcessoVl= new PdfPCell();
						pCellpAcessoVl.setBorderWidth(0);
						pCellpAcessoVl.addElement(pAcessoVl);
						
						PdfPTable tbAcessoTitle = new PdfPTable(new float[]{1f,1f});
						tbAcessoTitle.setWidthPercentage(100f);	
						tbAcessoTitle.addCell(pCellpAcesso);
						tbAcessoTitle.addCell(pCellpAcessoVl);
						tbAcessoTitle.setSpacingAfter(10);
						
						doc.add(tbAcessoTitle);
							
		
						//TIPO		
						StringBuilder SbFormAcesso= new StringBuilder();
						StringBuilder SbVlAcesso= new StringBuilder();
						
						String fiador = "";
						if(acesso.getFiador()!=null){					 
							fiador = acesso.getFiador().getNome_razao()+" - "+String.valueOf(acesso.getFiador().getId());
						}
						
						SbFormAcesso.append("CONTRATO:"+"\n"+"REGIME:"+"\n"+"\n"+"CLIENTE:"+"\n"+"FIADOR:"+"\n"+"\n"+"PLANO:"+"\n"+"VALOR PLANO:"+"\n"+"ENDEREÇO:"+"\n"+"STATUS:"+"\n"+"\n"+"ORDENAÇÃO:");			
						SbVlAcesso.append(acesso.getId()+" - "+acesso.getContrato().getNome()+"\n"+acesso.getRegime()+"\n"+"\n"+acesso.getCliente().getId().toString()+" - "+acesso.getCliente().getNome_razao()+"\n"+
						fiador+"\n"+"\n"+
						acesso.getPlano().getNome()+" ("+acesso.getPlano().getUpload()+"/"+acesso.getPlano().getDownload()+")"+"\n"+"R$ "+acesso.getPlano().getValor()+"\n"+
						acesso.getEndereco().getCep()+", "+acesso.getEndereco().getEndereco()+", "+acesso.getEndereco().getNumero()+" "+acesso.getEndereco().getBairro()+", "+
						acesso.getEndereco().getComplemento()+", "+acesso.getEndereco().getCidade()+" - "+acesso.getEndereco().getUf()+"\n"+
						acesso.getStatus_2()+"\n"+"\n"+"DATA FINAL SESSÃO");
			
						Paragraph formatoAcesso = new Paragraph(SbFormAcesso.toString(),fCab);
						Paragraph ordenacaoAcesso = new Paragraph(SbVlAcesso.toString(),fSubTitulo);
						
						PdfPCell pCellFormAcesso = new PdfPCell();
						pCellFormAcesso.setBorderWidth(0);
						pCellFormAcesso.addElement(formatoAcesso);		
						
						PdfPCell pCellVlAcesso = new PdfPCell();
						pCellVlAcesso.setBorderWidth(0);
						pCellVlAcesso.addElement(ordenacaoAcesso);
						
						PdfPTable tbformAcesso = new PdfPTable(new float[]{0.25f,1f});
						tbformAcesso.setWidthPercentage(100f);	
						tbformAcesso.addCell(pCellFormAcesso);
						tbformAcesso.addCell(pCellVlAcesso);
						tbformAcesso.setSpacingAfter(10);
						
						doc.add(tbformAcesso);

						//CABEÇALHO DA TABELA
						Paragraph pInicio = new Paragraph("INÍCIO SESSÃO",fCampoBold);
						PdfPCell pCellInicio = new PdfPCell();
						pCellInicio.addElement(pInicio);
						pCellInicio.setPaddingTop(paddingTop);
						pCellInicio.setBackgroundColor(new BaseColor(114, 131, 151));
						pCellInicio.setBorderColor(new BaseColor(255, 255, 255));	
						pCellInicio.setBorderWidth(1.5f);
						
						Paragraph pFinal = new Paragraph("FINAL SESSÃO",fCampoBold);
						PdfPCell pCellFinal= new PdfPCell();
						pCellFinal.addElement(pFinal);
						pCellFinal.setPaddingTop(paddingTop);
						pCellFinal.setBackgroundColor(new BaseColor(114, 131, 151));
						pCellFinal.setBorderColor(new BaseColor(255, 255, 255));	
						pCellFinal.setBorderWidth(1.5f);
						
						Paragraph pIP = new Paragraph("IP",fCampoBold);
						PdfPCell pCellIp = new PdfPCell();
						pCellIp.addElement(pIP);
						pCellIp.setPaddingTop(paddingTop);
						pCellIp.setBackgroundColor(new BaseColor(114, 131, 151));
						pCellIp.setBorderColor(new BaseColor(255, 255, 255));	
						pCellIp.setBorderWidth(1.5f);
						
						Paragraph pMac = new Paragraph("MAC",fCampoBold);
						PdfPCell pCellMac = new PdfPCell();
						pCellMac.addElement(pMac);
						pCellMac.setPaddingTop(paddingTop);
						pCellMac.setBackgroundColor(new BaseColor(114, 131, 151));
						pCellMac.setBorderColor(new BaseColor(255, 255, 255));	
						pCellMac.setBorderWidth(1.5f);
						
						Paragraph pTempoUtil = new Paragraph("TEMPO UTILIZADO",fCampoBold);
						PdfPCell pCellTempoUtil = new PdfPCell();
						pCellTempoUtil.addElement(pTempoUtil);
						pCellTempoUtil.setPaddingTop(paddingTop);
						pCellTempoUtil.setBackgroundColor(new BaseColor(114, 131, 151));
						pCellTempoUtil.setBorderColor(new BaseColor(255, 255, 255));	
						pCellTempoUtil.setBorderWidth(1.5f);
						
						Paragraph pUp = new Paragraph("UPLOAD",fCampoBold);
						PdfPCell pCellUp = new PdfPCell();
						pCellUp.addElement(pUp);
						pCellUp.setPaddingTop(paddingTop);
						pCellUp.setBackgroundColor(new BaseColor(114, 131, 151));
						pCellUp.setBorderColor(new BaseColor(255, 255, 255));	
						pCellUp.setBorderWidth(1.5f);
						
						Paragraph pDown = new Paragraph("DOWNLOAD",fCampoBold);
						PdfPCell pCellDown = new PdfPCell();
						pCellDown.addElement(pDown);
						pCellDown.setPaddingTop(paddingTop);
						pCellDown.setBackgroundColor(new BaseColor(114, 131, 151));
						pCellDown.setBorderColor(new BaseColor(255, 255, 255));	
						pCellDown.setBorderWidth(1.5f);
						
						Paragraph pCauseTerm = new Paragraph("CAUSA DO TÉRMINO",fCampoBold);
						PdfPCell pCellCauseTerm = new PdfPCell();
						pCellCauseTerm.addElement(pCauseTerm);
						pCellCauseTerm.setPaddingTop(paddingTop);
						pCellCauseTerm.setBackgroundColor(new BaseColor(114, 131, 151));
						pCellCauseTerm.setBorderColor(new BaseColor(255, 255, 255));	
						pCellCauseTerm.setBorderWidth(1.5f);
						
						PdfPTable tbCabAcesso = new PdfPTable(new float[]{0.80f,0.80f,0.65f,0.65f,0.80f,0.50f,0.50f,1f});
						tbCabAcesso.setWidthPercentage(100f);	
						tbCabAcesso.addCell(pCellInicio);
						tbCabAcesso.addCell(pCellFinal);
						tbCabAcesso.addCell(pCellIp);
						tbCabAcesso.addCell(pCellMac);
						tbCabAcesso.addCell(pCellTempoUtil);
						tbCabAcesso.addCell(pCellUp);
						tbCabAcesso.addCell(pCellDown);
						tbCabAcesso.addCell(pCellCauseTerm);
						
						doc.add(tbCabAcesso);
		
										
						Integer cont = 0;
										
						Paragraph pUPSoma = new Paragraph();
						Paragraph pUPSomaDown = new Paragraph();
						Paragraph pTempTotal = new Paragraph();
							for(RadAcct radAcct: rad){
							cont++;
								
								Paragraph pInicioVl = new Paragraph(dtUtil.parseDataHoraBra(radAcct.getAcctstarttime().toString()),fCampo);
								PdfPCell pCellInicioVl = new PdfPCell();
								pCellInicioVl.setPaddingTop(paddingTop);
								pCellInicioVl.addElement(pInicioVl);
								pCellInicioVl.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellInicioVl.setBorderColor(new BaseColor(255, 255, 255));	
								pCellInicioVl.setBorderWidth(1.5f);
								
								Paragraph pFinalVl = null;								
								if(radAcct.getAcctstoptime()!=null){
									pFinalVl = new Paragraph(dtUtil.parseDataHoraBra(radAcct.getAcctstoptime().toString()),fCampo);								
								}
								PdfPCell pCellFinalVl = new PdfPCell();
								pCellFinalVl.setPaddingTop(paddingTop);
								pCellFinalVl.addElement(pFinalVl);
								pCellFinalVl.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellFinalVl.setBorderColor(new BaseColor(255, 255, 255));	
								pCellFinalVl.setBorderWidth(1.5f);
								
								Paragraph pIpVl = new Paragraph(radAcct.getFramedipaddress(),fCampo);
								PdfPCell pCellIplVl = new PdfPCell();
								pCellIplVl.setPaddingTop(paddingTop);
								pCellIplVl.addElement(pIpVl);
								pCellIplVl.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellIplVl.setBorderColor(new BaseColor(255, 255, 255));	
								pCellIplVl.setBorderWidth(1.5f);
								
								Paragraph pMacVl = new Paragraph(radAcct.getCallingstationid(),fCampo);
								PdfPCell pCellMaclVl = new PdfPCell();
								pCellMaclVl.setPaddingTop(paddingTop);
								pCellMaclVl.addElement(pMacVl);
								pCellMaclVl.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellMaclVl.setBorderColor(new BaseColor(255, 255, 255));	
								pCellMaclVl.setBorderWidth(1.5f);
		
								Date dt2 = new Date();
								long Second = (dt2.getTime()-radAcct.getAcctstarttime().getTime()) / 1000;
								
								Integer segundos = Integer.parseInt(String.valueOf(Second));
								
								String valor = "";
								valor = TimeUtil.formataTempo(Integer.parseInt(radAcct.getAcctsessiontime().toString()));					
								
								if(valor.equals("0d 0h 0min")){
									valor = TimeUtil.formataTempo(segundos);
								}						
								
								temp = temp + radAcct.getAcctsessiontime();
								
								Paragraph pTempoUtilVl = new Paragraph(valor,fCampo);
								PdfPCell pCellTempoUtilVl = new PdfPCell();
								pCellTempoUtilVl.setPaddingTop(paddingTop);
								pCellTempoUtilVl.addElement(pTempoUtilVl);
								pCellTempoUtilVl.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellTempoUtilVl.setBorderColor(new BaseColor(255, 255, 255));	
								pCellTempoUtilVl.setBorderWidth(1.5f);
								
								String valorUp = ByteUtil.humanReadableByteCount((long)radAcct.getAcctinputoctets(), true);			
								
								
								Up =  Up + radAcct.getAcctinputoctets();

								Paragraph pUpVl = new Paragraph(valorUp,fCampo);
								PdfPCell pCellUpVl = new PdfPCell();
								pCellUpVl.setPaddingTop(paddingTop);
								pCellUpVl.addElement(pUpVl);
								pCellUpVl.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellUpVl.setBorderColor(new BaseColor(255, 255, 255));	
								pCellUpVl.setBorderWidth(1.5f);
								
								String valorDown = ByteUtil.humanReadableByteCount((long)radAcct.getAcctoutputoctets(), true);				
								
								down =  down + radAcct.getAcctoutputoctets();

								
								
								Paragraph pDownVl = new Paragraph(valorDown,fCampo);
								PdfPCell pCellDownVl = new PdfPCell();
								pCellDownVl.setPaddingTop(paddingTop);
								pCellDownVl.addElement(pDownVl);
								pCellDownVl.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellDownVl.setBorderColor(new BaseColor(255, 255, 255));	
								pCellDownVl.setBorderWidth(1.5f);
								
								
								String valorCausa = radAcct.getAcctterminatecause();
								
								if(valorCausa.equals("User-Request")){
									valorCausa = "Usuário Requisitou";
								}else if(valorCausa.equals("Lost-Carrier")){
									valorCausa = "Conexão Perdida";
								}else if(valorCausa.equals("Lost-Service")){
									valorCausa = "Serviço foi Perdido";
								}else if(valorCausa.equals("Idle-Timeout")){
									valorCausa = "Tempo de Ociosidade Esgotou";
								}else if(valorCausa.equals("Session-Timeout")){
									valorCausa = "Tempo de Sessão Esgotou";
								}else if(valorCausa.equals("Admin-Reset")){
									valorCausa = "Resetado pelo Admin";
								}else if(valorCausa.equals("Admin-Reboot")){
									valorCausa = "Reiniciado pelo Admin";
								}else if(valorCausa.equals("Port-Error")){
									valorCausa = "Erro na Porta";
								}else if(valorCausa.equals("NAS-Error")){
									valorCausa = "Erro no Concentrador";
								}else if(valorCausa.equals("NAS-Request")){
									valorCausa = "Requisitado no Contrador";
								}else if(valorCausa.equals("NAS-Reboot")){
									valorCausa = "Reiniciado pelo Concentrador";
								}else if(valorCausa.equals("Port-Unneeded")){
									valorCausa = "Porta Desnecessária";
								}else if(valorCausa.equals("Port-Preempted")){
									valorCausa = "Porta Interrompida";
								}else if(valorCausa.equals("Port-Suspended")){
									valorCausa = "Porta Suspensa";
								}else if(valorCausa.equals("Service-Unavailable")){
									valorCausa = "Porta não Disponível";
								}else if(valorCausa.equals("Callback")){
									valorCausa = "Opus Callback";
								}else if(valorCausa.equals("User-Error")){
									valorCausa = "Erro do Usuário";
								}else if(valorCausa.equals("Host-Request")){
									valorCausa = "Requisitado pelo Host";
								}
							
													
								Paragraph pCausaVl = new Paragraph(valorCausa,fCampo);
								PdfPCell pCellCauseTermVl = new PdfPCell();
								pCellCauseTermVl.setPaddingTop(paddingTop);
								pCellCauseTermVl.addElement(pCausaVl);
								pCellCauseTermVl.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellCauseTermVl.setBorderColor(new BaseColor(255, 255, 255));	
								pCellCauseTermVl.setBorderWidth(1.5f);
		
								PdfPTable tbCabAcessoVl = new PdfPTable(new float[]{0.80f,0.80f,0.65f,0.65f,0.80f,0.50f,0.50f,1f});
								tbCabAcessoVl.setWidthPercentage(100f);	
								tbCabAcessoVl.addCell(pCellInicioVl);
								tbCabAcessoVl.addCell(pCellFinalVl);
								tbCabAcessoVl.addCell(pCellIplVl);
								tbCabAcessoVl.addCell(pCellMaclVl);
								tbCabAcessoVl.addCell(pCellTempoUtilVl);
								tbCabAcessoVl.addCell(pCellUpVl);
								tbCabAcessoVl.addCell(pCellDownVl);
								tbCabAcessoVl.addCell(pCellCauseTermVl);
								
								doc.add(tbCabAcessoVl);
							}
							
							String tempTotal = "";
							tempTotal = TimeUtil.formataTempo((int) temp).toString();
							
							
							pUPSoma = new Paragraph(ByteUtil.humanReadableByteCount((long)Up, true),fCampo);
							pUPSomaDown = new Paragraph(ByteUtil.humanReadableByteCount((long)down, true),fCampo);
							pTempTotal = new Paragraph(tempTotal,fCampo);
							
						
							Paragraph pva = new Paragraph("");
							PdfPCell pCellvazio = new PdfPCell();
							pCellvazio.setPaddingTop(paddingTop);
							pCellvazio.addElement(pva);
							pCellvazio.setBorderWidth(0);
							
							PdfPCell pCellUpT = new PdfPCell();
							pCellUpT.setPaddingTop(paddingTop);
							pCellUpT.addElement(pUPSoma);
							pCellUpT.setBackgroundColor(new BaseColor(232, 235, 237));
							pCellUpT.setBorderColor(new BaseColor(255, 255, 255));	
							pCellUpT.setBorderWidth(1.5f);
							
							PdfPCell pCellTemp = new PdfPCell();
							pCellTemp.setPaddingTop(paddingTop);
							pCellTemp.addElement(pTempTotal);
							pCellTemp.setBackgroundColor(new BaseColor(232, 235, 237));
							pCellTemp.setBorderColor(new BaseColor(255, 255, 255));	
							pCellTemp.setBorderWidth(1.5f);
							
							PdfPCell pCellDownT = new PdfPCell();
							pCellDownT.setPaddingTop(paddingTop);
							pCellDownT.addElement(pUPSomaDown);
							pCellDownT.setBackgroundColor(new BaseColor(232, 235, 237));
							pCellDownT.setBorderColor(new BaseColor(255, 255, 255));	
							pCellDownT.setBorderWidth(1.5f);
							
							PdfPTable tbCabAcessoVl2 = new PdfPTable(new float[]{0.80f,0.80f,0.65f,0.65f,0.80f,0.50f,0.50f,1f});
							tbCabAcessoVl2.setWidthPercentage(100f);	
							tbCabAcessoVl2.addCell(pCellvazio);
							tbCabAcessoVl2.addCell(pCellvazio);
							tbCabAcessoVl2.addCell(pCellvazio);
							tbCabAcessoVl2.addCell(pCellvazio);
							tbCabAcessoVl2.addCell(pCellTemp);
							tbCabAcessoVl2.addCell(pCellUpT);
							tbCabAcessoVl2.addCell(pCellDownT);
							tbCabAcessoVl2.addCell(pCellvazio);
					
							
							doc.add(tbCabAcessoVl2);
							
							Paragraph pRegistro = new Paragraph("   "+String.valueOf(cont)+" Registros encontrados",new Font(FontFamily.HELVETICA, 4));
							doc.add(pRegistro);
						}
						
//								doc.add(Chunk.NEXTPAGE);  
								
						
					}
//				}
		}
			
//----------HISTÓRICO DE CHAMADOS -----------------------------------------------------------------------------------------------------------
			List<Ose> ose = null;
			
			Query qOse = em.createQuery("select os from Ose os where os.cliente = :codCliente", Ose.class);
			qOse.setParameter("codCliente", new Cliente(cliente.getId()));			
			if(qOse.getResultList().size() > 0){
				ose = (List<Ose>) qOse.getResultList();
			}
			
			
			if(ose!=null){				
				doc.add(Chunk.NEXTPAGE);
				doc.add(tbCab);

				Paragraph pOse = new Paragraph("HISTÓRICO DE CHAMADOS", fTituloBold);
				pOse.setAlignment(Element.ALIGN_CENTER);			
				pOse.setSpacingAfter(10);			
				doc.add(pOse);
					

				//TIPO		
				StringBuilder SbFormOse= new StringBuilder();
				StringBuilder SbVlOse= new StringBuilder();
								
				SbFormOse.append("CLIENTE:"+"\n"+"\n"+"ORDENAÇÃO:");			
				SbVlOse.append(cliente.getId().toString()+" - "+cliente.getNome_razao()+"\n"+"\n"+"DATA PREVISÃO");
	
				Paragraph formatOse = new Paragraph(SbFormOse.toString(),fCab);
				Paragraph ordenacaoOse = new Paragraph(SbVlOse.toString(),fSubTitulo);
				
				PdfPCell pCellFormOse = new PdfPCell();
				pCellFormOse.setBorderWidth(0);
				pCellFormOse.addElement(formatOse);		
				
				PdfPCell pCellVlOse = new PdfPCell();
				pCellVlOse.setBorderWidth(0);
				pCellVlOse.addElement(ordenacaoOse);
				
				PdfPTable tbformAOse = new PdfPTable(new float[]{0.25f,1f});
				tbformAOse.setWidthPercentage(100f);	
				tbformAOse.addCell(pCellFormOse);
				tbformAOse.addCell(pCellVlOse);
				tbformAOse.setSpacingAfter(10);
				
				doc.add(tbformAOse);
				
				//CABEÇALHO DA TABELA
				Paragraph pOseCod = new Paragraph("OS",fCampoBold);
				PdfPCell pCellOseCod = new PdfPCell();
				pCellOseCod.addElement(pOseCod);
				pCellOseCod.setPaddingTop(paddingTop);
				pCellOseCod.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellOseCod.setBorderColor(new BaseColor(255, 255, 255));	
				pCellOseCod.setBorderWidth(1.5f);
				
				Paragraph pOseDtAb = new Paragraph("DATA ABERTURA",fCampoBold);
				PdfPCell pCellOseDtAb= new PdfPCell();
				pCellOseDtAb.addElement(pOseDtAb);
				pCellOseDtAb.setPaddingTop(paddingTop);
				pCellOseDtAb.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellOseDtAb.setBorderColor(new BaseColor(255, 255, 255));	
				pCellOseDtAb.setBorderWidth(1.5f);
				
				Paragraph pOseGrupo = new Paragraph("GRUPO",fCampoBold);
				PdfPCell pCellOseGrupo = new PdfPCell();
				pCellOseGrupo.addElement(pOseGrupo);
				pCellOseGrupo.setPaddingTop(paddingTop);
				pCellOseGrupo.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellOseGrupo.setBorderColor(new BaseColor(255, 255, 255));	
				pCellOseGrupo.setBorderWidth(1.5f);
				
				Paragraph pOseSubGrupo = new Paragraph("SUBGRUPO",fCampoBold);
				PdfPCell pCellOseSubGrupo = new PdfPCell();
				pCellOseSubGrupo.addElement(pOseSubGrupo);
				pCellOseSubGrupo.setPaddingTop(paddingTop);
				pCellOseSubGrupo.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellOseSubGrupo.setBorderColor(new BaseColor(255, 255, 255));	
				pCellOseSubGrupo.setBorderWidth(1.5f);
				
				Paragraph pOseTipo = new Paragraph("TIPO",fCampoBold);
				PdfPCell pCellOseTipo = new PdfPCell();
				pCellOseTipo.addElement(pOseTipo);
				pCellOseTipo.setPaddingTop(paddingTop);
				pCellOseTipo.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellOseTipo.setBorderColor(new BaseColor(255, 255, 255));	
				pCellOseTipo.setBorderWidth(1.5f);
				
				Paragraph pOseDtPre = new Paragraph("PREVISÃO",fCampoBold);
				PdfPCell pCellOseDtPre = new PdfPCell();
				pCellOseDtPre.addElement(pOseDtPre);
				pCellOseDtPre.setPaddingTop(paddingTop);
				pCellOseDtPre.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellOseDtPre.setBorderColor(new BaseColor(255, 255, 255));	
				pCellOseDtPre.setBorderWidth(1.5f);
				
				Paragraph pOseDtConclusao = new Paragraph("CONCLUSÃO",fCampoBold);
				PdfPCell pCellOseDtConclusao = new PdfPCell();
				pCellOseDtConclusao.addElement(pOseDtConclusao);
				pCellOseDtConclusao.setPaddingTop(paddingTop);
				pCellOseDtConclusao.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellOseDtConclusao.setBorderColor(new BaseColor(255, 255, 255));	
				pCellOseDtConclusao.setBorderWidth(1.5f);
				
				Paragraph pOseStatus = new Paragraph("STATUS",fCampoBold);
				PdfPCell pCellOseStatus = new PdfPCell();
				pCellOseStatus.addElement(pOseStatus);
				pCellOseStatus.setPaddingTop(paddingTop);
				pCellOseStatus.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellOseStatus.setBorderColor(new BaseColor(255, 255, 255));	
				pCellOseStatus.setBorderWidth(1.5f);
				
				PdfPTable tbCabOse = new PdfPTable(new float[]{0.15f,0.30f,0.30f,0.30f,0.30f,0.30f,0.30f,0.30f});
				tbCabOse.setWidthPercentage(100f);	
				tbCabOse.addCell(pCellOseCod);
				tbCabOse.addCell(pCellOseDtAb);
				tbCabOse.addCell(pCellOseGrupo);
				tbCabOse.addCell(pCellOseSubGrupo);
				tbCabOse.addCell(pCellOseTipo);
				tbCabOse.addCell(pCellOseDtPre);
				tbCabOse.addCell(pCellOseDtConclusao);
				tbCabOse.addCell(pCellOseStatus);
				
				doc.add(tbCabOse);

				
				for(Ose os: ose){
						
					//CONTEUDO DA TABELA
					Paragraph pOseCodVl = new Paragraph(os.getId().toString(),fCampo);
					PdfPCell pCellOseCodVl = new PdfPCell();
					pCellOseCodVl.addElement(pOseCodVl);
					pCellOseCodVl.setPaddingBottom(4f);
					pCellOseCodVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellOseCodVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellOseCodVl.setBorderWidth(1.5f);
					
					Paragraph pOseDtAbVl = new Paragraph(dtUtil.parseDataHoraBra(os.getData_abertura().toString()),fCampo);
					PdfPCell pCellOseDtAbVl= new PdfPCell();
					pCellOseDtAbVl.addElement(pOseDtAbVl);
					pCellOseDtAbVl.setPaddingBottom(4f);
					pCellOseDtAbVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellOseDtAbVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellOseDtAbVl.setBorderWidth(1.5f);
					
					Paragraph pOseGrupoVl = new Paragraph();						
					if(os.getGrupo()!=null){
						pOseGrupoVl = new Paragraph(os.getGrupo().getNome(),fCampo);						
					}
					PdfPCell pCellOseGrupoVl = new PdfPCell();
					pCellOseGrupoVl.addElement(pOseGrupoVl);
					pCellOseGrupoVl.setPaddingBottom(4f);
					pCellOseGrupoVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellOseGrupoVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellOseGrupoVl.setBorderWidth(1.5f);
					
					Paragraph pOseSubGrupoVl = new Paragraph();						
					if(os.getSubgrupo()!=null){
						pOseSubGrupoVl = new Paragraph(os.getSubgrupo().getNome(),fCampo);						
					}
					PdfPCell pCellOseSubGrupoVl = new PdfPCell();
					pCellOseSubGrupoVl.addElement(pOseSubGrupoVl);
					pCellOseSubGrupoVl.setPaddingBottom(4f);
					pCellOseSubGrupoVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellOseSubGrupoVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellOseSubGrupoVl.setBorderWidth(1.5f);
					
					Paragraph pOseTipoVl = new Paragraph();						
					if(os.getTipo_subgrupo()!=null){
						pOseTipoVl = new Paragraph(os.getTipo_subgrupo().getNome(),fCampo);						
					}
					PdfPCell pCellOseTipoVl = new PdfPCell();
					pCellOseTipoVl.addElement(pOseTipoVl);
					pCellOseTipoVl.setPaddingBottom(4f);
					pCellOseTipoVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellOseTipoVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellOseTipoVl.setBorderWidth(1.5f);
					
					Paragraph pOseDtPreVl = new Paragraph(dtUtil.parseDataHoraBra(os.getData_ex().toString()),fCampo);
					PdfPCell pCellOseDtPreVl = new PdfPCell();
					pCellOseDtPreVl.addElement(pOseDtPreVl);
					pCellOseDtPreVl.setPaddingBottom(4f);
					pCellOseDtPreVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellOseDtPreVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellOseDtPreVl.setBorderWidth(1.5f);
					
					Paragraph pOseDtConclusaoVl = new Paragraph();						
					if(os.getData_conclusao()!=null){
						pOseDtConclusaoVl = new Paragraph(dtUtil.parseDataHoraBra(os.getData_conclusao().toString()),fCampo);						
					}
					PdfPCell pCellOseDtConclusaoVl = new PdfPCell();
					pCellOseDtConclusaoVl.addElement(pOseDtConclusaoVl);
					pCellOseDtConclusaoVl.setPaddingBottom(4f);
					pCellOseDtConclusaoVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellOseDtConclusaoVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellOseDtConclusaoVl.setBorderWidth(1.5f);
					
					Paragraph pOseStatusVl = new Paragraph(os.getStatus(),fCampo);
					PdfPCell pCellOseStatusVl = new PdfPCell();
					pCellOseStatusVl.addElement(pOseStatusVl);
					pCellOseStatusVl.setPaddingBottom(4f);
					pCellOseStatusVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellOseStatusVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellOseStatusVl.setBorderWidth(1.5f);
					

					PdfPTable tbVlOse = new PdfPTable(new float[]{0.15f,0.30f,0.30f,0.30f,0.30f,0.30f,0.30f,0.30f});
					tbVlOse.setWidthPercentage(100f);	
					tbVlOse.addCell(pCellOseCodVl);
					tbVlOse.addCell(pCellOseDtAbVl);
					tbVlOse.addCell(pCellOseGrupoVl);
					tbVlOse.addCell(pCellOseSubGrupoVl);
					tbVlOse.addCell(pCellOseTipoVl);
					tbVlOse.addCell(pCellOseDtPreVl);
					tbVlOse.addCell(pCellOseDtConclusaoVl);
					tbVlOse.addCell(pCellOseStatusVl);
					
					doc.add(tbVlOse);
									
				}
				Paragraph pRegistro = new Paragraph("   "+String.valueOf(ose.size())+" Registros encontrados",new Font(FontFamily.HELVETICA, 4));
				doc.add(pRegistro);
			}
			
//----------HISTÓRICO DE CONTATOS -----------------------------------------------------------------------------------------------------------
			List<Crm> crm = null;
			
			Query qCrm = em.createQuery("select cr from Crm cr where cr.cliente = :codCliente", Crm.class);
			qCrm.setParameter("codCliente", new Cliente(cliente.getId()));			
			if(qCrm.getResultList().size() > 0){
				crm = (List<Crm>) qCrm.getResultList();
			}
			
			
			if(crm!=null){				
				doc.add(Chunk.NEXTPAGE);
				doc.add(tbCab);

				Paragraph pCrm = new Paragraph("HISTÓRICO DE CRM", fTituloBold);
				pCrm.setAlignment(Element.ALIGN_CENTER);			
				pCrm.setSpacingAfter(10);			
				doc.add(pCrm);
					

				//TIPO		
				StringBuilder SbFormpCrm= new StringBuilder();
				StringBuilder SbVlpCrm= new StringBuilder();
								
				SbFormpCrm.append("CLIENTE:"+"\n"+"\n"+"ORDENAÇÃO:");			
				SbVlpCrm.append(cliente.getId().toString()+" - "+cliente.getNome_razao()+"\n"+"\n"+"DATA PREVISÃO");
	
				Paragraph formatpCrm = new Paragraph(SbFormpCrm.toString(),fCab);
				Paragraph ordenacaopCrm = new Paragraph(SbVlpCrm.toString(),fSubTitulo);
				
				PdfPCell pCellFormpCrm = new PdfPCell();
				pCellFormpCrm.setBorderWidth(0);
				pCellFormpCrm.addElement(formatpCrm);		
				
				PdfPCell pCellVlpCrm = new PdfPCell();
				pCellVlpCrm.setBorderWidth(0);
				pCellVlpCrm.addElement(ordenacaopCrm);
				
				PdfPTable tbformApCrm = new PdfPTable(new float[]{0.25f,1f});
				tbformApCrm.setWidthPercentage(100f);	
				tbformApCrm.addCell(pCellFormpCrm);
				tbformApCrm.addCell(pCellVlpCrm);
				tbformApCrm.setSpacingAfter(10);
				
				doc.add(tbformApCrm);
				
				//CABEÇALHO DA TABELA
				Paragraph pCrmCod = new Paragraph("PROTOCOLO",fCampoBold);
				PdfPCell pCellCrmCod = new PdfPCell();
				pCellCrmCod.addElement(pCrmCod);
				pCellCrmCod.setPaddingTop(paddingTop);
				pCellCrmCod.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmCod.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmCod.setBorderWidth(1.5f);
				
				Paragraph pCrmDtAb = new Paragraph("ABERTURA",fCampoBold);
				PdfPCell pCellCrmDtAb= new PdfPCell();
				pCellCrmDtAb.addElement(pCrmDtAb);
				pCellCrmDtAb.setPaddingTop(paddingTop);
				pCellCrmDtAb.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmDtAb.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmDtAb.setBorderWidth(1.5f);
				
				Paragraph pCrmHr = new Paragraph("HORA",fCampoBold);
				PdfPCell pCellHr= new PdfPCell();
				pCellHr.addElement(pCrmHr);
				pCellHr.setPaddingTop(paddingTop);
				pCellHr.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellHr.setBorderColor(new BaseColor(255, 255, 255));	
				pCellHr.setBorderWidth(1.5f);
				
				Paragraph pCrmSetor = new Paragraph("SETOR",fCampoBold);
				PdfPCell pCellCrmSetor= new PdfPCell();
				pCellCrmSetor.addElement(pCrmSetor);
				pCellCrmSetor.setPaddingTop(paddingTop);
				pCellCrmSetor.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmSetor.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmSetor.setBorderWidth(1.5f);
				
				Paragraph pCrmAssunto = new Paragraph("ASSUNTO",fCampoBold);
				PdfPCell pCellCrmAssunto = new PdfPCell();
				pCellCrmAssunto.addElement(pCrmAssunto);
				pCellCrmAssunto.setPaddingTop(paddingTop);
				pCellCrmAssunto.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmAssunto.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmAssunto.setBorderWidth(1.5f);
				
				Paragraph pCrmContato = new Paragraph("CONTATO",fCampoBold);
				PdfPCell pCellCrmContato = new PdfPCell();
				pCellCrmContato.addElement(pCrmContato);
				pCellCrmContato.setPaddingTop(paddingTop);
				pCellCrmContato.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmContato.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmContato.setBorderWidth(1.5f);
				
				Paragraph pCrmPrevissao = new Paragraph("PREVISÃO",fCampoBold);
				PdfPCell pCellCrmPrevissao = new PdfPCell();
				pCellCrmPrevissao.addElement(pCrmPrevissao);
				pCellCrmPrevissao.setPaddingTop(paddingTop);
				pCellCrmPrevissao.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmPrevissao.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmPrevissao.setBorderWidth(1.5f);
				
				Paragraph pCrmDtConclusao = new Paragraph("CONCLUSÃO",fCampoBold);
				PdfPCell pCellCrmDtConclusao = new PdfPCell();
				pCellCrmDtConclusao.addElement(pCrmDtConclusao);
				pCellCrmDtConclusao.setPaddingTop(paddingTop);
				pCellCrmDtConclusao.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmDtConclusao.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmDtConclusao.setBorderWidth(1.5f);

				Paragraph pCrmTempTrat = new Paragraph("TEMPO TRATAMENTO",fCampoBold);
				PdfPCell pCellCrmTempTrat = new PdfPCell();
				pCellCrmTempTrat.addElement(pCrmTempTrat);
				pCellCrmTempTrat.setPaddingTop(paddingTop);
				pCellCrmTempTrat.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmTempTrat.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmTempTrat.setBorderWidth(1.5f);
				
				Paragraph pCrmOperador = new Paragraph("OPERADOR",fCampoBold);
				PdfPCell pCellCrmOperador = new PdfPCell();
				pCellCrmOperador.addElement(pCrmOperador);
				pCellCrmOperador.setPaddingTop(paddingTop);
				pCellCrmOperador.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmOperador.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmOperador.setBorderWidth(1.5f);
				
				Paragraph pOseStatus = new Paragraph("STATUS",fCampoBold);
				PdfPCell pCellCrmStatus = new PdfPCell();
				pCellCrmStatus.addElement(pOseStatus);
				pCellCrmStatus.setPaddingTop(paddingTop);
				pCellCrmStatus.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmStatus.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmStatus.setBorderWidth(1.5f);
				
				Paragraph pOseReagendar = new Paragraph("REAGENDAMENTO",fCampoBold);
				PdfPCell pCellCrmReagendar = new PdfPCell();
				pCellCrmReagendar.addElement(pOseReagendar);
				pCellCrmReagendar.setPaddingTop(paddingTop);
				pCellCrmReagendar.setBackgroundColor(new BaseColor(114, 131, 151));
				pCellCrmReagendar.setBorderColor(new BaseColor(255, 255, 255));	
				pCellCrmReagendar.setBorderWidth(1.5f);
			
				PdfPTable tbCabCrm = new PdfPTable(new float[]{0.18f,0.20f,0.20f,0.30f,0.30f,0.30f,0.30f,0.30f,0.30f,0.20f,0.20f,0.30f});
				tbCabCrm.setWidthPercentage(100f);	
				tbCabCrm.addCell(pCellCrmCod);
				tbCabCrm.addCell(pCellCrmDtAb);
				tbCabCrm.addCell(pCellHr);
				tbCabCrm.addCell(pCellCrmSetor);
				tbCabCrm.addCell(pCellCrmAssunto);
				tbCabCrm.addCell(pCellCrmContato);
				tbCabCrm.addCell(pCellCrmPrevissao);
				tbCabCrm.addCell(pCellCrmDtConclusao);
				tbCabCrm.addCell(pCellCrmTempTrat);
				tbCabCrm.addCell(pCellCrmOperador);
				tbCabCrm.addCell(pCellCrmStatus);
				tbCabCrm.addCell(pCellCrmReagendar);
				
				doc.add(tbCabCrm);

				
				for(Crm cr: crm){
					String dataAbertura = null;
					String horaAbertura = null;
					
					if(cr.getData_cadastro()!=null){
						dataAbertura = DataUtil.formatDateBra(cr.getData_cadastro());
						horaAbertura = DataUtil.formatHoraBra(cr.getData_cadastro());
					}

					//CONTEUDO DA TABELA
					Paragraph pCrmCodVl = new Paragraph(cr.getId().toString(),fCampo);
					PdfPCell pCellCrmCodVl = new PdfPCell();
					pCellCrmCodVl.addElement(pCrmCodVl);
					pCellCrmCodVl.setPaddingTop(paddingTop);
					pCellCrmCodVl.setPaddingTop(paddingTop);
					pCellCrmCodVl.setPaddingBottom(4f);
					pCellCrmCodVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmCodVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCrmCodVl.setBorderWidth(1.5f);
					
					Paragraph pCrmDtAbVl = new Paragraph(dataAbertura,fCampo);
					PdfPCell pCellCrmDtAbVl= new PdfPCell();
					pCellCrmDtAbVl.addElement(pCrmDtAbVl);
					pCellCrmDtAbVl.setPaddingTop(paddingTop);
					pCellCrmDtAbVl.setPaddingBottom(4f);
					pCellCrmDtAbVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmDtAbVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCrmDtAbVl.setBorderWidth(1.5f);
					
					Paragraph pCrmHrAbVl = new Paragraph(horaAbertura,fCampo);
					PdfPCell pCellCrmHrAbVl= new PdfPCell();
					pCellCrmHrAbVl.addElement(pCrmHrAbVl);
					pCellCrmHrAbVl.setPaddingTop(paddingTop);
					pCellCrmHrAbVl.setPaddingBottom(4f);
					pCellCrmHrAbVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmHrAbVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCrmHrAbVl.setBorderWidth(1.5f);
					
					Paragraph pCrmSetorVl = new Paragraph(cr.getSetor().getNome(),fCampo);
					PdfPCell pCellCrmSetorVl= new PdfPCell();
					pCellCrmSetorVl.addElement(pCrmSetorVl);
					pCellCrmSetorVl.setPaddingBottom(4f);
					pCellCrmSetorVl.setPaddingTop(paddingTop);
					pCellCrmSetorVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmSetorVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCrmSetorVl.setBorderWidth(1.5f);
					
					Paragraph pCrmAssuntoVl = new Paragraph(cr.getCrm_assuntos().getNome(),fCampo);
					PdfPCell pCellCrmAssuntoVl = new PdfPCell();
					pCellCrmAssuntoVl.addElement(pCrmAssuntoVl);
					pCellCrmAssuntoVl.setPaddingBottom(4f);
					pCellCrmAssuntoVl.setPaddingTop(paddingTop);
					pCellCrmAssuntoVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmAssuntoVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCrmAssuntoVl.setBorderWidth(1.5f);
					
					Paragraph pCrmContatoVl = new Paragraph(cr.getContato(),fCampo);
					PdfPCell pCellCrmContatoVl = new PdfPCell();
					pCellCrmContatoVl.addElement(pCrmContatoVl);
					pCellCrmContatoVl.setPaddingBottom(4f);
					pCellCrmContatoVl.setPaddingTop(paddingTop);
					pCellCrmContatoVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmContatoVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCrmContatoVl.setBorderWidth(1.5f);	
					
					Paragraph pCrmPrevissaoVl = null;
					if(cr.getHora_agendado()!=null){
						 pCrmPrevissaoVl = new Paragraph(dtUtil.parseDataBra(cr.getData_agendado())+" "+DataUtil.formatHoraBra(cr.getHora_agendado()),fCampo);
					}else{
						 pCrmPrevissaoVl = new Paragraph(dtUtil.parseDataBra(cr.getData_agendado()),fCampo);
					}
					PdfPCell pCellCrmPrevisaoVl = new PdfPCell();
					pCellCrmPrevisaoVl.addElement(pCrmPrevissaoVl);
					pCellCrmPrevisaoVl.setPaddingBottom(4f);
					pCellCrmPrevisaoVl.setPaddingTop(paddingTop);
					pCellCrmPrevisaoVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmPrevisaoVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCrmPrevisaoVl.setBorderWidth(1.5f);
					
					Paragraph pCrmDtConclusaoVl = new Paragraph();
					if(cr.getData_efetuado()!=null){
						pCrmDtConclusaoVl = new Paragraph(dtUtil.parseDataHoraBra(cr.getData_efetuado().toString()),fCampo);
					}
					
					PdfPCell pCellCrmDtConclusaoVl = new PdfPCell();
					pCellCrmDtConclusaoVl.addElement(pCrmDtConclusaoVl);
					pCellCrmDtConclusaoVl.setPaddingBottom(4f);
					pCellCrmDtConclusaoVl.setPaddingTop(paddingTop);
					pCellCrmDtConclusaoVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmDtConclusaoVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCrmDtConclusaoVl.setBorderWidth(1.5f);
					
					Paragraph pCrmTempTratVl = null;
					if(cr.getTempo_total_tratamento()!=null){
						pCrmTempTratVl = new Paragraph(cr.getTempo_total_tratamento().toString(),fCampo);
					}
					
					PdfPCell pCellTempTratVl = new PdfPCell();
					pCellTempTratVl.addElement(pCrmTempTratVl);
					pCellTempTratVl.setPaddingTop(paddingTop);
					pCellTempTratVl.setPaddingBottom(4f);
					pCellTempTratVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellTempTratVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellTempTratVl.setBorderWidth(1.5f);
					
					Paragraph pCrmOperadorVl = new Paragraph(cr.getOperador_tratamento(),fCampo);
					PdfPCell pCellCrmOperadorVl = new PdfPCell();
					pCellCrmOperadorVl.addElement(pCrmOperadorVl);
					pCellCrmOperadorVl.setPaddingTop(paddingTop);
					pCellCrmOperadorVl.setPaddingBottom(4f);
					pCellCrmOperadorVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmOperadorVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCrmOperadorVl.setBorderWidth(1.5f);
					
					Paragraph pOseStatusVl = new Paragraph(cr.getStatus(),fCampo);
					PdfPCell pCellCrmStatusVl = new PdfPCell();
					pCellCrmStatusVl.addElement(pOseStatusVl);
					pCellCrmStatusVl.setPaddingBottom(4f);
					pCellCrmStatusVl.setPaddingTop(paddingTop);
					pCellCrmStatusVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmStatusVl.setBorderColor(new BaseColor(255, 255, 255));		
					pCellCrmStatusVl.setBorderWidth(1.5f);
										
					Paragraph pCrmReagendarVl = new Paragraph(cr.getMotivo_reagendamento(),fCampo);
					PdfPCell pCellCrmReagendarVl = new PdfPCell();
					pCellCrmReagendarVl.addElement(pCrmReagendarVl);
					pCellCrmReagendarVl.setPaddingBottom(4f);
					pCellCrmReagendarVl.setPaddingTop(paddingTop);
					pCellCrmReagendarVl.setBackgroundColor(new BaseColor(232, 235, 237));
					pCellCrmReagendarVl.setBorderColor(new BaseColor(255, 255, 255));	
					pCellCrmReagendarVl.setBorderWidth(1.5f);
					

					PdfPTable tbCabCrmVl = new PdfPTable(new float[]{0.18f,0.20f,0.20f,0.30f,0.30f,0.30f,0.30f,0.30f,0.30f,0.20f,0.20f,0.30f});
					tbCabCrmVl.setWidthPercentage(100f);	
					tbCabCrmVl.addCell(pCellCrmCodVl);
					tbCabCrmVl.addCell(pCellCrmDtAbVl);
					tbCabCrmVl.addCell(pCellCrmHrAbVl);
					tbCabCrmVl.addCell(pCellCrmSetorVl);
					tbCabCrmVl.addCell(pCellCrmAssuntoVl);
					tbCabCrmVl.addCell(pCellCrmContatoVl);
					tbCabCrmVl.addCell(pCellCrmPrevisaoVl);
					tbCabCrmVl.addCell(pCellCrmDtConclusaoVl);
					tbCabCrmVl.addCell(pCellTempTratVl);
					tbCabCrmVl.addCell(pCellCrmOperadorVl);
					tbCabCrmVl.addCell(pCellCrmStatusVl);
					tbCabCrmVl.addCell(pCellCrmReagendarVl);
					
					doc.add(tbCabCrmVl);
									
				}
				Paragraph pRegistro = new Paragraph("   "+String.valueOf(crm.size())+" Registros encontrados",new Font(FontFamily.HELVETICA, 4));
				doc.add(pRegistro);
			}					
					
		}catch(Exception e){
			e.printStackTrace();
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
