package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.ConfigNfe;
import com.digital.opuserp.domain.ConfigNfeMod55;
import com.digital.opuserp.util.ConnUtil;

public class NfeDAO55 {

	public static ConfigNfeMod55 getDefaultValue(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from ConfigNfeMod55 c", ConfigNfeMod55.class);
		if(q.getResultList().size() == 1){
			return (ConfigNfeMod55)q.getSingleResult();
		}
		
		return null;
	}
	
	public static void saveValueDefault(ConfigNfeMod55 c){
		
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		if(c.getId() != null){
			em.merge(c);
		}else{
			em.persist(c); 
		}
		em.getTransaction().commit();
	}
	
}
