package com.digital.opuserp.test;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;

public class ProdutoEntrada {

	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args) {
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
	
		Query q = em.createQuery("select p from ProdutoEntrada p", com.digital.opuserp.domain.ProdutoEntrada.class);
		
		List<com.digital.opuserp.domain.ProdutoEntrada> result = q.getResultList();
		
		em.getTransaction().begin();
		for (com.digital.opuserp.domain.ProdutoEntrada produtoEntrada : result) {
			
				Produto p = em.find(Produto.class, produtoEntrada.getId_produto());				
				
				if(p != null){
					try{
						p.setNome(p.getNome()+" | "+produtoEntrada.getLocal_work());
					}catch(Exception e){
						e.printStackTrace();
					}				
			}		
		}
		em.getTransaction().commit();
		
	}
}
