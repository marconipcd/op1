package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.AlteracoesOse;
import com.digital.opuserp.domain.OcorrenciaContasReceber;
import com.digital.opuserp.util.ConnUtil;

public class OcorrenciaContasReceberDAO {

	public static boolean add(OcorrenciaContasReceber s){
		EntityManager em = ConnUtil.getEntity();
		try{
			
			em.getTransaction().begin();
			em.persist(s);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
}
