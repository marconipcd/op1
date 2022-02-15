package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.AlteracoesProduto;
import com.digital.opuserp.domain.AlteracoesSerial;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.util.ConnUtil;

public class AlteracoesSerialDAO {

	
	public static void save(AlteracoesSerial alteracao){
		EntityManager em = ConnUtil.getEntity();
		try {
			em.getTransaction().begin();
			em.persist(alteracao);
			em.getTransaction().commit();
			
		} catch (Exception e) {
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			e.printStackTrace();
		}
	}
	
	public static void saveNovo(AlteracoesSerial alteracao){
		
		EntityManager em = ConnUtil.getEntity();	
		
		try{
			em.persist(alteracao);
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	public static boolean remove(Integer codSerial){
		
		boolean allow = false;
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query qAcesso = em.createQuery("select s from AlteracoesSerial s where s.serial_id = :codSerial",AlteracoesSerial.class);										
		qAcesso.setParameter("codSerial", new SerialProduto(codSerial));
		List<AlteracoesSerial> as = null;
		if(qAcesso.getResultList().size() > 0){
			as = qAcesso.getResultList();
			try {
				em.getTransaction().begin();
				if(as!=null){
					for(AlteracoesSerial s:as){
						em.remove(s);					
					}
				}
				em.getTransaction().commit();
				allow = true;
				
			} catch (Exception e) {
				if(em.getTransaction().isActive()){
					em.getTransaction().rollback();
				}
				e.printStackTrace();
			}
		}
		return allow;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	
}

