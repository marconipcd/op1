package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.PreContrato;
import com.digital.opuserp.util.ConnUtil;

public class PreContratoDAO {

	public static boolean salvarPreContrato(PreContrato preContrato){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			em.persist(preContrato);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
