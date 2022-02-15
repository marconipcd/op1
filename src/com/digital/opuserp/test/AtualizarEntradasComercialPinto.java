package com.digital.opuserp.test;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import me.legrange.mikrotik.ResultListener;

import com.digital.opuserp.dao.MarcaDAO;
import com.digital.opuserp.domain.Marca;
import com.digital.opuserp.domain.MovimentoEntCabecalho;
import com.digital.opuserp.domain.MovimentoEntDetalhe;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.util.TentarNovamente;

public class AtualizarEntradasComercialPinto {

	
	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args){
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
		
		Query q = em.createQuery("select p from ProdutoEntrada p", com.digital.opuserp.domain.ProdutoEntrada.class);
		List<com.digital.opuserp.domain.ProdutoEntrada> result = q.getResultList();
		
		em.getTransaction().begin();
		for (com.digital.opuserp.domain.ProdutoEntrada produtoEntrada : result) {
			Produto p = em.find(Produto.class, produtoEntrada.getId_produto());
			
			Query qM = em.createQuery("select m from Marca m where m.nome = :nomeMarca", Marca.class);
			qM.setParameter("nomeMarca", produtoEntrada.getLocal_work());
			if(qM.getResultList().size() == 1){
				Marca marca = (Marca)qM.getSingleResult();
						
				if(p != null && marca != null){
					p.setMarcasId(marca);
					em.merge(p);
					System.out.println(p.getNome());
				}
			}
		}
		em.getTransaction().commit();
		
		
		
		
		
//		Query q = em.createQuery("select e from MovimentoEntCabecalho e where e.empresa_id=4", MovimentoEntCabecalho.class);
//		List<MovimentoEntCabecalho> entradas = q.getResultList();
//		
//		
//		em.getTransaction().begin();
//		for (MovimentoEntCabecalho entrada : entradas) {
//			
//			Query q2 = em.createQuery("select i from MovimentoEntDetalhe i where i.movimentoEntCabecalhoId =:entrada", MovimentoEntDetalhe.class);
//			q2.setParameter("entrada", entrada.getId());
//			List<MovimentoEntDetalhe> itens = q2.getResultList();
//			
//			for (MovimentoEntDetalhe movimentoEntDetalhe : itens) {
//					Produto p = em.find(Produto.class, movimentoEntDetalhe.getProdutoId());				
//					if(p != null && p.getDataAlteracao() != null){
//						entrada.setDataHora(p.getDataAlteracao());				
//					
//						em.merge(entrada);
//						System.out.println(entrada.getDataHora());
//					}
//			}		
//		}
//		em.getTransaction().commit();
		
		
		
		
//		Query q = em.createQuery("select p from Produto p  where p.empresaId = 4", Produto.class);
//		List<Produto> produtos = q.getResultList();
//		
//		em.getTransaction().begin();
//		for (Produto produto : produtos) {
//			MovimentoEntCabecalho entrada = new MovimentoEntCabecalho(null, 4, 1, produto.getFornecedorId(), new Float(1), produto.getValorCusto(), new Date(), "COMPRA");
//			entrada.setSituacao("F");
//			entrada.setUsuario("opus");
//			entrada.setDataHora(produto.getDataAlteracao());
//			
//			
//			if(produto.getFornecedorId() != null){
//				em.persist(entrada);			
//				MovimentoEntDetalhe item = new MovimentoEntDetalhe(null, produto.getId(), entrada.getId(), 17,new Float("1"),produto.getValorCusto(), produto.getValorVenda(), new Float("1"), new Float("1"), new Float("1"),"NENHUMA", "1");
//				em.persist(item);
//				System.out.println(produto.getNome());
//			}
//		}
//		em.getTransaction().commit();
		
	}	
}
