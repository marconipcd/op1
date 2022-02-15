package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.EmpresaMarcas;
import com.digital.opuserp.util.ConnUtil;

public class EmpresaMarcaDAO {

	
	public static void save(EmpresaMarcas empresaMarca){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		em.persist(empresaMarca);
		em.getTransaction().commit();
	}

}
