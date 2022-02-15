package com.digital.opuserp.dao;


import java.lang.reflect.Array;
import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.EcfPreVendaCabecalho;
import com.digital.opuserp.util.ConnUtil;

public class EcfPreVendaCabecalhoDAO {
	
	EntityManager em;
	
	public EcfPreVendaCabecalhoDAO(){
		em = ConnUtil.getEntity();
	}
	
	public static EcfPreVendaCabecalho find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(EcfPreVendaCabecalho.class, id);
	}
	
	
	public String getPrimeDate(Integer codCliente){
		try {
			em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SELECT * FROM ecf_pre_venda_cabecalho WHERE CLIENTES_ID = "+codCliente);	
			
			Integer checar = q.getResultList().size();
			
			if(checar >= 1){
				Object resultPrime = q.getResultList().get(0);
				Object[] obPrime = (Object[]) resultPrime;
				String primeDate = obPrime[4].toString();
				
				return primeDate;
			}else{
				return new String("Nao Possui Compra!");
			}
			
		} catch (Exception e) {
			System.out.println("Erro: "+e.getMessage());
			return null;
		}
		
	}
	
	public String getLastDate(Integer codCliente){
		String lastDate = "";
		try {
			em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SELECT * FROM ecf_pre_venda_cabecalho WHERE CLIENTES_ID = "+codCliente);	
			
			Integer ultima = q.getResultList().size();
			if(ultima == 1){
				Object resultLast = q.getResultList().get(0);
				Object[] obLast = (Object[]) resultLast;
				lastDate = obLast[4].toString();
				
				return lastDate;
			}else if(ultima >= 1){
				Object resultLast = q.getResultList().get(ultima-1);
				Object[] obLast = (Object[]) resultLast;
				lastDate = obLast[4].toString();
				
				return lastDate;
			}else{
				return new String("Nao Possui Compra!");
			}
			
		} catch (Exception e) {
			System.out.println("Erro: "+e.getMessage());
			return null;
		}
		
	}
	
	
}
