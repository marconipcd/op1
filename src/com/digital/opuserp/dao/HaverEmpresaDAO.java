package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Haver;
import com.digital.opuserp.domain.HaverEmpresas;
import com.digital.opuserp.util.ConnUtil;

public class HaverEmpresaDAO {

	public static void add(Haver h, Empresa e){
		try{
			EntityManager em = ConnUtil.getEntity();
			em.getTransaction().begin();
			em.persist(new HaverEmpresas(null, e,h));			
			em.getTransaction().commit();
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void remove(Haver h, Empresa e){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select he from HaverEmpresas he where he.empresa=:empreasa and he.haver =:haver", HaverEmpresas.class);
		q.setParameter("empreasa", e);
		q.setParameter("haver", h);
		
		em.getTransaction().begin();
		for (HaverEmpresas he : (List<HaverEmpresas>)q.getResultList()) {
			em.remove(he);			
		}
		em.getTransaction().commit();
	}
	
	public static boolean check(Haver h, Empresa e){
		EntityManager em = ConnUtil.getEntity();
		
		Query q = em.createQuery("select he from HaverEmpresas he where he.empresa=:empreasa and he.haver =:haver", HaverEmpresas.class);
		q.setParameter("empreasa", e);
		q.setParameter("haver", h);
		
		if(q.getResultList().size() > 0){
			return true;
		}
		
		return false;
	}
}
