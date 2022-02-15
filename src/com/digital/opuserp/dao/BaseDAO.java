package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.Base;
import com.digital.opuserp.domain.Concentrador;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.util.ConnUtil;

public class BaseDAO {


	/**
	 * Checa se a Base esta em Uso
	 * 
	 * @param codBase
	 * @return boolean
	 */
	public static boolean allowRemove(Integer codBase){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
			boolean allow = true;
			//Verificar se a BAse está em uso, 
			
			//Acesso Cliente
			Query qAcesso = em.createQuery("select c from Concentrador c where c.base = :Base");
			qAcesso.setParameter("Base", new Base(codBase));
			qAcesso.setMaxResults(1);
			if(qAcesso.getResultList().size() > 0){
				allow = false;
			}	
			return allow;
			
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
