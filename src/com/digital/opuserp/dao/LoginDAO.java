package com.digital.opuserp.dao;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresasUsuario;
import com.digital.opuserp.domain.ModuloUsuario;
import com.digital.opuserp.domain.SubModuloUsuario;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.StringUtil;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Marconi
 */
public class LoginDAO {
    
    
    
    
    
    public static Usuario fazerLogin(Usuario u){
    	EntityManager em = ConnUtil.getEntity();
        try{             
            
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Usuario> c = cb.createQuery(Usuario.class);
            Root<Usuario> venda = c.from(Usuario.class);
            c.where(cb.and(
                    cb.equal(venda.get("username"),cb.parameter(String.class, "username")),
                    cb.equal(venda.get("password"),cb.parameter(String.class, "funcao"))
                    ));
            
            
            TypedQuery q = em.createQuery(c);
            q.setParameter("username", u.getUsername());
            q.setParameter("funcao",StringUtil.md5(u.getPassword()));
            //em.close();
            return (Usuario) q.getSingleResult();
         
        }catch(Exception e){
        	e.printStackTrace();
            System.out.println("Login:"+u.getUsername()+" ou Senha:"+u.getPassword()+" Incorretos:");
            return null;            
        }
    }
    
    public static List<EmpresasUsuario> getEmpresasUsuario(Usuario u){
        
    	EntityManager em = ConnUtil.getEntity();
    	
    	try{
    		
    		Query q = em.createQuery("select e from EmpresasUsuario e where e.usuario=:user", EmpresasUsuario.class);
	        q.setParameter("user", u);
	        
	        return q.getResultList();
	        
    	}catch(Exception e){
    		return null; 
    	}
        
    }
    
    public static List<ModuloUsuario> getModulosUsuario(Usuario u){
        
    	EntityManager em = ConnUtil.getEntity();
    	
    	try{
    		
    		Query q = em.createQuery("select e from ModuloUsuario e where e.usuario=:user", ModuloUsuario.class);
	        q.setParameter("user", u);
	        
	        return q.getResultList();
	        
    	}catch(Exception e){
    		return null; 
    	}
        
    }
 
    public static List<SubModuloUsuario> getSubModulosUsuario(Usuario u){
        
    	EntityManager em = ConnUtil.getEntity();
    	
    	try{
    		
    		Query q = em.createQuery("select e from SubModuloUsuario e where e.usuario=:user", SubModuloUsuario.class);
	        q.setParameter("user", u);
	        
	        return q.getResultList();
	        
    	}catch(Exception e){
    		return null; 
    	}
        
    }
    
    
    public static List<Empresa> getEmpresas(){
    	
    	EntityManager em = ConnUtil.getEntity();
    	
    	try{
    	
    	Query q = em.createQuery("select e from Empresa e");
    	return q.getResultList();
    	}catch(Exception e){
    		return null;
    	}
        
    }
    
    public static boolean validarUsuario(String username){
    	EntityManager em = ConnUtil.getEntity();
    	
    	try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
	    	CriteriaQuery<Usuario> c = cb.createQuery(Usuario.class);
	    	Root<Usuario> user = c.from(Usuario.class);
	    	c.where(cb.equal(user.get("username"),cb.parameter(String.class,"name")));
	    	
	    	TypedQuery q = em.createQuery(c);
	    	q.setParameter("name", username);
	    	
	    	if (q.getResultList().size()>0){
	    		return false;
	    	}else{
	    		return true;
	    	}
		} catch (Exception e) {
			System.out.println("Erro ao Logar!");
			return false;
		} 
          	
    	
    }
    
    
    
    
    
    
    
    
    
}
