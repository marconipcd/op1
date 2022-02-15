package com.digital.opuserp.test;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.domain.TotaisPedido;
import com.digital.opuserp.util.ConnUtil;

public class PedidoScript {

	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args){
		//migrarInformacoesFormaPgto();
	}
	
//	private static void migrarInformacoesFormaPgto(){
//		emf = Persistence.createEntityManagerFactory("OpusERP4");
//		em = emf.createEntityManager();		
//		
//		try{
//			Query q = em.createQuery("select p from EcfPreVendaCabecalho p where p.situacao='F'", EcfPreVendaCabecalho.class);
//			em.getTransaction().begin();
//			for (EcfPreVendaCabecalho pedido :(List<EcfPreVendaCabecalho>)q.getResultList()) {
//				List<TotaisPedido> formasPgto = new  ArrayList<>();
//				
//				if(pedido != null && pedido.getFormaPagtoID() != null && pedido.getValor() != null){
//					formasPgto.add(new TotaisPedido(null, pedido, pedido.getFormaPagtoID(), pedido.getValor()));
//					for (TotaisPedido totaisPedido : formasPgto) {
//						em.persist(totaisPedido);
//					}
//				}
//				
//				System.out.println(pedido.getId().toString());
//			}
//			em.getTransaction().commit();
//		}catch(Exception e){
//			e.printStackTrace();
//			em.getTransaction().rollback();
//		}
//	}
}
