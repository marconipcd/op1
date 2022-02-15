package com.digital.opuserp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.AlteracoesContasPg;
import com.digital.opuserp.domain.Contas;
import com.digital.opuserp.domain.ContasPagar;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.Crm;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.LogAcoes;
import com.digital.opuserp.util.ConnUtil;

public class ContasPagarDAO {
	
	
	
	public static List<ContasPagar> getTitulosPendentes(){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		Query q = em.createQuery("select cp from ContasPagar cp where cp.empresa_id =:empresa and cp.data_vencimento <=:data and cp.status='A PAGAR'", ContasPagar.class);
		q.setParameter("empresa", OpusERP4UI.getEmpresa().getId());
		q.setParameter("data", new Date());
		
		if(q.getResultList().size()>0){
			return q.getResultList();
		}else{
			return null;
		}
		
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}

	public List<ContasPagar> listarTitulos(String nDoc){
		
		EntityManager em = ConnUtil.getEntity();
		
		try{
		
		Query q = em.createQuery("select cp from ContasReceber cp where cp.n_doc =:n_doc", ContasPagar.class);
		q.setParameter("n_doc", nDoc);
		
		if(q.getResultList().size()>0){
			return q.getResultList();
		}else{
			return null;
		}
		
		}catch (Exception e){
			e.printStackTrace();			
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
	
	
	public static ContasPagar gerarBoletos(ContasPagar cp) throws Exception{
		
		EntityManager em = ConnUtil.getEntity();
		
		
		try{
	
			Date dataAtual = new Date();
			boolean definirNDocAuto;
			if(cp.getN_doc().equals(""))
			{
				definirNDocAuto = true;
			}else{
				definirNDocAuto = false;
			}  				
	
	        String ndocumento  = null;
			if(definirNDocAuto)
			{
				ndocumento = String.valueOf(cp.getN_doc()+"/"+dataAtual.getYear()+"-"+String.format("%02d", 1)+"/"+String.format("%02d", 1));
				
				cp.setN_doc(ndocumento);
			} 
	
			em.persist(cp);			
			
			em.persist(new AlteracoesContasPg(null, "CADASTROU ", cp, OpusERP4UI.getUsuarioLogadoUI(), new Date()));
				
			return cp;
			
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
    
	}	

	public static ContasPagar find(Integer cod){
		EntityManager em = ConnUtil.getEntity();
		
		try{
			return em.find(ContasPagar.class, cod);
		}catch (Exception e){
			e.printStackTrace();			
			return null;
		}
	}
}

