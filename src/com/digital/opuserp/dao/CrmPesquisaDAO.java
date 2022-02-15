package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.Perguntas;
import com.digital.opuserp.domain.Respostas;
import com.digital.opuserp.util.ConnUtil;

public class CrmPesquisaDAO {

	public static Perguntas save(Perguntas p){
		
		EntityManager em = ConnUtil.getEntity();
		try{
			
			em.getTransaction().begin();
			
			if(p.getId() != null){
				em.merge(p);
			}else{
				em.persist(p);
			}
			
			em.getTransaction().commit();
			
			return p;
		}catch(Exception e){
			em.getTransaction().rollback();
			return null;
		}
				
	}
	
	public static Respostas saveResposta(Respostas p){
		
		EntityManager em = ConnUtil.getEntity();
		try{
			
			em.getTransaction().begin();
			
			if(p.getId() != null){
				em.merge(p);
			}else{
				em.persist(p);
			}
			
			em.getTransaction().commit();
			
			return p;
		}catch(Exception e){
			em.getTransaction().rollback();
			return null; 
		}
				
	}
	
	
	public static boolean removeResposta(Respostas p){
		EntityManager em = ConnUtil.getEntity();
		try{
			em.getTransaction().begin();
			em.remove(em.contains(p) ? p : em.merge(p));
			em.getTransaction().commit();
			return true;
		}catch(Exception e){
			em.getTransaction().rollback();
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean remove(Perguntas p){
		EntityManager em = ConnUtil.getEntity();
		try{
			em.getTransaction().begin();
			em.remove(em.contains(p) ? p : em.merge(p));
			em.getTransaction().commit();
			return true;
		}catch(Exception e){
			em.getTransaction().rollback();
			e.printStackTrace();
			return false;
		}
	}
}
