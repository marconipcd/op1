package com.digital.opuserp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.OcorrenciaPlanoAcao;
import com.digital.opuserp.domain.PlanoAcao;
import com.digital.opuserp.util.ConnUtil;

public class PlanoAcaoDAO {

	public static List<OcorrenciaPlanoAcao> getDetalhes(PlanoAcao plano_acao){
		
		try{
			EntityManager em  = ConnUtil.getEntity();
			
			Query q = em.createQuery("select o from OcorrenciaPlanoAcao o where o.plano=:p", OcorrenciaPlanoAcao.class);
			q.setParameter("p", plano_acao);
			
			return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static boolean salvar(PlanoAcao plano_acao){
		
		try{
			EntityManager em  = ConnUtil.getEntity();
			
			em.getTransaction().begin();
			
			if(plano_acao.getId() != null){
				em.merge(plano_acao);
			}else{
				em.persist(plano_acao);
			}
			
			em.getTransaction().commit();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			
			return false;
		}
	}
	public static Integer getSizeGeral(){
		EntityManager em  = ConnUtil.getEntity();
		Query q = em.createQuery("select p from PlanoAcao p where p.operador=:o", PlanoAcao.class);
		q.setParameter("o", OpusERP4UI.getUsuarioLogadoUI());
		return q.getResultList().size();
	}
	public static Integer getSizeConcluido(){
		EntityManager em  = ConnUtil.getEntity();
		Query q = em.createQuery("select p from PlanoAcao p where p.status like 'Concluido' and p.operador=:o ", PlanoAcao.class);
		q.setParameter("o", OpusERP4UI.getUsuarioLogadoUI());
		return q.getResultList().size();
	}
	public static Integer getSizeNoPrazo(){
		EntityManager em  = ConnUtil.getEntity();
		Query q = em.createQuery("select p from PlanoAcao p where p.status like 'Pendente' and p.data_prazo >=:dataHoje and p.operador=:o", PlanoAcao.class);
		q.setParameter("dataHoje", new Date());
		q.setParameter("o", OpusERP4UI.getUsuarioLogadoUI());
		return q.getResultList().size();
	}
	public static Integer getSizeAtrasado(){
		EntityManager em  = ConnUtil.getEntity();
		Query q = em.createQuery("select p from PlanoAcao p where p.status like 'Pendente' and p.data_prazo <:dataHoje and p.operador=:o", PlanoAcao.class);
		q.setParameter("dataHoje", new Date());
		q.setParameter("o", OpusERP4UI.getUsuarioLogadoUI());
		return q.getResultList().size();
	}
}
