package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.AgendaRenovacoes;
import com.digital.opuserp.util.ConnUtil;

public class AgendaRenovacoesDAO {

	public static void save(AgendaRenovacoes ar){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		em.persist(ar);
		em.getTransaction().commit();
	}
}
