package com.digital.opuserp.filter;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.digital.opuserp.util.ConnUtil;
import com.google.inject.Provider;


public class EntityManagerFilter implements Filter {

	private EntityManagerFactory entityManagerFactory;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        entityManagerFactory = Persistence.createEntityManagerFactory("OpusERP4");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            // Create and set the entity manager
            ConnUtil.setCurrentEntityManager(entityManagerFactory.createEntityManager());

            // Handle the request
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // Reset the entity manager
        	ConnUtil.setCurrentEntityManager(null);
        }
    }

    @Override
    public void destroy() {
        entityManagerFactory = null;
    }

}
