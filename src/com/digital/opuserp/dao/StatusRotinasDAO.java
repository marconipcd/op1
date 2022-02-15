package com.digital.opuserp.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.StatusRotinas;
import com.digital.opuserp.util.ConnUtil;

public class StatusRotinasDAO {

	public static boolean checkRunning(String desc){
		
		try {
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select s from StatusRotinas s where s.empresa_id=:empresa and s.desc=:desc and s.data=:data", StatusRotinas.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			q.setParameter("desc", desc);
			q.setParameter("data", new Date());
			
			if(q.getResultList().size() == 0){
				return false;
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return true;			
		}		
		
	}
	
	public static StatusRotinas setRunning(String desc){
//		EntityManager em = ConnUtil.getEntityManager();
//		
//		try {
//			StatusRotinas status = new StatusRotinas(null, OpusERP4UI.getEmpresa().getId(), desc, new Date());
//			
//			em.getTransaction().begin();
//			em.persist(status);
//			em.getTransaction().commit();
//			
//			return status;
//		} catch (Exception e) {
//			em.getTransaction().rollback();
//			e.printStackTrace();
//			
			return null;
//		}
	}
	
	
	public static StatusRotinas setStop(StatusRotinas status){
		EntityManager em = ConnUtil.getEntity();
		
		try {
						
			em.getTransaction().begin();
			em.remove(status);
			em.getTransaction().commit();
			
			return status;
		} catch (Exception e) {
			em.getTransaction().rollback();
			e.printStackTrace();
			
			return null;
		}
	}
	
}
