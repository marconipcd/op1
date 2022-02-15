package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.Swith;
import com.digital.opuserp.util.ConnUtil;

public class SwithDAO {

	
	private static EntityManager em = ConnUtil.getEntity();
	
	public static List<Swith> getSwiths(){
		
		Query q = em.createQuery("select s from Swith s where s.empresa_id = :empresa_id", Swith.class);
		q.setParameter("empresa_id", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}
	
	public static List<Swith> getSwithsbyConcentrador(Integer codConcentrador){
		
		Query q = em.createQuery("select s from Swith s where s.concentrador = :concentrador", Swith.class);
		q.setParameter("concentrador", new Concentrador(codConcentrador));
		
		return q.getResultList();
	}
	public static boolean allowDelete(Swith switch2){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select a from AcessoCliente a where a.swith=:s", AcessoCliente.class);
		q.setParameter("s", switch2);
		
		if(q.getResultList().size() > 0){
			return false;
		}
		
		return true;
	}
	public static Swith findByNome(String identificacao){
		
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select a from Swith a where a.identificacao=:s", Swith.class);
		q.setParameter("s", identificacao);
		
		if(q.getResultList().size() == 1){
			return (Swith)q.getSingleResult();
		}
		
		return null;
	}
	
	public static boolean atualizar(Swith s){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			em.merge(s);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
}
