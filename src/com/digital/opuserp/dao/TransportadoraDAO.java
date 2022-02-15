package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.domain.Transportadoras;
import com.digital.opuserp.util.ConnUtil;


public class TransportadoraDAO {
	
	EntityManager em;

	public TransportadoraDAO() {

	em = ConnUtil.getEntity();
	
	}



	public boolean cpfCnpjCadastrado(Integer empresa, String cnpj){
    try{             
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Transportadoras> c = cb.createQuery(Transportadoras.class);
        Root<Transportadoras> venda = c.from(Transportadoras.class);
        c.where(cb.and(
                cb.equal(venda.get("cnpj"),cb.parameter(String.class, "cpfCnpj")),
                cb.equal(venda.get("empresa_id"),cb.parameter(Integer.class, "empresa"))
                ));
            
        TypedQuery q = em.createQuery(c);
        q.setParameter("cpfCnpj", cnpj);
        q.setParameter("empresa",empresa);
        //em.close();
        
        if (q.getResultList().size()>0){
    		return false;
    	}else{
    		return true;
    	}
     
    }catch(Exception e){
        System.out.println("Erro: "+e.getMessage());
        return false;
    }
   //     return null;            
}

	 public boolean allowRemove(Integer codTransportadora, Integer codEmpresa){
			
			boolean allow = true;
			//Verificar se Tem Viculo com Fornecedor, 
			Query qAcesso = em.createQuery("select fn from Fornecedor fn where fn.transportadora_padrao_id =:codTransportador", Fornecedor.class);								   
			qAcesso.setParameter("codTransportador", codTransportadora);
			
			if(qAcesso.getResultList().size() > 0){
				allow = false;
			}
			return allow;
		}	
	 
	 
//	public String getLimiteCredito(Integer codTransportadora){
//		try {
//			em = ConnUtil.getEntityManager();	
//			Query q = em.createNativeQuery("SELECT * FROM transportadoras WHERE ID = " + codTransportadora);	
//			
//			Object resultPrime = q.getSingleResult();
//			Object[] obPrime = (Object[]) resultPrime;
//			String limite = obPrime[3].toString();
//			return limite;
//		} catch (Exception e) {
//			System.out.println("Erro ao buscar o saldo: "+e.getMessage()+".\n Causado por: "+e.getCause());
//			return new String("Nao Possui Saldo!");
//		}
//		
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}