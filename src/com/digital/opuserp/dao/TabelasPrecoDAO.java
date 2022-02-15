package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.TabelasPreco;
import com.digital.opuserp.util.ConnUtil;

public class TabelasPrecoDAO {
	
	EntityManager em;
	
	public TabelasPrecoDAO(){
		em = ConnUtil.getEntity();
	}
	
	
	public TabelasPreco getPreco(Integer id){
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TabelasPreco> tb = cb.createQuery(TabelasPreco.class);
			Root<TabelasPreco> root = tb.from(TabelasPreco.class);
			tb.where(cb.equal(root.get("id"), cb.parameter(TabelasPreco.class, "id")));
			
			TypedQuery<TabelasPreco> q = em.createQuery(tb);
			q.setParameter("id", id);
			return (TabelasPreco) q.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao buscar o preco: "+e.getMessage());
			return null;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
