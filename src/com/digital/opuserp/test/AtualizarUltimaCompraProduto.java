package com.digital.opuserp.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.DataUtil;

public class AtualizarUltimaCompraProduto {

	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args){
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
		
		Query qProduto = em.createQuery("select p from Produto p ", Produto.class);
		
		em.getTransaction().begin();
		for (Produto produto :(List<Produto>)qProduto.getResultList()) {
			
			Query qCompraDetalhe = em.createQuery("select med from MovimentoEntDetalhe med where med.produtoId =:produto order by med.id desc", MovimentoEntDetalhe.class);
			qCompraDetalhe.setParameter("produto", produto.getId());
			
			
			if(qCompraDetalhe.getResultList() != null && qCompraDetalhe.getResultList().size() >= 1){
				MovimentoEntDetalhe med = (MovimentoEntDetalhe)qCompraDetalhe.getResultList().get(0);
				
				MovimentoEntCabecalho mec = em.find(MovimentoEntCabecalho.class, med.getMovimentoEntCabecalhoId());
				if(mec != null && mec.getDataHora() != null){
					produto.setUltima_compra(DataUtil.formatDateBra(mec.getDataHora()));
					em.merge(produto);				
				}	
				
			}
			
			System.out.println(produto.getNome());
		}
		em.getTransaction().commit();
	}
}
