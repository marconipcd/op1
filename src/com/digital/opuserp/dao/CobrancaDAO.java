package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.ConfigBoleto;
import com.digital.opuserp.util.ConnUtil;

public class CobrancaDAO {

	public static ConfigBoleto getDefaultValue(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from ConfigBoleto c", ConfigBoleto.class);
			if(q.getResultList().size() == 1){
				return (ConfigBoleto)q.getSingleResult();
			}
			
			return null;
		}catch(Exception e){
            System.out.println("Erro: "+e.getMessage());
            return null;
        }
	}
	
	public static void saveValueDefault(ConfigBoleto c){
		
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
            System.out.println("Erro: "+e.getMessage());
            
        }
	
	}
}
