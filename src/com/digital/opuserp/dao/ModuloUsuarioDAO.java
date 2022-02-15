package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.mapping.List;

import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Modulo;
import com.digital.opuserp.domain.ModuloUsuario;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;

public class ModuloUsuarioDAO {

	EntityManager em;
	
	public ModuloUsuarioDAO() {
		
		em = ConnUtil.getEntity();
		
		}
		
		public void adicionarRegistro(Integer codEmpresa, Integer codModulo,Integer codUsuario){
			try {
				
				em.getTransaction().begin();
				em.persist(new ModuloUsuario(null, new Empresa(codEmpresa), new Modulo(codModulo),new Usuario(codUsuario)));
				em.getTransaction().commit();
				
			} catch (Exception e) {
				System.out.println("ERRO :"+e.getMessage());
				System.out.println("ERRO :"+e.getCause());
				System.out.println("ERRO :"+e.getStackTrace());
			}		
		}
		
		public void deletarRegistro(Integer codEmpresa, Integer codModulo,Integer codUsuario){
			try {
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ModuloUsuario> cq = cb.createQuery(ModuloUsuario.class);
				Root<ModuloUsuario> root = cq.from(ModuloUsuario.class);
				cq.where(cb.and(
							 cb.equal(root.get("empresa"),cb.parameter(Empresa.class, "codempresa")),
							 cb.equal(root.get("modulo"),cb.parameter(Modulo.class, "codmodulo")),
							 cb.equal(root.get("usuario"),cb.parameter(Usuario.class, "codusuario"))
							 ));
				
				TypedQuery tq = em.createQuery(cq);
				tq.setParameter("codempresa",new Empresa(codEmpresa));
				tq.setParameter("codmodulo",new Modulo(codModulo));
				tq.setParameter("codusuario",new Usuario(codUsuario));
				
				tq.getResultList().size();
				em.getTransaction().begin();
				em.remove(tq.getSingleResult());
				em.getTransaction().commit();
								
	
			} catch (Exception e) {
				System.out.println("ERRO :"+e.getMessage());
				System.out.println("ERRO :"+e.getCause());
				System.out.println("ERRO :"+e.getStackTrace());
				
			}
	}
	

}
