package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.LogError;
import com.digital.opuserp.domain.PrintLog;
import com.digital.opuserp.util.ConnUtil;

public class PrintLogDAO {

	
	EntityManager em;
	
	public PrintLogDAO(){
		em = ConnUtil.getEntity();
	}
	
	public PrintLog getScreenByLog(Integer codLog){
		
		Query q = em.createQuery("select p from PrintLog p where p.log = :codLog", PrintLog.class);
		q.setParameter("codLog", new LogError(codLog));
		
		if(q.getResultList().size() == 1){
			PrintLog screen = (PrintLog) q.getSingleResult();
			return screen;
		}else{
			return null;
		}
	}
}
