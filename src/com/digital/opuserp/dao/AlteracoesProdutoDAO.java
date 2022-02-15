package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.AlteracoesProduto;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.ConnUtil;

public class AlteracoesProdutoDAO {

	
	public static void save(AlteracoesProduto alteracao){
		EntityManager em = ConnUtil.getEntity();
		try {
			em.getTransaction().begin();
			em.persist(alteracao);
			em.getTransaction().commit();
			
		} catch (Exception e) {
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			e.printStackTrace();
		}
	}
	
	public static List<AlteracoesProduto> findalteracao(Integer codProduto){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select p from AlteracoesProduto p where p.produto_id =:produto_id and p.tipo = 'CORRECAO DE ESTOQUE' ",AlteracoesProduto.class);
		q.setParameter("produto_id", new Produto(codProduto));
		List<AlteracoesProduto> produto = null;
		
		if(q.getResultList().size() > 0){
			produto =  q.getResultList();
		}
		return produto;
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	
}
