package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.PreferenciasDashboard;
import com.digital.opuserp.util.ConnUtil;

public class PrefeDashDAO {

	
	
	public static boolean allow(String app){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select p from PreferenciasDashboard p where p.empresa =:empresa and p.usuario =:usuario and p.app=:app", PreferenciasDashboard.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa());
			q.setParameter("usuario", OpusERP4UI.getUsuarioLogadoUI());
			q.setParameter("app", app);
			
				
			if(q.getResultList().size() > 0){
				return false;
			}
			
			return true;
		}catch(Exception e){
		
			return true;		
		}
	}
	
	public static void add(String app){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select p from PreferenciasDashboard p where p.empresa =:empresa and p.usuario =:usuario and p.app=:app", PreferenciasDashboard.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		q.setParameter("usuario", OpusERP4UI.getUsuarioLogadoUI());
		q.setParameter("app", app);
		
		em.getTransaction().begin();	
		if(q.getResultList().size() > 0){
			for (PreferenciasDashboard p : (List<PreferenciasDashboard>)q.getResultList()) {
				em.remove(p); 
			}
		}
		em.persist(new PreferenciasDashboard(null, OpusERP4UI.getEmpresa(), OpusERP4UI.getUsuarioLogadoUI(), app));		
		em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void remove(String app){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select p from PreferenciasDashboard p where p.empresa =:empresa and p.usuario =:usuario and p.app=:app", PreferenciasDashboard.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa());
			q.setParameter("usuario", OpusERP4UI.getUsuarioLogadoUI());
			q.setParameter("app", app);
			
			em.getTransaction().begin();
			if(q.getResultList().size() > 0){
				for (PreferenciasDashboard p : (List<PreferenciasDashboard>)q.getResultList()) {
					em.remove(p); 
				}
			}
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static List<PreferenciasDashboard> getApps(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select p from PreferenciasDashboard p where p.empresa =:empresa and p.usuario =:usuario", PreferenciasDashboard.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		q.setParameter("usuario", OpusERP4UI.getUsuarioLogadoUI());
	
		return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
	
	public static boolean appExists(String app){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select p from PreferenciasDashboard p where p.empresa =:empresa and p.usuario =:usuario and p.app =:app", PreferenciasDashboard.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		q.setParameter("usuario", OpusERP4UI.getUsuarioLogadoUI());
		q.setParameter("app", app);
	
		if(q.getResultList().size() == 1){
			return true;
		}else{
			return false;
		}
		
		}catch(Exception e){
			return false;
		}
		
	}
}
