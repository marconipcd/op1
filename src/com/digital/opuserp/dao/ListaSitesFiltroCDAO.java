package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.ListaSitesFiltroC;
import com.digital.opuserp.util.ConnUtil;

public class ListaSitesFiltroCDAO {

	public static ListaSitesFiltroC save(ListaSitesFiltroC lista){
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			if(lista.getId() != null){
				em.merge(lista);
			}else{
				em.persist(lista);
			}
			em.getTransaction().commit();
			
			return lista;
		}catch(Exception e){
			e.printStackTrace();
			return null;			
		}
		
	}
}
