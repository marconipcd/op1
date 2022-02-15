package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.AlteracoesEstoqueMovel;
import com.digital.opuserp.util.ConnUtil;

public class AlteracoesEstoqueMovelDAO {

	public static void save(AlteracoesEstoqueMovel alteracao){
		EntityManager em = ConnUtil.getEntity();
		try {
			//em.getTransaction().begin();
			em.persist(alteracao);
			//em.getTransaction().commit();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	

	
}
