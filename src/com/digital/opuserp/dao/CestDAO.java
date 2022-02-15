package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.Cest;
import com.digital.opuserp.util.ConnUtil;

public class CestDAO {

	public static Cest getCest(String id){
		
		EntityManager em  = ConnUtil.getEntity();
		
		try{
		
			Query q = em.createQuery("select c from Cest c where c.cest =:codigo", Cest.class);
			q.setParameter("codigo", id);
			
			if(q.getResultList().size() == 1){
				return (Cest)q.getSingleResult();
			}
			
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
