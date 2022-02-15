package com.digital.opuserp.lazy;

import javax.persistence.EntityManager;

import com.vaadin.addon.jpacontainer.EntityManagerProvider;

public class LazyHibernateEntityManagerProvider implements EntityManagerProvider {
		
	private static ThreadLocal<EntityManager> entityManagerThreadLocal =    new ThreadLocal<EntityManager>();

	@Override
	public EntityManager getEntityManager() {
		return entityManagerThreadLocal.get();
	}
	
	public static void setCurrentEntityManager(EntityManager em) {
	 entityManagerThreadLocal.set(em);
	}
}
