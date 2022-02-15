package com.digital.opuserp.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.FiltroAcesso;
import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.RadAcct;
import com.digital.opuserp.domain.RadCheck;
import com.digital.opuserp.domain.RadReply;
import com.digital.opuserp.domain.RadUserGgroup;
import com.digital.opuserp.domain.RegistroTrocaMaterial;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.ui.Notification;


public class CredenciaisAcessoDAO {

	EntityManager em;
	
	public CredenciaisAcessoDAO(){
		em = ConnUtil.getEntity();
	}

		
	public static boolean ipDisponibility(String ip){
		EntityManager emr = ConnUtil.getEntity();
		Query q = emr.createQuery("select ac from AcessoCliente ac where ac.status_2 != :status and  ac.endereco_ip =:ip", AcessoCliente.class);
		q.setParameter("status", "ENCERRADO");
		q.setParameter("ip", ip);
		
		if(q.getResultList().size() > 0){
			return false;
		}
		
		return true;		
	}
	
	public static boolean ChecarPadraoAcessoNDoc(String nDoc){
		Pattern pAntigo = Pattern.compile("[0-9]{1,}/[0-9]{2}/[0-9]{2}");
		Matcher mAntigo = pAntigo.matcher(nDoc);
		
		Pattern pNovo = Pattern.compile("[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}");
	    Matcher mNovo = pNovo.matcher(nDoc);
	    
	    Pattern pNovoProRata = Pattern.compile("[0-9]{1,}/PRORATA");
	    Matcher mNovoProRata = pNovoProRata.matcher(nDoc);
	    
	    
	    if (mAntigo.matches() || mNovo.matches()|| mNovoProRata.matches()) {
	    	return true;
	    }
	    
	    return false;
	}
	
	public static List<AcessoCliente> ProcurarContratosVencidos(){
		try{
			EntityManager emr = ConnUtil.getEntity();
			
			Query q = emr.createQuery("select a from AcessoCliente a where a.data_venc_contrato < :dataHoje and a.status_2 = 'ATIVO' ", AcessoCliente.class);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			q.setParameter("dataHoje", sdf.parse(sdf.format(new Date())));
			
			return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	

	
	public static boolean bloquearContratoCliente(Integer codAcesso){
		
		EntityManager emr = ConnUtil.getEntity();
		
		AcessoCliente acesso = emr.find(AcessoCliente.class, codAcesso);
		if(acesso != null){
			
			emr.getTransaction().begin();
			
				//Muda Status do Acesso
				acesso.setStatus_2("BLOQUEADO");					
			
				//Cadastra Plano Novo
				//emr.persist(new RadUserGgroup(null, acesso.getLogin(), acesso.getContrato().getId().toString()+"_BLOQUEADO", "1"));
				
				//Retira IPs Fixados no RADIUS
				Query q = emr.createQuery("select rr from RadReply rr where rr.username =:username and rr.attribute =:attribute", RadReply.class);
				q.setParameter("username", acesso.getLogin());
				q.setParameter("attribute", "Framed-IP-Address");
				
				for (RadReply rr : (List<RadReply>)q.getResultList()) {
					emr.remove(rr); 
				}
				
				Query q2 = emr.createQuery("select rr from RadReply rr where rr.username =:username and rr.attribute =:attribute", RadReply.class);
				q2.setParameter("username", acesso.getLogin());
				q2.setParameter("attribute", "Framed-Pool");
				
				for (RadReply rr : (List<RadReply>)q2.getResultList()) {
					emr.remove(rr); 
				}
				
				emr.persist(new RadReply(null, acesso.getLogin(), "Framed-Pool", "=", "BLOQUEADO"));					
				
				//Derruba Cliente Caso Esteja Logado
				//MikrotikUtil.derrubarConexaoHOTSPOT(acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()), acesso.getLogin());
				MikrotikUtil.derrubarConexaoPPPOE(acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()), acesso.getLogin());
				
				if(acesso != null && acesso.getIttv_id() != null){
					try {
						
						String s = IttvDAO.atualizarStatus(acesso.getIttv_id(), "INACTIVE");
						System.out.println(s);
						
					} catch (Exception e) {						
						e.printStackTrace();
					}
				}
			
			emr.getTransaction().commit();
			
			return true;
		}
		
		return false;
	}
	public static boolean bloquearContratoClienteTotal(Integer codAcesso){
		
		EntityManager emr = ConnUtil.getEntity();
		
		AcessoCliente acesso = emr.find(AcessoCliente.class, codAcesso);
		if(acesso != null){
			
			emr.getTransaction().begin();
			
				//Muda Status do Acesso
				acesso.setStatus_2("BLOQUEADO");					
			
				//Cadastra Plano Novo
				//emr.persist(new RadUserGgroup(null, acesso.getLogin(), acesso.getContrato().getId().toString()+"_BLOQUEADO", "1"));
				
				//Retira IPs Fixados no RADIUS
				Query q = emr.createQuery("select rr from RadReply rr where rr.username =:username and rr.attribute =:attribute", RadReply.class);
				q.setParameter("username", acesso.getLogin());
				q.setParameter("attribute", "Framed-IP-Address");
				
				for (RadReply rr : (List<RadReply>)q.getResultList()) {
					emr.remove(rr); 
				}
				
				Query q2 = emr.createQuery("select rr from RadReply rr where rr.username =:username and rr.attribute =:attribute", RadReply.class);
				q2.setParameter("username", acesso.getLogin());
				q2.setParameter("attribute", "Framed-Pool");
				
				for (RadReply rr : (List<RadReply>)q2.getResultList()) {
					emr.remove(rr); 
				}
				
				emr.persist(new RadReply(null, acesso.getLogin(), "Framed-Pool", "=", "BLOQUEADO_TOTAL"));					
				
				//Derruba Cliente Caso Esteja Logado
				//MikrotikUtil.derrubarConexaoHOTSPOT(acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()), acesso.getLogin());
				MikrotikUtil.derrubarConexaoPPPOE(acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()), acesso.getLogin());
				
				//Volta cadastro ITTV para ativo
				if(acesso != null && acesso.getIttv_id() != null){
					try {
						
						String s = IttvDAO.atualizarStatus(acesso.getIttv_id(), "INACTIVE");
						System.out.println(s);
						
					} catch (Exception e) {						
						e.printStackTrace();
					}
				}
			
			emr.getTransaction().commit();
			
			return true;
		}
		
		return false;
	}
	
	public static boolean DesbloquearContrato(Integer codContrato){
		
		EntityManager emr = ConnUtil.getEntity();
		AcessoCliente acesso = null;
		if(ModuloEmpresaDAO.checkModuloEmpresa(Modulo.COM.ACESSO)){				
				acesso = emr.find(AcessoCliente.class,codContrato);			
		}
			
		if(ModuloEmpresaDAO.checkModuloEmpresa(Modulo.COM.ACESSO)							
				&& acesso != null 
				&& acesso.getStatus_2() != null
				&& acesso.getStatus_2().equals("BLOQUEADO")){
			
				emr.getTransaction().begin();
				
				//Atualiza Status do Acesso para Ativo
				acesso.setStatus_2("ATIVO");
				emr.merge(acesso);
				
				//Remove marcações RadReply
				Query qrr = emr.createQuery("select rr from RadReply rr where rr.username = :usuario and rr.attribute = 'Framed-Pool' and rr.value = 'BLOQUEADO'", RadReply.class);
				qrr.setParameter("usuario", acesso.getLogin());						
				if(qrr.getResultList().size() >0){
							
					List<RadReply> marcacoes = qrr.getResultList(); 
					for(RadReply rr:marcacoes){
						emr.remove(rr);
					}
				}
				
				Query qrr2 = emr.createQuery("select rr from RadReply rr where rr.username = :usuario and rr.attribute = 'Framed-Pool' and rr.value = 'BLOQUEADO_TOTAL'", RadReply.class);
				qrr2.setParameter("usuario", acesso.getLogin());						
				if(qrr2.getResultList().size() >0){
							
					List<RadReply> marcacoes2 = qrr2.getResultList(); 
					for(RadReply rr:marcacoes2){
						emr.remove(rr);
					}
				}
				
				
				if(acesso.getEndereco_ip() != null && !acesso.getEndereco_ip().equals("")){
					emr.persist(new RadReply(null, acesso.getLogin(), "Framed-IP-Address", "=", acesso.getEndereco_ip()));
				}
				
				if(acesso != null && acesso.getIttv_id() != null){
					try{
						String s = IttvDAO.atualizarStatus(acesso.getIttv_id(), "ACTIVE");
						System.out.println(s);
					}catch(Exception e){
						e.printStackTrace();
					}					
				}
				
     							
				emr.getTransaction().commit();

				//Derruba Conexão do Cliente
				MikrotikUtil.desconectarCliente(acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()), acesso.getLogin());
				
				Notify.Show("Contrato de Acesso Desbloqueado!", Notify.TYPE_SUCCESS);	
				return true;										
		}
		
