package com.digital.opuserp.util;


import javax.persistence.EntityManager;

import com.digital.opuserp.OpusERP4UI;
import com.vaadin.addon.jpacontainer.EntityManagerProvider;

/**
 *
 * @author Marconi
 */
public class ConnUtil   {
	
	
	private static ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<EntityManager>();

	public static EntityManager getEntity(){
		return entityManagerThreadLocal.get();
	}

	public static void setCurrentEntityManager(EntityManager em) {
		entityManagerThreadLocal.set(em);
	}
	

   
}
