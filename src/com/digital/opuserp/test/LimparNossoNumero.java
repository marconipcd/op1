package com.digital.opuserp.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class LimparNossoNumero {

	static EntityManager em;
	static EntityManagerFactory emf;
	
	public static void main(String[] args){
		func();
	}
	public static void func(){
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();		
		
		Query q = em.createNativeQuery("SELECT cr.ID FROM alteracoes_conta_Receber acr, contas_receber cr, clientes c, enderecos_principais ep  WHERE c.ID = ep.CLIENTES_ID and acr.CONTA_RECEBER_ID = cr.ID and c.ID = cr.CLIENTES_ID AND `TIPO` = 'IMPRIMIU UM BOLETO' AND acr.EMPRESA_ID = 1 AND cr.STATUS_2 = 'ABERTO' and acr.DATA_ALTERACAO >= '2018-08-22' AND cr.N_NUMERO_SICRED IS NOT NULL GROUP BY cr.ID ORDER BY cr.DATA_VENCIMENTO ASC");
		
		for (Object o: q.getResultList()) {
			System.out.println(o);
		}
	}
}