		return false;
	}
	
	public static boolean ConcluiDesbloqueioAcesso(Integer codBoleto){
		EntityManager emr = ConnUtil.getEntity();
		ContasReceber conta = emr.find(ContasReceber.class, codBoleto);
		
		AcessoCliente acesso = null;
		if(ModuloEmpresaDAO.checkModuloEmpresa(Modulo.COM.ACESSO)){
			if(conta != null && conta.getN_doc() != null && !ContasReceberDAO.allowNdocManual(conta.getN_doc())){
				String numeroDoc = conta.getN_doc();
				Integer codAcesso = Integer.parseInt(numeroDoc.split("/")[0].toString());				
				acesso = emr.find(AcessoCliente.class,codAcesso);
			}
		}
			
		if(ModuloEmpresaDAO.checkModuloEmpresa(Modulo.COM.ACESSO) 
				&& conta != null 
				&& CredenciaisAcessoDAO.ChecarPadraoAcessoNDoc(conta.getN_doc())				
				&& acesso != null 
				&& acesso.getStatus_2() != null
				&& acesso.getStatus_2().equals("BLOQUEADO")){
			
			//Verificar Se Mais Algum Boleto Desse Cliente Esta Bloqueado
			if(!ContasReceberDAO.existeAlbumBoletoBloqueadoeAbertoPorContrato(acesso.getId(), conta.getId())){
				
				emr.getTransaction().begin();
				
				//Atualiza Status do Acesso para Ativo
				acesso.setStatus_2("ATIVO");
				emr.merge(acesso);
				
				//Remove marcações RadReply
				Query qrr = emr.createQuery("select rr from RadReply rr where rr.username = :usuario and rr.attribute = 'Framed-Pool' and rr.value = 'BLOQUEADO'", RadReply.class);
				qrr.setParameter("usuario", acesso.getLogin());						
				if(qrr.getResultList().size() >0){
							
					List<RadReply> marcacoes = qrr.getResultList(); 
					for(RadReply rr:marcacoes){
						emr.remove(rr);
					}
				}
				Query qrr2 = emr.createQuery("select rr from RadReply rr where rr.username = :usuario and rr.attribute = 'Framed-Pool' and rr.value = 'BLOQUEADO_TOTAL'", RadReply.class);
				qrr2.setParameter("usuario", acesso.getLogin());						
				if(qrr2.getResultList().size() >0){
							
					List<RadReply> marcacoes = qrr2.getResultList(); 
					for(RadReply rr:marcacoes){
						emr.remove(rr);
					}
				}
				
				if(acesso.getEndereco_ip() != null && !acesso.getEndereco_ip().equals("")){
					emr.persist(new RadReply(null, acesso.getLogin(), "Framed-IP-Address", "=", acesso.getEndereco_ip()));
				}
												
				conta.setDesbloqueado("S");
				emr.merge(conta);
				
				emr.getTransaction().commit();

				//Derruba Conexão do Cliente
				MikrotikUtil.desconectarCliente(acesso.getBase().getUsuario(), acesso.getBase().getSenha(), acesso.getBase().getEndereco_ip(), Integer.parseInt(acesso.getBase().getPorta_api()), acesso.getLogin());
				
				Notify.Show("Contrato de Acesso Desbloqueado!", Notify.TYPE_NOTICE);				
			}										
		}
		
		return false;
	}
	
	public static boolean ConcluiRecargaPrePaga(Integer codBoleto){
			
		EntityManager emr = ConnUtil.getEntity();
		ContasReceber cr = ContasReceberDAO.find(codBoleto);
		
		if(cr != null){
			 
					      
		    if (ChecarPadraoAcessoNDoc(cr.getN_doc())) {
		    	
		    	String numeroDoc = cr.getN_doc();

				Integer codAcesso = Integer.parseInt(numeroDoc.split("/")[0].toString());		    	
		    	AcessoCliente acessoCliente = emr.find(AcessoCliente.class, codAcesso);
		    	
		    	if(acessoCliente != null && acessoCliente.getContrato().getTipo_contrato().equals("PRE-PAGO")){
		    		
		    		
		    		if(acessoCliente.getData_venc_contrato().before(new Date())){
		    		
		    			acessoCliente.setData_venc_contrato(
			    				new DateTime().plusMonths(acessoCliente.getContrato().getVigencia()).toDate());		    			
		    		
		    		}else{
		    			acessoCliente.setData_venc_contrato(
		    					new DateTime(acessoCliente.getData_venc_contrato()).plusMonths(acessoCliente.getContrato().getVigencia()).toDate());
		    		}
		    		
		    		
		    		
		    		emr.getTransaction().begin();
		    		emr.merge(acessoCliente);
		    		emr.getTransaction().commit();
		    		
		    		return true;
		    	}
		    }
		}		
		
		return false;
	}
	
	
	public static boolean checkAcessAtivo(Integer codCliente){
		EntityManager emr = ConnUtil.getEntity();
		
		Query q = emr.createQuery("select a from AcessoCliente a where a.cliente = :cliente",AcessoCliente.class);
		q.setParameter("cliente",emr.find(Cliente.class, codCliente));
		
		if(q.getResultList().size() > 0){		
			return true;
		}
		
		return false;
	}
	
	public boolean checkSerialProprio(String serial){
		
		Query q = em.createQuery("select s from SerialProduto s where s.serial = :serial",SerialProduto.class);
		
		q.setParameter("serial", serial);
		//q.setParameter("empresa", OpusERP4UI.getEmpresa());
		
		List<SerialProduto> seriais = q.getResultList();
		
		if(q.getResultList().size() > 0){
			for (SerialProduto s : seriais) {
				
				if( s.getStatus().equals("VENDIDO")){
					return true;
				}			
			}
			
			return false;
		}else{
			return true;
		}
			
	}
	
	public AcessoCliente getAcessoByCliente(Integer codCliente){
		Query q = em.createQuery("select ac from AcessoCliente ac where ac.cliente =:cliente", AcessoCliente.class);
		q.setParameter("cliente", new Cliente(codCliente));
		
		if(q.getResultList().size() == 1){
			return (AcessoCliente)q.getSingleResult();
		}else{
			return null;
		}
	}
	
	public List<AcessoCliente> buscarAcessoByCliente(Integer codCliente){
		Query q = em.createQuery("select ac from AcessoCliente ac where ac.cliente =:cliente ORDER BY ac.data_cadastro DESC", AcessoCliente.class);
		q.setParameter("cliente", new Cliente(codCliente));
		
		List<AcessoCliente> acessoCliente = null; 
		if(q.getResultList().size() > 0){
			return acessoCliente = q.getResultList();
		}else{
			return null;
		}
	}
	
	public boolean permitirGerarNovoAcesso(Integer codCliente){
		Query q = em.createQuery("select ac from AcessoCliente ac where ac.cliente =:cliente and ac.status_2 !=:status", AcessoCliente.class);
		q.setParameter("cliente", new Cliente(codCliente));
		q.setParameter("status", "ENCERRADO");
		
		
		if(q.getResultList().size() > 0){
			return false;
		}else{
			return true;
		}
	}
	
	public AcessoCliente getAcessoByCodAceso(Integer codAcesso){
		Query q = em.createQuery("select ac from AcessoCliente ac where ac.id =:codAcesso", AcessoCliente.class);
		q.setParameter("codAcesso", codAcesso);
		
		if(q.getResultList().size() == 1){
			return (AcessoCliente)q.getSingleResult();
		}else{
			return null;
		}
	}
	
	public boolean getAcessoBySerialMAC(String EnderecoMAC,Integer codMaterial, Integer codAcessoEncerrado){
		boolean valid = false;
		Query q = em.createQuery("select ac from AcessoCliente ac where  ac.endereco_mac =:EnderecoMAC and ac.material =:codMaterial and ac.id !=:codAcessoEncerrado and ac.status_2 !=:status", AcessoCliente.class);
		q.setParameter("EnderecoMAC", EnderecoMAC);
		q.setParameter("codMaterial", new Produto(codMaterial));
		q.setParameter("codAcessoEncerrado", codAcessoEncerrado);
		q.setParameter("status", "ENCERRADO");
		
		if(q.getResultList().size() > 0){
			valid = true;
		}
		return valid;
	}
	
	public boolean getAcessoBySerialSerialOnu(String EnderecoMAC,Integer codMaterial, Integer codAcessoEncerrado){
		boolean valid = false;
		Query q = em.createQuery("select ac from AcessoCliente ac where  ac.onu_serial =:EnderecoMAC and ac.onu =:codMaterial and ac.id !=:codAcessoEncerrado and ac.status_2 !=:status", AcessoCliente.class);
		q.setParameter("EnderecoMAC", EnderecoMAC);
		q.setParameter("codMaterial", new Produto(codMaterial));
		q.setParameter("codAcessoEncerrado", codAcessoEncerrado);
		q.setParameter("status", "ENCERRADO");
		
		if(q.getResultList().size() > 0){
			valid = true;
		}
		return valid;
	}
	
	public List<Cliente> getClientesEncerrados(){
		
		Query  q = em.createQuery("select c from AcessoCliente c where c.empresa_id =:Empresa and c.status_2 =:status", AcessoCliente.class);
		q.setParameter("Empresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("status", "ENCERRADO");
		
		
		if(q.getResultList().size() >0){
			
			List<Cliente> result = new ArrayList<>();
			
			for(AcessoCliente ac:(List<AcessoCliente>)q.getResultList()){
				
				result.add(ac.getCliente());
			}
			
			return result;
		}else{
			return null;
		}
	}
	
	public boolean cadastrarFiltroAcesso(Integer codAcesso, String palavra){
		
		AcessoCliente ac = em.find(AcessoCliente.class, codAcesso);
		MikrotikUtil mkUtil = new MikrotikUtil();
		
		return mkUtil.criarMarcacaoFirewallFiltroBloqContext(ac.getBase().getUsuario(), ac.getBase().getSenha(), ac.getBase().getEndereco_ip(), 
							Integer.parseInt(ac.getBase().getPorta_api()),ac.getId().toString(), ac.getCliente().getNome_razao(), palavra);
		
		
	}
	
	public boolean removerFiltroAcesso(Integer codAcesso, String palavra){
		
		AcessoCliente ac = em.find(AcessoCliente.class, codAcesso);
		MikrotikUtil mkUtil = new MikrotikUtil();
		
		return mkUtil.removerMarcacaoFirewallFiltroBloqContext(ac.getBase().getUsuario(), ac.getBase().getSenha(), ac.getBase().getEndereco_ip(), 
							Integer.parseInt(ac.getBase().getPorta_api()),ac.getId().toString(), ac.getCliente().getNome_razao(), palavra);
		
		
	}
	
	public static List<FiltroAcesso> getFiltrosAcesso(Integer codAcesso){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from FiltroAcesso f where f.cod_contrato = :codAcesso",FiltroAcesso.class);
		q.setParameter("codAcesso", codAcesso);
		
		return q.getResultList();
	}
	
	public static String calcDiferencaValorPlano(Integer codAcesso,Integer codPlanoNovo) throws Exception{
			
			EntityManager em = ConnUtil.getEntity();
	
			PlanoAcesso planoNovo = em.find(PlanoAcesso.class, codPlanoNovo);
			AcessoCliente ac = em.find(AcessoCliente.class, codAcesso);		
			PlanoAcesso planoAntigo = em.find(PlanoAcesso.class,ac.getPlano().getId());
			
			ContasReceberDAO crDao = new ContasReceberDAO();
			Integer codigoCliente = ac.getCliente().getId();

			ContasReceber boletoAtual = null;
			
			//Verifica se Existem Boletos Em Abertos-Não Vencidos									
			if(ac != null){						
				List<ContasReceber> boletosAbertosNaoVencidos = crDao.buscarTitulosNaoVencidosDeAcessoPorContrato(codAcesso);		

				if(boletosAbertosNaoVencidos != null){
					boletoAtual = boletosAbertosNaoVencidos.get(0);
				}else if(!ac.getContrato().getNome().equals("GRATIS")){
					Notification.show("SEM BOLETOS");
				}
			}
				
				
			
			if(boletoAtual != null){
				//Expressão Regular do Primeiro Boleto
				
				Pattern pNovoProRata = Pattern.compile("[0-9]{1,}/PRORATA");
			    Matcher mNovoProRata = pNovoProRata.matcher(boletoAtual.getN_doc());
				
				Pattern pAntigo = Pattern.compile(codAcesso+"/[0-9]{2}/01");
				Matcher mAntigo = pAntigo.matcher(boletoAtual.getN_doc());
				
				Pattern pNovo = Pattern.compile(codAcesso+"/[0-9]{2}-01/[0-9]{2}");
			    Matcher mNovo = pNovo.matcher(boletoAtual.getN_doc());
			      
			    if (mAntigo.matches() || mNovo.matches() || mNovoProRata.matches()) {
			    	
					Date dataProxVencBoleto = boletoAtual.getData_vencimento();
					DateTime dataAnterior = new DateTime(dataProxVencBoleto).minusMonths(1);
					
					Integer qtdDias = Days.daysBetween(dataAnterior, new DateTime(dataProxVencBoleto)).getDays(); //Dias do Mês
					
					Integer qtdDiasUtilizados = 0;
					if(new Date().before(ac.getData_instalacao())){
						qtdDiasUtilizados = 0;
					}else{
						if(ac.getData_renovacao() == null){
							qtdDiasUtilizados = Days.daysBetween(new DateTime(ac.getData_instalacao()), new DateTime(new Date())).getDays();
						}else{
							qtdDiasUtilizados = Days.daysBetween(new DateTime(dataProxVencBoleto).minusMonths(1), new DateTime(new Date())).getDays();
						}  						
					}
					
					double vlrDoDiaPlanoAntigo = Real.formatStringToDBDouble(planoAntigo.getValor()) / qtdDias; //Valor Diário
					double vlrDosDiasUtilizados = vlrDoDiaPlanoAntigo * qtdDiasUtilizados;
		

					Integer qtdDiasRestantes = 0;
					if(new Date().before(ac.getData_instalacao())){
						qtdDiasRestantes  = Days.daysBetween(new DateTime(ac.getData_instalacao()), new DateTime(dataProxVencBoleto)).getDays();
					}else{
						qtdDiasRestantes  = Days.daysBetween(new DateTime(new Date()).minusDays(1), new DateTime(dataProxVencBoleto)).getDays();
					}
					
					
					
					double vlrDoDiaPlanoNovo = Real.formatStringToDBDouble(planoNovo.getValor()) / qtdDias;
					double vlrDosDiasRestantes = vlrDoDiaPlanoNovo * qtdDiasRestantes;
					
					Double valorBoleto = (vlrDosDiasUtilizados + vlrDosDiasRestantes);
				//	if(planoAntigo.getValor().equals(planoNovo.getValor())){
				//		valorBoleto = Real.formatStringToDBDouble(planoNovo.getValor());
				//	}
					String vlBoleto = Real.formatDbToString(valorBoleto.toString());
					
					Integer  qtdDiasTeste = Days.daysBetween(new DateTime(ac.getData_instalacao()), new DateTime(dataProxVencBoleto)).getDays();
					
//					System.out.println("O Primeiro Boleto ainda irá Vencer");
//					
//					Notification.show("O Primeiro Boleto ainda irá Vencer",
//							"Data Prox. Vencimento:"+dataProxVencBoleto.toString()+"\n"+							
//							"Data Instalação:"+ac.getData_instalacao().toString()+"\n"+
//							"Qtd. Dias Entre Int e Venc:"+qtdDiasTeste.toString()+"\n"+
//							"Qtd. Dias Entre as Datas Acima:"+qtdDias.toString()+"\n"+
//							"Data de Hoje:"+new Date().toString()+"\n"+
//							"Qtd. de Dias Utilizados:"+qtdDiasUtilizados.toString()+"\n"+
//							"Valor do Dia Plano Antigo:"+String.valueOf(vlrDoDiaPlanoAntigo)+"\n"+
//							"Valor Total dos Dias Utilizados:"+String.valueOf(vlrDosDiasUtilizados)+"\n"+							
//							"Qtd. de Dias Restantes:"+qtdDiasRestantes.toString()+"\n"+
//							"Valor do Dia Plano Novo:"+String.valueOf(vlrDoDiaPlanoNovo)+"\n"+
//							"Valor Total dos Dias Restantes:"+String.valueOf(vlrDosDiasRestantes)+"\n"+
//							"Valor Total do Boleto:"+vlBoleto+"\n"
//									, Type.ERROR_MESSAGE);
					
					return vlBoleto;					
				}else{	
					Date dataProxVencBoleto = boletoAtual.getData_vencimento();
					DateTime dataAnterior = new DateTime(dataProxVencBoleto).minusMonths(1);
					
					Integer qtdDias = Days.daysBetween(dataAnterior, new DateTime(dataProxVencBoleto)).getDays(); //Dias do Mês
					Integer qtdDiasUtilizados = Days.daysBetween(new DateTime(dataAnterior), new DateTime(new Date())).getDays();
					
					double vlrDoDiaPlanoAntigo = Real.formatStringToDBDouble(planoAntigo.getValor()) / qtdDias; //Valor Diário
					double vlrDosDiasUtilizados = vlrDoDiaPlanoAntigo * qtdDiasUtilizados;
					
					Integer qtdDiasRestantes  = Days.daysBetween(new DateTime(new Date()).minusDays(1), new DateTime(dataProxVencBoleto)).getDays();
					
					
					double vlrDoDiaPlanoNovo = Real.formatStringToDBDouble(planoNovo.getValor()) / qtdDias;
					double vlrDosDiasRestantes = vlrDoDiaPlanoNovo * qtdDiasRestantes;
					
					Double valorBoleto = (vlrDosDiasUtilizados + vlrDosDiasRestantes);
					String vlBoleto = Real.formatDbToString(valorBoleto.toString());
					    
					    
//					System.out.println("O Primeiro Boleto já venceu");
//					
//					Notification.show("O Primeiro Boleto já Venceu",
//							"Data Prox. Vencimento:"+dataProxVencBoleto.toString()+"\n"+
//							"Data Anterior:"+dataAnterior.toString()+"\n"+
//							"Qtd. Dias Entre as Datas Acima:"+qtdDias.toString()+"\n"+							
//							"Data de Hoje:"+new Date().toString()+"\n"+
//							"Qtd. de Dias Utilizados:"+qtdDiasUtilizados.toString()+"\n"+
//							"Valor do Dia Plano Antigo:"+String.valueOf(vlrDoDiaPlanoAntigo)+"\n"+
//							"Valor Total dos Dias Utilizados:"+String.valueOf(vlrDosDiasUtilizados)+"\n"+							
//							"Qtd. de Dias Restantes:"+qtdDiasRestantes.toString()+"\n"+
//							"Valor do Dia Plano Novo:"+String.valueOf(vlrDoDiaPlanoNovo)+"\n"+
//							"Valor Total dos Dias Restantes:"+String.valueOf(vlrDosDiasRestantes)+"\n"+
//							"Valor Total do Boleto:"+vlBoleto+"\n"
//				     		, Type.ERROR_MESSAGE);
					    
					return vlBoleto;
				}
			}else{
				return "0,00";
			}
				
	}
	public boolean allowRenovacaoComBoletosAdiantados(Integer codAcesso, Integer codPlanoNovo){
		
		AcessoCliente ac = find(codAcesso);
		PlanoAcesso planoNovo = em.find(PlanoAcesso.class, codPlanoNovo);
		
		if(ac != null && planoNovo != null){
			
			double vlrPlanoAntigo = Real.formatStringToDBDouble(ac.getPlano().getValor());
			double vlrPlanoNovo = Real.formatStringToDBDouble(planoNovo.getValor());
			
			if(vlrPlanoNovo < vlrPlanoAntigo){
				
				ContasReceberDAO crDAO = new ContasReceberDAO();
				List<ContasReceber> boletosAbertos = crDAO.buscarTitulosNaoVencidosDeAcessoPorContrato(ac.getId());
				
				if(boletosAbertos !=null && boletosAbertos.size() > 0){
					for (ContasReceber contasReceber : boletosAbertos) {
						if(contasReceber.getStatus().equals("FECHADO")){
							
							return false;
						}
					}
				}
				
				return true;
				
			}else{
				return true;
			}
		}else{
			return false;
		}				
	}
	
	public boolean alterarPlanoAcesso(AcessoCliente contrato,String excluirBoletosExistentes, PlanoAcesso planoNovo){
		
		try{
									
			//Inicia TransaçãoalterarPlanoAcesso
			em.getTransaction().begin();
			
			
			if(!contrato.getContrato().getTipo_contrato().equals("GRATIS")){
				
				List<ContasReceber> resConta = ContasReceberDAO.buscarTitulosNaoVencidosDeAcessoPorContrato(contrato.getId());		
				ContasReceber proximoBoleto = resConta.get(0);
				Date dtVencimentoBoleto = proximoBoleto.getData_vencimento();
							
				String valorBoleto = "";			
				valorBoleto = calcDiferencaValorPlano(contrato.getId(), planoNovo.getId());	
								
				//Verifica se Existem Boletos Em Abertos-Não Vencidos					
				if(excluirBoletosExistentes != null && excluirBoletosExistentes.equals("SIM")){
					
					if(contrato != null){		
						
						List<ContasReceber> boletosAbertosNaoVencidos = ContasReceberDAO.buscarTitulosAbertosNaoVencidosDeAcessoPorContrato(contrato.getId());
						
						if(boletosAbertosNaoVencidos != null){
							for(ContasReceber cr: boletosAbertosNaoVencidos){							
								em.remove(cr);							
							}
						}
					}
				}
				
				contrato.setData_venc_contrato(calcularDataVencContrato(contrato.getContrato().getId(), dtVencimentoBoleto));
			
				gerarBoletosAcesso(contrato.getCliente().getId(),contrato, contrato.getContrato(), planoNovo, dtVencimentoBoleto, valorBoleto,"ALTERAR_PLANO");	
			}
			
			
			String carencia = "";
			if(contrato.getContrato().getCarencia().equals("SIM")){
				//Meses da Atual Carencia			
				DateTime dt1 = new DateTime(new Date());
				DateTime dt2 = new DateTime(contrato.getData_venc_contrato());
				
				Integer meses = Months.monthsBetween(dt1, dt2).getMonths();

				if(meses>1){
					//SIM
					carencia = "SIM";
					
				}else{
					//NAO
					if(contrato.getPlano().getId() == planoNovo.getId()){
						carencia = "NAO";
					}else{
						carencia = "SIM";
					}								
				}
			}else{						
				carencia = "NAO";
			}
				
		
			Integer vigencia1 = contrato.getContrato().getVigencia();							
			String tipoContrato = contrato.getContrato().getTipo_contrato();
			Date dataVencContratoNova = new Date();
			
			if(!tipoContrato.equals("PRE-PAGO")){
				DateTime date1 = new DateTime(new Date());
				LocalDate localDate = new LocalDate(date1);
				LocalDate moreDays = localDate.plusMonths(vigencia1);				
				dataVencContratoNova = moreDays.toDate();
			}			
			
			contrato.setData_alteracao_plano(new Date());
			contrato.setPlano(planoNovo);
						
			contrato.setCarencia(carencia);

			//Atualiza Plano no Radius
			String nomePlanoRadius = contrato.getContrato().getId().toString()+"_"+planoNovo.getNome();
			alterarPlanoLoginRadius(contrato.getLogin(), nomePlanoRadius);					

			
			//Atualiza Contrato
			em.merge(contrato);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();				
			}
			e.printStackTrace();
			return false;
		}		
	}
	
	
	
	
	
	
	public static void alterarPlanoLoginRadius(String login, String groupname) throws Exception{		
		
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select rug from RadUserGgroup rug where rug.username = :username",RadUserGgroup.class);
		q.setParameter("username", login);
		
		if(q.getResultList().size() > 0){
			List<RadUserGgroup> result = q.getResultList();
			for(RadUserGgroup rug: result){								
				em.remove(rug);				
			}			
		}
		
		//Cadastra Novo Plano
		if(login!=null){
			em.persist(new RadUserGgroup(null, login, groupname, "1"));			
		}
	}
	public void encerrarContrato(Integer codAcesso){
		
		boolean status;
		
		//Mudar Status para ENCERRADO
		AcessoCliente ac = em.find(AcessoCliente.class, codAcesso);
		if(ac.getStatus_2().equals("PENDENTE_INSTALACAO")){
			status = true;
		}else{
			status = false;
			
		}			
		ac.setStatus_2("ENCERRADO");
		ac.setUltimo_endereco_mac(ac.getEndereco_mac());
		String mac = ac.getEndereco_mac();
		ac.setEndereco_mac(null);
		
		//-----Calcular multa,beneficio;
		//----------------------------------------------
		String carencia = "";
		Integer months = 0;
		if(ac.getCarencia() != null && ac.getCarencia().equals("SIM") && ac.getContrato().getCarencia().equals("SIM")){			
			
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");				
				DateTime dt1 = new DateTime(sdf.parse(sdf.format(new Date())));
				DateTime dt2 = new DateTime(sdf.parse(sdf.format(ac.getData_venc_contrato()))).plusMonths(1);			 
				months = Months.monthsBetween(dt1, dt2).getMonths();				
				carencia = months.toString()+" MESES";
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(months > 0){
				carencia = months.toString()+" MESES";
			}else{
				carencia = "SEM CARENCIA";
			}
		}else{
			carencia = "SEM CARENCIA";
		}
		
		String bc = "0,00";
		String bi = "0,00";
		
		if(!carencia.equals("SEM CARENCIA")){
			bc = Real.formatDbToString(String.valueOf(ac.getValor_beneficio_comodato()));												
		}
		
		if(ac.getRegime().equals("PROPRIO")){
			bc = "0,00";
		}
		
		if(ac.getInstalacao_gratis() != null && ac.getInstalacao_gratis().equals("SIM")){
			
			if(ac.getCarencia() != null && ac.getCarencia().equals("SIM")){		

				EntityManager em = ConnUtil.getEntity();
				Query qValorBenfInst = em.createNativeQuery("SELECT ts.VALOR FROM ose o, tipos_ose g, tipos_subgrupo ts WHERE "
						+ "o.GRUPO_ID = g.ID AND ts.ID = "
						+ "o.TIPO_SUBGRUPO_ID AND g.NOME LIKE '%INSTALACAO%' AND o.ACESSO_CLIENTE_ID = :contrato ");
				qValorBenfInst.setParameter("contrato", ac.getId());
				
				if(qValorBenfInst.getResultList().size() > 0){
					bi = Real.formatDbToString(qValorBenfInst.getResultList().toArray()[0].toString());
				}
			}else{
				bi = "0,00";
			}
		}else{
			bi = "0,00";
		}
		
		int qtd_boletos_abertos = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(ac.getId()) != null ? ContasReceberDAO.procurarBoletosDoAcessoPorContrato(ac.getId()).size() : 0;
		double valor_multa = 0;
		double valor_adesao = !carencia.equals("SEM CARENCIA") ? ac.getValor_beneficio_adesao() : 0;
										 //!carencia.equals("SEM CARENCIA")?Real.formatDbToString(String.valueOf(ac.getValor_beneficio_adesao())):"0,00"
		double valor_equipamento = Real.formatStringToDBDouble(bc);
		double valor_instalacao =  Real.formatStringToDBDouble(bi);
		double valor_total = valor_adesao+valor_equipamento+valor_instalacao;
		
		if(qtd_boletos_abertos >= 12){
			valor_multa = valor_total;
		}else if(qtd_boletos_abertos == 11){
			valor_multa = (valor_total*91.67)/100;
		}else if(qtd_boletos_abertos == 10){
			valor_multa = (valor_total*83.34)/100;
		}else if(qtd_boletos_abertos == 9){
			valor_multa = (valor_total*75.01)/100;
		}else if(qtd_boletos_abertos == 8){
			valor_multa = (valor_total*66.68)/100;
		}else if(qtd_boletos_abertos == 7){
			valor_multa = (valor_total*58.35)/100;
		}else if(qtd_boletos_abertos == 6){
			valor_multa = (valor_total*50.02)/100;
		}else if(qtd_boletos_abertos == 5){
			valor_multa = (valor_total*41.69)/100;
		}else if(qtd_boletos_abertos == 4){
			valor_multa = (valor_total*33.36)/100;
		}else if(qtd_boletos_abertos == 3){
			valor_multa = (valor_total*25.03)/100;
		}else if(qtd_boletos_abertos == 2){
			valor_multa = (valor_total*16.70)/100;
		}else if(qtd_boletos_abertos == 1){
			valor_multa = (valor_total*8.37)/100;
		}
		
		ac.setValor_multa_pos_encerramento(Real.formatDbToString(String.valueOf(valor_multa)));
		ac.setValor_adesao_pos_encerramento(Real.formatDbToString(String.valueOf(valor_adesao)));
		ac.setValor_equipamento_pos_encerramento(Real.formatDbToString(String.valueOf(valor_equipamento)));
		ac.setValor_instalacao_pos_encerramento(Real.formatDbToString(String.valueOf(valor_instalacao)));
		ac.setValor_total_pos_encerramento(Real.formatDbToString(String.valueOf(valor_total)));
		
		//-----Calcular multa,beneficio;
		//----------------------------------------------
		
		em.getTransaction().begin();
		em.merge(ac);
		
		//Desconectar Cliente
		if(ac.getBase() != null){
			MikrotikUtil.desconectarCliente(ac.getBase().getUsuario(), ac.getBase().getSenha(), ac.getBase().getEndereco_ip(), Integer.parseInt(ac.getBase().getPorta_api()), ac.getLogin());
		}

		
		//Excluir Registros das Tabelas RadCHECK.
		Query qrrc = em.createQuery("select rc from RadCheck rc where rc.username = :usuario", RadCheck.class);
		qrrc.setParameter("usuario", ac.getLogin());
		
		if(qrrc.getResultList().size() >0){
			
			List<RadCheck> credencias = qrrc.getResultList(); 
			for(RadCheck rc:credencias){
				em.remove(rc);
			}
		}
		
		//Excluir Registros das Tabelas RadUSerGroup.
		Query qrug = em.createQuery("select rug from RadUserGgroup rug where rug.username = :usuario", RadUserGgroup.class);
		qrug.setParameter("usuario", ac.getLogin());
		
		if(qrug.getResultList().size() >0){
			
			List<RadUserGgroup> planos = qrug.getResultList(); 
			for(RadUserGgroup rug:planos){
				em.remove(rug);
			}
		}
		
		
		//Excluir Registros das Tabelas RadReply.
		Query qrr = em.createQuery("select rr from RadReply rr where rr.username = :usuario", RadReply.class);
		qrr.setParameter("usuario", ac.getLogin());
				
		if(qrr.getResultList().size() >0){
					
			List<RadReply> marcacoes = qrr.getResultList(); 
			for(RadReply rr:marcacoes){
				em.remove(rr);
			}
		}
		
		
		
		
		em.getTransaction().commit();
		
		//Remover MAC do Access List do Concentrador
		if(!status){		
			MikrotikUtil mUtil = new MikrotikUtil();
			
			mUtil.removerAccessList(ac.getBase().getUsuario(), ac.getBase().getSenha(), ac.getBase().getEndereco_ip(), 
			Integer.parseInt(ac.getBase().getPorta_api()), ac.getCliente().getNome_razao(), mac);
			
			//Verifica Palavras na Base Antiga
			List<FiltroAcesso>  filtros= getFiltrosAcesso(ac.getId());
			if(filtros.size() > 0){
				for (FiltroAcesso filtro : filtros) {
								
					MikrotikUtil.removerMarcacaoFirewallFiltroBloqContext(ac.getBase().getUsuario(), ac.getBase().getSenha(), 
							ac.getBase().getEndereco_ip(), Integer.parseInt(ac.getBase().getPorta_api()),ac.getId().toString(), ac.getCliente().getNome_razao(), filtro.getPalavra());
			
					
				}
			}
			
		}	
		
		
		
		//Estornar Produto
		//Alterar Quantidade de Produto do Material Escolhido	
		
		if(ac != null && ac.getContrato() != null && ac.getRegime().equals("COMODATO (TOTAL)") && ac.getMaterial() != null){
			//-----Logg
			LogProdutoDAO.registraLog(new LogProduto(null,ac.getMaterial(), "ENCERRAMENTO CONTRATO COMODATO",ac.getMaterial().getQtdEstoque(),ac.getMaterial().getQtdEstoque()+1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
			//-----Logg
			
			ProdutoDAO pDAO = new ProdutoDAO();

			pDAO.estornarProdutoQtdDeposito(ac.getMaterial(),new Float(1),"A");
			pDAO.estornarProdutoQtdDeposito(ac.getOnu(),new Float(1),"A");
			
			SerialDAO serialDAO = new SerialDAO();
			SerialProduto serialRoteador = serialDAO.findByNameAndCod(ac.getMaterial(),mac);
			SerialProduto serialOnu = serialDAO.findByNameAndCod(ac.getOnu(), ac.getOnu_serial());
			
				boolean emUso = getAcessoBySerialMAC(ac.getUltimo_endereco_mac(),ac.getMaterial().getId(),codAcesso);

				if(serialRoteador != null ){
					
					serialRoteador.setStatus("ATIVO");
					serialRoteador.setData_alteracao(new Date());
					serialDAO.atualizaeSerial(serialRoteador);
					
					AlteracoesSerialDAO.save(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" ESTORNO", serialRoteador,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));

				}else if(serialRoteador == null && !emUso){
					serialDAO.addNovoSerial(ac.getId(),ac.getMaterial().getId(), mac);
				}
				
				if(serialOnu != null ){
					
					serialOnu.setStatus("ATIVO");
					serialOnu.setData_alteracao(new Date());
					serialDAO.atualizaeSerial(serialOnu);
					
					AlteracoesSerialDAO.save(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" ESTORNO", serialOnu,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));

				}else if(serialOnu == null && !emUso){
					serialDAO.addNovoSerial(ac.getId(),ac.getOnu().getId(), ac.getOnu_serial());
				}
			
		}
		
		if(ac != null && ac.getContrato() != null && ac.getRegime().equals("COMODATO") && ac.getMaterial() != null){
		
			//-----Logg
			LogProdutoDAO.registraLog(new LogProduto(null,ac.getMaterial(), "ENCERRAMENTO CONTRATO COMODATO",ac.getMaterial().getQtdEstoque(),ac.getMaterial().getQtdEstoque()+1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
			//-----Logg
			
			ProdutoDAO pDAO = new ProdutoDAO();
			//ac.getMaterial().setQtdEstoque(ac.getMaterial().getQtdEstoque()+1);
			pDAO.estornarProdutoQtdDeposito(ac.getMaterial(),new Float(1),"A");		
			
			SerialDAO serialDAO = new SerialDAO();
			SerialProduto serial = serialDAO.findByNameAndCod(ac.getMaterial(), mac);
			
			boolean emUso = getAcessoBySerialMAC(ac.getUltimo_endereco_mac(),ac.getMaterial().getId(),codAcesso);

				if(serial != null && !emUso){
					
					serial.setStatus("ATIVO");
					serial.setData_alteracao(new Date());
					serialDAO.atualizaeSerial(serial);
					
					AlteracoesSerialDAO.save(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" ESTORNO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));

				}else if(serial == null && !emUso){
					serialDAO.addNovoSerial(ac.getId(),ac.getMaterial().getId(), mac);
				}
		}	
		
		if(ac != null && ac.getContrato() != null && ac.getRegime().equals("PROPRIO (PARCIAL)") && ac.getOnu() != null){
			
			//-----Logg
			LogProdutoDAO.registraLog(new LogProduto(null,ac.getOnu(), "ENCERRAMENTO CONTRATO PROPRIO (PARCIAL)",ac.getOnu().getQtdEstoque(),ac.getOnu().getQtdEstoque()+1, new Date(), OpusERP4UI.getUsuarioLogadoUI().getUsername()));
			//-----Logg
			
			ProdutoDAO pDAO = new ProdutoDAO();
			//ac.getOnu().setQtdEstoque(ac.getOnu().getQtdEstoque()+1);
			pDAO.estornarProdutoQtdDeposito(ac.getOnu(),new Float(1), "A");		
			
			SerialDAO serialDAO = new SerialDAO();
			SerialProduto serial = serialDAO.findByNameAndCod(ac.getOnu(), ac.getOnu_serial());
			
			boolean emUso = getAcessoBySerialSerialOnu(ac.getUltimo_endereco_mac(),ac.getOnu().getId(),codAcesso);

			if(serial != null && !emUso){
				
				serial.setStatus("ATIVO");
				serial.setData_alteracao(new Date());
				serialDAO.atualizaeSerial(serial);
				
				AlteracoesSerialDAO.save(new AlteracoesSerial(null, "ACESSO "+ac.getId()+" ESTORNO", serial,OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), new Date()));

			}else if(serial == null && !emUso){
				serialDAO.addNovoSerial(ac.getId(),ac.getOnu().getId(), ac.getOnu_serial());
			}
		}
	}
	
	
	public AcessoCliente find(Integer codAcesso){
		
		AcessoCliente ac = em.find(AcessoCliente.class, codAcesso);
		if(ac != null){
			return ac;
		}else{
			return null;
		}
	}

	
	
	public static void alterarCredenciaisRadius(String statusContrato, String usernameAntigo,String username, String senha, String gruoupName,
			String nomeCliente, String mac, String ip){
		
		EntityManager em = ConnUtil.getEntity();
				
		Query qRadCheck = em.createQuery("select ac from RadCheck ac where ac.username = :usuario");
		qRadCheck.setParameter("usuario", usernameAntigo);
		
		Query qRadRaply = em.createQuery("select ac from RadReply ac where ac.username = :usuario", RadReply.class);
		qRadRaply.setParameter("usuario", usernameAntigo);
		
		Query qRadUserGroup = em.createQuery("select ac from RadUserGgroup ac where ac.username = :usuario", RadUserGgroup.class);
		qRadUserGroup.setParameter("usuario", usernameAntigo);
		
		Query qRadAcct = em.createQuery("select ac from RadAcct ac where ac.username = :usuario", RadAcct.class);
		qRadAcct.setParameter("usuario", usernameAntigo);
		
		//Remove todas Linhas com o Username Antigo da Tabela RadUserGroup
		if(qRadAcct.getResultList().size() > 0){
			
			List<RadAcct> result = qRadAcct.getResultList();
			
			for(RadAcct r:result){
				r.setUsername(username);
				em.merge(r);
			}
		}
		if(qRadUserGroup.getResultList().size() > 0){
			
			List<RadUserGgroup> result = qRadUserGroup.getResultList();
			
			for(RadUserGgroup r:result){
				//r.setUsername(username);
				//em.merge(r);
				em.remove(r); 
			}
		}
		
		//Remove todas Linhas com o Username Antigo da Tabela RadCheck
		if(qRadCheck.getResultList().size() > 0){
			
			List<RadCheck> result = qRadCheck.getResultList();
			
			for(RadCheck r:result){
				em.remove(r);
			}
		}
		
		
		//Remove todas Linhas com o Username Antigo da Tabela RadRaply
		if(qRadRaply.getResultList().size() > 0){
			
			List<RadReply> result = qRadRaply.getResultList();
			
			for(RadReply r:result){
				em.remove(r);
			}
		}
		
		//Credenciais na Tabela RadCheck
		em.persist(new RadCheck(null, username, "Password", "==", senha));
		em.persist(new RadCheck(null, username, "Calling-Station-ID", "==", mac));
		
		//Plano na Tabela RadGroupReply
		em.persist(new RadUserGgroup(null, username, gruoupName, "1"));
				
		//Marcação na Tabela RadReply
		//em.persist(new RadReply(null, username, "Mikrotik-Address-List", "=", "IP_"+nomeCliente));
		
		if(statusContrato.equals("BLOQUEADO")){
			em.persist(new RadReply(null, username, "Framed-Pool", "=", "BLOQUEADO"));
		}		
		
		if(ip != null && !ip.equals("") && !ip.isEmpty()){
			em.persist(new RadReply(null, username, "Framed-IP-Address", "=", ip));
		}
		
		
		
	}
	
	
	
	public static void alterarCredenciaisRadiusMac(String usernameAntigo,String username, String mac){
		
		EntityManager em = ConnUtil.getEntity();
				
		Query q = em.createQuery("select ac from RadCheck ac where ac.username = :usuario and ac.attribute = 'Calling-Station-ID'");
		q.setParameter("usuario", usernameAntigo);
		
		//Remove todas Linhas com o Username Antigo
		if(q.getResultList().size() > 0){
			
			List<RadCheck> result = q.getResultList();
			
			for(RadCheck r:result){
				em.remove(r);
			}
		}

		if(mac != null){
			em.persist(new RadCheck(null, username, "Calling-Station-ID", "==", mac));
		}
		
	}

	public void alterarNovaCredenciaisRadiusMac(String usernameAntigo,String username, String mac){
		
//		em.getTransaction().begin();
		
		Query q = em.createQuery("select ac from RadCheck ac where ac.username = :usuario and ac.attribute = 'Calling-Station-ID'");
		q.setParameter("usuario", usernameAntigo);
		
		//Remove todas Linhas com o Username Antigo
		if(q.getResultList().size() > 0){
			
			List<RadCheck> result = q.getResultList();
			
			for(RadCheck r:result){
				em.remove(r);
			}
		}
		
		//Credenciais na Tabela RadCheck
//		em.persist(new RadCheck(null, username, "Password", "==", senha));
		em.persist(new RadCheck(null, username, "Calling-Station-ID", "==", mac));
			
		//TODO Remover todas as Linhas da Tabela RadUserGroup e Radreply e Cadastralas Novamente
		
//		em.getTransaction().commit();
		
	}
	
	public void alterarNovaCredenciaisRadiusMacTrans(String usernameAntigo,String username, String mac){
		
		em.getTransaction().begin();
		
		Query q = em.createQuery("select ac from RadCheck ac where ac.username = :usuario and ac.attribute = 'Calling-Station-ID'");
		q.setParameter("usuario", usernameAntigo);
		
		//Remove todas Linhas com o Username Antigo
		if(q.getResultList().size() > 0){
			
			List<RadCheck> result = q.getResultList();
			
			for(RadCheck r:result){
				em.remove(r);
			}
		}
		
		//Credenciais na Tabela RadCheck
//		em.persist(new RadCheck(null, username, "Password", "==", senha));
		em.persist(new RadCheck(null, username, "Calling-Station-ID", "==", mac));
			
		//TODO Remover todas as Linhas da Tabela RadUserGroup e Radreply e Cadastralas Novamente
		
		em.getTransaction().commit();
		
	}
	
	
	public static void cadastrarCredenciaisRadius(String nomeCliente,String username, String senha,String ip, String groupname,String mac){
		
		EntityManager em = ConnUtil.getEntity();
		
		Query q1 = em.createQuery("select r from RadCheck r where r.username LIKE:u", RadCheck.class);
		q1.setParameter("u", username);		
		for (RadCheck r : (List<RadCheck>)q1.getResultList()) {
				em.remove(r); 
		}
		
		Query q2 = em.createQuery("select rug from RadUserGgroup rug where rug.username LIKE:u", RadUserGgroup.class);
		q2.setParameter("u", username);
		for (RadUserGgroup r : (List<RadUserGgroup>)q2.getResultList()) {
				em.remove(r); 
		}
		
		Query q3 = em.createQuery("select rr from RadReply rr where rr.username LIKE:u", RadReply.class);
		q3.setParameter("u", username);
		for (RadReply r: (List<RadReply>)q3.getResultList()) {
			em.remove(r); 
		}
			
		
		//Credenciais na Tabela RadCheck
		em.persist(new RadCheck(null, username, "Password", "==", senha));
		em.persist(new RadCheck(null, username, "Calling-Station-ID", "==", mac));
		
		//Plano na Tabela RadGroupReply
		em.persist(new RadUserGgroup(null, username, groupname, "1"));
		
		//Marcação na Tabela RadReply
		//em.persist(new RadReply(null, username, "Mikrotik-Address-List", "=", "IP_"+nomeCliente));
		if(ip != null && !ip.equals("") && !ip.isEmpty()){
			em.persist(new RadReply(null, username, "Framed-IP-Address", "=", ip));
		}
		
		
		
	}

	public static void cadastrarTrocaMaterial(Produto codMaterialAntigo,Produto codMaterialNovo, String motivo,AcessoCliente codAcesso, Date dataTroca){
		
		EntityManager em = ConnUtil.getEntity();
		em.persist(new RegistroTrocaMaterial(codMaterialAntigo, codMaterialNovo, motivo, codAcesso, dataTroca));
		
	}
	
	public void cadastrarNovaTrocaMaterial(Produto codMaterialAntigo,Produto codMaterialNovo, String motivo,AcessoCliente codAcesso, Date dataTroca){
		
		 //em.getTransaction().begin();		
		//cadastrar Troca de MAterial
		em.persist(new RegistroTrocaMaterial(codMaterialAntigo, codMaterialNovo, motivo, codAcesso, dataTroca));		
		//em.getTransaction().commit();
	}
	
	
	public boolean usernameDisponibility(String username){
		
		Query q = em.createQuery("select ac from AcessoCliente ac where ac.login = :username");
		q.setParameter("username", username);
		
		if(q.getResultList().size()>0){
			return false;
		}else{
			return true;
		}
	}
	
//	public boolean ipDisponibility(String ip){
//		
//		Query q = em.createQuery("select ac from AcessoCliente ac where ac.endereco_ip = :ip");
//		q.setParameter("ip", ip);
//		
//		if(q.getResultList().size()>0){
//			return false;
//		}else{
//			return true;
//		}
//	}
//	
	public static void gerarBoletosAcesso(Integer codCliente,AcessoCliente contratoAcesso, ContratosAcesso contrato, PlanoAcesso plano, Date dataBoleto, 	String valorPrimieiroBoleto, String tipo) throws Exception{
						
		Integer vigencia = contrato.getVigencia();
		//Float resultAdesao = Float.parseFloat(String.valueOf(contratoAcesso.getValor_beneficio_adesao())) - Float.parseFloat(Real.formatStringToDB(contrato.getValor_desconto()));
		String valorAdesao = "0,00";
				
		String valorPlano = plano.getValor();		
		Date dataVenc = dataBoleto;	 	

		String controle = "";
		if(contrato.getTipo_contrato().equals("POS-PAGO")){
			controle = "ACESSO-POS";
		}else{
			controle = "ACESSO-PRE";
		}
		
		if(tipo.equals("LIBERAR_BOLETO")){
			ContasReceberDAO.gerarBoletosAcesso(codCliente, contratoAcesso, valorPrimieiroBoleto, valorPlano, dataVenc, vigencia, valorAdesao, controle, plano.getId());
		}
		
		if(tipo.equals("ALTERAR_PLANO")){
			ContasReceberDAO.gerarBoletosAcesso2(codCliente, contratoAcesso, valorPrimieiroBoleto, valorPlano, dataVenc, vigencia, valorAdesao, controle, plano.getId());
		}
	}
	
	public static Date calcularDataVencContrato(Integer codContrato, Date dataPrimeiroBoleto){
		
		EntityManager em  = ConnUtil.getEntity();
		ContratosAcesso ca = em.find(ContratosAcesso.class, codContrato);
		
		Date dataReturn = null;
		
		if(ca != null){
			if(ca.getTipo_contrato().equals("PRE-PAGO")){
				dataReturn = dataPrimeiroBoleto;
			}else{
				DateTime date1 = new DateTime(dataPrimeiroBoleto);
				LocalDate localDate = new LocalDate(date1);
				LocalDate moreDays = localDate.plusMonths(ca.getVigencia()-1);
				dataReturn = moreDays.toDate();			
			}
		}
		
		
		return dataReturn;
	}

//	public function pegarDataVencMensalidadeAcesso($codAcesso){
//    	$acessoClienteModel = new AcessoCliente();
//    	$res = $acessoClienteModel->find($codAcesso);
//    	if($res->count() > 0){
//    		$rowAcesso = $res->current()->toArray();
//    		$codCliente = $rowAcesso['CLIENTES_ID'];
//    		$contasReceberModel = new ContasReceber();
//    		$resContas = $contasReceberModel->buscarBoletosPorCodClienteStatus($codCliente);
//    		if($resContas->count() > 0){
//    			$rowContas = $resContas->toArray();
//    			$aux = explode('-', $rowContas[0]['DATA_VENCIMENTO']);
//    			$dataUltimoBoleto = $aux[2].'/'.$aux[1].'/'.$aux[0];
//    			return $dataUltimoBoleto;
//    		}else{
//    			return '';
//    		}
//    	}
//    }
	public Date buscarDataVencBoleto(Integer codAcesso){

		AcessoCliente acCliente = em.find(AcessoCliente.class, codAcesso);		
		List<ContasReceber> resConta = ContasReceberDAO.procurarBoletosDoAcessoPorContrato(acCliente.getId());
		
		Date dataReturn = null;
		if(resConta!=null){
			ContasReceber ct = resConta.get(0);
			dataReturn = ct.getData_vencimento();		
		}		
		return dataReturn;
	}

	
	public String calcularValorPrimeiroBoleto(Integer codContrato,Integer codPlano,Date dataInstalacao, Date dataPrimeiroBoleto)
	{
		try{
//				Date dataAtual = new Date();							
				
				//Pegar Valor do Plano
				PlanoAcesso plano = em.find(PlanoAcesso.class, codPlano);
				
				String valorBoleto = null;
				if(plano != null){
					valorBoleto = plano.getValor();
				}
				
				//Pega Tipo do Contrato
				ContratosAcesso contrato = em.find(ContratosAcesso.class, codContrato);
				
				String tipoContrato = null;
				if(contrato != null){
					tipoContrato = contrato.getTipo_contrato();
				}

				if (tipoContrato.equals("POS-PAGO"))
				{				
					Date dataProxVencBoleto = dataPrimeiroBoleto;
					DateTime dataAnterior = new DateTime(dataProxVencBoleto).minusMonths(1);
					
					Integer qtdDias = Days.daysBetween(dataAnterior, new DateTime(dataProxVencBoleto)).getDays(); //Dias do Mês
					Integer qtdDiasUtilizados = Days.daysBetween(new DateTime(dataInstalacao), new DateTime(dataProxVencBoleto)).getDays();
										
					double vlrDoDiaPlano = Real.formatStringToDBDouble(plano.getValor()) / qtdDias; //Valor Diário
					double vlrDosDiasTotal = vlrDoDiaPlano * qtdDiasUtilizados;
					
					valorBoleto = String.valueOf(vlrDosDiasTotal);
				}else{
					valorBoleto = Real.formatStringToDB(valorBoleto);
				}
				
				return valorBoleto;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	
		
	}
	
	public String getNextID(){
		try{
			em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'acesso_cliente'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Erro ao tentar pegar o próximo ID do Acesso: "+e.getMessage());
			return null;
		}
	}
	
	public RadAcct buscarRadacct(String username){
		em = ConnUtil.getEntity();		
		
		Query q = em.createQuery("select rad from RadAcct rad where rad.username =:username and rad.framedprotocol =:PPP ORDER BY rad.acctstarttime DESC",RadAcct.class);		

		q.setParameter("username", username);
		q.setParameter("PPP","PPP");
		RadAcct result = null;
		
		if(q.getResultList().size()>0){
			result = (RadAcct) q.getResultList().get(0);
			return result;
		}else{
			return null;
		}	
	}
	
	public RadAcct buscarbyUserRadacct(String username){
		em = ConnUtil.getEntity();		
		
		Query q = em.createQuery("select rad from RadAcct rad where rad.username =:username ORDER BY rad.acctstarttime DESC",RadAcct.class);				
		q.setParameter("username", username);
		RadAcct result = null;
		
		if(q.getResultList().size()>0){
			result = (RadAcct) q.getResultList().get(0);
			if(result.getAcctsessiontime().equals(0)){
				return result;				
			}else{
				return null;
			}
		}else{
			return null;
		}	
	}
	
	public void atualizarRadacct(String username){
		em = ConnUtil.getEntity();		
		
		Query q = em.createQuery("select rad from RadAcct rad where rad.username =:username ORDER BY rad.acctstarttime DESC",RadAcct.class);				
		q.setParameter("username", username);
		RadAcct result = null;
		
		if(q.getResultList().size()>0){
			result = (RadAcct) q.getResultList().get(0);
			if(result.getAcctsessiontime().equals(0)){
				result.setAcctstoptime(new Date());		
				em.persist(result);
			}
		}	
	}
	
	public List<FiltroAcesso> buscarFiltrobyCodAcesso(Integer codAcesso){
		em = ConnUtil.getEntity();		
		
		Query q = em.createQuery("select f from FiltroAcesso f where f.cod_contrato =:codAcesso",FiltroAcesso.class);		
		
		q.setParameter("codAcesso", codAcesso);
		
		if(q.getResultList().size()>0){
			return  q.getResultList();
		}else{
			return null;
		}
		
		
	}
	
}
