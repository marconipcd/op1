package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.CidadeIbge;
import com.digital.opuserp.util.ConnUtil;

public class MunicipioDAO {

	public static CidadeIbge findByName(String s){
		
		EntityManager em  = ConnUtil.getEntity();
		Query q = em.createQuery("select m from CidadeIbge m where m.cidade = :cidade",CidadeIbge.class);
		q.setParameter("cidade", s);
		
		if(q.getResultList().size() == 1){
			return (CidadeIbge)q.getSingleResult();
		}
		
		return null;
	}
}
