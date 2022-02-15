package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.SubModulo;
import com.digital.opuserp.util.ConnUtil;

public class ModuloDAO {

	public static Integer getId(String nome){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select s from Modulo s where s.nome_modulo = :nomeSubModulo", com.digital.opuserp.domain.Modulo.class);			
			q.setParameter("nomeSubModulo", nome);
			
			if(q.getResultList().size()>0){
				com.digital.opuserp.domain.Modulo sm = (com.digital.opuserp.domain.Modulo)q.getSingleResult();
				return sm.getId();
			}else{
				return 0;
			}
		}catch(Exception e){	
			e.printStackTrace();
			return 0;
		}
	}
}
