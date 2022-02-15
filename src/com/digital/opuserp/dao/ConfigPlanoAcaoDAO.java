package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.ConfigPlanoAcao;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;

public class ConfigPlanoAcaoDAO {

	public static ConfigPlanoAcao getInfo(Usuario operador){
		
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select c from ConfigPlanoAcao c where c.operador=:o", ConfigPlanoAcao.class);
		q.setParameter("o", operador);
		
		if(q.getResultList().size() == 1){
			return (ConfigPlanoAcao)q.getSingleResult();
		}else{
			return null;
		}
	}
	
	public static boolean save(ConfigPlanoAcao config){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			em.persist(config);
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
}
