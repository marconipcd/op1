package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Contas;
import com.digital.opuserp.domain.CrmAssunto;
import com.digital.opuserp.util.ConnUtil;

public class ContasDAO {
	
	
	
	public Contas getContas(Integer id){
		EntityManager em = ConnUtil.getEntity();
		
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Contas> c = cb.createQuery(Contas.class);
			Root<Contas> root = c.from(Contas.class);
			c.where(cb.equal(root.get("id"), cb.parameter(Integer.class, "id"))
					);
			TypedQuery<Contas> q = em.createQuery(c);
			q.setParameter("id", id);
			
			if(q.getResultList().size() == 1){
				return (Contas) q.getSingleResult();				
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro: "+e.getMessage());
			return null;
		}
	}
	
	public static Contas find(Integer codConta){		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			if(OpusERP4UI.getEmpresa() != null){
			Query q = em.createQuery("select c from Contas c where c.id = :cod", Contas.class);
			q.setParameter("cod", codConta);
			
			if(q.getResultList().size() == 1){
				return (Contas)q.getSingleResult();
			}else{
			return null;
			}
			
			}else{
				return em.find(Contas.class, codConta);
			}	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro: "+e.getMessage());
			return null;
		}
	}
	
	public static Contas findCod_Ref(String codCod_Ref){		
		EntityManager em = ConnUtil.getEntity();
		
		try{
			if(OpusERP4UI.getEmpresa() != null){
				Query q = em.createQuery("select c from Contas c where c.cod_cta_ref = :cod", Contas.class);
				q.setParameter("cod", codCod_Ref);
				
				if(q.getResultList().size() == 1){
					return (Contas)q.getSingleResult();
				}else{
					return null;
				}
				
			}else{
				return null;
			}	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro: "+e.getMessage());
			return null;
		}
	}

	
	public String getNextID(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'contas_pagar'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Erro ao tentar pegar o próximo ID de contas a pagar: "+e.getMessage());
			return null;
		}
	}

	
	public static Contas getContaById(String id){
			
		EntityManager em = ConnUtil.getEntity();
		
		try{
			Query q = em.createQuery("select c from Contas c where c.cod_cta_ref = :cod", Contas.class);
			q.setParameter("cod", id);
			
			if(q.getResultList().size() > 0){
				return (Contas)q.getResultList().get(0);
			}
			
			return null;
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
	public static List<Contas> getContas(){
		EntityManager em = ConnUtil.getEntity();
		
		try{
		Query q = em.createQuery("select c from Contas c", Contas.class);
		
		return q.getResultList();
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
	
}
