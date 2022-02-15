package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.ComoNosConheceu;
import com.digital.opuserp.util.ConnUtil;

public class ComoNosConheceuDAO {

	public static List<ComoNosConheceu> getComoNosConheceu(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select c from ComoNosConheceu c ", ComoNosConheceu.class);
		
		
		return q.getResultList();
		}catch(Exception e){
            return null;
            
        }
	}
}
