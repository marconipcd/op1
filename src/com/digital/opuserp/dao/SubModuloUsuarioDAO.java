package com.digital.opuserp.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.digital.opuserp.domain.Empresa;
import com.digital.opuserp.domain.Modulo;
import com.digital.opuserp.domain.ModuloUsuario;
import com.digital.opuserp.domain.SubModulo;
import com.digital.opuserp.domain.SubmoduloEmpresa;
import com.digital.opuserp.domain.SubModuloUsuario;
import com.digital.opuserp.domain.Usuario;
import com.digital.opuserp.util.ConnUtil;

public class SubModuloUsuarioDAO {

	EntityManager em;
	
	public SubModuloUsuarioDAO() {
		
		em = ConnUtil.getEntity();
		
	}
	
	public void adicionarRegistro(Integer codEmpresa, Integer codSubModulo, Integer codUsuario){
		try {
			
			em.getTransaction().begin();
			em.persist(new SubModuloUsuario(null, em.find(Empresa.class, codEmpresa), 
					em.find(SubModulo.class, codSubModulo), em.find(Usuario.class, codUsuario)));			
			em.getTransaction().commit();
			
		} catch (Exception e) {
			System.out.println("ERRO :"+e.getMessage());
			System.out.println("ERRO :"+e.getCause());
			System.out.println("ERRO :"+e.getStackTrace());
		}		
	}
	
	public void deletarRegistro(Integer codEmpresa, Integer codSubModulo,Integer codUsuario){
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SubModuloUsuario> cq = cb.createQuery(SubModuloUsuario.class);
			Root<SubModuloUsuario> root = cq.from(SubModuloUsuario.class);
			cq.where(cb.and(
						 cb.equal(root.get("empresa"),cb.parameter(Empresa.class, "codempresa")),
						 cb.equal(root.get("submodulo"),cb.parameter(SubModulo.class, "codsubmodulo")),
						 cb.equal(root.get("usuario"),cb.parameter(Usuario.class, "codusuario"))
						 ));
			
			TypedQuery tq = em.createQuery(cq);
			tq.setParameter("codempresa",new Empresa(codEmpresa));
			tq.setParameter("codsubmodulo",new SubModulo(codSubModulo));
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
