package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.digital.opuserp.domain.CstOrigem;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;

public class CstDAO {
	
	public static CstOrigem find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from CstOrigem f where f.id =:cod", CstOrigem.class);
		q.setParameter("cod", id);
				
		if(q.getResultList().size() == 1){
			return (CstOrigem)q.getSingleResult();
		}
		
		return null;
	}
	
	public static CstOrigem findCod(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from CstOrigem f where f.cst_origem_cod =:cod", CstOrigem.class);
		q.setParameter("cod", cod);
		
		if(q.getResultList().size() == 1){
			return (CstOrigem)q.getSingleResult();
		}
		
		return null;
	}

}
