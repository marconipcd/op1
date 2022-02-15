package com.digital.opuserp.test;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.ContasReceber;

public class VerificarDataVencContrato {

	static EntityManager em;
	static EntityManagerFactory emf;
	
	
	public static void main(String[] args) {
		
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
		
		Query q = em.createQuery("select a from AcessoCliente a where a.status_2 != 'ENCERRADO'", AcessoCliente.class);
		
		for (AcessoCliente a :(List<AcessoCliente>) q.getResultList()) {
			
			
			String regexNova = "^"+a.getId().toString()+"/[0-9]{2}-12/12";
			
			
			Query qn = em.createNativeQuery("select * from contas_receber cr where " +				
					"cr.status_2 ='ABERTO' " +				
					"and cr.n_doc REGEXP :rNova ",
					
					ContasReceber.class);
			
			qn.setParameter("rNova", regexNova);			
			qn.setParameter("dataVencimento", new Date());
			
			
			if(qn.getResultList().size() == 1){
				ContasReceber boleto = (ContasReceber)qn.getSingleResult();
				System.out.println("CONTRATO:"+a.getId().toString()+"|DATA VENC CONTRATO:"+a.getData_venc_contrato()+"|BOLETO:"+boleto.getN_doc()+"|VENC:"+boleto.getData_vencimento());
			}else{
				System.out.println("CONTRATO:"+a.getId().toString()+"|PROBLEMAS NA BUSCA DO BOLETO!");
			}
			
		}
		
		
	}
}
