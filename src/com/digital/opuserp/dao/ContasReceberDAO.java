package com.digital.opuserp.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.compiler.ast.CondExpr;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.jrimum.texgit.FlatFile;
import org.jrimum.texgit.Record;
import org.jrimum.texgit.Texgit;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AgendaRenovacoes;
import com.digital.opuserp.domain.AlteracoesContasReceber;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContaBancaria;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ControleTitulo;
import com.digital.opuserp.domain.CreditoCliente;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.HaverBoleto;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.RadReply;
import com.digital.opuserp.domain.RegistroLiquidado;
import com.digital.opuserp.domain.RemessaBanco;
import com.digital.opuserp.util.CheckNdocUtil;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.jcabi.immutable.ArrayMap;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ContasReceberDAO {
	
	
//	private void gerarCarne(Set<Object> boletos){
//		
//		try{
//			
//			Set<Object> selecteds = boletos;
//			if(selecteds.size() > 0){
//				
//				List<String> links= new ArrayList<>();
//				EntityManager em = ConnUtil.getEntity();
//				
//				
//				for (Object o: selecteds.toArray()) {
//										
//					String codCliente = tb.getItem(o).getItemProperty("Cod.").getValue().toString();
//					ContasReceber boleto = ContasReceberDAO.find(Integer.parseInt(codCliente));
//					
//					if(boleto.getTransacao_gerencianet() != null && !boleto.getTransacao_gerencianet().equals("")){
//						
//						if(boleto.getN_numero_gerencianet() != null && !boleto.getN_numero_gerencianet().equals("")){
//							links.add("URLhttp://172.17.0.13/boletoNovo/boletoUnico.php?url2="+boleto.getN_numero_gerencianet());
//						}else{
//							//Pegar a URL
//							String detalhes = detalharTransacao(boleto.getTransacao_gerencianet());
//							if(detalhes != null && !detalhes.equals("")){
//								em.getTransaction().begin();
//								boleto.setN_numero_gerencianet(detalhes);
//								em.merge(boleto);
//								em.getTransaction().commit();
//								
//								links.add("URLhttp://172.17.0.13/boletoNovo/boletoUnico.php?url2="+boleto.getN_numero_gerencianet());
//							}
//						}
//					}else{
//						
//							boolean atrasado = false;
//							double valor_corrigido = 0;
//							if(new DateTime(boleto.getData_vencimento()).isBeforeNow()){
//								atrasado = true;								
//								valor_corrigido = calcularAtraso(boleto.getId());								
//							}
//					
//							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");							
//							String descricao = "Boleto";           
//				            
//							String valor = boleto.getValor_titulo().replace(",", "").replace(".", "");	
//				            if(atrasado){
//				            	valor = Real.formatDbToString(String.valueOf(valor_corrigido)).replace(",", "").replace(".", "");
//				            }
//				            
//				            String nome_cliente =retirarAcentos(boleto.getCliente().getNome_razao());            
//				            String cpf = boleto.getCliente().getDoc_cpf_cnpj();
//				            String telefone = boleto.getCliente().getDdd_fone1()+""+boleto.getCliente().getTelefone1();
//				            String email = boleto.getCliente().getEmail() != null ? boleto.getCliente().getEmail() : "";            
//				            String vencimento = sdf.format(boleto.getData_vencimento());
//				            String codBoleto = boleto.getId().toString();
//				            
//				            String url = "http://172.17.0.13/boleto/api.php";
//		
//				            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
//		
//				            //add reuqest header
//				            httpClient.setRequestMethod("POST");
//				            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
//				            httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//				            
//				            String codContrato = "";
//				            if(ContasReceberDAO.ChecarPadraoAcessoNDoc(boleto.getN_doc())){
//				            	codContrato = boleto.getN_doc().split("/")[0].toString();	
//				            }
//		
//				            String urlParameters = "funcao=gerarBoletos&"
//				            		+ "descricao="+descricao+"&"
//				            		+ "valor="+valor+"&"
//				            		+ "quantidade=1&"
//				            		+ "nome_cliente="+nome_cliente+"&"
//				            		+ "cpf="+cpf+"&"
//				            		+ "telefone="+telefone+"&"
//				            		+ "vencimento="+vencimento+"&"
//				            		+ "codBoleto="+codBoleto+"&"
//				            		+ "email="+email+"&"
//				            		+ "codContrato="+codContrato;
//	        
//				            httpClient.setDoOutput(true);
//				            try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
//				                wr.writeBytes(urlParameters);
//				                wr.flush();
//				            }
//
//				            try (BufferedReader in = new BufferedReader(
//				                    new InputStreamReader(httpClient.getInputStream()))) {
//		
//				                String line;
//				                StringBuilder response = new StringBuilder();
//		
//				                while ((line = in.readLine()) != null) {
//				                    response.append(line);
//				                }
//		
//				                System.out.println(response.toString());
//				                
//				                org.json.JSONObject json = new org.json.JSONObject(response.toString());		                
//				    	        org.json.JSONObject jsonData = new org.json.JSONObject(json.get("data").toString());
//				    	         
//				    	        String link = jsonData.get("link").toString();
//				    	        String transacao = jsonData.get("charge_id").toString();
//				    	        
//				    	        System.out.println(link);
//				    	        System.out.println(transacao);
//		
//				    	        boleto.setN_numero_gerencianet(link); 
//				    	        boleto.setTransacao_gerencianet(transacao);
//				    	        
//				    	        em.getTransaction().begin();
//				    	        em.merge(boleto);
//				    	        em.getTransaction().commit();
//				    	        
//				    	        links.add("URLhttp://172.17.0.13/boletoNovo/boletoUnico.php?url2="+boleto.getN_numero_gerencianet());
//				    	        
//				            }
//		            
//		            
//					}
//  
//				}
//				
//				
//				if(links.size() > 0){
//					
//					String urls = "";
//					for (String s: links) {
//						urls += s;
//					}
//					
//					final String url = "http://172.17.0.13/boletoNovo/pegaCodigoHtml2.php?url="+urls;
//
//					Window wVideo  = new Window("Imprimir boletos");
//			        wVideo.setResizable(false); 
//			        wVideo.setModal(true);
//			        wVideo.center();
//			        wVideo.setWidth("942px");
//			        wVideo.setHeight("680px");
//			        
//			        wVideo.setContent(new VerticalLayout(){
//			        	{
//			        		setMargin(true);
//			        		setSpacing(true);
//			        		
//			        		setWidth("940px");
//					        setHeight("633px");
//			        		
//			        		String link = url;
//			        		
//			        		final Embedded video = new Embedded(null,new ExternalResource(link));
//					        video.setType(Embedded.TYPE_BROWSER);
//					        video.setSizeFull();
//
//			        		addComponent(video);
//			        		
//			        	}
//			        });
//			        
//			        OpusERP4UI.getCurrent().getUI().addWindow(wVideo);
//					
//					//refresh(tfBusca.getValue());
//					
//				}else{
//					Notify.Show("Não foi possível imprimir nenhum boleto!", Notify.TYPE_ERROR);
//				}
//				
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	
	public static void save(ContasReceber boleto){
		
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		em.merge(boleto);
		em.getTransaction().commit();
	}
	
	public static boolean retirarTransacaoBoleto(String transacao){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			Query q = em.createQuery("select c from ContasReceber c where c.transacao_gerencianet=:t", ContasReceber.class);
			
			q.setParameter("t", transacao);
			
			em.getTransaction().begin();
			for (ContasReceber boleto : (List<ContasReceber>)q.getResultList()) {
				boleto.setTransacao_gerencianet(null);
				boleto.setN_numero_gerencianet(null);
			}
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean retirarTransacaoBoletoSemTrasaction(String transacao){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			Query q = em.createQuery("select c from ContasReceber c where c.transacao_gerencianet=:t", ContasReceber.class);
			
			q.setParameter("t", transacao);
			
			//em.getTransaction().begin();
			for (ContasReceber boleto : (List<ContasReceber>)q.getResultList()) {
				boleto.setTransacao_gerencianet(null);
				boleto.setN_numero_gerencianet(null);
			}
			//em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean verificarDuplicidade(String nNumero){
		
		EntityManager em = ConnUtil.getEntity();
		
		StringBuilder sbnNumero = new StringBuilder(nNumero);
		sbnNumero.insert(2, "/");
		sbnNumero.insert(9, "-");
		
		Query q = em.createQuery("select c from ContasReceber c where c.n_numero_sicred_antigo=:nn", ContasReceber.class);
		q.setParameter("nn", sbnNumero.toString());
		
		if(q.getResultList().size() > 0){
			return true;
		}
		
		return false;
	}
	
	public static boolean checkBoletoVencidoPorEndereco(String endereco, String bairro, String numero, String cidade){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			Query q = em.createNativeQuery("SELECT cr.ID, cr.CLIENTES_ID "+
					"FROM enderecos_principais ep, contas_receber cr, clientes c "+
					"WHERE cr.CLIENTES_ID = c.ID "+
					"AND c.EMPRESA_ID = "+OpusERP4UI.getEmpresa().getId()+" "+
					"AND c.ENDERECO_ID = ep.ID "+
					"AND cr.STATUS_2 != 'FECHADO' "+
					"AND cr.STATUS_2 != 'EXCLUIDO' "+
					"AND cr.DATA_VENCIMENTO < NOW() "+
					"AND ep.ENDERECO LIKE '%"+endereco+"%' "
						+ "AND ep.BAIRRO LIKE '%"+bairro+"%' "
						+ "AND ep.NUMERO LIKE '"+numero+"'"
						+ "AND ep.CIDADE LIKE '%"+cidade+"%'");
			
			if(q.getResultList().size() > 0){
				
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
	
	public static boolean checkBoletoAdiantadoPorContrato(ContasReceber cr){
		

		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		String codContrato = cr.getN_doc().split("/")[0].toString();
		String regex = "^"+codContrato.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
				
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP :reg "+	
				"and cr.data_vencimento < :dataHoje ",
				
				ContasReceber.class);
		
		qn.setParameter("reg", regex);
		qn.setParameter("dataHoje", cr.getData_vencimento());
		
		if(qn.getResultList().size() > 0){
			return true;
		}
		
		return false;
		
		}catch (Exception e){
			e.printStackTrace();			
			return false;
		}
	}
	
	public static boolean allowEdit(ContasReceber c){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from NfeMestre c where c.contas_receber =:contas_receber ",NfeMestre.class);
			q.setParameter("contas_receber", c);
			
			if(q.getResultList().size() == 1){
				return false;
			}else{
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();			
			return false;
		}
	}
	
	public static boolean allowCloseOsi(String codOsi){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			Query q = em.createQuery("select c from ContasReceber c where c.n_doc =:ndoc and c.status = 'ABERTO'",ContasReceber.class);
			q.setParameter("ndoc", "OS"+codOsi);
			
			if(q.getResultList().size() == 1){
				return false;
			}else{
				return true;
			}
		
		}catch (Exception e){
			e.printStackTrace();			
			return false;
		}
	}
	
	
	/**
	 * Busca Todos os Boletos Atrasados, Abertos e No Padrão Novo ou Antigo de Acesso
	 * 
	 * @return List<ContasReceber>
	 */
	public static List<ContasReceber> getBoletosAtrasados(){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
				
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{1,}-[a-z]{2}[0-9]{1,}' "+	
				"and cr.data_vencimento < :dataHoje " +
				"and cr.bloquear is null  " +
				
				"or cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}' "+	
				"and cr.data_vencimento < :dataHoje " +
				"and cr.bloquear is null  " +
				
				"or cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}' "+	
				"and cr.data_vencimento < :dataHoje " +
				"and cr.bloquear != 'S' " +
				
				"or " +
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}/[0-9]{2}' "+
				"and cr.data_vencimento <= :dataHoje "+
				"and cr.bloquear is null  " +
				
				"or " +
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}/[0-9]{2}' "+
				"and cr.data_vencimento <= :dataHoje "+
				"and cr.bloquear != 'S' ",
				ContasReceber.class);
		
		qn.setParameter("dataHoje", new Date());
		
		return qn.getResultList();
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	/**
	 * Busca Todos os Boletos no Padrão de Acesso Marcados para Bloquear 
	 * e que ainda não Foram Bloqueado e também vencidos
	 * 
	 * @return List<ContasReceber>
	 */
	public static List<ContasReceber> getBoletosParaBloquear(){
		
		EntityManager em = ConnUtil.getEntity();
		
		
		try{
		
		Query qn = em.createNativeQuery(
				"select * from contas_receber cr where " +				
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}' "+	
				"and cr.data_vencimento < :dataHoje " +
				"and cr.bloquear = 'S' " +
				"and cr.bloqueado is null " +
				
				"or cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}' "+	
				"and cr.data_vencimento < :dataHoje " +
				"and cr.bloquear = 'S' " +
				"and cr.bloqueado != 'S' " +
								
				"or " +
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}/[0-9]{2}' "+
				"and cr.data_vencimento <= :dataHoje "+
				"and cr.bloquear = 'S' "+
				"and cr.bloqueado is null "+
				
				"or " +
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}/[0-9]{2}' "+
				"and cr.data_vencimento <= :dataHoje "+
				"and cr.bloquear = 'S' "+
				"and cr.bloqueado != 'S' ",
				ContasReceber.class);
		
		qn.setParameter("dataHoje", new Date());
		
		return qn.getResultList();
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public static List<ContasReceber> getBoletosBloqueado(Integer codContrato){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			Query qn = em.createNativeQuery(
					"select * from contas_receber cr where " +				
					"cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP '^"+codContrato+"/[0-9]{2}-[0-9]{2}/[0-9]{2}' "+	
					"and cr.data_vencimento < :dataHoje " +
					"and cr.bloqueado = 'S' " +				
					
					"or cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP '^"+codContrato+"/[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}' "+	
					"and cr.data_vencimento < :dataHoje " +
					"and cr.bloqueado = 'S' "+
					
					"or cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP '^"+codContrato+"/PRORATA' "+	
					"and cr.data_vencimento < :dataHoje " +
					"and cr.bloqueado = 'S' ", 
					
					ContasReceber.class);
			
			qn.setParameter("dataHoje", new Date());
			
			return qn.getResultList();
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	/**
	 * Verifica se é Permitido o Cadastro Manual do NDoc Passado
	 * 
	 * @param ndoc
	 * @return boolean
	 */
	public static boolean allowNdocManual(String ndoc){
		
		List<String> default_nDocs = new ArrayList<>();
		default_nDocs.add("^[0-9]{1,}/[0-9]{2}/[0-9]{2}");
		default_nDocs.add("^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}");
		default_nDocs.add("^[0-9]{1,}/PRORATA");
		
		for (String string : default_nDocs) {
			
			Pattern p = Pattern.compile(string);
			Matcher m = p.matcher(ndoc);
			
			if(m.matches()){
				return false;
			}			
		}
		
		return true;
	}
	
	/**
	 * Verifica se Número do Documento Passado Já Existe na Tabela
	 * 
	 * @param nDoc
	 * @return boolean
	 */
	public static boolean isExistNDoc(String nDoc){
		EntityManager em = ConnUtil.getEntity();
		
		
		try{
			Query q = em.createQuery("select s from ContasReceber s where s.n_doc = :ndoc", ContasReceber.class);
			q.setParameter("ndoc", nDoc);
			
			if(q.getResultList().size() >0){
				return true;
			}
			
			return false;
		}catch (Exception e){
			e.printStackTrace();			
			return false;
		}
	}
	
	
	public static List<ContasReceber> getParcelas(String nDoc){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select cr from ContasReceber cr where cr.n_doc LIKE CONCAT(:nDoc,'%')", ContasReceber.class);
			q.setParameter("nDoc", nDoc);
			
			return q.getResultList();
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public static Integer getQtdParcelas(String nDoc){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select cr from ContasReceber cr where cr.n_doc LIKE CONCAT(:nDoc,'%')", ContasReceber.class);
			q.setParameter("nDoc", nDoc);
			
			return q.getResultList().size();
		}catch (Exception e){
			e.printStackTrace();			
			return 0;
		}
		
	}
	
	public static boolean baixaBolProcCompl(Integer codContaReceber, String valor, String valor_lancamento, String valor_tarifa, String formaPgto, String tipoBaixa, Date dataPagamento, 
			Date dataBaixa, Double valorRecebido, Double valorTroco){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			
			em.getTransaction().commit();			
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		
	}
			
	public static boolean baixarBoletoProcessoCompleto(Integer codContaReceber, String valor, String valor_lancamento, String valor_tarifa, String formaPgto, String tipoBaixa, 
			Date dataPagamento, Date dataBaixa, Double valorRecebido, Double valorTroco){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();		
		
				//--Baixa Boleto
				ContasReceber cr = em.find(ContasReceber.class, codContaReceber);
				
				if(cr != null){
					cr.setValor_lancamento(valor_lancamento);
					cr.setValor_tarifa(valor_tarifa);
					cr.setValor_pagamento(valor);
					cr.setForma_pgto(formaPgto);
					cr.setTipo_baixa(tipoBaixa);
					cr.setData_pagamento(dataPagamento);
					cr.setData_baixa(dataBaixa);
					cr.setStatus("FECHADO");
					cr.setValor_recebido(valorRecebido);
					cr.setValor_troco(valorTroco);
					cr.setOperador(OpusERP4UI.getUsuarioLogadoUI().getUsername());
					
					//REENVIA REMESSA 
					//SOLICITA BAIXA DE BOLETO NO BANCO
					if(cr.getRemessaEnviada() != null && cr.getRemessaEnviada().equals("SIM")){
						cr.setRemessaEnviada(null);
						cr.setComandoRemessa("02");
					}
					
					em.merge(cr);	
				}
				
				//--Recarga Pre-Paga
				if (ChecarPadraoAcessoNDoc(cr.getN_doc())) {
			    	
			    	String numeroDoc = cr.getN_doc();
	
					Integer codAcesso = Integer.parseInt(numeroDoc.split("/")[0].toString());		    	
			    	AcessoCliente acessoCliente = em.find(AcessoCliente.class, codAcesso);
			    	
			    	if(acessoCliente != null && acessoCliente.getContrato().getTipo_contrato().equals("PRE-PAGO")){		    		
			    		
			    		if(acessoCliente.getData_venc_contrato().before(new Date())){
			    		
			    			acessoCliente.setData_venc_contrato(
				    				new DateTime().plusMonths(acessoCliente.getContrato().getVigencia()).toDate());		    			
			    		
			    		}else{
			    			acessoCliente.setData_venc_contrato(
			    					new DateTime(acessoCliente.getData_venc_contrato()).plusMonths(acessoCliente.getContrato().getVigencia()).toDate());
			    		}
			    		
			    		em.merge(acessoCliente);		    	
			    	}
			    }
				
				
				//--Desbloqueia Contrato
				AcessoCliente acesso = null;
				if(ModuloEmpresaDAO.checkModuloEmpresa(Modulo.COM.ACESSO)){
					if(cr != null && cr.getN_doc() != null && !ContasReceberDAO.allowNdocManual(cr.getN_doc())){
						String numeroDoc = cr.getN_doc();
						Integer codAcesso = Integer.parseInt(numeroDoc.split("/")[0].toString());				
						acesso = em.find(AcessoCliente.class,codAcesso);
					}
				}
					
				if(ModuloEmpresaDAO.checkModuloEmpresa(Modulo.COM.ACESSO) 
						&& cr != null 
						&& CredenciaisAcessoDAO.ChecarPadraoAcessoNDoc(cr.getN_doc())				
						&& acesso != null 
						&& acesso.getStatus_2() != null
						&& acesso.getStatus_2().equals("BLOQUEADO")){
					
					//Verificar Se Mais Algum Boleto Desse Cliente Esta Bloqueado
					if(!ContasReceberDAO.existeAlbumBoletoBloqueadoeAbertoPorContrato(acesso.getId(), cr.getId())){
											
						//Atualiza Status do Acesso para Ativo
						acesso.setStatus_2("ATIVO");
						em.merge(acesso);
						
						//Remove marcações RadReply
						Query qrr = em.createQuery("select rr from RadReply rr where rr.username = :usuario and rr.attribute = 'Framed-Pool' and rr.value = 'BLOQUEADO'", RadReply.class);
						qrr.setParameter("usuario", acesso.getLogin());						
						if(qrr.getResultList().size() >0){
									
							List<RadReply> marcacoes = qrr.getResultList(); 
							for(RadReply rr:marcacoes){
								em.remove(rr);
							}
						}
						Query qrr2 = em.createQuery("select rr from RadReply rr where rr.username = :usuario and rr.attribute = 'Framed-Pool' and rr.value = 'BLOQUEADO_TOTAL'", RadReply.class);
						qrr2.setParameter("usuario", acesso.getLogin());						
						if(qrr2.getResultList().size() >0){
									
							List<RadReply> marcacoes = qrr2.getResultList(); 
							for(RadReply rr:marcacoes){
								em.remove(rr);
							}
						}
						
						if(acesso.getEndereco_ip() != null && !acesso.getEndereco_ip().equals("")){
							em.persist(new RadReply(null, acesso.getLogin(), "Framed-IP-Address", "=", acesso.getEndereco_ip()));
						}
														
						cr.setDesbloqueado("S");
						em.merge(cr);
						
						//Derruba Conexão do Cliente
						if(acesso != null && acesso.getBase() != null && acesso.getBase().getUsuario() != null && acesso.getBase().getSenha() != null && acesso.getBase().getEndereco_ip() != null && acesso.getBase().getPorta_api() != null && acesso.getLogin() != null){
							MikrotikUtil.desconectarCliente(acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()), acesso.getLogin());
						}
						
						//Muda status do ITTV para ativo
						if(acesso != null && acesso.getIttv_id() != null){
							String s = IttvDAO.atualizarStatus(acesso.getIttv_id(), "ACTIVE");
							System.out.println(s);
						}
						
						Notify.Show("Contrato de Acesso Desbloqueado!", Notify.TYPE_NOTICE);				
					}										
				}
				
				//--Devolve Crédito
				Query q = em.createNativeQuery("SELECT * FROM credito_cliente WHERE CLIENTES_ID = "+cr.getCliente().getId(), CreditoCliente.class);	
				
				if(q.getResultList().size() == 1){
					CreditoCliente credito = (CreditoCliente) q.getSingleResult();
					
					Double saldo = Double.parseDouble(credito.getSaldo());
					Double novoSaldo = saldo + Real.formatStringToDBDouble(cr.getValor_titulo());
					credito.setSaldo(novoSaldo.toString());
					
					em.merge(credito);				
				}
				
				//--Gera Logs
				em.persist(new AlteracoesContasReceber(null, "BAIXOU UM BOLETO(R$ "+valor.toString()+")", cr,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
				em.persist(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Baixou um Boleto"));
			
			em.getTransaction().commit();
			
			if (ChecarPadraoAcessoNDoc(cr.getN_doc()) && cr.getControle().equals("ACESSO-POS")) {
				Integer codAcesso = Integer.parseInt(cr.getN_doc().split("/")[0].toString());		    	
		    	AcessoCliente acessoCliente = em.find(AcessoCliente.class, codAcesso);
	            if(acessoCliente !=  null && !acessoCliente.getStatus_2().equals("ENCERRADO")){
	            	List<ContasReceber> boletosAbertos = procurarAbertoBoletosDoAcessoPorContrato(codAcesso);
	            	if(boletosAbertos == null){
	            		AcessoDAO.renovarContratoAcesso(acessoCliente);       
	            		AgendaRenovacoesDAO.save(new AgendaRenovacoes(null, acessoCliente, "RENOVADO", new Date())); 
	            	}
	            }
			}
			
			
	    	
			return true;	
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			
			e.printStackTrace();
			return false;
		}
		
	}

public static boolean ChecarPadraoAcessoNDoc(String nDoc){
	Pattern pAntigo = Pattern.compile("[0-9]{1,}/[0-9]{2}/[0-9]{2}");
	Matcher mAntigo = pAntigo.matcher(nDoc);
	
	Pattern pNovo = Pattern.compile("[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}");
    Matcher mNovo = pNovo.matcher(nDoc);
      
    if (mAntigo.matches() || mNovo.matches()) {
    	return true;
    }
    
    return false;
}
	
	
	/**
	 * Baixa um Boleto na Tabela
	 * 
	 * @param codContaReceber
	 * @param valor
	 * @param formaPgto
	 * @param tipoBaixa
	 * @param dataPagamento
	 * @param dataBaixa
	 * @param valorRecebido
	 * @param valorTroco
	 * 
	 * @return boolean
	 */
	public static boolean baixarBoleto(Integer codContaReceber, String valor, String valor_lancamento, String valor_tarifa, String formaPgto, String tipoBaixa, 
			Date dataPagamento, Date dataBaixa, Double valorRecebido, Double valorTroco){
		
		EntityManager em = ConnUtil.getEntity();
		try{
			ContasReceber cr = em.find(ContasReceber.class, codContaReceber);
			
			if(cr != null)
				cr.setValor_lancamento(valor_lancamento);
				cr.setValor_tarifa(valor_tarifa);
				cr.setValor_pagamento(valor);
				cr.setForma_pgto(formaPgto);
				cr.setTipo_baixa(tipoBaixa);
				cr.setData_pagamento(dataPagamento);
				cr.setData_baixa(dataBaixa);
				cr.setStatus("FECHADO");
				cr.setValor_recebido(valorRecebido);
				cr.setValor_troco(valorTroco);
				cr.setOperador(OpusERP4UI.getUsuarioLogadoUI().getUsername());
				
				//REENVIA REMESSA 
				//SOLICITA BAIXA DE BOLETO NO BANCO
				if(cr.getRemessaEnviada() != null && cr.getRemessaEnviada().equals("SIM")){
					cr.setRemessaEnviada(null);
					cr.setComandoRemessa("02");
				}
				
				em.getTransaction().begin();		
				em.merge(cr);
				em.getTransaction().commit();
				
				if (ChecarPadraoAcessoNDoc(cr.getN_doc()) && cr.getControle().equals("ACESSO-POS")) {
					Integer codAcesso = Integer.parseInt(cr.getN_doc().split("/")[0].toString());		    	
			    	AcessoCliente acessoCliente = em.find(AcessoCliente.class, codAcesso);
		            if(acessoCliente !=  null){
		            	List<ContasReceber> boletosAbertos = procurarAbertoBoletosDoAcessoPorContrato(codAcesso);
		            	if(boletosAbertos == null){
		            		AcessoDAO.renovarContratoAcesso(acessoCliente);       
		            		AgendaRenovacoesDAO.save(new AgendaRenovacoes(null, acessoCliente, "RENOVADO", new Date())); 
		            	}
		            }
				}

				if(cr.getStatus().equals("FECHADO")){			
					return true;
				}
						
			return false;			
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Procura uma Boleto pelo Código
	 * 
	 * @param cod
	 * @return ContasReceber
	 */
	public static ContasReceber find(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		return em.find(ContasReceber.class, cod);
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	/**
	 * Verifica se Existe Algum boleto Bloqueado num Contrato
	 * 
	 * @param codAcesso
	 * @param codBoleto como excessão da busca
	 * @return boolean
	 */
	public static boolean existeAlbumBoletoBloqueadoeAbertoPorContrato(Integer codAcesso, Integer codBoleto){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
		String regexProrata = "^"+codAcesso.toString()+"/PRORATA";
		
		Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
				"cr.status_2 ='ABERTO' " +
				"and cr.bloqueado ='S' " +
				"and cr.n_doc REGEXP :rNova " +
				"and cr.id != :id "+
				
				"or cr.status_2 ='ABERTO' " +
				"and cr.bloqueado ='S' " +
				"and cr.n_doc REGEXP :rAntiga " +
				"and cr.id != :id "+
				
				"or cr.status_2 ='ABERTO' " +
				"and cr.bloqueado ='S' " +
				"and cr.n_doc REGEXP :rProrata " +
				"and cr.id != :id ",
				
				ContasReceber.class);
		
		qn.setParameter("rNova", regexNova);
		qn.setParameter("rAntiga", regexAntiga);
		qn.setParameter("rProrata", regexProrata);
		qn.setParameter("id", codBoleto);
		
		if(qn.getResultList().size()>0){
			return true;
		}
		
		return false;
		
		}catch (Exception e){
			e.printStackTrace();			
			return false;
		}
		
	}
	
	public static boolean existeAlgumBoletoAbertoeVencido(Cliente cliente){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select cr from ContasReceber cr where cr.cliente.doc_cpf_cnpj = :cliente and cr.data_vencimento <:dataHoje and cr.status !='FECHADO' and cr.status !='EXCLUIDO'", ContasReceber.class);
			q.setParameter("cliente", cliente.getDoc_cpf_cnpj());
			q.setParameter("dataHoje", new Date());
			
			if(q.getResultList().size()>0){
				return true;
			}
			
			return false;
		
		}catch (Exception e){
			e.printStackTrace();			
			return false;
		}
		
	}
	
	public static boolean existeAlgumBoletoBloqueadoeAberto(Integer codCliente, Integer codBoleto){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select c from ContasReceber c where "
				+ "c.bloqueado = 'S' and c.status = 'ABERTO' "
				+ "and c.id != :id "
				+ "and c.cliente = :cliente", 
				ContasReceber.class);
		
		q.setParameter("id", codBoleto);
		q.setParameter("cliente", new Cliente(codCliente));
		
		if(q.getResultList().size()>0){
			return true;
		}
		
		return false;
		
		}catch (Exception e){
			e.printStackTrace();			
			return false;
		}
	}
	
	
	/**
	 * Lista todos os boletos de Uma Empresa
	 * 
	 * @param codEmpresa
	 * @return List<ContasReceber>
	 */
	public List<ContasReceber> listarTitulos(Integer codEmpresa){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			Query q = em.createQuery("select cr from ContasReceber cr where cr.empresa_id =:codEmpresa ", ContasReceber.class);
			q.setParameter("codEmpresa", codEmpresa);
			
			if(q.getResultList().size()>0){
				return q.getResultList();
			}else{
				return null;
			}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	/**
	 * Muda Titularidade de Clientes
	 * 
	 * @param codClienteAntigo
	 * @param codClienteNovo
	 * @throws Exception
	 */
	public static void mudarTitular(Integer codClienteAntigo, Integer codClienteNovo, Integer codAcesso) throws Exception{
			
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
			String regexProrata = "^"+codAcesso.toString()+"/PRORATA";
			
			Query q = em.createNativeQuery("select * from contas_receber cr where " +				
					"cr.status_2 ='ABERTO' " +				
					"and cr.n_doc REGEXP :rNova " +				
					
					"or cr.status_2 ='ABERTO' " +				
					"and cr.n_doc REGEXP :rAntiga "+
					
					"or cr.status_2 ='ABERTO' " +				
					"and cr.n_doc REGEXP :rProrata ",			
					ContasReceber.class);
			
			q.setParameter("rNova", regexNova);
			q.setParameter("rAntiga", regexAntiga);
			q.setParameter("rProrata", regexProrata);
			
			if(q.getResultList().size() > 0){
				//em.getTransaction().begin();
					for(ContasReceber cr:(List<ContasReceber>)q.getResultList()){
						cr.setCliente(new Cliente(codClienteNovo));
						em.merge(cr);
					}
				//em.getTransaction().commit();
			}
		
		}catch (Exception e){
			e.printStackTrace();			
			
		}
	}
	
	
	/**
	 * Busca Boletos Abertos que Nãos Estejam Vencidos de um Determinado Contrato
	 * 
	 * @param codAcesso
	 * @return List<ContasReceber>
	 */
	public static  List<ContasReceber> buscarTitulosAbertosNaoVencidosDeAcessoPorContrato(Integer codAcesso){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
			String rProrata = codAcesso.toString()+"/PRORATA";
			
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
					"cr.status_2 ='ABERTO' " +				
					"and cr.n_doc REGEXP :rNova " +
					"and cr.data_vencimento >= :dataVencimento "+
					
					"or cr.status_2 ='ABERTO' " +				
					"and cr.n_doc REGEXP :rAntiga " +
					"and cr.data_vencimento >= :dataVencimento "+
					
					"or cr.status_2 ='ABERTO' " +				
					"and cr.n_doc REGEXP :rProrata " +
					"and cr.data_vencimento >= :dataVencimento "
					
					+ "ORDER BY cr.data_vencimento ASC ",
					
					ContasReceber.class);
			
			qn.setParameter("rNova", regexNova);
			qn.setParameter("rAntiga", regexAntiga);
			qn.setParameter("rProrata", rProrata);
			qn.setParameter("dataVencimento", new Date());
			
			if(qn.getResultList().size()>0){
				return qn.getResultList();
			}
			
			return null;
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public ContasReceber buscarTitulosAbertosVencidosDeAcessoPorContrato(Integer codAcesso){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			String regexProrata = codAcesso.toString()+"/PRORATA";
			String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
					"cr.status_2 ='ABERTO' " +				
					"and cr.n_doc REGEXP :rNova " +
					"and cr.data_vencimento <= :dataVencimento "+
					
					"or cr.status_2 ='ABERTO' " +				
					"and cr.n_doc REGEXP :rAntiga " +
					"and cr.data_vencimento <= :dataVencimento "+
					
					"or cr.status_2 ='ABERTO' " +				
					"and cr.n_doc LIKE :rNovaProrata " +
					"and cr.data_vencimento <= :dataVencimento "
					+ "ORDER BY cr.data_vencimento ASC ",
					
					ContasReceber.class);
			
			qn.setParameter("rNovaProrata", regexProrata);
			qn.setParameter("rNova", regexNova);
			qn.setParameter("rAntiga", regexAntiga);
			qn.setParameter("dataVencimento", new Date());
			
			if(qn.getResultList().size()>0){
				return  (ContasReceber) qn.getResultList().get(0);
			}
			
			return null;
			
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public List<ContasReceber> buscarTitulosAbertosNaoVencidosDeAcesso(Integer codCliente) {
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
				
			Query qn = em.createNativeQuery("select * from contas_receber cr where " +
					"cr.clientes_id = :cliente " +
					"and cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}' "+	
					"and cr.data_vencimento >= :dataVencimento " +
					
					"or cr.clientes_id = :cliente " +
					"and cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP '^[0-9]{4}/[0-9]{2}/[0-9]{2}' "+
					"and cr.data_vencimento >= :dataVencimento "+
					
					"or cr.clientes_id = :cliente " +
					"and cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP '^[0-9]{4}/PRORATA' "+
					"and cr.data_vencimento >= :dataVencimento "
					
					+ "ORDER BY cr.data_vencimento ASC ",
					ContasReceber.class); 
			
			qn.setParameter("cliente", codCliente);		
			qn.setParameter("dataVencimento", new Date());
			
			if(qn.getResultList().size() > 0){
				return qn.getResultList();
			}else{
				return null;
			}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	
	/**
	 * Verifica se Existem Boletos Pagos e Adiantados de um Determinado Contrato
	 * 
	 * @param codAcesso
	 * @return boolean
	 */
	public boolean buscarTitulosAdiantadosDeAcessoPorContrato(Integer codAcesso) {
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
			String regexProrata = "^"+codAcesso.toString()+"/PRORATA";
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
					"cr.status_2 ='FECHADO' " +				
					"and cr.n_doc REGEXP :rNova " +
					"and cr.data_vencimento > :dataVencimento "+
					
					"or cr.status_2 ='FECHADO' " +				
					"and cr.n_doc REGEXP :rAntiga " +
					"and cr.data_vencimento > :dataVencimento "+
					
					"or cr.status_2 ='FECHADO' " +				
					"and cr.n_doc REGEXP :rProrata " +
					"and cr.data_vencimento > :dataVencimento ",
					
					ContasReceber.class);
			
			qn.setParameter("rNova", regexNova);
			qn.setParameter("rAntiga", regexAntiga);
			qn.setParameter("rProrata", regexProrata);
			qn.setParameter("dataVencimento", new Date());
			
			
			if(qn.getResultList().size()>0){
				return true;
			}
			
			return false;
			
		}catch (Exception e){
			e.printStackTrace();			
			return false;
		}
	}
	
	
	public boolean buscarTitulosAdiantadosDeAcesso(Integer codCliente) {
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			boolean check = false;
			Query qn = em.createNativeQuery("select * from contas_receber cr where " +
					"cr.clientes_id = :cliente " +
					"and cr.status_2 ='FECHADO' " +
					"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}' "+	
					"and cr.data_vencimento > :dataVencimento " +
					
					"or cr.clientes_id = :cliente " +
					"and cr.status_2 ='FECHADO' " +
					"and cr.n_doc REGEXP '^[0-9]{4}/[0-9]{2}/[0-9]{2}' "+
					"and cr.data_vencimento > :dataVencimento "+
					
					"or cr.clientes_id = :cliente " +
					"and cr.status_2 ='FECHADO' " +
					"and cr.n_doc REGEXP '^[0-9]{4}/PRORATA' "+
					"and cr.data_vencimento > :dataVencimento ",
					ContasReceber.class); 
			
			qn.setParameter("cliente", codCliente);		
			qn.setParameter("dataVencimento", new Date());
			
			if(qn.getResultList().size() > 0){			
				check = true;
			}
			
			return check;
			
		}catch (Exception e){
			e.printStackTrace();			
			return false;
		}
	}
	
	/**
	 * Buscar Boletos não Vencidos de um Determinado Contrato
	 * 
	 * @param codAcesso
	 * @return List<ContasReceber>
	 */
	public static List<ContasReceber> buscarTitulosNaoVencidosDeAcessoPorContrato(Integer codAcesso) {
		
		EntityManager em = ConnUtil.getEntity();
		
		
		try{
		String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
		String regexProrata= "^"+codAcesso.toString()+"/PRORATA";
		
		//or cr.status_2 != 'EXCLUIDO' and cr.status_2 NOT LIKE '%NEGATIVADO%' and cr.data
		Query qn = em.createNativeQuery("select * from contas_receber cr where "+				
				"cr.status_2 != 'EXCLUIDO' "+ 
				"and cr.status_2 NOT LIKE '%NEGATIVADO%' "+ 
				"and cr.data_vencimento >= :dataVencimento "+
				"and cr.n_doc REGEXP :rNova "+
				
				"or "+
				"cr.status_2 != 'EXCLUIDO' "+ 
				"and cr.status_2 NOT LIKE '%NEGATIVADO%' "+ 
				"and cr.data_vencimento >= :dataVencimento "+
				"and cr.n_doc REGEXP :rAntiga "+
				
				"or "+
				"cr.status_2 != 'EXCLUIDO' "+ 
				"and cr.status_2 NOT LIKE '%NEGATIVADO%' "+ 
				"and cr.data_vencimento >= :dataVencimento "+
				"and cr.n_doc REGEXP :rProrata "+
				"order by cr.data_vencimento asc "
				
				, ContasReceber.class);
				
		qn.setParameter("dataVencimento", new Date());
		qn.setParameter("rNova", regexNova);
		qn.setParameter("rAntiga", regexAntiga);
		qn.setParameter("rProrata", regexProrata);
		
		if(qn.getResultList().size() > 0){
			return qn.getResultList();
		}else{
			return null;
		}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public List<ContasReceber> buscarTitulosNaoVencidosDeAcesso(Integer codCliente) {
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		Query qn = em.createNativeQuery("select * from contas_receber cr where "+ 
				"cr.clientes_id = :cliente "+
				"and cr.status_2 != 'EXCLUIDO' "+ 
				"and cr.status_2 NOT LIKE '%NEGATIVADO%' "+ 
				"and cr.data_vencimento >= :dataVencimento "+
				"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}' "+
				
				"or cr.clientes_id = :cliente "+
				"and cr.status_2 != 'EXCLUIDO' "+ 
				"and cr.status_2 NOT LIKE '%NEGATIVADO%' "+ 
				"and cr.data_vencimento >= :dataVencimento "+
				"and cr.n_doc REGEXP '^[0-9]{4}/[0-9]{2}/[0-9]{2}' "+
				
				"or cr.clientes_id = :cliente "+
				"and cr.status_2 != 'EXCLUIDO' "+ 
				"and cr.status_2 NOT LIKE '%NEGATIVADO%' "+ 
				"and cr.data_vencimento >= :dataVencimento "+
				"and cr.n_doc REGEXP '^[0-9]{4}/PRORATA' "+
				
				"order by cr.data_vencimento asc "
				, ContasReceber.class);
		
		qn.setParameter("cliente", codCliente);		
		qn.setParameter("dataVencimento", new Date());
		
		if(qn.getResultList().size() > 0){
			return qn.getResultList();
		}else{
			return null;
		}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
		
	/**
	 * Busca Boletos Bloqueados e ainda Abertos de um Determinado Contrato
	 * 
	 * @param codAcesso
	 * @return List<ContasReceber>
	 */
	public static List<ContasReceber> procurarBoletosBloqueadosPorContrato(Integer codAcesso){
		
		EntityManager em = ConnUtil.getEntity();
		
		
		try{
				
			String regexProrata = "^"+codAcesso.toString()+"/PRORATA";
			String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
					"cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP :rNova " +
					"and cr.bloqueado = 'S' "+
					"and cr.desbloqueado IS NULL "+
					
					"or cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP :rAntiga " +
					"and cr.bloqueado = 'S' "+
					"and cr.desbloqueado IS NULL "+
					
					"or cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP :rProrata " +
					"and cr.bloqueado = 'S' "+
					"and cr.desbloqueado IS NULL ",
					
					ContasReceber.class);
			
			qn.setParameter("rProrata", regexProrata);
			qn.setParameter("rNova", regexNova);
			qn.setParameter("rAntiga", regexAntiga);
					
			if(qn.getResultList().size() > 0){
				return  qn.getResultList();
			}
		
			return null;
			
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public List<ContasReceber> procurarBoletosBloqueadosPorClientes(Integer codCliente)
	{
		EntityManager em = ConnUtil.getEntity();
				
		
		try{
			Query q = em.createQuery("select cr from ContasReceber cr where cr.cliente = :cliente " +
					"and cr.status ='ABERTO' " +
					"and cr.bloqueado = 'S' " +
					"and cr.desbloqueado IS NULL", ContasReceber.class);
			q.setParameter("cliente", new Cliente(codCliente));
			
			if(q.getResultList().size() > 0){
				return q.getResultList();
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;			
		}
	}
	
	/**
	 * Busca Boletos Abertos de Um Determinado Contrato
	 * 
	 * @param codAcesso
	 * @return List<ContasReceber>
	 */
	public static List<ContasReceber> procurarBoletosDoAcessoPorContrato(Integer codAcesso){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
		String regexPro = "^"+codAcesso.toString()+"/PRORATA";
		
		Query qn = em.createNativeQuery("select * from contas_receber cr where "+				
				"cr.status_2 = 'ABERTO' "+			
				"and cr.n_doc REGEXP :rNova "+
				
				"or "+
				"cr.status_2 = 'ABERTO' "+				
				"and cr.n_doc REGEXP :rAntiga "+

				"or "+
				"cr.status_2 = 'ABERTO' "+				
				"and cr.n_doc REGEXP :rProRata "
				
				, ContasReceber.class);
			
		qn.setParameter("rNova", regexNova);
		qn.setParameter("rAntiga", regexAntiga);
		qn.setParameter("rProRata", regexPro);
		
		if(qn.getResultList().size() > 0){
			return qn.getResultList();
		}else{
			return null;
		}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public static ContasReceber procurarBoletoMensal(Integer codAcesso, String anoMes){
		
		EntityManager em = ConnUtil.getEntity();
		
		
		try{
		String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
		String regexProrata = "^"+codAcesso.toString()+"/PRORATA";
		
		Query qn = em.createNativeQuery("select * from contas_receber cr where "+				
				"cr.status_2 != 'EXCLUIDO' "+			
				"and cr.n_doc REGEXP :rNova "+ 
				"and DATE_FORMAT(cr.data_vencimento, '%y%m') =:anoMes "+
				
				"or "+
				"cr.status_2 != 'EXCLUIDO' "+				
				"and cr.n_doc REGEXP :rAntiga "+
				"and DATE_FORMAT(cr.data_vencimento, '%y%m') =:anoMes"+
				
				"or "+
				"cr.status_2 != 'EXCLUIDO' "+				
				"and cr.n_doc REGEXP :rProRata "+
				"and DATE_FORMAT(cr.data_vencimento, '%y%m') =:anoMes"
				
				, ContasReceber.class);
			
		qn.setParameter("rNova", regexNova);
		qn.setParameter("rAntiga", regexAntiga);
		qn.setParameter("rProRata", regexProrata);
		qn.setParameter("anoMes", anoMes);
		
		
		if(qn.getResultList().size() == 1){
			return (ContasReceber)qn.getSingleResult();
		}else{
			return null;
		}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public List<ContasReceber> procurarBoletosDoAcessoPorClientes(Integer codCliente)
	{
		EntityManager em  = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select cr from ContasReceber cr where cr.cliente = :cliente " +
				"and cr.status ='ABERTO' ", ContasReceber.class);
		q.setParameter("cliente", new Cliente(codCliente));
		
		if(q.getResultList().size() > 0){
			return q.getResultList();
		}else{
			return null;
		}
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	/**
	 * Busca Todos os Boletos de um Determinado Contrato
	 * 
	 * @param codAcesso
	 * @return List<ContasReceber>
	 */
	public static List<ContasReceber> procurarTodosBoletosDoAcessoPorContrato(Integer codAcesso){
		EntityManager em = ConnUtil.getEntity();

		try{
		String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";//1245/15-12/12
		String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
		String rProRata = codAcesso.toString()+"/PRORATA";
		
		Query qn = em.createNativeQuery("select * from contas_receber cr where "+				
							
				"cr.n_doc REGEXP :rNova "+ "and cr.status_2 !='EXCLUIDO' "+
				
				"or "+							
				"cr.n_doc REGEXP :rAntiga " + "and cr.status_2 !='EXCLUIDO' " +
				"or "+
				"cr.n_doc LIKE :rProrata " + "and cr.status_2 !='EXCLUIDO' " +
				
				
				"ORDER BY cr.data_vencimento DESC "
				
				, ContasReceber.class);
			
		qn.setParameter("rNova", regexNova);
		qn.setParameter("rAntiga", regexAntiga);
		qn.setParameter("rProrata", rProRata);
		
		if(qn.getResultList().size() > 0){
			return qn.getResultList();
		}else{
			return null;
		}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public static List<ContasReceber> procurarBoletoPorAcessoeVencimento(Integer codAcesso, Date dataVencimento){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
			String rProrata = codAcesso.toString()+"/PRORATA";
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where "+	
					"cr.n_doc REGEXP :rNova "+ "and cr.status_2 ='ABERTO' and cr.data_vencimento =:dVencimento "+
					"or "+							
					"cr.n_doc REGEXP :rAntiga " + "and cr.status_2 ='ABERTO' and cr.data_vencimento =:dVencimento " + 
					"or "+
					"cr.n_doc LIKE :rProrata " + "and cr.status_2 ='ABERTO' and cr.data_vencimento =:dVencimento " 
					, ContasReceber.class);
				
			qn.setParameter("rNova", regexNova);
			qn.setParameter("rAntiga", regexAntiga);
			qn.setParameter("rProrata", rProrata);
			qn.setParameter("dVencimento", dataVencimento);
			
			if(qn.getResultList().size() > 0){
				return qn.getResultList();
			}else{
				return null;
			}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public static List<ContasReceber> procurarAbertoBoletosDoAcessoPorContrato(Integer codAcesso){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
			String rProrata = codAcesso.toString()+"/PRORATA";
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where "+	
					"cr.n_doc REGEXP :rNova "+ "and cr.status_2 ='ABERTO' "+
					"or "+							
					"cr.n_doc REGEXP :rAntiga " + "and cr.status_2 ='ABERTO' " + 
					"or "+
					"cr.n_doc LIKE :rProrata " + "and cr.status_2 ='ABERTO' " + 
					"ORDER BY cr.data_vencimento DESC "
					, ContasReceber.class);
				
			qn.setParameter("rNova", regexNova);
			qn.setParameter("rAntiga", regexAntiga);
			qn.setParameter("rProrata", rProrata);
			
			if(qn.getResultList().size() > 0){
				return qn.getResultList();
			}else{
				return null;
			}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public static List<ContasReceber> procurarAbertoBoletosDoAcessoPorContratoManutencao(Integer codContrato){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		String regexNova = "^MA"+codContrato.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		
		
		Query qn = em.createNativeQuery("select * from contas_receber cr where "+				
							
				"cr.n_doc REGEXP :rNova "+ "and cr.status_2 ='ABERTO' "+
				"ORDER BY cr.data_vencimento DESC "
				
				, ContasReceber.class);
			
		qn.setParameter("rNova", regexNova);
				
		if(qn.getResultList().size() > 0){
			return qn.getResultList();
		}else{
			return null;
		}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public List<ContasReceber> procurarTodosBoletosDoAcessoPorClientes(Integer codCliente)
	{
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select cr from ContasReceber cr where cr.cliente = :cliente " +
				"ORDER BY cr.data_vencimento DESC ", ContasReceber.class);
		q.setParameter("cliente", new Cliente(codCliente));
		
		if(q.getResultList().size() > 0){
			return q.getResultList();
		}else{
			return null;
		}
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	
	/**
	 * Procura por um Boleto Fechado e com Data de Vencimento Específica de um Determinado Contrato
	 * 
	 * @param codAcesso
	 * @param dataBoleto
	 * @return
	 */
	private ContasReceber procurarBoletosDoAcessoFechadoPorContrato(Integer codAcesso, Date dataBoleto){
		
		EntityManager em = ConnUtil.getEntity();
		
		
		try{
		
		String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
		String regexProrata = "^"+codAcesso.toString()+"/PRORATA";
		
		
		Query q = em.createNativeQuery(""
				+ "select * from contas_receber cr where "+
				
				"cr.n_doc REGEXP :rNova and "+
				 "cr.data_vencimento = :dataVenc and cr.status_2 = 'FECHADO' "+
				
				"or" +
				"cr.n_doc REGEXP :rAntiga and "+
				 "cr.data_vencimento = :dataVenc and cr.status_2 = 'FECHADO' "+
				
				"or" +
				"cr.n_doc REGEXP :rProrata and "+
				 "cr.data_vencimento = :dataVenc and cr.status_2 = 'FECHADO' ",
				ContasReceber.class);
				
		
		q.setParameter("rNova", regexNova);
		q.setParameter("rAntiga", regexAntiga);
		q.setParameter("rProrata", regexProrata);
		q.setParameter("dataVenc", dataBoleto);
		
		if(q.getResultList().size() == 1){
			return (ContasReceber)q.getSingleResult();
		}else{
			return null;
		}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	private static ContasReceber procurarBoletosDoAcessoFechado(Integer codContrato, String ndocumento,Date dataBoleto){
		
		EntityManager em = ConnUtil.getEntity();

		
		try{
		String regexNova = "^"+codContrato.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		String regexAntiga = "^"+codContrato.toString()+"/[0-9]{2}/[0-9]{2}";
		String regexProrata = "^"+codContrato.toString()+"/PRORATA";
		
		Query q = em.createNativeQuery(""
				+"select * from contas_receber cr where "				
				+"cr.n_doc REGEXP :rNova  and "
				+"cr.data_vencimento = :dataVenc and cr.status_2 = 'FECHADO' "
								
				+"or cr.n_doc REGEXP :rAntiga  and "+
				 "cr.data_vencimento = :dataVenc and cr.status_2 = 'FECHADO' "
				
				 +"or cr.n_doc REGEXP :rProrata  and "+
				 "cr.data_vencimento = :dataVenc and cr.status_2 = 'FECHADO' ",
				ContasReceber.class);
		
		q.setParameter("rNova", regexNova);
		q.setParameter("rAntiga", regexAntiga);
		q.setParameter("rProrata", regexProrata);
		q.setParameter("dataVenc", dataBoleto);
		
		if(q.getResultList().size() == 1){
			return (ContasReceber)q.getSingleResult();
		}else{
			return null;
		}
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	
	/**
	 * Gera Boleto(s) de Acesso
	 * 
	 * @param codCliente
	 * @param codContrato
	 * @param valorPrimeiroBoleto
	 * @param valorPlano
	 * @param primeiroVenc
	 * @param qtd
	 * @param valorAdesao
	 * @throws Exception
	 */
	public static void gerarBoletosAcesso(Integer codCliente, AcessoCliente contrato,String valorPrimeiroBoleto,String valorPlano, Date primeiroVenc, 
			Integer qtd,String valorAdesao, String controle, Integer codPlano) throws Exception{
		
			if(!valorPlano.equals(valorPrimeiroBoleto)){
				qtd = qtd+1;
			}
		
		
		
		   EntityManager em = ConnUtil.getEntity();
		   
		   boolean condicao2 = false;
				
			try {				
				for (int i = 0; i < qtd; i++) {			
										
					Date dataAtual = new Date();
					//Calendar cal = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yy"); 
					
					String anoDoc = String.valueOf(sdf.format(new Date()));
					
					condicao2 = !valorPlano.equals(valorPrimeiroBoleto) ? true : false;
					
					String ndocumento1;
					if(!condicao2){
						ndocumento1 = String.valueOf(contrato.getId())+"/"+anoDoc+"-"+String.format("%02d", i+1)+"/"+String.format("%02d", qtd);
					}else{
						ndocumento1 = String.valueOf(contrato.getId())+"/"+anoDoc+"-"+String.format("%02d", i)+"/"+String.format("%02d", qtd-1);
					}
					
					LocalDate localDate = new LocalDate(primeiroVenc);
					LocalDate moreDays = localDate.plusMonths(i);	
					Date dataBoleto = moreDays.toDate();
					contrato.setData_venc_contrato(dataBoleto); 
					
					ContasReceber crCadastrado = null;
					ContasReceber boletoFechado = procurarBoletosDoAcessoFechado(codCliente, ndocumento1,dataBoleto);
					
					boolean subtrair = false;
					if(boletoFechado != null && !contrato.getContrato().getTipo_contrato().equals("PRE-PAGO")){
						subtrair = true;
					}
					
					if(i == 0){
						
						String ndocumento = !valorPlano.equals(valorPrimeiroBoleto) ? contrato.getId().toString()+"/"+"PRORATA" : ndocumento1;
						
						if(subtrair){
							double valorSubtraido = Real.formatStringToDBDouble(valorPlano) - Real.formatStringToDBDouble(boletoFechado.getValor_titulo());
							
							if(valorSubtraido > 0){
								ContasReceber crXX = new ContasReceber(new Cliente(codCliente), ndocumento, Real.formatDbToString(String.valueOf(valorSubtraido)), new Date(), dataBoleto,"ABERTO", "BOLETO",controle, codPlano);
								crXX.setCodigo_cartao(contrato.getCodigo_cartao());
								
								crCadastrado = gerarBoletos(crXX);
							}
						}else{
							ContasReceber crXX = new ContasReceber(new Cliente(codCliente), ndocumento, valorPrimeiroBoleto, new Date(), dataBoleto,"ABERTO", "BOLETO",controle, codPlano);
							crXX.setCodigo_cartao(contrato.getCodigo_cartao());
							
							crCadastrado = gerarBoletos(crXX);
						}
						
					}else{
						
						if(subtrair){
							double valorSubtraido = Real.formatStringToDBDouble(valorPlano) - Real.formatStringToDBDouble(boletoFechado.getValor_titulo());
							ContasReceber crXX = new ContasReceber(new Cliente(codCliente), ndocumento1, Real.formatDbToString(String.valueOf(valorSubtraido)), new Date(), dataBoleto,"ABERTO", "BOLETO",controle, codPlano);
							crXX.setCodigo_cartao(contrato.getCodigo_cartao());
							
							crCadastrado = gerarBoletos(crXX);
						}else{
							ContasReceber crXX = new ContasReceber(new Cliente(codCliente), ndocumento1, valorPlano, new Date(), dataBoleto,"ABERTO", "BOLETO",controle, codPlano);
							crXX.setCodigo_cartao(contrato.getCodigo_cartao());
							
							crCadastrado = gerarBoletos(crXX);
						}
						
					}				
				}	
								
				if(valorAdesao != null && !valorAdesao.equals("0,00") && !valorAdesao.equals("0.00") && !valorAdesao.equals("0.0") && !valorAdesao.isEmpty()){
					ContasReceber crXX = new ContasReceber(new Cliente(codCliente), contrato.getId().toString()+"/ADESAO", Real.formatDbToString(valorAdesao), new Date(), new Date(),"ABERTO", "BOLETO","ADESAO"); 
					crXX.setCodigo_cartao(contrato.getCodigo_cartao());
					
					gerarBoletos(crXX);
				}
				
				em.merge(contrato);
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
	}
	
	public static void gerarBoletosAcesso2(Integer codCliente, AcessoCliente contrato,String valorPrimeiroBoleto,String valorPlano, Date primeiroVenc, 
			Integer qtd,String valorAdesao, String controle, Integer codPlano) throws Exception{
		
		   EntityManager em = ConnUtil.getEntity();
				
			try {				
				for (int i = 0; i < qtd; i++) {			
					
					
					
					Date dataAtual = new Date();
					//Calendar cal = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yy"); 
					
					String anoDoc = String.valueOf(sdf.format(new Date()));
					String ndocumento = String.valueOf(contrato.getId())+"/"+anoDoc+"-"+String.format("%02d", i+1)+"/"+String.format("%02d", qtd);
					
					LocalDate localDate = new LocalDate(primeiroVenc);
					LocalDate moreDays = localDate.plusMonths(i);	
					Date dataBoleto = moreDays.toDate();
					
					ContasReceber crCadastrado = null;
					ContasReceber boletoFechado = procurarBoletosDoAcessoFechado(codCliente, ndocumento,dataBoleto);
					
					boolean subtrair = false;
					if(boletoFechado != null && !contrato.getContrato().getTipo_contrato().equals("PRE-PAGO")){
						subtrair = true;
					}
					
					if(i == 0){
						
						if(subtrair){
							double valorSubtraido = Real.formatStringToDBDouble(valorPlano) - Real.formatStringToDBDouble(boletoFechado.getValor_titulo());
							
							if(valorSubtraido > 0){
								ContasReceber cr = new ContasReceber(new Cliente(codCliente), ndocumento, Real.formatDbToString(String.valueOf(valorSubtraido)), new Date(), dataBoleto,"ABERTO", "BOLETO",controle, codPlano);
								
								if(contrato.getCodigo_cartao() != null){
									cr.setCodigo_cartao(contrato.getCodigo_cartao());
								}
								
								crCadastrado = gerarBoletos(cr);
							}
						}else{
							ContasReceber cr = new ContasReceber(new Cliente(codCliente), ndocumento, valorPlano, new Date(), dataBoleto,"ABERTO", "BOLETO",controle, codPlano);
							
							if(contrato.getCodigo_cartao() != null){
								cr.setCodigo_cartao(contrato.getCodigo_cartao());
							}
							
							crCadastrado = gerarBoletos(cr);
						}
						
					}else{
						
						if(subtrair){
							
							double valorSubtraido = Real.formatStringToDBDouble(valorPlano) - Real.formatStringToDBDouble(boletoFechado.getValor_titulo());
							
							ContasReceber cr = new ContasReceber(new Cliente(codCliente), ndocumento, Real.formatDbToString(String.valueOf(valorSubtraido)), new Date(), dataBoleto,"ABERTO", "BOLETO",controle, codPlano);
							
							if(contrato.getCodigo_cartao() != null){
								cr.setCodigo_cartao(contrato.getCodigo_cartao());
							}
							
							
							crCadastrado = gerarBoletos(cr);
						}else{
							ContasReceber cr = new ContasReceber(new Cliente(codCliente), ndocumento, valorPlano, new Date(), dataBoleto,"ABERTO", "BOLETO",controle, codPlano);
							
							if(contrato.getCodigo_cartao() != null){
								cr.setCodigo_cartao(contrato.getCodigo_cartao());
							}
							
							crCadastrado = gerarBoletos(cr);
						}
						
					}
				

				}	
				
				
				if(valorAdesao != null && !valorAdesao.equals("0,00") && !valorAdesao.equals("0.00") && !valorAdesao.equals("0.0") && !valorAdesao.isEmpty()){
					
					ContasReceber cr = new ContasReceber(new Cliente(codCliente), contrato.getId().toString()+"/ADESAO", Real.formatDbToString(valorAdesao), new Date(), new Date(),"ABERTO", "BOLETO","ADESAO");
					
					if(contrato.getCodigo_cartao() != null){
						cr.setCodigo_cartao(contrato.getCodigo_cartao());
					}
					
					gerarBoletos(cr);
				}
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
	}
	
	public static void gerarBoletosManutencao(Integer codContrato, Integer codCliente,String valorPlano, Date primeiroVenc,Integer qtd,String controle, String valor_primeiro_boleto ) throws Exception{
		
		   EntityManager em = ConnUtil.getEntity();
				
			try {				
				for (int i = 0; i < qtd; i++) {			
					
					Date dataAtual = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yy"); 
					
					String anoDoc = String.valueOf(sdf.format(new Date()));
					String ndocumento = "MA"+codContrato+"/"+anoDoc+"-"+String.format("%02d", i+1)+"/"+String.format("%02d", qtd);
					
					LocalDate localDate = new LocalDate(primeiroVenc);
					LocalDate moreDays = localDate.plusMonths(i);	
					Date dataBoleto = moreDays.toDate();
					
					if(i == 0){
						gerarBoletos(new ContasReceber(new Cliente(codCliente), ndocumento, valor_primeiro_boleto, new Date(), dataBoleto,"ABERTO", "BOLETO",controle));
					}else{					
						ContasReceber crCadastrado = null;										
						crCadastrado = gerarBoletos(new ContasReceber(new Cliente(codCliente), ndocumento, valorPlano, new Date(), dataBoleto,"ABERTO", "BOLETO",controle));
					}
					
				}	
				
			} catch (Exception e) {				
				e.printStackTrace();
			}
	}
	
	public static boolean allowDeletePv(EcfPreVendaCabecalho pv)
	{
		EntityManager em = ConnUtil.getEntity();
		
		try{
		String nDoc = "PV/"+pv.getId().toString();
		Query q = em.createQuery("select cr from ContasReceber cr where cr.status = 'ABERTO' and cr.n_doc LIKE CONCAT(:nDoc,'%')", ContasReceber.class);
		q.setParameter("nDoc", nDoc);
	
		if(q.getResultList().size() == 0){
			return true;
		}else{
			return false;
		}
		
		}catch (Exception e){
			e.printStackTrace();			
			return false;
		}
	}
	public static void gerarBoletosPedido( EcfPreVendaCabecalho pedido,double valor, Integer qtd, Date dataPrimBoleto, double vlr_primeira_parc) throws Exception{
		
			EntityManager em = ConnUtil.getEntity();	
			try {		
				
				if(pedido.getTotal_desc() > 0){
					valor = pedido.getValor();
					if(qtd == 1){
						vlr_primeira_parc = pedido.getValor();
					}
				}
				

				Integer indice = 1;
				em.getTransaction().begin();
				if(vlr_primeira_parc > 0){
					String ndocumento1 = "PV/"+pedido.getId().toString()+"-"+String.format("%02d", 1)+"/"+String.format("%02d", qtd);
					gerarBoletos(new ContasReceber(pedido.getCliente(), ndocumento1, Real.formatDbToString(String.valueOf(vlr_primeira_parc)), new Date(), dataPrimBoleto,"ABERTO", "BOLETO",pedido.getTipoVenda(), String.valueOf(indice)+"/"+String.valueOf(qtd)));
					indice++;
				}
				
				
				double valor_parc = qtd > 1 ? Real.formatStringToDBDouble(new DecimalFormat("0.00").format((valor-vlr_primeira_parc)/(qtd-1))) : valor;
				BigDecimal valorParc = new BigDecimal(valor_parc);
				
				String s1= String.valueOf(valor_parc).replaceAll("\\.", ",");
				String[] s2 = s1.split(",");
				
				if(s2.length > 0 && s2[1].length() > 2){
//					valorPrimeiraParc = valorPrimeiraParc.setScale(2, BigDecimal.ROUND_CEILING);
					valorParc = valorParc.setScale(2, BigDecimal.ROUND_CEILING); 
				}		
				
				
				double valor_ultima_parc= qtd>1 ? (valor - (Double.parseDouble(valorParc.toString()) * (qtd-2)) - vlr_primeira_parc) : 0;
				
				for (int i = vlr_primeira_parc > 0 ? 1 : 0; i < qtd; i++) {											
					
					//Calendar cal = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yy"); 
					
					String anoDoc = String.valueOf(sdf.format(new Date()));
					//String ndocumento = "PV/"+pedido.getId().toString();
					String ndocumento = "PV/"+pedido.getId().toString()+"-"+String.format("%02d", i+1)+"/"+String.format("%02d", qtd);
					
					LocalDate localDate = new LocalDate(dataPrimBoleto);
					LocalDate moreDays = localDate.plusMonths(i);	
					Date dataBoleto = moreDays.toDate();		
					
					if(qtd > 1){
						if((qtd-1) == i){
							if(qtd>2){							
								gerarBoletos(new ContasReceber(pedido.getCliente(), ndocumento, Real.formatDbToString(String.valueOf(valor_ultima_parc)), new Date(), dataBoleto,"ABERTO", "BOLETO",pedido.getTipoVenda(), String.valueOf(indice)+"/"+String.valueOf(qtd)));							
							}else{
								gerarBoletos(new ContasReceber(pedido.getCliente(), ndocumento, Real.formatDbToString(String.valueOf(Double.parseDouble(valorParc.toString()) )), new Date(), dataBoleto,"ABERTO", "BOLETO",pedido.getTipoVenda(), String.valueOf(indice)+"/"+String.valueOf(qtd)));
							}
						}else{
							gerarBoletos(new ContasReceber(pedido.getCliente(), ndocumento, Real.formatDbToString(String.valueOf(Double.parseDouble(valorParc.toString()) )), new Date(), dataBoleto,"ABERTO", "BOLETO",pedido.getTipoVenda(), String.valueOf(indice)+"/"+String.valueOf(qtd)));
						}
					}else{
						gerarBoletos(new ContasReceber(pedido.getCliente(), ndocumento, Real.formatDbToString(String.valueOf(Double.parseDouble(valorParc.toString()) )), new Date(), dataBoleto,"ABERTO", "BOLETO",pedido.getTipoVenda(), String.valueOf(indice)+"/"+String.valueOf(qtd)));
					}
					indice++;
				}	
				
				CreditoClienteDAO.setSaldo(pedido.getCliente().getId(), new Double(valor));
				
				em.getTransaction().commit();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
	}
	
	public static boolean reemitirBoleto(Integer codBoleto){
		EntityManager em = ConnUtil.getEntity();
		try{
			
			ContasReceber boleto = em.find(ContasReceber.class, codBoleto);
			if(boleto != null && 
					!boleto.getN_numero_sicred().substring(0, 4).equals("18/3") && 
					!boleto.getN_numero_sicred().substring(0, 4).equals("19/3")){
				
					boleto.setN_numero_sicred_antigo(boleto.getN_numero_sicred());
					boleto.setN_numero_sicred(null);
					boleto.setData_emissao(new Date());
					
					//Reenviar remessa para o banco
					boleto.setRemessa_recebida_banco(null);
					boleto.setRemessaEnviada(null); 
					boleto.setComandoRemessa(null); 
					
					em.getTransaction().begin();
					em.merge(boleto);
					em.getTransaction().commit();
				
				return true ;
			}else{			
				return false ;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean gerarNossoNumero(Integer codBoleto){
		EntityManager em = ConnUtil.getEntity();
		try{
			
			ContasReceber boleto = em.find(ContasReceber.class, codBoleto);
			if(boleto != null){
				if(boleto.getN_numero_sicred() == null || boleto.getN_numero_sicred().isEmpty() || boleto.getN_numero_sicred().equals("")){
					
					Query qControleNovo = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
					qControleNovo.setParameter("nome", boleto.getControle());
					qControleNovo.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
					
					ContaBancaria cb = null;
					if(qControleNovo.getResultList().size() == 1){
						
						ControleTitulo ct = (ControleTitulo)qControleNovo.getSingleResult();
						cb = ct.getConta_bancaria_bkp();
					}
					
					String NumeroNovo = "";
					if(cb != null){
						if(cb.getNome_banco().equals("SICRED")){					
							NumeroNovo = calcularNossoNumeroSicred(boleto.getCliente().getId(),cb);				
						}
						
						if(cb.getNome_banco().equals("BANCO DO BRASIL")){
							//NumeroNovo = calcularNossoNumero(boleto.getCliente().getId());
						}
					}
					
					
					boleto.setN_numero_sicred(NumeroNovo);
					em.getTransaction().begin();
					em.merge(boleto);
					em.getTransaction().commit();
					
					return true;
				}
				
				return false ;
			}
			
			
			return false ;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Cadastra um Boleto na Tabela
	 * 
	 * @param cr
	 * @return
	 * @throws Exception
	 */
	public static ContasReceber gerarBoletos(ContasReceber cr) throws Exception{
		EntityManager em = ConnUtil.getEntity();
		try{
		
			Date dataAtual = new Date();
			boolean definirNDocAuto;
			if(cr.getN_doc().equals(""))
			{
				definirNDocAuto = true;
			}else{
				definirNDocAuto = false;
			}  				
	         
			Query qControleNovo = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
			qControleNovo.setParameter("nome", cr.getControle());
			qControleNovo.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			
			ContaBancaria cb = null;
			if(qControleNovo.getResultList().size() == 1){
				
				ControleTitulo ct = (ControleTitulo)qControleNovo.getSingleResult();
				cb = ct.getConta_bancaria_bkp();
			}
			
			//-- Desativado devido a lentidão
//			String NumeroNovo = "";
//			if(cb != null){
//				if(cb.getNome_banco().equals("SICRED")){					
//					NumeroNovo = calcularNossoNumeroSicred(cr.getCliente().getId(),cb);				
//				}
//				
//				if(cb.getNome_banco().equals("BANCO DO BRASIL")){
//					NumeroNovo = calcularNossoNumero(cr.getCliente().getId());
//				}
//			}
				
	        String ndocumento  = null;
			if(definirNDocAuto)
			{
				ndocumento = String.valueOf(cr.getCliente().getId())+"/"+dataAtual.getYear()+"-"+String.format("%02d", 1)+"/"+String.format("%02d", 1);
				
				cr.setN_doc(ndocumento);
			} 
			
//			if(cb != null){
//				if(cb.getNome_banco().equals("SICRED")){					
//					cr.setN_numero_sicred(NumeroNovo);	
//				}
//				
//				if(cb.getNome_banco().equals("BANCO DO BRASIL")){
//					cr.setN_numero(NumeroNovo);	
//				}
//			}
			
			
			cr.setEmpresa_id(OpusERP4UI.getEmpresa().getId());
						
			em.persist(cr);			
			em.persist(new AlteracoesContasReceber(null, "CADASTRO UM BOLETO", cr,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
			
			return cr;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String calcularNossoNumeroSicred(Integer codCliente,ContaBancaria cb){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			StringBuilder numero = new StringBuilder(); 
			Integer QtdZeros = 5;
			Integer Qtd = 1;
			
			while(true){
				
				numero = new StringBuilder();
				
				SimpleDateFormat sdf = new SimpleDateFormat("YY");
				
				 //-//------//--  Padrão : AA/B XXXXX-D  --//-------------
				//-//------//-----------------------------------------------------------------------------------------------------------------------//--
			   	
				// agencia(AAAA)+posto(PP)+conta(NNNN)+ano(YY)+byte(B)+sequencia(SSSSS)
				
				String caract1 = sdf.format(new Date()).toString()+"/3";
				String caract2 = String.format("%0"+QtdZeros.toString()+"d", Qtd);
				int digito = getMod11(cb.getAgencia_banco()+cb.getPosto_beneficiario()+cb.getCod_beneficiario()+caract1.replace("/", "")+caract2);
				if(digito == 10 || digito == 11){
					digito = 0;
				}
				String caract3 = "-"+digito;
				
				numero.append(caract1);
				numero.append(caract2);
				numero.append(caract3);
				
				Query consultaNNumero = em.createQuery("select cr from ContasReceber cr where cr.n_numero_sicred = :codNossoNumero", ContasReceber.class);
				consultaNNumero.setParameter("codNossoNumero", numero.toString());
				
				Query consultaNNumero2 = em.createQuery("select cr from ContasReceber cr where cr.n_numero_sicred_antigo = :codNossoNumero", ContasReceber.class);
				consultaNNumero2.setParameter("codNossoNumero", numero.toString());
				
				boolean exists = false;
				if(consultaNNumero.getResultList().size() == 0 && consultaNNumero2.getResultList().size() == 0){
					exists = true;
				}else{
					Qtd++;
				}
				
				boolean valid = false;
				if(numero.toString().length() == 11){
					valid = true;
				}else{
					if(numero.toString().length() > 11){
						QtdZeros--;
					}else{
						QtdZeros++;
					}
				}
				
				if(valid && exists){
					break;
				}
					
			}
			
			return numero.toString();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static int getMod11(String num){
	    
	    String[] numeros,parcial;
	    numeros = new String[num.length()+1];
	    parcial = new String[num.length()+1];
	    
	    int peso = 2;
	    
	    for (int i = num.length(); i > 0; i--) {
	    	
	    	if(peso > 9){
	    		peso = 2;
	    	}
	    
	        numeros[i] = num.substring(i-1,i);	    
	        String r1 = String.valueOf(Integer.parseInt(numeros[i]) * peso);
	        
	        parcial[i] = r1;
	       
	        peso++;
	    }
	    
	    int resultado_soma = 0;	    
	    for (int i = parcial.length-1; i > 0; i--) {
			
	    	resultado_soma = resultado_soma + Integer.parseInt(parcial[i]);
		}	    	    
	    int divisao = resultado_soma / 11;	    
	    int digito =11 - (resultado_soma - (Integer.parseInt(String.valueOf(divisao).split(",")[0]) * 11)) ;	    
	    
	    System.out.println("Divisao: "+divisao);	    
	    return digito == 10 && digito == 11 ? 0 : digito;
	}
	
	
	/**
	 * Calcula Nosso Número
	 * 
	 * @param codCliente
	 * @return String
	 */
	private static String calcularNossoNumero(Integer codCliente)
	{
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		String numero = null;		
		Integer QtdZeros = 6;
		Integer Qtd =1;
		while (true) {
			numero = codCliente+String.format("%0"+QtdZeros.toString()+"d", Qtd);
			
			Query consultaNNumero = em.createQuery("select cr from ContasReceber cr where cr.n_numero = :codNossoNumero", ContasReceber.class);
			consultaNNumero.setParameter("codNossoNumero", numero);
			
			boolean exists =false;
			if(consultaNNumero.getResultList().size() == 0){
				exists = true;
			}else{
				Qtd++;//Soma ao Número
			}
			
			boolean valid = false;
			if(numero.length() == 10){				
				valid = true;
			}else{
				if(numero.length() > 10){
					QtdZeros--;//Diminui Zeros
				}else{
					QtdZeros++;
				}
			}
			
			if(valid && exists){
				break;
			}
		}
		return numero;

		}catch(Exception e){
			return null;
		}
			
	}
	
	/**
	 * Calcula ProRata de Boleto para Alteração de Vencimento
	 * 
	 * @param codAcesso
	 * @param novaData
	 * @return String
	 */
	public static String CalcularProRataBoletoAlteracaoVencimento(AcessoCliente ac, Date novaData){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		PlanoAcesso planoAntigo = em.find(PlanoAcesso.class,ac.getPlano().getId());
		
		ContasReceberDAO crDao = new ContasReceberDAO();
		Integer codigoCliente = ac.getCliente().getId();

		ContasReceber boletoAtual = null;
		
		//Verifica se Existem Boletos Em Abertos-Não Vencidos									
		if(ac != null){						
			List<ContasReceber> boletosAbertosNaoVencidos = crDao.buscarTitulosNaoVencidosDeAcessoPorContrato(ac.getId());		

			if(boletosAbertosNaoVencidos != null){
				boletoAtual = boletosAbertosNaoVencidos.get(0);
			}else{
				Notification.show("SEM BOLETOS");
			}
		}
			
			
		
		if(boletoAtual != null){
			//Expressão Regular do Primeiro Boleto
			Pattern pAntigo = Pattern.compile("^"+ac.getId()+"/[0-9]{2}/01");
			Matcher mAntigo = pAntigo.matcher(boletoAtual.getN_doc());
			
			Pattern pNovo = Pattern.compile("^"+ac.getId()+"/[0-9]{2}-01/[0-9]{2}");
		    Matcher mNovo = pNovo.matcher(boletoAtual.getN_doc());
		    
		    Pattern pProRata = Pattern.compile("^"+ac.getId()+"/[0-9]{2}-01/[0-9]{2}");
		    Matcher mProRata = pProRata.matcher(boletoAtual.getN_doc());
		      
		    if (mAntigo.matches() || mNovo.matches() || mProRata.matches()) {			
				
				Date dataProxVencBoleto = boletoAtual.getData_vencimento();
				DateTime dataAnterior = new DateTime(dataProxVencBoleto).minusMonths(1);
				
				Integer qtdDias = Days.daysBetween(dataAnterior, new DateTime(dataProxVencBoleto)).getDays(); //Dias do Mês
				
				Integer qtdDiasUtilizados = 0;				
				if(dataProxVencBoleto == null){
					qtdDiasUtilizados = Days.daysBetween(new DateTime(ac.getData_instalacao()), new DateTime(novaData)).getDays();
				}else{
					qtdDiasUtilizados = Days.daysBetween(new DateTime(dataProxVencBoleto).minusMonths(1), new DateTime(novaData)).getDays();
				}  						
										
				double vlrDoDiaPlanoAntigo = Real.formatStringToDBDouble(planoAntigo.getValor()) / qtdDias; //Valor Diário
				double vlrDosDiasUtilizados = vlrDoDiaPlanoAntigo * qtdDiasUtilizados;
				
				String vlBoleto = Real.formatDbToString(String.valueOf(vlrDosDiasUtilizados));
				
				Integer  qtdDiasTeste = Days.daysBetween(new DateTime(ac.getData_instalacao()), new DateTime(dataProxVencBoleto)).getDays();
				
				System.out.println("O Primeiro Boleto ainda irá Vencer");
				

				return vlBoleto;					
			}else{	
				Date dataProxVencBoleto = boletoAtual.getData_vencimento();
				DateTime dataAnterior = new DateTime(dataProxVencBoleto).minusMonths(1);
				
				Integer qtdDias = Days.daysBetween(dataAnterior, new DateTime(dataProxVencBoleto)).getDays(); //Dias do Mês
				Integer qtdDiasUtilizados = Days.daysBetween(new DateTime(dataAnterior), new DateTime(novaData)).getDays();
				
				double vlrDoDiaPlanoAntigo = Real.formatStringToDBDouble(planoAntigo.getValor()) / qtdDias; //Valor Diário
				double vlrDosDiasUtilizados = vlrDoDiaPlanoAntigo * qtdDiasUtilizados;
				
	
				String vlBoleto = Real.formatDbToString(String.valueOf(vlrDosDiasUtilizados));
				
				
				System.out.println("O Primeiro Boleto já venceu");
				
//				Notification.show("O Primeiro Boleto já Venceu",
//						"Data Prox. Vencimento:"+dataProxVencBoleto.toString()+"\n"+
//						"Data Anterior:"+dataAnterior.toString()+"\n"+
//						"Qtd. Dias Entre as Datas Acima:"+qtdDias.toString()+"\n"+							
//						"Data de Hoje:"+new Date().toString()+"\n"+
//						"Qtd. de Dias Utilizados:"+qtdDiasUtilizados.toString()+"\n"+
//						"Valor do Dia Plano Antigo:"+String.valueOf(vlrDoDiaPlanoAntigo)+"\n"+
//						"Valor Total dos Dias Utilizados:"+String.valueOf(vlrDosDiasUtilizados)+"\n"+							
////						"Qtd. de Dias Restantes:"+qtdDiasRestantes.toString()+"\n"+
////						"Valor do Dia Plano Novo:"+String.valueOf(vlrDoDiaPlanoNovo)+"\n"+
////						"Valor Total dos Dias Restantes:"+String.valueOf(vlrDosDiasRestantes)+"\n"+
//						"Valor Total do Boleto:"+vlBoleto+"\n"
//								, Type.ERROR_MESSAGE);
				
				return vlBoleto;
			}
		}else{
			return "0,00";
		}
		
		}catch(Exception e){
			return null;
		}
	}

	
	/**
	 * Calcula ProRata de Boleto
	 * 
	 * @param codAcesso
	 * @param novaData
	 * @return String
	 */
	public static String CalcularProRataBoleto(AcessoCliente ac, Date novaData){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		PlanoAcesso planoAntigo = em.find(PlanoAcesso.class,ac.getPlano().getId());
		
		ContasReceberDAO crDao = new ContasReceberDAO();
		Integer codigoCliente = ac.getCliente().getId();

		ContasReceber boletoAtual = null;
		
		//Verifica se Existem Boletos Em Abertos-Não Vencidos									
		if(ac != null){						
			List<ContasReceber> boletosAbertosNaoVencidos = crDao.buscarTitulosNaoVencidosDeAcessoPorContrato(ac.getId());		

			if(boletosAbertosNaoVencidos != null){
				boletoAtual = boletosAbertosNaoVencidos.get(0);
			}else{
				Notification.show("SEM BOLETOS");
			}
		}
			
			
		
		if(boletoAtual != null){
			//Expressão Regular do Primeiro Boleto
			Pattern pAntigo = Pattern.compile("^"+ac.getId()+"/[0-9]{2}/01");
			Matcher mAntigo = pAntigo.matcher(boletoAtual.getN_doc());
			
			Pattern pNovo = Pattern.compile("^"+ac.getId()+"/[0-9]{2}-01/[0-9]{2}");
		    Matcher mNovo = pNovo.matcher(boletoAtual.getN_doc());
		    
		    Pattern pProRata = Pattern.compile("^"+ac.getId()+"/[0-9]{2}-01/[0-9]{2}");
		    Matcher mProRata = pProRata.matcher(boletoAtual.getN_doc());
		      
		    if (mAntigo.matches() || mNovo.matches() || mProRata.matches()) {			
				
				Date dataProxVencBoleto = boletoAtual.getData_vencimento();
				DateTime dataAnterior = new DateTime(dataProxVencBoleto).minusMonths(1);
				
				Integer qtdDias = Days.daysBetween(dataAnterior, new DateTime(dataProxVencBoleto)).getDays(); //Dias do Mês
				
				Integer qtdDiasUtilizados = 0;				
				if(ac.getData_renovacao() == null){
					qtdDiasUtilizados = Days.daysBetween(new DateTime(ac.getData_instalacao()), new DateTime(novaData)).getDays();
				}else{
					qtdDiasUtilizados = Days.daysBetween(new DateTime(dataProxVencBoleto).minusMonths(1), new DateTime(novaData)).getDays();
				}  						
										
				double vlrDoDiaPlanoAntigo = Real.formatStringToDBDouble(planoAntigo.getValor()) / qtdDias; //Valor Diário
				double vlrDosDiasUtilizados = vlrDoDiaPlanoAntigo * qtdDiasUtilizados;
				
				String vlBoleto = Real.formatDbToString(String.valueOf(vlrDosDiasUtilizados));
				
				Integer  qtdDiasTeste = Days.daysBetween(new DateTime(ac.getData_instalacao()), new DateTime(dataProxVencBoleto)).getDays();
				
				System.out.println("O Primeiro Boleto ainda irá Vencer");
				

				return vlBoleto;					
			}else{	
				Date dataProxVencBoleto = boletoAtual.getData_vencimento();
				DateTime dataAnterior = new DateTime(dataProxVencBoleto).minusMonths(1);
				
				Integer qtdDias = Days.daysBetween(dataAnterior, new DateTime(dataProxVencBoleto)).getDays(); //Dias do Mês
				Integer qtdDiasUtilizados = Days.daysBetween(new DateTime(dataAnterior), new DateTime(novaData)).getDays();
				
				double vlrDoDiaPlanoAntigo = Real.formatStringToDBDouble(planoAntigo.getValor()) / qtdDias; //Valor Diário
				double vlrDosDiasUtilizados = vlrDoDiaPlanoAntigo * qtdDiasUtilizados;
				
	
				String vlBoleto = Real.formatDbToString(String.valueOf(vlrDosDiasUtilizados));
				
				
				System.out.println("O Primeiro Boleto já venceu");
				
//				Notification.show("O Primeiro Boleto já Venceu",
//						"Data Prox. Vencimento:"+dataProxVencBoleto.toString()+"\n"+
//						"Data Anterior:"+dataAnterior.toString()+"\n"+
//						"Qtd. Dias Entre as Datas Acima:"+qtdDias.toString()+"\n"+							
//						"Data de Hoje:"+new Date().toString()+"\n"+
//						"Qtd. de Dias Utilizados:"+qtdDiasUtilizados.toString()+"\n"+
//						"Valor do Dia Plano Antigo:"+String.valueOf(vlrDoDiaPlanoAntigo)+"\n"+
//						"Valor Total dos Dias Utilizados:"+String.valueOf(vlrDosDiasUtilizados)+"\n"+							
////						"Qtd. de Dias Restantes:"+qtdDiasRestantes.toString()+"\n"+
////						"Valor do Dia Plano Novo:"+String.valueOf(vlrDoDiaPlanoNovo)+"\n"+
////						"Valor Total dos Dias Restantes:"+String.valueOf(vlrDosDiasRestantes)+"\n"+
//						"Valor Total do Boleto:"+vlBoleto+"\n"
//								, Type.ERROR_MESSAGE);
				
				return vlBoleto;
			}
		}else{
			return "0,00";
		}
		
		}catch(Exception e){
			return null;
		}
	}
	
	
	public static Integer CalcularQtdDiasProRata(AcessoCliente ac, Date novaData){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		PlanoAcesso planoAntigo = em.find(PlanoAcesso.class,ac.getPlano().getId());
		
		ContasReceberDAO crDao = new ContasReceberDAO();
		Integer codigoCliente = ac.getCliente().getId();

		ContasReceber boletoAtual = null;
		
		//Verifica se Existem Boletos Em Abertos-Não Vencidos									
		if(ac != null){						
			List<ContasReceber> boletosAbertosNaoVencidos = crDao.buscarTitulosNaoVencidosDeAcessoPorContrato(ac.getId());		

			if(boletosAbertosNaoVencidos != null){
				boletoAtual = boletosAbertosNaoVencidos.get(0);
			}else{
				Notification.show("SEM BOLETOS");
			}
		}
			
		if(boletoAtual != null){
			//Expressão Regular do Primeiro Boleto
			Pattern pAntigo = Pattern.compile("^"+ac.getId()+"/[0-9]{2}/01");
			Matcher mAntigo = pAntigo.matcher(boletoAtual.getN_doc());
			
			Pattern pNovo = Pattern.compile("^"+ac.getId()+"/[0-9]{2}-01/[0-9]{2}");
		    Matcher mNovo = pNovo.matcher(boletoAtual.getN_doc());
		      
		    if (mAntigo.matches() || mNovo.matches()) {			
				
				Date dataProxVencBoleto = boletoAtual.getData_vencimento();
				DateTime dataAnterior = new DateTime(dataProxVencBoleto).minusMonths(1);
				
				Integer qtdDias = Days.daysBetween(dataAnterior, new DateTime(dataProxVencBoleto)).getDays(); //Dias do Mês
				
				Integer qtdDiasUtilizados = 0;				
				if(ac.getData_renovacao() == null){
					qtdDiasUtilizados = Days.daysBetween(new DateTime(ac.getData_instalacao()), new DateTime(novaData)).getDays();
				}else{
					qtdDiasUtilizados = Days.daysBetween(new DateTime(dataProxVencBoleto).minusMonths(1), new DateTime(novaData)).getDays();
				}  						
											
				return qtdDiasUtilizados;					
			}
		    return 0;
		}
		
		return 0;
		
		}catch(Exception e){
			return null;
		}
	}
	
	
	public void AlterarDataBoleto(AcessoCliente ac, Date novaData){
				
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
				List<ContasReceber> resConta = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(ac.getId());		
				ContasReceber ct = resConta.get(0);
								
				List<ContasReceber> result = new ArrayList<>();		
				Date novaDataContrato = CredenciaisAcessoDAO.calcularDataVencContrato(ac.getContrato().getId(), novaData);	
				ac.setData_venc_contrato(novaDataContrato);
				
				ContasReceberDAO cDAO = new ContasReceberDAO();
				String valorProrata = cDAO.CalcularProRataBoleto(ac, novaData);
				
				result.add(new ContasReceber(ct.getId(),ct.getN_doc(),ct.getN_numero(),valorProrata,ct.getValor_pagamento(),ct.getData_emissao(),novaData,ct.getData_pagamento(),ct.getData_baixa(),ct.getData_exclusao(),ct.getForma_pgto(),ct.getTipo_baixa(),ct.getControle(),ct.getCentro_custo(),ct.getStatus(),ct.getDesbloqueado(),ct.getBloquear(),ct.getDesbloquear(),ct.getBloqueado(),ct.getTipo_titulo(),new Cliente(ct.getCliente().getId()),ct.getEmpresa_id()));                        
				
				int i = 0;
				for(ContasReceber cont: resConta){
					
					if(cont.getId() != ct.getId()){
						 	
							i++;
							LocalDate localDate = new LocalDate(novaData);
							LocalDate moreDays = localDate.plusMonths(i);	
							Date dataBoleto = moreDays.toDate();
						
						    result.add(new ContasReceber(cont.getId(),cont.getN_doc(),cont.getN_numero(),cont.getValor_titulo(),cont.getValor_pagamento(),cont.getData_emissao(),dataBoleto,cont.getData_pagamento(),cont.getData_baixa(),cont.getData_exclusao(),cont.getForma_pgto(),cont.getTipo_baixa(),cont.getControle(),cont.getCentro_custo(),cont.getStatus(),cont.getDesbloqueado(),cont.getBloquear(),cont.getDesbloquear(),cont.getBloqueado(),cont.getTipo_titulo(),new Cliente(cont.getCliente().getId()),cont.getEmpresa_id()));                        									
					}	
				}		
				
				em.getTransaction().begin();
				
		        for(ContasReceber cont: result){			
						em.merge(cont);
				}			
				
				em.getTransaction().commit();
		
		}catch(Exception e){
			e.printStackTrace();
		}
				
	}		
			

	/**
	 * Buscar Boletos Abertos de um Determinado Acesso
	 * 
	 * @param codAcesso
	 * @return boolean
	 */
	public boolean getBoletosAcessoAbertosPorContrato(Integer codAcesso){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		String rNovo = "^"+codAcesso+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		String rAntigo = "^"+codAcesso+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		String rProrata = "^"+codAcesso+"/PRORATA";
		
		Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP :rNovo " +
				
				"or "+
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP :rAntigo "+
				
				"or "+
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP :rProrata ",
				 ContasReceber.class);
		qn.setParameter("rNovo", rNovo);
		qn.setParameter("rAntigo", rAntigo);
		qn.setParameter("rProrata", rProrata);
	
		if(qn.getResultList().size() > 0){
			return true;
		}
		return false;
		
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean getBoletosAcessoAbertosClientes(Integer codCliente){
		
		EntityManager em = ConnUtil.getEntity();
		
		
		try{
		boolean allow = false;	
			Query qn = em.createNativeQuery("select * from contas_receber cr where " +
					"cr.clientes_id = :cliente " +
					"and cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}' " +
					
					"or cr.clientes_id = :cliente " +
					"and cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP '^[0-9]{4}/[0-9]{2}/[0-9]{2}' "+
					
					"or cr.clientes_id = :cliente " +
					"and cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP '^[0-9]{4}/PRORATA' ",
					 ContasReceber.class);
			qn.setParameter("cliente", codCliente);		
		
			if(qn.getResultList().size() > 0){
				allow = true;
			}
			return allow;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static boolean verificarBoletoPendentePrePago(Integer codAcesso){
		EntityManager em  = ConnUtil.getEntity();
		
		try{
		String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
		String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
		String regexProrata = "^"+codAcesso.toString()+"/PRORATA";
		
		Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
				"cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP :rNova " +				
				
				"or cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP :rAntiga "+
				
				"or cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP :rProrata ",
				ContasReceber.class);
		
		qn.setParameter("rNova", regexNova);
		qn.setParameter("rAntiga", regexAntiga);
		qn.setParameter("rProrata", regexProrata);
				
		if(qn.getResultList().size() > 0){
			return  true;
		}
			return false;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Verifica se Existem Boletos Vencidos e Abertos de Um Determinando Contrato
	 * 
	 * @param codAcesso
	 * @return boolean
	 */
	public static boolean getBoletosAcessoVencidosPorContrato(Integer codAcesso){
		
		EntityManager  em = ConnUtil.getEntity();
		try{			
			String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
			String regexProrata = "^"+codAcesso.toString()+"/PRORATA";
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
					"cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP :rNova " +
					"and cr.data_vencimento < :dataVencimento "+
					
					"or cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP :rAntiga " +
					"and cr.data_vencimento < :dataVencimento "+
					
					"or cr.status_2 ='ABERTO' " +
					"and cr.n_doc REGEXP :rProrata " +
					"and cr.data_vencimento < :dataVencimento ",
					ContasReceber.class);
			
			qn.setParameter("rNova", regexNova);
			qn.setParameter("rAntiga", regexAntiga);
			qn.setParameter("rProrata", regexProrata);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			qn.setParameter("dataVencimento", sdf.parse(sdf.format(new Date())));
			
			if(qn.getResultList().size() > 0){
				return  true;
			}
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public static boolean getBoletosAcessoAdiantadosPorContrato(Integer codAcesso){
		EntityManager em = ConnUtil.getEntity();
		try{			
			String regexNova = "^"+codAcesso.toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
			String regexAntiga = "^"+codAcesso.toString()+"/[0-9]{2}/[0-9]{2}";
			String regexProrata = "^"+codAcesso.toString()+"/PRORATA";
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
					"cr.status_2 ='FECHADO' " +
					"and cr.n_doc REGEXP :rNova " +
					"and cr.data_vencimento > :dataVencimento "+
					
					"or cr.status_2 ='FECHADO' " +
					"and cr.n_doc REGEXP :rAntiga " +
					"and cr.data_vencimento > :dataVencimento "+
					
					"or cr.status_2 ='FECHADO' " +
					"and cr.n_doc REGEXP :rProrata " +
					"and cr.data_vencimento > :dataVencimento ",
					ContasReceber.class);
			
			qn.setParameter("rNova", regexNova);
			qn.setParameter("rAntiga", regexAntiga);
			qn.setParameter("rProrata", regexProrata);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			qn.setParameter("dataVencimento", sdf.parse(sdf.format(new Date())));
			
			if(qn.getResultList().size() > 0){
				return  true;
			}
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	public boolean getBoletosAcessoVencidosClientes(Integer codCliente){
		EntityManager em = ConnUtil.getEntity();
		
		
		try{
		boolean allow = false;	
		Query qn = em.createNativeQuery("select * from contas_receber cr where " +
				"cr.clientes_id = :cliente " +
				"and cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}' " +
				"and cr.data_vencimento < :dataVencimento "+
				
				"or cr.clientes_id = :cliente " +
				"and cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{4}/[0-9]{2}/[0-9]{2}' "+
				"and cr.data_vencimento < :dataVencimento "+
				
				"or cr.clientes_id = :cliente " +
				"and cr.status_2 ='ABERTO' " +
				"and cr.n_doc REGEXP '^[0-9]{4}/PRORATA "+
				"and cr.data_vencimento < :dataVencimento ",
				ContasReceber.class);
		qn.setParameter("cliente", codCliente);		
		qn.setParameter("dataVencimento", new Date());
		
		if(qn.getResultList().size() > 0){
			allow = true;
		}
			return allow;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	/**
	 * 
	 * Monta um Layout de Impressão de Boleto para Impressoa de Cupom
	 * 	
	 * @param c
	 * @return String
	 */
	public static String getLayoutRecibo(ContasReceber c){
		
		if(c != null){
			StringBuilder s = new StringBuilder();
			
			String formaPg = "";
			
			if(c.getForma_pgto().equals("HAVER")){
				List<HaverBoleto> haverBoleto = HaverBoletoDAO.findAll(c);
				for (HaverBoleto hb : haverBoleto) {
					formaPg = formaPg+" HAVER-"+hb.getHaver().getId().toString();
				}
			}else{
				formaPg = c.getForma_pgto();
			}
			

			char ABRE_CONDENSED1 = 27;
			char ABRE_CONDENSED2 = 15;
			char FECHA_CONDENSED = 18;
			char GUILHOTINA27 = 27;
			char GUILHOTINA_TOTAL = 119;
			
			
			String quebra = "\n";
			s.append(ABRE_CONDENSED2);			
			s.append("             d i g i t a l \n");
			s.append("      Rua Adjar Maciel, 35 Centro\n");
			s.append("    Belo Jardim/PE CEP: 55.150-040\n");
			s.append("       CNPJ: 07.578.965/0001-05 \n");
			s.append("           IE: 18.3.050.0329 \n");
			s.append("       www.digitalonline.com.br \n");
			s.append("          Fone: (81)3726.3125\n");
			
			s.append("\n");
			
			s.append("          RECIBO DE PAGAMENTO \n");
			
			SimpleDateFormat formHora= new SimpleDateFormat("hh:mm:ss");
			String hora = formHora.format(new Date());
			
			SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
			String hoje = form.format(new Date());
			String vencimento = form.format(c.getData_vencimento());
			String pagamento = form.format(c.getData_pagamento());
			
			s.append("---------------------------------------\n");
			s.append("EMISSAO: "+hoje+" HORA: "+hora+ "\n");
					
			s.append(quebra);
			s.append("         INFORMACOES DO BOLETO\n");
			s.append("---------------------------------------\n");			
			s.append("Cliente..: "+c.getCliente().getNome_razao());			
			s.append(quebra);	
			s.append("Cod Boleto: "+c.getId().toString());
			s.append(quebra);
			s.append("N. Doc.: "+c.getN_doc());
			s.append(quebra);
			s.append("N. Numero.: "+c.getN_numero());
			s.append(quebra);
			s.append("Valor.: "+c.getValor_titulo());
			s.append(quebra);
			s.append("Data Venc.: "+vencimento);
			s.append(quebra);
			s.append("Data Pag..: "+pagamento);
			s.append(quebra);
			s.append("Valor Pgto.: "+c.getValor_pagamento());
			s.append(quebra);
			s.append("Forma Pgto.:"+formaPg);
			s.append(quebra);
			s.append("Tipo.: "+c.getTipo_baixa());
			s.append(quebra);
			s.append("---------------------------------------\n");
			s.append(FECHA_CONDENSED);
			s.append(GUILHOTINA27);
			s.append(GUILHOTINA_TOTAL);
			
						
			return s.toString();
		
		}else{
			return "boleto não encontrado!";
		}
	}
	
	
	public static boolean baixarLiquidado(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		boolean baixar = false;
		
		Query q = em.createQuery("select r from RegistroLiquidado r where r.status='A'", RegistroLiquidado.class);
		
		for (RegistroLiquidado registro : (List<RegistroLiquidado>) q.getResultList()) {
						
			Query qBoleto = em.createQuery("select c from ContasReceber c where c.n_numero=:nNumero", ContasReceber.class);
			qBoleto.setParameter("nNumero", registro.getNosso_numero());
			if(qBoleto.getResultList().size() == 1){	
				baixar = true;
				
				registro.setStatus("F");
				em.getTransaction().begin();
				em.merge(registro);
				em.getTransaction().commit();
			
				try{
					ContasReceber cc = (ContasReceber)qBoleto.getSingleResult();	
					String data = registro.getData_pago().substring(0, 2)+"/"+registro.getData_pago().substring(2, 4)+"/"+registro.getData_pago().substring(4, 8);
					SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyy");				
					boolean check = baixarBoleto(cc.getId(), registro.getValor_pago(),registro.getValor_creditado(), registro.getValor_tarifa(), "BANCO", "liquidado", sdf.parse(data), new Date(),Real.formatStringToDBDouble(registro.getValor_pago()), Real.formatStringToDBDouble("0,00"));
					
					AlteracoesContasReceberDAO.save(new AlteracoesContasReceber(null, "BAIXOU ARQUIVO RETORNO",cc,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
					
					if(check){
						CredenciaisAcessoDAO.ConcluiDesbloqueioAcesso(cc.getId());
						CredenciaisAcessoDAO.ConcluiRecargaPrePaga(cc.getId());
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}			
		}
		return baixar;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static boolean baixarLiquidadoSicred(String nomeArquivo){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			boolean baixar = false;
			
			Query q = em.createQuery("select r from RegistroLiquidado r where r.status='A' and r.nome_arquivo=:file", RegistroLiquidado.class);
			q.setParameter("file", nomeArquivo);
			
			for (RegistroLiquidado registro : (List<RegistroLiquidado>) q.getResultList()) {
						
				StringBuilder sbnNumero = new StringBuilder(registro.getNosso_numero());
				sbnNumero.insert(2, "/");
				sbnNumero.insert(9, "-");
				
				Query qBoleto = em.createQuery("select c from ContasReceber c where c.n_numero_sicred=:nNumero", ContasReceber.class);
				qBoleto.setParameter("nNumero", sbnNumero.toString());
				if(qBoleto.getResultList().size() == 1){	
					baixar = true;
					
					registro.setStatus("F");
					em.getTransaction().begin();
					em.merge(registro);
					em.getTransaction().commit();
				
					try{
						ContasReceber cc = (ContasReceber)qBoleto.getSingleResult();	
						String data = registro.getData_pago().substring(0, 2)+"/"+registro.getData_pago().substring(2, 4)+"/"+registro.getData_pago().substring(4, 8);
						SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyy");				
						boolean check = baixarBoleto(cc.getId(), registro.getValor_pago(),registro.getValor_creditado(), registro.getValor_tarifa(), "BANCO", "liquidado", sdf.parse(data), new Date(),Real.formatStringToDBDouble(registro.getValor_pago()), Real.formatStringToDBDouble("0,00"));
						
						AlteracoesContasReceberDAO.save(new AlteracoesContasReceber(null, "BAIXOU ARQUIVO RETORNO",cc,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));
						
						if(check){
							CredenciaisAcessoDAO.ConcluiDesbloqueioAcesso(cc.getId());
							CredenciaisAcessoDAO.ConcluiRecargaPrePaga(cc.getId());
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			return baixar;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static List<RegistroLiquidado> liquidarBoletosPadraoCnab400(File arq){
		EntityManager em = ConnUtil.getEntity();
		
		List<RegistroLiquidado> result = new ArrayList<>();
		
		try{
		
		List<String> lines = FileUtils.readLines(arq, "UTF8");

		if(lines.size() > 0 && lines.get(0).length() == 400){
			
			String HEADER = lines.get(0); 
			List<String> registros = new ArrayList<String>();
			String TRAILLER = lines.get(lines.size()-1);
			
			lines.remove(lines.size()-1);
			lines.remove(0);
			
			for (String string : lines) {
				registros.add(string);
			}			
			
			double vlr_total = 0;
			
			double qtd_entrada = 0;
			double qtd_liquidado = 0;
			double qtd_recusado = 0;
			double qtd_baixado = 0;
			for (String string : registros) {
				
				if(string.substring(108, 110).equals("05")  || string.substring(108, 110).equals("06")  || string.substring(108, 110).equals("07") || string.substring(108, 110).equals("08") ){
				
					qtd_liquidado++;
					
					String vlr = Long.valueOf(string.substring(152, 165)).toString();
					String vlr_formatado = Real.formatDbToString(vlr.substring(0,vlr.length()-2)+"."+vlr.substring(vlr.length()-2, vlr.length()));
					
					String vlr_formatado2 = formataValor(string);
					String vlr_creditado =  formataValorCreditado(string);
					String vlr_tarifa =  formataValorTarifa(string);
					
					double vlr_dou = Real.formatStringToDBDouble(vlr_formatado);				
					vlr_total = vlr_total + vlr_dou;
					
					String comando = "";
					String desc_comando = "";
					
					if(string.substring(108, 110).equals("05")){
						comando = "05";
						desc_comando = "Liquidado sem registro";
					}
					
					if(string.substring(108, 110).equals("06")){
						comando = "06";
						desc_comando = "Liquidação Normal";
					}
					
					if(string.substring(108, 110).equals("07")){
						comando = "07";
						desc_comando = "Liquidação por Conta/Parcial";
					}
					
					if(string.substring(108, 110).equals("08")){
						comando = "08";
						desc_comando = "Liquidação por Saldo";
					}
					
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
					SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyy");
					
					String data = string.substring(110, 116);
					Date data1 = sdf.parse(data);
					data = sdf1.format(data1); 
					
					Query q = em.createQuery("select s from RegistroLiquidado s where s.empresa =:Empresa and s.nosso_numero=:Nnumero", RegistroLiquidado.class);
					q.setParameter("Empresa", OpusERP4UI.getEmpresa().getId().toString());
					q.setParameter("Nnumero", string.substring(70, 80));
		
					em.getTransaction().begin();
					for(int i = 0; i < q.getResultList().size(); i++ ){								
						em.remove(q.getResultList().get(i));
					}							
					em.getTransaction().commit();
					
					em.getTransaction().begin();
					RegistroLiquidado regliquid = new RegistroLiquidado(null, string.substring(70, 80), vlr_formatado2,vlr_creditado, vlr_tarifa,data, "A",comando,desc_comando, OpusERP4UI.getEmpresa().getId().toString(), new Date()); 
					em.persist(regliquid);
					em.getTransaction().commit();	
					result.add(regliquid);
				
				}
				
				if(string.substring(108, 110).equals("02")){
					
				    Query  q = em.createQuery("select c from ContasReceber c where c.n_numero = :nNumero ", ContasReceber.class);
				    q.setParameter("nNumero", string.substring(70, 80));
					
				    if(q.getResultList().size() == 1){
				    	ContasReceber boleto = (ContasReceber)q.getSingleResult();
				    	boleto.setRemessa_recebida_banco("SIM");
				    	
				    	em.getTransaction().begin();
				    	em.merge(boleto);
				    	em.getTransaction().commit();
				    }
				    
					qtd_entrada++;
				}
				
				if(string.substring(108, 110).equals("03")){
					qtd_recusado++;
				}
				
				if(string.substring(108, 110).equals("09")){
					qtd_baixado++;
				}
			}
			
			Notification.show("Arquivo CNAB400 Lido com Sucesso", String.format(	"Qtd.: Entrada: %s, Qtd.: Liquidado: %s,Qtd.: Recusado: %s, Qtd.: Baixado: %s",
					qtd_entrada,qtd_liquidado, qtd_recusado, qtd_baixado), Notification.TYPE_TRAY_NOTIFICATION);
			
		}else{
			if(lines.size() == 0){
				System.out.println("Arquivo Vazio");
			}
			
			if(lines.get(0).length() == 400){
				System.out.println("Arquivo fora do padrão CNAB400");
			}
		}
		
	}catch(Exception e){
		
		e.printStackTrace();
		//System.out.println("Arquivo não compatível com Layout CBR643!");
	}
		
		
		return result;
	}
	
		
	public static List<RegistroLiquidado> liquidarBoletosPadraoCnab400Sicred(File arq, String tipo){
		EntityManager em = ConnUtil.getEntity();
		
		List<RegistroLiquidado> result = new ArrayList<>();
		List<RegistroLiquidado> result_recusados = new ArrayList<>();
		
		try{
		
		List<String> lines = FileUtils.readLines(arq, "UTF8");

		if(lines.size() > 0 && lines.get(0).length() == 400){
			
			String HEADER = lines.get(0); 
			List<String> registros = new ArrayList<String>();
			String TRAILLER = lines.get(lines.size()-1);
			
			lines.remove(lines.size()-1);
			lines.remove(0);
			
			for (String string : lines) {
				registros.add(string);
			}			
	
			double vlr_total = 0;
			
			double qtd_entrada = 0;
			double qtd_liquidado = 0;
			double qtd_recusado = 0;
			double qtd_baixado = 0;
			for (String string : registros) {
				
				if(string.substring(108, 110).equals("06")  || string.substring(108, 110).equals("09")  || string.substring(108, 110).equals("10") || string.substring(108, 110).equals("17") ){
				
					qtd_liquidado++;
					
					String vlr = Long.valueOf(string.substring(152, 165)).toString();
					String vlr_efetiv_pago = Long.valueOf(string.substring(253, 266)).toString();
					String vlr_despesas = !string.substring(175, 188).replaceAll(" ", "").equals("") ? Long.valueOf(string.substring(175, 188)).toString() : "";
					
					
					String vlr_formatado = Real.formatDbToString(vlr.substring(0,vlr.length()-2)+"."+vlr.substring(vlr.length()-2, vlr.length()));
					String vlr_efetiv_pago_formatado = vlr_efetiv_pago.length() > 1 ? Real.formatDbToString(vlr_efetiv_pago.substring(0,vlr_efetiv_pago.length()-2)+"."+vlr_efetiv_pago.substring(vlr_efetiv_pago.length()-2, vlr_efetiv_pago.length())) : "0";
					String vlr_despesas_formatado = "0,00";//vlr_despesas != "" ? Real.formatDbToString(vlr_despesas.substring(0,vlr_despesas.length()-2)+"."+vlr_despesas.substring(vlr_despesas.length()-2, vlr_despesas.length())) : "";
					
					double vlr_tarifa2 = !vlr_despesas_formatado.equals("") ? Real.formatStringToDBDouble(vlr_despesas_formatado) : new Double(0);
					double vlr_efeti_pago = !vlr_efetiv_pago_formatado.equals("") ? Real.formatStringToDBDouble(vlr_efetiv_pago_formatado) : new Double(0);
					double vlr_creditado = vlr_efeti_pago - vlr_tarifa2;
					String vlr_creditado_formatado = Real.formatDbToString(String.valueOf(vlr_creditado));
					
					
					//String vlr_formatado2 = formataValor(string);
					//String vlr_creditado =  formataValorCreditado(string);
					//String vlr_tarifa =  formataValorTarifa(string);
					
					double vlr_dou = Real.formatStringToDBDouble(vlr_efetiv_pago);				
					vlr_total = vlr_total + vlr_dou;
					
					String comando = "";
					String desc_comando = "";
					
					if(string.substring(108, 110).equals("02")){
						comando = "02";
						desc_comando = "Entrada confirmada";
					}
					
					if(string.substring(108, 110).equals("03")){
						comando = "03";
						desc_comando = "Entrada rejeitada";
					}
					
					if(string.substring(108, 110).equals("06")){
						comando = "06";
						desc_comando = "Liquidação normal";
					}
					
					if(string.substring(108, 110).equals("09")){
						comando = "09";
						desc_comando = "Baixado automaticamente via arquivo";
					}
					
					if(string.substring(108, 110).equals("10")){
						comando = "10";
						desc_comando = "Baixado conforme instruções da cooperativa de crédito";
					}
					
					if(string.substring(108, 110).equals("12")){
						comando = "12";
						desc_comando = "Abatimento concedido";
					}
					
					if(string.substring(108, 110).equals("13")){
						comando = "13";
						desc_comando = "Abatimento cancelado";
					}
					
					if(string.substring(108, 110).equals("14")){
						comando = "14";
						desc_comando = "Vencimento alterado";
					}
					
					if(string.substring(108, 110).equals("15")){
						comando = "15";
						desc_comando = "Liquidação em cartório";
					}
					
					if(string.substring(108, 110).equals("17")){
						comando = "17";
						desc_comando = "Liquidação após baixa";
					}
					
					if(string.substring(108, 110).equals("19")){
						comando = "19";
						desc_comando = "Confirmação de recebimento de instrução de protesto";
					}
					
					if(string.substring(108, 110).equals("20")){
						comando = "20";
						desc_comando = "Confirmação de recebimento de instrução de sustação de protesto";
					}
					
					
					if(string.substring(108, 110).equals("23")){
						comando = "23";
						desc_comando = "Entrada de título em cartório";
					}
					
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
					SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyy");
					
					String data = string.substring(110, 116);
					Date data1 = sdf.parse(data);
					data = sdf1.format(data1); 
					
					if(comando.equals("06") || comando.equals("17")){
						Query q = em.createQuery("select s from RegistroLiquidado s where s.empresa =:Empresa and s.nosso_numero=:Nnumero", RegistroLiquidado.class);
						q.setParameter("Empresa", OpusERP4UI.getEmpresa().getId().toString());
						q.setParameter("Nnumero", string.substring(47, 62));
			
						em.getTransaction().begin();
						for(int i = 0; i < q.getResultList().size(); i++ ){								
							em.remove(q.getResultList().get(i));
						}							
						em.getTransaction().commit();
						
						em.getTransaction().begin();
						RegistroLiquidado regliquid = new RegistroLiquidado(null, string.substring(47, 62), vlr_efetiv_pago_formatado,vlr_creditado_formatado, vlr_despesas_formatado,data, "A",comando,desc_comando, OpusERP4UI.getEmpresa().getId().toString(), new Date(), arq.getName()); 
						em.persist(regliquid);
						em.getTransaction().commit();	
						result.add(regliquid);
					
					}
				
				}
				
				if(string.substring(108, 110).equals("02")){
					
					StringBuilder sbnNumero = new StringBuilder(string.substring(47, 62));
					sbnNumero.insert(2, "/");
					sbnNumero.insert(9, "-");
					
				    Query  q = em.createQuery("select c from ContasReceber c where c.n_numero_sicred = :nNumero ", ContasReceber.class);
				    q.setParameter("nNumero", sbnNumero.toString());
					
				    if(q.getResultList().size() == 1){
				    	ContasReceber boleto = (ContasReceber)q.getSingleResult();
				    	boleto.setRemessa_recebida_banco("SIM");
				    	
				    	em.getTransaction().begin();
				    	em.merge(boleto);
				    	em.getTransaction().commit();
				    }
				    
					qtd_entrada++;
				}
				
				if(string.substring(108, 110).equals("03")){
					
					StringBuilder sbnNumero = new StringBuilder(string.substring(47, 62));
					
					sbnNumero.insert(2, "/");
					sbnNumero.insert(9, "-");
					
				    Query  q = em.createQuery("select c from ContasReceber c where c.n_numero_sicred = :nNumero ", ContasReceber.class);
				    q.setParameter("nNumero", sbnNumero.toString());
					
				    if(q.getResultList().size() == 1){
				    
				    	ContasReceber boleto = (ContasReceber)q.getSingleResult();
				    	boleto.setRemessa_recebida_banco(null);
				    	boleto.setRemessaEnviada(null); 
				    	boleto.setComandoRemessa(null); 
				    					    	
				    	em.getTransaction().begin();
				    	
						RegistroLiquidado regliquid = new RegistroLiquidado(null, string.substring(47, 62), null,null, null,null, "A","03",string.substring(318, 327), OpusERP4UI.getEmpresa().getId().toString(), new Date(),arq.getName()); 
						em.persist(regliquid);
						
						result_recusados.add(regliquid);
						
				    	em.merge(boleto);
				    	em.getTransaction().commit();
				    }
					
					
					qtd_recusado++;
				}
				
				//if(string.substring(108, 110).equals("09")){
				//	qtd_baixado++;
				//}
				
				
				
			}
			
			em.getTransaction().begin();
			for (RegistroLiquidado rl: result) {
				
				rl.setQtdEntrada(String.valueOf(qtd_entrada));
				rl.setQtdRecusado(String.valueOf(qtd_recusado));
				rl.setQtdLiquidado(String.valueOf(qtd_liquidado));
				
				em.merge(rl);
			}
			em.getTransaction().commit();
			
			Notification.show("Arquivo CNAB400 Lido com Sucesso", String.format(
					"Qtd.: Entrada: %s, Qtd.: Liquidado: %s,Qtd.: Recusado: %s, Qtd.: Baixado: %s"
					,qtd_entrada,qtd_liquidado	, qtd_recusado, qtd_baixado), Notification.TYPE_TRAY_NOTIFICATION);
			
			
		}else{
			if(lines.size() == 0){
				System.out.println("Arquivo Vazio");
			}
			
			if(lines.get(0).length() == 400){
				System.out.println("Arquivo fora do padrão CNAB400");
			}
		}
		
	}catch(Exception e){
		
		e.printStackTrace();
		//System.out.println("Arquivo não compatível com Layout CBR643!");
	}
		
		if(tipo.equals("liquidados")){
		return result;
		}
		
		if(tipo.equals("recusados")){
			return result_recusados;
		}
		
		return null;
	}
	
	public static String formataValor(String string){
		String vlr2 = Long.valueOf(string.substring(253, 266)).toString();
		String vlr_formatado2 = Real.formatDbToString(vlr2.substring(0,vlr2.length()-2)+"."+vlr2.substring(vlr2.length()-2, vlr2.length()));
			
		return vlr_formatado2;
	}
	
	public static String formataValorCreditado(String string){
				
		String vlr2 = Long.valueOf(string.substring(305,318)).toString();
		String vlr_formatado2 = Real.formatDbToString(vlr2.substring(0,vlr2.length()-2)+"."+vlr2.substring(vlr2.length()-2, vlr2.length()));
		
		return vlr_formatado2;
	}
	
	public static String formataValorTarifa(String string){
						
		String vlr2 = Long.valueOf(string.substring(181, 188)).toString();
		String vlr_formatado2 = Real.formatDbToString(vlr2.substring(0,vlr2.length()-2)+"."+vlr2.substring(vlr2.length()-2, vlr2.length()));
		
		return vlr_formatado2;
	}
	
	
	
	public static List<RegistroLiquidado> liquidarBoletosPadraoAntigo(File arq){
		EntityManager em = ConnUtil.getEntity();

		List<RegistroLiquidado> result = new ArrayList<>();
		boolean liquid = false;
		try{

			FileReader fileReader = new FileReader(arq);
			BufferedReader buff = new BufferedReader(fileReader);
			
			List<String> nNumeros = new ArrayList<>();
			List<String> nNumeros2 = new ArrayList<>();
			List<String> valores = new ArrayList<>();
			List<String> datas = new ArrayList<>();
			
			Integer qtdBoletos = 0;
			while (buff.ready()) {
				
				String registroAtual = buff.readLine();				
				registroAtual = registroAtual.replaceAll(" ", "");		

				if(registroAtual.substring(13, 14).equals("T") || registroAtual.substring(13, 14).equals("U")){

					//System.out.println("SEGMENTO:"+registroAtual.substring(13, 14));
				
					if(registroAtual.substring(13, 14).equals("T")){
						String nNumero = registroAtual.substring(42, 52); 
						//System.out.println(nNumero);
						
						Query q = em.createQuery("select s from RegistroLiquidado s where s.empresa =:Empresa and s.nosso_numero=:Nnumero", RegistroLiquidado.class);
						q.setParameter("Empresa", OpusERP4UI.getEmpresa().getId().toString());
						q.setParameter("Nnumero", nNumero);
						
				//		if(q.getResultList().size() == 1){
				//			result.add((RegistroLiquidado)q.getSingleResult());	
					//	}else if(q.getResultList().size() > 1){
							
							em.getTransaction().begin();
							for(int i = 0; i < q.getResultList().size(); i++ ){								
								em.remove(q.getResultList().get(i));
							}							
							em.getTransaction().commit();
							
					//	}else if(q.getResultList().size() == 0){
							nNumeros.add(nNumero);	
					//	}else{
					//		result.add((RegistroLiquidado)q.getSingleResult());							
					//	}
						
						
						
						qtdBoletos++;
					}
					
					if(registroAtual.substring(13, 14).equals("U")){
						
						Integer valor;
						
						if(registroAtual.substring(83, 91).substring(7).equals("0")){
							valor = Integer.parseInt(registroAtual.substring(83, 90));
							valores.add(Real.formatDbToString(valor.toString().substring(0, valor.toString().length()-1)+"."+valor.toString().substring(valor.toString().length()-1, valor.toString().length())+"0"));
							//System.out.println("VALOR C/ FORMATACAO: "+Real.formatDbToString(valor.toString().substring(0, valor.toString().length()-1)+"."+valor.toString().substring(valor.toString().length()-1, valor.toString().length())+"0"));					
							//System.out.println("VALOR S/ FORMATACAO: "+valor);
						}else{
							valor = Integer.parseInt(registroAtual.substring(83, 91));
							valores.add(Real.formatDbToString(valor.toString().substring(0, valor.toString().length()-2)+"."+valor.toString().substring(valor.toString().length()-2, valor.toString().length())));
							//System.out.println("VALOR C/ FORMATACAO: "+Real.formatDbToString(valor.toString().substring(0, valor.toString().length()-2)+"."+valor.toString().substring(valor.toString().length()-2, valor.toString().length())));					
							//System.out.println("VALOR S/ FORMATACAO: "+valor);
						}
							
						
						String dataPagamento = registroAtual.substring(136, 144);
						System.out.println(dataPagamento);
						
						datas.add(dataPagamento);
					}
			
				}
				
			}
	
			for (int i = 0; i < nNumeros.size(); i++) {
				
				em.getTransaction().begin();
				RegistroLiquidado regliquid = new RegistroLiquidado(null, nNumeros.get(i), valores.get(i), datas.get(i), "A", OpusERP4UI.getEmpresa().getId().toString(), new Date()); 
				em.persist(regliquid);
				em.getTransaction().commit();	
				result.add(regliquid);
			}

			
			
			
			return result;
			
//			baixarLiquidado();
			
//			String data = datas.get(0).substring(0, 2)+"/"+datas.get(0).substring(2, 4)+"/"+datas.get(0).substring(4, 8);
//			Notification.show(qtdBoletos.toString()+" Boletos Liquidados em: "+data,  Type.ERROR_MESSAGE);
			
//			Window winBoletos = new Window("Confirmar Baixa");
//			winBoletos.addCloseListener(new Window.CloseListener() {
//				
//				@Override
//				public void windowClose(CloseEvent e) {
//					
//				}
//			});
//			
//			winBoletos.getUI().addWindow(winBoletos);
					
		}catch(Exception e){
			e.printStackTrace();
			return result;
		}
		
		
	}
	
	
	
	public static List<ContasReceber> procurarBoletosOse(Ose ose)
	{
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select cr from ContasReceber cr where cr.n_doc LIKE:codOse and cr.controle = 'SERVICO' and cr.status != 'EXCLUIDO'", ContasReceber.class);
		if(ose.getContrato()!=null){
			//6685-OS29771-1/1
			q.setParameter("codOse", ose.getContrato().getId()+"-"+"OS"+ose.getId().toString()+"%");			
		}else{
			q.setParameter("codOse", "OS"+ose.getId().toString());						
		}
		
		if(q.getResultList().size() > 0){
			return q.getResultList();
		}else{
			return null;
		}
		}catch(Exception e){
			return null;
		}
	}
	

	
	
	public List<ContasReceber> procurarBoletosOsi(Integer codOsi)
	{
		
		EntityManager em = ConnUtil.getEntity();
				
		try{
			Query q = em.createQuery("select cr from ContasReceber cr where cr.n_doc =:codOsi and cr.controle = 'ASSISTENCIA'", ContasReceber.class);
			q.setParameter("codOsi", "OS"+codOsi.toString());
			
			if(q.getResultList().size() > 0){
				return q.getResultList();
			}else{
				return null;
			}
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public ContasReceber procurarBoletosNdoc(String nNumero)
	{
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select cr from ContasReceber cr where cr.n_numero =:Nnumero", ContasReceber.class);
			q.setParameter("Nnumero",nNumero);
			
			if(q.getResultList().size() > 0){
				return (ContasReceber) q.getSingleResult();
			}else{
				return null;
			}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public boolean allowNewContrato(String codCliente){
		EntityManager em = ConnUtil.getEntity();
		
		try {		
			boolean allow = true;

			Query qn = em.createNativeQuery("SELECT cr.ID, cr.DATA_VENCIMENTO, cr.STATUS_2, cr.CLIENTES_ID FROM contas_receber cr, "
					+ "clientes c WHERE c.ID = cr.CLIENTES_ID AND c.DOC_CPF_CNPJ LIKE :cliente AND cr.STATUS_2 != 'FECHADO' AND "
					+ "cr.STATUS_2 != 'EXCLUIDO' AND cr.DATA_VENCIMENTO <:dataVencimento");
						
			qn.setParameter("cliente", codCliente);		
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			qn.setParameter("dataVencimento", sdf.parse(sdf.format(new Date())));
			if(qn.getResultList().size() > 0){
				allow = false;
			}
			return allow;
		
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	
	}
	
	public ContasReceber procurarBoletosNdocSicred(String nNumero)
	{
		EntityManager em = ConnUtil.getEntity();
		
		
		//17/200003-0
		StringBuilder sbnNumero = new StringBuilder(nNumero);
		sbnNumero.insert(2, "/");
		sbnNumero.insert(9, "-");
		
		try{
			Query q = em.createQuery("select cr from ContasReceber cr where cr.n_numero_sicred =:Nnumero", ContasReceber.class);
			q.setParameter("Nnumero",sbnNumero.toString());
			
			if(q.getResultList().size() > 0){
				return (ContasReceber) q.getSingleResult();
			}else{
				return null;
			}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public static void gerarArquivoRemessa(){
		EntityManager em =ConnUtil.getEntity();
		
		try{
			String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();				
			File layoutTemporario = new File(basepath +"/WEB-INF/layouts/SICREDIRemessa400.txg.xml"); 	        
	        FlatFile<Record> ff = Texgit.createFlatFile(layoutTemporario);  
	       
	        
	        ContaBancaria cb = getContaBancaria();
	       
	        SimpleDateFormat formatMes = new SimpleDateFormat("M");
	        SimpleDateFormat formatDia = new SimpleDateFormat("dd");
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat format2 = new SimpleDateFormat("ddMMyy");
			
			Query qRemessa = em.createQuery("select r from RemessaBanco r order by id desc", RemessaBanco.class);
			List<RemessaBanco> listArqRemess = qRemessa.getResultList();
			
			String numeroRemessa;
			
			if(listArqRemess != null && listArqRemess.size() > 0){
				numeroRemessa =String.valueOf(listArqRemess.get(0).getId()+1);
			}else{
				numeroRemessa = "1";
			}
			
			
			em.getTransaction().begin();
			em.persist(new RemessaBanco(null, String.valueOf(Integer.parseInt(numeroRemessa)+1), new Date()));
			em.getTransaction().commit();
	        
			if(cb != null){
		        Record header = ff.createRecord("HeaderArquivo");	        
		        header.setValue("CodBeneficiario", cb.getCod_beneficiario());
		        header.setValue("CpfCnpjBeneficiario", OpusERP4UI.getEmpresa().getCnpj());
		        header.setValue("DataGravacao",format.format(new Date()));
		        header.setValue("VersaoSistema","2.00");	
		        header.setValue("SequencialRemessa", numeroRemessa);
		        ff.addRecord(header);
		        
		        Date today = new Date();  

		        Calendar calendar = Calendar.getInstance();  
		        calendar.setTime(today);  

		        calendar.add(Calendar.MONTH, 1);  
		        calendar.set(Calendar.DAY_OF_MONTH, 1);  
		        calendar.add(Calendar.DATE, -1);  
		        Date lastDayOfMonth = calendar.getTime();
		        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		
	        	       
		       Query q= em.createNativeQuery("SELECT  * FROM `contas_receber` c WHERE "
		        		+ "c.`STATUS_2` = 'ABERTO' AND c.`EMPRESA_ID` = 1 AND c.`REMESSA_ENVIADA` IS NULL and  c.`REMESSA_RECEBIDA_BANCO` IS NULL AND c.`N_NUMERO_SICRED` IS NOT NULL "
		        		+ "AND c.`DATA_VENCIMENTO` >= '"+sdf.format(new Date())+"' "
		        		, ContasReceber.class);
		       
		     
		        List<ContasReceber> lista_boletos = q.getResultList();
		        
		        if(lista_boletos.size()>0)
		        {

		        Integer registro = 1;
		        
		        
		        
		        em.getTransaction().begin();
		        for (ContasReceber boleto : lista_boletos) {
		        	
		        		if(boleto.getN_numero_sicred() != null){
		        	
		        		ControleTitulo controle = getControle(boleto.getControle());

		        		if(controle != null && controle.getRegistro() != null && controle.getRegistro().equals("SIM")){
		        			
		        			if(boleto.getN_doc() != null && CheckNdocUtil.checkNdocAcesso(boleto.getN_doc())){

		        				AcessoCliente contrato = null;
		    					if(CheckNdocUtil.checkNdocAcesso(boleto.getN_doc())){
		    						String[] codigos = boleto.getN_doc().split("/");
		    						
		    						if(codigos[0] != null){
		    							try{
		    								contrato = ContratosAcessoDAO.find(Integer.parseInt(codigos[0].toString()));
		    							}catch(Exception e){
		    								e.printStackTrace();		    								
		    							}
		    						}
		    					}
		        				
		    					boolean gerar = false;
		    					if(contrato != null && contrato.getEmitir_nfe_automatico() != null && contrato.getEmitir_nfe_automatico().equals("SIM")){
		    					
				        			
		    						Record detalheArquivo = ff.createRecord("DetalheArquivo"); 
				        			detalheArquivo.setValue("NossoNumero",boleto.getN_numero_sicred().replace("/", "").replace("-", ""));				        			
				        			detalheArquivo.setValue("TipoJuros","A");
				        			detalheArquivo.setValue("PercentMultaPgtoAtraso","0200");
				        			detalheArquivo.setValue("ValorPercJurosDiaAtraso","0000000000033");
				        			
				        			detalheArquivo.setValue("DataInstrucao",format.format(new Date()));
				        			detalheArquivo.setValue("PostagemTitulo", "N");
				        			detalheArquivo.setValue("EmissaoBoleto", "B");				        							        							        			
				        			detalheArquivo.setValue("Instrucao", "01");
				        			detalheArquivo.setValue("DataVencimento", format2.format(boleto.getData_vencimento()));
				        			detalheArquivo.setValue("ValorTitulo", Real.formatStringToDB(boleto.getValor_titulo()).replace(".", ""));
				        			detalheArquivo.setValue("EspecieDoc", "A");
				        			detalheArquivo.setValue("AceiteTitulo", "S");
				        			detalheArquivo.setValue("DataEmissao", format2.format(boleto.getData_emissao()));
				        			detalheArquivo.setValue("InstrucaoProtestoAutomatico", "00");
				        			detalheArquivo.setValue("NumeroDiasProtestoAutomatico", "00");
				        			detalheArquivo.setValue("SeuNumero", boleto.getId().toString());
				        			detalheArquivo.setValue("DataLimiteConcDesconto", format2.format(boleto.getData_vencimento()));
				        			detalheArquivo.setValue("TipoPessoaPagador",boleto.getCliente().getDoc_cpf_cnpj().length() == 11 ? 1 : 2);
				        			detalheArquivo.setValue("CPFCNPJPagador", boleto.getCliente().getDoc_cpf_cnpj());
				        			detalheArquivo.setValue("NomePagador",boleto.getCliente().getNome_razao().length() > 40 ? boleto.getCliente().getNome_razao().substring(0, 40) : boleto.getCliente().getNome_razao());
				        			detalheArquivo.setValue("EnderecoPagador",boleto.getCliente().getEndereco_principal().getEndereco().length() > 40 ? boleto.getCliente().getEndereco_principal().getEndereco().substring(0, 40) : boleto.getCliente().getEndereco_principal().getEndereco());
				        			//detalheArquivo.setValue("CodPagador", boleto.getCliente().getId());
				        			detalheArquivo.setValue("CepPagador", "55150040");
				        			detalheArquivo.setValue("NumeroSequencialRegistro", registro+1);
				        			
				        			registro++;
				        			
				        			ff.addRecord(detalheArquivo);
				        			
				        			boleto.setRemessaEnviada("SIM");
				        			em.merge(boleto);
		    					}
		        			}else{
		        				Record detalheArquivo = ff.createRecord("DetalheArquivo"); 
			        			detalheArquivo.setValue("NossoNumero",boleto.getN_numero_sicred().replace("/", "").replace("-", ""));				        			
			        			detalheArquivo.setValue("TipoJuros","A");
			        			detalheArquivo.setValue("PercentMultaPgtoAtraso","0200");
			        			detalheArquivo.setValue("ValorPercJurosDiaAtraso","0000000000033");
			        			
			        			detalheArquivo.setValue("DataInstrucao",format.format(new Date()));
			        			detalheArquivo.setValue("PostagemTitulo", "N");
			        			detalheArquivo.setValue("EmissaoBoleto", "B");				        							        							        			
			        			detalheArquivo.setValue("Instrucao", "01");
			        			detalheArquivo.setValue("DataVencimento", format2.format(boleto.getData_vencimento()));
			        			detalheArquivo.setValue("ValorTitulo", Real.formatStringToDB(boleto.getValor_titulo()).replace(".", ""));
			        			detalheArquivo.setValue("EspecieDoc", "A");
			        			detalheArquivo.setValue("AceiteTitulo", "S");
			        			detalheArquivo.setValue("DataEmissao", format2.format(boleto.getData_emissao()));
			        			detalheArquivo.setValue("InstrucaoProtestoAutomatico", "00");
			        			detalheArquivo.setValue("NumeroDiasProtestoAutomatico", "00");
			        			detalheArquivo.setValue("SeuNumero", boleto.getId().toString());
			        			detalheArquivo.setValue("DataLimiteConcDesconto", format2.format(boleto.getData_vencimento()));
			        			detalheArquivo.setValue("TipoPessoaPagador",boleto.getCliente().getDoc_cpf_cnpj().length() == 11 ? 1 : 2);
			        			detalheArquivo.setValue("CPFCNPJPagador", boleto.getCliente().getDoc_cpf_cnpj());
			        			detalheArquivo.setValue("NomePagador",boleto.getCliente().getNome_razao().length() > 40 ? boleto.getCliente().getNome_razao().substring(0, 40) : boleto.getCliente().getNome_razao());
			        			detalheArquivo.setValue("EnderecoPagador",boleto.getCliente().getEndereco_principal().getEndereco().length() > 40 ? boleto.getCliente().getEndereco_principal().getEndereco().substring(0, 40) : boleto.getCliente().getEndereco_principal().getEndereco());
			        			//detalheArquivo.setValue("CodPagador", boleto.getCliente().getId());
			        			detalheArquivo.setValue("CepPagador", "55150040");
			        			detalheArquivo.setValue("NumeroSequencialRegistro", registro+1);
			        			
			        			registro++;
			        			
			        			ff.addRecord(detalheArquivo);
			        			
			        			boleto.setRemessaEnviada("SIM");
			        			em.merge(boleto);
		        			}
		        		}
		        		
		        		}
				        	
		       }
		        		
		        		
				
		        
		        Record TrailerArquivo = ff.createRecord("TrailerArquivo");        
		        TrailerArquivo.setValue("CodBeneficiario",cb.getCod_beneficiario());
		        TrailerArquivo.setValue("SequencialRegistro",registro+1);	      
		        ff.addRecord(TrailerArquivo);
	        
		        String mes = "";
		        if(Integer.parseInt(formatMes.format(new Date())) > 9){
		        	if(formatMes.format(new Date()).equals("10")){
		        		mes = "O";
		        	}
		        	
		        	if(formatMes.format(new Date()).equals("11")){
		        		mes = "N";
		        	}
		        	
		        	if(formatMes.format(new Date()).equals("12")){
		        		mes = "D";
		        	}
		        }else{
		        	mes = formatMes.format(new Date());
		        }
		        
		        String nomeArquivo  = cb.getCod_beneficiario()+mes+formatDia.format(new Date())+".CRM";     	
		        final File zipFile = new File(basepath + "/WEB-INF/uploads/"+nomeArquivo);
	        
		        //OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(zipFile), "ASCII");		        
		        //FileOutputStream fos = new FileOutputStream(zipFile);		       
		        //BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(zipFile), StandardCharsets.US_ASCII));
		        
		        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(zipFile), "ASCII"));
		        
			
				for (String line : ff.write()) {						
					br.write(line);
					br.write("\r\n");					
					//br.newLine();
				}
				br.close(); 
	        
				StreamSource ssZip = new StreamSource() {
		            InputStream is = new ByteArrayInputStream(readImageOldWay(zipFile)); 
		            @Override
		            public InputStream getStream() {
		                return is;
		            }
		        };
	        
		        StreamResource srZip = new StreamResource(ssZip, zipFile.getName());	        
		        srZip.setMIMEType("application/unknow");
	        
		        Page.getCurrent().open(srZip, "_blank", true);
	        
		        em.getTransaction().commit();
		        Notify.Show("Arquivo Remessa gerado com Sucesso!", Notify.TYPE_NOTICE);
		        }else{		        	
		        	Notify.Show("Nenhum boleto encontrado!", Notify.TYPE_NOTICE);
		        	
		        }
		        
			}
		        
			
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			e.printStackTrace();
		}
        
	}
	
//	public static void gerarArquivoRemessa(){
//		EntityManager em =ConnUtil.getEntity();
//		
//		try{
//			String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();				
//			File layoutTemporario = new File(basepath +"/WEB-INF/layouts/BBRemessa400.txg.xml"); 	        
//	        FlatFile<Record> ff = Texgit.createFlatFile(layoutTemporario);  
//	        
//	        ContaBancaria cb = getContaBancaria();
//	       
//			SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
//	        
//			if(cb != null){
//		        Record header = ff.createRecord("HeaderArquivo");	        
//		        header.setValue("Agencia", cb.getAgencia_banco().replace("0", ""));
//		        header.setValue("AgenciaDV", 8);
//		        header.setValue("ContaCorrente",cb.getN_conta());
//		        header.setValue("ContaCorrenteDV", 8);
//		        header.setValue("NomeCedente",cb.getCedente().length() > 30 ? cb.getCedente().toUpperCase().substring(0, 30) : cb.getCedente().toUpperCase());
//		        header.setValue("DataGravacao", format.format(new Date()));
//		        header.setValue("SequencialRemessa", "1");
//		        header.setValue("NumeroConvenioLider",cb.getConvenio());	        
//		        header.setValue("SequencialRegistro", "1");
//		        ff.addRecord(header);
//		        
//		        
//		        Date today = new Date();  
//
//		        Calendar calendar = Calendar.getInstance();  
//		        calendar.setTime(today);  
//
//		        calendar.add(Calendar.MONTH, 1);  
//		        calendar.set(Calendar.DAY_OF_MONTH, 1);  
//		        calendar.add(Calendar.DATE, -1);  
//		        Date lastDayOfMonth = calendar.getTime();
//		        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
//		
//	        	       
//		       Query q= em.createNativeQuery("SELECT  * FROM `contas_receber` c, `acesso_cliente` a WHERE c.`N_DOC` REGEXP '[0-9]/[0-9]{2}-[0-9]{2}/[0-9]{2}' "
//		        		+ "AND a.`ID` = SUBSTRING_INDEX(c.`N_DOC`,'/',1) AND a.`EMITIR_NFE_AUTOMATICO` = 'SIM'  AND c.`STATUS_2` = 'ABERTO' "
//		        		+ "AND c.`EMPRESA_ID` = 1 AND c.`REMESSA_ENVIADA` IS NULL AND c.`DATA_VENCIMENTO` >= '"+sdf.format(new Date())+"' "
//		        		, ContasReceber.class);
//		        
//		        
//		       // Query q = em.createNativeQuery("SELECT * FROM `contas_receber` WHERE `STATUS_2` LIKE 'ABERTO' AND `REMESSA_ENVIADA` LIKE 'SIM' AND `REMESSA_RECEBIDA_BANCO` LIKE 'SIM'", ContasReceber.class);
//		        
//		        List<ContasReceber> lista_boletos = q.getResultList();
//		        
//		        if(lista_boletos.size()>0)
//		        {
//
//		        Integer registro = 1;
//		        
//		        
//		        
//		        em.getTransaction().begin();
//		        for (ContasReceber boleto : lista_boletos) {
//		        	
//		        		ControleTitulo controle = getControle(boleto.getControle());
//
//		        		if(controle != null && controle.getRegistro() != null && controle.getRegistro().equals("SIM")){
//		        			
//		        			if(boleto.getN_doc() != null && CheckNdocUtil.checkNdocAcesso(boleto.getN_doc())){
//
//		        				AcessoCliente contrato = null;
//		    					if(CheckNdocUtil.checkNdocAcesso(boleto.getN_doc())){
//		    						String[] codigos = boleto.getN_doc().split("/");
//		    						
//		    						if(codigos[0] != null){
//		    							try{
//		    								contrato = ContratosAcessoDAO.find(Integer.parseInt(codigos[0].toString()));
//		    							}catch(Exception e){
//		    								
//		    							}
//		    						}
//		    					}
//		        				
//		    					
//		    					if(contrato != null && contrato.getEmitir_nfe_automatico() != null && contrato.getEmitir_nfe_automatico().equals("SIM")){
//				        			
//		    						Record detalheArquivo = ff.createRecord("DetalheArquivo"); 
//				        			detalheArquivo.setValue("TipoInscCedente",02);
//				        			detalheArquivo.setValue("CpfCnpjCedente", "07578965000105");
//				        			detalheArquivo.setValue("Agencia","0721");
//				        			detalheArquivo.setValue("AgenciaDV", "8");
//				        			detalheArquivo.setValue("ContaCorrente", "18751");
//				        			detalheArquivo.setValue("ContaCorrenteDV","8");
//				        			detalheArquivo.setValue("Convenio", "2827198");
//				        			detalheArquivo.setValue("CodControleEmpresa", boleto.getId().toString());
//				        			detalheArquivo.setValue("NossoNumero", "2827198"+boleto.getN_numero());
//				        			detalheArquivo.setValue("VariacaoCarteira", "019");
//				        			detalheArquivo.setValue("CarteiraCobranca", "17");
//				        			detalheArquivo.setValue("Comando",boleto.getComandoRemessa() == null ?  "01" : boleto.getComandoRemessa());
//				        			detalheArquivo.setValue("NumeroTitulo", boleto.getId().toString());
//				        			if(boleto.getData_vencimento() != null){
//				        				detalheArquivo.setValue("DataVencimento", format.format(boleto.getData_vencimento()));
//				        			}
//				        			detalheArquivo.setValue("ValorTitulo",boleto.getValor_titulo().replace(".", "").replace(",", ""));
//				        			detalheArquivo.setValue("EspecieTitulo", "12");
//				        			detalheArquivo.setValue("AceiteTitulo", "N");
//				        			detalheArquivo.setValue("DataEmissao", format.format(boleto.getData_emissao()) );
//				        			detalheArquivo.setValue("JurosMoraDiaAtraso", cb.getJuros().replace(".", "") );	
//				        			detalheArquivo.setValue("DataLimiteDesconto", "000000" );
//				        			detalheArquivo.setValue("ValorDesconto", "0000000000000" );
//				        			detalheArquivo.setValue("InstrucaoCodificada1", "07"); 
//				        			detalheArquivo.setValue("TipoInscSacado",boleto.getCliente().getDoc_cpf_cnpj().length() == 11 ? 01 : 02);
//				        			detalheArquivo.setValue("CpfCnpjSacado", boleto.getCliente().getDoc_cpf_cnpj());
//				        			detalheArquivo.setValue("NomeSacado",tratarString(boleto.getCliente().getNome_razao().toUpperCase(), 37) );
//				        			detalheArquivo.setValue("EnderecoSacado",tratarString(boleto.getCliente().getEndereco_principal().getEndereco().toUpperCase(), 40));
//				        			detalheArquivo.setValue("BairroSacado",tratarString(boleto.getCliente().getEndereco_principal().getBairro().toUpperCase(), 12)); 
//				        			detalheArquivo.setValue("Cep", boleto.getCliente().getEndereco_principal().getCep());
//				        			detalheArquivo.setValue("CidadeSacado", tratarString(boleto.getCliente().getEndereco_principal().getCidade().toUpperCase(), 15));
//				        			detalheArquivo.setValue("UfCidadeSacado", boleto.getCliente().getEndereco_principal().getUf());
//				        			detalheArquivo.setValue("SequencialRegistro", registro+1);
//				        			
//				        			registro++;
//				        			
//				        			Record detalheMulta = ff.createRecord("DetalheArquivoMulta"); 
//				        			detalheMulta.setValue("CodigoMulta",2);
//				        			detalheMulta.setValue("DataInicio",format.format(new DateTime(boleto.getData_vencimento()).plusDays(1).toDate()));
//				        			detalheMulta.setValue("ValorPercentual",cb.getMulta()+"00");
//				        			detalheMulta.setValue("SequencialRegistro",registro+1);
//				        			detalheArquivo.addInnerRecord(detalheMulta);
//				        			
//				        			ff.addRecord(detalheArquivo);
//				        			
//				        			registro++;
//				        			
//				        			
//
//				        			
//				        			boleto.setRemessaEnviada("SIM");
//				        			em.merge(boleto);
//		    					}
//		        			}else{
//		        				Record detalheArquivo = ff.createRecord("DetalheArquivo"); 
//			        			detalheArquivo.setValue("TipoInscCedente", 02);
//			        			detalheArquivo.setValue("CpfCnpjCedente", "07578965000105");
//			        			detalheArquivo.setValue("Agencia","0721");
//			        			detalheArquivo.setValue("AgenciaDV", "8");
//			        			detalheArquivo.setValue("ContaCorrente", "18751");
//			        			detalheArquivo.setValue("ContaCorrenteDV","8");
//			        			detalheArquivo.setValue("Convenio", "2827198");
//			        			detalheArquivo.setValue("CodControleEmpresa", boleto.getId().toString());
//			        			detalheArquivo.setValue("NossoNumero", "2827198"+boleto.getN_numero());
//			        			detalheArquivo.setValue("VariacaoCarteira", "019");
//			        			detalheArquivo.setValue("CarteiraCobranca", "17");
//			        			detalheArquivo.setValue("Comando",boleto.getComandoRemessa() == null ?  "01" : boleto.getComandoRemessa());
//			        			detalheArquivo.setValue("NumeroTitulo", boleto.getId().toString());
//			        			if(boleto.getData_vencimento() != null){
//			        				detalheArquivo.setValue("DataVencimento", format.format(boleto.getData_vencimento()));
//			        			}
//			        			detalheArquivo.setValue("ValorTitulo",boleto.getValor_titulo().replace(".", "").replace(",", ""));
//			        			detalheArquivo.setValue("EspecieTitulo", "12");
//			        			detalheArquivo.setValue("AceiteTitulo", "N");
//			        			detalheArquivo.setValue("DataEmissao", format.format(boleto.getData_emissao()) );
//			        			detalheArquivo.setValue("JurosMoraDiaAtraso", cb.getJuros().replace(".", "") );
//			        			detalheArquivo.setValue("DataLimiteDesconto", "000000" );
//			        			detalheArquivo.setValue("ValorDesconto", "0000000000000" );
//			        			detalheArquivo.setValue("InstrucaoCodificada1", "07"); 
//			        			detalheArquivo.setValue("TipoInscSacado",boleto.getCliente().getDoc_cpf_cnpj().length() == 11 ? 01 : 02);
//			        			detalheArquivo.setValue("CpfCnpjSacado", boleto.getCliente().getDoc_cpf_cnpj());
//			        			detalheArquivo.setValue("NomeSacado",tratarString(boleto.getCliente().getNome_razao().toUpperCase(), 37) );
//			        			detalheArquivo.setValue("EnderecoSacado",tratarString(boleto.getCliente().getEndereco_principal().getEndereco().toUpperCase(), 40));
//			        			detalheArquivo.setValue("BairroSacado",tratarString(boleto.getCliente().getEndereco_principal().getBairro().toUpperCase(), 12)); 
//			        			detalheArquivo.setValue("Cep", boleto.getCliente().getEndereco_principal().getCep());
//			        			detalheArquivo.setValue("CidadeSacado", tratarString(boleto.getCliente().getEndereco_principal().getCidade().toUpperCase(), 15));
//			        			detalheArquivo.setValue("UfCidadeSacado", boleto.getCliente().getEndereco_principal().getUf());
//			        			detalheArquivo.setValue("SequencialRegistro", registro+1);
//			        			
//			        			
//			        			
//			        			registro++;
//			        			
//			        			Record detalheMulta = ff.createRecord("DetalheArquivoMulta"); 
//			        			detalheMulta.setValue("CodigoMulta",2);
//			        			detalheMulta.setValue("DataInicio",format.format(new DateTime(boleto.getData_vencimento()).plusDays(1).toDate()));
//			        			detalheMulta.setValue("ValorPercentual",cb.getMulta()+"00");
//			        			detalheMulta.setValue("SequencialRegistro",registro+1);
//			        			detalheArquivo.addInnerRecord(detalheMulta);
//			        			
//			        			ff.addRecord(detalheArquivo);
//			        			
//			        			registro++;
//			        			
//			        			
//			        			
//			        			
//			        			boleto.setRemessaEnviada("SIM");
//			        			em.merge(boleto);
//		        			}
//		        		}
//				        	
//		       }
//		        		
//		        		
//				
//		        
//		        Record TrailerArquivo = ff.createRecord("TrailerArquivo");        
//		        TrailerArquivo.setValue("SequencialRegistro",registro+1);	      
//		        ff.addRecord(TrailerArquivo);
//	        
//		        String nomeArquivo  = "CNAB400_"+cb.getAgencia_banco()+"_"+cb.getN_conta()+"_"+format.format(new Date())+".REM";     	
//		        final File zipFile = new File(basepath + "/WEB-INF/uploads/"+nomeArquivo);
//	        
//		        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(zipFile), "ASCII"));
//			
//			
//				for (String line : ff.write()) {						
//					br.write(line);
//					br.newLine();
//				}
//				br.close(); 
//	        
//				StreamSource ssZip = new StreamSource() {
//		            InputStream is = new ByteArrayInputStream(readImageOldWay(zipFile)); 
//		            @Override
//		            public InputStream getStream() {
//		                return is;
//		            }
//		        };
//	        
//		        StreamResource srZip = new StreamResource(ssZip, zipFile.getName());	        
//		        srZip.setMIMEType("application/unknow");
//	        
//		        Page.getCurrent().open(srZip, "_blank", true);
//	        
//		        em.getTransaction().commit();
//		        Notify.Show("Arquivo Remessa gerado com Sucesso!", Notify.TYPE_NOTICE);
//		        }else{		        	
//		        	Notify.Show("Nenhum boleto encontrado!", Notify.TYPE_NOTICE);
//		        }
//		        
//			}
//		        
//			
//		}catch(Exception e){
//			if(em.getTransaction().isActive()){
//				em.getTransaction().rollback();
//			}
//			e.printStackTrace();
//		}
//        
//	}
	
	
	private static String tratarString(String valor, Integer qtd){
		
		String valor_novo = Normalizer.normalize(valor, Normalizer.Form.NFD);
		valor_novo = valor_novo.replaceAll("[^\\p{ASCII}]", "");
        
		return valor_novo.length() > qtd ? valor_novo.substring(0,qtd) : valor_novo;
	}
	
	private static ContaBancaria getContaBancaria(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			String controle =  "ACESSO-POS";
			Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
			qControle.setParameter("nome", controle);
			qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
											
			ContaBancaria cb = null;
			if(qControle.getResultList().size() ==1){
				cb = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria_bkp();
			}
			
			return cb;
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	private static ControleTitulo getControle(String controle){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
			qControle.setParameter("nome", controle);
			qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
											
			
			if(qControle.getResultList().size() ==1){
				return  ((ControleTitulo)qControle.getSingleResult());
			}
			
			return null;
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	private static Record createDetalheArquivo(FlatFile<Record> ff,List<ContasReceber> boletos) {  

	 	SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
	 
	 	Integer registro = 1;
     
	 	Record DetalheArquivo = ff.createRecord("Detalhe-Header-Lancamento"); 
        for (ContasReceber boleto : boletos) {
        	
        	if(registro == 1){
        	
	        	DetalheArquivo.setValue("TipoInscCedente", 02);
	        	DetalheArquivo.setValue("CpfCnpjCedente", "07578965000105");
	        	DetalheArquivo.setValue("Agencia","0721");
	        	DetalheArquivo.setValue("AgenciaDV", "8");
	        	DetalheArquivo.setValue("ContaCorrente", "18751");
	        	DetalheArquivo.setValue("ContaCorrenteDV","8");
	        	DetalheArquivo.setValue("Convenio", "2827198");
	        	DetalheArquivo.setValue("CodControleEmpresa", boleto.getId().toString());
	        	DetalheArquivo.setValue("NossoNumero", "2827198"+boleto.getN_numero());
	        	DetalheArquivo.setValue("VariacaoCarteira", "019");
	        	DetalheArquivo.setValue("CarteiraCobranca", "17");
	        	DetalheArquivo.setValue("Comando", "01");
	        	DetalheArquivo.setValue("NumeroTitulo", boleto.getId().toString());
	        	if(boleto.getData_vencimento() != null){
	        		DetalheArquivo.setValue("DataVencimento", format.format(boleto.getData_vencimento()));
	        	}
	        	DetalheArquivo.setValue("ValorTitulo", "10");
	        	DetalheArquivo.setValue("EspecieTitulo", "12");
	        	DetalheArquivo.setValue("AceiteTitulo", "N");
	        	DetalheArquivo.setValue("DataEmissao", format.format(new Date()) );
	        	DetalheArquivo.setValue("InstrucaoCodificada1", "07"); 
	        	DetalheArquivo.setValue("TipoInscSacado", 01);
	        	DetalheArquivo.setValue("CpfCnpjSacado", boleto.getCliente().getDoc_cpf_cnpj());
	        	DetalheArquivo.setValue("NomeSacado",  boleto.getCliente().getNome_razao().toUpperCase().substring(0, boleto.getCliente().getNome_razao().length() > 37 ?  37 : boleto.getCliente().getNome_razao().length()));
	        	DetalheArquivo.setValue("EnderecoSacado",boleto.getCliente().getEndereco_principal().getEndereco().toUpperCase().substring(0, boleto.getCliente().getEndereco_principal().getEndereco().length() > 40 ? 40 : boleto.getCliente().getEndereco_principal().getEndereco().length()));
	        	DetalheArquivo.setValue("BairroSacado", boleto.getCliente().getEndereco_principal().getBairro().toUpperCase());
	        	DetalheArquivo.setValue("Cep", boleto.getCliente().getEndereco_principal().getCep());
	        	DetalheArquivo.setValue("CidadeSacado", boleto.getCliente().getEndereco_principal().getCidade().toUpperCase());
	        	DetalheArquivo.setValue("UfCidadeSacado", boleto.getCliente().getEndereco_principal().getUf());
	        	DetalheArquivo.setValue("SequencialRegistro", registro+1);
        	}else{
        		Record xxx = ff.createRecord("Detalhe-Body-Lancamento"); 
        		xxx.setValue("TipoInscCedente", 02);
        		xxx.setValue("CpfCnpjCedente", "07578965000105");
        		xxx.setValue("Agencia","0721");
        		xxx.setValue("AgenciaDV", "8");
        		xxx.setValue("ContaCorrente", "18751");
        		xxx.setValue("ContaCorrenteDV","8");
        		xxx.setValue("Convenio", "2827198");
        		xxx.setValue("CodControleEmpresa", boleto.getId().toString());
        		xxx.setValue("NossoNumero", "2827198"+boleto.getN_numero());
        		xxx.setValue("VariacaoCarteira", "019");
        		xxx.setValue("CarteiraCobranca", "17");
        		xxx.setValue("Comando", "01");
        		xxx.setValue("NumeroTitulo", boleto.getId().toString());
	        	if(boleto.getData_vencimento() != null){
	        		xxx.setValue("DataVencimento", format.format(boleto.getData_vencimento()));
	        	}
	        	xxx.setValue("ValorTitulo", "10");
	        	xxx.setValue("EspecieTitulo", "12");
	        	xxx.setValue("AceiteTitulo", "N");
	        	xxx.setValue("DataEmissao", format.format(new Date()) );
	        	xxx.setValue("InstrucaoCodificada1", "07"); 
	        	xxx.setValue("TipoInscSacado", 01);
	        	xxx.setValue("CpfCnpjSacado", boleto.getCliente().getDoc_cpf_cnpj());
	        	xxx.setValue("NomeSacado",  boleto.getCliente().getNome_razao().toUpperCase().substring(0, boleto.getCliente().getNome_razao().length() > 37 ?  37 : boleto.getCliente().getNome_razao().length()));
	        	xxx.setValue("EnderecoSacado",boleto.getCliente().getEndereco_principal().getEndereco().toUpperCase().substring(0, boleto.getCliente().getEndereco_principal().getEndereco().length() > 40 ? 40 : boleto.getCliente().getEndereco_principal().getEndereco().length()));
	        	xxx.setValue("BairroSacado", boleto.getCliente().getEndereco_principal().getBairro().toUpperCase());
	        	xxx.setValue("Cep", boleto.getCliente().getEndereco_principal().getCep());
	        	xxx.setValue("CidadeSacado", boleto.getCliente().getEndereco_principal().getCidade().toUpperCase());
	        	xxx.setValue("UfCidadeSacado", boleto.getCliente().getEndereco_principal().getUf());
	        	xxx.setValue("SequencialRegistro", registro+1);
	        	
	        	if(DetalheArquivo != null && xxx != null){
	        		DetalheArquivo.addInnerRecord(xxx);
	        	}
        	}
        	
        	registro++;
		}
        
        return DetalheArquivo;  
 } 	 
	
//	public static void gerarArquivoRemessa(){
//		try{
//			String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();				
//			File layoutTemporario = new File(basepath +"/WEB-INF/layouts/BBRemessa400.txg.xml"); 	        
//	        FlatFile<Record> ff = Texgit.createFlatFile(layoutTemporario);  
//	        
//	        EntityManager em =ConnUtil.getEntityManager();
//	        
//	        String controle =  "ACESSO-POS";
//			Query qControle = em.createQuery("select c from ControleTitulo c where c.nome=:nome and c.empresa_id =:empresa", ControleTitulo.class);
//			qControle.setParameter("nome", controle);
//			qControle.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
//											
//			ContaBancaria cb = null;
//			if(qControle.getResultList().size() ==1){
//				cb = ((ControleTitulo)qControle.getSingleResult()).getConta_bancaria();
//			}
//			
//			SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
//	        
//			if(cb != null){
//		        Record header = ff.createRecord("HeaderArquivo");	        
//		        header.setValue("Agencia", cb.getAgencia_banco().replace("0", ""));
//		        header.setValue("AgenciaDV", 8);
//		        header.setValue("ContaCorrente",cb.getN_conta());
//		        header.setValue("ContaCorrenteDV", 8);
//		        header.setValue("NomeCedente",cb.getCedente().length() > 30 ? cb.getCedente().toUpperCase().substring(0, 30) : cb.getCedente().toUpperCase());
//		        header.setValue("DataGravacao", format.format(new Date()));
//		        header.setValue("SequencialRemessa", "1");
//		        header.setValue("NumeroConvenioLider",cb.getConvenio());	        
//		        header.setValue("SequencialRegistro", "1");
//		        ff.addRecord(header);
//	        	       
//	        
//	        Query q= em.createQuery("select c from ContasReceber c where c.remessa_enviada != 'SIM' ", ContasReceber.class);        
//	        List<ContasReceber> lista_boletos = q.getResultList();
//	    
//			Integer registro = 1;
//			for (ContasReceber boleto: lista_boletos) {			
//				
//				if(CheckNdocUtil.checkNdocAcesso(boleto.getN_doc())){
//					String[] codigos = boleto.getN_doc().split("/");
//					String cod_contrato = codigos[0];
//					
//					AcessoCliente contrato = ContratosAcessoDAO.find(Integer.parseInt(cod_contrato));
//					
//					if(contrato.getEmitir_nfe_automatico() != null && contrato.getEmitir_nfe_automatico().equals("SIM")){
//						
//						Record DetalheArquivo = ff.createRecord("DetalheArquivo"); 
//						DetalheArquivo.setValue("TipoInscCedente", 02);
////						DetalheArquivo.setValue("CpfCnpjCedente", OpusERP4UI.getEmpresa().getCnpj());
////						DetalheArquivo.setValue("Agencia",cb.getAgencia_banco().replace("0", ""));
////						DetalheArquivo.setValue("AgenciaDV", "8");
////						DetalheArquivo.setValue("ContaCorrente", cb.getN_conta());
////						DetalheArquivo.setValue("ContaCorrenteDV","8");
////						DetalheArquivo.setValue("Convenio", cb.getConvenio());
////						DetalheArquivo.setValue("CodControleEmpresa", boleto.getId().toString());
////						DetalheArquivo.setValue("NossoNumero", cb.getConvenio()+boleto.getN_numero());
////						DetalheArquivo.setValue("VariacaoCarteira", cb.getVariacao_carteira());
////						DetalheArquivo.setValue("CarteiraCobranca", cb.getCarteira());
////						DetalheArquivo.setValue("Comando",boleto.getComandoRemessa() == null ?  "01" : boleto.getComandoRemessa());
////						DetalheArquivo.setValue("NumeroTitulo", boleto.getId().toString());
////						DetalheArquivo.setValue("DataVencimento", format.format(boleto.getData_vencimento()));
////						DetalheArquivo.setValue("ValorTitulo", Real.formatStringToDB(boleto.getValor_titulo()).replace(".", ""));
////						DetalheArquivo.setValue("EspecieTitulo", "12");
////						DetalheArquivo.setValue("AceiteTitulo", "N");
////						DetalheArquivo.setValue("DataEmissao", format.format(new Date()) );
////						DetalheArquivo.setValue("InstrucaoCodificada1", "07"); 
////						DetalheArquivo.setValue("TipoInscSacado", 01);
////						DetalheArquivo.setValue("CpfCnpjSacado", boleto.getCliente().getDoc_cpf_cnpj());
////						DetalheArquivo.setValue("NomeSacado",  boleto.getCliente().getNome_razao().toUpperCase().substring(0, boleto.getCliente().getNome_razao().length() > 37 ?  37 : boleto.getCliente().getNome_razao().length()));
////						DetalheArquivo.setValue("EnderecoSacado",boleto.getCliente().getEndereco_principal().getEndereco().toUpperCase().substring(0, boleto.getCliente().getEndereco_principal().getEndereco().length() > 40 ? 40 : boleto.getCliente().getEndereco_principal().getEndereco().length()));
////						DetalheArquivo.setValue("BairroSacado", boleto.getCliente().getEndereco_principal().getBairro().length() > 12 ? boleto.getCliente().getEndereco_principal().getBairro().toUpperCase().substring(0, 12) : boleto.getCliente().getEndereco_principal().getBairro().toUpperCase());
////						DetalheArquivo.setValue("Cep", boleto.getCliente().getEndereco_principal().getCep());
////						DetalheArquivo.setValue("CidadeSacado", boleto.getCliente().getEndereco_principal().getCidade().length() > 15 ? boleto.getCliente().getEndereco_principal().getCidade().toUpperCase().substring(0, 15) : boleto.getCliente().getEndereco_principal().getCidade().toUpperCase());
////						DetalheArquivo.setValue("UfCidadeSacado", boleto.getCliente().getEndereco_principal().getUf());
////						DetalheArquivo.setValue("SequencialRegistro", registro+1);
//						ff.addRecord(DetalheArquivo);
//
//						registro ++;
//					}
//				}
//				   	 
//		        
//		        
//			}
//	        
//	        Record TrailerArquivo = ff.createRecord("TrailerArquivo");        
//	        TrailerArquivo.setValue("SequencialRegistro", registro+1);
//	        ff.addRecord(TrailerArquivo);
//	        
//	        String nomeArquivo  = "CNAB400_"+cb.getN_conta()+"_"+cb.getAgencia_banco()+"_"+format.format(new Date())+".REM";     	
//	        final File zipFile = new File(basepath + "/WEB-INF/uploads/"+nomeArquivo);
//	        zipFile.createNewFile();
//	        
//			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(zipFile), "ASCII"));
//			
//			
//			for (String line : ff.write()) {						
//				br.write(line);
//				br.newLine();
//			}
//			br.close(); 
//	        
//			StreamSource ssZip = new StreamSource() {
//	            InputStream is = new ByteArrayInputStream(readImageOldWay(zipFile)); 
//	            @Override
//	            public InputStream getStream() {
//	                return is;
//	            }
//	        };
//	        
//	        StreamResource srZip = new StreamResource(ssZip, zipFile.getName());	        
//	        srZip.setMIMEType("application/unknow");
//	        
//	        Page.getCurrent().open(srZip, "_blank", true);
//	        
//	        
//			}		 
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//        
//	}
	
	public static byte[] readImageOldWay(File file) throws IOException
	{
	  //Logger.getLogger(Main.class.getName()).log(Level.INFO, "[Open File] " + file.getAbsolutePath());
	  if(file != null){
		  
		  InputStream is = new FileInputStream(file);
		  // Get the size of the file
		  long length = file.length();
		  // You cannot create an array using a long type.
		  // It needs to be an int type.
		  // Before converting to an int type, check
		  // to ensure that file is not larger than Integer.MAX_VALUE.
		  if (length > Integer.MAX_VALUE)
		  {
			  // File is too large
		  }
		  // Create the byte array to hold the data
		  byte[] bytes = new byte[(int) length];
		  // Read in the bytes
		  int offset = 0;
		  int numRead = 0;
		  while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
		  {
			  offset += numRead;
		  }
		  // Ensure all the bytes have been read in
		  if (offset < bytes.length)
		  {
			  throw new IOException("Could not completely read file " + file.getName());
		  }
		  // Close the input stream and return bytes
		  is.close();
		  return bytes;
	  }
	return null;
	}

}


//sl 40






//Deus sempre foi o cuidador de Israel, sempre foi o líder, sempre foi o rei
//Deus disse a abraão que um dia seria estabelecido a monarquia Gn17.6
//Jacó também profetiza a respeito de um rei em GN 49.10
//Através de Moises também é profetizado um Rei
//O Problema não era o povo desejar um rei, e sim, rejeitar a Deus
//Ainda não era a hora certa para ser instituido a monarquia
//O Povo rejeitou o próprio Deus
