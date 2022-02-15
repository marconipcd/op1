package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.AlteracoesAssistencia;
import com.digital.opuserp.domain.Osi;
import com.digital.opuserp.util.ConnUtil;

public class AlteracoesOsiDAO {


	public static List<AlteracoesAssistencia> getAltercoesAssistencia(Integer codOsi){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		Query q = em.createQuery("select a from AlteracoesAssistencia a where a.osi=:osi", AlteracoesAssistencia.class);
		q.setParameter("osi", new Osi(codOsi));
		
		return q.getResultList();
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean add(AlteracoesAssistencia s){
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
