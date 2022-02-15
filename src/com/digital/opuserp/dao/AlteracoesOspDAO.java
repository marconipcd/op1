package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.AlteracoesOsp;
import com.digital.opuserp.domain.Osp;
import com.digital.opuserp.util.ConnUtil;


public class AlteracoesOspDAO {
	
	
	public static void remove(AlteracoesOsp alteracaoOsp){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		em.getTransaction().begin();
		em.remove(alteracaoOsp);		
		em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	public static List<AlteracoesOsp> getAltercoesOsp(Integer codOsp){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select a from AlteracoesOsp a where a.osp=:osp", AlteracoesOsp.class);
		q.setParameter("osp", new Osp(codOsp));
		
		return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean add(AlteracoesOsp s){
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
	
	public static List<AlteracoesOsp> getAltercoesAssistencia(Integer codOse){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select a from AlteracoesOsp a where a.osp=:osp", AlteracoesOsp.class);
		q.setParameter("osp", new Osp(codOse));
		
		return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
}
