package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.AlteracoesOse;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.util.ConnUtil;

public class AlteracoesOseDAO {

	
	public static List<AlteracoesOse> getAltercoesAssistencia(Integer codOse){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select a from AlteracoesOse a where a.ose=:ose", AlteracoesOse.class);
		q.setParameter("ose", new Ose(codOse));
		
		return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean add(AlteracoesOse s){
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
