package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder.In;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.GrupoOse;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.domain.SubGrupoOse;
import com.digital.opuserp.domain.TipoSubGrupoOse;
import com.digital.opuserp.util.ConnUtil;

public class SubGrupoDAO {

	public static SubGrupoOse find(Integer codSubGrupo){
		EntityManager em = ConnUtil.getEntity();
		return em.find(SubGrupoOse.class, codSubGrupo);		
	}
	
	public static List<SubGrupoOse> getSubGrupos(Integer codGrupo){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select s from SubGrupoOse s where s.grupo =:grupo", SubGrupoOse.class);
		q.setParameter("grupo", new GrupoOse(codGrupo));
		
		return q.getResultList();
	}

	public static boolean allowRemove(Integer codSubGrupo){
		
		List<TipoSubGrupoOse> tipoSubGrupos = TipoSubGrupoDAO.getTipoSubGrupos(codSubGrupo);
		
		if(tipoSubGrupos.size() > 0){
			return false; 
		}
		
		return true;
	}
	
	public static void remove(Integer codSubGrupo){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		em.remove(find(codSubGrupo));
		em.getTransaction().commit();
	}
	
	
	public static List<SubGrupoOse> getSubGrupoOse(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from SubGrupoOse c", SubGrupoOse.class);
//		Query q = em.createQuery("select c from SubGrupoOse c where c.empresa_id =:empresa", SubGrupoOse.class);
//		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}
	
	
	
}
