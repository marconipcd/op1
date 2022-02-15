package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.AlteracoesContasPg;
import com.digital.opuserp.util.ConnUtil;

public class AlteracoesContasPgDAO {

	
	public static void save(AlteracoesContasPg alteracao){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			em.persist(alteracao);
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
}
