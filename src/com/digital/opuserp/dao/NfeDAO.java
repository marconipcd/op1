package com.digital.opuserp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.domain.AcessoCliente;
import com.digital.opuserp.domain.ConfigNfe;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.NfeMestre;
import com.digital.opuserp.util.ConnUtil;

public class NfeDAO {
	
	public static String getNextID(){
		try{
			EntityManager em = ConnUtil.getEntity();	
			Query q = em.createNativeQuery("SHOW TABLE STATUS LIKE 'nfe'");	
			Object result = q.getSingleResult();
			Object[] ob = (Object[]) result;
			String cod = ob[10].toString();
			
			return cod;
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Erro ao tentar pegar o próximo ID da Nfe: "+e.getMessage());
			return null;
		}
	}
	
	public static ConfigNfe getDefaultValue(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select c from ConfigNfe c", ConfigNfe.class);
		if(q.getResultList().size() == 1){
			return (ConfigNfe)q.getSingleResult();
		}
		
		return null;
	}
	
	public static List<NfeMestre> getNfeParaEnviarEmail(Date data1, Date data2,String nomeCliente){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select a from NfeMestre a where a.email_enviado =false and  a.data_emissao >=:data1 and a.data_emissao <=:data2 and a.situacao_doc = 'N' and a.cliente.nome_razao like :nomeCliente", NfeMestre.class);
		q.setParameter("data1", data1);
		q.setParameter("data2", data2);
		q.setParameter("nomeCliente", '%'+nomeCliente+'%');
		
		return q.getResultList();
	}
	
	
	public static void saveValueDefault(ConfigNfe c){
		
		EntityManager em = ConnUtil.getEntity();
		em.getTransaction().begin();
		if(c.getId() != null){
			em.merge(c);
		}else{
			em.persist(c); 
		}
		em.getTransaction().commit();
	}
	
	public static NfeMestre find(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		return em.find(NfeMestre.class, cod);
	}

	public static List<NfeMestre> getNfe(String ano_mes_ref){
		
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("SELECT n FROM NfeMestre n WHERE DATE_FORMAT(n.data_emissao, '%Y%m') = :ano_mes", NfeMestre.class);
		q.setParameter("ano_mes", ano_mes_ref);
		
		return q.getResultList();
	}
	
	public static List<String> getDatas(){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createNativeQuery("SELECT DATE_FORMAT(DATA_EMISSAO, '%m/%Y' ) FROM nfe_mestre GROUP BY DATE_FORMAT(DATA_EMISSAO, '%m/%Y' ) ORDER BY DATA_EMISSAO ASC ");
		return q.getResultList();		
	}

	public static void save(NfeMestre nfeMestre){
		EntityManager em = ConnUtil.getEntity();
		
		em.getTransaction().begin();
		em.persist(nfeMestre);
		em.getTransaction().commit();		
	}
	

	
	public static boolean verifica_se_gerado_mes(AcessoCliente contrato, String ano_mes){
		EntityManager em  = ConnUtil.getEntity();
		Query q = em.createQuery("select nfe From NfeMestre nfe where nfe.contrato =:contrato and nfe.ano_mes_ref=:ano_mes and nfe.situacao_doc = 'N'", NfeMestre.class);
		q.setParameter("contrato", contrato);
		q.setParameter("ano_mes", ano_mes);
		
		if(q.getResultList().size() > 0){
			return true;
		}
		
		
		return false;
		
	}
	
	public static boolean verifica_nfe_por_boleto(AcessoCliente contrato, ContasReceber boleto){
		EntityManager em  = ConnUtil.getEntity();
		Query q = em.createQuery("select nfe From NfeMestre nfe where nfe.contrato =:contrato and nfe.contas_receber=:boleto", NfeMestre.class);
		q.setParameter("contrato", contrato);
		q.setParameter("boleto", boleto);
		
		if(q.getResultList().size() > 0){
			return true;
		}
		
		
		return false;
		
	}
}
