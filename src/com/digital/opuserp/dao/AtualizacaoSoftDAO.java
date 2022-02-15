package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.AtualizacoesSoftware;
import com.digital.opuserp.util.ConnUtil;

public class AtualizacaoSoftDAO {

	public static boolean salvar(AtualizacoesSoftware as){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			em.getTransaction().begin();
			
			if(as.getId() != null){
				AtualizacoesSoftware as1 = em.find(AtualizacoesSoftware.class, as.getId());
				as1.setDescricao(as.getDescricao());
				as1.setData(as.getData());
				
				em.merge(as1);
			}else{
				em.persist(as);
			}
			
			em.getTransaction().commit();
			return true;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	
}
