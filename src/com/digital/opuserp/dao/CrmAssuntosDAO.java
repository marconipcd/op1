package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.util.ConnUtil;

public class CrmAssuntosDAO {
	
	public static List<CrmAssunto> getAssuntos(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from CrmAssunto c where c.empresa_id =:empresa", CrmAssunto.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		return q.getResultList();
	}
	
	public static List<CrmAssunto> getAssuntos(String setor){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from CrmAssunto c where c.empresa_id =:empresa and c.setor=:s and c.status = 'ATIVO' ", CrmAssunto.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("s", new Setores(Integer.parseInt(setor)));
		
		return q.getResultList();
	}
	
	public static CrmAssunto save(CrmAssunto crmAssunto){
		EntityManager em = ConnUtil.getEntity();
		try{
			em.getTransaction().begin();
			if(crmAssunto.getId() != null){
				em.merge(crmAssunto);
			}else{
				em.persist(crmAssunto); 
			}
			em.getTransaction().commit();
			return crmAssunto;
		}catch(Exception e){
			em.getTransaction().rollback();
			e.printStackTrace();
			return null;
		}
	}
}
