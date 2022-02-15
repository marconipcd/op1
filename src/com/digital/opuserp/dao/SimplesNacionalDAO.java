package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.digital.opuserp.domain.SimplesNacional;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;

public class SimplesNacionalDAO {
	
	public static SimplesNacional find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from SimplesNacional f where f.id =:cod", SimplesNacional.class);
		q.setParameter("cod", id);
				
		if(q.getResultList().size() == 1){
			return (SimplesNacional)q.getSingleResult();
		}
		
		return null;
	}
	
	public static SimplesNacional findCod(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from SimplesNacional f where f.simples_nacional_cod =:cod", SimplesNacional.class);
		q.setParameter("cod", cod);
		
		if(q.getResultList().size() == 1){
			return (SimplesNacional)q.getSingleResult();
		}
		
		return null;
	}

}
