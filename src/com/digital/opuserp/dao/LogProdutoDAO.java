package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.LogProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;

public class LogProdutoDAO {

	public static void registraLog(LogProduto log){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			em.getTransaction().begin();
			em.persist(log);
			em.getTransaction().commit();
		}catch(Exception e){
			Notify.Show("Não foi possível REGISTRAR log de Alteração de Produto!", Notify.TYPE_ERROR);
			
			e.printStackTrace();
		}
		
	}
	
	public static void registraLogSemTransacao(LogProduto log){
		
		try{
			EntityManager em = ConnUtil.getEntity();
			
			em.persist(log);
			
		}catch(Exception e){
			Notify.Show("Não foi possível REGISTRAR log de Alteração de Produto!", Notify.TYPE_ERROR);
			
			e.printStackTrace();
		}
		
	}
}
