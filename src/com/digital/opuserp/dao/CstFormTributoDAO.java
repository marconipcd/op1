package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.CstFormaTributo;
import com.digital.opuserp.domain.CstOrigem;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;

public class CstFormTributoDAO {
	
	public static CstFormaTributo find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from CstFormaTributo f where f.id =:cod", CstFormaTributo.class);
		q.setParameter("cod", id);
				
		if(q.getResultList().size() == 1){
			return (CstFormaTributo)q.getSingleResult();
		}
		
		return null;
	}
	
	public static CstFormaTributo findCod(String cod){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from CstFormaTributo f where f.cst_tributo_cod =:cod", CstFormaTributo.class);
		q.setParameter("cod", cod);
		
		if(q.getResultList().size() == 1){
			return (CstFormaTributo)q.getSingleResult();
		}
		
		return null;
	}

}
