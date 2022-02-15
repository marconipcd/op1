package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Cnae;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Transportadoras;
import com.digital.opuserp.util.ConnUtil;

public class EmpresaDAO {
	
	EntityManager em;

	public EmpresaDAO() {
		
		em = ConnUtil.getEntity();
	
	}
	public Cnae getCnae(Integer codigoCnae){
		return em.find(Cnae.class, codigoCnae);
	}
	
	public Cliente getCliente(Integer codCliente){
		return em.find(Cliente.class, codCliente);
	}
	
	public boolean cpfCnpjCadastrado(Integer empresa, String cnpj){
	    try{             
	        
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<Empresa> c = cb.createQuery(Empresa.class);
	        Root<Empresa> venda = c.from(Empresa.class);
	        c.where(cb.and(
	                cb.equal(venda.get("cnpj"),cb.parameter(String.class, "cpfCnpj")),
	                cb.equal(venda.get("id"),cb.parameter(Integer.class, "empresa"))
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

	}
	
	public static Empresa find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return em.find(Empresa.class, id);
	}

}