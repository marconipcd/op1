package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.CentralAssinante;
import com.digital.opuserp.util.ConnUtil;

public class CentralAssinantesDAO {

	public static CentralAssinante findByCodCliente(Integer codCliente){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select ca from CentralAssinante ca where ca.cliente_id=:cliente", CentralAssinante.class);
			q.setParameter("cliente", codCliente);
			
			if(q.getResultList().size() == 1){
				return (CentralAssinante) q.getSingleResult();
			}
			
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void save(CentralAssinante c){
		EntityManager em = ConnUtil.getEntity();
		try{
			em.getTransaction().begin();
			if(findByCodCliente(c.getCliente_id()) != null){
				em.merge(c);
			}else{			
				em.persist(c);
			}
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}
}
