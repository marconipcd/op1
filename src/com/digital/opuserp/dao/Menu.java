package com.digital.opuserp.dao;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.ModuloUsuario;
import com.digital.opuserp.domain.ModulosEmpresa;
import com.digital.opuserp.domain.Setores;
import com.digital.opuserp.domain.SubModulo;
import com.digital.opuserp.domain.SubModuloUsuario;
import com.digital.opuserp.domain.SubmoduloEmpresa;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.domain.SetoresUsuario;
import com.digital.opuserp.util.ConnUtil;

/**
 *
 * @author Marconi
 */
public class Menu {
    
    EntityManager em;
    
    public Menu(){
       
       em = ConnUtil.getEntity();
    }
    
    
//    public List<Modulo> getModulosEmpresa(Integer codEmpresa){
//        
//        Empresa empresa = em.find(Empresa.class, codEmpresa);         
//        return empresa.getModulos();
//    }
    
	public List<ModulosEmpresa> getModulosdasEmpresa(Integer codEmpresa){
        
        Empresa empresa = em.find(Empresa.class, codEmpresa);         
        return empresa.getModulos();
    }
		
//	public List<setoresUsuario> getSetoresdasEmpresa(Integer codEmpresa){
//        
//        Empresa empresa = em.find(Empresa.class, codEmpresa);         
//        return empresa.getSetoresUsuario();
//    }
	
//	public List<setoresUsuario> getSetoresdasEmpresa(Integer codSetor, Integer CodEmpresa){
//        
//		CriteriaBuilder cb = em.getCriteriaBuilder();//coponente do jpa para abrir uma busca
//		CriteriaQuery<setoresUsuario> cq = cb.createQuery(setoresUsuario.class);
//		Root<setoresUsuario> setorUser = cq.from(setoresUsuario.class);
//		cq.where(cb.and(cb.equal(setorUser.get("setor"), cb.parameter(Setores.class, "codSetor")),
//				cb.equal(setorUser.get("empresa"), cb.parameter(Empresa.class, "codEmpresa"))
//				));
//		
//		TypedQuery tq = em.createQuery(cq);
//		tq.setParameter("codSetor", new Setores(codSetor));
//		tq.setParameter("codEmpresa", new Empresa(CodEmpresa));
//    
//        return tq.getResultList();
//    }
	

	public List<ModuloUsuario> getModulosUsuario(Integer codUsuario, Integer CodEmpresa){
        
		CriteriaBuilder cb = em.getCriteriaBuilder();//coponente do jpa para abrir uma busca
		CriteriaQuery<ModuloUsuario> cq = cb.createQuery(ModuloUsuario.class);
		Root<ModuloUsuario> modUser = cq.from(ModuloUsuario.class);
		cq.where(cb.and(cb.equal(modUser.get("usuario"), cb.parameter(Usuario.class, "codUsuario")),
				cb.equal(modUser.get("empresa"), cb.parameter(Empresa.class, "codEmpresa"))
				));
		
		TypedQuery tq = em.createQuery(cq);
		tq.setParameter("codUsuario", new Usuario(codUsuario));
		tq.setParameter("codEmpresa", new Empresa(CodEmpresa));
    
        return tq.getResultList();
    }
	
	
	 public List<SubModuloUsuario> getSubModuloUsuario(Integer codUsuario, Integer CodEmpresa){
	        
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<SubModuloUsuario> cq = cb.createQuery(SubModuloUsuario.class);
	        Root<SubModuloUsuario> subModUser = cq.from(SubModuloUsuario.class);
	        
	        cq.where(cb.and(cb.equal(subModUser.get("usuario"), cb.parameter(Usuario.class, "codUsuario")),
					cb.equal(subModUser.get("empresa"), cb.parameter(Empresa.class, "codEmpresa"))
					));
	        
	        TypedQuery tq = em.createQuery(cq);
	        tq.setParameter("codUsuario", new Usuario(codUsuario));
			tq.setParameter("codEmpresa", new Empresa(CodEmpresa));
	           
	        return tq.getResultList();
	    }
	
	
	    
	public List<SubModulo> getSubModulos(Integer codModulo){
	    
	    CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<SubModulo> c = cb.createQuery(SubModulo.class);
	    Root<SubModulo> venda = c.from(SubModulo.class);
	    c.where(cb.equal(venda.get("modulo_id"),cb.parameter(Integer.class, "modulo_id")));
	    TypedQuery q = em.createQuery(c);
	    q.setParameter("modulo_id", codModulo);
	       
	    return q.getResultList();
	}
    
    public Boolean verificarPermissaoSubModuloEmpresa(Integer codEmpresa,Integer codSubModulo){
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SubmoduloEmpresa> c = cb.createQuery(SubmoduloEmpresa.class);
        Root<SubmoduloEmpresa> venda = c.from(SubmoduloEmpresa.class);
        c.where(cb.and(cb.equal(venda.get("submodulo_id"),cb.parameter(Integer.class, "submodulo_id")),
                cb.equal(venda.get("empresa_id"),cb.parameter(Integer.class, "empresa_id"))));
        TypedQuery q = em.createQuery(c);
        q.setParameter("submodulo_id", codSubModulo);
        q.setParameter("empresa_id", codEmpresa);
        
        if(q.getResultList().size()>0){
            return true;
        }else{
            return false;
        }
    }
}
