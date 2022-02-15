package com.digital.opuserp.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.util.Real;

public class AtualizarValoresAcesso {

	static EntityManager em;
	static EntityManagerFactory emf;
	
	public static void main(String[] args) {
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();		
		
		Query q = em.createQuery("select a from AcessoCliente a", AcessoCliente.class);
		List<AcessoCliente> list_contratos = q.getResultList();
		
		em.getTransaction().begin();
		for (AcessoCliente acessoCliente : list_contratos) {
			
			
			acessoCliente.setValor_beneficio_adesao(Real.formatStringToDBDouble(acessoCliente.getContrato().getValor_adesao()));
			acessoCliente.setValor_beneficio_comodato(Real.formatStringToDBDouble(acessoCliente.getContrato().getValor_equipamento()));
			em.merge(acessoCliente);
			
			System.out.println(acessoCliente.getId().toString());
		}
		
		em.getTransaction().commit();
		
	}

}
