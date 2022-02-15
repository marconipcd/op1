package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.SubGrupoOse;
import com.digital.opuserp.domain.TipoSubGrupoOse;
import com.digital.opuserp.util.ConnUtil;

public class TipoSubGrupoDAO {

	public static TipoSubGrupoOse find(Integer codTipoSubGrupo){
		EntityManager em = ConnUtil.getEntity();
		return em.find(TipoSubGrupoOse.class, codTipoSubGrupo);		
	}
	
	public static List<TipoSubGrupoOse> getTipoSubGrupos(Integer codSubGrupo){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select s from TipoSubGrupoOse s where s.subgrupo_id =:grupo", TipoSubGrupoOse.class);
		q.setParameter("grupo", new SubGrupoOse(codSubGrupo));
		
		return q.getResultList();
	}
	
	public static void remove(Integer codTipoGrupo){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		em.remove(find(codTipoGrupo));
		em.getTransaction().commit();
	}
	
	public static List<TipoSubGrupoOse> getTipoSubGrupoOse(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from TipoSubGrupoOse c", TipoSubGrupoOse.class);
//		Query q = em.createQuery("select c from GrupoOse c where c.empresa_id =:empresa", TipoSubGrupoOse.class);
//		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}
}
