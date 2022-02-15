package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.EcfPreVendaDetalhe;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.EnderecoCobranca;
import com.digital.opuserp.domain.EnderecoEntrega;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.Osi;
import com.digital.opuserp.domain.RadReply;
import com.digital.opuserp.domain.RadUserGgroup;
import com.digital.opuserp.domain.VendaServicoCabecalho;
import com.digital.opuserp.domain.VendaServicoDetalhe;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.MikrotikUtil;
import com.digital.opuserp.util.mk_bean.AccessList;

public class ScriptDAO {
	
	
	public static void derrucarTodosBloqueados(){
		
		
		
		
	}
	
	public static void corrigirPlanosEMarcacoesClientesBloqueados(){
		
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select a from AcessoCliente a where a.status_2 = :status", AcessoCliente.class);
		q.setParameter("status", "BLOQUEADO");
		
		for(AcessoCliente a :(List<AcessoCliente>) q.getResultList()){
			System.out.println("CORRIGINDO CLIENTE: "+a.getLogin());
			String nomePlano = a.getContrato().getId().toString()+"_"+a.getPlano().getNome();
			
			Query qUserGroup = em.createQuery("select plano from RadUserGgroup plano where plano.username =:username", RadUserGgroup.class);
			qUserGroup.setParameter("username", a.getLogin());
			
			Query qReply = em.createQuery("select r from RadReply r where r.username =:username and r.value=:value", RadReply.class);
			qReply.setParameter("username", a.getLogin());
			qReply.setParameter("value", "bloqueado");
			
					
			em.getTransaction().begin();
			
			
			for(RadUserGgroup plano:(List<RadUserGgroup>) qUserGroup.getResultList()){
				em.remove(plano); 
				
				RadUserGgroup novoPlano = new RadUserGgroup(null, a.getLogin(), nomePlano,"1");				
				em.persist(novoPlano); 
			}
			
			for(RadReply rr: (List<RadReply>) qReply.getResultList()){
				em.remove(rr); 
				
				RadReply NovoRR = new RadReply(null, a.getLogin(), "Framed-Pool", "=", "BLOQUEADO");
				em.persist(NovoRR); 
			}
			
			em.getTransaction().commit();
			
			
		}
	}
	
		
	public static void retirarCopia(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from Cliente c where c.empresa_anterior = 2", Cliente.class);
		
		for (Cliente c : (List<Cliente>)q.getResultList()) {
			c.setNome_razao(c.getNome_razao().replaceAll("COPIA", ""));
			em.getTransaction().begin();
			em.merge(c);
			em.getTransaction().commit();
			
			System.out.println("Cliente :"+c.getNome_razao()+" ATUALIZADO.");
			System.out.println("===============================================");
		}
	}
	
