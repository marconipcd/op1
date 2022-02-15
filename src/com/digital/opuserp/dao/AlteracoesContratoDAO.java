package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.AlteracoesContrato;
import com.digital.opuserp.util.ConnUtil;

public class AlteracoesContratoDAO {

	
	public static void save(AlteracoesContrato alteracao){
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
