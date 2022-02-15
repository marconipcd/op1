package com.digital.opuserp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.EmpresasUsuario;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.StringUtil;


public class EmpresaUsuarioDAO {

	EntityManager em;
	
	public EmpresaUsuarioDAO() {
		
		
		em = ConnUtil.getEntity();		
	}
	
	public void adicionaRegistro(Integer codUsuario, Integer codEmpresa){
		try {
			
			em.getTransaction().begin();
			em.persist(new EmpresasUsuario(null, new Usuario(codUsuario), new Empresa(codEmpresa)));
			em.getTransaction().commit();
			
		} catch (Exception e) {
			System.out.println("ERRO :"+e.getMessage());
			System.out.println("ERRO :"+e.getCause());
			System.out.println("ERRO :"+e.getStackTrace());
		}
	}
	
	public void deletarRegistro(Integer codUsuario, Integer codEmpresa){
		try {
			
			
			 CriteriaBuilder cb = em.getCriteriaBuilder();
	         CriteriaQuery<EmpresasUsuario> c = cb.createQuery(EmpresasUsuario.class);
	         Root<EmpresasUsuario> venda = c.from(EmpresasUsuario.class);
	         c.where(cb.and(
	                    cb.equal(venda.get("usuario"),cb.parameter(Usuario.class, "user")),
	                    cb.equal(venda.get("empresa"),cb.parameter(Empresa.class, "empresa"))
	                    ));
	            
	            
	        TypedQuery q = em.createQuery(c);
	        q.setParameter("user", new Usuario(codUsuario));
	        q.setParameter("empresa",new Empresa(codEmpresa));			
			
			em.getTransaction().begin();			
			em.remove(q.getSingleResult());
			em.getTransaction().commit();		
			
		} catch (Exception e) {
			System.out.println("ERRO :"+e.getMessage());
			System.out.println("ERRO :"+e.getCause());
			System.out.println("ERRO :"+e.getStackTrace());
		}
	}
	
	public static List<Empresa> UsuarioDaEmpresa (Integer userLogado ){
		
			EntityManager em = ConnUtil.getEntity();
		
		try {
			
			CriteriaBuilder builder = em.getCriteriaBuilder();			
			CriteriaQuery<EmpresasUsuario> criteria = builder.createQuery(EmpresasUsuario.class);			
			Root<EmpresasUsuario> empUserRoot =  criteria.from(EmpresasUsuario.class);

			criteria.select(empUserRoot);
			
			criteria.where(builder.equal(empUserRoot.get("usuario_id"),builder.parameter(Integer.class,"USUARIO_ID")));
		
						
				TypedQuery typQuery = em.createQuery(criteria);
				typQuery.setParameter("USUARIO_ID",userLogado);
				List<EmpresasUsuario> empresa = em.createQuery(criteria).getResultList();
				
				return (List<Empresa>) typQuery.getResultList();
				
		} catch (Exception e) {
			System.out.println("Erro"+e.getMessage());
		}
		return null;
		
			
	}
	
	
	public static List<EmpresasUsuario> getUsuarios (){
		
		EntityManager em = ConnUtil.getEntity();
	
		try {
			
			Query q = em.createQuery("select eu from EmpresasUsuario eu where eu.empresa = :empresa", EmpresasUsuario.class);
			q.setParameter("empresa", OpusERP4UI.getEmpresa());
			
			return q.getResultList();
				
		} catch (Exception e) {
			System.out.println("Erro"+e.getMessage());
			return null;
		}
	
		
	}

	
	
	
	
	
	
	
}