	public static void mesclarClientes(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from Cliente c where c.empresa_anterior = 2", Cliente.class);
		
		System.out.println(q.getResultList().size());
		for (Cliente c : (List<Cliente>)q.getResultList()) {
						
			//Verificar se Existe Outro Cliente na Mesma Empresa que Possua o Mesmo CPF ou CNPJ
			Query q2 = em.createQuery("select c from Cliente c where c.doc_cpf_cnpj =:cpf and c.empresa=:empresa and c.id !=:cod", Cliente.class);
			q2.setParameter("cpf", c.getDoc_cpf_cnpj());
			q2.setParameter("empresa", new Empresa(1));
			q2.setParameter("cod", c.getId());
			
			if(q2.getResultList().size() == 1){
				Cliente cliente1 = (Cliente)q2.getSingleResult();
				
				if(cliente1 != null){
					em.getTransaction().begin();
					
					
										
					Query p = em.createQuery("select p from EcfPreVendaCabecalho p where p.cliente =:cliente", EcfPreVendaCabecalho.class);
					p.setParameter("cliente", c);
					
					if(p.getResultList().size() > 0){
						for (EcfPreVendaCabecalho pedido : (List<EcfPreVendaCabecalho>)p.getResultList()) {
							pedido.setCliente(cliente1);
							System.out.println("Pedidos do Cliente: "+cliente1.getNome_razao()+" ATUALIZADO");
						}
					}
					
					Query conta = em.createQuery("select conta from ContasReceber conta where conta.cliente =:cliente", ContasReceber.class);
					conta.setParameter("cliente", c);
					
					if(conta.getResultList().size() > 0){
						for (ContasReceber boleto : (List<ContasReceber>) conta.getResultList()) {
							boleto.setCliente(cliente1);
							System.out.println("Boleto do Cliente: "+cliente1.getNome_razao()+" ATUALIZADO");
						}
					}
					
					Query crm = em.createQuery("select cr from Crm cr where cr.cliente =:cliente", Crm.class);
					crm.setParameter("cliente", c);
					
					if(crm.getResultList().size() > 0){
						for (Crm cr : (List<Crm>) crm.getResultList()) {
							cr.setCliente(cliente1);
							System.out.println("Crm do Cliente: "+cliente1.getNome_razao()+" ATUALIZADO");
						}
					}
					
					Query ose = em.createQuery("select ose from Ose ose where ose.cliente =:cliente", Ose.class);
					ose.setParameter("cliente", c);
					
					if(ose.getResultList().size() > 0){
						for (Ose os: (List<Ose>) ose.getResultList()) {
							os.setCliente(cliente1);
							System.out.println("Ose do Cliente: "+cliente1.getNome_razao()+" ATUALIZADO");
						}
					}
					
					Query osi = em.createQuery("select osi from Osi osi where osi.cliente =:cliente", Osi.class);
					osi.setParameter("cliente", c);
					
					if(osi.getResultList().size() > 0){
						for (Osi os: (List<Osi>) osi.getResultList()) {
							os.setCliente(cliente1);
							System.out.println("Osi do Cliente: "+cliente1.getNome_razao()+" ATUALIZADO");
						}
					}
					
					Query acesso = em.createQuery("select acesso from AcessoCliente acesso where acesso.cliente =:cliente", AcessoCliente.class);
					acesso.setParameter("cliente", c);
					
					if(acesso.getResultList().size() > 0){
						for (AcessoCliente ac: (List<AcessoCliente>) acesso.getResultList()) {
							ac.setCliente(cliente1);
							System.out.println("Acesso do Cliente: "+cliente1.getNome_razao()+" ATUALIZADO");
						}
					}

					em.merge(cliente1);
					
					
					//Apaga Endereços dos Clientes
					//Query end1 = em.createQuery("select end from Endereco end where end.clientes=:cliente", Endereco.class);
					Query end1 = em.createQuery("select ose from Endereco ose where ose.clientes =:cliente", Endereco.class);
					end1.setParameter("cliente", c);
					
					if(end1.getResultList().size() > 0){
						for (Endereco end : (List<Endereco>) end1.getResultList()) {
							em.remove(end);
						}
					}
					
					//Query end2 = em.createQuery("select end from EnderecoCobranca end where end.clientes=:cliente", EnderecoCobranca.class);
					Query end2 = em.createQuery("select ose from EnderecoCobranca ose where ose.cliente =:cliente", EnderecoCobranca.class);
					end2.setParameter("cliente", c);
					
					if(end2.getResultList().size() > 0){
						for (EnderecoCobranca end : (List<EnderecoCobranca>) end2.getResultList()) {
							em.remove(end);
						}
					}
					
					//Query end3 = em.createQuery("select end from EnderecoEntrega end where end.clientes=:cliente", EnderecoEntrega.class);
					Query end3 = em.createQuery("select ose from EnderecoEntrega ose where ose.cliente =:cliente", EnderecoEntrega.class);
					end3.setParameter("cliente", c);
					
					if(end3.getResultList().size() > 0){
						for (EnderecoEntrega end : (List<EnderecoEntrega>) end3.getResultList()) {
							em.remove(end);
						}
					}
							
					em.remove(c);
					em.getTransaction().commit();
					
					System.out.println("Cliente: "+c.getNome_razao()+" EXCLUÍDO");
					System.out.println("=================================================");
					System.out.println("Cliente: "+cliente1.getNome_razao()+" ATUALIZADO TABELAS: CONTAS_RECEBER, ECF_PREVENDA_CABECALHO, CRM");
					System.out.println("=================================================");
					
					
					
				}
			}
			
						
			//Optar por preservar o Cliente sem marcação

			//Transferir Boletos do Cliente com Marcação para o Cliente Sem Marcação se Houver : contas_receber
			//Somar e/ou Transferir Crédito de Cliente sem Marcação para Cliente Sem Marcação : credito_cliente
			
		}
	}
	
	
	public static void migrarVendaServico(){
		EntityManager em = ConnUtil.getEntity();
		Query q= em.createQuery("select v from VendaServicoCabecalho v where v.SITUACAO = 'F'  ", VendaServicoCabecalho.class);
		
		if(q.getResultList().size() > 0){
			
			for (VendaServicoCabecalho venda : (List<VendaServicoCabecalho>)q.getResultList()) {
												
					EcfPreVendaCabecalho pedido =	PedidoDAO.saveAndReturn(new EcfPreVendaCabecalho(null, 
								venda.getEMPRESA_ID(),								
								venda.getDATA_VENDA(), 
								venda.getHORA_VENDA(), 
								venda.getSITUACAO(), new Integer(0), 
								venda.getTOTAL_DESC(), venda.getTOTAL_ACREC(), 
								venda.getSUBTOTAL(), venda.getVALOR_TOTAL(), 
								venda.getVENDEDOR(), 
								"NAO", null, null, null, "PEDIDO" ,ClienteDAO.find(venda.getCLIENTES_ID()),"SERVICO"));
										
					System.out.println("Pedido de Servico: "+venda.getID()+" MIGRADO");
					
					Query qItensVendaServico = em.createQuery("select d from VendaServicoDetalhe d where d.VENDA_SERVICO_CABECALHO_ID =:venda_cab", VendaServicoDetalhe.class);
					qItensVendaServico.setParameter("venda_cab", venda.getID());
					
					if(qItensVendaServico.getResultList().size() > 0){
						
						for (VendaServicoDetalhe detalhe : (List<VendaServicoDetalhe>)qItensVendaServico.getResultList()) {
							PedidoDAO.saveItem(new EcfPreVendaDetalhe(null, detalhe.getID_PRODUTO(), 
									pedido.getId(), detalhe.getORDEM(), detalhe.getQUANTIDADE(), detalhe.getVALOR_UNITARIO(), detalhe.getVALOR_TOTAL(), 
									detalhe.getCANCELADO()));
							
							System.out.println("Item: "+detalhe.getID()+" CADASTRADO PARA O PEDIDO");
						}
						
					}				
			}
		}
	}
	
