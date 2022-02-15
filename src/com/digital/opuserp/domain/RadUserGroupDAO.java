package com.digital.opuserp.domain;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.util.ConnUtil;

public class RadUserGroupDAO {

	
	public static String getPlanoByUsername(String username){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select rad from RadUserGgroup rad where rad.username =:login", RadUserGgroup.class);
		q.setParameter("login", username);
		
		if(q.getResultList().size() == 1){
			RadUserGgroup ras = (RadUserGgroup)q.getSingleResult();
			String[] valores = ras.getGroupname().split("_");
			return valores[1];
		}
		
		return "MAIS DE UM PLANO !";
	}
}
