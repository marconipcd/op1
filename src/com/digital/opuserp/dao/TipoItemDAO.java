package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.TipoItemProduto;
import com.digital.opuserp.util.ConnUtil;

public class TipoItemDAO {

	public static void save(TipoItemProduto tipoItem){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			em.merge(tipoItem);
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void excluir(Integer id){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			em.getTransaction().begin();
			em.remove(em.find(TipoItemProduto.class, id));
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
