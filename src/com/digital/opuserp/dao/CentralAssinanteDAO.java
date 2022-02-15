package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.ConfigCentralAssinante;
import com.digital.opuserp.util.ConnUtil;

public class CentralAssinanteDAO {

	
	public static ConfigCentralAssinante getDefaultValue(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from ConfigCentralAssinante c", ConfigCentralAssinante.class);
			if(q.getResultList().size() == 1){
				return (ConfigCentralAssinante)q.getSingleResult();
			}
			
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void saveValueDefault(ConfigCentralAssinante c){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			if(c.getId() != null){
				em.merge(c);
			}else{
				em.persist(c); 
			}
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
}
