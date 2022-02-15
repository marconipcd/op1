package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.Icms;
import com.digital.opuserp.domain.Ipi;
import com.digital.opuserp.util.ConnUtil;

public class IpiDAO {
	
	public static Ipi findByProduto(Integer produto_id){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select i from Ipi i where i.produto = :produto", Ipi.class);
		q.setParameter("produto", ProdutoDAO.find(produto_id));
		if(q.getResultList().size() == 1){
			return (Ipi)q.getSingleResult();
		}
		
		return null;
	}

	public static void save(Ipi ipi){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		if(ipi.getId() != null){
			em.merge(ipi);
		}else{
			em.persist(ipi);
		}
		em.getTransaction().commit();		
	}
}
