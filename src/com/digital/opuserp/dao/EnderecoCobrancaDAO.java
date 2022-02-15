package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.EnderecoCobranca;
import com.digital.opuserp.util.ConnUtil;

public class EnderecoCobrancaDAO {
	
	EntityManager em;
	
	public EnderecoCobrancaDAO(){
		em = ConnUtil.getEntity();
	}
	
	public void persistEnderecoCobranca(EnderecoCobranca endCob){
		try {
			em.persist(endCob);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao persistir EnderecoCobranca: "+e.getMessage());
		}
	}
	
	public EnderecoCobranca getEnderecoCobrancaCliente(Integer codCliente){
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EnderecoCobranca> endCob = cb.createQuery(EnderecoCobranca.class);
			Root<EnderecoCobranca> root = endCob.from(EnderecoCobranca.class);
			endCob.where(cb.equal(root.get("cliente"), cb.parameter(Cliente.class, "codCliente")));
			
			TypedQuery<EnderecoCobranca> q = em.createQuery(endCob);
			q.setParameter("codCliente", new Cliente(codCliente));
			return (EnderecoCobranca) q.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro: EnderecoCobrancaDAO, "+e.getMessage());
			return null;
		}
		
	}
	
	public EnderecoCobranca saveEndereco(EnderecoCobranca end){
		if(end != null){
		    try {
		    	
//		    	if(em == null){
//		    	}
		    	// Inicia uma transação com o banco de dados.
		    	em.getTransaction().begin();
		    	
		    	// Verifica se a pessoa ainda não está salva no banco de dados.
		    	if(end.getId() == null) {
		    		//Salva os dados da pessoa.
		    		em.persist(end);
		    	} else {
		    		//Atualiza os dados da pessoa.
		    		end = em.merge(end);
		    	}
		    	// Finaliza a transação.
		    	em.getTransaction().commit();
		    } catch(Exception e) {
		    	e.printStackTrace();
		    	em.getTransaction().rollback();
		    	System.out.println("Erro: "+e.getMessage());
		    }
		}
	    return end;
		  
	}
	
	public void deleteEndereco(EnderecoCobranca endCob){
		try {
			em.getTransaction().begin();
			
			em.remove(endCob);
			try{
				em.getTransaction().commit();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Erro ao comitar informações-> EnderecoCobrancaDAO: line 69, "+e.getMessage());
			}
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("Erro: "+e.getMessage());
		}
	}
	
	
	
}
