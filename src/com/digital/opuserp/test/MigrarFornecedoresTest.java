package com.digital.opuserp.test;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresaFornecedor;
import com.digital.opuserp.domain.EmpresaGrupoProduto;
import com.digital.opuserp.domain.EmpresaMarcas;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.GrupoProduto;
import com.digital.opuserp.domain.Marca;

public class MigrarFornecedoresTest {

	static EntityManager em;
	static EntityManagerFactory emf;
	
	public static void main(String[] args) {
		emf = Persistence.createEntityManagerFactory("OpusERP4");
		em = emf.createEntityManager();
				
	   Query q = em.createQuery("select f from Categorias f where f.empresa_id=:empresa", Categorias.class);
	   q.setParameter("empresa", 1);
	   
	   em.getTransaction().begin();
	   for (EmpresaFornecedor f : (List<EmpresaFornecedor>)q.getResultList()) {
		   
		   Query qEM = em.createQuery("select em from EmpresaFornecedor em where em.fornecedor=:fornecedor and em.empresa=5", EmpresaFornecedor.class);
		   qEM.setParameter("fornecedor", f.getFornecedor());
		   
		   if(qEM.getResultList().size() == 0){
			   em.persist(new EmpresaFornecedor(null, new Empresa(5), f.getFornecedor()));
		   }
		   
		   System.out.println(f.getFornecedor().getRazao_social());
	   }
	   em.getTransaction().commit();
	   
	}

}
