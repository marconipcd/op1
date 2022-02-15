package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.AlteracoesHaver;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.util.ConnUtil;

public class AlteracoesHaverDAO {

	
	public static boolean add(AlteracoesHaver s){
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
	
	public static List<AlteracoesHaver> list_alteracoes_haver(Haver haver){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select ah from AlteracoesHaver ah where ah.haver = :haver", AlteracoesHaver.class);
		q.setParameter("haver", haver);
		
		return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
}