	public static void corrigirPedidos(){
		EntityManager em = ConnUtil.getEntity();
		
		em.getTransaction().begin();
		Query q = em.createNativeQuery("UPDATE ecf_pre_venda_cabecalho SET TIPO = 'PEDIDO' WHERE TIPO IS NULL");
		Query q2 = em.createNativeQuery("UPDATE ecf_pre_venda_cabecalho SET TIPO_VENDA = 'PRODUTO' WHERE TIPO_VENDA IS NULL");
		em.getTransaction().commit();
	}
	
	public static void corrigirEnderecos(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from Cliente c where c.endereco_principal is null", Cliente.class);
		
		em.getTransaction().begin();
		for (Cliente c :(List<Cliente>)q.getResultList()) {
			Query qEnd = em.createQuery("select e from Endereco e where e.clientes =:cliente", Endereco.class);
			qEnd.setParameter("cliente", c);
			
			if(qEnd.getResultList().size() == 1){
				c.setEndereco_principal((Endereco)qEnd.getSingleResult());
				em.merge(c);
			}
		}
		em.getTransaction().commit();
	}

	public static void atualizarTelefones(){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from Cliente c", Cliente.class);
		
		em.getTransaction().begin();
		for (Cliente c :(List<Cliente>)q.getResultList()) {
			if(c.getTelefone1().isEmpty() || c.getTelefone1() == null || c.getTelefone1().equals("")){
				if(c.getTelefone2() != null && !c.getTelefone2().equals("") && !c.getTelefone2().isEmpty()){
					c.setTelefone1(c.getTelefone2());
				}else if(c.getCelular1() != null && !c.getCelular1().equals("") && !c.getCelular1().isEmpty()){
					c.setTelefone1(c.getCelular1());
				}else if(c.getCelular2() != null && !c.getCelular2().equals("") && !c.getCelular2().isEmpty()){
					c.setTelefone1(c.getCelular2());
				}
			}
			
			if(c.getDdd_fone1() == null || c.getDdd_fone1().isEmpty() || c.getDdd_fone1().equals("")){
				if(c.getDdd_fone2() != null && !c.getDdd_fone2().isEmpty() && !c.getDdd_fone2().equals("")){
					c.setDdd_fone1(c.getDdd_fone2());
				}else if(c.getDdd_cel1() != null && !c.getDdd_cel1().isEmpty() && !c.getDdd_cel1().equals("")){
					c.setDdd_fone1(c.getDdd_cel1());
				}else if(c.getDdd_cel2() != null && !c.getDdd_cel2().isEmpty() && !c.getDdd_cel2().equals("")){
					c.setDdd_fone1(c.getDdd_cel2());
				}else{
					c.setDdd_fone1("81");
				}
			}
			
			em.merge(c);
			System.out.println(c.getNome_razao());
		}
		
		em.getTransaction().commit();
	}
	
