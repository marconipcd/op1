package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.FormasPgto;
import com.digital.opuserp.util.ConnUtil;

public class FormaPgtoDAO {

	public static FormasPgto find(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		
		Query q;
		
		if(OpusERP4UI.getEmpresa() != null){
			q = em.createQuery("select c from FormasPgto c where c.empresa_id=:empresa and c.id = :cod", FormasPgto.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			q.setParameter("cod", cod);
			
			if(q.getResultList().size() == 1){
				return (FormasPgto)q.getSingleResult();
			}else{
				return null;
			}
		}else{
			return em.find(FormasPgto.class, cod);			
		}
		
		
	}
	
	public static FormasPgto find(String nome){
		EntityManager em = ConnUtil.getEntity();
		
		Query q;
		
		if(OpusERP4UI.getEmpresa() != null){
			q = em.createQuery("select c from FormasPgto c where c.empresa_id=:empresa and c.nome = :nome", FormasPgto.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			q.setParameter("nome", nome);
			
			if(q.getResultList().size() == 1){
				return (FormasPgto)q.getSingleResult();
			}else{
				return null;
			}
		}else{
			return null;
		}
		
		
	}
}
