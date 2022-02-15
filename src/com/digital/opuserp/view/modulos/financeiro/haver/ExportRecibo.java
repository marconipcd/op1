package com.digital.opuserp.view.modulos.financeiro.haver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ComoNosConheceu;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Notification;

public class ExportRecibo implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportRecibo(Haver haver)throws Exception {

		EntityManager em = ConnUtil.getEntity();
		
		
		Document doc;
		
		doc = new Document(PageSize.A4, 24, 24, 24, 24);
		
		

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

			// Cabeçalho
			Empresa empresa = em.find(Empresa.class, OpusERP4UI.getEmpresa().getId());
			DataUtil dtUtil = new DataUtil();
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");	
			String hora;
			hora = " às "+ sdf.format(new Date());
			
			String 	date = dtUtil.getDataExtenso(new Date());

			StringBuilder SbCabecalho = new StringBuilder();
			SbCabecalho.append("OPUS ERP4"+"\n"+"EMPRESA:"+"\n"+"EMISSÃO:"+"\n"+"OPERADOR:");
			
			StringBuilder SbCabecalhoVl = new StringBuilder();
			SbCabecalhoVl.append("\n"+empresa.getNome_fantasia()+"\n"+date+hora+"\n"+OpusERP4UI.getUsuarioLogadoUI().getUsername());
			
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

			// SUBTITULO
			Paragraph pExport = new Paragraph("RECIBO DE HAVER",fTitulo);
			pExport.setAlignment(Element.ALIGN_CENTER);
			pExport.setSpacingAfter(10);
			doc.add(pExport);
			
			
				
			
			
			
			
			
			// Estilos de Fonts
			Font fValores = new Font(FontFamily.COURIER, 6, Font.NORMAL);

			
			
		} finally {
			if (doc.isOpen() && doc != null) {
				doc.close();
			}
		}

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

	private String changeHeaderColumn(String string) {

		switch (string) {

		case "id":
			return "CÓDIGO";
			// break;

		case "nome_razao":
			return "NOME/RAZÃO SOCIAL";
			// break;

		case "tipo_pessoa":
			return "TIPO PESSOA";
			// break;
			
		case "doc_cpf_cnpj":
			return "CPF/CNPJ";
			// break;

		case "doc_rg_insc_estadual":
			return "IE/RG";
			// break;

		case "categoria.nome":
			return "CATEGORIA";
			// break;

		case "como_nos_conheceu.nome":
			return "COMO CONHECEU";
			// break;
			
		case "nome_fantasia":
			return "NOME FANTASIA";
			// break;

		case "contato":
			return "CONTATO";
			// break;

		case "sexo":
			return "SEXO";
			// break;

		case "data_nascimento":
			return "DATA NASC.";
			// break;

		case "telefone1":
			return "TEL. PRINCIPAL";
			// break;

		case "telefone2":
			return "TEL. ALT 1";
			// break;

		case "celular1":
			return "TEL. ALT 2";
			// break;

		case "celular2":
			return "TEL. ALT 3";
			// break;

		case "email":
			return "EMAIL PRINCIPAL";
			// break;
			
		case "msn":
			return "EMAIL ALT";
			// break;

		case "como_nos_conheceu":
			return "COMO CONHECEU";
			// break;

		case "obs":
			return "OBS";
			// break;

		case "data_cadastro":
			return "CADASTRO";
			// break;

		case "data_alteracao":
			return "ALTERAÇÃO";
			// break;

		case "status":
			return "STATUS";
			// break;

		case "como_quer_ser_chamado":
			return "COMO QUER SER CHAMADO";
			// break;
			
		case "endereco_principal.cep":
			return "CEP";
			// break;

		case "endereco_principal.numero":
			return "NÚMERO";
			// break;
			
		case "endereco_principal.endereco":
			return "ENDEREÇO";
			// break;
			
		case "endereco_principal.bairro":
			return "BAIRRO";
			// break;
			
		case "endereco_principal.cidade":
			return "CIDADE";
			// break;
			
		case "endereco_principal.uf":
			return "UF";
			// break;
			
		case "endereco_principal.pais":
			return "PAÍS";
			// break;
			
		case "endereco_principal.complemento":
			return "COMPLEMENTO";
			// break;
			
		case "tratamento":
			return "TRATAMENTO";
			// break;Filtro
		case "endereco_principal.referencia":
			return "REFERÊNCIA";
			// break;Filtro

		default:
			return "Coluna Não Identificada";
			// break;
		}

	}
	
	public String selectCampo(String s) {
		
		String filtro = "";
		if(s.equals("Nome/Razão Social")){
			filtro = "nome_razao";			
		}else if(s.equals("Código")){
			filtro = "id";			
		}else if(s.equals("CPF/CNPJ")){
			filtro = "doc_cpf_cnpj";			
		}else if(s.equals("Inscrição Estadual/RG")){
			filtro = "doc_rg_insc_estadual";			
		}else if(s.equals("Nome Fantasia")){
			filtro = "nome_fantasia";			
		}else if(s.equals("Sexo")){
			filtro = "sexo";			
		}else if(s.equals("Data de Nascimento")){
			filtro = "data_nascimento";			
		}else if(s.equals("Telefone Principal")){
			filtro = "telefone1";			
		}else if(s.equals("Telefone Alternativo")){
			filtro = "telefone2";			
		}else if(s.equals("Tratamento")){
			filtro = "tratamento";			
		}else if(s.equals("Nome Fantasia")){
			filtro = "nome_fantasia";			
		}else if(s.equals("Contato")){
			filtro = "contato";			
		}else if(s.equals("Telefone Alternativo 2")){
			filtro = "celular1";			
		}else if(s.equals("Telefone Alternativo 3")){
			filtro = "celular2";			
		}else if(s.equals("Email Principal")){
			filtro = "email";			
		}else if(s.equals("Como nos Conheceu?")){
			filtro = "como_nos_conheceu.nome";			
		}else if(s.equals("Email Alternativo")){
			filtro = "msn";			
		}else if(s.equals("OBS")){
			filtro = "obs";			
		}else if(s.equals("Data de Cadastro")){
			filtro = "data_cadastro";			
		}else if(s.equals("Data de Alteração")){
			filtro = "data_alteracao";			
		}else if(s.equals("Categoria")){
			filtro = "categoria.nome";			
		}else if(s.equals("Como Quer Ser Chamado")){
			filtro = "como_quer_ser_chamado";			
		}else if(s.equals("Status")){
			filtro = "status";			
		}else if(s.equals("CEP")){
			filtro = "endereco_principal.cep";			
		}else if(s.equals("Endereço")){
			filtro = "endereco_principal.endereco";			
		}else if(s.equals("Número")){
			filtro = "endereco_principal.numero";			
		}else if(s.equals("Bairro")){
			filtro = "endereco_principal.bairro";			
		}else if(s.equals("Cidade")){
			filtro = "endereco_principal.cidade";			
		}else if(s.equals("Pais")){
			filtro = "endereco_principal.pais";			
		}else if(s.equals("Complemento")){
			filtro = "endereco_principal.complemento";			
		}else if(s.equals("Referencia")){
			filtro = "endereco_principal.referencia";			
		}else if(s.equals("Tipo Pessoa")){
			filtro = "tipo_pessoa";			
		}
				
		return filtro;
	}

	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
	}

}