	public static void atualizarRegimeAcessos(){
		
		
//		EntityManager em = ConnUtil.getEntityManager();
//		
//		Query q = em.createQuery("select a from AcessoCliente a", AcessoCliente.class);
//		
//		if(q.getResultList().size() >0){
//			
//			em.getTransaction().begin();
//			for (AcessoCliente a : (List<AcessoCliente>) q.getResultList()) {
//				
//				System.out.println(a.getId().toString());
//				if(a.getContrato() != null){				
//					a.setRegime(a.getContrato().getRegime());
//					em.merge(a);
//				}
//			}
//			em.getTransaction().commit();
//		}
		
		
	}
	
	public static void atualizarTipoPessoaClientes(){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select c from Cliente c", Cliente.class);
		
		if(q.getResultList().size()>0){
			
			em.getTransaction().begin();
			for (Cliente c: (List<Cliente>) q.getResultList()) {
				
				System.out.println(c.getNome_razao());
				
				if(c.getDoc_cpf_cnpj() != null && !c.getDoc_cpf_cnpj().isEmpty() && !c.getDoc_cpf_cnpj().equals("")){
					
					if(c.getDoc_cpf_cnpj().length() == 11){
						c.setTipo_pessoa("Pessoa Física");
					}else{
						c.setTipo_pessoa("Pessoa Jurídica");
					}					
					em.merge(c);
				}				
				
			}
			em.getTransaction().commit();
		}
	}
	
	
	public static void atualizarEnderecoPrincipalCliente(){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select e from Endereco e", Endereco.class);
		
				
		em.getTransaction().begin();
		for (Endereco end :(List<Endereco>) q.getResultList()) {
			
			System.out.println(end.getCep());
			if(end.isPrincipal()){
				end.getClientes().setEndereco_principal(end);
				em.merge(end.getClientes());
			}
		}
		em.getTransaction().commit();
		System.out.println("TErminou!");
	}
	
	public static void atualizarFiadoresAcesso(){
//		EntityManager em = ConnUtil.getEntityManager();
//		
//		Query q = em.createQuery("select a from AcessoCliente a", AcessoCliente.class);
//		
//		if(q.getResultList().size() > 0){
//			
//			em.getTransaction().begin();
//			for (AcessoCliente acesso : (List<AcessoCliente>) q.getResultList()) {
//				
//				Query qFiador = em.createQuery("select f from Fiadores f where f.CLIENTES_ID = :cliente", Fiadores.class);
//				qFiador.setParameter("cliente", acesso.getCliente().getId());
//				
//				if(qFiador.getResultList().size() == 1){
//					Fiadores fiador = (Fiadores) qFiador.getSingleResult();
//					acesso.setFiador(new Cliente(fiador.getCLIENTES_ID()));
//					em.merge(acesso);
//					
//					System.out.println("CONTRATO: "+acesso.getId().toString()+" FIADOR: "+acesso.getFiador().getId().toString());
//				}
//			}
//			em.getTransaction().commit();
//		}
	}
	
