package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.util.ConnUtil;

public class ServicoDAO {

	public static List<Servico> getServicos(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select s from Servico s where s.empresa_id =:empresa and s.status = 'ATIVO' ORDER BY s.nome ASC", Servico.class);		
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
			
		return q.getResultList();
	}
	
	public static Servico find(Integer codServico){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select s from Servico s where id =:cod and s.empresa_id =:empresa", Servico.class);
		q.setParameter("cod", codServico);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		
		if(q.getResultList().size() == 1){
			return (Servico)q.getSingleResult();
		}
		
		return null;
	}
	
	public static String getNextID(){
		try{
			EntityManager em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'servicos'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Erro ao tentar pegar o próximo ID do cliente: "+e.getMessage());
			return null;
		}
	}
}
