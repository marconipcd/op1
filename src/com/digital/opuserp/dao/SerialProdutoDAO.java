package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.util.ConnUtil;

public class SerialProdutoDAO {

	public static SerialProduto find(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		return em.find(SerialProduto.class, cod);
	}
	public static void save(SerialProduto s){
		try{
			EntityManager em  = ConnUtil.getEntity();
							
			em.getTransaction().begin();
			
			
			if(s.getId() != null){
				em.merge(s);
			}else{
				em.persist(s);
			}
			
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void saveSemTransaction(SerialProduto s){
		try{
			EntityManager em  = ConnUtil.getEntity();
							
			//em.getTransaction().begin();
			
			
			if(s.getId() != null){
				em.merge(s);
			}else{
				em.persist(s);
			}
			
			//em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
