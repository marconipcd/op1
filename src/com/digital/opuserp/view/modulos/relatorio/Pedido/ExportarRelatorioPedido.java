package com.digital.opuserp.view.modulos.relatorio.Pedido;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.PedidoDAO;
import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.dao.TotaisPedidoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.domain.NaturezaOperacao;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.domain.TotaisPedido;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.DataUtil;
import com.digital.opuserp.util.Real;
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
import com.lowagie.text.pdf.PdfCell;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;

public class ExportarRelatorioPedido implements StreamSource {

	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ExportarRelatorioPedido(String tipo,String order,String orientacao,String resumo, List<SearchParameters> lista_parametros_busca, List<Object> columns)throws Exception {

		EntityManager em = ConnUtil.getEntity();		
		
		Document doc = orientacao.equals("RETRATO") ? new Document(PageSize.A4, 24, 24, 24, 24) : new Document(PageSize.A4.rotate(), 24, 24, 24, 24);

		try {

			// -----------BUSCA

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EcfPreVendaCabecalho> criteriaQuery = cb.createQuery(EcfPreVendaCabecalho.class);
			Root<EcfPreVendaCabecalho> rootCliente = criteriaQuery.from(EcfPreVendaCabecalho.class);
			EntityType<EcfPreVendaCabecalho> type = em.getMetamodel().entity(EcfPreVendaCabecalho.class);
			List<Predicate> criteria = new ArrayList<Predicate>();
			List<EcfPreVendaDetalhe> resultDetalhe = null;

			criteria.add(cb.equal(rootCliente.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));
			criteria.add(cb.notLike(cb.lower(rootCliente.<String> get("tipo")), "CORRECAO".toLowerCase()));

			if (lista_parametros_busca.size() > 0) {
				for (SearchParameters s : lista_parametros_busca) {
					
					if (s.getOperador().equals("IGUAL")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.id")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("id")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("email")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("telefone1")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("telefone2")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("celular1")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("celular2")), s.getValor().toLowerCase()));
						}	
						
						//endereco_principals
						if (s.getCampo().equals("cliente.endereco_principal.endereco")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("endereco")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.bairro")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("bairro")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.cidade")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("cidade")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.uf")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("uf")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("formas_pgto")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("formaPagtoID").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("naturezaOperacao.descricao")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("naturezaOperacao").<String>get("descricao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.transportadora_padrao")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("transportadora_padrao")), s.getValor().toLowerCase()));
						}					
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.email") && 
								!s.getCampo().equals("formas_pgto") && !s.getCampo().equals("cliente.transportadora_padrao") &&
								!s.getCampo().equals("cliente.endereco_principal.endereco") && !s.getCampo().equals("cliente.endereco_principal.bairro") &&
								!s.getCampo().equals("cliente.endereco_principal.uf") && !s.getCampo().equals("cliente.endereco_principal.cidade") && 
								!s.getCampo().equals("naturezaOperacao.descricao")) {
							
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.equal(rootCliente.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(String.class)){
								if(s.getCampo().equals("situacao")){
									criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().subSequence(0, 1).toString().toUpperCase()));
								}else{
									criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().toLowerCase()));
								}
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.equal(rootCliente.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
								criteria.add(cb.equal(rootCliente.<Float>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
						}

					}else if (s.getOperador().equals("DIFERENTE")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.id")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("id")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("email")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("telefone1")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("telefone2")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("celular1")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("celular2")), s.getValor().toLowerCase()));
						}	
						
						//endereco_principals
						if (s.getCampo().equals("cliente.endereco_principal.endereco")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("endereco")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.bairro")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("bairro")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.cidade")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("cidade")), s.getValor().toLowerCase()));
						}
						if (s.getCampo().equals("cliente.endereco_principal.uf")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("uf")), s.getValor().toLowerCase()));
						}
						
						if (s.getCampo().equals("formas_pgto")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("formaPagtoID").<String>get("nome")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("naturezaOperacao.descricao")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("naturezaOperacao").<String>get("descricao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("cliente.transportadora_padrao")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("transportadora_padrao")), s.getValor().toLowerCase()));
						}					
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.email") && 
								!s.getCampo().equals("formas_pgto") && !s.getCampo().equals("cliente.transportadora_padrao") &&
								!s.getCampo().equals("cliente.endereco_principal.endereco") && !s.getCampo().equals("cliente.endereco_principal.bairro") &&
								!s.getCampo().equals("cliente.endereco_principal.uf") && !s.getCampo().equals("cliente.endereco_principal.cidade") && 
								!s.getCampo().equals("naturezaOperacao.descricao")) {								
							
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								criteria.add(cb.notEqual(rootCliente.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(String.class)){
								if(s.getCampo().equals("situacao")){
									criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())), s.getValor().substring(0,1).toString().toUpperCase()));
								}else{
									criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())), s.getValor().toLowerCase()));
								}
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.notEqual(rootCliente.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
								criteria.add(cb.notEqual(rootCliente.<Float>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
						}

					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.id")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("id")),"%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("email")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("telefone1")),"%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("telefone2")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("celular1")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("celular2")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						
						//endereco_principals
						if (s.getCampo().equals("cliente.endereco_principal.endereco")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.bairro")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.cidade")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.uf")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("formas_pgto")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("formaPagtoID").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}		
						if (s.getCampo().equals("naturezaOperacao.descricao")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("naturezaOperacao").<String>get("descricao")), "%" + s.getValor().toLowerCase()+ "%"));
						}			
						if (s.getCampo().equals("cliente.transportadora_padrao")) {
							criteria.add(cb.like(cb.lower(rootCliente.get("cliente").<String>get("transportadora_padrao")), "%" + s.getValor().toLowerCase()+ "%"));
						}			
												
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.email") && 
								!s.getCampo().equals("formas_pgto") && !s.getCampo().equals("cliente.transportadora_padrao") &&
								!s.getCampo().equals("cliente.endereco_principal.endereco") && !s.getCampo().equals("cliente.endereco_principal.bairro") &&
								!s.getCampo().equals("cliente.endereco_principal.uf") && !s.getCampo().equals("cliente.endereco_principal.cidade") && 
								!s.getCampo().equals("naturezaOperacao.descricao")) {
							
							if(s.getCampo().equals("situacao")){
								criteria.add(cb.like(cb.lower(rootCliente.<String>get(s.getCampo())), s.getValor().subSequence(0, 1).toString().toUpperCase()));
							}else{
								criteria.add(cb.like(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
							}
						}										
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						

						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("nome_razao")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.id")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("id")),"%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.email")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("email")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.telefone1")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("telefone1")),"%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.telefone2")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("telefone2")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						if (s.getCampo().equals("cliente.celular1")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("celular1")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("cliente.celular2")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("celular2")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						
						//endereco_principals
						if (s.getCampo().equals("cliente.endereco_principal.endereco")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("endereco")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.bairro")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("bairro")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.cidade")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("cidade")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("cliente.endereco_principal.uf")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("endereco_principal").<String>get("uf")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						
						if (s.getCampo().equals("formas_pgto")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("formaPagtoID").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("naturezaOperacao.descricao")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("naturezaOperacao").<String>get("descricao")), "%" + s.getValor().toLowerCase()+ "%"));
						}			
						if (s.getCampo().equals("cliente.transportadora_padrao")) {
							criteria.add(cb.notLike(cb.lower(rootCliente.get("cliente").<String>get("transportadora_padrao")), "%" + s.getValor().toLowerCase()+ "%"));
						}			
												
						if (!s.getCampo().equals("cliente.nome_razao") && !s.getCampo().equals("cliente.email") && 
								!s.getCampo().equals("formas_pgto") && !s.getCampo().equals("cliente.transportadora_padrao") &&
								!s.getCampo().equals("cliente.endereco_principal.endereco") && !s.getCampo().equals("cliente.endereco_principal.bairro") &&
								!s.getCampo().equals("cliente.endereco_principal.uf") && !s.getCampo().equals("cliente.endereco_principal.cidade") && 
								!s.getCampo().equals("naturezaOperacao.descricao")) {
							
							if(s.getCampo().equals("situacao")){						
								criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().subSequence(0, 1).toString().toUpperCase() + "%"));
							}else{
								criteria.add(cb.notLike(cb.lower(rootCliente.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
							}
						}
					} else if (s.getOperador().equals("MAIOR QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThan(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.greaterThan(rootCliente.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){								
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.greaterThan(rootCliente.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
								
							}catch(Exception e)
							{
								e.printStackTrace();		
							}
					} else if (s.getOperador().equals("MENOR QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.lessThan(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.lessThan(rootCliente.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.lessThan(rootCliente.<Date> get(s.getCampo()),  sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							
						
					} else if (s.getOperador().equals("MAIOR IGUAL QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.greaterThanOrEqualTo(rootCliente.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
		
					} else if (s.getOperador().equals("MENOR IGUAL QUE")) {

							try{						
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.lessThanOrEqualTo(rootCliente.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Float.class)){
									criteria.add(cb.lessThanOrEqualTo(rootCliente.<Float> get(s.getCampo()), Float.valueOf(s.getValor())));
								}
								
								if(rootCliente.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									criteria.add(cb.lessThanOrEqualTo(rootCliente.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
						}
				}//for parametros busca
			}

			if (criteria.size() == 0) {
				throw new RuntimeException("no criteria");
			} else if (criteria.size() == 1) {
				criteriaQuery.where(criteria.get(0));
			} else {
				criteriaQuery.where(cb.and(criteria.toArray(new Predicate[0])));
			}

			if(selectFiltro(order).equals("cliente.nome_razao")){				
				criteriaQuery.orderBy(cb.asc(rootCliente.join("cliente").get("nome_razao")));				
			}else if (selectFiltro(order).equals("cliente.id")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cliente").get("id")));
			}else if (selectFiltro(order).equals("cliente.email")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cliente").get("email")));			
			}else if (selectFiltro(order).equals("cliente.telefone1")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cliente").get("telefone1")));			
			}else if (selectFiltro(order).equals("cliente.telefone2")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cliente").get("telefone2")));			
			}else if (selectFiltro(order).equals("cliente.celular1")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cliente").get("celular1")));			
			}else if (selectFiltro(order).equals("cliente.celular2")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cliente").get("celular2")));			
			}else if (selectFiltro(order).equals("cliente.endereco_principal.cep")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("cep")));
			}else if (selectFiltro(order).equals("cliente.endereco_principal.numero")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("endereco_principal").get("numero")));
				
			//Endereço
			}else if (selectFiltro(order).equals("cliente.endereco_principal.endereco")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cliente").get("endereco_principal").get("endereco")));
			}else if (selectFiltro(order).equals("cliente.endereco_principal.bairro")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cliente").get("endereco_principal").get("bairro")));
			}else if (selectFiltro(order).equals("cliente.endereco_principal.cidade")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cliente").get("endereco_principal").get("cidade")));
			}else if (selectFiltro(order).equals("cliente.endereco_principal.uf")) {				
				criteriaQuery.orderBy(cb.asc(rootCliente.get("cliente").get("endereco_principal").get("uf")));
	
			}
//			else if(selectFiltro(order).equals("formas_pgto")){				
//				criteriaQuery.orderBy(cb.asc(rootCliente.join("formaPagtoID").get("nome")));				
//			}
			else if(selectFiltro(order).equals("naturezaOperacao.descricao")){
				criteriaQuery.orderBy(cb.asc(rootCliente.join("naturezaOperacao").get("descricao")));				
			}else if(selectFiltro(order).equals("cliente.transportadora_padrao")){
				criteriaQuery.orderBy(cb.asc(rootCliente.join("cliente").get("transportadora_padrao")));				
			}else{
				criteriaQuery.orderBy(cb.asc(rootCliente.get(selectFiltro(order))));				
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
			String hora = " às "+ sdf.format(new Date());
			String data = dtUtil.getDataExtenso(new Date());

			StringBuilder SbCabecalho = new StringBuilder();
			SbCabecalho.append("OPUS ERP4"+"\n"+"EMPRESA:"+"\n"+"EMISSÃO:"+"\n"+"OPERADOR:");
			
			StringBuilder SbCabecalhoVl = new StringBuilder();
			SbCabecalhoVl.append("\n"+empresa.getNome_fantasia()+"\n"+data+hora+"\n"+OpusERP4UI.getUsuarioLogadoUI().getUsername());
			
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
			Paragraph pExport = new Paragraph("RELATÓRIO DE PEDIDO",fTitulo);
			pExport.setAlignment(Element.ALIGN_CENTER);
			pExport.setSpacingAfter(10);
			doc.add(pExport);
			
			//FiILTROS					
			StringBuilder SbTipo = new StringBuilder();
			StringBuilder SbOperad =  new StringBuilder();
			StringBuilder SbValor = new StringBuilder();
			
			if (lista_parametros_busca.size() > 0) {
				for (SearchParameters s : lista_parametros_busca) {	
	
					SbTipo.append(selectHeader(s.getCampo())+"\n");
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
			SbVl.append(tipo+"\n"+orientacao+"\n"+selectUpHeader(selectFiltro(order))+"\n");

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

			// Estilos de Fonts
			Font fValores = new Font(FontFamily.COURIER, 6, Font.NORMAL);

			List<EcfPreVendaCabecalho> pedidos = q.getResultList();	
			
			
			float[] f = new float[columns.size()+2];
			f[columns.size()] = (0.21f);
			f[columns.size()+1] = (0.1f);
			//f[columns.size()+2] = (0.1f);
//			f[columns.size()+3] = (0.1f);
//			f[columns.size()+4] = (0.1f);	
			
			
			
			Integer i=0;
			for (Object c : columns) {		
			
				
				if(selectHeader(c.toString()).equals("CLIENTE")){
					f[i] = (0.40f);		
				}	
			    if(selectHeader(c.toString()).equals("CEP.")){
			    	f[i] = (0.14f);	
			    }
			    if(selectHeader(c.toString()).equals("ENDEREÇO")){
			    	f[i] = (0.35f);	
			    }
			    if(selectHeader(c.toString()).equals("CIDADE")){
			    	f[i] = (0.20f);	
			    }
			    if(selectHeader(c.toString()).equals("BAIRRO")){
			    	f[i] = (0.17f);	
			    }
			    if(selectHeader(c.toString()).equals("COD.")){
			    	f[i] = (0.10f);	
			    }
			    if(selectHeader(c.toString()).equals("PAIS")){
			    	f[i] = (0.14f);	
			    }
			    if(selectHeader(c.toString()).equals("EMAIL")){
			    	f[i] = (0.40f);	
			    }
			    if(selectHeader(c.toString()).equals("FORM PGTO")){
			    	f[i] = (0.19f);	
			    }
			    if(selectHeader(c.toString()).equals("DATA")){
			    	f[i] = (0.11f);	
			    }
			    if(selectHeader(c.toString()).equals("HORA")){
			    	f[i] = (0.11f);	
			    }
			    if(selectHeader(c.toString()).equals("DESC.")){
			    	f[i] = (0.10f);	
			    }
			    if(selectHeader(c.toString()).equals("ACRESC.")){
			    	f[i] = (0.10f);	
			    }
			    if(selectHeader(c.toString()).equals("SUBTOTAL")){
			    	f[i] = (0.10f);	
			    }
			    if(selectHeader(c.toString()).equals("TOTAL")){
			    	f[i] = (0.10f);	
			    }
			    if(selectHeader(c.toString()).equals("STATUS")){
			    	f[i] = (0.10f);	
			    }			   
			    if(selectHeader(c.toString()).equals("COMPRADOR")){
			    	f[i] = (0.14f);	
			    }
			    if(selectHeader(c.toString()).equals("ENTREGA")){
			    	f[i] = (0.11f);	
			    }
			    if(selectHeader(c.toString()).equals("VENDEDOR")){
			    	f[i] = (0.13f);	
			    }
			    if(selectHeader(c.toString()).equals("TIPO")){
			    	f[i] = (0.12f);	
			    }
			    if(selectHeader(c.toString()).equals("NAT.ITENS")){
			    	f[i] = (0.12f);	
			    }
			    if(selectHeader(c.toString()).equals("NAT.OPER.")){
			    	f[i] = (0.12f);	
			    }
			    if(selectHeader(c.toString()).equals("OBS")){
			    	f[i] = (0.20f);	
			    }
			    if(selectHeader(c.toString()).equals("UF")){
			    	f[i] = (0.10f);	
			    }
			    i++;
     		  }				
			
			double mercadoLivre = 0;
			double haver = 0;
			double valeRefeicao = 0;
			double dinheiro = 0;			
			double deposito = 0;
			double banco = 0;
			double cheque = 0;			
			double cartCredito = 0;			
			double cartDebito = 0;			
			double totalPago = 0;			
			double totalAserPago = 0;	
			double boleto = 0;
			double nenhuma = 0;
			int totalGeral = 0;
			
			Integer reg= 0;
			Integer cont = 0;
			
			PdfPTable tbConteudo2 = new PdfPTable(f);
			
			
			if(tipo.equals("DETALHAMENTO")){
				
				PdfPTable tbTopo = new PdfPTable(f);
				tbTopo.setWidthPercentage(100f);				
				
				PdfPCell pCellTop = new PdfPCell();
				for (Object c : columns) {
					
					pCellTop = new PdfPCell(new Phrase(selectHeader(c.toString()), fCampoBold));
					pCellTop.setPaddingTop(2);
					pCellTop.setPaddingBottom(4);
					pCellTop.setBackgroundColor(new BaseColor(114, 131, 151));
					pCellTop.setBorderColor(new BaseColor(255, 255, 255));	
					pCellTop.setBorderWidth(1.5f);
					tbTopo.addCell(pCellTop);
				}
				
				PdfPCell pdfCellTopo = new PdfPCell(new Phrase("FORMA DE PGTO", fCampoBold));
				pdfCellTopo.setBackgroundColor(new BaseColor(114, 131, 151));
				pdfCellTopo.setBorderColor(new BaseColor(255, 255, 255));
				pdfCellTopo.setBorderWidth(1.5f);
				pdfCellTopo.setPaddingTop(2);
				pdfCellTopo.setPaddingBottom(4);
				
				
				PdfPCell pdfCellValorForma = new PdfPCell(new Phrase("VALOR", fCampoBold));
				pdfCellValorForma.setBackgroundColor(new BaseColor(114, 131, 151));
				pdfCellValorForma.setBorderColor(new BaseColor(255, 255, 255));
				pdfCellValorForma.setBorderWidth(1.5f);
				pdfCellValorForma.setPaddingTop(2);
				pdfCellValorForma.setPaddingBottom(4);
				
//				PdfPCell pdfCellTopoAcresc = new PdfPCell(new Phrase("ACRESC. R$", fCampoBold));
//				pdfCellTopoAcresc.setBackgroundColor(new BaseColor(114, 131, 151));
//				pdfCellTopoAcresc.setBorderColor(new BaseColor(255, 255, 255));
//				pdfCellTopoAcresc.setBorderWidth(1.5f);
//				pdfCellTopoAcresc.setPaddingTop(2);
//				pdfCellTopoAcresc.setPaddingBottom(4);
//				
//				PdfPCell pdfCellTopoDesc = new PdfPCell(new Phrase("DESC. %", fCampoBold));
//				pdfCellTopoDesc.setBackgroundColor(new BaseColor(114, 131, 151));
//				pdfCellTopoDesc.setBorderColor(new BaseColor(255, 255, 255));
//				pdfCellTopoDesc.setBorderWidth(1.5f);
//				pdfCellTopoDesc.setPaddingTop(2);
//				pdfCellTopoDesc.setPaddingBottom(4);
				
//				PdfPCell pdfCellTotal = new PdfPCell(new Phrase("TOTAL", fCampoBold));
//				pdfCellTotal.setBackgroundColor(new BaseColor(114, 131, 151));
//				pdfCellTotal.setBorderColor(new BaseColor(255, 255, 255));
//				pdfCellTotal.setBorderWidth(1.5f);
//				pdfCellTotal.setPaddingTop(2);
//				pdfCellTotal.setPaddingBottom(4);
				
				
				tbTopo.addCell(pdfCellTopo);
				tbTopo.addCell(pdfCellValorForma);
//				tbTopo.addCell(pdfCellTopoAcresc);
//				tbTopo.addCell(pdfCellTopoDesc);		
				//tbTopo.addCell(pdfCellTotal);

				//Produto
				PdfPTable tbItens = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
				PdfPCell pCellProduto = new PdfPCell();			
				PdfPCell pCellQtdproduto = new PdfPCell();	
				PdfPCell pCellVlrUnit = new PdfPCell();						
				PdfPCell pCellValorToTal = new PdfPCell();
							
				
				doc.add(tbTopo);	
				Integer qtd = 0;
				Endereco endereco = null;
				for (EcfPreVendaCabecalho pedido : pedidos) {
					
					if(pedido.getCliente() != null && pedido.getCliente().getEndereco_principal()!=null){
						 endereco = em.find(Endereco.class, pedido.getCliente().getEndereco_principal().getId());
					}
					
					
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(5f);
					
								
										
					List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedido);
					for (TotaisPedido formasPgtoItem : totais) {
						
						String forma = formasPgtoItem.getForma_pgto() != null ? formasPgtoItem.getForma_pgto().getNome() : "HAVER"; 
						
						
						String tlPago =  Real.formatDbToString(String.valueOf(totalPago));				
													
						if(forma != null && pedido.getValor() != null && !pedido.getValor().equals(0.0) && !pedido.getValor().equals("")){
											
							totalPago = totalPago + formasPgtoItem.getValor();
							
							if(forma.equals("VALE REFEICAO")){
								valeRefeicao = valeRefeicao + formasPgtoItem.getValor();
								totalGeral  = totalGeral+1;
							}
							if(forma.equals("DINHEIRO")){
								dinheiro = dinheiro + formasPgtoItem.getValor();
								totalGeral  = totalGeral+1;
							}
							if(forma.equals("BANCO")){
								banco = banco + formasPgtoItem.getValor();
								totalGeral  = totalGeral+1;
							}
							if(forma.equals("CHEQUE")){
								cheque = cheque + formasPgtoItem.getValor();
								totalGeral  = totalGeral+1;
							}
							if(forma.equals("CARTAO CREDITO")){
								cartCredito = cartCredito + formasPgtoItem.getValor();
								totalGeral  = totalGeral+1;
							}
							if(forma.equals("CARTAO DEBITO")){
								cartDebito = cartDebito + formasPgtoItem.getValor();
								totalGeral  = totalGeral+1;
							}	
							if(forma.equals("DEPOSITO")){
								deposito = deposito + formasPgtoItem.getValor();
								totalGeral  = totalGeral+1;
							}				
							if(forma.equals("BOLETO")){
								boleto = boleto + formasPgtoItem.getValor();
								totalGeral  = totalGeral+1;
							}				
							if(forma.equals("NENHUMA")){
								nenhuma = nenhuma + formasPgtoItem.getValor();
								totalGeral  = totalGeral+1;
							}	
							if(forma.equals("HAVER")){
								haver = haver + formasPgtoItem.getValor();
								totalGeral  = totalGeral+1;
							}	
							if(forma.equals("MERCADO LIVRE")){
								mercadoLivre = mercadoLivre + formasPgtoItem.getValor();
								totalGeral = totalGeral+1;
							}	
									
								
						}
					}
					

					PdfPCell pCell3 = new PdfPCell();
					pCell3.setPaddingTop(0);
					pCell3.setPaddingBottom(4);
					pCell3.setBackgroundColor(new BaseColor(232, 235, 237));
					pCell3.setBorderColor(new BaseColor(255, 255, 255));	
					pCell3.setBorderWidth(1.5f);					
					
					Integer dias= 0;
					Paragraph valorColuna2 = null;
					valorColuna2 = new Paragraph(" ",fCampo);							
					pCell3.addElement(valorColuna2);	
										
					
//					PdfPTable tbConteudo = new PdfPTable(new float[]{0.14f,0.34f,0.24f,0.14f,0.34f,0.14f,0.14f,0.24f,0.14f,1f,0.24f});
					PdfPTable tbConteudo = new PdfPTable(f);
					tbConteudo.setWidthPercentage(100f);	
					if(qtd != 0){
						tbConteudo.setSpacingBefore(3);						
					}
					qtd++;
					
					
					for (Object c : columns) {
						
						PdfPCell pCellConteudo = new PdfPCell();
						pCellConteudo.setPaddingTop(2);
						pCellConteudo.setPaddingBottom(4);
						pCellConteudo.setBackgroundColor(new BaseColor(184, 191, 198));
						pCellConteudo.setBorderColor(new BaseColor(255, 255, 255));	
						pCellConteudo.setBorderWidth(1.5f);
						
						Paragraph valorColuna3 = null;
						valorColuna3 = new Paragraph(" ",fCampoBold);							
											
						if(selectHeader(c.toString()).equals("COD.")){
							valorColuna3 = new Paragraph(pedido.getId().toString(),fCampoBold);					
						}else if(selectHeader(c.toString()).equals("TIPO")){
							valorColuna3 = new Paragraph(pedido.getTipo(),fCampoBold);
						}else if(selectHeader(c.toString()).equals("NAT.ITENS")){
							valorColuna3 = new Paragraph(pedido.getTipoVenda(),fCampoBold);
						}else if(selectHeader(c.toString()).equals("NAT.OPER.")){
			
						if(pedido.getNaturezaOperacao()!=null && pedido.getNaturezaOperacao().getDescricao()!=null){
							valorColuna3 = new Paragraph(pedido.getNaturezaOperacao().getDescricao(),fCampoBold);
						}else{
							valorColuna3 = new Paragraph("",fCampoBold);
						}
							
						}else if(selectHeader(c.toString()).equals("CLIENTE")){
							String nomeCliente = pedido.getCliente() != null ? pedido.getCliente().getNome_razao() : "";
							valorColuna3 = new Paragraph(nomeCliente,fCampoBold);
						}else if(selectHeader(c.toString()).equals("EMAIL")){
							String email = pedido.getCliente() != null ? pedido.getCliente().getEmail() : "";
							valorColuna3 = new Paragraph(email,fCampoBold);	
						}else if(selectHeader(c.toString()).equals("ENDEREÇO") && endereco!=null){						
							valorColuna3 = new Paragraph(endereco.getEndereco(),fCampoBold);
						}else if(selectHeader(c.toString()).equals("CIDADE")&& endereco!=null){
							valorColuna3 = new Paragraph(endereco.getCidade(),fCampoBold);
						}else if(selectHeader(c.toString()).equals("BAIRRO")&& endereco !=null){
							valorColuna3 = new Paragraph(endereco.getBairro(),fCampoBold);
						}else if(selectHeader(c.toString()).equals("UF")&& endereco !=null){
							valorColuna3 = new Paragraph(endereco.getUf(),fCampoBold);
							
							
						}else if(selectHeader(c.toString()).equals("DATA")){
							valorColuna3 = new Paragraph(DataUtil.formatDateBra(pedido.getData()),fCampoBold);
						}else if(selectHeader(c.toString()).equals("HORA")){
							valorColuna3 = new Paragraph(DataUtil.formatHoraBra(pedido.getHora_pv()),fCampoBold);
						}
//						else if(selectHeader(c.toString()).equals("FORM PGTO")&&pedido.getFormaPagtoID()!=null){
//							valorColuna3 = new Paragraph(pedido.getFormaPagtoID().getNome(),fCampoBold);
//						}
						
						else if(selectHeader(c.toString()).equals("DESC.")){
							valorColuna3 = new Paragraph(Real.formatDbToString(pedido.getTotal_desc().toString()),fCampoBold);
							valorColuna3.setAlignment(Element.ALIGN_RIGHT);
							
						}else if(selectHeader(c.toString()).equals("ACRESC.")){
							if(pedido.getTotal_acres()!=null){
								valorColuna3 = new Paragraph(Real.formatDbToString(pedido.getTotal_acres().toString()),fCampoBold);								
							}else{
								valorColuna3 = new Paragraph("",fCampoBold);																
							}
							valorColuna3.setAlignment(Element.ALIGN_RIGHT);
							
						}else if(selectHeader(c.toString()).equals("SUBTOTAL")){
							valorColuna3 = new Paragraph(Real.formatDbToString(pedido.getSubTotal().toString()),fCampoBold);
							valorColuna3.setAlignment(Element.ALIGN_RIGHT);
						}else if(selectHeader(c.toString()).equals("TOTAL")){
							valorColuna3 = new Paragraph(Real.formatDbToString(pedido.getValor().toString()),fCampoBold);
							valorColuna3.setAlignment(Element.ALIGN_RIGHT);
						}else if(selectHeader(c.toString()).equals("VENDEDOR")){
							valorColuna3 = new Paragraph(pedido.getVendedor(),fCampoBold);
						}else if(selectHeader(c.toString()).equals("ENTREGA")){
							valorColuna3 = new Paragraph(pedido.getEntregar(),fCampoBold);
						}else if(selectHeader(c.toString()).equals("COMPRADOR")){
							valorColuna3 = new Paragraph(pedido.getComprador(),fCampoBold);
						}else if(selectHeader(c.toString()).equals("STATUS")){
							valorColuna3 = new Paragraph(pedido.getSituacao(),fCampoBold);
						}
							
						pCellConteudo.addElement(valorColuna3);	
						tbConteudo.addCell(pCellConteudo);
					}
					PdfPCell pdfCellVazioTopo = new PdfPCell(new Paragraph(" ",fCampoBold));
					pdfCellVazioTopo.setBackgroundColor(new BaseColor(184, 191, 198));
					pdfCellVazioTopo.setBorderColor(new BaseColor(255, 255, 255));
					pdfCellVazioTopo.setPaddingBottom(4);
					pdfCellVazioTopo.setBorderWidth(1.5f);
					
					Paragraph pTotalPedido = new Paragraph(pedido.getValor() != null ? Real.formatDbToString(String.valueOf(pedido.getValor())) : "",fCampoBold);
					//pTotalPedido.setAlignment(Element.ALIGN_RIGHT);
					
					PdfPCell pdfCellConteudoTotal = new PdfPCell(pTotalPedido);
					pdfCellConteudoTotal.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
					pdfCellConteudoTotal.setBackgroundColor(new BaseColor(184, 191, 198));
					pdfCellConteudoTotal.setBorderColor(new BaseColor(255, 255, 255));
					pdfCellConteudoTotal.setPaddingBottom(4);
					pdfCellConteudoTotal.setBorderWidth(1.5f);
					
					
					PdfPCell pdfCellConteudoAcres = new PdfPCell(new Paragraph(pedido.getTotal_acres() != null ? Real.formatDbToString(String.valueOf(pedido.getTotal_acres())) : "",fCampoBold));
					pdfCellConteudoAcres.setBackgroundColor(new BaseColor(184, 191, 198));
					pdfCellConteudoAcres.setBorderColor(new BaseColor(255, 255, 255));
					pdfCellConteudoAcres.setPaddingBottom(4);
					pdfCellConteudoAcres.setBorderWidth(1.5f);
					
					PdfPCell pdfCellConteudoDesc = new PdfPCell(new Paragraph(pedido.getTotal_desc() != null ? Real.formatDbToString(String.valueOf(pedido.getTotal_desc())) : "",fCampoBold));
					pdfCellConteudoDesc.setBackgroundColor(new BaseColor(184, 191, 198));
					pdfCellConteudoDesc.setBorderColor(new BaseColor(255, 255, 255));
					pdfCellConteudoDesc.setPaddingBottom(4);
					pdfCellConteudoDesc.setBorderWidth(1.5f);
					
					tbConteudo.addCell(pdfCellVazioTopo);
					tbConteudo.addCell(pdfCellVazioTopo);
//					tbConteudo.addCell(pdfCellConteudoAcres);
//					tbConteudo.addCell(pdfCellConteudoDesc);
					//tbConteudo.addCell(pdfCellConteudoTotal);	
					
					doc.add(tbConteudo);
					
					
					List<TotaisPedido> totaisforma = TotaisPedidoDAO.getTotais(pedido);
					
					for (TotaisPedido totaisPedido : totaisforma) {
						
						PdfPTable tbFormaPgtoDetalhamento = new PdfPTable(f);
						tbFormaPgtoDetalhamento.setWidthPercentage(100f);	
						for (Object c : columns) {
							PdfPCell pdfCellVazio = new PdfPCell(new Paragraph(" ",fCampoBold));
							pdfCellVazio.setBorderColor(new BaseColor(255, 255, 255));	
							tbFormaPgtoDetalhamento.addCell(pdfCellVazio);
						}
						
						String forma = totaisPedido != null && totaisPedido.getForma_pgto() != null ? totaisPedido.getForma_pgto().getNome() : "HAVER";
						PdfPCell pdfCecllNomeFormaPgto = new PdfPCell(new Paragraph(forma,fCampoBold));
						pdfCecllNomeFormaPgto.setBackgroundColor(new BaseColor(184, 191, 198));
						pdfCecllNomeFormaPgto.setBorderColor(new BaseColor(255, 255, 255));	
						pdfCecllNomeFormaPgto.setPaddingBottom(4);
						pdfCecllNomeFormaPgto.setBorderWidth(1.5f);
						
						tbFormaPgtoDetalhamento.addCell(pdfCecllNomeFormaPgto);
						
						
						Paragraph pValorForma = new Paragraph(Real.formatDbToString(String.valueOf(totaisPedido.getValor())),fCampoBold);						
						PdfPCell pdfCecllSubTotal = new PdfPCell(pValorForma);
						pdfCecllSubTotal.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
						pdfCecllSubTotal.setBackgroundColor(new BaseColor(184, 191, 198));
						pdfCecllSubTotal.setBorderColor(new BaseColor(255, 255, 255));	
						pdfCecllSubTotal.setPaddingBottom(4);
						pdfCecllSubTotal.setBorderWidth(1.5f);
						
						tbFormaPgtoDetalhamento.addCell(pdfCecllSubTotal);					
						
						
						PdfPCell pdfCellVazio = new PdfPCell(new Paragraph(" ",fCampoBold));
						pdfCellVazio.setBorderColor(new BaseColor(255, 255, 255));	
						//tbFormaPgtoDetalhamento.addCell(pdfCellVazio);
//						tbFormaPgtoDetalhamento.addCell(pdfCellVazio);
//						tbFormaPgtoDetalhamento.addCell(pdfCellVazio);
						
						
						doc.add(tbFormaPgtoDetalhamento);
					}
					
					
					

					
	
					resultDetalhe = PedidoDAO.getItensPedido(pedido.getId());
					
					PdfPTable tbItensTotal = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
					tbItensTotal.setWidthPercentage(99f);
					//PdfPTable tbItens = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
					//tbItensTotal.setWidthPercentage(100f);
					
					double totalItens = 0;
					if(resultDetalhe!=null){
						for(EcfPreVendaDetalhe detalhe: resultDetalhe){
								
								EcfPreVendaCabecalho pedido1 = PedidoDAO.find(detalhe.getEcfPreVendaCabecalhoId());
								totalItens = pedido1.getSubTotal() != null ? pedido1.getSubTotal() : 0;
								
								if(pedido.getTipoVenda().equals("PRODUTO")){
								
									Produto produto = new Produto();
									if(detalhe.getProdutoId() != null){
										produto = ProdutoDAO.find(detalhe.getProdutoId());	
									}

									tbItens = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
									tbItens.setWidthPercentage(99f);
									if(produto!=null){
										tbItens.addCell(buildPdfCelItem(produto.getId().toString()+" - "+produto.getNome(), true,false));										
									}
									
									if(produto != null && produto.getId()!=null){
										
										if(produto.getFracionar() == null || produto.getFracionar() == 0){				
												
											Paragraph p  = new Paragraph(String.valueOf((int)(Math.round(detalhe.getQuantidade()))), fCampo);
											p.setAlignment(Element.ALIGN_CENTER);
											
											PdfPCell pCellItem = new PdfPCell(p);									
											pCellItem.setRunDirection(PdfWriter.RUN_DIRECTION_NO_BIDI);
											pCellItem.setPaddingTop(2);
											pCellItem.setPaddingBottom(4);
											pCellItem.setBackgroundColor(new BaseColor(232, 235, 237));
											pCellItem.setBorderColor(new BaseColor(255, 255, 255));
											pCellItem.setBorderWidth(0.4f);
											
											//tbItens.addCell(pCellItem);
											tbItens.addCell(buildPdfCelItem(String.valueOf((int)(Math.round(detalhe.getQuantidade()))), true,true));
										}else{
											tbItens.addCell(buildPdfCelItem(String.valueOf((int)(Math.round(detalhe.getQuantidade()))), true,true));
										}
									}
									tbItens.addCell(buildPdfCelItem(Real.formatDbToString(String.valueOf(detalhe.getValorUnitario())), false,false));
									tbItens.addCell(buildPdfCelItem(Real.formatDbToString(String.valueOf(detalhe.getValorTotal())), false,false));								
									
									tbItens.setSpacingBefore(0);
									tbItens.setSpacingAfter(0);
									
									doc.add(tbItens);                  
								}else if(pedido.getTipoVenda().equals("SERVICO")){
									Servico servico = new Servico();
									if(detalhe.getProdutoId() != null){
										servico = ServicoDAO.find(detalhe.getProdutoId());	
									}

									tbItens = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
									tbItens.setWidthPercentage(99f);
									if(servico!=null){
										tbItens.addCell(buildPdfCelItem(servico.getNome(), true,false));										
									}
									tbItens.addCell(buildPdfCelItem(Real.formatDbToString(String.valueOf(detalhe.getQuantidade())), false,false));
									tbItens.addCell(buildPdfCelItem(Real.formatDbToString(String.valueOf(detalhe.getValorUnitario())), false,false));
									tbItens.addCell(buildPdfCelItem(Real.formatDbToString(String.valueOf(detalhe.getValorTotal())), false,false));
									tbItens.setSpacingBefore(0);
									tbItens.setSpacingAfter(0);
									
									doc.add(tbItens);
								}else{
									tbItens = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
									tbItens.setWidthPercentage(99f);
									tbItens.addCell(buildPdfCelItem("NENHUM ITEM DEFINIDO", true,false));
									tbItens.addCell(buildPdfCelItem("--", false,false));
									tbItens.addCell(buildPdfCelItem("--", false,false));
									tbItens.addCell(buildPdfCelItem("--", false,false));
									tbItens.setSpacingBefore(0);
									tbItens.setSpacingAfter(0);
									
									doc.add(tbItens);
								}
							}
						
							PdfPCell pCellItemVazio = new PdfPCell();					
							pCellItemVazio.setPaddingTop(2);
							pCellItemVazio.setPaddingBottom(4);
							pCellItemVazio.setBackgroundColor(new BaseColor(255, 255, 255));
							pCellItemVazio.setBorderColor(new BaseColor(255, 255, 255));
							pCellItemVazio.setBorderWidth(1.5f);
						
							tbItensTotal.addCell(pCellItemVazio);
							tbItensTotal.addCell(pCellItemVazio);
							tbItensTotal.addCell(pCellItemVazio);
							
							Paragraph PtotaisItens = new Paragraph(Real.formatDbToString(String.valueOf(totalItens)),fCampoBold);
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
			
			
			
			if(tipo.equals("MULTI COLUNA")){
				
				PdfPTable tbTopo = new PdfPTable(f);
				tbTopo.setWidthPercentage(100f);				
				tbTopo.setSpacingBefore(5);				
				
				PdfPCell pCellTop = new PdfPCell();
				
				PdfPTable tb20 = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
				PdfPCell pCellProduto = new PdfPCell();			
				PdfPCell pCellQtdproduto = new PdfPCell();	
				PdfPCell pCellValorUnitario = new PdfPCell();
				pCellValorUnitario.setBorderWidth(1.5f);			
				PdfPCell pCellValorToTal = new PdfPCell();

				for (Object c : columns) {
//					cont++;
					
					pCellTop = new PdfPCell(new Phrase(selectHeader(c.toString()), fCampoBold));
					pCellTop.setPaddingTop(2);
					pCellTop.setPaddingBottom(4);
					pCellTop.setBackgroundColor(new BaseColor(114, 131, 151));
					pCellTop.setBorderColor(new BaseColor(255, 255, 255));	
					pCellTop.setBorderWidth(1.5f);
					tbTopo.addCell(pCellTop);
				}
				
//				doc.add(tbTopo);				
				
				
				for (EcfPreVendaCabecalho pedido : pedidos) {


					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(5f);
					
					PdfPTable tbConteudo = new PdfPTable(f);
					tbConteudo.setWidthPercentage(100f);				

					
					Query qEcfPreVenDetalhe = em.createQuery("select ecfdeta from EcfPreVendaDetalhe ecfdeta where ecfdeta.ecfPreVendaCabecalhoId = :cod", EcfPreVendaDetalhe.class);
					qEcfPreVenDetalhe.setParameter("cod", pedido.getId());	 
					if(qEcfPreVenDetalhe.getResultList().size() > 0){
						resultDetalhe = qEcfPreVenDetalhe.getResultList();
					}
					
						List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedido);
						
						for (TotaisPedido totaisPedido : totais) {
							
							FormasPgto formaPgto = totaisPedido.getForma_pgto();							
							String tlPago =  Real.formatDbToString(String.valueOf(totalPago));				
							
							if(formaPgto != null && pedido.getValor() != null && !pedido.getValor().equals(0.0)){
								
								if(pedido.getValor() != null && !pedido.getValor().equals("")){
									
									totalPago = totalPago + pedido.getValor();
									
									if(pedido.getValor()!=null && !pedido.getValor().equals("")){
										if(formaPgto.getNome().equals("VALE REFEICAO")){
											valeRefeicao = valeRefeicao + pedido.getValor();
											totalGeral  = totalGeral+1;
										}
										if(formaPgto.getNome().equals("DINHEIRO")){
											dinheiro = dinheiro + pedido.getValor();
											totalGeral  = totalGeral+1;
										}
										if(formaPgto.getNome().equals("BANCO")){
											banco = banco + pedido.getValor();
											totalGeral  = totalGeral+1;
										}
										if(formaPgto.getNome().equals("CHEQUE")){
											cheque = cheque + pedido.getValor();
											totalGeral  = totalGeral+1;
										}
										if(formaPgto.getNome().equals("CARTAO CREDITO")){
											cartCredito = cartCredito + pedido.getValor();
											totalGeral  = totalGeral+1;
										}
										if(formaPgto.getNome().equals("CARTAO DEBITO")){
											cartDebito = cartDebito + pedido.getValor();
											totalGeral  = totalGeral+1;
										}	
										if(formaPgto.getNome().equals("DEPOSITO")){
											deposito = deposito + pedido.getValor();
											totalGeral  = totalGeral+1;
										}				
										if(formaPgto.getNome().equals("BOLETO")){
											boleto = boleto + pedido.getValor();
											totalGeral  = totalGeral+1;
										}				
										if(formaPgto.getNome().equals("NENHUMA")){
											nenhuma = nenhuma + pedido.getValor();
											totalGeral  = totalGeral+1;
										}	
										if(formaPgto.getNome().equals("MERCADO LIVRE")){
											mercadoLivre = mercadoLivre + pedido.getValor();
											totalGeral  = totalGeral+1;
										}	
									}
								}
							}
							
						}					

					PdfPCell pCell3 = new PdfPCell();
					pCell3.setPaddingTop(0);
					pCell3.setPaddingBottom(4);
					pCell3.setBackgroundColor(new BaseColor(232, 235, 237));
					pCell3.setBorderColor(new BaseColor(255, 255, 255));	
					pCell3.setBorderWidth(1.5f);
					
					
						Integer dias= 0;
						Paragraph valorColuna2 = null;
							
						valorColuna2 = new Paragraph(" ",fCampo);																					

							
						pCell3.addElement(valorColuna2);
					
					
					
					for (Object c : columns) {
						
						try {
							PdfPCell pCell = new PdfPCell();
							pCell.setPaddingTop(0);
							pCell.setPaddingBottom(4);
							pCell.setBackgroundColor(new BaseColor(184, 191, 198));
							pCell.setBorderColor(new BaseColor(255, 255, 255));	
							pCell.setBorderWidth(1.5f);
	
							String metodo = "get"
									+ c.toString().substring(0, 1).toUpperCase()
									+ c.toString().substring(1,c.toString().length());
							
							metodo = metodo.replace(".nome_razao", "");
							metodo = metodo.replace(".id", "");
							metodo = metodo.replace(".email", "");
							metodo = metodo.replace(".telefone1", "");
							metodo = metodo.replace(".telefone2", "");
							metodo = metodo.replace(".celular1", "");
							metodo = metodo.replace(".celular2", "");
							metodo = metodo.replace(".transportadora_padrao", "");
							metodo = metodo.replace(".nome", "");
							metodo = metodo.replace(".descricao", "");							
							metodo = metodo.replace(".bairro", "");
							metodo = metodo.replace(".cidade", "");
							metodo = metodo.replace(".pais", "");
							metodo = metodo.replace(".complemento", "");
							metodo = metodo.replace(".referencia", "");
							metodo = metodo.replace(".endereco", "");
							metodo = metodo.replace(".numero", "");
							metodo = metodo.replace(".uf", "");
							metodo = metodo.replace(".cep", "");
							
							

							Class cls = pedido.getClass();
							
							Method method = cls.getMethod(metodo);
							
							String valor = null;
							
								if (method.invoke(pedido) instanceof String || method.invoke(pedido) instanceof Integer || method.invoke(pedido) instanceof Date) {
									
									if(c.toString().equals("cliente.telefone1")){
										String metodo2 = "getDdd_fone1";
										Method method2 = cls.getMethod(metodo2);
										if(method2.invoke(pedido)!=null && !method2.invoke(pedido).toString().equals("")&& !method.invoke(pedido).toString().equals("")){
											valor = method2.invoke(pedido).toString()+" "+method.invoke(pedido).toString();									
										}else{
											valor = method.invoke(pedido).toString();										
										}
									}else if(c.toString().equals("cliente.telefone2")){
										String metodo2 = "getDdd_fone2";
										Method method2 = cls.getMethod(metodo2);
										if(method2.invoke(pedido)!=null && !method2.invoke(pedido).toString().equals("")&& !method.invoke(pedido).toString().equals("")){
											valor = method2.invoke(pedido).toString()+" "+method.invoke(pedido).toString();									
										}else{
											valor = method.invoke(pedido).toString();										
										}	
									}else if(c.toString().equals("cliente.celular1")){
										String metodo2 = "getDdd_cel1";
										Method method2 = cls.getMethod(metodo2);
										if(method2.invoke(pedido)!=null && !method2.invoke(pedido).toString().equals("")&& !method.invoke(pedido).toString().equals("")){
											valor = method2.invoke(pedido).toString()+" "+method.invoke(pedido).toString();									
										}else{
											valor = method.invoke(pedido).toString();										
										}	
									}else if(c.toString().equals("cliente.celular2")){
										String metodo2 = "getDdd_cel2";
										Method method2 = cls.getMethod(metodo2);
										if(method2.invoke(pedido)!=null && !method2.invoke(pedido).toString().equals("") && !method.invoke(pedido).toString().equals("")){
											valor = method2.invoke(pedido).toString()+" "+method.invoke(pedido).toString();									
										}else{
											valor = method.invoke(pedido).toString();										
										}	
									}else{
										valor = method.invoke(pedido).toString();										
									}
									
									Paragraph valorColuna = null;
									
									 if(c.toString().equals("data")){
										
										String dateform = dtUtil.parseDataBra(valor);
										valorColuna = new Paragraph(dateform,fCampo);									
									}else{
										valorColuna = new Paragraph(valor,fCampo);									
									}
									 
									pCell.addElement(valorColuna);		
						
									
								}else if (method.invoke(pedido) instanceof Cliente) {
									
									Cliente cat = (Cliente) method.invoke(pedido);
									
									String valorColuna = "";
									
									if(c.toString().equals("cliente.nome_razao")){
										valorColuna = cat.getNome_razao();
									}	
									if(c.toString().equals("cliente.id")){
										valorColuna = cat.getId().toString();
									}
									if(c.toString().equals("cliente.email")){
										valorColuna = cat.getEmail();
									}	
									if(c.toString().equals("cliente.transportadora_padrao")){
										valorColuna = cat.getTransportadora_padrao();
									}
									if(c.toString().equals("cliente.endereco_principal.bairro")){
										valorColuna = cat.getEndereco_principal().getBairro();
									}
									pCell.addElement(new Phrase(valorColuna, fCampo));			
																
										
								}else if (method.invoke(pedido) instanceof FormasPgto) {
									
									FormasPgto cat = (FormasPgto) method.invoke(pedido);
									
									String valorColuna = "";
									
									if(c.toString().equals("formas_pgto")){
										valorColuna = cat.getNome();
									}
									
									pCell.addElement(new Phrase(valorColuna, fCampo));	
									
								}else if (method.invoke(pedido) instanceof NaturezaOperacao) {
									
									NaturezaOperacao cat = (NaturezaOperacao) method.invoke(pedido);
									
									String valorColuna = "";
									
									if(c.toString().equals("naturezaOperacao.descricao")){
										valorColuna = cat.getDescricao();
									}
									
									pCell.addElement(new Phrase(valorColuna, fCampo));		

								}else if (method.invoke(pedido) instanceof Endereco) {
									Endereco end = (Endereco) method.invoke(pedido);
									
									String valorColuna = "";
									
									if(c.toString().equals("cliente.endereco_principal.cep")){
										valorColuna = end.getCep();
									}else if(c.toString().equals("cliente.endereco_principal.uf")){
										valorColuna = end.getUf();
									}else if(c.toString().equals("cliente.endereco_principal.endereco")){
										valorColuna = end.getEndereco();
									}else if(c.toString().equals("cliente.endereco_principal.bairro")){
										valorColuna = end.getBairro();
									}else if(c.toString().equals("cliente.endereco_principal.cidade")){
										valorColuna = end.getCidade();
									}else if(c.toString().equals("cliente.endereco_principal.complemento")){
										valorColuna = end.getComplemento();
									}else if(c.toString().equals("cliente.endereco_principal.numero")){
										valorColuna = end.getNumero();
									}else if(c.toString().equals("cliente.endereco_principal.pais")){
										valorColuna = end.getPais();
									}else if(c.toString().equals("cliente.endereco_principal.referencia")){
										valorColuna = end.getReferencia();
									}				
								}
//							tbTopo.addCell(pCellTop);
							tbConteudo.addCell(pCell);	

							
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show("ERRO!");
						}
					}
					tbConteudo.addCell(pCell3);
					doc.add(tbTopo);	
					doc.add(tbConteudo);	

					
					
					
					Query qEcfPreVenDetalhe2 = em.createQuery("select ecfdeta from EcfPreVendaDetalhe ecfdeta where ecfdeta.ecfPreVendaCabecalhoId = :cod", EcfPreVendaDetalhe.class);
					qEcfPreVenDetalhe2.setParameter("cod", pedido.getId());	 
					if(qEcfPreVenDetalhe2.getResultList().size() > 0){
						resultDetalhe = qEcfPreVenDetalhe2.getResultList();
					}
	
					if(resultDetalhe!=null){
						for(EcfPreVendaDetalhe detalhe: resultDetalhe){
								
//							if(!detalhe.getCancelado().equals("S")){
								
								Produto produto = new Produto();
								if(detalhe.getProdutoId() != null){
									produto = em.find(Produto.class, detalhe.getProdutoId());	
								}
								
								Paragraph pProduto = new Paragraph(produto.getNome(), fCampo);
								
								Paragraph pQtdProduto = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getQuantidade())),fCampo);
								pQtdProduto.setAlignment(Element.ALIGN_RIGHT);
								Paragraph pValorUnitario = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getValorUnitario())),fCampo);
								pValorUnitario.setAlignment(Element.ALIGN_RIGHT);
								Paragraph pValorTotal = new Paragraph(Real.formatDbToString(String.valueOf(detalhe.getValorTotal())),fCampo);
								pValorTotal.setAlignment(Element.ALIGN_RIGHT);
								
								pCellProduto = new PdfPCell();
								pCellProduto.addElement(pProduto);
								pCellProduto.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellProduto.setBorderColor(new BaseColor(255, 255, 255));
								pCellProduto.setBorderWidth(1.5f);
								
								pCellQtdproduto = new PdfPCell();
								pCellQtdproduto.addElement(pQtdProduto);
								pCellQtdproduto.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellQtdproduto.setBorderColor(new BaseColor(255, 255, 255));
								pCellQtdproduto.setBorderWidth(1.5f);
								
								pCellValorUnitario = new PdfPCell();
								pCellValorUnitario.addElement(pValorUnitario);
								pCellValorUnitario.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellValorUnitario.setBorderColor(new BaseColor(255, 255, 255));
								pCellValorUnitario.setBorderWidth(1.5f);
								
								pCellValorToTal = new PdfPCell();
								pCellValorToTal.addElement(pValorTotal);
								pCellValorToTal.setBackgroundColor(new BaseColor(232, 235, 237));
								pCellValorToTal.setBorderColor(new BaseColor(255, 255, 255));
								pCellValorToTal.setBorderWidth(1.5f);
								
								tb20 = new PdfPTable(new float[] {1f, 0.20f, 0.20f, 0.30f});
								tb20.setWidthPercentage(99f);
								tb20.setSpacingBefore(1);
								tb20.addCell(pCellProduto);
								tb20.addCell(pCellQtdproduto);
								tb20.addCell(pCellValorUnitario);
								tb20.addCell(pCellValorToTal);
								doc.add(tb20);
					
							}
						}
//					}
					
					
					
					
				}

			}

			
			
			if(tipo.equals("COLUNA ÚNICA")){
				for (EcfPreVendaCabecalho pedido : pedidos) {
	
					PdfPTable tb1 = new PdfPTable(1);
					tb1.setWidthPercentage(100f);
					tb1.setSpacingAfter(25f);

					for (Object c : columns) {
						try {
							PdfPCell pCell = new PdfPCell();
							pCell.setPaddingBottom(5);
							pCell.setPaddingTop(0);
							pCell.addElement(new Phrase(selectHeader(c.toString()), fCampo));
							
							String metodo = "get"
									+ c.toString().substring(0, 1).toUpperCase()
									+ c.toString().substring(1,c.toString().length());
							
							metodo = metodo.replace(".nome_razao", "");
							metodo = metodo.replace(".id", "");
							metodo = metodo.replace(".email", "");
							metodo = metodo.replace(".telefone1", "");
							metodo = metodo.replace(".telefone2", "");
							metodo = metodo.replace(".celular1", "");
							metodo = metodo.replace(".celular2", "");
							metodo = metodo.replace(".transportadora_padrao", "");
							metodo = metodo.replace(".nome", "");
							metodo = metodo.replace(".descricao", "");							
							metodo = metodo.replace(".bairro", "");
							metodo = metodo.replace(".cidade", "");
							metodo = metodo.replace(".pais", "");
							metodo = metodo.replace(".complemento", "");
							metodo = metodo.replace(".referencia", "");
							metodo = metodo.replace(".endereco", "");
							metodo = metodo.replace(".numero", "");
							metodo = metodo.replace(".uf", "");
							metodo = metodo.replace(".cep", "");
							
							Class cls = pedido.getClass();
							Method method = cls.getMethod(metodo);
	
							if(method.invoke(pedido)==null||method.invoke(pedido).toString().equals("")){
								pCell.addElement(new Phrase(" ", fConteudo));		
							}	
														
							if (method.invoke(pedido) instanceof String || method.invoke(pedido) instanceof Integer || method.invoke(pedido) instanceof Date) {
								String valor = method.invoke(pedido).toString();
								
								if(c.toString().equals("cliente.telefone1")){
									String metodo2 = "getDdd_fone1";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(pedido)!=null && !method2.invoke(pedido).toString().equals("")&& !method.invoke(pedido).toString().equals("")){
										valor = method2.invoke(pedido).toString()+" "+method.invoke(pedido).toString();									
									}else{
										valor = method.invoke(pedido).toString();										
									}
								}else if(c.toString().equals("cliente.telefone2")){
									String metodo2 = "getDdd_fone2";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(pedido)!=null && !method2.invoke(pedido).toString().equals("")&& !method.invoke(pedido).toString().equals("")){
										valor = method2.invoke(pedido).toString()+" "+method.invoke(pedido).toString();									
									}else{
										valor = method.invoke(pedido).toString();										
									}	
								}else if(c.toString().equals("cliente.celular1")){
									String metodo2 = "getDdd_cel1";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(pedido)!=null && !method2.invoke(pedido).toString().equals("")&& !method.invoke(pedido).toString().equals("")){
										valor = method2.invoke(pedido).toString()+" "+method.invoke(pedido).toString();									
									}else{
										valor = method.invoke(pedido).toString();										
									}	
								}else if(c.toString().equals("cliente.celular2")){
									String metodo2 = "getDdd_cel2";
									Method method2 = cls.getMethod(metodo2);
									if(method2.invoke(pedido)!=null && !method2.invoke(pedido).toString().equals("") && !method.invoke(pedido).toString().equals("")){
										valor = method2.invoke(pedido).toString()+" "+method.invoke(pedido).toString();									
									}else{
										valor = method.invoke(pedido).toString();										
									}	
								}else{
									valor = method.invoke(pedido).toString();										
								}
								
								
								Paragraph valorColuna = null;
								
								 if(c.toString().equals("data")){
									String dateform = dtUtil.parseDataBra(valor);
									valorColuna = new Paragraph(dateform,fConteudo);									
								}else if(c.toString().equals("situacao")){
									
									if(valor.equals("A")){
										valorColuna = new Paragraph("ABERTO",fConteudo);
									}
									
									if(valor.equals("F")){
										valorColuna = new Paragraph("FECHADO",fConteudo);
									}
									
									if(valor.equals("C")){
										valorColuna = new Paragraph("CANCELADO",fConteudo);
									}
								}else{
									valorColuna = new Paragraph(valor,fConteudo);									
								}
								
								pCell.addElement(valorColuna);								
							
							}else if (method.invoke(pedido) instanceof Cliente) {

								Cliente cat = (Cliente) method.invoke(pedido);
															
								Paragraph valorColuna = null;
								if(c.toString().equals("cliente.nome_razao")){
									valorColuna = new Paragraph(cat.getNome_razao(),fConteudo);		
								}
								
								pCell.addElement(valorColuna);		
							}else if (method.invoke(pedido) instanceof Cliente) {
								
								Cliente cat = (Cliente) method.invoke(pedido);
								
								String valorColuna = "";
								
								if(c.toString().equals("cliente.id")){
									valorColuna = cat.getId().toString();
								}	
							}else if (method.invoke(pedido) instanceof Cliente) {
								
								Cliente cat = (Cliente) method.invoke(pedido);
								
								String valorColuna = "";
								
								if(c.toString().equals("cliente.email")){
									valorColuna = cat.getEmail();
								}	
								
								pCell.addElement(new Phrase(valorColuna, fCampo));				
								
							}else if (method.invoke(pedido) instanceof Cliente) {
								
								Cliente cat = (Cliente) method.invoke(pedido);
								
								String valorColuna = "";
								
								if(c.toString().equals("cliente.transportadora_padrao")){
									valorColuna = cat.getTransportadora_padrao();
								}	
								if(c.toString().equals("cliente.endereco_principal.bairro")){
									valorColuna = cat.getEndereco_principal().getBairro();
								}	
								
								pCell.addElement(new Phrase(valorColuna, fCampo));								
									
							}else if (method.invoke(pedido) instanceof FormasPgto) {
								
								FormasPgto cat = (FormasPgto) method.invoke(pedido);
								
								String valorColuna = "";
								
								if(c.toString().equals("formas_pgto")){
									valorColuna = cat.getNome();
								}
								
								pCell.addElement(new Phrase(valorColuna, fCampo));	
								
							}else if (method.invoke(pedido) instanceof NaturezaOperacao) {
								
								NaturezaOperacao cat = (NaturezaOperacao) method.invoke(pedido);
								
								String valorColuna = "";
								
								if(c.toString().equals("naturezaOperacao.descricao")){
									valorColuna = cat.getDescricao();
								}
								
								pCell.addElement(new Phrase(valorColuna, fCampo));		
								
							}else if (method.invoke(pedido) instanceof NaturezaOperacao) {
								
								NaturezaOperacao cat = (NaturezaOperacao) method.invoke(pedido);
								
								String valorColuna = "";
								
								if(c.toString().equals("naturezaOperacao.descricao")){
									valorColuna = cat.getDescricao();
								}
								
								pCell.addElement(new Phrase(valorColuna, fCampo));			

								
							}else if (method.invoke(pedido) instanceof FormasPgto) {

								FormasPgto cat = (FormasPgto) method.invoke(pedido);
															
								Paragraph valorColuna = null;
								if(c.toString().equals("formas_pgto")){
									valorColuna = new Paragraph(cat.getNome(),fConteudo);		
								}
					
								pCell.addElement(valorColuna);	
								
							}else if (method.invoke(pedido) instanceof NaturezaOperacao) {
								
								NaturezaOperacao cat = (NaturezaOperacao) method.invoke(pedido);
								
								Paragraph valorColuna = null;								
								if(c.toString().equals("naturezaOperacao.descricao")){
									valorColuna = new Paragraph(cat.getDescricao(),fConteudo);
								}
								
								pCell.addElement(valorColuna);
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
			
			Paragraph pResumoVl = new Paragraph(selectUpHeader(selectFiltro(resumo)),fSubTitulo);
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
			
			
			CriteriaQuery<EcfPreVendaCabecalho> criteriaQueryGroup = cb.createQuery(EcfPreVendaCabecalho.class);
			Root<EcfPreVendaCabecalho> rootGroup = criteriaQueryGroup.from(EcfPreVendaCabecalho.class);
		
			if (selectFiltro(resumo).equals("cliente.nome_razao")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("nome_razao");		
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("nome_razao"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			}
			
			if (selectFiltro(resumo).equals("cliente.id")) {			
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("id");			
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("id"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			}

			if (selectFiltro(resumo).equals("cliente.email")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("email");				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("email"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			}
			if (selectFiltro(resumo).equals("cliente.telefone1")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("telefone1");				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("telefone1"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			}
			if (selectFiltro(resumo).equals("cliente.telefone2")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("telefone2");				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("telefone2"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			}
			if (selectFiltro(resumo).equals("cliente.celular1")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("celular1");				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("celular1"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			}
			if (selectFiltro(resumo).equals("cliente.celular2")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("celular2");				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("celular2"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			}
			
			//endereco_principals					
			if (selectFiltro(resumo).equals("cliente.endereco_principal.endereco")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("endereco_principal").get("endereco");				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("endereco_principal").get("endereco"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			}
			if (selectFiltro(resumo).equals("cliente.endereco_principal.bairro")) {
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("endereco_principal").get("bairro");			
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("endereco_principal").get("bairro"));		
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));			
			}
			if (selectFiltro(resumo).equals("cliente.endereco_principal.cidade")) {		
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("endereco_principal").get("cidade");			
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("endereco_principal").get("cidade"));			
				
				
				if (criteria.size() == 0) {
					throw new RuntimeException("no criteria");
				} else if (criteria.size() == 1) {					
					criteriaQueryGroup.where(cb.and(criteria.get(0)));
				} else {					
					criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
				}
			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
				
			}
			if (selectFiltro(resumo).equals("cliente.endereco_principal.uf")) {			
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("endereco_principal").get("uf");				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("endereco_principal").get("uf"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
				
			}
			if (selectFiltro(resumo).equals("naturezaOperacao.descricao")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("naturezaOperacao").get("descricao");				
				criteriaQueryGroup.groupBy(rootGroup.join("naturezaOperacao").get("descricao"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			}
			if (selectFiltro(resumo).equals("cliente.transportadora_padrao")) {				
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get("cliente").get("transportadora_padrao");				
				criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("transportadora_padrao"));			
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
			}
			
			if (!selectFiltro(resumo).equals("cliente.nome_razao") && !selectFiltro(resumo).equals("cliente.id") && !selectFiltro(resumo).equals("cliente.email") &&
					!selectFiltro(resumo).equals("cliente.telefone1") && !selectFiltro(resumo).equals("cliente.telefone2") && !selectFiltro(resumo).equals("cliente.transportadora_padrao") && 
					!selectFiltro(resumo).equals("cliente.celular1") &&!selectFiltro(resumo).equals("cliente.celular2") && !selectFiltro(resumo).equals("formas_pgto") &&
					!selectFiltro(resumo).equals("cliente.endereco_principal.cep") && !selectFiltro(resumo).equals("cliente.endereco_principal.numero") &&
					!selectFiltro(resumo).equals("cliente.endereco_principal.endereco") && !selectFiltro(resumo).equals("cliente.endereco_principal.bairro") &&
					!selectFiltro(resumo).equals("cliente.endereco_principal.uf") && !selectFiltro(resumo).equals("cliente.endereco_principal.cidade") && 
					!selectFiltro(resumo).equals("cliente.endereco_principal.pais") && !selectFiltro(resumo).equals("cliente.endereco_principal.complemento") &&
					!selectFiltro(resumo).equals("naturezaOperacao.descricao")) {			
				Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
				Selection<String> coluna = rootGroup.get(selectFiltro(resumo));				
				criteriaQueryGroup.groupBy(rootGroup.get(selectFiltro(resumo)));			
				
				if (criteria.size() == 0) {
					throw new RuntimeException("no criteria");
				} else if (criteria.size() == 1) {					
					criteriaQueryGroup.where(criteria.get(0));
				} else {					
					criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
				}
				
				
				//criteriaQueryGroup.where(cb.not(cb.equal(rootGroup.join("endereco_principal").get("cidade"), "BELO JARDIM")));
				criteriaQueryGroup.select(cb.construct(EcfPreVendaCabecalho.class,coluna, qtd));
				
			}
			
			
			if (!selectFiltro(resumo).equals("formas_pgto")) {	

			TypedQuery qGroup = em.createQuery(criteriaQueryGroup);	
	
			for (EcfPreVendaCabecalho c :(List<EcfPreVendaCabecalho>) qGroup.getResultList()) {

				Paragraph pResum = new Paragraph();
				
				if(selectFiltro(resumo).equals("cliente.nome_razao") || selectFiltro(resumo).equals("cliente.id") || selectFiltro(resumo).equals("cliente.email")|| selectFiltro(resumo).equals("cliente.transportadora_padrao")
						|| selectFiltro(resumo).equals("cliente.endereco_principal.cep") || selectFiltro(resumo).equals("cliente.endereco_principal.endereco") || selectFiltro(resumo).equals("cliente.endereco_principal.numero")
						|| selectFiltro(resumo).equals("cliente.endereco_principal.bairro") || selectFiltro(resumo).equals("cliente.endereco_principal.cidade") || selectFiltro(resumo).equals("cliente.endereco_principal.pais")
						|| selectFiltro(resumo).equals("cliente.endereco_principal.uf") || selectFiltro(resumo).equals("cliente.endereco_principal.referencia")||selectFiltro(resumo).equals("cliente.categoria.nome")
						|| selectFiltro(resumo).equals("naturezaOperacao.descricao")|| selectFiltro(resumo).equals("cliente.telefone2") || selectFiltro(resumo).equals("cliente.celular1")||selectFiltro(resumo).equals("cliente.celular2")){
					pResum = new Paragraph(c.getColuna(), fCaptionsBold);	
					
				}else{
				
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(String.class)){
						pResum = new Paragraph(c.getColuna(), fCaptionsBold);
					}
					
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Date.class)){
						SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy");
						if(c.getColuna_date()!=null){
							pResum = new Paragraph(smf.format(c.getColuna_date()), fCaptionsBold);												
						}
					}
				
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Integer.class)){
						pResum = new Paragraph(c.getColuna_Inter().toString(), fCaptionsBold);					
					}
					
					if(rootGroup.get(selectFiltro(resumo)).getJavaType().equals(Float.class)){
						pResum = new Paragraph(String.valueOf(c.getColuna_Float()), fCaptionsBold);					
					}
				}
				pResum.setAlignment(Element.ALIGN_LEFT);
				
				
				
				
				PdfPCell pCellResumo = new PdfPCell();
				pCellResumo.setPaddingTop(0);
				pCellResumo.setPaddingBottom(4f);
				pCellResumo.addElement(pResum);
				pCellResumo.setBackgroundColor(new BaseColor(232, 235, 237));
				pCellResumo.setBorderColor(new BaseColor(255, 255, 255));	
				pCellResumo.setBorderWidth(1.5f);
				
				String qtd = c.getQtd() != null ? c.getQtd().toString():"0";
				Paragraph pResumolVl = new Paragraph(qtd,fCaptions);
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
					
				Paragraph pResumoVazioPgt = null;
				
				if(c.getColuna()!=null){				
					if(c.getColuna().equals("DINHEIRO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(dinheiro)),fCaptionsBold);
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("BANCO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(banco)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("DEPOSITO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(deposito)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("CHEQUE")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(cheque)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("CARTAO CREDITO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(cartCredito)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("CARTAO DEBITO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(cartDebito)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("BOLETO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(boleto)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("NENHUMA")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(nenhuma)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("VALE REFEICAO")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(valeRefeicao)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}else if(c.getColuna().equals("MERCADO LIVRE")){
						pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(mercadoLivre)),fCaptionsBold);					
						pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
					}
				}
						
				PdfPCell pCellResumoVazioPgt = new PdfPCell();
				pCellResumoVazioPgt.addElement(pResumoVazioPgt);
				pCellResumoVazioPgt.setBackgroundColor(new BaseColor(232, 235, 237));
				pCellResumoVazioPgt.setBorderColor(new BaseColor(255, 255, 255));	
				pCellResumoVazioPgt.setBorderWidth(1.5f);			
								
				PdfPTable tbResumo = new PdfPTable(new float[]{0.55f,0.08f,1f});
				tbResumo.setWidthPercentage(100f);	
				tbResumo.addCell(pCellResumo);
				tbResumo.addCell(pCellResumoVl);
				tbResumo.addCell(pCellResumoVazio);
				
				PdfPTable tbResumoPgt = new PdfPTable(new float[]{0.55f,0.08f,0.10f,1f});
				tbResumoPgt.setWidthPercentage(100f);	
				tbResumoPgt.addCell(pCellResumo);
				tbResumoPgt.addCell(pCellResumoVl);
				tbResumoPgt.addCell(pCellResumoVazioPgt);
				tbResumoPgt.addCell(pCellResumoVazio);
		
				if(selectFiltro(resumo).equals("formas_pgto")){
					doc.add(tbResumoPgt);											
				}else{
					doc.add(tbResumo);																
				}
			}
			
			}else{
				
				
				List<String> formas = new ArrayList<>();
				Map<String, Integer> qtdForma = new HashMap<String, Integer>();
				Map<String, Double> valorForma = new HashMap<String, Double>();
				
				
				for (EcfPreVendaCabecalho pedido :(List<EcfPreVendaCabecalho>) q.getResultList()) {
					List<FormasPgto> formasPGto = pedido.getFormas_pgto();
					List<TotaisPedido> totais = TotaisPedidoDAO.getTotais(pedido);
					
					for (TotaisPedido total: totais) {
						
						if(total.getForma_pgto() != null){
						
							if(!formas.contains(total.getForma_pgto().getNome())){
								formas.add(total.getForma_pgto().getNome());
							}
							
							
							if(qtdForma.get(total.getForma_pgto().getNome()) != null){
								Integer qtd = qtdForma.get(total.getForma_pgto().getNome())+1;
								qtdForma.remove(total.getForma_pgto().getNome());
								qtdForma.put(total.getForma_pgto().getNome(), qtd);
							}else{
								qtdForma.put(total.getForma_pgto().getNome(), 1);
							}
						
						}
						
						if(total.getHaver() != null){
							
							if(!formas.contains("HAVER")){
								formas.add("HAVER");
							}
							
							
							if(qtdForma.get("HAVER") != null){
								Integer qtd = qtdForma.get("HAVER")+1;
								qtdForma.remove("HAVER");
								qtdForma.put("HAVER", qtd);
							}else{
								qtdForma.put("HAVER", 1);
							}
						
						}
						
					}
					
				}
				
				for (String forma : formas) {			
				
						String nomeFormaPgto = forma;
						Paragraph pResum = new Paragraph(nomeFormaPgto, fCaptionsBold);				
						pResum.setAlignment(Element.ALIGN_LEFT);				
						
						PdfPCell pCellResumo = new PdfPCell();
						pCellResumo.setPaddingTop(0);
						pCellResumo.setPaddingBottom(4f);
						pCellResumo.addElement(pResum);
						pCellResumo.setBackgroundColor(new BaseColor(232, 235, 237));
						pCellResumo.setBorderColor(new BaseColor(255, 255, 255));	
						pCellResumo.setBorderWidth(1.5f);
						
						String qtd = qtdForma.get(nomeFormaPgto).toString();
						Paragraph pResumolVl = new Paragraph(qtd,fCaptions);
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
							
						Paragraph pResumoVazioPgt = null;
						
						
						if(nomeFormaPgto!=null){				
							if(nomeFormaPgto.equals("DINHEIRO")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(dinheiro)),fCaptionsBold);
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else if(nomeFormaPgto.equals("BANCO")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(banco)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else if(nomeFormaPgto.equals("DEPOSITO")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(deposito)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else if(nomeFormaPgto.equals("CHEQUE")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(cheque)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else if(nomeFormaPgto.equals("CARTAO CREDITO")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(cartCredito)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else if(nomeFormaPgto.equals("CARTAO DEBITO")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(cartDebito)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else if(nomeFormaPgto.equals("BOLETO")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(boleto)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else if(nomeFormaPgto.equals("NENHUMA")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(nenhuma)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else if(nomeFormaPgto.equals("VALE REFEICAO")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(valeRefeicao)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else if(nomeFormaPgto.equals("HAVER")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(haver)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else if(nomeFormaPgto.equals("MERCADO LIVRE")){
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(mercadoLivre)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}else{
								pResumoVazioPgt = new Paragraph(Real.formatDbToString(String.valueOf(haver)),fCaptionsBold);					
								pResumoVazioPgt.setAlignment(Element.ALIGN_RIGHT);
							}
						}
								
						PdfPCell pCellResumoVazioPgt = new PdfPCell();
						pCellResumoVazioPgt.addElement(pResumoVazioPgt);
						pCellResumoVazioPgt.setBackgroundColor(new BaseColor(232, 235, 237));
						pCellResumoVazioPgt.setBorderColor(new BaseColor(255, 255, 255));	
						pCellResumoVazioPgt.setBorderWidth(1.5f);			
										
						PdfPTable tbResumo = new PdfPTable(new float[]{0.55f,0.08f,1f});
						tbResumo.setWidthPercentage(100f);	
						tbResumo.addCell(pCellResumo);
						tbResumo.addCell(pCellResumoVl);
						tbResumo.addCell(pCellResumoVazio);
						
						PdfPTable tbResumoPgt = new PdfPTable(new float[]{0.55f,0.08f,0.10f,1f});
						tbResumoPgt.setWidthPercentage(100f);	
						tbResumoPgt.addCell(pCellResumo);
						tbResumoPgt.addCell(pCellResumoVl);
						tbResumoPgt.addCell(pCellResumoVazioPgt);
						tbResumoPgt.addCell(pCellResumoVazio);
				
						
						doc.add(tbResumoPgt);											
				}
			}
			
			Paragraph ptotal = new Paragraph("TOTAL:",fCaptions);
			ptotal.setAlignment(Element.ALIGN_LEFT);
			PdfPCell pCellTotal = new PdfPCell();
			pCellTotal.setBorderWidth(0);	
			pCellTotal.addElement(ptotal);
			
			Paragraph pTotalVl = new Paragraph(""+String.valueOf(totalGeral),fCaptionsBold);
			pTotalVl.setAlignment(Element.ALIGN_RIGHT);
			
			PdfPCell pCellTotalVl = new PdfPCell();
			pCellTotalVl.setBorderWidth(0);
			pCellTotalVl.addElement(pTotalVl);
			
			Paragraph pTotalPgt = new Paragraph(Real.formatDbToString(String.valueOf(totalPago))	,fCaptionsBold);
			pTotalPgt.setAlignment(Element.ALIGN_RIGHT);
			
			PdfPCell pCellTotalPgt = new PdfPCell();
			pCellTotalPgt.setBorderWidth(0);
			pCellTotalPgt.addElement(pTotalPgt);
			
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
			
			PdfPTable tbTotalPgt = new PdfPTable(new float[]{0.55f,0.08f,0.10f,1f});
			tbTotalPgt.setWidthPercentage(100f);	
			tbTotalPgt.addCell(pCellTotal);
			tbTotalPgt.addCell(pCellTotalVl);
			tbTotalPgt.addCell(pCellTotalPgt);
			tbTotalPgt.addCell(pCellTotalVazio);
			tbTotalPgt.setSpacingBefore(10);
			
			if(selectFiltro(resumo).equals("formas_pgto")){
				doc.add(tbTotalPgt);								
			}else{
				doc.add(tbTotal);
			}
						

			
		} finally {
			if (doc.isOpen() && doc != null) {
				doc.close();
			}
		}

	}
	public String selectUpHeader(String s) {
	
		String filtro = "";
	
		if(s.equals("cliente.id")){
			filtro = "Cod.Cliente";							
		}else if(s.equals("cliente.nome_razao")){
			filtro = "Cliente";							
		}else if(s.equals("id")){
			filtro = "Código";					
		}else if(s.equals("formas_pgto")){
			filtro = "Forma Pagamento.";					
		}else if(s.equals("data")){
			filtro = "Data";		
		}else if(s.equals("hora_pv")){
			filtro = "Hora";	
		}else if(s.equals("situação")){
			filtro = "Status";
		}else if(s.equals("tipoVenda")){
			filtro = "Natureza Itens";
		}else if(s.equals("total_acres")){
			filtro = "Acrescimo";	
		}else if(s.equals("total_desc")){
			filtro = "Desconto";	
		}else if(s.equals("subTotal")){
			filtro = "SubTotal";	
		}else if(s.equals("valor")){
			filtro = "Valor Total";			
		}else if(s.equals("naturezaOperacao.descricao")){
			filtro = "Natureza Operação";
		}else if(s.equals("tipo")){
			filtro = "Tipo";
		}else if(s.equals("vendedor")){
			filtro = "Vendedor";
		}else if(s.equals("entregar")){
			filtro = "Entrega";
		}else if(s.equals("comprador")){
			filtro = "Comprador";
		}else if(s.equals("cliente.endereco_principal.cep")){
			filtro = "CEP";			
		}else if(s.equals("cliente.endereco_principal.endereco")){
			filtro = "Endereço";			
		}else if(s.equals("cliente.endereco_principal.numero")){
			filtro = "Número";			
		}else if(s.equals("cliente.endereco_principal.bairro")){
			filtro = "Bairro";			
		}else if(s.equals("cliente.endereco_principal.cidade")){
			filtro = "Cidade";			
		}else if(s.equals("cliente.endereco_principal.pais")){
			filtro = "Pais";			
		}else if(s.equals("cliente.endereco_principal.complemento")){
			filtro = "Complemento";			
		}else if(s.equals("cliente.endereco_principal.uf")){
			filtro = "UF";
		}else if(s.equals("cliente.email")){
			filtro = "Email";			
		}else if(s.equals("cliente.telefone1")){
			filtro = "Telefone Principal";			
		}else if(s.equals("cliente.telefone2")){
			filtro = "Telefone Alternativo 1";			
		}else if(s.equals("cliente.celular1")){
			filtro = "Telefone Alternativo 2";			
		}else if(s.equals("cliente.celular2")){
			filtro = "Telefone Alternativo 3";
		}else if(s.equals("obs")){
			filtro = "OBS";	
		}
		
		return filtro;
	}
	public String selectHeader(String s) {
		
		String filtro = "";
		if(s.equals("cliente.id")){
			filtro = "COD.CLIENT";							
		}else if(s.equals("cliente.nome_razao")){
			filtro = "CLIENTE";							
		}else if(s.equals("id")){
			filtro = "COD.";					
		}else if(s.equals("formas_pgto")){
			filtro = "FORM PGTO";					
		}else if(s.equals("data")){
			filtro = "DATA";	
		}else if(s.equals("hora_pv")){
			filtro = "HORA";	
		}else if(s.equals("situacao")){
			filtro = "STATUS";
		}else if(s.equals("tipoVenda")){
			filtro = "NAT.ITENS";
		}else if(s.equals("total_acres")){
			filtro = "ACRESC.";	
		}else if(s.equals("total_desc")){
			filtro = "DESC.";	
		}else if(s.equals("subTotal")){
			filtro = "SUBTOTAL";	
		}else if(s.equals("valor")){
			filtro = "TOTAL";		
		}else if(s.equals("naturezaOperacao.descricao")){
			filtro = "NAT.OPER.";
		}else if(s.equals("tipo")){
			filtro = "TIPO";
		}else if(s.equals("vendedor")){
			filtro = "VENDEDOR";
		}else if(s.equals("entregar")){
			filtro = "ENTREGA";
		}else if(s.equals("comprador")){
			filtro = "COMPRADOR";
		}else if(s.equals("cliente.endereco_principal.cep")){
			filtro = "CEP";			
		}else if(s.equals("cliente.endereco_principal.endereco")){
			filtro = "ENDEREÇO";			
		}else if(s.equals("cliente.endereco_principal.numero")){
			filtro = "Nº";			
		}else if(s.equals("cliente.endereco_principal.bairro")){
			filtro = "BAIRRO";			
		}else if(s.equals("cliente.endereco_principal.cidade")){
			filtro = "CIDADE";			
		}else if(s.equals("cliente.endereco_principal.pais")){
			filtro = "PAIS";			
		}else if(s.equals("cliente.endereco_principal.uf")){
			filtro = "UF";			
		}else if(s.equals("cliente.endereco_principal.referencia")){
			filtro = "REFERENCIA";
		}else if(s.equals("cliente.email")){
			filtro = "EMAIL";			
		}else if(s.equals("cliente.telefone1")){
			filtro = "TEL. PRICIPAL";			
		}else if(s.equals("cliente.telefone2")){
			filtro = "TEL. ALTER. 1";			
		}else if(s.equals("cliente.celular1")){
			filtro = "TEL. ALTER. 2";			
		}else if(s.equals("cliente.celular2")){
			filtro = "TEL. ALTER. 3";
		}else if(s.equals("obs")){
			filtro = "OBS";	
		}else if(s.equals("cliente.transportadora_padrao")){
			filtro = "TRASNP. PADRÃO";
		}
		
		return filtro;
	}
	public String selectFiltro(String s) {
		
		String filtro = "";		
		if(s.equals("Cliente")){
			filtro = "cliente.nome_razao";												
		}else if(s.equals("Código")){
			filtro = "id";					
		}else if(s.equals("Forma Pagamento")){
			filtro = "formas_pgto";					
		}else if(s.equals("Data")){
			filtro = "data";
		}else if(s.equals("Hora")){
			filtro = "hora_pv";	
		}else if(s.equals("Status")){
			filtro = "situacao";
		}else if(s.equals("Natureza Itens")){
			filtro = "tipoVenda";
		}else if(s.equals("Desconto")){
			filtro = "total_desc";	
		}else if(s.equals("SubTotal")){
			filtro = "subTotal";	
		}else if(s.equals("Valor Total")){
			filtro = "valor";		
		}else if(s.equals("Natureza Operação")){
			filtro = "naturezaOperacao.descricao";
		}else if(s.equals("Comprador")){
			filtro = "comprador";
		}else if(s.equals("Tipo")){
			filtro = "tipo";
		}else if(s.equals("Vendedor")){
			filtro = "vendedor";
		}else if(s.equals("Entrega")){
			filtro = "entregar";
		}else if(s.equals("Endereço")){
			filtro = "cliente.endereco_principal.endereco";						
		}else if(s.equals("Bairro")){
			filtro = "cliente.endereco_principal.bairro";			
		}else if(s.equals("Cidade")){
			filtro = "cliente.endereco_principal.cidade";					
		}else if(s.equals("UF")){
			filtro = "cliente.endereco_principal.uf";
		}else if(s.equals("Email")){
			filtro = "cliente.email";			
		}
				
		return filtro;
	}	
	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(baos.toByteArray());
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
		pCellItem.setBackgroundColor(new BaseColor(232, 235, 237));
		pCellItem.setBorderColor(new BaseColor(255, 255, 255));
		pCellItem.setBorderWidth(1.5f);
		
		
		return pCellItem;
	}
}

