package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.domain.CrmPesquisaRel;
import com.digital.opuserp.domain.Perguntas;
import com.digital.opuserp.domain.Respostas;
import com.digital.opuserp.util.ConnUtil;

public class PerguntaDAO {
	
	
	public static Perguntas findPerguntaById(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		Perguntas p =em.find(Perguntas.class, cod);
			
		return p;
	}

	public static List<Perguntas> findByAssunto(CrmAssunto assunto){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select p from Perguntas p where p.assunto =:assunto and p.status = 'ATIVO'", Perguntas.class);
		q.setParameter("assunto", assunto);		
		
		if(q.getResultList().size() > 0){
			return q.getResultList();
		}
		
		return null;
	}
	
	public static List<Respostas> findByPergunta(Perguntas pergunta){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select p from Respostas p where p.pergunta =:pergunta", Respostas.class);
		q.setParameter("pergunta", pergunta);		
		
		if(q.getResultList().size() > 0){
			return q.getResultList();
		}
		
		return null;
	}
	
	public static CrmPesquisaRel findByCodCrmPerg(Integer codCrm, Integer codPergunta){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select p from CrmPesquisaRel p where p.cod_crm =:cod_crm and p.cod_pergunta=:cod_pergunta", CrmPesquisaRel.class);
		q.setParameter("cod_crm", new Crm(codCrm));		
		q.setParameter("cod_pergunta", codPergunta);		
		
		CrmPesquisaRel result =null;
		if(q.getResultList().size() > 0){
			result = (CrmPesquisaRel) q.getSingleResult();
		}
		
		return result;
	}
	
	public static void saveRel(CrmPesquisaRel crmPesqRel){
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		em.persist(crmPesqRel);
		em.getTransaction().commit();
	}
}
