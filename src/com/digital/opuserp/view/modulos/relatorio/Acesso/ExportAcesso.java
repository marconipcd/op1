package com.digital.opuserp.view.modulos.relatorio.Acesso;

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
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Swith;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.itextpdf.text.BaseColor;
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
import com.vaadin.ui.Notification;

public class ExportAcesso implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportAcesso(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista, List<Object> columns)throws Exception {

		EntityManager em = ConnUtil.getEntity();
		
		
		Document doc;
		if(orientacao.equals("RETRATO")){
			doc = new Document(PageSize.A4, 24, 24, 24, 24);
		}else{
			doc = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
		}
		

		try {

			// -----------BUSCA

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<AcessoCliente> criteriaQuery = cb.createQuery(AcessoCliente.class);
			Root<AcessoCliente> rootAcesso = criteriaQuery.from(AcessoCliente.class);
			EntityType<AcessoCliente> type = em.getMetamodel().entity(AcessoCliente.class);

			List<Predicate> criteria = new ArrayList<Predicate>();

			criteria.add(cb.equal(rootAcesso.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

			if (lista.size() > 0) {
				for (SearchParameters s : lista) {

					if (s.getOperador().equals("IGUAL")) {
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("cliente").<String>get("email")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("cliente").<String>get("telefone1")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("cliente").<String>get("telefone2")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("cliente").<String>get("celular1")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("cliente").<String>get("celular2")), s.getValor().toLowerCase()));
						}

						if (s.getCampo().equals("plano.nome")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("plano").<String>get("nome")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("base.identificacao")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("base").<String>get("identificacao")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("swith.identificacao")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("swith").<String>get("identificacao")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("material.nome")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("material").<String>get("nome")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("contrato.nome")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("contrato").<String>get("nome")), s.getValor().toLowerCase()));
						}
						
						//enderecos
						if (s.getCampo().equals("endereco.cep")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("endereco").<String>get("cep")), s.getValor().toLowerCase()));
						}						
						if (s.getCampo().equals("endereco.numero")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("endereco").<String>get("numero")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.endereco")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("endereco").<String>get("endereco")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.bairro")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("endereco").<String>get("bairro")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.cidade")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("endereco").<String>get("cidade")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.uf")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("endereco").<String>get("uf")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.pais")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("endereco").<String>get("pais")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.complemento")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("endereco").<String>get("complemento")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.referencia")) {
							criteria.add(cb.like(cb.lower(rootAcesso.join("endereco").<String>get("referencia")), s.getValor().toLowerCase()));
						}

						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.email") 
								&& !s.getCampo().equals("plano.nome") && 
								!s.getCampo().equals("base.identificacao") && !s.getCampo().equals("swith.identificacao") &&
								!s.getCampo().equals("material.nome") && !s.getCampo().equals("contrato.nome")&&
								!s.getCampo().equals("endereco.cep") && !s.getCampo().equals("endereco.numero") &&
								!s.getCampo().equals("endereco.endereco") && !s.getCampo().equals("endereco.bairro") &&
								!s.getCampo().equals("endereco.uf") && !s.getCampo().equals("endereco.cidade") && 
								!s.getCampo().equals("endereco.pais") && !s.getCampo().equals("endereco.complemento") &&
								!s.getCampo().equals("endereco.referencia")) {
							criteria.add(cb.like(cb.lower(rootAcesso.<String>get(s.getCampo())), s.getValor().toLowerCase()));
						}

					}else if (s.getOperador().equals("DIFERENTE")) {
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("email")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("telefone1")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("telefone2")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("celular1")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("celular2")), s.getValor().toLowerCase()));
						}

						if (s.getCampo().equals("plano.nome")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("plano").<String>get("nome")),s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("base.identificacao")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("base").<String>get("identificacao")),s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("swith.identificacao")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("swith").<String>get("identificacao")),s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("material.nome")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("material").<String>get("nome")),s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("contrato.nome")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("contrato").<String>get("nome")),s.getValor().toLowerCase()));
						}
						
						//enderecos
						if (s.getCampo().equals("endereco.cep")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("cep")),  s.getValor().toLowerCase()));
						}						
						if (s.getCampo().equals("endereco.numero")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("numero")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.endereco")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("endereco")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.bairro")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("bairro")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.cidade")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("cidade")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.uf")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("uf")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.pais")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("pais")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.complemento")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("complemento")),  s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("endereco.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("referencia")), s.getValor().toLowerCase()));
						}
						
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.email") && !s.getCampo().equals("cliente.telefone1") && !s.getCampo().equals("cliente.telefone2") && !s.getCampo().equals("cliente.celular1") && !s.getCampo().equals("cliente.celular2") &&!s.getCampo().equals("plano.nome") && 
								!s.getCampo().equals("base.identificacao") && !s.getCampo().equals("swith.identificacao") &&
								!s.getCampo().equals("material.nome") && !s.getCampo().equals("contrato.nome")&&
								!s.getCampo().equals("endereco.cep") && !s.getCampo().equals("endereco.numero") &&
								!s.getCampo().equals("endereco.endereco") && !s.getCampo().equals("endereco.bairro") &&
								!s.getCampo().equals("endereco.uf") && !s.getCampo().equals("endereco.cidade") && 
								!s.getCampo().equals("endereco.pais") && !s.getCampo().equals("endereco.complemento") &&
								!s.getCampo().equals("endereco.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.<String>get(s.getCampo())), s.getValor().toLowerCase()));
						}

					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("cliente").<String>get("nome_razao")),"%" + s.getValor().toLowerCase()+ "%"));							
						}
						
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("cliente").<String>get("email")),"%" + s.getValor().toLowerCase()+ "%"));							
						}
						
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("cliente").<String>get("telefone1")),"%" + s.getValor().toLowerCase()+ "%"));							
						}
						
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("cliente").<String>get("telefone2")),"%" + s.getValor().toLowerCase()+ "%"));							
						}
						
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("cliente").<String>get("celular1")),"%" + s.getValor().toLowerCase()+ "%"));							
						}
						
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("cliente").<String>get("celular2")),"%" + s.getValor().toLowerCase()+ "%"));							
						}

						if (s.getCampo().equals("plano.nome")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("plano").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("base.identificacao")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("base").<String>get("identificacao")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("swith.identificacao")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("swith").<String>get("identificacao")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("material.nome")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("material").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("contrato.nome")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("contrato").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						//enderecos
						if (s.getCampo().equals("endereco.cep")) {							
							criteria.add(cb.like(cb.lower(rootAcesso.get("endereco").<String>get("cep")), "%" + s.getValor().toLowerCase()+ "%"));
						}						
						if (s.getCampo().equals("endereco.numero")) {							
							criteria.add(cb.like(cb.lower(rootAcesso.get("endereco").<String>get("numero")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.endereco")) {							
							criteria.add(cb.like(cb.lower(rootAcesso.get("endereco").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.bairro")) {							
							criteria.add(cb.like(cb.lower(rootAcesso.get("endereco").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.cidade")) {							
							criteria.add(cb.like(cb.lower(rootAcesso.get("endereco").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.uf")) {							
							criteria.add(cb.like(cb.lower(rootAcesso.get("endereco").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.pais")) {							
							criteria.add(cb.like(cb.lower(rootAcesso.get("endereco").<String>get("pais")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.complemento")) {							
							criteria.add(cb.like(cb.lower(rootAcesso.get("endereco").<String>get("complemento")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.referencia")) {
							criteria.add(cb.like(cb.lower(rootAcesso.get("endereco").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
						}
					
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.email") && !s.getCampo().equals("plano.nome") && 
								!s.getCampo().equals("base.identificacao") && !s.getCampo().equals("swith.identificacao") &&
								!s.getCampo().equals("material.nome") && !s.getCampo().equals("contrato.nome")&&
								!s.getCampo().equals("endereco.cep") && !s.getCampo().equals("endereco.numero") &&
								!s.getCampo().equals("endereco.endereco") && !s.getCampo().equals("endereco.bairro") &&
								!s.getCampo().equals("endereco.uf") && !s.getCampo().equals("endereco.cidade") && 
								!s.getCampo().equals("endereco.pais") && !s.getCampo().equals("endereco.complemento") &&
								!s.getCampo().equals("endereco.referencia")) {
							criteria.add(cb.like(cb.lower(rootAcesso.<String>get(s.getCampo())), "%" +s.getValor().toLowerCase()+ "%"));
						}
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("nome_razao")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("email")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("telefone1")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("telefone2")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("celular1")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("cliente").<String>get("celular2")),"%" + s.getValor().toLowerCase()+ "%"));
						}

						if (s.getCampo().equals("plano.nome")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("plano").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("base.identificacao")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("base").<String>get("identificacao")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("swith.identificacao")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("swith").<String>get("identificacao")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("material.nome")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("material").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("contrato.nome")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("contrato").<String>get("nome")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						
						//enderecos
						if (s.getCampo().equals("endereco.cep")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("cep")), "%" + s.getValor().toLowerCase()+ "%"));
						}						
						if (s.getCampo().equals("endereco.numero")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("numero")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.endereco")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.bairro")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.cidade")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.uf")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.pais")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("pais")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.complemento")) {							
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("complemento")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("endereco.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.get("endereco").<String>get("referencia")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.email") && !s.getCampo().equals("plano.nome") && 
								!s.getCampo().equals("base.identificacao") && !s.getCampo().equals("swith.identificacao") &&
								!s.getCampo().equals("material.nome") && !s.getCampo().equals("contrato.nome")&&
								!s.getCampo().equals("endereco.cep") && !s.getCampo().equals("endereco.numero") &&
								!s.getCampo().equals("endereco.endereco") && !s.getCampo().equals("endereco.bairro") &&
								!s.getCampo().equals("endereco.uf") && !s.getCampo().equals("endereco.cidade") && 
								!s.getCampo().equals("endereco.pais") && !s.getCampo().equals("endereco.complemento") &&
								!s.getCampo().equals("endereco.referencia")) {
							criteria.add(cb.notLike(cb.lower(rootAcesso.<String>get(s.getCampo())), s.getValor().toLowerCase()));
						}
						
					} else if (s.getOperador().equals("MAIOR QUE")) {
						
						try{						
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.greaterThan(rootAcesso.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Date.class)){
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.greaterThan(rootAcesso.<Date> get(s.getCampo()), dtValor));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
						
					} else if (s.getOperador().equals("MENOR QUE")) {
						
						
						
						try{						
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.lessThan(rootAcesso.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Date.class)){
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)+ date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.lessThan(rootAcesso.<Date> get(s.getCampo()), dtValor));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
						
						
					} else if (s.getOperador().equals("MAIOR IGUAL QUE")) {
						
						
						try{						
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.greaterThanOrEqualTo(rootAcesso.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Date.class)){
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)+ date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.greaterThanOrEqualTo(rootAcesso.<Date> get(s.getCampo()), dtValor));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						
						
					} else if (s.getOperador().equals("MENOR IGUAL QUE")) {
						
						try{						
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.lessThanOrEqualTo(rootAcesso.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
							}
							
							if(rootAcesso.get(s.getCampo()).getJavaType().equals(Date.class)){
								String date = s.getValor();
								Date dtValor = new Date(Date.parse(date.substring(3, 6)	+ date.substring(0, 3) + date.substring(6, 10)));
								criteria.add(cb.lessThanOrEqualTo(rootAcesso.<Date> get(s.getCampo()), dtValor));
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}

			if (criteria.size() == 0) {
				throw new RuntimeException("no criteria");
			} else if (criteria.size() == 1) {
				criteriaQuery.where(criteria.get(0));
			} else {
				criteriaQuery.where(cb.and(criteria.toArray(new Predicate[0])));
			}

			
			if (selectCampo(order).equals("cliente.nome_razao")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("cliente").get("nome_razao")));
			}else if (selectCampo(order).equals("cliente.email")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("cliente").get("email")));
			}else if (selectCampo(order).equals("cliente.telefone1")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("cliente").get("telefone1")));
			}else if (selectCampo(order).equals("cliente.telefone2")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("cliente").get("telefone2")));
			}else if (selectCampo(order).equals("cliente.celular1")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("cliente").get("celular1")));
			}else if (selectCampo(order).equals("cliente.celular2")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("cliente").get("celular2")));
			}else if (selectCampo(order).equals("plano.nome")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("plano").get("nome")));
			}else if (selectCampo(order).equals("base.identificacao")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("base").get("identificacao")));
			}else if (selectCampo(order).equals("swith.identificacao")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("swith").get("identificacao")));
			}else if (selectCampo(order).equals("material.nome")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("material").get("nome")));
			}else if (selectCampo(order).equals("contrato.nome")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("contrato").get("nome")));
				
			}else if (selectCampo(order).equals("endereco.cep")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("endereco").get("cep")));
			}else if (selectCampo(order).equals("endereco.numero")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("endereco").get("numero")));
			}else if (selectCampo(order).equals("endereco.endereco")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("endereco").get("endereco")));
			}else if (selectCampo(order).equals("endereco.bairro")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("endereco").get("bairro")));
			}else if (selectCampo(order).equals("endereco.cidade")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("endereco").get("cidade")));
			}else if (selectCampo(order).equals("endereco.uf")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("endereco").get("uf")));
			}else if (selectCampo(order).equals("endereco.pais")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("endereco").get("pais")));
			}else if (selectCampo(order).equals("endereco.complemento")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("endereco").get("complemento")));
			}else if (selectCampo(order).equals("endereco.referencia")) {				
				criteriaQuery.orderBy(cb.asc(rootAcesso.get("endereco").get("referencia")));
			}else{								
				criteriaQuery.orderBy(cb.asc(rootAcesso.get(selectCampo(order))));
			}
			
			
		
			TypedQuery q = em.createQuery(criteriaQuery);		
			
			
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE ACESSO",fTitulo);
			pExport.setAlignment(Element.ALIGN_CENTER);
			pExport.setSpacingAfter(10);
			doc.add(pExport);
			
			//FiILTROS					
			StringBuilder SbTipo = new StringBuilder();
			StringBuilder SbOperad =  new StringBuilder();
			StringBuilder SbValor = new StringBuilder();
			
			if (lista.size() > 0) {
				for (SearchParameters s : lista) {	
	
					SbTipo.append(changeHeaderColumn(s.getCampo())+"\n");
					SbOperad.append(s.getOperador()+"\n");
					SbValor.append(s.getValor()+"\n");
					
				}
			}
			Paragraph pCampo = new Paragraph(SbTipo.toString(),fSubTitulo);
			pCampo.setAlignment(Element.ALIGN_LEFT);
			Paragraph pOperqador = new Paragraph(SbOperad.toString(),fCab);
			pOperqador.setAlignment(Element.ALIGN_LEFT);
			Paragraph pValor= new Paragraph(SbValor.toString(),fSubTitulo);
			pValor.setAlignment(Element.ALIGN_LEFT);

			PdfPCell pCellTipo = new PdfPCell();
			pCellTipo.setBorderWidth(0);
			pCellTipo.addElement(pCampo);	
			
			PdfPCell pCellOperador = new PdfPCell();
			pCellOperador.setBorderWidth(0);
			pCellOperador.addElement(pOperqador);		
			
			PdfPCell pCellValor = new PdfPCell();
			pCellValor.setBorderWidth(0);
			pCellValor.addElement(pValor);		
			
			PdfPTable tbTipo = new PdfPTable(new float[]{0.32f,0.30f,1f});
			tbTipo.setWidthPercentage(100f);	
			tbTipo.addCell(pCellTipo);
			tbTipo.addCell(pCellOperador);
			tbTipo.addCell(pCellValor);
			tbTipo.setSpacingAfter(10);
			
			doc.add(tbTipo);
				
			//TIPO		
			StringBuilder SbForm= new StringBuilder();
			StringBuilder SbVl= new StringBuilder();
			
			SbForm.append("TIPO:"+"\n"+"ORIENTAÇÃO:"+"\n"+"ORDENAÇÃO"+"\n");			
			SbVl.append(tipo+"\n"+orientacao+"\n"+changeHeaderColumn(selectCampo(order))+"\n");

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

			// Estilos de Fonts
			Font fValores = new Font(FontFamily.COURIER, 6, Font.NORMAL);

			List<AcessoCliente> acessos = q.getResultList();
				
			float[] f = new float[columns.size()];
			
			Integer i=0;
			for (Object c : columns) {
	
				if(changeHeaderColumn(c.toString()).equals("COD.")){
					f[i] = (0.10f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("CLIENTE")){
					f[i] = (0.55f);		
				}
				if(changeHeaderColumn(c.toString()).equals("EMAIL")){
					f[i] = (0.20f);		
				}
				if(changeHeaderColumn(c.toString()).equals("TELEFONE1")){
					f[i] = (0.20f);		
				}
				if(changeHeaderColumn(c.toString()).equals("TELEFONE2")){
					f[i] = (0.20f);		
				}
				if(changeHeaderColumn(c.toString()).equals("CELULAR1")){
					f[i] = (0.20f);		
				}
				if(changeHeaderColumn(c.toString()).equals("CELULAR2")){
					f[i] = (0.20f);		
				}
				if(changeHeaderColumn(c.toString()).equals("PLANO")){
					f[i] = (0.20f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("CONCENT.")){
					f[i] = (0.20f);		
				}
				if(changeHeaderColumn(c.toString()).equals("INTERFACE")){
					f[i] = (0.29f);		
				}
				if(changeHeaderColumn(c.toString()).equals("SIG. STRE.")){
					f[i] = (0.27f);		
				}		
				if(changeHeaderColumn(c.toString()).equals("SWITH")){
					f[i] = (0.18f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("MATERIAL")){
					f[i] = (0.65f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("CONTRATO")){
					f[i] = (0.45f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("USERNAME")){
					f[i] = (0.31f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("SENHA")){
					f[i] = (0.20f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("END. IP")){
					f[i] = (0.21f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("END. MAC")){
					f[i] = (0.26f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("STATUS")){
					f[i] = (0.17f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("REGIME")){
					f[i] = (0.15f);		
				}	
				if(changeHeaderColumn(c.toString()).equals("INSTAL.")){
					f[i] = (0.21f);		
				}	
				
				if(changeHeaderColumn(c.toString()).equals("ALT. PLANO")){
					f[i] = (0.21f);		
				}	
				
				if(changeHeaderColumn(c.toString()).equals("CEP")){
				    f[i] = (0.25f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("ENDEREÇO")){
			    	f[i] = (0.50f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("Nº")){
			    	f[i] = (0.10f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("CIDADE")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("BAIRRO")){
			    	f[i] = (0.42f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("UF")){
			    	f[i] = (0.11f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("PAIS")){
			    	f[i] = (0.20f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("COMPLEMENTO")){
			    	f[i] = (0.40f);	
			    }
			    if(changeHeaderColumn(c.toString()).equals("REFERENCIA")){
			    	f[i] = (0.37f);	
			    }		   
			    i++;
     		 }					

			
			if(tipo.equals("MULTI COLUNA")){

				PdfPTable tbTopo = new PdfPTable(f);
				tbTopo.setWidthPercentage(100f);				
				
				for (Object c : columns) {
					PdfPCell pCell = new PdfPCell(new Phrase(changeHeaderColumn(c.toString()), fCampoBold));
					pCell.setPaddingTop(2);
					pCell.setPaddingBottom(4);
					pCell.setBackgroundColor(new BaseColor(114, 131, 151));
					pCell.setBorderColor(new BaseColor(255, 255, 255));	
					pCell.setBorderWidth(1.5f);
					tbTopo.addCell(pCell);
				}
				
				doc.add(tbTopo);				
				
				
				for (AcessoCliente acesso : acessos) {
	
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(5f);
		
					PdfPTable tbConteudo = new PdfPTable(f);
					tbConteudo.setWidthPercentage(100f);
				
					
					for (Object c : columns) {
						try {
							PdfPCell pCell = new PdfPCell();
							pCell.setPaddingTop(0);
							pCell.setPaddingBottom(4);
							pCell.setBackgroundColor(new BaseColor(232, 235, 237));
							pCell.setBorderColor(new BaseColor(255, 255, 255));	
							pCell.setBorderWidth(1.5f);
	
							String metodo = "get"
									+ c.toString().substring(0, 1).toUpperCase()
									+ c.toString().substring(1,c.toString().length());
							
							metodo = metodo.replace(".nome_razao", "");
							metodo = metodo.replace(".nome", "");
							metodo = metodo.replace(".email", "");
							metodo = metodo.replace(".telefone1", "");
							metodo = metodo.replace(".telefone2", "");
							metodo = metodo.replace(".celular1", "");
							metodo = metodo.replace(".celular2", "");
							metodo = metodo.replace(".identificacao", "");
							metodo = metodo.replace(".cep", "");
							metodo = metodo.replace(".endereco", "");
							metodo = metodo.replace(".numero", "");
							metodo = metodo.replace(".bairro", "");
							metodo = metodo.replace(".cidade", "");
							metodo = metodo.replace(".uf", "");
							metodo = metodo.replace(".pais", "");
							metodo = metodo.replace(".complemento", "");
							metodo = metodo.replace(".referencia", "");
							

							Class cls = acesso.getClass();
							Method method = cls.getMethod(metodo);
							
							String valor = null;
							
							if (method.invoke(acesso) instanceof String || method.invoke(acesso) instanceof Integer || method.invoke(acesso) instanceof Date) {
	
																	
								valor = method.invoke(acesso).toString();																								
								
								Paragraph valorColuna = null;
								
								 if(c.toString().equals("data_instalacao") || c.toString().equals("data_alteracao_plano")){
									
									String dateform = dtUtil.parseDataBra(valor);
									valorColuna = new Paragraph(dateform,fCampo);									
								}else{
									valorColuna = new Paragraph(valor,fCampo);									
								}

								 pCell.addElement(valorColuna);			
								 
							
							} else if (method.invoke(acesso) instanceof Cliente) {
								Cliente cliente = (Cliente) method.invoke(acesso);
								
								if(c.toString().equals("cliente.nome_razao")){
									pCell.addElement(new Phrase(cliente.getNome_razao(),fCampo));
								}else if(c.toString().equals("cliente.email")){
									pCell.addElement(new Phrase(cliente.getEmail(),fCampo));
								}else if(c.toString().equals("cliente.telefone1")){
									pCell.addElement(new Phrase(cliente.getTelefone1(),fCampo));
								}else if(c.toString().equals("cliente.telefone2")){
									pCell.addElement(new Phrase(cliente.getTelefone2(),fCampo));
								}else if(c.toString().equals("cliente.celular1")){
									pCell.addElement(new Phrase(cliente.getCelular1(),fCampo));
								}else if(c.toString().equals("cliente.celular2")){
									pCell.addElement(new Phrase(cliente.getCelular2(),fCampo));
								}
								
								
							} else if (method.invoke(acesso) instanceof PlanoAcesso) {
								PlanoAcesso plano = (PlanoAcesso) method.invoke(acesso);
								pCell.addElement(new Phrase(plano.getNome(), fCampo));
							} else if (method.invoke(acesso) instanceof Concentrador) {
								Concentrador base = (Concentrador) method.invoke(acesso);
								pCell.addElement(new Phrase(base.getIdentificacao(), fCampo));
							} else if (method.invoke(acesso) instanceof Swith) {
								Swith swith = (Swith) method.invoke(acesso);
								pCell.addElement(new Phrase(swith.getIdentificacao(), fCampo));
							} else if (method.invoke(acesso) instanceof Produto) {
								Produto produto = (Produto) method.invoke(acesso);
								pCell.addElement(new Phrase(produto.getNome(), fCampo));
							} else if (method.invoke(acesso) instanceof ContratosAcesso) {
								ContratosAcesso contrato = (ContratosAcesso) method.invoke(acesso);
								pCell.addElement(new Phrase(contrato.getNome(), fCampo));
							}else if (method.invoke(acesso) instanceof Endereco) {
								Endereco end = (Endereco) method.invoke(acesso);
								
								String valorColuna = "";
								
								if(c.toString().equals("endereco.cep")){
									valorColuna = end.getCep();
								}else if(c.toString().equals("endereco.uf")){
									valorColuna = end.getUf();
								}else if(c.toString().equals("endereco.endereco")){
									valorColuna = end.getEndereco();
								}else if(c.toString().equals("endereco.bairro")){
									valorColuna = end.getBairro();
								}else if(c.toString().equals("endereco.cidade")){
									valorColuna = end.getCidade();
								}else if(c.toString().equals("endereco.complemento")){
									valorColuna = end.getComplemento();
								}else if(c.toString().equals("endereco.numero")){
									valorColuna = end.getNumero();
								}else if(c.toString().equals("endereco.pais")){
									valorColuna = end.getPais();
								}else if(c.toString().equals("endereco.referencia")){
									valorColuna = end.getReferencia();
								}						
								
									pCell.addElement(new Phrase(valorColuna, fCampo));
							}
							
							//tbConteudo.addCell(pTopo);
							tbConteudo.addCell(pCell);					
							
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show("ERRO!");
						}
					}
	
					doc.add(tbConteudo);
					
				}
			}
			
			if(tipo.equals("COLUNA ÚNICA")){
				for (AcessoCliente acesso : acessos) {
	
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(25f);
	
					for (Object c : columns) {
						try {
							PdfPCell pCell = new PdfPCell();
							pCell.setPaddingBottom(5);
							pCell.setPaddingTop(0);
							pCell.addElement(new Phrase(changeHeaderColumn(c.toString()), fCampo));
							
							String metodo = "get"
									+ c.toString().substring(0, 1).toUpperCase()
									+ c.toString().substring(1,c.toString().length());
							
							metodo = metodo.replace(".nome_razao", "");
							metodo = metodo.replace(".cliente", "");
							metodo = metodo.replace(".email", "");
							metodo = metodo.replace(".telefone1", "");
							metodo = metodo.replace(".telefone2", "");
							metodo = metodo.replace(".celular1", "");
							metodo = metodo.replace(".celular2", "");
							metodo = metodo.replace(".nome", "");
							metodo = metodo.replace(".identificacao", "");
							metodo = metodo.replace(".cep", "");
							metodo = metodo.replace(".endereco", "");
							metodo = metodo.replace(".numero", "");
							metodo = metodo.replace(".bairro", "");
							metodo = metodo.replace(".cidade", "");
							metodo = metodo.replace(".uf", "");
							metodo = metodo.replace(".pais", "");
							metodo = metodo.replace(".complemento", "");
							metodo = metodo.replace(".referencia", "");
			
							Class cls = acesso.getClass();
							Method method = cls.getMethod(metodo);
	
							if(method.invoke(acesso)==null||method.invoke(acesso).toString().equals("")){
								pCell.addElement(new Phrase(" ", fConteudo));		
							}	
										
							String valor = null;
							
							if (method.invoke(acesso) instanceof String || method.invoke(acesso) instanceof Integer || method.invoke(acesso) instanceof Date) {
																								
								valor = method.invoke(acesso).toString();																	
								pCell.addElement(new Phrase(valor, fConteudo));									
							
							} else if (method.invoke(acesso) instanceof Cliente) {
								Cliente cliente = (Cliente) method.invoke(acesso);
								
								if(c.toString().equals("cliente.nome_razao")){
									pCell.addElement(new Phrase(cliente.getNome_razao(),fCampo));
								}else if(c.toString().equals("cliente.email")){
									pCell.addElement(new Phrase(cliente.getEmail(),fCampo));
								}
								
							} else if (method.invoke(acesso) instanceof PlanoAcesso) {
								PlanoAcesso plano = (PlanoAcesso) method.invoke(acesso);
								pCell.addElement(new Phrase(plano.getNome(), fConteudo));
							} else if (method.invoke(acesso) instanceof Concentrador) {
								Concentrador base = (Concentrador) method.invoke(acesso);
								pCell.addElement(new Phrase(base.getIdentificacao(), fConteudo));
							} else if (method.invoke(acesso) instanceof Swith) {
								Swith swith = (Swith) method.invoke(acesso);
								pCell.addElement(new Phrase(swith.getIdentificacao(), fConteudo));
							} else if (method.invoke(acesso) instanceof Produto) {
								Produto produto = (Produto) method.invoke(acesso);
								pCell.addElement(new Phrase(produto.getNome(), fConteudo));
							} else if (method.invoke(acesso) instanceof ContratosAcesso) {
								ContratosAcesso contrato = (ContratosAcesso) method.invoke(acesso);
								pCell.addElement(new Phrase(contrato.getNome(), fConteudo));
							}else if (method.invoke(acesso) instanceof Endereco) {
								Endereco end = (Endereco) method.invoke(acesso);
																
								if(end.getComplemento()==null||end.getComplemento().equals(" ")||end.getComplemento().equals("\t")){
									end.setComplemento(" ");
								}
								if(end.getReferencia()==null||end.getReferencia().equals(" ")||end.getComplemento().equals("\t")){
									end.setReferencia(" ");
								}
								
								String valorColuna = "";
								
								if(c.toString().equals("endereco.cep")){
									valorColuna = end.getCep();
								}else if(c.toString().equals("endereco.uf")){
									valorColuna = end.getUf();
								}else if(c.toString().equals("endereco.endereco")){
									valorColuna = end.getEndereco();
								}else if(c.toString().equals("endereco.bairro")){
									valorColuna = end.getBairro();
								}else if(c.toString().equals("endereco.cidade")){
									valorColuna = end.getCidade();
								}else if(c.toString().equals("endereco.complemento")){	
									valorColuna = end.getComplemento();
								}else if(c.toString().equals("endereco.numero")){
									valorColuna = end.getNumero();
								}else if(c.toString().equals("endereco.pais")){
									valorColuna = end.getPais();
								}else if(c.toString().equals("endereco.referencia")){
									valorColuna = end.getReferencia();						
								}							
								pCell.addElement(new Phrase(valorColuna, fConteudo));
							}
	
							
							tb1.addCell(pCell);
													
							
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show("ERRO!");
						}
					}
	
					doc.add(tb1);
					
				}
			}
		
			Paragraph pQtdRegistro = new Paragraph(String.valueOf(q.getResultList().size())+" Registros Encontrados",fCampo);
			pQtdRegistro.setAlignment(Element.ALIGN_LEFT);
			doc.add(pQtdRegistro);
					
			Paragraph pResumo = new Paragraph("RESUMO:",fCab);
			pResumo.setAlignment(Element.ALIGN_LEFT);
			PdfPCell pCellRe = new PdfPCell();
			pCellRe.setBorderWidth(0);
			pCellRe.addElement(pResumo);		
			
			Paragraph pResumoVl = new Paragraph(changeHeaderColumn(selectCampo(resumo)),fSubTitulo);
			pResumo.setAlignment(Element.ALIGN_LEFT);
			PdfPCell pCellReVl = new PdfPCell();
			pCellReVl.setBorderWidth(0);
			pCellReVl.addElement(pResumoVl);
			
			PdfPTable tbResu = new PdfPTable(new float[]{0.25f,1f});
			tbResu.setWidthPercentage(100f);	
			tbResu.addCell(pCellRe);
			tbResu.addCell(pCellReVl);
			tbResu.setSpacingBefore(20);
			tbResu.setSpacingAfter(10);
			
			doc.add(tbResu);
			
			
			CriteriaQuery<AcessoCliente> criteriaQueryGroup = cb.createQuery(AcessoCliente.class);
			Root<AcessoCliente> rootGroup = criteriaQueryGroup.from(AcessoCliente.class);
			

			
			
			if (selectCampo(resumo).equals("cliente.nome_razao")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("nome_razao");
				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("nome_razao"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			if (selectCampo(resumo).equals("cliente.email")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("email");
				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("email"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			
			if (selectCampo(resumo).equals("cliente.telefone1")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("telefone1");
				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("telefone1"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			
			if (selectCampo(resumo).equals("cliente.telefone2")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("telefone2");
				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("telefone2"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			
			if (selectCampo(resumo).equals("cliente.celular1")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("celular1");
				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("celular1"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			
			if (selectCampo(resumo).equals("cliente.celular2")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("celular2");
				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("celular2"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}

			if (selectCampo(resumo).equals("plano.nome")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("plano").get("nome");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("plano").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			
			if (selectCampo(resumo).equals("base.identificacao")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("base").get("identificacao");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("base").get("identificacao"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			
			if (selectCampo(resumo).equals("swith.identificacao")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("swith").get("identificacao");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("swith").get("identificacao"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			
			if (selectCampo(resumo).equals("material.nome")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("material").get("nome");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("material").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			
			if (selectCampo(resumo).equals("contrato.nome")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("contrato").get("nome");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("contrato").get("nome"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			
			//enderecos
			if (selectCampo(resumo).equals("endereco.cep")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco").get("cep");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("cep"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}						
			if (selectCampo(resumo).equals("endereco.numero")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco").get("numero");
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("numero"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			if (selectCampo(resumo).equals("endereco.endereco")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco").get("endereco");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("endereco"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
			}
			if (selectCampo(resumo).equals("endereco.bairro")) {

				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco").get("bairro");
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("bairro"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("endereco.cidade")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco").get("cidade");
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("cidade"));			
				
				
				if (criteria.size() == 0) {
					throw new RuntimeException("no criteria");
				} else if (criteria.size() == 1) {					
					criteriaQueryGroup.where(cb.and(criteria.get(0)));
				} else {					
					criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
				}
			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("endereco.uf")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco").get("uf");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("uf"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("endereco.pais")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco").get("pais");		
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("pais"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("endereco.complemento")) {
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco").get("complemento");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("complemento"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
				
			}
			if (selectCampo(resumo).equals("endereco.referencia")) {
								
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("endereco").get("referencia");	
				
				criteriaQueryGroup.groupBy(rootGroup.join("endereco").get("referencia"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
				
			}
			
			
			

			if (!selectCampo(resumo).equals("cliente.nome_razao") && !selectCampo(resumo).equals("cliente.email") && !selectCampo(resumo).equals("cliente.telefone1") && !selectCampo(resumo).equals("cliente.telefone2") && !selectCampo(resumo).equals("cliente.celular1") && !selectCampo(resumo).equals("cliente.celular2") && !selectCampo(resumo).equals("plano.nome") && 
					!selectCampo(resumo).equals("base.identificacao") && !selectCampo(resumo).equals("swith.identificacao") &&
					!selectCampo(resumo).equals("material.nome") && !selectCampo(resumo).equals("contrato.nome")&&
					!selectCampo(resumo).equals("endereco.cep") && !selectCampo(resumo).equals("endereco.numero") &&
					!selectCampo(resumo).equals("endereco.endereco") && !selectCampo(resumo).equals("endereco.bairro") &&
					!selectCampo(resumo).equals("endereco.uf") && !selectCampo(resumo).equals("endereco.cidade") && 
					!selectCampo(resumo).equals("endereco.pais") && !selectCampo(resumo).equals("endereco.complemento") &&
					!selectCampo(resumo).equals("endereco.referencia")) {
				
				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get(selectCampo(resumo));				
				criteriaQueryGroup.groupBy(rootGroup.get(selectCampo(resumo)));			
				
				if (criteria.size() == 0) {
					throw new RuntimeException("no criteria");
				} else if (criteria.size() == 1) {					
					criteriaQueryGroup.where(criteria.get(0));
				} else {					
					criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
				}
				
				
				//criteriaQueryGroup.where(cb.not(cb.equal(rootGroup.join("endereco").get("cidade"), "BELO JARDIM")));
				criteriaQueryGroup.select(cb.construct(AcessoCliente.class,coluna, qtd));
				
			}

			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);			
			
			
			for (AcessoCliente c :(List<AcessoCliente>) qGroup.getResultList()) {
				
				
				Paragraph pResum = new Paragraph();
				
				if(selectCampo(resumo).equals("cliente.nome_razao") || selectCampo(resumo).equals("cliente.email") || 
						selectCampo(resumo).equals("cliente.telefone1") || selectCampo(resumo).equals("cliente.telefone2") ||
						selectCampo(resumo).equals("cliente.celular1") || selectCampo(resumo).equals("cliente.celular2") ||
						selectCampo(resumo).equals("plano.nome") || 
						selectCampo(resumo).equals("base.identificacao") || selectCampo(resumo).equals("swith.identificacao") ||
						selectCampo(resumo).equals("material.nome") || selectCampo(resumo).equals("contrato.nome")||
						selectCampo(resumo).equals("endereco.cep") || selectCampo(resumo).equals("endereco.numero") ||
						selectCampo(resumo).equals("endereco.endereco") || selectCampo(resumo).equals("endereco.bairro") ||
						selectCampo(resumo).equals("endereco.uf") || selectCampo(resumo).equals("endereco.cidade") || 
						selectCampo(resumo).equals("endereco.pais") || selectCampo(resumo).equals("endereco.complemento") ||
						selectCampo(resumo).equals("endereco.referencia")){
				
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);				
					pResum.setAlignment(Element.ALIGN_LEFT);
					
				}else{

					if(rootGroup.get(selectCampo(resumo)).getJavaType().equals(String.class)){
						pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					}
					
					if(rootGroup.get(selectCampo(resumo)).getJavaType().equals(Date.class)){
						SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy");
						pResum = new Paragraph(smf.format(c.getColuna_date()), fCaptionsBold);					
					}
					pResum.setAlignment(Element.ALIGN_LEFT);
				}
				
				PdfPCell pCellResumo = new PdfPCell();
				pCellResumo.setPaddingTop(0);
				pCellResumo.setPaddingBottom(4f);
				pCellResumo.addElement(pResum);
				pCellResumo.setBackgroundColor(new BaseColor(232, 235, 237));
				pCellResumo.setBorderColor(new BaseColor(255, 255, 255));	
				pCellResumo.setBorderWidth(1.5f);
				
				Paragraph pResumolVl = new Paragraph(c.getQtd().toString(),fCaptions);
				pResumolVl.setAlignment(Element.ALIGN_RIGHT);
				
				PdfPCell pCellResumoVl = new PdfPCell();
				pCellResumoVl.addElement(pResumolVl);
				pCellResumoVl.setBackgroundColor(new BaseColor(232, 235, 237));
				pCellResumoVl.setBorderColor(new BaseColor(255, 255, 255));	
				pCellResumoVl.setBorderWidth(1.5f);
				
				Paragraph pResumoVazio = new Paragraph("");
				
				PdfPCell pCellResumoVazio = new PdfPCell();
				pCellResumoVazio.addElement(pResumoVazio);
				pCellResumoVazio.setBorderColor(new BaseColor(255, 255, 255));	
				pCellResumoVazio.setBorderWidth(1.5f);
				
				PdfPTable tbResumo = new PdfPTable(new float[]{0.55f,0.08f,1f});
				tbResumo.setWidthPercentage(100f);	
				tbResumo.addCell(pCellResumo);
				tbResumo.addCell(pCellResumoVl);
				tbResumo.addCell(pCellResumoVazio);
				
				doc.add(tbResumo);			
			}
			
			Paragraph ptotal = new Paragraph("TOTAL:",fCab);
			ptotal.setAlignment(Element.ALIGN_LEFT);
			PdfPCell pCellTotal = new PdfPCell();
			pCellTotal.setBorderWidth(0);	
			pCellTotal.addElement(ptotal);
			
			Paragraph pTotalVl = new Paragraph(""+q.getResultList().size(),fSubTitulo);
			pTotalVl.setAlignment(Element.ALIGN_RIGHT);
			PdfPCell pCellTotalVl = new PdfPCell();
			pCellTotalVl.setBorderWidth(0);
			pCellTotalVl.addElement(pTotalVl);
			
			Paragraph pTotalVazio = new Paragraph("");
			
			PdfPCell pCellTotalVazio = new PdfPCell();
			pCellTotalVazio.addElement(pTotalVazio);
			pCellTotalVazio.setBorderWidth(0);
			
			PdfPTable tbTotal = new PdfPTable(new float[]{0.55f,0.08f,1f});
			tbTotal.setWidthPercentage(100f);	
			tbTotal.addCell(pCellTotal);
			tbTotal.addCell(pCellTotalVl);
			tbTotal.addCell(pCellTotalVazio);
			tbTotal.setSpacingBefore(10);
			
			doc.add(tbTotal);
			
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

	private String changeHeaderColumn(String s) {

		String filtro = "";
		if(s.equals("id")){
			filtro = "COD.";			
		}else if(s.equals("cliente.nome_razao")){
			filtro = "CLIENTE";			
		}else if(s.equals("cliente.email")){
			filtro = "EMAIL";			
		}else if(s.equals("cliente.telefone1")){
			filtro = "TELEFONE1";			
		}else if(s.equals("cliente.telefone2")){
			filtro = "TELEFONE2";			
		}else if(s.equals("cliente.celular1")){
			filtro = "CELULAR1";			
		}else if(s.equals("cliente.celular2")){
			filtro = "CELULAR2";			
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
		}else if(s.equals("data_alteracao_plano")){
			filtro = "ALT. PLANO";
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
		}else if(s.equals("Email")){
			filtro = "cliente.email";			
		}else if(s.equals("Telefone1")){
			filtro = "cliente.telefone1";			
		}else if(s.equals("Telefone2")){
			filtro = "cliente.telefone2";			
		}else if(s.equals("Celular1")){
			filtro = "cliente.celular1";			
		}else if(s.equals("Celular2")){
			filtro = "cliente.celular2";			
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
		}else if(s.equals("Data Alteração Plano")){
			filtro = "data_alteracao_plano";
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
