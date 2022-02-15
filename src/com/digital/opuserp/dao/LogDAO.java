package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.util.ConnUtil;

public class LogDAO {

	
	
	public static void add(LogAcoes log){
		EntityManager em = ConnUtil.getEntity();
		try{
			if(em == null){
				em = ConnUtil.getEntity();
			}
			em.getTransaction().begin();
			em.persist(log);
			em.getTransaction().commit();
		}catch(Exception e){
			
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			e.printStackTrace();
		}
	}
}
