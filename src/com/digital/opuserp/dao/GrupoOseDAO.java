package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.GrupoOse;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.domain.SubGrupoOse;
import com.digital.opuserp.util.ConnUtil;

public class GrupoOseDAO {

	public static GrupoOse find(Integer codGrupo){
		EntityManager em = ConnUtil.getEntity();
		return em.find(GrupoOse.class, codGrupo);		
	}
	
	public static boolean allowRemove(Integer codGrupo){
		
		List<SubGrupoOse> subgrupos = SubGrupoDAO.getSubGrupos(codGrupo);
		
		if(subgrupos.size() > 0){
			return false; 
		}
		
		return true;
	}
	
	public static void remove(Integer codGrupo){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		em.remove(find(codGrupo));
		em.getTransaction().commit();
	}
	
	public static List<GrupoOse> getGrupoOse(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from GrupoOse c where c.empresa_id =:empresa", GrupoOse.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}
	
	public static void save(GrupoOse grupo){
		EntityManager em = ConnUtil.getEntity();

		try{
			em.getTransaction().begin();
			if(grupo.getId() != null){
				em.merge(grupo);
			}else{
				em.persist(grupo);				
			}
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}
	
	
	
}
