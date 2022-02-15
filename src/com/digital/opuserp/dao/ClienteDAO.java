package com.digital.opuserp.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;

public class ClienteDAO {
	
	
	public static void remove(Cliente c){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			em.remove(em.contains(c) ? c : em.merge(c));
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}
	public static List<Cliente> search(String query){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select c from Cliente c where c.empresa=:empresa and c.telefone1 LIKE CONCAT('%', :data, '%')", Cliente.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		q.setParameter("data", query);
		
		return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Cliente> getAniversariantes(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select c from Cliente c where c.empresa=:empresa and c.data_nascimento LIKE CONCAT('%', :data, '%')", Cliente.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		q.setParameter("data", sdf.format(new Date()));
		
		return q.getResultList();
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean checkEmailExists(String email){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select c from Cliente c where c.empresa =:empresa and c.email =:email", Cliente.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		q.setParameter("email", email);
		
		if(q.getResultList().size() >0){
			return true;
		}
		
		return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static Cliente findByCpf(String cpf){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select c from Cliente c where c.empresa =:empresa and c.doc_cpf_cnpj =:doc_cpf_cnpj", Cliente.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		q.setParameter("doc_cpf_cnpj", cpf);
		Cliente cliente =null;		
		
		if(q.getResultList().size() >0){
			cliente = (Cliente)q.getSingleResult();
		}
		
		return cliente;
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Procurar Cliente por Código
	 * 
	 * @param codCliente
	 * @return Cliente
	 */
	public static Cliente find(Integer codCliente){		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		if(OpusERP4UI.getEmpresa() != null){
		Query q = em.createQuery("select c from Cliente c where c.empresa=:empresa and c.id = :cod", Cliente.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		q.setParameter("cod", codCliente);
		
		if(q.getResultList().size() == 1){
			return (Cliente)q.getSingleResult();
		}else{
		return null;
		}
		
		}else{
			return em.find(Cliente.class, codCliente);
		}
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public static List<ContasReceber> listarClientesTitulosContasReceber(String statusTitulo,String valor,String controle,Integer maxResult){
		
		String nnSicred= "";
		if(valor.length() == 10){
			StringBuilder s = new StringBuilder(valor);
			s.delete(0, 1);
			s.insert(2, "/");
			s.insert(9, "-");
			
			nnSicred = s.toString();
		}
		
		EntityManager em = ConnUtil.getEntity();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ContasReceber> criteriaQuery = cb.createQuery(ContasReceber.class);		
			Root<ContasReceber> rootFinanceiro = criteriaQuery.from(ContasReceber.class);
			EntityType<ContasReceber> type = em.getMetamodel().entity(ContasReceber.class);
			List<Predicate> criteria = new ArrayList<Predicate>();
			
			criteria.add(cb.equal(rootFinanceiro.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));
			
			if(controle != null){
				criteria.add(cb.equal(rootFinanceiro.get("controle"), controle));
			}
			
			
			boolean tipo_int = false;
			
			try{
				Integer codBoleto = Integer.parseInt(valor);
				tipo_int = true;
			}catch(Exception e){
				tipo_int = false;
			}
			
			
			boolean tipo_date = false;
			
			try{
				
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");  
				Date data = new Date(format.parse(valor).getTime());  
				
				tipo_date = true;
			}catch(Exception e){
				tipo_date = false;
			}
			
			if(tipo_int && !tipo_date){
				Integer codBoleto = Integer.parseInt(valor);
				if(statusTitulo.equals("VENCIDOS")){				
					criteria.add(cb.equal(rootFinanceiro.get("status"), "ABERTO"));				
					criteria.add(cb.lessThan(rootFinanceiro.<Date> get("data_vencimento"), sdf.parse(sdf.format(new Date()))));					
				}else if(statusTitulo.equals("TODOS")){			
					criteria.add(cb.notEqual(rootFinanceiro.get("status"), "EXCLUIDO"));		
				}else{								
					criteria.add(cb.equal(rootFinanceiro.get("status"), statusTitulo));			
				}	
				
				criteria.add(cb.or(
						cb.equal(rootFinanceiro.get("id"), codBoleto),
						cb.equal(rootFinanceiro.get("n_numero"), valor),
						cb.equal(rootFinanceiro.get("n_numero"), valor),
						cb.equal(rootFinanceiro.get("n_numero_sicred"), nnSicred),
						cb.like(rootFinanceiro.get(type.getDeclaredSingularAttribute("codigo_cartao", String.class)),"%"+valor+"%")
						));
			}else if(!tipo_int && tipo_date){
				
				try{
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");  
					Date data = new Date(format.parse(valor).getTime());
					
					if(statusTitulo.equals("VENCIDOS")){				
						criteria.add(cb.equal(rootFinanceiro.get("status"), "ABERTO"));				
						criteria.add(cb.lessThan(rootFinanceiro.<Date> get("data_vencimento"), sdf.parse(sdf.format(new Date()))));					
					}else if(statusTitulo.equals("TODOS")){			
						criteria.add(cb.notEqual(rootFinanceiro.get("status"), "EXCLUIDO"));		
					}else{								
						criteria.add(cb.equal(rootFinanceiro.get("status"), statusTitulo));			
					}	
					
					criteria.add(cb.or(									
							cb.equal(rootFinanceiro.get("data_vencimento"), data),
							cb.equal(rootFinanceiro.get("data_pagamento"), data),
							cb.equal(rootFinanceiro.get("data_emissao"), data),
							cb.equal(rootFinanceiro.get("n_doc"), valor)	,
							cb.like(rootFinanceiro.get(type.getDeclaredSingularAttribute("codigo_cartao", String.class)),"%"+valor+"%")
							));
				
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				
			}else{
				if(statusTitulo.equals("VENCIDOS")){							
					criteria.add(cb.equal(rootFinanceiro.get("status"), "ABERTO"));
					criteria.add(cb.lessThan(rootFinanceiro.<Date> get("data_vencimento"), sdf.parse(sdf.format(new Date()))));				
				}else if(statusTitulo.equals("TODOS")){
					criteria.add(cb.notEqual(rootFinanceiro.get("status"), "EXCLUIDO"));		
				}else{
					criteria.add(cb.equal(rootFinanceiro.get("status"), statusTitulo));
				}
				
				criteria.add(cb.or(
						cb.like(rootFinanceiro.join("cliente").<String>get("nome_razao"), "%"+valor+"%"),				
						cb.like(rootFinanceiro.get(type.getDeclaredSingularAttribute("controle", String.class)),"%"+valor+"%"),
						cb.equal(rootFinanceiro.get("n_numero"), valor),
						cb.equal(rootFinanceiro.get("n_numero_sicred"), valor),
						cb.equal(rootFinanceiro.get("n_numero_sicred"), nnSicred),
						cb.equal(rootFinanceiro.get("valor_titulo"), valor),
						cb.equal(rootFinanceiro.get("valor_pagamento"), valor),
						cb.equal(rootFinanceiro.get("forma_pgto"), valor),
						cb.like(rootFinanceiro.get(type.getDeclaredSingularAttribute("codigo_cartao", String.class)),"%"+valor+"%"),
						cb.like(rootFinanceiro.get(type.getDeclaredSingularAttribute("n_doc", String.class)),"%"+valor+"%")
						));
			}
			
			
			criteriaQuery.orderBy(
					
					cb.asc(rootFinanceiro.join("cliente").get("id")),
					cb.asc(rootFinanceiro.get("data_vencimento"))
			);
			
			criteriaQuery.where(cb.and(criteria.toArray(new Predicate[0])));
			
			TypedQuery q = em.createQuery(criteriaQuery);
			
			if(maxResult != null){
				q.setMaxResults(maxResult);
			}
			
			if(q.getResultList().size()>0){
				return q.getResultList();
			}else{
				return null;
			}
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void changeStatusFromActive(Cliente c) throws Exception{
					
			EntityManager em = ConnUtil.getEntity();
			
			try{
			c.setStatus("ATIVO");
			em.merge(c);
			}catch(Exception e){
				e.printStackTrace();
				
			}
			
	}
	
	public static void changeStatusFromActive(Integer cod) throws Exception{
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Cliente c = em.find(Cliente.class, cod);
		c.setStatus("ATIVO");
		em.merge(c);
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
	}
	
	public void changeStatusWithInActive(Integer codCliente) throws Exception{
		EntityManager em = ConnUtil.getEntity();

		try{
		Cliente c = em.find(Cliente.class, codCliente);
		c.setStatus("INATIVO");
		em.merge(c);
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
	}
	
	public boolean allowRemove(Integer codCliente, Integer codEmpresa){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		boolean allow = true;
		//Verificar se Tem Acesso, Boletos em Aberto, CRM, OSE, OSI e OSP, 
		
		//Acesso Cliente
		Query qAcesso = em.createQuery("select ac from AcessoCliente ac where ac.cliente = :Cliente and ac.empresa_id = :codEmpresa");
		qAcesso.setParameter("Cliente", new Cliente(codCliente));
		qAcesso.setParameter("codEmpresa", codEmpresa);
		if(qAcesso.getResultList().size() > 0){
			allow = false;
		}
		
		//Contas a Receber
		Query qContasReceber = em.createQuery("select cr from ContasReceber cr where cr.cliente = :Cliente and cr.empresa_id = :codEmpresa");
		qContasReceber.setParameter("Cliente", new Cliente(codCliente));
		qContasReceber.setParameter("codEmpresa", codEmpresa);
		if(qContasReceber.getResultList().size() > 0){
			allow = false;
		}
		
		//Crm
		Query qCrm = em.createQuery("select crm from Crm crm where crm.cliente = :Cliente and crm.empresa_id = :codEmpresa");
		qCrm.setParameter("Cliente", new Cliente(codCliente));
		qCrm.setParameter("codEmpresa", codEmpresa);
		if(qCrm.getResultList().size() > 0){
			allow = false;
		}
		
		//Ose
		Query qOse = em.createQuery("select ose from Ose ose where ose.cliente = :Cliente and ose.empresa_id = :codEmpresa");
		qOse.setParameter("Cliente", new Cliente(codCliente));
		qOse.setParameter("codEmpresa", codEmpresa);
		if(qOse.getResultList().size() > 0){
			allow = false;
		}
		
		//Osi
		Query qOsi = em.createQuery("select osi from Osi osi where osi.cliente = :Cliente and osi.empresa_id = :codEmpresa");
		qOsi.setParameter("Cliente", new Cliente(codCliente));
		qOsi.setParameter("codEmpresa", codEmpresa);
		if(qOsi.getResultList().size() > 0){
			allow = false;
		}
		
		//Osp
		Query qOsp = em.createQuery("select osp from Osp osp where osp.cliente = :Cliente and osp.empresa_id = :codEmpresa");
		qOsp.setParameter("Cliente", new Cliente(codCliente));
		qOsp.setParameter("codEmpresa", codEmpresa);
		if(qOsp.getResultList().size() > 0){
			allow = false;
		}
		
		
		//ECF Prevenda
		Query qEcfPrevenda = em.createQuery("select epv from EcfPreVendaCabecalho epv where epv.cliente = :Cliente and epv.empresa_id = :codEmpresa");
		qEcfPrevenda.setParameter("Cliente", new Cliente(codCliente));
		qEcfPrevenda.setParameter("codEmpresa", codEmpresa);
		if(qEcfPrevenda.getResultList().size() > 0){
			allow = false;
		}
				
		
		
		
		return allow;
		
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static Cliente saveCliente(Cliente cliente, List<Endereco> enderecos){
		
		EntityManager em = ConnUtil.getEntity();
		
	    try {
	    	
	    	
	    	em.getTransaction().begin();
	    	
	    	
	    	if(cliente.getId() == null) {
	    		em.persist(cliente);
	    	} else {
	    		cliente = em.merge(cliente);
	    	}

	    	if(enderecos != null){
	    		for (Endereco end : enderecos) {
	    			end.setClientes(cliente);
	    			
	    			if(end.isPrincipal()){
	    				cliente.setEndereco_principal(end);
	    				em.merge(cliente);							
	    			}
	    			
	    			if(end.getId() != null){
	    				em.merge(end);
	    			}else{
	    				em.persist(end);
	    			}
	    														
	    		}

	    		em.getTransaction().commit();
	    	}else{
	    		em.getTransaction().rollback();
	    		Notify.Show("Problema ao concluir, o cadastro precisa ser Refeito!", Notify.TYPE_ERROR);
	    		System.out.println("####################Cliente"+cliente.getId()+"Sem Endereço#########################");
	    		LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cliente sem Endereco"));
	    	}
	    	
	    	return cliente;
	    	
	    }catch(Exception e){
			e.printStackTrace();
			return null;
		}
		  
	}
	
public static Cliente saveImporteCliente(Cliente cliente, List<Endereco> enderecos){
		
		EntityManager em = ConnUtil.getEntity();
		
	    try {
	    	
	    	em.getTransaction().begin();
	    	
	    	
	    	if(cliente.getId() == null) {
	    		em.persist(cliente);
	    	} else {
	    		cliente = em.merge(cliente);
	    	}

	    	if(enderecos != null){
	    		for (Endereco end : enderecos) {
	    			
	    			end.setId(null);
	    			
	    			end.setClientes(cliente);
	    			em.persist(end);
	    			
	    			
	    			if(end.isPrincipal()){
	    				cliente.setEndereco_principal(end);
	    				em.persist(cliente);							
	    			}
	    			
	    			
	    			if(end.getId() != null){
	    				em.persist(end);
	    			}else{
	    				em.persist(end);
	    			}
	    														
	    		}

	    		em.getTransaction().commit();
	    	}else{
	    		em.getTransaction().rollback();
	    		Notify.Show("Problema ao concluir, o cadastro precisa ser Refeito!", Notify.TYPE_ERROR);
	    		System.out.println("####################Cliente"+cliente.getId()+"Sem Endereço#########################");
	    		LogDAO.add(new LogAcoes(null, OpusERP4UI.getUsuarioLogadoUI().getUsername(), "Cliente sem Endereco"));
	    	}
	    	
	    	return cliente;
	    	
	    }catch(Exception e){
			e.printStackTrace();
			
			return null;
			
		}
		  
	}

	public static Cliente saveCliente(Cliente cliente, Endereco end){
		
		EntityManager em = ConnUtil.getEntity();
		
	    try {
	    	
	    	
	    	em.getTransaction().begin();
	    	
	    	
	    	if(cliente.getId() == null) {
	    		em.persist(cliente);
	    	} else {
	    		
	    		if(end.isPrincipal()){
	    			cliente.setEndereco_principal(end);	    		
	    		}
	    		cliente = em.merge(cliente);
	    		
	    		//em.remove(endExclu);
	    	}

	    	
	    	
	    	em.getTransaction().commit();
	    	
	    	return cliente;
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Erro ao atualizar ou cadastrar Endereço: "+e.getMessage());
	    	
	    	return null;
	    }
		  
	}

public static Cliente saveCliente(Cliente cliente){
		
		EntityManager em = ConnUtil.getEntity();
		
	    try {
	    	
	    	
	    	em.getTransaction().begin();
	    	
	    	
	    	if(cliente.getId() == null) {
	    		em.persist(cliente);
	    	} else {
	    		cliente = em.merge(cliente);
	    	}

	    	
	    	
	    	em.getTransaction().commit();
	    	
	    	return cliente;
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Erro ao atualizar ou cadastrar Endereço: "+e.getMessage());
	    	
	    	return null;
	    }
	}

	public Cliente getCliente(Integer id){
		
		EntityManager em = ConnUtil.getEntity();
		
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Cliente> c = cb.createQuery(Cliente.class);
			Root<Cliente> root = c.from(Cliente.class);
			c.where(cb.equal(root.get("id"), cb.parameter(Integer.class, "id"))
					);
			TypedQuery<Cliente> q = em.createQuery(c);
			q.setParameter("id", id);
			
			if(q.getResultList().size() == 1){
				return (Cliente) q.getSingleResult();				
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro: "+e.getMessage());
			return null;
		}
	}
	
	public String getNextID(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'clientes'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Erro ao tentar pegar o próximo ID do cliente: "+e.getMessage());
			return null;
		}
	}
	
	public String getEnderecoID(Integer codEndereco){
		EntityManager em = ConnUtil.getEntity();
		
		try {
			em = ConnUtil.getEntity();
			Query q = em.createNativeQuery("SELECT ENDERECO_ID FROM clientes WHERE ID = "+codEndereco);
			String result = (String) q.getSingleResult();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao buscar COD de ENDERECO: "+e.getMessage());
			System.out.println("Causado por: "+e.getCause());
			return null;
		}
	}
	
	public Cliente getClienteTabelaPreco(Integer codCliente){
		
		EntityManager em = ConnUtil.getEntity();

		
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Cliente> c = cb.createQuery(Cliente.class);
			Root<Cliente> root = c.from(Cliente.class);
			c.where(cb.equal(
					root.get("tabelaPrecoID"), cb.parameter(Integer.class, "idTable"))
					);
			
			TypedQuery<Cliente> q = em.createQuery(c);
			q.setParameter("idTable", new Cliente(codCliente));
			return (Cliente) q.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao buscar a tabela de preco: "+e.getMessage());
			return null;
		}
		
	}
	
	
	 public Cliente cpfCnpjCadastrado(Empresa empresa, String cnpj){
		 
			EntityManager em = ConnUtil.getEntity();

	        try{             
	            
	            CriteriaBuilder cb = em.getCriteriaBuilder();
	            CriteriaQuery<Cliente> c = cb.createQuery(Cliente.class);
	            Root<Cliente> venda = c.from(Cliente.class);
	            c.where(cb.and(
	                    cb.equal(venda.get("doc_cpf_cnpj"),cb.parameter(String.class, "cpfCnpj")),
	                    cb.equal(venda.get("empresa"),cb.parameter(Empresa.class, "empresa"))
	                    ));
	                
	            TypedQuery q = em.createQuery(c);
	            q.setParameter("cpfCnpj", cnpj);
	            q.setParameter("empresa",empresa);
	            //em.close();
	            
	            if (q.getResultList().size()==1){
		    		return (Cliente) q.getSingleResult();
		    	}else{
		    		return null;
		    	}
	         
	        }catch(Exception e){
	            System.out.println("Erro: "+e.getMessage());
	            return null;
	        }     
	    }
	
	 
	 public boolean categoriaEmUso(Empresa empresa, Categorias categoria){
		 
			EntityManager em = ConnUtil.getEntity();

			
	        try{             
	            
	            CriteriaBuilder cb = em.getCriteriaBuilder();
	            CriteriaQuery<Cliente> c = cb.createQuery(Cliente.class);
	            Root<Cliente> venda = c.from(Cliente.class);
	            c.where(cb.and(
	                    //cb.equal(venda.get("id"),cb.parameter(Integer.class, "codCliente")),
	                    cb.equal(venda.get("empresa"),cb.parameter(Empresa.class, "empresa")),
	                    cb.equal(venda.get("categoria"),cb.parameter(Categorias.class, "categoria"))
	                    ));
	                
	            TypedQuery q = em.createQuery(c);
	            //q.setParameter("id", codCliente);
	            q.setParameter("empresa",empresa);
	            q.setParameter("categoria",categoria);
	            //em.close();
	            
	            if (q.getResultList().size()>0){
	            	return true;
		    	}else{
		    		return false;
		    	}
	         
	        }catch(Exception e){
	            System.out.println("Erro: "+e.getMessage());
	            return false;
	        }         
	    }
	 
	 

}
