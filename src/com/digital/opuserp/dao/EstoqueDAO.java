package com.digital.opuserp.dao;

import javax.persistence.EntityManager;

import com.digital.opuserp.domain.Cfop;
import com.digital.opuserp.domain.Cofins;
import com.digital.opuserp.domain.Cofinsst;
import com.digital.opuserp.domain.Icms;
import com.digital.opuserp.domain.Ii;
import com.digital.opuserp.domain.Ipi;
import com.digital.opuserp.domain.Issqn;
import com.digital.opuserp.domain.Pis;
import com.digital.opuserp.domain.Pisst;
import com.digital.opuserp.util.ConnUtil;

public class EstoqueDAO {

	
	
	
//	public static Cofins findCfopByID(Integer id){
//		EntityManager em = ConnUtil.getEntityManager();
//		return em.find(cfop.class, id);
//	}
	public static Cofins findCofinsByID(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Cofins.class, id);
	}
	
	public static Cofinsst findCofinsstByID(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Cofinsst.class, id);
	}
	
	public static Icms findIcmsByID(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Icms.class, id);
	}
	
	public static Ii findIIByID(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Ii.class, id);
	}
	public static Ipi findIpiByID(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Ipi.class, id);
	}
	
	public static Issqn findIssqnByID(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Issqn.class, id);
	}
	
	public static Pis findPisByID(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Pis.class, id);
	}
	public static Pisst findPisStByID(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Pisst.class, id);
	}
	public static Cfop findCfopByID(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Cfop.class, id);
	}
	
}