	public static void atualizarNdoc(){
		
		EntityManager em = ConnUtil.getEntity();
		Query q  = em.createQuery("select c from ContasReceber c where c.empresa_id = 1 and c.status != 'EXCLUIDO' and c.id > 52978",ContasReceber.class);
		
		for (ContasReceber conta : (List<ContasReceber>) q.getResultList()) {
			
			String nDocAntigo = conta.getN_doc();
						
			
			Query qAcesso = em.createQuery("select a from AcessoCliente a where a.cliente = :cliente", AcessoCliente.class);
			qAcesso.setParameter("cliente", conta.getCliente());
			
			if(qAcesso.getResultList().size() == 1){
				System.out.println(conta.getCliente().getNome_razao());
				
				AcessoCliente ac = (AcessoCliente)qAcesso.getSingleResult();
				String nDocNovo = 
						ac.getId().toString()+
						nDocAntigo.replaceAll(conta.getCliente().getId().toString(), "").replaceAll(ac.getId().toString(), "");
				
				conta.setN_doc(nDocNovo);
				
				em.getTransaction().begin();
				em.merge(conta);
				em.getTransaction().commit();
				
				System.out.println("NUMERO DE DOC ANTIGO É "+nDocAntigo);
				System.out.println("NUMERO DE DOC NOVO É "+nDocNovo);
				System.out.println("CÓDIGO DO BOLETO É :"+conta.getId().toString());
				
			}else{
				System.out.println(conta.getCliente().getNome_razao()+" NÃO ENCONTRADO ACESSO");
			}
		}
		
		
	}
	
	public static void atualizarEnderecoAcesso(){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select a from AcessoCliente a", AcessoCliente.class);
		
		if(q.getResultList().size() >0){
			
			
			em.getTransaction().begin();
			for (AcessoCliente acesso: (List<AcessoCliente>) q.getResultList()) {
				
								
				Query qEndereco = em.createQuery("select e from Endereco e where e.clientes=:cliente", Endereco.class);
				qEndereco.setParameter("cliente", acesso.getCliente());
				
				if(qEndereco.getResultList().size() == 1){
					Endereco end = (Endereco)qEndereco.getSingleResult();
					acesso.setEndereco(end);
					
					em.merge(acesso);
					
					System.out.println("Acesso "+acesso.getId()+" Atualizado com o Endereço "+end.getId());
				}else{
					System.out.println("Acesso "+acesso.getId()+" Não Pode ser Atualizad !");
				}
			}
			em.getTransaction().commit();
		}
	}
	
	public static void atualizarPlanoClienteBloqueado(){
				
//		EntityManager em = ConnUtil.getEntityManager();
//		
//		Query q = em.createQuery("select cb from ClienteBloqueado cb", ClienteBloqueado.class);
//		
//		em.getTransaction().begin();
//		for (ClienteBloqueado cb : (List<ClienteBloqueado>)q.getResultList()) {
//			
//			AcessoCliente ac = CredenciaisAcessoDAO.findByCodCliente(cb.getCliente().getId());
//			if(ac != null){
//				ac.setPlano(cb.getPlano());
//				em.merge(ac);
//				
//				System.out.println("PLANO ATUALIZADO NO CONTRATO : "+ac.getId());
//			}			
//		}
//		em.getTransaction().commit();
		
	}
	
//	private static void atualizarBase02Clientes(){
//    	
//    	
//    	List<AccessList> listAccess = MikrotikUtil.listarAccessList("rbdigital", "mkcolorau", "172.32.0.21", 8728);
//    	
//    	for (AccessList accessList : listAccess) {
//			
//    		    		
//    		EntityManager em = ConnUtil.getEntityManager();
//    		
//    		Query q = em.createQuery("select a from AcessoCliente a where a.endereco_mac = :mac", AcessoCliente.class);
//    		q.setParameter("mac", accessList.getMac_address());
//    		
//    		if(q.getResultList().size() >0){
//    			for (AcessoCliente acesso: (List<AcessoCliente>)q.getResultList()) {
//    			
//	    			if(acesso != null){
//	    				acesso.setBase(new Concentrador(35));
//	    				em.getTransaction().begin();
//	    				em.merge(acesso);
//	    				em.getTransaction().commit();
//	    			
//	    				System.out.println(acesso.getId()+" : "+accessList.getMac_address());
//	    			}    			
//    			}
//    		}else{
//    			
//    					System.out.println(accessList.getMac_address()+" NÃO ENCONTRADO!");		
//    		}
//		}
//    	
//    }
    
