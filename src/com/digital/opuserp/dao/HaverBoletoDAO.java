package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.AlteracoesHaver;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.domain.HaverBoleto;
import com.digital.opuserp.util.ConnUtil;

public class HaverBoletoDAO {

	public static void remove(HaverBoleto haverBoleto){
		try{
			EntityManager em = ConnUtil.getEntity();
			em.getTransaction().begin();
			em.remove(haverBoleto);
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void add(HaverBoleto haver){
		try{
			EntityManager em = ConnUtil.getEntity();
			em.getTransaction().begin();
			em.persist(haver);
			em.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static List<HaverBoleto> findAll(ContasReceber contaReceber){
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select c from HaverBoleto c where c.boleto=:boleto", HaverBoleto.class);
			q.setParameter("boleto", contaReceber);
		
			return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<AlteracoesHaver> findAlteracoes(Haver haver, String tipo){
		try{
			EntityManager em = ConnUtil.getEntity();
			Query q = em.createQuery("select c from AlteracoesHaver c where c.haver=:haver and tipo:=tipo", AlteracoesHaver.class);
			q.setParameter("haver", haver);
			q.setParameter("tipo", "UTILIZADO BOLETO Nº "+tipo);
			
			return q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
