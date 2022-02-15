package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.NaturezaOperacao;
import com.digital.opuserp.util.ConnUtil;

public class NaturezaOperacaoDAO {
	
	
	public static NaturezaOperacao findbyName(String desc){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select c from NaturezaOperacao c where c.empresa_id=:empresa and c.descricao = :desc and c.status='ATIVO'", NaturezaOperacao.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("desc", desc);
		
		if(q.getResultList().size() == 1){
			return (NaturezaOperacao)q.getSingleResult();
		}
		
		return null;
	}
	
	public static NaturezaOperacao find(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select c from NaturezaOperacao c where c.empresa_id=:empresa and c.id = :cod and c.status='ATIVO'", NaturezaOperacao.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("cod", cod);
		
		if(q.getResultList().size() == 1){
			return (NaturezaOperacao)q.getSingleResult();
		}
		
		return null;
	}

}
