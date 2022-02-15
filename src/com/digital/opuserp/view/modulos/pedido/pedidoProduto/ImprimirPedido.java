package com.digital.opuserp.view.modulos.pedido.pedidoProduto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.dao.TotaisPedidoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SeriaisPedido;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.domain.TotaisPedido;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;



public class ImprimirPedido implements StreamSource{

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ImprimirPedido(Integer cod,String tipo) throws Exception {
		
		EntityManager em = ConnUtil.getEntity();
		
		Document doc = new Document(PageSize.A4, 24, 24, 24, 24);	
		doc.setMargins(12, 12, 12, 12);
		PdfWriter pdfWriter = null;
		try{
			pdfWriter = PdfWriter.getInstance(doc, baos);
			doc.open();

			HTMLWorker htmlWorker = new HTMLWorker(doc);
			
			
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			EcfPreVendaCabecalho pedido = em.find(EcfPreVendaCabecalho.class, cod);
			Cliente cliente = pedido.getCliente();
			

			Query qEcf = em.createQuery("select ecf from EcfPreVendaDetalhe ecf where ecf.ecfPreVendaCabecalhoId =:codCab", EcfPreVendaDetalhe.class);
			qEcf.setParameter("codCab", cod);			
			List<EcfPreVendaDetalhe> resultDetalhe = null;
			if(qEcf.getResultList().size() > 0){
				resultDetalhe = qEcf.getResultList();
			}

						
			DataUtil dtUtil = new DataUtil();
			
			Font cabBold = new Font(FontFamily.HELVETICA, 9, Font.BOLD);
			Font fBold = new Font(FontFamily.HELVETICA, 7, Font.BOLD);
			Font f = new Font(FontFamily.HELVETICA, 7);
			Font fcam = new Font(FontFamily.HELVETICA, 6);
			Font fcamBold = new Font(FontFamily.HELVETICA, 6,Font.BOLD);
			
			// CABECALHO		
			 Paragraph pLinha = new Paragraph("__________________________________________________________________________________________________________________________________________________",f);			 
//			 pLinha.setSpacingAfter(5);
			 doc.add(pLinha);
			 
			 Paragraph pCab = new Paragraph("DOCUMENTO AUXILIAR DE VENDA - "+tipo,cabBold);
			 pCab.setSpacingAfter(10);
			 pCab.setAlignment(Element.ALIGN_CENTER);
			 doc.add(pCab);
			 
			 Paragraph pDoc = new Paragraph("Nº DO DOCUMENTO: ",f);
			 PdfPCell pCellDoc= new PdfPCell();
			 pCellDoc.setBorderWidth(0);
			 pCellDoc.addElement(pDoc);
			 pCellDoc.setPadding(0);
			 
			 Paragraph pDocVl = new Paragraph(cod.toString(),fBold);
			 PdfPCell pCellDocVl= new PdfPCell();
			 pCellDocVl.setBorderWidth(0);
			 pCellDocVl.addElement(pDocVl);
			 pCellDocVl.setPadding(0);
			 
			 Paragraph pDocF = new Paragraph("Nº DO DOCUMENTO FISCAL:____________________ ",f);
			 PdfPCell pCellDocF= new PdfPCell();
			 pCellDocF.setBorderWidth(0);
			 pCellDocF.addElement(pDocF);
			 pCellDocF.setPadding(0);
			 
			 Paragraph pDocFVl = new Paragraph("",fBold);
			 PdfPCell pCellDocFVl= new PdfPCell();
			 pCellDocFVl.setBorderWidth(0);
			 pCellDocFVl.addElement(pDocFVl);
			 pCellDocFVl.setPadding(0);
			 
			 Paragraph pData = new Paragraph("DATA: ",f);
			 PdfPCell pCellData= new PdfPCell();
			 pCellData.setBorderWidth(0);
			 pCellData.addElement(pData);
			 pCellData.setPadding(0);
			 
			 
			 SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");	
			 
			 String hora = sdf.format(new Date());
			 String date = dtUtil.parseDataBra(new Date());

			 String horaPedido = sdf.format(pedido.getHora_pv());
			 String datePedido = dtUtil.parseDataBra(pedido.getData());

			 Paragraph pDataVl = new Paragraph(datePedido+" às "+horaPedido+" por "+pedido.getVendedor(),fBold);
			 PdfPCell pCellDataVl= new PdfPCell();
			 pCellDataVl.setBorderWidth(0);
			 pCellDataVl.addElement(pDataVl);
			 pCellDataVl.setPadding(0);
					 
			 PdfPTable tbCab = new PdfPTable(new float[] {0.25f,0.26f,0.60f,0.10f,0.40f});
			 tbCab.setWidthPercentage(100f);
			 tbCab.setSpacingAfter(5);
			 tbCab.addCell(pCellDoc);
			 tbCab.addCell(pCellDocVl);
			 tbCab.addCell(pCellDocF);
//			 tbCab.addCell(pCellDocFVl);
			 tbCab.addCell(pCellData);
			 tbCab.addCell(pCellDataVl);
			 doc.add(tbCab);
			  
			 doc.add(pLinha);
			
			 Paragraph pFiscal = new Paragraph("NÃO É DOCUMENTO FISCAL - NÃO É VÁLIDO COMO RECIBO - NÃO É VÁLIDO COMO GARANTIA DE MERCADORIA",f);
			 pFiscal.setSpacingBefore(5);
			 pFiscal.setAlignment(Element.ALIGN_CENTER);
			 doc.add(pFiscal);			
						
			 doc.add(pLinha);
			
			//INDETIFICAÇÃO ESTABELECIMENTO 
			 
			 Paragraph pIndetificacao = new Paragraph("INDENTIFICAÇÃO DO ESTABELECIMENTO EMITENTE",fBold);
			 pIndetificacao.setSpacingBefore(5);
			 pIndetificacao.setSpacingAfter(10);
			 pIndetificacao.setAlignment(Element.ALIGN_CENTER);
			 doc.add(pIndetificacao);
			 
			 Paragraph pEmpresa = new Paragraph(OpusERP4UI.getEmpresa().getNome_fantasia(),fBold);
			 Paragraph pEnd = new Paragraph(OpusERP4UI.getEmpresa().getCep()+" "+OpusERP4UI.getEmpresa().getEndereco()+", "+OpusERP4UI.getEmpresa().getNumero()+" "+OpusERP4UI.getEmpresa().getBairro()+" "+OpusERP4UI.getEmpresa().getCidade()+" - "+OpusERP4UI.getEmpresa().getUf(),f);
			 Paragraph pCnpj = new Paragraph(OpusERP4UI.getEmpresa().getCnpj(),f);		
			 Paragraph pEmail = new Paragraph(OpusERP4UI.getEmpresa().getDdd_fone1()+" "+OpusERP4UI.getEmpresa().getFone1()+" "+OpusERP4UI.getEmpresa().getEmail(),f);
			
			PdfPCell pCabInd = new PdfPCell();
			pCabInd.setBorderWidth(0);
			pCabInd.addElement(pEmpresa);
			pCabInd.addElement(pEnd);
			pCabInd.addElement(pCnpj);
			pCabInd.addElement(pEmail);

			//Logo
			byte[] logo = OpusERP4UI.getEmpresa().getLogo_empresa();
			PdfPCell pCabLogo = new PdfPCell();
			Image imgLogo = null;
			if(logo!=null){				
				imgLogo = Image.getInstance(logo);
				imgLogo.setAlignment(Element.ALIGN_RIGHT);
				pCabLogo.addElement(imgLogo);
			}else{				
				pCabLogo.addElement(new Paragraph(""));
			}
			
//			imgLogo.setSpacingBefore(15);								
			pCabLogo.setBorderWidth(0);
			
			 PdfPTable tbCabLogo = new PdfPTable(new float[] {1f,0.40f});
			 tbCabLogo.setWidthPercentage(100f);
//			 tbCLiente.setSpacingAfter(5);
			 tbCabLogo.addCell(pCabInd);
			 tbCabLogo.addCell(pCabLogo);
			 doc.add(tbCabLogo);
			 
			 
			 
			 doc.add(pLinha);
			 
			 //INDETIFICAÇÃO DESTINATARIO
			 Paragraph pIndenti = new Paragraph("INDENTIFICAÇÃO DO DESTINATÁRIO",fBold);
			 pIndenti.setSpacingBefore(5);
			 pIndenti.setSpacingAfter(10);
			 pIndenti.setAlignment(Element.ALIGN_CENTER);
			 doc.add(pIndenti);
			 
			 StringBuilder stbCliente = new StringBuilder();
			 stbCliente.append("CLIENTE:"+"\n"+"COMPRADOR:"+"\n"+"ENDEREÇO:"+"\n"+"ENTREGA:");
			 Paragraph pCliente = new Paragraph(stbCliente.toString(),f);
			 
			 String comprador = pedido.getComprador()!=null ? pedido.getComprador() : "";			 
			 StringBuilder stbClienteVl = new StringBuilder();
			 Paragraph pClienteVl = new Paragraph();
			 if(cliente != null && cliente.getEndereco_principal()!=null){
				 
				 String codigo = cliente.getId() != null ? cliente.getId().toString() : "";
				 String nome = cliente.getNome_razao() != null ? cliente.getNome_razao() : "";
				 String cpf_cnpj = cliente.getDoc_cpf_cnpj() != null ? cliente.getDoc_cpf_cnpj() : "";
				 String cep = cliente.getEndereco_principal().getCep() != null ? cliente.getEndereco_principal().getCep() : "";
				 String endereco = cliente.getEndereco_principal().getEndereco() != null ? cliente.getEndereco_principal().getEndereco() : "";
				 String numero = cliente.getEndereco_principal().getNumero() != null ? cliente.getEndereco_principal().getNumero() : "";
				 String bairro = cliente.getEndereco_principal().getBairro() != null ? cliente.getEndereco_principal().getBairro() : "";
				 String cidade = cliente.getEndereco_principal().getCidade() != null ? cliente.getEndereco_principal().getCidade() : "";
				 String uf = cliente.getEndereco_principal().getUf() != null ? cliente.getEndereco_principal().getUf() : "";
				 String referencia = ", "+cliente.getEndereco_principal().getReferencia() != null ? cliente.getEndereco_principal().getReferencia() : "";
				 
				 String endereco_entrega = pedido.getEntregar().equals("SIM") ? " - "+pedido.getEnd().getCep()+" "+pedido.getEnd().getEndereco()+", "+pedido.getEnd().getNumero()+" "+pedido.getEnd().getBairro()+" "+pedido.getEnd().getCidade()+", "+pedido.getEnd().getReferencia() : "";
				 stbClienteVl.append(codigo+" - "+nome+" ("+cpf_cnpj+")"+"\n"+comprador+"\n"+cep+" "+endereco+", "+numero+" "+bairro+" "+cidade+"-"+uf+" "+referencia+"\n"+pedido.getEntregar()+" "+endereco_entrega);
				 pClienteVl = new Paragraph(stbClienteVl.toString(),fBold);
			 }else{
				 if(cliente != null){
					 stbClienteVl.append(cliente.getId().toString()+" - "+cliente.getNome_razao()+" ("+cliente.getDoc_cpf_cnpj()+")"+"\n"+comprador+"\n"+
					 " "+"\n"+pedido.getEntregar());
					 pClienteVl = new Paragraph(stbClienteVl.toString(),fBold);
				 }
			 }
			 
			 PdfPCell pCellCliente= new PdfPCell();
			 pCellCliente.setBorderWidth(0);
			 pCellCliente.addElement(pCliente);
			 pCellCliente.setPadding(0);
			 
			 PdfPCell pCellClienteVl= new PdfPCell();
			 pCellClienteVl.setBorderWidth(0);
			 pCellClienteVl.addElement(pClienteVl);
			 pCellClienteVl.setPadding(0);
			
			 PdfPTable tbCLiente = new PdfPTable(new float[] {0.15f,1f});
			 tbCLiente.setWidthPercentage(100f);
//			 tbCLiente.setSpacingAfter(5);
			 tbCLiente.addCell(pCellCliente);
			 tbCLiente.addCell(pCellClienteVl);
			 doc.add(tbCLiente);
			 
			 doc.add(pLinha);
			 
			 //TABELA
			 //CABEÇALHO
			 Paragraph pCod = new Paragraph("CÓDIGO",fBold);
			 pCod.setAlignment(Element.ALIGN_CENTER);
			 PdfPCell pCellCabCod = new PdfPCell();
			 pCellCabCod.addElement(pCod);
			 pCellCabCod.setPaddingTop(2);
			 pCellCabCod.setPaddingBottom(4);
			 pCellCabCod.setBackgroundColor(new BaseColor(114, 131, 151));
			 pCellCabCod.setBorderColor(new BaseColor(255, 255, 255));	
			 pCellCabCod.setBorderWidth(1.5f);
			
			 Paragraph pCodBarras = new Paragraph("CÓD.BARRAS",fBold);
			 pCodBarras.setAlignment(Element.ALIGN_CENTER);
			 PdfPCell pCellCabBarras = new PdfPCell();
			 pCellCabBarras.addElement(pCodBarras);
			 pCellCabBarras.setPaddingTop(2);
			 pCellCabBarras.setPaddingBottom(4);
			 pCellCabBarras.setBackgroundColor(new BaseColor(114, 131, 151));
			 pCellCabBarras.setBorderColor(new BaseColor(255, 255, 255));	
			 pCellCabBarras.setBorderWidth(1.5f);
			 
			 Paragraph pDesc = new Paragraph();

			 if(pedido.getTipoVenda().equals("PRODUTO")){
				 pDesc = new Paragraph("PRODUTO",fBold);
			 }else if(pedido.getTipoVenda().equals("SERVICO")){
				 pDesc = new Paragraph("SERVIÇO",fBold);				 
			 }
			 
			 pDesc.setAlignment(Element.ALIGN_LEFT);
			 PdfPCell pCellDesc = new PdfPCell();
			 pCellDesc.addElement(pDesc);
			 pCellDesc.setPaddingTop(2);
			 pCellDesc.setPaddingBottom(4);
			 pCellDesc.setBackgroundColor(new BaseColor(114, 131, 151));
			 pCellDesc.setBorderColor(new BaseColor(255, 255, 255));	
			 pCellDesc.setBorderWidth(1.5f);
			 
			 Paragraph pQtd = new Paragraph("QTD.",fBold);
			 pQtd.setAlignment(Element.ALIGN_CENTER);
			 PdfPCell pCellQtd = new PdfPCell();
			 pCellQtd.addElement(pQtd);
			 pCellQtd.setPaddingTop(2);
			 pCellQtd.setPaddingBottom(4);
			 pCellQtd.setBackgroundColor(new BaseColor(114, 131, 151));
			 pCellQtd.setBorderColor(new BaseColor(255, 255, 255));	
			 pCellQtd.setBorderWidth(1.5f);
			 
			 Paragraph pValorUni = new Paragraph("VALOR UNIT.",fBold);
			 pValorUni.setAlignment(Element.ALIGN_CENTER);
			 PdfPCell pCellValorUni = new PdfPCell();
			 pCellValorUni.addElement(pValorUni);
			 pCellValorUni.setPaddingTop(2);
			 pCellValorUni.setPaddingBottom(4);
			 pCellValorUni.setBackgroundColor(new BaseColor(114, 131, 151));
			 pCellValorUni.setBorderColor(new BaseColor(255, 255, 255));	
			 pCellValorUni.setBorderWidth(1.5f);
			 
			 Paragraph pValorTotal = new Paragraph("VALOT TOTAL R$",fBold);
			 pValorTotal.setAlignment(Element.ALIGN_CENTER);
			 PdfPCell pCellValorTotal = new PdfPCell();
			 pCellValorTotal.addElement(pValorTotal);
			 pCellValorTotal.setPaddingTop(2);
			 pCellValorTotal.setPaddingBottom(4);
			 pCellValorTotal.setBackgroundColor(new BaseColor(114, 131, 151));
			 pCellValorTotal.setBorderColor(new BaseColor(255, 255, 255));	
			 pCellValorTotal.setBorderWidth(1.5f);
			 
			 PdfPTable tbCab2 = new PdfPTable(new float[] {0.20f,0.30f,1f,0.15f,0.30f,0.30f});
			 tbCab2.setWidthPercentage(100f);
			 tbCab2.setSpacingBefore(10);
			 tbCab2.addCell(pCellCabCod);
			 tbCab2.addCell(pCellCabBarras);
			 tbCab2.addCell(pCellDesc);
			 tbCab2.addCell(pCellQtd);
			 tbCab2.addCell(pCellValorUni);
			 tbCab2.addCell(pCellValorTotal);
			 doc.add(tbCab2);
			 
			 //CONTEUDO			 
			 float qtd = 0 ;
			 float totalUni = 0 ;
			 float totalVl = 0 ;
			 //FormasPgto formPgto = new FormasPgto();
			 //String pgto = "";
			 String virgula = " ";
			 			 
			 
			 
			
			 
			 
				if(resultDetalhe!=null){
					for(EcfPreVendaDetalhe detalhe: resultDetalhe){
							
						Produto produto = new Produto();
						Servico servico = new Servico();
						Paragraph pCodVl = new Paragraph();
						Paragraph pCodBarrasVl = new Paragraph();
						Paragraph pDescVl = new Paragraph();

						if(pedido.getTipoVenda().equals("PRODUTO")){
							
							produto = ProdutoDAO.find(detalhe.getProdutoId());	
							pCodVl = new Paragraph(produto.getId().toString(),f);
							pCodBarrasVl = new Paragraph(produto.getgTin(),f);
							
							Query qSerial = em.createQuery("select sp from SeriaisPedido sp where sp.itemPedido =:detalhe", SeriaisPedido.class);
							qSerial.setParameter("detalhe",detalhe);			
							List <SeriaisPedido> resultSerial = null;
							if(qSerial.getResultList().size() > 0){
								resultSerial = qSerial.getResultList();
								
								StringBuilder sbSerial = new StringBuilder();
								
								for(SeriaisPedido serial: resultSerial){
								SerialProduto sp = em.find(SerialProduto.class, serial.getSerial().getId());
								sbSerial.append(sp.getSerial()+" ");
								}
								pDescVl = new Paragraph(produto.getNome()+"\n"+"SN: "+sbSerial.toString(),f);
							}else{
								pDescVl = new Paragraph(produto.getNome(),f);								
							}
							
							
							
						}else{
							
							servico = ServicoDAO.find(detalhe.getProdutoId());
							pCodVl = new Paragraph(servico.getId().toString(),f);
							pCodBarrasVl = new Paragraph(" ",f);
							pDescVl = new Paragraph(servico.getNome(),f);
						}
							
							//if(ecfCab.getFormaPagtoID()!= null){
							//	formPgto = em.find(FormasPgto.class, ecfCab.getFormaPagtoID().getId());
							//	pgto = formPgto.getNome();
																
							//}
													
							qtd = qtd + detalhe.getQuantidade();
							totalUni = totalUni + detalhe.getValorUnitario();
							totalVl = totalVl + detalhe.getValorTotal();
													
							 pCodVl.setAlignment(Element.ALIGN_CENTER);
							 PdfPCell pCellCabCodVl = new PdfPCell();
							 pCellCabCodVl.addElement(pCodVl);
							 pCellCabCodVl.setPaddingTop(2);
							 pCellCabCodVl.setPaddingBottom(4);
							 pCellCabCodVl.setBackgroundColor(new BaseColor(232, 235, 237));
							 pCellCabCodVl.setBorderColor(new BaseColor(255, 255, 255));	
							 pCellCabCodVl.setBorderWidth(1.5f);
							 
							 pCodBarrasVl.setAlignment(Element.ALIGN_CENTER);
							 PdfPCell pCellCabBarrasVl = new PdfPCell();
							 pCellCabBarrasVl.addElement(pCodBarrasVl);
							 pCellCabBarrasVl.setPaddingTop(2);
							 pCellCabBarrasVl.setPaddingBottom(4);
							 pCellCabBarrasVl.setBackgroundColor(new BaseColor(232, 235, 237));
							 pCellCabBarrasVl.setBorderColor(new BaseColor(255, 255, 255));	
							 pCellCabBarrasVl.setBorderWidth(1.5f);
							 
							 PdfPCell pCellDescVl = new PdfPCell();
							 pCellDescVl.addElement(pDescVl);
							 pCellDescVl.setPaddingTop(2);
							 pCellDescVl.setPaddingBottom(4);
							 pCellDescVl.setBackgroundColor(new BaseColor(232, 235, 237));
							 pCellDescVl.setBorderColor(new BaseColor(255, 255, 255));	
							 pCellDescVl.setBorderWidth(1.5f);

							Paragraph pQtdProduto = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getQuantidade())),f);
							pQtdProduto.setAlignment(Element.ALIGN_CENTER);
							PdfPCell pCellQtdproduto = new PdfPCell();
							pCellQtdproduto.setPaddingTop(2);
							pCellQtdproduto.setPaddingBottom(4);
							pCellQtdproduto.addElement(pQtdProduto);
							pCellQtdproduto.setBackgroundColor(new BaseColor(232, 235, 237));
							pCellQtdproduto.setBorderColor(new BaseColor(255, 255, 255));
							pCellQtdproduto.setBorderWidth(1.5f);
							
							Paragraph pValorUnitario = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getValorUnitario())),f);
							pValorUnitario.setAlignment(Element.ALIGN_RIGHT);
							PdfPCell pCellValorUnitario = new PdfPCell();
							pCellValorUnitario.setPaddingTop(2);
							pCellValorUnitario.setPaddingBottom(4);
							pCellValorUnitario.addElement(pValorUnitario);
							pCellValorUnitario.setBackgroundColor(new BaseColor(232, 235, 237));
							pCellValorUnitario.setBorderColor(new BaseColor(255, 255, 255));
							pCellValorUnitario.setBorderWidth(1.5f);
							
							Paragraph pValorTotalVl = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getValorTotal())),f);
							pValorTotalVl.setAlignment(Element.ALIGN_RIGHT);
							PdfPCell pCellValorToTal = new PdfPCell();
							pCellValorToTal.setPaddingTop(2);
							pCellValorToTal.setPaddingBottom(4);
							pCellValorToTal.addElement(pValorTotalVl);
							pCellValorToTal.setBackgroundColor(new BaseColor(232, 235, 237));
							pCellValorToTal.setBorderColor(new BaseColor(255, 255, 255));
							pCellValorToTal.setBorderWidth(1.5f);
							
							
							
							PdfPTable tbCab3 = new PdfPTable(new float[] {0.20f,0.30f,1f,0.15f,0.30f,0.30f});
							tbCab3.setWidthPercentage(100f);
							tbCab3.addCell(pCellCabCodVl);
							tbCab3.addCell(pCellCabBarrasVl);
							tbCab3.addCell(pCellDescVl);
							tbCab3.addCell(pCellQtdproduto);
							tbCab3.addCell(pCellValorUnitario);
							tbCab3.addCell(pCellValorToTal);
							doc.add(tbCab3);		
						}
					
							Paragraph pVazio = new Paragraph(" ",f);
							PdfPCell pCellVazio = new PdfPCell();
							pCellVazio.addElement(pVazio);
							pCellVazio.setBorderWidth(0);
							
							Paragraph pTotal = new Paragraph("TOTAL",fBold);
							pTotal.setAlignment(Element.ALIGN_RIGHT);
							PdfPCell pCellToTal = new PdfPCell();
							pCellToTal.addElement(pTotal);
							pCellToTal.setBorderWidth(0);
							
							Paragraph pTotalQtd = new Paragraph(Real.formatDbToString(String.valueOf(qtd)),fBold);
							pTotalQtd.setAlignment(Element.ALIGN_CENTER);
							PdfPCell pCellTotalQtd = new PdfPCell();
							pCellTotalQtd.addElement(pTotalQtd);
							pCellTotalQtd.setBorderWidth(0);
							
							Paragraph pTotalUni = new Paragraph("R$ "+Real.formatDbToString(String.valueOf(totalUni)),fBold);
							pTotalUni.setAlignment(Element.ALIGN_RIGHT);
							PdfPCell pCellTotalUni = new PdfPCell();
							//pCellTotalUni.addElement(pTotalUni);
							pCellTotalUni.setBorderWidth(0);
							
							Paragraph pTotalVl = new Paragraph("R$ "+Real.formatDbToString(String.valueOf(totalVl)),fBold);
							pTotalVl.setAlignment(Element.ALIGN_RIGHT);
							PdfPCell pCellTotalVl = new PdfPCell();
							pCellTotalVl.addElement(pTotalVl);
							pCellTotalVl.setBorderWidth(0);
							
							
							PdfPTable tbCab4 = new PdfPTable(new float[] {0.20f,0.30f,1f,0.15f,0.30f,0.30f});
							tbCab4.setWidthPercentage(100f);
							tbCab4.addCell(pCellVazio);
							tbCab4.addCell(pCellVazio);
							tbCab4.addCell(pCellToTal);
							tbCab4.addCell(pCellTotalQtd);
							tbCab4.addCell(pCellTotalUni);
							tbCab4.addCell(pCellTotalVl);
							doc.add(tbCab4);
					}
				
				
				//NOVAS FORMA DE PGTO
				
