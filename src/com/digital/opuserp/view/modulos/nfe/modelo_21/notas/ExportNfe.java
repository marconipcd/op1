package com.digital.opuserp.view.modulos.nfe.modelo_21.notas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.digital.opuserp.domain.Cfop;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.util.StringUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.data.Container;
import com.vaadin.server.StreamResource.StreamSource;

public class ExportNfe implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	Container container;

	public ExportNfe(String titulo, Container container)throws Exception {

		
		this.container = container;
		
		Document doc;
	//	if(orientacao.equals("RETRATO")){
	//		doc = new Document(PageSize.A4, 24, 24, 24, 24);
	//	}else{
			doc = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
	//	}
		

		try {

			
			
			// -----------BUSCA

			PdfWriter writer = PdfWriter.getInstance(doc, baos);
			doc.open();
			
			
			//Estilos de Fonts
			Font fCaptions = new Font(FontFamily.HELVETICA, 6);
			Font fCaptionsBold = new Font(FontFamily.HELVETICA, 6, Font.BOLD);
			Font fCampo = new Font(FontFamily.HELVETICA, 5);
			Font fCampoBold = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font fConteudo = new Font(FontFamily.HELVETICA, 7, Font.BOLD);
			Font fTitulo  = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font fSubTitulo  = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
			Font fCab = new Font(FontFamily.HELVETICA, 8);
			Font fValores = new Font(FontFamily.COURIER, 6, Font.NORMAL);
			

			// SUBTITULO
			Paragraph pExport = new Paragraph(titulo,fTitulo);
			pExport.setAlignment(Element.ALIGN_CENTER);
			pExport.setSpacingAfter(10);
			doc.add(pExport);

			PdfPTable tbConteudo = new PdfPTable(new float[]{0.2f,0.4f,0.4f,1f,0.3f,0.4f,0.1f,0.8f,0.2f,0.3f});
			
			tbConteudo.setWidthPercentage(100f);
			
			
			tbConteudo.addCell(buildPdfCelItemTopo("Nº NF",true, false));			
			tbConteudo.addCell(buildPdfCelItemTopo("Data Emissão",true, false));
			tbConteudo.addCell(buildPdfCelItemTopo("Data Prestação",true, false));
			tbConteudo.addCell(buildPdfCelItemTopo("Cliente",true, false));
			tbConteudo.addCell(buildPdfCelItemTopo("CNPJ/CPF",true, false));
			tbConteudo.addCell(buildPdfCelItemTopo("Inscrição Estadual",true, false));
			tbConteudo.addCell(buildPdfCelItemTopo("CFOP",true, false));
			tbConteudo.addCell(buildPdfCelItemTopo("Natureza Operação",true, false));
			tbConteudo.addCell(buildPdfCelItemTopo("Valor Total",true, false));
			tbConteudo.addCell(buildPdfCelItemTopo("Status",true, false));
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			double total_cancelado = 0;
			double total_aceito = 0;
			for (Object itemId: container.getItemIds().toArray()) {
				
				
					String id, data_emissao, data_prestacao, nome_razao, doc_cpf_cnpj, rg_insc_estadual;
					
					id = container.getItem(itemId).getItemProperty("id").getValue() != null ? formatarCodNf(StringUtil.preencheCom(container.getItem(itemId).getItemProperty("id").getValue().toString(), "0", 9, 1)) : "";
					data_emissao = container.getItem(itemId).getItemProperty("data_emissao").getValue() != null ? sdf.format(container.getItem(itemId).getItemProperty("data_emissao").getValue()) : "";
					data_prestacao = container.getItem(itemId).getItemProperty("data_prestacao").getValue() != null ? sdf.format(container.getItem(itemId).getItemProperty("data_prestacao").getValue()) : "";
					nome_razao = container.getItem(itemId).getItemProperty("cliente").getValue() != null ? ((Cliente)container.getItem(itemId).getItemProperty("cliente").getValue()).getNome_razao() : "";
					doc_cpf_cnpj = container.getItem(itemId).getItemProperty("cliente").getValue() != null ? ((Cliente)container.getItem(itemId).getItemProperty("cliente").getValue()).getDoc_cpf_cnpj() : "";
				
					tbConteudo.addCell(buildPdfCelItem(id,true,false));
					tbConteudo.addCell(buildPdfCelItem(data_emissao,true,false));
					tbConteudo.addCell(buildPdfCelItem(data_prestacao,true,false));
					tbConteudo.addCell(buildPdfCelItem(nome_razao,true,false));
					tbConteudo.addCell(buildPdfCelItem(doc_cpf_cnpj,true,false));
				

				String valor = Real.formatDbToString(String.valueOf((double)container.getItem(itemId).getItemProperty("bc_icms").getValue()));
				if(((Cliente)container.getItem(itemId).getItemProperty("cliente").getValue()).getDoc_cpf_cnpj().length() == 11){
					String ins = "ISENTO";							
					tbConteudo.addCell(buildPdfCelItem(ins,true,false));
				}else{
					String ins = ((Cliente)container.getItem(itemId).getItemProperty("cliente").getValue()).getDoc_rg_insc_estadual() != null &&  !((Cliente)container.getItem(itemId).getItemProperty("cliente").getValue()).getDoc_rg_insc_estadual().equals("")  ? ((Cliente)container.getItem(itemId).getItemProperty("cliente").getValue()).getDoc_rg_insc_estadual() : "ISENTO";							
					tbConteudo.addCell(buildPdfCelItem(ins,true,false));
				}
				
				
				tbConteudo.addCell(buildPdfCelItem(((Cfop)container.getItem(itemId).getItemProperty("natOpe").getValue()).getId().toString(),true,false));
				tbConteudo.addCell(buildPdfCelItem(((Cfop)container.getItem(itemId).getItemProperty("natOpe").getValue()).getDescricao(),true,false));
				tbConteudo.addCell(buildPdfCelItem(valor,false,false));
				
				
				String status = container.getItem(itemId).getItemProperty("situacao_doc").getValue().toString();
				tbConteudo.addCell(buildPdfCelItem(status.equals("N") ? "Aceito" : "Cancelado",true,false));
				
				if(status.equals("S")){
					total_cancelado = total_cancelado + Real.formatStringToDBDouble(valor);
				}
				
				if(status.equals("N")){
					total_aceito = total_aceito + Real.formatStringToDBDouble(valor);
				}
			}
			
			
			
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelItemTopo("TOTAL CANCELADO",true,false));
			
			tbConteudo.addCell(buildPdfCelItemTopo(Real.formatDbToString(String.valueOf(total_cancelado)),false,false));
			tbConteudo.addCell(buildPdfCelVazio());
			
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelVazio());
			tbConteudo.addCell(buildPdfCelItemTopo("TOTAL ACEITO",true,false));			
			tbConteudo.addCell(buildPdfCelItemTopo(Real.formatDbToString(String.valueOf(total_aceito)),false,false));
			tbConteudo.addCell(buildPdfCelVazio());
					
			
			doc.add(tbConteudo);
			
			PdfPTable tbREg = new PdfPTable(new float[]{1f});
			tbREg.setWidthPercentage(100);
			
			
			
			Paragraph p  = new Paragraph(container.size()+" registros encontrados", fCampo);
			PdfPCell pCellItem = new PdfPCell(p);		
			
			pCellItem.setPaddingTop(2);
			pCellItem.setPaddingBottom(4);
			pCellItem.setBackgroundColor(new BaseColor(255, 255, 255));
			//pCellItem.setBorderColor(new BaseColor(255, 255, 255));
			pCellItem.disableBorderSide(PdfPCell.TOP);
			pCellItem.disableBorderSide(PdfPCell.LEFT);
			pCellItem.disableBorderSide(PdfPCell.RIGHT);
			pCellItem.disableBorderSide(PdfPCell.BOTTOM);
			
			tbREg.addCell(pCellItem);
			doc.add(tbREg);
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if (doc.isOpen() && doc != null) {
				doc.close();
			}
		}

	}
	private String formatarCodNf(String s){
		
		return s.substring(0, 3)+"."+s.substring(3, 6)+"."+s.substring(6, 9);
	}
	
	private PdfPCell buildPdfCelItemTopo(String s, boolean left, boolean centralizar){
		Font fCampo = new Font(FontFamily.HELVETICA, 5);
		fCampo.setStyle(Font.BOLD);
		
		
		Paragraph p  = new Paragraph(s.toUpperCase(), fCampo);
		PdfPCell pCellItem = new PdfPCell(p);		
		if(!left && !centralizar){			
			p.setAlignment(Element.ALIGN_RIGHT);
			pCellItem.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		}
		
		if(centralizar){
			p.setAlignment(Element.ALIGN_CENTER);
			pCellItem.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
		}
		
		pCellItem.setPaddingTop(2);
		pCellItem.setPaddingBottom(4);
		pCellItem.setBackgroundColor(new BaseColor(232, 235, 237));
		
		//pCellItem.setBorderColor(new BaseColor(255, 255, 255));
		//pCellItem.setBorderWidth(1.5f);
		
		
		return pCellItem;
	}
	
	private PdfPCell buildPdfCelItem(String s, boolean left, boolean centralizar){
		Font fCampo = new Font(FontFamily.HELVETICA, 5);
		
		Paragraph p  = new Paragraph(s, fCampo);
		PdfPCell pCellItem = new PdfPCell(p);		
		if(!left && !centralizar){			
			p.setAlignment(Element.ALIGN_RIGHT);
			pCellItem.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		}
		
		if(centralizar){
			p.setAlignment(Element.ALIGN_CENTER);
			pCellItem.setRunDirection(PdfWriter.RUN_DIRECTION_DEFAULT);
		}
		
		pCellItem.setPaddingTop(2);
		pCellItem.setPaddingBottom(4);
		pCellItem.setBackgroundColor(new BaseColor(255, 255, 255));
		//pCellItem.setBorderColor(new BaseColor(255, 255, 255));
		//pCellItem.setBorderWidth(1.5f);
		
		
		return pCellItem;
	}
	
	private PdfPCell buildPdfCelVazio(){
		Font fCampo = new Font(FontFamily.HELVETICA, 5);
		
		Paragraph p  = new Paragraph(" ", fCampo);
		PdfPCell pCellItem = new PdfPCell(p);		
		
		
		
		pCellItem.setPaddingTop(2);
		pCellItem.setPaddingBottom(4);
		pCellItem.setBackgroundColor(new BaseColor(255, 255, 255));
		//pCellItem.setBorderColor(new BaseColor(255, 255, 255));
		pCellItem.disableBorderSide(PdfPCell.TOP);
		pCellItem.disableBorderSide(PdfPCell.LEFT);
		pCellItem.disableBorderSide(PdfPCell.RIGHT);
		pCellItem.disableBorderSide(PdfPCell.BOTTOM);
		//pCellItem.setBorderWidth(1.5f);
		
		
		return pCellItem;
	}
	
	
	public static JFreeChart generatePieChart(String titulo, Map<String, Integer> lista) {
        DefaultPieDataset dataSet = new DefaultPieDataset();
              
        Set<String> chaves = lista.keySet();  
        for (String chave : chaves)  
        {            
        	System.out.println(chave + lista.get(chave));  
            dataSet.setValue(chave,lista.get(chave));            
        } 
        
        JFreeChart chart = ChartFactory.createPieChart(titulo, dataSet, false, true, false);

        return chart;
    }
	
	public static JFreeChart generateBarChart(String titulo, Map<String, Integer> lista) {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		             
        Set<String> chaves = lista.keySet();
        Integer i = 0;
        for (String chave : chaves)  
        {            
        	i++; 
            dataSet.setValue(lista.get(chave), lista.get(chave), chave);	           
        } 
        
        JFreeChart chart = ChartFactory.createBarChart(titulo,"Categoria","Valor",
                dataSet, PlotOrientation.VERTICAL, false, true, false);

        return chart;
    }

	private String changeHeaderColumn(String s) {

		String filtro = "";
		if(s.equals("id")){
			filtro = "COD.";			
		}else if(s.equals("cliente.nome_razao")){
			filtro = "CLIENTE";			
		}else if(s.equals("plano.nome")){
			filtro = "PLANO";			
		}else if(s.equals("base.identificacao")){
			filtro = "CONCENT.";	
		}else if(s.equals("interfaces")){
			filtro = "INTERFACE";	
		}else if(s.equals("signal_strength")){
			filtro = "SIG. STRE.";			
		}else if(s.equals("swith.identificacao")){
			filtro = "SWITH";			
		}else if(s.equals("material.nome")){
			filtro = "MATERIAL";			
		}else if(s.equals("contrato.nome")){
			filtro = "CONTRATO";			
		}else if(s.equals("login")){
			filtro = "USERNAME";			
		}else if(s.equals("senha")){
			filtro = "SENHA";			
		}else if(s.equals("endereco_ip")){
			filtro = "END. IP";
		}else if(s.equals("endereco_mac")){
			filtro = "END. MAC";
		}else if(s.equals("status_2")){
			filtro = "STATUS";			
		}else if(s.equals("regime")){
			filtro = "REGIME";			
		}else if(s.equals("data_instalacao")){
			filtro = "INSTAL.";
		}else if(s.equals("endereco.cep")){
			filtro = "CEP";			
		}else if(s.equals("endereco.endereco")){
			filtro = "ENDEREÇO";			
		}else if(s.equals("endereco.numero")){
			filtro = "Nº";			
		}else if(s.equals("endereco.bairro")){
			filtro = "BAIRRO";			
		}else if(s.equals("endereco.cidade")){
			filtro = "CIDADE";			
		}else if(s.equals("endereco.uf")){
			filtro = "UF";			
		}else if(s.equals("endereco.pais")){
			filtro = "PAIS";			
		}else if(s.equals("endereco.complemento")){
			filtro = "COMPLEMENTO";			
		}else if(s.equals("endereco.referencia")){
			filtro = "REFERENCIA";			
		}
				
		return filtro;

	}
	
	public String selectCampo(String s) {
		
		String filtro = "";
		if(s.equals("Código")){
			filtro = "id";			
		}else if(s.equals("Cliente")){
			filtro = "cliente.nome_razao";			
		}else if(s.equals("Plano")){
			filtro = "plano.nome";			
		}else if(s.equals("Concentrador")){
			filtro = "base.identificacao";
		}else if(s.equals("Interface")){
			filtro = "interfaces";
		}else if(s.equals("Signal Strength")){
			filtro = "signal_strength";			
		}else if(s.equals("Swith")){
			filtro = "swith.identificacao";			
		}else if(s.equals("Material")){
			filtro = "material.nome";			
		}else if(s.equals("Contrato")){
			filtro = "contrato.nome";			
		}else if(s.equals("Username")){
			filtro = "login";			
		}else if(s.equals("Senha")){
			filtro = "senha";			
		}else if(s.equals("Endereço IP")){
			filtro = "endereco_ip";			
		}else if(s.equals("Endereço MAC")){
			filtro = "endereco_mac";			
		}else if(s.equals("Status")){
			filtro = "status_2";			
		}else if(s.equals("Regime")){
			filtro = "regime";			
		}else if(s.equals("Data Instalação")){
			filtro = "data_instalacao";
		}else if(s.equals("CEP")){
			filtro = "endereco.cep";			
		}else if(s.equals("Endereço")){
			filtro = "endereco.endereco";			
		}else if(s.equals("Número")){
			filtro = "endereco.numero";			
		}else if(s.equals("Bairro")){
			filtro = "endereco.bairro";			
		}else if(s.equals("Cidade")){
			filtro = "endereco.cidade";			
		}else if(s.equals("UF")){
			filtro = "endereco.uf";			
		}else if(s.equals("Pais")){
			filtro = "endereco.pais";			
		}else if(s.equals("Complemento")){
			filtro = "endereco.complemento";			
		}else if(s.equals("Referencia")){
			filtro = "endereco.referencia";
		}		
		return filtro;
	}

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}
