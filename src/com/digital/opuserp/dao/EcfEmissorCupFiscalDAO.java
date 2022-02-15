package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.EcfEmissorCupFiscal;
import com.digital.opuserp.domain.SimplesNacional;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;

public class EcfEmissorCupFiscalDAO {
	
	public static EcfEmissorCupFiscal find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from EcfEmissorCupFiscal f where f.id =:cod", EcfEmissorCupFiscal.class);
		q.setParameter("cod", id);
				
		if(q.getResultList().size() == 1){
			return (EcfEmissorCupFiscal)q.getSingleResult();
		}
		
		return null;
	}
	
	public static EcfEmissorCupFiscal findCod(String cod){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from EcfEmissorCupFiscal f where f.ecf_emissor_cod =:cod", EcfEmissorCupFiscal.class);
		q.setParameter("cod", cod);
		
		if(q.getResultList().size() == 1){
			return (EcfEmissorCupFiscal)q.getSingleResult();
		}
		
		return null;
	}

}