//				Paragraph pNaturezaOperacao= new Paragraph("NATUREZA DE OPERAÇÃO",f);
//				Paragraph pVenda= new Paragraph("VENDA",fBold);
//				 
//				 PdfPCell pCellNaturezaOperacao = new PdfPCell();
//				 pCellNaturezaOperacao.addElement(pNaturezaOperacao);
//				 pCellNaturezaOperacao.setBorderWidth(0);
//				 
//				 PdfPCell pCellVenda = new PdfPCell();
//				 pCellVenda.addElement(pVenda);
//				 pCellVenda.setBorderWidth(0);
//				 
//				 PdfPCell pCellValor = new PdfPCell();
//				 //pCellVenda.addElement(pVenda);
//				 pCellValor.setBorderWidth(0);
//				 
//				 PdfPCell pCellParcelamento = new PdfPCell();
//				 //pCellVenda.addElement(pVenda);
//				 pCellVenda.setBorderWidth(0);
//				
//				PdfPTable tbCabFormaPgto = new PdfPTable(new float[] {0.30f,1f,1f});
//				tbCabFormaPgto.setWidthPercentage(100f);
//				tbCabFormaPgto.setSpacingBefore(10);
//				tbCabFormaPgto.setSpacingAfter(10);
//				tbCabFormaPgto.addCell(pCellNaturezaOperacao);
//				tbCabFormaPgto.addCell(pCellVenda);
//				tbCabFormaPgto.addCell(pCellValor);
//				tbCabFormaPgto.addCell(pCellParcelamento);
//				doc.add(tbCabFormaPgto);
				 
				 
				 
				 
				 
				 
				
				
				
				
				 //FORMA DE PAGAMENTO
				 StringBuilder stbFormPgto = new StringBuilder();
				 stbFormPgto.append("NATUREZA DE OPERAÇÃO:"+"\n\n"+"FORMA DE PAGAMENTO:"+"\n");
				 Paragraph pFormPgto = new Paragraph(stbFormPgto.toString(),f);
				 
				 PdfPCell pCellFormPgto = new PdfPCell();
				 pCellFormPgto.addElement(pFormPgto);
				 pCellFormPgto.setBorderWidth(0);

				 
				 StringBuilder stbFormPgtoVl = new StringBuilder();
				 StringBuilder stbFormPgtoValores = new StringBuilder();
				 stbFormPgtoValores.append("\n\n");
				 stbFormPgtoVl.append(pedido.getNaturezaOperacao().getDescricao()+"\n\n");
				 
				 List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedido);				 
				 
				 for (TotaisPedido totaisPedido : totais) {					
				
					 Integer qtd_parc = ContasReceberDAO.getQtdParcelas("PV/"+totaisPedido.getPedido().getId().toString());
					 if(totaisPedido.getForma_pgto().getTipo_titulo().equals("BOLETO") && qtd_parc > 0){
						 
						 stbFormPgtoVl.append(String.format("%-31.50s%s%s",totaisPedido.getForma_pgto().getNome(), "R$ "+Real.formatDbToString(String.valueOf(totaisPedido.getValor())),"          PARCELAS: "+String.format("%02d", qtd_parc)+"\n"));
						 List<ContasReceber> boletos = ContasReceberDAO.getParcelas("PV/"+totaisPedido.getPedido().getId().toString());
						 stbFormPgtoVl.append("\nVENCIMENTOS: \n");
						 for (ContasReceber contasReceber : boletos) {
							
							 stbFormPgtoVl.append(String.format("%-30.50s%s",contasReceber.getQuantidade()+"  "+DataUtil.formatDateBra(contasReceber.getData_vencimento()),"R$ "+contasReceber.getValor_titulo()+"\n"));
						}
					 }else{


						 stbFormPgtoVl.append(String.format("%-30.50s%s",totaisPedido.getForma_pgto().getNome(), "R$ "+Real.formatDbToString(String.valueOf(totaisPedido.getValor())))+"\n");

						 //String.format ("%-32.32s", s);  
					 }
					 
					 //if(totaisPedido.getPedido().get)
//					 Integer qtd_parc = ContasReceberDAO.getQtdParcelas("PV/"+totaisPedido.getPedido().getId().toString());
//					 if(totaisPedido.getForma_pgto().getTipo_titulo().equals("BOLETO") && qtd_parc > 0){
//						 stbFormPgtoValores.append(Real.formatDbToString(String.valueOf(totaisPedido.getValor()))+"      PARCELAS: "+String.format("%02d", qtd_parc)+"\n");
//					 }else{
//						 stbFormPgtoValores.append(Real.formatDbToString(String.valueOf(totaisPedido.getValor()))+"\n");
//					 }
				}
				 
				 
				 Paragraph pFormPgtoVl = new Paragraph(stbFormPgtoVl.toString(),fBold);
				 pFormPgtoVl.setAlignment(Element.ALIGN_LEFT);
				 
				 Paragraph pFormPgtoValores = new Paragraph(stbFormPgtoValores.toString(),fBold);
				 pFormPgtoValores.setAlignment(Element.ALIGN_LEFT);
				 
				 PdfPCell pCellFormPgtoVl = new PdfPCell();
				 pCellFormPgtoVl.addElement(pFormPgtoVl);
				 pCellFormPgtoVl.setBorderWidth(0);
				 
				 PdfPCell pCellFormPgtoValores = new PdfPCell();
				 pCellFormPgtoValores.addElement(pFormPgtoValores);
				 pCellFormPgtoValores.setBorderWidth(0);
				 
				 PdfPTable tbCab5 = new PdfPTable(new float[] {0.15f,0.60f});
				 tbCab5.setWidthPercentage(100f);
				 tbCab5.setSpacingBefore(10);
				 tbCab5.setSpacingAfter(10);
				 tbCab5.addCell(pCellFormPgto);
				 tbCab5.addCell(pCellFormPgtoVl);
				 //tbCab5.addCell(pCellFormPgtoValores);
				 doc.add(tbCab5);
				 
				 //SUBTOTAL
				 StringBuilder stbSub = new StringBuilder();
				 stbSub.append("ACRÉSCIMO:"+"\n\n"+"SUBTOTAL:"+"\n"+"DESCONTO(%):"+"\n");
				 Paragraph pSub = new Paragraph(stbSub.toString(),f);
				 pSub.setAlignment(Element.ALIGN_RIGHT);
				 
				 PdfPCell pCellSub = new PdfPCell();
				 pCellSub.addElement(pSub);
				 pCellSub.setBorderWidth(0);
				 
									 
				 StringBuilder stbSubVl = new StringBuilder();
				 stbSubVl.append("R$ "+Real.formatDbToString(String.valueOf(pedido.getTotal_acres()))+"\n\n"+"R$ "+Real.formatDbToString(String.valueOf(pedido.getSubTotal()+pedido.getTotal_acres()))+"\n"+"% "+Real.formatDbToString(String.valueOf(pedido.getTotal_desc())));
				 Paragraph pSubVl = new Paragraph(stbSubVl.toString(),f);
				 pSubVl.setAlignment(Element.ALIGN_RIGHT);
				 
				 PdfPCell pCellSubVl = new PdfPCell();
				 pCellSubVl.addElement(pSubVl);
				 pCellSubVl.setBorderWidth(0);
								 
				 PdfPTable tbCab6 = new PdfPTable(new float[] {1f,0.15f});
				 tbCab6.setWidthPercentage(100f);
				 tbCab6.addCell(pCellSub);
				 tbCab6.addCell(pCellSubVl);
				 doc.add(tbCab6);
				 
				 Paragraph pTotal = new Paragraph("TOTAL:",fBold);
				 pTotal.setAlignment(Element.ALIGN_RIGHT);
				 PdfPCell pCellTotal = new PdfPCell();
				 pCellTotal.addElement(pTotal);
				 pCellTotal.setBorderWidth(0);
				 
				 double valor_final_desconto = 0;
				 
				 if(pedido.getTotal_desc()!=null){
					 double percentual = pedido.getTotal_desc() / 100.0; 
					 valor_final_desconto = percentual * pedido.getSubTotal();
				 }
				 double total = pedido.getSubTotal()-valor_final_desconto+pedido.getTotal_acres(); 
				 
				 Paragraph pTotalVl = new Paragraph("R$ "+Real.formatDbToString(String.valueOf(total)),fBold);
				 pTotalVl.setAlignment(Element.ALIGN_RIGHT);
				 PdfPCell pCellTotalVl = new PdfPCell();
				 pCellTotalVl.addElement(pTotalVl);
				 pCellTotalVl.setBorderWidth(0);
				 
				 PdfPTable tbCab7 = new PdfPTable(new float[] {1f,0.15f});
				 tbCab7.setWidthPercentage(100f);
				 tbCab7.setSpacingAfter(10);
				 tbCab7.addCell(pCellTotal);
				 tbCab7.addCell(pCellTotalVl);
				 doc.add(tbCab7);
				 
				 //OBS
				 Paragraph pObs = new Paragraph("OBSERVAÇÃO:",fcam);
				 Paragraph pObsVl = new Paragraph(pedido.getObs(),fcam);
				 PdfPCell pCellObs = new PdfPCell();
				 pCellObs.addElement(pObs);
				 pCellObs.addElement(pObsVl);
				 pCellObs.setBorderWidth(0);
				
				 PdfPTable tbCab8 = new PdfPTable(new float[] {1f});
				 tbCab8.setWidthPercentage(100f);
				 tbCab8.setSpacingAfter(15);
				 tbCab8.addCell(pCellObs);
				 doc.add(tbCab8);
				 
				 //Validade Orçamento
				 Paragraph pOrca = new Paragraph("ORÇAMENTO VÁLIDO POR 5 (CINCO) DIAS.",fcam);		 
				 if(tipo.equals("ORCAMENTO")){
					 doc.add(pOrca);					   
				 }
				 
				 
				 //RODA PÉ
				 Paragraph pRod = new Paragraph(OpusERP4UI.getEmpresa().getNome_fantasia()+" - É VETADA A AUTENTICAÇÃO DESTE DOCUMENTO",fBold);
				 pRod.setAlignment(Element.ALIGN_CENTER);

				 
				 PdfPCell pCellRod = new PdfPCell();
				 pCellRod.addElement(pRod);
				 pCellRod.setBorderWidthLeft(0);
				 pCellRod.setBorderWidthRight(0);
				 pCellRod.setPaddingBottom(5);
				 
				 Paragraph pDataAtual = new Paragraph("OpusERP4, Emissão: "+date+" às "+hora+" por "+OpusERP4UI.getUsuarioLogadoUI().getUsername(),fcamBold	);
				 PdfPCell pCellDataAtul = new PdfPCell();
				 pCellDataAtul.addElement(pDataAtual);
				 pCellDataAtul.setBorderWidth(0);

				  Rectangle page = doc.getPageSize();
				  PdfPTable foot = new PdfPTable(new float[] {1f});
				  foot.setWidthPercentage(100f);
			      foot.addCell(pCellRod);
			      foot.addCell(pCellDataAtul);
			      foot.setTotalWidth(page.getWidth() - doc.leftMargin() - doc.rightMargin());
			      foot.writeSelectedRows(0, -4, doc.leftMargin(), doc.bottomMargin() + 35,pdfWriter.getDirectContent());
			      
				 
						 
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

