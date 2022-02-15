package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.util.ConnUtil;

public class CategoriasDAO {
	
	public static List<Categorias> getCategorias(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from Categorias c where c.empresa_id =:empresa", Categorias.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			
			return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static Categorias save(Categorias c){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			
			if(c.getId() != null){
				em.merge(c);
			}else{
				em.persist(c);
			}
			
			em.getTransaction().commit();
			
			return c;
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Categorias> getCategoriasPendentes(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from Categorias c where c.empresa_id =:empresa and c.status='WORK' and c.user_change=:user_change", Categorias.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			q.setParameter("user_change", OpusERP4UI.getUsuarioLogadoUI().getUsername());
			
			return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void remove(Categorias c){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em.getTransaction().begin();
			em.remove(c);
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
	}
}