    private static void alterarDataVencContratos(){
    	
    	
    	EntityManager em = ConnUtil.getEntity();
    	Query qTodosAcesso = em.createQuery("select ac from AcessoCliente ac where ac.status_2 != 'ENCERRADO'"
    			+ "and ac.contrato != :contrato", AcessoCliente.class);
    	qTodosAcesso.setParameter("contrato", new ContratosAcesso(5));
    	
    	
    	if(qTodosAcesso.getResultList().size()>0){
    		
    		System.out.println("Quantidade de Acessos:"+qTodosAcesso.getResultList().size());
    		for (AcessoCliente acesso : (List<AcessoCliente>)qTodosAcesso.getResultList()) {
				
    			Query qn = em.createNativeQuery(
    	    			"select * from contas_receber cr where " +
    	    			"cr.clientes_id = :cliente and "+		
    					"cr.n_doc REGEXP '[0-9]{1,}/[0-9]{2}-12/[0-9]{2}' "+								
    					"or "+
    					"cr.clientes_id = :cliente and "+
    					"cr.n_doc REGEXP '[0-9]{1,}/[0-9]{2}/12' "+
    					"ORDER BY cr.id DESC",
    					ContasReceber.class);
    			
    			qn.setParameter("cliente", acesso.getCliente().getId());
    			
    			if(qn.getResultList().size()>0){
    				
    				for (ContasReceber conta: (List<ContasReceber>) qn.getResultList()) {
    					if(acesso != null){
            				System.out.println("ACESSO "+acesso.getId());
            				System.out.println("DATA DE VENC. CONTRATO "+conta.getData_vencimento());
            				
            				
            				em.getTransaction().begin();
            				acesso.setData_venc_contrato(conta.getData_vencimento());
            				em.getTransaction().commit();
            				
            				break;
            			}  
					}    				 
    			}else{
    				System.out.println("BOLETOS NÃO ENCONTRADO | ACESSO"+acesso.getId());
    				
    			}
			}
    	}
    	
    	
    }
    
    private static void alterarDataInstContratos(){
    	
    	
    	EntityManager em = ConnUtil.getEntity();
    	Query qTodosAcesso = em.createQuery("select ac from AcessoCliente ac where ac.status_2 != 'ENCERRADO'", AcessoCliente.class);  	
    	
    	
    	if(qTodosAcesso.getResultList().size()>0){
    		
    		System.out.println("Quantidade de Acessos:"+qTodosAcesso.getResultList().size());
    		for (AcessoCliente acesso : (List<AcessoCliente>)qTodosAcesso.getResultList()) {
				
    			Query qn = em.createNativeQuery(
    	    			"select * from contas_receber cr where " +
    	    			"cr.clientes_id = :cliente and "+		
    					"cr.n_doc REGEXP '[0-9]{1,}/[0-9]{2}-01/[0-9]{2}' "+								
    					"or "+
    					"cr.clientes_id = :cliente and "+
    					"cr.n_doc REGEXP '[0-9]{1,}/[0-9]{2}/01' "+
    					"ORDER BY cr.id ASC ",
    					ContasReceber.class);
    			
    			qn.setParameter("cliente", acesso.getCliente().getId());
    			
    			if(qn.getResultList().size()>0){
    				
    				for (ContasReceber conta: (List<ContasReceber>) qn.getResultList()) {
    					if(acesso != null){
            				System.out.println("ACESSO "+acesso.getId());
            				System.out.println("DATA DE INSTALACAO "+conta.getData_emissao());
            				
            				
            				em.getTransaction().begin();
            				acesso.setData_instalacao(conta.getData_emissao());
            				em.getTransaction().commit();
            				
            				break;
            			}  
					}    				 
    			}else{
    				System.out.println("BOLETOS NÃO ENCONTRADO | ACESSO"+acesso.getId());
    				
    			}
			}
    	}
    	
    	
    }
}
