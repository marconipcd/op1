package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.AlteracoesContasPg;
import com.digital.opuserp.domain.AlteracoesContasReceber;
import com.digital.opuserp.util.ConnUtil;

public class AlteracoesContasReceberDAO {

	
	
	public static void save(AlteracoesContasReceber alteracao){
		
		EntityManager em = ConnUtil.getEntity();		
		try{					
			em.getTransaction().begin();
			em.persist(alteracao);
			em.getTransaction().commit();
		}catch(Exception e){
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			e.printStackTrace();			
		}
	}
	
}
