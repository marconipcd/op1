package com.digital.opuserp.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresaFornecedor;
import com.digital.opuserp.domain.Fornecedor;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.Notify;

public class FornecedorDAO {

	EntityManager em;
	
	public FornecedorDAO() {		
		em = ConnUtil.getEntity();		
	} 
	
	public static void remove(Fornecedor f){
		EntityManager em = ConnUtil.getEntity();
		
		em.getTransaction().begin();
		em.remove(em.find(Fornecedor.class, f.getId()));
		em.getTransaction().commit();
	}
	
	public static Fornecedor find(Integer id){
		EntityManager em = ConnUtil.getEntity();
		return  em.find(Fornecedor.class, id);
		
	}
	
	public static Fornecedor findbyCnpj(String cnpj, Integer empresa){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from Fornecedor f where f.cnpj =:cnpj and f.empresa=:empresa", Fornecedor.class);
		q.setParameter("cnpj", cnpj);
		q.setParameter("empresa", new Empresa(empresa)); 
				
		if(q.getResultList().size() == 1){
			return (Fornecedor)q.getSingleResult();
		}
		
		return null;
	}
	
	public static Fornecedor findbyCnpjEmp(String cnpj, Integer empresa){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from Fornecedor f where f.cnpj =:cnpj and f.empresa=:empresa", Fornecedor.class);
		q.setParameter("cnpj", cnpj);
		q.setParameter("empresa", new Empresa(empresa)); 
		
		if(q.getResultList().size() > 0){
			return (Fornecedor)q.getResultList().get(0);
		}
		
		return null;
	}
	
	public static Fornecedor findFornecedor(Integer id){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from Fornecedor f where f.id =:cod and f.empresa=:empresa and f.fornecedor =:fornecedor", Fornecedor.class);
		q.setParameter("cod", id);
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		q.setParameter("fornecedor", true);
				
		if(q.getResultList().size() == 1){
			return (Fornecedor)q.getSingleResult();
		}
		
		return null;
	}
	
	public static Fornecedor findFornecedorCnpj(String cnpj){
		EntityManager em = ConnUtil.getEntity();
		Query q = em.createQuery("select f from Fornecedor f where f.cnpj =:cnpj and f.empresa=:empresa and f.fornecedor =:fornecedor", Fornecedor.class);
		q.setParameter("cnpj", cnpj);
		q.setParameter("empresa", OpusERP4UI.getEmpresa());
		q.setParameter("fornecedor", true);
				
		if(q.getResultList().size() == 1){
			return (Fornecedor)q.getSingleResult();
		}
		
		return null;
	}
	
    public boolean cpfCnpjCadastrado(Empresa empresa, String cnpj){
        try{             
            
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Fornecedor> c = cb.createQuery(Fornecedor.class);
            Root<Fornecedor> venda = c.from(Fornecedor.class);
            c.where(cb.and(
                    cb.equal(venda.get("cnpj"),cb.parameter(String.class, "cnpj")),
                    cb.equal(venda.get("empresa"),cb.parameter(Empresa.class, "empresa"))
                    ));
                
            TypedQuery q = em.createQuery(c);
            q.setParameter("cnpj", cnpj);
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
		    
    
    public boolean allowRemove(Integer codFornecedor, Integer codEmpresa){
		
		boolean allow = true;
		//Verificar se Tem Viculo com Entrada Cabecalho, 
		
		//Entrada Cabecalho
		Query qAcesso = em.createQuery("select mec from MovimentoEntCabecalho mec where mec.fornecedor = :Fornecedor and mec.empresa_id = :codEmpresa");
		qAcesso.setParameter("Fornecedor", new Fornecedor(codFornecedor));
		qAcesso.setParameter("codEmpresa", codEmpresa);
		
		if(qAcesso.getResultList().size() > 0){
			allow = false;
		}
		return allow;
	}
    
    public Fornecedor saveFornecedor(Fornecedor fornecedor){
	    try {
	    	
	    	// Inicia uma transação com o banco de dados.
	    	em.getTransaction().begin();
	    	
	    	// Verifica se a pessoa ainda não está salva no banco de dados.
	    	if(fornecedor.getId() == null) {
	    		//Salva os dados da pessoa.
	    		em.persist(fornecedor);
	    	} else {
	    		//Atualiza os dados da pessoa.
	    		fornecedor = em.merge(fornecedor);
	    	}
	    	try{
	    		em.getTransaction().commit();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		System.out.println("Erro ao comitar as informações-> FornecedorDAO: line 74 "+e.getMessage());
	    	}
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Erro ao atualizar ou cadastrar fornecedor: "+e.getMessage());
	    }
	    return fornecedor;
		  
	}
    public static Fornecedor save(Fornecedor fornecedor){
	    
    	EntityManager em = ConnUtil.getEntity();
    	try {
    		
	    	em.getTransaction().begin();	    	
	    	if(fornecedor.getId() == null) {
	    		em.persist(fornecedor);
	    	} else {
	    		fornecedor = em.merge(fornecedor);
	    	}
	    	
	    	em.getTransaction().commit();
	    	
	    } catch(Exception e) {
	    	e.printStackTrace();	    	
	    }
    	
	    return fornecedor;		  
	}
    
    public static List<Fornecedor> getFornecedores(String status, String s){
    	EntityManager em = ConnUtil.getEntity();
    	Query q = em.createNativeQuery("SELECT * FROM  fornecedores WHERE  RAZAO_SOCIAL LIKE '%"+s.toUpperCase()+"%' AND STATUS LIKE '"+status+"' ", Fornecedor.class);
    	
    	List<Fornecedor> fornecedores = new ArrayList<>();
    	for (Fornecedor fornecedor : (List<Fornecedor>)q.getResultList()) {
			Query q2 = em.createQuery("select ef from EmpresaFornecedor ef where ef.fornecedor=:f and ef.empresa=:e", EmpresaFornecedor.class);
			q2.setParameter("f", fornecedor);
			q2.setParameter("e", OpusERP4UI.getEmpresa());
			
			if(q2.getResultList().size() > 0){
				fornecedores.add(fornecedor);
			}
		}
    	
    	
    	return fornecedores;
    }
    
}

	

