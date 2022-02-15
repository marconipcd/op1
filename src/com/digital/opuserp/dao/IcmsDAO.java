package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.Icms;
import com.digital.opuserp.util.ConnUtil;

public class IcmsDAO {

	public static Icms findByProduto(Integer produto_id){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select i from Icms i where i.produto = :produto", Icms.class);
		q.setParameter("produto", ProdutoDAO.find(produto_id));
		if(q.getResultList().size() == 1){
			return (Icms)q.getSingleResult();
		}
		
		return null;
	}
	public static void save(Icms icms){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		if(icms.getId() != null){
			em.merge(icms);
		}else{
			em.persist(icms);
		}
		em.getTransaction().commit();
		
	}
}
