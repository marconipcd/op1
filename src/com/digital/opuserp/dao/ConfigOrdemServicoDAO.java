package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.ConfigOse;
import com.digital.opuserp.util.ConnUtil;


public class ConfigOrdemServicoDAO {

	public static ConfigOse getTipoItemDefault(){
		
		EntityManager em = ConnUtil.getEntity();
		
		ConfigOse config = em.find(ConfigOse.class, 1);
		
		return config;
	}
	
	public static void save(ConfigOse ose){
		
		EntityManager em = ConnUtil.getEntity();
		
		em.getTransaction().begin();
		em.merge(ose);
		em.getTransaction().commit();
		
		
	}
}
