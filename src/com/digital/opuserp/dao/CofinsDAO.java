package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.Cofins;
import com.digital.opuserp.domain.Pis;
import com.digital.opuserp.util.ConnUtil;

public class CofinsDAO {

	public static Cofins findByProduto(Integer produto_id){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select i from Cofins i where i.produto = :produto", Cofins.class);
			q.setParameter("produto", ProdutoDAO.find(produto_id));
			if(q.getResultList().size() == 1){
				return (Cofins)q.getSingleResult();
			}
			
			return null;
		}catch(Exception e){
            System.out.println("Erro: "+e.getMessage());
            return null;
        }
	}
	public static void save(Cofins cofins){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			if(cofins.getId() != null){
				em.merge(cofins);
			}else{
				em.persist(cofins);
			}
			em.getTransaction().commit();
		}catch(Exception e){
            System.out.println("Erro: "+e.getMessage());
            
        }
		
	}
}
