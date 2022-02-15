package com.digital.opuserp.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.ContasReceber;

public class BoletoScript {

			static EntityManager em;
			static EntityManagerFactory emf;
			public static void main(String[] args) {		
				setarPlanoBoletos();
			}
			
			private static void setarPlanoBoletos() {
				emf = Persistence.createEntityManagerFactory("OpusERP4");
				em = emf.createEntityManager();		
				
				Query q = em.createNativeQuery("select * from contas_receber  where n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}'  and status_2 !='EXCLUIDO'",ContasReceber.class);
				List<ContasReceber> list = q.getResultList();
				
				em.getTransaction().begin();
				for (ContasReceber contasReceber : list) {
					Integer codContrato = Integer.parseInt(contasReceber.getN_doc().split("/")[0].toString());
					
					if(codContrato != null){
						AcessoCliente ac =  em.find(AcessoCliente.class, codContrato);
						
						if(contasReceber.getPlano_contrato() == null){
							System.out.println("CONTRATO: "+ac.getId().toString());
						}
						
						if(ac != null && ac.getPlano() != null){
							contasReceber.setPlano_contrato(ac.getPlano().getId());
							em.merge(contasReceber);
							System.out.println(contasReceber.getId().toString());
						}
					}
				}			
				em.getTransaction().commit();
			}
}
