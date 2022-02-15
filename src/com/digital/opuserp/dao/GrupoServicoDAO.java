package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.GrupoServico;
import com.digital.opuserp.util.ConnUtil;

public class GrupoServicoDAO {

	public static GrupoServico findbyName(String nome){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from GrupoServico f where f.nome =:cod and f.empresa=:empresa_id", GrupoServico.class);
		q.setParameter("cod", nome);
		q.setParameter("empresa_id", OpusERP4UI.getEmpresa());
				
		if(q.getResultList().size() == 1){
			return (GrupoServico)q.getSingleResult();
		}
		
		return null;
	}
}
