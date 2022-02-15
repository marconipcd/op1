package com.digital.opuserp.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.Cliente;

public class ClientTest {

	static EntityManager em;
	static EntityManagerFactory emf;
	public static void main(String[] args){
		ajustarOrgaoEmissor();
	}
	
	private static void ajustarOrgaoEmissor(){
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();		
		
		try{
			Query q = em.createQuery("select c from Cliente c ", Cliente.class);

			em.getTransaction().begin();
			List<Cliente> clientes = q.getResultList();
			for(Cliente c : clientes){
				
				if(c.getOrgao_emissor_rg() != null){
					c.setOrgao_emissor_rg(c.getOrgao_emissor_rg().replaceAll("0", ""));
					c.setOrgao_emissor_rg(c.getOrgao_emissor_rg().replaceAll("1", ""));
					c.setOrgao_emissor_rg(c.getOrgao_emissor_rg().replaceAll("2", ""));
					c.setOrgao_emissor_rg(c.getOrgao_emissor_rg().replaceAll("3", ""));
					c.setOrgao_emissor_rg(c.getOrgao_emissor_rg().replaceAll("4", ""));
					c.setOrgao_emissor_rg(c.getOrgao_emissor_rg().replaceAll("5", ""));
					c.setOrgao_emissor_rg(c.getOrgao_emissor_rg().replaceAll("6", ""));
					c.setOrgao_emissor_rg(c.getOrgao_emissor_rg().replaceAll("7", ""));
					c.setOrgao_emissor_rg(c.getOrgao_emissor_rg().replaceAll("8", ""));
					c.setOrgao_emissor_rg(c.getOrgao_emissor_rg().replaceAll("9", ""));
				}
				
				em.merge(c);
			}
			
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}
}
