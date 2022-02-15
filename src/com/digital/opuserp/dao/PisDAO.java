package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.Icms;
import com.digital.opuserp.domain.Pis;
import com.digital.opuserp.util.ConnUtil;

public class PisDAO {

	public static Pis findByProduto(Integer produto_id){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select i from Pis i where i.produto = :produto", Pis.class);
		q.setParameter("produto", ProdutoDAO.find(produto_id));
		if(q.getResultList().size() == 1){
			return (Pis)q.getSingleResult();
		}
		
		return null;
	}
	public static void save(Pis pis){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		if(pis.getId() != null){
			em.merge(pis);
		}else{
			em.persist(pis);
		}
		em.getTransaction().commit();
		
	}
}
