package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.FotosProdutos;
import com.digital.opuserp.util.ConnUtil;

public class FotoProdutoDAO {

	public static void deleteFoto(FotosProdutos fp){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			em.getTransaction().begin();
			em.remove(fp);
			em.getTransaction().commit();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static boolean adicionarFoto(FotosProdutos fp){
		EntityManager em = ConnUtil.getEntity();
		try{
			
			List<FotosProdutos> fotos = getFotosProduto(fp.getProduto_id());
			for (FotosProdutos foto : fotos) {
				deleteFoto(foto);
			}
			
			em.getTransaction().begin();						
			em.persist(fp); 			
			em.getTransaction().commit();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<FotosProdutos> getFotosProduto(Integer codProduto){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from FotosProdutos f where f.produto_id=:p", FotosProdutos.class);
		q.setParameter("p", codProduto);
		
		return q.getResultList();
	}
}
