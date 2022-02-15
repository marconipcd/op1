package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.util.ConnUtil;

public class TesteDAO {

	/**
	 * @param args
	 */
	
	static EntityManager em;
	public static void main(String[] args) {
		em = ConnUtil.getEntity();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Cliente> c = cb.createQuery(Cliente.class);
		Root<Cliente> root = c.from(Cliente.class);	
		TypedQuery<Cliente> q = em.createQuery(c);		
		System.out.println(q.getResultList());
	}

}
