package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.util.ConnUtil;

public class EnderecoDAO {

	EntityManager em;
	
	public EnderecoDAO(){
		em = ConnUtil.getEntity();
	}
	
	public static Endereco find(Integer codEndereco){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Endereco.class, codEndereco);
	}
	
	public static void remove(Endereco end){
		EntityManager em = ConnUtil.getEntity();
		try{
			em.getTransaction().begin();
			em.remove(em.merge(end));
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static boolean checkUtilizacao(Integer codEndereco){
		EntityManager emr = ConnUtil.getEntity();
		
		Query q = emr.createQuery("select a from AcessoCliente a where a.endereco =:endereco", AcessoCliente.class);
		q.setParameter("endereco", new Endereco(codEndereco));
		
		if(q.getResultList().size() >0){
			return true;
		}
			return false;
		
	}
	
	public static boolean checkExistsDefault(Integer codCliente){
		EntityManager emr = ConnUtil.getEntity();
		Query q = emr.createQuery("select e from Endereco e where e.clientes =:cliente and e.principal =:principal", Endereco.class);
		q.setParameter("cliente", new Cliente(codCliente));
		q.setParameter("principal", true);
		
		if(q.getResultList().size()>0){
			return true;
		}
			return false;
		
	}
	public static void changeEnderecoPrincipal(Integer codCliente, Integer codEndPrincipalNovo){
		
		EntityManager emr = ConnUtil.getEntity();
		Query q = emr.createQuery("select e from Endereco e where e.clientes =:cliente and e.principal =:principal", Endereco.class);
		q.setParameter("cliente", new Cliente(codCliente));
		q.setParameter("principal", true);
		
		emr.getTransaction().begin();
		
		for (Endereco end : (List<Endereco>)q.getResultList()) {
			end.setPrincipal(false);
			emr.merge(end);
		}
		
		
		Endereco endPrincipal = emr.find(Endereco.class, codEndPrincipalNovo);
		if(endPrincipal != null){
			
			endPrincipal.setPrincipal(true);
			emr.merge(endPrincipal);
			
			Cliente c = emr.find(Cliente.class, codCliente);
			if(c != null){
				c.setEndereco_principal(endPrincipal);
				emr.merge(c);
			}
			
		}
		
		emr.getTransaction().commit();
	}
	
	public static void save(Endereco e){
		EntityManager emr = ConnUtil.getEntity();
		
		
		emr.getTransaction().begin();
		if(e.getId() == null){
			emr.persist(e);
		}else{
			emr.merge(e);
		}
		emr.getTransaction().commit();
		
	}
	
	public void persistEndereco(Endereco end){
		try {
			em.persist(end);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao persistir Endereco: "+e.getMessage());
		}
	}
	
	public static void deleteEnderecos(Integer codCliente){
		EntityManager em = ConnUtil.getEntity();
		Query q =  em.createQuery("select e from Endereco e where e.clientes =:cliente", Endereco.class);
		q.setParameter("cliente", new Cliente(codCliente));
		
		em.getTransaction().begin();
		for (Endereco end : (List<Endereco>)q.getResultList()) {
			em.remove(end);
		}
		em.getTransaction().commit();
	}
	
	public Endereco getEnderecoCliente(Integer codCliente){
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Endereco> c = cb.createQuery(Endereco.class);
			Root<Endereco> root = c.from(Endereco.class);
			c.where(
						cb.equal(root.get("clientes"), cb.parameter(Cliente.class, "codCliente"))
					);
			TypedQuery<Endereco> q = em.createQuery(c);
			q.setParameter("codCliente", new Cliente(codCliente));
			return (Endereco) q.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro: EnderecoDAO, "+e.getMessage());
			return null;
		}
		
	}
	
	public Endereco getEndereco(Endereco end){
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Endereco> e = cb.createQuery(Endereco.class);
			Root<Endereco> root = e.from(Endereco.class);
			e.where(cb.and(
						cb.equal(root.get("id"), cb.parameter(Integer.class, "cod"))
					));
			TypedQuery<Endereco> q = em.createQuery(e);
			q.setParameter("cod", end.getId());
			return (Endereco) q.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERRO: "+e.getMessage());
			return null;
		}
	}
	
//	public Endereco saveEndereco(Endereco end){
//	    
//		if(end != null){
//	    	
//		try{
//	    	// Inicia uma transação com o banco de dados.
//	    	em.getTransaction().begin();
//	    	
//	    	// Verifica se a pessoa ainda não está salva no banco de dados.
//	    	if(end.getId() == null) {
//	    		//Salva os dados da pessoa.
//	    		em.persist(end);
//	    	} else {
//	    		//Atualiza os dados da pessoa.
//	    		end = em.merge(end);
//	    	}
//	    		em.getTransaction().commit();
//	    	}catch(Exception e){
//	    		e.printStackTrace();
//	    		em.getTransaction().rollback();	    		
//	    	}
//		}
//	    return end;
//		  
//	}
	
	public void deleteEndereco(Endereco end){
		try{
			em.getTransaction().begin();
			
//			Endereco endPrincipal = em.find(Endereco.class, end.getId());
			
			em.remove(end);
			try{
				em.getTransaction().commit();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Erro ao comitar informações-> EnderecoDAO: line 97 "+e.getMessage());
			}
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Erro ao excluir: "+e.getMessage());
		}
	}
	
	public String getNextID(){
		try{
			em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'enderecos_principais'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Erro ao tentar pegar o próximo ID do endereço: "+e.getMessage());
			return null;
		}
	}
	
}
