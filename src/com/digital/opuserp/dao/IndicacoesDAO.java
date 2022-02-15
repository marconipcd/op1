package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.IndicacoesCliente;
import com.digital.opuserp.util.ConnUtil;

public class IndicacoesDAO {

	
	public static boolean salvar(IndicacoesCliente indicacao){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			em.getTransaction().begin();

			if(indicacao.getId() != null){
				em.merge(indicacao);
			}else{
				em.persist(indicacao);
			}
			
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			
			return false;
		}
		
		
	}
}
