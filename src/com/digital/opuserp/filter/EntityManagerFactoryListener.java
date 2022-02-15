package com.digital.opuserp.filter;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.digital.opuserp.util.ConnUtil;


public class EntityManagerFactoryListener implements ServletContextListener {



	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// ConnUtil.closeEntityManagerFactory();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}