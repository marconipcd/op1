package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.PlanoAcesso;
import com.digital.opuserp.util.ConnUtil;

public class PlanoAcessoDAO {
	EntityManager em;

	public PlanoAcessoDAO() {
		em = ConnUtil.getEntity();
	}
		
	public static List<PlanoAcesso> getPlanos(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from PlanoAcesso c", PlanoAcesso.class);
		
		return q.getResultList();
	}
	
	 public boolean allowRemove(Integer codPlanoAcesso){
			
			boolean allow = true;
			//Verificar se o Plano de Acesso está em uso, 
			
			//Acesso Cliente
			Query qAcesso = em.createQuery("select ac from AcessoCliente ac where ac.plano = :Plano");
			qAcesso.setParameter("Plano", new PlanoAcesso(codPlanoAcesso));
			qAcesso.setMaxResults(1);
			if(qAcesso.getResultList().size() > 0){
				allow = false;
			}	
			return allow;
		}
	
	
	 public boolean checkPlanoExist(String nome, Integer codContrato){
		
		boolean nomePlano = false;
		//Buscar o nome do plano, 
		
		//Plano de Acesso
		Query qAcesso = em.createQuery("select p from PlanoAcesso p where p.nome = :nome and p.contrato_acesso = :contrato", PlanoAcesso.class);
		qAcesso.setParameter("nome", nome);
		qAcesso.setParameter("contrato", new ContratosAcesso(codContrato));
		
		qAcesso.setMaxResults(1);
		if(qAcesso.getResultList().size() > 0){
			nomePlano = true;
		}	
		return nomePlano;
	}

	 public static PlanoAcesso find(Integer cod){
		 EntityManager em = ConnUtil.getEntity();
		return  em.find(PlanoAcesso.class, cod);
		
	 }
}
