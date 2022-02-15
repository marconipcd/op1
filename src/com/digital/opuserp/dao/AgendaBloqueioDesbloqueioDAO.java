package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.AgendamentoBloqueioDesbloqueio;
import com.digital.opuserp.util.ConnUtil;

public class AgendaBloqueioDesbloqueioDAO {

	public static boolean remover(AgendamentoBloqueioDesbloqueio abd){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			em.remove(em.find(AgendamentoBloqueioDesbloqueio.class, abd.getId()));
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
}
