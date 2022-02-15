package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.ArquivosOsp;
import com.digital.opuserp.domain.Osp;
import com.digital.opuserp.util.ConnUtil;

public class ArquivosOspDAO {
	
	public static List<ArquivosOsp> getFiles(Osp osp){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select a from ArquivosOsp a where a.osp=:osp", ArquivosOsp.class);
			q.setParameter("osp", osp);
			
			if(q.getResultList().size() >0){
				return q.getResultList();
			}
			
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static void save(ArquivosOsp fileOsp){
		EntityManager em = ConnUtil.getEntity();

		try{
		em.getTransaction().begin();
		em.persist(fileOsp);
		em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	public static void remove(ArquivosOsp fileOsp){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		em.getTransaction().begin();
		em.remove(fileOsp);
		em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
}
