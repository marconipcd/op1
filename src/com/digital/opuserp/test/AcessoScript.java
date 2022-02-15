package com.digital.opuserp.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Produto;

public class AcessoScript {

	
	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args) {
		
		try{
		
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
		
		
		
		Query q = em.createNativeQuery("SELECT * FROM contas_receber, clientes WHERE "
				+ "CLIENTES_ID = clientes.ID AND data_pagamento > '2016-11-01' AND FORMA_PGTO = 'BANCO' AND VALOR_TARIFA IS NULL AND n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}'", ContasReceber.class);
		
		
		
		for (ContasReceber cr: (List<ContasReceber>)q.getResultList()) {
			
			AcessoCliente ac = em.find(AcessoCliente.class,Integer.parseInt(cr.getN_doc().substring(0, 4).replaceAll("/", "")));
			if(ac != null){
				if(ac.getEmitir_nfe() != null && !ac.getEmitir_nfe().equals("NFE-MOD21")){
					System.out.println(cr.getCliente().getNome_razao());
				}
				
				if(ac.getEmitir_nfe() == null){
					System.out.println(cr.getCliente().getNome_razao());
				}
			}
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
			//corrigirdataVenci();
	
	}
//	private static void corrigirdataVenci() {
//		emf = Persistence.createEntityManagerFactory("OpusERP4");
//		em = emf.createEntityManager();		
//		
//		Query q = em.createQuery("select a from AcessoCliente a where a.status_2 != 'PENDENTE_INSTALACAO' and a.status_2 != 'ENCERRADO' ",AcessoCliente.class);
//		List<AcessoCliente> list = q.getResultList();
//		
//		em.getTransaction().begin();
//		for (AcessoCliente acessoCliente : list) {
//			if(acessoCliente.getContrato().getTipo_contrato().equals("POS-PAGO")){
//				
//				String r = "^"+acessoCliente.getId().toString()+"/[0-9]{2}-[0-9]{2}/[0-9]{2}";
//				Query q2 = em.createNativeQuery("SELECT * FROM contas_receber cr WHERE cr.n_doc REGEXP :r  AND cr.status_2 !='EXCLUIDO' ORDER BY cr.data_vencimento DESC",ContasReceber.class);
//				q2.setParameter("r", r);
//				
//				if(q2.getResultList().size() > 0){
//					ContasReceber ct = (ContasReceber) q2.getResultList().get(0);				
//					acessoCliente.setData_venc_contrato(ct.getData_vencimento());
//					System.out.println("Contrato: "+acessoCliente.getId().toString());
//					System.out.println(ct.getData_vencimento());
//					System.out.println("====================================================");
//				}else{
//					System.out.println("Contrato: "+acessoCliente.getId()+" SEM BOLETOS");
//				}
//			}
//		}
//		em.getTransaction().commit();
//		
//	}
//	
}
