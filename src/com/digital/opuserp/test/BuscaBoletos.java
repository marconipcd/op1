package com.digital.opuserp.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.ContasReceber;

public class BuscaBoletos {

	
	
	EntityManager em;
	EntityManagerFactory emf;
	
	public void main(String[] args){

		
		
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
		
		
		
		Query q = em.createNativeQuery("SELECT contas_receber.ID AS COD_BOLETO, NOME_RAZAO, DATA_PAGAMENTO, FORMA_PGTO, VALOR_TARIFA FROM contas_receber, clientes WHERE "
				+ "CLIENTES_ID = clientes.ID AND data_pagamento > '2016-11-01' AND FORMA_PGTO = 'BANCO' AND VALOR_TARIFA IS NULL AND n_doc REGEXP '^[0-9]{1,}/[0-9]{2}-[0-9]{2}/[0-9]{2}'", ContasReceber.class);
		
		
		
		for (ContasReceber cr: (List<ContasReceber>)q.getResultList()) {
			
			System.out.println(cr.getN_doc());
			
		}
		
		
	}
}
