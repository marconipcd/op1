package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Endereco;
import com.digital.opuserp.domain.EnderecoEntrega;
import com.digital.opuserp.util.ConnUtil;

public class EnderecoEntregaDAO {
	
	EntityManager em;
	
	public EnderecoEntregaDAO(){
		em = ConnUtil.getEntity();
	}
	
	public void persistEnderecoEntrega(EnderecoEntrega endEnt){
		try {
			em.persist(endEnt);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao persistir EnderecoEntrega: "+e.getMessage());
		}
	}
	
	public EnderecoEntrega getEnderecoEntregaCliente(Integer codCliente){
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EnderecoEntrega> e = cb.createQuery(EnderecoEntrega.class);
			Root<EnderecoEntrega> root = e.from(EnderecoEntrega.class);
			e.where(cb.and(
						cb.equal(root.get("cliente"), cb.parameter(Cliente.class, "cod"))
						
					));
			TypedQuery<EnderecoEntrega> q = em.createQuery(e);
			q.setParameter("cod", new Cliente(codCliente));
			return (EnderecoEntrega) q.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERRO: EnderecoEntregaDAO, "+e.getMessage());
			return null;
		}
		
	}
	
	public EnderecoEntrega saveEndereco(EnderecoEntrega end){
		if(end != null){
		  
			try{
		    	
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
		    	}catch(Exception e){
		    		e.printStackTrace();
		    		em.getTransaction().rollback();
		    		System.out.println("Erro ao comitar as informações-> EnderecoEntregaDAO: line 51 "+e.getMessage());
		    	}
		    	
		   
		}
	    return end;
		  
	}
	
	public EnderecoEntrega deleteEndereco(EnderecoEntrega endEntrega){
		try{
			em.getTransaction().begin();
			
			em.remove(endEntrega);
			
			em.getTransaction().commit();
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Erro: "+e.getMessage());
		}
		return null;
	}
	
	
	
}
