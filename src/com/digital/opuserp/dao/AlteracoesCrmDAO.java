package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.AlteracoesCrm;
import com.digital.opuserp.util.ConnUtil;

public class AlteracoesCrmDAO {

	
	public static boolean save(AlteracoesCrm alteracao){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			em.persist(alteracao);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
}
