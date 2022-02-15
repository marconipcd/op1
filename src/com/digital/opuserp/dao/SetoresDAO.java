package com.digital.opuserp.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.hibernate.criterion.Restrictions;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.domain.SetoresUsuario;
import com.digital.opuserp.util.ConnUtil;

public class SetoresDAO {
	
	
	
	
	public static void remove(Integer codSetor){
		EntityManager em = ConnUtil.getEntity();
		try{
			em.getTransaction().begin();
			em.remove(em.find(Setores.class, codSetor));
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}
	
	public static void commit(Setores setor){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		em.getTransaction().begin();
		
		if(setor.getId() != null){
			em.merge(setor);
		}else{
			em.persist(setor);
		}
		
		em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Setores find(Integer codSetor){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			
			return em.find(Setores.class, codSetor);
		}catch(Exception e){
			return null;
		}
	}
	
	public static List<Setores> getSetoresRoot(String s){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select s from Setores s where s.setor_root = null and s.empresa_id =:empresa and s.nome like:nome", Setores.class);
		q.setParameter("nome", "%"+s+"%");
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		
		return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
	
	public static List<Setores> getSetoresRoot(Integer codEmpresa){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select s from Setores s where s.setor_root = null and s.empresa_id =:empresa", Setores.class);
		q.setParameter("empresa", codEmpresa);
		
		
		return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
	
	
	public static List<Setores> getSetoresRoot(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select s from Setores s where s.setor_root = null and s.empresa_id =:empresa", Setores.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		
		return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
	
	public static List<Setores> getSetoresChild(Setores setor_root){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select s from Setores s where s.setor_root =:setor_pai", Setores.class);
		q.setParameter("setor_pai", setor_root);
		
		return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
	
	public static List<SetoresUsuario> getSetoresByUser(){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select s from SetoresUsuario s where s.empresa_id =:empresa and s.usuario_id =:usuario", SetoresUsuario.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("usuario", OpusERP4UI.getUsuarioLogadoUI().getId());
		
		return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
	
	public boolean getSetores(Integer codEmpresa, String nome){
		EntityManager em = ConnUtil.getEntity();

		
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Setores> query = cb.createQuery(Setores.class);
			Root<Setores> root = query.from(Setores.class);
			
			Expression<String> path = root.get("nome");
			Expression<String> param = cb.parameter(String.class, "nome");
			
			
			query.where(
						cb.and(
								cb.equal(root.get("empresa_id"), cb.parameter(Integer.class, "codEmpresa")),
								cb.equal(path, param)
							)
					);
			
			TypedQuery<Setores> q = em.createQuery(query);
			q.setParameter("codEmpresa", codEmpresa);
			q.setParameter("nome", nome.toUpperCase());
			if(q.getResultList().size() > 0){
				return false;
			}else{
				return true;
			}
					
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao buscar setores: "+e.getMessage()+".\n Causado por: "+e.getCause()+".");
			return false;
		}
		
		
	}
	
	public String converterNameSetor(String name){
		return name.toUpperCase();
	}
	
	
	public static List<Setores> getsetores(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from Setores c where c.empresa_id =:empresa", Setores.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			
			return q.getResultList();
		}catch(Exception e){
			return null;
		}
	}
	
	
	
	
}
