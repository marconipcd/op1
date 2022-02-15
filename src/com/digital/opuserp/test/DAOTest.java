package com.digital.opuserp.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.domain.Ceps;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.EnderecoEntrega;
import com.digital.opuserp.util.ConnUtil;

public class DAOTest {

	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args) {
		
		
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
		
			try {	
				Query q = em.createNativeQuery("SELECT * FROM ecf_pre_venda_cabecalho WHERE CLIENTES_ID = "+4139);	
				
				Integer checar = q.getResultList().size();
				
				if(checar >= 1){
					Object resultPrime = q.getResultList().get(0);
					Object[] obPrime = (Object[]) resultPrime;
					String primeDate = obPrime[4].toString();
					
					System.out.println(primeDate);
				}else{
					System.out.println("Nao Possui Compra!");
				}
				
			} catch (Exception e) {
				System.out.println("Erro: "+e.getMessage());
			}
			
		
		
//		try{
//			
//			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'enderecos_principais'");	
//			Object result = q.getSingleResult();
//			Object[] ob = (Object[]) result;
//			String cod = ob[10].toString();
//			
//			System.out.println("Correto: "+cod);
//		}catch (Exception e){
//			e.printStackTrace();
//			System.out.println("Erro ao tentar pegar o próximo ID do cliente: "+e.getMessage());
//			System.out.println("Causado por: "+e.getCause());
//		}
		
		
//		em.getTransaction().begin();
//		em.persist(new com.digital.opuserp.domain.Root(null, "Root1"));
//		em.persist(new Child(null, "child1", "1", "desc", new com.digital.opuserp.domain.Root(1)));
//		em.persist(new com.digital.opuserp.domain.Root(null, "Root2"));
//		em.persist(new Child(null, "child2", "2", "desc", new com.digital.opuserp.domain.Root(2)));
//		em.persist(new com.digital.opuserp.domain.Root(null, "Root3"));
//		em.persist(new Child(null, "child3", "3", "desc", new com.digital.opuserp.domain.Root(3)));
//		em.persist(new com.digital.opuserp.domain.Root(null, "Root4"));
//		em.persist(new Child(null, "child4", "4", "desc", new com.digital.opuserp.domain.Root(4)));
//		em.persist(new com.digital.opuserp.domain.Root(null, "Root5"));
//		em.persist(new Child(null, "child5", "5", "desc", new com.digital.opuserp.domain.Root(5)));
//		em.persist(new com.digital.opuserp.domain.Root(null, "Root6"));
//		em.persist(new Child(null, "child6", "6", "desc", new com.digital.opuserp.domain.Root(6)));
//		em.persist(new com.digital.opuserp.domain.Root(null, "Root7"));
//		em.persist(new Child(null, "child7", "7", "desc", new com.digital.opuserp.domain.Root(7)));
//		em.getTransaction().commit();
		
		
		
		
		
//		
//		
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Setores> query = cb.createQuery(Setores.class);
//		Root<Setores> root = query.from(Setores.class);
//		
//		Expression<String> path = root.get("nome");
//		Expression<String> param = cb.parameter(String.class, "nome");
//		
//		
//		query.where(
//					cb.and(
//							cb.equal(root.get("empresa_id"), cb.parameter(Integer.class, "codEmpresa")),
//							cb.like(path, param)
//						)
//				);
//		
//
//		TypedQuery<Setores> q = em.createQuery(query);
//		q.setParameter("codEmpresa", 1);
//		q.setParameter("nome", "vendas".toUpperCase());
//		System.out.println("Aqui: "+q.getSingleResult());

//		 CriteriaBuilder cb = em.getCriteriaBuilder();
//         CriteriaQuery<Fornecedor> c = cb.createQuery(Fornecedor.class);
//         Root<Fornecedor> venda = c.from(Fornecedor.class);
//         c.where(cb.and(
//                 cb.equal(venda.get("cnpj"),cb.parameter(String.class, "cpfCnpj")),
//                 cb.equal(venda.get("empresa_id"),cb.parameter(Integer.class, "empresa"))
//                 ));
//             
//         TypedQuery q = em.createQuery(c);
//         q.setParameter("cpfCnpj", "03311396421");
//         q.setParameter("empresa",1);
//         //em.close();
//         
//         if (q.getResultList().size()>0){
//	    	System.out.println("cpf existente!");
//	    	}
		
		  
//		  CriteriaBuilder cb = em.getCriteriaBuilder();
//	      CriteriaQuery<Fornecedor> c = cb.createQuery(Fornecedor.class);
//	      Root<Fornecedor> venda = c.from(Fornecedor.class);
//	      Expression<String> path = venda.get("razao_social");
//	      Expression<String> param = cb.parameter(String.class, "nome");
//	      c.where(cb.and(
//	    		  cb.like(path, "%GENESIS%"),
//	    		  cb.
//	              cb.equal(venda.get("empresa_id"),cb.parameter(Integer.class, "empresa"))
//	              ));
//	          
//	      TypedQuery q = em.createQuery(c);	      
//	      q.setParameter("empresa",2);
//	      //q.setParameter("nome","%genesis%");
//      
//      
//      if (q.getResultList().size()>0){
//	    	System.out.println("cpf existente!");
//	    	}

		
	}
	

}
