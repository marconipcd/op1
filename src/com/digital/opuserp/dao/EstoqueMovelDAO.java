package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.EstoqueMovel;
import com.digital.opuserp.util.ConnUtil;

public class EstoqueMovelDAO {

	
	public static boolean update(EstoqueMovel estoque){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			em.merge(estoque);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
}
